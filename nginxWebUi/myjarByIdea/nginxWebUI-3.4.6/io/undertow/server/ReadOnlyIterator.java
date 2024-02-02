package io.undertow.server;

import java.util.Iterator;
import java.util.function.Consumer;

final class ReadOnlyIterator<E> implements Iterator<E> {
   final Iterator<E> delegate;

   ReadOnlyIterator(Iterator<E> delegate) {
      this.delegate = delegate;
   }

   public void remove() {
      throw new UnsupportedOperationException();
   }

   public void forEachRemaining(Consumer<? super E> action) {
      this.delegate.forEachRemaining(action);
   }

   public boolean hasNext() {
      return this.delegate.hasNext();
   }

   public E next() {
      return this.delegate.next();
   }
}
