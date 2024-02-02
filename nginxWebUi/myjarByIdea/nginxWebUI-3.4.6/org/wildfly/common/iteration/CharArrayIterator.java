package org.wildfly.common.iteration;

import java.util.NoSuchElementException;

final class CharArrayIterator extends CodePointIterator {
   private final int len;
   private final char[] chars;
   private final int offs;
   private int idx;
   private int offset;

   CharArrayIterator(int len, char[] chars, int offs) {
      this.len = len;
      this.chars = chars;
      this.offs = offs;
      this.idx = 0;
      this.offset = 0;
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
         int var1;
         try {
            ++this.offset;
            var1 = Character.codePointAt(this.chars, this.offs + this.idx);
         } finally {
            this.idx = Character.offsetByCodePoints(this.chars, this.offs, this.len, this.offs + this.idx, 1) - this.offs;
         }

         return var1;
      }
   }

   public int peekNext() throws NoSuchElementException {
      if (!this.hasNext()) {
         throw new NoSuchElementException();
      } else {
         return Character.codePointAt(this.chars, this.offs + this.idx);
      }
   }

   public int previous() {
      if (!this.hasPrevious()) {
         throw new NoSuchElementException();
      } else {
         this.idx = Character.offsetByCodePoints(this.chars, this.offs, this.len, this.offs + this.idx, -1) - this.offs;
         --this.offset;
         return Character.codePointAt(this.chars, this.offs + this.idx);
      }
   }

   public int peekPrevious() throws NoSuchElementException {
      if (!this.hasPrevious()) {
         throw new NoSuchElementException();
      } else {
         return Character.codePointBefore(this.chars, this.offs + this.idx);
      }
   }

   public long getIndex() {
      return (long)this.offset;
   }
}
