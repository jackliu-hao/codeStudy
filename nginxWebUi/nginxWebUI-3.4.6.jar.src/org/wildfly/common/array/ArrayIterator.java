/*     */ package org.wildfly.common.array;
/*     */ 
/*     */ import java.util.ListIterator;
/*     */ import java.util.NoSuchElementException;
/*     */ import org.wildfly.common.Assert;
/*     */ import org.wildfly.common.iteration.EnumerationIterator;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class ArrayIterator<E>
/*     */   implements ListIterator<E>, EnumerationIterator<E>
/*     */ {
/*     */   private final E[] elements;
/*     */   private final boolean descending;
/*     */   private int idx;
/*     */   
/*     */   public ArrayIterator(E[] elements) {
/*  43 */     this(elements, false);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ArrayIterator(E[] elements, boolean descending) {
/*  53 */     this(elements, descending, descending ? elements.length : 0);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ArrayIterator(E[] elements, int startIdx) {
/*  63 */     this(elements, false, startIdx);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ArrayIterator(E[] elements, boolean descending, int startIdx) {
/*  74 */     Assert.checkNotNullParam("elements", elements);
/*  75 */     Assert.checkMinimumParameter("startIdx", 0, startIdx);
/*  76 */     Assert.checkMaximumParameter("startIdx", elements.length, startIdx);
/*  77 */     this.elements = elements;
/*  78 */     this.descending = descending;
/*  79 */     this.idx = startIdx;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean hasPrevious() {
/*  90 */     return this.descending ? ((this.idx < this.elements.length)) : ((this.idx > 0));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean hasNext() {
/*  99 */     return this.descending ? ((this.idx > 0)) : ((this.idx < this.elements.length));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public E previous() {
/* 108 */     if (!hasPrevious()) throw new NoSuchElementException(); 
/* 109 */     return this.elements[this.descending ? this.idx++ : --this.idx];
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public E next() {
/* 118 */     if (!hasNext()) throw new NoSuchElementException(); 
/* 119 */     return this.elements[this.descending ? --this.idx : this.idx++];
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int nextIndex() {
/* 128 */     return this.descending ? (this.idx - 1) : this.idx;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int previousIndex() {
/* 137 */     return this.descending ? this.idx : (this.idx - 1);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void remove() {
/* 146 */     throw Assert.unsupported();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void set(E e) {
/* 155 */     throw Assert.unsupported();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void add(E e) {
/* 164 */     throw Assert.unsupported();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\wildfly\common\array\ArrayIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */