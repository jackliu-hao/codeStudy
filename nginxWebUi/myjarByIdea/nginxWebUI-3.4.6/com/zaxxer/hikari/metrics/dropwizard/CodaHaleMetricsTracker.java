package com.zaxxer.hikari.metrics.dropwizard;

import com.codahale.metrics.Histogram;
import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.zaxxer.hikari.metrics.IMetricsTracker;
import com.zaxxer.hikari.metrics.PoolStats;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public final class CodaHaleMetricsTracker implements IMetricsTracker {
   private final String poolName;
   private final Timer connectionObtainTimer;
   private final Histogram connectionUsage;
   private final Histogram connectionCreation;
   private final Meter connectionTimeoutMeter;
   private final MetricRegistry registry;
   private static final String METRIC_CATEGORY = "pool";
   private static final String METRIC_NAME_WAIT = "Wait";
   private static final String METRIC_NAME_USAGE = "Usage";
   private static final String METRIC_NAME_CONNECT = "ConnectionCreation";
   private static final String METRIC_NAME_TIMEOUT_RATE = "ConnectionTimeoutRate";
   private static final String METRIC_NAME_TOTAL_CONNECTIONS = "TotalConnections";
   private static final String METRIC_NAME_IDLE_CONNECTIONS = "IdleConnections";
   private static final String METRIC_NAME_ACTIVE_CONNECTIONS = "ActiveConnections";
   private static final String METRIC_NAME_PENDING_CONNECTIONS = "PendingConnections";
   private static final String METRIC_NAME_MAX_CONNECTIONS = "MaxConnections";
   private static final String METRIC_NAME_MIN_CONNECTIONS = "MinConnections";

   CodaHaleMetricsTracker(String poolName, PoolStats poolStats, MetricRegistry registry) {
      this.poolName = poolName;
      this.registry = registry;
      this.connectionObtainTimer = registry.timer(MetricRegistry.name(poolName, new String[]{"pool", "Wait"}));
      this.connectionUsage = registry.histogram(MetricRegistry.name(poolName, new String[]{"pool", "Usage"}));
      this.connectionCreation = registry.histogram(MetricRegistry.name(poolName, new String[]{"pool", "ConnectionCreation"}));
      this.connectionTimeoutMeter = registry.meter(MetricRegistry.name(poolName, new String[]{"pool", "ConnectionTimeoutRate"}));
      String var10001 = MetricRegistry.name(poolName, new String[]{"pool", "TotalConnections"});
      Objects.requireNonNull(poolStats);
      registry.register(var10001, poolStats::getTotalConnections);
      var10001 = MetricRegistry.name(poolName, new String[]{"pool", "IdleConnections"});
      Objects.requireNonNull(poolStats);
      registry.register(var10001, poolStats::getIdleConnections);
      var10001 = MetricRegistry.name(poolName, new String[]{"pool", "ActiveConnections"});
      Objects.requireNonNull(poolStats);
      registry.register(var10001, poolStats::getActiveConnections);
      var10001 = MetricRegistry.name(poolName, new String[]{"pool", "PendingConnections"});
      Objects.requireNonNull(poolStats);
      registry.register(var10001, poolStats::getPendingThreads);
      var10001 = MetricRegistry.name(poolName, new String[]{"pool", "MaxConnections"});
      Objects.requireNonNull(poolStats);
      registry.register(var10001, poolStats::getMaxConnections);
      var10001 = MetricRegistry.name(poolName, new String[]{"pool", "MinConnections"});
      Objects.requireNonNull(poolStats);
      registry.register(var10001, poolStats::getMinConnections);
   }

   public void close() {
      this.registry.remove(MetricRegistry.name(this.poolName, new String[]{"pool", "Wait"}));
      this.registry.remove(MetricRegistry.name(this.poolName, new String[]{"pool", "Usage"}));
      this.registry.remove(MetricRegistry.name(this.poolName, new String[]{"pool", "ConnectionCreation"}));
      this.registry.remove(MetricRegistry.name(this.poolName, new String[]{"pool", "ConnectionTimeoutRate"}));
      this.registry.remove(MetricRegistry.name(this.poolName, new String[]{"pool", "TotalConnections"}));
      this.registry.remove(MetricRegistry.name(this.poolName, new String[]{"pool", "IdleConnections"}));
      this.registry.remove(MetricRegistry.name(this.poolName, new String[]{"pool", "ActiveConnections"}));
      this.registry.remove(MetricRegistry.name(this.poolName, new String[]{"pool", "PendingConnections"}));
      this.registry.remove(MetricRegistry.name(this.poolName, new String[]{"pool", "MaxConnections"}));
      this.registry.remove(MetricRegistry.name(this.poolName, new String[]{"pool", "MinConnections"}));
   }

   public void recordConnectionAcquiredNanos(long elapsedAcquiredNanos) {
      this.connectionObtainTimer.update(elapsedAcquiredNanos, TimeUnit.NANOSECONDS);
   }

   public void recordConnectionUsageMillis(long elapsedBorrowedMillis) {
      this.connectionUsage.update(elapsedBorrowedMillis);
   }

   public void recordConnectionTimeout() {
      this.connectionTimeoutMeter.mark();
   }

   public void recordConnectionCreatedMillis(long connectionCreatedMillis) {
      this.connectionCreation.update(connectionCreatedMillis);
   }

   public Timer getConnectionAcquisitionTimer() {
      return this.connectionObtainTimer;
   }

   public Histogram getConnectionDurationHistogram() {
      return this.connectionUsage;
   }

   public Histogram getConnectionCreationHistogram() {
      return this.connectionCreation;
   }
}
