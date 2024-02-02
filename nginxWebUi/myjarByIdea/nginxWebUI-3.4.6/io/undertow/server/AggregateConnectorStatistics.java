package io.undertow.server;

public class AggregateConnectorStatistics implements ConnectorStatistics {
   private final ConnectorStatistics[] connectorStatistics;

   public AggregateConnectorStatistics(ConnectorStatistics[] connectorStatistics) {
      this.connectorStatistics = connectorStatistics;
   }

   public long getRequestCount() {
      long count = 0L;
      ConnectorStatistics[] var3 = this.connectorStatistics;
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         ConnectorStatistics c = var3[var5];
         count += c.getRequestCount();
      }

      return count;
   }

   public long getBytesSent() {
      long count = 0L;
      ConnectorStatistics[] var3 = this.connectorStatistics;
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         ConnectorStatistics c = var3[var5];
         count += c.getBytesSent();
      }

      return count;
   }

   public long getBytesReceived() {
      long count = 0L;
      ConnectorStatistics[] var3 = this.connectorStatistics;
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         ConnectorStatistics c = var3[var5];
         count += c.getBytesReceived();
      }

      return count;
   }

   public long getErrorCount() {
      long count = 0L;
      ConnectorStatistics[] var3 = this.connectorStatistics;
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         ConnectorStatistics c = var3[var5];
         count += c.getErrorCount();
      }

      return count;
   }

   public long getProcessingTime() {
      long count = 0L;
      ConnectorStatistics[] var3 = this.connectorStatistics;
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         ConnectorStatistics c = var3[var5];
         count += c.getProcessingTime();
      }

      return count;
   }

   public long getMaxProcessingTime() {
      long max = 0L;
      ConnectorStatistics[] var3 = this.connectorStatistics;
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         ConnectorStatistics c = var3[var5];
         max = Math.max(c.getMaxProcessingTime(), max);
      }

      return max;
   }

   public void reset() {
      ConnectorStatistics[] var1 = this.connectorStatistics;
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         ConnectorStatistics c = var1[var3];
         c.reset();
      }

   }

   public long getActiveConnections() {
      long count = 0L;
      ConnectorStatistics[] var3 = this.connectorStatistics;
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         ConnectorStatistics c = var3[var5];
         count += c.getActiveConnections();
      }

      return count;
   }

   public long getMaxActiveConnections() {
      long count = 0L;
      ConnectorStatistics[] var3 = this.connectorStatistics;
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         ConnectorStatistics c = var3[var5];
         count += c.getMaxActiveConnections();
      }

      return count;
   }

   public long getActiveRequests() {
      long count = 0L;
      ConnectorStatistics[] var3 = this.connectorStatistics;
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         ConnectorStatistics c = var3[var5];
         count += c.getActiveRequests();
      }

      return count;
   }

   public long getMaxActiveRequests() {
      long count = 0L;
      ConnectorStatistics[] var3 = this.connectorStatistics;
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         ConnectorStatistics c = var3[var5];
         count += c.getMaxActiveRequests();
      }

      return count;
   }
}
