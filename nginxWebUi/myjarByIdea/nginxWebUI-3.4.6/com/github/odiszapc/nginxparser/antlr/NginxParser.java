package com.github.odiszapc.nginxparser.antlr;

import com.github.odiszapc.nginxparser.NgxBlock;
import com.github.odiszapc.nginxparser.NgxComment;
import com.github.odiszapc.nginxparser.NgxConfig;
import com.github.odiszapc.nginxparser.NgxIfBlock;
import com.github.odiszapc.nginxparser.NgxParam;
import com.github.odiszapc.nginxparser.NgxToken;
import java.util.ArrayList;
import java.util.List;
import org.antlr.v4.runtime.NoViableAltException;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.RuntimeMetaData;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.Vocabulary;
import org.antlr.v4.runtime.VocabularyImpl;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.ParserATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;
import org.antlr.v4.runtime.tree.TerminalNode;

public class NginxParser extends Parser {
   protected static final DFA[] _decisionToDFA;
   protected static final PredictionContextCache _sharedContextCache;
   public static final int T__0 = 1;
   public static final int T__1 = 2;
   public static final int T__2 = 3;
   public static final int T__3 = 4;
   public static final int T__4 = 5;
   public static final int T__5 = 6;
   public static final int T__6 = 7;
   public static final int T__7 = 8;
   public static final int T__8 = 9;
   public static final int T__9 = 10;
   public static final int T__10 = 11;
   public static final int T__11 = 12;
   public static final int T__12 = 13;
   public static final int T__13 = 14;
   public static final int Value = 15;
   public static final int STR_EXT = 16;
   public static final int Comment = 17;
   public static final int REGEXP_PREFIXED = 18;
   public static final int QUOTED_STRING = 19;
   public static final int SINGLE_QUOTED = 20;
   public static final int WS = 21;
   public static final int RULE_config = 0;
   public static final int RULE_statement = 1;
   public static final int RULE_genericStatement = 2;
   public static final int RULE_regexHeaderStatement = 3;
   public static final int RULE_block = 4;
   public static final int RULE_genericBlockHeader = 5;
   public static final int RULE_if_statement = 6;
   public static final int RULE_if_body = 7;
   public static final int RULE_regexp = 8;
   public static final int RULE_locationBlockHeader = 9;
   public static final int RULE_rewriteStatement = 10;
   public static final String[] ruleNames;
   private static final String[] _LITERAL_NAMES;
   private static final String[] _SYMBOLIC_NAMES;
   public static final Vocabulary VOCABULARY;
   /** @deprecated */
   @Deprecated
   public static final String[] tokenNames;
   public static final String _serializedATN = "\u0003а훑舆괭䐗껱趀ꫝ\u0003\u0017º\u0004\u0002\t\u0002\u0004\u0003\t\u0003\u0004\u0004\t\u0004\u0004\u0005\t\u0005\u0004\u0006\t\u0006\u0004\u0007\t\u0007\u0004\b\t\b\u0004\t\t\t\u0004\n\t\n\u0004\u000b\t\u000b\u0004\f\t\f\u0003\u0002\u0003\u0002\u0003\u0002\u0003\u0002\u0003\u0002\u0003\u0002\u0003\u0002\u0003\u0002\u0006\u0002!\n\u0002\r\u0002\u000e\u0002\"\u0003\u0003\u0003\u0003\u0003\u0003\u0003\u0003\u0003\u0003\u0003\u0003\u0003\u0003\u0003\u0003\u0003\u0003\u0005\u0003.\n\u0003\u0003\u0003\u0003\u0003\u0003\u0004\u0003\u0004\u0003\u0004\u0003\u0004\u0003\u0004\u0003\u0004\u0003\u0004\u0007\u00049\n\u0004\f\u0004\u000e\u0004<\u000b\u0004\u0003\u0005\u0003\u0005\u0003\u0005\u0003\u0005\u0003\u0005\u0003\u0006\u0003\u0006\u0003\u0006\u0003\u0006\u0003\u0006\u0003\u0006\u0005\u0006I\n\u0006\u0003\u0006\u0005\u0006L\n\u0006\u0003\u0006\u0003\u0006\u0003\u0006\u0003\u0006\u0003\u0006\u0003\u0006\u0003\u0006\u0003\u0006\u0003\u0006\u0003\u0006\u0003\u0006\u0003\u0006\u0007\u0006Z\n\u0006\f\u0006\u000e\u0006]\u000b\u0006\u0003\u0006\u0003\u0006\u0003\u0007\u0003\u0007\u0003\u0007\u0003\u0007\u0003\u0007\u0003\u0007\u0003\u0007\u0007\u0007h\n\u0007\f\u0007\u000e\u0007k\u000b\u0007\u0003\b\u0003\b\u0003\b\u0003\b\u0003\b\u0005\br\n\b\u0003\b\u0003\b\u0003\b\u0003\b\u0007\bx\n\b\f\b\u000e\b{\u000b\b\u0003\b\u0003\b\u0003\t\u0003\t\u0003\t\u0003\t\u0003\t\u0005\t\u0084\n\t\u0003\t\u0003\t\u0003\t\u0003\t\u0003\t\u0005\t\u008b\n\t\u0003\t\u0003\t\u0003\n\u0003\n\u0003\n\u0003\n\u0003\n\u0003\n\u0003\n\u0003\n\u0003\n\u0003\n\u0003\n\u0006\n\u009a\n\n\r\n\u000e\n\u009b\u0003\u000b\u0003\u000b\u0003\u000b\u0003\u000b\u0005\u000b¢\n\u000b\u0003\u000b\u0003\u000b\u0003\u000b\u0003\u000b\u0003\u000b\u0005\u000b©\n\u000b\u0003\f\u0003\f\u0003\f\u0003\f\u0003\f\u0003\f\u0003\f\u0005\f²\n\f\u0003\f\u0003\f\u0003\f\u0003\f\u0005\f¸\n\f\u0003\f\u0002\u0002\r\u0002\u0004\u0006\b\n\f\u000e\u0010\u0012\u0014\u0016\u0002\u0003\u0003\u0002\r\u0010Ê\u0002 \u0003\u0002\u0002\u0002\u0004-\u0003\u0002\u0002\u0002\u00061\u0003\u0002\u0002\u0002\b=\u0003\u0002\u0002\u0002\nH\u0003\u0002\u0002\u0002\f`\u0003\u0002\u0002\u0002\u000el\u0003\u0002\u0002\u0002\u0010~\u0003\u0002\u0002\u0002\u0012\u0099\u0003\u0002\u0002\u0002\u0014\u009d\u0003\u0002\u0002\u0002\u0016ª\u0003\u0002\u0002\u0002\u0018\u0019\u0005\u0004\u0003\u0002\u0019\u001a\b\u0002\u0001\u0002\u001a!\u0003\u0002\u0002\u0002\u001b\u001c\u0005\n\u0006\u0002\u001c\u001d\b\u0002\u0001\u0002\u001d!\u0003\u0002\u0002\u0002\u001e\u001f\u0007\u0013\u0002\u0002\u001f!\b\u0002\u0001\u0002 \u0018\u0003\u0002\u0002\u0002 \u001b\u0003\u0002\u0002\u0002 \u001e\u0003\u0002\u0002\u0002!\"\u0003\u0002\u0002\u0002\" \u0003\u0002\u0002\u0002\"#\u0003\u0002\u0002\u0002#\u0003\u0003\u0002\u0002\u0002$%\u0005\u0016\f\u0002%&\b\u0003\u0001\u0002&.\u0003\u0002\u0002\u0002'(\u0005\u0006\u0004\u0002()\b\u0003\u0001\u0002).\u0003\u0002\u0002\u0002*+\u0005\b\u0005\u0002+,\b\u0003\u0001\u0002,.\u0003\u0002\u0002\u0002-$\u0003\u0002\u0002\u0002-'\u0003\u0002\u0002\u0002-*\u0003\u0002\u0002\u0002./\u0003\u0002\u0002\u0002/0\u0007\u0003\u0002\u00020\u0005\u0003\u0002\u0002\u000212\u0007\u0011\u0002\u00022:\b\u0004\u0001\u000234\u0007\u0011\u0002\u000249\b\u0004\u0001\u000256\u0005\u0012\n\u000267\b\u0004\u0001\u000279\u0003\u0002\u0002\u000283\u0003\u0002\u0002\u000285\u0003\u0002\u0002\u00029<\u0003\u0002\u0002\u0002:8\u0003\u0002\u0002\u0002:;\u0003\u0002\u0002\u0002;\u0007\u0003\u0002\u0002\u0002<:\u0003\u0002\u0002\u0002=>\u0007\u0014\u0002\u0002>?\b\u0005\u0001\u0002?@\u0007\u0011\u0002\u0002@A\b\u0005\u0001\u0002A\t\u0003\u0002\u0002\u0002BC\u0005\u0014\u000b\u0002CD\b\u0006\u0001\u0002DI\u0003\u0002\u0002\u0002EF\u0005\f\u0007\u0002FG\b\u0006\u0001\u0002GI\u0003\u0002\u0002\u0002HB\u0003\u0002\u0002\u0002HE\u0003\u0002\u0002\u0002IK\u0003\u0002\u0002\u0002JL\u0007\u0013\u0002\u0002KJ\u0003\u0002\u0002\u0002KL\u0003\u0002\u0002\u0002LM\u0003\u0002\u0002\u0002M[\u0007\u0004\u0002\u0002NO\u0005\u0004\u0003\u0002OP\b\u0006\u0001\u0002PZ\u0003\u0002\u0002\u0002QR\u0005\n\u0006\u0002RS\b\u0006\u0001\u0002SZ\u0003\u0002\u0002\u0002TU\u0005\u000e\b\u0002UV\b\u0006\u0001\u0002VZ\u0003\u0002\u0002\u0002WX\u0007\u0013\u0002\u0002XZ\b\u0006\u0001\u0002YN\u0003\u0002\u0002\u0002YQ\u0003\u0002\u0002\u0002YT\u0003\u0002\u0002\u0002YW\u0003\u0002\u0002\u0002Z]\u0003\u0002\u0002\u0002[Y\u0003\u0002\u0002\u0002[\\\u0003\u0002\u0002\u0002\\^\u0003\u0002\u0002\u0002][\u0003\u0002\u0002\u0002^_\u0007\u0005\u0002\u0002_\u000b\u0003\u0002\u0002\u0002`a\u0007\u0011\u0002\u0002ai\b\u0007\u0001\u0002bc\u0007\u0011\u0002\u0002ch\b\u0007\u0001\u0002de\u0005\u0012\n\u0002ef\b\u0007\u0001\u0002fh\u0003\u0002\u0002\u0002gb\u0003\u0002\u0002\u0002gd\u0003\u0002\u0002\u0002hk\u0003\u0002\u0002\u0002ig\u0003\u0002\u0002\u0002ij\u0003\u0002\u0002\u0002j\r\u0003\u0002\u0002\u0002ki\u0003\u0002\u0002\u0002lm\u0007\u0006\u0002\u0002mn\b\b\u0001\u0002no\u0005\u0010\t\u0002oq\b\b\u0001\u0002pr\u0007\u0013\u0002\u0002qp\u0003\u0002\u0002\u0002qr\u0003\u0002\u0002\u0002rs\u0003\u0002\u0002\u0002sy\u0007\u0004\u0002\u0002tu\u0005\u0004\u0003\u0002uv\b\b\u0001\u0002vx\u0003\u0002\u0002\u0002wt\u0003\u0002\u0002\u0002x{\u0003\u0002\u0002\u0002yw\u0003\u0002\u0002\u0002yz\u0003\u0002\u0002\u0002z|\u0003\u0002\u0002\u0002{y\u0003\u0002\u0002\u0002|}\u0007\u0005\u0002\u0002}\u000f\u0003\u0002\u0002\u0002~\u007f\u0007\u0007\u0002\u0002\u007f\u0080\u0007\u0011\u0002\u0002\u0080\u0083\b\t\u0001\u0002\u0081\u0082\u0007\u0011\u0002\u0002\u0082\u0084\b\t\u0001\u0002\u0083\u0081\u0003\u0002\u0002\u0002\u0083\u0084\u0003\u0002\u0002\u0002\u0084\u008a\u0003\u0002\u0002\u0002\u0085\u0086\u0007\u0011\u0002\u0002\u0086\u008b\b\t\u0001\u0002\u0087\u0088\u0005\u0012\n\u0002\u0088\u0089\b\t\u0001\u0002\u0089\u008b\u0003\u0002\u0002\u0002\u008a\u0085\u0003\u0002\u0002\u0002\u008a\u0087\u0003\u0002\u0002\u0002\u008a\u008b\u0003\u0002\u0002\u0002\u008b\u008c\u0003\u0002\u0002\u0002\u008c\u008d\u0007\b\u0002\u0002\u008d\u0011\u0003\u0002\u0002\u0002\u008e\u008f\u0007\t\u0002\u0002\u008f\u009a\b\n\u0001\u0002\u0090\u0091\u0007\n\u0002\u0002\u0091\u009a\b\n\u0001\u0002\u0092\u0093\u0007\u0011\u0002\u0002\u0093\u009a\b\n\u0001\u0002\u0094\u0095\u0007\u0007\u0002\u0002\u0095\u0096\u0005\u0012\n\u0002\u0096\u0097\b\n\u0001\u0002\u0097\u0098\u0007\b\u0002\u0002\u0098\u009a\u0003\u0002\u0002\u0002\u0099\u008e\u0003\u0002\u0002\u0002\u0099\u0090\u0003\u0002\u0002\u0002\u0099\u0092\u0003\u0002\u0002\u0002\u0099\u0094\u0003\u0002\u0002\u0002\u009a\u009b\u0003\u0002\u0002\u0002\u009b\u0099\u0003\u0002\u0002\u0002\u009b\u009c\u0003\u0002\u0002\u0002\u009c\u0013\u0003\u0002\u0002\u0002\u009d\u009e\u0007\u000b\u0002\u0002\u009e¡\b\u000b\u0001\u0002\u009f \u0007\u0011\u0002\u0002 ¢\b\u000b\u0001\u0002¡\u009f\u0003\u0002\u0002\u0002¡¢\u0003\u0002\u0002\u0002¢¨\u0003\u0002\u0002\u0002£¤\u0007\u0011\u0002\u0002¤©\b\u000b\u0001\u0002¥¦\u0005\u0012\n\u0002¦§\b\u000b\u0001\u0002§©\u0003\u0002\u0002\u0002¨£\u0003\u0002\u0002\u0002¨¥\u0003\u0002\u0002\u0002©\u0015\u0003\u0002\u0002\u0002ª«\u0007\f\u0002\u0002«±\b\f\u0001\u0002¬\u00ad\u0007\u0011\u0002\u0002\u00ad²\b\f\u0001\u0002®¯\u0005\u0012\n\u0002¯°\b\f\u0001\u0002°²\u0003\u0002\u0002\u0002±¬\u0003\u0002\u0002\u0002±®\u0003\u0002\u0002\u0002²³\u0003\u0002\u0002\u0002³´\u0007\u0011\u0002\u0002´·\b\f\u0001\u0002µ¶\t\u0002\u0002\u0002¶¸\b\f\u0001\u0002·µ\u0003\u0002\u0002\u0002·¸\u0003\u0002\u0002\u0002¸\u0017\u0003\u0002\u0002\u0002\u0017 \"-8:HKY[giqy\u0083\u008a\u0099\u009b¡¨±·";
   public static final ATN _ATN;

   /** @deprecated */
   @Deprecated
   public String[] getTokenNames() {
      return tokenNames;
   }

   public Vocabulary getVocabulary() {
      return VOCABULARY;
   }

   public String getGrammarFileName() {
      return "Nginx.g4";
   }

   public String[] getRuleNames() {
      return ruleNames;
   }

   public String getSerializedATN() {
      return "\u0003а훑舆괭䐗껱趀ꫝ\u0003\u0017º\u0004\u0002\t\u0002\u0004\u0003\t\u0003\u0004\u0004\t\u0004\u0004\u0005\t\u0005\u0004\u0006\t\u0006\u0004\u0007\t\u0007\u0004\b\t\b\u0004\t\t\t\u0004\n\t\n\u0004\u000b\t\u000b\u0004\f\t\f\u0003\u0002\u0003\u0002\u0003\u0002\u0003\u0002\u0003\u0002\u0003\u0002\u0003\u0002\u0003\u0002\u0006\u0002!\n\u0002\r\u0002\u000e\u0002\"\u0003\u0003\u0003\u0003\u0003\u0003\u0003\u0003\u0003\u0003\u0003\u0003\u0003\u0003\u0003\u0003\u0003\u0003\u0005\u0003.\n\u0003\u0003\u0003\u0003\u0003\u0003\u0004\u0003\u0004\u0003\u0004\u0003\u0004\u0003\u0004\u0003\u0004\u0003\u0004\u0007\u00049\n\u0004\f\u0004\u000e\u0004<\u000b\u0004\u0003\u0005\u0003\u0005\u0003\u0005\u0003\u0005\u0003\u0005\u0003\u0006\u0003\u0006\u0003\u0006\u0003\u0006\u0003\u0006\u0003\u0006\u0005\u0006I\n\u0006\u0003\u0006\u0005\u0006L\n\u0006\u0003\u0006\u0003\u0006\u0003\u0006\u0003\u0006\u0003\u0006\u0003\u0006\u0003\u0006\u0003\u0006\u0003\u0006\u0003\u0006\u0003\u0006\u0003\u0006\u0007\u0006Z\n\u0006\f\u0006\u000e\u0006]\u000b\u0006\u0003\u0006\u0003\u0006\u0003\u0007\u0003\u0007\u0003\u0007\u0003\u0007\u0003\u0007\u0003\u0007\u0003\u0007\u0007\u0007h\n\u0007\f\u0007\u000e\u0007k\u000b\u0007\u0003\b\u0003\b\u0003\b\u0003\b\u0003\b\u0005\br\n\b\u0003\b\u0003\b\u0003\b\u0003\b\u0007\bx\n\b\f\b\u000e\b{\u000b\b\u0003\b\u0003\b\u0003\t\u0003\t\u0003\t\u0003\t\u0003\t\u0005\t\u0084\n\t\u0003\t\u0003\t\u0003\t\u0003\t\u0003\t\u0005\t\u008b\n\t\u0003\t\u0003\t\u0003\n\u0003\n\u0003\n\u0003\n\u0003\n\u0003\n\u0003\n\u0003\n\u0003\n\u0003\n\u0003\n\u0006\n\u009a\n\n\r\n\u000e\n\u009b\u0003\u000b\u0003\u000b\u0003\u000b\u0003\u000b\u0005\u000b¢\n\u000b\u0003\u000b\u0003\u000b\u0003\u000b\u0003\u000b\u0003\u000b\u0005\u000b©\n\u000b\u0003\f\u0003\f\u0003\f\u0003\f\u0003\f\u0003\f\u0003\f\u0005\f²\n\f\u0003\f\u0003\f\u0003\f\u0003\f\u0005\f¸\n\f\u0003\f\u0002\u0002\r\u0002\u0004\u0006\b\n\f\u000e\u0010\u0012\u0014\u0016\u0002\u0003\u0003\u0002\r\u0010Ê\u0002 \u0003\u0002\u0002\u0002\u0004-\u0003\u0002\u0002\u0002\u00061\u0003\u0002\u0002\u0002\b=\u0003\u0002\u0002\u0002\nH\u0003\u0002\u0002\u0002\f`\u0003\u0002\u0002\u0002\u000el\u0003\u0002\u0002\u0002\u0010~\u0003\u0002\u0002\u0002\u0012\u0099\u0003\u0002\u0002\u0002\u0014\u009d\u0003\u0002\u0002\u0002\u0016ª\u0003\u0002\u0002\u0002\u0018\u0019\u0005\u0004\u0003\u0002\u0019\u001a\b\u0002\u0001\u0002\u001a!\u0003\u0002\u0002\u0002\u001b\u001c\u0005\n\u0006\u0002\u001c\u001d\b\u0002\u0001\u0002\u001d!\u0003\u0002\u0002\u0002\u001e\u001f\u0007\u0013\u0002\u0002\u001f!\b\u0002\u0001\u0002 \u0018\u0003\u0002\u0002\u0002 \u001b\u0003\u0002\u0002\u0002 \u001e\u0003\u0002\u0002\u0002!\"\u0003\u0002\u0002\u0002\" \u0003\u0002\u0002\u0002\"#\u0003\u0002\u0002\u0002#\u0003\u0003\u0002\u0002\u0002$%\u0005\u0016\f\u0002%&\b\u0003\u0001\u0002&.\u0003\u0002\u0002\u0002'(\u0005\u0006\u0004\u0002()\b\u0003\u0001\u0002).\u0003\u0002\u0002\u0002*+\u0005\b\u0005\u0002+,\b\u0003\u0001\u0002,.\u0003\u0002\u0002\u0002-$\u0003\u0002\u0002\u0002-'\u0003\u0002\u0002\u0002-*\u0003\u0002\u0002\u0002./\u0003\u0002\u0002\u0002/0\u0007\u0003\u0002\u00020\u0005\u0003\u0002\u0002\u000212\u0007\u0011\u0002\u00022:\b\u0004\u0001\u000234\u0007\u0011\u0002\u000249\b\u0004\u0001\u000256\u0005\u0012\n\u000267\b\u0004\u0001\u000279\u0003\u0002\u0002\u000283\u0003\u0002\u0002\u000285\u0003\u0002\u0002\u00029<\u0003\u0002\u0002\u0002:8\u0003\u0002\u0002\u0002:;\u0003\u0002\u0002\u0002;\u0007\u0003\u0002\u0002\u0002<:\u0003\u0002\u0002\u0002=>\u0007\u0014\u0002\u0002>?\b\u0005\u0001\u0002?@\u0007\u0011\u0002\u0002@A\b\u0005\u0001\u0002A\t\u0003\u0002\u0002\u0002BC\u0005\u0014\u000b\u0002CD\b\u0006\u0001\u0002DI\u0003\u0002\u0002\u0002EF\u0005\f\u0007\u0002FG\b\u0006\u0001\u0002GI\u0003\u0002\u0002\u0002HB\u0003\u0002\u0002\u0002HE\u0003\u0002\u0002\u0002IK\u0003\u0002\u0002\u0002JL\u0007\u0013\u0002\u0002KJ\u0003\u0002\u0002\u0002KL\u0003\u0002\u0002\u0002LM\u0003\u0002\u0002\u0002M[\u0007\u0004\u0002\u0002NO\u0005\u0004\u0003\u0002OP\b\u0006\u0001\u0002PZ\u0003\u0002\u0002\u0002QR\u0005\n\u0006\u0002RS\b\u0006\u0001\u0002SZ\u0003\u0002\u0002\u0002TU\u0005\u000e\b\u0002UV\b\u0006\u0001\u0002VZ\u0003\u0002\u0002\u0002WX\u0007\u0013\u0002\u0002XZ\b\u0006\u0001\u0002YN\u0003\u0002\u0002\u0002YQ\u0003\u0002\u0002\u0002YT\u0003\u0002\u0002\u0002YW\u0003\u0002\u0002\u0002Z]\u0003\u0002\u0002\u0002[Y\u0003\u0002\u0002\u0002[\\\u0003\u0002\u0002\u0002\\^\u0003\u0002\u0002\u0002][\u0003\u0002\u0002\u0002^_\u0007\u0005\u0002\u0002_\u000b\u0003\u0002\u0002\u0002`a\u0007\u0011\u0002\u0002ai\b\u0007\u0001\u0002bc\u0007\u0011\u0002\u0002ch\b\u0007\u0001\u0002de\u0005\u0012\n\u0002ef\b\u0007\u0001\u0002fh\u0003\u0002\u0002\u0002gb\u0003\u0002\u0002\u0002gd\u0003\u0002\u0002\u0002hk\u0003\u0002\u0002\u0002ig\u0003\u0002\u0002\u0002ij\u0003\u0002\u0002\u0002j\r\u0003\u0002\u0002\u0002ki\u0003\u0002\u0002\u0002lm\u0007\u0006\u0002\u0002mn\b\b\u0001\u0002no\u0005\u0010\t\u0002oq\b\b\u0001\u0002pr\u0007\u0013\u0002\u0002qp\u0003\u0002\u0002\u0002qr\u0003\u0002\u0002\u0002rs\u0003\u0002\u0002\u0002sy\u0007\u0004\u0002\u0002tu\u0005\u0004\u0003\u0002uv\b\b\u0001\u0002vx\u0003\u0002\u0002\u0002wt\u0003\u0002\u0002\u0002x{\u0003\u0002\u0002\u0002yw\u0003\u0002\u0002\u0002yz\u0003\u0002\u0002\u0002z|\u0003\u0002\u0002\u0002{y\u0003\u0002\u0002\u0002|}\u0007\u0005\u0002\u0002}\u000f\u0003\u0002\u0002\u0002~\u007f\u0007\u0007\u0002\u0002\u007f\u0080\u0007\u0011\u0002\u0002\u0080\u0083\b\t\u0001\u0002\u0081\u0082\u0007\u0011\u0002\u0002\u0082\u0084\b\t\u0001\u0002\u0083\u0081\u0003\u0002\u0002\u0002\u0083\u0084\u0003\u0002\u0002\u0002\u0084\u008a\u0003\u0002\u0002\u0002\u0085\u0086\u0007\u0011\u0002\u0002\u0086\u008b\b\t\u0001\u0002\u0087\u0088\u0005\u0012\n\u0002\u0088\u0089\b\t\u0001\u0002\u0089\u008b\u0003\u0002\u0002\u0002\u008a\u0085\u0003\u0002\u0002\u0002\u008a\u0087\u0003\u0002\u0002\u0002\u008a\u008b\u0003\u0002\u0002\u0002\u008b\u008c\u0003\u0002\u0002\u0002\u008c\u008d\u0007\b\u0002\u0002\u008d\u0011\u0003\u0002\u0002\u0002\u008e\u008f\u0007\t\u0002\u0002\u008f\u009a\b\n\u0001\u0002\u0090\u0091\u0007\n\u0002\u0002\u0091\u009a\b\n\u0001\u0002\u0092\u0093\u0007\u0011\u0002\u0002\u0093\u009a\b\n\u0001\u0002\u0094\u0095\u0007\u0007\u0002\u0002\u0095\u0096\u0005\u0012\n\u0002\u0096\u0097\b\n\u0001\u0002\u0097\u0098\u0007\b\u0002\u0002\u0098\u009a\u0003\u0002\u0002\u0002\u0099\u008e\u0003\u0002\u0002\u0002\u0099\u0090\u0003\u0002\u0002\u0002\u0099\u0092\u0003\u0002\u0002\u0002\u0099\u0094\u0003\u0002\u0002\u0002\u009a\u009b\u0003\u0002\u0002\u0002\u009b\u0099\u0003\u0002\u0002\u0002\u009b\u009c\u0003\u0002\u0002\u0002\u009c\u0013\u0003\u0002\u0002\u0002\u009d\u009e\u0007\u000b\u0002\u0002\u009e¡\b\u000b\u0001\u0002\u009f \u0007\u0011\u0002\u0002 ¢\b\u000b\u0001\u0002¡\u009f\u0003\u0002\u0002\u0002¡¢\u0003\u0002\u0002\u0002¢¨\u0003\u0002\u0002\u0002£¤\u0007\u0011\u0002\u0002¤©\b\u000b\u0001\u0002¥¦\u0005\u0012\n\u0002¦§\b\u000b\u0001\u0002§©\u0003\u0002\u0002\u0002¨£\u0003\u0002\u0002\u0002¨¥\u0003\u0002\u0002\u0002©\u0015\u0003\u0002\u0002\u0002ª«\u0007\f\u0002\u0002«±\b\f\u0001\u0002¬\u00ad\u0007\u0011\u0002\u0002\u00ad²\b\f\u0001\u0002®¯\u0005\u0012\n\u0002¯°\b\f\u0001\u0002°²\u0003\u0002\u0002\u0002±¬\u0003\u0002\u0002\u0002±®\u0003\u0002\u0002\u0002²³\u0003\u0002\u0002\u0002³´\u0007\u0011\u0002\u0002´·\b\f\u0001\u0002µ¶\t\u0002\u0002\u0002¶¸\b\f\u0001\u0002·µ\u0003\u0002\u0002\u0002·¸\u0003\u0002\u0002\u0002¸\u0017\u0003\u0002\u0002\u0002\u0017 \"-8:HKY[giqy\u0083\u008a\u0099\u009b¡¨±·";
   }

   public ATN getATN() {
      return _ATN;
   }

   public NginxParser(TokenStream input) {
      super(input);
      this._interp = new ParserATNSimulator(this, _ATN, _decisionToDFA, _sharedContextCache);
   }

   public final ConfigContext config() throws RecognitionException {
      ConfigContext _localctx = new ConfigContext(this._ctx, this.getState());
      this.enterRule(_localctx, 0, 0);
      _localctx.ret = new NgxConfig();

      try {
         this.enterOuterAlt(_localctx, 1);
         this.setState(30);
         this._errHandler.sync(this);
         int _la = this._input.LA(1);

         do {
            this.setState(30);
            this._errHandler.sync(this);
            switch (((ParserATNSimulator)this.getInterpreter()).adaptivePredict(this._input, 0, this._ctx)) {
               case 1:
                  this.setState(22);
                  _localctx.statement = this.statement();
                  _localctx.ret.addEntry(_localctx.statement.ret);
                  break;
               case 2:
                  this.setState(25);
                  _localctx.block = this.block();
                  _localctx.ret.addEntry(_localctx.block.ret);
                  break;
               case 3:
                  this.setState(28);
                  _localctx.Comment = this.match(17);
                  _localctx.ret.addEntry(new NgxComment(_localctx.Comment != null ? _localctx.Comment.getText() : null));
            }

            this.setState(32);
            this._errHandler.sync(this);
            _la = this._input.LA(1);
         } while((_la & -64) == 0 && (1L << _la & 427520L) != 0L);
      } catch (RecognitionException var7) {
         _localctx.exception = var7;
         this._errHandler.reportError(this, var7);
         this._errHandler.recover(this, var7);
      } finally {
         this.exitRule();
      }

      return _localctx;
   }

   public final StatementContext statement() throws RecognitionException {
      StatementContext _localctx = new StatementContext(this._ctx, this.getState());
      this.enterRule(_localctx, 2, 1);

      try {
         this.enterOuterAlt(_localctx, 1);
         this.setState(43);
         switch (this._input.LA(1)) {
            case 10:
               this.setState(34);
               _localctx.rewriteStatement = this.rewriteStatement();
               _localctx.ret = _localctx.rewriteStatement.ret;
               break;
            case 15:
               this.setState(37);
               _localctx.genericStatement = this.genericStatement();
               _localctx.ret = _localctx.genericStatement.ret;
               break;
            case 18:
               this.setState(40);
               _localctx.regexHeaderStatement = this.regexHeaderStatement();
               _localctx.ret = _localctx.regexHeaderStatement.ret;
               break;
            default:
               throw new NoViableAltException(this);
         }

         this.setState(45);
         this.match(1);
      } catch (RecognitionException var6) {
         _localctx.exception = var6;
         this._errHandler.reportError(this, var6);
         this._errHandler.recover(this, var6);
      } finally {
         this.exitRule();
      }

      return _localctx;
   }

   public final GenericStatementContext genericStatement() throws RecognitionException {
      GenericStatementContext _localctx = new GenericStatementContext(this._ctx, this.getState());
      this.enterRule(_localctx, 4, 2);
      _localctx.ret = new NgxParam();

      try {
         this.enterOuterAlt(_localctx, 1);
         this.setState(47);
         _localctx.Value = this.match(15);
         _localctx.ret.addValue(_localctx.Value != null ? _localctx.Value.getText() : null);
         this.setState(56);
         this._errHandler.sync(this);

         for(int _la = this._input.LA(1); (_la & -64) == 0 && (1L << _la & 33184L) != 0L; _la = this._input.LA(1)) {
            this.setState(54);
            this._errHandler.sync(this);
            switch (((ParserATNSimulator)this.getInterpreter()).adaptivePredict(this._input, 3, this._ctx)) {
               case 1:
                  this.setState(49);
                  _localctx.Value = this.match(15);
                  _localctx.ret.addValue(_localctx.Value != null ? _localctx.Value.getText() : null);
                  break;
               case 2:
                  this.setState(51);
                  _localctx.r = this.regexp();
                  _localctx.ret.addValue(_localctx.r.ret);
            }

            this.setState(58);
            this._errHandler.sync(this);
         }
      } catch (RecognitionException var7) {
         _localctx.exception = var7;
         this._errHandler.reportError(this, var7);
         this._errHandler.recover(this, var7);
      } finally {
         this.exitRule();
      }

      return _localctx;
   }

   public final RegexHeaderStatementContext regexHeaderStatement() throws RecognitionException {
      RegexHeaderStatementContext _localctx = new RegexHeaderStatementContext(this._ctx, this.getState());
      this.enterRule(_localctx, 6, 3);
      _localctx.ret = new NgxParam();

      try {
         this.enterOuterAlt(_localctx, 1);
         this.setState(59);
         _localctx.REGEXP_PREFIXED = this.match(18);
         _localctx.ret.addValue(_localctx.REGEXP_PREFIXED != null ? _localctx.REGEXP_PREFIXED.getText() : null);
         this.setState(61);
         _localctx.Value = this.match(15);
         _localctx.ret.addValue(_localctx.Value != null ? _localctx.Value.getText() : null);
      } catch (RecognitionException var6) {
         _localctx.exception = var6;
         this._errHandler.reportError(this, var6);
         this._errHandler.recover(this, var6);
      } finally {
         this.exitRule();
      }

      return _localctx;
   }

   public final BlockContext block() throws RecognitionException {
      BlockContext _localctx = new BlockContext(this._ctx, this.getState());
      this.enterRule(_localctx, 8, 4);
      _localctx.ret = new NgxBlock();

      try {
         this.enterOuterAlt(_localctx, 1);
         this.setState(70);
         switch (this._input.LA(1)) {
            case 9:
               this.setState(64);
               _localctx.locationBlockHeader = this.locationBlockHeader();
               _localctx.ret.getTokens().addAll(_localctx.locationBlockHeader.ret);
               break;
            case 15:
               this.setState(67);
               _localctx.genericBlockHeader = this.genericBlockHeader();
               _localctx.ret.getTokens().addAll(_localctx.genericBlockHeader.ret);
               break;
            default:
               throw new NoViableAltException(this);
         }

         this.setState(73);
         int _la = this._input.LA(1);
         if (_la == 17) {
            this.setState(72);
            _localctx.Comment = this.match(17);
         }

         this.setState(75);
         this.match(2);
         this.setState(89);
         this._errHandler.sync(this);

         for(_la = this._input.LA(1); (_la & -64) == 0 && (1L << _la & 427536L) != 0L; _la = this._input.LA(1)) {
            this.setState(87);
            this._errHandler.sync(this);
            switch (((ParserATNSimulator)this.getInterpreter()).adaptivePredict(this._input, 7, this._ctx)) {
               case 1:
                  this.setState(76);
                  _localctx.statement = this.statement();
                  _localctx.ret.addEntry(_localctx.statement.ret);
                  break;
               case 2:
                  this.setState(79);
                  _localctx.b = this.block();
                  _localctx.ret.addEntry(_localctx.b.ret);
                  break;
               case 3:
                  this.setState(82);
                  _localctx.if_statement = this.if_statement();
                  _localctx.ret.addEntry(_localctx.if_statement.ret);
                  break;
               case 4:
                  this.setState(85);
                  _localctx.Comment = this.match(17);
                  _localctx.ret.addEntry(new NgxComment(_localctx.Comment != null ? _localctx.Comment.getText() : null));
            }

            this.setState(91);
            this._errHandler.sync(this);
         }

         this.setState(92);
         this.match(3);
      } catch (RecognitionException var7) {
         _localctx.exception = var7;
         this._errHandler.reportError(this, var7);
         this._errHandler.recover(this, var7);
      } finally {
         this.exitRule();
      }

      return _localctx;
   }

   public final GenericBlockHeaderContext genericBlockHeader() throws RecognitionException {
      GenericBlockHeaderContext _localctx = new GenericBlockHeaderContext(this._ctx, this.getState());
      this.enterRule(_localctx, 10, 5);
      _localctx.ret = new ArrayList();

      try {
         this.enterOuterAlt(_localctx, 1);
         this.setState(94);
         _localctx.Value = this.match(15);
         _localctx.ret.add(new NgxToken(_localctx.Value != null ? _localctx.Value.getText() : null));
         this.setState(103);
         this._errHandler.sync(this);

         for(int _la = this._input.LA(1); (_la & -64) == 0 && (1L << _la & 33184L) != 0L; _la = this._input.LA(1)) {
            this.setState(101);
            this._errHandler.sync(this);
            switch (((ParserATNSimulator)this.getInterpreter()).adaptivePredict(this._input, 9, this._ctx)) {
               case 1:
                  this.setState(96);
                  _localctx.Value = this.match(15);
                  _localctx.ret.add(new NgxToken(_localctx.Value != null ? _localctx.Value.getText() : null));
                  break;
               case 2:
                  this.setState(98);
                  _localctx.regexp = this.regexp();
                  _localctx.ret.add(new NgxToken(_localctx.regexp.ret));
            }

            this.setState(105);
            this._errHandler.sync(this);
         }
      } catch (RecognitionException var7) {
         _localctx.exception = var7;
         this._errHandler.reportError(this, var7);
         this._errHandler.recover(this, var7);
      } finally {
         this.exitRule();
      }

      return _localctx;
   }

   public final If_statementContext if_statement() throws RecognitionException {
      If_statementContext _localctx = new If_statementContext(this._ctx, this.getState());
      this.enterRule(_localctx, 12, 6);
      _localctx.ret = new NgxIfBlock();

      try {
         this.enterOuterAlt(_localctx, 1);
         this.setState(106);
         _localctx.id = this.match(4);
         _localctx.ret.addValue(new NgxToken(_localctx.id != null ? _localctx.id.getText() : null));
         this.setState(108);
         _localctx.if_body = this.if_body();
         _localctx.ret.getTokens().addAll(_localctx.if_body.ret);
         this.setState(111);
         int _la = this._input.LA(1);
         if (_la == 17) {
            this.setState(110);
            this.match(17);
         }

         this.setState(113);
         this.match(2);
         this.setState(119);
         this._errHandler.sync(this);

         for(_la = this._input.LA(1); (_la & -64) == 0 && (1L << _la & 295936L) != 0L; _la = this._input.LA(1)) {
            this.setState(114);
            _localctx.statement = this.statement();
            _localctx.ret.addEntry(_localctx.statement.ret);
            this.setState(121);
            this._errHandler.sync(this);
         }

         this.setState(122);
         this.match(3);
      } catch (RecognitionException var7) {
         _localctx.exception = var7;
         this._errHandler.reportError(this, var7);
         this._errHandler.recover(this, var7);
      } finally {
         this.exitRule();
      }

      return _localctx;
   }

   public final If_bodyContext if_body() throws RecognitionException {
      If_bodyContext _localctx = new If_bodyContext(this._ctx, this.getState());
      this.enterRule(_localctx, 14, 7);
      _localctx.ret = new ArrayList();

      try {
         this.enterOuterAlt(_localctx, 1);
         this.setState(124);
         this.match(5);
         this.setState(125);
         _localctx.Value = this.match(15);
         _localctx.ret.add(new NgxToken(_localctx.Value != null ? _localctx.Value.getText() : null));
         this.setState(129);
         this._errHandler.sync(this);
         switch (((ParserATNSimulator)this.getInterpreter()).adaptivePredict(this._input, 13, this._ctx)) {
            case 1:
               this.setState(127);
               _localctx.Value = this.match(15);
               _localctx.ret.add(new NgxToken(_localctx.Value != null ? _localctx.Value.getText() : null));
            default:
               this.setState(136);
               this._errHandler.sync(this);
               switch (((ParserATNSimulator)this.getInterpreter()).adaptivePredict(this._input, 14, this._ctx)) {
                  case 1:
                     this.setState(131);
                     _localctx.Value = this.match(15);
                     _localctx.ret.add(new NgxToken(_localctx.Value != null ? _localctx.Value.getText() : null));
                     break;
                  case 2:
                     this.setState(133);
                     _localctx.regexp = this.regexp();
                     _localctx.ret.add(new NgxToken(_localctx.regexp.ret));
               }

               this.setState(138);
               this.match(6);
         }
      } catch (RecognitionException var6) {
         _localctx.exception = var6;
         this._errHandler.reportError(this, var6);
         this._errHandler.recover(this, var6);
      } finally {
         this.exitRule();
      }

      return _localctx;
   }

   public final RegexpContext regexp() throws RecognitionException {
      RegexpContext _localctx = new RegexpContext(this._ctx, this.getState());
      this.enterRule(_localctx, 16, 8);
      _localctx.ret = "";

      try {
         this.enterOuterAlt(_localctx, 1);
         this.setState(151);
         this._errHandler.sync(this);
         int _alt = 1;

         do {
            switch (_alt) {
               case 1:
                  this.setState(151);
                  switch (this._input.LA(1)) {
                     case 5:
                        this.setState(146);
                        this.match(5);
                        this.setState(147);
                        _localctx.r = this.regexp();
                        _localctx.ret = _localctx.ret + "(".concat(_localctx.r.ret).concat(")");
                        this.setState(149);
                        this.match(6);
                        break;
                     case 7:
                        this.setState(140);
                        _localctx.id = this.match(7);
                        _localctx.ret = _localctx.ret + (_localctx.id != null ? _localctx.id.getText() : null);
                        break;
                     case 8:
                        this.setState(142);
                        _localctx.id = this.match(8);
                        _localctx.ret = _localctx.ret + (_localctx.id != null ? _localctx.id.getText() : null);
                        break;
                     case 15:
                        this.setState(144);
                        _localctx.Value = this.match(15);
                        _localctx.ret = _localctx.ret + (_localctx.Value != null ? _localctx.Value.getText() : null);
                        break;
                     default:
                        throw new NoViableAltException(this);
                  }

                  this.setState(153);
                  this._errHandler.sync(this);
                  _alt = ((ParserATNSimulator)this.getInterpreter()).adaptivePredict(this._input, 16, this._ctx);
                  break;
               default:
                  throw new NoViableAltException(this);
            }
         } while(_alt != 2 && _alt != 0);
      } catch (RecognitionException var6) {
         _localctx.exception = var6;
         this._errHandler.reportError(this, var6);
         this._errHandler.recover(this, var6);
      } finally {
         this.exitRule();
      }

      return _localctx;
   }

   public final LocationBlockHeaderContext locationBlockHeader() throws RecognitionException {
      LocationBlockHeaderContext _localctx = new LocationBlockHeaderContext(this._ctx, this.getState());
      this.enterRule(_localctx, 18, 9);
      _localctx.ret = new ArrayList();

      try {
         this.enterOuterAlt(_localctx, 1);
         this.setState(155);
         _localctx.id = this.match(9);
         _localctx.ret.add(new NgxToken(_localctx.id != null ? _localctx.id.getText() : null));
         this.setState(159);
         this._errHandler.sync(this);
         switch (((ParserATNSimulator)this.getInterpreter()).adaptivePredict(this._input, 17, this._ctx)) {
            case 1:
               this.setState(157);
               _localctx.Value = this.match(15);
               _localctx.ret.add(new NgxToken(_localctx.Value != null ? _localctx.Value.getText() : null));
            default:
               this.setState(166);
               this._errHandler.sync(this);
               switch (((ParserATNSimulator)this.getInterpreter()).adaptivePredict(this._input, 18, this._ctx)) {
                  case 1:
                     this.setState(161);
                     _localctx.Value = this.match(15);
                     _localctx.ret.add(new NgxToken(_localctx.Value != null ? _localctx.Value.getText() : null));
                     break;
                  case 2:
                     this.setState(163);
                     _localctx.regexp = this.regexp();
                     _localctx.ret.add(new NgxToken(_localctx.regexp.ret));
               }
         }
      } catch (RecognitionException var6) {
         _localctx.exception = var6;
         this._errHandler.reportError(this, var6);
         this._errHandler.recover(this, var6);
      } finally {
         this.exitRule();
      }

      return _localctx;
   }

   public final RewriteStatementContext rewriteStatement() throws RecognitionException {
      RewriteStatementContext _localctx = new RewriteStatementContext(this._ctx, this.getState());
      this.enterRule(_localctx, 20, 10);
      _localctx.ret = new NgxParam();

      try {
         this.enterOuterAlt(_localctx, 1);
         this.setState(168);
         _localctx.id = this.match(10);
         _localctx.ret.addValue(_localctx.id != null ? _localctx.id.getText() : null);
         this.setState(175);
         this._errHandler.sync(this);
         switch (((ParserATNSimulator)this.getInterpreter()).adaptivePredict(this._input, 19, this._ctx)) {
            case 1:
               this.setState(170);
               _localctx.Value = this.match(15);
               _localctx.ret.addValue(_localctx.Value != null ? _localctx.Value.getText() : null);
               break;
            case 2:
               this.setState(172);
               _localctx.regexp = this.regexp();
               _localctx.ret.addValue(_localctx.regexp.ret);
         }

         this.setState(177);
         _localctx.Value = this.match(15);
         _localctx.ret.addValue(_localctx.Value != null ? _localctx.Value.getText() : null);
         this.setState(181);
         int _la = this._input.LA(1);
         if ((_la & -64) == 0 && (1L << _la & 30720L) != 0L) {
            this.setState(179);
            _localctx.opt = this._input.LT(1);
            _la = this._input.LA(1);
            if ((_la & -64) == 0 && (1L << _la & 30720L) != 0L) {
               this.consume();
            } else {
               _localctx.opt = this._errHandler.recoverInline(this);
            }

            _localctx.ret.addValue(_localctx.opt != null ? _localctx.opt.getText() : null);
         }
      } catch (RecognitionException var7) {
         _localctx.exception = var7;
         this._errHandler.reportError(this, var7);
         this._errHandler.recover(this, var7);
      } finally {
         this.exitRule();
      }

      return _localctx;
   }

   static {
      RuntimeMetaData.checkVersion("4.5.3", "4.5.3");
      _sharedContextCache = new PredictionContextCache();
      ruleNames = new String[]{"config", "statement", "genericStatement", "regexHeaderStatement", "block", "genericBlockHeader", "if_statement", "if_body", "regexp", "locationBlockHeader", "rewriteStatement"};
      _LITERAL_NAMES = new String[]{null, "';'", "'{'", "'}'", "'if'", "'('", "')'", "'\\.'", "'^'", "'location'", "'rewrite'", "'last'", "'break'", "'redirect'", "'permanent'"};
      _SYMBOLIC_NAMES = new String[]{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, "Value", "STR_EXT", "Comment", "REGEXP_PREFIXED", "QUOTED_STRING", "SINGLE_QUOTED", "WS"};
      VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);
      tokenNames = new String[_SYMBOLIC_NAMES.length];

      int i;
      for(i = 0; i < tokenNames.length; ++i) {
         tokenNames[i] = VOCABULARY.getLiteralName(i);
         if (tokenNames[i] == null) {
            tokenNames[i] = VOCABULARY.getSymbolicName(i);
         }

         if (tokenNames[i] == null) {
            tokenNames[i] = "<INVALID>";
         }
      }

      _ATN = (new ATNDeserializer()).deserialize("\u0003а훑舆괭䐗껱趀ꫝ\u0003\u0017º\u0004\u0002\t\u0002\u0004\u0003\t\u0003\u0004\u0004\t\u0004\u0004\u0005\t\u0005\u0004\u0006\t\u0006\u0004\u0007\t\u0007\u0004\b\t\b\u0004\t\t\t\u0004\n\t\n\u0004\u000b\t\u000b\u0004\f\t\f\u0003\u0002\u0003\u0002\u0003\u0002\u0003\u0002\u0003\u0002\u0003\u0002\u0003\u0002\u0003\u0002\u0006\u0002!\n\u0002\r\u0002\u000e\u0002\"\u0003\u0003\u0003\u0003\u0003\u0003\u0003\u0003\u0003\u0003\u0003\u0003\u0003\u0003\u0003\u0003\u0003\u0003\u0005\u0003.\n\u0003\u0003\u0003\u0003\u0003\u0003\u0004\u0003\u0004\u0003\u0004\u0003\u0004\u0003\u0004\u0003\u0004\u0003\u0004\u0007\u00049\n\u0004\f\u0004\u000e\u0004<\u000b\u0004\u0003\u0005\u0003\u0005\u0003\u0005\u0003\u0005\u0003\u0005\u0003\u0006\u0003\u0006\u0003\u0006\u0003\u0006\u0003\u0006\u0003\u0006\u0005\u0006I\n\u0006\u0003\u0006\u0005\u0006L\n\u0006\u0003\u0006\u0003\u0006\u0003\u0006\u0003\u0006\u0003\u0006\u0003\u0006\u0003\u0006\u0003\u0006\u0003\u0006\u0003\u0006\u0003\u0006\u0003\u0006\u0007\u0006Z\n\u0006\f\u0006\u000e\u0006]\u000b\u0006\u0003\u0006\u0003\u0006\u0003\u0007\u0003\u0007\u0003\u0007\u0003\u0007\u0003\u0007\u0003\u0007\u0003\u0007\u0007\u0007h\n\u0007\f\u0007\u000e\u0007k\u000b\u0007\u0003\b\u0003\b\u0003\b\u0003\b\u0003\b\u0005\br\n\b\u0003\b\u0003\b\u0003\b\u0003\b\u0007\bx\n\b\f\b\u000e\b{\u000b\b\u0003\b\u0003\b\u0003\t\u0003\t\u0003\t\u0003\t\u0003\t\u0005\t\u0084\n\t\u0003\t\u0003\t\u0003\t\u0003\t\u0003\t\u0005\t\u008b\n\t\u0003\t\u0003\t\u0003\n\u0003\n\u0003\n\u0003\n\u0003\n\u0003\n\u0003\n\u0003\n\u0003\n\u0003\n\u0003\n\u0006\n\u009a\n\n\r\n\u000e\n\u009b\u0003\u000b\u0003\u000b\u0003\u000b\u0003\u000b\u0005\u000b¢\n\u000b\u0003\u000b\u0003\u000b\u0003\u000b\u0003\u000b\u0003\u000b\u0005\u000b©\n\u000b\u0003\f\u0003\f\u0003\f\u0003\f\u0003\f\u0003\f\u0003\f\u0005\f²\n\f\u0003\f\u0003\f\u0003\f\u0003\f\u0005\f¸\n\f\u0003\f\u0002\u0002\r\u0002\u0004\u0006\b\n\f\u000e\u0010\u0012\u0014\u0016\u0002\u0003\u0003\u0002\r\u0010Ê\u0002 \u0003\u0002\u0002\u0002\u0004-\u0003\u0002\u0002\u0002\u00061\u0003\u0002\u0002\u0002\b=\u0003\u0002\u0002\u0002\nH\u0003\u0002\u0002\u0002\f`\u0003\u0002\u0002\u0002\u000el\u0003\u0002\u0002\u0002\u0010~\u0003\u0002\u0002\u0002\u0012\u0099\u0003\u0002\u0002\u0002\u0014\u009d\u0003\u0002\u0002\u0002\u0016ª\u0003\u0002\u0002\u0002\u0018\u0019\u0005\u0004\u0003\u0002\u0019\u001a\b\u0002\u0001\u0002\u001a!\u0003\u0002\u0002\u0002\u001b\u001c\u0005\n\u0006\u0002\u001c\u001d\b\u0002\u0001\u0002\u001d!\u0003\u0002\u0002\u0002\u001e\u001f\u0007\u0013\u0002\u0002\u001f!\b\u0002\u0001\u0002 \u0018\u0003\u0002\u0002\u0002 \u001b\u0003\u0002\u0002\u0002 \u001e\u0003\u0002\u0002\u0002!\"\u0003\u0002\u0002\u0002\" \u0003\u0002\u0002\u0002\"#\u0003\u0002\u0002\u0002#\u0003\u0003\u0002\u0002\u0002$%\u0005\u0016\f\u0002%&\b\u0003\u0001\u0002&.\u0003\u0002\u0002\u0002'(\u0005\u0006\u0004\u0002()\b\u0003\u0001\u0002).\u0003\u0002\u0002\u0002*+\u0005\b\u0005\u0002+,\b\u0003\u0001\u0002,.\u0003\u0002\u0002\u0002-$\u0003\u0002\u0002\u0002-'\u0003\u0002\u0002\u0002-*\u0003\u0002\u0002\u0002./\u0003\u0002\u0002\u0002/0\u0007\u0003\u0002\u00020\u0005\u0003\u0002\u0002\u000212\u0007\u0011\u0002\u00022:\b\u0004\u0001\u000234\u0007\u0011\u0002\u000249\b\u0004\u0001\u000256\u0005\u0012\n\u000267\b\u0004\u0001\u000279\u0003\u0002\u0002\u000283\u0003\u0002\u0002\u000285\u0003\u0002\u0002\u00029<\u0003\u0002\u0002\u0002:8\u0003\u0002\u0002\u0002:;\u0003\u0002\u0002\u0002;\u0007\u0003\u0002\u0002\u0002<:\u0003\u0002\u0002\u0002=>\u0007\u0014\u0002\u0002>?\b\u0005\u0001\u0002?@\u0007\u0011\u0002\u0002@A\b\u0005\u0001\u0002A\t\u0003\u0002\u0002\u0002BC\u0005\u0014\u000b\u0002CD\b\u0006\u0001\u0002DI\u0003\u0002\u0002\u0002EF\u0005\f\u0007\u0002FG\b\u0006\u0001\u0002GI\u0003\u0002\u0002\u0002HB\u0003\u0002\u0002\u0002HE\u0003\u0002\u0002\u0002IK\u0003\u0002\u0002\u0002JL\u0007\u0013\u0002\u0002KJ\u0003\u0002\u0002\u0002KL\u0003\u0002\u0002\u0002LM\u0003\u0002\u0002\u0002M[\u0007\u0004\u0002\u0002NO\u0005\u0004\u0003\u0002OP\b\u0006\u0001\u0002PZ\u0003\u0002\u0002\u0002QR\u0005\n\u0006\u0002RS\b\u0006\u0001\u0002SZ\u0003\u0002\u0002\u0002TU\u0005\u000e\b\u0002UV\b\u0006\u0001\u0002VZ\u0003\u0002\u0002\u0002WX\u0007\u0013\u0002\u0002XZ\b\u0006\u0001\u0002YN\u0003\u0002\u0002\u0002YQ\u0003\u0002\u0002\u0002YT\u0003\u0002\u0002\u0002YW\u0003\u0002\u0002\u0002Z]\u0003\u0002\u0002\u0002[Y\u0003\u0002\u0002\u0002[\\\u0003\u0002\u0002\u0002\\^\u0003\u0002\u0002\u0002][\u0003\u0002\u0002\u0002^_\u0007\u0005\u0002\u0002_\u000b\u0003\u0002\u0002\u0002`a\u0007\u0011\u0002\u0002ai\b\u0007\u0001\u0002bc\u0007\u0011\u0002\u0002ch\b\u0007\u0001\u0002de\u0005\u0012\n\u0002ef\b\u0007\u0001\u0002fh\u0003\u0002\u0002\u0002gb\u0003\u0002\u0002\u0002gd\u0003\u0002\u0002\u0002hk\u0003\u0002\u0002\u0002ig\u0003\u0002\u0002\u0002ij\u0003\u0002\u0002\u0002j\r\u0003\u0002\u0002\u0002ki\u0003\u0002\u0002\u0002lm\u0007\u0006\u0002\u0002mn\b\b\u0001\u0002no\u0005\u0010\t\u0002oq\b\b\u0001\u0002pr\u0007\u0013\u0002\u0002qp\u0003\u0002\u0002\u0002qr\u0003\u0002\u0002\u0002rs\u0003\u0002\u0002\u0002sy\u0007\u0004\u0002\u0002tu\u0005\u0004\u0003\u0002uv\b\b\u0001\u0002vx\u0003\u0002\u0002\u0002wt\u0003\u0002\u0002\u0002x{\u0003\u0002\u0002\u0002yw\u0003\u0002\u0002\u0002yz\u0003\u0002\u0002\u0002z|\u0003\u0002\u0002\u0002{y\u0003\u0002\u0002\u0002|}\u0007\u0005\u0002\u0002}\u000f\u0003\u0002\u0002\u0002~\u007f\u0007\u0007\u0002\u0002\u007f\u0080\u0007\u0011\u0002\u0002\u0080\u0083\b\t\u0001\u0002\u0081\u0082\u0007\u0011\u0002\u0002\u0082\u0084\b\t\u0001\u0002\u0083\u0081\u0003\u0002\u0002\u0002\u0083\u0084\u0003\u0002\u0002\u0002\u0084\u008a\u0003\u0002\u0002\u0002\u0085\u0086\u0007\u0011\u0002\u0002\u0086\u008b\b\t\u0001\u0002\u0087\u0088\u0005\u0012\n\u0002\u0088\u0089\b\t\u0001\u0002\u0089\u008b\u0003\u0002\u0002\u0002\u008a\u0085\u0003\u0002\u0002\u0002\u008a\u0087\u0003\u0002\u0002\u0002\u008a\u008b\u0003\u0002\u0002\u0002\u008b\u008c\u0003\u0002\u0002\u0002\u008c\u008d\u0007\b\u0002\u0002\u008d\u0011\u0003\u0002\u0002\u0002\u008e\u008f\u0007\t\u0002\u0002\u008f\u009a\b\n\u0001\u0002\u0090\u0091\u0007\n\u0002\u0002\u0091\u009a\b\n\u0001\u0002\u0092\u0093\u0007\u0011\u0002\u0002\u0093\u009a\b\n\u0001\u0002\u0094\u0095\u0007\u0007\u0002\u0002\u0095\u0096\u0005\u0012\n\u0002\u0096\u0097\b\n\u0001\u0002\u0097\u0098\u0007\b\u0002\u0002\u0098\u009a\u0003\u0002\u0002\u0002\u0099\u008e\u0003\u0002\u0002\u0002\u0099\u0090\u0003\u0002\u0002\u0002\u0099\u0092\u0003\u0002\u0002\u0002\u0099\u0094\u0003\u0002\u0002\u0002\u009a\u009b\u0003\u0002\u0002\u0002\u009b\u0099\u0003\u0002\u0002\u0002\u009b\u009c\u0003\u0002\u0002\u0002\u009c\u0013\u0003\u0002\u0002\u0002\u009d\u009e\u0007\u000b\u0002\u0002\u009e¡\b\u000b\u0001\u0002\u009f \u0007\u0011\u0002\u0002 ¢\b\u000b\u0001\u0002¡\u009f\u0003\u0002\u0002\u0002¡¢\u0003\u0002\u0002\u0002¢¨\u0003\u0002\u0002\u0002£¤\u0007\u0011\u0002\u0002¤©\b\u000b\u0001\u0002¥¦\u0005\u0012\n\u0002¦§\b\u000b\u0001\u0002§©\u0003\u0002\u0002\u0002¨£\u0003\u0002\u0002\u0002¨¥\u0003\u0002\u0002\u0002©\u0015\u0003\u0002\u0002\u0002ª«\u0007\f\u0002\u0002«±\b\f\u0001\u0002¬\u00ad\u0007\u0011\u0002\u0002\u00ad²\b\f\u0001\u0002®¯\u0005\u0012\n\u0002¯°\b\f\u0001\u0002°²\u0003\u0002\u0002\u0002±¬\u0003\u0002\u0002\u0002±®\u0003\u0002\u0002\u0002²³\u0003\u0002\u0002\u0002³´\u0007\u0011\u0002\u0002´·\b\f\u0001\u0002µ¶\t\u0002\u0002\u0002¶¸\b\f\u0001\u0002·µ\u0003\u0002\u0002\u0002·¸\u0003\u0002\u0002\u0002¸\u0017\u0003\u0002\u0002\u0002\u0017 \"-8:HKY[giqy\u0083\u008a\u0099\u009b¡¨±·".toCharArray());
      _decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];

      for(i = 0; i < _ATN.getNumberOfDecisions(); ++i) {
         _decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
      }

   }

   public static class RewriteStatementContext extends ParserRuleContext {
      public NgxParam ret;
      public Token id;
      public Token Value;
      public RegexpContext regexp;
      public Token opt;

      public List<TerminalNode> Value() {
         return this.getTokens(15);
      }

      public TerminalNode Value(int i) {
         return this.getToken(15, i);
      }

      public RegexpContext regexp() {
         return (RegexpContext)this.getRuleContext(RegexpContext.class, 0);
      }

      public RewriteStatementContext(ParserRuleContext parent, int invokingState) {
         super(parent, invokingState);
      }

      public int getRuleIndex() {
         return 10;
      }

      public void enterRule(ParseTreeListener listener) {
         if (listener instanceof NginxListener) {
            ((NginxListener)listener).enterRewriteStatement(this);
         }

      }

      public void exitRule(ParseTreeListener listener) {
         if (listener instanceof NginxListener) {
            ((NginxListener)listener).exitRewriteStatement(this);
         }

      }

      public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
         return visitor instanceof NginxVisitor ? ((NginxVisitor)visitor).visitRewriteStatement(this) : visitor.visitChildren(this);
      }
   }

   public static class LocationBlockHeaderContext extends ParserRuleContext {
      public List<NgxToken> ret;
      public Token id;
      public Token Value;
      public RegexpContext regexp;

      public List<TerminalNode> Value() {
         return this.getTokens(15);
      }

      public TerminalNode Value(int i) {
         return this.getToken(15, i);
      }

      public RegexpContext regexp() {
         return (RegexpContext)this.getRuleContext(RegexpContext.class, 0);
      }

      public LocationBlockHeaderContext(ParserRuleContext parent, int invokingState) {
         super(parent, invokingState);
      }

      public int getRuleIndex() {
         return 9;
      }

      public void enterRule(ParseTreeListener listener) {
         if (listener instanceof NginxListener) {
            ((NginxListener)listener).enterLocationBlockHeader(this);
         }

      }

      public void exitRule(ParseTreeListener listener) {
         if (listener instanceof NginxListener) {
            ((NginxListener)listener).exitLocationBlockHeader(this);
         }

      }

      public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
         return visitor instanceof NginxVisitor ? ((NginxVisitor)visitor).visitLocationBlockHeader(this) : visitor.visitChildren(this);
      }
   }

   public static class RegexpContext extends ParserRuleContext {
      public String ret;
      public Token id;
      public Token Value;
      public RegexpContext r;

      public List<TerminalNode> Value() {
         return this.getTokens(15);
      }

      public TerminalNode Value(int i) {
         return this.getToken(15, i);
      }

      public List<RegexpContext> regexp() {
         return this.getRuleContexts(RegexpContext.class);
      }

      public RegexpContext regexp(int i) {
         return (RegexpContext)this.getRuleContext(RegexpContext.class, i);
      }

      public RegexpContext(ParserRuleContext parent, int invokingState) {
         super(parent, invokingState);
      }

      public int getRuleIndex() {
         return 8;
      }

      public void enterRule(ParseTreeListener listener) {
         if (listener instanceof NginxListener) {
            ((NginxListener)listener).enterRegexp(this);
         }

      }

      public void exitRule(ParseTreeListener listener) {
         if (listener instanceof NginxListener) {
            ((NginxListener)listener).exitRegexp(this);
         }

      }

      public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
         return visitor instanceof NginxVisitor ? ((NginxVisitor)visitor).visitRegexp(this) : visitor.visitChildren(this);
      }
   }

   public static class If_bodyContext extends ParserRuleContext {
      public List<NgxToken> ret;
      public Token Value;
      public RegexpContext regexp;

      public List<TerminalNode> Value() {
         return this.getTokens(15);
      }

      public TerminalNode Value(int i) {
         return this.getToken(15, i);
      }

      public RegexpContext regexp() {
         return (RegexpContext)this.getRuleContext(RegexpContext.class, 0);
      }

      public If_bodyContext(ParserRuleContext parent, int invokingState) {
         super(parent, invokingState);
      }

      public int getRuleIndex() {
         return 7;
      }

      public void enterRule(ParseTreeListener listener) {
         if (listener instanceof NginxListener) {
            ((NginxListener)listener).enterIf_body(this);
         }

      }

      public void exitRule(ParseTreeListener listener) {
         if (listener instanceof NginxListener) {
            ((NginxListener)listener).exitIf_body(this);
         }

      }

      public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
         return visitor instanceof NginxVisitor ? ((NginxVisitor)visitor).visitIf_body(this) : visitor.visitChildren(this);
      }
   }

   public static class If_statementContext extends ParserRuleContext {
      public NgxIfBlock ret;
      public Token id;
      public If_bodyContext if_body;
      public StatementContext statement;

      public If_bodyContext if_body() {
         return (If_bodyContext)this.getRuleContext(If_bodyContext.class, 0);
      }

      public TerminalNode Comment() {
         return this.getToken(17, 0);
      }

      public List<StatementContext> statement() {
         return this.getRuleContexts(StatementContext.class);
      }

      public StatementContext statement(int i) {
         return (StatementContext)this.getRuleContext(StatementContext.class, i);
      }

      public If_statementContext(ParserRuleContext parent, int invokingState) {
         super(parent, invokingState);
      }

      public int getRuleIndex() {
         return 6;
      }

      public void enterRule(ParseTreeListener listener) {
         if (listener instanceof NginxListener) {
            ((NginxListener)listener).enterIf_statement(this);
         }

      }

      public void exitRule(ParseTreeListener listener) {
         if (listener instanceof NginxListener) {
            ((NginxListener)listener).exitIf_statement(this);
         }

      }

      public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
         return visitor instanceof NginxVisitor ? ((NginxVisitor)visitor).visitIf_statement(this) : visitor.visitChildren(this);
      }
   }

   public static class GenericBlockHeaderContext extends ParserRuleContext {
      public List<NgxToken> ret;
      public Token Value;
      public RegexpContext regexp;

      public List<TerminalNode> Value() {
         return this.getTokens(15);
      }

      public TerminalNode Value(int i) {
         return this.getToken(15, i);
      }

      public List<RegexpContext> regexp() {
         return this.getRuleContexts(RegexpContext.class);
      }

      public RegexpContext regexp(int i) {
         return (RegexpContext)this.getRuleContext(RegexpContext.class, i);
      }

      public GenericBlockHeaderContext(ParserRuleContext parent, int invokingState) {
         super(parent, invokingState);
      }

      public int getRuleIndex() {
         return 5;
      }

      public void enterRule(ParseTreeListener listener) {
         if (listener instanceof NginxListener) {
            ((NginxListener)listener).enterGenericBlockHeader(this);
         }

      }

      public void exitRule(ParseTreeListener listener) {
         if (listener instanceof NginxListener) {
            ((NginxListener)listener).exitGenericBlockHeader(this);
         }

      }

      public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
         return visitor instanceof NginxVisitor ? ((NginxVisitor)visitor).visitGenericBlockHeader(this) : visitor.visitChildren(this);
      }
   }

   public static class BlockContext extends ParserRuleContext {
      public NgxBlock ret;
      public LocationBlockHeaderContext locationBlockHeader;
      public GenericBlockHeaderContext genericBlockHeader;
      public Token Comment;
      public StatementContext statement;
      public BlockContext b;
      public If_statementContext if_statement;

      public LocationBlockHeaderContext locationBlockHeader() {
         return (LocationBlockHeaderContext)this.getRuleContext(LocationBlockHeaderContext.class, 0);
      }

      public GenericBlockHeaderContext genericBlockHeader() {
         return (GenericBlockHeaderContext)this.getRuleContext(GenericBlockHeaderContext.class, 0);
      }

      public List<TerminalNode> Comment() {
         return this.getTokens(17);
      }

      public TerminalNode Comment(int i) {
         return this.getToken(17, i);
      }

      public List<StatementContext> statement() {
         return this.getRuleContexts(StatementContext.class);
      }

      public StatementContext statement(int i) {
         return (StatementContext)this.getRuleContext(StatementContext.class, i);
      }

      public List<If_statementContext> if_statement() {
         return this.getRuleContexts(If_statementContext.class);
      }

      public If_statementContext if_statement(int i) {
         return (If_statementContext)this.getRuleContext(If_statementContext.class, i);
      }

      public List<BlockContext> block() {
         return this.getRuleContexts(BlockContext.class);
      }

      public BlockContext block(int i) {
         return (BlockContext)this.getRuleContext(BlockContext.class, i);
      }

      public BlockContext(ParserRuleContext parent, int invokingState) {
         super(parent, invokingState);
      }

      public int getRuleIndex() {
         return 4;
      }

      public void enterRule(ParseTreeListener listener) {
         if (listener instanceof NginxListener) {
            ((NginxListener)listener).enterBlock(this);
         }

      }

      public void exitRule(ParseTreeListener listener) {
         if (listener instanceof NginxListener) {
            ((NginxListener)listener).exitBlock(this);
         }

      }

      public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
         return visitor instanceof NginxVisitor ? ((NginxVisitor)visitor).visitBlock(this) : visitor.visitChildren(this);
      }
   }

   public static class RegexHeaderStatementContext extends ParserRuleContext {
      public NgxParam ret;
      public Token REGEXP_PREFIXED;
      public Token Value;

      public TerminalNode REGEXP_PREFIXED() {
         return this.getToken(18, 0);
      }

      public TerminalNode Value() {
         return this.getToken(15, 0);
      }

      public RegexHeaderStatementContext(ParserRuleContext parent, int invokingState) {
         super(parent, invokingState);
      }

      public int getRuleIndex() {
         return 3;
      }

      public void enterRule(ParseTreeListener listener) {
         if (listener instanceof NginxListener) {
            ((NginxListener)listener).enterRegexHeaderStatement(this);
         }

      }

      public void exitRule(ParseTreeListener listener) {
         if (listener instanceof NginxListener) {
            ((NginxListener)listener).exitRegexHeaderStatement(this);
         }

      }

      public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
         return visitor instanceof NginxVisitor ? ((NginxVisitor)visitor).visitRegexHeaderStatement(this) : visitor.visitChildren(this);
      }
   }

   public static class GenericStatementContext extends ParserRuleContext {
      public NgxParam ret;
      public Token Value;
      public RegexpContext r;

      public List<TerminalNode> Value() {
         return this.getTokens(15);
      }

      public TerminalNode Value(int i) {
         return this.getToken(15, i);
      }

      public List<RegexpContext> regexp() {
         return this.getRuleContexts(RegexpContext.class);
      }

      public RegexpContext regexp(int i) {
         return (RegexpContext)this.getRuleContext(RegexpContext.class, i);
      }

      public GenericStatementContext(ParserRuleContext parent, int invokingState) {
         super(parent, invokingState);
      }

      public int getRuleIndex() {
         return 2;
      }

      public void enterRule(ParseTreeListener listener) {
         if (listener instanceof NginxListener) {
            ((NginxListener)listener).enterGenericStatement(this);
         }

      }

      public void exitRule(ParseTreeListener listener) {
         if (listener instanceof NginxListener) {
            ((NginxListener)listener).exitGenericStatement(this);
         }

      }

      public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
         return visitor instanceof NginxVisitor ? ((NginxVisitor)visitor).visitGenericStatement(this) : visitor.visitChildren(this);
      }
   }

   public static class StatementContext extends ParserRuleContext {
      public NgxParam ret;
      public RewriteStatementContext rewriteStatement;
      public GenericStatementContext genericStatement;
      public RegexHeaderStatementContext regexHeaderStatement;

      public RewriteStatementContext rewriteStatement() {
         return (RewriteStatementContext)this.getRuleContext(RewriteStatementContext.class, 0);
      }

      public GenericStatementContext genericStatement() {
         return (GenericStatementContext)this.getRuleContext(GenericStatementContext.class, 0);
      }

      public RegexHeaderStatementContext regexHeaderStatement() {
         return (RegexHeaderStatementContext)this.getRuleContext(RegexHeaderStatementContext.class, 0);
      }

      public StatementContext(ParserRuleContext parent, int invokingState) {
         super(parent, invokingState);
      }

      public int getRuleIndex() {
         return 1;
      }

      public void enterRule(ParseTreeListener listener) {
         if (listener instanceof NginxListener) {
            ((NginxListener)listener).enterStatement(this);
         }

      }

      public void exitRule(ParseTreeListener listener) {
         if (listener instanceof NginxListener) {
            ((NginxListener)listener).exitStatement(this);
         }

      }

      public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
         return visitor instanceof NginxVisitor ? ((NginxVisitor)visitor).visitStatement(this) : visitor.visitChildren(this);
      }
   }

   public static class ConfigContext extends ParserRuleContext {
      public NgxConfig ret;
      public StatementContext statement;
      public BlockContext block;
      public Token Comment;

      public List<StatementContext> statement() {
         return this.getRuleContexts(StatementContext.class);
      }

      public StatementContext statement(int i) {
         return (StatementContext)this.getRuleContext(StatementContext.class, i);
      }

      public List<BlockContext> block() {
         return this.getRuleContexts(BlockContext.class);
      }

      public BlockContext block(int i) {
         return (BlockContext)this.getRuleContext(BlockContext.class, i);
      }

      public List<TerminalNode> Comment() {
         return this.getTokens(17);
      }

      public TerminalNode Comment(int i) {
         return this.getToken(17, i);
      }

      public ConfigContext(ParserRuleContext parent, int invokingState) {
         super(parent, invokingState);
      }

      public int getRuleIndex() {
         return 0;
      }

      public void enterRule(ParseTreeListener listener) {
         if (listener instanceof NginxListener) {
            ((NginxListener)listener).enterConfig(this);
         }

      }

      public void exitRule(ParseTreeListener listener) {
         if (listener instanceof NginxListener) {
            ((NginxListener)listener).exitConfig(this);
         }

      }

      public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
         return visitor instanceof NginxVisitor ? ((NginxVisitor)visitor).visitConfig(this) : visitor.visitChildren(this);
      }
   }
}
