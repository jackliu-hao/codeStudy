/*    */ package io.undertow.security.handlers;
/*    */ 
/*    */ import io.undertow.security.api.SecurityContext;
/*    */ import io.undertow.server.HttpServerExchange;
/*    */ import java.security.AccessController;
/*    */ import java.security.PrivilegedAction;
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
/*    */ class SecurityActions
/*    */ {
/*    */   static void setSecurityContext(final HttpServerExchange exchange, final SecurityContext securityContext) {
/* 28 */     if (System.getSecurityManager() == null) {
/* 29 */       exchange.setSecurityContext(securityContext);
/*    */     } else {
/* 31 */       AccessController.doPrivileged(new PrivilegedAction()
/*    */           {
/*    */             public Object run() {
/* 34 */               exchange.setSecurityContext(securityContext);
/* 35 */               return null;
/*    */             }
/*    */           });
/*    */     } 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\security\handlers\SecurityActions.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */