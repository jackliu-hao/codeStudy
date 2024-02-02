/*      */ package freemarker.core;
/*      */ 
/*      */ import freemarker.template.Configuration;
/*      */ import freemarker.template.SimpleScalar;
/*      */ import freemarker.template.Template;
/*      */ import freemarker.template.TemplateBooleanModel;
/*      */ import freemarker.template.TemplateCollectionModel;
/*      */ import freemarker.template.TemplateHashModelEx;
/*      */ import freemarker.template.TemplateModel;
/*      */ import freemarker.template.TemplateModelException;
/*      */ import freemarker.template.TemplateModelIterator;
/*      */ import freemarker.template.TemplateScalarModel;
/*      */ import freemarker.template.Version;
/*      */ import freemarker.template._TemplateAPI;
/*      */ import freemarker.template.utility.ClassUtil;
/*      */ import freemarker.template.utility.CollectionUtils;
/*      */ import freemarker.template.utility.DeepUnwrap;
/*      */ import freemarker.template.utility.NullArgumentException;
/*      */ import freemarker.template.utility.StringUtil;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.io.Reader;
/*      */ import java.io.StringReader;
/*      */ import java.io.UnsupportedEncodingException;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Collections;
/*      */ import java.util.HashMap;
/*      */ import java.util.LinkedHashMap;
/*      */ import java.util.LinkedList;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.StringTokenizer;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class FMParser
/*      */   implements FMParserConstants
/*      */ {
/*      */   private static final int ITERATOR_BLOCK_KIND_LIST = 0;
/*      */   private static final int ITERATOR_BLOCK_KIND_FOREACH = 1;
/*      */   private static final int ITERATOR_BLOCK_KIND_ITEMS = 2;
/*      */   private static final int ITERATOR_BLOCK_KIND_USER_DIRECTIVE = 3;
/*      */   private Template template;
/*      */   private boolean stripWhitespace;
/*      */   private boolean stripText;
/*      */   private boolean preventStrippings;
/*      */   private int incompatibleImprovements;
/*      */   private OutputFormat outputFormat;
/*      */   private int autoEscapingPolicy;
/*      */   private boolean autoEscaping;
/*      */   private ParserConfiguration pCfg;
/*      */   private List<ParserIteratorBlockContext> iteratorBlockContexts;
/*      */   private int breakableDirectiveNesting;
/*      */   private int continuableDirectiveNesting;
/*      */   private boolean inMacro;
/*      */   private boolean inFunction;
/*      */   private boolean requireArgsSpecialVariable;
/*      */   
/*      */   private static class ParserIteratorBlockContext
/*      */   {
/*      */     private String loopVarName;
/*      */     private String loopVar2Name;
/*      */     private int kind;
/*      */     private boolean hashListing;
/*      */     
/*      */     private ParserIteratorBlockContext() {}
/*      */   }
/*   69 */   private LinkedList escapes = new LinkedList(); private int mixedContentNesting; public FMParserTokenManager token_source; SimpleCharStream jj_input_stream; public Token token;
/*      */   public Token jj_nt;
/*      */   private int jj_ntk;
/*      */   private Token jj_scanpos;
/*      */   private Token jj_lastpos;
/*      */   private int jj_la;
/*      */   private int jj_gen;
/*      */   
/*      */   public static FMParser createExpressionParser(String s) {
/*   78 */     SimpleCharStream scs = new SimpleCharStream(new StringReader(s), 1, 1, s.length());
/*   79 */     FMParserTokenManager token_source = new FMParserTokenManager(scs);
/*   80 */     token_source.SwitchTo(2);
/*   81 */     FMParser parser = new FMParser(token_source);
/*   82 */     token_source.setParser(parser);
/*   83 */     return parser;
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
/*      */   public FMParser(Template template, Reader reader, boolean strictSyntaxMode, boolean stripWhitespace) {
/*   99 */     this(template, reader, strictSyntaxMode, stripWhitespace, 0);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public FMParser(Template template, Reader reader, boolean strictSyntaxMode, boolean stripWhitespace, int tagSyntax) {
/*  106 */     this(template, reader, strictSyntaxMode, stripWhitespace, tagSyntax, Configuration.PARSED_DEFAULT_INCOMPATIBLE_ENHANCEMENTS);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public FMParser(Template template, Reader reader, boolean strictSyntaxMode, boolean stripWhitespace, int tagSyntax, int incompatibleImprovements) {
/*  115 */     this(template, reader, strictSyntaxMode, stripWhitespace, tagSyntax, 10, incompatibleImprovements);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public FMParser(String template) {
/*  123 */     this(dummyTemplate(), new StringReader(template), true, true);
/*      */   }
/*      */ 
/*      */   
/*      */   private static Template dummyTemplate() {
/*      */     try {
/*  129 */       return new Template(null, new StringReader(""), Configuration.getDefaultConfiguration());
/*  130 */     } catch (IOException e) {
/*  131 */       throw new RuntimeException("Failed to create dummy template", e);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public FMParser(Template template, Reader reader, boolean strictSyntaxMode, boolean whitespaceStripping, int tagSyntax, int namingConvention, int incompatibleImprovements) {
/*  140 */     this(template, reader, new LegacyConstructorParserConfiguration(strictSyntaxMode, whitespaceStripping, tagSyntax, 20, namingConvention, 
/*      */ 
/*      */ 
/*      */           
/*  144 */           Integer.valueOf((template != null) ? template.getParserConfiguration().getAutoEscapingPolicy() : 21), (template != null) ? template
/*      */           
/*  146 */           .getParserConfiguration().getOutputFormat() : null, (template != null) ? 
/*      */           
/*  148 */           Boolean.valueOf(template.getParserConfiguration().getRecognizeStandardFileExtensions()) : null, (template != null) ? 
/*      */           
/*  150 */           Integer.valueOf(template.getParserConfiguration().getTabSize()) : null, new Version(incompatibleImprovements), (template != null) ? template
/*      */ 
/*      */           
/*  153 */           .getArithmeticEngine() : null));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public FMParser(Template template, Reader reader, ParserConfiguration pCfg) {
/*  162 */     this(template, true, readerToTokenManager(reader, pCfg), pCfg);
/*      */   }
/*      */   
/*      */   private static FMParserTokenManager readerToTokenManager(Reader reader, ParserConfiguration pCfg) {
/*  166 */     SimpleCharStream simpleCharStream = new SimpleCharStream(reader, 1, 1);
/*  167 */     simpleCharStream.setTabSize(pCfg.getTabSize());
/*  168 */     return new FMParserTokenManager(simpleCharStream);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public FMParser(Template template, boolean newTemplate, FMParserTokenManager tkMan, ParserConfiguration pCfg) {
/*  177 */     this(tkMan);
/*      */     
/*  179 */     NullArgumentException.check(pCfg);
/*  180 */     this.pCfg = pCfg;
/*      */     
/*  182 */     NullArgumentException.check(template);
/*  183 */     this.template = template;
/*      */ 
/*      */     
/*  186 */     if (pCfg instanceof LegacyConstructorParserConfiguration) {
/*  187 */       LegacyConstructorParserConfiguration lpCfg = (LegacyConstructorParserConfiguration)pCfg;
/*  188 */       lpCfg.setArithmeticEngineIfNotSet(template.getArithmeticEngine());
/*  189 */       lpCfg.setAutoEscapingPolicyIfNotSet(template.getConfiguration().getAutoEscapingPolicy());
/*  190 */       lpCfg.setOutputFormatIfNotSet(template.getOutputFormat());
/*  191 */       lpCfg.setRecognizeStandardFileExtensionsIfNotSet(template
/*  192 */           .getParserConfiguration().getRecognizeStandardFileExtensions());
/*  193 */       lpCfg.setTabSizeIfNotSet(template
/*  194 */           .getParserConfiguration().getTabSize());
/*      */     } 
/*      */     
/*  197 */     int incompatibleImprovements = pCfg.getIncompatibleImprovements().intValue();
/*  198 */     this.token_source.incompatibleImprovements = incompatibleImprovements;
/*  199 */     this.incompatibleImprovements = incompatibleImprovements;
/*      */     
/*      */     OutputFormat outputFormatFromExt;
/*      */     
/*  203 */     if (!pCfg.getRecognizeStandardFileExtensions() || (
/*  204 */       outputFormatFromExt = getFormatFromStdFileExt()) == null) {
/*  205 */       this.autoEscapingPolicy = pCfg.getAutoEscapingPolicy();
/*  206 */       this.outputFormat = pCfg.getOutputFormat();
/*      */     } else {
/*      */       
/*  209 */       this.autoEscapingPolicy = 21;
/*  210 */       this.outputFormat = outputFormatFromExt;
/*      */     } 
/*      */     
/*  213 */     recalculateAutoEscapingField();
/*      */     
/*  215 */     this.token_source.setParser(this);
/*      */     
/*  217 */     this.token_source.strictSyntaxMode = pCfg.getStrictSyntaxMode();
/*      */     
/*  219 */     int tagSyntax = pCfg.getTagSyntax();
/*  220 */     switch (tagSyntax) {
/*      */       case 0:
/*  222 */         this.token_source.autodetectTagSyntax = true;
/*      */         break;
/*      */       case 1:
/*  225 */         this.token_source.squBracTagSyntax = false;
/*      */         break;
/*      */       case 2:
/*  228 */         this.token_source.squBracTagSyntax = true;
/*      */         break;
/*      */       default:
/*  231 */         throw new IllegalArgumentException("Illegal argument for tagSyntax: " + tagSyntax);
/*      */     } 
/*      */     
/*  234 */     this.token_source.interpolationSyntax = pCfg.getInterpolationSyntax();
/*      */     
/*  236 */     int namingConvention = pCfg.getNamingConvention();
/*  237 */     switch (namingConvention) {
/*      */       case 10:
/*      */       case 11:
/*      */       case 12:
/*  241 */         this.token_source.initialNamingConvention = namingConvention;
/*  242 */         this.token_source.namingConvention = namingConvention;
/*      */         break;
/*      */       default:
/*  245 */         throw new IllegalArgumentException("Illegal argument for namingConvention: " + namingConvention);
/*      */     } 
/*      */     
/*  248 */     this.stripWhitespace = pCfg.getWhitespaceStripping();
/*      */ 
/*      */ 
/*      */     
/*  252 */     if (newTemplate) {
/*  253 */       _TemplateAPI.setAutoEscaping(template, this.autoEscaping);
/*  254 */       _TemplateAPI.setOutputFormat(template, this.outputFormat);
/*      */     } 
/*      */   }
/*      */   
/*      */   void setupStringLiteralMode(FMParser parentParser, OutputFormat outputFormat) {
/*  259 */     FMParserTokenManager parentTokenSource = parentParser.token_source;
/*      */     
/*  261 */     this.token_source.initialNamingConvention = parentTokenSource.initialNamingConvention;
/*  262 */     this.token_source.namingConvention = parentTokenSource.namingConvention;
/*  263 */     this.token_source.namingConventionEstabilisher = parentTokenSource.namingConventionEstabilisher;
/*  264 */     this.token_source.SwitchTo(1);
/*      */     
/*  266 */     this.outputFormat = outputFormat;
/*  267 */     recalculateAutoEscapingField();
/*  268 */     if (this.incompatibleImprovements < _TemplateAPI.VERSION_INT_2_3_24)
/*      */     {
/*  270 */       this.incompatibleImprovements = _TemplateAPI.VERSION_INT_2_3_0;
/*      */     }
/*      */ 
/*      */     
/*  274 */     this.iteratorBlockContexts = parentParser.iteratorBlockContexts;
/*      */   }
/*      */ 
/*      */   
/*      */   void tearDownStringLiteralMode(FMParser parentParser) {
/*  279 */     FMParserTokenManager parentTokenSource = parentParser.token_source;
/*  280 */     parentTokenSource.namingConvention = this.token_source.namingConvention;
/*  281 */     parentTokenSource.namingConventionEstabilisher = this.token_source.namingConventionEstabilisher;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   void setPreventStrippings(boolean preventStrippings) {
/*  288 */     this.preventStrippings = preventStrippings;
/*      */   }
/*      */   
/*      */   private OutputFormat getFormatFromStdFileExt() {
/*  292 */     String sourceName = this.template.getSourceName();
/*  293 */     if (sourceName == null) {
/*  294 */       return null;
/*      */     }
/*      */     
/*  297 */     int ln = sourceName.length();
/*  298 */     if (ln < 5) return null;
/*      */     
/*  300 */     char c = sourceName.charAt(ln - 5);
/*  301 */     if (c != '.') return null;
/*      */     
/*  303 */     c = sourceName.charAt(ln - 4);
/*  304 */     if (c != 'f' && c != 'F') return null;
/*      */     
/*  306 */     c = sourceName.charAt(ln - 3);
/*  307 */     if (c != 't' && c != 'T') return null;
/*      */     
/*  309 */     c = sourceName.charAt(ln - 2);
/*  310 */     if (c != 'l' && c != 'L') return null;
/*      */     
/*  312 */     c = sourceName.charAt(ln - 1);
/*      */     
/*      */     try {
/*  315 */       if (c == 'h' || c == 'H') {
/*  316 */         return this.template.getConfiguration().getOutputFormat(HTMLOutputFormat.INSTANCE.getName());
/*      */       }
/*  318 */       if (c == 'x' || c == 'X') {
/*  319 */         return this.template.getConfiguration().getOutputFormat(XMLOutputFormat.INSTANCE.getName());
/*      */       }
/*  321 */     } catch (UnregisteredOutputFormatException e) {
/*  322 */       throw new BugException("Unregistered std format", e);
/*      */     } 
/*  324 */     return null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void recalculateAutoEscapingField() {
/*  331 */     if (this.outputFormat instanceof MarkupOutputFormat) {
/*  332 */       if (this.autoEscapingPolicy == 21) {
/*  333 */         this.autoEscaping = ((MarkupOutputFormat)this.outputFormat).isAutoEscapedByDefault();
/*  334 */       } else if (this.autoEscapingPolicy == 22) {
/*  335 */         this.autoEscaping = true;
/*  336 */       } else if (this.autoEscapingPolicy == 20) {
/*  337 */         this.autoEscaping = false;
/*      */       } else {
/*  339 */         throw new IllegalStateException("Unhandled autoEscaping ENUM: " + this.autoEscapingPolicy);
/*      */       } 
/*      */     } else {
/*  342 */       this.autoEscaping = false;
/*      */     } 
/*      */   }
/*      */   
/*      */   MarkupOutputFormat getMarkupOutputFormat() {
/*  347 */     return (this.outputFormat instanceof MarkupOutputFormat) ? (MarkupOutputFormat)this.outputFormat : null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int _getLastTagSyntax() {
/*  354 */     return this.token_source.squBracTagSyntax ? 2 : 1;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int _getLastNamingConvention() {
/*  365 */     return this.token_source.namingConvention;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void notStringLiteral(Expression exp, String expected) throws ParseException {
/*  372 */     if (exp instanceof StringLiteral) {
/*  373 */       throw new ParseException("Found string literal: " + exp + ". Expecting: " + expected, exp);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void notNumberLiteral(Expression exp, String expected) throws ParseException {
/*  383 */     if (exp instanceof NumberLiteral) {
/*  384 */       throw new ParseException("Found number literal: " + exp
/*  385 */           .getCanonicalForm() + ". Expecting " + expected, exp);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void notBooleanLiteral(Expression exp, String expected) throws ParseException {
/*  394 */     if (exp instanceof BooleanLiteral) {
/*  395 */       throw new ParseException("Found: " + exp.getCanonicalForm() + " literal. Expecting " + expected, exp);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void notHashLiteral(Expression exp, String expected) throws ParseException {
/*  403 */     if (exp instanceof HashLiteral) {
/*  404 */       throw new ParseException("Found hash literal: " + exp
/*  405 */           .getCanonicalForm() + ". Expecting " + expected, exp);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void notListLiteral(Expression exp, String expected) throws ParseException {
/*  416 */     if (exp instanceof ListLiteral) {
/*  417 */       throw new ParseException("Found list literal: " + exp
/*  418 */           .getCanonicalForm() + ". Expecting " + expected, exp);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void numberLiteralOnly(Expression exp) throws ParseException {
/*  427 */     notStringLiteral(exp, "number");
/*  428 */     notListLiteral(exp, "number");
/*  429 */     notHashLiteral(exp, "number");
/*  430 */     notBooleanLiteral(exp, "number");
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void stringLiteralOnly(Expression exp) throws ParseException {
/*  437 */     notNumberLiteral(exp, "string");
/*  438 */     notListLiteral(exp, "string");
/*  439 */     notHashLiteral(exp, "string");
/*  440 */     notBooleanLiteral(exp, "string");
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void booleanLiteralOnly(Expression exp) throws ParseException {
/*  447 */     notStringLiteral(exp, "boolean (true/false)");
/*  448 */     notListLiteral(exp, "boolean (true/false)");
/*  449 */     notHashLiteral(exp, "boolean (true/false)");
/*  450 */     notNumberLiteral(exp, "boolean (true/false)");
/*      */   }
/*      */   
/*      */   private Expression escapedExpression(Expression exp) throws ParseException {
/*  454 */     if (!this.escapes.isEmpty()) {
/*  455 */       return ((EscapeBlock)this.escapes.getFirst()).doEscape(exp);
/*      */     }
/*  457 */     return exp;
/*      */   }
/*      */ 
/*      */   
/*      */   private boolean getBoolean(Expression exp, boolean legacyCompat) throws ParseException {
/*  462 */     TemplateModel tm = null;
/*      */     try {
/*  464 */       tm = exp.eval((Environment)null);
/*  465 */     } catch (Exception e) {
/*  466 */       throw new ParseException(e.getMessage() + "\nCould not evaluate expression: " + exp
/*      */           
/*  468 */           .getCanonicalForm(), exp, e);
/*      */     } 
/*      */ 
/*      */     
/*  472 */     if (tm instanceof TemplateBooleanModel) {
/*      */       try {
/*  474 */         return ((TemplateBooleanModel)tm).getAsBoolean();
/*  475 */       } catch (TemplateModelException templateModelException) {}
/*      */     }
/*      */     
/*  478 */     if (legacyCompat && tm instanceof TemplateScalarModel) {
/*      */       try {
/*  480 */         return StringUtil.getYesNo(((TemplateScalarModel)tm).getAsString());
/*  481 */       } catch (Exception e) {
/*  482 */         throw new ParseException(e.getMessage() + "\nExpecting boolean (true/false), found: " + exp
/*  483 */             .getCanonicalForm(), exp);
/*      */       } 
/*      */     }
/*      */     
/*  487 */     throw new ParseException("Expecting boolean (true/false) parameter", exp);
/*      */   }
/*      */   
/*      */   void checkCurrentOutputFormatCanEscape(Token start) throws ParseException {
/*  491 */     if (!(this.outputFormat instanceof MarkupOutputFormat)) {
/*  492 */       throw new ParseException("The current output format can't do escaping: " + this.outputFormat, this.template, start);
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   private ParserIteratorBlockContext pushIteratorBlockContext() {
/*  498 */     if (this.iteratorBlockContexts == null) {
/*  499 */       this.iteratorBlockContexts = new ArrayList<>(4);
/*      */     }
/*  501 */     ParserIteratorBlockContext newCtx = new ParserIteratorBlockContext();
/*  502 */     this.iteratorBlockContexts.add(newCtx);
/*  503 */     return newCtx;
/*      */   }
/*      */   
/*      */   private void popIteratorBlockContext() {
/*  507 */     this.iteratorBlockContexts.remove(this.iteratorBlockContexts.size() - 1);
/*      */   }
/*      */   
/*      */   private ParserIteratorBlockContext peekIteratorBlockContext() {
/*  511 */     int size = (this.iteratorBlockContexts != null) ? this.iteratorBlockContexts.size() : 0;
/*  512 */     return (size != 0) ? this.iteratorBlockContexts.get(size - 1) : null;
/*      */   }
/*      */ 
/*      */   
/*      */   private void checkLoopVariableBuiltInLHO(String loopVarName, Expression lhoExp, Token biName) throws ParseException {
/*  517 */     int size = (this.iteratorBlockContexts != null) ? this.iteratorBlockContexts.size() : 0;
/*  518 */     for (int i = size - 1; i >= 0; i--) {
/*  519 */       ParserIteratorBlockContext ctx = this.iteratorBlockContexts.get(i);
/*  520 */       if (loopVarName.equals(ctx.loopVarName) || loopVarName.equals(ctx.loopVar2Name)) {
/*  521 */         if (ctx.kind == 3) {
/*  522 */           throw new ParseException("The left hand operand of ?" + biName.image + " can't be the loop variable of an user defined directive: " + loopVarName, lhoExp);
/*      */         }
/*      */ 
/*      */         
/*      */         return;
/*      */       } 
/*      */     } 
/*      */ 
/*      */     
/*  531 */     throw new ParseException("The left hand operand of ?" + biName.image + " must be a loop variable, but there's no loop variable in scope with this name: " + loopVarName, lhoExp);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private String forEachDirectiveSymbol() {
/*  539 */     return (this.token_source.namingConvention == 12) ? "#forEach" : "#foreach";
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
/*      */   public final Expression Expression() throws ParseException {
/*  552 */     Expression exp = OrExpression();
/*  553 */     if ("" != null) return exp; 
/*  554 */     throw new Error("Missing return statement in function");
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final Expression PrimaryExpression() throws ParseException {
/*  563 */     Expression exp = AtomicExpression();
/*      */     
/*      */     while (true) {
/*  566 */       switch ((this.jj_ntk == -1) ? jj_ntk_f() : this.jj_ntk) {
/*      */         case 99:
/*      */         case 103:
/*      */         case 104:
/*      */         case 129:
/*      */         case 133:
/*      */         case 135:
/*      */         case 153:
/*      */           break;
/*      */ 
/*      */         
/*      */         default:
/*  578 */           this.jj_la1[0] = this.jj_gen;
/*      */           break;
/*      */       } 
/*  581 */       switch ((this.jj_ntk == -1) ? jj_ntk_f() : this.jj_ntk) {
/*      */         case 99:
/*  583 */           exp = DotVariable(exp);
/*      */           continue;
/*      */         
/*      */         case 133:
/*  587 */           exp = DynamicKey(exp);
/*      */           continue;
/*      */         
/*      */         case 135:
/*  591 */           exp = MethodArgs(exp);
/*      */           continue;
/*      */         
/*      */         case 103:
/*  595 */           exp = BuiltIn(exp);
/*      */           continue;
/*      */         
/*      */         case 129:
/*      */         case 153:
/*  600 */           exp = DefaultTo(exp);
/*      */           continue;
/*      */         
/*      */         case 104:
/*  604 */           exp = Exists(exp);
/*      */           continue;
/*      */       } 
/*      */       
/*  608 */       this.jj_la1[1] = this.jj_gen;
/*  609 */       jj_consume_token(-1);
/*  610 */       throw new ParseException();
/*      */     } 
/*      */     
/*  613 */     if ("" != null) return exp; 
/*  614 */     throw new Error("Missing return statement in function");
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final Expression AtomicExpression() throws ParseException {
/*      */     Expression exp;
/*  623 */     switch ((this.jj_ntk == -1) ? jj_ntk_f() : this.jj_ntk) {
/*      */       case 97:
/*      */       case 98:
/*  626 */         exp = NumberLiteral();
/*      */         break;
/*      */       
/*      */       case 137:
/*  630 */         exp = HashLiteral();
/*      */         break;
/*      */       
/*      */       case 93:
/*      */       case 94:
/*  635 */         exp = StringLiteral(true);
/*      */         break;
/*      */       
/*      */       case 95:
/*      */       case 96:
/*  640 */         exp = BooleanLiteral();
/*      */         break;
/*      */       
/*      */       case 133:
/*  644 */         exp = ListLiteral();
/*      */         break;
/*      */       
/*      */       case 142:
/*  648 */         exp = Identifier();
/*      */         break;
/*      */       
/*      */       case 135:
/*  652 */         exp = Parenthesis();
/*      */         break;
/*      */       
/*      */       case 99:
/*  656 */         exp = BuiltinVariable();
/*      */         break;
/*      */       
/*      */       default:
/*  660 */         this.jj_la1[2] = this.jj_gen;
/*  661 */         jj_consume_token(-1);
/*  662 */         throw new ParseException();
/*      */     } 
/*  664 */     if ("" != null) return exp; 
/*  665 */     throw new Error("Missing return statement in function");
/*      */   }
/*      */ 
/*      */   
/*      */   public final Expression Parenthesis() throws ParseException {
/*  670 */     Token start = jj_consume_token(135);
/*  671 */     Expression exp = Expression();
/*  672 */     Token end = jj_consume_token(136);
/*  673 */     Expression result = new ParentheticalExpression(exp);
/*  674 */     result.setLocation(this.template, start, end);
/*  675 */     if ("" != null) return result; 
/*  676 */     throw new Error("Missing return statement in function");
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final Expression UnaryExpression() throws ParseException {
/*      */     Expression result;
/*  684 */     boolean haveNot = false;
/*  685 */     Token t = null, start = null;
/*  686 */     switch ((this.jj_ntk == -1) ? jj_ntk_f() : this.jj_ntk) {
/*      */       case 120:
/*      */       case 121:
/*  689 */         result = UnaryPlusMinusExpression();
/*      */         break;
/*      */       
/*      */       case 129:
/*  693 */         result = NotExpression();
/*      */         break;
/*      */       
/*      */       case 93:
/*      */       case 94:
/*      */       case 95:
/*      */       case 96:
/*      */       case 97:
/*      */       case 98:
/*      */       case 99:
/*      */       case 133:
/*      */       case 135:
/*      */       case 137:
/*      */       case 142:
/*  707 */         result = PrimaryExpression();
/*      */         break;
/*      */       
/*      */       default:
/*  711 */         this.jj_la1[3] = this.jj_gen;
/*  712 */         jj_consume_token(-1);
/*  713 */         throw new ParseException();
/*      */     } 
/*  715 */     if ("" != null) return result; 
/*  716 */     throw new Error("Missing return statement in function");
/*      */   }
/*      */   
/*      */   public final Expression NotExpression() throws ParseException {
/*  720 */     Expression result = null;
/*  721 */     ArrayList<Token> nots = new ArrayList();
/*      */     
/*      */     while (true) {
/*  724 */       Token t = jj_consume_token(129);
/*  725 */       nots.add(t);
/*  726 */       switch ((this.jj_ntk == -1) ? jj_ntk_f() : this.jj_ntk) {
/*      */         case 129:
/*      */           continue;
/*      */       } 
/*      */       break;
/*      */     } 
/*  732 */     this.jj_la1[4] = this.jj_gen;
/*      */ 
/*      */ 
/*      */     
/*  736 */     Expression exp = PrimaryExpression();
/*  737 */     for (int i = 0; i < nots.size(); i++) {
/*  738 */       result = new NotExpression(exp);
/*  739 */       Token tok = nots.get(nots.size() - i - 1);
/*  740 */       result.setLocation(this.template, tok, exp);
/*  741 */       exp = result;
/*      */     } 
/*  743 */     if ("" != null) return result; 
/*  744 */     throw new Error("Missing return statement in function");
/*      */   }
/*      */   public final Expression UnaryPlusMinusExpression() throws ParseException {
/*      */     Token t;
/*  748 */     boolean isMinus = false;
/*      */     
/*  750 */     switch ((this.jj_ntk == -1) ? jj_ntk_f() : this.jj_ntk) {
/*      */       case 120:
/*  752 */         t = jj_consume_token(120);
/*      */         break;
/*      */       
/*      */       case 121:
/*  756 */         t = jj_consume_token(121);
/*  757 */         isMinus = true;
/*      */         break;
/*      */       
/*      */       default:
/*  761 */         this.jj_la1[5] = this.jj_gen;
/*  762 */         jj_consume_token(-1);
/*  763 */         throw new ParseException();
/*      */     } 
/*  765 */     Expression exp = PrimaryExpression();
/*  766 */     Expression result = new UnaryPlusMinusExpression(exp, isMinus);
/*  767 */     result.setLocation(this.template, t, exp);
/*  768 */     if ("" != null) return result; 
/*  769 */     throw new Error("Missing return statement in function");
/*      */   }
/*      */ 
/*      */   
/*      */   public final Expression AdditiveExpression() throws ParseException {
/*  774 */     Expression lhs = MultiplicativeExpression();
/*  775 */     Expression result = lhs;
/*      */ 
/*      */     
/*  778 */     while (jj_2_1(2147483647)) {
/*      */       boolean plus;
/*      */ 
/*      */ 
/*      */       
/*  783 */       switch ((this.jj_ntk == -1) ? jj_ntk_f() : this.jj_ntk) {
/*      */         case 120:
/*  785 */           jj_consume_token(120);
/*  786 */           plus = true;
/*      */           break;
/*      */         
/*      */         case 121:
/*  790 */           jj_consume_token(121);
/*  791 */           plus = false;
/*      */           break;
/*      */         
/*      */         default:
/*  795 */           this.jj_la1[6] = this.jj_gen;
/*  796 */           jj_consume_token(-1);
/*  797 */           throw new ParseException();
/*      */       } 
/*  799 */       Expression rhs = MultiplicativeExpression();
/*  800 */       if (plus) {
/*      */ 
/*      */         
/*  803 */         result = new AddConcatExpression(lhs, rhs);
/*      */       } else {
/*  805 */         numberLiteralOnly(lhs);
/*  806 */         numberLiteralOnly(rhs);
/*  807 */         result = new ArithmeticExpression(lhs, rhs, 0);
/*      */       } 
/*  809 */       result.setLocation(this.template, lhs, rhs);
/*  810 */       lhs = result;
/*      */     } 
/*  812 */     if ("" != null) return result; 
/*  813 */     throw new Error("Missing return statement in function");
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final Expression MultiplicativeExpression() throws ParseException {
/*  821 */     int operation = 1;
/*  822 */     Expression lhs = UnaryExpression();
/*  823 */     Expression result = lhs;
/*      */ 
/*      */     
/*  826 */     while (jj_2_2(2147483647)) {
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  831 */       switch ((this.jj_ntk == -1) ? jj_ntk_f() : this.jj_ntk) {
/*      */         case 122:
/*  833 */           jj_consume_token(122);
/*  834 */           operation = 1;
/*      */           break;
/*      */         
/*      */         case 125:
/*  838 */           jj_consume_token(125);
/*  839 */           operation = 2;
/*      */           break;
/*      */         
/*      */         case 126:
/*  843 */           jj_consume_token(126);
/*  844 */           operation = 3;
/*      */           break;
/*      */         
/*      */         default:
/*  848 */           this.jj_la1[7] = this.jj_gen;
/*  849 */           jj_consume_token(-1);
/*  850 */           throw new ParseException();
/*      */       } 
/*  852 */       Expression rhs = UnaryExpression();
/*  853 */       numberLiteralOnly(lhs);
/*  854 */       numberLiteralOnly(rhs);
/*  855 */       result = new ArithmeticExpression(lhs, rhs, operation);
/*  856 */       result.setLocation(this.template, lhs, rhs);
/*  857 */       lhs = result;
/*      */     } 
/*  859 */     if ("" != null) return result; 
/*  860 */     throw new Error("Missing return statement in function");
/*      */   }
/*      */ 
/*      */   
/*      */   public final Expression EqualityExpression() throws ParseException {
/*  865 */     Expression lhs = RelationalExpression();
/*  866 */     Expression result = lhs;
/*  867 */     if (jj_2_3(2147483647)) {
/*  868 */       Token t; switch ((this.jj_ntk == -1) ? jj_ntk_f() : this.jj_ntk) {
/*      */         case 107:
/*  870 */           t = jj_consume_token(107);
/*      */           break;
/*      */         
/*      */         case 105:
/*  874 */           t = jj_consume_token(105);
/*      */           break;
/*      */         
/*      */         case 106:
/*  878 */           t = jj_consume_token(106);
/*      */           break;
/*      */         
/*      */         default:
/*  882 */           this.jj_la1[8] = this.jj_gen;
/*  883 */           jj_consume_token(-1);
/*  884 */           throw new ParseException();
/*      */       } 
/*  886 */       Expression rhs = RelationalExpression();
/*  887 */       notHashLiteral(lhs, "different type for equality check");
/*  888 */       notHashLiteral(rhs, "different type for equality check");
/*  889 */       notListLiteral(lhs, "different type for equality check");
/*  890 */       notListLiteral(rhs, "different type for equality check");
/*  891 */       result = new ComparisonExpression(lhs, rhs, t.image);
/*  892 */       result.setLocation(this.template, lhs, rhs);
/*      */     } 
/*      */ 
/*      */     
/*  896 */     if ("" != null) return result; 
/*  897 */     throw new Error("Missing return statement in function");
/*      */   }
/*      */   
/*      */   public final Expression RelationalExpression() throws ParseException
/*      */   {
/*  902 */     Expression lhs = RangeExpression();
/*  903 */     Expression result = lhs;
/*  904 */     if (jj_2_4(2147483647)) {
/*  905 */       Token t; switch ((this.jj_ntk == -1) ? jj_ntk_f() : this.jj_ntk) {
/*      */         case 151:
/*  907 */           t = jj_consume_token(151);
/*      */           break;
/*      */         
/*      */         case 118:
/*  911 */           t = jj_consume_token(118);
/*      */           break;
/*      */         
/*      */         case 150:
/*  915 */           t = jj_consume_token(150);
/*      */           break;
/*      */         
/*      */         case 117:
/*  919 */           t = jj_consume_token(117);
/*      */           break;
/*      */         
/*      */         case 116:
/*  923 */           t = jj_consume_token(116);
/*      */           break;
/*      */         
/*      */         case 115:
/*  927 */           t = jj_consume_token(115);
/*      */           break;
/*      */         
/*      */         default:
/*  931 */           this.jj_la1[9] = this.jj_gen;
/*  932 */           jj_consume_token(-1);
/*  933 */           throw new ParseException();
/*      */       } 
/*  935 */       Expression rhs = RangeExpression();
/*  936 */       numberLiteralOnly(lhs);
/*  937 */       numberLiteralOnly(rhs);
/*  938 */       result = new ComparisonExpression(lhs, rhs, t.image);
/*  939 */       result.setLocation(this.template, lhs, rhs);
/*      */     } 
/*      */ 
/*      */     
/*  943 */     if ("" != null) return result; 
/*  944 */     throw new Error("Missing return statement in function"); } public final Expression RangeExpression() throws ParseException {
/*      */     int endType;
/*      */     Range range;
/*  947 */     Expression rhs = null;
/*      */     
/*  949 */     Token dotDot = null;
/*  950 */     Expression lhs = AdditiveExpression();
/*  951 */     Expression result = lhs;
/*  952 */     switch ((this.jj_ntk == -1) ? jj_ntk_f() : this.jj_ntk) {
/*      */       case 100:
/*      */       case 101:
/*      */       case 102:
/*  956 */         switch ((this.jj_ntk == -1) ? jj_ntk_f() : this.jj_ntk) {
/*      */           case 101:
/*      */           case 102:
/*  959 */             switch ((this.jj_ntk == -1) ? jj_ntk_f() : this.jj_ntk) {
/*      */               case 101:
/*  961 */                 jj_consume_token(101);
/*  962 */                 endType = 1;
/*      */                 break;
/*      */               
/*      */               case 102:
/*  966 */                 jj_consume_token(102);
/*  967 */                 endType = 3;
/*      */                 break;
/*      */               
/*      */               default:
/*  971 */                 this.jj_la1[10] = this.jj_gen;
/*  972 */                 jj_consume_token(-1);
/*  973 */                 throw new ParseException();
/*      */             } 
/*  975 */             rhs = AdditiveExpression();
/*      */             break;
/*      */           
/*      */           case 100:
/*  979 */             dotDot = jj_consume_token(100);
/*  980 */             endType = 2;
/*  981 */             if (jj_2_5(2147483647)) {
/*  982 */               rhs = AdditiveExpression();
/*  983 */               endType = 0;
/*      */             } 
/*      */             break;
/*      */ 
/*      */ 
/*      */           
/*      */           default:
/*  990 */             this.jj_la1[11] = this.jj_gen;
/*  991 */             jj_consume_token(-1);
/*  992 */             throw new ParseException();
/*      */         } 
/*  994 */         numberLiteralOnly(lhs);
/*  995 */         if (rhs != null) {
/*  996 */           numberLiteralOnly(rhs);
/*      */         }
/*      */         
/*  999 */         range = new Range(lhs, rhs, endType);
/* 1000 */         if (rhs != null) {
/* 1001 */           range.setLocation(this.template, lhs, rhs);
/*      */         } else {
/* 1003 */           range.setLocation(this.template, lhs, dotDot);
/*      */         } 
/* 1005 */         result = range;
/*      */         break;
/*      */       
/*      */       default:
/* 1009 */         this.jj_la1[12] = this.jj_gen;
/*      */         break;
/*      */     } 
/* 1012 */     if ("" != null) return result; 
/* 1013 */     throw new Error("Missing return statement in function");
/*      */   }
/*      */   
/*      */   public final Expression AndExpression() throws ParseException {
/* 1017 */     Expression lhs = EqualityExpression();
/* 1018 */     Expression result = lhs;
/*      */ 
/*      */     
/* 1021 */     while (jj_2_6(2147483647)) {
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 1026 */       jj_consume_token(127);
/* 1027 */       Expression rhs = EqualityExpression();
/* 1028 */       booleanLiteralOnly(lhs);
/* 1029 */       booleanLiteralOnly(rhs);
/* 1030 */       result = new AndExpression(lhs, rhs);
/* 1031 */       result.setLocation(this.template, lhs, rhs);
/* 1032 */       lhs = result;
/*      */     } 
/* 1034 */     if ("" != null) return result; 
/* 1035 */     throw new Error("Missing return statement in function");
/*      */   }
/*      */   
/*      */   public final Expression OrExpression() throws ParseException {
/* 1039 */     Expression lhs = AndExpression();
/* 1040 */     Expression result = lhs;
/*      */ 
/*      */     
/* 1043 */     while (jj_2_7(2147483647)) {
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 1048 */       jj_consume_token(128);
/* 1049 */       Expression rhs = AndExpression();
/* 1050 */       booleanLiteralOnly(lhs);
/* 1051 */       booleanLiteralOnly(rhs);
/* 1052 */       result = new OrExpression(lhs, rhs);
/* 1053 */       result.setLocation(this.template, lhs, rhs);
/* 1054 */       lhs = result;
/*      */     } 
/* 1056 */     if ("" != null) return result; 
/* 1057 */     throw new Error("Missing return statement in function");
/*      */   }
/*      */   public final ListLiteral ListLiteral() throws ParseException {
/* 1060 */     ArrayList<Expression> values = new ArrayList();
/*      */     
/* 1062 */     Token begin = jj_consume_token(133);
/* 1063 */     values = PositionalArgs();
/* 1064 */     Token end = jj_consume_token(134);
/* 1065 */     ListLiteral result = new ListLiteral(values);
/* 1066 */     result.setLocation(this.template, begin, end);
/* 1067 */     if ("" != null) return result; 
/* 1068 */     throw new Error("Missing return statement in function");
/*      */   }
/*      */   public final Expression NumberLiteral() throws ParseException {
/* 1071 */     Token t, op = null;
/* 1072 */     switch ((this.jj_ntk == -1) ? jj_ntk_f() : this.jj_ntk) {
/*      */       case 97:
/* 1074 */         t = jj_consume_token(97);
/*      */         break;
/*      */       
/*      */       case 98:
/* 1078 */         t = jj_consume_token(98);
/*      */         break;
/*      */       
/*      */       default:
/* 1082 */         this.jj_la1[13] = this.jj_gen;
/* 1083 */         jj_consume_token(-1);
/* 1084 */         throw new ParseException();
/*      */     } 
/* 1086 */     String s = t.image;
/* 1087 */     Expression result = new NumberLiteral(this.pCfg.getArithmeticEngine().toNumber(s));
/* 1088 */     Token startToken = (op != null) ? op : t;
/* 1089 */     result.setLocation(this.template, startToken, t);
/* 1090 */     if ("" != null) return result; 
/* 1091 */     throw new Error("Missing return statement in function");
/*      */   }
/*      */   
/*      */   public final Identifier Identifier() throws ParseException {
/* 1095 */     Token t = jj_consume_token(142);
/* 1096 */     Identifier id = new Identifier(t.image);
/* 1097 */     id.setLocation(this.template, t, t);
/* 1098 */     if ("" != null) return id; 
/* 1099 */     throw new Error("Missing return statement in function");
/*      */   }
/*      */   public final Expression IdentifierOrStringLiteral() throws ParseException {
/*      */     Expression exp;
/* 1103 */     switch ((this.jj_ntk == -1) ? jj_ntk_f() : this.jj_ntk) {
/*      */       case 142:
/* 1105 */         exp = Identifier();
/*      */         break;
/*      */       
/*      */       case 93:
/*      */       case 94:
/* 1110 */         exp = StringLiteral(false);
/*      */         break;
/*      */       
/*      */       default:
/* 1114 */         this.jj_la1[14] = this.jj_gen;
/* 1115 */         jj_consume_token(-1);
/* 1116 */         throw new ParseException();
/*      */     } 
/* 1118 */     if ("" != null) return exp; 
/* 1119 */     throw new Error("Missing return statement in function");
/*      */   }
/*      */   public final BuiltinVariable BuiltinVariable() throws ParseException {
/*      */     TemplateModel parseTimeValue;
/* 1123 */     Token dot = jj_consume_token(99);
/* 1124 */     Token name = jj_consume_token(142);
/* 1125 */     BuiltinVariable result = null;
/* 1126 */     this.token_source.checkNamingConvention(name);
/*      */ 
/*      */     
/* 1129 */     String nameStr = name.image;
/* 1130 */     if (nameStr.equals("output_format") || nameStr.equals("outputFormat")) {
/* 1131 */       SimpleScalar simpleScalar = new SimpleScalar(this.outputFormat.getName());
/* 1132 */     } else if (nameStr.equals("auto_esc") || nameStr.equals("autoEsc")) {
/* 1133 */       TemplateBooleanModel templateBooleanModel = this.autoEscaping ? TemplateBooleanModel.TRUE : TemplateBooleanModel.FALSE;
/* 1134 */     } else if (nameStr.equals("args")) {
/* 1135 */       if (!this.inMacro && !this.inFunction) {
/* 1136 */         throw new ParseException("The \"args\" special variable must be inside a macro or function in the template source code.", this.template, name);
/*      */       }
/*      */       
/* 1139 */       this.requireArgsSpecialVariable = true;
/* 1140 */       parseTimeValue = null;
/*      */     } else {
/* 1142 */       parseTimeValue = null;
/*      */     } 
/*      */     
/* 1145 */     result = new BuiltinVariable(name, this.token_source, parseTimeValue);
/*      */     
/* 1147 */     result.setLocation(this.template, dot, name);
/* 1148 */     if ("" != null) return result; 
/* 1149 */     throw new Error("Missing return statement in function");
/*      */   } public final Expression DefaultTo(Expression exp) throws ParseException {
/*      */     Token t;
/* 1152 */     Expression rhs = null;
/*      */     
/* 1154 */     switch ((this.jj_ntk == -1) ? jj_ntk_f() : this.jj_ntk) {
/*      */       case 153:
/* 1156 */         t = jj_consume_token(153);
/*      */         break;
/*      */       
/*      */       case 129:
/* 1160 */         t = jj_consume_token(129);
/* 1161 */         if (jj_2_8(2147483647)) {
/* 1162 */           rhs = Expression();
/*      */         }
/*      */         break;
/*      */ 
/*      */ 
/*      */       
/*      */       default:
/* 1169 */         this.jj_la1[15] = this.jj_gen;
/* 1170 */         jj_consume_token(-1);
/* 1171 */         throw new ParseException();
/*      */     } 
/* 1173 */     DefaultToExpression result = new DefaultToExpression(exp, rhs);
/* 1174 */     if (rhs == null) {
/*      */       
/* 1176 */       result.setLocation(this.template, exp.beginColumn, exp.beginLine, t.beginColumn, t.beginLine);
/*      */     } else {
/* 1178 */       result.setLocation(this.template, exp, rhs);
/*      */     } 
/* 1180 */     if ("" != null) return result; 
/* 1181 */     throw new Error("Missing return statement in function");
/*      */   }
/*      */   
/*      */   public final Expression Exists(Expression exp) throws ParseException {
/* 1185 */     Token t = jj_consume_token(104);
/* 1186 */     ExistsExpression result = new ExistsExpression(exp);
/* 1187 */     result.setLocation(this.template, exp, t);
/* 1188 */     if ("" != null) return result; 
/* 1189 */     throw new Error("Missing return statement in function");
/*      */   }
/*      */   public final Expression BuiltIn(Expression lhoExp) throws ParseException {
/* 1192 */     Token t = null;
/*      */     
/* 1194 */     ArrayList<Expression> args = null;
/*      */ 
/*      */ 
/*      */     
/* 1198 */     jj_consume_token(103);
/* 1199 */     t = jj_consume_token(142);
/* 1200 */     this.token_source.checkNamingConvention(t);
/* 1201 */     BuiltIn result = BuiltIn.newBuiltIn(this.incompatibleImprovements, lhoExp, t, this.token_source);
/* 1202 */     result.setLocation(this.template, lhoExp, t);
/*      */     
/* 1204 */     if (!(result instanceof SpecialBuiltIn) && 
/* 1205 */       "" != null) return result;
/*      */ 
/*      */     
/* 1208 */     if (result instanceof BuiltInForLoopVariable) {
/* 1209 */       if (!(lhoExp instanceof Identifier)) {
/* 1210 */         throw new ParseException("Expression used as the left hand operand of ?" + t.image + " must be a simple loop variable name.", lhoExp);
/*      */       }
/*      */ 
/*      */       
/* 1214 */       String loopVarName = ((Identifier)lhoExp).getName();
/* 1215 */       checkLoopVariableBuiltInLHO(loopVarName, lhoExp, t);
/* 1216 */       ((BuiltInForLoopVariable)result).bindToLoopVariable(loopVarName);
/*      */       
/* 1218 */       if ("" != null) return result;
/*      */     
/*      */     } 
/* 1221 */     if (result instanceof BuiltInBannedWhenAutoEscaping) {
/* 1222 */       if (this.outputFormat instanceof MarkupOutputFormat && this.autoEscaping) {
/* 1223 */         throw new ParseException("Using ?" + t.image + " (legacy escaping) is not allowed when auto-escaping is on with a markup output format (" + this.outputFormat
/*      */             
/* 1225 */             .getName() + "), to avoid double-escaping mistakes.", this.template, t);
/*      */       }
/*      */ 
/*      */       
/* 1229 */       if ("" != null) return result;
/*      */     
/*      */     } 
/* 1232 */     if (result instanceof MarkupOutputFormatBoundBuiltIn) {
/* 1233 */       if (!(this.outputFormat instanceof MarkupOutputFormat)) {
/* 1234 */         throw new ParseException("?" + t.image + " can't be used here, as the current output format isn't a markup (escaping) format: " + this.outputFormat, this.template, t);
/*      */       }
/*      */ 
/*      */       
/* 1238 */       ((MarkupOutputFormatBoundBuiltIn)result).bindToMarkupOutputFormat((MarkupOutputFormat)this.outputFormat);
/*      */       
/* 1240 */       if ("" != null) return result;
/*      */     
/*      */     } 
/* 1243 */     if (result instanceof OutputFormatBoundBuiltIn) {
/* 1244 */       ((OutputFormatBoundBuiltIn)result).bindToOutputFormat(this.outputFormat, this.autoEscapingPolicy);
/*      */       
/* 1246 */       if ("" != null) return result; 
/*      */     } 
/* 1248 */     if (result instanceof BuiltInWithParseTimeParameters && 
/* 1249 */       !((BuiltInWithParseTimeParameters)result).isLocalLambdaParameterSupported()) {
/* 1250 */       Token openParen = jj_consume_token(135);
/* 1251 */       args = PositionalArgs();
/* 1252 */       Token closeParen = jj_consume_token(136);
/* 1253 */       result.setLocation(this.template, lhoExp, closeParen);
/* 1254 */       ((BuiltInWithParseTimeParameters)result).bindToParameters(args, openParen, closeParen);
/*      */       
/* 1256 */       if ("" != null) return result;
/*      */     
/*      */     } 
/*      */     
/* 1260 */     if (result instanceof BuiltInWithParseTimeParameters && ((BuiltInWithParseTimeParameters)result)
/* 1261 */       .isLocalLambdaParameterSupported()) {
/* 1262 */       Token openParen = jj_consume_token(135);
/* 1263 */       args = PositionalMaybeLambdaArgs();
/* 1264 */       Token closeParen = jj_consume_token(136);
/* 1265 */       result.setLocation(this.template, lhoExp, closeParen);
/* 1266 */       ((BuiltInWithParseTimeParameters)result).bindToParameters(args, openParen, closeParen);
/*      */       
/* 1268 */       if ("" != null) return result;
/*      */     
/*      */     } 
/*      */     
/* 1272 */     if (jj_2_9(2147483647) && result instanceof BuiltInWithDirectCallOptimization) {
/* 1273 */       MethodCall methodCall = MethodArgs(result);
/* 1274 */       ((BuiltInWithDirectCallOptimization)result).setDirectlyCalled();
/* 1275 */       if ("" != null) return methodCall;
/*      */     
/*      */     } 
/*      */     
/* 1279 */     if (result instanceof BuiltInWithDirectCallOptimization)
/*      */     {
/* 1281 */       if ("" != null) return result;
/*      */     
/*      */     }
/*      */     
/* 1285 */     throw new AssertionError("Unhandled " + SpecialBuiltIn.class.getName() + " subclass: " + result.getClass());
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public final Expression LocalLambdaExpression() throws ParseException {
/*      */     Expression result;
/* 1292 */     if (jj_2_10(2147483647)) {
/* 1293 */       LocalLambdaExpression.LambdaParameterList lhs = LambdaExpressionParameterList();
/* 1294 */       jj_consume_token(119);
/* 1295 */       Expression rhs = OrExpression();
/* 1296 */       result = new LocalLambdaExpression(lhs, rhs);
/* 1297 */       if (lhs.getOpeningParenthesis() != null) {
/*      */         
/* 1299 */         result.setLocation(this.template, lhs.getOpeningParenthesis(), rhs);
/*      */       } else {
/*      */         
/* 1302 */         result.setLocation(this.template, lhs.getParameters().get(0), rhs);
/*      */       } 
/*      */     } else {
/* 1305 */       switch ((this.jj_ntk == -1) ? jj_ntk_f() : this.jj_ntk) {
/*      */         case 93:
/*      */         case 94:
/*      */         case 95:
/*      */         case 96:
/*      */         case 97:
/*      */         case 98:
/*      */         case 99:
/*      */         case 120:
/*      */         case 121:
/*      */         case 129:
/*      */         case 133:
/*      */         case 135:
/*      */         case 137:
/*      */         case 142:
/* 1320 */           result = OrExpression();
/*      */           break;
/*      */         
/*      */         default:
/* 1324 */           this.jj_la1[16] = this.jj_gen;
/* 1325 */           jj_consume_token(-1);
/* 1326 */           throw new ParseException();
/*      */       } 
/*      */     } 
/* 1329 */     if ("" != null) return result; 
/* 1330 */     throw new Error("Missing return statement in function");
/*      */   } public final LocalLambdaExpression.LambdaParameterList LambdaExpressionParameterList() throws ParseException {
/*      */     Identifier param;
/* 1333 */     Token openParen = null;
/* 1334 */     Token closeParen = null;
/* 1335 */     List<Identifier> params = null;
/*      */     
/* 1337 */     switch ((this.jj_ntk == -1) ? jj_ntk_f() : this.jj_ntk) {
/*      */       case 135:
/* 1339 */         openParen = jj_consume_token(135);
/* 1340 */         switch ((this.jj_ntk == -1) ? jj_ntk_f() : this.jj_ntk) {
/*      */           case 142:
/* 1342 */             param = Identifier();
/* 1343 */             params = new ArrayList<>(4);
/* 1344 */             params.add(param);
/*      */             
/*      */             while (true) {
/* 1347 */               switch ((this.jj_ntk == -1) ? jj_ntk_f() : this.jj_ntk) {
/*      */                 case 130:
/*      */                   break;
/*      */ 
/*      */                 
/*      */                 default:
/* 1353 */                   this.jj_la1[17] = this.jj_gen;
/*      */                   break;
/*      */               } 
/* 1356 */               jj_consume_token(130);
/* 1357 */               param = Identifier();
/* 1358 */               params.add(param);
/*      */             } 
/*      */             break;
/*      */           
/*      */           default:
/* 1363 */             this.jj_la1[18] = this.jj_gen;
/*      */             break;
/*      */         } 
/* 1366 */         closeParen = jj_consume_token(136);
/*      */         break;
/*      */       
/*      */       case 142:
/* 1370 */         param = Identifier();
/* 1371 */         params = Collections.singletonList(param);
/*      */         break;
/*      */       
/*      */       default:
/* 1375 */         this.jj_la1[19] = this.jj_gen;
/* 1376 */         jj_consume_token(-1);
/* 1377 */         throw new ParseException();
/*      */     } 
/* 1379 */     if ("" != null) return new LocalLambdaExpression.LambdaParameterList(openParen, (params != null) ? params : 
/*      */           
/* 1381 */           Collections.<Identifier>emptyList(), closeParen);
/*      */     
/* 1383 */     throw new Error("Missing return statement in function");
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public final Expression DotVariable(Expression exp) throws ParseException {
/*      */     Token t;
/* 1390 */     jj_consume_token(99);
/* 1391 */     switch ((this.jj_ntk == -1) ? jj_ntk_f() : this.jj_ntk) {
/*      */       case 142:
/* 1393 */         t = jj_consume_token(142);
/*      */         break;
/*      */       
/*      */       case 122:
/* 1397 */         t = jj_consume_token(122);
/*      */         break;
/*      */       
/*      */       case 123:
/* 1401 */         t = jj_consume_token(123);
/*      */         break;
/*      */       
/*      */       case 95:
/*      */       case 96:
/*      */       case 115:
/*      */       case 116:
/*      */       case 117:
/*      */       case 118:
/*      */       case 139:
/*      */       case 140:
/*      */       case 141:
/* 1413 */         switch ((this.jj_ntk == -1) ? jj_ntk_f() : this.jj_ntk) {
/*      */           case 115:
/* 1415 */             t = jj_consume_token(115);
/*      */             break;
/*      */           
/*      */           case 116:
/* 1419 */             t = jj_consume_token(116);
/*      */             break;
/*      */           
/*      */           case 117:
/* 1423 */             t = jj_consume_token(117);
/*      */             break;
/*      */           
/*      */           case 118:
/* 1427 */             t = jj_consume_token(118);
/*      */             break;
/*      */           
/*      */           case 95:
/* 1431 */             t = jj_consume_token(95);
/*      */             break;
/*      */           
/*      */           case 96:
/* 1435 */             t = jj_consume_token(96);
/*      */             break;
/*      */           
/*      */           case 139:
/* 1439 */             t = jj_consume_token(139);
/*      */             break;
/*      */           
/*      */           case 140:
/* 1443 */             t = jj_consume_token(140);
/*      */             break;
/*      */           
/*      */           case 141:
/* 1447 */             t = jj_consume_token(141);
/*      */             break;
/*      */           
/*      */           default:
/* 1451 */             this.jj_la1[20] = this.jj_gen;
/* 1452 */             jj_consume_token(-1);
/* 1453 */             throw new ParseException();
/*      */         } 
/* 1455 */         if (!Character.isLetter(t.image.charAt(0))) {
/* 1456 */           throw new ParseException(t.image + " is not a valid identifier.", this.template, t);
/*      */         }
/*      */         break;
/*      */       
/*      */       default:
/* 1461 */         this.jj_la1[21] = this.jj_gen;
/* 1462 */         jj_consume_token(-1);
/* 1463 */         throw new ParseException();
/*      */     } 
/* 1465 */     notListLiteral(exp, "hash");
/* 1466 */     notStringLiteral(exp, "hash");
/* 1467 */     notBooleanLiteral(exp, "hash");
/* 1468 */     Dot dot = new Dot(exp, t.image);
/* 1469 */     dot.setLocation(this.template, exp, t);
/* 1470 */     if ("" != null) return dot; 
/* 1471 */     throw new Error("Missing return statement in function");
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final Expression DynamicKey(Expression exp) throws ParseException {
/* 1480 */     jj_consume_token(133);
/* 1481 */     Expression arg = Expression();
/* 1482 */     Token t = jj_consume_token(134);
/* 1483 */     notBooleanLiteral(exp, "list or hash");
/* 1484 */     notNumberLiteral(exp, "list or hash");
/* 1485 */     DynamicKeyName dkn = new DynamicKeyName(exp, arg);
/* 1486 */     dkn.setLocation(this.template, exp, t);
/* 1487 */     if ("" != null) return dkn; 
/* 1488 */     throw new Error("Missing return statement in function");
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public final MethodCall MethodArgs(Expression exp) throws ParseException {
/* 1494 */     ArrayList args = new ArrayList();
/*      */     
/* 1496 */     jj_consume_token(135);
/* 1497 */     args = PositionalArgs();
/* 1498 */     Token end = jj_consume_token(136);
/* 1499 */     args.trimToSize();
/* 1500 */     MethodCall result = new MethodCall(exp, args);
/* 1501 */     result.setLocation(this.template, exp, end);
/* 1502 */     if ("" != null) return result; 
/* 1503 */     throw new Error("Missing return statement in function");
/*      */   } public final StringLiteral StringLiteral(boolean interpolate) throws ParseException {
/*      */     Token t;
/*      */     String s;
/* 1507 */     boolean raw = false;
/* 1508 */     switch ((this.jj_ntk == -1) ? jj_ntk_f() : this.jj_ntk) {
/*      */       case 93:
/* 1510 */         t = jj_consume_token(93);
/*      */         break;
/*      */       
/*      */       case 94:
/* 1514 */         t = jj_consume_token(94);
/* 1515 */         raw = true;
/*      */         break;
/*      */       
/*      */       default:
/* 1519 */         this.jj_la1[22] = this.jj_gen;
/* 1520 */         jj_consume_token(-1);
/* 1521 */         throw new ParseException();
/*      */     } 
/*      */ 
/*      */     
/* 1525 */     if (raw) {
/* 1526 */       s = t.image.substring(2, t.image.length() - 1);
/*      */     } else {
/*      */       try {
/* 1529 */         s = StringUtil.FTLStringLiteralDec(t.image.substring(1, t.image.length() - 1));
/* 1530 */       } catch (ParseException pe) {
/* 1531 */         pe.lineNumber = t.beginLine;
/* 1532 */         pe.columnNumber = t.beginColumn;
/* 1533 */         pe.endLineNumber = t.endLine;
/* 1534 */         pe.endColumnNumber = t.endColumn;
/* 1535 */         throw pe;
/*      */       } 
/*      */     } 
/* 1538 */     StringLiteral result = new StringLiteral(s);
/* 1539 */     result.setLocation(this.template, t, t);
/* 1540 */     if (interpolate && !raw) {
/*      */       
/* 1542 */       int interpolationSyntax = this.pCfg.getInterpolationSyntax();
/* 1543 */       if (((interpolationSyntax == 20 || interpolationSyntax == 21) && t.image
/*      */         
/* 1545 */         .indexOf("${") != -1) || (interpolationSyntax == 20 && t.image
/*      */         
/* 1547 */         .indexOf("#{") != -1) || (interpolationSyntax == 22 && t.image
/*      */         
/* 1549 */         .indexOf("[=") != -1)) {
/* 1550 */         result.parseValue(this, this.outputFormat);
/*      */       }
/*      */     } 
/* 1553 */     if ("" != null) return result; 
/* 1554 */     throw new Error("Missing return statement in function");
/*      */   }
/*      */   public final Expression BooleanLiteral() throws ParseException {
/*      */     Token t;
/*      */     Expression result;
/* 1559 */     switch ((this.jj_ntk == -1) ? jj_ntk_f() : this.jj_ntk) {
/*      */       case 95:
/* 1561 */         t = jj_consume_token(95);
/* 1562 */         result = new BooleanLiteral(false);
/*      */         break;
/*      */       
/*      */       case 96:
/* 1566 */         t = jj_consume_token(96);
/* 1567 */         result = new BooleanLiteral(true);
/*      */         break;
/*      */       
/*      */       default:
/* 1571 */         this.jj_la1[23] = this.jj_gen;
/* 1572 */         jj_consume_token(-1);
/* 1573 */         throw new ParseException();
/*      */     } 
/* 1575 */     result.setLocation(this.template, t, t);
/* 1576 */     if ("" != null) return result; 
/* 1577 */     throw new Error("Missing return statement in function");
/*      */   }
/*      */   
/*      */   public final HashLiteral HashLiteral() throws ParseException {
/*      */     Expression key, value;
/* 1582 */     ArrayList<Expression> keys = new ArrayList<>();
/* 1583 */     ArrayList<Expression> values = new ArrayList<>();
/* 1584 */     Token begin = jj_consume_token(137);
/* 1585 */     switch ((this.jj_ntk == -1) ? jj_ntk_f() : this.jj_ntk) {
/*      */       case 93:
/*      */       case 94:
/*      */       case 95:
/*      */       case 96:
/*      */       case 97:
/*      */       case 98:
/*      */       case 99:
/*      */       case 120:
/*      */       case 121:
/*      */       case 129:
/*      */       case 133:
/*      */       case 135:
/*      */       case 137:
/*      */       case 142:
/* 1600 */         key = Expression();
/* 1601 */         switch ((this.jj_ntk == -1) ? jj_ntk_f() : this.jj_ntk) {
/*      */           case 130:
/* 1603 */             jj_consume_token(130);
/*      */             break;
/*      */           
/*      */           case 132:
/* 1607 */             jj_consume_token(132);
/*      */             break;
/*      */           
/*      */           default:
/* 1611 */             this.jj_la1[24] = this.jj_gen;
/* 1612 */             jj_consume_token(-1);
/* 1613 */             throw new ParseException();
/*      */         } 
/* 1615 */         value = Expression();
/* 1616 */         stringLiteralOnly(key);
/* 1617 */         keys.add(key);
/* 1618 */         values.add(value);
/*      */         
/*      */         while (true) {
/* 1621 */           switch ((this.jj_ntk == -1) ? jj_ntk_f() : this.jj_ntk) {
/*      */             case 130:
/*      */               break;
/*      */ 
/*      */             
/*      */             default:
/* 1627 */               this.jj_la1[25] = this.jj_gen;
/*      */               break;
/*      */           } 
/* 1630 */           jj_consume_token(130);
/* 1631 */           key = Expression();
/* 1632 */           switch ((this.jj_ntk == -1) ? jj_ntk_f() : this.jj_ntk) {
/*      */             case 130:
/* 1634 */               jj_consume_token(130);
/*      */               break;
/*      */             
/*      */             case 132:
/* 1638 */               jj_consume_token(132);
/*      */               break;
/*      */             
/*      */             default:
/* 1642 */               this.jj_la1[26] = this.jj_gen;
/* 1643 */               jj_consume_token(-1);
/* 1644 */               throw new ParseException();
/*      */           } 
/* 1646 */           value = Expression();
/* 1647 */           stringLiteralOnly(key);
/* 1648 */           keys.add(key);
/* 1649 */           values.add(value);
/*      */         } 
/*      */         break;
/*      */       
/*      */       default:
/* 1654 */         this.jj_la1[27] = this.jj_gen;
/*      */         break;
/*      */     } 
/* 1657 */     Token end = jj_consume_token(138);
/* 1658 */     keys.trimToSize();
/* 1659 */     values.trimToSize();
/* 1660 */     HashLiteral result = new HashLiteral(keys, values);
/* 1661 */     result.setLocation(this.template, begin, end);
/* 1662 */     if ("" != null) return result; 
/* 1663 */     throw new Error("Missing return statement in function");
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public final DollarVariable StringOutput() throws ParseException {
/*      */     Expression exp;
/*      */     Token begin, end;
/* 1671 */     switch ((this.jj_ntk == -1) ? jj_ntk_f() : this.jj_ntk) {
/*      */       case 82:
/* 1673 */         begin = jj_consume_token(82);
/* 1674 */         exp = Expression();
/* 1675 */         end = jj_consume_token(138);
/*      */         break;
/*      */       
/*      */       case 84:
/* 1679 */         begin = jj_consume_token(84);
/* 1680 */         exp = Expression();
/* 1681 */         end = jj_consume_token(134);
/*      */         break;
/*      */       
/*      */       default:
/* 1685 */         this.jj_la1[28] = this.jj_gen;
/* 1686 */         jj_consume_token(-1);
/* 1687 */         throw new ParseException();
/*      */     } 
/* 1689 */     notHashLiteral(exp, "string or something automatically convertible to string (number, date or boolean)");
/* 1690 */     notListLiteral(exp, "string or something automatically convertible to string (number, date or boolean)");
/*      */ 
/*      */     
/* 1693 */     DollarVariable result = new DollarVariable(exp, escapedExpression(exp), this.outputFormat, this.autoEscaping);
/*      */ 
/*      */     
/* 1696 */     result.setLocation(this.template, begin, end);
/* 1697 */     if ("" != null) return result; 
/* 1698 */     throw new Error("Missing return statement in function");
/*      */   }
/*      */   
/*      */   public final NumericalOutput NumericalOutput() throws ParseException {
/*      */     NumericalOutput result;
/* 1703 */     Token fmt = null;
/* 1704 */     Token begin = jj_consume_token(83);
/* 1705 */     Expression exp = Expression();
/* 1706 */     numberLiteralOnly(exp);
/* 1707 */     switch ((this.jj_ntk == -1) ? jj_ntk_f() : this.jj_ntk) {
/*      */       case 131:
/* 1709 */         jj_consume_token(131);
/* 1710 */         fmt = jj_consume_token(142);
/*      */         break;
/*      */       
/*      */       default:
/* 1714 */         this.jj_la1[29] = this.jj_gen;
/*      */         break;
/*      */     } 
/* 1717 */     Token end = jj_consume_token(138);
/* 1718 */     MarkupOutputFormat<?> autoEscOF = (this.autoEscaping && this.outputFormat instanceof MarkupOutputFormat) ? (MarkupOutputFormat)this.outputFormat : null;
/*      */ 
/*      */ 
/*      */     
/* 1722 */     if (fmt != null) {
/* 1723 */       int minFrac = -1;
/* 1724 */       int maxFrac = -1;
/*      */       
/* 1726 */       StringTokenizer st = new StringTokenizer(fmt.image, "mM", true);
/* 1727 */       char type = '-';
/* 1728 */       while (st.hasMoreTokens()) {
/* 1729 */         String token = st.nextToken();
/*      */         try {
/* 1731 */           if (type != '-') {
/* 1732 */             switch (type) {
/*      */               case 'm':
/* 1734 */                 if (minFrac != -1) throw new ParseException("Invalid formatting string", this.template, fmt); 
/* 1735 */                 minFrac = Integer.parseInt(token);
/*      */                 break;
/*      */               case 'M':
/* 1738 */                 if (maxFrac != -1) throw new ParseException("Invalid formatting string", this.template, fmt); 
/* 1739 */                 maxFrac = Integer.parseInt(token);
/*      */                 break;
/*      */               default:
/* 1742 */                 throw new ParseException("Invalid formatting string", this.template, fmt);
/*      */             } 
/* 1744 */             type = '-'; continue;
/* 1745 */           }  if (token.equals("m")) {
/* 1746 */             type = 'm'; continue;
/* 1747 */           }  if (token.equals("M")) {
/* 1748 */             type = 'M'; continue;
/*      */           } 
/* 1750 */           throw new ParseException();
/*      */         }
/* 1752 */         catch (ParseException e) {
/* 1753 */           throw new ParseException("Invalid format specifier " + fmt.image, this.template, fmt);
/* 1754 */         } catch (NumberFormatException e) {
/* 1755 */           throw new ParseException("Invalid number in the format specifier " + fmt.image, this.template, fmt);
/*      */         } 
/*      */       } 
/*      */       
/* 1759 */       if (maxFrac == -1) {
/* 1760 */         if (minFrac == -1) {
/* 1761 */           throw new ParseException("Invalid format specification, at least one of m and M must be specified!", this.template, fmt);
/*      */         }
/*      */         
/* 1764 */         maxFrac = minFrac;
/* 1765 */       } else if (minFrac == -1) {
/* 1766 */         minFrac = 0;
/*      */       } 
/* 1768 */       if (minFrac > maxFrac) {
/* 1769 */         throw new ParseException("Invalid format specification, min cannot be greater than max!", this.template, fmt);
/*      */       }
/*      */       
/* 1772 */       if (minFrac > 50 || maxFrac > 50) {
/* 1773 */         throw new ParseException("Cannot specify more than 50 fraction digits", this.template, fmt);
/*      */       }
/* 1775 */       result = new NumericalOutput(exp, minFrac, maxFrac, autoEscOF);
/*      */     } else {
/* 1777 */       result = new NumericalOutput(exp, autoEscOF);
/*      */     } 
/* 1779 */     result.setLocation(this.template, begin, end);
/* 1780 */     if ("" != null) return result; 
/* 1781 */     throw new Error("Missing return statement in function");
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final TemplateElement If() throws ParseException {
/* 1789 */     Token t, start = jj_consume_token(8);
/* 1790 */     Expression condition = Expression();
/* 1791 */     Token end = jj_consume_token(148);
/* 1792 */     TemplateElements children = MixedContentElements();
/* 1793 */     ConditionalBlock cblock = new ConditionalBlock(condition, children, 0);
/* 1794 */     cblock.setLocation(this.template, start, end, children);
/* 1795 */     IfBlock ifBlock = new IfBlock(cblock);
/*      */     
/*      */     while (true) {
/* 1798 */       switch ((this.jj_ntk == -1) ? jj_ntk_f() : this.jj_ntk) {
/*      */         case 9:
/*      */           break;
/*      */ 
/*      */         
/*      */         default:
/* 1804 */           this.jj_la1[30] = this.jj_gen;
/*      */           break;
/*      */       } 
/* 1807 */       Token token = jj_consume_token(9);
/* 1808 */       condition = Expression();
/* 1809 */       end = LooseDirectiveEnd();
/* 1810 */       children = MixedContentElements();
/* 1811 */       cblock = new ConditionalBlock(condition, children, 2);
/* 1812 */       cblock.setLocation(this.template, token, end, children);
/* 1813 */       ifBlock.addBlock(cblock);
/*      */     } 
/* 1815 */     switch ((this.jj_ntk == -1) ? jj_ntk_f() : this.jj_ntk) {
/*      */       case 54:
/* 1817 */         t = jj_consume_token(54);
/* 1818 */         children = MixedContentElements();
/* 1819 */         cblock = new ConditionalBlock(null, children, 1);
/* 1820 */         cblock.setLocation(this.template, t, t, children);
/* 1821 */         ifBlock.addBlock(cblock);
/*      */         break;
/*      */       
/*      */       default:
/* 1825 */         this.jj_la1[31] = this.jj_gen;
/*      */         break;
/*      */     } 
/* 1828 */     end = jj_consume_token(36);
/* 1829 */     ifBlock.setLocation(this.template, start, end);
/* 1830 */     if ("" != null) return ifBlock; 
/* 1831 */     throw new Error("Missing return statement in function");
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public final AttemptBlock Attempt() throws ParseException {
/* 1837 */     Token end, start = jj_consume_token(6);
/* 1838 */     TemplateElements children = MixedContentElements();
/* 1839 */     RecoveryBlock recoveryBlock = Recover();
/* 1840 */     switch ((this.jj_ntk == -1) ? jj_ntk_f() : this.jj_ntk) {
/*      */       case 40:
/* 1842 */         end = jj_consume_token(40);
/*      */         break;
/*      */       
/*      */       case 41:
/* 1846 */         end = jj_consume_token(41);
/*      */         break;
/*      */       
/*      */       default:
/* 1850 */         this.jj_la1[32] = this.jj_gen;
/* 1851 */         jj_consume_token(-1);
/* 1852 */         throw new ParseException();
/*      */     } 
/* 1854 */     AttemptBlock result = new AttemptBlock(children, recoveryBlock);
/* 1855 */     result.setLocation(this.template, start, end);
/* 1856 */     if ("" != null) return result; 
/* 1857 */     throw new Error("Missing return statement in function");
/*      */   }
/*      */ 
/*      */   
/*      */   public final RecoveryBlock Recover() throws ParseException {
/* 1862 */     Token start = jj_consume_token(7);
/* 1863 */     TemplateElements children = MixedContentElements();
/* 1864 */     RecoveryBlock result = new RecoveryBlock(children);
/* 1865 */     result.setLocation(this.template, start, start, children);
/* 1866 */     if ("" != null) return result; 
/* 1867 */     throw new Error("Missing return statement in function");
/*      */   }
/*      */   public final TemplateElement List() throws ParseException {
/*      */     TemplateElement result;
/* 1871 */     Token loopVar = null, loopVar2 = null;
/*      */     
/* 1873 */     ElseOfList elseOfList = null;
/*      */     
/* 1875 */     Token start = jj_consume_token(10);
/* 1876 */     Expression exp = Expression();
/* 1877 */     switch ((this.jj_ntk == -1) ? jj_ntk_f() : this.jj_ntk) {
/*      */       case 140:
/* 1879 */         jj_consume_token(140);
/* 1880 */         loopVar = jj_consume_token(142);
/* 1881 */         switch ((this.jj_ntk == -1) ? jj_ntk_f() : this.jj_ntk) {
/*      */           case 130:
/* 1883 */             jj_consume_token(130);
/* 1884 */             loopVar2 = jj_consume_token(142);
/*      */             break;
/*      */         } 
/*      */         
/* 1888 */         this.jj_la1[33] = this.jj_gen;
/*      */         break;
/*      */ 
/*      */ 
/*      */       
/*      */       default:
/* 1894 */         this.jj_la1[34] = this.jj_gen;
/*      */         break;
/*      */     } 
/* 1897 */     jj_consume_token(148);
/* 1898 */     ParserIteratorBlockContext iterCtx = pushIteratorBlockContext();
/* 1899 */     if (loopVar != null) {
/* 1900 */       iterCtx.loopVarName = loopVar.image;
/* 1901 */       this.breakableDirectiveNesting++;
/* 1902 */       this.continuableDirectiveNesting++;
/* 1903 */       if (loopVar2 != null) {
/* 1904 */         iterCtx.loopVar2Name = loopVar2.image;
/* 1905 */         iterCtx.hashListing = true;
/* 1906 */         if (iterCtx.loopVar2Name.equals(iterCtx.loopVarName)) {
/* 1907 */           throw new ParseException("The key and value loop variable names must differ, but both were: " + iterCtx
/* 1908 */               .loopVarName, this.template, start);
/*      */         }
/*      */       } 
/*      */     } 
/*      */     
/* 1913 */     TemplateElements childrendBeforeElse = MixedContentElements();
/* 1914 */     if (loopVar != null) {
/* 1915 */       this.breakableDirectiveNesting--;
/* 1916 */       this.continuableDirectiveNesting--;
/* 1917 */     } else if (iterCtx.kind != 2) {
/* 1918 */       throw new ParseException("#list must have either \"as loopVar\" parameter or nested #items that belongs to it.", this.template, start);
/*      */     } 
/*      */ 
/*      */     
/* 1922 */     popIteratorBlockContext();
/* 1923 */     switch ((this.jj_ntk == -1) ? jj_ntk_f() : this.jj_ntk) {
/*      */       case 54:
/* 1925 */         elseOfList = ElseOfList();
/*      */         break;
/*      */       
/*      */       default:
/* 1929 */         this.jj_la1[35] = this.jj_gen;
/*      */         break;
/*      */     } 
/* 1932 */     Token end = jj_consume_token(37);
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1937 */     IteratorBlock list = new IteratorBlock(exp, (loopVar != null) ? loopVar.image : null, (loopVar2 != null) ? loopVar2.image : null, childrendBeforeElse, iterCtx.hashListing, false);
/* 1938 */     list.setLocation(this.template, start, end);
/*      */ 
/*      */     
/* 1941 */     if (elseOfList == null) {
/* 1942 */       result = list;
/*      */     } else {
/* 1944 */       result = new ListElseContainer(list, elseOfList);
/* 1945 */       result.setLocation(this.template, start, end);
/*      */     } 
/* 1947 */     if ("" != null) return result; 
/* 1948 */     throw new Error("Missing return statement in function");
/*      */   }
/*      */ 
/*      */   
/*      */   public final ElseOfList ElseOfList() throws ParseException {
/* 1953 */     Token start = jj_consume_token(54);
/* 1954 */     TemplateElements children = MixedContentElements();
/* 1955 */     ElseOfList result = new ElseOfList(children);
/* 1956 */     result.setLocation(this.template, start, start, children);
/* 1957 */     if ("" != null) return result; 
/* 1958 */     throw new Error("Missing return statement in function");
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public final IteratorBlock ForEach() throws ParseException {
/* 1964 */     Token start = jj_consume_token(13);
/* 1965 */     Token loopVar = jj_consume_token(142);
/* 1966 */     jj_consume_token(139);
/* 1967 */     Expression exp = Expression();
/* 1968 */     jj_consume_token(148);
/* 1969 */     ParserIteratorBlockContext iterCtx = pushIteratorBlockContext();
/* 1970 */     iterCtx.loopVarName = loopVar.image;
/* 1971 */     iterCtx.kind = 1;
/* 1972 */     this.breakableDirectiveNesting++;
/* 1973 */     this.continuableDirectiveNesting++;
/* 1974 */     TemplateElements children = MixedContentElements();
/* 1975 */     Token end = jj_consume_token(42);
/* 1976 */     this.breakableDirectiveNesting--;
/* 1977 */     this.continuableDirectiveNesting--;
/* 1978 */     popIteratorBlockContext();
/*      */     
/* 1980 */     IteratorBlock result = new IteratorBlock(exp, loopVar.image, null, children, false, true);
/* 1981 */     result.setLocation(this.template, start, end);
/* 1982 */     if ("" != null) return result; 
/* 1983 */     throw new Error("Missing return statement in function");
/*      */   }
/*      */   public final Items Items() throws ParseException {
/* 1986 */     Token loopVar2 = null;
/*      */ 
/*      */     
/* 1989 */     Token start = jj_consume_token(11);
/* 1990 */     Token loopVar = jj_consume_token(142);
/* 1991 */     switch ((this.jj_ntk == -1) ? jj_ntk_f() : this.jj_ntk) {
/*      */       case 130:
/* 1993 */         jj_consume_token(130);
/* 1994 */         loopVar2 = jj_consume_token(142);
/*      */         break;
/*      */       
/*      */       default:
/* 1998 */         this.jj_la1[36] = this.jj_gen;
/*      */         break;
/*      */     } 
/* 2001 */     jj_consume_token(148);
/* 2002 */     ParserIteratorBlockContext iterCtx = peekIteratorBlockContext();
/* 2003 */     if (iterCtx == null) {
/* 2004 */       throw new ParseException("#items must be inside a #list block.", this.template, start);
/*      */     }
/* 2006 */     if (iterCtx.loopVarName != null) {
/*      */       String msg;
/* 2008 */       if (iterCtx.kind == 1) {
/* 2009 */         msg = forEachDirectiveSymbol() + " doesn't support nested #items.";
/* 2010 */       } else if (iterCtx.kind == 2) {
/* 2011 */         msg = "Can't nest #items into each other when they belong to the same #list.";
/*      */       } else {
/* 2013 */         msg = "The parent #list of the #items must not have \"as loopVar\" parameter.";
/*      */       } 
/* 2015 */       throw new ParseException(msg, this.template, start);
/*      */     } 
/* 2017 */     iterCtx.kind = 2;
/* 2018 */     iterCtx.loopVarName = loopVar.image;
/* 2019 */     if (loopVar2 != null) {
/* 2020 */       iterCtx.loopVar2Name = loopVar2.image;
/* 2021 */       iterCtx.hashListing = true;
/* 2022 */       if (iterCtx.loopVar2Name.equals(iterCtx.loopVarName)) {
/* 2023 */         throw new ParseException("The key and value loop variable names must differ, but both were: " + iterCtx
/* 2024 */             .loopVarName, this.template, start);
/*      */       }
/*      */     } 
/*      */ 
/*      */     
/* 2029 */     this.breakableDirectiveNesting++;
/* 2030 */     this.continuableDirectiveNesting++;
/* 2031 */     TemplateElements children = MixedContentElements();
/* 2032 */     Token end = jj_consume_token(38);
/* 2033 */     this.breakableDirectiveNesting--;
/* 2034 */     this.continuableDirectiveNesting--;
/* 2035 */     iterCtx.loopVarName = null;
/* 2036 */     iterCtx.loopVar2Name = null;
/*      */     
/* 2038 */     Items result = new Items(loopVar.image, (loopVar2 != null) ? loopVar2.image : null, children);
/* 2039 */     result.setLocation(this.template, start, end);
/* 2040 */     if ("" != null) return result; 
/* 2041 */     throw new Error("Missing return statement in function");
/*      */   }
/*      */   public final Sep Sep() throws ParseException {
/* 2044 */     Token end = null;
/*      */     
/* 2046 */     Token start = jj_consume_token(12);
/* 2047 */     if (peekIteratorBlockContext() == null) {
/* 2048 */       throw new ParseException("#sep must be inside a #list (or " + 
/* 2049 */           forEachDirectiveSymbol() + ") block.", this.template, start);
/*      */     }
/*      */     
/* 2052 */     TemplateElements children = MixedContentElements();
/* 2053 */     switch ((this.jj_ntk == -1) ? jj_ntk_f() : this.jj_ntk) {
/*      */       case 39:
/* 2055 */         end = jj_consume_token(39);
/*      */         break;
/*      */       
/*      */       default:
/* 2059 */         this.jj_la1[37] = this.jj_gen;
/*      */         break;
/*      */     } 
/* 2062 */     Sep result = new Sep(children);
/* 2063 */     if (end != null) {
/* 2064 */       result.setLocation(this.template, start, end);
/*      */     } else {
/* 2066 */       result.setLocation(this.template, start, start, children);
/*      */     } 
/* 2068 */     if ("" != null) return result; 
/* 2069 */     throw new Error("Missing return statement in function");
/*      */   }
/*      */   
/*      */   public final VisitNode Visit() throws ParseException {
/* 2073 */     Expression namespaces = null;
/* 2074 */     Token start = jj_consume_token(24);
/* 2075 */     Expression targetNode = Expression();
/* 2076 */     switch ((this.jj_ntk == -1) ? jj_ntk_f() : this.jj_ntk) {
/*      */       case 141:
/* 2078 */         jj_consume_token(141);
/* 2079 */         namespaces = Expression();
/*      */         break;
/*      */       
/*      */       default:
/* 2083 */         this.jj_la1[38] = this.jj_gen;
/*      */         break;
/*      */     } 
/* 2086 */     Token end = LooseDirectiveEnd();
/* 2087 */     VisitNode result = new VisitNode(targetNode, namespaces);
/* 2088 */     result.setLocation(this.template, start, end);
/* 2089 */     if ("" != null) return result; 
/* 2090 */     throw new Error("Missing return statement in function");
/*      */   }
/*      */   public final RecurseNode Recurse() throws ParseException {
/* 2093 */     Token start, end = null;
/* 2094 */     Expression node = null, namespaces = null;
/* 2095 */     switch ((this.jj_ntk == -1) ? jj_ntk_f() : this.jj_ntk) {
/*      */       case 67:
/* 2097 */         start = jj_consume_token(67);
/*      */         break;
/*      */       
/*      */       case 68:
/* 2101 */         start = jj_consume_token(68);
/* 2102 */         switch ((this.jj_ntk == -1) ? jj_ntk_f() : this.jj_ntk) {
/*      */           case 93:
/*      */           case 94:
/*      */           case 95:
/*      */           case 96:
/*      */           case 97:
/*      */           case 98:
/*      */           case 99:
/*      */           case 120:
/*      */           case 121:
/*      */           case 129:
/*      */           case 133:
/*      */           case 135:
/*      */           case 137:
/*      */           case 142:
/* 2117 */             node = Expression();
/*      */             break;
/*      */           
/*      */           default:
/* 2121 */             this.jj_la1[39] = this.jj_gen;
/*      */             break;
/*      */         } 
/* 2124 */         switch ((this.jj_ntk == -1) ? jj_ntk_f() : this.jj_ntk) {
/*      */           case 141:
/* 2126 */             jj_consume_token(141);
/* 2127 */             namespaces = Expression();
/*      */             break;
/*      */           
/*      */           default:
/* 2131 */             this.jj_la1[40] = this.jj_gen;
/*      */             break;
/*      */         } 
/* 2134 */         end = LooseDirectiveEnd();
/*      */         break;
/*      */       
/*      */       default:
/* 2138 */         this.jj_la1[41] = this.jj_gen;
/* 2139 */         jj_consume_token(-1);
/* 2140 */         throw new ParseException();
/*      */     } 
/* 2142 */     if (end == null) end = start; 
/* 2143 */     RecurseNode result = new RecurseNode(node, namespaces);
/* 2144 */     result.setLocation(this.template, start, end);
/* 2145 */     if ("" != null) return result; 
/* 2146 */     throw new Error("Missing return statement in function");
/*      */   }
/*      */   
/*      */   public final FallbackInstruction FallBack() throws ParseException {
/* 2150 */     Token tok = jj_consume_token(69);
/* 2151 */     if (!this.inMacro) {
/* 2152 */       throw new ParseException("Cannot fall back outside a macro.", this.template, tok);
/*      */     }
/* 2154 */     FallbackInstruction result = new FallbackInstruction();
/* 2155 */     result.setLocation(this.template, tok, tok);
/* 2156 */     if ("" != null) return result; 
/* 2157 */     throw new Error("Missing return statement in function");
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final BreakInstruction Break() throws ParseException {
/* 2164 */     Token start = jj_consume_token(55);
/* 2165 */     if (this.breakableDirectiveNesting < 1) {
/* 2166 */       throw new ParseException(start.image + " must be nested inside a directive that supports it:  #list with \"as\", #items, #switch (or the deprecated " + 
/* 2167 */           forEachDirectiveSymbol() + ")", this.template, start);
/*      */     }
/*      */     
/* 2170 */     BreakInstruction result = new BreakInstruction();
/* 2171 */     result.setLocation(this.template, start, start);
/* 2172 */     if ("" != null) return result; 
/* 2173 */     throw new Error("Missing return statement in function");
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final ContinueInstruction Continue() throws ParseException {
/* 2180 */     Token start = jj_consume_token(56);
/* 2181 */     if (this.continuableDirectiveNesting < 1) {
/* 2182 */       throw new ParseException(start.image + " must be nested inside a directive that supports it:  #list with \"as\", #items (or the deprecated " + 
/* 2183 */           forEachDirectiveSymbol() + ")", this.template, start);
/*      */     }
/*      */     
/* 2186 */     ContinueInstruction result = new ContinueInstruction();
/* 2187 */     result.setLocation(this.template, start, start);
/* 2188 */     if ("" != null) return result; 
/* 2189 */     throw new Error("Missing return statement in function");
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final ReturnInstruction Return() throws ParseException {
/* 2196 */     Token start, end = null;
/* 2197 */     Expression exp = null;
/* 2198 */     switch ((this.jj_ntk == -1) ? jj_ntk_f() : this.jj_ntk) {
/*      */       case 57:
/* 2200 */         start = jj_consume_token(57);
/* 2201 */         end = start;
/*      */         break;
/*      */       
/*      */       case 26:
/* 2205 */         start = jj_consume_token(26);
/* 2206 */         exp = Expression();
/* 2207 */         end = LooseDirectiveEnd();
/*      */         break;
/*      */       
/*      */       default:
/* 2211 */         this.jj_la1[42] = this.jj_gen;
/* 2212 */         jj_consume_token(-1);
/* 2213 */         throw new ParseException();
/*      */     } 
/* 2215 */     if (this.inMacro) {
/* 2216 */       if (exp != null) {
/* 2217 */         throw new ParseException("A macro cannot return a value", this.template, start);
/*      */       }
/* 2219 */     } else if (this.inFunction) {
/* 2220 */       if (exp == null) {
/* 2221 */         throw new ParseException("A function must return a value", this.template, start);
/*      */       }
/*      */     }
/* 2224 */     else if (exp == null) {
/* 2225 */       throw new ParseException("A return instruction can only occur inside a macro or function", this.template, start);
/*      */     } 
/*      */ 
/*      */     
/* 2229 */     ReturnInstruction result = new ReturnInstruction(exp);
/* 2230 */     result.setLocation(this.template, start, end);
/* 2231 */     if ("" != null) return result; 
/* 2232 */     throw new Error("Missing return statement in function");
/*      */   }
/*      */   public final StopInstruction Stop() throws ParseException {
/* 2235 */     Token start = null;
/* 2236 */     Expression exp = null;
/* 2237 */     switch ((this.jj_ntk == -1) ? jj_ntk_f() : this.jj_ntk) {
/*      */       case 58:
/* 2239 */         start = jj_consume_token(58);
/*      */         break;
/*      */       
/*      */       case 25:
/* 2243 */         start = jj_consume_token(25);
/* 2244 */         exp = Expression();
/* 2245 */         LooseDirectiveEnd();
/*      */         break;
/*      */       
/*      */       default:
/* 2249 */         this.jj_la1[43] = this.jj_gen;
/* 2250 */         jj_consume_token(-1);
/* 2251 */         throw new ParseException();
/*      */     } 
/* 2253 */     StopInstruction result = new StopInstruction(exp);
/* 2254 */     result.setLocation(this.template, start, start);
/* 2255 */     if ("" != null) return result; 
/* 2256 */     throw new Error("Missing return statement in function");
/*      */   }
/*      */   public final TemplateElement Nested() throws ParseException {
/*      */     Token t, end;
/*      */     ArrayList bodyParameters;
/* 2261 */     BodyInstruction result = null;
/* 2262 */     switch ((this.jj_ntk == -1) ? jj_ntk_f() : this.jj_ntk) {
/*      */       case 65:
/* 2264 */         t = jj_consume_token(65);
/* 2265 */         result = new BodyInstruction(null);
/* 2266 */         result.setLocation(this.template, t, t);
/*      */         break;
/*      */       
/*      */       case 66:
/* 2270 */         t = jj_consume_token(66);
/* 2271 */         bodyParameters = PositionalArgs();
/* 2272 */         end = LooseDirectiveEnd();
/* 2273 */         result = new BodyInstruction(bodyParameters);
/* 2274 */         result.setLocation(this.template, t, end);
/*      */         break;
/*      */       
/*      */       default:
/* 2278 */         this.jj_la1[44] = this.jj_gen;
/* 2279 */         jj_consume_token(-1);
/* 2280 */         throw new ParseException();
/*      */     } 
/* 2282 */     if (!this.inMacro) {
/* 2283 */       throw new ParseException("Cannot use a " + t.image + " instruction outside a macro.", this.template, t);
/*      */     }
/* 2285 */     if ("" != null) return result; 
/* 2286 */     throw new Error("Missing return statement in function");
/*      */   }
/*      */   
/*      */   public final TemplateElement Flush() throws ParseException {
/* 2290 */     Token t = jj_consume_token(59);
/* 2291 */     FlushInstruction result = new FlushInstruction();
/* 2292 */     result.setLocation(this.template, t, t);
/* 2293 */     if ("" != null) return result; 
/* 2294 */     throw new Error("Missing return statement in function");
/*      */   }
/*      */   
/*      */   public final TemplateElement Trim() throws ParseException { Token t;
/* 2298 */     TrimInstruction result = null;
/* 2299 */     switch ((this.jj_ntk == -1) ? jj_ntk_f() : this.jj_ntk) {
/*      */       case 60:
/* 2301 */         t = jj_consume_token(60);
/* 2302 */         result = new TrimInstruction(true, true);
/*      */         break;
/*      */       
/*      */       case 61:
/* 2306 */         t = jj_consume_token(61);
/* 2307 */         result = new TrimInstruction(true, false);
/*      */         break;
/*      */       
/*      */       case 62:
/* 2311 */         t = jj_consume_token(62);
/* 2312 */         result = new TrimInstruction(false, true);
/*      */         break;
/*      */       
/*      */       case 63:
/* 2316 */         t = jj_consume_token(63);
/* 2317 */         result = new TrimInstruction(false, false);
/*      */         break;
/*      */       
/*      */       default:
/* 2321 */         this.jj_la1[45] = this.jj_gen;
/* 2322 */         jj_consume_token(-1);
/* 2323 */         throw new ParseException();
/*      */     } 
/* 2325 */     result.setLocation(this.template, t, t);
/* 2326 */     if ("" != null) return result; 
/* 2327 */     throw new Error("Missing return statement in function"); } public final TemplateElement Assign() throws ParseException { Token start, end; int scope; Token equalsOp;
/*      */     Expression exp;
/*      */     Assignment ass;
/*      */     TemplateElements children;
/*      */     BlockAssignment ba;
/* 2332 */     Token id = null;
/*      */     
/* 2334 */     Expression nsExp = null;
/*      */     
/* 2336 */     ArrayList<Assignment> assignments = new ArrayList();
/*      */ 
/*      */     
/* 2339 */     switch ((this.jj_ntk == -1) ? jj_ntk_f() : this.jj_ntk) {
/*      */       case 16:
/* 2341 */         start = jj_consume_token(16);
/* 2342 */         scope = 1;
/*      */         break;
/*      */       
/*      */       case 17:
/* 2346 */         start = jj_consume_token(17);
/* 2347 */         scope = 3;
/*      */         break;
/*      */       
/*      */       case 18:
/* 2351 */         start = jj_consume_token(18);
/* 2352 */         scope = 2;
/* 2353 */         scope = 2;
/* 2354 */         if (!this.inMacro && !this.inFunction) {
/* 2355 */           throw new ParseException("Local variable assigned outside a macro.", this.template, start);
/*      */         }
/*      */         break;
/*      */       
/*      */       default:
/* 2360 */         this.jj_la1[46] = this.jj_gen;
/* 2361 */         jj_consume_token(-1);
/* 2362 */         throw new ParseException();
/*      */     } 
/* 2364 */     Expression nameExp = IdentifierOrStringLiteral();
/*      */ 
/*      */     
/* 2367 */     String varName = (nameExp instanceof StringLiteral) ? ((StringLiteral)nameExp).getAsString() : ((Identifier)nameExp).getName();
/* 2368 */     switch ((this.jj_ntk == -1) ? jj_ntk_f() : this.jj_ntk) {
/*      */       case 105:
/*      */       case 108:
/*      */       case 109:
/*      */       case 110:
/*      */       case 111:
/*      */       case 112:
/*      */       case 113:
/*      */       case 114:
/* 2377 */         switch ((this.jj_ntk == -1) ? jj_ntk_f() : this.jj_ntk) {
/*      */           case 105:
/*      */           case 108:
/*      */           case 109:
/*      */           case 110:
/*      */           case 111:
/*      */           case 112:
/* 2384 */             switch ((this.jj_ntk == -1) ? jj_ntk_f() : this.jj_ntk) {
/*      */               case 105:
/* 2386 */                 jj_consume_token(105);
/*      */                 break;
/*      */               
/*      */               case 108:
/* 2390 */                 jj_consume_token(108);
/*      */                 break;
/*      */               
/*      */               case 109:
/* 2394 */                 jj_consume_token(109);
/*      */                 break;
/*      */               
/*      */               case 110:
/* 2398 */                 jj_consume_token(110);
/*      */                 break;
/*      */               
/*      */               case 111:
/* 2402 */                 jj_consume_token(111);
/*      */                 break;
/*      */               
/*      */               case 112:
/* 2406 */                 jj_consume_token(112);
/*      */                 break;
/*      */               
/*      */               default:
/* 2410 */                 this.jj_la1[47] = this.jj_gen;
/* 2411 */                 jj_consume_token(-1);
/* 2412 */                 throw new ParseException();
/*      */             } 
/* 2414 */             equalsOp = this.token;
/* 2415 */             exp = Expression();
/*      */             break;
/*      */           
/*      */           case 113:
/*      */           case 114:
/* 2420 */             switch ((this.jj_ntk == -1) ? jj_ntk_f() : this.jj_ntk) {
/*      */               case 113:
/* 2422 */                 jj_consume_token(113);
/*      */                 break;
/*      */               
/*      */               case 114:
/* 2426 */                 jj_consume_token(114);
/*      */                 break;
/*      */               
/*      */               default:
/* 2430 */                 this.jj_la1[48] = this.jj_gen;
/* 2431 */                 jj_consume_token(-1);
/* 2432 */                 throw new ParseException();
/*      */             } 
/* 2434 */             equalsOp = this.token;
/* 2435 */             exp = null;
/*      */             break;
/*      */           
/*      */           default:
/* 2439 */             this.jj_la1[49] = this.jj_gen;
/* 2440 */             jj_consume_token(-1);
/* 2441 */             throw new ParseException();
/*      */         } 
/* 2443 */         ass = new Assignment(varName, equalsOp.kind, exp, scope);
/* 2444 */         if (exp != null) {
/* 2445 */           ass.setLocation(this.template, nameExp, exp);
/*      */         } else {
/* 2447 */           ass.setLocation(this.template, nameExp, equalsOp);
/*      */         } 
/* 2449 */         assignments.add(ass);
/*      */ 
/*      */         
/* 2452 */         while (jj_2_11(2147483647)) {
/*      */ 
/*      */ 
/*      */ 
/*      */           
/* 2457 */           switch ((this.jj_ntk == -1) ? jj_ntk_f() : this.jj_ntk) {
/*      */             case 130:
/* 2459 */               jj_consume_token(130);
/*      */               break;
/*      */             
/*      */             default:
/* 2463 */               this.jj_la1[50] = this.jj_gen;
/*      */               break;
/*      */           } 
/* 2466 */           nameExp = IdentifierOrStringLiteral();
/*      */ 
/*      */           
/* 2469 */           varName = (nameExp instanceof StringLiteral) ? ((StringLiteral)nameExp).getAsString() : ((Identifier)nameExp).getName();
/* 2470 */           switch ((this.jj_ntk == -1) ? jj_ntk_f() : this.jj_ntk) {
/*      */             case 105:
/*      */             case 108:
/*      */             case 109:
/*      */             case 110:
/*      */             case 111:
/*      */             case 112:
/* 2477 */               switch ((this.jj_ntk == -1) ? jj_ntk_f() : this.jj_ntk) {
/*      */                 case 105:
/* 2479 */                   jj_consume_token(105);
/*      */                   break;
/*      */                 
/*      */                 case 108:
/* 2483 */                   jj_consume_token(108);
/*      */                   break;
/*      */                 
/*      */                 case 109:
/* 2487 */                   jj_consume_token(109);
/*      */                   break;
/*      */                 
/*      */                 case 110:
/* 2491 */                   jj_consume_token(110);
/*      */                   break;
/*      */                 
/*      */                 case 111:
/* 2495 */                   jj_consume_token(111);
/*      */                   break;
/*      */                 
/*      */                 case 112:
/* 2499 */                   jj_consume_token(112);
/*      */                   break;
/*      */                 
/*      */                 default:
/* 2503 */                   this.jj_la1[51] = this.jj_gen;
/* 2504 */                   jj_consume_token(-1);
/* 2505 */                   throw new ParseException();
/*      */               } 
/* 2507 */               equalsOp = this.token;
/* 2508 */               exp = Expression();
/*      */               break;
/*      */             
/*      */             case 113:
/*      */             case 114:
/* 2513 */               switch ((this.jj_ntk == -1) ? jj_ntk_f() : this.jj_ntk) {
/*      */                 case 113:
/* 2515 */                   jj_consume_token(113);
/*      */                   break;
/*      */                 
/*      */                 case 114:
/* 2519 */                   jj_consume_token(114);
/*      */                   break;
/*      */                 
/*      */                 default:
/* 2523 */                   this.jj_la1[52] = this.jj_gen;
/* 2524 */                   jj_consume_token(-1);
/* 2525 */                   throw new ParseException();
/*      */               } 
/* 2527 */               equalsOp = this.token;
/* 2528 */               exp = null;
/*      */               break;
/*      */             
/*      */             default:
/* 2532 */               this.jj_la1[53] = this.jj_gen;
/* 2533 */               jj_consume_token(-1);
/* 2534 */               throw new ParseException();
/*      */           } 
/* 2536 */           ass = new Assignment(varName, equalsOp.kind, exp, scope);
/* 2537 */           if (exp != null) {
/* 2538 */             ass.setLocation(this.template, nameExp, exp);
/*      */           } else {
/* 2540 */             ass.setLocation(this.template, nameExp, equalsOp);
/*      */           } 
/* 2542 */           assignments.add(ass);
/*      */         } 
/* 2544 */         switch ((this.jj_ntk == -1) ? jj_ntk_f() : this.jj_ntk) {
/*      */           case 139:
/* 2546 */             id = jj_consume_token(139);
/* 2547 */             nsExp = Expression();
/* 2548 */             if (scope != 1) {
/* 2549 */               throw new ParseException("Cannot assign to namespace here.", this.template, id);
/*      */             }
/*      */             break;
/*      */           
/*      */           default:
/* 2554 */             this.jj_la1[54] = this.jj_gen;
/*      */             break;
/*      */         } 
/* 2557 */         end = LooseDirectiveEnd();
/* 2558 */         if (assignments.size() == 1) {
/* 2559 */           Assignment a = assignments.get(0);
/* 2560 */           a.setNamespaceExp(nsExp);
/* 2561 */           a.setLocation(this.template, start, end);
/* 2562 */           if ("" != null) return a; 
/*      */         } else {
/* 2564 */           AssignmentInstruction ai = new AssignmentInstruction(scope);
/* 2565 */           for (int i = 0; i < assignments.size(); i++) {
/* 2566 */             ai.addAssignment(assignments.get(i));
/*      */           }
/* 2568 */           ai.setNamespaceExp(nsExp);
/* 2569 */           ai.setLocation(this.template, start, end);
/* 2570 */           if ("" != null) return ai;
/*      */         
/*      */         } 
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
/* 2629 */         throw new Error("Missing return statement in function");case 139: case 148: switch ((this.jj_ntk == -1) ? jj_ntk_f() : this.jj_ntk) { case 139: id = jj_consume_token(139); nsExp = Expression(); if (scope != 1) throw new ParseException("Cannot assign to namespace here.", this.template, id);  break;default: this.jj_la1[55] = this.jj_gen; break; }  jj_consume_token(148); children = MixedContentElements(); switch ((this.jj_ntk == -1) ? jj_ntk_f() : this.jj_ntk) { case 43: end = jj_consume_token(43); if (scope != 2) throw new ParseException("Mismatched assignment tags.", this.template, end);  break;case 45: end = jj_consume_token(45); if (scope != 1) throw new ParseException("Mismatched assignment tags.", this.template, end);  break;case 44: end = jj_consume_token(44); if (scope != 3) throw new ParseException("Mismatched assignment tags", this.template, end);  break;default: this.jj_la1[56] = this.jj_gen; jj_consume_token(-1); throw new ParseException(); }  ba = new BlockAssignment(children, varName, scope, nsExp, getMarkupOutputFormat()); ba.setLocation(this.template, start, end); if ("" != null) return ba;  throw new Error("Missing return statement in function");
/*      */     } 
/*      */     this.jj_la1[57] = this.jj_gen;
/*      */     jj_consume_token(-1);
/*      */     throw new ParseException(); } public final Include Include() throws ParseException {
/* 2634 */     Expression parseExp = null, encodingExp = null, ignoreMissingExp = null;
/* 2635 */     Token start = jj_consume_token(19);
/* 2636 */     Expression nameExp = Expression();
/* 2637 */     switch ((this.jj_ntk == -1) ? jj_ntk_f() : this.jj_ntk) {
/*      */       case 131:
/* 2639 */         jj_consume_token(131);
/*      */         break;
/*      */       
/*      */       default:
/* 2643 */         this.jj_la1[58] = this.jj_gen;
/*      */         break;
/*      */     } 
/*      */     
/*      */     while (true) {
/* 2648 */       switch ((this.jj_ntk == -1) ? jj_ntk_f() : this.jj_ntk) {
/*      */         case 142:
/*      */           break;
/*      */ 
/*      */         
/*      */         default:
/* 2654 */           this.jj_la1[59] = this.jj_gen;
/*      */           break;
/*      */       } 
/* 2657 */       Token att = jj_consume_token(142);
/* 2658 */       jj_consume_token(105);
/* 2659 */       Expression exp = Expression();
/* 2660 */       String attString = att.image;
/* 2661 */       if (attString.equalsIgnoreCase("parse")) {
/* 2662 */         parseExp = exp; continue;
/* 2663 */       }  if (attString.equalsIgnoreCase("encoding")) {
/* 2664 */         encodingExp = exp; continue;
/* 2665 */       }  if (attString.equalsIgnoreCase("ignore_missing") || attString.equals("ignoreMissing")) {
/* 2666 */         this.token_source.checkNamingConvention(att);
/* 2667 */         ignoreMissingExp = exp; continue;
/*      */       } 
/* 2669 */       String correctedName = attString.equals("ignoreMissing") ? "ignore_missing" : null;
/* 2670 */       throw new ParseException("Unsupported named #include parameter: \"" + attString + "\". Supported parameters are: \"parse\", \"encoding\", \"ignore_missing\"." + ((correctedName == null) ? "" : " Supporting camelCase parameter names is planned for FreeMarker 2.4.0; check if an update is available, and if it indeed supports camel case."), this.template, att);
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 2681 */     Token end = LooseDirectiveEnd();
/* 2682 */     Include result = new Include(this.template, nameExp, encodingExp, parseExp, ignoreMissingExp);
/* 2683 */     result.setLocation(this.template, start, end);
/* 2684 */     if ("" != null) return result; 
/* 2685 */     throw new Error("Missing return statement in function");
/*      */   }
/*      */ 
/*      */   
/*      */   public final LibraryLoad Import() throws ParseException {
/* 2690 */     Token start = jj_consume_token(20);
/* 2691 */     Expression nameExp = Expression();
/* 2692 */     jj_consume_token(140);
/* 2693 */     Token ns = jj_consume_token(142);
/* 2694 */     Token end = LooseDirectiveEnd();
/* 2695 */     LibraryLoad result = new LibraryLoad(this.template, nameExp, ns.image);
/* 2696 */     result.setLocation(this.template, start, end);
/* 2697 */     this.template.addImport(result);
/* 2698 */     if ("" != null) return result; 
/* 2699 */     throw new Error("Missing return statement in function");
/*      */   }
/*      */   
/*      */   public final Macro Macro() throws ParseException {
/*      */     Token start, end;
/*      */     int lastBreakableDirectiveNesting, lastContinuableDirectiveNesting;
/* 2705 */     Map<String, Expression> paramNamesWithDefault = new LinkedHashMap<>();
/* 2706 */     Expression defValue = null;
/* 2707 */     String catchAllParamName = null;
/* 2708 */     boolean isCatchAll = false;
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 2713 */     boolean isFunction = false;
/* 2714 */     boolean hasDefaults = false;
/* 2715 */     switch ((this.jj_ntk == -1) ? jj_ntk_f() : this.jj_ntk) {
/*      */       case 22:
/* 2717 */         start = jj_consume_token(22);
/*      */         break;
/*      */       
/*      */       case 21:
/* 2721 */         start = jj_consume_token(21);
/* 2722 */         isFunction = true;
/*      */         break;
/*      */       
/*      */       default:
/* 2726 */         this.jj_la1[60] = this.jj_gen;
/* 2727 */         jj_consume_token(-1);
/* 2728 */         throw new ParseException();
/*      */     } 
/* 2730 */     if (this.inMacro || this.inFunction) {
/* 2731 */       throw new ParseException("Macro or function definitions can't be nested into each other.", this.template, start);
/*      */     }
/* 2733 */     if (isFunction) { this.inFunction = true; } else { this.inMacro = true; }
/* 2734 */      this.requireArgsSpecialVariable = false;
/* 2735 */     Expression nameExp = IdentifierOrStringLiteral();
/*      */ 
/*      */     
/* 2738 */     String name = (nameExp instanceof StringLiteral) ? ((StringLiteral)nameExp).getAsString() : ((Identifier)nameExp).getName();
/* 2739 */     switch ((this.jj_ntk == -1) ? jj_ntk_f() : this.jj_ntk) {
/*      */       case 135:
/* 2741 */         jj_consume_token(135);
/*      */         break;
/*      */       
/*      */       default:
/* 2745 */         this.jj_la1[61] = this.jj_gen;
/*      */         break;
/*      */     } 
/*      */     
/*      */     while (true) {
/* 2750 */       switch ((this.jj_ntk == -1) ? jj_ntk_f() : this.jj_ntk) {
/*      */         case 142:
/*      */           break;
/*      */ 
/*      */         
/*      */         default:
/* 2756 */           this.jj_la1[62] = this.jj_gen;
/*      */           break;
/*      */       } 
/* 2759 */       Token arg = jj_consume_token(142);
/* 2760 */       defValue = null;
/* 2761 */       switch ((this.jj_ntk == -1) ? jj_ntk_f() : this.jj_ntk) {
/*      */         case 124:
/* 2763 */           jj_consume_token(124);
/* 2764 */           isCatchAll = true;
/*      */           break;
/*      */         
/*      */         default:
/* 2768 */           this.jj_la1[63] = this.jj_gen;
/*      */           break;
/*      */       } 
/* 2771 */       switch ((this.jj_ntk == -1) ? jj_ntk_f() : this.jj_ntk) {
/*      */         case 105:
/* 2773 */           jj_consume_token(105);
/* 2774 */           defValue = Expression();
/* 2775 */           hasDefaults = true;
/*      */           break;
/*      */         
/*      */         default:
/* 2779 */           this.jj_la1[64] = this.jj_gen;
/*      */           break;
/*      */       } 
/* 2782 */       switch ((this.jj_ntk == -1) ? jj_ntk_f() : this.jj_ntk) {
/*      */         case 130:
/* 2784 */           jj_consume_token(130);
/*      */           break;
/*      */         
/*      */         default:
/* 2788 */           this.jj_la1[65] = this.jj_gen;
/*      */           break;
/*      */       } 
/* 2791 */       if (catchAllParamName != null) {
/* 2792 */         throw new ParseException("There may only be one \"catch-all\" parameter in a macro declaration, and it must be the last parameter.", this.template, arg);
/*      */       }
/*      */ 
/*      */       
/* 2796 */       if (isCatchAll) {
/* 2797 */         if (defValue != null) {
/* 2798 */           throw new ParseException("\"Catch-all\" macro parameter may not have a default value.", this.template, arg);
/*      */         }
/*      */ 
/*      */         
/* 2802 */         catchAllParamName = arg.image; continue;
/*      */       } 
/* 2804 */       if (hasDefaults && defValue == null) {
/* 2805 */         throw new ParseException("In a macro declaration, parameters without a default value must all occur before the parameters with default values.", this.template, arg);
/*      */       }
/*      */ 
/*      */ 
/*      */       
/* 2810 */       paramNamesWithDefault.put(arg.image, defValue);
/*      */     } 
/*      */     
/* 2813 */     switch ((this.jj_ntk == -1) ? jj_ntk_f() : this.jj_ntk) {
/*      */       case 136:
/* 2815 */         jj_consume_token(136);
/*      */         break;
/*      */       
/*      */       default:
/* 2819 */         this.jj_la1[66] = this.jj_gen;
/*      */         break;
/*      */     } 
/* 2822 */     jj_consume_token(148);
/*      */     
/* 2824 */     List<ParserIteratorBlockContext> lastIteratorBlockContexts = this.iteratorBlockContexts;
/* 2825 */     this.iteratorBlockContexts = null;
/* 2826 */     if (this.incompatibleImprovements >= _TemplateAPI.VERSION_INT_2_3_23) {
/* 2827 */       lastBreakableDirectiveNesting = this.breakableDirectiveNesting;
/* 2828 */       lastContinuableDirectiveNesting = this.continuableDirectiveNesting;
/* 2829 */       this.breakableDirectiveNesting = 0;
/* 2830 */       this.continuableDirectiveNesting = 0;
/*      */     } else {
/* 2832 */       lastBreakableDirectiveNesting = 0;
/* 2833 */       lastContinuableDirectiveNesting = 0;
/*      */     } 
/* 2835 */     TemplateElements children = MixedContentElements();
/* 2836 */     switch ((this.jj_ntk == -1) ? jj_ntk_f() : this.jj_ntk) {
/*      */       case 47:
/* 2838 */         end = jj_consume_token(47);
/* 2839 */         if (isFunction) throw new ParseException("Expected function end tag here.", this.template, end);
/*      */         
/*      */         break;
/*      */       case 46:
/* 2843 */         end = jj_consume_token(46);
/* 2844 */         if (!isFunction) throw new ParseException("Expected macro end tag here.", this.template, end);
/*      */         
/*      */         break;
/*      */       default:
/* 2848 */         this.jj_la1[67] = this.jj_gen;
/* 2849 */         jj_consume_token(-1);
/* 2850 */         throw new ParseException();
/*      */     } 
/* 2852 */     this.iteratorBlockContexts = lastIteratorBlockContexts;
/* 2853 */     if (this.incompatibleImprovements >= _TemplateAPI.VERSION_INT_2_3_23) {
/* 2854 */       this.breakableDirectiveNesting = lastBreakableDirectiveNesting;
/* 2855 */       this.continuableDirectiveNesting = lastContinuableDirectiveNesting;
/*      */     } 
/*      */     
/* 2858 */     this.inMacro = this.inFunction = false;
/* 2859 */     Macro result = new Macro(name, paramNamesWithDefault, catchAllParamName, isFunction, this.requireArgsSpecialVariable, children);
/*      */     
/* 2861 */     result.setLocation(this.template, start, end);
/* 2862 */     this.template.addMacro(result);
/* 2863 */     if ("" != null) return result; 
/* 2864 */     throw new Error("Missing return statement in function");
/*      */   }
/*      */   
/*      */   public final CompressedBlock Compress() throws ParseException
/*      */   {
/* 2869 */     Token start = jj_consume_token(32);
/* 2870 */     TemplateElements children = MixedContentElements();
/* 2871 */     Token end = jj_consume_token(51);
/* 2872 */     CompressedBlock cb = new CompressedBlock(children);
/* 2873 */     cb.setLocation(this.template, start, end);
/* 2874 */     if ("" != null) return cb; 
/* 2875 */     throw new Error("Missing return statement in function"); } public final TemplateElement UnifiedMacroTransform() throws ParseException { Token end, t; Expression startTagNameExp; TemplateElements children;
/*      */     int i;
/*      */     String endTagName;
/* 2878 */     Token start = null;
/* 2879 */     HashMap<String, ? extends Expression> namedArgs = null;
/* 2880 */     ArrayList<? extends Expression> positionalArgs = null; ArrayList<String> bodyParameters = null;
/*      */ 
/*      */ 
/*      */     
/* 2884 */     int pushedCtxCount = 0;
/* 2885 */     start = jj_consume_token(74);
/* 2886 */     Expression exp = Expression();
/*      */     
/* 2888 */     Expression cleanedExp = exp;
/* 2889 */     if (cleanedExp instanceof MethodCall) {
/* 2890 */       Expression methodCallTarget = ((MethodCall)cleanedExp).getTarget();
/* 2891 */       if (methodCallTarget instanceof BuiltInsForCallables.with_argsBI) {
/* 2892 */         cleanedExp = ((BuiltInsForCallables.with_argsBI)methodCallTarget).target;
/*      */       }
/*      */     } 
/*      */     
/* 2896 */     if (cleanedExp instanceof Identifier || (cleanedExp instanceof Dot && ((Dot)cleanedExp).onlyHasIdentifiers())) {
/* 2897 */       startTagNameExp = cleanedExp;
/*      */     } else {
/* 2899 */       startTagNameExp = null;
/*      */     } 
/* 2901 */     switch ((this.jj_ntk == -1) ? jj_ntk_f() : this.jj_ntk) {
/*      */       case 152:
/* 2903 */         jj_consume_token(152);
/*      */         break;
/*      */       
/*      */       default:
/* 2907 */         this.jj_la1[68] = this.jj_gen;
/*      */         break;
/*      */     } 
/* 2910 */     if (jj_2_12(2147483647)) {
/* 2911 */       namedArgs = NamedArgs();
/*      */     } else {
/* 2913 */       positionalArgs = PositionalArgs();
/*      */     } 
/* 2915 */     switch ((this.jj_ntk == -1) ? jj_ntk_f() : this.jj_ntk) {
/*      */       case 131:
/* 2917 */         jj_consume_token(131);
/* 2918 */         bodyParameters = new ArrayList(4);
/* 2919 */         switch ((this.jj_ntk == -1) ? jj_ntk_f() : this.jj_ntk) {
/*      */           case 142:
/*      */           case 152:
/* 2922 */             switch ((this.jj_ntk == -1) ? jj_ntk_f() : this.jj_ntk) {
/*      */               case 152:
/* 2924 */                 jj_consume_token(152);
/*      */                 break;
/*      */               
/*      */               default:
/* 2928 */                 this.jj_la1[69] = this.jj_gen;
/*      */                 break;
/*      */             } 
/* 2931 */             t = jj_consume_token(142);
/* 2932 */             bodyParameters.add(t.image);
/*      */             
/*      */             while (true) {
/* 2935 */               switch ((this.jj_ntk == -1) ? jj_ntk_f() : this.jj_ntk) {
/*      */                 case 130:
/*      */                 case 152:
/*      */                   break;
/*      */ 
/*      */                 
/*      */                 default:
/* 2942 */                   this.jj_la1[70] = this.jj_gen;
/*      */                   break;
/*      */               } 
/* 2945 */               switch ((this.jj_ntk == -1) ? jj_ntk_f() : this.jj_ntk) {
/*      */                 case 152:
/* 2947 */                   jj_consume_token(152);
/*      */                   break;
/*      */                 
/*      */                 default:
/* 2951 */                   this.jj_la1[71] = this.jj_gen;
/*      */                   break;
/*      */               } 
/* 2954 */               jj_consume_token(130);
/* 2955 */               switch ((this.jj_ntk == -1) ? jj_ntk_f() : this.jj_ntk) {
/*      */                 case 152:
/* 2957 */                   jj_consume_token(152);
/*      */                   break;
/*      */                 
/*      */                 default:
/* 2961 */                   this.jj_la1[72] = this.jj_gen;
/*      */                   break;
/*      */               } 
/* 2964 */               t = jj_consume_token(142);
/* 2965 */               bodyParameters.add(t.image);
/*      */             } 
/*      */             break;
/*      */         } 
/*      */         
/* 2970 */         this.jj_la1[73] = this.jj_gen;
/*      */         break;
/*      */ 
/*      */ 
/*      */       
/*      */       default:
/* 2976 */         this.jj_la1[74] = this.jj_gen;
/*      */         break;
/*      */     } 
/* 2979 */     switch ((this.jj_ntk == -1) ? jj_ntk_f() : this.jj_ntk) {
/*      */       case 149:
/* 2981 */         end = jj_consume_token(149);
/* 2982 */         children = TemplateElements.EMPTY;
/*      */         break;
/*      */       
/*      */       case 148:
/* 2986 */         jj_consume_token(148);
/* 2987 */         if (bodyParameters != null && this.iteratorBlockContexts != null && !this.iteratorBlockContexts.isEmpty()) {
/*      */           
/* 2989 */           int ctxsLen = this.iteratorBlockContexts.size();
/* 2990 */           int bodyParsLen = bodyParameters.size();
/* 2991 */           for (int bodyParIdx = 0; bodyParIdx < bodyParsLen; bodyParIdx++) {
/* 2992 */             String bodyParName = bodyParameters.get(bodyParIdx);
/* 2993 */             for (int ctxIdx = ctxsLen - 1; ctxIdx >= 0; ctxIdx--) {
/*      */               
/* 2995 */               ParserIteratorBlockContext ctx = this.iteratorBlockContexts.get(ctxIdx);
/* 2996 */               if (ctx.loopVarName != null && ctx.loopVarName.equals(bodyParName)) {
/*      */                 
/* 2998 */                 if (ctx.kind != 3) {
/* 2999 */                   ParserIteratorBlockContext shadowingCtx = pushIteratorBlockContext();
/* 3000 */                   shadowingCtx.loopVarName = bodyParName;
/* 3001 */                   shadowingCtx.kind = 3;
/* 3002 */                   pushedCtxCount++;
/*      */                 } 
/*      */                 break;
/*      */               } 
/*      */             } 
/*      */           } 
/*      */         } 
/* 3009 */         children = MixedContentElements();
/* 3010 */         end = jj_consume_token(75);
/* 3011 */         for (i = 0; i < pushedCtxCount; i++) {
/* 3012 */           popIteratorBlockContext();
/*      */         }
/*      */         
/* 3015 */         endTagName = end.image.substring(3, end.image.length() - 1).trim();
/* 3016 */         if (endTagName.length() > 0) {
/* 3017 */           if (startTagNameExp == null) {
/* 3018 */             throw new ParseException("Expecting </@>", this.template, end);
/*      */           }
/* 3020 */           String startTagName = startTagNameExp.getCanonicalForm();
/* 3021 */           if (!endTagName.equals(startTagName)) {
/* 3022 */             throw new ParseException("Expecting </@> or </@" + startTagName + ">", this.template, end);
/*      */           }
/*      */         } 
/*      */         break;
/*      */ 
/*      */       
/*      */       default:
/* 3029 */         this.jj_la1[75] = this.jj_gen;
/* 3030 */         jj_consume_token(-1);
/* 3031 */         throw new ParseException();
/*      */     } 
/* 3033 */     TemplateElement result = (positionalArgs != null) ? new UnifiedCall(exp, positionalArgs, children, bodyParameters) : new UnifiedCall(exp, namedArgs, children, bodyParameters);
/*      */ 
/*      */     
/* 3036 */     result.setLocation(this.template, start, end);
/* 3037 */     if ("" != null) return result; 
/* 3038 */     throw new Error("Missing return statement in function"); }
/*      */ 
/*      */   
/*      */   public final TemplateElement Call() throws ParseException {
/* 3042 */     HashMap<String, ? extends Expression> namedArgs = null;
/* 3043 */     ArrayList<? extends Expression> positionalArgs = null;
/* 3044 */     Identifier macroName = null;
/* 3045 */     Token start = jj_consume_token(27);
/* 3046 */     Token id = jj_consume_token(142);
/* 3047 */     macroName = new Identifier(id.image);
/* 3048 */     macroName.setLocation(this.template, id, id);
/* 3049 */     if (jj_2_14(2147483647)) {
/* 3050 */       namedArgs = NamedArgs();
/*      */     } else {
/* 3052 */       if (jj_2_13(2147483647)) {
/* 3053 */         jj_consume_token(135);
/*      */       }
/*      */ 
/*      */       
/* 3057 */       positionalArgs = PositionalArgs();
/* 3058 */       switch ((this.jj_ntk == -1) ? jj_ntk_f() : this.jj_ntk) {
/*      */         case 136:
/* 3060 */           jj_consume_token(136);
/*      */           break;
/*      */         
/*      */         default:
/* 3064 */           this.jj_la1[76] = this.jj_gen;
/*      */           break;
/*      */       } 
/*      */     } 
/* 3068 */     Token end = LooseDirectiveEnd();
/* 3069 */     UnifiedCall result = null;
/* 3070 */     if (positionalArgs != null) {
/* 3071 */       result = new UnifiedCall(macroName, positionalArgs, TemplateElements.EMPTY, null);
/*      */     } else {
/* 3073 */       result = new UnifiedCall(macroName, namedArgs, TemplateElements.EMPTY, null);
/*      */     } 
/* 3075 */     result.legacySyntax = true;
/* 3076 */     result.setLocation(this.template, start, end);
/* 3077 */     if ("" != null) return result; 
/* 3078 */     throw new Error("Missing return statement in function");
/*      */   }
/*      */   public final HashMap NamedArgs() throws ParseException {
/* 3081 */     HashMap<Object, Object> result = new HashMap<>();
/*      */ 
/*      */ 
/*      */     
/*      */     while (true) {
/* 3086 */       Token t = jj_consume_token(142);
/* 3087 */       jj_consume_token(105);
/* 3088 */       this.token_source.SwitchTo(4);
/* 3089 */       this.token_source.inInvocation = true;
/* 3090 */       Expression exp = Expression();
/* 3091 */       result.put(t.image, exp);
/* 3092 */       switch ((this.jj_ntk == -1) ? jj_ntk_f() : this.jj_ntk) {
/*      */         case 142:
/*      */           continue;
/*      */       } 
/*      */       break;
/*      */     } 
/* 3098 */     this.jj_la1[77] = this.jj_gen;
/*      */ 
/*      */ 
/*      */     
/* 3102 */     this.token_source.inInvocation = false;
/* 3103 */     if ("" != null) return result; 
/* 3104 */     throw new Error("Missing return statement in function");
/*      */   } public final ArrayList PositionalArgs() throws ParseException {
/*      */     Expression arg;
/* 3107 */     ArrayList<Expression> result = new ArrayList();
/*      */     
/* 3109 */     switch ((this.jj_ntk == -1) ? jj_ntk_f() : this.jj_ntk) {
/*      */       case 93:
/*      */       case 94:
/*      */       case 95:
/*      */       case 96:
/*      */       case 97:
/*      */       case 98:
/*      */       case 99:
/*      */       case 120:
/*      */       case 121:
/*      */       case 129:
/*      */       case 133:
/*      */       case 135:
/*      */       case 137:
/*      */       case 142:
/* 3124 */         arg = Expression();
/* 3125 */         result.add(arg);
/*      */         
/*      */         while (true) {
/* 3128 */           switch ((this.jj_ntk == -1) ? jj_ntk_f() : this.jj_ntk) {
/*      */             case 93:
/*      */             case 94:
/*      */             case 95:
/*      */             case 96:
/*      */             case 97:
/*      */             case 98:
/*      */             case 99:
/*      */             case 120:
/*      */             case 121:
/*      */             case 129:
/*      */             case 130:
/*      */             case 133:
/*      */             case 135:
/*      */             case 137:
/*      */             case 142:
/*      */               break;
/*      */ 
/*      */             
/*      */             default:
/* 3148 */               this.jj_la1[78] = this.jj_gen;
/*      */               break;
/*      */           } 
/* 3151 */           switch ((this.jj_ntk == -1) ? jj_ntk_f() : this.jj_ntk) {
/*      */             case 130:
/* 3153 */               jj_consume_token(130);
/*      */               break;
/*      */             
/*      */             default:
/* 3157 */               this.jj_la1[79] = this.jj_gen;
/*      */               break;
/*      */           } 
/* 3160 */           arg = Expression();
/* 3161 */           result.add(arg);
/*      */         } 
/*      */         break;
/*      */       
/*      */       default:
/* 3166 */         this.jj_la1[80] = this.jj_gen;
/*      */         break;
/*      */     } 
/* 3169 */     if ("" != null) return result; 
/* 3170 */     throw new Error("Missing return statement in function");
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public final ArrayList PositionalMaybeLambdaArgs() throws ParseException {
/*      */     Expression arg;
/* 3177 */     ArrayList<Expression> result = new ArrayList();
/*      */     
/* 3179 */     switch ((this.jj_ntk == -1) ? jj_ntk_f() : this.jj_ntk) {
/*      */       case 93:
/*      */       case 94:
/*      */       case 95:
/*      */       case 96:
/*      */       case 97:
/*      */       case 98:
/*      */       case 99:
/*      */       case 120:
/*      */       case 121:
/*      */       case 129:
/*      */       case 133:
/*      */       case 135:
/*      */       case 137:
/*      */       case 142:
/* 3194 */         arg = LocalLambdaExpression();
/* 3195 */         result.add(arg);
/*      */         
/*      */         while (true) {
/* 3198 */           switch ((this.jj_ntk == -1) ? jj_ntk_f() : this.jj_ntk) {
/*      */             case 93:
/*      */             case 94:
/*      */             case 95:
/*      */             case 96:
/*      */             case 97:
/*      */             case 98:
/*      */             case 99:
/*      */             case 120:
/*      */             case 121:
/*      */             case 129:
/*      */             case 130:
/*      */             case 133:
/*      */             case 135:
/*      */             case 137:
/*      */             case 142:
/*      */               break;
/*      */ 
/*      */             
/*      */             default:
/* 3218 */               this.jj_la1[81] = this.jj_gen;
/*      */               break;
/*      */           } 
/* 3221 */           switch ((this.jj_ntk == -1) ? jj_ntk_f() : this.jj_ntk) {
/*      */             case 130:
/* 3223 */               jj_consume_token(130);
/*      */               break;
/*      */             
/*      */             default:
/* 3227 */               this.jj_la1[82] = this.jj_gen;
/*      */               break;
/*      */           } 
/* 3230 */           arg = LocalLambdaExpression();
/* 3231 */           result.add(arg);
/*      */         } 
/*      */         break;
/*      */       
/*      */       default:
/* 3236 */         this.jj_la1[83] = this.jj_gen;
/*      */         break;
/*      */     } 
/* 3239 */     if ("" != null) return result; 
/* 3240 */     throw new Error("Missing return statement in function");
/*      */   }
/*      */   public final Comment Comment() throws ParseException {
/*      */     Token start;
/* 3244 */     StringBuilder buf = new StringBuilder();
/* 3245 */     switch ((this.jj_ntk == -1) ? jj_ntk_f() : this.jj_ntk) {
/*      */       case 33:
/* 3247 */         start = jj_consume_token(33);
/*      */         break;
/*      */       
/*      */       case 34:
/* 3251 */         start = jj_consume_token(34);
/*      */         break;
/*      */       
/*      */       default:
/* 3255 */         this.jj_la1[84] = this.jj_gen;
/* 3256 */         jj_consume_token(-1);
/* 3257 */         throw new ParseException();
/*      */     } 
/* 3259 */     Token end = UnparsedContent(start, buf);
/* 3260 */     Comment result = new Comment(buf.toString());
/* 3261 */     result.setLocation(this.template, start, end);
/* 3262 */     if ("" != null) return result; 
/* 3263 */     throw new Error("Missing return statement in function");
/*      */   }
/*      */   
/*      */   public final TextBlock NoParse() throws ParseException {
/* 3267 */     StringBuilder buf = new StringBuilder();
/* 3268 */     Token start = jj_consume_token(35);
/* 3269 */     Token end = UnparsedContent(start, buf);
/* 3270 */     TextBlock result = new TextBlock(buf.toString(), true);
/* 3271 */     result.setLocation(this.template, start, end);
/* 3272 */     if ("" != null) return result; 
/* 3273 */     throw new Error("Missing return statement in function");
/*      */   }
/*      */   
/*      */   public final TransformBlock Transform() throws ParseException {
/*      */     Token end;
/* 3278 */     TemplateElements children = null;
/* 3279 */     HashMap<Object, Object> args = null;
/* 3280 */     Token start = jj_consume_token(23);
/* 3281 */     Expression exp = Expression();
/* 3282 */     switch ((this.jj_ntk == -1) ? jj_ntk_f() : this.jj_ntk) {
/*      */       case 131:
/* 3284 */         jj_consume_token(131);
/*      */         break;
/*      */       
/*      */       default:
/* 3288 */         this.jj_la1[85] = this.jj_gen;
/*      */         break;
/*      */     } 
/*      */     
/*      */     while (true) {
/* 3293 */       switch ((this.jj_ntk == -1) ? jj_ntk_f() : this.jj_ntk) {
/*      */         case 142:
/*      */           break;
/*      */ 
/*      */         
/*      */         default:
/* 3299 */           this.jj_la1[86] = this.jj_gen;
/*      */           break;
/*      */       } 
/* 3302 */       Token argName = jj_consume_token(142);
/* 3303 */       jj_consume_token(105);
/* 3304 */       Expression argExp = Expression();
/* 3305 */       if (args == null) args = new HashMap<>(); 
/* 3306 */       args.put(argName.image, argExp);
/*      */     } 
/* 3308 */     switch ((this.jj_ntk == -1) ? jj_ntk_f() : this.jj_ntk) {
/*      */       case 149:
/* 3310 */         end = jj_consume_token(149);
/*      */         break;
/*      */       
/*      */       case 148:
/* 3314 */         jj_consume_token(148);
/* 3315 */         children = MixedContentElements();
/* 3316 */         end = jj_consume_token(52);
/*      */         break;
/*      */       
/*      */       default:
/* 3320 */         this.jj_la1[87] = this.jj_gen;
/* 3321 */         jj_consume_token(-1);
/* 3322 */         throw new ParseException();
/*      */     } 
/* 3324 */     TransformBlock result = new TransformBlock(exp, args, children);
/* 3325 */     result.setLocation(this.template, start, end);
/* 3326 */     if ("" != null) return result; 
/* 3327 */     throw new Error("Missing return statement in function");
/*      */   }
/*      */   
/*      */   public final SwitchBlock Switch() throws ParseException {
/* 3331 */     MixedContent ignoredSectionBeforeFirstCase = null;
/*      */ 
/*      */ 
/*      */     
/* 3335 */     boolean defaultFound = false;
/* 3336 */     Token start = jj_consume_token(14);
/* 3337 */     Expression switchExp = Expression();
/* 3338 */     jj_consume_token(148);
/* 3339 */     switch ((this.jj_ntk == -1) ? jj_ntk_f() : this.jj_ntk) {
/*      */       case 33:
/*      */       case 34:
/*      */       case 79:
/* 3343 */         ignoredSectionBeforeFirstCase = WhitespaceAndComments();
/*      */         break;
/*      */       
/*      */       default:
/* 3347 */         this.jj_la1[88] = this.jj_gen;
/*      */         break;
/*      */     } 
/* 3350 */     this.breakableDirectiveNesting++;
/* 3351 */     SwitchBlock switchBlock = new SwitchBlock(switchExp, ignoredSectionBeforeFirstCase);
/* 3352 */     switch ((this.jj_ntk == -1) ? jj_ntk_f() : this.jj_ntk) {
/*      */       
/*      */       case 15:
/*      */       case 64:
/*      */         while (true) {
/* 3357 */           Case caseIns = Case();
/* 3358 */           if (caseIns.condition == null) {
/* 3359 */             if (defaultFound) {
/* 3360 */               throw new ParseException("You can only have one default case in a switch statement", this.template, start);
/*      */             }
/*      */             
/* 3363 */             defaultFound = true;
/*      */           } 
/* 3365 */           switchBlock.addCase(caseIns);
/* 3366 */           switch ((this.jj_ntk == -1) ? jj_ntk_f() : this.jj_ntk) {
/*      */             case 15:
/*      */             case 64:
/*      */               continue;
/*      */           } 
/*      */           break;
/*      */         } 
/* 3373 */         this.jj_la1[89] = this.jj_gen;
/*      */ 
/*      */ 
/*      */         
/* 3377 */         switch ((this.jj_ntk == -1) ? jj_ntk_f() : this.jj_ntk) {
/*      */           case 79:
/* 3379 */             jj_consume_token(79);
/*      */             break;
/*      */         } 
/*      */         
/* 3383 */         this.jj_la1[90] = this.jj_gen;
/*      */         break;
/*      */ 
/*      */ 
/*      */       
/*      */       default:
/* 3389 */         this.jj_la1[91] = this.jj_gen;
/*      */         break;
/*      */     } 
/* 3392 */     Token end = jj_consume_token(53);
/* 3393 */     this.breakableDirectiveNesting--;
/* 3394 */     switchBlock.setLocation(this.template, start, end);
/* 3395 */     if ("" != null) return switchBlock; 
/* 3396 */     throw new Error("Missing return statement in function");
/*      */   }
/*      */   
/*      */   public final Case Case() throws ParseException {
/*      */     Expression exp;
/*      */     Token start;
/* 3402 */     switch ((this.jj_ntk == -1) ? jj_ntk_f() : this.jj_ntk) {
/*      */       case 15:
/* 3404 */         start = jj_consume_token(15);
/* 3405 */         exp = Expression();
/* 3406 */         jj_consume_token(148);
/*      */         break;
/*      */       
/*      */       case 64:
/* 3410 */         start = jj_consume_token(64);
/* 3411 */         exp = null;
/*      */         break;
/*      */       
/*      */       default:
/* 3415 */         this.jj_la1[92] = this.jj_gen;
/* 3416 */         jj_consume_token(-1);
/* 3417 */         throw new ParseException();
/*      */     } 
/* 3419 */     TemplateElements children = MixedContentElements();
/* 3420 */     Case result = new Case(exp, children);
/* 3421 */     result.setLocation(this.template, start, start, children);
/* 3422 */     if ("" != null) return result; 
/* 3423 */     throw new Error("Missing return statement in function");
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public final EscapeBlock Escape() throws ParseException {
/* 3429 */     Token start = jj_consume_token(70);
/* 3430 */     if (this.outputFormat instanceof MarkupOutputFormat && this.autoEscaping) {
/* 3431 */       throw new ParseException("Using the \"escape\" directive (legacy escaping) is not allowed when auto-escaping is on with a markup output format (" + this.outputFormat
/*      */           
/* 3433 */           .getName() + "), to avoid confusion and double-escaping mistakes.", this.template, start);
/*      */     }
/*      */ 
/*      */     
/* 3437 */     Token variable = jj_consume_token(142);
/* 3438 */     jj_consume_token(140);
/* 3439 */     Expression escapeExpr = Expression();
/* 3440 */     jj_consume_token(148);
/* 3441 */     EscapeBlock result = new EscapeBlock(variable.image, escapeExpr, escapedExpression(escapeExpr));
/* 3442 */     this.escapes.addFirst(result);
/* 3443 */     TemplateElements children = MixedContentElements();
/* 3444 */     result.setContent(children);
/* 3445 */     this.escapes.removeFirst();
/* 3446 */     Token end = jj_consume_token(71);
/* 3447 */     result.setLocation(this.template, start, end);
/* 3448 */     if ("" != null) return result; 
/* 3449 */     throw new Error("Missing return statement in function");
/*      */   }
/*      */ 
/*      */   
/*      */   public final NoEscapeBlock NoEscape() throws ParseException {
/* 3454 */     Token start = jj_consume_token(72);
/* 3455 */     if (this.escapes.isEmpty()) {
/* 3456 */       throw new ParseException("#noescape with no matching #escape encountered.", this.template, start);
/*      */     }
/* 3458 */     Object escape = this.escapes.removeFirst();
/* 3459 */     TemplateElements children = MixedContentElements();
/* 3460 */     Token end = jj_consume_token(73);
/* 3461 */     this.escapes.addFirst(escape);
/* 3462 */     NoEscapeBlock result = new NoEscapeBlock(children);
/* 3463 */     result.setLocation(this.template, start, end);
/* 3464 */     if ("" != null) return result; 
/* 3465 */     throw new Error("Missing return statement in function");
/*      */   }
/*      */ 
/*      */   
/*      */   public final OutputFormatBlock OutputFormat() throws ParseException {
/*      */     TemplateModel paramTM;
/*      */     String paramStr;
/* 3472 */     Token start = jj_consume_token(29);
/* 3473 */     Expression paramExp = Expression();
/* 3474 */     jj_consume_token(148);
/* 3475 */     if (!paramExp.isLiteral()) {
/* 3476 */       throw new ParseException("Parameter expression must be parse-time evaluable (constant): " + paramExp
/*      */           
/* 3478 */           .getCanonicalForm(), paramExp);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     try {
/* 3484 */       paramTM = paramExp.eval((Environment)null);
/* 3485 */     } catch (Exception e) {
/* 3486 */       throw new ParseException("Could not evaluate expression (on parse-time): " + paramExp
/* 3487 */           .getCanonicalForm() + "\nUnderlying cause: " + e, paramExp, e);
/*      */     } 
/*      */ 
/*      */ 
/*      */     
/* 3492 */     if (paramTM instanceof TemplateScalarModel) {
/*      */       try {
/* 3494 */         paramStr = ((TemplateScalarModel)paramTM).getAsString();
/* 3495 */       } catch (TemplateModelException e) {
/* 3496 */         throw new ParseException("Could not evaluate expression (on parse-time): " + paramExp
/* 3497 */             .getCanonicalForm() + "\nUnderlying cause: " + e, paramExp, e);
/*      */       }
/*      */     
/*      */     } else {
/*      */       
/* 3502 */       throw new ParseException("Parameter must be a string, but was: " + 
/* 3503 */           ClassUtil.getFTLTypeDescription(paramTM), paramExp);
/*      */     } 
/*      */ 
/*      */     
/* 3507 */     OutputFormat lastOutputFormat = this.outputFormat;
/*      */     try {
/* 3509 */       if (paramStr.startsWith("{")) {
/* 3510 */         if (!paramStr.endsWith("}")) {
/* 3511 */           throw new ParseException("Output format name that starts with '{' must end with '}': " + paramStr, this.template, start);
/*      */         }
/*      */         
/* 3514 */         OutputFormat innerOutputFormat = this.template.getConfiguration().getOutputFormat(paramStr
/* 3515 */             .substring(1, paramStr.length() - 1));
/* 3516 */         if (!(innerOutputFormat instanceof MarkupOutputFormat)) {
/* 3517 */           throw new ParseException("The output format inside the {...} must be a markup format, but was: " + innerOutputFormat, this.template, start);
/*      */         }
/*      */ 
/*      */ 
/*      */         
/* 3522 */         if (!(this.outputFormat instanceof MarkupOutputFormat)) {
/* 3523 */           throw new ParseException("The current output format must be a markup format when using {...}, but was: " + this.outputFormat, this.template, start);
/*      */         }
/*      */ 
/*      */ 
/*      */         
/* 3528 */         this.outputFormat = new CombinedMarkupOutputFormat((MarkupOutputFormat)this.outputFormat, (MarkupOutputFormat)innerOutputFormat);
/*      */       } else {
/*      */         
/* 3531 */         this.outputFormat = this.template.getConfiguration().getOutputFormat(paramStr);
/*      */       } 
/* 3533 */       recalculateAutoEscapingField();
/* 3534 */     } catch (IllegalArgumentException e) {
/* 3535 */       throw new ParseException("Invalid format name: " + e.getMessage(), this.template, start, e.getCause());
/* 3536 */     } catch (UnregisteredOutputFormatException e) {
/* 3537 */       throw new ParseException(e.getMessage(), this.template, start, e.getCause());
/*      */     } 
/* 3539 */     TemplateElements children = MixedContentElements();
/* 3540 */     Token end = jj_consume_token(48);
/* 3541 */     OutputFormatBlock result = new OutputFormatBlock(children, paramExp);
/* 3542 */     result.setLocation(this.template, start, end);
/*      */     
/* 3544 */     this.outputFormat = lastOutputFormat;
/* 3545 */     recalculateAutoEscapingField();
/* 3546 */     if ("" != null) return result; 
/* 3547 */     throw new Error("Missing return statement in function");
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public final AutoEscBlock AutoEsc() throws ParseException {
/* 3553 */     Token start = jj_consume_token(30);
/* 3554 */     checkCurrentOutputFormatCanEscape(start);
/* 3555 */     int lastAutoEscapingPolicy = this.autoEscapingPolicy;
/* 3556 */     this.autoEscapingPolicy = 22;
/* 3557 */     recalculateAutoEscapingField();
/* 3558 */     TemplateElements children = MixedContentElements();
/* 3559 */     Token end = jj_consume_token(49);
/* 3560 */     AutoEscBlock result = new AutoEscBlock(children);
/* 3561 */     result.setLocation(this.template, start, end);
/*      */     
/* 3563 */     this.autoEscapingPolicy = lastAutoEscapingPolicy;
/* 3564 */     recalculateAutoEscapingField();
/* 3565 */     if ("" != null) return result; 
/* 3566 */     throw new Error("Missing return statement in function");
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public final NoAutoEscBlock NoAutoEsc() throws ParseException {
/* 3572 */     Token start = jj_consume_token(31);
/* 3573 */     int lastAutoEscapingPolicy = this.autoEscapingPolicy;
/* 3574 */     this.autoEscapingPolicy = 20;
/* 3575 */     recalculateAutoEscapingField();
/* 3576 */     TemplateElements children = MixedContentElements();
/* 3577 */     Token end = jj_consume_token(50);
/* 3578 */     NoAutoEscBlock result = new NoAutoEscBlock(children);
/* 3579 */     result.setLocation(this.template, start, end);
/*      */     
/* 3581 */     this.autoEscapingPolicy = lastAutoEscapingPolicy;
/* 3582 */     recalculateAutoEscapingField();
/* 3583 */     if ("" != null) return result; 
/* 3584 */     throw new Error("Missing return statement in function");
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public final Token LooseDirectiveEnd() throws ParseException {
/*      */     Token t;
/* 3591 */     switch ((this.jj_ntk == -1) ? jj_ntk_f() : this.jj_ntk) {
/*      */       case 148:
/* 3593 */         t = jj_consume_token(148);
/*      */         break;
/*      */       
/*      */       case 149:
/* 3597 */         t = jj_consume_token(149);
/*      */         break;
/*      */       
/*      */       default:
/* 3601 */         this.jj_la1[93] = this.jj_gen;
/* 3602 */         jj_consume_token(-1);
/* 3603 */         throw new ParseException();
/*      */     } 
/* 3605 */     if ("" != null) return t; 
/* 3606 */     throw new Error("Missing return statement in function");
/*      */   }
/*      */ 
/*      */   
/*      */   public final PropertySetting Setting() throws ParseException {
/* 3611 */     Token start = jj_consume_token(28);
/* 3612 */     Token key = jj_consume_token(142);
/* 3613 */     jj_consume_token(105);
/* 3614 */     Expression value = Expression();
/* 3615 */     Token end = LooseDirectiveEnd();
/* 3616 */     this.token_source.checkNamingConvention(key);
/* 3617 */     PropertySetting result = new PropertySetting(key, this.token_source, value, this.template.getConfiguration());
/* 3618 */     result.setLocation(this.template, start, end);
/* 3619 */     if ("" != null) return result; 
/* 3620 */     throw new Error("Missing return statement in function");
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public final TemplateElement FreemarkerDirective() throws ParseException {
/*      */     TemplateElement tp;
/* 3627 */     switch ((this.jj_ntk == -1) ? jj_ntk_f() : this.jj_ntk) {
/*      */       case 8:
/* 3629 */         tp = If();
/*      */         break;
/*      */       
/*      */       case 10:
/* 3633 */         tp = List();
/*      */         break;
/*      */       
/*      */       case 13:
/* 3637 */         tp = ForEach();
/*      */         break;
/*      */       
/*      */       case 16:
/*      */       case 17:
/*      */       case 18:
/* 3643 */         tp = Assign();
/*      */         break;
/*      */       
/*      */       case 19:
/* 3647 */         tp = Include();
/*      */         break;
/*      */       
/*      */       case 20:
/* 3651 */         tp = Import();
/*      */         break;
/*      */       
/*      */       case 21:
/*      */       case 22:
/* 3656 */         tp = Macro();
/*      */         break;
/*      */       
/*      */       case 32:
/* 3660 */         tp = Compress();
/*      */         break;
/*      */       
/*      */       case 74:
/* 3664 */         tp = UnifiedMacroTransform();
/*      */         break;
/*      */       
/*      */       case 11:
/* 3668 */         tp = Items();
/*      */         break;
/*      */       
/*      */       case 12:
/* 3672 */         tp = Sep();
/*      */         break;
/*      */       
/*      */       case 27:
/* 3676 */         tp = Call();
/*      */         break;
/*      */       
/*      */       case 33:
/*      */       case 34:
/* 3681 */         tp = Comment();
/*      */         break;
/*      */       
/*      */       case 35:
/* 3685 */         tp = NoParse();
/*      */         break;
/*      */       
/*      */       case 23:
/* 3689 */         tp = Transform();
/*      */         break;
/*      */       
/*      */       case 14:
/* 3693 */         tp = Switch();
/*      */         break;
/*      */       
/*      */       case 28:
/* 3697 */         tp = Setting();
/*      */         break;
/*      */       
/*      */       case 55:
/* 3701 */         tp = Break();
/*      */         break;
/*      */       
/*      */       case 56:
/* 3705 */         tp = Continue();
/*      */         break;
/*      */       
/*      */       case 26:
/*      */       case 57:
/* 3710 */         tp = Return();
/*      */         break;
/*      */       
/*      */       case 25:
/*      */       case 58:
/* 3715 */         tp = Stop();
/*      */         break;
/*      */       
/*      */       case 59:
/* 3719 */         tp = Flush();
/*      */         break;
/*      */       
/*      */       case 60:
/*      */       case 61:
/*      */       case 62:
/*      */       case 63:
/* 3726 */         tp = Trim();
/*      */         break;
/*      */       
/*      */       case 65:
/*      */       case 66:
/* 3731 */         tp = Nested();
/*      */         break;
/*      */       
/*      */       case 70:
/* 3735 */         tp = Escape();
/*      */         break;
/*      */       
/*      */       case 72:
/* 3739 */         tp = NoEscape();
/*      */         break;
/*      */       
/*      */       case 24:
/* 3743 */         tp = Visit();
/*      */         break;
/*      */       
/*      */       case 67:
/*      */       case 68:
/* 3748 */         tp = Recurse();
/*      */         break;
/*      */       
/*      */       case 69:
/* 3752 */         tp = FallBack();
/*      */         break;
/*      */       
/*      */       case 6:
/* 3756 */         tp = Attempt();
/*      */         break;
/*      */       
/*      */       case 29:
/* 3760 */         tp = OutputFormat();
/*      */         break;
/*      */       
/*      */       case 30:
/* 3764 */         tp = AutoEsc();
/*      */         break;
/*      */       
/*      */       case 31:
/* 3768 */         tp = NoAutoEsc();
/*      */         break;
/*      */       
/*      */       default:
/* 3772 */         this.jj_la1[94] = this.jj_gen;
/* 3773 */         jj_consume_token(-1);
/* 3774 */         throw new ParseException();
/*      */     } 
/* 3776 */     if ("" != null) return tp; 
/* 3777 */     throw new Error("Missing return statement in function");
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final TextBlock PCData() throws ParseException {
/* 3785 */     StringBuilder buf = new StringBuilder();
/* 3786 */     Token t = null, start = null, prevToken = null;
/*      */     
/*      */     while (true) {
/* 3789 */       switch ((this.jj_ntk == -1) ? jj_ntk_f() : this.jj_ntk) {
/*      */         case 79:
/* 3791 */           t = jj_consume_token(79);
/*      */           break;
/*      */         
/*      */         case 80:
/* 3795 */           t = jj_consume_token(80);
/*      */           break;
/*      */         
/*      */         case 81:
/* 3799 */           t = jj_consume_token(81);
/*      */           break;
/*      */         
/*      */         default:
/* 3803 */           this.jj_la1[95] = this.jj_gen;
/* 3804 */           jj_consume_token(-1);
/* 3805 */           throw new ParseException();
/*      */       } 
/* 3807 */       buf.append(t.image);
/* 3808 */       if (start == null) start = t; 
/* 3809 */       if (prevToken != null) prevToken.next = null; 
/* 3810 */       prevToken = t;
/* 3811 */       switch ((this.jj_ntk == -1) ? jj_ntk_f() : this.jj_ntk) {
/*      */         case 79:
/*      */         case 80:
/*      */         case 81:
/*      */           continue;
/*      */       } 
/*      */       break;
/*      */     } 
/* 3819 */     this.jj_la1[96] = this.jj_gen;
/*      */ 
/*      */ 
/*      */     
/* 3823 */     if (this.stripText && this.mixedContentNesting == 1 && !this.preventStrippings && "" != null) return null;
/*      */     
/* 3825 */     TextBlock result = new TextBlock(buf.toString(), false);
/* 3826 */     result.setLocation(this.template, start, t);
/* 3827 */     if ("" != null) return result; 
/* 3828 */     throw new Error("Missing return statement in function");
/*      */   }
/*      */   public final TextBlock WhitespaceText() throws ParseException {
/* 3831 */     Token t = null, start = null;
/* 3832 */     t = jj_consume_token(79);
/* 3833 */     if (this.stripText && this.mixedContentNesting == 1 && !this.preventStrippings && "" != null) return null;
/*      */     
/* 3835 */     TextBlock result = new TextBlock(t.image, false);
/* 3836 */     result.setLocation(this.template, t, t);
/* 3837 */     if ("" != null) return result; 
/* 3838 */     throw new Error("Missing return statement in function");
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final Token UnparsedContent(Token start, StringBuilder buf) throws ParseException {
/*      */     Token t;
/*      */     while (true) {
/* 3850 */       switch ((this.jj_ntk == -1) ? jj_ntk_f() : this.jj_ntk) {
/*      */         case 156:
/* 3852 */           t = jj_consume_token(156);
/*      */           break;
/*      */         
/*      */         case 155:
/* 3856 */           t = jj_consume_token(155);
/*      */           break;
/*      */         
/*      */         case 154:
/* 3860 */           t = jj_consume_token(154);
/*      */           break;
/*      */         
/*      */         case 157:
/* 3864 */           t = jj_consume_token(157);
/*      */           break;
/*      */         
/*      */         default:
/* 3868 */           this.jj_la1[97] = this.jj_gen;
/* 3869 */           jj_consume_token(-1);
/* 3870 */           throw new ParseException();
/*      */       } 
/* 3872 */       buf.append(t.image);
/* 3873 */       switch ((this.jj_ntk == -1) ? jj_ntk_f() : this.jj_ntk) {
/*      */         case 154:
/*      */         case 155:
/*      */         case 156:
/*      */         case 157:
/*      */           continue;
/*      */       } 
/*      */       break;
/*      */     } 
/* 3882 */     this.jj_la1[98] = this.jj_gen;
/*      */ 
/*      */ 
/*      */     
/* 3886 */     buf.setLength(buf.length() - t.image.length());
/* 3887 */     if (!t.image.endsWith(";") && 
/* 3888 */       _TemplateAPI.getTemplateLanguageVersionAsInt(this.template) >= _TemplateAPI.VERSION_INT_2_3_21) {
/* 3889 */       throw new ParseException("Unclosed \"" + start.image + "\"", this.template, start);
/*      */     }
/* 3891 */     if ("" != null) return t; 
/* 3892 */     throw new Error("Missing return statement in function");
/*      */   }
/*      */   public final TemplateElements MixedContentElements() throws ParseException {
/* 3895 */     TemplateElement[] childBuffer = null;
/* 3896 */     int childCount = 0;
/*      */     
/* 3898 */     this.mixedContentNesting++;
/*      */     while (true) {
/*      */       TemplateElement elem;
/* 3901 */       switch ((this.jj_ntk == -1) ? jj_ntk_f() : this.jj_ntk) {
/*      */         case 6:
/*      */         case 8:
/*      */         case 10:
/*      */         case 11:
/*      */         case 12:
/*      */         case 13:
/*      */         case 14:
/*      */         case 16:
/*      */         case 17:
/*      */         case 18:
/*      */         case 19:
/*      */         case 20:
/*      */         case 21:
/*      */         case 22:
/*      */         case 23:
/*      */         case 24:
/*      */         case 25:
/*      */         case 26:
/*      */         case 27:
/*      */         case 28:
/*      */         case 29:
/*      */         case 30:
/*      */         case 31:
/*      */         case 32:
/*      */         case 33:
/*      */         case 34:
/*      */         case 35:
/*      */         case 55:
/*      */         case 56:
/*      */         case 57:
/*      */         case 58:
/*      */         case 59:
/*      */         case 60:
/*      */         case 61:
/*      */         case 62:
/*      */         case 63:
/*      */         case 65:
/*      */         case 66:
/*      */         case 67:
/*      */         case 68:
/*      */         case 69:
/*      */         case 70:
/*      */         case 72:
/*      */         case 74:
/*      */         case 79:
/*      */         case 80:
/*      */         case 81:
/*      */         case 82:
/*      */         case 83:
/*      */         case 84:
/*      */           break;
/*      */ 
/*      */         
/*      */         default:
/* 3956 */           this.jj_la1[99] = this.jj_gen;
/*      */           break;
/*      */       } 
/* 3959 */       switch ((this.jj_ntk == -1) ? jj_ntk_f() : this.jj_ntk) {
/*      */         case 79:
/*      */         case 80:
/*      */         case 81:
/* 3963 */           elem = PCData();
/*      */           break;
/*      */         
/*      */         case 82:
/*      */         case 84:
/* 3968 */           elem = StringOutput();
/*      */           break;
/*      */         
/*      */         case 83:
/* 3972 */           elem = NumericalOutput();
/*      */           break;
/*      */         
/*      */         case 6:
/*      */         case 8:
/*      */         case 10:
/*      */         case 11:
/*      */         case 12:
/*      */         case 13:
/*      */         case 14:
/*      */         case 16:
/*      */         case 17:
/*      */         case 18:
/*      */         case 19:
/*      */         case 20:
/*      */         case 21:
/*      */         case 22:
/*      */         case 23:
/*      */         case 24:
/*      */         case 25:
/*      */         case 26:
/*      */         case 27:
/*      */         case 28:
/*      */         case 29:
/*      */         case 30:
/*      */         case 31:
/*      */         case 32:
/*      */         case 33:
/*      */         case 34:
/*      */         case 35:
/*      */         case 55:
/*      */         case 56:
/*      */         case 57:
/*      */         case 58:
/*      */         case 59:
/*      */         case 60:
/*      */         case 61:
/*      */         case 62:
/*      */         case 63:
/*      */         case 65:
/*      */         case 66:
/*      */         case 67:
/*      */         case 68:
/*      */         case 69:
/*      */         case 70:
/*      */         case 72:
/*      */         case 74:
/* 4019 */           elem = FreemarkerDirective();
/*      */           break;
/*      */         
/*      */         default:
/* 4023 */           this.jj_la1[100] = this.jj_gen;
/* 4024 */           jj_consume_token(-1);
/* 4025 */           throw new ParseException();
/*      */       } 
/*      */       
/* 4028 */       if (elem != null) {
/* 4029 */         childCount++;
/* 4030 */         if (childBuffer == null) {
/* 4031 */           childBuffer = new TemplateElement[16];
/* 4032 */         } else if (childBuffer.length < childCount) {
/* 4033 */           TemplateElement[] newChildBuffer = new TemplateElement[childCount * 2];
/* 4034 */           for (int i = 0; i < childBuffer.length; i++) {
/* 4035 */             newChildBuffer[i] = childBuffer[i];
/*      */           }
/* 4037 */           childBuffer = newChildBuffer;
/*      */         } 
/* 4039 */         childBuffer[childCount - 1] = elem;
/*      */       } 
/*      */     } 
/* 4042 */     this.mixedContentNesting--;
/* 4043 */     if ("" != null) return (childBuffer != null) ? new TemplateElements(childBuffer, childCount) : TemplateElements.EMPTY; 
/* 4044 */     throw new Error("Missing return statement in function");
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final MixedContent MixedContent() throws ParseException {
/*      */     TemplateElement elem;
/* 4052 */     MixedContent mixedContent = new MixedContent();
/* 4053 */     TemplateElement begin = null;
/* 4054 */     this.mixedContentNesting++;
/*      */     
/*      */     while (true) {
/* 4057 */       switch ((this.jj_ntk == -1) ? jj_ntk_f() : this.jj_ntk) {
/*      */         case 79:
/*      */         case 80:
/*      */         case 81:
/* 4061 */           elem = PCData();
/*      */           break;
/*      */         
/*      */         case 82:
/*      */         case 84:
/* 4066 */           elem = StringOutput();
/*      */           break;
/*      */         
/*      */         case 83:
/* 4070 */           elem = NumericalOutput();
/*      */           break;
/*      */         
/*      */         case 6:
/*      */         case 8:
/*      */         case 10:
/*      */         case 11:
/*      */         case 12:
/*      */         case 13:
/*      */         case 14:
/*      */         case 16:
/*      */         case 17:
/*      */         case 18:
/*      */         case 19:
/*      */         case 20:
/*      */         case 21:
/*      */         case 22:
/*      */         case 23:
/*      */         case 24:
/*      */         case 25:
/*      */         case 26:
/*      */         case 27:
/*      */         case 28:
/*      */         case 29:
/*      */         case 30:
/*      */         case 31:
/*      */         case 32:
/*      */         case 33:
/*      */         case 34:
/*      */         case 35:
/*      */         case 55:
/*      */         case 56:
/*      */         case 57:
/*      */         case 58:
/*      */         case 59:
/*      */         case 60:
/*      */         case 61:
/*      */         case 62:
/*      */         case 63:
/*      */         case 65:
/*      */         case 66:
/*      */         case 67:
/*      */         case 68:
/*      */         case 69:
/*      */         case 70:
/*      */         case 72:
/*      */         case 74:
/* 4117 */           elem = FreemarkerDirective();
/*      */           break;
/*      */         
/*      */         default:
/* 4121 */           this.jj_la1[101] = this.jj_gen;
/* 4122 */           jj_consume_token(-1);
/* 4123 */           throw new ParseException();
/*      */       } 
/* 4125 */       if (begin == null) {
/* 4126 */         begin = elem;
/*      */       }
/* 4128 */       mixedContent.addElement(elem);
/* 4129 */       switch ((this.jj_ntk == -1) ? jj_ntk_f() : this.jj_ntk) {
/*      */         case 6:
/*      */         case 8:
/*      */         case 10:
/*      */         case 11:
/*      */         case 12:
/*      */         case 13:
/*      */         case 14:
/*      */         case 16:
/*      */         case 17:
/*      */         case 18:
/*      */         case 19:
/*      */         case 20:
/*      */         case 21:
/*      */         case 22:
/*      */         case 23:
/*      */         case 24:
/*      */         case 25:
/*      */         case 26:
/*      */         case 27:
/*      */         case 28:
/*      */         case 29:
/*      */         case 30:
/*      */         case 31:
/*      */         case 32:
/*      */         case 33:
/*      */         case 34:
/*      */         case 35:
/*      */         case 55:
/*      */         case 56:
/*      */         case 57:
/*      */         case 58:
/*      */         case 59:
/*      */         case 60:
/*      */         case 61:
/*      */         case 62:
/*      */         case 63:
/*      */         case 65:
/*      */         case 66:
/*      */         case 67:
/*      */         case 68:
/*      */         case 69:
/*      */         case 70:
/*      */         case 72:
/*      */         case 74:
/*      */         case 79:
/*      */         case 80:
/*      */         case 81:
/*      */         case 82:
/*      */         case 83:
/*      */         case 84:
/*      */           continue;
/*      */       } 
/*      */       break;
/*      */     } 
/* 4184 */     this.jj_la1[102] = this.jj_gen;
/*      */ 
/*      */ 
/*      */     
/* 4188 */     this.mixedContentNesting--;
/* 4189 */     mixedContent.setLocation(this.template, begin, elem);
/* 4190 */     if ("" != null) return mixedContent; 
/* 4191 */     throw new Error("Missing return statement in function");
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
/*      */   public final TemplateElement OptionalBlock() throws ParseException {
/* 4203 */     TemplateElement tp = null;
/* 4204 */     switch ((this.jj_ntk == -1) ? jj_ntk_f() : this.jj_ntk) {
/*      */       
/*      */       case 6:
/*      */       case 8:
/*      */       case 10:
/*      */       case 11:
/*      */       case 12:
/*      */       case 13:
/*      */       case 14:
/*      */       case 16:
/*      */       case 17:
/*      */       case 18:
/*      */       case 19:
/*      */       case 20:
/*      */       case 21:
/*      */       case 22:
/*      */       case 23:
/*      */       case 24:
/*      */       case 25:
/*      */       case 26:
/*      */       case 27:
/*      */       case 28:
/*      */       case 29:
/*      */       case 30:
/*      */       case 31:
/*      */       case 32:
/*      */       case 33:
/*      */       case 34:
/*      */       case 35:
/*      */       case 55:
/*      */       case 56:
/*      */       case 57:
/*      */       case 58:
/*      */       case 59:
/*      */       case 60:
/*      */       case 61:
/*      */       case 62:
/*      */       case 63:
/*      */       case 65:
/*      */       case 66:
/*      */       case 67:
/*      */       case 68:
/*      */       case 69:
/*      */       case 70:
/*      */       case 72:
/*      */       case 74:
/*      */       case 79:
/*      */       case 80:
/*      */       case 81:
/*      */       case 82:
/*      */       case 83:
/*      */       case 84:
/* 4256 */         tp = MixedContent();
/*      */         break;
/*      */       
/*      */       default:
/* 4260 */         this.jj_la1[103] = this.jj_gen;
/*      */         break;
/*      */     } 
/* 4263 */     if ("" != null) return (tp != null) ? tp : new TextBlock(CollectionUtils.EMPTY_CHAR_ARRAY, false); 
/* 4264 */     throw new Error("Missing return statement in function");
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public final TemplateElement FreeMarkerText() throws ParseException {
/*      */     TemplateElement elem;
/* 4271 */     MixedContent nodes = new MixedContent();
/* 4272 */     TemplateElement begin = null;
/*      */     
/*      */     while (true) {
/* 4275 */       switch ((this.jj_ntk == -1) ? jj_ntk_f() : this.jj_ntk) {
/*      */         case 79:
/*      */         case 80:
/*      */         case 81:
/* 4279 */           elem = PCData();
/*      */           break;
/*      */         
/*      */         case 82:
/*      */         case 84:
/* 4284 */           elem = StringOutput();
/*      */           break;
/*      */         
/*      */         case 83:
/* 4288 */           elem = NumericalOutput();
/*      */           break;
/*      */         
/*      */         default:
/* 4292 */           this.jj_la1[104] = this.jj_gen;
/* 4293 */           jj_consume_token(-1);
/* 4294 */           throw new ParseException();
/*      */       } 
/* 4296 */       if (begin == null) {
/* 4297 */         begin = elem;
/*      */       }
/* 4299 */       nodes.addChild(elem);
/* 4300 */       switch ((this.jj_ntk == -1) ? jj_ntk_f() : this.jj_ntk) {
/*      */         case 79:
/*      */         case 80:
/*      */         case 81:
/*      */         case 82:
/*      */         case 83:
/*      */         case 84:
/*      */           continue;
/*      */       } 
/*      */       break;
/*      */     } 
/* 4311 */     this.jj_la1[105] = this.jj_gen;
/*      */ 
/*      */ 
/*      */     
/* 4315 */     nodes.setLocation(this.template, begin, elem);
/* 4316 */     if ("" != null) return nodes; 
/* 4317 */     throw new Error("Missing return statement in function");
/*      */   }
/*      */ 
/*      */   
/*      */   public final MixedContent WhitespaceAndComments() throws ParseException {
/*      */     TemplateElement elem;
/* 4323 */     MixedContent nodes = new MixedContent();
/* 4324 */     TemplateElement begin = null;
/*      */     
/*      */     while (true) {
/* 4327 */       switch ((this.jj_ntk == -1) ? jj_ntk_f() : this.jj_ntk) {
/*      */         case 79:
/* 4329 */           elem = WhitespaceText();
/*      */           break;
/*      */         
/*      */         case 33:
/*      */         case 34:
/* 4334 */           elem = Comment();
/*      */           break;
/*      */         
/*      */         default:
/* 4338 */           this.jj_la1[106] = this.jj_gen;
/* 4339 */           jj_consume_token(-1);
/* 4340 */           throw new ParseException();
/*      */       } 
/* 4342 */       if (elem != null) {
/* 4343 */         if (begin == null) {
/* 4344 */           begin = elem;
/*      */         }
/* 4346 */         nodes.addChild(elem);
/*      */       } 
/* 4348 */       switch ((this.jj_ntk == -1) ? jj_ntk_f() : this.jj_ntk) {
/*      */         case 33:
/*      */         case 34:
/*      */         case 79:
/*      */           continue;
/*      */       } 
/*      */       break;
/*      */     } 
/* 4356 */     this.jj_la1[107] = this.jj_gen;
/*      */ 
/*      */ 
/*      */     
/* 4360 */     if ((begin == null || (this.stripWhitespace && !this.preventStrippings && nodes
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 4365 */       .getChildCount() == 1 && nodes.getChild(0) instanceof TextBlock)) && 
/* 4366 */       "" != null) return null;
/*      */     
/* 4368 */     nodes.setLocation(this.template, begin, elem);
/* 4369 */     if ("" != null) return nodes; 
/* 4370 */     throw new Error("Missing return statement in function");
/*      */   }
/*      */   
/*      */   public final void HeaderElement() throws ParseException {
/* 4374 */     Expression exp = null;
/* 4375 */     Token autoEscRequester = null;
/* 4376 */     switch ((this.jj_ntk == -1) ? jj_ntk_f() : this.jj_ntk) {
/*      */       case 79:
/* 4378 */         jj_consume_token(79);
/*      */         break;
/*      */       
/*      */       default:
/* 4382 */         this.jj_la1[108] = this.jj_gen;
/*      */         break;
/*      */     } 
/* 4385 */     switch ((this.jj_ntk == -1) ? jj_ntk_f() : this.jj_ntk) {
/*      */       case 77:
/* 4387 */         jj_consume_token(77);
/*      */         return;
/*      */       
/*      */       case 76:
/* 4391 */         jj_consume_token(76);
/*      */         
/*      */         while (true) {
/* 4394 */           switch ((this.jj_ntk == -1) ? jj_ntk_f() : this.jj_ntk) {
/*      */             case 142:
/*      */               break;
/*      */ 
/*      */             
/*      */             default:
/* 4400 */               this.jj_la1[109] = this.jj_gen;
/*      */               break;
/*      */           } 
/* 4403 */           Token key = jj_consume_token(142);
/* 4404 */           jj_consume_token(105);
/* 4405 */           exp = Expression();
/* 4406 */           this.token_source.checkNamingConvention(key);
/*      */           
/* 4408 */           String ks = key.image;
/* 4409 */           TemplateModel value = null;
/*      */           try {
/* 4411 */             value = exp.eval((Environment)null);
/* 4412 */           } catch (Exception e) {
/* 4413 */             throw new ParseException("Could not evaluate expression (on parse-time): " + exp
/* 4414 */                 .getCanonicalForm() + " \nUnderlying cause: " + e, exp, e);
/*      */           } 
/*      */ 
/*      */           
/* 4418 */           String vs = null;
/* 4419 */           if (value instanceof TemplateScalarModel) {
/*      */             try {
/* 4421 */               vs = ((TemplateScalarModel)exp).getAsString();
/* 4422 */             } catch (TemplateModelException templateModelException) {}
/*      */           }
/* 4424 */           if (this.template != null) {
/* 4425 */             String correctName; if (ks.equalsIgnoreCase("encoding")) {
/* 4426 */               if (vs == null) {
/* 4427 */                 throw new ParseException("Expected a string constant for \"" + ks + "\".", exp);
/*      */               }
/* 4429 */               String encoding = this.template.getEncoding();
/* 4430 */               if (encoding != null && !encoding.equalsIgnoreCase(vs))
/* 4431 */                 throw new Template.WrongEncodingException(vs, encoding);  continue;
/*      */             } 
/* 4433 */             if (ks.equalsIgnoreCase("STRIP_WHITESPACE") || ks.equals("stripWhitespace")) {
/* 4434 */               this.stripWhitespace = getBoolean(exp, true); continue;
/* 4435 */             }  if (ks.equalsIgnoreCase("STRIP_TEXT") || ks.equals("stripText")) {
/* 4436 */               this.stripText = getBoolean(exp, true); continue;
/* 4437 */             }  if (ks.equalsIgnoreCase("STRICT_SYNTAX") || ks.equals("strictSyntax")) {
/* 4438 */               this.token_source.strictSyntaxMode = getBoolean(exp, true); continue;
/* 4439 */             }  if (ks.equalsIgnoreCase("auto_esc") || ks.equals("autoEsc")) {
/* 4440 */               if (getBoolean(exp, false)) {
/* 4441 */                 autoEscRequester = key;
/* 4442 */                 this.autoEscapingPolicy = 22;
/*      */               } else {
/* 4444 */                 this.autoEscapingPolicy = 20;
/*      */               } 
/* 4446 */               recalculateAutoEscapingField();
/* 4447 */               _TemplateAPI.setAutoEscaping(this.template, this.autoEscaping); continue;
/* 4448 */             }  if (ks.equalsIgnoreCase("output_format") || ks.equals("outputFormat")) {
/* 4449 */               if (vs == null) {
/* 4450 */                 throw new ParseException("Expected a string constant for \"" + ks + "\".", exp);
/*      */               }
/*      */               try {
/* 4453 */                 this.outputFormat = this.template.getConfiguration().getOutputFormat(vs);
/* 4454 */               } catch (IllegalArgumentException e) {
/* 4455 */                 throw new ParseException("Invalid format name: " + e.getMessage(), exp, e.getCause());
/* 4456 */               } catch (UnregisteredOutputFormatException e) {
/* 4457 */                 throw new ParseException(e.getMessage(), exp, e.getCause());
/*      */               } 
/* 4459 */               recalculateAutoEscapingField();
/* 4460 */               _TemplateAPI.setOutputFormat(this.template, this.outputFormat);
/* 4461 */               _TemplateAPI.setAutoEscaping(this.template, this.autoEscaping); continue;
/* 4462 */             }  if (ks.equalsIgnoreCase("ns_prefixes") || ks.equals("nsPrefixes")) {
/* 4463 */               if (!(value instanceof TemplateHashModelEx)) {
/* 4464 */                 throw new ParseException("Expecting a hash of prefixes to namespace URI's.", exp);
/*      */               }
/* 4466 */               TemplateHashModelEx prefixMap = (TemplateHashModelEx)value;
/*      */               try {
/* 4468 */                 TemplateCollectionModel keys = prefixMap.keys();
/* 4469 */                 for (TemplateModelIterator it = keys.iterator(); it.hasNext(); ) {
/* 4470 */                   String prefix = ((TemplateScalarModel)it.next()).getAsString();
/* 4471 */                   TemplateModel valueModel = prefixMap.get(prefix);
/* 4472 */                   if (!(valueModel instanceof TemplateScalarModel)) {
/* 4473 */                     throw new ParseException("Non-string value in prefix to namespace hash.", exp);
/*      */                   }
/* 4475 */                   String nsURI = ((TemplateScalarModel)valueModel).getAsString();
/*      */                   try {
/* 4477 */                     this.template.addPrefixNSMapping(prefix, nsURI);
/* 4478 */                   } catch (IllegalArgumentException iae) {
/* 4479 */                     throw new ParseException(iae.getMessage(), exp);
/*      */                   } 
/*      */                 } 
/* 4482 */               } catch (TemplateModelException templateModelException) {} continue;
/*      */             } 
/* 4484 */             if (ks.equalsIgnoreCase("attributes")) {
/* 4485 */               if (!(value instanceof TemplateHashModelEx)) {
/* 4486 */                 throw new ParseException("Expecting a hash of attribute names to values.", exp);
/*      */               }
/* 4488 */               TemplateHashModelEx attributeMap = (TemplateHashModelEx)value;
/*      */               try {
/* 4490 */                 TemplateCollectionModel keys = attributeMap.keys();
/* 4491 */                 for (TemplateModelIterator it = keys.iterator(); it.hasNext(); ) {
/* 4492 */                   String attName = ((TemplateScalarModel)it.next()).getAsString();
/* 4493 */                   Object attValue = DeepUnwrap.unwrap(attributeMap.get(attName));
/* 4494 */                   this.template.setCustomAttribute(attName, attValue);
/*      */                 } 
/* 4496 */               } catch (TemplateModelException templateModelException) {}
/*      */               
/*      */               continue;
/*      */             } 
/* 4500 */             if (ks.equals("charset")) {
/* 4501 */               correctName = "encoding";
/* 4502 */             } else if (ks.equals("xmlns")) {
/*      */               
/* 4504 */               correctName = (this.token_source.namingConvention == 12) ? "nsPrefixes" : "ns_prefixes";
/*      */             
/*      */             }
/* 4507 */             else if (ks.equals("auto_escape") || ks.equals("auto_escaping") || ks.equals("autoesc")) {
/* 4508 */               correctName = "auto_esc";
/* 4509 */             } else if (ks.equals("autoEscape") || ks.equals("autoEscaping")) {
/* 4510 */               correctName = "autoEsc";
/*      */             } else {
/* 4512 */               correctName = null;
/*      */             } 
/* 4514 */             throw new ParseException("Unknown FTL header parameter: " + key.image + ((correctName == null) ? "" : (". You may meant: " + correctName)), this.template, key);
/*      */           } 
/*      */         } 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 4521 */         if (autoEscRequester != null) {
/* 4522 */           checkCurrentOutputFormatCanEscape(autoEscRequester);
/*      */         }
/* 4524 */         LooseDirectiveEnd();
/*      */         return;
/*      */     } 
/*      */     
/* 4528 */     this.jj_la1[110] = this.jj_gen;
/* 4529 */     jj_consume_token(-1);
/* 4530 */     throw new ParseException();
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public final Map ParamList() throws ParseException {
/* 4536 */     Map<Object, Object> result = new HashMap<>();
/*      */     
/*      */     while (true) {
/* 4539 */       Identifier id = Identifier();
/* 4540 */       jj_consume_token(105);
/* 4541 */       Expression exp = Expression();
/* 4542 */       result.put(id.toString(), exp);
/* 4543 */       switch ((this.jj_ntk == -1) ? jj_ntk_f() : this.jj_ntk) {
/*      */         case 130:
/* 4545 */           jj_consume_token(130);
/*      */           break;
/*      */         
/*      */         default:
/* 4549 */           this.jj_la1[111] = this.jj_gen;
/*      */           break;
/*      */       } 
/* 4552 */       switch ((this.jj_ntk == -1) ? jj_ntk_f() : this.jj_ntk) {
/*      */         case 142:
/*      */           continue;
/*      */       } 
/*      */       break;
/*      */     } 
/* 4558 */     this.jj_la1[112] = this.jj_gen;
/*      */ 
/*      */ 
/*      */     
/* 4562 */     if ("" != null) return result; 
/* 4563 */     throw new Error("Missing return statement in function");
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final List<Object> StaticTextAndInterpolations() throws ParseException {
/* 4573 */     StringBuilder staticTextCollector = null;
/* 4574 */     ArrayList<Object> parts = new ArrayList(); while (true) {
/*      */       Token t; Interpolation interpolation;
/*      */       String s;
/* 4577 */       switch ((this.jj_ntk == -1) ? jj_ntk_f() : this.jj_ntk) {
/*      */         case 79:
/*      */         case 80:
/*      */         case 81:
/*      */         case 82:
/*      */         case 83:
/*      */         case 84:
/*      */           break;
/*      */ 
/*      */         
/*      */         default:
/* 4588 */           this.jj_la1[113] = this.jj_gen;
/*      */           break;
/*      */       } 
/* 4591 */       switch ((this.jj_ntk == -1) ? jj_ntk_f() : this.jj_ntk) {
/*      */         case 79:
/*      */         case 80:
/*      */         case 81:
/* 4595 */           switch ((this.jj_ntk == -1) ? jj_ntk_f() : this.jj_ntk) {
/*      */             case 79:
/* 4597 */               t = jj_consume_token(79);
/*      */               break;
/*      */             
/*      */             case 80:
/* 4601 */               t = jj_consume_token(80);
/*      */               break;
/*      */             
/*      */             case 81:
/* 4605 */               t = jj_consume_token(81);
/*      */               break;
/*      */             
/*      */             default:
/* 4609 */               this.jj_la1[114] = this.jj_gen;
/* 4610 */               jj_consume_token(-1);
/* 4611 */               throw new ParseException();
/*      */           } 
/* 4613 */           s = t.image;
/* 4614 */           if (s.length() != 0) {
/* 4615 */             if (staticTextCollector == null) {
/* 4616 */               staticTextCollector = new StringBuilder(t.image); continue;
/*      */             } 
/* 4618 */             staticTextCollector.append(t.image);
/*      */           } 
/*      */           continue;
/*      */ 
/*      */         
/*      */         case 82:
/*      */         case 83:
/*      */         case 84:
/* 4626 */           if (jj_2_15(2147483647)) {
/* 4627 */             interpolation = StringOutput();
/* 4628 */           } else if (jj_2_16(2147483647)) {
/* 4629 */             interpolation = NumericalOutput();
/*      */           } else {
/* 4631 */             jj_consume_token(-1);
/* 4632 */             throw new ParseException();
/*      */           } 
/* 4634 */           if (staticTextCollector != null) {
/* 4635 */             parts.add(staticTextCollector.toString());
/* 4636 */             staticTextCollector.setLength(0);
/*      */           } 
/* 4638 */           parts.add(interpolation);
/*      */           continue;
/*      */       } 
/*      */       
/* 4642 */       this.jj_la1[115] = this.jj_gen;
/* 4643 */       jj_consume_token(-1);
/* 4644 */       throw new ParseException();
/*      */     } 
/*      */     
/* 4647 */     if (staticTextCollector != null && staticTextCollector.length() != 0) {
/* 4648 */       parts.add(staticTextCollector.toString());
/*      */     }
/* 4650 */     parts.trimToSize();
/* 4651 */     if ("" != null) return parts; 
/* 4652 */     throw new Error("Missing return statement in function");
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final TemplateElement Root() throws ParseException {
/* 4660 */     if (jj_2_17(2147483647)) {
/* 4661 */       HeaderElement();
/*      */     }
/*      */ 
/*      */     
/* 4665 */     TemplateElements children = MixedContentElements();
/* 4666 */     jj_consume_token(0);
/* 4667 */     TemplateElement root = children.asSingleElement();
/* 4668 */     root.setFieldsForRootElement();
/* 4669 */     if (!this.preventStrippings) {
/* 4670 */       root = root.postParseCleanup(this.stripWhitespace);
/*      */     }
/*      */     
/* 4673 */     root.setFieldsForRootElement();
/* 4674 */     if ("" != null) return root; 
/* 4675 */     throw new Error("Missing return statement in function");
/*      */   }
/*      */ 
/*      */   
/*      */   private boolean jj_2_1(int xla) {
/* 4680 */     this.jj_la = xla; this.jj_lastpos = this.jj_scanpos = this.token; 
/* 4681 */     try { return !jj_3_1(); }
/* 4682 */     catch (LookaheadSuccess ls) { return true; }
/* 4683 */     finally { jj_save(0, xla); }
/*      */   
/*      */   }
/*      */   
/*      */   private boolean jj_2_2(int xla) {
/* 4688 */     this.jj_la = xla; this.jj_lastpos = this.jj_scanpos = this.token; 
/* 4689 */     try { return !jj_3_2(); }
/* 4690 */     catch (LookaheadSuccess ls) { return true; }
/* 4691 */     finally { jj_save(1, xla); }
/*      */   
/*      */   }
/*      */   
/*      */   private boolean jj_2_3(int xla) {
/* 4696 */     this.jj_la = xla; this.jj_lastpos = this.jj_scanpos = this.token; 
/* 4697 */     try { return !jj_3_3(); }
/* 4698 */     catch (LookaheadSuccess ls) { return true; }
/* 4699 */     finally { jj_save(2, xla); }
/*      */   
/*      */   }
/*      */   
/*      */   private boolean jj_2_4(int xla) {
/* 4704 */     this.jj_la = xla; this.jj_lastpos = this.jj_scanpos = this.token; 
/* 4705 */     try { return !jj_3_4(); }
/* 4706 */     catch (LookaheadSuccess ls) { return true; }
/* 4707 */     finally { jj_save(3, xla); }
/*      */   
/*      */   }
/*      */   
/*      */   private boolean jj_2_5(int xla) {
/* 4712 */     this.jj_la = xla; this.jj_lastpos = this.jj_scanpos = this.token; 
/* 4713 */     try { return !jj_3_5(); }
/* 4714 */     catch (LookaheadSuccess ls) { return true; }
/* 4715 */     finally { jj_save(4, xla); }
/*      */   
/*      */   }
/*      */   
/*      */   private boolean jj_2_6(int xla) {
/* 4720 */     this.jj_la = xla; this.jj_lastpos = this.jj_scanpos = this.token; 
/* 4721 */     try { return !jj_3_6(); }
/* 4722 */     catch (LookaheadSuccess ls) { return true; }
/* 4723 */     finally { jj_save(5, xla); }
/*      */   
/*      */   }
/*      */   
/*      */   private boolean jj_2_7(int xla) {
/* 4728 */     this.jj_la = xla; this.jj_lastpos = this.jj_scanpos = this.token; 
/* 4729 */     try { return !jj_3_7(); }
/* 4730 */     catch (LookaheadSuccess ls) { return true; }
/* 4731 */     finally { jj_save(6, xla); }
/*      */   
/*      */   }
/*      */   
/*      */   private boolean jj_2_8(int xla) {
/* 4736 */     this.jj_la = xla; this.jj_lastpos = this.jj_scanpos = this.token; 
/* 4737 */     try { return !jj_3_8(); }
/* 4738 */     catch (LookaheadSuccess ls) { return true; }
/* 4739 */     finally { jj_save(7, xla); }
/*      */   
/*      */   }
/*      */   
/*      */   private boolean jj_2_9(int xla) {
/* 4744 */     this.jj_la = xla; this.jj_lastpos = this.jj_scanpos = this.token; 
/* 4745 */     try { return !jj_3_9(); }
/* 4746 */     catch (LookaheadSuccess ls) { return true; }
/* 4747 */     finally { jj_save(8, xla); }
/*      */   
/*      */   }
/*      */   
/*      */   private boolean jj_2_10(int xla) {
/* 4752 */     this.jj_la = xla; this.jj_lastpos = this.jj_scanpos = this.token; 
/* 4753 */     try { return !jj_3_10(); }
/* 4754 */     catch (LookaheadSuccess ls) { return true; }
/* 4755 */     finally { jj_save(9, xla); }
/*      */   
/*      */   }
/*      */   
/*      */   private boolean jj_2_11(int xla) {
/* 4760 */     this.jj_la = xla; this.jj_lastpos = this.jj_scanpos = this.token; 
/* 4761 */     try { return !jj_3_11(); }
/* 4762 */     catch (LookaheadSuccess ls) { return true; }
/* 4763 */     finally { jj_save(10, xla); }
/*      */   
/*      */   }
/*      */   
/*      */   private boolean jj_2_12(int xla) {
/* 4768 */     this.jj_la = xla; this.jj_lastpos = this.jj_scanpos = this.token; 
/* 4769 */     try { return !jj_3_12(); }
/* 4770 */     catch (LookaheadSuccess ls) { return true; }
/* 4771 */     finally { jj_save(11, xla); }
/*      */   
/*      */   }
/*      */   
/*      */   private boolean jj_2_13(int xla) {
/* 4776 */     this.jj_la = xla; this.jj_lastpos = this.jj_scanpos = this.token; 
/* 4777 */     try { return !jj_3_13(); }
/* 4778 */     catch (LookaheadSuccess ls) { return true; }
/* 4779 */     finally { jj_save(12, xla); }
/*      */   
/*      */   }
/*      */   
/*      */   private boolean jj_2_14(int xla) {
/* 4784 */     this.jj_la = xla; this.jj_lastpos = this.jj_scanpos = this.token; 
/* 4785 */     try { return !jj_3_14(); }
/* 4786 */     catch (LookaheadSuccess ls) { return true; }
/* 4787 */     finally { jj_save(13, xla); }
/*      */   
/*      */   }
/*      */   
/*      */   private boolean jj_2_15(int xla) {
/* 4792 */     this.jj_la = xla; this.jj_lastpos = this.jj_scanpos = this.token; 
/* 4793 */     try { return !jj_3_15(); }
/* 4794 */     catch (LookaheadSuccess ls) { return true; }
/* 4795 */     finally { jj_save(14, xla); }
/*      */   
/*      */   }
/*      */   
/*      */   private boolean jj_2_16(int xla) {
/* 4800 */     this.jj_la = xla; this.jj_lastpos = this.jj_scanpos = this.token; 
/* 4801 */     try { return !jj_3_16(); }
/* 4802 */     catch (LookaheadSuccess ls) { return true; }
/* 4803 */     finally { jj_save(15, xla); }
/*      */   
/*      */   }
/*      */   
/*      */   private boolean jj_2_17(int xla) {
/* 4808 */     this.jj_la = xla; this.jj_lastpos = this.jj_scanpos = this.token; 
/* 4809 */     try { return !jj_3_17(); }
/* 4810 */     catch (LookaheadSuccess ls) { return true; }
/* 4811 */     finally { jj_save(16, xla); }
/*      */   
/*      */   }
/*      */   
/*      */   private boolean jj_3R_66() {
/* 4816 */     if (jj_3R_81()) return true; 
/* 4817 */     return false;
/*      */   }
/*      */ 
/*      */   
/*      */   private boolean jj_3R_65() {
/* 4822 */     if (jj_3R_80()) return true; 
/* 4823 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean jj_3_3() {
/* 4829 */     Token xsp = this.jj_scanpos;
/* 4830 */     if (jj_scan_token(107)) {
/* 4831 */       this.jj_scanpos = xsp;
/* 4832 */       if (jj_scan_token(105)) {
/* 4833 */         this.jj_scanpos = xsp;
/* 4834 */         if (jj_scan_token(106)) return true; 
/*      */       } 
/*      */     } 
/* 4837 */     return false;
/*      */   }
/*      */ 
/*      */   
/*      */   private boolean jj_3_12() {
/* 4842 */     if (jj_scan_token(142)) return true; 
/* 4843 */     if (jj_scan_token(105)) return true; 
/* 4844 */     return false;
/*      */   }
/*      */ 
/*      */   
/*      */   private boolean jj_3R_64() {
/* 4849 */     if (jj_3R_79()) return true; 
/* 4850 */     return false;
/*      */   }
/*      */ 
/*      */   
/*      */   private boolean jj_3R_102() {
/* 4855 */     if (jj_3R_88()) return true; 
/* 4856 */     return false;
/*      */   }
/*      */ 
/*      */   
/*      */   private boolean jj_3R_95() {
/* 4861 */     if (jj_scan_token(94)) return true; 
/* 4862 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean jj_3R_60() {
/* 4868 */     Token xsp = this.jj_scanpos;
/* 4869 */     if (jj_3R_64()) {
/* 4870 */       this.jj_scanpos = xsp;
/* 4871 */       if (jj_3R_65()) {
/* 4872 */         this.jj_scanpos = xsp;
/* 4873 */         if (jj_3R_66()) {
/* 4874 */           this.jj_scanpos = xsp;
/* 4875 */           if (jj_3R_67()) {
/* 4876 */             this.jj_scanpos = xsp;
/* 4877 */             if (jj_3R_68()) {
/* 4878 */               this.jj_scanpos = xsp;
/* 4879 */               if (jj_3R_69()) {
/* 4880 */                 this.jj_scanpos = xsp;
/* 4881 */                 if (jj_3R_70()) {
/* 4882 */                   this.jj_scanpos = xsp;
/* 4883 */                   if (jj_3R_71()) return true; 
/*      */                 } 
/*      */               } 
/*      */             } 
/*      */           } 
/*      */         } 
/*      */       } 
/*      */     } 
/* 4891 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean jj_3R_57() {
/* 4897 */     Token xsp = this.jj_scanpos;
/* 4898 */     if (jj_scan_token(107)) {
/* 4899 */       this.jj_scanpos = xsp;
/* 4900 */       if (jj_scan_token(105)) {
/* 4901 */         this.jj_scanpos = xsp;
/* 4902 */         if (jj_scan_token(106)) return true; 
/*      */       } 
/*      */     } 
/* 4905 */     if (jj_3R_56()) return true; 
/* 4906 */     return false;
/*      */   }
/*      */ 
/*      */   
/*      */   private boolean jj_3R_43() {
/* 4911 */     if (jj_scan_token(142)) return true; 
/* 4912 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean jj_3R_81() {
/* 4918 */     Token xsp = this.jj_scanpos;
/* 4919 */     if (jj_scan_token(93)) {
/* 4920 */       this.jj_scanpos = xsp;
/* 4921 */       if (jj_3R_95()) return true; 
/*      */     } 
/* 4923 */     return false;
/*      */   }
/*      */ 
/*      */   
/*      */   private boolean jj_3R_50() {
/* 4928 */     if (jj_3R_56()) return true;
/*      */     
/* 4930 */     Token xsp = this.jj_scanpos;
/* 4931 */     if (jj_3R_57()) this.jj_scanpos = xsp; 
/* 4932 */     return false;
/*      */   }
/*      */ 
/*      */   
/*      */   private boolean jj_3R_101() {
/* 4937 */     if (jj_scan_token(135)) return true; 
/* 4938 */     if (jj_3R_109()) return true; 
/* 4939 */     if (jj_scan_token(136)) return true; 
/* 4940 */     return false;
/*      */   }
/*      */ 
/*      */   
/*      */   private boolean jj_3R_77() {
/* 4945 */     if (jj_3R_91()) return true; 
/* 4946 */     return false;
/*      */   }
/*      */ 
/*      */   
/*      */   private boolean jj_3R_76() {
/* 4951 */     if (jj_3R_90()) return true; 
/* 4952 */     return false;
/*      */   }
/*      */ 
/*      */   
/*      */   private boolean jj_3R_88() {
/* 4957 */     if (jj_scan_token(135)) return true; 
/* 4958 */     if (jj_3R_98()) return true; 
/* 4959 */     if (jj_scan_token(136)) return true; 
/* 4960 */     return false;
/*      */   }
/*      */ 
/*      */   
/*      */   private boolean jj_3R_75() {
/* 4965 */     if (jj_3R_89()) return true; 
/* 4966 */     return false;
/*      */   }
/*      */ 
/*      */   
/*      */   private boolean jj_3R_74() {
/* 4971 */     if (jj_3R_88()) return true; 
/* 4972 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean jj_3R_114() {
/* 4978 */     Token xsp = this.jj_scanpos;
/* 4979 */     if (jj_scan_token(130)) this.jj_scanpos = xsp; 
/* 4980 */     if (jj_3R_113()) return true; 
/* 4981 */     return false;
/*      */   }
/*      */ 
/*      */   
/*      */   private boolean jj_3R_73() {
/* 4986 */     if (jj_3R_87()) return true; 
/* 4987 */     return false;
/*      */   }
/*      */ 
/*      */   
/*      */   private boolean jj_3R_49() {
/* 4992 */     if (jj_scan_token(126)) return true; 
/* 4993 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean jj_3R_79() {
/* 4999 */     Token xsp = this.jj_scanpos;
/* 5000 */     if (jj_scan_token(97)) {
/* 5001 */       this.jj_scanpos = xsp;
/* 5002 */       if (jj_scan_token(98)) return true; 
/*      */     } 
/* 5004 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean jj_3R_61() {
/* 5010 */     Token xsp = this.jj_scanpos;
/* 5011 */     if (jj_3R_72()) {
/* 5012 */       this.jj_scanpos = xsp;
/* 5013 */       if (jj_3R_73()) {
/* 5014 */         this.jj_scanpos = xsp;
/* 5015 */         if (jj_3R_74()) {
/* 5016 */           this.jj_scanpos = xsp;
/* 5017 */           if (jj_3R_75()) {
/* 5018 */             this.jj_scanpos = xsp;
/* 5019 */             if (jj_3R_76()) {
/* 5020 */               this.jj_scanpos = xsp;
/* 5021 */               if (jj_3R_77()) return true; 
/*      */             } 
/*      */           } 
/*      */         } 
/*      */       } 
/*      */     } 
/* 5027 */     return false;
/*      */   }
/*      */ 
/*      */   
/*      */   private boolean jj_3R_72() {
/* 5032 */     if (jj_3R_86()) return true; 
/* 5033 */     return false;
/*      */   }
/*      */ 
/*      */   
/*      */   private boolean jj_3R_48() {
/* 5038 */     if (jj_scan_token(125)) return true; 
/* 5039 */     return false;
/*      */   }
/*      */ 
/*      */   
/*      */   private boolean jj_3R_47() {
/* 5044 */     if (jj_scan_token(122)) return true; 
/* 5045 */     return false;
/*      */   }
/*      */ 
/*      */   
/*      */   private boolean jj_3R_112() {
/* 5050 */     if (jj_3R_113()) return true;
/*      */     
/*      */     while (true) {
/* 5053 */       Token xsp = this.jj_scanpos;
/* 5054 */       if (jj_3R_114()) { this.jj_scanpos = xsp;
/*      */         
/* 5056 */         return false; }
/*      */     
/*      */     } 
/*      */   }
/*      */   private boolean jj_3R_100() {
/* 5061 */     if (jj_scan_token(135)) return true; 
/* 5062 */     if (jj_3R_98()) return true; 
/* 5063 */     if (jj_scan_token(136)) return true; 
/* 5064 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean jj_3_2() {
/* 5070 */     Token xsp = this.jj_scanpos;
/* 5071 */     if (jj_scan_token(122)) {
/* 5072 */       this.jj_scanpos = xsp;
/* 5073 */       if (jj_scan_token(125)) {
/* 5074 */         this.jj_scanpos = xsp;
/* 5075 */         if (jj_scan_token(126)) return true; 
/*      */       } 
/*      */     } 
/* 5078 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean jj_3_17() {
/* 5084 */     Token xsp = this.jj_scanpos;
/* 5085 */     if (jj_scan_token(79)) this.jj_scanpos = xsp; 
/* 5086 */     xsp = this.jj_scanpos;
/* 5087 */     if (jj_scan_token(77)) {
/* 5088 */       this.jj_scanpos = xsp;
/* 5089 */       if (jj_scan_token(76)) return true; 
/*      */     } 
/* 5091 */     return false;
/*      */   }
/*      */ 
/*      */   
/*      */   private boolean jj_3R_55() {
/* 5096 */     if (jj_3R_60()) return true;
/*      */     
/*      */     while (true) {
/* 5099 */       Token xsp = this.jj_scanpos;
/* 5100 */       if (jj_3R_61()) { this.jj_scanpos = xsp;
/*      */         
/* 5102 */         return false; }
/*      */     
/*      */     } 
/*      */   }
/*      */   
/*      */   private boolean jj_3R_109() {
/* 5108 */     Token xsp = this.jj_scanpos;
/* 5109 */     if (jj_3R_112()) this.jj_scanpos = xsp; 
/* 5110 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean jj_3R_37() {
/* 5116 */     Token xsp = this.jj_scanpos;
/* 5117 */     if (jj_3R_47()) {
/* 5118 */       this.jj_scanpos = xsp;
/* 5119 */       if (jj_3R_48()) {
/* 5120 */         this.jj_scanpos = xsp;
/* 5121 */         if (jj_3R_49()) return true; 
/*      */       } 
/*      */     } 
/* 5124 */     if (jj_3R_36()) return true; 
/* 5125 */     return false;
/*      */   }
/*      */ 
/*      */   
/*      */   private boolean jj_3R_83() {
/* 5130 */     if (jj_scan_token(133)) return true; 
/* 5131 */     if (jj_3R_98()) return true; 
/* 5132 */     if (jj_scan_token(134)) return true; 
/* 5133 */     return false;
/*      */   }
/*      */ 
/*      */   
/*      */   private boolean jj_3R_87() {
/* 5138 */     if (jj_scan_token(133)) return true; 
/* 5139 */     if (jj_3R_29()) return true; 
/* 5140 */     if (jj_scan_token(134)) return true; 
/* 5141 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean jj_3R_111() {
/* 5147 */     Token xsp = this.jj_scanpos;
/* 5148 */     if (jj_scan_token(130)) this.jj_scanpos = xsp; 
/* 5149 */     if (jj_3R_29()) return true; 
/* 5150 */     return false;
/*      */   }
/*      */ 
/*      */   
/*      */   private boolean jj_3R_31() {
/* 5155 */     if (jj_3R_36()) return true;
/*      */     
/*      */     while (true) {
/* 5158 */       Token xsp = this.jj_scanpos;
/* 5159 */       if (jj_3R_37()) { this.jj_scanpos = xsp;
/*      */         
/* 5161 */         return false; }
/*      */     
/*      */     } 
/*      */   }
/*      */   private boolean jj_3R_29() {
/* 5166 */     if (jj_3R_33()) return true; 
/* 5167 */     return false;
/*      */   }
/*      */ 
/*      */   
/*      */   private boolean jj_3_7() {
/* 5172 */     if (jj_scan_token(128)) return true; 
/* 5173 */     return false;
/*      */   }
/*      */ 
/*      */   
/*      */   private boolean jj_3R_108() {
/* 5178 */     if (jj_3R_29()) return true;
/*      */     
/*      */     while (true) {
/* 5181 */       Token xsp = this.jj_scanpos;
/* 5182 */       if (jj_3R_111()) { this.jj_scanpos = xsp;
/*      */         
/* 5184 */         return false; }
/*      */     
/*      */     } 
/*      */   }
/*      */   
/*      */   private boolean jj_3R_98() {
/* 5190 */     Token xsp = this.jj_scanpos;
/* 5191 */     if (jj_3R_108()) this.jj_scanpos = xsp; 
/* 5192 */     return false;
/*      */   }
/*      */ 
/*      */   
/*      */   private boolean jj_3R_41() {
/* 5197 */     if (jj_scan_token(128)) return true; 
/* 5198 */     if (jj_3R_40()) return true; 
/* 5199 */     return false;
/*      */   }
/*      */ 
/*      */   
/*      */   private boolean jj_3_16() {
/* 5204 */     if (jj_scan_token(83)) return true; 
/* 5205 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean jj_3_15() {
/* 5211 */     Token xsp = this.jj_scanpos;
/* 5212 */     if (jj_scan_token(82)) {
/* 5213 */       this.jj_scanpos = xsp;
/* 5214 */       if (jj_scan_token(84)) return true; 
/*      */     } 
/* 5216 */     return false;
/*      */   }
/*      */ 
/*      */   
/*      */   private boolean jj_3R_33() {
/* 5221 */     if (jj_3R_40()) return true;
/*      */     
/*      */     while (true) {
/* 5224 */       Token xsp = this.jj_scanpos;
/* 5225 */       if (jj_3R_41()) { this.jj_scanpos = xsp;
/*      */         
/* 5227 */         return false; }
/*      */     
/*      */     } 
/*      */   }
/*      */   private boolean jj_3R_39() {
/* 5232 */     if (jj_scan_token(121)) return true; 
/* 5233 */     return false;
/*      */   }
/*      */ 
/*      */   
/*      */   private boolean jj_3R_38() {
/* 5238 */     if (jj_scan_token(120)) return true; 
/* 5239 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean jj_3_1() {
/* 5245 */     Token xsp = this.jj_scanpos;
/* 5246 */     if (jj_scan_token(120)) {
/* 5247 */       this.jj_scanpos = xsp;
/* 5248 */       if (jj_scan_token(121)) return true; 
/*      */     } 
/* 5250 */     return false;
/*      */   }
/*      */ 
/*      */   
/*      */   private boolean jj_3_6() {
/* 5255 */     if (jj_scan_token(127)) return true; 
/* 5256 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean jj_3R_32() {
/* 5262 */     Token xsp = this.jj_scanpos;
/* 5263 */     if (jj_3R_38()) {
/* 5264 */       this.jj_scanpos = xsp;
/* 5265 */       if (jj_3R_39()) return true; 
/*      */     } 
/* 5267 */     if (jj_3R_31()) return true; 
/* 5268 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean jj_3R_99() {
/* 5274 */     Token xsp = this.jj_scanpos;
/* 5275 */     if (jj_scan_token(115)) {
/* 5276 */       this.jj_scanpos = xsp;
/* 5277 */       if (jj_scan_token(116)) {
/* 5278 */         this.jj_scanpos = xsp;
/* 5279 */         if (jj_scan_token(117)) {
/* 5280 */           this.jj_scanpos = xsp;
/* 5281 */           if (jj_scan_token(118)) {
/* 5282 */             this.jj_scanpos = xsp;
/* 5283 */             if (jj_scan_token(95)) {
/* 5284 */               this.jj_scanpos = xsp;
/* 5285 */               if (jj_scan_token(96)) {
/* 5286 */                 this.jj_scanpos = xsp;
/* 5287 */                 if (jj_scan_token(139)) {
/* 5288 */                   this.jj_scanpos = xsp;
/* 5289 */                   if (jj_scan_token(140)) {
/* 5290 */                     this.jj_scanpos = xsp;
/* 5291 */                     if (jj_scan_token(141)) return true; 
/*      */                   } 
/*      */                 } 
/*      */               } 
/*      */             } 
/*      */           } 
/*      */         } 
/*      */       } 
/*      */     } 
/* 5300 */     return false;
/*      */   }
/*      */ 
/*      */   
/*      */   private boolean jj_3R_51() {
/* 5305 */     if (jj_scan_token(127)) return true; 
/* 5306 */     if (jj_3R_50()) return true; 
/* 5307 */     return false;
/*      */   }
/*      */ 
/*      */   
/*      */   private boolean jj_3R_28() {
/* 5312 */     if (jj_3R_31()) return true;
/*      */     
/*      */     while (true) {
/* 5315 */       Token xsp = this.jj_scanpos;
/* 5316 */       if (jj_3R_32()) { this.jj_scanpos = xsp;
/*      */         
/* 5318 */         return false; }
/*      */     
/*      */     } 
/*      */   }
/*      */   private boolean jj_3R_89() {
/* 5323 */     if (jj_scan_token(103)) return true; 
/* 5324 */     if (jj_scan_token(142)) return true;
/*      */     
/* 5326 */     Token xsp = this.jj_scanpos;
/* 5327 */     if (jj_3R_100()) this.jj_scanpos = xsp; 
/* 5328 */     xsp = this.jj_scanpos;
/* 5329 */     if (jj_3R_101()) this.jj_scanpos = xsp; 
/* 5330 */     xsp = this.jj_scanpos;
/* 5331 */     if (jj_3R_102()) this.jj_scanpos = xsp; 
/* 5332 */     return false;
/*      */   }
/*      */ 
/*      */   
/*      */   private boolean jj_3R_40() {
/* 5337 */     if (jj_3R_50()) return true;
/*      */     
/*      */     while (true) {
/* 5340 */       Token xsp = this.jj_scanpos;
/* 5341 */       if (jj_3R_51()) { this.jj_scanpos = xsp;
/*      */         
/* 5343 */         return false; }
/*      */     
/*      */     } 
/*      */   }
/*      */   private boolean jj_3R_86() {
/* 5348 */     if (jj_scan_token(99)) return true;
/*      */     
/* 5350 */     Token xsp = this.jj_scanpos;
/* 5351 */     if (jj_scan_token(142)) {
/* 5352 */       this.jj_scanpos = xsp;
/* 5353 */       if (jj_scan_token(122)) {
/* 5354 */         this.jj_scanpos = xsp;
/* 5355 */         if (jj_scan_token(123)) {
/* 5356 */           this.jj_scanpos = xsp;
/* 5357 */           if (jj_3R_99()) return true; 
/*      */         } 
/*      */       } 
/*      */     } 
/* 5361 */     return false;
/*      */   }
/*      */ 
/*      */   
/*      */   private boolean jj_3_13() {
/* 5366 */     if (jj_scan_token(135)) return true; 
/* 5367 */     return false;
/*      */   }
/*      */ 
/*      */   
/*      */   private boolean jj_3_5() {
/* 5372 */     if (jj_3R_28()) return true; 
/* 5373 */     return false;
/*      */   }
/*      */ 
/*      */   
/*      */   private boolean jj_3R_58() {
/* 5378 */     if (jj_scan_token(121)) return true; 
/* 5379 */     return false;
/*      */   }
/*      */ 
/*      */   
/*      */   private boolean jj_3_14() {
/* 5384 */     if (jj_scan_token(142)) return true; 
/* 5385 */     if (jj_scan_token(105)) return true; 
/* 5386 */     return false;
/*      */   }
/*      */ 
/*      */   
/*      */   private boolean jj_3R_106() {
/* 5391 */     if (jj_3R_28()) return true; 
/* 5392 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean jj_3R_53() {
/* 5398 */     Token xsp = this.jj_scanpos;
/* 5399 */     if (jj_scan_token(120)) {
/* 5400 */       this.jj_scanpos = xsp;
/* 5401 */       if (jj_3R_58()) return true; 
/*      */     } 
/* 5403 */     if (jj_3R_55()) return true; 
/* 5404 */     return false;
/*      */   }
/*      */ 
/*      */   
/*      */   private boolean jj_3R_91() {
/* 5409 */     if (jj_scan_token(104)) return true; 
/* 5410 */     return false;
/*      */   }
/*      */ 
/*      */   
/*      */   private boolean jj_3_8() {
/* 5415 */     if (jj_3R_29()) return true; 
/* 5416 */     return false;
/*      */   }
/*      */ 
/*      */   
/*      */   private boolean jj_3R_52() {
/* 5421 */     if (jj_scan_token(130)) return true; 
/* 5422 */     if (jj_3R_43()) return true; 
/* 5423 */     return false;
/*      */   }
/*      */ 
/*      */   
/*      */   private boolean jj_3R_35() {
/* 5428 */     if (jj_3R_43()) return true; 
/* 5429 */     return false;
/*      */   }
/*      */ 
/*      */   
/*      */   private boolean jj_3R_107() {
/* 5434 */     if (jj_scan_token(130)) return true; 
/* 5435 */     if (jj_3R_29()) return true;
/*      */     
/* 5437 */     Token xsp = this.jj_scanpos;
/* 5438 */     if (jj_scan_token(130)) {
/* 5439 */       this.jj_scanpos = xsp;
/* 5440 */       if (jj_scan_token(132)) return true; 
/*      */     } 
/* 5442 */     if (jj_3R_29()) return true; 
/* 5443 */     return false;
/*      */   }
/*      */ 
/*      */   
/*      */   private boolean jj_3R_105() {
/* 5448 */     if (jj_scan_token(102)) return true; 
/* 5449 */     return false;
/*      */   }
/*      */ 
/*      */   
/*      */   private boolean jj_3R_104() {
/* 5454 */     if (jj_scan_token(101)) return true; 
/* 5455 */     return false;
/*      */   }
/*      */ 
/*      */   
/*      */   private boolean jj_3R_110() {
/* 5460 */     if (jj_3R_29()) return true; 
/* 5461 */     return false;
/*      */   }
/*      */ 
/*      */   
/*      */   private boolean jj_3R_93() {
/* 5466 */     if (jj_scan_token(100)) return true;
/*      */     
/* 5468 */     Token xsp = this.jj_scanpos;
/* 5469 */     if (jj_3R_106()) this.jj_scanpos = xsp; 
/* 5470 */     return false;
/*      */   }
/*      */ 
/*      */   
/*      */   private boolean jj_3R_42() {
/* 5475 */     if (jj_3R_43()) return true;
/*      */     
/*      */     while (true) {
/* 5478 */       Token xsp = this.jj_scanpos;
/* 5479 */       if (jj_3R_52()) { this.jj_scanpos = xsp;
/*      */         
/* 5481 */         return false; }
/*      */     
/*      */     } 
/*      */   }
/*      */   private boolean jj_3R_59() {
/* 5486 */     if (jj_scan_token(129)) return true; 
/* 5487 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean jj_3R_92() {
/* 5493 */     Token xsp = this.jj_scanpos;
/* 5494 */     if (jj_3R_104()) {
/* 5495 */       this.jj_scanpos = xsp;
/* 5496 */       if (jj_3R_105()) return true; 
/*      */     } 
/* 5498 */     if (jj_3R_28()) return true; 
/* 5499 */     return false;
/*      */   }
/*      */ 
/*      */   
/*      */   private boolean jj_3R_94() {
/* 5504 */     if (jj_3R_29()) return true;
/*      */     
/* 5506 */     Token xsp = this.jj_scanpos;
/* 5507 */     if (jj_scan_token(130)) {
/* 5508 */       this.jj_scanpos = xsp;
/* 5509 */       if (jj_scan_token(132)) return true; 
/*      */     } 
/* 5511 */     if (jj_3R_29()) return true; 
/*      */     while (true) {
/* 5513 */       xsp = this.jj_scanpos;
/* 5514 */       if (jj_3R_107()) { this.jj_scanpos = xsp;
/*      */         
/* 5516 */         return false; }
/*      */     
/*      */     } 
/*      */   }
/*      */   private boolean jj_3R_103() {
/* 5521 */     if (jj_scan_token(129)) return true;
/*      */     
/* 5523 */     Token xsp = this.jj_scanpos;
/* 5524 */     if (jj_3R_110()) this.jj_scanpos = xsp; 
/* 5525 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean jj_3R_54() {
/* 5531 */     if (jj_3R_59()) return true; 
/*      */     while (true) {
/* 5533 */       Token xsp = this.jj_scanpos;
/* 5534 */       if (jj_3R_59()) { this.jj_scanpos = xsp;
/*      */         
/* 5536 */         if (jj_3R_55()) return true; 
/* 5537 */         return false; }
/*      */     
/*      */     } 
/*      */   }
/*      */   private boolean jj_3R_34() {
/* 5542 */     if (jj_scan_token(135)) return true;
/*      */     
/* 5544 */     Token xsp = this.jj_scanpos;
/* 5545 */     if (jj_3R_42()) this.jj_scanpos = xsp; 
/* 5546 */     if (jj_scan_token(136)) return true; 
/* 5547 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean jj_3R_78() {
/* 5553 */     Token xsp = this.jj_scanpos;
/* 5554 */     if (jj_3R_92()) {
/* 5555 */       this.jj_scanpos = xsp;
/* 5556 */       if (jj_3R_93()) return true; 
/*      */     } 
/* 5558 */     return false;
/*      */   }
/*      */ 
/*      */   
/*      */   private boolean jj_3R_80() {
/* 5563 */     if (jj_scan_token(137)) return true;
/*      */     
/* 5565 */     Token xsp = this.jj_scanpos;
/* 5566 */     if (jj_3R_94()) this.jj_scanpos = xsp; 
/* 5567 */     if (jj_scan_token(138)) return true; 
/* 5568 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean jj_3R_90() {
/* 5574 */     Token xsp = this.jj_scanpos;
/* 5575 */     if (jj_scan_token(153)) {
/* 5576 */       this.jj_scanpos = xsp;
/* 5577 */       if (jj_3R_103()) return true; 
/*      */     } 
/* 5579 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean jj_3R_30() {
/* 5585 */     Token xsp = this.jj_scanpos;
/* 5586 */     if (jj_3R_34()) {
/* 5587 */       this.jj_scanpos = xsp;
/* 5588 */       if (jj_3R_35()) return true; 
/*      */     } 
/* 5590 */     return false;
/*      */   }
/*      */ 
/*      */   
/*      */   private boolean jj_3R_46() {
/* 5595 */     if (jj_3R_55()) return true; 
/* 5596 */     return false;
/*      */   }
/*      */ 
/*      */   
/*      */   private boolean jj_3R_62() {
/* 5601 */     if (jj_3R_28()) return true;
/*      */     
/* 5603 */     Token xsp = this.jj_scanpos;
/* 5604 */     if (jj_3R_78()) this.jj_scanpos = xsp; 
/* 5605 */     return false;
/*      */   }
/*      */ 
/*      */   
/*      */   private boolean jj_3R_45() {
/* 5610 */     if (jj_3R_54()) return true; 
/* 5611 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean jj_3_11() {
/* 5617 */     Token xsp = this.jj_scanpos;
/* 5618 */     if (jj_scan_token(130)) this.jj_scanpos = xsp; 
/* 5619 */     xsp = this.jj_scanpos;
/* 5620 */     if (jj_scan_token(142)) {
/* 5621 */       this.jj_scanpos = xsp;
/* 5622 */       if (jj_scan_token(93)) return true; 
/*      */     } 
/* 5624 */     xsp = this.jj_scanpos;
/* 5625 */     if (jj_scan_token(105)) {
/* 5626 */       this.jj_scanpos = xsp;
/* 5627 */       if (jj_scan_token(108)) {
/* 5628 */         this.jj_scanpos = xsp;
/* 5629 */         if (jj_scan_token(109)) {
/* 5630 */           this.jj_scanpos = xsp;
/* 5631 */           if (jj_scan_token(110)) {
/* 5632 */             this.jj_scanpos = xsp;
/* 5633 */             if (jj_scan_token(111)) {
/* 5634 */               this.jj_scanpos = xsp;
/* 5635 */               if (jj_scan_token(112)) {
/* 5636 */                 this.jj_scanpos = xsp;
/* 5637 */                 if (jj_scan_token(113)) {
/* 5638 */                   this.jj_scanpos = xsp;
/* 5639 */                   if (jj_scan_token(114)) return true; 
/*      */                 } 
/*      */               } 
/*      */             } 
/*      */           } 
/*      */         } 
/*      */       } 
/*      */     } 
/* 5647 */     return false;
/*      */   }
/*      */ 
/*      */   
/*      */   private boolean jj_3R_44() {
/* 5652 */     if (jj_3R_53()) return true; 
/* 5653 */     return false;
/*      */   }
/*      */ 
/*      */   
/*      */   private boolean jj_3R_97() {
/* 5658 */     if (jj_scan_token(96)) return true; 
/* 5659 */     return false;
/*      */   }
/*      */ 
/*      */   
/*      */   private boolean jj_3R_116() {
/* 5664 */     if (jj_3R_33()) return true; 
/* 5665 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean jj_3R_36() {
/* 5671 */     Token xsp = this.jj_scanpos;
/* 5672 */     if (jj_3R_44()) {
/* 5673 */       this.jj_scanpos = xsp;
/* 5674 */       if (jj_3R_45()) {
/* 5675 */         this.jj_scanpos = xsp;
/* 5676 */         if (jj_3R_46()) return true; 
/*      */       } 
/*      */     } 
/* 5679 */     return false;
/*      */   }
/*      */ 
/*      */   
/*      */   private boolean jj_3R_96() {
/* 5684 */     if (jj_scan_token(95)) return true; 
/* 5685 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean jj_3R_82() {
/* 5691 */     Token xsp = this.jj_scanpos;
/* 5692 */     if (jj_3R_96()) {
/* 5693 */       this.jj_scanpos = xsp;
/* 5694 */       if (jj_3R_97()) return true; 
/*      */     } 
/* 5696 */     return false;
/*      */   }
/*      */ 
/*      */   
/*      */   private boolean jj_3_10() {
/* 5701 */     if (jj_3R_30()) return true; 
/* 5702 */     if (jj_scan_token(119)) return true; 
/* 5703 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean jj_3_4() {
/* 5709 */     Token xsp = this.jj_scanpos;
/* 5710 */     if (jj_scan_token(151)) {
/* 5711 */       this.jj_scanpos = xsp;
/* 5712 */       if (jj_scan_token(118)) {
/* 5713 */         this.jj_scanpos = xsp;
/* 5714 */         if (jj_scan_token(150)) {
/* 5715 */           this.jj_scanpos = xsp;
/* 5716 */           if (jj_scan_token(117)) {
/* 5717 */             this.jj_scanpos = xsp;
/* 5718 */             if (jj_scan_token(116)) {
/* 5719 */               this.jj_scanpos = xsp;
/* 5720 */               if (jj_scan_token(116)) {
/* 5721 */                 this.jj_scanpos = xsp;
/* 5722 */                 if (jj_scan_token(115)) return true; 
/*      */               } 
/*      */             } 
/*      */           } 
/*      */         } 
/*      */       } 
/*      */     } 
/* 5729 */     return false;
/*      */   }
/*      */ 
/*      */   
/*      */   private boolean jj_3R_115() {
/* 5734 */     if (jj_3R_30()) return true; 
/* 5735 */     if (jj_scan_token(119)) return true; 
/* 5736 */     if (jj_3R_33()) return true; 
/* 5737 */     return false;
/*      */   }
/*      */ 
/*      */   
/*      */   private boolean jj_3R_84() {
/* 5742 */     if (jj_scan_token(135)) return true; 
/* 5743 */     if (jj_3R_29()) return true; 
/* 5744 */     if (jj_scan_token(136)) return true; 
/* 5745 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean jj_3R_63() {
/* 5751 */     Token xsp = this.jj_scanpos;
/* 5752 */     if (jj_scan_token(151)) {
/* 5753 */       this.jj_scanpos = xsp;
/* 5754 */       if (jj_scan_token(118)) {
/* 5755 */         this.jj_scanpos = xsp;
/* 5756 */         if (jj_scan_token(150)) {
/* 5757 */           this.jj_scanpos = xsp;
/* 5758 */           if (jj_scan_token(117)) {
/* 5759 */             this.jj_scanpos = xsp;
/* 5760 */             if (jj_scan_token(116)) {
/* 5761 */               this.jj_scanpos = xsp;
/* 5762 */               if (jj_scan_token(115)) return true; 
/*      */             } 
/*      */           } 
/*      */         } 
/*      */       } 
/*      */     } 
/* 5768 */     if (jj_3R_62()) return true; 
/* 5769 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean jj_3R_113() {
/* 5775 */     Token xsp = this.jj_scanpos;
/* 5776 */     if (jj_3R_115()) {
/* 5777 */       this.jj_scanpos = xsp;
/* 5778 */       if (jj_3R_116()) return true; 
/*      */     } 
/* 5780 */     return false;
/*      */   }
/*      */ 
/*      */   
/*      */   private boolean jj_3R_85() {
/* 5785 */     if (jj_scan_token(99)) return true; 
/* 5786 */     if (jj_scan_token(142)) return true; 
/* 5787 */     return false;
/*      */   }
/*      */ 
/*      */   
/*      */   private boolean jj_3R_56() {
/* 5792 */     if (jj_3R_62()) return true;
/*      */     
/* 5794 */     Token xsp = this.jj_scanpos;
/* 5795 */     if (jj_3R_63()) this.jj_scanpos = xsp; 
/* 5796 */     return false;
/*      */   }
/*      */ 
/*      */   
/*      */   private boolean jj_3R_71() {
/* 5801 */     if (jj_3R_85()) return true; 
/* 5802 */     return false;
/*      */   }
/*      */ 
/*      */   
/*      */   private boolean jj_3R_70() {
/* 5807 */     if (jj_3R_84()) return true; 
/* 5808 */     return false;
/*      */   }
/*      */ 
/*      */   
/*      */   private boolean jj_3R_69() {
/* 5813 */     if (jj_3R_43()) return true; 
/* 5814 */     return false;
/*      */   }
/*      */ 
/*      */   
/*      */   private boolean jj_3_9() {
/* 5819 */     if (jj_scan_token(135)) return true; 
/* 5820 */     return false;
/*      */   }
/*      */ 
/*      */   
/*      */   private boolean jj_3R_68() {
/* 5825 */     if (jj_3R_83()) return true; 
/* 5826 */     return false;
/*      */   }
/*      */ 
/*      */   
/*      */   private boolean jj_3R_67() {
/* 5831 */     if (jj_3R_82()) return true; 
/* 5832 */     return false;
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
/* 5846 */   private final int[] jj_la1 = new int[116]; private static int[] jj_la1_0;
/*      */   private static int[] jj_la1_1;
/*      */   private static int[] jj_la1_2;
/*      */   private static int[] jj_la1_3;
/*      */   private static int[] jj_la1_4;
/*      */   
/*      */   static {
/* 5853 */     jj_la1_init_0();
/* 5854 */     jj_la1_init_1();
/* 5855 */     jj_la1_init_2();
/* 5856 */     jj_la1_init_3();
/* 5857 */     jj_la1_init_4();
/*      */   }
/*      */   private static void jj_la1_init_0() {
/* 5860 */     jj_la1_0 = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 512, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 67108864, 33554432, 0, 0, 458752, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 6291456, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 32768, 0, 32768, 32768, 0, -33472, 0, 0, 0, 0, -33472, -33472, -33472, -33472, -33472, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
/*      */   }
/*      */   private static void jj_la1_init_1() {
/* 5863 */     jj_la1_1 = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 4194304, 768, 0, 0, 4194304, 0, 128, 0, 0, 0, 0, 33554432, 67108864, 0, -268435456, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 14336, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 49152, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 6, 0, 0, 0, 6, 0, 0, 0, 0, 0, -8388593, 0, 0, 0, 0, -8388593, -8388593, -8388593, -8388593, -8388593, 0, 0, 6, 6, 0, 0, 0, 0, 0, 0, 0, 0 };
/*      */   }
/*      */   private static void jj_la1_init_2() {
/* 5866 */     jj_la1_2 = new int[] { 0, 0, -536870912, -536870912, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1610612736, 0, -536870912, 0, 0, 0, Integer.MIN_VALUE, Integer.MIN_VALUE, 1610612736, Integer.MIN_VALUE, 0, 0, 0, -536870912, 1310720, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -536870912, 0, 24, 0, 0, 6, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -536870912, 0, -536870912, -536870912, 0, -536870912, 0, 0, 0, 0, 32768, 1, 32768, 1, 1, 0, 1406, 229376, 229376, 0, 0, 2065790, 2065790, 2065790, 2065790, 2065790, 2064384, 2064384, 32768, 32768, 32768, 0, 12288, 0, 0, 2064384, 229376, 2064384 };
/*      */   }
/*      */   private static void jj_la1_init_3() {
/* 5869 */     jj_la1_3 = new int[] { 392, 392, 15, 50331663, 0, 50331648, 50331648, 1677721600, 3584, 7864320, 96, 112, 112, 6, 0, 0, 50331663, 0, 0, 0, 7864321, 209190913, 0, 1, 0, 0, 0, 50331663, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 50331663, 0, 0, 0, 0, 0, 0, 0, 127488, 393216, 520704, 0, 127488, 393216, 520704, 0, 0, 0, 520704, 0, 0, 0, 0, 0, 268435456, 512, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 50331663, 0, 50331663, 50331663, 0, 50331663, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
/*      */   }
/*      */   private static void jj_la1_init_4() {
/* 5872 */     jj_la1_4 = new int[] { 33554594, 33554594, 17056, 17058, 2, 0, 0, 0, 0, 12582912, 0, 0, 0, 0, 16384, 33554434, 17058, 4, 16384, 16512, 14336, 30720, 0, 0, 20, 4, 20, 17058, 0, 8, 0, 0, 0, 4, 4096, 0, 4, 0, 8192, 17058, 8192, 0, 0, 0, 0, 0, 0, 0, 0, 0, 4, 0, 0, 0, 2048, 2048, 0, 1050624, 8, 16384, 0, 128, 16384, 0, 0, 4, 256, 0, 16777216, 16777216, 16777220, 16777216, 16777216, 16793600, 8, 3145728, 256, 16384, 17062, 4, 17058, 17062, 4, 17058, 0, 8, 16384, 3145728, 0, 0, 0, 0, 0, 3145728, 0, 0, 0, 1006632960, 1006632960, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 16384, 0, 4, 16384, 0, 0, 0 };
/*      */   }
/* 5874 */   private final JJCalls[] jj_2_rtns = new JJCalls[17];
/*      */   private boolean jj_rescan = false;
/* 5876 */   private int jj_gc = 0; private final LookaheadSuccess jj_ls; private List<int[]> jj_expentries; private int[] jj_expentry; private int jj_kind; private int[] jj_lasttokens;
/*      */   private int jj_endpos;
/*      */   
/*      */   public FMParser(InputStream stream) {
/* 5880 */     this(stream, null);
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
/*      */   public void ReInit(InputStream stream) {
/* 5895 */     ReInit(stream, null);
/*      */   }
/*      */   public void ReInit(InputStream stream, String encoding) {
/*      */     
/* 5899 */     try { this.jj_input_stream.ReInit(stream, encoding, 1, 1); } catch (UnsupportedEncodingException e) { throw new RuntimeException(e); }
/* 5900 */      this.token_source.ReInit(this.jj_input_stream);
/* 5901 */     this.token = new Token();
/* 5902 */     this.jj_ntk = -1;
/* 5903 */     this.jj_gen = 0; int i;
/* 5904 */     for (i = 0; i < 116; ) { this.jj_la1[i] = -1; i++; }
/* 5905 */      for (i = 0; i < this.jj_2_rtns.length; ) { this.jj_2_rtns[i] = new JJCalls(); i++; }
/*      */   
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
/*      */   public void ReInit(Reader stream) {
/* 5921 */     if (this.jj_input_stream == null) {
/* 5922 */       this.jj_input_stream = new SimpleCharStream(stream, 1, 1);
/*      */     } else {
/* 5924 */       this.jj_input_stream.ReInit(stream, 1, 1);
/*      */     } 
/* 5926 */     if (this.token_source == null) {
/* 5927 */       this.token_source = new FMParserTokenManager(this.jj_input_stream);
/*      */     }
/*      */     
/* 5930 */     this.token_source.ReInit(this.jj_input_stream);
/* 5931 */     this.token = new Token();
/* 5932 */     this.jj_ntk = -1;
/* 5933 */     this.jj_gen = 0; int i;
/* 5934 */     for (i = 0; i < 116; ) { this.jj_la1[i] = -1; i++; }
/* 5935 */      for (i = 0; i < this.jj_2_rtns.length; ) { this.jj_2_rtns[i] = new JJCalls(); i++; }
/*      */   
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
/*      */   public void ReInit(FMParserTokenManager tm) {
/* 5950 */     this.token_source = tm;
/* 5951 */     this.token = new Token();
/* 5952 */     this.jj_ntk = -1;
/* 5953 */     this.jj_gen = 0; int i;
/* 5954 */     for (i = 0; i < 116; ) { this.jj_la1[i] = -1; i++; }
/* 5955 */      for (i = 0; i < this.jj_2_rtns.length; ) { this.jj_2_rtns[i] = new JJCalls(); i++; }
/*      */   
/*      */   }
/*      */   private Token jj_consume_token(int kind) throws ParseException {
/*      */     Token oldToken;
/* 5960 */     if ((oldToken = this.token).next != null) { this.token = this.token.next; }
/* 5961 */     else { this.token = this.token.next = this.token_source.getNextToken(); }
/* 5962 */      this.jj_ntk = -1;
/* 5963 */     if (this.token.kind == kind) {
/* 5964 */       this.jj_gen++;
/* 5965 */       if (++this.jj_gc > 100) {
/* 5966 */         this.jj_gc = 0;
/* 5967 */         for (int i = 0; i < this.jj_2_rtns.length; i++) {
/* 5968 */           JJCalls c = this.jj_2_rtns[i];
/* 5969 */           while (c != null) {
/* 5970 */             if (c.gen < this.jj_gen) c.first = null; 
/* 5971 */             c = c.next;
/*      */           } 
/*      */         } 
/*      */       } 
/* 5975 */       return this.token;
/*      */     } 
/* 5977 */     this.token = oldToken;
/* 5978 */     this.jj_kind = kind;
/* 5979 */     throw generateParseException();
/*      */   }
/*      */   private static final class LookaheadSuccess extends Error {
/*      */     private LookaheadSuccess() {} }
/*      */   
/* 5984 */   public FMParser(InputStream stream, String encoding) { this.jj_ls = new LookaheadSuccess();
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
/* 6033 */     this.jj_expentries = (List)new ArrayList<>();
/*      */     
/* 6035 */     this.jj_kind = -1;
/* 6036 */     this.jj_lasttokens = new int[100]; try { this.jj_input_stream = new SimpleCharStream(stream, encoding, 1, 1); } catch (UnsupportedEncodingException e) { throw new RuntimeException(e); }  this.token_source = new FMParserTokenManager(this.jj_input_stream); this.token = new Token(); this.jj_ntk = -1; this.jj_gen = 0; int i; for (i = 0; i < 116; ) { this.jj_la1[i] = -1; i++; }  for (i = 0; i < this.jj_2_rtns.length; ) { this.jj_2_rtns[i] = new JJCalls(); i++; }  } public FMParser(Reader stream) { this.jj_ls = new LookaheadSuccess(); this.jj_expentries = (List)new ArrayList<>(); this.jj_kind = -1; this.jj_lasttokens = new int[100]; this.jj_input_stream = new SimpleCharStream(stream, 1, 1); this.token_source = new FMParserTokenManager(this.jj_input_stream); this.token = new Token(); this.jj_ntk = -1; this.jj_gen = 0; int i; for (i = 0; i < 116; ) { this.jj_la1[i] = -1; i++; }  for (i = 0; i < this.jj_2_rtns.length; ) { this.jj_2_rtns[i] = new JJCalls(); i++; }  } public FMParser(FMParserTokenManager tm) { this.jj_ls = new LookaheadSuccess(); this.jj_expentries = (List)new ArrayList<>(); this.jj_kind = -1; this.jj_lasttokens = new int[100]; this.token_source = tm; this.token = new Token(); this.jj_ntk = -1; this.jj_gen = 0; int i; for (i = 0; i < 116; ) { this.jj_la1[i] = -1; i++; }  for (i = 0; i < this.jj_2_rtns.length; ) { this.jj_2_rtns[i] = new JJCalls(); i++; }  }
/*      */   private boolean jj_scan_token(int kind) { if (this.jj_scanpos == this.jj_lastpos) { this.jj_la--; if (this.jj_scanpos.next == null) { this.jj_lastpos = this.jj_scanpos = this.jj_scanpos.next = this.token_source.getNextToken(); } else { this.jj_lastpos = this.jj_scanpos = this.jj_scanpos.next; }  } else { this.jj_scanpos = this.jj_scanpos.next; }  if (this.jj_rescan) { int i = 0; Token tok = this.token; while (tok != null && tok != this.jj_scanpos) { i++; tok = tok.next; }  if (tok != null)
/*      */         jj_add_error_token(kind, i);  }  if (this.jj_scanpos.kind != kind)
/*      */       return true;  if (this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos)
/* 6040 */       throw this.jj_ls;  return false; } private void jj_add_error_token(int kind, int pos) { if (pos >= 100) {
/*      */       return;
/*      */     }
/*      */     
/* 6044 */     if (pos == this.jj_endpos + 1)
/* 6045 */     { this.jj_lasttokens[this.jj_endpos++] = kind; }
/* 6046 */     else if (this.jj_endpos != 0)
/* 6047 */     { this.jj_expentry = new int[this.jj_endpos];
/*      */       
/* 6049 */       for (int i = 0; i < this.jj_endpos; i++) {
/* 6050 */         this.jj_expentry[i] = this.jj_lasttokens[i];
/*      */       }
/*      */       
/* 6053 */       for (int[] oldentry : this.jj_expentries) {
/* 6054 */         if (oldentry.length == this.jj_expentry.length) {
/* 6055 */           boolean isMatched = true;
/*      */           
/* 6057 */           for (int j = 0; j < this.jj_expentry.length; j++) {
/* 6058 */             if (oldentry[j] != this.jj_expentry[j]) {
/* 6059 */               isMatched = false;
/*      */               
/*      */               break;
/*      */             } 
/*      */           } 
/* 6064 */           if (isMatched) {
/* 6065 */             this.jj_expentries.add(this.jj_expentry);
/*      */             
/*      */             break;
/*      */           } 
/*      */         } 
/*      */       } 
/* 6071 */       if (pos != 0)
/* 6072 */         this.jj_lasttokens[(this.jj_endpos = pos) - 1] = kind;  }  }
/*      */   public final Token getNextToken() { if (this.token.next != null) { this.token = this.token.next; } else { this.token = this.token.next = this.token_source.getNextToken(); }  this.jj_ntk = -1; this.jj_gen++; return this.token; }
/*      */   public final Token getToken(int index) { Token t = this.token; for (int i = 0; i < index; i++) { if (t.next != null) { t = t.next; }
/*      */       else { t = t.next = this.token_source.getNextToken(); }
/*      */        }
/*      */      return t; }
/*      */   private int jj_ntk_f() { if ((this.jj_nt = this.token.next) == null)
/* 6079 */       return this.jj_ntk = (this.token.next = this.token_source.getNextToken()).kind;  return this.jj_ntk = this.jj_nt.kind; } public ParseException generateParseException() { this.jj_expentries.clear();
/* 6080 */     boolean[] la1tokens = new boolean[158];
/* 6081 */     if (this.jj_kind >= 0) {
/* 6082 */       la1tokens[this.jj_kind] = true;
/* 6083 */       this.jj_kind = -1;
/*      */     }  int i;
/* 6085 */     for (i = 0; i < 116; i++) {
/* 6086 */       if (this.jj_la1[i] == this.jj_gen) {
/* 6087 */         for (int k = 0; k < 32; k++) {
/* 6088 */           if ((jj_la1_0[i] & 1 << k) != 0) {
/* 6089 */             la1tokens[k] = true;
/*      */           }
/* 6091 */           if ((jj_la1_1[i] & 1 << k) != 0) {
/* 6092 */             la1tokens[32 + k] = true;
/*      */           }
/* 6094 */           if ((jj_la1_2[i] & 1 << k) != 0) {
/* 6095 */             la1tokens[64 + k] = true;
/*      */           }
/* 6097 */           if ((jj_la1_3[i] & 1 << k) != 0) {
/* 6098 */             la1tokens[96 + k] = true;
/*      */           }
/* 6100 */           if ((jj_la1_4[i] & 1 << k) != 0) {
/* 6101 */             la1tokens[128 + k] = true;
/*      */           }
/*      */         } 
/*      */       }
/*      */     } 
/* 6106 */     for (i = 0; i < 158; i++) {
/* 6107 */       if (la1tokens[i]) {
/* 6108 */         this.jj_expentry = new int[1];
/* 6109 */         this.jj_expentry[0] = i;
/* 6110 */         this.jj_expentries.add(this.jj_expentry);
/*      */       } 
/*      */     } 
/* 6113 */     this.jj_endpos = 0;
/* 6114 */     jj_rescan_token();
/* 6115 */     jj_add_error_token(0, 0);
/* 6116 */     int[][] exptokseq = new int[this.jj_expentries.size()][];
/* 6117 */     for (int j = 0; j < this.jj_expentries.size(); j++) {
/* 6118 */       exptokseq[j] = this.jj_expentries.get(j);
/*      */     }
/* 6120 */     return new ParseException(this.token, exptokseq, tokenImage); }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final void enable_tracing() {}
/*      */ 
/*      */   
/*      */   public final void disable_tracing() {}
/*      */ 
/*      */   
/*      */   private void jj_rescan_token() {
/* 6132 */     this.jj_rescan = true;
/* 6133 */     for (int i = 0; i < 17; i++) {
/*      */       try {
/* 6135 */         JJCalls p = this.jj_2_rtns[i];
/*      */         
/*      */         do {
/* 6138 */           if (p.gen > this.jj_gen) {
/* 6139 */             this.jj_la = p.arg; this.jj_lastpos = this.jj_scanpos = p.first;
/* 6140 */             switch (i) { case 0:
/* 6141 */                 jj_3_1(); break;
/* 6142 */               case 1: jj_3_2(); break;
/* 6143 */               case 2: jj_3_3(); break;
/* 6144 */               case 3: jj_3_4(); break;
/* 6145 */               case 4: jj_3_5(); break;
/* 6146 */               case 5: jj_3_6(); break;
/* 6147 */               case 6: jj_3_7(); break;
/* 6148 */               case 7: jj_3_8(); break;
/* 6149 */               case 8: jj_3_9(); break;
/* 6150 */               case 9: jj_3_10(); break;
/* 6151 */               case 10: jj_3_11(); break;
/* 6152 */               case 11: jj_3_12(); break;
/* 6153 */               case 12: jj_3_13(); break;
/* 6154 */               case 13: jj_3_14(); break;
/* 6155 */               case 14: jj_3_15(); break;
/* 6156 */               case 15: jj_3_16(); break;
/* 6157 */               case 16: jj_3_17(); break; }
/*      */           
/*      */           } 
/* 6160 */           p = p.next;
/* 6161 */         } while (p != null);
/*      */       }
/* 6163 */       catch (LookaheadSuccess lookaheadSuccess) {}
/*      */     } 
/* 6165 */     this.jj_rescan = false;
/*      */   }
/*      */   
/*      */   private void jj_save(int index, int xla) {
/* 6169 */     JJCalls p = this.jj_2_rtns[index];
/* 6170 */     while (p.gen > this.jj_gen) {
/* 6171 */       if (p.next == null) { p = p.next = new JJCalls(); break; }
/* 6172 */        p = p.next;
/*      */     } 
/*      */     
/* 6175 */     p.gen = this.jj_gen + xla - this.jj_la;
/* 6176 */     p.first = this.token;
/* 6177 */     p.arg = xla;
/*      */   }
/*      */   
/*      */   static final class JJCalls {
/*      */     int gen;
/*      */     Token first;
/*      */     int arg;
/*      */     JJCalls next;
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\FMParser.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */