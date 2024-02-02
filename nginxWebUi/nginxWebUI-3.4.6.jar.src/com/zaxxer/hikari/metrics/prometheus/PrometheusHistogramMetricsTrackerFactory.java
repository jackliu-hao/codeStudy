/*    */ package com.zaxxer.hikari.metrics.prometheus;
/*    */ 
/*    */ import com.zaxxer.hikari.metrics.IMetricsTracker;
/*    */ import com.zaxxer.hikari.metrics.MetricsTrackerFactory;
/*    */ import com.zaxxer.hikari.metrics.PoolStats;
/*    */ import io.prometheus.client.Collector;
/*    */ import io.prometheus.client.CollectorRegistry;
/*    */ import java.util.Map;
/*    */ import java.util.concurrent.ConcurrentHashMap;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class PrometheusHistogramMetricsTrackerFactory
/*    */   implements MetricsTrackerFactory
/*    */ {
/* 39 */   private static final Map<CollectorRegistry, PrometheusMetricsTrackerFactory.RegistrationStatus> registrationStatuses = new ConcurrentHashMap<>();
/*    */   
/* 41 */   private final HikariCPCollector collector = new HikariCPCollector();
/*    */ 
/*    */ 
/*    */   
/*    */   private final CollectorRegistry collectorRegistry;
/*    */ 
/*    */ 
/*    */   
/*    */   public PrometheusHistogramMetricsTrackerFactory() {
/* 50 */     this(CollectorRegistry.defaultRegistry);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public PrometheusHistogramMetricsTrackerFactory(CollectorRegistry collectorRegistry) {
/* 58 */     this.collectorRegistry = collectorRegistry;
/*    */   }
/*    */ 
/*    */   
/*    */   public IMetricsTracker create(String poolName, PoolStats poolStats) {
/* 63 */     registerCollector(this.collector, this.collectorRegistry);
/* 64 */     this.collector.add(poolName, poolStats);
/* 65 */     return new PrometheusHistogramMetricsTracker(poolName, this.collectorRegistry, this.collector);
/*    */   }
/*    */   
/*    */   private void registerCollector(Collector collector, CollectorRegistry collectorRegistry) {
/* 69 */     if (registrationStatuses.putIfAbsent(collectorRegistry, PrometheusMetricsTrackerFactory.RegistrationStatus.REGISTERED) == null)
/* 70 */       collector.register(collectorRegistry); 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\zaxxer\hikari\metrics\prometheus\PrometheusHistogramMetricsTrackerFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */