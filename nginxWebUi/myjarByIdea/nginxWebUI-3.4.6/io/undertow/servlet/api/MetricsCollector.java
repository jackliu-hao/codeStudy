package io.undertow.servlet.api;

import io.undertow.server.handlers.MetricsHandler;

public interface MetricsCollector {
   void registerMetric(String var1, MetricsHandler var2);
}
