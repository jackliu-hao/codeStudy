/*     */ package org.apache.commons.compress.compressors;
/*     */ 
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.Locale;
/*     */ import java.util.Map;
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
/*     */ public class FileNameUtil
/*     */ {
/*  37 */   private final Map<String, String> compressSuffix = new HashMap<>();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final Map<String, String> uncompressSuffix;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final int longestCompressedSuffix;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final int shortestCompressedSuffix;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final int longestUncompressedSuffix;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final int shortestUncompressedSuffix;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final String defaultExtension;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public FileNameUtil(Map<String, String> uncompressSuffix, String defaultExtension) {
/*  93 */     this.uncompressSuffix = Collections.unmodifiableMap(uncompressSuffix);
/*  94 */     int lc = Integer.MIN_VALUE, sc = Integer.MAX_VALUE;
/*  95 */     int lu = Integer.MIN_VALUE, su = Integer.MAX_VALUE;
/*  96 */     for (Map.Entry<String, String> ent : uncompressSuffix.entrySet()) {
/*  97 */       int cl = ((String)ent.getKey()).length();
/*  98 */       if (cl > lc) {
/*  99 */         lc = cl;
/*     */       }
/* 101 */       if (cl < sc) {
/* 102 */         sc = cl;
/*     */       }
/*     */       
/* 105 */       String u = ent.getValue();
/* 106 */       int ul = u.length();
/* 107 */       if (ul > 0) {
/* 108 */         if (!this.compressSuffix.containsKey(u)) {
/* 109 */           this.compressSuffix.put(u, ent.getKey());
/*     */         }
/* 111 */         if (ul > lu) {
/* 112 */           lu = ul;
/*     */         }
/* 114 */         if (ul < su) {
/* 115 */           su = ul;
/*     */         }
/*     */       } 
/*     */     } 
/* 119 */     this.longestCompressedSuffix = lc;
/* 120 */     this.longestUncompressedSuffix = lu;
/* 121 */     this.shortestCompressedSuffix = sc;
/* 122 */     this.shortestUncompressedSuffix = su;
/* 123 */     this.defaultExtension = defaultExtension;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isCompressedFilename(String fileName) {
/* 134 */     String lower = fileName.toLowerCase(Locale.ENGLISH);
/* 135 */     int n = lower.length();
/* 136 */     int i = this.shortestCompressedSuffix;
/* 137 */     for (; i <= this.longestCompressedSuffix && i < n; i++) {
/* 138 */       if (this.uncompressSuffix.containsKey(lower.substring(n - i))) {
/* 139 */         return true;
/*     */       }
/*     */     } 
/* 142 */     return false;
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
/*     */   public String getUncompressedFilename(String fileName) {
/* 159 */     String lower = fileName.toLowerCase(Locale.ENGLISH);
/* 160 */     int n = lower.length();
/* 161 */     int i = this.shortestCompressedSuffix;
/* 162 */     for (; i <= this.longestCompressedSuffix && i < n; i++) {
/* 163 */       String suffix = this.uncompressSuffix.get(lower.substring(n - i));
/* 164 */       if (suffix != null) {
/* 165 */         return fileName.substring(0, n - i) + suffix;
/*     */       }
/*     */     } 
/* 168 */     return fileName;
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
/*     */   public String getCompressedFilename(String fileName) {
/* 183 */     String lower = fileName.toLowerCase(Locale.ENGLISH);
/* 184 */     int n = lower.length();
/* 185 */     int i = this.shortestUncompressedSuffix;
/* 186 */     for (; i <= this.longestUncompressedSuffix && i < n; i++) {
/* 187 */       String suffix = this.compressSuffix.get(lower.substring(n - i));
/* 188 */       if (suffix != null) {
/* 189 */         return fileName.substring(0, n - i) + suffix;
/*     */       }
/*     */     } 
/*     */     
/* 193 */     return fileName + this.defaultExtension;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\compressors\FileNameUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */