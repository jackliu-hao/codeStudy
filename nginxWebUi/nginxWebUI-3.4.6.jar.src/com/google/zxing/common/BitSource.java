/*     */ package com.google.zxing.common;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class BitSource
/*     */ {
/*     */   private final byte[] bytes;
/*     */   private int byteOffset;
/*     */   private int bitOffset;
/*     */   
/*     */   public BitSource(byte[] bytes) {
/*  39 */     this.bytes = bytes;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getBitOffset() {
/*  46 */     return this.bitOffset;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getByteOffset() {
/*  53 */     return this.byteOffset;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int readBits(int numBits) {
/*  63 */     if (numBits <= 0 || numBits > 32 || numBits > available()) {
/*  64 */       throw new IllegalArgumentException(String.valueOf(numBits));
/*     */     }
/*     */     
/*  67 */     int result = 0;
/*     */ 
/*     */     
/*  70 */     if (this.bitOffset > 0) {
/*  71 */       int bitsLeft = 8 - this.bitOffset;
/*  72 */       int toRead = (numBits < bitsLeft) ? numBits : bitsLeft;
/*  73 */       int bitsToNotRead = bitsLeft - toRead;
/*  74 */       int mask = 255 >> 8 - toRead << bitsToNotRead;
/*  75 */       result = (this.bytes[this.byteOffset] & mask) >> bitsToNotRead;
/*  76 */       numBits -= toRead;
/*  77 */       this.bitOffset += toRead;
/*  78 */       if (this.bitOffset == 8) {
/*  79 */         this.bitOffset = 0;
/*  80 */         this.byteOffset++;
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/*  85 */     if (numBits > 0) {
/*  86 */       while (numBits >= 8) {
/*  87 */         result = result << 8 | this.bytes[this.byteOffset] & 0xFF;
/*  88 */         this.byteOffset++;
/*  89 */         numBits -= 8;
/*     */       } 
/*     */ 
/*     */       
/*  93 */       if (numBits > 0) {
/*  94 */         int bitsToNotRead = 8 - numBits;
/*  95 */         int mask = 255 >> bitsToNotRead << bitsToNotRead;
/*  96 */         result = result << numBits | (this.bytes[this.byteOffset] & mask) >> bitsToNotRead;
/*  97 */         this.bitOffset += numBits;
/*     */       } 
/*     */     } 
/*     */     
/* 101 */     return result;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int available() {
/* 108 */     return 8 * (this.bytes.length - this.byteOffset) - this.bitOffset;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\zxing\common\BitSource.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */