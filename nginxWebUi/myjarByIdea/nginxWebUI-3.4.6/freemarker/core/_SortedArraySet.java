package freemarker.core;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

public class _SortedArraySet<E> extends _UnmodifiableSet<E> {
   private final E[] array;

   public _SortedArraySet(E[] array) {
      this.array = array;
   }

   public int size() {
      return this.array.length;
   }

   public boolean contains(Object o) {
      return Arrays.binarySearch(this.array, o) >= 0;
   }

   public Iterator<E> iterator() {
      return new _ArrayIterator(this.array);
   }

   public boolean add(E o) {
      throw new UnsupportedOperationException();
   }

   public boolean remove(Object o) {
      throw new UnsupportedOperationException();
   }

   public boolean addAll(Collection<? extends E> c) {
      throw new UnsupportedOperationException();
   }

   public boolean removeAll(Collection<?> c) {
      throw new UnsupportedOperationException();
   }

   public boolean retainAll(Collection<?> c) {
      throw new UnsupportedOperationException();
   }

   public void clear() {
      throw new UnsupportedOperationException();
   }
}
