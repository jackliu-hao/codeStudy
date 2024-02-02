/*     */ package org.apache.commons.codec.binary;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Base32
/*     */   extends BaseNCodec
/*     */ {
/*     */   private static final int BITS_PER_ENCODED_BYTE = 5;
/*     */   private static final int BYTES_PER_ENCODED_BLOCK = 8;
/*     */   private static final int BYTES_PER_UNENCODED_BLOCK = 5;
/*  59 */   private static final byte[] CHUNK_SEPARATOR = new byte[] { 13, 10 };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  66 */   private static final byte[] DECODE_TABLE = new byte[] { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 26, 27, 28, 29, 30, 31, -1, -1, -1, -1, -1, -1, -1, -1, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, -1, -1, -1, -1, -1, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25 };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  83 */   private static final byte[] ENCODE_TABLE = new byte[] { 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 50, 51, 52, 53, 54, 55 };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  94 */   private static final byte[] HEX_DECODE_TABLE = new byte[] { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, -1, -1, -1, -1, -1, -1, -1, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31 };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 111 */   private static final byte[] HEX_ENCODE_TABLE = new byte[] { 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86 };
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static final long MASK_7BITS = 127L;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static final long MASK_6BITS = 63L;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static final int MASK_5BITS = 31;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static final long MASK_4BITS = 15L;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static final long MASK_3BITS = 7L;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static final long MASK_2BITS = 3L;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static final long MASK_1BITS = 1L;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final int decodeSize;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final byte[] decodeTable;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final int encodeSize;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final byte[] encodeTable;
/*     */ 
/*     */ 
/*     */   
/*     */   private final byte[] lineSeparator;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Base32() {
/* 176 */     this(false);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Base32(byte pad) {
/* 187 */     this(false, pad);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Base32(boolean useHex) {
/* 198 */     this(0, (byte[])null, useHex, (byte)61);
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
/*     */   public Base32(boolean useHex, byte pad) {
/* 210 */     this(0, (byte[])null, useHex, pad);
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
/*     */   public Base32(int lineLength) {
/* 225 */     this(lineLength, CHUNK_SEPARATOR);
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
/*     */   public Base32(int lineLength, byte[] lineSeparator) {
/* 247 */     this(lineLength, lineSeparator, false, (byte)61);
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
/*     */   public Base32(int lineLength, byte[] lineSeparator, boolean useHex) {
/* 272 */     this(lineLength, lineSeparator, useHex, (byte)61);
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
/*     */   public Base32(int lineLength, byte[] lineSeparator, boolean useHex, byte pad) {
/* 298 */     super(5, 8, lineLength, (lineSeparator == null) ? 0 : lineSeparator.length, pad);
/*     */     
/* 300 */     if (useHex) {
/* 301 */       this.encodeTable = HEX_ENCODE_TABLE;
/* 302 */       this.decodeTable = HEX_DECODE_TABLE;
/*     */     } else {
/* 304 */       this.encodeTable = ENCODE_TABLE;
/* 305 */       this.decodeTable = DECODE_TABLE;
/*     */     } 
/* 307 */     if (lineLength > 0) {
/* 308 */       if (lineSeparator == null) {
/* 309 */         throw new IllegalArgumentException("lineLength " + lineLength + " > 0, but lineSeparator is null");
/*     */       }
/*     */       
/* 312 */       if (containsAlphabetOrPad(lineSeparator)) {
/* 313 */         String sep = StringUtils.newStringUtf8(lineSeparator);
/* 314 */         throw new IllegalArgumentException("lineSeparator must not contain Base32 characters: [" + sep + "]");
/*     */       } 
/* 316 */       this.encodeSize = 8 + lineSeparator.length;
/* 317 */       this.lineSeparator = new byte[lineSeparator.length];
/* 318 */       System.arraycopy(lineSeparator, 0, this.lineSeparator, 0, lineSeparator.length);
/*     */     } else {
/* 320 */       this.encodeSize = 8;
/* 321 */       this.lineSeparator = null;
/*     */     } 
/* 323 */     this.decodeSize = this.encodeSize - 1;
/*     */     
/* 325 */     if (isInAlphabet(pad) || isWhiteSpace(pad)) {
/* 326 */       throw new IllegalArgumentException("pad must not be in alphabet or whitespace");
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
/*     */   void decode(byte[] input, int inPos, int inAvail, BaseNCodec.Context context) {
/* 356 */     if (context.eof) {
/*     */       return;
/*     */     }
/* 359 */     if (inAvail < 0) {
/* 360 */       context.eof = true;
/*     */     }
/* 362 */     for (int i = 0; i < inAvail; i++) {
/* 363 */       byte b = input[inPos++];
/* 364 */       if (b == this.pad) {
/*     */         
/* 366 */         context.eof = true;
/*     */         break;
/*     */       } 
/* 369 */       byte[] buffer = ensureBufferSize(this.decodeSize, context);
/* 370 */       if (b >= 0 && b < this.decodeTable.length) {
/* 371 */         int result = this.decodeTable[b];
/* 372 */         if (result >= 0) {
/* 373 */           context.modulus = (context.modulus + 1) % 8;
/*     */           
/* 375 */           context.lbitWorkArea = (context.lbitWorkArea << 5L) + result;
/* 376 */           if (context.modulus == 0) {
/* 377 */             buffer[context.pos++] = (byte)(int)(context.lbitWorkArea >> 32L & 0xFFL);
/* 378 */             buffer[context.pos++] = (byte)(int)(context.lbitWorkArea >> 24L & 0xFFL);
/* 379 */             buffer[context.pos++] = (byte)(int)(context.lbitWorkArea >> 16L & 0xFFL);
/* 380 */             buffer[context.pos++] = (byte)(int)(context.lbitWorkArea >> 8L & 0xFFL);
/* 381 */             buffer[context.pos++] = (byte)(int)(context.lbitWorkArea & 0xFFL);
/*     */           } 
/*     */         } 
/*     */       } 
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 390 */     if (context.eof && context.modulus >= 2) {
/* 391 */       byte[] buffer = ensureBufferSize(this.decodeSize, context);
/*     */ 
/*     */       
/* 394 */       switch (context.modulus) {
/*     */         case 2:
/* 396 */           validateCharacter(3L, context);
/* 397 */           buffer[context.pos++] = (byte)(int)(context.lbitWorkArea >> 2L & 0xFFL);
/*     */           return;
/*     */         case 3:
/* 400 */           validateCharacter(127L, context);
/* 401 */           buffer[context.pos++] = (byte)(int)(context.lbitWorkArea >> 7L & 0xFFL);
/*     */           return;
/*     */         case 4:
/* 404 */           validateCharacter(15L, context);
/* 405 */           context.lbitWorkArea >>= 4L;
/* 406 */           buffer[context.pos++] = (byte)(int)(context.lbitWorkArea >> 8L & 0xFFL);
/* 407 */           buffer[context.pos++] = (byte)(int)(context.lbitWorkArea & 0xFFL);
/*     */           return;
/*     */         case 5:
/* 410 */           validateCharacter(1L, context);
/* 411 */           context.lbitWorkArea >>= 1L;
/* 412 */           buffer[context.pos++] = (byte)(int)(context.lbitWorkArea >> 16L & 0xFFL);
/* 413 */           buffer[context.pos++] = (byte)(int)(context.lbitWorkArea >> 8L & 0xFFL);
/* 414 */           buffer[context.pos++] = (byte)(int)(context.lbitWorkArea & 0xFFL);
/*     */           return;
/*     */         case 6:
/* 417 */           validateCharacter(63L, context);
/* 418 */           context.lbitWorkArea >>= 6L;
/* 419 */           buffer[context.pos++] = (byte)(int)(context.lbitWorkArea >> 16L & 0xFFL);
/* 420 */           buffer[context.pos++] = (byte)(int)(context.lbitWorkArea >> 8L & 0xFFL);
/* 421 */           buffer[context.pos++] = (byte)(int)(context.lbitWorkArea & 0xFFL);
/*     */           return;
/*     */         case 7:
/* 424 */           validateCharacter(7L, context);
/* 425 */           context.lbitWorkArea >>= 3L;
/* 426 */           buffer[context.pos++] = (byte)(int)(context.lbitWorkArea >> 24L & 0xFFL);
/* 427 */           buffer[context.pos++] = (byte)(int)(context.lbitWorkArea >> 16L & 0xFFL);
/* 428 */           buffer[context.pos++] = (byte)(int)(context.lbitWorkArea >> 8L & 0xFFL);
/* 429 */           buffer[context.pos++] = (byte)(int)(context.lbitWorkArea & 0xFFL);
/*     */           return;
/*     */       } 
/*     */       
/* 433 */       throw new IllegalStateException("Impossible modulus " + context.modulus);
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
/*     */   void encode(byte[] input, int inPos, int inAvail, BaseNCodec.Context context) {
/* 457 */     if (context.eof) {
/*     */       return;
/*     */     }
/*     */ 
/*     */     
/* 462 */     if (inAvail < 0) {
/* 463 */       context.eof = true;
/* 464 */       if (0 == context.modulus && this.lineLength == 0) {
/*     */         return;
/*     */       }
/* 467 */       byte[] buffer = ensureBufferSize(this.encodeSize, context);
/* 468 */       int savedPos = context.pos;
/* 469 */       switch (context.modulus) {
/*     */         case 0:
/*     */           break;
/*     */         case 1:
/* 473 */           buffer[context.pos++] = this.encodeTable[(int)(context.lbitWorkArea >> 3L) & 0x1F];
/* 474 */           buffer[context.pos++] = this.encodeTable[(int)(context.lbitWorkArea << 2L) & 0x1F];
/* 475 */           buffer[context.pos++] = this.pad;
/* 476 */           buffer[context.pos++] = this.pad;
/* 477 */           buffer[context.pos++] = this.pad;
/* 478 */           buffer[context.pos++] = this.pad;
/* 479 */           buffer[context.pos++] = this.pad;
/* 480 */           buffer[context.pos++] = this.pad;
/*     */           break;
/*     */         case 2:
/* 483 */           buffer[context.pos++] = this.encodeTable[(int)(context.lbitWorkArea >> 11L) & 0x1F];
/* 484 */           buffer[context.pos++] = this.encodeTable[(int)(context.lbitWorkArea >> 6L) & 0x1F];
/* 485 */           buffer[context.pos++] = this.encodeTable[(int)(context.lbitWorkArea >> 1L) & 0x1F];
/* 486 */           buffer[context.pos++] = this.encodeTable[(int)(context.lbitWorkArea << 4L) & 0x1F];
/* 487 */           buffer[context.pos++] = this.pad;
/* 488 */           buffer[context.pos++] = this.pad;
/* 489 */           buffer[context.pos++] = this.pad;
/* 490 */           buffer[context.pos++] = this.pad;
/*     */           break;
/*     */         case 3:
/* 493 */           buffer[context.pos++] = this.encodeTable[(int)(context.lbitWorkArea >> 19L) & 0x1F];
/* 494 */           buffer[context.pos++] = this.encodeTable[(int)(context.lbitWorkArea >> 14L) & 0x1F];
/* 495 */           buffer[context.pos++] = this.encodeTable[(int)(context.lbitWorkArea >> 9L) & 0x1F];
/* 496 */           buffer[context.pos++] = this.encodeTable[(int)(context.lbitWorkArea >> 4L) & 0x1F];
/* 497 */           buffer[context.pos++] = this.encodeTable[(int)(context.lbitWorkArea << 1L) & 0x1F];
/* 498 */           buffer[context.pos++] = this.pad;
/* 499 */           buffer[context.pos++] = this.pad;
/* 500 */           buffer[context.pos++] = this.pad;
/*     */           break;
/*     */         case 4:
/* 503 */           buffer[context.pos++] = this.encodeTable[(int)(context.lbitWorkArea >> 27L) & 0x1F];
/* 504 */           buffer[context.pos++] = this.encodeTable[(int)(context.lbitWorkArea >> 22L) & 0x1F];
/* 505 */           buffer[context.pos++] = this.encodeTable[(int)(context.lbitWorkArea >> 17L) & 0x1F];
/* 506 */           buffer[context.pos++] = this.encodeTable[(int)(context.lbitWorkArea >> 12L) & 0x1F];
/* 507 */           buffer[context.pos++] = this.encodeTable[(int)(context.lbitWorkArea >> 7L) & 0x1F];
/* 508 */           buffer[context.pos++] = this.encodeTable[(int)(context.lbitWorkArea >> 2L) & 0x1F];
/* 509 */           buffer[context.pos++] = this.encodeTable[(int)(context.lbitWorkArea << 3L) & 0x1F];
/* 510 */           buffer[context.pos++] = this.pad;
/*     */           break;
/*     */         default:
/* 513 */           throw new IllegalStateException("Impossible modulus " + context.modulus);
/*     */       } 
/* 515 */       context.currentLinePos += context.pos - savedPos;
/*     */       
/* 517 */       if (this.lineLength > 0 && context.currentLinePos > 0) {
/* 518 */         System.arraycopy(this.lineSeparator, 0, buffer, context.pos, this.lineSeparator.length);
/* 519 */         context.pos += this.lineSeparator.length;
/*     */       } 
/*     */     } else {
/* 522 */       for (int i = 0; i < inAvail; i++) {
/* 523 */         byte[] buffer = ensureBufferSize(this.encodeSize, context);
/* 524 */         context.modulus = (context.modulus + 1) % 5;
/* 525 */         int b = input[inPos++];
/* 526 */         if (b < 0) {
/* 527 */           b += 256;
/*     */         }
/* 529 */         context.lbitWorkArea = (context.lbitWorkArea << 8L) + b;
/* 530 */         if (0 == context.modulus) {
/* 531 */           buffer[context.pos++] = this.encodeTable[(int)(context.lbitWorkArea >> 35L) & 0x1F];
/* 532 */           buffer[context.pos++] = this.encodeTable[(int)(context.lbitWorkArea >> 30L) & 0x1F];
/* 533 */           buffer[context.pos++] = this.encodeTable[(int)(context.lbitWorkArea >> 25L) & 0x1F];
/* 534 */           buffer[context.pos++] = this.encodeTable[(int)(context.lbitWorkArea >> 20L) & 0x1F];
/* 535 */           buffer[context.pos++] = this.encodeTable[(int)(context.lbitWorkArea >> 15L) & 0x1F];
/* 536 */           buffer[context.pos++] = this.encodeTable[(int)(context.lbitWorkArea >> 10L) & 0x1F];
/* 537 */           buffer[context.pos++] = this.encodeTable[(int)(context.lbitWorkArea >> 5L) & 0x1F];
/* 538 */           buffer[context.pos++] = this.encodeTable[(int)context.lbitWorkArea & 0x1F];
/* 539 */           context.currentLinePos += 8;
/* 540 */           if (this.lineLength > 0 && this.lineLength <= context.currentLinePos) {
/* 541 */             System.arraycopy(this.lineSeparator, 0, buffer, context.pos, this.lineSeparator.length);
/* 542 */             context.pos += this.lineSeparator.length;
/* 543 */             context.currentLinePos = 0;
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
/*     */   public boolean isInAlphabet(byte octet) {
/* 559 */     return (octet >= 0 && octet < this.decodeTable.length && this.decodeTable[octet] != -1);
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
/*     */   private static void validateCharacter(long emptyBitsMask, BaseNCodec.Context context) {
/* 576 */     if ((context.lbitWorkArea & emptyBitsMask) != 0L)
/* 577 */       throw new IllegalArgumentException("Last encoded character (before the paddings if any) is a valid base 32 alphabet but not a possible value. Expected the discarded bits to be zero."); 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\codec\binary\Base32.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */