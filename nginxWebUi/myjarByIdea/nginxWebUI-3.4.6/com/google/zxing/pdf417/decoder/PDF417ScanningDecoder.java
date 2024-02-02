package com.google.zxing.pdf417.decoder;

import com.google.zxing.ChecksumException;
import com.google.zxing.FormatException;
import com.google.zxing.NotFoundException;
import com.google.zxing.ResultPoint;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.DecoderResult;
import com.google.zxing.common.detector.MathUtils;
import com.google.zxing.pdf417.PDF417Common;
import com.google.zxing.pdf417.decoder.ec.ErrorCorrection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Formatter;
import java.util.List;

public final class PDF417ScanningDecoder {
   private static final int CODEWORD_SKEW_SIZE = 2;
   private static final int MAX_ERRORS = 3;
   private static final int MAX_EC_CODEWORDS = 512;
   private static final ErrorCorrection errorCorrection = new ErrorCorrection();

   private PDF417ScanningDecoder() {
   }

   public static DecoderResult decode(BitMatrix image, ResultPoint imageTopLeft, ResultPoint imageBottomLeft, ResultPoint imageTopRight, ResultPoint imageBottomRight, int minCodewordWidth, int maxCodewordWidth) throws NotFoundException, FormatException, ChecksumException {
      BoundingBox boundingBox = new BoundingBox(image, imageTopLeft, imageBottomLeft, imageTopRight, imageBottomRight);
      DetectionResultRowIndicatorColumn leftRowIndicatorColumn = null;
      DetectionResultRowIndicatorColumn rightRowIndicatorColumn = null;
      DetectionResult detectionResult = null;

      int i;
      for(i = 0; i < 2; ++i) {
         if (imageTopLeft != null) {
            leftRowIndicatorColumn = getRowIndicatorColumn(image, boundingBox, imageTopLeft, true, minCodewordWidth, maxCodewordWidth);
         }

         if (imageTopRight != null) {
            rightRowIndicatorColumn = getRowIndicatorColumn(image, boundingBox, imageTopRight, false, minCodewordWidth, maxCodewordWidth);
         }

         if ((detectionResult = merge(leftRowIndicatorColumn, rightRowIndicatorColumn)) == null) {
            throw NotFoundException.getNotFoundInstance();
         }

         if (i != 0 || detectionResult.getBoundingBox() == null || detectionResult.getBoundingBox().getMinY() >= boundingBox.getMinY() && detectionResult.getBoundingBox().getMaxY() <= boundingBox.getMaxY()) {
            detectionResult.setBoundingBox(boundingBox);
            break;
         }

         boundingBox = detectionResult.getBoundingBox();
      }

      i = detectionResult.getBarcodeColumnCount() + 1;
      detectionResult.setDetectionResultColumn(0, leftRowIndicatorColumn);
      detectionResult.setDetectionResultColumn(i, rightRowIndicatorColumn);
      boolean leftToRight = leftRowIndicatorColumn != null;

      for(int barcodeColumnCount = 1; barcodeColumnCount <= i; ++barcodeColumnCount) {
         int barcodeColumn = leftToRight ? barcodeColumnCount : i - barcodeColumnCount;
         if (detectionResult.getDetectionResultColumn(barcodeColumn) == null) {
            Object detectionResultColumn;
            if (barcodeColumn != 0 && barcodeColumn != i) {
               detectionResultColumn = new DetectionResultColumn(boundingBox);
            } else {
               detectionResultColumn = new DetectionResultRowIndicatorColumn(boundingBox, barcodeColumn == 0);
            }

            detectionResult.setDetectionResultColumn(barcodeColumn, (DetectionResultColumn)detectionResultColumn);
            int previousStartColumn = -1;

            for(int imageRow = boundingBox.getMinY(); imageRow <= boundingBox.getMaxY(); ++imageRow) {
               int startColumn;
               if ((startColumn = getStartColumn(detectionResult, barcodeColumn, imageRow, leftToRight)) < 0 || startColumn > boundingBox.getMaxX()) {
                  if (previousStartColumn == -1) {
                     continue;
                  }

                  startColumn = previousStartColumn;
               }

               Codeword codeword;
               if ((codeword = detectCodeword(image, boundingBox.getMinX(), boundingBox.getMaxX(), leftToRight, startColumn, imageRow, minCodewordWidth, maxCodewordWidth)) != null) {
                  ((DetectionResultColumn)detectionResultColumn).setCodeword(imageRow, codeword);
                  previousStartColumn = startColumn;
                  minCodewordWidth = Math.min(minCodewordWidth, codeword.getWidth());
                  maxCodewordWidth = Math.max(maxCodewordWidth, codeword.getWidth());
               }
            }
         }
      }

      return createDecoderResult(detectionResult);
   }

   private static DetectionResult merge(DetectionResultRowIndicatorColumn leftRowIndicatorColumn, DetectionResultRowIndicatorColumn rightRowIndicatorColumn) throws NotFoundException {
      if (leftRowIndicatorColumn == null && rightRowIndicatorColumn == null) {
         return null;
      } else {
         BarcodeMetadata barcodeMetadata;
         if ((barcodeMetadata = getBarcodeMetadata(leftRowIndicatorColumn, rightRowIndicatorColumn)) == null) {
            return null;
         } else {
            BoundingBox boundingBox = BoundingBox.merge(adjustBoundingBox(leftRowIndicatorColumn), adjustBoundingBox(rightRowIndicatorColumn));
            return new DetectionResult(barcodeMetadata, boundingBox);
         }
      }
   }

   private static BoundingBox adjustBoundingBox(DetectionResultRowIndicatorColumn rowIndicatorColumn) throws NotFoundException {
      if (rowIndicatorColumn == null) {
         return null;
      } else {
         int[] rowHeights;
         if ((rowHeights = rowIndicatorColumn.getRowHeights()) == null) {
            return null;
         } else {
            int maxRowHeight = getMax(rowHeights);
            int missingStartRows = 0;
            int[] var4 = rowHeights;
            int missingEndRows = rowHeights.length;

            int row;
            for(row = 0; row < missingEndRows; ++row) {
               int rowHeight = var4[row];
               missingStartRows += maxRowHeight - rowHeight;
               if (rowHeight > 0) {
                  break;
               }
            }

            Codeword[] codewords = rowIndicatorColumn.getCodewords();

            for(missingEndRows = 0; missingStartRows > 0 && codewords[missingEndRows] == null; ++missingEndRows) {
               --missingStartRows;
            }

            missingEndRows = 0;

            for(row = rowHeights.length - 1; row >= 0; --row) {
               missingEndRows += maxRowHeight - rowHeights[row];
               if (rowHeights[row] > 0) {
                  break;
               }
            }

            for(row = codewords.length - 1; missingEndRows > 0 && codewords[row] == null; --row) {
               --missingEndRows;
            }

            return rowIndicatorColumn.getBoundingBox().addMissingRows(missingStartRows, missingEndRows, rowIndicatorColumn.isLeft());
         }
      }
   }

   private static int getMax(int[] values) {
      int maxValue = -1;
      int[] var2 = values;
      int var3 = values.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         int value = var2[var4];
         maxValue = Math.max(maxValue, value);
      }

      return maxValue;
   }

   private static BarcodeMetadata getBarcodeMetadata(DetectionResultRowIndicatorColumn leftRowIndicatorColumn, DetectionResultRowIndicatorColumn rightRowIndicatorColumn) {
      BarcodeMetadata leftBarcodeMetadata;
      if (leftRowIndicatorColumn != null && (leftBarcodeMetadata = leftRowIndicatorColumn.getBarcodeMetadata()) != null) {
         BarcodeMetadata rightBarcodeMetadata;
         if (rightRowIndicatorColumn != null && (rightBarcodeMetadata = rightRowIndicatorColumn.getBarcodeMetadata()) != null) {
            return leftBarcodeMetadata.getColumnCount() != rightBarcodeMetadata.getColumnCount() && leftBarcodeMetadata.getErrorCorrectionLevel() != rightBarcodeMetadata.getErrorCorrectionLevel() && leftBarcodeMetadata.getRowCount() != rightBarcodeMetadata.getRowCount() ? null : leftBarcodeMetadata;
         } else {
            return leftBarcodeMetadata;
         }
      } else {
         return rightRowIndicatorColumn == null ? null : rightRowIndicatorColumn.getBarcodeMetadata();
      }
   }

   private static DetectionResultRowIndicatorColumn getRowIndicatorColumn(BitMatrix image, BoundingBox boundingBox, ResultPoint startPoint, boolean leftToRight, int minCodewordWidth, int maxCodewordWidth) {
      DetectionResultRowIndicatorColumn rowIndicatorColumn = new DetectionResultRowIndicatorColumn(boundingBox, leftToRight);

      for(int i = 0; i < 2; ++i) {
         int increment = i == 0 ? 1 : -1;
         int startColumn = (int)startPoint.getX();

         for(int imageRow = (int)startPoint.getY(); imageRow <= boundingBox.getMaxY() && imageRow >= boundingBox.getMinY(); imageRow += increment) {
            Codeword codeword;
            if ((codeword = detectCodeword(image, 0, image.getWidth(), leftToRight, startColumn, imageRow, minCodewordWidth, maxCodewordWidth)) != null) {
               rowIndicatorColumn.setCodeword(imageRow, codeword);
               if (leftToRight) {
                  startColumn = codeword.getStartX();
               } else {
                  startColumn = codeword.getEndX();
               }
            }
         }
      }

      return rowIndicatorColumn;
   }

   private static void adjustCodewordCount(DetectionResult detectionResult, BarcodeValue[][] barcodeMatrix) throws NotFoundException {
      int[] numberOfCodewords = barcodeMatrix[0][1].getValue();
      int calculatedNumberOfCodewords = detectionResult.getBarcodeColumnCount() * detectionResult.getBarcodeRowCount() - getNumberOfECCodeWords(detectionResult.getBarcodeECLevel());
      if (numberOfCodewords.length == 0) {
         if (calculatedNumberOfCodewords > 0 && calculatedNumberOfCodewords <= 928) {
            barcodeMatrix[0][1].setValue(calculatedNumberOfCodewords);
         } else {
            throw NotFoundException.getNotFoundInstance();
         }
      } else {
         if (numberOfCodewords[0] != calculatedNumberOfCodewords) {
            barcodeMatrix[0][1].setValue(calculatedNumberOfCodewords);
         }

      }
   }

   private static DecoderResult createDecoderResult(DetectionResult detectionResult) throws FormatException, ChecksumException, NotFoundException {
      BarcodeValue[][] barcodeMatrix = createBarcodeMatrix(detectionResult);
      adjustCodewordCount(detectionResult, barcodeMatrix);
      Collection<Integer> erasures = new ArrayList();
      int[] codewords = new int[detectionResult.getBarcodeRowCount() * detectionResult.getBarcodeColumnCount()];
      List<int[]> ambiguousIndexValuesList = new ArrayList();
      List<Integer> ambiguousIndexesList = new ArrayList();

      int i;
      for(int row = 0; row < detectionResult.getBarcodeRowCount(); ++row) {
         for(i = 0; i < detectionResult.getBarcodeColumnCount(); ++i) {
            int[] values = barcodeMatrix[row][i + 1].getValue();
            int codewordIndex = row * detectionResult.getBarcodeColumnCount() + i;
            if (values.length == 0) {
               erasures.add(codewordIndex);
            } else if (values.length == 1) {
               codewords[codewordIndex] = values[0];
            } else {
               ambiguousIndexesList.add(codewordIndex);
               ambiguousIndexValuesList.add(values);
            }
         }
      }

      int[][] ambiguousIndexValues = new int[ambiguousIndexValuesList.size()][];

      for(i = 0; i < ambiguousIndexValues.length; ++i) {
         ambiguousIndexValues[i] = (int[])ambiguousIndexValuesList.get(i);
      }

      return createDecoderResultFromAmbiguousValues(detectionResult.getBarcodeECLevel(), codewords, PDF417Common.toIntArray(erasures), PDF417Common.toIntArray(ambiguousIndexesList), ambiguousIndexValues);
   }

   private static DecoderResult createDecoderResultFromAmbiguousValues(int ecLevel, int[] codewords, int[] erasureArray, int[] ambiguousIndexes, int[][] ambiguousIndexValues) throws FormatException, ChecksumException {
      int[] ambiguousIndexCount = new int[ambiguousIndexes.length];
      int tries = 100;

      while(tries-- > 0) {
         int i;
         for(i = 0; i < ambiguousIndexCount.length; ++i) {
            codewords[ambiguousIndexes[i]] = ambiguousIndexValues[i][ambiguousIndexCount[i]];
         }

         try {
            return decodeCodewords(codewords, ecLevel, erasureArray);
         } catch (ChecksumException var8) {
            if (ambiguousIndexCount.length == 0) {
               throw ChecksumException.getChecksumInstance();
            }

            for(i = 0; i < ambiguousIndexCount.length; ++i) {
               if (ambiguousIndexCount[i] < ambiguousIndexValues[i].length - 1) {
                  int var10002 = ambiguousIndexCount[i]++;
                  break;
               }

               ambiguousIndexCount[i] = 0;
               if (i == ambiguousIndexCount.length - 1) {
                  throw ChecksumException.getChecksumInstance();
               }
            }
         }
      }

      throw ChecksumException.getChecksumInstance();
   }

   private static BarcodeValue[][] createBarcodeMatrix(DetectionResult detectionResult) {
      BarcodeValue[][] barcodeMatrix = new BarcodeValue[detectionResult.getBarcodeRowCount()][detectionResult.getBarcodeColumnCount() + 2];

      int column;
      for(column = 0; column < barcodeMatrix.length; ++column) {
         for(int column = 0; column < barcodeMatrix[column].length; ++column) {
            barcodeMatrix[column][column] = new BarcodeValue();
         }
      }

      column = 0;
      DetectionResultColumn[] var12;
      int var4 = (var12 = detectionResult.getDetectionResultColumns()).length;

      for(int var5 = 0; var5 < var4; ++var5) {
         DetectionResultColumn detectionResultColumn;
         if ((detectionResultColumn = var12[var5]) != null) {
            Codeword[] var7;
            int var8 = (var7 = detectionResultColumn.getCodewords()).length;

            for(int var9 = 0; var9 < var8; ++var9) {
               Codeword codeword;
               int rowNumber;
               if ((codeword = var7[var9]) != null && (rowNumber = codeword.getRowNumber()) >= 0 && rowNumber < barcodeMatrix.length) {
                  barcodeMatrix[rowNumber][column].setValue(codeword.getValue());
               }
            }
         }

         ++column;
      }

      return barcodeMatrix;
   }

   private static boolean isValidBarcodeColumn(DetectionResult detectionResult, int barcodeColumn) {
      return barcodeColumn >= 0 && barcodeColumn <= detectionResult.getBarcodeColumnCount() + 1;
   }

   private static int getStartColumn(DetectionResult detectionResult, int barcodeColumn, int imageRow, boolean leftToRight) {
      int offset = leftToRight ? 1 : -1;
      Codeword codeword = null;
      if (isValidBarcodeColumn(detectionResult, barcodeColumn - offset)) {
         codeword = detectionResult.getDetectionResultColumn(barcodeColumn - offset).getCodeword(imageRow);
      }

      if (codeword != null) {
         return leftToRight ? codeword.getEndX() : codeword.getStartX();
      } else if ((codeword = detectionResult.getDetectionResultColumn(barcodeColumn).getCodewordNearby(imageRow)) != null) {
         return leftToRight ? codeword.getStartX() : codeword.getEndX();
      } else {
         if (isValidBarcodeColumn(detectionResult, barcodeColumn - offset)) {
            codeword = detectionResult.getDetectionResultColumn(barcodeColumn - offset).getCodewordNearby(imageRow);
         }

         if (codeword != null) {
            return leftToRight ? codeword.getEndX() : codeword.getStartX();
         } else {
            for(int skippedColumns = 0; isValidBarcodeColumn(detectionResult, barcodeColumn - offset); ++skippedColumns) {
               barcodeColumn -= offset;
               Codeword[] var7;
               int var8 = (var7 = detectionResult.getDetectionResultColumn(barcodeColumn).getCodewords()).length;

               for(int var9 = 0; var9 < var8; ++var9) {
                  Codeword previousRowCodeword;
                  if ((previousRowCodeword = var7[var9]) != null) {
                     return (leftToRight ? previousRowCodeword.getEndX() : previousRowCodeword.getStartX()) + offset * skippedColumns * (previousRowCodeword.getEndX() - previousRowCodeword.getStartX());
                  }
               }
            }

            if (leftToRight) {
               return detectionResult.getBoundingBox().getMinX();
            } else {
               return detectionResult.getBoundingBox().getMaxX();
            }
         }
      }
   }

   private static Codeword detectCodeword(BitMatrix image, int minColumn, int maxColumn, boolean leftToRight, int startColumn, int imageRow, int minCodewordWidth, int maxCodewordWidth) {
      startColumn = adjustCodewordStartColumn(image, minColumn, maxColumn, leftToRight, startColumn, imageRow);
      int[] moduleBitCount;
      if ((moduleBitCount = getModuleBitCount(image, minColumn, maxColumn, leftToRight, startColumn, imageRow)) == null) {
         return null;
      } else {
         int codewordBitCount = MathUtils.sum(moduleBitCount);
         int endColumn;
         int decodedValue;
         int codeword;
         if (leftToRight) {
            endColumn = startColumn + codewordBitCount;
         } else {
            for(decodedValue = 0; decodedValue < moduleBitCount.length / 2; ++decodedValue) {
               codeword = moduleBitCount[decodedValue];
               moduleBitCount[decodedValue] = moduleBitCount[moduleBitCount.length - 1 - decodedValue];
               moduleBitCount[moduleBitCount.length - 1 - decodedValue] = codeword;
            }

            endColumn = startColumn;
            startColumn -= codewordBitCount;
         }

         if (!checkCodewordSkew(codewordBitCount, minCodewordWidth, maxCodewordWidth)) {
            return null;
         } else {
            return (codeword = PDF417Common.getCodeword(decodedValue = PDF417CodewordDecoder.getDecodedValue(moduleBitCount))) == -1 ? null : new Codeword(startColumn, endColumn, getCodewordBucketNumber(decodedValue), codeword);
         }
      }
   }

   private static int[] getModuleBitCount(BitMatrix image, int minColumn, int maxColumn, boolean leftToRight, int startColumn, int imageRow) {
      int imageColumn = startColumn;
      int[] moduleBitCount = new int[8];
      int moduleNumber = 0;
      int increment = leftToRight ? 1 : -1;
      boolean previousPixelValue = leftToRight;

      while(true) {
         if (leftToRight) {
            if (imageColumn >= maxColumn) {
               break;
            }
         } else if (imageColumn < minColumn) {
            break;
         }

         if (moduleNumber >= 8) {
            break;
         }

         if (image.get(imageColumn, imageRow) == previousPixelValue) {
            int var10002 = moduleBitCount[moduleNumber]++;
            imageColumn += increment;
         } else {
            ++moduleNumber;
            previousPixelValue = !previousPixelValue;
         }
      }

      return moduleNumber != 8 && (imageColumn != (leftToRight ? maxColumn : minColumn) || moduleNumber != 7) ? null : moduleBitCount;
   }

   private static int getNumberOfECCodeWords(int barcodeECLevel) {
      return 2 << barcodeECLevel;
   }

   private static int adjustCodewordStartColumn(BitMatrix image, int minColumn, int maxColumn, boolean leftToRight, int codewordStartColumn, int imageRow) {
      int correctedStartColumn = codewordStartColumn;
      int increment = leftToRight ? -1 : 1;

      for(int i = 0; i < 2; ++i) {
         while(true) {
            if (leftToRight) {
               if (correctedStartColumn < minColumn) {
                  break;
               }
            } else if (correctedStartColumn >= maxColumn) {
               break;
            }

            if (leftToRight != image.get(correctedStartColumn, imageRow)) {
               break;
            }

            if (Math.abs(codewordStartColumn - correctedStartColumn) > 2) {
               return codewordStartColumn;
            }

            correctedStartColumn += increment;
         }

         increment = -increment;
         leftToRight = !leftToRight;
      }

      return correctedStartColumn;
   }

   private static boolean checkCodewordSkew(int codewordSize, int minCodewordWidth, int maxCodewordWidth) {
      return minCodewordWidth - 2 <= codewordSize && codewordSize <= maxCodewordWidth + 2;
   }

   private static DecoderResult decodeCodewords(int[] codewords, int ecLevel, int[] erasures) throws FormatException, ChecksumException {
      if (codewords.length == 0) {
         throw FormatException.getFormatInstance();
      } else {
         int numECCodewords = 1 << ecLevel + 1;
         int correctedErrorsCount = correctErrors(codewords, erasures, numECCodewords);
         verifyCodewordCount(codewords, numECCodewords);
         DecoderResult decoderResult;
         (decoderResult = DecodedBitStreamParser.decode(codewords, String.valueOf(ecLevel))).setErrorsCorrected(correctedErrorsCount);
         decoderResult.setErasures(erasures.length);
         return decoderResult;
      }
   }

   private static int correctErrors(int[] codewords, int[] erasures, int numECCodewords) throws ChecksumException {
      if ((erasures == null || erasures.length <= numECCodewords / 2 + 3) && numECCodewords >= 0 && numECCodewords <= 512) {
         return errorCorrection.decode(codewords, numECCodewords, erasures);
      } else {
         throw ChecksumException.getChecksumInstance();
      }
   }

   private static void verifyCodewordCount(int[] codewords, int numECCodewords) throws FormatException {
      if (codewords.length < 4) {
         throw FormatException.getFormatInstance();
      } else {
         int numberOfCodewords;
         if ((numberOfCodewords = codewords[0]) > codewords.length) {
            throw FormatException.getFormatInstance();
         } else if (numberOfCodewords == 0) {
            if (numECCodewords < codewords.length) {
               codewords[0] = codewords.length - numECCodewords;
            } else {
               throw FormatException.getFormatInstance();
            }
         }
      }
   }

   private static int[] getBitCountForCodeword(int codeword) {
      int[] result = new int[8];
      int previousValue = 0;
      int i = 7;

      while(true) {
         if ((codeword & 1) != previousValue) {
            previousValue = codeword & 1;
            --i;
            if (i < 0) {
               return result;
            }
         }

         int var10002 = result[i]++;
         codeword >>= 1;
      }
   }

   private static int getCodewordBucketNumber(int codeword) {
      return getCodewordBucketNumber(getBitCountForCodeword(codeword));
   }

   private static int getCodewordBucketNumber(int[] moduleBitCount) {
      return (moduleBitCount[0] - moduleBitCount[2] + moduleBitCount[4] - moduleBitCount[6] + 9) % 9;
   }

   public static String toString(BarcodeValue[][] barcodeMatrix) {
      Formatter formatter = new Formatter();

      for(int row = 0; row < barcodeMatrix.length; ++row) {
         formatter.format("Row %2d: ", row);

         for(int column = 0; column < barcodeMatrix[row].length; ++column) {
            BarcodeValue barcodeValue;
            if ((barcodeValue = barcodeMatrix[row][column]).getValue().length == 0) {
               formatter.format("        ", (Object[])null);
            } else {
               formatter.format("%4d(%2d)", barcodeValue.getValue()[0], barcodeValue.getConfidence(barcodeValue.getValue()[0]));
            }
         }

         formatter.format("%n");
      }

      String result = formatter.toString();
      formatter.close();
      return result;
   }
}
