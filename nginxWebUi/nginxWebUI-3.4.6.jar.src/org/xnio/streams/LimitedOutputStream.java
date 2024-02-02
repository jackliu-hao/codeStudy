/*    */ package org.xnio.streams;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.io.InterruptedIOException;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public final class LimitedOutputStream
/*    */   extends OutputStream
/*    */ {
/*    */   private final OutputStream delegate;
/*    */   private long remaining;
/*    */   
/*    */   public LimitedOutputStream(OutputStream delegate, long size) {
/* 43 */     this.delegate = delegate;
/* 44 */     this.remaining = size;
/*    */   }
/*    */ 
/*    */   
/*    */   public void write(int b) throws IOException {
/* 49 */     long remaining = this.remaining;
/* 50 */     if (remaining < 1L) {
/* 51 */       throw notEnoughSpace();
/*    */     }
/* 53 */     this.delegate.write(b);
/* 54 */     this.remaining = remaining - 1L;
/*    */   }
/*    */ 
/*    */   
/*    */   public void write(byte[] b, int off, int len) throws IOException {
/* 59 */     long remaining = this.remaining;
/* 60 */     if (remaining < len) {
/* 61 */       throw notEnoughSpace();
/*    */     }
/*    */     try {
/* 64 */       this.delegate.write(b, off, len);
/* 65 */       this.remaining = remaining - len;
/* 66 */     } catch (InterruptedIOException e) {
/* 67 */       this.remaining = remaining - (e.bytesTransferred & 0xFFFFFFFFL);
/* 68 */       throw e;
/*    */     } 
/*    */   }
/*    */ 
/*    */   
/*    */   public void flush() throws IOException {
/* 74 */     this.delegate.flush();
/*    */   }
/*    */ 
/*    */   
/*    */   public void close() throws IOException {
/* 79 */     this.delegate.close();
/*    */   }
/*    */   
/*    */   private static IOException notEnoughSpace() {
/* 83 */     return new IOException("Not enough space in output stream");
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\streams\LimitedOutputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */