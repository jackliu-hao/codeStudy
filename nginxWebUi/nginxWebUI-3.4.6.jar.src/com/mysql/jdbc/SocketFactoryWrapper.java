/*    */ package com.mysql.jdbc;
/*    */ 
/*    */ import com.mysql.cj.conf.PropertySet;
/*    */ import com.mysql.cj.log.Log;
/*    */ import com.mysql.cj.protocol.ServerSession;
/*    */ import com.mysql.cj.protocol.SocketConnection;
/*    */ import com.mysql.cj.protocol.SocketFactory;
/*    */ import com.mysql.cj.protocol.StandardSocketFactory;
/*    */ import java.io.IOException;
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
/*    */ public class SocketFactoryWrapper
/*    */   extends StandardSocketFactory
/*    */   implements SocketFactory
/*    */ {
/*    */   SocketFactory socketFactory;
/*    */   
/*    */   public SocketFactoryWrapper(Object legacyFactory) {
/* 52 */     this.socketFactory = (SocketFactory)legacyFactory;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public <T extends java.io.Closeable> T connect(String hostname, int portNumber, PropertySet pset, int loginTimeout) throws IOException {
/* 58 */     this.rawSocket = this.socketFactory.connect(hostname, portNumber, pset.exposeAsProperties());
/* 59 */     return (T)this.rawSocket;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public <T extends java.io.Closeable> T performTlsHandshake(SocketConnection socketConnection, ServerSession serverSession, Log log) throws IOException {
/* 65 */     return (T)super.performTlsHandshake(socketConnection, serverSession, log);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public void beforeHandshake() throws IOException {
/* 71 */     this.socketFactory.beforeHandshake();
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public void afterHandshake() throws IOException {
/* 77 */     this.socketFactory.afterHandshake();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\jdbc\SocketFactoryWrapper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */