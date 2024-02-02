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
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
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
/*     */ public final class Code128Reader
/*     */   extends OneDReader
/*     */ {
/*  39 */   static final int[][] CODE_PATTERNS = new int[][] { { 2, 1, 2, 2, 2, 2 }, { 2, 2, 2, 1, 2, 2 }, { 2, 2, 2, 2, 2, 1 }, { 1, 2, 1, 2, 2, 3 }, { 1, 2, 1, 3, 2, 2 }, { 1, 3, 1, 2, 2, 2 }, { 1, 2, 2, 2, 1, 3 }, { 1, 2, 2, 3, 1, 2 }, { 1, 3, 2, 2, 1, 2 }, { 2, 2, 1, 2, 1, 3 }, { 2, 2, 1, 3, 1, 2 }, { 2, 3, 1, 2, 1, 2 }, { 1, 1, 2, 2, 3, 2 }, { 1, 2, 2, 1, 3, 2 }, { 1, 2, 2, 2, 3, 1 }, { 1, 1, 3, 2, 2, 2 }, { 1, 2, 3, 1, 2, 2 }, { 1, 2, 3, 2, 2, 1 }, { 2, 2, 3, 2, 1, 1 }, { 2, 2, 1, 1, 3, 2 }, { 2, 2, 1, 2, 3, 1 }, { 2, 1, 3, 2, 1, 2 }, { 2, 2, 3, 1, 1, 2 }, { 3, 1, 2, 1, 3, 1 }, { 3, 1, 1, 2, 2, 2 }, { 3, 2, 1, 1, 2, 2 }, { 3, 2, 1, 2, 2, 1 }, { 3, 1, 2, 2, 1, 2 }, { 3, 2, 2, 1, 1, 2 }, { 3, 2, 2, 2, 1, 1 }, { 2, 1, 2, 1, 2, 3 }, { 2, 1, 2, 3, 2, 1 }, { 2, 3, 2, 1, 2, 1 }, { 1, 1, 1, 3, 2, 3 }, { 1, 3, 1, 1, 2, 3 }, { 1, 3, 1, 3, 2, 1 }, { 1, 1, 2, 3, 1, 3 }, { 1, 3, 2, 1, 1, 3 }, { 1, 3, 2, 3, 1, 1 }, { 2, 1, 1, 3, 1, 3 }, { 2, 3, 1, 1, 1, 3 }, { 2, 3, 1, 3, 1, 1 }, { 1, 1, 2, 1, 3, 3 }, { 1, 1, 2, 3, 3, 1 }, { 1, 3, 2, 1, 3, 1 }, { 1, 1, 3, 1, 2, 3 }, { 1, 1, 3, 3, 2, 1 }, { 1, 3, 3, 1, 2, 1 }, { 3, 1, 3, 1, 2, 1 }, { 2, 1, 1, 3, 3, 1 }, { 2, 3, 1, 1, 3, 1 }, { 2, 1, 3, 1, 1, 3 }, { 2, 1, 3, 3, 1, 1 }, { 2, 1, 3, 1, 3, 1 }, { 3, 1, 1, 1, 2, 3 }, { 3, 1, 1, 3, 2, 1 }, { 3, 3, 1, 1, 2, 1 }, { 3, 1, 2, 1, 1, 3 }, { 3, 1, 2, 3, 1, 1 }, { 3, 3, 2, 1, 1, 1 }, { 3, 1, 4, 1, 1, 1 }, { 2, 2, 1, 4, 1, 1 }, { 4, 3, 1, 1, 1, 1 }, { 1, 1, 1, 2, 2, 4 }, { 1, 1, 1, 4, 2, 2 }, { 1, 2, 1, 1, 2, 4 }, { 1, 2, 1, 4, 2, 1 }, { 1, 4, 1, 1, 2, 2 }, { 1, 4, 1, 2, 2, 1 }, { 1, 1, 2, 2, 1, 4 }, { 1, 1, 2, 4, 1, 2 }, { 1, 2, 2, 1, 1, 4 }, { 1, 2, 2, 4, 1, 1 }, { 1, 4, 2, 1, 1, 2 }, { 1, 4, 2, 2, 1, 1 }, { 2, 4, 1, 2, 1, 1 }, { 2, 2, 1, 1, 1, 4 }, { 4, 1, 3, 1, 1, 1 }, { 2, 4, 1, 1, 1, 2 }, { 1, 3, 4, 1, 1, 1 }, { 1, 1, 1, 2, 4, 2 }, { 1, 2, 1, 1, 4, 2 }, { 1, 2, 1, 2, 4, 1 }, { 1, 1, 4, 2, 1, 2 }, { 1, 2, 4, 1, 1, 2 }, { 1, 2, 4, 2, 1, 1 }, { 4, 1, 1, 2, 1, 2 }, { 4, 2, 1, 1, 1, 2 }, { 4, 2, 1, 2, 1, 1 }, { 2, 1, 2, 1, 4, 1 }, { 2, 1, 4, 1, 2, 1 }, { 4, 1, 2, 1, 2, 1 }, { 1, 1, 1, 1, 4, 3 }, { 1, 1, 1, 3, 4, 1 }, { 1, 3, 1, 1, 4, 1 }, { 1, 1, 4, 1, 1, 3 }, { 1, 1, 4, 3, 1, 1 }, { 4, 1, 1, 1, 1, 3 }, { 4, 1, 1, 3, 1, 1 }, { 1, 1, 3, 1, 4, 1 }, { 1, 1, 4, 1, 3, 1 }, { 3, 1, 1, 1, 4, 1 }, { 4, 1, 1, 1, 3, 1 }, { 2, 1, 1, 4, 1, 2 }, { 2, 1, 1, 2, 1, 4 }, { 2, 1, 1, 2, 3, 2 }, { 2, 3, 3, 1, 1, 1, 2 } };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static final float MAX_AVG_VARIANCE = 0.25F;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static final float MAX_INDIVIDUAL_VARIANCE = 0.7F;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static final int CODE_SHIFT = 98;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static final int CODE_CODE_C = 99;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static final int CODE_CODE_B = 100;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static final int CODE_CODE_A = 101;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static final int CODE_FNC_1 = 102;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static final int CODE_FNC_2 = 97;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static final int CODE_FNC_3 = 96;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static final int CODE_FNC_4_A = 101;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static final int CODE_FNC_4_B = 100;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static final int CODE_START_A = 103;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static final int CODE_START_B = 104;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static final int CODE_START_C = 105;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static final int CODE_STOP = 106;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static int[] findStartPattern(BitArray row) throws NotFoundException {
/* 170 */     int width = row.getSize();
/* 171 */     int rowOffset = row.getNextSet(0);
/*     */     
/* 173 */     int counterPosition = 0;
/* 174 */     int[] counters = new int[6];
/* 175 */     int patternStart = rowOffset;
/* 176 */     boolean isWhite = false;
/*     */ 
/*     */     
/* 179 */     for (int i = rowOffset; i < width; i++) {
/* 180 */       if (row.get(i) ^ isWhite) {
/* 181 */         counters[counterPosition] = counters[counterPosition] + 1;
/*     */       } else {
/* 183 */         if (counterPosition == 5) {
/* 184 */           float bestVariance = 0.25F;
/* 185 */           int bestMatch = -1;
/* 186 */           for (int startCode = 103; startCode <= 105; startCode++) {
/*     */             float variance;
/*     */             
/* 189 */             if ((variance = patternMatchVariance(counters, CODE_PATTERNS[startCode], 0.7F)) < bestVariance) {
/* 190 */               bestVariance = variance;
/* 191 */               bestMatch = startCode;
/*     */             } 
/*     */           } 
/*     */           
/* 195 */           if (bestMatch >= 0 && row
/* 196 */             .isRange(Math.max(0, patternStart - (i - patternStart) / 2), patternStart, false)) {
/* 197 */             return new int[] { patternStart, i, bestMatch };
/*     */           }
/* 199 */           patternStart += counters[0] + counters[1];
/* 200 */           System.arraycopy(counters, 2, counters, 0, 4);
/* 201 */           counters[4] = 0;
/* 202 */           counters[5] = 0;
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
/*     */   private static int decodeCode(BitArray row, int[] counters, int rowOffset) throws NotFoundException {
/* 216 */     recordPattern(row, rowOffset, counters);
/* 217 */     float bestVariance = 0.25F;
/* 218 */     int bestMatch = -1;
/* 219 */     for (int d = 0; d < CODE_PATTERNS.length; d++) {
/* 220 */       int[] pattern = CODE_PATTERNS[d];
/*     */       float variance;
/* 222 */       if ((variance = patternMatchVariance(counters, pattern, 0.7F)) < bestVariance) {
/* 223 */         bestVariance = variance;
/* 224 */         bestMatch = d;
/*     */       } 
/*     */     } 
/*     */     
/* 228 */     if (bestMatch >= 0) {
/* 229 */       return bestMatch;
/*     */     }
/* 231 */     throw NotFoundException.getNotFoundInstance();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Result decodeRow(int rowNumber, BitArray row, Map<DecodeHintType, ?> hints) throws NotFoundException, FormatException, ChecksumException {
/*     */     int codeSet;
/* 239 */     boolean convertFNC1 = (hints != null && hints.containsKey(DecodeHintType.ASSUME_GS1));
/*     */ 
/*     */     
/* 242 */     int startPatternInfo[], startCode = (startPatternInfo = findStartPattern(row))[2];
/*     */     
/*     */     List<Byte> rawCodes;
/* 245 */     (rawCodes = new ArrayList<>(20)).add(Byte.valueOf((byte)startCode));
/*     */ 
/*     */     
/* 248 */     switch (startCode) {
/*     */       case 103:
/* 250 */         codeSet = 101;
/*     */         break;
/*     */       case 104:
/* 253 */         codeSet = 100;
/*     */         break;
/*     */       case 105:
/* 256 */         codeSet = 99;
/*     */         break;
/*     */       default:
/* 259 */         throw FormatException.getFormatInstance();
/*     */     } 
/*     */     
/* 262 */     boolean done = false;
/* 263 */     boolean isNextShifted = false;
/*     */     
/* 265 */     StringBuilder result = new StringBuilder(20);
/*     */     
/* 267 */     int lastStart = startPatternInfo[0];
/* 268 */     int nextStart = startPatternInfo[1];
/* 269 */     int[] counters = new int[6];
/*     */     
/* 271 */     int lastCode = 0;
/* 272 */     int code = 0;
/* 273 */     int checksumTotal = startCode;
/* 274 */     int multiplier = 0;
/* 275 */     boolean lastCharacterWasPrintable = true;
/* 276 */     boolean upperMode = false;
/* 277 */     boolean shiftUpperMode = false;
/*     */     
/* 279 */     while (!done) {
/*     */       
/* 281 */       boolean unshift = isNextShifted;
/* 282 */       isNextShifted = false;
/*     */ 
/*     */       
/* 285 */       lastCode = code;
/*     */ 
/*     */       
/* 288 */       code = decodeCode(row, counters, nextStart);
/*     */       
/* 290 */       rawCodes.add(Byte.valueOf((byte)code));
/*     */ 
/*     */       
/* 293 */       if (code != 106) {
/* 294 */         lastCharacterWasPrintable = true;
/*     */       }
/*     */ 
/*     */       
/* 298 */       if (code != 106) {
/* 299 */         multiplier++;
/* 300 */         checksumTotal += multiplier * code;
/*     */       } 
/*     */ 
/*     */       
/* 304 */       lastStart = nextStart; int[] arrayOfInt; byte b;
/* 305 */       for (arrayOfInt = counters, b = 0; b < 6; ) { int counter = arrayOfInt[b];
/* 306 */         nextStart += counter;
/*     */         
/*     */         b++; }
/*     */       
/* 310 */       switch (code) {
/*     */         case 103:
/*     */         case 104:
/*     */         case 105:
/* 314 */           throw FormatException.getFormatInstance();
/*     */       } 
/*     */       
/* 317 */       switch (codeSet) {
/*     */         
/*     */         case 101:
/* 320 */           if (code < 64) {
/* 321 */             if (shiftUpperMode == upperMode) {
/* 322 */               result.append((char)(code + 32));
/*     */             } else {
/* 324 */               result.append((char)(code + 32 + 128));
/*     */             } 
/* 326 */             shiftUpperMode = false; break;
/* 327 */           }  if (code < 96) {
/* 328 */             if (shiftUpperMode == upperMode) {
/* 329 */               result.append((char)(code - 64));
/*     */             } else {
/* 331 */               result.append((char)(code + 64));
/*     */             } 
/* 333 */             shiftUpperMode = false;
/*     */             
/*     */             break;
/*     */           } 
/* 337 */           if (code != 106) {
/* 338 */             lastCharacterWasPrintable = false;
/*     */           }
/* 340 */           switch (code) {
/*     */             case 102:
/* 342 */               if (convertFNC1) {
/* 343 */                 if (result.length() == 0) {
/*     */ 
/*     */                   
/* 346 */                   result.append("]C1");
/*     */                   break;
/*     */                 } 
/* 349 */                 result.append('\035');
/*     */               } 
/*     */               break;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */             
/*     */             case 101:
/* 358 */               if (!upperMode && shiftUpperMode) {
/* 359 */                 upperMode = true;
/* 360 */                 shiftUpperMode = false; break;
/* 361 */               }  if (upperMode && shiftUpperMode) {
/* 362 */                 upperMode = false;
/* 363 */                 shiftUpperMode = false; break;
/*     */               } 
/* 365 */               shiftUpperMode = true;
/*     */               break;
/*     */             
/*     */             case 98:
/* 369 */               isNextShifted = true;
/* 370 */               codeSet = 100;
/*     */               break;
/*     */             case 100:
/* 373 */               codeSet = 100;
/*     */               break;
/*     */             case 99:
/* 376 */               codeSet = 99;
/*     */               break;
/*     */             case 106:
/* 379 */               done = true;
/*     */               break;
/*     */           } 
/*     */           
/*     */           break;
/*     */         case 100:
/* 385 */           if (code < 96) {
/* 386 */             if (shiftUpperMode == upperMode) {
/* 387 */               result.append((char)(code + 32));
/*     */             } else {
/* 389 */               result.append((char)(code + 32 + 128));
/*     */             } 
/* 391 */             shiftUpperMode = false; break;
/*     */           } 
/* 393 */           if (code != 106) {
/* 394 */             lastCharacterWasPrintable = false;
/*     */           }
/* 396 */           switch (code) {
/*     */             case 102:
/* 398 */               if (convertFNC1) {
/* 399 */                 if (result.length() == 0) {
/*     */ 
/*     */                   
/* 402 */                   result.append("]C1");
/*     */                   break;
/*     */                 } 
/* 405 */                 result.append('\035');
/*     */               } 
/*     */               break;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */             
/*     */             case 100:
/* 414 */               if (!upperMode && shiftUpperMode) {
/* 415 */                 upperMode = true;
/* 416 */                 shiftUpperMode = false; break;
/* 417 */               }  if (upperMode && shiftUpperMode) {
/* 418 */                 upperMode = false;
/* 419 */                 shiftUpperMode = false; break;
/*     */               } 
/* 421 */               shiftUpperMode = true;
/*     */               break;
/*     */             
/*     */             case 98:
/* 425 */               isNextShifted = true;
/* 426 */               codeSet = 101;
/*     */               break;
/*     */             case 101:
/* 429 */               codeSet = 101;
/*     */               break;
/*     */             case 99:
/* 432 */               codeSet = 99;
/*     */               break;
/*     */             case 106:
/* 435 */               done = true;
/*     */               break;
/*     */           } 
/*     */           
/*     */           break;
/*     */         case 99:
/* 441 */           if (code < 100) {
/* 442 */             if (code < 10) {
/* 443 */               result.append('0');
/*     */             }
/* 445 */             result.append(code); break;
/*     */           } 
/* 447 */           if (code != 106) {
/* 448 */             lastCharacterWasPrintable = false;
/*     */           }
/* 450 */           switch (code) {
/*     */             case 102:
/* 452 */               if (convertFNC1) {
/* 453 */                 if (result.length() == 0) {
/*     */ 
/*     */                   
/* 456 */                   result.append("]C1");
/*     */                   break;
/*     */                 } 
/* 459 */                 result.append('\035');
/*     */               } 
/*     */               break;
/*     */             
/*     */             case 101:
/* 464 */               codeSet = 101;
/*     */               break;
/*     */             case 100:
/* 467 */               codeSet = 100;
/*     */               break;
/*     */             case 106:
/* 470 */               done = true;
/*     */               break;
/*     */           } 
/*     */ 
/*     */           
/*     */           break;
/*     */       } 
/*     */       
/* 478 */       if (unshift) {
/* 479 */         codeSet = (codeSet == 101) ? 100 : 101;
/*     */       }
/*     */     } 
/*     */ 
/*     */     
/* 484 */     int lastPatternSize = nextStart - lastStart;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 489 */     nextStart = row.getNextUnset(nextStart);
/* 490 */     if (!row.isRange(nextStart, 
/* 491 */         Math.min(row.getSize(), nextStart + (nextStart - lastStart) / 2), false))
/*     */     {
/* 493 */       throw NotFoundException.getNotFoundInstance();
/*     */     }
/*     */ 
/*     */     
/* 497 */     if ((checksumTotal - multiplier * lastCode) % 
/*     */       
/* 499 */       103 != lastCode) {
/* 500 */       throw ChecksumException.getChecksumInstance();
/*     */     }
/*     */     
/*     */     int resultLength;
/*     */     
/* 505 */     if ((resultLength = result.length()) == 0)
/*     */     {
/* 507 */       throw NotFoundException.getNotFoundInstance();
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 512 */     if (resultLength > 0 && lastCharacterWasPrintable) {
/* 513 */       if (codeSet == 99) {
/* 514 */         result.delete(resultLength - 2, resultLength);
/*     */       } else {
/* 516 */         result.delete(resultLength - 1, resultLength);
/*     */       } 
/*     */     }
/*     */     
/* 520 */     float left = (startPatternInfo[1] + startPatternInfo[0]) / 2.0F;
/* 521 */     float right = lastStart + lastPatternSize / 2.0F;
/*     */     
/*     */     int rawCodesSize;
/* 524 */     byte[] rawBytes = new byte[rawCodesSize = rawCodes.size()];
/* 525 */     for (int i = 0; i < rawCodesSize; i++) {
/* 526 */       rawBytes[i] = ((Byte)rawCodes.get(i)).byteValue();
/*     */     }
/*     */     
/* 529 */     return new Result(result
/* 530 */         .toString(), rawBytes, new ResultPoint[] { new ResultPoint(left, rowNumber), new ResultPoint(right, rowNumber) }BarcodeFormat.CODE_128);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\zxing\oned\Code128Reader.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */