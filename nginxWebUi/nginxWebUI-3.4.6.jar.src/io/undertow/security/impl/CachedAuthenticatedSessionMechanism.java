/*    */ package io.undertow.security.impl;
/*    */ 
/*    */ import io.undertow.security.api.AuthenticatedSessionManager;
/*    */ import io.undertow.security.api.AuthenticationMechanism;
/*    */ import io.undertow.security.api.SecurityContext;
/*    */ import io.undertow.security.idm.Account;
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
/*    */ public class CachedAuthenticatedSessionMechanism
/*    */   implements AuthenticationMechanism
/*    */ {
/*    */   private final IdentityManager identityManager;
/*    */   
/*    */   public CachedAuthenticatedSessionMechanism() {
/* 38 */     this(null);
/*    */   }
/*    */   
/*    */   public CachedAuthenticatedSessionMechanism(IdentityManager identityManager) {
/* 42 */     this.identityManager = identityManager;
/*    */   }
/*    */ 
/*    */   
/*    */   private IdentityManager getIdentityManager(SecurityContext securityContext) {
/* 47 */     return (this.identityManager != null) ? this.identityManager : securityContext.getIdentityManager();
/*    */   }
/*    */ 
/*    */   
/*    */   public AuthenticationMechanism.AuthenticationMechanismOutcome authenticate(HttpServerExchange exchange, SecurityContext securityContext) {
/* 52 */     AuthenticatedSessionManager sessionManager = (AuthenticatedSessionManager)exchange.getAttachment(AuthenticatedSessionManager.ATTACHMENT_KEY);
/* 53 */     if (sessionManager != null) {
/* 54 */       return runCached(exchange, securityContext, sessionManager);
/*    */     }
/* 56 */     return AuthenticationMechanism.AuthenticationMechanismOutcome.NOT_ATTEMPTED;
/*    */   }
/*    */ 
/*    */   
/*    */   public AuthenticationMechanism.AuthenticationMechanismOutcome runCached(HttpServerExchange exchange, SecurityContext securityContext, AuthenticatedSessionManager sessionManager) {
/* 61 */     AuthenticatedSessionManager.AuthenticatedSession authSession = sessionManager.lookupSession(exchange);
/* 62 */     if (authSession != null) {
/* 63 */       Account account = getIdentityManager(securityContext).verify(authSession.getAccount());
/* 64 */       if (account != null) {
/* 65 */         securityContext.authenticationComplete(account, authSession.getMechanism(), false);
/* 66 */         return AuthenticationMechanism.AuthenticationMechanismOutcome.AUTHENTICATED;
/*    */       } 
/* 68 */       sessionManager.clearSession(exchange);
/*    */ 
/*    */       
/* 71 */       return AuthenticationMechanism.AuthenticationMechanismOutcome.NOT_ATTEMPTED;
/*    */     } 
/*    */ 
/*    */ 
/*    */     
/* 76 */     return AuthenticationMechanism.AuthenticationMechanismOutcome.NOT_ATTEMPTED;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public AuthenticationMechanism.ChallengeResult sendChallenge(HttpServerExchange exchange, SecurityContext securityContext) {
/* 84 */     return AuthenticationMechanism.ChallengeResult.NOT_SENT;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\security\impl\CachedAuthenticatedSessionMechanism.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */