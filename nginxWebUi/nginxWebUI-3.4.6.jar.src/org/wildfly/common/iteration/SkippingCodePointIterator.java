/*     */ package org.wildfly.common.iteration;
/*     */ 
/*     */ import java.util.NoSuchElementException;
/*     */ import java.util.function.IntPredicate;
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
/*     */ final class SkippingCodePointIterator
/*     */   extends CodePointIterator
/*     */ {
/*     */   private final CodePointIterator iter;
/*     */   private final IntPredicate predicate;
/*     */   
/*     */   SkippingCodePointIterator(CodePointIterator iter, IntPredicate predicate) {
/*  31 */     this.iter = iter;
/*  32 */     this.predicate = predicate;
/*     */   }
/*     */   
/*     */   public boolean hasNext() {
/*  36 */     return (this.iter.hasNext() && !skip(peekNext()));
/*     */   }
/*     */   
/*     */   public boolean hasPrevious() {
/*  40 */     return (this.iter.hasPrevious() && !skip(peekPrevious()));
/*     */   }
/*     */   
/*     */   public int next() {
/*  44 */     if (!hasNext()) {
/*  45 */       throw new NoSuchElementException();
/*     */     }
/*     */     
/*  48 */     return this.iter.next();
/*     */   }
/*     */   
/*     */   public int peekNext() throws NoSuchElementException {
/*  52 */     if (!this.iter.hasNext()) {
/*  53 */       throw new NoSuchElementException();
/*     */     }
/*     */     
/*  56 */     int next = seekNext(this.iter.peekNext());
/*     */     
/*  58 */     if (!skip(next)) {
/*  59 */       return next;
/*     */     }
/*     */     
/*  62 */     return next;
/*     */   }
/*     */   
/*     */   private int seekNext(int next) throws NoSuchElementException {
/*  66 */     if (!this.iter.hasNext()) {
/*  67 */       return next;
/*     */     }
/*     */     
/*  70 */     next = this.iter.next();
/*     */     
/*  72 */     if (skip(next)) {
/*  73 */       return seekNext(next);
/*     */     }
/*     */     
/*  76 */     this.iter.previous();
/*     */     
/*  78 */     return next;
/*     */   }
/*     */   
/*     */   public int previous() {
/*  82 */     if (!hasPrevious()) {
/*  83 */       throw new NoSuchElementException();
/*     */     }
/*     */     
/*  86 */     return this.iter.previous();
/*     */   }
/*     */   
/*     */   public int peekPrevious() throws NoSuchElementException {
/*  90 */     if (!this.iter.hasPrevious()) {
/*  91 */       throw new NoSuchElementException();
/*     */     }
/*     */     
/*  94 */     int prev = seekPrev(this.iter.peekPrevious());
/*     */     
/*  96 */     if (!skip(prev)) {
/*  97 */       return prev;
/*     */     }
/*     */     
/* 100 */     return prev;
/*     */   }
/*     */   
/*     */   private int seekPrev(int prev) throws NoSuchElementException {
/* 104 */     if (!this.iter.hasPrevious()) {
/* 105 */       return prev;
/*     */     }
/*     */     
/* 108 */     prev = this.iter.previous();
/*     */     
/* 110 */     if (skip(prev)) {
/* 111 */       return seekPrev(prev);
/*     */     }
/*     */     
/* 114 */     this.iter.next();
/*     */     
/* 116 */     return prev;
/*     */   }
/*     */   
/*     */   public long getIndex() {
/* 120 */     return this.iter.getIndex();
/*     */   }
/*     */   
/*     */   private boolean skip(int c) {
/* 124 */     return this.predicate.test(c);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\wildfly\common\iteration\SkippingCodePointIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */