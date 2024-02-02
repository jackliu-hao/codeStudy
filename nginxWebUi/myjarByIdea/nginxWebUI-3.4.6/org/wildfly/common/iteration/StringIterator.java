package org.wildfly.common.iteration;

import java.util.NoSuchElementException;

final class StringIterator extends CodePointIterator {
   private final int len;
   private final String string;
   private final int offs;
   private int idx;
   private long offset;

   StringIterator(int len, String string, int offs) {
      this.len = len;
      this.string = string;
      this.offs = offs;
      this.idx = 0;
      this.offset = 0L;
   }

   public boolean hasNext() {
      return this.idx < this.len;
   }

   public boolean hasPrevious() {
      return this.offset > 0L;
   }

   public int next() {
      if (!this.hasNext()) {
         throw new NoSuchElementException();
      } else {
         int var1;
         try {
            ++this.offset;
            var1 = this.string.codePointAt(this.idx + this.offs);
         } finally {
            this.idx = this.string.offsetByCodePoints(this.idx + this.offs, 1) - this.offs;
         }

         return var1;
      }
   }

   public int peekNext() throws NoSuchElementException {
      if (!this.hasNext()) {
         throw new NoSuchElementException();
      } else {
         return this.string.codePointAt(this.idx + this.offs);
      }
   }

   public int previous() {
      if (!this.hasPrevious()) {
         throw new NoSuchElementException();
      } else {
         this.idx = this.string.offsetByCodePoints(this.idx + this.offs, -1) - this.offs;
         --this.offset;
         return this.string.codePointAt(this.idx + this.offs);
      }
   }

   public int peekPrevious() throws NoSuchElementException {
      if (!this.hasPrevious()) {
         throw new NoSuchElementException();
      } else {
         return this.string.codePointBefore(this.idx + this.offs);
      }
   }

   public long getIndex() {
      return this.offset;
   }

   public StringBuilder drainTo(StringBuilder b) {
      StringBuilder var2;
      try {
         var2 = b.append(this.string, this.idx + this.offs, this.offs + this.len);
      } finally {
         this.offset += (long)this.string.codePointCount(this.idx + this.offs, this.offs + this.len);
         this.idx = this.len;
      }

      return var2;
   }

   public String drainToString() {
      String var1;
      try {
         var1 = this.string.substring(this.idx + this.offs, this.offs + this.len);
      } finally {
         this.offset += (long)this.string.codePointCount(this.idx + this.offs, this.offs + this.len);
         this.idx = this.len;
      }

      return var1;
   }
}
