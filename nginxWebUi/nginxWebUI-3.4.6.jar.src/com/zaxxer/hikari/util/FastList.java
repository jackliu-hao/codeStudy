/*     */ package com.zaxxer.hikari.util;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ import java.lang.reflect.Array;
/*     */ import java.util.Collection;
/*     */ import java.util.Comparator;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.ListIterator;
/*     */ import java.util.NoSuchElementException;
/*     */ import java.util.RandomAccess;
/*     */ import java.util.Spliterator;
/*     */ import java.util.function.Consumer;
/*     */ import java.util.function.Predicate;
/*     */ import java.util.function.UnaryOperator;
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
/*     */ public final class FastList<T>
/*     */   implements List<T>, RandomAccess, Serializable
/*     */ {
/*     */   private static final long serialVersionUID = -4598088075242913858L;
/*     */   private final Class<?> clazz;
/*     */   private T[] elementData;
/*     */   private int size;
/*     */   
/*     */   public FastList(Class<?> clazz) {
/*  53 */     this.elementData = (T[])Array.newInstance(clazz, 32);
/*  54 */     this.clazz = clazz;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public FastList(Class<?> clazz, int capacity) {
/*  65 */     this.elementData = (T[])Array.newInstance(clazz, capacity);
/*  66 */     this.clazz = clazz;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean add(T element) {
/*  77 */     if (this.size < this.elementData.length) {
/*  78 */       this.elementData[this.size++] = element;
/*     */     }
/*     */     else {
/*     */       
/*  82 */       int oldCapacity = this.elementData.length;
/*  83 */       int newCapacity = oldCapacity << 1;
/*     */       
/*  85 */       T[] newElementData = (T[])Array.newInstance(this.clazz, newCapacity);
/*  86 */       System.arraycopy(this.elementData, 0, newElementData, 0, oldCapacity);
/*  87 */       newElementData[this.size++] = element;
/*  88 */       this.elementData = newElementData;
/*     */     } 
/*     */     
/*  91 */     return true;
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
/*     */   public T get(int index) {
/* 103 */     return this.elementData[index];
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
/*     */   public T removeLast() {
/* 115 */     T element = this.elementData[--this.size];
/* 116 */     this.elementData[this.size] = null;
/* 117 */     return element;
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
/*     */   public boolean remove(Object element) {
/* 130 */     for (int index = this.size - 1; index >= 0; index--) {
/* 131 */       if (element == this.elementData[index]) {
/* 132 */         int numMoved = this.size - index - 1;
/* 133 */         if (numMoved > 0) {
/* 134 */           System.arraycopy(this.elementData, index + 1, this.elementData, index, numMoved);
/*     */         }
/* 136 */         this.elementData[--this.size] = null;
/* 137 */         return true;
/*     */       } 
/*     */     } 
/*     */     
/* 141 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void clear() {
/* 150 */     for (int i = 0; i < this.size; i++) {
/* 151 */       this.elementData[i] = null;
/*     */     }
/*     */     
/* 154 */     this.size = 0;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int size() {
/* 165 */     return this.size;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isEmpty() {
/* 172 */     return (this.size == 0);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public T set(int index, T element) {
/* 179 */     T old = this.elementData[index];
/* 180 */     this.elementData[index] = element;
/* 181 */     return old;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public T remove(int index) {
/* 188 */     if (this.size == 0) {
/* 189 */       return null;
/*     */     }
/*     */     
/* 192 */     T old = this.elementData[index];
/*     */     
/* 194 */     int numMoved = this.size - index - 1;
/* 195 */     if (numMoved > 0) {
/* 196 */       System.arraycopy(this.elementData, index + 1, this.elementData, index, numMoved);
/*     */     }
/*     */     
/* 199 */     this.elementData[--this.size] = null;
/*     */     
/* 201 */     return old;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean contains(Object o) {
/* 208 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Iterator<T> iterator() {
/* 215 */     return new Iterator<T>()
/*     */       {
/*     */         private int index;
/*     */ 
/*     */         
/*     */         public boolean hasNext() {
/* 221 */           return (this.index < FastList.this.size);
/*     */         }
/*     */ 
/*     */ 
/*     */         
/*     */         public T next() {
/* 227 */           if (this.index < FastList.this.size) {
/* 228 */             return (T)FastList.this.elementData[this.index++];
/*     */           }
/*     */           
/* 231 */           throw new NoSuchElementException("No more elements in FastList");
/*     */         }
/*     */       };
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Object[] toArray() {
/* 240 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public <E> E[] toArray(E[] a) {
/* 247 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean containsAll(Collection<?> c) {
/* 254 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean addAll(Collection<? extends T> c) {
/* 261 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean addAll(int index, Collection<? extends T> c) {
/* 268 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean removeAll(Collection<?> c) {
/* 275 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean retainAll(Collection<?> c) {
/* 282 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void add(int index, T element) {
/* 289 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int indexOf(Object o) {
/* 296 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int lastIndexOf(Object o) {
/* 303 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ListIterator<T> listIterator() {
/* 310 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ListIterator<T> listIterator(int index) {
/* 317 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public List<T> subList(int fromIndex, int toIndex) {
/* 324 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Object clone() {
/* 331 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void forEach(Consumer<? super T> action) {
/* 338 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Spliterator<T> spliterator() {
/* 345 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean removeIf(Predicate<? super T> filter) {
/* 352 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void replaceAll(UnaryOperator<T> operator) {
/* 359 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void sort(Comparator<? super T> c) {
/* 366 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\zaxxer\hikar\\util\FastList.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */