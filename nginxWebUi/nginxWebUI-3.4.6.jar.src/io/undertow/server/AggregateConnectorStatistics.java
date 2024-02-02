/*     */ package io.undertow.server;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class AggregateConnectorStatistics
/*     */   implements ConnectorStatistics
/*     */ {
/*     */   private final ConnectorStatistics[] connectorStatistics;
/*     */   
/*     */   public AggregateConnectorStatistics(ConnectorStatistics[] connectorStatistics) {
/*  29 */     this.connectorStatistics = connectorStatistics;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getRequestCount() {
/*  34 */     long count = 0L;
/*  35 */     for (ConnectorStatistics c : this.connectorStatistics) {
/*  36 */       count += c.getRequestCount();
/*     */     }
/*  38 */     return count;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getBytesSent() {
/*  43 */     long count = 0L;
/*  44 */     for (ConnectorStatistics c : this.connectorStatistics) {
/*  45 */       count += c.getBytesSent();
/*     */     }
/*  47 */     return count;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getBytesReceived() {
/*  52 */     long count = 0L;
/*  53 */     for (ConnectorStatistics c : this.connectorStatistics) {
/*  54 */       count += c.getBytesReceived();
/*     */     }
/*  56 */     return count;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getErrorCount() {
/*  61 */     long count = 0L;
/*  62 */     for (ConnectorStatistics c : this.connectorStatistics) {
/*  63 */       count += c.getErrorCount();
/*     */     }
/*  65 */     return count;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getProcessingTime() {
/*  70 */     long count = 0L;
/*  71 */     for (ConnectorStatistics c : this.connectorStatistics) {
/*  72 */       count += c.getProcessingTime();
/*     */     }
/*  74 */     return count;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getMaxProcessingTime() {
/*  79 */     long max = 0L;
/*  80 */     for (ConnectorStatistics c : this.connectorStatistics) {
/*  81 */       max = Math.max(c.getMaxProcessingTime(), max);
/*     */     }
/*  83 */     return max;
/*     */   }
/*     */ 
/*     */   
/*     */   public void reset() {
/*  88 */     for (ConnectorStatistics c : this.connectorStatistics) {
/*  89 */       c.reset();
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public long getActiveConnections() {
/*  95 */     long count = 0L;
/*  96 */     for (ConnectorStatistics c : this.connectorStatistics) {
/*  97 */       count += c.getActiveConnections();
/*     */     }
/*  99 */     return count;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getMaxActiveConnections() {
/* 104 */     long count = 0L;
/* 105 */     for (ConnectorStatistics c : this.connectorStatistics) {
/* 106 */       count += c.getMaxActiveConnections();
/*     */     }
/* 108 */     return count;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getActiveRequests() {
/* 113 */     long count = 0L;
/* 114 */     for (ConnectorStatistics c : this.connectorStatistics) {
/* 115 */       count += c.getActiveRequests();
/*     */     }
/* 117 */     return count;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getMaxActiveRequests() {
/* 122 */     long count = 0L;
/* 123 */     for (ConnectorStatistics c : this.connectorStatistics) {
/* 124 */       count += c.getMaxActiveRequests();
/*     */     }
/* 126 */     return count;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\AggregateConnectorStatistics.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */