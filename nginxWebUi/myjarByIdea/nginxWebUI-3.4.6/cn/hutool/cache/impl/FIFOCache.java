package cn.hutool.cache.impl;

import java.util.Iterator;
import java.util.LinkedHashMap;

public class FIFOCache<K, V> extends StampedCache<K, V> {
   private static final long serialVersionUID = 1L;

   public FIFOCache(int capacity) {
      this(capacity, 0L);
   }

   public FIFOCache(int capacity, long timeout) {
      this.capacity = capacity;
      this.timeout = timeout;
      this.cacheMap = new LinkedHashMap(capacity + 1, 1.0F, false);
   }

   protected int pruneCache() {
      int count = 0;
      CacheObj<K, V> first = null;
      Iterator<CacheObj<K, V>> values = this.cacheObjIter();
      if (this.isPruneExpiredActive()) {
         while(values.hasNext()) {
            CacheObj<K, V> co = (CacheObj)values.next();
            if (co.isExpired()) {
               values.remove();
               this.onRemove(co.key, co.obj);
               ++count;
            } else if (first == null) {
               first = co;
            }
         }
      } else {
         first = values.hasNext() ? (CacheObj)values.next() : null;
      }

      if (this.isFull() && null != first) {
         this.removeWithoutLock(first.key, false);
         this.onRemove(first.key, first.obj);
         ++count;
      }

      return count;
   }
}
