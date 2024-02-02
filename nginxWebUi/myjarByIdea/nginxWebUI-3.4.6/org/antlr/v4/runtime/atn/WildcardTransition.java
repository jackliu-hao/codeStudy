package org.antlr.v4.runtime.atn;

public final class WildcardTransition extends Transition {
   public WildcardTransition(ATNState target) {
      super(target);
   }

   public int getSerializationType() {
      return 9;
   }

   public boolean matches(int symbol, int minVocabSymbol, int maxVocabSymbol) {
      return symbol >= minVocabSymbol && symbol <= maxVocabSymbol;
   }

   public String toString() {
      return ".";
   }
}
