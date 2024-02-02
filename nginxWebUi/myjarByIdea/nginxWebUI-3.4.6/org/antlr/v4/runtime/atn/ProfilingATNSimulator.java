package org.antlr.v4.runtime.atn;

import java.util.BitSet;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.dfa.DFAState;

public class ProfilingATNSimulator extends ParserATNSimulator {
   protected final DecisionInfo[] decisions;
   protected int numDecisions;
   protected int _sllStopIndex;
   protected int _llStopIndex;
   protected int currentDecision;
   protected DFAState currentState;
   protected int conflictingAltResolvedBySLL;

   public ProfilingATNSimulator(Parser parser) {
      super(parser, ((ParserATNSimulator)parser.getInterpreter()).atn, ((ParserATNSimulator)parser.getInterpreter()).decisionToDFA, ((ParserATNSimulator)parser.getInterpreter()).sharedContextCache);
      this.numDecisions = this.atn.decisionToState.size();
      this.decisions = new DecisionInfo[this.numDecisions];

      for(int i = 0; i < this.numDecisions; ++i) {
         this.decisions[i] = new DecisionInfo(i);
      }

   }

   public int adaptivePredict(TokenStream input, int decision, ParserRuleContext outerContext) {
      int LL_k;
      try {
         this._sllStopIndex = -1;
         this._llStopIndex = -1;
         this.currentDecision = decision;
         long start = System.nanoTime();
         int alt = super.adaptivePredict(input, decision, outerContext);
         long stop = System.nanoTime();
         DecisionInfo var10000 = this.decisions[decision];
         var10000.timeInPrediction += stop - start;
         ++this.decisions[decision].invocations;
         int SLL_k = this._sllStopIndex - this._startIndex + 1;
         var10000 = this.decisions[decision];
         var10000.SLL_TotalLook += (long)SLL_k;
         this.decisions[decision].SLL_MinLook = this.decisions[decision].SLL_MinLook == 0L ? (long)SLL_k : Math.min(this.decisions[decision].SLL_MinLook, (long)SLL_k);
         if ((long)SLL_k > this.decisions[decision].SLL_MaxLook) {
            this.decisions[decision].SLL_MaxLook = (long)SLL_k;
            this.decisions[decision].SLL_MaxLookEvent = new LookaheadEventInfo(decision, (ATNConfigSet)null, alt, input, this._startIndex, this._sllStopIndex, false);
         }

         if (this._llStopIndex >= 0) {
            LL_k = this._llStopIndex - this._startIndex + 1;
            var10000 = this.decisions[decision];
            var10000.LL_TotalLook += (long)LL_k;
            this.decisions[decision].LL_MinLook = this.decisions[decision].LL_MinLook == 0L ? (long)LL_k : Math.min(this.decisions[decision].LL_MinLook, (long)LL_k);
            if ((long)LL_k > this.decisions[decision].LL_MaxLook) {
               this.decisions[decision].LL_MaxLook = (long)LL_k;
               this.decisions[decision].LL_MaxLookEvent = new LookaheadEventInfo(decision, (ATNConfigSet)null, alt, input, this._startIndex, this._llStopIndex, true);
            }
         }

         LL_k = alt;
      } finally {
         this.currentDecision = -1;
      }

      return LL_k;
   }

   protected DFAState getExistingTargetState(DFAState previousD, int t) {
      this._sllStopIndex = this._input.index();
      DFAState existingTargetState = super.getExistingTargetState(previousD, t);
      if (existingTargetState != null) {
         ++this.decisions[this.currentDecision].SLL_DFATransitions;
         if (existingTargetState == ERROR) {
            this.decisions[this.currentDecision].errors.add(new ErrorInfo(this.currentDecision, previousD.configs, this._input, this._startIndex, this._sllStopIndex, false));
         }
      }

      this.currentState = existingTargetState;
      return existingTargetState;
   }

   protected DFAState computeTargetState(DFA dfa, DFAState previousD, int t) {
      DFAState state = super.computeTargetState(dfa, previousD, t);
      this.currentState = state;
      return state;
   }

   protected ATNConfigSet computeReachSet(ATNConfigSet closure, int t, boolean fullCtx) {
      if (fullCtx) {
         this._llStopIndex = this._input.index();
      }

      ATNConfigSet reachConfigs = super.computeReachSet(closure, t, fullCtx);
      if (fullCtx) {
         ++this.decisions[this.currentDecision].LL_ATNTransitions;
         if (reachConfigs == null) {
            this.decisions[this.currentDecision].errors.add(new ErrorInfo(this.currentDecision, closure, this._input, this._startIndex, this._llStopIndex, true));
         }
      } else {
         ++this.decisions[this.currentDecision].SLL_ATNTransitions;
         if (reachConfigs == null) {
            this.decisions[this.currentDecision].errors.add(new ErrorInfo(this.currentDecision, closure, this._input, this._startIndex, this._sllStopIndex, false));
         }
      }

      return reachConfigs;
   }

   protected boolean evalSemanticContext(SemanticContext pred, ParserRuleContext parserCallStack, int alt, boolean fullCtx) {
      boolean result = super.evalSemanticContext(pred, parserCallStack, alt, fullCtx);
      if (!(pred instanceof SemanticContext.PrecedencePredicate)) {
         boolean fullContext = this._llStopIndex >= 0;
         int stopIndex = fullContext ? this._llStopIndex : this._sllStopIndex;
         this.decisions[this.currentDecision].predicateEvals.add(new PredicateEvalInfo(this.currentDecision, this._input, this._startIndex, stopIndex, pred, result, alt, fullCtx));
      }

      return result;
   }

   protected void reportAttemptingFullContext(DFA dfa, BitSet conflictingAlts, ATNConfigSet configs, int startIndex, int stopIndex) {
      if (conflictingAlts != null) {
         this.conflictingAltResolvedBySLL = conflictingAlts.nextSetBit(0);
      } else {
         this.conflictingAltResolvedBySLL = configs.getAlts().nextSetBit(0);
      }

      ++this.decisions[this.currentDecision].LL_Fallback;
      super.reportAttemptingFullContext(dfa, conflictingAlts, configs, startIndex, stopIndex);
   }

   protected void reportContextSensitivity(DFA dfa, int prediction, ATNConfigSet configs, int startIndex, int stopIndex) {
      if (prediction != this.conflictingAltResolvedBySLL) {
         this.decisions[this.currentDecision].contextSensitivities.add(new ContextSensitivityInfo(this.currentDecision, configs, this._input, startIndex, stopIndex));
      }

      super.reportContextSensitivity(dfa, prediction, configs, startIndex, stopIndex);
   }

   protected void reportAmbiguity(DFA dfa, DFAState D, int startIndex, int stopIndex, boolean exact, BitSet ambigAlts, ATNConfigSet configs) {
      int prediction;
      if (ambigAlts != null) {
         prediction = ambigAlts.nextSetBit(0);
      } else {
         prediction = configs.getAlts().nextSetBit(0);
      }

      if (configs.fullCtx && prediction != this.conflictingAltResolvedBySLL) {
         this.decisions[this.currentDecision].contextSensitivities.add(new ContextSensitivityInfo(this.currentDecision, configs, this._input, startIndex, stopIndex));
      }

      this.decisions[this.currentDecision].ambiguities.add(new AmbiguityInfo(this.currentDecision, configs, ambigAlts, this._input, startIndex, stopIndex, configs.fullCtx));
      super.reportAmbiguity(dfa, D, startIndex, stopIndex, exact, ambigAlts, configs);
   }

   public DecisionInfo[] getDecisionInfo() {
      return this.decisions;
   }

   public DFAState getCurrentState() {
      return this.currentState;
   }
}
