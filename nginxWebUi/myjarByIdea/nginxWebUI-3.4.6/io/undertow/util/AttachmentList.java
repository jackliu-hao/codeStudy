package io.undertow.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.RandomAccess;

public final class AttachmentList<T> implements List<T>, RandomAccess {
   private final Class<T> valueClass;
   private final List<T> delegate;

   public AttachmentList(int initialCapacity, Class<T> valueClass) {
      this.delegate = Collections.checkedList(new ArrayList(initialCapacity), valueClass);
      this.valueClass = valueClass;
   }

   public AttachmentList(Class<T> valueClass) {
      this.delegate = Collections.checkedList(new ArrayList(), valueClass);
      this.valueClass = valueClass;
   }

   public AttachmentList(Collection<? extends T> c, Class<T> valueClass) {
      this.delegate = Collections.checkedList(new ArrayList(c.size()), valueClass);
      this.delegate.addAll(c);
      this.valueClass = valueClass;
   }

   public Class<T> getValueClass() {
      return this.valueClass;
   }

   public int size() {
      return this.delegate.size();
   }

   public boolean isEmpty() {
      return this.delegate.isEmpty();
   }

   public boolean contains(Object o) {
      return this.delegate.contains(o);
   }

   public Iterator<T> iterator() {
      return this.delegate.iterator();
   }

   public Object[] toArray() {
      return this.delegate.toArray();
   }

   public <T> T[] toArray(T[] a) {
      return this.delegate.toArray(a);
   }

   public boolean add(T t) {
      return this.delegate.add(t);
   }

   public boolean remove(Object o) {
      return this.delegate.remove(o);
   }

   public boolean containsAll(Collection<?> c) {
      return this.delegate.containsAll(c);
   }

   public boolean addAll(Collection<? extends T> c) {
      return this.delegate.addAll(c);
   }

   public boolean addAll(int index, Collection<? extends T> c) {
      return this.delegate.addAll(index, c);
   }

   public boolean removeAll(Collection<?> c) {
      return this.delegate.removeAll(c);
   }

   public boolean retainAll(Collection<?> c) {
      return this.delegate.retainAll(c);
   }

   public void clear() {
      this.delegate.clear();
   }

   public boolean equals(Object o) {
      return this.delegate.equals(o);
   }

   public int hashCode() {
      return this.delegate.hashCode();
   }

   public T get(int index) {
      return this.delegate.get(index);
   }

   public T set(int index, T element) {
      return this.delegate.set(index, element);
   }

   public void add(int index, T element) {
      this.delegate.add(index, element);
   }

   public T remove(int index) {
      return this.delegate.remove(index);
   }

   public int indexOf(Object o) {
      return this.delegate.indexOf(o);
   }

   public int lastIndexOf(Object o) {
      return this.delegate.lastIndexOf(o);
   }

   public ListIterator<T> listIterator() {
      return this.delegate.listIterator();
   }

   public ListIterator<T> listIterator(int index) {
      return this.delegate.listIterator(index);
   }

   public List<T> subList(int fromIndex, int toIndex) {
      return this.delegate.subList(fromIndex, toIndex);
   }
}
