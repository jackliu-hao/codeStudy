/*    */ package io.undertow.servlet.core;
/*    */ 
/*    */ import io.undertow.server.HttpHandler;
/*    */ import io.undertow.server.HttpServerExchange;
/*    */ import io.undertow.server.handlers.MetricsHandler;
/*    */ import io.undertow.servlet.api.Deployment;
/*    */ import io.undertow.servlet.api.MetricsCollector;
/*    */ import io.undertow.servlet.api.ServletInfo;
/*    */ import io.undertow.servlet.handlers.ServletHandler;
/*    */ import io.undertow.servlet.handlers.ServletRequestContext;
/*    */ import java.util.Collections;
/*    */ import java.util.HashMap;
/*    */ import java.util.Map;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ class MetricsChainHandler
/*    */   implements HttpHandler
/*    */ {
/*    */   private final HttpHandler next;
/*    */   private final Map<String, MetricsHandler> servletHandlers;
/*    */   
/*    */   MetricsChainHandler(HttpHandler next, MetricsCollector collector, Deployment deployment) {
/* 44 */     this.next = next;
/* 45 */     Map<String, MetricsHandler> servletHandlers = new HashMap<>();
/* 46 */     for (Map.Entry<String, ServletHandler> entry : deployment.getServlets().getServletHandlers().entrySet()) {
/* 47 */       MetricsHandler handler = new MetricsHandler(next);
/* 48 */       servletHandlers.put(entry.getKey(), handler);
/* 49 */       collector.registerMetric(entry.getKey(), handler);
/*    */     } 
/* 51 */     this.servletHandlers = Collections.unmodifiableMap(servletHandlers);
/*    */   }
/*    */   
/*    */   public void handleRequest(HttpServerExchange exchange) throws Exception {
/* 55 */     ServletRequestContext context = (ServletRequestContext)exchange.getAttachment(ServletRequestContext.ATTACHMENT_KEY);
/* 56 */     ServletInfo servletInfo = context.getCurrentServlet().getManagedServlet().getServletInfo();
/* 57 */     MetricsHandler handler = this.servletHandlers.get(servletInfo.getName());
/* 58 */     if (handler != null) {
/* 59 */       handler.handleRequest(exchange);
/*    */     } else {
/* 61 */       this.next.handleRequest(exchange);
/*    */     } 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\servlet\core\MetricsChainHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */