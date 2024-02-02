/*    */ package org.xnio;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.util.concurrent.CancellationException;
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
/*    */ public class FinishedIoFuture<T>
/*    */   implements IoFuture<T>
/*    */ {
/*    */   private final T result;
/*    */   
/*    */   public FinishedIoFuture(T result) {
/* 25 */     this.result = result;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public IoFuture<T> cancel() {
/* 34 */     return this;
/*    */   }
/*    */ 
/*    */   
/*    */   public IoFuture.Status getStatus() {
/* 39 */     return IoFuture.Status.DONE;
/*    */   }
/*    */ 
/*    */   
/*    */   public IoFuture.Status await() {
/* 44 */     return IoFuture.Status.DONE;
/*    */   }
/*    */ 
/*    */   
/*    */   public IoFuture.Status await(long time, TimeUnit timeUnit) {
/* 49 */     return IoFuture.Status.DONE;
/*    */   }
/*    */ 
/*    */   
/*    */   public IoFuture.Status awaitInterruptibly() throws InterruptedException {
/* 54 */     return IoFuture.Status.DONE;
/*    */   }
/*    */ 
/*    */   
/*    */   public IoFuture.Status awaitInterruptibly(long time, TimeUnit timeUnit) throws InterruptedException {
/* 59 */     return IoFuture.Status.DONE;
/*    */   }
/*    */ 
/*    */   
/*    */   public T get() throws IOException, CancellationException {
/* 64 */     return this.result;
/*    */   }
/*    */ 
/*    */   
/*    */   public T getInterruptibly() throws IOException, InterruptedException, CancellationException {
/* 69 */     return this.result;
/*    */   }
/*    */ 
/*    */   
/*    */   public IOException getException() throws IllegalStateException {
/* 74 */     throw new IllegalStateException();
/*    */   }
/*    */ 
/*    */   
/*    */   public <A> IoFuture<T> addNotifier(IoFuture.Notifier<? super T, A> notifier, A attachment) {
/* 79 */     notifier.notify(this, attachment);
/* 80 */     return this;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\FinishedIoFuture.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */