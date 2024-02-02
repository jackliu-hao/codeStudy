/*     */ package com.google.zxing.oned;
/*     */ 
/*     */ import com.google.zxing.BarcodeFormat;
/*     */ import com.google.zxing.FormatException;
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
/*     */ public final class UPCEReader
/*     */   extends UPCEANReader
/*     */ {
/*  55 */   static final int[] CHECK_DIGIT_ENCODINGS = new int[] { 56, 52, 50, 49, 44, 38, 35, 42, 41, 37 };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  62 */   private static final int[] MIDDLE_END_PATTERN = new int[] { 1, 1, 1, 1, 1, 1 };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  69 */   private static final int[][] NUMSYS_AND_CHECK_DIGIT_PATTERNS = new int[][] { { 56, 52, 50, 49, 44, 38, 35, 42, 41, 37 }, { 7, 11, 13, 14, 19, 25, 28, 21, 22, 26 } };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  77 */   private final int[] decodeMiddleCounters = new int[4];
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected int decodeMiddle(BitArray row, int[] startRange, StringBuilder result) throws NotFoundException {
/*     */     int[] counters;
/*  84 */     (counters = this.decodeMiddleCounters)[0] = 0;
/*  85 */     counters[1] = 0;
/*  86 */     counters[2] = 0;
/*  87 */     counters[3] = 0;
/*  88 */     int end = row.getSize();
/*  89 */     int rowOffset = startRange[1];
/*     */     
/*  91 */     int lgPatternFound = 0;
/*     */     
/*  93 */     for (int x = 0; x < 6 && rowOffset < end; x++) {
/*  94 */       int bestMatch = decodeDigit(row, counters, rowOffset, L_AND_G_PATTERNS);
/*  95 */       result.append((char)(48 + bestMatch % 10)); int arrayOfInt[], i; byte b;
/*  96 */       for (i = (arrayOfInt = counters).length, b = 0; b < i; ) { int counter = arrayOfInt[b];
/*  97 */         rowOffset += counter; b++; }
/*     */       
/*  99 */       if (bestMatch >= 10) {
/* 100 */         lgPatternFound |= 1 << 5 - x;
/*     */       }
/*     */     } 
/*     */     
/* 104 */     determineNumSysAndCheckDigit(result, lgPatternFound);
/*     */     
/* 106 */     return rowOffset;
/*     */   }
/*     */ 
/*     */   
/*     */   protected int[] decodeEnd(BitArray row, int endStart) throws NotFoundException {
/* 111 */     return findGuardPattern(row, endStart, true, MIDDLE_END_PATTERN);
/*     */   }
/*     */ 
/*     */   
/*     */   protected boolean checkChecksum(String s) throws FormatException {
/* 116 */     return super.checkChecksum(convertUPCEtoUPCA(s));
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static void determineNumSysAndCheckDigit(StringBuilder resultString, int lgPatternFound) throws NotFoundException {
/* 122 */     for (int numSys = 0; numSys <= 1; numSys++) {
/* 123 */       for (int d = 0; d < 10; d++) {
/* 124 */         if (lgPatternFound == NUMSYS_AND_CHECK_DIGIT_PATTERNS[numSys][d]) {
/* 125 */           resultString.insert(0, (char)(numSys + 48));
/* 126 */           resultString.append((char)(d + 48));
/*     */           return;
/*     */         } 
/*     */       } 
/*     */     } 
/* 131 */     throw NotFoundException.getNotFoundInstance();
/*     */   }
/*     */ 
/*     */   
/*     */   BarcodeFormat getBarcodeFormat() {
/* 136 */     return BarcodeFormat.UPC_E;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String convertUPCEtoUPCA(String upce) {
/* 146 */     char[] upceChars = new char[6];
/* 147 */     upce.getChars(1, 7, upceChars, 0);
/*     */     StringBuilder result;
/* 149 */     (result = new StringBuilder(12)).append(upce.charAt(0));
/*     */     char lastChar;
/* 151 */     switch (lastChar = upceChars[5])
/*     */     { case '0':
/*     */       case '1':
/*     */       case '2':
/* 155 */         result.append(upceChars, 0, 2);
/* 156 */         result.append(lastChar);
/* 157 */         result.append("0000");
/* 158 */         result.append(upceChars, 2, 3);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 176 */         result.append(upce.charAt(7));
/* 177 */         return result.toString();case '3': result.append(upceChars, 0, 3); result.append("00000"); result.append(upceChars, 3, 2); result.append(upce.charAt(7)); return result.toString();case '4': result.append(upceChars, 0, 4); result.append("00000"); result.append(upceChars[4]); result.append(upce.charAt(7)); return result.toString(); }  result.append(upceChars, 0, 5); result.append("0000"); result.append(lastChar); result.append(upce.charAt(7)); return result.toString();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\zxing\oned\UPCEReader.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */