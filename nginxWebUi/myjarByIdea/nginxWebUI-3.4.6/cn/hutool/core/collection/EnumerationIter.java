package cn.hutool.core.collection;

import java.io.Serializable;
import java.util.Enumeration;

public class EnumerationIter<E> implements IterableIter<E>, Serializable {
   private static final long serialVersionUID = 1L;
   private final Enumeration<E> e;

   public EnumerationIter(Enumeration<E> enumeration) {
      this.e = enumeration;
   }

   public boolean hasNext() {
      return this.e.hasMoreElements();
   }

   public E next() {
      return this.e.nextElement();
   }

   public void remove() {
      throw new UnsupportedOperationException();
   }
}
