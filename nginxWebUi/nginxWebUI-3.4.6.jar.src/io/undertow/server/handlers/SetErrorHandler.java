/*     */ package io.undertow.server.handlers;
/*     */ 
/*     */ import io.undertow.server.HandlerWrapper;
/*     */ import io.undertow.server.HttpHandler;
/*     */ import io.undertow.server.HttpServerExchange;
/*     */ import io.undertow.server.handlers.builder.HandlerBuilder;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class SetErrorHandler
/*     */   implements HttpHandler
/*     */ {
/*     */   private final int responseCode;
/*     */   private final HttpHandler next;
/*     */   
/*     */   public SetErrorHandler(HttpHandler next, int responseCode) {
/*  48 */     this.next = next;
/*  49 */     this.responseCode = responseCode;
/*     */   }
/*     */ 
/*     */   
/*     */   public void handleRequest(HttpServerExchange exchange) throws Exception {
/*  54 */     exchange.setStatusCode(this.responseCode);
/*  55 */     this.next.handleRequest(exchange);
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/*  60 */     return "set-error( " + this.responseCode + " )";
/*     */   }
/*     */ 
/*     */   
/*     */   public static class Builder
/*     */     implements HandlerBuilder
/*     */   {
/*     */     public String name() {
/*  68 */       return "set-error";
/*     */     }
/*     */ 
/*     */     
/*     */     public Map<String, Class<?>> parameters() {
/*  73 */       Map<String, Class<?>> params = new HashMap<>();
/*  74 */       params.put("response-code", Integer.class);
/*  75 */       return params;
/*     */     }
/*     */ 
/*     */     
/*     */     public Set<String> requiredParameters() {
/*  80 */       Set<String> req = new HashSet<>();
/*  81 */       req.add("response-code");
/*  82 */       return req;
/*     */     }
/*     */ 
/*     */     
/*     */     public String defaultParameter() {
/*  87 */       return "response-code";
/*     */     }
/*     */ 
/*     */     
/*     */     public HandlerWrapper build(Map<String, Object> config) {
/*  92 */       return new SetErrorHandler.Wrapper((Integer)config.get("response-code"));
/*     */     }
/*     */   }
/*     */   
/*     */   private static class Wrapper
/*     */     implements HandlerWrapper
/*     */   {
/*     */     private final Integer responseCode;
/*     */     
/*     */     private Wrapper(Integer responseCode) {
/* 102 */       this.responseCode = responseCode;
/*     */     }
/*     */ 
/*     */     
/*     */     public HttpHandler wrap(HttpHandler handler) {
/* 107 */       return new SetErrorHandler(handler, this.responseCode.intValue());
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\handlers\SetErrorHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */