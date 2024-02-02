/*    */ package cn.hutool.cron;
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
/*    */ public class TaskLauncher
/*    */   implements Runnable
/*    */ {
/*    */   private final Scheduler scheduler;
/*    */   private final long millis;
/*    */   
/*    */   public TaskLauncher(Scheduler scheduler, long millis) {
/* 22 */     this.scheduler = scheduler;
/* 23 */     this.millis = millis;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public void run() {
/* 29 */     this.scheduler.taskTable.executeTaskIfMatch(this.scheduler, this.millis);
/*    */ 
/*    */     
/* 32 */     this.scheduler.taskLauncherManager.notifyLauncherCompleted(this);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\cron\TaskLauncher.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */