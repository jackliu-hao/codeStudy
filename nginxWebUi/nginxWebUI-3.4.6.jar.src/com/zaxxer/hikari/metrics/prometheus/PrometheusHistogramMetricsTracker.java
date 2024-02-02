/*     */ package com.zaxxer.hikari.metrics.prometheus;
/*     */ 
/*     */ import com.zaxxer.hikari.metrics.IMetricsTracker;
/*     */ import io.prometheus.client.CollectorRegistry;
/*     */ import io.prometheus.client.Counter;
/*     */ import io.prometheus.client.Histogram;
/*     */ import java.util.Map;
/*     */ import java.util.concurrent.ConcurrentHashMap;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ class PrometheusHistogramMetricsTracker
/*     */   implements IMetricsTracker
/*     */ {
/*  39 */   private static final Counter CONNECTION_TIMEOUT_COUNTER = ((Counter.Builder)((Counter.Builder)((Counter.Builder)Counter.build()
/*  40 */     .name("hikaricp_connection_timeout_total"))
/*  41 */     .labelNames(new String[] { "pool"
/*  42 */       })).help("Connection timeout total count"))
/*  43 */     .create();
/*     */ 
/*     */   
/*  46 */   private static final Histogram ELAPSED_ACQUIRED_HISTOGRAM = registerHistogram("hikaricp_connection_acquired_nanos", "Connection acquired time (ns)", 1000.0D);
/*     */ 
/*     */   
/*  49 */   private static final Histogram ELAPSED_BORROWED_HISTOGRAM = registerHistogram("hikaricp_connection_usage_millis", "Connection usage (ms)", 1.0D);
/*     */ 
/*     */   
/*  52 */   private static final Histogram ELAPSED_CREATION_HISTOGRAM = registerHistogram("hikaricp_connection_creation_millis", "Connection creation (ms)", 1.0D);
/*     */   
/*     */   private final Counter.Child connectionTimeoutCounterChild;
/*     */   
/*     */   private static Histogram registerHistogram(String name, String help, double bucketStart) {
/*  57 */     return ((Histogram.Builder)((Histogram.Builder)((Histogram.Builder)Histogram.build()
/*  58 */       .name(name))
/*  59 */       .labelNames(new String[] { "pool"
/*  60 */         })).help(help))
/*  61 */       .exponentialBuckets(bucketStart, 2.0D, 11)
/*  62 */       .create();
/*     */   }
/*     */   
/*  65 */   private static final Map<CollectorRegistry, PrometheusMetricsTrackerFactory.RegistrationStatus> registrationStatuses = new ConcurrentHashMap<>();
/*     */   
/*     */   private final String poolName;
/*     */   
/*     */   private final HikariCPCollector hikariCPCollector;
/*     */   private final Histogram.Child elapsedAcquiredHistogramChild;
/*     */   private final Histogram.Child elapsedBorrowedHistogramChild;
/*     */   private final Histogram.Child elapsedCreationHistogramChild;
/*     */   
/*     */   PrometheusHistogramMetricsTracker(String poolName, CollectorRegistry collectorRegistry, HikariCPCollector hikariCPCollector) {
/*  75 */     registerMetrics(collectorRegistry);
/*  76 */     this.poolName = poolName;
/*  77 */     this.hikariCPCollector = hikariCPCollector;
/*  78 */     this.connectionTimeoutCounterChild = (Counter.Child)CONNECTION_TIMEOUT_COUNTER.labels(new String[] { poolName });
/*  79 */     this.elapsedAcquiredHistogramChild = (Histogram.Child)ELAPSED_ACQUIRED_HISTOGRAM.labels(new String[] { poolName });
/*  80 */     this.elapsedBorrowedHistogramChild = (Histogram.Child)ELAPSED_BORROWED_HISTOGRAM.labels(new String[] { poolName });
/*  81 */     this.elapsedCreationHistogramChild = (Histogram.Child)ELAPSED_CREATION_HISTOGRAM.labels(new String[] { poolName });
/*     */   }
/*     */   
/*     */   private void registerMetrics(CollectorRegistry collectorRegistry) {
/*  85 */     if (registrationStatuses.putIfAbsent(collectorRegistry, PrometheusMetricsTrackerFactory.RegistrationStatus.REGISTERED) == null) {
/*  86 */       CONNECTION_TIMEOUT_COUNTER.register(collectorRegistry);
/*  87 */       ELAPSED_ACQUIRED_HISTOGRAM.register(collectorRegistry);
/*  88 */       ELAPSED_BORROWED_HISTOGRAM.register(collectorRegistry);
/*  89 */       ELAPSED_CREATION_HISTOGRAM.register(collectorRegistry);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void recordConnectionAcquiredNanos(long elapsedAcquiredNanos) {
/*  95 */     this.elapsedAcquiredHistogramChild.observe(elapsedAcquiredNanos);
/*     */   }
/*     */ 
/*     */   
/*     */   public void recordConnectionUsageMillis(long elapsedBorrowedMillis) {
/* 100 */     this.elapsedBorrowedHistogramChild.observe(elapsedBorrowedMillis);
/*     */   }
/*     */ 
/*     */   
/*     */   public void recordConnectionCreatedMillis(long connectionCreatedMillis) {
/* 105 */     this.elapsedCreationHistogramChild.observe(connectionCreatedMillis);
/*     */   }
/*     */ 
/*     */   
/*     */   public void recordConnectionTimeout() {
/* 110 */     this.connectionTimeoutCounterChild.inc();
/*     */   }
/*     */ 
/*     */   
/*     */   public void close() {
/* 115 */     this.hikariCPCollector.remove(this.poolName);
/* 116 */     CONNECTION_TIMEOUT_COUNTER.remove(new String[] { this.poolName });
/* 117 */     ELAPSED_ACQUIRED_HISTOGRAM.remove(new String[] { this.poolName });
/* 118 */     ELAPSED_BORROWED_HISTOGRAM.remove(new String[] { this.poolName });
/* 119 */     ELAPSED_CREATION_HISTOGRAM.remove(new String[] { this.poolName });
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\zaxxer\hikari\metrics\prometheus\PrometheusHistogramMetricsTracker.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */