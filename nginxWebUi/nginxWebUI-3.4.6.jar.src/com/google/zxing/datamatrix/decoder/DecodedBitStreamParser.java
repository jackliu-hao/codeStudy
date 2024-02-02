/*     */ package com.google.zxing.datamatrix.decoder;
/*     */ 
/*     */ import com.google.zxing.FormatException;
/*     */ import com.google.zxing.common.BitSource;
/*     */ import com.google.zxing.common.DecoderResult;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.List;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ final class DecodedBitStreamParser
/*     */ {
/*     */   private enum Mode
/*     */   {
/*  40 */     PAD_ENCODE,
/*  41 */     ASCII_ENCODE,
/*  42 */     C40_ENCODE,
/*  43 */     TEXT_ENCODE,
/*  44 */     ANSIX12_ENCODE,
/*  45 */     EDIFACT_ENCODE,
/*  46 */     BASE256_ENCODE;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  53 */   private static final char[] C40_BASIC_SET_CHARS = new char[] { '*', '*', '*', ' ', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z' };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  59 */   private static final char[] C40_SHIFT2_SET_CHARS = new char[] { '!', '"', '#', '$', '%', '&', '\'', '(', ')', '*', '+', ',', '-', '.', '/', ':', ';', '<', '=', '>', '?', '@', '[', '\\', ']', '^', '_' };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  68 */   private static final char[] TEXT_BASIC_SET_CHARS = new char[] { '*', '*', '*', ' ', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z' };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  75 */   private static final char[] TEXT_SHIFT2_SET_CHARS = C40_SHIFT2_SET_CHARS;
/*     */   
/*  77 */   private static final char[] TEXT_SHIFT3_SET_CHARS = new char[] { '`', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '{', '|', '}', '~', '' };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static DecoderResult decode(byte[] bytes) throws FormatException {
/*  86 */     BitSource bits = new BitSource(bytes);
/*  87 */     StringBuilder result = new StringBuilder(100);
/*  88 */     StringBuilder resultTrailer = new StringBuilder(0);
/*  89 */     List<byte[]> byteSegments = (List)new ArrayList<>(1);
/*  90 */     Mode mode = Mode.ASCII_ENCODE;
/*     */     do {
/*  92 */       if (mode == Mode.ASCII_ENCODE) {
/*  93 */         mode = decodeAsciiSegment(bits, result, resultTrailer);
/*     */       } else {
/*  95 */         switch (mode) {
/*     */           case C40_ENCODE:
/*  97 */             decodeC40Segment(bits, result);
/*     */             break;
/*     */           case TEXT_ENCODE:
/* 100 */             decodeTextSegment(bits, result);
/*     */             break;
/*     */           case ANSIX12_ENCODE:
/* 103 */             decodeAnsiX12Segment(bits, result);
/*     */             break;
/*     */           case EDIFACT_ENCODE:
/* 106 */             decodeEdifactSegment(bits, result);
/*     */             break;
/*     */           case BASE256_ENCODE:
/* 109 */             decodeBase256Segment(bits, result, (Collection<byte[]>)byteSegments);
/*     */             break;
/*     */           default:
/* 112 */             throw FormatException.getFormatInstance();
/*     */         } 
/* 114 */         mode = Mode.ASCII_ENCODE;
/*     */       } 
/* 116 */     } while (mode != Mode.PAD_ENCODE && bits.available() > 0);
/* 117 */     if (resultTrailer.length() > 0) {
/* 118 */       result.append(resultTrailer);
/*     */     }
/* 120 */     return new DecoderResult(bytes, result.toString(), byteSegments.isEmpty() ? null : byteSegments, null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static Mode decodeAsciiSegment(BitSource bits, StringBuilder result, StringBuilder resultTrailer) throws FormatException {
/* 129 */     boolean upperShift = false;
/*     */     while (true) {
/*     */       int oneByte;
/* 132 */       if ((oneByte = bits.readBits(8)) == 0)
/* 133 */         throw FormatException.getFormatInstance(); 
/* 134 */       if (oneByte <= 128) {
/* 135 */         if (upperShift) {
/* 136 */           oneByte += 128;
/*     */         }
/*     */         
/* 139 */         result.append((char)(oneByte - 1));
/* 140 */         return Mode.ASCII_ENCODE;
/* 141 */       }  if (oneByte == 129)
/* 142 */         return Mode.PAD_ENCODE; 
/* 143 */       if (oneByte <= 229)
/*     */       { int value;
/* 145 */         if ((value = oneByte - 130) < 10) {
/* 146 */           result.append('0');
/*     */         }
/* 148 */         result.append(value); }
/* 149 */       else { if (oneByte == 230)
/* 150 */           return Mode.C40_ENCODE; 
/* 151 */         if (oneByte == 231)
/* 152 */           return Mode.BASE256_ENCODE; 
/* 153 */         if (oneByte == 232) {
/*     */           
/* 155 */           result.append('\035');
/* 156 */         } else if (oneByte != 233 && oneByte != 234) {
/*     */ 
/*     */ 
/*     */           
/* 160 */           if (oneByte == 235)
/* 161 */           { upperShift = true; }
/* 162 */           else if (oneByte == 236)
/* 163 */           { result.append("[)>\03605\035");
/* 164 */             resultTrailer.insert(0, "\036\004"); }
/* 165 */           else if (oneByte == 237)
/* 166 */           { result.append("[)>\03606\035");
/* 167 */             resultTrailer.insert(0, "\036\004"); }
/* 168 */           else { if (oneByte == 238)
/* 169 */               return Mode.ANSIX12_ENCODE; 
/* 170 */             if (oneByte == 239)
/* 171 */               return Mode.TEXT_ENCODE; 
/* 172 */             if (oneByte == 240)
/* 173 */               return Mode.EDIFACT_ENCODE; 
/* 174 */             if (oneByte != 241)
/*     */             {
/*     */ 
/*     */               
/* 178 */               if (oneByte >= 242)
/*     */               {
/* 180 */                 if (oneByte != 254 || bits.available() != 0)
/* 181 */                   throw FormatException.getFormatInstance();  }  }  }
/*     */         
/*     */         }  }
/* 184 */        if (bits.available() <= 0) {
/* 185 */         return Mode.ASCII_ENCODE;
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static void decodeC40Segment(BitSource bits, StringBuilder result) throws FormatException {
/* 195 */     boolean upperShift = false;
/*     */     
/* 197 */     int[] cValues = new int[3];
/* 198 */     int shift = 0;
/*     */ 
/*     */     
/*     */     do {
/* 202 */       if (bits.available() == 8) {
/*     */         return;
/*     */       }
/*     */       int firstByte;
/* 206 */       if ((firstByte = bits.readBits(8)) == 254) {
/*     */         return;
/*     */       }
/*     */       
/* 210 */       parseTwoBytes(firstByte, bits.readBits(8), cValues);
/*     */       
/* 212 */       for (int i = 0; i < 3; i++) {
/* 213 */         int cValue = cValues[i];
/* 214 */         switch (shift) {
/*     */           case 0:
/* 216 */             if (cValue < 3) {
/* 217 */               shift = cValue + 1; break;
/* 218 */             }  if (cValue < C40_BASIC_SET_CHARS.length) {
/* 219 */               char c40char = C40_BASIC_SET_CHARS[cValue];
/* 220 */               if (upperShift) {
/* 221 */                 result.append((char)(c40char + 128));
/* 222 */                 upperShift = false; break;
/*     */               } 
/* 224 */               result.append(c40char);
/*     */               break;
/*     */             } 
/* 227 */             throw FormatException.getFormatInstance();
/*     */ 
/*     */           
/*     */           case 1:
/* 231 */             if (upperShift) {
/* 232 */               result.append((char)(cValue + 128));
/* 233 */               upperShift = false;
/*     */             } else {
/* 235 */               result.append((char)cValue);
/*     */             } 
/* 237 */             shift = 0;
/*     */             break;
/*     */           case 2:
/* 240 */             if (cValue < C40_SHIFT2_SET_CHARS.length) {
/* 241 */               char c40char = C40_SHIFT2_SET_CHARS[cValue];
/* 242 */               if (upperShift) {
/* 243 */                 result.append((char)(c40char + 128));
/* 244 */                 upperShift = false;
/*     */               } else {
/* 246 */                 result.append(c40char);
/*     */               } 
/* 248 */             } else if (cValue == 27) {
/* 249 */               result.append('\035');
/* 250 */             } else if (cValue == 30) {
/* 251 */               upperShift = true;
/*     */             } else {
/* 253 */               throw FormatException.getFormatInstance();
/*     */             } 
/* 255 */             shift = 0;
/*     */             break;
/*     */           case 3:
/* 258 */             if (upperShift) {
/* 259 */               result.append((char)(cValue + 224));
/* 260 */               upperShift = false;
/*     */             } else {
/* 262 */               result.append((char)(cValue + 96));
/*     */             } 
/* 264 */             shift = 0;
/*     */             break;
/*     */           default:
/* 267 */             throw FormatException.getFormatInstance();
/*     */         } 
/*     */       } 
/* 270 */     } while (bits.available() > 0);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static void decodeTextSegment(BitSource bits, StringBuilder result) throws FormatException {
/* 280 */     boolean upperShift = false;
/*     */     
/* 282 */     int[] cValues = new int[3];
/* 283 */     int shift = 0;
/*     */     
/*     */     do {
/* 286 */       if (bits.available() == 8) {
/*     */         return;
/*     */       }
/*     */       int firstByte;
/* 290 */       if ((firstByte = bits.readBits(8)) == 254) {
/*     */         return;
/*     */       }
/*     */       
/* 294 */       parseTwoBytes(firstByte, bits.readBits(8), cValues);
/*     */       
/* 296 */       for (int i = 0; i < 3; i++) {
/* 297 */         int cValue = cValues[i];
/* 298 */         switch (shift) {
/*     */           case 0:
/* 300 */             if (cValue < 3) {
/* 301 */               shift = cValue + 1; break;
/* 302 */             }  if (cValue < TEXT_BASIC_SET_CHARS.length) {
/* 303 */               char textChar = TEXT_BASIC_SET_CHARS[cValue];
/* 304 */               if (upperShift) {
/* 305 */                 result.append((char)(textChar + 128));
/* 306 */                 upperShift = false; break;
/*     */               } 
/* 308 */               result.append(textChar);
/*     */               break;
/*     */             } 
/* 311 */             throw FormatException.getFormatInstance();
/*     */ 
/*     */           
/*     */           case 1:
/* 315 */             if (upperShift) {
/* 316 */               result.append((char)(cValue + 128));
/* 317 */               upperShift = false;
/*     */             } else {
/* 319 */               result.append((char)cValue);
/*     */             } 
/* 321 */             shift = 0;
/*     */             break;
/*     */           
/*     */           case 2:
/* 325 */             if (cValue < TEXT_SHIFT2_SET_CHARS.length) {
/* 326 */               char textChar = TEXT_SHIFT2_SET_CHARS[cValue];
/* 327 */               if (upperShift) {
/* 328 */                 result.append((char)(textChar + 128));
/* 329 */                 upperShift = false;
/*     */               } else {
/* 331 */                 result.append(textChar);
/*     */               } 
/* 333 */             } else if (cValue == 27) {
/* 334 */               result.append('\035');
/* 335 */             } else if (cValue == 30) {
/* 336 */               upperShift = true;
/*     */             } else {
/* 338 */               throw FormatException.getFormatInstance();
/*     */             } 
/* 340 */             shift = 0;
/*     */             break;
/*     */           case 3:
/* 343 */             if (cValue < TEXT_SHIFT3_SET_CHARS.length) {
/* 344 */               char textChar = TEXT_SHIFT3_SET_CHARS[cValue];
/* 345 */               if (upperShift) {
/* 346 */                 result.append((char)(textChar + 128));
/* 347 */                 upperShift = false;
/*     */               } else {
/* 349 */                 result.append(textChar);
/*     */               } 
/* 351 */               shift = 0; break;
/*     */             } 
/* 353 */             throw FormatException.getFormatInstance();
/*     */ 
/*     */           
/*     */           default:
/* 357 */             throw FormatException.getFormatInstance();
/*     */         } 
/*     */       } 
/* 360 */     } while (bits.available() > 0);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static void decodeAnsiX12Segment(BitSource bits, StringBuilder result) throws FormatException {
/* 371 */     int[] cValues = new int[3];
/*     */     
/*     */     do {
/* 374 */       if (bits.available() == 8) {
/*     */         return;
/*     */       }
/*     */       int firstByte;
/* 378 */       if ((firstByte = bits.readBits(8)) == 254) {
/*     */         return;
/*     */       }
/*     */       
/* 382 */       parseTwoBytes(firstByte, bits.readBits(8), cValues);
/*     */       
/* 384 */       for (int i = 0; i < 3; i++) {
/*     */         int cValue;
/* 386 */         if ((cValue = cValues[i]) == 0) {
/* 387 */           result.append('\r');
/* 388 */         } else if (cValue == 1) {
/* 389 */           result.append('*');
/* 390 */         } else if (cValue == 2) {
/* 391 */           result.append('>');
/* 392 */         } else if (cValue == 3) {
/* 393 */           result.append(' ');
/* 394 */         } else if (cValue < 14) {
/* 395 */           result.append((char)(cValue + 44));
/* 396 */         } else if (cValue < 40) {
/* 397 */           result.append((char)(cValue + 51));
/*     */         } else {
/* 399 */           throw FormatException.getFormatInstance();
/*     */         } 
/*     */       } 
/* 402 */     } while (bits.available() > 0);
/*     */   }
/*     */ 
/*     */   
/*     */   private static void parseTwoBytes(int firstByte, int secondByte, int[] result) {
/* 407 */     int fullBitValue, temp = (fullBitValue = (firstByte << 8) + secondByte - 1) / 1600;
/* 408 */     result[0] = temp;
/*     */     
/* 410 */     temp = (fullBitValue = fullBitValue - temp * 1600) / 40;
/* 411 */     result[1] = temp;
/* 412 */     result[2] = fullBitValue - temp * 40;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static void decodeEdifactSegment(BitSource bits, StringBuilder result) {
/*     */     do {
/* 421 */       if (bits.available() <= 16) {
/*     */         return;
/*     */       }
/*     */       
/* 425 */       for (int i = 0; i < 4; i++) {
/*     */         int edifactValue;
/*     */ 
/*     */         
/* 429 */         if ((edifactValue = bits.readBits(6)) == 31) {
/*     */           int bitsLeft;
/*     */           
/* 432 */           if ((bitsLeft = 8 - bits.getBitOffset()) != 8) {
/* 433 */             bits.readBits(bitsLeft);
/*     */           }
/*     */           
/*     */           return;
/*     */         } 
/* 438 */         if ((edifactValue & 0x20) == 0) {
/* 439 */           edifactValue |= 0x40;
/*     */         }
/* 441 */         result.append((char)edifactValue);
/*     */       } 
/* 443 */     } while (bits.available() > 0);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static void decodeBase256Segment(BitSource bits, StringBuilder result, Collection<byte[]> byteSegments) throws FormatException {
/* 454 */     int count, codewordPosition = 1 + bits.getByteOffset();
/*     */     
/*     */     int d1;
/* 457 */     if ((d1 = unrandomize255State(bits.readBits(8), codewordPosition++)) == 0) {
/* 458 */       count = bits.available() / 8;
/* 459 */     } else if (d1 < 250) {
/* 460 */       count = d1;
/*     */     } else {
/* 462 */       count = 250 * (d1 - 249) + unrandomize255State(bits.readBits(8), codewordPosition++);
/*     */     } 
/*     */ 
/*     */     
/* 466 */     if (count < 0) {
/* 467 */       throw FormatException.getFormatInstance();
/*     */     }
/*     */     
/* 470 */     byte[] bytes = new byte[count];
/* 471 */     for (int i = 0; i < count; i++) {
/*     */ 
/*     */       
/* 474 */       if (bits.available() < 8) {
/* 475 */         throw FormatException.getFormatInstance();
/*     */       }
/* 477 */       bytes[i] = (byte)unrandomize255State(bits.readBits(8), codewordPosition++);
/*     */     } 
/* 479 */     byteSegments.add(bytes);
/*     */     try {
/* 481 */       result.append(new String(bytes, "ISO8859_1")); return;
/* 482 */     } catch (UnsupportedEncodingException uee) {
/* 483 */       throw new IllegalStateException("Platform does not support required encoding: " + uee);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static int unrandomize255State(int randomizedBase256Codeword, int base256CodewordPosition) {
/* 492 */     int pseudoRandomNumber = base256CodewordPosition * 149 % 255 + 1;
/*     */     int tempVariable;
/* 494 */     return ((tempVariable = randomizedBase256Codeword - pseudoRandomNumber) >= 0) ? tempVariable : (tempVariable + 256);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\zxing\datamatrix\decoder\DecodedBitStreamParser.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */