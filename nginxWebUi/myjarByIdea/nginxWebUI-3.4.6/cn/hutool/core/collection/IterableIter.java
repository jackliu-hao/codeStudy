package cn.hutool.core.collection;

import java.util.Iterator;

public interface IterableIter<T> extends Iterable<T>, Iterator<T> {
   default Iterator<T> iterator() {
      return this;
   }
}
