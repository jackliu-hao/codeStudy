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
/*    */ final class LimitedByteIterator
/*    */   extends ByteIterator
/*    */ {
/*    */   private final ByteIterator iter;
/*    */   private final long size;
/*    */   long offset;
/*    */   
/*    */   LimitedByteIterator(ByteIterator iter, long size) {
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
/* 45 */     if (this.offset == this.size) {
/* 46 */       throw new NoSuchElementException();
/*    */     }
/* 48 */     this.offset++;
/* 49 */     return this.iter.next();
/*    */   }
/*    */   
/*    */   public int peekNext() throws NoSuchElementException {
/* 53 */     if (this.offset == this.size) {
/* 54 */       throw new NoSuchElementException();
/*    */     }
/* 56 */     return this.iter.peekNext();
/*    */   }
/*    */   
/*    */   public int previous() {
/* 60 */     if (this.offset == 0L) {
/* 61 */       throw new NoSuchElementException();
/*    */     }
/* 63 */     this.offset--;
/* 64 */     return this.iter.previous();
/*    */   }
/*    */   
/*    */   public int peekPrevious() throws NoSuchElementException {
/* 68 */     if (this.offset == 0L) {
/* 69 */       throw new NoSuchElementException();
/*    */     }
/* 71 */     return this.iter.peekPrevious();
/*    */   }
/*    */   
/*    */   public int drain(byte[] dst, int offs, int len) {
/* 75 */     return super.drain(dst, offs, (int)Math.min(len, this.size - this.offset));
/*    */   }
/*    */   
/*    */   public long getIndex() {
/* 79 */     return this.offset;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\wildfly\common\iteration\LimitedByteIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */