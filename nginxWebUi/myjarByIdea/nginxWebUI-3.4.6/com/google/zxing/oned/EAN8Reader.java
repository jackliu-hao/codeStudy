package com.google.zxing.oned;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.NotFoundException;
import com.google.zxing.common.BitArray;

public final class EAN8Reader extends UPCEANReader {
   private final int[] decodeMiddleCounters = new int[4];

   protected int decodeMiddle(BitArray row, int[] startRange, StringBuilder result) throws NotFoundException {
      int[] counters;
      (counters = this.decodeMiddleCounters)[0] = 0;
      counters[1] = 0;
      counters[2] = 0;
      counters[3] = 0;
      int end = row.getSize();
      int rowOffset = startRange[1];

      int x;
      int var11;
      int counter;
      for(int x = 0; x < 4 && rowOffset < end; ++x) {
         x = decodeDigit(row, counters, rowOffset, L_PATTERNS);
         result.append((char)(x + 48));
         int[] var9 = counters;
         int var10 = counters.length;

         for(var11 = 0; var11 < var10; ++var11) {
            counter = var9[var11];
            rowOffset += counter;
         }
      }

      rowOffset = findGuardPattern(row, rowOffset, true, MIDDLE_PATTERN)[1];

      for(x = 0; x < 4 && rowOffset < end; ++x) {
         int bestMatch = decodeDigit(row, counters, rowOffset, L_PATTERNS);
         result.append((char)(bestMatch + 48));
         int[] var15 = counters;
         var11 = counters.length;

         for(counter = 0; counter < var11; ++counter) {
            int counter = var15[counter];
            rowOffset += counter;
         }
      }

      return rowOffset;
   }

   BarcodeFormat getBarcodeFormat() {
      return BarcodeFormat.EAN_8;
   }
}
