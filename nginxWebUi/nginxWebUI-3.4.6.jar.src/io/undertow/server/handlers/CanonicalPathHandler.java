/*    */ package io.undertow.server.handlers;
/*    */ 
/*    */ import io.undertow.Handlers;
/*    */ import io.undertow.server.HandlerWrapper;
/*    */ import io.undertow.server.HttpHandler;
/*    */ import io.undertow.server.HttpServerExchange;
/*    */ import io.undertow.server.handlers.builder.HandlerBuilder;
/*    */ import io.undertow.util.CanonicalPathUtils;
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
/*    */ public class CanonicalPathHandler
/*    */   implements HttpHandler
/*    */ {
/* 37 */   private volatile HttpHandler next = ResponseCodeHandler.HANDLE_404;
/*    */ 
/*    */   
/*    */   public CanonicalPathHandler() {}
/*    */   
/*    */   public CanonicalPathHandler(HttpHandler next) {
/* 43 */     this.next = next;
/*    */   }
/*    */ 
/*    */   
/*    */   public void handleRequest(HttpServerExchange exchange) throws Exception {
/* 48 */     exchange.setRelativePath(CanonicalPathUtils.canonicalize(exchange.getRelativePath()));
/* 49 */     this.next.handleRequest(exchange);
/*    */   }
/*    */   
/*    */   public HttpHandler getNext() {
/* 53 */     return this.next;
/*    */   }
/*    */   
/*    */   public CanonicalPathHandler setNext(HttpHandler next) {
/* 57 */     Handlers.handlerNotNull(next);
/* 58 */     this.next = next;
/* 59 */     return this;
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 64 */     return "canonical-path()";
/*    */   }
/*    */   
/*    */   public static class Builder
/*    */     implements HandlerBuilder
/*    */   {
/*    */     public String name() {
/* 71 */       return "canonical-path";
/*    */     }
/*    */ 
/*    */     
/*    */     public Map<String, Class<?>> parameters() {
/* 76 */       return Collections.emptyMap();
/*    */     }
/*    */ 
/*    */     
/*    */     public Set<String> requiredParameters() {
/* 81 */       return Collections.emptySet();
/*    */     }
/*    */ 
/*    */     
/*    */     public String defaultParameter() {
/* 86 */       return null;
/*    */     }
/*    */ 
/*    */     
/*    */     public HandlerWrapper build(Map<String, Object> config) {
/* 91 */       return new CanonicalPathHandler.Wrapper();
/*    */     }
/*    */   }
/*    */   
/*    */   private static class Wrapper implements HandlerWrapper {
/*    */     private Wrapper() {}
/*    */     
/*    */     public HttpHandler wrap(HttpHandler handler) {
/* 99 */       return new CanonicalPathHandler(handler);
/*    */     }
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\handlers\CanonicalPathHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */