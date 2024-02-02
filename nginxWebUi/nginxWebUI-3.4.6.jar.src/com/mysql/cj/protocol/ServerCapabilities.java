package com.mysql.cj.protocol;

import com.mysql.cj.ServerVersion;

public interface ServerCapabilities {
  int getCapabilityFlags();
  
  void setCapabilityFlags(int paramInt);
  
  ServerVersion getServerVersion();
  
  long getThreadId();
  
  void setThreadId(long paramLong);
  
  boolean serverSupportsFracSecs();
  
  int getServerDefaultCollationIndex();
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\protocol\ServerCapabilities.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */