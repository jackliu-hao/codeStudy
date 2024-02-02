/*    */ package org.wildfly.common.iteration;
/*    */ 
/*    */ import java.nio.ByteBuffer;
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
/*    */ final class ByteBufferIterator
/*    */   extends ByteIterator
/*    */ {
/*    */   private final ByteBuffer buffer;
/*    */   private final int initialPosition;
/*    */   
/*    */   ByteBufferIterator(ByteBuffer buffer) {
/* 31 */     this.buffer = buffer;
/* 32 */     this.initialPosition = buffer.position();
/*    */   }
/*    */   
/*    */   public boolean hasNext() {
/* 36 */     return this.buffer.hasRemaining();
/*    */   }
/*    */   
/*    */   public boolean hasPrevious() {
/* 40 */     return (this.buffer.position() > this.initialPosition);
/*    */   }
/*    */   
/*    */   public int next() throws NoSuchElementException {
/* 44 */     if (!hasNext()) throw new NoSuchElementException(); 
/* 45 */     return this.buffer.get() & 0xFF;
/*    */   }
/*    */   
/*    */   public int peekNext() throws NoSuchElementException {
/* 49 */     if (!hasNext()) throw new NoSuchElementException(); 
/* 50 */     return this.buffer.get(this.buffer.position()) & 0xFF;
/*    */   }
/*    */   
/*    */   public int previous() throws NoSuchElementException {
/* 54 */     if (!hasPrevious()) throw new NoSuchElementException(); 
/* 55 */     this.buffer.position(this.buffer.position() - 1);
/* 56 */     return peekNext();
/*    */   }
/*    */   
/*    */   public int peekPrevious() throws NoSuchElementException {
/* 60 */     if (!hasPrevious()) throw new NoSuchElementException(); 
/* 61 */     return this.buffer.get(this.buffer.position() - 1) & 0xFF;
/*    */   }
/*    */   
/*    */   public long getIndex() {
/* 65 */     return (this.buffer.position() - this.initialPosition);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\wildfly\common\iteration\ByteBufferIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */