package cn.hutool.core.collection;

import cn.hutool.core.lang.Chain;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

public class IterChain<T> implements Iterator<T>, Chain<Iterator<T>, IterChain<T>> {
   protected final List<Iterator<T>> allIterators = new ArrayList();
   protected int currentIter = -1;

   public IterChain() {
   }

   @SafeVarargs
   public IterChain(Iterator<T>... iterators) {
      Iterator[] var2 = iterators;
      int var3 = iterators.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Iterator<T> iterator = var2[var4];
         this.addChain(iterator);
      }

   }

   public IterChain<T> addChain(Iterator<T> iterator) {
      if (this.allIterators.contains(iterator)) {
         throw new IllegalArgumentException("Duplicate iterator");
      } else {
         this.allIterators.add(iterator);
         return this;
      }
   }

   public boolean hasNext() {
      if (this.currentIter == -1) {
         this.currentIter = 0;
      }

      int size = this.allIterators.size();

      for(int i = this.currentIter; i < size; ++i) {
         Iterator<T> iterator = (Iterator)this.allIterators.get(i);
         if (iterator.hasNext()) {
            this.currentIter = i;
            return true;
         }
      }

      return false;
   }

   public T next() {
      if (!this.hasNext()) {
         throw new NoSuchElementException();
      } else {
         return ((Iterator)this.allIterators.get(this.currentIter)).next();
      }
   }

   public void remove() {
      if (-1 == this.currentIter) {
         throw new IllegalStateException("next() has not yet been called");
      } else {
         ((Iterator)this.allIterators.get(this.currentIter)).remove();
      }
   }

   public Iterator<Iterator<T>> iterator() {
      return this.allIterators.iterator();
   }
}
