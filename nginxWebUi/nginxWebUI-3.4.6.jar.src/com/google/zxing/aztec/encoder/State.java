/*     */ package com.google.zxing.aztec.encoder;
/*     */ 
/*     */ import com.google.zxing.common.BitArray;
/*     */ import java.util.Deque;
/*     */ import java.util.Iterator;
/*     */ import java.util.LinkedList;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ final class State
/*     */ {
/*  30 */   static final State INITIAL_STATE = new State(Token.EMPTY, 0, 0, 0);
/*     */ 
/*     */   
/*     */   private final int mode;
/*     */ 
/*     */   
/*     */   private final Token token;
/*     */ 
/*     */   
/*     */   private final int binaryShiftByteCount;
/*     */   
/*     */   private final int bitCount;
/*     */ 
/*     */   
/*     */   private State(Token token, int mode, int binaryBytes, int bitCount) {
/*  45 */     this.token = token;
/*  46 */     this.mode = mode;
/*  47 */     this.binaryShiftByteCount = binaryBytes;
/*  48 */     this.bitCount = bitCount;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   int getMode() {
/*  58 */     return this.mode;
/*     */   }
/*     */   
/*     */   Token getToken() {
/*  62 */     return this.token;
/*     */   }
/*     */   
/*     */   int getBinaryShiftByteCount() {
/*  66 */     return this.binaryShiftByteCount;
/*     */   }
/*     */   
/*     */   int getBitCount() {
/*  70 */     return this.bitCount;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   State latchAndAppend(int mode, int value) {
/*  77 */     int bitCount = this.bitCount;
/*  78 */     Token token = this.token;
/*  79 */     if (mode != this.mode) {
/*  80 */       int latch = HighLevelEncoder.LATCH_TABLE[this.mode][mode];
/*  81 */       token = token.add(latch & 0xFFFF, latch >> 16);
/*  82 */       bitCount += latch >> 16;
/*     */     } 
/*  84 */     int latchModeBitCount = (mode == 2) ? 4 : 5;
/*  85 */     token = token.add(value, latchModeBitCount);
/*  86 */     return new State(token, mode, 0, bitCount + latchModeBitCount);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   State shiftAndAppend(int mode, int value) {
/*  93 */     Token token = this.token;
/*  94 */     int thisModeBitCount = (this.mode == 2) ? 4 : 5;
/*     */ 
/*     */     
/*  97 */     token = token.add(HighLevelEncoder.SHIFT_TABLE[this.mode][mode], thisModeBitCount).add(value, 5);
/*  98 */     return new State(token, this.mode, 0, this.bitCount + thisModeBitCount + 5);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   State addBinaryShiftChar(int index) {
/* 104 */     Token token = this.token;
/* 105 */     int mode = this.mode;
/* 106 */     int bitCount = this.bitCount;
/* 107 */     if (this.mode == 4 || this.mode == 2) {
/*     */       
/* 109 */       int latch = HighLevelEncoder.LATCH_TABLE[mode][0];
/* 110 */       token = token.add(latch & 0xFFFF, latch >> 16);
/* 111 */       bitCount += latch >> 16;
/* 112 */       mode = 0;
/*     */     } 
/* 114 */     int deltaBitCount = (this.binaryShiftByteCount == 0 || this.binaryShiftByteCount == 31) ? 18 : ((this.binaryShiftByteCount == 62) ? 9 : 8);
/*     */     
/*     */     State result;
/*     */     
/* 118 */     if ((result = new State(token, mode, this.binaryShiftByteCount + 1, bitCount + deltaBitCount)).binaryShiftByteCount == 2078)
/*     */     {
/* 120 */       result = result.endBinaryShift(index + 1);
/*     */     }
/* 122 */     return result;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   State endBinaryShift(int index) {
/* 128 */     if (this.binaryShiftByteCount == 0) {
/* 129 */       return this;
/*     */     }
/*     */     
/* 132 */     Token token = this.token.addBinaryShift(index - this.binaryShiftByteCount, this.binaryShiftByteCount);
/*     */     
/* 134 */     return new State(token, this.mode, 0, this.bitCount);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   boolean isBetterThanOrEqualTo(State other) {
/* 140 */     int mySize = this.bitCount + (HighLevelEncoder.LATCH_TABLE[this.mode][other.mode] >> 16);
/* 141 */     if (other.binaryShiftByteCount > 0 && (this.binaryShiftByteCount == 0 || this.binaryShiftByteCount > other.binaryShiftByteCount))
/*     */     {
/* 143 */       mySize += 10;
/*     */     }
/* 145 */     return (mySize <= other.bitCount);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   BitArray toBitArray(byte[] text) {
/* 151 */     Deque<Token> symbols = new LinkedList<>();
/* 152 */     for (Token token = (endBinaryShift(text.length)).token; token != null; token = token.getPrevious()) {
/* 153 */       symbols.addFirst(token);
/*     */     }
/* 155 */     BitArray bitArray = new BitArray();
/*     */     
/* 157 */     for (Iterator<Token> iterator = symbols.iterator(); iterator.hasNext();) ((Token)iterator.next())
/* 158 */         .appendTo(bitArray, text);
/*     */ 
/*     */     
/* 161 */     return bitArray;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 166 */     return String.format("%s bits=%d bytes=%d", new Object[] { HighLevelEncoder.MODE_NAMES[this.mode], Integer.valueOf(this.bitCount), Integer.valueOf(this.binaryShiftByteCount) });
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\zxing\aztec\encoder\State.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */