/*    */ package cn.hutool.core.thread;
/*    */ 
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
/*    */ public class AsyncUtil
/*    */ {
/*    */   public static void waitAll(CompletableFuture<?>... tasks) {
/*    */     try {
/* 24 */       CompletableFuture.allOf(tasks).get();
/* 25 */     } catch (InterruptedException|java.util.concurrent.ExecutionException e) {
/* 26 */       throw new ThreadException(e);
/*    */     } 
/*    */   }
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
/*    */   public static <T> T waitAny(CompletableFuture<?>... tasks) {
/*    */     try {
/* 41 */       return (T)CompletableFuture.anyOf(tasks).get();
/* 42 */     } catch (InterruptedException|java.util.concurrent.ExecutionException e) {
/* 43 */       throw new ThreadException(e);
/*    */     } 
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static <T> T get(CompletableFuture<T> task) {
/*    */     try {
/* 57 */       return task.get();
/* 58 */     } catch (InterruptedException|java.util.concurrent.ExecutionException e) {
/* 59 */       throw new ThreadException(e);
/*    */     } 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\thread\AsyncUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */