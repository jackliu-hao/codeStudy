package com.google.zxing.oned;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import java.util.Map;

public final class Code39Writer extends OneDimensionalCodeWriter {
   public BitMatrix encode(String contents, BarcodeFormat format, int width, int height, Map<EncodeHintType, ?> hints) throws WriterException {
      if (format != BarcodeFormat.CODE_39) {
         throw new IllegalArgumentException("Can only encode CODE_39, but got " + format);
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
         int codeWidth = length + 25;

         int pos;
         int[] narrowWhite;
         int indexInString;
         for(int i = 0; i < length; ++i) {
            if ((pos = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ-. *$/+%".indexOf(contents.charAt(i))) < 0) {
               throw new IllegalArgumentException("Bad contents: " + contents);
            }

            toIntArray(Code39Reader.CHARACTER_ENCODINGS[pos], widths);
            narrowWhite = widths;

            for(indexInString = 0; indexInString < 9; ++indexInString) {
               int width = narrowWhite[indexInString];
               codeWidth += width;
            }
         }

         boolean[] result = new boolean[codeWidth];
         toIntArray(Code39Reader.ASTERISK_ENCODING, widths);
         pos = appendPattern(result, 0, widths, true);
         narrowWhite = new int[]{1};
         pos += appendPattern(result, pos, narrowWhite, false);

         for(int i = 0; i < length; ++i) {
            indexInString = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ-. *$/+%".indexOf(contents.charAt(i));
            toIntArray(Code39Reader.CHARACTER_ENCODINGS[indexInString], widths);
            pos = (pos += appendPattern(result, pos, widths, true)) + appendPattern(result, pos, narrowWhite, false);
         }

         toIntArray(Code39Reader.ASTERISK_ENCODING, widths);
         appendPattern(result, pos, widths, true);
         return result;
      }
   }

   private static void toIntArray(int a, int[] toReturn) {
      for(int i = 0; i < 9; ++i) {
         int temp = a & 1 << 8 - i;
         toReturn[i] = temp == 0 ? 1 : 2;
      }

   }
}
