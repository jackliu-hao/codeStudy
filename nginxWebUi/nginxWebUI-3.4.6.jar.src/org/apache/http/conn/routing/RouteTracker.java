/*     */ package org.apache.http.conn.routing;
/*     */ 
/*     */ import java.net.InetAddress;
/*     */ import org.apache.http.HttpHost;
/*     */ import org.apache.http.util.Args;
/*     */ import org.apache.http.util.Asserts;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class RouteTracker
/*     */   implements RouteInfo, Cloneable
/*     */ {
/*     */   private final HttpHost targetHost;
/*     */   private final InetAddress localAddress;
/*     */   private boolean connected;
/*     */   private HttpHost[] proxyChain;
/*     */   private RouteInfo.TunnelType tunnelled;
/*     */   private RouteInfo.LayerType layered;
/*     */   private boolean secure;
/*     */   
/*     */   public RouteTracker(HttpHost target, InetAddress local) {
/*  80 */     Args.notNull(target, "Target host");
/*  81 */     this.targetHost = target;
/*  82 */     this.localAddress = local;
/*  83 */     this.tunnelled = RouteInfo.TunnelType.PLAIN;
/*  84 */     this.layered = RouteInfo.LayerType.PLAIN;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void reset() {
/*  91 */     this.connected = false;
/*  92 */     this.proxyChain = null;
/*  93 */     this.tunnelled = RouteInfo.TunnelType.PLAIN;
/*  94 */     this.layered = RouteInfo.LayerType.PLAIN;
/*  95 */     this.secure = false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public RouteTracker(HttpRoute route) {
/* 106 */     this(route.getTargetHost(), route.getLocalAddress());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final void connectTarget(boolean secure) {
/* 116 */     Asserts.check(!this.connected, "Already connected");
/* 117 */     this.connected = true;
/* 118 */     this.secure = secure;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final void connectProxy(HttpHost proxy, boolean secure) {
/* 129 */     Args.notNull(proxy, "Proxy host");
/* 130 */     Asserts.check(!this.connected, "Already connected");
/* 131 */     this.connected = true;
/* 132 */     this.proxyChain = new HttpHost[] { proxy };
/* 133 */     this.secure = secure;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final void tunnelTarget(boolean secure) {
/* 143 */     Asserts.check(this.connected, "No tunnel unless connected");
/* 144 */     Asserts.notNull(this.proxyChain, "No tunnel without proxy");
/* 145 */     this.tunnelled = RouteInfo.TunnelType.TUNNELLED;
/* 146 */     this.secure = secure;
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
/*     */   public final void tunnelProxy(HttpHost proxy, boolean secure) {
/* 159 */     Args.notNull(proxy, "Proxy host");
/* 160 */     Asserts.check(this.connected, "No tunnel unless connected");
/* 161 */     Asserts.notNull(this.proxyChain, "No tunnel without proxy");
/*     */     
/* 163 */     HttpHost[] proxies = new HttpHost[this.proxyChain.length + 1];
/* 164 */     System.arraycopy(this.proxyChain, 0, proxies, 0, this.proxyChain.length);
/*     */     
/* 166 */     proxies[proxies.length - 1] = proxy;
/*     */     
/* 168 */     this.proxyChain = proxies;
/* 169 */     this.secure = secure;
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
/*     */   public final void layerProtocol(boolean secure) {
/* 181 */     Asserts.check(this.connected, "No layered protocol unless connected");
/* 182 */     this.layered = RouteInfo.LayerType.LAYERED;
/* 183 */     this.secure = secure;
/*     */   }
/*     */ 
/*     */   
/*     */   public final HttpHost getTargetHost() {
/* 188 */     return this.targetHost;
/*     */   }
/*     */ 
/*     */   
/*     */   public final InetAddress getLocalAddress() {
/* 193 */     return this.localAddress;
/*     */   }
/*     */ 
/*     */   
/*     */   public final int getHopCount() {
/* 198 */     int hops = 0;
/* 199 */     if (this.connected) {
/* 200 */       if (this.proxyChain == null) {
/* 201 */         hops = 1;
/*     */       } else {
/* 203 */         hops = this.proxyChain.length + 1;
/*     */       } 
/*     */     }
/* 206 */     return hops;
/*     */   }
/*     */ 
/*     */   
/*     */   public final HttpHost getHopTarget(int hop) {
/* 211 */     Args.notNegative(hop, "Hop index");
/* 212 */     int hopcount = getHopCount();
/* 213 */     Args.check((hop < hopcount), "Hop index exceeds tracked route length");
/* 214 */     HttpHost result = null;
/* 215 */     if (hop < hopcount - 1) {
/* 216 */       result = this.proxyChain[hop];
/*     */     } else {
/* 218 */       result = this.targetHost;
/*     */     } 
/*     */     
/* 221 */     return result;
/*     */   }
/*     */ 
/*     */   
/*     */   public final HttpHost getProxyHost() {
/* 226 */     return (this.proxyChain == null) ? null : this.proxyChain[0];
/*     */   }
/*     */   
/*     */   public final boolean isConnected() {
/* 230 */     return this.connected;
/*     */   }
/*     */ 
/*     */   
/*     */   public final RouteInfo.TunnelType getTunnelType() {
/* 235 */     return this.tunnelled;
/*     */   }
/*     */ 
/*     */   
/*     */   public final boolean isTunnelled() {
/* 240 */     return (this.tunnelled == RouteInfo.TunnelType.TUNNELLED);
/*     */   }
/*     */ 
/*     */   
/*     */   public final RouteInfo.LayerType getLayerType() {
/* 245 */     return this.layered;
/*     */   }
/*     */ 
/*     */   
/*     */   public final boolean isLayered() {
/* 250 */     return (this.layered == RouteInfo.LayerType.LAYERED);
/*     */   }
/*     */ 
/*     */   
/*     */   public final boolean isSecure() {
/* 255 */     return this.secure;
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
/*     */   public final HttpRoute toRoute() {
/* 267 */     return !this.connected ? null : new HttpRoute(this.targetHost, this.localAddress, this.proxyChain, this.secure, this.tunnelled, this.layered);
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
/*     */   public final boolean equals(Object o) {
/* 283 */     if (o == this) {
/* 284 */       return true;
/*     */     }
/* 286 */     if (!(o instanceof RouteTracker)) {
/* 287 */       return false;
/*     */     }
/*     */     
/* 290 */     RouteTracker that = (RouteTracker)o;
/* 291 */     return (this.connected == that.connected && this.secure == that.secure && this.tunnelled == that.tunnelled && this.layered == that.layered && LangUtils.equals(this.targetHost, that.targetHost) && LangUtils.equals(this.localAddress, that.localAddress) && LangUtils.equals((Object[])this.proxyChain, (Object[])that.proxyChain));
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
/*     */   public final int hashCode() {
/* 312 */     int hash = 17;
/* 313 */     hash = LangUtils.hashCode(hash, this.targetHost);
/* 314 */     hash = LangUtils.hashCode(hash, this.localAddress);
/* 315 */     if (this.proxyChain != null) {
/* 316 */       for (HttpHost element : this.proxyChain) {
/* 317 */         hash = LangUtils.hashCode(hash, element);
/*     */       }
/*     */     }
/* 320 */     hash = LangUtils.hashCode(hash, this.connected);
/* 321 */     hash = LangUtils.hashCode(hash, this.secure);
/* 322 */     hash = LangUtils.hashCode(hash, this.tunnelled);
/* 323 */     hash = LangUtils.hashCode(hash, this.layered);
/* 324 */     return hash;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final String toString() {
/* 334 */     StringBuilder cab = new StringBuilder(50 + getHopCount() * 30);
/*     */     
/* 336 */     cab.append("RouteTracker[");
/* 337 */     if (this.localAddress != null) {
/* 338 */       cab.append(this.localAddress);
/* 339 */       cab.append("->");
/*     */     } 
/* 341 */     cab.append('{');
/* 342 */     if (this.connected) {
/* 343 */       cab.append('c');
/*     */     }
/* 345 */     if (this.tunnelled == RouteInfo.TunnelType.TUNNELLED) {
/* 346 */       cab.append('t');
/*     */     }
/* 348 */     if (this.layered == RouteInfo.LayerType.LAYERED) {
/* 349 */       cab.append('l');
/*     */     }
/* 351 */     if (this.secure) {
/* 352 */       cab.append('s');
/*     */     }
/* 354 */     cab.append("}->");
/* 355 */     if (this.proxyChain != null) {
/* 356 */       for (HttpHost element : this.proxyChain) {
/* 357 */         cab.append(element);
/* 358 */         cab.append("->");
/*     */       } 
/*     */     }
/* 361 */     cab.append(this.targetHost);
/* 362 */     cab.append(']');
/*     */     
/* 364 */     return cab.toString();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Object clone() throws CloneNotSupportedException {
/* 371 */     return super.clone();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\conn\routing\RouteTracker.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */