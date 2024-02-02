/*     */ package io.undertow.security.impl;
/*     */ 
/*     */ import io.undertow.UndertowMessages;
/*     */ import io.undertow.security.api.AuthenticationMechanism;
/*     */ import io.undertow.security.api.AuthenticationMechanismFactory;
/*     */ import io.undertow.security.api.SecurityContext;
/*     */ import io.undertow.security.idm.Account;
/*     */ import io.undertow.security.idm.Credential;
/*     */ import io.undertow.security.idm.IdentityManager;
/*     */ import io.undertow.security.idm.PasswordCredential;
/*     */ import io.undertow.server.HttpServerExchange;
/*     */ import io.undertow.server.handlers.Cookie;
/*     */ import io.undertow.server.handlers.form.FormParserFactory;
/*     */ import io.undertow.util.HttpString;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class GenericHeaderAuthenticationMechanism
/*     */   implements AuthenticationMechanism
/*     */ {
/*  50 */   public static final AuthenticationMechanismFactory FACTORY = new Factory();
/*     */   
/*     */   public static final String NAME = "GENERIC_HEADER";
/*     */   
/*     */   public static final String IDENTITY_HEADER = "identity-header";
/*     */   public static final String SESSION_HEADER = "session-header";
/*     */   private final String mechanismName;
/*     */   private final List<HttpString> identityHeaders;
/*     */   private final List<String> sessionCookieNames;
/*     */   private final IdentityManager identityManager;
/*     */   
/*     */   public GenericHeaderAuthenticationMechanism(String mechanismName, List<HttpString> identityHeaders, List<String> sessionCookieNames, IdentityManager identityManager) {
/*  62 */     this.mechanismName = mechanismName;
/*  63 */     this.identityHeaders = identityHeaders;
/*  64 */     this.sessionCookieNames = sessionCookieNames;
/*  65 */     this.identityManager = identityManager;
/*     */   }
/*     */ 
/*     */   
/*     */   public AuthenticationMechanism.AuthenticationMechanismOutcome authenticate(HttpServerExchange exchange, SecurityContext securityContext) {
/*  70 */     String principal = getPrincipal(exchange);
/*  71 */     if (principal == null) {
/*  72 */       return AuthenticationMechanism.AuthenticationMechanismOutcome.NOT_ATTEMPTED;
/*     */     }
/*  74 */     String session = getSession(exchange);
/*  75 */     if (session == null) {
/*  76 */       return AuthenticationMechanism.AuthenticationMechanismOutcome.NOT_ATTEMPTED;
/*     */     }
/*  78 */     Account account = this.identityManager.verify(principal, (Credential)new PasswordCredential(session.toCharArray()));
/*  79 */     if (account == null) {
/*  80 */       securityContext.authenticationFailed(UndertowMessages.MESSAGES.authenticationFailed(principal), this.mechanismName);
/*  81 */       return AuthenticationMechanism.AuthenticationMechanismOutcome.NOT_AUTHENTICATED;
/*     */     } 
/*  83 */     securityContext.authenticationComplete(account, this.mechanismName, false);
/*  84 */     return AuthenticationMechanism.AuthenticationMechanismOutcome.AUTHENTICATED;
/*     */   }
/*     */   
/*     */   private String getSession(HttpServerExchange exchange) {
/*  88 */     for (String header : this.sessionCookieNames) {
/*  89 */       for (Cookie cookie : exchange.requestCookies()) {
/*  90 */         if (header.equals(cookie.getName())) {
/*  91 */           return cookie.getValue();
/*     */         }
/*     */       } 
/*     */     } 
/*  95 */     return null;
/*     */   }
/*     */   
/*     */   private String getPrincipal(HttpServerExchange exchange) {
/*  99 */     for (HttpString header : this.identityHeaders) {
/* 100 */       String res = exchange.getRequestHeaders().getFirst(header);
/* 101 */       if (res != null) {
/* 102 */         return res;
/*     */       }
/*     */     } 
/* 105 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public AuthenticationMechanism.ChallengeResult sendChallenge(HttpServerExchange exchange, SecurityContext securityContext) {
/* 110 */     return AuthenticationMechanism.ChallengeResult.NOT_SENT;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static class Factory
/*     */     implements AuthenticationMechanismFactory
/*     */   {
/*     */     @Deprecated
/*     */     public Factory(IdentityManager identityManager) {}
/*     */ 
/*     */     
/*     */     public Factory() {}
/*     */ 
/*     */     
/*     */     public AuthenticationMechanism create(String mechanismName, IdentityManager identityManager, FormParserFactory formParserFactory, Map<String, String> properties) {
/* 126 */       String identity = properties.get("identity-header");
/* 127 */       if (identity == null) {
/* 128 */         throw UndertowMessages.MESSAGES.authenticationPropertyNotSet(mechanismName, "identity-header");
/*     */       }
/* 130 */       String session = properties.get("session-header");
/* 131 */       if (session == null) {
/* 132 */         throw UndertowMessages.MESSAGES.authenticationPropertyNotSet(mechanismName, "session-header");
/*     */       }
/* 134 */       List<HttpString> ids = new ArrayList<>();
/* 135 */       for (String s : identity.split(",")) {
/* 136 */         ids.add(new HttpString(s));
/*     */       }
/* 138 */       List<String> sessions = new ArrayList<>();
/* 139 */       for (String s : session.split(",")) {
/* 140 */         sessions.add(s);
/*     */       }
/* 142 */       return new GenericHeaderAuthenticationMechanism(mechanismName, ids, sessions, identityManager);
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\security\impl\GenericHeaderAuthenticationMechanism.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */