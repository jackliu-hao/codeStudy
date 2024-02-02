/*     */ package com.google.zxing.oned.rss;
/*     */ 
/*     */ import com.google.zxing.BarcodeFormat;
/*     */ import com.google.zxing.DecodeHintType;
/*     */ import com.google.zxing.NotFoundException;
/*     */ import com.google.zxing.Result;
/*     */ import com.google.zxing.ResultPoint;
/*     */ import com.google.zxing.ResultPointCallback;
/*     */ import com.google.zxing.common.BitArray;
/*     */ import com.google.zxing.common.detector.MathUtils;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
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
/*     */ public final class RSS14Reader
/*     */   extends AbstractRSSReader
/*     */ {
/*  38 */   private static final int[] OUTSIDE_EVEN_TOTAL_SUBSET = new int[] { 1, 10, 34, 70, 126 };
/*  39 */   private static final int[] INSIDE_ODD_TOTAL_SUBSET = new int[] { 4, 20, 48, 81 };
/*  40 */   private static final int[] OUTSIDE_GSUM = new int[] { 0, 161, 961, 2015, 2715 };
/*  41 */   private static final int[] INSIDE_GSUM = new int[] { 0, 336, 1036, 1516 };
/*  42 */   private static final int[] OUTSIDE_ODD_WIDEST = new int[] { 8, 6, 4, 3, 1 };
/*  43 */   private static final int[] INSIDE_ODD_WIDEST = new int[] { 2, 4, 6, 8 };
/*     */   
/*  45 */   private static final int[][] FINDER_PATTERNS = new int[][] { { 3, 8, 2, 1 }, { 3, 5, 5, 1 }, { 3, 3, 7, 1 }, { 3, 1, 9, 1 }, { 2, 7, 4, 1 }, { 2, 5, 6, 1 }, { 2, 3, 8, 1 }, { 1, 5, 7, 1 }, { 1, 3, 9, 1 } };
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
/*  61 */   private final List<Pair> possibleLeftPairs = new ArrayList<>();
/*  62 */   private final List<Pair> possibleRightPairs = new ArrayList<>();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Result decodeRow(int rowNumber, BitArray row, Map<DecodeHintType, ?> hints) throws NotFoundException {
/*  69 */     Pair leftPair = decodePair(row, false, rowNumber, hints);
/*  70 */     addOrTally(this.possibleLeftPairs, leftPair);
/*  71 */     row.reverse();
/*  72 */     Pair rightPair = decodePair(row, true, rowNumber, hints);
/*  73 */     addOrTally(this.possibleRightPairs, rightPair);
/*  74 */     row.reverse();
/*  75 */     for (Iterator<Pair> iterator = this.possibleLeftPairs.iterator(); iterator.hasNext();) {
/*  76 */       if ((left = iterator.next()).getCount() > 1) {
/*  77 */         for (Iterator<Pair> iterator1 = this.possibleRightPairs.iterator(); iterator1.hasNext();) {
/*  78 */           if ((right = iterator1.next()).getCount() > 1 && 
/*  79 */             checkChecksum(left, right)) {
/*  80 */             return constructResult(left, right);
/*     */           }
/*     */         } 
/*     */       }
/*     */     } 
/*     */     
/*  86 */     throw NotFoundException.getNotFoundInstance();
/*     */   }
/*     */   
/*     */   private static void addOrTally(Collection<Pair> possiblePairs, Pair pair) {
/*  90 */     if (pair == null) {
/*     */       return;
/*     */     }
/*  93 */     boolean found = false;
/*  94 */     for (Iterator<Pair> iterator = possiblePairs.iterator(); iterator.hasNext();) {
/*  95 */       if ((other = iterator.next()).getValue() == pair.getValue()) {
/*  96 */         other.incrementCount();
/*  97 */         found = true;
/*     */         break;
/*     */       } 
/*     */     } 
/* 101 */     if (!found) {
/* 102 */       possiblePairs.add(pair);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public void reset() {
/* 108 */     this.possibleLeftPairs.clear();
/* 109 */     this.possibleRightPairs.clear();
/*     */   }
/*     */ 
/*     */   
/*     */   private static Result constructResult(Pair leftPair, Pair rightPair) {
/* 114 */     String text = String.valueOf(4537077L * leftPair.getValue() + rightPair.getValue());
/*     */     
/* 116 */     StringBuilder buffer = new StringBuilder(14);
/* 117 */     for (int i = 13 - text.length(); i > 0; i--) {
/* 118 */       buffer.append('0');
/*     */     }
/* 120 */     buffer.append(text);
/*     */     
/* 122 */     int checkDigit = 0;
/* 123 */     for (int j = 0; j < 13; j++) {
/* 124 */       int digit = buffer.charAt(j) - 48;
/* 125 */       checkDigit += ((j & 0x1) == 0) ? (3 * digit) : digit;
/*     */     } 
/*     */     
/* 128 */     if ((checkDigit = 10 - checkDigit % 10) == 10) {
/* 129 */       checkDigit = 0;
/*     */     }
/* 131 */     buffer.append(checkDigit);
/*     */     
/* 133 */     ResultPoint[] leftPoints = leftPair.getFinderPattern().getResultPoints();
/* 134 */     ResultPoint[] rightPoints = rightPair.getFinderPattern().getResultPoints();
/* 135 */     return new Result(
/* 136 */         String.valueOf(buffer.toString()), null, new ResultPoint[] { leftPoints[0], leftPoints[1], rightPoints[0], rightPoints[1] }, BarcodeFormat.RSS_14);
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
/*     */   private static boolean checkChecksum(Pair leftPair, Pair rightPair) {
/* 148 */     int checkValue = (leftPair.getChecksumPortion() + 16 * rightPair.getChecksumPortion()) % 79;
/*     */     
/*     */     int targetCheckValue;
/* 151 */     if ((targetCheckValue = 9 * leftPair.getFinderPattern().getValue() + rightPair.getFinderPattern().getValue()) > 72) {
/* 152 */       targetCheckValue--;
/*     */     }
/* 154 */     if (targetCheckValue > 8) {
/* 155 */       targetCheckValue--;
/*     */     }
/* 157 */     return (checkValue == targetCheckValue);
/*     */   }
/*     */   
/*     */   private Pair decodePair(BitArray row, boolean right, int rowNumber, Map<DecodeHintType, ?> hints) {
/*     */     try {
/* 162 */       int[] startEnd = findFinderPattern(row, 0, right);
/* 163 */       FinderPattern pattern = parseFoundFinderPattern(row, rowNumber, right, startEnd);
/*     */ 
/*     */       
/*     */       ResultPointCallback resultPointCallback;
/*     */       
/* 168 */       if ((resultPointCallback = (ResultPointCallback)((hints == null) ? null : hints.get(DecodeHintType.NEED_RESULT_POINT_CALLBACK))) != null) {
/* 169 */         float center = (startEnd[0] + startEnd[1]) / 2.0F;
/* 170 */         if (right)
/*     */         {
/* 172 */           center = (row.getSize() - 1) - center;
/*     */         }
/* 174 */         resultPointCallback.foundPossibleResultPoint(new ResultPoint(center, rowNumber));
/*     */       } 
/*     */       
/* 177 */       DataCharacter outside = decodeDataCharacter(row, pattern, true);
/* 178 */       DataCharacter inside = decodeDataCharacter(row, pattern, false);
/* 179 */       return new Pair(1597 * outside.getValue() + inside.getValue(), outside
/* 180 */           .getChecksumPortion() + 4 * inside.getChecksumPortion(), pattern);
/*     */     }
/* 182 */     catch (NotFoundException notFoundException) {
/* 183 */       return null;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private DataCharacter decodeDataCharacter(BitArray row, FinderPattern pattern, boolean outsideChar) throws NotFoundException {
/*     */     int[] counters;
/* 191 */     (counters = getDataCharacterCounters())[0] = 0;
/* 192 */     counters[1] = 0;
/* 193 */     counters[2] = 0;
/* 194 */     counters[3] = 0;
/* 195 */     counters[4] = 0;
/* 196 */     counters[5] = 0;
/* 197 */     counters[6] = 0;
/* 198 */     counters[7] = 0;
/*     */     
/* 200 */     if (outsideChar) {
/* 201 */       recordPatternInReverse(row, pattern.getStartEnd()[0], counters);
/*     */     } else {
/* 203 */       recordPattern(row, pattern.getStartEnd()[1] + 1, counters);
/*     */       
/* 205 */       for (int m = 0, n = counters.length - 1; m < n; m++, n--) {
/* 206 */         int temp = counters[m];
/* 207 */         counters[m] = counters[n];
/* 208 */         counters[n] = temp;
/*     */       } 
/*     */     } 
/*     */     
/* 212 */     int numModules = outsideChar ? 16 : 15;
/* 213 */     float elementWidth = MathUtils.sum(counters) / numModules;
/*     */     
/* 215 */     int[] oddCounts = getOddCounts();
/* 216 */     int[] evenCounts = getEvenCounts();
/* 217 */     float[] oddRoundingErrors = getOddRoundingErrors();
/* 218 */     float[] evenRoundingErrors = getEvenRoundingErrors();
/*     */     
/* 220 */     for (int i = 0; i < counters.length; i++) {
/*     */       float value;
/*     */       int count;
/* 223 */       if ((count = (int)((value = counters[i] / elementWidth) + 0.5F)) <= 0) {
/* 224 */         count = 1;
/* 225 */       } else if (count > 8) {
/* 226 */         count = 8;
/*     */       } 
/* 228 */       int offset = i / 2;
/* 229 */       if ((i & 0x1) == 0) {
/* 230 */         oddCounts[offset] = count;
/* 231 */         oddRoundingErrors[offset] = value - count;
/*     */       } else {
/* 233 */         evenCounts[offset] = count;
/* 234 */         evenRoundingErrors[offset] = value - count;
/*     */       } 
/*     */     } 
/*     */     
/* 238 */     adjustOddEvenCounts(outsideChar, numModules);
/*     */     
/* 240 */     int oddSum = 0;
/* 241 */     int oddChecksumPortion = 0;
/* 242 */     for (int j = oddCounts.length - 1; j >= 0; j--) {
/*     */       
/* 244 */       oddChecksumPortion = oddChecksumPortion * 9 + oddCounts[j];
/* 245 */       oddSum += oddCounts[j];
/*     */     } 
/* 247 */     int evenChecksumPortion = 0;
/* 248 */     int evenSum = 0;
/* 249 */     for (int k = evenCounts.length - 1; k >= 0; k--) {
/*     */       
/* 251 */       evenChecksumPortion = evenChecksumPortion * 9 + evenCounts[k];
/* 252 */       evenSum += evenCounts[k];
/*     */     } 
/* 254 */     int checksumPortion = oddChecksumPortion + 3 * evenChecksumPortion;
/*     */     
/* 256 */     if (outsideChar) {
/* 257 */       if ((oddSum & 0x1) != 0 || oddSum > 12 || oddSum < 4) {
/* 258 */         throw NotFoundException.getNotFoundInstance();
/*     */       }
/* 260 */       int m = (12 - oddSum) / 2;
/* 261 */       int n = OUTSIDE_ODD_WIDEST[m];
/* 262 */       int i1 = 9 - n;
/* 263 */       int i2 = RSSUtils.getRSSvalue(oddCounts, n, false);
/* 264 */       int i3 = RSSUtils.getRSSvalue(evenCounts, i1, true);
/* 265 */       int tEven = OUTSIDE_EVEN_TOTAL_SUBSET[m];
/* 266 */       int i4 = OUTSIDE_GSUM[m];
/* 267 */       return new DataCharacter(i2 * tEven + i3 + i4, checksumPortion);
/*     */     } 
/* 269 */     if ((evenSum & 0x1) != 0 || evenSum > 10 || evenSum < 4) {
/* 270 */       throw NotFoundException.getNotFoundInstance();
/*     */     }
/* 272 */     int group = (10 - evenSum) / 2;
/* 273 */     int oddWidest = INSIDE_ODD_WIDEST[group];
/* 274 */     int evenWidest = 9 - oddWidest;
/* 275 */     int vOdd = RSSUtils.getRSSvalue(oddCounts, oddWidest, true);
/* 276 */     int vEven = RSSUtils.getRSSvalue(evenCounts, evenWidest, false);
/* 277 */     int tOdd = INSIDE_ODD_TOTAL_SUBSET[group];
/* 278 */     int gSum = INSIDE_GSUM[group];
/* 279 */     return new DataCharacter(vEven * tOdd + vOdd + gSum, checksumPortion);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private int[] findFinderPattern(BitArray row, int rowOffset, boolean rightFinderPattern) throws NotFoundException {
/*     */     int[] counters;
/* 288 */     (counters = getDecodeFinderCounters())[0] = 0;
/* 289 */     counters[1] = 0;
/* 290 */     counters[2] = 0;
/* 291 */     counters[3] = 0;
/*     */     
/* 293 */     int width = row.getSize();
/* 294 */     boolean isWhite = false;
/* 295 */     while (rowOffset < width) {
/* 296 */       isWhite = !row.get(rowOffset);
/* 297 */       if (rightFinderPattern != isWhite)
/*     */       {
/*     */ 
/*     */         
/* 301 */         rowOffset++;
/*     */       }
/*     */     } 
/* 304 */     int counterPosition = 0;
/* 305 */     int patternStart = rowOffset;
/* 306 */     for (int x = rowOffset; x < width; x++) {
/* 307 */       if (row.get(x) ^ isWhite) {
/* 308 */         counters[counterPosition] = counters[counterPosition] + 1;
/*     */       } else {
/* 310 */         if (counterPosition == 3) {
/* 311 */           if (isFinderPattern(counters)) {
/* 312 */             return new int[] { patternStart, x };
/*     */           }
/* 314 */           patternStart += counters[0] + counters[1];
/* 315 */           counters[0] = counters[2];
/* 316 */           counters[1] = counters[3];
/* 317 */           counters[2] = 0;
/* 318 */           counters[3] = 0;
/* 319 */           counterPosition--;
/*     */         } else {
/* 321 */           counterPosition++;
/*     */         } 
/* 323 */         counters[counterPosition] = 1;
/* 324 */         isWhite = !isWhite;
/*     */       } 
/*     */     } 
/* 327 */     throw NotFoundException.getNotFoundInstance();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private FinderPattern parseFoundFinderPattern(BitArray row, int rowNumber, boolean right, int[] startEnd) throws NotFoundException {
/* 334 */     boolean firstIsBlack = row.get(startEnd[0]);
/* 335 */     int firstElementStart = startEnd[0] - 1;
/*     */     
/* 337 */     while (firstElementStart >= 0 && firstIsBlack ^ row.get(firstElementStart)) {
/* 338 */       firstElementStart--;
/*     */     }
/* 340 */     firstElementStart++;
/* 341 */     int firstCounter = startEnd[0] - firstElementStart;
/*     */     
/*     */     int[] counters;
/* 344 */     System.arraycopy(counters = getDecodeFinderCounters(), 0, counters, 1, counters.length - 1);
/* 345 */     counters[0] = firstCounter;
/* 346 */     int value = parseFinderValue(counters, FINDER_PATTERNS);
/* 347 */     int start = firstElementStart;
/* 348 */     int end = startEnd[1];
/* 349 */     if (right) {
/*     */       
/* 351 */       start = row.getSize() - 1 - start;
/* 352 */       end = row.getSize() - 1 - end;
/*     */     } 
/* 354 */     return new FinderPattern(value, new int[] { firstElementStart, startEnd[1] }, start, end, rowNumber);
/*     */   }
/*     */ 
/*     */   
/*     */   private void adjustOddEvenCounts(boolean outsideChar, int numModules) throws NotFoundException {
/* 359 */     int oddSum = MathUtils.sum(getOddCounts());
/* 360 */     int evenSum = MathUtils.sum(getEvenCounts());
/*     */     
/* 362 */     boolean incrementOdd = false;
/* 363 */     boolean decrementOdd = false;
/* 364 */     boolean incrementEven = false;
/* 365 */     boolean decrementEven = false;
/*     */     
/* 367 */     if (outsideChar) {
/* 368 */       if (oddSum > 12) {
/* 369 */         decrementOdd = true;
/* 370 */       } else if (oddSum < 4) {
/* 371 */         incrementOdd = true;
/*     */       } 
/* 373 */       if (evenSum > 12) {
/* 374 */         decrementEven = true;
/* 375 */       } else if (evenSum < 4) {
/* 376 */         incrementEven = true;
/*     */       } 
/*     */     } else {
/* 379 */       if (oddSum > 11) {
/* 380 */         decrementOdd = true;
/* 381 */       } else if (oddSum < 5) {
/* 382 */         incrementOdd = true;
/*     */       } 
/* 384 */       if (evenSum > 10) {
/* 385 */         decrementEven = true;
/* 386 */       } else if (evenSum < 4) {
/* 387 */         incrementEven = true;
/*     */       } 
/*     */     } 
/*     */     
/* 391 */     int mismatch = oddSum + evenSum - numModules;
/* 392 */     boolean oddParityBad = ((oddSum & 0x1) == (outsideChar ? 1 : 0));
/* 393 */     boolean evenParityBad = ((evenSum & 0x1) == 1);
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
/* 406 */     if (mismatch == 1) {
/* 407 */       if (oddParityBad) {
/* 408 */         if (evenParityBad) {
/* 409 */           throw NotFoundException.getNotFoundInstance();
/*     */         }
/* 411 */         decrementOdd = true;
/*     */       } else {
/* 413 */         if (!evenParityBad) {
/* 414 */           throw NotFoundException.getNotFoundInstance();
/*     */         }
/* 416 */         decrementEven = true;
/*     */       } 
/* 418 */     } else if (mismatch == -1) {
/* 419 */       if (oddParityBad) {
/* 420 */         if (evenParityBad) {
/* 421 */           throw NotFoundException.getNotFoundInstance();
/*     */         }
/* 423 */         incrementOdd = true;
/*     */       } else {
/* 425 */         if (!evenParityBad) {
/* 426 */           throw NotFoundException.getNotFoundInstance();
/*     */         }
/* 428 */         incrementEven = true;
/*     */       } 
/* 430 */     } else if (mismatch == 0) {
/* 431 */       if (oddParityBad) {
/* 432 */         if (!evenParityBad) {
/* 433 */           throw NotFoundException.getNotFoundInstance();
/*     */         }
/*     */         
/* 436 */         if (oddSum < evenSum) {
/* 437 */           incrementOdd = true;
/* 438 */           decrementEven = true;
/*     */         } else {
/* 440 */           decrementOdd = true;
/* 441 */           incrementEven = true;
/*     */         }
/*     */       
/* 444 */       } else if (evenParityBad) {
/* 445 */         throw NotFoundException.getNotFoundInstance();
/*     */       }
/*     */     
/*     */     } else {
/*     */       
/* 450 */       throw NotFoundException.getNotFoundInstance();
/*     */     } 
/*     */     
/* 453 */     if (incrementOdd) {
/* 454 */       if (decrementOdd) {
/* 455 */         throw NotFoundException.getNotFoundInstance();
/*     */       }
/* 457 */       increment(getOddCounts(), getOddRoundingErrors());
/*     */     } 
/* 459 */     if (decrementOdd) {
/* 460 */       decrement(getOddCounts(), getOddRoundingErrors());
/*     */     }
/* 462 */     if (incrementEven) {
/* 463 */       if (decrementEven) {
/* 464 */         throw NotFoundException.getNotFoundInstance();
/*     */       }
/* 466 */       increment(getEvenCounts(), getOddRoundingErrors());
/*     */     } 
/* 468 */     if (decrementEven)
/* 469 */       decrement(getEvenCounts(), getEvenRoundingErrors()); 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\zxing\oned\rss\RSS14Reader.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */