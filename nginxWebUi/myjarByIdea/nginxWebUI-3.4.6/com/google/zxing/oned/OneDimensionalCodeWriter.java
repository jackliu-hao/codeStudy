package com.google.zxing.oned;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.Writer;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import java.util.Map;

public abstract class OneDimensionalCodeWriter implements Writer {
   public final BitMatrix encode(String contents, BarcodeFormat format, int width, int height) throws WriterException {
      return this.encode(contents, format, width, height, (Map)null);
   }

   public BitMatrix encode(String contents, BarcodeFormat format, int width, int height, Map<EncodeHintType, ?> hints) throws WriterException {
      if (contents.isEmpty()) {
         throw new IllegalArgumentException("Found empty contents");
      } else if (width >= 0 && height >= 0) {
         int sidesMargin = this.getDefaultMargin();
         if (hints != null && hints.containsKey(EncodeHintType.MARGIN)) {
            sidesMargin = Integer.parseInt(hints.get(EncodeHintType.MARGIN).toString());
         }

         return renderResult(this.encode(contents), width, height, sidesMargin);
      } else {
         throw new IllegalArgumentException("Negative size is not allowed. Input: " + width + 'x' + height);
      }
   }

   private static BitMatrix renderResult(boolean[] code, int width, int height, int sidesMargin) {
      int inputWidth;
      int fullWidth = (inputWidth = code.length) + sidesMargin;
      int outputWidth = Math.max(width, fullWidth);
      int outputHeight = Math.max(1, height);
      int multiple = outputWidth / fullWidth;
      int leftPadding = (outputWidth - inputWidth * multiple) / 2;
      BitMatrix output = new BitMatrix(outputWidth, outputHeight);
      int inputX = 0;

      for(int outputX = leftPadding; inputX < inputWidth; outputX += multiple) {
         if (code[inputX]) {
            output.setRegion(outputX, 0, multiple, outputHeight);
         }

         ++inputX;
      }

      return output;
   }

   protected static int appendPattern(boolean[] target, int pos, int[] pattern, boolean startColor) {
      boolean color = startColor;
      int numAdded = 0;
      int[] var6 = pattern;
      int var7 = pattern.length;

      for(int var8 = 0; var8 < var7; ++var8) {
         int len = var6[var8];

         for(int j = 0; j < len; ++j) {
            target[pos++] = color;
         }

         numAdded += len;
         color = !color;
      }

      return numAdded;
   }

   public int getDefaultMargin() {
      return 10;
   }

   public abstract boolean[] encode(String var1);
}
