/*     */ package cn.hutool.core.lang.hash;
/*     */ 
/*     */ import cn.hutool.core.util.ByteUtil;
/*     */ import cn.hutool.core.util.CharsetUtil;
/*     */ import cn.hutool.core.util.StrUtil;
/*     */ import java.io.Serializable;
/*     */ import java.nio.ByteOrder;
/*     */ import java.nio.charset.Charset;
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
/*     */ public class MurmurHash
/*     */   implements Serializable
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   private static final int C1_32 = -862048943;
/*     */   private static final int C2_32 = 461845907;
/*     */   private static final int R1_32 = 15;
/*     */   private static final int R2_32 = 13;
/*     */   private static final int M_32 = 5;
/*     */   private static final int N_32 = -430675100;
/*     */   private static final long C1 = -8663945395140668459L;
/*     */   private static final long C2 = 5545529020109919103L;
/*     */   private static final int R1 = 31;
/*     */   private static final int R2 = 27;
/*     */   private static final int R3 = 33;
/*     */   private static final int M = 5;
/*     */   private static final int N1 = 1390208809;
/*     */   private static final int N2 = 944331445;
/*     */   private static final int DEFAULT_SEED = 0;
/*  45 */   private static final Charset DEFAULT_CHARSET = CharsetUtil.CHARSET_UTF_8;
/*  46 */   private static final ByteOrder DEFAULT_ORDER = ByteOrder.LITTLE_ENDIAN;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int hash32(CharSequence data) {
/*  55 */     return hash32(StrUtil.bytes(data, DEFAULT_CHARSET));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int hash32(byte[] data) {
/*  65 */     return hash32(data, data.length, 0);
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
/*     */   public static int hash32(byte[] data, int length, int seed) {
/*  77 */     int hash = seed;
/*  78 */     int nblocks = length >> 2;
/*     */ 
/*     */     
/*  81 */     for (int i = 0; i < nblocks; i++) {
/*  82 */       int i4 = i << 2;
/*  83 */       int k = ByteUtil.bytesToInt(data, i4, DEFAULT_ORDER);
/*     */ 
/*     */       
/*  86 */       k *= -862048943;
/*  87 */       k = Integer.rotateLeft(k, 15);
/*  88 */       k *= 461845907;
/*  89 */       hash ^= k;
/*  90 */       hash = Integer.rotateLeft(hash, 13) * 5 + -430675100;
/*     */     } 
/*     */ 
/*     */     
/*  94 */     int idx = nblocks << 2;
/*  95 */     int k1 = 0;
/*  96 */     switch (length - idx) {
/*     */       case 3:
/*  98 */         k1 ^= data[idx + 2] << 16;
/*     */       case 2:
/* 100 */         k1 ^= data[idx + 1] << 8;
/*     */       case 1:
/* 102 */         k1 ^= data[idx];
/*     */ 
/*     */         
/* 105 */         k1 *= -862048943;
/* 106 */         k1 = Integer.rotateLeft(k1, 15);
/* 107 */         k1 *= 461845907;
/* 108 */         hash ^= k1;
/*     */         break;
/*     */     } 
/*     */     
/* 112 */     hash ^= length;
/* 113 */     hash ^= hash >>> 16;
/* 114 */     hash *= -2048144789;
/* 115 */     hash ^= hash >>> 13;
/* 116 */     hash *= -1028477387;
/* 117 */     hash ^= hash >>> 16;
/*     */     
/* 119 */     return hash;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static long hash64(CharSequence data) {
/* 129 */     return hash64(StrUtil.bytes(data, DEFAULT_CHARSET));
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
/*     */   public static long hash64(byte[] data) {
/* 141 */     return hash64(data, data.length, 0);
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
/*     */   public static long hash64(byte[] data, int length, int seed) {
/* 154 */     long hash = seed;
/* 155 */     int nblocks = length >> 3;
/*     */ 
/*     */     
/* 158 */     for (int i = 0; i < nblocks; i++) {
/* 159 */       int i8 = i << 3;
/* 160 */       long k = ByteUtil.bytesToLong(data, i8, DEFAULT_ORDER);
/*     */ 
/*     */       
/* 163 */       k *= -8663945395140668459L;
/* 164 */       k = Long.rotateLeft(k, 31);
/* 165 */       k *= 5545529020109919103L;
/* 166 */       hash ^= k;
/* 167 */       hash = Long.rotateLeft(hash, 27) * 5L + 1390208809L;
/*     */     } 
/*     */ 
/*     */     
/* 171 */     long k1 = 0L;
/* 172 */     int tailStart = nblocks << 3;
/* 173 */     switch (length - tailStart) {
/*     */       case 7:
/* 175 */         k1 ^= (data[tailStart + 6] & 0xFFL) << 48L;
/*     */       case 6:
/* 177 */         k1 ^= (data[tailStart + 5] & 0xFFL) << 40L;
/*     */       case 5:
/* 179 */         k1 ^= (data[tailStart + 4] & 0xFFL) << 32L;
/*     */       case 4:
/* 181 */         k1 ^= (data[tailStart + 3] & 0xFFL) << 24L;
/*     */       case 3:
/* 183 */         k1 ^= (data[tailStart + 2] & 0xFFL) << 16L;
/*     */       case 2:
/* 185 */         k1 ^= (data[tailStart + 1] & 0xFFL) << 8L;
/*     */       case 1:
/* 187 */         k1 ^= data[tailStart] & 0xFFL;
/* 188 */         k1 *= -8663945395140668459L;
/* 189 */         k1 = Long.rotateLeft(k1, 31);
/* 190 */         k1 *= 5545529020109919103L;
/* 191 */         hash ^= k1;
/*     */         break;
/*     */     } 
/*     */     
/* 195 */     hash ^= length;
/* 196 */     hash = fmix64(hash);
/*     */     
/* 198 */     return hash;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static long[] hash128(CharSequence data) {
/* 208 */     return hash128(StrUtil.bytes(data, DEFAULT_CHARSET));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static long[] hash128(byte[] data) {
/* 218 */     return hash128(data, data.length, 0);
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
/*     */   public static long[] hash128(byte[] data, int length, int seed) {
/* 230 */     long h1 = seed;
/* 231 */     long h2 = seed;
/* 232 */     int nblocks = length >> 4;
/*     */ 
/*     */     
/* 235 */     for (int i = 0; i < nblocks; i++) {
/* 236 */       int i16 = i << 4;
/* 237 */       long l1 = ByteUtil.bytesToLong(data, i16, DEFAULT_ORDER);
/* 238 */       long l2 = ByteUtil.bytesToLong(data, i16 + 8, DEFAULT_ORDER);
/*     */ 
/*     */       
/* 241 */       l1 *= -8663945395140668459L;
/* 242 */       l1 = Long.rotateLeft(l1, 31);
/* 243 */       l1 *= 5545529020109919103L;
/* 244 */       h1 ^= l1;
/* 245 */       h1 = Long.rotateLeft(h1, 27);
/* 246 */       h1 += h2;
/* 247 */       h1 = h1 * 5L + 1390208809L;
/*     */ 
/*     */       
/* 250 */       l2 *= 5545529020109919103L;
/* 251 */       l2 = Long.rotateLeft(l2, 33);
/* 252 */       l2 *= -8663945395140668459L;
/* 253 */       h2 ^= l2;
/* 254 */       h2 = Long.rotateLeft(h2, 31);
/* 255 */       h2 += h1;
/* 256 */       h2 = h2 * 5L + 944331445L;
/*     */     } 
/*     */ 
/*     */     
/* 260 */     long k1 = 0L;
/* 261 */     long k2 = 0L;
/* 262 */     int tailStart = nblocks << 4;
/* 263 */     switch (length - tailStart) {
/*     */       case 15:
/* 265 */         k2 ^= (data[tailStart + 14] & 0xFF) << 48L;
/*     */       case 14:
/* 267 */         k2 ^= (data[tailStart + 13] & 0xFF) << 40L;
/*     */       case 13:
/* 269 */         k2 ^= (data[tailStart + 12] & 0xFF) << 32L;
/*     */       case 12:
/* 271 */         k2 ^= (data[tailStart + 11] & 0xFF) << 24L;
/*     */       case 11:
/* 273 */         k2 ^= (data[tailStart + 10] & 0xFF) << 16L;
/*     */       case 10:
/* 275 */         k2 ^= (data[tailStart + 9] & 0xFF) << 8L;
/*     */       case 9:
/* 277 */         k2 ^= (data[tailStart + 8] & 0xFF);
/* 278 */         k2 *= 5545529020109919103L;
/* 279 */         k2 = Long.rotateLeft(k2, 33);
/* 280 */         k2 *= -8663945395140668459L;
/* 281 */         h2 ^= k2;
/*     */       
/*     */       case 8:
/* 284 */         k1 ^= (data[tailStart + 7] & 0xFF) << 56L;
/*     */       case 7:
/* 286 */         k1 ^= (data[tailStart + 6] & 0xFF) << 48L;
/*     */       case 6:
/* 288 */         k1 ^= (data[tailStart + 5] & 0xFF) << 40L;
/*     */       case 5:
/* 290 */         k1 ^= (data[tailStart + 4] & 0xFF) << 32L;
/*     */       case 4:
/* 292 */         k1 ^= (data[tailStart + 3] & 0xFF) << 24L;
/*     */       case 3:
/* 294 */         k1 ^= (data[tailStart + 2] & 0xFF) << 16L;
/*     */       case 2:
/* 296 */         k1 ^= (data[tailStart + 1] & 0xFF) << 8L;
/*     */       case 1:
/* 298 */         k1 ^= (data[tailStart] & 0xFF);
/* 299 */         k1 *= -8663945395140668459L;
/* 300 */         k1 = Long.rotateLeft(k1, 31);
/* 301 */         k1 *= 5545529020109919103L;
/* 302 */         h1 ^= k1;
/*     */         break;
/*     */     } 
/*     */     
/* 306 */     h1 ^= length;
/* 307 */     h2 ^= length;
/*     */     
/* 309 */     h1 += h2;
/* 310 */     h2 += h1;
/*     */     
/* 312 */     h1 = fmix64(h1);
/* 313 */     h2 = fmix64(h2);
/*     */     
/* 315 */     h1 += h2;
/* 316 */     h2 += h1;
/*     */     
/* 318 */     return new long[] { h1, h2 };
/*     */   }
/*     */   
/*     */   private static long fmix64(long h) {
/* 322 */     h ^= h >>> 33L;
/* 323 */     h *= -49064778989728563L;
/* 324 */     h ^= h >>> 33L;
/* 325 */     h *= -4265267296055464877L;
/* 326 */     h ^= h >>> 33L;
/* 327 */     return h;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\lang\hash\MurmurHash.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */