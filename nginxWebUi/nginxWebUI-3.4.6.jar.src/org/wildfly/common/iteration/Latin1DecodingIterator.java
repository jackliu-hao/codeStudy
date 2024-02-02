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
/*    */ final class Latin1DecodingIterator
/*    */   extends CodePointIterator
/*    */ {
/*    */   private final ByteIterator iter;
/*    */   private final long start;
/*    */   
/*    */   Latin1DecodingIterator(ByteIterator iter, long start) {
/* 30 */     this.iter = iter;
/* 31 */     this.start = start;
/*    */   }
/*    */   
/*    */   public boolean hasNext() {
/* 35 */     return this.iter.hasNext();
/*    */   }
/*    */   
/*    */   public boolean hasPrevious() {
/* 39 */     return (this.start > 0L && this.iter.hasPrevious());
/*    */   }
/*    */   
/*    */   public int next() {
/* 43 */     return this.iter.next();
/*    */   }
/*    */   
/*    */   public int peekNext() throws NoSuchElementException {
/* 47 */     return this.iter.peekNext();
/*    */   }
/*    */   
/*    */   public int previous() {
/* 51 */     if (this.start == 0L) throw new NoSuchElementException(); 
/* 52 */     return this.iter.previous();
/*    */   }
/*    */   
/*    */   public int peekPrevious() throws NoSuchElementException {
/* 56 */     return this.iter.peekPrevious();
/*    */   }
/*    */   
/*    */   public long getIndex() {
/* 60 */     return this.iter.getIndex() - this.start;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\wildfly\common\iteration\Latin1DecodingIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */