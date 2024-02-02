/*     */ package com.google.zxing.qrcode.detector;
/*     */ 
/*     */ import com.google.zxing.DecodeHintType;
/*     */ import com.google.zxing.NotFoundException;
/*     */ import com.google.zxing.ResultPoint;
/*     */ import com.google.zxing.ResultPointCallback;
/*     */ import com.google.zxing.common.BitMatrix;
/*     */ import java.io.Serializable;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.Comparator;
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
/*     */ public class FinderPatternFinder
/*     */ {
/*     */   private static final int CENTER_QUORUM = 2;
/*     */   protected static final int MIN_SKIP = 3;
/*     */   protected static final int MAX_MODULES = 57;
/*     */   private final BitMatrix image;
/*     */   private final List<FinderPattern> possibleCenters;
/*     */   private boolean hasSkipped;
/*     */   private final int[] crossCheckStateCount;
/*     */   private final ResultPointCallback resultPointCallback;
/*     */   
/*     */   public FinderPatternFinder(BitMatrix image) {
/*  58 */     this(image, null);
/*     */   }
/*     */   
/*     */   public FinderPatternFinder(BitMatrix image, ResultPointCallback resultPointCallback) {
/*  62 */     this.image = image;
/*  63 */     this.possibleCenters = new ArrayList<>();
/*  64 */     this.crossCheckStateCount = new int[5];
/*  65 */     this.resultPointCallback = resultPointCallback;
/*     */   }
/*     */   
/*     */   protected final BitMatrix getImage() {
/*  69 */     return this.image;
/*     */   }
/*     */   
/*     */   protected final List<FinderPattern> getPossibleCenters() {
/*  73 */     return this.possibleCenters;
/*     */   }
/*     */   
/*     */   final FinderPatternInfo find(Map<DecodeHintType, ?> hints) throws NotFoundException {
/*  77 */     boolean tryHarder = (hints != null && hints.containsKey(DecodeHintType.TRY_HARDER));
/*  78 */     boolean pureBarcode = (hints != null && hints.containsKey(DecodeHintType.PURE_BARCODE));
/*  79 */     int maxI = this.image.getHeight();
/*  80 */     int maxJ = this.image.getWidth();
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     int iSkip;
/*     */ 
/*     */ 
/*     */     
/*  89 */     if ((iSkip = 3 * maxI / 228) < 3 || tryHarder) {
/*  90 */       iSkip = 3;
/*     */     }
/*     */     
/*  93 */     boolean done = false;
/*  94 */     int[] stateCount = new int[5]; int i;
/*  95 */     for (i = iSkip - 1; i < maxI && !done; i += iSkip) {
/*     */       
/*  97 */       stateCount[0] = 0;
/*  98 */       stateCount[1] = 0;
/*  99 */       stateCount[2] = 0;
/* 100 */       stateCount[3] = 0;
/* 101 */       stateCount[4] = 0;
/* 102 */       int currentState = 0;
/* 103 */       int j = 0; while (true) { if (j < maxJ)
/* 104 */         { if (this.image.get(j, i)) {
/*     */             
/* 106 */             if ((currentState & 0x1) == 1) {
/* 107 */               currentState++;
/*     */             }
/* 109 */             stateCount[currentState] = stateCount[currentState] + 1;
/*     */           }
/* 111 */           else if ((currentState & 0x1) == 0) {
/* 112 */             if (currentState == 4) {
/* 113 */               if (foundPatternCross(stateCount)) {
/* 114 */                 if (handlePossibleCenter(stateCount, i, j, pureBarcode)) {
/*     */ 
/*     */ 
/*     */                   
/* 118 */                   iSkip = 2;
/* 119 */                   if (this.hasSkipped) {
/* 120 */                     done = haveMultiplyConfirmedCenters();
/*     */                   } else {
/*     */                     int rowSkip;
/* 123 */                     if ((rowSkip = findRowSkip()) > stateCount[2]) {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */                       
/* 132 */                       i += rowSkip - stateCount[2] - 2;
/* 133 */                       j = maxJ - 1;
/*     */                     } 
/*     */                   } 
/*     */                 } else {
/* 137 */                   stateCount[0] = stateCount[2];
/* 138 */                   stateCount[1] = stateCount[3];
/* 139 */                   stateCount[2] = stateCount[4];
/* 140 */                   stateCount[3] = 1;
/* 141 */                   stateCount[4] = 0;
/* 142 */                   currentState = 3;
/*     */                   
/*     */                   j++;
/*     */                 } 
/* 146 */                 currentState = 0;
/* 147 */                 stateCount[0] = 0;
/* 148 */                 stateCount[1] = 0;
/* 149 */                 stateCount[2] = 0;
/* 150 */                 stateCount[3] = 0;
/* 151 */                 stateCount[4] = 0;
/*     */               } else {
/* 153 */                 stateCount[0] = stateCount[2];
/* 154 */                 stateCount[1] = stateCount[3];
/* 155 */                 stateCount[2] = stateCount[4];
/* 156 */                 stateCount[3] = 1;
/* 157 */                 stateCount[4] = 0;
/* 158 */                 currentState = 3;
/*     */               } 
/*     */             } else {
/* 161 */               stateCount[++currentState] = stateCount[++currentState] + 1;
/*     */             } 
/*     */           } else {
/* 164 */             stateCount[currentState] = stateCount[currentState] + 1;
/*     */           }  }
/*     */         else { break; }
/*     */          j++; }
/* 168 */        if (foundPatternCross(stateCount) && 
/* 169 */         handlePossibleCenter(stateCount, i, maxJ, pureBarcode)) {
/*     */         
/* 171 */         iSkip = stateCount[0];
/* 172 */         if (this.hasSkipped)
/*     */         {
/* 174 */           done = haveMultiplyConfirmedCenters();
/*     */         }
/*     */       } 
/*     */     } 
/*     */     
/*     */     FinderPattern[] patternInfo;
/*     */     
/* 181 */     ResultPoint.orderBestPatterns((ResultPoint[])(patternInfo = selectBestPatterns()));
/*     */     
/* 183 */     return new FinderPatternInfo(patternInfo);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static float centerFromEnd(int[] stateCount, int end) {
/* 191 */     return (end - stateCount[4] - stateCount[3]) - stateCount[2] / 2.0F;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected static boolean foundPatternCross(int[] stateCount) {
/* 200 */     int totalModuleSize = 0;
/* 201 */     for (int i = 0; i < 5; i++) {
/*     */       int count;
/* 203 */       if ((count = stateCount[i]) == 0) {
/* 204 */         return false;
/*     */       }
/* 206 */       totalModuleSize += count;
/*     */     } 
/* 208 */     if (totalModuleSize < 7) {
/* 209 */       return false;
/*     */     }
/*     */     
/* 212 */     float moduleSize, maxVariance = (moduleSize = totalModuleSize / 7.0F) / 2.0F;
/*     */ 
/*     */     
/* 215 */     if (Math.abs(moduleSize - stateCount[0]) < maxVariance && 
/* 216 */       Math.abs(moduleSize - stateCount[1]) < maxVariance && 
/* 217 */       Math.abs(3.0F * moduleSize - stateCount[2]) < 3.0F * maxVariance && 
/* 218 */       Math.abs(moduleSize - stateCount[3]) < maxVariance && 
/* 219 */       Math.abs(moduleSize - stateCount[4]) < maxVariance) return true; 
/*     */     return false;
/*     */   }
/*     */   private int[] getCrossCheckStateCount() {
/* 223 */     this.crossCheckStateCount[0] = 0;
/* 224 */     this.crossCheckStateCount[1] = 0;
/* 225 */     this.crossCheckStateCount[2] = 0;
/* 226 */     this.crossCheckStateCount[3] = 0;
/* 227 */     this.crossCheckStateCount[4] = 0;
/* 228 */     return this.crossCheckStateCount;
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
/*     */   private boolean crossCheckDiagonal(int startI, int centerJ, int maxCount, int originalStateCountTotal) {
/* 244 */     int[] stateCount = getCrossCheckStateCount();
/*     */ 
/*     */     
/* 247 */     int i = 0;
/* 248 */     while (startI >= i && centerJ >= i && this.image.get(centerJ - i, startI - i)) {
/* 249 */       stateCount[2] = stateCount[2] + 1;
/* 250 */       i++;
/*     */     } 
/*     */     
/* 253 */     if (startI < i || centerJ < i) {
/* 254 */       return false;
/*     */     }
/*     */ 
/*     */     
/* 258 */     while (startI >= i && centerJ >= i && !this.image.get(centerJ - i, startI - i) && stateCount[1] <= maxCount) {
/*     */       
/* 260 */       stateCount[1] = stateCount[1] + 1;
/* 261 */       i++;
/*     */     } 
/*     */ 
/*     */     
/* 265 */     if (startI < i || centerJ < i || stateCount[1] > maxCount) {
/* 266 */       return false;
/*     */     }
/*     */ 
/*     */     
/* 270 */     while (startI >= i && centerJ >= i && this.image.get(centerJ - i, startI - i) && stateCount[0] <= maxCount) {
/*     */       
/* 272 */       stateCount[0] = stateCount[0] + 1;
/* 273 */       i++;
/*     */     } 
/* 275 */     if (stateCount[0] > maxCount) {
/* 276 */       return false;
/*     */     }
/*     */     
/* 279 */     int maxI = this.image.getHeight();
/* 280 */     int maxJ = this.image.getWidth();
/*     */ 
/*     */     
/* 283 */     i = 1;
/* 284 */     while (startI + i < maxI && centerJ + i < maxJ && this.image.get(centerJ + i, startI + i)) {
/* 285 */       stateCount[2] = stateCount[2] + 1;
/* 286 */       i++;
/*     */     } 
/*     */ 
/*     */     
/* 290 */     if (startI + i >= maxI || centerJ + i >= maxJ) {
/* 291 */       return false;
/*     */     }
/*     */     
/* 294 */     while (startI + i < maxI && centerJ + i < maxJ && !this.image.get(centerJ + i, startI + i) && stateCount[3] < maxCount) {
/*     */       
/* 296 */       stateCount[3] = stateCount[3] + 1;
/* 297 */       i++;
/*     */     } 
/*     */     
/* 300 */     if (startI + i >= maxI || centerJ + i >= maxJ || stateCount[3] >= maxCount) {
/* 301 */       return false;
/*     */     }
/*     */     
/* 304 */     while (startI + i < maxI && centerJ + i < maxJ && this.image.get(centerJ + i, startI + i) && stateCount[4] < maxCount) {
/*     */       
/* 306 */       stateCount[4] = stateCount[4] + 1;
/* 307 */       i++;
/*     */     } 
/*     */     
/* 310 */     if (stateCount[4] >= maxCount) {
/* 311 */       return false;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 318 */     if (Math.abs(stateCount[0] + stateCount[1] + stateCount[2] + stateCount[3] + stateCount[4] - originalStateCountTotal) < 2 * originalStateCountTotal && 
/* 319 */       foundPatternCross(stateCount)) return true;
/*     */     
/*     */     return false;
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
/*     */   private float crossCheckVertical(int startI, int centerJ, int maxCount, int originalStateCountTotal) {
/*     */     BitMatrix image;
/* 337 */     int maxI = (image = this.image).getHeight();
/* 338 */     int[] stateCount = getCrossCheckStateCount();
/*     */ 
/*     */     
/* 341 */     int i = startI;
/* 342 */     while (i >= 0 && image.get(centerJ, i)) {
/* 343 */       stateCount[2] = stateCount[2] + 1;
/* 344 */       i--;
/*     */     } 
/* 346 */     if (i < 0) {
/* 347 */       return Float.NaN;
/*     */     }
/* 349 */     while (i >= 0 && !image.get(centerJ, i) && stateCount[1] <= maxCount) {
/* 350 */       stateCount[1] = stateCount[1] + 1;
/* 351 */       i--;
/*     */     } 
/*     */     
/* 354 */     if (i < 0 || stateCount[1] > maxCount) {
/* 355 */       return Float.NaN;
/*     */     }
/* 357 */     while (i >= 0 && image.get(centerJ, i) && stateCount[0] <= maxCount) {
/* 358 */       stateCount[0] = stateCount[0] + 1;
/* 359 */       i--;
/*     */     } 
/* 361 */     if (stateCount[0] > maxCount) {
/* 362 */       return Float.NaN;
/*     */     }
/*     */ 
/*     */     
/* 366 */     i = startI + 1;
/* 367 */     while (i < maxI && image.get(centerJ, i)) {
/* 368 */       stateCount[2] = stateCount[2] + 1;
/* 369 */       i++;
/*     */     } 
/* 371 */     if (i == maxI) {
/* 372 */       return Float.NaN;
/*     */     }
/* 374 */     while (i < maxI && !image.get(centerJ, i) && stateCount[3] < maxCount) {
/* 375 */       stateCount[3] = stateCount[3] + 1;
/* 376 */       i++;
/*     */     } 
/* 378 */     if (i == maxI || stateCount[3] >= maxCount) {
/* 379 */       return Float.NaN;
/*     */     }
/* 381 */     while (i < maxI && image.get(centerJ, i) && stateCount[4] < maxCount) {
/* 382 */       stateCount[4] = stateCount[4] + 1;
/* 383 */       i++;
/*     */     } 
/* 385 */     if (stateCount[4] >= maxCount) {
/* 386 */       return Float.NaN;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 391 */     int stateCountTotal = stateCount[0] + stateCount[1] + stateCount[2] + stateCount[3] + stateCount[4];
/*     */     
/* 393 */     if (5 * Math.abs(stateCountTotal - originalStateCountTotal) >= 2 * originalStateCountTotal) {
/* 394 */       return Float.NaN;
/*     */     }
/*     */     
/* 397 */     return foundPatternCross(stateCount) ? centerFromEnd(stateCount, i) : Float.NaN;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private float crossCheckHorizontal(int startJ, int centerI, int maxCount, int originalStateCountTotal) {
/*     */     BitMatrix image;
/* 409 */     int maxJ = (image = this.image).getWidth();
/* 410 */     int[] stateCount = getCrossCheckStateCount();
/*     */     
/* 412 */     int j = startJ;
/* 413 */     while (j >= 0 && image.get(j, centerI)) {
/* 414 */       stateCount[2] = stateCount[2] + 1;
/* 415 */       j--;
/*     */     } 
/* 417 */     if (j < 0) {
/* 418 */       return Float.NaN;
/*     */     }
/* 420 */     while (j >= 0 && !image.get(j, centerI) && stateCount[1] <= maxCount) {
/* 421 */       stateCount[1] = stateCount[1] + 1;
/* 422 */       j--;
/*     */     } 
/* 424 */     if (j < 0 || stateCount[1] > maxCount) {
/* 425 */       return Float.NaN;
/*     */     }
/* 427 */     while (j >= 0 && image.get(j, centerI) && stateCount[0] <= maxCount) {
/* 428 */       stateCount[0] = stateCount[0] + 1;
/* 429 */       j--;
/*     */     } 
/* 431 */     if (stateCount[0] > maxCount) {
/* 432 */       return Float.NaN;
/*     */     }
/*     */     
/* 435 */     j = startJ + 1;
/* 436 */     while (j < maxJ && image.get(j, centerI)) {
/* 437 */       stateCount[2] = stateCount[2] + 1;
/* 438 */       j++;
/*     */     } 
/* 440 */     if (j == maxJ) {
/* 441 */       return Float.NaN;
/*     */     }
/* 443 */     while (j < maxJ && !image.get(j, centerI) && stateCount[3] < maxCount) {
/* 444 */       stateCount[3] = stateCount[3] + 1;
/* 445 */       j++;
/*     */     } 
/* 447 */     if (j == maxJ || stateCount[3] >= maxCount) {
/* 448 */       return Float.NaN;
/*     */     }
/* 450 */     while (j < maxJ && image.get(j, centerI) && stateCount[4] < maxCount) {
/* 451 */       stateCount[4] = stateCount[4] + 1;
/* 452 */       j++;
/*     */     } 
/* 454 */     if (stateCount[4] >= maxCount) {
/* 455 */       return Float.NaN;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 460 */     int stateCountTotal = stateCount[0] + stateCount[1] + stateCount[2] + stateCount[3] + stateCount[4];
/*     */     
/* 462 */     if (5 * Math.abs(stateCountTotal - originalStateCountTotal) >= originalStateCountTotal) {
/* 463 */       return Float.NaN;
/*     */     }
/*     */     
/* 466 */     return foundPatternCross(stateCount) ? centerFromEnd(stateCount, j) : Float.NaN;
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
/*     */   protected final boolean handlePossibleCenter(int[] stateCount, int i, int j, boolean pureBarcode) {
/* 488 */     int stateCountTotal = stateCount[0] + stateCount[1] + stateCount[2] + stateCount[3] + stateCount[4];
/*     */     
/* 490 */     float centerJ = centerFromEnd(stateCount, j);
/*     */     float centerI;
/* 492 */     if (!Float.isNaN(centerI = crossCheckVertical(i, (int)centerJ, stateCount[2], stateCountTotal)))
/*     */     {
/*     */       
/* 495 */       if (!Float.isNaN(centerJ = crossCheckHorizontal((int)centerJ, (int)centerI, stateCount[2], stateCountTotal)) && (!pureBarcode || 
/* 496 */         crossCheckDiagonal((int)centerI, (int)centerJ, stateCount[2], stateCountTotal))) {
/* 497 */         float estimatedModuleSize = stateCountTotal / 7.0F;
/* 498 */         boolean found = false;
/* 499 */         for (int index = 0; index < this.possibleCenters.size(); index++) {
/*     */           FinderPattern center;
/*     */           
/* 502 */           if ((center = this.possibleCenters.get(index)).aboutEquals(estimatedModuleSize, centerI, centerJ)) {
/* 503 */             this.possibleCenters.set(index, center.combineEstimate(centerI, centerJ, estimatedModuleSize));
/* 504 */             found = true;
/*     */             break;
/*     */           } 
/*     */         } 
/* 508 */         if (!found) {
/* 509 */           FinderPattern point = new FinderPattern(centerJ, centerI, estimatedModuleSize);
/* 510 */           this.possibleCenters.add(point);
/* 511 */           if (this.resultPointCallback != null) {
/* 512 */             this.resultPointCallback.foundPossibleResultPoint(point);
/*     */           }
/*     */         } 
/* 515 */         return true;
/*     */       } 
/*     */     }
/* 518 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private int findRowSkip() {
/* 528 */     if (this.possibleCenters.size() <= 
/* 529 */       1) {
/* 530 */       return 0;
/*     */     }
/* 532 */     ResultPoint firstConfirmedCenter = null;
/* 533 */     for (Iterator<FinderPattern> iterator = this.possibleCenters.iterator(); iterator.hasNext();) {
/* 534 */       if ((center = iterator.next()).getCount() >= 2) {
/* 535 */         if (firstConfirmedCenter == null) {
/* 536 */           firstConfirmedCenter = center;
/*     */ 
/*     */           
/*     */           continue;
/*     */         } 
/*     */ 
/*     */         
/* 543 */         this.hasSkipped = true;
/* 544 */         return 
/* 545 */           (int)(Math.abs(firstConfirmedCenter.getX() - center.getX()) - Math.abs(firstConfirmedCenter.getY() - center.getY())) / 2;
/*     */       } 
/*     */     } 
/*     */     
/* 549 */     return 0;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean haveMultiplyConfirmedCenters() {
/* 558 */     int confirmedCount = 0;
/* 559 */     float totalModuleSize = 0.0F;
/* 560 */     int max = this.possibleCenters.size();
/* 561 */     for (Iterator<FinderPattern> iterator = this.possibleCenters.iterator(); iterator.hasNext();) {
/* 562 */       if ((pattern = iterator.next()).getCount() >= 2) {
/* 563 */         confirmedCount++;
/* 564 */         totalModuleSize += pattern.getEstimatedModuleSize();
/*     */       } 
/*     */     } 
/* 567 */     if (confirmedCount < 3) {
/* 568 */       return false;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 574 */     float average = totalModuleSize / max;
/* 575 */     float totalDeviation = 0.0F;
/* 576 */     for (FinderPattern pattern : this.possibleCenters) {
/* 577 */       totalDeviation += Math.abs(pattern.getEstimatedModuleSize() - average);
/*     */     }
/* 579 */     return (totalDeviation <= 0.05F * totalModuleSize);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private FinderPattern[] selectBestPatterns() throws NotFoundException {
/*     */     int startSize;
/* 591 */     if ((startSize = this.possibleCenters.size()) < 3)
/*     */     {
/* 593 */       throw NotFoundException.getNotFoundInstance();
/*     */     }
/*     */ 
/*     */     
/* 597 */     if (startSize > 3) {
/*     */       
/* 599 */       float totalModuleSize = 0.0F;
/* 600 */       float square = 0.0F;
/* 601 */       for (Iterator<FinderPattern> iterator = this.possibleCenters.iterator(); iterator.hasNext(); ) {
/* 602 */         float size = ((FinderPattern)iterator.next()).getEstimatedModuleSize();
/* 603 */         totalModuleSize += size;
/* 604 */         square += size * size;
/*     */       } 
/* 606 */       float average = totalModuleSize / startSize;
/* 607 */       float stdDev = (float)Math.sqrt((square / startSize - average * average));
/*     */       
/* 609 */       Collections.sort(this.possibleCenters, new FurthestFromAverageComparator(average));
/*     */       
/* 611 */       float limit = Math.max(0.2F * average, stdDev);
/*     */       
/* 613 */       for (int i = 0; i < this.possibleCenters.size() && this.possibleCenters.size() > 3; i++) {
/*     */         
/* 615 */         if (Math.abs(((FinderPattern)this.possibleCenters.get(i)).getEstimatedModuleSize() - average) > limit) {
/* 616 */           this.possibleCenters.remove(i);
/* 617 */           i--;
/*     */         } 
/*     */       } 
/*     */     } 
/*     */     
/* 622 */     if (this.possibleCenters.size() > 3) {
/*     */ 
/*     */       
/* 625 */       float totalModuleSize = 0.0F;
/* 626 */       for (FinderPattern possibleCenter : this.possibleCenters) {
/* 627 */         totalModuleSize += possibleCenter.getEstimatedModuleSize();
/*     */       }
/*     */       
/* 630 */       float average = totalModuleSize / this.possibleCenters.size();
/*     */       
/* 632 */       Collections.sort(this.possibleCenters, new CenterComparator(average));
/*     */       
/* 634 */       this.possibleCenters.subList(3, this.possibleCenters.size()).clear();
/*     */     } 
/*     */     
/* 637 */     return new FinderPattern[] { this.possibleCenters
/* 638 */         .get(0), this.possibleCenters
/* 639 */         .get(1), this.possibleCenters
/* 640 */         .get(2) };
/*     */   }
/*     */ 
/*     */   
/*     */   private static final class FurthestFromAverageComparator
/*     */     implements Serializable, Comparator<FinderPattern>
/*     */   {
/*     */     private final float average;
/*     */     
/*     */     private FurthestFromAverageComparator(float f) {
/* 650 */       this.average = f;
/*     */     }
/*     */     
/*     */     public int compare(FinderPattern center1, FinderPattern center2) {
/* 654 */       float dA = Math.abs(center2.getEstimatedModuleSize() - this.average);
/* 655 */       float dB = Math.abs(center1.getEstimatedModuleSize() - this.average);
/* 656 */       return (dA < dB) ? -1 : ((dA == dB) ? 0 : 1);
/*     */     }
/*     */   }
/*     */   
/*     */   private static final class CenterComparator
/*     */     implements Serializable, Comparator<FinderPattern>
/*     */   {
/*     */     private final float average;
/*     */     
/*     */     private CenterComparator(float f) {
/* 666 */       this.average = f;
/*     */     }
/*     */     
/*     */     public int compare(FinderPattern center1, FinderPattern center2) {
/* 670 */       if (center2.getCount() == center1.getCount()) {
/* 671 */         float dA = Math.abs(center2.getEstimatedModuleSize() - this.average);
/* 672 */         float dB = Math.abs(center1.getEstimatedModuleSize() - this.average);
/* 673 */         return (dA < dB) ? 1 : ((dA == dB) ? 0 : -1);
/*     */       } 
/* 675 */       return center2.getCount() - center1.getCount();
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\zxing\qrcode\detector\FinderPatternFinder.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */