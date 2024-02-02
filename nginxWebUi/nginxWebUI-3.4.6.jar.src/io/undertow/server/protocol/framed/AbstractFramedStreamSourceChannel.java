/*     */ package io.undertow.server.protocol.framed;
/*     */ 
/*     */ import io.undertow.UndertowLogger;
/*     */ import io.undertow.UndertowMessages;
/*     */ import io.undertow.connector.PooledByteBuffer;
/*     */ import java.io.Closeable;
/*     */ import java.io.IOException;
/*     */ import java.io.InterruptedIOException;
/*     */ import java.nio.Buffer;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.channels.Channel;
/*     */ import java.nio.channels.FileChannel;
/*     */ import java.util.Deque;
/*     */ import java.util.LinkedList;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import org.xnio.Bits;
/*     */ import org.xnio.Buffers;
/*     */ import org.xnio.ChannelListener;
/*     */ import org.xnio.ChannelListeners;
/*     */ import org.xnio.IoUtils;
/*     */ import org.xnio.Option;
/*     */ import org.xnio.XnioExecutor;
/*     */ import org.xnio.XnioIoThread;
/*     */ import org.xnio.XnioWorker;
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
/*     */ public abstract class AbstractFramedStreamSourceChannel<C extends AbstractFramedChannel<C, R, S>, R extends AbstractFramedStreamSourceChannel<C, R, S>, S extends AbstractFramedStreamSinkChannel<C, R, S>>
/*     */   implements StreamSourceChannel
/*     */ {
/*  56 */   private final ChannelListener.SimpleSetter<? extends R> readSetter = new ChannelListener.SimpleSetter();
/*  57 */   private final ChannelListener.SimpleSetter<? extends R> closeSetter = new ChannelListener.SimpleSetter();
/*     */   
/*     */   private final C framedChannel;
/*  60 */   private final Deque<FrameData> pendingFrameData = new LinkedList<>();
/*     */   
/*  62 */   private int state = 0;
/*     */   
/*     */   private static final int STATE_DONE = 2;
/*     */   
/*     */   private static final int STATE_READS_RESUMED = 4;
/*     */   
/*     */   private static final int STATE_READS_AWAKEN = 8;
/*     */   
/*     */   private static final int STATE_CLOSED = 16;
/*     */   
/*     */   private static final int STATE_LAST_FRAME = 32;
/*     */   
/*     */   private static final int STATE_IN_LISTENER_LOOP = 64;
/*     */   
/*     */   private static final int STATE_STREAM_BROKEN = 128;
/*     */   
/*     */   private static final int STATE_RETURNED_MINUS_ONE = 256;
/*     */   
/*     */   private static final int STATE_WAITNG_MINUS_ONE = 512;
/*     */   
/*     */   private volatile PooledByteBuffer data;
/*     */   private int currentDataOriginalSize;
/*     */   private long frameDataRemaining;
/*  85 */   private final Object lock = new Object();
/*     */   
/*     */   private int waiters;
/*     */   private volatile boolean waitingForFrame;
/*  89 */   private int readFrameCount = 0;
/*  90 */   private long maxStreamSize = -1L;
/*     */   private long currentStreamSize;
/*  92 */   private ChannelListener[] closeListeners = null;
/*     */   
/*     */   public AbstractFramedStreamSourceChannel(C framedChannel) {
/*  95 */     this.framedChannel = framedChannel;
/*  96 */     this.waitingForFrame = true;
/*     */   }
/*     */   
/*     */   public AbstractFramedStreamSourceChannel(C framedChannel, PooledByteBuffer data, long frameDataRemaining) {
/* 100 */     this.framedChannel = framedChannel;
/* 101 */     this.waitingForFrame = (data == null && frameDataRemaining <= 0L);
/* 102 */     this.frameDataRemaining = frameDataRemaining;
/* 103 */     this.currentStreamSize = frameDataRemaining;
/* 104 */     if (data != null) {
/* 105 */       if (!data.getBuffer().hasRemaining()) {
/* 106 */         data.close();
/* 107 */         this.data = null;
/* 108 */         this.waitingForFrame = (frameDataRemaining <= 0L);
/*     */       } else {
/* 110 */         dataReady(null, data);
/*     */       } 
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public long transferTo(long position, long count, FileChannel target) throws IOException {
/* 117 */     if (Bits.anyAreSet(this.state, 2)) {
/* 118 */       return -1L;
/*     */     }
/* 120 */     beforeRead();
/* 121 */     if (this.waitingForFrame) {
/* 122 */       return 0L;
/*     */     }
/*     */     try {
/* 125 */       if (this.frameDataRemaining == 0L && Bits.anyAreSet(this.state, 32))
/* 126 */         synchronized (this.lock) {
/* 127 */           this.state |= 0x100;
/* 128 */           return -1L;
/*     */         }  
/* 130 */       if (this.data != null) {
/* 131 */         int old = this.data.getBuffer().limit();
/*     */         try {
/* 133 */           if (count < this.data.getBuffer().remaining()) {
/* 134 */             this.data.getBuffer().limit((int)(this.data.getBuffer().position() + count));
/*     */           }
/* 136 */           return target.write(this.data.getBuffer(), position);
/*     */         } finally {
/* 138 */           this.data.getBuffer().limit(old);
/* 139 */           decrementFrameDataRemaining();
/*     */         } 
/*     */       } 
/* 142 */       return 0L;
/*     */     } finally {
/* 144 */       exitRead();
/*     */     } 
/*     */   }
/*     */   
/*     */   private void decrementFrameDataRemaining() {
/* 149 */     if (!this.data.getBuffer().hasRemaining()) {
/* 150 */       this.frameDataRemaining -= this.currentDataOriginalSize;
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public long transferTo(long count, ByteBuffer throughBuffer, StreamSinkChannel streamSinkChannel) throws IOException {
/* 156 */     if (Bits.anyAreSet(this.state, 2)) {
/* 157 */       return -1L;
/*     */     }
/* 159 */     beforeRead();
/* 160 */     if (this.waitingForFrame) {
/* 161 */       throughBuffer.position(throughBuffer.limit());
/* 162 */       return 0L;
/*     */     } 
/*     */     try {
/* 165 */       if (this.frameDataRemaining == 0L && Bits.anyAreSet(this.state, 32))
/* 166 */         synchronized (this.lock) {
/* 167 */           this.state |= 0x100;
/* 168 */           return -1L;
/*     */         }  
/* 170 */       if (this.data != null && this.data.getBuffer().hasRemaining()) {
/* 171 */         int old = this.data.getBuffer().limit();
/*     */         try {
/* 173 */           if (count < this.data.getBuffer().remaining()) {
/* 174 */             this.data.getBuffer().limit((int)(this.data.getBuffer().position() + count));
/*     */           }
/* 176 */           int written = streamSinkChannel.write(this.data.getBuffer());
/* 177 */           if (this.data.getBuffer().hasRemaining()) {
/*     */ 
/*     */             
/* 180 */             throughBuffer.clear();
/* 181 */             Buffers.copy(throughBuffer, this.data.getBuffer());
/* 182 */             throughBuffer.flip();
/*     */           } else {
/* 184 */             throughBuffer.position(throughBuffer.limit());
/*     */           } 
/* 186 */           return written;
/*     */         } finally {
/* 188 */           this.data.getBuffer().limit(old);
/* 189 */           decrementFrameDataRemaining();
/*     */         } 
/*     */       } 
/* 192 */       throughBuffer.position(throughBuffer.limit());
/*     */       
/* 194 */       return 0L;
/*     */     } finally {
/* 196 */       exitRead();
/*     */     } 
/*     */   }
/*     */   
/*     */   public long getMaxStreamSize() {
/* 201 */     return this.maxStreamSize;
/*     */   }
/*     */   
/*     */   public void setMaxStreamSize(long maxStreamSize) {
/* 205 */     this.maxStreamSize = maxStreamSize;
/* 206 */     if (maxStreamSize > 0L && 
/* 207 */       maxStreamSize < this.currentStreamSize) {
/* 208 */       handleStreamTooLarge();
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   private void handleStreamTooLarge() {
/* 214 */     IoUtils.safeClose((Closeable)this);
/*     */   }
/*     */ 
/*     */   
/*     */   public void suspendReads() {
/* 219 */     synchronized (this.lock) {
/* 220 */       this.state &= 0xFFFFFFF3;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void complete() throws IOException {
/* 230 */     close();
/*     */   }
/*     */   
/*     */   protected boolean isComplete() {
/* 234 */     return Bits.anyAreSet(this.state, 2);
/*     */   }
/*     */ 
/*     */   
/*     */   public void resumeReads() {
/* 239 */     resumeReadsInternal(false);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isReadResumed() {
/* 244 */     return Bits.anyAreSet(this.state, 4);
/*     */   }
/*     */ 
/*     */   
/*     */   public void wakeupReads() {
/* 249 */     resumeReadsInternal(true);
/*     */   }
/*     */   
/*     */   public void addCloseTask(ChannelListener<R> channelListener) {
/* 253 */     if (this.closeListeners == null) {
/* 254 */       this.closeListeners = new ChannelListener[] { channelListener };
/*     */     } else {
/* 256 */       ChannelListener[] old = this.closeListeners;
/* 257 */       this.closeListeners = new ChannelListener[old.length + 1];
/* 258 */       System.arraycopy(old, 0, this.closeListeners, 0, old.length);
/* 259 */       this.closeListeners[old.length] = channelListener;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   void resumeReadsInternal(boolean wakeup) {
/* 267 */     synchronized (this.lock) {
/* 268 */       this.state |= 0x4;
/*     */       
/* 270 */       if (wakeup) {
/* 271 */         this.state |= 0x8;
/*     */       }
/* 273 */       else if (!Bits.anyAreSet(this.state, 4)) {
/*     */         return;
/* 275 */       }  if (!Bits.anyAreSet(this.state, 64)) {
/* 276 */         this.state |= 0x40;
/* 277 */         getFramedChannel().runInIoThread(new Runnable()
/*     */             {
/*     */               public void run()
/*     */               {
/*     */                 try {
/*     */                   boolean readAgain;
/*     */                   do {
/* 284 */                     synchronized (AbstractFramedStreamSourceChannel.this.lock) {
/* 285 */                       AbstractFramedStreamSourceChannel.this.state = AbstractFramedStreamSourceChannel.this.state & 0xFFFFFFF7;
/*     */                     } 
/* 287 */                     ChannelListener<? super R> listener = AbstractFramedStreamSourceChannel.this.getReadListener();
/* 288 */                     if (listener == null || !AbstractFramedStreamSourceChannel.this.isReadResumed()) {
/*     */                       return;
/*     */                     }
/* 291 */                     ChannelListeners.invokeChannelListener((Channel)AbstractFramedStreamSourceChannel.this, listener);
/*     */ 
/*     */ 
/*     */                     
/* 295 */                     boolean moreData = ((AbstractFramedStreamSourceChannel.this.frameDataRemaining > 0L && AbstractFramedStreamSourceChannel.this.data != null) || !AbstractFramedStreamSourceChannel.access$500(AbstractFramedStreamSourceChannel.this).isEmpty() || Bits.anyAreSet(AbstractFramedStreamSourceChannel.this.state, 512));
/*     */                     
/* 297 */                     synchronized (AbstractFramedStreamSourceChannel.this.lock) {
/*     */ 
/*     */ 
/*     */                       
/* 301 */                       readAgain = (((AbstractFramedStreamSourceChannel.this.isReadResumed() && moreData) || Bits.allAreSet(AbstractFramedStreamSourceChannel.this.state, 8)) && Bits.allAreClear(AbstractFramedStreamSourceChannel.this.state, 144));
/* 302 */                       if (!readAgain)
/* 303 */                         AbstractFramedStreamSourceChannel.this.state = AbstractFramedStreamSourceChannel.this.state & 0xFFFFFFBF; 
/*     */                     } 
/* 305 */                   } while (readAgain);
/* 306 */                 } catch (RuntimeException|Error e) {
/* 307 */                   RuntimeException runtimeException; synchronized (AbstractFramedStreamSourceChannel.this.lock) {
/* 308 */                     AbstractFramedStreamSourceChannel.this.state = AbstractFramedStreamSourceChannel.this.state & 0xFFFFFFBF;
/*     */                   } 
/*     */                 } 
/*     */               }
/*     */             });
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   private ChannelListener<? super R> getReadListener() {
/* 318 */     return this.readSetter.get();
/*     */   }
/*     */ 
/*     */   
/*     */   public void shutdownReads() throws IOException {
/* 323 */     close();
/*     */   }
/*     */   
/*     */   protected void lastFrame() {
/* 327 */     synchronized (this.lock) {
/* 328 */       this.state |= 0x20;
/*     */     } 
/* 330 */     this.waitingForFrame = false;
/* 331 */     if (this.data == null && this.pendingFrameData.isEmpty() && this.frameDataRemaining == 0L) {
/* 332 */       synchronized (this.lock) {
/* 333 */         this.state |= 0x2;
/*     */       } 
/* 335 */       getFramedChannel().notifyFrameReadComplete(this);
/* 336 */       IoUtils.safeClose((Closeable)this);
/*     */     } 
/*     */   }
/*     */   
/*     */   protected boolean isLastFrame() {
/* 341 */     return Bits.anyAreSet(this.state, 32);
/*     */   }
/*     */ 
/*     */   
/*     */   public void awaitReadable() throws IOException {
/* 346 */     if (Thread.currentThread() == getIoThread()) {
/* 347 */       throw UndertowMessages.MESSAGES.awaitCalledFromIoThread();
/*     */     }
/* 349 */     if (this.data == null && this.pendingFrameData.isEmpty() && !Bits.anyAreSet(this.state, 144)) {
/* 350 */       synchronized (this.lock) {
/* 351 */         if (this.data == null && this.pendingFrameData.isEmpty() && !Bits.anyAreSet(this.state, 144)) {
/*     */           try {
/* 353 */             this.waiters++;
/* 354 */             this.lock.wait();
/* 355 */           } catch (InterruptedException e) {
/* 356 */             Thread.currentThread().interrupt();
/* 357 */             throw new InterruptedIOException();
/*     */           } finally {
/* 359 */             this.waiters--;
/*     */           } 
/*     */         }
/*     */       } 
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public void awaitReadable(long l, TimeUnit timeUnit) throws IOException {
/* 368 */     if (Thread.currentThread() == getIoThread()) {
/* 369 */       throw UndertowMessages.MESSAGES.awaitCalledFromIoThread();
/*     */     }
/* 371 */     if (this.data == null && this.pendingFrameData.isEmpty() && !Bits.anyAreSet(this.state, 144)) {
/* 372 */       synchronized (this.lock) {
/* 373 */         if (this.data == null && this.pendingFrameData.isEmpty() && !Bits.anyAreSet(this.state, 144)) {
/*     */           try {
/* 375 */             this.waiters++;
/* 376 */             this.lock.wait(timeUnit.toMillis(l));
/* 377 */           } catch (InterruptedException e) {
/* 378 */             Thread.currentThread().interrupt();
/* 379 */             throw new InterruptedIOException();
/*     */           } finally {
/* 381 */             this.waiters--;
/*     */           } 
/*     */         }
/*     */       } 
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void dataReady(FrameHeaderData headerData, PooledByteBuffer frameData) {
/* 395 */     if (Bits.anyAreSet(this.state, 144)) {
/* 396 */       frameData.close();
/*     */       return;
/*     */     } 
/* 399 */     synchronized (this.lock) {
/* 400 */       boolean newData = this.pendingFrameData.isEmpty();
/* 401 */       this.pendingFrameData.add(new FrameData(headerData, frameData));
/* 402 */       if (newData && 
/* 403 */         this.waiters > 0) {
/* 404 */         this.lock.notifyAll();
/*     */       }
/*     */       
/* 407 */       this.waitingForFrame = false;
/*     */     } 
/* 409 */     if (Bits.anyAreSet(this.state, 4)) {
/* 410 */       resumeReadsInternal(true);
/*     */     }
/* 412 */     if (headerData != null) {
/* 413 */       this.currentStreamSize += headerData.getFrameLength();
/* 414 */       if (this.maxStreamSize > 0L && this.currentStreamSize > this.maxStreamSize) {
/* 415 */         handleStreamTooLarge();
/*     */       }
/*     */     } 
/*     */   }
/*     */   
/*     */   protected long updateFrameDataRemaining(PooledByteBuffer frameData, long frameDataRemaining) {
/* 421 */     return frameDataRemaining;
/*     */   }
/*     */ 
/*     */   
/*     */   protected PooledByteBuffer processFrameData(PooledByteBuffer data, boolean lastFragmentOfFrame) throws IOException {
/* 426 */     return data;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected void handleHeaderData(FrameHeaderData headerData) {}
/*     */ 
/*     */   
/*     */   public XnioExecutor getReadThread() {
/* 435 */     return (XnioExecutor)this.framedChannel.getIoThread();
/*     */   }
/*     */ 
/*     */   
/*     */   public ChannelListener.Setter<? extends R> getReadSetter() {
/* 440 */     return (ChannelListener.Setter<? extends R>)this.readSetter;
/*     */   }
/*     */ 
/*     */   
/*     */   public ChannelListener.Setter<? extends R> getCloseSetter() {
/* 445 */     return (ChannelListener.Setter<? extends R>)this.closeSetter;
/*     */   }
/*     */ 
/*     */   
/*     */   public XnioWorker getWorker() {
/* 450 */     return this.framedChannel.getWorker();
/*     */   }
/*     */ 
/*     */   
/*     */   public XnioIoThread getIoThread() {
/* 455 */     return this.framedChannel.getIoThread();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean supportsOption(Option<?> option) {
/* 460 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public <T> T getOption(Option<T> tOption) throws IOException {
/* 465 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public <T> T setOption(Option<T> tOption, T t) throws IllegalArgumentException, IOException {
/* 470 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public long read(ByteBuffer[] dsts, int offset, int length) throws IOException {
/* 475 */     if (Bits.anyAreSet(this.state, 2)) {
/* 476 */       return -1L;
/*     */     }
/* 478 */     beforeRead();
/* 479 */     if (this.waitingForFrame) {
/* 480 */       return 0L;
/*     */     }
/*     */     try {
/* 483 */       if (this.frameDataRemaining == 0L && Bits.anyAreSet(this.state, 32)) {
/* 484 */         synchronized (this.lock) {
/* 485 */           this.state |= 0x100;
/*     */         } 
/* 487 */         return -1L;
/* 488 */       }  if (this.data != null) {
/* 489 */         int old = this.data.getBuffer().limit();
/*     */         try {
/* 491 */           long count = Buffers.remaining((Buffer[])dsts, offset, length);
/* 492 */           if (count < this.data.getBuffer().remaining()) {
/* 493 */             this.data.getBuffer().limit((int)(this.data.getBuffer().position() + count));
/*     */           } else {
/* 495 */             count = this.data.getBuffer().remaining();
/*     */           } 
/* 497 */           return Buffers.copy((int)count, dsts, offset, length, this.data.getBuffer());
/*     */         } finally {
/* 499 */           this.data.getBuffer().limit(old);
/* 500 */           decrementFrameDataRemaining();
/*     */         } 
/*     */       } 
/* 503 */       return 0L;
/*     */     } finally {
/* 505 */       exitRead();
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public long read(ByteBuffer[] dsts) throws IOException {
/* 511 */     return read(dsts, 0, dsts.length);
/*     */   }
/*     */ 
/*     */   
/*     */   public int read(ByteBuffer dst) throws IOException {
/* 516 */     if (Bits.anyAreSet(this.state, 2)) {
/* 517 */       return -1;
/*     */     }
/* 519 */     if (!dst.hasRemaining()) {
/* 520 */       return 0;
/*     */     }
/* 522 */     beforeRead();
/* 523 */     if (this.waitingForFrame) {
/* 524 */       return 0;
/*     */     }
/*     */     try {
/* 527 */       if (this.frameDataRemaining == 0L && Bits.anyAreSet(this.state, 32)) {
/* 528 */         synchronized (this.lock) {
/* 529 */           this.state |= 0x100;
/*     */         } 
/* 531 */         return -1;
/* 532 */       }  if (this.data != null) {
/* 533 */         int old = this.data.getBuffer().limit();
/*     */         try {
/* 535 */           int count = dst.remaining();
/* 536 */           if (count < this.data.getBuffer().remaining()) {
/* 537 */             this.data.getBuffer().limit(this.data.getBuffer().position() + count);
/*     */           } else {
/* 539 */             count = this.data.getBuffer().remaining();
/*     */           } 
/* 541 */           return Buffers.copy(count, dst, this.data.getBuffer());
/*     */         } finally {
/* 543 */           this.data.getBuffer().limit(old);
/* 544 */           decrementFrameDataRemaining();
/*     */         } 
/*     */       } 
/* 547 */       return 0;
/*     */     } finally {
/*     */       try {
/* 550 */         exitRead();
/* 551 */       } catch (Throwable e) {
/* 552 */         markStreamBroken();
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   private void beforeRead() throws IOException {
/* 558 */     if (Bits.anyAreSet(this.state, 128)) {
/* 559 */       throw UndertowMessages.MESSAGES.channelIsClosed();
/*     */     }
/* 561 */     if (this.data == null) {
/* 562 */       synchronized (this.lock) {
/* 563 */         FrameData pending = this.pendingFrameData.poll();
/* 564 */         if (pending != null) {
/* 565 */           PooledByteBuffer frameData = pending.getFrameData();
/* 566 */           boolean hasData = true;
/* 567 */           if (!frameData.getBuffer().hasRemaining()) {
/* 568 */             frameData.close();
/* 569 */             hasData = false;
/*     */           } 
/* 571 */           if (pending.getFrameHeaderData() != null) {
/* 572 */             this.frameDataRemaining = pending.getFrameHeaderData().getFrameLength();
/* 573 */             handleHeaderData(pending.getFrameHeaderData());
/*     */           } 
/* 575 */           if (hasData) {
/* 576 */             this.frameDataRemaining = updateFrameDataRemaining(frameData, this.frameDataRemaining);
/* 577 */             this.currentDataOriginalSize = frameData.getBuffer().remaining();
/*     */             try {
/* 579 */               this.data = processFrameData(frameData, (this.frameDataRemaining - this.currentDataOriginalSize == 0L));
/* 580 */             } catch (Throwable e) {
/* 581 */               frameData.close();
/* 582 */               UndertowLogger.REQUEST_IO_LOGGER.ioException(new IOException(e));
/* 583 */               markStreamBroken();
/*     */             } 
/*     */           } 
/*     */         } 
/*     */       } 
/*     */     }
/*     */   }
/*     */   
/*     */   private void exitRead() throws IOException {
/* 592 */     if (this.data != null && !this.data.getBuffer().hasRemaining()) {
/* 593 */       this.data.close();
/* 594 */       this.data = null;
/*     */     } 
/* 596 */     if (this.frameDataRemaining == 0L) {
/*     */       try {
/* 598 */         synchronized (this.lock) {
/* 599 */           this.readFrameCount++;
/* 600 */           if (this.pendingFrameData.isEmpty()) {
/* 601 */             if (Bits.anyAreSet(this.state, 256)) {
/* 602 */               this.state |= 0x2;
/* 603 */               complete();
/* 604 */               close();
/* 605 */             } else if (Bits.anyAreSet(this.state, 32)) {
/* 606 */               this.state |= 0x200;
/*     */             } else {
/* 608 */               this.waitingForFrame = true;
/*     */             } 
/*     */           }
/*     */         } 
/*     */       } finally {
/* 613 */         if (this.pendingFrameData.isEmpty()) {
/* 614 */           this.framedChannel.notifyFrameReadComplete(this);
/*     */         }
/*     */       } 
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isOpen() {
/* 622 */     return Bits.allAreClear(this.state, 16);
/*     */   }
/*     */ 
/*     */   
/*     */   public void close() {
/* 627 */     if (Bits.anyAreSet(this.state, 16)) {
/*     */       return;
/*     */     }
/* 630 */     synchronized (this.lock) {
/*     */       
/* 632 */       if (Bits.anyAreSet(this.state, 16)) {
/*     */         return;
/*     */       }
/* 635 */       this.state |= 0x10;
/* 636 */       if (Bits.allAreClear(this.state, 34)) {
/* 637 */         this.state |= 0x80;
/* 638 */         channelForciblyClosed();
/*     */       } 
/* 640 */       if (this.data != null) {
/* 641 */         this.data.close();
/* 642 */         this.data = null;
/*     */       } 
/* 644 */       while (!this.pendingFrameData.isEmpty()) {
/* 645 */         (this.pendingFrameData.poll()).frameData.close();
/*     */       }
/*     */       
/* 648 */       ChannelListeners.invokeChannelListener((Channel)this, this.closeSetter.get());
/* 649 */       if (this.closeListeners != null) {
/* 650 */         for (int i = 0; i < this.closeListeners.length; i++) {
/* 651 */           this.closeListeners[i].handleEvent((Channel)this);
/*     */         }
/*     */       }
/*     */ 
/*     */       
/* 656 */       if (this.waiters > 0) {
/* 657 */         this.lock.notifyAll();
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected void channelForciblyClosed() {}
/*     */ 
/*     */   
/*     */   protected C getFramedChannel() {
/* 668 */     return this.framedChannel;
/*     */   }
/*     */   
/*     */   protected int getReadFrameCount() {
/* 672 */     return this.readFrameCount;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void markStreamBroken() {
/* 680 */     if (Bits.anyAreSet(this.state, 128)) {
/*     */       return;
/*     */     }
/* 683 */     synchronized (this.lock) {
/* 684 */       this.state |= 0x80;
/* 685 */       PooledByteBuffer data = this.data;
/* 686 */       if (data != null) {
/*     */         try {
/* 688 */           data.close();
/* 689 */         } catch (Throwable throwable) {}
/*     */ 
/*     */         
/* 692 */         this.data = null;
/*     */       } 
/* 694 */       for (FrameData frame : this.pendingFrameData) {
/* 695 */         frame.frameData.close();
/*     */       }
/* 697 */       this.pendingFrameData.clear();
/* 698 */       if (isReadResumed()) {
/* 699 */         resumeReadsInternal(true);
/*     */       }
/* 701 */       if (this.waiters > 0) {
/* 702 */         this.lock.notifyAll();
/*     */       }
/*     */     } 
/*     */   }
/*     */   
/*     */   private class FrameData
/*     */   {
/*     */     private final FrameHeaderData frameHeaderData;
/*     */     private final PooledByteBuffer frameData;
/*     */     
/*     */     FrameData(FrameHeaderData frameHeaderData, PooledByteBuffer frameData) {
/* 713 */       this.frameHeaderData = frameHeaderData;
/* 714 */       this.frameData = frameData;
/*     */     }
/*     */     
/*     */     FrameHeaderData getFrameHeaderData() {
/* 718 */       return this.frameHeaderData;
/*     */     }
/*     */     
/*     */     PooledByteBuffer getFrameData() {
/* 722 */       return this.frameData;
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\protocol\framed\AbstractFramedStreamSourceChannel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */