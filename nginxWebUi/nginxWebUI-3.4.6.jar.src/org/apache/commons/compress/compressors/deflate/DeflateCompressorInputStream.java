/*     */ package org.apache.commons.compress.compressors.deflate;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.util.zip.Inflater;
/*     */ import java.util.zip.InflaterInputStream;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class DeflateCompressorInputStream
/*     */   extends CompressorInputStream
/*     */   implements InputStreamStatistics
/*     */ {
/*     */   private static final int MAGIC_1 = 120;
/*     */   private static final int MAGIC_2a = 1;
/*     */   private static final int MAGIC_2b = 94;
/*     */   private static final int MAGIC_2c = 156;
/*     */   private static final int MAGIC_2d = 218;
/*     */   private final CountingInputStream countingStream;
/*     */   private final InputStream in;
/*     */   private final Inflater inflater;
/*     */   
/*     */   public DeflateCompressorInputStream(InputStream inputStream) {
/*  56 */     this(inputStream, new DeflateParameters());
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
/*     */   public DeflateCompressorInputStream(InputStream inputStream, DeflateParameters parameters) {
/*  68 */     this.inflater = new Inflater(!parameters.withZlibHeader());
/*  69 */     this.in = new InflaterInputStream((InputStream)(this.countingStream = new CountingInputStream(inputStream)), this.inflater);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public int read() throws IOException {
/*  75 */     int ret = this.in.read();
/*  76 */     count((ret == -1) ? 0 : 1);
/*  77 */     return ret;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public int read(byte[] buf, int off, int len) throws IOException {
/*  83 */     if (len == 0) {
/*  84 */       return 0;
/*     */     }
/*  86 */     int ret = this.in.read(buf, off, len);
/*  87 */     count(ret);
/*  88 */     return ret;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public long skip(long n) throws IOException {
/*  94 */     return IOUtils.skip(this.in, n);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public int available() throws IOException {
/* 100 */     return this.in.available();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void close() throws IOException {
/*     */     try {
/* 107 */       this.in.close();
/*     */     } finally {
/* 109 */       this.inflater.end();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long getCompressedCount() {
/* 118 */     return this.countingStream.getBytesRead();
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
/*     */   public static boolean matches(byte[] signature, int length) {
/* 135 */     return (length > 3 && signature[0] == 120 && (signature[1] == 1 || signature[1] == 94 || signature[1] == -100 || signature[1] == -38));
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\compressors\deflate\DeflateCompressorInputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */