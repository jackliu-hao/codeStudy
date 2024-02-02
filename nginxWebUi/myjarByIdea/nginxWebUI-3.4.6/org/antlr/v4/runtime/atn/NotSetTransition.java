package org.antlr.v4.runtime.atn;

import org.antlr.v4.runtime.misc.IntervalSet;

public final class NotSetTransition extends SetTransition {
   public NotSetTransition(ATNState target, IntervalSet set) {
      super(target, set);
   }

   public int getSerializationType() {
      return 8;
   }

   public boolean matches(int symbol, int minVocabSymbol, int maxVocabSymbol) {
      return symbol >= minVocabSymbol && symbol <= maxVocabSymbol && !super.matches(symbol, minVocabSymbol, maxVocabSymbol);
   }

   public String toString() {
      return '~' + super.toString();
   }
}
