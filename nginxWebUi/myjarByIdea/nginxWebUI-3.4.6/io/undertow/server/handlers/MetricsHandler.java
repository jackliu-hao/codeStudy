package io.undertow.server.handlers;

import io.undertow.server.ExchangeCompletionListener;
import io.undertow.server.HandlerWrapper;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import java.util.Date;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.concurrent.atomic.AtomicLongFieldUpdater;

public class MetricsHandler implements HttpHandler {
   public static final HandlerWrapper WRAPPER = new HandlerWrapper() {
      public HttpHandler wrap(HttpHandler handler) {
         return new MetricsHandler(handler);
      }
   };
   private volatile MetricResult totalResult = new MetricResult(new Date());
   private final HttpHandler next;

   public MetricsHandler(HttpHandler next) {
      this.next = next;
   }

   public void handleRequest(HttpServerExchange exchange) throws Exception {
      if (!exchange.isComplete()) {
         final long start = System.currentTimeMillis();
         exchange.addExchangeCompleteListener(new ExchangeCompletionListener() {
            public void exchangeEvent(HttpServerExchange exchange, ExchangeCompletionListener.NextListener nextListener) {
               long time = System.currentTimeMillis() - start;
               MetricsHandler.this.totalResult.update((int)time, exchange.getStatusCode());
               nextListener.proceed();
            }
         });
      }

      this.next.handleRequest(exchange);
   }

   public void reset() {
      this.totalResult = new MetricResult(new Date());
   }

   public MetricResult getMetrics() {
      return new MetricResult(this.totalResult);
   }

   public static class MetricResult {
      private static final AtomicLongFieldUpdater<MetricResult> totalRequestTimeUpdater = AtomicLongFieldUpdater.newUpdater(MetricResult.class, "totalRequestTime");
      private static final AtomicIntegerFieldUpdater<MetricResult> maxRequestTimeUpdater = AtomicIntegerFieldUpdater.newUpdater(MetricResult.class, "maxRequestTime");
      private static final AtomicIntegerFieldUpdater<MetricResult> minRequestTimeUpdater = AtomicIntegerFieldUpdater.newUpdater(MetricResult.class, "minRequestTime");
      private static final AtomicLongFieldUpdater<MetricResult> invocationsUpdater = AtomicLongFieldUpdater.newUpdater(MetricResult.class, "totalRequests");
      private static final AtomicLongFieldUpdater<MetricResult> errorsUpdater = AtomicLongFieldUpdater.newUpdater(MetricResult.class, "totalErrors");
      private final Date metricsStartDate;
      private volatile long totalRequestTime;
      private volatile int maxRequestTime;
      private volatile int minRequestTime = -1;
      private volatile long totalRequests;
      private volatile long totalErrors;

      public MetricResult(Date metricsStartDate) {
         this.metricsStartDate = metricsStartDate;
      }

      public MetricResult(MetricResult copy) {
         this.metricsStartDate = copy.metricsStartDate;
         this.totalRequestTime = copy.totalRequestTime;
         this.maxRequestTime = copy.maxRequestTime;
         this.minRequestTime = copy.minRequestTime;
         this.totalRequests = copy.totalRequests;
         this.totalErrors = copy.totalErrors;
      }

      void update(int requestTime, int statusCode) {
         totalRequestTimeUpdater.addAndGet(this, (long)requestTime);

         int maxRequestTime;
         do {
            maxRequestTime = this.maxRequestTime;
         } while(requestTime >= maxRequestTime && !maxRequestTimeUpdater.compareAndSet(this, maxRequestTime, requestTime));

         int minRequestTime;
         do {
            minRequestTime = this.minRequestTime;
         } while((requestTime <= minRequestTime || minRequestTime == -1) && !minRequestTimeUpdater.compareAndSet(this, minRequestTime, requestTime));

         invocationsUpdater.incrementAndGet(this);
         if (statusCode >= 400) {
            errorsUpdater.incrementAndGet(this);
         }

      }

      public Date getMetricsStartDate() {
         return this.metricsStartDate;
      }

      public long getTotalRequestTime() {
         return this.totalRequestTime;
      }

      public int getMaxRequestTime() {
         return this.maxRequestTime;
      }

      public int getMinRequestTime() {
         return this.minRequestTime;
      }

      public long getTotalRequests() {
         return this.totalRequests;
      }

      public long getTotalErrors() {
         return this.totalErrors;
      }
   }
}
