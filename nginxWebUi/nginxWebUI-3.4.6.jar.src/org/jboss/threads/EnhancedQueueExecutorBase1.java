/*     */ package org.jboss.threads;
/*     */ 
/*     */ import org.wildfly.common.annotation.NotNull;
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
/*     */ abstract class EnhancedQueueExecutorBase1
/*     */   extends EnhancedQueueExecutorBase0
/*     */ {
/*     */   static final long tailLockOffset;
/*     */   static final long tailOffset;
/*     */   
/*     */   static {
/*     */     try {
/*  36 */       tailLockOffset = JBossExecutors.unsafe.objectFieldOffset(EnhancedQueueExecutorBase1.class.getDeclaredField("tailLock"));
/*  37 */       tailOffset = JBossExecutors.unsafe.objectFieldOffset(EnhancedQueueExecutorBase1.class.getDeclaredField("tail"));
/*  38 */     } catch (NoSuchFieldException e) {
/*  39 */       throw new NoSuchFieldError(e.getMessage());
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
/*  51 */   static final boolean COMBINED_LOCK = readBooleanPropertyPrefixed("combined-lock", false);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  57 */   static final boolean TAIL_SPIN = readBooleanPropertyPrefixed("tail-spin", false);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  63 */   static final boolean TAIL_LOCK = readBooleanPropertyPrefixed("tail-lock", true);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   volatile int tailLock;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   volatile EnhancedQueueExecutor.TaskNode tail;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   void compareAndSetTail(EnhancedQueueExecutor.TaskNode expect, EnhancedQueueExecutor.TaskNode update) {
/*  87 */     if (this.tail == expect) JBossExecutors.unsafe.compareAndSwapObject(this, tailOffset, expect, update);
/*     */   
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   final void lockTail() {
/*  95 */     int spins = 0;
/*     */     while (true) {
/*  97 */       if (this.tailLock == 0 && JBossExecutors.unsafe.compareAndSwapInt(this, tailLockOffset, 0, 1)) {
/*     */         return;
/*     */       }
/* 100 */       if (spins == EnhancedQueueExecutorBase3.YIELD_SPINS) {
/* 101 */         spins = 0;
/* 102 */         Thread.yield(); continue;
/*     */       } 
/* 104 */       spins++;
/* 105 */       JDKSpecific.onSpinWait();
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   final void unlockTail() {
/* 111 */     assert this.tailLock == 1;
/* 112 */     this.tailLock = 0;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\jboss\threads\EnhancedQueueExecutorBase1.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */