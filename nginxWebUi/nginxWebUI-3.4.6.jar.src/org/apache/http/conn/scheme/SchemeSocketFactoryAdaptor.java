/*    */ package org.apache.http.conn.scheme;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.net.InetAddress;
/*    */ import java.net.InetSocketAddress;
/*    */ import java.net.Socket;
/*    */ import java.net.UnknownHostException;
/*    */ import org.apache.http.conn.ConnectTimeoutException;
/*    */ import org.apache.http.params.HttpParams;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ @Deprecated
/*    */ class SchemeSocketFactoryAdaptor
/*    */   implements SchemeSocketFactory
/*    */ {
/*    */   private final SocketFactory factory;
/*    */   
/*    */   SchemeSocketFactoryAdaptor(SocketFactory factory) {
/* 49 */     this.factory = factory;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public Socket connectSocket(Socket sock, InetSocketAddress remoteAddress, InetSocketAddress localAddress, HttpParams params) throws IOException, UnknownHostException, ConnectTimeoutException {
/* 58 */     String host = remoteAddress.getHostName();
/* 59 */     int port = remoteAddress.getPort();
/* 60 */     InetAddress local = null;
/* 61 */     int localPort = 0;
/* 62 */     if (localAddress != null) {
/* 63 */       local = localAddress.getAddress();
/* 64 */       localPort = localAddress.getPort();
/*    */     } 
/* 66 */     return this.factory.connectSocket(sock, host, port, local, localPort, params);
/*    */   }
/*    */ 
/*    */   
/*    */   public Socket createSocket(HttpParams params) throws IOException {
/* 71 */     return this.factory.createSocket();
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean isSecure(Socket sock) throws IllegalArgumentException {
/* 76 */     return this.factory.isSecure(sock);
/*    */   }
/*    */   
/*    */   public SocketFactory getFactory() {
/* 80 */     return this.factory;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean equals(Object obj) {
/* 85 */     if (obj == null) {
/* 86 */       return false;
/*    */     }
/* 88 */     if (this == obj) {
/* 89 */       return true;
/*    */     }
/* 91 */     return (obj instanceof SchemeSocketFactoryAdaptor) ? this.factory.equals(((SchemeSocketFactoryAdaptor)obj).factory) : this.factory.equals(obj);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public int hashCode() {
/* 98 */     return this.factory.hashCode();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\conn\scheme\SchemeSocketFactoryAdaptor.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */