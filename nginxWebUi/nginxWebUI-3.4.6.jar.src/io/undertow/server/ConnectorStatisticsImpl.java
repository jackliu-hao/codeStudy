/*     */ package io.undertow.server;
/*     */ 
/*     */ import io.undertow.conduits.ByteActivityCallback;
/*     */ import java.util.concurrent.atomic.AtomicLongFieldUpdater;
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
/*     */ 
/*     */ 
/*     */ public class ConnectorStatisticsImpl
/*     */   implements ConnectorStatistics
/*     */ {
/*  31 */   private static final AtomicLongFieldUpdater<ConnectorStatisticsImpl> requestCountUpdater = AtomicLongFieldUpdater.newUpdater(ConnectorStatisticsImpl.class, "requestCount");
/*  32 */   private static final AtomicLongFieldUpdater<ConnectorStatisticsImpl> bytesSentUpdater = AtomicLongFieldUpdater.newUpdater(ConnectorStatisticsImpl.class, "bytesSent");
/*  33 */   private static final AtomicLongFieldUpdater<ConnectorStatisticsImpl> bytesReceivedUpdater = AtomicLongFieldUpdater.newUpdater(ConnectorStatisticsImpl.class, "bytesReceived");
/*  34 */   private static final AtomicLongFieldUpdater<ConnectorStatisticsImpl> errorCountUpdater = AtomicLongFieldUpdater.newUpdater(ConnectorStatisticsImpl.class, "errorCount");
/*  35 */   private static final AtomicLongFieldUpdater<ConnectorStatisticsImpl> processingTimeUpdater = AtomicLongFieldUpdater.newUpdater(ConnectorStatisticsImpl.class, "processingTime");
/*  36 */   private static final AtomicLongFieldUpdater<ConnectorStatisticsImpl> maxProcessingTimeUpdater = AtomicLongFieldUpdater.newUpdater(ConnectorStatisticsImpl.class, "maxProcessingTime");
/*  37 */   private static final AtomicLongFieldUpdater<ConnectorStatisticsImpl> activeConnectionsUpdater = AtomicLongFieldUpdater.newUpdater(ConnectorStatisticsImpl.class, "activeConnections");
/*  38 */   private static final AtomicLongFieldUpdater<ConnectorStatisticsImpl> maxActiveConnectionsUpdater = AtomicLongFieldUpdater.newUpdater(ConnectorStatisticsImpl.class, "maxActiveConnections");
/*  39 */   private static final AtomicLongFieldUpdater<ConnectorStatisticsImpl> activeRequestsUpdater = AtomicLongFieldUpdater.newUpdater(ConnectorStatisticsImpl.class, "activeRequests");
/*  40 */   private static final AtomicLongFieldUpdater<ConnectorStatisticsImpl> maxActiveRequestsUpdater = AtomicLongFieldUpdater.newUpdater(ConnectorStatisticsImpl.class, "maxActiveRequests");
/*     */   
/*     */   private volatile long requestCount;
/*     */   private volatile long bytesSent;
/*     */   private volatile long bytesReceived;
/*     */   private volatile long errorCount;
/*     */   private volatile long processingTime;
/*     */   private volatile long maxProcessingTime;
/*     */   private volatile long activeConnections;
/*     */   private volatile long maxActiveConnections;
/*     */   private volatile long activeRequests;
/*     */   private volatile long maxActiveRequests;
/*     */   
/*  53 */   private final ExchangeCompletionListener completionListener = new ExchangeCompletionListener()
/*     */     {
/*     */       public void exchangeEvent(HttpServerExchange exchange, ExchangeCompletionListener.NextListener nextListener) {
/*     */         try {
/*  57 */           ConnectorStatisticsImpl.activeRequestsUpdater.decrementAndGet(ConnectorStatisticsImpl.this);
/*  58 */           if (exchange.getStatusCode() == 500) {
/*  59 */             ConnectorStatisticsImpl.errorCountUpdater.incrementAndGet(ConnectorStatisticsImpl.this);
/*     */           }
/*  61 */           long start = exchange.getRequestStartTime();
/*  62 */           if (start > 0L) {
/*  63 */             long oldMax, elapsed = System.nanoTime() - start;
/*  64 */             ConnectorStatisticsImpl.processingTimeUpdater.addAndGet(ConnectorStatisticsImpl.this, elapsed);
/*     */             
/*     */             do {
/*  67 */               oldMax = ConnectorStatisticsImpl.maxProcessingTimeUpdater.get(ConnectorStatisticsImpl.this);
/*  68 */               if (oldMax >= elapsed) {
/*     */                 break;
/*     */               }
/*  71 */             } while (!ConnectorStatisticsImpl.maxProcessingTimeUpdater.compareAndSet(ConnectorStatisticsImpl.this, oldMax, elapsed));
/*     */           } 
/*     */         } finally {
/*     */           
/*  75 */           nextListener.proceed();
/*     */         } 
/*     */       }
/*     */     };
/*     */   
/*  80 */   private final ByteActivityCallback bytesSentAccumulator = new BytesSentAccumulator();
/*  81 */   private final ByteActivityCallback bytesReceivedAccumulator = new BytesReceivedAccumulator();
/*     */ 
/*     */   
/*     */   public long getRequestCount() {
/*  85 */     return requestCountUpdater.get(this);
/*     */   }
/*     */ 
/*     */   
/*     */   public long getBytesSent() {
/*  90 */     return bytesSentUpdater.get(this);
/*     */   }
/*     */ 
/*     */   
/*     */   public long getBytesReceived() {
/*  95 */     return bytesReceivedUpdater.get(this);
/*     */   }
/*     */ 
/*     */   
/*     */   public long getErrorCount() {
/* 100 */     return errorCountUpdater.get(this);
/*     */   }
/*     */ 
/*     */   
/*     */   public long getProcessingTime() {
/* 105 */     return processingTimeUpdater.get(this);
/*     */   }
/*     */ 
/*     */   
/*     */   public long getMaxProcessingTime() {
/* 110 */     return maxProcessingTimeUpdater.get(this);
/*     */   }
/*     */ 
/*     */   
/*     */   public void reset() {
/* 115 */     requestCountUpdater.set(this, 0L);
/* 116 */     bytesSentUpdater.set(this, 0L);
/* 117 */     bytesReceivedUpdater.set(this, 0L);
/* 118 */     errorCountUpdater.set(this, 0L);
/* 119 */     maxProcessingTimeUpdater.set(this, 0L);
/* 120 */     processingTimeUpdater.set(this, 0L);
/* 121 */     maxActiveConnectionsUpdater.set(this, 0L);
/* 122 */     maxActiveRequestsUpdater.set(this, 0L);
/*     */   }
/*     */ 
/*     */   
/*     */   public void requestFinished(long bytesSent, long bytesReceived, boolean error) {
/* 127 */     bytesSentUpdater.addAndGet(this, bytesSent);
/* 128 */     bytesReceivedUpdater.addAndGet(this, bytesReceived);
/* 129 */     if (error) {
/* 130 */       errorCountUpdater.incrementAndGet(this);
/*     */     }
/*     */   }
/*     */   
/*     */   public void updateBytesSent(long bytes) {
/* 135 */     bytesSentUpdater.addAndGet(this, bytes);
/*     */   }
/*     */   
/*     */   public void updateBytesReceived(long bytes) {
/* 139 */     bytesReceivedUpdater.addAndGet(this, bytes);
/*     */   }
/*     */   public void setup(HttpServerExchange exchange) {
/*     */     long maxActiveRequests;
/* 143 */     requestCountUpdater.incrementAndGet(this);
/* 144 */     long current = activeRequestsUpdater.incrementAndGet(this);
/*     */     
/*     */     do {
/* 147 */       maxActiveRequests = this.maxActiveRequests;
/* 148 */       if (current <= maxActiveRequests) {
/*     */         break;
/*     */       }
/* 151 */     } while (!maxActiveRequestsUpdater.compareAndSet(this, maxActiveRequests, current));
/* 152 */     exchange.addExchangeCompleteListener(this.completionListener);
/*     */   }
/*     */   
/*     */   public ByteActivityCallback sentAccumulator() {
/* 156 */     return this.bytesSentAccumulator;
/*     */   }
/*     */   
/*     */   public ByteActivityCallback receivedAccumulator() {
/* 160 */     return this.bytesReceivedAccumulator;
/*     */   }
/*     */   
/*     */   private class BytesSentAccumulator implements ByteActivityCallback {
/*     */     private BytesSentAccumulator() {}
/*     */     
/*     */     public void activity(long bytes) {
/* 167 */       ConnectorStatisticsImpl.bytesSentUpdater.addAndGet(ConnectorStatisticsImpl.this, bytes);
/*     */     } }
/*     */   
/*     */   private class BytesReceivedAccumulator implements ByteActivityCallback {
/*     */     private BytesReceivedAccumulator() {}
/*     */     
/*     */     public void activity(long bytes) {
/* 174 */       ConnectorStatisticsImpl.bytesReceivedUpdater.addAndGet(ConnectorStatisticsImpl.this, bytes);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public long getActiveConnections() {
/* 180 */     return this.activeConnections;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getMaxActiveConnections() {
/* 185 */     return this.maxActiveConnections;
/*     */   }
/*     */   
/*     */   public void incrementConnectionCount() {
/* 189 */     long maxActiveConnections, current = activeConnectionsUpdater.incrementAndGet(this);
/*     */     
/*     */     do {
/* 192 */       maxActiveConnections = this.maxActiveConnections;
/* 193 */       if (current <= maxActiveConnections) {
/*     */         return;
/*     */       }
/* 196 */     } while (!maxActiveConnectionsUpdater.compareAndSet(this, maxActiveConnections, current));
/*     */   }
/*     */   
/*     */   public void decrementConnectionCount() {
/* 200 */     activeConnectionsUpdater.decrementAndGet(this);
/*     */   }
/*     */   
/*     */   public long getActiveRequests() {
/* 204 */     return this.activeRequests;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getMaxActiveRequests() {
/* 209 */     return this.maxActiveRequests;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\ConnectorStatisticsImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */