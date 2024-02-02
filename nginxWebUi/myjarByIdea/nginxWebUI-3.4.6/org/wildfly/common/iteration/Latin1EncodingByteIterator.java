package org.wildfly.common.iteration;

import java.util.NoSuchElementException;

final class Latin1EncodingByteIterator extends ByteIterator {
   private final CodePointIterator iter;

   Latin1EncodingByteIterator(CodePointIterator iter) {
      this.iter = iter;
   }

   public boolean hasNext() {
      return this.iter.hasNext();
   }

   public boolean hasPrevious() {
      return this.iter.hasPrevious();
   }

   public int next() throws NoSuchElementException {
      int v = this.iter.next();
      return v > 255 ? 63 : v;
   }

   public int peekNext() throws NoSuchElementException {
      int v = this.iter.peekNext();
      return v > 255 ? 63 : v;
   }

   public int previous() throws NoSuchElementException {
      int v = this.iter.previous();
      return v > 255 ? 63 : v;
   }

   public int peekPrevious() throws NoSuchElementException {
      int v = this.iter.peekPrevious();
      return v > 255 ? 63 : v;
   }

   public long getIndex() {
      return this.iter.getIndex();
   }
}
