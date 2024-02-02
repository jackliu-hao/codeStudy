package com.google.zxing.aztec.encoder;

import com.google.zxing.common.BitArray;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;

final class State {
   static final State INITIAL_STATE;
   private final int mode;
   private final Token token;
   private final int binaryShiftByteCount;
   private final int bitCount;

   private State(Token token, int mode, int binaryBytes, int bitCount) {
      this.token = token;
      this.mode = mode;
      this.binaryShiftByteCount = binaryBytes;
      this.bitCount = bitCount;
   }

   int getMode() {
      return this.mode;
   }

   Token getToken() {
      return this.token;
   }

   int getBinaryShiftByteCount() {
      return this.binaryShiftByteCount;
   }

   int getBitCount() {
      return this.bitCount;
   }

   State latchAndAppend(int mode, int value) {
      int bitCount = this.bitCount;
      Token token = this.token;
      int latchModeBitCount;
      if (mode != this.mode) {
         latchModeBitCount = HighLevelEncoder.LATCH_TABLE[this.mode][mode];
         token = token.add(latchModeBitCount & '\uffff', latchModeBitCount >> 16);
         bitCount += latchModeBitCount >> 16;
      }

      latchModeBitCount = mode == 2 ? 4 : 5;
      token = token.add(value, latchModeBitCount);
      return new State(token, mode, 0, bitCount + latchModeBitCount);
   }

   State shiftAndAppend(int mode, int value) {
      Token token = this.token;
      int thisModeBitCount = this.mode == 2 ? 4 : 5;
      token = token.add(HighLevelEncoder.SHIFT_TABLE[this.mode][mode], thisModeBitCount).add(value, 5);
      return new State(token, this.mode, 0, this.bitCount + thisModeBitCount + 5);
   }

   State addBinaryShiftChar(int index) {
      Token token = this.token;
      int mode = this.mode;
      int bitCount = this.bitCount;
      int deltaBitCount;
      if (this.mode == 4 || this.mode == 2) {
         deltaBitCount = HighLevelEncoder.LATCH_TABLE[mode][0];
         token = token.add(deltaBitCount & '\uffff', deltaBitCount >> 16);
         bitCount += deltaBitCount >> 16;
         mode = 0;
      }

      deltaBitCount = this.binaryShiftByteCount != 0 && this.binaryShiftByteCount != 31 ? (this.binaryShiftByteCount == 62 ? 9 : 8) : 18;
      State result;
      if ((result = new State(token, mode, this.binaryShiftByteCount + 1, bitCount + deltaBitCount)).binaryShiftByteCount == 2078) {
         result = result.endBinaryShift(index + 1);
      }

      return result;
   }

   State endBinaryShift(int index) {
      if (this.binaryShiftByteCount == 0) {
         return this;
      } else {
         Token token = this.token.addBinaryShift(index - this.binaryShiftByteCount, this.binaryShiftByteCount);
         return new State(token, this.mode, 0, this.bitCount);
      }
   }

   boolean isBetterThanOrEqualTo(State other) {
      int mySize = this.bitCount + (HighLevelEncoder.LATCH_TABLE[this.mode][other.mode] >> 16);
      if (other.binaryShiftByteCount > 0 && (this.binaryShiftByteCount == 0 || this.binaryShiftByteCount > other.binaryShiftByteCount)) {
         mySize += 10;
      }

      return mySize <= other.bitCount;
   }

   BitArray toBitArray(byte[] text) {
      Deque<Token> symbols = new LinkedList();

      for(Token token = this.endBinaryShift(text.length).token; token != null; token = token.getPrevious()) {
         symbols.addFirst(token);
      }

      BitArray bitArray = new BitArray();
      Iterator var4 = symbols.iterator();

      while(var4.hasNext()) {
         ((Token)var4.next()).appendTo(bitArray, text);
      }

      return bitArray;
   }

   public String toString() {
      return String.format("%s bits=%d bytes=%d", HighLevelEncoder.MODE_NAMES[this.mode], this.bitCount, this.binaryShiftByteCount);
   }

   static {
      INITIAL_STATE = new State(Token.EMPTY, 0, 0, 0);
   }
}
