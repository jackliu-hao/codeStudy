package cn.hutool.cache.file;

import cn.hutool.cache.Cache;
import cn.hutool.cache.impl.LRUCache;
import java.io.File;

public class LRUFileCache extends AbstractFileCache {
   private static final long serialVersionUID = 1L;

   public LRUFileCache(int capacity) {
      this(capacity, capacity / 2, 0L);
   }

   public LRUFileCache(int capacity, int maxFileSize) {
      this(capacity, maxFileSize, 0L);
   }

   public LRUFileCache(int capacity, int maxFileSize, long timeout) {
      super(capacity, maxFileSize, timeout);
   }

   protected Cache<File, byte[]> initCache() {
      return new LRUCache<File, byte[]>(this.capacity, super.timeout) {
         private static final long serialVersionUID = 1L;

         public boolean isFull() {
            return LRUFileCache.this.usedSize > this.capacity;
         }

         protected void onRemove(File key, byte[] cachedObject) {
            LRUFileCache var10000 = LRUFileCache.this;
            var10000.usedSize -= cachedObject.length;
         }
      };
   }
}
