package com.zaxxer.hikari.metrics.prometheus;

import com.zaxxer.hikari.metrics.IMetricsTracker;
import com.zaxxer.hikari.metrics.MetricsTrackerFactory;
import com.zaxxer.hikari.metrics.PoolStats;
import io.prometheus.client.Collector;
import io.prometheus.client.CollectorRegistry;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PrometheusHistogramMetricsTrackerFactory implements MetricsTrackerFactory {
   private static final Map<CollectorRegistry, PrometheusMetricsTrackerFactory.RegistrationStatus> registrationStatuses = new ConcurrentHashMap();
   private final HikariCPCollector collector;
   private final CollectorRegistry collectorRegistry;

   public PrometheusHistogramMetricsTrackerFactory() {
      this(CollectorRegistry.defaultRegistry);
   }

   public PrometheusHistogramMetricsTrackerFactory(CollectorRegistry collectorRegistry) {
      this.collector = new HikariCPCollector();
      this.collectorRegistry = collectorRegistry;
   }

   public IMetricsTracker create(String poolName, PoolStats poolStats) {
      this.registerCollector(this.collector, this.collectorRegistry);
      this.collector.add(poolName, poolStats);
      return new PrometheusHistogramMetricsTracker(poolName, this.collectorRegistry, this.collector);
   }

   private void registerCollector(Collector collector, CollectorRegistry collectorRegistry) {
      if (registrationStatuses.putIfAbsent(collectorRegistry, PrometheusMetricsTrackerFactory.RegistrationStatus.REGISTERED) == null) {
         collector.register(collectorRegistry);
      }

   }
}
