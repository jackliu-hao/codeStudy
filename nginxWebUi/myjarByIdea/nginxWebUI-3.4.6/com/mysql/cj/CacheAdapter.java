package com.mysql.cj;

import java.util.Set;

public interface CacheAdapter<K, V> {
   V get(K var1);

   void put(K var1, V var2);

   void invalidate(K var1);

   void invalidateAll(Set<K> var1);

   void invalidateAll();
}
