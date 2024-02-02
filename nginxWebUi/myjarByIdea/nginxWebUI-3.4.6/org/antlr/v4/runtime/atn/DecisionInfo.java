package org.antlr.v4.runtime.atn;

import java.util.ArrayList;
import java.util.List;

public class DecisionInfo {
   public final int decision;
   public long invocations;
   public long timeInPrediction;
   public long SLL_TotalLook;
   public long SLL_MinLook;
   public long SLL_MaxLook;
   public LookaheadEventInfo SLL_MaxLookEvent;
   public long LL_TotalLook;
   public long LL_MinLook;
   public long LL_MaxLook;
   public LookaheadEventInfo LL_MaxLookEvent;
   public final List<ContextSensitivityInfo> contextSensitivities = new ArrayList();
   public final List<ErrorInfo> errors = new ArrayList();
   public final List<AmbiguityInfo> ambiguities = new ArrayList();
   public final List<PredicateEvalInfo> predicateEvals = new ArrayList();
   public long SLL_ATNTransitions;
   public long SLL_DFATransitions;
   public long LL_Fallback;
   public long LL_ATNTransitions;
   public long LL_DFATransitions;

   public DecisionInfo(int decision) {
      this.decision = decision;
   }

   public String toString() {
      return "{decision=" + this.decision + ", contextSensitivities=" + this.contextSensitivities.size() + ", errors=" + this.errors.size() + ", ambiguities=" + this.ambiguities.size() + ", SLL_lookahead=" + this.SLL_TotalLook + ", SLL_ATNTransitions=" + this.SLL_ATNTransitions + ", SLL_DFATransitions=" + this.SLL_DFATransitions + ", LL_Fallback=" + this.LL_Fallback + ", LL_lookahead=" + this.LL_TotalLook + ", LL_ATNTransitions=" + this.LL_ATNTransitions + '}';
   }
}
