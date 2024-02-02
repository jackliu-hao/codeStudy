package org.antlr.v4.runtime.atn;

public final class RuleStartState extends ATNState {
   public RuleStopState stopState;
   public boolean isLeftRecursiveRule;

   public int getStateType() {
      return 2;
   }
}
