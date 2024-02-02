package org.antlr.v4.runtime.atn;

import org.antlr.v4.runtime.TokenStream;

public class LookaheadEventInfo extends DecisionEventInfo {
   public int predictedAlt;

   public LookaheadEventInfo(int decision, ATNConfigSet configs, int predictedAlt, TokenStream input, int startIndex, int stopIndex, boolean fullCtx) {
      super(decision, configs, input, startIndex, stopIndex, fullCtx);
      this.predictedAlt = predictedAlt;
   }
}
