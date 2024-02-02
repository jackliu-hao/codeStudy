/*     */ package org.h2.util;
/*     */ 
/*     */ import java.util.concurrent.atomic.AtomicLong;
/*     */ import org.h2.mvstore.type.DataType;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class MemoryEstimator
/*     */ {
/*     */   private static final int SKIP_SUM_SHIFT = 8;
/*     */   private static final int COUNTER_MASK = 255;
/*     */   private static final int SKIP_SUM_MASK = 65535;
/*     */   private static final int INIT_BIT_SHIFT = 24;
/*     */   private static final int INIT_BIT = 16777216;
/*     */   private static final int WINDOW_SHIFT = 8;
/*     */   private static final int MAGNITUDE_LIMIT = 7;
/*     */   private static final int WINDOW_SIZE = 256;
/*     */   private static final int WINDOW_HALF_SIZE = 128;
/*     */   private static final int SUM_SHIFT = 32;
/*     */   
/*     */   public static <T> int estimateMemory(AtomicLong paramAtomicLong, DataType<T> paramDataType, T paramT) {
/*  62 */     long l1 = paramAtomicLong.get();
/*  63 */     int i = getCounter(l1);
/*  64 */     int j = getSkipSum(l1);
/*  65 */     long l2 = l1 & 0x1000000L;
/*  66 */     long l3 = l1 >>> 32L;
/*  67 */     boolean bool1 = false;
/*  68 */     boolean bool2 = false;
/*  69 */     if (l2 == 0L || i-- == 0) {
/*  70 */       bool2 = true;
/*  71 */       bool1 = (paramT == null) ? false : paramDataType.getMemory(paramT);
/*  72 */       long l = (bool1 << 8L) - l3;
/*  73 */       if (l2 == 0L) {
/*  74 */         if (++i == 256) {
/*  75 */           l2 = 16777216L;
/*     */         }
/*  77 */         l3 = (l3 * i + l + (i >> 1)) / i;
/*     */       } else {
/*  79 */         long l5 = (l >= 0L) ? l : -l;
/*  80 */         int k = calculateMagnitude(l3, l5);
/*  81 */         l3 += (l >> 7 - k) + 1L >> 1L;
/*  82 */         i = (1 << k) - 1 & 0xFF;
/*     */         
/*  84 */         l = ((i << 8) - j);
/*  85 */         j = (int)(j + (l + 128L >> 8L));
/*     */       } 
/*     */     } 
/*  88 */     long l4 = updateStatsData(paramAtomicLong, l1, i, j, l2, l3, bool2, bool1);
/*  89 */     return getAverage(l4);
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
/*     */   public static <T> int estimateMemory(AtomicLong paramAtomicLong, DataType<T> paramDataType, T[] paramArrayOfT, int paramInt) {
/* 102 */     long l1 = paramAtomicLong.get();
/* 103 */     int i = getCounter(l1);
/* 104 */     int j = getSkipSum(l1);
/* 105 */     long l2 = l1 & 0x1000000L;
/* 106 */     long l3 = l1 >>> 32L;
/* 107 */     byte b = 0;
/* 108 */     int k = 0;
/* 109 */     if (l2 != 0L && i >= paramInt) {
/* 110 */       i -= paramInt;
/*     */     } else {
/* 112 */       int m = paramInt;
/* 113 */       while (m-- > 0) {
/* 114 */         T t = paramArrayOfT[b++];
/* 115 */         byte b1 = (t == null) ? 0 : paramDataType.getMemory(t);
/* 116 */         k += b1;
/* 117 */         long l5 = (b1 << 8L) - l3;
/* 118 */         if (l2 == 0L) {
/* 119 */           if (++i == 256) {
/* 120 */             l2 = 16777216L;
/*     */           }
/* 122 */           l3 = (l3 * i + l5 + (i >> 1)) / i; continue;
/*     */         } 
/* 124 */         m -= i;
/* 125 */         long l6 = (l5 >= 0L) ? l5 : -l5;
/* 126 */         int n = calculateMagnitude(l3, l6);
/* 127 */         l3 += (l5 >> 7 - n) + 1L >> 1L;
/* 128 */         i += (1 << n) - 1 & 0xFF;
/*     */         
/* 130 */         l5 = (i << 8L) - j;
/* 131 */         j = (int)(j + (l5 + 128L >> 8L));
/*     */       } 
/*     */     } 
/*     */     
/* 135 */     long l4 = updateStatsData(paramAtomicLong, l1, i, j, l2, l3, b, k);
/* 136 */     return (getAverage(l4) + 8) * paramInt;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int samplingPct(AtomicLong paramAtomicLong) {
/* 145 */     long l = paramAtomicLong.get();
/* 146 */     byte b = ((l & 0x1000000L) == 0L) ? getCounter(l) : 256;
/* 147 */     int i = getSkipSum(l) + b;
/* 148 */     return (b * 100 + (i >> 1)) / i;
/*     */   }
/*     */   
/*     */   private static int calculateMagnitude(long paramLong1, long paramLong2) {
/* 152 */     byte b = 0;
/* 153 */     while (paramLong2 < paramLong1 && b < 7) {
/* 154 */       b++;
/* 155 */       paramLong2 <<= 1L;
/*     */     } 
/* 157 */     return b;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static long updateStatsData(AtomicLong paramAtomicLong, long paramLong1, int paramInt1, int paramInt2, long paramLong2, long paramLong3, int paramInt3, int paramInt4) {
/* 163 */     return updateStatsData(paramAtomicLong, paramLong1, 
/* 164 */         constructStatsData(paramLong3, paramLong2, paramInt2, paramInt1), paramInt3, paramInt4);
/*     */   }
/*     */   
/*     */   private static long constructStatsData(long paramLong1, long paramLong2, int paramInt1, int paramInt2) {
/* 168 */     return paramLong1 << 32L | paramLong2 | paramInt1 << 8L | paramInt2;
/*     */   }
/*     */ 
/*     */   
/*     */   private static long updateStatsData(AtomicLong paramAtomicLong, long paramLong1, long paramLong2, int paramInt1, int paramInt2) {
/* 173 */     while (!paramAtomicLong.compareAndSet(paramLong1, paramLong2)) {
/* 174 */       paramLong1 = paramAtomicLong.get();
/* 175 */       long l = paramLong1 >>> 32L;
/* 176 */       if (paramInt1 > 0) {
/* 177 */         l += paramInt2 - (l * paramInt1 + 128L >> 8L);
/*     */       }
/* 179 */       paramLong2 = l << 32L | paramLong1 & 0x100FFFFL;
/*     */     } 
/* 181 */     return paramLong2;
/*     */   }
/*     */   
/*     */   private static int getCounter(long paramLong) {
/* 185 */     return (int)(paramLong & 0xFFL);
/*     */   }
/*     */   
/*     */   private static int getSkipSum(long paramLong) {
/* 189 */     return (int)(paramLong >> 8L & 0xFFFFL);
/*     */   }
/*     */   
/*     */   private static int getAverage(long paramLong) {
/* 193 */     return (int)(paramLong >>> 40L);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h\\util\MemoryEstimator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */