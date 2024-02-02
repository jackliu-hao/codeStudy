package org.h2.util;

import java.util.concurrent.atomic.AtomicLong;
import org.h2.mvstore.type.DataType;

public final class MemoryEstimator {
   private static final int SKIP_SUM_SHIFT = 8;
   private static final int COUNTER_MASK = 255;
   private static final int SKIP_SUM_MASK = 65535;
   private static final int INIT_BIT_SHIFT = 24;
   private static final int INIT_BIT = 16777216;
   private static final int WINDOW_SHIFT = 8;
   private static final int MAGNITUDE_LIMIT = 7;
   private static final int WINDOW_SIZE = 256;
   private static final int WINDOW_HALF_SIZE = 128;
   private static final int SUM_SHIFT = 32;

   private MemoryEstimator() {
   }

   public static <T> int estimateMemory(AtomicLong var0, DataType<T> var1, T var2) {
      long var3 = var0.get();
      int var5 = getCounter(var3);
      int var6 = getSkipSum(var3);
      long var7 = var3 & 16777216L;
      long var9 = var3 >>> 32;
      int var11 = 0;
      byte var12 = 0;
      long var13;
      if (var7 == 0L || var5-- == 0) {
         var12 = 1;
         var11 = var2 == null ? 0 : var1.getMemory(var2);
         var13 = ((long)var11 << 8) - var9;
         if (var7 == 0L) {
            ++var5;
            if (var5 == 256) {
               var7 = 16777216L;
            }

            var9 = (var9 * (long)var5 + var13 + (long)(var5 >> 1)) / (long)var5;
         } else {
            long var15 = var13 >= 0L ? var13 : -var13;
            int var17 = calculateMagnitude(var9, var15);
            var9 += (var13 >> 7 - var17) + 1L >> 1;
            var5 = (1 << var17) - 1 & 255;
            var13 = (long)((var5 << 8) - var6);
            var6 = (int)((long)var6 + (var13 + 128L >> 8));
         }
      }

      var13 = updateStatsData(var0, var3, var5, var6, var7, var9, var12, var11);
      return getAverage(var13);
   }

   public static <T> int estimateMemory(AtomicLong var0, DataType<T> var1, T[] var2, int var3) {
      long var4 = var0.get();
      int var6 = getCounter(var4);
      int var7 = getSkipSum(var4);
      long var8 = var4 & 16777216L;
      long var10 = var4 >>> 32;
      int var12 = 0;
      int var13 = 0;
      if (var8 != 0L && var6 >= var3) {
         var6 -= var3;
      } else {
         int var14 = var3;

         while(var14-- > 0) {
            Object var15 = var2[var12++];
            int var16 = var15 == null ? 0 : var1.getMemory(var15);
            var13 += var16;
            long var17 = ((long)var16 << 8) - var10;
            if (var8 == 0L) {
               ++var6;
               if (var6 == 256) {
                  var8 = 16777216L;
               }

               var10 = (var10 * (long)var6 + var17 + (long)(var6 >> 1)) / (long)var6;
            } else {
               var14 -= var6;
               long var19 = var17 >= 0L ? var17 : -var17;
               int var21 = calculateMagnitude(var10, var19);
               var10 += (var17 >> 7 - var21) + 1L >> 1;
               var6 += (1 << var21) - 1 & 255;
               var17 = ((long)var6 << 8) - (long)var7;
               var7 = (int)((long)var7 + (var17 + 128L >> 8));
            }
         }
      }

      long var22 = updateStatsData(var0, var4, var6, var7, var8, var10, var12, var13);
      return (getAverage(var22) + 8) * var3;
   }

   public static int samplingPct(AtomicLong var0) {
      long var1 = var0.get();
      int var3 = (var1 & 16777216L) == 0L ? getCounter(var1) : 256;
      int var4 = getSkipSum(var1) + var3;
      return (var3 * 100 + (var4 >> 1)) / var4;
   }

   private static int calculateMagnitude(long var0, long var2) {
      int var4;
      for(var4 = 0; var2 < var0 && var4 < 7; var2 <<= 1) {
         ++var4;
      }

      return var4;
   }

   private static long updateStatsData(AtomicLong var0, long var1, int var3, int var4, long var5, long var7, int var9, int var10) {
      return updateStatsData(var0, var1, constructStatsData(var7, var5, var4, var3), var9, var10);
   }

   private static long constructStatsData(long var0, long var2, int var4, int var5) {
      return var0 << 32 | var2 | (long)var4 << 8 | (long)var5;
   }

   private static long updateStatsData(AtomicLong var0, long var1, long var3, int var5, int var6) {
      long var7;
      for(; !var0.compareAndSet(var1, var3); var3 = var7 << 32 | var1 & 16842751L) {
         var1 = var0.get();
         var7 = var1 >>> 32;
         if (var5 > 0) {
            var7 += (long)var6 - (var7 * (long)var5 + 128L >> 8);
         }
      }

      return var3;
   }

   private static int getCounter(long var0) {
      return (int)(var0 & 255L);
   }

   private static int getSkipSum(long var0) {
      return (int)(var0 >> 8 & 65535L);
   }

   private static int getAverage(long var0) {
      return (int)(var0 >>> 40);
   }
}
