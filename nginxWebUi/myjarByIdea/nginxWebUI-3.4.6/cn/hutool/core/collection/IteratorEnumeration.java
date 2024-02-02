package cn.hutool.core.collection;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.Iterator;

public class IteratorEnumeration<E> implements Enumeration<E>, Serializable {
   private static final long serialVersionUID = 1L;
   private final Iterator<E> iterator;

   public IteratorEnumeration(Iterator<E> iterator) {
      this.iterator = iterator;
   }

   public boolean hasMoreElements() {
      return this.iterator.hasNext();
   }

   public E nextElement() {
      return this.iterator.next();
   }
}
