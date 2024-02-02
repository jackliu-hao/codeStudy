/*     */ package org.antlr.v4.runtime.misc;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class MurmurHash
/*     */ {
/*     */   private static final int DEFAULT_SEED = 0;
/*     */   
/*     */   public static int initialize() {
/*  47 */     return initialize(0);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int initialize(int seed) {
/*  57 */     return seed;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int update(int hash, int value) {
/*  68 */     int c1 = -862048943;
/*  69 */     int c2 = 461845907;
/*  70 */     int r1 = 15;
/*  71 */     int r2 = 13;
/*  72 */     int m = 5;
/*  73 */     int n = -430675100;
/*     */     
/*  75 */     int k = value;
/*  76 */     k *= -862048943;
/*  77 */     k = k << 15 | k >>> 17;
/*  78 */     k *= 461845907;
/*     */     
/*  80 */     hash ^= k;
/*  81 */     hash = hash << 13 | hash >>> 19;
/*  82 */     hash = hash * 5 + -430675100;
/*     */     
/*  84 */     return hash;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int update(int hash, Object value) {
/*  95 */     return update(hash, (value != null) ? value.hashCode() : 0);
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
/*     */   public static int finish(int hash, int numberOfWords) {
/* 107 */     hash ^= numberOfWords * 4;
/* 108 */     hash ^= hash >>> 16;
/* 109 */     hash *= -2048144789;
/* 110 */     hash ^= hash >>> 13;
/* 111 */     hash *= -1028477387;
/* 112 */     hash ^= hash >>> 16;
/* 113 */     return hash;
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
/*     */   public static <T> int hashCode(T[] data, int seed) {
/* 126 */     int hash = initialize(seed);
/* 127 */     for (T value : data) {
/* 128 */       hash = update(hash, value);
/*     */     }
/*     */     
/* 131 */     hash = finish(hash, data.length);
/* 132 */     return hash;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\antlr\v4\runtime\misc\MurmurHash.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */