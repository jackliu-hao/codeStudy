/*    */ package io.undertow.server.handlers.proxy;
/*    */ 
/*    */ import io.undertow.client.ClientConnection;
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
/*    */ public class ProxyConnection
/*    */ {
/*    */   private final ClientConnection connection;
/*    */   private final String targetPath;
/*    */   
/*    */   public ProxyConnection(ClientConnection connection, String targetPath) {
/* 34 */     this.connection = connection;
/* 35 */     this.targetPath = targetPath;
/*    */   }
/*    */   
/*    */   public ClientConnection getConnection() {
/* 39 */     return this.connection;
/*    */   }
/*    */   
/*    */   public String getTargetPath() {
/* 43 */     return this.targetPath;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\handlers\proxy\ProxyConnection.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */