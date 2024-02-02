/*     */ package freemarker.core;
/*     */ 
/*     */ import freemarker.template.MalformedTemplateNameException;
/*     */ import freemarker.template.ObjectWrapper;
/*     */ import freemarker.template.SimpleHash;
/*     */ import freemarker.template.Template;
/*     */ import freemarker.template.TemplateBooleanModel;
/*     */ import freemarker.template.TemplateDirectiveBody;
/*     */ import freemarker.template.TemplateDirectiveModel;
/*     */ import freemarker.template.TemplateException;
/*     */ import freemarker.template.TemplateHashModelEx;
/*     */ import freemarker.template.TemplateHashModelEx2;
/*     */ import freemarker.template.TemplateMethodModelEx;
/*     */ import freemarker.template.TemplateModel;
/*     */ import freemarker.template.TemplateModelException;
/*     */ import freemarker.template.TemplateScalarModel;
/*     */ import freemarker.template._TemplateAPI;
/*     */ import freemarker.template.utility.TemplateModelUtils;
/*     */ import java.io.IOException;
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
/*     */ class GetOptionalTemplateMethod
/*     */   implements TemplateMethodModelEx
/*     */ {
/*  48 */   static final GetOptionalTemplateMethod INSTANCE = new GetOptionalTemplateMethod("get_optional_template");
/*     */   
/*  50 */   static final GetOptionalTemplateMethod INSTANCE_CC = new GetOptionalTemplateMethod("getOptionalTemplate");
/*     */   
/*     */   private static final String OPTION_ENCODING = "encoding";
/*     */   
/*     */   private static final String OPTION_PARSE = "parse";
/*     */   
/*     */   private static final String RESULT_INCLUDE = "include";
/*     */   
/*     */   private static final String RESULT_IMPORT = "import";
/*     */   
/*     */   private static final String RESULT_EXISTS = "exists";
/*     */   private final String methodName;
/*     */   
/*     */   private GetOptionalTemplateMethod(String builtInVarName) {
/*  64 */     this.methodName = "." + builtInVarName;
/*     */   } public Object exec(List<TemplateModel> args) throws TemplateModelException {
/*     */     String absTemplateName;
/*     */     TemplateHashModelEx options;
/*     */     final Template template;
/*  69 */     int argCnt = args.size();
/*  70 */     if (argCnt < 1 || argCnt > 2) {
/*  71 */       throw _MessageUtil.newArgCntError(this.methodName, argCnt, 1, 2);
/*     */     }
/*     */     
/*  74 */     final Environment env = Environment.getCurrentEnvironment();
/*  75 */     if (env == null) {
/*  76 */       throw new IllegalStateException("No freemarer.core.Environment is associated to the current thread.");
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*  81 */     TemplateModel arg = args.get(0);
/*  82 */     if (!(arg instanceof TemplateScalarModel)) {
/*  83 */       throw _MessageUtil.newMethodArgMustBeStringException(this.methodName, 0, arg);
/*     */     }
/*  85 */     String templateName = EvalUtil.modelToString((TemplateScalarModel)arg, null, env);
/*     */     
/*     */     try {
/*  88 */       absTemplateName = env.toFullTemplateName(env.getCurrentTemplate().getName(), templateName);
/*  89 */     } catch (MalformedTemplateNameException e) {
/*  90 */       throw new _TemplateModelException(e, "Failed to convert template path to full path; see cause exception.");
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  96 */     if (argCnt > 1) {
/*  97 */       TemplateModel templateModel = args.get(1);
/*  98 */       if (!(templateModel instanceof TemplateHashModelEx)) {
/*  99 */         throw _MessageUtil.newMethodArgMustBeExtendedHashException(this.methodName, 1, templateModel);
/*     */       }
/* 101 */       options = (TemplateHashModelEx)templateModel;
/*     */     } else {
/* 103 */       options = null;
/*     */     } 
/*     */     
/* 106 */     String encoding = null;
/* 107 */     boolean parse = true;
/* 108 */     if (options != null) {
/* 109 */       TemplateHashModelEx2.KeyValuePairIterator kvpi = TemplateModelUtils.getKeyValuePairIterator(options);
/* 110 */       while (kvpi.hasNext()) {
/* 111 */         TemplateHashModelEx2.KeyValuePair kvp = kvpi.next();
/*     */ 
/*     */ 
/*     */         
/* 115 */         TemplateModel optNameTM = kvp.getKey();
/* 116 */         if (!(optNameTM instanceof TemplateScalarModel)) {
/* 117 */           throw _MessageUtil.newMethodArgInvalidValueException(this.methodName, 1, new Object[] { "All keys in the options hash must be strings, but found ", new _DelayedAOrAn(new _DelayedFTLTypeDescription(optNameTM)) });
/*     */         }
/*     */ 
/*     */         
/* 121 */         String optName = ((TemplateScalarModel)optNameTM).getAsString();
/*     */ 
/*     */         
/* 124 */         TemplateModel optValue = kvp.getValue();
/*     */         
/* 126 */         if ("encoding".equals(optName)) {
/* 127 */           encoding = getStringOption("encoding", optValue); continue;
/* 128 */         }  if ("parse".equals(optName)) {
/* 129 */           parse = getBooleanOption("parse", optValue); continue;
/*     */         } 
/* 131 */         throw _MessageUtil.newMethodArgInvalidValueException(this.methodName, 1, new Object[] { "Unsupported option ", new _DelayedJQuote(optName), "; valid names are: ", new _DelayedJQuote("encoding"), ", ", new _DelayedJQuote("parse"), "." });
/*     */       } 
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     try {
/* 140 */       template = env.getTemplateForInclusion(absTemplateName, encoding, parse, true);
/* 141 */     } catch (IOException e) {
/* 142 */       throw new _TemplateModelException(e, new Object[] { "I/O error when trying to load optional template ", new _DelayedJQuote(absTemplateName), "; see cause exception" });
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 147 */     SimpleHash result = new SimpleHash((ObjectWrapper)_TemplateAPI.SAFE_OBJECT_WRAPPER);
/* 148 */     result.put("exists", (template != null));
/*     */ 
/*     */     
/* 151 */     if (template != null) {
/* 152 */       result.put("include", new TemplateDirectiveModel()
/*     */           {
/*     */             public void execute(Environment env, Map params, TemplateModel[] loopVars, TemplateDirectiveBody body) throws TemplateException, IOException
/*     */             {
/* 156 */               if (!params.isEmpty()) {
/* 157 */                 throw new TemplateException("This directive supports no parameters.", env);
/*     */               }
/* 159 */               if (loopVars.length != 0) {
/* 160 */                 throw new TemplateException("This directive supports no loop variables.", env);
/*     */               }
/* 162 */               if (body != null) {
/* 163 */                 throw new TemplateException("This directive supports no nested content.", env);
/*     */               }
/*     */               
/* 166 */               env.include(template);
/*     */             }
/*     */           });
/* 169 */       result.put("import", new TemplateMethodModelEx()
/*     */           {
/*     */             public Object exec(List args) throws TemplateModelException {
/* 172 */               if (!args.isEmpty()) {
/* 173 */                 throw new TemplateModelException("This method supports no parameters.");
/*     */               }
/*     */               
/*     */               try {
/* 177 */                 return env.importLib(template, (String)null);
/* 178 */               } catch (IOException|TemplateException e) {
/* 179 */                 throw new _TemplateModelException(e, "Failed to import loaded template; see cause exception");
/*     */               } 
/*     */             }
/*     */           });
/*     */     } 
/* 184 */     return result;
/*     */   }
/*     */   
/*     */   private boolean getBooleanOption(String optionName, TemplateModel value) throws TemplateModelException {
/* 188 */     if (!(value instanceof TemplateBooleanModel)) {
/* 189 */       throw _MessageUtil.newMethodArgInvalidValueException(this.methodName, 1, new Object[] { "The value of the ", new _DelayedJQuote(optionName), " option must be a boolean, but it was ", new _DelayedAOrAn(new _DelayedFTLTypeDescription(value)), "." });
/*     */     }
/*     */ 
/*     */     
/* 193 */     return ((TemplateBooleanModel)value).getAsBoolean();
/*     */   }
/*     */   
/*     */   private String getStringOption(String optionName, TemplateModel value) throws TemplateModelException {
/* 197 */     if (!(value instanceof TemplateScalarModel)) {
/* 198 */       throw _MessageUtil.newMethodArgInvalidValueException(this.methodName, 1, new Object[] { "The value of the ", new _DelayedJQuote(optionName), " option must be a string, but it was ", new _DelayedAOrAn(new _DelayedFTLTypeDescription(value)), "." });
/*     */     }
/*     */ 
/*     */     
/* 202 */     return EvalUtil.modelToString((TemplateScalarModel)value, null, null);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\GetOptionalTemplateMethod.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */