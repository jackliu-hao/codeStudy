package org.wildfly.common.iteration;

import java.util.NoSuchElementException;
import org.wildfly.common._private.CommonMessages;

final class Base16DecodingByteIterator extends ByteIterator {
   private final CodePointIterator iter;
   private int b;
   private long offset;
   private boolean havePair;

   Base16DecodingByteIterator(CodePointIterator iter) {
      this.iter = iter;
   }

   private int calc(int b0, int b1) {
      int d0 = Character.digit(b0, 16);
      int d1 = Character.digit(b1, 16);
      if (d0 != -1 && d1 != -1) {
         return (d0 << 4 | d1) & 255;
      } else {
         throw CommonMessages.msg.invalidHexCharacter();
      }
   }

   public boolean hasNext() {
      if (this.havePair) {
         return true;
      } else if (!this.iter.hasNext()) {
         return false;
      } else {
         int b0 = this.iter.next();
         if (!this.iter.hasNext()) {
            throw CommonMessages.msg.expectedEvenNumberOfHexCharacters();
         } else {
            int b1 = this.iter.next();
            this.b = this.calc(b0, b1);
            this.havePair = true;
            return true;
         }
      }
   }

   public boolean hasPrevious() {
      return this.offset > 0L;
   }

   public int next() throws NoSuchElementException {
      if (!this.hasNext()) {
         throw new NoSuchElementException();
      } else {
         ++this.offset;
         this.havePair = false;
         return this.b;
      }
   }

   public int peekNext() throws NoSuchElementException {
      if (!this.hasNext()) {
         throw new NoSuchElementException();
      } else {
         return this.b;
      }
   }

   public int previous() throws NoSuchElementException {
      if (!this.hasPrevious()) {
         throw new NoSuchElementException();
      } else {
         int b1 = this.iter.previous();
         int b0 = this.iter.previous();
         this.b = this.calc(b0, b1);
         --this.offset;
         this.havePair = true;
         return this.b;
      }
   }

   public int peekPrevious() throws NoSuchElementException {
      if (!this.hasPrevious()) {
         throw new NoSuchElementException();
      } else {
         int b1 = this.iter.previous();
         int b0 = this.iter.peekPrevious();
         this.iter.next();
         return this.calc(b0, b1);
      }
   }

   public long getIndex() {
      return this.offset;
   }
}
