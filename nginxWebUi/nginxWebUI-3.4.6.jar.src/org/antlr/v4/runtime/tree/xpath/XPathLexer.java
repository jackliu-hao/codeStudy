/*     */ package org.antlr.v4.runtime.tree.xpath;
/*     */ import org.antlr.v4.runtime.CharStream;
/*     */ import org.antlr.v4.runtime.RuleContext;
/*     */ import org.antlr.v4.runtime.RuntimeMetaData;
/*     */ import org.antlr.v4.runtime.Vocabulary;
/*     */ import org.antlr.v4.runtime.VocabularyImpl;
/*     */ import org.antlr.v4.runtime.atn.ATN;
/*     */ import org.antlr.v4.runtime.atn.LexerATNSimulator;
/*     */ import org.antlr.v4.runtime.atn.PredictionContextCache;
/*     */ import org.antlr.v4.runtime.dfa.DFA;
/*     */ 
/*     */ public class XPathLexer extends Lexer {
/*     */   static {
/*  14 */     RuntimeMetaData.checkVersion("4.5", "4.5.3");
/*     */   }
/*     */   protected static final DFA[] _decisionToDFA;
/*  17 */   protected static final PredictionContextCache _sharedContextCache = new PredictionContextCache();
/*     */   public static final int TOKEN_REF = 1;
/*     */   public static final int RULE_REF = 2;
/*     */   public static final int ANYWHERE = 3;
/*     */   public static final int ROOT = 4;
/*  22 */   public static String[] modeNames = new String[] { "DEFAULT_MODE" }; public static final int WILDCARD = 5;
/*     */   public static final int BANG = 6;
/*     */   public static final int ID = 7;
/*     */   public static final int STRING = 8;
/*  26 */   public static final String[] ruleNames = new String[] { "ANYWHERE", "ROOT", "WILDCARD", "BANG", "ID", "NameChar", "NameStartChar", "STRING" };
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  31 */   private static final String[] _LITERAL_NAMES = new String[] { null, null, null, "'//'", "'/'", "'*'", "'!'" };
/*     */ 
/*     */   
/*  34 */   private static final String[] _SYMBOLIC_NAMES = new String[] { null, "TOKEN_REF", "RULE_REF", "ANYWHERE", "ROOT", "WILDCARD", "BANG", "ID", "STRING" };
/*     */ 
/*     */ 
/*     */   
/*  38 */   public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*  46 */   public static final String[] tokenNames = new String[_SYMBOLIC_NAMES.length]; static { int i;
/*  47 */     for (i = 0; i < tokenNames.length; i++) {
/*  48 */       tokenNames[i] = VOCABULARY.getLiteralName(i);
/*  49 */       if (tokenNames[i] == null) {
/*  50 */         tokenNames[i] = VOCABULARY.getSymbolicName(i);
/*     */       }
/*     */       
/*  53 */       if (tokenNames[i] == null)
/*  54 */         tokenNames[i] = "<INVALID>"; 
/*     */     }  }
/*     */ 
/*     */   
/*     */   public static final String _serializedATN = "\003а훑舆괭䐗껱趀ꫝ\002\n4\b\001\004\002\t\002\004\003\t\003\004\004\t\004\004\005\t\005\004\006\t\006\004\007\t\007\004\b\t\b\004\t\t\t\003\002\003\002\003\002\003\003\003\003\003\004\003\004\003\005\003\005\003\006\003\006\007\006\037\n\006\f\006\016\006\"\013\006\003\006\003\006\003\007\003\007\005\007(\n\007\003\b\003\b\003\t\003\t\007\t.\n\t\f\t\016\t1\013\t\003\t\003\t\003/\002\n\003\005\005\006\007\007\t\b\013\t\r\002\017\002\021\n\003\002\004\007\0022;aa¹¹̂ͱ⁁⁂\017\002C\\c|ÂØÚøú́ͲͿ΁ ‎‏⁲↑Ⰲ⿱〃?車﷑ﷲ￿4\002\003\003\002\002\002\002\005\003\002\002\002\002\007\003\002\002\002\002\t\003\002\002\002\002\013\003\002\002\002\002\021\003\002\002\002\003\023\003\002\002\002\005\026\003\002\002\002\007\030\003\002\002\002\t\032\003\002\002\002\013\034\003\002\002\002\r'\003\002\002\002\017)\003\002\002\002\021+\003\002\002\002\023\024\0071\002\002\024\025\0071\002\002\025\004\003\002\002\002\026\027\0071\002\002\027\006\003\002\002\002\030\031\007,\002\002\031\b\003\002\002\002\032\033\007#\002\002\033\n\003\002\002\002\034 \005\017\b\002\035\037\005\r\007\002\036\035\003\002\002\002\037\"\003\002\002\002 \036\003\002\002\002 !\003\002\002\002!#\003\002\002\002\" \003\002\002\002#$\b\006\002\002$\f\003\002\002\002%(\005\017\b\002&(\t\002\002\002'%\003\002\002\002'&\003\002\002\002(\016\003\002\002\002)*\t\003\002\002*\020\003\002\002\002+/\007)\002\002,.\013\002\002\002-,\003\002\002\002.1\003\002\002\002/0\003\002\002\002/-\003\002\002\00202\003\002\002\0021/\003\002\002\00223\007)\002\0023\022\003\002\002\002\006\002 '/\003\003\006\002";
/*     */   
/*     */   @Deprecated
/*     */   public String[] getTokenNames() {
/*  62 */     return tokenNames;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Vocabulary getVocabulary() {
/*  68 */     return VOCABULARY;
/*     */   }
/*     */ 
/*     */   
/*     */   public XPathLexer(CharStream input) {
/*  73 */     super(input);
/*  74 */     this._interp = new LexerATNSimulator(this, _ATN, _decisionToDFA, _sharedContextCache);
/*     */   }
/*     */   
/*     */   public String getGrammarFileName() {
/*  78 */     return "XPathLexer.g4";
/*     */   }
/*     */   public String[] getRuleNames() {
/*  81 */     return ruleNames;
/*     */   }
/*     */   public String getSerializedATN() {
/*  84 */     return "\003а훑舆괭䐗껱趀ꫝ\002\n4\b\001\004\002\t\002\004\003\t\003\004\004\t\004\004\005\t\005\004\006\t\006\004\007\t\007\004\b\t\b\004\t\t\t\003\002\003\002\003\002\003\003\003\003\003\004\003\004\003\005\003\005\003\006\003\006\007\006\037\n\006\f\006\016\006\"\013\006\003\006\003\006\003\007\003\007\005\007(\n\007\003\b\003\b\003\t\003\t\007\t.\n\t\f\t\016\t1\013\t\003\t\003\t\003/\002\n\003\005\005\006\007\007\t\b\013\t\r\002\017\002\021\n\003\002\004\007\0022;aa¹¹̂ͱ⁁⁂\017\002C\\c|ÂØÚøú́ͲͿ΁ ‎‏⁲↑Ⰲ⿱〃?車﷑ﷲ￿4\002\003\003\002\002\002\002\005\003\002\002\002\002\007\003\002\002\002\002\t\003\002\002\002\002\013\003\002\002\002\002\021\003\002\002\002\003\023\003\002\002\002\005\026\003\002\002\002\007\030\003\002\002\002\t\032\003\002\002\002\013\034\003\002\002\002\r'\003\002\002\002\017)\003\002\002\002\021+\003\002\002\002\023\024\0071\002\002\024\025\0071\002\002\025\004\003\002\002\002\026\027\0071\002\002\027\006\003\002\002\002\030\031\007,\002\002\031\b\003\002\002\002\032\033\007#\002\002\033\n\003\002\002\002\034 \005\017\b\002\035\037\005\r\007\002\036\035\003\002\002\002\037\"\003\002\002\002 \036\003\002\002\002 !\003\002\002\002!#\003\002\002\002\" \003\002\002\002#$\b\006\002\002$\f\003\002\002\002%(\005\017\b\002&(\t\002\002\002'%\003\002\002\002'&\003\002\002\002(\016\003\002\002\002)*\t\003\002\002*\020\003\002\002\002+/\007)\002\002,.\013\002\002\002-,\003\002\002\002.1\003\002\002\002/0\003\002\002\002/-\003\002\002\00202\003\002\002\0021/\003\002\002\00223\007)\002\0023\022\003\002\002\002\006\002 '/\003\003\006\002";
/*     */   }
/*     */   public String[] getModeNames() {
/*  87 */     return modeNames;
/*     */   }
/*     */   public ATN getATN() {
/*  90 */     return _ATN;
/*     */   }
/*     */   
/*     */   public void action(RuleContext _localctx, int ruleIndex, int actionIndex) {
/*  94 */     switch (ruleIndex) {
/*     */       case 4:
/*  96 */         ID_action(_localctx, actionIndex);
/*     */         break;
/*     */     } 
/*     */   } private void ID_action(RuleContext _localctx, int actionIndex) {
/*     */     String text;
/* 101 */     switch (actionIndex) {
/*     */       
/*     */       case 0:
/* 104 */         text = getText();
/* 105 */         if (Character.isUpperCase(text.charAt(0))) { setType(1); break; }
/* 106 */          setType(2);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 130 */   public static final ATN _ATN = (new ATNDeserializer()).deserialize("\003а훑舆괭䐗껱趀ꫝ\002\n4\b\001\004\002\t\002\004\003\t\003\004\004\t\004\004\005\t\005\004\006\t\006\004\007\t\007\004\b\t\b\004\t\t\t\003\002\003\002\003\002\003\003\003\003\003\004\003\004\003\005\003\005\003\006\003\006\007\006\037\n\006\f\006\016\006\"\013\006\003\006\003\006\003\007\003\007\005\007(\n\007\003\b\003\b\003\t\003\t\007\t.\n\t\f\t\016\t1\013\t\003\t\003\t\003/\002\n\003\005\005\006\007\007\t\b\013\t\r\002\017\002\021\n\003\002\004\007\0022;aa¹¹̂ͱ⁁⁂\017\002C\\c|ÂØÚøú́ͲͿ΁ ‎‏⁲↑Ⰲ⿱〃?車﷑ﷲ￿4\002\003\003\002\002\002\002\005\003\002\002\002\002\007\003\002\002\002\002\t\003\002\002\002\002\013\003\002\002\002\002\021\003\002\002\002\003\023\003\002\002\002\005\026\003\002\002\002\007\030\003\002\002\002\t\032\003\002\002\002\013\034\003\002\002\002\r'\003\002\002\002\017)\003\002\002\002\021+\003\002\002\002\023\024\0071\002\002\024\025\0071\002\002\025\004\003\002\002\002\026\027\0071\002\002\027\006\003\002\002\002\030\031\007,\002\002\031\b\003\002\002\002\032\033\007#\002\002\033\n\003\002\002\002\034 \005\017\b\002\035\037\005\r\007\002\036\035\003\002\002\002\037\"\003\002\002\002 \036\003\002\002\002 !\003\002\002\002!#\003\002\002\002\" \003\002\002\002#$\b\006\002\002$\f\003\002\002\002%(\005\017\b\002&(\t\002\002\002'%\003\002\002\002'&\003\002\002\002(\016\003\002\002\002)*\t\003\002\002*\020\003\002\002\002+/\007)\002\002,.\013\002\002\002-,\003\002\002\002.1\003\002\002\002/0\003\002\002\002/-\003\002\002\00202\003\002\002\0021/\003\002\002\00223\007)\002\0023\022\003\002\002\002\006\002 '/\003\003\006\002".toCharArray());
/*     */   
/*     */   static {
/* 133 */     _decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
/* 134 */     for (i = 0; i < _ATN.getNumberOfDecisions(); i++)
/* 135 */       _decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i); 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\antlr\v4\runtime\tree\xpath\XPathLexer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */