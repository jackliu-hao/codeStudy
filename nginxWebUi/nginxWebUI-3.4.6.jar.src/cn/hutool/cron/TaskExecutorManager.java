/*    */ package cn.hutool.cron;
/*    */ 
/*    */ import cn.hutool.cron.task.CronTask;
/*    */ import java.io.Serializable;
/*    */ import java.util.ArrayList;
/*    */ import java.util.Collections;
/*    */ import java.util.List;
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
/*    */ public class TaskExecutorManager
/*    */   implements Serializable
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   protected Scheduler scheduler;
/* 29 */   private final List<TaskExecutor> executors = new ArrayList<>();
/*    */   
/*    */   public TaskExecutorManager(Scheduler scheduler) {
/* 32 */     this.scheduler = scheduler;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public List<TaskExecutor> getExecutors() {
/* 42 */     return Collections.unmodifiableList(this.executors);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public TaskExecutor spawnExecutor(CronTask task) {
/* 52 */     TaskExecutor executor = new TaskExecutor(this.scheduler, task);
/* 53 */     synchronized (this.executors) {
/* 54 */       this.executors.add(executor);
/*    */     } 
/*    */ 
/*    */ 
/*    */     
/* 59 */     this.scheduler.threadExecutor.execute(executor);
/* 60 */     return executor;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public TaskExecutorManager notifyExecutorCompleted(TaskExecutor executor) {
/* 70 */     synchronized (this.executors) {
/* 71 */       this.executors.remove(executor);
/*    */     } 
/* 73 */     return this;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\cron\TaskExecutorManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */