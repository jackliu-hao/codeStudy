/*     */ package org.xnio;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class Bits
/*     */ {
/*     */   public static int intBitMask(int low, int high) {
/*  41 */     assert low >= 0;
/*  42 */     assert low <= high;
/*  43 */     assert high < 32;
/*  44 */     return ((high == 31) ? 0 : (1 << high + 1)) - (1 << low);
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
/*     */   public static long longBitMask(int low, int high) {
/*  56 */     assert low >= 0;
/*  57 */     assert low <= high;
/*  58 */     assert high < 64;
/*  59 */     return ((high == 63) ? 0L : (1L << (int)(high + 1L))) - (1L << (int)low);
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
/*     */   public static boolean anyAreSet(int var, int flags) {
/*  72 */     return ((var & flags) != 0);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean allAreSet(int var, int flags) {
/*  83 */     return ((var & flags) == flags);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean anyAreClear(int var, int flags) {
/*  94 */     return ((var & flags) != flags);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean allAreClear(int var, int flags) {
/* 105 */     return ((var & flags) == 0);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean anyAreSet(long var, long flags) {
/* 116 */     return ((var & flags) != 0L);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean allAreSet(long var, long flags) {
/* 127 */     return ((var & flags) == flags);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean anyAreClear(long var, long flags) {
/* 138 */     return ((var & flags) != flags);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean allAreClear(long var, long flags) {
/* 149 */     return ((var & flags) == 0L);
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
/*     */   public static int unsigned(byte v) {
/* 161 */     return v & 0xFF;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int unsigned(short v) {
/* 171 */     return v & 0xFFFF;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static long unsigned(int v) {
/* 181 */     return v & 0xFFFFFFFFL;
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
/*     */   public static short shortFromBytesLE(byte[] b, int off) {
/* 194 */     return (short)(b[off + 1] << 8 | b[off] & 0xFF);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static short shortFromBytesBE(byte[] b, int off) {
/* 205 */     return (short)(b[off] << 8 | b[off + 1] & 0xFF);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static char charFromBytesLE(byte[] b, int off) {
/* 216 */     return (char)(b[off + 1] << 8 | b[off] & 0xFF);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static char charFromBytesBE(byte[] b, int off) {
/* 227 */     return (char)(b[off] << 8 | b[off + 1] & 0xFF);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int mediumFromBytesLE(byte[] b, int off) {
/* 238 */     return (b[off + 2] & 0xFF) << 16 | (b[off + 1] & 0xFF) << 8 | b[off] & 0xFF;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int mediumFromBytesBE(byte[] b, int off) {
/* 249 */     return (b[off] & 0xFF) << 16 | (b[off + 1] & 0xFF) << 8 | b[off + 2] & 0xFF;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int intFromBytesLE(byte[] b, int off) {
/* 260 */     return b[off + 3] << 24 | (b[off + 2] & 0xFF) << 16 | (b[off + 1] & 0xFF) << 8 | b[off] & 0xFF;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int intFromBytesBE(byte[] b, int off) {
/* 271 */     return b[off] << 24 | (b[off + 1] & 0xFF) << 16 | (b[off + 2] & 0xFF) << 8 | b[off + 3] & 0xFF;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static long longFromBytesLE(byte[] b, int off) {
/* 282 */     return (b[off + 7] & 0xFFL) << 56L | (b[off + 6] & 0xFFL) << 48L | (b[off + 5] & 0xFFL) << 40L | (b[off + 4] & 0xFFL) << 32L | (b[off + 3] & 0xFFL) << 24L | (b[off + 2] & 0xFFL) << 16L | (b[off + 1] & 0xFFL) << 8L | b[off] & 0xFFL;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static long longFromBytesBE(byte[] b, int off) {
/* 293 */     return (b[off] & 0xFFL) << 56L | (b[off + 1] & 0xFFL) << 48L | (b[off + 2] & 0xFFL) << 40L | (b[off + 3] & 0xFFL) << 32L | (b[off + 4] & 0xFFL) << 24L | (b[off + 5] & 0xFFL) << 16L | (b[off + 6] & 0xFFL) << 8L | b[off + 7] & 0xFFL;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\Bits.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */