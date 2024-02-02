package org.h2.util;

import java.util.LinkedHashMap;
import java.util.Map;

public class SmallLRUCache<K, V> extends LinkedHashMap<K, V> {
   private static final long serialVersionUID = 1L;
   private int size;

   private SmallLRUCache(int var1) {
      super(var1, 0.75F, true);
      this.size = var1;
   }

   public static <K, V> SmallLRUCache<K, V> newInstance(int var0) {
      return new SmallLRUCache(var0);
   }

   public void setMaxSize(int var1) {
      this.size = var1;
   }

   protected boolean removeEldestEntry(Map.Entry<K, V> var1) {
      return this.size() > this.size;
   }
}
