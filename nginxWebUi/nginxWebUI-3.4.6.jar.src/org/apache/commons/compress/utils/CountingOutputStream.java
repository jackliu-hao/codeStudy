/*    */ package org.apache.commons.compress.utils;
/*    */ 
/*    */ import java.io.FilterOutputStream;
/*    */ import java.io.IOException;
/*    */ import java.io.OutputStream;
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
/*    */ public class CountingOutputStream
/*    */   extends FilterOutputStream
/*    */ {
/*    */   private long bytesWritten;
/*    */   
/*    */   public CountingOutputStream(OutputStream out) {
/* 34 */     super(out);
/*    */   }
/*    */ 
/*    */   
/*    */   public void write(int b) throws IOException {
/* 39 */     this.out.write(b);
/* 40 */     count(1L);
/*    */   }
/*    */   
/*    */   public void write(byte[] b) throws IOException {
/* 44 */     write(b, 0, b.length);
/*    */   }
/*    */   
/*    */   public void write(byte[] b, int off, int len) throws IOException {
/* 48 */     this.out.write(b, off, len);
/* 49 */     count(len);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   protected void count(long written) {
/* 59 */     if (written != -1L) {
/* 60 */       this.bytesWritten += written;
/*    */     }
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public long getBytesWritten() {
/* 69 */     return this.bytesWritten;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compres\\utils\CountingOutputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */