package cn.hutool.cache.impl;

import cn.hutool.core.map.FixedLinkedHashMap;
import java.util.Iterator;

public class LRUCache<K, V> extends ReentrantCache<K, V> {
   private static final long serialVersionUID = 1L;

   public LRUCache(int capacity) {
      this(capacity, 0L);
   }

   public LRUCache(int capacity, long timeout) {
      if (Integer.MAX_VALUE == capacity) {
         --capacity;
      }

      this.capacity = capacity;
      this.timeout = timeout;
      this.cacheMap = new FixedLinkedHashMap(capacity);
   }

   protected int pruneCache() {
      if (!this.isPruneExpiredActive()) {
         return 0;
      } else {
         int count = 0;
         Iterator<CacheObj<K, V>> values = this.cacheObjIter();

         while(values.hasNext()) {
            CacheObj<K, V> co = (CacheObj)values.next();
            if (co.isExpired()) {
               values.remove();
               this.onRemove(co.key, co.obj);
               ++count;
            }
         }

         return count;
      }
   }
}
