package io.undertow.server;

import io.undertow.conduits.ByteActivityCallback;
import java.util.concurrent.atomic.AtomicLongFieldUpdater;

public class ConnectorStatisticsImpl implements ConnectorStatistics {
   private static final AtomicLongFieldUpdater<ConnectorStatisticsImpl> requestCountUpdater = AtomicLongFieldUpdater.newUpdater(ConnectorStatisticsImpl.class, "requestCount");
   private static final AtomicLongFieldUpdater<ConnectorStatisticsImpl> bytesSentUpdater = AtomicLongFieldUpdater.newUpdater(ConnectorStatisticsImpl.class, "bytesSent");
   private static final AtomicLongFieldUpdater<ConnectorStatisticsImpl> bytesReceivedUpdater = AtomicLongFieldUpdater.newUpdater(ConnectorStatisticsImpl.class, "bytesReceived");
   private static final AtomicLongFieldUpdater<ConnectorStatisticsImpl> errorCountUpdater = AtomicLongFieldUpdater.newUpdater(ConnectorStatisticsImpl.class, "errorCount");
   private static final AtomicLongFieldUpdater<ConnectorStatisticsImpl> processingTimeUpdater = AtomicLongFieldUpdater.newUpdater(ConnectorStatisticsImpl.class, "processingTime");
   private static final AtomicLongFieldUpdater<ConnectorStatisticsImpl> maxProcessingTimeUpdater = AtomicLongFieldUpdater.newUpdater(ConnectorStatisticsImpl.class, "maxProcessingTime");
   private static final AtomicLongFieldUpdater<ConnectorStatisticsImpl> activeConnectionsUpdater = AtomicLongFieldUpdater.newUpdater(ConnectorStatisticsImpl.class, "activeConnections");
   private static final AtomicLongFieldUpdater<ConnectorStatisticsImpl> maxActiveConnectionsUpdater = AtomicLongFieldUpdater.newUpdater(ConnectorStatisticsImpl.class, "maxActiveConnections");
   private static final AtomicLongFieldUpdater<ConnectorStatisticsImpl> activeRequestsUpdater = AtomicLongFieldUpdater.newUpdater(ConnectorStatisticsImpl.class, "activeRequests");
   private static final AtomicLongFieldUpdater<ConnectorStatisticsImpl> maxActiveRequestsUpdater = AtomicLongFieldUpdater.newUpdater(ConnectorStatisticsImpl.class, "maxActiveRequests");
   private volatile long requestCount;
   private volatile long bytesSent;
   private volatile long bytesReceived;
   private volatile long errorCount;
   private volatile long processingTime;
   private volatile long maxProcessingTime;
   private volatile long activeConnections;
   private volatile long maxActiveConnections;
   private volatile long activeRequests;
   private volatile long maxActiveRequests;
   private final ExchangeCompletionListener completionListener = new ExchangeCompletionListener() {
      public void exchangeEvent(HttpServerExchange exchange, ExchangeCompletionListener.NextListener nextListener) {
         try {
            ConnectorStatisticsImpl.activeRequestsUpdater.decrementAndGet(ConnectorStatisticsImpl.this);
            if (exchange.getStatusCode() == 500) {
               ConnectorStatisticsImpl.errorCountUpdater.incrementAndGet(ConnectorStatisticsImpl.this);
            }

            long start = exchange.getRequestStartTime();
            if (start > 0L) {
               long elapsed = System.nanoTime() - start;
               ConnectorStatisticsImpl.processingTimeUpdater.addAndGet(ConnectorStatisticsImpl.this, elapsed);

               long oldMax;
               do {
                  oldMax = ConnectorStatisticsImpl.maxProcessingTimeUpdater.get(ConnectorStatisticsImpl.this);
               } while(oldMax < elapsed && !ConnectorStatisticsImpl.maxProcessingTimeUpdater.compareAndSet(ConnectorStatisticsImpl.this, oldMax, elapsed));
            }
         } finally {
            nextListener.proceed();
         }

      }
   };
   private final ByteActivityCallback bytesSentAccumulator = new BytesSentAccumulator();
   private final ByteActivityCallback bytesReceivedAccumulator = new BytesReceivedAccumulator();

   public long getRequestCount() {
      return requestCountUpdater.get(this);
   }

   public long getBytesSent() {
      return bytesSentUpdater.get(this);
   }

   public long getBytesReceived() {
      return bytesReceivedUpdater.get(this);
   }

   public long getErrorCount() {
      return errorCountUpdater.get(this);
   }

   public long getProcessingTime() {
      return processingTimeUpdater.get(this);
   }

   public long getMaxProcessingTime() {
      return maxProcessingTimeUpdater.get(this);
   }

   public void reset() {
      requestCountUpdater.set(this, 0L);
      bytesSentUpdater.set(this, 0L);
      bytesReceivedUpdater.set(this, 0L);
      errorCountUpdater.set(this, 0L);
      maxProcessingTimeUpdater.set(this, 0L);
      processingTimeUpdater.set(this, 0L);
      maxActiveConnectionsUpdater.set(this, 0L);
      maxActiveRequestsUpdater.set(this, 0L);
   }

   public void requestFinished(long bytesSent, long bytesReceived, boolean error) {
      bytesSentUpdater.addAndGet(this, bytesSent);
      bytesReceivedUpdater.addAndGet(this, bytesReceived);
      if (error) {
         errorCountUpdater.incrementAndGet(this);
      }

   }

   public void updateBytesSent(long bytes) {
      bytesSentUpdater.addAndGet(this, bytes);
   }

   public void updateBytesReceived(long bytes) {
      bytesReceivedUpdater.addAndGet(this, bytes);
   }

   public void setup(HttpServerExchange exchange) {
      requestCountUpdater.incrementAndGet(this);
      long current = activeRequestsUpdater.incrementAndGet(this);

      long maxActiveRequests;
      do {
         maxActiveRequests = this.maxActiveRequests;
      } while(current > maxActiveRequests && !maxActiveRequestsUpdater.compareAndSet(this, maxActiveRequests, current));

      exchange.addExchangeCompleteListener(this.completionListener);
   }

   public ByteActivityCallback sentAccumulator() {
      return this.bytesSentAccumulator;
   }

   public ByteActivityCallback receivedAccumulator() {
      return this.bytesReceivedAccumulator;
   }

   public long getActiveConnections() {
      return this.activeConnections;
   }

   public long getMaxActiveConnections() {
      return this.maxActiveConnections;
   }

   public void incrementConnectionCount() {
      long current = activeConnectionsUpdater.incrementAndGet(this);

      long maxActiveConnections;
      do {
         maxActiveConnections = this.maxActiveConnections;
         if (current <= maxActiveConnections) {
            return;
         }
      } while(!maxActiveConnectionsUpdater.compareAndSet(this, maxActiveConnections, current));

   }

   public void decrementConnectionCount() {
      activeConnectionsUpdater.decrementAndGet(this);
   }

   public long getActiveRequests() {
      return this.activeRequests;
   }

   public long getMaxActiveRequests() {
      return this.maxActiveRequests;
   }

   private class BytesReceivedAccumulator implements ByteActivityCallback {
      private BytesReceivedAccumulator() {
      }

      public void activity(long bytes) {
         ConnectorStatisticsImpl.bytesReceivedUpdater.addAndGet(ConnectorStatisticsImpl.this, bytes);
      }

      // $FF: synthetic method
      BytesReceivedAccumulator(Object x1) {
         this();
      }
   }

   private class BytesSentAccumulator implements ByteActivityCallback {
      private BytesSentAccumulator() {
      }

      public void activity(long bytes) {
         ConnectorStatisticsImpl.bytesSentUpdater.addAndGet(ConnectorStatisticsImpl.this, bytes);
      }

      // $FF: synthetic method
      BytesSentAccumulator(Object x1) {
         this();
      }
   }
}
