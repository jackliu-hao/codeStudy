/*     */ package com.github.odiszapc.nginxparser.antlr;
/*     */ import org.antlr.v4.runtime.CharStream;
/*     */ import org.antlr.v4.runtime.RuntimeMetaData;
/*     */ import org.antlr.v4.runtime.Vocabulary;
/*     */ import org.antlr.v4.runtime.VocabularyImpl;
/*     */ import org.antlr.v4.runtime.atn.ATN;
/*     */ import org.antlr.v4.runtime.atn.ATNDeserializer;
/*     */ import org.antlr.v4.runtime.atn.LexerATNSimulator;
/*     */ import org.antlr.v4.runtime.atn.PredictionContextCache;
/*     */ import org.antlr.v4.runtime.dfa.DFA;
/*     */ 
/*     */ public class NginxLexer extends Lexer {
/*     */   protected static final DFA[] _decisionToDFA;
/*     */   
/*     */   static {
/*  16 */     RuntimeMetaData.checkVersion("4.5.3", "4.5.3");
/*     */   }
/*     */   public static final int T__0 = 1; public static final int T__1 = 2; public static final int T__2 = 3; public static final int T__3 = 4; public static final int T__4 = 5;
/*  19 */   protected static final PredictionContextCache _sharedContextCache = new PredictionContextCache();
/*     */   public static final int T__5 = 6;
/*     */   public static final int T__6 = 7;
/*     */   public static final int T__7 = 8;
/*     */   public static final int T__8 = 9;
/*     */   public static final int T__9 = 10;
/*  25 */   public static String[] modeNames = new String[] { "DEFAULT_MODE" }; public static final int T__10 = 11; public static final int T__11 = 12;
/*     */   public static final int T__12 = 13;
/*     */   public static final int T__13 = 14;
/*     */   public static final int Value = 15;
/*  29 */   public static final String[] ruleNames = new String[] { "T__0", "T__1", "T__2", "T__3", "T__4", "T__5", "T__6", "T__7", "T__8", "T__9", "T__10", "T__11", "T__12", "T__13", "Value", "STR_EXT", "Comment", "REGEXP_PREFIXED", "QUOTED_STRING", "RegexpPrefix", "StringCharacters", "NON_ASCII", "EscapeSequence", "SINGLE_QUOTED", "WS" };
/*     */   public static final int STR_EXT = 16;
/*     */   public static final int Comment = 17;
/*     */   public static final int REGEXP_PREFIXED = 18;
/*     */   public static final int QUOTED_STRING = 19;
/*     */   public static final int SINGLE_QUOTED = 20;
/*     */   public static final int WS = 21;
/*  36 */   private static final String[] _LITERAL_NAMES = new String[] { null, "';'", "'{'", "'}'", "'if'", "'('", "')'", "'\\.'", "'^'", "'location'", "'rewrite'", "'last'", "'break'", "'redirect'", "'permanent'" };
/*     */ 
/*     */ 
/*     */   
/*  40 */   private static final String[] _SYMBOLIC_NAMES = new String[] { null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, "Value", "STR_EXT", "Comment", "REGEXP_PREFIXED", "QUOTED_STRING", "SINGLE_QUOTED", "WS" };
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  45 */   public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*  53 */   public static final String[] tokenNames = new String[_SYMBOLIC_NAMES.length]; public static final String _serializedATN = "\003а훑舆괭䐗껱趀ꫝ\002\027´\b\001\004\002\t\002\004\003\t\003\004\004\t\004\004\005\t\005\004\006\t\006\004\007\t\007\004\b\t\b\004\t\t\t\004\n\t\n\004\013\t\013\004\f\t\f\004\r\t\r\004\016\t\016\004\017\t\017\004\020\t\020\004\021\t\021\004\022\t\022\004\023\t\023\004\024\t\024\004\025\t\025\004\026\t\026\004\027\t\027\004\030\t\030\004\031\t\031\004\032\t\032\003\002\003\002\003\003\003\003\003\004\003\004\003\005\003\005\003\005\003\006\003\006\003\007\003\007\003\b\003\b\003\b\003\t\003\t\003\n\003\n\003\n\003\n\003\n\003\n\003\n\003\n\003\n\003\013\003\013\003\013\003\013\003\013\003\013\003\013\003\013\003\f\003\f\003\f\003\f\003\f\003\r\003\r\003\r\003\r\003\r\003\r\003\016\003\016\003\016\003\016\003\016\003\016\003\016\003\016\003\016\003\017\003\017\003\017\003\017\003\017\003\017\003\017\003\017\003\017\003\017\003\020\003\020\003\020\005\020z\n\020\003\021\003\021\006\021~\n\021\r\021\016\021\003\022\003\022\007\022\n\022\f\022\016\022\013\022\003\023\003\023\006\023\n\023\r\023\016\023\003\024\003\024\005\024\n\024\003\024\003\024\003\025\003\025\005\025\n\025\003\026\003\026\006\026\n\026\r\026\016\026\003\027\003\027\003\030\003\030\005\030£\n\030\003\031\003\031\007\031§\n\031\f\031\016\031ª\013\031\003\031\003\031\003\032\006\032¯\n\032\r\032\016\032°\003\032\003\032\002\002\033\003\003\005\004\007\005\t\006\013\007\r\b\017\t\021\n\023\013\025\f\027\r\031\016\033\017\035\020\037\021!\022#\023%\024'\025)\002+\002-\002/\0021\0263\027\003\002\013\013\002##%&((,<??Aac|~~\004\002\f\f\017\017\013\002##%&((*<??Aac|~~\003\002\003\002,,\004\002$$^^\n\002$$))^^ddhhppttvv\004\002))^^\005\002\013\f\017\017\"\"¼\002\003\003\002\002\002\002\005\003\002\002\002\002\007\003\002\002\002\002\t\003\002\002\002\002\013\003\002\002\002\002\r\003\002\002\002\002\017\003\002\002\002\002\021\003\002\002\002\002\023\003\002\002\002\002\025\003\002\002\002\002\027\003\002\002\002\002\031\003\002\002\002\002\033\003\002\002\002\002\035\003\002\002\002\002\037\003\002\002\002\002!\003\002\002\002\002#\003\002\002\002\002%\003\002\002\002\002'\003\002\002\002\0021\003\002\002\002\0023\003\002\002\002\0035\003\002\002\002\0057\003\002\002\002\0079\003\002\002\002\t;\003\002\002\002\013>\003\002\002\002\r@\003\002\002\002\017B\003\002\002\002\021E\003\002\002\002\023G\003\002\002\002\025P\003\002\002\002\027X\003\002\002\002\031]\003\002\002\002\033c\003\002\002\002\035l\003\002\002\002\037y\003\002\002\002!}\003\002\002\002#\003\002\002\002%\003\002\002\002'\003\002\002\002)\003\002\002\002+\003\002\002\002-\003\002\002\002/ \003\002\002\0021¤\003\002\002\0023®\003\002\002\00256\007=\002\0026\004\003\002\002\00278\007}\002\0028\006\003\002\002\0029:\007\002\002:\b\003\002\002\002;<\007k\002\002<=\007h\002\002=\n\003\002\002\002>?\007*\002\002?\f\003\002\002\002@A\007+\002\002A\016\003\002\002\002BC\007^\002\002CD\0070\002\002D\020\003\002\002\002EF\007`\002\002F\022\003\002\002\002GH\007n\002\002HI\007q\002\002IJ\007e\002\002JK\007c\002\002KL\007v\002\002LM\007k\002\002MN\007q\002\002NO\007p\002\002O\024\003\002\002\002PQ\007t\002\002QR\007g\002\002RS\007y\002\002ST\007t\002\002TU\007k\002\002UV\007v\002\002VW\007g\002\002W\026\003\002\002\002XY\007n\002\002YZ\007c\002\002Z[\007u\002\002[\\\007v\002\002\\\030\003\002\002\002]^\007d\002\002^_\007t\002\002_`\007g\002\002`a\007c\002\002ab\007m\002\002b\032\003\002\002\002cd\007t\002\002de\007g\002\002ef\007f\002\002fg\007k\002\002gh\007t\002\002hi\007g\002\002ij\007e\002\002jk\007v\002\002k\034\003\002\002\002lm\007r\002\002mn\007g\002\002no\007t\002\002op\007o\002\002pq\007c\002\002qr\007p\002\002rs\007g\002\002st\007p\002\002tu\007v\002\002u\036\003\002\002\002vz\005!\021\002wz\005'\024\002xz\0051\031\002yv\003\002\002\002yw\003\002\002\002yx\003\002\002\002z \003\002\002\002{~\t\002\002\002|~\005-\027\002}{\003\002\002\002}|\003\002\002\002~\003\002\002\002}\003\002\002\002\003\002\002\002\"\003\002\002\002\007%\002\002\n\003\002\002\003\002\002\002\003\002\002\002\003\002\002\002\003\002\002\002$\003\002\002\002\003\002\002\002\005)\025\002\t\004\002\002\003\002\002\002\003\002\002\002\003\002\002\002\003\002\002\002&\003\002\002\002\007$\002\002\005+\026\002\003\002\002\002\003\002\002\002\003\002\002\002\007$\002\002(\003\002\002\002\t\005\002\002\t\006\002\002\003\002\002\002\003\002\002\002*\003\002\002\002\n\007\002\002\005/\030\002\003\002\002\002\003\002\002\002\003\002\002\002\003\002\002\002\003\002\002\002,\003\002\002\002\004\001\002.\003\002\002\002 ¢\007^\002\002¡£\t\b\002\002¢¡\003\002\002\002¢£\003\002\002\002£0\003\002\002\002¤¨\007)\002\002¥§\n\t\002\002¦¥\003\002\002\002§ª\003\002\002\002¨¦\003\002\002\002¨©\003\002\002\002©«\003\002\002\002ª¨\003\002\002\002«¬\007)\002\002¬2\003\002\002\002­¯\t\n\002\002®­\003\002\002\002¯°\003\002\002\002°®\003\002\002\002°±\003\002\002\002±²\003\002\002\002²³\b\032\002\002³4\003\002\002\002\017\002y}¢¨°\003\b\002\002"; static { int i;
/*  54 */     for (i = 0; i < tokenNames.length; i++) {
/*  55 */       tokenNames[i] = VOCABULARY.getLiteralName(i);
/*  56 */       if (tokenNames[i] == null) {
/*  57 */         tokenNames[i] = VOCABULARY.getSymbolicName(i);
/*     */       }
/*     */       
/*  60 */       if (tokenNames[i] == null) {
/*  61 */         tokenNames[i] = "<INVALID>";
/*     */       }
/*     */     }  }
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public String[] getTokenNames() {
/*  69 */     return tokenNames;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Vocabulary getVocabulary() {
/*  75 */     return VOCABULARY;
/*     */   }
/*     */ 
/*     */   
/*     */   public NginxLexer(CharStream input) {
/*  80 */     super(input);
/*  81 */     this._interp = new LexerATNSimulator(this, _ATN, _decisionToDFA, _sharedContextCache);
/*     */   }
/*     */   
/*     */   public String getGrammarFileName() {
/*  85 */     return "Nginx.g4";
/*     */   }
/*     */   public String[] getRuleNames() {
/*  88 */     return ruleNames;
/*     */   }
/*     */   public String getSerializedATN() {
/*  91 */     return "\003а훑舆괭䐗껱趀ꫝ\002\027´\b\001\004\002\t\002\004\003\t\003\004\004\t\004\004\005\t\005\004\006\t\006\004\007\t\007\004\b\t\b\004\t\t\t\004\n\t\n\004\013\t\013\004\f\t\f\004\r\t\r\004\016\t\016\004\017\t\017\004\020\t\020\004\021\t\021\004\022\t\022\004\023\t\023\004\024\t\024\004\025\t\025\004\026\t\026\004\027\t\027\004\030\t\030\004\031\t\031\004\032\t\032\003\002\003\002\003\003\003\003\003\004\003\004\003\005\003\005\003\005\003\006\003\006\003\007\003\007\003\b\003\b\003\b\003\t\003\t\003\n\003\n\003\n\003\n\003\n\003\n\003\n\003\n\003\n\003\013\003\013\003\013\003\013\003\013\003\013\003\013\003\013\003\f\003\f\003\f\003\f\003\f\003\r\003\r\003\r\003\r\003\r\003\r\003\016\003\016\003\016\003\016\003\016\003\016\003\016\003\016\003\016\003\017\003\017\003\017\003\017\003\017\003\017\003\017\003\017\003\017\003\017\003\020\003\020\003\020\005\020z\n\020\003\021\003\021\006\021~\n\021\r\021\016\021\003\022\003\022\007\022\n\022\f\022\016\022\013\022\003\023\003\023\006\023\n\023\r\023\016\023\003\024\003\024\005\024\n\024\003\024\003\024\003\025\003\025\005\025\n\025\003\026\003\026\006\026\n\026\r\026\016\026\003\027\003\027\003\030\003\030\005\030£\n\030\003\031\003\031\007\031§\n\031\f\031\016\031ª\013\031\003\031\003\031\003\032\006\032¯\n\032\r\032\016\032°\003\032\003\032\002\002\033\003\003\005\004\007\005\t\006\013\007\r\b\017\t\021\n\023\013\025\f\027\r\031\016\033\017\035\020\037\021!\022#\023%\024'\025)\002+\002-\002/\0021\0263\027\003\002\013\013\002##%&((,<??Aac|~~\004\002\f\f\017\017\013\002##%&((*<??Aac|~~\003\002\003\002,,\004\002$$^^\n\002$$))^^ddhhppttvv\004\002))^^\005\002\013\f\017\017\"\"¼\002\003\003\002\002\002\002\005\003\002\002\002\002\007\003\002\002\002\002\t\003\002\002\002\002\013\003\002\002\002\002\r\003\002\002\002\002\017\003\002\002\002\002\021\003\002\002\002\002\023\003\002\002\002\002\025\003\002\002\002\002\027\003\002\002\002\002\031\003\002\002\002\002\033\003\002\002\002\002\035\003\002\002\002\002\037\003\002\002\002\002!\003\002\002\002\002#\003\002\002\002\002%\003\002\002\002\002'\003\002\002\002\0021\003\002\002\002\0023\003\002\002\002\0035\003\002\002\002\0057\003\002\002\002\0079\003\002\002\002\t;\003\002\002\002\013>\003\002\002\002\r@\003\002\002\002\017B\003\002\002\002\021E\003\002\002\002\023G\003\002\002\002\025P\003\002\002\002\027X\003\002\002\002\031]\003\002\002\002\033c\003\002\002\002\035l\003\002\002\002\037y\003\002\002\002!}\003\002\002\002#\003\002\002\002%\003\002\002\002'\003\002\002\002)\003\002\002\002+\003\002\002\002-\003\002\002\002/ \003\002\002\0021¤\003\002\002\0023®\003\002\002\00256\007=\002\0026\004\003\002\002\00278\007}\002\0028\006\003\002\002\0029:\007\002\002:\b\003\002\002\002;<\007k\002\002<=\007h\002\002=\n\003\002\002\002>?\007*\002\002?\f\003\002\002\002@A\007+\002\002A\016\003\002\002\002BC\007^\002\002CD\0070\002\002D\020\003\002\002\002EF\007`\002\002F\022\003\002\002\002GH\007n\002\002HI\007q\002\002IJ\007e\002\002JK\007c\002\002KL\007v\002\002LM\007k\002\002MN\007q\002\002NO\007p\002\002O\024\003\002\002\002PQ\007t\002\002QR\007g\002\002RS\007y\002\002ST\007t\002\002TU\007k\002\002UV\007v\002\002VW\007g\002\002W\026\003\002\002\002XY\007n\002\002YZ\007c\002\002Z[\007u\002\002[\\\007v\002\002\\\030\003\002\002\002]^\007d\002\002^_\007t\002\002_`\007g\002\002`a\007c\002\002ab\007m\002\002b\032\003\002\002\002cd\007t\002\002de\007g\002\002ef\007f\002\002fg\007k\002\002gh\007t\002\002hi\007g\002\002ij\007e\002\002jk\007v\002\002k\034\003\002\002\002lm\007r\002\002mn\007g\002\002no\007t\002\002op\007o\002\002pq\007c\002\002qr\007p\002\002rs\007g\002\002st\007p\002\002tu\007v\002\002u\036\003\002\002\002vz\005!\021\002wz\005'\024\002xz\0051\031\002yv\003\002\002\002yw\003\002\002\002yx\003\002\002\002z \003\002\002\002{~\t\002\002\002|~\005-\027\002}{\003\002\002\002}|\003\002\002\002~\003\002\002\002}\003\002\002\002\003\002\002\002\"\003\002\002\002\007%\002\002\n\003\002\002\003\002\002\002\003\002\002\002\003\002\002\002\003\002\002\002$\003\002\002\002\003\002\002\002\005)\025\002\t\004\002\002\003\002\002\002\003\002\002\002\003\002\002\002\003\002\002\002&\003\002\002\002\007$\002\002\005+\026\002\003\002\002\002\003\002\002\002\003\002\002\002\007$\002\002(\003\002\002\002\t\005\002\002\t\006\002\002\003\002\002\002\003\002\002\002*\003\002\002\002\n\007\002\002\005/\030\002\003\002\002\002\003\002\002\002\003\002\002\002\003\002\002\002\003\002\002\002,\003\002\002\002\004\001\002.\003\002\002\002 ¢\007^\002\002¡£\t\b\002\002¢¡\003\002\002\002¢£\003\002\002\002£0\003\002\002\002¤¨\007)\002\002¥§\n\t\002\002¦¥\003\002\002\002§ª\003\002\002\002¨¦\003\002\002\002¨©\003\002\002\002©«\003\002\002\002ª¨\003\002\002\002«¬\007)\002\002¬2\003\002\002\002­¯\t\n\002\002®­\003\002\002\002¯°\003\002\002\002°®\003\002\002\002°±\003\002\002\002±²\003\002\002\002²³\b\032\002\002³4\003\002\002\002\017\002y}¢¨°\003\b\002\002";
/*     */   }
/*     */   public String[] getModeNames() {
/*  94 */     return modeNames;
/*     */   }
/*     */   public ATN getATN() {
/*  97 */     return _ATN;
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
/* 156 */   public static final ATN _ATN = (new ATNDeserializer()).deserialize("\003а훑舆괭䐗껱趀ꫝ\002\027´\b\001\004\002\t\002\004\003\t\003\004\004\t\004\004\005\t\005\004\006\t\006\004\007\t\007\004\b\t\b\004\t\t\t\004\n\t\n\004\013\t\013\004\f\t\f\004\r\t\r\004\016\t\016\004\017\t\017\004\020\t\020\004\021\t\021\004\022\t\022\004\023\t\023\004\024\t\024\004\025\t\025\004\026\t\026\004\027\t\027\004\030\t\030\004\031\t\031\004\032\t\032\003\002\003\002\003\003\003\003\003\004\003\004\003\005\003\005\003\005\003\006\003\006\003\007\003\007\003\b\003\b\003\b\003\t\003\t\003\n\003\n\003\n\003\n\003\n\003\n\003\n\003\n\003\n\003\013\003\013\003\013\003\013\003\013\003\013\003\013\003\013\003\f\003\f\003\f\003\f\003\f\003\r\003\r\003\r\003\r\003\r\003\r\003\016\003\016\003\016\003\016\003\016\003\016\003\016\003\016\003\016\003\017\003\017\003\017\003\017\003\017\003\017\003\017\003\017\003\017\003\017\003\020\003\020\003\020\005\020z\n\020\003\021\003\021\006\021~\n\021\r\021\016\021\003\022\003\022\007\022\n\022\f\022\016\022\013\022\003\023\003\023\006\023\n\023\r\023\016\023\003\024\003\024\005\024\n\024\003\024\003\024\003\025\003\025\005\025\n\025\003\026\003\026\006\026\n\026\r\026\016\026\003\027\003\027\003\030\003\030\005\030£\n\030\003\031\003\031\007\031§\n\031\f\031\016\031ª\013\031\003\031\003\031\003\032\006\032¯\n\032\r\032\016\032°\003\032\003\032\002\002\033\003\003\005\004\007\005\t\006\013\007\r\b\017\t\021\n\023\013\025\f\027\r\031\016\033\017\035\020\037\021!\022#\023%\024'\025)\002+\002-\002/\0021\0263\027\003\002\013\013\002##%&((,<??Aac|~~\004\002\f\f\017\017\013\002##%&((*<??Aac|~~\003\002\003\002,,\004\002$$^^\n\002$$))^^ddhhppttvv\004\002))^^\005\002\013\f\017\017\"\"¼\002\003\003\002\002\002\002\005\003\002\002\002\002\007\003\002\002\002\002\t\003\002\002\002\002\013\003\002\002\002\002\r\003\002\002\002\002\017\003\002\002\002\002\021\003\002\002\002\002\023\003\002\002\002\002\025\003\002\002\002\002\027\003\002\002\002\002\031\003\002\002\002\002\033\003\002\002\002\002\035\003\002\002\002\002\037\003\002\002\002\002!\003\002\002\002\002#\003\002\002\002\002%\003\002\002\002\002'\003\002\002\002\0021\003\002\002\002\0023\003\002\002\002\0035\003\002\002\002\0057\003\002\002\002\0079\003\002\002\002\t;\003\002\002\002\013>\003\002\002\002\r@\003\002\002\002\017B\003\002\002\002\021E\003\002\002\002\023G\003\002\002\002\025P\003\002\002\002\027X\003\002\002\002\031]\003\002\002\002\033c\003\002\002\002\035l\003\002\002\002\037y\003\002\002\002!}\003\002\002\002#\003\002\002\002%\003\002\002\002'\003\002\002\002)\003\002\002\002+\003\002\002\002-\003\002\002\002/ \003\002\002\0021¤\003\002\002\0023®\003\002\002\00256\007=\002\0026\004\003\002\002\00278\007}\002\0028\006\003\002\002\0029:\007\002\002:\b\003\002\002\002;<\007k\002\002<=\007h\002\002=\n\003\002\002\002>?\007*\002\002?\f\003\002\002\002@A\007+\002\002A\016\003\002\002\002BC\007^\002\002CD\0070\002\002D\020\003\002\002\002EF\007`\002\002F\022\003\002\002\002GH\007n\002\002HI\007q\002\002IJ\007e\002\002JK\007c\002\002KL\007v\002\002LM\007k\002\002MN\007q\002\002NO\007p\002\002O\024\003\002\002\002PQ\007t\002\002QR\007g\002\002RS\007y\002\002ST\007t\002\002TU\007k\002\002UV\007v\002\002VW\007g\002\002W\026\003\002\002\002XY\007n\002\002YZ\007c\002\002Z[\007u\002\002[\\\007v\002\002\\\030\003\002\002\002]^\007d\002\002^_\007t\002\002_`\007g\002\002`a\007c\002\002ab\007m\002\002b\032\003\002\002\002cd\007t\002\002de\007g\002\002ef\007f\002\002fg\007k\002\002gh\007t\002\002hi\007g\002\002ij\007e\002\002jk\007v\002\002k\034\003\002\002\002lm\007r\002\002mn\007g\002\002no\007t\002\002op\007o\002\002pq\007c\002\002qr\007p\002\002rs\007g\002\002st\007p\002\002tu\007v\002\002u\036\003\002\002\002vz\005!\021\002wz\005'\024\002xz\0051\031\002yv\003\002\002\002yw\003\002\002\002yx\003\002\002\002z \003\002\002\002{~\t\002\002\002|~\005-\027\002}{\003\002\002\002}|\003\002\002\002~\003\002\002\002}\003\002\002\002\003\002\002\002\"\003\002\002\002\007%\002\002\n\003\002\002\003\002\002\002\003\002\002\002\003\002\002\002\003\002\002\002$\003\002\002\002\003\002\002\002\005)\025\002\t\004\002\002\003\002\002\002\003\002\002\002\003\002\002\002\003\002\002\002&\003\002\002\002\007$\002\002\005+\026\002\003\002\002\002\003\002\002\002\003\002\002\002\007$\002\002(\003\002\002\002\t\005\002\002\t\006\002\002\003\002\002\002\003\002\002\002*\003\002\002\002\n\007\002\002\005/\030\002\003\002\002\002\003\002\002\002\003\002\002\002\003\002\002\002\003\002\002\002,\003\002\002\002\004\001\002.\003\002\002\002 ¢\007^\002\002¡£\t\b\002\002¢¡\003\002\002\002¢£\003\002\002\002£0\003\002\002\002¤¨\007)\002\002¥§\n\t\002\002¦¥\003\002\002\002§ª\003\002\002\002¨¦\003\002\002\002¨©\003\002\002\002©«\003\002\002\002ª¨\003\002\002\002«¬\007)\002\002¬2\003\002\002\002­¯\t\n\002\002®­\003\002\002\002¯°\003\002\002\002°®\003\002\002\002°±\003\002\002\002±²\003\002\002\002²³\b\032\002\002³4\003\002\002\002\017\002y}¢¨°\003\b\002\002".toCharArray());
/*     */   
/*     */   static {
/* 159 */     _decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
/* 160 */     for (i = 0; i < _ATN.getNumberOfDecisions(); i++)
/* 161 */       _decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i); 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\github\odiszapc\nginxparser\antlr\NginxLexer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */