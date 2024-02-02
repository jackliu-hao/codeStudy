/*      */ package org.antlr.v4.runtime.atn;
/*      */ 
/*      */ import java.util.ArrayList;
/*      */ import java.util.BitSet;
/*      */ import java.util.Collection;
/*      */ import java.util.HashMap;
/*      */ import java.util.HashSet;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Set;
/*      */ import org.antlr.v4.runtime.NoViableAltException;
/*      */ import org.antlr.v4.runtime.Parser;
/*      */ import org.antlr.v4.runtime.ParserRuleContext;
/*      */ import org.antlr.v4.runtime.RuleContext;
/*      */ import org.antlr.v4.runtime.TokenStream;
/*      */ import org.antlr.v4.runtime.Vocabulary;
/*      */ import org.antlr.v4.runtime.VocabularyImpl;
/*      */ import org.antlr.v4.runtime.dfa.DFA;
/*      */ import org.antlr.v4.runtime.dfa.DFAState;
/*      */ import org.antlr.v4.runtime.misc.DoubleKeyMap;
/*      */ import org.antlr.v4.runtime.misc.IntervalSet;
/*      */ import org.antlr.v4.runtime.misc.Pair;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class ParserATNSimulator
/*      */   extends ATNSimulator
/*      */ {
/*      */   public static final boolean debug = false;
/*      */   public static final boolean debug_list_atn_decisions = false;
/*      */   public static final boolean dfa_debug = false;
/*      */   public static final boolean retry_debug = false;
/*      */   protected final Parser parser;
/*      */   public final DFA[] decisionToDFA;
/*  300 */   private PredictionMode mode = PredictionMode.LL;
/*      */ 
/*      */ 
/*      */   
/*      */   protected DoubleKeyMap<PredictionContext, PredictionContext, PredictionContext> mergeCache;
/*      */ 
/*      */ 
/*      */   
/*      */   protected TokenStream _input;
/*      */ 
/*      */   
/*      */   protected int _startIndex;
/*      */ 
/*      */   
/*      */   protected ParserRuleContext _outerContext;
/*      */ 
/*      */   
/*      */   protected DFA _dfa;
/*      */ 
/*      */ 
/*      */   
/*      */   public ParserATNSimulator(ATN atn, DFA[] decisionToDFA, PredictionContextCache sharedContextCache) {
/*  322 */     this((Parser)null, atn, decisionToDFA, sharedContextCache);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ParserATNSimulator(Parser parser, ATN atn, DFA[] decisionToDFA, PredictionContextCache sharedContextCache) {
/*  329 */     super(atn, sharedContextCache);
/*  330 */     this.parser = parser;
/*  331 */     this.decisionToDFA = decisionToDFA;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void reset() {}
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void clearDFA() {
/*  343 */     for (int d = 0; d < this.decisionToDFA.length; d++) {
/*  344 */       this.decisionToDFA[d] = new DFA(this.atn.getDecisionState(d), d);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int adaptivePredict(TokenStream input, int decision, ParserRuleContext outerContext) {
/*  357 */     this._input = input;
/*  358 */     this._startIndex = input.index();
/*  359 */     this._outerContext = outerContext;
/*  360 */     DFA dfa = this.decisionToDFA[decision];
/*  361 */     this._dfa = dfa;
/*      */     
/*  363 */     int m = input.mark();
/*  364 */     int index = this._startIndex;
/*      */ 
/*      */     
/*      */     try {
/*      */       DFAState s0;
/*      */       
/*  370 */       if (dfa.isPrecedenceDfa()) {
/*      */ 
/*      */         
/*  373 */         s0 = dfa.getPrecedenceStartState(this.parser.getPrecedence());
/*      */       }
/*      */       else {
/*      */         
/*  377 */         s0 = dfa.s0;
/*      */       } 
/*      */       
/*  380 */       if (s0 == null) {
/*  381 */         if (outerContext == null) outerContext = ParserRuleContext.EMPTY;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  388 */         boolean fullCtx = false;
/*  389 */         ATNConfigSet s0_closure = computeStartState(dfa.atnStartState, ParserRuleContext.EMPTY, fullCtx);
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  394 */         if (dfa.isPrecedenceDfa()) {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           
/*  401 */           dfa.s0.configs = s0_closure;
/*  402 */           s0_closure = applyPrecedenceFilter(s0_closure);
/*  403 */           s0 = addDFAState(dfa, new DFAState(s0_closure));
/*  404 */           dfa.setPrecedenceStartState(this.parser.getPrecedence(), s0);
/*      */         } else {
/*      */           
/*  407 */           s0 = addDFAState(dfa, new DFAState(s0_closure));
/*  408 */           dfa.s0 = s0;
/*      */         } 
/*      */       } 
/*      */       
/*  412 */       int alt = execATN(dfa, s0, input, index, outerContext);
/*      */       
/*  414 */       return alt;
/*      */     } finally {
/*      */       
/*  417 */       this.mergeCache = null;
/*  418 */       this._dfa = null;
/*  419 */       input.seek(index);
/*  420 */       input.release(m);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected int execATN(DFA dfa, DFAState s0, TokenStream input, int startIndex, ParserRuleContext outerContext) {
/*  464 */     DFAState previousD = s0;
/*      */ 
/*      */ 
/*      */     
/*  468 */     int t = input.LA(1);
/*      */     
/*      */     while (true) {
/*  471 */       DFAState D = getExistingTargetState(previousD, t);
/*  472 */       if (D == null) {
/*  473 */         D = computeTargetState(dfa, previousD, t);
/*      */       }
/*      */       
/*  476 */       if (D == ERROR) {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  486 */         NoViableAltException e = noViableAlt(input, outerContext, previousD.configs, startIndex);
/*  487 */         input.seek(startIndex);
/*  488 */         int alt = getSynValidOrSemInvalidAltThatFinishedDecisionEntryRule(previousD.configs, outerContext);
/*  489 */         if (alt != 0) {
/*  490 */           return alt;
/*      */         }
/*  492 */         throw e;
/*      */       } 
/*      */       
/*  495 */       if (D.requiresFullContext && this.mode != PredictionMode.SLL) {
/*      */         
/*  497 */         BitSet conflictingAlts = D.configs.conflictingAlts;
/*  498 */         if (D.predicates != null) {
/*      */           
/*  500 */           int conflictIndex = input.index();
/*  501 */           if (conflictIndex != startIndex) {
/*  502 */             input.seek(startIndex);
/*      */           }
/*      */           
/*  505 */           conflictingAlts = evalSemanticContext(D.predicates, outerContext, true);
/*  506 */           if (conflictingAlts.cardinality() == 1)
/*      */           {
/*  508 */             return conflictingAlts.nextSetBit(0);
/*      */           }
/*      */           
/*  511 */           if (conflictIndex != startIndex)
/*      */           {
/*      */             
/*  514 */             input.seek(conflictIndex);
/*      */           }
/*      */         } 
/*      */ 
/*      */         
/*  519 */         boolean fullCtx = true;
/*  520 */         ATNConfigSet s0_closure = computeStartState(dfa.atnStartState, outerContext, fullCtx);
/*      */ 
/*      */         
/*  523 */         reportAttemptingFullContext(dfa, conflictingAlts, D.configs, startIndex, input.index());
/*  524 */         int alt = execATNWithFullContext(dfa, D, s0_closure, input, startIndex, outerContext);
/*      */ 
/*      */         
/*  527 */         return alt;
/*      */       } 
/*      */       
/*  530 */       if (D.isAcceptState) {
/*  531 */         if (D.predicates == null) {
/*  532 */           return D.prediction;
/*      */         }
/*      */         
/*  535 */         int stopIndex = input.index();
/*  536 */         input.seek(startIndex);
/*  537 */         BitSet alts = evalSemanticContext(D.predicates, outerContext, true);
/*  538 */         switch (alts.cardinality()) {
/*      */           case 0:
/*  540 */             throw noViableAlt(input, outerContext, D.configs, startIndex);
/*      */           
/*      */           case 1:
/*  543 */             return alts.nextSetBit(0);
/*      */         } 
/*      */ 
/*      */ 
/*      */         
/*  548 */         reportAmbiguity(dfa, D, startIndex, stopIndex, false, alts, D.configs);
/*  549 */         return alts.nextSetBit(0);
/*      */       } 
/*      */ 
/*      */       
/*  553 */       previousD = D;
/*      */       
/*  555 */       if (t != -1) {
/*  556 */         input.consume();
/*  557 */         t = input.LA(1);
/*      */       } 
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected DFAState getExistingTargetState(DFAState previousD, int t) {
/*  574 */     DFAState[] edges = previousD.edges;
/*  575 */     if (edges == null || t + 1 < 0 || t + 1 >= edges.length) {
/*  576 */       return null;
/*      */     }
/*      */     
/*  579 */     return edges[t + 1];
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected DFAState computeTargetState(DFA dfa, DFAState previousD, int t) {
/*  595 */     ATNConfigSet reach = computeReachSet(previousD.configs, t, false);
/*  596 */     if (reach == null) {
/*  597 */       addDFAEdge(dfa, previousD, t, ERROR);
/*  598 */       return ERROR;
/*      */     } 
/*      */ 
/*      */     
/*  602 */     DFAState D = new DFAState(reach);
/*      */     
/*  604 */     int predictedAlt = getUniqueAlt(reach);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  615 */     if (predictedAlt != 0) {
/*      */       
/*  617 */       D.isAcceptState = true;
/*  618 */       D.configs.uniqueAlt = predictedAlt;
/*  619 */       D.prediction = predictedAlt;
/*      */     }
/*  621 */     else if (PredictionMode.hasSLLConflictTerminatingPrediction(this.mode, reach)) {
/*      */       
/*  623 */       D.configs.conflictingAlts = getConflictingAlts(reach);
/*  624 */       D.requiresFullContext = true;
/*      */       
/*  626 */       D.isAcceptState = true;
/*  627 */       D.prediction = D.configs.conflictingAlts.nextSetBit(0);
/*      */     } 
/*      */     
/*  630 */     if (D.isAcceptState && D.configs.hasSemanticContext) {
/*  631 */       predicateDFAState(D, this.atn.getDecisionState(dfa.decision));
/*  632 */       if (D.predicates != null) {
/*  633 */         D.prediction = 0;
/*      */       }
/*      */     } 
/*      */ 
/*      */     
/*  638 */     D = addDFAEdge(dfa, previousD, t, D);
/*  639 */     return D;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected void predicateDFAState(DFAState dfaState, DecisionState decisionState) {
/*  645 */     int nalts = decisionState.getNumberOfTransitions();
/*      */ 
/*      */     
/*  648 */     BitSet altsToCollectPredsFrom = getConflictingAltsOrUniqueAlt(dfaState.configs);
/*  649 */     SemanticContext[] altToPred = getPredsForAmbigAlts(altsToCollectPredsFrom, dfaState.configs, nalts);
/*  650 */     if (altToPred != null) {
/*  651 */       dfaState.predicates = getPredicatePredictions(altsToCollectPredsFrom, altToPred);
/*  652 */       dfaState.prediction = 0;
/*      */     
/*      */     }
/*      */     else {
/*      */ 
/*      */       
/*  658 */       dfaState.prediction = altsToCollectPredsFrom.nextSetBit(0);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected int execATNWithFullContext(DFA dfa, DFAState D, ATNConfigSet s0, TokenStream input, int startIndex, ParserRuleContext outerContext) {
/*      */     int predictedAlt;
/*  672 */     boolean fullCtx = true;
/*  673 */     boolean foundExactAmbig = false;
/*  674 */     ATNConfigSet reach = null;
/*  675 */     ATNConfigSet previous = s0;
/*  676 */     input.seek(startIndex);
/*  677 */     int t = input.LA(1);
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     while (true) {
/*  683 */       reach = computeReachSet(previous, t, fullCtx);
/*  684 */       if (reach == null) {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  694 */         NoViableAltException e = noViableAlt(input, outerContext, previous, startIndex);
/*  695 */         input.seek(startIndex);
/*  696 */         int alt = getSynValidOrSemInvalidAltThatFinishedDecisionEntryRule(previous, outerContext);
/*  697 */         if (alt != 0) {
/*  698 */           return alt;
/*      */         }
/*  700 */         throw e;
/*      */       } 
/*      */       
/*  703 */       Collection<BitSet> altSubSets = PredictionMode.getConflictingAltSubsets(reach);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  713 */       reach.uniqueAlt = getUniqueAlt(reach);
/*      */       
/*  715 */       if (reach.uniqueAlt != 0) {
/*  716 */         predictedAlt = reach.uniqueAlt;
/*      */         break;
/*      */       } 
/*  719 */       if (this.mode != PredictionMode.LL_EXACT_AMBIG_DETECTION) {
/*  720 */         predictedAlt = PredictionMode.resolvesToJustOneViableAlt(altSubSets);
/*  721 */         if (predictedAlt != 0)
/*      */         {
/*      */           break;
/*      */         
/*      */         }
/*      */       
/*      */       }
/*  728 */       else if (PredictionMode.allSubsetsConflict(altSubSets) && PredictionMode.allSubsetsEqual(altSubSets)) {
/*      */ 
/*      */         
/*  731 */         foundExactAmbig = true;
/*  732 */         predictedAlt = PredictionMode.getSingleViableAlt(altSubSets);
/*      */ 
/*      */ 
/*      */         
/*      */         break;
/*      */       } 
/*      */ 
/*      */       
/*  740 */       previous = reach;
/*  741 */       if (t != -1) {
/*  742 */         input.consume();
/*  743 */         t = input.LA(1);
/*      */       } 
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  750 */     if (reach.uniqueAlt != 0) {
/*  751 */       reportContextSensitivity(dfa, predictedAlt, reach, startIndex, input.index());
/*  752 */       return predictedAlt;
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  782 */     reportAmbiguity(dfa, D, startIndex, input.index(), foundExactAmbig, reach.getAlts(), reach);
/*      */ 
/*      */     
/*  785 */     return predictedAlt;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected ATNConfigSet computeReachSet(ATNConfigSet closure, int t, boolean fullCtx) {
/*  793 */     if (this.mergeCache == null) {
/*  794 */       this.mergeCache = new DoubleKeyMap<PredictionContext, PredictionContext, PredictionContext>();
/*      */     }
/*      */     
/*  797 */     ATNConfigSet intermediate = new ATNConfigSet(fullCtx);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  809 */     List<ATNConfig> skippedStopStates = null;
/*      */ 
/*      */     
/*  812 */     for (ATNConfig c : closure) {
/*      */ 
/*      */       
/*  815 */       if (c.state instanceof RuleStopState) {
/*  816 */         assert c.context.isEmpty();
/*  817 */         if (fullCtx || t == -1) {
/*  818 */           if (skippedStopStates == null) {
/*  819 */             skippedStopStates = new ArrayList<ATNConfig>();
/*      */           }
/*      */           
/*  822 */           skippedStopStates.add(c);
/*      */         } 
/*      */         
/*      */         continue;
/*      */       } 
/*      */       
/*  828 */       int n = c.state.getNumberOfTransitions();
/*  829 */       for (int ti = 0; ti < n; ti++) {
/*  830 */         Transition trans = c.state.transition(ti);
/*  831 */         ATNState target = getReachableTarget(trans, t);
/*  832 */         if (target != null) {
/*  833 */           intermediate.add(new ATNConfig(c, target), this.mergeCache);
/*      */         }
/*      */       } 
/*      */     } 
/*      */ 
/*      */ 
/*      */     
/*  840 */     ATNConfigSet reach = null;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  851 */     if (skippedStopStates == null && t != -1) {
/*  852 */       if (intermediate.size() == 1) {
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  857 */         reach = intermediate;
/*      */       }
/*  859 */       else if (getUniqueAlt(intermediate) != 0) {
/*      */ 
/*      */         
/*  862 */         reach = intermediate;
/*      */       } 
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  869 */     if (reach == null) {
/*  870 */       reach = new ATNConfigSet(fullCtx);
/*  871 */       Set<ATNConfig> closureBusy = new HashSet<ATNConfig>();
/*  872 */       boolean treatEofAsEpsilon = (t == -1);
/*  873 */       for (ATNConfig c : intermediate) {
/*  874 */         closure(c, reach, closureBusy, false, fullCtx, treatEofAsEpsilon);
/*      */       }
/*      */     } 
/*      */     
/*  878 */     if (t == -1)
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  896 */       reach = removeAllConfigsNotInRuleStopState(reach, (reach == intermediate));
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  907 */     if (skippedStopStates != null && (!fullCtx || !PredictionMode.hasConfigInRuleStopState(reach))) {
/*  908 */       assert !skippedStopStates.isEmpty();
/*  909 */       for (ATNConfig c : skippedStopStates) {
/*  910 */         reach.add(c, this.mergeCache);
/*      */       }
/*      */     } 
/*      */     
/*  914 */     if (reach.isEmpty()) return null; 
/*  915 */     return reach;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected ATNConfigSet removeAllConfigsNotInRuleStopState(ATNConfigSet configs, boolean lookToEndOfRule) {
/*  939 */     if (PredictionMode.allConfigsInRuleStopStates(configs)) {
/*  940 */       return configs;
/*      */     }
/*      */     
/*  943 */     ATNConfigSet result = new ATNConfigSet(configs.fullCtx);
/*  944 */     for (ATNConfig config : configs) {
/*  945 */       if (config.state instanceof RuleStopState) {
/*  946 */         result.add(config, this.mergeCache);
/*      */         
/*      */         continue;
/*      */       } 
/*  950 */       if (lookToEndOfRule && config.state.onlyHasEpsilonTransitions()) {
/*  951 */         IntervalSet nextTokens = this.atn.nextTokens(config.state);
/*  952 */         if (nextTokens.contains(-2)) {
/*  953 */           ATNState endOfRuleState = this.atn.ruleToStopState[config.state.ruleIndex];
/*  954 */           result.add(new ATNConfig(config, endOfRuleState), this.mergeCache);
/*      */         } 
/*      */       } 
/*      */     } 
/*      */     
/*  959 */     return result;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected ATNConfigSet computeStartState(ATNState p, RuleContext ctx, boolean fullCtx) {
/*  968 */     PredictionContext initialContext = PredictionContext.fromRuleContext(this.atn, ctx);
/*  969 */     ATNConfigSet configs = new ATNConfigSet(fullCtx);
/*      */     
/*  971 */     for (int i = 0; i < p.getNumberOfTransitions(); i++) {
/*  972 */       ATNState target = (p.transition(i)).target;
/*  973 */       ATNConfig c = new ATNConfig(target, i + 1, initialContext);
/*  974 */       Set<ATNConfig> closureBusy = new HashSet<ATNConfig>();
/*  975 */       closure(c, configs, closureBusy, true, fullCtx, false);
/*      */     } 
/*      */     
/*  978 */     return configs;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected ATNConfigSet applyPrecedenceFilter(ATNConfigSet configs) {
/* 1149 */     Map<Integer, PredictionContext> statesFromAlt1 = new HashMap<Integer, PredictionContext>();
/* 1150 */     ATNConfigSet configSet = new ATNConfigSet(configs.fullCtx);
/* 1151 */     for (ATNConfig config : configs) {
/*      */       
/* 1153 */       if (config.alt != 1) {
/*      */         continue;
/*      */       }
/*      */       
/* 1157 */       SemanticContext updatedContext = config.semanticContext.evalPrecedence(this.parser, this._outerContext);
/* 1158 */       if (updatedContext == null) {
/*      */         continue;
/*      */       }
/*      */ 
/*      */       
/* 1163 */       statesFromAlt1.put(Integer.valueOf(config.state.stateNumber), config.context);
/* 1164 */       if (updatedContext != config.semanticContext) {
/* 1165 */         configSet.add(new ATNConfig(config, updatedContext), this.mergeCache);
/*      */         continue;
/*      */       } 
/* 1168 */       configSet.add(config, this.mergeCache);
/*      */     } 
/*      */ 
/*      */     
/* 1172 */     for (ATNConfig config : configs) {
/* 1173 */       if (config.alt == 1) {
/*      */         continue;
/*      */       }
/*      */ 
/*      */       
/* 1178 */       if (!config.isPrecedenceFilterSuppressed()) {
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 1183 */         PredictionContext context = statesFromAlt1.get(Integer.valueOf(config.state.stateNumber));
/* 1184 */         if (context != null && context.equals(config.context)) {
/*      */           continue;
/*      */         }
/*      */       } 
/*      */ 
/*      */       
/* 1190 */       configSet.add(config, this.mergeCache);
/*      */     } 
/*      */     
/* 1193 */     return configSet;
/*      */   }
/*      */   
/*      */   protected ATNState getReachableTarget(Transition trans, int ttype) {
/* 1197 */     if (trans.matches(ttype, 0, this.atn.maxTokenType)) {
/* 1198 */       return trans.target;
/*      */     }
/*      */     
/* 1201 */     return null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected SemanticContext[] getPredsForAmbigAlts(BitSet ambigAlts, ATNConfigSet configs, int nalts) {
/* 1220 */     SemanticContext[] altToPred = new SemanticContext[nalts + 1];
/* 1221 */     for (ATNConfig c : configs) {
/* 1222 */       if (ambigAlts.get(c.alt)) {
/* 1223 */         altToPred[c.alt] = SemanticContext.or(altToPred[c.alt], c.semanticContext);
/*      */       }
/*      */     } 
/*      */     
/* 1227 */     int nPredAlts = 0;
/* 1228 */     for (int i = 1; i <= nalts; i++) {
/* 1229 */       if (altToPred[i] == null) {
/* 1230 */         altToPred[i] = SemanticContext.NONE;
/*      */       }
/* 1232 */       else if (altToPred[i] != SemanticContext.NONE) {
/* 1233 */         nPredAlts++;
/*      */       } 
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1243 */     if (nPredAlts == 0) altToPred = null;
/*      */     
/* 1245 */     return altToPred;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected DFAState.PredPrediction[] getPredicatePredictions(BitSet ambigAlts, SemanticContext[] altToPred) {
/* 1251 */     List<DFAState.PredPrediction> pairs = new ArrayList<DFAState.PredPrediction>();
/* 1252 */     boolean containsPredicate = false;
/* 1253 */     for (int i = 1; i < altToPred.length; i++) {
/* 1254 */       SemanticContext pred = altToPred[i];
/*      */ 
/*      */       
/* 1257 */       assert pred != null;
/*      */       
/* 1259 */       if (ambigAlts != null && ambigAlts.get(i)) {
/* 1260 */         pairs.add(new DFAState.PredPrediction(pred, i));
/*      */       }
/* 1262 */       if (pred != SemanticContext.NONE) containsPredicate = true;
/*      */     
/*      */     } 
/* 1265 */     if (!containsPredicate) {
/* 1266 */       return null;
/*      */     }
/*      */ 
/*      */     
/* 1270 */     return pairs.<DFAState.PredPrediction>toArray(new DFAState.PredPrediction[pairs.size()]);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected int getSynValidOrSemInvalidAltThatFinishedDecisionEntryRule(ATNConfigSet configs, ParserRuleContext outerContext) {
/* 1322 */     Pair<ATNConfigSet, ATNConfigSet> sets = splitAccordingToSemanticValidity(configs, outerContext);
/*      */     
/* 1324 */     ATNConfigSet semValidConfigs = (ATNConfigSet)sets.a;
/* 1325 */     ATNConfigSet semInvalidConfigs = (ATNConfigSet)sets.b;
/* 1326 */     int alt = getAltThatFinishedDecisionEntryRule(semValidConfigs);
/* 1327 */     if (alt != 0) {
/* 1328 */       return alt;
/*      */     }
/*      */     
/* 1331 */     if (semInvalidConfigs.size() > 0) {
/* 1332 */       alt = getAltThatFinishedDecisionEntryRule(semInvalidConfigs);
/* 1333 */       if (alt != 0) {
/* 1334 */         return alt;
/*      */       }
/*      */     } 
/* 1337 */     return 0;
/*      */   }
/*      */   
/*      */   protected int getAltThatFinishedDecisionEntryRule(ATNConfigSet configs) {
/* 1341 */     IntervalSet alts = new IntervalSet(new int[0]);
/* 1342 */     for (ATNConfig c : configs) {
/* 1343 */       if (c.getOuterContextDepth() > 0 || (c.state instanceof RuleStopState && c.context.hasEmptyPath())) {
/* 1344 */         alts.add(c.alt);
/*      */       }
/*      */     } 
/* 1347 */     if (alts.size() == 0) return 0; 
/* 1348 */     return alts.getMinElement();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected Pair<ATNConfigSet, ATNConfigSet> splitAccordingToSemanticValidity(ATNConfigSet configs, ParserRuleContext outerContext) {
/* 1364 */     ATNConfigSet succeeded = new ATNConfigSet(configs.fullCtx);
/* 1365 */     ATNConfigSet failed = new ATNConfigSet(configs.fullCtx);
/* 1366 */     for (ATNConfig c : configs) {
/* 1367 */       if (c.semanticContext != SemanticContext.NONE) {
/* 1368 */         boolean predicateEvaluationResult = evalSemanticContext(c.semanticContext, outerContext, c.alt, configs.fullCtx);
/* 1369 */         if (predicateEvaluationResult) {
/* 1370 */           succeeded.add(c);
/*      */           continue;
/*      */         } 
/* 1373 */         failed.add(c);
/*      */         
/*      */         continue;
/*      */       } 
/* 1377 */       succeeded.add(c);
/*      */     } 
/*      */     
/* 1380 */     return new Pair<ATNConfigSet, ATNConfigSet>(succeeded, failed);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected BitSet evalSemanticContext(DFAState.PredPrediction[] predPredictions, ParserRuleContext outerContext, boolean complete) {
/* 1393 */     BitSet predictions = new BitSet();
/* 1394 */     for (DFAState.PredPrediction pair : predPredictions) {
/* 1395 */       if (pair.pred == SemanticContext.NONE) {
/* 1396 */         predictions.set(pair.alt);
/* 1397 */         if (!complete) {
/*      */           break;
/*      */         }
/*      */       }
/*      */       else {
/*      */         
/* 1403 */         boolean fullCtx = false;
/* 1404 */         boolean predicateEvaluationResult = evalSemanticContext(pair.pred, outerContext, pair.alt, fullCtx);
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 1409 */         if (predicateEvaluationResult) {
/*      */           
/* 1411 */           predictions.set(pair.alt);
/* 1412 */           if (!complete) {
/*      */             break;
/*      */           }
/*      */         } 
/*      */       } 
/*      */     } 
/* 1418 */     return predictions;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected boolean evalSemanticContext(SemanticContext pred, ParserRuleContext parserCallStack, int alt, boolean fullCtx) {
/* 1452 */     return pred.eval(this.parser, parserCallStack);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void closure(ATNConfig config, ATNConfigSet configs, Set<ATNConfig> closureBusy, boolean collectPredicates, boolean fullCtx, boolean treatEofAsEpsilon) {
/* 1469 */     int initialDepth = 0;
/* 1470 */     closureCheckingStopState(config, configs, closureBusy, collectPredicates, fullCtx, 0, treatEofAsEpsilon);
/*      */ 
/*      */     
/* 1473 */     assert !fullCtx || !configs.dipsIntoOuterContext;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void closureCheckingStopState(ATNConfig config, ATNConfigSet configs, Set<ATNConfig> closureBusy, boolean collectPredicates, boolean fullCtx, int depth, boolean treatEofAsEpsilon) {
/* 1486 */     if (config.state instanceof RuleStopState) {
/*      */ 
/*      */       
/* 1489 */       if (!config.context.isEmpty()) {
/* 1490 */         for (int i = 0; i < config.context.size(); i++) {
/* 1491 */           if (config.context.getReturnState(i) == Integer.MAX_VALUE) {
/* 1492 */             if (fullCtx) {
/* 1493 */               configs.add(new ATNConfig(config, config.state, PredictionContext.EMPTY), this.mergeCache);
/*      */ 
/*      */             
/*      */             }
/*      */             else {
/*      */ 
/*      */               
/* 1500 */               closure_(config, configs, closureBusy, collectPredicates, fullCtx, depth, treatEofAsEpsilon);
/*      */             }
/*      */           
/*      */           } else {
/*      */             
/* 1505 */             ATNState returnState = this.atn.states.get(config.context.getReturnState(i));
/* 1506 */             PredictionContext newContext = config.context.getParent(i);
/* 1507 */             ATNConfig c = new ATNConfig(returnState, config.alt, newContext, config.semanticContext);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */             
/* 1516 */             c.reachesIntoOuterContext = config.reachesIntoOuterContext;
/* 1517 */             assert depth > Integer.MIN_VALUE;
/* 1518 */             closureCheckingStopState(c, configs, closureBusy, collectPredicates, fullCtx, depth - 1, treatEofAsEpsilon);
/*      */           } 
/*      */         } 
/*      */         return;
/*      */       } 
/* 1523 */       if (fullCtx) {
/*      */         
/* 1525 */         configs.add(config, this.mergeCache);
/*      */ 
/*      */ 
/*      */         
/*      */         return;
/*      */       } 
/*      */     } 
/*      */ 
/*      */ 
/*      */     
/* 1535 */     closure_(config, configs, closureBusy, collectPredicates, fullCtx, depth, treatEofAsEpsilon);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void closure_(ATNConfig config, ATNConfigSet configs, Set<ATNConfig> closureBusy, boolean collectPredicates, boolean fullCtx, int depth, boolean treatEofAsEpsilon) {
/* 1548 */     ATNState p = config.state;
/*      */     
/* 1550 */     if (!p.onlyHasEpsilonTransitions()) {
/* 1551 */       configs.add(config, this.mergeCache);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1557 */     for (int i = 0; i < p.getNumberOfTransitions(); i++) {
/* 1558 */       Transition t = p.transition(i);
/* 1559 */       boolean continueCollecting = (!(t instanceof ActionTransition) && collectPredicates);
/*      */       
/* 1561 */       ATNConfig c = getEpsilonTarget(config, t, continueCollecting, (depth == 0), fullCtx, treatEofAsEpsilon);
/*      */       
/* 1563 */       if (c == null || (
/* 1564 */         !t.isEpsilon() && !closureBusy.add(c))) {
/*      */         continue;
/*      */       }
/*      */ 
/*      */       
/* 1569 */       int newDepth = depth;
/* 1570 */       if (config.state instanceof RuleStopState) {
/* 1571 */         assert !fullCtx;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 1578 */         if (!closureBusy.add(c)) {
/*      */           continue;
/*      */         }
/*      */ 
/*      */         
/* 1583 */         if (this._dfa != null && this._dfa.isPrecedenceDfa()) {
/* 1584 */           int outermostPrecedenceReturn = ((EpsilonTransition)t).outermostPrecedenceReturn();
/* 1585 */           if (outermostPrecedenceReturn == this._dfa.atnStartState.ruleIndex) {
/* 1586 */             c.setPrecedenceFilterSuppressed(true);
/*      */           }
/*      */         } 
/*      */         
/* 1590 */         c.reachesIntoOuterContext++;
/* 1591 */         configs.dipsIntoOuterContext = true;
/* 1592 */         assert newDepth > Integer.MIN_VALUE;
/* 1593 */         newDepth--;
/*      */       
/*      */       }
/* 1596 */       else if (t instanceof RuleTransition) {
/*      */         
/* 1598 */         if (newDepth >= 0) {
/* 1599 */           newDepth++;
/*      */         }
/*      */       } 
/*      */       
/* 1603 */       closureCheckingStopState(c, configs, closureBusy, continueCollecting, fullCtx, newDepth, treatEofAsEpsilon);
/*      */       continue;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public String getRuleName(int index) {
/* 1611 */     if (this.parser != null && index >= 0) return this.parser.getRuleNames()[index]; 
/* 1612 */     return "<rule " + index + ">";
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected ATNConfig getEpsilonTarget(ATNConfig config, Transition t, boolean collectPredicates, boolean inContext, boolean fullCtx, boolean treatEofAsEpsilon) {
/* 1623 */     switch (t.getSerializationType()) {
/*      */       case 3:
/* 1625 */         return ruleTransition(config, (RuleTransition)t);
/*      */       
/*      */       case 10:
/* 1628 */         return precedenceTransition(config, (PrecedencePredicateTransition)t, collectPredicates, inContext, fullCtx);
/*      */       
/*      */       case 4:
/* 1631 */         return predTransition(config, (PredicateTransition)t, collectPredicates, inContext, fullCtx);
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       case 6:
/* 1637 */         return actionTransition(config, (ActionTransition)t);
/*      */       
/*      */       case 1:
/* 1640 */         return new ATNConfig(config, t.target);
/*      */ 
/*      */ 
/*      */       
/*      */       case 2:
/*      */       case 5:
/*      */       case 7:
/* 1647 */         if (treatEofAsEpsilon && 
/* 1648 */           t.matches(-1, 0, 1)) {
/* 1649 */           return new ATNConfig(config, t.target);
/*      */         }
/*      */ 
/*      */         
/* 1653 */         return null;
/*      */     } 
/*      */     
/* 1656 */     return null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected ATNConfig actionTransition(ATNConfig config, ActionTransition t) {
/* 1663 */     return new ATNConfig(config, t.target);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ATNConfig precedenceTransition(ATNConfig config, PrecedencePredicateTransition pt, boolean collectPredicates, boolean inContext, boolean fullCtx) {
/* 1683 */     ATNConfig c = null;
/* 1684 */     if (collectPredicates && inContext) {
/* 1685 */       if (fullCtx) {
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 1690 */         int currentPosition = this._input.index();
/* 1691 */         this._input.seek(this._startIndex);
/* 1692 */         boolean predSucceeds = evalSemanticContext(pt.getPredicate(), this._outerContext, config.alt, fullCtx);
/* 1693 */         this._input.seek(currentPosition);
/* 1694 */         if (predSucceeds) {
/* 1695 */           c = new ATNConfig(config, pt.target);
/*      */         }
/*      */       } else {
/*      */         
/* 1699 */         SemanticContext newSemCtx = SemanticContext.and(config.semanticContext, pt.getPredicate());
/*      */         
/* 1701 */         c = new ATNConfig(config, pt.target, newSemCtx);
/*      */       } 
/*      */     } else {
/*      */       
/* 1705 */       c = new ATNConfig(config, pt.target);
/*      */     } 
/*      */ 
/*      */     
/* 1709 */     return c;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected ATNConfig predTransition(ATNConfig config, PredicateTransition pt, boolean collectPredicates, boolean inContext, boolean fullCtx) {
/* 1729 */     ATNConfig c = null;
/* 1730 */     if (collectPredicates && (!pt.isCtxDependent || (pt.isCtxDependent && inContext))) {
/*      */ 
/*      */       
/* 1733 */       if (fullCtx) {
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 1738 */         int currentPosition = this._input.index();
/* 1739 */         this._input.seek(this._startIndex);
/* 1740 */         boolean predSucceeds = evalSemanticContext(pt.getPredicate(), this._outerContext, config.alt, fullCtx);
/* 1741 */         this._input.seek(currentPosition);
/* 1742 */         if (predSucceeds) {
/* 1743 */           c = new ATNConfig(config, pt.target);
/*      */         }
/*      */       } else {
/*      */         
/* 1747 */         SemanticContext newSemCtx = SemanticContext.and(config.semanticContext, pt.getPredicate());
/*      */         
/* 1749 */         c = new ATNConfig(config, pt.target, newSemCtx);
/*      */       } 
/*      */     } else {
/*      */       
/* 1753 */       c = new ATNConfig(config, pt.target);
/*      */     } 
/*      */ 
/*      */     
/* 1757 */     return c;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected ATNConfig ruleTransition(ATNConfig config, RuleTransition t) {
/* 1767 */     ATNState returnState = t.followState;
/* 1768 */     PredictionContext newContext = SingletonPredictionContext.create(config.context, returnState.stateNumber);
/*      */     
/* 1770 */     return new ATNConfig(config, t.target, newContext);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected BitSet getConflictingAlts(ATNConfigSet configs) {
/* 1783 */     Collection<BitSet> altsets = PredictionMode.getConflictingAltSubsets(configs);
/* 1784 */     return PredictionMode.getAlts(altsets);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected BitSet getConflictingAltsOrUniqueAlt(ATNConfigSet configs) {
/*      */     BitSet conflictingAlts;
/* 1825 */     if (configs.uniqueAlt != 0) {
/* 1826 */       conflictingAlts = new BitSet();
/* 1827 */       conflictingAlts.set(configs.uniqueAlt);
/*      */     } else {
/*      */       
/* 1830 */       conflictingAlts = configs.conflictingAlts;
/*      */     } 
/* 1832 */     return conflictingAlts;
/*      */   }
/*      */ 
/*      */   
/*      */   public String getTokenName(int t) {
/* 1837 */     if (t == -1) {
/* 1838 */       return "EOF";
/*      */     }
/*      */     
/* 1841 */     Vocabulary vocabulary = (this.parser != null) ? this.parser.getVocabulary() : VocabularyImpl.EMPTY_VOCABULARY;
/* 1842 */     String displayName = vocabulary.getDisplayName(t);
/* 1843 */     if (displayName.equals(Integer.toString(t))) {
/* 1844 */       return displayName;
/*      */     }
/*      */     
/* 1847 */     return displayName + "<" + t + ">";
/*      */   }
/*      */   
/*      */   public String getLookaheadName(TokenStream input) {
/* 1851 */     return getTokenName(input.LA(1));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void dumpDeadEndConfigs(NoViableAltException nvae) {
/* 1859 */     System.err.println("dead end configs: ");
/* 1860 */     for (ATNConfig c : nvae.getDeadEndConfigs()) {
/* 1861 */       String trans = "no edges";
/* 1862 */       if (c.state.getNumberOfTransitions() > 0) {
/* 1863 */         Transition t = c.state.transition(0);
/* 1864 */         if (t instanceof AtomTransition) {
/* 1865 */           AtomTransition at = (AtomTransition)t;
/* 1866 */           trans = "Atom " + getTokenName(at.label);
/*      */         }
/* 1868 */         else if (t instanceof SetTransition) {
/* 1869 */           SetTransition st = (SetTransition)t;
/* 1870 */           boolean not = st instanceof NotSetTransition;
/* 1871 */           trans = (not ? "~" : "") + "Set " + st.set.toString();
/*      */         } 
/*      */       } 
/* 1874 */       System.err.println(c.toString(this.parser, true) + ":" + trans);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected NoViableAltException noViableAlt(TokenStream input, ParserRuleContext outerContext, ATNConfigSet configs, int startIndex) {
/* 1884 */     return new NoViableAltException(this.parser, input, input.get(startIndex), input.LT(1), configs, outerContext);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected static int getUniqueAlt(ATNConfigSet configs) {
/* 1891 */     int alt = 0;
/* 1892 */     for (ATNConfig c : configs) {
/* 1893 */       if (alt == 0) {
/* 1894 */         alt = c.alt; continue;
/*      */       } 
/* 1896 */       if (c.alt != alt) {
/* 1897 */         return 0;
/*      */       }
/*      */     } 
/* 1900 */     return alt;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected DFAState addDFAEdge(DFA dfa, DFAState from, int t, DFAState to) {
/* 1932 */     if (to == null) {
/* 1933 */       return null;
/*      */     }
/*      */     
/* 1936 */     to = addDFAState(dfa, to);
/* 1937 */     if (from == null || t < -1 || t > this.atn.maxTokenType) {
/* 1938 */       return to;
/*      */     }
/*      */     
/* 1941 */     synchronized (from) {
/* 1942 */       if (from.edges == null) {
/* 1943 */         from.edges = new DFAState[this.atn.maxTokenType + 1 + 1];
/*      */       }
/*      */       
/* 1946 */       from.edges[t + 1] = to;
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1953 */     return to;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected DFAState addDFAState(DFA dfa, DFAState D) {
/* 1972 */     if (D == ERROR) {
/* 1973 */       return D;
/*      */     }
/*      */     
/* 1976 */     synchronized (dfa.states) {
/* 1977 */       DFAState existing = dfa.states.get(D);
/* 1978 */       if (existing != null) return existing;
/*      */       
/* 1980 */       D.stateNumber = dfa.states.size();
/* 1981 */       if (!D.configs.isReadonly()) {
/* 1982 */         D.configs.optimizeConfigs(this);
/* 1983 */         D.configs.setReadonly(true);
/*      */       } 
/* 1985 */       dfa.states.put(D, D);
/*      */       
/* 1987 */       return D;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void reportAttemptingFullContext(DFA dfa, BitSet conflictingAlts, ATNConfigSet configs, int startIndex, int stopIndex) {
/* 1997 */     if (this.parser != null) this.parser.getErrorListenerDispatch().reportAttemptingFullContext(this.parser, dfa, startIndex, stopIndex, conflictingAlts, configs);
/*      */   
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void reportContextSensitivity(DFA dfa, int prediction, ATNConfigSet configs, int startIndex, int stopIndex) {
/* 2006 */     if (this.parser != null) this.parser.getErrorListenerDispatch().reportContextSensitivity(this.parser, dfa, startIndex, stopIndex, prediction, configs);
/*      */   
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void reportAmbiguity(DFA dfa, DFAState D, int startIndex, int stopIndex, boolean exact, BitSet ambigAlts, ATNConfigSet configs) {
/* 2023 */     if (this.parser != null) this.parser.getErrorListenerDispatch().reportAmbiguity(this.parser, dfa, startIndex, stopIndex, exact, ambigAlts, configs);
/*      */   
/*      */   }
/*      */   
/*      */   public final void setPredictionMode(PredictionMode mode) {
/* 2028 */     this.mode = mode;
/*      */   }
/*      */ 
/*      */   
/*      */   public final PredictionMode getPredictionMode() {
/* 2033 */     return this.mode;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Parser getParser() {
/* 2040 */     return this.parser;
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\antlr\v4\runtime\atn\ParserATNSimulator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */