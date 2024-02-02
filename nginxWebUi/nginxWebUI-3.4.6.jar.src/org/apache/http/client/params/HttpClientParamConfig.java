/*     */ package org.apache.http.client.params;
/*     */ 
/*     */ import java.net.InetAddress;
/*     */ import java.util.Collection;
/*     */ import org.apache.http.HttpHost;
/*     */ import org.apache.http.client.config.RequestConfig;
/*     */ import org.apache.http.params.HttpParams;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ @Deprecated
/*     */ public final class HttpClientParamConfig
/*     */ {
/*     */   public static RequestConfig getRequestConfig(HttpParams params) {
/*  54 */     return getRequestConfig(params, RequestConfig.DEFAULT);
/*     */   }
/*     */ 
/*     */   
/*     */   public static RequestConfig getRequestConfig(HttpParams params, RequestConfig defaultConfig) {
/*  59 */     RequestConfig.Builder builder = RequestConfig.copy(defaultConfig).setSocketTimeout(params.getIntParameter("http.socket.timeout", defaultConfig.getSocketTimeout())).setStaleConnectionCheckEnabled(params.getBooleanParameter("http.connection.stalecheck", defaultConfig.isStaleConnectionCheckEnabled())).setConnectTimeout(params.getIntParameter("http.connection.timeout", defaultConfig.getConnectTimeout())).setExpectContinueEnabled(params.getBooleanParameter("http.protocol.expect-continue", defaultConfig.isExpectContinueEnabled())).setAuthenticationEnabled(params.getBooleanParameter("http.protocol.handle-authentication", defaultConfig.isAuthenticationEnabled())).setCircularRedirectsAllowed(params.getBooleanParameter("http.protocol.allow-circular-redirects", defaultConfig.isCircularRedirectsAllowed())).setConnectionRequestTimeout((int)params.getLongParameter("http.conn-manager.timeout", defaultConfig.getConnectionRequestTimeout())).setMaxRedirects(params.getIntParameter("http.protocol.max-redirects", defaultConfig.getMaxRedirects())).setRedirectsEnabled(params.getBooleanParameter("http.protocol.handle-redirects", defaultConfig.isRedirectsEnabled())).setRelativeRedirectsAllowed(!params.getBooleanParameter("http.protocol.reject-relative-redirect", !defaultConfig.isRelativeRedirectsAllowed()));
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
/*  81 */     HttpHost proxy = (HttpHost)params.getParameter("http.route.default-proxy");
/*  82 */     if (proxy != null) {
/*  83 */       builder.setProxy(proxy);
/*     */     }
/*  85 */     InetAddress localAddress = (InetAddress)params.getParameter("http.route.local-address");
/*  86 */     if (localAddress != null) {
/*  87 */       builder.setLocalAddress(localAddress);
/*     */     }
/*  89 */     Collection<String> targetAuthPrefs = (Collection<String>)params.getParameter("http.auth.target-scheme-pref");
/*  90 */     if (targetAuthPrefs != null) {
/*  91 */       builder.setTargetPreferredAuthSchemes(targetAuthPrefs);
/*     */     }
/*  93 */     Collection<String> proxySuthPrefs = (Collection<String>)params.getParameter("http.auth.proxy-scheme-pref");
/*  94 */     if (proxySuthPrefs != null) {
/*  95 */       builder.setProxyPreferredAuthSchemes(proxySuthPrefs);
/*     */     }
/*  97 */     String cookiePolicy = (String)params.getParameter("http.protocol.cookie-policy");
/*  98 */     if (cookiePolicy != null) {
/*  99 */       builder.setCookieSpec(cookiePolicy);
/*     */     }
/* 101 */     return builder.build();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\client\params\HttpClientParamConfig.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */