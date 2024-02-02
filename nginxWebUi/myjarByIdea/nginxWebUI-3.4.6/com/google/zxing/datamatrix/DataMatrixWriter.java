package com.google.zxing.datamatrix;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Dimension;
import com.google.zxing.EncodeHintType;
import com.google.zxing.Writer;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.datamatrix.encoder.DefaultPlacement;
import com.google.zxing.datamatrix.encoder.ErrorCorrection;
import com.google.zxing.datamatrix.encoder.HighLevelEncoder;
import com.google.zxing.datamatrix.encoder.SymbolInfo;
import com.google.zxing.datamatrix.encoder.SymbolShapeHint;
import com.google.zxing.qrcode.encoder.ByteMatrix;
import java.util.Map;

public final class DataMatrixWriter implements Writer {
   public BitMatrix encode(String contents, BarcodeFormat format, int width, int height) {
      return this.encode(contents, format, width, height, (Map)null);
   }

   public BitMatrix encode(String contents, BarcodeFormat format, int width, int height, Map<EncodeHintType, ?> hints) {
      if (contents.isEmpty()) {
         throw new IllegalArgumentException("Found empty contents");
      } else if (format != BarcodeFormat.DATA_MATRIX) {
         throw new IllegalArgumentException("Can only encode DATA_MATRIX, but got " + format);
      } else if (width >= 0 && height >= 0) {
         SymbolShapeHint shape = SymbolShapeHint.FORCE_NONE;
         Dimension minSize = null;
         Dimension maxSize = null;
         if (hints != null) {
            SymbolShapeHint requestedShape;
            if ((requestedShape = (SymbolShapeHint)hints.get(EncodeHintType.DATA_MATRIX_SHAPE)) != null) {
               shape = requestedShape;
            }

            Dimension requestedMinSize;
            if ((requestedMinSize = (Dimension)hints.get(EncodeHintType.MIN_SIZE)) != null) {
               minSize = requestedMinSize;
            }

            Dimension requestedMaxSize;
            if ((requestedMaxSize = (Dimension)hints.get(EncodeHintType.MAX_SIZE)) != null) {
               maxSize = requestedMaxSize;
            }
         }

         String encoded;
         SymbolInfo symbolInfo = SymbolInfo.lookup((encoded = HighLevelEncoder.encodeHighLevel(contents, shape, minSize, maxSize)).length(), shape, minSize, maxSize, true);
         String codewords = ErrorCorrection.encodeECC200(encoded, symbolInfo);
         DefaultPlacement placement;
         (placement = new DefaultPlacement(codewords, symbolInfo.getSymbolDataWidth(), symbolInfo.getSymbolDataHeight())).place();
         return encodeLowLevel(placement, symbolInfo);
      } else {
         throw new IllegalArgumentException("Requested dimensions are too small: " + width + 'x' + height);
      }
   }

   private static BitMatrix encodeLowLevel(DefaultPlacement placement, SymbolInfo symbolInfo) {
      int symbolWidth = symbolInfo.getSymbolDataWidth();
      int symbolHeight = symbolInfo.getSymbolDataHeight();
      ByteMatrix matrix = new ByteMatrix(symbolInfo.getSymbolWidth(), symbolInfo.getSymbolHeight());
      int matrixY = 0;

      for(int y = 0; y < symbolHeight; ++y) {
         int matrixX;
         int x;
         if (y % symbolInfo.matrixHeight == 0) {
            matrixX = 0;

            for(x = 0; x < symbolInfo.getSymbolWidth(); ++x) {
               matrix.set(matrixX, matrixY, x % 2 == 0);
               ++matrixX;
            }

            ++matrixY;
         }

         matrixX = 0;

         for(x = 0; x < symbolWidth; ++x) {
            if (x % symbolInfo.matrixWidth == 0) {
               matrix.set(matrixX, matrixY, true);
               ++matrixX;
            }

            matrix.set(matrixX, matrixY, placement.getBit(x, y));
            ++matrixX;
            if (x % symbolInfo.matrixWidth == symbolInfo.matrixWidth - 1) {
               matrix.set(matrixX, matrixY, y % 2 == 0);
               ++matrixX;
            }
         }

         ++matrixY;
         if (y % symbolInfo.matrixHeight == symbolInfo.matrixHeight - 1) {
            matrixX = 0;

            for(x = 0; x < symbolInfo.getSymbolWidth(); ++x) {
               matrix.set(matrixX, matrixY, true);
               ++matrixX;
            }

            ++matrixY;
         }
      }

      return convertByteMatrixToBitMatrix(matrix);
   }

   private static BitMatrix convertByteMatrixToBitMatrix(ByteMatrix matrix) {
      int matrixWidgth = matrix.getWidth();
      int matrixHeight = matrix.getHeight();
      BitMatrix output;
      (output = new BitMatrix(matrixWidgth, matrixHeight)).clear();

      for(int i = 0; i < matrixWidgth; ++i) {
         for(int j = 0; j < matrixHeight; ++j) {
            if (matrix.get(i, j) == 1) {
               output.set(i, j);
            }
         }
      }

      return output;
   }
}
