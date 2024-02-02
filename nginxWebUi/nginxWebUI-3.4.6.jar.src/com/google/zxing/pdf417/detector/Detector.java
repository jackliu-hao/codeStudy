/*     */ package com.google.zxing.pdf417.detector;
/*     */ 
/*     */ import com.google.zxing.BinaryBitmap;
/*     */ import com.google.zxing.DecodeHintType;
/*     */ import com.google.zxing.NotFoundException;
/*     */ import com.google.zxing.ResultPoint;
/*     */ import com.google.zxing.common.BitMatrix;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class Detector
/*     */ {
/*  40 */   private static final int[] INDEXES_START_PATTERN = new int[] { 0, 4, 1, 5 };
/*  41 */   private static final int[] INDEXES_STOP_PATTERN = new int[] { 6, 2, 7, 3 };
/*     */   
/*     */   private static final float MAX_AVG_VARIANCE = 0.42F;
/*     */   
/*     */   private static final float MAX_INDIVIDUAL_VARIANCE = 0.8F;
/*     */   
/*  47 */   private static final int[] START_PATTERN = new int[] { 8, 1, 1, 1, 1, 1, 1, 3 };
/*     */   
/*  49 */   private static final int[] STOP_PATTERN = new int[] { 7, 1, 1, 3, 1, 1, 1, 2, 1 };
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static final int MAX_PIXEL_DRIFT = 3;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static final int MAX_PATTERN_DRIFT = 5;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static final int SKIPPED_ROW_COUNT_MAX = 25;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static final int ROW_STEP = 5;
/*     */ 
/*     */ 
/*     */   
/*     */   private static final int BARCODE_MIN_HEIGHT = 10;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static PDF417DetectorResult detect(BinaryBitmap image, Map<DecodeHintType, ?> hints, boolean multiple) throws NotFoundException {
/*  79 */     BitMatrix bitMatrix = image.getBlackMatrix();
/*     */     
/*     */     List<ResultPoint[]> barcodeCoordinates;
/*  82 */     if ((barcodeCoordinates = detect(multiple, bitMatrix)).isEmpty()) {
/*     */       
/*  84 */       (bitMatrix = bitMatrix.clone()).rotate180();
/*  85 */       barcodeCoordinates = detect(multiple, bitMatrix);
/*     */     } 
/*  87 */     return new PDF417DetectorResult(bitMatrix, barcodeCoordinates);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static List<ResultPoint[]> detect(boolean multiple, BitMatrix bitMatrix) {
/*  98 */     List<ResultPoint[]> barcodeCoordinates = (List)new ArrayList<>();
/*  99 */     int row = 0;
/* 100 */     int column = 0;
/* 101 */     boolean foundBarcodeInRow = false;
/* 102 */     while (row < bitMatrix.getHeight()) {
/*     */       ResultPoint[] vertices;
/*     */       
/* 105 */       if ((vertices = findVertices(bitMatrix, row, column))[0] == null && vertices[3] == null) {
/* 106 */         if (foundBarcodeInRow) {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */           
/* 112 */           foundBarcodeInRow = false;
/* 113 */           column = 0;
/* 114 */           for (Iterator<ResultPoint> iterator = barcodeCoordinates.iterator(); iterator.hasNext(); ) {
/* 115 */             ResultPoint[] barcodeCoordinate; if ((barcodeCoordinate = (ResultPoint[])iterator.next())[1] != null) {
/* 116 */               row = (int)Math.max(row, barcodeCoordinate[1].getY());
/*     */             }
/* 118 */             if (barcodeCoordinate[3] != null) {
/* 119 */               row = Math.max(row, (int)barcodeCoordinate[3].getY());
/*     */             }
/*     */           } 
/* 122 */           row += 5; continue;
/*     */         }  break;
/*     */       } 
/* 125 */       foundBarcodeInRow = true;
/* 126 */       barcodeCoordinates.add(vertices);
/* 127 */       if (multiple) {
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 132 */         if (vertices[2] != null) {
/* 133 */           column = (int)vertices[2].getX();
/* 134 */           row = (int)vertices[2].getY(); continue;
/*     */         } 
/* 136 */         column = (int)vertices[4].getX();
/* 137 */         row = (int)vertices[4].getY();
/*     */       } 
/*     */     } 
/* 140 */     return barcodeCoordinates;
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
/*     */   private static ResultPoint[] findVertices(BitMatrix matrix, int startRow, int startColumn) {
/* 159 */     int height = matrix.getHeight();
/* 160 */     int width = matrix.getWidth();
/*     */     
/*     */     ResultPoint[] result;
/* 163 */     copyToResult(result = new ResultPoint[8], findRowsWithPattern(matrix, height, width, startRow, startColumn, START_PATTERN), INDEXES_START_PATTERN);
/*     */ 
/*     */     
/* 166 */     if (result[4] != null) {
/* 167 */       startColumn = (int)result[4].getX();
/* 168 */       startRow = (int)result[4].getY();
/*     */     } 
/* 170 */     copyToResult(result, findRowsWithPattern(matrix, height, width, startRow, startColumn, STOP_PATTERN), INDEXES_STOP_PATTERN);
/*     */     
/* 172 */     return result;
/*     */   }
/*     */   
/*     */   private static void copyToResult(ResultPoint[] result, ResultPoint[] tmpResult, int[] destinationIndexes) {
/* 176 */     for (int i = 0; i < destinationIndexes.length; i++) {
/* 177 */       result[destinationIndexes[i]] = tmpResult[i];
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static ResultPoint[] findRowsWithPattern(BitMatrix matrix, int height, int width, int startRow, int startColumn, int[] pattern) {
/* 187 */     ResultPoint[] result = new ResultPoint[4];
/* 188 */     boolean found = false;
/* 189 */     int[] counters = new int[pattern.length];
/* 190 */     for (; startRow < height; startRow += 5) {
/*     */       int[] loc;
/* 192 */       if ((loc = findGuardPattern(matrix, startColumn, startRow, width, false, pattern, counters)) != null) {
/* 193 */         while (startRow > 0) {
/*     */           int[] previousRowLoc;
/* 195 */           if ((previousRowLoc = findGuardPattern(matrix, startColumn, --startRow, width, false, pattern, counters)) != null) {
/* 196 */             loc = previousRowLoc; continue;
/*     */           } 
/* 198 */           startRow++;
/*     */         } 
/*     */ 
/*     */         
/* 202 */         result[0] = new ResultPoint(loc[0], startRow);
/* 203 */         result[1] = new ResultPoint(loc[1], startRow);
/* 204 */         found = true;
/*     */         break;
/*     */       } 
/*     */     } 
/* 208 */     int stopRow = startRow + 1;
/*     */     
/* 210 */     if (found) {
/* 211 */       int skippedRowCount = 0;
/* 212 */       int[] previousRowLoc = { (int)result[0].getX(), (int)result[1].getX() };
/* 213 */       for (; stopRow < height; stopRow++) {
/*     */         int[] loc;
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 219 */         if ((loc = findGuardPattern(matrix, previousRowLoc[0], stopRow, width, false, pattern, counters)) != null && 
/* 220 */           Math.abs(previousRowLoc[0] - loc[0]) < 5 && 
/* 221 */           Math.abs(previousRowLoc[1] - loc[1]) < 5) {
/* 222 */           previousRowLoc = loc;
/* 223 */           skippedRowCount = 0;
/*     */         }
/* 225 */         else if (skippedRowCount <= 25) {
/*     */ 
/*     */           
/* 228 */           skippedRowCount++;
/*     */         } else {
/*     */           break;
/*     */         } 
/* 232 */       }  stopRow -= skippedRowCount + 1;
/* 233 */       result[2] = new ResultPoint(previousRowLoc[0], stopRow);
/* 234 */       result[3] = new ResultPoint(previousRowLoc[1], stopRow);
/*     */     } 
/* 236 */     if (stopRow - startRow < 10) {
/* 237 */       for (int i = 0; i < 4; i++) {
/* 238 */         result[i] = null;
/*     */       }
/*     */     }
/* 241 */     return result;
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
/*     */   private static int[] findGuardPattern(BitMatrix matrix, int column, int row, int width, boolean whiteFirst, int[] pattern, int[] counters) {
/* 261 */     Arrays.fill(counters, 0, counters.length, 0);
/* 262 */     int patternStart = column;
/* 263 */     int pixelDrift = 0;
/*     */ 
/*     */     
/* 266 */     while (matrix.get(patternStart, row) && patternStart > 0 && pixelDrift++ < 3) {
/* 267 */       patternStart--;
/*     */     }
/* 269 */     int x = patternStart;
/* 270 */     int counterPosition = 0;
/* 271 */     int patternLength = pattern.length;
/* 272 */     boolean isWhite = whiteFirst;
/* 273 */     for (; x < width; x++) {
/* 274 */       if (matrix.get(x, row) ^ 
/* 275 */         isWhite) {
/* 276 */         counters[counterPosition] = counters[counterPosition] + 1;
/*     */       } else {
/* 278 */         if (counterPosition == patternLength - 1) {
/* 279 */           if (patternMatchVariance(counters, pattern, 0.8F) < 0.42F) {
/* 280 */             return new int[] { patternStart, x };
/*     */           }
/* 282 */           patternStart += counters[0] + counters[1];
/* 283 */           System.arraycopy(counters, 2, counters, 0, patternLength - 2);
/* 284 */           counters[patternLength - 2] = 0;
/* 285 */           counters[patternLength - 1] = 0;
/* 286 */           counterPosition--;
/*     */         } else {
/* 288 */           counterPosition++;
/*     */         } 
/* 290 */         counters[counterPosition] = 1;
/* 291 */         isWhite = !isWhite;
/*     */       } 
/*     */     } 
/* 294 */     if (counterPosition == patternLength - 1 && 
/* 295 */       patternMatchVariance(counters, pattern, 0.8F) < 0.42F) {
/* 296 */       return new int[] { patternStart, x - 1 };
/*     */     }
/*     */     
/* 299 */     return null;
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
/*     */   private static float patternMatchVariance(int[] counters, int[] pattern, float maxIndividualVariance) {
/* 314 */     int numCounters = counters.length;
/* 315 */     int total = 0;
/* 316 */     int patternLength = 0;
/* 317 */     for (int i = 0; i < numCounters; i++) {
/* 318 */       total += counters[i];
/* 319 */       patternLength += pattern[i];
/*     */     } 
/* 321 */     if (total < patternLength)
/*     */     {
/*     */       
/* 324 */       return Float.POSITIVE_INFINITY;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 329 */     float unitBarWidth = total / patternLength;
/* 330 */     maxIndividualVariance *= unitBarWidth;
/*     */     
/* 332 */     float totalVariance = 0.0F;
/* 333 */     for (int x = 0; x < numCounters; x++) {
/* 334 */       int counter = counters[x];
/* 335 */       float scaledPattern = pattern[x] * unitBarWidth;
/*     */       float variance;
/* 337 */       if ((variance = (counter > scaledPattern) ? (counter - scaledPattern) : (scaledPattern - counter)) > maxIndividualVariance) {
/* 338 */         return Float.POSITIVE_INFINITY;
/*     */       }
/* 340 */       totalVariance += variance;
/*     */     } 
/* 342 */     return totalVariance / total;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\zxing\pdf417\detector\Detector.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */