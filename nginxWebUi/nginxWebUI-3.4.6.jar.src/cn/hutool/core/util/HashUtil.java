/*     */ package cn.hutool.core.util;
/*     */ 
/*     */ import cn.hutool.core.lang.hash.CityHash;
/*     */ import cn.hutool.core.lang.hash.MetroHash;
/*     */ import cn.hutool.core.lang.hash.MurmurHash;
/*     */ import cn.hutool.core.lang.hash.Number128;
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
/*     */ public class HashUtil
/*     */ {
/*     */   public static int additiveHash(String key, int prime) {
/*     */     int hash;
/*     */     int i;
/*  25 */     for (hash = key.length(), i = 0; i < key.length(); i++) {
/*  26 */       hash += key.charAt(i);
/*     */     }
/*  28 */     return hash % prime;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int rotatingHash(String key, int prime) {
/*     */     int hash;
/*     */     int i;
/*  40 */     for (hash = key.length(), i = 0; i < key.length(); i++) {
/*  41 */       hash = hash << 4 ^ hash >> 28 ^ key.charAt(i);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  47 */     return hash % prime;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int oneByOneHash(String key) {
/*     */     int hash;
/*     */     int i;
/*  58 */     for (hash = 0, i = 0; i < key.length(); i++) {
/*  59 */       hash += key.charAt(i);
/*  60 */       hash += hash << 10;
/*  61 */       hash ^= hash >> 6;
/*     */     } 
/*  63 */     hash += hash << 3;
/*  64 */     hash ^= hash >> 11;
/*  65 */     hash += hash << 15;
/*     */     
/*  67 */     return hash;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int bernstein(String key) {
/*  77 */     int hash = 0;
/*     */     
/*  79 */     for (int i = 0; i < key.length(); i++) {
/*  80 */       hash = 33 * hash + key.charAt(i);
/*     */     }
/*  82 */     return hash;
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
/*     */   public static int universal(char[] key, int mask, int[] tab) {
/*  94 */     int hash = key.length, len = key.length;
/*  95 */     for (int i = 0; i < len << 3; i += 8) {
/*  96 */       char k = key[i >> 3];
/*  97 */       if ((k & 0x1) == 0) {
/*  98 */         hash ^= tab[i];
/*     */       }
/* 100 */       if ((k & 0x2) == 0) {
/* 101 */         hash ^= tab[i + 1];
/*     */       }
/* 103 */       if ((k & 0x4) == 0) {
/* 104 */         hash ^= tab[i + 2];
/*     */       }
/* 106 */       if ((k & 0x8) == 0) {
/* 107 */         hash ^= tab[i + 3];
/*     */       }
/* 109 */       if ((k & 0x10) == 0) {
/* 110 */         hash ^= tab[i + 4];
/*     */       }
/* 112 */       if ((k & 0x20) == 0) {
/* 113 */         hash ^= tab[i + 5];
/*     */       }
/* 115 */       if ((k & 0x40) == 0) {
/* 116 */         hash ^= tab[i + 6];
/*     */       }
/* 118 */       if ((k & 0x80) == 0) {
/* 119 */         hash ^= tab[i + 7];
/*     */       }
/*     */     } 
/* 122 */     return hash & mask;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int zobrist(char[] key, int mask, int[][] tab) {
/*     */     int hash;
/*     */     int i;
/* 135 */     for (hash = key.length, i = 0; i < key.length; i++) {
/* 136 */       hash ^= tab[i][key[i]];
/*     */     }
/* 138 */     return hash & mask;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int fnvHash(byte[] data) {
/* 148 */     int p = 16777619;
/* 149 */     int hash = -2128831035;
/* 150 */     for (byte b : data) {
/* 151 */       hash = (hash ^ b) * 16777619;
/*     */     }
/* 153 */     hash += hash << 13;
/* 154 */     hash ^= hash >> 7;
/* 155 */     hash += hash << 3;
/* 156 */     hash ^= hash >> 17;
/* 157 */     hash += hash << 5;
/* 158 */     return Math.abs(hash);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int fnvHash(String data) {
/* 168 */     int p = 16777619;
/* 169 */     int hash = -2128831035;
/* 170 */     for (int i = 0; i < data.length(); i++) {
/* 171 */       hash = (hash ^ data.charAt(i)) * 16777619;
/*     */     }
/* 173 */     hash += hash << 13;
/* 174 */     hash ^= hash >> 7;
/* 175 */     hash += hash << 3;
/* 176 */     hash ^= hash >> 17;
/* 177 */     hash += hash << 5;
/* 178 */     return Math.abs(hash);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int intHash(int key) {
/* 188 */     key += key << 15 ^ 0xFFFFFFFF;
/* 189 */     key ^= key >>> 10;
/* 190 */     key += key << 3;
/* 191 */     key ^= key >>> 6;
/* 192 */     key += key << 11 ^ 0xFFFFFFFF;
/* 193 */     key ^= key >>> 16;
/* 194 */     return key;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int rsHash(String str) {
/* 204 */     int b = 378551;
/* 205 */     int a = 63689;
/* 206 */     int hash = 0;
/*     */     
/* 208 */     for (int i = 0; i < str.length(); i++) {
/* 209 */       hash = hash * a + str.charAt(i);
/* 210 */       a *= b;
/*     */     } 
/*     */     
/* 213 */     return hash & Integer.MAX_VALUE;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int jsHash(String str) {
/* 223 */     int hash = 1315423911;
/*     */     
/* 225 */     for (int i = 0; i < str.length(); i++) {
/* 226 */       hash ^= (hash << 5) + str.charAt(i) + (hash >> 2);
/*     */     }
/*     */     
/* 229 */     return Math.abs(hash) & Integer.MAX_VALUE;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int pjwHash(String str) {
/* 239 */     int bitsInUnsignedInt = 32;
/* 240 */     int threeQuarters = bitsInUnsignedInt * 3 / 4;
/* 241 */     int oneEighth = bitsInUnsignedInt / 8;
/* 242 */     int highBits = -1 << bitsInUnsignedInt - oneEighth;
/* 243 */     int hash = 0;
/*     */ 
/*     */     
/* 246 */     for (int i = 0; i < str.length(); i++) {
/* 247 */       hash = (hash << oneEighth) + str.charAt(i);
/*     */       int test;
/* 249 */       if ((test = hash & highBits) != 0) {
/* 250 */         hash = (hash ^ test >> threeQuarters) & (highBits ^ 0xFFFFFFFF);
/*     */       }
/*     */     } 
/*     */     
/* 254 */     return hash & Integer.MAX_VALUE;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int elfHash(String str) {
/* 264 */     int hash = 0;
/*     */ 
/*     */     
/* 267 */     for (int i = 0; i < str.length(); i++) {
/* 268 */       hash = (hash << 4) + str.charAt(i); int x;
/* 269 */       if ((x = (int)(hash & 0xF0000000L)) != 0) {
/* 270 */         hash ^= x >> 24;
/* 271 */         hash &= x ^ 0xFFFFFFFF;
/*     */       } 
/*     */     } 
/*     */     
/* 275 */     return hash & Integer.MAX_VALUE;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int bkdrHash(String str) {
/* 285 */     int seed = 131;
/* 286 */     int hash = 0;
/*     */     
/* 288 */     for (int i = 0; i < str.length(); i++) {
/* 289 */       hash = hash * seed + str.charAt(i);
/*     */     }
/*     */     
/* 292 */     return hash & Integer.MAX_VALUE;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int sdbmHash(String str) {
/* 302 */     int hash = 0;
/*     */     
/* 304 */     for (int i = 0; i < str.length(); i++) {
/* 305 */       hash = str.charAt(i) + (hash << 6) + (hash << 16) - hash;
/*     */     }
/*     */     
/* 308 */     return hash & Integer.MAX_VALUE;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int djbHash(String str) {
/* 318 */     int hash = 5381;
/*     */     
/* 320 */     for (int i = 0; i < str.length(); i++) {
/* 321 */       hash = (hash << 5) + hash + str.charAt(i);
/*     */     }
/*     */     
/* 324 */     return hash & Integer.MAX_VALUE;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int dekHash(String str) {
/* 334 */     int hash = str.length();
/*     */     
/* 336 */     for (int i = 0; i < str.length(); i++) {
/* 337 */       hash = hash << 5 ^ hash >> 27 ^ str.charAt(i);
/*     */     }
/*     */     
/* 340 */     return hash & Integer.MAX_VALUE;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int apHash(String str) {
/* 350 */     int hash = 0;
/*     */     
/* 352 */     for (int i = 0; i < str.length(); i++) {
/* 353 */       hash ^= ((i & 0x1) == 0) ? (hash << 7 ^ str.charAt(i) ^ hash >> 3) : (hash << 11 ^ str.charAt(i) ^ hash >> 5 ^ 0xFFFFFFFF);
/*     */     }
/*     */ 
/*     */     
/* 357 */     return hash;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static long tianlHash(String str) {
/*     */     long hash;
/* 369 */     int iLength = str.length();
/* 370 */     if (iLength == 0) {
/* 371 */       return 0L;
/*     */     }
/*     */     
/* 374 */     if (iLength <= 256) {
/* 375 */       hash = 16777216L * (iLength - 1);
/*     */     } else {
/* 377 */       hash = 4278190080L;
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 384 */     if (iLength <= 96) {
/* 385 */       for (int i = 1; i <= iLength; i++) {
/* 386 */         char ucChar = str.charAt(i - 1);
/* 387 */         if (ucChar <= 'Z' && ucChar >= 'A') {
/* 388 */           ucChar = (char)(ucChar + 32);
/*     */         }
/* 390 */         hash += (3L * i * ucChar * ucChar + 5L * i * ucChar + 7L * i + (11 * ucChar)) % 16777216L;
/*     */       } 
/*     */     } else {
/* 393 */       for (int i = 1; i <= 96; i++) {
/* 394 */         char ucChar = str.charAt(i + iLength - 96 - 1);
/* 395 */         if (ucChar <= 'Z' && ucChar >= 'A') {
/* 396 */           ucChar = (char)(ucChar + 32);
/*     */         }
/* 398 */         hash += (3L * i * ucChar * ucChar + 5L * i * ucChar + 7L * i + (11 * ucChar)) % 16777216L;
/*     */       } 
/*     */     } 
/* 401 */     if (hash < 0L) {
/* 402 */       hash *= -1L;
/*     */     }
/* 404 */     return hash;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int javaDefaultHash(String str) {
/* 414 */     int h = 0;
/* 415 */     int off = 0;
/* 416 */     int len = str.length();
/* 417 */     for (int i = 0; i < len; i++) {
/* 418 */       h = 31 * h + str.charAt(off++);
/*     */     }
/* 420 */     return h;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static long mixHash(String str) {
/* 430 */     long hash = str.hashCode();
/* 431 */     hash <<= 32L;
/* 432 */     hash |= fnvHash(str);
/* 433 */     return hash;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int identityHashCode(Object obj) {
/* 444 */     return System.identityHashCode(obj);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int murmur32(byte[] data) {
/* 455 */     return MurmurHash.hash32(data);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static long murmur64(byte[] data) {
/* 466 */     return MurmurHash.hash64(data);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static long[] murmur128(byte[] data) {
/* 477 */     return MurmurHash.hash128(data);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int cityHash32(byte[] data) {
/* 488 */     return CityHash.hash32(data);
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
/*     */   public static long cityHash64(byte[] data, long seed) {
/* 500 */     return CityHash.hash64(data, seed);
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
/*     */   public static long cityHash64(byte[] data, long seed0, long seed1) {
/* 513 */     return CityHash.hash64(data, seed0, seed1);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static long cityHash64(byte[] data) {
/* 524 */     return CityHash.hash64(data);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static long[] cityHash128(byte[] data) {
/* 535 */     return CityHash.hash128(data).getLongArray();
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
/*     */   public static long[] cityHash128(byte[] data, Number128 seed) {
/* 547 */     return CityHash.hash128(data, seed).getLongArray();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static long metroHash64(byte[] data, long seed) {
/* 558 */     return MetroHash.hash64(data, seed);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static long metroHash64(byte[] data) {
/* 568 */     return MetroHash.hash64(data);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static long[] metroHash128(byte[] data, long seed) {
/* 579 */     return MetroHash.hash128(data, seed).getLongArray();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static long[] metroHash128(byte[] data) {
/* 589 */     return MetroHash.hash128(data).getLongArray();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static long hfHash(String data) {
/* 600 */     int length = data.length();
/* 601 */     long hash = 0L;
/*     */     
/* 603 */     for (int i = 0; i < length; i++) {
/* 604 */       hash += data.charAt(i) * 3L * i;
/*     */     }
/*     */     
/* 607 */     if (hash < 0L) {
/* 608 */       hash = -hash;
/*     */     }
/*     */     
/* 611 */     return hash;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static long hfIpHash(String data) {
/* 622 */     int length = data.length();
/* 623 */     long hash = 0L;
/* 624 */     for (int i = 0; i < length; i++) {
/* 625 */       hash += (data.charAt(i % 4) ^ data.charAt(i));
/*     */     }
/* 627 */     return hash;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\cor\\util\HashUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */