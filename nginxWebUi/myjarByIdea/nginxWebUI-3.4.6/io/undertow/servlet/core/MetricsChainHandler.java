package io.undertow.servlet.core;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.MetricsHandler;
import io.undertow.servlet.api.Deployment;
import io.undertow.servlet.api.MetricsCollector;
import io.undertow.servlet.api.ServletInfo;
import io.undertow.servlet.handlers.ServletHandler;
import io.undertow.servlet.handlers.ServletRequestContext;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

class MetricsChainHandler implements HttpHandler {
   private final HttpHandler next;
   private final Map<String, MetricsHandler> servletHandlers;

   MetricsChainHandler(HttpHandler next, MetricsCollector collector, Deployment deployment) {
      this.next = next;
      Map<String, MetricsHandler> servletHandlers = new HashMap();
      Iterator var5 = deployment.getServlets().getServletHandlers().entrySet().iterator();

      while(var5.hasNext()) {
         Map.Entry<String, ServletHandler> entry = (Map.Entry)var5.next();
         MetricsHandler handler = new MetricsHandler(next);
         servletHandlers.put(entry.getKey(), handler);
         collector.registerMetric((String)entry.getKey(), handler);
      }

      this.servletHandlers = Collections.unmodifiableMap(servletHandlers);
   }

   public void handleRequest(HttpServerExchange exchange) throws Exception {
      ServletRequestContext context = (ServletRequestContext)exchange.getAttachment(ServletRequestContext.ATTACHMENT_KEY);
      ServletInfo servletInfo = context.getCurrentServlet().getManagedServlet().getServletInfo();
      MetricsHandler handler = (MetricsHandler)this.servletHandlers.get(servletInfo.getName());
      if (handler != null) {
         handler.handleRequest(exchange);
      } else {
         this.next.handleRequest(exchange);
      }

   }
}
