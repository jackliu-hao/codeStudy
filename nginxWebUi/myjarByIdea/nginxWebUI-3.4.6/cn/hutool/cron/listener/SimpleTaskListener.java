package cn.hutool.cron.listener;

import cn.hutool.cron.TaskExecutor;

public class SimpleTaskListener implements TaskListener {
   public void onStart(TaskExecutor executor) {
   }

   public void onSucceeded(TaskExecutor executor) {
   }

   public void onFailed(TaskExecutor executor, Throwable exception) {
   }
}
