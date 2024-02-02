/*    */ package cn.hutool.cron.listener;
/*    */ 
/*    */ import cn.hutool.cron.TaskExecutor;
/*    */ import cn.hutool.log.StaticLog;
/*    */ import java.io.Serializable;
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class TaskListenerManager
/*    */   implements Serializable
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/* 18 */   private final List<TaskListener> listeners = new ArrayList<>();
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public TaskListenerManager addListener(TaskListener listener) {
/* 26 */     synchronized (this.listeners) {
/* 27 */       this.listeners.add(listener);
/*    */     } 
/* 29 */     return this;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public TaskListenerManager removeListener(TaskListener listener) {
/* 38 */     synchronized (this.listeners) {
/* 39 */       this.listeners.remove(listener);
/*    */     } 
/* 41 */     return this;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void notifyTaskStart(TaskExecutor executor) {
/* 49 */     synchronized (this.listeners) {
/*    */       
/* 51 */       for (TaskListener taskListener : this.listeners) {
/* 52 */         TaskListener listener = taskListener;
/* 53 */         if (null != listener) {
/* 54 */           listener.onStart(executor);
/*    */         }
/*    */       } 
/*    */     } 
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void notifyTaskSucceeded(TaskExecutor executor) {
/* 65 */     synchronized (this.listeners) {
/* 66 */       for (TaskListener listener : this.listeners) {
/* 67 */         listener.onSucceeded(executor);
/*    */       }
/*    */     } 
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void notifyTaskFailed(TaskExecutor executor, Throwable exception) {
/* 79 */     synchronized (this.listeners) {
/* 80 */       int size = this.listeners.size();
/* 81 */       if (size > 0) {
/* 82 */         for (TaskListener listener : this.listeners) {
/* 83 */           listener.onFailed(executor, exception);
/*    */         }
/*    */       } else {
/* 86 */         StaticLog.error(exception, exception.getMessage(), new Object[0]);
/*    */       } 
/*    */     } 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\cron\listener\TaskListenerManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */