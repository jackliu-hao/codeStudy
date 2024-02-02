package freemarker.cache;

public class NullCacheStorage implements ConcurrentCacheStorage, CacheStorageWithGetSize {
   public static final NullCacheStorage INSTANCE = new NullCacheStorage();

   public boolean isConcurrent() {
      return true;
   }

   public Object get(Object key) {
      return null;
   }

   public void put(Object key, Object value) {
   }

   public void remove(Object key) {
   }

   public void clear() {
   }

   public int getSize() {
      return 0;
   }
}
