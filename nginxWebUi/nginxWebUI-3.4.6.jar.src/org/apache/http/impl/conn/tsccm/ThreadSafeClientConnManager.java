/*     */ package org.apache.http.impl.conn.tsccm;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import org.apache.commons.logging.Log;
/*     */ import org.apache.commons.logging.LogFactory;
/*     */ import org.apache.http.annotation.Contract;
/*     */ import org.apache.http.annotation.ThreadingBehavior;
/*     */ import org.apache.http.conn.ClientConnectionManager;
/*     */ import org.apache.http.conn.ClientConnectionOperator;
/*     */ import org.apache.http.conn.ClientConnectionRequest;
/*     */ import org.apache.http.conn.ConnectionPoolTimeoutException;
/*     */ import org.apache.http.conn.ManagedClientConnection;
/*     */ import org.apache.http.conn.params.ConnPerRoute;
/*     */ import org.apache.http.conn.params.ConnPerRouteBean;
/*     */ import org.apache.http.conn.routing.HttpRoute;
/*     */ import org.apache.http.conn.scheme.SchemeRegistry;
/*     */ import org.apache.http.impl.conn.DefaultClientConnectionOperator;
/*     */ import org.apache.http.impl.conn.SchemeRegistryFactory;
/*     */ import org.apache.http.params.HttpParams;
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
/*     */ @Deprecated
/*     */ @Contract(threading = ThreadingBehavior.SAFE_CONDITIONAL)
/*     */ public class ThreadSafeClientConnManager
/*     */   implements ClientConnectionManager
/*     */ {
/*     */   private final Log log;
/*     */   protected final SchemeRegistry schemeRegistry;
/*     */   protected final AbstractConnPool connectionPool;
/*     */   protected final ConnPoolByRoute pool;
/*     */   protected final ClientConnectionOperator connOperator;
/*     */   protected final ConnPerRouteBean connPerRoute;
/*     */   
/*     */   public ThreadSafeClientConnManager(SchemeRegistry schreg) {
/*  95 */     this(schreg, -1L, TimeUnit.MILLISECONDS);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ThreadSafeClientConnManager() {
/* 102 */     this(SchemeRegistryFactory.createDefault());
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
/*     */   public ThreadSafeClientConnManager(SchemeRegistry schreg, long connTTL, TimeUnit connTTLTimeUnit) {
/* 116 */     this(schreg, connTTL, connTTLTimeUnit, new ConnPerRouteBean());
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
/*     */   public ThreadSafeClientConnManager(SchemeRegistry schreg, long connTTL, TimeUnit connTTLTimeUnit, ConnPerRouteBean connPerRoute) {
/* 134 */     Args.notNull(schreg, "Scheme registry");
/* 135 */     this.log = LogFactory.getLog(getClass());
/* 136 */     this.schemeRegistry = schreg;
/* 137 */     this.connPerRoute = connPerRoute;
/* 138 */     this.connOperator = createConnectionOperator(schreg);
/* 139 */     this.pool = createConnectionPool(connTTL, connTTLTimeUnit);
/* 140 */     this.connectionPool = this.pool;
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
/*     */   @Deprecated
/*     */   public ThreadSafeClientConnManager(HttpParams params, SchemeRegistry schreg) {
/* 154 */     Args.notNull(schreg, "Scheme registry");
/* 155 */     this.log = LogFactory.getLog(getClass());
/* 156 */     this.schemeRegistry = schreg;
/* 157 */     this.connPerRoute = new ConnPerRouteBean();
/* 158 */     this.connOperator = createConnectionOperator(schreg);
/* 159 */     this.pool = (ConnPoolByRoute)createConnectionPool(params);
/* 160 */     this.connectionPool = this.pool;
/*     */   }
/*     */ 
/*     */   
/*     */   protected void finalize() throws Throwable {
/*     */     try {
/* 166 */       shutdown();
/*     */     } finally {
/* 168 */       super.finalize();
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
/*     */   @Deprecated
/*     */   protected AbstractConnPool createConnectionPool(HttpParams params) {
/* 181 */     return new ConnPoolByRoute(this.connOperator, params);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected ConnPoolByRoute createConnectionPool(long connTTL, TimeUnit connTTLTimeUnit) {
/* 192 */     return new ConnPoolByRoute(this.connOperator, (ConnPerRoute)this.connPerRoute, 20, connTTL, connTTLTimeUnit);
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
/*     */   protected ClientConnectionOperator createConnectionOperator(SchemeRegistry schreg) {
/* 210 */     return (ClientConnectionOperator)new DefaultClientConnectionOperator(schreg);
/*     */   }
/*     */ 
/*     */   
/*     */   public SchemeRegistry getSchemeRegistry() {
/* 215 */     return this.schemeRegistry;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ClientConnectionRequest requestConnection(final HttpRoute route, Object state) {
/* 223 */     final PoolEntryRequest poolRequest = this.pool.requestPoolEntry(route, state);
/*     */ 
/*     */     
/* 226 */     return new ClientConnectionRequest()
/*     */       {
/*     */         public void abortRequest()
/*     */         {
/* 230 */           poolRequest.abortRequest();
/*     */         }
/*     */ 
/*     */ 
/*     */ 
/*     */         
/*     */         public ManagedClientConnection getConnection(long timeout, TimeUnit timeUnit) throws InterruptedException, ConnectionPoolTimeoutException {
/* 237 */           Args.notNull(route, "Route");
/*     */           
/* 239 */           if (ThreadSafeClientConnManager.this.log.isDebugEnabled()) {
/* 240 */             ThreadSafeClientConnManager.this.log.debug("Get connection: " + route + ", timeout = " + timeout);
/*     */           }
/*     */           
/* 243 */           BasicPoolEntry entry = poolRequest.getPoolEntry(timeout, timeUnit);
/* 244 */           return (ManagedClientConnection)new BasicPooledConnAdapter(ThreadSafeClientConnManager.this, entry);
/*     */         }
/*     */       };
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void releaseConnection(ManagedClientConnection conn, long validDuration, TimeUnit timeUnit) {
/* 253 */     Args.check(conn instanceof BasicPooledConnAdapter, "Connection class mismatch, connection not obtained from this manager");
/*     */     
/* 255 */     BasicPooledConnAdapter hca = (BasicPooledConnAdapter)conn;
/* 256 */     if (hca.getPoolEntry() != null) {
/* 257 */       Asserts.check((hca.getManager() == this), "Connection not obtained from this manager");
/*     */     }
/* 259 */     synchronized (hca) {
/* 260 */       BasicPoolEntry entry = (BasicPoolEntry)hca.getPoolEntry();
/* 261 */       if (entry == null) {
/*     */         return;
/*     */       }
/*     */       
/*     */       try {
/* 266 */         if (hca.isOpen() && !hca.isMarkedReusable())
/*     */         {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */           
/* 275 */           hca.shutdown();
/*     */         }
/* 277 */       } catch (IOException iox) {
/* 278 */         if (this.log.isDebugEnabled()) {
/* 279 */           this.log.debug("Exception shutting down released connection.", iox);
/*     */         }
/*     */       } finally {
/*     */         
/* 283 */         boolean reusable = hca.isMarkedReusable();
/* 284 */         if (this.log.isDebugEnabled()) {
/* 285 */           if (reusable) {
/* 286 */             this.log.debug("Released connection is reusable.");
/*     */           } else {
/* 288 */             this.log.debug("Released connection is not reusable.");
/*     */           } 
/*     */         }
/* 291 */         hca.detach();
/* 292 */         this.pool.freeEntry(entry, reusable, validDuration, timeUnit);
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void shutdown() {
/* 299 */     this.log.debug("Shutting down");
/* 300 */     this.pool.shutdown();
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
/*     */   public int getConnectionsInPool(HttpRoute route) {
/* 314 */     return this.pool.getConnectionsInPool(route);
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
/*     */   public int getConnectionsInPool() {
/* 326 */     return this.pool.getConnectionsInPool();
/*     */   }
/*     */ 
/*     */   
/*     */   public void closeIdleConnections(long idleTimeout, TimeUnit timeUnit) {
/* 331 */     if (this.log.isDebugEnabled()) {
/* 332 */       this.log.debug("Closing connections idle longer than " + idleTimeout + " " + timeUnit);
/*     */     }
/* 334 */     this.pool.closeIdleConnections(idleTimeout, timeUnit);
/*     */   }
/*     */ 
/*     */   
/*     */   public void closeExpiredConnections() {
/* 339 */     this.log.debug("Closing expired connections");
/* 340 */     this.pool.closeExpiredConnections();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getMaxTotal() {
/* 347 */     return this.pool.getMaxTotalConnections();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setMaxTotal(int max) {
/* 354 */     this.pool.setMaxTotalConnections(max);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getDefaultMaxPerRoute() {
/* 361 */     return this.connPerRoute.getDefaultMaxPerRoute();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setDefaultMaxPerRoute(int max) {
/* 368 */     this.connPerRoute.setDefaultMaxPerRoute(max);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getMaxForRoute(HttpRoute route) {
/* 375 */     return this.connPerRoute.getMaxForRoute(route);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setMaxForRoute(HttpRoute route, int max) {
/* 382 */     this.connPerRoute.setMaxForRoute(route, max);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\impl\conn\tsccm\ThreadSafeClientConnManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */