/*     */ package com.google.zxing.pdf417.decoder;
/*     */ 
/*     */ import com.google.zxing.ChecksumException;
/*     */ import com.google.zxing.FormatException;
/*     */ import com.google.zxing.NotFoundException;
/*     */ import com.google.zxing.ResultPoint;
/*     */ import com.google.zxing.common.BitMatrix;
/*     */ import com.google.zxing.common.DecoderResult;
/*     */ import com.google.zxing.common.detector.MathUtils;
/*     */ import com.google.zxing.pdf417.PDF417Common;
/*     */ import com.google.zxing.pdf417.decoder.ec.ErrorCorrection;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.Formatter;
/*     */ import java.util.List;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class PDF417ScanningDecoder
/*     */ {
/*     */   private static final int CODEWORD_SKEW_SIZE = 2;
/*     */   private static final int MAX_ERRORS = 3;
/*     */   private static final int MAX_EC_CODEWORDS = 512;
/*  43 */   private static final ErrorCorrection errorCorrection = new ErrorCorrection();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static DecoderResult decode(BitMatrix image, ResultPoint imageTopLeft, ResultPoint imageBottomLeft, ResultPoint imageTopRight, ResultPoint imageBottomRight, int minCodewordWidth, int maxCodewordWidth) throws NotFoundException, FormatException, ChecksumException {
/*  59 */     BoundingBox boundingBox = new BoundingBox(image, imageTopLeft, imageBottomLeft, imageTopRight, imageBottomRight);
/*  60 */     DetectionResultRowIndicatorColumn leftRowIndicatorColumn = null;
/*  61 */     DetectionResultRowIndicatorColumn rightRowIndicatorColumn = null;
/*  62 */     DetectionResult detectionResult = null;
/*  63 */     for (int i = 0; i < 2; i++) {
/*  64 */       if (imageTopLeft != null) {
/*  65 */         leftRowIndicatorColumn = getRowIndicatorColumn(image, boundingBox, imageTopLeft, true, minCodewordWidth, maxCodewordWidth);
/*     */       }
/*     */       
/*  68 */       if (imageTopRight != null) {
/*  69 */         rightRowIndicatorColumn = getRowIndicatorColumn(image, boundingBox, imageTopRight, false, minCodewordWidth, maxCodewordWidth);
/*     */       }
/*     */ 
/*     */       
/*  73 */       if ((detectionResult = merge(leftRowIndicatorColumn, rightRowIndicatorColumn)) == null) {
/*  74 */         throw NotFoundException.getNotFoundInstance();
/*     */       }
/*  76 */       if (i == 0 && detectionResult.getBoundingBox() != null && (detectionResult
/*  77 */         .getBoundingBox().getMinY() < boundingBox.getMinY() || detectionResult.getBoundingBox()
/*  78 */         .getMaxY() > boundingBox.getMaxY())) {
/*  79 */         boundingBox = detectionResult.getBoundingBox();
/*     */       } else {
/*  81 */         detectionResult.setBoundingBox(boundingBox);
/*     */         break;
/*     */       } 
/*     */     } 
/*  85 */     int maxBarcodeColumn = detectionResult.getBarcodeColumnCount() + 1;
/*  86 */     detectionResult.setDetectionResultColumn(0, leftRowIndicatorColumn);
/*  87 */     detectionResult.setDetectionResultColumn(maxBarcodeColumn, rightRowIndicatorColumn);
/*     */     
/*  89 */     boolean leftToRight = (leftRowIndicatorColumn != null);
/*  90 */     for (int barcodeColumnCount = 1; barcodeColumnCount <= maxBarcodeColumn; barcodeColumnCount++) {
/*  91 */       int barcodeColumn = leftToRight ? barcodeColumnCount : (maxBarcodeColumn - barcodeColumnCount);
/*  92 */       if (detectionResult.getDetectionResultColumn(barcodeColumn) == null) {
/*     */         DetectionResultColumn detectionResultColumn;
/*     */ 
/*     */ 
/*     */         
/*  97 */         if (barcodeColumn == 0 || barcodeColumn == maxBarcodeColumn) {
/*  98 */           detectionResultColumn = new DetectionResultRowIndicatorColumn(boundingBox, (barcodeColumn == 0));
/*     */         } else {
/* 100 */           detectionResultColumn = new DetectionResultColumn(boundingBox);
/*     */         } 
/* 102 */         detectionResult.setDetectionResultColumn(barcodeColumn, detectionResultColumn);
/*     */         
/* 104 */         int previousStartColumn = -1;
/*     */         
/* 106 */         for (int imageRow = boundingBox.getMinY(); imageRow <= boundingBox.getMaxY(); imageRow++) {
/*     */           int startColumn;
/* 108 */           if ((startColumn = getStartColumn(detectionResult, barcodeColumn, imageRow, leftToRight)) < 0 || startColumn > boundingBox.getMaxX())
/* 109 */             if (previousStartColumn != -1) {
/*     */ 
/*     */               
/* 112 */               startColumn = previousStartColumn;
/*     */             } else {
/*     */               continue;
/*     */             }   Codeword codeword;
/* 116 */           if ((codeword = detectCodeword(image, boundingBox.getMinX(), boundingBox.getMaxX(), leftToRight, startColumn, imageRow, minCodewordWidth, maxCodewordWidth)) != null) {
/* 117 */             detectionResultColumn.setCodeword(imageRow, codeword);
/* 118 */             previousStartColumn = startColumn;
/* 119 */             minCodewordWidth = Math.min(minCodewordWidth, codeword.getWidth());
/* 120 */             maxCodewordWidth = Math.max(maxCodewordWidth, codeword.getWidth());
/*     */           }  continue;
/*     */         } 
/*     */       } 
/* 124 */     }  return createDecoderResult(detectionResult);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static DetectionResult merge(DetectionResultRowIndicatorColumn leftRowIndicatorColumn, DetectionResultRowIndicatorColumn rightRowIndicatorColumn) throws NotFoundException {
/* 130 */     if (leftRowIndicatorColumn == null && rightRowIndicatorColumn == null) {
/* 131 */       return null;
/*     */     }
/*     */     BarcodeMetadata barcodeMetadata;
/* 134 */     if ((barcodeMetadata = getBarcodeMetadata(leftRowIndicatorColumn, rightRowIndicatorColumn)) == null) {
/* 135 */       return null;
/*     */     }
/* 137 */     BoundingBox boundingBox = BoundingBox.merge(adjustBoundingBox(leftRowIndicatorColumn), 
/* 138 */         adjustBoundingBox(rightRowIndicatorColumn));
/* 139 */     return new DetectionResult(barcodeMetadata, boundingBox);
/*     */   }
/*     */ 
/*     */   
/*     */   private static BoundingBox adjustBoundingBox(DetectionResultRowIndicatorColumn rowIndicatorColumn) throws NotFoundException {
/* 144 */     if (rowIndicatorColumn == null) {
/* 145 */       return null;
/*     */     }
/*     */     int[] rowHeights;
/* 148 */     if ((rowHeights = rowIndicatorColumn.getRowHeights()) == null) {
/* 149 */       return null;
/*     */     }
/* 151 */     int maxRowHeight = getMax(rowHeights);
/* 152 */     int missingStartRows = 0; int arrayOfInt1[], i; byte b;
/* 153 */     for (i = (arrayOfInt1 = rowHeights).length, b = 0; b < i; ) { int rowHeight = arrayOfInt1[b];
/* 154 */       missingStartRows += maxRowHeight - rowHeight;
/* 155 */       if (rowHeight <= 0) {
/*     */         b++;
/*     */       } }
/*     */     
/* 159 */     Codeword[] codewords = rowIndicatorColumn.getCodewords();
/* 160 */     for (int row = 0; missingStartRows > 0 && codewords[row] == null; row++) {
/* 161 */       missingStartRows--;
/*     */     }
/* 163 */     int missingEndRows = 0; int j;
/* 164 */     for (j = rowHeights.length - 1; j >= 0; ) {
/* 165 */       missingEndRows += maxRowHeight - rowHeights[j];
/* 166 */       if (rowHeights[j] <= 0) {
/*     */         j--;
/*     */       }
/*     */     } 
/* 170 */     for (j = codewords.length - 1; missingEndRows > 0 && codewords[j] == null; j--) {
/* 171 */       missingEndRows--;
/*     */     }
/* 173 */     return rowIndicatorColumn.getBoundingBox().addMissingRows(missingStartRows, missingEndRows, rowIndicatorColumn
/* 174 */         .isLeft());
/*     */   }
/*     */   
/*     */   private static int getMax(int[] values) {
/* 178 */     int maxValue = -1; int arrayOfInt[], i; byte b;
/* 179 */     for (i = (arrayOfInt = values).length, b = 0; b < i; ) { int value = arrayOfInt[b];
/* 180 */       maxValue = Math.max(maxValue, value); b++; }
/*     */     
/* 182 */     return maxValue;
/*     */   }
/*     */ 
/*     */   
/*     */   private static BarcodeMetadata getBarcodeMetadata(DetectionResultRowIndicatorColumn leftRowIndicatorColumn, DetectionResultRowIndicatorColumn rightRowIndicatorColumn) {
/*     */     BarcodeMetadata leftBarcodeMetadata;
/* 188 */     if (leftRowIndicatorColumn == null || (
/* 189 */       leftBarcodeMetadata = leftRowIndicatorColumn.getBarcodeMetadata()) == null) {
/* 190 */       return (rightRowIndicatorColumn == null) ? null : rightRowIndicatorColumn.getBarcodeMetadata();
/*     */     }
/*     */     BarcodeMetadata rightBarcodeMetadata;
/* 193 */     if (rightRowIndicatorColumn == null || (
/* 194 */       rightBarcodeMetadata = rightRowIndicatorColumn.getBarcodeMetadata()) == null) {
/* 195 */       return leftBarcodeMetadata;
/*     */     }
/*     */     
/* 198 */     if (leftBarcodeMetadata.getColumnCount() != rightBarcodeMetadata.getColumnCount() && leftBarcodeMetadata
/* 199 */       .getErrorCorrectionLevel() != rightBarcodeMetadata.getErrorCorrectionLevel() && leftBarcodeMetadata
/* 200 */       .getRowCount() != rightBarcodeMetadata.getRowCount()) {
/* 201 */       return null;
/*     */     }
/* 203 */     return leftBarcodeMetadata;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static DetectionResultRowIndicatorColumn getRowIndicatorColumn(BitMatrix image, BoundingBox boundingBox, ResultPoint startPoint, boolean leftToRight, int minCodewordWidth, int maxCodewordWidth) {
/* 212 */     DetectionResultRowIndicatorColumn rowIndicatorColumn = new DetectionResultRowIndicatorColumn(boundingBox, leftToRight);
/*     */     
/* 214 */     for (int i = 0; i < 2; i++) {
/* 215 */       int increment = (i == 0) ? 1 : -1;
/* 216 */       int startColumn = (int)startPoint.getX(); int imageRow;
/* 217 */       for (imageRow = (int)startPoint.getY(); imageRow <= boundingBox.getMaxY() && imageRow >= boundingBox
/* 218 */         .getMinY(); imageRow += increment) {
/*     */         Codeword codeword;
/*     */         
/* 221 */         if ((codeword = detectCodeword(image, 0, image.getWidth(), leftToRight, startColumn, imageRow, minCodewordWidth, maxCodewordWidth)) != null) {
/* 222 */           rowIndicatorColumn.setCodeword(imageRow, codeword);
/* 223 */           if (leftToRight) {
/* 224 */             startColumn = codeword.getStartX();
/*     */           } else {
/* 226 */             startColumn = codeword.getEndX();
/*     */           } 
/*     */         } 
/*     */       } 
/*     */     } 
/* 231 */     return rowIndicatorColumn;
/*     */   }
/*     */ 
/*     */   
/*     */   private static void adjustCodewordCount(DetectionResult detectionResult, BarcodeValue[][] barcodeMatrix) throws NotFoundException {
/* 236 */     int[] numberOfCodewords = barcodeMatrix[0][1].getValue();
/*     */ 
/*     */     
/* 239 */     int calculatedNumberOfCodewords = detectionResult.getBarcodeColumnCount() * detectionResult.getBarcodeRowCount() - getNumberOfECCodeWords(detectionResult.getBarcodeECLevel());
/* 240 */     if (numberOfCodewords.length == 0) {
/* 241 */       if (calculatedNumberOfCodewords <= 0 || calculatedNumberOfCodewords > 928) {
/* 242 */         throw NotFoundException.getNotFoundInstance();
/*     */       }
/* 244 */       barcodeMatrix[0][1].setValue(calculatedNumberOfCodewords); return;
/* 245 */     }  if (numberOfCodewords[0] != calculatedNumberOfCodewords)
/*     */     {
/* 247 */       barcodeMatrix[0][1].setValue(calculatedNumberOfCodewords);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   private static DecoderResult createDecoderResult(DetectionResult detectionResult) throws FormatException, ChecksumException, NotFoundException {
/* 253 */     BarcodeValue[][] barcodeMatrix = createBarcodeMatrix(detectionResult);
/* 254 */     adjustCodewordCount(detectionResult, barcodeMatrix);
/* 255 */     Collection<Integer> erasures = new ArrayList<>();
/* 256 */     int[] codewords = new int[detectionResult.getBarcodeRowCount() * detectionResult.getBarcodeColumnCount()];
/* 257 */     List<int[]> ambiguousIndexValuesList = (List)new ArrayList<>();
/* 258 */     List<Integer> ambiguousIndexesList = new ArrayList<>();
/* 259 */     for (int row = 0; row < detectionResult.getBarcodeRowCount(); row++) {
/* 260 */       for (int column = 0; column < detectionResult.getBarcodeColumnCount(); column++) {
/* 261 */         int[] values = barcodeMatrix[row][column + 1].getValue();
/* 262 */         int codewordIndex = row * detectionResult.getBarcodeColumnCount() + column;
/* 263 */         if (values.length == 0) {
/* 264 */           erasures.add(Integer.valueOf(codewordIndex));
/* 265 */         } else if (values.length == 1) {
/* 266 */           codewords[codewordIndex] = values[0];
/*     */         } else {
/* 268 */           ambiguousIndexesList.add(Integer.valueOf(codewordIndex));
/* 269 */           ambiguousIndexValuesList.add(values);
/*     */         } 
/*     */       } 
/*     */     } 
/* 273 */     int[][] ambiguousIndexValues = new int[ambiguousIndexValuesList.size()][];
/* 274 */     for (int i = 0; i < ambiguousIndexValues.length; i++) {
/* 275 */       ambiguousIndexValues[i] = ambiguousIndexValuesList.get(i);
/*     */     }
/* 277 */     return createDecoderResultFromAmbiguousValues(detectionResult.getBarcodeECLevel(), codewords, 
/* 278 */         PDF417Common.toIntArray(erasures), PDF417Common.toIntArray(ambiguousIndexesList), ambiguousIndexValues);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static DecoderResult createDecoderResultFromAmbiguousValues(int ecLevel, int[] codewords, int[] erasureArray, int[] ambiguousIndexes, int[][] ambiguousIndexValues) throws FormatException, ChecksumException {
/* 300 */     int[] ambiguousIndexCount = new int[ambiguousIndexes.length];
/*     */     
/* 302 */     int tries = 100;
/* 303 */     while (tries-- > 0) {
/* 304 */       int i; for (i = 0; i < ambiguousIndexCount.length; i++) {
/* 305 */         codewords[ambiguousIndexes[i]] = ambiguousIndexValues[i][ambiguousIndexCount[i]];
/*     */       }
/*     */       try {
/* 308 */         return decodeCodewords(codewords, ecLevel, erasureArray);
/* 309 */       } catch (ChecksumException checksumException) {
/*     */ 
/*     */         
/* 312 */         if (ambiguousIndexCount.length == 0) {
/* 313 */           throw ChecksumException.getChecksumInstance();
/*     */         }
/* 315 */         for (i = 0; i < ambiguousIndexCount.length; i++) {
/* 316 */           if (ambiguousIndexCount[i] < (ambiguousIndexValues[i]).length - 1) {
/* 317 */             ambiguousIndexCount[i] = ambiguousIndexCount[i] + 1;
/*     */             break;
/*     */           } 
/* 320 */           ambiguousIndexCount[i] = 0;
/* 321 */           if (i == ambiguousIndexCount.length - 1) {
/* 322 */             throw ChecksumException.getChecksumInstance();
/*     */           }
/*     */         } 
/*     */       } 
/*     */     } 
/* 327 */     throw ChecksumException.getChecksumInstance();
/*     */   }
/*     */ 
/*     */   
/*     */   private static BarcodeValue[][] createBarcodeMatrix(DetectionResult detectionResult) {
/* 332 */     BarcodeValue[][] barcodeMatrix = new BarcodeValue[detectionResult.getBarcodeRowCount()][detectionResult.getBarcodeColumnCount() + 2];
/* 333 */     for (int row = 0; row < barcodeMatrix.length; row++) {
/* 334 */       for (int j = 0; j < (barcodeMatrix[row]).length; j++) {
/* 335 */         barcodeMatrix[row][j] = new BarcodeValue();
/*     */       }
/*     */     } 
/*     */     
/* 339 */     int column = 0; DetectionResultColumn[] arrayOfDetectionResultColumn; int i; byte b;
/* 340 */     for (i = (arrayOfDetectionResultColumn = detectionResult.getDetectionResultColumns()).length, b = 0; b < i; b++) {
/* 341 */       DetectionResultColumn detectionResultColumn; if ((detectionResultColumn = arrayOfDetectionResultColumn[b]) != null) {
/* 342 */         Codeword[] arrayOfCodeword; int j; byte b1; for (j = (arrayOfCodeword = detectionResultColumn.getCodewords()).length, b1 = 0; b1 < j; ) {
/* 343 */           Codeword codeword; int rowNumber; if ((codeword = arrayOfCodeword[b1]) != null && (
/*     */             
/* 345 */             rowNumber = codeword.getRowNumber()) >= 0 && 
/* 346 */             rowNumber < barcodeMatrix.length)
/*     */           {
/*     */ 
/*     */             
/* 350 */             barcodeMatrix[rowNumber][column].setValue(codeword.getValue());
/*     */           }
/*     */           b1++;
/*     */         } 
/*     */       } 
/* 355 */       column++;
/*     */     } 
/* 357 */     return barcodeMatrix;
/*     */   }
/*     */   
/*     */   private static boolean isValidBarcodeColumn(DetectionResult detectionResult, int barcodeColumn) {
/* 361 */     return (barcodeColumn >= 0 && barcodeColumn <= detectionResult.getBarcodeColumnCount() + 1);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static int getStartColumn(DetectionResult detectionResult, int barcodeColumn, int imageRow, boolean leftToRight) {
/* 368 */     int offset = leftToRight ? 1 : -1;
/* 369 */     Codeword codeword = null;
/* 370 */     if (isValidBarcodeColumn(detectionResult, barcodeColumn - offset)) {
/* 371 */       codeword = detectionResult.getDetectionResultColumn(barcodeColumn - offset).getCodeword(imageRow);
/*     */     }
/* 373 */     if (codeword != null) {
/* 374 */       return leftToRight ? codeword.getEndX() : codeword.getStartX();
/*     */     }
/*     */     
/* 377 */     if ((codeword = detectionResult.getDetectionResultColumn(barcodeColumn).getCodewordNearby(imageRow)) != null) {
/* 378 */       return leftToRight ? codeword.getStartX() : codeword.getEndX();
/*     */     }
/* 380 */     if (isValidBarcodeColumn(detectionResult, barcodeColumn - offset)) {
/* 381 */       codeword = detectionResult.getDetectionResultColumn(barcodeColumn - offset).getCodewordNearby(imageRow);
/*     */     }
/* 383 */     if (codeword != null) {
/* 384 */       return leftToRight ? codeword.getEndX() : codeword.getStartX();
/*     */     }
/* 386 */     int skippedColumns = 0;
/*     */     
/* 388 */     while (isValidBarcodeColumn(detectionResult, barcodeColumn - offset)) {
/* 389 */       barcodeColumn -= offset; Codeword[] arrayOfCodeword; int i; byte b;
/* 390 */       for (i = (arrayOfCodeword = detectionResult.getDetectionResultColumn(barcodeColumn).getCodewords()).length, b = 0; b < i; b++) {
/* 391 */         Codeword previousRowCodeword; if ((previousRowCodeword = arrayOfCodeword[b]) != null) {
/* 392 */           return (leftToRight ? previousRowCodeword.getEndX() : previousRowCodeword.getStartX()) + offset * skippedColumns * (previousRowCodeword
/*     */ 
/*     */             
/* 395 */             .getEndX() - previousRowCodeword.getStartX());
/*     */         }
/*     */       } 
/* 398 */       skippedColumns++;
/*     */     } 
/* 400 */     return leftToRight ? detectionResult.getBoundingBox().getMinX() : detectionResult.getBoundingBox().getMaxX();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static Codeword detectCodeword(BitMatrix image, int minColumn, int maxColumn, boolean leftToRight, int startColumn, int imageRow, int minCodewordWidth, int maxCodewordWidth) {
/*     */     int endColumn;
/* 411 */     startColumn = adjustCodewordStartColumn(image, minColumn, maxColumn, leftToRight, startColumn, imageRow);
/*     */ 
/*     */     
/*     */     int[] moduleBitCount;
/*     */ 
/*     */     
/* 417 */     if ((moduleBitCount = getModuleBitCount(image, minColumn, maxColumn, leftToRight, startColumn, imageRow)) == null) {
/* 418 */       return null;
/*     */     }
/*     */     
/* 421 */     int codewordBitCount = MathUtils.sum(moduleBitCount);
/* 422 */     if (leftToRight) {
/* 423 */       endColumn = startColumn + codewordBitCount;
/*     */     } else {
/* 425 */       for (int i = 0; i < moduleBitCount.length / 2; i++) {
/* 426 */         int tmpCount = moduleBitCount[i];
/* 427 */         moduleBitCount[i] = moduleBitCount[moduleBitCount.length - 1 - i];
/* 428 */         moduleBitCount[moduleBitCount.length - 1 - i] = tmpCount;
/*     */       } 
/* 430 */       endColumn = startColumn;
/* 431 */       startColumn -= codewordBitCount;
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 447 */     if (!checkCodewordSkew(codewordBitCount, minCodewordWidth, maxCodewordWidth))
/*     */     {
/*     */       
/* 450 */       return null;
/*     */     }
/*     */     
/*     */     int decodedValue, codeword;
/*     */     
/* 455 */     if ((codeword = PDF417Common.getCodeword(decodedValue = PDF417CodewordDecoder.getDecodedValue(moduleBitCount))) == -1) {
/* 456 */       return null;
/*     */     }
/* 458 */     return new Codeword(startColumn, endColumn, getCodewordBucketNumber(decodedValue), codeword);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static int[] getModuleBitCount(BitMatrix image, int minColumn, int maxColumn, boolean leftToRight, int startColumn, int imageRow) {
/* 467 */     int imageColumn = startColumn;
/* 468 */     int[] moduleBitCount = new int[8];
/* 469 */     int moduleNumber = 0;
/* 470 */     int increment = leftToRight ? 1 : -1;
/* 471 */     boolean previousPixelValue = leftToRight;
/* 472 */     while ((leftToRight ? (imageColumn < maxColumn) : (imageColumn >= minColumn)) && moduleNumber < 8) {
/*     */       
/* 474 */       if (image.get(imageColumn, imageRow) == previousPixelValue) {
/* 475 */         moduleBitCount[moduleNumber] = moduleBitCount[moduleNumber] + 1;
/* 476 */         imageColumn += increment; continue;
/*     */       } 
/* 478 */       moduleNumber++;
/* 479 */       previousPixelValue = !previousPixelValue;
/*     */     } 
/*     */     
/* 482 */     if (moduleNumber == 8 || (imageColumn == (leftToRight ? maxColumn : minColumn) && moduleNumber == 7))
/*     */     {
/*     */       
/* 485 */       return moduleBitCount;
/*     */     }
/* 487 */     return null;
/*     */   }
/*     */   
/*     */   private static int getNumberOfECCodeWords(int barcodeECLevel) {
/* 491 */     return 2 << barcodeECLevel;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static int adjustCodewordStartColumn(BitMatrix image, int minColumn, int maxColumn, boolean leftToRight, int codewordStartColumn, int imageRow) {
/* 500 */     int correctedStartColumn = codewordStartColumn;
/* 501 */     int increment = leftToRight ? -1 : 1;
/*     */     
/* 503 */     for (int i = 0; i < 2; i++) {
/* 504 */       while ((leftToRight ? (correctedStartColumn >= minColumn) : (correctedStartColumn < maxColumn)) && leftToRight == image
/* 505 */         .get(correctedStartColumn, imageRow)) {
/* 506 */         if (Math.abs(codewordStartColumn - correctedStartColumn) > 2) {
/* 507 */           return codewordStartColumn;
/*     */         }
/* 509 */         correctedStartColumn += increment;
/*     */       } 
/* 511 */       increment = -increment;
/* 512 */       leftToRight = !leftToRight;
/*     */     } 
/* 514 */     return correctedStartColumn;
/*     */   }
/*     */   
/*     */   private static boolean checkCodewordSkew(int codewordSize, int minCodewordWidth, int maxCodewordWidth) {
/* 518 */     return (minCodewordWidth - 2 <= codewordSize && codewordSize <= maxCodewordWidth + 2);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static DecoderResult decodeCodewords(int[] codewords, int ecLevel, int[] erasures) throws FormatException, ChecksumException {
/* 524 */     if (codewords.length == 0) {
/* 525 */       throw FormatException.getFormatInstance();
/*     */     }
/*     */     
/* 528 */     int numECCodewords = 1 << ecLevel + 1;
/* 529 */     int correctedErrorsCount = correctErrors(codewords, erasures, numECCodewords);
/* 530 */     verifyCodewordCount(codewords, numECCodewords);
/*     */     
/*     */     DecoderResult decoderResult;
/*     */     
/* 534 */     (decoderResult = DecodedBitStreamParser.decode(codewords, String.valueOf(ecLevel))).setErrorsCorrected(Integer.valueOf(correctedErrorsCount));
/* 535 */     decoderResult.setErasures(Integer.valueOf(erasures.length));
/* 536 */     return decoderResult;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static int correctErrors(int[] codewords, int[] erasures, int numECCodewords) throws ChecksumException {
/* 549 */     if ((erasures != null && erasures.length > numECCodewords / 2 + 3) || numECCodewords < 0 || numECCodewords > 512)
/*     */     {
/*     */ 
/*     */ 
/*     */       
/* 554 */       throw ChecksumException.getChecksumInstance();
/*     */     }
/* 556 */     return errorCorrection.decode(codewords, numECCodewords, erasures);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static void verifyCodewordCount(int[] codewords, int numECCodewords) throws FormatException {
/* 563 */     if (codewords.length < 4)
/*     */     {
/*     */       
/* 566 */       throw FormatException.getFormatInstance();
/*     */     }
/*     */ 
/*     */     
/*     */     int numberOfCodewords;
/*     */     
/* 572 */     if ((numberOfCodewords = codewords[0]) > codewords.length) {
/* 573 */       throw FormatException.getFormatInstance();
/*     */     }
/* 575 */     if (numberOfCodewords == 0) {
/*     */       
/* 577 */       if (numECCodewords < codewords.length) {
/* 578 */         codewords[0] = codewords.length - numECCodewords; return;
/*     */       } 
/* 580 */       throw FormatException.getFormatInstance();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static int[] getBitCountForCodeword(int codeword) {
/*     */     // Byte code:
/*     */     //   0: bipush #8
/*     */     //   2: newarray int
/*     */     //   4: astore_1
/*     */     //   5: iconst_0
/*     */     //   6: istore_2
/*     */     //   7: bipush #7
/*     */     //   9: istore_3
/*     */     //   10: iload_0
/*     */     //   11: iconst_1
/*     */     //   12: iand
/*     */     //   13: iload_2
/*     */     //   14: if_icmpeq -> 28
/*     */     //   17: iload_0
/*     */     //   18: iconst_1
/*     */     //   19: iand
/*     */     //   20: istore_2
/*     */     //   21: iinc #3, -1
/*     */     //   24: iload_3
/*     */     //   25: iflt -> 42
/*     */     //   28: aload_1
/*     */     //   29: iload_3
/*     */     //   30: dup2
/*     */     //   31: iaload
/*     */     //   32: iconst_1
/*     */     //   33: iadd
/*     */     //   34: iastore
/*     */     //   35: iload_0
/*     */     //   36: iconst_1
/*     */     //   37: ishr
/*     */     //   38: istore_0
/*     */     //   39: goto -> 10
/*     */     //   42: aload_1
/*     */     //   43: areturn
/*     */     // Line number table:
/*     */     //   Java source line number -> byte code offset
/*     */     //   #586	-> 0
/*     */     //   #587	-> 5
/*     */     //   #588	-> 7
/*     */     //   #590	-> 10
/*     */     //   #591	-> 17
/*     */     //   #592	-> 21
/*     */     //   #593	-> 24
/*     */     //   #597	-> 28
/*     */     //   #598	-> 35
/*     */     //   #600	-> 42
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	descriptor
/*     */     //   0	44	0	codeword	I
/*     */     //   5	39	1	result	[I
/*     */     //   7	37	2	previousValue	I
/*     */     //   10	34	3	i	I
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static int getCodewordBucketNumber(int codeword) {
/* 604 */     return getCodewordBucketNumber(getBitCountForCodeword(codeword));
/*     */   }
/*     */   
/*     */   private static int getCodewordBucketNumber(int[] moduleBitCount) {
/* 608 */     return (moduleBitCount[0] - moduleBitCount[2] + moduleBitCount[4] - moduleBitCount[6] + 9) % 9;
/*     */   }
/*     */   
/*     */   public static String toString(BarcodeValue[][] barcodeMatrix) {
/* 612 */     Formatter formatter = new Formatter();
/* 613 */     for (int row = 0; row < barcodeMatrix.length; row++) {
/* 614 */       formatter.format("Row %2d: ", new Object[] { Integer.valueOf(row) });
/* 615 */       for (int column = 0; column < (barcodeMatrix[row]).length; column++) {
/*     */         BarcodeValue barcodeValue;
/* 617 */         if (((barcodeValue = barcodeMatrix[row][column]).getValue()).length == 0) {
/* 618 */           formatter.format("        ", null);
/*     */         } else {
/* 620 */           formatter.format("%4d(%2d)", new Object[] { Integer.valueOf(barcodeValue.getValue()[0]), barcodeValue
/* 621 */                 .getConfidence(barcodeValue.getValue()[0]) });
/*     */         } 
/*     */       } 
/* 624 */       formatter.format("%n", new Object[0]);
/*     */     } 
/* 626 */     String result = formatter.toString();
/* 627 */     formatter.close();
/* 628 */     return result;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\zxing\pdf417\decoder\PDF417ScanningDecoder.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */