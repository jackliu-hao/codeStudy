/*    */ package com.mysql.cj.protocol.a;
/*    */ 
/*    */ import com.mysql.cj.protocol.MessageSender;
/*    */ import java.io.BufferedOutputStream;
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
/*    */ public class SimplePacketSender
/*    */   implements MessageSender<NativePacketPayload>
/*    */ {
/*    */   private BufferedOutputStream outputStream;
/*    */   
/*    */   public SimplePacketSender(BufferedOutputStream outputStream) {
/* 45 */     this.outputStream = outputStream;
/*    */   }
/*    */   
/*    */   public void send(byte[] packet, int packetLen, byte packetSequence) throws IOException {
/* 49 */     PacketSplitter packetSplitter = new PacketSplitter(packetLen);
/* 50 */     while (packetSplitter.nextPacket()) {
/* 51 */       this.outputStream.write(NativeUtils.encodeMysqlThreeByteInteger(packetSplitter.getPacketLen()));
/* 52 */       packetSequence = (byte)(packetSequence + 1); this.outputStream.write(packetSequence);
/* 53 */       this.outputStream.write(packet, packetSplitter.getOffset(), packetSplitter.getPacketLen());
/*    */     } 
/* 55 */     this.outputStream.flush();
/*    */   }
/*    */ 
/*    */   
/*    */   public MessageSender<NativePacketPayload> undecorateAll() {
/* 60 */     return this;
/*    */   }
/*    */ 
/*    */   
/*    */   public MessageSender<NativePacketPayload> undecorate() {
/* 65 */     return this;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\protocol\a\SimplePacketSender.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */