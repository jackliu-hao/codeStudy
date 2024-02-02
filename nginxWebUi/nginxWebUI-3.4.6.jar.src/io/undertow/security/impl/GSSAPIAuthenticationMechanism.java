/*     */ package io.undertow.security.impl;
/*     */ 
/*     */ import io.undertow.UndertowLogger;
/*     */ import io.undertow.security.api.AuthenticationMechanism;
/*     */ import io.undertow.security.api.GSSAPIServerSubjectFactory;
/*     */ import io.undertow.security.api.SecurityContext;
/*     */ import io.undertow.security.idm.Account;
/*     */ import io.undertow.security.idm.Credential;
/*     */ import io.undertow.security.idm.GSSContextCredential;
/*     */ import io.undertow.security.idm.IdentityManager;
/*     */ import io.undertow.server.HttpServerExchange;
/*     */ import io.undertow.server.ServerConnection;
/*     */ import io.undertow.server.handlers.proxy.ExclusivityChecker;
/*     */ import io.undertow.util.AttachmentKey;
/*     */ import io.undertow.util.FlexBase64;
/*     */ import io.undertow.util.HeaderValues;
/*     */ import io.undertow.util.Headers;
/*     */ import java.io.IOException;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.security.GeneralSecurityException;
/*     */ import java.security.Principal;
/*     */ import java.security.PrivilegedActionException;
/*     */ import java.security.PrivilegedExceptionAction;
/*     */ import javax.security.auth.Subject;
/*     */ import javax.security.auth.kerberos.KerberosPrincipal;
/*     */ import org.ietf.jgss.GSSContext;
/*     */ import org.ietf.jgss.GSSCredential;
/*     */ import org.ietf.jgss.GSSException;
/*     */ import org.ietf.jgss.GSSManager;
/*     */ import org.ietf.jgss.GSSName;
/*     */ import org.ietf.jgss.Oid;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ public class GSSAPIAuthenticationMechanism
/*     */   implements AuthenticationMechanism
/*     */ {
/*  67 */   public static final ExclusivityChecker EXCLUSIVITY_CHECKER = new ExclusivityChecker()
/*     */     {
/*     */       public boolean isExclusivityRequired(HttpServerExchange exchange)
/*     */       {
/*  71 */         HeaderValues headerValues = exchange.getRequestHeaders().get(Headers.AUTHORIZATION);
/*  72 */         if (headerValues != null) {
/*  73 */           for (String current : headerValues) {
/*  74 */             if (current.startsWith(GSSAPIAuthenticationMechanism.NEGOTIATE_PREFIX)) {
/*  75 */               return true;
/*     */             }
/*     */           } 
/*     */         }
/*     */         
/*  80 */         return false;
/*     */       }
/*     */     };
/*     */   
/*  84 */   private static final String NEGOTIATION_PLAIN = Headers.NEGOTIATE.toString();
/*  85 */   private static final String NEGOTIATE_PREFIX = Headers.NEGOTIATE + " ";
/*     */   private static final Oid[] DEFAULT_MECHANISMS;
/*     */   private static final String name = "SPNEGO";
/*     */   
/*     */   static {
/*     */     try {
/*  91 */       Oid spnego = new Oid("1.3.6.1.5.5.2");
/*  92 */       Oid kerberos = new Oid("1.2.840.113554.1.2.2");
/*  93 */       DEFAULT_MECHANISMS = new Oid[] { spnego, kerberos };
/*  94 */     } catch (GSSException e) {
/*  95 */       throw new RuntimeException(e);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private final IdentityManager identityManager;
/*     */   private final GSSAPIServerSubjectFactory subjectFactory;
/*     */   private final Oid[] mechanisms;
/*     */   
/*     */   public GSSAPIAuthenticationMechanism(GSSAPIServerSubjectFactory subjectFactory, IdentityManager identityManager, Oid... supportedMechanisms) {
/* 105 */     this.subjectFactory = subjectFactory;
/* 106 */     this.identityManager = identityManager;
/* 107 */     this.mechanisms = supportedMechanisms;
/*     */   }
/*     */   
/*     */   public GSSAPIAuthenticationMechanism(GSSAPIServerSubjectFactory subjectFactory, Oid... supportedMechanisms) {
/* 111 */     this(subjectFactory, null, supportedMechanisms);
/*     */   }
/*     */   
/*     */   public GSSAPIAuthenticationMechanism(GSSAPIServerSubjectFactory subjectFactory) {
/* 115 */     this(subjectFactory, DEFAULT_MECHANISMS);
/*     */   }
/*     */ 
/*     */   
/*     */   private IdentityManager getIdentityManager(SecurityContext securityContext) {
/* 120 */     return (this.identityManager != null) ? this.identityManager : securityContext.getIdentityManager();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public AuthenticationMechanism.AuthenticationMechanismOutcome authenticate(HttpServerExchange exchange, SecurityContext securityContext) {
/* 126 */     ServerConnection connection = exchange.getConnection();
/* 127 */     NegotiationContext negContext = (NegotiationContext)connection.getAttachment(NegotiationContext.ATTACHMENT_KEY);
/* 128 */     if (negContext != null) {
/*     */       
/* 130 */       UndertowLogger.SECURITY_LOGGER.debugf("Existing negotiation context found for %s", exchange);
/* 131 */       exchange.putAttachment(NegotiationContext.ATTACHMENT_KEY, negContext);
/* 132 */       if (negContext.isEstablished()) {
/* 133 */         IdentityManager identityManager = getIdentityManager(securityContext);
/* 134 */         Account account = identityManager.verify((Credential)new GSSContextCredential(negContext.getGssContext()));
/* 135 */         if (account != null) {
/* 136 */           securityContext.authenticationComplete(account, "SPNEGO", false);
/* 137 */           UndertowLogger.SECURITY_LOGGER.debugf("Authenticated as user %s with existing GSSAPI negotiation context for %s", account.getPrincipal().getName(), exchange);
/* 138 */           return AuthenticationMechanism.AuthenticationMechanismOutcome.AUTHENTICATED;
/*     */         } 
/* 140 */         UndertowLogger.SECURITY_LOGGER.debugf("Failed to authenticate with existing GSSAPI negotiation context for %s", exchange);
/* 141 */         return AuthenticationMechanism.AuthenticationMechanismOutcome.NOT_AUTHENTICATED;
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/* 146 */     HeaderValues headerValues = exchange.getRequestHeaders().get(Headers.AUTHORIZATION);
/* 147 */     if (headerValues != null) {
/* 148 */       for (String current : headerValues) {
/* 149 */         if (current.startsWith(NEGOTIATE_PREFIX)) {
/* 150 */           String base64Challenge = current.substring(NEGOTIATE_PREFIX.length());
/*     */           try {
/* 152 */             ByteBuffer challenge = FlexBase64.decode(base64Challenge);
/* 153 */             return runGSSAPI(exchange, challenge, securityContext);
/* 154 */           } catch (IOException iOException) {
/*     */ 
/*     */ 
/*     */ 
/*     */             
/* 159 */             return AuthenticationMechanism.AuthenticationMechanismOutcome.NOT_AUTHENTICATED;
/*     */           } 
/*     */         } 
/*     */       } 
/*     */     }
/*     */     
/* 165 */     return AuthenticationMechanism.AuthenticationMechanismOutcome.NOT_ATTEMPTED;
/*     */   }
/*     */   
/*     */   public AuthenticationMechanism.ChallengeResult sendChallenge(HttpServerExchange exchange, SecurityContext securityContext) {
/* 169 */     NegotiationContext negContext = (NegotiationContext)exchange.getAttachment(NegotiationContext.ATTACHMENT_KEY);
/*     */     
/* 171 */     String header = NEGOTIATION_PLAIN;
/*     */     
/* 173 */     if (negContext != null) {
/* 174 */       byte[] responseChallenge = negContext.useResponseToken();
/* 175 */       exchange.putAttachment(NegotiationContext.ATTACHMENT_KEY, null);
/* 176 */       if (responseChallenge != null) {
/* 177 */         header = NEGOTIATE_PREFIX + FlexBase64.encodeString(responseChallenge, false);
/*     */       }
/*     */     } else {
/* 180 */       Subject server = null;
/*     */       try {
/* 182 */         server = this.subjectFactory.getSubjectForHost(getHostName(exchange));
/* 183 */       } catch (GeneralSecurityException generalSecurityException) {}
/*     */ 
/*     */       
/* 186 */       if (server == null) {
/* 187 */         return AuthenticationMechanism.ChallengeResult.NOT_SENT;
/*     */       }
/*     */     } 
/*     */     
/* 191 */     exchange.getResponseHeaders().add(Headers.WWW_AUTHENTICATE, header);
/*     */     
/* 193 */     UndertowLogger.SECURITY_LOGGER.debugf("Sending GSSAPI challenge for %s", exchange);
/* 194 */     return new AuthenticationMechanism.ChallengeResult(true, Integer.valueOf(401));
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public AuthenticationMechanism.AuthenticationMechanismOutcome runGSSAPI(HttpServerExchange exchange, ByteBuffer challenge, SecurityContext securityContext) {
/*     */     try {
/* 201 */       Subject server = this.subjectFactory.getSubjectForHost(getHostName(exchange));
/*     */       
/* 203 */       return Subject.<AuthenticationMechanism.AuthenticationMechanismOutcome>doAs(server, new AcceptSecurityContext(exchange, challenge, securityContext));
/* 204 */     } catch (GeneralSecurityException e) {
/* 205 */       UndertowLogger.SECURITY_LOGGER.failedToObtainSubject(exchange, e);
/* 206 */       return AuthenticationMechanism.AuthenticationMechanismOutcome.NOT_AUTHENTICATED;
/* 207 */     } catch (PrivilegedActionException e) {
/* 208 */       UndertowLogger.SECURITY_LOGGER.failedToNegotiateAtGSSAPI(exchange, e.getCause());
/* 209 */       return AuthenticationMechanism.AuthenticationMechanismOutcome.NOT_AUTHENTICATED;
/*     */     } 
/*     */   }
/*     */   
/*     */   private String getHostName(HttpServerExchange exchange) {
/* 214 */     String hostName = exchange.getRequestHeaders().getFirst(Headers.HOST);
/* 215 */     if (hostName != null) {
/* 216 */       if (hostName.startsWith("[") && hostName.contains("]")) {
/* 217 */         hostName = hostName.substring(0, hostName.indexOf(']') + 1);
/* 218 */       } else if (hostName.contains(":")) {
/* 219 */         hostName = hostName.substring(0, hostName.indexOf(":"));
/*     */       } 
/* 221 */       return hostName;
/*     */     } 
/*     */     
/* 224 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   private class AcceptSecurityContext
/*     */     implements PrivilegedExceptionAction<AuthenticationMechanism.AuthenticationMechanismOutcome>
/*     */   {
/*     */     private final HttpServerExchange exchange;
/*     */     private final ByteBuffer challenge;
/*     */     private final SecurityContext securityContext;
/*     */     
/*     */     private AcceptSecurityContext(HttpServerExchange exchange, ByteBuffer challenge, SecurityContext securityContext) {
/* 236 */       this.exchange = exchange;
/* 237 */       this.challenge = challenge;
/* 238 */       this.securityContext = securityContext;
/*     */     }
/*     */     
/*     */     public AuthenticationMechanism.AuthenticationMechanismOutcome run() throws GSSException {
/* 242 */       GSSAPIAuthenticationMechanism.NegotiationContext negContext = (GSSAPIAuthenticationMechanism.NegotiationContext)this.exchange.getAttachment(GSSAPIAuthenticationMechanism.NegotiationContext.ATTACHMENT_KEY);
/* 243 */       if (negContext == null) {
/* 244 */         negContext = new GSSAPIAuthenticationMechanism.NegotiationContext();
/* 245 */         this.exchange.putAttachment(GSSAPIAuthenticationMechanism.NegotiationContext.ATTACHMENT_KEY, negContext);
/*     */         
/* 247 */         this.exchange.getConnection().putAttachment(GSSAPIAuthenticationMechanism.NegotiationContext.ATTACHMENT_KEY, negContext);
/*     */       } 
/*     */       
/* 250 */       GSSContext gssContext = negContext.getGssContext();
/* 251 */       if (gssContext == null) {
/* 252 */         GSSManager manager = GSSManager.getInstance();
/*     */         
/* 254 */         GSSCredential credential = manager.createCredential((GSSName)null, 2147483647, GSSAPIAuthenticationMechanism.this.mechanisms, 2);
/*     */         
/* 256 */         gssContext = manager.createContext(credential);
/*     */         
/* 258 */         negContext.setGssContext(gssContext);
/*     */       } 
/*     */       
/* 261 */       byte[] respToken = gssContext.acceptSecContext(this.challenge.array(), this.challenge.arrayOffset(), this.challenge.limit());
/* 262 */       negContext.setResponseToken(respToken);
/*     */       
/* 264 */       if (negContext.isEstablished()) {
/*     */         
/* 266 */         if (respToken != null)
/*     */         {
/* 268 */           this.exchange.getResponseHeaders().add(Headers.WWW_AUTHENTICATE, GSSAPIAuthenticationMechanism
/* 269 */               .NEGOTIATE_PREFIX + FlexBase64.encodeString(respToken, false));
/*     */         }
/* 271 */         IdentityManager identityManager = this.securityContext.getIdentityManager();
/* 272 */         Account account = identityManager.verify((Credential)new GSSContextCredential(negContext.getGssContext()));
/* 273 */         if (account != null) {
/* 274 */           this.securityContext.authenticationComplete(account, "SPNEGO", false);
/* 275 */           return AuthenticationMechanism.AuthenticationMechanismOutcome.AUTHENTICATED;
/*     */         } 
/* 277 */         return AuthenticationMechanism.AuthenticationMechanismOutcome.NOT_AUTHENTICATED;
/*     */       } 
/*     */ 
/*     */       
/* 281 */       return AuthenticationMechanism.AuthenticationMechanismOutcome.NOT_AUTHENTICATED;
/*     */     }
/*     */   }
/*     */   
/*     */   private static class NegotiationContext {
/*     */     private NegotiationContext() {}
/*     */     
/* 288 */     static final AttachmentKey<NegotiationContext> ATTACHMENT_KEY = AttachmentKey.create(NegotiationContext.class);
/*     */     
/*     */     private GSSContext gssContext;
/*     */     private byte[] responseToken;
/*     */     private Principal principal;
/*     */     
/*     */     GSSContext getGssContext() {
/* 295 */       return this.gssContext;
/*     */     }
/*     */     
/*     */     void setGssContext(GSSContext gssContext) {
/* 299 */       this.gssContext = gssContext;
/*     */     }
/*     */ 
/*     */     
/*     */     byte[] useResponseToken() {
/*     */       try {
/* 305 */         return this.responseToken;
/*     */       } finally {
/* 307 */         this.responseToken = null;
/*     */       } 
/*     */     }
/*     */     
/*     */     void setResponseToken(byte[] responseToken) {
/* 312 */       this.responseToken = responseToken;
/*     */     }
/*     */     
/*     */     boolean isEstablished() {
/* 316 */       return (this.gssContext != null) ? this.gssContext.isEstablished() : false;
/*     */     }
/*     */     
/*     */     Principal getPrincipal() {
/* 320 */       if (!isEstablished()) {
/* 321 */         throw new IllegalStateException("No established GSSContext to use for the Principal.");
/*     */       }
/*     */       
/* 324 */       if (this.principal == null) {
/*     */         try {
/* 326 */           this.principal = new KerberosPrincipal(this.gssContext.getSrcName().toString());
/* 327 */         } catch (GSSException e) {
/* 328 */           throw new IllegalStateException("Unable to create Principal", e);
/*     */         } 
/*     */       }
/*     */ 
/*     */       
/* 333 */       return this.principal;
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\security\impl\GSSAPIAuthenticationMechanism.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */