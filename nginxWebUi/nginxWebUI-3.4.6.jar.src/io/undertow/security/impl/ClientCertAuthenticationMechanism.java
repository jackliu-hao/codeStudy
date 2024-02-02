/*     */ package io.undertow.security.impl;
/*     */ 
/*     */ import io.undertow.security.api.AuthenticationMechanism;
/*     */ import io.undertow.security.api.AuthenticationMechanismFactory;
/*     */ import io.undertow.security.api.SecurityContext;
/*     */ import io.undertow.security.idm.Account;
/*     */ import io.undertow.security.idm.Credential;
/*     */ import io.undertow.security.idm.IdentityManager;
/*     */ import io.undertow.security.idm.X509CertificateCredential;
/*     */ import io.undertow.server.HttpServerExchange;
/*     */ import io.undertow.server.RenegotiationRequiredException;
/*     */ import io.undertow.server.SSLSessionInfo;
/*     */ import io.undertow.server.handlers.form.FormParserFactory;
/*     */ import java.io.IOException;
/*     */ import java.security.cert.Certificate;
/*     */ import java.security.cert.X509Certificate;
/*     */ import java.util.Map;
/*     */ import javax.net.ssl.SSLPeerUnverifiedException;
/*     */ import org.xnio.SslClientAuthMode;
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
/*     */ public class ClientCertAuthenticationMechanism
/*     */   implements AuthenticationMechanism
/*     */ {
/*  51 */   public static final AuthenticationMechanismFactory FACTORY = new Factory();
/*     */ 
/*     */   
/*     */   public static final String FORCE_RENEGOTIATION = "force_renegotiation";
/*     */   
/*     */   private final String name;
/*     */   
/*     */   private final IdentityManager identityManager;
/*     */   
/*     */   private final boolean forceRenegotiation;
/*     */ 
/*     */   
/*     */   public ClientCertAuthenticationMechanism() {
/*  64 */     this(true);
/*     */   }
/*     */   
/*     */   public ClientCertAuthenticationMechanism(boolean forceRenegotiation) {
/*  68 */     this("CLIENT_CERT", forceRenegotiation);
/*     */   }
/*     */   
/*     */   public ClientCertAuthenticationMechanism(String mechanismName) {
/*  72 */     this(mechanismName, true);
/*     */   }
/*     */   
/*     */   public ClientCertAuthenticationMechanism(String mechanismName, boolean forceRenegotiation) {
/*  76 */     this(mechanismName, forceRenegotiation, null);
/*     */   }
/*     */   
/*     */   public ClientCertAuthenticationMechanism(String mechanismName, boolean forceRenegotiation, IdentityManager identityManager) {
/*  80 */     this.name = mechanismName;
/*  81 */     this.forceRenegotiation = forceRenegotiation;
/*  82 */     this.identityManager = identityManager;
/*     */   }
/*     */ 
/*     */   
/*     */   private IdentityManager getIdentityManager(SecurityContext securityContext) {
/*  87 */     return (this.identityManager != null) ? this.identityManager : securityContext.getIdentityManager();
/*     */   }
/*     */   
/*     */   public AuthenticationMechanism.AuthenticationMechanismOutcome authenticate(HttpServerExchange exchange, SecurityContext securityContext) {
/*  91 */     SSLSessionInfo sslSession = exchange.getConnection().getSslSessionInfo();
/*  92 */     if (sslSession != null) {
/*     */       try {
/*  94 */         Certificate[] clientCerts = getPeerCertificates(exchange, sslSession, securityContext);
/*  95 */         if (clientCerts[0] instanceof X509Certificate) {
/*  96 */           X509CertificateCredential x509CertificateCredential = new X509CertificateCredential((X509Certificate)clientCerts[0]);
/*     */           
/*  98 */           IdentityManager idm = getIdentityManager(securityContext);
/*  99 */           Account account = idm.verify((Credential)x509CertificateCredential);
/* 100 */           if (account != null) {
/* 101 */             securityContext.authenticationComplete(account, this.name, false);
/* 102 */             return AuthenticationMechanism.AuthenticationMechanismOutcome.AUTHENTICATED;
/*     */           } 
/*     */         } 
/* 105 */       } catch (SSLPeerUnverifiedException sSLPeerUnverifiedException) {}
/*     */     }
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
/* 117 */     return AuthenticationMechanism.AuthenticationMechanismOutcome.NOT_ATTEMPTED;
/*     */   }
/*     */   
/*     */   private Certificate[] getPeerCertificates(HttpServerExchange exchange, SSLSessionInfo sslSession, SecurityContext securityContext) throws SSLPeerUnverifiedException {
/*     */     try {
/* 122 */       return sslSession.getPeerCertificates();
/* 123 */     } catch (RenegotiationRequiredException e) {
/*     */       
/* 125 */       if (this.forceRenegotiation && securityContext.isAuthenticationRequired()) {
/*     */         try {
/* 127 */           sslSession.renegotiate(exchange, SslClientAuthMode.REQUESTED);
/* 128 */           return sslSession.getPeerCertificates();
/*     */         }
/* 130 */         catch (IOException iOException) {
/*     */         
/* 132 */         } catch (RenegotiationRequiredException renegotiationRequiredException) {}
/*     */       }
/*     */ 
/*     */ 
/*     */       
/* 137 */       throw new SSLPeerUnverifiedException("");
/*     */     } 
/*     */   }
/*     */   
/*     */   public AuthenticationMechanism.ChallengeResult sendChallenge(HttpServerExchange exchange, SecurityContext securityContext) {
/* 142 */     return AuthenticationMechanism.ChallengeResult.NOT_SENT;
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
/* 154 */       String forceRenegotiation = properties.get("force_renegotiation");
/* 155 */       return new ClientCertAuthenticationMechanism(mechanismName, (forceRenegotiation == null) ? true : "true".equals(forceRenegotiation), identityManager);
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\security\impl\ClientCertAuthenticationMechanism.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */