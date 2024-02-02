package org.antlr.v4.runtime.atn;

import java.util.BitSet;
import org.antlr.v4.runtime.TokenStream;

public class AmbiguityInfo extends DecisionEventInfo {
   public BitSet ambigAlts;

   public AmbiguityInfo(int decision, ATNConfigSet configs, BitSet ambigAlts, TokenStream input, int startIndex, int stopIndex, boolean fullCtx) {
      super(decision, configs, input, startIndex, stopIndex, fullCtx);
      this.ambigAlts = ambigAlts;
   }
}
