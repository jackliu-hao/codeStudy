/*     */ package io.undertow.util;
/*     */ 
/*     */ import io.undertow.UndertowMessages;
/*     */ import io.undertow.connector.PooledByteBuffer;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
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
/*     */ public class ReferenceCountedPooled
/*     */   implements PooledByteBuffer
/*     */ {
/*     */   private final PooledByteBuffer underlying;
/*     */   private volatile int referenceCount;
/*     */   boolean mainFreed = false;
/*  43 */   private ByteBuffer slice = null;
/*     */   
/*     */   private final FreeNotifier freeNotifier;
/*  46 */   private static final AtomicIntegerFieldUpdater<ReferenceCountedPooled> referenceCountUpdater = AtomicIntegerFieldUpdater.newUpdater(ReferenceCountedPooled.class, "referenceCount");
/*     */   
/*     */   public ReferenceCountedPooled(PooledByteBuffer underlying, int referenceCount) {
/*  49 */     this(underlying, referenceCount, null);
/*     */   }
/*     */   
/*     */   public ReferenceCountedPooled(PooledByteBuffer underlying, int referenceCount, FreeNotifier freeNotifier) {
/*  53 */     this.underlying = underlying;
/*  54 */     this.referenceCount = referenceCount;
/*  55 */     this.freeNotifier = freeNotifier;
/*     */   }
/*     */ 
/*     */   
/*     */   public void close() {
/*  60 */     if (this.mainFreed) {
/*     */       return;
/*     */     }
/*  63 */     this.mainFreed = true;
/*  64 */     freeInternal();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isOpen() {
/*  69 */     return !this.mainFreed;
/*     */   }
/*     */   
/*     */   public boolean isFreed() {
/*  73 */     return this.mainFreed;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean tryUnfree() {
/*     */     while (true) {
/*  79 */       int refs = referenceCountUpdater.get(this);
/*  80 */       if (refs <= 0) {
/*  81 */         return false;
/*     */       }
/*  83 */       if (referenceCountUpdater.compareAndSet(this, refs, refs + 1)) {
/*  84 */         ByteBuffer resource = (this.slice != null) ? this.slice : this.underlying.getBuffer();
/*  85 */         resource.position(resource.limit());
/*  86 */         resource.limit(resource.capacity());
/*  87 */         this.slice = resource.slice();
/*  88 */         this.mainFreed = false;
/*  89 */         return true;
/*     */       } 
/*     */     } 
/*     */   } private void freeInternal() {
/*  93 */     if (referenceCountUpdater.decrementAndGet(this) == 0) {
/*  94 */       this.underlying.close();
/*  95 */       if (this.freeNotifier != null) {
/*  96 */         this.freeNotifier.freed();
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuffer getBuffer() throws IllegalStateException {
/* 103 */     if (this.mainFreed) {
/* 104 */       throw UndertowMessages.MESSAGES.bufferAlreadyFreed();
/*     */     }
/* 106 */     if (this.slice != null) {
/* 107 */       return this.slice;
/*     */     }
/* 109 */     return this.underlying.getBuffer();
/*     */   }
/*     */   
/*     */   public PooledByteBuffer createView(int viewSize) {
/* 113 */     ByteBuffer newView = getBuffer().duplicate();
/* 114 */     newView.limit(newView.position() + viewSize);
/* 115 */     final ByteBuffer newValue = newView.slice();
/* 116 */     ByteBuffer newUnderlying = getBuffer().duplicate();
/* 117 */     newUnderlying.position(newUnderlying.position() + viewSize);
/*     */     
/* 119 */     int oldRemaining = newUnderlying.remaining();
/* 120 */     newUnderlying.limit(newUnderlying.capacity());
/* 121 */     newUnderlying = newUnderlying.slice();
/* 122 */     newUnderlying.limit(newUnderlying.position() + oldRemaining);
/* 123 */     this.slice = newUnderlying;
/*     */     
/* 125 */     increaseReferenceCount();
/* 126 */     return new PooledByteBuffer()
/*     */       {
/*     */         boolean free = false;
/*     */ 
/*     */ 
/*     */         
/*     */         public void close() {
/* 133 */           if (!this.free) {
/* 134 */             this.free = true;
/* 135 */             ReferenceCountedPooled.this.freeInternal();
/*     */           } 
/*     */         }
/*     */ 
/*     */         
/*     */         public boolean isOpen() {
/* 141 */           return !this.free;
/*     */         }
/*     */ 
/*     */         
/*     */         public ByteBuffer getBuffer() throws IllegalStateException {
/* 146 */           if (this.free) {
/* 147 */             throw UndertowMessages.MESSAGES.bufferAlreadyFreed();
/*     */           }
/* 149 */           return newValue;
/*     */         }
/*     */ 
/*     */         
/*     */         public String toString() {
/* 154 */           return "ReferenceCountedPooled$view{buffer=" + newValue + "free=" + this.free + "underlying=" + ReferenceCountedPooled.this
/*     */ 
/*     */             
/* 157 */             .underlying + ", referenceCount=" + ReferenceCountedPooled.this
/* 158 */             .referenceCount + ", mainFreed=" + ReferenceCountedPooled.this.mainFreed + ", slice=" + ReferenceCountedPooled.this
/*     */             
/* 160 */             .slice + '}';
/*     */         }
/*     */       };
/*     */   }
/*     */   public static interface FreeNotifier {
/*     */     void freed(); }
/*     */   public PooledByteBuffer createView() {
/* 167 */     return createView(getBuffer().remaining());
/*     */   }
/*     */   
/*     */   public void increaseReferenceCount() {
/*     */     int val;
/*     */     do {
/* 173 */       val = referenceCountUpdater.get(this);
/* 174 */       if (val == 0)
/*     */       {
/*     */         
/* 177 */         throw UndertowMessages.MESSAGES.objectWasFreed();
/*     */       }
/* 179 */     } while (!referenceCountUpdater.compareAndSet(this, val, val + 1));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/* 188 */     return "ReferenceCountedPooled{underlying=" + this.underlying + ", referenceCount=" + this.referenceCount + ", mainFreed=" + this.mainFreed + ", slice=" + this.slice + '}';
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\underto\\util\ReferenceCountedPooled.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */