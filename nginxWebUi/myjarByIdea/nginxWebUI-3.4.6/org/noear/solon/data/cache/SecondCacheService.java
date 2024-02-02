package org.noear.solon.data.cache;

public class SecondCacheService implements CacheService {
   private CacheService cache1;
   private CacheService cache2;
   private int bufferSeconds;

   public SecondCacheService(CacheService cache1, CacheService cache2) {
      this(cache1, cache2, 5);
   }

   public SecondCacheService(CacheService cache1, CacheService cache2, int bufferSeconds) {
      this.cache1 = cache1;
      this.cache2 = cache2;
      this.bufferSeconds = bufferSeconds;
   }

   public void store(String key, Object obj, int seconds) {
      this.cache1.store(key, obj, seconds);
      this.cache2.store(key, obj, seconds);
   }

   public Object get(String key) {
      Object temp = this.cache1.get(key);
      if (temp == null) {
         temp = this.cache2.get(key);
         if (this.bufferSeconds > 0 && temp != null) {
            this.cache1.store(key, temp, this.bufferSeconds);
         }
      }

      return temp;
   }

   public void remove(String key) {
      this.cache2.remove(key);
      this.cache1.remove(key);
   }
}
