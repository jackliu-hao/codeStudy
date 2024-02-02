/*     */ package com.mysql.cj.protocol.a;
/*     */ 
/*     */ import com.mysql.cj.protocol.MessageSender;
/*     */ import java.io.BufferedOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.util.zip.Deflater;
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
/*     */ public class CompressedPacketSender
/*     */   implements MessageSender<NativePacketPayload>
/*     */ {
/*     */   private BufferedOutputStream outputStream;
/*  45 */   private Deflater deflater = new Deflater();
/*     */   
/*     */   private byte[] compressedPacket;
/*     */   
/*  49 */   private byte compressedSequenceId = 0;
/*     */   
/*  51 */   private int compressedPayloadLen = 0;
/*     */   
/*     */   public static final int COMP_HEADER_LENGTH = 7;
/*     */   public static final int MIN_COMPRESS_LEN = 50;
/*     */   
/*     */   public CompressedPacketSender(BufferedOutputStream outputStream) {
/*  57 */     this.outputStream = outputStream;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void stop() {
/*  64 */     this.deflater.end();
/*  65 */     this.deflater = null;
/*     */   }
/*     */   
/*     */   private void resetPacket() {
/*  69 */     this.compressedPayloadLen = 0;
/*  70 */     this.deflater.reset();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void addUncompressedHeader(byte packetSequence, int uncompressedPacketLen) {
/*  82 */     byte[] uncompressedHeader = new byte[4];
/*  83 */     NativeUtils.encodeMysqlThreeByteInteger(uncompressedPacketLen, uncompressedHeader, 0);
/*  84 */     uncompressedHeader[3] = packetSequence;
/*  85 */     this.deflater.setInput(uncompressedHeader);
/*  86 */     this.compressedPayloadLen += this.deflater.deflate(this.compressedPacket, this.compressedPayloadLen, this.compressedPacket.length - this.compressedPayloadLen);
/*     */   }
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
/*     */   private void addPayload(byte[] payload, int payloadOffset, int payloadLen) {
/* 101 */     this.deflater.setInput(payload, payloadOffset, payloadLen);
/* 102 */     this.compressedPayloadLen += this.deflater.deflate(this.compressedPacket, this.compressedPayloadLen, this.compressedPacket.length - this.compressedPayloadLen);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void completeCompression() {
/* 110 */     this.deflater.finish();
/* 111 */     this.compressedPayloadLen += this.deflater.deflate(this.compressedPacket, this.compressedPayloadLen, this.compressedPacket.length - this.compressedPayloadLen);
/*     */   }
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
/*     */   private void writeCompressedHeader(int compLen, byte seq, int uncompLen) throws IOException {
/* 128 */     this.outputStream.write(NativeUtils.encodeMysqlThreeByteInteger(compLen));
/* 129 */     this.outputStream.write(seq);
/* 130 */     this.outputStream.write(NativeUtils.encodeMysqlThreeByteInteger(uncompLen));
/*     */   }
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
/*     */   private void writeUncompressedHeader(int packetLen, byte packetSequence) throws IOException {
/* 144 */     this.outputStream.write(NativeUtils.encodeMysqlThreeByteInteger(packetLen));
/* 145 */     this.outputStream.write(packetSequence);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void sendCompressedPacket(int uncompressedPayloadLen) throws IOException {
/* 157 */     this.compressedSequenceId = (byte)(this.compressedSequenceId + 1); writeCompressedHeader(this.compressedPayloadLen, this.compressedSequenceId, uncompressedPayloadLen);
/*     */ 
/*     */     
/* 160 */     this.outputStream.write(this.compressedPacket, 0, this.compressedPayloadLen);
/*     */   }
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
/*     */   public void send(byte[] packet, int packetLen, byte packetSequence) throws IOException {
/* 181 */     this.compressedSequenceId = packetSequence;
/*     */ 
/*     */     
/* 184 */     if (packetLen < 50) {
/* 185 */       writeCompressedHeader(packetLen + 4, this.compressedSequenceId, 0);
/* 186 */       writeUncompressedHeader(packetLen, packetSequence);
/* 187 */       this.outputStream.write(packet, 0, packetLen);
/* 188 */       this.outputStream.flush();
/*     */       
/*     */       return;
/*     */     } 
/* 192 */     if (packetLen + 4 > 16777215) {
/* 193 */       this.compressedPacket = new byte[16777215];
/*     */     } else {
/* 195 */       this.compressedPacket = new byte[4 + packetLen];
/*     */     } 
/*     */     
/* 198 */     PacketSplitter packetSplitter = new PacketSplitter(packetLen);
/*     */     
/* 200 */     int unsentPayloadLen = 0;
/* 201 */     int unsentOffset = 0;
/*     */     
/*     */     while (true) {
/* 204 */       this.compressedPayloadLen = 0;
/*     */       
/* 206 */       if (packetSplitter.nextPacket())
/*     */       
/* 208 */       { if (unsentPayloadLen > 0) {
/* 209 */           addPayload(packet, unsentOffset, unsentPayloadLen);
/*     */         }
/*     */ 
/*     */         
/* 213 */         int remaining = 16777215 - unsentPayloadLen;
/*     */ 
/*     */         
/* 216 */         int len = Math.min(remaining, 4 + packetSplitter.getPacketLen());
/* 217 */         int lenNoHdr = len - 4;
/* 218 */         addUncompressedHeader(packetSequence, packetSplitter.getPacketLen());
/* 219 */         addPayload(packet, packetSplitter.getOffset(), lenNoHdr);
/*     */         
/* 221 */         completeCompression();
/*     */         
/* 223 */         if (this.compressedPayloadLen >= len) {
/*     */           
/* 225 */           this.compressedSequenceId = (byte)(this.compressedSequenceId + 1); writeCompressedHeader(unsentPayloadLen + len, this.compressedSequenceId, 0);
/* 226 */           this.outputStream.write(packet, unsentOffset, unsentPayloadLen);
/* 227 */           writeUncompressedHeader(lenNoHdr, packetSequence);
/* 228 */           this.outputStream.write(packet, packetSplitter.getOffset(), lenNoHdr);
/*     */         } else {
/* 230 */           sendCompressedPacket(len + unsentPayloadLen);
/*     */         } 
/*     */         
/* 233 */         packetSequence = (byte)(packetSequence + 1);
/* 234 */         unsentPayloadLen = packetSplitter.getPacketLen() - lenNoHdr;
/* 235 */         unsentOffset = packetSplitter.getOffset() + lenNoHdr;
/* 236 */         resetPacket(); continue; }  break;
/* 237 */     }  if (unsentPayloadLen > 0) {
/*     */       
/* 239 */       addPayload(packet, unsentOffset, unsentPayloadLen);
/* 240 */       completeCompression();
/* 241 */       if (this.compressedPayloadLen >= unsentPayloadLen) {
/* 242 */         writeCompressedHeader(unsentPayloadLen, this.compressedSequenceId, 0);
/* 243 */         this.outputStream.write(packet, unsentOffset, unsentPayloadLen);
/*     */       } else {
/* 245 */         sendCompressedPacket(unsentPayloadLen);
/*     */       } 
/* 247 */       resetPacket();
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 255 */     this.outputStream.flush();
/*     */ 
/*     */     
/* 258 */     this.compressedPacket = null;
/*     */   }
/*     */ 
/*     */   
/*     */   public MessageSender<NativePacketPayload> undecorateAll() {
/* 263 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public MessageSender<NativePacketPayload> undecorate() {
/* 268 */     return this;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\protocol\a\CompressedPacketSender.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */