package com.google.zxing.oned;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import java.util.Map;

public final class UPCEWriter extends UPCEANWriter {
   private static final int CODE_WIDTH = 51;

   public BitMatrix encode(String contents, BarcodeFormat format, int width, int height, Map<EncodeHintType, ?> hints) throws WriterException {
      if (format != BarcodeFormat.UPC_E) {
         throw new IllegalArgumentException("Can only encode UPC_E, but got " + format);
      } else {
         return super.encode(contents, format, width, height, hints);
      }
   }

   public boolean[] encode(String contents) {
      if (contents.length() != 8) {
         throw new IllegalArgumentException("Requested contents should be 8 digits long, but got " + contents.length());
      } else {
         int checkDigit = Integer.parseInt(contents.substring(7, 8));
         int parities = UPCEReader.CHECK_DIGIT_ENCODINGS[checkDigit];
         boolean[] result = new boolean[51];
         int pos = 0 + appendPattern(result, 0, UPCEANReader.START_END_PATTERN, true);

         for(int i = 1; i <= 6; ++i) {
            int digit = Integer.parseInt(contents.substring(i, i + 1));
            if ((parities >> 6 - i & 1) == 1) {
               digit += 10;
            }

            pos += appendPattern(result, pos, UPCEANReader.L_AND_G_PATTERNS[digit], false);
         }

         appendPattern(result, pos, UPCEANReader.END_PATTERN, false);
         return result;
      }
   }
}
