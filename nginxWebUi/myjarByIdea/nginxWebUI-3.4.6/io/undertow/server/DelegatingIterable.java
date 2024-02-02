package io.undertow.server;

import java.util.Iterator;

final class DelegatingIterable<E> implements Iterable<E> {
   private final Iterable<E> delegate;

   DelegatingIterable(Iterable<E> delegate) {
      this.delegate = delegate;
   }

   Iterable<E> getDelegate() {
      return this.delegate;
   }

   public Iterator<E> iterator() {
      return new ReadOnlyIterator(this.delegate.iterator());
   }
}
