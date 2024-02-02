package com.google.zxing.oned;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.ChecksumException;
import com.google.zxing.DecodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.NotFoundException;
import com.google.zxing.ReaderException;
import com.google.zxing.Result;
import com.google.zxing.ResultMetadataType;
import com.google.zxing.ResultPoint;
import com.google.zxing.ResultPointCallback;
import com.google.zxing.common.BitArray;
import java.util.Arrays;
import java.util.Map;

public abstract class UPCEANReader extends OneDReader {
   private static final float MAX_AVG_VARIANCE = 0.48F;
   private static final float MAX_INDIVIDUAL_VARIANCE = 0.7F;
   static final int[] START_END_PATTERN = new int[]{1, 1, 1};
   static final int[] MIDDLE_PATTERN = new int[]{1, 1, 1, 1, 1};
   static final int[] END_PATTERN = new int[]{1, 1, 1, 1, 1, 1};
   static final int[][] L_PATTERNS = new int[][]{{3, 2, 1, 1}, {2, 2, 2, 1}, {2, 1, 2, 2}, {1, 4, 1, 1}, {1, 1, 3, 2}, {1, 2, 3, 1}, {1, 1, 1, 4}, {1, 3, 1, 2}, {1, 2, 1, 3}, {3, 1, 1, 2}};
   static final int[][] L_AND_G_PATTERNS = new int[20][];
   private final StringBuilder decodeRowStringBuffer = new StringBuilder(20);
   private final UPCEANExtensionSupport extensionReader = new UPCEANExtensionSupport();
   private final EANManufacturerOrgSupport eanManSupport = new EANManufacturerOrgSupport();

   protected UPCEANReader() {
   }

   static int[] findStartGuardPattern(BitArray row) throws NotFoundException {
      boolean foundStart = false;
      int[] startRange = null;
      int nextStart = 0;
      int[] counters = new int[START_END_PATTERN.length];

      while(!foundStart) {
         Arrays.fill(counters, 0, START_END_PATTERN.length, 0);
         int start = (startRange = findGuardPattern(row, nextStart, false, START_END_PATTERN, counters))[0];
         nextStart = startRange[1];
         int var10001 = nextStart - start;
         int var10000 = start - (nextStart - start);
         int quietStart = start - var10001;
         if (var10000 >= 0) {
            foundStart = row.isRange(quietStart, start, false);
         }
      }

      return startRange;
   }

   public Result decodeRow(int rowNumber, BitArray row, Map<DecodeHintType, ?> hints) throws NotFoundException, ChecksumException, FormatException {
      return this.decodeRow(rowNumber, row, findStartGuardPattern(row), hints);
   }

   public Result decodeRow(int rowNumber, BitArray row, int[] startGuardRange, Map<DecodeHintType, ?> hints) throws NotFoundException, ChecksumException, FormatException {
      ResultPointCallback var10000 = hints == null ? null : (ResultPointCallback)hints.get(DecodeHintType.NEED_RESULT_POINT_CALLBACK);
      ResultPointCallback resultPointCallback = var10000;
      if (var10000 != null) {
         resultPointCallback.foundPossibleResultPoint(new ResultPoint((float)(startGuardRange[0] + startGuardRange[1]) / 2.0F, (float)rowNumber));
      }

      StringBuilder result;
      (result = this.decodeRowStringBuffer).setLength(0);
      int endStart = this.decodeMiddle(row, startGuardRange, result);
      if (resultPointCallback != null) {
         resultPointCallback.foundPossibleResultPoint(new ResultPoint((float)endStart, (float)rowNumber));
      }

      int[] endRange = this.decodeEnd(row, endStart);
      if (resultPointCallback != null) {
         resultPointCallback.foundPossibleResultPoint(new ResultPoint((float)(endRange[0] + endRange[1]) / 2.0F, (float)rowNumber));
      }

      int end;
      int quietEnd;
      if ((quietEnd = endRange[1] + ((end = endRange[1]) - endRange[0])) < row.getSize() && row.isRange(end, quietEnd, false)) {
         String resultString;
         if ((resultString = result.toString()).length() < 8) {
            throw FormatException.getFormatInstance();
         } else if (!this.checkChecksum(resultString)) {
            throw ChecksumException.getChecksumInstance();
         } else {
            float left = (float)(startGuardRange[1] + startGuardRange[0]) / 2.0F;
            float right = (float)(endRange[1] + endRange[0]) / 2.0F;
            BarcodeFormat format = this.getBarcodeFormat();
            Result decodeResult = new Result(resultString, (byte[])null, new ResultPoint[]{new ResultPoint(left, (float)rowNumber), new ResultPoint(right, (float)rowNumber)}, format);
            int extensionLength = 0;

            try {
               Result extensionResult = this.extensionReader.decodeRow(rowNumber, row, endRange[1]);
               decodeResult.putMetadata(ResultMetadataType.UPC_EAN_EXTENSION, extensionResult.getText());
               decodeResult.putAllMetadata(extensionResult.getResultMetadata());
               decodeResult.addResultPoints(extensionResult.getResultPoints());
               extensionLength = extensionResult.getText().length();
            } catch (ReaderException var23) {
            }

            int[] var24 = hints == null ? null : (int[])((int[])hints.get(DecodeHintType.ALLOWED_EAN_EXTENSIONS));
            int[] allowedExtensions = var24;
            if (var24 != null) {
               boolean valid = false;
               int[] var19 = allowedExtensions;
               int var20 = allowedExtensions.length;

               for(int var21 = 0; var21 < var20; ++var21) {
                  int length = var19[var21];
                  if (extensionLength == length) {
                     valid = true;
                     break;
                  }
               }

               if (!valid) {
                  throw NotFoundException.getNotFoundInstance();
               }
            }

            String countryID;
            if ((format == BarcodeFormat.EAN_13 || format == BarcodeFormat.UPC_A) && (countryID = this.eanManSupport.lookupCountryIdentifier(resultString)) != null) {
               decodeResult.putMetadata(ResultMetadataType.POSSIBLE_COUNTRY, countryID);
            }

            return decodeResult;
         }
      } else {
         throw NotFoundException.getNotFoundInstance();
      }
   }

   boolean checkChecksum(String s) throws FormatException {
      return checkStandardUPCEANChecksum(s);
   }

   static boolean checkStandardUPCEANChecksum(CharSequence s) throws FormatException {
      int length;
      if ((length = s.length()) == 0) {
         return false;
      } else {
         int sum = 0;

         int i;
         int digit;
         for(i = length - 2; i >= 0; i -= 2) {
            if ((digit = s.charAt(i) - 48) < 0 || digit > 9) {
               throw FormatException.getFormatInstance();
            }

            sum += digit;
         }

         sum *= 3;

         for(i = length - 1; i >= 0; i -= 2) {
            if ((digit = s.charAt(i) - 48) < 0 || digit > 9) {
               throw FormatException.getFormatInstance();
            }

            sum += digit;
         }

         if (sum % 10 == 0) {
            return true;
         } else {
            return false;
         }
      }
   }

   int[] decodeEnd(BitArray row, int endStart) throws NotFoundException {
      return findGuardPattern(row, endStart, false, START_END_PATTERN);
   }

   static int[] findGuardPattern(BitArray row, int rowOffset, boolean whiteFirst, int[] pattern) throws NotFoundException {
      return findGuardPattern(row, rowOffset, whiteFirst, pattern, new int[pattern.length]);
   }

   private static int[] findGuardPattern(BitArray row, int rowOffset, boolean whiteFirst, int[] pattern, int[] counters) throws NotFoundException {
      int width = row.getSize();
      rowOffset = whiteFirst ? row.getNextUnset(rowOffset) : row.getNextSet(rowOffset);
      int counterPosition = 0;
      int patternStart = rowOffset;
      int patternLength = pattern.length;
      boolean isWhite = whiteFirst;

      for(int x = rowOffset; x < width; ++x) {
         if (row.get(x) ^ isWhite) {
            int var10002 = counters[counterPosition]++;
         } else {
            if (counterPosition == patternLength - 1) {
               if (patternMatchVariance(counters, pattern, 0.7F) < 0.48F) {
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

   static int decodeDigit(BitArray row, int[] counters, int rowOffset, int[][] patterns) throws NotFoundException {
      recordPattern(row, rowOffset, counters);
      float bestVariance = 0.48F;
      int bestMatch = -1;
      int max = patterns.length;

      for(int i = 0; i < max; ++i) {
         int[] pattern = patterns[i];
         float variance;
         if ((variance = patternMatchVariance(counters, pattern, 0.7F)) < bestVariance) {
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

   abstract BarcodeFormat getBarcodeFormat();

   protected abstract int decodeMiddle(BitArray var1, int[] var2, StringBuilder var3) throws NotFoundException;

   static {
      System.arraycopy(L_PATTERNS, 0, L_AND_G_PATTERNS, 0, 10);

      for(int i = 10; i < 20; ++i) {
         int[] widths;
         int[] reversedWidths = new int[(widths = L_PATTERNS[i - 10]).length];

         for(int j = 0; j < widths.length; ++j) {
            reversedWidths[j] = widths[widths.length - j - 1];
         }

         L_AND_G_PATTERNS[i] = reversedWidths;
      }

   }
}
