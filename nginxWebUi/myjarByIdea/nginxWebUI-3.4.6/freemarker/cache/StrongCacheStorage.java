package freemarker.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class StrongCacheStorage implements ConcurrentCacheStorage, CacheStorageWithGetSize {
   private final Map map = new ConcurrentHashMap();

   public boolean isConcurrent() {
      return true;
   }

   public Object get(Object key) {
      return this.map.get(key);
   }

   public void put(Object key, Object value) {
      this.map.put(key, value);
   }

   public void remove(Object key) {
      this.map.remove(key);
   }

   public int getSize() {
      return this.map.size();
   }

   public void clear() {
      this.map.clear();
   }
}
