/*     */ package org.apache.commons.compress.utils;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.util.Objects;
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
/*     */ public class ChecksumCalculatingInputStream
/*     */   extends InputStream
/*     */ {
/*     */   private final InputStream in;
/*     */   private final Checksum checksum;
/*     */   
/*     */   public ChecksumCalculatingInputStream(Checksum checksum, InputStream inputStream) {
/*  36 */     Objects.requireNonNull(checksum, "checksum");
/*  37 */     Objects.requireNonNull(inputStream, "in");
/*     */     
/*  39 */     this.checksum = checksum;
/*  40 */     this.in = inputStream;
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
/*  51 */     int ret = this.in.read();
/*  52 */     if (ret >= 0) {
/*  53 */       this.checksum.update(ret);
/*     */     }
/*  55 */     return ret;
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
/*  66 */     return read(b, 0, b.length);
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
/*  77 */     if (len == 0) {
/*  78 */       return 0;
/*     */     }
/*  80 */     int ret = this.in.read(b, off, len);
/*  81 */     if (ret >= 0) {
/*  82 */       this.checksum.update(b, off, ret);
/*     */     }
/*  84 */     return ret;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public long skip(long n) throws IOException {
/*  90 */     if (read() >= 0) {
/*  91 */       return 1L;
/*     */     }
/*  93 */     return 0L;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long getValue() {
/* 101 */     return this.checksum.getValue();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compres\\utils\ChecksumCalculatingInputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */