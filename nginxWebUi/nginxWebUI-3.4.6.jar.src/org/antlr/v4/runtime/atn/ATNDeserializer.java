/*     */ package org.antlr.v4.runtime.atn;
/*     */ 
/*     */ import java.io.InvalidClassException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.Locale;
/*     */ import java.util.UUID;
/*     */ import org.antlr.v4.runtime.misc.IntervalSet;
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
/*     */ public class ATNDeserializer
/*     */ {
/*  53 */   public static final int SERIALIZED_VERSION = 3;
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
/*  85 */   private static final UUID BASE_SERIALIZED_UUID = UUID.fromString("33761B2D-78BB-4A43-8B0B-4F5BEE8AACF3");
/*  86 */   private static final UUID ADDED_PRECEDENCE_TRANSITIONS = UUID.fromString("1DA0C57D-6C06-438A-9B27-10BCB3CE0F61");
/*  87 */   private static final UUID ADDED_LEXER_ACTIONS = UUID.fromString("AADB8D7E-AEEF-4415-AD2B-8204D6CF042E");
/*     */   
/*  89 */   private static final List<UUID> SUPPORTED_UUIDS = new ArrayList<UUID>(); static {
/*  90 */     SUPPORTED_UUIDS.add(BASE_SERIALIZED_UUID);
/*  91 */     SUPPORTED_UUIDS.add(ADDED_PRECEDENCE_TRANSITIONS);
/*  92 */     SUPPORTED_UUIDS.add(ADDED_LEXER_ACTIONS);
/*     */   }
/*  94 */   public static final UUID SERIALIZED_UUID = ADDED_LEXER_ACTIONS;
/*     */ 
/*     */   
/*     */   private final ATNDeserializationOptions deserializationOptions;
/*     */ 
/*     */   
/*     */   public ATNDeserializer() {
/* 101 */     this(ATNDeserializationOptions.getDefaultOptions());
/*     */   }
/*     */   
/*     */   public ATNDeserializer(ATNDeserializationOptions deserializationOptions) {
/* 105 */     if (deserializationOptions == null) {
/* 106 */       deserializationOptions = ATNDeserializationOptions.getDefaultOptions();
/*     */     }
/*     */     
/* 109 */     this.deserializationOptions = deserializationOptions;
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
/*     */   protected boolean isFeatureSupported(UUID feature, UUID actualUuid) {
/* 126 */     int featureIndex = SUPPORTED_UUIDS.indexOf(feature);
/* 127 */     if (featureIndex < 0) {
/* 128 */       return false;
/*     */     }
/*     */     
/* 131 */     return (SUPPORTED_UUIDS.indexOf(actualUuid) >= featureIndex);
/*     */   }
/*     */ 
/*     */   
/*     */   public ATN deserialize(char[] data) {
/* 136 */     data = (char[])data.clone();
/*     */     
/* 138 */     for (int i = 1; i < data.length; i++) {
/* 139 */       data[i] = (char)(data[i] - 2);
/*     */     }
/*     */     
/* 142 */     int p = 0;
/* 143 */     int version = toInt(data[p++]);
/* 144 */     if (version != SERIALIZED_VERSION) {
/* 145 */       String reason = String.format(Locale.getDefault(), "Could not deserialize ATN with version %d (expected %d).", new Object[] { Integer.valueOf(version), Integer.valueOf(SERIALIZED_VERSION) });
/* 146 */       throw new UnsupportedOperationException(new InvalidClassException(ATN.class.getName(), reason));
/*     */     } 
/*     */     
/* 149 */     UUID uuid = toUUID(data, p);
/* 150 */     p += 8;
/* 151 */     if (!SUPPORTED_UUIDS.contains(uuid)) {
/* 152 */       String reason = String.format(Locale.getDefault(), "Could not deserialize ATN with UUID %s (expected %s or a legacy UUID).", new Object[] { uuid, SERIALIZED_UUID });
/* 153 */       throw new UnsupportedOperationException(new InvalidClassException(ATN.class.getName(), reason));
/*     */     } 
/*     */     
/* 156 */     boolean supportsPrecedencePredicates = isFeatureSupported(ADDED_PRECEDENCE_TRANSITIONS, uuid);
/* 157 */     boolean supportsLexerActions = isFeatureSupported(ADDED_LEXER_ACTIONS, uuid);
/*     */     
/* 159 */     ATNType grammarType = ATNType.values()[toInt(data[p++])];
/* 160 */     int maxTokenType = toInt(data[p++]);
/* 161 */     ATN atn = new ATN(grammarType, maxTokenType);
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 166 */     List<Pair<LoopEndState, Integer>> loopBackStateNumbers = new ArrayList<Pair<LoopEndState, Integer>>();
/* 167 */     List<Pair<BlockStartState, Integer>> endStateNumbers = new ArrayList<Pair<BlockStartState, Integer>>();
/* 168 */     int nstates = toInt(data[p++]);
/* 169 */     for (int j = 0; j < nstates; j++) {
/* 170 */       int stype = toInt(data[p++]);
/*     */       
/* 172 */       if (stype == 0) {
/* 173 */         atn.addState(null);
/*     */       }
/*     */       else {
/*     */         
/* 177 */         int ruleIndex = toInt(data[p++]);
/* 178 */         if (ruleIndex == 65535) {
/* 179 */           ruleIndex = -1;
/*     */         }
/*     */         
/* 182 */         ATNState s = stateFactory(stype, ruleIndex);
/* 183 */         if (stype == 12) {
/* 184 */           int loopBackStateNumber = toInt(data[p++]);
/* 185 */           loopBackStateNumbers.add(new Pair<LoopEndState, Integer>((LoopEndState)s, Integer.valueOf(loopBackStateNumber)));
/*     */         }
/* 187 */         else if (s instanceof BlockStartState) {
/* 188 */           int endStateNumber = toInt(data[p++]);
/* 189 */           endStateNumbers.add(new Pair<BlockStartState, Integer>((BlockStartState)s, Integer.valueOf(endStateNumber)));
/*     */         } 
/* 191 */         atn.addState(s);
/*     */       } 
/*     */     } 
/*     */     
/* 195 */     for (Pair<LoopEndState, Integer> pair : loopBackStateNumbers) {
/* 196 */       ((LoopEndState)pair.a).loopBackState = atn.states.get(((Integer)pair.b).intValue());
/*     */     }
/*     */     
/* 199 */     for (Pair<BlockStartState, Integer> pair : endStateNumbers) {
/* 200 */       ((BlockStartState)pair.a).endState = (BlockEndState)atn.states.get(((Integer)pair.b).intValue());
/*     */     }
/*     */     
/* 203 */     int numNonGreedyStates = toInt(data[p++]);
/* 204 */     for (int k = 0; k < numNonGreedyStates; k++) {
/* 205 */       int stateNumber = toInt(data[p++]);
/* 206 */       ((DecisionState)atn.states.get(stateNumber)).nonGreedy = true;
/*     */     } 
/*     */     
/* 209 */     if (supportsPrecedencePredicates) {
/* 210 */       int numPrecedenceStates = toInt(data[p++]);
/* 211 */       for (int i4 = 0; i4 < numPrecedenceStates; i4++) {
/* 212 */         int stateNumber = toInt(data[p++]);
/* 213 */         ((RuleStartState)atn.states.get(stateNumber)).isLeftRecursiveRule = true;
/*     */       } 
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 220 */     int nrules = toInt(data[p++]);
/* 221 */     if (atn.grammarType == ATNType.LEXER) {
/* 222 */       atn.ruleToTokenType = new int[nrules];
/*     */     }
/*     */     
/* 225 */     atn.ruleToStartState = new RuleStartState[nrules];
/* 226 */     for (int m = 0; m < nrules; m++) {
/* 227 */       int s = toInt(data[p++]);
/* 228 */       RuleStartState startState = (RuleStartState)atn.states.get(s);
/* 229 */       atn.ruleToStartState[m] = startState;
/* 230 */       if (atn.grammarType == ATNType.LEXER) {
/* 231 */         int tokenType = toInt(data[p++]);
/* 232 */         if (tokenType == 65535) {
/* 233 */           tokenType = -1;
/*     */         }
/*     */         
/* 236 */         atn.ruleToTokenType[m] = tokenType;
/*     */         
/* 238 */         if (!isFeatureSupported(ADDED_LEXER_ACTIONS, uuid))
/*     */         {
/*     */           
/* 241 */           int actionIndexIgnored = toInt(data[p++]);
/*     */         }
/*     */       } 
/*     */     } 
/*     */     
/* 246 */     atn.ruleToStopState = new RuleStopState[nrules];
/* 247 */     for (ATNState state : atn.states) {
/* 248 */       if (!(state instanceof RuleStopState)) {
/*     */         continue;
/*     */       }
/*     */       
/* 252 */       RuleStopState stopState = (RuleStopState)state;
/* 253 */       atn.ruleToStopState[state.ruleIndex] = stopState;
/* 254 */       (atn.ruleToStartState[state.ruleIndex]).stopState = stopState;
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 260 */     int nmodes = toInt(data[p++]);
/* 261 */     for (int n = 0; n < nmodes; n++) {
/* 262 */       int s = toInt(data[p++]);
/* 263 */       atn.modeToStartState.add((TokensStartState)atn.states.get(s));
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 269 */     List<IntervalSet> sets = new ArrayList<IntervalSet>();
/* 270 */     int nsets = toInt(data[p++]);
/* 271 */     for (int i1 = 0; i1 < nsets; i1++) {
/* 272 */       int nintervals = toInt(data[p]);
/* 273 */       p++;
/* 274 */       IntervalSet set = new IntervalSet(new int[0]);
/* 275 */       sets.add(set);
/*     */       
/* 277 */       boolean containsEof = (toInt(data[p++]) != 0);
/* 278 */       if (containsEof) {
/* 279 */         set.add(-1);
/*     */       }
/*     */       
/* 282 */       for (int i4 = 0; i4 < nintervals; i4++) {
/* 283 */         set.add(toInt(data[p]), toInt(data[p + 1]));
/* 284 */         p += 2;
/*     */       } 
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 291 */     int nedges = toInt(data[p++]);
/* 292 */     for (int i2 = 0; i2 < nedges; i2++) {
/* 293 */       int src = toInt(data[p]);
/* 294 */       int trg = toInt(data[p + 1]);
/* 295 */       int ttype = toInt(data[p + 2]);
/* 296 */       int arg1 = toInt(data[p + 3]);
/* 297 */       int arg2 = toInt(data[p + 4]);
/* 298 */       int arg3 = toInt(data[p + 5]);
/* 299 */       Transition trans = edgeFactory(atn, ttype, src, trg, arg1, arg2, arg3, sets);
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 304 */       ATNState srcState = atn.states.get(src);
/* 305 */       srcState.addTransition(trans);
/* 306 */       p += 6;
/*     */     } 
/*     */ 
/*     */     
/* 310 */     for (ATNState state : atn.states) {
/* 311 */       for (int i4 = 0; i4 < state.getNumberOfTransitions(); i4++) {
/* 312 */         Transition t = state.transition(i4);
/* 313 */         if (t instanceof RuleTransition) {
/*     */ 
/*     */ 
/*     */           
/* 317 */           RuleTransition ruleTransition = (RuleTransition)t;
/* 318 */           int outermostPrecedenceReturn = -1;
/* 319 */           if ((atn.ruleToStartState[ruleTransition.target.ruleIndex]).isLeftRecursiveRule && 
/* 320 */             ruleTransition.precedence == 0) {
/* 321 */             outermostPrecedenceReturn = ruleTransition.target.ruleIndex;
/*     */           }
/*     */ 
/*     */           
/* 325 */           EpsilonTransition returnTransition = new EpsilonTransition(ruleTransition.followState, outermostPrecedenceReturn);
/* 326 */           atn.ruleToStopState[ruleTransition.target.ruleIndex].addTransition(returnTransition);
/*     */         } 
/*     */       } 
/*     */     } 
/* 330 */     for (ATNState state : atn.states) {
/* 331 */       if (state instanceof BlockStartState) {
/*     */         
/* 333 */         if (((BlockStartState)state).endState == null) {
/* 334 */           throw new IllegalStateException();
/*     */         }
/*     */ 
/*     */         
/* 338 */         if (((BlockStartState)state).endState.startState != null) {
/* 339 */           throw new IllegalStateException();
/*     */         }
/*     */         
/* 342 */         ((BlockStartState)state).endState.startState = (BlockStartState)state;
/*     */       } 
/*     */       
/* 345 */       if (state instanceof PlusLoopbackState) {
/* 346 */         PlusLoopbackState loopbackState = (PlusLoopbackState)state;
/* 347 */         for (int i4 = 0; i4 < loopbackState.getNumberOfTransitions(); i4++) {
/* 348 */           ATNState target = (loopbackState.transition(i4)).target;
/* 349 */           if (target instanceof PlusBlockStartState)
/* 350 */             ((PlusBlockStartState)target).loopBackState = loopbackState; 
/*     */         } 
/*     */         continue;
/*     */       } 
/* 354 */       if (state instanceof StarLoopbackState) {
/* 355 */         StarLoopbackState loopbackState = (StarLoopbackState)state;
/* 356 */         for (int i4 = 0; i4 < loopbackState.getNumberOfTransitions(); i4++) {
/* 357 */           ATNState target = (loopbackState.transition(i4)).target;
/* 358 */           if (target instanceof StarLoopEntryState) {
/* 359 */             ((StarLoopEntryState)target).loopBackState = loopbackState;
/*     */           }
/*     */         } 
/*     */       } 
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 368 */     int ndecisions = toInt(data[p++]); int i3;
/* 369 */     for (i3 = 1; i3 <= ndecisions; i3++) {
/* 370 */       int s = toInt(data[p++]);
/* 371 */       DecisionState decState = (DecisionState)atn.states.get(s);
/* 372 */       atn.decisionToState.add(decState);
/* 373 */       decState.decision = i3 - 1;
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 379 */     if (atn.grammarType == ATNType.LEXER) {
/* 380 */       if (supportsLexerActions) {
/* 381 */         atn.lexerActions = new LexerAction[toInt(data[p++])];
/* 382 */         for (i3 = 0; i3 < atn.lexerActions.length; i3++) {
/* 383 */           LexerActionType actionType = LexerActionType.values()[toInt(data[p++])];
/* 384 */           int data1 = toInt(data[p++]);
/* 385 */           if (data1 == 65535) {
/* 386 */             data1 = -1;
/*     */           }
/*     */           
/* 389 */           int data2 = toInt(data[p++]);
/* 390 */           if (data2 == 65535) {
/* 391 */             data2 = -1;
/*     */           }
/*     */           
/* 394 */           LexerAction lexerAction = lexerActionFactory(actionType, data1, data2);
/*     */           
/* 396 */           atn.lexerActions[i3] = lexerAction;
/*     */         
/*     */         }
/*     */       
/*     */       }
/*     */       else {
/*     */         
/* 403 */         List<LexerAction> legacyLexerActions = new ArrayList<LexerAction>();
/* 404 */         for (ATNState state : atn.states) {
/* 405 */           for (int i4 = 0; i4 < state.getNumberOfTransitions(); i4++) {
/* 406 */             Transition transition = state.transition(i4);
/* 407 */             if (transition instanceof ActionTransition) {
/*     */ 
/*     */ 
/*     */               
/* 411 */               int ruleIndex = ((ActionTransition)transition).ruleIndex;
/* 412 */               int actionIndex = ((ActionTransition)transition).actionIndex;
/* 413 */               LexerCustomAction lexerAction = new LexerCustomAction(ruleIndex, actionIndex);
/* 414 */               state.setTransition(i4, new ActionTransition(transition.target, ruleIndex, legacyLexerActions.size(), false));
/* 415 */               legacyLexerActions.add(lexerAction);
/*     */             } 
/*     */           } 
/*     */         } 
/* 419 */         atn.lexerActions = legacyLexerActions.<LexerAction>toArray(new LexerAction[legacyLexerActions.size()]);
/*     */       } 
/*     */     }
/*     */     
/* 423 */     markPrecedenceDecisions(atn);
/*     */     
/* 425 */     if (this.deserializationOptions.isVerifyATN()) {
/* 426 */       verifyATN(atn);
/*     */     }
/*     */     
/* 429 */     if (this.deserializationOptions.isGenerateRuleBypassTransitions() && atn.grammarType == ATNType.PARSER) {
/* 430 */       atn.ruleToTokenType = new int[atn.ruleToStartState.length];
/* 431 */       for (i3 = 0; i3 < atn.ruleToStartState.length; i3++) {
/* 432 */         atn.ruleToTokenType[i3] = atn.maxTokenType + i3 + 1;
/*     */       }
/*     */       
/* 435 */       for (i3 = 0; i3 < atn.ruleToStartState.length; i3++) {
/* 436 */         ATNState endState; BasicBlockStartState bypassStart = new BasicBlockStartState();
/* 437 */         bypassStart.ruleIndex = i3;
/* 438 */         atn.addState(bypassStart);
/*     */         
/* 440 */         BlockEndState bypassStop = new BlockEndState();
/* 441 */         bypassStop.ruleIndex = i3;
/* 442 */         atn.addState(bypassStop);
/*     */         
/* 444 */         bypassStart.endState = bypassStop;
/* 445 */         atn.defineDecisionState(bypassStart);
/*     */         
/* 447 */         bypassStop.startState = bypassStart;
/*     */ 
/*     */         
/* 450 */         Transition excludeTransition = null;
/* 451 */         if ((atn.ruleToStartState[i3]).isLeftRecursiveRule) {
/*     */           
/* 453 */           endState = null;
/* 454 */           for (ATNState state : atn.states) {
/* 455 */             if (state.ruleIndex != i3) {
/*     */               continue;
/*     */             }
/*     */             
/* 459 */             if (!(state instanceof StarLoopEntryState)) {
/*     */               continue;
/*     */             }
/*     */             
/* 463 */             ATNState maybeLoopEndState = (state.transition(state.getNumberOfTransitions() - 1)).target;
/* 464 */             if (!(maybeLoopEndState instanceof LoopEndState)) {
/*     */               continue;
/*     */             }
/*     */             
/* 468 */             if (maybeLoopEndState.epsilonOnlyTransitions && (maybeLoopEndState.transition(0)).target instanceof RuleStopState) {
/* 469 */               endState = state;
/*     */               
/*     */               break;
/*     */             } 
/*     */           } 
/* 474 */           if (endState == null) {
/* 475 */             throw new UnsupportedOperationException("Couldn't identify final state of the precedence rule prefix section.");
/*     */           }
/*     */           
/* 478 */           excludeTransition = ((StarLoopEntryState)endState).loopBackState.transition(0);
/*     */         } else {
/*     */           
/* 481 */           endState = atn.ruleToStopState[i3];
/*     */         } 
/*     */ 
/*     */         
/* 485 */         for (ATNState state : atn.states) {
/* 486 */           for (Transition transition : state.transitions) {
/* 487 */             if (transition == excludeTransition) {
/*     */               continue;
/*     */             }
/*     */             
/* 491 */             if (transition.target == endState) {
/* 492 */               transition.target = bypassStop;
/*     */             }
/*     */           } 
/*     */         } 
/*     */ 
/*     */         
/* 498 */         while (atn.ruleToStartState[i3].getNumberOfTransitions() > 0) {
/* 499 */           Transition transition = atn.ruleToStartState[i3].removeTransition(atn.ruleToStartState[i3].getNumberOfTransitions() - 1);
/* 500 */           bypassStart.addTransition(transition);
/*     */         } 
/*     */ 
/*     */         
/* 504 */         atn.ruleToStartState[i3].addTransition(new EpsilonTransition(bypassStart));
/* 505 */         bypassStop.addTransition(new EpsilonTransition(endState));
/*     */         
/* 507 */         ATNState matchState = new BasicState();
/* 508 */         atn.addState(matchState);
/* 509 */         matchState.addTransition(new AtomTransition(bypassStop, atn.ruleToTokenType[i3]));
/* 510 */         bypassStart.addTransition(new EpsilonTransition(matchState));
/*     */       } 
/*     */       
/* 513 */       if (this.deserializationOptions.isVerifyATN())
/*     */       {
/* 515 */         verifyATN(atn);
/*     */       }
/*     */     } 
/*     */     
/* 519 */     return atn;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void markPrecedenceDecisions(ATN atn) {
/* 530 */     for (ATNState state : atn.states) {
/* 531 */       if (!(state instanceof StarLoopEntryState)) {
/*     */         continue;
/*     */       }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 539 */       if ((atn.ruleToStartState[state.ruleIndex]).isLeftRecursiveRule) {
/* 540 */         ATNState maybeLoopEndState = (state.transition(state.getNumberOfTransitions() - 1)).target;
/* 541 */         if (maybeLoopEndState instanceof LoopEndState && 
/* 542 */           maybeLoopEndState.epsilonOnlyTransitions && (maybeLoopEndState.transition(0)).target instanceof RuleStopState) {
/* 543 */           ((StarLoopEntryState)state).isPrecedenceDecision = true;
/*     */         }
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected void verifyATN(ATN atn) {
/* 552 */     for (ATNState state : atn.states) {
/* 553 */       if (state == null) {
/*     */         continue;
/*     */       }
/*     */       
/* 557 */       checkCondition((state.onlyHasEpsilonTransitions() || state.getNumberOfTransitions() <= 1));
/*     */       
/* 559 */       if (state instanceof PlusBlockStartState) {
/* 560 */         checkCondition((((PlusBlockStartState)state).loopBackState != null));
/*     */       }
/*     */       
/* 563 */       if (state instanceof StarLoopEntryState) {
/* 564 */         StarLoopEntryState starLoopEntryState = (StarLoopEntryState)state;
/* 565 */         checkCondition((starLoopEntryState.loopBackState != null));
/* 566 */         checkCondition((starLoopEntryState.getNumberOfTransitions() == 2));
/*     */         
/* 568 */         if ((starLoopEntryState.transition(0)).target instanceof StarBlockStartState) {
/* 569 */           checkCondition((starLoopEntryState.transition(1)).target instanceof LoopEndState);
/* 570 */           checkCondition(!starLoopEntryState.nonGreedy);
/*     */         }
/* 572 */         else if ((starLoopEntryState.transition(0)).target instanceof LoopEndState) {
/* 573 */           checkCondition((starLoopEntryState.transition(1)).target instanceof StarBlockStartState);
/* 574 */           checkCondition(starLoopEntryState.nonGreedy);
/*     */         } else {
/*     */           
/* 577 */           throw new IllegalStateException();
/*     */         } 
/*     */       } 
/*     */       
/* 581 */       if (state instanceof StarLoopbackState) {
/* 582 */         checkCondition((state.getNumberOfTransitions() == 1));
/* 583 */         checkCondition((state.transition(0)).target instanceof StarLoopEntryState);
/*     */       } 
/*     */       
/* 586 */       if (state instanceof LoopEndState) {
/* 587 */         checkCondition((((LoopEndState)state).loopBackState != null));
/*     */       }
/*     */       
/* 590 */       if (state instanceof RuleStartState) {
/* 591 */         checkCondition((((RuleStartState)state).stopState != null));
/*     */       }
/*     */       
/* 594 */       if (state instanceof BlockStartState) {
/* 595 */         checkCondition((((BlockStartState)state).endState != null));
/*     */       }
/*     */       
/* 598 */       if (state instanceof BlockEndState) {
/* 599 */         checkCondition((((BlockEndState)state).startState != null));
/*     */       }
/*     */       
/* 602 */       if (state instanceof DecisionState) {
/* 603 */         DecisionState decisionState = (DecisionState)state;
/* 604 */         checkCondition((decisionState.getNumberOfTransitions() <= 1 || decisionState.decision >= 0));
/*     */         continue;
/*     */       } 
/* 607 */       checkCondition((state.getNumberOfTransitions() <= 1 || state instanceof RuleStopState));
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   protected void checkCondition(boolean condition) {
/* 613 */     checkCondition(condition, null);
/*     */   }
/*     */   
/*     */   protected void checkCondition(boolean condition, String message) {
/* 617 */     if (!condition) {
/* 618 */       throw new IllegalStateException(message);
/*     */     }
/*     */   }
/*     */   
/*     */   protected static int toInt(char c) {
/* 623 */     return c;
/*     */   }
/*     */   
/*     */   protected static int toInt32(char[] data, int offset) {
/* 627 */     return data[offset] | data[offset + 1] << 16;
/*     */   }
/*     */   
/*     */   protected static long toLong(char[] data, int offset) {
/* 631 */     long lowOrder = toInt32(data, offset) & 0xFFFFFFFFL;
/* 632 */     return lowOrder | toInt32(data, offset + 2) << 32L;
/*     */   }
/*     */   
/*     */   protected static UUID toUUID(char[] data, int offset) {
/* 636 */     long leastSigBits = toLong(data, offset);
/* 637 */     long mostSigBits = toLong(data, offset + 4);
/* 638 */     return new UUID(mostSigBits, leastSigBits);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected Transition edgeFactory(ATN atn, int type, int src, int trg, int arg1, int arg2, int arg3, List<IntervalSet> sets) {
/*     */     RuleTransition rt;
/*     */     PredicateTransition pt;
/*     */     ActionTransition a;
/* 647 */     ATNState target = atn.states.get(trg);
/* 648 */     switch (type) { case 1:
/* 649 */         return new EpsilonTransition(target);
/*     */       case 2:
/* 651 */         if (arg3 != 0) {
/* 652 */           return new RangeTransition(target, -1, arg2);
/*     */         }
/*     */         
/* 655 */         return new RangeTransition(target, arg1, arg2);
/*     */       
/*     */       case 3:
/* 658 */         rt = new RuleTransition((RuleStartState)atn.states.get(arg1), arg2, arg3, target);
/* 659 */         return rt;
/*     */       case 4:
/* 661 */         pt = new PredicateTransition(target, arg1, arg2, (arg3 != 0));
/* 662 */         return pt;
/*     */       case 10:
/* 664 */         return new PrecedencePredicateTransition(target, arg1);
/*     */       case 5:
/* 666 */         if (arg3 != 0) {
/* 667 */           return new AtomTransition(target, -1);
/*     */         }
/*     */         
/* 670 */         return new AtomTransition(target, arg1);
/*     */       
/*     */       case 6:
/* 673 */         a = new ActionTransition(target, arg1, arg2, (arg3 != 0));
/* 674 */         return a;
/* 675 */       case 7: return new SetTransition(target, sets.get(arg1));
/* 676 */       case 8: return new NotSetTransition(target, sets.get(arg1));
/* 677 */       case 9: return new WildcardTransition(target); }
/*     */ 
/*     */     
/* 680 */     throw new IllegalArgumentException("The specified transition type is not valid.");
/*     */   }
/*     */   
/*     */   protected ATNState stateFactory(int type, int ruleIndex) {
/*     */     ATNState s;
/* 685 */     switch (type) { case 0:
/* 686 */         return null;
/* 687 */       case 1: s = new BasicState();
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
/* 704 */         s.ruleIndex = ruleIndex;
/* 705 */         return s;case 2: s = new RuleStartState(); s.ruleIndex = ruleIndex; return s;case 3: s = new BasicBlockStartState(); s.ruleIndex = ruleIndex; return s;case 4: s = new PlusBlockStartState(); s.ruleIndex = ruleIndex; return s;case 5: s = new StarBlockStartState(); s.ruleIndex = ruleIndex; return s;case 6: s = new TokensStartState(); s.ruleIndex = ruleIndex; return s;case 7: s = new RuleStopState(); s.ruleIndex = ruleIndex; return s;case 8: s = new BlockEndState(); s.ruleIndex = ruleIndex; return s;case 9: s = new StarLoopbackState(); s.ruleIndex = ruleIndex; return s;case 10: s = new StarLoopEntryState(); s.ruleIndex = ruleIndex; return s;case 11: s = new PlusLoopbackState(); s.ruleIndex = ruleIndex; return s;case 12: s = new LoopEndState(); s.ruleIndex = ruleIndex; return s; }
/*     */     
/*     */     String message = String.format(Locale.getDefault(), "The specified state type %d is not valid.", new Object[] { Integer.valueOf(type) });
/*     */     throw new IllegalArgumentException(message); } protected LexerAction lexerActionFactory(LexerActionType type, int data1, int data2) {
/* 709 */     switch (type) {
/*     */       case CHANNEL:
/* 711 */         return new LexerChannelAction(data1);
/*     */       
/*     */       case CUSTOM:
/* 714 */         return new LexerCustomAction(data1, data2);
/*     */       
/*     */       case MODE:
/* 717 */         return new LexerModeAction(data1);
/*     */       
/*     */       case MORE:
/* 720 */         return LexerMoreAction.INSTANCE;
/*     */       
/*     */       case POP_MODE:
/* 723 */         return LexerPopModeAction.INSTANCE;
/*     */       
/*     */       case PUSH_MODE:
/* 726 */         return new LexerPushModeAction(data1);
/*     */       
/*     */       case SKIP:
/* 729 */         return LexerSkipAction.INSTANCE;
/*     */       
/*     */       case TYPE:
/* 732 */         return new LexerTypeAction(data1);
/*     */     } 
/*     */     
/* 735 */     String message = String.format(Locale.getDefault(), "The specified lexer action type %d is not valid.", new Object[] { type });
/* 736 */     throw new IllegalArgumentException(message);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\antlr\v4\runtime\atn\ATNDeserializer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */