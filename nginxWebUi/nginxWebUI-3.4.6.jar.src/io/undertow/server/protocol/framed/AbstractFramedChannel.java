/*      */ package io.undertow.server.protocol.framed;
/*      */ 
/*      */ import io.undertow.UndertowLogger;
/*      */ import io.undertow.UndertowMessages;
/*      */ import io.undertow.UndertowOptions;
/*      */ import io.undertow.conduits.IdleTimeoutConduit;
/*      */ import io.undertow.connector.ByteBufferPool;
/*      */ import io.undertow.connector.PooledByteBuffer;
/*      */ import io.undertow.util.ReferenceCountedPooled;
/*      */ import java.io.Closeable;
/*      */ import java.io.IOException;
/*      */ import java.net.InetSocketAddress;
/*      */ import java.net.SocketAddress;
/*      */ import java.nio.Buffer;
/*      */ import java.nio.ByteBuffer;
/*      */ import java.nio.channels.Channel;
/*      */ import java.nio.channels.ClosedChannelException;
/*      */ import java.util.ArrayDeque;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Collection;
/*      */ import java.util.Deque;
/*      */ import java.util.HashSet;
/*      */ import java.util.LinkedList;
/*      */ import java.util.List;
/*      */ import java.util.ListIterator;
/*      */ import java.util.Set;
/*      */ import java.util.concurrent.CopyOnWriteArrayList;
/*      */ import java.util.concurrent.LinkedBlockingDeque;
/*      */ import java.util.concurrent.RejectedExecutionException;
/*      */ import java.util.concurrent.TimeUnit;
/*      */ import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
/*      */ import org.xnio.Buffers;
/*      */ import org.xnio.ChannelExceptionHandler;
/*      */ import org.xnio.ChannelListener;
/*      */ import org.xnio.ChannelListeners;
/*      */ import org.xnio.IoUtils;
/*      */ import org.xnio.Option;
/*      */ import org.xnio.OptionMap;
/*      */ import org.xnio.StreamConnection;
/*      */ import org.xnio.XnioIoThread;
/*      */ import org.xnio.XnioWorker;
/*      */ import org.xnio.channels.CloseableChannel;
/*      */ import org.xnio.channels.ConnectedChannel;
/*      */ import org.xnio.channels.StreamSinkChannel;
/*      */ import org.xnio.channels.StreamSourceChannel;
/*      */ import org.xnio.channels.SuspendableWriteChannel;
/*      */ import org.xnio.conduits.StreamSinkConduit;
/*      */ import org.xnio.conduits.StreamSourceConduit;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public abstract class AbstractFramedChannel<C extends AbstractFramedChannel<C, R, S>, R extends AbstractFramedStreamSourceChannel<C, R, S>, S extends AbstractFramedStreamSinkChannel<C, R, S>>
/*      */   implements ConnectedChannel
/*      */ {
/*      */   private final int maxQueuedBuffers;
/*      */   private final StreamConnection channel;
/*      */   private final IdleTimeoutConduit idleTimeoutConduit;
/*      */   private final ChannelListener.SimpleSetter<C> closeSetter;
/*      */   private final ChannelListener.SimpleSetter<C> receiveSetter;
/*      */   private final ByteBufferPool bufferPool;
/*      */   private final FramePriority<C, R, S> framePriority;
/*   99 */   private final List<S> pendingFrames = new LinkedList<>();
/*      */ 
/*      */ 
/*      */   
/*  103 */   private final Deque<S> heldFrames = new ArrayDeque<>();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  109 */   private final Deque<S> newFrames = new LinkedBlockingDeque<>();
/*      */   
/*      */   private volatile long frameDataRemaining;
/*      */   
/*      */   private volatile R receiver;
/*      */   
/*      */   private volatile boolean receivesSuspendedByUser = true;
/*      */   
/*      */   private volatile boolean receivesSuspendedTooManyQueuedMessages = false;
/*      */   private volatile boolean receivesSuspendedTooManyBuffers = false;
/*  119 */   private volatile int readsBroken = 0;
/*      */ 
/*      */   
/*  122 */   private volatile int writesBroken = 0;
/*      */ 
/*      */   
/*  125 */   private static final AtomicIntegerFieldUpdater<AbstractFramedChannel> readsBrokenUpdater = AtomicIntegerFieldUpdater.newUpdater(AbstractFramedChannel.class, "readsBroken");
/*  126 */   private static final AtomicIntegerFieldUpdater<AbstractFramedChannel> writesBrokenUpdater = AtomicIntegerFieldUpdater.newUpdater(AbstractFramedChannel.class, "writesBroken");
/*      */   
/*  128 */   private volatile ReferenceCountedPooled readData = null;
/*  129 */   private final List<ChannelListener<C>> closeTasks = new CopyOnWriteArrayList<>();
/*      */   
/*      */   private volatile boolean flushingSenders = false;
/*      */   
/*      */   private boolean partialRead = false;
/*      */   
/*      */   private volatile int outstandingBuffers;
/*  136 */   private static final AtomicIntegerFieldUpdater<AbstractFramedChannel> outstandingBuffersUpdater = AtomicIntegerFieldUpdater.newUpdater(AbstractFramedChannel.class, "outstandingBuffers");
/*      */   
/*  138 */   private final LinkedBlockingDeque<Runnable> taskRunQueue = new LinkedBlockingDeque<>();
/*  139 */   private final Runnable taskRunQueueRunnable = new Runnable()
/*      */     {
/*      */       public void run() {
/*      */         Runnable runnable;
/*  143 */         while ((runnable = AbstractFramedChannel.this.taskRunQueue.poll()) != null) {
/*  144 */           runnable.run();
/*      */         }
/*      */       }
/*      */     };
/*      */ 
/*      */   
/*      */   private final OptionMap settings;
/*      */   
/*      */   private volatile boolean requireExplicitFlush = false;
/*      */   
/*      */   private volatile boolean readChannelDone = false;
/*      */   
/*      */   private final int queuedFrameHighWaterMark;
/*      */   private final int queuedFrameLowWaterMark;
/*      */   
/*  159 */   private final ReferenceCountedPooled.FreeNotifier freeNotifier = new ReferenceCountedPooled.FreeNotifier()
/*      */     {
/*      */       public void freed() {
/*  162 */         int res = AbstractFramedChannel.outstandingBuffersUpdater.decrementAndGet(AbstractFramedChannel.this);
/*  163 */         if (!AbstractFramedChannel.this.receivesSuspendedByUser && res == AbstractFramedChannel.this.maxQueuedBuffers - 1)
/*      */         {
/*      */           
/*  166 */           AbstractFramedChannel.this.getIoThread().execute(new Runnable()
/*      */               {
/*      */                 public void run() {
/*  169 */                   synchronized (AbstractFramedChannel.this) {
/*  170 */                     if (AbstractFramedChannel.outstandingBuffersUpdater.get(AbstractFramedChannel.this) < AbstractFramedChannel.this.maxQueuedBuffers) {
/*  171 */                       if (UndertowLogger.REQUEST_IO_LOGGER.isTraceEnabled()) {
/*  172 */                         UndertowLogger.REQUEST_IO_LOGGER.tracef("Resuming reads on %s as buffers have been consumed", AbstractFramedChannel.this);
/*      */                       }
/*  174 */                       (new AbstractFramedChannel.UpdateResumeState(null, Boolean.valueOf(false), null)).run();
/*      */                     } 
/*      */                   } 
/*      */                 }
/*      */               });
/*      */         }
/*      */       }
/*      */     };
/*      */   
/*  183 */   private static final ChannelListener<AbstractFramedChannel> DRAIN_LISTENER = new ChannelListener<AbstractFramedChannel>()
/*      */     {
/*      */       public void handleEvent(AbstractFramedChannel channel) {
/*      */         try {
/*  187 */           AbstractFramedStreamSourceChannel stream = (AbstractFramedStreamSourceChannel)channel.receive();
/*  188 */           if (stream != null) {
/*  189 */             UndertowLogger.REQUEST_IO_LOGGER.debugf("Draining channel %s as no receive listener has been set", stream);
/*  190 */             stream.getReadSetter().set(ChannelListeners.drainListener(Long.MAX_VALUE, null, null));
/*  191 */             stream.wakeupReads();
/*      */           } 
/*  193 */         } catch (IOException|RuntimeException|Error e) {
/*  194 */           IoUtils.safeClose((Closeable)channel);
/*      */         } 
/*      */       }
/*      */     };
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected AbstractFramedChannel(StreamConnection connectedStreamChannel, ByteBufferPool bufferPool, FramePriority<C, R, S> framePriority, PooledByteBuffer readData, OptionMap settings) {
/*  209 */     this.framePriority = framePriority;
/*  210 */     this.maxQueuedBuffers = settings.get(UndertowOptions.MAX_QUEUED_READ_BUFFERS, 10);
/*  211 */     this.settings = settings;
/*  212 */     if (readData != null) {
/*  213 */       if (readData.getBuffer().hasRemaining()) {
/*  214 */         this.readData = new ReferenceCountedPooled(readData, 1);
/*      */       } else {
/*  216 */         readData.close();
/*      */       } 
/*      */     }
/*  219 */     if (bufferPool == null) {
/*  220 */       throw UndertowMessages.MESSAGES.argumentCannotBeNull("bufferPool");
/*      */     }
/*  222 */     if (connectedStreamChannel == null) {
/*  223 */       throw UndertowMessages.MESSAGES.argumentCannotBeNull("connectedStreamChannel");
/*      */     }
/*  225 */     IdleTimeoutConduit idle = createIdleTimeoutChannel(connectedStreamChannel);
/*  226 */     connectedStreamChannel.getSourceChannel().setConduit((StreamSourceConduit)idle);
/*  227 */     connectedStreamChannel.getSinkChannel().setConduit((StreamSinkConduit)idle);
/*  228 */     this.idleTimeoutConduit = idle;
/*  229 */     this.channel = connectedStreamChannel;
/*  230 */     this.bufferPool = bufferPool;
/*      */     
/*  232 */     this.closeSetter = new ChannelListener.SimpleSetter();
/*  233 */     this.receiveSetter = new ChannelListener.SimpleSetter();
/*  234 */     this.channel.getSourceChannel().getReadSetter().set(null);
/*  235 */     this.channel.getSourceChannel().suspendReads();
/*      */     
/*  237 */     this.channel.getSourceChannel().getReadSetter().set(new FrameReadListener());
/*  238 */     connectedStreamChannel.getSinkChannel().getWriteSetter().set(new FrameWriteListener());
/*  239 */     FrameCloseListener closeListener = new FrameCloseListener();
/*  240 */     connectedStreamChannel.getSinkChannel().getCloseSetter().set(closeListener);
/*  241 */     connectedStreamChannel.getSourceChannel().getCloseSetter().set(closeListener);
/*  242 */     this.queuedFrameHighWaterMark = settings.get(UndertowOptions.QUEUED_FRAMES_HIGH_WATER_MARK, 50);
/*  243 */     this.queuedFrameLowWaterMark = settings.get(UndertowOptions.QUEUED_FRAMES_LOW_WATER_MARK, 10);
/*      */   }
/*      */   
/*      */   protected IdleTimeoutConduit createIdleTimeoutChannel(StreamConnection connectedStreamChannel) {
/*  247 */     return new IdleTimeoutConduit(connectedStreamChannel);
/*      */   }
/*      */   
/*      */   void runInIoThread(Runnable task) {
/*  251 */     this.taskRunQueue.add(task);
/*      */     try {
/*  253 */       getIoThread().execute(this.taskRunQueueRunnable);
/*  254 */     } catch (RejectedExecutionException e) {
/*      */       
/*  256 */       ShutdownFallbackExecutor.execute(this.taskRunQueueRunnable);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ByteBufferPool getBufferPool() {
/*  266 */     return this.bufferPool;
/*      */   }
/*      */ 
/*      */   
/*      */   public SocketAddress getLocalAddress() {
/*  271 */     return this.channel.getLocalAddress();
/*      */   }
/*      */ 
/*      */   
/*      */   public <A extends SocketAddress> A getLocalAddress(Class<A> type) {
/*  276 */     return (A)this.channel.getLocalAddress(type);
/*      */   }
/*      */ 
/*      */   
/*      */   public XnioWorker getWorker() {
/*  281 */     return this.channel.getWorker();
/*      */   }
/*      */ 
/*      */   
/*      */   public XnioIoThread getIoThread() {
/*  286 */     return this.channel.getIoThread();
/*      */   }
/*      */ 
/*      */   
/*      */   public boolean supportsOption(Option<?> option) {
/*  291 */     return this.channel.supportsOption(option);
/*      */   }
/*      */ 
/*      */   
/*      */   public <T> T getOption(Option<T> option) throws IOException {
/*  296 */     return (T)this.channel.getOption(option);
/*      */   }
/*      */ 
/*      */   
/*      */   public <T> T setOption(Option<T> option, T value) throws IOException {
/*  301 */     return (T)this.channel.setOption(option, value);
/*      */   }
/*      */ 
/*      */   
/*      */   public boolean isOpen() {
/*  306 */     return this.channel.isOpen();
/*      */   }
/*      */ 
/*      */   
/*      */   public SocketAddress getPeerAddress() {
/*  311 */     return this.channel.getPeerAddress();
/*      */   }
/*      */ 
/*      */   
/*      */   public <A extends SocketAddress> A getPeerAddress(Class<A> type) {
/*  316 */     return (A)this.channel.getPeerAddress(type);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public InetSocketAddress getSourceAddress() {
/*  325 */     return getPeerAddress(InetSocketAddress.class);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public InetSocketAddress getDestinationAddress() {
/*  334 */     return getLocalAddress(InetSocketAddress.class);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized R receive() throws IOException {
/*  346 */     ReferenceCountedPooled pooled = this.readData;
/*  347 */     if (this.readChannelDone && this.receiver == null) {
/*      */ 
/*      */ 
/*      */       
/*  351 */       if (pooled != null) {
/*  352 */         pooled.close();
/*  353 */         this.readData = null;
/*      */       } 
/*  355 */       this.channel.getSourceChannel().suspendReads();
/*  356 */       this.channel.getSourceChannel().shutdownReads();
/*  357 */       return null;
/*      */     } 
/*  359 */     this.partialRead = false;
/*  360 */     boolean requiresReinvoke = false;
/*  361 */     int reinvokeDataRemaining = 0;
/*  362 */     boolean hasData = false;
/*  363 */     if (pooled == null) {
/*  364 */       pooled = allocateReferenceCountedBuffer();
/*  365 */       if (pooled == null) {
/*  366 */         return null;
/*      */       }
/*  368 */     } else if (pooled.isFreed()) {
/*      */       
/*  370 */       if (!pooled.tryUnfree()) {
/*  371 */         pooled = allocateReferenceCountedBuffer();
/*  372 */         if (pooled == null) {
/*  373 */           return null;
/*      */         }
/*      */       } 
/*  376 */       pooled.getBuffer().clear();
/*      */     } else {
/*  378 */       hasData = pooled.getBuffer().hasRemaining();
/*  379 */       pooled.getBuffer().compact();
/*      */     } 
/*  381 */     boolean forceFree = false;
/*  382 */     int read = 0;
/*      */     try {
/*  384 */       read = this.channel.getSourceChannel().read(pooled.getBuffer());
/*  385 */       if (read == 0 && !hasData) {
/*      */         
/*  387 */         forceFree = true;
/*  388 */         return null;
/*  389 */       }  if (read == -1 && !hasData) {
/*  390 */         forceFree = true;
/*  391 */         this.readChannelDone = true;
/*  392 */         lastDataRead();
/*  393 */         return null;
/*  394 */       }  if (isLastFrameReceived() && this.frameDataRemaining == 0L) {
/*      */         
/*  396 */         forceFree = true;
/*  397 */         markReadsBroken(new ClosedChannelException());
/*      */       } 
/*  399 */       pooled.getBuffer().flip();
/*  400 */       if (read == -1) {
/*  401 */         requiresReinvoke = true;
/*  402 */         reinvokeDataRemaining = pooled.getBuffer().remaining();
/*      */       } 
/*  404 */       if (this.frameDataRemaining > 0L) {
/*  405 */         if (this.frameDataRemaining >= pooled.getBuffer().remaining()) {
/*  406 */           this.frameDataRemaining -= pooled.getBuffer().remaining();
/*  407 */           if (this.receiver != null) {
/*      */ 
/*      */             
/*  410 */             PooledByteBuffer pooledByteBuffer = pooled.createView();
/*  411 */             this.receiver.dataReady(null, pooledByteBuffer);
/*      */           } else {
/*      */             
/*  414 */             pooled.close();
/*  415 */             this.readData = null;
/*      */           } 
/*  417 */           if (this.frameDataRemaining == 0L) {
/*  418 */             this.receiver = null;
/*      */           }
/*  420 */           return null;
/*      */         } 
/*  422 */         PooledByteBuffer frameData = pooled.createView((int)this.frameDataRemaining);
/*  423 */         this.frameDataRemaining = 0L;
/*  424 */         if (this.receiver != null) {
/*  425 */           this.receiver.dataReady(null, frameData);
/*      */         } else {
/*      */           
/*  428 */           frameData.close();
/*      */         } 
/*  430 */         this.receiver = null;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  439 */         frameData = null; return (R)frameData;
/*      */       } 
/*  441 */       FrameHeaderData data = parseFrame(pooled.getBuffer());
/*  442 */       if (data != null) {
/*      */         PooledByteBuffer frameData;
/*  444 */         if (data.getFrameLength() >= pooled.getBuffer().remaining()) {
/*  445 */           this.frameDataRemaining = data.getFrameLength() - pooled.getBuffer().remaining();
/*  446 */           frameData = pooled.createView();
/*  447 */           pooled.getBuffer().position(pooled.getBuffer().limit());
/*      */         } else {
/*  449 */           frameData = pooled.createView((int)data.getFrameLength());
/*      */         } 
/*  451 */         AbstractFramedStreamSourceChannel<?, ?, ?> existing = data.getExistingChannel();
/*  452 */         if (existing != null) {
/*  453 */           if (data.getFrameLength() > frameData.getBuffer().remaining()) {
/*  454 */             this.receiver = (R)existing;
/*      */           }
/*  456 */           existing.dataReady(data, frameData);
/*  457 */           if (isLastFrameReceived()) {
/*  458 */             handleLastFrame(existing);
/*      */           }
/*  460 */           return null;
/*      */         } 
/*  462 */         boolean moreData = (data.getFrameLength() > frameData.getBuffer().remaining());
/*  463 */         R newChannel = createChannel(data, frameData);
/*  464 */         if (newChannel != null) {
/*  465 */           if (moreData) {
/*  466 */             this.receiver = newChannel;
/*      */           }
/*      */           
/*  469 */           if (isLastFrameReceived()) {
/*  470 */             handleLastFrame((AbstractFramedStreamSourceChannel)newChannel);
/*      */           }
/*      */         } else {
/*  473 */           frameData.close();
/*      */         } 
/*  475 */         return newChannel;
/*      */       } 
/*      */ 
/*      */       
/*  479 */       this.partialRead = true;
/*      */       
/*  481 */       return null;
/*  482 */     } catch (IOException|RuntimeException|Error e) {
/*      */ 
/*      */       
/*  485 */       markReadsBroken(e);
/*  486 */       forceFree = true;
/*  487 */       throw e;
/*      */     }
/*      */     finally {
/*      */       
/*  491 */       if (this.readData != null && (
/*  492 */         !pooled.getBuffer().hasRemaining() || forceFree)) {
/*  493 */         if (pooled.getBuffer().capacity() < 1024 || forceFree)
/*      */         {
/*  495 */           this.readData = null;
/*      */         }
/*      */ 
/*      */         
/*  499 */         pooled.close();
/*      */       } 
/*      */ 
/*      */       
/*  503 */       if (requiresReinvoke) {
/*  504 */         if (pooled != null && !pooled.isFreed() && 
/*  505 */           pooled.getBuffer().remaining() == reinvokeDataRemaining) {
/*  506 */           pooled.close();
/*  507 */           this.readData = null;
/*  508 */           UndertowLogger.REQUEST_IO_LOGGER.debugf("Partial message read before connection close %s", this);
/*      */         } 
/*      */         
/*  511 */         this.channel.getSourceChannel().wakeupReads();
/*      */       } 
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void handleLastFrame(AbstractFramedStreamSourceChannel<C, R, S> newChannel) {
/*  522 */     Set<AbstractFramedStreamSourceChannel<C, R, S>> receivers = new HashSet<>(getReceivers());
/*  523 */     for (AbstractFramedStreamSourceChannel<C, R, S> r : receivers) {
/*  524 */       if (r != newChannel) {
/*  525 */         r.markStreamBroken();
/*      */       }
/*      */     } 
/*      */   }
/*      */   
/*      */   private ReferenceCountedPooled allocateReferenceCountedBuffer() {
/*  531 */     if (this.maxQueuedBuffers > 0) {
/*      */       int expect;
/*      */       do {
/*  534 */         expect = outstandingBuffersUpdater.get(this);
/*  535 */         if (expect != this.maxQueuedBuffers)
/*  536 */           continue;  synchronized (this) {
/*      */           
/*  538 */           expect = outstandingBuffersUpdater.get(this);
/*  539 */           if (expect == this.maxQueuedBuffers) {
/*  540 */             if (UndertowLogger.REQUEST_IO_LOGGER.isTraceEnabled()) {
/*  541 */               UndertowLogger.REQUEST_IO_LOGGER.tracef("Suspending reads on %s due to too many outstanding buffers", this);
/*      */             }
/*  543 */             getIoThread().execute(new UpdateResumeState(null, Boolean.valueOf(true), null));
/*  544 */             return null;
/*      */           }
/*      */         
/*      */         } 
/*  548 */       } while (!outstandingBuffersUpdater.compareAndSet(this, expect, expect + 1));
/*      */     } 
/*  550 */     PooledByteBuffer buf = this.bufferPool.allocate();
/*  551 */     return this.readData = new ReferenceCountedPooled(buf, 1, (this.maxQueuedBuffers > 0) ? this.freeNotifier : null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void lastDataRead() {}
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected synchronized void recalculateHeldFrames() throws IOException {
/*  580 */     if (!this.heldFrames.isEmpty()) {
/*  581 */       this.framePriority.frameAdded(null, this.pendingFrames, this.heldFrames);
/*  582 */       flushSenders();
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected synchronized void flushSenders() {
/*  594 */     if (this.flushingSenders) {
/*  595 */       throw UndertowMessages.MESSAGES.recursiveCallToFlushingSenders();
/*      */     }
/*  597 */     this.flushingSenders = true;
/*      */     try {
/*  599 */       int toSend = 0;
/*      */       AbstractFramedStreamSinkChannel abstractFramedStreamSinkChannel;
/*  601 */       while ((abstractFramedStreamSinkChannel = (AbstractFramedStreamSinkChannel)this.newFrames.poll()) != null) {
/*  602 */         abstractFramedStreamSinkChannel.preWrite();
/*  603 */         if (this.framePriority.insertFrame((S)abstractFramedStreamSinkChannel, this.pendingFrames)) {
/*  604 */           if (!this.heldFrames.isEmpty())
/*  605 */             this.framePriority.frameAdded((S)abstractFramedStreamSinkChannel, this.pendingFrames, this.heldFrames); 
/*      */           continue;
/*      */         } 
/*  608 */         this.heldFrames.add((S)abstractFramedStreamSinkChannel);
/*      */       } 
/*      */ 
/*      */       
/*  612 */       boolean finalFrame = false;
/*  613 */       ListIterator<S> it = this.pendingFrames.listIterator();
/*  614 */       while (it.hasNext()) {
/*  615 */         AbstractFramedStreamSinkChannel abstractFramedStreamSinkChannel1 = (AbstractFramedStreamSinkChannel)it.next();
/*  616 */         if (abstractFramedStreamSinkChannel1.isReadyForFlush()) {
/*  617 */           toSend++;
/*      */ 
/*      */ 
/*      */           
/*  621 */           if (abstractFramedStreamSinkChannel1.isLastFrame())
/*  622 */             finalFrame = true; 
/*      */         } 
/*      */       } 
/*  625 */       if (toSend == 0) {
/*      */         
/*      */         try {
/*  628 */           if (this.channel.getSinkChannel().flush()) {
/*  629 */             this.channel.getSinkChannel().suspendWrites();
/*      */           }
/*  631 */         } catch (Throwable e) {
/*  632 */           IoUtils.safeClose((Closeable)this.channel);
/*  633 */           markWritesBroken(e);
/*      */         } 
/*      */         return;
/*      */       } 
/*  637 */       ByteBuffer[] data = new ByteBuffer[toSend * 3];
/*  638 */       int j = 0;
/*  639 */       it = this.pendingFrames.listIterator(); try {
/*      */         long res;
/*  641 */         while (j < toSend) {
/*  642 */           AbstractFramedStreamSinkChannel abstractFramedStreamSinkChannel1 = (AbstractFramedStreamSinkChannel)it.next();
/*      */           
/*  644 */           SendFrameHeader frameHeader = abstractFramedStreamSinkChannel1.getFrameHeader();
/*  645 */           PooledByteBuffer frameHeaderByteBuffer = frameHeader.getByteBuffer();
/*  646 */           ByteBuffer frameTrailerBuffer = frameHeader.getTrailer();
/*  647 */           data[j * 3] = (frameHeaderByteBuffer != null) ? frameHeaderByteBuffer
/*  648 */             .getBuffer() : Buffers.EMPTY_BYTE_BUFFER;
/*      */           
/*  650 */           data[j * 3 + 1] = (abstractFramedStreamSinkChannel1.getBuffer() == null) ? Buffers.EMPTY_BYTE_BUFFER : abstractFramedStreamSinkChannel1.getBuffer();
/*  651 */           data[j * 3 + 2] = (frameTrailerBuffer != null) ? frameTrailerBuffer : Buffers.EMPTY_BYTE_BUFFER;
/*  652 */           j++;
/*      */         } 
/*  654 */         long toWrite = Buffers.remaining((Buffer[])data);
/*      */         
/*      */         do {
/*  657 */           res = this.channel.getSinkChannel().write(data);
/*  658 */           toWrite -= res;
/*  659 */         } while (res > 0L && toWrite > 0L);
/*  660 */         int max = toSend;
/*      */         
/*  662 */         while (max > 0) {
/*  663 */           AbstractFramedStreamSinkChannel abstractFramedStreamSinkChannel1 = (AbstractFramedStreamSinkChannel)this.pendingFrames.get(0);
/*  664 */           PooledByteBuffer frameHeaderByteBuffer = abstractFramedStreamSinkChannel1.getFrameHeader().getByteBuffer();
/*  665 */           ByteBuffer frameTrailerBuffer = abstractFramedStreamSinkChannel1.getFrameHeader().getTrailer();
/*  666 */           if ((frameHeaderByteBuffer != null && frameHeaderByteBuffer.getBuffer().hasRemaining()) || (abstractFramedStreamSinkChannel1
/*  667 */             .getBuffer() != null && abstractFramedStreamSinkChannel1.getBuffer().hasRemaining()) || (frameTrailerBuffer != null && frameTrailerBuffer
/*  668 */             .hasRemaining())) {
/*      */             break;
/*      */           }
/*  671 */           abstractFramedStreamSinkChannel1.flushComplete();
/*  672 */           this.pendingFrames.remove(abstractFramedStreamSinkChannel1);
/*  673 */           max--;
/*      */         } 
/*  675 */         if (!this.pendingFrames.isEmpty() || !this.channel.getSinkChannel().flush()) {
/*  676 */           this.channel.getSinkChannel().resumeWrites();
/*      */         } else {
/*  678 */           this.channel.getSinkChannel().suspendWrites();
/*      */         } 
/*  680 */         if (this.pendingFrames.isEmpty() && finalFrame) {
/*      */           
/*  682 */           this.channel.getSinkChannel().shutdownWrites();
/*  683 */           if (!this.channel.getSinkChannel().flush()) {
/*  684 */             this.channel.getSinkChannel().setWriteListener(ChannelListeners.flushingChannelListener(null, null));
/*  685 */             this.channel.getSinkChannel().resumeWrites();
/*      */           } 
/*  687 */         } else if (this.pendingFrames.size() > this.queuedFrameHighWaterMark) {
/*  688 */           (new UpdateResumeState(null, null, Boolean.valueOf(true))).run();
/*  689 */         } else if (this.receivesSuspendedTooManyQueuedMessages && this.pendingFrames.size() < this.queuedFrameLowWaterMark) {
/*  690 */           (new UpdateResumeState(null, null, Boolean.valueOf(false))).run();
/*      */         }
/*      */       
/*  693 */       } catch (IOException|RuntimeException|Error e) {
/*  694 */         IoUtils.safeClose((Closeable)this.channel);
/*  695 */         markWritesBroken(e);
/*      */       } 
/*      */     } finally {
/*  698 */       this.flushingSenders = false;
/*  699 */       if (!this.newFrames.isEmpty()) {
/*  700 */         runInIoThread(new Runnable()
/*      */             {
/*      */               public void run() {
/*  703 */                 AbstractFramedChannel.this.flushSenders();
/*      */               }
/*      */             });
/*      */       }
/*      */     } 
/*      */   }
/*      */   
/*      */   void awaitWritable() throws IOException {
/*  711 */     this.channel.getSinkChannel().awaitWritable();
/*      */   }
/*      */   
/*      */   void awaitWritable(long time, TimeUnit unit) throws IOException {
/*  715 */     this.channel.getSinkChannel().awaitWritable(time, unit);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void queueFrame(S channel) throws IOException {
/*  727 */     assert !this.newFrames.contains(channel);
/*  728 */     if (isWritesBroken() || !this.channel.getSinkChannel().isOpen() || channel.isBroken() || !channel.isOpen()) {
/*  729 */       IoUtils.safeClose((Closeable)channel);
/*  730 */       throw UndertowMessages.MESSAGES.channelIsClosed();
/*      */     } 
/*  732 */     this.newFrames.add(channel);
/*      */     
/*  734 */     if (!this.requireExplicitFlush || channel.isBufferFull()) {
/*  735 */       flush();
/*      */     }
/*      */   }
/*      */   
/*      */   public void flush() {
/*  740 */     if (!this.flushingSenders) {
/*  741 */       if (this.channel.getIoThread() == Thread.currentThread()) {
/*  742 */         flushSenders();
/*      */       } else {
/*  744 */         runInIoThread(new Runnable()
/*      */             {
/*      */               public void run() {
/*  747 */                 AbstractFramedChannel.this.flushSenders();
/*      */               }
/*      */             });
/*      */       } 
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ChannelListener.Setter<C> getReceiveSetter() {
/*  781 */     return (ChannelListener.Setter<C>)this.receiveSetter;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void suspendReceives() {
/*  788 */     this.receivesSuspendedByUser = true;
/*  789 */     getIoThread().execute(new UpdateResumeState(Boolean.valueOf(true), null, null));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void resumeReceives() {
/*  796 */     this.receivesSuspendedByUser = false;
/*  797 */     getIoThread().execute(new UpdateResumeState(Boolean.valueOf(false), null, null));
/*      */   }
/*      */ 
/*      */   
/*      */   private void doResume() {
/*  802 */     ReferenceCountedPooled localReadData = this.readData;
/*  803 */     if (localReadData != null && !localReadData.isFreed()) {
/*  804 */       this.channel.getSourceChannel().wakeupReads();
/*      */     } else {
/*  806 */       this.channel.getSourceChannel().resumeReads();
/*      */     } 
/*      */   }
/*      */   
/*      */   public boolean isReceivesResumed() {
/*  811 */     return !this.receivesSuspendedByUser;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void close() throws IOException {
/*  819 */     if (UndertowLogger.REQUEST_IO_LOGGER.isTraceEnabled()) {
/*  820 */       UndertowLogger.REQUEST_IO_LOGGER.tracef(new ClosedChannelException(), "Channel %s is being closed", this);
/*      */     }
/*  822 */     IoUtils.safeClose((Closeable)this.channel);
/*  823 */     ReferenceCountedPooled localReadData = this.readData;
/*  824 */     if (localReadData != null) {
/*  825 */       localReadData.close();
/*  826 */       this.readData = null;
/*      */     } 
/*  828 */     closeSubChannels();
/*      */   }
/*      */ 
/*      */   
/*      */   public ChannelListener.Setter<? extends AbstractFramedChannel> getCloseSetter() {
/*  833 */     return (ChannelListener.Setter<? extends AbstractFramedChannel>)this.closeSetter;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void markReadsBroken(Throwable cause) {
/*  845 */     if (readsBrokenUpdater.compareAndSet(this, 0, 1)) {
/*  846 */       if (UndertowLogger.REQUEST_IO_LOGGER.isDebugEnabled()) {
/*  847 */         UndertowLogger.REQUEST_IO_LOGGER.debugf(new ClosedChannelException(), "Marking reads broken on channel %s", this);
/*      */       }
/*  849 */       if (this.receiver != null) {
/*  850 */         this.receiver.markStreamBroken();
/*      */       }
/*  852 */       for (AbstractFramedStreamSourceChannel<C, R, S> r : (Iterable<AbstractFramedStreamSourceChannel<C, R, S>>)new ArrayList(getReceivers())) {
/*  853 */         r.markStreamBroken();
/*      */       }
/*      */       
/*  856 */       handleBrokenSourceChannel(cause);
/*  857 */       IoUtils.safeClose((Closeable)this.channel.getSourceChannel());
/*  858 */       closeSubChannels();
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void markWritesBroken(Throwable cause) {
/*  880 */     if (writesBrokenUpdater.compareAndSet(this, 0, 1)) {
/*  881 */       if (UndertowLogger.REQUEST_IO_LOGGER.isDebugEnabled()) {
/*  882 */         UndertowLogger.REQUEST_IO_LOGGER.debugf(new ClosedChannelException(), "Marking writes broken on channel %s", this);
/*      */       }
/*  884 */       handleBrokenSinkChannel(cause);
/*  885 */       IoUtils.safeClose((Closeable)this.channel.getSinkChannel());
/*  886 */       synchronized (this) {
/*  887 */         for (AbstractFramedStreamSinkChannel abstractFramedStreamSinkChannel : this.pendingFrames) {
/*  888 */           abstractFramedStreamSinkChannel.markBroken();
/*      */         }
/*  890 */         this.pendingFrames.clear();
/*  891 */         for (AbstractFramedStreamSinkChannel abstractFramedStreamSinkChannel : this.newFrames) {
/*  892 */           abstractFramedStreamSinkChannel.markBroken();
/*      */         }
/*  894 */         this.newFrames.clear();
/*  895 */         for (AbstractFramedStreamSinkChannel abstractFramedStreamSinkChannel : this.heldFrames) {
/*  896 */           abstractFramedStreamSinkChannel.markBroken();
/*      */         }
/*  898 */         this.heldFrames.clear();
/*      */       } 
/*      */     } 
/*      */   }
/*      */   
/*      */   protected boolean isWritesBroken() {
/*  904 */     return (writesBrokenUpdater.get(this) != 0);
/*      */   }
/*      */   
/*      */   protected boolean isReadsBroken() {
/*  908 */     return (readsBrokenUpdater.get(this) != 0);
/*      */   }
/*      */ 
/*      */   
/*      */   void resumeWrites() {
/*  913 */     this.channel.getSinkChannel().resumeWrites();
/*      */   }
/*      */   
/*      */   void suspendWrites() {
/*  917 */     this.channel.getSinkChannel().suspendWrites();
/*      */   }
/*      */   
/*      */   void wakeupWrites() {
/*  921 */     this.channel.getSinkChannel().wakeupWrites();
/*      */   }
/*      */   
/*      */   StreamSourceChannel getSourceChannel() {
/*  925 */     return (StreamSourceChannel)this.channel.getSourceChannel();
/*      */   }
/*      */ 
/*      */   
/*      */   void notifyFrameReadComplete(AbstractFramedStreamSourceChannel<C, R, S> channel) {}
/*      */ 
/*      */   
/*      */   private final class FrameReadListener
/*      */     implements ChannelListener<StreamSourceChannel>
/*      */   {
/*      */     private FrameReadListener() {}
/*      */ 
/*      */     
/*      */     public void handleEvent(final StreamSourceChannel channel) {
/*      */       boolean partialRead;
/*      */       Runnable runnable;
/*  941 */       while ((runnable = AbstractFramedChannel.this.taskRunQueue.poll()) != null) {
/*  942 */         runnable.run();
/*      */       }
/*      */       
/*  945 */       AbstractFramedStreamSourceChannel abstractFramedStreamSourceChannel = (AbstractFramedStreamSourceChannel)AbstractFramedChannel.this.receiver;
/*  946 */       if ((AbstractFramedChannel.this.readChannelDone || AbstractFramedChannel.this.isReadsSuspended()) && abstractFramedStreamSourceChannel == null) {
/*  947 */         channel.suspendReads();
/*      */         return;
/*      */       } 
/*  950 */       ChannelListener listener = AbstractFramedChannel.this.receiveSetter.get();
/*  951 */       if (listener == null) {
/*  952 */         listener = AbstractFramedChannel.DRAIN_LISTENER;
/*      */       }
/*  954 */       UndertowLogger.REQUEST_IO_LOGGER.tracef("Invoking receive listener: %s - receiver: %s", listener, abstractFramedStreamSourceChannel);
/*  955 */       ChannelListeners.invokeChannelListener((Channel)AbstractFramedChannel.this, listener);
/*      */ 
/*      */       
/*  958 */       synchronized (AbstractFramedChannel.this) {
/*  959 */         partialRead = AbstractFramedChannel.this.partialRead;
/*      */       } 
/*  961 */       ReferenceCountedPooled localReadData = AbstractFramedChannel.this.readData;
/*  962 */       if (localReadData != null && !localReadData.isFreed() && channel.isOpen() && !partialRead) {
/*      */         try {
/*  964 */           AbstractFramedChannel.this.runInIoThread(new Runnable()
/*      */               {
/*      */                 public void run() {
/*  967 */                   ChannelListeners.invokeChannelListener((Channel)channel, AbstractFramedChannel.FrameReadListener.this);
/*      */                 }
/*      */               });
/*  970 */         } catch (RejectedExecutionException e) {
/*  971 */           IoUtils.safeClose((Closeable)AbstractFramedChannel.this);
/*      */         } 
/*      */       }
/*  974 */       synchronized (AbstractFramedChannel.this) {
/*  975 */         AbstractFramedChannel.this.partialRead = false;
/*      */       } 
/*      */     }
/*      */   }
/*      */   
/*      */   private boolean isReadsSuspended() {
/*  981 */     return (this.receivesSuspendedByUser || this.receivesSuspendedTooManyBuffers || this.receivesSuspendedTooManyQueuedMessages);
/*      */   }
/*      */   
/*      */   private class FrameWriteListener implements ChannelListener<StreamSinkChannel> { private FrameWriteListener() {}
/*      */     
/*      */     public void handleEvent(StreamSinkChannel channel) {
/*  987 */       AbstractFramedChannel.this.flushSenders();
/*      */     } }
/*      */ 
/*      */ 
/*      */   
/*      */   private class FrameCloseListener
/*      */     implements ChannelListener<CloseableChannel>
/*      */   {
/*      */     private boolean sinkClosed;
/*      */     private boolean sourceClosed;
/*      */     
/*      */     private FrameCloseListener() {}
/*      */     
/*      */     public void handleEvent(final CloseableChannel c) {
/* 1001 */       if (Thread.currentThread() != c.getIoThread() && !c.getWorker().isShutdown()) {
/* 1002 */         AbstractFramedChannel.this.runInIoThread(new Runnable()
/*      */             {
/*      */               public void run() {
/* 1005 */                 ChannelListeners.invokeChannelListener((Channel)c, AbstractFramedChannel.FrameCloseListener.this);
/*      */               }
/*      */             });
/*      */         
/*      */         return;
/*      */       } 
/*      */       
/* 1012 */       if (c instanceof StreamSinkChannel) {
/* 1013 */         this.sinkClosed = true;
/* 1014 */       } else if (c instanceof StreamSourceChannel) {
/* 1015 */         this.sourceClosed = true;
/*      */       } 
/*      */       
/* 1018 */       final ReferenceCountedPooled localReadData = AbstractFramedChannel.this.readData;
/* 1019 */       if (!this.sourceClosed || !this.sinkClosed)
/*      */         return; 
/* 1021 */       if (localReadData != null && !localReadData.isFreed()) {
/*      */         
/* 1023 */         AbstractFramedChannel.this.runInIoThread(new Runnable()
/*      */             {
/*      */               public void run() {
/* 1026 */                 while (localReadData != null && !localReadData.isFreed()) {
/* 1027 */                   int rem = localReadData.getBuffer().remaining();
/* 1028 */                   ChannelListener listener = AbstractFramedChannel.this.receiveSetter.get();
/* 1029 */                   if (listener == null) {
/* 1030 */                     listener = AbstractFramedChannel.DRAIN_LISTENER;
/*      */                   }
/* 1032 */                   ChannelListeners.invokeChannelListener((Channel)AbstractFramedChannel.this, listener);
/* 1033 */                   if (!AbstractFramedChannel.this.isOpen()) {
/*      */                     break;
/*      */                   }
/* 1036 */                   if (localReadData != null && rem == localReadData.getBuffer().remaining()) {
/*      */                     break;
/*      */                   }
/*      */                 } 
/* 1040 */                 AbstractFramedChannel.FrameCloseListener.this.handleEvent(c);
/*      */               }
/*      */             });
/*      */         
/*      */         return;
/*      */       } 
/* 1046 */       AbstractFramedStreamSourceChannel abstractFramedStreamSourceChannel = (AbstractFramedStreamSourceChannel)AbstractFramedChannel.this.receiver; try {
/*      */         List<S> pendingFrames; List<S> newFrames; List<S> heldFrames; List<AbstractFramedStreamSourceChannel<C, R, S>> receivers;
/* 1048 */         if (abstractFramedStreamSourceChannel != null && abstractFramedStreamSourceChannel.isOpen() && abstractFramedStreamSourceChannel.isReadResumed()) {
/* 1049 */           ChannelListeners.invokeChannelListener((Channel)abstractFramedStreamSourceChannel, ((ChannelListener.SimpleSetter)abstractFramedStreamSourceChannel.getReadSetter()).get());
/*      */         }
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 1055 */         synchronized (AbstractFramedChannel.this) {
/* 1056 */           pendingFrames = new ArrayList<>(AbstractFramedChannel.this.pendingFrames);
/* 1057 */           newFrames = new ArrayList<>(AbstractFramedChannel.this.newFrames);
/* 1058 */           heldFrames = new ArrayList<>(AbstractFramedChannel.this.heldFrames);
/* 1059 */           receivers = new ArrayList<>(AbstractFramedChannel.this.getReceivers());
/*      */         } 
/* 1061 */         for (AbstractFramedStreamSinkChannel abstractFramedStreamSinkChannel : pendingFrames)
/*      */         {
/* 1063 */           abstractFramedStreamSinkChannel.markBroken();
/*      */         }
/*      */         
/* 1066 */         for (AbstractFramedStreamSinkChannel abstractFramedStreamSinkChannel : newFrames)
/*      */         {
/* 1068 */           abstractFramedStreamSinkChannel.markBroken();
/*      */         }
/* 1070 */         for (AbstractFramedStreamSinkChannel abstractFramedStreamSinkChannel : heldFrames)
/*      */         {
/* 1072 */           abstractFramedStreamSinkChannel.markBroken();
/*      */         }
/* 1074 */         for (AbstractFramedStreamSourceChannel<C, R, S> r : receivers) {
/* 1075 */           IoUtils.safeClose((Closeable)r);
/*      */         }
/*      */       } finally {
/*      */         
/*      */         try {
/* 1080 */           for (ChannelListener<C> task : (Iterable<ChannelListener<C>>)AbstractFramedChannel.this.closeTasks) {
/* 1081 */             ChannelListeners.invokeChannelListener((Channel)AbstractFramedChannel.this, task);
/*      */           }
/*      */         } finally {
/* 1084 */           synchronized (AbstractFramedChannel.this) {
/* 1085 */             AbstractFramedChannel.this.closeSubChannels();
/* 1086 */             if (localReadData != null) {
/* 1087 */               localReadData.close();
/* 1088 */               AbstractFramedChannel.this.readData = null;
/*      */             } 
/*      */           } 
/* 1091 */           ChannelListeners.invokeChannelListener((Channel)AbstractFramedChannel.this, AbstractFramedChannel.this.closeSetter.get());
/*      */         } 
/*      */       } 
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public void setIdleTimeout(long timeout) {
/* 1100 */     this.idleTimeoutConduit.setIdleTimeout(timeout);
/*      */   }
/*      */   
/*      */   public long getIdleTimeout() {
/* 1104 */     return this.idleTimeoutConduit.getIdleTimeout();
/*      */   }
/*      */   
/*      */   protected FramePriority<C, R, S> getFramePriority() {
/* 1108 */     return this.framePriority;
/*      */   }
/*      */   
/*      */   public void addCloseTask(ChannelListener<C> task) {
/* 1112 */     this.closeTasks.add(task);
/*      */   }
/*      */ 
/*      */   
/*      */   public String toString() {
/* 1117 */     StringBuilder stringBuilder = new StringBuilder(150);
/* 1118 */     stringBuilder.append(getClass().getSimpleName())
/* 1119 */       .append(" peer ")
/* 1120 */       .append(this.channel.getPeerAddress())
/* 1121 */       .append(" local ")
/* 1122 */       .append(this.channel.getLocalAddress())
/* 1123 */       .append("[ ");
/* 1124 */     synchronized (this) {
/* 1125 */       stringBuilder.append((this.receiver == null) ? "No Receiver" : this.receiver.toString())
/* 1126 */         .append(" ")
/* 1127 */         .append(this.pendingFrames.toString())
/* 1128 */         .append(" -- ")
/* 1129 */         .append(this.heldFrames.toString())
/* 1130 */         .append(" -- ")
/* 1131 */         .append(this.newFrames.toString());
/*      */     } 
/*      */     
/* 1134 */     return stringBuilder.toString();
/*      */   }
/*      */   
/*      */   protected StreamConnection getUnderlyingConnection() {
/* 1138 */     return this.channel;
/*      */   }
/*      */ 
/*      */   
/*      */   protected ChannelExceptionHandler<SuspendableWriteChannel> writeExceptionHandler() {
/* 1143 */     return new ChannelExceptionHandler<SuspendableWriteChannel>()
/*      */       {
/*      */         public void handleException(SuspendableWriteChannel channel, IOException exception) {
/* 1146 */           AbstractFramedChannel.this.markWritesBroken(exception);
/*      */         }
/*      */       };
/*      */   }
/*      */   
/*      */   public boolean isRequireExplicitFlush() {
/* 1152 */     return this.requireExplicitFlush;
/*      */   }
/*      */   
/*      */   public void setRequireExplicitFlush(boolean requireExplicitFlush) {
/* 1156 */     this.requireExplicitFlush = requireExplicitFlush;
/*      */   }
/*      */   
/*      */   protected OptionMap getSettings() {
/* 1160 */     return this.settings;
/*      */   } protected abstract R createChannel(FrameHeaderData paramFrameHeaderData, PooledByteBuffer paramPooledByteBuffer) throws IOException; protected abstract FrameHeaderData parseFrame(ByteBuffer paramByteBuffer) throws IOException;
/*      */   protected abstract boolean isLastFrameReceived();
/*      */   protected abstract boolean isLastFrameSent();
/*      */   protected abstract void handleBrokenSourceChannel(Throwable paramThrowable);
/*      */   protected abstract void handleBrokenSinkChannel(Throwable paramThrowable);
/*      */   protected abstract void closeSubChannels();
/*      */   protected abstract Collection<AbstractFramedStreamSourceChannel<C, R, S>> getReceivers();
/*      */   private class UpdateResumeState implements Runnable { private final Boolean user;
/*      */     private UpdateResumeState(Boolean user, Boolean buffers, Boolean frames) {
/* 1170 */       this.user = user;
/* 1171 */       this.buffers = buffers;
/* 1172 */       this.frames = frames;
/*      */     }
/*      */     private final Boolean buffers; private final Boolean frames;
/*      */     
/*      */     public void run() {
/* 1177 */       if (this.user != null) {
/* 1178 */         AbstractFramedChannel.this.receivesSuspendedByUser = this.user.booleanValue();
/*      */       }
/* 1180 */       if (this.buffers != null) {
/* 1181 */         AbstractFramedChannel.this.receivesSuspendedTooManyBuffers = this.buffers.booleanValue();
/*      */       }
/* 1183 */       if (this.frames != null) {
/* 1184 */         AbstractFramedChannel.this.receivesSuspendedTooManyQueuedMessages = this.frames.booleanValue();
/*      */       }
/* 1186 */       if (AbstractFramedChannel.this.receivesSuspendedByUser || AbstractFramedChannel.this.receivesSuspendedTooManyQueuedMessages || AbstractFramedChannel.this.receivesSuspendedTooManyBuffers) {
/* 1187 */         AbstractFramedChannel.this.channel.getSourceChannel().suspendReads();
/*      */       } else {
/* 1189 */         AbstractFramedChannel.this.doResume();
/*      */       } 
/*      */     } }
/*      */ 
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\protocol\framed\AbstractFramedChannel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */