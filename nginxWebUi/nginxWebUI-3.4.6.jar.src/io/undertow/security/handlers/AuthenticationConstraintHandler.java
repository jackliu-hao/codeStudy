/*    */ package io.undertow.security.handlers;
/*    */ 
/*    */ import io.undertow.UndertowLogger;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ public class AuthenticationConstraintHandler
/*    */   implements HttpHandler
/*    */ {
/*    */   private final HttpHandler next;
/*    */   
/*    */   public AuthenticationConstraintHandler(HttpHandler next) {
/* 39 */     this.next = next;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void handleRequest(HttpServerExchange exchange) throws Exception {
/* 47 */     if (isAuthenticationRequired(exchange)) {
/* 48 */       SecurityContext context = exchange.getSecurityContext();
/* 49 */       UndertowLogger.SECURITY_LOGGER.debugf("Setting authentication required for exchange %s", exchange);
/* 50 */       context.setAuthenticationRequired();
/*    */     } 
/*    */     
/* 53 */     this.next.handleRequest(exchange);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   protected boolean isAuthenticationRequired(HttpServerExchange exchange) {
/* 65 */     return true;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\security\handlers\AuthenticationConstraintHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */