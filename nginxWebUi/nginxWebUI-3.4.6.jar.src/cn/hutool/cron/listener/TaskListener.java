package cn.hutool.cron.listener;

import cn.hutool.cron.TaskExecutor;

public interface TaskListener {
  void onStart(TaskExecutor paramTaskExecutor);
  
  void onSucceeded(TaskExecutor paramTaskExecutor);
  
  void onFailed(TaskExecutor paramTaskExecutor, Throwable paramThrowable);
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\cron\listener\TaskListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */