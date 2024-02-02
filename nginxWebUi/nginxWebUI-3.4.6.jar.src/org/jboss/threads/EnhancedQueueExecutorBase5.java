/*    */ package org.jboss.threads;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ abstract class EnhancedQueueExecutorBase5
/*    */   extends EnhancedQueueExecutorBase4
/*    */ {
/*    */   static final long threadStatusOffset;
/*    */   volatile long threadStatus;
/*    */   
/*    */   static {
/*    */     try {
/* 35 */       threadStatusOffset = JBossExecutors.unsafe.objectFieldOffset(EnhancedQueueExecutorBase5.class.getDeclaredField("threadStatus"));
/* 36 */     } catch (NoSuchFieldException e) {
/* 37 */       throw new NoSuchFieldError(e.getMessage());
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
/*    */   boolean compareAndSetThreadStatus(long expect, long update) {
/* 69 */     return JBossExecutors.unsafe.compareAndSwapLong(this, threadStatusOffset, expect, update);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\jboss\threads\EnhancedQueueExecutorBase5.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */