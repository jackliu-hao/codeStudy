package cn.hutool.cron.listener;

import cn.hutool.cron.TaskExecutor;

public class SimpleTaskListener implements TaskListener {
  public void onStart(TaskExecutor executor) {}
  
  public void onSucceeded(TaskExecutor executor) {}
  
  public void onFailed(TaskExecutor executor, Throwable exception) {}
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\cron\listener\SimpleTaskListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */