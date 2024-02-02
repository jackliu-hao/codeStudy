/*     */ package org.antlr.v4.runtime.atn;
/*     */ 
/*     */ import org.antlr.v4.runtime.CharStream;
/*     */ import org.antlr.v4.runtime.Lexer;
/*     */ import org.antlr.v4.runtime.LexerNoViableAltException;
/*     */ import org.antlr.v4.runtime.dfa.DFA;
/*     */ import org.antlr.v4.runtime.dfa.DFAState;
/*     */ import org.antlr.v4.runtime.misc.Interval;
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
/*     */ 
/*     */ public class LexerATNSimulator
/*     */   extends ATNSimulator
/*     */ {
/*     */   public static final boolean debug = false;
/*     */   public static final boolean dfa_debug = false;
/*     */   public static final int MIN_DFA_EDGE = 0;
/*     */   public static final int MAX_DFA_EDGE = 127;
/*     */   protected final Lexer recog;
/*     */   
/*     */   protected static class SimState
/*     */   {
/*  68 */     protected int index = -1;
/*  69 */     protected int line = 0;
/*  70 */     protected int charPos = -1;
/*     */     protected DFAState dfaState;
/*     */     
/*     */     protected void reset() {
/*  74 */       this.index = -1;
/*  75 */       this.line = 0;
/*  76 */       this.charPos = -1;
/*  77 */       this.dfaState = null;
/*     */     }
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
/*  89 */   protected int startIndex = -1;
/*     */ 
/*     */   
/*  92 */   protected int line = 1;
/*     */ 
/*     */   
/*  95 */   protected int charPositionInLine = 0;
/*     */   
/*     */   public final DFA[] decisionToDFA;
/*     */   
/*  99 */   protected int mode = 0;
/*     */ 
/*     */ 
/*     */   
/* 103 */   protected final SimState prevAccept = new SimState();
/*     */   
/* 105 */   public static int match_calls = 0;
/*     */ 
/*     */ 
/*     */   
/*     */   public LexerATNSimulator(ATN atn, DFA[] decisionToDFA, PredictionContextCache sharedContextCache) {
/* 110 */     this((Lexer)null, atn, decisionToDFA, sharedContextCache);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public LexerATNSimulator(Lexer recog, ATN atn, DFA[] decisionToDFA, PredictionContextCache sharedContextCache) {
/* 117 */     super(atn, sharedContextCache);
/* 118 */     this.decisionToDFA = decisionToDFA;
/* 119 */     this.recog = recog;
/*     */   }
/*     */   
/*     */   public void copyState(LexerATNSimulator simulator) {
/* 123 */     this.charPositionInLine = simulator.charPositionInLine;
/* 124 */     this.line = simulator.line;
/* 125 */     this.mode = simulator.mode;
/* 126 */     this.startIndex = simulator.startIndex;
/*     */   }
/*     */   
/*     */   public int match(CharStream input, int mode) {
/* 130 */     match_calls++;
/* 131 */     this.mode = mode;
/* 132 */     int mark = input.mark();
/*     */     try {
/* 134 */       this.startIndex = input.index();
/* 135 */       this.prevAccept.reset();
/* 136 */       DFA dfa = this.decisionToDFA[mode];
/* 137 */       if (dfa.s0 == null) {
/* 138 */         return matchATN(input);
/*     */       }
/*     */       
/* 141 */       return execATN(input, dfa.s0);
/*     */     }
/*     */     finally {
/*     */       
/* 145 */       input.release(mark);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void reset() {
/* 151 */     this.prevAccept.reset();
/* 152 */     this.startIndex = -1;
/* 153 */     this.line = 1;
/* 154 */     this.charPositionInLine = 0;
/* 155 */     this.mode = 0;
/*     */   }
/*     */ 
/*     */   
/*     */   public void clearDFA() {
/* 160 */     for (int d = 0; d < this.decisionToDFA.length; d++) {
/* 161 */       this.decisionToDFA[d] = new DFA(this.atn.getDecisionState(d), d);
/*     */     }
/*     */   }
/*     */   
/*     */   protected int matchATN(CharStream input) {
/* 166 */     ATNState startState = this.atn.modeToStartState.get(this.mode);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 172 */     int old_mode = this.mode;
/*     */     
/* 174 */     ATNConfigSet s0_closure = computeStartState(input, startState);
/* 175 */     boolean suppressEdge = s0_closure.hasSemanticContext;
/* 176 */     s0_closure.hasSemanticContext = false;
/*     */     
/* 178 */     DFAState next = addDFAState(s0_closure);
/* 179 */     if (!suppressEdge) {
/* 180 */       (this.decisionToDFA[this.mode]).s0 = next;
/*     */     }
/*     */     
/* 183 */     int predict = execATN(input, next);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 189 */     return predict;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected int execATN(CharStream input, DFAState ds0) {
/* 198 */     if (ds0.isAcceptState)
/*     */     {
/* 200 */       captureSimState(this.prevAccept, input, ds0);
/*     */     }
/*     */     
/* 203 */     int t = input.LA(1);
/*     */     
/* 205 */     DFAState s = ds0;
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
/*     */     while (true) {
/* 229 */       DFAState target = getExistingTargetState(s, t);
/* 230 */       if (target == null) {
/* 231 */         target = computeTargetState(input, s, t);
/*     */       }
/*     */       
/* 234 */       if (target == ERROR) {
/*     */         break;
/*     */       }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 242 */       if (t != -1) {
/* 243 */         consume(input);
/*     */       }
/*     */       
/* 246 */       if (target.isAcceptState) {
/* 247 */         captureSimState(this.prevAccept, input, target);
/* 248 */         if (t == -1) {
/*     */           break;
/*     */         }
/*     */       } 
/*     */       
/* 253 */       t = input.LA(1);
/* 254 */       s = target;
/*     */     } 
/*     */     
/* 257 */     return failOrAccept(this.prevAccept, input, s.configs, t);
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
/*     */   protected DFAState getExistingTargetState(DFAState s, int t) {
/* 273 */     if (s.edges == null || t < 0 || t > 127) {
/* 274 */       return null;
/*     */     }
/*     */     
/* 277 */     DFAState target = s.edges[t - 0];
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 283 */     return target;
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
/*     */   protected DFAState computeTargetState(CharStream input, DFAState s, int t) {
/* 300 */     ATNConfigSet reach = new OrderedATNConfigSet();
/*     */ 
/*     */ 
/*     */     
/* 304 */     getReachableConfigSet(input, s.configs, reach, t);
/*     */     
/* 306 */     if (reach.isEmpty()) {
/* 307 */       if (!reach.hasSemanticContext)
/*     */       {
/*     */         
/* 310 */         addDFAEdge(s, t, ERROR);
/*     */       }
/*     */ 
/*     */       
/* 314 */       return ERROR;
/*     */     } 
/*     */ 
/*     */     
/* 318 */     return addDFAEdge(s, t, reach);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected int failOrAccept(SimState prevAccept, CharStream input, ATNConfigSet reach, int t) {
/* 324 */     if (prevAccept.dfaState != null) {
/* 325 */       LexerActionExecutor lexerActionExecutor = prevAccept.dfaState.lexerActionExecutor;
/* 326 */       accept(input, lexerActionExecutor, this.startIndex, prevAccept.index, prevAccept.line, prevAccept.charPos);
/*     */       
/* 328 */       return prevAccept.dfaState.prediction;
/*     */     } 
/*     */ 
/*     */     
/* 332 */     if (t == -1 && input.index() == this.startIndex) {
/* 333 */       return -1;
/*     */     }
/*     */     
/* 336 */     throw new LexerNoViableAltException(this.recog, input, this.startIndex, reach);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void getReachableConfigSet(CharStream input, ATNConfigSet closure, ATNConfigSet reach, int t) {
/* 347 */     int skipAlt = 0;
/* 348 */     for (ATNConfig c : closure) {
/* 349 */       boolean currentAltReachedAcceptState = (c.alt == skipAlt);
/* 350 */       if (currentAltReachedAcceptState && ((LexerATNConfig)c).hasPassedThroughNonGreedyDecision()) {
/*     */         continue;
/*     */       }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 358 */       int n = c.state.getNumberOfTransitions();
/* 359 */       for (int ti = 0; ti < n; ti++) {
/* 360 */         Transition trans = c.state.transition(ti);
/* 361 */         ATNState target = getReachableTarget(trans, t);
/* 362 */         if (target != null) {
/* 363 */           LexerActionExecutor lexerActionExecutor = ((LexerATNConfig)c).getLexerActionExecutor();
/* 364 */           if (lexerActionExecutor != null) {
/* 365 */             lexerActionExecutor = lexerActionExecutor.fixOffsetBeforeMatch(input.index() - this.startIndex);
/*     */           }
/*     */           
/* 368 */           boolean treatEofAsEpsilon = (t == -1);
/* 369 */           if (closure(input, new LexerATNConfig((LexerATNConfig)c, target, lexerActionExecutor), reach, currentAltReachedAcceptState, true, treatEofAsEpsilon)) {
/*     */ 
/*     */             
/* 372 */             skipAlt = c.alt;
/*     */             break;
/*     */           } 
/*     */         } 
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void accept(CharStream input, LexerActionExecutor lexerActionExecutor, int startIndex, int index, int line, int charPos) {
/* 388 */     input.seek(index);
/* 389 */     this.line = line;
/* 390 */     this.charPositionInLine = charPos;
/*     */     
/* 392 */     if (lexerActionExecutor != null && this.recog != null) {
/* 393 */       lexerActionExecutor.execute(this.recog, input, startIndex);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   protected ATNState getReachableTarget(Transition trans, int t) {
/* 399 */     if (trans.matches(t, 0, 65535)) {
/* 400 */       return trans.target;
/*     */     }
/*     */     
/* 403 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected ATNConfigSet computeStartState(CharStream input, ATNState p) {
/* 410 */     PredictionContext initialContext = PredictionContext.EMPTY;
/* 411 */     ATNConfigSet configs = new OrderedATNConfigSet();
/* 412 */     for (int i = 0; i < p.getNumberOfTransitions(); i++) {
/* 413 */       ATNState target = (p.transition(i)).target;
/* 414 */       LexerATNConfig c = new LexerATNConfig(target, i + 1, initialContext);
/* 415 */       closure(input, c, configs, false, false, false);
/*     */     } 
/* 417 */     return configs;
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
/*     */   protected boolean closure(CharStream input, LexerATNConfig config, ATNConfigSet configs, boolean currentAltReachedAcceptState, boolean speculative, boolean treatEofAsEpsilon) {
/* 435 */     if (config.state instanceof RuleStopState) {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 445 */       if (config.context == null || config.context.hasEmptyPath()) {
/* 446 */         if (config.context == null || config.context.isEmpty()) {
/* 447 */           configs.add(config);
/* 448 */           return true;
/*     */         } 
/*     */         
/* 451 */         configs.add(new LexerATNConfig(config, config.state, PredictionContext.EMPTY));
/* 452 */         currentAltReachedAcceptState = true;
/*     */       } 
/*     */ 
/*     */       
/* 456 */       if (config.context != null && !config.context.isEmpty()) {
/* 457 */         for (int j = 0; j < config.context.size(); j++) {
/* 458 */           if (config.context.getReturnState(j) != Integer.MAX_VALUE) {
/* 459 */             PredictionContext newContext = config.context.getParent(j);
/* 460 */             ATNState returnState = this.atn.states.get(config.context.getReturnState(j));
/* 461 */             LexerATNConfig c = new LexerATNConfig(config, returnState, newContext);
/* 462 */             currentAltReachedAcceptState = closure(input, c, configs, currentAltReachedAcceptState, speculative, treatEofAsEpsilon);
/*     */           } 
/*     */         } 
/*     */       }
/*     */       
/* 467 */       return currentAltReachedAcceptState;
/*     */     } 
/*     */ 
/*     */     
/* 471 */     if (!config.state.onlyHasEpsilonTransitions() && (
/* 472 */       !currentAltReachedAcceptState || !config.hasPassedThroughNonGreedyDecision())) {
/* 473 */       configs.add(config);
/*     */     }
/*     */ 
/*     */     
/* 477 */     ATNState p = config.state;
/* 478 */     for (int i = 0; i < p.getNumberOfTransitions(); i++) {
/* 479 */       Transition t = p.transition(i);
/* 480 */       LexerATNConfig c = getEpsilonTarget(input, config, t, configs, speculative, treatEofAsEpsilon);
/* 481 */       if (c != null) {
/* 482 */         currentAltReachedAcceptState = closure(input, c, configs, currentAltReachedAcceptState, speculative, treatEofAsEpsilon);
/*     */       }
/*     */     } 
/*     */     
/* 486 */     return currentAltReachedAcceptState;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected LexerATNConfig getEpsilonTarget(CharStream input, LexerATNConfig config, Transition t, ATNConfigSet configs, boolean speculative, boolean treatEofAsEpsilon) {
/*     */     RuleTransition ruleTransition;
/*     */     PredictionContext newContext;
/*     */     PredicateTransition pt;
/* 498 */     LexerATNConfig c = null;
/* 499 */     switch (t.getSerializationType()) {
/*     */       case 3:
/* 501 */         ruleTransition = (RuleTransition)t;
/* 502 */         newContext = SingletonPredictionContext.create(config.context, ruleTransition.followState.stateNumber);
/*     */         
/* 504 */         c = new LexerATNConfig(config, t.target, newContext);
/*     */         break;
/*     */       
/*     */       case 10:
/* 508 */         throw new UnsupportedOperationException("Precedence predicates are not supported in lexers.");
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
/*     */       case 4:
/* 529 */         pt = (PredicateTransition)t;
/*     */ 
/*     */ 
/*     */         
/* 533 */         configs.hasSemanticContext = true;
/* 534 */         if (evaluatePredicate(input, pt.ruleIndex, pt.predIndex, speculative)) {
/* 535 */           c = new LexerATNConfig(config, t.target);
/*     */         }
/*     */         break;
/*     */       
/*     */       case 6:
/* 540 */         if (config.context == null || config.context.hasEmptyPath()) {
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
/* 553 */           LexerActionExecutor lexerActionExecutor = LexerActionExecutor.append(config.getLexerActionExecutor(), this.atn.lexerActions[((ActionTransition)t).actionIndex]);
/* 554 */           c = new LexerATNConfig(config, t.target, lexerActionExecutor);
/*     */           
/*     */           break;
/*     */         } 
/*     */         
/* 559 */         c = new LexerATNConfig(config, t.target);
/*     */         break;
/*     */ 
/*     */       
/*     */       case 1:
/* 564 */         c = new LexerATNConfig(config, t.target);
/*     */         break;
/*     */       
/*     */       case 2:
/*     */       case 5:
/*     */       case 7:
/* 570 */         if (treatEofAsEpsilon && 
/* 571 */           t.matches(-1, 0, 65535)) {
/* 572 */           c = new LexerATNConfig(config, t.target);
/*     */         }
/*     */         break;
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 580 */     return c;
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
/*     */   protected boolean evaluatePredicate(CharStream input, int ruleIndex, int predIndex, boolean speculative) {
/* 606 */     if (this.recog == null) {
/* 607 */       return true;
/*     */     }
/*     */     
/* 610 */     if (!speculative) {
/* 611 */       return this.recog.sempred(null, ruleIndex, predIndex);
/*     */     }
/*     */     
/* 614 */     int savedCharPositionInLine = this.charPositionInLine;
/* 615 */     int savedLine = this.line;
/* 616 */     int index = input.index();
/* 617 */     int marker = input.mark();
/*     */     try {
/* 619 */       consume(input);
/* 620 */       return this.recog.sempred(null, ruleIndex, predIndex);
/*     */     } finally {
/*     */       
/* 623 */       this.charPositionInLine = savedCharPositionInLine;
/* 624 */       this.line = savedLine;
/* 625 */       input.seek(index);
/* 626 */       input.release(marker);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void captureSimState(SimState settings, CharStream input, DFAState dfaState) {
/* 634 */     settings.index = input.index();
/* 635 */     settings.line = this.line;
/* 636 */     settings.charPos = this.charPositionInLine;
/* 637 */     settings.dfaState = dfaState;
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
/*     */   protected DFAState addDFAEdge(DFAState from, int t, ATNConfigSet q) {
/* 656 */     boolean suppressEdge = q.hasSemanticContext;
/* 657 */     q.hasSemanticContext = false;
/*     */ 
/*     */     
/* 660 */     DFAState to = addDFAState(q);
/*     */     
/* 662 */     if (suppressEdge) {
/* 663 */       return to;
/*     */     }
/*     */     
/* 666 */     addDFAEdge(from, t, to);
/* 667 */     return to;
/*     */   }
/*     */   
/*     */   protected void addDFAEdge(DFAState p, int t, DFAState q) {
/* 671 */     if (t < 0 || t > 127) {
/*     */       return;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 680 */     synchronized (p) {
/* 681 */       if (p.edges == null)
/*     */       {
/* 683 */         p.edges = new DFAState[128];
/*     */       }
/* 685 */       p.edges[t - 0] = q;
/*     */     } 
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
/*     */   protected DFAState addDFAState(ATNConfigSet configs) {
/* 699 */     assert !configs.hasSemanticContext;
/*     */     
/* 701 */     DFAState proposed = new DFAState(configs);
/* 702 */     ATNConfig firstConfigWithRuleStopState = null;
/* 703 */     for (ATNConfig c : configs) {
/* 704 */       if (c.state instanceof RuleStopState) {
/* 705 */         firstConfigWithRuleStopState = c;
/*     */         
/*     */         break;
/*     */       } 
/*     */     } 
/* 710 */     if (firstConfigWithRuleStopState != null) {
/* 711 */       proposed.isAcceptState = true;
/* 712 */       proposed.lexerActionExecutor = ((LexerATNConfig)firstConfigWithRuleStopState).getLexerActionExecutor();
/* 713 */       proposed.prediction = this.atn.ruleToTokenType[firstConfigWithRuleStopState.state.ruleIndex];
/*     */     } 
/*     */     
/* 716 */     DFA dfa = this.decisionToDFA[this.mode];
/* 717 */     synchronized (dfa.states) {
/* 718 */       DFAState existing = dfa.states.get(proposed);
/* 719 */       if (existing != null) return existing;
/*     */       
/* 721 */       DFAState newState = proposed;
/*     */       
/* 723 */       newState.stateNumber = dfa.states.size();
/* 724 */       configs.setReadonly(true);
/* 725 */       newState.configs = configs;
/* 726 */       dfa.states.put(newState, newState);
/* 727 */       return newState;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public final DFA getDFA(int mode) {
/* 733 */     return this.decisionToDFA[mode];
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getText(CharStream input) {
/* 741 */     return input.getText(Interval.of(this.startIndex, input.index() - 1));
/*     */   }
/*     */   
/*     */   public int getLine() {
/* 745 */     return this.line;
/*     */   }
/*     */   
/*     */   public void setLine(int line) {
/* 749 */     this.line = line;
/*     */   }
/*     */   
/*     */   public int getCharPositionInLine() {
/* 753 */     return this.charPositionInLine;
/*     */   }
/*     */   
/*     */   public void setCharPositionInLine(int charPositionInLine) {
/* 757 */     this.charPositionInLine = charPositionInLine;
/*     */   }
/*     */   
/*     */   public void consume(CharStream input) {
/* 761 */     int curChar = input.LA(1);
/* 762 */     if (curChar == 10) {
/* 763 */       this.line++;
/* 764 */       this.charPositionInLine = 0;
/*     */     } else {
/* 766 */       this.charPositionInLine++;
/*     */     } 
/* 768 */     input.consume();
/*     */   }
/*     */ 
/*     */   
/*     */   public String getTokenName(int t) {
/* 773 */     if (t == -1) return "EOF";
/*     */     
/* 775 */     return "'" + (char)t + "'";
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\antlr\v4\runtime\atn\LexerATNSimulator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */