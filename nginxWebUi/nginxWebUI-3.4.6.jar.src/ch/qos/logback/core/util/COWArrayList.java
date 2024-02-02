/*     */ package ch.qos.logback.core.util;
/*     */ 
/*     */ import java.util.Collection;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.ListIterator;
/*     */ import java.util.concurrent.CopyOnWriteArrayList;
/*     */ import java.util.concurrent.atomic.AtomicBoolean;
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
/*     */ public class COWArrayList<E>
/*     */   implements List<E>
/*     */ {
/*  45 */   AtomicBoolean fresh = new AtomicBoolean(false);
/*  46 */   CopyOnWriteArrayList<E> underlyingList = new CopyOnWriteArrayList<E>();
/*     */   E[] ourCopy;
/*     */   final E[] modelArray;
/*     */   
/*     */   public COWArrayList(E[] modelArray) {
/*  51 */     this.modelArray = modelArray;
/*     */   }
/*     */ 
/*     */   
/*     */   public int size() {
/*  56 */     return this.underlyingList.size();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isEmpty() {
/*  61 */     return this.underlyingList.isEmpty();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean contains(Object o) {
/*  66 */     return this.underlyingList.contains(o);
/*     */   }
/*     */ 
/*     */   
/*     */   public Iterator<E> iterator() {
/*  71 */     return this.underlyingList.iterator();
/*     */   }
/*     */   
/*     */   private void refreshCopyIfNecessary() {
/*  75 */     if (!isFresh()) {
/*  76 */       refreshCopy();
/*     */     }
/*     */   }
/*     */   
/*     */   private boolean isFresh() {
/*  81 */     return this.fresh.get();
/*     */   }
/*     */   
/*     */   private void refreshCopy() {
/*  85 */     this.ourCopy = this.underlyingList.toArray(this.modelArray);
/*  86 */     this.fresh.set(true);
/*     */   }
/*     */ 
/*     */   
/*     */   public Object[] toArray() {
/*  91 */     refreshCopyIfNecessary();
/*  92 */     return (Object[])this.ourCopy;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public <T> T[] toArray(T[] a) {
/*  98 */     refreshCopyIfNecessary();
/*  99 */     return (T[])this.ourCopy;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public E[] asTypedArray() {
/* 110 */     refreshCopyIfNecessary();
/* 111 */     return this.ourCopy;
/*     */   }
/*     */   
/*     */   private void markAsStale() {
/* 115 */     this.fresh.set(false);
/*     */   }
/*     */   
/*     */   public void addIfAbsent(E e) {
/* 119 */     this.underlyingList.addIfAbsent(e);
/* 120 */     markAsStale();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean add(E e) {
/* 125 */     boolean result = this.underlyingList.add(e);
/* 126 */     markAsStale();
/* 127 */     return result;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean remove(Object o) {
/* 132 */     boolean result = this.underlyingList.remove(o);
/* 133 */     markAsStale();
/* 134 */     return result;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean containsAll(Collection<?> c) {
/* 139 */     return this.underlyingList.containsAll(c);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean addAll(Collection<? extends E> c) {
/* 144 */     boolean result = this.underlyingList.addAll(c);
/* 145 */     markAsStale();
/* 146 */     return result;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean addAll(int index, Collection<? extends E> col) {
/* 151 */     boolean result = this.underlyingList.addAll(index, col);
/* 152 */     markAsStale();
/* 153 */     return result;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean removeAll(Collection<?> col) {
/* 158 */     boolean result = this.underlyingList.removeAll(col);
/* 159 */     markAsStale();
/* 160 */     return result;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean retainAll(Collection<?> col) {
/* 165 */     boolean result = this.underlyingList.retainAll(col);
/* 166 */     markAsStale();
/* 167 */     return result;
/*     */   }
/*     */ 
/*     */   
/*     */   public void clear() {
/* 172 */     this.underlyingList.clear();
/* 173 */     markAsStale();
/*     */   }
/*     */ 
/*     */   
/*     */   public E get(int index) {
/* 178 */     refreshCopyIfNecessary();
/* 179 */     return this.ourCopy[index];
/*     */   }
/*     */ 
/*     */   
/*     */   public E set(int index, E element) {
/* 184 */     E e = this.underlyingList.set(index, element);
/* 185 */     markAsStale();
/* 186 */     return e;
/*     */   }
/*     */ 
/*     */   
/*     */   public void add(int index, E element) {
/* 191 */     this.underlyingList.add(index, element);
/* 192 */     markAsStale();
/*     */   }
/*     */ 
/*     */   
/*     */   public E remove(int index) {
/* 197 */     E e = this.underlyingList.remove(index);
/* 198 */     markAsStale();
/* 199 */     return e;
/*     */   }
/*     */ 
/*     */   
/*     */   public int indexOf(Object o) {
/* 204 */     return this.underlyingList.indexOf(o);
/*     */   }
/*     */ 
/*     */   
/*     */   public int lastIndexOf(Object o) {
/* 209 */     return this.underlyingList.lastIndexOf(o);
/*     */   }
/*     */ 
/*     */   
/*     */   public ListIterator<E> listIterator() {
/* 214 */     return this.underlyingList.listIterator();
/*     */   }
/*     */ 
/*     */   
/*     */   public ListIterator<E> listIterator(int index) {
/* 219 */     return this.underlyingList.listIterator(index);
/*     */   }
/*     */ 
/*     */   
/*     */   public List<E> subList(int fromIndex, int toIndex) {
/* 224 */     return this.underlyingList.subList(fromIndex, toIndex);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\cor\\util\COWArrayList.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */