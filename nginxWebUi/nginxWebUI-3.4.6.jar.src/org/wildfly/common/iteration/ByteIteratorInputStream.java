/*    */ package org.wildfly.common.iteration;
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
/*    */ final class ByteIteratorInputStream
/*    */   extends InputStream
/*    */ {
/*    */   private final ByteIterator iter;
/*    */   
/*    */   ByteIteratorInputStream(ByteIterator iter) {
/* 30 */     this.iter = iter;
/*    */   }
/*    */   
/*    */   public int read() throws IOException {
/* 34 */     return this.iter.hasNext() ? this.iter.next() : -1;
/*    */   }
/*    */   
/*    */   public int read(byte[] b, int off, int len) throws IOException {
/* 38 */     if (len == 0) return 0; 
/* 39 */     if (!this.iter.hasNext()) return -1; 
/* 40 */     return this.iter.drain(b, off, len);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\wildfly\common\iteration\ByteIteratorInputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */