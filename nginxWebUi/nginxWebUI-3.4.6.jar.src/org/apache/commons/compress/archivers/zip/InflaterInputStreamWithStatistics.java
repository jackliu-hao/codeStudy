/*    */ package org.apache.commons.compress.archivers.zip;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.io.InputStream;
/*    */ import java.util.zip.Inflater;
/*    */ import java.util.zip.InflaterInputStream;
/*    */ import org.apache.commons.compress.utils.InputStreamStatistics;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ class InflaterInputStreamWithStatistics
/*    */   extends InflaterInputStream
/*    */   implements InputStreamStatistics
/*    */ {
/*    */   private long compressedCount;
/*    */   private long uncompressedCount;
/*    */   
/*    */   public InflaterInputStreamWithStatistics(InputStream in) {
/* 39 */     super(in);
/*    */   }
/*    */   
/*    */   public InflaterInputStreamWithStatistics(InputStream in, Inflater inf) {
/* 43 */     super(in, inf);
/*    */   }
/*    */   
/*    */   public InflaterInputStreamWithStatistics(InputStream in, Inflater inf, int size) {
/* 47 */     super(in, inf, size);
/*    */   }
/*    */ 
/*    */   
/*    */   protected void fill() throws IOException {
/* 52 */     super.fill();
/* 53 */     this.compressedCount += this.inf.getRemaining();
/*    */   }
/*    */ 
/*    */   
/*    */   public int read() throws IOException {
/* 58 */     int b = super.read();
/* 59 */     if (b > -1) {
/* 60 */       this.uncompressedCount++;
/*    */     }
/* 62 */     return b;
/*    */   }
/*    */ 
/*    */   
/*    */   public int read(byte[] b, int off, int len) throws IOException {
/* 67 */     int bytes = super.read(b, off, len);
/* 68 */     if (bytes > -1) {
/* 69 */       this.uncompressedCount += bytes;
/*    */     }
/* 71 */     return bytes;
/*    */   }
/*    */ 
/*    */   
/*    */   public long getCompressedCount() {
/* 76 */     return this.compressedCount;
/*    */   }
/*    */ 
/*    */   
/*    */   public long getUncompressedCount() {
/* 81 */     return this.uncompressedCount;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\archivers\zip\InflaterInputStreamWithStatistics.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */