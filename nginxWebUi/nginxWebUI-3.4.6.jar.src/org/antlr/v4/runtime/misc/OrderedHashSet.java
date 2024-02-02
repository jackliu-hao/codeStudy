/*     */ package org.antlr.v4.runtime.misc;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.Iterator;
/*     */ import java.util.LinkedHashSet;
/*     */ import java.util.List;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class OrderedHashSet<T>
/*     */   extends LinkedHashSet<T>
/*     */ {
/*  45 */   protected ArrayList<T> elements = new ArrayList<T>();
/*     */   
/*     */   public T get(int i) {
/*  48 */     return this.elements.get(i);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public T set(int i, T value) {
/*  55 */     T oldElement = this.elements.get(i);
/*  56 */     this.elements.set(i, value);
/*  57 */     super.remove(oldElement);
/*  58 */     super.add(value);
/*  59 */     return oldElement;
/*     */   }
/*     */   
/*     */   public boolean remove(int i) {
/*  63 */     T o = this.elements.remove(i);
/*  64 */     return super.remove(o);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean add(T value) {
/*  73 */     boolean result = super.add(value);
/*  74 */     if (result) {
/*  75 */       this.elements.add(value);
/*     */     }
/*  77 */     return result;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean remove(Object o) {
/*  82 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   
/*     */   public void clear() {
/*  87 */     this.elements.clear();
/*  88 */     super.clear();
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/*  93 */     return this.elements.hashCode();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object o) {
/*  98 */     if (!(o instanceof OrderedHashSet)) {
/*  99 */       return false;
/*     */     }
/*     */ 
/*     */     
/* 103 */     boolean same = (this.elements != null && this.elements.equals(((OrderedHashSet)o).elements));
/*     */     
/* 105 */     return same;
/*     */   }
/*     */ 
/*     */   
/*     */   public Iterator<T> iterator() {
/* 110 */     return this.elements.iterator();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public List<T> elements() {
/* 117 */     return this.elements;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Object clone() {
/* 123 */     OrderedHashSet<T> dup = (OrderedHashSet<T>)super.clone();
/* 124 */     dup.elements = new ArrayList<T>(this.elements);
/* 125 */     return dup;
/*     */   }
/*     */ 
/*     */   
/*     */   public Object[] toArray() {
/* 130 */     return this.elements.toArray();
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 135 */     return this.elements.toString();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\antlr\v4\runtime\misc\OrderedHashSet.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */