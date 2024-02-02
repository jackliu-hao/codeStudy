/*    */ package org.apache.commons.compress.compressors.deflate;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.io.OutputStream;
/*    */ import java.util.zip.Deflater;
/*    */ import java.util.zip.DeflaterOutputStream;
/*    */ import org.apache.commons.compress.compressors.CompressorOutputStream;
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
/*    */ public class DeflateCompressorOutputStream
/*    */   extends CompressorOutputStream
/*    */ {
/*    */   private final DeflaterOutputStream out;
/*    */   private final Deflater deflater;
/*    */   
/*    */   public DeflateCompressorOutputStream(OutputStream outputStream) throws IOException {
/* 42 */     this(outputStream, new DeflateParameters());
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public DeflateCompressorOutputStream(OutputStream outputStream, DeflateParameters parameters) throws IOException {
/* 53 */     this.deflater = new Deflater(parameters.getCompressionLevel(), !parameters.withZlibHeader());
/* 54 */     this.out = new DeflaterOutputStream(outputStream, this.deflater);
/*    */   }
/*    */ 
/*    */   
/*    */   public void write(int b) throws IOException {
/* 59 */     this.out.write(b);
/*    */   }
/*    */ 
/*    */   
/*    */   public void write(byte[] buf, int off, int len) throws IOException {
/* 64 */     this.out.write(buf, off, len);
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
/* 75 */     this.out.flush();
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void finish() throws IOException {
/* 84 */     this.out.finish();
/*    */   }
/*    */ 
/*    */   
/*    */   public void close() throws IOException {
/*    */     try {
/* 90 */       this.out.close();
/*    */     } finally {
/* 92 */       this.deflater.end();
/*    */     } 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\compressors\deflate\DeflateCompressorOutputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */