/*     */ package org.apache.http.conn.params;
/*     */ 
/*     */ import java.util.Map;
/*     */ import java.util.concurrent.ConcurrentHashMap;
/*     */ import org.apache.http.annotation.Contract;
/*     */ import org.apache.http.annotation.ThreadingBehavior;
/*     */ import org.apache.http.conn.routing.HttpRoute;
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
/*     */ 
/*     */ 
/*     */ @Deprecated
/*     */ @Contract(threading = ThreadingBehavior.SAFE)
/*     */ public final class ConnPerRouteBean
/*     */   implements ConnPerRoute
/*     */ {
/*     */   public static final int DEFAULT_MAX_CONNECTIONS_PER_ROUTE = 2;
/*     */   private final ConcurrentHashMap<HttpRoute, Integer> maxPerHostMap;
/*     */   private volatile int defaultMax;
/*     */   
/*     */   public ConnPerRouteBean(int defaultMax) {
/*  60 */     this.maxPerHostMap = new ConcurrentHashMap<HttpRoute, Integer>();
/*  61 */     setDefaultMaxPerRoute(defaultMax);
/*     */   }
/*     */   
/*     */   public ConnPerRouteBean() {
/*  65 */     this(2);
/*     */   }
/*     */   
/*     */   public int getDefaultMax() {
/*  69 */     return this.defaultMax;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getDefaultMaxPerRoute() {
/*  76 */     return this.defaultMax;
/*     */   }
/*     */   
/*     */   public void setDefaultMaxPerRoute(int max) {
/*  80 */     Args.positive(max, "Default max per route");
/*  81 */     this.defaultMax = max;
/*     */   }
/*     */   
/*     */   public void setMaxForRoute(HttpRoute route, int max) {
/*  85 */     Args.notNull(route, "HTTP route");
/*  86 */     Args.positive(max, "Max per route");
/*  87 */     this.maxPerHostMap.put(route, Integer.valueOf(max));
/*     */   }
/*     */ 
/*     */   
/*     */   public int getMaxForRoute(HttpRoute route) {
/*  92 */     Args.notNull(route, "HTTP route");
/*  93 */     Integer max = this.maxPerHostMap.get(route);
/*  94 */     if (max != null) {
/*  95 */       return max.intValue();
/*     */     }
/*  97 */     return this.defaultMax;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setMaxForRoutes(Map<HttpRoute, Integer> map) {
/* 102 */     if (map == null) {
/*     */       return;
/*     */     }
/* 105 */     this.maxPerHostMap.clear();
/* 106 */     this.maxPerHostMap.putAll(map);
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 111 */     return this.maxPerHostMap.toString();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\conn\params\ConnPerRouteBean.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */