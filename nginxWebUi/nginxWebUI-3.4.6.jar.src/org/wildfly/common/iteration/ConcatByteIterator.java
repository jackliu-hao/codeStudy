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
/*    */ final class ConcatByteIterator
/*    */   extends ByteIterator
/*    */ {
/*    */   private final ByteIterator[] iterators;
/* 27 */   private long index = 0L;
/*    */   
/*    */   ConcatByteIterator(ByteIterator[] iterators) {
/* 30 */     this.iterators = iterators;
/*    */   }
/*    */   
/*    */   private int seekNext() {
/* 34 */     for (int i = 0; i < this.iterators.length; i++) {
/* 35 */       if (this.iterators[i].hasNext()) {
/* 36 */         return i;
/*    */       }
/*    */     } 
/* 39 */     return -1;
/*    */   }
/*    */   
/*    */   private int seekPrevious() {
/* 43 */     for (int i = this.iterators.length - 1; i >= 0; i--) {
/* 44 */       if (this.iterators[i].hasPrevious()) {
/* 45 */         return i;
/*    */       }
/*    */     } 
/* 48 */     return -1;
/*    */   }
/*    */   
/*    */   public boolean hasNext() {
/* 52 */     return (seekNext() != -1);
/*    */   }
/*    */   
/*    */   public boolean hasPrevious() {
/* 56 */     return (seekPrevious() != -1);
/*    */   }
/*    */   
/*    */   public int next() throws NoSuchElementException {
/* 60 */     int seek = seekNext();
/* 61 */     if (seek == -1) throw new NoSuchElementException(); 
/* 62 */     int next = this.iterators[seek].next();
/* 63 */     this.index++;
/* 64 */     return next;
/*    */   }
/*    */   
/*    */   public int peekNext() throws NoSuchElementException {
/* 68 */     int seek = seekNext();
/* 69 */     if (seek == -1) throw new NoSuchElementException(); 
/* 70 */     return this.iterators[seek].peekNext();
/*    */   }
/*    */   
/*    */   public int previous() throws NoSuchElementException {
/* 74 */     int seek = seekPrevious();
/* 75 */     if (seek == -1) throw new NoSuchElementException(); 
/* 76 */     int previous = this.iterators[seek].previous();
/* 77 */     this.index--;
/* 78 */     return previous;
/*    */   }
/*    */   
/*    */   public int peekPrevious() throws NoSuchElementException {
/* 82 */     int seek = seekPrevious();
/* 83 */     if (seek == -1) throw new NoSuchElementException(); 
/* 84 */     return this.iterators[seek].peekPrevious();
/*    */   }
/*    */   
/*    */   public long getIndex() {
/* 88 */     return this.index;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\wildfly\common\iteration\ConcatByteIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */