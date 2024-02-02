/*     */ package com.google.zxing.qrcode.decoder;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ final class FormatInformation
/*     */ {
/*     */   private static final int FORMAT_INFO_MASK_QR = 21522;
/*  34 */   private static final int[][] FORMAT_INFO_DECODE_LOOKUP = new int[][] { { 21522, 0 }, { 20773, 1 }, { 24188, 2 }, { 23371, 3 }, { 17913, 4 }, { 16590, 5 }, { 20375, 6 }, { 19104, 7 }, { 30660, 8 }, { 29427, 9 }, { 32170, 10 }, { 30877, 11 }, { 26159, 12 }, { 25368, 13 }, { 27713, 14 }, { 26998, 15 }, { 5769, 16 }, { 5054, 17 }, { 7399, 18 }, { 6608, 19 }, { 1890, 20 }, { 597, 21 }, { 3340, 22 }, { 2107, 23 }, { 13663, 24 }, { 12392, 25 }, { 16177, 26 }, { 14854, 27 }, { 9396, 28 }, { 8579, 29 }, { 11994, 30 }, { 11245, 31 } };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final ErrorCorrectionLevel errorCorrectionLevel;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final byte dataMask;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private FormatInformation(int formatInfo) {
/*  74 */     this.errorCorrectionLevel = ErrorCorrectionLevel.forBits(formatInfo >> 3 & 0x3);
/*     */     
/*  76 */     this.dataMask = (byte)(formatInfo & 0x7);
/*     */   }
/*     */   
/*     */   static int numBitsDiffering(int a, int b) {
/*  80 */     return Integer.bitCount(a ^ b);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static FormatInformation decodeFormatInformation(int maskedFormatInfo1, int maskedFormatInfo2) {
/*     */     FormatInformation formatInfo;
/*  92 */     if ((formatInfo = doDecodeFormatInformation(maskedFormatInfo1, maskedFormatInfo2)) != null) {
/*  93 */       return formatInfo;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*  98 */     return doDecodeFormatInformation(maskedFormatInfo1 ^ 0x5412, maskedFormatInfo2 ^ 0x5412);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static FormatInformation doDecodeFormatInformation(int maskedFormatInfo1, int maskedFormatInfo2) {
/* 104 */     int bestDifference = Integer.MAX_VALUE;
/* 105 */     int bestFormatInfo = 0; int arrayOfInt[][], i; byte b;
/* 106 */     for (i = (arrayOfInt = FORMAT_INFO_DECODE_LOOKUP).length, b = 0; b < i; ) {
/*     */       int[] decodeInfo; int targetInfo;
/* 108 */       if ((targetInfo = (decodeInfo = arrayOfInt[b])[0]) == maskedFormatInfo1 || targetInfo == maskedFormatInfo2)
/*     */       {
/* 110 */         return new FormatInformation(decodeInfo[1]);
/*     */       }
/*     */       int bitsDifference;
/* 113 */       if ((bitsDifference = numBitsDiffering(maskedFormatInfo1, targetInfo)) < bestDifference) {
/* 114 */         bestFormatInfo = decodeInfo[1];
/* 115 */         bestDifference = bitsDifference;
/*     */       } 
/* 117 */       if (maskedFormatInfo1 != maskedFormatInfo2)
/*     */       {
/*     */         
/* 120 */         if ((bitsDifference = numBitsDiffering(maskedFormatInfo2, targetInfo)) < bestDifference) {
/* 121 */           bestFormatInfo = decodeInfo[1];
/* 122 */           bestDifference = bitsDifference;
/*     */         } 
/*     */       }
/*     */       
/*     */       b++;
/*     */     } 
/* 128 */     if (bestDifference <= 3) {
/* 129 */       return new FormatInformation(bestFormatInfo);
/*     */     }
/* 131 */     return null;
/*     */   }
/*     */   
/*     */   ErrorCorrectionLevel getErrorCorrectionLevel() {
/* 135 */     return this.errorCorrectionLevel;
/*     */   }
/*     */   
/*     */   byte getDataMask() {
/* 139 */     return this.dataMask;
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 144 */     return this.errorCorrectionLevel.ordinal() << 3 | this.dataMask;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object o) {
/* 149 */     if (!(o instanceof FormatInformation)) {
/* 150 */       return false;
/*     */     }
/* 152 */     FormatInformation other = (FormatInformation)o;
/* 153 */     return (this.errorCorrectionLevel == other.errorCorrectionLevel && this.dataMask == other.dataMask);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\zxing\qrcode\decoder\FormatInformation.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */