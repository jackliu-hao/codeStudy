package org.wildfly.common.iteration;

import java.util.NoSuchElementException;

final class ConcatByteIterator extends ByteIterator {
   private final ByteIterator[] iterators;
   private long index = 0L;

   ConcatByteIterator(ByteIterator[] iterators) {
      this.iterators = iterators;
   }

   private int seekNext() {
      for(int i = 0; i < this.iterators.length; ++i) {
         if (this.iterators[i].hasNext()) {
            return i;
         }
      }

      return -1;
   }

   private int seekPrevious() {
      for(int i = this.iterators.length - 1; i >= 0; --i) {
         if (this.iterators[i].hasPrevious()) {
            return i;
         }
      }

      return -1;
   }

   public boolean hasNext() {
      return this.seekNext() != -1;
   }

   public boolean hasPrevious() {
      return this.seekPrevious() != -1;
   }

   public int next() throws NoSuchElementException {
      int seek = this.seekNext();
      if (seek == -1) {
         throw new NoSuchElementException();
      } else {
         int next = this.iterators[seek].next();
         ++this.index;
         return next;
      }
   }

   public int peekNext() throws NoSuchElementException {
      int seek = this.seekNext();
      if (seek == -1) {
         throw new NoSuchElementException();
      } else {
         return this.iterators[seek].peekNext();
      }
   }

   public int previous() throws NoSuchElementException {
      int seek = this.seekPrevious();
      if (seek == -1) {
         throw new NoSuchElementException();
      } else {
         int previous = this.iterators[seek].previous();
         --this.index;
         return previous;
      }
   }

   public int peekPrevious() throws NoSuchElementException {
      int seek = this.seekPrevious();
      if (seek == -1) {
         throw new NoSuchElementException();
      } else {
         return this.iterators[seek].peekPrevious();
      }
   }

   public long getIndex() {
      return this.index;
   }
}
