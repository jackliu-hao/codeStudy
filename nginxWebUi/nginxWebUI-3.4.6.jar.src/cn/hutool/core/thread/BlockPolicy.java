/*    */ package cn.hutool.core.thread;
/*    */ 
/*    */ import java.util.concurrent.RejectedExecutionException;
/*    */ import java.util.concurrent.RejectedExecutionHandler;
/*    */ import java.util.concurrent.ThreadPoolExecutor;
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
/*    */ public class BlockPolicy
/*    */   implements RejectedExecutionHandler
/*    */ {
/*    */   public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
/*    */     try {
/* 23 */       e.getQueue().put(r);
/* 24 */     } catch (InterruptedException ex) {
/* 25 */       throw new RejectedExecutionException("Task " + r + " rejected from " + e);
/*    */     } 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\thread\BlockPolicy.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */