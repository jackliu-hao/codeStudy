/*     */ package io.undertow.server.handlers;
/*     */ 
/*     */ import io.undertow.server.ExchangeCompletionListener;
/*     */ import io.undertow.server.HandlerWrapper;
/*     */ import io.undertow.server.HttpHandler;
/*     */ import io.undertow.server.HttpServerExchange;
/*     */ import java.util.Date;
/*     */ import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
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
/*     */ 
/*     */ public class MetricsHandler
/*     */   implements HttpHandler
/*     */ {
/*  37 */   public static final HandlerWrapper WRAPPER = new HandlerWrapper()
/*     */     {
/*     */       public HttpHandler wrap(HttpHandler handler) {
/*  40 */         return new MetricsHandler(handler);
/*     */       }
/*     */     };
/*     */   
/*  44 */   private volatile MetricResult totalResult = new MetricResult(new Date());
/*     */   private final HttpHandler next;
/*     */   
/*     */   public MetricsHandler(HttpHandler next) {
/*  48 */     this.next = next;
/*     */   }
/*     */ 
/*     */   
/*     */   public void handleRequest(HttpServerExchange exchange) throws Exception {
/*  53 */     if (!exchange.isComplete()) {
/*  54 */       final long start = System.currentTimeMillis();
/*  55 */       exchange.addExchangeCompleteListener(new ExchangeCompletionListener()
/*     */           {
/*     */             public void exchangeEvent(HttpServerExchange exchange, ExchangeCompletionListener.NextListener nextListener) {
/*  58 */               long time = System.currentTimeMillis() - start;
/*  59 */               MetricsHandler.this.totalResult.update((int)time, exchange.getStatusCode());
/*  60 */               nextListener.proceed();
/*     */             }
/*     */           });
/*     */     } 
/*  64 */     this.next.handleRequest(exchange);
/*     */   }
/*     */   
/*     */   public void reset() {
/*  68 */     this.totalResult = new MetricResult(new Date());
/*     */   }
/*     */   
/*     */   public MetricResult getMetrics() {
/*  72 */     return new MetricResult(this.totalResult);
/*     */   }
/*     */   
/*     */   public static class MetricResult
/*     */   {
/*  77 */     private static final AtomicLongFieldUpdater<MetricResult> totalRequestTimeUpdater = AtomicLongFieldUpdater.newUpdater(MetricResult.class, "totalRequestTime");
/*  78 */     private static final AtomicIntegerFieldUpdater<MetricResult> maxRequestTimeUpdater = AtomicIntegerFieldUpdater.newUpdater(MetricResult.class, "maxRequestTime");
/*  79 */     private static final AtomicIntegerFieldUpdater<MetricResult> minRequestTimeUpdater = AtomicIntegerFieldUpdater.newUpdater(MetricResult.class, "minRequestTime");
/*  80 */     private static final AtomicLongFieldUpdater<MetricResult> invocationsUpdater = AtomicLongFieldUpdater.newUpdater(MetricResult.class, "totalRequests");
/*  81 */     private static final AtomicLongFieldUpdater<MetricResult> errorsUpdater = AtomicLongFieldUpdater.newUpdater(MetricResult.class, "totalErrors");
/*     */     
/*     */     private final Date metricsStartDate;
/*     */     
/*     */     private volatile long totalRequestTime;
/*     */     private volatile int maxRequestTime;
/*  87 */     private volatile int minRequestTime = -1;
/*     */     private volatile long totalRequests;
/*     */     private volatile long totalErrors;
/*     */     
/*     */     public MetricResult(Date metricsStartDate) {
/*  92 */       this.metricsStartDate = metricsStartDate;
/*     */     }
/*     */     
/*     */     public MetricResult(MetricResult copy) {
/*  96 */       this.metricsStartDate = copy.metricsStartDate;
/*  97 */       this.totalRequestTime = copy.totalRequestTime;
/*  98 */       this.maxRequestTime = copy.maxRequestTime;
/*  99 */       this.minRequestTime = copy.minRequestTime;
/* 100 */       this.totalRequests = copy.totalRequests;
/* 101 */       this.totalErrors = copy.totalErrors;
/*     */     }
/*     */     void update(int requestTime, int statusCode) {
/*     */       int maxRequestTime, minRequestTime;
/* 105 */       totalRequestTimeUpdater.addAndGet(this, requestTime);
/*     */       
/*     */       do {
/* 108 */         maxRequestTime = this.maxRequestTime;
/* 109 */         if (requestTime < maxRequestTime) {
/*     */           break;
/*     */         }
/* 112 */       } while (!maxRequestTimeUpdater.compareAndSet(this, maxRequestTime, requestTime));
/*     */ 
/*     */       
/*     */       do {
/* 116 */         minRequestTime = this.minRequestTime;
/* 117 */         if (requestTime > minRequestTime && minRequestTime != -1) {
/*     */           break;
/*     */         }
/* 120 */       } while (!minRequestTimeUpdater.compareAndSet(this, minRequestTime, requestTime));
/* 121 */       invocationsUpdater.incrementAndGet(this);
/* 122 */       if (statusCode >= 400) {
/* 123 */         errorsUpdater.incrementAndGet(this);
/*     */       }
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public Date getMetricsStartDate() {
/* 130 */       return this.metricsStartDate;
/*     */     }
/*     */     
/*     */     public long getTotalRequestTime() {
/* 134 */       return this.totalRequestTime;
/*     */     }
/*     */     
/*     */     public int getMaxRequestTime() {
/* 138 */       return this.maxRequestTime;
/*     */     }
/*     */     
/*     */     public int getMinRequestTime() {
/* 142 */       return this.minRequestTime;
/*     */     }
/*     */     
/*     */     public long getTotalRequests() {
/* 146 */       return this.totalRequests;
/*     */     }
/*     */     
/*     */     public long getTotalErrors() {
/* 150 */       return this.totalErrors;
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\handlers\MetricsHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */