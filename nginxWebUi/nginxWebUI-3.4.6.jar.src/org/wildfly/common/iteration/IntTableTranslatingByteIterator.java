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
/*    */ final class IntTableTranslatingByteIterator
/*    */   extends ByteIterator
/*    */ {
/*    */   private final ByteIterator iter;
/*    */   private final int[] table;
/*    */   
/*    */   IntTableTranslatingByteIterator(ByteIterator iter, int[] table) {
/* 30 */     this.iter = iter;
/* 31 */     this.table = table;
/*    */   }
/*    */   
/*    */   public boolean hasNext() {
/* 35 */     return this.iter.hasNext();
/*    */   }
/*    */   
/*    */   public boolean hasPrevious() {
/* 39 */     return this.iter.hasPrevious();
/*    */   }
/*    */   
/*    */   public int next() throws NoSuchElementException {
/* 43 */     return this.table[this.iter.next()] & 0xFF;
/*    */   }
/*    */   
/*    */   public int peekNext() throws NoSuchElementException {
/* 47 */     return this.table[this.iter.peekNext()] & 0xFF;
/*    */   }
/*    */   
/*    */   public int previous() throws NoSuchElementException {
/* 51 */     return this.table[this.iter.previous()] & 0xFF;
/*    */   }
/*    */   
/*    */   public int peekPrevious() throws NoSuchElementException {
/* 55 */     return this.table[this.iter.peekPrevious()] & 0xFF;
/*    */   }
/*    */   
/*    */   public long getIndex() {
/* 59 */     return this.iter.getIndex();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\wildfly\common\iteration\IntTableTranslatingByteIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */