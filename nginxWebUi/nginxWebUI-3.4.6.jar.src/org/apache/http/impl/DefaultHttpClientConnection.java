/*    */ package org.apache.http.impl;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.net.Socket;
/*    */ import org.apache.http.params.HttpParams;
/*    */ import org.apache.http.util.Args;
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
/*    */ public class DefaultHttpClientConnection
/*    */   extends SocketHttpClientConnection
/*    */ {
/*    */   public void bind(Socket socket, HttpParams params) throws IOException {
/* 55 */     Args.notNull(socket, "Socket");
/* 56 */     Args.notNull(params, "HTTP parameters");
/* 57 */     assertNotOpen();
/* 58 */     socket.setTcpNoDelay(params.getBooleanParameter("http.tcp.nodelay", true));
/* 59 */     socket.setSoTimeout(params.getIntParameter("http.socket.timeout", 0));
/* 60 */     socket.setKeepAlive(params.getBooleanParameter("http.socket.keepalive", false));
/* 61 */     int linger = params.getIntParameter("http.socket.linger", -1);
/* 62 */     if (linger >= 0) {
/* 63 */       socket.setSoLinger((linger > 0), linger);
/*    */     }
/* 65 */     super.bind(socket, params);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\impl\DefaultHttpClientConnection.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */