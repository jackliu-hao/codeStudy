/*     */ package org.apache.http.impl.conn;
/*     */ 
/*     */ import java.io.Closeable;
/*     */ import java.io.IOException;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.concurrent.CancellationException;
/*     */ import java.util.concurrent.ConcurrentHashMap;
/*     */ import java.util.concurrent.ExecutionException;
/*     */ import java.util.concurrent.Future;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import java.util.concurrent.TimeoutException;
/*     */ import java.util.concurrent.atomic.AtomicBoolean;
/*     */ import org.apache.commons.logging.Log;
/*     */ import org.apache.commons.logging.LogFactory;
/*     */ import org.apache.http.HttpClientConnection;
/*     */ import org.apache.http.HttpHost;
/*     */ import org.apache.http.annotation.Contract;
/*     */ import org.apache.http.annotation.ThreadingBehavior;
/*     */ import org.apache.http.config.ConnectionConfig;
/*     */ import org.apache.http.config.Lookup;
/*     */ import org.apache.http.config.Registry;
/*     */ import org.apache.http.config.RegistryBuilder;
/*     */ import org.apache.http.config.SocketConfig;
/*     */ import org.apache.http.conn.ConnectionPoolTimeoutException;
/*     */ import org.apache.http.conn.ConnectionRequest;
/*     */ import org.apache.http.conn.DnsResolver;
/*     */ import org.apache.http.conn.HttpClientConnectionManager;
/*     */ import org.apache.http.conn.HttpClientConnectionOperator;
/*     */ import org.apache.http.conn.HttpConnectionFactory;
/*     */ import org.apache.http.conn.ManagedHttpClientConnection;
/*     */ import org.apache.http.conn.SchemePortResolver;
/*     */ import org.apache.http.conn.routing.HttpRoute;
/*     */ import org.apache.http.conn.socket.ConnectionSocketFactory;
/*     */ import org.apache.http.conn.socket.PlainConnectionSocketFactory;
/*     */ import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
/*     */ import org.apache.http.pool.ConnFactory;
/*     */ import org.apache.http.pool.ConnPoolControl;
/*     */ import org.apache.http.pool.PoolEntry;
/*     */ import org.apache.http.pool.PoolEntryCallback;
/*     */ import org.apache.http.pool.PoolStats;
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
/*     */ @Contract(threading = ThreadingBehavior.SAFE_CONDITIONAL)
/*     */ public class PoolingHttpClientConnectionManager
/*     */   implements HttpClientConnectionManager, ConnPoolControl<HttpRoute>, Closeable
/*     */ {
/* 108 */   private final Log log = LogFactory.getLog(getClass());
/*     */   
/*     */   private final ConfigData configData;
/*     */   private final CPool pool;
/*     */   private final HttpClientConnectionOperator connectionOperator;
/*     */   private final AtomicBoolean isShutDown;
/*     */   
/*     */   private static Registry<ConnectionSocketFactory> getDefaultRegistry() {
/* 116 */     return RegistryBuilder.create().register("http", PlainConnectionSocketFactory.getSocketFactory()).register("https", SSLConnectionSocketFactory.getSocketFactory()).build();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public PoolingHttpClientConnectionManager() {
/* 123 */     this(getDefaultRegistry());
/*     */   }
/*     */   
/*     */   public PoolingHttpClientConnectionManager(long timeToLive, TimeUnit timeUnit) {
/* 127 */     this(getDefaultRegistry(), null, null, null, timeToLive, timeUnit);
/*     */   }
/*     */ 
/*     */   
/*     */   public PoolingHttpClientConnectionManager(Registry<ConnectionSocketFactory> socketFactoryRegistry) {
/* 132 */     this(socketFactoryRegistry, null, null);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public PoolingHttpClientConnectionManager(Registry<ConnectionSocketFactory> socketFactoryRegistry, DnsResolver dnsResolver) {
/* 138 */     this(socketFactoryRegistry, null, dnsResolver);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public PoolingHttpClientConnectionManager(Registry<ConnectionSocketFactory> socketFactoryRegistry, HttpConnectionFactory<HttpRoute, ManagedHttpClientConnection> connFactory) {
/* 144 */     this(socketFactoryRegistry, connFactory, null);
/*     */   }
/*     */ 
/*     */   
/*     */   public PoolingHttpClientConnectionManager(HttpConnectionFactory<HttpRoute, ManagedHttpClientConnection> connFactory) {
/* 149 */     this(getDefaultRegistry(), connFactory, null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public PoolingHttpClientConnectionManager(Registry<ConnectionSocketFactory> socketFactoryRegistry, HttpConnectionFactory<HttpRoute, ManagedHttpClientConnection> connFactory, DnsResolver dnsResolver) {
/* 156 */     this(socketFactoryRegistry, connFactory, null, dnsResolver, -1L, TimeUnit.MILLISECONDS);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public PoolingHttpClientConnectionManager(Registry<ConnectionSocketFactory> socketFactoryRegistry, HttpConnectionFactory<HttpRoute, ManagedHttpClientConnection> connFactory, SchemePortResolver schemePortResolver, DnsResolver dnsResolver, long timeToLive, TimeUnit timeUnit) {
/* 165 */     this(new DefaultHttpClientConnectionOperator((Lookup<ConnectionSocketFactory>)socketFactoryRegistry, schemePortResolver, dnsResolver), connFactory, timeToLive, timeUnit);
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
/*     */   public PoolingHttpClientConnectionManager(HttpClientConnectionOperator httpClientConnectionOperator, HttpConnectionFactory<HttpRoute, ManagedHttpClientConnection> connFactory, long timeToLive, TimeUnit timeUnit) {
/* 180 */     this.configData = new ConfigData();
/* 181 */     this.pool = new CPool(new InternalConnectionFactory(this.configData, connFactory), 2, 20, timeToLive, timeUnit);
/*     */     
/* 183 */     this.pool.setValidateAfterInactivity(2000);
/* 184 */     this.connectionOperator = (HttpClientConnectionOperator)Args.notNull(httpClientConnectionOperator, "HttpClientConnectionOperator");
/* 185 */     this.isShutDown = new AtomicBoolean(false);
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
/*     */   PoolingHttpClientConnectionManager(CPool pool, Lookup<ConnectionSocketFactory> socketFactoryRegistry, SchemePortResolver schemePortResolver, DnsResolver dnsResolver) {
/* 197 */     this.configData = new ConfigData();
/* 198 */     this.pool = pool;
/* 199 */     this.connectionOperator = new DefaultHttpClientConnectionOperator(socketFactoryRegistry, schemePortResolver, dnsResolver);
/*     */     
/* 201 */     this.isShutDown = new AtomicBoolean(false);
/*     */   }
/*     */ 
/*     */   
/*     */   protected void finalize() throws Throwable {
/*     */     try {
/* 207 */       shutdown();
/*     */     } finally {
/* 209 */       super.finalize();
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void close() {
/* 215 */     shutdown();
/*     */   }
/*     */   
/*     */   private String format(HttpRoute route, Object state) {
/* 219 */     StringBuilder buf = new StringBuilder();
/* 220 */     buf.append("[route: ").append(route).append("]");
/* 221 */     if (state != null) {
/* 222 */       buf.append("[state: ").append(state).append("]");
/*     */     }
/* 224 */     return buf.toString();
/*     */   }
/*     */   
/*     */   private String formatStats(HttpRoute route) {
/* 228 */     StringBuilder buf = new StringBuilder();
/* 229 */     PoolStats totals = this.pool.getTotalStats();
/* 230 */     PoolStats stats = this.pool.getStats(route);
/* 231 */     buf.append("[total available: ").append(totals.getAvailable()).append("; ");
/* 232 */     buf.append("route allocated: ").append(stats.getLeased() + stats.getAvailable());
/* 233 */     buf.append(" of ").append(stats.getMax()).append("; ");
/* 234 */     buf.append("total allocated: ").append(totals.getLeased() + totals.getAvailable());
/* 235 */     buf.append(" of ").append(totals.getMax()).append("]");
/* 236 */     return buf.toString();
/*     */   }
/*     */   
/*     */   private String format(CPoolEntry entry) {
/* 240 */     StringBuilder buf = new StringBuilder();
/* 241 */     buf.append("[id: ").append(entry.getId()).append("]");
/* 242 */     buf.append("[route: ").append(entry.getRoute()).append("]");
/* 243 */     Object state = entry.getState();
/* 244 */     if (state != null) {
/* 245 */       buf.append("[state: ").append(state).append("]");
/*     */     }
/* 247 */     return buf.toString();
/*     */   }
/*     */   
/*     */   private SocketConfig resolveSocketConfig(HttpHost host) {
/* 251 */     SocketConfig socketConfig = this.configData.getSocketConfig(host);
/* 252 */     if (socketConfig == null) {
/* 253 */       socketConfig = this.configData.getDefaultSocketConfig();
/*     */     }
/* 255 */     if (socketConfig == null) {
/* 256 */       socketConfig = SocketConfig.DEFAULT;
/*     */     }
/* 258 */     return socketConfig;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ConnectionRequest requestConnection(final HttpRoute route, Object state) {
/* 265 */     Args.notNull(route, "HTTP route");
/* 266 */     if (this.log.isDebugEnabled()) {
/* 267 */       this.log.debug("Connection request: " + format(route, state) + formatStats(route));
/*     */     }
/* 269 */     Asserts.check(!this.isShutDown.get(), "Connection pool shut down");
/* 270 */     final Future<CPoolEntry> future = this.pool.lease(route, state, null);
/* 271 */     return new ConnectionRequest()
/*     */       {
/*     */         public boolean cancel()
/*     */         {
/* 275 */           return future.cancel(true);
/*     */         }
/*     */ 
/*     */ 
/*     */ 
/*     */         
/*     */         public HttpClientConnection get(long timeout, TimeUnit timeUnit) throws InterruptedException, ExecutionException, ConnectionPoolTimeoutException {
/* 282 */           HttpClientConnection conn = PoolingHttpClientConnectionManager.this.leaseConnection(future, timeout, timeUnit);
/* 283 */           if (conn.isOpen()) {
/*     */             HttpHost host;
/* 285 */             if (route.getProxyHost() != null) {
/* 286 */               host = route.getProxyHost();
/*     */             } else {
/* 288 */               host = route.getTargetHost();
/*     */             } 
/* 290 */             SocketConfig socketConfig = PoolingHttpClientConnectionManager.this.resolveSocketConfig(host);
/* 291 */             conn.setSocketTimeout(socketConfig.getSoTimeout());
/*     */           } 
/* 293 */           return conn;
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
/*     */   protected HttpClientConnection leaseConnection(Future<CPoolEntry> future, long timeout, TimeUnit timeUnit) throws InterruptedException, ExecutionException, ConnectionPoolTimeoutException {
/*     */     try {
/* 306 */       CPoolEntry entry = future.get(timeout, timeUnit);
/* 307 */       if (entry == null || future.isCancelled()) {
/* 308 */         throw new ExecutionException(new CancellationException("Operation cancelled"));
/*     */       }
/* 310 */       Asserts.check((entry.getConnection() != null), "Pool entry with no connection");
/* 311 */       if (this.log.isDebugEnabled()) {
/* 312 */         this.log.debug("Connection leased: " + format(entry) + formatStats((HttpRoute)entry.getRoute()));
/*     */       }
/* 314 */       return CPoolProxy.newProxy(entry);
/* 315 */     } catch (TimeoutException ex) {
/* 316 */       throw new ConnectionPoolTimeoutException("Timeout waiting for connection from pool");
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void releaseConnection(HttpClientConnection managedConn, Object state, long keepalive, TimeUnit timeUnit) {
/* 325 */     Args.notNull(managedConn, "Managed connection");
/* 326 */     synchronized (managedConn) {
/* 327 */       CPoolEntry entry = CPoolProxy.detach(managedConn);
/* 328 */       if (entry == null) {
/*     */         return;
/*     */       }
/* 331 */       ManagedHttpClientConnection conn = (ManagedHttpClientConnection)entry.getConnection();
/*     */       try {
/* 333 */         if (conn.isOpen()) {
/* 334 */           TimeUnit effectiveUnit = (timeUnit != null) ? timeUnit : TimeUnit.MILLISECONDS;
/* 335 */           entry.setState(state);
/* 336 */           entry.updateExpiry(keepalive, effectiveUnit);
/* 337 */           if (this.log.isDebugEnabled()) {
/*     */             String s;
/* 339 */             if (keepalive > 0L) {
/* 340 */               s = "for " + (effectiveUnit.toMillis(keepalive) / 1000.0D) + " seconds";
/*     */             } else {
/* 342 */               s = "indefinitely";
/*     */             } 
/* 344 */             this.log.debug("Connection " + format(entry) + " can be kept alive " + s);
/*     */           } 
/* 346 */           conn.setSocketTimeout(0);
/*     */         } 
/*     */       } finally {
/* 349 */         this.pool.release(entry, (conn.isOpen() && entry.isRouteComplete()));
/* 350 */         if (this.log.isDebugEnabled()) {
/* 351 */           this.log.debug("Connection released: " + format(entry) + formatStats((HttpRoute)entry.getRoute()));
/*     */         }
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void connect(HttpClientConnection managedConn, HttpRoute route, int connectTimeout, HttpContext context) throws IOException {
/*     */     ManagedHttpClientConnection conn;
/*     */     HttpHost host;
/* 363 */     Args.notNull(managedConn, "Managed Connection");
/* 364 */     Args.notNull(route, "HTTP route");
/*     */     
/* 366 */     synchronized (managedConn) {
/* 367 */       CPoolEntry entry = CPoolProxy.getPoolEntry(managedConn);
/* 368 */       conn = (ManagedHttpClientConnection)entry.getConnection();
/*     */     } 
/*     */     
/* 371 */     if (route.getProxyHost() != null) {
/* 372 */       host = route.getProxyHost();
/*     */     } else {
/* 374 */       host = route.getTargetHost();
/*     */     } 
/* 376 */     this.connectionOperator.connect(conn, host, route.getLocalSocketAddress(), connectTimeout, resolveSocketConfig(host), context);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void upgrade(HttpClientConnection managedConn, HttpRoute route, HttpContext context) throws IOException {
/*     */     ManagedHttpClientConnection conn;
/* 385 */     Args.notNull(managedConn, "Managed Connection");
/* 386 */     Args.notNull(route, "HTTP route");
/*     */     
/* 388 */     synchronized (managedConn) {
/* 389 */       CPoolEntry entry = CPoolProxy.getPoolEntry(managedConn);
/* 390 */       conn = (ManagedHttpClientConnection)entry.getConnection();
/*     */     } 
/* 392 */     this.connectionOperator.upgrade(conn, route.getTargetHost(), context);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void routeComplete(HttpClientConnection managedConn, HttpRoute route, HttpContext context) throws IOException {
/* 400 */     Args.notNull(managedConn, "Managed Connection");
/* 401 */     Args.notNull(route, "HTTP route");
/* 402 */     synchronized (managedConn) {
/* 403 */       CPoolEntry entry = CPoolProxy.getPoolEntry(managedConn);
/* 404 */       entry.markRouteComplete();
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void shutdown() {
/* 410 */     if (this.isShutDown.compareAndSet(false, true)) {
/* 411 */       this.log.debug("Connection manager is shutting down");
/*     */       try {
/* 413 */         this.pool.enumLeased(new PoolEntryCallback<HttpRoute, ManagedHttpClientConnection>()
/*     */             {
/*     */               public void process(PoolEntry<HttpRoute, ManagedHttpClientConnection> entry)
/*     */               {
/* 417 */                 ManagedHttpClientConnection connection = (ManagedHttpClientConnection)entry.getConnection();
/* 418 */                 if (connection != null) {
/*     */                   try {
/* 420 */                     connection.shutdown();
/* 421 */                   } catch (IOException iox) {
/* 422 */                     if (PoolingHttpClientConnectionManager.this.log.isDebugEnabled()) {
/* 423 */                       PoolingHttpClientConnectionManager.this.log.debug("I/O exception shutting down connection", iox);
/*     */                     }
/*     */                   } 
/*     */                 }
/*     */               }
/*     */             });
/*     */         
/* 430 */         this.pool.shutdown();
/* 431 */       } catch (IOException ex) {
/* 432 */         this.log.debug("I/O exception shutting down connection manager", ex);
/*     */       } 
/* 434 */       this.log.debug("Connection manager shut down");
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void closeIdleConnections(long idleTimeout, TimeUnit timeUnit) {
/* 440 */     if (this.log.isDebugEnabled()) {
/* 441 */       this.log.debug("Closing connections idle longer than " + idleTimeout + " " + timeUnit);
/*     */     }
/* 443 */     this.pool.closeIdle(idleTimeout, timeUnit);
/*     */   }
/*     */ 
/*     */   
/*     */   public void closeExpiredConnections() {
/* 448 */     this.log.debug("Closing expired connections");
/* 449 */     this.pool.closeExpired();
/*     */   }
/*     */   
/*     */   protected void enumAvailable(PoolEntryCallback<HttpRoute, ManagedHttpClientConnection> callback) {
/* 453 */     this.pool.enumAvailable(callback);
/*     */   }
/*     */   
/*     */   protected void enumLeased(PoolEntryCallback<HttpRoute, ManagedHttpClientConnection> callback) {
/* 457 */     this.pool.enumLeased(callback);
/*     */   }
/*     */ 
/*     */   
/*     */   public int getMaxTotal() {
/* 462 */     return this.pool.getMaxTotal();
/*     */   }
/*     */ 
/*     */   
/*     */   public void setMaxTotal(int max) {
/* 467 */     this.pool.setMaxTotal(max);
/*     */   }
/*     */ 
/*     */   
/*     */   public int getDefaultMaxPerRoute() {
/* 472 */     return this.pool.getDefaultMaxPerRoute();
/*     */   }
/*     */ 
/*     */   
/*     */   public void setDefaultMaxPerRoute(int max) {
/* 477 */     this.pool.setDefaultMaxPerRoute(max);
/*     */   }
/*     */ 
/*     */   
/*     */   public int getMaxPerRoute(HttpRoute route) {
/* 482 */     return this.pool.getMaxPerRoute(route);
/*     */   }
/*     */ 
/*     */   
/*     */   public void setMaxPerRoute(HttpRoute route, int max) {
/* 487 */     this.pool.setMaxPerRoute(route, max);
/*     */   }
/*     */ 
/*     */   
/*     */   public PoolStats getTotalStats() {
/* 492 */     return this.pool.getTotalStats();
/*     */   }
/*     */ 
/*     */   
/*     */   public PoolStats getStats(HttpRoute route) {
/* 497 */     return this.pool.getStats(route);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Set<HttpRoute> getRoutes() {
/* 504 */     return this.pool.getRoutes();
/*     */   }
/*     */   
/*     */   public SocketConfig getDefaultSocketConfig() {
/* 508 */     return this.configData.getDefaultSocketConfig();
/*     */   }
/*     */   
/*     */   public void setDefaultSocketConfig(SocketConfig defaultSocketConfig) {
/* 512 */     this.configData.setDefaultSocketConfig(defaultSocketConfig);
/*     */   }
/*     */   
/*     */   public ConnectionConfig getDefaultConnectionConfig() {
/* 516 */     return this.configData.getDefaultConnectionConfig();
/*     */   }
/*     */   
/*     */   public void setDefaultConnectionConfig(ConnectionConfig defaultConnectionConfig) {
/* 520 */     this.configData.setDefaultConnectionConfig(defaultConnectionConfig);
/*     */   }
/*     */   
/*     */   public SocketConfig getSocketConfig(HttpHost host) {
/* 524 */     return this.configData.getSocketConfig(host);
/*     */   }
/*     */   
/*     */   public void setSocketConfig(HttpHost host, SocketConfig socketConfig) {
/* 528 */     this.configData.setSocketConfig(host, socketConfig);
/*     */   }
/*     */   
/*     */   public ConnectionConfig getConnectionConfig(HttpHost host) {
/* 532 */     return this.configData.getConnectionConfig(host);
/*     */   }
/*     */   
/*     */   public void setConnectionConfig(HttpHost host, ConnectionConfig connectionConfig) {
/* 536 */     this.configData.setConnectionConfig(host, connectionConfig);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getValidateAfterInactivity() {
/* 545 */     return this.pool.getValidateAfterInactivity();
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
/*     */   public void setValidateAfterInactivity(int ms) {
/* 560 */     this.pool.setValidateAfterInactivity(ms);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static class ConfigData
/*     */   {
/* 572 */     private final Map<HttpHost, SocketConfig> socketConfigMap = new ConcurrentHashMap<HttpHost, SocketConfig>();
/* 573 */     private final Map<HttpHost, ConnectionConfig> connectionConfigMap = new ConcurrentHashMap<HttpHost, ConnectionConfig>();
/*     */     private volatile SocketConfig defaultSocketConfig;
/*     */     
/*     */     public SocketConfig getDefaultSocketConfig() {
/* 577 */       return this.defaultSocketConfig;
/*     */     }
/*     */     private volatile ConnectionConfig defaultConnectionConfig;
/*     */     public void setDefaultSocketConfig(SocketConfig defaultSocketConfig) {
/* 581 */       this.defaultSocketConfig = defaultSocketConfig;
/*     */     }
/*     */     
/*     */     public ConnectionConfig getDefaultConnectionConfig() {
/* 585 */       return this.defaultConnectionConfig;
/*     */     }
/*     */     
/*     */     public void setDefaultConnectionConfig(ConnectionConfig defaultConnectionConfig) {
/* 589 */       this.defaultConnectionConfig = defaultConnectionConfig;
/*     */     }
/*     */     
/*     */     public SocketConfig getSocketConfig(HttpHost host) {
/* 593 */       return this.socketConfigMap.get(host);
/*     */     }
/*     */     
/*     */     public void setSocketConfig(HttpHost host, SocketConfig socketConfig) {
/* 597 */       this.socketConfigMap.put(host, socketConfig);
/*     */     }
/*     */     
/*     */     public ConnectionConfig getConnectionConfig(HttpHost host) {
/* 601 */       return this.connectionConfigMap.get(host);
/*     */     }
/*     */     
/*     */     public void setConnectionConfig(HttpHost host, ConnectionConfig connectionConfig) {
/* 605 */       this.connectionConfigMap.put(host, connectionConfig);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   static class InternalConnectionFactory
/*     */     implements ConnFactory<HttpRoute, ManagedHttpClientConnection>
/*     */   {
/*     */     private final PoolingHttpClientConnectionManager.ConfigData configData;
/*     */     
/*     */     private final HttpConnectionFactory<HttpRoute, ManagedHttpClientConnection> connFactory;
/*     */ 
/*     */     
/*     */     InternalConnectionFactory(PoolingHttpClientConnectionManager.ConfigData configData, HttpConnectionFactory<HttpRoute, ManagedHttpClientConnection> connFactory) {
/* 619 */       this.configData = (configData != null) ? configData : new PoolingHttpClientConnectionManager.ConfigData();
/* 620 */       this.connFactory = (connFactory != null) ? connFactory : ManagedHttpClientConnectionFactory.INSTANCE;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public ManagedHttpClientConnection create(HttpRoute route) throws IOException {
/* 626 */       ConnectionConfig config = null;
/* 627 */       if (route.getProxyHost() != null) {
/* 628 */         config = this.configData.getConnectionConfig(route.getProxyHost());
/*     */       }
/* 630 */       if (config == null) {
/* 631 */         config = this.configData.getConnectionConfig(route.getTargetHost());
/*     */       }
/* 633 */       if (config == null) {
/* 634 */         config = this.configData.getDefaultConnectionConfig();
/*     */       }
/* 636 */       if (config == null) {
/* 637 */         config = ConnectionConfig.DEFAULT;
/*     */       }
/* 639 */       return (ManagedHttpClientConnection)this.connFactory.create(route, config);
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\impl\conn\PoolingHttpClientConnectionManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */