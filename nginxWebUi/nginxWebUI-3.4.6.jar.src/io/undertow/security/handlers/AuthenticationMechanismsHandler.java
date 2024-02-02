/*    */ package io.undertow.security.handlers;
/*    */ 
/*    */ import io.undertow.Handlers;
/*    */ import io.undertow.security.api.AuthenticationMechanism;
/*    */ import io.undertow.security.api.AuthenticationMechanismContext;
/*    */ import io.undertow.security.api.SecurityContext;
/*    */ import io.undertow.server.HttpHandler;
/*    */ import io.undertow.server.HttpServerExchange;
/*    */ import io.undertow.server.handlers.ResponseCodeHandler;
/*    */ import java.util.List;
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
/*    */ public class AuthenticationMechanismsHandler
/*    */   implements HttpHandler
/*    */ {
/* 39 */   private volatile HttpHandler next = (HttpHandler)ResponseCodeHandler.HANDLE_404;
/*    */   private final AuthenticationMechanism[] authenticationMechanisms;
/*    */   
/*    */   public AuthenticationMechanismsHandler(HttpHandler next, List<AuthenticationMechanism> authenticationMechanisms) {
/* 43 */     this.next = next;
/* 44 */     this.authenticationMechanisms = authenticationMechanisms.<AuthenticationMechanism>toArray(new AuthenticationMechanism[authenticationMechanisms.size()]);
/*    */   }
/*    */   
/*    */   public AuthenticationMechanismsHandler(List<AuthenticationMechanism> authenticationHandlers) {
/* 48 */     this.authenticationMechanisms = authenticationHandlers.<AuthenticationMechanism>toArray(new AuthenticationMechanism[authenticationHandlers.size()]);
/*    */   }
/*    */ 
/*    */   
/*    */   public void handleRequest(HttpServerExchange exchange) throws Exception {
/* 53 */     SecurityContext sc = exchange.getSecurityContext();
/* 54 */     if (sc != null && sc instanceof AuthenticationMechanismContext) {
/* 55 */       AuthenticationMechanismContext amc = (AuthenticationMechanismContext)sc;
/* 56 */       for (int i = 0; i < this.authenticationMechanisms.length; i++) {
/* 57 */         amc.addAuthenticationMechanism(this.authenticationMechanisms[i]);
/*    */       }
/*    */     } 
/* 60 */     this.next.handleRequest(exchange);
/*    */   }
/*    */   
/*    */   public HttpHandler getNext() {
/* 64 */     return this.next;
/*    */   }
/*    */   
/*    */   public AuthenticationMechanismsHandler setNext(HttpHandler next) {
/* 68 */     Handlers.handlerNotNull(next);
/* 69 */     this.next = next;
/* 70 */     return this;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\security\handlers\AuthenticationMechanismsHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */