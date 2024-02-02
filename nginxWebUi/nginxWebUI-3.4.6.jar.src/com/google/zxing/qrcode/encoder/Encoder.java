/*     */ package com.google.zxing.qrcode.encoder;
/*     */ 
/*     */ import com.google.zxing.EncodeHintType;
/*     */ import com.google.zxing.WriterException;
/*     */ import com.google.zxing.common.BitArray;
/*     */ import com.google.zxing.common.CharacterSetECI;
/*     */ import com.google.zxing.common.reedsolomon.GenericGF;
/*     */ import com.google.zxing.common.reedsolomon.ReedSolomonEncoder;
/*     */ import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
/*     */ import com.google.zxing.qrcode.decoder.Mode;
/*     */ import com.google.zxing.qrcode.decoder.Version;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.Iterator;
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
/*     */ public final class Encoder
/*     */ {
/*  41 */   private static final int[] ALPHANUMERIC_TABLE = new int[] { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 36, -1, -1, -1, 37, 38, -1, -1, -1, -1, 39, 40, -1, 41, 42, 43, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 44, -1, -1, -1, -1, -1, -1, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, -1, -1, -1, -1, -1 };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static final String DEFAULT_BYTE_MODE_ENCODING = "ISO-8859-1";
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static int calculateMaskPenalty(ByteMatrix matrix) {
/*  58 */     return MaskUtil.applyMaskPenaltyRule1(matrix) + 
/*  59 */       MaskUtil.applyMaskPenaltyRule2(matrix) + 
/*  60 */       MaskUtil.applyMaskPenaltyRule3(matrix) + 
/*  61 */       MaskUtil.applyMaskPenaltyRule4(matrix);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static QRCode encode(String content, ErrorCorrectionLevel ecLevel) throws WriterException {
/*  72 */     return encode(content, ecLevel, null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static QRCode encode(String content, ErrorCorrectionLevel ecLevel, Map<EncodeHintType, ?> hints) throws WriterException {
/*     */     Version version;
/*  80 */     String encoding = "ISO-8859-1";
/*  81 */     if (hints != null && hints.containsKey(EncodeHintType.CHARACTER_SET)) {
/*  82 */       encoding = hints.get(EncodeHintType.CHARACTER_SET).toString();
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*  87 */     Mode mode = chooseMode(content, encoding);
/*     */ 
/*     */ 
/*     */     
/*  91 */     BitArray headerBits = new BitArray();
/*     */     
/*     */     CharacterSetECI eci;
/*  94 */     if (mode == Mode.BYTE && !"ISO-8859-1".equals(encoding) && (
/*     */       
/*  96 */       eci = CharacterSetECI.getCharacterSetECIByName(encoding)) != null) {
/*  97 */       appendECI(eci, headerBits);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 102 */     appendModeInfo(mode, headerBits);
/*     */ 
/*     */ 
/*     */     
/* 106 */     BitArray dataBits = new BitArray();
/* 107 */     appendBytes(content, mode, dataBits, encoding);
/*     */ 
/*     */     
/* 110 */     if (hints != null && hints.containsKey(EncodeHintType.QR_VERSION)) {
/*     */       
/* 112 */       version = Version.getVersionForNumber(Integer.parseInt(hints.get(EncodeHintType.QR_VERSION).toString()));
/*     */       
/* 114 */       if (!willFit(calculateBitsNeeded(mode, headerBits, dataBits, version), version, ecLevel)) {
/* 115 */         throw new WriterException("Data too big for requested version");
/*     */       }
/*     */     } else {
/* 118 */       version = recommendVersion(ecLevel, mode, headerBits, dataBits);
/*     */     } 
/*     */     
/*     */     BitArray headerAndDataBits;
/* 122 */     (headerAndDataBits = new BitArray()).appendBitArray(headerBits);
/*     */ 
/*     */     
/* 125 */     appendLengthInfo((mode == Mode.BYTE) ? dataBits.getSizeInBytes() : content.length(), version, mode, headerAndDataBits);
/*     */     
/* 127 */     headerAndDataBits.appendBitArray(dataBits);
/*     */     
/* 129 */     Version.ECBlocks ecBlocks = version.getECBlocksForLevel(ecLevel);
/*     */     
/*     */     int numDataBytes;
/*     */     
/* 133 */     terminateBits(numDataBytes = version.getTotalCodewords() - ecBlocks.getTotalECCodewords(), headerAndDataBits);
/*     */ 
/*     */     
/* 136 */     BitArray finalBits = interleaveWithECBytes(headerAndDataBits, version
/* 137 */         .getTotalCodewords(), numDataBytes, ecBlocks
/*     */         
/* 139 */         .getNumBlocks());
/*     */     
/*     */     QRCode qrCode;
/*     */     
/* 143 */     (qrCode = new QRCode()).setECLevel(ecLevel);
/* 144 */     qrCode.setMode(mode);
/* 145 */     qrCode.setVersion(version);
/*     */ 
/*     */     
/* 148 */     int dimension = version.getDimensionForVersion();
/* 149 */     ByteMatrix matrix = new ByteMatrix(dimension, dimension);
/* 150 */     int maskPattern = chooseMaskPattern(finalBits, ecLevel, version, matrix);
/* 151 */     qrCode.setMaskPattern(maskPattern);
/*     */ 
/*     */     
/* 154 */     MatrixUtil.buildMatrix(finalBits, ecLevel, version, maskPattern, matrix);
/* 155 */     qrCode.setMatrix(matrix);
/*     */     
/* 157 */     return qrCode;
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
/*     */   private static Version recommendVersion(ErrorCorrectionLevel ecLevel, Mode mode, BitArray headerBits, BitArray dataBits) throws WriterException {
/* 173 */     Version provisionalVersion = chooseVersion(calculateBitsNeeded(mode, headerBits, dataBits, Version.getVersionForNumber(1)), ecLevel);
/*     */ 
/*     */ 
/*     */     
/* 177 */     return chooseVersion(calculateBitsNeeded(mode, headerBits, dataBits, provisionalVersion), ecLevel);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static int calculateBitsNeeded(Mode mode, BitArray headerBits, BitArray dataBits, Version version) {
/* 184 */     return headerBits.getSize() + mode.getCharacterCountBits(version) + dataBits.getSize();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static int getAlphanumericCode(int code) {
/* 192 */     if (code < ALPHANUMERIC_TABLE.length) {
/* 193 */       return ALPHANUMERIC_TABLE[code];
/*     */     }
/* 195 */     return -1;
/*     */   }
/*     */   
/*     */   public static Mode chooseMode(String content) {
/* 199 */     return chooseMode(content, null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static Mode chooseMode(String content, String encoding) {
/* 207 */     if ("Shift_JIS".equals(encoding) && isOnlyDoubleByteKanji(content))
/*     */     {
/* 209 */       return Mode.KANJI;
/*     */     }
/* 211 */     boolean hasNumeric = false;
/* 212 */     boolean hasAlphanumeric = false;
/* 213 */     for (int i = 0; i < content.length(); i++) {
/*     */       char c;
/* 215 */       if ((c = content.charAt(i)) >= '0' && c <= '9') {
/* 216 */         hasNumeric = true;
/* 217 */       } else if (getAlphanumericCode(c) != -1) {
/* 218 */         hasAlphanumeric = true;
/*     */       } else {
/* 220 */         return Mode.BYTE;
/*     */       } 
/*     */     } 
/* 223 */     if (hasAlphanumeric) {
/* 224 */       return Mode.ALPHANUMERIC;
/*     */     }
/* 226 */     if (hasNumeric) {
/* 227 */       return Mode.NUMERIC;
/*     */     }
/* 229 */     return Mode.BYTE;
/*     */   }
/*     */   
/*     */   private static boolean isOnlyDoubleByteKanji(String content) {
/*     */     byte[] bytes;
/*     */     try {
/* 235 */       bytes = content.getBytes("Shift_JIS");
/* 236 */     } catch (UnsupportedEncodingException unsupportedEncodingException) {
/* 237 */       return false;
/*     */     } 
/*     */     int length;
/* 240 */     if ((length = bytes.length) % 2 != 0) {
/* 241 */       return false;
/*     */     }
/* 243 */     for (int i = 0; i < length; i += 2) {
/*     */       int byte1;
/* 245 */       if (((byte1 = bytes[i] & 0xFF) < 129 || byte1 > 159) && (byte1 < 224 || byte1 > 235)) {
/* 246 */         return false;
/*     */       }
/*     */     } 
/* 249 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static int chooseMaskPattern(BitArray bits, ErrorCorrectionLevel ecLevel, Version version, ByteMatrix matrix) throws WriterException {
/* 257 */     int minPenalty = Integer.MAX_VALUE;
/* 258 */     int bestMaskPattern = -1;
/*     */     
/* 260 */     for (int maskPattern = 0; maskPattern < 8; maskPattern++) {
/* 261 */       MatrixUtil.buildMatrix(bits, ecLevel, version, maskPattern, matrix);
/*     */       int penalty;
/* 263 */       if ((penalty = calculateMaskPenalty(matrix)) < minPenalty) {
/* 264 */         minPenalty = penalty;
/* 265 */         bestMaskPattern = maskPattern;
/*     */       } 
/*     */     } 
/* 268 */     return bestMaskPattern;
/*     */   }
/*     */   
/*     */   private static Version chooseVersion(int numInputBits, ErrorCorrectionLevel ecLevel) throws WriterException {
/* 272 */     for (int versionNum = 1; versionNum <= 40; versionNum++) {
/* 273 */       Version version = Version.getVersionForNumber(versionNum);
/* 274 */       if (willFit(numInputBits, version, ecLevel)) {
/* 275 */         return version;
/*     */       }
/*     */     } 
/* 278 */     throw new WriterException("Data too big");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static boolean willFit(int numInputBits, Version version, ErrorCorrectionLevel ecLevel) {
/* 288 */     int numBytes = version.getTotalCodewords();
/*     */ 
/*     */     
/* 291 */     int numEcBytes = version.getECBlocksForLevel(ecLevel).getTotalECCodewords();
/*     */     
/* 293 */     int numDataBytes = numBytes - numEcBytes;
/* 294 */     int totalInputBytes = (numInputBits + 7) / 8;
/* 295 */     return (numDataBytes >= totalInputBytes);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static void terminateBits(int numDataBytes, BitArray bits) throws WriterException {
/* 302 */     int capacity = numDataBytes << 3;
/* 303 */     if (bits.getSize() > capacity) {
/* 304 */       throw new WriterException("data bits cannot fit in the QR Code" + bits.getSize() + " > " + capacity);
/*     */     }
/*     */     
/* 307 */     for (int i = 0; i < 4 && bits.getSize() < capacity; i++) {
/* 308 */       bits.appendBit(false);
/*     */     }
/*     */     
/*     */     int numBitsInLastByte;
/*     */     
/* 313 */     if ((numBitsInLastByte = bits.getSize() & 0x7) > 0) {
/* 314 */       for (int k = numBitsInLastByte; k < 8; k++) {
/* 315 */         bits.appendBit(false);
/*     */       }
/*     */     }
/*     */     
/* 319 */     int numPaddingBytes = numDataBytes - bits.getSizeInBytes();
/* 320 */     for (int j = 0; j < numPaddingBytes; j++) {
/* 321 */       bits.appendBits(((j & 0x1) == 0) ? 236 : 17, 8);
/*     */     }
/* 323 */     if (bits.getSize() != capacity) {
/* 324 */       throw new WriterException("Bits size does not equal capacity");
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
/*     */   static void getNumDataBytesAndNumECBytesForBlockID(int numTotalBytes, int numDataBytes, int numRSBlocks, int blockID, int[] numDataBytesInBlock, int[] numECBytesInBlock) throws WriterException {
/* 339 */     if (blockID >= numRSBlocks) {
/* 340 */       throw new WriterException("Block ID too large");
/*     */     }
/*     */     
/* 343 */     int numRsBlocksInGroup2 = numTotalBytes % numRSBlocks;
/*     */     
/* 345 */     int numRsBlocksInGroup1 = numRSBlocks - numRsBlocksInGroup2;
/*     */ 
/*     */ 
/*     */     
/* 349 */     int numTotalBytesInGroup1, numTotalBytesInGroup2 = (numTotalBytesInGroup1 = numTotalBytes / numRSBlocks) + 1;
/*     */ 
/*     */ 
/*     */     
/* 353 */     int numDataBytesInGroup1, numDataBytesInGroup2 = (numDataBytesInGroup1 = numDataBytes / numRSBlocks) + 1;
/*     */     
/* 355 */     int numEcBytesInGroup1 = numTotalBytesInGroup1 - numDataBytesInGroup1;
/*     */     
/* 357 */     int numEcBytesInGroup2 = numTotalBytesInGroup2 - numDataBytesInGroup2;
/*     */ 
/*     */     
/* 360 */     if (numEcBytesInGroup1 != numEcBytesInGroup2) {
/* 361 */       throw new WriterException("EC bytes mismatch");
/*     */     }
/*     */     
/* 364 */     if (numRSBlocks != numRsBlocksInGroup1 + numRsBlocksInGroup2) {
/* 365 */       throw new WriterException("RS blocks mismatch");
/*     */     }
/*     */     
/* 368 */     if (numTotalBytes != (numDataBytesInGroup1 + numEcBytesInGroup1) * numRsBlocksInGroup1 + (numDataBytesInGroup2 + numEcBytesInGroup2) * numRsBlocksInGroup2)
/*     */     {
/*     */ 
/*     */ 
/*     */       
/* 373 */       throw new WriterException("Total bytes mismatch");
/*     */     }
/*     */     
/* 376 */     if (blockID < numRsBlocksInGroup1) {
/* 377 */       numDataBytesInBlock[0] = numDataBytesInGroup1;
/* 378 */       numECBytesInBlock[0] = numEcBytesInGroup1; return;
/*     */     } 
/* 380 */     numDataBytesInBlock[0] = numDataBytesInGroup2;
/* 381 */     numECBytesInBlock[0] = numEcBytesInGroup2;
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
/*     */   static BitArray interleaveWithECBytes(BitArray bits, int numTotalBytes, int numDataBytes, int numRSBlocks) throws WriterException {
/* 395 */     if (bits.getSizeInBytes() != numDataBytes) {
/* 396 */       throw new WriterException("Number of bits and data bytes does not match");
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 401 */     int dataBytesOffset = 0;
/* 402 */     int maxNumDataBytes = 0;
/* 403 */     int maxNumEcBytes = 0;
/*     */ 
/*     */     
/* 406 */     Collection<BlockPair> blocks = new ArrayList<>(numRSBlocks);
/*     */     
/* 408 */     for (int i = 0; i < numRSBlocks; i++) {
/* 409 */       int[] numDataBytesInBlock = new int[1];
/* 410 */       int[] numEcBytesInBlock = new int[1];
/* 411 */       getNumDataBytesAndNumECBytesForBlockID(numTotalBytes, numDataBytes, numRSBlocks, i, numDataBytesInBlock, numEcBytesInBlock);
/*     */ 
/*     */       
/*     */       int size;
/*     */       
/* 416 */       byte[] dataBytes = new byte[size = numDataBytesInBlock[0]];
/* 417 */       bits.toBytes(dataBytesOffset << 3, dataBytes, 0, size);
/* 418 */       byte[] ecBytes = generateECBytes(dataBytes, numEcBytesInBlock[0]);
/* 419 */       blocks.add(new BlockPair(dataBytes, ecBytes));
/*     */       
/* 421 */       maxNumDataBytes = Math.max(maxNumDataBytes, size);
/* 422 */       maxNumEcBytes = Math.max(maxNumEcBytes, ecBytes.length);
/* 423 */       dataBytesOffset += numDataBytesInBlock[0];
/*     */     } 
/* 425 */     if (numDataBytes != dataBytesOffset) {
/* 426 */       throw new WriterException("Data bytes does not match offset");
/*     */     }
/*     */     
/* 429 */     BitArray result = new BitArray();
/*     */     
/*     */     int j;
/* 432 */     for (j = 0; j < maxNumDataBytes; j++) {
/* 433 */       for (Iterator<BlockPair> iterator = blocks.iterator(); iterator.hasNext(); ) {
/* 434 */         byte[] dataBytes = ((BlockPair)iterator.next()).getDataBytes();
/* 435 */         if (j < dataBytes.length) {
/* 436 */           result.appendBits(dataBytes[j], 8);
/*     */         }
/*     */       } 
/*     */     } 
/*     */     
/* 441 */     for (j = 0; j < maxNumEcBytes; j++) {
/* 442 */       for (Iterator<BlockPair> iterator = blocks.iterator(); iterator.hasNext(); ) {
/* 443 */         byte[] ecBytes = ((BlockPair)iterator.next()).getErrorCorrectionBytes();
/* 444 */         if (j < ecBytes.length) {
/* 445 */           result.appendBits(ecBytes[j], 8);
/*     */         }
/*     */       } 
/*     */     } 
/* 449 */     if (numTotalBytes != result.getSizeInBytes()) {
/* 450 */       throw new WriterException("Interleaving error: " + numTotalBytes + " and " + result
/* 451 */           .getSizeInBytes() + " differ.");
/*     */     }
/*     */     
/* 454 */     return result;
/*     */   }
/*     */ 
/*     */   
/*     */   static byte[] generateECBytes(byte[] dataBytes, int numEcBytesInBlock) {
/* 459 */     int numDataBytes, toEncode[] = new int[(numDataBytes = dataBytes.length) + numEcBytesInBlock];
/* 460 */     for (int i = 0; i < numDataBytes; i++) {
/* 461 */       toEncode[i] = dataBytes[i] & 0xFF;
/*     */     }
/* 463 */     (new ReedSolomonEncoder(GenericGF.QR_CODE_FIELD_256)).encode(toEncode, numEcBytesInBlock);
/*     */     
/* 465 */     byte[] ecBytes = new byte[numEcBytesInBlock];
/* 466 */     for (int j = 0; j < numEcBytesInBlock; j++) {
/* 467 */       ecBytes[j] = (byte)toEncode[numDataBytes + j];
/*     */     }
/* 469 */     return ecBytes;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static void appendModeInfo(Mode mode, BitArray bits) {
/* 476 */     bits.appendBits(mode.getBits(), 4);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static void appendLengthInfo(int numLetters, Version version, Mode mode, BitArray bits) throws WriterException {
/* 484 */     int numBits = mode.getCharacterCountBits(version);
/* 485 */     if (numLetters >= 1 << numBits) {
/* 486 */       throw new WriterException(numLetters + " is bigger than " + ((1 << numBits) - 1));
/*     */     }
/* 488 */     bits.appendBits(numLetters, numBits);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static void appendBytes(String content, Mode mode, BitArray bits, String encoding) throws WriterException {
/* 498 */     switch (mode) {
/*     */       case NUMERIC:
/* 500 */         appendNumericBytes(content, bits);
/*     */         return;
/*     */       case ALPHANUMERIC:
/* 503 */         appendAlphanumericBytes(content, bits);
/*     */         return;
/*     */       case BYTE:
/* 506 */         append8BitBytes(content, bits, encoding);
/*     */         return;
/*     */       case KANJI:
/* 509 */         appendKanjiBytes(content, bits);
/*     */         return;
/*     */     } 
/* 512 */     throw new WriterException("Invalid mode: " + mode);
/*     */   }
/*     */ 
/*     */   
/*     */   static void appendNumericBytes(CharSequence content, BitArray bits) {
/* 517 */     int length = content.length();
/* 518 */     int i = 0;
/* 519 */     while (i < length) {
/* 520 */       int num1 = content.charAt(i) - 48;
/* 521 */       if (i + 2 < length) {
/*     */         
/* 523 */         int num2 = content.charAt(i + 1) - 48;
/* 524 */         int num3 = content.charAt(i + 2) - 48;
/* 525 */         bits.appendBits(num1 * 100 + num2 * 10 + num3, 10);
/* 526 */         i += 3; continue;
/* 527 */       }  if (i + 1 < length) {
/*     */         
/* 529 */         int num2 = content.charAt(i + 1) - 48;
/* 530 */         bits.appendBits(num1 * 10 + num2, 7);
/* 531 */         i += 2;
/*     */         continue;
/*     */       } 
/* 534 */       bits.appendBits(num1, 4);
/* 535 */       i++;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   static void appendAlphanumericBytes(CharSequence content, BitArray bits) throws WriterException {
/* 541 */     int length = content.length();
/* 542 */     int i = 0;
/* 543 */     while (i < length) {
/*     */       int code1;
/* 545 */       if ((code1 = getAlphanumericCode(content.charAt(i))) == -1) {
/* 546 */         throw new WriterException();
/*     */       }
/* 548 */       if (i + 1 < length) {
/*     */         int code2;
/* 550 */         if ((code2 = getAlphanumericCode(content.charAt(i + 1))) == -1) {
/* 551 */           throw new WriterException();
/*     */         }
/*     */         
/* 554 */         bits.appendBits(code1 * 45 + code2, 11);
/* 555 */         i += 2;
/*     */         continue;
/*     */       } 
/* 558 */       bits.appendBits(code1, 6);
/* 559 */       i++;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   static void append8BitBytes(String content, BitArray bits, String encoding) throws WriterException {
/*     */     byte[] bytes;
/*     */     try {
/* 568 */       bytes = content.getBytes(encoding);
/* 569 */     } catch (UnsupportedEncodingException uee) {
/* 570 */       throw new WriterException(uee);
/*     */     }  byte[] arrayOfByte1; int i; byte b;
/* 572 */     for (i = (arrayOfByte1 = bytes).length, b = 0; b < i; ) { byte b1 = arrayOfByte1[b];
/* 573 */       bits.appendBits(b1, 8);
/*     */       b++; }
/*     */   
/*     */   }
/*     */   static void appendKanjiBytes(String content, BitArray bits) throws WriterException {
/*     */     byte[] bytes;
/*     */     try {
/* 580 */       bytes = content.getBytes("Shift_JIS");
/* 581 */     } catch (UnsupportedEncodingException uee) {
/* 582 */       throw new WriterException(uee);
/*     */     } 
/* 584 */     int length = bytes.length;
/* 585 */     for (int i = 0; i < length; i += 2) {
/* 586 */       int byte1 = bytes[i] & 0xFF;
/* 587 */       int byte2 = bytes[i + 1] & 0xFF;
/* 588 */       int code = byte1 << 8 | byte2;
/* 589 */       int subtracted = -1;
/* 590 */       if (code >= 33088 && code <= 40956) {
/* 591 */         subtracted = code - 33088;
/* 592 */       } else if (code >= 57408 && code <= 60351) {
/* 593 */         subtracted = code - 49472;
/*     */       } 
/* 595 */       if (subtracted == -1) {
/* 596 */         throw new WriterException("Invalid byte sequence");
/*     */       }
/* 598 */       int encoded = (subtracted >> 8) * 192 + (subtracted & 0xFF);
/* 599 */       bits.appendBits(encoded, 13);
/*     */     } 
/*     */   }
/*     */   
/*     */   private static void appendECI(CharacterSetECI eci, BitArray bits) {
/* 604 */     bits.appendBits(Mode.ECI.getBits(), 4);
/*     */     
/* 606 */     bits.appendBits(eci.getValue(), 8);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\zxing\qrcode\encoder\Encoder.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */