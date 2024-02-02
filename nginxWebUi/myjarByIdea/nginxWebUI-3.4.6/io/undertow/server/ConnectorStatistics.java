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
