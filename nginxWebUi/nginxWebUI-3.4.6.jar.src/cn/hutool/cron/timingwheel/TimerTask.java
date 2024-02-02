/*    */ package cn.hutool.cron.timingwheel;
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
/*    */ 
/*    */ public class TimerTask
/*    */ {
/*    */   private final long delayMs;
/*    */   private final Runnable task;
/*    */   protected TimerTaskList timerTaskList;
/*    */   protected TimerTask next;
/*    */   protected TimerTask prev;
/*    */   public String desc;
/*    */   
/*    */   public TimerTask(Runnable task, long delayMs) {
/* 47 */     this.delayMs = System.currentTimeMillis() + delayMs;
/* 48 */     this.task = task;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public Runnable getTask() {
/* 57 */     return this.task;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public long getDelayMs() {
/* 65 */     return this.delayMs;
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 70 */     return this.desc;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\cron\timingwheel\TimerTask.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */