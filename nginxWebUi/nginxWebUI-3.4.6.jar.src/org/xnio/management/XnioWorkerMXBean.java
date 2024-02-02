package org.xnio.management;

import java.util.Set;

public interface XnioWorkerMXBean {
  String getProviderName();
  
  String getName();
  
  boolean isShutdownRequested();
  
  int getCoreWorkerPoolSize();
  
  int getMaxWorkerPoolSize();
  
  int getWorkerPoolSize();
  
  int getBusyWorkerThreadCount();
  
  int getIoThreadCount();
  
  int getWorkerQueueSize();
  
  Set<XnioServerMXBean> getServerMXBeans();
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\management\XnioWorkerMXBean.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */