package com.zaxxer.hikari.metrics;

public interface MetricsTrackerFactory {
   IMetricsTracker create(String var1, PoolStats var2);
}
