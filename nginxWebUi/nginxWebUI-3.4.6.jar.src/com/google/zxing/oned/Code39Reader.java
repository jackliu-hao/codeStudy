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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class Code39Reader
/*     */   extends OneDReader
/*     */ {
/*     */   static final String ALPHABET_STRING = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ-. *$/+%";
/*     */   private static final String CHECK_DIGIT_STRING = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ-. $/+%";
/*  48 */   static final int[] CHARACTER_ENCODINGS = new int[] { 52, 289, 97, 352, 49, 304, 112, 37, 292, 100, 265, 73, 328, 25, 280, 88, 13, 268, 76, 28, 259, 67, 322, 19, 274, 82, 7, 262, 70, 22, 385, 193, 448, 145, 400, 208, 133, 388, 196, 148, 168, 162, 138, 42 }; static final int ASTERISK_ENCODING = (new int[44])[
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/*  56 */       39];
/*     */   
/*     */   private final boolean usingCheckDigit;
/*     */   
/*     */   private final boolean extendedMode;
/*     */   
/*     */   private final StringBuilder decodeRowResult;
/*     */   
/*     */   private final int[] counters;
/*     */ 
/*     */   
/*     */   public Code39Reader() {
/*  68 */     this(false);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Code39Reader(boolean usingCheckDigit) {
/*  79 */     this(usingCheckDigit, false);
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
/*     */   public Code39Reader(boolean usingCheckDigit, boolean extendedMode) {
/*  93 */     this.usingCheckDigit = usingCheckDigit;
/*  94 */     this.extendedMode = extendedMode;
/*  95 */     this.decodeRowResult = new StringBuilder(20);
/*  96 */     this.counters = new int[9];
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Result decodeRow(int rowNumber, BitArray row, Map<DecodeHintType, ?> hints) throws NotFoundException, ChecksumException, FormatException {
/*     */     int[] theCounters;
/* 104 */     Arrays.fill(theCounters = this.counters, 0);
/*     */     StringBuilder result;
/* 106 */     (result = this.decodeRowResult).setLength(0);
/*     */     
/* 108 */     int[] start = findAsteriskPattern(row, theCounters);
/*     */     
/* 110 */     int nextStart = row.getNextSet(start[1]);
/* 111 */     int end = row.getSize();
/*     */ 
/*     */ 
/*     */     
/*     */     while (true) {
/* 116 */       recordPattern(row, nextStart, theCounters);
/*     */       int pattern;
/* 118 */       if ((pattern = toNarrowWidePattern(theCounters)) < 0) {
/* 119 */         throw NotFoundException.getNotFoundInstance();
/*     */       }
/* 121 */       char decodedChar = patternToChar(pattern);
/* 122 */       result.append(decodedChar);
/* 123 */       int lastStart = nextStart; int arrayOfInt[], i; byte b;
/* 124 */       for (i = (arrayOfInt = theCounters).length, b = 0; b < i; ) { int counter = arrayOfInt[b];
/* 125 */         nextStart += counter;
/*     */         b++; }
/*     */       
/* 128 */       nextStart = row.getNextSet(nextStart);
/* 129 */       if (decodedChar == '*') {
/* 130 */         String resultString; result.setLength(result.length() - 1);
/*     */ 
/*     */         
/* 133 */         int lastPatternSize = 0;
/* 134 */         for (i = (arrayOfInt = theCounters).length, b = 0; b < i; ) { int counter = arrayOfInt[b];
/* 135 */           lastPatternSize += counter; b++; }
/*     */         
/* 137 */         int whiteSpaceAfterEnd = nextStart - lastStart - lastPatternSize;
/*     */ 
/*     */         
/* 140 */         if (nextStart != end && whiteSpaceAfterEnd << 1 < lastPatternSize) {
/* 141 */           throw NotFoundException.getNotFoundInstance();
/*     */         }
/*     */         
/* 144 */         if (this.usingCheckDigit) {
/* 145 */           int max = result.length() - 1;
/* 146 */           int total = 0;
/* 147 */           for (int j = 0; j < max; j++) {
/* 148 */             total += "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ-. $/+%".indexOf(this.decodeRowResult.charAt(j));
/*     */           }
/* 150 */           if (result.charAt(max) != "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ-. $/+%".charAt(total % 43)) {
/* 151 */             throw ChecksumException.getChecksumInstance();
/*     */           }
/* 153 */           result.setLength(max);
/*     */         } 
/*     */         
/* 156 */         if (result.length() == 0)
/*     */         {
/* 158 */           throw NotFoundException.getNotFoundInstance();
/*     */         }
/*     */ 
/*     */         
/* 162 */         if (this.extendedMode) {
/* 163 */           resultString = decodeExtended(result);
/*     */         } else {
/* 165 */           resultString = result.toString();
/*     */         } 
/*     */         
/* 168 */         float left = (start[1] + start[0]) / 2.0F;
/* 169 */         float right = lastStart + lastPatternSize / 2.0F;
/* 170 */         return new Result(resultString, null, new ResultPoint[] { new ResultPoint(left, rowNumber), new ResultPoint(right, rowNumber) }BarcodeFormat.CODE_39);
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static int[] findAsteriskPattern(BitArray row, int[] counters) throws NotFoundException {
/* 181 */     int width = row.getSize();
/* 182 */     int rowOffset = row.getNextSet(0);
/*     */     
/* 184 */     int counterPosition = 0;
/* 185 */     int patternStart = rowOffset;
/* 186 */     boolean isWhite = false;
/* 187 */     int patternLength = counters.length;
/*     */     
/* 189 */     for (int i = rowOffset; i < width; i++) {
/* 190 */       if (row.get(i) ^ isWhite) {
/* 191 */         counters[counterPosition] = counters[counterPosition] + 1;
/*     */       } else {
/* 193 */         if (counterPosition == patternLength - 1) {
/*     */           
/* 195 */           if (toNarrowWidePattern(counters) == ASTERISK_ENCODING && row
/* 196 */             .isRange(Math.max(0, patternStart - (i - patternStart) / 2), patternStart, false)) {
/* 197 */             return new int[] { patternStart, i };
/*     */           }
/* 199 */           patternStart += counters[0] + counters[1];
/* 200 */           System.arraycopy(counters, 2, counters, 0, patternLength - 2);
/* 201 */           counters[patternLength - 2] = 0;
/* 202 */           counters[patternLength - 1] = 0;
/* 203 */           counterPosition--;
/*     */         } else {
/* 205 */           counterPosition++;
/*     */         } 
/* 207 */         counters[counterPosition] = 1;
/* 208 */         isWhite = !isWhite;
/*     */       } 
/*     */     } 
/* 211 */     throw NotFoundException.getNotFoundInstance();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static int toNarrowWidePattern(int[] counters) {
/* 217 */     int numCounters = counters.length;
/* 218 */     int maxNarrowCounter = 0;
/*     */     
/*     */     while (true) {
/* 221 */       int minCounter = Integer.MAX_VALUE; int arrayOfInt[], j; byte b;
/* 222 */       for (j = (arrayOfInt = counters).length, b = 0; b < j; ) {
/* 223 */         int counter; if ((counter = arrayOfInt[b]) < minCounter && counter > maxNarrowCounter)
/* 224 */           minCounter = counter; 
/*     */         b++;
/*     */       } 
/* 227 */       maxNarrowCounter = minCounter;
/* 228 */       int wideCounters = 0;
/* 229 */       int totalWideCountersWidth = 0;
/* 230 */       int pattern = 0; int i;
/* 231 */       for (i = 0; i < numCounters; i++) {
/*     */         int counter;
/* 233 */         if ((counter = counters[i]) > maxNarrowCounter) {
/* 234 */           pattern |= 1 << numCounters - 1 - i;
/* 235 */           wideCounters++;
/* 236 */           totalWideCountersWidth += counter;
/*     */         } 
/*     */       } 
/* 239 */       if (wideCounters == 3) {
/*     */ 
/*     */ 
/*     */         
/* 243 */         for (i = 0; i < numCounters && wideCounters > 0; i++) {
/*     */           int counter;
/* 245 */           if ((counter = counters[i]) > maxNarrowCounter) {
/* 246 */             wideCounters--;
/*     */             
/* 248 */             if (counter << 1 >= totalWideCountersWidth) {
/* 249 */               return -1;
/*     */             }
/*     */           } 
/*     */         } 
/* 253 */         return pattern;
/*     */       } 
/* 255 */       if (wideCounters <= 3)
/* 256 */         return -1; 
/*     */     } 
/*     */   }
/*     */   private static char patternToChar(int pattern) throws NotFoundException {
/* 260 */     for (int i = 0; i < CHARACTER_ENCODINGS.length; i++) {
/* 261 */       if (CHARACTER_ENCODINGS[i] == pattern) {
/* 262 */         return "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ-. *$/+%".charAt(i);
/*     */       }
/*     */     } 
/* 265 */     throw NotFoundException.getNotFoundInstance();
/*     */   }
/*     */   
/*     */   private static String decodeExtended(CharSequence encoded) throws FormatException {
/* 269 */     int length = encoded.length();
/* 270 */     StringBuilder decoded = new StringBuilder(length);
/* 271 */     for (int i = 0; i < length; i++) {
/*     */       char c;
/* 273 */       if ((c = encoded.charAt(i)) == '+' || c == '$' || c == '%' || c == '/') {
/* 274 */         char next = encoded.charAt(i + 1);
/* 275 */         char decodedChar = Character.MIN_VALUE;
/* 276 */         switch (c) {
/*     */           
/*     */           case '+':
/* 279 */             if (next >= 'A' && next <= 'Z') {
/* 280 */               decodedChar = (char)(next + 32); break;
/*     */             } 
/* 282 */             throw FormatException.getFormatInstance();
/*     */ 
/*     */ 
/*     */           
/*     */           case '$':
/* 287 */             if (next >= 'A' && next <= 'Z') {
/* 288 */               decodedChar = (char)(next - 64); break;
/*     */             } 
/* 290 */             throw FormatException.getFormatInstance();
/*     */ 
/*     */ 
/*     */           
/*     */           case '%':
/* 295 */             if (next >= 'A' && next <= 'E') {
/* 296 */               decodedChar = (char)(next - 38); break;
/* 297 */             }  if (next >= 'F' && next <= 'W') {
/* 298 */               decodedChar = (char)(next - 11); break;
/*     */             } 
/* 300 */             throw FormatException.getFormatInstance();
/*     */ 
/*     */ 
/*     */           
/*     */           case '/':
/* 305 */             if (next >= 'A' && next <= 'O') {
/* 306 */               decodedChar = (char)(next - 32); break;
/* 307 */             }  if (next == 'Z') {
/* 308 */               decodedChar = ':'; break;
/*     */             } 
/* 310 */             throw FormatException.getFormatInstance();
/*     */         } 
/*     */ 
/*     */         
/* 314 */         decoded.append(decodedChar);
/*     */         
/* 316 */         i++;
/*     */       } else {
/* 318 */         decoded.append(c);
/*     */       } 
/*     */     } 
/* 321 */     return decoded.toString();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\zxing\oned\Code39Reader.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */