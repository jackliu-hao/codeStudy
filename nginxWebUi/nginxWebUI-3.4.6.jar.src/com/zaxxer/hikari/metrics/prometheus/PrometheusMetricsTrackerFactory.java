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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class PrometheusMetricsTrackerFactory
/*    */   implements MetricsTrackerFactory
/*    */ {
/* 46 */   private static final Map<CollectorRegistry, RegistrationStatus> registrationStatuses = new ConcurrentHashMap<>();
/*    */   
/* 48 */   private final HikariCPCollector collector = new HikariCPCollector();
/*    */   
/*    */   private final CollectorRegistry collectorRegistry;
/*    */   
/*    */   enum RegistrationStatus
/*    */   {
/* 54 */     REGISTERED;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public PrometheusMetricsTrackerFactory() {
/* 63 */     this(CollectorRegistry.defaultRegistry);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public PrometheusMetricsTrackerFactory(CollectorRegistry collectorRegistry) {
/* 72 */     this.collectorRegistry = collectorRegistry;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public IMetricsTracker create(String poolName, PoolStats poolStats) {
/* 78 */     registerCollector(this.collector, this.collectorRegistry);
/* 79 */     this.collector.add(poolName, poolStats);
/* 80 */     return new PrometheusMetricsTracker(poolName, this.collectorRegistry, this.collector);
/*    */   }
/*    */ 
/*    */   
/*    */   private void registerCollector(Collector collector, CollectorRegistry collectorRegistry) {
/* 85 */     if (registrationStatuses.putIfAbsent(collectorRegistry, RegistrationStatus.REGISTERED) == null)
/* 86 */       collector.register(collectorRegistry); 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\zaxxer\hikari\metrics\prometheus\PrometheusMetricsTrackerFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */