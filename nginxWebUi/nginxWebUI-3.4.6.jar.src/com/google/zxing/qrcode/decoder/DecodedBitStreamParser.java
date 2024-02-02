/*     */ package com.google.zxing.qrcode.decoder;
/*     */ 
/*     */ import com.google.zxing.DecodeHintType;
/*     */ import com.google.zxing.FormatException;
/*     */ import com.google.zxing.common.BitSource;
/*     */ import com.google.zxing.common.CharacterSetECI;
/*     */ import com.google.zxing.common.DecoderResult;
/*     */ import com.google.zxing.common.StringUtils;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ final class DecodedBitStreamParser
/*     */ {
/*  45 */   private static final char[] ALPHANUMERIC_CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ $%*+-./:"
/*  46 */     .toCharArray();
/*     */ 
/*     */ 
/*     */   
/*     */   private static final int GB2312_SUBSET = 1;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static DecoderResult decode(byte[] bytes, Version version, ErrorCorrectionLevel ecLevel, Map<DecodeHintType, ?> hints) throws FormatException {
/*  56 */     BitSource bits = new BitSource(bytes);
/*  57 */     StringBuilder result = new StringBuilder(50);
/*  58 */     List<byte[]> byteSegments = (List)new ArrayList<>(1);
/*  59 */     int symbolSequence = -1;
/*  60 */     int parityData = -1;
/*     */     try {
/*     */       Mode mode;
/*  63 */       CharacterSetECI currentCharacterSetECI = null;
/*  64 */       boolean fc1InEffect = false;
/*     */ 
/*     */       
/*     */       do {
/*  68 */         if (bits.available() < 4) {
/*     */           
/*  70 */           mode = Mode.TERMINATOR;
/*     */         } else {
/*  72 */           mode = Mode.forBits(bits.readBits(4));
/*     */         } 
/*  74 */         if (mode == Mode.TERMINATOR)
/*  75 */           continue;  if (mode == Mode.FNC1_FIRST_POSITION || mode == Mode.FNC1_SECOND_POSITION) {
/*     */           
/*  77 */           fc1InEffect = true;
/*  78 */         } else if (mode == Mode.STRUCTURED_APPEND) {
/*  79 */           if (bits.available() < 16) {
/*  80 */             throw FormatException.getFormatInstance();
/*     */           }
/*     */ 
/*     */           
/*  84 */           symbolSequence = bits.readBits(8);
/*  85 */           parityData = bits.readBits(8);
/*  86 */         } else if (mode == Mode.ECI) {
/*     */ 
/*     */ 
/*     */           
/*  90 */           if ((currentCharacterSetECI = CharacterSetECI.getCharacterSetECIByValue(parseECIValue(bits))) == null) {
/*  91 */             throw FormatException.getFormatInstance();
/*     */           
/*     */           }
/*     */         }
/*  95 */         else if (mode == Mode.HANZI) {
/*     */           
/*  97 */           int subset = bits.readBits(4);
/*  98 */           int countHanzi = bits.readBits(mode.getCharacterCountBits(version));
/*  99 */           if (subset == 1) {
/* 100 */             decodeHanziSegment(bits, result, countHanzi);
/*     */           }
/*     */         }
/*     */         else {
/*     */           
/* 105 */           int count = bits.readBits(mode.getCharacterCountBits(version));
/* 106 */           if (mode == Mode.NUMERIC) {
/* 107 */             decodeNumericSegment(bits, result, count);
/* 108 */           } else if (mode == Mode.ALPHANUMERIC) {
/* 109 */             decodeAlphanumericSegment(bits, result, count, fc1InEffect);
/* 110 */           } else if (mode == Mode.BYTE) {
/* 111 */             decodeByteSegment(bits, result, count, currentCharacterSetECI, (Collection<byte[]>)byteSegments, hints);
/* 112 */           } else if (mode == Mode.KANJI) {
/* 113 */             decodeKanjiSegment(bits, result, count);
/*     */           } else {
/* 115 */             throw FormatException.getFormatInstance();
/*     */           }
/*     */         
/*     */         }
/*     */       
/* 120 */       } while (mode != Mode.TERMINATOR);
/* 121 */     } catch (IllegalArgumentException illegalArgumentException) {
/*     */       
/* 123 */       throw FormatException.getFormatInstance();
/*     */     } 
/*     */     
/* 126 */     return new DecoderResult(bytes, result
/* 127 */         .toString(), 
/* 128 */         byteSegments.isEmpty() ? null : byteSegments, (ecLevel == null) ? null : ecLevel
/* 129 */         .toString(), symbolSequence, parityData);
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
/*     */   private static void decodeHanziSegment(BitSource bits, StringBuilder result, int count) throws FormatException {
/* 141 */     if (count * 13 > bits.available()) {
/* 142 */       throw FormatException.getFormatInstance();
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 147 */     byte[] buffer = new byte[2 * count];
/* 148 */     int offset = 0;
/* 149 */     while (count > 0) {
/*     */       int twoBytes;
/*     */       
/*     */       int assembledTwoBytes;
/* 153 */       if ((assembledTwoBytes = (twoBytes = bits.readBits(13)) / 96 << 8 | twoBytes % 96) < 959) {
/*     */         
/* 155 */         assembledTwoBytes += 41377;
/*     */       } else {
/*     */         
/* 158 */         assembledTwoBytes += 42657;
/*     */       } 
/* 160 */       buffer[offset] = (byte)(assembledTwoBytes >> 8);
/* 161 */       buffer[offset + 1] = (byte)assembledTwoBytes;
/* 162 */       offset += 2;
/* 163 */       count--;
/*     */     } 
/*     */     
/*     */     try {
/* 167 */       result.append(new String(buffer, "GB2312")); return;
/* 168 */     } catch (UnsupportedEncodingException unsupportedEncodingException) {
/* 169 */       throw FormatException.getFormatInstance();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static void decodeKanjiSegment(BitSource bits, StringBuilder result, int count) throws FormatException {
/* 177 */     if (count * 13 > bits.available()) {
/* 178 */       throw FormatException.getFormatInstance();
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 183 */     byte[] buffer = new byte[2 * count];
/* 184 */     int offset = 0;
/* 185 */     while (count > 0) {
/*     */       int twoBytes;
/*     */       
/*     */       int assembledTwoBytes;
/* 189 */       if ((assembledTwoBytes = (twoBytes = bits.readBits(13)) / 192 << 8 | twoBytes % 192) < 7936) {
/*     */         
/* 191 */         assembledTwoBytes += 33088;
/*     */       } else {
/*     */         
/* 194 */         assembledTwoBytes += 49472;
/*     */       } 
/* 196 */       buffer[offset] = (byte)(assembledTwoBytes >> 8);
/* 197 */       buffer[offset + 1] = (byte)assembledTwoBytes;
/* 198 */       offset += 2;
/* 199 */       count--;
/*     */     } 
/*     */     
/*     */     try {
/* 203 */       result.append(new String(buffer, "SJIS")); return;
/* 204 */     } catch (UnsupportedEncodingException unsupportedEncodingException) {
/* 205 */       throw FormatException.getFormatInstance();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static void decodeByteSegment(BitSource bits, StringBuilder result, int count, CharacterSetECI currentCharacterSetECI, Collection<byte[]> byteSegments, Map<DecodeHintType, ?> hints) throws FormatException {
/*     */     String encoding;
/* 216 */     if (count << 3 > bits.available()) {
/* 217 */       throw FormatException.getFormatInstance();
/*     */     }
/*     */     
/* 220 */     byte[] readBytes = new byte[count];
/* 221 */     for (int i = 0; i < count; i++) {
/* 222 */       readBytes[i] = (byte)bits.readBits(8);
/*     */     }
/*     */     
/* 225 */     if (currentCharacterSetECI == null) {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 231 */       encoding = StringUtils.guessEncoding(readBytes, hints);
/*     */     } else {
/* 233 */       encoding = currentCharacterSetECI.name();
/*     */     } 
/*     */     try {
/* 236 */       result.append(new String(readBytes, encoding));
/* 237 */     } catch (UnsupportedEncodingException unsupportedEncodingException) {
/* 238 */       throw FormatException.getFormatInstance();
/*     */     } 
/* 240 */     byteSegments.add(readBytes);
/*     */   }
/*     */   
/*     */   private static char toAlphaNumericChar(int value) throws FormatException {
/* 244 */     if (value >= ALPHANUMERIC_CHARS.length) {
/* 245 */       throw FormatException.getFormatInstance();
/*     */     }
/* 247 */     return ALPHANUMERIC_CHARS[value];
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static void decodeAlphanumericSegment(BitSource bits, StringBuilder result, int count, boolean fc1InEffect) throws FormatException {
/* 255 */     int start = result.length();
/* 256 */     while (count > 1) {
/* 257 */       if (bits.available() < 11) {
/* 258 */         throw FormatException.getFormatInstance();
/*     */       }
/* 260 */       int nextTwoCharsBits = bits.readBits(11);
/* 261 */       result.append(toAlphaNumericChar(nextTwoCharsBits / 45));
/* 262 */       result.append(toAlphaNumericChar(nextTwoCharsBits % 45));
/* 263 */       count -= 2;
/*     */     } 
/* 265 */     if (count == 1) {
/*     */       
/* 267 */       if (bits.available() < 6) {
/* 268 */         throw FormatException.getFormatInstance();
/*     */       }
/* 270 */       result.append(toAlphaNumericChar(bits.readBits(6)));
/*     */     } 
/*     */     
/* 273 */     if (fc1InEffect)
/*     */     {
/* 275 */       for (int i = start; i < result.length(); i++) {
/* 276 */         if (result.charAt(i) == '%') {
/* 277 */           if (i < result.length() - 1 && result.charAt(i + 1) == '%') {
/*     */             
/* 279 */             result.deleteCharAt(i + 1);
/*     */           } else {
/*     */             
/* 282 */             result.setCharAt(i, '\035');
/*     */           } 
/*     */         }
/*     */       } 
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static void decodeNumericSegment(BitSource bits, StringBuilder result, int count) throws FormatException {
/* 293 */     while (count >= 3) {
/*     */       
/* 295 */       if (bits.available() < 10) {
/* 296 */         throw FormatException.getFormatInstance();
/*     */       }
/*     */       int threeDigitsBits;
/* 299 */       if ((threeDigitsBits = bits.readBits(10)) >= 1000) {
/* 300 */         throw FormatException.getFormatInstance();
/*     */       }
/* 302 */       result.append(toAlphaNumericChar(threeDigitsBits / 100));
/* 303 */       result.append(toAlphaNumericChar(threeDigitsBits / 10 % 10));
/* 304 */       result.append(toAlphaNumericChar(threeDigitsBits % 10));
/* 305 */       count -= 3;
/*     */     } 
/* 307 */     if (count == 2) {
/*     */       
/* 309 */       if (bits.available() < 7) {
/* 310 */         throw FormatException.getFormatInstance();
/*     */       }
/*     */       int twoDigitsBits;
/* 313 */       if ((twoDigitsBits = bits.readBits(7)) >= 100) {
/* 314 */         throw FormatException.getFormatInstance();
/*     */       }
/* 316 */       result.append(toAlphaNumericChar(twoDigitsBits / 10));
/* 317 */       result.append(toAlphaNumericChar(twoDigitsBits % 10)); return;
/* 318 */     }  if (count == 1) {
/*     */       
/* 320 */       if (bits.available() < 4) {
/* 321 */         throw FormatException.getFormatInstance();
/*     */       }
/*     */       int digitBits;
/* 324 */       if ((digitBits = bits.readBits(4)) >= 10) {
/* 325 */         throw FormatException.getFormatInstance();
/*     */       }
/* 327 */       result.append(toAlphaNumericChar(digitBits));
/*     */     } 
/*     */   }
/*     */   
/*     */   private static int parseECIValue(BitSource bits) throws FormatException {
/*     */     int firstByte;
/* 333 */     if (((firstByte = bits.readBits(8)) & 0x80) == 0)
/*     */     {
/* 335 */       return firstByte & 0x7F;
/*     */     }
/* 337 */     if ((firstByte & 0xC0) == 128) {
/*     */       
/* 339 */       int secondByte = bits.readBits(8);
/* 340 */       return (firstByte & 0x3F) << 8 | secondByte;
/*     */     } 
/* 342 */     if ((firstByte & 0xE0) == 192) {
/*     */       
/* 344 */       int secondThirdBytes = bits.readBits(16);
/* 345 */       return (firstByte & 0x1F) << 16 | secondThirdBytes;
/*     */     } 
/* 347 */     throw FormatException.getFormatInstance();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\zxing\qrcode\decoder\DecodedBitStreamParser.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */