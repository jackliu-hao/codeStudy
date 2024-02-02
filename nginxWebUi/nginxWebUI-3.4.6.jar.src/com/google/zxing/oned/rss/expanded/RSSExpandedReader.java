/*     */ package com.google.zxing.oned.rss.expanded;
/*     */ 
/*     */ import com.google.zxing.BarcodeFormat;
/*     */ import com.google.zxing.DecodeHintType;
/*     */ import com.google.zxing.FormatException;
/*     */ import com.google.zxing.NotFoundException;
/*     */ import com.google.zxing.Result;
/*     */ import com.google.zxing.ResultPoint;
/*     */ import com.google.zxing.common.BitArray;
/*     */ import com.google.zxing.common.detector.MathUtils;
/*     */ import com.google.zxing.oned.rss.AbstractRSSReader;
/*     */ import com.google.zxing.oned.rss.DataCharacter;
/*     */ import com.google.zxing.oned.rss.FinderPattern;
/*     */ import com.google.zxing.oned.rss.RSSUtils;
/*     */ import com.google.zxing.oned.rss.expanded.decoders.AbstractExpandedDecoder;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class RSSExpandedReader
/*     */   extends AbstractRSSReader
/*     */ {
/*  55 */   private static final int[] SYMBOL_WIDEST = new int[] { 7, 5, 4, 3, 1 };
/*  56 */   private static final int[] EVEN_TOTAL_SUBSET = new int[] { 4, 20, 52, 104, 204 };
/*  57 */   private static final int[] GSUM = new int[] { 0, 348, 1388, 2948, 3988 };
/*     */   
/*  59 */   private static final int[][] FINDER_PATTERNS = new int[][] { { 1, 8, 4, 1 }, { 3, 6, 4, 1 }, { 3, 4, 6, 1 }, { 3, 2, 8, 1 }, { 2, 6, 5, 1 }, { 2, 2, 9, 1 } };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  68 */   private static final int[][] WEIGHTS = new int[][] { { 1, 3, 9, 27, 81, 32, 96, 77 }, { 20, 60, 180, 118, 143, 7, 21, 63 }, { 189, 145, 13, 39, 117, 140, 209, 205 }, { 193, 157, 49, 147, 19, 57, 171, 91 }, { 62, 186, 136, 197, 169, 85, 44, 132 }, { 185, 133, 188, 142, 4, 12, 36, 108 }, { 113, 128, 173, 97, 80, 29, 87, 50 }, { 150, 28, 84, 41, 123, 158, 52, 156 }, { 46, 138, 203, 187, 139, 206, 196, 166 }, { 76, 17, 51, 153, 37, 111, 122, 155 }, { 43, 129, 176, 106, 107, 110, 119, 146 }, { 16, 48, 144, 10, 30, 90, 59, 177 }, { 109, 116, 137, 200, 178, 112, 125, 164 }, { 70, 210, 208, 202, 184, 130, 179, 115 }, { 134, 191, 151, 31, 93, 68, 204, 190 }, { 148, 22, 66, 198, 172, 94, 71, 2 }, { 6, 18, 54, 162, 64, 192, 154, 40 }, { 120, 149, 25, 75, 14, 42, 126, 167 }, { 79, 26, 78, 23, 69, 207, 199, 175 }, { 103, 98, 83, 38, 114, 131, 182, 124 }, { 161, 61, 183, 127, 170, 88, 53, 159 }, { 55, 165, 73, 8, 24, 72, 5, 15 }, { 45, 135, 194, 160, 58, 174, 100, 89 } };
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static final int FINDER_PAT_A = 0;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static final int FINDER_PAT_B = 1;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static final int FINDER_PAT_C = 2;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static final int FINDER_PAT_D = 3;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static final int FINDER_PAT_E = 4;
/*     */ 
/*     */ 
/*     */   
/*     */   private static final int FINDER_PAT_F = 5;
/*     */ 
/*     */ 
/*     */   
/* 101 */   private static final int[][] FINDER_PATTERN_SEQUENCES = new int[][] { { 0, 0 }, { 0, 1, 1 }, { 0, 2, 1, 3 }, { 0, 4, 1, 3, 2 }, { 0, 4, 1, 3, 3, 5 }, { 0, 4, 1, 3, 4, 5, 5 }, { 0, 0, 1, 1, 2, 2, 3, 3 }, { 0, 0, 1, 1, 2, 2, 3, 4, 4 }, { 0, 0, 1, 1, 2, 2, 3, 4, 5, 5 }, { 0, 0, 1, 1, 2, 3, 3, 4, 4, 5, 5 } };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static final int MAX_PAIRS = 11;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 116 */   private final List<ExpandedPair> pairs = new ArrayList<>(11);
/* 117 */   private final List<ExpandedRow> rows = new ArrayList<>();
/* 118 */   private final int[] startEnd = new int[2];
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean startFromEven;
/*     */ 
/*     */ 
/*     */   
/*     */   public Result decodeRow(int rowNumber, BitArray row, Map<DecodeHintType, ?> hints) throws NotFoundException, FormatException {
/* 127 */     this.pairs.clear();
/* 128 */     this.startFromEven = false;
/*     */     try {
/* 130 */       return constructResult(decodeRow2pairs(rowNumber, row));
/* 131 */     } catch (NotFoundException notFoundException) {
/*     */ 
/*     */ 
/*     */       
/* 135 */       this.pairs.clear();
/* 136 */       this.startFromEven = true;
/* 137 */       return constructResult(decodeRow2pairs(rowNumber, row));
/*     */     } 
/*     */   }
/*     */   
/*     */   public void reset() {
/* 142 */     this.pairs.clear();
/* 143 */     this.rows.clear();
/*     */   }
/*     */ 
/*     */   
/*     */   List<ExpandedPair> decodeRow2pairs(int rowNumber, BitArray row) throws NotFoundException {
/*     */     try {
/*     */       while (true) {
/* 150 */         ExpandedPair nextPair = retrieveNextPair(row, this.pairs, rowNumber);
/* 151 */         this.pairs.add(nextPair);
/*     */       }
/*     */     
/* 154 */     } catch (NotFoundException nfe) {
/* 155 */       if (this.pairs.isEmpty()) {
/* 156 */         throw nfe;
/*     */       }
/*     */ 
/*     */ 
/*     */       
/* 161 */       if (checkChecksum()) {
/* 162 */         return this.pairs;
/*     */       }
/*     */       
/* 165 */       boolean tryStackedDecode = !this.rows.isEmpty();
/* 166 */       storeRow(rowNumber, false);
/* 167 */       if (tryStackedDecode) {
/*     */         List<ExpandedPair> ps;
/*     */ 
/*     */         
/* 171 */         if ((ps = checkRows(false)) != null) {
/* 172 */           return ps;
/*     */         }
/*     */         
/* 175 */         if ((ps = checkRows(true)) != null) {
/* 176 */           return ps;
/*     */         }
/*     */       } 
/*     */       
/* 180 */       throw NotFoundException.getNotFoundInstance();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private List<ExpandedPair> checkRows(boolean reverse) {
/* 187 */     if (this.rows.size() > 25) {
/* 188 */       this.rows.clear();
/* 189 */       return null;
/*     */     } 
/*     */     
/* 192 */     this.pairs.clear();
/* 193 */     if (reverse) {
/* 194 */       Collections.reverse(this.rows);
/*     */     }
/*     */     
/* 197 */     List<ExpandedPair> ps = null;
/*     */     try {
/* 199 */       ps = checkRows(new ArrayList<>(), 0);
/* 200 */     } catch (NotFoundException notFoundException) {}
/*     */ 
/*     */ 
/*     */     
/* 204 */     if (reverse) {
/* 205 */       Collections.reverse(this.rows);
/*     */     }
/*     */     
/* 208 */     return ps;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private List<ExpandedPair> checkRows(List<ExpandedRow> collectedRows, int currentRow) throws NotFoundException {
/* 214 */     for (int i = currentRow; i < this.rows.size(); i++) {
/* 215 */       ExpandedRow row = this.rows.get(i);
/* 216 */       this.pairs.clear();
/* 217 */       for (ExpandedRow collectedRow : collectedRows) {
/* 218 */         this.pairs.addAll(collectedRow.getPairs());
/*     */       }
/* 220 */       this.pairs.addAll(row.getPairs());
/*     */       
/* 222 */       if (isValidSequence(this.pairs)) {
/*     */ 
/*     */ 
/*     */         
/* 226 */         if (checkChecksum()) {
/* 227 */           return this.pairs;
/*     */         }
/*     */         
/*     */         List<ExpandedRow> rs;
/* 231 */         (rs = new ArrayList<>()).addAll(collectedRows);
/* 232 */         rs.add(row);
/*     */         
/*     */         try {
/* 235 */           return checkRows(rs, i + 1);
/* 236 */         } catch (NotFoundException notFoundException) {}
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/* 241 */     throw NotFoundException.getNotFoundInstance();
/*     */   }
/*     */   private static boolean isValidSequence(List<ExpandedPair> pairs) {
/*     */     int[][] arrayOfInt;
/*     */     int i;
/*     */     byte b;
/* 247 */     for (i = (arrayOfInt = FINDER_PATTERN_SEQUENCES).length, b = 0; b < i; ) { int[] sequence = arrayOfInt[b];
/* 248 */       if (pairs.size() <= sequence.length) {
/*     */ 
/*     */ 
/*     */         
/* 252 */         boolean stop = true;
/* 253 */         for (int j = 0; j < pairs.size(); j++) {
/* 254 */           if (((ExpandedPair)pairs.get(j)).getFinderPattern().getValue() != sequence[j]) {
/* 255 */             stop = false;
/*     */             
/*     */             break;
/*     */           } 
/*     */         } 
/* 260 */         if (stop)
/* 261 */           return true; 
/*     */       } 
/*     */       b++; }
/*     */     
/* 265 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   private void storeRow(int rowNumber, boolean wasReversed) {
/* 270 */     int insertPos = 0;
/* 271 */     boolean prevIsSame = false;
/* 272 */     boolean nextIsSame = false;
/* 273 */     while (insertPos < this.rows.size()) {
/*     */       ExpandedRow erow;
/* 275 */       if ((erow = this.rows.get(insertPos)).getRowNumber() > rowNumber) {
/* 276 */         nextIsSame = erow.isEquivalent(this.pairs);
/*     */         break;
/*     */       } 
/* 279 */       prevIsSame = erow.isEquivalent(this.pairs);
/* 280 */       insertPos++;
/*     */     } 
/* 282 */     if (nextIsSame || prevIsSame) {
/*     */       return;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 291 */     if (isPartialRow(this.pairs, this.rows)) {
/*     */       return;
/*     */     }
/*     */     
/* 295 */     this.rows.add(insertPos, new ExpandedRow(this.pairs, rowNumber, wasReversed));
/*     */     
/* 297 */     removePartialRows(this.pairs, this.rows);
/*     */   }
/*     */ 
/*     */   
/*     */   private static void removePartialRows(List<ExpandedPair> pairs, List<ExpandedRow> rows) {
/* 302 */     for (Iterator<ExpandedRow> iterator = rows.iterator(); iterator.hasNext();) {
/*     */       
/* 304 */       if ((r = iterator.next()).getPairs().size() != pairs.size()) {
/*     */ 
/*     */         
/* 307 */         boolean allFound = true;
/* 308 */         for (ExpandedPair p : r.getPairs()) {
/* 309 */           boolean found = false;
/* 310 */           for (ExpandedPair pp : pairs) {
/* 311 */             if (p.equals(pp)) {
/* 312 */               found = true;
/*     */               break;
/*     */             } 
/*     */           } 
/* 316 */           if (!found) {
/* 317 */             allFound = false;
/*     */             break;
/*     */           } 
/*     */         } 
/* 321 */         if (allFound)
/*     */         {
/* 323 */           iterator.remove();
/*     */         }
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   private static boolean isPartialRow(Iterable<ExpandedPair> pairs, Iterable<ExpandedRow> rows) {
/* 330 */     for (ExpandedRow r : rows) {
/* 331 */       boolean allFound = true;
/* 332 */       for (ExpandedPair p : pairs) {
/* 333 */         boolean found = false;
/* 334 */         for (ExpandedPair pp : r.getPairs()) {
/* 335 */           if (p.equals(pp)) {
/* 336 */             found = true;
/*     */             break;
/*     */           } 
/*     */         } 
/* 340 */         if (!found) {
/* 341 */           allFound = false;
/*     */           break;
/*     */         } 
/*     */       } 
/* 345 */       if (allFound)
/*     */       {
/* 347 */         return true;
/*     */       }
/*     */     } 
/* 350 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   List<ExpandedRow> getRows() {
/* 355 */     return this.rows;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static Result constructResult(List<ExpandedPair> pairs) throws NotFoundException, FormatException {
/* 363 */     String resultingString = AbstractExpandedDecoder.createDecoder(BitArrayBuilder.buildBitArray(pairs)).parseInformation();
/*     */     
/* 365 */     ResultPoint[] firstPoints = ((ExpandedPair)pairs.get(0)).getFinderPattern().getResultPoints();
/* 366 */     ResultPoint[] lastPoints = ((ExpandedPair)pairs.get(pairs.size() - 1)).getFinderPattern().getResultPoints();
/*     */     
/* 368 */     return new Result(resultingString, null, new ResultPoint[] { firstPoints[0], firstPoints[1], lastPoints[0], lastPoints[1] }, BarcodeFormat.RSS_EXPANDED);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean checkChecksum() {
/*     */     ExpandedPair firstPair;
/* 378 */     DataCharacter checkCharacter = (firstPair = this.pairs.get(0)).getLeftChar();
/*     */     
/*     */     DataCharacter firstCharacter;
/* 381 */     if ((firstCharacter = firstPair.getRightChar()) == null) {
/* 382 */       return false;
/*     */     }
/*     */     
/* 385 */     int checksum = firstCharacter.getChecksumPortion();
/* 386 */     int s = 2;
/*     */     
/* 388 */     for (int i = 1; i < this.pairs.size(); i++) {
/* 389 */       ExpandedPair currentPair = this.pairs.get(i);
/* 390 */       checksum += currentPair.getLeftChar().getChecksumPortion();
/* 391 */       s++;
/*     */       DataCharacter currentRightChar;
/* 393 */       if ((currentRightChar = currentPair.getRightChar()) != null) {
/* 394 */         checksum += currentRightChar.getChecksumPortion();
/* 395 */         s++;
/*     */       } 
/*     */     } 
/*     */     
/* 399 */     checksum %= 211;
/*     */ 
/*     */ 
/*     */     
/* 403 */     return (211 * (s - 4) + checksum == checkCharacter.getValue());
/*     */   }
/*     */   
/*     */   private static int getNextSecondBar(BitArray row, int initialPos) {
/*     */     int currentPos;
/* 408 */     if (row.get(initialPos)) {
/* 409 */       currentPos = row.getNextUnset(initialPos);
/* 410 */       currentPos = row.getNextSet(currentPos);
/*     */     } else {
/* 412 */       currentPos = row.getNextSet(initialPos);
/* 413 */       currentPos = row.getNextUnset(currentPos);
/*     */     } 
/* 415 */     return currentPos;
/*     */   }
/*     */   
/*     */   ExpandedPair retrieveNextPair(BitArray row, List<ExpandedPair> previousPairs, int rowNumber) throws NotFoundException {
/*     */     FinderPattern pattern;
/*     */     DataCharacter rightChar;
/* 421 */     boolean isOddPattern = (previousPairs.size() % 2 == 0);
/* 422 */     if (this.startFromEven) {
/* 423 */       isOddPattern = !isOddPattern;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 428 */     boolean keepFinding = true;
/* 429 */     int forcedOffset = -1;
/*     */     do {
/* 431 */       findNextPair(row, previousPairs, forcedOffset);
/*     */       
/* 433 */       if ((pattern = parseFoundFinderPattern(row, rowNumber, isOddPattern)) == null) {
/* 434 */         forcedOffset = getNextSecondBar(row, this.startEnd[0]);
/*     */       } else {
/* 436 */         keepFinding = false;
/*     */       } 
/* 438 */     } while (keepFinding);
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 443 */     DataCharacter leftChar = decodeDataCharacter(row, pattern, isOddPattern, true);
/*     */     
/* 445 */     if (!previousPairs.isEmpty() && ((ExpandedPair)previousPairs.get(previousPairs.size() - 1)).mustBeLast()) {
/* 446 */       throw NotFoundException.getNotFoundInstance();
/*     */     }
/*     */ 
/*     */     
/*     */     try {
/* 451 */       rightChar = decodeDataCharacter(row, pattern, isOddPattern, false);
/* 452 */     } catch (NotFoundException notFoundException) {
/* 453 */       rightChar = null;
/*     */     } 
/* 455 */     return new ExpandedPair(leftChar, rightChar, pattern, true);
/*     */   }
/*     */ 
/*     */   
/*     */   private void findNextPair(BitArray row, List<ExpandedPair> previousPairs, int forcedOffset) throws NotFoundException {
/*     */     int rowOffset, counters[];
/* 461 */     (counters = getDecodeFinderCounters())[0] = 0;
/* 462 */     counters[1] = 0;
/* 463 */     counters[2] = 0;
/* 464 */     counters[3] = 0;
/*     */     
/* 466 */     int width = row.getSize();
/*     */ 
/*     */     
/* 469 */     if (forcedOffset >= 0) {
/* 470 */       rowOffset = forcedOffset;
/* 471 */     } else if (previousPairs.isEmpty()) {
/* 472 */       rowOffset = 0;
/*     */     } else {
/*     */       
/* 475 */       rowOffset = ((ExpandedPair)previousPairs.get(previousPairs.size() - 1)).getFinderPattern().getStartEnd()[1];
/*     */     } 
/* 477 */     boolean bool = (previousPairs.size() % 2 != 0) ? true : false;
/* 478 */     if (this.startFromEven) {
/* 479 */       bool = !bool ? true : false;
/*     */     }
/*     */     
/* 482 */     boolean isWhite = false;
/* 483 */     while (rowOffset < width) {
/*     */       
/* 485 */       if (isWhite = !row.get(rowOffset))
/*     */       {
/*     */         
/* 488 */         rowOffset++;
/*     */       }
/*     */     } 
/* 491 */     int counterPosition = 0;
/* 492 */     int patternStart = rowOffset;
/* 493 */     for (int x = rowOffset; x < width; x++) {
/* 494 */       if (row.get(x) ^ isWhite) {
/* 495 */         counters[counterPosition] = counters[counterPosition] + 1;
/*     */       } else {
/* 497 */         if (counterPosition == 3) {
/* 498 */           if (bool) {
/* 499 */             reverseCounters(counters);
/*     */           }
/*     */           
/* 502 */           if (isFinderPattern(counters)) {
/* 503 */             this.startEnd[0] = patternStart;
/* 504 */             this.startEnd[1] = x;
/*     */             
/*     */             return;
/*     */           } 
/* 508 */           if (bool) {
/* 509 */             reverseCounters(counters);
/*     */           }
/*     */           
/* 512 */           patternStart += counters[0] + counters[1];
/* 513 */           counters[0] = counters[2];
/* 514 */           counters[1] = counters[3];
/* 515 */           counters[2] = 0;
/* 516 */           counters[3] = 0;
/* 517 */           counterPosition--;
/*     */         } else {
/* 519 */           counterPosition++;
/*     */         } 
/* 521 */         counters[counterPosition] = 1;
/* 522 */         isWhite = !isWhite;
/*     */       } 
/*     */     } 
/* 525 */     throw NotFoundException.getNotFoundInstance();
/*     */   }
/*     */   
/*     */   private static void reverseCounters(int[] counters) {
/* 529 */     int length = counters.length;
/* 530 */     for (int i = 0; i < length / 2; i++) {
/* 531 */       int tmp = counters[i];
/* 532 */       counters[i] = counters[length - i - 1];
/* 533 */       counters[length - i - 1] = tmp;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private FinderPattern parseFoundFinderPattern(BitArray row, int rowNumber, boolean oddPattern) {
/*     */     int firstCounter, start, end, value;
/* 543 */     if (oddPattern) {
/*     */ 
/*     */       
/* 546 */       int firstElementStart = this.startEnd[0] - 1;
/*     */       
/* 548 */       while (firstElementStart >= 0 && !row.get(firstElementStart)) {
/* 549 */         firstElementStart--;
/*     */       }
/*     */       
/* 552 */       firstElementStart++;
/* 553 */       firstCounter = this.startEnd[0] - firstElementStart;
/* 554 */       start = firstElementStart;
/* 555 */       end = this.startEnd[1];
/*     */     
/*     */     }
/*     */     else {
/*     */       
/* 560 */       start = this.startEnd[0];
/*     */ 
/*     */       
/* 563 */       firstCounter = (end = row.getNextUnset(this.startEnd[1] + 1)) - this.startEnd[1];
/*     */     } 
/*     */     
/*     */     int[] counters;
/*     */     
/* 568 */     System.arraycopy(counters = getDecodeFinderCounters(), 0, counters, 1, counters.length - 1);
/*     */     
/* 570 */     counters[0] = firstCounter;
/*     */     
/*     */     try {
/* 573 */       value = parseFinderValue(counters, FINDER_PATTERNS);
/* 574 */     } catch (NotFoundException notFoundException) {
/* 575 */       return null;
/*     */     } 
/* 577 */     return new FinderPattern(value, new int[] { start, end }, start, end, rowNumber);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   DataCharacter decodeDataCharacter(BitArray row, FinderPattern pattern, boolean isOddPattern, boolean leftChar) throws NotFoundException {
/*     */     int[] counters;
/* 585 */     (counters = getDataCharacterCounters())[0] = 0;
/* 586 */     counters[1] = 0;
/* 587 */     counters[2] = 0;
/* 588 */     counters[3] = 0;
/* 589 */     counters[4] = 0;
/* 590 */     counters[5] = 0;
/* 591 */     counters[6] = 0;
/* 592 */     counters[7] = 0;
/*     */     
/* 594 */     if (leftChar) {
/* 595 */       recordPatternInReverse(row, pattern.getStartEnd()[0], counters);
/*     */     } else {
/* 597 */       recordPattern(row, pattern.getStartEnd()[1], counters);
/*     */       
/* 599 */       for (int m = 0, n = counters.length - 1; m < n; m++, n--) {
/* 600 */         int temp = counters[m];
/* 601 */         counters[m] = counters[n];
/* 602 */         counters[n] = temp;
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/* 607 */     float elementWidth = MathUtils.sum(counters) / 17.0F;
/*     */ 
/*     */     
/* 610 */     float expectedElementWidth = (pattern.getStartEnd()[1] - pattern.getStartEnd()[0]) / 15.0F;
/* 611 */     if (Math.abs(elementWidth - expectedElementWidth) / expectedElementWidth > 0.3F) {
/* 612 */       throw NotFoundException.getNotFoundInstance();
/*     */     }
/*     */     
/* 615 */     int[] oddCounts = getOddCounts();
/* 616 */     int[] evenCounts = getEvenCounts();
/* 617 */     float[] oddRoundingErrors = getOddRoundingErrors();
/* 618 */     float[] evenRoundingErrors = getEvenRoundingErrors();
/*     */     
/* 620 */     for (int i = 0; i < counters.length; i++) {
/*     */       float f;
/*     */       int count;
/* 623 */       if ((count = (int)((f = 1.0F * counters[i] / elementWidth) + 0.5F)) <= 0) {
/* 624 */         if (f < 0.3F) {
/* 625 */           throw NotFoundException.getNotFoundInstance();
/*     */         }
/* 627 */         count = 1;
/* 628 */       } else if (count > 8) {
/* 629 */         if (f > 8.7F) {
/* 630 */           throw NotFoundException.getNotFoundInstance();
/*     */         }
/* 632 */         count = 8;
/*     */       } 
/* 634 */       int offset = i / 2;
/* 635 */       if ((i & 0x1) == 0) {
/* 636 */         oddCounts[offset] = count;
/* 637 */         oddRoundingErrors[offset] = f - count;
/*     */       } else {
/* 639 */         evenCounts[offset] = count;
/* 640 */         evenRoundingErrors[offset] = f - count;
/*     */       } 
/*     */     } 
/*     */     
/* 644 */     adjustOddEvenCounts(17);
/*     */     
/* 646 */     int weightRowNumber = 4 * pattern.getValue() + (isOddPattern ? 0 : 2) + (leftChar ? 0 : 1) - 1;
/*     */     
/* 648 */     int oddSum = 0;
/* 649 */     int oddChecksumPortion = 0;
/* 650 */     for (int j = oddCounts.length - 1; j >= 0; j--) {
/* 651 */       if (isNotA1left(pattern, isOddPattern, leftChar)) {
/* 652 */         int weight = WEIGHTS[weightRowNumber][2 * j];
/* 653 */         oddChecksumPortion += oddCounts[j] * weight;
/*     */       } 
/* 655 */       oddSum += oddCounts[j];
/*     */     } 
/* 657 */     int evenChecksumPortion = 0;
/*     */     
/* 659 */     for (int k = evenCounts.length - 1; k >= 0; k--) {
/* 660 */       if (isNotA1left(pattern, isOddPattern, leftChar)) {
/* 661 */         int weight = WEIGHTS[weightRowNumber][2 * k + 1];
/* 662 */         evenChecksumPortion += evenCounts[k] * weight;
/*     */       } 
/*     */     } 
/*     */     
/* 666 */     int checksumPortion = oddChecksumPortion + evenChecksumPortion;
/*     */     
/* 668 */     if ((oddSum & 0x1) != 0 || oddSum > 13 || oddSum < 4) {
/* 669 */       throw NotFoundException.getNotFoundInstance();
/*     */     }
/*     */     
/* 672 */     int group = (13 - oddSum) / 2;
/* 673 */     int oddWidest = SYMBOL_WIDEST[group];
/* 674 */     int evenWidest = 9 - oddWidest;
/* 675 */     int vOdd = RSSUtils.getRSSvalue(oddCounts, oddWidest, true);
/* 676 */     int vEven = RSSUtils.getRSSvalue(evenCounts, evenWidest, false);
/* 677 */     int tEven = EVEN_TOTAL_SUBSET[group];
/* 678 */     int gSum = GSUM[group];
/* 679 */     int value = vOdd * tEven + vEven + gSum;
/*     */     
/* 681 */     return new DataCharacter(value, checksumPortion);
/*     */   }
/*     */ 
/*     */   
/*     */   private static boolean isNotA1left(FinderPattern pattern, boolean isOddPattern, boolean leftChar) {
/* 686 */     return (pattern.getValue() != 0 || !isOddPattern || !leftChar);
/*     */   }
/*     */ 
/*     */   
/*     */   private void adjustOddEvenCounts(int numModules) throws NotFoundException {
/* 691 */     int oddSum = MathUtils.sum(getOddCounts());
/* 692 */     int evenSum = MathUtils.sum(getEvenCounts());
/*     */     
/* 694 */     boolean incrementOdd = false;
/* 695 */     boolean decrementOdd = false;
/*     */     
/* 697 */     if (oddSum > 13) {
/* 698 */       decrementOdd = true;
/* 699 */     } else if (oddSum < 4) {
/* 700 */       incrementOdd = true;
/*     */     } 
/* 702 */     boolean incrementEven = false;
/* 703 */     boolean decrementEven = false;
/* 704 */     if (evenSum > 13) {
/* 705 */       decrementEven = true;
/* 706 */     } else if (evenSum < 4) {
/* 707 */       incrementEven = true;
/*     */     } 
/*     */     
/* 710 */     int mismatch = oddSum + evenSum - numModules;
/* 711 */     boolean oddParityBad = ((oddSum & 0x1) == 1);
/* 712 */     boolean evenParityBad = ((evenSum & 0x1) == 0);
/* 713 */     if (mismatch == 1) {
/* 714 */       if (oddParityBad) {
/* 715 */         if (evenParityBad) {
/* 716 */           throw NotFoundException.getNotFoundInstance();
/*     */         }
/* 718 */         decrementOdd = true;
/*     */       } else {
/* 720 */         if (!evenParityBad) {
/* 721 */           throw NotFoundException.getNotFoundInstance();
/*     */         }
/* 723 */         decrementEven = true;
/*     */       } 
/* 725 */     } else if (mismatch == -1) {
/* 726 */       if (oddParityBad) {
/* 727 */         if (evenParityBad) {
/* 728 */           throw NotFoundException.getNotFoundInstance();
/*     */         }
/* 730 */         incrementOdd = true;
/*     */       } else {
/* 732 */         if (!evenParityBad) {
/* 733 */           throw NotFoundException.getNotFoundInstance();
/*     */         }
/* 735 */         incrementEven = true;
/*     */       } 
/* 737 */     } else if (mismatch == 0) {
/* 738 */       if (oddParityBad) {
/* 739 */         if (!evenParityBad) {
/* 740 */           throw NotFoundException.getNotFoundInstance();
/*     */         }
/*     */         
/* 743 */         if (oddSum < evenSum) {
/* 744 */           incrementOdd = true;
/* 745 */           decrementEven = true;
/*     */         } else {
/* 747 */           decrementOdd = true;
/* 748 */           incrementEven = true;
/*     */         }
/*     */       
/* 751 */       } else if (evenParityBad) {
/* 752 */         throw NotFoundException.getNotFoundInstance();
/*     */       }
/*     */     
/*     */     } else {
/*     */       
/* 757 */       throw NotFoundException.getNotFoundInstance();
/*     */     } 
/*     */     
/* 760 */     if (incrementOdd) {
/* 761 */       if (decrementOdd) {
/* 762 */         throw NotFoundException.getNotFoundInstance();
/*     */       }
/* 764 */       increment(getOddCounts(), getOddRoundingErrors());
/*     */     } 
/* 766 */     if (decrementOdd) {
/* 767 */       decrement(getOddCounts(), getOddRoundingErrors());
/*     */     }
/* 769 */     if (incrementEven) {
/* 770 */       if (decrementEven) {
/* 771 */         throw NotFoundException.getNotFoundInstance();
/*     */       }
/* 773 */       increment(getEvenCounts(), getOddRoundingErrors());
/*     */     } 
/* 775 */     if (decrementEven)
/* 776 */       decrement(getEvenCounts(), getEvenRoundingErrors()); 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\zxing\oned\rss\expanded\RSSExpandedReader.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */