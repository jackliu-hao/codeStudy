package cn.hutool.cron;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class TaskLauncherManager implements Serializable {
   private static final long serialVersionUID = 1L;
   protected Scheduler scheduler;
   protected final List<TaskLauncher> launchers = new ArrayList();

   public TaskLauncherManager(Scheduler scheduler) {
      this.scheduler = scheduler;
   }

   protected TaskLauncher spawnLauncher(long millis) {
      TaskLauncher launcher = new TaskLauncher(this.scheduler, millis);
      synchronized(this.launchers) {
         this.launchers.add(launcher);
      }

      this.scheduler.threadExecutor.execute(launcher);
      return launcher;
   }

   protected void notifyLauncherCompleted(TaskLauncher launcher) {
      synchronized(this.launchers) {
         this.launchers.remove(launcher);
      }
   }
}
