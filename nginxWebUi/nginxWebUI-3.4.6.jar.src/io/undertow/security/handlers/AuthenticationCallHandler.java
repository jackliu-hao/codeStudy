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
/*    */ 
/*    */ public class AuthenticationCallHandler
/*    */   implements HttpHandler
/*    */ {
/*    */   private final HttpHandler next;
/*    */   
/*    */   public AuthenticationCallHandler(HttpHandler next) {
/* 35 */     this.next = next;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void handleRequest(HttpServerExchange exchange) throws Exception {
/* 45 */     if (exchange.isInIoThread()) {
/* 46 */       exchange.dispatch(this);
/*    */       return;
/*    */     } 
/* 49 */     SecurityContext context = exchange.getSecurityContext();
/* 50 */     if (context.authenticate()) {
/* 51 */       if (!exchange.isComplete()) {
/* 52 */         this.next.handleRequest(exchange);
/*    */       }
/*    */     } else {
/* 55 */       exchange.endExchange();
/*    */     } 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\security\handlers\AuthenticationCallHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */