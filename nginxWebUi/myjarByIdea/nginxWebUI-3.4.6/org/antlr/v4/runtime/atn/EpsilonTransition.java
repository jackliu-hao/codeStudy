package org.antlr.v4.runtime.atn;

public final class EpsilonTransition extends Transition {
   private final int outermostPrecedenceReturn;

   public EpsilonTransition(ATNState target) {
      this(target, -1);
   }

   public EpsilonTransition(ATNState target, int outermostPrecedenceReturn) {
      super(target);
      this.outermostPrecedenceReturn = outermostPrecedenceReturn;
   }

   public int outermostPrecedenceReturn() {
      return this.outermostPrecedenceReturn;
   }

   public int getSerializationType() {
      return 1;
   }

   public boolean isEpsilon() {
      return true;
   }

   public boolean matches(int symbol, int minVocabSymbol, int maxVocabSymbol) {
      return false;
   }

   public String toString() {
      return "epsilon";
   }
}
