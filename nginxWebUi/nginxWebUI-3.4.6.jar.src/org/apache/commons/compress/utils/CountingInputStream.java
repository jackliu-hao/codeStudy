/*    */ package org.apache.commons.compress.utils;
/*    */ 
/*    */ import java.io.FilterInputStream;
/*    */ import java.io.IOException;
/*    */ import java.io.InputStream;
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
/*    */ public class CountingInputStream
/*    */   extends FilterInputStream
/*    */ {
/*    */   private long bytesRead;
/*    */   
/*    */   public CountingInputStream(InputStream in) {
/* 34 */     super(in);
/*    */   }
/*    */ 
/*    */   
/*    */   public int read() throws IOException {
/* 39 */     int r = this.in.read();
/* 40 */     if (r >= 0) {
/* 41 */       count(1L);
/*    */     }
/* 43 */     return r;
/*    */   }
/*    */ 
/*    */   
/*    */   public int read(byte[] b) throws IOException {
/* 48 */     return read(b, 0, b.length);
/*    */   }
/*    */ 
/*    */   
/*    */   public int read(byte[] b, int off, int len) throws IOException {
/* 53 */     if (len == 0) {
/* 54 */       return 0;
/*    */     }
/* 56 */     int r = this.in.read(b, off, len);
/* 57 */     if (r >= 0) {
/* 58 */       count(r);
/*    */     }
/* 60 */     return r;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   protected final void count(long read) {
/* 70 */     if (read != -1L) {
/* 71 */       this.bytesRead += read;
/*    */     }
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public long getBytesRead() {
/* 80 */     return this.bytesRead;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compres\\utils\CountingInputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */