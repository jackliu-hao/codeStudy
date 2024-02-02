/*    */ package com.mysql.cj.protocol.x;
/*    */ 
/*    */ import com.mysql.cj.protocol.MessageHeader;
/*    */ import java.nio.ByteBuffer;
/*    */ import java.nio.ByteOrder;
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
/*    */ public class XMessageHeader
/*    */   implements MessageHeader
/*    */ {
/*    */   public static final int MESSAGE_SIZE_LENGTH = 4;
/*    */   public static final int MESSAGE_TYPE_LENGTH = 1;
/*    */   public static final int HEADER_LENGTH = 5;
/*    */   private ByteBuffer headerBuf;
/* 44 */   private int messageType = -1;
/*    */   
/* 46 */   private int messageSize = -1;
/*    */   
/*    */   public XMessageHeader() {
/* 49 */     this.headerBuf = ByteBuffer.allocate(5).order(ByteOrder.LITTLE_ENDIAN);
/*    */   }
/*    */   
/*    */   public XMessageHeader(byte[] buf) {
/* 53 */     this.headerBuf = ByteBuffer.wrap(buf).order(ByteOrder.LITTLE_ENDIAN);
/*    */   }
/*    */   
/*    */   private void parseBuffer() {
/* 57 */     if (this.messageSize == -1) {
/* 58 */       this.headerBuf.position(0);
/* 59 */       this.messageSize = this.headerBuf.getInt() - 1;
/* 60 */       this.messageType = this.headerBuf.get();
/*    */     } 
/*    */   }
/*    */ 
/*    */   
/*    */   public ByteBuffer getBuffer() {
/* 66 */     return this.headerBuf;
/*    */   }
/*    */ 
/*    */   
/*    */   public int getMessageSize() {
/* 71 */     parseBuffer();
/* 72 */     return this.messageSize;
/*    */   }
/*    */ 
/*    */   
/*    */   public byte getMessageSequence() {
/* 77 */     return 0;
/*    */   }
/*    */   
/*    */   public int getMessageType() {
/* 81 */     parseBuffer();
/* 82 */     return this.messageType;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\protocol\x\XMessageHeader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */