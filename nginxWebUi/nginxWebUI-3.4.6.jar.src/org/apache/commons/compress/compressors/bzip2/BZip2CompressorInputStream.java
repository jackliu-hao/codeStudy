/*     */ package org.apache.commons.compress.compressors.bzip2;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.nio.ByteOrder;
/*     */ import java.util.Arrays;
/*     */ import org.apache.commons.compress.compressors.CompressorInputStream;
/*     */ import org.apache.commons.compress.utils.BitInputStream;
/*     */ import org.apache.commons.compress.utils.CloseShieldFilterInputStream;
/*     */ import org.apache.commons.compress.utils.InputStreamStatistics;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class BZip2CompressorInputStream
/*     */   extends CompressorInputStream
/*     */   implements BZip2Constants, InputStreamStatistics
/*     */ {
/*     */   private int last;
/*     */   private int origPtr;
/*     */   private int blockSize100k;
/*     */   private boolean blockRandomised;
/*  63 */   private final CRC crc = new CRC();
/*     */   
/*     */   private int nInUse;
/*     */   
/*     */   private BitInputStream bin;
/*     */   
/*     */   private final boolean decompressConcatenated;
/*     */   
/*     */   private static final int EOF = 0;
/*     */   private static final int START_BLOCK_STATE = 1;
/*     */   private static final int RAND_PART_A_STATE = 2;
/*     */   private static final int RAND_PART_B_STATE = 3;
/*     */   private static final int RAND_PART_C_STATE = 4;
/*     */   private static final int NO_RAND_PART_A_STATE = 5;
/*     */   private static final int NO_RAND_PART_B_STATE = 6;
/*     */   private static final int NO_RAND_PART_C_STATE = 7;
/*  79 */   private int currentState = 1;
/*     */ 
/*     */   
/*     */   private int storedBlockCRC;
/*     */ 
/*     */   
/*     */   private int storedCombinedCRC;
/*     */   
/*     */   private int computedBlockCRC;
/*     */   
/*     */   private int computedCombinedCRC;
/*     */   
/*     */   private int su_count;
/*     */   
/*     */   private int su_ch2;
/*     */   
/*     */   private int su_chPrev;
/*     */   
/*     */   private int su_i2;
/*     */   
/*     */   private int su_j2;
/*     */   
/*     */   private int su_rNToGo;
/*     */   
/*     */   private int su_rTPos;
/*     */   
/*     */   private int su_tPos;
/*     */   
/*     */   private char su_z;
/*     */   
/*     */   private Data data;
/*     */ 
/*     */   
/*     */   public BZip2CompressorInputStream(InputStream in) throws IOException {
/* 113 */     this(in, false);
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
/*     */   public BZip2CompressorInputStream(InputStream in, boolean decompressConcatenated) throws IOException {
/* 131 */     this.bin = new BitInputStream((in == System.in) ? (InputStream)new CloseShieldFilterInputStream(in) : in, ByteOrder.BIG_ENDIAN);
/*     */     
/* 133 */     this.decompressConcatenated = decompressConcatenated;
/*     */     
/* 135 */     init(true);
/* 136 */     initBlock();
/*     */   }
/*     */ 
/*     */   
/*     */   public int read() throws IOException {
/* 141 */     if (this.bin != null) {
/* 142 */       int r = read0();
/* 143 */       count((r < 0) ? -1 : 1);
/* 144 */       return r;
/*     */     } 
/* 146 */     throw new IOException("Stream closed");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int read(byte[] dest, int offs, int len) throws IOException {
/* 157 */     if (offs < 0) {
/* 158 */       throw new IndexOutOfBoundsException("offs(" + offs + ") < 0.");
/*     */     }
/* 160 */     if (len < 0) {
/* 161 */       throw new IndexOutOfBoundsException("len(" + len + ") < 0.");
/*     */     }
/* 163 */     if (offs + len > dest.length) {
/* 164 */       throw new IndexOutOfBoundsException("offs(" + offs + ") + len(" + len + ") > dest.length(" + dest.length + ").");
/*     */     }
/*     */     
/* 167 */     if (this.bin == null) {
/* 168 */       throw new IOException("Stream closed");
/*     */     }
/* 170 */     if (len == 0) {
/* 171 */       return 0;
/*     */     }
/*     */     
/* 174 */     int hi = offs + len;
/* 175 */     int destOffs = offs;
/*     */     int b;
/* 177 */     while (destOffs < hi && (b = read0()) >= 0) {
/* 178 */       dest[destOffs++] = (byte)b;
/* 179 */       count(1);
/*     */     } 
/*     */     
/* 182 */     return (destOffs == offs) ? -1 : (destOffs - offs);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long getCompressedCount() {
/* 190 */     return this.bin.getBytesRead();
/*     */   }
/*     */   
/*     */   private void makeMaps() {
/* 194 */     boolean[] inUse = this.data.inUse;
/* 195 */     byte[] seqToUnseq = this.data.seqToUnseq;
/*     */     
/* 197 */     int nInUseShadow = 0;
/*     */     
/* 199 */     for (int i = 0; i < 256; i++) {
/* 200 */       if (inUse[i]) {
/* 201 */         seqToUnseq[nInUseShadow++] = (byte)i;
/*     */       }
/*     */     } 
/*     */     
/* 205 */     this.nInUse = nInUseShadow;
/*     */   }
/*     */   
/*     */   private int read0() throws IOException {
/* 209 */     switch (this.currentState) {
/*     */       case 0:
/* 211 */         return -1;
/*     */       
/*     */       case 1:
/* 214 */         return setupBlock();
/*     */       
/*     */       case 2:
/* 217 */         throw new IllegalStateException();
/*     */       
/*     */       case 3:
/* 220 */         return setupRandPartB();
/*     */       
/*     */       case 4:
/* 223 */         return setupRandPartC();
/*     */       
/*     */       case 5:
/* 226 */         throw new IllegalStateException();
/*     */       
/*     */       case 6:
/* 229 */         return setupNoRandPartB();
/*     */       
/*     */       case 7:
/* 232 */         return setupNoRandPartC();
/*     */     } 
/*     */     
/* 235 */     throw new IllegalStateException();
/*     */   }
/*     */ 
/*     */   
/*     */   private int readNextByte(BitInputStream in) throws IOException {
/* 240 */     long b = in.readBits(8);
/* 241 */     return (int)b;
/*     */   }
/*     */   
/*     */   private boolean init(boolean isFirstStream) throws IOException {
/* 245 */     if (null == this.bin) {
/* 246 */       throw new IOException("No InputStream");
/*     */     }
/*     */     
/* 249 */     if (!isFirstStream) {
/* 250 */       this.bin.clearBitCache();
/*     */     }
/*     */     
/* 253 */     int magic0 = readNextByte(this.bin);
/* 254 */     if (magic0 == -1 && !isFirstStream) {
/* 255 */       return false;
/*     */     }
/* 257 */     int magic1 = readNextByte(this.bin);
/* 258 */     int magic2 = readNextByte(this.bin);
/*     */     
/* 260 */     if (magic0 != 66 || magic1 != 90 || magic2 != 104) {
/* 261 */       throw new IOException(isFirstStream ? "Stream is not in the BZip2 format" : "Garbage after a valid BZip2 stream");
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 266 */     int blockSize = readNextByte(this.bin);
/* 267 */     if (blockSize < 49 || blockSize > 57) {
/* 268 */       throw new IOException("BZip2 block size is invalid");
/*     */     }
/*     */     
/* 271 */     this.blockSize100k = blockSize - 48;
/*     */     
/* 273 */     this.computedCombinedCRC = 0;
/*     */     
/* 275 */     return true;
/*     */   }
/*     */   private void initBlock() throws IOException {
/*     */     char magic0, magic1, magic2, magic3, magic4, magic5;
/* 279 */     BitInputStream bin = this.bin;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     while (true) {
/* 289 */       magic0 = bsGetUByte(bin);
/* 290 */       magic1 = bsGetUByte(bin);
/* 291 */       magic2 = bsGetUByte(bin);
/* 292 */       magic3 = bsGetUByte(bin);
/* 293 */       magic4 = bsGetUByte(bin);
/* 294 */       magic5 = bsGetUByte(bin);
/*     */ 
/*     */       
/* 297 */       if (magic0 != '\027' || magic1 != 'r' || magic2 != 'E' || magic3 != '8' || magic4 != 'P' || magic5 != '') {
/*     */         break;
/*     */       }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 305 */       if (complete()) {
/*     */         return;
/*     */       }
/*     */     } 
/*     */     
/* 310 */     if (magic0 != '1' || magic1 != 'A' || magic2 != 'Y' || magic3 != '&' || magic4 != 'S' || magic5 != 'Y') {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 317 */       this.currentState = 0;
/* 318 */       throw new IOException("Bad block header");
/*     */     } 
/* 320 */     this.storedBlockCRC = bsGetInt(bin);
/* 321 */     this.blockRandomised = (bsR(bin, 1) == 1);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 327 */     if (this.data == null) {
/* 328 */       this.data = new Data(this.blockSize100k);
/*     */     }
/*     */ 
/*     */     
/* 332 */     getAndMoveToFrontDecode();
/*     */     
/* 334 */     this.crc.initializeCRC();
/* 335 */     this.currentState = 1;
/*     */   }
/*     */   
/*     */   private void endBlock() throws IOException {
/* 339 */     this.computedBlockCRC = this.crc.getFinalCRC();
/*     */ 
/*     */     
/* 342 */     if (this.storedBlockCRC != this.computedBlockCRC) {
/*     */ 
/*     */       
/* 345 */       this.computedCombinedCRC = this.storedCombinedCRC << 1 | this.storedCombinedCRC >>> 31;
/*     */       
/* 347 */       this.computedCombinedCRC ^= this.storedBlockCRC;
/*     */       
/* 349 */       throw new IOException("BZip2 CRC error");
/*     */     } 
/*     */     
/* 352 */     this.computedCombinedCRC = this.computedCombinedCRC << 1 | this.computedCombinedCRC >>> 31;
/*     */     
/* 354 */     this.computedCombinedCRC ^= this.computedBlockCRC;
/*     */   }
/*     */   
/*     */   private boolean complete() throws IOException {
/* 358 */     this.storedCombinedCRC = bsGetInt(this.bin);
/* 359 */     this.currentState = 0;
/* 360 */     this.data = null;
/*     */     
/* 362 */     if (this.storedCombinedCRC != this.computedCombinedCRC) {
/* 363 */       throw new IOException("BZip2 CRC error");
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 368 */     return (!this.decompressConcatenated || !init(false));
/*     */   }
/*     */ 
/*     */   
/*     */   public void close() throws IOException {
/* 373 */     BitInputStream inShadow = this.bin;
/* 374 */     if (inShadow != null) {
/*     */       try {
/* 376 */         inShadow.close();
/*     */       } finally {
/* 378 */         this.data = null;
/* 379 */         this.bin = null;
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
/*     */   private static int bsR(BitInputStream bin, int n) throws IOException {
/* 391 */     long thech = bin.readBits(n);
/* 392 */     if (thech < 0L) {
/* 393 */       throw new IOException("Unexpected end of stream");
/*     */     }
/* 395 */     return (int)thech;
/*     */   }
/*     */   
/*     */   private static boolean bsGetBit(BitInputStream bin) throws IOException {
/* 399 */     return (bsR(bin, 1) != 0);
/*     */   }
/*     */   
/*     */   private static char bsGetUByte(BitInputStream bin) throws IOException {
/* 403 */     return (char)bsR(bin, 8);
/*     */   }
/*     */   
/*     */   private static int bsGetInt(BitInputStream bin) throws IOException {
/* 407 */     return bsR(bin, 32);
/*     */   }
/*     */ 
/*     */   
/*     */   private static void checkBounds(int checkVal, int limitExclusive, String name) throws IOException {
/* 412 */     if (checkVal < 0) {
/* 413 */       throw new IOException("Corrupted input, " + name + " value negative");
/*     */     }
/* 415 */     if (checkVal >= limitExclusive) {
/* 416 */       throw new IOException("Corrupted input, " + name + " value too big");
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static void hbCreateDecodeTables(int[] limit, int[] base, int[] perm, char[] length, int minLen, int maxLen, int alphaSize) throws IOException {
/*     */     int i;
/*     */     int pp;
/* 427 */     for (i = minLen, pp = 0; i <= maxLen; i++) {
/* 428 */       for (int k = 0; k < alphaSize; k++) {
/* 429 */         if (length[k] == i) {
/* 430 */           perm[pp++] = k;
/*     */         }
/*     */       } 
/*     */     } 
/*     */     
/* 435 */     for (i = 23; --i > 0; ) {
/* 436 */       base[i] = 0;
/* 437 */       limit[i] = 0;
/*     */     } 
/*     */     
/* 440 */     for (i = 0; i < alphaSize; i++) {
/* 441 */       int l = length[i];
/* 442 */       checkBounds(l, 258, "length");
/* 443 */       base[l + 1] = base[l + 1] + 1;
/*     */     } 
/*     */     int b;
/* 446 */     for (i = 1, b = base[0]; i < 23; i++) {
/* 447 */       b += base[i];
/* 448 */       base[i] = b;
/*     */     }  int vec;
/*     */     int j;
/* 451 */     for (i = minLen, vec = 0, j = base[i]; i <= maxLen; i++) {
/* 452 */       int nb = base[i + 1];
/* 453 */       vec += nb - j;
/* 454 */       j = nb;
/* 455 */       limit[i] = vec - 1;
/* 456 */       vec <<= 1;
/*     */     } 
/*     */     
/* 459 */     for (i = minLen + 1; i <= maxLen; i++) {
/* 460 */       base[i] = (limit[i - 1] + 1 << 1) - base[i];
/*     */     }
/*     */   }
/*     */   
/*     */   private void recvDecodingTables() throws IOException {
/* 465 */     BitInputStream bin = this.bin;
/* 466 */     Data dataShadow = this.data;
/* 467 */     boolean[] inUse = dataShadow.inUse;
/* 468 */     byte[] pos = dataShadow.recvDecodingTables_pos;
/* 469 */     byte[] selector = dataShadow.selector;
/* 470 */     byte[] selectorMtf = dataShadow.selectorMtf;
/*     */     
/* 472 */     int inUse16 = 0;
/*     */     
/*     */     int i;
/* 475 */     for (i = 0; i < 16; i++) {
/* 476 */       if (bsGetBit(bin)) {
/* 477 */         inUse16 |= 1 << i;
/*     */       }
/*     */     } 
/*     */     
/* 481 */     Arrays.fill(inUse, false);
/* 482 */     for (i = 0; i < 16; i++) {
/* 483 */       if ((inUse16 & 1 << i) != 0) {
/* 484 */         int i16 = i << 4;
/* 485 */         for (int m = 0; m < 16; m++) {
/* 486 */           if (bsGetBit(bin)) {
/* 487 */             inUse[i16 + m] = true;
/*     */           }
/*     */         } 
/*     */       } 
/*     */     } 
/*     */     
/* 493 */     makeMaps();
/* 494 */     int alphaSize = this.nInUse + 2;
/*     */     
/* 496 */     int nGroups = bsR(bin, 3);
/* 497 */     int selectors = bsR(bin, 15);
/* 498 */     if (selectors < 0) {
/* 499 */       throw new IOException("Corrupted input, nSelectors value negative");
/*     */     }
/* 501 */     checkBounds(alphaSize, 259, "alphaSize");
/* 502 */     checkBounds(nGroups, 7, "nGroups");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 508 */     for (int j = 0; j < selectors; j++) {
/* 509 */       int m = 0;
/* 510 */       while (bsGetBit(bin)) {
/* 511 */         m++;
/*     */       }
/* 513 */       if (j < 18002) {
/* 514 */         selectorMtf[j] = (byte)m;
/*     */       }
/*     */     } 
/* 517 */     int nSelectors = (selectors > 18002) ? 18002 : selectors;
/*     */ 
/*     */     
/* 520 */     for (int v = nGroups; --v >= 0;) {
/* 521 */       pos[v] = (byte)v;
/*     */     }
/*     */     
/* 524 */     for (int k = 0; k < nSelectors; k++) {
/* 525 */       int m = selectorMtf[k] & 0xFF;
/* 526 */       checkBounds(m, 6, "selectorMtf");
/* 527 */       byte tmp = pos[m];
/* 528 */       while (m > 0) {
/*     */         
/* 530 */         pos[m] = pos[m - 1];
/* 531 */         m--;
/*     */       } 
/* 533 */       pos[0] = tmp;
/* 534 */       selector[k] = tmp;
/*     */     } 
/*     */     
/* 537 */     char[][] len = dataShadow.temp_charArray2d;
/*     */ 
/*     */     
/* 540 */     for (int t = 0; t < nGroups; t++) {
/* 541 */       int curr = bsR(bin, 5);
/* 542 */       char[] len_t = len[t];
/* 543 */       for (int m = 0; m < alphaSize; m++) {
/* 544 */         while (bsGetBit(bin)) {
/* 545 */           curr += bsGetBit(bin) ? -1 : 1;
/*     */         }
/* 547 */         len_t[m] = (char)curr;
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/* 552 */     createHuffmanDecodingTables(alphaSize, nGroups);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void createHuffmanDecodingTables(int alphaSize, int nGroups) throws IOException {
/* 560 */     Data dataShadow = this.data;
/* 561 */     char[][] len = dataShadow.temp_charArray2d;
/* 562 */     int[] minLens = dataShadow.minLens;
/* 563 */     int[][] limit = dataShadow.limit;
/* 564 */     int[][] base = dataShadow.base;
/* 565 */     int[][] perm = dataShadow.perm;
/*     */     
/* 567 */     for (int t = 0; t < nGroups; t++) {
/* 568 */       int minLen = 32;
/* 569 */       int maxLen = 0;
/* 570 */       char[] len_t = len[t];
/* 571 */       for (int i = alphaSize; --i >= 0; ) {
/* 572 */         char lent = len_t[i];
/* 573 */         if (lent > maxLen) {
/* 574 */           maxLen = lent;
/*     */         }
/* 576 */         if (lent < minLen) {
/* 577 */           minLen = lent;
/*     */         }
/*     */       } 
/* 580 */       hbCreateDecodeTables(limit[t], base[t], perm[t], len[t], minLen, maxLen, alphaSize);
/*     */       
/* 582 */       minLens[t] = minLen;
/*     */     } 
/*     */   }
/*     */   
/*     */   private void getAndMoveToFrontDecode() throws IOException {
/* 587 */     BitInputStream bin = this.bin;
/* 588 */     this.origPtr = bsR(bin, 24);
/* 589 */     recvDecodingTables();
/*     */     
/* 591 */     Data dataShadow = this.data;
/* 592 */     byte[] ll8 = dataShadow.ll8;
/* 593 */     int[] unzftab = dataShadow.unzftab;
/* 594 */     byte[] selector = dataShadow.selector;
/* 595 */     byte[] seqToUnseq = dataShadow.seqToUnseq;
/* 596 */     char[] yy = dataShadow.getAndMoveToFrontDecode_yy;
/* 597 */     int[] minLens = dataShadow.minLens;
/* 598 */     int[][] limit = dataShadow.limit;
/* 599 */     int[][] base = dataShadow.base;
/* 600 */     int[][] perm = dataShadow.perm;
/* 601 */     int limitLast = this.blockSize100k * 100000;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 608 */     for (int i = 256; --i >= 0; ) {
/* 609 */       yy[i] = (char)i;
/* 610 */       unzftab[i] = 0;
/*     */     } 
/*     */     
/* 613 */     int groupNo = 0;
/* 614 */     int groupPos = 49;
/* 615 */     int eob = this.nInUse + 1;
/* 616 */     int nextSym = getAndMoveToFrontDecode0();
/* 617 */     int lastShadow = -1;
/* 618 */     int zt = selector[groupNo] & 0xFF;
/* 619 */     checkBounds(zt, 6, "zt");
/* 620 */     int[] base_zt = base[zt];
/* 621 */     int[] limit_zt = limit[zt];
/* 622 */     int[] perm_zt = perm[zt];
/* 623 */     int minLens_zt = minLens[zt];
/*     */     
/* 625 */     while (nextSym != eob) {
/* 626 */       if (nextSym == 0 || nextSym == 1) {
/* 627 */         int s = -1;
/*     */         int n;
/* 629 */         for (n = 1;; n <<= 1) {
/* 630 */           if (nextSym == 0) {
/* 631 */             s += n;
/* 632 */           } else if (nextSym == 1) {
/* 633 */             s += n << 1;
/*     */           } else {
/*     */             break;
/*     */           } 
/*     */           
/* 638 */           if (groupPos == 0) {
/* 639 */             groupPos = 49;
/* 640 */             checkBounds(++groupNo, 18002, "groupNo");
/* 641 */             zt = selector[groupNo] & 0xFF;
/* 642 */             checkBounds(zt, 6, "zt");
/* 643 */             base_zt = base[zt];
/* 644 */             limit_zt = limit[zt];
/* 645 */             perm_zt = perm[zt];
/* 646 */             minLens_zt = minLens[zt];
/*     */           } else {
/* 648 */             groupPos--;
/*     */           } 
/*     */           
/* 651 */           int j = minLens_zt;
/* 652 */           checkBounds(j, 258, "zn");
/* 653 */           int k = bsR(bin, j);
/* 654 */           while (k > limit_zt[j]) {
/* 655 */             checkBounds(++j, 258, "zn");
/* 656 */             k = k << 1 | bsR(bin, 1);
/*     */           } 
/* 658 */           int m = k - base_zt[j];
/* 659 */           checkBounds(m, 258, "zvec");
/* 660 */           nextSym = perm_zt[m];
/*     */         } 
/* 662 */         checkBounds(s, this.data.ll8.length, "s");
/*     */         
/* 664 */         int yy0 = yy[0];
/* 665 */         checkBounds(yy0, 256, "yy");
/* 666 */         byte ch = seqToUnseq[yy0];
/* 667 */         unzftab[ch & 0xFF] = unzftab[ch & 0xFF] + s + 1;
/*     */         
/* 669 */         int from = ++lastShadow;
/* 670 */         lastShadow += s;
/* 671 */         checkBounds(lastShadow, this.data.ll8.length, "lastShadow");
/* 672 */         Arrays.fill(ll8, from, lastShadow + 1, ch);
/*     */         
/* 674 */         if (lastShadow >= limitLast) {
/* 675 */           throw new IOException("Block overrun while expanding RLE in MTF, " + lastShadow + " exceeds " + limitLast);
/*     */         }
/*     */         continue;
/*     */       } 
/* 679 */       if (++lastShadow >= limitLast) {
/* 680 */         throw new IOException("Block overrun in MTF, " + lastShadow + " exceeds " + limitLast);
/*     */       }
/*     */       
/* 683 */       checkBounds(nextSym, 257, "nextSym");
/*     */       
/* 685 */       char tmp = yy[nextSym - 1];
/* 686 */       checkBounds(tmp, 256, "yy");
/* 687 */       unzftab[seqToUnseq[tmp] & 0xFF] = unzftab[seqToUnseq[tmp] & 0xFF] + 1;
/* 688 */       ll8[lastShadow] = seqToUnseq[tmp];
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 695 */       if (nextSym <= 16) {
/* 696 */         for (int j = nextSym - 1; j > 0;) {
/* 697 */           yy[j] = yy[--j];
/*     */         }
/*     */       } else {
/* 700 */         System.arraycopy(yy, 0, yy, 1, nextSym - 1);
/*     */       } 
/*     */       
/* 703 */       yy[0] = tmp;
/*     */       
/* 705 */       if (groupPos == 0) {
/* 706 */         groupPos = 49;
/* 707 */         checkBounds(++groupNo, 18002, "groupNo");
/* 708 */         zt = selector[groupNo] & 0xFF;
/* 709 */         checkBounds(zt, 6, "zt");
/* 710 */         base_zt = base[zt];
/* 711 */         limit_zt = limit[zt];
/* 712 */         perm_zt = perm[zt];
/* 713 */         minLens_zt = minLens[zt];
/*     */       } else {
/* 715 */         groupPos--;
/*     */       } 
/*     */       
/* 718 */       int zn = minLens_zt;
/* 719 */       checkBounds(zn, 258, "zn");
/* 720 */       int zvec = bsR(bin, zn);
/* 721 */       while (zvec > limit_zt[zn]) {
/* 722 */         checkBounds(++zn, 258, "zn");
/* 723 */         zvec = zvec << 1 | bsR(bin, 1);
/*     */       } 
/* 725 */       int idx = zvec - base_zt[zn];
/* 726 */       checkBounds(idx, 258, "zvec");
/* 727 */       nextSym = perm_zt[idx];
/*     */     } 
/*     */ 
/*     */     
/* 731 */     this.last = lastShadow;
/*     */   }
/*     */   
/*     */   private int getAndMoveToFrontDecode0() throws IOException {
/* 735 */     Data dataShadow = this.data;
/* 736 */     int zt = dataShadow.selector[0] & 0xFF;
/* 737 */     checkBounds(zt, 6, "zt");
/* 738 */     int[] limit_zt = dataShadow.limit[zt];
/* 739 */     int zn = dataShadow.minLens[zt];
/* 740 */     checkBounds(zn, 258, "zn");
/* 741 */     int zvec = bsR(this.bin, zn);
/* 742 */     while (zvec > limit_zt[zn]) {
/* 743 */       checkBounds(++zn, 258, "zn");
/* 744 */       zvec = zvec << 1 | bsR(this.bin, 1);
/*     */     } 
/* 746 */     int tmp = zvec - dataShadow.base[zt][zn];
/* 747 */     checkBounds(tmp, 258, "zvec");
/*     */     
/* 749 */     return dataShadow.perm[zt][tmp];
/*     */   }
/*     */   
/*     */   private int setupBlock() throws IOException {
/* 753 */     if (this.currentState == 0 || this.data == null) {
/* 754 */       return -1;
/*     */     }
/*     */     
/* 757 */     int[] cftab = this.data.cftab;
/* 758 */     int ttLen = this.last + 1;
/* 759 */     int[] tt = this.data.initTT(ttLen);
/* 760 */     byte[] ll8 = this.data.ll8;
/* 761 */     cftab[0] = 0;
/* 762 */     System.arraycopy(this.data.unzftab, 0, cftab, 1, 256);
/*     */     int i, c;
/* 764 */     for (i = 1, c = cftab[0]; i <= 256; i++) {
/* 765 */       c += cftab[i];
/* 766 */       cftab[i] = c;
/*     */     } 
/*     */     int lastShadow;
/* 769 */     for (i = 0, lastShadow = this.last; i <= lastShadow; i++) {
/* 770 */       cftab[ll8[i] & 0xFF] = cftab[ll8[i] & 0xFF] + 1; int tmp = cftab[ll8[i] & 0xFF];
/* 771 */       checkBounds(tmp, ttLen, "tt index");
/* 772 */       tt[tmp] = i;
/*     */     } 
/*     */     
/* 775 */     if (this.origPtr < 0 || this.origPtr >= tt.length) {
/* 776 */       throw new IOException("Stream corrupted");
/*     */     }
/*     */     
/* 779 */     this.su_tPos = tt[this.origPtr];
/* 780 */     this.su_count = 0;
/* 781 */     this.su_i2 = 0;
/* 782 */     this.su_ch2 = 256;
/*     */     
/* 784 */     if (this.blockRandomised) {
/* 785 */       this.su_rNToGo = 0;
/* 786 */       this.su_rTPos = 0;
/* 787 */       return setupRandPartA();
/*     */     } 
/* 789 */     return setupNoRandPartA();
/*     */   }
/*     */   
/*     */   private int setupRandPartA() throws IOException {
/* 793 */     if (this.su_i2 <= this.last) {
/* 794 */       this.su_chPrev = this.su_ch2;
/* 795 */       int su_ch2Shadow = this.data.ll8[this.su_tPos] & 0xFF;
/* 796 */       checkBounds(this.su_tPos, this.data.tt.length, "su_tPos");
/* 797 */       this.su_tPos = this.data.tt[this.su_tPos];
/* 798 */       if (this.su_rNToGo == 0) {
/* 799 */         this.su_rNToGo = Rand.rNums(this.su_rTPos) - 1;
/* 800 */         if (++this.su_rTPos == 512) {
/* 801 */           this.su_rTPos = 0;
/*     */         }
/*     */       } else {
/* 804 */         this.su_rNToGo--;
/*     */       } 
/* 806 */       this.su_ch2 = su_ch2Shadow ^= (this.su_rNToGo == 1) ? 1 : 0;
/* 807 */       this.su_i2++;
/* 808 */       this.currentState = 3;
/* 809 */       this.crc.updateCRC(su_ch2Shadow);
/* 810 */       return su_ch2Shadow;
/*     */     } 
/* 812 */     endBlock();
/* 813 */     initBlock();
/* 814 */     return setupBlock();
/*     */   }
/*     */   
/*     */   private int setupNoRandPartA() throws IOException {
/* 818 */     if (this.su_i2 <= this.last) {
/* 819 */       this.su_chPrev = this.su_ch2;
/* 820 */       int su_ch2Shadow = this.data.ll8[this.su_tPos] & 0xFF;
/* 821 */       this.su_ch2 = su_ch2Shadow;
/* 822 */       checkBounds(this.su_tPos, this.data.tt.length, "su_tPos");
/* 823 */       this.su_tPos = this.data.tt[this.su_tPos];
/* 824 */       this.su_i2++;
/* 825 */       this.currentState = 6;
/* 826 */       this.crc.updateCRC(su_ch2Shadow);
/* 827 */       return su_ch2Shadow;
/*     */     } 
/* 829 */     this.currentState = 5;
/* 830 */     endBlock();
/* 831 */     initBlock();
/* 832 */     return setupBlock();
/*     */   }
/*     */   
/*     */   private int setupRandPartB() throws IOException {
/* 836 */     if (this.su_ch2 != this.su_chPrev) {
/* 837 */       this.currentState = 2;
/* 838 */       this.su_count = 1;
/* 839 */       return setupRandPartA();
/*     */     } 
/* 841 */     if (++this.su_count < 4) {
/* 842 */       this.currentState = 2;
/* 843 */       return setupRandPartA();
/*     */     } 
/* 845 */     this.su_z = (char)(this.data.ll8[this.su_tPos] & 0xFF);
/* 846 */     checkBounds(this.su_tPos, this.data.tt.length, "su_tPos");
/* 847 */     this.su_tPos = this.data.tt[this.su_tPos];
/* 848 */     if (this.su_rNToGo == 0) {
/* 849 */       this.su_rNToGo = Rand.rNums(this.su_rTPos) - 1;
/* 850 */       if (++this.su_rTPos == 512) {
/* 851 */         this.su_rTPos = 0;
/*     */       }
/*     */     } else {
/* 854 */       this.su_rNToGo--;
/*     */     } 
/* 856 */     this.su_j2 = 0;
/* 857 */     this.currentState = 4;
/* 858 */     if (this.su_rNToGo == 1) {
/* 859 */       this.su_z = (char)(this.su_z ^ 0x1);
/*     */     }
/* 861 */     return setupRandPartC();
/*     */   }
/*     */   
/*     */   private int setupRandPartC() throws IOException {
/* 865 */     if (this.su_j2 < this.su_z) {
/* 866 */       this.crc.updateCRC(this.su_ch2);
/* 867 */       this.su_j2++;
/* 868 */       return this.su_ch2;
/*     */     } 
/* 870 */     this.currentState = 2;
/* 871 */     this.su_i2++;
/* 872 */     this.su_count = 0;
/* 873 */     return setupRandPartA();
/*     */   }
/*     */   
/*     */   private int setupNoRandPartB() throws IOException {
/* 877 */     if (this.su_ch2 != this.su_chPrev) {
/* 878 */       this.su_count = 1;
/* 879 */       return setupNoRandPartA();
/*     */     } 
/* 881 */     if (++this.su_count >= 4) {
/* 882 */       checkBounds(this.su_tPos, this.data.ll8.length, "su_tPos");
/* 883 */       this.su_z = (char)(this.data.ll8[this.su_tPos] & 0xFF);
/* 884 */       this.su_tPos = this.data.tt[this.su_tPos];
/* 885 */       this.su_j2 = 0;
/* 886 */       return setupNoRandPartC();
/*     */     } 
/* 888 */     return setupNoRandPartA();
/*     */   }
/*     */   
/*     */   private int setupNoRandPartC() throws IOException {
/* 892 */     if (this.su_j2 < this.su_z) {
/* 893 */       int su_ch2Shadow = this.su_ch2;
/* 894 */       this.crc.updateCRC(su_ch2Shadow);
/* 895 */       this.su_j2++;
/* 896 */       this.currentState = 7;
/* 897 */       return su_ch2Shadow;
/*     */     } 
/* 899 */     this.su_i2++;
/* 900 */     this.su_count = 0;
/* 901 */     return setupNoRandPartA();
/*     */   }
/*     */ 
/*     */   
/*     */   private static final class Data
/*     */   {
/* 907 */     final boolean[] inUse = new boolean[256];
/*     */     
/* 909 */     final byte[] seqToUnseq = new byte[256];
/* 910 */     final byte[] selector = new byte[18002];
/* 911 */     final byte[] selectorMtf = new byte[18002];
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 917 */     final int[] unzftab = new int[256];
/*     */     
/* 919 */     final int[][] limit = new int[6][258];
/* 920 */     final int[][] base = new int[6][258];
/* 921 */     final int[][] perm = new int[6][258];
/* 922 */     final int[] minLens = new int[6];
/*     */     
/* 924 */     final int[] cftab = new int[257];
/* 925 */     final char[] getAndMoveToFrontDecode_yy = new char[256];
/* 926 */     final char[][] temp_charArray2d = new char[6][258];
/*     */     
/* 928 */     final byte[] recvDecodingTables_pos = new byte[6];
/*     */ 
/*     */ 
/*     */     
/*     */     int[] tt;
/*     */ 
/*     */     
/*     */     final byte[] ll8;
/*     */ 
/*     */ 
/*     */     
/*     */     Data(int blockSize100k) {
/* 940 */       this.ll8 = new byte[blockSize100k * 100000];
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     int[] initTT(int length) {
/* 951 */       int[] ttShadow = this.tt;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 957 */       if (ttShadow == null || ttShadow.length < length) {
/* 958 */         this.tt = ttShadow = new int[length];
/*     */       }
/*     */       
/* 961 */       return ttShadow;
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
/*     */   public static boolean matches(byte[] signature, int length) {
/* 978 */     return (length >= 3 && signature[0] == 66 && signature[1] == 90 && signature[2] == 104);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\compressors\bzip2\BZip2CompressorInputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */