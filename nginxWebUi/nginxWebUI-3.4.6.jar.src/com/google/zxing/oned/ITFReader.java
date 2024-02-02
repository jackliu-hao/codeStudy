/*     */ package com.google.zxing.oned;
/*     */ 
/*     */ import com.google.zxing.BarcodeFormat;
/*     */ import com.google.zxing.DecodeHintType;
/*     */ import com.google.zxing.FormatException;
/*     */ import com.google.zxing.NotFoundException;
/*     */ import com.google.zxing.Result;
/*     */ import com.google.zxing.ResultPoint;
/*     */ import com.google.zxing.common.BitArray;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class ITFReader
/*     */   extends OneDReader
/*     */ {
/*     */   private static final float MAX_AVG_VARIANCE = 0.38F;
/*     */   private static final float MAX_INDIVIDUAL_VARIANCE = 0.78F;
/*     */   private static final int W = 3;
/*     */   private static final int N = 1;
/*  54 */   private static final int[] DEFAULT_ALLOWED_LENGTHS = new int[] { 6, 8, 10, 12, 14 };
/*     */ 
/*     */   
/*  57 */   private int narrowLineWidth = -1;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  65 */   private static final int[] START_PATTERN = new int[] { 1, 1, 1, 1 };
/*  66 */   private static final int[] END_PATTERN_REVERSED = new int[] { 1, 1, 3 };
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  71 */   static final int[][] PATTERNS = new int[][] { { 1, 1, 3, 3, 1 }, { 3, 1, 1, 1, 3 }, { 1, 3, 1, 1, 3 }, { 3, 3, 1, 1, 1 }, { 1, 1, 3, 1, 3 }, { 3, 1, 3, 1, 1 }, { 1, 3, 3, 1, 1 }, { 1, 1, 1, 3, 3 }, { 3, 1, 1, 3, 1 }, { 1, 3, 1, 3, 1 } };
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
/*     */   public Result decodeRow(int rowNumber, BitArray row, Map<DecodeHintType, ?> hints) throws FormatException, NotFoundException {
/*  89 */     int[] startRange = decodeStart(row);
/*  90 */     int[] endRange = decodeEnd(row);
/*     */     
/*  92 */     StringBuilder result = new StringBuilder(20);
/*  93 */     decodeMiddle(row, startRange[1], endRange[0], result);
/*  94 */     String resultString = result.toString();
/*     */     
/*  96 */     int[] allowedLengths = null;
/*  97 */     if (hints != null) {
/*  98 */       allowedLengths = (int[])hints.get(DecodeHintType.ALLOWED_LENGTHS);
/*     */     }
/*     */     
/* 101 */     if (allowedLengths == null) {
/* 102 */       allowedLengths = DEFAULT_ALLOWED_LENGTHS;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 107 */     int length = resultString.length();
/* 108 */     boolean lengthOK = false;
/* 109 */     int maxAllowedLength = 0; int arrayOfInt1[], i; byte b;
/* 110 */     for (i = (arrayOfInt1 = allowedLengths).length, b = 0; b < i; ) { int allowedLength = arrayOfInt1[b];
/* 111 */       if (length == allowedLength) {
/* 112 */         lengthOK = true;
/*     */         break;
/*     */       } 
/* 115 */       if (allowedLength > maxAllowedLength)
/* 116 */         maxAllowedLength = allowedLength; 
/*     */       b++; }
/*     */     
/* 119 */     if (!lengthOK && length > maxAllowedLength) {
/* 120 */       lengthOK = true;
/*     */     }
/* 122 */     if (!lengthOK) {
/* 123 */       throw FormatException.getFormatInstance();
/*     */     }
/*     */     
/* 126 */     return new Result(resultString, null, new ResultPoint[] { new ResultPoint(startRange[1], rowNumber), new ResultPoint(endRange[0], rowNumber) }BarcodeFormat.ITF);
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
/*     */   private static void decodeMiddle(BitArray row, int payloadStart, int payloadEnd, StringBuilder resultString) throws NotFoundException {
/* 150 */     int[] counterDigitPair = new int[10];
/* 151 */     int[] counterBlack = new int[5];
/* 152 */     int[] counterWhite = new int[5];
/*     */     
/* 154 */     while (payloadStart < payloadEnd) {
/*     */ 
/*     */       
/* 157 */       recordPattern(row, payloadStart, counterDigitPair);
/*     */       
/* 159 */       for (int k = 0; k < 5; k++) {
/* 160 */         int twoK = 2 * k;
/* 161 */         counterBlack[k] = counterDigitPair[twoK];
/* 162 */         counterWhite[k] = counterDigitPair[twoK + 1];
/*     */       } 
/*     */       
/* 165 */       int bestMatch = decodeDigit(counterBlack);
/* 166 */       resultString.append((char)(bestMatch + 48));
/* 167 */       bestMatch = decodeDigit(counterWhite);
/* 168 */       resultString.append((char)(bestMatch + 48)); int[] arrayOfInt;
/*     */       byte b;
/* 170 */       for (arrayOfInt = counterDigitPair, b = 0; b < 10; ) { int counterDigit = arrayOfInt[b];
/* 171 */         payloadStart += counterDigit;
/*     */         b++; }
/*     */     
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private int[] decodeStart(BitArray row) throws NotFoundException {
/* 184 */     int endStart = skipWhiteSpace(row);
/* 185 */     int[] startPattern = findGuardPattern(row, endStart, START_PATTERN);
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 190 */     this.narrowLineWidth = (startPattern[1] - startPattern[0]) / 4;
/*     */     
/* 192 */     validateQuietZone(row, startPattern[0]);
/*     */     
/* 194 */     return startPattern;
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
/*     */   private void validateQuietZone(BitArray row, int startPattern) throws NotFoundException {
/* 217 */     int quietCount = ((quietCount = this.narrowLineWidth * 10) < startPattern) ? quietCount : startPattern;
/*     */     
/* 219 */     for (int i = startPattern - 1; quietCount > 0 && i >= 0 && 
/* 220 */       !row.get(i); i--)
/*     */     {
/*     */       
/* 223 */       quietCount--;
/*     */     }
/* 225 */     if (quietCount != 0)
/*     */     {
/* 227 */       throw NotFoundException.getNotFoundInstance();
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
/*     */   private static int skipWhiteSpace(BitArray row) throws NotFoundException {
/* 239 */     int width = row.getSize();
/*     */     int endStart;
/* 241 */     if ((endStart = row.getNextSet(0)) == width) {
/* 242 */       throw NotFoundException.getNotFoundInstance();
/*     */     }
/*     */     
/* 245 */     return endStart;
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
/*     */   private int[] decodeEnd(BitArray row) throws NotFoundException {
/* 259 */     row.reverse();
/*     */     try {
/* 261 */       int endStart = skipWhiteSpace(row);
/* 262 */       int[] endPattern = findGuardPattern(row, endStart, END_PATTERN_REVERSED);
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 267 */       validateQuietZone(row, endPattern[0]);
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 272 */       int temp = endPattern[0];
/* 273 */       endPattern[0] = row.getSize() - endPattern[1];
/* 274 */       endPattern[1] = row.getSize() - temp;
/*     */       
/* 276 */       return endPattern;
/*     */     } finally {
/*     */       
/* 279 */       row.reverse();
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
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static int[] findGuardPattern(BitArray row, int rowOffset, int[] pattern) throws NotFoundException {
/* 296 */     int patternLength, counters[] = new int[patternLength = pattern.length];
/* 297 */     int width = row.getSize();
/* 298 */     boolean isWhite = false;
/*     */     
/* 300 */     int counterPosition = 0;
/* 301 */     int patternStart = rowOffset;
/* 302 */     for (int x = rowOffset; x < width; x++) {
/* 303 */       if (row.get(x) ^ isWhite) {
/* 304 */         counters[counterPosition] = counters[counterPosition] + 1;
/*     */       } else {
/* 306 */         if (counterPosition == patternLength - 1) {
/* 307 */           if (patternMatchVariance(counters, pattern, 0.78F) < 0.38F) {
/* 308 */             return new int[] { patternStart, x };
/*     */           }
/* 310 */           patternStart += counters[0] + counters[1];
/* 311 */           System.arraycopy(counters, 2, counters, 0, patternLength - 2);
/* 312 */           counters[patternLength - 2] = 0;
/* 313 */           counters[patternLength - 1] = 0;
/* 314 */           counterPosition--;
/*     */         } else {
/* 316 */           counterPosition++;
/*     */         } 
/* 318 */         counters[counterPosition] = 1;
/* 319 */         isWhite = !isWhite;
/*     */       } 
/*     */     } 
/* 322 */     throw NotFoundException.getNotFoundInstance();
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
/*     */   private static int decodeDigit(int[] counters) throws NotFoundException {
/* 334 */     float bestVariance = 0.38F;
/* 335 */     int bestMatch = -1;
/* 336 */     int max = PATTERNS.length;
/* 337 */     for (int i = 0; i < max; i++) {
/* 338 */       int[] pattern = PATTERNS[i];
/*     */       float variance;
/* 340 */       if ((variance = patternMatchVariance(counters, pattern, 0.78F)) < bestVariance) {
/* 341 */         bestVariance = variance;
/* 342 */         bestMatch = i;
/*     */       } 
/*     */     } 
/* 345 */     if (bestMatch >= 0) {
/* 346 */       return bestMatch;
/*     */     }
/* 348 */     throw NotFoundException.getNotFoundInstance();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\zxing\oned\ITFReader.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */