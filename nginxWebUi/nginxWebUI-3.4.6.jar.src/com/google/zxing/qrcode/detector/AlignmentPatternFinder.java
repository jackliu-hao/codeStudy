/*     */ package com.google.zxing.qrcode.detector;
/*     */ 
/*     */ import com.google.zxing.NotFoundException;
/*     */ import com.google.zxing.ResultPointCallback;
/*     */ import com.google.zxing.common.BitMatrix;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Iterator;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ final class AlignmentPatternFinder
/*     */ {
/*     */   private final BitMatrix image;
/*     */   private final List<AlignmentPattern> possibleCenters;
/*     */   private final int startX;
/*     */   private final int startY;
/*     */   private final int width;
/*     */   private final int height;
/*     */   private final float moduleSize;
/*     */   private final int[] crossCheckStateCount;
/*     */   private final ResultPointCallback resultPointCallback;
/*     */   
/*     */   AlignmentPatternFinder(BitMatrix image, int startX, int startY, int width, int height, float moduleSize, ResultPointCallback resultPointCallback) {
/*  69 */     this.image = image;
/*  70 */     this.possibleCenters = new ArrayList<>(5);
/*  71 */     this.startX = startX;
/*  72 */     this.startY = startY;
/*  73 */     this.width = width;
/*  74 */     this.height = height;
/*  75 */     this.moduleSize = moduleSize;
/*  76 */     this.crossCheckStateCount = new int[3];
/*  77 */     this.resultPointCallback = resultPointCallback;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   AlignmentPattern find() throws NotFoundException {
/*  88 */     int startX = this.startX;
/*  89 */     int height = this.height;
/*  90 */     int maxJ = startX + this.width;
/*  91 */     int middleI = this.startY + height / 2;
/*     */ 
/*     */     
/*  94 */     int[] stateCount = new int[3];
/*  95 */     for (int iGen = 0; iGen < height; iGen++) {
/*     */       
/*  97 */       int i = middleI + (((iGen & 0x1) == 0) ? ((iGen + 1) / 2) : -((iGen + 1) / 2));
/*  98 */       stateCount[0] = 0;
/*  99 */       stateCount[1] = 0;
/* 100 */       stateCount[2] = 0;
/* 101 */       int j = startX;
/*     */ 
/*     */ 
/*     */       
/* 105 */       while (j < maxJ && !this.image.get(j, i)) {
/* 106 */         j++;
/*     */       }
/* 108 */       int currentState = 0;
/* 109 */       while (j < maxJ) {
/* 110 */         if (this.image.get(j, i)) {
/*     */           
/* 112 */           if (currentState == 1) {
/* 113 */             stateCount[1] = stateCount[1] + 1;
/*     */           }
/* 115 */           else if (currentState == 2) {
/* 116 */             AlignmentPattern alignmentPattern; if (foundPatternCross(stateCount) && (
/*     */               
/* 118 */               alignmentPattern = handlePossibleCenter(stateCount, i, j)) != null) {
/* 119 */               return alignmentPattern;
/*     */             }
/*     */             
/* 122 */             stateCount[0] = stateCount[2];
/* 123 */             stateCount[1] = 1;
/* 124 */             stateCount[2] = 0;
/* 125 */             currentState = 1;
/*     */           } else {
/* 127 */             stateCount[++currentState] = stateCount[++currentState] + 1;
/*     */           } 
/*     */         } else {
/*     */           
/* 131 */           if (currentState == 1) {
/* 132 */             currentState++;
/*     */           }
/* 134 */           stateCount[currentState] = stateCount[currentState] + 1;
/*     */         } 
/* 136 */         j++;
/*     */       }  AlignmentPattern confirmed;
/* 138 */       if (foundPatternCross(stateCount) && (
/*     */         
/* 140 */         confirmed = handlePossibleCenter(stateCount, i, maxJ)) != null) {
/* 141 */         return confirmed;
/*     */       }
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 149 */     if (!this.possibleCenters.isEmpty()) {
/* 150 */       return this.possibleCenters.get(0);
/*     */     }
/*     */     
/* 153 */     throw NotFoundException.getNotFoundInstance();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static float centerFromEnd(int[] stateCount, int end) {
/* 161 */     return (end - stateCount[2]) - stateCount[1] / 2.0F;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean foundPatternCross(int[] stateCount) {
/* 171 */     float moduleSize, maxVariance = (moduleSize = this.moduleSize) / 2.0F;
/* 172 */     for (int i = 0; i < 3; i++) {
/* 173 */       if (Math.abs(moduleSize - stateCount[i]) >= maxVariance) {
/* 174 */         return false;
/*     */       }
/*     */     } 
/* 177 */     return true;
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
/*     */   private float crossCheckVertical(int startI, int centerJ, int maxCount, int originalStateCountTotal) {
/*     */     BitMatrix image;
/* 195 */     int maxI = (image = this.image).getHeight();
/*     */     int[] stateCount;
/* 197 */     (stateCount = this.crossCheckStateCount)[0] = 0;
/* 198 */     stateCount[1] = 0;
/* 199 */     stateCount[2] = 0;
/*     */ 
/*     */     
/* 202 */     int i = startI;
/* 203 */     while (i >= 0 && image.get(centerJ, i) && stateCount[1] <= maxCount) {
/* 204 */       stateCount[1] = stateCount[1] + 1;
/* 205 */       i--;
/*     */     } 
/*     */     
/* 208 */     if (i < 0 || stateCount[1] > maxCount) {
/* 209 */       return Float.NaN;
/*     */     }
/* 211 */     while (i >= 0 && !image.get(centerJ, i) && stateCount[0] <= maxCount) {
/* 212 */       stateCount[0] = stateCount[0] + 1;
/* 213 */       i--;
/*     */     } 
/* 215 */     if (stateCount[0] > maxCount) {
/* 216 */       return Float.NaN;
/*     */     }
/*     */ 
/*     */     
/* 220 */     i = startI + 1;
/* 221 */     while (i < maxI && image.get(centerJ, i) && stateCount[1] <= maxCount) {
/* 222 */       stateCount[1] = stateCount[1] + 1;
/* 223 */       i++;
/*     */     } 
/* 225 */     if (i == maxI || stateCount[1] > maxCount) {
/* 226 */       return Float.NaN;
/*     */     }
/* 228 */     while (i < maxI && !image.get(centerJ, i) && stateCount[2] <= maxCount) {
/* 229 */       stateCount[2] = stateCount[2] + 1;
/* 230 */       i++;
/*     */     } 
/* 232 */     if (stateCount[2] > maxCount) {
/* 233 */       return Float.NaN;
/*     */     }
/*     */     
/* 236 */     int stateCountTotal = stateCount[0] + stateCount[1] + stateCount[2];
/* 237 */     if (5 * Math.abs(stateCountTotal - originalStateCountTotal) >= 2 * originalStateCountTotal) {
/* 238 */       return Float.NaN;
/*     */     }
/*     */     
/* 241 */     return foundPatternCross(stateCount) ? centerFromEnd(stateCount, i) : Float.NaN;
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
/*     */   private AlignmentPattern handlePossibleCenter(int[] stateCount, int i, int j) {
/* 256 */     int stateCountTotal = stateCount[0] + stateCount[1] + stateCount[2];
/* 257 */     float centerJ = centerFromEnd(stateCount, j);
/*     */     float centerI;
/* 259 */     if (!Float.isNaN(centerI = crossCheckVertical(i, (int)centerJ, 2 * stateCount[1], stateCountTotal))) {
/* 260 */       float estimatedModuleSize = (stateCount[0] + stateCount[1] + stateCount[2]) / 3.0F;
/* 261 */       for (Iterator<AlignmentPattern> iterator = this.possibleCenters.iterator(); iterator.hasNext();) {
/*     */         
/* 263 */         if ((center = iterator.next()).aboutEquals(estimatedModuleSize, centerI, centerJ)) {
/* 264 */           return center.combineEstimate(centerI, centerJ, estimatedModuleSize);
/*     */         }
/*     */       } 
/*     */       
/* 268 */       AlignmentPattern point = new AlignmentPattern(centerJ, centerI, estimatedModuleSize);
/* 269 */       this.possibleCenters.add(point);
/* 270 */       if (this.resultPointCallback != null) {
/* 271 */         this.resultPointCallback.foundPossibleResultPoint(point);
/*     */       }
/*     */     } 
/* 274 */     return null;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\zxing\qrcode\detector\AlignmentPatternFinder.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */