/*    */ package io.undertow.server.handlers;
/*    */ 
/*    */ import io.undertow.predicate.Predicate;
/*    */ import io.undertow.server.HttpHandler;
/*    */ import io.undertow.server.HttpServerExchange;
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
/*    */ public class PredicateHandler
/*    */   implements HttpHandler
/*    */ {
/*    */   private volatile Predicate predicate;
/*    */   private volatile HttpHandler trueHandler;
/*    */   private volatile HttpHandler falseHandler;
/*    */   
/*    */   public PredicateHandler(Predicate predicate, HttpHandler trueHandler, HttpHandler falseHandler) {
/* 35 */     this.predicate = predicate;
/* 36 */     this.trueHandler = trueHandler;
/* 37 */     this.falseHandler = falseHandler;
/*    */   }
/*    */ 
/*    */   
/*    */   public void handleRequest(HttpServerExchange exchange) throws Exception {
/* 42 */     HttpHandler next = this.predicate.resolve(exchange) ? this.trueHandler : this.falseHandler;
/* 43 */     next.handleRequest(exchange);
/*    */   }
/*    */   
/*    */   public Predicate getPredicate() {
/* 47 */     return this.predicate;
/*    */   }
/*    */   
/*    */   public PredicateHandler setPredicate(Predicate predicate) {
/* 51 */     this.predicate = predicate;
/* 52 */     return this;
/*    */   }
/*    */   
/*    */   public HttpHandler getTrueHandler() {
/* 56 */     return this.trueHandler;
/*    */   }
/*    */   
/*    */   public PredicateHandler setTrueHandler(HttpHandler trueHandler) {
/* 60 */     this.trueHandler = trueHandler;
/* 61 */     return this;
/*    */   }
/*    */   
/*    */   public HttpHandler getFalseHandler() {
/* 65 */     return this.falseHandler;
/*    */   }
/*    */   
/*    */   public PredicateHandler setFalseHandler(HttpHandler falseHandler) {
/* 69 */     this.falseHandler = falseHandler;
/* 70 */     return this;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\handlers\PredicateHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */