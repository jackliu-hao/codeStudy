package cn.hutool.cache.file;

import cn.hutool.cache.Cache;
import cn.hutool.cache.impl.LFUCache;
import java.io.File;

public class LFUFileCache extends AbstractFileCache {
   private static final long serialVersionUID = 1L;

   public LFUFileCache(int capacity) {
      this(capacity, capacity / 2, 0L);
   }

   public LFUFileCache(int capacity, int maxFileSize) {
      this(capacity, maxFileSize, 0L);
   }

   public LFUFileCache(int capacity, int maxFileSize, long timeout) {
      super(capacity, maxFileSize, timeout);
   }

   protected Cache<File, byte[]> initCache() {
      return new LFUCache<File, byte[]>(this.capacity, this.timeout) {
         private static final long serialVersionUID = 1L;

         public boolean isFull() {
            return LFUFileCache.this.usedSize > this.capacity;
         }

         protected void onRemove(File key, byte[] cachedObject) {
            LFUFileCache var10000 = LFUFileCache.this;
            var10000.usedSize -= cachedObject.length;
         }
      };
   }
}
