/*     */ package org.apache.http.impl.conn;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.net.InetAddress;
/*     */ import java.net.Socket;
/*     */ import javax.net.ssl.SSLSession;
/*     */ import org.apache.http.HttpClientConnection;
/*     */ import org.apache.http.HttpConnectionMetrics;
/*     */ import org.apache.http.HttpEntityEnclosingRequest;
/*     */ import org.apache.http.HttpException;
/*     */ import org.apache.http.HttpRequest;
/*     */ import org.apache.http.HttpResponse;
/*     */ import org.apache.http.conn.ManagedHttpClientConnection;
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
/*     */ class CPoolProxy
/*     */   implements ManagedHttpClientConnection, HttpContext
/*     */ {
/*     */   private volatile CPoolEntry poolEntry;
/*     */   
/*     */   CPoolProxy(CPoolEntry entry) {
/*  53 */     this.poolEntry = entry;
/*     */   }
/*     */   
/*     */   CPoolEntry getPoolEntry() {
/*  57 */     return this.poolEntry;
/*     */   }
/*     */   
/*     */   CPoolEntry detach() {
/*  61 */     CPoolEntry local = this.poolEntry;
/*  62 */     this.poolEntry = null;
/*  63 */     return local;
/*     */   }
/*     */   
/*     */   ManagedHttpClientConnection getConnection() {
/*  67 */     CPoolEntry local = this.poolEntry;
/*  68 */     if (local == null) {
/*  69 */       return null;
/*     */     }
/*  71 */     return (ManagedHttpClientConnection)local.getConnection();
/*     */   }
/*     */   
/*     */   ManagedHttpClientConnection getValidConnection() {
/*  75 */     ManagedHttpClientConnection conn = getConnection();
/*  76 */     if (conn == null) {
/*  77 */       throw new ConnectionShutdownException();
/*     */     }
/*  79 */     return conn;
/*     */   }
/*     */ 
/*     */   
/*     */   public void close() throws IOException {
/*  84 */     CPoolEntry local = this.poolEntry;
/*  85 */     if (local != null) {
/*  86 */       local.closeConnection();
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public void shutdown() throws IOException {
/*  92 */     CPoolEntry local = this.poolEntry;
/*  93 */     if (local != null) {
/*  94 */       local.shutdownConnection();
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isOpen() {
/* 100 */     CPoolEntry local = this.poolEntry;
/* 101 */     return (local != null) ? (!local.isClosed()) : false;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isStale() {
/* 106 */     ManagedHttpClientConnection managedHttpClientConnection = getConnection();
/* 107 */     return (managedHttpClientConnection != null) ? managedHttpClientConnection.isStale() : true;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setSocketTimeout(int timeout) {
/* 112 */     getValidConnection().setSocketTimeout(timeout);
/*     */   }
/*     */ 
/*     */   
/*     */   public int getSocketTimeout() {
/* 117 */     return getValidConnection().getSocketTimeout();
/*     */   }
/*     */ 
/*     */   
/*     */   public String getId() {
/* 122 */     return getValidConnection().getId();
/*     */   }
/*     */ 
/*     */   
/*     */   public void bind(Socket socket) throws IOException {
/* 127 */     getValidConnection().bind(socket);
/*     */   }
/*     */ 
/*     */   
/*     */   public Socket getSocket() {
/* 132 */     return getValidConnection().getSocket();
/*     */   }
/*     */ 
/*     */   
/*     */   public SSLSession getSSLSession() {
/* 137 */     return getValidConnection().getSSLSession();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isResponseAvailable(int timeout) throws IOException {
/* 142 */     return getValidConnection().isResponseAvailable(timeout);
/*     */   }
/*     */ 
/*     */   
/*     */   public void sendRequestHeader(HttpRequest request) throws HttpException, IOException {
/* 147 */     getValidConnection().sendRequestHeader(request);
/*     */   }
/*     */ 
/*     */   
/*     */   public void sendRequestEntity(HttpEntityEnclosingRequest request) throws HttpException, IOException {
/* 152 */     getValidConnection().sendRequestEntity(request);
/*     */   }
/*     */ 
/*     */   
/*     */   public HttpResponse receiveResponseHeader() throws HttpException, IOException {
/* 157 */     return getValidConnection().receiveResponseHeader();
/*     */   }
/*     */ 
/*     */   
/*     */   public void receiveResponseEntity(HttpResponse response) throws HttpException, IOException {
/* 162 */     getValidConnection().receiveResponseEntity(response);
/*     */   }
/*     */ 
/*     */   
/*     */   public void flush() throws IOException {
/* 167 */     getValidConnection().flush();
/*     */   }
/*     */ 
/*     */   
/*     */   public HttpConnectionMetrics getMetrics() {
/* 172 */     return getValidConnection().getMetrics();
/*     */   }
/*     */ 
/*     */   
/*     */   public InetAddress getLocalAddress() {
/* 177 */     return getValidConnection().getLocalAddress();
/*     */   }
/*     */ 
/*     */   
/*     */   public int getLocalPort() {
/* 182 */     return getValidConnection().getLocalPort();
/*     */   }
/*     */ 
/*     */   
/*     */   public InetAddress getRemoteAddress() {
/* 187 */     return getValidConnection().getRemoteAddress();
/*     */   }
/*     */ 
/*     */   
/*     */   public int getRemotePort() {
/* 192 */     return getValidConnection().getRemotePort();
/*     */   }
/*     */ 
/*     */   
/*     */   public Object getAttribute(String id) {
/* 197 */     ManagedHttpClientConnection conn = getValidConnection();
/* 198 */     return (conn instanceof HttpContext) ? ((HttpContext)conn).getAttribute(id) : null;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setAttribute(String id, Object obj) {
/* 203 */     ManagedHttpClientConnection conn = getValidConnection();
/* 204 */     if (conn instanceof HttpContext) {
/* 205 */       ((HttpContext)conn).setAttribute(id, obj);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public Object removeAttribute(String id) {
/* 211 */     ManagedHttpClientConnection conn = getValidConnection();
/* 212 */     return (conn instanceof HttpContext) ? ((HttpContext)conn).removeAttribute(id) : null;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 217 */     StringBuilder sb = new StringBuilder("CPoolProxy{");
/* 218 */     ManagedHttpClientConnection conn = getConnection();
/* 219 */     if (conn != null) {
/* 220 */       sb.append(conn);
/*     */     } else {
/* 222 */       sb.append("detached");
/*     */     } 
/* 224 */     sb.append('}');
/* 225 */     return sb.toString();
/*     */   }
/*     */   
/*     */   public static HttpClientConnection newProxy(CPoolEntry poolEntry) {
/* 229 */     return (HttpClientConnection)new CPoolProxy(poolEntry);
/*     */   }
/*     */   
/*     */   private static CPoolProxy getProxy(HttpClientConnection conn) {
/* 233 */     if (!CPoolProxy.class.isInstance(conn)) {
/* 234 */       throw new IllegalStateException("Unexpected connection proxy class: " + conn.getClass());
/*     */     }
/* 236 */     return CPoolProxy.class.cast(conn);
/*     */   }
/*     */   
/*     */   public static CPoolEntry getPoolEntry(HttpClientConnection proxy) {
/* 240 */     CPoolEntry entry = getProxy(proxy).getPoolEntry();
/* 241 */     if (entry == null) {
/* 242 */       throw new ConnectionShutdownException();
/*     */     }
/* 244 */     return entry;
/*     */   }
/*     */   
/*     */   public static CPoolEntry detach(HttpClientConnection conn) {
/* 248 */     return getProxy(conn).detach();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\impl\conn\CPoolProxy.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */