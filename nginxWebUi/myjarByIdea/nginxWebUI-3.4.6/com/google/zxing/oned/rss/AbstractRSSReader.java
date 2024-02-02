package com.google.zxing.oned.rss;

import com.google.zxing.NotFoundException;
import com.google.zxing.common.detector.MathUtils;
import com.google.zxing.oned.OneDReader;

public abstract class AbstractRSSReader extends OneDReader {
   private static final float MAX_AVG_VARIANCE = 0.2F;
   private static final float MAX_INDIVIDUAL_VARIANCE = 0.45F;
   private static final float MIN_FINDER_PATTERN_RATIO = 0.7916667F;
   private static final float MAX_FINDER_PATTERN_RATIO = 0.89285713F;
   private final int[] decodeFinderCounters = new int[4];
   private final int[] dataCharacterCounters = new int[8];
   private final float[] oddRoundingErrors = new float[4];
   private final float[] evenRoundingErrors = new float[4];
   private final int[] oddCounts;
   private final int[] evenCounts;

   protected AbstractRSSReader() {
      this.oddCounts = new int[this.dataCharacterCounters.length / 2];
      this.evenCounts = new int[this.dataCharacterCounters.length / 2];
   }

   protected final int[] getDecodeFinderCounters() {
      return this.decodeFinderCounters;
   }

   protected final int[] getDataCharacterCounters() {
      return this.dataCharacterCounters;
   }

   protected final float[] getOddRoundingErrors() {
      return this.oddRoundingErrors;
   }

   protected final float[] getEvenRoundingErrors() {
      return this.evenRoundingErrors;
   }

   protected final int[] getOddCounts() {
      return this.oddCounts;
   }

   protected final int[] getEvenCounts() {
      return this.evenCounts;
   }

   protected static int parseFinderValue(int[] counters, int[][] finderPatterns) throws NotFoundException {
      for(int value = 0; value < finderPatterns.length; ++value) {
         if (patternMatchVariance(counters, finderPatterns[value], 0.45F) < 0.2F) {
            return value;
         }
      }

      throw NotFoundException.getNotFoundInstance();
   }

   /** @deprecated */
   @Deprecated
   protected static int count(int[] array) {
      return MathUtils.sum(array);
   }

   protected static void increment(int[] array, float[] errors) {
      int index = 0;
      float biggestError = errors[0];

      for(int i = 1; i < array.length; ++i) {
         if (errors[i] > biggestError) {
            biggestError = errors[i];
            index = i;
         }
      }

      int var10002 = array[index]++;
   }

   protected static void decrement(int[] array, float[] errors) {
      int index = 0;
      float biggestError = errors[0];

      for(int i = 1; i < array.length; ++i) {
         if (errors[i] < biggestError) {
            biggestError = errors[i];
            index = i;
         }
      }

      int var10002 = array[index]--;
   }

   protected static boolean isFinderPattern(int[] counters) {
      int firstTwoSum;
      int sum = (firstTwoSum = counters[0] + counters[1]) + counters[2] + counters[3];
      float ratio;
      if ((ratio = (float)firstTwoSum / (float)sum) >= 0.7916667F && ratio <= 0.89285713F) {
         int minCounter = Integer.MAX_VALUE;
         int maxCounter = Integer.MIN_VALUE;
         int[] var6 = counters;
         int var7 = counters.length;

         for(int var8 = 0; var8 < var7; ++var8) {
            int counter;
            if ((counter = var6[var8]) > maxCounter) {
               maxCounter = counter;
            }

            if (counter < minCounter) {
               minCounter = counter;
            }
         }

         if (maxCounter < minCounter * 10) {
            return true;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }
}
