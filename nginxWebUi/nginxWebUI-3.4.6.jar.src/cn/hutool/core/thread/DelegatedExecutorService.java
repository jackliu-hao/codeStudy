/*    */ package cn.hutool.core.thread;
/*    */ 
/*    */ import cn.hutool.core.lang.Assert;
/*    */ import java.util.Collection;
/*    */ import java.util.List;
/*    */ import java.util.concurrent.AbstractExecutorService;
/*    */ import java.util.concurrent.Callable;
/*    */ import java.util.concurrent.ExecutionException;
/*    */ import java.util.concurrent.ExecutorService;
/*    */ import java.util.concurrent.Future;
/*    */ import java.util.concurrent.TimeUnit;
/*    */ import java.util.concurrent.TimeoutException;
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
/*    */ public class DelegatedExecutorService
/*    */   extends AbstractExecutorService
/*    */ {
/*    */   private final ExecutorService e;
/*    */   
/*    */   DelegatedExecutorService(ExecutorService executor) {
/* 29 */     Assert.notNull(executor, "executor must be not null !", new Object[0]);
/* 30 */     this.e = executor;
/*    */   }
/*    */ 
/*    */   
/*    */   public void execute(Runnable command) {
/* 35 */     this.e.execute(command);
/*    */   }
/*    */ 
/*    */   
/*    */   public void shutdown() {
/* 40 */     this.e.shutdown();
/*    */   }
/*    */ 
/*    */   
/*    */   public List<Runnable> shutdownNow() {
/* 45 */     return this.e.shutdownNow();
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean isShutdown() {
/* 50 */     return this.e.isShutdown();
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean isTerminated() {
/* 55 */     return this.e.isTerminated();
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
/* 60 */     return this.e.awaitTermination(timeout, unit);
/*    */   }
/*    */ 
/*    */   
/*    */   public Future<?> submit(Runnable task) {
/* 65 */     return this.e.submit(task);
/*    */   }
/*    */ 
/*    */   
/*    */   public <T> Future<T> submit(Callable<T> task) {
/* 70 */     return this.e.submit(task);
/*    */   }
/*    */ 
/*    */   
/*    */   public <T> Future<T> submit(Runnable task, T result) {
/* 75 */     return this.e.submit(task, result);
/*    */   }
/*    */ 
/*    */   
/*    */   public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException {
/* 80 */     return this.e.invokeAll(tasks);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException {
/* 86 */     return this.e.invokeAll(tasks, timeout, unit);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
/* 92 */     return this.e.invokeAny(tasks);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
/* 98 */     return this.e.invokeAny(tasks, timeout, unit);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\thread\DelegatedExecutorService.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */