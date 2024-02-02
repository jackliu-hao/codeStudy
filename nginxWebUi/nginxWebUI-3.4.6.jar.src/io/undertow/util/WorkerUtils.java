/*    */ package io.undertow.util;
/*    */ 
/*    */ import io.undertow.UndertowLogger;
/*    */ import java.util.concurrent.RejectedExecutionException;
/*    */ import java.util.concurrent.TimeUnit;
/*    */ import org.xnio.XnioExecutor;
/*    */ import org.xnio.XnioIoThread;
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
/*    */ public class WorkerUtils
/*    */ {
/*    */   public static XnioExecutor.Key executeAfter(XnioIoThread thread, Runnable task, long timeout, TimeUnit timeUnit) {
/*    */     try {
/* 46 */       return thread.executeAfter(task, timeout, timeUnit);
/* 47 */     } catch (RejectedExecutionException e) {
/* 48 */       if (thread.getWorker().isShutdown()) {
/* 49 */         UndertowLogger.ROOT_LOGGER.debugf(e, "Failed to schedule task %s as worker is shutting down", task);
/*    */         
/* 51 */         return new XnioExecutor.Key()
/*    */           {
/*    */             public boolean remove() {
/* 54 */               return false;
/*    */             }
/*    */           };
/*    */       } 
/* 58 */       throw e;
/*    */     } 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\underto\\util\WorkerUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */