/*     */ package com.zaxxer.hikari;
/*     */ 
/*     */ import com.zaxxer.hikari.metrics.MetricsTrackerFactory;
/*     */ import com.zaxxer.hikari.pool.HikariPool;
/*     */ import java.io.Closeable;
/*     */ import java.io.PrintWriter;
/*     */ import java.sql.Connection;
/*     */ import java.sql.SQLException;
/*     */ import java.sql.SQLFeatureNotSupportedException;
/*     */ import java.util.concurrent.atomic.AtomicBoolean;
/*     */ import java.util.logging.Logger;
/*     */ import javax.sql.DataSource;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
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
/*     */ public class HikariDataSource
/*     */   extends HikariConfig
/*     */   implements DataSource, Closeable
/*     */ {
/*  42 */   private static final Logger LOGGER = LoggerFactory.getLogger(HikariDataSource.class);
/*     */   
/*  44 */   private final AtomicBoolean isShutdown = new AtomicBoolean();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final HikariPool fastPathPool;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private volatile HikariPool pool;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public HikariDataSource() {
/*  62 */     this.fastPathPool = null;
/*     */   }
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
/*     */   public HikariDataSource(HikariConfig configuration) {
/*  77 */     configuration.validate();
/*  78 */     configuration.copyStateTo(this);
/*     */     
/*  80 */     LOGGER.info("{} - Starting...", configuration.getPoolName());
/*  81 */     this.pool = this.fastPathPool = new HikariPool(this);
/*  82 */     LOGGER.info("{} - Start completed.", configuration.getPoolName());
/*     */     
/*  84 */     seal();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Connection getConnection() throws SQLException {
/*  95 */     if (isClosed()) {
/*  96 */       throw new SQLException("HikariDataSource " + this + " has been closed.");
/*     */     }
/*     */     
/*  99 */     if (this.fastPathPool != null) {
/* 100 */       return this.fastPathPool.getConnection();
/*     */     }
/*     */ 
/*     */     
/* 104 */     HikariPool result = this.pool;
/* 105 */     if (result == null) {
/* 106 */       synchronized (this) {
/* 107 */         result = this.pool;
/* 108 */         if (result == null) {
/* 109 */           validate();
/* 110 */           LOGGER.info("{} - Starting...", getPoolName());
/*     */           try {
/* 112 */             this.pool = result = new HikariPool(this);
/* 113 */             seal();
/*     */           }
/* 115 */           catch (com.zaxxer.hikari.pool.HikariPool.PoolInitializationException pie) {
/* 116 */             if (pie.getCause() instanceof SQLException) {
/* 117 */               throw (SQLException)pie.getCause();
/*     */             }
/*     */             
/* 120 */             throw pie;
/*     */           } 
/*     */           
/* 123 */           LOGGER.info("{} - Start completed.", getPoolName());
/*     */         } 
/*     */       } 
/*     */     }
/*     */     
/* 128 */     return result.getConnection();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Connection getConnection(String username, String password) throws SQLException {
/* 135 */     throw new SQLFeatureNotSupportedException();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public PrintWriter getLogWriter() throws SQLException {
/* 142 */     HikariPool p = this.pool;
/* 143 */     return (p != null) ? p.getUnwrappedDataSource().getLogWriter() : null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setLogWriter(PrintWriter out) throws SQLException {
/* 150 */     HikariPool p = this.pool;
/* 151 */     if (p != null) {
/* 152 */       p.getUnwrappedDataSource().setLogWriter(out);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setLoginTimeout(int seconds) throws SQLException {
/* 160 */     HikariPool p = this.pool;
/* 161 */     if (p != null) {
/* 162 */       p.getUnwrappedDataSource().setLoginTimeout(seconds);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getLoginTimeout() throws SQLException {
/* 170 */     HikariPool p = this.pool;
/* 171 */     return (p != null) ? p.getUnwrappedDataSource().getLoginTimeout() : 0;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Logger getParentLogger() throws SQLFeatureNotSupportedException {
/* 178 */     throw new SQLFeatureNotSupportedException();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public <T> T unwrap(Class<T> iface) throws SQLException {
/* 186 */     if (iface.isInstance(this)) {
/* 187 */       return (T)this;
/*     */     }
/*     */     
/* 190 */     HikariPool p = this.pool;
/* 191 */     if (p != null) {
/* 192 */       DataSource unwrappedDataSource = p.getUnwrappedDataSource();
/* 193 */       if (iface.isInstance(unwrappedDataSource)) {
/* 194 */         return (T)unwrappedDataSource;
/*     */       }
/*     */       
/* 197 */       if (unwrappedDataSource != null) {
/* 198 */         return unwrappedDataSource.unwrap(iface);
/*     */       }
/*     */     } 
/*     */     
/* 202 */     throw new SQLException("Wrapped DataSource is not an instance of " + iface);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isWrapperFor(Class<?> iface) throws SQLException {
/* 209 */     if (iface.isInstance(this)) {
/* 210 */       return true;
/*     */     }
/*     */     
/* 213 */     HikariPool p = this.pool;
/* 214 */     if (p != null) {
/* 215 */       DataSource unwrappedDataSource = p.getUnwrappedDataSource();
/* 216 */       if (iface.isInstance(unwrappedDataSource)) {
/* 217 */         return true;
/*     */       }
/*     */       
/* 220 */       if (unwrappedDataSource != null) {
/* 221 */         return unwrappedDataSource.isWrapperFor(iface);
/*     */       }
/*     */     } 
/*     */     
/* 225 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setMetricRegistry(Object metricRegistry) {
/* 236 */     boolean isAlreadySet = (getMetricRegistry() != null);
/* 237 */     super.setMetricRegistry(metricRegistry);
/*     */     
/* 239 */     HikariPool p = this.pool;
/* 240 */     if (p != null) {
/* 241 */       if (isAlreadySet) {
/* 242 */         throw new IllegalStateException("MetricRegistry can only be set one time");
/*     */       }
/*     */       
/* 245 */       p.setMetricRegistry(getMetricRegistry());
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setMetricsTrackerFactory(MetricsTrackerFactory metricsTrackerFactory) {
/* 254 */     boolean isAlreadySet = (getMetricsTrackerFactory() != null);
/* 255 */     super.setMetricsTrackerFactory(metricsTrackerFactory);
/*     */     
/* 257 */     HikariPool p = this.pool;
/* 258 */     if (p != null) {
/* 259 */       if (isAlreadySet) {
/* 260 */         throw new IllegalStateException("MetricsTrackerFactory can only be set one time");
/*     */       }
/*     */       
/* 263 */       p.setMetricsTrackerFactory(getMetricsTrackerFactory());
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setHealthCheckRegistry(Object healthCheckRegistry) {
/* 272 */     boolean isAlreadySet = (getHealthCheckRegistry() != null);
/* 273 */     super.setHealthCheckRegistry(healthCheckRegistry);
/*     */     
/* 275 */     HikariPool p = this.pool;
/* 276 */     if (p != null) {
/* 277 */       if (isAlreadySet) {
/* 278 */         throw new IllegalStateException("HealthCheckRegistry can only be set one time");
/*     */       }
/*     */       
/* 281 */       p.setHealthCheckRegistry(getHealthCheckRegistry());
/*     */     } 
/*     */   }
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
/*     */   public boolean isRunning() {
/* 297 */     return (this.pool != null && this.pool.poolState == 0);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public HikariPoolMXBean getHikariPoolMXBean() {
/* 309 */     return (HikariPoolMXBean)this.pool;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public HikariConfigMXBean getHikariConfigMXBean() {
/* 319 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void evictConnection(Connection connection) {
/*     */     HikariPool p;
/* 332 */     if (!isClosed() && (p = this.pool) != null && connection.getClass().getName().startsWith("com.zaxxer.hikari")) {
/* 333 */       p.evictConnection(connection);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void close() {
/* 343 */     if (this.isShutdown.getAndSet(true)) {
/*     */       return;
/*     */     }
/*     */     
/* 347 */     HikariPool p = this.pool;
/* 348 */     if (p != null) {
/*     */       try {
/* 350 */         LOGGER.info("{} - Shutdown initiated...", getPoolName());
/* 351 */         p.shutdown();
/* 352 */         LOGGER.info("{} - Shutdown completed.", getPoolName());
/*     */       }
/* 354 */       catch (InterruptedException e) {
/* 355 */         LOGGER.warn("{} - Interrupted during closing", getPoolName(), e);
/* 356 */         Thread.currentThread().interrupt();
/*     */       } 
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isClosed() {
/* 368 */     return this.isShutdown.get();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/* 375 */     return "HikariDataSource (" + this.pool + ")";
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\zaxxer\hikari\HikariDataSource.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */