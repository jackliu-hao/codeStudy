/*     */ package com.zaxxer.hikari.metrics.prometheus;
/*     */ 
/*     */ import com.zaxxer.hikari.metrics.IMetricsTracker;
/*     */ import io.prometheus.client.CollectorRegistry;
/*     */ import io.prometheus.client.Counter;
/*     */ import io.prometheus.client.Summary;
/*     */ import java.util.Map;
/*     */ import java.util.concurrent.ConcurrentHashMap;
/*     */ import java.util.concurrent.TimeUnit;
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
/*     */ class PrometheusMetricsTracker
/*     */   implements IMetricsTracker
/*     */ {
/*  33 */   private static final Counter CONNECTION_TIMEOUT_COUNTER = ((Counter.Builder)((Counter.Builder)((Counter.Builder)Counter.build()
/*  34 */     .name("hikaricp_connection_timeout_total"))
/*  35 */     .labelNames(new String[] { "pool"
/*  36 */       })).help("Connection timeout total count"))
/*  37 */     .create();
/*     */ 
/*     */   
/*  40 */   private static final Summary ELAPSED_ACQUIRED_SUMMARY = createSummary("hikaricp_connection_acquired_nanos", "Connection acquired time (ns)");
/*     */ 
/*     */   
/*  43 */   private static final Summary ELAPSED_USAGE_SUMMARY = createSummary("hikaricp_connection_usage_millis", "Connection usage (ms)");
/*     */ 
/*     */   
/*  46 */   private static final Summary ELAPSED_CREATION_SUMMARY = createSummary("hikaricp_connection_creation_millis", "Connection creation (ms)");
/*     */   
/*  48 */   private static final Map<CollectorRegistry, PrometheusMetricsTrackerFactory.RegistrationStatus> registrationStatuses = new ConcurrentHashMap<>();
/*     */   
/*     */   private final String poolName;
/*     */   
/*     */   private final HikariCPCollector hikariCPCollector;
/*     */   
/*     */   private final Counter.Child connectionTimeoutCounterChild;
/*     */   
/*     */   private final Summary.Child elapsedAcquiredSummaryChild;
/*     */   private final Summary.Child elapsedUsageSummaryChild;
/*     */   private final Summary.Child elapsedCreationSummaryChild;
/*     */   
/*     */   PrometheusMetricsTracker(String poolName, CollectorRegistry collectorRegistry, HikariCPCollector hikariCPCollector) {
/*  61 */     registerMetrics(collectorRegistry);
/*  62 */     this.poolName = poolName;
/*  63 */     this.hikariCPCollector = hikariCPCollector;
/*  64 */     this.connectionTimeoutCounterChild = (Counter.Child)CONNECTION_TIMEOUT_COUNTER.labels(new String[] { poolName });
/*  65 */     this.elapsedAcquiredSummaryChild = (Summary.Child)ELAPSED_ACQUIRED_SUMMARY.labels(new String[] { poolName });
/*  66 */     this.elapsedUsageSummaryChild = (Summary.Child)ELAPSED_USAGE_SUMMARY.labels(new String[] { poolName });
/*  67 */     this.elapsedCreationSummaryChild = (Summary.Child)ELAPSED_CREATION_SUMMARY.labels(new String[] { poolName });
/*     */   }
/*     */ 
/*     */   
/*     */   private void registerMetrics(CollectorRegistry collectorRegistry) {
/*  72 */     if (registrationStatuses.putIfAbsent(collectorRegistry, PrometheusMetricsTrackerFactory.RegistrationStatus.REGISTERED) == null) {
/*  73 */       CONNECTION_TIMEOUT_COUNTER.register(collectorRegistry);
/*  74 */       ELAPSED_ACQUIRED_SUMMARY.register(collectorRegistry);
/*  75 */       ELAPSED_USAGE_SUMMARY.register(collectorRegistry);
/*  76 */       ELAPSED_CREATION_SUMMARY.register(collectorRegistry);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void recordConnectionAcquiredNanos(long elapsedAcquiredNanos) {
/*  83 */     this.elapsedAcquiredSummaryChild.observe(elapsedAcquiredNanos);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void recordConnectionUsageMillis(long elapsedBorrowedMillis) {
/*  89 */     this.elapsedUsageSummaryChild.observe(elapsedBorrowedMillis);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void recordConnectionCreatedMillis(long connectionCreatedMillis) {
/*  95 */     this.elapsedCreationSummaryChild.observe(connectionCreatedMillis);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void recordConnectionTimeout() {
/* 101 */     this.connectionTimeoutCounterChild.inc();
/*     */   }
/*     */ 
/*     */   
/*     */   private static Summary createSummary(String name, String help) {
/* 106 */     return ((Summary.Builder)((Summary.Builder)((Summary.Builder)Summary.build()
/* 107 */       .name(name))
/* 108 */       .labelNames(new String[] { "pool"
/* 109 */         })).help(help))
/* 110 */       .quantile(0.5D, 0.05D)
/* 111 */       .quantile(0.95D, 0.01D)
/* 112 */       .quantile(0.99D, 0.001D)
/* 113 */       .maxAgeSeconds(TimeUnit.MINUTES.toSeconds(5L))
/* 114 */       .ageBuckets(5)
/* 115 */       .create();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void close() {
/* 121 */     this.hikariCPCollector.remove(this.poolName);
/* 122 */     CONNECTION_TIMEOUT_COUNTER.remove(new String[] { this.poolName });
/* 123 */     ELAPSED_ACQUIRED_SUMMARY.remove(new String[] { this.poolName });
/* 124 */     ELAPSED_USAGE_SUMMARY.remove(new String[] { this.poolName });
/* 125 */     ELAPSED_CREATION_SUMMARY.remove(new String[] { this.poolName });
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\zaxxer\hikari\metrics\prometheus\PrometheusMetricsTracker.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */