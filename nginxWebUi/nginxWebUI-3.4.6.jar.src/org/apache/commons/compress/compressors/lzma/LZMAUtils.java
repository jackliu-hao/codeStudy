/*     */ package org.apache.commons.compress.compressors.lzma;
/*     */ 
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import org.apache.commons.compress.compressors.FileNameUtil;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class LZMAUtils
/*     */ {
/*     */   private static final FileNameUtil fileNameUtil;
/*  38 */   private static final byte[] HEADER_MAGIC = new byte[] { 93, 0, 0 };
/*     */   private static volatile CachedAvailability cachedLZMAAvailability;
/*     */   
/*     */   enum CachedAvailability
/*     */   {
/*  43 */     DONT_CACHE, CACHED_AVAILABLE, CACHED_UNAVAILABLE;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   static {
/*  49 */     Map<String, String> uncompressSuffix = new HashMap<>();
/*  50 */     uncompressSuffix.put(".lzma", "");
/*  51 */     uncompressSuffix.put("-lzma", "");
/*  52 */     fileNameUtil = new FileNameUtil(uncompressSuffix, ".lzma");
/*  53 */     cachedLZMAAvailability = CachedAvailability.DONT_CACHE;
/*  54 */     setCacheLZMAAvailablity(!OsgiUtils.isRunningInOsgiEnvironment());
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
/*     */ 
/*     */   
/*     */   public static boolean matches(byte[] signature, int length) {
/*  69 */     if (length < HEADER_MAGIC.length) {
/*  70 */       return false;
/*     */     }
/*     */     
/*  73 */     for (int i = 0; i < HEADER_MAGIC.length; i++) {
/*  74 */       if (signature[i] != HEADER_MAGIC[i]) {
/*  75 */         return false;
/*     */       }
/*     */     } 
/*     */     
/*  79 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean isLZMACompressionAvailable() {
/*  88 */     CachedAvailability cachedResult = cachedLZMAAvailability;
/*  89 */     if (cachedResult != CachedAvailability.DONT_CACHE) {
/*  90 */       return (cachedResult == CachedAvailability.CACHED_AVAILABLE);
/*     */     }
/*  92 */     return internalIsLZMACompressionAvailable();
/*     */   }
/*     */   
/*     */   private static boolean internalIsLZMACompressionAvailable() {
/*     */     try {
/*  97 */       LZMACompressorInputStream.matches(null, 0);
/*  98 */       return true;
/*  99 */     } catch (NoClassDefFoundError error) {
/* 100 */       return false;
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
/*     */   public static boolean isCompressedFilename(String fileName) {
/* 112 */     return fileNameUtil.isCompressedFilename(fileName);
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
/*     */   
/*     */   public static String getUncompressedFilename(String fileName) {
/* 126 */     return fileNameUtil.getUncompressedFilename(fileName);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String getCompressedFilename(String fileName) {
/* 137 */     return fileNameUtil.getCompressedFilename(fileName);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void setCacheLZMAAvailablity(boolean doCache) {
/* 147 */     if (!doCache) {
/* 148 */       cachedLZMAAvailability = CachedAvailability.DONT_CACHE;
/* 149 */     } else if (cachedLZMAAvailability == CachedAvailability.DONT_CACHE) {
/* 150 */       boolean hasLzma = internalIsLZMACompressionAvailable();
/* 151 */       cachedLZMAAvailability = hasLzma ? CachedAvailability.CACHED_AVAILABLE : CachedAvailability.CACHED_UNAVAILABLE;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   static CachedAvailability getCachedLZMAAvailability() {
/* 158 */     return cachedLZMAAvailability;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\compressors\lzma\LZMAUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */