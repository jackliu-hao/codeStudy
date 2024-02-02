/*     */ package com.google.zxing.oned;
/*     */ 
/*     */ import com.google.zxing.BarcodeFormat;
/*     */ import com.google.zxing.ChecksumException;
/*     */ import com.google.zxing.DecodeHintType;
/*     */ import com.google.zxing.FormatException;
/*     */ import com.google.zxing.NotFoundException;
/*     */ import com.google.zxing.Result;
/*     */ import com.google.zxing.ResultPoint;
/*     */ import com.google.zxing.common.BitArray;
/*     */ import java.util.Arrays;
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
/*     */ public final class Code93Reader
/*     */   extends OneDReader
/*     */ {
/*     */   static final String ALPHABET_STRING = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ-. $/+%abcd*";
/*  41 */   private static final char[] ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ-. $/+%abcd*".toCharArray();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  47 */   static final int[] CHARACTER_ENCODINGS = new int[] { 276, 328, 324, 322, 296, 292, 290, 336, 274, 266, 424, 420, 418, 404, 402, 394, 360, 356, 354, 308, 282, 344, 332, 326, 300, 278, 436, 434, 428, 422, 406, 410, 364, 358, 310, 314, 302, 468, 466, 458, 366, 374, 430, 294, 474, 470, 306, 350 }; private static final int ASTERISK_ENCODING = (new int[48])[
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/*  55 */       47];
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  61 */   private final StringBuilder decodeRowResult = new StringBuilder(20);
/*  62 */   private final int[] counters = new int[6];
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Result decodeRow(int rowNumber, BitArray row, Map<DecodeHintType, ?> hints) throws NotFoundException, ChecksumException, FormatException {
/*  69 */     int[] start = findAsteriskPattern(row);
/*     */     
/*  71 */     int nextStart = row.getNextSet(start[1]);
/*  72 */     int end = row.getSize();
/*     */     
/*     */     int[] theCounters;
/*  75 */     Arrays.fill(theCounters = this.counters, 0);
/*     */     StringBuilder result;
/*  77 */     (result = this.decodeRowResult).setLength(0);
/*     */ 
/*     */ 
/*     */     
/*     */     while (true) {
/*  82 */       recordPattern(row, nextStart, theCounters);
/*     */       int pattern;
/*  84 */       if ((pattern = toPattern(theCounters)) < 0) {
/*  85 */         throw NotFoundException.getNotFoundInstance();
/*     */       }
/*  87 */       char decodedChar = patternToChar(pattern);
/*  88 */       result.append(decodedChar);
/*  89 */       int lastStart = nextStart; int arrayOfInt[], i; byte b;
/*  90 */       for (i = (arrayOfInt = theCounters).length, b = 0; b < i; ) { int counter = arrayOfInt[b];
/*  91 */         nextStart += counter;
/*     */         b++; }
/*     */       
/*  94 */       nextStart = row.getNextSet(nextStart);
/*  95 */       if (decodedChar == '*') {
/*  96 */         result.deleteCharAt(result.length() - 1);
/*     */         
/*  98 */         int lastPatternSize = 0;
/*  99 */         for (i = (arrayOfInt = theCounters).length, b = 0; b < i; ) { int counter = arrayOfInt[b];
/* 100 */           lastPatternSize += counter;
/*     */           
/*     */           b++; }
/*     */         
/* 104 */         if (nextStart == end || !row.get(nextStart)) {
/* 105 */           throw NotFoundException.getNotFoundInstance();
/*     */         }
/*     */         
/* 108 */         if (result.length() < 2)
/*     */         {
/* 110 */           throw NotFoundException.getNotFoundInstance();
/*     */         }
/*     */         
/* 113 */         checkChecksums(result);
/*     */         
/* 115 */         result.setLength(result.length() - 2);
/*     */         
/* 117 */         String resultString = decodeExtended(result);
/*     */         
/* 119 */         float left = (start[1] + start[0]) / 2.0F;
/* 120 */         float right = lastStart + lastPatternSize / 2.0F;
/* 121 */         return new Result(resultString, null, new ResultPoint[] { new ResultPoint(left, rowNumber), new ResultPoint(right, rowNumber) }BarcodeFormat.CODE_93);
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private int[] findAsteriskPattern(BitArray row) throws NotFoundException {
/* 132 */     int width = row.getSize();
/* 133 */     int rowOffset = row.getNextSet(0);
/*     */     
/* 135 */     Arrays.fill(this.counters, 0);
/* 136 */     int[] theCounters = this.counters;
/* 137 */     int patternStart = rowOffset;
/* 138 */     boolean isWhite = false;
/* 139 */     int patternLength = theCounters.length;
/*     */     
/* 141 */     int counterPosition = 0;
/* 142 */     for (int i = rowOffset; i < width; i++) {
/* 143 */       if (row.get(i) ^ isWhite) {
/* 144 */         theCounters[counterPosition] = theCounters[counterPosition] + 1;
/*     */       } else {
/* 146 */         if (counterPosition == patternLength - 1) {
/* 147 */           if (toPattern(theCounters) == ASTERISK_ENCODING) {
/* 148 */             return new int[] { patternStart, i };
/*     */           }
/* 150 */           patternStart += theCounters[0] + theCounters[1];
/* 151 */           System.arraycopy(theCounters, 2, theCounters, 0, patternLength - 2);
/* 152 */           theCounters[patternLength - 2] = 0;
/* 153 */           theCounters[patternLength - 1] = 0;
/* 154 */           counterPosition--;
/*     */         } else {
/* 156 */           counterPosition++;
/*     */         } 
/* 158 */         theCounters[counterPosition] = 1;
/* 159 */         isWhite = !isWhite;
/*     */       } 
/*     */     } 
/* 162 */     throw NotFoundException.getNotFoundInstance();
/*     */   }
/*     */   
/*     */   private static int toPattern(int[] counters) {
/* 166 */     int sum = 0; int arrayOfInt[], j; byte b;
/* 167 */     for (j = (arrayOfInt = counters).length, b = 0; b < j; ) { int counter = arrayOfInt[b];
/* 168 */       sum += counter; b++; }
/*     */     
/* 170 */     int pattern = 0;
/* 171 */     int max = counters.length;
/* 172 */     for (int i = 0; i < max; i++) {
/*     */       int scaled;
/* 174 */       if ((scaled = Math.round(counters[i] * 9.0F / sum)) <= 0 || scaled > 4) {
/* 175 */         return -1;
/*     */       }
/* 177 */       if ((i & 0x1) == 0) {
/* 178 */         for (int k = 0; k < scaled; k++) {
/* 179 */           pattern = pattern << 1 | 0x1;
/*     */         }
/*     */       } else {
/* 182 */         pattern <<= scaled;
/*     */       } 
/*     */     } 
/* 185 */     return pattern;
/*     */   }
/*     */   
/*     */   private static char patternToChar(int pattern) throws NotFoundException {
/* 189 */     for (int i = 0; i < CHARACTER_ENCODINGS.length; i++) {
/* 190 */       if (CHARACTER_ENCODINGS[i] == pattern) {
/* 191 */         return ALPHABET[i];
/*     */       }
/*     */     } 
/* 194 */     throw NotFoundException.getNotFoundInstance();
/*     */   }
/*     */   
/*     */   private static String decodeExtended(CharSequence encoded) throws FormatException {
/* 198 */     int length = encoded.length();
/* 199 */     StringBuilder decoded = new StringBuilder(length);
/* 200 */     for (int i = 0; i < length; i++) {
/*     */       char c;
/* 202 */       if ((c = encoded.charAt(i)) >= 'a' && c <= 'd') {
/* 203 */         if (i >= length - 1) {
/* 204 */           throw FormatException.getFormatInstance();
/*     */         }
/* 206 */         char next = encoded.charAt(i + 1);
/* 207 */         char decodedChar = Character.MIN_VALUE;
/* 208 */         switch (c) {
/*     */           
/*     */           case 'd':
/* 211 */             if (next >= 'A' && next <= 'Z') {
/* 212 */               decodedChar = (char)(next + 32); break;
/*     */             } 
/* 214 */             throw FormatException.getFormatInstance();
/*     */ 
/*     */ 
/*     */           
/*     */           case 'a':
/* 219 */             if (next >= 'A' && next <= 'Z') {
/* 220 */               decodedChar = (char)(next - 64); break;
/*     */             } 
/* 222 */             throw FormatException.getFormatInstance();
/*     */ 
/*     */           
/*     */           case 'b':
/* 226 */             if (next >= 'A' && next <= 'E') {
/*     */               
/* 228 */               decodedChar = (char)(next - 38); break;
/* 229 */             }  if (next >= 'F' && next <= 'J') {
/*     */               
/* 231 */               decodedChar = (char)(next - 11); break;
/* 232 */             }  if (next >= 'K' && next <= 'O') {
/*     */               
/* 234 */               decodedChar = (char)(next + 16); break;
/* 235 */             }  if (next >= 'P' && next <= 'S') {
/*     */               
/* 237 */               decodedChar = (char)(next + 43); break;
/* 238 */             }  if (next >= 'T' && next <= 'Z') {
/*     */               
/* 240 */               decodedChar = ''; break;
/*     */             } 
/* 242 */             throw FormatException.getFormatInstance();
/*     */ 
/*     */ 
/*     */           
/*     */           case 'c':
/* 247 */             if (next >= 'A' && next <= 'O') {
/* 248 */               decodedChar = (char)(next - 32); break;
/* 249 */             }  if (next == 'Z') {
/* 250 */               decodedChar = ':'; break;
/*     */             } 
/* 252 */             throw FormatException.getFormatInstance();
/*     */         } 
/*     */ 
/*     */         
/* 256 */         decoded.append(decodedChar);
/*     */         
/* 258 */         i++;
/*     */       } else {
/* 260 */         decoded.append(c);
/*     */       } 
/*     */     } 
/* 263 */     return decoded.toString();
/*     */   }
/*     */   
/*     */   private static void checkChecksums(CharSequence result) throws ChecksumException {
/* 267 */     int length = result.length();
/* 268 */     checkOneChecksum(result, length - 2, 20);
/* 269 */     checkOneChecksum(result, length - 1, 15);
/*     */   }
/*     */ 
/*     */   
/*     */   private static void checkOneChecksum(CharSequence result, int checkPosition, int weightMax) throws ChecksumException {
/* 274 */     int weight = 1;
/* 275 */     int total = 0;
/* 276 */     for (int i = checkPosition - 1; i >= 0; i--) {
/* 277 */       total += weight * "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ-. $/+%abcd*".indexOf(result.charAt(i));
/* 278 */       if (++weight > weightMax) {
/* 279 */         weight = 1;
/*     */       }
/*     */     } 
/* 282 */     if (result.charAt(checkPosition) != ALPHABET[total % 47])
/* 283 */       throw ChecksumException.getChecksumInstance(); 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\zxing\oned\Code93Reader.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */