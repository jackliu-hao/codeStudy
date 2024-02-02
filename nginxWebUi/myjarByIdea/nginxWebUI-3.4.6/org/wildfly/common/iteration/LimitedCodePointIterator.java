package org.wildfly.common.iteration;

import java.util.NoSuchElementException;

final class LimitedCodePointIterator extends CodePointIterator {
   private final CodePointIterator iter;
   private final long size;
   private long offset;

   LimitedCodePointIterator(CodePointIterator iter, long size) {
      this.iter = iter;
      this.size = size;
      this.offset = 0L;
   }

   public boolean hasNext() {
      return this.offset < this.size && this.iter.hasNext();
   }

   public boolean hasPrevious() {
      return this.offset > 0L;
   }

   public int next() {
      if (!this.hasNext()) {
         throw new NoSuchElementException();
      } else {
         ++this.offset;
         return this.iter.next();
      }
   }

   public int peekNext() throws NoSuchElementException {
      if (!this.hasNext()) {
         throw new NoSuchElementException();
      } else {
         return this.iter.peekNext();
      }
   }

   public int previous() {
      if (!this.hasPrevious()) {
         throw new NoSuchElementException();
      } else {
         --this.offset;
         return this.iter.previous();
      }
   }

   public int peekPrevious() throws NoSuchElementException {
      if (!this.hasPrevious()) {
         throw new NoSuchElementException();
      } else {
         return this.iter.peekPrevious();
      }
   }

   public long getIndex() {
      return this.offset;
   }
}
