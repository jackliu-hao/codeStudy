package cn.hutool.cron.timingwheel;

import cn.hutool.core.thread.ThreadUtil;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

public class SystemTimer {
   private final TimingWheel timeWheel;
   private final DelayQueue<TimerTaskList> delayQueue = new DelayQueue();
   private long delayQueueTimeout = 100L;
   private ExecutorService bossThreadPool;

   public SystemTimer() {
      DelayQueue var10005 = this.delayQueue;
      var10005.getClass();
      this.timeWheel = new TimingWheel(1L, 20, var10005::offer);
   }

   public SystemTimer setDelayQueueTimeout(long delayQueueTimeout) {
      this.delayQueueTimeout = delayQueueTimeout;
      return this;
   }

   public SystemTimer start() {
      this.bossThreadPool = ThreadUtil.newSingleExecutor();
      this.bossThreadPool.submit(() -> {
         while(this.advanceClock()) {
         }

      });
      return this;
   }

   public void stop() {
      this.bossThreadPool.shutdown();
   }

   public void addTask(TimerTask timerTask) {
      if (!this.timeWheel.addTask(timerTask)) {
         ThreadUtil.execAsync(timerTask.getTask());
      }

   }

   private boolean advanceClock() {
      try {
         TimerTaskList timerTaskList = this.poll();
         if (null != timerTaskList) {
            this.timeWheel.advanceClock(timerTaskList.getExpire());
            timerTaskList.flush(this::addTask);
         }

         return true;
      } catch (InterruptedException var2) {
         return false;
      }
   }

   private TimerTaskList poll() throws InterruptedException {
      return this.delayQueueTimeout > 0L ? (TimerTaskList)this.delayQueue.poll(this.delayQueueTimeout, TimeUnit.MILLISECONDS) : (TimerTaskList)this.delayQueue.poll();
   }
}
