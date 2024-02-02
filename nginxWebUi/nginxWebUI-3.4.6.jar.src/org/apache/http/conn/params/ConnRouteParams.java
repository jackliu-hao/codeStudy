/*     */ package org.apache.http.conn.params;
/*     */ 
/*     */ import java.net.InetAddress;
/*     */ import org.apache.http.HttpHost;
/*     */ import org.apache.http.annotation.Contract;
/*     */ import org.apache.http.annotation.ThreadingBehavior;
/*     */ import org.apache.http.conn.routing.HttpRoute;
/*     */ import org.apache.http.params.HttpParams;
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
/*     */ @Deprecated
/*     */ @Contract(threading = ThreadingBehavior.IMMUTABLE)
/*     */ public class ConnRouteParams
/*     */   implements ConnRoutePNames
/*     */ {
/*  55 */   public static final HttpHost NO_HOST = new HttpHost("127.0.0.255", 0, "no-host");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  62 */   public static final HttpRoute NO_ROUTE = new HttpRoute(NO_HOST);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static HttpHost getDefaultProxy(HttpParams params) {
/*  81 */     Args.notNull(params, "Parameters");
/*  82 */     HttpHost proxy = (HttpHost)params.getParameter("http.route.default-proxy");
/*     */     
/*  84 */     if (proxy != null && NO_HOST.equals(proxy))
/*     */     {
/*  86 */       proxy = null;
/*     */     }
/*  88 */     return proxy;
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
/*     */   public static void setDefaultProxy(HttpParams params, HttpHost proxy) {
/* 103 */     Args.notNull(params, "Parameters");
/* 104 */     params.setParameter("http.route.default-proxy", proxy);
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
/*     */   public static HttpRoute getForcedRoute(HttpParams params) {
/* 119 */     Args.notNull(params, "Parameters");
/* 120 */     HttpRoute route = (HttpRoute)params.getParameter("http.route.forced-route");
/*     */     
/* 122 */     if (route != null && NO_ROUTE.equals(route))
/*     */     {
/* 124 */       route = null;
/*     */     }
/* 126 */     return route;
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
/*     */   public static void setForcedRoute(HttpParams params, HttpRoute route) {
/* 141 */     Args.notNull(params, "Parameters");
/* 142 */     params.setParameter("http.route.forced-route", route);
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
/*     */   public static InetAddress getLocalAddress(HttpParams params) {
/* 158 */     Args.notNull(params, "Parameters");
/* 159 */     InetAddress local = (InetAddress)params.getParameter("http.route.local-address");
/*     */ 
/*     */     
/* 162 */     return local;
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
/*     */   public static void setLocalAddress(HttpParams params, InetAddress local) {
/* 174 */     Args.notNull(params, "Parameters");
/* 175 */     params.setParameter("http.route.local-address", local);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\conn\params\ConnRouteParams.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */