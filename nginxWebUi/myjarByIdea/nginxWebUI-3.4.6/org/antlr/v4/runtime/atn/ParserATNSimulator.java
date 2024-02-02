package org.antlr.v4.runtime.atn;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.antlr.v4.runtime.NoViableAltException;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.Vocabulary;
import org.antlr.v4.runtime.VocabularyImpl;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.dfa.DFAState;
import org.antlr.v4.runtime.misc.DoubleKeyMap;
import org.antlr.v4.runtime.misc.IntervalSet;
import org.antlr.v4.runtime.misc.Pair;

public class ParserATNSimulator extends ATNSimulator {
   public static final boolean debug = false;
   public static final boolean debug_list_atn_decisions = false;
   public static final boolean dfa_debug = false;
   public static final boolean retry_debug = false;
   protected final Parser parser;
   public final DFA[] decisionToDFA;
   private PredictionMode mode;
   protected DoubleKeyMap<PredictionContext, PredictionContext, PredictionContext> mergeCache;
   protected TokenStream _input;
   protected int _startIndex;
   protected ParserRuleContext _outerContext;
   protected DFA _dfa;

   public ParserATNSimulator(ATN atn, DFA[] decisionToDFA, PredictionContextCache sharedContextCache) {
      this((Parser)null, atn, decisionToDFA, sharedContextCache);
   }

   public ParserATNSimulator(Parser parser, ATN atn, DFA[] decisionToDFA, PredictionContextCache sharedContextCache) {
      super(atn, sharedContextCache);
      this.mode = PredictionMode.LL;
      this.parser = parser;
      this.decisionToDFA = decisionToDFA;
   }

   public void reset() {
   }

   public void clearDFA() {
      for(int d = 0; d < this.decisionToDFA.length; ++d) {
         this.decisionToDFA[d] = new DFA(this.atn.getDecisionState(d), d);
      }

   }

   public int adaptivePredict(TokenStream input, int decision, ParserRuleContext outerContext) {
      this._input = input;
      this._startIndex = input.index();
      this._outerContext = outerContext;
      DFA dfa = this.decisionToDFA[decision];
      this._dfa = dfa;
      int m = input.mark();
      int index = this._startIndex;

      int var14;
      try {
         DFAState s0;
         if (dfa.isPrecedenceDfa()) {
            s0 = dfa.getPrecedenceStartState(this.parser.getPrecedence());
         } else {
            s0 = dfa.s0;
         }

         if (s0 == null) {
            if (outerContext == null) {
               outerContext = ParserRuleContext.EMPTY;
            }

            boolean fullCtx = false;
            ATNConfigSet s0_closure = this.computeStartState(dfa.atnStartState, ParserRuleContext.EMPTY, fullCtx);
            if (dfa.isPrecedenceDfa()) {
               dfa.s0.configs = s0_closure;
               s0_closure = this.applyPrecedenceFilter(s0_closure);
               s0 = this.addDFAState(dfa, new DFAState(s0_closure));
               dfa.setPrecedenceStartState(this.parser.getPrecedence(), s0);
            } else {
               s0 = this.addDFAState(dfa, new DFAState(s0_closure));
               dfa.s0 = s0;
            }
         }

         int alt = this.execATN(dfa, s0, input, index, outerContext);
         var14 = alt;
      } finally {
         this.mergeCache = null;
         this._dfa = null;
         input.seek(index);
         input.release(m);
      }

      return var14;
   }

   protected int execATN(DFA dfa, DFAState s0, TokenStream input, int startIndex, ParserRuleContext outerContext) {
      DFAState previousD = s0;
      int t = input.LA(1);

      while(true) {
         DFAState D = this.getExistingTargetState(previousD, t);
         if (D == null) {
            D = this.computeTargetState(dfa, previousD, t);
         }

         int conflictIndex;
         if (D == ERROR) {
            NoViableAltException e = this.noViableAlt(input, outerContext, previousD.configs, startIndex);
            input.seek(startIndex);
            conflictIndex = this.getSynValidOrSemInvalidAltThatFinishedDecisionEntryRule(previousD.configs, outerContext);
            if (conflictIndex != 0) {
               return conflictIndex;
            }

            throw e;
         }

         if (D.requiresFullContext && this.mode != PredictionMode.SLL) {
            BitSet conflictingAlts = D.configs.conflictingAlts;
            if (D.predicates != null) {
               conflictIndex = input.index();
               if (conflictIndex != startIndex) {
                  input.seek(startIndex);
               }

               conflictingAlts = this.evalSemanticContext(D.predicates, outerContext, true);
               if (conflictingAlts.cardinality() == 1) {
                  return conflictingAlts.nextSetBit(0);
               }

               if (conflictIndex != startIndex) {
                  input.seek(conflictIndex);
               }
            }

            boolean fullCtx = true;
            ATNConfigSet s0_closure = this.computeStartState(dfa.atnStartState, outerContext, fullCtx);
            this.reportAttemptingFullContext(dfa, conflictingAlts, D.configs, startIndex, input.index());
            int alt = this.execATNWithFullContext(dfa, D, s0_closure, input, startIndex, outerContext);
            return alt;
         }

         if (D.isAcceptState) {
            if (D.predicates == null) {
               return D.prediction;
            }

            int stopIndex = input.index();
            input.seek(startIndex);
            BitSet alts = this.evalSemanticContext(D.predicates, outerContext, true);
            switch (alts.cardinality()) {
               case 0:
                  throw this.noViableAlt(input, outerContext, D.configs, startIndex);
               case 1:
                  return alts.nextSetBit(0);
               default:
                  this.reportAmbiguity(dfa, D, startIndex, stopIndex, false, alts, D.configs);
                  return alts.nextSetBit(0);
            }
         }

         previousD = D;
         if (t != -1) {
            input.consume();
            t = input.LA(1);
         }
      }
   }

   protected DFAState getExistingTargetState(DFAState previousD, int t) {
      DFAState[] edges = previousD.edges;
      return edges != null && t + 1 >= 0 && t + 1 < edges.length ? edges[t + 1] : null;
   }

   protected DFAState computeTargetState(DFA dfa, DFAState previousD, int t) {
      ATNConfigSet reach = this.computeReachSet(previousD.configs, t, false);
      if (reach == null) {
         this.addDFAEdge(dfa, previousD, t, ERROR);
         return ERROR;
      } else {
         DFAState D = new DFAState(reach);
         int predictedAlt = getUniqueAlt(reach);
         if (predictedAlt != 0) {
            D.isAcceptState = true;
            D.configs.uniqueAlt = predictedAlt;
            D.prediction = predictedAlt;
         } else if (PredictionMode.hasSLLConflictTerminatingPrediction(this.mode, reach)) {
            D.configs.conflictingAlts = this.getConflictingAlts(reach);
            D.requiresFullContext = true;
            D.isAcceptState = true;
            D.prediction = D.configs.conflictingAlts.nextSetBit(0);
         }

         if (D.isAcceptState && D.configs.hasSemanticContext) {
            this.predicateDFAState(D, this.atn.getDecisionState(dfa.decision));
            if (D.predicates != null) {
               D.prediction = 0;
            }
         }

         D = this.addDFAEdge(dfa, previousD, t, D);
         return D;
      }
   }

   protected void predicateDFAState(DFAState dfaState, DecisionState decisionState) {
      int nalts = decisionState.getNumberOfTransitions();
      BitSet altsToCollectPredsFrom = this.getConflictingAltsOrUniqueAlt(dfaState.configs);
      SemanticContext[] altToPred = this.getPredsForAmbigAlts(altsToCollectPredsFrom, dfaState.configs, nalts);
      if (altToPred != null) {
         dfaState.predicates = this.getPredicatePredictions(altsToCollectPredsFrom, altToPred);
         dfaState.prediction = 0;
      } else {
         dfaState.prediction = altsToCollectPredsFrom.nextSetBit(0);
      }

   }

   protected int execATNWithFullContext(DFA dfa, DFAState D, ATNConfigSet s0, TokenStream input, int startIndex, ParserRuleContext outerContext) {
      boolean fullCtx = true;
      boolean foundExactAmbig = false;
      ATNConfigSet reach = null;
      ATNConfigSet previous = s0;
      input.seek(startIndex);
      int t = input.LA(1);

      int predictedAlt;
      while(true) {
         reach = this.computeReachSet(previous, t, fullCtx);
         if (reach == null) {
            NoViableAltException e = this.noViableAlt(input, outerContext, previous, startIndex);
            input.seek(startIndex);
            int alt = this.getSynValidOrSemInvalidAltThatFinishedDecisionEntryRule(previous, outerContext);
            if (alt != 0) {
               return alt;
            }

            throw e;
         }

         Collection<BitSet> altSubSets = PredictionMode.getConflictingAltSubsets(reach);
         reach.uniqueAlt = getUniqueAlt(reach);
         if (reach.uniqueAlt != 0) {
            predictedAlt = reach.uniqueAlt;
            break;
         }

         if (this.mode != PredictionMode.LL_EXACT_AMBIG_DETECTION) {
            predictedAlt = PredictionMode.resolvesToJustOneViableAlt(altSubSets);
            if (predictedAlt != 0) {
               break;
            }
         } else if (PredictionMode.allSubsetsConflict(altSubSets) && PredictionMode.allSubsetsEqual(altSubSets)) {
            foundExactAmbig = true;
            predictedAlt = PredictionMode.getSingleViableAlt(altSubSets);
            break;
         }

         previous = reach;
         if (t != -1) {
            input.consume();
            t = input.LA(1);
         }
      }

      if (reach.uniqueAlt != 0) {
         this.reportContextSensitivity(dfa, predictedAlt, reach, startIndex, input.index());
         return predictedAlt;
      } else {
         this.reportAmbiguity(dfa, D, startIndex, input.index(), foundExactAmbig, reach.getAlts(), reach);
         return predictedAlt;
      }
   }

   protected ATNConfigSet computeReachSet(ATNConfigSet closure, int t, boolean fullCtx) {
      if (this.mergeCache == null) {
         this.mergeCache = new DoubleKeyMap();
      }

      ATNConfigSet intermediate = new ATNConfigSet(fullCtx);
      List<ATNConfig> skippedStopStates = null;
      Iterator i$ = closure.iterator();

      while(true) {
         ATNConfig c;
         label111:
         do {
            while(i$.hasNext()) {
               c = (ATNConfig)i$.next();
               if (c.state instanceof RuleStopState) {
                  assert c.context.isEmpty();
                  continue label111;
               }

               int n = c.state.getNumberOfTransitions();

               for(int ti = 0; ti < n; ++ti) {
                  Transition trans = c.state.transition(ti);
                  ATNState target = this.getReachableTarget(trans, t);
                  if (target != null) {
                     intermediate.add(new ATNConfig(c, target), this.mergeCache);
                  }
               }
            }

            ATNConfigSet reach = null;
            if (skippedStopStates == null && t != -1) {
               if (intermediate.size() == 1) {
                  reach = intermediate;
               } else if (getUniqueAlt(intermediate) != 0) {
                  reach = intermediate;
               }
            }

            if (reach == null) {
               reach = new ATNConfigSet(fullCtx);
               Set<ATNConfig> closureBusy = new HashSet();
               boolean treatEofAsEpsilon = t == -1;
               Iterator i$ = intermediate.iterator();

               while(i$.hasNext()) {
                  ATNConfig c = (ATNConfig)i$.next();
                  this.closure(c, reach, closureBusy, false, fullCtx, treatEofAsEpsilon);
               }
            }

            if (t == -1) {
               reach = this.removeAllConfigsNotInRuleStopState(reach, reach == intermediate);
            }

            if (skippedStopStates != null && (!fullCtx || !PredictionMode.hasConfigInRuleStopState(reach))) {
               assert !skippedStopStates.isEmpty();

               Iterator i$ = skippedStopStates.iterator();

               while(i$.hasNext()) {
                  ATNConfig c = (ATNConfig)i$.next();
                  reach.add(c, this.mergeCache);
               }
            }

            if (reach.isEmpty()) {
               return null;
            }

            return reach;
         } while(!fullCtx && t != -1);

         if (skippedStopStates == null) {
            skippedStopStates = new ArrayList();
         }

         skippedStopStates.add(c);
      }
   }

   protected ATNConfigSet removeAllConfigsNotInRuleStopState(ATNConfigSet configs, boolean lookToEndOfRule) {
      if (PredictionMode.allConfigsInRuleStopStates(configs)) {
         return configs;
      } else {
         ATNConfigSet result = new ATNConfigSet(configs.fullCtx);
         Iterator i$ = configs.iterator();

         while(i$.hasNext()) {
            ATNConfig config = (ATNConfig)i$.next();
            if (config.state instanceof RuleStopState) {
               result.add(config, this.mergeCache);
            } else if (lookToEndOfRule && config.state.onlyHasEpsilonTransitions()) {
               IntervalSet nextTokens = this.atn.nextTokens(config.state);
               if (nextTokens.contains(-2)) {
                  ATNState endOfRuleState = this.atn.ruleToStopState[config.state.ruleIndex];
                  result.add(new ATNConfig(config, endOfRuleState), this.mergeCache);
               }
            }
         }

         return result;
      }
   }

   protected ATNConfigSet computeStartState(ATNState p, RuleContext ctx, boolean fullCtx) {
      PredictionContext initialContext = PredictionContext.fromRuleContext(this.atn, ctx);
      ATNConfigSet configs = new ATNConfigSet(fullCtx);

      for(int i = 0; i < p.getNumberOfTransitions(); ++i) {
         ATNState target = p.transition(i).target;
         ATNConfig c = new ATNConfig(target, i + 1, initialContext);
         Set<ATNConfig> closureBusy = new HashSet();
         this.closure(c, configs, closureBusy, true, fullCtx, false);
      }

      return configs;
   }

   protected ATNConfigSet applyPrecedenceFilter(ATNConfigSet configs) {
      Map<Integer, PredictionContext> statesFromAlt1 = new HashMap();
      ATNConfigSet configSet = new ATNConfigSet(configs.fullCtx);
      Iterator i$ = configs.iterator();

      ATNConfig config;
      while(i$.hasNext()) {
         config = (ATNConfig)i$.next();
         if (config.alt == 1) {
            SemanticContext updatedContext = config.semanticContext.evalPrecedence(this.parser, this._outerContext);
            if (updatedContext != null) {
               statesFromAlt1.put(config.state.stateNumber, config.context);
               if (updatedContext != config.semanticContext) {
                  configSet.add(new ATNConfig(config, updatedContext), this.mergeCache);
               } else {
                  configSet.add(config, this.mergeCache);
               }
            }
         }
      }

      i$ = configs.iterator();

      while(true) {
         PredictionContext context;
         do {
            do {
               if (!i$.hasNext()) {
                  return configSet;
               }

               config = (ATNConfig)i$.next();
            } while(config.alt == 1);

            if (config.isPrecedenceFilterSuppressed()) {
               break;
            }

            context = (PredictionContext)statesFromAlt1.get(config.state.stateNumber);
         } while(context != null && context.equals(config.context));

         configSet.add(config, this.mergeCache);
      }
   }

   protected ATNState getReachableTarget(Transition trans, int ttype) {
      return trans.matches(ttype, 0, this.atn.maxTokenType) ? trans.target : null;
   }

   protected SemanticContext[] getPredsForAmbigAlts(BitSet ambigAlts, ATNConfigSet configs, int nalts) {
      SemanticContext[] altToPred = new SemanticContext[nalts + 1];
      Iterator i$ = configs.iterator();

      while(i$.hasNext()) {
         ATNConfig c = (ATNConfig)i$.next();
         if (ambigAlts.get(c.alt)) {
            altToPred[c.alt] = SemanticContext.or(altToPred[c.alt], c.semanticContext);
         }
      }

      int nPredAlts = 0;

      for(int i = 1; i <= nalts; ++i) {
         if (altToPred[i] == null) {
            altToPred[i] = SemanticContext.NONE;
         } else if (altToPred[i] != SemanticContext.NONE) {
            ++nPredAlts;
         }
      }

      if (nPredAlts == 0) {
         altToPred = null;
      }

      return altToPred;
   }

   protected DFAState.PredPrediction[] getPredicatePredictions(BitSet ambigAlts, SemanticContext[] altToPred) {
      List<DFAState.PredPrediction> pairs = new ArrayList();
      boolean containsPredicate = false;

      for(int i = 1; i < altToPred.length; ++i) {
         SemanticContext pred = altToPred[i];

         assert pred != null;

         if (ambigAlts != null && ambigAlts.get(i)) {
            pairs.add(new DFAState.PredPrediction(pred, i));
         }

         if (pred != SemanticContext.NONE) {
            containsPredicate = true;
         }
      }

      if (!containsPredicate) {
         return null;
      } else {
         return (DFAState.PredPrediction[])pairs.toArray(new DFAState.PredPrediction[pairs.size()]);
      }
   }

   protected int getSynValidOrSemInvalidAltThatFinishedDecisionEntryRule(ATNConfigSet configs, ParserRuleContext outerContext) {
      Pair<ATNConfigSet, ATNConfigSet> sets = this.splitAccordingToSemanticValidity(configs, outerContext);
      ATNConfigSet semValidConfigs = (ATNConfigSet)sets.a;
      ATNConfigSet semInvalidConfigs = (ATNConfigSet)sets.b;
      int alt = this.getAltThatFinishedDecisionEntryRule(semValidConfigs);
      if (alt != 0) {
         return alt;
      } else {
         if (semInvalidConfigs.size() > 0) {
            alt = this.getAltThatFinishedDecisionEntryRule(semInvalidConfigs);
            if (alt != 0) {
               return alt;
            }
         }

         return 0;
      }
   }

   protected int getAltThatFinishedDecisionEntryRule(ATNConfigSet configs) {
      IntervalSet alts = new IntervalSet(new int[0]);
      Iterator i$ = configs.iterator();

      while(true) {
         ATNConfig c;
         do {
            if (!i$.hasNext()) {
               if (alts.size() == 0) {
                  return 0;
               }

               return alts.getMinElement();
            }

            c = (ATNConfig)i$.next();
         } while(c.getOuterContextDepth() <= 0 && (!(c.state instanceof RuleStopState) || !c.context.hasEmptyPath()));

         alts.add(c.alt);
      }
   }

   protected Pair<ATNConfigSet, ATNConfigSet> splitAccordingToSemanticValidity(ATNConfigSet configs, ParserRuleContext outerContext) {
      ATNConfigSet succeeded = new ATNConfigSet(configs.fullCtx);
      ATNConfigSet failed = new ATNConfigSet(configs.fullCtx);
      Iterator i$ = configs.iterator();

      while(i$.hasNext()) {
         ATNConfig c = (ATNConfig)i$.next();
         if (c.semanticContext != SemanticContext.NONE) {
            boolean predicateEvaluationResult = this.evalSemanticContext(c.semanticContext, outerContext, c.alt, configs.fullCtx);
            if (predicateEvaluationResult) {
               succeeded.add(c);
            } else {
               failed.add(c);
            }
         } else {
            succeeded.add(c);
         }
      }

      return new Pair(succeeded, failed);
   }

   protected BitSet evalSemanticContext(DFAState.PredPrediction[] predPredictions, ParserRuleContext outerContext, boolean complete) {
      BitSet predictions = new BitSet();
      DFAState.PredPrediction[] arr$ = predPredictions;
      int len$ = predPredictions.length;

      for(int i$ = 0; i$ < len$; ++i$) {
         DFAState.PredPrediction pair = arr$[i$];
         if (pair.pred == SemanticContext.NONE) {
            predictions.set(pair.alt);
            if (!complete) {
               break;
            }
         } else {
            boolean fullCtx = false;
            boolean predicateEvaluationResult = this.evalSemanticContext(pair.pred, outerContext, pair.alt, fullCtx);
            if (predicateEvaluationResult) {
               predictions.set(pair.alt);
               if (!complete) {
                  break;
               }
            }
         }
      }

      return predictions;
   }

   protected boolean evalSemanticContext(SemanticContext pred, ParserRuleContext parserCallStack, int alt, boolean fullCtx) {
      return pred.eval(this.parser, parserCallStack);
   }

   protected void closure(ATNConfig config, ATNConfigSet configs, Set<ATNConfig> closureBusy, boolean collectPredicates, boolean fullCtx, boolean treatEofAsEpsilon) {
      int initialDepth = false;
      this.closureCheckingStopState(config, configs, closureBusy, collectPredicates, fullCtx, 0, treatEofAsEpsilon);

      assert !fullCtx || !configs.dipsIntoOuterContext;

   }

   protected void closureCheckingStopState(ATNConfig config, ATNConfigSet configs, Set<ATNConfig> closureBusy, boolean collectPredicates, boolean fullCtx, int depth, boolean treatEofAsEpsilon) {
      if (config.state instanceof RuleStopState) {
         if (!config.context.isEmpty()) {
            for(int i = 0; i < config.context.size(); ++i) {
               if (config.context.getReturnState(i) == Integer.MAX_VALUE) {
                  if (fullCtx) {
                     configs.add(new ATNConfig(config, config.state, PredictionContext.EMPTY), this.mergeCache);
                  } else {
                     this.closure_(config, configs, closureBusy, collectPredicates, fullCtx, depth, treatEofAsEpsilon);
                  }
               } else {
                  ATNState returnState = (ATNState)this.atn.states.get(config.context.getReturnState(i));
                  PredictionContext newContext = config.context.getParent(i);
                  ATNConfig c = new ATNConfig(returnState, config.alt, newContext, config.semanticContext);
                  c.reachesIntoOuterContext = config.reachesIntoOuterContext;

                  assert depth > Integer.MIN_VALUE;

                  this.closureCheckingStopState(c, configs, closureBusy, collectPredicates, fullCtx, depth - 1, treatEofAsEpsilon);
               }
            }

            return;
         }

         if (fullCtx) {
            configs.add(config, this.mergeCache);
            return;
         }
      }

      this.closure_(config, configs, closureBusy, collectPredicates, fullCtx, depth, treatEofAsEpsilon);
   }

   protected void closure_(ATNConfig config, ATNConfigSet configs, Set<ATNConfig> closureBusy, boolean collectPredicates, boolean fullCtx, int depth, boolean treatEofAsEpsilon) {
      ATNState p = config.state;
      if (!p.onlyHasEpsilonTransitions()) {
         configs.add(config, this.mergeCache);
      }

      for(int i = 0; i < p.getNumberOfTransitions(); ++i) {
         Transition t = p.transition(i);
         boolean continueCollecting = !(t instanceof ActionTransition) && collectPredicates;
         ATNConfig c = this.getEpsilonTarget(config, t, continueCollecting, depth == 0, fullCtx, treatEofAsEpsilon);
         if (c != null && (t.isEpsilon() || closureBusy.add(c))) {
            int newDepth = depth;
            if (config.state instanceof RuleStopState) {
               assert !fullCtx;

               if (!closureBusy.add(c)) {
                  continue;
               }

               if (this._dfa != null && this._dfa.isPrecedenceDfa()) {
                  int outermostPrecedenceReturn = ((EpsilonTransition)t).outermostPrecedenceReturn();
                  if (outermostPrecedenceReturn == this._dfa.atnStartState.ruleIndex) {
                     c.setPrecedenceFilterSuppressed(true);
                  }
               }

               ++c.reachesIntoOuterContext;
               configs.dipsIntoOuterContext = true;

               assert depth > Integer.MIN_VALUE;

               newDepth = depth - 1;
            } else if (t instanceof RuleTransition && depth >= 0) {
               newDepth = depth + 1;
            }

            this.closureCheckingStopState(c, configs, closureBusy, continueCollecting, fullCtx, newDepth, treatEofAsEpsilon);
         }
      }

   }

   public String getRuleName(int index) {
      return this.parser != null && index >= 0 ? this.parser.getRuleNames()[index] : "<rule " + index + ">";
   }

   protected ATNConfig getEpsilonTarget(ATNConfig config, Transition t, boolean collectPredicates, boolean inContext, boolean fullCtx, boolean treatEofAsEpsilon) {
      switch (t.getSerializationType()) {
         case 1:
            return new ATNConfig(config, t.target);
         case 2:
         case 5:
         case 7:
            if (treatEofAsEpsilon && t.matches(-1, 0, 1)) {
               return new ATNConfig(config, t.target);
            }

            return null;
         case 3:
            return this.ruleTransition(config, (RuleTransition)t);
         case 4:
            return this.predTransition(config, (PredicateTransition)t, collectPredicates, inContext, fullCtx);
         case 6:
            return this.actionTransition(config, (ActionTransition)t);
         case 8:
         case 9:
         default:
            return null;
         case 10:
            return this.precedenceTransition(config, (PrecedencePredicateTransition)t, collectPredicates, inContext, fullCtx);
      }
   }

   protected ATNConfig actionTransition(ATNConfig config, ActionTransition t) {
      return new ATNConfig(config, t.target);
   }

   public ATNConfig precedenceTransition(ATNConfig config, PrecedencePredicateTransition pt, boolean collectPredicates, boolean inContext, boolean fullCtx) {
      ATNConfig c = null;
      if (collectPredicates && inContext) {
         if (fullCtx) {
            int currentPosition = this._input.index();
            this._input.seek(this._startIndex);
            boolean predSucceeds = this.evalSemanticContext(pt.getPredicate(), this._outerContext, config.alt, fullCtx);
            this._input.seek(currentPosition);
            if (predSucceeds) {
               c = new ATNConfig(config, pt.target);
            }
         } else {
            SemanticContext newSemCtx = SemanticContext.and(config.semanticContext, pt.getPredicate());
            c = new ATNConfig(config, pt.target, newSemCtx);
         }
      } else {
         c = new ATNConfig(config, pt.target);
      }

      return c;
   }

   protected ATNConfig predTransition(ATNConfig config, PredicateTransition pt, boolean collectPredicates, boolean inContext, boolean fullCtx) {
      ATNConfig c = null;
      if (!collectPredicates || pt.isCtxDependent && (!pt.isCtxDependent || !inContext)) {
         c = new ATNConfig(config, pt.target);
      } else if (fullCtx) {
         int currentPosition = this._input.index();
         this._input.seek(this._startIndex);
         boolean predSucceeds = this.evalSemanticContext(pt.getPredicate(), this._outerContext, config.alt, fullCtx);
         this._input.seek(currentPosition);
         if (predSucceeds) {
            c = new ATNConfig(config, pt.target);
         }
      } else {
         SemanticContext newSemCtx = SemanticContext.and(config.semanticContext, pt.getPredicate());
         c = new ATNConfig(config, pt.target, newSemCtx);
      }

      return c;
   }

   protected ATNConfig ruleTransition(ATNConfig config, RuleTransition t) {
      ATNState returnState = t.followState;
      PredictionContext newContext = SingletonPredictionContext.create(config.context, returnState.stateNumber);
      return new ATNConfig(config, t.target, newContext);
   }

   protected BitSet getConflictingAlts(ATNConfigSet configs) {
      Collection<BitSet> altsets = PredictionMode.getConflictingAltSubsets(configs);
      return PredictionMode.getAlts(altsets);
   }

   protected BitSet getConflictingAltsOrUniqueAlt(ATNConfigSet configs) {
      BitSet conflictingAlts;
      if (configs.uniqueAlt != 0) {
         conflictingAlts = new BitSet();
         conflictingAlts.set(configs.uniqueAlt);
      } else {
         conflictingAlts = configs.conflictingAlts;
      }

      return conflictingAlts;
   }

   public String getTokenName(int t) {
      if (t == -1) {
         return "EOF";
      } else {
         Vocabulary vocabulary = this.parser != null ? this.parser.getVocabulary() : VocabularyImpl.EMPTY_VOCABULARY;
         String displayName = ((Vocabulary)vocabulary).getDisplayName(t);
         return displayName.equals(Integer.toString(t)) ? displayName : displayName + "<" + t + ">";
      }
   }

   public String getLookaheadName(TokenStream input) {
      return this.getTokenName(input.LA(1));
   }

   public void dumpDeadEndConfigs(NoViableAltException nvae) {
      System.err.println("dead end configs: ");

      ATNConfig c;
      String trans;
      for(Iterator i$ = nvae.getDeadEndConfigs().iterator(); i$.hasNext(); System.err.println(c.toString(this.parser, true) + ":" + trans)) {
         c = (ATNConfig)i$.next();
         trans = "no edges";
         if (c.state.getNumberOfTransitions() > 0) {
            Transition t = c.state.transition(0);
            if (t instanceof AtomTransition) {
               AtomTransition at = (AtomTransition)t;
               trans = "Atom " + this.getTokenName(at.label);
            } else if (t instanceof SetTransition) {
               SetTransition st = (SetTransition)t;
               boolean not = st instanceof NotSetTransition;
               trans = (not ? "~" : "") + "Set " + st.set.toString();
            }
         }
      }

   }

   protected NoViableAltException noViableAlt(TokenStream input, ParserRuleContext outerContext, ATNConfigSet configs, int startIndex) {
      return new NoViableAltException(this.parser, input, input.get(startIndex), input.LT(1), configs, outerContext);
   }

   protected static int getUniqueAlt(ATNConfigSet configs) {
      int alt = 0;
      Iterator i$ = configs.iterator();

      while(i$.hasNext()) {
         ATNConfig c = (ATNConfig)i$.next();
         if (alt == 0) {
            alt = c.alt;
         } else if (c.alt != alt) {
            return 0;
         }
      }

      return alt;
   }

   protected DFAState addDFAEdge(DFA dfa, DFAState from, int t, DFAState to) {
      if (to == null) {
         return null;
      } else {
         to = this.addDFAState(dfa, to);
         if (from != null && t >= -1 && t <= this.atn.maxTokenType) {
            synchronized(from) {
               if (from.edges == null) {
                  from.edges = new DFAState[this.atn.maxTokenType + 1 + 1];
               }

               from.edges[t + 1] = to;
               return to;
            }
         } else {
            return to;
         }
      }
   }

   protected DFAState addDFAState(DFA dfa, DFAState D) {
      if (D == ERROR) {
         return D;
      } else {
         synchronized(dfa.states) {
            DFAState existing = (DFAState)dfa.states.get(D);
            if (existing != null) {
               return existing;
            } else {
               D.stateNumber = dfa.states.size();
               if (!D.configs.isReadonly()) {
                  D.configs.optimizeConfigs(this);
                  D.configs.setReadonly(true);
               }

               dfa.states.put(D, D);
               return D;
            }
         }
      }
   }

   protected void reportAttemptingFullContext(DFA dfa, BitSet conflictingAlts, ATNConfigSet configs, int startIndex, int stopIndex) {
      if (this.parser != null) {
         this.parser.getErrorListenerDispatch().reportAttemptingFullContext(this.parser, dfa, startIndex, stopIndex, conflictingAlts, configs);
      }

   }

   protected void reportContextSensitivity(DFA dfa, int prediction, ATNConfigSet configs, int startIndex, int stopIndex) {
      if (this.parser != null) {
         this.parser.getErrorListenerDispatch().reportContextSensitivity(this.parser, dfa, startIndex, stopIndex, prediction, configs);
      }

   }

   protected void reportAmbiguity(DFA dfa, DFAState D, int startIndex, int stopIndex, boolean exact, BitSet ambigAlts, ATNConfigSet configs) {
      if (this.parser != null) {
         this.parser.getErrorListenerDispatch().reportAmbiguity(this.parser, dfa, startIndex, stopIndex, exact, ambigAlts, configs);
      }

   }

   public final void setPredictionMode(PredictionMode mode) {
      this.mode = mode;
   }

   public final PredictionMode getPredictionMode() {
      return this.mode;
   }

   public Parser getParser() {
      return this.parser;
   }
}
