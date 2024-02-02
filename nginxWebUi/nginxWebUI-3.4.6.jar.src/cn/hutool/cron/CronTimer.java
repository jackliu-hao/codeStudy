/*    */ package cn.hutool.cron;
/*    */ 
/*    */ import cn.hutool.core.date.DateUnit;
/*    */ import cn.hutool.core.thread.ThreadUtil;
/*    */ import cn.hutool.log.Log;
/*    */ import cn.hutool.log.LogFactory;
/*    */ import java.io.Serializable;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class CronTimer
/*    */   extends Thread
/*    */   implements Serializable
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/* 19 */   private static final Log log = LogFactory.get();
/*    */ 
/*    */   
/* 22 */   private final long TIMER_UNIT_SECOND = DateUnit.SECOND.getMillis();
/*    */   
/* 24 */   private final long TIMER_UNIT_MINUTE = DateUnit.MINUTE.getMillis();
/*    */ 
/*    */   
/*    */   private boolean isStop;
/*    */ 
/*    */   
/*    */   private final Scheduler scheduler;
/*    */ 
/*    */ 
/*    */   
/*    */   public CronTimer(Scheduler scheduler) {
/* 35 */     this.scheduler = scheduler;
/*    */   }
/*    */ 
/*    */   
/*    */   public void run() {
/* 40 */     long timerUnit = this.scheduler.config.matchSecond ? this.TIMER_UNIT_SECOND : this.TIMER_UNIT_MINUTE;
/*    */     
/* 42 */     long thisTime = System.currentTimeMillis();
/*    */ 
/*    */     
/* 45 */     while (false == this.isStop) {
/*    */ 
/*    */       
/* 48 */       long nextTime = (thisTime / timerUnit + 1L) * timerUnit;
/* 49 */       long sleep = nextTime - System.currentTimeMillis();
/* 50 */       if (isValidSleepMillis(sleep, timerUnit)) {
/* 51 */         if (false == ThreadUtil.safeSleep(sleep)) {
/*    */           break;
/*    */         }
/*    */ 
/*    */         
/* 56 */         thisTime = System.currentTimeMillis();
/* 57 */         spawnLauncher(thisTime);
/*    */         continue;
/*    */       } 
/* 60 */       thisTime = System.currentTimeMillis();
/*    */     } 
/*    */     
/* 63 */     log.debug("Hutool-cron timer stopped.", new Object[0]);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public synchronized void stopTimer() {
/* 70 */     this.isStop = true;
/* 71 */     ThreadUtil.interrupt(this, true);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   private void spawnLauncher(long millis) {
/* 79 */     this.scheduler.taskLauncherManager.spawnLauncher(millis);
/*    */   }
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
/*    */   private static boolean isValidSleepMillis(long millis, long timerUnit) {
/* 95 */     return (millis > 0L && millis < 2L * timerUnit);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\cron\CronTimer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */