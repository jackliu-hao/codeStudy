/*    */ package org.noear.solon.socketd.protocol;
/*    */ 
/*    */ import java.nio.ByteBuffer;
/*    */ import org.noear.solon.core.message.Message;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public abstract class MessageProtocolSecret
/*    */   implements MessageProtocol
/*    */ {
/* 15 */   protected MessageProtocol baseProtocol = MessageProtocolBase.instance;
/*    */ 
/*    */   
/*    */   public MessageProtocolSecret() {}
/*    */ 
/*    */   
/*    */   public MessageProtocolSecret(MessageProtocol baseProtocol) {
/* 22 */     this.baseProtocol = baseProtocol;
/*    */   }
/*    */ 
/*    */   
/*    */   public abstract byte[] encrypt(byte[] paramArrayOfbyte) throws Exception;
/*    */ 
/*    */   
/*    */   public abstract byte[] decrypt(byte[] paramArrayOfbyte) throws Exception;
/*    */   
/*    */   public ByteBuffer encode(Message message) throws Exception {
/* 32 */     ByteBuffer buffer = this.baseProtocol.encode(message);
/*    */ 
/*    */     
/* 35 */     byte[] bytes = encrypt(buffer.array());
/* 36 */     message = Message.wrapContainer(bytes);
/*    */     
/* 38 */     return this.baseProtocol.encode(message);
/*    */   }
/*    */ 
/*    */   
/*    */   public Message decode(ByteBuffer buffer) throws Exception {
/* 43 */     Message message = this.baseProtocol.decode(buffer);
/*    */     
/* 45 */     if (message.flag() == 1) {
/* 46 */       byte[] bytes = decrypt(message.body());
/* 47 */       buffer = ByteBuffer.wrap(bytes);
/*    */       
/* 49 */       message = this.baseProtocol.decode(buffer);
/*    */     } 
/*    */     
/* 52 */     return message;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\socketd\protocol\MessageProtocolSecret.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */