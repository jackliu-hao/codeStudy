/*    */ package org.noear.solon.socketd.protocol;
/*    */ 
/*    */ import java.nio.ByteBuffer;
/*    */ import org.noear.solon.core.message.Message;
/*    */ import org.noear.solon.socketd.protocol.util.GzipUtil;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class MessageProtocolCompress
/*    */   implements MessageProtocol
/*    */ {
/* 16 */   protected MessageProtocol baseProtocol = MessageProtocolBase.instance;
/* 17 */   protected int allowCompressSize = 1024;
/*    */ 
/*    */   
/*    */   public MessageProtocolCompress() {}
/*    */ 
/*    */   
/*    */   public MessageProtocolCompress(int allowCompressSize) {
/* 24 */     this.allowCompressSize = allowCompressSize;
/*    */   }
/*    */   
/*    */   public MessageProtocolCompress(MessageProtocol baseProtocol) {
/* 28 */     this.baseProtocol = baseProtocol;
/*    */   }
/*    */   
/*    */   public MessageProtocolCompress(int allowCompressSize, MessageProtocol baseProtocol) {
/* 32 */     this.baseProtocol = baseProtocol;
/* 33 */     this.allowCompressSize = allowCompressSize;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public boolean allowCompress(int byteSize) {
/* 40 */     return (byteSize > this.allowCompressSize);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public byte[] compress(byte[] bytes) throws Exception {
/* 47 */     return GzipUtil.compress(bytes);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public byte[] uncompress(byte[] bytes) throws Exception {
/* 54 */     return GzipUtil.uncompress(bytes);
/*    */   }
/*    */ 
/*    */   
/*    */   public ByteBuffer encode(Message message) throws Exception {
/* 59 */     ByteBuffer buffer = this.baseProtocol.encode(message);
/*    */     
/* 61 */     if (allowCompress((buffer.array()).length)) {
/* 62 */       byte[] bytes = compress(buffer.array());
/* 63 */       message = Message.wrapContainer(bytes);
/*    */       
/* 65 */       buffer = this.baseProtocol.encode(message);
/*    */     } 
/*    */     
/* 68 */     return buffer;
/*    */   }
/*    */ 
/*    */   
/*    */   public Message decode(ByteBuffer buffer) throws Exception {
/* 73 */     Message message = this.baseProtocol.decode(buffer);
/*    */     
/* 75 */     if (message.flag() == 1) {
/* 76 */       byte[] bytes = uncompress(message.body());
/* 77 */       buffer = ByteBuffer.wrap(bytes);
/*    */       
/* 79 */       message = this.baseProtocol.decode(buffer);
/*    */     } 
/*    */     
/* 82 */     return message;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\socketd\protocol\MessageProtocolCompress.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */