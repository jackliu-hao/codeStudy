/*     */ package org.apache.commons.compress.utils;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.util.zip.Checksum;
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
/*     */ public class ChecksumVerifyingInputStream
/*     */   extends InputStream
/*     */ {
/*     */   private final InputStream in;
/*     */   private long bytesRemaining;
/*     */   private final long expectedChecksum;
/*     */   private final Checksum checksum;
/*     */   
/*     */   public ChecksumVerifyingInputStream(Checksum checksum, InputStream in, long size, long expectedChecksum) {
/*  38 */     this.checksum = checksum;
/*  39 */     this.in = in;
/*  40 */     this.expectedChecksum = expectedChecksum;
/*  41 */     this.bytesRemaining = size;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int read() throws IOException {
/*  52 */     if (this.bytesRemaining <= 0L) {
/*  53 */       return -1;
/*     */     }
/*  55 */     int ret = this.in.read();
/*  56 */     if (ret >= 0) {
/*  57 */       this.checksum.update(ret);
/*  58 */       this.bytesRemaining--;
/*     */     } 
/*  60 */     if (this.bytesRemaining == 0L && this.expectedChecksum != this.checksum.getValue()) {
/*  61 */       throw new IOException("Checksum verification failed");
/*     */     }
/*  63 */     return ret;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int read(byte[] b) throws IOException {
/*  74 */     return read(b, 0, b.length);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int read(byte[] b, int off, int len) throws IOException {
/*  85 */     if (len == 0) {
/*  86 */       return 0;
/*     */     }
/*  88 */     int ret = this.in.read(b, off, len);
/*  89 */     if (ret >= 0) {
/*  90 */       this.checksum.update(b, off, ret);
/*  91 */       this.bytesRemaining -= ret;
/*     */     } 
/*  93 */     if (this.bytesRemaining <= 0L && this.expectedChecksum != this.checksum.getValue()) {
/*  94 */       throw new IOException("Checksum verification failed");
/*     */     }
/*  96 */     return ret;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public long skip(long n) throws IOException {
/* 102 */     if (read() >= 0) {
/* 103 */       return 1L;
/*     */     }
/* 105 */     return 0L;
/*     */   }
/*     */ 
/*     */   
/*     */   public void close() throws IOException {
/* 110 */     this.in.close();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long getBytesRemaining() {
/* 118 */     return this.bytesRemaining;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compres\\utils\ChecksumVerifyingInputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */