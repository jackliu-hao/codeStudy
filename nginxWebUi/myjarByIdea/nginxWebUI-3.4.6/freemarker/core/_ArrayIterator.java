package freemarker.core;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class _ArrayIterator implements Iterator {
   private final Object[] array;
   private int nextIndex;

   public _ArrayIterator(Object[] array) {
      this.array = array;
      this.nextIndex = 0;
   }

   public boolean hasNext() {
      return this.nextIndex < this.array.length;
   }

   public Object next() {
      if (this.nextIndex >= this.array.length) {
         throw new NoSuchElementException();
      } else {
         return this.array[this.nextIndex++];
      }
   }

   public void remove() {
      throw new UnsupportedOperationException();
   }
}
