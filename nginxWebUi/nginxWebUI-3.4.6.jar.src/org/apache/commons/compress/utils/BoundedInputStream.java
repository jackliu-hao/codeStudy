/*    */ package org.apache.commons.compress.utils;
/*    */ 
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class BoundedInputStream
/*    */   extends InputStream
/*    */ {
/*    */   private final InputStream in;
/*    */   private long bytesRemaining;
/*    */   
/*    */   public BoundedInputStream(InputStream in, long size) {
/* 39 */     this.in = in;
/* 40 */     this.bytesRemaining = size;
/*    */   }
/*    */ 
/*    */   
/*    */   public int read() throws IOException {
/* 45 */     if (this.bytesRemaining > 0L) {
/* 46 */       this.bytesRemaining--;
/* 47 */       return this.in.read();
/*    */     } 
/* 49 */     return -1;
/*    */   }
/*    */ 
/*    */   
/*    */   public int read(byte[] b, int off, int len) throws IOException {
/* 54 */     if (len == 0) {
/* 55 */       return 0;
/*    */     }
/* 57 */     if (this.bytesRemaining == 0L) {
/* 58 */       return -1;
/*    */     }
/* 60 */     int bytesToRead = len;
/* 61 */     if (bytesToRead > this.bytesRemaining) {
/* 62 */       bytesToRead = (int)this.bytesRemaining;
/*    */     }
/* 64 */     int bytesRead = this.in.read(b, off, bytesToRead);
/* 65 */     if (bytesRead >= 0) {
/* 66 */       this.bytesRemaining -= bytesRead;
/*    */     }
/* 68 */     return bytesRead;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void close() {}
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public long skip(long n) throws IOException {
/* 82 */     long bytesToSkip = Math.min(this.bytesRemaining, n);
/* 83 */     long bytesSkipped = this.in.skip(bytesToSkip);
/* 84 */     this.bytesRemaining -= bytesSkipped;
/*    */     
/* 86 */     return bytesSkipped;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public long getBytesRemaining() {
/* 94 */     return this.bytesRemaining;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compres\\utils\BoundedInputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */