/*     */ package freemarker.core;
/*     */ 
/*     */ import freemarker.template.EmptyMap;
/*     */ import freemarker.template.TemplateDirectiveModel;
/*     */ import freemarker.template.TemplateException;
/*     */ import freemarker.template.TemplateModel;
/*     */ import freemarker.template.TemplateTransformModel;
/*     */ import freemarker.template.utility.ObjectFactory;
/*     */ import freemarker.template.utility.StringUtil;
/*     */ import java.io.IOException;
/*     */ import java.lang.ref.Reference;
/*     */ import java.lang.ref.SoftReference;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
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
/*     */ final class UnifiedCall
/*     */   extends TemplateElement
/*     */   implements DirectiveCallPlace
/*     */ {
/*     */   private Expression nameExp;
/*     */   private Map<String, ? extends Expression> namedArgs;
/*     */   private List<? extends Expression> positionalArgs;
/*     */   private List<String> bodyParameterNames;
/*     */   boolean legacySyntax;
/*     */   private volatile transient SoftReference sortedNamedArgsCache;
/*     */   private CustomDataHolder customDataHolder;
/*     */   
/*     */   UnifiedCall(Expression nameExp, Map<String, ? extends Expression> namedArgs, TemplateElements children, List<String> bodyParameterNames) {
/*  56 */     this.nameExp = nameExp;
/*  57 */     this.namedArgs = namedArgs;
/*  58 */     setChildren(children);
/*  59 */     this.bodyParameterNames = bodyParameterNames;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   UnifiedCall(Expression nameExp, List<? extends Expression> positionalArgs, TemplateElements children, List<String> bodyParameterNames) {
/*  66 */     this.nameExp = nameExp;
/*  67 */     this.positionalArgs = positionalArgs;
/*  68 */     setChildren(children);
/*  69 */     this.bodyParameterNames = bodyParameterNames;
/*     */   }
/*     */ 
/*     */   
/*     */   TemplateElement[] accept(Environment env) throws TemplateException, IOException {
/*  74 */     TemplateModel tm = this.nameExp.eval(env);
/*  75 */     if (tm == Macro.DO_NOTHING_MACRO) return null; 
/*  76 */     if (tm instanceof Macro) {
/*  77 */       Macro macro = (Macro)tm;
/*  78 */       if (macro.isFunction() && !this.legacySyntax) {
/*  79 */         throw new _MiscTemplateException(env, new Object[] { "Routine ", new _DelayedJQuote(macro
/*  80 */                 .getName()), " is a function, not a directive. Functions can only be called from expressions, like in ${f()}, ${x + f()} or ", "<@someDirective someParam=f() />", "." });
/*     */       }
/*     */ 
/*     */       
/*  84 */       env.invokeMacro(macro, this.namedArgs, this.positionalArgs, this.bodyParameterNames, this);
/*     */     } else {
/*  86 */       boolean isDirectiveModel = tm instanceof TemplateDirectiveModel;
/*  87 */       if (isDirectiveModel || tm instanceof TemplateTransformModel)
/*     */       { EmptyMap emptyMap;
/*  89 */         if (this.namedArgs != null && !this.namedArgs.isEmpty()) {
/*  90 */           Map<Object, Object> args = new HashMap<>();
/*  91 */           for (Iterator<Map.Entry> it = this.namedArgs.entrySet().iterator(); it.hasNext(); ) {
/*  92 */             Map.Entry entry = it.next();
/*  93 */             String key = (String)entry.getKey();
/*  94 */             Expression valueExp = (Expression)entry.getValue();
/*  95 */             TemplateModel value = valueExp.eval(env);
/*  96 */             args.put(key, value);
/*     */           } 
/*     */         } else {
/*  99 */           emptyMap = EmptyMap.instance;
/*     */         } 
/* 101 */         if (isDirectiveModel) {
/* 102 */           env.visit(getChildBuffer(), (TemplateDirectiveModel)tm, (Map)emptyMap, this.bodyParameterNames);
/*     */         } else {
/* 104 */           env.visitAndTransform(getChildBuffer(), (TemplateTransformModel)tm, (Map)emptyMap);
/*     */         }  }
/* 106 */       else { if (tm == null) {
/* 107 */           throw InvalidReferenceException.getInstance(this.nameExp, env);
/*     */         }
/* 109 */         throw new NonUserDefinedDirectiveLikeException(this.nameExp, tm, env); }
/*     */     
/*     */     } 
/* 112 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   protected String dump(boolean canonical) {
/* 117 */     StringBuilder sb = new StringBuilder();
/* 118 */     if (canonical) sb.append('<'); 
/* 119 */     sb.append('@');
/* 120 */     _MessageUtil.appendExpressionAsUntearable(sb, this.nameExp);
/* 121 */     boolean nameIsInParen = (sb.charAt(sb.length() - 1) == ')');
/* 122 */     if (this.positionalArgs != null) {
/* 123 */       for (int i = 0; i < this.positionalArgs.size(); i++) {
/* 124 */         Expression argExp = this.positionalArgs.get(i);
/* 125 */         if (i != 0) {
/* 126 */           sb.append(',');
/*     */         }
/* 128 */         sb.append(' ');
/* 129 */         sb.append(argExp.getCanonicalForm());
/*     */       } 
/*     */     } else {
/* 132 */       List<Map.Entry> entries = getSortedNamedArgs();
/* 133 */       for (int i = 0; i < entries.size(); i++) {
/* 134 */         Map.Entry entry = entries.get(i);
/* 135 */         Expression argExp = (Expression)entry.getValue();
/* 136 */         sb.append(' ');
/* 137 */         sb.append(_CoreStringUtils.toFTLTopLevelIdentifierReference((String)entry.getKey()));
/* 138 */         sb.append('=');
/* 139 */         _MessageUtil.appendExpressionAsUntearable(sb, argExp);
/*     */       } 
/*     */     } 
/* 142 */     if (this.bodyParameterNames != null && !this.bodyParameterNames.isEmpty()) {
/* 143 */       sb.append("; ");
/* 144 */       for (int i = 0; i < this.bodyParameterNames.size(); i++) {
/* 145 */         if (i != 0) {
/* 146 */           sb.append(", ");
/*     */         }
/* 148 */         sb.append(_CoreStringUtils.toFTLTopLevelIdentifierReference(this.bodyParameterNames.get(i)));
/*     */       } 
/*     */     } 
/* 151 */     if (canonical) {
/* 152 */       if (getChildCount() == 0) {
/* 153 */         sb.append("/>");
/*     */       } else {
/* 155 */         sb.append('>');
/* 156 */         sb.append(getChildrenCanonicalForm());
/* 157 */         sb.append("</@");
/* 158 */         if (!nameIsInParen && (this.nameExp instanceof Identifier || (this.nameExp instanceof Dot && ((Dot)this.nameExp)
/*     */           
/* 160 */           .onlyHasIdentifiers()))) {
/* 161 */           sb.append(this.nameExp.getCanonicalForm());
/*     */         }
/* 163 */         sb.append('>');
/*     */       } 
/*     */     }
/* 166 */     return sb.toString();
/*     */   }
/*     */ 
/*     */   
/*     */   String getNodeTypeSymbol() {
/* 171 */     return "@";
/*     */   }
/*     */ 
/*     */   
/*     */   int getParameterCount() {
/* 176 */     return 1 + ((this.positionalArgs != null) ? this.positionalArgs
/* 177 */       .size() : 0) + ((this.namedArgs != null) ? (this.namedArgs
/* 178 */       .size() * 2) : 0) + ((this.bodyParameterNames != null) ? this.bodyParameterNames
/* 179 */       .size() : 0);
/*     */   }
/*     */ 
/*     */   
/*     */   Object getParameterValue(int idx) {
/* 184 */     if (idx == 0) {
/* 185 */       return this.nameExp;
/*     */     }
/* 187 */     int base = 1;
/* 188 */     int positionalArgsSize = (this.positionalArgs != null) ? this.positionalArgs.size() : 0;
/* 189 */     if (idx - base < positionalArgsSize) {
/* 190 */       return this.positionalArgs.get(idx - base);
/*     */     }
/* 192 */     base += positionalArgsSize;
/* 193 */     int namedArgsSize = (this.namedArgs != null) ? this.namedArgs.size() : 0;
/* 194 */     if (idx - base < namedArgsSize * 2) {
/* 195 */       Map.Entry namedArg = getSortedNamedArgs().get((idx - base) / 2);
/* 196 */       return ((idx - base) % 2 == 0) ? namedArg.getKey() : namedArg.getValue();
/*     */     } 
/* 198 */     base += namedArgsSize * 2;
/* 199 */     int bodyParameterNamesSize = (this.bodyParameterNames != null) ? this.bodyParameterNames.size() : 0;
/* 200 */     if (idx - base < bodyParameterNamesSize) {
/* 201 */       return this.bodyParameterNames.get(idx - base);
/*     */     }
/* 203 */     throw new IndexOutOfBoundsException();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   ParameterRole getParameterRole(int idx) {
/* 212 */     if (idx == 0) {
/* 213 */       return ParameterRole.CALLEE;
/*     */     }
/* 215 */     int base = 1;
/* 216 */     int positionalArgsSize = (this.positionalArgs != null) ? this.positionalArgs.size() : 0;
/* 217 */     if (idx - base < positionalArgsSize) {
/* 218 */       return ParameterRole.ARGUMENT_VALUE;
/*     */     }
/* 220 */     base += positionalArgsSize;
/* 221 */     int namedArgsSize = (this.namedArgs != null) ? this.namedArgs.size() : 0;
/* 222 */     if (idx - base < namedArgsSize * 2) {
/* 223 */       return ((idx - base) % 2 == 0) ? ParameterRole.ARGUMENT_NAME : ParameterRole.ARGUMENT_VALUE;
/*     */     }
/* 225 */     base += namedArgsSize * 2;
/* 226 */     int bodyParameterNamesSize = (this.bodyParameterNames != null) ? this.bodyParameterNames.size() : 0;
/* 227 */     if (idx - base < bodyParameterNamesSize) {
/* 228 */       return ParameterRole.TARGET_LOOP_VARIABLE;
/*     */     }
/* 230 */     throw new IndexOutOfBoundsException();
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
/*     */   private List getSortedNamedArgs() {
/* 242 */     Reference<List> ref = this.sortedNamedArgsCache;
/* 243 */     if (ref != null) {
/* 244 */       List list = ref.get();
/* 245 */       if (list != null) return list;
/*     */     
/*     */     } 
/* 248 */     List res = MiscUtil.sortMapOfExpressions(this.namedArgs);
/* 249 */     this.sortedNamedArgsCache = new SoftReference<>(res);
/* 250 */     return res;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Object getOrCreateCustomData(Object providerIdentity, ObjectFactory objectFactory) throws CallPlaceCustomDataInitializationException {
/* 260 */     CustomDataHolder customDataHolder = this.customDataHolder;
/* 261 */     if (customDataHolder == null) {
/* 262 */       synchronized (this) {
/* 263 */         customDataHolder = this.customDataHolder;
/* 264 */         if (customDataHolder == null || customDataHolder.providerIdentity != providerIdentity) {
/* 265 */           customDataHolder = createNewCustomData(providerIdentity, objectFactory);
/* 266 */           this.customDataHolder = customDataHolder;
/*     */         } 
/*     */       } 
/*     */     }
/*     */     
/* 271 */     if (customDataHolder.providerIdentity != providerIdentity) {
/* 272 */       synchronized (this) {
/* 273 */         customDataHolder = this.customDataHolder;
/* 274 */         if (customDataHolder == null || customDataHolder.providerIdentity != providerIdentity) {
/* 275 */           customDataHolder = createNewCustomData(providerIdentity, objectFactory);
/* 276 */           this.customDataHolder = customDataHolder;
/*     */         } 
/*     */       } 
/*     */     }
/*     */     
/* 281 */     return customDataHolder.customData;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private CustomDataHolder createNewCustomData(Object provierIdentity, ObjectFactory objectFactory) throws CallPlaceCustomDataInitializationException {
/*     */     Object customData;
/*     */     try {
/* 289 */       customData = objectFactory.createObject();
/* 290 */     } catch (Exception e) {
/* 291 */       throw new CallPlaceCustomDataInitializationException("Failed to initialize custom data for provider identity " + 
/*     */           
/* 293 */           StringUtil.tryToString(provierIdentity) + " via factory " + 
/* 294 */           StringUtil.tryToString(objectFactory), e);
/*     */     } 
/* 296 */     if (customData == null) {
/* 297 */       throw new NullPointerException("ObjectFactory.createObject() has returned null");
/*     */     }
/* 299 */     CustomDataHolder customDataHolder = new CustomDataHolder(provierIdentity, customData);
/* 300 */     return customDataHolder;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isNestedOutputCacheable() {
/* 305 */     return isChildrenOutputCacheable();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static class CustomDataHolder
/*     */   {
/*     */     private final Object providerIdentity;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     private final Object customData;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public CustomDataHolder(Object providerIdentity, Object customData) {
/* 328 */       this.providerIdentity = providerIdentity;
/* 329 */       this.customData = customData;
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   boolean isNestedBlockRepeater() {
/* 336 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   boolean isShownInStackTrace() {
/* 341 */     return true;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\UnifiedCall.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */