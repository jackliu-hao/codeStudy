package cn.hutool.cache.impl;

import java.io.Serializable;
import java.util.Iterator;

public class CacheValuesIterator<V> implements Iterator<V>, Serializable {
   private static final long serialVersionUID = 1L;
   private final CacheObjIterator<?, V> cacheObjIter;

   CacheValuesIterator(CacheObjIterator<?, V> iterator) {
      this.cacheObjIter = iterator;
   }

   public boolean hasNext() {
      return this.cacheObjIter.hasNext();
   }

   public V next() {
      return this.cacheObjIter.next().getValue();
   }

   public void remove() {
      this.cacheObjIter.remove();
   }
}
