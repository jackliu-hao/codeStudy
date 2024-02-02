/*     */ package freemarker.core;
/*     */ 
/*     */ import freemarker.template.Template;
/*     */ import freemarker.template.TemplateCollectionModel;
/*     */ import freemarker.template.TemplateDirectiveBody;
/*     */ import freemarker.template.TemplateException;
/*     */ import freemarker.template.TemplateModelException;
/*     */ import freemarker.template._TemplateAPI;
/*     */ import freemarker.template.utility.ClassUtil;
/*     */ import java.io.Writer;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.Set;
/*     */ import java.util.TreeSet;
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
/*     */ public class _CoreAPI
/*     */ {
/*     */   public static final String ERROR_MESSAGE_HR = "----";
/*     */   public static final Set<String> ALL_BUILT_IN_DIRECTIVE_NAMES;
/*     */   public static final Set<String> LEGACY_BUILT_IN_DIRECTIVE_NAMES;
/*     */   public static final Set<String> CAMEL_CASE_BUILT_IN_DIRECTIVE_NAMES;
/*     */   
/*     */   private static void addName(Set<String> allNames, Set<String> lcNames, Set<String> ccNames, String commonName) {
/*  53 */     allNames.add(commonName);
/*  54 */     lcNames.add(commonName);
/*  55 */     ccNames.add(commonName);
/*     */   }
/*     */ 
/*     */   
/*     */   private static void addName(Set<String> allNames, Set<String> lcNames, Set<String> ccNames, String lcName, String ccName) {
/*  60 */     allNames.add(lcName);
/*  61 */     allNames.add(ccName);
/*  62 */     lcNames.add(lcName);
/*  63 */     ccNames.add(ccName);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static {
/*  70 */     Set<String> allNames = new TreeSet<>();
/*  71 */     Set<String> lcNames = new TreeSet<>();
/*  72 */     Set<String> ccNames = new TreeSet<>();
/*     */     
/*  74 */     addName(allNames, lcNames, ccNames, "assign");
/*  75 */     addName(allNames, lcNames, ccNames, "attempt");
/*  76 */     addName(allNames, lcNames, ccNames, "autoesc", "autoEsc");
/*  77 */     addName(allNames, lcNames, ccNames, "break");
/*  78 */     addName(allNames, lcNames, ccNames, "call");
/*  79 */     addName(allNames, lcNames, ccNames, "case");
/*  80 */     addName(allNames, lcNames, ccNames, "comment");
/*  81 */     addName(allNames, lcNames, ccNames, "compress");
/*  82 */     addName(allNames, lcNames, ccNames, "continue");
/*  83 */     addName(allNames, lcNames, ccNames, "default");
/*  84 */     addName(allNames, lcNames, ccNames, "else");
/*  85 */     addName(allNames, lcNames, ccNames, "elseif", "elseIf");
/*  86 */     addName(allNames, lcNames, ccNames, "escape");
/*  87 */     addName(allNames, lcNames, ccNames, "fallback");
/*  88 */     addName(allNames, lcNames, ccNames, "flush");
/*  89 */     addName(allNames, lcNames, ccNames, "foreach", "forEach");
/*  90 */     addName(allNames, lcNames, ccNames, "ftl");
/*  91 */     addName(allNames, lcNames, ccNames, "function");
/*  92 */     addName(allNames, lcNames, ccNames, "global");
/*  93 */     addName(allNames, lcNames, ccNames, "if");
/*  94 */     addName(allNames, lcNames, ccNames, "import");
/*  95 */     addName(allNames, lcNames, ccNames, "include");
/*  96 */     addName(allNames, lcNames, ccNames, "items");
/*  97 */     addName(allNames, lcNames, ccNames, "list");
/*  98 */     addName(allNames, lcNames, ccNames, "local");
/*  99 */     addName(allNames, lcNames, ccNames, "lt");
/* 100 */     addName(allNames, lcNames, ccNames, "macro");
/* 101 */     addName(allNames, lcNames, ccNames, "nested");
/* 102 */     addName(allNames, lcNames, ccNames, "noautoesc", "noAutoEsc");
/* 103 */     addName(allNames, lcNames, ccNames, "noescape", "noEscape");
/* 104 */     addName(allNames, lcNames, ccNames, "noparse", "noParse");
/* 105 */     addName(allNames, lcNames, ccNames, "nt");
/* 106 */     addName(allNames, lcNames, ccNames, "outputformat", "outputFormat");
/* 107 */     addName(allNames, lcNames, ccNames, "recover");
/* 108 */     addName(allNames, lcNames, ccNames, "recurse");
/* 109 */     addName(allNames, lcNames, ccNames, "return");
/* 110 */     addName(allNames, lcNames, ccNames, "rt");
/* 111 */     addName(allNames, lcNames, ccNames, "sep");
/* 112 */     addName(allNames, lcNames, ccNames, "setting");
/* 113 */     addName(allNames, lcNames, ccNames, "stop");
/* 114 */     addName(allNames, lcNames, ccNames, "switch");
/* 115 */     addName(allNames, lcNames, ccNames, "t");
/* 116 */     addName(allNames, lcNames, ccNames, "transform");
/* 117 */     addName(allNames, lcNames, ccNames, "visit");
/*     */     
/* 119 */     ALL_BUILT_IN_DIRECTIVE_NAMES = Collections.unmodifiableSet(allNames);
/* 120 */     LEGACY_BUILT_IN_DIRECTIVE_NAMES = Collections.unmodifiableSet(lcNames);
/* 121 */     CAMEL_CASE_BUILT_IN_DIRECTIVE_NAMES = Collections.unmodifiableSet(ccNames);
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
/*     */   public static Set<String> getSupportedBuiltInNames(int namingConvention) {
/*     */     Set<String> names;
/* 136 */     if (namingConvention == 10) {
/* 137 */       names = BuiltIn.BUILT_INS_BY_NAME.keySet();
/* 138 */     } else if (namingConvention == 11) {
/* 139 */       names = BuiltIn.SNAKE_CASE_NAMES;
/* 140 */     } else if (namingConvention == 12) {
/* 141 */       names = BuiltIn.CAMEL_CASE_NAMES;
/*     */     } else {
/* 143 */       throw new IllegalArgumentException("Unsupported naming convention constant: " + namingConvention);
/*     */     } 
/* 145 */     return Collections.unmodifiableSet(names);
/*     */   }
/*     */   
/*     */   public static void appendInstructionStackItem(TemplateElement stackEl, StringBuilder sb) {
/* 149 */     Environment.appendInstructionStackItem(stackEl, sb);
/*     */   }
/*     */   
/*     */   public static TemplateElement[] getInstructionStackSnapshot(Environment env) {
/* 153 */     return env.getInstructionStackSnapshot();
/*     */   }
/*     */ 
/*     */   
/*     */   public static void outputInstructionStack(TemplateElement[] instructionStackSnapshot, boolean terseMode, Writer pw) {
/* 158 */     Environment.outputInstructionStack(instructionStackSnapshot, terseMode, pw);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final void addThreadInterruptedChecks(Template template) {
/*     */     try {
/* 167 */       (new ThreadInterruptionSupportTemplatePostProcessor()).postProcess(template);
/* 168 */     } catch (TemplatePostProcessorException e) {
/* 169 */       throw new RuntimeException("Template post-processing failed", e);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public static final void checkHasNoNestedContent(TemplateDirectiveBody body) throws NestedContentNotSupportedException {
/* 175 */     NestedContentNotSupportedException.check(body);
/*     */   }
/*     */   
/*     */   public static final void replaceText(TextBlock textBlock, String text) {
/* 179 */     textBlock.replaceText(text);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void checkSettingValueItemsType(String somethingsSentenceStart, Class<?> expectedClass, Collection<?> values) {
/* 188 */     if (values == null)
/* 189 */       return;  for (Object value : values) {
/* 190 */       if (!expectedClass.isInstance(value)) {
/* 191 */         throw new IllegalArgumentException(somethingsSentenceStart + " must be instances of " + 
/* 192 */             ClassUtil.getShortClassName(expectedClass) + ", but one of them was a(n) " + 
/* 193 */             ClassUtil.getShortClassNameOfObject(value) + ".");
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static TemplateModelException ensureIsTemplateModelException(String modelOpMsg, TemplateException e) {
/* 203 */     if (e instanceof TemplateModelException) {
/* 204 */       return (TemplateModelException)e;
/*     */     }
/* 206 */     return new _TemplateModelException(
/* 207 */         _TemplateAPI.getBlamedExpression(e), e.getCause(), e.getEnvironment(), modelOpMsg);
/*     */   }
/*     */ 
/*     */   
/*     */   public static TemplateElement getParentElement(TemplateElement te) {
/* 212 */     return te.getParentElement();
/*     */   }
/*     */   
/*     */   public static TemplateElement getChildElement(TemplateElement te, int index) {
/* 216 */     return te.getChild(index);
/*     */   }
/*     */   
/*     */   public static void setPreventStrippings(FMParser parser, boolean preventStrippings) {
/* 220 */     parser.setPreventStrippings(preventStrippings);
/*     */   }
/*     */   
/*     */   public static boolean isLazilyGeneratedSequenceModel(TemplateCollectionModel model) {
/* 224 */     return (model instanceof LazilyGeneratedCollectionModel && ((LazilyGeneratedCollectionModel)model).isSequence());
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\_CoreAPI.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */