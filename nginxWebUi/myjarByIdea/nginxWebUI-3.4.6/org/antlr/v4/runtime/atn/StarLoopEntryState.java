package org.antlr.v4.runtime.atn;

public final class StarLoopEntryState extends DecisionState {
   public StarLoopbackState loopBackState;
   public boolean isPrecedenceDecision;

   public int getStateType() {
      return 10;
   }
}
