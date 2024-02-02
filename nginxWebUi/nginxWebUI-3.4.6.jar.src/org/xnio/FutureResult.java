/*    */ package org.xnio;
/*    */ 
/*    */ import java.io.IOException;
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
/*    */ public class FutureResult<T>
/*    */   implements Result<T>
/*    */ {
/* 39 */   private final AbstractIoFuture<T> ioFuture = new AbstractIoFuture<T>() {
/*    */     
/*    */     };
/*    */   
/*    */   public FutureResult(Executor executor) {}
/*    */   
/*    */   public FutureResult() {
/* 46 */     this(IoUtils.directExecutor());
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public IoFuture<T> getIoFuture() {
/* 55 */     return this.ioFuture;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void addCancelHandler(Cancellable cancellable) {
/* 65 */     this.ioFuture.addCancelHandler(cancellable);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public boolean setResult(T result) {
/* 75 */     return this.ioFuture.setResult(result);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public boolean setException(IOException exception) {
/* 85 */     return this.ioFuture.setException(exception);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public boolean setCancelled() {
/* 94 */     return this.ioFuture.setCancelled();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\FutureResult.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */