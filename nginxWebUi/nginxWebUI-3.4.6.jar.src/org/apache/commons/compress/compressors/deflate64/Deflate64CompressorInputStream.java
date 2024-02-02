/*     */ package org.apache.commons.compress.compressors.deflate64;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import org.apache.commons.compress.compressors.CompressorInputStream;
/*     */ import org.apache.commons.compress.utils.IOUtils;
/*     */ import org.apache.commons.compress.utils.InputStreamStatistics;
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
/*     */ public class Deflate64CompressorInputStream
/*     */   extends CompressorInputStream
/*     */   implements InputStreamStatistics
/*     */ {
/*     */   private InputStream originalStream;
/*     */   private HuffmanDecoder decoder;
/*     */   private long compressedBytesRead;
/*  38 */   private final byte[] oneByte = new byte[1];
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Deflate64CompressorInputStream(InputStream in) {
/*  46 */     this(new HuffmanDecoder(in));
/*  47 */     this.originalStream = in;
/*     */   }
/*     */   
/*     */   Deflate64CompressorInputStream(HuffmanDecoder decoder) {
/*  51 */     this.decoder = decoder;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int read() throws IOException {
/*     */     int r;
/*     */     while (true) {
/*  60 */       r = read(this.oneByte);
/*  61 */       switch (r) {
/*     */         case 1:
/*  63 */           return this.oneByte[0] & 0xFF;
/*     */         case -1:
/*  65 */           return -1;
/*     */         case 0:
/*     */           continue;
/*     */       }  break;
/*  69 */     }  throw new IllegalStateException("Invalid return value from read: " + r);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int read(byte[] b, int off, int len) throws IOException {
/*  79 */     if (len == 0) {
/*  80 */       return 0;
/*     */     }
/*  82 */     int read = -1;
/*  83 */     if (this.decoder != null) {
/*     */       try {
/*  85 */         read = this.decoder.decode(b, off, len);
/*  86 */       } catch (RuntimeException ex) {
/*  87 */         throw new IOException("Invalid Deflate64 input", ex);
/*     */       } 
/*  89 */       this.compressedBytesRead = this.decoder.getBytesRead();
/*  90 */       count(read);
/*  91 */       if (read == -1) {
/*  92 */         closeDecoder();
/*     */       }
/*     */     } 
/*  95 */     return read;
/*     */   }
/*     */ 
/*     */   
/*     */   public int available() throws IOException {
/* 100 */     return (this.decoder != null) ? this.decoder.available() : 0;
/*     */   }
/*     */ 
/*     */   
/*     */   public void close() throws IOException {
/*     */     try {
/* 106 */       closeDecoder();
/*     */     } finally {
/* 108 */       if (this.originalStream != null) {
/* 109 */         this.originalStream.close();
/* 110 */         this.originalStream = null;
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long getCompressedCount() {
/* 120 */     return this.compressedBytesRead;
/*     */   }
/*     */   
/*     */   private void closeDecoder() {
/* 124 */     IOUtils.closeQuietly(this.decoder);
/* 125 */     this.decoder = null;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\compressors\deflate64\Deflate64CompressorInputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */