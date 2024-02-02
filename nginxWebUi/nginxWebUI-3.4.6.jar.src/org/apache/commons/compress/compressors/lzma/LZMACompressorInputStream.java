/*     */ package org.apache.commons.compress.compressors.lzma;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import org.apache.commons.compress.MemoryLimitException;
/*     */ import org.apache.commons.compress.compressors.CompressorInputStream;
/*     */ import org.apache.commons.compress.utils.CountingInputStream;
/*     */ import org.apache.commons.compress.utils.IOUtils;
/*     */ import org.apache.commons.compress.utils.InputStreamStatistics;
/*     */ import org.tukaani.xz.LZMAInputStream;
/*     */ import org.tukaani.xz.MemoryLimitException;
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
/*     */ public class LZMACompressorInputStream
/*     */   extends CompressorInputStream
/*     */   implements InputStreamStatistics
/*     */ {
/*     */   private final CountingInputStream countingStream;
/*     */   private final InputStream in;
/*     */   
/*     */   public LZMACompressorInputStream(InputStream inputStream) throws IOException {
/*  56 */     this.in = (InputStream)new LZMAInputStream((InputStream)(this.countingStream = new CountingInputStream(inputStream)), -1);
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
/*     */   
/*     */   public LZMACompressorInputStream(InputStream inputStream, int memoryLimitInKb) throws IOException {
/*     */     try {
/*  79 */       this.in = (InputStream)new LZMAInputStream((InputStream)(this.countingStream = new CountingInputStream(inputStream)), memoryLimitInKb);
/*  80 */     } catch (MemoryLimitException e) {
/*     */       
/*  82 */       throw new MemoryLimitException(e.getMemoryNeeded(), e.getMemoryLimit(), e);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public int read() throws IOException {
/*  89 */     int ret = this.in.read();
/*  90 */     count((ret == -1) ? 0 : 1);
/*  91 */     return ret;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public int read(byte[] buf, int off, int len) throws IOException {
/*  97 */     int ret = this.in.read(buf, off, len);
/*  98 */     count(ret);
/*  99 */     return ret;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public long skip(long n) throws IOException {
/* 105 */     return IOUtils.skip(this.in, n);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public int available() throws IOException {
/* 111 */     return this.in.available();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void close() throws IOException {
/* 117 */     this.in.close();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long getCompressedCount() {
/* 125 */     return this.countingStream.getBytesRead();
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
/*     */   public static boolean matches(byte[] signature, int length) {
/* 140 */     return (signature != null && length >= 3 && signature[0] == 93 && signature[1] == 0 && signature[2] == 0);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\compressors\lzma\LZMACompressorInputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */