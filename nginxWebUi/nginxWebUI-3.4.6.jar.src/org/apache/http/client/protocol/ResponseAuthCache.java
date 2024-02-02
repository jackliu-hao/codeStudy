/*     */ package org.apache.http.client.protocol;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import org.apache.commons.logging.Log;
/*     */ import org.apache.commons.logging.LogFactory;
/*     */ import org.apache.http.HttpException;
/*     */ import org.apache.http.HttpHost;
/*     */ import org.apache.http.HttpResponse;
/*     */ import org.apache.http.HttpResponseInterceptor;
/*     */ import org.apache.http.annotation.Contract;
/*     */ import org.apache.http.annotation.ThreadingBehavior;
/*     */ import org.apache.http.auth.AuthProtocolState;
/*     */ import org.apache.http.auth.AuthScheme;
/*     */ import org.apache.http.auth.AuthState;
/*     */ import org.apache.http.client.AuthCache;
/*     */ import org.apache.http.conn.scheme.Scheme;
/*     */ import org.apache.http.conn.scheme.SchemeRegistry;
/*     */ import org.apache.http.impl.client.BasicAuthCache;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @Deprecated
/*     */ @Contract(threading = ThreadingBehavior.IMMUTABLE)
/*     */ public class ResponseAuthCache
/*     */   implements HttpResponseInterceptor
/*     */ {
/*  65 */   private final Log log = LogFactory.getLog(getClass());
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void process(HttpResponse response, HttpContext context) throws HttpException, IOException {
/*     */     BasicAuthCache basicAuthCache;
/*  74 */     Args.notNull(response, "HTTP request");
/*  75 */     Args.notNull(context, "HTTP context");
/*  76 */     AuthCache authCache = (AuthCache)context.getAttribute("http.auth.auth-cache");
/*     */     
/*  78 */     HttpHost target = (HttpHost)context.getAttribute("http.target_host");
/*  79 */     AuthState targetState = (AuthState)context.getAttribute("http.auth.target-scope");
/*  80 */     if (target != null && targetState != null) {
/*  81 */       if (this.log.isDebugEnabled()) {
/*  82 */         this.log.debug("Target auth state: " + targetState.getState());
/*     */       }
/*  84 */       if (isCachable(targetState)) {
/*  85 */         SchemeRegistry schemeRegistry = (SchemeRegistry)context.getAttribute("http.scheme-registry");
/*     */         
/*  87 */         if (target.getPort() < 0) {
/*  88 */           Scheme scheme = schemeRegistry.getScheme(target);
/*  89 */           target = new HttpHost(target.getHostName(), scheme.resolvePort(target.getPort()), target.getSchemeName());
/*     */         } 
/*     */         
/*  92 */         if (authCache == null) {
/*  93 */           basicAuthCache = new BasicAuthCache();
/*  94 */           context.setAttribute("http.auth.auth-cache", basicAuthCache);
/*     */         } 
/*  96 */         switch (targetState.getState()) {
/*     */           case CHALLENGED:
/*  98 */             cache((AuthCache)basicAuthCache, target, targetState.getAuthScheme());
/*     */             break;
/*     */           case FAILURE:
/* 101 */             uncache((AuthCache)basicAuthCache, target, targetState.getAuthScheme());
/*     */             break;
/*     */         } 
/*     */       } 
/*     */     } 
/* 106 */     HttpHost proxy = (HttpHost)context.getAttribute("http.proxy_host");
/* 107 */     AuthState proxyState = (AuthState)context.getAttribute("http.auth.proxy-scope");
/* 108 */     if (proxy != null && proxyState != null) {
/* 109 */       if (this.log.isDebugEnabled()) {
/* 110 */         this.log.debug("Proxy auth state: " + proxyState.getState());
/*     */       }
/* 112 */       if (isCachable(proxyState)) {
/* 113 */         if (basicAuthCache == null) {
/* 114 */           basicAuthCache = new BasicAuthCache();
/* 115 */           context.setAttribute("http.auth.auth-cache", basicAuthCache);
/*     */         } 
/* 117 */         switch (proxyState.getState()) {
/*     */           case CHALLENGED:
/* 119 */             cache((AuthCache)basicAuthCache, proxy, proxyState.getAuthScheme());
/*     */             break;
/*     */           case FAILURE:
/* 122 */             uncache((AuthCache)basicAuthCache, proxy, proxyState.getAuthScheme());
/*     */             break;
/*     */         } 
/*     */       } 
/*     */     } 
/*     */   }
/*     */   private boolean isCachable(AuthState authState) {
/* 129 */     AuthScheme authScheme = authState.getAuthScheme();
/* 130 */     if (authScheme == null || !authScheme.isComplete()) {
/* 131 */       return false;
/*     */     }
/* 133 */     String schemeName = authScheme.getSchemeName();
/* 134 */     return (schemeName.equalsIgnoreCase("Basic") || schemeName.equalsIgnoreCase("Digest"));
/*     */   }
/*     */ 
/*     */   
/*     */   private void cache(AuthCache authCache, HttpHost host, AuthScheme authScheme) {
/* 139 */     if (this.log.isDebugEnabled()) {
/* 140 */       this.log.debug("Caching '" + authScheme.getSchemeName() + "' auth scheme for " + host);
/*     */     }
/*     */     
/* 143 */     authCache.put(host, authScheme);
/*     */   }
/*     */   
/*     */   private void uncache(AuthCache authCache, HttpHost host, AuthScheme authScheme) {
/* 147 */     if (this.log.isDebugEnabled()) {
/* 148 */       this.log.debug("Removing from cache '" + authScheme.getSchemeName() + "' auth scheme for " + host);
/*     */     }
/*     */     
/* 151 */     authCache.remove(host);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\client\protocol\ResponseAuthCache.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */