/*     */ package com.google.zxing.oned;
/*     */ 
/*     */ import com.google.zxing.BarcodeFormat;
/*     */ import com.google.zxing.DecodeHintType;
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
/*     */ public final class CodaBarReader
/*     */   extends OneDReader
/*     */ {
/*     */   private static final float MAX_ACCEPTABLE = 2.0F;
/*     */   private static final float PADDING = 1.5F;
/*     */   private static final String ALPHABET_STRING = "0123456789-$:/.+ABCD";
/*  44 */   static final char[] ALPHABET = "0123456789-$:/.+ABCD".toCharArray();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  50 */   static final int[] CHARACTER_ENCODINGS = new int[] { 3, 6, 9, 96, 18, 66, 33, 36, 48, 72, 12, 24, 69, 81, 84, 21, 26, 41, 11, 14 };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static final int MIN_CHARACTER_LENGTH = 3;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  61 */   private static final char[] STARTEND_ENCODING = new char[] { 'A', 'B', 'C', 'D' };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  74 */   private final StringBuilder decodeRowResult = new StringBuilder(20);
/*  75 */   private int[] counters = new int[80];
/*  76 */   private int counterLength = 0;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Result decodeRow(int rowNumber, BitArray row, Map<DecodeHintType, ?> hints) throws NotFoundException {
/*  82 */     Arrays.fill(this.counters, 0);
/*  83 */     setCounters(row);
/*     */     
/*  85 */     int startOffset = findStartPattern(), nextStart = startOffset;
/*     */     
/*  87 */     this.decodeRowResult.setLength(0);
/*     */     while (true) {
/*     */       int charOffset;
/*  90 */       if ((charOffset = toNarrowWidePattern(nextStart)) == -1) {
/*  91 */         throw NotFoundException.getNotFoundInstance();
/*     */       }
/*     */ 
/*     */ 
/*     */       
/*  96 */       this.decodeRowResult.append((char)charOffset);
/*  97 */       nextStart += 8;
/*     */       
/*  99 */       if (this.decodeRowResult.length() <= 1 || 
/* 100 */         !arrayContains(STARTEND_ENCODING, ALPHABET[charOffset])) {
/*     */ 
/*     */         
/* 103 */         if (nextStart >= this.counterLength)
/*     */           break;  continue;
/*     */       }  break;
/* 106 */     }  int trailingWhitespace = this.counters[nextStart - 1];
/* 107 */     int lastPatternSize = 0; int i;
/* 108 */     for (i = -8; i < -1; i++) {
/* 109 */       lastPatternSize += this.counters[nextStart + i];
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 115 */     if (nextStart < this.counterLength && trailingWhitespace < lastPatternSize / 2) {
/* 116 */       throw NotFoundException.getNotFoundInstance();
/*     */     }
/*     */     
/* 119 */     validatePattern(startOffset);
/*     */ 
/*     */     
/* 122 */     for (i = 0; i < this.decodeRowResult.length(); i++) {
/* 123 */       this.decodeRowResult.setCharAt(i, ALPHABET[this.decodeRowResult.charAt(i)]);
/*     */     }
/*     */     
/* 126 */     char startchar = this.decodeRowResult.charAt(0);
/* 127 */     if (!arrayContains(STARTEND_ENCODING, startchar)) {
/* 128 */       throw NotFoundException.getNotFoundInstance();
/*     */     }
/* 130 */     char endchar = this.decodeRowResult.charAt(this.decodeRowResult.length() - 1);
/* 131 */     if (!arrayContains(STARTEND_ENCODING, endchar)) {
/* 132 */       throw NotFoundException.getNotFoundInstance();
/*     */     }
/*     */ 
/*     */     
/* 136 */     if (this.decodeRowResult.length() <= 3)
/*     */     {
/* 138 */       throw NotFoundException.getNotFoundInstance();
/*     */     }
/*     */     
/* 141 */     if (hints == null || !hints.containsKey(DecodeHintType.RETURN_CODABAR_START_END)) {
/* 142 */       this.decodeRowResult.deleteCharAt(this.decodeRowResult.length() - 1);
/* 143 */       this.decodeRowResult.deleteCharAt(0);
/*     */     } 
/*     */     
/* 146 */     int runningCount = 0;
/* 147 */     for (int j = 0; j < startOffset; j++) {
/* 148 */       runningCount += this.counters[j];
/*     */     }
/* 150 */     float left = runningCount;
/* 151 */     for (int k = startOffset; k < nextStart - 1; k++) {
/* 152 */       runningCount += this.counters[k];
/*     */     }
/* 154 */     float right = runningCount;
/* 155 */     return new Result(this.decodeRowResult
/* 156 */         .toString(), null, new ResultPoint[] { new ResultPoint(left, rowNumber), new ResultPoint(right, rowNumber) }BarcodeFormat.CODABAR);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void validatePattern(int start) throws NotFoundException {
/* 166 */     int[] sizes = { 0, 0, 0, 0 };
/* 167 */     int[] counts = { 0, 0, 0, 0 };
/* 168 */     int end = this.decodeRowResult.length() - 1;
/*     */ 
/*     */ 
/*     */     
/* 172 */     int pos = start;
/* 173 */     int i = 0; while (true) {
/* 174 */       int pattern = CHARACTER_ENCODINGS[this.decodeRowResult.charAt(i)];
/* 175 */       for (int k = 6; k >= 0; k--) {
/*     */ 
/*     */         
/* 178 */         int category = (k & 0x1) + ((pattern & 0x1) << 1);
/* 179 */         sizes[category] = sizes[category] + this.counters[pos + k];
/* 180 */         counts[category] = counts[category] + 1;
/* 181 */         pattern >>= 1;
/*     */       } 
/* 183 */       if (i < end) {
/*     */ 
/*     */ 
/*     */         
/* 187 */         pos += 8; i++;
/*     */       } 
/*     */       break;
/*     */     } 
/* 191 */     float[] maxes = new float[4];
/* 192 */     float[] mins = new float[4];
/*     */     
/*     */     int j;
/*     */     
/* 196 */     for (j = 0; j < 2; j++) {
/* 197 */       mins[j] = 0.0F;
/* 198 */       mins[j + 2] = (sizes[j] / counts[j] + sizes[j + 2] / counts[j + 2]) / 2.0F;
/* 199 */       maxes[j] = mins[j + 2];
/* 200 */       maxes[j + 2] = (sizes[j + 2] * 2.0F + 1.5F) / counts[j + 2];
/*     */     } 
/*     */ 
/*     */     
/* 204 */     pos = start;
/* 205 */     j = 0; while (true) {
/* 206 */       int pattern = CHARACTER_ENCODINGS[this.decodeRowResult.charAt(j)];
/* 207 */       for (int k = 6; k >= 0; k--) {
/*     */ 
/*     */         
/* 210 */         int category = (k & 0x1) + ((pattern & 0x1) << 1);
/*     */         int size;
/* 212 */         if ((size = this.counters[pos + k]) < mins[category] || size > maxes[category]) {
/* 213 */           throw NotFoundException.getNotFoundInstance();
/*     */         }
/* 215 */         pattern >>= 1;
/*     */       } 
/* 217 */       if (j < end) {
/*     */ 
/*     */         
/* 220 */         pos += 8;
/*     */         j++;
/*     */       } 
/*     */       break;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void setCounters(BitArray row) throws NotFoundException {
/* 231 */     this.counterLength = 0;
/*     */     
/* 233 */     int i = row.getNextUnset(0);
/* 234 */     int end = row.getSize();
/* 235 */     if (i >= end) {
/* 236 */       throw NotFoundException.getNotFoundInstance();
/*     */     }
/* 238 */     boolean isWhite = true;
/* 239 */     int count = 0;
/* 240 */     while (i < end) {
/* 241 */       if (row.get(i) ^ isWhite) {
/* 242 */         count++;
/*     */       } else {
/* 244 */         counterAppend(count);
/* 245 */         count = 1;
/* 246 */         isWhite = !isWhite;
/*     */       } 
/* 248 */       i++;
/*     */     } 
/* 250 */     counterAppend(count);
/*     */   }
/*     */   
/*     */   private void counterAppend(int e) {
/* 254 */     this.counters[this.counterLength] = e;
/* 255 */     this.counterLength++;
/* 256 */     if (this.counterLength >= this.counters.length) {
/* 257 */       int[] temp = new int[this.counterLength << 1];
/* 258 */       System.arraycopy(this.counters, 0, temp, 0, this.counterLength);
/* 259 */       this.counters = temp;
/*     */     } 
/*     */   }
/*     */   
/*     */   private int findStartPattern() throws NotFoundException {
/* 264 */     for (int i = 1; i < this.counterLength; i += 2) {
/*     */       int charOffset;
/* 266 */       if ((charOffset = toNarrowWidePattern(i)) != -1 && arrayContains(STARTEND_ENCODING, ALPHABET[charOffset])) {
/*     */ 
/*     */         
/* 269 */         int patternSize = 0;
/* 270 */         for (int j = i; j < i + 7; j++) {
/* 271 */           patternSize += this.counters[j];
/*     */         }
/* 273 */         if (i == 1 || this.counters[i - 1] >= patternSize / 2) {
/* 274 */           return i;
/*     */         }
/*     */       } 
/*     */     } 
/* 278 */     throw NotFoundException.getNotFoundInstance();
/*     */   }
/*     */   
/*     */   static boolean arrayContains(char[] array, char key) {
/* 282 */     if (array != null) {
/* 283 */       char[] arrayOfChar; int i; byte b; for (i = (arrayOfChar = array).length, b = 0; b < i; b++) { if (arrayOfChar[b] == 
/* 284 */           key) {
/* 285 */           return true;
/*     */         } }
/*     */     
/*     */     } 
/* 289 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   private int toNarrowWidePattern(int position) {
/*     */     int end;
/* 295 */     if ((end = position + 7) >= this.counterLength) {
/* 296 */       return -1;
/*     */     }
/*     */     
/* 299 */     int[] theCounters = this.counters;
/*     */     
/* 301 */     int maxBar = 0;
/* 302 */     int minBar = Integer.MAX_VALUE;
/* 303 */     for (int j = position; j < end; j += 2) {
/*     */       int currentCounter;
/* 305 */       if ((currentCounter = theCounters[j]) < minBar) {
/* 306 */         minBar = currentCounter;
/*     */       }
/* 308 */       if (currentCounter > maxBar) {
/* 309 */         maxBar = currentCounter;
/*     */       }
/*     */     } 
/* 312 */     int thresholdBar = (minBar + maxBar) / 2;
/*     */     
/* 314 */     int maxSpace = 0;
/* 315 */     int minSpace = Integer.MAX_VALUE;
/* 316 */     for (int k = position + 1; k < end; k += 2) {
/*     */       int currentCounter;
/* 318 */       if ((currentCounter = theCounters[k]) < minSpace) {
/* 319 */         minSpace = currentCounter;
/*     */       }
/* 321 */       if (currentCounter > maxSpace) {
/* 322 */         maxSpace = currentCounter;
/*     */       }
/*     */     } 
/* 325 */     int thresholdSpace = (minSpace + maxSpace) / 2;
/*     */     
/* 327 */     int bitmask = 128;
/* 328 */     int pattern = 0; int i;
/* 329 */     for (i = 0; i < 7; i++) {
/* 330 */       int threshold = ((i & 0x1) == 0) ? thresholdBar : thresholdSpace;
/* 331 */       bitmask >>= 1;
/* 332 */       if (theCounters[position + i] > threshold) {
/* 333 */         pattern |= bitmask;
/*     */       }
/*     */     } 
/*     */     
/* 337 */     for (i = 0; i < CHARACTER_ENCODINGS.length; i++) {
/* 338 */       if (CHARACTER_ENCODINGS[i] == pattern) {
/* 339 */         return i;
/*     */       }
/*     */     } 
/* 342 */     return -1;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\zxing\oned\CodaBarReader.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */