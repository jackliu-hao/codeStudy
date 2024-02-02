package cn.hutool.cron.task;

public class RunnableTask implements Task {
   private final Runnable runnable;

   public RunnableTask(Runnable runnable) {
      this.runnable = runnable;
   }

   public void execute() {
      this.runnable.run();
   }
}
