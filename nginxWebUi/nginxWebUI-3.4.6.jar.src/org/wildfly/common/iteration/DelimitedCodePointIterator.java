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
/*    */ final class DelimitedCodePointIterator
/*    */   extends CodePointIterator
/*    */ {
/*    */   private final CodePointIterator iter;
/*    */   private final int[] delims;
/*    */   long offset;
/*    */   
/*    */   DelimitedCodePointIterator(CodePointIterator iter, int... delims) {
/* 31 */     this.iter = iter;
/* 32 */     this.delims = delims;
/* 33 */     this.offset = 0L;
/*    */   }
/*    */   
/*    */   public boolean hasNext() {
/* 37 */     return (this.iter.hasNext() && !isDelim(this.iter.peekNext()));
/*    */   }
/*    */   
/*    */   public boolean hasPrevious() {
/* 41 */     return (this.offset > 0L);
/*    */   }
/*    */   
/*    */   public int next() {
/* 45 */     if (!hasNext()) throw new NoSuchElementException(); 
/* 46 */     this.offset++;
/* 47 */     return this.iter.next();
/*    */   }
/*    */   
/*    */   public int peekNext() throws NoSuchElementException {
/* 51 */     if (!hasNext()) throw new NoSuchElementException(); 
/* 52 */     return this.iter.peekNext();
/*    */   }
/*    */   
/*    */   public int previous() {
/* 56 */     if (!hasPrevious()) throw new NoSuchElementException(); 
/* 57 */     this.offset--;
/* 58 */     return this.iter.previous();
/*    */   }
/*    */   
/*    */   public int peekPrevious() throws NoSuchElementException {
/* 62 */     if (!hasPrevious()) throw new NoSuchElementException(); 
/* 63 */     return this.iter.peekPrevious();
/*    */   }
/*    */   
/*    */   public long getIndex() {
/* 67 */     return this.offset;
/*    */   }
/*    */   
/*    */   private boolean isDelim(int b) {
/* 71 */     for (int delim : this.delims) {
/* 72 */       if (delim == b) {
/* 73 */         return true;
/*    */       }
/*    */     } 
/* 76 */     return false;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\wildfly\common\iteration\DelimitedCodePointIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */