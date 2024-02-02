/*    */ package io.undertow.server.handlers;
/*    */ 
/*    */ import io.undertow.predicate.Predicate;
/*    */ import io.undertow.server.HttpHandler;
/*    */ import io.undertow.server.HttpServerExchange;
/*    */ import java.util.TreeMap;
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
/*    */ public class PredicateContextHandler
/*    */   implements HttpHandler
/*    */ {
/*    */   private final HttpHandler next;
/*    */   
/*    */   public PredicateContextHandler(HttpHandler next) {
/* 37 */     this.next = next;
/*    */   }
/*    */ 
/*    */   
/*    */   public void handleRequest(HttpServerExchange exchange) throws Exception {
/* 42 */     exchange.putAttachment(Predicate.PREDICATE_CONTEXT, new TreeMap<>());
/* 43 */     this.next.handleRequest(exchange);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\handlers\PredicateContextHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */