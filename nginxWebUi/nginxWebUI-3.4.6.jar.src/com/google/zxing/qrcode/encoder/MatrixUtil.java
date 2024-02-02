/*     */ package com.google.zxing.qrcode.encoder;
/*     */ 
/*     */ import com.google.zxing.WriterException;
/*     */ import com.google.zxing.common.BitArray;
/*     */ import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
/*     */ import com.google.zxing.qrcode.decoder.Version;
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
/*     */ final class MatrixUtil
/*     */ {
/*  34 */   private static final int[][] POSITION_DETECTION_PATTERN = new int[][] { { 1, 1, 1, 1, 1, 1, 1 }, { 1, 0, 0, 0, 0, 0, 1 }, { 1, 0, 1, 1, 1, 0, 1 }, { 1, 0, 1, 1, 1, 0, 1 }, { 1, 0, 1, 1, 1, 0, 1 }, { 1, 0, 0, 0, 0, 0, 1 }, { 1, 1, 1, 1, 1, 1, 1 } };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  44 */   private static final int[][] POSITION_ADJUSTMENT_PATTERN = new int[][] { { 1, 1, 1, 1, 1 }, { 1, 0, 0, 0, 1 }, { 1, 0, 1, 0, 1 }, { 1, 0, 0, 0, 1 }, { 1, 1, 1, 1, 1 } };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  53 */   private static final int[][] POSITION_ADJUSTMENT_PATTERN_COORDINATE_TABLE = new int[][] { { -1, -1, -1, -1, -1, -1, -1 }, { 6, 18, -1, -1, -1, -1, -1 }, { 6, 22, -1, -1, -1, -1, -1 }, { 6, 26, -1, -1, -1, -1, -1 }, { 6, 30, -1, -1, -1, -1, -1 }, { 6, 34, -1, -1, -1, -1, -1 }, { 6, 22, 38, -1, -1, -1, -1 }, { 6, 24, 42, -1, -1, -1, -1 }, { 6, 26, 46, -1, -1, -1, -1 }, { 6, 28, 50, -1, -1, -1, -1 }, { 6, 30, 54, -1, -1, -1, -1 }, { 6, 32, 58, -1, -1, -1, -1 }, { 6, 34, 62, -1, -1, -1, -1 }, { 6, 26, 46, 66, -1, -1, -1 }, { 6, 26, 48, 70, -1, -1, -1 }, { 6, 26, 50, 74, -1, -1, -1 }, { 6, 30, 54, 78, -1, -1, -1 }, { 6, 30, 56, 82, -1, -1, -1 }, { 6, 30, 58, 86, -1, -1, -1 }, { 6, 34, 62, 90, -1, -1, -1 }, { 6, 28, 50, 72, 94, -1, -1 }, { 6, 26, 50, 74, 98, -1, -1 }, { 6, 30, 54, 78, 102, -1, -1 }, { 6, 28, 54, 80, 106, -1, -1 }, { 6, 32, 58, 84, 110, -1, -1 }, { 6, 30, 58, 86, 114, -1, -1 }, { 6, 34, 62, 90, 118, -1, -1 }, { 6, 26, 50, 74, 98, 122, -1 }, { 6, 30, 54, 78, 102, 126, -1 }, { 6, 26, 52, 78, 104, 130, -1 }, { 6, 30, 56, 82, 108, 134, -1 }, { 6, 34, 60, 86, 112, 138, -1 }, { 6, 30, 58, 86, 114, 142, -1 }, { 6, 34, 62, 90, 118, 146, -1 }, { 6, 30, 54, 78, 102, 126, 150 }, { 6, 24, 50, 76, 102, 128, 154 }, { 6, 28, 54, 80, 106, 132, 158 }, { 6, 32, 58, 84, 110, 136, 162 }, { 6, 26, 54, 82, 110, 138, 166 }, { 6, 30, 58, 86, 114, 142, 170 } };
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
/*  97 */   private static final int[][] TYPE_INFO_COORDINATES = new int[][] { { 8, 0 }, { 8, 1 }, { 8, 2 }, { 8, 3 }, { 8, 4 }, { 8, 5 }, { 8, 7 }, { 8, 8 }, { 7, 8 }, { 5, 8 }, { 4, 8 }, { 3, 8 }, { 2, 8 }, { 1, 8 }, { 0, 8 } };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static final int VERSION_INFO_POLY = 7973;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static final int TYPE_INFO_POLY = 1335;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static final int TYPE_INFO_MASK_PATTERN = 21522;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static void clearMatrix(ByteMatrix matrix) {
/* 127 */     matrix.clear((byte)-1);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static void buildMatrix(BitArray dataBits, ErrorCorrectionLevel ecLevel, Version version, int maskPattern, ByteMatrix matrix) throws WriterException {
/* 137 */     clearMatrix(matrix);
/* 138 */     embedBasicPatterns(version, matrix);
/*     */     
/* 140 */     embedTypeInfo(ecLevel, maskPattern, matrix);
/*     */     
/* 142 */     maybeEmbedVersionInfo(version, matrix);
/*     */     
/* 144 */     embedDataBits(dataBits, maskPattern, matrix);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static void embedBasicPatterns(Version version, ByteMatrix matrix) throws WriterException {
/* 155 */     embedPositionDetectionPatternsAndSeparators(matrix);
/*     */     
/* 157 */     embedDarkDotAtLeftBottomCorner(matrix);
/*     */ 
/*     */     
/* 160 */     maybeEmbedPositionAdjustmentPatterns(version, matrix);
/*     */     
/* 162 */     embedTimingPatterns(matrix);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   static void embedTypeInfo(ErrorCorrectionLevel ecLevel, int maskPattern, ByteMatrix matrix) throws WriterException {
/* 168 */     BitArray typeInfoBits = new BitArray();
/* 169 */     makeTypeInfoBits(ecLevel, maskPattern, typeInfoBits);
/*     */     
/* 171 */     for (int i = 0; i < typeInfoBits.getSize(); i++) {
/*     */ 
/*     */       
/* 174 */       boolean bit = typeInfoBits.get(typeInfoBits.getSize() - 1 - i);
/*     */ 
/*     */       
/* 177 */       int x1 = TYPE_INFO_COORDINATES[i][0];
/* 178 */       int y1 = TYPE_INFO_COORDINATES[i][1];
/* 179 */       matrix.set(x1, y1, bit);
/*     */       
/* 181 */       if (i < 8) {
/*     */         
/* 183 */         int x2 = matrix.getWidth() - i - 1;
/*     */         
/* 185 */         matrix.set(x2, 8, bit);
/*     */       }
/*     */       else {
/*     */         
/* 189 */         int y2 = matrix.getHeight() - 7 + i - 8;
/* 190 */         matrix.set(8, y2, bit);
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   static void maybeEmbedVersionInfo(Version version, ByteMatrix matrix) throws WriterException {
/* 198 */     if (version.getVersionNumber() < 7) {
/*     */       return;
/*     */     }
/* 201 */     BitArray versionInfoBits = new BitArray();
/* 202 */     makeVersionInfoBits(version, versionInfoBits);
/*     */     
/* 204 */     int bitIndex = 17;
/* 205 */     for (int i = 0; i < 6; i++) {
/* 206 */       for (int j = 0; j < 3; j++) {
/*     */         
/* 208 */         boolean bit = versionInfoBits.get(bitIndex);
/* 209 */         bitIndex--;
/*     */         
/* 211 */         matrix.set(i, matrix.getHeight() - 11 + j, bit);
/*     */         
/* 213 */         matrix.set(matrix.getHeight() - 11 + j, i, bit);
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static void embedDataBits(BitArray dataBits, int maskPattern, ByteMatrix matrix) throws WriterException {
/* 223 */     int bitIndex = 0;
/* 224 */     int direction = -1;
/*     */     
/* 226 */     int x = matrix.getWidth() - 1;
/* 227 */     int y = matrix.getHeight() - 1;
/* 228 */     while (x > 0) {
/*     */       
/* 230 */       if (x == 6) {
/* 231 */         x--;
/*     */       }
/* 233 */       while (y >= 0 && y < matrix.getHeight()) {
/* 234 */         for (int i = 0; i < 2; i++) {
/* 235 */           int xx = x - i;
/*     */           
/* 237 */           if (isEmpty(matrix.get(xx, y))) {
/*     */             boolean bit;
/*     */ 
/*     */             
/* 241 */             if (bitIndex < dataBits.getSize()) {
/* 242 */               bit = dataBits.get(bitIndex);
/* 243 */               bitIndex++;
/*     */             }
/*     */             else {
/*     */               
/* 247 */               bit = false;
/*     */             } 
/*     */ 
/*     */             
/* 251 */             if (maskPattern != -1 && MaskUtil.getDataMaskBit(maskPattern, xx, y)) {
/* 252 */               bit = !bit;
/*     */             }
/* 254 */             matrix.set(xx, y, bit);
/*     */           } 
/* 256 */         }  y += direction;
/*     */       } 
/* 258 */       direction = -direction;
/* 259 */       y += direction;
/* 260 */       x -= 2;
/*     */     } 
/*     */     
/* 263 */     if (bitIndex != dataBits.getSize()) {
/* 264 */       throw new WriterException("Not all bits consumed: " + bitIndex + '/' + dataBits.getSize());
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static int findMSBSet(int value) {
/* 274 */     return 32 - Integer.numberOfLeadingZeros(value);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static int calculateBCHCode(int value, int poly) {
/* 303 */     if (poly == 0) {
/* 304 */       throw new IllegalArgumentException("0 polynomial");
/*     */     }
/*     */ 
/*     */     
/* 308 */     int msbSetInPoly = findMSBSet(poly);
/* 309 */     value <<= msbSetInPoly - 1;
/*     */     
/* 311 */     while (findMSBSet(value) >= msbSetInPoly) {
/* 312 */       value ^= poly << findMSBSet(value) - msbSetInPoly;
/*     */     }
/*     */     
/* 315 */     return value;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static void makeTypeInfoBits(ErrorCorrectionLevel ecLevel, int maskPattern, BitArray bits) throws WriterException {
/* 323 */     if (!QRCode.isValidMaskPattern(maskPattern)) {
/* 324 */       throw new WriterException("Invalid mask pattern");
/*     */     }
/* 326 */     int typeInfo = ecLevel.getBits() << 3 | maskPattern;
/* 327 */     bits.appendBits(typeInfo, 5);
/*     */     
/* 329 */     int bchCode = calculateBCHCode(typeInfo, 1335);
/* 330 */     bits.appendBits(bchCode, 10);
/*     */     
/*     */     BitArray maskBits;
/* 333 */     (maskBits = new BitArray()).appendBits(21522, 15);
/* 334 */     bits.xor(maskBits);
/*     */     
/* 336 */     if (bits.getSize() != 15) {
/* 337 */       throw new WriterException("should not happen but we got: " + bits.getSize());
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   static void makeVersionInfoBits(Version version, BitArray bits) throws WriterException {
/* 344 */     bits.appendBits(version.getVersionNumber(), 6);
/* 345 */     int bchCode = calculateBCHCode(version.getVersionNumber(), 7973);
/* 346 */     bits.appendBits(bchCode, 12);
/*     */     
/* 348 */     if (bits.getSize() != 18) {
/* 349 */       throw new WriterException("should not happen but we got: " + bits.getSize());
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   private static boolean isEmpty(int value) {
/* 355 */     return (value == -1);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static void embedTimingPatterns(ByteMatrix matrix) {
/* 361 */     for (int i = 8; i < matrix.getWidth() - 8; i++) {
/* 362 */       int bit = (i + 1) % 2;
/*     */       
/* 364 */       if (isEmpty(matrix.get(i, 6))) {
/* 365 */         matrix.set(i, 6, bit);
/*     */       }
/*     */       
/* 368 */       if (isEmpty(matrix.get(6, i))) {
/* 369 */         matrix.set(6, i, bit);
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private static void embedDarkDotAtLeftBottomCorner(ByteMatrix matrix) throws WriterException {
/* 376 */     if (matrix.get(8, matrix.getHeight() - 8) == 0) {
/* 377 */       throw new WriterException();
/*     */     }
/* 379 */     matrix.set(8, matrix.getHeight() - 8, 1);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static void embedHorizontalSeparationPattern(int xStart, int yStart, ByteMatrix matrix) throws WriterException {
/* 385 */     for (int x = 0; x < 8; x++) {
/* 386 */       if (!isEmpty(matrix.get(xStart + x, yStart))) {
/* 387 */         throw new WriterException();
/*     */       }
/* 389 */       matrix.set(xStart + x, yStart, 0);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static void embedVerticalSeparationPattern(int xStart, int yStart, ByteMatrix matrix) throws WriterException {
/* 396 */     for (int y = 0; y < 7; y++) {
/* 397 */       if (!isEmpty(matrix.get(xStart, yStart + y))) {
/* 398 */         throw new WriterException();
/*     */       }
/* 400 */       matrix.set(xStart, yStart + y, 0);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static void embedPositionAdjustmentPattern(int xStart, int yStart, ByteMatrix matrix) {
/* 408 */     for (int y = 0; y < 5; y++) {
/* 409 */       for (int x = 0; x < 5; x++) {
/* 410 */         matrix.set(xStart + x, yStart + y, POSITION_ADJUSTMENT_PATTERN[y][x]);
/*     */       }
/*     */     } 
/*     */   }
/*     */   
/*     */   private static void embedPositionDetectionPattern(int xStart, int yStart, ByteMatrix matrix) {
/* 416 */     for (int y = 0; y < 7; y++) {
/* 417 */       for (int x = 0; x < 7; x++) {
/* 418 */         matrix.set(xStart + x, yStart + y, POSITION_DETECTION_PATTERN[y][x]);
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static void embedPositionDetectionPatternsAndSeparators(ByteMatrix matrix) throws WriterException {
/* 426 */     int pdpWidth = (POSITION_DETECTION_PATTERN[0]).length;
/*     */     
/* 428 */     embedPositionDetectionPattern(0, 0, matrix);
/*     */     
/* 430 */     embedPositionDetectionPattern(matrix.getWidth() - pdpWidth, 0, matrix);
/*     */     
/* 432 */     embedPositionDetectionPattern(0, matrix.getWidth() - pdpWidth, matrix);
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 437 */     embedHorizontalSeparationPattern(0, 7, matrix);
/*     */     
/* 439 */     embedHorizontalSeparationPattern(matrix.getWidth() - 8, 7, matrix);
/*     */ 
/*     */     
/* 442 */     embedHorizontalSeparationPattern(0, matrix.getWidth() - 8, matrix);
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 447 */     embedVerticalSeparationPattern(7, 0, matrix);
/*     */     
/* 449 */     embedVerticalSeparationPattern(matrix.getHeight() - 7 - 1, 0, matrix);
/*     */     
/* 451 */     embedVerticalSeparationPattern(7, matrix.getHeight() - 7, matrix);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static void maybeEmbedPositionAdjustmentPatterns(Version version, ByteMatrix matrix) {
/* 457 */     if (version.getVersionNumber() < 2) {
/*     */       return;
/*     */     }
/* 460 */     int index = version.getVersionNumber() - 1;
/* 461 */     int[] coordinates = POSITION_ADJUSTMENT_PATTERN_COORDINATE_TABLE[index];
/* 462 */     int numCoordinates = (POSITION_ADJUSTMENT_PATTERN_COORDINATE_TABLE[index]).length;
/* 463 */     for (int i = 0; i < numCoordinates; i++) {
/* 464 */       for (int j = 0; j < numCoordinates; j++) {
/* 465 */         int y = coordinates[i];
/*     */         int x;
/* 467 */         if ((x = coordinates[j]) != -1 && y != -1)
/*     */         {
/*     */ 
/*     */           
/* 471 */           if (isEmpty(matrix.get(x, y)))
/*     */           {
/*     */             
/* 474 */             embedPositionAdjustmentPattern(x - 2, y - 2, matrix);
/*     */           }
/*     */         }
/*     */       } 
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\zxing\qrcode\encoder\MatrixUtil.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */