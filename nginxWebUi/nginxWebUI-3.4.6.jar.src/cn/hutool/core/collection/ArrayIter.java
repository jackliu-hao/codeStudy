/*     */ package cn.hutool.core.collection;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ import java.lang.reflect.Array;
/*     */ import java.util.NoSuchElementException;
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
/*     */ public class ArrayIter<E>
/*     */   implements IterableIter<E>, ResettableIter<E>, Serializable
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   private final Object array;
/*     */   private int startIndex;
/*     */   private int endIndex;
/*     */   private int index;
/*     */   
/*     */   public ArrayIter(E[] array) {
/*  42 */     this(array);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ArrayIter(Object array) {
/*  53 */     this(array, 0);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ArrayIter(Object array, int startIndex) {
/*  65 */     this(array, startIndex, -1);
/*     */   }
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
/*     */   public ArrayIter(Object array, int startIndex, int endIndex) {
/*  78 */     this.endIndex = Array.getLength(array);
/*  79 */     if (endIndex > 0 && endIndex < this.endIndex) {
/*  80 */       this.endIndex = endIndex;
/*     */     }
/*     */     
/*  83 */     if (startIndex >= 0 && startIndex < this.endIndex) {
/*  84 */       this.startIndex = startIndex;
/*     */     }
/*  86 */     this.array = array;
/*  87 */     this.index = this.startIndex;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean hasNext() {
/*  92 */     return (this.index < this.endIndex);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public E next() {
/*  98 */     if (!hasNext()) {
/*  99 */       throw new NoSuchElementException();
/*     */     }
/* 101 */     return (E)Array.get(this.array, this.index++);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void remove() {
/* 111 */     throw new UnsupportedOperationException("remove() method is not supported");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Object getArray() {
/* 123 */     return this.array;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void reset() {
/* 131 */     this.index = this.startIndex;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\collection\ArrayIter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */