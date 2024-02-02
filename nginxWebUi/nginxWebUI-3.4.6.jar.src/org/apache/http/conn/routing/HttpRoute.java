/*     */ package org.apache.http.conn.routing;
/*     */ 
/*     */ import java.net.InetAddress;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ import org.apache.http.HttpHost;
/*     */ import org.apache.http.annotation.Contract;
/*     */ import org.apache.http.annotation.ThreadingBehavior;
/*     */ import org.apache.http.util.Args;
/*     */ import org.apache.http.util.LangUtils;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
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
/*     */ public final class HttpRoute
/*     */   implements RouteInfo, Cloneable
/*     */ {
/*     */   private final HttpHost targetHost;
/*     */   private final InetAddress localAddress;
/*     */   private final List<HttpHost> proxyChain;
/*     */   private final RouteInfo.TunnelType tunnelled;
/*     */   private final RouteInfo.LayerType layered;
/*     */   private final boolean secure;
/*     */   
/*     */   private HttpRoute(HttpHost target, InetAddress local, List<HttpHost> proxies, boolean secure, RouteInfo.TunnelType tunnelled, RouteInfo.LayerType layered) {
/*  74 */     Args.notNull(target, "Target host");
/*  75 */     this.targetHost = normalize(target);
/*  76 */     this.localAddress = local;
/*  77 */     if (proxies != null && !proxies.isEmpty()) {
/*  78 */       this.proxyChain = new ArrayList<HttpHost>(proxies);
/*     */     } else {
/*  80 */       this.proxyChain = null;
/*     */     } 
/*  82 */     if (tunnelled == RouteInfo.TunnelType.TUNNELLED) {
/*  83 */       Args.check((this.proxyChain != null), "Proxy required if tunnelled");
/*     */     }
/*  85 */     this.secure = secure;
/*  86 */     this.tunnelled = (tunnelled != null) ? tunnelled : RouteInfo.TunnelType.PLAIN;
/*  87 */     this.layered = (layered != null) ? layered : RouteInfo.LayerType.PLAIN;
/*     */   }
/*     */ 
/*     */   
/*     */   private static int getDefaultPort(String schemeName) {
/*  92 */     if ("http".equalsIgnoreCase(schemeName))
/*  93 */       return 80; 
/*  94 */     if ("https".equalsIgnoreCase(schemeName)) {
/*  95 */       return 443;
/*     */     }
/*  97 */     return -1;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static HttpHost normalize(HttpHost target) {
/* 104 */     if (target.getPort() >= 0) {
/* 105 */       return target;
/*     */     }
/* 107 */     InetAddress address = target.getAddress();
/* 108 */     String schemeName = target.getSchemeName();
/* 109 */     return (address != null) ? new HttpHost(address, getDefaultPort(schemeName), schemeName) : new HttpHost(target.getHostName(), getDefaultPort(schemeName), schemeName);
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
/*     */   public HttpRoute(HttpHost target, InetAddress local, HttpHost[] proxies, boolean secure, RouteInfo.TunnelType tunnelled, RouteInfo.LayerType layered) {
/* 130 */     this(target, local, (proxies != null) ? Arrays.<HttpHost>asList(proxies) : null, secure, tunnelled, layered);
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
/*     */   public HttpRoute(HttpHost target, InetAddress local, HttpHost proxy, boolean secure, RouteInfo.TunnelType tunnelled, RouteInfo.LayerType layered) {
/* 153 */     this(target, local, (proxy != null) ? Collections.<HttpHost>singletonList(proxy) : null, secure, tunnelled, layered);
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
/*     */   public HttpRoute(HttpHost target, InetAddress local, boolean secure) {
/* 168 */     this(target, local, Collections.emptyList(), secure, RouteInfo.TunnelType.PLAIN, RouteInfo.LayerType.PLAIN);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public HttpRoute(HttpHost target) {
/* 178 */     this(target, (InetAddress)null, Collections.emptyList(), false, RouteInfo.TunnelType.PLAIN, RouteInfo.LayerType.PLAIN);
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
/*     */   public HttpRoute(HttpHost target, InetAddress local, HttpHost proxy, boolean secure) {
/* 197 */     this(target, local, Collections.singletonList(Args.notNull(proxy, "Proxy host")), secure, secure ? RouteInfo.TunnelType.TUNNELLED : RouteInfo.TunnelType.PLAIN, secure ? RouteInfo.LayerType.LAYERED : RouteInfo.LayerType.PLAIN);
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
/*     */   public HttpRoute(HttpHost target, HttpHost proxy) {
/* 211 */     this(target, null, proxy, false);
/*     */   }
/*     */ 
/*     */   
/*     */   public final HttpHost getTargetHost() {
/* 216 */     return this.targetHost;
/*     */   }
/*     */ 
/*     */   
/*     */   public final InetAddress getLocalAddress() {
/* 221 */     return this.localAddress;
/*     */   }
/*     */   
/*     */   public final InetSocketAddress getLocalSocketAddress() {
/* 225 */     return (this.localAddress != null) ? new InetSocketAddress(this.localAddress, 0) : null;
/*     */   }
/*     */ 
/*     */   
/*     */   public final int getHopCount() {
/* 230 */     return (this.proxyChain != null) ? (this.proxyChain.size() + 1) : 1;
/*     */   }
/*     */ 
/*     */   
/*     */   public final HttpHost getHopTarget(int hop) {
/* 235 */     Args.notNegative(hop, "Hop index");
/* 236 */     int hopcount = getHopCount();
/* 237 */     Args.check((hop < hopcount), "Hop index exceeds tracked route length");
/* 238 */     return (hop < hopcount - 1) ? this.proxyChain.get(hop) : this.targetHost;
/*     */   }
/*     */ 
/*     */   
/*     */   public final HttpHost getProxyHost() {
/* 243 */     return (this.proxyChain != null && !this.proxyChain.isEmpty()) ? this.proxyChain.get(0) : null;
/*     */   }
/*     */ 
/*     */   
/*     */   public final RouteInfo.TunnelType getTunnelType() {
/* 248 */     return this.tunnelled;
/*     */   }
/*     */ 
/*     */   
/*     */   public final boolean isTunnelled() {
/* 253 */     return (this.tunnelled == RouteInfo.TunnelType.TUNNELLED);
/*     */   }
/*     */ 
/*     */   
/*     */   public final RouteInfo.LayerType getLayerType() {
/* 258 */     return this.layered;
/*     */   }
/*     */ 
/*     */   
/*     */   public final boolean isLayered() {
/* 263 */     return (this.layered == RouteInfo.LayerType.LAYERED);
/*     */   }
/*     */ 
/*     */   
/*     */   public final boolean isSecure() {
/* 268 */     return this.secure;
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
/*     */   public final boolean equals(Object obj) {
/* 281 */     if (this == obj) {
/* 282 */       return true;
/*     */     }
/* 284 */     if (obj instanceof HttpRoute) {
/* 285 */       HttpRoute that = (HttpRoute)obj;
/* 286 */       return (this.secure == that.secure && this.tunnelled == that.tunnelled && this.layered == that.layered && LangUtils.equals(this.targetHost, that.targetHost) && LangUtils.equals(this.localAddress, that.localAddress) && LangUtils.equals(this.proxyChain, that.proxyChain));
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 295 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final int hashCode() {
/* 306 */     int hash = 17;
/* 307 */     hash = LangUtils.hashCode(hash, this.targetHost);
/* 308 */     hash = LangUtils.hashCode(hash, this.localAddress);
/* 309 */     if (this.proxyChain != null) {
/* 310 */       for (HttpHost element : this.proxyChain) {
/* 311 */         hash = LangUtils.hashCode(hash, element);
/*     */       }
/*     */     }
/* 314 */     hash = LangUtils.hashCode(hash, this.secure);
/* 315 */     hash = LangUtils.hashCode(hash, this.tunnelled);
/* 316 */     hash = LangUtils.hashCode(hash, this.layered);
/* 317 */     return hash;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final String toString() {
/* 327 */     StringBuilder cab = new StringBuilder(50 + getHopCount() * 30);
/* 328 */     if (this.localAddress != null) {
/* 329 */       cab.append(this.localAddress);
/* 330 */       cab.append("->");
/*     */     } 
/* 332 */     cab.append('{');
/* 333 */     if (this.tunnelled == RouteInfo.TunnelType.TUNNELLED) {
/* 334 */       cab.append('t');
/*     */     }
/* 336 */     if (this.layered == RouteInfo.LayerType.LAYERED) {
/* 337 */       cab.append('l');
/*     */     }
/* 339 */     if (this.secure) {
/* 340 */       cab.append('s');
/*     */     }
/* 342 */     cab.append("}->");
/* 343 */     if (this.proxyChain != null) {
/* 344 */       for (HttpHost aProxyChain : this.proxyChain) {
/* 345 */         cab.append(aProxyChain);
/* 346 */         cab.append("->");
/*     */       } 
/*     */     }
/* 349 */     cab.append(this.targetHost);
/* 350 */     return cab.toString();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Object clone() throws CloneNotSupportedException {
/* 356 */     return super.clone();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\conn\routing\HttpRoute.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */