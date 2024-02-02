package org.wildfly.common.iteration;

import java.util.NoSuchElementException;

final class LimitedByteIterator extends ByteIterator {
   private final ByteIterator iter;
   private final long size;
   long offset;

   LimitedByteIterator(ByteIterator iter, long size) {
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
      if (this.offset == this.size) {
         throw new NoSuchElementException();
      } else {
         ++this.offset;
         return this.iter.next();
      }
   }

   public int peekNext() throws NoSuchElementException {
      if (this.offset == this.size) {
         throw new NoSuchElementException();
      } else {
         return this.iter.peekNext();
      }
   }

   public int previous() {
      if (this.offset == 0L) {
         throw new NoSuchElementException();
      } else {
         --this.offset;
         return this.iter.previous();
      }
   }

   public int peekPrevious() throws NoSuchElementException {
      if (this.offset == 0L) {
         throw new NoSuchElementException();
      } else {
         return this.iter.peekPrevious();
      }
   }

   public int drain(byte[] dst, int offs, int len) {
      return super.drain(dst, offs, (int)Math.min((long)len, this.size - this.offset));
   }

   public long getIndex() {
      return this.offset;
   }
}
