/*    */ package org.apache.http.impl.io;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ @Deprecated
/*    */ public class SocketOutputBuffer
/*    */   extends AbstractSessionOutputBuffer
/*    */ {
/*    */   public SocketOutputBuffer(Socket socket, int bufferSize, HttpParams params) throws IOException {
/* 62 */     Args.notNull(socket, "Socket");
/* 63 */     int n = bufferSize;
/* 64 */     if (n < 0) {
/* 65 */       n = socket.getSendBufferSize();
/*    */     }
/* 67 */     if (n < 1024) {
/* 68 */       n = 1024;
/*    */     }
/* 70 */     init(socket.getOutputStream(), n, params);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\impl\io\SocketOutputBuffer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */