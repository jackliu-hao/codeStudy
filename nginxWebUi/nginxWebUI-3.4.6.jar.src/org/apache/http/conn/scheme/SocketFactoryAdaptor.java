/*    */ package org.apache.http.conn.scheme;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.net.InetAddress;
/*    */ import java.net.InetSocketAddress;
/*    */ import java.net.Socket;
/*    */ import java.net.UnknownHostException;
/*    */ import org.apache.http.conn.ConnectTimeoutException;
/*    */ import org.apache.http.params.BasicHttpParams;
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
/*    */ class SocketFactoryAdaptor
/*    */   implements SocketFactory
/*    */ {
/*    */   private final SchemeSocketFactory factory;
/*    */   
/*    */   SocketFactoryAdaptor(SchemeSocketFactory factory) {
/* 50 */     this.factory = factory;
/*    */   }
/*    */ 
/*    */   
/*    */   public Socket createSocket() throws IOException {
/* 55 */     BasicHttpParams basicHttpParams = new BasicHttpParams();
/* 56 */     return this.factory.createSocket((HttpParams)basicHttpParams);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public Socket connectSocket(Socket socket, String host, int port, InetAddress localAddress, int localPort, HttpParams params) throws IOException, UnknownHostException, ConnectTimeoutException {
/* 65 */     InetSocketAddress local = null;
/* 66 */     if (localAddress != null || localPort > 0) {
/* 67 */       local = new InetSocketAddress(localAddress, (localPort > 0) ? localPort : 0);
/*    */     }
/* 69 */     InetAddress remoteAddress = InetAddress.getByName(host);
/* 70 */     InetSocketAddress remote = new InetSocketAddress(remoteAddress, port);
/* 71 */     return this.factory.connectSocket(socket, remote, local, params);
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean isSecure(Socket socket) throws IllegalArgumentException {
/* 76 */     return this.factory.isSecure(socket);
/*    */   }
/*    */   
/*    */   public SchemeSocketFactory getFactory() {
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
/* 91 */     return (obj instanceof SocketFactoryAdaptor) ? this.factory.equals(((SocketFactoryAdaptor)obj).factory) : this.factory.equals(obj);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public int hashCode() {
/* 98 */     return this.factory.hashCode();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\conn\scheme\SocketFactoryAdaptor.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */