package cn.hutool.cron;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import java.io.Serializable;

public class CronTimer extends Thread implements Serializable {
   private static final long serialVersionUID = 1L;
   private static final Log log = LogFactory.get();
   private final long TIMER_UNIT_SECOND;
   private final long TIMER_UNIT_MINUTE;
   private boolean isStop;
   private final Scheduler scheduler;

   public CronTimer(Scheduler scheduler) {
      this.TIMER_UNIT_SECOND = DateUnit.SECOND.getMillis();
      this.TIMER_UNIT_MINUTE = DateUnit.MINUTE.getMillis();
      this.scheduler = scheduler;
   }

   public void run() {
      long timerUnit = this.scheduler.config.matchSecond ? this.TIMER_UNIT_SECOND : this.TIMER_UNIT_MINUTE;
      long thisTime = System.currentTimeMillis();

      while(!this.isStop) {
         long nextTime = (thisTime / timerUnit + 1L) * timerUnit;
         long sleep = nextTime - System.currentTimeMillis();
         if (isValidSleepMillis(sleep, timerUnit)) {
            if (!ThreadUtil.safeSleep(sleep)) {
               break;
            }

            thisTime = System.currentTimeMillis();
            this.spawnLauncher(thisTime);
         } else {
            thisTime = System.currentTimeMillis();
         }
      }

      log.debug("Hutool-cron timer stopped.", new Object[0]);
   }

   public synchronized void stopTimer() {
      this.isStop = true;
      ThreadUtil.interrupt(this, true);
   }

   private void spawnLauncher(long millis) {
      this.scheduler.taskLauncherManager.spawnLauncher(millis);
   }

   private static boolean isValidSleepMillis(long millis, long timerUnit) {
      return millis > 0L && millis < 2L * timerUnit;
   }
}
