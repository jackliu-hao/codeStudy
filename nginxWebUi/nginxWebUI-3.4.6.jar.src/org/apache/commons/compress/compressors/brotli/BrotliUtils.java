/*    */ package org.apache.commons.compress.compressors.brotli;
/*    */ 
/*    */ import org.apache.commons.compress.utils.OsgiUtils;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class BrotliUtils
/*    */ {
/*    */   enum CachedAvailability
/*    */   {
/* 31 */     DONT_CACHE, CACHED_AVAILABLE, CACHED_UNAVAILABLE;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/* 37 */   private static volatile CachedAvailability cachedBrotliAvailability = CachedAvailability.DONT_CACHE; static {
/* 38 */     setCacheBrotliAvailablity(!OsgiUtils.isRunningInOsgiEnvironment());
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static boolean isBrotliCompressionAvailable() {
/* 51 */     CachedAvailability cachedResult = cachedBrotliAvailability;
/* 52 */     if (cachedResult != CachedAvailability.DONT_CACHE) {
/* 53 */       return (cachedResult == CachedAvailability.CACHED_AVAILABLE);
/*    */     }
/* 55 */     return internalIsBrotliCompressionAvailable();
/*    */   }
/*    */   
/*    */   private static boolean internalIsBrotliCompressionAvailable() {
/*    */     try {
/* 60 */       Class.forName("org.brotli.dec.BrotliInputStream");
/* 61 */       return true;
/* 62 */     } catch (NoClassDefFoundError|Exception error) {
/* 63 */       return false;
/*    */     } 
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static void setCacheBrotliAvailablity(boolean doCache) {
/* 74 */     if (!doCache) {
/* 75 */       cachedBrotliAvailability = CachedAvailability.DONT_CACHE;
/* 76 */     } else if (cachedBrotliAvailability == CachedAvailability.DONT_CACHE) {
/* 77 */       boolean hasBrotli = internalIsBrotliCompressionAvailable();
/* 78 */       cachedBrotliAvailability = hasBrotli ? CachedAvailability.CACHED_AVAILABLE : CachedAvailability.CACHED_UNAVAILABLE;
/*    */     } 
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   static CachedAvailability getCachedBrotliAvailability() {
/* 85 */     return cachedBrotliAvailability;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\compressors\brotli\BrotliUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */