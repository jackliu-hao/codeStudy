/*     */ package com.mysql.cj.protocol.a;
/*     */ 
/*     */ import com.mysql.cj.Messages;
/*     */ import com.mysql.cj.protocol.Message;
/*     */ import com.mysql.cj.protocol.MessageHeader;
/*     */ import com.mysql.cj.protocol.MessageReader;
/*     */ import java.io.IOException;
/*     */ import java.util.Optional;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class MultiPacketReader
/*     */   implements MessageReader<NativePacketHeader, NativePacketPayload>
/*     */ {
/*     */   private MessageReader<NativePacketHeader, NativePacketPayload> packetReader;
/*     */   
/*     */   public MultiPacketReader(MessageReader<NativePacketHeader, NativePacketPayload> packetReader) {
/*  49 */     this.packetReader = packetReader;
/*     */   }
/*     */ 
/*     */   
/*     */   public NativePacketHeader readHeader() throws IOException {
/*  54 */     return (NativePacketHeader)this.packetReader.readHeader();
/*     */   }
/*     */ 
/*     */   
/*     */   public NativePacketHeader probeHeader() throws IOException {
/*  59 */     return (NativePacketHeader)this.packetReader.probeHeader();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public NativePacketPayload readMessage(Optional<NativePacketPayload> reuse, NativePacketHeader header) throws IOException {
/*  65 */     int packetLength = header.getMessageSize();
/*  66 */     NativePacketPayload buf = (NativePacketPayload)this.packetReader.readMessage(reuse, header);
/*     */     
/*  68 */     if (packetLength == 16777215) {
/*     */       
/*  70 */       buf.setPosition(16777215);
/*     */       
/*  72 */       NativePacketPayload multiPacket = null;
/*  73 */       int multiPacketLength = -1;
/*  74 */       byte multiPacketSeq = getMessageSequence();
/*     */       
/*     */       do {
/*  77 */         NativePacketHeader hdr = readHeader();
/*  78 */         multiPacketLength = hdr.getMessageSize();
/*     */         
/*  80 */         if (multiPacket == null) {
/*  81 */           multiPacket = new NativePacketPayload(multiPacketLength);
/*     */         }
/*     */         
/*  84 */         multiPacketSeq = (byte)(multiPacketSeq + 1);
/*  85 */         if (multiPacketSeq != hdr.getMessageSequence()) {
/*  86 */           throw new IOException(Messages.getString("PacketReader.10"));
/*     */         }
/*     */         
/*  89 */         this.packetReader.readMessage(Optional.of(multiPacket), hdr);
/*     */         
/*  91 */         buf.writeBytes(NativeConstants.StringLengthDataType.STRING_FIXED, multiPacket.getByteBuffer(), 0, multiPacketLength);
/*     */       }
/*  93 */       while (multiPacketLength == 16777215);
/*     */       
/*  95 */       buf.setPosition(0);
/*     */     } 
/*     */     
/*  98 */     return buf;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public NativePacketPayload probeMessage(Optional<NativePacketPayload> reuse, NativePacketHeader header) throws IOException {
/* 104 */     int packetLength = header.getMessageSize();
/* 105 */     NativePacketPayload buf = (NativePacketPayload)this.packetReader.probeMessage(reuse, header);
/*     */     
/* 107 */     if (packetLength == 16777215) {
/*     */       
/* 109 */       buf.setPosition(16777215);
/*     */       
/* 111 */       NativePacketPayload multiPacket = null;
/* 112 */       int multiPacketLength = -1;
/* 113 */       byte multiPacketSeq = getMessageSequence();
/*     */       
/*     */       do {
/* 116 */         NativePacketHeader hdr = readHeader();
/* 117 */         multiPacketLength = hdr.getMessageSize();
/*     */         
/* 119 */         if (multiPacket == null) {
/* 120 */           multiPacket = new NativePacketPayload(multiPacketLength);
/*     */         }
/*     */         
/* 123 */         multiPacketSeq = (byte)(multiPacketSeq + 1);
/* 124 */         if (multiPacketSeq != hdr.getMessageSequence()) {
/* 125 */           throw new IOException(Messages.getString("PacketReader.10"));
/*     */         }
/*     */         
/* 128 */         this.packetReader.probeMessage(Optional.of(multiPacket), hdr);
/*     */         
/* 130 */         buf.writeBytes(NativeConstants.StringLengthDataType.STRING_FIXED, multiPacket.getByteBuffer(), 0, multiPacketLength);
/*     */       }
/* 132 */       while (multiPacketLength == 16777215);
/*     */       
/* 134 */       buf.setPosition(0);
/*     */     } 
/*     */     
/* 137 */     return buf;
/*     */   }
/*     */ 
/*     */   
/*     */   public byte getMessageSequence() {
/* 142 */     return this.packetReader.getMessageSequence();
/*     */   }
/*     */ 
/*     */   
/*     */   public void resetMessageSequence() {
/* 147 */     this.packetReader.resetMessageSequence();
/*     */   }
/*     */ 
/*     */   
/*     */   public MessageReader<NativePacketHeader, NativePacketPayload> undecorateAll() {
/* 152 */     return this.packetReader.undecorateAll();
/*     */   }
/*     */ 
/*     */   
/*     */   public MessageReader<NativePacketHeader, NativePacketPayload> undecorate() {
/* 157 */     return this.packetReader;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\protocol\a\MultiPacketReader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */