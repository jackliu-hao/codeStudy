/*    */ package org.apache.http.conn.socket;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.net.InetSocketAddress;
/*    */ import java.net.Socket;
/*    */ import org.apache.http.HttpHost;
/*    */ import org.apache.http.annotation.Contract;
/*    */ import org.apache.http.annotation.ThreadingBehavior;
/*    */ import org.apache.http.protocol.HttpContext;
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
/*    */ 
/*    */ @Contract(threading = ThreadingBehavior.IMMUTABLE)
/*    */ public class PlainConnectionSocketFactory
/*    */   implements ConnectionSocketFactory
/*    */ {
/* 47 */   public static final PlainConnectionSocketFactory INSTANCE = new PlainConnectionSocketFactory();
/*    */   
/*    */   public static PlainConnectionSocketFactory getSocketFactory() {
/* 50 */     return INSTANCE;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public Socket createSocket(HttpContext context) throws IOException {
/* 59 */     return new Socket();
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public Socket connectSocket(int connectTimeout, Socket socket, HttpHost host, InetSocketAddress remoteAddress, InetSocketAddress localAddress, HttpContext context) throws IOException {
/* 70 */     Socket sock = (socket != null) ? socket : createSocket(context);
/* 71 */     if (localAddress != null) {
/* 72 */       sock.bind(localAddress);
/*    */     }
/*    */     try {
/* 75 */       sock.connect(remoteAddress, connectTimeout);
/* 76 */     } catch (IOException ex) {
/*    */       try {
/* 78 */         sock.close();
/* 79 */       } catch (IOException ignore) {}
/*    */       
/* 81 */       throw ex;
/*    */     } 
/* 83 */     return sock;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\conn\socket\PlainConnectionSocketFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */