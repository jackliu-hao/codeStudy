package io.undertow.server;

public interface ConnectorStatistics {
  long getRequestCount();
  
  long getBytesSent();
  
  long getBytesReceived();
  
  long getErrorCount();
  
  long getProcessingTime();
  
  long getMaxProcessingTime();
  
  void reset();
  
  long getActiveConnections();
  
  long getMaxActiveConnections();
  
  long getActiveRequests();
  
  long getMaxActiveRequests();
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\ConnectorStatistics.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */