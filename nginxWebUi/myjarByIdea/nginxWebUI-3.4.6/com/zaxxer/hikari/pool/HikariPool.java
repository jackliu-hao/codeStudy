package com.zaxxer.hikari.pool;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.health.HealthCheckRegistry;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariPoolMXBean;
import com.zaxxer.hikari.metrics.MetricsTrackerFactory;
import com.zaxxer.hikari.metrics.PoolStats;
import com.zaxxer.hikari.metrics.dropwizard.CodahaleHealthChecker;
import com.zaxxer.hikari.metrics.dropwizard.CodahaleMetricsTrackerFactory;
import com.zaxxer.hikari.metrics.micrometer.MicrometerMetricsTrackerFactory;
import com.zaxxer.hikari.util.ClockSource;
import com.zaxxer.hikari.util.ConcurrentBag;
import com.zaxxer.hikari.util.SuspendResumeLock;
import com.zaxxer.hikari.util.UtilityElf;
import io.micrometer.core.instrument.MeterRegistry;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLTransientConnectionException;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class HikariPool extends PoolBase implements HikariPoolMXBean, ConcurrentBag.IBagStateListener {
   private final Logger logger = LoggerFactory.getLogger(HikariPool.class);
   public static final int POOL_NORMAL = 0;
   public static final int POOL_SUSPENDED = 1;
   public static final int POOL_SHUTDOWN = 2;
   public volatile int poolState;
   private final long aliveBypassWindowMs;
   private final long housekeepingPeriodMs;
   private static final String EVICTED_CONNECTION_MESSAGE = "(connection was evicted)";
   private static final String DEAD_CONNECTION_MESSAGE = "(connection is dead)";
   private final PoolEntryCreator poolEntryCreator;
   private final PoolEntryCreator postFillPoolEntryCreator;
   private final Collection<Runnable> addConnectionQueueReadOnlyView;
   private final ThreadPoolExecutor addConnectionExecutor;
   private final ThreadPoolExecutor closeConnectionExecutor;
   private final ConcurrentBag<PoolEntry> connectionBag;
   private final ProxyLeakTaskFactory leakTaskFactory;
   private final SuspendResumeLock suspendResumeLock;
   private final ScheduledExecutorService houseKeepingExecutorService;
   private ScheduledFuture<?> houseKeeperTask;

   public HikariPool(HikariConfig config) {
      super(config);
      this.aliveBypassWindowMs = Long.getLong("com.zaxxer.hikari.aliveBypassWindowMs", TimeUnit.MILLISECONDS.toMillis(500L));
      this.housekeepingPeriodMs = Long.getLong("com.zaxxer.hikari.housekeeping.periodMs", TimeUnit.SECONDS.toMillis(30L));
      this.poolEntryCreator = new PoolEntryCreator((String)null);
      this.postFillPoolEntryCreator = new PoolEntryCreator("After adding ");
      this.connectionBag = new ConcurrentBag(this);
      this.suspendResumeLock = config.isAllowPoolSuspension() ? new SuspendResumeLock() : SuspendResumeLock.FAUX_LOCK;
      this.houseKeepingExecutorService = this.initializeHouseKeepingExecutorService();
      this.checkFailFast();
      if (config.getMetricsTrackerFactory() != null) {
         this.setMetricsTrackerFactory(config.getMetricsTrackerFactory());
      } else {
         this.setMetricRegistry(config.getMetricRegistry());
      }

      this.setHealthCheckRegistry(config.getHealthCheckRegistry());
      this.handleMBeans(this, true);
      ThreadFactory threadFactory = config.getThreadFactory();
      int maxPoolSize = config.getMaximumPoolSize();
      LinkedBlockingQueue<Runnable> addConnectionQueue = new LinkedBlockingQueue(maxPoolSize);
      this.addConnectionQueueReadOnlyView = Collections.unmodifiableCollection(addConnectionQueue);
      this.addConnectionExecutor = UtilityElf.createThreadPoolExecutor(addConnectionQueue, this.poolName + " connection adder", threadFactory, new ThreadPoolExecutor.DiscardOldestPolicy());
      this.closeConnectionExecutor = UtilityElf.createThreadPoolExecutor(maxPoolSize, this.poolName + " connection closer", threadFactory, new ThreadPoolExecutor.CallerRunsPolicy());
      this.leakTaskFactory = new ProxyLeakTaskFactory(config.getLeakDetectionThreshold(), this.houseKeepingExecutorService);
      this.houseKeeperTask = this.houseKeepingExecutorService.scheduleWithFixedDelay(new HouseKeeper(), 100L, this.housekeepingPeriodMs, TimeUnit.MILLISECONDS);
      if (Boolean.getBoolean("com.zaxxer.hikari.blockUntilFilled") && config.getInitializationFailTimeout() > 1L) {
         this.addConnectionExecutor.setMaximumPoolSize(Math.min(16, Runtime.getRuntime().availableProcessors()));
         this.addConnectionExecutor.setCorePoolSize(Math.min(16, Runtime.getRuntime().availableProcessors()));
         long startTime = ClockSource.currentTime();

         while(ClockSource.elapsedMillis(startTime) < config.getInitializationFailTimeout() && this.getTotalConnections() < config.getMinimumIdle()) {
            UtilityElf.quietlySleep(TimeUnit.MILLISECONDS.toMillis(100L));
         }

         this.addConnectionExecutor.setCorePoolSize(1);
         this.addConnectionExecutor.setMaximumPoolSize(1);
      }

   }

   public Connection getConnection() throws SQLException {
      return this.getConnection(this.connectionTimeout);
   }

   public Connection getConnection(long hardTimeout) throws SQLException {
      this.suspendResumeLock.acquire();
      long startTime = ClockSource.currentTime();

      try {
         long timeout = hardTimeout;

         while(true) {
            PoolEntry poolEntry = (PoolEntry)this.connectionBag.borrow(timeout, TimeUnit.MILLISECONDS);
            if (poolEntry != null) {
               long now = ClockSource.currentTime();
               if (!poolEntry.isMarkedEvicted() && (ClockSource.elapsedMillis(poolEntry.lastAccessed, now) <= this.aliveBypassWindowMs || this.isConnectionAlive(poolEntry.connection))) {
                  this.metricsTracker.recordBorrowStats(poolEntry, startTime);
                  Connection var10 = poolEntry.createProxyConnection(this.leakTaskFactory.schedule(poolEntry), now);
                  return var10;
               }

               this.closeConnection(poolEntry, poolEntry.isMarkedEvicted() ? "(connection was evicted)" : "(connection is dead)");
               timeout = hardTimeout - ClockSource.elapsedMillis(startTime);
               if (timeout > 0L) {
                  continue;
               }
            }

            this.metricsTracker.recordBorrowTimeoutStats(startTime);
            throw this.createTimeoutException(startTime);
         }
      } catch (InterruptedException var14) {
         Thread.currentThread().interrupt();
         throw new SQLException(this.poolName + " - Interrupted during connection acquisition", var14);
      } finally {
         this.suspendResumeLock.release();
      }
   }

   public synchronized void shutdown() throws InterruptedException {
      try {
         this.poolState = 2;
         if (this.addConnectionExecutor != null) {
            this.logPoolState("Before shutdown ");
            if (this.houseKeeperTask != null) {
               this.houseKeeperTask.cancel(false);
               this.houseKeeperTask = null;
            }

            this.softEvictConnections();
            this.addConnectionExecutor.shutdown();
            this.addConnectionExecutor.awaitTermination(this.getLoginTimeout(), TimeUnit.SECONDS);
            this.destroyHouseKeepingExecutorService();
            this.connectionBag.close();
            ExecutorService assassinExecutor = UtilityElf.createThreadPoolExecutor(this.config.getMaximumPoolSize(), this.poolName + " connection assassinator", this.config.getThreadFactory(), new ThreadPoolExecutor.CallerRunsPolicy());

            try {
               long start = ClockSource.currentTime();

               do {
                  this.abortActiveConnections(assassinExecutor);
                  this.softEvictConnections();
               } while(this.getTotalConnections() > 0 && ClockSource.elapsedMillis(start) < TimeUnit.SECONDS.toMillis(10L));
            } finally {
               assassinExecutor.shutdown();
               assassinExecutor.awaitTermination(10L, TimeUnit.SECONDS);
            }

            this.shutdownNetworkTimeoutExecutor();
            this.closeConnectionExecutor.shutdown();
            this.closeConnectionExecutor.awaitTermination(10L, TimeUnit.SECONDS);
            return;
         }
      } finally {
         this.logPoolState("After shutdown ");
         this.handleMBeans(this, false);
         this.metricsTracker.close();
      }

   }

   public void evictConnection(Connection connection) {
      ProxyConnection proxyConnection = (ProxyConnection)connection;
      proxyConnection.cancelLeakTask();

      try {
         this.softEvictConnection(proxyConnection.getPoolEntry(), "(connection evicted by user)", !connection.isClosed());
      } catch (SQLException var4) {
      }

   }

   public void setMetricRegistry(Object metricRegistry) {
      if (metricRegistry != null && UtilityElf.safeIsAssignableFrom(metricRegistry, "com.codahale.metrics.MetricRegistry")) {
         this.setMetricsTrackerFactory(new CodahaleMetricsTrackerFactory((MetricRegistry)metricRegistry));
      } else if (metricRegistry != null && UtilityElf.safeIsAssignableFrom(metricRegistry, "io.micrometer.core.instrument.MeterRegistry")) {
         this.setMetricsTrackerFactory(new MicrometerMetricsTrackerFactory((MeterRegistry)metricRegistry));
      } else {
         this.setMetricsTrackerFactory((MetricsTrackerFactory)null);
      }

   }

   public void setMetricsTrackerFactory(MetricsTrackerFactory metricsTrackerFactory) {
      if (metricsTrackerFactory != null) {
         this.metricsTracker = new PoolBase.MetricsTrackerDelegate(metricsTrackerFactory.create(this.config.getPoolName(), this.getPoolStats()));
      } else {
         this.metricsTracker = new PoolBase.NopMetricsTrackerDelegate();
      }

   }

   public void setHealthCheckRegistry(Object healthCheckRegistry) {
      if (healthCheckRegistry != null) {
         CodahaleHealthChecker.registerHealthChecks(this, this.config, (HealthCheckRegistry)healthCheckRegistry);
      }

   }

   public void addBagItem(int waiting) {
      boolean shouldAdd = waiting - this.addConnectionQueueReadOnlyView.size() >= 0;
      if (shouldAdd) {
         this.addConnectionExecutor.submit(this.poolEntryCreator);
      } else {
         this.logger.debug("{} - Add connection elided, waiting {}, queue {}", this.poolName, waiting, this.addConnectionQueueReadOnlyView.size());
      }

   }

   public int getActiveConnections() {
      return this.connectionBag.getCount(1);
   }

   public int getIdleConnections() {
      return this.connectionBag.getCount(0);
   }

   public int getTotalConnections() {
      return this.connectionBag.size();
   }

   public int getThreadsAwaitingConnection() {
      return this.connectionBag.getWaitingThreadCount();
   }

   public void softEvictConnections() {
      this.connectionBag.values().forEach((poolEntry) -> {
         this.softEvictConnection(poolEntry, "(connection evicted)", false);
      });
   }

   public synchronized void suspendPool() {
      if (this.suspendResumeLock == SuspendResumeLock.FAUX_LOCK) {
         throw new IllegalStateException(this.poolName + " - is not suspendable");
      } else {
         if (this.poolState != 1) {
            this.suspendResumeLock.suspend();
            this.poolState = 1;
         }

      }
   }

   public synchronized void resumePool() {
      if (this.poolState == 1) {
         this.poolState = 0;
         this.fillPool();
         this.suspendResumeLock.resume();
      }

   }

   void logPoolState(String... prefix) {
      if (this.logger.isDebugEnabled()) {
         this.logger.debug("{} - {}stats (total={}, active={}, idle={}, waiting={})", this.poolName, prefix.length > 0 ? prefix[0] : "", this.getTotalConnections(), this.getActiveConnections(), this.getIdleConnections(), this.getThreadsAwaitingConnection());
      }

   }

   void recycle(PoolEntry poolEntry) {
      this.metricsTracker.recordConnectionUsage(poolEntry);
      this.connectionBag.requite(poolEntry);
   }

   void closeConnection(PoolEntry poolEntry, String closureReason) {
      if (this.connectionBag.remove(poolEntry)) {
         Connection connection = poolEntry.close();
         this.closeConnectionExecutor.execute(() -> {
            this.quietlyCloseConnection(connection, closureReason);
            if (this.poolState == 0) {
               this.fillPool();
            }

         });
      }

   }

   int[] getPoolStateCounts() {
      return this.connectionBag.getStateCounts();
   }

   private PoolEntry createPoolEntry() {
      try {
         PoolEntry poolEntry = this.newPoolEntry();
         long maxLifetime = this.config.getMaxLifetime();
         long keepaliveTime;
         long variance;
         if (maxLifetime > 0L) {
            keepaliveTime = maxLifetime > 10000L ? ThreadLocalRandom.current().nextLong(maxLifetime / 40L) : 0L;
            variance = maxLifetime - keepaliveTime;
            poolEntry.setFutureEol(this.houseKeepingExecutorService.schedule(new MaxLifetimeTask(poolEntry), variance, TimeUnit.MILLISECONDS));
         }

         keepaliveTime = this.config.getKeepaliveTime();
         if (keepaliveTime > 0L) {
            variance = ThreadLocalRandom.current().nextLong(keepaliveTime / 10L);
            long heartbeatTime = keepaliveTime - variance;
            poolEntry.setKeepalive(this.houseKeepingExecutorService.scheduleWithFixedDelay(new KeepaliveTask(poolEntry), heartbeatTime, heartbeatTime, TimeUnit.MILLISECONDS));
         }

         return poolEntry;
      } catch (PoolBase.ConnectionSetupException var10) {
         if (this.poolState == 0) {
            this.logger.error((String)"{} - Error thrown while acquiring connection from data source", (Object)this.poolName, (Object)var10.getCause());
            this.lastConnectionFailure.set(var10);
         }
      } catch (Exception var11) {
         if (this.poolState == 0) {
            this.logger.debug((String)"{} - Cannot acquire connection from data source", (Object)this.poolName, (Object)var11);
         }
      }

      return null;
   }

   private synchronized void fillPool() {
      int connectionsToAdd = Math.min(this.config.getMaximumPoolSize() - this.getTotalConnections(), this.config.getMinimumIdle() - this.getIdleConnections()) - this.addConnectionQueueReadOnlyView.size();
      if (connectionsToAdd <= 0) {
         this.logger.debug((String)"{} - Fill pool skipped, pool is at sufficient level.", (Object)this.poolName);
      }

      for(int i = 0; i < connectionsToAdd; ++i) {
         this.addConnectionExecutor.submit(i < connectionsToAdd - 1 ? this.poolEntryCreator : this.postFillPoolEntryCreator);
      }

   }

   private void abortActiveConnections(ExecutorService assassinExecutor) {
      Iterator var2 = this.connectionBag.values(1).iterator();

      while(var2.hasNext()) {
         PoolEntry poolEntry = (PoolEntry)var2.next();
         Connection connection = poolEntry.close();

         try {
            connection.abort(assassinExecutor);
         } catch (Throwable var9) {
            this.quietlyCloseConnection(connection, "(connection aborted during shutdown)");
         } finally {
            this.connectionBag.remove(poolEntry);
         }
      }

   }

   private void checkFailFast() {
      long initializationTimeout = this.config.getInitializationFailTimeout();
      if (initializationTimeout >= 0L) {
         long startTime = ClockSource.currentTime();

         do {
            PoolEntry poolEntry = this.createPoolEntry();
            if (poolEntry != null) {
               if (this.config.getMinimumIdle() > 0) {
                  this.connectionBag.add(poolEntry);
                  this.logger.debug((String)"{} - Added connection {}", (Object)this.poolName, (Object)poolEntry.connection);
               } else {
                  this.quietlyCloseConnection(poolEntry.close(), "(initialization check complete and minimumIdle is zero)");
               }

               return;
            }

            if (this.getLastConnectionFailure() instanceof PoolBase.ConnectionSetupException) {
               this.throwPoolInitializationException(this.getLastConnectionFailure().getCause());
            }

            UtilityElf.quietlySleep(TimeUnit.SECONDS.toMillis(1L));
         } while(ClockSource.elapsedMillis(startTime) < initializationTimeout);

         if (initializationTimeout > 0L) {
            this.throwPoolInitializationException(this.getLastConnectionFailure());
         }

      }
   }

   private void throwPoolInitializationException(Throwable t) {
      this.logger.error((String)"{} - Exception during pool initialization.", (Object)this.poolName, (Object)t);
      this.destroyHouseKeepingExecutorService();
      throw new PoolInitializationException(t);
   }

   private boolean softEvictConnection(PoolEntry poolEntry, String reason, boolean owner) {
      poolEntry.markEvicted();
      if (!owner && !this.connectionBag.reserve(poolEntry)) {
         return false;
      } else {
         this.closeConnection(poolEntry, reason);
         return true;
      }
   }

   private ScheduledExecutorService initializeHouseKeepingExecutorService() {
      if (this.config.getScheduledExecutor() == null) {
         ThreadFactory threadFactory = (ThreadFactory)Optional.ofNullable(this.config.getThreadFactory()).orElseGet(() -> {
            return new UtilityElf.DefaultThreadFactory(this.poolName + " housekeeper", true);
         });
         ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1, threadFactory, new ThreadPoolExecutor.DiscardPolicy());
         executor.setExecuteExistingDelayedTasksAfterShutdownPolicy(false);
         executor.setRemoveOnCancelPolicy(true);
         return executor;
      } else {
         return this.config.getScheduledExecutor();
      }
   }

   private void destroyHouseKeepingExecutorService() {
      if (this.config.getScheduledExecutor() == null) {
         this.houseKeepingExecutorService.shutdownNow();
      }

   }

   private PoolStats getPoolStats() {
      return new PoolStats(TimeUnit.SECONDS.toMillis(1L)) {
         protected void update() {
            this.pendingThreads = HikariPool.this.getThreadsAwaitingConnection();
            this.idleConnections = HikariPool.this.getIdleConnections();
            this.totalConnections = HikariPool.this.getTotalConnections();
            this.activeConnections = HikariPool.this.getActiveConnections();
            this.maxConnections = HikariPool.this.config.getMaximumPoolSize();
            this.minConnections = HikariPool.this.config.getMinimumIdle();
         }
      };
   }

   private SQLException createTimeoutException(long startTime) {
      this.logPoolState("Timeout failure ");
      this.metricsTracker.recordConnectionTimeout();
      String sqlState = null;
      Throwable originalException = this.getLastConnectionFailure();
      if (originalException instanceof SQLException) {
         sqlState = ((SQLException)originalException).getSQLState();
      }

      SQLException connectionException = new SQLTransientConnectionException(this.poolName + " - Connection is not available, request timed out after " + ClockSource.elapsedMillis(startTime) + "ms.", sqlState, originalException);
      if (originalException instanceof SQLException) {
         connectionException.setNextException((SQLException)originalException);
      }

      return connectionException;
   }

   public static class PoolInitializationException extends RuntimeException {
      private static final long serialVersionUID = 929872118275916520L;

      public PoolInitializationException(Throwable t) {
         super("Failed to initialize pool: " + t.getMessage(), t);
      }
   }

   private final class KeepaliveTask implements Runnable {
      private final PoolEntry poolEntry;

      KeepaliveTask(PoolEntry poolEntry) {
         this.poolEntry = poolEntry;
      }

      public void run() {
         if (HikariPool.this.connectionBag.reserve(this.poolEntry)) {
            if (!HikariPool.this.isConnectionAlive(this.poolEntry.connection)) {
               HikariPool.this.softEvictConnection(this.poolEntry, "(connection is dead)", true);
               HikariPool.this.addBagItem(HikariPool.this.connectionBag.getWaitingThreadCount());
            } else {
               HikariPool.this.connectionBag.unreserve(this.poolEntry);
               HikariPool.this.logger.debug((String)"{} - keepalive: connection {} is alive", (Object)HikariPool.this.poolName, (Object)this.poolEntry.connection);
            }
         }

      }
   }

   private final class MaxLifetimeTask implements Runnable {
      private final PoolEntry poolEntry;

      MaxLifetimeTask(PoolEntry poolEntry) {
         this.poolEntry = poolEntry;
      }

      public void run() {
         if (HikariPool.this.softEvictConnection(this.poolEntry, "(connection has passed maxLifetime)", false)) {
            HikariPool.this.addBagItem(HikariPool.this.connectionBag.getWaitingThreadCount());
         }

      }
   }

   private final class HouseKeeper implements Runnable {
      private volatile long previous;

      private HouseKeeper() {
         this.previous = ClockSource.plusMillis(ClockSource.currentTime(), -HikariPool.this.housekeepingPeriodMs);
      }

      public void run() {
         try {
            HikariPool.this.connectionTimeout = HikariPool.this.config.getConnectionTimeout();
            HikariPool.this.validationTimeout = HikariPool.this.config.getValidationTimeout();
            HikariPool.this.leakTaskFactory.updateLeakDetectionThreshold(HikariPool.this.config.getLeakDetectionThreshold());
            HikariPool.this.catalog = HikariPool.this.config.getCatalog() != null && !HikariPool.this.config.getCatalog().equals(HikariPool.this.catalog) ? HikariPool.this.config.getCatalog() : HikariPool.this.catalog;
            long idleTimeout = HikariPool.this.config.getIdleTimeout();
            long now = ClockSource.currentTime();
            if (ClockSource.plusMillis(now, 128L) < ClockSource.plusMillis(this.previous, HikariPool.this.housekeepingPeriodMs)) {
               HikariPool.this.logger.warn((String)"{} - Retrograde clock change detected (housekeeper delta={}), soft-evicting connections from pool.", (Object)HikariPool.this.poolName, (Object)ClockSource.elapsedDisplayString(this.previous, now));
               this.previous = now;
               HikariPool.this.softEvictConnections();
               return;
            }

            if (now > ClockSource.plusMillis(this.previous, 3L * HikariPool.this.housekeepingPeriodMs / 2L)) {
               HikariPool.this.logger.warn((String)"{} - Thread starvation or clock leap detected (housekeeper delta={}).", (Object)HikariPool.this.poolName, (Object)ClockSource.elapsedDisplayString(this.previous, now));
            }

            this.previous = now;
            String afterPrefix = "Pool ";
            if (idleTimeout > 0L && HikariPool.this.config.getMinimumIdle() < HikariPool.this.config.getMaximumPoolSize()) {
               HikariPool.this.logPoolState("Before cleanup ");
               afterPrefix = "After cleanup  ";
               List<PoolEntry> notInUse = HikariPool.this.connectionBag.values(0);
               int toRemove = notInUse.size() - HikariPool.this.config.getMinimumIdle();
               Iterator var8 = notInUse.iterator();

               while(var8.hasNext()) {
                  PoolEntry entry = (PoolEntry)var8.next();
                  if (toRemove > 0 && ClockSource.elapsedMillis(entry.lastAccessed, now) > idleTimeout && HikariPool.this.connectionBag.reserve(entry)) {
                     HikariPool.this.closeConnection(entry, "(connection has passed idleTimeout)");
                     --toRemove;
                  }
               }
            }

            HikariPool.this.logPoolState(afterPrefix);
            HikariPool.this.fillPool();
         } catch (Exception var10) {
            HikariPool.this.logger.error((String)"Unexpected exception in housekeeping task", (Throwable)var10);
         }

      }

      // $FF: synthetic method
      HouseKeeper(Object x1) {
         this();
      }
   }

   private final class PoolEntryCreator implements Callable<Boolean> {
      private final String loggingPrefix;

      PoolEntryCreator(String loggingPrefix) {
         this.loggingPrefix = loggingPrefix;
      }

      public Boolean call() {
         for(long sleepBackoff = 250L; HikariPool.this.poolState == 0 && this.shouldCreateAnotherConnection(); sleepBackoff = Math.min(TimeUnit.SECONDS.toMillis(10L), Math.min(HikariPool.this.connectionTimeout, (long)((double)sleepBackoff * 1.5)))) {
            PoolEntry poolEntry = HikariPool.this.createPoolEntry();
            if (poolEntry != null) {
               HikariPool.this.connectionBag.add(poolEntry);
               HikariPool.this.logger.debug((String)"{} - Added connection {}", (Object)HikariPool.this.poolName, (Object)poolEntry.connection);
               if (this.loggingPrefix != null) {
                  HikariPool.this.logPoolState(this.loggingPrefix);
               }

               return Boolean.TRUE;
            }

            if (this.loggingPrefix != null) {
               HikariPool.this.logger.debug((String)"{} - Connection add failed, sleeping with backoff: {}ms", (Object)HikariPool.this.poolName, (Object)sleepBackoff);
            }

            UtilityElf.quietlySleep(sleepBackoff);
         }

         return Boolean.FALSE;
      }

      private synchronized boolean shouldCreateAnotherConnection() {
         return HikariPool.this.getTotalConnections() < HikariPool.this.config.getMaximumPoolSize() && (HikariPool.this.connectionBag.getWaitingThreadCount() > 0 || HikariPool.this.getIdleConnections() < HikariPool.this.config.getMinimumIdle());
      }
   }
}
