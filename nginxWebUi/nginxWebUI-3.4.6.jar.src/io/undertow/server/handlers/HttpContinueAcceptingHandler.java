/*     */ package io.undertow.server.handlers;
/*     */ 
/*     */ import io.undertow.UndertowLogger;
/*     */ import io.undertow.io.IoCallback;
/*     */ import io.undertow.io.Sender;
/*     */ import io.undertow.predicate.Predicate;
/*     */ import io.undertow.predicate.Predicates;
/*     */ import io.undertow.server.HandlerWrapper;
/*     */ import io.undertow.server.HttpHandler;
/*     */ import io.undertow.server.HttpServerExchange;
/*     */ import io.undertow.server.handlers.builder.HandlerBuilder;
/*     */ import io.undertow.server.protocol.http.HttpContinue;
/*     */ import java.io.IOException;
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
/*     */ public class HttpContinueAcceptingHandler
/*     */   implements HttpHandler
/*     */ {
/*     */   private final HttpHandler next;
/*     */   private final Predicate accept;
/*     */   
/*     */   public HttpContinueAcceptingHandler(HttpHandler next, Predicate accept) {
/*  54 */     this.next = next;
/*  55 */     this.accept = accept;
/*     */   }
/*     */   
/*     */   public HttpContinueAcceptingHandler(HttpHandler next) {
/*  59 */     this(next, Predicates.truePredicate());
/*     */   }
/*     */ 
/*     */   
/*     */   public void handleRequest(HttpServerExchange exchange) throws Exception {
/*  64 */     if (HttpContinue.requiresContinueResponse(exchange)) {
/*  65 */       if (this.accept.resolve(exchange)) {
/*  66 */         HttpContinue.sendContinueResponse(exchange, new IoCallback()
/*     */             {
/*     */               public void onComplete(HttpServerExchange exchange, Sender sender) {
/*  69 */                 exchange.dispatch(HttpContinueAcceptingHandler.this.next);
/*     */               }
/*     */ 
/*     */               
/*     */               public void onException(HttpServerExchange exchange, Sender sender, IOException exception) {
/*  74 */                 UndertowLogger.REQUEST_IO_LOGGER.ioException(exception);
/*  75 */                 exchange.endExchange();
/*     */               }
/*     */             });
/*     */       } else {
/*     */         
/*  80 */         HttpContinue.rejectExchange(exchange);
/*     */       } 
/*     */     } else {
/*  83 */       this.next.handleRequest(exchange);
/*     */     } 
/*     */   }
/*     */   
/*     */   public static final class Wrapper
/*     */     implements HandlerWrapper {
/*     */     private final Predicate predicate;
/*     */     
/*     */     public Wrapper(Predicate predicate) {
/*  92 */       this.predicate = predicate;
/*     */     }
/*     */ 
/*     */     
/*     */     public HttpHandler wrap(HttpHandler handler) {
/*  97 */       return new HttpContinueAcceptingHandler(handler, this.predicate);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 103 */     return "http-continue-accept()";
/*     */   }
/*     */   
/*     */   public static class Builder
/*     */     implements HandlerBuilder
/*     */   {
/*     */     public String name() {
/* 110 */       return "http-continue-accept";
/*     */     }
/*     */ 
/*     */     
/*     */     public Map<String, Class<?>> parameters() {
/* 115 */       return Collections.emptyMap();
/*     */     }
/*     */ 
/*     */     
/*     */     public Set<String> requiredParameters() {
/* 120 */       return null;
/*     */     }
/*     */ 
/*     */     
/*     */     public String defaultParameter() {
/* 125 */       return null;
/*     */     }
/*     */ 
/*     */     
/*     */     public HandlerWrapper build(Map<String, Object> config) {
/* 130 */       return new HttpContinueAcceptingHandler.Wrapper(Predicates.truePredicate());
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\handlers\HttpContinueAcceptingHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */