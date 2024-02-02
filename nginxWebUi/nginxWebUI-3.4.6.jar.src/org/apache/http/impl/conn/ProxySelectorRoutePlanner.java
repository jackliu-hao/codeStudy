/*     */ package org.apache.http.impl.conn;
/*     */ 
/*     */ import java.net.InetAddress;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.net.Proxy;
/*     */ import java.net.ProxySelector;
/*     */ import java.net.URI;
/*     */ import java.net.URISyntaxException;
/*     */ import java.util.List;
/*     */ import org.apache.http.HttpException;
/*     */ import org.apache.http.HttpHost;
/*     */ import org.apache.http.HttpRequest;
/*     */ import org.apache.http.conn.params.ConnRouteParams;
/*     */ import org.apache.http.conn.routing.HttpRoute;
/*     */ import org.apache.http.conn.routing.HttpRoutePlanner;
/*     */ import org.apache.http.conn.scheme.Scheme;
/*     */ import org.apache.http.conn.scheme.SchemeRegistry;
/*     */ import org.apache.http.protocol.HttpContext;
/*     */ import org.apache.http.util.Args;
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
/*     */ public class ProxySelectorRoutePlanner
/*     */   implements HttpRoutePlanner
/*     */ {
/*     */   protected final SchemeRegistry schemeRegistry;
/*     */   protected ProxySelector proxySelector;
/*     */   
/*     */   public ProxySelectorRoutePlanner(SchemeRegistry schreg, ProxySelector prosel) {
/*  91 */     Args.notNull(schreg, "SchemeRegistry");
/*  92 */     this.schemeRegistry = schreg;
/*  93 */     this.proxySelector = prosel;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ProxySelector getProxySelector() {
/* 102 */     return this.proxySelector;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setProxySelector(ProxySelector prosel) {
/* 112 */     this.proxySelector = prosel;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public HttpRoute determineRoute(HttpHost target, HttpRequest request, HttpContext context) throws HttpException {
/* 121 */     Args.notNull(request, "HTTP request");
/*     */ 
/*     */     
/* 124 */     HttpRoute route = ConnRouteParams.getForcedRoute(request.getParams());
/*     */     
/* 126 */     if (route != null) {
/* 127 */       return route;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 133 */     Asserts.notNull(target, "Target host");
/*     */     
/* 135 */     InetAddress local = ConnRouteParams.getLocalAddress(request.getParams());
/*     */     
/* 137 */     HttpHost proxy = determineProxy(target, request, context);
/*     */     
/* 139 */     Scheme schm = this.schemeRegistry.getScheme(target.getSchemeName());
/*     */ 
/*     */ 
/*     */     
/* 143 */     boolean secure = schm.isLayered();
/*     */     
/* 145 */     if (proxy == null) {
/* 146 */       route = new HttpRoute(target, local, secure);
/*     */     } else {
/* 148 */       route = new HttpRoute(target, local, proxy, secure);
/*     */     } 
/* 150 */     return route;
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
/*     */   
/*     */   protected HttpHost determineProxy(HttpHost target, HttpRequest request, HttpContext context) throws HttpException {
/* 171 */     ProxySelector psel = this.proxySelector;
/* 172 */     if (psel == null) {
/* 173 */       psel = ProxySelector.getDefault();
/*     */     }
/* 175 */     if (psel == null) {
/* 176 */       return null;
/*     */     }
/*     */     
/* 179 */     URI targetURI = null;
/*     */     try {
/* 181 */       targetURI = new URI(target.toURI());
/* 182 */     } catch (URISyntaxException usx) {
/* 183 */       throw new HttpException("Cannot convert host to URI: " + target, usx);
/*     */     } 
/*     */     
/* 186 */     List<Proxy> proxies = psel.select(targetURI);
/*     */     
/* 188 */     Proxy p = chooseProxy(proxies, target, request, context);
/*     */     
/* 190 */     HttpHost result = null;
/* 191 */     if (p.type() == Proxy.Type.HTTP) {
/*     */       
/* 193 */       if (!(p.address() instanceof InetSocketAddress)) {
/* 194 */         throw new HttpException("Unable to handle non-Inet proxy address: " + p.address());
/*     */       }
/*     */       
/* 197 */       InetSocketAddress isa = (InetSocketAddress)p.address();
/*     */       
/* 199 */       result = new HttpHost(getHost(isa), isa.getPort());
/*     */     } 
/*     */     
/* 202 */     return result;
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
/*     */   protected String getHost(InetSocketAddress isa) {
/* 221 */     return isa.isUnresolved() ? isa.getHostName() : isa.getAddress().getHostAddress();
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected Proxy chooseProxy(List<Proxy> proxies, HttpHost target, HttpRequest request, HttpContext context) {
/* 246 */     Args.notEmpty(proxies, "List of proxies");
/*     */     
/* 248 */     Proxy result = null;
/*     */ 
/*     */     
/* 251 */     for (int i = 0; result == null && i < proxies.size(); i++) {
/*     */       
/* 253 */       Proxy p = proxies.get(i);
/* 254 */       switch (p.type()) {
/*     */         
/*     */         case DIRECT:
/*     */         case HTTP:
/* 258 */           result = p;
/*     */           break;
/*     */       } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     } 
/* 268 */     if (result == null)
/*     */     {
/*     */ 
/*     */       
/* 272 */       result = Proxy.NO_PROXY;
/*     */     }
/*     */     
/* 275 */     return result;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\impl\conn\ProxySelectorRoutePlanner.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */