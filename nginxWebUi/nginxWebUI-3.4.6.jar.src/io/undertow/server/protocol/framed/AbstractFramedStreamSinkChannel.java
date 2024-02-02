/*     */ package io.undertow.server.protocol.framed;
/*     */ 
/*     */ import io.undertow.UndertowLogger;
/*     */ import io.undertow.UndertowMessages;
/*     */ import io.undertow.connector.PooledByteBuffer;
/*     */ import io.undertow.util.ImmediatePooledByteBuffer;
/*     */ import java.io.Closeable;
/*     */ import java.io.IOException;
/*     */ import java.io.InterruptedIOException;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.channels.Channel;
/*     */ import java.nio.channels.FileChannel;
/*     */ import java.nio.channels.ReadableByteChannel;
/*     */ import java.nio.channels.WritableByteChannel;
/*     */ import java.security.AccessController;
/*     */ import java.util.concurrent.Executor;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
/*     */ import org.xnio.Bits;
/*     */ import org.xnio.Buffers;
/*     */ import org.xnio.ChannelListener;
/*     */ import org.xnio.ChannelListeners;
/*     */ import org.xnio.IoUtils;
/*     */ import org.xnio.Option;
/*     */ import org.xnio.XnioExecutor;
/*     */ import org.xnio.XnioIoThread;
/*     */ import org.xnio.XnioWorker;
/*     */ import org.xnio.channels.Channels;
/*     */ import org.xnio.channels.StreamSinkChannel;
/*     */ import org.xnio.channels.StreamSourceChannel;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class AbstractFramedStreamSinkChannel<C extends AbstractFramedChannel<C, R, S>, R extends AbstractFramedStreamSourceChannel<C, R, S>, S extends AbstractFramedStreamSinkChannel<C, R, S>>
/*     */   implements StreamSinkChannel
/*     */ {
/*     */   private static final int AWAIT_WRITABLE_TIMEOUT;
/*     */   
/*     */   static {
/*  69 */     int defaultAwaitWritableTimeout = 600000;
/*  70 */     int await_writable_timeout = ((Integer)AccessController.<Integer>doPrivileged(() -> Integer.getInteger("io.undertow.await_writable_timeout", 600000))).intValue();
/*  71 */     AWAIT_WRITABLE_TIMEOUT = (await_writable_timeout > 0) ? await_writable_timeout : 600000;
/*     */   }
/*  73 */   private static final PooledByteBuffer EMPTY_BYTE_BUFFER = (PooledByteBuffer)new ImmediatePooledByteBuffer(ByteBuffer.allocateDirect(0));
/*     */   
/*     */   private final C channel;
/*  76 */   private final ChannelListener.SimpleSetter<S> writeSetter = new ChannelListener.SimpleSetter();
/*  77 */   private final ChannelListener.SimpleSetter<S> closeSetter = new ChannelListener.SimpleSetter();
/*     */   
/*  79 */   private final Object lock = new Object();
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  84 */   private volatile int state = 0;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private volatile boolean readyForFlush;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private volatile boolean fullyFlushed;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private volatile boolean finalFrameQueued;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private volatile boolean broken;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 109 */   private volatile int waiterCount = 0;
/*     */   
/*     */   private volatile SendFrameHeader header;
/*     */   
/*     */   private volatile PooledByteBuffer writeBuffer;
/*     */   
/*     */   private volatile PooledByteBuffer body;
/*     */   
/*     */   private static final int STATE_CLOSED = 1;
/*     */   
/*     */   private static final int STATE_WRITES_SHUTDOWN = 2;
/*     */   
/*     */   private static final int STATE_FIRST_DATA_WRITTEN = 4;
/*     */   private static final int STATE_PRE_WRITE_CALLED = 8;
/*     */   private volatile boolean bufferFull;
/*     */   private volatile boolean writesResumed;
/*     */   private volatile int inListenerLoop;
/*     */   private volatile boolean writeSucceeded;
/* 127 */   private static final AtomicIntegerFieldUpdater<AbstractFramedStreamSinkChannel> inListenerLoopUpdater = AtomicIntegerFieldUpdater.newUpdater(AbstractFramedStreamSinkChannel.class, "inListenerLoop");
/*     */   
/*     */   protected AbstractFramedStreamSinkChannel(C channel) {
/* 130 */     this.channel = channel;
/*     */   }
/*     */   
/*     */   public long transferFrom(FileChannel src, long position, long count) throws IOException {
/* 134 */     return src.transferTo(position, count, (WritableByteChannel)this);
/*     */   }
/*     */   
/*     */   public long transferFrom(StreamSourceChannel source, long count, ByteBuffer throughBuffer) throws IOException {
/* 138 */     return IoUtils.transfer((ReadableByteChannel)source, count, throughBuffer, (WritableByteChannel)this);
/*     */   }
/*     */ 
/*     */   
/*     */   public void suspendWrites() {
/* 143 */     this.writesResumed = false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   final SendFrameHeader getFrameHeader() throws IOException {
/* 159 */     if (this.header == null) {
/* 160 */       this.header = createFrameHeader();
/* 161 */       if (this.header == null) {
/* 162 */         this.header = new SendFrameHeader(0, null);
/*     */       }
/*     */     } 
/* 165 */     return this.header;
/*     */   }
/*     */   
/*     */   protected SendFrameHeader createFrameHeader() throws IOException {
/* 169 */     return null;
/*     */   }
/*     */   
/*     */   final void preWrite() {
/* 173 */     synchronized (this.lock) {
/* 174 */       if (Bits.allAreClear(this.state, 8)) {
/* 175 */         this.state |= 0x8;
/* 176 */         this.body = preWriteTransform(this.body);
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   protected PooledByteBuffer preWriteTransform(PooledByteBuffer body) {
/* 182 */     return body;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isWriteResumed() {
/* 187 */     return this.writesResumed;
/*     */   }
/*     */ 
/*     */   
/*     */   public void wakeupWrites() {
/* 192 */     resumeWritesInternal(true);
/*     */   }
/*     */ 
/*     */   
/*     */   public void resumeWrites() {
/* 197 */     resumeWritesInternal(false);
/*     */   }
/*     */   
/*     */   protected void resumeWritesInternal(boolean wakeup) {
/* 201 */     boolean alreadyResumed = this.writesResumed;
/* 202 */     if (!wakeup && alreadyResumed) {
/*     */       return;
/*     */     }
/* 205 */     this.writesResumed = true;
/* 206 */     if (this.readyForFlush && !wakeup) {
/*     */       return;
/*     */     }
/*     */ 
/*     */     
/* 211 */     if (inListenerLoopUpdater.compareAndSet(this, 0, 1)) {
/* 212 */       getChannel().runInIoThread(new Runnable()
/*     */           {
/*     */ 
/*     */             
/* 216 */             int loopCount = 0;
/*     */ 
/*     */             
/*     */             public void run() {
/*     */               try {
/* 221 */                 ChannelListener<? super S> listener = AbstractFramedStreamSinkChannel.this.getWriteListener();
/* 222 */                 if (listener == null || !AbstractFramedStreamSinkChannel.this.isWriteResumed()) {
/*     */                   return;
/*     */                 }
/* 225 */                 if (AbstractFramedStreamSinkChannel.this.writeSucceeded) {
/*     */                   
/* 227 */                   AbstractFramedStreamSinkChannel.this.writeSucceeded = false;
/* 228 */                   this.loopCount = 0;
/* 229 */                 } else if (this.loopCount++ == 100) {
/*     */                   
/* 231 */                   UndertowLogger.ROOT_LOGGER.listenerNotProgressing();
/* 232 */                   IoUtils.safeClose((Closeable)AbstractFramedStreamSinkChannel.this);
/*     */                   return;
/*     */                 } 
/* 235 */                 ChannelListeners.invokeChannelListener((Channel)AbstractFramedStreamSinkChannel.this, listener);
/*     */               } finally {
/*     */                 
/* 238 */                 AbstractFramedStreamSinkChannel.inListenerLoopUpdater.set(AbstractFramedStreamSinkChannel.this, 0);
/*     */               } 
/*     */ 
/*     */ 
/*     */               
/* 243 */               if (AbstractFramedStreamSinkChannel.this.writesResumed && Bits.allAreClear(AbstractFramedStreamSinkChannel.this.state, 1) && !AbstractFramedStreamSinkChannel.this.broken && !AbstractFramedStreamSinkChannel.this.readyForFlush && !AbstractFramedStreamSinkChannel.this.fullyFlushed && AbstractFramedStreamSinkChannel
/* 244 */                 .inListenerLoopUpdater.compareAndSet(AbstractFramedStreamSinkChannel.this, 0, 1)) {
/* 245 */                 AbstractFramedStreamSinkChannel.this.getIoThread().execute(this);
/*     */               }
/*     */             }
/*     */           });
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void shutdownWrites() throws IOException {
/* 257 */     queueFinalFrame();
/* 258 */     synchronized (this.lock) {
/* 259 */       if (Bits.anyAreSet(this.state, 2) || this.broken) {
/*     */         return;
/*     */       }
/* 262 */       this.state |= 0x2;
/*     */     } 
/*     */   }
/*     */   
/*     */   private void queueFinalFrame() throws IOException {
/* 267 */     synchronized (this.lock) {
/* 268 */       if (!this.readyForFlush && !this.fullyFlushed && Bits.allAreClear(this.state, 1) && !this.broken && !this.finalFrameQueued)
/* 269 */       { if (null == this.body && null != this.writeBuffer) {
/* 270 */           sendWriteBuffer();
/* 271 */         } else if (null == this.body) {
/* 272 */           this.body = EMPTY_BYTE_BUFFER;
/*     */         } 
/* 274 */         this.readyForFlush = true;
/* 275 */         this.state |= 0x4;
/* 276 */         this.state |= 0x2;
/* 277 */         this.finalFrameQueued = true; }
/*     */       else { return; }
/*     */     
/* 280 */     }  this.channel.queueFrame(this);
/*     */   }
/*     */   
/*     */   protected boolean isFinalFrameQueued() {
/* 284 */     return this.finalFrameQueued;
/*     */   }
/*     */ 
/*     */   
/*     */   public void awaitWritable() throws IOException {
/* 289 */     if (Thread.currentThread() == getIoThread()) {
/* 290 */       throw UndertowMessages.MESSAGES.awaitCalledFromIoThread();
/*     */     }
/* 292 */     synchronized (this.lock) {
/* 293 */       if (Bits.anyAreSet(this.state, 1) || this.broken) {
/*     */         return;
/*     */       }
/* 296 */       if (this.readyForFlush) {
/*     */         try {
/* 298 */           this.waiterCount++;
/*     */           
/* 300 */           if (this.readyForFlush && !Bits.anyAreSet(this.state, 1) && !this.broken) {
/* 301 */             this.lock.wait(AWAIT_WRITABLE_TIMEOUT);
/*     */           }
/* 303 */         } catch (InterruptedException e) {
/* 304 */           Thread.currentThread().interrupt();
/* 305 */           throw new InterruptedIOException();
/*     */         } finally {
/* 307 */           this.waiterCount--;
/*     */         } 
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void awaitWritable(long l, TimeUnit timeUnit) throws IOException {
/* 315 */     if (Thread.currentThread() == getIoThread()) {
/* 316 */       throw UndertowMessages.MESSAGES.awaitCalledFromIoThread();
/*     */     }
/* 318 */     synchronized (this.lock) {
/* 319 */       if (Bits.anyAreSet(this.state, 1) || this.broken) {
/*     */         return;
/*     */       }
/* 322 */       if (this.readyForFlush) {
/*     */         try {
/* 324 */           this.waiterCount++;
/* 325 */           if (this.readyForFlush && !Bits.anyAreSet(this.state, 1) && !this.broken) {
/* 326 */             this.lock.wait(timeUnit.toMillis(l));
/*     */           }
/* 328 */         } catch (InterruptedException e) {
/* 329 */           Thread.currentThread().interrupt();
/* 330 */           throw new InterruptedIOException();
/*     */         } finally {
/* 332 */           this.waiterCount--;
/*     */         } 
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public XnioExecutor getWriteThread() {
/* 340 */     return (XnioExecutor)this.channel.getIoThread();
/*     */   }
/*     */ 
/*     */   
/*     */   public ChannelListener.Setter<? extends S> getWriteSetter() {
/* 345 */     return (ChannelListener.Setter<? extends S>)this.writeSetter;
/*     */   }
/*     */ 
/*     */   
/*     */   public ChannelListener.Setter<? extends S> getCloseSetter() {
/* 350 */     return (ChannelListener.Setter<? extends S>)this.closeSetter;
/*     */   }
/*     */ 
/*     */   
/*     */   public XnioWorker getWorker() {
/* 355 */     return this.channel.getWorker();
/*     */   }
/*     */ 
/*     */   
/*     */   public XnioIoThread getIoThread() {
/* 360 */     return this.channel.getIoThread();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean flush() throws IOException {
/* 365 */     if (Bits.anyAreSet(this.state, 1)) {
/* 366 */       return true;
/*     */     }
/* 368 */     if (this.broken) {
/* 369 */       throw UndertowMessages.MESSAGES.channelIsClosed();
/*     */     }
/*     */     
/* 372 */     if (this.readyForFlush) {
/* 373 */       return false;
/*     */     }
/* 375 */     synchronized (this.lock) {
/* 376 */       if (this.fullyFlushed) {
/* 377 */         this.state |= 0x1;
/* 378 */         return true;
/*     */       } 
/*     */     } 
/* 381 */     if (Bits.anyAreSet(this.state, 2) && !this.finalFrameQueued) {
/* 382 */       queueFinalFrame();
/* 383 */       return false;
/*     */     } 
/* 385 */     if (Bits.anyAreSet(this.state, 2)) {
/* 386 */       return false;
/*     */     }
/* 388 */     if (isFlushRequiredOnEmptyBuffer() || (this.writeBuffer != null && this.writeBuffer.getBuffer().position() > 0)) {
/* 389 */       handleBufferFull();
/* 390 */       return !this.readyForFlush;
/*     */     } 
/* 392 */     return true;
/*     */   }
/*     */   
/*     */   protected boolean isFlushRequiredOnEmptyBuffer() {
/* 396 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public long write(ByteBuffer[] srcs, int offset, int length) throws IOException {
/* 401 */     if (!safeToSend()) {
/* 402 */       return 0L;
/*     */     }
/* 404 */     if (this.writeBuffer == null) {
/* 405 */       this.writeBuffer = getChannel().getBufferPool().allocate();
/*     */     }
/* 407 */     ByteBuffer buffer = this.writeBuffer.getBuffer();
/* 408 */     int copied = Buffers.copy(buffer, srcs, offset, length);
/* 409 */     if (!buffer.hasRemaining()) {
/* 410 */       handleBufferFull();
/*     */     }
/* 412 */     this.writeSucceeded = (this.writeSucceeded || copied > 0);
/* 413 */     return copied;
/*     */   }
/*     */ 
/*     */   
/*     */   public long write(ByteBuffer[] srcs) throws IOException {
/* 418 */     return write(srcs, 0, srcs.length);
/*     */   }
/*     */ 
/*     */   
/*     */   public int write(ByteBuffer src) throws IOException {
/* 423 */     if (!safeToSend()) {
/* 424 */       return 0;
/*     */     }
/* 426 */     if (this.writeBuffer == null) {
/* 427 */       this.writeBuffer = getChannel().getBufferPool().allocate();
/*     */     }
/* 429 */     ByteBuffer buffer = this.writeBuffer.getBuffer();
/* 430 */     int copied = Buffers.copy(buffer, src);
/* 431 */     if (!buffer.hasRemaining()) {
/* 432 */       handleBufferFull();
/*     */     }
/* 434 */     this.writeSucceeded = (this.writeSucceeded || copied > 0);
/* 435 */     return copied;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean send(PooledByteBuffer pooled) throws IOException {
/* 447 */     if (isWritesShutdown()) {
/* 448 */       throw UndertowMessages.MESSAGES.channelIsClosed();
/*     */     }
/* 450 */     boolean result = sendInternal(pooled);
/* 451 */     if (result) {
/* 452 */       flush();
/*     */     }
/* 454 */     return result;
/*     */   }
/*     */   
/*     */   protected boolean sendInternal(PooledByteBuffer pooled) throws IOException {
/* 458 */     if (safeToSend()) {
/* 459 */       this.body = pooled;
/* 460 */       this.writeSucceeded = true;
/* 461 */       return true;
/*     */     } 
/* 463 */     return false;
/*     */   }
/*     */   
/*     */   protected boolean safeToSend() throws IOException {
/* 467 */     int state = this.state;
/* 468 */     if (Bits.anyAreSet(state, 1) || this.broken) {
/* 469 */       throw UndertowMessages.MESSAGES.channelIsClosed();
/*     */     }
/* 471 */     if (this.readyForFlush) {
/* 472 */       return false;
/*     */     }
/* 474 */     if (null != this.body) {
/* 475 */       throw UndertowMessages.MESSAGES.bodyIsSetAndNotReadyForFlush();
/*     */     }
/* 477 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected long getAwaitWritableTimeout() {
/* 486 */     return AWAIT_WRITABLE_TIMEOUT;
/*     */   }
/*     */ 
/*     */   
/*     */   public long writeFinal(ByteBuffer[] srcs, int offset, int length) throws IOException {
/* 491 */     return Channels.writeFinalBasic(this, srcs, offset, length);
/*     */   }
/*     */ 
/*     */   
/*     */   public long writeFinal(ByteBuffer[] srcs) throws IOException {
/* 496 */     return writeFinal(srcs, 0, srcs.length);
/*     */   }
/*     */ 
/*     */   
/*     */   public int writeFinal(ByteBuffer src) throws IOException {
/* 501 */     return Channels.writeFinalBasic(this, src);
/*     */   }
/*     */   
/*     */   private void handleBufferFull() throws IOException {
/* 505 */     synchronized (this.lock) {
/* 506 */       this.bufferFull = true;
/* 507 */       if (this.readyForFlush)
/* 508 */         return;  sendWriteBuffer();
/* 509 */       this.readyForFlush = true;
/* 510 */       this.state |= 0x4;
/*     */     } 
/* 512 */     this.channel.queueFrame(this);
/*     */   }
/*     */   
/*     */   private void sendWriteBuffer() throws IOException {
/* 516 */     if (this.writeBuffer == null) {
/* 517 */       this.writeBuffer = EMPTY_BYTE_BUFFER;
/*     */     }
/* 519 */     this.writeBuffer.getBuffer().flip();
/* 520 */     if (!sendInternal(this.writeBuffer)) {
/* 521 */       throw UndertowMessages.MESSAGES.failedToSendAfterBeingSafe();
/*     */     }
/* 523 */     this.writeBuffer = null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isReadyForFlush() {
/* 536 */     return this.readyForFlush;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isWritesShutdown() {
/* 543 */     return Bits.anyAreSet(this.state, 2);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isOpen() {
/* 548 */     return Bits.allAreClear(this.state, 1);
/*     */   }
/*     */ 
/*     */   
/*     */   public void close() throws IOException {
/* 553 */     if (this.fullyFlushed || Bits.anyAreSet(this.state, 1)) {
/*     */       return;
/*     */     }
/*     */     try {
/* 557 */       synchronized (this.lock) {
/*     */         
/* 559 */         if (this.fullyFlushed || Bits.anyAreSet(this.state, 1)) {
/*     */           return;
/*     */         }
/* 562 */         this.state |= 0x1;
/* 563 */         if (this.writeBuffer != null) {
/* 564 */           this.writeBuffer.close();
/* 565 */           this.writeBuffer = null;
/*     */         } 
/* 567 */         if (this.body != null) {
/* 568 */           this.body.close();
/* 569 */           this.body = null;
/*     */         } 
/* 571 */         if (this.header != null && this.header.getByteBuffer() != null) {
/* 572 */           this.header.getByteBuffer().close();
/* 573 */           this.header = null;
/*     */         } 
/*     */       } 
/* 576 */       channelForciblyClosed();
/*     */       
/* 578 */       if (isWriteResumed()) {
/* 579 */         ChannelListeners.invokeChannelListener((Executor)getIoThread(), (Channel)this, getWriteListener());
/*     */       }
/* 581 */       wakeupWrites();
/*     */     } finally {
/* 583 */       wakeupWaiters();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void channelForciblyClosed() throws IOException {
/* 598 */     if (isFirstDataWritten()) {
/* 599 */       getChannel().markWritesBroken(null);
/*     */     }
/* 601 */     wakeupWaiters();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean supportsOption(Option<?> option) {
/* 606 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public <T> T getOption(Option<T> tOption) throws IOException {
/* 611 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public <T> T setOption(Option<T> tOption, T t) throws IllegalArgumentException, IOException {
/* 616 */     return null;
/*     */   }
/*     */   
/*     */   public ByteBuffer getBuffer() {
/* 620 */     if (Bits.anyAreSet(this.state, 1)) {
/* 621 */       throw new IllegalStateException();
/*     */     }
/* 623 */     if (this.body == null)
/*     */     {
/* 625 */       this.body = EMPTY_BYTE_BUFFER;
/*     */     }
/* 627 */     return this.body.getBuffer();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   final void flushComplete() throws IOException {
/* 634 */     synchronized (this.lock) {
/*     */       try {
/* 636 */         boolean resetReadyForFlush = true;
/* 637 */         this.bufferFull = false;
/* 638 */         int remaining = this.header.getRemainingInBuffer();
/* 639 */         boolean finalFrame = this.finalFrameQueued;
/* 640 */         boolean channelClosed = (finalFrame && remaining == 0 && !this.header.isAnotherFrameRequired());
/* 641 */         if (remaining > 0) {
/*     */ 
/*     */ 
/*     */ 
/*     */           
/* 646 */           this.body.getBuffer().limit(this.body.getBuffer().limit() + remaining);
/* 647 */           this.body.getBuffer().compact();
/* 648 */           this.writeBuffer = this.body;
/* 649 */           this.body = null;
/* 650 */           this.state &= 0xFFFFFFF7;
/* 651 */           if (finalFrame) {
/*     */             
/* 653 */             this.finalFrameQueued = false;
/*     */ 
/*     */ 
/*     */             
/* 657 */             resetReadyForFlush = this.readyForFlush = false;
/* 658 */             queueFinalFrame();
/*     */           } 
/* 660 */         } else if (this.header.isAnotherFrameRequired()) {
/* 661 */           this.finalFrameQueued = false;
/* 662 */           if (this.body != null) {
/* 663 */             this.body.close();
/* 664 */             this.body = null;
/* 665 */             this.state &= 0xFFFFFFF7;
/*     */           } 
/* 667 */         } else if (this.body != null) {
/* 668 */           this.body.close();
/* 669 */           this.body = null;
/* 670 */           this.state &= 0xFFFFFFF7;
/*     */         } 
/* 672 */         if (channelClosed) {
/* 673 */           this.fullyFlushed = true;
/* 674 */           if (this.body != null) {
/* 675 */             this.body.close();
/* 676 */             this.body = null;
/* 677 */             this.state &= 0xFFFFFFF7;
/*     */           } 
/*     */         } 
/*     */         
/* 681 */         if (this.header.getByteBuffer() != null) {
/* 682 */           this.header.getByteBuffer().close();
/*     */         }
/* 684 */         this.header = null;
/*     */         
/* 686 */         if (resetReadyForFlush) {
/* 687 */           this.readyForFlush = false;
/*     */         }
/*     */         
/* 690 */         if (isWriteResumed() && !channelClosed) {
/* 691 */           wakeupWrites();
/* 692 */         } else if (isWriteResumed()) {
/*     */ 
/*     */           
/* 695 */           ChannelListeners.invokeChannelListener((Executor)getIoThread(), (Channel)this, getWriteListener());
/*     */         } 
/*     */         
/* 698 */         ChannelListener<? super S> closeListener = this.closeSetter.get();
/* 699 */         if (channelClosed && closeListener != null) {
/* 700 */           ChannelListeners.invokeChannelListener((Executor)getIoThread(), (Channel)this, closeListener);
/*     */         }
/* 702 */         handleFlushComplete(channelClosed);
/*     */       } finally {
/* 704 */         wakeupWaiters();
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   protected void handleFlushComplete(boolean finalFrame) {}
/*     */ 
/*     */   
/*     */   protected boolean isFirstDataWritten() {
/* 714 */     return Bits.anyAreSet(this.state, 4);
/*     */   }
/*     */   
/*     */   public void markBroken() {
/* 718 */     this.broken = true;
/*     */     try {
/* 720 */       wakeupWrites();
/* 721 */       wakeupWaiters();
/* 722 */       if (isWriteResumed()) {
/* 723 */         ChannelListener<? super S> writeListener = this.writeSetter.get();
/* 724 */         if (writeListener != null) {
/* 725 */           ChannelListeners.invokeChannelListener((Executor)getIoThread(), (Channel)this, writeListener);
/*     */         }
/*     */       } 
/* 728 */       ChannelListener<? super S> closeListener = this.closeSetter.get();
/* 729 */       if (closeListener != null) {
/* 730 */         ChannelListeners.invokeChannelListener((Executor)getIoThread(), (Channel)this, closeListener);
/*     */       }
/*     */     } finally {
/* 733 */       if (this.header != null && 
/* 734 */         this.header.getByteBuffer() != null) {
/* 735 */         this.header.getByteBuffer().close();
/* 736 */         this.header = null;
/*     */       } 
/*     */       
/* 739 */       if (this.body != null) {
/* 740 */         this.body.close();
/* 741 */         this.body = null;
/*     */       } 
/* 743 */       if (this.writeBuffer != null) {
/* 744 */         this.writeBuffer.close();
/* 745 */         this.writeBuffer = null;
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   ChannelListener<? super S> getWriteListener() {
/* 751 */     return this.writeSetter.get();
/*     */   }
/*     */   
/*     */   private void wakeupWaiters() {
/* 755 */     if (this.waiterCount > 0) {
/* 756 */       synchronized (this.lock) {
/*     */ 
/*     */         
/* 759 */         if (this.waiterCount > 0) {
/* 760 */           this.lock.notifyAll();
/*     */         }
/*     */       } 
/*     */     }
/*     */   }
/*     */   
/*     */   public C getChannel() {
/* 767 */     return this.channel;
/*     */   }
/*     */   
/*     */   public boolean isBroken() {
/* 771 */     return this.broken;
/*     */   }
/*     */   
/*     */   public boolean isBufferFull() {
/* 775 */     return this.bufferFull;
/*     */   }
/*     */   
/*     */   protected abstract boolean isLastFrame();
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\protocol\framed\AbstractFramedStreamSinkChannel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */