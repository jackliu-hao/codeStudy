/*     */ package org.apache.commons.codec.digest;
/*     */ 
/*     */ import org.apache.commons.codec.binary.StringUtils;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class MurmurHash2
/*     */ {
/*     */   private static final int M32 = 1540483477;
/*     */   private static final int R32 = 24;
/*     */   private static final long M64 = -4132994306676758123L;
/*     */   private static final int R64 = 47;
/*     */   
/*     */   public static int hash32(byte[] data, int length, int seed) {
/*  75 */     int h = seed ^ length;
/*     */ 
/*     */     
/*  78 */     int nblocks = length >> 2;
/*     */ 
/*     */     
/*  81 */     for (int i = 0; i < nblocks; i++) {
/*  82 */       int j = i << 2;
/*  83 */       int k = getLittleEndianInt(data, j);
/*  84 */       k *= 1540483477;
/*  85 */       k ^= k >>> 24;
/*  86 */       k *= 1540483477;
/*  87 */       h *= 1540483477;
/*  88 */       h ^= k;
/*     */     } 
/*     */ 
/*     */     
/*  92 */     int index = nblocks << 2;
/*  93 */     switch (length - index) {
/*     */       case 3:
/*  95 */         h ^= (data[index + 2] & 0xFF) << 16;
/*     */       case 2:
/*  97 */         h ^= (data[index + 1] & 0xFF) << 8;
/*     */       case 1:
/*  99 */         h ^= data[index] & 0xFF;
/* 100 */         h *= 1540483477;
/*     */         break;
/*     */     } 
/*     */ 
/*     */     
/* 105 */     h ^= h >>> 13;
/* 106 */     h *= 1540483477;
/* 107 */     h ^= h >>> 15;
/*     */     
/* 109 */     return h;
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
/*     */   public static int hash32(byte[] data, int length) {
/* 127 */     return hash32(data, length, -1756908916);
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
/*     */ 
/*     */   
/*     */   public static int hash32(String text) {
/* 149 */     byte[] bytes = StringUtils.getBytesUtf8(text);
/* 150 */     return hash32(bytes, bytes.length);
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
/*     */   
/*     */   public static int hash32(String text, int from, int length) {
/* 171 */     return hash32(text.substring(from, from + length));
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
/*     */   public static long hash64(byte[] data, int length, int seed) {
/* 183 */     long h = seed & 0xFFFFFFFFL ^ length * -4132994306676758123L;
/*     */     
/* 185 */     int nblocks = length >> 3;
/*     */ 
/*     */     
/* 188 */     for (int i = 0; i < nblocks; i++) {
/* 189 */       int j = i << 3;
/* 190 */       long k = getLittleEndianLong(data, j);
/*     */       
/* 192 */       k *= -4132994306676758123L;
/* 193 */       k ^= k >>> 47L;
/* 194 */       k *= -4132994306676758123L;
/*     */       
/* 196 */       h ^= k;
/* 197 */       h *= -4132994306676758123L;
/*     */     } 
/*     */     
/* 200 */     int index = nblocks << 3;
/* 201 */     switch (length - index) {
/*     */       case 7:
/* 203 */         h ^= (data[index + 6] & 0xFFL) << 48L;
/*     */       case 6:
/* 205 */         h ^= (data[index + 5] & 0xFFL) << 40L;
/*     */       case 5:
/* 207 */         h ^= (data[index + 4] & 0xFFL) << 32L;
/*     */       case 4:
/* 209 */         h ^= (data[index + 3] & 0xFFL) << 24L;
/*     */       case 3:
/* 211 */         h ^= (data[index + 2] & 0xFFL) << 16L;
/*     */       case 2:
/* 213 */         h ^= (data[index + 1] & 0xFFL) << 8L;
/*     */       case 1:
/* 215 */         h ^= data[index] & 0xFFL;
/* 216 */         h *= -4132994306676758123L;
/*     */         break;
/*     */     } 
/* 219 */     h ^= h >>> 47L;
/* 220 */     h *= -4132994306676758123L;
/* 221 */     h ^= h >>> 47L;
/*     */     
/* 223 */     return h;
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
/*     */   public static long hash64(byte[] data, int length) {
/* 241 */     return hash64(data, length, -512093083);
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
/*     */ 
/*     */   
/*     */   public static long hash64(String text) {
/* 263 */     byte[] bytes = StringUtils.getBytesUtf8(text);
/* 264 */     return hash64(bytes, bytes.length);
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
/*     */   
/*     */   public static long hash64(String text, int from, int length) {
/* 285 */     return hash64(text.substring(from, from + length));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static int getLittleEndianInt(byte[] data, int index) {
/* 296 */     return data[index] & 0xFF | (data[index + 1] & 0xFF) << 8 | (data[index + 2] & 0xFF) << 16 | (data[index + 3] & 0xFF) << 24;
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
/*     */   private static long getLittleEndianLong(byte[] data, int index) {
/* 310 */     return data[index] & 0xFFL | (data[index + 1] & 0xFFL) << 8L | (data[index + 2] & 0xFFL) << 16L | (data[index + 3] & 0xFFL) << 24L | (data[index + 4] & 0xFFL) << 32L | (data[index + 5] & 0xFFL) << 40L | (data[index + 6] & 0xFFL) << 48L | (data[index + 7] & 0xFFL) << 56L;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\codec\digest\MurmurHash2.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */