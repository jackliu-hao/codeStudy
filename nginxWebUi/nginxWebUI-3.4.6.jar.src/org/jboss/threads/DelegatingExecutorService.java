/*    */ package org.jboss.threads;
/*    */ 
/*    */ import java.util.List;
/*    */ import java.util.concurrent.AbstractExecutorService;
/*    */ import java.util.concurrent.Executor;
/*    */ import java.util.concurrent.ExecutorService;
/*    */ import java.util.concurrent.TimeUnit;
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
/*    */ class DelegatingExecutorService
/*    */   extends AbstractExecutorService
/*    */   implements ExecutorService
/*    */ {
/*    */   private final Executor delegate;
/*    */   
/*    */   DelegatingExecutorService(Executor delegate) {
/* 34 */     this.delegate = delegate;
/*    */   }
/*    */   
/*    */   public void execute(Runnable command) {
/* 38 */     this.delegate.execute(command);
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean isShutdown() {
/* 43 */     return false;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean isTerminated() {
/* 48 */     return false;
/*    */   }
/*    */   
/*    */   public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
/* 52 */     return false;
/*    */   }
/*    */   
/*    */   public void shutdown() {
/* 56 */     throw Messages.msg.notAllowedContainerManaged("shutdown");
/*    */   }
/*    */   
/*    */   public List<Runnable> shutdownNow() {
/* 60 */     throw Messages.msg.notAllowedContainerManaged("shutdownNow");
/*    */   }
/*    */   
/*    */   public String toString() {
/* 64 */     return String.format("%s -> %s", new Object[] { super.toString(), this.delegate });
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\jboss\threads\DelegatingExecutorService.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */