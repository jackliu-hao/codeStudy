package io.undertow.server.protocol.framed;

import io.undertow.UndertowLogger;
import io.undertow.UndertowMessages;
import io.undertow.connector.PooledByteBuffer;
import io.undertow.util.ImmediatePooledByteBuffer;
import java.io.Closeable;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.security.AccessController;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import org.xnio.Bits;
import org.xnio.Buffers;
import org.xnio.ChannelListener;
import org.xnio.ChannelListeners;
import org.xnio.IoUtils;
import org.xnio.Option;
import org.xnio.XnioExecutor;
import org.xnio.XnioIoThread;
import org.xnio.XnioWorker;
import org.xnio.channels.Channels;
import org.xnio.channels.StreamSinkChannel;
import org.xnio.channels.StreamSourceChannel;

public abstract class AbstractFramedStreamSinkChannel<C extends AbstractFramedChannel<C, R, S>, R extends AbstractFramedStreamSourceChannel<C, R, S>, S extends AbstractFramedStreamSinkChannel<C, R, S>> implements StreamSinkChannel {
   private static final int AWAIT_WRITABLE_TIMEOUT;
   private static final PooledByteBuffer EMPTY_BYTE_BUFFER;
   private final C channel;
   private final ChannelListener.SimpleSetter<S> writeSetter = new ChannelListener.SimpleSetter();
   private final ChannelListener.SimpleSetter<S> closeSetter = new ChannelListener.SimpleSetter();
   private final Object lock = new Object();
   private volatile int state = 0;
   private volatile boolean readyForFlush;
   private volatile boolean fullyFlushed;
   private volatile boolean finalFrameQueued;
   private volatile boolean broken;
   private volatile int waiterCount = 0;
   private volatile SendFrameHeader header;
   private volatile PooledByteBuffer writeBuffer;
   private volatile PooledByteBuffer body;
   private static final int STATE_CLOSED = 1;
   private static final int STATE_WRITES_SHUTDOWN = 2;
   private static final int STATE_FIRST_DATA_WRITTEN = 4;
   private static final int STATE_PRE_WRITE_CALLED = 8;
   private volatile boolean bufferFull;
   private volatile boolean writesResumed;
   private volatile int inListenerLoop;
   private volatile boolean writeSucceeded;
   private static final AtomicIntegerFieldUpdater<AbstractFramedStreamSinkChannel> inListenerLoopUpdater;

   protected AbstractFramedStreamSinkChannel(C channel) {
      this.channel = channel;
   }

   public long transferFrom(FileChannel src, long position, long count) throws IOException {
      return src.transferTo(position, count, this);
   }

   public long transferFrom(StreamSourceChannel source, long count, ByteBuffer throughBuffer) throws IOException {
      return IoUtils.transfer(source, count, throughBuffer, this);
   }

   public void suspendWrites() {
      this.writesResumed = false;
   }

   final SendFrameHeader getFrameHeader() throws IOException {
      if (this.header == null) {
         this.header = this.createFrameHeader();
         if (this.header == null) {
            this.header = new SendFrameHeader(0, (PooledByteBuffer)null);
         }
      }

      return this.header;
   }

   protected SendFrameHeader createFrameHeader() throws IOException {
      return null;
   }

   final void preWrite() {
      synchronized(this.lock) {
         if (Bits.allAreClear(this.state, 8)) {
            this.state |= 8;
            this.body = this.preWriteTransform(this.body);
         }

      }
   }

   protected PooledByteBuffer preWriteTransform(PooledByteBuffer body) {
      return body;
   }

   public boolean isWriteResumed() {
      return this.writesResumed;
   }

   public void wakeupWrites() {
      this.resumeWritesInternal(true);
   }

   public void resumeWrites() {
      this.resumeWritesInternal(false);
   }

   protected void resumeWritesInternal(boolean wakeup) {
      boolean alreadyResumed = this.writesResumed;
      if (wakeup || !alreadyResumed) {
         this.writesResumed = true;
         if (!this.readyForFlush || wakeup) {
            if (inListenerLoopUpdater.compareAndSet(this, 0, 1)) {
               this.getChannel().runInIoThread(new Runnable() {
                  int loopCount = 0;

                  public void run() {
                     try {
                        ChannelListener<? super S> listener = AbstractFramedStreamSinkChannel.this.getWriteListener();
                        if (listener == null || !AbstractFramedStreamSinkChannel.this.isWriteResumed()) {
                           return;
                        }

                        if (AbstractFramedStreamSinkChannel.this.writeSucceeded) {
                           AbstractFramedStreamSinkChannel.this.writeSucceeded = false;
                           this.loopCount = 0;
                        } else if (this.loopCount++ == 100) {
                           UndertowLogger.ROOT_LOGGER.listenerNotProgressing();
                           IoUtils.safeClose((Closeable)AbstractFramedStreamSinkChannel.this);
                           return;
                        }

                        ChannelListeners.invokeChannelListener(AbstractFramedStreamSinkChannel.this, listener);
                     } finally {
                        AbstractFramedStreamSinkChannel.inListenerLoopUpdater.set(AbstractFramedStreamSinkChannel.this, 0);
                     }

                     if (AbstractFramedStreamSinkChannel.this.writesResumed && Bits.allAreClear(AbstractFramedStreamSinkChannel.this.state, 1) && !AbstractFramedStreamSinkChannel.this.broken && !AbstractFramedStreamSinkChannel.this.readyForFlush && !AbstractFramedStreamSinkChannel.this.fullyFlushed && AbstractFramedStreamSinkChannel.inListenerLoopUpdater.compareAndSet(AbstractFramedStreamSinkChannel.this, 0, 1)) {
                        AbstractFramedStreamSinkChannel.this.getIoThread().execute(this);
                     }

                  }
               });
            }

         }
      }
   }

   public void shutdownWrites() throws IOException {
      this.queueFinalFrame();
      synchronized(this.lock) {
         if (!Bits.anyAreSet(this.state, 2) && !this.broken) {
            this.state |= 2;
         }
      }
   }

   private void queueFinalFrame() throws IOException {
      synchronized(this.lock) {
         if (this.readyForFlush || this.fullyFlushed || !Bits.allAreClear(this.state, 1) || this.broken || this.finalFrameQueued) {
            return;
         }

         if (null == this.body && null != this.writeBuffer) {
            this.sendWriteBuffer();
         } else if (null == this.body) {
            this.body = EMPTY_BYTE_BUFFER;
         }

         this.readyForFlush = true;
         this.state |= 4;
         this.state |= 2;
         this.finalFrameQueued = true;
      }

      this.channel.queueFrame(this);
   }

   protected boolean isFinalFrameQueued() {
      return this.finalFrameQueued;
   }

   public void awaitWritable() throws IOException {
      if (Thread.currentThread() == this.getIoThread()) {
         throw UndertowMessages.MESSAGES.awaitCalledFromIoThread();
      } else {
         synchronized(this.lock) {
            if (!Bits.anyAreSet(this.state, 1) && !this.broken) {
               if (this.readyForFlush) {
                  try {
                     ++this.waiterCount;
                     if (this.readyForFlush && !Bits.anyAreSet(this.state, 1) && !this.broken) {
                        this.lock.wait((long)AWAIT_WRITABLE_TIMEOUT);
                     }
                  } catch (InterruptedException var8) {
                     Thread.currentThread().interrupt();
                     throw new InterruptedIOException();
                  } finally {
                     --this.waiterCount;
                  }
               }

            }
         }
      }
   }

   public void awaitWritable(long l, TimeUnit timeUnit) throws IOException {
      if (Thread.currentThread() == this.getIoThread()) {
         throw UndertowMessages.MESSAGES.awaitCalledFromIoThread();
      } else {
         synchronized(this.lock) {
            if (!Bits.anyAreSet(this.state, 1) && !this.broken) {
               if (this.readyForFlush) {
                  try {
                     ++this.waiterCount;
                     if (this.readyForFlush && !Bits.anyAreSet(this.state, 1) && !this.broken) {
                        this.lock.wait(timeUnit.toMillis(l));
                     }
                  } catch (InterruptedException var11) {
                     Thread.currentThread().interrupt();
                     throw new InterruptedIOException();
                  } finally {
                     --this.waiterCount;
                  }
               }

            }
         }
      }
   }

   public XnioExecutor getWriteThread() {
      return this.channel.getIoThread();
   }

   public ChannelListener.Setter<? extends S> getWriteSetter() {
      return this.writeSetter;
   }

   public ChannelListener.Setter<? extends S> getCloseSetter() {
      return this.closeSetter;
   }

   public XnioWorker getWorker() {
      return this.channel.getWorker();
   }

   public XnioIoThread getIoThread() {
      return this.channel.getIoThread();
   }

   public boolean flush() throws IOException {
      if (Bits.anyAreSet(this.state, 1)) {
         return true;
      } else if (this.broken) {
         throw UndertowMessages.MESSAGES.channelIsClosed();
      } else if (this.readyForFlush) {
         return false;
      } else {
         synchronized(this.lock) {
            if (this.fullyFlushed) {
               this.state |= 1;
               return true;
            }
         }

         if (Bits.anyAreSet(this.state, 2) && !this.finalFrameQueued) {
            this.queueFinalFrame();
            return false;
         } else if (Bits.anyAreSet(this.state, 2)) {
            return false;
         } else if (!this.isFlushRequiredOnEmptyBuffer() && (this.writeBuffer == null || this.writeBuffer.getBuffer().position() <= 0)) {
            return true;
         } else {
            this.handleBufferFull();
            return !this.readyForFlush;
         }
      }
   }

   protected boolean isFlushRequiredOnEmptyBuffer() {
      return false;
   }

   public long write(ByteBuffer[] srcs, int offset, int length) throws IOException {
      if (!this.safeToSend()) {
         return 0L;
      } else {
         if (this.writeBuffer == null) {
            this.writeBuffer = this.getChannel().getBufferPool().allocate();
         }

         ByteBuffer buffer = this.writeBuffer.getBuffer();
         int copied = Buffers.copy(buffer, srcs, offset, length);
         if (!buffer.hasRemaining()) {
            this.handleBufferFull();
         }

         this.writeSucceeded = this.writeSucceeded || copied > 0;
         return (long)copied;
      }
   }

   public long write(ByteBuffer[] srcs) throws IOException {
      return this.write(srcs, 0, srcs.length);
   }

   public int write(ByteBuffer src) throws IOException {
      if (!this.safeToSend()) {
         return 0;
      } else {
         if (this.writeBuffer == null) {
            this.writeBuffer = this.getChannel().getBufferPool().allocate();
         }

         ByteBuffer buffer = this.writeBuffer.getBuffer();
         int copied = Buffers.copy(buffer, src);
         if (!buffer.hasRemaining()) {
            this.handleBufferFull();
         }

         this.writeSucceeded = this.writeSucceeded || copied > 0;
         return copied;
      }
   }

   public boolean send(PooledByteBuffer pooled) throws IOException {
      if (this.isWritesShutdown()) {
         throw UndertowMessages.MESSAGES.channelIsClosed();
      } else {
         boolean result = this.sendInternal(pooled);
         if (result) {
            this.flush();
         }

         return result;
      }
   }

   protected boolean sendInternal(PooledByteBuffer pooled) throws IOException {
      if (this.safeToSend()) {
         this.body = pooled;
         this.writeSucceeded = true;
         return true;
      } else {
         return false;
      }
   }

   protected boolean safeToSend() throws IOException {
      int state = this.state;
      if (!Bits.anyAreSet(state, 1) && !this.broken) {
         if (this.readyForFlush) {
            return false;
         } else if (null != this.body) {
            throw UndertowMessages.MESSAGES.bodyIsSetAndNotReadyForFlush();
         } else {
            return true;
         }
      } else {
         throw UndertowMessages.MESSAGES.channelIsClosed();
      }
   }

   protected long getAwaitWritableTimeout() {
      return (long)AWAIT_WRITABLE_TIMEOUT;
   }

   public long writeFinal(ByteBuffer[] srcs, int offset, int length) throws IOException {
      return Channels.writeFinalBasic(this, srcs, offset, length);
   }

   public long writeFinal(ByteBuffer[] srcs) throws IOException {
      return this.writeFinal(srcs, 0, srcs.length);
   }

   public int writeFinal(ByteBuffer src) throws IOException {
      return Channels.writeFinalBasic(this, src);
   }

   private void handleBufferFull() throws IOException {
      synchronized(this.lock) {
         this.bufferFull = true;
         if (this.readyForFlush) {
            return;
         }

         this.sendWriteBuffer();
         this.readyForFlush = true;
         this.state |= 4;
      }

      this.channel.queueFrame(this);
   }

   private void sendWriteBuffer() throws IOException {
      if (this.writeBuffer == null) {
         this.writeBuffer = EMPTY_BYTE_BUFFER;
      }

      this.writeBuffer.getBuffer().flip();
      if (!this.sendInternal(this.writeBuffer)) {
         throw UndertowMessages.MESSAGES.failedToSendAfterBeingSafe();
      } else {
         this.writeBuffer = null;
      }
   }

   protected abstract boolean isLastFrame();

   public boolean isReadyForFlush() {
      return this.readyForFlush;
   }

   public boolean isWritesShutdown() {
      return Bits.anyAreSet(this.state, 2);
   }

   public boolean isOpen() {
      return Bits.allAreClear(this.state, 1);
   }

   public void close() throws IOException {
      if (!this.fullyFlushed && !Bits.anyAreSet(this.state, 1)) {
         try {
            synchronized(this.lock) {
               if (this.fullyFlushed || Bits.anyAreSet(this.state, 1)) {
                  return;
               }

               this.state |= 1;
               if (this.writeBuffer != null) {
                  this.writeBuffer.close();
                  this.writeBuffer = null;
               }

               if (this.body != null) {
                  this.body.close();
                  this.body = null;
               }

               if (this.header != null && this.header.getByteBuffer() != null) {
                  this.header.getByteBuffer().close();
                  this.header = null;
               }
            }

            this.channelForciblyClosed();
            if (this.isWriteResumed()) {
               ChannelListeners.invokeChannelListener(this.getIoThread(), this, this.getWriteListener());
            }

            this.wakeupWrites();
         } finally {
            this.wakeupWaiters();
         }

      }
   }

   protected void channelForciblyClosed() throws IOException {
      if (this.isFirstDataWritten()) {
         this.getChannel().markWritesBroken((Throwable)null);
      }

      this.wakeupWaiters();
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

   public ByteBuffer getBuffer() {
      if (Bits.anyAreSet(this.state, 1)) {
         throw new IllegalStateException();
      } else {
         if (this.body == null) {
            this.body = EMPTY_BYTE_BUFFER;
         }

         return this.body.getBuffer();
      }
   }

   final void flushComplete() throws IOException {
      synchronized(this.lock) {
         try {
            boolean resetReadyForFlush = true;
            this.bufferFull = false;
            int remaining = this.header.getRemainingInBuffer();
            boolean finalFrame = this.finalFrameQueued;
            boolean channelClosed = finalFrame && remaining == 0 && !this.header.isAnotherFrameRequired();
            if (remaining > 0) {
               this.body.getBuffer().limit(this.body.getBuffer().limit() + remaining);
               this.body.getBuffer().compact();
               this.writeBuffer = this.body;
               this.body = null;
               this.state &= -9;
               if (finalFrame) {
                  this.finalFrameQueued = false;
                  resetReadyForFlush = this.readyForFlush = false;
                  this.queueFinalFrame();
               }
            } else if (this.header.isAnotherFrameRequired()) {
               this.finalFrameQueued = false;
               if (this.body != null) {
                  this.body.close();
                  this.body = null;
                  this.state &= -9;
               }
            } else if (this.body != null) {
               this.body.close();
               this.body = null;
               this.state &= -9;
            }

            if (channelClosed) {
               this.fullyFlushed = true;
               if (this.body != null) {
                  this.body.close();
                  this.body = null;
                  this.state &= -9;
               }
            }

            if (this.header.getByteBuffer() != null) {
               this.header.getByteBuffer().close();
            }

            this.header = null;
            if (resetReadyForFlush) {
               this.readyForFlush = false;
            }

            if (this.isWriteResumed() && !channelClosed) {
               this.wakeupWrites();
            } else if (this.isWriteResumed()) {
               ChannelListeners.invokeChannelListener(this.getIoThread(), this, this.getWriteListener());
            }

            ChannelListener<? super S> closeListener = this.closeSetter.get();
            if (channelClosed && closeListener != null) {
               ChannelListeners.invokeChannelListener(this.getIoThread(), this, closeListener);
            }

            this.handleFlushComplete(channelClosed);
         } finally {
            this.wakeupWaiters();
         }

      }
   }

   protected void handleFlushComplete(boolean finalFrame) {
   }

   protected boolean isFirstDataWritten() {
      return Bits.anyAreSet(this.state, 4);
   }

   public void markBroken() {
      this.broken = true;

      try {
         this.wakeupWrites();
         this.wakeupWaiters();
         ChannelListener closeListener;
         if (this.isWriteResumed()) {
            closeListener = this.writeSetter.get();
            if (closeListener != null) {
               ChannelListeners.invokeChannelListener(this.getIoThread(), this, closeListener);
            }
         }

         closeListener = this.closeSetter.get();
         if (closeListener != null) {
            ChannelListeners.invokeChannelListener(this.getIoThread(), this, closeListener);
         }
      } finally {
         if (this.header != null && this.header.getByteBuffer() != null) {
            this.header.getByteBuffer().close();
            this.header = null;
         }

         if (this.body != null) {
            this.body.close();
            this.body = null;
         }

         if (this.writeBuffer != null) {
            this.writeBuffer.close();
            this.writeBuffer = null;
         }

      }

   }

   ChannelListener<? super S> getWriteListener() {
      return this.writeSetter.get();
   }

   private void wakeupWaiters() {
      if (this.waiterCount > 0) {
         synchronized(this.lock) {
            if (this.waiterCount > 0) {
               this.lock.notifyAll();
            }
         }
      }

   }

   public C getChannel() {
      return this.channel;
   }

   public boolean isBroken() {
      return this.broken;
   }

   public boolean isBufferFull() {
      return this.bufferFull;
   }

   static {
      int defaultAwaitWritableTimeout = 600000;
      int await_writable_timeout = (Integer)AccessController.doPrivileged(() -> {
         return Integer.getInteger("io.undertow.await_writable_timeout", 600000);
      });
      AWAIT_WRITABLE_TIMEOUT = await_writable_timeout > 0 ? await_writable_timeout : 600000;
      EMPTY_BYTE_BUFFER = new ImmediatePooledByteBuffer(ByteBuffer.allocateDirect(0));
      inListenerLoopUpdater = AtomicIntegerFieldUpdater.newUpdater(AbstractFramedStreamSinkChannel.class, "inListenerLoop");
   }
}
