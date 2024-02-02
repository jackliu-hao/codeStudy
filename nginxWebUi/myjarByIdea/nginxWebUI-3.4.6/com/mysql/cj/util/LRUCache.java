package com.mysql.cj.util;

import java.util.LinkedHashMap;
import java.util.Map;

public class LRUCache<K, V> extends LinkedHashMap<K, V> {
   private static final long serialVersionUID = 1L;
   protected int maxElements;

   public LRUCache(int maxSize) {
      super(maxSize, 0.75F, true);
      this.maxElements = maxSize;
   }

   protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
      return this.size() > this.maxElements;
   }
}
