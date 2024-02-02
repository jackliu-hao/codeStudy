/*     */ package freemarker.core;
/*     */ 
/*     */ import freemarker.template.Template;
/*     */ import freemarker.template.TemplateException;
/*     */ import freemarker.template.TemplateHashModelEx;
/*     */ import freemarker.template.TemplateHashModelEx2;
/*     */ import freemarker.template.TemplateModel;
/*     */ import freemarker.template.TemplateModelException;
/*     */ import freemarker.template.utility.StringUtil;
/*     */ import java.util.ArrayList;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class _MessageUtil
/*     */ {
/*     */   static final String UNKNOWN_DATE_TO_STRING_ERROR_MESSAGE = "Can't convert the date-like value to string because it isn't known if it's a date (no time part), time or date-time value.";
/*     */   static final String UNKNOWN_DATE_PARSING_ERROR_MESSAGE = "Can't parse the string to date-like value because it isn't known if it's desired result should be a date (no time part), a time, or a date-time value.";
/*     */   static final String UNKNOWN_DATE_TYPE_ERROR_TIP = "Use ?date, ?time, or ?datetime to tell FreeMarker the exact type.";
/*  49 */   static final Object[] UNKNOWN_DATE_TO_STRING_TIPS = new Object[] { "Use ?date, ?time, or ?datetime to tell FreeMarker the exact type.", "If you need a particular format only once, use ?string(pattern), like ?string('dd.MM.yyyy HH:mm:ss'), to specify which fields to display. " };
/*     */ 
/*     */ 
/*     */   
/*     */   static final String EMBEDDED_MESSAGE_BEGIN = "---begin-message---\n";
/*     */ 
/*     */ 
/*     */   
/*     */   static final String EMBEDDED_MESSAGE_END = "\n---end-message---";
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static String formatLocationForSimpleParsingError(Template template, int line, int column) {
/*  63 */     return formatLocation("in", template, line, column);
/*     */   }
/*     */   
/*     */   static String formatLocationForSimpleParsingError(String templateSourceName, int line, int column) {
/*  67 */     return formatLocation("in", templateSourceName, line, column);
/*     */   }
/*     */   
/*     */   static String formatLocationForDependentParsingError(Template template, int line, int column) {
/*  71 */     return formatLocation("on", template, line, column);
/*     */   }
/*     */   
/*     */   static String formatLocationForDependentParsingError(String templateSourceName, int line, int column) {
/*  75 */     return formatLocation("on", templateSourceName, line, column);
/*     */   }
/*     */   
/*     */   static String formatLocationForEvaluationError(Template template, int line, int column) {
/*  79 */     return formatLocation("at", template, line, column);
/*     */   }
/*     */   
/*     */   static String formatLocationForEvaluationError(Macro macro, int line, int column) {
/*  83 */     Template t = macro.getTemplate();
/*  84 */     return formatLocation("at", (t != null) ? t.getSourceName() : null, macro.getName(), macro.isFunction(), line, column);
/*     */   }
/*     */   
/*     */   static String formatLocationForEvaluationError(String templateSourceName, int line, int column) {
/*  88 */     return formatLocation("at", templateSourceName, line, column);
/*     */   }
/*     */   
/*     */   private static String formatLocation(String preposition, Template template, int line, int column) {
/*  92 */     return formatLocation(preposition, (template != null) ? template.getSourceName() : null, line, column);
/*     */   }
/*     */   
/*     */   private static String formatLocation(String preposition, String templateSourceName, int line, int column) {
/*  96 */     return formatLocation(preposition, templateSourceName, null, false, line, column);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static String formatLocation(String preposition, String templateSourceName, String macroOrFuncName, boolean isFunction, int line, int column) {
/*     */     String templateDesc;
/* 107 */     if (line < 0) {
/* 108 */       templateDesc = "?eval-ed string";
/* 109 */       macroOrFuncName = null;
/*     */     } else {
/*     */       
/* 112 */       templateDesc = (templateSourceName != null) ? ("template " + StringUtil.jQuoteNoXSS(templateSourceName)) : "nameless template";
/*     */     } 
/*     */     
/* 115 */     return "in " + templateDesc + ((macroOrFuncName != null) ? (" in " + (isFunction ? "function " : "macro ") + 
/*     */       
/* 117 */       StringUtil.jQuote(macroOrFuncName)) : "") + " " + preposition + " " + 
/*     */ 
/*     */       
/* 120 */       formatPosition(line, column);
/*     */   }
/*     */   
/*     */   static String formatPosition(int line, int column) {
/* 124 */     return "line " + ((line >= 0) ? line : (line - -1000000001)) + ", column " + column;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String shorten(String s, int maxLength) {
/* 134 */     if (maxLength < 5) maxLength = 5;
/*     */     
/* 136 */     boolean isTruncated = false;
/*     */     
/* 138 */     int brIdx = s.indexOf('\n');
/* 139 */     if (brIdx != -1) {
/* 140 */       s = s.substring(0, brIdx);
/* 141 */       isTruncated = true;
/*     */     } 
/* 143 */     brIdx = s.indexOf('\r');
/* 144 */     if (brIdx != -1) {
/* 145 */       s = s.substring(0, brIdx);
/* 146 */       isTruncated = true;
/*     */     } 
/*     */     
/* 149 */     if (s.length() > maxLength) {
/* 150 */       s = s.substring(0, maxLength - 3);
/* 151 */       isTruncated = true;
/*     */     } 
/*     */     
/* 154 */     if (!isTruncated) {
/* 155 */       return s;
/*     */     }
/* 157 */     if (s.endsWith(".")) {
/* 158 */       if (s.endsWith("..")) {
/* 159 */         if (s.endsWith("...")) {
/* 160 */           return s;
/*     */         }
/* 162 */         return s + ".";
/*     */       } 
/*     */       
/* 165 */       return s + "..";
/*     */     } 
/*     */     
/* 168 */     return s + "...";
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static StringBuilder appendExpressionAsUntearable(StringBuilder sb, Expression argExp) {
/* 174 */     boolean needParen = (!(argExp instanceof NumberLiteral) && !(argExp instanceof StringLiteral) && !(argExp instanceof BooleanLiteral) && !(argExp instanceof ListLiteral) && !(argExp instanceof HashLiteral) && !(argExp instanceof Identifier) && !(argExp instanceof Dot) && !(argExp instanceof DynamicKeyName) && !(argExp instanceof MethodCall) && !(argExp instanceof BuiltIn) && !(argExp instanceof ExistsExpression) && !(argExp instanceof ParentheticalExpression));
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 187 */     if (needParen) sb.append('('); 
/* 188 */     sb.append(argExp.getCanonicalForm());
/* 189 */     if (needParen) sb.append(')'); 
/* 190 */     return sb;
/*     */   }
/*     */   
/*     */   public static TemplateModelException newArgCntError(String methodName, int argCnt, int expectedCnt) {
/* 194 */     return newArgCntError(methodName, argCnt, expectedCnt, expectedCnt);
/*     */   }
/*     */   
/*     */   public static TemplateModelException newArgCntError(String methodName, int argCnt, int minCnt, int maxCnt) {
/* 198 */     ArrayList<String> desc = new ArrayList(20);
/*     */     
/* 200 */     desc.add(methodName);
/*     */     
/* 202 */     desc.add("(");
/* 203 */     if (maxCnt != 0) desc.add("..."); 
/* 204 */     desc.add(") expects ");
/*     */     
/* 206 */     if (minCnt == maxCnt) {
/* 207 */       if (maxCnt == 0) {
/* 208 */         desc.add("no");
/*     */       } else {
/* 210 */         desc.add(Integer.valueOf(maxCnt));
/*     */       } 
/* 212 */     } else if (maxCnt - minCnt == 1) {
/* 213 */       desc.add(Integer.valueOf(minCnt));
/* 214 */       desc.add(" or ");
/* 215 */       desc.add(Integer.valueOf(maxCnt));
/*     */     } else {
/* 217 */       desc.add(Integer.valueOf(minCnt));
/* 218 */       if (maxCnt != Integer.MAX_VALUE) {
/* 219 */         desc.add(" to ");
/* 220 */         desc.add(Integer.valueOf(maxCnt));
/*     */       } else {
/* 222 */         desc.add(" or more (unlimited)");
/*     */       } 
/*     */     } 
/* 225 */     desc.add(" argument");
/* 226 */     if (maxCnt > 1) desc.add("s");
/*     */     
/* 228 */     desc.add(" but has received ");
/* 229 */     if (argCnt == 0) {
/* 230 */       desc.add("none");
/*     */     } else {
/* 232 */       desc.add(Integer.valueOf(argCnt));
/*     */     } 
/* 234 */     desc.add(".");
/*     */     
/* 236 */     return new _TemplateModelException(desc.toArray());
/*     */   }
/*     */   
/*     */   public static TemplateModelException newMethodArgMustBeStringException(String methodName, int argIdx, TemplateModel arg) {
/* 240 */     return newMethodArgUnexpectedTypeException(methodName, argIdx, "string", arg);
/*     */   }
/*     */   
/*     */   public static TemplateModelException newMethodArgMustBeNumberException(String methodName, int argIdx, TemplateModel arg) {
/* 244 */     return newMethodArgUnexpectedTypeException(methodName, argIdx, "number", arg);
/*     */   }
/*     */   
/*     */   public static TemplateModelException newMethodArgMustBeBooleanException(String methodName, int argIdx, TemplateModel arg) {
/* 248 */     return newMethodArgUnexpectedTypeException(methodName, argIdx, "boolean", arg);
/*     */   }
/*     */ 
/*     */   
/*     */   public static TemplateModelException newMethodArgMustBeExtendedHashException(String methodName, int argIdx, TemplateModel arg) {
/* 253 */     return newMethodArgUnexpectedTypeException(methodName, argIdx, "extended hash", arg);
/*     */   }
/*     */ 
/*     */   
/*     */   public static TemplateModelException newMethodArgMustBeExtendedHashOrSequnceException(String methodName, int argIdx, TemplateModel arg) {
/* 258 */     return newMethodArgUnexpectedTypeException(methodName, argIdx, "extended hash or sequence", arg);
/*     */   }
/*     */ 
/*     */   
/*     */   public static TemplateModelException newMethodArgMustBeSequenceException(String methodName, int argIdx, TemplateModel arg) {
/* 263 */     return newMethodArgUnexpectedTypeException(methodName, argIdx, "sequence", arg);
/*     */   }
/*     */ 
/*     */   
/*     */   public static TemplateModelException newMethodArgMustBeSequenceOrCollectionException(String methodName, int argIdx, TemplateModel arg) {
/* 268 */     return newMethodArgUnexpectedTypeException(methodName, argIdx, "sequence or collection", arg);
/*     */   }
/*     */ 
/*     */   
/*     */   public static TemplateModelException newMethodArgMustBeStringOrMarkupOutputException(String methodName, int argIdx, TemplateModel arg) {
/* 273 */     return newMethodArgUnexpectedTypeException(methodName, argIdx, "string or markup output", arg);
/*     */   }
/*     */ 
/*     */   
/*     */   public static TemplateModelException newMethodArgUnexpectedTypeException(String methodName, int argIdx, String expectedType, TemplateModel arg) {
/* 278 */     return new _TemplateModelException(new Object[] { methodName, "(...) expects ", new _DelayedAOrAn(expectedType), " as argument #", 
/* 279 */           Integer.valueOf(argIdx + 1), ", but received ", new _DelayedAOrAn(new _DelayedFTLTypeDescription(arg)), "." });
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static TemplateModelException newMethodArgInvalidValueException(String methodName, int argIdx, Object... details) {
/* 288 */     return new _TemplateModelException(new Object[] { methodName, "(...) argument #", 
/* 289 */           Integer.valueOf(argIdx + 1), " had invalid value: ", details });
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static TemplateModelException newMethodArgsInvalidValueException(String methodName, Object... details) {
/* 298 */     return new _TemplateModelException(new Object[] { methodName, "(...) arguments have invalid value: ", details });
/*     */   }
/*     */   
/*     */   public static TemplateException newInstantiatingClassNotAllowedException(String className, Environment env) {
/* 302 */     return new _MiscTemplateException(env, new Object[] { "Instantiating ", className, " is not allowed in the template for security reasons." });
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static _TemplateModelException newCantFormatUnknownTypeDateException(Expression dateSourceExpr, UnknownDateTypeFormattingUnsupportedException cause) {
/* 308 */     return new _TemplateModelException(cause, null, (new _ErrorDescriptionBuilder("Can't convert the date-like value to string because it isn't known if it's a date (no time part), time or date-time value."))
/*     */         
/* 310 */         .blame(dateSourceExpr)
/* 311 */         .tips(UNKNOWN_DATE_TO_STRING_TIPS));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static TemplateException newCantFormatDateException(TemplateDateFormat format, Expression dataSrcExp, TemplateValueFormatException e, boolean useTempModelExc) {
/* 319 */     _ErrorDescriptionBuilder desc = (new _ErrorDescriptionBuilder(new Object[] { "Failed to format date/time/datetime with format ", new _DelayedJQuote(format.getDescription()), ": ", e.getMessage() })).blame(dataSrcExp);
/* 320 */     return useTempModelExc ? (TemplateException)new _TemplateModelException(e, null, desc) : new _MiscTemplateException(e, null, desc);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static TemplateException newCantFormatNumberException(TemplateNumberFormat format, Expression dataSrcExp, TemplateValueFormatException e, boolean useTempModelExc) {
/* 330 */     _ErrorDescriptionBuilder desc = (new _ErrorDescriptionBuilder(new Object[] { "Failed to format number with format ", new _DelayedJQuote(format.getDescription()), ": ", e.getMessage() })).blame(dataSrcExp);
/* 331 */     return useTempModelExc ? (TemplateException)new _TemplateModelException(e, null, desc) : new _MiscTemplateException(e, null, desc);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static TemplateModelException newKeyValuePairListingNonStringKeyExceptionMessage(TemplateModel key, TemplateHashModelEx listedHashEx) {
/* 338 */     return new _TemplateModelException((new _ErrorDescriptionBuilder(new Object[] { "When listing key-value pairs of traditional hash implementations, all keys must be strings, but one of them was ", new _DelayedAOrAn(new _DelayedFTLTypeDescription(key)), "."
/*     */ 
/*     */ 
/*     */ 
/*     */           
/* 343 */           })).tip(new Object[] {
/* 344 */             "The listed value's TemplateModel class was ", new _DelayedShortClassName(listedHashEx.getClass()), ", which doesn't implement ", new _DelayedShortClassName(TemplateHashModelEx2.class), ", which leads to this restriction."
/*     */           }));
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
/*     */   public static TemplateException newLazilyGeneratedCollectionMustBeSequenceException(Expression blamed) {
/* 357 */     return new _MiscTemplateException(blamed, new Object[] { "The result is a listable value with lazy transformation(s) applied on it, but it's not an FTL sequence (it's not a List-like value, but an Iterator-like value). The place doesn't support such values due to technical limitations. So either pass it to a construct that supports such values (like ", "<#list transformedListable as x>", "), or, if you know that you don't have too many elements, use transformedListable?sequence to allow it to be treated as an FTL sequence." });
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
/*     */   public static String getAOrAn(String s) {
/* 369 */     if (s == null) return null; 
/* 370 */     if (s.length() == 0) return "";
/*     */     
/* 372 */     char fc = Character.toLowerCase(s.charAt(0));
/* 373 */     if (fc == 'a' || fc == 'e' || fc == 'i')
/* 374 */       return "an"; 
/* 375 */     if (fc == 'h') {
/* 376 */       String ls = s.toLowerCase();
/* 377 */       if (ls.startsWith("has") || ls.startsWith("hi"))
/* 378 */         return "a"; 
/* 379 */       if (ls.startsWith("ht")) {
/* 380 */         return "an";
/*     */       }
/* 382 */       return "a(n)";
/*     */     } 
/* 384 */     if (fc == 'u' || fc == 'o') {
/* 385 */       return "a(n)";
/*     */     }
/* 387 */     char sc = (s.length() > 1) ? s.charAt(1) : Character.MIN_VALUE;
/* 388 */     if (fc == 'x' && sc != 'a' && sc != 'e' && sc != 'i' && sc != 'a' && sc != 'o' && sc != 'u') {
/* 389 */       return "an";
/*     */     }
/* 391 */     return "a";
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\_MessageUtil.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */