package org.xnio.management;

public interface XnioServerMXBean {
  String getProviderName();
  
  String getWorkerName();
  
  String getBindAddress();
  
  int getConnectionCount();
  
  int getConnectionLimitHighWater();
  
  int getConnectionLimitLowWater();
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\management\XnioServerMXBean.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */