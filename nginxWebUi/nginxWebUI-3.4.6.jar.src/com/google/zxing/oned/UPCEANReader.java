/*     */ package com.google.zxing.oned;
/*     */ 
/*     */ import com.google.zxing.BarcodeFormat;
/*     */ import com.google.zxing.ChecksumException;
/*     */ import com.google.zxing.DecodeHintType;
/*     */ import com.google.zxing.FormatException;
/*     */ import com.google.zxing.NotFoundException;
/*     */ import com.google.zxing.ReaderException;
/*     */ import com.google.zxing.Result;
/*     */ import com.google.zxing.ResultMetadataType;
/*     */ import com.google.zxing.ResultPoint;
/*     */ import com.google.zxing.ResultPointCallback;
/*     */ import com.google.zxing.common.BitArray;
/*     */ import java.util.Arrays;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class UPCEANReader
/*     */   extends OneDReader
/*     */ {
/*     */   private static final float MAX_AVG_VARIANCE = 0.48F;
/*     */   private static final float MAX_INDIVIDUAL_VARIANCE = 0.7F;
/*  53 */   static final int[] START_END_PATTERN = new int[] { 1, 1, 1 };
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  58 */   static final int[] MIDDLE_PATTERN = new int[] { 1, 1, 1, 1, 1 };
/*     */ 
/*     */ 
/*     */   
/*  62 */   static final int[] END_PATTERN = new int[] { 1, 1, 1, 1, 1, 1 };
/*     */ 
/*     */ 
/*     */   
/*  66 */   static final int[][] L_PATTERNS = new int[][] { { 3, 2, 1, 1 }, { 2, 2, 2, 1 }, { 2, 1, 2, 2 }, { 1, 4, 1, 1 }, { 1, 1, 3, 2 }, { 1, 2, 3, 1 }, { 1, 1, 1, 4 }, { 1, 3, 1, 2 }, { 1, 2, 1, 3 }, { 3, 1, 1, 2 } };
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
/*  85 */   static final int[][] L_AND_G_PATTERNS = new int[20][]; static {
/*  86 */     System.arraycopy(L_PATTERNS, 0, L_AND_G_PATTERNS, 0, 10);
/*  87 */     for (int i = 10; i < 20; i++) {
/*     */       
/*  89 */       int[] widths, reversedWidths = new int[(widths = L_PATTERNS[i - 10]).length];
/*  90 */       for (int j = 0; j < widths.length; j++) {
/*  91 */         reversedWidths[j] = widths[widths.length - j - 1];
/*     */       }
/*  93 */       L_AND_G_PATTERNS[i] = reversedWidths;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 102 */   private final StringBuilder decodeRowStringBuffer = new StringBuilder(20);
/* 103 */   private final UPCEANExtensionSupport extensionReader = new UPCEANExtensionSupport();
/* 104 */   private final EANManufacturerOrgSupport eanManSupport = new EANManufacturerOrgSupport();
/*     */ 
/*     */   
/*     */   static int[] findStartGuardPattern(BitArray row) throws NotFoundException {
/* 108 */     boolean foundStart = false;
/* 109 */     int[] startRange = null;
/* 110 */     int nextStart = 0;
/* 111 */     int[] counters = new int[START_END_PATTERN.length];
/* 112 */     while (!foundStart) {
/* 113 */       Arrays.fill(counters, 0, START_END_PATTERN.length, 0);
/*     */       
/* 115 */       int start = (startRange = findGuardPattern(row, nextStart, false, START_END_PATTERN, counters))[0];
/* 116 */       nextStart = startRange[1];
/*     */ 
/*     */       
/*     */       int quietStart;
/*     */       
/* 121 */       if ((quietStart = start - nextStart - start) >= 0) {
/* 122 */         foundStart = row.isRange(quietStart, start, false);
/*     */       }
/*     */     } 
/* 125 */     return startRange;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Result decodeRow(int rowNumber, BitArray row, Map<DecodeHintType, ?> hints) throws NotFoundException, ChecksumException, FormatException {
/* 131 */     return decodeRow(rowNumber, row, findStartGuardPattern(row), hints);
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
/*     */   public Result decodeRow(int rowNumber, BitArray row, int[] startGuardRange, Map<DecodeHintType, ?> hints) throws NotFoundException, ChecksumException, FormatException {
/*     */     ResultPointCallback resultPointCallback;
/* 157 */     if ((resultPointCallback = (ResultPointCallback)((hints == null) ? null : hints.get(DecodeHintType.NEED_RESULT_POINT_CALLBACK))) != null) {
/* 158 */       resultPointCallback.foundPossibleResultPoint(new ResultPoint((startGuardRange[0] + startGuardRange[1]) / 2.0F, rowNumber));
/*     */     }
/*     */ 
/*     */     
/*     */     StringBuilder result;
/*     */     
/* 164 */     (result = this.decodeRowStringBuffer).setLength(0);
/* 165 */     int endStart = decodeMiddle(row, startGuardRange, result);
/*     */     
/* 167 */     if (resultPointCallback != null) {
/* 168 */       resultPointCallback.foundPossibleResultPoint(new ResultPoint(endStart, rowNumber));
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 173 */     int[] endRange = decodeEnd(row, endStart);
/*     */     
/* 175 */     if (resultPointCallback != null) {
/* 176 */       resultPointCallback.foundPossibleResultPoint(new ResultPoint((endRange[0] + endRange[1]) / 2.0F, rowNumber));
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     int end, quietEnd;
/*     */ 
/*     */ 
/*     */     
/* 186 */     if ((quietEnd = endRange[1] + (end = endRange[1]) - endRange[0]) >= row.getSize() || !row.isRange(end, quietEnd, false)) {
/* 187 */       throw NotFoundException.getNotFoundInstance();
/*     */     }
/*     */     
/*     */     String resultString;
/*     */     
/* 192 */     if ((resultString = result.toString()).length() < 8) {
/* 193 */       throw FormatException.getFormatInstance();
/*     */     }
/* 195 */     if (!checkChecksum(resultString)) {
/* 196 */       throw ChecksumException.getChecksumInstance();
/*     */     }
/*     */     
/* 199 */     float left = (startGuardRange[1] + startGuardRange[0]) / 2.0F;
/* 200 */     float right = (endRange[1] + endRange[0]) / 2.0F;
/* 201 */     BarcodeFormat format = getBarcodeFormat();
/* 202 */     Result decodeResult = new Result(resultString, null, new ResultPoint[] { new ResultPoint(left, rowNumber), new ResultPoint(right, rowNumber) }format);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 209 */     int extensionLength = 0;
/*     */     
/*     */     try {
/* 212 */       Result extensionResult = this.extensionReader.decodeRow(rowNumber, row, endRange[1]);
/* 213 */       decodeResult.putMetadata(ResultMetadataType.UPC_EAN_EXTENSION, extensionResult.getText());
/* 214 */       decodeResult.putAllMetadata(extensionResult.getResultMetadata());
/* 215 */       decodeResult.addResultPoints(extensionResult.getResultPoints());
/* 216 */       extensionLength = extensionResult.getText().length();
/* 217 */     } catch (ReaderException readerException) {}
/*     */ 
/*     */     
/*     */     int[] allowedExtensions;
/*     */ 
/*     */     
/* 223 */     if ((allowedExtensions = (int[])((hints == null) ? null : hints.get(DecodeHintType.ALLOWED_EAN_EXTENSIONS))) != null) {
/* 224 */       boolean valid = false; int arrayOfInt[], i; byte b;
/* 225 */       for (i = (arrayOfInt = allowedExtensions).length, b = 0; b < i; ) { int length = arrayOfInt[b];
/* 226 */         if (extensionLength == length) {
/* 227 */           valid = true; break;
/*     */         } 
/*     */         b++; }
/*     */       
/* 231 */       if (!valid) {
/* 232 */         throw NotFoundException.getNotFoundInstance();
/*     */       }
/*     */     } 
/*     */     String countryID;
/* 236 */     if ((format == BarcodeFormat.EAN_13 || format == BarcodeFormat.UPC_A) && (
/*     */       
/* 238 */       countryID = this.eanManSupport.lookupCountryIdentifier(resultString)) != null) {
/* 239 */       decodeResult.putMetadata(ResultMetadataType.POSSIBLE_COUNTRY, countryID);
/*     */     }
/*     */ 
/*     */     
/* 243 */     return decodeResult;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   boolean checkChecksum(String s) throws FormatException {
/* 252 */     return checkStandardUPCEANChecksum(s);
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
/*     */   static boolean checkStandardUPCEANChecksum(CharSequence s) throws FormatException {
/*     */     int length;
/* 265 */     if ((length = s.length()) == 0) {
/* 266 */       return false;
/*     */     }
/*     */     
/* 269 */     int sum = 0; int i;
/* 270 */     for (i = length - 2; i >= 0; i -= 2) {
/*     */       int digit;
/* 272 */       if ((digit = s.charAt(i) - 48) < 0 || digit > 9) {
/* 273 */         throw FormatException.getFormatInstance();
/*     */       }
/* 275 */       sum += digit;
/*     */     } 
/* 277 */     sum *= 3;
/* 278 */     for (i = length - 1; i >= 0; i -= 2) {
/*     */       int digit;
/* 280 */       if ((digit = s.charAt(i) - 48) < 0 || digit > 9) {
/* 281 */         throw FormatException.getFormatInstance();
/*     */       }
/* 283 */       sum += digit;
/*     */     } 
/* 285 */     return (sum % 10 == 0);
/*     */   }
/*     */   
/*     */   int[] decodeEnd(BitArray row, int endStart) throws NotFoundException {
/* 289 */     return findGuardPattern(row, endStart, false, START_END_PATTERN);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static int[] findGuardPattern(BitArray row, int rowOffset, boolean whiteFirst, int[] pattern) throws NotFoundException {
/* 296 */     return findGuardPattern(row, rowOffset, whiteFirst, pattern, new int[pattern.length]);
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
/*     */   private static int[] findGuardPattern(BitArray row, int rowOffset, boolean whiteFirst, int[] pattern, int[] counters) throws NotFoundException {
/* 315 */     int width = row.getSize();
/* 316 */     rowOffset = whiteFirst ? row.getNextUnset(rowOffset) : row.getNextSet(rowOffset);
/* 317 */     int counterPosition = 0;
/* 318 */     int patternStart = rowOffset;
/* 319 */     int patternLength = pattern.length;
/* 320 */     boolean isWhite = whiteFirst;
/* 321 */     for (int x = rowOffset; x < width; x++) {
/* 322 */       if (row.get(x) ^ isWhite) {
/* 323 */         counters[counterPosition] = counters[counterPosition] + 1;
/*     */       } else {
/* 325 */         if (counterPosition == patternLength - 1) {
/* 326 */           if (patternMatchVariance(counters, pattern, 0.7F) < 0.48F) {
/* 327 */             return new int[] { patternStart, x };
/*     */           }
/* 329 */           patternStart += counters[0] + counters[1];
/* 330 */           System.arraycopy(counters, 2, counters, 0, patternLength - 2);
/* 331 */           counters[patternLength - 2] = 0;
/* 332 */           counters[patternLength - 1] = 0;
/* 333 */           counterPosition--;
/*     */         } else {
/* 335 */           counterPosition++;
/*     */         } 
/* 337 */         counters[counterPosition] = 1;
/* 338 */         isWhite = !isWhite;
/*     */       } 
/*     */     } 
/* 341 */     throw NotFoundException.getNotFoundInstance();
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
/*     */   static int decodeDigit(BitArray row, int[] counters, int rowOffset, int[][] patterns) throws NotFoundException {
/* 358 */     recordPattern(row, rowOffset, counters);
/* 359 */     float bestVariance = 0.48F;
/* 360 */     int bestMatch = -1;
/* 361 */     int max = patterns.length;
/* 362 */     for (int i = 0; i < max; i++) {
/* 363 */       int[] pattern = patterns[i];
/*     */       float variance;
/* 365 */       if ((variance = patternMatchVariance(counters, pattern, 0.7F)) < bestVariance) {
/* 366 */         bestVariance = variance;
/* 367 */         bestMatch = i;
/*     */       } 
/*     */     } 
/* 370 */     if (bestMatch >= 0) {
/* 371 */       return bestMatch;
/*     */     }
/* 373 */     throw NotFoundException.getNotFoundInstance();
/*     */   }
/*     */   
/*     */   abstract BarcodeFormat getBarcodeFormat();
/*     */   
/*     */   protected abstract int decodeMiddle(BitArray paramBitArray, int[] paramArrayOfint, StringBuilder paramStringBuilder) throws NotFoundException;
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\zxing\oned\UPCEANReader.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */