/*     */ package org.apache.commons.compress.compressors.zstandard;
/*     */ 
/*     */ import org.apache.commons.compress.utils.OsgiUtils;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ZstdUtils
/*     */ {
/*     */   enum CachedAvailability
/*     */   {
/*  31 */     DONT_CACHE, CACHED_AVAILABLE, CACHED_UNAVAILABLE;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  37 */   private static final byte[] ZSTANDARD_FRAME_MAGIC = new byte[] { 40, -75, 47, -3 };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  44 */   private static final byte[] SKIPPABLE_FRAME_MAGIC = new byte[] { 42, 77, 24 };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  51 */   private static volatile CachedAvailability cachedZstdAvailability = CachedAvailability.DONT_CACHE; static {
/*  52 */     setCacheZstdAvailablity(!OsgiUtils.isRunningInOsgiEnvironment());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean isZstdCompressionAvailable() {
/*  64 */     CachedAvailability cachedResult = cachedZstdAvailability;
/*  65 */     if (cachedResult != CachedAvailability.DONT_CACHE) {
/*  66 */       return (cachedResult == CachedAvailability.CACHED_AVAILABLE);
/*     */     }
/*  68 */     return internalIsZstdCompressionAvailable();
/*     */   }
/*     */   
/*     */   private static boolean internalIsZstdCompressionAvailable() {
/*     */     try {
/*  73 */       Class.forName("com.github.luben.zstd.ZstdInputStream");
/*  74 */       return true;
/*  75 */     } catch (NoClassDefFoundError|Exception error) {
/*  76 */       return false;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void setCacheZstdAvailablity(boolean doCache) {
/*  87 */     if (!doCache) {
/*  88 */       cachedZstdAvailability = CachedAvailability.DONT_CACHE;
/*  89 */     } else if (cachedZstdAvailability == CachedAvailability.DONT_CACHE) {
/*  90 */       boolean hasZstd = internalIsZstdCompressionAvailable();
/*  91 */       cachedZstdAvailability = hasZstd ? CachedAvailability.CACHED_AVAILABLE : CachedAvailability.CACHED_UNAVAILABLE;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean matches(byte[] signature, int length) {
/* 105 */     if (length < ZSTANDARD_FRAME_MAGIC.length) {
/* 106 */       return false;
/*     */     }
/*     */     
/* 109 */     boolean isZstandard = true; int i;
/* 110 */     for (i = 0; i < ZSTANDARD_FRAME_MAGIC.length; i++) {
/* 111 */       if (signature[i] != ZSTANDARD_FRAME_MAGIC[i]) {
/* 112 */         isZstandard = false;
/*     */         break;
/*     */       } 
/*     */     } 
/* 116 */     if (isZstandard) {
/* 117 */       return true;
/*     */     }
/*     */     
/* 120 */     if (80 == (signature[0] & 0xF0)) {
/*     */       
/* 122 */       for (i = 0; i < SKIPPABLE_FRAME_MAGIC.length; i++) {
/* 123 */         if (signature[i + 1] != SKIPPABLE_FRAME_MAGIC[i]) {
/* 124 */           return false;
/*     */         }
/*     */       } 
/*     */       
/* 128 */       return true;
/*     */     } 
/*     */     
/* 131 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   static CachedAvailability getCachedZstdAvailability() {
/* 136 */     return cachedZstdAvailability;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\compressors\zstandard\ZstdUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */