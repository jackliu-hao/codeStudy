/*    */ package org.apache.http.conn.scheme;
/*    */ 
/*    */ import java.io.IOException;
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
/*    */ class SchemeLayeredSocketFactoryAdaptor2
/*    */   implements SchemeLayeredSocketFactory
/*    */ {
/*    */   private final LayeredSchemeSocketFactory factory;
/*    */   
/*    */   SchemeLayeredSocketFactoryAdaptor2(LayeredSchemeSocketFactory factory) {
/* 48 */     this.factory = factory;
/*    */   }
/*    */ 
/*    */   
/*    */   public Socket createSocket(HttpParams params) throws IOException {
/* 53 */     return this.factory.createSocket(params);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public Socket connectSocket(Socket sock, InetSocketAddress remoteAddress, InetSocketAddress localAddress, HttpParams params) throws IOException, UnknownHostException, ConnectTimeoutException {
/* 62 */     return this.factory.connectSocket(sock, remoteAddress, localAddress, params);
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean isSecure(Socket sock) throws IllegalArgumentException {
/* 67 */     return this.factory.isSecure(sock);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public Socket createLayeredSocket(Socket socket, String target, int port, HttpParams params) throws IOException, UnknownHostException {
/* 75 */     return this.factory.createLayeredSocket(socket, target, port, true);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\conn\scheme\SchemeLayeredSocketFactoryAdaptor2.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */