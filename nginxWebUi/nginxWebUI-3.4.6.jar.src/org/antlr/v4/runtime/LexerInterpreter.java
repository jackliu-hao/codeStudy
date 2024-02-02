/*     */ package org.antlr.v4.runtime;
/*     */ 
/*     */ import java.util.Collection;
/*     */ import org.antlr.v4.runtime.atn.ATN;
/*     */ import org.antlr.v4.runtime.atn.ATNType;
/*     */ import org.antlr.v4.runtime.atn.LexerATNSimulator;
/*     */ import org.antlr.v4.runtime.atn.PredictionContextCache;
/*     */ import org.antlr.v4.runtime.dfa.DFA;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class LexerInterpreter
/*     */   extends Lexer
/*     */ {
/*     */   protected final String grammarFileName;
/*     */   protected final ATN atn;
/*     */   @Deprecated
/*     */   protected final String[] tokenNames;
/*     */   protected final String[] ruleNames;
/*     */   protected final String[] modeNames;
/*     */   private final Vocabulary vocabulary;
/*     */   protected final DFA[] _decisionToDFA;
/*  54 */   protected final PredictionContextCache _sharedContextCache = new PredictionContextCache();
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public LexerInterpreter(String grammarFileName, Collection<String> tokenNames, Collection<String> ruleNames, Collection<String> modeNames, ATN atn, CharStream input) {
/*  59 */     this(grammarFileName, VocabularyImpl.fromTokenNames(tokenNames.<String>toArray(new String[tokenNames.size()])), ruleNames, modeNames, atn, input);
/*     */   }
/*     */   
/*     */   public LexerInterpreter(String grammarFileName, Vocabulary vocabulary, Collection<String> ruleNames, Collection<String> modeNames, ATN atn, CharStream input) {
/*  63 */     super(input);
/*     */     
/*  65 */     if (atn.grammarType != ATNType.LEXER) {
/*  66 */       throw new IllegalArgumentException("The ATN must be a lexer ATN.");
/*     */     }
/*     */     
/*  69 */     this.grammarFileName = grammarFileName;
/*  70 */     this.atn = atn;
/*  71 */     this.tokenNames = new String[atn.maxTokenType]; int i;
/*  72 */     for (i = 0; i < this.tokenNames.length; i++) {
/*  73 */       this.tokenNames[i] = vocabulary.getDisplayName(i);
/*     */     }
/*     */     
/*  76 */     this.ruleNames = ruleNames.<String>toArray(new String[ruleNames.size()]);
/*  77 */     this.modeNames = modeNames.<String>toArray(new String[modeNames.size()]);
/*  78 */     this.vocabulary = vocabulary;
/*     */     
/*  80 */     this._decisionToDFA = new DFA[atn.getNumberOfDecisions()];
/*  81 */     for (i = 0; i < this._decisionToDFA.length; i++) {
/*  82 */       this._decisionToDFA[i] = new DFA(atn.getDecisionState(i), i);
/*     */     }
/*  84 */     this._interp = new LexerATNSimulator(this, atn, this._decisionToDFA, this._sharedContextCache);
/*     */   }
/*     */ 
/*     */   
/*     */   public ATN getATN() {
/*  89 */     return this.atn;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getGrammarFileName() {
/*  94 */     return this.grammarFileName;
/*     */   }
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public String[] getTokenNames() {
/* 100 */     return this.tokenNames;
/*     */   }
/*     */ 
/*     */   
/*     */   public String[] getRuleNames() {
/* 105 */     return this.ruleNames;
/*     */   }
/*     */ 
/*     */   
/*     */   public String[] getModeNames() {
/* 110 */     return this.modeNames;
/*     */   }
/*     */ 
/*     */   
/*     */   public Vocabulary getVocabulary() {
/* 115 */     if (this.vocabulary != null) {
/* 116 */       return this.vocabulary;
/*     */     }
/*     */     
/* 119 */     return super.getVocabulary();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\antlr\v4\runtime\LexerInterpreter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */