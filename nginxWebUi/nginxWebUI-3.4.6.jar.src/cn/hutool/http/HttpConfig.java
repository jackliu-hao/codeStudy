/*     */ package cn.hutool.http;
/*     */ 
/*     */ import cn.hutool.core.lang.Assert;
/*     */ import cn.hutool.core.net.SSLUtil;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.net.Proxy;
/*     */ import javax.net.ssl.HostnameVerifier;
/*     */ import javax.net.ssl.SSLSocketFactory;
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
/*     */ public class HttpConfig
/*     */ {
/*     */   public static HttpConfig create() {
/*  25 */     return new HttpConfig();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  31 */   int connectionTimeout = HttpGlobalConfig.getTimeout();
/*     */ 
/*     */ 
/*     */   
/*  35 */   int readTimeout = HttpGlobalConfig.getTimeout();
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   boolean isDisableCache;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  45 */   int maxRedirectCount = HttpGlobalConfig.getMaxRedirectCount();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   Proxy proxy;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   HostnameVerifier hostnameVerifier;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   SSLSocketFactory ssf;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   int blockSize;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  71 */   boolean ignoreEOFError = HttpGlobalConfig.isIgnoreEOFError();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  77 */   boolean decodeUrl = HttpGlobalConfig.isDecodeUrl();
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  82 */   final HttpInterceptor.Chain<HttpRequest> requestInterceptors = GlobalInterceptor.INSTANCE.getCopiedRequestInterceptor();
/*     */ 
/*     */ 
/*     */   
/*  86 */   final HttpInterceptor.Chain<HttpResponse> responseInterceptors = GlobalInterceptor.INSTANCE.getCopiedResponseInterceptor();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   boolean interceptorOnRedirect;
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
/*     */   public HttpConfig timeout(int milliseconds) {
/* 108 */     setConnectionTimeout(milliseconds);
/* 109 */     setReadTimeout(milliseconds);
/* 110 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public HttpConfig setConnectionTimeout(int milliseconds) {
/* 120 */     this.connectionTimeout = milliseconds;
/* 121 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public HttpConfig setReadTimeout(int milliseconds) {
/* 131 */     this.readTimeout = milliseconds;
/* 132 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public HttpConfig disableCache() {
/* 141 */     this.isDisableCache = true;
/* 142 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public HttpConfig setMaxRedirectCount(int maxRedirectCount) {
/* 153 */     this.maxRedirectCount = Math.max(maxRedirectCount, 0);
/* 154 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public HttpConfig setHostnameVerifier(HostnameVerifier hostnameVerifier) {
/* 166 */     this.hostnameVerifier = hostnameVerifier;
/* 167 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public HttpConfig setHttpProxy(String host, int port) {
/* 178 */     Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(host, port));
/*     */     
/* 180 */     return setProxy(proxy);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public HttpConfig setProxy(Proxy proxy) {
/* 190 */     this.proxy = proxy;
/* 191 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public HttpConfig setSSLSocketFactory(SSLSocketFactory ssf) {
/* 203 */     this.ssf = ssf;
/* 204 */     return this;
/*     */   }
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
/*     */   public HttpConfig setSSLProtocol(String protocol) {
/* 224 */     Assert.notBlank(protocol, "protocol must be not blank!", new Object[0]);
/* 225 */     setSSLSocketFactory(SSLUtil.createSSLContext(protocol).getSocketFactory());
/* 226 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public HttpConfig setBlockSize(int blockSize) {
/* 237 */     this.blockSize = blockSize;
/* 238 */     return this;
/*     */   }
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
/*     */   public HttpConfig setIgnoreEOFError(boolean ignoreEOFError) {
/* 251 */     this.ignoreEOFError = ignoreEOFError;
/* 252 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public HttpConfig setDecodeUrl(boolean decodeUrl) {
/* 264 */     this.decodeUrl = decodeUrl;
/* 265 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public HttpConfig addRequestInterceptor(HttpInterceptor<HttpRequest> interceptor) {
/* 275 */     this.requestInterceptors.addChain(interceptor);
/* 276 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public HttpConfig addResponseInterceptor(HttpInterceptor<HttpResponse> interceptor) {
/* 286 */     this.responseInterceptors.addChain(interceptor);
/* 287 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public HttpConfig setInterceptorOnRedirect(boolean interceptorOnRedirect) {
/* 297 */     this.interceptorOnRedirect = interceptorOnRedirect;
/* 298 */     return this;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\http\HttpConfig.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */