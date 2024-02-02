/*     */ package org.apache.commons.compress.compressors.brotli;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import org.apache.commons.compress.compressors.CompressorInputStream;
/*     */ import org.apache.commons.compress.utils.CountingInputStream;
/*     */ import org.apache.commons.compress.utils.IOUtils;
/*     */ import org.apache.commons.compress.utils.InputStreamStatistics;
/*     */ import org.brotli.dec.BrotliInputStream;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class BrotliCompressorInputStream
/*     */   extends CompressorInputStream
/*     */   implements InputStreamStatistics
/*     */ {
/*     */   private final CountingInputStream countingStream;
/*     */   private final BrotliInputStream decIS;
/*     */   
/*     */   public BrotliCompressorInputStream(InputStream in) throws IOException {
/*  42 */     this.decIS = new BrotliInputStream((InputStream)(this.countingStream = new CountingInputStream(in)));
/*     */   }
/*     */ 
/*     */   
/*     */   public int available() throws IOException {
/*  47 */     return this.decIS.available();
/*     */   }
/*     */ 
/*     */   
/*     */   public void close() throws IOException {
/*  52 */     this.decIS.close();
/*     */   }
/*     */ 
/*     */   
/*     */   public int read(byte[] b) throws IOException {
/*  57 */     return this.decIS.read(b);
/*     */   }
/*     */ 
/*     */   
/*     */   public long skip(long n) throws IOException {
/*  62 */     return IOUtils.skip((InputStream)this.decIS, n);
/*     */   }
/*     */ 
/*     */   
/*     */   public synchronized void mark(int readlimit) {
/*  67 */     this.decIS.mark(readlimit);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean markSupported() {
/*  72 */     return this.decIS.markSupported();
/*     */   }
/*     */ 
/*     */   
/*     */   public int read() throws IOException {
/*  77 */     int ret = this.decIS.read();
/*  78 */     count((ret == -1) ? 0 : 1);
/*  79 */     return ret;
/*     */   }
/*     */ 
/*     */   
/*     */   public int read(byte[] buf, int off, int len) throws IOException {
/*  84 */     int ret = this.decIS.read(buf, off, len);
/*  85 */     count(ret);
/*  86 */     return ret;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/*  91 */     return this.decIS.toString();
/*     */   }
/*     */ 
/*     */   
/*     */   public synchronized void reset() throws IOException {
/*  96 */     this.decIS.reset();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long getCompressedCount() {
/* 104 */     return this.countingStream.getBytesRead();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\compressors\brotli\BrotliCompressorInputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */