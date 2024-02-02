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
/*    */ final class Base16EncodingCodePointIterator
/*    */   extends CodePointIterator
/*    */ {
/*    */   private ByteIterator iter;
/*    */   private final boolean toUpperCase;
/*    */   int b;
/*    */   boolean lo;
/*    */   
/*    */   Base16EncodingCodePointIterator(ByteIterator iter, boolean toUpperCase) {
/* 32 */     this.iter = iter;
/* 33 */     this.toUpperCase = toUpperCase;
/*    */   }
/*    */   
/*    */   public boolean hasNext() {
/* 37 */     return (this.lo || this.iter.hasNext());
/*    */   }
/*    */   
/*    */   public boolean hasPrevious() {
/* 41 */     return (this.lo || this.iter.hasPrevious());
/*    */   }
/*    */   
/*    */   private int hex(int i) {
/* 45 */     if (i < 10) {
/* 46 */       return 48 + i;
/*    */     }
/* 48 */     assert i < 16;
/* 49 */     return (this.toUpperCase ? 65 : 97) + i - 10;
/*    */   }
/*    */ 
/*    */   
/*    */   public int next() throws NoSuchElementException {
/* 54 */     if (!hasNext()) throw new NoSuchElementException(); 
/* 55 */     if (this.lo) {
/* 56 */       this.lo = false;
/* 57 */       return hex(this.b & 0xF);
/*    */     } 
/* 59 */     this.b = this.iter.next();
/* 60 */     this.lo = true;
/* 61 */     return hex(this.b >> 4);
/*    */   }
/*    */ 
/*    */   
/*    */   public int peekNext() throws NoSuchElementException {
/* 66 */     if (!hasNext()) throw new NoSuchElementException(); 
/* 67 */     if (this.lo) {
/* 68 */       return hex(this.b & 0xF);
/*    */     }
/* 70 */     return hex(this.iter.peekNext() >> 4);
/*    */   }
/*    */ 
/*    */   
/*    */   public int previous() throws NoSuchElementException {
/* 75 */     if (!hasPrevious()) throw new NoSuchElementException(); 
/* 76 */     if (this.lo) {
/* 77 */       this.lo = false;
/* 78 */       this.iter.previous();
/* 79 */       return hex(this.b >> 4);
/*    */     } 
/* 81 */     this.b = this.iter.peekPrevious();
/* 82 */     this.lo = true;
/* 83 */     return hex(this.b & 0xF);
/*    */   }
/*    */ 
/*    */   
/*    */   public int peekPrevious() throws NoSuchElementException {
/* 88 */     if (!hasPrevious()) throw new NoSuchElementException(); 
/* 89 */     if (this.lo) {
/* 90 */       return hex(this.b >> 4);
/*    */     }
/* 92 */     return hex(this.iter.peekPrevious() & 0xF);
/*    */   }
/*    */ 
/*    */   
/*    */   public long getIndex() {
/* 97 */     return this.iter.getIndex() * 2L + (this.lo ? 1L : 0L);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\wildfly\common\iteration\Base16EncodingCodePointIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */