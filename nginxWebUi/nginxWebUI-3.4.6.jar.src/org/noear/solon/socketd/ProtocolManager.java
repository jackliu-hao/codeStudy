/*    */ package org.noear.solon.socketd;
/*    */ 
/*    */ import java.nio.ByteBuffer;
/*    */ import org.noear.solon.core.message.Message;
/*    */ import org.noear.solon.socketd.protocol.MessageProtocol;
/*    */ import org.noear.solon.socketd.protocol.MessageProtocolBase;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class ProtocolManager
/*    */ {
/* 16 */   private static MessageProtocol protocol = MessageProtocolBase.instance;
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static void setProtocol(MessageProtocol protocol) {
/* 22 */     if (protocol != null) {
/* 23 */       ProtocolManager.protocol = protocol;
/*    */     }
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static ByteBuffer encode(Message message) throws IllegalArgumentException {
/*    */     try {
/* 34 */       return protocol.encode(message);
/* 35 */     } catch (Throwable e) {
/* 36 */       throw new IllegalArgumentException(e);
/*    */     } 
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static Message decode(ByteBuffer buffer) throws IllegalArgumentException {
/*    */     try {
/* 47 */       return protocol.decode(buffer);
/* 48 */     } catch (Throwable e) {
/* 49 */       throw new IllegalArgumentException(e);
/*    */     } 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\socketd\ProtocolManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */