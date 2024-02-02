/*    */ package cn.hutool.cron.task;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class RunnableTask
/*    */   implements Task
/*    */ {
/*    */   private final Runnable runnable;
/*    */   
/*    */   public RunnableTask(Runnable runnable) {
/* 12 */     this.runnable = runnable;
/*    */   }
/*    */ 
/*    */   
/*    */   public void execute() {
/* 17 */     this.runnable.run();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\cron\task\RunnableTask.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */