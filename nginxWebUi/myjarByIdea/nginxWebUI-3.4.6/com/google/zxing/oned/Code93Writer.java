package com.google.zxing.oned;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import java.util.Map;

public class Code93Writer extends OneDimensionalCodeWriter {
   public BitMatrix encode(String contents, BarcodeFormat format, int width, int height, Map<EncodeHintType, ?> hints) throws WriterException {
      if (format != BarcodeFormat.CODE_93) {
         throw new IllegalArgumentException("Can only encode CODE_93, but got " + format);
      } else {
         return super.encode(contents, format, width, height, hints);
      }
   }

   public boolean[] encode(String contents) {
      int length;
      if ((length = contents.length()) > 80) {
         throw new IllegalArgumentException("Requested contents should be less than 80 digits long, but got " + length);
      } else {
         int[] widths = new int[9];
         boolean[] result = new boolean[(contents.length() + 2 + 2) * 9 + 1];
         toIntArray(Code93Reader.CHARACTER_ENCODINGS[47], widths);
         int pos = appendPattern(result, 0, widths, true);

         int check1;
         int check2;
         for(check1 = 0; check1 < length; ++check1) {
            check2 = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ-. $/+%abcd*".indexOf(contents.charAt(check1));
            toIntArray(Code93Reader.CHARACTER_ENCODINGS[check2], widths);
            pos += appendPattern(result, pos, widths, true);
         }

         check1 = computeChecksumIndex(contents, 20);
         toIntArray(Code93Reader.CHARACTER_ENCODINGS[check1], widths);
         pos += appendPattern(result, pos, widths, true);
         check2 = computeChecksumIndex(contents + "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ-. $/+%abcd*".charAt(check1), 15);
         toIntArray(Code93Reader.CHARACTER_ENCODINGS[check2], widths);
         pos += appendPattern(result, pos, widths, true);
         toIntArray(Code93Reader.CHARACTER_ENCODINGS[47], widths);
         pos += appendPattern(result, pos, widths, true);
         result[pos] = true;
         return result;
      }
   }

   private static void toIntArray(int a, int[] toReturn) {
      for(int i = 0; i < 9; ++i) {
         int temp = a & 1 << 8 - i;
         toReturn[i] = temp == 0 ? 0 : 1;
      }

   }

   protected static int appendPattern(boolean[] target, int pos, int[] pattern, boolean startColor) {
      int[] var4 = pattern;
      int var5 = pattern.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         int bit = var4[var6];
         target[pos++] = bit != 0;
      }

      return 9;
   }

   private static int computeChecksumIndex(String contents, int maxWeight) {
      int weight = 1;
      int total = 0;

      for(int i = contents.length() - 1; i >= 0; --i) {
         int indexInString = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ-. $/+%abcd*".indexOf(contents.charAt(i));
         total += indexInString * weight;
         ++weight;
         if (weight > maxWeight) {
            weight = 1;
         }
      }

      return total % 47;
   }
}
