/*     */ package org.apache.commons.compress.compressors.zstandard;
/*     */ 
/*     */ import com.github.luben.zstd.BufferPool;
/*     */ import com.github.luben.zstd.ZstdInputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import org.apache.commons.compress.compressors.CompressorInputStream;
/*     */ import org.apache.commons.compress.utils.CountingInputStream;
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
/*     */ 
/*     */ 
/*     */ public class ZstdCompressorInputStream
/*     */   extends CompressorInputStream
/*     */   implements InputStreamStatistics
/*     */ {
/*     */   private final CountingInputStream countingStream;
/*     */   private final ZstdInputStream decIS;
/*     */   
/*     */   public ZstdCompressorInputStream(InputStream in) throws IOException {
/*  44 */     this.decIS = new ZstdInputStream((InputStream)(this.countingStream = new CountingInputStream(in)));
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
/*     */   public ZstdCompressorInputStream(InputStream in, BufferPool bufferPool) throws IOException {
/*  60 */     this.decIS = new ZstdInputStream((InputStream)(this.countingStream = new CountingInputStream(in)), bufferPool);
/*     */   }
/*     */ 
/*     */   
/*     */   public int available() throws IOException {
/*  65 */     return this.decIS.available();
/*     */   }
/*     */ 
/*     */   
/*     */   public void close() throws IOException {
/*  70 */     this.decIS.close();
/*     */   }
/*     */ 
/*     */   
/*     */   public int read(byte[] b) throws IOException {
/*  75 */     return read(b, 0, b.length);
/*     */   }
/*     */ 
/*     */   
/*     */   public long skip(long n) throws IOException {
/*  80 */     return IOUtils.skip((InputStream)this.decIS, n);
/*     */   }
/*     */ 
/*     */   
/*     */   public synchronized void mark(int readlimit) {
/*  85 */     this.decIS.mark(readlimit);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean markSupported() {
/*  90 */     return this.decIS.markSupported();
/*     */   }
/*     */ 
/*     */   
/*     */   public int read() throws IOException {
/*  95 */     int ret = this.decIS.read();
/*  96 */     count((ret == -1) ? 0 : 1);
/*  97 */     return ret;
/*     */   }
/*     */ 
/*     */   
/*     */   public int read(byte[] buf, int off, int len) throws IOException {
/* 102 */     if (len == 0) {
/* 103 */       return 0;
/*     */     }
/* 105 */     int ret = this.decIS.read(buf, off, len);
/* 106 */     count(ret);
/* 107 */     return ret;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 112 */     return this.decIS.toString();
/*     */   }
/*     */ 
/*     */   
/*     */   public synchronized void reset() throws IOException {
/* 117 */     this.decIS.reset();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long getCompressedCount() {
/* 125 */     return this.countingStream.getBytesRead();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\compressors\zstandard\ZstdCompressorInputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */