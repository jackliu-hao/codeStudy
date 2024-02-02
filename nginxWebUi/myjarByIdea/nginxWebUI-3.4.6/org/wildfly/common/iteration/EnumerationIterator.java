package org.wildfly.common.iteration;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.NoSuchElementException;

public interface EnumerationIterator<E> extends Enumeration<E>, Iterator<E> {
   default boolean hasMoreElements() {
      return this.hasNext();
   }

   default E nextElement() {
      return this.next();
   }

   static <E> EnumerationIterator<E> over(final E item) {
      return new EnumerationIterator<E>() {
         boolean done;

         public boolean hasNext() {
            return !this.done;
         }

         public E next() {
            if (!this.hasNext()) {
               throw new NoSuchElementException();
            } else {
               this.done = true;
               return item;
            }
         }
      };
   }
}
