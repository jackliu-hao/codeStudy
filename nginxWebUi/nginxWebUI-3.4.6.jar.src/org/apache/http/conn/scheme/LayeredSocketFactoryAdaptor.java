/*    */ package org.apache.http.conn.scheme;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.net.Socket;
/*    */ import java.net.UnknownHostException;
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
/*    */ class LayeredSocketFactoryAdaptor
/*    */   extends SocketFactoryAdaptor
/*    */   implements LayeredSocketFactory
/*    */ {
/*    */   private final LayeredSchemeSocketFactory factory;
/*    */   
/*    */   LayeredSocketFactoryAdaptor(LayeredSchemeSocketFactory factory) {
/* 43 */     super(factory);
/* 44 */     this.factory = factory;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public Socket createSocket(Socket socket, String host, int port, boolean autoClose) throws IOException, UnknownHostException {
/* 51 */     return this.factory.createLayeredSocket(socket, host, port, autoClose);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\conn\scheme\LayeredSocketFactoryAdaptor.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */