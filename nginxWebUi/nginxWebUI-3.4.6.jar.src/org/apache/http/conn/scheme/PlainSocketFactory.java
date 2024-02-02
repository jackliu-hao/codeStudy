/*     */ package org.apache.http.conn.scheme;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.net.InetAddress;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.net.Socket;
/*     */ import java.net.SocketTimeoutException;
/*     */ import java.net.UnknownHostException;
/*     */ import org.apache.http.annotation.Contract;
/*     */ import org.apache.http.annotation.ThreadingBehavior;
/*     */ import org.apache.http.conn.ConnectTimeoutException;
/*     */ import org.apache.http.params.HttpConnectionParams;
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
/*     */ @Deprecated
/*     */ @Contract(threading = ThreadingBehavior.IMMUTABLE)
/*     */ public class PlainSocketFactory
/*     */   implements SocketFactory, SchemeSocketFactory
/*     */ {
/*     */   private final HostNameResolver nameResolver;
/*     */   
/*     */   public static PlainSocketFactory getSocketFactory() {
/*  63 */     return new PlainSocketFactory();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public PlainSocketFactory(HostNameResolver nameResolver) {
/*  72 */     this.nameResolver = nameResolver;
/*     */   }
/*     */ 
/*     */   
/*     */   public PlainSocketFactory() {
/*  77 */     this.nameResolver = null;
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
/*     */   public Socket createSocket(HttpParams params) {
/*  89 */     return new Socket();
/*     */   }
/*     */ 
/*     */   
/*     */   public Socket createSocket() {
/*  94 */     return new Socket();
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
/*     */   public Socket connectSocket(Socket socket, InetSocketAddress remoteAddress, InetSocketAddress localAddress, HttpParams params) throws IOException, ConnectTimeoutException {
/* 106 */     Args.notNull(remoteAddress, "Remote address");
/* 107 */     Args.notNull(params, "HTTP parameters");
/* 108 */     Socket sock = socket;
/* 109 */     if (sock == null) {
/* 110 */       sock = createSocket();
/*     */     }
/* 112 */     if (localAddress != null) {
/* 113 */       sock.setReuseAddress(HttpConnectionParams.getSoReuseaddr(params));
/* 114 */       sock.bind(localAddress);
/*     */     } 
/* 116 */     int connTimeout = HttpConnectionParams.getConnectionTimeout(params);
/* 117 */     int soTimeout = HttpConnectionParams.getSoTimeout(params);
/*     */     
/*     */     try {
/* 120 */       sock.setSoTimeout(soTimeout);
/* 121 */       sock.connect(remoteAddress, connTimeout);
/* 122 */     } catch (SocketTimeoutException ex) {
/* 123 */       throw new ConnectTimeoutException("Connect to " + remoteAddress + " timed out");
/*     */     } 
/* 125 */     return sock;
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
/*     */   public final boolean isSecure(Socket sock) {
/* 139 */     return false;
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
/*     */   public Socket connectSocket(Socket socket, String host, int port, InetAddress localAddress, int localPort, HttpParams params) throws IOException, UnknownHostException, ConnectTimeoutException {
/*     */     InetAddress remoteAddress;
/* 152 */     InetSocketAddress local = null;
/* 153 */     if (localAddress != null || localPort > 0) {
/* 154 */       local = new InetSocketAddress(localAddress, (localPort > 0) ? localPort : 0);
/*     */     }
/*     */     
/* 157 */     if (this.nameResolver != null) {
/* 158 */       remoteAddress = this.nameResolver.resolve(host);
/*     */     } else {
/* 160 */       remoteAddress = InetAddress.getByName(host);
/*     */     } 
/* 162 */     InetSocketAddress remote = new InetSocketAddress(remoteAddress, port);
/* 163 */     return connectSocket(socket, remote, local, params);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\conn\scheme\PlainSocketFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */