/*     */ package org.apache.http.conn;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.net.InetAddress;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.net.Socket;
/*     */ import java.net.SocketTimeoutException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ import org.apache.http.annotation.Contract;
/*     */ import org.apache.http.annotation.ThreadingBehavior;
/*     */ import org.apache.http.conn.scheme.SocketFactory;
/*     */ import org.apache.http.params.HttpConnectionParams;
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
/*     */ @Deprecated
/*     */ @Contract(threading = ThreadingBehavior.IMMUTABLE)
/*     */ public final class MultihomePlainSocketFactory
/*     */   implements SocketFactory
/*     */ {
/*  68 */   private static final MultihomePlainSocketFactory DEFAULT_FACTORY = new MultihomePlainSocketFactory();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static MultihomePlainSocketFactory getSocketFactory() {
/*  75 */     return DEFAULT_FACTORY;
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
/*     */   public Socket createSocket() {
/*  89 */     return new Socket();
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
/*     */ 
/*     */   
/*     */   public Socket connectSocket(Socket socket, String host, int port, InetAddress localAddress, int localPort, HttpParams params) throws IOException {
/* 112 */     Args.notNull(host, "Target host");
/* 113 */     Args.notNull(params, "HTTP parameters");
/*     */     
/* 115 */     Socket sock = socket;
/* 116 */     if (sock == null) {
/* 117 */       sock = createSocket();
/*     */     }
/*     */     
/* 120 */     if (localAddress != null || localPort > 0) {
/* 121 */       InetSocketAddress isa = new InetSocketAddress(localAddress, (localPort > 0) ? localPort : 0);
/*     */       
/* 123 */       sock.bind(isa);
/*     */     } 
/*     */     
/* 126 */     int timeout = HttpConnectionParams.getConnectionTimeout(params);
/*     */     
/* 128 */     InetAddress[] inetadrs = InetAddress.getAllByName(host);
/* 129 */     List<InetAddress> addresses = new ArrayList<InetAddress>(inetadrs.length);
/* 130 */     addresses.addAll(Arrays.asList(inetadrs));
/* 131 */     Collections.shuffle(addresses);
/*     */     
/* 133 */     IOException lastEx = null;
/* 134 */     for (InetAddress remoteAddress : addresses) {
/*     */       try {
/* 136 */         sock.connect(new InetSocketAddress(remoteAddress, port), timeout);
/*     */         break;
/* 138 */       } catch (SocketTimeoutException ex) {
/* 139 */         throw new ConnectTimeoutException("Connect to " + remoteAddress + " timed out");
/* 140 */       } catch (IOException ex) {
/*     */         
/* 142 */         sock = new Socket();
/*     */         
/* 144 */         lastEx = ex;
/*     */       } 
/*     */     } 
/* 147 */     if (lastEx != null) {
/* 148 */       throw lastEx;
/*     */     }
/* 150 */     return sock;
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
/*     */   public final boolean isSecure(Socket sock) throws IllegalArgumentException {
/* 169 */     Args.notNull(sock, "Socket");
/*     */ 
/*     */     
/* 172 */     Asserts.check(!sock.isClosed(), "Socket is closed");
/* 173 */     return false;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\conn\MultihomePlainSocketFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */