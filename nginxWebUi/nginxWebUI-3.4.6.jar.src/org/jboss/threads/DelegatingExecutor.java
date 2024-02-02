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
/*    */ 
/*    */ 
/*    */ 
/*    */ class DelegatingExecutor
/*    */   implements Executor
/*    */ {
/*    */   private final Executor delegate;
/*    */   
/*    */   DelegatingExecutor(Executor delegate) {
/* 31 */     this.delegate = delegate;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void execute(Runnable command) {
/* 40 */     this.delegate.execute(command);
/*    */   }
/*    */   
/*    */   public String toString() {
/* 44 */     return String.format("%s -> %s", new Object[] { super.toString(), this.delegate });
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\jboss\threads\DelegatingExecutor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */