package cn.hutool.core.collection;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

public class CopiedIter<E> implements IterableIter<E>, Serializable {
   private static final long serialVersionUID = 1L;
   private final Iterator<E> listIterator;

   public static <E> CopiedIter<E> copyOf(Iterator<E> iterator) {
      return new CopiedIter(iterator);
   }

   public CopiedIter(Iterator<E> iterator) {
      List<E> eleList = ListUtil.toList(iterator);
      this.listIterator = eleList.iterator();
   }

   public boolean hasNext() {
      return this.listIterator.hasNext();
   }

   public E next() {
      return this.listIterator.next();
   }

   public void remove() throws UnsupportedOperationException {
      throw new UnsupportedOperationException("This is a read-only iterator.");
   }
}
