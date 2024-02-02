/*     */ package cn.hutool.core.lang.hash;
/*     */ 
/*     */ import cn.hutool.core.util.ByteUtil;
/*     */ import java.util.Arrays;
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
/*     */ public class CityHash
/*     */ {
/*     */   private static final long k0 = -4348849565147123417L;
/*     */   private static final long k1 = -5435081209227447693L;
/*     */   private static final long k2 = -7286425919675154353L;
/*     */   private static final long kMul = -7070675565921424023L;
/*     */   private static final int c1 = -862048943;
/*     */   private static final int c2 = 461845907;
/*     */   
/*     */   public static int hash32(byte[] data) {
/*  38 */     int len = data.length;
/*  39 */     if (len <= 24) {
/*  40 */       return (len <= 12) ? ((len <= 4) ? 
/*  41 */         hash32Len0to4(data) : hash32Len5to12(data)) : 
/*  42 */         hash32Len13to24(data);
/*     */     }
/*     */ 
/*     */     
/*  46 */     int h = len, g = -862048943 * len, f = g;
/*  47 */     int a0 = rotate32(fetch32(data, len - 4) * -862048943, 17) * 461845907;
/*  48 */     int a1 = rotate32(fetch32(data, len - 8) * -862048943, 17) * 461845907;
/*  49 */     int a2 = rotate32(fetch32(data, len - 16) * -862048943, 17) * 461845907;
/*  50 */     int a3 = rotate32(fetch32(data, len - 12) * -862048943, 17) * 461845907;
/*  51 */     int a4 = rotate32(fetch32(data, len - 20) * -862048943, 17) * 461845907;
/*  52 */     h ^= a0;
/*  53 */     h = rotate32(h, 19);
/*  54 */     h = h * 5 + -430675100;
/*  55 */     h ^= a2;
/*  56 */     h = rotate32(h, 19);
/*  57 */     h = h * 5 + -430675100;
/*  58 */     g ^= a1;
/*  59 */     g = rotate32(g, 19);
/*  60 */     g = g * 5 + -430675100;
/*  61 */     g ^= a3;
/*  62 */     g = rotate32(g, 19);
/*  63 */     g = g * 5 + -430675100;
/*  64 */     f += a4;
/*  65 */     f = rotate32(f, 19);
/*  66 */     f = f * 5 + -430675100;
/*  67 */     int iters = (len - 1) / 20;
/*     */     
/*  69 */     int pos = 0;
/*     */     do {
/*  71 */       a0 = rotate32(fetch32(data, pos) * -862048943, 17) * 461845907;
/*  72 */       a1 = fetch32(data, pos + 4);
/*  73 */       a2 = rotate32(fetch32(data, pos + 8) * -862048943, 17) * 461845907;
/*  74 */       a3 = rotate32(fetch32(data, pos + 12) * -862048943, 17) * 461845907;
/*  75 */       a4 = fetch32(data, pos + 16);
/*  76 */       h ^= a0;
/*  77 */       h = rotate32(h, 18);
/*  78 */       h = h * 5 + -430675100;
/*  79 */       f += a1;
/*  80 */       f = rotate32(f, 19);
/*  81 */       f *= -862048943;
/*  82 */       g += a2;
/*  83 */       g = rotate32(g, 18);
/*  84 */       g = g * 5 + -430675100;
/*  85 */       h ^= a3 + a1;
/*  86 */       h = rotate32(h, 19);
/*  87 */       h = h * 5 + -430675100;
/*  88 */       g ^= a4;
/*  89 */       g = Integer.reverseBytes(g) * 5;
/*  90 */       h += a4 * 5;
/*  91 */       h = Integer.reverseBytes(h);
/*  92 */       f += a0;
/*  93 */       int swapValue = f;
/*  94 */       f = g;
/*  95 */       g = h;
/*  96 */       h = swapValue;
/*     */       
/*  98 */       pos += 20;
/*  99 */     } while (--iters != 0);
/*     */     
/* 101 */     g = rotate32(g, 11) * -862048943;
/* 102 */     g = rotate32(g, 17) * -862048943;
/* 103 */     f = rotate32(f, 11) * -862048943;
/* 104 */     f = rotate32(f, 17) * -862048943;
/* 105 */     h = rotate32(h + g, 19);
/* 106 */     h = h * 5 + -430675100;
/* 107 */     h = rotate32(h, 17) * -862048943;
/* 108 */     h = rotate32(h + f, 19);
/* 109 */     h = h * 5 + -430675100;
/* 110 */     h = rotate32(h, 17) * -862048943;
/* 111 */     return h;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static long hash64(byte[] data) {
/* 121 */     int len = data.length;
/* 122 */     if (len <= 32) {
/* 123 */       if (len <= 16) {
/* 124 */         return hashLen0to16(data);
/*     */       }
/* 126 */       return hashLen17to32(data);
/*     */     } 
/* 128 */     if (len <= 64) {
/* 129 */       return hashLen33to64(data);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 134 */     long x = fetch64(data, len - 40);
/* 135 */     long y = fetch64(data, len - 16) + fetch64(data, len - 56);
/* 136 */     long z = hashLen16(fetch64(data, len - 48) + len, fetch64(data, len - 24));
/* 137 */     Number128 v = weakHashLen32WithSeeds(data, len - 64, len, z);
/* 138 */     Number128 w = weakHashLen32WithSeeds(data, len - 32, y + -5435081209227447693L, x);
/* 139 */     x = x * -5435081209227447693L + fetch64(data, 0);
/*     */ 
/*     */     
/* 142 */     len = len - 1 & 0xFFFFFFC0;
/* 143 */     int pos = 0;
/*     */     while (true) {
/* 145 */       x = rotate64(x + y + v.getLowValue() + fetch64(data, pos + 8), 37) * -5435081209227447693L;
/* 146 */       y = rotate64(y + v.getHighValue() + fetch64(data, pos + 48), 42) * -5435081209227447693L;
/* 147 */       x ^= w.getHighValue();
/* 148 */       y += v.getLowValue() + fetch64(data, pos + 40);
/* 149 */       z = rotate64(z + w.getLowValue(), 33) * -5435081209227447693L;
/* 150 */       v = weakHashLen32WithSeeds(data, pos, v.getHighValue() * -5435081209227447693L, x + w.getLowValue());
/* 151 */       w = weakHashLen32WithSeeds(data, pos + 32, z + w.getHighValue(), y + fetch64(data, pos + 16));
/*     */       
/* 153 */       long swapValue = x;
/* 154 */       x = z;
/* 155 */       z = swapValue;
/* 156 */       pos += 64;
/* 157 */       len -= 64;
/* 158 */       if (len == 0) {
/* 159 */         return hashLen16(hashLen16(v.getLowValue(), w.getLowValue()) + shiftMix(y) * -5435081209227447693L + z, 
/* 160 */             hashLen16(v.getHighValue(), w.getHighValue()) + x);
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static long hash64(byte[] data, long seed0, long seed1) {
/* 172 */     return hashLen16(hash64(data) - seed0, seed1);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static long hash64(byte[] data, long seed) {
/* 183 */     return hash64(data, -7286425919675154353L, seed);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Number128 hash128(byte[] data) {
/* 193 */     int len = data.length;
/* 194 */     return (len >= 16) ? 
/* 195 */       hash128(data, 16, new Number128(
/* 196 */           fetch64(data, 0), fetch64(data, 8) + -4348849565147123417L)) : 
/* 197 */       hash128(data, 0, new Number128(-4348849565147123417L, -5435081209227447693L));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Number128 hash128(byte[] data, Number128 seed) {
/* 208 */     return hash128(data, 0, seed);
/*     */   }
/*     */ 
/*     */   
/*     */   private static Number128 hash128(byte[] byteArray, int start, Number128 seed) {
/* 213 */     int len = byteArray.length - start;
/*     */     
/* 215 */     if (len < 128) {
/* 216 */       return cityMurmur(Arrays.copyOfRange(byteArray, start, byteArray.length), seed);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 221 */     Number128 v = new Number128(0L, 0L);
/* 222 */     Number128 w = new Number128(0L, 0L);
/* 223 */     long x = seed.getLowValue();
/* 224 */     long y = seed.getHighValue();
/* 225 */     long z = len * -5435081209227447693L;
/* 226 */     v.setLowValue(rotate64(y ^ 0xB492B66FBE98F273L, 49) * -5435081209227447693L + fetch64(byteArray, start));
/* 227 */     v.setHighValue(rotate64(v.getLowValue(), 42) * -5435081209227447693L + fetch64(byteArray, start + 8));
/* 228 */     w.setLowValue(rotate64(y + z, 35) * -5435081209227447693L + x);
/* 229 */     w.setHighValue(rotate64(x + fetch64(byteArray, start + 88), 53) * -5435081209227447693L);
/*     */ 
/*     */     
/* 232 */     int pos = start;
/*     */     while (true) {
/* 234 */       x = rotate64(x + y + v.getLowValue() + fetch64(byteArray, pos + 8), 37) * -5435081209227447693L;
/* 235 */       y = rotate64(y + v.getHighValue() + fetch64(byteArray, pos + 48), 42) * -5435081209227447693L;
/* 236 */       x ^= w.getHighValue();
/* 237 */       y += v.getLowValue() + fetch64(byteArray, pos + 40);
/* 238 */       z = rotate64(z + w.getLowValue(), 33) * -5435081209227447693L;
/* 239 */       v = weakHashLen32WithSeeds(byteArray, pos, v.getHighValue() * -5435081209227447693L, x + w.getLowValue());
/* 240 */       w = weakHashLen32WithSeeds(byteArray, pos + 32, z + w.getHighValue(), y + fetch64(byteArray, pos + 16));
/*     */       
/* 242 */       long swapValue = x;
/* 243 */       x = z;
/* 244 */       z = swapValue;
/* 245 */       pos += 64;
/* 246 */       x = rotate64(x + y + v.getLowValue() + fetch64(byteArray, pos + 8), 37) * -5435081209227447693L;
/* 247 */       y = rotate64(y + v.getHighValue() + fetch64(byteArray, pos + 48), 42) * -5435081209227447693L;
/* 248 */       x ^= w.getHighValue();
/* 249 */       y += v.getLowValue() + fetch64(byteArray, pos + 40);
/* 250 */       z = rotate64(z + w.getLowValue(), 33) * -5435081209227447693L;
/* 251 */       v = weakHashLen32WithSeeds(byteArray, pos, v.getHighValue() * -5435081209227447693L, x + w.getLowValue());
/* 252 */       w = weakHashLen32WithSeeds(byteArray, pos + 32, z + w.getHighValue(), y + fetch64(byteArray, pos + 16));
/* 253 */       swapValue = x;
/* 254 */       x = z;
/* 255 */       z = swapValue;
/* 256 */       pos += 64;
/* 257 */       len -= 128;
/* 258 */       if (len < 128) {
/* 259 */         x += rotate64(v.getLowValue() + z, 49) * -4348849565147123417L;
/* 260 */         y = y * -4348849565147123417L + rotate64(w.getHighValue(), 37);
/* 261 */         z = z * -4348849565147123417L + rotate64(w.getLowValue(), 27);
/* 262 */         w.setLowValue(w.getLowValue() * 9L);
/* 263 */         v.setLowValue(v.getLowValue() * -4348849565147123417L);
/*     */ 
/*     */         
/* 266 */         for (int tail_done = 0; tail_done < len; ) {
/* 267 */           tail_done += 32;
/* 268 */           y = rotate64(x + y, 42) * -4348849565147123417L + v.getHighValue();
/* 269 */           w.setLowValue(w.getLowValue() + fetch64(byteArray, pos + len - tail_done + 16));
/* 270 */           x = x * -4348849565147123417L + w.getLowValue();
/* 271 */           z += w.getHighValue() + fetch64(byteArray, pos + len - tail_done);
/* 272 */           w.setHighValue(w.getHighValue() + v.getLowValue());
/* 273 */           v = weakHashLen32WithSeeds(byteArray, pos + len - tail_done, v.getLowValue() + z, v.getHighValue());
/* 274 */           v.setLowValue(v.getLowValue() * -4348849565147123417L);
/*     */         } 
/*     */ 
/*     */ 
/*     */         
/* 279 */         x = hashLen16(x, v.getLowValue());
/* 280 */         y = hashLen16(y + z, w.getLowValue());
/* 281 */         return new Number128(hashLen16(x + v.getHighValue(), w.getHighValue()) + y, 
/* 282 */             hashLen16(x + w.getHighValue(), y + v.getHighValue()));
/*     */       } 
/*     */     } 
/*     */   }
/*     */   private static int hash32Len0to4(byte[] byteArray) {
/* 287 */     int b = 0;
/* 288 */     int c = 9;
/* 289 */     int len = byteArray.length;
/* 290 */     for (int v : byteArray) {
/* 291 */       b = b * -862048943 + v;
/* 292 */       c ^= b;
/*     */     } 
/* 294 */     return fmix(mur(b, mur(len, c)));
/*     */   }
/*     */   
/*     */   private static int hash32Len5to12(byte[] byteArray) {
/* 298 */     int len = byteArray.length;
/* 299 */     int a = len, b = len * 5, c = 9, d = b;
/* 300 */     a += fetch32(byteArray, 0);
/* 301 */     b += fetch32(byteArray, len - 4);
/* 302 */     c += fetch32(byteArray, len >>> 1 & 0x4);
/* 303 */     return fmix(mur(c, mur(b, mur(a, d))));
/*     */   }
/*     */   
/*     */   private static int hash32Len13to24(byte[] byteArray) {
/* 307 */     int len = byteArray.length;
/* 308 */     int a = fetch32(byteArray, (len >>> 1) - 4);
/* 309 */     int b = fetch32(byteArray, 4);
/* 310 */     int c = fetch32(byteArray, len - 8);
/* 311 */     int d = fetch32(byteArray, len >>> 1);
/* 312 */     int e = fetch32(byteArray, 0);
/* 313 */     int f = fetch32(byteArray, len - 4);
/*     */     
/* 315 */     int h = len;
/*     */     
/* 317 */     return fmix(mur(f, mur(e, mur(d, mur(c, mur(b, mur(a, h)))))));
/*     */   }
/*     */   
/*     */   private static long hashLen0to16(byte[] byteArray) {
/* 321 */     int len = byteArray.length;
/* 322 */     if (len >= 8) {
/* 323 */       long mul = -7286425919675154353L + len * 2L;
/* 324 */       long a = fetch64(byteArray, 0) + -7286425919675154353L;
/* 325 */       long b = fetch64(byteArray, len - 8);
/* 326 */       long c = rotate64(b, 37) * mul + a;
/* 327 */       long d = (rotate64(a, 25) + b) * mul;
/* 328 */       return hashLen16(c, d, mul);
/*     */     } 
/* 330 */     if (len >= 4) {
/* 331 */       long mul = -7286425919675154353L + (len * 2);
/* 332 */       long a = fetch32(byteArray, 0) & 0xFFFFFFFFL;
/* 333 */       return hashLen16(len + (a << 3L), fetch32(byteArray, len - 4) & 0xFFFFFFFFL, mul);
/*     */     } 
/* 335 */     if (len > 0) {
/* 336 */       int a = byteArray[0] & 0xFF;
/* 337 */       int b = byteArray[len >>> 1] & 0xFF;
/* 338 */       int c = byteArray[len - 1] & 0xFF;
/* 339 */       int y = a + (b << 8);
/* 340 */       int z = len + (c << 2);
/* 341 */       return shiftMix(y * -7286425919675154353L ^ z * -4348849565147123417L) * -7286425919675154353L;
/*     */     } 
/* 343 */     return -7286425919675154353L;
/*     */   }
/*     */ 
/*     */   
/*     */   private static long hashLen17to32(byte[] byteArray) {
/* 348 */     int len = byteArray.length;
/* 349 */     long mul = -7286425919675154353L + len * 2L;
/* 350 */     long a = fetch64(byteArray, 0) * -5435081209227447693L;
/* 351 */     long b = fetch64(byteArray, 8);
/* 352 */     long c = fetch64(byteArray, len - 8) * mul;
/* 353 */     long d = fetch64(byteArray, len - 16) * -7286425919675154353L;
/* 354 */     return hashLen16(rotate64(a + b, 43) + rotate64(c, 30) + d, a + 
/* 355 */         rotate64(b + -7286425919675154353L, 18) + c, mul);
/*     */   }
/*     */   
/*     */   private static long hashLen33to64(byte[] byteArray) {
/* 359 */     int len = byteArray.length;
/* 360 */     long mul = -7286425919675154353L + len * 2L;
/* 361 */     long a = fetch64(byteArray, 0) * -7286425919675154353L;
/* 362 */     long b = fetch64(byteArray, 8);
/* 363 */     long c = fetch64(byteArray, len - 24);
/* 364 */     long d = fetch64(byteArray, len - 32);
/* 365 */     long e = fetch64(byteArray, 16) * -7286425919675154353L;
/* 366 */     long f = fetch64(byteArray, 24) * 9L;
/* 367 */     long g = fetch64(byteArray, len - 8);
/* 368 */     long h = fetch64(byteArray, len - 16) * mul;
/* 369 */     long u = rotate64(a + g, 43) + (rotate64(b, 30) + c) * 9L;
/* 370 */     long v = (a + g ^ d) + f + 1L;
/* 371 */     long w = Long.reverseBytes((u + v) * mul) + h;
/* 372 */     long x = rotate64(e + f, 42) + c;
/* 373 */     long y = (Long.reverseBytes((v + w) * mul) + g) * mul;
/* 374 */     long z = e + f + c;
/* 375 */     a = Long.reverseBytes((x + z) * mul + y) + b;
/* 376 */     b = shiftMix((z + a) * mul + d + h) * mul;
/* 377 */     return b + x;
/*     */   }
/*     */   
/*     */   private static long fetch64(byte[] byteArray, int start) {
/* 381 */     return ByteUtil.bytesToLong(byteArray, start, ByteUtil.CPU_ENDIAN);
/*     */   }
/*     */   
/*     */   private static int fetch32(byte[] byteArray, int start) {
/* 385 */     return ByteUtil.bytesToInt(byteArray, start, ByteUtil.CPU_ENDIAN);
/*     */   }
/*     */ 
/*     */   
/*     */   private static long rotate64(long val, int shift) {
/* 390 */     return (shift == 0) ? val : (val >>> shift | val << 64 - shift);
/*     */   }
/*     */ 
/*     */   
/*     */   private static int rotate32(int val, int shift) {
/* 395 */     return (shift == 0) ? val : (val >>> shift | val << 32 - shift);
/*     */   }
/*     */ 
/*     */   
/*     */   private static long hashLen16(long u, long v, long mul) {
/* 400 */     long a = (u ^ v) * mul;
/* 401 */     a ^= a >>> 47L;
/* 402 */     long b = (v ^ a) * mul;
/* 403 */     b ^= b >>> 47L;
/* 404 */     b *= mul;
/* 405 */     return b;
/*     */   }
/*     */   
/*     */   private static long hashLen16(long u, long v) {
/* 409 */     return hash128to64(new Number128(u, v));
/*     */   }
/*     */ 
/*     */   
/*     */   private static long hash128to64(Number128 number128) {
/* 414 */     long a = (number128.getLowValue() ^ number128.getHighValue()) * -7070675565921424023L;
/* 415 */     a ^= a >>> 47L;
/* 416 */     long b = (number128.getHighValue() ^ a) * -7070675565921424023L;
/* 417 */     b ^= b >>> 47L;
/* 418 */     b *= -7070675565921424023L;
/* 419 */     return b;
/*     */   }
/*     */   
/*     */   private static long shiftMix(long val) {
/* 423 */     return val ^ val >>> 47L;
/*     */   }
/*     */   
/*     */   private static int fmix(int h) {
/* 427 */     h ^= h >>> 16;
/* 428 */     h *= -2048144789;
/* 429 */     h ^= h >>> 13;
/* 430 */     h *= -1028477387;
/* 431 */     h ^= h >>> 16;
/* 432 */     return h;
/*     */   }
/*     */ 
/*     */   
/*     */   private static int mur(int a, int h) {
/* 437 */     a *= -862048943;
/* 438 */     a = rotate32(a, 17);
/* 439 */     a *= 461845907;
/* 440 */     h ^= a;
/* 441 */     h = rotate32(h, 19);
/* 442 */     return h * 5 + -430675100;
/*     */   }
/*     */ 
/*     */   
/*     */   private static Number128 weakHashLen32WithSeeds(long w, long x, long y, long z, long a, long b) {
/* 447 */     a += w;
/* 448 */     b = rotate64(b + a + z, 21);
/* 449 */     long c = a;
/* 450 */     a += x;
/* 451 */     a += y;
/* 452 */     b += rotate64(a, 44);
/* 453 */     return new Number128(a + z, b + c);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static Number128 weakHashLen32WithSeeds(byte[] byteArray, int start, long a, long b) {
/* 459 */     return weakHashLen32WithSeeds(fetch64(byteArray, start), 
/* 460 */         fetch64(byteArray, start + 8), 
/* 461 */         fetch64(byteArray, start + 16), 
/* 462 */         fetch64(byteArray, start + 24), a, b);
/*     */   }
/*     */ 
/*     */   
/*     */   private static Number128 cityMurmur(byte[] byteArray, Number128 seed) {
/*     */     long c, d;
/* 468 */     int len = byteArray.length;
/* 469 */     long a = seed.getLowValue();
/* 470 */     long b = seed.getHighValue();
/*     */ 
/*     */     
/* 473 */     int l = len - 16;
/* 474 */     if (l <= 0) {
/* 475 */       a = shiftMix(a * -5435081209227447693L) * -5435081209227447693L;
/* 476 */       c = b * -5435081209227447693L + hashLen0to16(byteArray);
/* 477 */       d = shiftMix(a + ((len >= 8) ? fetch64(byteArray, 0) : c));
/*     */     } else {
/* 479 */       c = hashLen16(fetch64(byteArray, len - 8) + -5435081209227447693L, a);
/* 480 */       d = hashLen16(b + len, c + fetch64(byteArray, len - 16));
/* 481 */       a += d;
/* 482 */       int pos = 0;
/*     */       do {
/* 484 */         a ^= shiftMix(fetch64(byteArray, pos) * -5435081209227447693L) * -5435081209227447693L;
/* 485 */         a *= -5435081209227447693L;
/* 486 */         b ^= a;
/* 487 */         c ^= shiftMix(fetch64(byteArray, pos + 8) * -5435081209227447693L) * -5435081209227447693L;
/* 488 */         c *= -5435081209227447693L;
/* 489 */         d ^= c;
/* 490 */         pos += 16;
/* 491 */         l -= 16;
/* 492 */       } while (l > 0);
/*     */     } 
/* 494 */     a = hashLen16(a, c);
/* 495 */     b = hashLen16(d, b);
/* 496 */     return new Number128(a ^ b, hashLen16(b, a));
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\lang\hash\CityHash.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */