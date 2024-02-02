/*     */ package org.apache.http.impl.conn;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.util.concurrent.ExecutionException;
/*     */ import java.util.concurrent.Future;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import java.util.concurrent.TimeoutException;
/*     */ import org.apache.commons.logging.Log;
/*     */ import org.apache.commons.logging.LogFactory;
/*     */ import org.apache.http.annotation.Contract;
/*     */ import org.apache.http.annotation.ThreadingBehavior;
/*     */ import org.apache.http.conn.ClientConnectionManager;
/*     */ import org.apache.http.conn.ClientConnectionOperator;
/*     */ import org.apache.http.conn.ClientConnectionRequest;
/*     */ import org.apache.http.conn.ConnectionPoolTimeoutException;
/*     */ import org.apache.http.conn.DnsResolver;
/*     */ import org.apache.http.conn.ManagedClientConnection;
/*     */ import org.apache.http.conn.routing.HttpRoute;
/*     */ import org.apache.http.conn.scheme.SchemeRegistry;
/*     */ import org.apache.http.pool.ConnPoolControl;
/*     */ import org.apache.http.pool.PoolStats;
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
/*     */ @Deprecated
/*     */ @Contract(threading = ThreadingBehavior.SAFE_CONDITIONAL)
/*     */ public class PoolingClientConnectionManager
/*     */   implements ClientConnectionManager, ConnPoolControl<HttpRoute>
/*     */ {
/*  76 */   private final Log log = LogFactory.getLog(getClass());
/*     */   
/*     */   private final SchemeRegistry schemeRegistry;
/*     */   
/*     */   private final HttpConnPool pool;
/*     */   
/*     */   private final ClientConnectionOperator operator;
/*     */   
/*     */   private final DnsResolver dnsResolver;
/*     */ 
/*     */   
/*     */   public PoolingClientConnectionManager(SchemeRegistry schreg) {
/*  88 */     this(schreg, -1L, TimeUnit.MILLISECONDS);
/*     */   }
/*     */   
/*     */   public PoolingClientConnectionManager(SchemeRegistry schreg, DnsResolver dnsResolver) {
/*  92 */     this(schreg, -1L, TimeUnit.MILLISECONDS, dnsResolver);
/*     */   }
/*     */   
/*     */   public PoolingClientConnectionManager() {
/*  96 */     this(SchemeRegistryFactory.createDefault());
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public PoolingClientConnectionManager(SchemeRegistry schemeRegistry, long timeToLive, TimeUnit timeUnit) {
/* 102 */     this(schemeRegistry, timeToLive, timeUnit, new SystemDefaultDnsResolver());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public PoolingClientConnectionManager(SchemeRegistry schemeRegistry, long timeToLive, TimeUnit timeUnit, DnsResolver dnsResolver) {
/* 109 */     Args.notNull(schemeRegistry, "Scheme registry");
/* 110 */     Args.notNull(dnsResolver, "DNS resolver");
/* 111 */     this.schemeRegistry = schemeRegistry;
/* 112 */     this.dnsResolver = dnsResolver;
/* 113 */     this.operator = createConnectionOperator(schemeRegistry);
/* 114 */     this.pool = new HttpConnPool(this.log, this.operator, 2, 20, timeToLive, timeUnit);
/*     */   }
/*     */ 
/*     */   
/*     */   protected void finalize() throws Throwable {
/*     */     try {
/* 120 */       shutdown();
/*     */     } finally {
/* 122 */       super.finalize();
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
/*     */   protected ClientConnectionOperator createConnectionOperator(SchemeRegistry schreg) {
/* 139 */     return new DefaultClientConnectionOperator(schreg, this.dnsResolver);
/*     */   }
/*     */ 
/*     */   
/*     */   public SchemeRegistry getSchemeRegistry() {
/* 144 */     return this.schemeRegistry;
/*     */   }
/*     */   
/*     */   private String format(HttpRoute route, Object state) {
/* 148 */     StringBuilder buf = new StringBuilder();
/* 149 */     buf.append("[route: ").append(route).append("]");
/* 150 */     if (state != null) {
/* 151 */       buf.append("[state: ").append(state).append("]");
/*     */     }
/* 153 */     return buf.toString();
/*     */   }
/*     */   
/*     */   private String formatStats(HttpRoute route) {
/* 157 */     StringBuilder buf = new StringBuilder();
/* 158 */     PoolStats totals = this.pool.getTotalStats();
/* 159 */     PoolStats stats = this.pool.getStats(route);
/* 160 */     buf.append("[total kept alive: ").append(totals.getAvailable()).append("; ");
/* 161 */     buf.append("route allocated: ").append(stats.getLeased() + stats.getAvailable());
/* 162 */     buf.append(" of ").append(stats.getMax()).append("; ");
/* 163 */     buf.append("total allocated: ").append(totals.getLeased() + totals.getAvailable());
/* 164 */     buf.append(" of ").append(totals.getMax()).append("]");
/* 165 */     return buf.toString();
/*     */   }
/*     */   
/*     */   private String format(HttpPoolEntry entry) {
/* 169 */     StringBuilder buf = new StringBuilder();
/* 170 */     buf.append("[id: ").append(entry.getId()).append("]");
/* 171 */     buf.append("[route: ").append(entry.getRoute()).append("]");
/* 172 */     Object state = entry.getState();
/* 173 */     if (state != null) {
/* 174 */       buf.append("[state: ").append(state).append("]");
/*     */     }
/* 176 */     return buf.toString();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ClientConnectionRequest requestConnection(HttpRoute route, Object state) {
/* 183 */     Args.notNull(route, "HTTP route");
/* 184 */     if (this.log.isDebugEnabled()) {
/* 185 */       this.log.debug("Connection request: " + format(route, state) + formatStats(route));
/*     */     }
/* 187 */     final Future<HttpPoolEntry> future = this.pool.lease(route, state);
/*     */     
/* 189 */     return new ClientConnectionRequest()
/*     */       {
/*     */         public void abortRequest()
/*     */         {
/* 193 */           future.cancel(true);
/*     */         }
/*     */ 
/*     */ 
/*     */ 
/*     */         
/*     */         public ManagedClientConnection getConnection(long timeout, TimeUnit timeUnit) throws InterruptedException, ConnectionPoolTimeoutException {
/* 200 */           return PoolingClientConnectionManager.this.leaseConnection(future, timeout, timeUnit);
/*     */         }
/*     */       };
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   ManagedClientConnection leaseConnection(Future<HttpPoolEntry> future, long timeout, TimeUnit timeUnit) throws InterruptedException, ConnectionPoolTimeoutException {
/*     */     try {
/* 213 */       HttpPoolEntry entry = future.get(timeout, timeUnit);
/* 214 */       if (entry == null || future.isCancelled()) {
/* 215 */         throw new InterruptedException();
/*     */       }
/* 217 */       Asserts.check((entry.getConnection() != null), "Pool entry with no connection");
/* 218 */       if (this.log.isDebugEnabled()) {
/* 219 */         this.log.debug("Connection leased: " + format(entry) + formatStats((HttpRoute)entry.getRoute()));
/*     */       }
/* 221 */       return new ManagedClientConnectionImpl(this, this.operator, entry);
/* 222 */     } catch (ExecutionException ex) {
/* 223 */       Throwable cause = ex.getCause();
/* 224 */       if (cause == null) {
/* 225 */         cause = ex;
/*     */       }
/* 227 */       this.log.error("Unexpected exception leasing connection from pool", cause);
/*     */       
/* 229 */       throw new InterruptedException();
/* 230 */     } catch (TimeoutException ex) {
/* 231 */       throw new ConnectionPoolTimeoutException("Timeout waiting for connection from pool");
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void releaseConnection(ManagedClientConnection conn, long keepalive, TimeUnit timeUnit) {
/* 239 */     Args.check(conn instanceof ManagedClientConnectionImpl, "Connection class mismatch, connection not obtained from this manager");
/*     */     
/* 241 */     ManagedClientConnectionImpl managedConn = (ManagedClientConnectionImpl)conn;
/* 242 */     Asserts.check((managedConn.getManager() == this), "Connection not obtained from this manager");
/* 243 */     synchronized (managedConn) {
/* 244 */       HttpPoolEntry entry = managedConn.detach();
/* 245 */       if (entry == null) {
/*     */         return;
/*     */       }
/*     */       try {
/* 249 */         if (managedConn.isOpen() && !managedConn.isMarkedReusable()) {
/*     */           try {
/* 251 */             managedConn.shutdown();
/* 252 */           } catch (IOException iox) {
/* 253 */             if (this.log.isDebugEnabled()) {
/* 254 */               this.log.debug("I/O exception shutting down released connection", iox);
/*     */             }
/*     */           } 
/*     */         }
/*     */         
/* 259 */         if (managedConn.isMarkedReusable()) {
/* 260 */           entry.updateExpiry(keepalive, (timeUnit != null) ? timeUnit : TimeUnit.MILLISECONDS);
/* 261 */           if (this.log.isDebugEnabled()) {
/*     */             String s;
/* 263 */             if (keepalive > 0L) {
/* 264 */               s = "for " + keepalive + " " + timeUnit;
/*     */             } else {
/* 266 */               s = "indefinitely";
/*     */             } 
/* 268 */             this.log.debug("Connection " + format(entry) + " can be kept alive " + s);
/*     */           } 
/*     */         } 
/*     */       } finally {
/* 272 */         this.pool.release(entry, managedConn.isMarkedReusable());
/*     */       } 
/* 274 */       if (this.log.isDebugEnabled()) {
/* 275 */         this.log.debug("Connection released: " + format(entry) + formatStats((HttpRoute)entry.getRoute()));
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void shutdown() {
/* 282 */     this.log.debug("Connection manager is shutting down");
/*     */     try {
/* 284 */       this.pool.shutdown();
/* 285 */     } catch (IOException ex) {
/* 286 */       this.log.debug("I/O exception shutting down connection manager", ex);
/*     */     } 
/* 288 */     this.log.debug("Connection manager shut down");
/*     */   }
/*     */ 
/*     */   
/*     */   public void closeIdleConnections(long idleTimeout, TimeUnit timeUnit) {
/* 293 */     if (this.log.isDebugEnabled()) {
/* 294 */       this.log.debug("Closing connections idle longer than " + idleTimeout + " " + timeUnit);
/*     */     }
/* 296 */     this.pool.closeIdle(idleTimeout, timeUnit);
/*     */   }
/*     */ 
/*     */   
/*     */   public void closeExpiredConnections() {
/* 301 */     this.log.debug("Closing expired connections");
/* 302 */     this.pool.closeExpired();
/*     */   }
/*     */ 
/*     */   
/*     */   public int getMaxTotal() {
/* 307 */     return this.pool.getMaxTotal();
/*     */   }
/*     */ 
/*     */   
/*     */   public void setMaxTotal(int max) {
/* 312 */     this.pool.setMaxTotal(max);
/*     */   }
/*     */ 
/*     */   
/*     */   public int getDefaultMaxPerRoute() {
/* 317 */     return this.pool.getDefaultMaxPerRoute();
/*     */   }
/*     */ 
/*     */   
/*     */   public void setDefaultMaxPerRoute(int max) {
/* 322 */     this.pool.setDefaultMaxPerRoute(max);
/*     */   }
/*     */ 
/*     */   
/*     */   public int getMaxPerRoute(HttpRoute route) {
/* 327 */     return this.pool.getMaxPerRoute(route);
/*     */   }
/*     */ 
/*     */   
/*     */   public void setMaxPerRoute(HttpRoute route, int max) {
/* 332 */     this.pool.setMaxPerRoute(route, max);
/*     */   }
/*     */ 
/*     */   
/*     */   public PoolStats getTotalStats() {
/* 337 */     return this.pool.getTotalStats();
/*     */   }
/*     */ 
/*     */   
/*     */   public PoolStats getStats(HttpRoute route) {
/* 342 */     return this.pool.getStats(route);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\impl\conn\PoolingClientConnectionManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */