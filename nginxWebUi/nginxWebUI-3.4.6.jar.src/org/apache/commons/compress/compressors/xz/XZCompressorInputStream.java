/*     */ package org.apache.commons.compress.compressors.xz;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import org.apache.commons.compress.MemoryLimitException;
/*     */ import org.apache.commons.compress.compressors.CompressorInputStream;
/*     */ import org.apache.commons.compress.utils.CountingInputStream;
/*     */ import org.apache.commons.compress.utils.IOUtils;
/*     */ import org.apache.commons.compress.utils.InputStreamStatistics;
/*     */ import org.tukaani.xz.MemoryLimitException;
/*     */ import org.tukaani.xz.SingleXZInputStream;
/*     */ import org.tukaani.xz.XZ;
/*     */ import org.tukaani.xz.XZInputStream;
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
/*     */ public class XZCompressorInputStream
/*     */   extends CompressorInputStream
/*     */   implements InputStreamStatistics
/*     */ {
/*     */   private final CountingInputStream countingStream;
/*     */   private final InputStream in;
/*     */   
/*     */   public static boolean matches(byte[] signature, int length) {
/*  52 */     if (length < XZ.HEADER_MAGIC.length) {
/*  53 */       return false;
/*     */     }
/*     */     
/*  56 */     for (int i = 0; i < XZ.HEADER_MAGIC.length; i++) {
/*  57 */       if (signature[i] != XZ.HEADER_MAGIC[i]) {
/*  58 */         return false;
/*     */       }
/*     */     } 
/*     */     
/*  62 */     return true;
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
/*     */   public XZCompressorInputStream(InputStream inputStream) throws IOException {
/*  80 */     this(inputStream, false);
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
/*     */   
/*     */   public XZCompressorInputStream(InputStream inputStream, boolean decompressConcatenated) throws IOException {
/* 103 */     this(inputStream, decompressConcatenated, -1);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public XZCompressorInputStream(InputStream inputStream, boolean decompressConcatenated, int memoryLimitInKb) throws IOException {
/* 131 */     this.countingStream = new CountingInputStream(inputStream);
/* 132 */     if (decompressConcatenated) {
/* 133 */       this.in = (InputStream)new XZInputStream((InputStream)this.countingStream, memoryLimitInKb);
/*     */     } else {
/* 135 */       this.in = (InputStream)new SingleXZInputStream((InputStream)this.countingStream, memoryLimitInKb);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public int read() throws IOException {
/*     */     try {
/* 142 */       int ret = this.in.read();
/* 143 */       count((ret == -1) ? -1 : 1);
/* 144 */       return ret;
/* 145 */     } catch (MemoryLimitException e) {
/* 146 */       throw new MemoryLimitException(e.getMemoryNeeded(), e.getMemoryLimit(), e);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public int read(byte[] buf, int off, int len) throws IOException {
/* 152 */     if (len == 0) {
/* 153 */       return 0;
/*     */     }
/*     */     try {
/* 156 */       int ret = this.in.read(buf, off, len);
/* 157 */       count(ret);
/* 158 */       return ret;
/* 159 */     } catch (MemoryLimitException e) {
/*     */       
/* 161 */       throw new MemoryLimitException(e.getMemoryNeeded(), e.getMemoryLimit(), e);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public long skip(long n) throws IOException {
/*     */     try {
/* 168 */       return IOUtils.skip(this.in, n);
/* 169 */     } catch (MemoryLimitException e) {
/*     */       
/* 171 */       throw new MemoryLimitException(e.getMemoryNeeded(), e.getMemoryLimit(), e);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public int available() throws IOException {
/* 177 */     return this.in.available();
/*     */   }
/*     */ 
/*     */   
/*     */   public void close() throws IOException {
/* 182 */     this.in.close();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long getCompressedCount() {
/* 190 */     return this.countingStream.getBytesRead();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\compressors\xz\XZCompressorInputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */