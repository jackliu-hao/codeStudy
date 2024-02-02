/*    */ package io.undertow.security.impl;
/*    */ 
/*    */ import io.undertow.security.api.AuthenticationMode;
/*    */ import io.undertow.security.api.SecurityContext;
/*    */ import io.undertow.security.api.SecurityContextFactory;
/*    */ import io.undertow.security.idm.IdentityManager;
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
/*    */ public class SecurityContextFactoryImpl
/*    */   implements SecurityContextFactory
/*    */ {
/* 37 */   public static final SecurityContextFactory INSTANCE = new SecurityContextFactoryImpl();
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public SecurityContext createSecurityContext(HttpServerExchange exchange, AuthenticationMode mode, IdentityManager identityManager, String programmaticMechName) {
/* 46 */     SecurityContextImpl securityContext = SecurityActions.createSecurityContextImpl(exchange, mode, identityManager);
/* 47 */     if (programmaticMechName != null)
/* 48 */       securityContext.setProgramaticMechName(programmaticMechName); 
/* 49 */     return securityContext;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\security\impl\SecurityContextFactoryImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */