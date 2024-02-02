package cn.hutool.cron;

import cn.hutool.cron.task.CronTask;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TaskExecutorManager implements Serializable {
   private static final long serialVersionUID = 1L;
   protected Scheduler scheduler;
   private final List<TaskExecutor> executors = new ArrayList();

   public TaskExecutorManager(Scheduler scheduler) {
      this.scheduler = scheduler;
   }

   public List<TaskExecutor> getExecutors() {
      return Collections.unmodifiableList(this.executors);
   }

   public TaskExecutor spawnExecutor(CronTask task) {
      TaskExecutor executor = new TaskExecutor(this.scheduler, task);
      synchronized(this.executors) {
         this.executors.add(executor);
      }

      this.scheduler.threadExecutor.execute(executor);
      return executor;
   }

   public TaskExecutorManager notifyExecutorCompleted(TaskExecutor executor) {
      synchronized(this.executors) {
         this.executors.remove(executor);
         return this;
      }
   }
}
