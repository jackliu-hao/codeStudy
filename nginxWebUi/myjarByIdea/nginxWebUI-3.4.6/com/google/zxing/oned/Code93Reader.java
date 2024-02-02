package com.google.zxing.oned;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.ChecksumException;
import com.google.zxing.DecodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.ResultPoint;
import com.google.zxing.common.BitArray;
import java.util.Arrays;
import java.util.Map;

public final class Code93Reader extends OneDReader {
   static final String ALPHABET_STRING = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ-. $/+%abcd*";
   private static final char[] ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ-. $/+%abcd*".toCharArray();
   static final int[] CHARACTER_ENCODINGS;
   private static final int ASTERISK_ENCODING;
   private final StringBuilder decodeRowResult = new StringBuilder(20);
   private final int[] counters = new int[6];

   public Result decodeRow(int rowNumber, BitArray row, Map<DecodeHintType, ?> hints) throws NotFoundException, ChecksumException, FormatException {
      int[] start = this.findAsteriskPattern(row);
      int nextStart = row.getNextSet(start[1]);
      int end = row.getSize();
      int[] theCounters;
      Arrays.fill(theCounters = this.counters, 0);
      StringBuilder result;
      (result = this.decodeRowResult).setLength(0);

      char decodedChar;
      int lastStart;
      int lastPatternSize;
      int[] var12;
      int var13;
      int var14;
      int counter;
      do {
         recordPattern(row, nextStart, theCounters);
         if ((lastPatternSize = toPattern(theCounters)) < 0) {
            throw NotFoundException.getNotFoundInstance();
         }

         decodedChar = patternToChar(lastPatternSize);
         result.append(decodedChar);
         lastStart = nextStart;
         var12 = theCounters;
         var13 = theCounters.length;

         for(var14 = 0; var14 < var13; ++var14) {
            counter = var12[var14];
            nextStart += counter;
         }

         nextStart = row.getNextSet(nextStart);
      } while(decodedChar != '*');

      result.deleteCharAt(result.length() - 1);
      lastPatternSize = 0;
      var12 = theCounters;
      var13 = theCounters.length;

      for(var14 = 0; var14 < var13; ++var14) {
         counter = var12[var14];
         lastPatternSize += counter;
      }

      if (nextStart != end && row.get(nextStart)) {
         if (result.length() < 2) {
            throw NotFoundException.getNotFoundInstance();
         } else {
            checkChecksums(result);
            result.setLength(result.length() - 2);
            String resultString = decodeExtended(result);
            float left = (float)(start[1] + start[0]) / 2.0F;
            float right = (float)lastStart + (float)lastPatternSize / 2.0F;
            return new Result(resultString, (byte[])null, new ResultPoint[]{new ResultPoint(left, (float)rowNumber), new ResultPoint(right, (float)rowNumber)}, BarcodeFormat.CODE_93);
         }
      } else {
         throw NotFoundException.getNotFoundInstance();
      }
   }

   private int[] findAsteriskPattern(BitArray row) throws NotFoundException {
      int width = row.getSize();
      int rowOffset = row.getNextSet(0);
      Arrays.fill(this.counters, 0);
      int[] theCounters = this.counters;
      int patternStart = rowOffset;
      boolean isWhite = false;
      int patternLength = theCounters.length;
      int counterPosition = 0;

      for(int i = rowOffset; i < width; ++i) {
         if (row.get(i) ^ isWhite) {
            int var10002 = theCounters[counterPosition]++;
         } else {
            if (counterPosition == patternLength - 1) {
               if (toPattern(theCounters) == ASTERISK_ENCODING) {
                  return new int[]{patternStart, i};
               }

               patternStart += theCounters[0] + theCounters[1];
               System.arraycopy(theCounters, 2, theCounters, 0, patternLength - 2);
               theCounters[patternLength - 2] = 0;
               theCounters[patternLength - 1] = 0;
               --counterPosition;
            } else {
               ++counterPosition;
            }

            theCounters[counterPosition] = 1;
            isWhite = !isWhite;
         }
      }

      throw NotFoundException.getNotFoundInstance();
   }

   private static int toPattern(int[] counters) {
      int sum = 0;
      int[] var2 = counters;
      int max = counters.length;

      int i;
      int scaled;
      for(i = 0; i < max; ++i) {
         scaled = var2[i];
         sum += scaled;
      }

      int pattern = 0;
      max = counters.length;

      for(i = 0; i < max; ++i) {
         if ((scaled = Math.round((float)counters[i] * 9.0F / (float)sum)) <= 0 || scaled > 4) {
            return -1;
         }

         if ((i & 1) == 0) {
            for(int j = 0; j < scaled; ++j) {
               pattern = pattern << 1 | 1;
            }
         } else {
            pattern <<= scaled;
         }
      }

      return pattern;
   }

   private static char patternToChar(int pattern) throws NotFoundException {
      for(int i = 0; i < CHARACTER_ENCODINGS.length; ++i) {
         if (CHARACTER_ENCODINGS[i] == pattern) {
            return ALPHABET[i];
         }
      }

      throw NotFoundException.getNotFoundInstance();
   }

   private static String decodeExtended(CharSequence encoded) throws FormatException {
      int length = encoded.length();
      StringBuilder decoded = new StringBuilder(length);

      for(int i = 0; i < length; ++i) {
         char c;
         if ((c = encoded.charAt(i)) >= 'a' && c <= 'd') {
            if (i >= length - 1) {
               throw FormatException.getFormatInstance();
            }

            char next = encoded.charAt(i + 1);
            char decodedChar = 0;
            switch (c) {
               case 'a':
                  if (next >= 'A' && next <= 'Z') {
                     decodedChar = (char)(next - 64);
                     break;
                  }

                  throw FormatException.getFormatInstance();
               case 'b':
                  if (next >= 'A' && next <= 'E') {
                     decodedChar = (char)(next - 38);
                  } else if (next >= 'F' && next <= 'J') {
                     decodedChar = (char)(next - 11);
                  } else if (next >= 'K' && next <= 'O') {
                     decodedChar = (char)(next + 16);
                  } else if (next >= 'P' && next <= 'S') {
                     decodedChar = (char)(next + 43);
                  } else {
                     if (next >= 'T' && next <= 'Z') {
                        decodedChar = 127;
                        break;
                     }

                     throw FormatException.getFormatInstance();
                  }
                  break;
               case 'c':
                  if (next >= 'A' && next <= 'O') {
                     decodedChar = (char)(next - 32);
                  } else {
                     if (next != 'Z') {
                        throw FormatException.getFormatInstance();
                     }

                     decodedChar = ':';
                  }
                  break;
               case 'd':
                  if (next < 'A' || next > 'Z') {
                     throw FormatException.getFormatInstance();
                  }

                  decodedChar = (char)(next + 32);
            }

            decoded.append(decodedChar);
            ++i;
         } else {
            decoded.append(c);
         }
      }

      return decoded.toString();
   }

   private static void checkChecksums(CharSequence result) throws ChecksumException {
      int length = result.length();
      checkOneChecksum(result, length - 2, 20);
      checkOneChecksum(result, length - 1, 15);
   }

   private static void checkOneChecksum(CharSequence result, int checkPosition, int weightMax) throws ChecksumException {
      int weight = 1;
      int total = 0;

      for(int i = checkPosition - 1; i >= 0; --i) {
         total += weight * "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ-. $/+%abcd*".indexOf(result.charAt(i));
         ++weight;
         if (weight > weightMax) {
            weight = 1;
         }
      }

      if (result.charAt(checkPosition) != ALPHABET[total % 47]) {
         throw ChecksumException.getChecksumInstance();
      }
   }

   static {
      ASTERISK_ENCODING = (CHARACTER_ENCODINGS = new int[]{276, 328, 324, 322, 296, 292, 290, 336, 274, 266, 424, 420, 418, 404, 402, 394, 360, 356, 354, 308, 282, 344, 332, 326, 300, 278, 436, 434, 428, 422, 406, 410, 364, 358, 310, 314, 302, 468, 466, 458, 366, 374, 430, 294, 474, 470, 306, 350})[47];
   }
}
