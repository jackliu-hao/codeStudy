/*    */ package cn.hutool.cron;
/*    */ 
/*    */ import java.io.Serializable;
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class TaskLauncherManager
/*    */   implements Serializable
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   protected Scheduler scheduler;
/* 18 */   protected final List<TaskLauncher> launchers = new ArrayList<>();
/*    */   
/*    */   public TaskLauncherManager(Scheduler scheduler) {
/* 21 */     this.scheduler = scheduler;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   protected TaskLauncher spawnLauncher(long millis) {
/* 30 */     TaskLauncher launcher = new TaskLauncher(this.scheduler, millis);
/* 31 */     synchronized (this.launchers) {
/* 32 */       this.launchers.add(launcher);
/*    */     } 
/*    */ 
/*    */ 
/*    */     
/* 37 */     this.scheduler.threadExecutor.execute(launcher);
/* 38 */     return launcher;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   protected void notifyLauncherCompleted(TaskLauncher launcher) {
/* 46 */     synchronized (this.launchers) {
/* 47 */       this.launchers.remove(launcher);
/*    */     } 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\cron\TaskLauncherManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */