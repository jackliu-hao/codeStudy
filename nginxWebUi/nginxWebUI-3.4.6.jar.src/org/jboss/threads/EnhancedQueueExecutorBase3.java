/*    */ package org.jboss.threads;
/*    */ 
/*    */ import org.wildfly.common.annotation.NotNull;
/*    */ import org.wildfly.common.cpu.ProcessorInfo;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ abstract class EnhancedQueueExecutorBase3
/*    */   extends EnhancedQueueExecutorBase2
/*    */ {
/*    */   static final long headOffset;
/*    */   
/*    */   static {
/*    */     try {
/* 34 */       headOffset = JBossExecutors.unsafe.objectFieldOffset(EnhancedQueueExecutorBase3.class.getDeclaredField("head"));
/* 35 */     } catch (NoSuchFieldException e) {
/* 36 */       throw new NoSuchFieldError(e.getMessage());
/*    */     } 
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/* 48 */   static final boolean HEAD_LOCK = readBooleanPropertyPrefixed("head-lock", false);
/*    */ 
/*    */ 
/*    */ 
/*    */   
/* 53 */   static final boolean HEAD_SPIN = readBooleanPropertyPrefixed("head-spin", true);
/*    */ 
/*    */ 
/*    */ 
/*    */   
/* 58 */   static final int YIELD_SPINS = readIntPropertyPrefixed("lock-yield-spins", (ProcessorInfo.availableProcessors() == 1) ? 0 : 128);
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   @NotNull
/* 73 */   volatile EnhancedQueueExecutor.TaskNode head = this.tail = new EnhancedQueueExecutor.TaskNode(null);
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   boolean compareAndSetHead(EnhancedQueueExecutor.TaskNode expect, EnhancedQueueExecutor.TaskNode update) {
/* 81 */     return JBossExecutors.unsafe.compareAndSwapObject(this, headOffset, expect, update);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\jboss\threads\EnhancedQueueExecutorBase3.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */