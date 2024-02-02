/*     */ package org.apache.http.impl.conn;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InterruptedIOException;
/*     */ import org.apache.http.HttpHost;
/*     */ import org.apache.http.conn.ClientConnectionOperator;
/*     */ import org.apache.http.conn.OperatedClientConnection;
/*     */ import org.apache.http.conn.routing.HttpRoute;
/*     */ import org.apache.http.conn.routing.RouteTracker;
/*     */ import org.apache.http.params.HttpParams;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @Deprecated
/*     */ public abstract class AbstractPoolEntry
/*     */ {
/*     */   protected final ClientConnectionOperator connOperator;
/*     */   protected final OperatedClientConnection connection;
/*     */   protected volatile HttpRoute route;
/*     */   protected volatile Object state;
/*     */   protected volatile RouteTracker tracker;
/*     */   
/*     */   protected AbstractPoolEntry(ClientConnectionOperator connOperator, HttpRoute route) {
/*  92 */     Args.notNull(connOperator, "Connection operator");
/*  93 */     this.connOperator = connOperator;
/*  94 */     this.connection = connOperator.createConnection();
/*  95 */     this.route = route;
/*  96 */     this.tracker = null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Object getState() {
/* 105 */     return this.state;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setState(Object state) {
/* 114 */     this.state = state;
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
/*     */   public void open(HttpRoute route, HttpContext context, HttpParams params) throws IOException {
/* 130 */     Args.notNull(route, "Route");
/* 131 */     Args.notNull(params, "HTTP parameters");
/* 132 */     if (this.tracker != null) {
/* 133 */       Asserts.check(!this.tracker.isConnected(), "Connection already open");
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 141 */     this.tracker = new RouteTracker(route);
/* 142 */     HttpHost proxy = route.getProxyHost();
/*     */     
/* 144 */     this.connOperator.openConnection(this.connection, (proxy != null) ? proxy : route.getTargetHost(), route.getLocalAddress(), context, params);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 150 */     RouteTracker localTracker = this.tracker;
/*     */ 
/*     */ 
/*     */     
/* 154 */     if (localTracker == null) {
/* 155 */       throw new InterruptedIOException("Request aborted");
/*     */     }
/*     */     
/* 158 */     if (proxy == null) {
/* 159 */       localTracker.connectTarget(this.connection.isSecure());
/*     */     } else {
/* 161 */       localTracker.connectProxy(proxy, this.connection.isSecure());
/*     */     } 
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
/*     */   public void tunnelTarget(boolean secure, HttpParams params) throws IOException {
/* 180 */     Args.notNull(params, "HTTP parameters");
/* 181 */     Asserts.notNull(this.tracker, "Route tracker");
/* 182 */     Asserts.check(this.tracker.isConnected(), "Connection not open");
/* 183 */     Asserts.check(!this.tracker.isTunnelled(), "Connection is already tunnelled");
/*     */     
/* 185 */     this.connection.update(null, this.tracker.getTargetHost(), secure, params);
/*     */     
/* 187 */     this.tracker.tunnelTarget(secure);
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
/*     */   public void tunnelProxy(HttpHost next, boolean secure, HttpParams params) throws IOException {
/* 208 */     Args.notNull(next, "Next proxy");
/* 209 */     Args.notNull(params, "Parameters");
/*     */     
/* 211 */     Asserts.notNull(this.tracker, "Route tracker");
/* 212 */     Asserts.check(this.tracker.isConnected(), "Connection not open");
/*     */     
/* 214 */     this.connection.update(null, next, secure, params);
/* 215 */     this.tracker.tunnelProxy(next, secure);
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
/*     */   public void layerProtocol(HttpContext context, HttpParams params) throws IOException {
/* 230 */     Args.notNull(params, "HTTP parameters");
/* 231 */     Asserts.notNull(this.tracker, "Route tracker");
/* 232 */     Asserts.check(this.tracker.isConnected(), "Connection not open");
/* 233 */     Asserts.check(this.tracker.isTunnelled(), "Protocol layering without a tunnel not supported");
/* 234 */     Asserts.check(!this.tracker.isLayered(), "Multiple protocol layering not supported");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 241 */     HttpHost target = this.tracker.getTargetHost();
/*     */     
/* 243 */     this.connOperator.updateSecureConnection(this.connection, target, context, params);
/*     */ 
/*     */     
/* 246 */     this.tracker.layerProtocol(this.connection.isSecure());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void shutdownEntry() {
/* 257 */     this.tracker = null;
/* 258 */     this.state = null;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\impl\conn\AbstractPoolEntry.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */