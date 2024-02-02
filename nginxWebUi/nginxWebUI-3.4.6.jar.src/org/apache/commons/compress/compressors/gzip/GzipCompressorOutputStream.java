/*     */ package org.apache.commons.compress.compressors.gzip;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.ByteOrder;
/*     */ import java.nio.charset.StandardCharsets;
/*     */ import java.util.zip.CRC32;
/*     */ import java.util.zip.Deflater;
/*     */ import org.apache.commons.compress.compressors.CompressorOutputStream;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class GzipCompressorOutputStream
/*     */   extends CompressorOutputStream
/*     */ {
/*     */   private static final int FNAME = 8;
/*     */   private static final int FCOMMENT = 16;
/*     */   private final OutputStream out;
/*     */   private final Deflater deflater;
/*     */   private final byte[] deflateBuffer;
/*     */   private boolean closed;
/*  62 */   private final CRC32 crc = new CRC32();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public GzipCompressorOutputStream(OutputStream out) throws IOException {
/*  70 */     this(out, new GzipParameters());
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
/*     */   public GzipCompressorOutputStream(OutputStream out, GzipParameters parameters) throws IOException {
/*  82 */     this.out = out;
/*  83 */     this.deflater = new Deflater(parameters.getCompressionLevel(), true);
/*  84 */     this.deflateBuffer = new byte[parameters.getBufferSize()];
/*  85 */     writeHeader(parameters);
/*     */   }
/*     */   
/*     */   private void writeHeader(GzipParameters parameters) throws IOException {
/*  89 */     String filename = parameters.getFilename();
/*  90 */     String comment = parameters.getComment();
/*     */     
/*  92 */     ByteBuffer buffer = ByteBuffer.allocate(10);
/*  93 */     buffer.order(ByteOrder.LITTLE_ENDIAN);
/*  94 */     buffer.putShort((short)-29921);
/*  95 */     buffer.put((byte)8);
/*  96 */     buffer.put((byte)(((filename != null) ? 8 : 0) | ((comment != null) ? 16 : 0)));
/*  97 */     buffer.putInt((int)(parameters.getModificationTime() / 1000L));
/*     */ 
/*     */     
/* 100 */     int compressionLevel = parameters.getCompressionLevel();
/* 101 */     if (compressionLevel == 9) {
/* 102 */       buffer.put((byte)2);
/* 103 */     } else if (compressionLevel == 1) {
/* 104 */       buffer.put((byte)4);
/*     */     } else {
/* 106 */       buffer.put((byte)0);
/*     */     } 
/*     */     
/* 109 */     buffer.put((byte)parameters.getOperatingSystem());
/*     */     
/* 111 */     this.out.write(buffer.array());
/*     */     
/* 113 */     if (filename != null) {
/* 114 */       this.out.write(filename.getBytes(StandardCharsets.ISO_8859_1));
/* 115 */       this.out.write(0);
/*     */     } 
/*     */     
/* 118 */     if (comment != null) {
/* 119 */       this.out.write(comment.getBytes(StandardCharsets.ISO_8859_1));
/* 120 */       this.out.write(0);
/*     */     } 
/*     */   }
/*     */   
/*     */   private void writeTrailer() throws IOException {
/* 125 */     ByteBuffer buffer = ByteBuffer.allocate(8);
/* 126 */     buffer.order(ByteOrder.LITTLE_ENDIAN);
/* 127 */     buffer.putInt((int)this.crc.getValue());
/* 128 */     buffer.putInt(this.deflater.getTotalIn());
/*     */     
/* 130 */     this.out.write(buffer.array());
/*     */   }
/*     */ 
/*     */   
/*     */   public void write(int b) throws IOException {
/* 135 */     write(new byte[] { (byte)(b & 0xFF) }, 0, 1);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void write(byte[] buffer) throws IOException {
/* 145 */     write(buffer, 0, buffer.length);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void write(byte[] buffer, int offset, int length) throws IOException {
/* 155 */     if (this.deflater.finished()) {
/* 156 */       throw new IOException("Cannot write more data, the end of the compressed data stream has been reached");
/*     */     }
/*     */     
/* 159 */     if (length > 0) {
/* 160 */       this.deflater.setInput(buffer, offset, length);
/*     */       
/* 162 */       while (!this.deflater.needsInput()) {
/* 163 */         deflate();
/*     */       }
/*     */       
/* 166 */       this.crc.update(buffer, offset, length);
/*     */     } 
/*     */   }
/*     */   
/*     */   private void deflate() throws IOException {
/* 171 */     int length = this.deflater.deflate(this.deflateBuffer, 0, this.deflateBuffer.length);
/* 172 */     if (length > 0) {
/* 173 */       this.out.write(this.deflateBuffer, 0, length);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void finish() throws IOException {
/* 184 */     if (!this.deflater.finished()) {
/* 185 */       this.deflater.finish();
/*     */       
/* 187 */       while (!this.deflater.finished()) {
/* 188 */         deflate();
/*     */       }
/*     */       
/* 191 */       writeTrailer();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void flush() throws IOException {
/* 202 */     this.out.flush();
/*     */   }
/*     */ 
/*     */   
/*     */   public void close() throws IOException {
/* 207 */     if (!this.closed)
/*     */       try {
/* 209 */         finish();
/*     */       } finally {
/* 211 */         this.deflater.end();
/* 212 */         this.out.close();
/* 213 */         this.closed = true;
/*     */       }  
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\compressors\gzip\GzipCompressorOutputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */