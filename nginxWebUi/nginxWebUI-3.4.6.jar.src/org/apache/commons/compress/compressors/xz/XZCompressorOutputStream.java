/*    */ package org.apache.commons.compress.compressors.xz;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.io.OutputStream;
/*    */ import org.apache.commons.compress.compressors.CompressorOutputStream;
/*    */ import org.tukaani.xz.FilterOptions;
/*    */ import org.tukaani.xz.LZMA2Options;
/*    */ import org.tukaani.xz.XZOutputStream;
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
/*    */ public class XZCompressorOutputStream
/*    */   extends CompressorOutputStream
/*    */ {
/*    */   private final XZOutputStream out;
/*    */   
/*    */   public XZCompressorOutputStream(OutputStream outputStream) throws IOException {
/* 43 */     this.out = new XZOutputStream(outputStream, (FilterOptions)new LZMA2Options());
/*    */   }
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
/*    */   public XZCompressorOutputStream(OutputStream outputStream, int preset) throws IOException {
/* 64 */     this.out = new XZOutputStream(outputStream, (FilterOptions)new LZMA2Options(preset));
/*    */   }
/*    */ 
/*    */   
/*    */   public void write(int b) throws IOException {
/* 69 */     this.out.write(b);
/*    */   }
/*    */ 
/*    */   
/*    */   public void write(byte[] buf, int off, int len) throws IOException {
/* 74 */     this.out.write(buf, off, len);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void flush() throws IOException {
/* 85 */     this.out.flush();
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void finish() throws IOException {
/* 94 */     this.out.finish();
/*    */   }
/*    */ 
/*    */   
/*    */   public void close() throws IOException {
/* 99 */     this.out.close();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\compressors\xz\XZCompressorOutputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */