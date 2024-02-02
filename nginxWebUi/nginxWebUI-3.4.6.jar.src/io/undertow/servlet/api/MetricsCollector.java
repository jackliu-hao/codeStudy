package io.undertow.servlet.api;

import io.undertow.server.handlers.MetricsHandler;

public interface MetricsCollector {
  void registerMetric(String paramString, MetricsHandler paramMetricsHandler);
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\servlet\api\MetricsCollector.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */