/*     */ package io.undertow.server.handlers;
/*     */ 
/*     */ import io.undertow.server.HandlerWrapper;
/*     */ import io.undertow.server.HttpHandler;
/*     */ import io.undertow.server.HttpServerExchange;
/*     */ import io.undertow.server.handlers.builder.HandlerBuilder;
/*     */ import io.undertow.util.HeaderValues;
/*     */ import io.undertow.util.Headers;
/*     */ import io.undertow.util.Methods;
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
/*     */ public class HttpTraceHandler
/*     */   implements HttpHandler
/*     */ {
/*     */   private final HttpHandler handler;
/*     */   
/*     */   public HttpTraceHandler(HttpHandler handler) {
/*  43 */     this.handler = handler;
/*     */   }
/*     */ 
/*     */   
/*     */   public void handleRequest(HttpServerExchange exchange) throws Exception {
/*  48 */     if (exchange.getRequestMethod().equals(Methods.TRACE)) {
/*  49 */       exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "message/http");
/*  50 */       StringBuilder body = new StringBuilder("TRACE ");
/*  51 */       body.append(exchange.getRequestURI());
/*  52 */       if (!exchange.getQueryString().isEmpty()) {
/*  53 */         body.append('?');
/*  54 */         body.append(exchange.getQueryString());
/*     */       } 
/*  56 */       body.append(' ');
/*  57 */       body.append(exchange.getProtocol().toString());
/*  58 */       body.append("\r\n");
/*  59 */       for (HeaderValues header : exchange.getRequestHeaders()) {
/*  60 */         for (String value : header) {
/*  61 */           body.append(header.getHeaderName());
/*  62 */           body.append(": ");
/*  63 */           body.append(value);
/*  64 */           body.append("\r\n");
/*     */         } 
/*     */       } 
/*  67 */       body.append("\r\n");
/*  68 */       exchange.getResponseSender().send(body.toString());
/*     */     } else {
/*  70 */       this.handler.handleRequest(exchange);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/*  76 */     return "trace()";
/*     */   }
/*     */ 
/*     */   
/*     */   public static class Builder
/*     */     implements HandlerBuilder
/*     */   {
/*     */     public String name() {
/*  84 */       return "trace";
/*     */     }
/*     */ 
/*     */     
/*     */     public Map<String, Class<?>> parameters() {
/*  89 */       return Collections.emptyMap();
/*     */     }
/*     */ 
/*     */     
/*     */     public Set<String> requiredParameters() {
/*  94 */       return Collections.emptySet();
/*     */     }
/*     */ 
/*     */     
/*     */     public String defaultParameter() {
/*  99 */       return null;
/*     */     }
/*     */ 
/*     */     
/*     */     public HandlerWrapper build(Map<String, Object> config) {
/* 104 */       return new HttpTraceHandler.Wrapper();
/*     */     }
/*     */   }
/*     */   
/*     */   private static class Wrapper implements HandlerWrapper {
/*     */     private Wrapper() {}
/*     */     
/*     */     public HttpHandler wrap(HttpHandler handler) {
/* 112 */       return new HttpTraceHandler(handler);
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\handlers\HttpTraceHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */