/*     */ package org.apache.commons.compress.compressors.zstandard;
/*     */ 
/*     */ import com.github.luben.zstd.ZstdOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
/*     */ import org.apache.commons.compress.compressors.CompressorOutputStream;
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
/*     */ public class ZstdCompressorOutputStream
/*     */   extends CompressorOutputStream
/*     */ {
/*     */   private final ZstdOutputStream encOS;
/*     */   
/*     */   public ZstdCompressorOutputStream(OutputStream outStream, int level, boolean closeFrameOnFlush, boolean useChecksum) throws IOException {
/*  48 */     this.encOS = new ZstdOutputStream(outStream, level);
/*  49 */     this.encOS.setCloseFrameOnFlush(closeFrameOnFlush);
/*  50 */     this.encOS.setChecksum(useChecksum);
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
/*     */   public ZstdCompressorOutputStream(OutputStream outStream, int level, boolean closeFrameOnFlush) throws IOException {
/*  63 */     this.encOS = new ZstdOutputStream(outStream, level);
/*  64 */     this.encOS.setCloseFrameOnFlush(closeFrameOnFlush);
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
/*     */   public ZstdCompressorOutputStream(OutputStream outStream, int level) throws IOException {
/*  76 */     this.encOS = new ZstdOutputStream(outStream, level);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ZstdCompressorOutputStream(OutputStream outStream) throws IOException {
/*  86 */     this.encOS = new ZstdOutputStream(outStream);
/*     */   }
/*     */ 
/*     */   
/*     */   public void close() throws IOException {
/*  91 */     this.encOS.close();
/*     */   }
/*     */ 
/*     */   
/*     */   public void write(int b) throws IOException {
/*  96 */     this.encOS.write(b);
/*     */   }
/*     */ 
/*     */   
/*     */   public void write(byte[] buf, int off, int len) throws IOException {
/* 101 */     this.encOS.write(buf, off, len);
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 106 */     return this.encOS.toString();
/*     */   }
/*     */ 
/*     */   
/*     */   public void flush() throws IOException {
/* 111 */     this.encOS.flush();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\compressors\zstandard\ZstdCompressorOutputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */