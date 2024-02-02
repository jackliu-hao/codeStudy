/*    */ package ch.qos.logback.core.net.server;
/*    */ 
/*    */ import ch.qos.logback.core.util.CloseUtil;
/*    */ import java.io.IOException;
/*    */ import java.net.ServerSocket;
/*    */ import java.net.Socket;
/*    */ import java.net.SocketAddress;
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
/*    */ public abstract class ServerSocketListener<T extends Client>
/*    */   implements ServerListener<T>
/*    */ {
/*    */   private final ServerSocket serverSocket;
/*    */   
/*    */   public ServerSocketListener(ServerSocket serverSocket) {
/* 37 */     this.serverSocket = serverSocket;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public T acceptClient() throws IOException {
/* 44 */     Socket socket = this.serverSocket.accept();
/* 45 */     return createClient(socketAddressToString(socket.getRemoteSocketAddress()), socket);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   protected abstract T createClient(String paramString, Socket paramSocket) throws IOException;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void close() {
/* 61 */     CloseUtil.closeQuietly(this.serverSocket);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String toString() {
/* 69 */     return socketAddressToString(this.serverSocket.getLocalSocketAddress());
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   private String socketAddressToString(SocketAddress address) {
/* 78 */     String addr = address.toString();
/* 79 */     int i = addr.indexOf("/");
/* 80 */     if (i >= 0) {
/* 81 */       addr = addr.substring(i + 1);
/*    */     }
/* 83 */     return addr;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\core\net\server\ServerSocketListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */