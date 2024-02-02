package org.wildfly.common.iteration;

import java.util.NoSuchElementException;

final class DelimitedByteIterator extends ByteIterator {
   private final ByteIterator iter;
   private final int[] delims;
   long offset;

   DelimitedByteIterator(ByteIterator iter, int... delims) {
      this.iter = iter;
      this.delims = delims;
      this.offset = 0L;
   }

   public boolean hasNext() {
      return this.iter.hasNext() && !this.isDelim(this.iter.peekNext());
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

   private boolean isDelim(int b) {
      int[] var2 = this.delims;
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         int delim = var2[var4];
         if (delim == b) {
            return true;
         }
      }

      return false;
   }
}
