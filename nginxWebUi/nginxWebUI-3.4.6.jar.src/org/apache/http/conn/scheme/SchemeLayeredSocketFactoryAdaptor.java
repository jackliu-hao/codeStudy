/*    */ package org.apache.http.conn.scheme;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.net.Socket;
/*    */ import java.net.UnknownHostException;
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
/*    */ @Deprecated
/*    */ class SchemeLayeredSocketFactoryAdaptor
/*    */   extends SchemeSocketFactoryAdaptor
/*    */   implements SchemeLayeredSocketFactory
/*    */ {
/*    */   private final LayeredSocketFactory factory;
/*    */   
/*    */   SchemeLayeredSocketFactoryAdaptor(LayeredSocketFactory factory) {
/* 46 */     super(factory);
/* 47 */     this.factory = factory;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public Socket createLayeredSocket(Socket socket, String target, int port, HttpParams params) throws IOException, UnknownHostException {
/* 55 */     return this.factory.createSocket(socket, target, port, true);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\conn\scheme\SchemeLayeredSocketFactoryAdaptor.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */