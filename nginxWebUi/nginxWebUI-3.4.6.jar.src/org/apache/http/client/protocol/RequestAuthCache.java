/*     */ package org.apache.http.client.protocol;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import org.apache.commons.logging.Log;
/*     */ import org.apache.commons.logging.LogFactory;
/*     */ import org.apache.http.HttpException;
/*     */ import org.apache.http.HttpHost;
/*     */ import org.apache.http.HttpRequest;
/*     */ import org.apache.http.HttpRequestInterceptor;
/*     */ import org.apache.http.annotation.Contract;
/*     */ import org.apache.http.annotation.ThreadingBehavior;
/*     */ import org.apache.http.auth.AuthProtocolState;
/*     */ import org.apache.http.auth.AuthScheme;
/*     */ import org.apache.http.auth.AuthScope;
/*     */ import org.apache.http.auth.AuthState;
/*     */ import org.apache.http.auth.Credentials;
/*     */ import org.apache.http.client.AuthCache;
/*     */ import org.apache.http.client.CredentialsProvider;
/*     */ import org.apache.http.conn.routing.RouteInfo;
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
/*     */ 
/*     */ 
/*     */ @Contract(threading = ThreadingBehavior.IMMUTABLE)
/*     */ public class RequestAuthCache
/*     */   implements HttpRequestInterceptor
/*     */ {
/*  61 */   private final Log log = LogFactory.getLog(getClass());
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void process(HttpRequest request, HttpContext context) throws HttpException, IOException {
/*  70 */     Args.notNull(request, "HTTP request");
/*  71 */     Args.notNull(context, "HTTP context");
/*     */     
/*  73 */     HttpClientContext clientContext = HttpClientContext.adapt(context);
/*     */     
/*  75 */     AuthCache authCache = clientContext.getAuthCache();
/*  76 */     if (authCache == null) {
/*  77 */       this.log.debug("Auth cache not set in the context");
/*     */       
/*     */       return;
/*     */     } 
/*  81 */     CredentialsProvider credsProvider = clientContext.getCredentialsProvider();
/*  82 */     if (credsProvider == null) {
/*  83 */       this.log.debug("Credentials provider not set in the context");
/*     */       
/*     */       return;
/*     */     } 
/*  87 */     RouteInfo route = clientContext.getHttpRoute();
/*  88 */     if (route == null) {
/*  89 */       this.log.debug("Route info not set in the context");
/*     */       
/*     */       return;
/*     */     } 
/*  93 */     HttpHost target = clientContext.getTargetHost();
/*  94 */     if (target == null) {
/*  95 */       this.log.debug("Target host not set in the context");
/*     */       
/*     */       return;
/*     */     } 
/*  99 */     if (target.getPort() < 0) {
/* 100 */       target = new HttpHost(target.getHostName(), route.getTargetHost().getPort(), target.getSchemeName());
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 106 */     AuthState targetState = clientContext.getTargetAuthState();
/* 107 */     if (targetState != null && targetState.getState() == AuthProtocolState.UNCHALLENGED) {
/* 108 */       AuthScheme authScheme = authCache.get(target);
/* 109 */       if (authScheme != null) {
/* 110 */         doPreemptiveAuth(target, authScheme, targetState, credsProvider);
/*     */       }
/*     */     } 
/*     */     
/* 114 */     HttpHost proxy = route.getProxyHost();
/* 115 */     AuthState proxyState = clientContext.getProxyAuthState();
/* 116 */     if (proxy != null && proxyState != null && proxyState.getState() == AuthProtocolState.UNCHALLENGED) {
/* 117 */       AuthScheme authScheme = authCache.get(proxy);
/* 118 */       if (authScheme != null) {
/* 119 */         doPreemptiveAuth(proxy, authScheme, proxyState, credsProvider);
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void doPreemptiveAuth(HttpHost host, AuthScheme authScheme, AuthState authState, CredentialsProvider credsProvider) {
/* 129 */     String schemeName = authScheme.getSchemeName();
/* 130 */     if (this.log.isDebugEnabled()) {
/* 131 */       this.log.debug("Re-using cached '" + schemeName + "' auth scheme for " + host);
/*     */     }
/*     */     
/* 134 */     AuthScope authScope = new AuthScope(host, AuthScope.ANY_REALM, schemeName);
/* 135 */     Credentials creds = credsProvider.getCredentials(authScope);
/*     */     
/* 137 */     if (creds != null) {
/* 138 */       authState.update(authScheme, creds);
/*     */     } else {
/* 140 */       this.log.debug("No credentials for preemptive authentication");
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\client\protocol\RequestAuthCache.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */