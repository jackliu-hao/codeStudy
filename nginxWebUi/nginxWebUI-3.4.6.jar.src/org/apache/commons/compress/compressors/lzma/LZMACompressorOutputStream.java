/*    */ package org.apache.commons.compress.compressors.lzma;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.io.OutputStream;
/*    */ import org.apache.commons.compress.compressors.CompressorOutputStream;
/*    */ import org.tukaani.xz.LZMA2Options;
/*    */ import org.tukaani.xz.LZMAOutputStream;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class LZMACompressorOutputStream
/*    */   extends CompressorOutputStream
/*    */ {
/*    */   private final LZMAOutputStream out;
/*    */   
/*    */   public LZMACompressorOutputStream(OutputStream outputStream) throws IOException {
/* 43 */     this.out = new LZMAOutputStream(outputStream, new LZMA2Options(), -1L);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public void write(int b) throws IOException {
/* 49 */     this.out.write(b);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public void write(byte[] buf, int off, int len) throws IOException {
/* 55 */     this.out.write(buf, off, len);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void flush() throws IOException {}
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void finish() throws IOException {
/* 71 */     this.out.finish();
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public void close() throws IOException {
/* 77 */     this.out.close();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\compressors\lzma\LZMACompressorOutputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */