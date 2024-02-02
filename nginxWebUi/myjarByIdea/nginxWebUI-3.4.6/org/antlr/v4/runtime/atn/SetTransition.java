package org.antlr.v4.runtime.atn;

import org.antlr.v4.runtime.misc.IntervalSet;

public class SetTransition extends Transition {
   public final IntervalSet set;

   public SetTransition(ATNState target, IntervalSet set) {
      super(target);
      if (set == null) {
         set = IntervalSet.of(0);
      }

      this.set = set;
   }

   public int getSerializationType() {
      return 7;
   }

   public IntervalSet label() {
      return this.set;
   }

   public boolean matches(int symbol, int minVocabSymbol, int maxVocabSymbol) {
      return this.set.contains(symbol);
   }

   public String toString() {
      return this.set.toString();
   }
}
