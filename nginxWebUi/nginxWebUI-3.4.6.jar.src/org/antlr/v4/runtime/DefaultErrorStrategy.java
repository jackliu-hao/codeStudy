/*     */ package org.antlr.v4.runtime;
/*     */ 
/*     */ import org.antlr.v4.runtime.atn.ATN;
/*     */ import org.antlr.v4.runtime.atn.ATNState;
/*     */ import org.antlr.v4.runtime.atn.RuleTransition;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class DefaultErrorStrategy
/*     */   implements ANTLRErrorStrategy
/*     */ {
/*     */   protected boolean errorRecoveryMode = false;
/*  59 */   protected int lastErrorIndex = -1;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected IntervalSet lastErrorStates;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void reset(Parser recognizer) {
/*  71 */     endErrorCondition(recognizer);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void beginErrorCondition(Parser recognizer) {
/*  81 */     this.errorRecoveryMode = true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean inErrorRecoveryMode(Parser recognizer) {
/*  89 */     return this.errorRecoveryMode;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void endErrorCondition(Parser recognizer) {
/*  99 */     this.errorRecoveryMode = false;
/* 100 */     this.lastErrorStates = null;
/* 101 */     this.lastErrorIndex = -1;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void reportMatch(Parser recognizer) {
/* 111 */     endErrorCondition(recognizer);
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
/*     */   public void reportError(Parser recognizer, RecognitionException e) {
/* 139 */     if (inErrorRecoveryMode(recognizer)) {
/*     */       return;
/*     */     }
/*     */     
/* 143 */     beginErrorCondition(recognizer);
/* 144 */     if (e instanceof NoViableAltException) {
/* 145 */       reportNoViableAlternative(recognizer, (NoViableAltException)e);
/*     */     }
/* 147 */     else if (e instanceof InputMismatchException) {
/* 148 */       reportInputMismatch(recognizer, (InputMismatchException)e);
/*     */     }
/* 150 */     else if (e instanceof FailedPredicateException) {
/* 151 */       reportFailedPredicate(recognizer, (FailedPredicateException)e);
/*     */     } else {
/*     */       
/* 154 */       System.err.println("unknown recognition error type: " + e.getClass().getName());
/* 155 */       recognizer.notifyErrorListeners(e.getOffendingToken(), e.getMessage(), e);
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
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void recover(Parser recognizer, RecognitionException e) {
/* 173 */     if (this.lastErrorIndex == recognizer.getInputStream().index() && this.lastErrorStates != null && this.lastErrorStates.contains(recognizer.getState()))
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 183 */       recognizer.consume();
/*     */     }
/* 185 */     this.lastErrorIndex = recognizer.getInputStream().index();
/* 186 */     if (this.lastErrorStates == null) this.lastErrorStates = new IntervalSet(new int[0]); 
/* 187 */     this.lastErrorStates.add(recognizer.getState());
/* 188 */     IntervalSet followSet = getErrorRecoverySet(recognizer);
/* 189 */     consumeUntil(recognizer, followSet);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void sync(Parser recognizer) throws RecognitionException {
/*     */     IntervalSet expecting, whatFollowsLoopIterationOrRule;
/* 240 */     ATNState s = (recognizer.getInterpreter()).atn.states.get(recognizer.getState());
/*     */ 
/*     */     
/* 243 */     if (inErrorRecoveryMode(recognizer)) {
/*     */       return;
/*     */     }
/*     */     
/* 247 */     TokenStream tokens = recognizer.getInputStream();
/* 248 */     int la = tokens.LA(1);
/*     */ 
/*     */     
/* 251 */     if (recognizer.getATN().nextTokens(s).contains(la) || la == -1) {
/*     */       return;
/*     */     }
/* 254 */     if (recognizer.isExpectedToken(la)) {
/*     */       return;
/*     */     }
/*     */     
/* 258 */     switch (s.getStateType()) {
/*     */       
/*     */       case 3:
/*     */       case 4:
/*     */       case 5:
/*     */       case 10:
/* 264 */         if (singleTokenDeletion(recognizer) != null) {
/*     */           return;
/*     */         }
/*     */         
/* 268 */         throw new InputMismatchException(recognizer);
/*     */ 
/*     */       
/*     */       case 9:
/*     */       case 11:
/* 273 */         reportUnwantedToken(recognizer);
/* 274 */         expecting = recognizer.getExpectedTokens();
/* 275 */         whatFollowsLoopIterationOrRule = expecting.or(getErrorRecoverySet(recognizer));
/*     */         
/* 277 */         consumeUntil(recognizer, whatFollowsLoopIterationOrRule);
/*     */         break;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void reportNoViableAlternative(Parser recognizer, NoViableAltException e) {
/*     */     String input;
/* 298 */     TokenStream tokens = recognizer.getInputStream();
/*     */     
/* 300 */     if (tokens != null) {
/* 301 */       if (e.getStartToken().getType() == -1) { input = "<EOF>"; }
/* 302 */       else { input = tokens.getText(e.getStartToken(), e.getOffendingToken()); }
/*     */     
/*     */     } else {
/* 305 */       input = "<unknown input>";
/*     */     } 
/* 307 */     String msg = "no viable alternative at input " + escapeWSAndQuote(input);
/* 308 */     recognizer.notifyErrorListeners(e.getOffendingToken(), msg, e);
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
/*     */   protected void reportInputMismatch(Parser recognizer, InputMismatchException e) {
/* 323 */     String msg = "mismatched input " + getTokenErrorDisplay(e.getOffendingToken()) + " expecting " + e.getExpectedTokens().toString(recognizer.getVocabulary());
/*     */     
/* 325 */     recognizer.notifyErrorListeners(e.getOffendingToken(), msg, e);
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
/*     */   protected void reportFailedPredicate(Parser recognizer, FailedPredicateException e) {
/* 340 */     String ruleName = recognizer.getRuleNames()[recognizer._ctx.getRuleIndex()];
/* 341 */     String msg = "rule " + ruleName + " " + e.getMessage();
/* 342 */     recognizer.notifyErrorListeners(e.getOffendingToken(), msg, e);
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
/*     */   protected void reportUnwantedToken(Parser recognizer) {
/* 364 */     if (inErrorRecoveryMode(recognizer)) {
/*     */       return;
/*     */     }
/*     */     
/* 368 */     beginErrorCondition(recognizer);
/*     */     
/* 370 */     Token t = recognizer.getCurrentToken();
/* 371 */     String tokenName = getTokenErrorDisplay(t);
/* 372 */     IntervalSet expecting = getExpectedTokens(recognizer);
/* 373 */     String msg = "extraneous input " + tokenName + " expecting " + expecting.toString(recognizer.getVocabulary());
/*     */     
/* 375 */     recognizer.notifyErrorListeners(t, msg, (RecognitionException)null);
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
/*     */   protected void reportMissingToken(Parser recognizer) {
/* 396 */     if (inErrorRecoveryMode(recognizer)) {
/*     */       return;
/*     */     }
/*     */     
/* 400 */     beginErrorCondition(recognizer);
/*     */     
/* 402 */     Token t = recognizer.getCurrentToken();
/* 403 */     IntervalSet expecting = getExpectedTokens(recognizer);
/* 404 */     String msg = "missing " + expecting.toString(recognizer.getVocabulary()) + " at " + getTokenErrorDisplay(t);
/*     */ 
/*     */     
/* 407 */     recognizer.notifyErrorListeners(t, msg, (RecognitionException)null);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Token recoverInline(Parser recognizer) throws RecognitionException {
/* 465 */     Token matchedSymbol = singleTokenDeletion(recognizer);
/* 466 */     if (matchedSymbol != null) {
/*     */ 
/*     */       
/* 469 */       recognizer.consume();
/* 470 */       return matchedSymbol;
/*     */     } 
/*     */ 
/*     */     
/* 474 */     if (singleTokenInsertion(recognizer)) {
/* 475 */       return getMissingSymbol(recognizer);
/*     */     }
/*     */ 
/*     */     
/* 479 */     throw new InputMismatchException(recognizer);
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
/*     */   protected boolean singleTokenInsertion(Parser recognizer) {
/* 500 */     int currentSymbolType = recognizer.getInputStream().LA(1);
/*     */ 
/*     */ 
/*     */     
/* 504 */     ATNState currentState = (recognizer.getInterpreter()).atn.states.get(recognizer.getState());
/* 505 */     ATNState next = (currentState.transition(0)).target;
/* 506 */     ATN atn = (recognizer.getInterpreter()).atn;
/* 507 */     IntervalSet expectingAtLL2 = atn.nextTokens(next, recognizer._ctx);
/*     */     
/* 509 */     if (expectingAtLL2.contains(currentSymbolType)) {
/* 510 */       reportMissingToken(recognizer);
/* 511 */       return true;
/*     */     } 
/* 513 */     return false;
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
/*     */   protected Token singleTokenDeletion(Parser recognizer) {
/* 536 */     int nextTokenType = recognizer.getInputStream().LA(2);
/* 537 */     IntervalSet expecting = getExpectedTokens(recognizer);
/* 538 */     if (expecting.contains(nextTokenType)) {
/* 539 */       reportUnwantedToken(recognizer);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 546 */       recognizer.consume();
/*     */       
/* 548 */       Token matchedSymbol = recognizer.getCurrentToken();
/* 549 */       reportMatch(recognizer);
/* 550 */       return matchedSymbol;
/*     */     } 
/* 552 */     return null;
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
/*     */   protected Token getMissingSymbol(Parser recognizer) {
/*     */     String tokenText;
/* 575 */     Token currentSymbol = recognizer.getCurrentToken();
/* 576 */     IntervalSet expecting = getExpectedTokens(recognizer);
/* 577 */     int expectedTokenType = expecting.getMinElement();
/*     */     
/* 579 */     if (expectedTokenType == -1) { tokenText = "<missing EOF>"; }
/* 580 */     else { tokenText = "<missing " + recognizer.getVocabulary().getDisplayName(expectedTokenType) + ">"; }
/* 581 */      Token current = currentSymbol;
/* 582 */     Token lookback = recognizer.getInputStream().LT(-1);
/* 583 */     if (current.getType() == -1 && lookback != null) {
/* 584 */       current = lookback;
/*     */     }
/* 586 */     return (Token)recognizer.getTokenFactory().create(new Pair<TokenSource, CharStream>(current.getTokenSource(), current.getTokenSource().getInputStream()), expectedTokenType, tokenText, 0, -1, -1, current.getLine(), current.getCharPositionInLine());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected IntervalSet getExpectedTokens(Parser recognizer) {
/* 595 */     return recognizer.getExpectedTokens();
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
/*     */   protected String getTokenErrorDisplay(Token t) {
/* 607 */     if (t == null) return "<no token>"; 
/* 608 */     String s = getSymbolText(t);
/* 609 */     if (s == null) {
/* 610 */       if (getSymbolType(t) == -1) {
/* 611 */         s = "<EOF>";
/*     */       } else {
/*     */         
/* 614 */         s = "<" + getSymbolType(t) + ">";
/*     */       } 
/*     */     }
/* 617 */     return escapeWSAndQuote(s);
/*     */   }
/*     */   
/*     */   protected String getSymbolText(Token symbol) {
/* 621 */     return symbol.getText();
/*     */   }
/*     */   
/*     */   protected int getSymbolType(Token symbol) {
/* 625 */     return symbol.getType();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected String escapeWSAndQuote(String s) {
/* 631 */     s = s.replace("\n", "\\n");
/* 632 */     s = s.replace("\r", "\\r");
/* 633 */     s = s.replace("\t", "\\t");
/* 634 */     return "'" + s + "'";
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected IntervalSet getErrorRecoverySet(Parser recognizer) {
/* 730 */     ATN atn = (recognizer.getInterpreter()).atn;
/* 731 */     RuleContext ctx = recognizer._ctx;
/* 732 */     IntervalSet recoverSet = new IntervalSet(new int[0]);
/* 733 */     while (ctx != null && ctx.invokingState >= 0) {
/*     */       
/* 735 */       ATNState invokingState = atn.states.get(ctx.invokingState);
/* 736 */       RuleTransition rt = (RuleTransition)invokingState.transition(0);
/* 737 */       IntervalSet follow = atn.nextTokens(rt.followState);
/* 738 */       recoverSet.addAll(follow);
/* 739 */       ctx = ctx.parent;
/*     */     } 
/* 741 */     recoverSet.remove(-2);
/*     */     
/* 743 */     return recoverSet;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected void consumeUntil(Parser recognizer, IntervalSet set) {
/* 749 */     int ttype = recognizer.getInputStream().LA(1);
/* 750 */     while (ttype != -1 && !set.contains(ttype)) {
/*     */ 
/*     */       
/* 753 */       recognizer.consume();
/* 754 */       ttype = recognizer.getInputStream().LA(1);
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\antlr\v4\runtime\DefaultErrorStrategy.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */