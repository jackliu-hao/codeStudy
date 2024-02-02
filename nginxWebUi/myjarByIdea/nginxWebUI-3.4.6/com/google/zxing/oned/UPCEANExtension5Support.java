package com.google.zxing.oned;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.ResultMetadataType;
import com.google.zxing.ResultPoint;
import com.google.zxing.common.BitArray;
import java.util.EnumMap;
import java.util.Map;

final class UPCEANExtension5Support {
   private static final int[] CHECK_DIGIT_ENCODINGS = new int[]{24, 20, 18, 17, 12, 6, 3, 10, 9, 5};
   private final int[] decodeMiddleCounters = new int[4];
   private final StringBuilder decodeRowStringBuffer = new StringBuilder();

   Result decodeRow(int rowNumber, BitArray row, int[] extensionStartRange) throws NotFoundException {
      StringBuilder result;
      (result = this.decodeRowStringBuffer).setLength(0);
      int end = this.decodeMiddle(row, extensionStartRange, result);
      String resultString;
      Map<ResultMetadataType, Object> extensionData = parseExtensionString(resultString = result.toString());
      Result extensionResult = new Result(resultString, (byte[])null, new ResultPoint[]{new ResultPoint((float)(extensionStartRange[0] + extensionStartRange[1]) / 2.0F, (float)rowNumber), new ResultPoint((float)end, (float)rowNumber)}, BarcodeFormat.UPC_EAN_EXTENSION);
      if (extensionData != null) {
         extensionResult.putAllMetadata(extensionData);
      }

      return extensionResult;
   }

   private int decodeMiddle(BitArray row, int[] startRange, StringBuilder resultString) throws NotFoundException {
      int[] counters;
      (counters = this.decodeMiddleCounters)[0] = 0;
      counters[1] = 0;
      counters[2] = 0;
      counters[3] = 0;
      int end = row.getSize();
      int rowOffset = startRange[1];
      int lgPatternFound = 0;

      int x;
      for(x = 0; x < 5 && rowOffset < end; ++x) {
         int bestMatch = UPCEANReader.decodeDigit(row, counters, rowOffset, UPCEANReader.L_AND_G_PATTERNS);
         resultString.append((char)(48 + bestMatch % 10));
         int[] var10 = counters;
         int var11 = counters.length;

         for(int var12 = 0; var12 < var11; ++var12) {
            int counter = var10[var12];
            rowOffset += counter;
         }

         if (bestMatch >= 10) {
            lgPatternFound |= 1 << 4 - x;
         }

         if (x != 4) {
            rowOffset = row.getNextSet(rowOffset);
            rowOffset = row.getNextUnset(rowOffset);
         }
      }

      if (resultString.length() != 5) {
         throw NotFoundException.getNotFoundInstance();
      } else {
         x = determineCheckDigit(lgPatternFound);
         if (extensionChecksum(resultString.toString()) != x) {
            throw NotFoundException.getNotFoundInstance();
         } else {
            return rowOffset;
         }
      }
   }

   private static int extensionChecksum(CharSequence s) {
      int length = s.length();
      int sum = 0;

      int i;
      for(i = length - 2; i >= 0; i -= 2) {
         sum += s.charAt(i) - 48;
      }

      sum *= 3;

      for(i = length - 1; i >= 0; i -= 2) {
         sum += s.charAt(i) - 48;
      }

      return sum * 3 % 10;
   }

   private static int determineCheckDigit(int lgPatternFound) throws NotFoundException {
      for(int d = 0; d < 10; ++d) {
         if (lgPatternFound == CHECK_DIGIT_ENCODINGS[d]) {
            return d;
         }
      }

      throw NotFoundException.getNotFoundInstance();
   }

   private static Map<ResultMetadataType, Object> parseExtensionString(String raw) {
      if (raw.length() != 5) {
         return null;
      } else {
         String value;
         if ((value = parseExtension5String(raw)) == null) {
            return null;
         } else {
            EnumMap result;
            (result = new EnumMap(ResultMetadataType.class)).put(ResultMetadataType.SUGGESTED_PRICE, value);
            return result;
         }
      }
   }

   private static String parseExtension5String(String raw) {
      String currency;
      switch (raw.charAt(0)) {
         case '0':
            currency = "£";
            break;
         case '5':
            currency = "$";
            break;
         case '9':
            if ("90000".equals(raw)) {
               return null;
            }

            if ("99991".equals(raw)) {
               return "0.00";
            }

            if ("99990".equals(raw)) {
               return "Used";
            }

            currency = "";
            break;
         default:
            currency = "";
      }

      int rawAmount;
      String unitsString = String.valueOf((rawAmount = Integer.parseInt(raw.substring(1))) / 100);
      int hundredths;
      String hundredthsString = (hundredths = rawAmount % 100) < 10 ? "0" + hundredths : String.valueOf(hundredths);
      return currency + unitsString + '.' + hundredthsString;
   }
}
