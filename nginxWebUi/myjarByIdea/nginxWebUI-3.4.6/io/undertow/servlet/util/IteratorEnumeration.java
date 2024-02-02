package io.undertow.servlet.util;

import java.util.Enumeration;
import java.util.Iterator;

public class IteratorEnumeration<T> implements Enumeration<T> {
   private final Iterator<T> iterator;

   public IteratorEnumeration(Iterator<T> iterator) {
      this.iterator = iterator;
   }

   public boolean hasMoreElements() {
      return this.iterator.hasNext();
   }

   public T nextElement() {
      return this.iterator.next();
   }
}
