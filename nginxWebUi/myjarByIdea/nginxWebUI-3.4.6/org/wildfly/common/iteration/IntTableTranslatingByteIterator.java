package org.wildfly.common.iteration;

import java.util.NoSuchElementException;

final class IntTableTranslatingByteIterator extends ByteIterator {
   private final ByteIterator iter;
   private final int[] table;

   IntTableTranslatingByteIterator(ByteIterator iter, int[] table) {
      this.iter = iter;
      this.table = table;
   }

   public boolean hasNext() {
      return this.iter.hasNext();
   }

   public boolean hasPrevious() {
      return this.iter.hasPrevious();
   }

   public int next() throws NoSuchElementException {
      return this.table[this.iter.next()] & 255;
   }

   public int peekNext() throws NoSuchElementException {
      return this.table[this.iter.peekNext()] & 255;
   }

   public int previous() throws NoSuchElementException {
      return this.table[this.iter.previous()] & 255;
   }

   public int peekPrevious() throws NoSuchElementException {
      return this.table[this.iter.peekPrevious()] & 255;
   }

   public long getIndex() {
      return this.iter.getIndex();
   }
}
