/*    */ package cn.hutool.core.thread;
/*    */ 
/*    */ import cn.hutool.core.exceptions.UtilException;
/*    */ import java.util.concurrent.Callable;
/*    */ import java.util.concurrent.ExecutorService;
/*    */ import java.util.concurrent.Future;
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
/*    */ public class GlobalThreadPool
/*    */ {
/*    */   private static ExecutorService executor;
/*    */   
/*    */   static {
/* 23 */     init();
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static synchronized void init() {
/* 30 */     if (null != executor) {
/* 31 */       executor.shutdownNow();
/*    */     }
/* 33 */     executor = ExecutorBuilder.create().useSynchronousQueue().build();
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static synchronized void shutdown(boolean isNow) {
/* 42 */     if (null != executor) {
/* 43 */       if (isNow) {
/* 44 */         executor.shutdownNow();
/*    */       } else {
/* 46 */         executor.shutdown();
/*    */       } 
/*    */     }
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static ExecutorService getExecutor() {
/* 57 */     return executor;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static void execute(Runnable runnable) {
/*    */     try {
/* 67 */       executor.execute(runnable);
/* 68 */     } catch (Exception e) {
/* 69 */       throw new UtilException(e, "Exception when running task!", new Object[0]);
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
/*    */   public static <T> Future<T> submit(Callable<T> task) {
/* 82 */     return executor.submit(task);
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
/*    */   public static Future<?> submit(Runnable runnable) {
/* 94 */     return executor.submit(runnable);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\thread\GlobalThreadPool.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */