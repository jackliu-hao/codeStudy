package cn.hutool.core.collection;

import cn.hutool.core.lang.Assert;
import java.util.Iterator;
import java.util.function.Function;

public class TransIter<F, T> implements Iterator<T> {
   private final Iterator<? extends F> backingIterator;
   private final Function<? super F, ? extends T> func;

   public TransIter(Iterator<? extends F> backingIterator, Function<? super F, ? extends T> func) {
      this.backingIterator = (Iterator)Assert.notNull(backingIterator);
      this.func = (Function)Assert.notNull(func);
   }

   public final boolean hasNext() {
      return this.backingIterator.hasNext();
   }

   public final T next() {
      return this.func.apply(this.backingIterator.next());
   }

   public final void remove() {
      this.backingIterator.remove();
   }
}
