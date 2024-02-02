/*     */ package org.h2.mvstore;
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
/*     */ public final class RootReference<K, V>
/*     */ {
/*     */   public final Page<K, V> root;
/*     */   public final long version;
/*     */   private final byte holdCount;
/*     */   private final long ownerId;
/*     */   volatile RootReference<K, V> previous;
/*     */   final long updateCounter;
/*     */   final long updateAttemptCounter;
/*     */   private final byte appendCounter;
/*     */   
/*     */   RootReference(Page<K, V> paramPage, long paramLong) {
/*  56 */     this.root = paramPage;
/*  57 */     this.version = paramLong;
/*  58 */     this.previous = null;
/*  59 */     this.updateCounter = 1L;
/*  60 */     this.updateAttemptCounter = 1L;
/*  61 */     this.holdCount = 0;
/*  62 */     this.ownerId = 0L;
/*  63 */     this.appendCounter = 0;
/*     */   }
/*     */   
/*     */   private RootReference(RootReference<K, V> paramRootReference, Page<K, V> paramPage, long paramLong) {
/*  67 */     this.root = paramPage;
/*  68 */     this.version = paramRootReference.version;
/*  69 */     this.previous = paramRootReference.previous;
/*  70 */     this.updateCounter = paramRootReference.updateCounter + 1L;
/*  71 */     this.updateAttemptCounter = paramRootReference.updateAttemptCounter + paramLong;
/*  72 */     this.holdCount = 0;
/*  73 */     this.ownerId = 0L;
/*  74 */     this.appendCounter = paramRootReference.appendCounter;
/*     */   }
/*     */ 
/*     */   
/*     */   private RootReference(RootReference<K, V> paramRootReference, int paramInt) {
/*  79 */     this.root = paramRootReference.root;
/*  80 */     this.version = paramRootReference.version;
/*  81 */     this.previous = paramRootReference.previous;
/*  82 */     this.updateCounter = paramRootReference.updateCounter + 1L;
/*  83 */     this.updateAttemptCounter = paramRootReference.updateAttemptCounter + paramInt;
/*  84 */     assert paramRootReference.holdCount == 0 || paramRootReference.ownerId == Thread.currentThread().getId() : 
/*  85 */       Thread.currentThread().getId() + " " + paramRootReference;
/*  86 */     this.holdCount = (byte)(paramRootReference.holdCount + 1);
/*  87 */     this.ownerId = Thread.currentThread().getId();
/*  88 */     this.appendCounter = paramRootReference.appendCounter;
/*     */   }
/*     */ 
/*     */   
/*     */   private RootReference(RootReference<K, V> paramRootReference, Page<K, V> paramPage, boolean paramBoolean, int paramInt) {
/*  93 */     this.root = paramPage;
/*  94 */     this.version = paramRootReference.version;
/*  95 */     this.previous = paramRootReference.previous;
/*  96 */     this.updateCounter = paramRootReference.updateCounter;
/*  97 */     this.updateAttemptCounter = paramRootReference.updateAttemptCounter;
/*  98 */     assert paramRootReference.holdCount > 0 && paramRootReference.ownerId == Thread.currentThread().getId() : 
/*  99 */       Thread.currentThread().getId() + " " + paramRootReference;
/* 100 */     this.holdCount = (byte)(paramRootReference.holdCount - (paramBoolean ? 0 : 1));
/* 101 */     this.ownerId = (this.holdCount == 0) ? 0L : Thread.currentThread().getId();
/* 102 */     this.appendCounter = (byte)paramInt;
/*     */   }
/*     */ 
/*     */   
/*     */   private RootReference(RootReference<K, V> paramRootReference, long paramLong, int paramInt) {
/* 107 */     RootReference<K, V> rootReference1 = paramRootReference;
/*     */     RootReference<K, V> rootReference2;
/* 109 */     while ((rootReference2 = rootReference1.previous) != null && rootReference2.root == paramRootReference.root) {
/* 110 */       rootReference1 = rootReference2;
/*     */     }
/* 112 */     this.root = paramRootReference.root;
/* 113 */     this.version = paramLong;
/* 114 */     this.previous = rootReference1;
/* 115 */     this.updateCounter = paramRootReference.updateCounter + 1L;
/* 116 */     this.updateAttemptCounter = paramRootReference.updateAttemptCounter + paramInt;
/* 117 */     this.holdCount = (paramRootReference.holdCount == 0) ? 0 : (byte)(paramRootReference.holdCount - 1);
/* 118 */     this.ownerId = (this.holdCount == 0) ? 0L : paramRootReference.ownerId;
/* 119 */     assert paramRootReference.appendCounter == 0;
/* 120 */     this.appendCounter = 0;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   RootReference<K, V> updateRootPage(Page<K, V> paramPage, long paramLong) {
/* 131 */     return isFree() ? tryUpdate(new RootReference(this, paramPage, paramLong)) : null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   RootReference<K, V> tryLock(int paramInt) {
/* 141 */     return canUpdate() ? tryUpdate(new RootReference(this, paramInt)) : null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   RootReference<K, V> tryUnlockAndUpdateVersion(long paramLong, int paramInt) {
/* 152 */     return canUpdate() ? tryUpdate(new RootReference(this, paramLong, paramInt)) : null;
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
/*     */   RootReference<K, V> updatePageAndLockedStatus(Page<K, V> paramPage, boolean paramBoolean, int paramInt) {
/* 164 */     return canUpdate() ? tryUpdate(new RootReference(this, paramPage, paramBoolean, paramInt)) : null;
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
/*     */   void removeUnusedOldVersions(long paramLong) {
/* 178 */     for (RootReference<K, V> rootReference = this; rootReference != null; rootReference = rootReference.previous) {
/* 179 */       if (rootReference.version < paramLong) {
/*     */         RootReference<K, V> rootReference1;
/* 181 */         assert (rootReference1 = rootReference.previous) == null || rootReference1.getAppendCounter() == 0 : paramLong + " " + rootReference.previous;
/*     */         
/* 183 */         rootReference.previous = null;
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   boolean isLocked() {
/* 189 */     return (this.holdCount != 0);
/*     */   }
/*     */   
/*     */   private boolean isFree() {
/* 193 */     return (this.holdCount == 0);
/*     */   }
/*     */ 
/*     */   
/*     */   private boolean canUpdate() {
/* 198 */     return (isFree() || this.ownerId == Thread.currentThread().getId());
/*     */   }
/*     */   
/*     */   public boolean isLockedByCurrentThread() {
/* 202 */     return (this.holdCount != 0 && this.ownerId == Thread.currentThread().getId());
/*     */   }
/*     */   
/*     */   private RootReference<K, V> tryUpdate(RootReference<K, V> paramRootReference) {
/* 206 */     assert canUpdate();
/* 207 */     return this.root.map.compareAndSetRoot(this, paramRootReference) ? paramRootReference : null;
/*     */   }
/*     */   
/*     */   long getVersion() {
/* 211 */     RootReference<K, V> rootReference = this.previous;
/* 212 */     return (rootReference == null || rootReference.root != this.root || rootReference.appendCounter != this.appendCounter) ? this.version : rootReference
/*     */       
/* 214 */       .getVersion();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   boolean hasChangesSince(long paramLong, boolean paramBoolean) {
/* 225 */     return ((paramBoolean && (this.root.isSaved() ? (getAppendCounter() > 0) : (getTotalCount() > 0L))) || 
/* 226 */       getVersion() > paramLong);
/*     */   }
/*     */   
/*     */   int getAppendCounter() {
/* 230 */     return this.appendCounter & 0xFF;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean needFlush() {
/* 239 */     return (this.appendCounter != 0);
/*     */   }
/*     */   
/*     */   public long getTotalCount() {
/* 243 */     return this.root.getTotalCount() + getAppendCounter();
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 248 */     return "RootReference(" + System.identityHashCode(this.root) + ", v=" + this.version + ", owner=" + this.ownerId + (
/*     */       
/* 250 */       (this.ownerId == Thread.currentThread().getId()) ? "(current)" : "") + ", holdCnt=" + this.holdCount + ", keys=" + this.root
/*     */       
/* 252 */       .getTotalCount() + ", append=" + 
/* 253 */       getAppendCounter() + ")";
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\mvstore\RootReference.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */