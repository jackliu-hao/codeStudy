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
/*    */ final class CharArrayIterator
/*    */   extends CodePointIterator
/*    */ {
/*    */   private final int len;
/*    */   private final char[] chars;
/*    */   private final int offs;
/*    */   private int idx;
/*    */   private int offset;
/*    */   
/*    */   CharArrayIterator(int len, char[] chars, int offs) {
/* 33 */     this.len = len;
/* 34 */     this.chars = chars;
/* 35 */     this.offs = offs;
/* 36 */     this.idx = 0;
/* 37 */     this.offset = 0;
/*    */   }
/*    */   
/*    */   public boolean hasNext() {
/* 41 */     return (this.idx < this.len);
/*    */   }
/*    */   
/*    */   public boolean hasPrevious() {
/* 45 */     return (this.idx > 0);
/*    */   }
/*    */   
/*    */   public int next() {
/* 49 */     if (!hasNext()) throw new NoSuchElementException(); 
/*    */     try {
/* 51 */       this.offset++;
/* 52 */       return Character.codePointAt(this.chars, this.offs + this.idx);
/*    */     } finally {
/* 54 */       this.idx = Character.offsetByCodePoints(this.chars, this.offs, this.len, this.offs + this.idx, 1) - this.offs;
/*    */     } 
/*    */   }
/*    */   
/*    */   public int peekNext() throws NoSuchElementException {
/* 59 */     if (!hasNext()) throw new NoSuchElementException(); 
/* 60 */     return Character.codePointAt(this.chars, this.offs + this.idx);
/*    */   }
/*    */   
/*    */   public int previous() {
/* 64 */     if (!hasPrevious()) throw new NoSuchElementException(); 
/* 65 */     this.idx = Character.offsetByCodePoints(this.chars, this.offs, this.len, this.offs + this.idx, -1) - this.offs;
/* 66 */     this.offset--;
/* 67 */     return Character.codePointAt(this.chars, this.offs + this.idx);
/*    */   }
/*    */   
/*    */   public int peekPrevious() throws NoSuchElementException {
/* 71 */     if (!hasPrevious()) throw new NoSuchElementException(); 
/* 72 */     return Character.codePointBefore(this.chars, this.offs + this.idx);
/*    */   }
/*    */   
/*    */   public long getIndex() {
/* 76 */     return this.offset;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\wildfly\common\iteration\CharArrayIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */