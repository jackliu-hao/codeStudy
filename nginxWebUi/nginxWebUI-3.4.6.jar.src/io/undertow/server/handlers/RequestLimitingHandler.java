/*     */ package io.undertow.server.handlers;
/*     */ 
/*     */ import io.undertow.server.HandlerWrapper;
/*     */ import io.undertow.server.HttpHandler;
/*     */ import io.undertow.server.HttpServerExchange;
/*     */ import io.undertow.server.handlers.builder.HandlerBuilder;
/*     */ import java.util.Collections;
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
/*     */ 
/*     */ 
/*     */ public final class RequestLimitingHandler
/*     */   implements HttpHandler
/*     */ {
/*     */   private final HttpHandler nextHandler;
/*     */   private final RequestLimit requestLimit;
/*     */   
/*     */   public RequestLimitingHandler(int maximumConcurrentRequests, HttpHandler nextHandler) {
/*  49 */     this(maximumConcurrentRequests, -1, nextHandler);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public RequestLimitingHandler(int maximumConcurrentRequests, int queueSize, HttpHandler nextHandler) {
/*  61 */     if (nextHandler == null) {
/*  62 */       throw new IllegalArgumentException("nextHandler is null");
/*     */     }
/*  64 */     if (maximumConcurrentRequests < 1) {
/*  65 */       throw new IllegalArgumentException("Maximum concurrent requests must be at least 1");
/*     */     }
/*  67 */     this.requestLimit = new RequestLimit(maximumConcurrentRequests, queueSize);
/*  68 */     this.nextHandler = nextHandler;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public RequestLimitingHandler(RequestLimit requestLimit, HttpHandler nextHandler) {
/*  79 */     if (nextHandler == null) {
/*  80 */       throw new IllegalArgumentException("nextHandler is null");
/*     */     }
/*  82 */     this.requestLimit = requestLimit;
/*  83 */     this.nextHandler = nextHandler;
/*     */   }
/*     */   
/*     */   public void handleRequest(HttpServerExchange exchange) throws Exception {
/*  87 */     this.requestLimit.handleRequest(exchange, this.nextHandler);
/*     */   }
/*     */   
/*     */   public RequestLimit getRequestLimit() {
/*  91 */     return this.requestLimit;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/*  96 */     return "request-limit( " + this.requestLimit.getMaximumConcurrentRequests() + " )";
/*     */   }
/*     */   
/*     */   public static class Builder
/*     */     implements HandlerBuilder
/*     */   {
/*     */     public String name() {
/* 103 */       return "request-limit";
/*     */     }
/*     */ 
/*     */     
/*     */     public Map<String, Class<?>> parameters() {
/* 108 */       return Collections.singletonMap("requests", int.class);
/*     */     }
/*     */ 
/*     */     
/*     */     public Set<String> requiredParameters() {
/* 113 */       return Collections.singleton("requests");
/*     */     }
/*     */ 
/*     */     
/*     */     public String defaultParameter() {
/* 118 */       return "requests";
/*     */     }
/*     */ 
/*     */     
/*     */     public HandlerWrapper build(Map<String, Object> config) {
/* 123 */       return new RequestLimitingHandler.Wrapper(((Integer)config.get("requests")).intValue());
/*     */     }
/*     */   }
/*     */   
/*     */   private static class Wrapper
/*     */     implements HandlerWrapper
/*     */   {
/*     */     private final int requests;
/*     */     
/*     */     private Wrapper(int requests) {
/* 133 */       this.requests = requests;
/*     */     }
/*     */ 
/*     */     
/*     */     public HttpHandler wrap(HttpHandler handler) {
/* 138 */       return new RequestLimitingHandler(this.requests, handler);
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\handlers\RequestLimitingHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */