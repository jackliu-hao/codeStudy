/*     */ package freemarker.core;
/*     */ 
/*     */ import freemarker.template.Configuration;
/*     */ import freemarker.template.SimpleDate;
/*     */ import freemarker.template.SimpleScalar;
/*     */ import freemarker.template.TemplateException;
/*     */ import freemarker.template.TemplateHashModel;
/*     */ import freemarker.template.TemplateModel;
/*     */ import freemarker.template.TemplateModelException;
/*     */ import freemarker.template.TemplateScalarModel;
/*     */ import freemarker.template._TemplateAPI;
/*     */ import freemarker.template.utility.StringUtil;
/*     */ import java.util.Arrays;
/*     */ import java.util.Date;
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
/*     */ final class BuiltinVariable
/*     */   extends Expression
/*     */ {
/*     */   static final String TEMPLATE_NAME_CC = "templateName";
/*     */   static final String TEMPLATE_NAME = "template_name";
/*     */   static final String MAIN_TEMPLATE_NAME_CC = "mainTemplateName";
/*     */   static final String MAIN_TEMPLATE_NAME = "main_template_name";
/*     */   static final String CURRENT_TEMPLATE_NAME_CC = "currentTemplateName";
/*     */   static final String CURRENT_TEMPLATE_NAME = "current_template_name";
/*     */   static final String NAMESPACE = "namespace";
/*     */   static final String MAIN = "main";
/*     */   static final String GLOBALS = "globals";
/*     */   static final String LOCALS = "locals";
/*     */   static final String DATA_MODEL_CC = "dataModel";
/*     */   static final String DATA_MODEL = "data_model";
/*     */   static final String LANG = "lang";
/*     */   static final String LOCALE = "locale";
/*     */   static final String LOCALE_OBJECT_CC = "localeObject";
/*     */   static final String LOCALE_OBJECT = "locale_object";
/*     */   static final String TIME_ZONE_CC = "timeZone";
/*     */   static final String TIME_ZONE = "time_zone";
/*     */   static final String CURRENT_NODE_CC = "currentNode";
/*     */   static final String CURRENT_NODE = "current_node";
/*     */   static final String NODE = "node";
/*     */   static final String PASS = "pass";
/*     */   static final String VARS = "vars";
/*     */   static final String VERSION = "version";
/*     */   static final String INCOMPATIBLE_IMPROVEMENTS_CC = "incompatibleImprovements";
/*     */   static final String INCOMPATIBLE_IMPROVEMENTS = "incompatible_improvements";
/*     */   static final String ERROR = "error";
/*     */   static final String OUTPUT_ENCODING_CC = "outputEncoding";
/*     */   static final String OUTPUT_ENCODING = "output_encoding";
/*     */   static final String OUTPUT_FORMAT_CC = "outputFormat";
/*     */   static final String OUTPUT_FORMAT = "output_format";
/*     */   static final String AUTO_ESC_CC = "autoEsc";
/*     */   static final String AUTO_ESC = "auto_esc";
/*     */   static final String URL_ESCAPING_CHARSET_CC = "urlEscapingCharset";
/*     */   static final String URL_ESCAPING_CHARSET = "url_escaping_charset";
/*     */   static final String NOW = "now";
/*     */   static final String GET_OPTIONAL_TEMPLATE = "get_optional_template";
/*     */   static final String GET_OPTIONAL_TEMPLATE_CC = "getOptionalTemplate";
/*     */   static final String CALLER_TEMPLATE_NAME = "caller_template_name";
/*     */   static final String CALLER_TEMPLATE_NAME_CC = "callerTemplateName";
/*     */   static final String ARGS = "args";
/*  84 */   static final String[] SPEC_VAR_NAMES = new String[] { "args", "autoEsc", "auto_esc", "callerTemplateName", "caller_template_name", "currentNode", "currentTemplateName", "current_node", "current_template_name", "dataModel", "data_model", "error", "getOptionalTemplate", "get_optional_template", "globals", "incompatibleImprovements", "incompatible_improvements", "lang", "locale", "localeObject", "locale_object", "locals", "main", "mainTemplateName", "main_template_name", "namespace", "node", "now", "outputEncoding", "outputFormat", "output_encoding", "output_format", "pass", "templateName", "template_name", "timeZone", "time_zone", "urlEscapingCharset", "url_escaping_charset", "vars", "version" };
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
/*     */   private final String name;
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
/*     */   private final TemplateModel parseTimeValue;
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
/*     */   BuiltinVariable(Token nameTk, FMParserTokenManager tokenManager, TemplateModel parseTimeValue) throws ParseException {
/* 134 */     String name = nameTk.image;
/* 135 */     this.parseTimeValue = parseTimeValue;
/* 136 */     if (Arrays.binarySearch((Object[])SPEC_VAR_NAMES, name) < 0) {
/* 137 */       String correctName; StringBuilder sb = new StringBuilder();
/* 138 */       sb.append("Unknown special variable name: ");
/* 139 */       sb.append(StringUtil.jQuote(name)).append(".");
/*     */ 
/*     */ 
/*     */       
/* 143 */       int namingConvention = tokenManager.namingConvention;
/* 144 */       int shownNamingConvention = (namingConvention != 10) ? namingConvention : 11;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 150 */       if (name.equals("auto_escape") || name.equals("auto_escaping") || name.equals("autoesc")) {
/* 151 */         correctName = "auto_esc";
/* 152 */       } else if (name.equals("autoEscape") || name.equals("autoEscaping")) {
/* 153 */         correctName = "autoEsc";
/*     */       } else {
/* 155 */         correctName = null;
/*     */       } 
/* 157 */       if (correctName != null) {
/* 158 */         sb.append(" You may meant: ");
/* 159 */         sb.append(StringUtil.jQuote(correctName)).append(".");
/*     */       } 
/*     */ 
/*     */       
/* 163 */       sb.append("\nThe allowed special variable names are: ");
/* 164 */       boolean first = true;
/* 165 */       for (int i = 0; i < SPEC_VAR_NAMES.length; i++) {
/* 166 */         String str = SPEC_VAR_NAMES[i];
/* 167 */         int correctNameNamingConvetion = _CoreStringUtils.getIdentifierNamingConvention(str);
/* 168 */         if ((shownNamingConvention == 12) ? (correctNameNamingConvetion != 11) : (correctNameNamingConvetion != 12)) {
/*     */ 
/*     */           
/* 171 */           if (first) {
/* 172 */             first = false;
/*     */           } else {
/* 174 */             sb.append(", ");
/*     */           } 
/* 176 */           sb.append(str);
/*     */         } 
/*     */       } 
/* 179 */       throw new ParseException(sb.toString(), null, nameTk);
/*     */     } 
/*     */     
/* 182 */     this.name = name.intern();
/*     */   }
/*     */ 
/*     */   
/*     */   TemplateModel _eval(Environment env) throws TemplateException {
/* 187 */     if (this.parseTimeValue != null) {
/* 188 */       return this.parseTimeValue;
/*     */     }
/* 190 */     if (this.name == "namespace") {
/* 191 */       return (TemplateModel)env.getCurrentNamespace();
/*     */     }
/* 193 */     if (this.name == "main") {
/* 194 */       return (TemplateModel)env.getMainNamespace();
/*     */     }
/* 196 */     if (this.name == "globals") {
/* 197 */       return (TemplateModel)env.getGlobalVariables();
/*     */     }
/* 199 */     if (this.name == "locals") {
/* 200 */       Macro.Context ctx = env.getCurrentMacroContext();
/* 201 */       return (ctx == null) ? null : (TemplateModel)ctx.getLocals();
/*     */     } 
/* 203 */     if (this.name == "data_model" || this.name == "dataModel") {
/* 204 */       return (TemplateModel)env.getDataModel();
/*     */     }
/* 206 */     if (this.name == "vars") {
/* 207 */       return (TemplateModel)new VarsHash(env);
/*     */     }
/* 209 */     if (this.name == "locale") {
/* 210 */       return (TemplateModel)new SimpleScalar(env.getLocale().toString());
/*     */     }
/* 212 */     if (this.name == "locale_object" || this.name == "localeObject") {
/* 213 */       return env.getObjectWrapper().wrap(env.getLocale());
/*     */     }
/* 215 */     if (this.name == "lang") {
/* 216 */       return (TemplateModel)new SimpleScalar(env.getLocale().getLanguage());
/*     */     }
/* 218 */     if (this.name == "current_node" || this.name == "node" || this.name == "currentNode") {
/* 219 */       return (TemplateModel)env.getCurrentVisitorNode();
/*     */     }
/* 221 */     if (this.name == "template_name" || this.name == "templateName")
/*     */     {
/*     */ 
/*     */       
/* 225 */       return (env.getConfiguration().getIncompatibleImprovements().intValue() >= _TemplateAPI.VERSION_INT_2_3_23) ? (TemplateModel)new SimpleScalar(env
/* 226 */           .getTemplate230().getName()) : (TemplateModel)new SimpleScalar(env
/* 227 */           .getTemplate().getName());
/*     */     }
/* 229 */     if (this.name == "main_template_name" || this.name == "mainTemplateName") {
/* 230 */       return (TemplateModel)SimpleScalar.newInstanceOrNull(env.getMainTemplate().getName());
/*     */     }
/* 232 */     if (this.name == "current_template_name" || this.name == "currentTemplateName") {
/* 233 */       return (TemplateModel)SimpleScalar.newInstanceOrNull(env.getCurrentTemplate().getName());
/*     */     }
/* 235 */     if (this.name == "pass") {
/* 236 */       return Macro.DO_NOTHING_MACRO;
/*     */     }
/* 238 */     if (this.name == "output_encoding" || this.name == "outputEncoding") {
/* 239 */       String s = env.getOutputEncoding();
/* 240 */       return (TemplateModel)SimpleScalar.newInstanceOrNull(s);
/*     */     } 
/* 242 */     if (this.name == "url_escaping_charset" || this.name == "urlEscapingCharset") {
/* 243 */       String s = env.getURLEscapingCharset();
/* 244 */       return (TemplateModel)SimpleScalar.newInstanceOrNull(s);
/*     */     } 
/* 246 */     if (this.name == "error") {
/* 247 */       return (TemplateModel)new SimpleScalar(env.getCurrentRecoveredErrorMessage());
/*     */     }
/* 249 */     if (this.name == "now") {
/* 250 */       return (TemplateModel)new SimpleDate(new Date(), 3);
/*     */     }
/* 252 */     if (this.name == "version") {
/* 253 */       return (TemplateModel)new SimpleScalar(Configuration.getVersionNumber());
/*     */     }
/* 255 */     if (this.name == "incompatible_improvements" || this.name == "incompatibleImprovements") {
/* 256 */       return (TemplateModel)new SimpleScalar(env.getConfiguration().getIncompatibleImprovements().toString());
/*     */     }
/* 258 */     if (this.name == "get_optional_template") {
/* 259 */       return (TemplateModel)GetOptionalTemplateMethod.INSTANCE;
/*     */     }
/* 261 */     if (this.name == "getOptionalTemplate") {
/* 262 */       return (TemplateModel)GetOptionalTemplateMethod.INSTANCE_CC;
/*     */     }
/* 264 */     if (this.name == "caller_template_name" || this.name == "callerTemplateName") {
/* 265 */       TemplateObject callPlace = (getRequiredMacroContext(env)).callPlace;
/* 266 */       String name = (callPlace != null) ? callPlace.getTemplate().getName() : null;
/* 267 */       return (name != null) ? (TemplateModel)new SimpleScalar(name) : TemplateScalarModel.EMPTY_STRING;
/*     */     } 
/* 269 */     if (this.name == "args") {
/* 270 */       TemplateModel args = getRequiredMacroContext(env).getArgsSpecialVariableValue();
/* 271 */       if (args == null)
/*     */       {
/* 273 */         throw new _MiscTemplateException(this, new Object[] { "The \"", "args", "\" special variable wasn't initialized.", this.name });
/*     */       }
/* 275 */       return args;
/*     */     } 
/* 277 */     if (this.name == "time_zone" || this.name == "timeZone") {
/* 278 */       return (TemplateModel)new SimpleScalar(env.getTimeZone().getID());
/*     */     }
/*     */     
/* 281 */     throw new _MiscTemplateException(this, new Object[] { "Invalid special variable: ", this.name });
/*     */   }
/*     */ 
/*     */   
/*     */   private Macro.Context getRequiredMacroContext(Environment env) throws TemplateException {
/* 286 */     Macro.Context ctx = env.getCurrentMacroContext();
/* 287 */     if (ctx == null) {
/* 288 */       throw new TemplateException("Can't get ." + this.name + " here, as there's no macro or function (that's implemented in the template) call in context.", env);
/*     */     }
/*     */ 
/*     */     
/* 292 */     return ctx;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 297 */     return "." + this.name;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getCanonicalForm() {
/* 302 */     return "." + this.name;
/*     */   }
/*     */ 
/*     */   
/*     */   String getNodeTypeSymbol() {
/* 307 */     return getCanonicalForm();
/*     */   }
/*     */ 
/*     */   
/*     */   boolean isLiteral() {
/* 312 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected Expression deepCloneWithIdentifierReplaced_inner(String replacedIdentifier, Expression replacement, Expression.ReplacemenetState replacementState) {
/* 318 */     return this;
/*     */   }
/*     */   
/*     */   static class VarsHash
/*     */     implements TemplateHashModel {
/*     */     Environment env;
/*     */     
/*     */     VarsHash(Environment env) {
/* 326 */       this.env = env;
/*     */     }
/*     */ 
/*     */     
/*     */     public TemplateModel get(String key) throws TemplateModelException {
/* 331 */       return this.env.getVariable(key);
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean isEmpty() {
/* 336 */       return false;
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   int getParameterCount() {
/* 342 */     return 0;
/*     */   }
/*     */ 
/*     */   
/*     */   Object getParameterValue(int idx) {
/* 347 */     throw new IndexOutOfBoundsException();
/*     */   }
/*     */ 
/*     */   
/*     */   ParameterRole getParameterRole(int idx) {
/* 352 */     throw new IndexOutOfBoundsException();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\BuiltinVariable.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */