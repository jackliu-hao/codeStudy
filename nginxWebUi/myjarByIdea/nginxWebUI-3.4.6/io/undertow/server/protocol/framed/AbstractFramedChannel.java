package io.undertow.server.protocol.framed;

import io.undertow.UndertowLogger;
import io.undertow.UndertowMessages;
import io.undertow.UndertowOptions;
import io.undertow.conduits.IdleTimeoutConduit;
import io.undertow.connector.ByteBufferPool;
import io.undertow.connector.PooledByteBuffer;
import io.undertow.util.ReferenceCountedPooled;
import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import org.xnio.Buffers;
import org.xnio.ChannelExceptionHandler;
import org.xnio.ChannelListener;
import org.xnio.ChannelListeners;
import org.xnio.IoUtils;
import org.xnio.Option;
import org.xnio.OptionMap;
import org.xnio.StreamConnection;
import org.xnio.XnioIoThread;
import org.xnio.XnioWorker;
import org.xnio.channels.CloseableChannel;
import org.xnio.channels.ConnectedChannel;
import org.xnio.channels.StreamSinkChannel;
import org.xnio.channels.StreamSourceChannel;
import org.xnio.channels.SuspendableWriteChannel;

public abstract class AbstractFramedChannel<C extends AbstractFramedChannel<C, R, S>, R extends AbstractFramedStreamSourceChannel<C, R, S>, S extends AbstractFramedStreamSinkChannel<C, R, S>> implements ConnectedChannel {
   private final int maxQueuedBuffers;
   private final StreamConnection channel;
   private final IdleTimeoutConduit idleTimeoutConduit;
   private final ChannelListener.SimpleSetter<C> closeSetter;
   private final ChannelListener.SimpleSetter<C> receiveSetter;
   private final ByteBufferPool bufferPool;
   private final FramePriority<C, R, S> framePriority;
   private final List<S> pendingFrames = new LinkedList();
   private final Deque<S> heldFrames = new ArrayDeque();
   private final Deque<S> newFrames = new LinkedBlockingDeque();
   private volatile long frameDataRemaining;
   private volatile R receiver;
   private volatile boolean receivesSuspendedByUser = true;
   private volatile boolean receivesSuspendedTooManyQueuedMessages = false;
   private volatile boolean receivesSuspendedTooManyBuffers = false;
   private volatile int readsBroken = 0;
   private volatile int writesBroken = 0;
   private static final AtomicIntegerFieldUpdater<AbstractFramedChannel> readsBrokenUpdater = AtomicIntegerFieldUpdater.newUpdater(AbstractFramedChannel.class, "readsBroken");
   private static final AtomicIntegerFieldUpdater<AbstractFramedChannel> writesBrokenUpdater = AtomicIntegerFieldUpdater.newUpdater(AbstractFramedChannel.class, "writesBroken");
   private volatile ReferenceCountedPooled readData = null;
   private final List<ChannelListener<C>> closeTasks = new CopyOnWriteArrayList();
   private volatile boolean flushingSenders = false;
   private boolean partialRead = false;
   private volatile int outstandingBuffers;
   private static final AtomicIntegerFieldUpdater<AbstractFramedChannel> outstandingBuffersUpdater = AtomicIntegerFieldUpdater.newUpdater(AbstractFramedChannel.class, "outstandingBuffers");
   private final LinkedBlockingDeque<Runnable> taskRunQueue = new LinkedBlockingDeque();
   private final Runnable taskRunQueueRunnable = new Runnable() {
      public void run() {
         Runnable runnable;
         while((runnable = (Runnable)AbstractFramedChannel.this.taskRunQueue.poll()) != null) {
            runnable.run();
         }

      }
   };
   private final OptionMap settings;
   private volatile boolean requireExplicitFlush = false;
   private volatile boolean readChannelDone = false;
   private final int queuedFrameHighWaterMark;
   private final int queuedFrameLowWaterMark;
   private final ReferenceCountedPooled.FreeNotifier freeNotifier = new ReferenceCountedPooled.FreeNotifier() {
      public void freed() {
         int res = AbstractFramedChannel.outstandingBuffersUpdater.decrementAndGet(AbstractFramedChannel.this);
         if (!AbstractFramedChannel.this.receivesSuspendedByUser && res == AbstractFramedChannel.this.maxQueuedBuffers - 1) {
            AbstractFramedChannel.this.getIoThread().execute(new Runnable() {
               public void run() {
                  synchronized(AbstractFramedChannel.this) {
                     if (AbstractFramedChannel.outstandingBuffersUpdater.get(AbstractFramedChannel.this) < AbstractFramedChannel.this.maxQueuedBuffers) {
                        if (UndertowLogger.REQUEST_IO_LOGGER.isTraceEnabled()) {
                           UndertowLogger.REQUEST_IO_LOGGER.tracef("Resuming reads on %s as buffers have been consumed", AbstractFramedChannel.this);
                        }

                        (AbstractFramedChannel.this.new UpdateResumeState((Boolean)null, false, (Boolean)null)).run();
                     }

                  }
               }
            });
         }

      }
   };
   private static final ChannelListener<AbstractFramedChannel> DRAIN_LISTENER = new ChannelListener<AbstractFramedChannel>() {
      public void handleEvent(AbstractFramedChannel channel) {
         try {
            AbstractFramedStreamSourceChannel stream = channel.receive();
            if (stream != null) {
               UndertowLogger.REQUEST_IO_LOGGER.debugf("Draining channel %s as no receive listener has been set", stream);
               stream.getReadSetter().set(ChannelListeners.drainListener(Long.MAX_VALUE, (ChannelListener)null, (ChannelExceptionHandler)null));
               stream.wakeupReads();
            }
         } catch (RuntimeException | Error | IOException var3) {
            IoUtils.safeClose((Closeable)channel);
         }

      }
   };

   protected AbstractFramedChannel(StreamConnection connectedStreamChannel, ByteBufferPool bufferPool, FramePriority<C, R, S> framePriority, PooledByteBuffer readData, OptionMap settings) {
      this.framePriority = framePriority;
      this.maxQueuedBuffers = settings.get(UndertowOptions.MAX_QUEUED_READ_BUFFERS, 10);
      this.settings = settings;
      if (readData != null) {
         if (readData.getBuffer().hasRemaining()) {
            this.readData = new ReferenceCountedPooled(readData, 1);
         } else {
            readData.close();
         }
      }

      if (bufferPool == null) {
         throw UndertowMessages.MESSAGES.argumentCannotBeNull("bufferPool");
      } else if (connectedStreamChannel == null) {
         throw UndertowMessages.MESSAGES.argumentCannotBeNull("connectedStreamChannel");
      } else {
         IdleTimeoutConduit idle = this.createIdleTimeoutChannel(connectedStreamChannel);
         connectedStreamChannel.getSourceChannel().setConduit(idle);
         connectedStreamChannel.getSinkChannel().setConduit(idle);
         this.idleTimeoutConduit = idle;
         this.channel = connectedStreamChannel;
         this.bufferPool = bufferPool;
         this.closeSetter = new ChannelListener.SimpleSetter();
         this.receiveSetter = new ChannelListener.SimpleSetter();
         this.channel.getSourceChannel().getReadSetter().set((ChannelListener)null);
         this.channel.getSourceChannel().suspendReads();
         this.channel.getSourceChannel().getReadSetter().set(new FrameReadListener());
         connectedStreamChannel.getSinkChannel().getWriteSetter().set(new FrameWriteListener());
         AbstractFramedChannel<C, R, S>.FrameCloseListener closeListener = new FrameCloseListener();
         connectedStreamChannel.getSinkChannel().getCloseSetter().set(closeListener);
         connectedStreamChannel.getSourceChannel().getCloseSetter().set(closeListener);
         this.queuedFrameHighWaterMark = settings.get(UndertowOptions.QUEUED_FRAMES_HIGH_WATER_MARK, 50);
         this.queuedFrameLowWaterMark = settings.get(UndertowOptions.QUEUED_FRAMES_LOW_WATER_MARK, 10);
      }
   }

   protected IdleTimeoutConduit createIdleTimeoutChannel(StreamConnection connectedStreamChannel) {
      return new IdleTimeoutConduit(connectedStreamChannel);
   }

   void runInIoThread(Runnable task) {
      this.taskRunQueue.add(task);

      try {
         this.getIoThread().execute(this.taskRunQueueRunnable);
      } catch (RejectedExecutionException var3) {
         ShutdownFallbackExecutor.execute(this.taskRunQueueRunnable);
      }

   }

   public ByteBufferPool getBufferPool() {
      return this.bufferPool;
   }

   public SocketAddress getLocalAddress() {
      return this.channel.getLocalAddress();
   }

   public <A extends SocketAddress> A getLocalAddress(Class<A> type) {
      return this.channel.getLocalAddress(type);
   }

   public XnioWorker getWorker() {
      return this.channel.getWorker();
   }

   public XnioIoThread getIoThread() {
      return this.channel.getIoThread();
   }

   public boolean supportsOption(Option<?> option) {
      return this.channel.supportsOption(option);
   }

   public <T> T getOption(Option<T> option) throws IOException {
      return this.channel.getOption(option);
   }

   public <T> T setOption(Option<T> option, T value) throws IOException {
      return this.channel.setOption(option, value);
   }

   public boolean isOpen() {
      return this.channel.isOpen();
   }

   public SocketAddress getPeerAddress() {
      return this.channel.getPeerAddress();
   }

   public <A extends SocketAddress> A getPeerAddress(Class<A> type) {
      return this.channel.getPeerAddress(type);
   }

   public InetSocketAddress getSourceAddress() {
      return (InetSocketAddress)this.getPeerAddress(InetSocketAddress.class);
   }

   public InetSocketAddress getDestinationAddress() {
      return (InetSocketAddress)this.getLocalAddress(InetSocketAddress.class);
   }

   public synchronized R receive() throws IOException {
      ReferenceCountedPooled pooled = this.readData;
      if (this.readChannelDone && this.receiver == null) {
         if (pooled != null) {
            pooled.close();
            this.readData = null;
         }

         this.channel.getSourceChannel().suspendReads();
         this.channel.getSourceChannel().shutdownReads();
         return null;
      } else {
         this.partialRead = false;
         boolean requiresReinvoke = false;
         int reinvokeDataRemaining = 0;
         boolean hasData = false;
         if (pooled == null) {
            pooled = this.allocateReferenceCountedBuffer();
            if (pooled == null) {
               return null;
            }
         } else if (pooled.isFreed()) {
            if (!pooled.tryUnfree()) {
               pooled = this.allocateReferenceCountedBuffer();
               if (pooled == null) {
                  return null;
               }
            }

            pooled.getBuffer().clear();
         } else {
            hasData = pooled.getBuffer().hasRemaining();
            pooled.getBuffer().compact();
         }

         boolean forceFree = false;
         int read = false;

         PooledByteBuffer frameData;
         try {
            int read = this.channel.getSourceChannel().read(pooled.getBuffer());
            FrameHeaderData data;
            if (read == 0 && !hasData) {
               forceFree = true;
               data = null;
               return data;
            }

            if (read == -1 && !hasData) {
               forceFree = true;
               this.readChannelDone = true;
               this.lastDataRead();
               data = null;
               return data;
            }

            if (this.isLastFrameReceived() && this.frameDataRemaining == 0L) {
               forceFree = true;
               this.markReadsBroken(new ClosedChannelException());
            }

            pooled.getBuffer().flip();
            if (read == -1) {
               requiresReinvoke = true;
               reinvokeDataRemaining = pooled.getBuffer().remaining();
            }

            if (this.frameDataRemaining > 0L) {
               PooledByteBuffer frameData;
               if (this.frameDataRemaining >= (long)pooled.getBuffer().remaining()) {
                  this.frameDataRemaining -= (long)pooled.getBuffer().remaining();
                  if (this.receiver != null) {
                     frameData = pooled.createView();
                     this.receiver.dataReady((FrameHeaderData)null, frameData);
                  } else {
                     pooled.close();
                     this.readData = null;
                  }

                  if (this.frameDataRemaining == 0L) {
                     this.receiver = null;
                  }

                  data = null;
                  return data;
               }

               frameData = pooled.createView((int)this.frameDataRemaining);
               this.frameDataRemaining = 0L;
               if (this.receiver != null) {
                  this.receiver.dataReady((FrameHeaderData)null, frameData);
               } else {
                  frameData.close();
               }

               this.receiver = null;
               data = null;
               return data;
            }

            data = this.parseFrame(pooled.getBuffer());
            if (data != null) {
               if (data.getFrameLength() >= (long)pooled.getBuffer().remaining()) {
                  this.frameDataRemaining = data.getFrameLength() - (long)pooled.getBuffer().remaining();
                  frameData = pooled.createView();
                  pooled.getBuffer().position(pooled.getBuffer().limit());
               } else {
                  frameData = pooled.createView((int)data.getFrameLength());
               }

               AbstractFramedStreamSourceChannel<?, ?, ?> existing = data.getExistingChannel();
               if (existing != null) {
                  if (data.getFrameLength() > (long)frameData.getBuffer().remaining()) {
                     this.receiver = existing;
                  }

                  existing.dataReady(data, frameData);
                  if (this.isLastFrameReceived()) {
                     this.handleLastFrame(existing);
                  }

                  Object var20 = null;
                  return (AbstractFramedStreamSourceChannel)var20;
               }

               boolean moreData = data.getFrameLength() > (long)frameData.getBuffer().remaining();
               R newChannel = this.createChannel(data, frameData);
               if (newChannel != null) {
                  if (moreData) {
                     this.receiver = newChannel;
                  }

                  if (this.isLastFrameReceived()) {
                     this.handleLastFrame(newChannel);
                  }
               } else {
                  frameData.close();
               }

               AbstractFramedStreamSourceChannel var12 = newChannel;
               return var12;
            }

            this.partialRead = true;
            frameData = null;
         } catch (RuntimeException | Error | IOException var16) {
            this.markReadsBroken(var16);
            forceFree = true;
            throw var16;
         } finally {
            if (this.readData != null && (!pooled.getBuffer().hasRemaining() || forceFree)) {
               if (pooled.getBuffer().capacity() < 1024 || forceFree) {
                  this.readData = null;
               }

               pooled.close();
            }

            if (requiresReinvoke) {
               if (pooled != null && !pooled.isFreed() && pooled.getBuffer().remaining() == reinvokeDataRemaining) {
                  pooled.close();
                  this.readData = null;
                  UndertowLogger.REQUEST_IO_LOGGER.debugf("Partial message read before connection close %s", this);
               }

               this.channel.getSourceChannel().wakeupReads();
            }

         }

         return frameData;
      }
   }

   private void handleLastFrame(AbstractFramedStreamSourceChannel newChannel) {
      Set<AbstractFramedStreamSourceChannel<C, R, S>> receivers = new HashSet(this.getReceivers());
      Iterator var3 = receivers.iterator();

      while(var3.hasNext()) {
         AbstractFramedStreamSourceChannel<C, R, S> r = (AbstractFramedStreamSourceChannel)var3.next();
         if (r != newChannel) {
            r.markStreamBroken();
         }
      }

   }

   private ReferenceCountedPooled allocateReferenceCountedBuffer() {
      int expect;
      if (this.maxQueuedBuffers > 0) {
         do {
            expect = outstandingBuffersUpdater.get(this);
            if (expect == this.maxQueuedBuffers) {
               synchronized(this) {
                  expect = outstandingBuffersUpdater.get(this);
                  if (expect == this.maxQueuedBuffers) {
                     if (UndertowLogger.REQUEST_IO_LOGGER.isTraceEnabled()) {
                        UndertowLogger.REQUEST_IO_LOGGER.tracef("Suspending reads on %s due to too many outstanding buffers", this);
                     }

                     this.getIoThread().execute(new UpdateResumeState((Boolean)null, true, (Boolean)null));
                     return null;
                  }
               }
            }
         } while(!outstandingBuffersUpdater.compareAndSet(this, expect, expect + 1));
      }

      PooledByteBuffer buf = this.bufferPool.allocate();
      return this.readData = new ReferenceCountedPooled(buf, 1, this.maxQueuedBuffers > 0 ? this.freeNotifier : null);
   }

   protected void lastDataRead() {
   }

   protected abstract R createChannel(FrameHeaderData var1, PooledByteBuffer var2) throws IOException;

   protected abstract FrameHeaderData parseFrame(ByteBuffer var1) throws IOException;

   protected synchronized void recalculateHeldFrames() throws IOException {
      if (!this.heldFrames.isEmpty()) {
         this.framePriority.frameAdded((AbstractFramedStreamSinkChannel)null, this.pendingFrames, this.heldFrames);
         this.flushSenders();
      }

   }

   protected synchronized void flushSenders() {
      if (this.flushingSenders) {
         throw UndertowMessages.MESSAGES.recursiveCallToFlushingSenders();
      } else {
         this.flushingSenders = true;

         try {
            int toSend = 0;

            AbstractFramedStreamSinkChannel frame;
            while((frame = (AbstractFramedStreamSinkChannel)this.newFrames.poll()) != null) {
               frame.preWrite();
               if (this.framePriority.insertFrame(frame, this.pendingFrames)) {
                  if (!this.heldFrames.isEmpty()) {
                     this.framePriority.frameAdded(frame, this.pendingFrames, this.heldFrames);
                  }
               } else {
                  this.heldFrames.add(frame);
               }
            }

            boolean finalFrame = false;
            ListIterator<S> it = this.pendingFrames.listIterator();

            while(it.hasNext()) {
               S sender = (AbstractFramedStreamSinkChannel)it.next();
               if (!sender.isReadyForFlush()) {
                  break;
               }

               ++toSend;
               if (sender.isLastFrame()) {
                  finalFrame = true;
               }
            }

            if (toSend != 0) {
               ByteBuffer[] data = new ByteBuffer[toSend * 3];
               int j = 0;
               it = this.pendingFrames.listIterator();

               try {
                  while(j < toSend) {
                     S next = (AbstractFramedStreamSinkChannel)it.next();
                     SendFrameHeader frameHeader = next.getFrameHeader();
                     PooledByteBuffer frameHeaderByteBuffer = frameHeader.getByteBuffer();
                     ByteBuffer frameTrailerBuffer = frameHeader.getTrailer();
                     data[j * 3] = frameHeaderByteBuffer != null ? frameHeaderByteBuffer.getBuffer() : Buffers.EMPTY_BYTE_BUFFER;
                     data[j * 3 + 1] = next.getBuffer() == null ? Buffers.EMPTY_BYTE_BUFFER : next.getBuffer();
                     data[j * 3 + 2] = frameTrailerBuffer != null ? frameTrailerBuffer : Buffers.EMPTY_BYTE_BUFFER;
                     ++j;
                  }

                  long toWrite = Buffers.remaining(data);

                  long res;
                  do {
                     res = this.channel.getSinkChannel().write(data);
                     toWrite -= res;
                  } while(res > 0L && toWrite > 0L);

                  for(int max = toSend; max > 0; --max) {
                     S sinkChannel = (AbstractFramedStreamSinkChannel)this.pendingFrames.get(0);
                     PooledByteBuffer frameHeaderByteBuffer = sinkChannel.getFrameHeader().getByteBuffer();
                     ByteBuffer frameTrailerBuffer = sinkChannel.getFrameHeader().getTrailer();
                     if (frameHeaderByteBuffer != null && frameHeaderByteBuffer.getBuffer().hasRemaining() || sinkChannel.getBuffer() != null && sinkChannel.getBuffer().hasRemaining() || frameTrailerBuffer != null && frameTrailerBuffer.hasRemaining()) {
                        break;
                     }

                     sinkChannel.flushComplete();
                     this.pendingFrames.remove(sinkChannel);
                  }

                  if (this.pendingFrames.isEmpty() && this.channel.getSinkChannel().flush()) {
                     this.channel.getSinkChannel().suspendWrites();
                  } else {
                     this.channel.getSinkChannel().resumeWrites();
                  }

                  if (this.pendingFrames.isEmpty() && finalFrame) {
                     this.channel.getSinkChannel().shutdownWrites();
                     if (!this.channel.getSinkChannel().flush()) {
                        this.channel.getSinkChannel().setWriteListener(ChannelListeners.flushingChannelListener((ChannelListener)null, (ChannelExceptionHandler)null));
                        this.channel.getSinkChannel().resumeWrites();
                     }

                     return;
                  }

                  if (this.pendingFrames.size() > this.queuedFrameHighWaterMark) {
                     (new UpdateResumeState((Boolean)null, (Boolean)null, true)).run();
                  } else if (this.receivesSuspendedTooManyQueuedMessages && this.pendingFrames.size() < this.queuedFrameLowWaterMark) {
                     (new UpdateResumeState((Boolean)null, (Boolean)null, false)).run();
                     return;
                  }

                  return;
               } catch (RuntimeException | Error | IOException var20) {
                  IoUtils.safeClose((Closeable)this.channel);
                  this.markWritesBroken(var20);
                  return;
               }
            }

            try {
               if (this.channel.getSinkChannel().flush()) {
                  this.channel.getSinkChannel().suspendWrites();
               }
            } catch (Throwable var19) {
               IoUtils.safeClose((Closeable)this.channel);
               this.markWritesBroken(var19);
            }
         } finally {
            this.flushingSenders = false;
            if (!this.newFrames.isEmpty()) {
               this.runInIoThread(new Runnable() {
                  public void run() {
                     AbstractFramedChannel.this.flushSenders();
                  }
               });
            }

         }

      }
   }

   void awaitWritable() throws IOException {
      this.channel.getSinkChannel().awaitWritable();
   }

   void awaitWritable(long time, TimeUnit unit) throws IOException {
      this.channel.getSinkChannel().awaitWritable(time, unit);
   }

   protected void queueFrame(S channel) throws IOException {
      assert !this.newFrames.contains(channel);

      if (!this.isWritesBroken() && this.channel.getSinkChannel().isOpen() && !channel.isBroken() && channel.isOpen()) {
         this.newFrames.add(channel);
         if (!this.requireExplicitFlush || channel.isBufferFull()) {
            this.flush();
         }

      } else {
         IoUtils.safeClose((Closeable)channel);
         throw UndertowMessages.MESSAGES.channelIsClosed();
      }
   }

   public void flush() {
      if (!this.flushingSenders) {
         if (this.channel.getIoThread() == Thread.currentThread()) {
            this.flushSenders();
         } else {
            this.runInIoThread(new Runnable() {
               public void run() {
                  AbstractFramedChannel.this.flushSenders();
               }
            });
         }
      }

   }

   protected abstract boolean isLastFrameReceived();

   protected abstract boolean isLastFrameSent();

   protected abstract void handleBrokenSourceChannel(Throwable var1);

   protected abstract void handleBrokenSinkChannel(Throwable var1);

   public ChannelListener.Setter<C> getReceiveSetter() {
      return this.receiveSetter;
   }

   public synchronized void suspendReceives() {
      this.receivesSuspendedByUser = true;
      this.getIoThread().execute(new UpdateResumeState(true, (Boolean)null, (Boolean)null));
   }

   public synchronized void resumeReceives() {
      this.receivesSuspendedByUser = false;
      this.getIoThread().execute(new UpdateResumeState(false, (Boolean)null, (Boolean)null));
   }

   private void doResume() {
      ReferenceCountedPooled localReadData = this.readData;
      if (localReadData != null && !localReadData.isFreed()) {
         this.channel.getSourceChannel().wakeupReads();
      } else {
         this.channel.getSourceChannel().resumeReads();
      }

   }

   public boolean isReceivesResumed() {
      return !this.receivesSuspendedByUser;
   }

   public void close() throws IOException {
      if (UndertowLogger.REQUEST_IO_LOGGER.isTraceEnabled()) {
         UndertowLogger.REQUEST_IO_LOGGER.tracef(new ClosedChannelException(), "Channel %s is being closed", this);
      }

      IoUtils.safeClose((Closeable)this.channel);
      ReferenceCountedPooled localReadData = this.readData;
      if (localReadData != null) {
         localReadData.close();
         this.readData = null;
      }

      this.closeSubChannels();
   }

   public ChannelListener.Setter<? extends AbstractFramedChannel> getCloseSetter() {
      return this.closeSetter;
   }

   protected void markReadsBroken(Throwable cause) {
      if (readsBrokenUpdater.compareAndSet(this, 0, 1)) {
         if (UndertowLogger.REQUEST_IO_LOGGER.isDebugEnabled()) {
            UndertowLogger.REQUEST_IO_LOGGER.debugf(new ClosedChannelException(), "Marking reads broken on channel %s", this);
         }

         if (this.receiver != null) {
            this.receiver.markStreamBroken();
         }

         Iterator var2 = (new ArrayList(this.getReceivers())).iterator();

         while(var2.hasNext()) {
            AbstractFramedStreamSourceChannel<C, R, S> r = (AbstractFramedStreamSourceChannel)var2.next();
            r.markStreamBroken();
         }

         this.handleBrokenSourceChannel(cause);
         IoUtils.safeClose((Closeable)this.channel.getSourceChannel());
         this.closeSubChannels();
      }

   }

   protected abstract void closeSubChannels();

   protected void markWritesBroken(Throwable cause) {
      if (writesBrokenUpdater.compareAndSet(this, 0, 1)) {
         if (UndertowLogger.REQUEST_IO_LOGGER.isDebugEnabled()) {
            UndertowLogger.REQUEST_IO_LOGGER.debugf(new ClosedChannelException(), "Marking writes broken on channel %s", this);
         }

         this.handleBrokenSinkChannel(cause);
         IoUtils.safeClose((Closeable)this.channel.getSinkChannel());
         synchronized(this) {
            Iterator var3 = this.pendingFrames.iterator();

            AbstractFramedStreamSinkChannel channel;
            while(var3.hasNext()) {
               channel = (AbstractFramedStreamSinkChannel)var3.next();
               channel.markBroken();
            }

            this.pendingFrames.clear();
            var3 = this.newFrames.iterator();

            while(var3.hasNext()) {
               channel = (AbstractFramedStreamSinkChannel)var3.next();
               channel.markBroken();
            }

            this.newFrames.clear();
            var3 = this.heldFrames.iterator();

            while(var3.hasNext()) {
               channel = (AbstractFramedStreamSinkChannel)var3.next();
               channel.markBroken();
            }

            this.heldFrames.clear();
         }
      }

   }

   protected boolean isWritesBroken() {
      return writesBrokenUpdater.get(this) != 0;
   }

   protected boolean isReadsBroken() {
      return readsBrokenUpdater.get(this) != 0;
   }

   void resumeWrites() {
      this.channel.getSinkChannel().resumeWrites();
   }

   void suspendWrites() {
      this.channel.getSinkChannel().suspendWrites();
   }

   void wakeupWrites() {
      this.channel.getSinkChannel().wakeupWrites();
   }

   StreamSourceChannel getSourceChannel() {
      return this.channel.getSourceChannel();
   }

   void notifyFrameReadComplete(AbstractFramedStreamSourceChannel<C, R, S> channel) {
   }

   private boolean isReadsSuspended() {
      return this.receivesSuspendedByUser || this.receivesSuspendedTooManyBuffers || this.receivesSuspendedTooManyQueuedMessages;
   }

   protected abstract Collection<AbstractFramedStreamSourceChannel<C, R, S>> getReceivers();

   public void setIdleTimeout(long timeout) {
      this.idleTimeoutConduit.setIdleTimeout(timeout);
   }

   public long getIdleTimeout() {
      return this.idleTimeoutConduit.getIdleTimeout();
   }

   protected FramePriority<C, R, S> getFramePriority() {
      return this.framePriority;
   }

   public void addCloseTask(ChannelListener<C> task) {
      this.closeTasks.add(task);
   }

   public String toString() {
      StringBuilder stringBuilder = new StringBuilder(150);
      stringBuilder.append(this.getClass().getSimpleName()).append(" peer ").append(this.channel.getPeerAddress()).append(" local ").append(this.channel.getLocalAddress()).append("[ ");
      synchronized(this) {
         stringBuilder.append(this.receiver == null ? "No Receiver" : this.receiver.toString()).append(" ").append(this.pendingFrames.toString()).append(" -- ").append(this.heldFrames.toString()).append(" -- ").append(this.newFrames.toString());
      }

      return stringBuilder.toString();
   }

   protected StreamConnection getUnderlyingConnection() {
      return this.channel;
   }

   protected ChannelExceptionHandler<SuspendableWriteChannel> writeExceptionHandler() {
      return new ChannelExceptionHandler<SuspendableWriteChannel>() {
         public void handleException(SuspendableWriteChannel channel, IOException exception) {
            AbstractFramedChannel.this.markWritesBroken(exception);
         }
      };
   }

   public boolean isRequireExplicitFlush() {
      return this.requireExplicitFlush;
   }

   public void setRequireExplicitFlush(boolean requireExplicitFlush) {
      this.requireExplicitFlush = requireExplicitFlush;
   }

   protected OptionMap getSettings() {
      return this.settings;
   }

   private class UpdateResumeState implements Runnable {
      private final Boolean user;
      private final Boolean buffers;
      private final Boolean frames;

      private UpdateResumeState(Boolean user, Boolean buffers, Boolean frames) {
         this.user = user;
         this.buffers = buffers;
         this.frames = frames;
      }

      public void run() {
         if (this.user != null) {
            AbstractFramedChannel.this.receivesSuspendedByUser = this.user;
         }

         if (this.buffers != null) {
            AbstractFramedChannel.this.receivesSuspendedTooManyBuffers = this.buffers;
         }

         if (this.frames != null) {
            AbstractFramedChannel.this.receivesSuspendedTooManyQueuedMessages = this.frames;
         }

         if (!AbstractFramedChannel.this.receivesSuspendedByUser && !AbstractFramedChannel.this.receivesSuspendedTooManyQueuedMessages && !AbstractFramedChannel.this.receivesSuspendedTooManyBuffers) {
            AbstractFramedChannel.this.doResume();
         } else {
            AbstractFramedChannel.this.channel.getSourceChannel().suspendReads();
         }

      }

      // $FF: synthetic method
      UpdateResumeState(Boolean x1, Boolean x2, Boolean x3, Object x4) {
         this(x1, x2, x3);
      }
   }

   private class FrameCloseListener implements ChannelListener<CloseableChannel> {
      private boolean sinkClosed;
      private boolean sourceClosed;

      private FrameCloseListener() {
      }

      public void handleEvent(final CloseableChannel c) {
         if (Thread.currentThread() != c.getIoThread() && !c.getWorker().isShutdown()) {
            AbstractFramedChannel.this.runInIoThread(new Runnable() {
               public void run() {
                  ChannelListeners.invokeChannelListener(c, FrameCloseListener.this);
               }
            });
         } else {
            if (c instanceof StreamSinkChannel) {
               this.sinkClosed = true;
            } else if (c instanceof StreamSourceChannel) {
               this.sourceClosed = true;
            }

            final ReferenceCountedPooled localReadData = AbstractFramedChannel.this.readData;
            if (this.sourceClosed && this.sinkClosed) {
               if (localReadData != null && !localReadData.isFreed()) {
                  AbstractFramedChannel.this.runInIoThread(new Runnable() {
                     public void run() {
                        while(true) {
                           if (localReadData != null && !localReadData.isFreed()) {
                              int rem = localReadData.getBuffer().remaining();
                              ChannelListener listener = AbstractFramedChannel.this.receiveSetter.get();
                              if (listener == null) {
                                 listener = AbstractFramedChannel.DRAIN_LISTENER;
                              }

                              ChannelListeners.invokeChannelListener(AbstractFramedChannel.this, listener);
                              if (AbstractFramedChannel.this.isOpen() && (localReadData == null || rem != localReadData.getBuffer().remaining())) {
                                 continue;
                              }
                           }

                           FrameCloseListener.this.handleEvent(c);
                           return;
                        }
                     }
                  });
               } else {
                  AbstractFramedStreamSourceChannel receiver = AbstractFramedChannel.this.receiver;
                  boolean var47 = false;

                  try {
                     var47 = true;
                     if (receiver != null && receiver.isOpen() && receiver.isReadResumed()) {
                        ChannelListeners.invokeChannelListener(receiver, ((ChannelListener.SimpleSetter)receiver.getReadSetter()).get());
                     }

                     ArrayList pendingFrames;
                     ArrayList newFrames;
                     ArrayList heldFrames;
                     ArrayList receivers;
                     synchronized(AbstractFramedChannel.this) {
                        pendingFrames = new ArrayList(AbstractFramedChannel.this.pendingFrames);
                        newFrames = new ArrayList(AbstractFramedChannel.this.newFrames);
                        heldFrames = new ArrayList(AbstractFramedChannel.this.heldFrames);
                        receivers = new ArrayList(AbstractFramedChannel.this.getReceivers());
                     }

                     Iterator var8 = pendingFrames.iterator();

                     AbstractFramedStreamSinkChannel channel;
                     while(var8.hasNext()) {
                        channel = (AbstractFramedStreamSinkChannel)var8.next();
                        channel.markBroken();
                     }

                     var8 = newFrames.iterator();

                     while(var8.hasNext()) {
                        channel = (AbstractFramedStreamSinkChannel)var8.next();
                        channel.markBroken();
                     }

                     var8 = heldFrames.iterator();

                     while(true) {
                        if (!var8.hasNext()) {
                           var8 = receivers.iterator();

                           while(var8.hasNext()) {
                              AbstractFramedStreamSourceChannel<C, R, S> r = (AbstractFramedStreamSourceChannel)var8.next();
                              IoUtils.safeClose((Closeable)r);
                           }

                           var47 = false;
                           break;
                        }

                        channel = (AbstractFramedStreamSinkChannel)var8.next();
                        channel.markBroken();
                     }
                  } finally {
                     if (var47) {
                        boolean var38 = false;

                        try {
                           var38 = true;
                           Iterator var15 = AbstractFramedChannel.this.closeTasks.iterator();

                           while(var15.hasNext()) {
                              ChannelListener<C> taskx = (ChannelListener)var15.next();
                              ChannelListeners.invokeChannelListener(AbstractFramedChannel.this, taskx);
                           }

                           var38 = false;
                        } finally {
                           if (var38) {
                              synchronized(AbstractFramedChannel.this) {
                                 AbstractFramedChannel.this.closeSubChannels();
                                 if (localReadData != null) {
                                    localReadData.close();
                                    AbstractFramedChannel.this.readData = null;
                                 }
                              }

                              ChannelListeners.invokeChannelListener(AbstractFramedChannel.this, AbstractFramedChannel.this.closeSetter.get());
                           }
                        }

                        synchronized(AbstractFramedChannel.this) {
                           AbstractFramedChannel.this.closeSubChannels();
                           if (localReadData != null) {
                              localReadData.close();
                              AbstractFramedChannel.this.readData = null;
                           }
                        }

                        ChannelListeners.invokeChannelListener(AbstractFramedChannel.this, AbstractFramedChannel.this.closeSetter.get());
                     }
                  }

                  boolean var29 = false;

                  try {
                     var29 = true;
                     Iterator var56 = AbstractFramedChannel.this.closeTasks.iterator();

                     while(true) {
                        if (!var56.hasNext()) {
                           var29 = false;
                           break;
                        }

                        ChannelListener<C> task = (ChannelListener)var56.next();
                        ChannelListeners.invokeChannelListener(AbstractFramedChannel.this, task);
                     }
                  } finally {
                     if (var29) {
                        synchronized(AbstractFramedChannel.this) {
                           AbstractFramedChannel.this.closeSubChannels();
                           if (localReadData != null) {
                              localReadData.close();
                              AbstractFramedChannel.this.readData = null;
                           }
                        }

                        ChannelListeners.invokeChannelListener(AbstractFramedChannel.this, AbstractFramedChannel.this.closeSetter.get());
                     }
                  }

                  synchronized(AbstractFramedChannel.this) {
                     AbstractFramedChannel.this.closeSubChannels();
                     if (localReadData != null) {
                        localReadData.close();
                        AbstractFramedChannel.this.readData = null;
                     }
                  }

                  ChannelListeners.invokeChannelListener(AbstractFramedChannel.this, AbstractFramedChannel.this.closeSetter.get());
               }
            }
         }
      }

      // $FF: synthetic method
      FrameCloseListener(Object x1) {
         this();
      }
   }

   private class FrameWriteListener implements ChannelListener<StreamSinkChannel> {
      private FrameWriteListener() {
      }

      public void handleEvent(StreamSinkChannel channel) {
         AbstractFramedChannel.this.flushSenders();
      }

      // $FF: synthetic method
      FrameWriteListener(Object x1) {
         this();
      }
   }

   private final class FrameReadListener implements ChannelListener<StreamSourceChannel> {
      private FrameReadListener() {
      }

      public void handleEvent(final StreamSourceChannel channel) {
         Runnable runnable;
         while((runnable = (Runnable)AbstractFramedChannel.this.taskRunQueue.poll()) != null) {
            runnable.run();
         }

         R receiver = AbstractFramedChannel.this.receiver;
         if ((AbstractFramedChannel.this.readChannelDone || AbstractFramedChannel.this.isReadsSuspended()) && receiver == null) {
            channel.suspendReads();
         } else {
            ChannelListener listener = AbstractFramedChannel.this.receiveSetter.get();
            if (listener == null) {
               listener = AbstractFramedChannel.DRAIN_LISTENER;
            }

            UndertowLogger.REQUEST_IO_LOGGER.tracef("Invoking receive listener: %s - receiver: %s", listener, receiver);
            ChannelListeners.invokeChannelListener(AbstractFramedChannel.this, listener);
            boolean partialRead;
            synchronized(AbstractFramedChannel.this) {
               partialRead = AbstractFramedChannel.this.partialRead;
            }

            ReferenceCountedPooled localReadData = AbstractFramedChannel.this.readData;
            if (localReadData != null && !localReadData.isFreed() && channel.isOpen() && !partialRead) {
               try {
                  AbstractFramedChannel.this.runInIoThread(new Runnable() {
                     public void run() {
                        ChannelListeners.invokeChannelListener(channel, FrameReadListener.this);
                     }
                  });
               } catch (RejectedExecutionException var9) {
                  IoUtils.safeClose((Closeable)AbstractFramedChannel.this);
               }
            }

            synchronized(AbstractFramedChannel.this) {
               AbstractFramedChannel.this.partialRead = false;
            }
         }
      }

      // $FF: synthetic method
      FrameReadListener(Object x1) {
         this();
      }
   }
}
