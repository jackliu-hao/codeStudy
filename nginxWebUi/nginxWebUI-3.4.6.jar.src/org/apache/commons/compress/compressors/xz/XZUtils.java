/*     */ package org.apache.commons.compress.compressors.xz;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ public class XZUtils
/*     */ {
/*     */   private static final FileNameUtil fileNameUtil;
/*  41 */   private static final byte[] HEADER_MAGIC = new byte[] { -3, 55, 122, 88, 90, 0 };
/*     */   private static volatile CachedAvailability cachedXZAvailability;
/*     */   
/*     */   enum CachedAvailability
/*     */   {
/*  46 */     DONT_CACHE, CACHED_AVAILABLE, CACHED_UNAVAILABLE;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   static {
/*  52 */     Map<String, String> uncompressSuffix = new HashMap<>();
/*  53 */     uncompressSuffix.put(".txz", ".tar");
/*  54 */     uncompressSuffix.put(".xz", "");
/*  55 */     uncompressSuffix.put("-xz", "");
/*  56 */     fileNameUtil = new FileNameUtil(uncompressSuffix, ".xz");
/*  57 */     cachedXZAvailability = CachedAvailability.DONT_CACHE;
/*  58 */     setCacheXZAvailablity(!OsgiUtils.isRunningInOsgiEnvironment());
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean matches(byte[] signature, int length) {
/*  78 */     if (length < HEADER_MAGIC.length) {
/*  79 */       return false;
/*     */     }
/*     */     
/*  82 */     for (int i = 0; i < HEADER_MAGIC.length; i++) {
/*  83 */       if (signature[i] != HEADER_MAGIC[i]) {
/*  84 */         return false;
/*     */       }
/*     */     } 
/*     */     
/*  88 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean isXZCompressionAvailable() {
/*  97 */     CachedAvailability cachedResult = cachedXZAvailability;
/*  98 */     if (cachedResult != CachedAvailability.DONT_CACHE) {
/*  99 */       return (cachedResult == CachedAvailability.CACHED_AVAILABLE);
/*     */     }
/* 101 */     return internalIsXZCompressionAvailable();
/*     */   }
/*     */   
/*     */   private static boolean internalIsXZCompressionAvailable() {
/*     */     try {
/* 106 */       XZCompressorInputStream.matches(null, 0);
/* 107 */       return true;
/* 108 */     } catch (NoClassDefFoundError error) {
/* 109 */       return false;
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
/* 121 */     return fileNameUtil.isCompressedFilename(fileName);
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
/*     */ 
/*     */   
/*     */   public static String getUncompressedFilename(String fileName) {
/* 138 */     return fileNameUtil.getUncompressedFilename(fileName);
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
/*     */   public static String getCompressedFilename(String fileName) {
/* 153 */     return fileNameUtil.getCompressedFilename(fileName);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void setCacheXZAvailablity(boolean doCache) {
/* 164 */     if (!doCache) {
/* 165 */       cachedXZAvailability = CachedAvailability.DONT_CACHE;
/* 166 */     } else if (cachedXZAvailability == CachedAvailability.DONT_CACHE) {
/* 167 */       boolean hasXz = internalIsXZCompressionAvailable();
/* 168 */       cachedXZAvailability = hasXz ? CachedAvailability.CACHED_AVAILABLE : CachedAvailability.CACHED_UNAVAILABLE;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   static CachedAvailability getCachedXZAvailability() {
/* 175 */     return cachedXZAvailability;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\compressors\xz\XZUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */