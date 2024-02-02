/*     */ package org.apache.commons.codec.binary;
/*     */ 
/*     */ import java.math.BigInteger;
/*     */ import java.util.Objects;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Base64
/*     */   extends BaseNCodec
/*     */ {
/*     */   private static final int BITS_PER_ENCODED_BYTE = 6;
/*     */   private static final int BYTES_PER_UNENCODED_BLOCK = 3;
/*     */   private static final int BYTES_PER_ENCODED_BLOCK = 4;
/*  74 */   static final byte[] CHUNK_SEPARATOR = new byte[] { 13, 10 };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  83 */   private static final byte[] STANDARD_ENCODE_TABLE = new byte[] { 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 43, 47 };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  96 */   private static final byte[] URL_SAFE_ENCODE_TABLE = new byte[] { 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 45, 95 };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 115 */   private static final byte[] DECODE_TABLE = new byte[] { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 62, -1, 62, -1, 63, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, -1, -1, -1, -1, -1, -1, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, -1, -1, -1, -1, 63, -1, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51 };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static final int MASK_6BITS = 63;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static final int MASK_4BITS = 15;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static final int MASK_2BITS = 3;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final byte[] encodeTable;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 149 */   private final byte[] decodeTable = DECODE_TABLE;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final byte[] lineSeparator;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final int decodeSize;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final int encodeSize;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Base64() {
/* 179 */     this(0);
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
/*     */   public Base64(boolean urlSafe) {
/* 198 */     this(76, CHUNK_SEPARATOR, urlSafe);
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
/*     */   public Base64(int lineLength) {
/* 221 */     this(lineLength, CHUNK_SEPARATOR);
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
/*     */   public Base64(int lineLength, byte[] lineSeparator) {
/* 248 */     this(lineLength, lineSeparator, false);
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
/*     */   public Base64(int lineLength, byte[] lineSeparator, boolean urlSafe) {
/* 279 */     super(3, 4, lineLength, (lineSeparator == null) ? 0 : lineSeparator.length);
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 284 */     if (lineSeparator != null) {
/* 285 */       if (containsAlphabetOrPad(lineSeparator)) {
/* 286 */         String sep = StringUtils.newStringUtf8(lineSeparator);
/* 287 */         throw new IllegalArgumentException("lineSeparator must not contain base64 characters: [" + sep + "]");
/*     */       } 
/* 289 */       if (lineLength > 0) {
/* 290 */         this.encodeSize = 4 + lineSeparator.length;
/* 291 */         this.lineSeparator = new byte[lineSeparator.length];
/* 292 */         System.arraycopy(lineSeparator, 0, this.lineSeparator, 0, lineSeparator.length);
/*     */       } else {
/* 294 */         this.encodeSize = 4;
/* 295 */         this.lineSeparator = null;
/*     */       } 
/*     */     } else {
/* 298 */       this.encodeSize = 4;
/* 299 */       this.lineSeparator = null;
/*     */     } 
/* 301 */     this.decodeSize = this.encodeSize - 1;
/* 302 */     this.encodeTable = urlSafe ? URL_SAFE_ENCODE_TABLE : STANDARD_ENCODE_TABLE;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isUrlSafe() {
/* 312 */     return (this.encodeTable == URL_SAFE_ENCODE_TABLE);
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
/*     */   void encode(byte[] in, int inPos, int inAvail, BaseNCodec.Context context) {
/* 338 */     if (context.eof) {
/*     */       return;
/*     */     }
/*     */ 
/*     */     
/* 343 */     if (inAvail < 0) {
/* 344 */       context.eof = true;
/* 345 */       if (0 == context.modulus && this.lineLength == 0) {
/*     */         return;
/*     */       }
/* 348 */       byte[] buffer = ensureBufferSize(this.encodeSize, context);
/* 349 */       int savedPos = context.pos;
/* 350 */       switch (context.modulus) {
/*     */         case 0:
/*     */           break;
/*     */         
/*     */         case 1:
/* 355 */           buffer[context.pos++] = this.encodeTable[context.ibitWorkArea >> 2 & 0x3F];
/*     */           
/* 357 */           buffer[context.pos++] = this.encodeTable[context.ibitWorkArea << 4 & 0x3F];
/*     */           
/* 359 */           if (this.encodeTable == STANDARD_ENCODE_TABLE) {
/* 360 */             buffer[context.pos++] = this.pad;
/* 361 */             buffer[context.pos++] = this.pad;
/*     */           } 
/*     */           break;
/*     */         
/*     */         case 2:
/* 366 */           buffer[context.pos++] = this.encodeTable[context.ibitWorkArea >> 10 & 0x3F];
/* 367 */           buffer[context.pos++] = this.encodeTable[context.ibitWorkArea >> 4 & 0x3F];
/* 368 */           buffer[context.pos++] = this.encodeTable[context.ibitWorkArea << 2 & 0x3F];
/*     */           
/* 370 */           if (this.encodeTable == STANDARD_ENCODE_TABLE) {
/* 371 */             buffer[context.pos++] = this.pad;
/*     */           }
/*     */           break;
/*     */         default:
/* 375 */           throw new IllegalStateException("Impossible modulus " + context.modulus);
/*     */       } 
/* 377 */       context.currentLinePos += context.pos - savedPos;
/*     */       
/* 379 */       if (this.lineLength > 0 && context.currentLinePos > 0) {
/* 380 */         System.arraycopy(this.lineSeparator, 0, buffer, context.pos, this.lineSeparator.length);
/* 381 */         context.pos += this.lineSeparator.length;
/*     */       } 
/*     */     } else {
/* 384 */       for (int i = 0; i < inAvail; i++) {
/* 385 */         byte[] buffer = ensureBufferSize(this.encodeSize, context);
/* 386 */         context.modulus = (context.modulus + 1) % 3;
/* 387 */         int b = in[inPos++];
/* 388 */         if (b < 0) {
/* 389 */           b += 256;
/*     */         }
/* 391 */         context.ibitWorkArea = (context.ibitWorkArea << 8) + b;
/* 392 */         if (0 == context.modulus) {
/* 393 */           buffer[context.pos++] = this.encodeTable[context.ibitWorkArea >> 18 & 0x3F];
/* 394 */           buffer[context.pos++] = this.encodeTable[context.ibitWorkArea >> 12 & 0x3F];
/* 395 */           buffer[context.pos++] = this.encodeTable[context.ibitWorkArea >> 6 & 0x3F];
/* 396 */           buffer[context.pos++] = this.encodeTable[context.ibitWorkArea & 0x3F];
/* 397 */           context.currentLinePos += 4;
/* 398 */           if (this.lineLength > 0 && this.lineLength <= context.currentLinePos) {
/* 399 */             System.arraycopy(this.lineSeparator, 0, buffer, context.pos, this.lineSeparator.length);
/* 400 */             context.pos += this.lineSeparator.length;
/* 401 */             context.currentLinePos = 0;
/*     */           } 
/*     */         } 
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   void decode(byte[] in, int inPos, int inAvail, BaseNCodec.Context context) {
/* 435 */     if (context.eof) {
/*     */       return;
/*     */     }
/* 438 */     if (inAvail < 0) {
/* 439 */       context.eof = true;
/*     */     }
/* 441 */     for (int i = 0; i < inAvail; i++) {
/* 442 */       byte[] buffer = ensureBufferSize(this.decodeSize, context);
/* 443 */       byte b = in[inPos++];
/* 444 */       if (b == this.pad) {
/*     */         
/* 446 */         context.eof = true;
/*     */         break;
/*     */       } 
/* 449 */       if (b >= 0 && b < DECODE_TABLE.length) {
/* 450 */         int result = DECODE_TABLE[b];
/* 451 */         if (result >= 0) {
/* 452 */           context.modulus = (context.modulus + 1) % 4;
/* 453 */           context.ibitWorkArea = (context.ibitWorkArea << 6) + result;
/* 454 */           if (context.modulus == 0) {
/* 455 */             buffer[context.pos++] = (byte)(context.ibitWorkArea >> 16 & 0xFF);
/* 456 */             buffer[context.pos++] = (byte)(context.ibitWorkArea >> 8 & 0xFF);
/* 457 */             buffer[context.pos++] = (byte)(context.ibitWorkArea & 0xFF);
/*     */           } 
/*     */         } 
/*     */       } 
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 466 */     if (context.eof && context.modulus != 0) {
/* 467 */       byte[] buffer = ensureBufferSize(this.decodeSize, context);
/*     */ 
/*     */ 
/*     */       
/* 471 */       switch (context.modulus) {
/*     */         case 1:
/*     */           return;
/*     */ 
/*     */         
/*     */         case 2:
/* 477 */           validateCharacter(15, context);
/* 478 */           context.ibitWorkArea >>= 4;
/* 479 */           buffer[context.pos++] = (byte)(context.ibitWorkArea & 0xFF);
/*     */         
/*     */         case 3:
/* 482 */           validateCharacter(3, context);
/* 483 */           context.ibitWorkArea >>= 2;
/* 484 */           buffer[context.pos++] = (byte)(context.ibitWorkArea >> 8 & 0xFF);
/* 485 */           buffer[context.pos++] = (byte)(context.ibitWorkArea & 0xFF);
/*     */       } 
/*     */       
/* 488 */       throw new IllegalStateException("Impossible modulus " + context.modulus);
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
/*     */   @Deprecated
/*     */   public static boolean isArrayByteBase64(byte[] arrayOctet) {
/* 505 */     return isBase64(arrayOctet);
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
/*     */   public static boolean isBase64(byte octet) {
/* 517 */     return (octet == 61 || (octet >= 0 && octet < DECODE_TABLE.length && DECODE_TABLE[octet] != -1));
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
/*     */   public static boolean isBase64(String base64) {
/* 531 */     return isBase64(StringUtils.getBytesUtf8(base64));
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
/*     */   public static boolean isBase64(byte[] arrayOctet) {
/* 545 */     for (int i = 0; i < arrayOctet.length; i++) {
/* 546 */       if (!isBase64(arrayOctet[i]) && !isWhiteSpace(arrayOctet[i])) {
/* 547 */         return false;
/*     */       }
/*     */     } 
/* 550 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static byte[] encodeBase64(byte[] binaryData) {
/* 561 */     return encodeBase64(binaryData, false);
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
/*     */   public static String encodeBase64String(byte[] binaryData) {
/* 576 */     return StringUtils.newStringUsAscii(encodeBase64(binaryData, false));
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
/*     */   public static byte[] encodeBase64URLSafe(byte[] binaryData) {
/* 589 */     return encodeBase64(binaryData, false, true);
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
/*     */   public static String encodeBase64URLSafeString(byte[] binaryData) {
/* 602 */     return StringUtils.newStringUsAscii(encodeBase64(binaryData, false, true));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static byte[] encodeBase64Chunked(byte[] binaryData) {
/* 613 */     return encodeBase64(binaryData, true);
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
/*     */   public static byte[] encodeBase64(byte[] binaryData, boolean isChunked) {
/* 628 */     return encodeBase64(binaryData, isChunked, false);
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
/*     */   public static byte[] encodeBase64(byte[] binaryData, boolean isChunked, boolean urlSafe) {
/* 647 */     return encodeBase64(binaryData, isChunked, urlSafe, 2147483647);
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
/*     */   public static byte[] encodeBase64(byte[] binaryData, boolean isChunked, boolean urlSafe, int maxResultSize) {
/* 669 */     if (binaryData == null || binaryData.length == 0) {
/* 670 */       return binaryData;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 675 */     Base64 b64 = isChunked ? new Base64(urlSafe) : new Base64(0, CHUNK_SEPARATOR, urlSafe);
/* 676 */     long len = b64.getEncodedLength(binaryData);
/* 677 */     if (len > maxResultSize) {
/* 678 */       throw new IllegalArgumentException("Input array too big, the output array would be bigger (" + len + ") than the specified maximum size of " + maxResultSize);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 684 */     return b64.encode(binaryData);
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
/*     */   public static byte[] decodeBase64(String base64String) {
/* 699 */     return (new Base64()).decode(base64String);
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
/*     */   public static byte[] decodeBase64(byte[] base64Data) {
/* 713 */     return (new Base64()).decode(base64Data);
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
/*     */   public static BigInteger decodeInteger(byte[] pArray) {
/* 728 */     return new BigInteger(1, decodeBase64(pArray));
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
/*     */   public static byte[] encodeInteger(BigInteger bigInteger) {
/* 742 */     Objects.requireNonNull(bigInteger, "bigInteger");
/* 743 */     return encodeBase64(toIntegerBytes(bigInteger), false);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static byte[] toIntegerBytes(BigInteger bigInt) {
/* 754 */     int bitlen = bigInt.bitLength();
/*     */     
/* 756 */     bitlen = bitlen + 7 >> 3 << 3;
/* 757 */     byte[] bigBytes = bigInt.toByteArray();
/*     */     
/* 759 */     if (bigInt.bitLength() % 8 != 0 && bigInt.bitLength() / 8 + 1 == bitlen / 8) {
/* 760 */       return bigBytes;
/*     */     }
/*     */     
/* 763 */     int startSrc = 0;
/* 764 */     int len = bigBytes.length;
/*     */ 
/*     */     
/* 767 */     if (bigInt.bitLength() % 8 == 0) {
/* 768 */       startSrc = 1;
/* 769 */       len--;
/*     */     } 
/* 771 */     int startDst = bitlen / 8 - len;
/* 772 */     byte[] resizedBytes = new byte[bitlen / 8];
/* 773 */     System.arraycopy(bigBytes, startSrc, resizedBytes, startDst, len);
/* 774 */     return resizedBytes;
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
/*     */   protected boolean isInAlphabet(byte octet) {
/* 786 */     return (octet >= 0 && octet < this.decodeTable.length && this.decodeTable[octet] != -1);
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
/*     */   private static void validateCharacter(int emptyBitsMask, BaseNCodec.Context context) {
/* 802 */     if ((context.ibitWorkArea & emptyBitsMask) != 0)
/* 803 */       throw new IllegalArgumentException("Last encoded character (before the paddings if any) is a valid base 64 alphabet but not a possible value. Expected the discarded bits to be zero."); 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\codec\binary\Base64.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */