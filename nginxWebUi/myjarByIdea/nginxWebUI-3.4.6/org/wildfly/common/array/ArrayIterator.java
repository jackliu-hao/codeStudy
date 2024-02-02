package org.wildfly.common.array;

import java.util.ListIterator;
import java.util.NoSuchElementException;
import org.wildfly.common.Assert;
import org.wildfly.common.iteration.EnumerationIterator;

public final class ArrayIterator<E> implements ListIterator<E>, EnumerationIterator<E> {
   private final E[] elements;
   private final boolean descending;
   private int idx;

   public ArrayIterator(E[] elements) {
      this(elements, false);
   }

   public ArrayIterator(E[] elements, boolean descending) {
      this(elements, descending, descending ? elements.length : 0);
   }

   public ArrayIterator(E[] elements, int startIdx) {
      this(elements, false, startIdx);
   }

   public ArrayIterator(E[] elements, boolean descending, int startIdx) {
      Assert.checkNotNullParam("elements", elements);
      Assert.checkMinimumParameter("startIdx", 0, startIdx);
      Assert.checkMaximumParameter("startIdx", elements.length, startIdx);
      this.elements = elements;
      this.descending = descending;
      this.idx = startIdx;
   }

   public boolean hasPrevious() {
      return this.descending ? this.idx < this.elements.length : this.idx > 0;
   }

   public boolean hasNext() {
      return this.descending ? this.idx > 0 : this.idx < this.elements.length;
   }

   public E previous() {
      if (!this.hasPrevious()) {
         throw new NoSuchElementException();
      } else {
         Object[] var10000 = this.elements;
         int var10001;
         if (this.descending) {
            int var10003 = this.idx;
            var10001 = var10003;
            this.idx = var10003 + 1;
         } else {
            var10001 = --this.idx;
         }

         return var10000[var10001];
      }
   }

   public E next() {
      if (!this.hasNext()) {
         throw new NoSuchElementException();
      } else {
         Object[] var10000 = this.elements;
         int var10001;
         if (this.descending) {
            var10001 = --this.idx;
         } else {
            int var10003 = this.idx;
            var10001 = var10003;
            this.idx = var10003 + 1;
         }

         return var10000[var10001];
      }
   }

   public int nextIndex() {
      return this.descending ? this.idx - 1 : this.idx;
   }

   public int previousIndex() {
      return this.descending ? this.idx : this.idx - 1;
   }

   public void remove() {
      throw Assert.unsupported();
   }

   public void set(E e) {
      throw Assert.unsupported();
   }

   public void add(E e) {
      throw Assert.unsupported();
   }
}
