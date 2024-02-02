package com.google.zxing.aztec;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.Writer;
import com.google.zxing.aztec.encoder.AztecCode;
import com.google.zxing.aztec.encoder.Encoder;
import com.google.zxing.common.BitMatrix;
import java.nio.charset.Charset;
import java.util.Map;

public final class AztecWriter implements Writer {
   private static final Charset DEFAULT_CHARSET = Charset.forName("ISO-8859-1");

   public BitMatrix encode(String contents, BarcodeFormat format, int width, int height) {
      return this.encode(contents, format, width, height, (Map)null);
   }

   public BitMatrix encode(String contents, BarcodeFormat format, int width, int height, Map<EncodeHintType, ?> hints) {
      Charset charset = DEFAULT_CHARSET;
      int eccPercent = 33;
      int layers = 0;
      if (hints != null) {
         if (hints.containsKey(EncodeHintType.CHARACTER_SET)) {
            charset = Charset.forName(hints.get(EncodeHintType.CHARACTER_SET).toString());
         }

         if (hints.containsKey(EncodeHintType.ERROR_CORRECTION)) {
            eccPercent = Integer.parseInt(hints.get(EncodeHintType.ERROR_CORRECTION).toString());
         }

         if (hints.containsKey(EncodeHintType.AZTEC_LAYERS)) {
            layers = Integer.parseInt(hints.get(EncodeHintType.AZTEC_LAYERS).toString());
         }
      }

      return encode(contents, format, width, height, charset, eccPercent, layers);
   }

   private static BitMatrix encode(String contents, BarcodeFormat format, int width, int height, Charset charset, int eccPercent, int layers) {
      if (format != BarcodeFormat.AZTEC) {
         throw new IllegalArgumentException("Can only encode AZTEC, but got " + format);
      } else {
         return renderResult(Encoder.encode(contents.getBytes(charset), eccPercent, layers), width, height);
      }
   }

   private static BitMatrix renderResult(AztecCode code, int width, int height) {
      BitMatrix input;
      if ((input = code.getMatrix()) == null) {
         throw new IllegalStateException();
      } else {
         int inputWidth = input.getWidth();
         int inputHeight = input.getHeight();
         int outputWidth = Math.max(width, inputWidth);
         int outputHeight = Math.max(height, inputHeight);
         int multiple = Math.min(outputWidth / inputWidth, outputHeight / inputHeight);
         int leftPadding = (outputWidth - inputWidth * multiple) / 2;
         int topPadding = (outputHeight - inputHeight * multiple) / 2;
         BitMatrix output = new BitMatrix(outputWidth, outputHeight);
         int inputY = 0;

         for(int outputY = topPadding; inputY < inputHeight; outputY += multiple) {
            int inputX = 0;

            for(int outputX = leftPadding; inputX < inputWidth; outputX += multiple) {
               if (input.get(inputX, inputY)) {
                  output.setRegion(outputX, outputY, multiple, multiple);
               }

               ++inputX;
            }

            ++inputY;
         }

         return output;
      }
   }
}
