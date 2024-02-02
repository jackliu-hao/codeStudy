package org.wildfly.common.iteration;

import java.util.NoSuchElementException;

final class InterleavedByteArrayIterator extends ByteIterator {
   private final int len;
   private final byte[] bytes;
   private final int offs;
   private final int[] interleave;
   private int idx;

   InterleavedByteArrayIterator(int len, byte[] bytes, int offs, int[] interleave) {
      this.len = len;
      this.bytes = bytes;
      this.offs = offs;
      this.interleave = interleave;
      this.idx = 0;
   }

   public boolean hasNext() {
      return this.idx < this.len;
   }

   public boolean hasPrevious() {
      return this.idx > 0;
   }

   public int next() {
      if (!this.hasNext()) {
         throw new NoSuchElementException();
      } else {
         return this.bytes[this.offs + this.interleave[this.idx++]] & 255;
      }
   }

   public int previous() {
      if (!this.hasPrevious()) {
         throw new NoSuchElementException();
      } else {
         return this.bytes[this.offs + this.interleave[--this.idx]] & 255;
      }
   }

   public int peekNext() throws NoSuchElementException {
      if (!this.hasNext()) {
         throw new NoSuchElementException();
      } else {
         return this.bytes[this.offs + this.interleave[this.idx]] & 255;
      }
   }

   public int peekPrevious() throws NoSuchElementException {
      if (!this.hasPrevious()) {
         throw new NoSuchElementException();
      } else {
         return this.bytes[this.offs + this.interleave[this.idx - 1]] & 255;
      }
   }

   public long getIndex() {
      return (long)this.idx;
   }
}
