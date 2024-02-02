/*     */ package com.google.zxing.oned.rss.expanded.decoders;
/*     */ 
/*     */ import com.google.zxing.FormatException;
/*     */ import com.google.zxing.NotFoundException;
/*     */ import com.google.zxing.common.BitArray;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ final class GeneralAppIdDecoder
/*     */ {
/*     */   private final BitArray information;
/*  40 */   private final CurrentParsingState current = new CurrentParsingState();
/*  41 */   private final StringBuilder buffer = new StringBuilder();
/*     */   
/*     */   GeneralAppIdDecoder(BitArray information) {
/*  44 */     this.information = information;
/*     */   }
/*     */   
/*     */   String decodeAllCodes(StringBuilder buff, int initialPosition) throws NotFoundException, FormatException {
/*  48 */     int currentPosition = initialPosition;
/*  49 */     String remaining = null;
/*     */     while (true) {
/*     */       DecodedInformation info;
/*     */       String parsedFields;
/*  53 */       if ((parsedFields = FieldParser.parseFieldsInGeneralPurpose((info = decodeGeneralPurposeField(currentPosition, remaining)).getNewString())) != null) {
/*  54 */         buff.append(parsedFields);
/*     */       }
/*  56 */       if (info.isRemaining()) {
/*  57 */         remaining = String.valueOf(info.getRemainingValue());
/*     */       } else {
/*  59 */         remaining = null;
/*     */       } 
/*     */       
/*  62 */       if (currentPosition != info.getNewPosition()) {
/*     */ 
/*     */         
/*  65 */         currentPosition = info.getNewPosition(); continue;
/*     */       }  break;
/*     */     } 
/*  68 */     return buff.toString();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean isStillNumeric(int pos) {
/*  74 */     if (pos + 7 > this.information.getSize()) {
/*  75 */       return (pos + 4 <= this.information.getSize());
/*     */     }
/*     */     
/*  78 */     for (int i = pos; i < pos + 3; i++) {
/*  79 */       if (this.information.get(i)) {
/*  80 */         return true;
/*     */       }
/*     */     } 
/*     */     
/*  84 */     return this.information.get(pos + 3);
/*     */   }
/*     */   
/*     */   private DecodedNumeric decodeNumeric(int pos) throws FormatException {
/*  88 */     if (pos + 7 > this.information.getSize()) {
/*     */       int i;
/*  90 */       if ((i = extractNumericValueFromBitArray(pos, 4)) == 0) {
/*  91 */         return new DecodedNumeric(this.information.getSize(), 10, 10);
/*     */       }
/*  93 */       return new DecodedNumeric(this.information.getSize(), i - 1, 10);
/*     */     } 
/*     */ 
/*     */     
/*  97 */     int numeric, digit1 = ((numeric = extractNumericValueFromBitArray(pos, 7)) - 8) / 11;
/*  98 */     int digit2 = (numeric - 8) % 11;
/*     */     
/* 100 */     return new DecodedNumeric(pos + 7, digit1, digit2);
/*     */   }
/*     */   
/*     */   int extractNumericValueFromBitArray(int pos, int bits) {
/* 104 */     return extractNumericValueFromBitArray(this.information, pos, bits);
/*     */   }
/*     */   
/*     */   static int extractNumericValueFromBitArray(BitArray information, int pos, int bits) {
/* 108 */     int value = 0;
/* 109 */     for (int i = 0; i < bits; i++) {
/* 110 */       if (information.get(pos + i)) {
/* 111 */         value |= 1 << bits - i - 1;
/*     */       }
/*     */     } 
/*     */     
/* 115 */     return value;
/*     */   }
/*     */   
/*     */   DecodedInformation decodeGeneralPurposeField(int pos, String remaining) throws FormatException {
/* 119 */     this.buffer.setLength(0);
/*     */     
/* 121 */     if (remaining != null) {
/* 122 */       this.buffer.append(remaining);
/*     */     }
/*     */     
/* 125 */     this.current.setPosition(pos);
/*     */     
/*     */     DecodedInformation lastDecoded;
/* 128 */     if ((lastDecoded = parseBlocks()) != null && lastDecoded.isRemaining()) {
/* 129 */       return new DecodedInformation(this.current.getPosition(), this.buffer.toString(), lastDecoded.getRemainingValue());
/*     */     }
/* 131 */     return new DecodedInformation(this.current.getPosition(), this.buffer.toString());
/*     */   }
/*     */   
/*     */   private DecodedInformation parseBlocks() throws FormatException {
/*     */     BlockParsedResult result;
/*     */     while (true) {
/*     */       boolean isFinished;
/* 138 */       int initialPosition = this.current.getPosition();
/*     */       
/* 140 */       if (this.current.isAlpha()) {
/*     */         
/* 142 */         isFinished = (result = parseAlphaBlock()).isFinished();
/* 143 */       } else if (this.current.isIsoIec646()) {
/*     */         
/* 145 */         isFinished = (result = parseIsoIec646Block()).isFinished();
/*     */       } else {
/*     */         
/* 148 */         isFinished = (result = parseNumericBlock()).isFinished();
/*     */       } 
/*     */       
/* 151 */       if (((initialPosition != this.current.getPosition())) || 
/* 152 */         isFinished)
/*     */       
/*     */       { 
/* 155 */         if (isFinished)
/*     */           break;  continue; }  break;
/* 157 */     }  return result.getDecodedInformation();
/*     */   }
/*     */   
/*     */   private BlockParsedResult parseNumericBlock() throws FormatException {
/* 161 */     while (isStillNumeric(this.current.getPosition())) {
/* 162 */       DecodedNumeric numeric = decodeNumeric(this.current.getPosition());
/* 163 */       this.current.setPosition(numeric.getNewPosition());
/*     */       
/* 165 */       if (numeric.isFirstDigitFNC1()) {
/*     */         DecodedInformation information;
/* 167 */         if (numeric.isSecondDigitFNC1()) {
/* 168 */           information = new DecodedInformation(this.current.getPosition(), this.buffer.toString());
/*     */         } else {
/* 170 */           information = new DecodedInformation(this.current.getPosition(), this.buffer.toString(), numeric.getSecondDigit());
/*     */         } 
/* 172 */         return new BlockParsedResult(information, true);
/*     */       } 
/* 174 */       this.buffer.append(numeric.getFirstDigit());
/*     */       
/* 176 */       if (numeric.isSecondDigitFNC1()) {
/* 177 */         DecodedInformation information = new DecodedInformation(this.current.getPosition(), this.buffer.toString());
/* 178 */         return new BlockParsedResult(information, true);
/*     */       } 
/* 180 */       this.buffer.append(numeric.getSecondDigit());
/*     */     } 
/*     */     
/* 183 */     if (isNumericToAlphaNumericLatch(this.current.getPosition())) {
/* 184 */       this.current.setAlpha();
/* 185 */       this.current.incrementPosition(4);
/*     */     } 
/* 187 */     return new BlockParsedResult(false);
/*     */   }
/*     */   
/*     */   private BlockParsedResult parseIsoIec646Block() throws FormatException {
/* 191 */     while (isStillIsoIec646(this.current.getPosition())) {
/* 192 */       DecodedChar iso = decodeIsoIec646(this.current.getPosition());
/* 193 */       this.current.setPosition(iso.getNewPosition());
/*     */       
/* 195 */       if (iso.isFNC1()) {
/* 196 */         DecodedInformation information = new DecodedInformation(this.current.getPosition(), this.buffer.toString());
/* 197 */         return new BlockParsedResult(information, true);
/*     */       } 
/* 199 */       this.buffer.append(iso.getValue());
/*     */     } 
/*     */     
/* 202 */     if (isAlphaOr646ToNumericLatch(this.current.getPosition())) {
/* 203 */       this.current.incrementPosition(3);
/* 204 */       this.current.setNumeric();
/* 205 */     } else if (isAlphaTo646ToAlphaLatch(this.current.getPosition())) {
/* 206 */       if (this.current.getPosition() + 5 < this.information.getSize()) {
/* 207 */         this.current.incrementPosition(5);
/*     */       } else {
/* 209 */         this.current.setPosition(this.information.getSize());
/*     */       } 
/*     */       
/* 212 */       this.current.setAlpha();
/*     */     } 
/* 214 */     return new BlockParsedResult(false);
/*     */   }
/*     */   
/*     */   private BlockParsedResult parseAlphaBlock() {
/* 218 */     while (isStillAlpha(this.current.getPosition())) {
/* 219 */       DecodedChar alpha = decodeAlphanumeric(this.current.getPosition());
/* 220 */       this.current.setPosition(alpha.getNewPosition());
/*     */       
/* 222 */       if (alpha.isFNC1()) {
/* 223 */         DecodedInformation information = new DecodedInformation(this.current.getPosition(), this.buffer.toString());
/* 224 */         return new BlockParsedResult(information, true);
/*     */       } 
/*     */       
/* 227 */       this.buffer.append(alpha.getValue());
/*     */     } 
/*     */     
/* 230 */     if (isAlphaOr646ToNumericLatch(this.current.getPosition())) {
/* 231 */       this.current.incrementPosition(3);
/* 232 */       this.current.setNumeric();
/* 233 */     } else if (isAlphaTo646ToAlphaLatch(this.current.getPosition())) {
/* 234 */       if (this.current.getPosition() + 5 < this.information.getSize()) {
/* 235 */         this.current.incrementPosition(5);
/*     */       } else {
/* 237 */         this.current.setPosition(this.information.getSize());
/*     */       } 
/*     */       
/* 240 */       this.current.setIsoIec646();
/*     */     } 
/* 242 */     return new BlockParsedResult(false);
/*     */   }
/*     */   
/*     */   private boolean isStillIsoIec646(int pos) {
/* 246 */     if (pos + 5 > this.information.getSize()) {
/* 247 */       return false;
/*     */     }
/*     */     
/*     */     int fiveBitValue;
/* 251 */     if ((fiveBitValue = extractNumericValueFromBitArray(pos, 5)) >= 5 && fiveBitValue < 16) {
/* 252 */       return true;
/*     */     }
/*     */     
/* 255 */     if (pos + 7 > this.information.getSize()) {
/* 256 */       return false;
/*     */     }
/*     */     
/*     */     int sevenBitValue;
/* 260 */     if ((sevenBitValue = extractNumericValueFromBitArray(pos, 7)) >= 64 && sevenBitValue < 116) {
/* 261 */       return true;
/*     */     }
/*     */     
/* 264 */     if (pos + 8 > this.information.getSize()) {
/* 265 */       return false;
/*     */     }
/*     */     
/*     */     int eightBitValue;
/* 269 */     if ((eightBitValue = extractNumericValueFromBitArray(pos, 8)) >= 232 && eightBitValue < 253) return true;  return false;
/*     */   }
/*     */   
/*     */   private DecodedChar decodeIsoIec646(int pos) throws FormatException {
/*     */     char c;
/*     */     int fiveBitValue;
/* 275 */     if ((fiveBitValue = extractNumericValueFromBitArray(pos, 5)) == 15) {
/* 276 */       return new DecodedChar(pos + 5, '$');
/*     */     }
/*     */     
/* 279 */     if (fiveBitValue >= 5 && fiveBitValue < 15) {
/* 280 */       return new DecodedChar(pos + 5, (char)(fiveBitValue + 48 - 5));
/*     */     }
/*     */     
/*     */     int sevenBitValue;
/*     */     
/* 285 */     if ((sevenBitValue = extractNumericValueFromBitArray(pos, 7)) >= 64 && sevenBitValue < 90) {
/* 286 */       return new DecodedChar(pos + 7, (char)(sevenBitValue + 1));
/*     */     }
/*     */     
/* 289 */     if (sevenBitValue >= 90 && sevenBitValue < 116) {
/* 290 */       return new DecodedChar(pos + 7, (char)(sevenBitValue + 7));
/*     */     }
/*     */     
/* 293 */     switch (extractNumericValueFromBitArray(pos, 8)) {
/*     */ 
/*     */       
/*     */       case 232:
/* 297 */         c = '!';
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 362 */         return new DecodedChar(pos + 8, c);case 233: c = '"'; return new DecodedChar(pos + 8, c);case 234: c = '%'; return new DecodedChar(pos + 8, c);case 235: c = '&'; return new DecodedChar(pos + 8, c);case 236: c = '\''; return new DecodedChar(pos + 8, c);case 237: c = '('; return new DecodedChar(pos + 8, c);case 238: c = ')'; return new DecodedChar(pos + 8, c);case 239: c = '*'; return new DecodedChar(pos + 8, c);case 240: c = '+'; return new DecodedChar(pos + 8, c);case 241: c = ','; return new DecodedChar(pos + 8, c);case 242: c = '-'; return new DecodedChar(pos + 8, c);case 243: c = '.'; return new DecodedChar(pos + 8, c);case 244: c = '/'; return new DecodedChar(pos + 8, c);case 245: c = ':'; return new DecodedChar(pos + 8, c);case 246: c = ';'; return new DecodedChar(pos + 8, c);case 247: c = '<'; return new DecodedChar(pos + 8, c);case 248: c = '='; return new DecodedChar(pos + 8, c);case 249: c = '>'; return new DecodedChar(pos + 8, c);case 250: c = '?'; return new DecodedChar(pos + 8, c);case 251: c = '_'; return new DecodedChar(pos + 8, c);case 252: c = ' '; return new DecodedChar(pos + 8, c);
/*     */     } 
/*     */     throw FormatException.getFormatInstance();
/*     */   } private boolean isStillAlpha(int pos) {
/* 366 */     if (pos + 5 > this.information.getSize()) {
/* 367 */       return false;
/*     */     }
/*     */     
/*     */     int fiveBitValue;
/*     */     
/* 372 */     if ((fiveBitValue = extractNumericValueFromBitArray(pos, 5)) >= 5 && fiveBitValue < 16) {
/* 373 */       return true;
/*     */     }
/*     */     
/* 376 */     if (pos + 6 > this.information.getSize()) {
/* 377 */       return false;
/*     */     }
/*     */     
/*     */     int sixBitValue;
/* 381 */     if ((sixBitValue = extractNumericValueFromBitArray(pos, 6)) >= 16 && sixBitValue < 63) return true;  return false;
/*     */   }
/*     */   private DecodedChar decodeAlphanumeric(int pos) {
/*     */     char c;
/*     */     int fiveBitValue;
/* 386 */     if ((fiveBitValue = extractNumericValueFromBitArray(pos, 5)) == 15) {
/* 387 */       return new DecodedChar(pos + 5, '$');
/*     */     }
/*     */     
/* 390 */     if (fiveBitValue >= 5 && fiveBitValue < 15) {
/* 391 */       return new DecodedChar(pos + 5, (char)(fiveBitValue + 48 - 5));
/*     */     }
/*     */     
/*     */     int sixBitValue;
/*     */     
/* 396 */     if ((sixBitValue = extractNumericValueFromBitArray(pos, 6)) >= 32 && sixBitValue < 58) {
/* 397 */       return new DecodedChar(pos + 6, (char)(sixBitValue + 33));
/*     */     }
/*     */ 
/*     */     
/* 401 */     switch (sixBitValue) {
/*     */       case 58:
/* 403 */         c = '*';
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 420 */         return new DecodedChar(pos + 6, c);case 59: c = ','; return new DecodedChar(pos + 6, c);case 60: c = '-'; return new DecodedChar(pos + 6, c);case 61: c = '.'; return new DecodedChar(pos + 6, c);case 62: c = '/'; return new DecodedChar(pos + 6, c);
/*     */     } 
/*     */     throw new IllegalStateException("Decoding invalid alphanumeric value: " + sixBitValue);
/*     */   } private boolean isAlphaTo646ToAlphaLatch(int pos) {
/* 424 */     if (pos + 1 > this.information.getSize()) {
/* 425 */       return false;
/*     */     }
/*     */     
/* 428 */     for (int i = 0; i < 5 && i + pos < this.information.getSize(); i++) {
/* 429 */       if (i == 2) {
/* 430 */         if (!this.information.get(pos + 2)) {
/* 431 */           return false;
/*     */         }
/* 433 */       } else if (this.information.get(pos + i)) {
/* 434 */         return false;
/*     */       } 
/*     */     } 
/*     */     
/* 438 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   private boolean isAlphaOr646ToNumericLatch(int pos) {
/* 443 */     if (pos + 3 > this.information.getSize()) {
/* 444 */       return false;
/*     */     }
/*     */     
/* 447 */     for (int i = pos; i < pos + 3; i++) {
/* 448 */       if (this.information.get(i)) {
/* 449 */         return false;
/*     */       }
/*     */     } 
/* 452 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean isNumericToAlphaNumericLatch(int pos) {
/* 458 */     if (pos + 1 > this.information.getSize()) {
/* 459 */       return false;
/*     */     }
/*     */     
/* 462 */     for (int i = 0; i < 4 && i + pos < this.information.getSize(); i++) {
/* 463 */       if (this.information.get(pos + i)) {
/* 464 */         return false;
/*     */       }
/*     */     } 
/* 467 */     return true;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\zxing\oned\rss\expanded\decoders\GeneralAppIdDecoder.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */