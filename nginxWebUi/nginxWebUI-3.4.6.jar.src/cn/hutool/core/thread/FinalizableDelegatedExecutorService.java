/*    */ package cn.hutool.core.thread;
/*    */ 
/*    */ import java.util.concurrent.ExecutorService;
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
/*    */ public class FinalizableDelegatedExecutorService
/*    */   extends DelegatedExecutorService
/*    */ {
/*    */   FinalizableDelegatedExecutorService(ExecutorService executor) {
/* 18 */     super(executor);
/*    */   }
/*    */ 
/*    */   
/*    */   protected void finalize() {
/* 23 */     shutdown();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\thread\FinalizableDelegatedExecutorService.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */