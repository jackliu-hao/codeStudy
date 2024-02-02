package org.antlr.v4.runtime.atn;

import org.antlr.v4.runtime.misc.IntervalSet;

public final class RangeTransition extends Transition {
   public final int from;
   public final int to;

   public RangeTransition(ATNState target, int from, int to) {
      super(target);
      this.from = from;
      this.to = to;
   }

   public int getSerializationType() {
      return 2;
   }

   public IntervalSet label() {
      return IntervalSet.of(this.from, this.to);
   }

   public boolean matches(int symbol, int minVocabSymbol, int maxVocabSymbol) {
      return symbol >= this.from && symbol <= this.to;
   }

   public String toString() {
      return "'" + (char)this.from + "'..'" + (char)this.to + "'";
   }
}
