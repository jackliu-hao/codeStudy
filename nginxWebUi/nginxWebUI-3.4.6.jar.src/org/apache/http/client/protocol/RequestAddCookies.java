/*     */ package org.apache.http.client.protocol;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.net.URI;
/*     */ import java.net.URISyntaxException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Date;
/*     */ import java.util.List;
/*     */ import org.apache.commons.logging.Log;
/*     */ import org.apache.commons.logging.LogFactory;
/*     */ import org.apache.http.Header;
/*     */ import org.apache.http.HttpException;
/*     */ import org.apache.http.HttpHost;
/*     */ import org.apache.http.HttpRequest;
/*     */ import org.apache.http.HttpRequestInterceptor;
/*     */ import org.apache.http.annotation.Contract;
/*     */ import org.apache.http.annotation.ThreadingBehavior;
/*     */ import org.apache.http.client.CookieStore;
/*     */ import org.apache.http.client.config.RequestConfig;
/*     */ import org.apache.http.client.methods.HttpUriRequest;
/*     */ import org.apache.http.config.Lookup;
/*     */ import org.apache.http.conn.routing.RouteInfo;
/*     */ import org.apache.http.cookie.Cookie;
/*     */ import org.apache.http.cookie.CookieOrigin;
/*     */ import org.apache.http.cookie.CookieSpec;
/*     */ import org.apache.http.cookie.CookieSpecProvider;
/*     */ import org.apache.http.protocol.HttpContext;
/*     */ import org.apache.http.util.Args;
/*     */ import org.apache.http.util.TextUtils;
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
/*     */ @Contract(threading = ThreadingBehavior.IMMUTABLE)
/*     */ public class RequestAddCookies
/*     */   implements HttpRequestInterceptor
/*     */ {
/*  70 */   private final Log log = LogFactory.getLog(getClass());
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void process(HttpRequest request, HttpContext context) throws HttpException, IOException {
/*  79 */     Args.notNull(request, "HTTP request");
/*  80 */     Args.notNull(context, "HTTP context");
/*     */     
/*  82 */     String method = request.getRequestLine().getMethod();
/*  83 */     if (method.equalsIgnoreCase("CONNECT")) {
/*     */       return;
/*     */     }
/*     */     
/*  87 */     HttpClientContext clientContext = HttpClientContext.adapt(context);
/*     */ 
/*     */     
/*  90 */     CookieStore cookieStore = clientContext.getCookieStore();
/*  91 */     if (cookieStore == null) {
/*  92 */       this.log.debug("Cookie store not specified in HTTP context");
/*     */       
/*     */       return;
/*     */     } 
/*     */     
/*  97 */     Lookup<CookieSpecProvider> registry = clientContext.getCookieSpecRegistry();
/*  98 */     if (registry == null) {
/*  99 */       this.log.debug("CookieSpec registry not specified in HTTP context");
/*     */       
/*     */       return;
/*     */     } 
/*     */     
/* 104 */     HttpHost targetHost = clientContext.getTargetHost();
/* 105 */     if (targetHost == null) {
/* 106 */       this.log.debug("Target host not set in the context");
/*     */       
/*     */       return;
/*     */     } 
/*     */     
/* 111 */     RouteInfo route = clientContext.getHttpRoute();
/* 112 */     if (route == null) {
/* 113 */       this.log.debug("Connection route not set in the context");
/*     */       
/*     */       return;
/*     */     } 
/* 117 */     RequestConfig config = clientContext.getRequestConfig();
/* 118 */     String policy = config.getCookieSpec();
/* 119 */     if (policy == null) {
/* 120 */       policy = "default";
/*     */     }
/* 122 */     if (this.log.isDebugEnabled()) {
/* 123 */       this.log.debug("CookieSpec selected: " + policy);
/*     */     }
/*     */     
/* 126 */     URI requestURI = null;
/* 127 */     if (request instanceof HttpUriRequest) {
/* 128 */       requestURI = ((HttpUriRequest)request).getURI();
/*     */     } else {
/*     */       try {
/* 131 */         requestURI = new URI(request.getRequestLine().getUri());
/* 132 */       } catch (URISyntaxException ignore) {}
/*     */     } 
/*     */     
/* 135 */     String path = (requestURI != null) ? requestURI.getPath() : null;
/* 136 */     String hostName = targetHost.getHostName();
/* 137 */     int port = targetHost.getPort();
/* 138 */     if (port < 0) {
/* 139 */       port = route.getTargetHost().getPort();
/*     */     }
/*     */     
/* 142 */     CookieOrigin cookieOrigin = new CookieOrigin(hostName, (port >= 0) ? port : 0, !TextUtils.isEmpty(path) ? path : "/", route.isSecure());
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 149 */     CookieSpecProvider provider = (CookieSpecProvider)registry.lookup(policy);
/* 150 */     if (provider == null) {
/* 151 */       if (this.log.isDebugEnabled()) {
/* 152 */         this.log.debug("Unsupported cookie policy: " + policy);
/*     */       }
/*     */       
/*     */       return;
/*     */     } 
/* 157 */     CookieSpec cookieSpec = provider.create((HttpContext)clientContext);
/*     */     
/* 159 */     List<Cookie> cookies = cookieStore.getCookies();
/*     */     
/* 161 */     List<Cookie> matchedCookies = new ArrayList<Cookie>();
/* 162 */     Date now = new Date();
/* 163 */     boolean expired = false;
/* 164 */     for (Cookie cookie : cookies) {
/* 165 */       if (!cookie.isExpired(now)) {
/* 166 */         if (cookieSpec.match(cookie, cookieOrigin)) {
/* 167 */           if (this.log.isDebugEnabled()) {
/* 168 */             this.log.debug("Cookie " + cookie + " match " + cookieOrigin);
/*     */           }
/* 170 */           matchedCookies.add(cookie);
/*     */         }  continue;
/*     */       } 
/* 173 */       if (this.log.isDebugEnabled()) {
/* 174 */         this.log.debug("Cookie " + cookie + " expired");
/*     */       }
/* 176 */       expired = true;
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 182 */     if (expired) {
/* 183 */       cookieStore.clearExpired(now);
/*     */     }
/*     */     
/* 186 */     if (!matchedCookies.isEmpty()) {
/* 187 */       List<Header> headers = cookieSpec.formatCookies(matchedCookies);
/* 188 */       for (Header header : headers) {
/* 189 */         request.addHeader(header);
/*     */       }
/*     */     } 
/*     */     
/* 193 */     int ver = cookieSpec.getVersion();
/* 194 */     if (ver > 0) {
/* 195 */       Header header = cookieSpec.getVersionHeader();
/* 196 */       if (header != null)
/*     */       {
/* 198 */         request.addHeader(header);
/*     */       }
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 204 */     context.setAttribute("http.cookie-spec", cookieSpec);
/* 205 */     context.setAttribute("http.cookie-origin", cookieOrigin);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\client\protocol\RequestAddCookies.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */