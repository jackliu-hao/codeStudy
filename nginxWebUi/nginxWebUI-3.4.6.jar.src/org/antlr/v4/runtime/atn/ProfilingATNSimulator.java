/*     */ package org.antlr.v4.runtime.atn;
/*     */ 
/*     */ import java.util.BitSet;
/*     */ import org.antlr.v4.runtime.Parser;
/*     */ import org.antlr.v4.runtime.ParserRuleContext;
/*     */ import org.antlr.v4.runtime.TokenStream;
/*     */ import org.antlr.v4.runtime.dfa.DFA;
/*     */ import org.antlr.v4.runtime.dfa.DFAState;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ProfilingATNSimulator
/*     */   extends ParserATNSimulator
/*     */ {
/*     */   protected final DecisionInfo[] decisions;
/*     */   protected int numDecisions;
/*     */   protected int _sllStopIndex;
/*     */   protected int _llStopIndex;
/*     */   protected int currentDecision;
/*     */   protected DFAState currentState;
/*     */   protected int conflictingAltResolvedBySLL;
/*     */   
/*     */   public ProfilingATNSimulator(Parser parser) {
/*  68 */     super(parser, (parser.getInterpreter()).atn, (parser.getInterpreter()).decisionToDFA, (parser.getInterpreter()).sharedContextCache);
/*     */ 
/*     */ 
/*     */     
/*  72 */     this.numDecisions = this.atn.decisionToState.size();
/*  73 */     this.decisions = new DecisionInfo[this.numDecisions];
/*  74 */     for (int i = 0; i < this.numDecisions; i++) {
/*  75 */       this.decisions[i] = new DecisionInfo(i);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public int adaptivePredict(TokenStream input, int decision, ParserRuleContext outerContext) {
/*     */     try {
/*  82 */       this._sllStopIndex = -1;
/*  83 */       this._llStopIndex = -1;
/*  84 */       this.currentDecision = decision;
/*  85 */       long start = System.nanoTime();
/*  86 */       int alt = super.adaptivePredict(input, decision, outerContext);
/*  87 */       long stop = System.nanoTime();
/*  88 */       (this.decisions[decision]).timeInPrediction += stop - start;
/*  89 */       (this.decisions[decision]).invocations++;
/*     */       
/*  91 */       int SLL_k = this._sllStopIndex - this._startIndex + 1;
/*  92 */       (this.decisions[decision]).SLL_TotalLook += SLL_k;
/*  93 */       (this.decisions[decision]).SLL_MinLook = ((this.decisions[decision]).SLL_MinLook == 0L) ? SLL_k : Math.min((this.decisions[decision]).SLL_MinLook, SLL_k);
/*  94 */       if (SLL_k > (this.decisions[decision]).SLL_MaxLook) {
/*  95 */         (this.decisions[decision]).SLL_MaxLook = SLL_k;
/*  96 */         (this.decisions[decision]).SLL_MaxLookEvent = new LookaheadEventInfo(decision, null, alt, input, this._startIndex, this._sllStopIndex, false);
/*     */       } 
/*     */ 
/*     */       
/* 100 */       if (this._llStopIndex >= 0) {
/* 101 */         int LL_k = this._llStopIndex - this._startIndex + 1;
/* 102 */         (this.decisions[decision]).LL_TotalLook += LL_k;
/* 103 */         (this.decisions[decision]).LL_MinLook = ((this.decisions[decision]).LL_MinLook == 0L) ? LL_k : Math.min((this.decisions[decision]).LL_MinLook, LL_k);
/* 104 */         if (LL_k > (this.decisions[decision]).LL_MaxLook) {
/* 105 */           (this.decisions[decision]).LL_MaxLook = LL_k;
/* 106 */           (this.decisions[decision]).LL_MaxLookEvent = new LookaheadEventInfo(decision, null, alt, input, this._startIndex, this._llStopIndex, true);
/*     */         } 
/*     */       } 
/*     */ 
/*     */       
/* 111 */       return alt;
/*     */     } finally {
/*     */       
/* 114 */       this.currentDecision = -1;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected DFAState getExistingTargetState(DFAState previousD, int t) {
/* 122 */     this._sllStopIndex = this._input.index();
/*     */     
/* 124 */     DFAState existingTargetState = super.getExistingTargetState(previousD, t);
/* 125 */     if (existingTargetState != null) {
/* 126 */       (this.decisions[this.currentDecision]).SLL_DFATransitions++;
/* 127 */       if (existingTargetState == ERROR) {
/* 128 */         (this.decisions[this.currentDecision]).errors.add(new ErrorInfo(this.currentDecision, previousD.configs, this._input, this._startIndex, this._sllStopIndex, false));
/*     */       }
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 134 */     this.currentState = existingTargetState;
/* 135 */     return existingTargetState;
/*     */   }
/*     */ 
/*     */   
/*     */   protected DFAState computeTargetState(DFA dfa, DFAState previousD, int t) {
/* 140 */     DFAState state = super.computeTargetState(dfa, previousD, t);
/* 141 */     this.currentState = state;
/* 142 */     return state;
/*     */   }
/*     */ 
/*     */   
/*     */   protected ATNConfigSet computeReachSet(ATNConfigSet closure, int t, boolean fullCtx) {
/* 147 */     if (fullCtx)
/*     */     {
/*     */       
/* 150 */       this._llStopIndex = this._input.index();
/*     */     }
/*     */     
/* 153 */     ATNConfigSet reachConfigs = super.computeReachSet(closure, t, fullCtx);
/* 154 */     if (fullCtx) {
/* 155 */       (this.decisions[this.currentDecision]).LL_ATNTransitions++;
/* 156 */       if (reachConfigs == null)
/*     */       {
/*     */ 
/*     */         
/* 160 */         (this.decisions[this.currentDecision]).errors.add(new ErrorInfo(this.currentDecision, closure, this._input, this._startIndex, this._llStopIndex, true));
/*     */       
/*     */       }
/*     */     }
/*     */     else {
/*     */       
/* 166 */       (this.decisions[this.currentDecision]).SLL_ATNTransitions++;
/* 167 */       if (reachConfigs == null)
/*     */       {
/*     */         
/* 170 */         (this.decisions[this.currentDecision]).errors.add(new ErrorInfo(this.currentDecision, closure, this._input, this._startIndex, this._sllStopIndex, false));
/*     */       }
/*     */     } 
/*     */ 
/*     */     
/* 175 */     return reachConfigs;
/*     */   }
/*     */ 
/*     */   
/*     */   protected boolean evalSemanticContext(SemanticContext pred, ParserRuleContext parserCallStack, int alt, boolean fullCtx) {
/* 180 */     boolean result = super.evalSemanticContext(pred, parserCallStack, alt, fullCtx);
/* 181 */     if (!(pred instanceof SemanticContext.PrecedencePredicate)) {
/* 182 */       boolean fullContext = (this._llStopIndex >= 0);
/* 183 */       int stopIndex = fullContext ? this._llStopIndex : this._sllStopIndex;
/* 184 */       (this.decisions[this.currentDecision]).predicateEvals.add(new PredicateEvalInfo(this.currentDecision, this._input, this._startIndex, stopIndex, pred, result, alt, fullCtx));
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 189 */     return result;
/*     */   }
/*     */ 
/*     */   
/*     */   protected void reportAttemptingFullContext(DFA dfa, BitSet conflictingAlts, ATNConfigSet configs, int startIndex, int stopIndex) {
/* 194 */     if (conflictingAlts != null) {
/* 195 */       this.conflictingAltResolvedBySLL = conflictingAlts.nextSetBit(0);
/*     */     } else {
/*     */       
/* 198 */       this.conflictingAltResolvedBySLL = configs.getAlts().nextSetBit(0);
/*     */     } 
/* 200 */     (this.decisions[this.currentDecision]).LL_Fallback++;
/* 201 */     super.reportAttemptingFullContext(dfa, conflictingAlts, configs, startIndex, stopIndex);
/*     */   }
/*     */ 
/*     */   
/*     */   protected void reportContextSensitivity(DFA dfa, int prediction, ATNConfigSet configs, int startIndex, int stopIndex) {
/* 206 */     if (prediction != this.conflictingAltResolvedBySLL) {
/* 207 */       (this.decisions[this.currentDecision]).contextSensitivities.add(new ContextSensitivityInfo(this.currentDecision, configs, this._input, startIndex, stopIndex));
/*     */     }
/*     */ 
/*     */     
/* 211 */     super.reportContextSensitivity(dfa, prediction, configs, startIndex, stopIndex);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void reportAmbiguity(DFA dfa, DFAState D, int startIndex, int stopIndex, boolean exact, BitSet ambigAlts, ATNConfigSet configs) {
/*     */     int prediction;
/* 219 */     if (ambigAlts != null) {
/* 220 */       prediction = ambigAlts.nextSetBit(0);
/*     */     } else {
/*     */       
/* 223 */       prediction = configs.getAlts().nextSetBit(0);
/*     */     } 
/* 225 */     if (configs.fullCtx && prediction != this.conflictingAltResolvedBySLL)
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 231 */       (this.decisions[this.currentDecision]).contextSensitivities.add(new ContextSensitivityInfo(this.currentDecision, configs, this._input, startIndex, stopIndex));
/*     */     }
/*     */ 
/*     */     
/* 235 */     (this.decisions[this.currentDecision]).ambiguities.add(new AmbiguityInfo(this.currentDecision, configs, ambigAlts, this._input, startIndex, stopIndex, configs.fullCtx));
/*     */ 
/*     */ 
/*     */     
/* 239 */     super.reportAmbiguity(dfa, D, startIndex, stopIndex, exact, ambigAlts, configs);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public DecisionInfo[] getDecisionInfo() {
/* 245 */     return this.decisions;
/*     */   }
/*     */   
/*     */   public DFAState getCurrentState() {
/* 249 */     return this.currentState;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\antlr\v4\runtime\atn\ProfilingATNSimulator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */