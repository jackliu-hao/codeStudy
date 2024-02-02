/*     */ package org.h2.compress;
/*     */ 
/*     */ import java.nio.ByteBuffer;
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
/*     */ public final class CompressLZF
/*     */   implements Compressor
/*     */ {
/*     */   private static final int HASH_SIZE = 16384;
/*     */   private static final int MAX_LITERAL = 32;
/*     */   private static final int MAX_OFF = 8192;
/*     */   private static final int MAX_REF = 264;
/*     */   private int[] cachedHashTable;
/*     */   
/*     */   public void setOptions(String paramString) {}
/*     */   
/*     */   private static int first(byte[] paramArrayOfbyte, int paramInt) {
/* 125 */     return paramArrayOfbyte[paramInt] << 8 | paramArrayOfbyte[paramInt + 1] & 0xFF;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static int first(ByteBuffer paramByteBuffer, int paramInt) {
/* 133 */     return paramByteBuffer.get(paramInt) << 8 | paramByteBuffer.get(paramInt + 1) & 0xFF;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static int next(int paramInt1, byte[] paramArrayOfbyte, int paramInt2) {
/* 140 */     return paramInt1 << 8 | paramArrayOfbyte[paramInt2 + 2] & 0xFF;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static int next(int paramInt1, ByteBuffer paramByteBuffer, int paramInt2) {
/* 147 */     return paramInt1 << 8 | paramByteBuffer.get(paramInt2 + 2) & 0xFF;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static int hash(int paramInt) {
/* 154 */     return paramInt * 2777 >> 9 & 0x3FFF;
/*     */   }
/*     */ 
/*     */   
/*     */   public int compress(byte[] paramArrayOfbyte1, int paramInt1, int paramInt2, byte[] paramArrayOfbyte2, int paramInt3) {
/* 159 */     int i = paramInt1;
/* 160 */     paramInt2 += paramInt1;
/* 161 */     if (this.cachedHashTable == null) {
/* 162 */       this.cachedHashTable = new int[16384];
/*     */     }
/* 164 */     int[] arrayOfInt = this.cachedHashTable;
/* 165 */     byte b = 0;
/* 166 */     paramInt3++;
/* 167 */     int j = first(paramArrayOfbyte1, paramInt1);
/* 168 */     while (paramInt1 < paramInt2 - 4) {
/* 169 */       byte b1 = paramArrayOfbyte1[paramInt1 + 2];
/*     */       
/* 171 */       j = (j << 8) + (b1 & 0xFF);
/* 172 */       int k = hash(j);
/* 173 */       int m = arrayOfInt[k];
/* 174 */       arrayOfInt[k] = paramInt1;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 181 */       if (m < paramInt1 && m > i && (k = paramInt1 - m - 1) < 8192 && paramArrayOfbyte1[m + 2] == b1 && paramArrayOfbyte1[m + 1] == (byte)(j >> 8) && paramArrayOfbyte1[m] == (byte)(j >> 16)) {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 188 */         int n = paramInt2 - paramInt1 - 2;
/* 189 */         if (n > 264) {
/* 190 */           n = 264;
/*     */         }
/* 192 */         if (!b) {
/*     */ 
/*     */           
/* 195 */           paramInt3--;
/*     */         }
/*     */         else {
/*     */           
/* 199 */           paramArrayOfbyte2[paramInt3 - b - 1] = (byte)(b - 1);
/* 200 */           b = 0;
/*     */         } 
/* 202 */         byte b2 = 3;
/* 203 */         while (b2 < n && paramArrayOfbyte1[m + b2] == paramArrayOfbyte1[paramInt1 + b2]) {
/* 204 */           b2++;
/*     */         }
/* 206 */         b2 -= 2;
/* 207 */         if (b2 < 7) {
/* 208 */           paramArrayOfbyte2[paramInt3++] = (byte)((k >> 8) + (b2 << 5));
/*     */         } else {
/* 210 */           paramArrayOfbyte2[paramInt3++] = (byte)((k >> 8) + 224);
/* 211 */           paramArrayOfbyte2[paramInt3++] = (byte)(b2 - 7);
/*     */         } 
/* 213 */         paramArrayOfbyte2[paramInt3++] = (byte)k;
/*     */         
/* 215 */         paramInt3++;
/* 216 */         paramInt1 += b2;
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 221 */         j = first(paramArrayOfbyte1, paramInt1);
/* 222 */         j = next(j, paramArrayOfbyte1, paramInt1);
/* 223 */         arrayOfInt[hash(j)] = paramInt1++;
/* 224 */         j = next(j, paramArrayOfbyte1, paramInt1);
/* 225 */         arrayOfInt[hash(j)] = paramInt1++;
/*     */         continue;
/*     */       } 
/* 228 */       paramArrayOfbyte2[paramInt3++] = paramArrayOfbyte1[paramInt1++];
/* 229 */       b++;
/*     */ 
/*     */       
/* 232 */       if (b == 32) {
/* 233 */         paramArrayOfbyte2[paramInt3 - b - 1] = (byte)(b - 1);
/* 234 */         b = 0;
/*     */ 
/*     */         
/* 237 */         paramInt3++;
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/* 242 */     while (paramInt1 < paramInt2) {
/* 243 */       paramArrayOfbyte2[paramInt3++] = paramArrayOfbyte1[paramInt1++];
/* 244 */       b++;
/* 245 */       if (b == 32) {
/* 246 */         paramArrayOfbyte2[paramInt3 - b - 1] = (byte)(b - 1);
/* 247 */         b = 0;
/* 248 */         paramInt3++;
/*     */       } 
/*     */     } 
/*     */     
/* 252 */     paramArrayOfbyte2[paramInt3 - b - 1] = (byte)(b - 1);
/* 253 */     if (b == 0) {
/* 254 */       paramInt3--;
/*     */     }
/* 256 */     return paramInt3;
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
/*     */   public int compress(ByteBuffer paramByteBuffer, int paramInt1, byte[] paramArrayOfbyte, int paramInt2) {
/* 269 */     int i = paramInt1;
/* 270 */     int j = paramByteBuffer.capacity();
/* 271 */     if (this.cachedHashTable == null) {
/* 272 */       this.cachedHashTable = new int[16384];
/*     */     }
/* 274 */     int[] arrayOfInt = this.cachedHashTable;
/* 275 */     byte b = 0;
/* 276 */     paramInt2++;
/* 277 */     int k = first(paramByteBuffer, paramInt1);
/* 278 */     while (paramInt1 < j - 4) {
/* 279 */       byte b1 = paramByteBuffer.get(paramInt1 + 2);
/*     */       
/* 281 */       k = (k << 8) + (b1 & 0xFF);
/* 282 */       int m = hash(k);
/* 283 */       int n = arrayOfInt[m];
/* 284 */       arrayOfInt[m] = paramInt1;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 291 */       if (n < paramInt1 && n > i && (m = paramInt1 - n - 1) < 8192 && paramByteBuffer
/*     */ 
/*     */         
/* 294 */         .get(n + 2) == b1 && paramByteBuffer
/* 295 */         .get(n + 1) == (byte)(k >> 8) && paramByteBuffer
/* 296 */         .get(n) == (byte)(k >> 16)) {
/*     */         
/* 298 */         int i1 = j - paramInt1 - 2;
/* 299 */         if (i1 > 264) {
/* 300 */           i1 = 264;
/*     */         }
/* 302 */         if (!b) {
/*     */ 
/*     */           
/* 305 */           paramInt2--;
/*     */         }
/*     */         else {
/*     */           
/* 309 */           paramArrayOfbyte[paramInt2 - b - 1] = (byte)(b - 1);
/* 310 */           b = 0;
/*     */         } 
/* 312 */         byte b2 = 3;
/* 313 */         while (b2 < i1 && paramByteBuffer.get(n + b2) == paramByteBuffer.get(paramInt1 + b2)) {
/* 314 */           b2++;
/*     */         }
/* 316 */         b2 -= 2;
/* 317 */         if (b2 < 7) {
/* 318 */           paramArrayOfbyte[paramInt2++] = (byte)((m >> 8) + (b2 << 5));
/*     */         } else {
/* 320 */           paramArrayOfbyte[paramInt2++] = (byte)((m >> 8) + 224);
/* 321 */           paramArrayOfbyte[paramInt2++] = (byte)(b2 - 7);
/*     */         } 
/* 323 */         paramArrayOfbyte[paramInt2++] = (byte)m;
/*     */         
/* 325 */         paramInt2++;
/* 326 */         paramInt1 += b2;
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 331 */         k = first(paramByteBuffer, paramInt1);
/* 332 */         k = next(k, paramByteBuffer, paramInt1);
/* 333 */         arrayOfInt[hash(k)] = paramInt1++;
/* 334 */         k = next(k, paramByteBuffer, paramInt1);
/* 335 */         arrayOfInt[hash(k)] = paramInt1++;
/*     */         continue;
/*     */       } 
/* 338 */       paramArrayOfbyte[paramInt2++] = paramByteBuffer.get(paramInt1++);
/* 339 */       b++;
/*     */ 
/*     */       
/* 342 */       if (b == 32) {
/* 343 */         paramArrayOfbyte[paramInt2 - b - 1] = (byte)(b - 1);
/* 344 */         b = 0;
/*     */ 
/*     */         
/* 347 */         paramInt2++;
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/* 352 */     while (paramInt1 < j) {
/* 353 */       paramArrayOfbyte[paramInt2++] = paramByteBuffer.get(paramInt1++);
/* 354 */       b++;
/* 355 */       if (b == 32) {
/* 356 */         paramArrayOfbyte[paramInt2 - b - 1] = (byte)(b - 1);
/* 357 */         b = 0;
/* 358 */         paramInt2++;
/*     */       } 
/*     */     } 
/*     */     
/* 362 */     paramArrayOfbyte[paramInt2 - b - 1] = (byte)(b - 1);
/* 363 */     if (b == 0) {
/* 364 */       paramInt2--;
/*     */     }
/* 366 */     return paramInt2;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void expand(byte[] paramArrayOfbyte1, int paramInt1, int paramInt2, byte[] paramArrayOfbyte2, int paramInt3, int paramInt4) {
/* 373 */     if (paramInt1 < 0 || paramInt3 < 0 || paramInt4 < 0) {
/* 374 */       throw new IllegalArgumentException();
/*     */     }
/*     */     do {
/* 377 */       int i = paramArrayOfbyte1[paramInt1++] & 0xFF;
/* 378 */       if (i < 32) {
/*     */         
/* 380 */         i++;
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 385 */         System.arraycopy(paramArrayOfbyte1, paramInt1, paramArrayOfbyte2, paramInt3, i);
/* 386 */         paramInt3 += i;
/* 387 */         paramInt1 += i;
/*     */       }
/*     */       else {
/*     */         
/* 391 */         int j = i >> 5;
/*     */         
/* 393 */         if (j == 7) {
/* 394 */           j += paramArrayOfbyte1[paramInt1++] & 0xFF;
/*     */         }
/*     */ 
/*     */         
/* 398 */         j += 2;
/*     */ 
/*     */ 
/*     */         
/* 402 */         i = -((i & 0x1F) << 8) - 1;
/*     */ 
/*     */         
/* 405 */         i -= paramArrayOfbyte1[paramInt1++] & 0xFF;
/*     */ 
/*     */ 
/*     */         
/* 409 */         i += paramInt3;
/* 410 */         if (paramInt3 + j >= paramArrayOfbyte2.length)
/*     */         {
/* 412 */           throw new ArrayIndexOutOfBoundsException();
/*     */         }
/* 414 */         for (byte b = 0; b < j; b++) {
/* 415 */           paramArrayOfbyte2[paramInt3++] = paramArrayOfbyte2[i++];
/*     */         }
/*     */       } 
/* 418 */     } while (paramInt3 < paramInt4);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void expand(ByteBuffer paramByteBuffer1, ByteBuffer paramByteBuffer2) {
/*     */     do {
/* 429 */       int i = paramByteBuffer1.get() & 0xFF;
/* 430 */       if (i < 32) {
/*     */         
/* 432 */         i++;
/*     */ 
/*     */         
/* 435 */         for (byte b = 0; b < i; b++) {
/* 436 */           paramByteBuffer2.put(paramByteBuffer1.get());
/*     */         }
/*     */       }
/*     */       else {
/*     */         
/* 441 */         int j = i >> 5;
/*     */         
/* 443 */         if (j == 7) {
/* 444 */           j += paramByteBuffer1.get() & 0xFF;
/*     */         }
/*     */ 
/*     */         
/* 448 */         j += 2;
/*     */ 
/*     */ 
/*     */         
/* 452 */         i = -((i & 0x1F) << 8) - 1;
/*     */ 
/*     */         
/* 455 */         i -= paramByteBuffer1.get() & 0xFF;
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 460 */         i += paramByteBuffer2.position();
/* 461 */         for (byte b = 0; b < j; b++) {
/* 462 */           paramByteBuffer2.put(paramByteBuffer2.get(i++));
/*     */         }
/*     */       } 
/* 465 */     } while (paramByteBuffer2.position() < paramByteBuffer2.capacity());
/*     */   }
/*     */ 
/*     */   
/*     */   public int getAlgorithm() {
/* 470 */     return 1;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\compress\CompressLZF.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */