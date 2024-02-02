/*     */ package com.mysql.cj.protocol.x;
/*     */ 
/*     */ import com.google.protobuf.ByteString;
/*     */ import com.mysql.cj.x.protobuf.Mysqlx;
/*     */ import com.mysql.cj.x.protobuf.MysqlxConnection;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.FilterOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.ByteOrder;
/*     */ import java.util.Arrays;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ public class CompressionSplittedOutputStream
/*     */   extends FilterOutputStream
/*     */ {
/*     */   private CompressorStreamsFactory compressorIoStreamsFactory;
/*  54 */   private byte[] frameHeader = new byte[5];
/*  55 */   private int frameHeaderBuffered = 0;
/*  56 */   private int frameHeaderDumped = 0;
/*  57 */   private int framePayloadLength = 0;
/*  58 */   private int framePayloadDumped = 0;
/*  59 */   private XMessageHeader xMessageHeader = null;
/*     */   
/*     */   private boolean compressionEnabled = false;
/*     */   
/*  63 */   private ByteArrayOutputStream bufferOut = null;
/*  64 */   private OutputStream compressorOut = null;
/*     */   
/*  66 */   private byte[] singleByte = new byte[1];
/*     */   
/*     */   private boolean closed = false;
/*     */   
/*     */   public CompressionSplittedOutputStream(OutputStream out, CompressorStreamsFactory ioStreamsFactory) {
/*  71 */     super(out);
/*  72 */     this.compressorIoStreamsFactory = ioStreamsFactory;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void close() throws IOException {
/*  82 */     if (!this.closed) {
/*  83 */       super.close();
/*  84 */       this.out = null;
/*  85 */       this.bufferOut = null;
/*  86 */       if (this.compressorOut != null) {
/*  87 */         this.compressorOut.close();
/*     */       }
/*  89 */       this.compressorOut = null;
/*  90 */       this.closed = true;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void write(int b) throws IOException {
/* 101 */     ensureOpen();
/* 102 */     this.singleByte[0] = (byte)b;
/* 103 */     write(this.singleByte, 0, 1);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void write(byte[] b) throws IOException {
/* 113 */     ensureOpen();
/* 114 */     write(b, 0, b.length);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void write(byte[] b, int off, int len) throws IOException {
/* 125 */     ensureOpen();
/* 126 */     if ((off | len | b.length - len + off | off + len) < 0) {
/* 127 */       throw new IndexOutOfBoundsException();
/*     */     }
/*     */     
/* 130 */     int bytesProcessed = peekFrameHeader(b, off, len);
/*     */     
/* 132 */     if (isFrameHeaderBuffered() && !isFrameHeaderWriteComplete()) {
/* 133 */       this.xMessageHeader = new XMessageHeader(this.frameHeader);
/* 134 */       this.framePayloadLength = this.xMessageHeader.getMessageSize();
/* 135 */       this.framePayloadDumped = 0;
/*     */       
/* 137 */       this.compressionEnabled = (this.framePayloadLength >= 250);
/*     */       
/* 139 */       if (this.compressionEnabled) {
/* 140 */         this.bufferOut = new ByteArrayOutputStream();
/* 141 */         this.compressorOut = this.compressorIoStreamsFactory.getOutputStreamInstance(this.bufferOut);
/* 142 */         this.compressorOut.write(this.frameHeader, 0, 5);
/*     */       } else {
/* 144 */         this.out.write(this.frameHeader, 0, 5);
/*     */       } 
/* 146 */       this.frameHeaderDumped = 5;
/*     */     } 
/*     */     
/* 149 */     int bytesToDump = len - bytesProcessed;
/* 150 */     if (bytesToDump > 0) {
/* 151 */       if (this.compressionEnabled) {
/* 152 */         this.compressorOut.write(b, off + bytesProcessed, bytesToDump);
/*     */       } else {
/* 154 */         this.out.write(b, off + bytesProcessed, bytesToDump);
/*     */       } 
/*     */     }
/* 157 */     this.framePayloadDumped += bytesToDump;
/*     */     
/* 159 */     finalizeWrite();
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
/*     */   private int peekFrameHeader(byte[] b, int off, int len) {
/* 175 */     if (isPayloadWriteReady()) {
/* 176 */       return 0;
/*     */     }
/*     */     
/* 179 */     int toCollect = 0;
/* 180 */     if (this.frameHeaderBuffered < 5) {
/* 181 */       toCollect = Math.min(len, 5 - this.frameHeaderBuffered);
/* 182 */       System.arraycopy(b, off, this.frameHeader, this.frameHeaderBuffered, toCollect);
/* 183 */       this.frameHeaderBuffered += toCollect;
/*     */     } 
/* 185 */     return toCollect;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean isFrameHeaderBuffered() {
/* 195 */     return (this.frameHeaderBuffered == 5);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean isFrameHeaderWriteComplete() {
/* 205 */     return (this.frameHeaderDumped == 5);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean isPayloadWriteReady() {
/* 215 */     return (isFrameHeaderWriteComplete() && this.framePayloadDumped < this.framePayloadLength);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean isWriteComplete() {
/* 225 */     return (isFrameHeaderWriteComplete() && this.framePayloadDumped >= this.framePayloadLength);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void finalizeWrite() throws IOException {
/* 236 */     if (isWriteComplete()) {
/* 237 */       if (this.compressionEnabled) {
/* 238 */         this.compressorOut.close();
/* 239 */         this.compressorOut = null;
/*     */         
/* 241 */         byte[] compressedData = this.bufferOut.toByteArray();
/*     */ 
/*     */         
/* 244 */         MysqlxConnection.Compression compressedMessage = MysqlxConnection.Compression.newBuilder().setUncompressedSize((5 + this.framePayloadLength)).setClientMessages(Mysqlx.ClientMessages.Type.forNumber(this.xMessageHeader.getMessageType())).setPayload(ByteString.copyFrom(compressedData)).build();
/*     */         
/* 246 */         ByteBuffer messageHeader = ByteBuffer.allocate(5).order(ByteOrder.LITTLE_ENDIAN);
/* 247 */         messageHeader.putInt(compressedMessage.getSerializedSize() + 1);
/* 248 */         messageHeader.put((byte)46);
/*     */         
/* 250 */         this.out.write(messageHeader.array());
/* 251 */         compressedMessage.writeTo(this.out);
/* 252 */         this.out.flush();
/*     */         
/* 254 */         this.compressionEnabled = false;
/*     */       } 
/*     */       
/* 257 */       Arrays.fill(this.frameHeader, (byte)0);
/* 258 */       this.frameHeaderBuffered = 0;
/* 259 */       this.frameHeaderDumped = 0;
/* 260 */       this.framePayloadLength = 0;
/* 261 */       this.framePayloadDumped = 0;
/* 262 */       this.xMessageHeader = null;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void ensureOpen() throws IOException {
/* 273 */     if (this.closed)
/* 274 */       throw new IOException("Stream closed"); 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\protocol\x\CompressionSplittedOutputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */