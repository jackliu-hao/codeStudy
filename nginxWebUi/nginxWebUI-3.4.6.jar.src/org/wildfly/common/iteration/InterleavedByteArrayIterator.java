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
/*    */ final class InterleavedByteArrayIterator
/*    */   extends ByteIterator
/*    */ {
/*    */   private final int len;
/*    */   private final byte[] bytes;
/*    */   private final int offs;
/*    */   private final int[] interleave;
/*    */   private int idx;
/*    */   
/*    */   InterleavedByteArrayIterator(int len, byte[] bytes, int offs, int[] interleave) {
/* 33 */     this.len = len;
/* 34 */     this.bytes = bytes;
/* 35 */     this.offs = offs;
/* 36 */     this.interleave = interleave;
/* 37 */     this.idx = 0;
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
/* 50 */     return this.bytes[this.offs + this.interleave[this.idx++]] & 0xFF;
/*    */   }
/*    */   
/*    */   public int previous() {
/* 54 */     if (!hasPrevious()) throw new NoSuchElementException(); 
/* 55 */     return this.bytes[this.offs + this.interleave[--this.idx]] & 0xFF;
/*    */   }
/*    */   
/*    */   public int peekNext() throws NoSuchElementException {
/* 59 */     if (!hasNext()) throw new NoSuchElementException(); 
/* 60 */     return this.bytes[this.offs + this.interleave[this.idx]] & 0xFF;
/*    */   }
/*    */   
/*    */   public int peekPrevious() throws NoSuchElementException {
/* 64 */     if (!hasPrevious()) throw new NoSuchElementException(); 
/* 65 */     return this.bytes[this.offs + this.interleave[this.idx - 1]] & 0xFF;
/*    */   }
/*    */   
/*    */   public long getIndex() {
/* 69 */     return this.idx;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\wildfly\common\iteration\InterleavedByteArrayIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */