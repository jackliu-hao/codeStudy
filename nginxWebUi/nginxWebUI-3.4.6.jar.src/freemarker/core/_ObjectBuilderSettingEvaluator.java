/*      */ package freemarker.core;
/*      */ 
/*      */ import freemarker.cache.AndMatcher;
/*      */ import freemarker.cache.ConditionalTemplateConfigurationFactory;
/*      */ import freemarker.cache.FileExtensionMatcher;
/*      */ import freemarker.cache.FileNameGlobMatcher;
/*      */ import freemarker.cache.FirstMatchTemplateConfigurationFactory;
/*      */ import freemarker.cache.MergingTemplateConfigurationFactory;
/*      */ import freemarker.cache.NotMatcher;
/*      */ import freemarker.cache.OrMatcher;
/*      */ import freemarker.cache.PathGlobMatcher;
/*      */ import freemarker.cache.PathRegexMatcher;
/*      */ import freemarker.ext.beans.BeansWrapper;
/*      */ import freemarker.template.Configuration;
/*      */ import freemarker.template.DefaultObjectWrapper;
/*      */ import freemarker.template.SimpleObjectWrapper;
/*      */ import freemarker.template.TemplateHashModel;
/*      */ import freemarker.template.TemplateMethodModelEx;
/*      */ import freemarker.template.TemplateModel;
/*      */ import freemarker.template.TemplateModelException;
/*      */ import freemarker.template.Version;
/*      */ import freemarker.template.utility.ClassUtil;
/*      */ import freemarker.template.utility.StringUtil;
/*      */ import freemarker.template.utility.WriteProtectable;
/*      */ import java.beans.Introspector;
/*      */ import java.beans.PropertyDescriptor;
/*      */ import java.lang.reflect.Field;
/*      */ import java.lang.reflect.InvocationTargetException;
/*      */ import java.lang.reflect.Method;
/*      */ import java.math.BigDecimal;
/*      */ import java.math.BigInteger;
/*      */ import java.util.ArrayList;
/*      */ import java.util.HashMap;
/*      */ import java.util.LinkedHashMap;
/*      */ import java.util.List;
/*      */ import java.util.Locale;
/*      */ import java.util.Map;
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
/*      */ public class _ObjectBuilderSettingEvaluator
/*      */ {
/*      */   private static final String INSTANCE_FIELD_NAME = "INSTANCE";
/*      */   private static final String BUILD_METHOD_NAME = "build";
/*      */   private static final String BUILDER_CLASS_POSTFIX = "Builder";
/*      */   private static Map<String, String> SHORTHANDS;
/*   81 */   private static final Object VOID = new Object();
/*      */   
/*      */   private final String src;
/*      */   
/*      */   private final Class expectedClass;
/*      */   
/*      */   private final boolean allowNull;
/*      */   
/*      */   private final _SettingEvaluationEnvironment env;
/*      */   
/*      */   private int pos;
/*      */   
/*      */   private boolean modernMode = false;
/*      */   
/*      */   private _ObjectBuilderSettingEvaluator(String src, int pos, Class expectedClass, boolean allowNull, _SettingEvaluationEnvironment env) {
/*   96 */     this.src = src;
/*   97 */     this.pos = pos;
/*   98 */     this.expectedClass = expectedClass;
/*   99 */     this.allowNull = allowNull;
/*  100 */     this.env = env;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public static Object eval(String src, Class expectedClass, boolean allowNull, _SettingEvaluationEnvironment env) throws _ObjectBuilderSettingEvaluationException, ClassNotFoundException, InstantiationException, IllegalAccessException {
/*  106 */     return (new _ObjectBuilderSettingEvaluator(src, 0, expectedClass, allowNull, env)).eval();
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
/*      */   public static int configureBean(String argumentListSrc, int posAfterOpenParen, Object bean, _SettingEvaluationEnvironment env) throws _ObjectBuilderSettingEvaluationException, ClassNotFoundException, InstantiationException, IllegalAccessException {
/*  119 */     return (new _ObjectBuilderSettingEvaluator(argumentListSrc, posAfterOpenParen, bean
/*  120 */         .getClass(), true, env)).configureBean(bean);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private Object eval() throws _ObjectBuilderSettingEvaluationException, ClassNotFoundException, InstantiationException, IllegalAccessException {
/*      */     Object value;
/*  127 */     skipWS();
/*      */     try {
/*  129 */       value = ensureEvaled(fetchValue(false, true, false, true));
/*  130 */     } catch (LegacyExceptionWrapperSettingEvaluationExpression e) {
/*  131 */       e.rethrowLegacy();
/*  132 */       value = null;
/*      */     } 
/*  134 */     skipWS();
/*      */     
/*  136 */     if (this.pos != this.src.length()) {
/*  137 */       throw new _ObjectBuilderSettingEvaluationException("end-of-expression", this.src, this.pos);
/*      */     }
/*      */     
/*  140 */     if (value == null && !this.allowNull) {
/*  141 */       throw new _ObjectBuilderSettingEvaluationException("Value can't be null.");
/*      */     }
/*  143 */     if (value != null && !this.expectedClass.isInstance(value)) {
/*  144 */       throw new _ObjectBuilderSettingEvaluationException("The resulting object (of class " + value
/*  145 */           .getClass() + ") is not a(n) " + this.expectedClass.getName() + ".");
/*      */     }
/*      */     
/*  148 */     return value;
/*      */   }
/*      */ 
/*      */   
/*      */   private int configureBean(Object bean) throws _ObjectBuilderSettingEvaluationException, ClassNotFoundException, InstantiationException, IllegalAccessException {
/*  153 */     PropertyAssignmentsExpression propAssignments = new PropertyAssignmentsExpression(bean);
/*  154 */     fetchParameterListInto(propAssignments);
/*  155 */     skipWS();
/*  156 */     propAssignments.eval();
/*  157 */     return this.pos;
/*      */   }
/*      */   
/*      */   private Object ensureEvaled(Object value) throws _ObjectBuilderSettingEvaluationException {
/*  161 */     return (value instanceof SettingExpression) ? ((SettingExpression)value).eval() : value;
/*      */   }
/*      */ 
/*      */   
/*      */   private Object fetchBuilderCall(boolean optional, boolean topLevel) throws _ObjectBuilderSettingEvaluationException {
/*  166 */     int startPos = this.pos;
/*      */     
/*  168 */     BuilderCallExpression exp = new BuilderCallExpression();
/*      */ 
/*      */     
/*  171 */     exp.canBeStaticField = true;
/*      */     
/*  173 */     String fetchedClassName = fetchClassName(optional);
/*      */     
/*  175 */     if (fetchedClassName == null) {
/*  176 */       if (!optional) {
/*  177 */         throw new _ObjectBuilderSettingEvaluationException("class name", this.src, this.pos);
/*      */       }
/*  179 */       return VOID;
/*      */     } 
/*  181 */     exp.className = shorthandToFullQualified(fetchedClassName);
/*  182 */     if (!fetchedClassName.equals(exp.className)) {
/*      */       
/*  184 */       this.modernMode = true;
/*  185 */       exp.canBeStaticField = false;
/*      */     } 
/*      */ 
/*      */     
/*  189 */     skipWS();
/*      */     
/*  191 */     char openParen = fetchOptionalChar("(");
/*      */     
/*  193 */     if (openParen == '\000' && !topLevel) {
/*  194 */       if (fetchedClassName.indexOf('.') != -1) {
/*  195 */         exp.mustBeStaticField = true;
/*      */       } else {
/*  197 */         this.pos = startPos;
/*  198 */         return VOID;
/*      */       } 
/*      */     }
/*      */     
/*  202 */     if (openParen != '\000') {
/*  203 */       fetchParameterListInto(exp);
/*  204 */       exp.canBeStaticField = false;
/*      */     } 
/*      */     
/*  207 */     return exp;
/*      */   }
/*      */ 
/*      */   
/*      */   private void fetchParameterListInto(ExpressionWithParameters exp) throws _ObjectBuilderSettingEvaluationException {
/*  212 */     this.modernMode = true;
/*      */     
/*  214 */     skipWS();
/*  215 */     if (fetchOptionalChar(")") != ')') {
/*      */       do {
/*  217 */         skipWS();
/*      */         
/*  219 */         Object paramNameOrValue = fetchValue(false, false, true, false);
/*  220 */         if (paramNameOrValue == VOID)
/*  221 */           continue;  skipWS();
/*  222 */         if (paramNameOrValue instanceof Name) {
/*  223 */           exp.namedParamNames.add(((Name)paramNameOrValue).name);
/*      */           
/*  225 */           skipWS();
/*  226 */           fetchRequiredChar("=");
/*  227 */           skipWS();
/*      */           
/*  229 */           Object paramValue = fetchValue(false, false, true, true);
/*  230 */           exp.namedParamValues.add(ensureEvaled(paramValue));
/*      */         } else {
/*  232 */           if (!exp.namedParamNames.isEmpty()) {
/*  233 */             throw new _ObjectBuilderSettingEvaluationException("Positional parameters must precede named parameters");
/*      */           }
/*      */           
/*  236 */           if (!exp.getAllowPositionalParameters()) {
/*  237 */             throw new _ObjectBuilderSettingEvaluationException("Positional parameters not supported here");
/*      */           }
/*      */           
/*  240 */           exp.positionalParamValues.add(ensureEvaled(paramNameOrValue));
/*      */         } 
/*      */         
/*  243 */         skipWS();
/*      */       }
/*  245 */       while (fetchRequiredChar(",)") == ',');
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   private Object fetchValue(boolean optional, boolean topLevel, boolean resultCoerced, boolean resolveVariables) throws _ObjectBuilderSettingEvaluationException {
/*  251 */     if (this.pos < this.src.length()) {
/*  252 */       Object val = fetchNumberLike(true, resultCoerced);
/*  253 */       if (val != VOID) {
/*  254 */         return val;
/*      */       }
/*      */       
/*  257 */       val = fetchStringLiteral(true);
/*  258 */       if (val != VOID) {
/*  259 */         return val;
/*      */       }
/*      */       
/*  262 */       val = fetchListLiteral(true);
/*  263 */       if (val != VOID) {
/*  264 */         return val;
/*      */       }
/*      */       
/*  267 */       val = fetchMapLiteral(true);
/*  268 */       if (val != VOID) {
/*  269 */         return val;
/*      */       }
/*      */       
/*  272 */       val = fetchBuilderCall(true, topLevel);
/*  273 */       if (val != VOID) {
/*  274 */         return val;
/*      */       }
/*      */       
/*  277 */       String name = fetchSimpleName(true);
/*  278 */       if (name != null) {
/*  279 */         val = keywordToValueOrVoid(name);
/*  280 */         if (val != VOID) {
/*  281 */           return val;
/*      */         }
/*      */         
/*  284 */         if (resolveVariables)
/*      */         {
/*  286 */           throw new _ObjectBuilderSettingEvaluationException("Can't resolve variable reference: " + name);
/*      */         }
/*  288 */         return new Name(name);
/*      */       } 
/*      */     } 
/*      */ 
/*      */     
/*  293 */     if (optional) {
/*  294 */       return VOID;
/*      */     }
/*  296 */     throw new _ObjectBuilderSettingEvaluationException("value or name", this.src, this.pos);
/*      */   }
/*      */ 
/*      */   
/*      */   private boolean isKeyword(String name) {
/*  301 */     return (keywordToValueOrVoid(name) != VOID);
/*      */   }
/*      */   
/*      */   private Object keywordToValueOrVoid(String name) {
/*  305 */     if (name.equals("true")) return Boolean.TRUE; 
/*  306 */     if (name.equals("false")) return Boolean.FALSE; 
/*  307 */     if (name.equals("null")) return null; 
/*  308 */     return VOID;
/*      */   }
/*      */   
/*      */   private String fetchSimpleName(boolean optional) throws _ObjectBuilderSettingEvaluationException {
/*  312 */     char c = (this.pos < this.src.length()) ? this.src.charAt(this.pos) : Character.MIN_VALUE;
/*  313 */     if (!isIdentifierStart(c)) {
/*  314 */       if (optional) {
/*  315 */         return null;
/*      */       }
/*  317 */       throw new _ObjectBuilderSettingEvaluationException("class name", this.src, this.pos);
/*      */     } 
/*      */     
/*  320 */     int startPos = this.pos;
/*  321 */     this.pos++;
/*      */ 
/*      */     
/*  324 */     while (this.pos != this.src.length()) {
/*      */ 
/*      */       
/*  327 */       c = this.src.charAt(this.pos);
/*  328 */       if (!isIdentifierMiddle(c)) {
/*      */         break;
/*      */       }
/*  331 */       this.pos++;
/*      */     } 
/*      */     
/*  334 */     return this.src.substring(startPos, this.pos);
/*      */   }
/*      */   
/*      */   private String fetchClassName(boolean optional) throws _ObjectBuilderSettingEvaluationException {
/*  338 */     int startPos = this.pos;
/*  339 */     StringBuilder sb = new StringBuilder();
/*      */     while (true) {
/*  341 */       String name = fetchSimpleName(true);
/*  342 */       if (name == null) {
/*  343 */         if (!optional) {
/*  344 */           throw new _ObjectBuilderSettingEvaluationException("name", this.src, this.pos);
/*      */         }
/*  346 */         this.pos = startPos;
/*  347 */         return null;
/*      */       } 
/*      */       
/*  350 */       sb.append(name);
/*      */       
/*  352 */       skipWS();
/*      */       
/*  354 */       if (this.pos >= this.src.length() || this.src.charAt(this.pos) != '.') {
/*      */         break;
/*      */       }
/*  357 */       sb.append('.');
/*  358 */       this.pos++;
/*      */       
/*  360 */       skipWS();
/*      */     } 
/*      */     
/*  363 */     String className = sb.toString();
/*  364 */     if (isKeyword(className)) {
/*  365 */       this.pos = startPos;
/*  366 */       return null;
/*      */     } 
/*  368 */     return className;
/*      */   }
/*      */ 
/*      */   
/*      */   private Object fetchNumberLike(boolean optional, boolean resultCoerced) throws _ObjectBuilderSettingEvaluationException {
/*  373 */     int startPos = this.pos;
/*  374 */     boolean isVersion = false;
/*  375 */     boolean hasDot = false;
/*      */     
/*  377 */     while (this.pos != this.src.length()) {
/*      */ 
/*      */       
/*  380 */       char c = this.src.charAt(this.pos);
/*  381 */       if (c == '.') {
/*  382 */         if (hasDot) {
/*      */           
/*  384 */           isVersion = true;
/*      */         } else {
/*  386 */           hasDot = true;
/*      */         } 
/*  388 */       } else if (!isASCIIDigit(c) && c != '-') {
/*      */         break;
/*      */       } 
/*  391 */       this.pos++;
/*      */     } 
/*      */     
/*  394 */     if (startPos == this.pos) {
/*  395 */       if (optional) {
/*  396 */         return VOID;
/*      */       }
/*  398 */       throw new _ObjectBuilderSettingEvaluationException("number-like", this.src, this.pos);
/*      */     } 
/*      */ 
/*      */     
/*  402 */     String numStr = this.src.substring(startPos, this.pos);
/*  403 */     if (isVersion) {
/*      */       try {
/*  405 */         return new Version(numStr);
/*  406 */       } catch (IllegalArgumentException e) {
/*  407 */         throw new _ObjectBuilderSettingEvaluationException("Malformed version number: " + numStr, e);
/*      */       } 
/*      */     }
/*      */     
/*  411 */     String typePostfix = null;
/*      */     
/*  413 */     while (this.pos != this.src.length()) {
/*      */ 
/*      */       
/*  416 */       char c = this.src.charAt(this.pos);
/*  417 */       if (Character.isLetter(c)) {
/*  418 */         if (typePostfix == null) {
/*  419 */           typePostfix = String.valueOf(c);
/*      */         } else {
/*  421 */           typePostfix = typePostfix + c;
/*      */         } 
/*      */ 
/*      */ 
/*      */         
/*  426 */         this.pos++; continue;
/*      */       }  break;
/*      */     } 
/*      */     try {
/*  430 */       if (numStr.endsWith(".")) {
/*  431 */         throw new NumberFormatException("A number can't end with a dot");
/*      */       }
/*  433 */       if (numStr.startsWith(".") || numStr.startsWith("-.") || numStr.startsWith("+.")) {
/*  434 */         throw new NumberFormatException("A number can't start with a dot");
/*      */       }
/*      */       
/*  437 */       if (typePostfix == null) {
/*      */         
/*  439 */         if (numStr.indexOf('.') == -1) {
/*  440 */           BigInteger biNum = new BigInteger(numStr);
/*  441 */           int bitLength = biNum.bitLength();
/*  442 */           if (bitLength <= 31)
/*  443 */             return Integer.valueOf(biNum.intValue()); 
/*  444 */           if (bitLength <= 63) {
/*  445 */             return Long.valueOf(biNum.longValue());
/*      */           }
/*  447 */           return biNum;
/*      */         } 
/*      */         
/*  450 */         if (resultCoerced)
/*      */         {
/*  452 */           return new BigDecimal(numStr);
/*      */         }
/*      */         
/*  455 */         return Double.valueOf(numStr);
/*      */       } 
/*      */ 
/*      */       
/*  459 */       if (typePostfix.equalsIgnoreCase("l"))
/*  460 */         return Long.valueOf(numStr); 
/*  461 */       if (typePostfix.equalsIgnoreCase("bi"))
/*  462 */         return new BigInteger(numStr); 
/*  463 */       if (typePostfix.equalsIgnoreCase("bd"))
/*  464 */         return new BigDecimal(numStr); 
/*  465 */       if (typePostfix.equalsIgnoreCase("d"))
/*  466 */         return Double.valueOf(numStr); 
/*  467 */       if (typePostfix.equalsIgnoreCase("f")) {
/*  468 */         return Float.valueOf(numStr);
/*      */       }
/*  470 */       throw new _ObjectBuilderSettingEvaluationException("Unrecognized number type postfix: " + typePostfix);
/*      */ 
/*      */ 
/*      */     
/*      */     }
/*  475 */     catch (NumberFormatException e) {
/*  476 */       throw new _ObjectBuilderSettingEvaluationException("Malformed number: " + numStr, e);
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   private Object fetchStringLiteral(boolean optional) throws _ObjectBuilderSettingEvaluationException {
/*  482 */     int startPos = this.pos;
/*  483 */     char q = Character.MIN_VALUE;
/*  484 */     boolean afterEscape = false;
/*  485 */     boolean raw = false;
/*      */     while (true) {
/*  487 */       if (this.pos == this.src.length()) {
/*  488 */         if (q != '\000')
/*      */         {
/*  490 */           throw new _ObjectBuilderSettingEvaluationException(String.valueOf(q), this.src, this.pos);
/*      */         }
/*      */         break;
/*      */       } 
/*  494 */       char c = this.src.charAt(this.pos);
/*  495 */       if (q == '\000') {
/*  496 */         if (c == 'r' && this.pos + 1 < this.src.length()) {
/*      */           
/*  498 */           raw = true;
/*  499 */           c = this.src.charAt(this.pos + 1);
/*      */         } 
/*  501 */         if (c == '\'') {
/*  502 */           q = '\'';
/*  503 */         } else if (c == '"') {
/*  504 */           q = '"';
/*      */         } else {
/*      */           break;
/*      */         } 
/*  508 */         if (raw)
/*      */         {
/*  510 */           this.pos++;
/*      */         }
/*      */       }
/*  513 */       else if (!afterEscape) {
/*  514 */         if (c == '\\' && !raw)
/*  515 */         { afterEscape = true; }
/*  516 */         else { if (c == q)
/*      */             break; 
/*  518 */           if (c == '{') {
/*  519 */             char prevC = this.src.charAt(this.pos - 1);
/*  520 */             if (prevC == '$' || prevC == '#') {
/*  521 */               throw new _ObjectBuilderSettingEvaluationException("${...} and #{...} aren't allowed here.");
/*      */             }
/*      */           }  }
/*      */       
/*      */       } else {
/*  526 */         afterEscape = false;
/*      */       } 
/*      */       
/*  529 */       this.pos++;
/*      */     } 
/*  531 */     if (startPos == this.pos) {
/*  532 */       if (optional) {
/*  533 */         return VOID;
/*      */       }
/*  535 */       throw new _ObjectBuilderSettingEvaluationException("string literal", this.src, this.pos);
/*      */     } 
/*      */ 
/*      */     
/*  539 */     String sInside = this.src.substring(startPos + (raw ? 2 : 1), this.pos);
/*      */     try {
/*  541 */       this.pos++;
/*  542 */       return raw ? sInside : StringUtil.FTLStringLiteralDec(sInside);
/*  543 */     } catch (ParseException e) {
/*  544 */       throw new _ObjectBuilderSettingEvaluationException("Malformed string literal: " + sInside, e);
/*      */     } 
/*      */   }
/*      */   
/*      */   private Object fetchListLiteral(boolean optional) throws _ObjectBuilderSettingEvaluationException {
/*  549 */     if (this.pos == this.src.length() || this.src.charAt(this.pos) != '[') {
/*  550 */       if (!optional) {
/*  551 */         throw new _ObjectBuilderSettingEvaluationException("[", this.src, this.pos);
/*      */       }
/*  553 */       return VOID;
/*      */     } 
/*  555 */     this.pos++;
/*      */     
/*  557 */     ListExpression listExp = new ListExpression();
/*      */     
/*      */     while (true) {
/*  560 */       skipWS();
/*      */       
/*  562 */       if (fetchOptionalChar("]") != '\000') {
/*  563 */         return listExp;
/*      */       }
/*  565 */       if (listExp.itemCount() != 0) {
/*  566 */         fetchRequiredChar(",");
/*  567 */         skipWS();
/*      */       } 
/*      */       
/*  570 */       listExp.addItem(fetchValue(false, false, false, true));
/*      */       
/*  572 */       skipWS();
/*      */     } 
/*      */   }
/*      */   
/*      */   private Object fetchMapLiteral(boolean optional) throws _ObjectBuilderSettingEvaluationException {
/*  577 */     if (this.pos == this.src.length() || this.src.charAt(this.pos) != '{') {
/*  578 */       if (!optional) {
/*  579 */         throw new _ObjectBuilderSettingEvaluationException("{", this.src, this.pos);
/*      */       }
/*  581 */       return VOID;
/*      */     } 
/*  583 */     this.pos++;
/*      */     
/*  585 */     MapExpression mapExp = new MapExpression();
/*      */     
/*      */     while (true) {
/*  588 */       skipWS();
/*      */       
/*  590 */       if (fetchOptionalChar("}") != '\000') {
/*  591 */         return mapExp;
/*      */       }
/*  593 */       if (mapExp.itemCount() != 0) {
/*  594 */         fetchRequiredChar(",");
/*  595 */         skipWS();
/*      */       } 
/*      */       
/*  598 */       Object key = fetchValue(false, false, false, true);
/*  599 */       skipWS();
/*  600 */       fetchRequiredChar(":");
/*  601 */       skipWS();
/*  602 */       Object value = fetchValue(false, false, false, true);
/*  603 */       mapExp.addItem(new KeyValuePair(key, value));
/*      */       
/*  605 */       skipWS();
/*      */     } 
/*      */   }
/*      */   
/*      */   private void skipWS() {
/*      */     while (true) {
/*  611 */       if (this.pos == this.src.length()) {
/*      */         return;
/*      */       }
/*  614 */       char c = this.src.charAt(this.pos);
/*  615 */       if (!Character.isWhitespace(c)) {
/*      */         return;
/*      */       }
/*  618 */       this.pos++;
/*      */     } 
/*      */   }
/*      */   
/*      */   private char fetchOptionalChar(String expectedChars) throws _ObjectBuilderSettingEvaluationException {
/*  623 */     return fetchChar(expectedChars, true);
/*      */   }
/*      */   
/*      */   private char fetchRequiredChar(String expectedChars) throws _ObjectBuilderSettingEvaluationException {
/*  627 */     return fetchChar(expectedChars, false);
/*      */   }
/*      */   
/*      */   private char fetchChar(String expectedChars, boolean optional) throws _ObjectBuilderSettingEvaluationException {
/*  631 */     char c = (this.pos < this.src.length()) ? this.src.charAt(this.pos) : Character.MIN_VALUE;
/*  632 */     if (expectedChars.indexOf(c) != -1) {
/*  633 */       this.pos++;
/*  634 */       return c;
/*  635 */     }  if (optional) {
/*  636 */       return Character.MIN_VALUE;
/*      */     }
/*  638 */     StringBuilder sb = new StringBuilder();
/*  639 */     for (int i = 0; i < expectedChars.length(); i++) {
/*  640 */       if (i != 0) {
/*  641 */         sb.append(" or ");
/*      */       }
/*  643 */       sb.append(StringUtil.jQuote(expectedChars.substring(i, i + 1)));
/*      */     } 
/*  645 */     throw new _ObjectBuilderSettingEvaluationException(sb
/*  646 */         .toString(), this.src, this.pos);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean isASCIIDigit(char c) {
/*  652 */     return (c >= '0' && c <= '9');
/*      */   }
/*      */   
/*      */   private boolean isIdentifierStart(char c) {
/*  656 */     return (Character.isLetter(c) || c == '_' || c == '$');
/*      */   }
/*      */   
/*      */   private boolean isIdentifierMiddle(char c) {
/*  660 */     return (isIdentifierStart(c) || isASCIIDigit(c));
/*      */   }
/*      */   
/*      */   private static synchronized String shorthandToFullQualified(String className) {
/*  664 */     if (SHORTHANDS == null) {
/*  665 */       SHORTHANDS = new HashMap<>();
/*      */       
/*  667 */       addWithSimpleName(SHORTHANDS, DefaultObjectWrapper.class);
/*  668 */       addWithSimpleName(SHORTHANDS, BeansWrapper.class);
/*  669 */       addWithSimpleName(SHORTHANDS, SimpleObjectWrapper.class);
/*      */       
/*  671 */       addWithSimpleName(SHORTHANDS, TemplateConfiguration.class);
/*      */       
/*  673 */       addWithSimpleName(SHORTHANDS, PathGlobMatcher.class);
/*  674 */       addWithSimpleName(SHORTHANDS, FileNameGlobMatcher.class);
/*  675 */       addWithSimpleName(SHORTHANDS, FileExtensionMatcher.class);
/*  676 */       addWithSimpleName(SHORTHANDS, PathRegexMatcher.class);
/*  677 */       addWithSimpleName(SHORTHANDS, AndMatcher.class);
/*  678 */       addWithSimpleName(SHORTHANDS, OrMatcher.class);
/*  679 */       addWithSimpleName(SHORTHANDS, NotMatcher.class);
/*      */       
/*  681 */       addWithSimpleName(SHORTHANDS, ConditionalTemplateConfigurationFactory.class);
/*  682 */       addWithSimpleName(SHORTHANDS, MergingTemplateConfigurationFactory.class);
/*  683 */       addWithSimpleName(SHORTHANDS, FirstMatchTemplateConfigurationFactory.class);
/*      */       
/*  685 */       addWithSimpleName(SHORTHANDS, HTMLOutputFormat.class);
/*  686 */       addWithSimpleName(SHORTHANDS, XHTMLOutputFormat.class);
/*  687 */       addWithSimpleName(SHORTHANDS, XMLOutputFormat.class);
/*  688 */       addWithSimpleName(SHORTHANDS, RTFOutputFormat.class);
/*  689 */       addWithSimpleName(SHORTHANDS, PlainTextOutputFormat.class);
/*  690 */       addWithSimpleName(SHORTHANDS, UndefinedOutputFormat.class);
/*      */       
/*  692 */       addWithSimpleName(SHORTHANDS, DefaultTruncateBuiltinAlgorithm.class);
/*      */       
/*  694 */       addWithSimpleName(SHORTHANDS, Locale.class);
/*  695 */       SHORTHANDS.put("TimeZone", "freemarker.core._TimeZone");
/*  696 */       SHORTHANDS.put("markup", "freemarker.core._Markup");
/*      */ 
/*      */       
/*  699 */       addWithSimpleName(SHORTHANDS, Configuration.class);
/*      */     } 
/*  701 */     String fullClassName = SHORTHANDS.get(className);
/*  702 */     return (fullClassName == null) ? className : fullClassName;
/*      */   }
/*      */   
/*      */   private static void addWithSimpleName(Map<String, String> map, Class<?> pClass) {
/*  706 */     map.put(pClass.getSimpleName(), pClass.getName());
/*      */   }
/*      */ 
/*      */   
/*      */   private void setJavaBeanProperties(Object bean, List<String> namedParamNames, List namedParamValues) throws _ObjectBuilderSettingEvaluationException {
/*      */     Map<Object, Object> beanPropSetters;
/*  712 */     if (namedParamNames.isEmpty()) {
/*      */       return;
/*      */     }
/*      */     
/*  716 */     Class<?> cl = bean.getClass();
/*      */     
/*      */     try {
/*  719 */       PropertyDescriptor[] propDescs = Introspector.getBeanInfo(cl).getPropertyDescriptors();
/*  720 */       beanPropSetters = new HashMap<>(propDescs.length * 4 / 3, 1.0F);
/*  721 */       for (int j = 0; j < propDescs.length; j++) {
/*  722 */         PropertyDescriptor propDesc = propDescs[j];
/*  723 */         Method writeMethod = propDesc.getWriteMethod();
/*  724 */         if (writeMethod != null) {
/*  725 */           beanPropSetters.put(propDesc.getName(), writeMethod);
/*      */         }
/*      */       } 
/*  728 */     } catch (Exception e) {
/*  729 */       throw new _ObjectBuilderSettingEvaluationException("Failed to inspect " + cl.getName() + " class", e);
/*      */     } 
/*      */     
/*  732 */     TemplateHashModel beanTM = null;
/*  733 */     for (int i = 0; i < namedParamNames.size(); i++) {
/*  734 */       String name = namedParamNames.get(i);
/*  735 */       if (!beanPropSetters.containsKey(name)) {
/*  736 */         throw new _ObjectBuilderSettingEvaluationException("The " + cl
/*  737 */             .getName() + " class has no writeable JavaBeans property called " + 
/*  738 */             StringUtil.jQuote(name) + ".");
/*      */       }
/*      */       
/*  741 */       Method beanPropSetter = (Method)beanPropSetters.put(name, null);
/*  742 */       if (beanPropSetter == null) {
/*  743 */         throw new _ObjectBuilderSettingEvaluationException("JavaBeans property " + 
/*  744 */             StringUtil.jQuote(name) + " is set twice.");
/*      */       }
/*      */       
/*      */       try {
/*  748 */         if (beanTM == null) {
/*  749 */           TemplateModel wrappedObj = this.env.getObjectWrapper().wrap(bean);
/*  750 */           if (!(wrappedObj instanceof TemplateHashModel)) {
/*  751 */             throw new _ObjectBuilderSettingEvaluationException("The " + cl
/*  752 */                 .getName() + " class is not a wrapped as TemplateHashModel.");
/*      */           }
/*  754 */           beanTM = (TemplateHashModel)wrappedObj;
/*      */         } 
/*      */         
/*  757 */         TemplateModel m = beanTM.get(beanPropSetter.getName());
/*  758 */         if (m == null) {
/*  759 */           throw new _ObjectBuilderSettingEvaluationException("Can't find " + beanPropSetter + " as FreeMarker method.");
/*      */         }
/*      */         
/*  762 */         if (!(m instanceof TemplateMethodModelEx)) {
/*  763 */           throw new _ObjectBuilderSettingEvaluationException(
/*  764 */               StringUtil.jQuote(beanPropSetter.getName()) + " wasn't a TemplateMethodModelEx.");
/*      */         }
/*  766 */         List<TemplateModel> args = new ArrayList();
/*  767 */         args.add(this.env.getObjectWrapper().wrap(namedParamValues.get(i)));
/*  768 */         ((TemplateMethodModelEx)m).exec(args);
/*  769 */       } catch (Exception e) {
/*  770 */         throw new _ObjectBuilderSettingEvaluationException("Failed to set " + 
/*  771 */             StringUtil.jQuote(name), e);
/*      */       } 
/*      */     } 
/*      */   }
/*      */   
/*      */   private static class Name { private final String name;
/*      */     
/*      */     public Name(String name) {
/*  779 */       this.name = name;
/*      */     } }
/*      */ 
/*      */   
/*      */   private static abstract class SettingExpression { private SettingExpression() {}
/*      */     
/*      */     abstract Object eval() throws _ObjectBuilderSettingEvaluationException; }
/*      */   
/*      */   private abstract class ExpressionWithParameters extends SettingExpression { protected List positionalParamValues;
/*      */     
/*      */     private ExpressionWithParameters() {
/*  790 */       this.positionalParamValues = new ArrayList();
/*  791 */       this.namedParamNames = new ArrayList();
/*  792 */       this.namedParamValues = new ArrayList();
/*      */     }
/*      */     protected List namedParamNames; protected List namedParamValues;
/*      */     protected abstract boolean getAllowPositionalParameters(); }
/*      */   private class ListExpression extends SettingExpression { private List<Object> items;
/*      */     
/*      */     private ListExpression() {
/*  799 */       this.items = new ArrayList();
/*      */     }
/*      */     void addItem(Object item) {
/*  802 */       this.items.add(item);
/*      */     }
/*      */     
/*      */     public int itemCount() {
/*  806 */       return this.items.size();
/*      */     }
/*      */ 
/*      */     
/*      */     Object eval() throws _ObjectBuilderSettingEvaluationException {
/*  811 */       ArrayList<Object> res = new ArrayList(this.items.size());
/*  812 */       for (Object item : this.items) {
/*  813 */         res.add(_ObjectBuilderSettingEvaluator.this.ensureEvaled(item));
/*      */       }
/*  815 */       return res;
/*      */     } }
/*      */   
/*      */   private class MapExpression extends SettingExpression {
/*      */     private List<_ObjectBuilderSettingEvaluator.KeyValuePair> items;
/*      */     
/*      */     private MapExpression() {
/*  822 */       this.items = new ArrayList<>();
/*      */     }
/*      */     void addItem(_ObjectBuilderSettingEvaluator.KeyValuePair item) {
/*  825 */       this.items.add(item);
/*      */     }
/*      */     
/*      */     public int itemCount() {
/*  829 */       return this.items.size();
/*      */     }
/*      */ 
/*      */     
/*      */     Object eval() throws _ObjectBuilderSettingEvaluationException {
/*  834 */       LinkedHashMap<Object, Object> res = new LinkedHashMap<>(this.items.size() * 4 / 3, 1.0F);
/*  835 */       for (_ObjectBuilderSettingEvaluator.KeyValuePair item : this.items) {
/*  836 */         Object key = _ObjectBuilderSettingEvaluator.this.ensureEvaled(item.key);
/*  837 */         if (key == null) {
/*  838 */           throw new _ObjectBuilderSettingEvaluationException("Map can't use null as key.");
/*      */         }
/*  840 */         res.put(key, _ObjectBuilderSettingEvaluator.this.ensureEvaled(item.value));
/*      */       } 
/*  842 */       return res;
/*      */     }
/*      */   }
/*      */   
/*      */   private static class KeyValuePair
/*      */   {
/*      */     private final Object key;
/*      */     private final Object value;
/*      */     
/*      */     public KeyValuePair(Object key, Object value) {
/*  852 */       this.key = key;
/*  853 */       this.value = value;
/*      */     } }
/*      */   private class BuilderCallExpression extends ExpressionWithParameters { private String className; private boolean canBeStaticField;
/*      */     private boolean mustBeStaticField;
/*      */     
/*      */     private BuilderCallExpression() {}
/*      */     
/*      */     Object eval() throws _ObjectBuilderSettingEvaluationException {
/*      */       Class cl;
/*      */       boolean clIsBuilderClass;
/*      */       Object result;
/*  864 */       if (this.mustBeStaticField) {
/*  865 */         if (!this.canBeStaticField) {
/*  866 */           throw new BugException();
/*      */         }
/*  868 */         return getStaticFieldValue(this.className);
/*      */       } 
/*      */ 
/*      */ 
/*      */       
/*  873 */       if (!_ObjectBuilderSettingEvaluator.this.modernMode) {
/*      */         
/*      */         try {
/*  876 */           return ClassUtil.forName(this.className).newInstance();
/*  877 */         } catch (InstantiationException e) {
/*  878 */           throw new _ObjectBuilderSettingEvaluator.LegacyExceptionWrapperSettingEvaluationExpression(e);
/*  879 */         } catch (IllegalAccessException e) {
/*  880 */           throw new _ObjectBuilderSettingEvaluator.LegacyExceptionWrapperSettingEvaluationExpression(e);
/*  881 */         } catch (ClassNotFoundException e) {
/*  882 */           throw new _ObjectBuilderSettingEvaluator.LegacyExceptionWrapperSettingEvaluationExpression(e);
/*      */         }
/*  884 */         catch (LegacyExceptionWrapperSettingEvaluationExpression e) {
/*  885 */           if (!this.canBeStaticField || this.className.indexOf('.') == -1) {
/*  886 */             throw e;
/*      */           }
/*      */           
/*      */           try {
/*  890 */             return getStaticFieldValue(this.className);
/*  891 */           } catch (_ObjectBuilderSettingEvaluationException e2) {
/*  892 */             throw e;
/*      */           } 
/*      */         } 
/*      */       }
/*      */ 
/*      */       
/*      */       try {
/*  899 */         cl = ClassUtil.forName(this.className + "Builder");
/*  900 */         clIsBuilderClass = true;
/*  901 */       } catch (ClassNotFoundException e) {
/*  902 */         clIsBuilderClass = false;
/*      */         try {
/*  904 */           cl = ClassUtil.forName(this.className);
/*  905 */         } catch (Exception e2) {
/*      */           boolean failedToGetAsStaticField;
/*  907 */           if (this.canBeStaticField) {
/*      */             
/*      */             try {
/*  910 */               return getStaticFieldValue(this.className);
/*  911 */             } catch (_ObjectBuilderSettingEvaluationException e3) {
/*      */               
/*  913 */               failedToGetAsStaticField = true;
/*      */             } 
/*      */           } else {
/*  916 */             failedToGetAsStaticField = false;
/*      */           } 
/*  918 */           throw new _ObjectBuilderSettingEvaluationException("Failed to get class " + 
/*  919 */               StringUtil.jQuote(this.className) + (failedToGetAsStaticField ? " (also failed to resolve name as static field)" : "") + ".", e2);
/*      */         } 
/*      */       } 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  926 */       if (!clIsBuilderClass && hasNoParameters()) {
/*      */         try {
/*  928 */           Field f = cl.getField("INSTANCE");
/*  929 */           if ((f.getModifiers() & 0x9) == 9)
/*      */           {
/*  931 */             return f.get(null);
/*      */           }
/*  933 */         } catch (NoSuchFieldException noSuchFieldException) {
/*      */         
/*  935 */         } catch (Exception e) {
/*  936 */           throw new _ObjectBuilderSettingEvaluationException("Error when trying to access " + 
/*  937 */               StringUtil.jQuote(this.className) + "." + "INSTANCE", e);
/*      */         } 
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*  943 */       Object constructorResult = callConstructor(cl);
/*      */ 
/*      */       
/*  946 */       _ObjectBuilderSettingEvaluator.this.setJavaBeanProperties(constructorResult, this.namedParamNames, this.namedParamValues);
/*      */ 
/*      */       
/*  949 */       if (clIsBuilderClass) {
/*  950 */         result = callBuild(constructorResult);
/*      */       } else {
/*  952 */         if (constructorResult instanceof WriteProtectable) {
/*  953 */           ((WriteProtectable)constructorResult).writeProtect();
/*      */         }
/*  955 */         result = constructorResult;
/*      */       } 
/*      */       
/*  958 */       return result;
/*      */     } private Object getStaticFieldValue(String dottedName) throws _ObjectBuilderSettingEvaluationException {
/*      */       Class<?> cl;
/*      */       Field field;
/*  962 */       int lastDotIdx = dottedName.lastIndexOf('.');
/*  963 */       if (lastDotIdx == -1) {
/*  964 */         throw new IllegalArgumentException();
/*      */       }
/*  966 */       String className = _ObjectBuilderSettingEvaluator.shorthandToFullQualified(dottedName.substring(0, lastDotIdx));
/*  967 */       String fieldName = dottedName.substring(lastDotIdx + 1);
/*      */ 
/*      */       
/*      */       try {
/*  971 */         cl = ClassUtil.forName(className);
/*  972 */       } catch (Exception e) {
/*  973 */         throw new _ObjectBuilderSettingEvaluationException("Failed to get field's parent class, " + 
/*  974 */             StringUtil.jQuote(className) + ".", e);
/*      */       } 
/*      */ 
/*      */ 
/*      */       
/*      */       try {
/*  980 */         field = cl.getField(fieldName);
/*  981 */       } catch (Exception e) {
/*  982 */         throw new _ObjectBuilderSettingEvaluationException("Failed to get field " + 
/*  983 */             StringUtil.jQuote(fieldName) + " from class " + 
/*  984 */             StringUtil.jQuote(className) + ".", e);
/*      */       } 
/*      */ 
/*      */       
/*  988 */       if ((field.getModifiers() & 0x8) == 0) {
/*  989 */         throw new _ObjectBuilderSettingEvaluationException("Referred field isn't static: " + field);
/*      */       }
/*  991 */       if ((field.getModifiers() & 0x1) == 0) {
/*  992 */         throw new _ObjectBuilderSettingEvaluationException("Referred field isn't public: " + field);
/*      */       }
/*      */       
/*  995 */       if (field.getName().equals("INSTANCE")) {
/*  996 */         throw new _ObjectBuilderSettingEvaluationException("The INSTANCE field is only accessible through pseudo-constructor call: " + className + "()");
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       try {
/* 1002 */         return field.get(null);
/* 1003 */       } catch (Exception e) {
/* 1004 */         throw new _ObjectBuilderSettingEvaluationException("Failed to get field value: " + field, e);
/*      */       } 
/*      */     }
/*      */ 
/*      */     
/*      */     private Object callConstructor(Class cl) throws _ObjectBuilderSettingEvaluationException {
/* 1010 */       if (hasNoParameters()) {
/*      */         
/*      */         try {
/* 1013 */           return cl.newInstance();
/* 1014 */         } catch (Exception e) {
/* 1015 */           throw new _ObjectBuilderSettingEvaluationException("Failed to call " + cl
/* 1016 */               .getName() + " 0-argument constructor", e);
/*      */         } 
/*      */       }
/* 1019 */       BeansWrapper ow = _ObjectBuilderSettingEvaluator.this.env.getObjectWrapper();
/* 1020 */       List<TemplateModel> tmArgs = new ArrayList(this.positionalParamValues.size());
/* 1021 */       for (int i = 0; i < this.positionalParamValues.size(); i++) {
/*      */         try {
/* 1023 */           tmArgs.add(ow.wrap(this.positionalParamValues.get(i)));
/* 1024 */         } catch (TemplateModelException e) {
/* 1025 */           throw new _ObjectBuilderSettingEvaluationException("Failed to wrap arg #" + (i + 1), e);
/*      */         } 
/*      */       } 
/*      */       try {
/* 1029 */         return ow.newInstance(cl, tmArgs);
/* 1030 */       } catch (Exception e) {
/* 1031 */         throw new _ObjectBuilderSettingEvaluationException("Failed to call " + cl
/* 1032 */             .getName() + " constructor", e);
/*      */       } 
/*      */     }
/*      */ 
/*      */     
/*      */     private Object callBuild(Object constructorResult) throws _ObjectBuilderSettingEvaluationException {
/*      */       Method buildMethod;
/* 1039 */       Class<?> cl = constructorResult.getClass();
/*      */       
/*      */       try {
/* 1042 */         buildMethod = constructorResult.getClass().getMethod("build", (Class[])null);
/* 1043 */       } catch (NoSuchMethodException e) {
/* 1044 */         throw new _ObjectBuilderSettingEvaluationException("The " + cl.getName() + " builder class must have a public " + "build" + "() method", e);
/*      */       }
/* 1046 */       catch (Exception e) {
/* 1047 */         throw new _ObjectBuilderSettingEvaluationException("Failed to get the build() method of the " + cl
/* 1048 */             .getName() + " builder class", e);
/*      */       } 
/*      */       
/*      */       try {
/* 1052 */         return buildMethod.invoke(constructorResult, (Object[])null);
/* 1053 */       } catch (Exception e) {
/*      */         Throwable cause;
/* 1055 */         if (e instanceof InvocationTargetException) {
/* 1056 */           cause = ((InvocationTargetException)e).getTargetException();
/*      */         } else {
/* 1058 */           cause = e;
/*      */         } 
/* 1060 */         throw new _ObjectBuilderSettingEvaluationException("Failed to call build() method on " + cl
/* 1061 */             .getName() + " instance", cause);
/*      */       } 
/*      */     }
/*      */     
/*      */     private boolean hasNoParameters() {
/* 1066 */       return (this.positionalParamValues.isEmpty() && this.namedParamValues.isEmpty());
/*      */     }
/*      */ 
/*      */     
/*      */     protected boolean getAllowPositionalParameters() {
/* 1071 */       return true;
/*      */     } }
/*      */ 
/*      */   
/*      */   private class PropertyAssignmentsExpression
/*      */     extends ExpressionWithParameters
/*      */   {
/*      */     private final Object bean;
/*      */     
/*      */     public PropertyAssignmentsExpression(Object bean) {
/* 1081 */       this.bean = bean;
/*      */     }
/*      */ 
/*      */     
/*      */     Object eval() throws _ObjectBuilderSettingEvaluationException {
/* 1086 */       _ObjectBuilderSettingEvaluator.this.setJavaBeanProperties(this.bean, this.namedParamNames, this.namedParamValues);
/* 1087 */       return this.bean;
/*      */     }
/*      */ 
/*      */     
/*      */     protected boolean getAllowPositionalParameters() {
/* 1092 */       return false;
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   private static class LegacyExceptionWrapperSettingEvaluationExpression
/*      */     extends _ObjectBuilderSettingEvaluationException
/*      */   {
/*      */     public LegacyExceptionWrapperSettingEvaluationExpression(Throwable cause) {
/* 1101 */       super("Legacy operation failed", cause);
/* 1102 */       if (!(cause instanceof ClassNotFoundException) && !(cause instanceof InstantiationException) && !(cause instanceof IllegalAccessException))
/*      */       {
/*      */ 
/*      */ 
/*      */         
/* 1107 */         throw new IllegalArgumentException();
/*      */       }
/*      */     }
/*      */     
/*      */     public void rethrowLegacy() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
/* 1112 */       Throwable cause = getCause();
/* 1113 */       if (cause instanceof ClassNotFoundException) throw (ClassNotFoundException)cause; 
/* 1114 */       if (cause instanceof InstantiationException) throw (InstantiationException)cause; 
/* 1115 */       if (cause instanceof IllegalAccessException) throw (IllegalAccessException)cause; 
/* 1116 */       throw new BugException();
/*      */     }
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\_ObjectBuilderSettingEvaluator.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */