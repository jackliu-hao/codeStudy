/*    */ package org.jboss.threads;
/*    */ 
/*    */ import java.util.concurrent.Executor;
/*    */ import java.util.concurrent.RejectedExecutionException;
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
/*    */ class RejectingExecutor
/*    */   implements Executor
/*    */ {
/* 25 */   static final RejectingExecutor INSTANCE = new RejectingExecutor();
/*    */   
/*    */   private final String message;
/*    */   
/*    */   private RejectingExecutor() {
/* 30 */     this.message = null;
/*    */   }
/*    */   
/*    */   RejectingExecutor(String message) {
/* 34 */     this.message = message;
/*    */   }
/*    */   
/*    */   public void execute(Runnable command) {
/* 38 */     throw new RejectedExecutionException(this.message);
/*    */   }
/*    */   
/*    */   public String toString() {
/* 42 */     return "Rejecting executor";
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\jboss\threads\RejectingExecutor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */