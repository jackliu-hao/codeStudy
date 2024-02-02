/*     */ package org.apache.http.impl.conn;
/*     */ 
/*     */ import java.net.InetAddress;
/*     */ import org.apache.http.HttpException;
/*     */ import org.apache.http.HttpHost;
/*     */ import org.apache.http.HttpRequest;
/*     */ import org.apache.http.annotation.Contract;
/*     */ import org.apache.http.annotation.ThreadingBehavior;
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
/*     */ @Deprecated
/*     */ @Contract(threading = ThreadingBehavior.SAFE)
/*     */ public class DefaultHttpRoutePlanner
/*     */   implements HttpRoutePlanner
/*     */ {
/*     */   protected final SchemeRegistry schemeRegistry;
/*     */   
/*     */   public DefaultHttpRoutePlanner(SchemeRegistry schreg) {
/*  78 */     Args.notNull(schreg, "Scheme registry");
/*  79 */     this.schemeRegistry = schreg;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public HttpRoute determineRoute(HttpHost target, HttpRequest request, HttpContext context) throws HttpException {
/*     */     Scheme schm;
/*  88 */     Args.notNull(request, "HTTP request");
/*     */ 
/*     */     
/*  91 */     HttpRoute route = ConnRouteParams.getForcedRoute(request.getParams());
/*     */     
/*  93 */     if (route != null) {
/*  94 */       return route;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 100 */     Asserts.notNull(target, "Target host");
/*     */     
/* 102 */     InetAddress local = ConnRouteParams.getLocalAddress(request.getParams());
/*     */     
/* 104 */     HttpHost proxy = ConnRouteParams.getDefaultProxy(request.getParams());
/*     */ 
/*     */ 
/*     */     
/*     */     try {
/* 109 */       schm = this.schemeRegistry.getScheme(target.getSchemeName());
/* 110 */     } catch (IllegalStateException ex) {
/* 111 */       throw new HttpException(ex.getMessage());
/*     */     } 
/*     */ 
/*     */     
/* 115 */     boolean secure = schm.isLayered();
/*     */     
/* 117 */     if (proxy == null) {
/* 118 */       route = new HttpRoute(target, local, secure);
/*     */     } else {
/* 120 */       route = new HttpRoute(target, local, proxy, secure);
/*     */     } 
/* 122 */     return route;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\impl\conn\DefaultHttpRoutePlanner.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */