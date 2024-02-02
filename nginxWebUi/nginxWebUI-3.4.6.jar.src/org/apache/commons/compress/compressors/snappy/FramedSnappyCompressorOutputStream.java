/*     */ package org.apache.commons.compress.compressors.snappy;
/*     */ 
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
/*     */ import org.apache.commons.compress.compressors.CompressorOutputStream;
/*     */ import org.apache.commons.compress.compressors.lz77support.Parameters;
/*     */ import org.apache.commons.compress.utils.ByteUtils;
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
/*     */ public class FramedSnappyCompressorOutputStream
/*     */   extends CompressorOutputStream
/*     */ {
/*     */   private static final int MAX_COMPRESSED_BUFFER_SIZE = 65536;
/*     */   private final OutputStream out;
/*     */   private final Parameters params;
/*  47 */   private final PureJavaCrc32C checksum = new PureJavaCrc32C();
/*     */   
/*  49 */   private final byte[] oneByte = new byte[1];
/*  50 */   private final byte[] buffer = new byte[65536];
/*     */ 
/*     */ 
/*     */   
/*     */   private int currentIndex;
/*     */ 
/*     */   
/*     */   private final ByteUtils.ByteConsumer consumer;
/*     */ 
/*     */ 
/*     */   
/*     */   public FramedSnappyCompressorOutputStream(OutputStream out) throws IOException {
/*  62 */     this(out, SnappyCompressorOutputStream.createParameterBuilder(32768)
/*  63 */         .build());
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
/*     */   public FramedSnappyCompressorOutputStream(OutputStream out, Parameters params) throws IOException {
/*  75 */     this.out = out;
/*  76 */     this.params = params;
/*  77 */     this.consumer = (ByteUtils.ByteConsumer)new ByteUtils.OutputStreamByteConsumer(out);
/*  78 */     out.write(FramedSnappyCompressorInputStream.SZ_SIGNATURE);
/*     */   }
/*     */ 
/*     */   
/*     */   public void write(int b) throws IOException {
/*  83 */     this.oneByte[0] = (byte)(b & 0xFF);
/*  84 */     write(this.oneByte);
/*     */   }
/*     */ 
/*     */   
/*     */   public void write(byte[] data, int off, int len) throws IOException {
/*  89 */     if (this.currentIndex + len > 65536) {
/*  90 */       flushBuffer();
/*  91 */       while (len > 65536) {
/*  92 */         System.arraycopy(data, off, this.buffer, 0, 65536);
/*  93 */         off += 65536;
/*  94 */         len -= 65536;
/*  95 */         this.currentIndex = 65536;
/*  96 */         flushBuffer();
/*     */       } 
/*     */     } 
/*  99 */     System.arraycopy(data, off, this.buffer, this.currentIndex, len);
/* 100 */     this.currentIndex += len;
/*     */   }
/*     */ 
/*     */   
/*     */   public void close() throws IOException {
/*     */     try {
/* 106 */       finish();
/*     */     } finally {
/* 108 */       this.out.close();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void finish() throws IOException {
/* 118 */     if (this.currentIndex > 0) {
/* 119 */       flushBuffer();
/*     */     }
/*     */   }
/*     */   
/*     */   private void flushBuffer() throws IOException {
/* 124 */     this.out.write(0);
/* 125 */     ByteArrayOutputStream baos = new ByteArrayOutputStream();
/* 126 */     try (SnappyCompressorOutputStream null = new SnappyCompressorOutputStream(baos, this.currentIndex, this.params)) {
/* 127 */       snappyCompressorOutputStream.write(this.buffer, 0, this.currentIndex);
/*     */     } 
/* 129 */     byte[] b = baos.toByteArray();
/* 130 */     writeLittleEndian(3, b.length + 4L);
/* 131 */     writeCrc();
/* 132 */     this.out.write(b);
/* 133 */     this.currentIndex = 0;
/*     */   }
/*     */   
/*     */   private void writeLittleEndian(int numBytes, long num) throws IOException {
/* 137 */     ByteUtils.toLittleEndian(this.consumer, num, numBytes);
/*     */   }
/*     */   
/*     */   private void writeCrc() throws IOException {
/* 141 */     this.checksum.update(this.buffer, 0, this.currentIndex);
/* 142 */     writeLittleEndian(4, mask(this.checksum.getValue()));
/* 143 */     this.checksum.reset();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   static long mask(long x) {
/* 149 */     x = x >> 15L | x << 17L;
/* 150 */     x += 2726488792L;
/* 151 */     x &= 0xFFFFFFFFL;
/* 152 */     return x;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\compressors\snappy\FramedSnappyCompressorOutputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */