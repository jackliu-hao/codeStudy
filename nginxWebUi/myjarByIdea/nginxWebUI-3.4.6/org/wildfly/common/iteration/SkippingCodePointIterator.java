package org.wildfly.common.iteration;

import java.util.NoSuchElementException;
import java.util.function.IntPredicate;

final class SkippingCodePointIterator extends CodePointIterator {
   private final CodePointIterator iter;
   private final IntPredicate predicate;

   SkippingCodePointIterator(CodePointIterator iter, IntPredicate predicate) {
      this.iter = iter;
      this.predicate = predicate;
   }

   public boolean hasNext() {
      return this.iter.hasNext() && !this.skip(this.peekNext());
   }

   public boolean hasPrevious() {
      return this.iter.hasPrevious() && !this.skip(this.peekPrevious());
   }

   public int next() {
      if (!this.hasNext()) {
         throw new NoSuchElementException();
      } else {
         return this.iter.next();
      }
   }

   public int peekNext() throws NoSuchElementException {
      if (!this.iter.hasNext()) {
         throw new NoSuchElementException();
      } else {
         int next = this.seekNext(this.iter.peekNext());
         return !this.skip(next) ? next : next;
      }
   }

   private int seekNext(int next) throws NoSuchElementException {
      if (!this.iter.hasNext()) {
         return next;
      } else {
         next = this.iter.next();
         if (this.skip(next)) {
            return this.seekNext(next);
         } else {
            this.iter.previous();
            return next;
         }
      }
   }

   public int previous() {
      if (!this.hasPrevious()) {
         throw new NoSuchElementException();
      } else {
         return this.iter.previous();
      }
   }

   public int peekPrevious() throws NoSuchElementException {
      if (!this.iter.hasPrevious()) {
         throw new NoSuchElementException();
      } else {
         int prev = this.seekPrev(this.iter.peekPrevious());
         return !this.skip(prev) ? prev : prev;
      }
   }

   private int seekPrev(int prev) throws NoSuchElementException {
      if (!this.iter.hasPrevious()) {
         return prev;
      } else {
         prev = this.iter.previous();
         if (this.skip(prev)) {
            return this.seekPrev(prev);
         } else {
            this.iter.next();
            return prev;
         }
      }
   }

   public long getIndex() {
      return this.iter.getIndex();
   }

   private boolean skip(int c) {
      return this.predicate.test(c);
   }
}
