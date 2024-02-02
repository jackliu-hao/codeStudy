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
/*    */ final class StringIterator
/*    */   extends CodePointIterator
/*    */ {
/*    */   private final int len;
/*    */   private final String string;
/*    */   private final int offs;
/*    */   private int idx;
/*    */   private long offset;
/*    */   
/*    */   StringIterator(int len, String string, int offs) {
/* 33 */     this.len = len;
/* 34 */     this.string = string;
/* 35 */     this.offs = offs;
/* 36 */     this.idx = 0;
/* 37 */     this.offset = 0L;
/*    */   }
/*    */   
/*    */   public boolean hasNext() {
/* 41 */     return (this.idx < this.len);
/*    */   }
/*    */   
/*    */   public boolean hasPrevious() {
/* 45 */     return (this.offset > 0L);
/*    */   }
/*    */   
/*    */   public int next() {
/* 49 */     if (!hasNext()) throw new NoSuchElementException(); 
/*    */     try {
/* 51 */       this.offset++;
/* 52 */       return this.string.codePointAt(this.idx + this.offs);
/*    */     } finally {
/* 54 */       this.idx = this.string.offsetByCodePoints(this.idx + this.offs, 1) - this.offs;
/*    */     } 
/*    */   }
/*    */   
/*    */   public int peekNext() throws NoSuchElementException {
/* 59 */     if (!hasNext()) throw new NoSuchElementException(); 
/* 60 */     return this.string.codePointAt(this.idx + this.offs);
/*    */   }
/*    */   
/*    */   public int previous() {
/* 64 */     if (!hasPrevious()) throw new NoSuchElementException(); 
/* 65 */     this.idx = this.string.offsetByCodePoints(this.idx + this.offs, -1) - this.offs;
/* 66 */     this.offset--;
/* 67 */     return this.string.codePointAt(this.idx + this.offs);
/*    */   }
/*    */   
/*    */   public int peekPrevious() throws NoSuchElementException {
/* 71 */     if (!hasPrevious()) throw new NoSuchElementException(); 
/* 72 */     return this.string.codePointBefore(this.idx + this.offs);
/*    */   }
/*    */   
/*    */   public long getIndex() {
/* 76 */     return this.offset;
/*    */   }
/*    */   
/*    */   public StringBuilder drainTo(StringBuilder b) {
/*    */     try {
/* 81 */       return b.append(this.string, this.idx + this.offs, this.offs + this.len);
/*    */     } finally {
/* 83 */       this.offset += this.string.codePointCount(this.idx + this.offs, this.offs + this.len);
/* 84 */       this.idx = this.len;
/*    */     } 
/*    */   }
/*    */   
/*    */   public String drainToString() {
/*    */     try {
/* 90 */       return this.string.substring(this.idx + this.offs, this.offs + this.len);
/*    */     } finally {
/* 92 */       this.offset += this.string.codePointCount(this.idx + this.offs, this.offs + this.len);
/* 93 */       this.idx = this.len;
/*    */     } 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\wildfly\common\iteration\StringIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */