/*    */ package com.mysql.cj.protocol.a;
/*    */ 
/*    */ import com.mysql.cj.protocol.Message;
/*    */ import com.mysql.cj.protocol.MessageHeader;
/*    */ import com.mysql.cj.protocol.MessageReader;
/*    */ import com.mysql.cj.protocol.PacketReceivedTimeHolder;
/*    */ import java.io.IOException;
/*    */ import java.util.Optional;
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
/*    */ public class TimeTrackingPacketReader
/*    */   implements MessageReader<NativePacketHeader, NativePacketPayload>, PacketReceivedTimeHolder
/*    */ {
/*    */   private MessageReader<NativePacketHeader, NativePacketPayload> packetReader;
/* 44 */   private long lastPacketReceivedTimeMs = 0L;
/*    */   
/*    */   public TimeTrackingPacketReader(MessageReader<NativePacketHeader, NativePacketPayload> messageReader) {
/* 47 */     this.packetReader = messageReader;
/*    */   }
/*    */ 
/*    */   
/*    */   public NativePacketHeader readHeader() throws IOException {
/* 52 */     return (NativePacketHeader)this.packetReader.readHeader();
/*    */   }
/*    */ 
/*    */   
/*    */   public NativePacketHeader probeHeader() throws IOException {
/* 57 */     return (NativePacketHeader)this.packetReader.probeHeader();
/*    */   }
/*    */ 
/*    */   
/*    */   public NativePacketPayload readMessage(Optional<NativePacketPayload> reuse, NativePacketHeader header) throws IOException {
/* 62 */     NativePacketPayload buf = (NativePacketPayload)this.packetReader.readMessage(reuse, header);
/* 63 */     this.lastPacketReceivedTimeMs = System.currentTimeMillis();
/* 64 */     return buf;
/*    */   }
/*    */ 
/*    */   
/*    */   public NativePacketPayload probeMessage(Optional<NativePacketPayload> reuse, NativePacketHeader header) throws IOException {
/* 69 */     NativePacketPayload buf = (NativePacketPayload)this.packetReader.probeMessage(reuse, header);
/* 70 */     this.lastPacketReceivedTimeMs = System.currentTimeMillis();
/* 71 */     return buf;
/*    */   }
/*    */ 
/*    */   
/*    */   public long getLastPacketReceivedTime() {
/* 76 */     return this.lastPacketReceivedTimeMs;
/*    */   }
/*    */ 
/*    */   
/*    */   public byte getMessageSequence() {
/* 81 */     return this.packetReader.getMessageSequence();
/*    */   }
/*    */ 
/*    */   
/*    */   public void resetMessageSequence() {
/* 86 */     this.packetReader.resetMessageSequence();
/*    */   }
/*    */ 
/*    */   
/*    */   public MessageReader<NativePacketHeader, NativePacketPayload> undecorateAll() {
/* 91 */     return this.packetReader.undecorateAll();
/*    */   }
/*    */ 
/*    */   
/*    */   public MessageReader<NativePacketHeader, NativePacketPayload> undecorate() {
/* 96 */     return this.packetReader;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\protocol\a\TimeTrackingPacketReader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */