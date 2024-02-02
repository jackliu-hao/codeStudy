package org.noear.solon.data.cache;

import java.util.function.Supplier;

public interface CacheService {
   void store(String key, Object obj, int seconds);

   void remove(String key);

   Object get(String key);

   default <T> T getOrStore(String key, int seconds, Supplier supplier) {
      Object obj = this.get(key);
      if (obj == null) {
         obj = supplier.get();
         this.store(key, obj, seconds);
      }

      return obj;
   }
}
