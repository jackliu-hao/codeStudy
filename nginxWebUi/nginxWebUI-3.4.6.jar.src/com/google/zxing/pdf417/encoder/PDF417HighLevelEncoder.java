/*     */ package com.google.zxing.pdf417.encoder;
/*     */ 
/*     */ import com.google.zxing.WriterException;
/*     */ import com.google.zxing.common.CharacterSetECI;
/*     */ import java.math.BigInteger;
/*     */ import java.nio.charset.Charset;
/*     */ import java.nio.charset.CharsetEncoder;
/*     */ import java.util.Arrays;
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
/*     */ final class PDF417HighLevelEncoder
/*     */ {
/*     */   private static final int TEXT_COMPACTION = 0;
/*     */   private static final int BYTE_COMPACTION = 1;
/*     */   private static final int NUMERIC_COMPACTION = 2;
/*     */   private static final int SUBMODE_ALPHA = 0;
/*     */   private static final int SUBMODE_LOWER = 1;
/*     */   private static final int SUBMODE_MIXED = 2;
/*     */   private static final int SUBMODE_PUNCTUATION = 3;
/*     */   private static final int LATCH_TO_TEXT = 900;
/*     */   private static final int LATCH_TO_BYTE_PADDED = 901;
/*     */   private static final int LATCH_TO_NUMERIC = 902;
/*     */   private static final int SHIFT_TO_BYTE = 913;
/*     */   private static final int LATCH_TO_BYTE = 924;
/*     */   private static final int ECI_USER_DEFINED = 925;
/*     */   private static final int ECI_GENERAL_PURPOSE = 926;
/*     */   private static final int ECI_CHARSET = 927;
/* 115 */   private static final byte[] TEXT_MIXED_RAW = new byte[] { 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 38, 13, 9, 44, 58, 35, 45, 46, 36, 47, 43, 37, 42, 61, 94, 0, 32, 0, 0, 0 };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 122 */   private static final byte[] TEXT_PUNCTUATION_RAW = new byte[] { 59, 60, 62, 64, 91, 92, 93, 95, 96, 126, 33, 13, 9, 44, 58, 10, 45, 46, 36, 47, 34, 124, 42, 40, 41, 63, 123, 125, 39, 0 };
/*     */ 
/*     */ 
/*     */   
/* 126 */   private static final byte[] MIXED = new byte[128];
/* 127 */   private static final byte[] PUNCTUATION = new byte[128];
/*     */   
/* 129 */   private static final Charset DEFAULT_ENCODING = Charset.forName("ISO-8859-1");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static {
/* 136 */     Arrays.fill(MIXED, (byte)-1); int i;
/* 137 */     for (i = 0; i < TEXT_MIXED_RAW.length; i++) {
/*     */       byte b;
/* 139 */       if ((b = TEXT_MIXED_RAW[i]) > 0) {
/* 140 */         MIXED[b] = (byte)i;
/*     */       }
/*     */     } 
/* 143 */     Arrays.fill(PUNCTUATION, (byte)-1);
/* 144 */     for (i = 0; i < TEXT_PUNCTUATION_RAW.length; i++) {
/*     */       byte b;
/* 146 */       if ((b = TEXT_PUNCTUATION_RAW[i]) > 0) {
/* 147 */         PUNCTUATION[b] = (byte)i;
/*     */       }
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
/*     */   static String encodeHighLevel(String msg, Compaction compaction, Charset encoding) throws WriterException {
/* 166 */     StringBuilder sb = new StringBuilder(msg.length());
/*     */     
/* 168 */     if (encoding == null)
/* 169 */     { encoding = DEFAULT_ENCODING; }
/* 170 */     else { CharacterSetECI eci; if (!DEFAULT_ENCODING.equals(encoding) && (
/*     */         
/* 172 */         eci = CharacterSetECI.getCharacterSetECIByName(encoding.name())) != null) {
/* 173 */         encodingECI(eci.getValue(), sb);
/*     */       } }
/*     */ 
/*     */     
/* 177 */     int len = msg.length();
/* 178 */     int p = 0;
/* 179 */     int textSubMode = 0;
/*     */ 
/*     */     
/* 182 */     if (compaction == Compaction.TEXT) {
/* 183 */       encodeText(msg, 0, len, sb, 0);
/*     */     }
/* 185 */     else if (compaction == Compaction.BYTE) {
/*     */       byte[] bytes;
/* 187 */       encodeBinary(bytes = msg.getBytes(encoding), 0, bytes.length, 1, sb);
/*     */     }
/* 189 */     else if (compaction == Compaction.NUMERIC) {
/* 190 */       sb.append('Ά');
/* 191 */       encodeNumeric(msg, 0, len, sb);
/*     */     } else {
/*     */       
/* 194 */       int encodingMode = 0;
/* 195 */       while (p < len) {
/*     */         int n;
/* 197 */         if ((n = determineConsecutiveDigitCount(msg, p)) >= 13) {
/* 198 */           sb.append('Ά');
/* 199 */           encodingMode = 2;
/* 200 */           textSubMode = 0;
/* 201 */           encodeNumeric(msg, p, n, sb);
/* 202 */           p += n; continue;
/*     */         } 
/*     */         int t;
/* 205 */         if ((t = determineConsecutiveTextCount(msg, p)) >= 5 || n == len) {
/* 206 */           if (encodingMode != 0) {
/* 207 */             sb.append('΄');
/* 208 */             encodingMode = 0;
/* 209 */             textSubMode = 0;
/*     */           } 
/* 211 */           textSubMode = encodeText(msg, p, t, sb, textSubMode);
/* 212 */           p += t; continue;
/*     */         } 
/*     */         int b;
/* 215 */         if ((b = determineConsecutiveBinaryCount(msg, p, encoding)) == 0) {
/* 216 */           b = 1;
/*     */         }
/*     */         byte[] bytes;
/* 219 */         if ((bytes = msg.substring(p, p + b).getBytes(encoding)).length == 1 && encodingMode == 0) {
/*     */           
/* 221 */           encodeBinary(bytes, 0, 1, 0, sb);
/*     */         } else {
/*     */           
/* 224 */           encodeBinary(bytes, 0, bytes.length, encodingMode, sb);
/* 225 */           encodingMode = 1;
/* 226 */           textSubMode = 0;
/*     */         } 
/* 228 */         p += b;
/*     */       } 
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 234 */     return sb.toString();
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
/*     */   private static int encodeText(CharSequence msg, int startpos, int count, StringBuilder sb, int initialSubmode) {
/* 253 */     StringBuilder tmp = new StringBuilder(count);
/* 254 */     int submode = initialSubmode;
/* 255 */     int idx = 0;
/*     */     while (true) {
/* 257 */       char ch = msg.charAt(startpos + idx);
/* 258 */       switch (submode) {
/*     */         case 0:
/* 260 */           if (isAlphaUpper(ch)) {
/* 261 */             if (ch == ' ') {
/* 262 */               tmp.append('\032'); break;
/*     */             } 
/* 264 */             tmp.append((char)(ch - 65));
/*     */             break;
/*     */           } 
/* 267 */           if (isAlphaLower(ch)) {
/* 268 */             submode = 1;
/* 269 */             tmp.append('\033'); continue;
/*     */           } 
/* 271 */           if (isMixed(ch)) {
/* 272 */             submode = 2;
/* 273 */             tmp.append('\034');
/*     */             continue;
/*     */           } 
/* 276 */           tmp.append('\035');
/* 277 */           tmp.append((char)PUNCTUATION[ch]);
/*     */           break;
/*     */ 
/*     */ 
/*     */         
/*     */         case 1:
/* 283 */           if (isAlphaLower(ch)) {
/* 284 */             if (ch == ' ') {
/* 285 */               tmp.append('\032'); break;
/*     */             } 
/* 287 */             tmp.append((char)(ch - 97));
/*     */             break;
/*     */           } 
/* 290 */           if (isAlphaUpper(ch)) {
/* 291 */             tmp.append('\033');
/* 292 */             tmp.append((char)(ch - 65));
/*     */             break;
/*     */           } 
/* 295 */           if (isMixed(ch)) {
/* 296 */             submode = 2;
/* 297 */             tmp.append('\034');
/*     */             continue;
/*     */           } 
/* 300 */           tmp.append('\035');
/* 301 */           tmp.append((char)PUNCTUATION[ch]);
/*     */           break;
/*     */ 
/*     */ 
/*     */         
/*     */         case 2:
/* 307 */           if (isMixed(ch)) {
/* 308 */             tmp.append((char)MIXED[ch]); break;
/*     */           } 
/* 310 */           if (isAlphaUpper(ch)) {
/* 311 */             submode = 0;
/* 312 */             tmp.append('\034'); continue;
/*     */           } 
/* 314 */           if (isAlphaLower(ch)) {
/* 315 */             submode = 1;
/* 316 */             tmp.append('\033');
/*     */             continue;
/*     */           } 
/* 319 */           if (startpos + idx + 1 < count && 
/*     */             
/* 321 */             isPunctuation(msg.charAt(startpos + idx + 1))) {
/* 322 */             submode = 3;
/* 323 */             tmp.append('\031');
/*     */             
/*     */             continue;
/*     */           } 
/* 327 */           tmp.append('\035');
/* 328 */           tmp.append((char)PUNCTUATION[ch]);
/*     */           break;
/*     */ 
/*     */         
/*     */         default:
/* 333 */           if (isPunctuation(ch)) {
/* 334 */             tmp.append((char)PUNCTUATION[ch]); break;
/*     */           } 
/* 336 */           submode = 0;
/* 337 */           tmp.append('\035');
/*     */           continue;
/*     */       } 
/*     */       
/* 341 */       idx++;
/* 342 */       if (idx >= count) {
/*     */         break;
/*     */       }
/*     */     } 
/* 346 */     char h = Character.MIN_VALUE;
/* 347 */     int len = tmp.length();
/* 348 */     for (int i = 0; i < len; i++) {
/* 349 */       if ((i % 2 != 0)) {
/*     */         
/* 351 */         h = (char)(h * 30 + tmp.charAt(i));
/* 352 */         sb.append(h);
/*     */       } else {
/* 354 */         h = tmp.charAt(i);
/*     */       } 
/*     */     } 
/* 357 */     if (len % 2 != 0) {
/* 358 */       sb.append((char)(h * 30 + 29));
/*     */     }
/* 360 */     return submode;
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
/*     */   private static void encodeBinary(byte[] bytes, int startpos, int count, int startmode, StringBuilder sb) {
/* 379 */     if (count == 1 && startmode == 0) {
/* 380 */       sb.append('Α');
/*     */     }
/* 382 */     else if (count % 6 == 0) {
/* 383 */       sb.append('Μ');
/*     */     } else {
/* 385 */       sb.append('΅');
/*     */     } 
/*     */ 
/*     */     
/* 389 */     int idx = startpos;
/*     */     
/* 391 */     if (count >= 6) {
/* 392 */       char[] chars = new char[5];
/* 393 */       while (startpos + count - idx >= 6) {
/* 394 */         long t = 0L; int j;
/* 395 */         for (j = 0; j < 6; j++)
/*     */         {
/* 397 */           t = (t << 8L) + (bytes[idx + j] & 0xFF);
/*     */         }
/* 399 */         for (j = 0; j < 5; j++) {
/* 400 */           chars[j] = (char)(int)(t % 900L);
/* 401 */           t /= 900L;
/*     */         } 
/* 403 */         for (j = 4; j >= 0; j--) {
/* 404 */           sb.append(chars[j]);
/*     */         }
/* 406 */         idx += 6;
/*     */       } 
/*     */     } 
/*     */     
/* 410 */     for (int i = idx; i < startpos + count; i++) {
/* 411 */       int ch = bytes[i] & 0xFF;
/* 412 */       sb.append((char)ch);
/*     */     } 
/*     */   }
/*     */   
/*     */   private static void encodeNumeric(String msg, int startpos, int count, StringBuilder sb) {
/* 417 */     int idx = 0;
/* 418 */     StringBuilder tmp = new StringBuilder(count / 3 + 1);
/* 419 */     BigInteger num900 = BigInteger.valueOf(900L);
/* 420 */     BigInteger num0 = BigInteger.valueOf(0L);
/* 421 */     while (idx < count) {
/* 422 */       tmp.setLength(0);
/* 423 */       int len = Math.min(44, count - idx);
/* 424 */       String part = "1" + msg.substring(startpos + idx, startpos + idx + len);
/* 425 */       BigInteger bigint = new BigInteger(part);
/*     */       do {
/* 427 */         tmp.append((char)bigint.mod(num900).intValue());
/*     */       }
/* 429 */       while (!(bigint = bigint.divide(num900)).equals(num0));
/*     */ 
/*     */       
/* 432 */       for (int i = tmp.length() - 1; i >= 0; i--) {
/* 433 */         sb.append(tmp.charAt(i));
/*     */       }
/* 435 */       idx += len;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private static boolean isDigit(char ch) {
/* 441 */     return (ch >= '0' && ch <= '9');
/*     */   }
/*     */   
/*     */   private static boolean isAlphaUpper(char ch) {
/* 445 */     return (ch == ' ' || (ch >= 'A' && ch <= 'Z'));
/*     */   }
/*     */   
/*     */   private static boolean isAlphaLower(char ch) {
/* 449 */     return (ch == ' ' || (ch >= 'a' && ch <= 'z'));
/*     */   }
/*     */   
/*     */   private static boolean isMixed(char ch) {
/* 453 */     return (MIXED[ch] != -1);
/*     */   }
/*     */   
/*     */   private static boolean isPunctuation(char ch) {
/* 457 */     return (PUNCTUATION[ch] != -1);
/*     */   }
/*     */   
/*     */   private static boolean isText(char ch) {
/* 461 */     return (ch == '\t' || ch == '\n' || ch == '\r' || (ch >= ' ' && ch <= '~'));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static int determineConsecutiveDigitCount(CharSequence msg, int startpos) {
/* 472 */     int count = 0;
/* 473 */     int len = msg.length();
/* 474 */     int idx = startpos;
/* 475 */     if (startpos < len) {
/* 476 */       char ch = msg.charAt(startpos);
/* 477 */       while (isDigit(ch) && idx < len) {
/* 478 */         count++;
/* 479 */         idx++;
/* 480 */         if (idx < len) {
/* 481 */           ch = msg.charAt(idx);
/*     */         }
/*     */       } 
/*     */     } 
/* 485 */     return count;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static int determineConsecutiveTextCount(CharSequence msg, int startpos) {
/* 496 */     int len = msg.length();
/* 497 */     int idx = startpos;
/* 498 */     while (idx < len) {
/* 499 */       char ch = msg.charAt(idx);
/* 500 */       int numericCount = 0;
/* 501 */       while (numericCount < 13 && isDigit(ch) && idx < len) {
/* 502 */         numericCount++;
/* 503 */         idx++;
/* 504 */         if (idx < len) {
/* 505 */           ch = msg.charAt(idx);
/*     */         }
/*     */       } 
/* 508 */       if (numericCount >= 13) {
/* 509 */         return idx - startpos - numericCount;
/*     */       }
/* 511 */       if (numericCount <= 0) {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 518 */         if (isText(msg.charAt(idx)))
/*     */         
/*     */         { 
/* 521 */           idx++; continue; }  break;
/*     */       } 
/* 523 */     }  return idx - startpos;
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
/*     */   private static int determineConsecutiveBinaryCount(String msg, int startpos, Charset encoding) throws WriterException {
/* 536 */     CharsetEncoder encoder = encoding.newEncoder();
/* 537 */     int len = msg.length();
/* 538 */     int idx = startpos;
/* 539 */     while (idx < len) {
/* 540 */       char ch = msg.charAt(idx);
/* 541 */       int numericCount = 0;
/*     */ 
/*     */       
/* 544 */       numericCount++;
/*     */       
/*     */       int i;
/* 547 */       while (numericCount < 13 && isDigit(ch) && (i = idx + numericCount) < len)
/*     */       {
/*     */         
/* 550 */         ch = msg.charAt(i);
/*     */       }
/* 552 */       if (numericCount >= 13) {
/* 553 */         return idx - startpos;
/*     */       }
/* 555 */       ch = msg.charAt(idx);
/*     */       
/* 557 */       if (!encoder.canEncode(ch)) {
/* 558 */         throw new WriterException("Non-encodable character detected: " + ch + " (Unicode: " + ch + ')');
/*     */       }
/* 560 */       idx++;
/*     */     } 
/* 562 */     return idx - startpos;
/*     */   }
/*     */   
/*     */   private static void encodingECI(int eci, StringBuilder sb) throws WriterException {
/* 566 */     if (eci >= 0 && eci < 900) {
/* 567 */       sb.append('Ο');
/* 568 */       sb.append((char)eci); return;
/* 569 */     }  if (eci < 810900) {
/* 570 */       sb.append('Ξ');
/* 571 */       sb.append((char)(eci / 900 - 1));
/* 572 */       sb.append((char)(eci % 900)); return;
/* 573 */     }  if (eci < 811800) {
/* 574 */       sb.append('Ν');
/* 575 */       sb.append((char)(810900 - eci)); return;
/*     */     } 
/* 577 */     throw new WriterException("ECI number not in valid range from 0..811799, but was " + eci);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\zxing\pdf417\encoder\PDF417HighLevelEncoder.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */