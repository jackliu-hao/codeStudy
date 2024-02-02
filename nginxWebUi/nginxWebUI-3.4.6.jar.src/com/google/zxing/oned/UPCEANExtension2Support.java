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
/*     */ final class UPCEANExtension2Support
/*     */ {
/*  34 */   private final int[] decodeMiddleCounters = new int[4];
/*  35 */   private final StringBuilder decodeRowStringBuffer = new StringBuilder();
/*     */ 
/*     */   
/*     */   Result decodeRow(int rowNumber, BitArray row, int[] extensionStartRange) throws NotFoundException {
/*     */     StringBuilder result;
/*  40 */     (result = this.decodeRowStringBuffer).setLength(0);
/*  41 */     int end = decodeMiddle(row, extensionStartRange, result);
/*     */     
/*     */     String resultString;
/*  44 */     Map<ResultMetadataType, Object> extensionData = parseExtensionString(resultString = result.toString());
/*     */     
/*  46 */     Result extensionResult = new Result(resultString, null, new ResultPoint[] { new ResultPoint((extensionStartRange[0] + extensionStartRange[1]) / 2.0F, rowNumber), new ResultPoint(end, rowNumber) }BarcodeFormat.UPC_EAN_EXTENSION);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  54 */     if (extensionData != null) {
/*  55 */       extensionResult.putAllMetadata(extensionData);
/*     */     }
/*  57 */     return extensionResult;
/*     */   }
/*     */   
/*     */   private int decodeMiddle(BitArray row, int[] startRange, StringBuilder resultString) throws NotFoundException {
/*     */     int[] counters;
/*  62 */     (counters = this.decodeMiddleCounters)[0] = 0;
/*  63 */     counters[1] = 0;
/*  64 */     counters[2] = 0;
/*  65 */     counters[3] = 0;
/*  66 */     int end = row.getSize();
/*  67 */     int rowOffset = startRange[1];
/*     */     
/*  69 */     int checkParity = 0;
/*     */     
/*  71 */     for (int x = 0; x < 2 && rowOffset < end; x++) {
/*  72 */       int bestMatch = UPCEANReader.decodeDigit(row, counters, rowOffset, UPCEANReader.L_AND_G_PATTERNS);
/*  73 */       resultString.append((char)(48 + bestMatch % 10)); int arrayOfInt[], i; byte b;
/*  74 */       for (i = (arrayOfInt = counters).length, b = 0; b < i; ) { int counter = arrayOfInt[b];
/*  75 */         rowOffset += counter; b++; }
/*     */       
/*  77 */       if (bestMatch >= 10) {
/*  78 */         checkParity |= 1 << 1 - x;
/*     */       }
/*  80 */       if (x != 1) {
/*     */         
/*  82 */         rowOffset = row.getNextSet(rowOffset);
/*  83 */         rowOffset = row.getNextUnset(rowOffset);
/*     */       } 
/*     */     } 
/*     */     
/*  87 */     if (resultString.length() != 2) {
/*  88 */       throw NotFoundException.getNotFoundInstance();
/*     */     }
/*     */     
/*  91 */     if (Integer.parseInt(resultString.toString()) % 4 != checkParity) {
/*  92 */       throw NotFoundException.getNotFoundInstance();
/*     */     }
/*     */     
/*  95 */     return rowOffset;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static Map<ResultMetadataType, Object> parseExtensionString(String raw) {
/* 104 */     if (raw.length() != 2) {
/* 105 */       return null;
/*     */     }
/*     */     Map<ResultMetadataType, Object> result;
/* 108 */     (result = new EnumMap<>(ResultMetadataType.class)).put(ResultMetadataType.ISSUE_NUMBER, Integer.valueOf(raw));
/* 109 */     return result;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\zxing\oned\UPCEANExtension2Support.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */