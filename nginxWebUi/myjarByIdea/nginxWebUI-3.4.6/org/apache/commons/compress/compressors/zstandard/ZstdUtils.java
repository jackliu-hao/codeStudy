package org.apache.commons.compress.compressors.zstandard;

import org.apache.commons.compress.utils.OsgiUtils;

public class ZstdUtils {
   private static final byte[] ZSTANDARD_FRAME_MAGIC = new byte[]{40, -75, 47, -3};
   private static final byte[] SKIPPABLE_FRAME_MAGIC = new byte[]{42, 77, 24};
   private static volatile CachedAvailability cachedZstdAvailability;

   private ZstdUtils() {
   }

   public static boolean isZstdCompressionAvailable() {
      CachedAvailability cachedResult = cachedZstdAvailability;
      if (cachedResult != ZstdUtils.CachedAvailability.DONT_CACHE) {
         return cachedResult == ZstdUtils.CachedAvailability.CACHED_AVAILABLE;
      } else {
         return internalIsZstdCompressionAvailable();
      }
   }

   private static boolean internalIsZstdCompressionAvailable() {
      try {
         Class.forName("com.github.luben.zstd.ZstdInputStream");
         return true;
      } catch (Exception | NoClassDefFoundError var1) {
         return false;
      }
   }

   public static void setCacheZstdAvailablity(boolean doCache) {
      if (!doCache) {
         cachedZstdAvailability = ZstdUtils.CachedAvailability.DONT_CACHE;
      } else if (cachedZstdAvailability == ZstdUtils.CachedAvailability.DONT_CACHE) {
         boolean hasZstd = internalIsZstdCompressionAvailable();
         cachedZstdAvailability = hasZstd ? ZstdUtils.CachedAvailability.CACHED_AVAILABLE : ZstdUtils.CachedAvailability.CACHED_UNAVAILABLE;
      }

   }

   public static boolean matches(byte[] signature, int length) {
      if (length < ZSTANDARD_FRAME_MAGIC.length) {
         return false;
      } else {
         boolean isZstandard = true;

         int i;
         for(i = 0; i < ZSTANDARD_FRAME_MAGIC.length; ++i) {
            if (signature[i] != ZSTANDARD_FRAME_MAGIC[i]) {
               isZstandard = false;
               break;
            }
         }

         if (isZstandard) {
            return true;
         } else if (80 == (signature[0] & 240)) {
            for(i = 0; i < SKIPPABLE_FRAME_MAGIC.length; ++i) {
               if (signature[i + 1] != SKIPPABLE_FRAME_MAGIC[i]) {
                  return false;
               }
            }

            return true;
         } else {
            return false;
         }
      }
   }

   static CachedAvailability getCachedZstdAvailability() {
      return cachedZstdAvailability;
   }

   static {
      cachedZstdAvailability = ZstdUtils.CachedAvailability.DONT_CACHE;
      setCacheZstdAvailablity(!OsgiUtils.isRunningInOsgiEnvironment());
   }

   static enum CachedAvailability {
      DONT_CACHE,
      CACHED_AVAILABLE,
      CACHED_UNAVAILABLE;
   }
}
