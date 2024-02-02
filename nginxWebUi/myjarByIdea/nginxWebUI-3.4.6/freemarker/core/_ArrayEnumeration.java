package freemarker.core;

import java.util.Enumeration;
import java.util.NoSuchElementException;

public class _ArrayEnumeration implements Enumeration {
   private final Object[] array;
   private final int size;
   private int nextIndex;

   public _ArrayEnumeration(Object[] array, int size) {
      this.array = array;
      this.size = size;
      this.nextIndex = 0;
   }

   public boolean hasMoreElements() {
      return this.nextIndex < this.size;
   }

   public Object nextElement() {
      if (this.nextIndex >= this.size) {
         throw new NoSuchElementException();
      } else {
         return this.array[this.nextIndex++];
      }
   }
}
