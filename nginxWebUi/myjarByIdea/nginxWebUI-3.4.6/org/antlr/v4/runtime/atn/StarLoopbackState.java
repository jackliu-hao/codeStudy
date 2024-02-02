package org.antlr.v4.runtime.atn;

public final class StarLoopbackState extends ATNState {
   public final StarLoopEntryState getLoopEntryState() {
      return (StarLoopEntryState)this.transition(0).target;
   }

   public int getStateType() {
      return 9;
   }
}
