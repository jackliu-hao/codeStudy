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
/*    */ @Deprecated
/*    */ public class DefaultHttpServerConnection
/*    */   extends SocketHttpServerConnection
/*    */ {
/*    */   public void bind(Socket socket, HttpParams params) throws IOException {
/* 53 */     Args.notNull(socket, "Socket");
/* 54 */     Args.notNull(params, "HTTP parameters");
/* 55 */     assertNotOpen();
/* 56 */     socket.setTcpNoDelay(params.getBooleanParameter("http.tcp.nodelay", true));
/* 57 */     socket.setSoTimeout(params.getIntParameter("http.socket.timeout", 0));
/* 58 */     socket.setKeepAlive(params.getBooleanParameter("http.socket.keepalive", false));
/* 59 */     int linger = params.getIntParameter("http.socket.linger", -1);
/* 60 */     if (linger >= 0) {
/* 61 */       socket.setSoLinger((linger > 0), linger);
/*    */     }
/* 63 */     if (linger >= 0) {
/* 64 */       socket.setSoLinger((linger > 0), linger);
/*    */     }
/* 66 */     super.bind(socket, params);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\impl\DefaultHttpServerConnection.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */