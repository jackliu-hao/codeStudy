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
/*    */ final class LimitedCodePointIterator
/*    */   extends CodePointIterator
/*    */ {
/*    */   private final CodePointIterator iter;
/*    */   private final long size;
/*    */   private long offset;
/*    */   
/*    */   LimitedCodePointIterator(CodePointIterator iter, long size) {
/* 31 */     this.iter = iter;
/* 32 */     this.size = size;
/* 33 */     this.offset = 0L;
/*    */   }
/*    */   
/*    */   public boolean hasNext() {
/* 37 */     return (this.offset < this.size && this.iter.hasNext());
/*    */   }
/*    */   
/*    */   public boolean hasPrevious() {
/* 41 */     return (this.offset > 0L);
/*    */   }
/*    */   
/*    */   public int next() {
/* 45 */     if (!hasNext()) {
/* 46 */       throw new NoSuchElementException();
/*    */     }
/* 48 */     this.offset++;
/* 49 */     return this.iter.next();
/*    */   }
/*    */   
/*    */   public int peekNext() throws NoSuchElementException {
/* 53 */     if (!hasNext()) {
/* 54 */       throw new NoSuchElementException();
/*    */     }
/* 56 */     return this.iter.peekNext();
/*    */   }
/*    */   
/*    */   public int previous() {
/* 60 */     if (!hasPrevious()) {
/* 61 */       throw new NoSuchElementException();
/*    */     }
/* 63 */     this.offset--;
/* 64 */     return this.iter.previous();
/*    */   }
/*    */   
/*    */   public int peekPrevious() throws NoSuchElementException {
/* 68 */     if (!hasPrevious()) {
/* 69 */       throw new NoSuchElementException();
/*    */     }
/* 71 */     return this.iter.peekPrevious();
/*    */   }
/*    */   
/*    */   public long getIndex() {
/* 75 */     return this.offset;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\wildfly\common\iteration\LimitedCodePointIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */