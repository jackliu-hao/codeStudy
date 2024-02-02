package io.undertow.server.protocol.http;

import java.util.LinkedHashMap;
import java.util.Map;

public class CacheMap<K, V> extends LinkedHashMap<K, V> {
   static final float DEFAULT_LOAD_FACTOR = 0.75F;
   private static final long serialVersionUID = 1L;
   private int capacity;

   public CacheMap(int capacity) {
      super(capacity, 0.75F, true);
      this.capacity = capacity;
   }

   protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
      return this.size() > this.capacity;
   }
}
