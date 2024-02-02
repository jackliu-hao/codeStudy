package org.wildfly.common.cpu;

public final class CacheLevelInfo {
   private final int cacheLevel;
   private final CacheType cacheType;
   private final int cacheLevelSizeKB;
   private final int cacheLineSize;

   CacheLevelInfo(int cacheLevel, CacheType cacheType, int cacheLevelSizeKB, int cacheLineSize) {
      this.cacheLevel = cacheLevel;
      this.cacheType = cacheType;
      this.cacheLevelSizeKB = cacheLevelSizeKB;
      this.cacheLineSize = cacheLineSize;
   }

   public int getCacheLevel() {
      return this.cacheLevel;
   }

   public CacheType getCacheType() {
      return this.cacheType;
   }

   public int getCacheLevelSizeKB() {
      return this.cacheLevelSizeKB;
   }

   public int getCacheLineSize() {
      return this.cacheLineSize;
   }
}
