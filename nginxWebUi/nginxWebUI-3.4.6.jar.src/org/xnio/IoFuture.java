/*     */ package org.xnio;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.util.EventListener;
/*     */ import java.util.concurrent.CancellationException;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public interface IoFuture<T>
/*     */   extends Cancellable
/*     */ {
/*     */   IoFuture<T> cancel();
/*     */   
/*     */   Status getStatus();
/*     */   
/*     */   Status await();
/*     */   
/*     */   Status await(long paramLong, TimeUnit paramTimeUnit);
/*     */   
/*     */   Status awaitInterruptibly() throws InterruptedException;
/*     */   
/*     */   Status awaitInterruptibly(long paramLong, TimeUnit paramTimeUnit) throws InterruptedException;
/*     */   
/*     */   T get() throws IOException, CancellationException;
/*     */   
/*     */   T getInterruptibly() throws IOException, InterruptedException, CancellationException;
/*     */   
/*     */   IOException getException() throws IllegalStateException;
/*     */   
/*     */   <A> IoFuture<T> addNotifier(Notifier<? super T, A> paramNotifier, A paramA);
/*     */   
/*     */   public enum Status
/*     */   {
/*  64 */     WAITING,
/*     */ 
/*     */ 
/*     */     
/*  68 */     DONE,
/*     */ 
/*     */ 
/*     */     
/*  72 */     CANCELLED,
/*     */ 
/*     */ 
/*     */     
/*  76 */     FAILED;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static interface Notifier<T, A>
/*     */     extends EventListener
/*     */   {
/*     */     void notify(IoFuture<? extends T> param1IoFuture, A param1A);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static abstract class HandlingNotifier<T, A>
/*     */     implements Notifier<T, A>
/*     */   {
/*     */     public void notify(IoFuture<? extends T> future, A attachment) {
/* 202 */       switch (future.getStatus()) {
/*     */         case CANCELLED:
/* 204 */           handleCancelled(attachment);
/*     */           return;
/*     */         case DONE:
/*     */           try {
/* 208 */             handleDone(future.get(), attachment);
/* 209 */           } catch (IOException e) {
/*     */             
/* 211 */             throw new IllegalStateException();
/*     */           } 
/*     */           return;
/*     */         case FAILED:
/* 215 */           handleFailed(future.getException(), attachment);
/*     */           return;
/*     */       } 
/*     */       
/* 219 */       throw new IllegalStateException();
/*     */     }
/*     */     
/*     */     public void handleCancelled(A attachment) {}
/*     */     
/*     */     public void handleFailed(IOException exception, A attachment) {}
/*     */     
/*     */     public void handleDone(T data, A attachment) {}
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\IoFuture.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */