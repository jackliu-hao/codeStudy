package cn.hutool.cron;

public class TaskLauncher implements Runnable {
   private final Scheduler scheduler;
   private final long millis;

   public TaskLauncher(Scheduler scheduler, long millis) {
      this.scheduler = scheduler;
      this.millis = millis;
   }

   public void run() {
      this.scheduler.taskTable.executeTaskIfMatch(this.scheduler, this.millis);
      this.scheduler.taskLauncherManager.notifyLauncherCompleted(this);
   }
}
