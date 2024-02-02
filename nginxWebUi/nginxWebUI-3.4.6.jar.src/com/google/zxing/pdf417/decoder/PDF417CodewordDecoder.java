/*     */ package com.google.zxing.pdf417.decoder;
/*     */ 
/*     */ import com.google.zxing.common.detector.MathUtils;
/*     */ import com.google.zxing.pdf417.PDF417Common;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ final class PDF417CodewordDecoder
/*     */ {
/*  28 */   private static final float[][] RATIOS_TABLE = new float[PDF417Common.SYMBOL_TABLE.length][8];
/*     */ 
/*     */ 
/*     */   
/*     */   static {
/*  33 */     for (int i = 0; i < PDF417Common.SYMBOL_TABLE.length; i++) {
/*     */       
/*  35 */       int currentSymbol, currentBit = (currentSymbol = PDF417Common.SYMBOL_TABLE[i]) & 0x1;
/*  36 */       for (int j = 0; j < 8; j++) {
/*  37 */         float size = 0.0F;
/*  38 */         while ((currentSymbol & 0x1) == currentBit) {
/*  39 */           size++;
/*  40 */           currentSymbol >>= 1;
/*     */         } 
/*  42 */         currentBit = currentSymbol & 0x1;
/*  43 */         RATIOS_TABLE[i][8 - j - 1] = size / 17.0F;
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static int getDecodedValue(int[] moduleBitCount) {
/*     */     int decodedValue;
/*  53 */     if ((decodedValue = getDecodedCodewordValue(sampleBitCounts(moduleBitCount))) != -1) {
/*  54 */       return decodedValue;
/*     */     }
/*  56 */     return getClosestDecodedValue(moduleBitCount);
/*     */   }
/*     */   
/*     */   private static int[] sampleBitCounts(int[] moduleBitCount) {
/*  60 */     float bitCountSum = MathUtils.sum(moduleBitCount);
/*  61 */     int[] result = new int[8];
/*  62 */     int bitCountIndex = 0;
/*  63 */     int sumPreviousBits = 0;
/*  64 */     for (int i = 0; i < 17; i++) {
/*  65 */       float sampleIndex = bitCountSum / 34.0F + i * bitCountSum / 17.0F;
/*     */ 
/*     */       
/*  68 */       if ((sumPreviousBits + moduleBitCount[bitCountIndex]) <= sampleIndex) {
/*  69 */         sumPreviousBits += moduleBitCount[bitCountIndex];
/*  70 */         bitCountIndex++;
/*     */       } 
/*  72 */       result[bitCountIndex] = result[bitCountIndex] + 1;
/*     */     } 
/*  74 */     return result;
/*     */   }
/*     */   
/*     */   private static int getDecodedCodewordValue(int[] moduleBitCount) {
/*     */     int decodedValue;
/*  79 */     return (PDF417Common.getCodeword(decodedValue = getBitValue(moduleBitCount)) == -1) ? -1 : decodedValue;
/*     */   }
/*     */   
/*     */   private static int getBitValue(int[] moduleBitCount) {
/*  83 */     long result = 0L;
/*  84 */     for (int i = 0; i < moduleBitCount.length; i++) {
/*  85 */       for (int bit = 0; bit < moduleBitCount[i]; bit++) {
/*  86 */         result = result << 1L | ((i % 2 == 0) ? 1L : 0L);
/*     */       }
/*     */     } 
/*  89 */     return (int)result;
/*     */   }
/*     */   
/*     */   private static int getClosestDecodedValue(int[] moduleBitCount) {
/*  93 */     int bitCountSum = MathUtils.sum(moduleBitCount);
/*  94 */     float[] bitCountRatios = new float[8];
/*  95 */     for (int i = 0; i < 8; i++) {
/*  96 */       bitCountRatios[i] = moduleBitCount[i] / bitCountSum;
/*     */     }
/*  98 */     float bestMatchError = Float.MAX_VALUE;
/*  99 */     int bestMatch = -1;
/* 100 */     for (int j = 0; j < RATIOS_TABLE.length; j++) {
/* 101 */       float error = 0.0F;
/* 102 */       float[] ratioTableRow = RATIOS_TABLE[j];
/* 103 */       int k = 0;
/* 104 */       float diff = ratioTableRow[k] - bitCountRatios[k];
/*     */       
/* 106 */       for (; k < 8 && (error = error + diff * diff) < bestMatchError; k++);
/*     */ 
/*     */ 
/*     */       
/* 110 */       if (error < bestMatchError) {
/* 111 */         bestMatchError = error;
/* 112 */         bestMatch = PDF417Common.SYMBOL_TABLE[j];
/*     */       } 
/*     */     } 
/* 115 */     return bestMatch;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\zxing\pdf417\decoder\PDF417CodewordDecoder.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */