/*    */ package org.wildfly.common.iteration;
/*    */ 
/*    */ import java.util.NoSuchElementException;
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
/*    */ final class Latin1EncodingByteIterator
/*    */   extends ByteIterator
/*    */ {
/*    */   private final CodePointIterator iter;
/*    */   
/*    */   Latin1EncodingByteIterator(CodePointIterator iter) {
/* 29 */     this.iter = iter;
/*    */   }
/*    */   
/*    */   public boolean hasNext() {
/* 33 */     return this.iter.hasNext();
/*    */   }
/*    */   
/*    */   public boolean hasPrevious() {
/* 37 */     return this.iter.hasPrevious();
/*    */   }
/*    */   
/*    */   public int next() throws NoSuchElementException {
/* 41 */     int v = this.iter.next();
/* 42 */     return (v > 255) ? 63 : v;
/*    */   }
/*    */   
/*    */   public int peekNext() throws NoSuchElementException {
/* 46 */     int v = this.iter.peekNext();
/* 47 */     return (v > 255) ? 63 : v;
/*    */   }
/*    */   
/*    */   public int previous() throws NoSuchElementException {
/* 51 */     int v = this.iter.previous();
/* 52 */     return (v > 255) ? 63 : v;
/*    */   }
/*    */   
/*    */   public int peekPrevious() throws NoSuchElementException {
/* 56 */     int v = this.iter.peekPrevious();
/* 57 */     return (v > 255) ? 63 : v;
/*    */   }
/*    */   
/*    */   public long getIndex() {
/* 61 */     return this.iter.getIndex();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\wildfly\common\iteration\Latin1EncodingByteIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */