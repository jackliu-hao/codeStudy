package cn.hutool.cron.listener;

import cn.hutool.cron.TaskExecutor;

public interface TaskListener {
   void onStart(TaskExecutor var1);

   void onSucceeded(TaskExecutor var1);

   void onFailed(TaskExecutor var1, Throwable var2);
}
