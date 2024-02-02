/*     */ package com.google.zxing.oned;
/*     */ 
/*     */ import com.google.zxing.BarcodeFormat;
/*     */ import com.google.zxing.NotFoundException;
/*     */ import com.google.zxing.common.BitArray;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class EAN13Reader
/*     */   extends UPCEANReader
/*     */ {
/*  61 */   static final int[] FIRST_DIGIT_ENCODINGS = new int[] { 0, 11, 13, 14, 19, 25, 28, 21, 22, 26 };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  68 */   private final int[] decodeMiddleCounters = new int[4];
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected int decodeMiddle(BitArray row, int[] startRange, StringBuilder resultString) throws NotFoundException {
/*     */     int[] counters;
/*  76 */     (counters = this.decodeMiddleCounters)[0] = 0;
/*  77 */     counters[1] = 0;
/*  78 */     counters[2] = 0;
/*  79 */     counters[3] = 0;
/*  80 */     int end = row.getSize();
/*  81 */     int rowOffset = startRange[1];
/*     */     
/*  83 */     int lgPatternFound = 0;
/*     */     
/*  85 */     for (int x = 0; x < 6 && rowOffset < end; x++) {
/*  86 */       int bestMatch = decodeDigit(row, counters, rowOffset, L_AND_G_PATTERNS);
/*  87 */       resultString.append((char)(48 + bestMatch % 10)); int arrayOfInt[], j; byte b;
/*  88 */       for (j = (arrayOfInt = counters).length, b = 0; b < j; ) { int counter = arrayOfInt[b];
/*  89 */         rowOffset += counter; b++; }
/*     */       
/*  91 */       if (bestMatch >= 10) {
/*  92 */         lgPatternFound |= 1 << 5 - x;
/*     */       }
/*     */     } 
/*     */     
/*  96 */     determineFirstDigit(resultString, lgPatternFound);
/*     */ 
/*     */     
/*  99 */     rowOffset = findGuardPattern(row, rowOffset, true, MIDDLE_PATTERN)[1];
/*     */     
/* 101 */     for (int i = 0; i < 6 && rowOffset < end; i++) {
/* 102 */       int bestMatch = decodeDigit(row, counters, rowOffset, L_PATTERNS);
/* 103 */       resultString.append((char)(bestMatch + 48)); int arrayOfInt[], j; byte b;
/* 104 */       for (j = (arrayOfInt = counters).length, b = 0; b < j; ) { int counter = arrayOfInt[b];
/* 105 */         rowOffset += counter;
/*     */         b++; }
/*     */     
/*     */     } 
/* 109 */     return rowOffset;
/*     */   }
/*     */ 
/*     */   
/*     */   BarcodeFormat getBarcodeFormat() {
/* 114 */     return BarcodeFormat.EAN_13;
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
/*     */   private static void determineFirstDigit(StringBuilder resultString, int lgPatternFound) throws NotFoundException {
/* 129 */     for (int d = 0; d < 10; d++) {
/* 130 */       if (lgPatternFound == FIRST_DIGIT_ENCODINGS[d]) {
/* 131 */         resultString.insert(0, (char)(d + 48));
/*     */         return;
/*     */       } 
/*     */     } 
/* 135 */     throw NotFoundException.getNotFoundInstance();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\zxing\oned\EAN13Reader.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */