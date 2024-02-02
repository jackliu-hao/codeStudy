/*    */ package org.jboss.threads;
/*    */ 
/*    */ import java.util.concurrent.Callable;
/*    */ import java.util.concurrent.ScheduledExecutorService;
/*    */ import java.util.concurrent.ScheduledFuture;
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
/*    */ class DelegatingScheduledExecutorService
/*    */   extends DelegatingExecutorService
/*    */   implements ScheduledExecutorService
/*    */ {
/*    */   private final ScheduledExecutorService delegate;
/*    */   
/*    */   DelegatingScheduledExecutorService(ScheduledExecutorService delegate) {
/* 33 */     super(delegate);
/* 34 */     this.delegate = delegate;
/*    */   }
/*    */   
/*    */   public ScheduledFuture<?> schedule(Runnable command, long delay, TimeUnit unit) {
/* 38 */     return this.delegate.schedule(command, delay, unit);
/*    */   }
/*    */   
/*    */   public <V> ScheduledFuture<V> schedule(Callable<V> callable, long delay, TimeUnit unit) {
/* 42 */     return this.delegate.schedule(callable, delay, unit);
/*    */   }
/*    */   
/*    */   public ScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit) {
/* 46 */     return this.delegate.scheduleAtFixedRate(command, initialDelay, period, unit);
/*    */   }
/*    */   
/*    */   public ScheduledFuture<?> scheduleWithFixedDelay(Runnable command, long initialDelay, long delay, TimeUnit unit) {
/* 50 */     return this.delegate.scheduleWithFixedDelay(command, initialDelay, delay, unit);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\jboss\threads\DelegatingScheduledExecutorService.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */