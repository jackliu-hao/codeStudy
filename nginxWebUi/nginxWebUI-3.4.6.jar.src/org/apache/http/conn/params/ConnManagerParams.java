/*     */ package org.apache.http.conn.params;
/*     */ 
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @Deprecated
/*     */ @Contract(threading = ThreadingBehavior.IMMUTABLE)
/*     */ public final class ConnManagerParams
/*     */   implements ConnManagerPNames
/*     */ {
/*     */   public static final int DEFAULT_MAX_TOTAL_CONNECTIONS = 20;
/*     */   
/*     */   @Deprecated
/*     */   public static long getTimeout(HttpParams params) {
/*  64 */     Args.notNull(params, "HTTP parameters");
/*  65 */     return params.getLongParameter("http.conn-manager.timeout", 0L);
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
/*     */   @Deprecated
/*     */   public static void setTimeout(HttpParams params, long timeout) {
/*  80 */     Args.notNull(params, "HTTP parameters");
/*  81 */     params.setLongParameter("http.conn-manager.timeout", timeout);
/*     */   }
/*     */ 
/*     */   
/*  85 */   private static final ConnPerRoute DEFAULT_CONN_PER_ROUTE = new ConnPerRoute()
/*     */     {
/*     */       public int getMaxForRoute(HttpRoute route)
/*     */       {
/*  89 */         return 2;
/*     */       }
/*     */     };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void setMaxConnectionsPerRoute(HttpParams params, ConnPerRoute connPerRoute) {
/* 103 */     Args.notNull(params, "HTTP parameters");
/* 104 */     params.setParameter("http.conn-manager.max-per-route", connPerRoute);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ConnPerRoute getMaxConnectionsPerRoute(HttpParams params) {
/* 115 */     Args.notNull(params, "HTTP parameters");
/* 116 */     ConnPerRoute connPerRoute = (ConnPerRoute)params.getParameter("http.conn-manager.max-per-route");
/* 117 */     if (connPerRoute == null) {
/* 118 */       connPerRoute = DEFAULT_CONN_PER_ROUTE;
/*     */     }
/* 120 */     return connPerRoute;
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
/*     */   public static void setMaxTotalConnections(HttpParams params, int maxTotalConnections) {
/* 132 */     Args.notNull(params, "HTTP parameters");
/* 133 */     params.setIntParameter("http.conn-manager.max-total", maxTotalConnections);
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
/*     */   public static int getMaxTotalConnections(HttpParams params) {
/* 145 */     Args.notNull(params, "HTTP parameters");
/* 146 */     return params.getIntParameter("http.conn-manager.max-total", 20);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\conn\params\ConnManagerParams.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */