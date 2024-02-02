package cn.hutool.core.collection;

import java.util.Iterator;
import java.util.NoSuchElementException;

public abstract class ComputeIter<T> implements Iterator<T> {
   private T next;
   private boolean finished;

   protected abstract T computeNext();

   public boolean hasNext() {
      if (null != this.next) {
         return true;
      } else if (this.finished) {
         return false;
      } else {
         T result = this.computeNext();
         if (null == result) {
            this.finished = true;
            return false;
         } else {
            this.next = result;
            return true;
         }
      }
   }

   public T next() {
      if (!this.hasNext()) {
         throw new NoSuchElementException("No more lines");
      } else {
         T result = this.next;
         this.next = null;
         return result;
      }
   }

   public void finish() {
      this.finished = true;
      this.next = null;
   }
}
