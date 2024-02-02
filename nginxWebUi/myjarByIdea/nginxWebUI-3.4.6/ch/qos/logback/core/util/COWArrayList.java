package ch.qos.logback.core.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public class COWArrayList<E> implements List<E> {
   AtomicBoolean fresh = new AtomicBoolean(false);
   CopyOnWriteArrayList<E> underlyingList = new CopyOnWriteArrayList();
   E[] ourCopy;
   final E[] modelArray;

   public COWArrayList(E[] modelArray) {
      this.modelArray = modelArray;
   }

   public int size() {
      return this.underlyingList.size();
   }

   public boolean isEmpty() {
      return this.underlyingList.isEmpty();
   }

   public boolean contains(Object o) {
      return this.underlyingList.contains(o);
   }

   public Iterator<E> iterator() {
      return this.underlyingList.iterator();
   }

   private void refreshCopyIfNecessary() {
      if (!this.isFresh()) {
         this.refreshCopy();
      }

   }

   private boolean isFresh() {
      return this.fresh.get();
   }

   private void refreshCopy() {
      this.ourCopy = this.underlyingList.toArray(this.modelArray);
      this.fresh.set(true);
   }

   public Object[] toArray() {
      this.refreshCopyIfNecessary();
      return this.ourCopy;
   }

   public <T> T[] toArray(T[] a) {
      this.refreshCopyIfNecessary();
      return (Object[])this.ourCopy;
   }

   public E[] asTypedArray() {
      this.refreshCopyIfNecessary();
      return this.ourCopy;
   }

   private void markAsStale() {
      this.fresh.set(false);
   }

   public void addIfAbsent(E e) {
      this.underlyingList.addIfAbsent(e);
      this.markAsStale();
   }

   public boolean add(E e) {
      boolean result = this.underlyingList.add(e);
      this.markAsStale();
      return result;
   }

   public boolean remove(Object o) {
      boolean result = this.underlyingList.remove(o);
      this.markAsStale();
      return result;
   }

   public boolean containsAll(Collection<?> c) {
      return this.underlyingList.containsAll(c);
   }

   public boolean addAll(Collection<? extends E> c) {
      boolean result = this.underlyingList.addAll(c);
      this.markAsStale();
      return result;
   }

   public boolean addAll(int index, Collection<? extends E> col) {
      boolean result = this.underlyingList.addAll(index, col);
      this.markAsStale();
      return result;
   }

   public boolean removeAll(Collection<?> col) {
      boolean result = this.underlyingList.removeAll(col);
      this.markAsStale();
      return result;
   }

   public boolean retainAll(Collection<?> col) {
      boolean result = this.underlyingList.retainAll(col);
      this.markAsStale();
      return result;
   }

   public void clear() {
      this.underlyingList.clear();
      this.markAsStale();
   }

   public E get(int index) {
      this.refreshCopyIfNecessary();
      return this.ourCopy[index];
   }

   public E set(int index, E element) {
      E e = this.underlyingList.set(index, element);
      this.markAsStale();
      return e;
   }

   public void add(int index, E element) {
      this.underlyingList.add(index, element);
      this.markAsStale();
   }

   public E remove(int index) {
      E e = this.underlyingList.remove(index);
      this.markAsStale();
      return e;
   }

   public int indexOf(Object o) {
      return this.underlyingList.indexOf(o);
   }

   public int lastIndexOf(Object o) {
      return this.underlyingList.lastIndexOf(o);
   }

   public ListIterator<E> listIterator() {
      return this.underlyingList.listIterator();
   }

   public ListIterator<E> listIterator(int index) {
      return this.underlyingList.listIterator(index);
   }

   public List<E> subList(int fromIndex, int toIndex) {
      return this.underlyingList.subList(fromIndex, toIndex);
   }
}
