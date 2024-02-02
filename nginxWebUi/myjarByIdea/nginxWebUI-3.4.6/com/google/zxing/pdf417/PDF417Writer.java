package com.google.zxing.pdf417;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.Writer;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.pdf417.encoder.Compaction;
import com.google.zxing.pdf417.encoder.Dimensions;
import com.google.zxing.pdf417.encoder.PDF417;
import java.nio.charset.Charset;
import java.util.Map;

public final class PDF417Writer implements Writer {
   static final int WHITE_SPACE = 30;
   static final int DEFAULT_ERROR_CORRECTION_LEVEL = 2;

   public BitMatrix encode(String contents, BarcodeFormat format, int width, int height, Map<EncodeHintType, ?> hints) throws WriterException {
      if (format != BarcodeFormat.PDF_417) {
         throw new IllegalArgumentException("Can only encode PDF_417, but got " + format);
      } else {
         PDF417 encoder = new PDF417();
         int margin = 30;
         int errorCorrectionLevel = 2;
         if (hints != null) {
            if (hints.containsKey(EncodeHintType.PDF417_COMPACT)) {
               encoder.setCompact(Boolean.valueOf(hints.get(EncodeHintType.PDF417_COMPACT).toString()));
            }

            if (hints.containsKey(EncodeHintType.PDF417_COMPACTION)) {
               encoder.setCompaction(Compaction.valueOf(hints.get(EncodeHintType.PDF417_COMPACTION).toString()));
            }

            if (hints.containsKey(EncodeHintType.PDF417_DIMENSIONS)) {
               Dimensions dimensions = (Dimensions)hints.get(EncodeHintType.PDF417_DIMENSIONS);
               encoder.setDimensions(dimensions.getMaxCols(), dimensions.getMinCols(), dimensions.getMaxRows(), dimensions.getMinRows());
            }

            if (hints.containsKey(EncodeHintType.MARGIN)) {
               margin = Integer.parseInt(hints.get(EncodeHintType.MARGIN).toString());
            }

            if (hints.containsKey(EncodeHintType.ERROR_CORRECTION)) {
               errorCorrectionLevel = Integer.parseInt(hints.get(EncodeHintType.ERROR_CORRECTION).toString());
            }

            if (hints.containsKey(EncodeHintType.CHARACTER_SET)) {
               Charset encoding = Charset.forName(hints.get(EncodeHintType.CHARACTER_SET).toString());
               encoder.setEncoding(encoding);
            }
         }

         return bitMatrixFromEncoder(encoder, contents, errorCorrectionLevel, width, height, margin);
      }
   }

   public BitMatrix encode(String contents, BarcodeFormat format, int width, int height) throws WriterException {
      return this.encode(contents, format, width, height, (Map)null);
   }

   private static BitMatrix bitMatrixFromEncoder(PDF417 encoder, String contents, int errorCorrectionLevel, int width, int height, int margin) throws WriterException {
      encoder.generateBarcodeLogic(contents, errorCorrectionLevel);
      byte[][] originalScale = encoder.getBarcodeMatrix().getScaledMatrix(1, 4);
      boolean rotated = false;
      if (height > width ^ originalScale[0].length < originalScale.length) {
         originalScale = rotateArray(originalScale);
         rotated = true;
      }

      int scaleX = width / originalScale[0].length;
      int scaleY = height / originalScale.length;
      int scale;
      if (scaleX < scaleY) {
         scale = scaleX;
      } else {
         scale = scaleY;
      }

      if (scale > 1) {
         byte[][] scaledMatrix = encoder.getBarcodeMatrix().getScaledMatrix(scale, scale << 2);
         if (rotated) {
            scaledMatrix = rotateArray(scaledMatrix);
         }

         return bitMatrixFrombitArray(scaledMatrix, margin);
      } else {
         return bitMatrixFrombitArray(originalScale, margin);
      }
   }

   private static BitMatrix bitMatrixFrombitArray(byte[][] input, int margin) {
      BitMatrix output;
      (output = new BitMatrix(input[0].length + 2 * margin, input.length + 2 * margin)).clear();
      int y = 0;

      for(int yOutput = output.getHeight() - margin - 1; y < input.length; --yOutput) {
         for(int x = 0; x < input[0].length; ++x) {
            if (input[y][x] == 1) {
               output.set(x + margin, yOutput);
            }
         }

         ++y;
      }

      return output;
   }

   private static byte[][] rotateArray(byte[][] bitarray) {
      byte[][] temp = new byte[bitarray[0].length][bitarray.length];

      for(int ii = 0; ii < bitarray.length; ++ii) {
         int inverseii = bitarray.length - ii - 1;

         for(int jj = 0; jj < bitarray[0].length; ++jj) {
            temp[jj][inverseii] = bitarray[ii][jj];
         }
      }

      return temp;
   }
}
