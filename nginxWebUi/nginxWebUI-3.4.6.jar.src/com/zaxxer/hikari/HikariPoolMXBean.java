package com.zaxxer.hikari;

public interface HikariPoolMXBean {
  int getIdleConnections();
  
  int getActiveConnections();
  
  int getTotalConnections();
  
  int getThreadsAwaitingConnection();
  
  void softEvictConnections();
  
  void suspendPool();
  
  void resumePool();
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\zaxxer\hikari\HikariPoolMXBean.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */