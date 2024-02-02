/*     */ package com.zaxxer.hikari.metrics.micrometer;
/*     */ 
/*     */ import com.zaxxer.hikari.metrics.IMetricsTracker;
/*     */ import com.zaxxer.hikari.metrics.PoolStats;
/*     */ import io.micrometer.core.instrument.Counter;
/*     */ import io.micrometer.core.instrument.Gauge;
/*     */ import io.micrometer.core.instrument.Meter;
/*     */ import io.micrometer.core.instrument.MeterRegistry;
/*     */ import io.micrometer.core.instrument.Timer;
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
/*     */ public class MicrometerMetricsTracker
/*     */   implements IMetricsTracker
/*     */ {
/*     */   public static final String HIKARI_METRIC_NAME_PREFIX = "hikaricp";
/*     */   private static final String METRIC_CATEGORY = "pool";
/*     */   private static final String METRIC_NAME_WAIT = "hikaricp.connections.acquire";
/*     */   private static final String METRIC_NAME_USAGE = "hikaricp.connections.usage";
/*     */   private static final String METRIC_NAME_CONNECT = "hikaricp.connections.creation";
/*     */   private static final String METRIC_NAME_TIMEOUT_RATE = "hikaricp.connections.timeout";
/*     */   private static final String METRIC_NAME_TOTAL_CONNECTIONS = "hikaricp.connections";
/*     */   private static final String METRIC_NAME_IDLE_CONNECTIONS = "hikaricp.connections.idle";
/*     */   private static final String METRIC_NAME_ACTIVE_CONNECTIONS = "hikaricp.connections.active";
/*     */   private static final String METRIC_NAME_PENDING_CONNECTIONS = "hikaricp.connections.pending";
/*     */   private static final String METRIC_NAME_MAX_CONNECTIONS = "hikaricp.connections.max";
/*     */   private static final String METRIC_NAME_MIN_CONNECTIONS = "hikaricp.connections.min";
/*     */   private final Timer connectionObtainTimer;
/*     */   private final Counter connectionTimeoutCounter;
/*     */   private final Timer connectionUsage;
/*     */   private final Timer connectionCreation;
/*     */   private final Gauge totalConnectionGauge;
/*     */   private final Gauge idleConnectionGauge;
/*     */   private final Gauge activeConnectionGauge;
/*     */   private final Gauge pendingConnectionGauge;
/*     */   private final Gauge maxConnectionGauge;
/*     */   private final Gauge minConnectionGauge;
/*     */   private final MeterRegistry meterRegistry;
/*     */   private final PoolStats poolStats;
/*     */   
/*     */   MicrometerMetricsTracker(String poolName, PoolStats poolStats, MeterRegistry meterRegistry) {
/*  76 */     this.poolStats = poolStats;
/*     */     
/*  78 */     this.meterRegistry = meterRegistry;
/*     */     
/*  80 */     this
/*     */ 
/*     */       
/*  83 */       .connectionObtainTimer = Timer.builder("hikaricp.connections.acquire").description("Connection acquire time").tags(new String[] { "pool", poolName }).register(meterRegistry);
/*     */     
/*  85 */     this
/*     */ 
/*     */       
/*  88 */       .connectionCreation = Timer.builder("hikaricp.connections.creation").description("Connection creation time").tags(new String[] { "pool", poolName }).register(meterRegistry);
/*     */     
/*  90 */     this
/*     */ 
/*     */       
/*  93 */       .connectionUsage = Timer.builder("hikaricp.connections.usage").description("Connection usage time").tags(new String[] { "pool", poolName }).register(meterRegistry);
/*     */     
/*  95 */     this
/*     */ 
/*     */       
/*  98 */       .connectionTimeoutCounter = Counter.builder("hikaricp.connections.timeout").description("Connection timeout total count").tags(new String[] { "pool", poolName }).register(meterRegistry);
/*     */     
/* 100 */     this
/*     */ 
/*     */       
/* 103 */       .totalConnectionGauge = Gauge.builder("hikaricp.connections", poolStats, PoolStats::getTotalConnections).description("Total connections").tags(new String[] { "pool", poolName }).register(meterRegistry);
/*     */     
/* 105 */     this
/*     */ 
/*     */       
/* 108 */       .idleConnectionGauge = Gauge.builder("hikaricp.connections.idle", poolStats, PoolStats::getIdleConnections).description("Idle connections").tags(new String[] { "pool", poolName }).register(meterRegistry);
/*     */     
/* 110 */     this
/*     */ 
/*     */       
/* 113 */       .activeConnectionGauge = Gauge.builder("hikaricp.connections.active", poolStats, PoolStats::getActiveConnections).description("Active connections").tags(new String[] { "pool", poolName }).register(meterRegistry);
/*     */     
/* 115 */     this
/*     */ 
/*     */       
/* 118 */       .pendingConnectionGauge = Gauge.builder("hikaricp.connections.pending", poolStats, PoolStats::getPendingThreads).description("Pending threads").tags(new String[] { "pool", poolName }).register(meterRegistry);
/*     */     
/* 120 */     this
/*     */ 
/*     */       
/* 123 */       .maxConnectionGauge = Gauge.builder("hikaricp.connections.max", poolStats, PoolStats::getMaxConnections).description("Max connections").tags(new String[] { "pool", poolName }).register(meterRegistry);
/*     */     
/* 125 */     this
/*     */ 
/*     */       
/* 128 */       .minConnectionGauge = Gauge.builder("hikaricp.connections.min", poolStats, PoolStats::getMinConnections).description("Min connections").tags(new String[] { "pool", poolName }).register(meterRegistry);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void recordConnectionAcquiredNanos(long elapsedAcquiredNanos) {
/* 136 */     this.connectionObtainTimer.record(elapsedAcquiredNanos, TimeUnit.NANOSECONDS);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void recordConnectionUsageMillis(long elapsedBorrowedMillis) {
/* 143 */     this.connectionUsage.record(elapsedBorrowedMillis, TimeUnit.MILLISECONDS);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void recordConnectionTimeout() {
/* 149 */     this.connectionTimeoutCounter.increment();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void recordConnectionCreatedMillis(long connectionCreatedMillis) {
/* 155 */     this.connectionCreation.record(connectionCreatedMillis, TimeUnit.MILLISECONDS);
/*     */   }
/*     */ 
/*     */   
/*     */   public void close() {
/* 160 */     this.meterRegistry.remove((Meter)this.connectionObtainTimer);
/* 161 */     this.meterRegistry.remove((Meter)this.connectionTimeoutCounter);
/* 162 */     this.meterRegistry.remove((Meter)this.connectionUsage);
/* 163 */     this.meterRegistry.remove((Meter)this.connectionCreation);
/* 164 */     this.meterRegistry.remove((Meter)this.totalConnectionGauge);
/* 165 */     this.meterRegistry.remove((Meter)this.idleConnectionGauge);
/* 166 */     this.meterRegistry.remove((Meter)this.activeConnectionGauge);
/* 167 */     this.meterRegistry.remove((Meter)this.pendingConnectionGauge);
/* 168 */     this.meterRegistry.remove((Meter)this.maxConnectionGauge);
/* 169 */     this.meterRegistry.remove((Meter)this.minConnectionGauge);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\zaxxer\hikari\metrics\micrometer\MicrometerMetricsTracker.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */