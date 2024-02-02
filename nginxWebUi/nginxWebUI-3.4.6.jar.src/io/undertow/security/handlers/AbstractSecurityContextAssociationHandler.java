/*    */ package io.undertow.security.handlers;
/*    */ 
/*    */ import io.undertow.security.api.SecurityContext;
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
/*    */ 
/*    */ public abstract class AbstractSecurityContextAssociationHandler
/*    */   implements HttpHandler
/*    */ {
/*    */   private final HttpHandler next;
/*    */   
/*    */   protected AbstractSecurityContextAssociationHandler(HttpHandler next) {
/* 34 */     this.next = next;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void handleRequest(HttpServerExchange exchange) throws Exception {
/* 42 */     SecurityActions.setSecurityContext(exchange, createSecurityContext(exchange));
/* 43 */     this.next.handleRequest(exchange);
/*    */   }
/*    */   
/*    */   public abstract SecurityContext createSecurityContext(HttpServerExchange paramHttpServerExchange);
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\security\handlers\AbstractSecurityContextAssociationHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */