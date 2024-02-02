/*     */ package org.apache.commons.compress.compressors.lz77support;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.util.Arrays;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class LZ77Compressor
/*     */ {
/*     */   public static abstract class Block
/*     */   {
/*     */     public abstract BlockType getType();
/*     */     
/*     */     public enum BlockType
/*     */     {
/*  99 */       LITERAL, BACK_REFERENCE, EOD;
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static final class LiteralBlock
/*     */     extends Block
/*     */   {
/*     */     private final byte[] data;
/*     */     
/*     */     private final int offset;
/*     */     
/*     */     private final int length;
/*     */ 
/*     */     
/*     */     public LiteralBlock(byte[] data, int offset, int length) {
/* 116 */       this.data = data;
/* 117 */       this.offset = offset;
/* 118 */       this.length = length;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public byte[] getData() {
/* 128 */       return this.data;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public int getOffset() {
/* 135 */       return this.offset;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public int getLength() {
/* 142 */       return this.length;
/*     */     }
/*     */     
/*     */     public LZ77Compressor.Block.BlockType getType() {
/* 146 */       return LZ77Compressor.Block.BlockType.LITERAL;
/*     */     }
/*     */     
/*     */     public String toString() {
/* 150 */       return "LiteralBlock starting at " + this.offset + " with length " + this.length;
/*     */     }
/*     */   }
/*     */   
/*     */   public static final class BackReference
/*     */     extends Block {
/*     */     private final int offset;
/*     */     private final int length;
/*     */     
/*     */     public BackReference(int offset, int length) {
/* 160 */       this.offset = offset;
/* 161 */       this.length = length;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public int getOffset() {
/* 168 */       return this.offset;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public int getLength() {
/* 175 */       return this.length;
/*     */     }
/*     */     
/*     */     public LZ77Compressor.Block.BlockType getType() {
/* 179 */       return LZ77Compressor.Block.BlockType.BACK_REFERENCE;
/*     */     }
/*     */     
/*     */     public String toString() {
/* 183 */       return "BackReference with offset " + this.offset + " and length " + this.length;
/*     */     }
/*     */   }
/*     */   
/*     */   public static final class EOD
/*     */     extends Block
/*     */   {
/*     */     public LZ77Compressor.Block.BlockType getType() {
/* 191 */       return LZ77Compressor.Block.BlockType.EOD;
/*     */     }
/*     */   }
/*     */   
/* 195 */   private static final Block THE_EOD = new EOD();
/*     */ 
/*     */ 
/*     */   
/*     */   static final int NUMBER_OF_BYTES_IN_HASH = 3;
/*     */ 
/*     */ 
/*     */   
/*     */   private static final int NO_MATCH = -1;
/*     */ 
/*     */ 
/*     */   
/*     */   private final Parameters params;
/*     */ 
/*     */ 
/*     */   
/*     */   private final Callback callback;
/*     */ 
/*     */ 
/*     */   
/*     */   private final byte[] window;
/*     */ 
/*     */ 
/*     */   
/*     */   private final int[] head;
/*     */ 
/*     */ 
/*     */   
/*     */   private final int[] prev;
/*     */ 
/*     */ 
/*     */   
/*     */   private final int wMask;
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean initialized;
/*     */ 
/*     */   
/*     */   private int currentPosition;
/*     */ 
/*     */   
/*     */   private int lookahead;
/*     */ 
/*     */   
/*     */   private int insertHash;
/*     */ 
/*     */   
/*     */   private int blockStart;
/*     */ 
/*     */   
/* 246 */   private int matchStart = -1;
/*     */ 
/*     */   
/*     */   private int missedInserts;
/*     */   
/*     */   private static final int HASH_SIZE = 32768;
/*     */   
/*     */   private static final int HASH_MASK = 32767;
/*     */   
/*     */   private static final int H_SHIFT = 5;
/*     */ 
/*     */   
/*     */   public LZ77Compressor(Parameters params, Callback callback) {
/* 259 */     Objects.requireNonNull(params, "params");
/* 260 */     Objects.requireNonNull(callback, "callback");
/*     */     
/* 262 */     this.params = params;
/* 263 */     this.callback = callback;
/*     */     
/* 265 */     int wSize = params.getWindowSize();
/* 266 */     this.window = new byte[wSize * 2];
/* 267 */     this.wMask = wSize - 1;
/* 268 */     this.head = new int[32768];
/* 269 */     Arrays.fill(this.head, -1);
/* 270 */     this.prev = new int[wSize];
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void compress(byte[] data) throws IOException {
/* 281 */     compress(data, 0, data.length);
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
/*     */   public void compress(byte[] data, int off, int len) throws IOException {
/* 294 */     int wSize = this.params.getWindowSize();
/* 295 */     while (len > wSize) {
/* 296 */       doCompress(data, off, wSize);
/* 297 */       off += wSize;
/* 298 */       len -= wSize;
/*     */     } 
/* 300 */     if (len > 0) {
/* 301 */       doCompress(data, off, len);
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
/*     */   public void finish() throws IOException {
/* 315 */     if (this.blockStart != this.currentPosition || this.lookahead > 0) {
/* 316 */       this.currentPosition += this.lookahead;
/* 317 */       flushLiteralBlock();
/*     */     } 
/* 319 */     this.callback.accept(THE_EOD);
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
/*     */   public void prefill(byte[] data) {
/* 334 */     if (this.currentPosition != 0 || this.lookahead != 0) {
/* 335 */       throw new IllegalStateException("The compressor has already started to accept data, can't prefill anymore");
/*     */     }
/*     */ 
/*     */     
/* 339 */     int len = Math.min(this.params.getWindowSize(), data.length);
/* 340 */     System.arraycopy(data, data.length - len, this.window, 0, len);
/*     */     
/* 342 */     if (len >= 3) {
/* 343 */       initialize();
/* 344 */       int stop = len - 3 + 1;
/* 345 */       for (int i = 0; i < stop; i++) {
/* 346 */         insertString(i);
/*     */       }
/* 348 */       this.missedInserts = 2;
/*     */     } else {
/* 350 */       this.missedInserts = len;
/*     */     } 
/* 352 */     this.blockStart = this.currentPosition = len;
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
/*     */   private int nextHash(int oldHash, byte nextByte) {
/* 369 */     int nextVal = nextByte & 0xFF;
/* 370 */     return (oldHash << 5 ^ nextVal) & 0x7FFF;
/*     */   }
/*     */ 
/*     */   
/*     */   private void doCompress(byte[] data, int off, int len) throws IOException {
/* 375 */     int spaceLeft = this.window.length - this.currentPosition - this.lookahead;
/* 376 */     if (len > spaceLeft) {
/* 377 */       slide();
/*     */     }
/* 379 */     System.arraycopy(data, off, this.window, this.currentPosition + this.lookahead, len);
/* 380 */     this.lookahead += len;
/* 381 */     if (!this.initialized && this.lookahead >= this.params.getMinBackReferenceLength()) {
/* 382 */       initialize();
/*     */     }
/* 384 */     if (this.initialized) {
/* 385 */       compress();
/*     */     }
/*     */   }
/*     */   
/*     */   private void slide() throws IOException {
/* 390 */     int wSize = this.params.getWindowSize();
/* 391 */     if (this.blockStart != this.currentPosition && this.blockStart < wSize) {
/* 392 */       flushLiteralBlock();
/* 393 */       this.blockStart = this.currentPosition;
/*     */     } 
/* 395 */     System.arraycopy(this.window, wSize, this.window, 0, wSize);
/* 396 */     this.currentPosition -= wSize;
/* 397 */     this.matchStart -= wSize;
/* 398 */     this.blockStart -= wSize; int i;
/* 399 */     for (i = 0; i < 32768; i++) {
/* 400 */       int h = this.head[i];
/* 401 */       this.head[i] = (h >= wSize) ? (h - wSize) : -1;
/*     */     } 
/* 403 */     for (i = 0; i < wSize; i++) {
/* 404 */       int p = this.prev[i];
/* 405 */       this.prev[i] = (p >= wSize) ? (p - wSize) : -1;
/*     */     } 
/*     */   }
/*     */   
/*     */   private void initialize() {
/* 410 */     for (int i = 0; i < 2; i++) {
/* 411 */       this.insertHash = nextHash(this.insertHash, this.window[i]);
/*     */     }
/* 413 */     this.initialized = true;
/*     */   }
/*     */   
/*     */   private void compress() throws IOException {
/* 417 */     int minMatch = this.params.getMinBackReferenceLength();
/* 418 */     boolean lazy = this.params.getLazyMatching();
/* 419 */     int lazyThreshold = this.params.getLazyMatchingThreshold();
/*     */     
/* 421 */     while (this.lookahead >= minMatch) {
/* 422 */       catchUpMissedInserts();
/* 423 */       int matchLength = 0;
/* 424 */       int hashHead = insertString(this.currentPosition);
/* 425 */       if (hashHead != -1 && hashHead - this.currentPosition <= this.params.getMaxOffset()) {
/*     */         
/* 427 */         matchLength = longestMatch(hashHead);
/*     */         
/* 429 */         if (lazy && matchLength <= lazyThreshold && this.lookahead > minMatch)
/*     */         {
/* 431 */           matchLength = longestMatchForNextPosition(matchLength);
/*     */         }
/*     */       } 
/* 434 */       if (matchLength >= minMatch) {
/* 435 */         if (this.blockStart != this.currentPosition) {
/*     */           
/* 437 */           flushLiteralBlock();
/* 438 */           this.blockStart = -1;
/*     */         } 
/* 440 */         flushBackReference(matchLength);
/* 441 */         insertStringsInMatch(matchLength);
/* 442 */         this.lookahead -= matchLength;
/* 443 */         this.currentPosition += matchLength;
/* 444 */         this.blockStart = this.currentPosition;
/*     */         continue;
/*     */       } 
/* 447 */       this.lookahead--;
/* 448 */       this.currentPosition++;
/* 449 */       if (this.currentPosition - this.blockStart >= this.params.getMaxLiteralLength()) {
/* 450 */         flushLiteralBlock();
/* 451 */         this.blockStart = this.currentPosition;
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
/*     */   private int insertString(int pos) {
/* 465 */     this.insertHash = nextHash(this.insertHash, this.window[pos - 1 + 3]);
/* 466 */     int hashHead = this.head[this.insertHash];
/* 467 */     this.prev[pos & this.wMask] = hashHead;
/* 468 */     this.head[this.insertHash] = pos;
/* 469 */     return hashHead;
/*     */   }
/*     */ 
/*     */   
/*     */   private int longestMatchForNextPosition(int prevMatchLength) {
/* 474 */     int prevMatchStart = this.matchStart;
/* 475 */     int prevInsertHash = this.insertHash;
/*     */     
/* 477 */     this.lookahead--;
/* 478 */     this.currentPosition++;
/* 479 */     int hashHead = insertString(this.currentPosition);
/* 480 */     int prevHashHead = this.prev[this.currentPosition & this.wMask];
/* 481 */     int matchLength = longestMatch(hashHead);
/*     */     
/* 483 */     if (matchLength <= prevMatchLength) {
/*     */       
/* 485 */       matchLength = prevMatchLength;
/* 486 */       this.matchStart = prevMatchStart;
/*     */ 
/*     */       
/* 489 */       this.head[this.insertHash] = prevHashHead;
/* 490 */       this.insertHash = prevInsertHash;
/* 491 */       this.currentPosition--;
/* 492 */       this.lookahead++;
/*     */     } 
/* 494 */     return matchLength;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private void insertStringsInMatch(int matchLength) {
/* 500 */     int stop = Math.min(matchLength - 1, this.lookahead - 3);
/*     */     
/* 502 */     for (int i = 1; i <= stop; i++) {
/* 503 */       insertString(this.currentPosition + i);
/*     */     }
/* 505 */     this.missedInserts = matchLength - stop - 1;
/*     */   }
/*     */   
/*     */   private void catchUpMissedInserts() {
/* 509 */     while (this.missedInserts > 0) {
/* 510 */       insertString(this.currentPosition - this.missedInserts--);
/*     */     }
/*     */   }
/*     */   
/*     */   private void flushBackReference(int matchLength) throws IOException {
/* 515 */     this.callback.accept(new BackReference(this.currentPosition - this.matchStart, matchLength));
/*     */   }
/*     */   
/*     */   private void flushLiteralBlock() throws IOException {
/* 519 */     this.callback.accept(new LiteralBlock(this.window, this.blockStart, this.currentPosition - this.blockStart));
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
/*     */   private int longestMatch(int matchHead) {
/* 531 */     int minLength = this.params.getMinBackReferenceLength();
/* 532 */     int longestMatchLength = minLength - 1;
/* 533 */     int maxPossibleLength = Math.min(this.params.getMaxBackReferenceLength(), this.lookahead);
/* 534 */     int minIndex = Math.max(0, this.currentPosition - this.params.getMaxOffset());
/* 535 */     int niceBackReferenceLength = Math.min(maxPossibleLength, this.params.getNiceBackReferenceLength());
/* 536 */     int maxCandidates = this.params.getMaxCandidates();
/* 537 */     for (int candidates = 0; candidates < maxCandidates && matchHead >= minIndex; candidates++) {
/* 538 */       int currentLength = 0;
/* 539 */       for (int i = 0; i < maxPossibleLength && 
/* 540 */         this.window[matchHead + i] == this.window[this.currentPosition + i]; i++)
/*     */       {
/*     */         
/* 543 */         currentLength++;
/*     */       }
/* 545 */       if (currentLength > longestMatchLength) {
/* 546 */         longestMatchLength = currentLength;
/* 547 */         this.matchStart = matchHead;
/* 548 */         if (currentLength >= niceBackReferenceLength) {
/*     */           break;
/*     */         }
/*     */       } 
/*     */       
/* 553 */       matchHead = this.prev[matchHead & this.wMask];
/*     */     } 
/* 555 */     return longestMatchLength;
/*     */   }
/*     */   
/*     */   public static interface Callback {
/*     */     void accept(LZ77Compressor.Block param1Block) throws IOException;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\compressors\lz77support\LZ77Compressor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */