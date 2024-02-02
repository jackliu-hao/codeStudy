/*    */ package ch.qos.logback.classic.net.server;
/*    */ 
/*    */ import ch.qos.logback.core.net.server.Client;
/*    */ import ch.qos.logback.core.net.server.ServerSocketListener;
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
/*    */ 
/*    */ class RemoteAppenderServerListener
/*    */   extends ServerSocketListener<RemoteAppenderClient>
/*    */ {
/*    */   public RemoteAppenderServerListener(ServerSocket serverSocket) {
/* 36 */     super(serverSocket);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   protected RemoteAppenderClient createClient(String id, Socket socket) throws IOException {
/* 44 */     return new RemoteAppenderStreamClient(id, socket);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\classic\net\server\RemoteAppenderServerListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */