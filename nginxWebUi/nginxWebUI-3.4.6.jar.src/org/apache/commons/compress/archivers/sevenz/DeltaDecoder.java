/*    */ package org.apache.commons.compress.archivers.sevenz;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.io.InputStream;
/*    */ import java.io.OutputStream;
/*    */ import org.tukaani.xz.DeltaOptions;
/*    */ import org.tukaani.xz.FinishableOutputStream;
/*    */ import org.tukaani.xz.FinishableWrapperOutputStream;
/*    */ import org.tukaani.xz.UnsupportedOptionsException;
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
/*    */ class DeltaDecoder
/*    */   extends CoderBase
/*    */ {
/*    */   DeltaDecoder() {
/* 29 */     super(new Class[] { Number.class });
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   InputStream decode(String archiveName, InputStream in, long uncompressedLength, Coder coder, byte[] password, int maxMemoryLimitInKb) throws IOException {
/* 35 */     return (new DeltaOptions(getOptionsFromCoder(coder))).getInputStream(in);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   OutputStream encode(OutputStream out, Object options) throws IOException {
/* 41 */     int distance = numberOptionOrDefault(options, 1);
/*    */     try {
/* 43 */       return (OutputStream)(new DeltaOptions(distance)).getOutputStream((FinishableOutputStream)new FinishableWrapperOutputStream(out));
/* 44 */     } catch (UnsupportedOptionsException ex) {
/* 45 */       throw new IOException(ex.getMessage());
/*    */     } 
/*    */   }
/*    */ 
/*    */   
/*    */   byte[] getOptionsAsProperties(Object options) {
/* 51 */     return new byte[] {
/* 52 */         (byte)(numberOptionOrDefault(options, 1) - 1)
/*    */       };
/*    */   }
/*    */ 
/*    */   
/*    */   Object getOptionsFromCoder(Coder coder, InputStream in) {
/* 58 */     return Integer.valueOf(getOptionsFromCoder(coder));
/*    */   }
/*    */   
/*    */   private int getOptionsFromCoder(Coder coder) {
/* 62 */     if (coder.properties == null || coder.properties.length == 0) {
/* 63 */       return 1;
/*    */     }
/* 65 */     return (0xFF & coder.properties[0]) + 1;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\archivers\sevenz\DeltaDecoder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */