package cn.hutool.core.lang;

import cn.hutool.core.thread.lock.NoLock;
import java.io.Serializable;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Range<T> implements Iterable<T>, Iterator<T>, Serializable {
   private static final long serialVersionUID = 1L;
   private Lock lock;
   private final T start;
   private final T end;
   private T next;
   private final Stepper<T> stepper;
   private int index;
   private final boolean includeStart;
   private final boolean includeEnd;

   public Range(T start, Stepper<T> stepper) {
      this(start, (Object)null, stepper);
   }

   public Range(T start, T end, Stepper<T> stepper) {
      this(start, end, stepper, true, true);
   }

   public Range(T start, T end, Stepper<T> stepper, boolean isIncludeStart, boolean isIncludeEnd) {
      this.lock = new ReentrantLock();
      this.index = 0;
      Assert.notNull(start, "First element must be not null!");
      this.start = start;
      this.end = end;
      this.stepper = stepper;
      this.next = this.safeStep(this.start);
      this.includeStart = isIncludeStart;
      this.includeEnd = isIncludeEnd;
   }

   public Range<T> disableLock() {
      this.lock = new NoLock();
      return this;
   }

   public boolean hasNext() {
      this.lock.lock();

      boolean var1;
      try {
         if (0 == this.index && this.includeStart) {
            var1 = true;
            return var1;
         }

         if (null == this.next) {
            var1 = false;
            return var1;
         }

         if (this.includeEnd || !this.next.equals(this.end)) {
            return true;
         }

         var1 = false;
      } finally {
         this.lock.unlock();
      }

      return var1;
   }

   public T next() {
      this.lock.lock();

      Object var1;
      try {
         if (!this.hasNext()) {
            throw new NoSuchElementException("Has no next range!");
         }

         var1 = this.nextUncheck();
      } finally {
         this.lock.unlock();
      }

      return var1;
   }

   private T nextUncheck() {
      Object current;
      if (0 == this.index) {
         current = this.start;
         if (!this.includeStart) {
            ++this.index;
            return this.nextUncheck();
         }
      } else {
         current = this.next;
         this.next = this.safeStep(this.next);
      }

      ++this.index;
      return current;
   }

   private T safeStep(T base) {
      int index = this.index;
      T next = null;

      try {
         next = this.stepper.step(base, this.end, index);
      } catch (Exception var5) {
      }

      return next;
   }

   public void remove() {
      throw new UnsupportedOperationException("Can not remove ranged element!");
   }

   public Iterator<T> iterator() {
      return this;
   }

   public Range<T> reset() {
      this.lock.lock();

      try {
         this.index = 0;
         this.next = this.safeStep(this.start);
      } finally {
         this.lock.unlock();
      }

      return this;
   }

   @FunctionalInterface
   public interface Stepper<T> {
      T step(T var1, T var2, int var3);
   }
}
