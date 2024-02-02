/*     */ package org.apache.http.impl.pool;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.net.Socket;
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedActionException;
/*     */ import java.security.PrivilegedExceptionAction;
/*     */ import javax.net.SocketFactory;
/*     */ import javax.net.ssl.SSLSocketFactory;
/*     */ import org.apache.http.HttpClientConnection;
/*     */ import org.apache.http.HttpConnectionFactory;
/*     */ import org.apache.http.HttpHost;
/*     */ import org.apache.http.annotation.Contract;
/*     */ import org.apache.http.annotation.ThreadingBehavior;
/*     */ import org.apache.http.config.ConnectionConfig;
/*     */ import org.apache.http.config.SocketConfig;
/*     */ import org.apache.http.impl.DefaultBHttpClientConnection;
/*     */ import org.apache.http.impl.DefaultBHttpClientConnectionFactory;
/*     */ import org.apache.http.params.HttpParamConfig;
/*     */ import org.apache.http.params.HttpParams;
/*     */ import org.apache.http.pool.ConnFactory;
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
/*     */ @Contract(threading = ThreadingBehavior.IMMUTABLE_CONDITIONAL)
/*     */ public class BasicConnFactory
/*     */   implements ConnFactory<HttpHost, HttpClientConnection>
/*     */ {
/*     */   private final SocketFactory plainfactory;
/*     */   private final SSLSocketFactory sslfactory;
/*     */   private final int connectTimeout;
/*     */   private final SocketConfig sconfig;
/*     */   private final HttpConnectionFactory<? extends HttpClientConnection> connFactory;
/*     */   
/*     */   @Deprecated
/*     */   public BasicConnFactory(SSLSocketFactory sslfactory, HttpParams params) {
/*  80 */     Args.notNull(params, "HTTP params");
/*  81 */     this.plainfactory = null;
/*  82 */     this.sslfactory = sslfactory;
/*  83 */     this.connectTimeout = params.getIntParameter("http.connection.timeout", 0);
/*  84 */     this.sconfig = HttpParamConfig.getSocketConfig(params);
/*  85 */     this.connFactory = (HttpConnectionFactory<? extends HttpClientConnection>)new DefaultBHttpClientConnectionFactory(HttpParamConfig.getConnectionConfig(params));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public BasicConnFactory(HttpParams params) {
/*  95 */     this((SSLSocketFactory)null, params);
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
/*     */   public BasicConnFactory(SocketFactory plainfactory, SSLSocketFactory sslfactory, int connectTimeout, SocketConfig sconfig, ConnectionConfig cconfig) {
/* 108 */     this.plainfactory = plainfactory;
/* 109 */     this.sslfactory = sslfactory;
/* 110 */     this.connectTimeout = connectTimeout;
/* 111 */     this.sconfig = (sconfig != null) ? sconfig : SocketConfig.DEFAULT;
/* 112 */     this.connFactory = (HttpConnectionFactory<? extends HttpClientConnection>)new DefaultBHttpClientConnectionFactory((cconfig != null) ? cconfig : ConnectionConfig.DEFAULT);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public BasicConnFactory(int connectTimeout, SocketConfig sconfig, ConnectionConfig cconfig) {
/* 121 */     this(null, null, connectTimeout, sconfig, cconfig);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public BasicConnFactory(SocketConfig sconfig, ConnectionConfig cconfig) {
/* 128 */     this(null, null, 0, sconfig, cconfig);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public BasicConnFactory() {
/* 135 */     this(null, null, 0, SocketConfig.DEFAULT, ConnectionConfig.DEFAULT);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   protected HttpClientConnection create(Socket socket, HttpParams params) throws IOException {
/* 143 */     int bufsize = params.getIntParameter("http.socket.buffer-size", 8192);
/* 144 */     DefaultBHttpClientConnection conn = new DefaultBHttpClientConnection(bufsize);
/* 145 */     conn.bind(socket);
/* 146 */     return (HttpClientConnection)conn;
/*     */   }
/*     */   
/*     */   public HttpClientConnection create(HttpHost host) throws IOException {
/*     */     final Socket socket;
/* 151 */     String scheme = host.getSchemeName();
/*     */     
/* 153 */     if ("http".equalsIgnoreCase(scheme)) {
/* 154 */       socket = (this.plainfactory != null) ? this.plainfactory.createSocket() : new Socket();
/*     */     }
/* 156 */     else if ("https".equalsIgnoreCase(scheme)) {
/* 157 */       socket = ((this.sslfactory != null) ? this.sslfactory : SSLSocketFactory.getDefault()).createSocket();
/*     */     } else {
/*     */       
/* 160 */       throw new IOException(scheme + " scheme is not supported");
/*     */     } 
/* 162 */     String hostname = host.getHostName();
/* 163 */     int port = host.getPort();
/* 164 */     if (port == -1) {
/* 165 */       if (host.getSchemeName().equalsIgnoreCase("http")) {
/* 166 */         port = 80;
/* 167 */       } else if (host.getSchemeName().equalsIgnoreCase("https")) {
/* 168 */         port = 443;
/*     */       } 
/*     */     }
/* 171 */     socket.setSoTimeout(this.sconfig.getSoTimeout());
/* 172 */     if (this.sconfig.getSndBufSize() > 0) {
/* 173 */       socket.setSendBufferSize(this.sconfig.getSndBufSize());
/*     */     }
/* 175 */     if (this.sconfig.getRcvBufSize() > 0) {
/* 176 */       socket.setReceiveBufferSize(this.sconfig.getRcvBufSize());
/*     */     }
/* 178 */     socket.setTcpNoDelay(this.sconfig.isTcpNoDelay());
/* 179 */     int linger = this.sconfig.getSoLinger();
/* 180 */     if (linger >= 0) {
/* 181 */       socket.setSoLinger(true, linger);
/*     */     }
/* 183 */     socket.setKeepAlive(this.sconfig.isSoKeepAlive());
/*     */ 
/*     */     
/* 186 */     final InetSocketAddress address = new InetSocketAddress(hostname, port);
/*     */     try {
/* 188 */       AccessController.doPrivileged(new PrivilegedExceptionAction()
/*     */           {
/*     */             public Object run() throws IOException {
/* 191 */               socket.connect(address, BasicConnFactory.this.connectTimeout);
/* 192 */               return null;
/*     */             }
/*     */           });
/* 195 */     } catch (PrivilegedActionException e) {
/* 196 */       Asserts.check(e.getCause() instanceof IOException, "method contract violation only checked exceptions are wrapped: " + e.getCause());
/*     */ 
/*     */       
/* 199 */       throw (IOException)e.getCause();
/*     */     } 
/* 201 */     return (HttpClientConnection)this.connFactory.createConnection(socket);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\impl\pool\BasicConnFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */