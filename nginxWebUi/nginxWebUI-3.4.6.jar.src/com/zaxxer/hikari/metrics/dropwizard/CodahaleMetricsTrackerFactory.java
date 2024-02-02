/*    */ package com.zaxxer.hikari.metrics.dropwizard;
/*    */ 
/*    */ import com.codahale.metrics.MetricRegistry;
/*    */ import com.zaxxer.hikari.metrics.IMetricsTracker;
/*    */ import com.zaxxer.hikari.metrics.MetricsTrackerFactory;
/*    */ import com.zaxxer.hikari.metrics.PoolStats;
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
/*    */ public final class CodahaleMetricsTrackerFactory
/*    */   implements MetricsTrackerFactory
/*    */ {
/*    */   private final MetricRegistry registry;
/*    */   
/*    */   public CodahaleMetricsTrackerFactory(MetricRegistry registry) {
/* 30 */     this.registry = registry;
/*    */   }
/*    */ 
/*    */   
/*    */   public MetricRegistry getRegistry() {
/* 35 */     return this.registry;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public IMetricsTracker create(String poolName, PoolStats poolStats) {
/* 41 */     return new CodaHaleMetricsTracker(poolName, poolStats, this.registry);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\zaxxer\hikari\metrics\dropwizard\CodahaleMetricsTrackerFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */