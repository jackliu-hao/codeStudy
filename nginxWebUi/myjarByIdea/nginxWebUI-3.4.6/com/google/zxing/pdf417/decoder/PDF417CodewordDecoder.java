package com.google.zxing.pdf417.decoder;

import com.google.zxing.common.detector.MathUtils;
import com.google.zxing.pdf417.PDF417Common;

final class PDF417CodewordDecoder {
   private static final float[][] RATIOS_TABLE;

   private PDF417CodewordDecoder() {
   }

   static int getDecodedValue(int[] moduleBitCount) {
      int decodedValue;
      return (decodedValue = getDecodedCodewordValue(sampleBitCounts(moduleBitCount))) != -1 ? decodedValue : getClosestDecodedValue(moduleBitCount);
   }

   private static int[] sampleBitCounts(int[] moduleBitCount) {
      float bitCountSum = (float)MathUtils.sum(moduleBitCount);
      int[] result = new int[8];
      int bitCountIndex = 0;
      int sumPreviousBits = 0;

      for(int i = 0; i < 17; ++i) {
         float sampleIndex = bitCountSum / 34.0F + (float)i * bitCountSum / 17.0F;
         if ((float)(sumPreviousBits + moduleBitCount[bitCountIndex]) <= sampleIndex) {
            sumPreviousBits += moduleBitCount[bitCountIndex];
            ++bitCountIndex;
         }

         int var10002 = result[bitCountIndex]++;
      }

      return result;
   }

   private static int getDecodedCodewordValue(int[] moduleBitCount) {
      int decodedValue;
      return PDF417Common.getCodeword(decodedValue = getBitValue(moduleBitCount)) == -1 ? -1 : decodedValue;
   }

   private static int getBitValue(int[] moduleBitCount) {
      long result = 0L;

      for(int i = 0; i < moduleBitCount.length; ++i) {
         for(int bit = 0; bit < moduleBitCount[i]; ++bit) {
            result = result << 1 | (long)(i % 2 == 0 ? 1 : 0);
         }
      }

      return (int)result;
   }

   private static int getClosestDecodedValue(int[] moduleBitCount) {
      int bitCountSum = MathUtils.sum(moduleBitCount);
      float[] bitCountRatios = new float[8];

      for(int i = 0; i < 8; ++i) {
         bitCountRatios[i] = (float)moduleBitCount[i] / (float)bitCountSum;
      }

      float bestMatchError = Float.MAX_VALUE;
      int bestMatch = -1;

      for(int j = 0; j < RATIOS_TABLE.length; ++j) {
         float error = 0.0F;
         float[] ratioTableRow = RATIOS_TABLE[j];

         for(int k = 0; k < 8; ++k) {
            float diff = ratioTableRow[k] - bitCountRatios[k];
            if ((error += diff * diff) >= bestMatchError) {
               break;
            }
         }

         if (error < bestMatchError) {
            bestMatchError = error;
            bestMatch = PDF417Common.SYMBOL_TABLE[j];
         }
      }

      return bestMatch;
   }

   static {
      RATIOS_TABLE = new float[PDF417Common.SYMBOL_TABLE.length][8];

      for(int i = 0; i < PDF417Common.SYMBOL_TABLE.length; ++i) {
         int currentSymbol;
         int currentBit = (currentSymbol = PDF417Common.SYMBOL_TABLE[i]) & 1;

         for(int j = 0; j < 8; ++j) {
            float size;
            for(size = 0.0F; (currentSymbol & 1) == currentBit; currentSymbol >>= 1) {
               ++size;
            }

            currentBit = currentSymbol & 1;
            RATIOS_TABLE[i][8 - j - 1] = size / 17.0F;
         }
      }

   }
}
