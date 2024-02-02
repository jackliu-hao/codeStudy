/*     */ package org.wildfly.common.iteration;
/*     */ 
/*     */ import java.util.NoSuchElementException;
/*     */ import org.wildfly.common._private.CommonMessages;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ final class Base16DecodingByteIterator
/*     */   extends ByteIterator
/*     */ {
/*     */   private final CodePointIterator iter;
/*     */   private int b;
/*     */   private long offset;
/*     */   private boolean havePair;
/*     */   
/*     */   Base16DecodingByteIterator(CodePointIterator iter) {
/*  34 */     this.iter = iter;
/*     */   }
/*     */   
/*     */   private int calc(int b0, int b1) {
/*  38 */     int d0 = Character.digit(b0, 16);
/*  39 */     int d1 = Character.digit(b1, 16);
/*  40 */     if (d0 == -1 || d1 == -1) throw CommonMessages.msg.invalidHexCharacter(); 
/*  41 */     return (d0 << 4 | d1) & 0xFF;
/*     */   }
/*     */   
/*     */   public boolean hasNext() {
/*  45 */     if (this.havePair) {
/*  46 */       return true;
/*     */     }
/*  48 */     if (!this.iter.hasNext()) {
/*  49 */       return false;
/*     */     }
/*  51 */     int b0 = this.iter.next();
/*  52 */     if (!this.iter.hasNext()) {
/*  53 */       throw CommonMessages.msg.expectedEvenNumberOfHexCharacters();
/*     */     }
/*  55 */     int b1 = this.iter.next();
/*  56 */     this.b = calc(b0, b1);
/*  57 */     this.havePair = true;
/*  58 */     return true;
/*     */   }
/*     */   
/*     */   public boolean hasPrevious() {
/*  62 */     return (this.offset > 0L);
/*     */   }
/*     */   
/*     */   public int next() throws NoSuchElementException {
/*  66 */     if (!hasNext()) {
/*  67 */       throw new NoSuchElementException();
/*     */     }
/*  69 */     this.offset++;
/*  70 */     this.havePair = false;
/*  71 */     return this.b;
/*     */   }
/*     */   
/*     */   public int peekNext() throws NoSuchElementException {
/*  75 */     if (!hasNext()) {
/*  76 */       throw new NoSuchElementException();
/*     */     }
/*  78 */     return this.b;
/*     */   }
/*     */   
/*     */   public int previous() throws NoSuchElementException {
/*  82 */     if (!hasPrevious()) {
/*  83 */       throw new NoSuchElementException();
/*     */     }
/*  85 */     int b1 = this.iter.previous();
/*  86 */     int b0 = this.iter.previous();
/*  87 */     this.b = calc(b0, b1);
/*  88 */     this.offset--;
/*  89 */     this.havePair = true;
/*  90 */     return this.b;
/*     */   }
/*     */   
/*     */   public int peekPrevious() throws NoSuchElementException {
/*  94 */     if (!hasPrevious()) {
/*  95 */       throw new NoSuchElementException();
/*     */     }
/*  97 */     int b1 = this.iter.previous();
/*  98 */     int b0 = this.iter.peekPrevious();
/*  99 */     this.iter.next();
/* 100 */     return calc(b0, b1);
/*     */   }
/*     */   
/*     */   public long getIndex() {
/* 104 */     return this.offset;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\wildfly\common\iteration\Base16DecodingByteIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */