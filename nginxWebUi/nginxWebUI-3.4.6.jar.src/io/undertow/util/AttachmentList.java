/*     */ package io.undertow.util;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.ListIterator;
/*     */ import java.util.RandomAccess;
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
/*     */ public final class AttachmentList<T>
/*     */   implements List<T>, RandomAccess
/*     */ {
/*     */   private final Class<T> valueClass;
/*     */   private final List<T> delegate;
/*     */   
/*     */   public AttachmentList(int initialCapacity, Class<T> valueClass) {
/*  38 */     this.delegate = Collections.checkedList(new ArrayList<>(initialCapacity), valueClass);
/*  39 */     this.valueClass = valueClass;
/*     */   }
/*     */   
/*     */   public AttachmentList(Class<T> valueClass) {
/*  43 */     this.delegate = Collections.checkedList(new ArrayList<>(), valueClass);
/*  44 */     this.valueClass = valueClass;
/*     */   }
/*     */   
/*     */   public AttachmentList(Collection<? extends T> c, Class<T> valueClass) {
/*  48 */     this.delegate = Collections.checkedList(new ArrayList<>(c.size()), valueClass);
/*  49 */     this.delegate.addAll(c);
/*  50 */     this.valueClass = valueClass;
/*     */   }
/*     */   
/*     */   public Class<T> getValueClass() {
/*  54 */     return this.valueClass;
/*     */   }
/*     */   
/*     */   public int size() {
/*  58 */     return this.delegate.size();
/*     */   }
/*     */   
/*     */   public boolean isEmpty() {
/*  62 */     return this.delegate.isEmpty();
/*     */   }
/*     */   
/*     */   public boolean contains(Object o) {
/*  66 */     return this.delegate.contains(o);
/*     */   }
/*     */   
/*     */   public Iterator<T> iterator() {
/*  70 */     return this.delegate.iterator();
/*     */   }
/*     */   
/*     */   public Object[] toArray() {
/*  74 */     return this.delegate.toArray();
/*     */   }
/*     */   
/*     */   public <T> T[] toArray(T[] a) {
/*  78 */     return this.delegate.toArray(a);
/*     */   }
/*     */   
/*     */   public boolean add(T t) {
/*  82 */     return this.delegate.add(t);
/*     */   }
/*     */   
/*     */   public boolean remove(Object o) {
/*  86 */     return this.delegate.remove(o);
/*     */   }
/*     */   
/*     */   public boolean containsAll(Collection<?> c) {
/*  90 */     return this.delegate.containsAll(c);
/*     */   }
/*     */   
/*     */   public boolean addAll(Collection<? extends T> c) {
/*  94 */     return this.delegate.addAll(c);
/*     */   }
/*     */   
/*     */   public boolean addAll(int index, Collection<? extends T> c) {
/*  98 */     return this.delegate.addAll(index, c);
/*     */   }
/*     */   
/*     */   public boolean removeAll(Collection<?> c) {
/* 102 */     return this.delegate.removeAll(c);
/*     */   }
/*     */   
/*     */   public boolean retainAll(Collection<?> c) {
/* 106 */     return this.delegate.retainAll(c);
/*     */   }
/*     */   
/*     */   public void clear() {
/* 110 */     this.delegate.clear();
/*     */   }
/*     */   
/*     */   public boolean equals(Object o) {
/* 114 */     return this.delegate.equals(o);
/*     */   }
/*     */   
/*     */   public int hashCode() {
/* 118 */     return this.delegate.hashCode();
/*     */   }
/*     */   
/*     */   public T get(int index) {
/* 122 */     return this.delegate.get(index);
/*     */   }
/*     */   
/*     */   public T set(int index, T element) {
/* 126 */     return this.delegate.set(index, element);
/*     */   }
/*     */   
/*     */   public void add(int index, T element) {
/* 130 */     this.delegate.add(index, element);
/*     */   }
/*     */   
/*     */   public T remove(int index) {
/* 134 */     return this.delegate.remove(index);
/*     */   }
/*     */   
/*     */   public int indexOf(Object o) {
/* 138 */     return this.delegate.indexOf(o);
/*     */   }
/*     */   
/*     */   public int lastIndexOf(Object o) {
/* 142 */     return this.delegate.lastIndexOf(o);
/*     */   }
/*     */   
/*     */   public ListIterator<T> listIterator() {
/* 146 */     return this.delegate.listIterator();
/*     */   }
/*     */   
/*     */   public ListIterator<T> listIterator(int index) {
/* 150 */     return this.delegate.listIterator(index);
/*     */   }
/*     */   
/*     */   public List<T> subList(int fromIndex, int toIndex) {
/* 154 */     return this.delegate.subList(fromIndex, toIndex);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\underto\\util\AttachmentList.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */