/*     */ package com.zaxxer.hikari.metrics.dropwizard;
/*     */ 
/*     */ import com.codahale.metrics.Metric;
/*     */ import com.codahale.metrics.MetricRegistry;
/*     */ import com.codahale.metrics.Timer;
/*     */ import com.codahale.metrics.health.HealthCheck;
/*     */ import com.codahale.metrics.health.HealthCheckRegistry;
/*     */ import com.zaxxer.hikari.HikariConfig;
/*     */ import com.zaxxer.hikari.pool.HikariPool;
/*     */ import java.sql.Connection;
/*     */ import java.sql.SQLException;
/*     */ import java.util.Map;
/*     */ import java.util.Properties;
/*     */ import java.util.SortedMap;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class CodahaleHealthChecker
/*     */ {
/*     */   public static void registerHealthChecks(HikariPool pool, HikariConfig hikariConfig, HealthCheckRegistry registry) {
/*  58 */     Properties healthCheckProperties = hikariConfig.getHealthCheckProperties();
/*  59 */     MetricRegistry metricRegistry = (MetricRegistry)hikariConfig.getMetricRegistry();
/*     */     
/*  61 */     long checkTimeoutMs = Long.parseLong(healthCheckProperties.getProperty("connectivityCheckTimeoutMs", String.valueOf(hikariConfig.getConnectionTimeout())));
/*  62 */     registry.register(MetricRegistry.name(hikariConfig.getPoolName(), new String[] { "pool", "ConnectivityCheck" }), new ConnectivityHealthCheck(pool, checkTimeoutMs));
/*     */     
/*  64 */     long expected99thPercentile = Long.parseLong(healthCheckProperties.getProperty("expected99thPercentileMs", "0"));
/*  65 */     if (metricRegistry != null && expected99thPercentile > 0L) {
/*  66 */       SortedMap<String, Timer> timers = metricRegistry.getTimers((name, metric) -> name.equals(MetricRegistry.name(hikariConfig.getPoolName(), new String[] { "pool", "Wait" })));
/*     */       
/*  68 */       if (!timers.isEmpty()) {
/*  69 */         Timer timer = (Timer)((Map.Entry)timers.entrySet().iterator().next()).getValue();
/*  70 */         registry.register(MetricRegistry.name(hikariConfig.getPoolName(), new String[] { "pool", "Connection99Percent" }), new Connection99Percent(timer, expected99thPercentile));
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static class ConnectivityHealthCheck
/*     */     extends HealthCheck
/*     */   {
/*     */     private final HikariPool pool;
/*     */ 
/*     */     
/*     */     private final long checkTimeoutMs;
/*     */ 
/*     */     
/*     */     ConnectivityHealthCheck(HikariPool pool, long checkTimeoutMs) {
/*  87 */       this.pool = pool;
/*  88 */       this.checkTimeoutMs = (checkTimeoutMs > 0L && checkTimeoutMs != 2147483647L) ? checkTimeoutMs : TimeUnit.SECONDS.toMillis(10L);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     protected HealthCheck.Result check() throws Exception {
/*     */       
/*  95 */       try { Connection connection = this.pool.getConnection(this.checkTimeoutMs); 
/*  96 */         try { HealthCheck.Result result = HealthCheck.Result.healthy();
/*  97 */           if (connection != null) connection.close();  return result; } catch (Throwable throwable) { if (connection != null)
/*  98 */             try { connection.close(); } catch (Throwable throwable1) { throwable.addSuppressed(throwable1); }   throw throwable; }  } catch (SQLException e)
/*  99 */       { return HealthCheck.Result.unhealthy(e); }
/*     */     
/*     */     }
/*     */   }
/*     */   
/*     */   private static class Connection99Percent
/*     */     extends HealthCheck
/*     */   {
/*     */     private final Timer waitTimer;
/*     */     private final long expected99thPercentile;
/*     */     
/*     */     Connection99Percent(Timer waitTimer, long expected99thPercentile) {
/* 111 */       this.waitTimer = waitTimer;
/* 112 */       this.expected99thPercentile = expected99thPercentile;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     protected HealthCheck.Result check() throws Exception {
/* 119 */       long the99thPercentile = TimeUnit.NANOSECONDS.toMillis(Math.round(this.waitTimer.getSnapshot().get99thPercentile()));
/* 120 */       return (the99thPercentile <= this.expected99thPercentile) ? HealthCheck.Result.healthy() : HealthCheck.Result.unhealthy("99th percentile connection wait time of %dms exceeds the threshold %dms", new Object[] { Long.valueOf(the99thPercentile), Long.valueOf(this.expected99thPercentile) });
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\zaxxer\hikari\metrics\dropwizard\CodahaleHealthChecker.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */