/*    */ package com.mysql.cj.protocol.a;
/*    */ 
/*    */ import com.mysql.cj.protocol.MessageHeader;
/*    */ import java.nio.ByteBuffer;
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
/*    */ 
/*    */ public class NativePacketHeader
/*    */   implements MessageHeader
/*    */ {
/*    */   protected ByteBuffer packetHeaderBuf;
/*    */   
/*    */   public NativePacketHeader() {
/* 47 */     this.packetHeaderBuf = ByteBuffer.allocate(4);
/*    */   }
/*    */   
/*    */   public NativePacketHeader(byte[] buf) {
/* 51 */     this.packetHeaderBuf = ByteBuffer.wrap(buf);
/*    */   }
/*    */   
/*    */   public ByteBuffer getBuffer() {
/* 55 */     return this.packetHeaderBuf;
/*    */   }
/*    */ 
/*    */   
/*    */   public int getMessageSize() {
/* 60 */     return (this.packetHeaderBuf.array()[0] & 0xFF) + ((this.packetHeaderBuf.array()[1] & 0xFF) << 8) + ((this.packetHeaderBuf.array()[2] & 0xFF) << 16);
/*    */   }
/*    */ 
/*    */   
/*    */   public byte getMessageSequence() {
/* 65 */     return this.packetHeaderBuf.array()[3];
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\protocol\a\NativePacketHeader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */