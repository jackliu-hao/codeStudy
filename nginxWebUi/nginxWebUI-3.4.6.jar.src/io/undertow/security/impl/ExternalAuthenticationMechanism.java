/*     */ package io.undertow.security.impl;
/*     */ 
/*     */ import io.undertow.security.api.AuthenticationMechanism;
/*     */ import io.undertow.security.api.AuthenticationMechanismFactory;
/*     */ import io.undertow.security.api.SecurityContext;
/*     */ import io.undertow.security.idm.Account;
/*     */ import io.undertow.security.idm.Credential;
/*     */ import io.undertow.security.idm.ExternalCredential;
/*     */ import io.undertow.security.idm.IdentityManager;
/*     */ import io.undertow.server.HttpServerExchange;
/*     */ import io.undertow.server.handlers.form.FormParserFactory;
/*     */ import io.undertow.util.AttachmentKey;
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
/*     */ public class ExternalAuthenticationMechanism
/*     */   implements AuthenticationMechanism
/*     */ {
/*  45 */   public static final AuthenticationMechanismFactory FACTORY = new Factory();
/*     */   
/*     */   public static final String NAME = "EXTERNAL";
/*     */   
/*     */   private final String name;
/*     */   
/*     */   private final IdentityManager identityManager;
/*  52 */   public static final AttachmentKey<String> EXTERNAL_PRINCIPAL = AttachmentKey.create(String.class);
/*  53 */   public static final AttachmentKey<String> EXTERNAL_AUTHENTICATION_TYPE = AttachmentKey.create(String.class);
/*     */   
/*     */   public ExternalAuthenticationMechanism(String name, IdentityManager identityManager) {
/*  56 */     this.name = name;
/*  57 */     this.identityManager = identityManager;
/*     */   }
/*     */   
/*     */   public ExternalAuthenticationMechanism(String name) {
/*  61 */     this(name, null);
/*     */   }
/*     */   public ExternalAuthenticationMechanism() {
/*  64 */     this("EXTERNAL");
/*     */   }
/*     */ 
/*     */   
/*     */   private IdentityManager getIdentityManager(SecurityContext securityContext) {
/*  69 */     return (this.identityManager != null) ? this.identityManager : securityContext.getIdentityManager();
/*     */   }
/*     */ 
/*     */   
/*     */   public AuthenticationMechanism.AuthenticationMechanismOutcome authenticate(HttpServerExchange exchange, SecurityContext securityContext) {
/*  74 */     String principal = (String)exchange.getAttachment(EXTERNAL_PRINCIPAL);
/*  75 */     if (principal == null) {
/*  76 */       return AuthenticationMechanism.AuthenticationMechanismOutcome.NOT_ATTEMPTED;
/*     */     }
/*  78 */     Account account = getIdentityManager(securityContext).verify(principal, (Credential)ExternalCredential.INSTANCE);
/*  79 */     if (account == null) {
/*  80 */       return AuthenticationMechanism.AuthenticationMechanismOutcome.NOT_AUTHENTICATED;
/*     */     }
/*  82 */     String name = (String)exchange.getAttachment(EXTERNAL_AUTHENTICATION_TYPE);
/*  83 */     securityContext.authenticationComplete(account, (name != null) ? name : this.name, false);
/*     */     
/*  85 */     return AuthenticationMechanism.AuthenticationMechanismOutcome.AUTHENTICATED;
/*     */   }
/*     */ 
/*     */   
/*     */   public AuthenticationMechanism.ChallengeResult sendChallenge(HttpServerExchange exchange, SecurityContext securityContext) {
/*  90 */     return AuthenticationMechanism.ChallengeResult.NOT_SENT;
/*     */   }
/*     */   
/*     */   public static final class Factory
/*     */     implements AuthenticationMechanismFactory
/*     */   {
/*     */     @Deprecated
/*     */     public Factory(IdentityManager identityManager) {}
/*     */     
/*     */     public Factory() {}
/*     */     
/*     */     public AuthenticationMechanism create(String mechanismName, IdentityManager identityManager, FormParserFactory formParserFactory, Map<String, String> properties) {
/* 102 */       return new ExternalAuthenticationMechanism(mechanismName, identityManager);
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\security\impl\ExternalAuthenticationMechanism.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */