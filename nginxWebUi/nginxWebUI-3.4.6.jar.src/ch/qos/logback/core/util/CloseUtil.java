/*    */ package ch.qos.logback.core.util;
/*    */ 
/*    */ import java.io.Closeable;
/*    */ import java.io.IOException;
/*    */ import java.net.ServerSocket;
/*    */ import java.net.Socket;
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
/*    */ public class CloseUtil
/*    */ {
/*    */   public static void closeQuietly(Closeable closeable) {
/* 33 */     if (closeable == null)
/*    */       return; 
/*    */     try {
/* 36 */       closeable.close();
/* 37 */     } catch (IOException iOException) {}
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static void closeQuietly(Socket socket) {
/* 47 */     if (socket == null)
/*    */       return; 
/*    */     try {
/* 50 */       socket.close();
/* 51 */     } catch (IOException iOException) {}
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static void closeQuietly(ServerSocket serverSocket) {
/* 62 */     if (serverSocket == null)
/*    */       return; 
/*    */     try {
/* 65 */       serverSocket.close();
/* 66 */     } catch (IOException iOException) {}
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\cor\\util\CloseUtil.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */