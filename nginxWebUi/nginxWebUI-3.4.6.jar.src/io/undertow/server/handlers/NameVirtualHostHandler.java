/*    */ package io.undertow.server.handlers;
/*    */ 
/*    */ import io.undertow.Handlers;
/*    */ import io.undertow.server.HttpHandler;
/*    */ import io.undertow.server.HttpServerExchange;
/*    */ import io.undertow.util.CopyOnWriteMap;
/*    */ import io.undertow.util.Headers;
/*    */ import java.util.Locale;
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
/*    */ 
/*    */ 
/*    */ public class NameVirtualHostHandler
/*    */   implements HttpHandler
/*    */ {
/* 38 */   private volatile HttpHandler defaultHandler = ResponseCodeHandler.HANDLE_404;
/* 39 */   private final Map<String, HttpHandler> hosts = (Map<String, HttpHandler>)new CopyOnWriteMap();
/*    */ 
/*    */   
/*    */   public void handleRequest(HttpServerExchange exchange) throws Exception {
/* 43 */     String hostHeader = exchange.getRequestHeaders().getFirst(Headers.HOST);
/* 44 */     if (hostHeader != null) {
/*    */       String host;
/* 46 */       if (hostHeader.contains(":")) {
/* 47 */         host = hostHeader.substring(0, hostHeader.lastIndexOf(":"));
/*    */       } else {
/* 49 */         host = hostHeader;
/*    */       } 
/*    */       
/* 52 */       HttpHandler handler = this.hosts.get(host);
/* 53 */       if (handler != null) {
/* 54 */         handler.handleRequest(exchange);
/*    */         
/*    */         return;
/*    */       } 
/* 58 */       handler = this.hosts.get(host.toLowerCase(Locale.ENGLISH));
/* 59 */       if (handler != null) {
/* 60 */         handler.handleRequest(exchange);
/*    */         return;
/*    */       } 
/*    */     } 
/* 64 */     this.defaultHandler.handleRequest(exchange);
/*    */   }
/*    */   
/*    */   public HttpHandler getDefaultHandler() {
/* 68 */     return this.defaultHandler;
/*    */   }
/*    */   
/*    */   public Map<String, HttpHandler> getHosts() {
/* 72 */     return this.hosts;
/*    */   }
/*    */   
/*    */   public NameVirtualHostHandler setDefaultHandler(HttpHandler defaultHandler) {
/* 76 */     Handlers.handlerNotNull(defaultHandler);
/* 77 */     this.defaultHandler = defaultHandler;
/* 78 */     return this;
/*    */   }
/*    */   
/*    */   public synchronized NameVirtualHostHandler addHost(String host, HttpHandler handler) {
/* 82 */     Handlers.handlerNotNull(handler);
/* 83 */     this.hosts.put(host.toLowerCase(Locale.ENGLISH), handler);
/* 84 */     return this;
/*    */   }
/*    */   
/*    */   public synchronized NameVirtualHostHandler removeHost(String host) {
/* 88 */     this.hosts.remove(host.toLowerCase(Locale.ENGLISH));
/* 89 */     return this;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\handlers\NameVirtualHostHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */