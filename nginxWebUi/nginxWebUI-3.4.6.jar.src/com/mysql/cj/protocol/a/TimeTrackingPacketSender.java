/*    */ package com.mysql.cj.protocol.a;
/*    */ 
/*    */ import com.mysql.cj.protocol.MessageSender;
/*    */ import com.mysql.cj.protocol.PacketSentTimeHolder;
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
/*    */ public class TimeTrackingPacketSender
/*    */   implements MessageSender<NativePacketPayload>, PacketSentTimeHolder
/*    */ {
/*    */   private MessageSender<NativePacketPayload> packetSender;
/* 42 */   private long lastPacketSentTime = 0L;
/* 43 */   private long previousPacketSentTime = 0L;
/*    */   
/*    */   public TimeTrackingPacketSender(MessageSender<NativePacketPayload> packetSender) {
/* 46 */     this.packetSender = packetSender;
/*    */   }
/*    */   
/*    */   public void send(byte[] packet, int packetLen, byte packetSequence) throws IOException {
/* 50 */     this.packetSender.send(packet, packetLen, packetSequence);
/*    */     
/* 52 */     this.previousPacketSentTime = this.lastPacketSentTime;
/* 53 */     this.lastPacketSentTime = System.currentTimeMillis();
/*    */   }
/*    */   
/*    */   public long getLastPacketSentTime() {
/* 57 */     return this.lastPacketSentTime;
/*    */   }
/*    */ 
/*    */   
/*    */   public long getPreviousPacketSentTime() {
/* 62 */     return this.previousPacketSentTime;
/*    */   }
/*    */ 
/*    */   
/*    */   public MessageSender<NativePacketPayload> undecorateAll() {
/* 67 */     return this.packetSender.undecorateAll();
/*    */   }
/*    */ 
/*    */   
/*    */   public MessageSender<NativePacketPayload> undecorate() {
/* 72 */     return this.packetSender;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\protocol\a\TimeTrackingPacketSender.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */