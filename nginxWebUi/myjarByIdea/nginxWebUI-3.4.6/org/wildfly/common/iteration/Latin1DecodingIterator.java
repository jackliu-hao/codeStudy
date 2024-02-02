package org.wildfly.common.iteration;

import java.util.NoSuchElementException;

final class Latin1DecodingIterator extends CodePointIterator {
   private final ByteIterator iter;
   private final long start;

   Latin1DecodingIterator(ByteIterator iter, long start) {
      this.iter = iter;
      this.start = start;
   }

   public boolean hasNext() {
      return this.iter.hasNext();
   }

   public boolean hasPrevious() {
      return this.start > 0L && this.iter.hasPrevious();
   }

   public int next() {
      return this.iter.next();
   }

   public int peekNext() throws NoSuchElementException {
      return this.iter.peekNext();
   }

   public int previous() {
      if (this.start == 0L) {
         throw new NoSuchElementException();
      } else {
         return this.iter.previous();
      }
   }

   public int peekPrevious() throws NoSuchElementException {
      return this.iter.peekPrevious();
   }

   public long getIndex() {
      return this.iter.getIndex() - this.start;
   }
}
