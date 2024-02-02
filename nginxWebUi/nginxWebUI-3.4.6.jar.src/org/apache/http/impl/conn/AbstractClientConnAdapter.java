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
/*     */ import org.apache.http.HttpRequest;
/*     */ import org.apache.http.HttpResponse;
/*     */ import org.apache.http.conn.ClientConnectionManager;
/*     */ import org.apache.http.conn.ManagedClientConnection;
/*     */ import org.apache.http.conn.OperatedClientConnection;
/*     */ import org.apache.http.protocol.HttpContext;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
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
/*     */ public abstract class AbstractClientConnAdapter
/*     */   implements ManagedClientConnection, HttpContext
/*     */ {
/*     */   private final ClientConnectionManager connManager;
/*     */   private volatile OperatedClientConnection wrappedConnection;
/*     */   private volatile boolean markedReusable;
/*     */   private volatile boolean released;
/*     */   private volatile long duration;
/*     */   
/*     */   protected AbstractClientConnAdapter(ClientConnectionManager mgr, OperatedClientConnection conn) {
/* 102 */     this.connManager = mgr;
/* 103 */     this.wrappedConnection = conn;
/* 104 */     this.markedReusable = false;
/* 105 */     this.released = false;
/* 106 */     this.duration = Long.MAX_VALUE;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected synchronized void detach() {
/* 114 */     this.wrappedConnection = null;
/* 115 */     this.duration = Long.MAX_VALUE;
/*     */   }
/*     */   
/*     */   protected OperatedClientConnection getWrappedConnection() {
/* 119 */     return this.wrappedConnection;
/*     */   }
/*     */   
/*     */   protected ClientConnectionManager getManager() {
/* 123 */     return this.connManager;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   protected final void assertNotAborted() throws InterruptedIOException {
/* 131 */     if (isReleased()) {
/* 132 */       throw new InterruptedIOException("Connection has been shut down");
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected boolean isReleased() {
/* 141 */     return this.released;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected final void assertValid(OperatedClientConnection wrappedConn) throws ConnectionShutdownException {
/* 152 */     if (isReleased() || wrappedConn == null) {
/* 153 */       throw new ConnectionShutdownException();
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isOpen() {
/* 159 */     OperatedClientConnection conn = getWrappedConnection();
/* 160 */     if (conn == null) {
/* 161 */       return false;
/*     */     }
/*     */     
/* 164 */     return conn.isOpen();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isStale() {
/* 169 */     if (isReleased()) {
/* 170 */       return true;
/*     */     }
/* 172 */     OperatedClientConnection conn = getWrappedConnection();
/* 173 */     if (conn == null) {
/* 174 */       return true;
/*     */     }
/*     */     
/* 177 */     return conn.isStale();
/*     */   }
/*     */ 
/*     */   
/*     */   public void setSocketTimeout(int timeout) {
/* 182 */     OperatedClientConnection conn = getWrappedConnection();
/* 183 */     assertValid(conn);
/* 184 */     conn.setSocketTimeout(timeout);
/*     */   }
/*     */ 
/*     */   
/*     */   public int getSocketTimeout() {
/* 189 */     OperatedClientConnection conn = getWrappedConnection();
/* 190 */     assertValid(conn);
/* 191 */     return conn.getSocketTimeout();
/*     */   }
/*     */ 
/*     */   
/*     */   public HttpConnectionMetrics getMetrics() {
/* 196 */     OperatedClientConnection conn = getWrappedConnection();
/* 197 */     assertValid(conn);
/* 198 */     return conn.getMetrics();
/*     */   }
/*     */ 
/*     */   
/*     */   public void flush() throws IOException {
/* 203 */     OperatedClientConnection conn = getWrappedConnection();
/* 204 */     assertValid(conn);
/* 205 */     conn.flush();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isResponseAvailable(int timeout) throws IOException {
/* 210 */     OperatedClientConnection conn = getWrappedConnection();
/* 211 */     assertValid(conn);
/* 212 */     return conn.isResponseAvailable(timeout);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void receiveResponseEntity(HttpResponse response) throws HttpException, IOException {
/* 218 */     OperatedClientConnection conn = getWrappedConnection();
/* 219 */     assertValid(conn);
/* 220 */     unmarkReusable();
/* 221 */     conn.receiveResponseEntity(response);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public HttpResponse receiveResponseHeader() throws HttpException, IOException {
/* 227 */     OperatedClientConnection conn = getWrappedConnection();
/* 228 */     assertValid(conn);
/* 229 */     unmarkReusable();
/* 230 */     return conn.receiveResponseHeader();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void sendRequestEntity(HttpEntityEnclosingRequest request) throws HttpException, IOException {
/* 236 */     OperatedClientConnection conn = getWrappedConnection();
/* 237 */     assertValid(conn);
/* 238 */     unmarkReusable();
/* 239 */     conn.sendRequestEntity(request);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void sendRequestHeader(HttpRequest request) throws HttpException, IOException {
/* 245 */     OperatedClientConnection conn = getWrappedConnection();
/* 246 */     assertValid(conn);
/* 247 */     unmarkReusable();
/* 248 */     conn.sendRequestHeader(request);
/*     */   }
/*     */ 
/*     */   
/*     */   public InetAddress getLocalAddress() {
/* 253 */     OperatedClientConnection conn = getWrappedConnection();
/* 254 */     assertValid(conn);
/* 255 */     return conn.getLocalAddress();
/*     */   }
/*     */ 
/*     */   
/*     */   public int getLocalPort() {
/* 260 */     OperatedClientConnection conn = getWrappedConnection();
/* 261 */     assertValid(conn);
/* 262 */     return conn.getLocalPort();
/*     */   }
/*     */ 
/*     */   
/*     */   public InetAddress getRemoteAddress() {
/* 267 */     OperatedClientConnection conn = getWrappedConnection();
/* 268 */     assertValid(conn);
/* 269 */     return conn.getRemoteAddress();
/*     */   }
/*     */ 
/*     */   
/*     */   public int getRemotePort() {
/* 274 */     OperatedClientConnection conn = getWrappedConnection();
/* 275 */     assertValid(conn);
/* 276 */     return conn.getRemotePort();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isSecure() {
/* 281 */     OperatedClientConnection conn = getWrappedConnection();
/* 282 */     assertValid(conn);
/* 283 */     return conn.isSecure();
/*     */   }
/*     */ 
/*     */   
/*     */   public void bind(Socket socket) throws IOException {
/* 288 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   
/*     */   public Socket getSocket() {
/* 293 */     OperatedClientConnection conn = getWrappedConnection();
/* 294 */     assertValid(conn);
/* 295 */     if (!isOpen()) {
/* 296 */       return null;
/*     */     }
/* 298 */     return conn.getSocket();
/*     */   }
/*     */ 
/*     */   
/*     */   public SSLSession getSSLSession() {
/* 303 */     OperatedClientConnection conn = getWrappedConnection();
/* 304 */     assertValid(conn);
/* 305 */     if (!isOpen()) {
/* 306 */       return null;
/*     */     }
/*     */     
/* 309 */     SSLSession result = null;
/* 310 */     Socket sock = conn.getSocket();
/* 311 */     if (sock instanceof SSLSocket) {
/* 312 */       result = ((SSLSocket)sock).getSession();
/*     */     }
/* 314 */     return result;
/*     */   }
/*     */ 
/*     */   
/*     */   public void markReusable() {
/* 319 */     this.markedReusable = true;
/*     */   }
/*     */ 
/*     */   
/*     */   public void unmarkReusable() {
/* 324 */     this.markedReusable = false;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isMarkedReusable() {
/* 329 */     return this.markedReusable;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setIdleDuration(long duration, TimeUnit unit) {
/* 334 */     if (duration > 0L) {
/* 335 */       this.duration = unit.toMillis(duration);
/*     */     } else {
/* 337 */       this.duration = -1L;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public synchronized void releaseConnection() {
/* 343 */     if (this.released) {
/*     */       return;
/*     */     }
/* 346 */     this.released = true;
/* 347 */     this.connManager.releaseConnection(this, this.duration, TimeUnit.MILLISECONDS);
/*     */   }
/*     */ 
/*     */   
/*     */   public synchronized void abortConnection() {
/* 352 */     if (this.released) {
/*     */       return;
/*     */     }
/* 355 */     this.released = true;
/* 356 */     unmarkReusable();
/*     */     try {
/* 358 */       shutdown();
/* 359 */     } catch (IOException ignore) {}
/*     */     
/* 361 */     this.connManager.releaseConnection(this, this.duration, TimeUnit.MILLISECONDS);
/*     */   }
/*     */ 
/*     */   
/*     */   public Object getAttribute(String id) {
/* 366 */     OperatedClientConnection conn = getWrappedConnection();
/* 367 */     assertValid(conn);
/* 368 */     if (conn instanceof HttpContext) {
/* 369 */       return ((HttpContext)conn).getAttribute(id);
/*     */     }
/* 371 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Object removeAttribute(String id) {
/* 377 */     OperatedClientConnection conn = getWrappedConnection();
/* 378 */     assertValid(conn);
/* 379 */     if (conn instanceof HttpContext) {
/* 380 */       return ((HttpContext)conn).removeAttribute(id);
/*     */     }
/* 382 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void setAttribute(String id, Object obj) {
/* 388 */     OperatedClientConnection conn = getWrappedConnection();
/* 389 */     assertValid(conn);
/* 390 */     if (conn instanceof HttpContext)
/* 391 */       ((HttpContext)conn).setAttribute(id, obj); 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\impl\conn\AbstractClientConnAdapter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */