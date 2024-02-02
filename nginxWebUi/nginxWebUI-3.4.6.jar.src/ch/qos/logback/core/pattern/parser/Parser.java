/*     */ package ch.qos.logback.core.pattern.parser;
/*     */ 
/*     */ import ch.qos.logback.core.pattern.Converter;
/*     */ import ch.qos.logback.core.pattern.FormatInfo;
/*     */ import ch.qos.logback.core.pattern.IdentityCompositeConverter;
/*     */ import ch.qos.logback.core.pattern.ReplacingCompositeConverter;
/*     */ import ch.qos.logback.core.pattern.util.IEscapeUtil;
/*     */ import ch.qos.logback.core.pattern.util.RegularEscapeUtil;
/*     */ import ch.qos.logback.core.spi.ContextAwareBase;
/*     */ import ch.qos.logback.core.spi.ScanException;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Parser<E>
/*     */   extends ContextAwareBase
/*     */ {
/*     */   public static final String MISSING_RIGHT_PARENTHESIS = "http://logback.qos.ch/codes.html#missingRightParenthesis";
/*  47 */   public static final Map<String, String> DEFAULT_COMPOSITE_CONVERTER_MAP = new HashMap<String, String>();
/*     */   
/*     */   static {
/*  50 */     DEFAULT_COMPOSITE_CONVERTER_MAP.put(Token.BARE_COMPOSITE_KEYWORD_TOKEN.getValue().toString(), IdentityCompositeConverter.class.getName());
/*  51 */     DEFAULT_COMPOSITE_CONVERTER_MAP.put("replace", ReplacingCompositeConverter.class.getName());
/*     */   }
/*     */   public static final String REPLACE_CONVERTER_WORD = "replace";
/*     */   final List tokenList;
/*  55 */   int pointer = 0;
/*     */   
/*     */   Parser(TokenStream ts) throws ScanException {
/*  58 */     this.tokenList = ts.tokenize();
/*     */   }
/*     */   
/*     */   public Parser(String pattern) throws ScanException {
/*  62 */     this(pattern, (IEscapeUtil)new RegularEscapeUtil());
/*     */   }
/*     */   
/*     */   public Parser(String pattern, IEscapeUtil escapeUtil) throws ScanException {
/*     */     try {
/*  67 */       TokenStream ts = new TokenStream(pattern, escapeUtil);
/*  68 */       this.tokenList = ts.tokenize();
/*  69 */     } catch (IllegalArgumentException npe) {
/*  70 */       throw new ScanException("Failed to initialize Parser", npe);
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
/*     */   public Converter<E> compile(Node top, Map converterMap) {
/*  84 */     Compiler<E> compiler = new Compiler<E>(top, converterMap);
/*  85 */     compiler.setContext(this.context);
/*     */     
/*  87 */     return compiler.compile();
/*     */   }
/*     */   
/*     */   public Node parse() throws ScanException {
/*  91 */     return E();
/*     */   }
/*     */ 
/*     */   
/*     */   Node E() throws ScanException {
/*  96 */     Node t = T();
/*  97 */     if (t == null) {
/*  98 */       return null;
/*     */     }
/* 100 */     Node eOpt = Eopt();
/* 101 */     if (eOpt != null) {
/* 102 */       t.setNext(eOpt);
/*     */     }
/* 104 */     return t;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   Node Eopt() throws ScanException {
/* 110 */     Token next = getCurentToken();
/*     */     
/* 112 */     if (next == null) {
/* 113 */       return null;
/*     */     }
/* 115 */     return E();
/*     */   }
/*     */   
/*     */   Node T() throws ScanException {
/*     */     Token u;
/*     */     FormattingNode c;
/* 121 */     Token t = getCurentToken();
/* 122 */     expectNotNull(t, "a LITERAL or '%'");
/*     */     
/* 124 */     switch (t.getType()) {
/*     */       case 1000:
/* 126 */         advanceTokenPointer();
/* 127 */         return new Node(0, t.getValue());
/*     */       case 37:
/* 129 */         advanceTokenPointer();
/*     */ 
/*     */         
/* 132 */         u = getCurentToken();
/*     */         
/* 134 */         expectNotNull(u, "a FORMAT_MODIFIER, SIMPLE_KEYWORD or COMPOUND_KEYWORD");
/* 135 */         if (u.getType() == 1002) {
/* 136 */           FormatInfo fi = FormatInfo.valueOf((String)u.getValue());
/* 137 */           advanceTokenPointer();
/* 138 */           c = C();
/* 139 */           c.setFormatInfo(fi);
/*     */         } else {
/* 141 */           c = C();
/*     */         } 
/* 143 */         return c;
/*     */     } 
/*     */     
/* 146 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   FormattingNode C() throws ScanException {
/* 153 */     Token t = getCurentToken();
/*     */ 
/*     */     
/* 156 */     expectNotNull(t, "a LEFT_PARENTHESIS or KEYWORD");
/* 157 */     int type = t.getType();
/* 158 */     switch (type) {
/*     */       case 1004:
/* 160 */         return SINGLE();
/*     */       case 1005:
/* 162 */         advanceTokenPointer();
/* 163 */         return COMPOSITE(t.getValue().toString());
/*     */     } 
/* 165 */     throw new IllegalStateException("Unexpected token " + t);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   FormattingNode SINGLE() throws ScanException {
/* 171 */     Token t = getNextToken();
/*     */     
/* 173 */     SimpleKeywordNode keywordNode = new SimpleKeywordNode(t.getValue());
/*     */     
/* 175 */     Token ot = getCurentToken();
/* 176 */     if (ot != null && ot.getType() == 1006) {
/* 177 */       List<String> optionList = (List<String>)ot.getValue();
/* 178 */       keywordNode.setOptions(optionList);
/* 179 */       advanceTokenPointer();
/*     */     } 
/* 181 */     return keywordNode;
/*     */   }
/*     */   
/*     */   FormattingNode COMPOSITE(String keyword) throws ScanException {
/* 185 */     CompositeNode compositeNode = new CompositeNode(keyword);
/*     */     
/* 187 */     Node childNode = E();
/* 188 */     compositeNode.setChildNode(childNode);
/*     */     
/* 190 */     Token t = getNextToken();
/*     */     
/* 192 */     if (t == null || t.getType() != 41) {
/* 193 */       String msg = "Expecting RIGHT_PARENTHESIS token but got " + t;
/* 194 */       addError(msg);
/* 195 */       addError("See also http://logback.qos.ch/codes.html#missingRightParenthesis");
/* 196 */       throw new ScanException(msg);
/*     */     } 
/* 198 */     Token ot = getCurentToken();
/* 199 */     if (ot != null && ot.getType() == 1006) {
/* 200 */       List<String> optionList = (List<String>)ot.getValue();
/* 201 */       compositeNode.setOptions(optionList);
/* 202 */       advanceTokenPointer();
/*     */     } 
/* 204 */     return compositeNode;
/*     */   }
/*     */   
/*     */   Token getNextToken() {
/* 208 */     if (this.pointer < this.tokenList.size()) {
/* 209 */       return this.tokenList.get(this.pointer++);
/*     */     }
/* 211 */     return null;
/*     */   }
/*     */   
/*     */   Token getCurentToken() {
/* 215 */     if (this.pointer < this.tokenList.size()) {
/* 216 */       return this.tokenList.get(this.pointer);
/*     */     }
/* 218 */     return null;
/*     */   }
/*     */   
/*     */   void advanceTokenPointer() {
/* 222 */     this.pointer++;
/*     */   }
/*     */   
/*     */   void expectNotNull(Token t, String expected) {
/* 226 */     if (t == null)
/* 227 */       throw new IllegalStateException("All tokens consumed but was expecting " + expected); 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\core\pattern\parser\Parser.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */