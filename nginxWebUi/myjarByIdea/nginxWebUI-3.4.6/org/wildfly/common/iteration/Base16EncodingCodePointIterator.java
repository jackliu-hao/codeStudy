package org.wildfly.common.iteration;

import java.util.NoSuchElementException;

final class Base16EncodingCodePointIterator extends CodePointIterator {
   private ByteIterator iter;
   private final boolean toUpperCase;
   int b;
   boolean lo;

   Base16EncodingCodePointIterator(ByteIterator iter, boolean toUpperCase) {
      this.iter = iter;
      this.toUpperCase = toUpperCase;
   }

   public boolean hasNext() {
      return this.lo || this.iter.hasNext();
   }

   public boolean hasPrevious() {
      return this.lo || this.iter.hasPrevious();
   }

   private int hex(int i) {
      if (i < 10) {
         return 48 + i;
      } else {
         assert i < 16;

         return (this.toUpperCase ? 65 : 97) + i - 10;
      }
   }

   public int next() throws NoSuchElementException {
      if (!this.hasNext()) {
         throw new NoSuchElementException();
      } else if (this.lo) {
         this.lo = false;
         return this.hex(this.b & 15);
      } else {
         this.b = this.iter.next();
         this.lo = true;
         return this.hex(this.b >> 4);
      }
   }

   public int peekNext() throws NoSuchElementException {
      if (!this.hasNext()) {
         throw new NoSuchElementException();
      } else {
         return this.lo ? this.hex(this.b & 15) : this.hex(this.iter.peekNext() >> 4);
      }
   }

   public int previous() throws NoSuchElementException {
      if (!this.hasPrevious()) {
         throw new NoSuchElementException();
      } else if (this.lo) {
         this.lo = false;
         this.iter.previous();
         return this.hex(this.b >> 4);
      } else {
         this.b = this.iter.peekPrevious();
         this.lo = true;
         return this.hex(this.b & 15);
      }
   }

   public int peekPrevious() throws NoSuchElementException {
      if (!this.hasPrevious()) {
         throw new NoSuchElementException();
      } else {
         return this.lo ? this.hex(this.b >> 4) : this.hex(this.iter.peekPrevious() & 15);
      }
   }

   public long getIndex() {
      return this.iter.getIndex() * 2L + (long)(this.lo ? 1 : 0);
   }
}
