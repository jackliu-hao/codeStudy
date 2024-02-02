/*    */ package org.jboss.threads;
/*    */ 
/*    */ import java.util.concurrent.Executor;
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
/*    */ class DiscardingExecutor
/*    */   implements Executor
/*    */ {
/* 24 */   static final DiscardingExecutor INSTANCE = new DiscardingExecutor();
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void execute(Runnable command) {}
/*    */ 
/*    */ 
/*    */   
/*    */   public String toString() {
/* 34 */     return "Discarding executor";
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\jboss\threads\DiscardingExecutor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */