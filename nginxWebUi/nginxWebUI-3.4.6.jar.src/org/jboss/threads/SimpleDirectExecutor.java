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
/*    */ 
/*    */ class SimpleDirectExecutor
/*    */   implements Executor
/*    */ {
/* 25 */   static final SimpleDirectExecutor INSTANCE = new SimpleDirectExecutor();
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void execute(Runnable command) {
/* 31 */     command.run();
/*    */   }
/*    */   
/*    */   public String toString() {
/* 35 */     return "Direct executor";
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\jboss\threads\SimpleDirectExecutor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */