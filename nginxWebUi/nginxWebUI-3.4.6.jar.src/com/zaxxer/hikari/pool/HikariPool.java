/*     */ package com.zaxxer.hikari.pool;
/*     */ 
/*     */ import com.codahale.metrics.MetricRegistry;
/*     */ import com.codahale.metrics.health.HealthCheckRegistry;
/*     */ import com.zaxxer.hikari.HikariConfig;
/*     */ import com.zaxxer.hikari.HikariPoolMXBean;
/*     */ import com.zaxxer.hikari.metrics.MetricsTrackerFactory;
/*     */ import com.zaxxer.hikari.metrics.PoolStats;
/*     */ import com.zaxxer.hikari.metrics.dropwizard.CodahaleHealthChecker;
/*     */ import com.zaxxer.hikari.metrics.dropwizard.CodahaleMetricsTrackerFactory;
/*     */ import com.zaxxer.hikari.metrics.micrometer.MicrometerMetricsTrackerFactory;
/*     */ import com.zaxxer.hikari.util.ClockSource;
/*     */ import com.zaxxer.hikari.util.ConcurrentBag;
/*     */ import com.zaxxer.hikari.util.SuspendResumeLock;
/*     */ import com.zaxxer.hikari.util.UtilityElf;
/*     */ import io.micrometer.core.instrument.MeterRegistry;
/*     */ import java.sql.Connection;
/*     */ import java.sql.SQLException;
/*     */ import java.sql.SQLTransientConnectionException;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ import java.util.Optional;
/*     */ import java.util.concurrent.Callable;
/*     */ import java.util.concurrent.ExecutorService;
/*     */ import java.util.concurrent.LinkedBlockingQueue;
/*     */ import java.util.concurrent.ScheduledExecutorService;
/*     */ import java.util.concurrent.ScheduledFuture;
/*     */ import java.util.concurrent.ScheduledThreadPoolExecutor;
/*     */ import java.util.concurrent.ThreadFactory;
/*     */ import java.util.concurrent.ThreadLocalRandom;
/*     */ import java.util.concurrent.ThreadPoolExecutor;
/*     */ import java.util.concurrent.TimeUnit;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class HikariPool
/*     */   extends PoolBase
/*     */   implements HikariPoolMXBean, ConcurrentBag.IBagStateListener
/*     */ {
/*  73 */   private final Logger logger = LoggerFactory.getLogger(HikariPool.class);
/*     */   
/*     */   public static final int POOL_NORMAL = 0;
/*     */   
/*     */   public static final int POOL_SUSPENDED = 1;
/*     */   
/*     */   public static final int POOL_SHUTDOWN = 2;
/*     */   public volatile int poolState;
/*  81 */   private final long aliveBypassWindowMs = Long.getLong("com.zaxxer.hikari.aliveBypassWindowMs", TimeUnit.MILLISECONDS.toMillis(500L)).longValue();
/*  82 */   private final long housekeepingPeriodMs = Long.getLong("com.zaxxer.hikari.housekeeping.periodMs", TimeUnit.SECONDS.toMillis(30L)).longValue();
/*     */   
/*     */   private static final String EVICTED_CONNECTION_MESSAGE = "(connection was evicted)";
/*     */   
/*     */   private static final String DEAD_CONNECTION_MESSAGE = "(connection is dead)";
/*  87 */   private final PoolEntryCreator poolEntryCreator = new PoolEntryCreator(null);
/*  88 */   private final PoolEntryCreator postFillPoolEntryCreator = new PoolEntryCreator("After adding ");
/*     */   
/*     */   private final Collection<Runnable> addConnectionQueueReadOnlyView;
/*     */   
/*     */   private final ThreadPoolExecutor addConnectionExecutor;
/*     */   
/*     */   private final ThreadPoolExecutor closeConnectionExecutor;
/*     */   
/*     */   private final ConcurrentBag<PoolEntry> connectionBag;
/*     */   
/*     */   private final ProxyLeakTaskFactory leakTaskFactory;
/*     */   
/*     */   private final SuspendResumeLock suspendResumeLock;
/*     */   
/*     */   private final ScheduledExecutorService houseKeepingExecutorService;
/*     */   
/*     */   private ScheduledFuture<?> houseKeeperTask;
/*     */ 
/*     */   
/*     */   public HikariPool(HikariConfig config) {
/* 108 */     super(config);
/*     */     
/* 110 */     this.connectionBag = new ConcurrentBag(this);
/* 111 */     this.suspendResumeLock = config.isAllowPoolSuspension() ? new SuspendResumeLock() : SuspendResumeLock.FAUX_LOCK;
/*     */     
/* 113 */     this.houseKeepingExecutorService = initializeHouseKeepingExecutorService();
/*     */     
/* 115 */     checkFailFast();
/*     */     
/* 117 */     if (config.getMetricsTrackerFactory() != null) {
/* 118 */       setMetricsTrackerFactory(config.getMetricsTrackerFactory());
/*     */     } else {
/*     */       
/* 121 */       setMetricRegistry(config.getMetricRegistry());
/*     */     } 
/*     */     
/* 124 */     setHealthCheckRegistry(config.getHealthCheckRegistry());
/*     */     
/* 126 */     handleMBeans(this, true);
/*     */     
/* 128 */     ThreadFactory threadFactory = config.getThreadFactory();
/*     */     
/* 130 */     int maxPoolSize = config.getMaximumPoolSize();
/* 131 */     LinkedBlockingQueue<Runnable> addConnectionQueue = new LinkedBlockingQueue<>(maxPoolSize);
/* 132 */     this.addConnectionQueueReadOnlyView = Collections.unmodifiableCollection(addConnectionQueue);
/* 133 */     this.addConnectionExecutor = UtilityElf.createThreadPoolExecutor(addConnectionQueue, this.poolName + " connection adder", threadFactory, new ThreadPoolExecutor.DiscardOldestPolicy());
/* 134 */     this.closeConnectionExecutor = UtilityElf.createThreadPoolExecutor(maxPoolSize, this.poolName + " connection closer", threadFactory, new ThreadPoolExecutor.CallerRunsPolicy());
/*     */     
/* 136 */     this.leakTaskFactory = new ProxyLeakTaskFactory(config.getLeakDetectionThreshold(), this.houseKeepingExecutorService);
/*     */     
/* 138 */     this.houseKeeperTask = this.houseKeepingExecutorService.scheduleWithFixedDelay(new HouseKeeper(), 100L, this.housekeepingPeriodMs, TimeUnit.MILLISECONDS);
/*     */     
/* 140 */     if (Boolean.getBoolean("com.zaxxer.hikari.blockUntilFilled") && config.getInitializationFailTimeout() > 1L) {
/* 141 */       this.addConnectionExecutor.setMaximumPoolSize(Math.min(16, Runtime.getRuntime().availableProcessors()));
/* 142 */       this.addConnectionExecutor.setCorePoolSize(Math.min(16, Runtime.getRuntime().availableProcessors()));
/*     */       
/* 144 */       long startTime = ClockSource.currentTime();
/* 145 */       while (ClockSource.elapsedMillis(startTime) < config.getInitializationFailTimeout() && getTotalConnections() < config.getMinimumIdle()) {
/* 146 */         UtilityElf.quietlySleep(TimeUnit.MILLISECONDS.toMillis(100L));
/*     */       }
/*     */       
/* 149 */       this.addConnectionExecutor.setCorePoolSize(1);
/* 150 */       this.addConnectionExecutor.setMaximumPoolSize(1);
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
/*     */   public Connection getConnection() throws SQLException {
/* 162 */     return getConnection(this.connectionTimeout);
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
/*     */   public Connection getConnection(long hardTimeout) throws SQLException {
/* 174 */     this.suspendResumeLock.acquire();
/* 175 */     long startTime = ClockSource.currentTime();
/*     */     
/*     */     try {
/* 178 */       long timeout = hardTimeout;
/*     */       do {
/* 180 */         PoolEntry poolEntry = (PoolEntry)this.connectionBag.borrow(timeout, TimeUnit.MILLISECONDS);
/* 181 */         if (poolEntry == null) {
/*     */           break;
/*     */         }
/*     */         
/* 185 */         long now = ClockSource.currentTime();
/* 186 */         if (poolEntry.isMarkedEvicted() || (ClockSource.elapsedMillis(poolEntry.lastAccessed, now) > this.aliveBypassWindowMs && !isConnectionAlive(poolEntry.connection))) {
/* 187 */           closeConnection(poolEntry, poolEntry.isMarkedEvicted() ? "(connection was evicted)" : "(connection is dead)");
/* 188 */           timeout = hardTimeout - ClockSource.elapsedMillis(startTime);
/*     */         } else {
/*     */           
/* 191 */           this.metricsTracker.recordBorrowStats(poolEntry, startTime);
/* 192 */           return poolEntry.createProxyConnection(this.leakTaskFactory.schedule(poolEntry), now);
/*     */         } 
/* 194 */       } while (timeout > 0L);
/*     */       
/* 196 */       this.metricsTracker.recordBorrowTimeoutStats(startTime);
/* 197 */       throw createTimeoutException(startTime);
/*     */     }
/* 199 */     catch (InterruptedException e) {
/* 200 */       Thread.currentThread().interrupt();
/* 201 */       throw new SQLException(this.poolName + " - Interrupted during connection acquisition", e);
/*     */     } finally {
/*     */       
/* 204 */       this.suspendResumeLock.release();
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
/*     */   public synchronized void shutdown() throws InterruptedException {
/*     */     try {
/* 217 */       this.poolState = 2;
/*     */       
/* 219 */       if (this.addConnectionExecutor == null) {
/*     */         return;
/*     */       }
/*     */       
/* 223 */       logPoolState(new String[] { "Before shutdown " });
/*     */       
/* 225 */       if (this.houseKeeperTask != null) {
/* 226 */         this.houseKeeperTask.cancel(false);
/* 227 */         this.houseKeeperTask = null;
/*     */       } 
/*     */       
/* 230 */       softEvictConnections();
/*     */       
/* 232 */       this.addConnectionExecutor.shutdown();
/* 233 */       this.addConnectionExecutor.awaitTermination(getLoginTimeout(), TimeUnit.SECONDS);
/*     */       
/* 235 */       destroyHouseKeepingExecutorService();
/*     */       
/* 237 */       this.connectionBag.close();
/*     */       
/* 239 */       ExecutorService assassinExecutor = UtilityElf.createThreadPoolExecutor(this.config.getMaximumPoolSize(), this.poolName + " connection assassinator", this.config
/* 240 */           .getThreadFactory(), new ThreadPoolExecutor.CallerRunsPolicy());
/*     */       try {
/* 242 */         long start = ClockSource.currentTime();
/*     */         do {
/* 244 */           abortActiveConnections(assassinExecutor);
/* 245 */           softEvictConnections();
/* 246 */         } while (getTotalConnections() > 0 && ClockSource.elapsedMillis(start) < TimeUnit.SECONDS.toMillis(10L));
/*     */       } finally {
/*     */         
/* 249 */         assassinExecutor.shutdown();
/* 250 */         assassinExecutor.awaitTermination(10L, TimeUnit.SECONDS);
/*     */       } 
/*     */       
/* 253 */       shutdownNetworkTimeoutExecutor();
/* 254 */       this.closeConnectionExecutor.shutdown();
/* 255 */       this.closeConnectionExecutor.awaitTermination(10L, TimeUnit.SECONDS);
/*     */     } finally {
/*     */       
/* 258 */       logPoolState(new String[] { "After shutdown " });
/* 259 */       handleMBeans(this, false);
/* 260 */       this.metricsTracker.close();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void evictConnection(Connection connection) {
/* 271 */     ProxyConnection proxyConnection = (ProxyConnection)connection;
/* 272 */     proxyConnection.cancelLeakTask();
/*     */     
/*     */     try {
/* 275 */       softEvictConnection(proxyConnection.getPoolEntry(), "(connection evicted by user)", !connection.isClosed());
/*     */     }
/* 277 */     catch (SQLException sQLException) {}
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
/*     */   public void setMetricRegistry(Object metricRegistry) {
/* 290 */     if (metricRegistry != null && UtilityElf.safeIsAssignableFrom(metricRegistry, "com.codahale.metrics.MetricRegistry")) {
/* 291 */       setMetricsTrackerFactory((MetricsTrackerFactory)new CodahaleMetricsTrackerFactory((MetricRegistry)metricRegistry));
/*     */     }
/* 293 */     else if (metricRegistry != null && UtilityElf.safeIsAssignableFrom(metricRegistry, "io.micrometer.core.instrument.MeterRegistry")) {
/* 294 */       setMetricsTrackerFactory((MetricsTrackerFactory)new MicrometerMetricsTrackerFactory((MeterRegistry)metricRegistry));
/*     */     } else {
/*     */       
/* 297 */       setMetricsTrackerFactory((MetricsTrackerFactory)null);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setMetricsTrackerFactory(MetricsTrackerFactory metricsTrackerFactory) {
/* 308 */     if (metricsTrackerFactory != null) {
/* 309 */       this.metricsTracker = new PoolBase.MetricsTrackerDelegate(metricsTrackerFactory.create(this.config.getPoolName(), getPoolStats()));
/*     */     } else {
/*     */       
/* 312 */       this.metricsTracker = new PoolBase.NopMetricsTrackerDelegate();
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
/*     */   public void setHealthCheckRegistry(Object healthCheckRegistry) {
/* 324 */     if (healthCheckRegistry != null) {
/* 325 */       CodahaleHealthChecker.registerHealthChecks(this, this.config, (HealthCheckRegistry)healthCheckRegistry);
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
/*     */   public void addBagItem(int waiting) {
/* 337 */     boolean shouldAdd = (waiting - this.addConnectionQueueReadOnlyView.size() >= 0);
/* 338 */     if (shouldAdd) {
/* 339 */       this.addConnectionExecutor.submit(this.poolEntryCreator);
/*     */     } else {
/*     */       
/* 342 */       this.logger.debug("{} - Add connection elided, waiting {}, queue {}", new Object[] { this.poolName, Integer.valueOf(waiting), Integer.valueOf(this.addConnectionQueueReadOnlyView.size()) });
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
/*     */   public int getActiveConnections() {
/* 354 */     return this.connectionBag.getCount(1);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getIdleConnections() {
/* 361 */     return this.connectionBag.getCount(0);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getTotalConnections() {
/* 368 */     return this.connectionBag.size();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getThreadsAwaitingConnection() {
/* 375 */     return this.connectionBag.getWaitingThreadCount();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void softEvictConnections() {
/* 382 */     this.connectionBag.values().forEach(poolEntry -> softEvictConnection(poolEntry, "(connection evicted)", false));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized void suspendPool() {
/* 389 */     if (this.suspendResumeLock == SuspendResumeLock.FAUX_LOCK) {
/* 390 */       throw new IllegalStateException(this.poolName + " - is not suspendable");
/*     */     }
/* 392 */     if (this.poolState != 1) {
/* 393 */       this.suspendResumeLock.suspend();
/* 394 */       this.poolState = 1;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized void resumePool() {
/* 402 */     if (this.poolState == 1) {
/* 403 */       this.poolState = 0;
/* 404 */       fillPool();
/* 405 */       this.suspendResumeLock.resume();
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
/*     */   void logPoolState(String... prefix) {
/* 420 */     if (this.logger.isDebugEnabled()) {
/* 421 */       this.logger.debug("{} - {}stats (total={}, active={}, idle={}, waiting={})", new Object[] { this.poolName, 
/* 422 */             (prefix.length > 0) ? prefix[0] : "", 
/* 423 */             Integer.valueOf(getTotalConnections()), Integer.valueOf(getActiveConnections()), Integer.valueOf(getIdleConnections()), Integer.valueOf(getThreadsAwaitingConnection()) });
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
/*     */   void recycle(PoolEntry poolEntry) {
/* 435 */     this.metricsTracker.recordConnectionUsage(poolEntry);
/*     */     
/* 437 */     this.connectionBag.requite(poolEntry);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   void closeConnection(PoolEntry poolEntry, String closureReason) {
/* 448 */     if (this.connectionBag.remove(poolEntry)) {
/* 449 */       Connection connection = poolEntry.close();
/* 450 */       this.closeConnectionExecutor.execute(() -> {
/*     */             quietlyCloseConnection(connection, closureReason);
/*     */             if (this.poolState == 0) {
/*     */               fillPool();
/*     */             }
/*     */           });
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   int[] getPoolStateCounts() {
/* 461 */     return this.connectionBag.getStateCounts();
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
/*     */   private PoolEntry createPoolEntry() {
/*     */     try {
/* 476 */       PoolEntry poolEntry = newPoolEntry();
/*     */       
/* 478 */       long maxLifetime = this.config.getMaxLifetime();
/* 479 */       if (maxLifetime > 0L) {
/*     */         
/* 481 */         long variance = (maxLifetime > 10000L) ? ThreadLocalRandom.current().nextLong(maxLifetime / 40L) : 0L;
/* 482 */         long lifetime = maxLifetime - variance;
/* 483 */         poolEntry.setFutureEol(this.houseKeepingExecutorService.schedule(new MaxLifetimeTask(poolEntry), lifetime, TimeUnit.MILLISECONDS));
/*     */       } 
/*     */       
/* 486 */       long keepaliveTime = this.config.getKeepaliveTime();
/* 487 */       if (keepaliveTime > 0L) {
/*     */         
/* 489 */         long variance = ThreadLocalRandom.current().nextLong(keepaliveTime / 10L);
/* 490 */         long heartbeatTime = keepaliveTime - variance;
/* 491 */         poolEntry.setKeepalive(this.houseKeepingExecutorService.scheduleWithFixedDelay(new KeepaliveTask(poolEntry), heartbeatTime, heartbeatTime, TimeUnit.MILLISECONDS));
/*     */       } 
/*     */       
/* 494 */       return poolEntry;
/*     */     }
/* 496 */     catch (ConnectionSetupException e) {
/* 497 */       if (this.poolState == 0) {
/* 498 */         this.logger.error("{} - Error thrown while acquiring connection from data source", this.poolName, e.getCause());
/* 499 */         this.lastConnectionFailure.set(e);
/*     */       }
/*     */     
/* 502 */     } catch (Exception e) {
/* 503 */       if (this.poolState == 0) {
/* 504 */         this.logger.debug("{} - Cannot acquire connection from data source", this.poolName, e);
/*     */       }
/*     */     } 
/*     */     
/* 508 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private synchronized void fillPool() {
/* 517 */     int connectionsToAdd = Math.min(this.config.getMaximumPoolSize() - getTotalConnections(), this.config.getMinimumIdle() - getIdleConnections()) - this.addConnectionQueueReadOnlyView.size();
/* 518 */     if (connectionsToAdd <= 0) this.logger.debug("{} - Fill pool skipped, pool is at sufficient level.", this.poolName);
/*     */     
/* 520 */     for (int i = 0; i < connectionsToAdd; i++) {
/* 521 */       this.addConnectionExecutor.submit((i < connectionsToAdd - 1) ? this.poolEntryCreator : this.postFillPoolEntryCreator);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void abortActiveConnections(ExecutorService assassinExecutor) {
/* 532 */     for (PoolEntry poolEntry : this.connectionBag.values(1)) {
/* 533 */       Connection connection = poolEntry.close();
/*     */       try {
/* 535 */         connection.abort(assassinExecutor);
/*     */       }
/* 537 */       catch (Throwable e) {
/* 538 */         quietlyCloseConnection(connection, "(connection aborted during shutdown)");
/*     */       } finally {
/*     */         
/* 541 */         this.connectionBag.remove(poolEntry);
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
/*     */   
/*     */   private void checkFailFast() {
/* 554 */     long initializationTimeout = this.config.getInitializationFailTimeout();
/* 555 */     if (initializationTimeout < 0L) {
/*     */       return;
/*     */     }
/*     */     
/* 559 */     long startTime = ClockSource.currentTime();
/*     */     do {
/* 561 */       PoolEntry poolEntry = createPoolEntry();
/* 562 */       if (poolEntry != null) {
/* 563 */         if (this.config.getMinimumIdle() > 0) {
/* 564 */           this.connectionBag.add(poolEntry);
/* 565 */           this.logger.debug("{} - Added connection {}", this.poolName, poolEntry.connection);
/*     */         } else {
/*     */           
/* 568 */           quietlyCloseConnection(poolEntry.close(), "(initialization check complete and minimumIdle is zero)");
/*     */         } 
/*     */         
/*     */         return;
/*     */       } 
/*     */       
/* 574 */       if (getLastConnectionFailure() instanceof PoolBase.ConnectionSetupException) {
/* 575 */         throwPoolInitializationException(getLastConnectionFailure().getCause());
/*     */       }
/*     */       
/* 578 */       UtilityElf.quietlySleep(TimeUnit.SECONDS.toMillis(1L));
/* 579 */     } while (ClockSource.elapsedMillis(startTime) < initializationTimeout);
/*     */     
/* 581 */     if (initializationTimeout > 0L) {
/* 582 */       throwPoolInitializationException(getLastConnectionFailure());
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
/*     */   private void throwPoolInitializationException(Throwable t) {
/* 594 */     this.logger.error("{} - Exception during pool initialization.", this.poolName, t);
/* 595 */     destroyHouseKeepingExecutorService();
/* 596 */     throw new PoolInitializationException(t);
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
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean softEvictConnection(PoolEntry poolEntry, String reason, boolean owner) {
/* 614 */     poolEntry.markEvicted();
/* 615 */     if (owner || this.connectionBag.reserve(poolEntry)) {
/* 616 */       closeConnection(poolEntry, reason);
/* 617 */       return true;
/*     */     } 
/*     */     
/* 620 */     return false;
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
/*     */   private ScheduledExecutorService initializeHouseKeepingExecutorService() {
/* 632 */     if (this.config.getScheduledExecutor() == null) {
/* 633 */       ThreadFactory threadFactory = Optional.<ThreadFactory>ofNullable(this.config.getThreadFactory()).orElseGet(() -> new UtilityElf.DefaultThreadFactory(this.poolName + " housekeeper", true));
/* 634 */       ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1, threadFactory, new ThreadPoolExecutor.DiscardPolicy());
/* 635 */       executor.setExecuteExistingDelayedTasksAfterShutdownPolicy(false);
/* 636 */       executor.setRemoveOnCancelPolicy(true);
/* 637 */       return executor;
/*     */     } 
/*     */     
/* 640 */     return this.config.getScheduledExecutor();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void destroyHouseKeepingExecutorService() {
/* 649 */     if (this.config.getScheduledExecutor() == null) {
/* 650 */       this.houseKeepingExecutorService.shutdownNow();
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private PoolStats getPoolStats() {
/* 661 */     return new PoolStats(TimeUnit.SECONDS.toMillis(1L))
/*     */       {
/*     */         protected void update() {
/* 664 */           this.pendingThreads = HikariPool.this.getThreadsAwaitingConnection();
/* 665 */           this.idleConnections = HikariPool.this.getIdleConnections();
/* 666 */           this.totalConnections = HikariPool.this.getTotalConnections();
/* 667 */           this.activeConnections = HikariPool.this.getActiveConnections();
/* 668 */           this.maxConnections = HikariPool.this.config.getMaximumPoolSize();
/* 669 */           this.minConnections = HikariPool.this.config.getMinimumIdle();
/*     */         }
/*     */       };
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
/*     */ 
/*     */   
/*     */   private SQLException createTimeoutException(long startTime) {
/* 688 */     logPoolState(new String[] { "Timeout failure " });
/* 689 */     this.metricsTracker.recordConnectionTimeout();
/*     */     
/* 691 */     String sqlState = null;
/* 692 */     Throwable originalException = getLastConnectionFailure();
/* 693 */     if (originalException instanceof SQLException) {
/* 694 */       sqlState = ((SQLException)originalException).getSQLState();
/*     */     }
/* 696 */     SQLException connectionException = new SQLTransientConnectionException(this.poolName + " - Connection is not available, request timed out after " + ClockSource.elapsedMillis(startTime) + "ms.", sqlState, originalException);
/* 697 */     if (originalException instanceof SQLException) {
/* 698 */       connectionException.setNextException((SQLException)originalException);
/*     */     }
/*     */     
/* 701 */     return connectionException;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final class PoolEntryCreator
/*     */     implements Callable<Boolean>
/*     */   {
/*     */     private final String loggingPrefix;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     PoolEntryCreator(String loggingPrefix) {
/* 718 */       this.loggingPrefix = loggingPrefix;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public Boolean call() {
/* 724 */       long sleepBackoff = 250L;
/* 725 */       while (HikariPool.this.poolState == 0 && shouldCreateAnotherConnection()) {
/* 726 */         PoolEntry poolEntry = HikariPool.this.createPoolEntry();
/* 727 */         if (poolEntry != null) {
/* 728 */           HikariPool.this.connectionBag.add(poolEntry);
/* 729 */           HikariPool.this.logger.debug("{} - Added connection {}", HikariPool.this.poolName, poolEntry.connection);
/* 730 */           if (this.loggingPrefix != null) {
/* 731 */             HikariPool.this.logPoolState(new String[] { this.loggingPrefix });
/*     */           }
/* 733 */           return Boolean.TRUE;
/*     */         } 
/*     */ 
/*     */         
/* 737 */         if (this.loggingPrefix != null) HikariPool.this.logger.debug("{} - Connection add failed, sleeping with backoff: {}ms", HikariPool.this.poolName, Long.valueOf(sleepBackoff)); 
/* 738 */         UtilityElf.quietlySleep(sleepBackoff);
/* 739 */         sleepBackoff = Math.min(TimeUnit.SECONDS.toMillis(10L), Math.min(HikariPool.this.connectionTimeout, (long)(sleepBackoff * 1.5D)));
/*     */       } 
/*     */ 
/*     */       
/* 743 */       return Boolean.FALSE;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     private synchronized boolean shouldCreateAnotherConnection() {
/* 753 */       return (HikariPool.this.getTotalConnections() < HikariPool.this.config.getMaximumPoolSize() && (HikariPool.this
/* 754 */         .connectionBag.getWaitingThreadCount() > 0 || HikariPool.this.getIdleConnections() < HikariPool.this.config.getMinimumIdle()));
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private final class HouseKeeper
/*     */     implements Runnable
/*     */   {
/* 763 */     private volatile long previous = ClockSource.plusMillis(ClockSource.currentTime(), -HikariPool.this.housekeepingPeriodMs);
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public void run() {
/*     */       try {
/* 770 */         HikariPool.this.connectionTimeout = HikariPool.this.config.getConnectionTimeout();
/* 771 */         HikariPool.this.validationTimeout = HikariPool.this.config.getValidationTimeout();
/* 772 */         HikariPool.this.leakTaskFactory.updateLeakDetectionThreshold(HikariPool.this.config.getLeakDetectionThreshold());
/* 773 */         HikariPool.this.catalog = (HikariPool.this.config.getCatalog() != null && !HikariPool.this.config.getCatalog().equals(HikariPool.this.catalog)) ? HikariPool.this.config.getCatalog() : HikariPool.this.catalog;
/*     */         
/* 775 */         long idleTimeout = HikariPool.this.config.getIdleTimeout();
/* 776 */         long now = ClockSource.currentTime();
/*     */ 
/*     */         
/* 779 */         if (ClockSource.plusMillis(now, 128L) < ClockSource.plusMillis(this.previous, HikariPool.this.housekeepingPeriodMs)) {
/* 780 */           HikariPool.this.logger.warn("{} - Retrograde clock change detected (housekeeper delta={}), soft-evicting connections from pool.", HikariPool.this.poolName, 
/* 781 */               ClockSource.elapsedDisplayString(this.previous, now));
/* 782 */           this.previous = now;
/* 783 */           HikariPool.this.softEvictConnections();
/*     */           return;
/*     */         } 
/* 786 */         if (now > ClockSource.plusMillis(this.previous, 3L * HikariPool.this.housekeepingPeriodMs / 2L))
/*     */         {
/* 788 */           HikariPool.this.logger.warn("{} - Thread starvation or clock leap detected (housekeeper delta={}).", HikariPool.this.poolName, ClockSource.elapsedDisplayString(this.previous, now));
/*     */         }
/*     */         
/* 791 */         this.previous = now;
/*     */         
/* 793 */         String afterPrefix = "Pool ";
/* 794 */         if (idleTimeout > 0L && HikariPool.this.config.getMinimumIdle() < HikariPool.this.config.getMaximumPoolSize()) {
/* 795 */           HikariPool.this.logPoolState(new String[] { "Before cleanup " });
/* 796 */           afterPrefix = "After cleanup  ";
/*     */           
/* 798 */           List<PoolEntry> notInUse = HikariPool.this.connectionBag.values(0);
/* 799 */           int toRemove = notInUse.size() - HikariPool.this.config.getMinimumIdle();
/* 800 */           for (PoolEntry entry : notInUse) {
/* 801 */             if (toRemove > 0 && ClockSource.elapsedMillis(entry.lastAccessed, now) > idleTimeout && HikariPool.this.connectionBag.reserve(entry)) {
/* 802 */               HikariPool.this.closeConnection(entry, "(connection has passed idleTimeout)");
/* 803 */               toRemove--;
/*     */             } 
/*     */           } 
/*     */         } 
/*     */         
/* 808 */         HikariPool.this.logPoolState(new String[] { afterPrefix });
/*     */         
/* 810 */         HikariPool.this.fillPool();
/*     */       }
/* 812 */       catch (Exception e) {
/* 813 */         HikariPool.this.logger.error("Unexpected exception in housekeeping task", e);
/*     */       } 
/*     */     }
/*     */     
/*     */     private HouseKeeper() {}
/*     */   }
/*     */   
/*     */   private final class MaxLifetimeTask implements Runnable {
/*     */     private final PoolEntry poolEntry;
/*     */     
/*     */     MaxLifetimeTask(PoolEntry poolEntry) {
/* 824 */       this.poolEntry = poolEntry;
/*     */     }
/*     */ 
/*     */     
/*     */     public void run() {
/* 829 */       if (HikariPool.this.softEvictConnection(this.poolEntry, "(connection has passed maxLifetime)", false)) {
/* 830 */         HikariPool.this.addBagItem(HikariPool.this.connectionBag.getWaitingThreadCount());
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   private final class KeepaliveTask
/*     */     implements Runnable
/*     */   {
/*     */     private final PoolEntry poolEntry;
/*     */     
/*     */     KeepaliveTask(PoolEntry poolEntry) {
/* 841 */       this.poolEntry = poolEntry;
/*     */     }
/*     */ 
/*     */     
/*     */     public void run() {
/* 846 */       if (HikariPool.this.connectionBag.reserve(this.poolEntry)) {
/* 847 */         if (!HikariPool.this.isConnectionAlive(this.poolEntry.connection)) {
/* 848 */           HikariPool.this.softEvictConnection(this.poolEntry, "(connection is dead)", true);
/* 849 */           HikariPool.this.addBagItem(HikariPool.this.connectionBag.getWaitingThreadCount());
/*     */         } else {
/*     */           
/* 852 */           HikariPool.this.connectionBag.unreserve(this.poolEntry);
/* 853 */           HikariPool.this.logger.debug("{} - keepalive: connection {} is alive", HikariPool.this.poolName, this.poolEntry.connection);
/*     */         } 
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static class PoolInitializationException
/*     */     extends RuntimeException
/*     */   {
/*     */     private static final long serialVersionUID = 929872118275916520L;
/*     */ 
/*     */ 
/*     */     
/*     */     public PoolInitializationException(Throwable t) {
/* 869 */       super("Failed to initialize pool: " + t.getMessage(), t);
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\zaxxer\hikari\pool\HikariPool.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */