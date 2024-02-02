/*     */ package org.antlr.v4.runtime;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.WeakHashMap;
/*     */ import org.antlr.v4.runtime.atn.ATN;
/*     */ import org.antlr.v4.runtime.atn.ATNDeserializationOptions;
/*     */ import org.antlr.v4.runtime.atn.ATNDeserializer;
/*     */ import org.antlr.v4.runtime.atn.ATNSimulator;
/*     */ import org.antlr.v4.runtime.atn.ATNState;
/*     */ import org.antlr.v4.runtime.atn.ParseInfo;
/*     */ import org.antlr.v4.runtime.atn.ParserATNSimulator;
/*     */ import org.antlr.v4.runtime.atn.PredictionMode;
/*     */ import org.antlr.v4.runtime.atn.ProfilingATNSimulator;
/*     */ import org.antlr.v4.runtime.atn.RuleTransition;
/*     */ import org.antlr.v4.runtime.dfa.DFA;
/*     */ import org.antlr.v4.runtime.misc.IntegerStack;
/*     */ import org.antlr.v4.runtime.misc.IntervalSet;
/*     */ import org.antlr.v4.runtime.tree.ErrorNode;
/*     */ import org.antlr.v4.runtime.tree.ParseTreeListener;
/*     */ import org.antlr.v4.runtime.tree.TerminalNode;
/*     */ import org.antlr.v4.runtime.tree.pattern.ParseTreePattern;
/*     */ import org.antlr.v4.runtime.tree.pattern.ParseTreePatternMatcher;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class Parser
/*     */   extends Recognizer<Token, ParserATNSimulator>
/*     */ {
/*     */   public class TraceListener
/*     */     implements ParseTreeListener
/*     */   {
/*     */     public void enterEveryRule(ParserRuleContext ctx) {
/*  63 */       System.out.println("enter   " + Parser.this.getRuleNames()[ctx.getRuleIndex()] + ", LT(1)=" + Parser.this._input.LT(1).getText());
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public void visitTerminal(TerminalNode node) {
/*  69 */       System.out.println("consume " + node.getSymbol() + " rule " + Parser.this.getRuleNames()[Parser.this._ctx.getRuleIndex()]);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public void visitErrorNode(ErrorNode node) {}
/*     */ 
/*     */ 
/*     */     
/*     */     public void exitEveryRule(ParserRuleContext ctx) {
/*  79 */       System.out.println("exit    " + Parser.this.getRuleNames()[ctx.getRuleIndex()] + ", LT(1)=" + Parser.this._input.LT(1).getText());
/*     */     }
/*     */   }
/*     */   
/*     */   public static class TrimToSizeListener
/*     */     implements ParseTreeListener {
/*  85 */     public static final TrimToSizeListener INSTANCE = new TrimToSizeListener();
/*     */ 
/*     */     
/*     */     public void enterEveryRule(ParserRuleContext ctx) {}
/*     */ 
/*     */     
/*     */     public void visitTerminal(TerminalNode node) {}
/*     */ 
/*     */     
/*     */     public void visitErrorNode(ErrorNode node) {}
/*     */ 
/*     */     
/*     */     public void exitEveryRule(ParserRuleContext ctx) {
/*  98 */       if (ctx.children instanceof ArrayList) {
/*  99 */         ((ArrayList)ctx.children).trimToSize();
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
/* 110 */   private static final Map<String, ATN> bypassAltsAtnCache = new WeakHashMap<String, ATN>();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 121 */   protected ANTLRErrorStrategy _errHandler = new DefaultErrorStrategy();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected TokenStream _input;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 133 */   protected final IntegerStack _precedenceStack = new IntegerStack(); protected ParserRuleContext _ctx; protected boolean _buildParseTrees; private TraceListener _tracer; public Parser(TokenStream input) {
/* 134 */     this._precedenceStack.push(0);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 150 */     this._buildParseTrees = true;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 180 */     setInputStream(input);
/*     */   }
/*     */   protected List<ParseTreeListener> _parseListeners; protected int _syntaxErrors; protected boolean matchedEOF;
/*     */   
/*     */   public void reset() {
/* 185 */     if (getInputStream() != null) getInputStream().seek(0); 
/* 186 */     this._errHandler.reset(this);
/* 187 */     this._ctx = null;
/* 188 */     this._syntaxErrors = 0;
/* 189 */     this.matchedEOF = false;
/* 190 */     setTrace(false);
/* 191 */     this._precedenceStack.clear();
/* 192 */     this._precedenceStack.push(0);
/* 193 */     ATNSimulator interpreter = getInterpreter();
/* 194 */     if (interpreter != null) {
/* 195 */       interpreter.reset();
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
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Token match(int ttype) throws RecognitionException {
/* 218 */     Token t = getCurrentToken();
/* 219 */     if (t.getType() == ttype) {
/* 220 */       if (ttype == -1) {
/* 221 */         this.matchedEOF = true;
/*     */       }
/* 223 */       this._errHandler.reportMatch(this);
/* 224 */       consume();
/*     */     } else {
/*     */       
/* 227 */       t = this._errHandler.recoverInline(this);
/* 228 */       if (this._buildParseTrees && t.getTokenIndex() == -1)
/*     */       {
/*     */         
/* 231 */         this._ctx.addErrorNode(t);
/*     */       }
/*     */     } 
/* 234 */     return t;
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
/*     */   public Token matchWildcard() throws RecognitionException {
/* 255 */     Token t = getCurrentToken();
/* 256 */     if (t.getType() > 0) {
/* 257 */       this._errHandler.reportMatch(this);
/* 258 */       consume();
/*     */     } else {
/*     */       
/* 261 */       t = this._errHandler.recoverInline(this);
/* 262 */       if (this._buildParseTrees && t.getTokenIndex() == -1)
/*     */       {
/*     */         
/* 265 */         this._ctx.addErrorNode(t);
/*     */       }
/*     */     } 
/*     */     
/* 269 */     return t;
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
/*     */   public void setBuildParseTree(boolean buildParseTrees) {
/* 288 */     this._buildParseTrees = buildParseTrees;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean getBuildParseTree() {
/* 299 */     return this._buildParseTrees;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setTrimParseTree(boolean trimParseTrees) {
/* 310 */     if (trimParseTrees) {
/* 311 */       if (getTrimParseTree())
/* 312 */         return;  addParseListener(TrimToSizeListener.INSTANCE);
/*     */     } else {
/*     */       
/* 315 */       removeParseListener(TrimToSizeListener.INSTANCE);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean getTrimParseTree() {
/* 324 */     return getParseListeners().contains(TrimToSizeListener.INSTANCE);
/*     */   }
/*     */ 
/*     */   
/*     */   public List<ParseTreeListener> getParseListeners() {
/* 329 */     List<ParseTreeListener> listeners = this._parseListeners;
/* 330 */     if (listeners == null) {
/* 331 */       return Collections.emptyList();
/*     */     }
/*     */     
/* 334 */     return listeners;
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
/*     */   public void addParseListener(ParseTreeListener listener) {
/* 367 */     if (listener == null) {
/* 368 */       throw new NullPointerException("listener");
/*     */     }
/*     */     
/* 371 */     if (this._parseListeners == null) {
/* 372 */       this._parseListeners = new ArrayList<ParseTreeListener>();
/*     */     }
/*     */     
/* 375 */     this._parseListeners.add(listener);
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
/*     */   public void removeParseListener(ParseTreeListener listener) {
/* 389 */     if (this._parseListeners != null && 
/* 390 */       this._parseListeners.remove(listener) && 
/* 391 */       this._parseListeners.isEmpty()) {
/* 392 */       this._parseListeners = null;
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
/*     */   public void removeParseListeners() {
/* 404 */     this._parseListeners = null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void triggerEnterRuleEvent() {
/* 413 */     for (ParseTreeListener listener : this._parseListeners) {
/* 414 */       listener.enterEveryRule(this._ctx);
/* 415 */       this._ctx.enterRule(listener);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void triggerExitRuleEvent() {
/* 426 */     for (int i = this._parseListeners.size() - 1; i >= 0; i--) {
/* 427 */       ParseTreeListener listener = this._parseListeners.get(i);
/* 428 */       this._ctx.exitRule(listener);
/* 429 */       listener.exitEveryRule(this._ctx);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getNumberOfSyntaxErrors() {
/* 440 */     return this._syntaxErrors;
/*     */   }
/*     */ 
/*     */   
/*     */   public TokenFactory<?> getTokenFactory() {
/* 445 */     return this._input.getTokenSource().getTokenFactory();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void setTokenFactory(TokenFactory<?> factory) {
/* 451 */     this._input.getTokenSource().setTokenFactory(factory);
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
/*     */   public ATN getATNWithBypassAlts() {
/* 463 */     String serializedAtn = getSerializedATN();
/* 464 */     if (serializedAtn == null) {
/* 465 */       throw new UnsupportedOperationException("The current parser does not support an ATN with bypass alternatives.");
/*     */     }
/*     */     
/* 468 */     synchronized (bypassAltsAtnCache) {
/* 469 */       ATN result = bypassAltsAtnCache.get(serializedAtn);
/* 470 */       if (result == null) {
/* 471 */         ATNDeserializationOptions deserializationOptions = new ATNDeserializationOptions();
/* 472 */         deserializationOptions.setGenerateRuleBypassTransitions(true);
/* 473 */         result = (new ATNDeserializer(deserializationOptions)).deserialize(serializedAtn.toCharArray());
/* 474 */         bypassAltsAtnCache.put(serializedAtn, result);
/*     */       } 
/*     */       
/* 477 */       return result;
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
/*     */   public ParseTreePattern compileParseTreePattern(String pattern, int patternRuleIndex) {
/* 493 */     if (getTokenStream() != null) {
/* 494 */       TokenSource tokenSource = getTokenStream().getTokenSource();
/* 495 */       if (tokenSource instanceof Lexer) {
/* 496 */         Lexer lexer = (Lexer)tokenSource;
/* 497 */         return compileParseTreePattern(pattern, patternRuleIndex, lexer);
/*     */       } 
/*     */     } 
/* 500 */     throw new UnsupportedOperationException("Parser can't discover a lexer to use");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ParseTreePattern compileParseTreePattern(String pattern, int patternRuleIndex, Lexer lexer) {
/* 510 */     ParseTreePatternMatcher m = new ParseTreePatternMatcher(lexer, this);
/* 511 */     return m.compile(pattern, patternRuleIndex);
/*     */   }
/*     */ 
/*     */   
/*     */   public ANTLRErrorStrategy getErrorHandler() {
/* 516 */     return this._errHandler;
/*     */   }
/*     */   
/*     */   public void setErrorHandler(ANTLRErrorStrategy handler) {
/* 520 */     this._errHandler = handler;
/*     */   }
/*     */   
/*     */   public TokenStream getInputStream() {
/* 524 */     return getTokenStream();
/*     */   }
/*     */   
/*     */   public final void setInputStream(IntStream input) {
/* 528 */     setTokenStream((TokenStream)input);
/*     */   }
/*     */   
/*     */   public TokenStream getTokenStream() {
/* 532 */     return this._input;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setTokenStream(TokenStream input) {
/* 537 */     this._input = null;
/* 538 */     reset();
/* 539 */     this._input = input;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Token getCurrentToken() {
/* 547 */     return this._input.LT(1);
/*     */   }
/*     */   
/*     */   public final void notifyErrorListeners(String msg) {
/* 551 */     notifyErrorListeners(getCurrentToken(), msg, (RecognitionException)null);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void notifyErrorListeners(Token offendingToken, String msg, RecognitionException e) {
/* 557 */     this._syntaxErrors++;
/* 558 */     int line = -1;
/* 559 */     int charPositionInLine = -1;
/* 560 */     line = offendingToken.getLine();
/* 561 */     charPositionInLine = offendingToken.getCharPositionInLine();
/*     */     
/* 563 */     ANTLRErrorListener listener = getErrorListenerDispatch();
/* 564 */     listener.syntaxError(this, offendingToken, line, charPositionInLine, msg, e);
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
/*     */   public Token consume() {
/* 589 */     Token o = getCurrentToken();
/* 590 */     if (o.getType() != -1) {
/* 591 */       getInputStream().consume();
/*     */     }
/* 593 */     boolean hasListener = (this._parseListeners != null && !this._parseListeners.isEmpty());
/* 594 */     if (this._buildParseTrees || hasListener) {
/* 595 */       if (this._errHandler.inErrorRecoveryMode(this)) {
/* 596 */         ErrorNode node = this._ctx.addErrorNode(o);
/* 597 */         if (this._parseListeners != null) {
/* 598 */           for (ParseTreeListener listener : this._parseListeners) {
/* 599 */             listener.visitErrorNode(node);
/*     */           }
/*     */         }
/*     */       } else {
/*     */         
/* 604 */         TerminalNode node = this._ctx.addChild(o);
/* 605 */         if (this._parseListeners != null) {
/* 606 */           for (ParseTreeListener listener : this._parseListeners) {
/* 607 */             listener.visitTerminal(node);
/*     */           }
/*     */         }
/*     */       } 
/*     */     }
/* 612 */     return o;
/*     */   }
/*     */   
/*     */   protected void addContextToParseTree() {
/* 616 */     ParserRuleContext parent = (ParserRuleContext)this._ctx.parent;
/*     */     
/* 618 */     if (parent != null) {
/* 619 */       parent.addChild(this._ctx);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void enterRule(ParserRuleContext localctx, int state, int ruleIndex) {
/* 628 */     setState(state);
/* 629 */     this._ctx = localctx;
/* 630 */     this._ctx.start = this._input.LT(1);
/* 631 */     if (this._buildParseTrees) addContextToParseTree(); 
/* 632 */     if (this._parseListeners != null) triggerEnterRuleEvent(); 
/*     */   }
/*     */   
/*     */   public void exitRule() {
/* 636 */     if (this.matchedEOF) {
/*     */       
/* 638 */       this._ctx.stop = this._input.LT(1);
/*     */     } else {
/*     */       
/* 641 */       this._ctx.stop = this._input.LT(-1);
/*     */     } 
/*     */     
/* 644 */     if (this._parseListeners != null) triggerExitRuleEvent(); 
/* 645 */     setState(this._ctx.invokingState);
/* 646 */     this._ctx = (ParserRuleContext)this._ctx.parent;
/*     */   }
/*     */   
/*     */   public void enterOuterAlt(ParserRuleContext localctx, int altNum) {
/* 650 */     localctx.setAltNumber(altNum);
/*     */ 
/*     */     
/* 653 */     if (this._buildParseTrees && this._ctx != localctx) {
/* 654 */       ParserRuleContext parent = (ParserRuleContext)this._ctx.parent;
/* 655 */       if (parent != null) {
/* 656 */         parent.removeLastChild();
/* 657 */         parent.addChild(localctx);
/*     */       } 
/*     */     } 
/* 660 */     this._ctx = localctx;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final int getPrecedence() {
/* 670 */     if (this._precedenceStack.isEmpty()) {
/* 671 */       return -1;
/*     */     }
/*     */     
/* 674 */     return this._precedenceStack.peek();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public void enterRecursionRule(ParserRuleContext localctx, int ruleIndex) {
/* 683 */     enterRecursionRule(localctx, ((getATN()).ruleToStartState[ruleIndex]).stateNumber, ruleIndex, 0);
/*     */   }
/*     */   
/*     */   public void enterRecursionRule(ParserRuleContext localctx, int state, int ruleIndex, int precedence) {
/* 687 */     setState(state);
/* 688 */     this._precedenceStack.push(precedence);
/* 689 */     this._ctx = localctx;
/* 690 */     this._ctx.start = this._input.LT(1);
/* 691 */     if (this._parseListeners != null) {
/* 692 */       triggerEnterRuleEvent();
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void pushNewRecursionContext(ParserRuleContext localctx, int state, int ruleIndex) {
/* 700 */     ParserRuleContext previous = this._ctx;
/* 701 */     previous.parent = localctx;
/* 702 */     previous.invokingState = state;
/* 703 */     previous.stop = this._input.LT(-1);
/*     */     
/* 705 */     this._ctx = localctx;
/* 706 */     this._ctx.start = previous.start;
/* 707 */     if (this._buildParseTrees) {
/* 708 */       this._ctx.addChild(previous);
/*     */     }
/*     */     
/* 711 */     if (this._parseListeners != null) {
/* 712 */       triggerEnterRuleEvent();
/*     */     }
/*     */   }
/*     */   
/*     */   public void unrollRecursionContexts(ParserRuleContext _parentctx) {
/* 717 */     this._precedenceStack.pop();
/* 718 */     this._ctx.stop = this._input.LT(-1);
/* 719 */     ParserRuleContext retctx = this._ctx;
/*     */ 
/*     */     
/* 722 */     if (this._parseListeners != null) {
/* 723 */       while (this._ctx != _parentctx) {
/* 724 */         triggerExitRuleEvent();
/* 725 */         this._ctx = (ParserRuleContext)this._ctx.parent;
/*     */       } 
/*     */     } else {
/*     */       
/* 729 */       this._ctx = _parentctx;
/*     */     } 
/*     */ 
/*     */     
/* 733 */     retctx.parent = _parentctx;
/*     */     
/* 735 */     if (this._buildParseTrees && _parentctx != null)
/*     */     {
/* 737 */       _parentctx.addChild(retctx);
/*     */     }
/*     */   }
/*     */   
/*     */   public ParserRuleContext getInvokingContext(int ruleIndex) {
/* 742 */     ParserRuleContext p = this._ctx;
/* 743 */     while (p != null) {
/* 744 */       if (p.getRuleIndex() == ruleIndex) return p; 
/* 745 */       p = (ParserRuleContext)p.parent;
/*     */     } 
/* 747 */     return null;
/*     */   }
/*     */   
/*     */   public ParserRuleContext getContext() {
/* 751 */     return this._ctx;
/*     */   }
/*     */   
/*     */   public void setContext(ParserRuleContext ctx) {
/* 755 */     this._ctx = ctx;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean precpred(RuleContext localctx, int precedence) {
/* 760 */     return (precedence >= this._precedenceStack.peek());
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean inContext(String context) {
/* 765 */     return false;
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
/*     */   public boolean isExpectedToken(int symbol) {
/* 784 */     ATN atn = (getInterpreter()).atn;
/* 785 */     ParserRuleContext ctx = this._ctx;
/* 786 */     ATNState s = atn.states.get(getState());
/* 787 */     IntervalSet following = atn.nextTokens(s);
/* 788 */     if (following.contains(symbol)) {
/* 789 */       return true;
/*     */     }
/*     */     
/* 792 */     if (!following.contains(-2)) return false;
/*     */     
/* 794 */     while (ctx != null && ctx.invokingState >= 0 && following.contains(-2)) {
/* 795 */       ATNState invokingState = atn.states.get(ctx.invokingState);
/* 796 */       RuleTransition rt = (RuleTransition)invokingState.transition(0);
/* 797 */       following = atn.nextTokens(rt.followState);
/* 798 */       if (following.contains(symbol)) {
/* 799 */         return true;
/*     */       }
/*     */       
/* 802 */       ctx = (ParserRuleContext)ctx.parent;
/*     */     } 
/*     */     
/* 805 */     if (following.contains(-2) && symbol == -1) {
/* 806 */       return true;
/*     */     }
/*     */     
/* 809 */     return false;
/*     */   }
/*     */   
/*     */   public boolean isMatchedEOF() {
/* 813 */     return this.matchedEOF;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public IntervalSet getExpectedTokens() {
/* 824 */     return getATN().getExpectedTokens(getState(), getContext());
/*     */   }
/*     */ 
/*     */   
/*     */   public IntervalSet getExpectedTokensWithinCurrentRule() {
/* 829 */     ATN atn = (getInterpreter()).atn;
/* 830 */     ATNState s = atn.states.get(getState());
/* 831 */     return atn.nextTokens(s);
/*     */   }
/*     */ 
/*     */   
/*     */   public int getRuleIndex(String ruleName) {
/* 836 */     Integer ruleIndex = getRuleIndexMap().get(ruleName);
/* 837 */     if (ruleIndex != null) return ruleIndex.intValue(); 
/* 838 */     return -1;
/*     */   }
/*     */   public ParserRuleContext getRuleContext() {
/* 841 */     return this._ctx;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public List<String> getRuleInvocationStack() {
/* 851 */     return getRuleInvocationStack(this._ctx);
/*     */   }
/*     */   
/*     */   public List<String> getRuleInvocationStack(RuleContext p) {
/* 855 */     String[] ruleNames = getRuleNames();
/* 856 */     List<String> stack = new ArrayList<String>();
/* 857 */     while (p != null) {
/*     */       
/* 859 */       int ruleIndex = p.getRuleIndex();
/* 860 */       if (ruleIndex < 0) { stack.add("n/a"); }
/* 861 */       else { stack.add(ruleNames[ruleIndex]); }
/* 862 */        p = p.parent;
/*     */     } 
/* 864 */     return stack;
/*     */   }
/*     */ 
/*     */   
/*     */   public List<String> getDFAStrings() {
/* 869 */     synchronized (this._interp.decisionToDFA) {
/* 870 */       List<String> s = new ArrayList<String>();
/* 871 */       for (int d = 0; d < this._interp.decisionToDFA.length; d++) {
/* 872 */         DFA dfa = this._interp.decisionToDFA[d];
/* 873 */         s.add(dfa.toString(getVocabulary()));
/*     */       } 
/* 875 */       return s;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void dumpDFA() {
/* 881 */     synchronized (this._interp.decisionToDFA) {
/* 882 */       boolean seenOne = false;
/* 883 */       for (int d = 0; d < this._interp.decisionToDFA.length; d++) {
/* 884 */         DFA dfa = this._interp.decisionToDFA[d];
/* 885 */         if (!dfa.states.isEmpty()) {
/* 886 */           if (seenOne) System.out.println(); 
/* 887 */           System.out.println("Decision " + dfa.decision + ":");
/* 888 */           System.out.print(dfa.toString(getVocabulary()));
/* 889 */           seenOne = true;
/*     */         } 
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   public String getSourceName() {
/* 896 */     return this._input.getSourceName();
/*     */   }
/*     */ 
/*     */   
/*     */   public ParseInfo getParseInfo() {
/* 901 */     ParserATNSimulator interp = getInterpreter();
/* 902 */     if (interp instanceof ProfilingATNSimulator) {
/* 903 */       return new ParseInfo((ProfilingATNSimulator)interp);
/*     */     }
/* 905 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setProfile(boolean profile) {
/* 912 */     ParserATNSimulator interp = getInterpreter();
/* 913 */     PredictionMode saveMode = interp.getPredictionMode();
/* 914 */     if (profile) {
/* 915 */       if (!(interp instanceof ProfilingATNSimulator)) {
/* 916 */         setInterpreter(new ProfilingATNSimulator(this));
/*     */       }
/*     */     }
/* 919 */     else if (interp instanceof ProfilingATNSimulator) {
/* 920 */       ParserATNSimulator sim = new ParserATNSimulator(this, getATN(), interp.decisionToDFA, interp.getSharedContextCache());
/*     */       
/* 922 */       setInterpreter(sim);
/*     */     } 
/* 924 */     getInterpreter().setPredictionMode(saveMode);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setTrace(boolean trace) {
/* 931 */     if (!trace) {
/* 932 */       removeParseListener(this._tracer);
/* 933 */       this._tracer = null;
/*     */     } else {
/*     */       
/* 936 */       if (this._tracer != null) { removeParseListener(this._tracer); }
/* 937 */       else { this._tracer = new TraceListener(); }
/* 938 */        addParseListener(this._tracer);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isTrace() {
/* 949 */     return (this._tracer != null);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\antlr\v4\runtime\Parser.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */