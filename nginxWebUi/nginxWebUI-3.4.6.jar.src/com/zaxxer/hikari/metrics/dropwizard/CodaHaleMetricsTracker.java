/*     */ package com.zaxxer.hikari.metrics.dropwizard;
/*     */ 
/*     */ import com.codahale.metrics.Histogram;
/*     */ import com.codahale.metrics.Meter;
/*     */ import com.codahale.metrics.Metric;
/*     */ import com.codahale.metrics.MetricRegistry;
/*     */ import com.codahale.metrics.Timer;
/*     */ import com.zaxxer.hikari.metrics.IMetricsTracker;
/*     */ import com.zaxxer.hikari.metrics.PoolStats;
/*     */ import java.util.Objects;
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
/*     */ public final class CodaHaleMetricsTracker
/*     */   implements IMetricsTracker
/*     */ {
/*     */   private final String poolName;
/*     */   private final Timer connectionObtainTimer;
/*     */   private final Histogram connectionUsage;
/*     */   private final Histogram connectionCreation;
/*     */   private final Meter connectionTimeoutMeter;
/*     */   private final MetricRegistry registry;
/*     */   private static final String METRIC_CATEGORY = "pool";
/*     */   private static final String METRIC_NAME_WAIT = "Wait";
/*     */   private static final String METRIC_NAME_USAGE = "Usage";
/*     */   private static final String METRIC_NAME_CONNECT = "ConnectionCreation";
/*     */   private static final String METRIC_NAME_TIMEOUT_RATE = "ConnectionTimeoutRate";
/*     */   private static final String METRIC_NAME_TOTAL_CONNECTIONS = "TotalConnections";
/*     */   private static final String METRIC_NAME_IDLE_CONNECTIONS = "IdleConnections";
/*     */   private static final String METRIC_NAME_ACTIVE_CONNECTIONS = "ActiveConnections";
/*     */   private static final String METRIC_NAME_PENDING_CONNECTIONS = "PendingConnections";
/*     */   private static final String METRIC_NAME_MAX_CONNECTIONS = "MaxConnections";
/*     */   private static final String METRIC_NAME_MIN_CONNECTIONS = "MinConnections";
/*     */   
/*     */   CodaHaleMetricsTracker(String poolName, PoolStats poolStats, MetricRegistry registry) {
/*  52 */     this.poolName = poolName;
/*  53 */     this.registry = registry;
/*  54 */     this.connectionObtainTimer = registry.timer(MetricRegistry.name(poolName, new String[] { "pool", "Wait" }));
/*  55 */     this.connectionUsage = registry.histogram(MetricRegistry.name(poolName, new String[] { "pool", "Usage" }));
/*  56 */     this.connectionCreation = registry.histogram(MetricRegistry.name(poolName, new String[] { "pool", "ConnectionCreation" }));
/*  57 */     this.connectionTimeoutMeter = registry.meter(MetricRegistry.name(poolName, new String[] { "pool", "ConnectionTimeoutRate" }));
/*     */ 
/*     */     
/*  60 */     Objects.requireNonNull(poolStats); registry.register(MetricRegistry.name(poolName, new String[] { "pool", "TotalConnections" }), (Metric)poolStats::getTotalConnections);
/*     */ 
/*     */     
/*  63 */     Objects.requireNonNull(poolStats); registry.register(MetricRegistry.name(poolName, new String[] { "pool", "IdleConnections" }), (Metric)poolStats::getIdleConnections);
/*     */ 
/*     */     
/*  66 */     Objects.requireNonNull(poolStats); registry.register(MetricRegistry.name(poolName, new String[] { "pool", "ActiveConnections" }), (Metric)poolStats::getActiveConnections);
/*     */ 
/*     */     
/*  69 */     Objects.requireNonNull(poolStats); registry.register(MetricRegistry.name(poolName, new String[] { "pool", "PendingConnections" }), (Metric)poolStats::getPendingThreads);
/*     */ 
/*     */     
/*  72 */     Objects.requireNonNull(poolStats); registry.register(MetricRegistry.name(poolName, new String[] { "pool", "MaxConnections" }), (Metric)poolStats::getMaxConnections);
/*     */ 
/*     */     
/*  75 */     Objects.requireNonNull(poolStats); registry.register(MetricRegistry.name(poolName, new String[] { "pool", "MinConnections" }), (Metric)poolStats::getMinConnections);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void close() {
/*  82 */     this.registry.remove(MetricRegistry.name(this.poolName, new String[] { "pool", "Wait" }));
/*  83 */     this.registry.remove(MetricRegistry.name(this.poolName, new String[] { "pool", "Usage" }));
/*  84 */     this.registry.remove(MetricRegistry.name(this.poolName, new String[] { "pool", "ConnectionCreation" }));
/*  85 */     this.registry.remove(MetricRegistry.name(this.poolName, new String[] { "pool", "ConnectionTimeoutRate" }));
/*  86 */     this.registry.remove(MetricRegistry.name(this.poolName, new String[] { "pool", "TotalConnections" }));
/*  87 */     this.registry.remove(MetricRegistry.name(this.poolName, new String[] { "pool", "IdleConnections" }));
/*  88 */     this.registry.remove(MetricRegistry.name(this.poolName, new String[] { "pool", "ActiveConnections" }));
/*  89 */     this.registry.remove(MetricRegistry.name(this.poolName, new String[] { "pool", "PendingConnections" }));
/*  90 */     this.registry.remove(MetricRegistry.name(this.poolName, new String[] { "pool", "MaxConnections" }));
/*  91 */     this.registry.remove(MetricRegistry.name(this.poolName, new String[] { "pool", "MinConnections" }));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void recordConnectionAcquiredNanos(long elapsedAcquiredNanos) {
/*  98 */     this.connectionObtainTimer.update(elapsedAcquiredNanos, TimeUnit.NANOSECONDS);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void recordConnectionUsageMillis(long elapsedBorrowedMillis) {
/* 105 */     this.connectionUsage.update(elapsedBorrowedMillis);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void recordConnectionTimeout() {
/* 111 */     this.connectionTimeoutMeter.mark();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void recordConnectionCreatedMillis(long connectionCreatedMillis) {
/* 117 */     this.connectionCreation.update(connectionCreatedMillis);
/*     */   }
/*     */ 
/*     */   
/*     */   public Timer getConnectionAcquisitionTimer() {
/* 122 */     return this.connectionObtainTimer;
/*     */   }
/*     */ 
/*     */   
/*     */   public Histogram getConnectionDurationHistogram() {
/* 127 */     return this.connectionUsage;
/*     */   }
/*     */ 
/*     */   
/*     */   public Histogram getConnectionCreationHistogram() {
/* 132 */     return this.connectionCreation;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\zaxxer\hikari\metrics\dropwizard\CodaHaleMetricsTracker.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */