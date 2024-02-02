/*    */ package io.undertow.servlet.handlers;
/*    */ 
/*    */ import io.undertow.server.HandlerWrapper;
/*    */ import io.undertow.server.HttpHandler;
/*    */ import io.undertow.server.HttpServerExchange;
/*    */ import io.undertow.server.handlers.builder.HandlerBuilder;
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
/*    */ 
/*    */ 
/*    */ public class MarkSecureHandler
/*    */   implements HttpHandler
/*    */ {
/* 37 */   public static final HandlerWrapper WRAPPER = new Wrapper();
/*    */   
/*    */   private final HttpHandler next;
/*    */   
/*    */   public MarkSecureHandler(HttpHandler next) {
/* 42 */     this.next = next;
/*    */   }
/*    */ 
/*    */   
/*    */   public void handleRequest(HttpServerExchange exchange) throws Exception {
/* 47 */     exchange.putAttachment(HttpServerExchange.SECURE_REQUEST, Boolean.TRUE);
/* 48 */     this.next.handleRequest(exchange);
/*    */   }
/*    */   
/*    */   public static class Wrapper
/*    */     implements HandlerWrapper
/*    */   {
/*    */     public HttpHandler wrap(HttpHandler handler) {
/* 55 */       return new MarkSecureHandler(handler);
/*    */     }
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 61 */     return "mark-secure()";
/*    */   }
/*    */   
/*    */   public static class Builder
/*    */     implements HandlerBuilder
/*    */   {
/*    */     public String name() {
/* 68 */       return "mark-secure";
/*    */     }
/*    */ 
/*    */     
/*    */     public Map<String, Class<?>> parameters() {
/* 73 */       return Collections.emptyMap();
/*    */     }
/*    */ 
/*    */     
/*    */     public Set<String> requiredParameters() {
/* 78 */       return Collections.emptySet();
/*    */     }
/*    */ 
/*    */     
/*    */     public String defaultParameter() {
/* 83 */       return null;
/*    */     }
/*    */ 
/*    */     
/*    */     public HandlerWrapper build(Map<String, Object> config) {
/* 88 */       return MarkSecureHandler.WRAPPER;
/*    */     }
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\servlet\handlers\MarkSecureHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */