/*    */ package org.jboss.threads;
/*    */ 
/*    */ import java.util.concurrent.Executor;
/*    */ import java.util.concurrent.RejectedExecutionHandler;
/*    */ import java.util.concurrent.ThreadPoolExecutor;
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
/*    */ class HandoffRejectedExecutionHandler
/*    */   implements RejectedExecutionHandler
/*    */ {
/*    */   private final Executor target;
/*    */   
/*    */   HandoffRejectedExecutionHandler(Executor target) {
/* 30 */     this.target = target;
/*    */   }
/*    */   
/*    */   public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
/* 34 */     this.target.execute(r);
/*    */   }
/*    */   
/*    */   public String toString() {
/* 38 */     return String.format("%s -> %s", new Object[] { super.toString(), this.target });
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\jboss\threads\HandoffRejectedExecutionHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */