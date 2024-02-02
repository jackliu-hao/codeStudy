/*     */ package org.apache.http.impl.client;
/*     */ 
/*     */ import java.util.LinkedList;
/*     */ import java.util.Locale;
/*     */ import java.util.Map;
/*     */ import java.util.Queue;
/*     */ import org.apache.commons.logging.Log;
/*     */ import org.apache.commons.logging.LogFactory;
/*     */ import org.apache.http.Header;
/*     */ import org.apache.http.HttpHost;
/*     */ import org.apache.http.HttpResponse;
/*     */ import org.apache.http.annotation.Contract;
/*     */ import org.apache.http.annotation.ThreadingBehavior;
/*     */ import org.apache.http.auth.AuthOption;
/*     */ import org.apache.http.auth.AuthScheme;
/*     */ import org.apache.http.auth.AuthScope;
/*     */ import org.apache.http.auth.AuthenticationException;
/*     */ import org.apache.http.auth.Credentials;
/*     */ import org.apache.http.auth.MalformedChallengeException;
/*     */ import org.apache.http.client.AuthCache;
/*     */ import org.apache.http.client.AuthenticationHandler;
/*     */ import org.apache.http.client.AuthenticationStrategy;
/*     */ import org.apache.http.client.CredentialsProvider;
/*     */ import org.apache.http.protocol.HttpContext;
/*     */ import org.apache.http.util.Args;
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
/*     */ @Deprecated
/*     */ @Contract(threading = ThreadingBehavior.IMMUTABLE)
/*     */ class AuthenticationStrategyAdaptor
/*     */   implements AuthenticationStrategy
/*     */ {
/*  64 */   private final Log log = LogFactory.getLog(getClass());
/*     */   
/*     */   private final AuthenticationHandler handler;
/*     */ 
/*     */   
/*     */   public AuthenticationStrategyAdaptor(AuthenticationHandler handler) {
/*  70 */     this.handler = handler;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isAuthenticationRequested(HttpHost authhost, HttpResponse response, HttpContext context) {
/*  78 */     return this.handler.isAuthenticationRequested(response, context);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Map<String, Header> getChallenges(HttpHost authhost, HttpResponse response, HttpContext context) throws MalformedChallengeException {
/*  86 */     return this.handler.getChallenges(response, context);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Queue<AuthOption> select(Map<String, Header> challenges, HttpHost authhost, HttpResponse response, HttpContext context) throws MalformedChallengeException {
/*     */     AuthScheme authScheme;
/*  95 */     Args.notNull(challenges, "Map of auth challenges");
/*  96 */     Args.notNull(authhost, "Host");
/*  97 */     Args.notNull(response, "HTTP response");
/*  98 */     Args.notNull(context, "HTTP context");
/*     */     
/* 100 */     Queue<AuthOption> options = new LinkedList<AuthOption>();
/* 101 */     CredentialsProvider credsProvider = (CredentialsProvider)context.getAttribute("http.auth.credentials-provider");
/*     */     
/* 103 */     if (credsProvider == null) {
/* 104 */       this.log.debug("Credentials provider not set in the context");
/* 105 */       return options;
/*     */     } 
/*     */ 
/*     */     
/*     */     try {
/* 110 */       authScheme = this.handler.selectScheme(challenges, response, context);
/* 111 */     } catch (AuthenticationException ex) {
/* 112 */       if (this.log.isWarnEnabled()) {
/* 113 */         this.log.warn(ex.getMessage(), (Throwable)ex);
/*     */       }
/* 115 */       return options;
/*     */     } 
/* 117 */     String id = authScheme.getSchemeName();
/* 118 */     Header challenge = challenges.get(id.toLowerCase(Locale.ROOT));
/* 119 */     authScheme.processChallenge(challenge);
/*     */     
/* 121 */     AuthScope authScope = new AuthScope(authhost.getHostName(), authhost.getPort(), authScheme.getRealm(), authScheme.getSchemeName());
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 127 */     Credentials credentials = credsProvider.getCredentials(authScope);
/* 128 */     if (credentials != null) {
/* 129 */       options.add(new AuthOption(authScheme, credentials));
/*     */     }
/* 131 */     return options;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void authSucceeded(HttpHost authhost, AuthScheme authScheme, HttpContext context) {
/* 137 */     AuthCache authCache = (AuthCache)context.getAttribute("http.auth.auth-cache");
/* 138 */     if (isCachable(authScheme)) {
/* 139 */       if (authCache == null) {
/* 140 */         authCache = new BasicAuthCache();
/* 141 */         context.setAttribute("http.auth.auth-cache", authCache);
/*     */       } 
/* 143 */       if (this.log.isDebugEnabled()) {
/* 144 */         this.log.debug("Caching '" + authScheme.getSchemeName() + "' auth scheme for " + authhost);
/*     */       }
/*     */       
/* 147 */       authCache.put(authhost, authScheme);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void authFailed(HttpHost authhost, AuthScheme authScheme, HttpContext context) {
/* 154 */     AuthCache authCache = (AuthCache)context.getAttribute("http.auth.auth-cache");
/* 155 */     if (authCache == null) {
/*     */       return;
/*     */     }
/* 158 */     if (this.log.isDebugEnabled()) {
/* 159 */       this.log.debug("Removing from cache '" + authScheme.getSchemeName() + "' auth scheme for " + authhost);
/*     */     }
/*     */     
/* 162 */     authCache.remove(authhost);
/*     */   }
/*     */   
/*     */   private boolean isCachable(AuthScheme authScheme) {
/* 166 */     if (authScheme == null || !authScheme.isComplete()) {
/* 167 */       return false;
/*     */     }
/* 169 */     String schemeName = authScheme.getSchemeName();
/* 170 */     return schemeName.equalsIgnoreCase("Basic");
/*     */   }
/*     */   
/*     */   public AuthenticationHandler getHandler() {
/* 174 */     return this.handler;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\impl\client\AuthenticationStrategyAdaptor.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */