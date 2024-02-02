package com.google.zxing.oned;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.NotFoundException;
import com.google.zxing.common.BitArray;

public final class EAN13Reader extends UPCEANReader {
   static final int[] FIRST_DIGIT_ENCODINGS = new int[]{0, 11, 13, 14, 19, 25, 28, 21, 22, 26};
   private final int[] decodeMiddleCounters = new int[4];

   protected int decodeMiddle(BitArray row, int[] startRange, StringBuilder resultString) throws NotFoundException {
      int[] counters;
      (counters = this.decodeMiddleCounters)[0] = 0;
      counters[1] = 0;
      counters[2] = 0;
      counters[3] = 0;
      int end = row.getSize();
      int rowOffset = startRange[1];
      int lgPatternFound = 0;

      int bestMatch;
      int var12;
      int counter;
      for(int x = 0; x < 6 && rowOffset < end; ++x) {
         bestMatch = decodeDigit(row, counters, rowOffset, L_AND_G_PATTERNS);
         resultString.append((char)(48 + bestMatch % 10));
         int[] var10 = counters;
         int var11 = counters.length;

         for(var12 = 0; var12 < var11; ++var12) {
            counter = var10[var12];
            rowOffset += counter;
         }

         if (bestMatch >= 10) {
            lgPatternFound |= 1 << 5 - x;
         }
      }

      determineFirstDigit(resultString, lgPatternFound);
      rowOffset = findGuardPattern(row, rowOffset, true, MIDDLE_PATTERN)[1];

      for(bestMatch = 0; bestMatch < 6 && rowOffset < end; ++bestMatch) {
         int bestMatch = decodeDigit(row, counters, rowOffset, L_PATTERNS);
         resultString.append((char)(bestMatch + 48));
         int[] var16 = counters;
         var12 = counters.length;

         for(counter = 0; counter < var12; ++counter) {
            int counter = var16[counter];
            rowOffset += counter;
         }
      }

      return rowOffset;
   }

   BarcodeFormat getBarcodeFormat() {
      return BarcodeFormat.EAN_13;
   }

   private static void determineFirstDigit(StringBuilder resultString, int lgPatternFound) throws NotFoundException {
      for(int d = 0; d < 10; ++d) {
         if (lgPatternFound == FIRST_DIGIT_ENCODINGS[d]) {
            resultString.insert(0, (char)(d + 48));
            return;
         }
      }

      throw NotFoundException.getNotFoundInstance();
   }
}
