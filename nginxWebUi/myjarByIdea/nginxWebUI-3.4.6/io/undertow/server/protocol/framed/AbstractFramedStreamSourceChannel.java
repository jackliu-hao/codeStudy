package io.undertow.server.protocol.framed;

import io.undertow.UndertowLogger;
import io.undertow.UndertowMessages;
import io.undertow.connector.PooledByteBuffer;
import java.io.Closeable;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;
import org.xnio.Bits;
import org.xnio.Buffers;
import org.xnio.ChannelListener;
import org.xnio.ChannelListeners;
import org.xnio.IoUtils;
import org.xnio.Option;
import org.xnio.XnioExecutor;
import org.xnio.XnioIoThread;
import org.xnio.XnioWorker;
import org.xnio.channels.StreamSinkChannel;
import org.xnio.channels.StreamSourceChannel;

public abstract class AbstractFramedStreamSourceChannel<C extends AbstractFramedChannel<C, R, S>, R extends AbstractFramedStreamSourceChannel<C, R, S>, S extends AbstractFramedStreamSinkChannel<C, R, S>> implements StreamSourceChannel {
   private final ChannelListener.SimpleSetter<? extends R> readSetter = new ChannelListener.SimpleSetter();
   private final ChannelListener.SimpleSetter<? extends R> closeSetter = new ChannelListener.SimpleSetter();
   private final C framedChannel;
   private final Deque<AbstractFramedStreamSourceChannel<C, R, S>.FrameData> pendingFrameData = new LinkedList();
   private int state = 0;
   private static final int STATE_DONE = 2;
   private static final int STATE_READS_RESUMED = 4;
   private static final int STATE_READS_AWAKEN = 8;
   private static final int STATE_CLOSED = 16;
   private static final int STATE_LAST_FRAME = 32;
   private static final int STATE_IN_LISTENER_LOOP = 64;
   private static final int STATE_STREAM_BROKEN = 128;
   private static final int STATE_RETURNED_MINUS_ONE = 256;
   private static final int STATE_WAITNG_MINUS_ONE = 512;
   private volatile PooledByteBuffer data;
   private int currentDataOriginalSize;
   private long frameDataRemaining;
   private final Object lock = new Object();
   private int waiters;
   private volatile boolean waitingForFrame;
   private int readFrameCount = 0;
   private long maxStreamSize = -1L;
   private long currentStreamSize;
   private ChannelListener[] closeListeners = null;

   public AbstractFramedStreamSourceChannel(C framedChannel) {
      this.framedChannel = framedChannel;
      this.waitingForFrame = true;
   }

   public AbstractFramedStreamSourceChannel(C framedChannel, PooledByteBuffer data, long frameDataRemaining) {
      this.framedChannel = framedChannel;
      this.waitingForFrame = data == null && frameDataRemaining <= 0L;
      this.frameDataRemaining = frameDataRemaining;
      this.currentStreamSize = frameDataRemaining;
      if (data != null) {
         if (!data.getBuffer().hasRemaining()) {
            data.close();
            this.data = null;
            this.waitingForFrame = frameDataRemaining <= 0L;
         } else {
            this.dataReady((FrameHeaderData)null, data);
         }
      }

   }

   public long transferTo(long position, long count, FileChannel target) throws IOException {
      if (Bits.anyAreSet(this.state, 2)) {
         return -1L;
      } else {
         this.beforeRead();
         if (this.waitingForFrame) {
            return 0L;
         } else {
            long var6;
            try {
               long var7;
               if (this.frameDataRemaining == 0L && Bits.anyAreSet(this.state, 32)) {
                  synchronized(this.lock) {
                     this.state |= 256;
                     var7 = -1L;
                     return var7;
                  }
               }

               if (this.data != null) {
                  int old = this.data.getBuffer().limit();

                  try {
                     if (count < (long)this.data.getBuffer().remaining()) {
                        this.data.getBuffer().limit((int)((long)this.data.getBuffer().position() + count));
                     }

                     var7 = (long)target.write(this.data.getBuffer(), position);
                     return var7;
                  } finally {
                     this.data.getBuffer().limit(old);
                     this.decrementFrameDataRemaining();
                  }
               }

               var6 = 0L;
            } finally {
               this.exitRead();
            }

            return var6;
         }
      }
   }

   private void decrementFrameDataRemaining() {
      if (!this.data.getBuffer().hasRemaining()) {
         this.frameDataRemaining -= (long)this.currentDataOriginalSize;
      }

   }

   public long transferTo(long count, ByteBuffer throughBuffer, StreamSinkChannel streamSinkChannel) throws IOException {
      if (Bits.anyAreSet(this.state, 2)) {
         return -1L;
      } else {
         this.beforeRead();
         if (this.waitingForFrame) {
            throughBuffer.position(throughBuffer.limit());
            return 0L;
         } else {
            long var7;
            try {
               if (this.frameDataRemaining == 0L && Bits.anyAreSet(this.state, 32)) {
                  synchronized(this.lock) {
                     this.state |= 256;
                     long var21 = -1L;
                     return var21;
                  }
               }

               if (this.data == null || !this.data.getBuffer().hasRemaining()) {
                  throughBuffer.position(throughBuffer.limit());
                  long var20 = 0L;
                  return var20;
               }

               int old = this.data.getBuffer().limit();

               try {
                  if (count < (long)this.data.getBuffer().remaining()) {
                     this.data.getBuffer().limit((int)((long)this.data.getBuffer().position() + count));
                  }

                  int written = streamSinkChannel.write(this.data.getBuffer());
                  if (this.data.getBuffer().hasRemaining()) {
                     throughBuffer.clear();
                     Buffers.copy(throughBuffer, this.data.getBuffer());
                     throughBuffer.flip();
                  } else {
                     throughBuffer.position(throughBuffer.limit());
                  }

                  var7 = (long)written;
               } finally {
                  this.data.getBuffer().limit(old);
                  this.decrementFrameDataRemaining();
               }
            } finally {
               this.exitRead();
            }

            return var7;
         }
      }
   }

   public long getMaxStreamSize() {
      return this.maxStreamSize;
   }

   public void setMaxStreamSize(long maxStreamSize) {
      this.maxStreamSize = maxStreamSize;
      if (maxStreamSize > 0L && maxStreamSize < this.currentStreamSize) {
         this.handleStreamTooLarge();
      }

   }

   private void handleStreamTooLarge() {
      IoUtils.safeClose((Closeable)this);
   }

   public void suspendReads() {
      synchronized(this.lock) {
         this.state &= -13;
      }
   }

   protected void complete() throws IOException {
      this.close();
   }

   protected boolean isComplete() {
      return Bits.anyAreSet(this.state, 2);
   }

   public void resumeReads() {
      this.resumeReadsInternal(false);
   }

   public boolean isReadResumed() {
      return Bits.anyAreSet(this.state, 4);
   }

   public void wakeupReads() {
      this.resumeReadsInternal(true);
   }

   public void addCloseTask(ChannelListener<R> channelListener) {
      if (this.closeListeners == null) {
         this.closeListeners = new ChannelListener[]{channelListener};
      } else {
         ChannelListener[] old = this.closeListeners;
         this.closeListeners = new ChannelListener[old.length + 1];
         System.arraycopy(old, 0, this.closeListeners, 0, old.length);
         this.closeListeners[old.length] = channelListener;
      }

   }

   void resumeReadsInternal(boolean wakeup) {
      synchronized(this.lock) {
         this.state |= 4;
         if (wakeup) {
            this.state |= 8;
         } else if (!Bits.anyAreSet(this.state, 4)) {
            return;
         }

         if (!Bits.anyAreSet(this.state, 64)) {
            this.state |= 64;
            this.getFramedChannel().runInIoThread(new Runnable() {
               public void run() {
                  try {
                     while(true) {
                        synchronized(AbstractFramedStreamSourceChannel.this.lock) {
                           AbstractFramedStreamSourceChannel.this.state = AbstractFramedStreamSourceChannel.this.state & -9;
                        }

                        ChannelListener<? super R> listener = AbstractFramedStreamSourceChannel.this.getReadListener();
                        if (listener != null && AbstractFramedStreamSourceChannel.this.isReadResumed()) {
                           ChannelListeners.invokeChannelListener(AbstractFramedStreamSourceChannel.this, listener);
                           boolean moreData = AbstractFramedStreamSourceChannel.this.frameDataRemaining > 0L && AbstractFramedStreamSourceChannel.this.data != null || !AbstractFramedStreamSourceChannel.this.pendingFrameData.isEmpty() || Bits.anyAreSet(AbstractFramedStreamSourceChannel.this.state, 512);
                           boolean readAgain;
                           synchronized(AbstractFramedStreamSourceChannel.this.lock) {
                              readAgain = (AbstractFramedStreamSourceChannel.this.isReadResumed() && moreData || Bits.allAreSet(AbstractFramedStreamSourceChannel.this.state, 8)) && Bits.allAreClear(AbstractFramedStreamSourceChannel.this.state, 144);
                              if (!readAgain) {
                                 AbstractFramedStreamSourceChannel.this.state = AbstractFramedStreamSourceChannel.this.state & -65;
                              }
                           }

                           if (readAgain) {
                              continue;
                           }
                           break;
                        }

                        return;
                     }
                  } catch (Error | RuntimeException var10) {
                     synchronized(AbstractFramedStreamSourceChannel.this.lock) {
                        AbstractFramedStreamSourceChannel.this.state = AbstractFramedStreamSourceChannel.this.state & -65;
                     }
                  }

               }
            });
         }

      }
   }

   private ChannelListener<? super R> getReadListener() {
      return this.readSetter.get();
   }

   public void shutdownReads() throws IOException {
      this.close();
   }

   protected void lastFrame() {
      synchronized(this.lock) {
         this.state |= 32;
      }

      this.waitingForFrame = false;
      if (this.data == null && this.pendingFrameData.isEmpty() && this.frameDataRemaining == 0L) {
         synchronized(this.lock) {
            this.state |= 2;
         }

         this.getFramedChannel().notifyFrameReadComplete(this);
         IoUtils.safeClose((Closeable)this);
      }

   }

   protected boolean isLastFrame() {
      return Bits.anyAreSet(this.state, 32);
   }

   public void awaitReadable() throws IOException {
      if (Thread.currentThread() == this.getIoThread()) {
         throw UndertowMessages.MESSAGES.awaitCalledFromIoThread();
      } else {
         if (this.data == null && this.pendingFrameData.isEmpty() && !Bits.anyAreSet(this.state, 144)) {
            synchronized(this.lock) {
               if (this.data == null && this.pendingFrameData.isEmpty() && !Bits.anyAreSet(this.state, 144)) {
                  try {
                     ++this.waiters;
                     this.lock.wait();
                  } catch (InterruptedException var8) {
                     Thread.currentThread().interrupt();
                     throw new InterruptedIOException();
                  } finally {
                     --this.waiters;
                  }
               }
            }
         }

      }
   }

   public void awaitReadable(long l, TimeUnit timeUnit) throws IOException {
      if (Thread.currentThread() == this.getIoThread()) {
         throw UndertowMessages.MESSAGES.awaitCalledFromIoThread();
      } else {
         if (this.data == null && this.pendingFrameData.isEmpty() && !Bits.anyAreSet(this.state, 144)) {
            synchronized(this.lock) {
               if (this.data == null && this.pendingFrameData.isEmpty() && !Bits.anyAreSet(this.state, 144)) {
                  try {
                     ++this.waiters;
                     this.lock.wait(timeUnit.toMillis(l));
                  } catch (InterruptedException var11) {
                     Thread.currentThread().interrupt();
                     throw new InterruptedIOException();
                  } finally {
                     --this.waiters;
                  }
               }
            }
         }

      }
   }

   protected void dataReady(FrameHeaderData headerData, PooledByteBuffer frameData) {
      if (Bits.anyAreSet(this.state, 144)) {
         frameData.close();
      } else {
         synchronized(this.lock) {
            boolean newData = this.pendingFrameData.isEmpty();
            this.pendingFrameData.add(new FrameData(headerData, frameData));
            if (newData && this.waiters > 0) {
               this.lock.notifyAll();
            }

            this.waitingForFrame = false;
         }

         if (Bits.anyAreSet(this.state, 4)) {
            this.resumeReadsInternal(true);
         }

         if (headerData != null) {
            this.currentStreamSize += headerData.getFrameLength();
            if (this.maxStreamSize > 0L && this.currentStreamSize > this.maxStreamSize) {
               this.handleStreamTooLarge();
            }
         }

      }
   }

   protected long updateFrameDataRemaining(PooledByteBuffer frameData, long frameDataRemaining) {
      return frameDataRemaining;
   }

   protected PooledByteBuffer processFrameData(PooledByteBuffer data, boolean lastFragmentOfFrame) throws IOException {
      return data;
   }

   protected void handleHeaderData(FrameHeaderData headerData) {
   }

   public XnioExecutor getReadThread() {
      return this.framedChannel.getIoThread();
   }

   public ChannelListener.Setter<? extends R> getReadSetter() {
      return this.readSetter;
   }

   public ChannelListener.Setter<? extends R> getCloseSetter() {
      return this.closeSetter;
   }

   public XnioWorker getWorker() {
      return this.framedChannel.getWorker();
   }

   public XnioIoThread getIoThread() {
      return this.framedChannel.getIoThread();
   }

   public boolean supportsOption(Option<?> option) {
      return false;
   }

   public <T> T getOption(Option<T> tOption) throws IOException {
      return null;
   }

   public <T> T setOption(Option<T> tOption, T t) throws IllegalArgumentException, IOException {
      return null;
   }

   public long read(ByteBuffer[] dsts, int offset, int length) throws IOException {
      if (Bits.anyAreSet(this.state, 2)) {
         return -1L;
      } else {
         this.beforeRead();
         if (this.waitingForFrame) {
            return 0L;
         } else {
            long var7;
            try {
               long var20;
               if (this.frameDataRemaining == 0L && Bits.anyAreSet(this.state, 32)) {
                  synchronized(this.lock) {
                     this.state |= 256;
                  }

                  var20 = -1L;
                  return var20;
               }

               if (this.data == null) {
                  var20 = 0L;
                  return var20;
               }

               int old = this.data.getBuffer().limit();

               try {
                  long count = Buffers.remaining(dsts, offset, length);
                  if (count < (long)this.data.getBuffer().remaining()) {
                     this.data.getBuffer().limit((int)((long)this.data.getBuffer().position() + count));
                  } else {
                     count = (long)this.data.getBuffer().remaining();
                  }

                  var7 = (long)Buffers.copy((int)count, dsts, offset, length, this.data.getBuffer());
               } finally {
                  this.data.getBuffer().limit(old);
                  this.decrementFrameDataRemaining();
               }
            } finally {
               this.exitRead();
            }

            return var7;
         }
      }
   }

   public long read(ByteBuffer[] dsts) throws IOException {
      return this.read(dsts, 0, dsts.length);
   }

   public int read(ByteBuffer dst) throws IOException {
      if (Bits.anyAreSet(this.state, 2)) {
         return -1;
      } else if (!dst.hasRemaining()) {
         return 0;
      } else {
         this.beforeRead();
         if (this.waitingForFrame) {
            return 0;
         } else {
            int old;
            try {
               if (this.frameDataRemaining != 0L || !Bits.anyAreSet(this.state, 32)) {
                  if (this.data != null) {
                     old = this.data.getBuffer().limit();

                     try {
                        int count = dst.remaining();
                        if (count < this.data.getBuffer().remaining()) {
                           this.data.getBuffer().limit(this.data.getBuffer().position() + count);
                        } else {
                           count = this.data.getBuffer().remaining();
                        }

                        int var4 = Buffers.copy(count, dst, this.data.getBuffer());
                        return var4;
                     } finally {
                        this.data.getBuffer().limit(old);
                        this.decrementFrameDataRemaining();
                     }
                  }

                  old = 0;
                  return old;
               }

               synchronized(this.lock) {
                  this.state |= 256;
               }

               old = -1;
            } finally {
               try {
                  this.exitRead();
               } catch (Throwable var20) {
                  this.markStreamBroken();
               }

            }

            return old;
         }
      }
   }

   private void beforeRead() throws IOException {
      if (Bits.anyAreSet(this.state, 128)) {
         throw UndertowMessages.MESSAGES.channelIsClosed();
      } else {
         if (this.data == null) {
            synchronized(this.lock) {
               AbstractFramedStreamSourceChannel<C, R, S>.FrameData pending = (FrameData)this.pendingFrameData.poll();
               if (pending != null) {
                  PooledByteBuffer frameData = pending.getFrameData();
                  boolean hasData = true;
                  if (!frameData.getBuffer().hasRemaining()) {
                     frameData.close();
                     hasData = false;
                  }

                  if (pending.getFrameHeaderData() != null) {
                     this.frameDataRemaining = pending.getFrameHeaderData().getFrameLength();
                     this.handleHeaderData(pending.getFrameHeaderData());
                  }

                  if (hasData) {
                     this.frameDataRemaining = this.updateFrameDataRemaining(frameData, this.frameDataRemaining);
                     this.currentDataOriginalSize = frameData.getBuffer().remaining();

                     try {
                        this.data = this.processFrameData(frameData, this.frameDataRemaining - (long)this.currentDataOriginalSize == 0L);
                     } catch (Throwable var7) {
                        frameData.close();
                        UndertowLogger.REQUEST_IO_LOGGER.ioException(new IOException(var7));
                        this.markStreamBroken();
                     }
                  }
               }
            }
         }

      }
   }

   private void exitRead() throws IOException {
      if (this.data != null && !this.data.getBuffer().hasRemaining()) {
         this.data.close();
         this.data = null;
      }

      if (this.frameDataRemaining == 0L) {
         try {
            synchronized(this.lock) {
               ++this.readFrameCount;
               if (this.pendingFrameData.isEmpty()) {
                  if (Bits.anyAreSet(this.state, 256)) {
                     this.state |= 2;
                     this.complete();
                     this.close();
                  } else if (Bits.anyAreSet(this.state, 32)) {
                     this.state |= 512;
                  } else {
                     this.waitingForFrame = true;
                  }
               }
            }
         } finally {
            if (this.pendingFrameData.isEmpty()) {
               this.framedChannel.notifyFrameReadComplete(this);
            }

         }
      }

   }

   public boolean isOpen() {
      return Bits.allAreClear(this.state, 16);
   }

   public void close() {
      if (!Bits.anyAreSet(this.state, 16)) {
         synchronized(this.lock) {
            if (!Bits.anyAreSet(this.state, 16)) {
               this.state |= 16;
               if (Bits.allAreClear(this.state, 34)) {
                  this.state |= 128;
                  this.channelForciblyClosed();
               }

               if (this.data != null) {
                  this.data.close();
                  this.data = null;
               }

               while(!this.pendingFrameData.isEmpty()) {
                  ((FrameData)this.pendingFrameData.poll()).frameData.close();
               }

               ChannelListeners.invokeChannelListener(this, this.closeSetter.get());
               if (this.closeListeners != null) {
                  for(int i = 0; i < this.closeListeners.length; ++i) {
                     this.closeListeners[i].handleEvent(this);
                  }
               }

               if (this.waiters > 0) {
                  this.lock.notifyAll();
               }

            }
         }
      }
   }

   protected void channelForciblyClosed() {
   }

   protected C getFramedChannel() {
      return this.framedChannel;
   }

   protected int getReadFrameCount() {
      return this.readFrameCount;
   }

   protected void markStreamBroken() {
      if (!Bits.anyAreSet(this.state, 128)) {
         synchronized(this.lock) {
            this.state |= 128;
            PooledByteBuffer data = this.data;
            if (data != null) {
               try {
                  data.close();
               } catch (Throwable var6) {
               }

               this.data = null;
            }

            Iterator var3 = this.pendingFrameData.iterator();

            while(var3.hasNext()) {
               AbstractFramedStreamSourceChannel<C, R, S>.FrameData frame = (FrameData)var3.next();
               frame.frameData.close();
            }

            this.pendingFrameData.clear();
            if (this.isReadResumed()) {
               this.resumeReadsInternal(true);
            }

            if (this.waiters > 0) {
               this.lock.notifyAll();
            }

         }
      }
   }

   private class FrameData {
      private final FrameHeaderData frameHeaderData;
      private final PooledByteBuffer frameData;

      FrameData(FrameHeaderData frameHeaderData, PooledByteBuffer frameData) {
         this.frameHeaderData = frameHeaderData;
         this.frameData = frameData;
      }

      FrameHeaderData getFrameHeaderData() {
         return this.frameHeaderData;
      }

      PooledByteBuffer getFrameData() {
         return this.frameData;
      }
   }
}
