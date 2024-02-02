/*     */ package org.antlr.v4.runtime;
/*     */ 
/*     */ import java.util.ArrayDeque;
/*     */ import java.util.Collection;
/*     */ import java.util.Deque;
/*     */ import org.antlr.v4.runtime.atn.ATN;
/*     */ import org.antlr.v4.runtime.atn.ATNState;
/*     */ import org.antlr.v4.runtime.atn.ActionTransition;
/*     */ import org.antlr.v4.runtime.atn.AtomTransition;
/*     */ import org.antlr.v4.runtime.atn.DecisionState;
/*     */ import org.antlr.v4.runtime.atn.ParserATNSimulator;
/*     */ import org.antlr.v4.runtime.atn.PrecedencePredicateTransition;
/*     */ import org.antlr.v4.runtime.atn.PredicateTransition;
/*     */ import org.antlr.v4.runtime.atn.PredictionContextCache;
/*     */ import org.antlr.v4.runtime.atn.RuleStartState;
/*     */ import org.antlr.v4.runtime.atn.RuleTransition;
/*     */ import org.antlr.v4.runtime.atn.StarLoopEntryState;
/*     */ import org.antlr.v4.runtime.atn.Transition;
/*     */ import org.antlr.v4.runtime.dfa.DFA;
/*     */ import org.antlr.v4.runtime.misc.Pair;
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
/*     */ public class ParserInterpreter
/*     */   extends Parser
/*     */ {
/*     */   protected final String grammarFileName;
/*     */   protected final ATN atn;
/*     */   protected final DFA[] decisionToDFA;
/*  72 */   protected final PredictionContextCache sharedContextCache = new PredictionContextCache();
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   protected final String[] tokenNames;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected final String[] ruleNames;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final Vocabulary vocabulary;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  93 */   protected final Deque<Pair<ParserRuleContext, Integer>> _parentContextStack = new ArrayDeque<Pair<ParserRuleContext, Integer>>();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  99 */   protected int overrideDecision = -1;
/* 100 */   protected int overrideDecisionInputIndex = -1;
/* 101 */   protected int overrideDecisionAlt = -1;
/*     */ 
/*     */ 
/*     */   
/*     */   protected boolean overrideDecisionReached = false;
/*     */ 
/*     */   
/* 108 */   protected InterpreterRuleContext overrideDecisionRoot = null;
/*     */ 
/*     */ 
/*     */   
/*     */   protected InterpreterRuleContext rootContext;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public ParserInterpreter(String grammarFileName, Collection<String> tokenNames, Collection<String> ruleNames, ATN atn, TokenStream input) {
/* 119 */     this(grammarFileName, VocabularyImpl.fromTokenNames(tokenNames.<String>toArray(new String[tokenNames.size()])), ruleNames, atn, input);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public ParserInterpreter(String grammarFileName, Vocabulary vocabulary, Collection<String> ruleNames, ATN atn, TokenStream input) {
/* 125 */     super(input);
/* 126 */     this.grammarFileName = grammarFileName;
/* 127 */     this.atn = atn;
/* 128 */     this.tokenNames = new String[atn.maxTokenType];
/* 129 */     for (int i = 0; i < this.tokenNames.length; i++) {
/* 130 */       this.tokenNames[i] = vocabulary.getDisplayName(i);
/*     */     }
/*     */     
/* 133 */     this.ruleNames = ruleNames.<String>toArray(new String[ruleNames.size()]);
/* 134 */     this.vocabulary = vocabulary;
/*     */ 
/*     */     
/* 137 */     int numberOfDecisions = atn.getNumberOfDecisions();
/* 138 */     this.decisionToDFA = new DFA[numberOfDecisions];
/* 139 */     for (int j = 0; j < numberOfDecisions; j++) {
/* 140 */       DecisionState decisionState = atn.getDecisionState(j);
/* 141 */       this.decisionToDFA[j] = new DFA(decisionState, j);
/*     */     } 
/*     */ 
/*     */     
/* 145 */     setInterpreter(new ParserATNSimulator(this, atn, this.decisionToDFA, this.sharedContextCache));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void reset() {
/* 152 */     super.reset();
/* 153 */     this.overrideDecisionReached = false;
/* 154 */     this.overrideDecisionRoot = null;
/*     */   }
/*     */ 
/*     */   
/*     */   public ATN getATN() {
/* 159 */     return this.atn;
/*     */   }
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public String[] getTokenNames() {
/* 165 */     return this.tokenNames;
/*     */   }
/*     */ 
/*     */   
/*     */   public Vocabulary getVocabulary() {
/* 170 */     return this.vocabulary;
/*     */   }
/*     */ 
/*     */   
/*     */   public String[] getRuleNames() {
/* 175 */     return this.ruleNames;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getGrammarFileName() {
/* 180 */     return this.grammarFileName;
/*     */   }
/*     */ 
/*     */   
/*     */   public ParserRuleContext parse(int startRuleIndex) {
/* 185 */     RuleStartState startRuleStartState = this.atn.ruleToStartState[startRuleIndex];
/*     */     
/* 187 */     this.rootContext = createInterpreterRuleContext((ParserRuleContext)null, -1, startRuleIndex);
/* 188 */     if (startRuleStartState.isLeftRecursiveRule) {
/* 189 */       enterRecursionRule(this.rootContext, startRuleStartState.stateNumber, startRuleIndex, 0);
/*     */     } else {
/*     */       
/* 192 */       enterRule(this.rootContext, startRuleStartState.stateNumber, startRuleIndex);
/*     */     } 
/*     */     
/*     */     while (true) {
/* 196 */       ATNState p = getATNState();
/* 197 */       switch (p.getStateType()) {
/*     */         
/*     */         case 7:
/* 200 */           if (this._ctx.isEmpty()) {
/* 201 */             if (startRuleStartState.isLeftRecursiveRule) {
/* 202 */               ParserRuleContext result = this._ctx;
/* 203 */               Pair<ParserRuleContext, Integer> parentContext = this._parentContextStack.pop();
/* 204 */               unrollRecursionContexts((ParserRuleContext)parentContext.a);
/* 205 */               return result;
/*     */             } 
/*     */             
/* 208 */             exitRule();
/* 209 */             return this.rootContext;
/*     */           } 
/*     */ 
/*     */           
/* 213 */           visitRuleStopState(p);
/*     */           continue;
/*     */       } 
/*     */       
/*     */       try {
/* 218 */         visitState(p);
/*     */       }
/* 220 */       catch (RecognitionException e) {
/* 221 */         setState((this.atn.ruleToStopState[p.ruleIndex]).stateNumber);
/* 222 */         (getContext()).exception = e;
/* 223 */         getErrorHandler().reportError(this, e);
/* 224 */         recover(e);
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void enterRecursionRule(ParserRuleContext localctx, int state, int ruleIndex, int precedence) {
/* 234 */     Pair<ParserRuleContext, Integer> pair = new Pair<ParserRuleContext, Integer>(this._ctx, Integer.valueOf(localctx.invokingState));
/* 235 */     this._parentContextStack.push(pair);
/* 236 */     super.enterRecursionRule(localctx, state, ruleIndex, precedence);
/*     */   }
/*     */   
/*     */   protected ATNState getATNState() {
/* 240 */     return this.atn.states.get(getState()); } protected void visitState(ATNState p) { RuleStartState ruleStartState;
/*     */     int ruleIndex;
/*     */     InterpreterRuleContext newctx;
/*     */     PredicateTransition predicateTransition;
/*     */     ActionTransition actionTransition;
/* 245 */     int predictedAlt = 1;
/* 246 */     if (p instanceof DecisionState) {
/* 247 */       predictedAlt = visitDecisionState((DecisionState)p);
/*     */     }
/*     */     
/* 250 */     Transition transition = p.transition(predictedAlt - 1);
/* 251 */     switch (transition.getSerializationType()) {
/*     */       case 1:
/* 253 */         if (p.getStateType() == 10 && ((StarLoopEntryState)p).isPrecedenceDecision && !(transition.target instanceof org.antlr.v4.runtime.atn.LoopEndState)) {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */           
/* 259 */           InterpreterRuleContext localctx = createInterpreterRuleContext((ParserRuleContext)((Pair)this._parentContextStack.peek()).a, ((Integer)((Pair)this._parentContextStack.peek()).b).intValue(), this._ctx.getRuleIndex());
/*     */ 
/*     */ 
/*     */           
/* 263 */           pushNewRecursionContext(localctx, (this.atn.ruleToStartState[p.ruleIndex]).stateNumber, this._ctx.getRuleIndex());
/*     */         } 
/*     */         break;
/*     */ 
/*     */ 
/*     */       
/*     */       case 5:
/* 270 */         match(((AtomTransition)transition).label);
/*     */         break;
/*     */       
/*     */       case 2:
/*     */       case 7:
/*     */       case 8:
/* 276 */         if (!transition.matches(this._input.LA(1), 1, 65535)) {
/* 277 */           recoverInline();
/*     */         }
/* 279 */         matchWildcard();
/*     */         break;
/*     */       
/*     */       case 9:
/* 283 */         matchWildcard();
/*     */         break;
/*     */       
/*     */       case 3:
/* 287 */         ruleStartState = (RuleStartState)transition.target;
/* 288 */         ruleIndex = ruleStartState.ruleIndex;
/* 289 */         newctx = createInterpreterRuleContext(this._ctx, p.stateNumber, ruleIndex);
/* 290 */         if (ruleStartState.isLeftRecursiveRule) {
/* 291 */           enterRecursionRule(newctx, ruleStartState.stateNumber, ruleIndex, ((RuleTransition)transition).precedence);
/*     */           break;
/*     */         } 
/* 294 */         enterRule(newctx, transition.target.stateNumber, ruleIndex);
/*     */         break;
/*     */ 
/*     */       
/*     */       case 4:
/* 299 */         predicateTransition = (PredicateTransition)transition;
/* 300 */         if (!sempred(this._ctx, predicateTransition.ruleIndex, predicateTransition.predIndex)) {
/* 301 */           throw new FailedPredicateException(this);
/*     */         }
/*     */         break;
/*     */ 
/*     */       
/*     */       case 6:
/* 307 */         actionTransition = (ActionTransition)transition;
/* 308 */         action(this._ctx, actionTransition.ruleIndex, actionTransition.actionIndex);
/*     */         break;
/*     */       
/*     */       case 10:
/* 312 */         if (!precpred(this._ctx, ((PrecedencePredicateTransition)transition).precedence)) {
/* 313 */           throw new FailedPredicateException(this, String.format("precpred(_ctx, %d)", new Object[] { Integer.valueOf(((PrecedencePredicateTransition)transition).precedence) }));
/*     */         }
/*     */         break;
/*     */       
/*     */       default:
/* 318 */         throw new UnsupportedOperationException("Unrecognized ATN transition type.");
/*     */     } 
/*     */     
/* 321 */     setState(transition.target.stateNumber); }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected int visitDecisionState(DecisionState p) {
/* 329 */     int predictedAlt = 1;
/* 330 */     if (p.getNumberOfTransitions() > 1) {
/* 331 */       getErrorHandler().sync(this);
/* 332 */       int decision = p.decision;
/* 333 */       if (decision == this.overrideDecision && this._input.index() == this.overrideDecisionInputIndex && !this.overrideDecisionReached) {
/*     */ 
/*     */         
/* 336 */         predictedAlt = this.overrideDecisionAlt;
/* 337 */         this.overrideDecisionReached = true;
/*     */       } else {
/*     */         
/* 340 */         predictedAlt = getInterpreter().adaptivePredict(this._input, decision, this._ctx);
/*     */       } 
/*     */     } 
/* 343 */     return predictedAlt;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected InterpreterRuleContext createInterpreterRuleContext(ParserRuleContext parent, int invokingStateNumber, int ruleIndex) {
/* 354 */     return new InterpreterRuleContext(parent, invokingStateNumber, ruleIndex);
/*     */   }
/*     */   
/*     */   protected void visitRuleStopState(ATNState p) {
/* 358 */     RuleStartState ruleStartState = this.atn.ruleToStartState[p.ruleIndex];
/* 359 */     if (ruleStartState.isLeftRecursiveRule) {
/* 360 */       Pair<ParserRuleContext, Integer> parentContext = this._parentContextStack.pop();
/* 361 */       unrollRecursionContexts((ParserRuleContext)parentContext.a);
/* 362 */       setState(((Integer)parentContext.b).intValue());
/*     */     } else {
/*     */       
/* 365 */       exitRule();
/*     */     } 
/*     */     
/* 368 */     RuleTransition ruleTransition = (RuleTransition)((ATNState)this.atn.states.get(getState())).transition(0);
/* 369 */     setState(ruleTransition.followState.stateNumber);
/*     */   }
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
/*     */   public void addDecisionOverride(int decision, int tokenIndex, int forcedAlt) {
/* 413 */     this.overrideDecision = decision;
/* 414 */     this.overrideDecisionInputIndex = tokenIndex;
/* 415 */     this.overrideDecisionAlt = forcedAlt;
/*     */   }
/*     */   
/*     */   public InterpreterRuleContext getOverrideDecisionRoot() {
/* 419 */     return this.overrideDecisionRoot;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void recover(RecognitionException e) {
/* 427 */     int i = this._input.index();
/* 428 */     getErrorHandler().recover(this, e);
/* 429 */     if (this._input.index() == i)
/*     */     {
/* 431 */       if (e instanceof InputMismatchException) {
/* 432 */         InputMismatchException ime = (InputMismatchException)e;
/* 433 */         Token tok = e.getOffendingToken();
/* 434 */         int expectedTokenType = ime.getExpectedTokens().getMinElement();
/* 435 */         Token errToken = (Token)getTokenFactory().create(new Pair<TokenSource, CharStream>(tok.getTokenSource(), tok.getTokenSource().getInputStream()), expectedTokenType, tok.getText(), 0, -1, -1, tok.getLine(), tok.getCharPositionInLine());
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 441 */         this._ctx.addErrorNode(errToken);
/*     */       } else {
/*     */         
/* 444 */         Token tok = e.getOffendingToken();
/* 445 */         Token errToken = (Token)getTokenFactory().create(new Pair<TokenSource, CharStream>(tok.getTokenSource(), tok.getTokenSource().getInputStream()), 0, tok.getText(), 0, -1, -1, tok.getLine(), tok.getCharPositionInLine());
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 451 */         this._ctx.addErrorNode(errToken);
/*     */       } 
/*     */     }
/*     */   }
/*     */   
/*     */   protected Token recoverInline() {
/* 457 */     return this._errHandler.recoverInline(this);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public InterpreterRuleContext getRootContext() {
/* 469 */     return this.rootContext;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\antlr\v4\runtime\ParserInterpreter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */