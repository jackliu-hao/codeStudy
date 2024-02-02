/*     */ package org.apache.http.impl.conn;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InterruptedIOException;
/*     */ import java.net.InetAddress;
/*     */ import java.net.Socket;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import javax.net.ssl.SSLSession;
/*     */ import javax.net.ssl.SSLSocket;
/*     */ import org.apache.http.HttpConnectionMetrics;
/*     */ import org.apache.http.HttpEntityEnclosingRequest;
/*     */ import org.apache.http.HttpException;
/*     */ import org.apache.http.HttpHost;
/*     */ import org.apache.http.HttpRequest;
/*     */ import org.apache.http.HttpResponse;
/*     */ import org.apache.http.conn.ClientConnectionManager;
/*     */ import org.apache.http.conn.ClientConnectionOperator;
/*     */ import org.apache.http.conn.ManagedClientConnection;
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
/*     */ @Deprecated
/*     */ class ManagedClientConnectionImpl
/*     */   implements ManagedClientConnection
/*     */ {
/*     */   private final ClientConnectionManager manager;
/*     */   private final ClientConnectionOperator operator;
/*     */   private volatile HttpPoolEntry poolEntry;
/*     */   private volatile boolean reusable;
/*     */   private volatile long duration;
/*     */   
/*     */   ManagedClientConnectionImpl(ClientConnectionManager manager, ClientConnectionOperator operator, HttpPoolEntry entry) {
/*  74 */     Args.notNull(manager, "Connection manager");
/*  75 */     Args.notNull(operator, "Connection operator");
/*  76 */     Args.notNull(entry, "HTTP pool entry");
/*  77 */     this.manager = manager;
/*  78 */     this.operator = operator;
/*  79 */     this.poolEntry = entry;
/*  80 */     this.reusable = false;
/*  81 */     this.duration = Long.MAX_VALUE;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getId() {
/*  86 */     return null;
/*     */   }
/*     */   
/*     */   HttpPoolEntry getPoolEntry() {
/*  90 */     return this.poolEntry;
/*     */   }
/*     */   
/*     */   HttpPoolEntry detach() {
/*  94 */     HttpPoolEntry local = this.poolEntry;
/*  95 */     this.poolEntry = null;
/*  96 */     return local;
/*     */   }
/*     */   
/*     */   public ClientConnectionManager getManager() {
/* 100 */     return this.manager;
/*     */   }
/*     */   
/*     */   private OperatedClientConnection getConnection() {
/* 104 */     HttpPoolEntry local = this.poolEntry;
/* 105 */     if (local == null) {
/* 106 */       return null;
/*     */     }
/* 108 */     return (OperatedClientConnection)local.getConnection();
/*     */   }
/*     */   
/*     */   private OperatedClientConnection ensureConnection() {
/* 112 */     HttpPoolEntry local = this.poolEntry;
/* 113 */     if (local == null) {
/* 114 */       throw new ConnectionShutdownException();
/*     */     }
/* 116 */     return (OperatedClientConnection)local.getConnection();
/*     */   }
/*     */   
/*     */   private HttpPoolEntry ensurePoolEntry() {
/* 120 */     HttpPoolEntry local = this.poolEntry;
/* 121 */     if (local == null) {
/* 122 */       throw new ConnectionShutdownException();
/*     */     }
/* 124 */     return local;
/*     */   }
/*     */ 
/*     */   
/*     */   public void close() throws IOException {
/* 129 */     HttpPoolEntry local = this.poolEntry;
/* 130 */     if (local != null) {
/* 131 */       OperatedClientConnection conn = (OperatedClientConnection)local.getConnection();
/* 132 */       local.getTracker().reset();
/* 133 */       conn.close();
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void shutdown() throws IOException {
/* 139 */     HttpPoolEntry local = this.poolEntry;
/* 140 */     if (local != null) {
/* 141 */       OperatedClientConnection conn = (OperatedClientConnection)local.getConnection();
/* 142 */       local.getTracker().reset();
/* 143 */       conn.shutdown();
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isOpen() {
/* 149 */     OperatedClientConnection conn = getConnection();
/* 150 */     if (conn != null) {
/* 151 */       return conn.isOpen();
/*     */     }
/* 153 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isStale() {
/* 159 */     OperatedClientConnection conn = getConnection();
/* 160 */     if (conn != null) {
/* 161 */       return conn.isStale();
/*     */     }
/* 163 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void setSocketTimeout(int timeout) {
/* 169 */     OperatedClientConnection conn = ensureConnection();
/* 170 */     conn.setSocketTimeout(timeout);
/*     */   }
/*     */ 
/*     */   
/*     */   public int getSocketTimeout() {
/* 175 */     OperatedClientConnection conn = ensureConnection();
/* 176 */     return conn.getSocketTimeout();
/*     */   }
/*     */ 
/*     */   
/*     */   public HttpConnectionMetrics getMetrics() {
/* 181 */     OperatedClientConnection conn = ensureConnection();
/* 182 */     return conn.getMetrics();
/*     */   }
/*     */ 
/*     */   
/*     */   public void flush() throws IOException {
/* 187 */     OperatedClientConnection conn = ensureConnection();
/* 188 */     conn.flush();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isResponseAvailable(int timeout) throws IOException {
/* 193 */     OperatedClientConnection conn = ensureConnection();
/* 194 */     return conn.isResponseAvailable(timeout);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void receiveResponseEntity(HttpResponse response) throws HttpException, IOException {
/* 200 */     OperatedClientConnection conn = ensureConnection();
/* 201 */     conn.receiveResponseEntity(response);
/*     */   }
/*     */ 
/*     */   
/*     */   public HttpResponse receiveResponseHeader() throws HttpException, IOException {
/* 206 */     OperatedClientConnection conn = ensureConnection();
/* 207 */     return conn.receiveResponseHeader();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void sendRequestEntity(HttpEntityEnclosingRequest request) throws HttpException, IOException {
/* 213 */     OperatedClientConnection conn = ensureConnection();
/* 214 */     conn.sendRequestEntity(request);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void sendRequestHeader(HttpRequest request) throws HttpException, IOException {
/* 220 */     OperatedClientConnection conn = ensureConnection();
/* 221 */     conn.sendRequestHeader(request);
/*     */   }
/*     */ 
/*     */   
/*     */   public InetAddress getLocalAddress() {
/* 226 */     OperatedClientConnection conn = ensureConnection();
/* 227 */     return conn.getLocalAddress();
/*     */   }
/*     */ 
/*     */   
/*     */   public int getLocalPort() {
/* 232 */     OperatedClientConnection conn = ensureConnection();
/* 233 */     return conn.getLocalPort();
/*     */   }
/*     */ 
/*     */   
/*     */   public InetAddress getRemoteAddress() {
/* 238 */     OperatedClientConnection conn = ensureConnection();
/* 239 */     return conn.getRemoteAddress();
/*     */   }
/*     */ 
/*     */   
/*     */   public int getRemotePort() {
/* 244 */     OperatedClientConnection conn = ensureConnection();
/* 245 */     return conn.getRemotePort();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isSecure() {
/* 250 */     OperatedClientConnection conn = ensureConnection();
/* 251 */     return conn.isSecure();
/*     */   }
/*     */ 
/*     */   
/*     */   public void bind(Socket socket) throws IOException {
/* 256 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   
/*     */   public Socket getSocket() {
/* 261 */     OperatedClientConnection conn = ensureConnection();
/* 262 */     return conn.getSocket();
/*     */   }
/*     */ 
/*     */   
/*     */   public SSLSession getSSLSession() {
/* 267 */     OperatedClientConnection conn = ensureConnection();
/* 268 */     SSLSession result = null;
/* 269 */     Socket sock = conn.getSocket();
/* 270 */     if (sock instanceof SSLSocket) {
/* 271 */       result = ((SSLSocket)sock).getSession();
/*     */     }
/* 273 */     return result;
/*     */   }
/*     */   
/*     */   public Object getAttribute(String id) {
/* 277 */     OperatedClientConnection conn = ensureConnection();
/* 278 */     if (conn instanceof HttpContext) {
/* 279 */       return ((HttpContext)conn).getAttribute(id);
/*     */     }
/* 281 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public Object removeAttribute(String id) {
/* 286 */     OperatedClientConnection conn = ensureConnection();
/* 287 */     if (conn instanceof HttpContext) {
/* 288 */       return ((HttpContext)conn).removeAttribute(id);
/*     */     }
/* 290 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setAttribute(String id, Object obj) {
/* 295 */     OperatedClientConnection conn = ensureConnection();
/* 296 */     if (conn instanceof HttpContext) {
/* 297 */       ((HttpContext)conn).setAttribute(id, obj);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public HttpRoute getRoute() {
/* 303 */     HttpPoolEntry local = ensurePoolEntry();
/* 304 */     return local.getEffectiveRoute();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void open(HttpRoute route, HttpContext context, HttpParams params) throws IOException {
/*     */     OperatedClientConnection conn;
/* 312 */     Args.notNull(route, "Route");
/* 313 */     Args.notNull(params, "HTTP parameters");
/*     */     
/* 315 */     synchronized (this) {
/* 316 */       if (this.poolEntry == null) {
/* 317 */         throw new ConnectionShutdownException();
/*     */       }
/* 319 */       RouteTracker tracker = this.poolEntry.getTracker();
/* 320 */       Asserts.notNull(tracker, "Route tracker");
/* 321 */       Asserts.check(!tracker.isConnected(), "Connection already open");
/* 322 */       conn = (OperatedClientConnection)this.poolEntry.getConnection();
/*     */     } 
/*     */     
/* 325 */     HttpHost proxy = route.getProxyHost();
/* 326 */     this.operator.openConnection(conn, (proxy != null) ? proxy : route.getTargetHost(), route.getLocalAddress(), context, params);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 332 */     synchronized (this) {
/* 333 */       if (this.poolEntry == null) {
/* 334 */         throw new InterruptedIOException();
/*     */       }
/* 336 */       RouteTracker tracker = this.poolEntry.getTracker();
/* 337 */       if (proxy == null) {
/* 338 */         tracker.connectTarget(conn.isSecure());
/*     */       } else {
/* 340 */         tracker.connectProxy(proxy, conn.isSecure());
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   public void tunnelTarget(boolean secure, HttpParams params) throws IOException {
/*     */     HttpHost target;
/*     */     OperatedClientConnection conn;
/* 348 */     Args.notNull(params, "HTTP parameters");
/*     */ 
/*     */     
/* 351 */     synchronized (this) {
/* 352 */       if (this.poolEntry == null) {
/* 353 */         throw new ConnectionShutdownException();
/*     */       }
/* 355 */       RouteTracker tracker = this.poolEntry.getTracker();
/* 356 */       Asserts.notNull(tracker, "Route tracker");
/* 357 */       Asserts.check(tracker.isConnected(), "Connection not open");
/* 358 */       Asserts.check(!tracker.isTunnelled(), "Connection is already tunnelled");
/* 359 */       target = tracker.getTargetHost();
/* 360 */       conn = (OperatedClientConnection)this.poolEntry.getConnection();
/*     */     } 
/*     */     
/* 363 */     conn.update(null, target, secure, params);
/*     */     
/* 365 */     synchronized (this) {
/* 366 */       if (this.poolEntry == null) {
/* 367 */         throw new InterruptedIOException();
/*     */       }
/* 369 */       RouteTracker tracker = this.poolEntry.getTracker();
/* 370 */       tracker.tunnelTarget(secure);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void tunnelProxy(HttpHost next, boolean secure, HttpParams params) throws IOException {
/*     */     OperatedClientConnection conn;
/* 377 */     Args.notNull(next, "Next proxy");
/* 378 */     Args.notNull(params, "HTTP parameters");
/*     */     
/* 380 */     synchronized (this) {
/* 381 */       if (this.poolEntry == null) {
/* 382 */         throw new ConnectionShutdownException();
/*     */       }
/* 384 */       RouteTracker tracker = this.poolEntry.getTracker();
/* 385 */       Asserts.notNull(tracker, "Route tracker");
/* 386 */       Asserts.check(tracker.isConnected(), "Connection not open");
/* 387 */       conn = (OperatedClientConnection)this.poolEntry.getConnection();
/*     */     } 
/*     */     
/* 390 */     conn.update(null, next, secure, params);
/*     */     
/* 392 */     synchronized (this) {
/* 393 */       if (this.poolEntry == null) {
/* 394 */         throw new InterruptedIOException();
/*     */       }
/* 396 */       RouteTracker tracker = this.poolEntry.getTracker();
/* 397 */       tracker.tunnelProxy(next, secure);
/*     */     } 
/*     */   }
/*     */   
/*     */   public void layerProtocol(HttpContext context, HttpParams params) throws IOException {
/*     */     HttpHost target;
/*     */     OperatedClientConnection conn;
/* 404 */     Args.notNull(params, "HTTP parameters");
/*     */ 
/*     */     
/* 407 */     synchronized (this) {
/* 408 */       if (this.poolEntry == null) {
/* 409 */         throw new ConnectionShutdownException();
/*     */       }
/* 411 */       RouteTracker tracker = this.poolEntry.getTracker();
/* 412 */       Asserts.notNull(tracker, "Route tracker");
/* 413 */       Asserts.check(tracker.isConnected(), "Connection not open");
/* 414 */       Asserts.check(tracker.isTunnelled(), "Protocol layering without a tunnel not supported");
/* 415 */       Asserts.check(!tracker.isLayered(), "Multiple protocol layering not supported");
/* 416 */       target = tracker.getTargetHost();
/* 417 */       conn = (OperatedClientConnection)this.poolEntry.getConnection();
/*     */     } 
/* 419 */     this.operator.updateSecureConnection(conn, target, context, params);
/*     */     
/* 421 */     synchronized (this) {
/* 422 */       if (this.poolEntry == null) {
/* 423 */         throw new InterruptedIOException();
/*     */       }
/* 425 */       RouteTracker tracker = this.poolEntry.getTracker();
/* 426 */       tracker.layerProtocol(conn.isSecure());
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public Object getState() {
/* 432 */     HttpPoolEntry local = ensurePoolEntry();
/* 433 */     return local.getState();
/*     */   }
/*     */ 
/*     */   
/*     */   public void setState(Object state) {
/* 438 */     HttpPoolEntry local = ensurePoolEntry();
/* 439 */     local.setState(state);
/*     */   }
/*     */ 
/*     */   
/*     */   public void markReusable() {
/* 444 */     this.reusable = true;
/*     */   }
/*     */ 
/*     */   
/*     */   public void unmarkReusable() {
/* 449 */     this.reusable = false;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isMarkedReusable() {
/* 454 */     return this.reusable;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setIdleDuration(long duration, TimeUnit unit) {
/* 459 */     if (duration > 0L) {
/* 460 */       this.duration = unit.toMillis(duration);
/*     */     } else {
/* 462 */       this.duration = -1L;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void releaseConnection() {
/* 468 */     synchronized (this) {
/* 469 */       if (this.poolEntry == null) {
/*     */         return;
/*     */       }
/* 472 */       this.manager.releaseConnection(this, this.duration, TimeUnit.MILLISECONDS);
/* 473 */       this.poolEntry = null;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void abortConnection() {
/* 479 */     synchronized (this) {
/* 480 */       if (this.poolEntry == null) {
/*     */         return;
/*     */       }
/* 483 */       this.reusable = false;
/* 484 */       OperatedClientConnection conn = (OperatedClientConnection)this.poolEntry.getConnection();
/*     */       try {
/* 486 */         conn.shutdown();
/* 487 */       } catch (IOException ignore) {}
/*     */       
/* 489 */       this.manager.releaseConnection(this, this.duration, TimeUnit.MILLISECONDS);
/* 490 */       this.poolEntry = null;
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\impl\conn\ManagedClientConnectionImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */