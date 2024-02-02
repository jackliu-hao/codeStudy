/*    */ package io.undertow.server.handlers;
/*    */ 
/*    */ import io.undertow.server.HandlerWrapper;
/*    */ import io.undertow.server.HttpHandler;
/*    */ import io.undertow.server.HttpServerExchange;
/*    */ import io.undertow.server.handlers.builder.HandlerBuilder;
/*    */ import io.undertow.util.Headers;
/*    */ import java.util.Collections;
/*    */ import java.util.Map;
/*    */ import java.util.Set;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class DisableCacheHandler
/*    */   implements HttpHandler
/*    */ {
/*    */   private final HttpHandler next;
/*    */   
/*    */   public DisableCacheHandler(HttpHandler next) {
/* 25 */     this.next = next;
/*    */   }
/*    */ 
/*    */   
/*    */   public void handleRequest(HttpServerExchange exchange) throws Exception {
/* 30 */     exchange.getResponseHeaders().add(Headers.CACHE_CONTROL, "no-cache, no-store, must-revalidate");
/* 31 */     exchange.getResponseHeaders().add(Headers.PRAGMA, "no-cache");
/* 32 */     exchange.getResponseHeaders().add(Headers.EXPIRES, "0");
/* 33 */     this.next.handleRequest(exchange);
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 38 */     return "disable-cache()";
/*    */   }
/*    */   
/*    */   public static class Builder
/*    */     implements HandlerBuilder
/*    */   {
/*    */     public String name() {
/* 45 */       return "disable-cache";
/*    */     }
/*    */ 
/*    */     
/*    */     public Map<String, Class<?>> parameters() {
/* 50 */       return Collections.emptyMap();
/*    */     }
/*    */ 
/*    */     
/*    */     public Set<String> requiredParameters() {
/* 55 */       return Collections.emptySet();
/*    */     }
/*    */ 
/*    */     
/*    */     public String defaultParameter() {
/* 60 */       return null;
/*    */     }
/*    */ 
/*    */     
/*    */     public HandlerWrapper build(Map<String, Object> config) {
/* 65 */       return new DisableCacheHandler.Wrapper();
/*    */     }
/*    */   }
/*    */   
/*    */   private static class Wrapper implements HandlerWrapper {
/*    */     private Wrapper() {}
/*    */     
/*    */     public HttpHandler wrap(HttpHandler handler) {
/* 73 */       return new DisableCacheHandler(handler);
/*    */     }
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\handlers\DisableCacheHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */