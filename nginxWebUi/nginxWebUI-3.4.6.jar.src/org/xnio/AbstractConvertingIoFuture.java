/*    */ package org.xnio;
/*    */ 
/*    */ import java.io.IOException;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public abstract class AbstractConvertingIoFuture<T, D>
/*    */   implements IoFuture<T>
/*    */ {
/*    */   protected final IoFuture<? extends D> delegate;
/*    */   
/*    */   protected AbstractConvertingIoFuture(IoFuture<? extends D> delegate) {
/* 38 */     this.delegate = delegate;
/*    */   }
/*    */   
/*    */   protected IoFuture<? extends D> getDelegate() {
/* 42 */     return this.delegate;
/*    */   }
/*    */   
/*    */   public IoFuture<T> cancel() {
/* 46 */     this.delegate.cancel();
/* 47 */     return this;
/*    */   }
/*    */   
/*    */   public IoFuture.Status getStatus() {
/* 51 */     return this.delegate.getStatus();
/*    */   }
/*    */   
/*    */   public IoFuture.Status await() {
/* 55 */     return this.delegate.await();
/*    */   }
/*    */   
/*    */   public IoFuture.Status await(long time, TimeUnit timeUnit) {
/* 59 */     return this.delegate.await(time, timeUnit);
/*    */   }
/*    */   
/*    */   public IoFuture.Status awaitInterruptibly() throws InterruptedException {
/* 63 */     return this.delegate.awaitInterruptibly();
/*    */   }
/*    */   
/*    */   public IoFuture.Status awaitInterruptibly(long time, TimeUnit timeUnit) throws InterruptedException {
/* 67 */     return this.delegate.awaitInterruptibly(time, timeUnit);
/*    */   }
/*    */   
/*    */   public IOException getException() throws IllegalStateException {
/* 71 */     return this.delegate.getException();
/*    */   }
/*    */   
/*    */   public T get() throws IOException {
/* 75 */     return convert(this.delegate.get());
/*    */   }
/*    */   
/*    */   public T getInterruptibly() throws IOException, InterruptedException {
/* 79 */     return convert(this.delegate.getInterruptibly());
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public <A> IoFuture<T> addNotifier(final IoFuture.Notifier<? super T, A> notifier, A attachment) {
/* 85 */     this.delegate.addNotifier(new IoFuture.Notifier<D, A>() {
/*    */           public void notify(IoFuture<? extends D> future, A attachment) {
/* 87 */             notifier.notify(AbstractConvertingIoFuture.this, attachment);
/*    */           }
/*    */         }attachment);
/* 90 */     return this;
/*    */   }
/*    */   
/*    */   protected abstract T convert(D paramD) throws IOException;
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\AbstractConvertingIoFuture.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */