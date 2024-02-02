package org.antlr.v4.runtime;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNState;
import org.antlr.v4.runtime.atn.ActionTransition;
import org.antlr.v4.runtime.atn.AtomTransition;
import org.antlr.v4.runtime.atn.DecisionState;
import org.antlr.v4.runtime.atn.LoopEndState;
import org.antlr.v4.runtime.atn.ParserATNSimulator;
import org.antlr.v4.runtime.atn.PrecedencePredicateTransition;
import org.antlr.v4.runtime.atn.PredicateTransition;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.atn.RuleStartState;
import org.antlr.v4.runtime.atn.RuleTransition;
import org.antlr.v4.runtime.atn.StarLoopEntryState;
import org.antlr.v4.runtime.atn.Transition;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.Pair;

public class ParserInterpreter extends Parser {
   protected final String grammarFileName;
   protected final ATN atn;
   protected final DFA[] decisionToDFA;
   protected final PredictionContextCache sharedContextCache;
   /** @deprecated */
   @Deprecated
   protected final String[] tokenNames;
   protected final String[] ruleNames;
   private final Vocabulary vocabulary;
   protected final Deque<Pair<ParserRuleContext, Integer>> _parentContextStack;
   protected int overrideDecision;
   protected int overrideDecisionInputIndex;
   protected int overrideDecisionAlt;
   protected boolean overrideDecisionReached;
   protected InterpreterRuleContext overrideDecisionRoot;
   protected InterpreterRuleContext rootContext;

   /** @deprecated */
   @Deprecated
   public ParserInterpreter(String grammarFileName, Collection<String> tokenNames, Collection<String> ruleNames, ATN atn, TokenStream input) {
      this(grammarFileName, VocabularyImpl.fromTokenNames((String[])tokenNames.toArray(new String[tokenNames.size()])), ruleNames, atn, input);
   }

   public ParserInterpreter(String grammarFileName, Vocabulary vocabulary, Collection<String> ruleNames, ATN atn, TokenStream input) {
      super(input);
      this.sharedContextCache = new PredictionContextCache();
      this._parentContextStack = new ArrayDeque();
      this.overrideDecision = -1;
      this.overrideDecisionInputIndex = -1;
      this.overrideDecisionAlt = -1;
      this.overrideDecisionReached = false;
      this.overrideDecisionRoot = null;
      this.grammarFileName = grammarFileName;
      this.atn = atn;
      this.tokenNames = new String[atn.maxTokenType];

      int numberOfDecisions;
      for(numberOfDecisions = 0; numberOfDecisions < this.tokenNames.length; ++numberOfDecisions) {
         this.tokenNames[numberOfDecisions] = vocabulary.getDisplayName(numberOfDecisions);
      }

      this.ruleNames = (String[])ruleNames.toArray(new String[ruleNames.size()]);
      this.vocabulary = vocabulary;
      numberOfDecisions = atn.getNumberOfDecisions();
      this.decisionToDFA = new DFA[numberOfDecisions];

      for(int i = 0; i < numberOfDecisions; ++i) {
         DecisionState decisionState = atn.getDecisionState(i);
         this.decisionToDFA[i] = new DFA(decisionState, i);
      }

      this.setInterpreter(new ParserATNSimulator(this, atn, this.decisionToDFA, this.sharedContextCache));
   }

   public void reset() {
      super.reset();
      this.overrideDecisionReached = false;
      this.overrideDecisionRoot = null;
   }

   public ATN getATN() {
      return this.atn;
   }

   /** @deprecated */
   @Deprecated
   public String[] getTokenNames() {
      return this.tokenNames;
   }

   public Vocabulary getVocabulary() {
      return this.vocabulary;
   }

   public String[] getRuleNames() {
      return this.ruleNames;
   }

   public String getGrammarFileName() {
      return this.grammarFileName;
   }

   public ParserRuleContext parse(int startRuleIndex) {
      RuleStartState startRuleStartState = this.atn.ruleToStartState[startRuleIndex];
      this.rootContext = this.createInterpreterRuleContext((ParserRuleContext)null, -1, startRuleIndex);
      if (startRuleStartState.isLeftRecursiveRule) {
         this.enterRecursionRule(this.rootContext, startRuleStartState.stateNumber, startRuleIndex, 0);
      } else {
         this.enterRule(this.rootContext, startRuleStartState.stateNumber, startRuleIndex);
      }

      while(true) {
         while(true) {
            ATNState p = this.getATNState();
            switch (p.getStateType()) {
               case 7:
                  if (this._ctx.isEmpty()) {
                     if (startRuleStartState.isLeftRecursiveRule) {
                        ParserRuleContext result = this._ctx;
                        Pair<ParserRuleContext, Integer> parentContext = (Pair)this._parentContextStack.pop();
                        this.unrollRecursionContexts((ParserRuleContext)parentContext.a);
                        return result;
                     }

                     this.exitRule();
                     return this.rootContext;
                  }

                  this.visitRuleStopState(p);
                  break;
               default:
                  try {
                     this.visitState(p);
                  } catch (RecognitionException var6) {
                     this.setState(this.atn.ruleToStopState[p.ruleIndex].stateNumber);
                     this.getContext().exception = var6;
                     this.getErrorHandler().reportError(this, var6);
                     this.recover(var6);
                  }
            }
         }
      }
   }

   public void enterRecursionRule(ParserRuleContext localctx, int state, int ruleIndex, int precedence) {
      Pair<ParserRuleContext, Integer> pair = new Pair(this._ctx, localctx.invokingState);
      this._parentContextStack.push(pair);
      super.enterRecursionRule(localctx, state, ruleIndex, precedence);
   }

   protected ATNState getATNState() {
      return (ATNState)this.atn.states.get(this.getState());
   }

   protected void visitState(ATNState p) {
      int predictedAlt = 1;
      if (p instanceof DecisionState) {
         predictedAlt = this.visitDecisionState((DecisionState)p);
      }

      Transition transition = p.transition(predictedAlt - 1);
      switch (transition.getSerializationType()) {
         case 1:
            if (p.getStateType() == 10 && ((StarLoopEntryState)p).isPrecedenceDecision && !(transition.target instanceof LoopEndState)) {
               InterpreterRuleContext localctx = this.createInterpreterRuleContext((ParserRuleContext)((Pair)this._parentContextStack.peek()).a, (Integer)((Pair)this._parentContextStack.peek()).b, this._ctx.getRuleIndex());
               this.pushNewRecursionContext(localctx, this.atn.ruleToStartState[p.ruleIndex].stateNumber, this._ctx.getRuleIndex());
            }
            break;
         case 2:
         case 7:
         case 8:
            if (!transition.matches(this._input.LA(1), 1, 65535)) {
               this.recoverInline();
            }

            this.matchWildcard();
            break;
         case 3:
            RuleStartState ruleStartState = (RuleStartState)transition.target;
            int ruleIndex = ruleStartState.ruleIndex;
            InterpreterRuleContext newctx = this.createInterpreterRuleContext(this._ctx, p.stateNumber, ruleIndex);
            if (ruleStartState.isLeftRecursiveRule) {
               this.enterRecursionRule(newctx, ruleStartState.stateNumber, ruleIndex, ((RuleTransition)transition).precedence);
            } else {
               this.enterRule(newctx, transition.target.stateNumber, ruleIndex);
            }
            break;
         case 4:
            PredicateTransition predicateTransition = (PredicateTransition)transition;
            if (!this.sempred(this._ctx, predicateTransition.ruleIndex, predicateTransition.predIndex)) {
               throw new FailedPredicateException(this);
            }
            break;
         case 5:
            this.match(((AtomTransition)transition).label);
            break;
         case 6:
            ActionTransition actionTransition = (ActionTransition)transition;
            this.action(this._ctx, actionTransition.ruleIndex, actionTransition.actionIndex);
            break;
         case 9:
            this.matchWildcard();
            break;
         case 10:
            if (!this.precpred(this._ctx, ((PrecedencePredicateTransition)transition).precedence)) {
               throw new FailedPredicateException(this, String.format("precpred(_ctx, %d)", ((PrecedencePredicateTransition)transition).precedence));
            }
            break;
         default:
            throw new UnsupportedOperationException("Unrecognized ATN transition type.");
      }

      this.setState(transition.target.stateNumber);
   }

   protected int visitDecisionState(DecisionState p) {
      int predictedAlt = 1;
      if (p.getNumberOfTransitions() > 1) {
         this.getErrorHandler().sync(this);
         int decision = p.decision;
         if (decision == this.overrideDecision && this._input.index() == this.overrideDecisionInputIndex && !this.overrideDecisionReached) {
            predictedAlt = this.overrideDecisionAlt;
            this.overrideDecisionReached = true;
         } else {
            predictedAlt = ((ParserATNSimulator)this.getInterpreter()).adaptivePredict(this._input, decision, this._ctx);
         }
      }

      return predictedAlt;
   }

   protected InterpreterRuleContext createInterpreterRuleContext(ParserRuleContext parent, int invokingStateNumber, int ruleIndex) {
      return new InterpreterRuleContext(parent, invokingStateNumber, ruleIndex);
   }

   protected void visitRuleStopState(ATNState p) {
      RuleStartState ruleStartState = this.atn.ruleToStartState[p.ruleIndex];
      if (ruleStartState.isLeftRecursiveRule) {
         Pair<ParserRuleContext, Integer> parentContext = (Pair)this._parentContextStack.pop();
         this.unrollRecursionContexts((ParserRuleContext)parentContext.a);
         this.setState((Integer)parentContext.b);
      } else {
         this.exitRule();
      }

      RuleTransition ruleTransition = (RuleTransition)((ATNState)this.atn.states.get(this.getState())).transition(0);
      this.setState(ruleTransition.followState.stateNumber);
   }

   public void addDecisionOverride(int decision, int tokenIndex, int forcedAlt) {
      this.overrideDecision = decision;
      this.overrideDecisionInputIndex = tokenIndex;
      this.overrideDecisionAlt = forcedAlt;
   }

   public InterpreterRuleContext getOverrideDecisionRoot() {
      return this.overrideDecisionRoot;
   }

   protected void recover(RecognitionException e) {
      int i = this._input.index();
      this.getErrorHandler().recover(this, e);
      if (this._input.index() == i) {
         Token tok;
         if (e instanceof InputMismatchException) {
            InputMismatchException ime = (InputMismatchException)e;
            tok = e.getOffendingToken();
            int expectedTokenType = ime.getExpectedTokens().getMinElement();
            Token errToken = this.getTokenFactory().create(new Pair(tok.getTokenSource(), tok.getTokenSource().getInputStream()), expectedTokenType, tok.getText(), 0, -1, -1, tok.getLine(), tok.getCharPositionInLine());
            this._ctx.addErrorNode(errToken);
         } else {
            Token tok = e.getOffendingToken();
            tok = this.getTokenFactory().create(new Pair(tok.getTokenSource(), tok.getTokenSource().getInputStream()), 0, tok.getText(), 0, -1, -1, tok.getLine(), tok.getCharPositionInLine());
            this._ctx.addErrorNode(tok);
         }
      }

   }

   protected Token recoverInline() {
      return this._errHandler.recoverInline(this);
   }

   public InterpreterRuleContext getRootContext() {
      return this.rootContext;
   }
}
