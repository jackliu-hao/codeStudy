/*     */ package org.xnio.channels;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.nio.channels.Channel;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
/*     */ import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;
/*     */ import java.util.concurrent.locks.LockSupport;
/*     */ import org.xnio.Bits;
/*     */ import org.xnio.ChannelListener;
/*     */ import org.xnio.ChannelListeners;
/*     */ import org.xnio.IoUtils;
/*     */ import org.xnio.Option;
/*     */ import org.xnio.XnioExecutor;
/*     */ import org.xnio.XnioIoThread;
/*     */ import org.xnio.XnioWorker;
/*     */ import org.xnio._private.Messages;
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
/*     */ @Deprecated
/*     */ public abstract class TranslatingSuspendableChannel<C extends SuspendableChannel, W extends SuspendableChannel>
/*     */   implements SuspendableChannel, WrappedChannel<W>, ReadListenerSettable<C>, WriteListenerSettable<C>, CloseListenerSettable<C>
/*     */ {
/*     */   protected final W channel;
/*     */   private ChannelListener<? super C> readListener;
/*     */   private ChannelListener<? super C> writeListener;
/*     */   private ChannelListener<? super C> closeListener;
/*     */   private volatile int state;
/*     */   private volatile Thread readWaiter;
/*     */   private volatile Thread writeWaiter;
/*  76 */   private static final AtomicIntegerFieldUpdater<TranslatingSuspendableChannel> stateUpdater = AtomicIntegerFieldUpdater.newUpdater(TranslatingSuspendableChannel.class, "state");
/*     */ 
/*     */   
/*  79 */   private static final AtomicReferenceFieldUpdater<TranslatingSuspendableChannel, Thread> readWaiterUpdater = AtomicReferenceFieldUpdater.newUpdater(TranslatingSuspendableChannel.class, Thread.class, "readWaiter");
/*     */   
/*  81 */   private static final AtomicReferenceFieldUpdater<TranslatingSuspendableChannel, Thread> writeWaiterUpdater = AtomicReferenceFieldUpdater.newUpdater(TranslatingSuspendableChannel.class, Thread.class, "writeWaiter");
/*     */   
/*     */   private static final int READ_REQUESTED = 1;
/*     */   
/*     */   private static final int READ_REQUIRES_WRITE = 2;
/*     */   
/*     */   private static final int READ_READY = 4;
/*     */   
/*     */   private static final int READ_SHUT_DOWN = 8;
/*  90 */   private static final int READ_REQUIRES_EXT = Bits.intBitMask(11, 15);
/*     */   
/*     */   private static final int READ_SINGLE_EXT = 2048;
/*     */   
/*  94 */   private static final int READ_FLAGS = Bits.intBitMask(0, 15);
/*     */   
/*     */   private static final int WRITE_REQUESTED = 65536;
/*     */   
/*     */   private static final int WRITE_REQUIRES_READ = 131072;
/*     */   
/*     */   private static final int WRITE_READY = 262144;
/*     */   
/*     */   private static final int WRITE_SHUT_DOWN = 524288;
/*     */   private static final int WRITE_COMPLETE = 1048576;
/* 104 */   private static final int WRITE_REQUIRES_EXT = Bits.intBitMask(27, 31);
/*     */   
/*     */   private static final int WRITE_SINGLE_EXT = 134217728;
/*     */   
/* 108 */   private static final int WRITE_FLAGS = Bits.intBitMask(16, 31);
/*     */   
/* 110 */   private final ChannelListener<Channel> delegateReadListener = new ChannelListener<Channel>() {
/*     */       public void handleEvent(Channel channel) {
/* 112 */         TranslatingSuspendableChannel.this.handleReadable();
/*     */       }
/*     */       
/*     */       public String toString() {
/* 116 */         return "Read listener for " + TranslatingSuspendableChannel.this;
/*     */       }
/*     */     };
/*     */   
/* 120 */   private final ChannelListener<Channel> delegateWriteListener = new ChannelListener<Channel>() {
/*     */       public void handleEvent(Channel channel) {
/* 122 */         TranslatingSuspendableChannel.this.handleWritable();
/*     */       }
/*     */       
/*     */       public String toString() {
/* 126 */         return "Write listener for " + TranslatingSuspendableChannel.this;
/*     */       }
/*     */     };
/*     */   
/* 130 */   private final ChannelListener<Channel> delegateCloseListener = new ChannelListener<Channel>() {
/*     */       public void handleEvent(Channel channel) {
/* 132 */         IoUtils.safeClose(TranslatingSuspendableChannel.this);
/*     */       }
/*     */       
/*     */       public String toString() {
/* 136 */         return "Close listener for " + TranslatingSuspendableChannel.this;
/*     */       }
/*     */     };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected TranslatingSuspendableChannel(W channel) {
/* 146 */     if (channel == null) {
/* 147 */       throw Messages.msg.nullParameter("channel");
/*     */     }
/* 149 */     this.channel = channel;
/* 150 */     channel.getReadSetter().set(this.delegateReadListener);
/* 151 */     channel.getWriteSetter().set(this.delegateWriteListener);
/* 152 */     channel.getCloseSetter().set(this.delegateCloseListener);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void handleReadable() {
/* 160 */     int oldState = clearFlags(131072);
/* 161 */     if (Bits.allAreSet(oldState, 131072)) {
/* 162 */       unparkWriteWaiters();
/* 163 */       if (Bits.allAreSet(oldState, 65536)) {
/* 164 */         this.channel.wakeupWrites();
/*     */       }
/*     */     } 
/* 167 */     if (Bits.allAreClear(oldState, 4) && Bits.anyAreSet(oldState, 0x2 | READ_REQUIRES_EXT)) {
/* 168 */       this.channel.suspendReads();
/* 169 */       oldState = this.state;
/* 170 */       if (Bits.anyAreSet(oldState, 4) || Bits.allAreClear(oldState, 0x2 | READ_REQUIRES_EXT)) {
/*     */         
/* 172 */         this.channel.resumeReads();
/*     */       } else {
/*     */         return;
/*     */       } 
/*     */     } 
/*     */     do {
/* 178 */       if (Bits.anyAreSet(oldState, 8)) {
/* 179 */         this.channel.suspendReads();
/*     */         return;
/*     */       } 
/* 182 */       if (Bits.allAreClear(oldState, 1)) {
/* 183 */         this.channel.suspendReads();
/* 184 */         oldState = this.state;
/* 185 */         if (Bits.allAreSet(oldState, 1)) {
/*     */           
/* 187 */           this.channel.resumeReads();
/*     */         } else {
/*     */           return;
/*     */         } 
/*     */       } 
/* 192 */       unparkReadWaiters();
/* 193 */       ChannelListener<? super C> listener = this.readListener;
/* 194 */       if (listener == null) {
/*     */         
/* 196 */         oldState = clearFlag(131073) & 0xFFFFFFFE;
/*     */       } else {
/* 198 */         ChannelListeners.invokeChannelListener((Channel)thisTyped(), listener);
/* 199 */         oldState = clearFlags(131072);
/*     */       } 
/* 201 */       if (!Bits.allAreSet(oldState, 131072))
/* 202 */         continue;  unparkWriteWaiters();
/*     */       
/* 204 */       this.channel.wakeupWrites();
/*     */     }
/* 206 */     while (Bits.allAreSet(oldState, 4));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void handleWritable() {
/* 214 */     int oldState = clearFlags(2);
/* 215 */     if (Bits.allAreSet(oldState, 2)) {
/* 216 */       unparkReadWaiters();
/* 217 */       if (Bits.allAreSet(oldState, 1)) {
/* 218 */         this.channel.wakeupReads();
/*     */       }
/*     */     } 
/* 221 */     if (Bits.allAreClear(oldState, 262144) && Bits.anyAreSet(oldState, 0x20000 | WRITE_REQUIRES_EXT)) {
/* 222 */       this.channel.suspendWrites();
/* 223 */       oldState = this.state;
/* 224 */       if (Bits.anyAreSet(oldState, 262144) || Bits.allAreClear(oldState, 0x20000 | WRITE_REQUIRES_EXT)) {
/*     */         
/* 226 */         this.channel.resumeWrites();
/*     */       } else {
/*     */         return;
/*     */       } 
/*     */     } 
/*     */     do {
/* 232 */       if (Bits.anyAreSet(oldState, 1048576)) {
/* 233 */         this.channel.suspendWrites();
/*     */         return;
/*     */       } 
/* 236 */       if (Bits.allAreClear(oldState, 65536)) {
/* 237 */         this.channel.suspendWrites();
/* 238 */         oldState = this.state;
/* 239 */         if (Bits.allAreSet(oldState, 65536)) {
/*     */           
/* 241 */           this.channel.resumeWrites();
/*     */         } else {
/*     */           return;
/*     */         } 
/*     */       } 
/* 246 */       unparkWriteWaiters();
/* 247 */       ChannelListener<? super C> listener = this.writeListener;
/* 248 */       if (listener == null) {
/*     */         
/* 250 */         oldState = clearFlags(65538) & 0xFFFEFFFF;
/*     */       } else {
/* 252 */         ChannelListeners.invokeChannelListener((Channel)thisTyped(), listener);
/* 253 */         oldState = clearFlags(2);
/*     */       } 
/* 255 */       if (!Bits.allAreSet(oldState, 2))
/* 256 */         continue;  unparkReadWaiters();
/*     */       
/* 258 */       this.channel.wakeupReads();
/*     */     }
/* 260 */     while (Bits.allAreSet(oldState, 262144));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void handleClosed() {
/* 268 */     ChannelListeners.invokeChannelListener((Channel)thisTyped(), this.closeListener);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void setReadReady() {
/* 277 */     int oldState = setFlags(4);
/* 278 */     unparkReadWaiters();
/* 279 */     if (Bits.allAreSet(oldState, 4)) {
/*     */       return;
/*     */     }
/*     */     
/* 283 */     if (Bits.allAreSet(oldState, 1) && Bits.anyAreSet(oldState, READ_REQUIRES_EXT | 0x2))
/*     */     {
/* 285 */       this.channel.wakeupReads();
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void clearReadReady() {
/* 293 */     int oldState = clearFlags(4);
/* 294 */     if (Bits.allAreClear(oldState, 4)) {
/*     */       return;
/*     */     }
/*     */     
/* 298 */     if (!Bits.allAreClear(oldState, 1) && !Bits.anyAreSet(oldState, READ_REQUIRES_EXT | 0x2))
/*     */     {
/* 300 */       this.channel.resumeReads();
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void setReadRequiresWrite() {
/* 308 */     int oldState = setFlags(2);
/* 309 */     if (Bits.allAreSet(oldState, 2)) {
/*     */       return;
/*     */     }
/*     */     
/* 313 */     if (Bits.allAreClear(oldState, 0x4 | READ_REQUIRES_EXT))
/*     */     {
/* 315 */       this.channel.resumeWrites();
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected boolean readRequiresWrite() {
/* 323 */     return Bits.allAreSet(this.state, 2);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void clearReadRequiresWrite() {
/* 330 */     int oldState = clearFlags(2);
/* 331 */     if (Bits.allAreClear(oldState, 2)) {
/*     */       return;
/*     */     }
/*     */     
/* 335 */     if (Bits.allAreClear(oldState, READ_REQUIRES_EXT) && Bits.allAreSet(oldState, 1)) {
/* 336 */       if (Bits.allAreSet(oldState, 4)) {
/* 337 */         this.channel.wakeupReads();
/*     */       } else {
/* 339 */         this.channel.resumeReads();
/*     */       } 
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected boolean tryAddReadRequiresExternal() {
/* 350 */     int oldState = addFlag(READ_REQUIRES_EXT, 2048);
/* 351 */     return ((oldState & READ_REQUIRES_EXT) != READ_REQUIRES_EXT);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void removeReadRequiresExternal() {
/* 359 */     clearFlag(2048);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected boolean setReadShutDown() {
/* 368 */     return ((setFlags(8) & 0x80008) == 524288);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void setWriteReady() {
/* 377 */     int oldState = setFlags(262144);
/* 378 */     unparkWriteWaiters();
/* 379 */     if (Bits.allAreSet(oldState, 262144)) {
/*     */       return;
/*     */     }
/*     */     
/* 383 */     if (Bits.allAreSet(oldState, 65536) && Bits.anyAreSet(oldState, WRITE_REQUIRES_EXT | 0x20000))
/*     */     {
/* 385 */       this.channel.wakeupWrites();
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void clearWriteReady() {
/* 393 */     int oldState = clearFlags(262144);
/* 394 */     if (Bits.allAreClear(oldState, 262144)) {
/*     */       return;
/*     */     }
/*     */     
/* 398 */     if (!Bits.allAreClear(oldState, 65536) && !Bits.anyAreSet(oldState, WRITE_REQUIRES_EXT | 0x20000))
/*     */     {
/* 400 */       this.channel.resumeWrites();
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void setWriteRequiresRead() {
/* 408 */     int oldState = setFlags(131072);
/* 409 */     if (Bits.allAreSet(oldState, 131072)) {
/*     */       return;
/*     */     }
/*     */     
/* 413 */     if (Bits.allAreClear(oldState, 0x40000 | WRITE_REQUIRES_EXT))
/*     */     {
/* 415 */       this.channel.resumeReads();
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected boolean writeRequiresRead() {
/* 423 */     return Bits.allAreSet(this.state, 131072);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void clearWriteRequiresRead() {
/* 430 */     int oldState = clearFlags(131072);
/* 431 */     if (Bits.allAreClear(oldState, 131072)) {
/*     */       return;
/*     */     }
/*     */     
/* 435 */     if (Bits.allAreClear(oldState, WRITE_REQUIRES_EXT) && Bits.allAreSet(oldState, 65536)) {
/* 436 */       if (Bits.allAreSet(oldState, 262144)) {
/* 437 */         this.channel.wakeupWrites();
/*     */       } else {
/* 439 */         this.channel.resumeWrites();
/*     */       } 
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected boolean tryAddWriteRequiresExternal() {
/* 450 */     int oldState = addFlag(WRITE_REQUIRES_EXT, 134217728);
/* 451 */     return ((oldState & WRITE_REQUIRES_EXT) != WRITE_REQUIRES_EXT);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void removeWriteRequiresExternal() {
/* 459 */     clearFlag(134217728);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected boolean setWriteShutDown() {
/* 468 */     return ((setFlags(524288) & 0x80008) == 8);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected boolean setClosed() {
/* 479 */     return ((setFlags(524296) & 0x80008) != 524296);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected final C thisTyped() {
/* 489 */     return (C)this;
/*     */   }
/*     */   
/*     */   public void setReadListener(ChannelListener<? super C> readListener) {
/* 493 */     this.readListener = readListener;
/*     */   }
/*     */   
/*     */   public ChannelListener<? super C> getReadListener() {
/* 497 */     return this.readListener;
/*     */   }
/*     */   
/*     */   public void setWriteListener(ChannelListener<? super C> writeListener) {
/* 501 */     this.writeListener = writeListener;
/*     */   }
/*     */   
/*     */   public ChannelListener<? super C> getWriteListener() {
/* 505 */     return this.writeListener;
/*     */   }
/*     */   
/*     */   public void setCloseListener(ChannelListener<? super C> closeListener) {
/* 509 */     this.closeListener = closeListener;
/*     */   }
/*     */   
/*     */   public ChannelListener<? super C> getCloseListener() {
/* 513 */     return this.closeListener;
/*     */   }
/*     */ 
/*     */   
/*     */   public ChannelListener.Setter<C> getCloseSetter() {
/* 518 */     return new CloseListenerSettable.Setter<>(this);
/*     */   }
/*     */ 
/*     */   
/*     */   public ChannelListener.Setter<C> getReadSetter() {
/* 523 */     return new ReadListenerSettable.Setter<>(this);
/*     */   }
/*     */ 
/*     */   
/*     */   public ChannelListener.Setter<C> getWriteSetter() {
/* 528 */     return new WriteListenerSettable.Setter<>(this);
/*     */   }
/*     */ 
/*     */   
/*     */   public void suspendReads() {
/* 533 */     clearFlags(1);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void resumeReads() {
/* 539 */     int oldState = setFlags(1);
/* 540 */     if (Bits.anyAreSet(oldState, 9)) {
/*     */       return;
/*     */     }
/*     */     
/* 544 */     if (Bits.allAreSet(oldState, 4)) {
/*     */       
/* 546 */       this.channel.wakeupReads();
/*     */       return;
/*     */     } 
/* 549 */     if (Bits.allAreClear(oldState, READ_REQUIRES_EXT)) {
/* 550 */       if (Bits.allAreSet(oldState, 2)) {
/* 551 */         this.channel.resumeWrites();
/*     */       } else {
/* 553 */         this.channel.resumeReads();
/*     */       } 
/*     */     }
/*     */   }
/*     */   
/*     */   public boolean isReadResumed() {
/* 559 */     return Bits.allAreSet(this.state, 1);
/*     */   }
/*     */ 
/*     */   
/*     */   public void wakeupReads() {
/* 564 */     if (Bits.anyAreSet(this.state, 8)) {
/*     */       return;
/*     */     }
/* 567 */     setFlags(1);
/* 568 */     this.channel.wakeupReads();
/*     */   }
/*     */ 
/*     */   
/*     */   public void suspendWrites() {
/* 573 */     clearFlags(65536);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void resumeWrites() {
/* 579 */     int oldState = setFlags(65536);
/* 580 */     if (Bits.anyAreSet(oldState, 1114112)) {
/*     */       return;
/*     */     }
/*     */     
/* 584 */     if (Bits.allAreSet(oldState, 262144)) {
/*     */       
/* 586 */       this.channel.wakeupWrites();
/*     */       return;
/*     */     } 
/* 589 */     if (Bits.allAreClear(oldState, WRITE_REQUIRES_EXT)) {
/* 590 */       if (Bits.allAreSet(oldState, 131072)) {
/* 591 */         this.channel.resumeReads();
/*     */       } else {
/* 593 */         this.channel.resumeWrites();
/*     */       } 
/*     */     }
/*     */   }
/*     */   
/*     */   public boolean isWriteResumed() {
/* 599 */     return Bits.allAreSet(this.state, 65536);
/*     */   }
/*     */ 
/*     */   
/*     */   public void wakeupWrites() {
/* 604 */     if (Bits.anyAreSet(this.state, 524288)) {
/*     */       return;
/*     */     }
/* 607 */     setFlags(65536);
/* 608 */     this.channel.wakeupWrites();
/* 609 */     unparkWriteWaiters();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean supportsOption(Option<?> option) {
/* 614 */     return this.channel.supportsOption(option);
/*     */   }
/*     */ 
/*     */   
/*     */   public <T> T getOption(Option<T> option) throws IOException {
/* 619 */     return this.channel.getOption(option);
/*     */   }
/*     */ 
/*     */   
/*     */   public <T> T setOption(Option<T> option, T value) throws IllegalArgumentException, IOException {
/* 624 */     return this.channel.setOption(option, value);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final boolean flush() throws IOException {
/* 635 */     int oldState = stateUpdater.get(this);
/* 636 */     if (Bits.allAreSet(oldState, 1048576)) {
/* 637 */       return this.channel.flush();
/*     */     }
/* 639 */     boolean shutDown = Bits.allAreSet(oldState, 524288);
/* 640 */     if (!flushAction(shutDown)) {
/* 641 */       return false;
/*     */     }
/* 643 */     if (!shutDown) {
/* 644 */       return true;
/*     */     }
/* 646 */     int newState = oldState | 0x100000;
/* 647 */     while (!stateUpdater.compareAndSet(this, oldState, newState)) {
/* 648 */       oldState = stateUpdater.get(this);
/* 649 */       if (Bits.allAreSet(oldState, 1048576)) {
/* 650 */         return this.channel.flush();
/*     */       }
/* 652 */       newState = oldState | 0x100000;
/*     */     } 
/* 654 */     boolean readShutDown = Bits.allAreSet(oldState, 8);
/*     */     try {
/* 656 */       shutdownWritesComplete(readShutDown);
/*     */     } finally {
/* 658 */       if (readShutDown) ChannelListeners.invokeChannelListener((Channel)thisTyped(), this.closeListener); 
/*     */     } 
/* 660 */     return this.channel.flush();
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
/*     */   protected boolean flushAction(boolean shutDown) throws IOException {
/* 674 */     return this.channel.flush();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void shutdownWritesComplete(boolean readShutDown) throws IOException {}
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void shutdownReads() throws IOException {
/* 693 */     int old = setFlags(8);
/* 694 */     if (Bits.allAreClear(old, 8)) {
/* 695 */       boolean writeComplete = Bits.allAreSet(old, 1048576);
/*     */       try {
/* 697 */         shutdownReadsAction(writeComplete);
/*     */       } finally {
/* 699 */         if (writeComplete) {
/* 700 */           ChannelListeners.invokeChannelListener((Channel)thisTyped(), this.closeListener);
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
/*     */   protected void shutdownReadsAction(boolean writeComplete) throws IOException {
/* 713 */     this.channel.shutdownReads();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected boolean isReadShutDown() {
/* 722 */     return Bits.allAreSet(this.state, 8);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void shutdownWrites() throws IOException {
/* 731 */     int old = setFlags(524288);
/* 732 */     if (Bits.allAreClear(old, 524288)) {
/* 733 */       shutdownWritesAction();
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void shutdownWritesAction() throws IOException {
/* 744 */     this.channel.shutdownWrites();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected boolean isWriteShutDown() {
/* 753 */     return Bits.allAreSet(this.state, 524288);
/*     */   }
/*     */   
/*     */   protected boolean isWriteComplete() {
/* 757 */     return Bits.allAreSet(this.state, 1048576);
/*     */   }
/*     */ 
/*     */   
/*     */   public void awaitReadable() throws IOException {
/* 762 */     int oldState = this.state;
/* 763 */     if (Bits.anyAreSet(oldState, 12)) {
/*     */       return;
/*     */     }
/* 766 */     Thread thread = Thread.currentThread();
/* 767 */     Thread next = readWaiterUpdater.getAndSet(this, thread);
/*     */     try {
/* 769 */       if (Bits.anyAreSet(oldState = this.state, 12)) {
/*     */         return;
/*     */       }
/* 772 */       if (Bits.allAreSet(oldState, 2)) {
/* 773 */         this.channel.resumeWrites();
/*     */       } else {
/* 775 */         this.channel.resumeReads();
/*     */       } 
/* 777 */       LockSupport.park(this);
/* 778 */       if (thread.isInterrupted()) {
/* 779 */         throw Messages.msg.interruptedIO();
/*     */       }
/*     */     } finally {
/*     */       
/* 783 */       if (next != null) LockSupport.unpark(next);
/*     */     
/*     */     } 
/*     */   }
/*     */   
/*     */   public void awaitReadable(long time, TimeUnit timeUnit) throws IOException {
/* 789 */     int oldState = this.state;
/* 790 */     if (Bits.anyAreSet(oldState, 12)) {
/*     */       return;
/*     */     }
/* 793 */     Thread thread = Thread.currentThread();
/* 794 */     Thread next = readWaiterUpdater.getAndSet(this, thread);
/* 795 */     long duration = timeUnit.toNanos(time);
/*     */     try {
/* 797 */       if (Bits.anyAreSet(oldState = this.state, 12)) {
/*     */         return;
/*     */       }
/* 800 */       if (Bits.allAreSet(oldState, 2)) {
/* 801 */         this.channel.resumeWrites();
/*     */       } else {
/* 803 */         this.channel.resumeReads();
/*     */       } 
/* 805 */       LockSupport.parkNanos(this, duration);
/* 806 */       if (thread.isInterrupted()) {
/* 807 */         throw Messages.msg.interruptedIO();
/*     */       }
/*     */     } finally {
/*     */       
/* 811 */       if (next != null) LockSupport.unpark(next); 
/*     */     } 
/*     */   }
/*     */   
/*     */   @Deprecated
/*     */   public XnioExecutor getReadThread() {
/* 817 */     return this.channel.getReadThread();
/*     */   }
/*     */ 
/*     */   
/*     */   public void awaitWritable() throws IOException {
/* 822 */     int oldState = this.state;
/* 823 */     if (Bits.anyAreSet(oldState, 786432)) {
/*     */       return;
/*     */     }
/* 826 */     Thread thread = Thread.currentThread();
/* 827 */     Thread next = writeWaiterUpdater.getAndSet(this, thread);
/*     */     try {
/* 829 */       if (Bits.anyAreSet(oldState = this.state, 786432)) {
/*     */         return;
/*     */       }
/* 832 */       if (Bits.allAreSet(oldState, 131072)) {
/* 833 */         this.channel.resumeReads();
/*     */       } else {
/* 835 */         this.channel.resumeWrites();
/*     */       } 
/* 837 */       LockSupport.park(this);
/* 838 */       if (thread.isInterrupted()) {
/* 839 */         throw Messages.msg.interruptedIO();
/*     */       }
/*     */     } finally {
/*     */       
/* 843 */       if (next != null) LockSupport.unpark(next);
/*     */     
/*     */     } 
/*     */   }
/*     */   
/*     */   public void awaitWritable(long time, TimeUnit timeUnit) throws IOException {
/* 849 */     int oldState = this.state;
/* 850 */     if (Bits.anyAreSet(oldState, 786432)) {
/*     */       return;
/*     */     }
/* 853 */     Thread thread = Thread.currentThread();
/* 854 */     Thread next = writeWaiterUpdater.getAndSet(this, thread);
/* 855 */     long duration = timeUnit.toNanos(time);
/*     */     try {
/* 857 */       if (Bits.anyAreSet(oldState = this.state, 786432)) {
/*     */         return;
/*     */       }
/* 860 */       if (Bits.allAreSet(oldState, 131072)) {
/* 861 */         this.channel.resumeReads();
/*     */       } else {
/* 863 */         this.channel.resumeWrites();
/*     */       } 
/* 865 */       LockSupport.parkNanos(this, duration);
/* 866 */       if (thread.isInterrupted()) {
/* 867 */         throw Messages.msg.interruptedIO();
/*     */       }
/*     */     } finally {
/*     */       
/* 871 */       if (next != null) LockSupport.unpark(next); 
/*     */     } 
/*     */   }
/*     */   
/*     */   private void unparkReadWaiters() {
/* 876 */     Thread waiter = readWaiterUpdater.getAndSet(this, null);
/* 877 */     if (waiter != null) {
/* 878 */       LockSupport.unpark(waiter);
/*     */     }
/*     */   }
/*     */   
/*     */   private void unparkWriteWaiters() {
/* 883 */     Thread waiter = writeWaiterUpdater.getAndSet(this, null);
/* 884 */     if (waiter != null) {
/* 885 */       LockSupport.unpark(waiter);
/*     */     }
/*     */   }
/*     */   
/*     */   @Deprecated
/*     */   public XnioExecutor getWriteThread() {
/* 891 */     return this.channel.getWriteThread();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void close() throws IOException {
/* 900 */     int old = setFlags(1572872);
/* 901 */     boolean readShutDown = Bits.allAreSet(old, 8), writeShutDown = Bits.allAreSet(old, 1048576);
/* 902 */     if (!readShutDown || !writeShutDown) {
/* 903 */       try { closeAction(readShutDown, writeShutDown); }
/*     */       finally
/* 905 */       { ChannelListeners.invokeChannelListener((Channel)thisTyped(), this.closeListener); }
/*     */     
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
/*     */   protected void closeAction(boolean readShutDown, boolean writeShutDown) throws IOException {
/* 918 */     this.channel.close();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isOpen() {
/* 923 */     return !Bits.allAreSet(this.state, 1048584);
/*     */   }
/*     */ 
/*     */   
/*     */   public W getChannel() {
/* 928 */     return this.channel;
/*     */   }
/*     */ 
/*     */   
/*     */   public XnioWorker getWorker() {
/* 933 */     return this.channel.getWorker();
/*     */   }
/*     */ 
/*     */   
/*     */   public XnioIoThread getIoThread() {
/* 938 */     return this.channel.getIoThread();
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 943 */     return getClass().getName() + " around " + this.channel;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private int setFlags(int flags) {
/*     */     while (true) {
/* 951 */       int oldState = this.state;
/* 952 */       if ((oldState & flags) == flags) {
/* 953 */         return oldState;
/*     */       }
/* 955 */       if (stateUpdater.compareAndSet(this, oldState, oldState | flags))
/* 956 */         return oldState; 
/*     */     } 
/*     */   }
/*     */   
/*     */   private int clearFlags(int flags) {
/*     */     while (true) {
/* 962 */       int oldState = this.state;
/* 963 */       if ((oldState & flags) == 0) {
/* 964 */         return oldState;
/*     */       }
/* 966 */       if (stateUpdater.compareAndSet(this, oldState, oldState & (flags ^ 0xFFFFFFFF)))
/* 967 */         return oldState; 
/*     */     } 
/*     */   }
/*     */   
/*     */   private int addFlag(int mask, int count) {
/*     */     while (true) {
/* 973 */       int oldState = this.state;
/* 974 */       if ((oldState & mask) == mask) {
/* 975 */         return oldState;
/*     */       }
/* 977 */       if (stateUpdater.compareAndSet(this, oldState, oldState + count))
/* 978 */         return oldState; 
/*     */     } 
/*     */   }
/*     */   private int clearFlag(int count) {
/* 982 */     return stateUpdater.getAndAdd(this, -count);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\channels\TranslatingSuspendableChannel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */