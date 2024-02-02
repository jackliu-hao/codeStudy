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
/*     */ public final class BlockingHandler
/*     */   implements HttpHandler
/*     */ {
/*     */   private volatile HttpHandler handler;
/*     */   
/*     */   public BlockingHandler(HttpHandler handler) {
/*  42 */     this.handler = handler;
/*     */   }
/*     */   
/*     */   public BlockingHandler() {
/*  46 */     this(null);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void handleRequest(HttpServerExchange exchange) throws Exception {
/*  52 */     exchange.startBlocking();
/*  53 */     if (exchange.isInIoThread()) {
/*  54 */       exchange.dispatch(this.handler);
/*     */     } else {
/*  56 */       this.handler.handleRequest(exchange);
/*     */     } 
/*     */   }
/*     */   
/*     */   public HttpHandler getHandler() {
/*  61 */     return this.handler;
/*     */   }
/*     */   
/*     */   public BlockingHandler setRootHandler(HttpHandler rootHandler) {
/*  65 */     this.handler = rootHandler;
/*  66 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/*  71 */     return "blocking()";
/*     */   }
/*     */   
/*     */   public static class Builder
/*     */     implements HandlerBuilder
/*     */   {
/*     */     public String name() {
/*  78 */       return "blocking";
/*     */     }
/*     */ 
/*     */     
/*     */     public Map<String, Class<?>> parameters() {
/*  83 */       return Collections.emptyMap();
/*     */     }
/*     */ 
/*     */     
/*     */     public Set<String> requiredParameters() {
/*  88 */       return Collections.emptySet();
/*     */     }
/*     */ 
/*     */     
/*     */     public String defaultParameter() {
/*  93 */       return null;
/*     */     }
/*     */ 
/*     */     
/*     */     public HandlerWrapper build(Map<String, Object> config) {
/*  98 */       return new BlockingHandler.Wrapper();
/*     */     }
/*     */   }
/*     */   
/*     */   private static class Wrapper implements HandlerWrapper {
/*     */     private Wrapper() {}
/*     */     
/*     */     public HttpHandler wrap(HttpHandler handler) {
/* 106 */       return new BlockingHandler(handler);
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\handlers\BlockingHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */