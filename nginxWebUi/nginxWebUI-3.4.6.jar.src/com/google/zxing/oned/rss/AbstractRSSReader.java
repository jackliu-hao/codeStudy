/*     */ package com.google.zxing.oned.rss;
/*     */ 
/*     */ import com.google.zxing.NotFoundException;
/*     */ import com.google.zxing.common.detector.MathUtils;
/*     */ import com.google.zxing.oned.OneDReader;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class AbstractRSSReader
/*     */   extends OneDReader
/*     */ {
/*     */   private static final float MAX_AVG_VARIANCE = 0.2F;
/*     */   private static final float MAX_INDIVIDUAL_VARIANCE = 0.45F;
/*     */   private static final float MIN_FINDER_PATTERN_RATIO = 0.7916667F;
/*     */   private static final float MAX_FINDER_PATTERN_RATIO = 0.89285713F;
/*  43 */   private final int[] decodeFinderCounters = new int[4];
/*  44 */   private final int[] dataCharacterCounters = new int[8];
/*  45 */   private final float[] oddRoundingErrors = new float[4];
/*  46 */   private final float[] evenRoundingErrors = new float[4];
/*  47 */   private final int[] oddCounts = new int[this.dataCharacterCounters.length / 2];
/*  48 */   private final int[] evenCounts = new int[this.dataCharacterCounters.length / 2];
/*     */ 
/*     */   
/*     */   protected final int[] getDecodeFinderCounters() {
/*  52 */     return this.decodeFinderCounters;
/*     */   }
/*     */   
/*     */   protected final int[] getDataCharacterCounters() {
/*  56 */     return this.dataCharacterCounters;
/*     */   }
/*     */   
/*     */   protected final float[] getOddRoundingErrors() {
/*  60 */     return this.oddRoundingErrors;
/*     */   }
/*     */   
/*     */   protected final float[] getEvenRoundingErrors() {
/*  64 */     return this.evenRoundingErrors;
/*     */   }
/*     */   
/*     */   protected final int[] getOddCounts() {
/*  68 */     return this.oddCounts;
/*     */   }
/*     */   
/*     */   protected final int[] getEvenCounts() {
/*  72 */     return this.evenCounts;
/*     */   }
/*     */ 
/*     */   
/*     */   protected static int parseFinderValue(int[] counters, int[][] finderPatterns) throws NotFoundException {
/*  77 */     for (int value = 0; value < finderPatterns.length; value++) {
/*  78 */       if (patternMatchVariance(counters, finderPatterns[value], 0.45F) < 0.2F)
/*     */       {
/*  80 */         return value;
/*     */       }
/*     */     } 
/*  83 */     throw NotFoundException.getNotFoundInstance();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   protected static int count(int[] array) {
/*  93 */     return MathUtils.sum(array);
/*     */   }
/*     */   
/*     */   protected static void increment(int[] array, float[] errors) {
/*  97 */     int index = 0;
/*  98 */     float biggestError = errors[0];
/*  99 */     for (int i = 1; i < array.length; i++) {
/* 100 */       if (errors[i] > biggestError) {
/* 101 */         biggestError = errors[i];
/* 102 */         index = i;
/*     */       } 
/*     */     } 
/* 105 */     array[index] = array[index] + 1;
/*     */   }
/*     */   
/*     */   protected static void decrement(int[] array, float[] errors) {
/* 109 */     int index = 0;
/* 110 */     float biggestError = errors[0];
/* 111 */     for (int i = 1; i < array.length; i++) {
/* 112 */       if (errors[i] < biggestError) {
/* 113 */         biggestError = errors[i];
/* 114 */         index = i;
/*     */       } 
/*     */     } 
/* 117 */     array[index] = array[index] - 1;
/*     */   }
/*     */ 
/*     */   
/*     */   protected static boolean isFinderPattern(int[] counters) {
/* 122 */     int firstTwoSum, sum = (firstTwoSum = counters[0] + counters[1]) + counters[2] + counters[3];
/*     */     float ratio;
/* 124 */     if ((ratio = firstTwoSum / sum) >= 0.7916667F && ratio <= 0.89285713F) {
/*     */       
/* 126 */       int minCounter = Integer.MAX_VALUE;
/* 127 */       int maxCounter = Integer.MIN_VALUE; int arrayOfInt[], i; byte b;
/* 128 */       for (i = (arrayOfInt = counters).length, b = 0; b < i; b++) {
/* 129 */         int counter; if ((counter = arrayOfInt[b]) > maxCounter) {
/* 130 */           maxCounter = counter;
/*     */         }
/* 132 */         if (counter < minCounter) {
/* 133 */           minCounter = counter;
/*     */         }
/*     */       } 
/* 136 */       return (maxCounter < minCounter * 10);
/*     */     } 
/* 138 */     return false;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\zxing\oned\rss\AbstractRSSReader.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */