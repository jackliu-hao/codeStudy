/*    */ package com.mysql.cj.protocol;
/*    */ 
/*    */ import com.mysql.cj.Messages;
/*    */ import com.mysql.cj.Session;
/*    */ import java.net.InetAddress;
/*    */ import java.net.InetSocketAddress;
/*    */ import java.net.SocketAddress;
/*    */ import java.net.UnknownHostException;
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
/*    */ public interface SocketMetadata
/*    */ {
/*    */   default boolean isLocallyConnected(Session sess) {
/* 43 */     String processHost = sess.getProcessHost();
/* 44 */     return isLocallyConnected(sess, processHost);
/*    */   }
/*    */   
/*    */   default boolean isLocallyConnected(Session sess, String processHost) {
/* 48 */     if (processHost != null) {
/* 49 */       sess.getLog().logDebug(Messages.getString("SocketMetadata.0", new Object[] { processHost }));
/*    */       
/* 51 */       int endIndex = processHost.lastIndexOf(":");
/* 52 */       if (endIndex != -1) {
/* 53 */         processHost = processHost.substring(0, endIndex);
/*    */       }
/*    */ 
/*    */       
/*    */       try {
/* 58 */         InetAddress[] whereMysqlThinksIConnectedFrom = InetAddress.getAllByName(processHost);
/*    */         
/* 60 */         SocketAddress remoteSocketAddr = sess.getRemoteSocketAddress();
/*    */         
/* 62 */         if (remoteSocketAddr instanceof InetSocketAddress) {
/* 63 */           InetAddress whereIConnectedTo = ((InetSocketAddress)remoteSocketAddr).getAddress();
/*    */           
/* 65 */           for (InetAddress hostAddr : whereMysqlThinksIConnectedFrom) {
/* 66 */             if (hostAddr.equals(whereIConnectedTo)) {
/* 67 */               sess.getLog().logDebug(Messages.getString("SocketMetadata.1", new Object[] { hostAddr, whereIConnectedTo }));
/* 68 */               return true;
/*    */             } 
/* 70 */             sess.getLog().logDebug(Messages.getString("SocketMetadata.2", new Object[] { hostAddr, whereIConnectedTo }));
/*    */           } 
/*    */         } else {
/*    */           
/* 74 */           sess.getLog().logDebug(Messages.getString("SocketMetadata.3", new Object[] { remoteSocketAddr }));
/*    */         } 
/*    */         
/* 77 */         return false;
/* 78 */       } catch (UnknownHostException e) {
/* 79 */         sess.getLog().logWarn(Messages.getString("Connection.CantDetectLocalConnect", new Object[] { processHost }), e);
/*    */         
/* 81 */         return false;
/*    */       } 
/*    */     } 
/*    */ 
/*    */     
/* 86 */     return false;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\protocol\SocketMetadata.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */