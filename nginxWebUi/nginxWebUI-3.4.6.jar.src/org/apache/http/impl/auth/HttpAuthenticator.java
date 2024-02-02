/*     */ package org.apache.http.impl.auth;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.util.Locale;
/*     */ import java.util.Map;
/*     */ import java.util.Queue;
/*     */ import org.apache.commons.logging.Log;
/*     */ import org.apache.commons.logging.LogFactory;
/*     */ import org.apache.http.Header;
/*     */ import org.apache.http.HttpException;
/*     */ import org.apache.http.HttpHost;
/*     */ import org.apache.http.HttpRequest;
/*     */ import org.apache.http.HttpResponse;
/*     */ import org.apache.http.auth.AuthOption;
/*     */ import org.apache.http.auth.AuthProtocolState;
/*     */ import org.apache.http.auth.AuthScheme;
/*     */ import org.apache.http.auth.AuthState;
/*     */ import org.apache.http.auth.AuthenticationException;
/*     */ import org.apache.http.auth.ContextAwareAuthScheme;
/*     */ import org.apache.http.auth.Credentials;
/*     */ import org.apache.http.auth.MalformedChallengeException;
/*     */ import org.apache.http.client.AuthenticationStrategy;
/*     */ import org.apache.http.protocol.HttpContext;
/*     */ import org.apache.http.util.Asserts;
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
/*     */ 
/*     */ public class HttpAuthenticator
/*     */ {
/*     */   private final Log log;
/*     */   
/*     */   public HttpAuthenticator(Log log) {
/*  63 */     this.log = (log != null) ? log : LogFactory.getLog(getClass());
/*     */   }
/*     */   
/*     */   public HttpAuthenticator() {
/*  67 */     this(null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isAuthenticationRequested(HttpHost host, HttpResponse response, AuthenticationStrategy authStrategy, AuthState authState, HttpContext context) {
/*  76 */     if (authStrategy.isAuthenticationRequested(host, response, context)) {
/*  77 */       this.log.debug("Authentication required");
/*  78 */       if (authState.getState() == AuthProtocolState.SUCCESS) {
/*  79 */         authStrategy.authFailed(host, authState.getAuthScheme(), context);
/*     */       }
/*  81 */       return true;
/*     */     } 
/*  83 */     switch (authState.getState()) {
/*     */       case CHALLENGED:
/*     */       case HANDSHAKE:
/*  86 */         this.log.debug("Authentication succeeded");
/*  87 */         authState.setState(AuthProtocolState.SUCCESS);
/*  88 */         authStrategy.authSucceeded(host, authState.getAuthScheme(), context);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/*     */       case SUCCESS:
/*  95 */         return false;
/*     */     } 
/*     */     authState.setState(AuthProtocolState.UNCHALLENGED);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean handleAuthChallenge(HttpHost host, HttpResponse response, AuthenticationStrategy authStrategy, AuthState authState, HttpContext context) {
/*     */     try {
/* 105 */       if (this.log.isDebugEnabled()) {
/* 106 */         this.log.debug(host.toHostString() + " requested authentication");
/*     */       }
/* 108 */       Map<String, Header> challenges = authStrategy.getChallenges(host, response, context);
/* 109 */       if (challenges.isEmpty()) {
/* 110 */         this.log.debug("Response contains no authentication challenges");
/* 111 */         return false;
/*     */       } 
/*     */       
/* 114 */       AuthScheme authScheme = authState.getAuthScheme();
/* 115 */       switch (authState.getState()) {
/*     */         case FAILURE:
/* 117 */           return false;
/*     */         case SUCCESS:
/* 119 */           authState.reset();
/*     */           break;
/*     */         case CHALLENGED:
/*     */         case HANDSHAKE:
/* 123 */           if (authScheme == null) {
/* 124 */             this.log.debug("Auth scheme is null");
/* 125 */             authStrategy.authFailed(host, null, context);
/* 126 */             authState.reset();
/* 127 */             authState.setState(AuthProtocolState.FAILURE);
/* 128 */             return false;
/*     */           } 
/*     */         case UNCHALLENGED:
/* 131 */           if (authScheme != null) {
/* 132 */             String id = authScheme.getSchemeName();
/* 133 */             Header challenge = challenges.get(id.toLowerCase(Locale.ROOT));
/* 134 */             if (challenge != null) {
/* 135 */               this.log.debug("Authorization challenge processed");
/* 136 */               authScheme.processChallenge(challenge);
/* 137 */               if (authScheme.isComplete()) {
/* 138 */                 this.log.debug("Authentication failed");
/* 139 */                 authStrategy.authFailed(host, authState.getAuthScheme(), context);
/* 140 */                 authState.reset();
/* 141 */                 authState.setState(AuthProtocolState.FAILURE);
/* 142 */                 return false;
/*     */               } 
/* 144 */               authState.setState(AuthProtocolState.HANDSHAKE);
/* 145 */               return true;
/*     */             } 
/* 147 */             authState.reset();
/*     */           } 
/*     */           break;
/*     */       } 
/* 151 */       Queue<AuthOption> authOptions = authStrategy.select(challenges, host, response, context);
/* 152 */       if (authOptions != null && !authOptions.isEmpty()) {
/* 153 */         if (this.log.isDebugEnabled()) {
/* 154 */           this.log.debug("Selected authentication options: " + authOptions);
/*     */         }
/* 156 */         authState.setState(AuthProtocolState.CHALLENGED);
/* 157 */         authState.update(authOptions);
/* 158 */         return true;
/*     */       } 
/* 160 */       return false;
/* 161 */     } catch (MalformedChallengeException ex) {
/* 162 */       if (this.log.isWarnEnabled()) {
/* 163 */         this.log.warn("Malformed challenge: " + ex.getMessage());
/*     */       }
/* 165 */       authState.reset();
/* 166 */       return false;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void generateAuthResponse(HttpRequest request, AuthState authState, HttpContext context) throws HttpException, IOException {
/*     */     Queue<AuthOption> authOptions;
/* 174 */     AuthScheme authScheme = authState.getAuthScheme();
/* 175 */     Credentials creds = authState.getCredentials();
/* 176 */     switch (authState.getState()) {
/*     */       case FAILURE:
/*     */         return;
/*     */       case SUCCESS:
/* 180 */         ensureAuthScheme(authScheme);
/* 181 */         if (authScheme.isConnectionBased()) {
/*     */           return;
/*     */         }
/*     */         break;
/*     */       case CHALLENGED:
/* 186 */         authOptions = authState.getAuthOptions();
/* 187 */         if (authOptions != null) {
/* 188 */           while (!authOptions.isEmpty()) {
/* 189 */             AuthOption authOption = authOptions.remove();
/* 190 */             authScheme = authOption.getAuthScheme();
/* 191 */             creds = authOption.getCredentials();
/* 192 */             authState.update(authScheme, creds);
/* 193 */             if (this.log.isDebugEnabled()) {
/* 194 */               this.log.debug("Generating response to an authentication challenge using " + authScheme.getSchemeName() + " scheme");
/*     */             }
/*     */             
/*     */             try {
/* 198 */               Header header = doAuth(authScheme, creds, request, context);
/* 199 */               request.addHeader(header);
/*     */               break;
/* 201 */             } catch (AuthenticationException ex) {
/* 202 */               if (this.log.isWarnEnabled()) {
/* 203 */                 this.log.warn(authScheme + " authentication error: " + ex.getMessage());
/*     */               }
/*     */             } 
/*     */           } 
/*     */           return;
/*     */         } 
/* 209 */         ensureAuthScheme(authScheme); break;
/*     */     } 
/* 211 */     if (authScheme != null) {
/*     */       try {
/* 213 */         Header header = doAuth(authScheme, creds, request, context);
/* 214 */         request.addHeader(header);
/* 215 */       } catch (AuthenticationException ex) {
/* 216 */         if (this.log.isErrorEnabled()) {
/* 217 */           this.log.error(authScheme + " authentication error: " + ex.getMessage());
/*     */         }
/*     */       } 
/*     */     }
/*     */   }
/*     */   
/*     */   private void ensureAuthScheme(AuthScheme authScheme) {
/* 224 */     Asserts.notNull(authScheme, "Auth scheme");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private Header doAuth(AuthScheme authScheme, Credentials creds, HttpRequest request, HttpContext context) throws AuthenticationException {
/* 233 */     return (authScheme instanceof ContextAwareAuthScheme) ? ((ContextAwareAuthScheme)authScheme).authenticate(creds, request, context) : authScheme.authenticate(creds, request);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\impl\auth\HttpAuthenticator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */