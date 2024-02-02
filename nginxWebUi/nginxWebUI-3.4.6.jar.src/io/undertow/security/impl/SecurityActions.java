/*    */ package io.undertow.security.impl;
/*    */ 
/*    */ import io.undertow.security.api.AuthenticationMode;
/*    */ import io.undertow.security.idm.IdentityManager;
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
/*    */   static SecurityContextImpl createSecurityContextImpl(final HttpServerExchange exchange, final AuthenticationMode authenticationMode, final IdentityManager identityManager) {
/* 29 */     if (System.getSecurityManager() == null) {
/* 30 */       return new SecurityContextImpl(exchange, authenticationMode, identityManager);
/*    */     }
/* 32 */     return AccessController.<SecurityContextImpl>doPrivileged(new PrivilegedAction<SecurityContextImpl>()
/*    */         {
/*    */           public SecurityContextImpl run() {
/* 35 */             return new SecurityContextImpl(exchange, authenticationMode, identityManager);
/*    */           }
/*    */         });
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\security\impl\SecurityActions.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */