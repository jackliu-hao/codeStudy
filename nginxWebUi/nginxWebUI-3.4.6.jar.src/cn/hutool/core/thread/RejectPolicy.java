/*    */ package cn.hutool.core.thread;
/*    */ 
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
/*    */ public enum RejectPolicy
/*    */ {
/* 18 */   ABORT(new ThreadPoolExecutor.AbortPolicy()),
/*    */   
/* 20 */   DISCARD(new ThreadPoolExecutor.DiscardPolicy()),
/*    */   
/* 22 */   DISCARD_OLDEST(new ThreadPoolExecutor.DiscardOldestPolicy()),
/*    */   
/* 24 */   CALLER_RUNS(new ThreadPoolExecutor.CallerRunsPolicy()),
/*    */   
/* 26 */   BLOCK(new BlockPolicy());
/*    */   
/*    */   private final RejectedExecutionHandler value;
/*    */   
/*    */   RejectPolicy(RejectedExecutionHandler handler) {
/* 31 */     this.value = handler;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public RejectedExecutionHandler getValue() {
/* 40 */     return this.value;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\thread\RejectPolicy.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */