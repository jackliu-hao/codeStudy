package com.google.zxing.aztec.encoder;

import com.google.zxing.common.BitArray;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public final class HighLevelEncoder {
   static final String[] MODE_NAMES = new String[]{"UPPER", "LOWER", "DIGIT", "MIXED", "PUNCT"};
   static final int MODE_UPPER = 0;
   static final int MODE_LOWER = 1;
   static final int MODE_DIGIT = 2;
   static final int MODE_MIXED = 3;
   static final int MODE_PUNCT = 4;
   static final int[][] LATCH_TABLE = new int[][]{{0, 327708, 327710, 327709, 656318}, {590318, 0, 327710, 327709, 656318}, {262158, 590300, 0, 590301, 932798}, {327709, 327708, 656318, 0, 327710}, {327711, 656380, 656382, 656381, 0}};
   private static final int[][] CHAR_MAP;
   static final int[][] SHIFT_TABLE;
   private final byte[] text;

   public HighLevelEncoder(byte[] text) {
      this.text = text;
   }

   public BitArray encode() {
      Collection<State> states = Collections.singletonList(State.INITIAL_STATE);

      for(int index = 0; index < this.text.length; ++index) {
         int nextChar = index + 1 < this.text.length ? this.text[index + 1] : 0;
         int pairCode;
         switch (this.text[index]) {
            case 13:
               pairCode = nextChar == 10 ? 2 : 0;
               break;
            case 44:
               pairCode = nextChar == 32 ? 4 : 0;
               break;
            case 46:
               pairCode = nextChar == 32 ? 3 : 0;
               break;
            case 58:
               pairCode = nextChar == 32 ? 5 : 0;
               break;
            default:
               pairCode = 0;
         }

         if (pairCode > 0) {
            states = updateStateListForPair((Iterable)states, index, pairCode);
            ++index;
         } else {
            states = this.updateStateListForChar((Iterable)states, index);
         }
      }

      return ((State)Collections.min((Collection)states, new Comparator<State>() {
         public int compare(State a, State b) {
            return a.getBitCount() - b.getBitCount();
         }
      })).toBitArray(this.text);
   }

   private Collection<State> updateStateListForChar(Iterable<State> states, int index) {
      Collection<State> result = new LinkedList();
      Iterator var4 = states.iterator();

      while(var4.hasNext()) {
         State state = (State)var4.next();
         this.updateStateForChar(state, index, result);
      }

      return simplifyStates(result);
   }

   private void updateStateForChar(State state, int index, Collection<State> result) {
      char ch = (char)(this.text[index] & 255);
      boolean charInCurrentTable = CHAR_MAP[state.getMode()][ch] > 0;
      State stateNoBinary = null;

      for(int mode = 0; mode <= 4; ++mode) {
         int charInMode;
         if ((charInMode = CHAR_MAP[mode][ch]) > 0) {
            if (stateNoBinary == null) {
               stateNoBinary = state.endBinaryShift(index);
            }

            State shiftState;
            if (!charInCurrentTable || mode == state.getMode() || mode == 2) {
               shiftState = stateNoBinary.latchAndAppend(mode, charInMode);
               result.add(shiftState);
            }

            if (!charInCurrentTable && SHIFT_TABLE[state.getMode()][mode] >= 0) {
               shiftState = stateNoBinary.shiftAndAppend(mode, charInMode);
               result.add(shiftState);
            }
         }
      }

      if (state.getBinaryShiftByteCount() > 0 || CHAR_MAP[state.getMode()][ch] == 0) {
         State binaryState = state.addBinaryShiftChar(index);
         result.add(binaryState);
      }

   }

   private static Collection<State> updateStateListForPair(Iterable<State> states, int index, int pairCode) {
      Collection<State> result = new LinkedList();
      Iterator var4 = states.iterator();

      while(var4.hasNext()) {
         updateStateForPair((State)var4.next(), index, pairCode, result);
      }

      return simplifyStates(result);
   }

   private static void updateStateForPair(State state, int index, int pairCode, Collection<State> result) {
      State stateNoBinary = state.endBinaryShift(index);
      result.add(stateNoBinary.latchAndAppend(4, pairCode));
      if (state.getMode() != 4) {
         result.add(stateNoBinary.shiftAndAppend(4, pairCode));
      }

      State binaryState;
      if (pairCode == 3 || pairCode == 4) {
         binaryState = stateNoBinary.latchAndAppend(2, 16 - pairCode).latchAndAppend(2, 1);
         result.add(binaryState);
      }

      if (state.getBinaryShiftByteCount() > 0) {
         binaryState = state.addBinaryShiftChar(index).addBinaryShiftChar(index + 1);
         result.add(binaryState);
      }

   }

   private static Collection<State> simplifyStates(Iterable<State> states) {
      List<State> result = new LinkedList();
      Iterator var2 = states.iterator();

      while(var2.hasNext()) {
         State newState = (State)var2.next();
         boolean add = true;
         Iterator<State> iterator = result.iterator();

         while(iterator.hasNext()) {
            State oldState;
            if ((oldState = (State)iterator.next()).isBetterThanOrEqualTo(newState)) {
               add = false;
               break;
            }

            if (newState.isBetterThanOrEqualTo(oldState)) {
               iterator.remove();
            }
         }

         if (add) {
            result.add(newState);
         }
      }

      return result;
   }

   static {
      (CHAR_MAP = new int[5][256])[0][32] = 1;

      int c;
      for(c = 65; c <= 90; ++c) {
         CHAR_MAP[0][c] = c - 65 + 2;
      }

      CHAR_MAP[1][32] = 1;

      for(c = 97; c <= 122; ++c) {
         CHAR_MAP[1][c] = c - 97 + 2;
      }

      CHAR_MAP[2][32] = 1;

      for(c = 48; c <= 57; ++c) {
         CHAR_MAP[2][c] = c - 48 + 2;
      }

      CHAR_MAP[2][44] = 12;
      CHAR_MAP[2][46] = 13;
      int[] mixedTable = new int[]{0, 32, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 27, 28, 29, 30, 31, 64, 92, 94, 95, 96, 124, 126, 127};

      int i;
      for(i = 0; i < 28; CHAR_MAP[3][mixedTable[i]] = i++) {
      }

      int[] punctTable = new int[]{0, 13, 0, 0, 0, 0, 33, 39, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 58, 59, 60, 61, 62, 63, 91, 93, 123, 125};

      int i;
      for(i = 0; i < 31; ++i) {
         if (punctTable[i] > 0) {
            CHAR_MAP[4][punctTable[i]] = i;
         }
      }

      int[][] var5;
      i = (var5 = SHIFT_TABLE = new int[6][6]).length;

      for(i = 0; i < i; ++i) {
         Arrays.fill(var5[i], -1);
      }

      SHIFT_TABLE[0][4] = 0;
      SHIFT_TABLE[1][4] = 0;
      SHIFT_TABLE[1][0] = 28;
      SHIFT_TABLE[3][4] = 0;
      SHIFT_TABLE[2][4] = 0;
      SHIFT_TABLE[2][0] = 15;
   }
}
