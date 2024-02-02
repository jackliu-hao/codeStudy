package com.google.zxing.oned;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.DecodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.ResultPoint;
import com.google.zxing.common.BitArray;
import java.util.Map;

public final class ITFReader extends OneDReader {
   private static final float MAX_AVG_VARIANCE = 0.38F;
   private static final float MAX_INDIVIDUAL_VARIANCE = 0.78F;
   private static final int W = 3;
   private static final int N = 1;
   private static final int[] DEFAULT_ALLOWED_LENGTHS = new int[]{6, 8, 10, 12, 14};
   private int narrowLineWidth = -1;
   private static final int[] START_PATTERN = new int[]{1, 1, 1, 1};
   private static final int[] END_PATTERN_REVERSED = new int[]{1, 1, 3};
   static final int[][] PATTERNS = new int[][]{{1, 1, 3, 3, 1}, {3, 1, 1, 1, 3}, {1, 3, 1, 1, 3}, {3, 3, 1, 1, 1}, {1, 1, 3, 1, 3}, {3, 1, 3, 1, 1}, {1, 3, 3, 1, 1}, {1, 1, 1, 3, 3}, {3, 1, 1, 3, 1}, {1, 3, 1, 3, 1}};

   public Result decodeRow(int rowNumber, BitArray row, Map<DecodeHintType, ?> hints) throws FormatException, NotFoundException {
      int[] startRange = this.decodeStart(row);
      int[] endRange = this.decodeEnd(row);
      StringBuilder result = new StringBuilder(20);
      decodeMiddle(row, startRange[1], endRange[0], result);
      String resultString = result.toString();
      int[] allowedLengths = null;
      if (hints != null) {
         allowedLengths = (int[])((int[])hints.get(DecodeHintType.ALLOWED_LENGTHS));
      }

      if (allowedLengths == null) {
         allowedLengths = DEFAULT_ALLOWED_LENGTHS;
      }

      int length = resultString.length();
      boolean lengthOK = false;
      int maxAllowedLength = 0;
      int[] var12 = allowedLengths;
      int var13 = allowedLengths.length;

      for(int var14 = 0; var14 < var13; ++var14) {
         int allowedLength = var12[var14];
         if (length == allowedLength) {
            lengthOK = true;
            break;
         }

         if (allowedLength > maxAllowedLength) {
            maxAllowedLength = allowedLength;
         }
      }

      if (!lengthOK && length > maxAllowedLength) {
         lengthOK = true;
      }

      if (!lengthOK) {
         throw FormatException.getFormatInstance();
      } else {
         return new Result(resultString, (byte[])null, new ResultPoint[]{new ResultPoint((float)startRange[1], (float)rowNumber), new ResultPoint((float)endRange[0], (float)rowNumber)}, BarcodeFormat.ITF);
      }
   }

   private static void decodeMiddle(BitArray row, int payloadStart, int payloadEnd, StringBuilder resultString) throws NotFoundException {
      int[] counterDigitPair = new int[10];
      int[] counterBlack = new int[5];
      int[] counterWhite = new int[5];

      while(payloadStart < payloadEnd) {
         recordPattern(row, payloadStart, counterDigitPair);

         int bestMatch;
         for(bestMatch = 0; bestMatch < 5; ++bestMatch) {
            int twoK = 2 * bestMatch;
            counterBlack[bestMatch] = counterDigitPair[twoK];
            counterWhite[bestMatch] = counterDigitPair[twoK + 1];
         }

         bestMatch = decodeDigit(counterBlack);
         resultString.append((char)(bestMatch + 48));
         bestMatch = decodeDigit(counterWhite);
         resultString.append((char)(bestMatch + 48));
         int[] var11 = counterDigitPair;

         for(int var9 = 0; var9 < 10; ++var9) {
            int counterDigit = var11[var9];
            payloadStart += counterDigit;
         }
      }

   }

   private int[] decodeStart(BitArray row) throws NotFoundException {
      int endStart = skipWhiteSpace(row);
      int[] startPattern = findGuardPattern(row, endStart, START_PATTERN);
      this.narrowLineWidth = (startPattern[1] - startPattern[0]) / 4;
      this.validateQuietZone(row, startPattern[0]);
      return startPattern;
   }

   private void validateQuietZone(BitArray row, int startPattern) throws NotFoundException {
      int quietCount = (quietCount = this.narrowLineWidth * 10) < startPattern ? quietCount : startPattern;

      for(int i = startPattern - 1; quietCount > 0 && i >= 0 && !row.get(i); --i) {
         --quietCount;
      }

      if (quietCount != 0) {
         throw NotFoundException.getNotFoundInstance();
      }
   }

   private static int skipWhiteSpace(BitArray row) throws NotFoundException {
      int width = row.getSize();
      int endStart;
      if ((endStart = row.getNextSet(0)) == width) {
         throw NotFoundException.getNotFoundInstance();
      } else {
         return endStart;
      }
   }

   private int[] decodeEnd(BitArray row) throws NotFoundException {
      row.reverse();

      int[] var5;
      try {
         int endStart = skipWhiteSpace(row);
         int[] endPattern = findGuardPattern(row, endStart, END_PATTERN_REVERSED);
         this.validateQuietZone(row, endPattern[0]);
         int temp = endPattern[0];
         endPattern[0] = row.getSize() - endPattern[1];
         endPattern[1] = row.getSize() - temp;
         var5 = endPattern;
      } finally {
         row.reverse();
      }

      return var5;
   }

   private static int[] findGuardPattern(BitArray row, int rowOffset, int[] pattern) throws NotFoundException {
      int patternLength;
      int[] counters = new int[patternLength = pattern.length];
      int width = row.getSize();
      boolean isWhite = false;
      int counterPosition = 0;
      int patternStart = rowOffset;

      for(int x = rowOffset; x < width; ++x) {
         if (row.get(x) ^ isWhite) {
            int var10002 = counters[counterPosition]++;
         } else {
            if (counterPosition == patternLength - 1) {
               if (patternMatchVariance(counters, pattern, 0.78F) < 0.38F) {
                  return new int[]{patternStart, x};
               }

               patternStart += counters[0] + counters[1];
               System.arraycopy(counters, 2, counters, 0, patternLength - 2);
               counters[patternLength - 2] = 0;
               counters[patternLength - 1] = 0;
               --counterPosition;
            } else {
               ++counterPosition;
            }

            counters[counterPosition] = 1;
            isWhite = !isWhite;
         }
      }

      throw NotFoundException.getNotFoundInstance();
   }

   private static int decodeDigit(int[] counters) throws NotFoundException {
      float bestVariance = 0.38F;
      int bestMatch = -1;
      int max = PATTERNS.length;

      for(int i = 0; i < max; ++i) {
         int[] pattern = PATTERNS[i];
         float variance;
         if ((variance = patternMatchVariance(counters, pattern, 0.78F)) < bestVariance) {
            bestVariance = variance;
            bestMatch = i;
         }
      }

      if (bestMatch >= 0) {
         return bestMatch;
      } else {
         throw NotFoundException.getNotFoundInstance();
      }
   }
}
