package freemarker.core;

import java.util.AbstractSet;

public abstract class _UnmodifiableSet<E> extends AbstractSet<E> {
   public boolean add(E o) {
      throw new UnsupportedOperationException();
   }

   public boolean remove(Object o) {
      if (this.contains(o)) {
         throw new UnsupportedOperationException();
      } else {
         return false;
      }
   }

   public void clear() {
      if (!this.isEmpty()) {
         throw new UnsupportedOperationException();
      }
   }
}
