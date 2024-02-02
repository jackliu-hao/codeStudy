/*    */ package io.undertow.server.handlers;
/*    */ 
/*    */ import io.undertow.server.HttpHandler;
/*    */ import io.undertow.server.HttpServerExchange;
/*    */ import io.undertow.util.HeaderMap;
/*    */ import io.undertow.util.HttpString;
/*    */ import io.undertow.util.Methods;
/*    */ import io.undertow.util.PathMatcher;
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
/*    */ public class ConfiguredPushHandler
/*    */   implements HttpHandler
/*    */ {
/* 35 */   private final PathMatcher<String[]> pathMatcher = new PathMatcher();
/*    */   private final HttpHandler next;
/* 37 */   private final HeaderMap requestHeaders = new HeaderMap();
/*    */   
/*    */   public ConfiguredPushHandler(HttpHandler next) {
/* 40 */     this.next = next;
/*    */   }
/*    */ 
/*    */   
/*    */   public void handleRequest(HttpServerExchange exchange) throws Exception {
/* 45 */     if (exchange.getConnection().isPushSupported()) {
/* 46 */       PathMatcher.PathMatch<String[]> result = this.pathMatcher.match(exchange.getRelativePath());
/* 47 */       if (result != null) {
/* 48 */         String[] value = (String[])result.getValue();
/* 49 */         for (int i = 0; i < value.length; i++) {
/* 50 */           exchange.getConnection().pushResource(value[i], Methods.GET, this.requestHeaders);
/*    */         }
/*    */       } 
/*    */     } 
/* 54 */     this.next.handleRequest(exchange);
/*    */   }
/*    */   
/*    */   public ConfiguredPushHandler addRequestHeader(HttpString name, String value) {
/* 58 */     this.requestHeaders.put(name, value);
/* 59 */     return this;
/*    */   }
/*    */   
/*    */   public ConfiguredPushHandler addRoute(String url, String... resourcesToPush) {
/* 63 */     if (url.endsWith("/*")) {
/* 64 */       String partial = url.substring(0, url.length() - 1);
/* 65 */       this.pathMatcher.addPrefixPath(partial, resourcesToPush);
/*    */     } else {
/* 67 */       this.pathMatcher.addExactPath(url, resourcesToPush);
/*    */     } 
/* 69 */     return this;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\handlers\ConfiguredPushHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */