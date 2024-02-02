/*     */ package com.google.zxing.aztec.encoder;
/*     */ 
/*     */ import com.google.zxing.common.BitArray;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.Comparator;
/*     */ import java.util.Iterator;
/*     */ import java.util.LinkedList;
/*     */ import java.util.List;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class HighLevelEncoder
/*     */ {
/*  43 */   static final String[] MODE_NAMES = new String[] { "UPPER", "LOWER", "DIGIT", "MIXED", "PUNCT" };
/*     */ 
/*     */   
/*     */   static final int MODE_UPPER = 0;
/*     */   
/*     */   static final int MODE_LOWER = 1;
/*     */   
/*     */   static final int MODE_DIGIT = 2;
/*     */   
/*     */   static final int MODE_MIXED = 3;
/*     */   
/*     */   static final int MODE_PUNCT = 4;
/*     */   
/*  56 */   static final int[][] LATCH_TABLE = new int[][] { { 0, 327708, 327710, 327709, 656318 }, { 590318, 0, 327710, 327709, 656318 }, { 262158, 590300, 0, 590301, 932798 }, { 327709, 327708, 656318, 0, 327710 }, { 327711, 656380, 656382, 656381, 0 } };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static final int[][] CHAR_MAP;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static final int[][] SHIFT_TABLE;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final byte[] text;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static {
/*  99 */     (CHAR_MAP = new int[5][256])[0][32] = 1; int c;
/* 100 */     for (c = 65; c <= 90; c++) {
/* 101 */       CHAR_MAP[0][c] = c - 65 + 2;
/*     */     }
/* 103 */     CHAR_MAP[1][32] = 1;
/* 104 */     for (c = 97; c <= 122; c++) {
/* 105 */       CHAR_MAP[1][c] = c - 97 + 2;
/*     */     }
/* 107 */     CHAR_MAP[2][32] = 1;
/* 108 */     for (c = 48; c <= 57; c++) {
/* 109 */       CHAR_MAP[2][c] = c - 48 + 2;
/*     */     }
/* 111 */     CHAR_MAP[2][44] = 12;
/* 112 */     CHAR_MAP[2][46] = 13;
/* 113 */     int[] mixedTable = { 0, 32, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 27, 28, 29, 30, 31, 64, 92, 94, 95, 96, 124, 126, 127 };
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 118 */     for (int i = 0; i < 28; i++) {
/* 119 */       CHAR_MAP[3][mixedTable[i]] = i;
/*     */     }
/* 121 */     int[] punctTable = { 0, 13, 0, 0, 0, 0, 33, 39, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 58, 59, 60, 61, 62, 63, 91, 93, 123, 125 };
/*     */ 
/*     */     
/*     */     int k;
/*     */     
/* 126 */     for (k = 0; k < 31; k++) {
/* 127 */       if (punctTable[k] > 0) {
/* 128 */         CHAR_MAP[4][punctTable[k]] = k;
/*     */       }
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/*     */     int[][] arrayOfInt;
/*     */ 
/*     */     
/* 137 */     for (int j = (arrayOfInt = SHIFT_TABLE = new int[6][6]).length; k < j; k++) {
/* 138 */       Arrays.fill(arrayOfInt[k], -1);
/*     */     }
/* 140 */     SHIFT_TABLE[0][4] = 0;
/*     */     
/* 142 */     SHIFT_TABLE[1][4] = 0;
/* 143 */     SHIFT_TABLE[1][0] = 28;
/*     */     
/* 145 */     SHIFT_TABLE[3][4] = 0;
/*     */     
/* 147 */     SHIFT_TABLE[2][4] = 0;
/* 148 */     SHIFT_TABLE[2][0] = 15;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public HighLevelEncoder(byte[] text) {
/* 154 */     this.text = text;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public BitArray encode() {
/* 161 */     Collection<State> states = Collections.singletonList(State.INITIAL_STATE);
/* 162 */     for (int index = 0; index < this.text.length; index++) {
/*     */       
/* 164 */       int pairCode, nextChar = (index + 1 < this.text.length) ? this.text[index + 1] : 0;
/* 165 */       switch (this.text[index]) {
/*     */         case 13:
/* 167 */           pairCode = (nextChar == 10) ? 2 : 0;
/*     */           break;
/*     */         case 46:
/* 170 */           pairCode = (nextChar == 32) ? 3 : 0;
/*     */           break;
/*     */         case 44:
/* 173 */           pairCode = (nextChar == 32) ? 4 : 0;
/*     */           break;
/*     */         case 58:
/* 176 */           pairCode = (nextChar == 32) ? 5 : 0;
/*     */           break;
/*     */         default:
/* 179 */           pairCode = 0; break;
/*     */       } 
/* 181 */       if (pairCode > 0) {
/*     */ 
/*     */         
/* 184 */         states = updateStateListForPair(states, index, pairCode);
/* 185 */         index++;
/*     */       } else {
/*     */         
/* 188 */         states = updateStateListForChar(states, index);
/*     */       } 
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 199 */     return ((State)Collections.<State>min(states, new Comparator<State>() { public int compare(State a, State b) { return a.getBitCount() - b.getBitCount(); } })).toBitArray(this.text);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private Collection<State> updateStateListForChar(Iterable<State> states, int index) {
/* 206 */     Collection<State> result = new LinkedList<>();
/* 207 */     for (State state : states) {
/* 208 */       updateStateForChar(state, index, result);
/*     */     }
/* 210 */     return simplifyStates(result);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void updateStateForChar(State state, int index, Collection<State> result) {
/* 217 */     char ch = (char)(this.text[index] & 0xFF);
/* 218 */     boolean charInCurrentTable = (CHAR_MAP[state.getMode()][ch] > 0);
/* 219 */     State stateNoBinary = null;
/* 220 */     for (int mode = 0; mode <= 4; mode++) {
/*     */       int charInMode;
/* 222 */       if ((charInMode = CHAR_MAP[mode][ch]) > 0) {
/* 223 */         if (stateNoBinary == null)
/*     */         {
/* 225 */           stateNoBinary = state.endBinaryShift(index);
/*     */         }
/*     */         
/* 228 */         if (!charInCurrentTable || mode == state.getMode() || mode == 2) {
/*     */ 
/*     */ 
/*     */ 
/*     */           
/* 233 */           State latchState = stateNoBinary.latchAndAppend(mode, charInMode);
/* 234 */           result.add(latchState);
/*     */         } 
/*     */         
/* 237 */         if (!charInCurrentTable && SHIFT_TABLE[state.getMode()][mode] >= 0) {
/*     */ 
/*     */           
/* 240 */           State shiftState = stateNoBinary.shiftAndAppend(mode, charInMode);
/* 241 */           result.add(shiftState);
/*     */         } 
/*     */       } 
/*     */     } 
/* 245 */     if (state.getBinaryShiftByteCount() > 0 || CHAR_MAP[state.getMode()][ch] == 0) {
/*     */ 
/*     */ 
/*     */       
/* 249 */       State binaryState = state.addBinaryShiftChar(index);
/* 250 */       result.add(binaryState);
/*     */     } 
/*     */   }
/*     */   
/*     */   private static Collection<State> updateStateListForPair(Iterable<State> states, int index, int pairCode) {
/* 255 */     Collection<State> result = new LinkedList<>();
/* 256 */     for (Iterator<State> iterator = states.iterator(); iterator.hasNext();) {
/* 257 */       updateStateForPair(iterator.next(), index, pairCode, result);
/*     */     }
/* 259 */     return simplifyStates(result);
/*     */   }
/*     */   
/*     */   private static void updateStateForPair(State state, int index, int pairCode, Collection<State> result) {
/* 263 */     State stateNoBinary = state.endBinaryShift(index);
/*     */     
/* 265 */     result.add(stateNoBinary.latchAndAppend(4, pairCode));
/* 266 */     if (state.getMode() != 4)
/*     */     {
/*     */       
/* 269 */       result.add(stateNoBinary.shiftAndAppend(4, pairCode));
/*     */     }
/* 271 */     if (pairCode == 3 || pairCode == 4) {
/*     */ 
/*     */ 
/*     */       
/* 275 */       State digitState = stateNoBinary.latchAndAppend(2, 16 - pairCode).latchAndAppend(2, 1);
/* 276 */       result.add(digitState);
/*     */     } 
/* 278 */     if (state.getBinaryShiftByteCount() > 0) {
/*     */ 
/*     */       
/* 281 */       State binaryState = state.addBinaryShiftChar(index).addBinaryShiftChar(index + 1);
/* 282 */       result.add(binaryState);
/*     */     } 
/*     */   }
/*     */   
/*     */   private static Collection<State> simplifyStates(Iterable<State> states) {
/* 287 */     List<State> result = new LinkedList<>();
/* 288 */     for (State newState : states) {
/* 289 */       boolean add = true;
/* 290 */       for (Iterator<State> iterator = result.iterator(); iterator.hasNext(); ) {
/*     */         State oldState;
/* 292 */         if ((oldState = iterator.next()).isBetterThanOrEqualTo(newState)) {
/* 293 */           add = false;
/*     */           break;
/*     */         } 
/* 296 */         if (newState.isBetterThanOrEqualTo(oldState)) {
/* 297 */           iterator.remove();
/*     */         }
/*     */       } 
/* 300 */       if (add) {
/* 301 */         result.add(newState);
/*     */       }
/*     */     } 
/* 304 */     return result;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\zxing\aztec\encoder\HighLevelEncoder.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */