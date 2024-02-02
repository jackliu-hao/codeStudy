/*      */ package com.github.odiszapc.nginxparser.antlr;
/*      */ import com.github.odiszapc.nginxparser.NgxEntry;
/*      */ import com.github.odiszapc.nginxparser.NgxParam;
/*      */ import com.github.odiszapc.nginxparser.NgxToken;
/*      */ import java.util.ArrayList;
/*      */ import java.util.List;
/*      */ import org.antlr.v4.runtime.NoViableAltException;
/*      */ import org.antlr.v4.runtime.ParserRuleContext;
/*      */ import org.antlr.v4.runtime.RecognitionException;
/*      */ import org.antlr.v4.runtime.Token;
/*      */ import org.antlr.v4.runtime.tree.ParseTreeListener;
/*      */ import org.antlr.v4.runtime.tree.ParseTreeVisitor;
/*      */ import org.antlr.v4.runtime.tree.TerminalNode;
/*      */ 
/*      */ public class NginxParser extends Parser {
/*      */   static {
/*   17 */     RuntimeMetaData.checkVersion("4.5.3", "4.5.3");
/*      */   }
/*      */   protected static final DFA[] _decisionToDFA;
/*   20 */   protected static final PredictionContextCache _sharedContextCache = new PredictionContextCache(); public static final int T__0 = 1; public static final int T__1 = 2; public static final int T__2 = 3; public static final int T__3 = 4; public static final int T__4 = 5; public static final int T__5 = 6; public static final int T__6 = 7;
/*      */   public static final int T__7 = 8;
/*      */   public static final int T__8 = 9;
/*      */   public static final int T__9 = 10;
/*      */   public static final int T__10 = 11;
/*      */   public static final int T__11 = 12;
/*      */   public static final int T__12 = 13;
/*      */   public static final int T__13 = 14;
/*      */   public static final int Value = 15;
/*      */   public static final int STR_EXT = 16;
/*   30 */   public static final String[] ruleNames = new String[] { "config", "statement", "genericStatement", "regexHeaderStatement", "block", "genericBlockHeader", "if_statement", "if_body", "regexp", "locationBlockHeader", "rewriteStatement" }; public static final int Comment = 17; public static final int REGEXP_PREFIXED = 18; public static final int QUOTED_STRING = 19; public static final int SINGLE_QUOTED = 20; public static final int WS = 21; public static final int RULE_config = 0; public static final int RULE_statement = 1; public static final int RULE_genericStatement = 2; public static final int RULE_regexHeaderStatement = 3; public static final int RULE_block = 4; public static final int RULE_genericBlockHeader = 5;
/*      */   public static final int RULE_if_statement = 6;
/*      */   public static final int RULE_if_body = 7;
/*      */   public static final int RULE_regexp = 8;
/*      */   public static final int RULE_locationBlockHeader = 9;
/*      */   public static final int RULE_rewriteStatement = 10;
/*   36 */   private static final String[] _LITERAL_NAMES = new String[] { null, "';'", "'{'", "'}'", "'if'", "'('", "')'", "'\\.'", "'^'", "'location'", "'rewrite'", "'last'", "'break'", "'redirect'", "'permanent'" };
/*      */ 
/*      */ 
/*      */   
/*   40 */   private static final String[] _SYMBOLIC_NAMES = new String[] { null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, "Value", "STR_EXT", "Comment", "REGEXP_PREFIXED", "QUOTED_STRING", "SINGLE_QUOTED", "WS" };
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*   45 */   public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*   53 */   public static final String[] tokenNames = new String[_SYMBOLIC_NAMES.length]; public static final String _serializedATN = "\003а훑舆괭䐗껱趀ꫝ\003\027º\004\002\t\002\004\003\t\003\004\004\t\004\004\005\t\005\004\006\t\006\004\007\t\007\004\b\t\b\004\t\t\t\004\n\t\n\004\013\t\013\004\f\t\f\003\002\003\002\003\002\003\002\003\002\003\002\003\002\003\002\006\002!\n\002\r\002\016\002\"\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\005\003.\n\003\003\003\003\003\003\004\003\004\003\004\003\004\003\004\003\004\003\004\007\0049\n\004\f\004\016\004<\013\004\003\005\003\005\003\005\003\005\003\005\003\006\003\006\003\006\003\006\003\006\003\006\005\006I\n\006\003\006\005\006L\n\006\003\006\003\006\003\006\003\006\003\006\003\006\003\006\003\006\003\006\003\006\003\006\003\006\007\006Z\n\006\f\006\016\006]\013\006\003\006\003\006\003\007\003\007\003\007\003\007\003\007\003\007\003\007\007\007h\n\007\f\007\016\007k\013\007\003\b\003\b\003\b\003\b\003\b\005\br\n\b\003\b\003\b\003\b\003\b\007\bx\n\b\f\b\016\b{\013\b\003\b\003\b\003\t\003\t\003\t\003\t\003\t\005\t\n\t\003\t\003\t\003\t\003\t\003\t\005\t\n\t\003\t\003\t\003\n\003\n\003\n\003\n\003\n\003\n\003\n\003\n\003\n\003\n\003\n\006\n\n\n\r\n\016\n\003\013\003\013\003\013\003\013\005\013¢\n\013\003\013\003\013\003\013\003\013\003\013\005\013©\n\013\003\f\003\f\003\f\003\f\003\f\003\f\003\f\005\f²\n\f\003\f\003\f\003\f\003\f\005\f¸\n\f\003\f\002\002\r\002\004\006\b\n\f\016\020\022\024\026\002\003\003\002\r\020Ê\002 \003\002\002\002\004-\003\002\002\002\0061\003\002\002\002\b=\003\002\002\002\nH\003\002\002\002\f`\003\002\002\002\016l\003\002\002\002\020~\003\002\002\002\022\003\002\002\002\024\003\002\002\002\026ª\003\002\002\002\030\031\005\004\003\002\031\032\b\002\001\002\032!\003\002\002\002\033\034\005\n\006\002\034\035\b\002\001\002\035!\003\002\002\002\036\037\007\023\002\002\037!\b\002\001\002 \030\003\002\002\002 \033\003\002\002\002 \036\003\002\002\002!\"\003\002\002\002\" \003\002\002\002\"#\003\002\002\002#\003\003\002\002\002$%\005\026\f\002%&\b\003\001\002&.\003\002\002\002'(\005\006\004\002()\b\003\001\002).\003\002\002\002*+\005\b\005\002+,\b\003\001\002,.\003\002\002\002-$\003\002\002\002-'\003\002\002\002-*\003\002\002\002./\003\002\002\002/0\007\003\002\0020\005\003\002\002\00212\007\021\002\0022:\b\004\001\00234\007\021\002\00249\b\004\001\00256\005\022\n\00267\b\004\001\00279\003\002\002\00283\003\002\002\00285\003\002\002\0029<\003\002\002\002:8\003\002\002\002:;\003\002\002\002;\007\003\002\002\002<:\003\002\002\002=>\007\024\002\002>?\b\005\001\002?@\007\021\002\002@A\b\005\001\002A\t\003\002\002\002BC\005\024\013\002CD\b\006\001\002DI\003\002\002\002EF\005\f\007\002FG\b\006\001\002GI\003\002\002\002HB\003\002\002\002HE\003\002\002\002IK\003\002\002\002JL\007\023\002\002KJ\003\002\002\002KL\003\002\002\002LM\003\002\002\002M[\007\004\002\002NO\005\004\003\002OP\b\006\001\002PZ\003\002\002\002QR\005\n\006\002RS\b\006\001\002SZ\003\002\002\002TU\005\016\b\002UV\b\006\001\002VZ\003\002\002\002WX\007\023\002\002XZ\b\006\001\002YN\003\002\002\002YQ\003\002\002\002YT\003\002\002\002YW\003\002\002\002Z]\003\002\002\002[Y\003\002\002\002[\\\003\002\002\002\\^\003\002\002\002][\003\002\002\002^_\007\005\002\002_\013\003\002\002\002`a\007\021\002\002ai\b\007\001\002bc\007\021\002\002ch\b\007\001\002de\005\022\n\002ef\b\007\001\002fh\003\002\002\002gb\003\002\002\002gd\003\002\002\002hk\003\002\002\002ig\003\002\002\002ij\003\002\002\002j\r\003\002\002\002ki\003\002\002\002lm\007\006\002\002mn\b\b\001\002no\005\020\t\002oq\b\b\001\002pr\007\023\002\002qp\003\002\002\002qr\003\002\002\002rs\003\002\002\002sy\007\004\002\002tu\005\004\003\002uv\b\b\001\002vx\003\002\002\002wt\003\002\002\002x{\003\002\002\002yw\003\002\002\002yz\003\002\002\002z|\003\002\002\002{y\003\002\002\002|}\007\005\002\002}\017\003\002\002\002~\007\007\002\002\007\021\002\002\b\t\001\002\007\021\002\002\b\t\001\002\003\002\002\002\003\002\002\002\003\002\002\002\007\021\002\002\b\t\001\002\005\022\n\002\b\t\001\002\003\002\002\002\003\002\002\002\003\002\002\002\003\002\002\002\003\002\002\002\007\b\002\002\021\003\002\002\002\007\t\002\002\b\n\001\002\007\n\002\002\b\n\001\002\007\021\002\002\b\n\001\002\007\007\002\002\005\022\n\002\b\n\001\002\007\b\002\002\003\002\002\002\003\002\002\002\003\002\002\002\003\002\002\002\003\002\002\002\003\002\002\002\003\002\002\002\003\002\002\002\023\003\002\002\002\007\013\002\002¡\b\013\001\002 \007\021\002\002 ¢\b\013\001\002¡\003\002\002\002¡¢\003\002\002\002¢¨\003\002\002\002£¤\007\021\002\002¤©\b\013\001\002¥¦\005\022\n\002¦§\b\013\001\002§©\003\002\002\002¨£\003\002\002\002¨¥\003\002\002\002©\025\003\002\002\002ª«\007\f\002\002«±\b\f\001\002¬­\007\021\002\002­²\b\f\001\002®¯\005\022\n\002¯°\b\f\001\002°²\003\002\002\002±¬\003\002\002\002±®\003\002\002\002²³\003\002\002\002³´\007\021\002\002´·\b\f\001\002µ¶\t\002\002\002¶¸\b\f\001\002·µ\003\002\002\002·¸\003\002\002\002¸\027\003\002\002\002\027 \"-8:HKY[giqy¡¨±·"; static { int i;
/*   54 */     for (i = 0; i < tokenNames.length; i++) {
/*   55 */       tokenNames[i] = VOCABULARY.getLiteralName(i);
/*   56 */       if (tokenNames[i] == null) {
/*   57 */         tokenNames[i] = VOCABULARY.getSymbolicName(i);
/*      */       }
/*      */       
/*   60 */       if (tokenNames[i] == null) {
/*   61 */         tokenNames[i] = "<INVALID>";
/*      */       }
/*      */     }  }
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public String[] getTokenNames() {
/*   69 */     return tokenNames;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public Vocabulary getVocabulary() {
/*   75 */     return VOCABULARY;
/*      */   }
/*      */   
/*      */   public String getGrammarFileName() {
/*   79 */     return "Nginx.g4";
/*      */   }
/*      */   public String[] getRuleNames() {
/*   82 */     return ruleNames;
/*      */   }
/*      */   public String getSerializedATN() {
/*   85 */     return "\003а훑舆괭䐗껱趀ꫝ\003\027º\004\002\t\002\004\003\t\003\004\004\t\004\004\005\t\005\004\006\t\006\004\007\t\007\004\b\t\b\004\t\t\t\004\n\t\n\004\013\t\013\004\f\t\f\003\002\003\002\003\002\003\002\003\002\003\002\003\002\003\002\006\002!\n\002\r\002\016\002\"\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\005\003.\n\003\003\003\003\003\003\004\003\004\003\004\003\004\003\004\003\004\003\004\007\0049\n\004\f\004\016\004<\013\004\003\005\003\005\003\005\003\005\003\005\003\006\003\006\003\006\003\006\003\006\003\006\005\006I\n\006\003\006\005\006L\n\006\003\006\003\006\003\006\003\006\003\006\003\006\003\006\003\006\003\006\003\006\003\006\003\006\007\006Z\n\006\f\006\016\006]\013\006\003\006\003\006\003\007\003\007\003\007\003\007\003\007\003\007\003\007\007\007h\n\007\f\007\016\007k\013\007\003\b\003\b\003\b\003\b\003\b\005\br\n\b\003\b\003\b\003\b\003\b\007\bx\n\b\f\b\016\b{\013\b\003\b\003\b\003\t\003\t\003\t\003\t\003\t\005\t\n\t\003\t\003\t\003\t\003\t\003\t\005\t\n\t\003\t\003\t\003\n\003\n\003\n\003\n\003\n\003\n\003\n\003\n\003\n\003\n\003\n\006\n\n\n\r\n\016\n\003\013\003\013\003\013\003\013\005\013¢\n\013\003\013\003\013\003\013\003\013\003\013\005\013©\n\013\003\f\003\f\003\f\003\f\003\f\003\f\003\f\005\f²\n\f\003\f\003\f\003\f\003\f\005\f¸\n\f\003\f\002\002\r\002\004\006\b\n\f\016\020\022\024\026\002\003\003\002\r\020Ê\002 \003\002\002\002\004-\003\002\002\002\0061\003\002\002\002\b=\003\002\002\002\nH\003\002\002\002\f`\003\002\002\002\016l\003\002\002\002\020~\003\002\002\002\022\003\002\002\002\024\003\002\002\002\026ª\003\002\002\002\030\031\005\004\003\002\031\032\b\002\001\002\032!\003\002\002\002\033\034\005\n\006\002\034\035\b\002\001\002\035!\003\002\002\002\036\037\007\023\002\002\037!\b\002\001\002 \030\003\002\002\002 \033\003\002\002\002 \036\003\002\002\002!\"\003\002\002\002\" \003\002\002\002\"#\003\002\002\002#\003\003\002\002\002$%\005\026\f\002%&\b\003\001\002&.\003\002\002\002'(\005\006\004\002()\b\003\001\002).\003\002\002\002*+\005\b\005\002+,\b\003\001\002,.\003\002\002\002-$\003\002\002\002-'\003\002\002\002-*\003\002\002\002./\003\002\002\002/0\007\003\002\0020\005\003\002\002\00212\007\021\002\0022:\b\004\001\00234\007\021\002\00249\b\004\001\00256\005\022\n\00267\b\004\001\00279\003\002\002\00283\003\002\002\00285\003\002\002\0029<\003\002\002\002:8\003\002\002\002:;\003\002\002\002;\007\003\002\002\002<:\003\002\002\002=>\007\024\002\002>?\b\005\001\002?@\007\021\002\002@A\b\005\001\002A\t\003\002\002\002BC\005\024\013\002CD\b\006\001\002DI\003\002\002\002EF\005\f\007\002FG\b\006\001\002GI\003\002\002\002HB\003\002\002\002HE\003\002\002\002IK\003\002\002\002JL\007\023\002\002KJ\003\002\002\002KL\003\002\002\002LM\003\002\002\002M[\007\004\002\002NO\005\004\003\002OP\b\006\001\002PZ\003\002\002\002QR\005\n\006\002RS\b\006\001\002SZ\003\002\002\002TU\005\016\b\002UV\b\006\001\002VZ\003\002\002\002WX\007\023\002\002XZ\b\006\001\002YN\003\002\002\002YQ\003\002\002\002YT\003\002\002\002YW\003\002\002\002Z]\003\002\002\002[Y\003\002\002\002[\\\003\002\002\002\\^\003\002\002\002][\003\002\002\002^_\007\005\002\002_\013\003\002\002\002`a\007\021\002\002ai\b\007\001\002bc\007\021\002\002ch\b\007\001\002de\005\022\n\002ef\b\007\001\002fh\003\002\002\002gb\003\002\002\002gd\003\002\002\002hk\003\002\002\002ig\003\002\002\002ij\003\002\002\002j\r\003\002\002\002ki\003\002\002\002lm\007\006\002\002mn\b\b\001\002no\005\020\t\002oq\b\b\001\002pr\007\023\002\002qp\003\002\002\002qr\003\002\002\002rs\003\002\002\002sy\007\004\002\002tu\005\004\003\002uv\b\b\001\002vx\003\002\002\002wt\003\002\002\002x{\003\002\002\002yw\003\002\002\002yz\003\002\002\002z|\003\002\002\002{y\003\002\002\002|}\007\005\002\002}\017\003\002\002\002~\007\007\002\002\007\021\002\002\b\t\001\002\007\021\002\002\b\t\001\002\003\002\002\002\003\002\002\002\003\002\002\002\007\021\002\002\b\t\001\002\005\022\n\002\b\t\001\002\003\002\002\002\003\002\002\002\003\002\002\002\003\002\002\002\003\002\002\002\007\b\002\002\021\003\002\002\002\007\t\002\002\b\n\001\002\007\n\002\002\b\n\001\002\007\021\002\002\b\n\001\002\007\007\002\002\005\022\n\002\b\n\001\002\007\b\002\002\003\002\002\002\003\002\002\002\003\002\002\002\003\002\002\002\003\002\002\002\003\002\002\002\003\002\002\002\003\002\002\002\023\003\002\002\002\007\013\002\002¡\b\013\001\002 \007\021\002\002 ¢\b\013\001\002¡\003\002\002\002¡¢\003\002\002\002¢¨\003\002\002\002£¤\007\021\002\002¤©\b\013\001\002¥¦\005\022\n\002¦§\b\013\001\002§©\003\002\002\002¨£\003\002\002\002¨¥\003\002\002\002©\025\003\002\002\002ª«\007\f\002\002«±\b\f\001\002¬­\007\021\002\002­²\b\f\001\002®¯\005\022\n\002¯°\b\f\001\002°²\003\002\002\002±¬\003\002\002\002±®\003\002\002\002²³\003\002\002\002³´\007\021\002\002´·\b\f\001\002µ¶\t\002\002\002¶¸\b\f\001\002·µ\003\002\002\002·¸\003\002\002\002¸\027\003\002\002\002\027 \"-8:HKY[giqy¡¨±·";
/*      */   }
/*      */   public ATN getATN() {
/*   88 */     return _ATN;
/*      */   }
/*      */   public NginxParser(TokenStream input) {
/*   91 */     super(input);
/*   92 */     this._interp = new ParserATNSimulator(this, _ATN, _decisionToDFA, _sharedContextCache);
/*      */   }
/*      */   public static class ConfigContext extends ParserRuleContext { public NgxConfig ret;
/*      */     public NginxParser.StatementContext statement;
/*      */     public NginxParser.BlockContext block;
/*      */     public Token Comment;
/*      */     
/*      */     public List<NginxParser.StatementContext> statement() {
/*  100 */       return getRuleContexts(NginxParser.StatementContext.class);
/*      */     }
/*      */     public NginxParser.StatementContext statement(int i) {
/*  103 */       return getRuleContext(NginxParser.StatementContext.class, i);
/*      */     }
/*      */     public List<NginxParser.BlockContext> block() {
/*  106 */       return getRuleContexts(NginxParser.BlockContext.class);
/*      */     }
/*      */     public NginxParser.BlockContext block(int i) {
/*  109 */       return getRuleContext(NginxParser.BlockContext.class, i);
/*      */     } public List<TerminalNode> Comment() {
/*  111 */       return getTokens(17);
/*      */     } public TerminalNode Comment(int i) {
/*  113 */       return getToken(17, i);
/*      */     }
/*      */     public ConfigContext(ParserRuleContext parent, int invokingState) {
/*  116 */       super(parent, invokingState);
/*      */     } public int getRuleIndex() {
/*  118 */       return 0;
/*      */     }
/*      */     public void enterRule(ParseTreeListener listener) {
/*  121 */       if (listener instanceof NginxListener) ((NginxListener)listener).enterConfig(this); 
/*      */     }
/*      */     
/*      */     public void exitRule(ParseTreeListener listener) {
/*  125 */       if (listener instanceof NginxListener) ((NginxListener)listener).exitConfig(this); 
/*      */     }
/*      */     
/*      */     public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
/*  129 */       if (visitor instanceof NginxVisitor) return ((NginxVisitor)visitor).visitConfig(this); 
/*  130 */       return visitor.visitChildren(this);
/*      */     } }
/*      */ 
/*      */   
/*      */   public final ConfigContext config() throws RecognitionException {
/*  135 */     ConfigContext _localctx = new ConfigContext(this._ctx, getState());
/*  136 */     enterRule(_localctx, 0, 0);
/*  137 */     _localctx.ret = new NgxConfig();
/*      */     
/*      */     try {
/*  140 */       enterOuterAlt(_localctx, 1);
/*      */       
/*  142 */       setState(30);
/*  143 */       this._errHandler.sync(this);
/*  144 */       int _la = this._input.LA(1);
/*      */       
/*      */       do {
/*  147 */         setState(30);
/*  148 */         this._errHandler.sync(this);
/*  149 */         switch (getInterpreter().adaptivePredict(this._input, 0, this._ctx)) {
/*      */           
/*      */           case 1:
/*  152 */             setState(22);
/*  153 */             _localctx.statement = statement();
/*  154 */             _localctx.ret.addEntry((NgxEntry)_localctx.statement.ret);
/*      */             break;
/*      */ 
/*      */           
/*      */           case 2:
/*  159 */             setState(25);
/*  160 */             _localctx.block = block();
/*  161 */             _localctx.ret.addEntry((NgxEntry)_localctx.block.ret);
/*      */             break;
/*      */ 
/*      */           
/*      */           case 3:
/*  166 */             setState(28);
/*  167 */             _localctx.Comment = match(17);
/*  168 */             _localctx.ret.addEntry((NgxEntry)new NgxComment((_localctx.Comment != null) ? _localctx.Comment.getText() : null));
/*      */             break;
/*      */         } 
/*      */ 
/*      */         
/*  173 */         setState(32);
/*  174 */         this._errHandler.sync(this);
/*  175 */         _la = this._input.LA(1);
/*  176 */       } while ((_la & 0xFFFFFFC0) == 0 && (1L << _la & 0x68600L) != 0L);
/*      */     
/*      */     }
/*  179 */     catch (RecognitionException re) {
/*  180 */       _localctx.exception = re;
/*  181 */       this._errHandler.reportError(this, re);
/*  182 */       this._errHandler.recover(this, re);
/*      */     } finally {
/*      */       
/*  185 */       exitRule();
/*      */     } 
/*  187 */     return _localctx;
/*      */   }
/*      */   
/*      */   public static class StatementContext extends ParserRuleContext { public NgxParam ret;
/*      */     public NginxParser.RewriteStatementContext rewriteStatement;
/*      */     public NginxParser.GenericStatementContext genericStatement;
/*      */     public NginxParser.RegexHeaderStatementContext regexHeaderStatement;
/*      */     
/*      */     public NginxParser.RewriteStatementContext rewriteStatement() {
/*  196 */       return getRuleContext(NginxParser.RewriteStatementContext.class, 0);
/*      */     }
/*      */     public NginxParser.GenericStatementContext genericStatement() {
/*  199 */       return getRuleContext(NginxParser.GenericStatementContext.class, 0);
/*      */     }
/*      */     public NginxParser.RegexHeaderStatementContext regexHeaderStatement() {
/*  202 */       return getRuleContext(NginxParser.RegexHeaderStatementContext.class, 0);
/*      */     }
/*      */     public StatementContext(ParserRuleContext parent, int invokingState) {
/*  205 */       super(parent, invokingState);
/*      */     } public int getRuleIndex() {
/*  207 */       return 1;
/*      */     }
/*      */     public void enterRule(ParseTreeListener listener) {
/*  210 */       if (listener instanceof NginxListener) ((NginxListener)listener).enterStatement(this); 
/*      */     }
/*      */     
/*      */     public void exitRule(ParseTreeListener listener) {
/*  214 */       if (listener instanceof NginxListener) ((NginxListener)listener).exitStatement(this); 
/*      */     }
/*      */     
/*      */     public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
/*  218 */       if (visitor instanceof NginxVisitor) return ((NginxVisitor)visitor).visitStatement(this); 
/*  219 */       return visitor.visitChildren(this);
/*      */     } }
/*      */ 
/*      */   
/*      */   public final StatementContext statement() throws RecognitionException {
/*  224 */     StatementContext _localctx = new StatementContext(this._ctx, getState());
/*  225 */     enterRule(_localctx, 2, 1);
/*      */     try {
/*  227 */       enterOuterAlt(_localctx, 1);
/*      */       
/*  229 */       setState(43);
/*  230 */       switch (this._input.LA(1)) {
/*      */         
/*      */         case 10:
/*  233 */           setState(34);
/*  234 */           _localctx.rewriteStatement = rewriteStatement();
/*  235 */           _localctx.ret = _localctx.rewriteStatement.ret;
/*      */           break;
/*      */ 
/*      */         
/*      */         case 15:
/*  240 */           setState(37);
/*  241 */           _localctx.genericStatement = genericStatement();
/*  242 */           _localctx.ret = _localctx.genericStatement.ret;
/*      */           break;
/*      */ 
/*      */         
/*      */         case 18:
/*  247 */           setState(40);
/*  248 */           _localctx.regexHeaderStatement = regexHeaderStatement();
/*  249 */           _localctx.ret = _localctx.regexHeaderStatement.ret;
/*      */           break;
/*      */         
/*      */         default:
/*  253 */           throw new NoViableAltException(this);
/*      */       } 
/*  255 */       setState(45);
/*  256 */       match(1);
/*      */     
/*      */     }
/*  259 */     catch (RecognitionException re) {
/*  260 */       _localctx.exception = re;
/*  261 */       this._errHandler.reportError(this, re);
/*  262 */       this._errHandler.recover(this, re);
/*      */     } finally {
/*      */       
/*  265 */       exitRule();
/*      */     } 
/*  267 */     return _localctx;
/*      */   }
/*      */   public static class GenericStatementContext extends ParserRuleContext { public NgxParam ret;
/*      */     public Token Value;
/*      */     public NginxParser.RegexpContext r;
/*      */     
/*      */     public List<TerminalNode> Value() {
/*  274 */       return getTokens(15);
/*      */     } public TerminalNode Value(int i) {
/*  276 */       return getToken(15, i);
/*      */     }
/*      */     public List<NginxParser.RegexpContext> regexp() {
/*  279 */       return getRuleContexts(NginxParser.RegexpContext.class);
/*      */     }
/*      */     public NginxParser.RegexpContext regexp(int i) {
/*  282 */       return getRuleContext(NginxParser.RegexpContext.class, i);
/*      */     }
/*      */     public GenericStatementContext(ParserRuleContext parent, int invokingState) {
/*  285 */       super(parent, invokingState);
/*      */     } public int getRuleIndex() {
/*  287 */       return 2;
/*      */     }
/*      */     public void enterRule(ParseTreeListener listener) {
/*  290 */       if (listener instanceof NginxListener) ((NginxListener)listener).enterGenericStatement(this); 
/*      */     }
/*      */     
/*      */     public void exitRule(ParseTreeListener listener) {
/*  294 */       if (listener instanceof NginxListener) ((NginxListener)listener).exitGenericStatement(this); 
/*      */     }
/*      */     
/*      */     public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
/*  298 */       if (visitor instanceof NginxVisitor) return ((NginxVisitor)visitor).visitGenericStatement(this); 
/*  299 */       return visitor.visitChildren(this);
/*      */     } }
/*      */ 
/*      */   
/*      */   public final GenericStatementContext genericStatement() throws RecognitionException {
/*  304 */     GenericStatementContext _localctx = new GenericStatementContext(this._ctx, getState());
/*  305 */     enterRule(_localctx, 4, 2);
/*  306 */     _localctx.ret = new NgxParam();
/*      */     
/*      */     try {
/*  309 */       enterOuterAlt(_localctx, 1);
/*      */       
/*  311 */       setState(47);
/*  312 */       _localctx.Value = match(15);
/*  313 */       _localctx.ret.addValue((_localctx.Value != null) ? _localctx.Value.getText() : null);
/*  314 */       setState(56);
/*  315 */       this._errHandler.sync(this);
/*  316 */       int _la = this._input.LA(1);
/*  317 */       while ((_la & 0xFFFFFFC0) == 0 && (1L << _la & 0x81A0L) != 0L)
/*      */       {
/*  319 */         setState(54);
/*  320 */         this._errHandler.sync(this);
/*  321 */         switch (getInterpreter().adaptivePredict(this._input, 3, this._ctx)) {
/*      */           
/*      */           case 1:
/*  324 */             setState(49);
/*  325 */             _localctx.Value = match(15);
/*  326 */             _localctx.ret.addValue((_localctx.Value != null) ? _localctx.Value.getText() : null);
/*      */             break;
/*      */ 
/*      */           
/*      */           case 2:
/*  331 */             setState(51);
/*  332 */             _localctx.r = regexp();
/*  333 */             _localctx.ret.addValue(_localctx.r.ret);
/*      */             break;
/*      */         } 
/*      */ 
/*      */         
/*  338 */         setState(58);
/*  339 */         this._errHandler.sync(this);
/*  340 */         _la = this._input.LA(1);
/*      */       }
/*      */     
/*      */     }
/*  344 */     catch (RecognitionException re) {
/*  345 */       _localctx.exception = re;
/*  346 */       this._errHandler.reportError(this, re);
/*  347 */       this._errHandler.recover(this, re);
/*      */     } finally {
/*      */       
/*  350 */       exitRule();
/*      */     } 
/*  352 */     return _localctx;
/*      */   }
/*      */   
/*      */   public static class RegexHeaderStatementContext extends ParserRuleContext { public NgxParam ret;
/*      */     public Token REGEXP_PREFIXED;
/*      */     public Token Value;
/*      */     
/*  359 */     public TerminalNode REGEXP_PREFIXED() { return getToken(18, 0); } public TerminalNode Value() {
/*  360 */       return getToken(15, 0);
/*      */     } public RegexHeaderStatementContext(ParserRuleContext parent, int invokingState) {
/*  362 */       super(parent, invokingState);
/*      */     } public int getRuleIndex() {
/*  364 */       return 3;
/*      */     }
/*      */     public void enterRule(ParseTreeListener listener) {
/*  367 */       if (listener instanceof NginxListener) ((NginxListener)listener).enterRegexHeaderStatement(this); 
/*      */     }
/*      */     
/*      */     public void exitRule(ParseTreeListener listener) {
/*  371 */       if (listener instanceof NginxListener) ((NginxListener)listener).exitRegexHeaderStatement(this); 
/*      */     }
/*      */     
/*      */     public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
/*  375 */       if (visitor instanceof NginxVisitor) return ((NginxVisitor)visitor).visitRegexHeaderStatement(this); 
/*  376 */       return visitor.visitChildren(this);
/*      */     } }
/*      */ 
/*      */   
/*      */   public final RegexHeaderStatementContext regexHeaderStatement() throws RecognitionException {
/*  381 */     RegexHeaderStatementContext _localctx = new RegexHeaderStatementContext(this._ctx, getState());
/*  382 */     enterRule(_localctx, 6, 3);
/*  383 */     _localctx.ret = new NgxParam();
/*      */     try {
/*  385 */       enterOuterAlt(_localctx, 1);
/*      */       
/*  387 */       setState(59);
/*  388 */       _localctx.REGEXP_PREFIXED = match(18);
/*  389 */       _localctx.ret.addValue((_localctx.REGEXP_PREFIXED != null) ? _localctx.REGEXP_PREFIXED.getText() : null);
/*  390 */       setState(61);
/*  391 */       _localctx.Value = match(15);
/*  392 */       _localctx.ret.addValue((_localctx.Value != null) ? _localctx.Value.getText() : null);
/*      */     
/*      */     }
/*  395 */     catch (RecognitionException re) {
/*  396 */       _localctx.exception = re;
/*  397 */       this._errHandler.reportError(this, re);
/*  398 */       this._errHandler.recover(this, re);
/*      */     } finally {
/*      */       
/*  401 */       exitRule();
/*      */     } 
/*  403 */     return _localctx;
/*      */   }
/*      */   
/*      */   public static class BlockContext extends ParserRuleContext { public NgxBlock ret;
/*      */     public NginxParser.LocationBlockHeaderContext locationBlockHeader;
/*      */     public NginxParser.GenericBlockHeaderContext genericBlockHeader;
/*      */     public Token Comment;
/*      */     public NginxParser.StatementContext statement;
/*      */     public BlockContext b;
/*      */     public NginxParser.If_statementContext if_statement;
/*      */     
/*      */     public NginxParser.LocationBlockHeaderContext locationBlockHeader() {
/*  415 */       return getRuleContext(NginxParser.LocationBlockHeaderContext.class, 0);
/*      */     }
/*      */     public NginxParser.GenericBlockHeaderContext genericBlockHeader() {
/*  418 */       return getRuleContext(NginxParser.GenericBlockHeaderContext.class, 0);
/*      */     } public List<TerminalNode> Comment() {
/*  420 */       return getTokens(17);
/*      */     } public TerminalNode Comment(int i) {
/*  422 */       return getToken(17, i);
/*      */     }
/*      */     public List<NginxParser.StatementContext> statement() {
/*  425 */       return getRuleContexts(NginxParser.StatementContext.class);
/*      */     }
/*      */     public NginxParser.StatementContext statement(int i) {
/*  428 */       return getRuleContext(NginxParser.StatementContext.class, i);
/*      */     }
/*      */     public List<NginxParser.If_statementContext> if_statement() {
/*  431 */       return getRuleContexts(NginxParser.If_statementContext.class);
/*      */     }
/*      */     public NginxParser.If_statementContext if_statement(int i) {
/*  434 */       return getRuleContext(NginxParser.If_statementContext.class, i);
/*      */     }
/*      */     public List<BlockContext> block() {
/*  437 */       return getRuleContexts(BlockContext.class);
/*      */     }
/*      */     public BlockContext block(int i) {
/*  440 */       return getRuleContext(BlockContext.class, i);
/*      */     }
/*      */     public BlockContext(ParserRuleContext parent, int invokingState) {
/*  443 */       super(parent, invokingState);
/*      */     } public int getRuleIndex() {
/*  445 */       return 4;
/*      */     }
/*      */     public void enterRule(ParseTreeListener listener) {
/*  448 */       if (listener instanceof NginxListener) ((NginxListener)listener).enterBlock(this); 
/*      */     }
/*      */     
/*      */     public void exitRule(ParseTreeListener listener) {
/*  452 */       if (listener instanceof NginxListener) ((NginxListener)listener).exitBlock(this); 
/*      */     }
/*      */     
/*      */     public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
/*  456 */       if (visitor instanceof NginxVisitor) return ((NginxVisitor)visitor).visitBlock(this); 
/*  457 */       return visitor.visitChildren(this);
/*      */     } }
/*      */ 
/*      */   
/*      */   public final BlockContext block() throws RecognitionException {
/*  462 */     BlockContext _localctx = new BlockContext(this._ctx, getState());
/*  463 */     enterRule(_localctx, 8, 4);
/*  464 */     _localctx.ret = new NgxBlock();
/*      */     
/*      */     try {
/*  467 */       enterOuterAlt(_localctx, 1);
/*      */       
/*  469 */       setState(70);
/*  470 */       switch (this._input.LA(1)) {
/*      */         
/*      */         case 9:
/*  473 */           setState(64);
/*  474 */           _localctx.locationBlockHeader = locationBlockHeader();
/*  475 */           _localctx.ret.getTokens().addAll(_localctx.locationBlockHeader.ret);
/*      */           break;
/*      */ 
/*      */         
/*      */         case 15:
/*  480 */           setState(67);
/*  481 */           _localctx.genericBlockHeader = genericBlockHeader();
/*  482 */           _localctx.ret.getTokens().addAll(_localctx.genericBlockHeader.ret);
/*      */           break;
/*      */         
/*      */         default:
/*  486 */           throw new NoViableAltException(this);
/*      */       } 
/*  488 */       setState(73);
/*  489 */       int _la = this._input.LA(1);
/*  490 */       if (_la == 17) {
/*      */         
/*  492 */         setState(72);
/*  493 */         _localctx.Comment = match(17);
/*      */       } 
/*      */ 
/*      */       
/*  497 */       setState(75);
/*  498 */       match(2);
/*  499 */       setState(89);
/*  500 */       this._errHandler.sync(this);
/*  501 */       _la = this._input.LA(1);
/*  502 */       while ((_la & 0xFFFFFFC0) == 0 && (1L << _la & 0x68610L) != 0L) {
/*      */         
/*  504 */         setState(87);
/*  505 */         this._errHandler.sync(this);
/*  506 */         switch (getInterpreter().adaptivePredict(this._input, 7, this._ctx)) {
/*      */           
/*      */           case 1:
/*  509 */             setState(76);
/*  510 */             _localctx.statement = statement();
/*  511 */             _localctx.ret.addEntry((NgxEntry)_localctx.statement.ret);
/*      */             break;
/*      */ 
/*      */           
/*      */           case 2:
/*  516 */             setState(79);
/*  517 */             _localctx.b = block();
/*  518 */             _localctx.ret.addEntry((NgxEntry)_localctx.b.ret);
/*      */             break;
/*      */ 
/*      */           
/*      */           case 3:
/*  523 */             setState(82);
/*  524 */             _localctx.if_statement = if_statement();
/*  525 */             _localctx.ret.addEntry((NgxEntry)_localctx.if_statement.ret);
/*      */             break;
/*      */ 
/*      */           
/*      */           case 4:
/*  530 */             setState(85);
/*  531 */             _localctx.Comment = match(17);
/*  532 */             _localctx.ret.addEntry((NgxEntry)new NgxComment((_localctx.Comment != null) ? _localctx.Comment.getText() : null));
/*      */             break;
/*      */         } 
/*      */ 
/*      */         
/*  537 */         setState(91);
/*  538 */         this._errHandler.sync(this);
/*  539 */         _la = this._input.LA(1);
/*      */       } 
/*  541 */       setState(92);
/*  542 */       match(3);
/*      */     
/*      */     }
/*  545 */     catch (RecognitionException re) {
/*  546 */       _localctx.exception = re;
/*  547 */       this._errHandler.reportError(this, re);
/*  548 */       this._errHandler.recover(this, re);
/*      */     } finally {
/*      */       
/*  551 */       exitRule();
/*      */     } 
/*  553 */     return _localctx;
/*      */   }
/*      */   public static class GenericBlockHeaderContext extends ParserRuleContext { public List<NgxToken> ret;
/*      */     public Token Value;
/*      */     public NginxParser.RegexpContext regexp;
/*      */     
/*      */     public List<TerminalNode> Value() {
/*  560 */       return getTokens(15);
/*      */     } public TerminalNode Value(int i) {
/*  562 */       return getToken(15, i);
/*      */     }
/*      */     public List<NginxParser.RegexpContext> regexp() {
/*  565 */       return getRuleContexts(NginxParser.RegexpContext.class);
/*      */     }
/*      */     public NginxParser.RegexpContext regexp(int i) {
/*  568 */       return getRuleContext(NginxParser.RegexpContext.class, i);
/*      */     }
/*      */     public GenericBlockHeaderContext(ParserRuleContext parent, int invokingState) {
/*  571 */       super(parent, invokingState);
/*      */     } public int getRuleIndex() {
/*  573 */       return 5;
/*      */     }
/*      */     public void enterRule(ParseTreeListener listener) {
/*  576 */       if (listener instanceof NginxListener) ((NginxListener)listener).enterGenericBlockHeader(this); 
/*      */     }
/*      */     
/*      */     public void exitRule(ParseTreeListener listener) {
/*  580 */       if (listener instanceof NginxListener) ((NginxListener)listener).exitGenericBlockHeader(this); 
/*      */     }
/*      */     
/*      */     public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
/*  584 */       if (visitor instanceof NginxVisitor) return ((NginxVisitor)visitor).visitGenericBlockHeader(this); 
/*  585 */       return visitor.visitChildren(this);
/*      */     } }
/*      */ 
/*      */   
/*      */   public final GenericBlockHeaderContext genericBlockHeader() throws RecognitionException {
/*  590 */     GenericBlockHeaderContext _localctx = new GenericBlockHeaderContext(this._ctx, getState());
/*  591 */     enterRule(_localctx, 10, 5);
/*  592 */     _localctx.ret = new ArrayList<NgxToken>();
/*      */     
/*      */     try {
/*  595 */       enterOuterAlt(_localctx, 1);
/*      */       
/*  597 */       setState(94);
/*  598 */       _localctx.Value = match(15);
/*  599 */       _localctx.ret.add(new NgxToken((_localctx.Value != null) ? _localctx.Value.getText() : null));
/*  600 */       setState(103);
/*  601 */       this._errHandler.sync(this);
/*  602 */       int _la = this._input.LA(1);
/*  603 */       while ((_la & 0xFFFFFFC0) == 0 && (1L << _la & 0x81A0L) != 0L)
/*      */       {
/*  605 */         setState(101);
/*  606 */         this._errHandler.sync(this);
/*  607 */         switch (getInterpreter().adaptivePredict(this._input, 9, this._ctx)) {
/*      */           
/*      */           case 1:
/*  610 */             setState(96);
/*  611 */             _localctx.Value = match(15);
/*  612 */             _localctx.ret.add(new NgxToken((_localctx.Value != null) ? _localctx.Value.getText() : null));
/*      */             break;
/*      */ 
/*      */           
/*      */           case 2:
/*  617 */             setState(98);
/*  618 */             _localctx.regexp = regexp();
/*  619 */             _localctx.ret.add(new NgxToken(_localctx.regexp.ret));
/*      */             break;
/*      */         } 
/*      */ 
/*      */         
/*  624 */         setState(105);
/*  625 */         this._errHandler.sync(this);
/*  626 */         _la = this._input.LA(1);
/*      */       }
/*      */     
/*      */     }
/*  630 */     catch (RecognitionException re) {
/*  631 */       _localctx.exception = re;
/*  632 */       this._errHandler.reportError(this, re);
/*  633 */       this._errHandler.recover(this, re);
/*      */     } finally {
/*      */       
/*  636 */       exitRule();
/*      */     } 
/*  638 */     return _localctx;
/*      */   }
/*      */   
/*      */   public static class If_statementContext extends ParserRuleContext { public NgxIfBlock ret;
/*      */     public Token id;
/*      */     public NginxParser.If_bodyContext if_body;
/*      */     public NginxParser.StatementContext statement;
/*      */     
/*      */     public NginxParser.If_bodyContext if_body() {
/*  647 */       return getRuleContext(NginxParser.If_bodyContext.class, 0);
/*      */     } public TerminalNode Comment() {
/*  649 */       return getToken(17, 0);
/*      */     } public List<NginxParser.StatementContext> statement() {
/*  651 */       return getRuleContexts(NginxParser.StatementContext.class);
/*      */     }
/*      */     public NginxParser.StatementContext statement(int i) {
/*  654 */       return getRuleContext(NginxParser.StatementContext.class, i);
/*      */     }
/*      */     public If_statementContext(ParserRuleContext parent, int invokingState) {
/*  657 */       super(parent, invokingState);
/*      */     } public int getRuleIndex() {
/*  659 */       return 6;
/*      */     }
/*      */     public void enterRule(ParseTreeListener listener) {
/*  662 */       if (listener instanceof NginxListener) ((NginxListener)listener).enterIf_statement(this); 
/*      */     }
/*      */     
/*      */     public void exitRule(ParseTreeListener listener) {
/*  666 */       if (listener instanceof NginxListener) ((NginxListener)listener).exitIf_statement(this); 
/*      */     }
/*      */     
/*      */     public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
/*  670 */       if (visitor instanceof NginxVisitor) return ((NginxVisitor)visitor).visitIf_statement(this); 
/*  671 */       return visitor.visitChildren(this);
/*      */     } }
/*      */ 
/*      */   
/*      */   public final If_statementContext if_statement() throws RecognitionException {
/*  676 */     If_statementContext _localctx = new If_statementContext(this._ctx, getState());
/*  677 */     enterRule(_localctx, 12, 6);
/*  678 */     _localctx.ret = new NgxIfBlock();
/*      */     
/*      */     try {
/*  681 */       enterOuterAlt(_localctx, 1);
/*      */       
/*  683 */       setState(106);
/*  684 */       _localctx.id = match(4);
/*  685 */       _localctx.ret.addValue(new NgxToken((_localctx.id != null) ? _localctx.id.getText() : null));
/*  686 */       setState(108);
/*  687 */       _localctx.if_body = if_body();
/*  688 */       _localctx.ret.getTokens().addAll(_localctx.if_body.ret);
/*  689 */       setState(111);
/*  690 */       int _la = this._input.LA(1);
/*  691 */       if (_la == 17) {
/*      */         
/*  693 */         setState(110);
/*  694 */         match(17);
/*      */       } 
/*      */ 
/*      */       
/*  698 */       setState(113);
/*  699 */       match(2);
/*  700 */       setState(119);
/*  701 */       this._errHandler.sync(this);
/*  702 */       _la = this._input.LA(1);
/*  703 */       while ((_la & 0xFFFFFFC0) == 0 && (1L << _la & 0x48400L) != 0L) {
/*      */ 
/*      */         
/*  706 */         setState(114);
/*  707 */         _localctx.statement = statement();
/*  708 */         _localctx.ret.addEntry((NgxEntry)_localctx.statement.ret);
/*      */ 
/*      */         
/*  711 */         setState(121);
/*  712 */         this._errHandler.sync(this);
/*  713 */         _la = this._input.LA(1);
/*      */       } 
/*  715 */       setState(122);
/*  716 */       match(3);
/*      */     
/*      */     }
/*  719 */     catch (RecognitionException re) {
/*  720 */       _localctx.exception = re;
/*  721 */       this._errHandler.reportError(this, re);
/*  722 */       this._errHandler.recover(this, re);
/*      */     } finally {
/*      */       
/*  725 */       exitRule();
/*      */     } 
/*  727 */     return _localctx;
/*      */   }
/*      */   public static class If_bodyContext extends ParserRuleContext { public List<NgxToken> ret;
/*      */     public Token Value;
/*      */     public NginxParser.RegexpContext regexp;
/*      */     
/*      */     public List<TerminalNode> Value() {
/*  734 */       return getTokens(15);
/*      */     } public TerminalNode Value(int i) {
/*  736 */       return getToken(15, i);
/*      */     }
/*      */     public NginxParser.RegexpContext regexp() {
/*  739 */       return getRuleContext(NginxParser.RegexpContext.class, 0);
/*      */     }
/*      */     public If_bodyContext(ParserRuleContext parent, int invokingState) {
/*  742 */       super(parent, invokingState);
/*      */     } public int getRuleIndex() {
/*  744 */       return 7;
/*      */     }
/*      */     public void enterRule(ParseTreeListener listener) {
/*  747 */       if (listener instanceof NginxListener) ((NginxListener)listener).enterIf_body(this); 
/*      */     }
/*      */     
/*      */     public void exitRule(ParseTreeListener listener) {
/*  751 */       if (listener instanceof NginxListener) ((NginxListener)listener).exitIf_body(this); 
/*      */     }
/*      */     
/*      */     public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
/*  755 */       if (visitor instanceof NginxVisitor) return ((NginxVisitor)visitor).visitIf_body(this); 
/*  756 */       return visitor.visitChildren(this);
/*      */     } }
/*      */ 
/*      */   
/*      */   public final If_bodyContext if_body() throws RecognitionException {
/*  761 */     If_bodyContext _localctx = new If_bodyContext(this._ctx, getState());
/*  762 */     enterRule(_localctx, 14, 7);
/*  763 */     _localctx.ret = new ArrayList<NgxToken>();
/*      */     try {
/*  765 */       enterOuterAlt(_localctx, 1);
/*      */       
/*  767 */       setState(124);
/*  768 */       match(5);
/*  769 */       setState(125);
/*  770 */       _localctx.Value = match(15);
/*  771 */       _localctx.ret.add(new NgxToken((_localctx.Value != null) ? _localctx.Value.getText() : null));
/*  772 */       setState(129);
/*  773 */       this._errHandler.sync(this);
/*  774 */       switch (getInterpreter().adaptivePredict(this._input, 13, this._ctx)) {
/*      */         
/*      */         case 1:
/*  777 */           setState(127);
/*  778 */           _localctx.Value = match(15);
/*  779 */           _localctx.ret.add(new NgxToken((_localctx.Value != null) ? _localctx.Value.getText() : null));
/*      */           break;
/*      */       } 
/*      */       
/*  783 */       setState(136);
/*  784 */       this._errHandler.sync(this);
/*  785 */       switch (getInterpreter().adaptivePredict(this._input, 14, this._ctx)) {
/*      */         
/*      */         case 1:
/*  788 */           setState(131);
/*  789 */           _localctx.Value = match(15);
/*  790 */           _localctx.ret.add(new NgxToken((_localctx.Value != null) ? _localctx.Value.getText() : null));
/*      */           break;
/*      */ 
/*      */         
/*      */         case 2:
/*  795 */           setState(133);
/*  796 */           _localctx.regexp = regexp();
/*  797 */           _localctx.ret.add(new NgxToken(_localctx.regexp.ret));
/*      */           break;
/*      */       } 
/*      */       
/*  801 */       setState(138);
/*  802 */       match(6);
/*      */     
/*      */     }
/*  805 */     catch (RecognitionException re) {
/*  806 */       _localctx.exception = re;
/*  807 */       this._errHandler.reportError(this, re);
/*  808 */       this._errHandler.recover(this, re);
/*      */     } finally {
/*      */       
/*  811 */       exitRule();
/*      */     } 
/*  813 */     return _localctx;
/*      */   }
/*      */   public static class RegexpContext extends ParserRuleContext { public String ret;
/*      */     public Token id;
/*      */     public Token Value;
/*      */     public RegexpContext r;
/*      */     
/*      */     public List<TerminalNode> Value() {
/*  821 */       return getTokens(15);
/*      */     } public TerminalNode Value(int i) {
/*  823 */       return getToken(15, i);
/*      */     }
/*      */     public List<RegexpContext> regexp() {
/*  826 */       return getRuleContexts(RegexpContext.class);
/*      */     }
/*      */     public RegexpContext regexp(int i) {
/*  829 */       return getRuleContext(RegexpContext.class, i);
/*      */     }
/*      */     public RegexpContext(ParserRuleContext parent, int invokingState) {
/*  832 */       super(parent, invokingState);
/*      */     } public int getRuleIndex() {
/*  834 */       return 8;
/*      */     }
/*      */     public void enterRule(ParseTreeListener listener) {
/*  837 */       if (listener instanceof NginxListener) ((NginxListener)listener).enterRegexp(this); 
/*      */     }
/*      */     
/*      */     public void exitRule(ParseTreeListener listener) {
/*  841 */       if (listener instanceof NginxListener) ((NginxListener)listener).exitRegexp(this); 
/*      */     }
/*      */     
/*      */     public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
/*  845 */       if (visitor instanceof NginxVisitor) return ((NginxVisitor)visitor).visitRegexp(this); 
/*  846 */       return visitor.visitChildren(this);
/*      */     } }
/*      */ 
/*      */   
/*      */   public final RegexpContext regexp() throws RecognitionException {
/*  851 */     RegexpContext _localctx = new RegexpContext(this._ctx, getState());
/*  852 */     enterRule(_localctx, 16, 8);
/*  853 */     _localctx.ret = "";
/*      */     
/*      */     try {
/*  856 */       enterOuterAlt(_localctx, 1);
/*      */       
/*  858 */       setState(151);
/*  859 */       this._errHandler.sync(this);
/*  860 */       int _alt = 1;
/*      */       do {
/*  862 */         switch (_alt) {
/*      */           
/*      */           case 1:
/*  865 */             setState(151);
/*  866 */             switch (this._input.LA(1)) {
/*      */               
/*      */               case 7:
/*  869 */                 setState(140);
/*  870 */                 _localctx.id = match(7);
/*  871 */                 _localctx.ret += (_localctx.id != null) ? _localctx.id.getText() : null;
/*      */                 break;
/*      */ 
/*      */               
/*      */               case 8:
/*  876 */                 setState(142);
/*  877 */                 _localctx.id = match(8);
/*  878 */                 _localctx.ret += (_localctx.id != null) ? _localctx.id.getText() : null;
/*      */                 break;
/*      */ 
/*      */               
/*      */               case 15:
/*  883 */                 setState(144);
/*  884 */                 _localctx.Value = match(15);
/*  885 */                 _localctx.ret += (_localctx.Value != null) ? _localctx.Value.getText() : null;
/*      */                 break;
/*      */ 
/*      */               
/*      */               case 5:
/*  890 */                 setState(146);
/*  891 */                 match(5);
/*  892 */                 setState(147);
/*  893 */                 _localctx.r = regexp();
/*  894 */                 _localctx.ret += "(".concat(_localctx.r.ret).concat(")");
/*  895 */                 setState(149);
/*  896 */                 match(6);
/*      */                 break;
/*      */             } 
/*      */             
/*  900 */             throw new NoViableAltException(this);
/*      */ 
/*      */ 
/*      */           
/*      */           default:
/*  905 */             throw new NoViableAltException(this);
/*      */         } 
/*  907 */         setState(153);
/*  908 */         this._errHandler.sync(this);
/*  909 */         _alt = getInterpreter().adaptivePredict(this._input, 16, this._ctx);
/*  910 */       } while (_alt != 2 && _alt != 0);
/*      */     
/*      */     }
/*  913 */     catch (RecognitionException re) {
/*  914 */       _localctx.exception = re;
/*  915 */       this._errHandler.reportError(this, re);
/*  916 */       this._errHandler.recover(this, re);
/*      */     } finally {
/*      */       
/*  919 */       exitRule();
/*      */     } 
/*  921 */     return _localctx;
/*      */   }
/*      */   public static class LocationBlockHeaderContext extends ParserRuleContext { public List<NgxToken> ret;
/*      */     public Token id;
/*      */     public Token Value;
/*      */     public NginxParser.RegexpContext regexp;
/*      */     
/*      */     public List<TerminalNode> Value() {
/*  929 */       return getTokens(15);
/*      */     } public TerminalNode Value(int i) {
/*  931 */       return getToken(15, i);
/*      */     }
/*      */     public NginxParser.RegexpContext regexp() {
/*  934 */       return getRuleContext(NginxParser.RegexpContext.class, 0);
/*      */     }
/*      */     public LocationBlockHeaderContext(ParserRuleContext parent, int invokingState) {
/*  937 */       super(parent, invokingState);
/*      */     } public int getRuleIndex() {
/*  939 */       return 9;
/*      */     }
/*      */     public void enterRule(ParseTreeListener listener) {
/*  942 */       if (listener instanceof NginxListener) ((NginxListener)listener).enterLocationBlockHeader(this); 
/*      */     }
/*      */     
/*      */     public void exitRule(ParseTreeListener listener) {
/*  946 */       if (listener instanceof NginxListener) ((NginxListener)listener).exitLocationBlockHeader(this); 
/*      */     }
/*      */     
/*      */     public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
/*  950 */       if (visitor instanceof NginxVisitor) return ((NginxVisitor)visitor).visitLocationBlockHeader(this); 
/*  951 */       return visitor.visitChildren(this);
/*      */     } }
/*      */ 
/*      */   
/*      */   public final LocationBlockHeaderContext locationBlockHeader() throws RecognitionException {
/*  956 */     LocationBlockHeaderContext _localctx = new LocationBlockHeaderContext(this._ctx, getState());
/*  957 */     enterRule(_localctx, 18, 9);
/*  958 */     _localctx.ret = new ArrayList<NgxToken>();
/*      */     try {
/*  960 */       enterOuterAlt(_localctx, 1);
/*      */       
/*  962 */       setState(155);
/*  963 */       _localctx.id = match(9);
/*  964 */       _localctx.ret.add(new NgxToken((_localctx.id != null) ? _localctx.id.getText() : null));
/*  965 */       setState(159);
/*  966 */       this._errHandler.sync(this);
/*  967 */       switch (getInterpreter().adaptivePredict(this._input, 17, this._ctx)) {
/*      */         
/*      */         case 1:
/*  970 */           setState(157);
/*  971 */           _localctx.Value = match(15);
/*  972 */           _localctx.ret.add(new NgxToken((_localctx.Value != null) ? _localctx.Value.getText() : null));
/*      */           break;
/*      */       } 
/*      */       
/*  976 */       setState(166);
/*  977 */       this._errHandler.sync(this);
/*  978 */       switch (getInterpreter().adaptivePredict(this._input, 18, this._ctx)) {
/*      */         
/*      */         case 1:
/*  981 */           setState(161);
/*  982 */           _localctx.Value = match(15);
/*  983 */           _localctx.ret.add(new NgxToken((_localctx.Value != null) ? _localctx.Value.getText() : null));
/*      */           break;
/*      */ 
/*      */         
/*      */         case 2:
/*  988 */           setState(163);
/*  989 */           _localctx.regexp = regexp();
/*  990 */           _localctx.ret.add(new NgxToken(_localctx.regexp.ret));
/*      */           break;
/*      */       } 
/*      */ 
/*      */ 
/*      */     
/*  996 */     } catch (RecognitionException re) {
/*  997 */       _localctx.exception = re;
/*  998 */       this._errHandler.reportError(this, re);
/*  999 */       this._errHandler.recover(this, re);
/*      */     } finally {
/*      */       
/* 1002 */       exitRule();
/*      */     } 
/* 1004 */     return _localctx;
/*      */   }
/*      */   public static class RewriteStatementContext extends ParserRuleContext { public NgxParam ret;
/*      */     public Token id;
/*      */     public Token Value;
/*      */     public NginxParser.RegexpContext regexp;
/*      */     public Token opt;
/*      */     
/*      */     public List<TerminalNode> Value() {
/* 1013 */       return getTokens(15);
/*      */     } public TerminalNode Value(int i) {
/* 1015 */       return getToken(15, i);
/*      */     }
/*      */     public NginxParser.RegexpContext regexp() {
/* 1018 */       return getRuleContext(NginxParser.RegexpContext.class, 0);
/*      */     }
/*      */     public RewriteStatementContext(ParserRuleContext parent, int invokingState) {
/* 1021 */       super(parent, invokingState);
/*      */     } public int getRuleIndex() {
/* 1023 */       return 10;
/*      */     }
/*      */     public void enterRule(ParseTreeListener listener) {
/* 1026 */       if (listener instanceof NginxListener) ((NginxListener)listener).enterRewriteStatement(this); 
/*      */     }
/*      */     
/*      */     public void exitRule(ParseTreeListener listener) {
/* 1030 */       if (listener instanceof NginxListener) ((NginxListener)listener).exitRewriteStatement(this); 
/*      */     }
/*      */     
/*      */     public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
/* 1034 */       if (visitor instanceof NginxVisitor) return ((NginxVisitor)visitor).visitRewriteStatement(this); 
/* 1035 */       return visitor.visitChildren(this);
/*      */     } }
/*      */ 
/*      */   
/*      */   public final RewriteStatementContext rewriteStatement() throws RecognitionException {
/* 1040 */     RewriteStatementContext _localctx = new RewriteStatementContext(this._ctx, getState());
/* 1041 */     enterRule(_localctx, 20, 10);
/* 1042 */     _localctx.ret = new NgxParam();
/*      */     
/*      */     try {
/* 1045 */       enterOuterAlt(_localctx, 1);
/*      */       
/* 1047 */       setState(168);
/* 1048 */       _localctx.id = match(10);
/* 1049 */       _localctx.ret.addValue((_localctx.id != null) ? _localctx.id.getText() : null);
/* 1050 */       setState(175);
/* 1051 */       this._errHandler.sync(this);
/* 1052 */       switch (getInterpreter().adaptivePredict(this._input, 19, this._ctx)) {
/*      */         
/*      */         case 1:
/* 1055 */           setState(170);
/* 1056 */           _localctx.Value = match(15);
/* 1057 */           _localctx.ret.addValue((_localctx.Value != null) ? _localctx.Value.getText() : null);
/*      */           break;
/*      */ 
/*      */         
/*      */         case 2:
/* 1062 */           setState(172);
/* 1063 */           _localctx.regexp = regexp();
/* 1064 */           _localctx.ret.addValue(_localctx.regexp.ret);
/*      */           break;
/*      */       } 
/*      */       
/* 1068 */       setState(177);
/* 1069 */       _localctx.Value = match(15);
/* 1070 */       _localctx.ret.addValue((_localctx.Value != null) ? _localctx.Value.getText() : null);
/* 1071 */       setState(181);
/* 1072 */       int _la = this._input.LA(1);
/* 1073 */       if ((_la & 0xFFFFFFC0) == 0 && (1L << _la & 0x7800L) != 0L)
/*      */       {
/* 1075 */         setState(179);
/* 1076 */         _localctx.opt = this._input.LT(1);
/* 1077 */         _la = this._input.LA(1);
/* 1078 */         if ((_la & 0xFFFFFFC0) != 0 || (1L << _la & 0x7800L) == 0L) {
/* 1079 */           _localctx.opt = this._errHandler.recoverInline(this);
/*      */         } else {
/* 1081 */           consume();
/*      */         } 
/* 1083 */         _localctx.ret.addValue((_localctx.opt != null) ? _localctx.opt.getText() : null);
/*      */       
/*      */       }
/*      */ 
/*      */     
/*      */     }
/* 1089 */     catch (RecognitionException re) {
/* 1090 */       _localctx.exception = re;
/* 1091 */       this._errHandler.reportError(this, re);
/* 1092 */       this._errHandler.recover(this, re);
/*      */     } finally {
/*      */       
/* 1095 */       exitRule();
/*      */     } 
/* 1097 */     return _localctx;
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
/* 1156 */   public static final ATN _ATN = (new ATNDeserializer()).deserialize("\003а훑舆괭䐗껱趀ꫝ\003\027º\004\002\t\002\004\003\t\003\004\004\t\004\004\005\t\005\004\006\t\006\004\007\t\007\004\b\t\b\004\t\t\t\004\n\t\n\004\013\t\013\004\f\t\f\003\002\003\002\003\002\003\002\003\002\003\002\003\002\003\002\006\002!\n\002\r\002\016\002\"\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\003\005\003.\n\003\003\003\003\003\003\004\003\004\003\004\003\004\003\004\003\004\003\004\007\0049\n\004\f\004\016\004<\013\004\003\005\003\005\003\005\003\005\003\005\003\006\003\006\003\006\003\006\003\006\003\006\005\006I\n\006\003\006\005\006L\n\006\003\006\003\006\003\006\003\006\003\006\003\006\003\006\003\006\003\006\003\006\003\006\003\006\007\006Z\n\006\f\006\016\006]\013\006\003\006\003\006\003\007\003\007\003\007\003\007\003\007\003\007\003\007\007\007h\n\007\f\007\016\007k\013\007\003\b\003\b\003\b\003\b\003\b\005\br\n\b\003\b\003\b\003\b\003\b\007\bx\n\b\f\b\016\b{\013\b\003\b\003\b\003\t\003\t\003\t\003\t\003\t\005\t\n\t\003\t\003\t\003\t\003\t\003\t\005\t\n\t\003\t\003\t\003\n\003\n\003\n\003\n\003\n\003\n\003\n\003\n\003\n\003\n\003\n\006\n\n\n\r\n\016\n\003\013\003\013\003\013\003\013\005\013¢\n\013\003\013\003\013\003\013\003\013\003\013\005\013©\n\013\003\f\003\f\003\f\003\f\003\f\003\f\003\f\005\f²\n\f\003\f\003\f\003\f\003\f\005\f¸\n\f\003\f\002\002\r\002\004\006\b\n\f\016\020\022\024\026\002\003\003\002\r\020Ê\002 \003\002\002\002\004-\003\002\002\002\0061\003\002\002\002\b=\003\002\002\002\nH\003\002\002\002\f`\003\002\002\002\016l\003\002\002\002\020~\003\002\002\002\022\003\002\002\002\024\003\002\002\002\026ª\003\002\002\002\030\031\005\004\003\002\031\032\b\002\001\002\032!\003\002\002\002\033\034\005\n\006\002\034\035\b\002\001\002\035!\003\002\002\002\036\037\007\023\002\002\037!\b\002\001\002 \030\003\002\002\002 \033\003\002\002\002 \036\003\002\002\002!\"\003\002\002\002\" \003\002\002\002\"#\003\002\002\002#\003\003\002\002\002$%\005\026\f\002%&\b\003\001\002&.\003\002\002\002'(\005\006\004\002()\b\003\001\002).\003\002\002\002*+\005\b\005\002+,\b\003\001\002,.\003\002\002\002-$\003\002\002\002-'\003\002\002\002-*\003\002\002\002./\003\002\002\002/0\007\003\002\0020\005\003\002\002\00212\007\021\002\0022:\b\004\001\00234\007\021\002\00249\b\004\001\00256\005\022\n\00267\b\004\001\00279\003\002\002\00283\003\002\002\00285\003\002\002\0029<\003\002\002\002:8\003\002\002\002:;\003\002\002\002;\007\003\002\002\002<:\003\002\002\002=>\007\024\002\002>?\b\005\001\002?@\007\021\002\002@A\b\005\001\002A\t\003\002\002\002BC\005\024\013\002CD\b\006\001\002DI\003\002\002\002EF\005\f\007\002FG\b\006\001\002GI\003\002\002\002HB\003\002\002\002HE\003\002\002\002IK\003\002\002\002JL\007\023\002\002KJ\003\002\002\002KL\003\002\002\002LM\003\002\002\002M[\007\004\002\002NO\005\004\003\002OP\b\006\001\002PZ\003\002\002\002QR\005\n\006\002RS\b\006\001\002SZ\003\002\002\002TU\005\016\b\002UV\b\006\001\002VZ\003\002\002\002WX\007\023\002\002XZ\b\006\001\002YN\003\002\002\002YQ\003\002\002\002YT\003\002\002\002YW\003\002\002\002Z]\003\002\002\002[Y\003\002\002\002[\\\003\002\002\002\\^\003\002\002\002][\003\002\002\002^_\007\005\002\002_\013\003\002\002\002`a\007\021\002\002ai\b\007\001\002bc\007\021\002\002ch\b\007\001\002de\005\022\n\002ef\b\007\001\002fh\003\002\002\002gb\003\002\002\002gd\003\002\002\002hk\003\002\002\002ig\003\002\002\002ij\003\002\002\002j\r\003\002\002\002ki\003\002\002\002lm\007\006\002\002mn\b\b\001\002no\005\020\t\002oq\b\b\001\002pr\007\023\002\002qp\003\002\002\002qr\003\002\002\002rs\003\002\002\002sy\007\004\002\002tu\005\004\003\002uv\b\b\001\002vx\003\002\002\002wt\003\002\002\002x{\003\002\002\002yw\003\002\002\002yz\003\002\002\002z|\003\002\002\002{y\003\002\002\002|}\007\005\002\002}\017\003\002\002\002~\007\007\002\002\007\021\002\002\b\t\001\002\007\021\002\002\b\t\001\002\003\002\002\002\003\002\002\002\003\002\002\002\007\021\002\002\b\t\001\002\005\022\n\002\b\t\001\002\003\002\002\002\003\002\002\002\003\002\002\002\003\002\002\002\003\002\002\002\007\b\002\002\021\003\002\002\002\007\t\002\002\b\n\001\002\007\n\002\002\b\n\001\002\007\021\002\002\b\n\001\002\007\007\002\002\005\022\n\002\b\n\001\002\007\b\002\002\003\002\002\002\003\002\002\002\003\002\002\002\003\002\002\002\003\002\002\002\003\002\002\002\003\002\002\002\003\002\002\002\023\003\002\002\002\007\013\002\002¡\b\013\001\002 \007\021\002\002 ¢\b\013\001\002¡\003\002\002\002¡¢\003\002\002\002¢¨\003\002\002\002£¤\007\021\002\002¤©\b\013\001\002¥¦\005\022\n\002¦§\b\013\001\002§©\003\002\002\002¨£\003\002\002\002¨¥\003\002\002\002©\025\003\002\002\002ª«\007\f\002\002«±\b\f\001\002¬­\007\021\002\002­²\b\f\001\002®¯\005\022\n\002¯°\b\f\001\002°²\003\002\002\002±¬\003\002\002\002±®\003\002\002\002²³\003\002\002\002³´\007\021\002\002´·\b\f\001\002µ¶\t\002\002\002¶¸\b\f\001\002·µ\003\002\002\002·¸\003\002\002\002¸\027\003\002\002\002\027 \"-8:HKY[giqy¡¨±·".toCharArray());
/*      */   
/*      */   static {
/* 1159 */     _decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
/* 1160 */     for (i = 0; i < _ATN.getNumberOfDecisions(); i++)
/* 1161 */       _decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i); 
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\github\odiszapc\nginxparser\antlr\NginxParser.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */