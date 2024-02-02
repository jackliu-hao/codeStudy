/*     */ package com.mysql.cj.protocol.a;
/*     */ 
/*     */ import com.mysql.cj.Messages;
/*     */ import com.mysql.cj.conf.RuntimeProperty;
/*     */ import com.mysql.cj.protocol.Message;
/*     */ import com.mysql.cj.protocol.MessageHeader;
/*     */ import com.mysql.cj.protocol.MessageReader;
/*     */ import com.mysql.cj.util.StringUtils;
/*     */ import java.io.IOException;
/*     */ import java.util.LinkedList;
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
/*     */ public class DebugBufferingPacketReader
/*     */   implements MessageReader<NativePacketHeader, NativePacketPayload>
/*     */ {
/*     */   private static final int MAX_PACKET_DUMP_LENGTH = 1024;
/*     */   private static final int DEBUG_MSG_LEN = 96;
/*     */   private MessageReader<NativePacketHeader, NativePacketPayload> packetReader;
/*     */   private LinkedList<StringBuilder> packetDebugBuffer;
/*     */   private RuntimeProperty<Integer> packetDebugBufferSize;
/*  53 */   private String lastHeaderPayload = "";
/*     */   
/*     */   private boolean packetSequenceReset = false;
/*     */ 
/*     */   
/*     */   public DebugBufferingPacketReader(MessageReader<NativePacketHeader, NativePacketPayload> packetReader, LinkedList<StringBuilder> packetDebugBuffer, RuntimeProperty<Integer> packetDebugBufferSize) {
/*  59 */     this.packetReader = packetReader;
/*  60 */     this.packetDebugBuffer = packetDebugBuffer;
/*  61 */     this.packetDebugBufferSize = packetDebugBufferSize;
/*     */   }
/*     */ 
/*     */   
/*     */   public NativePacketHeader readHeader() throws IOException {
/*  66 */     byte prevPacketSeq = this.packetReader.getMessageSequence();
/*  67 */     return readHeaderLocal(prevPacketSeq, (NativePacketHeader)this.packetReader.readHeader());
/*     */   }
/*     */ 
/*     */   
/*     */   public NativePacketHeader probeHeader() throws IOException {
/*  72 */     byte prevPacketSeq = this.packetReader.getMessageSequence();
/*  73 */     return readHeaderLocal(prevPacketSeq, (NativePacketHeader)this.packetReader.probeHeader());
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private NativePacketHeader readHeaderLocal(byte prevPacketSeq, NativePacketHeader hdr) throws IOException {
/*  79 */     byte currPacketSeq = hdr.getMessageSequence();
/*  80 */     if (!this.packetSequenceReset) {
/*     */       
/*  82 */       if (currPacketSeq == Byte.MIN_VALUE && prevPacketSeq != Byte.MAX_VALUE) {
/*  83 */         throw new IOException(Messages.getString("PacketReader.9", new Object[] { "-128", Byte.valueOf(currPacketSeq) }));
/*     */       }
/*     */       
/*  86 */       if (prevPacketSeq == -1 && currPacketSeq != 0) {
/*  87 */         throw new IOException(Messages.getString("PacketReader.9", new Object[] { "-1", Byte.valueOf(currPacketSeq) }));
/*     */       }
/*     */       
/*  90 */       if (currPacketSeq != Byte.MIN_VALUE && prevPacketSeq != -1 && currPacketSeq != prevPacketSeq + 1) {
/*  91 */         throw new IOException(Messages.getString("PacketReader.9", new Object[] { Integer.valueOf(prevPacketSeq + 1), Byte.valueOf(currPacketSeq) }));
/*     */       }
/*     */     } else {
/*     */       
/*  95 */       this.packetSequenceReset = false;
/*     */     } 
/*     */     
/*  98 */     this.lastHeaderPayload = StringUtils.dumpAsHex(hdr.getBuffer().array(), 4);
/*     */     
/* 100 */     return hdr;
/*     */   }
/*     */ 
/*     */   
/*     */   public NativePacketPayload readMessage(Optional<NativePacketPayload> reuse, NativePacketHeader header) throws IOException {
/* 105 */     int packetLength = header.getMessageSize();
/* 106 */     NativePacketPayload buf = (NativePacketPayload)this.packetReader.readMessage(reuse, header);
/*     */     
/* 108 */     int bytesToDump = Math.min(1024, packetLength);
/* 109 */     String PacketPayloadImpl = StringUtils.dumpAsHex(buf.getByteBuffer(), bytesToDump);
/*     */     
/* 111 */     StringBuilder packetDump = new StringBuilder(100 + PacketPayloadImpl.length());
/* 112 */     packetDump.append("Server ");
/* 113 */     packetDump.append(reuse.isPresent() ? "(re-used) " : "(new) ");
/* 114 */     packetDump.append(buf.toString());
/* 115 */     packetDump.append(" --------------------> Client\n");
/* 116 */     packetDump.append("\nPacket payload:\n\n");
/* 117 */     packetDump.append(this.lastHeaderPayload);
/* 118 */     packetDump.append(PacketPayloadImpl);
/*     */     
/* 120 */     if (bytesToDump == 1024) {
/* 121 */       packetDump.append("\nNote: Packet of " + packetLength + " bytes truncated to " + 'Ѐ' + " bytes.\n");
/*     */     }
/*     */     
/* 124 */     if (this.packetDebugBuffer.size() + 1 > ((Integer)this.packetDebugBufferSize.getValue()).intValue()) {
/* 125 */       this.packetDebugBuffer.removeFirst();
/*     */     }
/*     */     
/* 128 */     this.packetDebugBuffer.addLast(packetDump);
/*     */     
/* 130 */     return buf;
/*     */   }
/*     */ 
/*     */   
/*     */   public NativePacketPayload probeMessage(Optional<NativePacketPayload> reuse, NativePacketHeader header) throws IOException {
/* 135 */     int packetLength = header.getMessageSize();
/* 136 */     NativePacketPayload buf = (NativePacketPayload)this.packetReader.probeMessage(reuse, header);
/*     */     
/* 138 */     int bytesToDump = Math.min(1024, packetLength);
/* 139 */     String PacketPayloadImpl = StringUtils.dumpAsHex(buf.getByteBuffer(), bytesToDump);
/*     */     
/* 141 */     StringBuilder packetDump = new StringBuilder(100 + PacketPayloadImpl.length());
/* 142 */     packetDump.append("Server ");
/* 143 */     packetDump.append(reuse.isPresent() ? "(re-used) " : "(new) ");
/* 144 */     packetDump.append(buf.toString());
/* 145 */     packetDump.append(" --------------------> Client\n");
/* 146 */     packetDump.append("\nPacket payload:\n\n");
/* 147 */     packetDump.append(this.lastHeaderPayload);
/* 148 */     packetDump.append(PacketPayloadImpl);
/*     */     
/* 150 */     if (bytesToDump == 1024) {
/* 151 */       packetDump.append("\nNote: Packet of " + packetLength + " bytes truncated to " + 'Ѐ' + " bytes.\n");
/*     */     }
/*     */     
/* 154 */     if (this.packetDebugBuffer.size() + 1 > ((Integer)this.packetDebugBufferSize.getValue()).intValue()) {
/* 155 */       this.packetDebugBuffer.removeFirst();
/*     */     }
/*     */     
/* 158 */     this.packetDebugBuffer.addLast(packetDump);
/*     */     
/* 160 */     return buf;
/*     */   }
/*     */ 
/*     */   
/*     */   public byte getMessageSequence() {
/* 165 */     return this.packetReader.getMessageSequence();
/*     */   }
/*     */ 
/*     */   
/*     */   public void resetMessageSequence() {
/* 170 */     this.packetReader.resetMessageSequence();
/* 171 */     this.packetSequenceReset = true;
/*     */   }
/*     */ 
/*     */   
/*     */   public MessageReader<NativePacketHeader, NativePacketPayload> undecorateAll() {
/* 176 */     return this.packetReader.undecorateAll();
/*     */   }
/*     */ 
/*     */   
/*     */   public MessageReader<NativePacketHeader, NativePacketPayload> undecorate() {
/* 181 */     return this.packetReader;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\protocol\a\DebugBufferingPacketReader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */