/*    */ package com.zaxxer.hikari.metrics.micrometer;
/*    */ 
/*    */ import com.zaxxer.hikari.metrics.IMetricsTracker;
/*    */ import com.zaxxer.hikari.metrics.MetricsTrackerFactory;
/*    */ import com.zaxxer.hikari.metrics.PoolStats;
/*    */ import io.micrometer.core.instrument.MeterRegistry;
/*    */ 
/*    */ 
/*    */ public class MicrometerMetricsTrackerFactory
/*    */   implements MetricsTrackerFactory
/*    */ {
/*    */   private final MeterRegistry registry;
/*    */   
/*    */   public MicrometerMetricsTrackerFactory(MeterRegistry registry) {
/* 15 */     this.registry = registry;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public IMetricsTracker create(String poolName, PoolStats poolStats) {
/* 21 */     return new MicrometerMetricsTracker(poolName, poolStats, this.registry);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\zaxxer\hikari\metrics\micrometer\MicrometerMetricsTrackerFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */