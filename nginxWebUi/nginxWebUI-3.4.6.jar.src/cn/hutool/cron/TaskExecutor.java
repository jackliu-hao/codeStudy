/*    */ package cn.hutool.cron;
/*    */ 
/*    */ import cn.hutool.cron.task.CronTask;
/*    */ import cn.hutool.cron.task.Task;
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
/*    */ public class TaskExecutor
/*    */   implements Runnable
/*    */ {
/*    */   private final Scheduler scheduler;
/*    */   private final CronTask task;
/*    */   
/*    */   public Task getTask() {
/* 24 */     return this.task.getRaw();
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public CronTask getCronTask() {
/* 34 */     return this.task;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public TaskExecutor(Scheduler scheduler, CronTask task) {
/* 44 */     this.scheduler = scheduler;
/* 45 */     this.task = task;
/*    */   }
/*    */ 
/*    */   
/*    */   public void run() {
/*    */     try {
/* 51 */       this.scheduler.listenerManager.notifyTaskStart(this);
/* 52 */       this.task.execute();
/* 53 */       this.scheduler.listenerManager.notifyTaskSucceeded(this);
/* 54 */     } catch (Exception e) {
/* 55 */       this.scheduler.listenerManager.notifyTaskFailed(this, e);
/*    */     } finally {
/* 57 */       this.scheduler.taskExecutorManager.notifyExecutorCompleted(this);
/*    */     } 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\cron\TaskExecutor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */