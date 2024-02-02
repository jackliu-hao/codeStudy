/*     */ package com.google.zxing.oned;
/*     */ 
/*     */ import com.google.zxing.BarcodeFormat;
/*     */ import com.google.zxing.NotFoundException;
/*     */ import com.google.zxing.Result;
/*     */ import com.google.zxing.ResultMetadataType;
/*     */ import com.google.zxing.ResultPoint;
/*     */ import com.google.zxing.common.BitArray;
/*     */ import java.util.EnumMap;
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
/*     */ final class UPCEANExtension5Support
/*     */ {
/*  34 */   private static final int[] CHECK_DIGIT_ENCODINGS = new int[] { 24, 20, 18, 17, 12, 6, 3, 10, 9, 5 };
/*     */ 
/*     */ 
/*     */   
/*  38 */   private final int[] decodeMiddleCounters = new int[4];
/*  39 */   private final StringBuilder decodeRowStringBuffer = new StringBuilder();
/*     */ 
/*     */   
/*     */   Result decodeRow(int rowNumber, BitArray row, int[] extensionStartRange) throws NotFoundException {
/*     */     StringBuilder result;
/*  44 */     (result = this.decodeRowStringBuffer).setLength(0);
/*  45 */     int end = decodeMiddle(row, extensionStartRange, result);
/*     */     
/*     */     String resultString;
/*  48 */     Map<ResultMetadataType, Object> extensionData = parseExtensionString(resultString = result.toString());
/*     */     
/*  50 */     Result extensionResult = new Result(resultString, null, new ResultPoint[] { new ResultPoint((extensionStartRange[0] + extensionStartRange[1]) / 2.0F, rowNumber), new ResultPoint(end, rowNumber) }BarcodeFormat.UPC_EAN_EXTENSION);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  58 */     if (extensionData != null) {
/*  59 */       extensionResult.putAllMetadata(extensionData);
/*     */     }
/*  61 */     return extensionResult;
/*     */   }
/*     */   
/*     */   private int decodeMiddle(BitArray row, int[] startRange, StringBuilder resultString) throws NotFoundException {
/*     */     int[] counters;
/*  66 */     (counters = this.decodeMiddleCounters)[0] = 0;
/*  67 */     counters[1] = 0;
/*  68 */     counters[2] = 0;
/*  69 */     counters[3] = 0;
/*  70 */     int end = row.getSize();
/*  71 */     int rowOffset = startRange[1];
/*     */     
/*  73 */     int lgPatternFound = 0;
/*     */     
/*  75 */     for (int x = 0; x < 5 && rowOffset < end; x++) {
/*  76 */       int bestMatch = UPCEANReader.decodeDigit(row, counters, rowOffset, UPCEANReader.L_AND_G_PATTERNS);
/*  77 */       resultString.append((char)(48 + bestMatch % 10)); int arrayOfInt[], i; byte b;
/*  78 */       for (i = (arrayOfInt = counters).length, b = 0; b < i; ) { int counter = arrayOfInt[b];
/*  79 */         rowOffset += counter; b++; }
/*     */       
/*  81 */       if (bestMatch >= 10) {
/*  82 */         lgPatternFound |= 1 << 4 - x;
/*     */       }
/*  84 */       if (x != 4) {
/*     */         
/*  86 */         rowOffset = row.getNextSet(rowOffset);
/*  87 */         rowOffset = row.getNextUnset(rowOffset);
/*     */       } 
/*     */     } 
/*     */     
/*  91 */     if (resultString.length() != 5) {
/*  92 */       throw NotFoundException.getNotFoundInstance();
/*     */     }
/*     */     
/*  95 */     int checkDigit = determineCheckDigit(lgPatternFound);
/*  96 */     if (extensionChecksum(resultString.toString()) != checkDigit) {
/*  97 */       throw NotFoundException.getNotFoundInstance();
/*     */     }
/*     */     
/* 100 */     return rowOffset;
/*     */   }
/*     */   
/*     */   private static int extensionChecksum(CharSequence s) {
/* 104 */     int length = s.length();
/* 105 */     int sum = 0; int i;
/* 106 */     for (i = length - 2; i >= 0; i -= 2) {
/* 107 */       sum += s.charAt(i) - 48;
/*     */     }
/* 109 */     sum *= 3;
/* 110 */     for (i = length - 1; i >= 0; i -= 2) {
/* 111 */       sum += s.charAt(i) - 48;
/*     */     }
/*     */     
/* 114 */     return sum * 3 % 10;
/*     */   }
/*     */ 
/*     */   
/*     */   private static int determineCheckDigit(int lgPatternFound) throws NotFoundException {
/* 119 */     for (int d = 0; d < 10; d++) {
/* 120 */       if (lgPatternFound == CHECK_DIGIT_ENCODINGS[d]) {
/* 121 */         return d;
/*     */       }
/*     */     } 
/* 124 */     throw NotFoundException.getNotFoundInstance();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static Map<ResultMetadataType, Object> parseExtensionString(String raw) {
/* 133 */     if (raw.length() != 5) {
/* 134 */       return null;
/*     */     }
/*     */     Object value;
/* 137 */     if ((value = parseExtension5String(raw)) == null) {
/* 138 */       return null;
/*     */     }
/*     */     Map<ResultMetadataType, Object> result;
/* 141 */     (result = new EnumMap<>(ResultMetadataType.class)).put(ResultMetadataType.SUGGESTED_PRICE, value);
/* 142 */     return result;
/*     */   }
/*     */   
/*     */   private static String parseExtension5String(String raw) {
/*     */     String currency;
/* 147 */     switch (raw.charAt(0)) {
/*     */       case '0':
/* 149 */         currency = "£";
/*     */         break;
/*     */       case '5':
/* 152 */         currency = "$";
/*     */         break;
/*     */       
/*     */       case '9':
/* 156 */         if ("90000".equals(raw))
/*     */         {
/* 158 */           return null;
/*     */         }
/* 160 */         if ("99991".equals(raw))
/*     */         {
/* 162 */           return "0.00";
/*     */         }
/* 164 */         if ("99990".equals(raw)) {
/* 165 */           return "Used";
/*     */         }
/*     */         
/* 168 */         currency = "";
/*     */         break;
/*     */       default:
/* 171 */         currency = "";
/*     */         break;
/*     */     } 
/*     */     int rawAmount;
/* 175 */     String unitsString = String.valueOf((rawAmount = Integer.parseInt(raw.substring(1))) / 100);
/*     */     int hundredths;
/* 177 */     String hundredthsString = ((hundredths = rawAmount % 100) < 10) ? ("0" + hundredths) : String.valueOf(hundredths);
/* 178 */     return currency + unitsString + '.' + hundredthsString;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\zxing\oned\UPCEANExtension5Support.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */