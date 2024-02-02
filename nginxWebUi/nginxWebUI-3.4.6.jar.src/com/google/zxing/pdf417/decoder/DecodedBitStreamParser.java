/*     */ package com.google.zxing.pdf417.decoder;
/*     */ 
/*     */ import com.google.zxing.FormatException;
/*     */ import com.google.zxing.common.CharacterSetECI;
/*     */ import com.google.zxing.common.DecoderResult;
/*     */ import com.google.zxing.pdf417.PDF417ResultMetadata;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.math.BigInteger;
/*     */ import java.nio.charset.Charset;
/*     */ import java.util.Arrays;
/*     */ 
/*     */ 
/*     */ 
/*     */ final class DecodedBitStreamParser
/*     */ {
/*     */   private static final int TEXT_COMPACTION_MODE_LATCH = 900;
/*     */   private static final int BYTE_COMPACTION_MODE_LATCH = 901;
/*     */   private static final int NUMERIC_COMPACTION_MODE_LATCH = 902;
/*     */   private static final int BYTE_COMPACTION_MODE_LATCH_6 = 924;
/*     */   private static final int ECI_USER_DEFINED = 925;
/*     */   private static final int ECI_GENERAL_PURPOSE = 926;
/*     */   private static final int ECI_CHARSET = 927;
/*     */   private static final int BEGIN_MACRO_PDF417_CONTROL_BLOCK = 928;
/*     */   private static final int BEGIN_MACRO_PDF417_OPTIONAL_FIELD = 923;
/*     */   private static final int MACRO_PDF417_TERMINATOR = 922;
/*     */   private static final int MODE_SHIFT_TO_BYTE_COMPACTION_MODE = 913;
/*     */   private static final int MAX_NUMERIC_CODEWORDS = 15;
/*     */   private static final int PL = 25;
/*     */   private static final int LL = 27;
/*     */   private static final int AS = 27;
/*     */   private static final int ML = 28;
/*     */   private static final int AL = 28;
/*     */   private static final int PS = 29;
/*     */   private static final int PAL = 29;
/*     */   
/*     */   private enum Mode
/*     */   {
/*  38 */     ALPHA,
/*  39 */     LOWER,
/*  40 */     MIXED,
/*  41 */     PUNCT,
/*  42 */     ALPHA_SHIFT,
/*  43 */     PUNCT_SHIFT;
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
/*  67 */   private static final char[] PUNCT_CHARS = ";<>@[\\]_`~!\r\t,:\n-.$/\"|*()?{}'"
/*  68 */     .toCharArray();
/*     */   
/*  70 */   private static final char[] MIXED_CHARS = "0123456789&\r\t,:#-.$/+%*=^"
/*  71 */     .toCharArray();
/*     */   
/*  73 */   private static final Charset DEFAULT_ENCODING = Charset.forName("ISO-8859-1");
/*     */ 
/*     */   
/*     */   private static final BigInteger[] EXP900;
/*     */   
/*     */   private static final int NUMBER_OF_SEQUENCE_CODEWORDS = 2;
/*     */ 
/*     */   
/*     */   static {
/*  82 */     (EXP900 = new BigInteger[16])[0] = BigInteger.ONE;
/*  83 */     BigInteger nineHundred = BigInteger.valueOf(900L);
/*  84 */     EXP900[1] = nineHundred;
/*  85 */     for (int i = 2; i < EXP900.length; i++) {
/*  86 */       EXP900[i] = EXP900[i - 1].multiply(nineHundred);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static DecoderResult decode(int[] codewords, String ecLevel) throws FormatException {
/*  96 */     StringBuilder result = new StringBuilder(codewords.length << 1);
/*  97 */     Charset encoding = DEFAULT_ENCODING;
/*     */     
/*  99 */     int codeIndex = 1;
/* 100 */     codeIndex++; int code = codewords[1];
/* 101 */     PDF417ResultMetadata resultMetadata = new PDF417ResultMetadata();
/* 102 */     while (codeIndex < codewords[0]) {
/* 103 */       switch (code) {
/*     */         case 900:
/* 105 */           codeIndex = textCompaction(codewords, codeIndex, result);
/*     */           break;
/*     */         case 901:
/*     */         case 924:
/* 109 */           codeIndex = byteCompaction(code, codewords, encoding, codeIndex, result);
/*     */           break;
/*     */         case 913:
/* 112 */           result.append((char)codewords[codeIndex++]);
/*     */           break;
/*     */         case 902:
/* 115 */           codeIndex = numericCompaction(codewords, codeIndex, result);
/*     */           break;
/*     */ 
/*     */         
/*     */         case 927:
/* 120 */           encoding = Charset.forName(CharacterSetECI.getCharacterSetECIByValue(codewords[codeIndex++]).name());
/*     */           break;
/*     */         
/*     */         case 926:
/* 124 */           codeIndex += 2;
/*     */           break;
/*     */         
/*     */         case 925:
/* 128 */           codeIndex++;
/*     */           break;
/*     */         case 928:
/* 131 */           codeIndex = decodeMacroBlock(codewords, codeIndex, resultMetadata);
/*     */           break;
/*     */         
/*     */         case 922:
/*     */         case 923:
/* 136 */           throw FormatException.getFormatInstance();
/*     */ 
/*     */ 
/*     */         
/*     */         default:
/* 141 */           codeIndex--;
/* 142 */           codeIndex = textCompaction(codewords, codeIndex, result);
/*     */           break;
/*     */       } 
/* 145 */       if (codeIndex < codewords.length) {
/* 146 */         code = codewords[codeIndex++]; continue;
/*     */       } 
/* 148 */       throw FormatException.getFormatInstance();
/*     */     } 
/*     */     
/* 151 */     if (result.length() == 0) {
/* 152 */       throw FormatException.getFormatInstance();
/*     */     }
/*     */     DecoderResult decoderResult;
/* 155 */     (decoderResult = new DecoderResult(null, result.toString(), null, ecLevel)).setOther(resultMetadata);
/* 156 */     return decoderResult;
/*     */   }
/*     */ 
/*     */   
/*     */   private static int decodeMacroBlock(int[] codewords, int codeIndex, PDF417ResultMetadata resultMetadata) throws FormatException {
/* 161 */     if (codeIndex + 2 > codewords[0])
/*     */     {
/* 163 */       throw FormatException.getFormatInstance();
/*     */     }
/* 165 */     int[] segmentIndexArray = new int[2];
/* 166 */     for (int i = 0; i < 2; i++, codeIndex++) {
/* 167 */       segmentIndexArray[i] = codewords[codeIndex];
/*     */     }
/* 169 */     resultMetadata.setSegmentIndex(Integer.parseInt(decodeBase900toBase10(segmentIndexArray, 2)));
/*     */ 
/*     */     
/* 172 */     StringBuilder fileId = new StringBuilder();
/* 173 */     codeIndex = textCompaction(codewords, codeIndex, fileId);
/* 174 */     resultMetadata.setFileId(fileId.toString());
/*     */     
/* 176 */     if (codewords[codeIndex] == 923) {
/* 177 */       codeIndex++;
/* 178 */       int[] additionalOptionCodeWords = new int[codewords[0] - codeIndex];
/* 179 */       int additionalOptionCodeWordsIndex = 0;
/*     */       
/* 181 */       boolean end = false;
/* 182 */       while (codeIndex < codewords[0] && !end) {
/*     */         int code;
/* 184 */         if ((code = codewords[codeIndex++]) < 900) {
/* 185 */           additionalOptionCodeWords[additionalOptionCodeWordsIndex++] = code; continue;
/*     */         } 
/* 187 */         switch (code) {
/*     */           case 922:
/* 189 */             resultMetadata.setLastSegment(true);
/* 190 */             codeIndex++;
/* 191 */             end = true;
/*     */             continue;
/*     */         } 
/* 194 */         throw FormatException.getFormatInstance();
/*     */       } 
/*     */ 
/*     */ 
/*     */       
/* 199 */       resultMetadata.setOptionalData(Arrays.copyOf(additionalOptionCodeWords, additionalOptionCodeWordsIndex));
/* 200 */     } else if (codewords[codeIndex] == 922) {
/* 201 */       resultMetadata.setLastSegment(true);
/* 202 */       codeIndex++;
/*     */     } 
/*     */     
/* 205 */     return codeIndex;
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
/*     */   private static int textCompaction(int[] codewords, int codeIndex, StringBuilder result) {
/* 220 */     int[] textCompactionData = new int[codewords[0] - codeIndex << 1];
/*     */     
/* 222 */     int[] byteCompactionData = new int[codewords[0] - codeIndex << 1];
/*     */     
/* 224 */     int index = 0;
/* 225 */     boolean end = false;
/* 226 */     while (codeIndex < codewords[0] && !end) {
/*     */       int code;
/* 228 */       if ((code = codewords[codeIndex++]) < 900) {
/* 229 */         textCompactionData[index] = code / 30;
/* 230 */         textCompactionData[index + 1] = code % 30;
/* 231 */         index += 2; continue;
/*     */       } 
/* 233 */       switch (code) {
/*     */         
/*     */         case 900:
/* 236 */           textCompactionData[index++] = 900;
/*     */         
/*     */         case 901:
/*     */         case 902:
/*     */         case 922:
/*     */         case 923:
/*     */         case 924:
/*     */         case 928:
/* 244 */           codeIndex--;
/* 245 */           end = true;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/*     */         case 913:
/* 254 */           textCompactionData[index] = 913;
/* 255 */           code = codewords[codeIndex++];
/* 256 */           byteCompactionData[index] = code;
/* 257 */           index++;
/*     */       } 
/*     */ 
/*     */     
/*     */     } 
/* 262 */     decodeTextCompaction(textCompactionData, byteCompactionData, index, result);
/* 263 */     return codeIndex;
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
/*     */   private static void decodeTextCompaction(int[] textCompactionData, int[] byteCompactionData, int length, StringBuilder result) {
/* 290 */     Mode subMode = Mode.ALPHA;
/* 291 */     Mode priorToShiftMode = Mode.ALPHA;
/* 292 */     int i = 0;
/* 293 */     while (i < length) {
/* 294 */       int subModeCh = textCompactionData[i];
/* 295 */       char ch = Character.MIN_VALUE;
/* 296 */       switch (subMode) {
/*     */         
/*     */         case ALPHA:
/* 299 */           if (subModeCh < 26) {
/*     */             
/* 301 */             ch = (char)(subModeCh + 65); break;
/*     */           } 
/* 303 */           if (subModeCh == 26) {
/* 304 */             ch = ' '; break;
/* 305 */           }  if (subModeCh == 27) {
/* 306 */             subMode = Mode.LOWER; break;
/* 307 */           }  if (subModeCh == 28) {
/* 308 */             subMode = Mode.MIXED; break;
/* 309 */           }  if (subModeCh == 29) {
/*     */             
/* 311 */             priorToShiftMode = subMode;
/* 312 */             subMode = Mode.PUNCT_SHIFT; break;
/* 313 */           }  if (subModeCh == 913) {
/* 314 */             result.append((char)byteCompactionData[i]); break;
/* 315 */           }  if (subModeCh == 900) {
/* 316 */             subMode = Mode.ALPHA;
/*     */           }
/*     */           break;
/*     */ 
/*     */ 
/*     */         
/*     */         case LOWER:
/* 323 */           if (subModeCh < 26) {
/* 324 */             ch = (char)(subModeCh + 97); break;
/*     */           } 
/* 326 */           if (subModeCh == 26) {
/* 327 */             ch = ' '; break;
/* 328 */           }  if (subModeCh == 27) {
/*     */             
/* 330 */             priorToShiftMode = subMode;
/* 331 */             subMode = Mode.ALPHA_SHIFT; break;
/* 332 */           }  if (subModeCh == 28) {
/* 333 */             subMode = Mode.MIXED; break;
/* 334 */           }  if (subModeCh == 29) {
/*     */             
/* 336 */             priorToShiftMode = subMode;
/* 337 */             subMode = Mode.PUNCT_SHIFT; break;
/* 338 */           }  if (subModeCh == 913) {
/*     */             
/* 340 */             result.append((char)byteCompactionData[i]); break;
/* 341 */           }  if (subModeCh == 900) {
/* 342 */             subMode = Mode.ALPHA;
/*     */           }
/*     */           break;
/*     */ 
/*     */ 
/*     */         
/*     */         case MIXED:
/* 349 */           if (subModeCh < 25) {
/* 350 */             ch = MIXED_CHARS[subModeCh]; break;
/*     */           } 
/* 352 */           if (subModeCh == 25) {
/* 353 */             subMode = Mode.PUNCT; break;
/* 354 */           }  if (subModeCh == 26) {
/* 355 */             ch = ' '; break;
/* 356 */           }  if (subModeCh == 27) {
/* 357 */             subMode = Mode.LOWER; break;
/* 358 */           }  if (subModeCh == 28) {
/* 359 */             subMode = Mode.ALPHA; break;
/* 360 */           }  if (subModeCh == 29) {
/*     */             
/* 362 */             priorToShiftMode = subMode;
/* 363 */             subMode = Mode.PUNCT_SHIFT; break;
/* 364 */           }  if (subModeCh == 913) {
/* 365 */             result.append((char)byteCompactionData[i]); break;
/* 366 */           }  if (subModeCh == 900) {
/* 367 */             subMode = Mode.ALPHA;
/*     */           }
/*     */           break;
/*     */ 
/*     */ 
/*     */         
/*     */         case PUNCT:
/* 374 */           if (subModeCh < 29) {
/* 375 */             ch = PUNCT_CHARS[subModeCh]; break;
/*     */           } 
/* 377 */           if (subModeCh == 29) {
/* 378 */             subMode = Mode.ALPHA; break;
/* 379 */           }  if (subModeCh == 913) {
/* 380 */             result.append((char)byteCompactionData[i]); break;
/* 381 */           }  if (subModeCh == 900) {
/* 382 */             subMode = Mode.ALPHA;
/*     */           }
/*     */           break;
/*     */ 
/*     */ 
/*     */         
/*     */         case ALPHA_SHIFT:
/* 389 */           subMode = priorToShiftMode;
/* 390 */           if (subModeCh < 26) {
/* 391 */             ch = (char)(subModeCh + 65); break;
/*     */           } 
/* 393 */           if (subModeCh == 26) {
/* 394 */             ch = ' '; break;
/* 395 */           }  if (subModeCh == 900) {
/* 396 */             subMode = Mode.ALPHA;
/*     */           }
/*     */           break;
/*     */ 
/*     */ 
/*     */         
/*     */         case PUNCT_SHIFT:
/* 403 */           subMode = priorToShiftMode;
/* 404 */           if (subModeCh < 29) {
/* 405 */             ch = PUNCT_CHARS[subModeCh]; break;
/*     */           } 
/* 407 */           if (subModeCh == 29) {
/* 408 */             subMode = Mode.ALPHA; break;
/* 409 */           }  if (subModeCh == 913) {
/*     */ 
/*     */             
/* 412 */             result.append((char)byteCompactionData[i]); break;
/* 413 */           }  if (subModeCh == 900) {
/* 414 */             subMode = Mode.ALPHA;
/*     */           }
/*     */           break;
/*     */       } 
/*     */       
/* 419 */       if (ch != '\000')
/*     */       {
/* 421 */         result.append(ch);
/*     */       }
/* 423 */       i++;
/*     */     } 
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static int byteCompaction(int mode, int[] codewords, Charset encoding, int codeIndex, StringBuilder result) {
/* 444 */     ByteArrayOutputStream decodedBytes = new ByteArrayOutputStream();
/* 445 */     if (mode == 901) {
/*     */ 
/*     */       
/* 448 */       int count = 0;
/* 449 */       long value = 0L;
/* 450 */       int[] byteCompactedCodewords = new int[6];
/* 451 */       boolean end = false;
/* 452 */       int nextCode = codewords[codeIndex++];
/* 453 */       while (codeIndex < codewords[0] && !end) {
/* 454 */         byteCompactedCodewords[count++] = nextCode;
/*     */         
/* 456 */         value = 900L * value + nextCode;
/*     */ 
/*     */         
/* 459 */         if ((nextCode = codewords[codeIndex++]) == 900 || nextCode == 901 || nextCode == 902 || nextCode == 924 || nextCode == 928 || nextCode == 923 || nextCode == 922) {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */           
/* 466 */           codeIndex--;
/* 467 */           end = true; continue;
/*     */         } 
/* 469 */         if (count % 5 == 0 && count > 0) {
/*     */ 
/*     */           
/* 472 */           for (int j = 0; j < 6; j++) {
/* 473 */             decodedBytes.write((byte)(int)(value >> 8 * (5 - j)));
/*     */           }
/* 475 */           value = 0L;
/* 476 */           count = 0;
/*     */         } 
/*     */       } 
/*     */ 
/*     */ 
/*     */       
/* 482 */       if (codeIndex == codewords[0] && nextCode < 900) {
/* 483 */         byteCompactedCodewords[count++] = nextCode;
/*     */       }
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 489 */       for (int i = 0; i < count; i++) {
/* 490 */         decodedBytes.write((byte)byteCompactedCodewords[i]);
/*     */       }
/*     */     }
/* 493 */     else if (mode == 924) {
/*     */ 
/*     */       
/* 496 */       int count = 0;
/* 497 */       long value = 0L;
/* 498 */       boolean end = false;
/* 499 */       while (codeIndex < codewords[0] && !end) {
/*     */         int code;
/* 501 */         if ((code = codewords[codeIndex++]) < 900) {
/* 502 */           count++;
/*     */           
/* 504 */           value = 900L * value + code;
/*     */         }
/* 506 */         else if (code == 900 || code == 901 || code == 902 || code == 924 || code == 928 || code == 923 || code == 922) {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */           
/* 513 */           codeIndex--;
/* 514 */           end = true;
/*     */         } 
/*     */         
/* 517 */         if (count % 5 == 0 && count > 0) {
/*     */ 
/*     */           
/* 520 */           for (int j = 0; j < 6; j++) {
/* 521 */             decodedBytes.write((byte)(int)(value >> 8 * (5 - j)));
/*     */           }
/* 523 */           value = 0L;
/* 524 */           count = 0;
/*     */         } 
/*     */       } 
/*     */     } 
/* 528 */     result.append(new String(decodedBytes.toByteArray(), encoding));
/* 529 */     return codeIndex;
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
/*     */   private static int numericCompaction(int[] codewords, int codeIndex, StringBuilder result) throws FormatException {
/* 541 */     int count = 0;
/* 542 */     boolean end = false;
/*     */     
/* 544 */     int[] numericCodewords = new int[15];
/*     */     
/* 546 */     while (codeIndex < codewords[0] && !end) {
/* 547 */       int code = codewords[codeIndex++];
/* 548 */       if (codeIndex == codewords[0]) {
/* 549 */         end = true;
/*     */       }
/* 551 */       if (code < 900) {
/* 552 */         numericCodewords[count] = code;
/* 553 */         count++;
/*     */       }
/* 555 */       else if (code == 900 || code == 901 || code == 924 || code == 928 || code == 923 || code == 922) {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 561 */         codeIndex--;
/* 562 */         end = true;
/*     */       } 
/*     */       
/* 565 */       if (count % 15 == 0 || code == 902 || end)
/*     */       {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 572 */         if (count > 0) {
/* 573 */           String s = decodeBase900toBase10(numericCodewords, count);
/* 574 */           result.append(s);
/* 575 */           count = 0;
/*     */         } 
/*     */       }
/*     */     } 
/* 579 */     return codeIndex;
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
/*     */   private static String decodeBase900toBase10(int[] codewords, int count) throws FormatException {
/* 626 */     BigInteger result = BigInteger.ZERO;
/* 627 */     for (int i = 0; i < count; i++) {
/* 628 */       result = result.add(EXP900[count - i - 1].multiply(BigInteger.valueOf(codewords[i])));
/*     */     }
/*     */     String resultString;
/* 631 */     if ((resultString = result.toString()).charAt(0) != '1') {
/* 632 */       throw FormatException.getFormatInstance();
/*     */     }
/* 634 */     return resultString.substring(1);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\zxing\pdf417\decoder\DecodedBitStreamParser.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */