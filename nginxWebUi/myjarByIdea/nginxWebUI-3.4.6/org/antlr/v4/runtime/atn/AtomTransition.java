package org.antlr.v4.runtime.atn;

import org.antlr.v4.runtime.misc.IntervalSet;

public final class AtomTransition extends Transition {
   public final int label;

   public AtomTransition(ATNState target, int label) {
      super(target);
      this.label = label;
   }

   public int getSerializationType() {
      return 5;
   }

   public IntervalSet label() {
      return IntervalSet.of(this.label);
   }

   public boolean matches(int symbol, int minVocabSymbol, int maxVocabSymbol) {
      return this.label == symbol;
   }

   public String toString() {
      return String.valueOf(this.label);
   }
}
