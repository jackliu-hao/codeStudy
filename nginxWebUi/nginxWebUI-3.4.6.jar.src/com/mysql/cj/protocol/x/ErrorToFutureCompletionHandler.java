/*    */ package com.mysql.cj.protocol.x;
/*    */ 
/*    */ import java.nio.channels.CompletionHandler;
/*    */ import java.util.concurrent.CompletableFuture;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class ErrorToFutureCompletionHandler<T>
/*    */   implements CompletionHandler<T, Void>
/*    */ {
/*    */   private CompletableFuture<?> future;
/*    */   private Runnable successCallback;
/*    */   
/*    */   public ErrorToFutureCompletionHandler(CompletableFuture<?> future, Runnable successCallback) {
/* 46 */     this.future = future;
/* 47 */     this.successCallback = successCallback;
/*    */   }
/*    */   
/*    */   public void completed(T result, Void attachment) {
/* 51 */     this.successCallback.run();
/*    */   }
/*    */   
/*    */   public void failed(Throwable ex, Void attachment) {
/* 55 */     this.future.completeExceptionally(ex);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\protocol\x\ErrorToFutureCompletionHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */