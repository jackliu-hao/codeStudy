package org.antlr.v4.runtime.atn;

public final class ActionTransition extends Transition {
   public final int ruleIndex;
   public final int actionIndex;
   public final boolean isCtxDependent;

   public ActionTransition(ATNState target, int ruleIndex) {
      this(target, ruleIndex, -1, false);
   }

   public ActionTransition(ATNState target, int ruleIndex, int actionIndex, boolean isCtxDependent) {
      super(target);
      this.ruleIndex = ruleIndex;
      this.actionIndex = actionIndex;
      this.isCtxDependent = isCtxDependent;
   }

   public int getSerializationType() {
      return 6;
   }

   public boolean isEpsilon() {
      return true;
   }

   public boolean matches(int symbol, int minVocabSymbol, int maxVocabSymbol) {
      return false;
   }

   public String toString() {
      return "action_" + this.ruleIndex + ":" + this.actionIndex;
   }
}
