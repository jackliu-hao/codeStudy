package org.h2.jmx;

public interface DatabaseInfoMBean {
  boolean isExclusive();
  
  boolean isReadOnly();
  
  String getMode();
  
  long getFileWriteCount();
  
  long getFileReadCount();
  
  long getFileSize();
  
  int getCacheSizeMax();
  
  void setCacheSizeMax(int paramInt);
  
  int getCacheSize();
  
  String getVersion();
  
  int getTraceLevel();
  
  void setTraceLevel(int paramInt);
  
  String listSettings();
  
  String listSessions();
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\jmx\DatabaseInfoMBean.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */