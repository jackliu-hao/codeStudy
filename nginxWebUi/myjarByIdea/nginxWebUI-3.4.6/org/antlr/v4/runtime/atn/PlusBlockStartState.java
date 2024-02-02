package org.antlr.v4.runtime.atn;

public final class PlusBlockStartState extends BlockStartState {
   public PlusLoopbackState loopBackState;

   public int getStateType() {
      return 4;
   }
}
