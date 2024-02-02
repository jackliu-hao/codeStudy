/*     */ package com.mysql.cj.protocol.a;
/*     */ 
/*     */ import com.mysql.cj.Messages;
/*     */ import com.mysql.cj.log.Log;
/*     */ import com.mysql.cj.protocol.Message;
/*     */ import com.mysql.cj.protocol.MessageHeader;
/*     */ import com.mysql.cj.protocol.MessageReader;
/*     */ import com.mysql.cj.util.StringUtils;
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
/*     */ public class TracingPacketReader
/*     */   implements MessageReader<NativePacketHeader, NativePacketPayload>
/*     */ {
/*     */   private static final int MAX_PACKET_DUMP_LENGTH = 1024;
/*     */   private MessageReader<NativePacketHeader, NativePacketPayload> packetReader;
/*     */   private Log log;
/*     */   
/*     */   public TracingPacketReader(MessageReader<NativePacketHeader, NativePacketPayload> packetReader, Log log) {
/*  52 */     this.packetReader = packetReader;
/*  53 */     this.log = log;
/*     */   }
/*     */ 
/*     */   
/*     */   public NativePacketHeader readHeader() throws IOException {
/*  58 */     return traceHeader((NativePacketHeader)this.packetReader.readHeader());
/*     */   }
/*     */ 
/*     */   
/*     */   public NativePacketHeader probeHeader() throws IOException {
/*  63 */     return traceHeader((NativePacketHeader)this.packetReader.probeHeader());
/*     */   }
/*     */   
/*     */   private NativePacketHeader traceHeader(NativePacketHeader hdr) throws IOException {
/*  67 */     StringBuilder traceMessageBuf = new StringBuilder();
/*     */     
/*  69 */     traceMessageBuf.append(Messages.getString("PacketReader.3"));
/*  70 */     traceMessageBuf.append(hdr.getMessageSize());
/*  71 */     traceMessageBuf.append(Messages.getString("PacketReader.4"));
/*  72 */     traceMessageBuf.append(StringUtils.dumpAsHex(hdr.getBuffer().array(), 4));
/*     */     
/*  74 */     this.log.logTrace(traceMessageBuf.toString());
/*     */     
/*  76 */     return hdr;
/*     */   }
/*     */ 
/*     */   
/*     */   public NativePacketPayload readMessage(Optional<NativePacketPayload> reuse, NativePacketHeader header) throws IOException {
/*  81 */     return traceMessage((NativePacketPayload)this.packetReader.readMessage(reuse, header), header.getMessageSize(), reuse.isPresent());
/*     */   }
/*     */ 
/*     */   
/*     */   public NativePacketPayload probeMessage(Optional<NativePacketPayload> reuse, NativePacketHeader header) throws IOException {
/*  86 */     return traceMessage((NativePacketPayload)this.packetReader.probeMessage(reuse, header), header.getMessageSize(), reuse.isPresent());
/*     */   }
/*     */   
/*     */   private NativePacketPayload traceMessage(NativePacketPayload buf, int packetLength, boolean reuse) throws IOException {
/*  90 */     StringBuilder traceMessageBuf = new StringBuilder();
/*     */     
/*  92 */     traceMessageBuf.append(Messages.getString(reuse ? "PacketReader.5" : "PacketReader.6"));
/*  93 */     traceMessageBuf.append(StringUtils.dumpAsHex(buf.getByteBuffer(), (packetLength < 1024) ? packetLength : 1024));
/*     */     
/*  95 */     if (packetLength > 1024) {
/*  96 */       traceMessageBuf.append(Messages.getString("PacketReader.7"));
/*  97 */       traceMessageBuf.append(1024);
/*  98 */       traceMessageBuf.append(Messages.getString("PacketReader.8"));
/*     */     } 
/*     */     
/* 101 */     this.log.logTrace(traceMessageBuf.toString());
/*     */     
/* 103 */     return buf;
/*     */   }
/*     */ 
/*     */   
/*     */   public byte getMessageSequence() {
/* 108 */     return this.packetReader.getMessageSequence();
/*     */   }
/*     */ 
/*     */   
/*     */   public void resetMessageSequence() {
/* 113 */     this.packetReader.resetMessageSequence();
/*     */   }
/*     */ 
/*     */   
/*     */   public MessageReader<NativePacketHeader, NativePacketPayload> undecorateAll() {
/* 118 */     return this.packetReader.undecorateAll();
/*     */   }
/*     */ 
/*     */   
/*     */   public MessageReader<NativePacketHeader, NativePacketPayload> undecorate() {
/* 123 */     return this.packetReader;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\protocol\a\TracingPacketReader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */