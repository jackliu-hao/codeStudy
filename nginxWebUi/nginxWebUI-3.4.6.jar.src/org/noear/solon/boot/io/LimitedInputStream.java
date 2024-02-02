/*    */ package org.noear.solon.boot.io;
/*    */ 
/*    */ import java.io.Closeable;
/*    */ import java.io.FilterInputStream;
/*    */ import java.io.IOException;
/*    */ import java.io.InputStream;
/*    */ 
/*    */ 
/*    */ 
/*    */ public class LimitedInputStream
/*    */   extends FilterInputStream
/*    */   implements Closeable
/*    */ {
/*    */   private final long sizeMax;
/*    */   private long count;
/*    */   
/*    */   public LimitedInputStream(InputStream inputStream, long limitSize) {
/* 18 */     super(inputStream);
/* 19 */     this.sizeMax = limitSize;
/*    */   }
/*    */   
/*    */   protected void raiseError(long pSizeMax, long pCount) throws IOException {
/* 23 */     throw new LimitedInputException("The input stream is too large: " + pSizeMax);
/*    */   }
/*    */ 
/*    */   
/*    */   private void checkLimit() throws IOException {
/* 28 */     if (this.sizeMax > 0L && this.count > this.sizeMax) {
/* 29 */       raiseError(this.sizeMax, this.count);
/*    */     }
/*    */   }
/*    */ 
/*    */   
/*    */   public int read() throws IOException {
/* 35 */     int res = super.read();
/* 36 */     if (res != -1) {
/* 37 */       this.count++;
/* 38 */       checkLimit();
/*    */     } 
/* 40 */     return res;
/*    */   }
/*    */ 
/*    */   
/*    */   public int read(byte[] b, int off, int len) throws IOException {
/* 45 */     int res = super.read(b, off, len);
/* 46 */     if (res > 0) {
/* 47 */       this.count += res;
/* 48 */       checkLimit();
/*    */     } 
/* 50 */     return res;
/*    */   }
/*    */ 
/*    */   
/*    */   public void close() throws IOException {
/* 55 */     super.close();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\boot\io\LimitedInputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */