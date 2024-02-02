/*     */ package freemarker.core;
/*     */ 
/*     */ import freemarker.template.ObjectWrapper;
/*     */ import freemarker.template.SimpleHash;
/*     */ import freemarker.template.SimpleSequence;
/*     */ import freemarker.template.TemplateException;
/*     */ import freemarker.template.TemplateHashModelEx;
/*     */ import freemarker.template.TemplateHashModelEx2;
/*     */ import freemarker.template.TemplateModel;
/*     */ import freemarker.template.TemplateModelException;
/*     */ import freemarker.template.TemplateModelIterator;
/*     */ import freemarker.template.TemplateScalarModel;
/*     */ import freemarker.template.TemplateSequenceModel;
/*     */ import freemarker.template._TemplateAPI;
/*     */ import freemarker.template.utility.Constants;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.HashSet;
/*     */ import java.util.LinkedHashMap;
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
/*     */ @Deprecated
/*     */ public final class Macro
/*     */   extends TemplateElement
/*     */   implements TemplateModel
/*     */ {
/*  51 */   static final Macro DO_NOTHING_MACRO = new Macro(".pass", Collections.EMPTY_MAP, null, false, false, TemplateElements.EMPTY);
/*     */ 
/*     */   
/*     */   static final int TYPE_MACRO = 0;
/*     */ 
/*     */   
/*     */   static final int TYPE_FUNCTION = 1;
/*     */ 
/*     */   
/*     */   private final String name;
/*     */   
/*     */   private final String[] paramNames;
/*     */   
/*     */   private final Map<String, Expression> paramNamesWithDefault;
/*     */   
/*     */   private final WithArgs withArgs;
/*     */   
/*     */   private boolean requireArgsSpecialVariable;
/*     */   
/*     */   private final String catchAllParamName;
/*     */   
/*     */   private final boolean function;
/*     */   
/*     */   private final Object namespaceLookupKey;
/*     */ 
/*     */   
/*     */   Macro(String name, Map<String, Expression> paramNamesWithDefault, String catchAllParamName, boolean function, boolean requireArgsSpecialVariable, TemplateElements children) {
/*  78 */     this.name = name;
/*  79 */     this.paramNamesWithDefault = paramNamesWithDefault;
/*  80 */     this.paramNames = (String[])paramNamesWithDefault.keySet().toArray((Object[])new String[0]);
/*  81 */     this.catchAllParamName = catchAllParamName;
/*  82 */     this.withArgs = null;
/*  83 */     this.requireArgsSpecialVariable = requireArgsSpecialVariable;
/*  84 */     this.function = function;
/*  85 */     setChildren(children);
/*  86 */     this.namespaceLookupKey = this;
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
/*     */   Macro(Macro that, WithArgs withArgs) {
/* 100 */     this.name = that.name;
/* 101 */     this.paramNamesWithDefault = that.paramNamesWithDefault;
/* 102 */     this.paramNames = that.paramNames;
/* 103 */     this.catchAllParamName = that.catchAllParamName;
/* 104 */     this.withArgs = withArgs;
/* 105 */     this.requireArgsSpecialVariable = that.requireArgsSpecialVariable;
/* 106 */     this.function = that.function;
/* 107 */     this.namespaceLookupKey = that.namespaceLookupKey;
/* 108 */     copyFieldsFrom(that);
/*     */   }
/*     */ 
/*     */   
/*     */   boolean getRequireArgsSpecialVariable() {
/* 113 */     return this.requireArgsSpecialVariable;
/*     */   }
/*     */   
/*     */   public String getCatchAll() {
/* 117 */     return this.catchAllParamName;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String[] getArgumentNames() {
/* 124 */     return (String[])this.paramNames.clone();
/*     */   }
/*     */   
/*     */   String[] getArgumentNamesNoCopy() {
/* 128 */     return this.paramNames;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean hasArgNamed(String name) {
/* 137 */     return this.paramNamesWithDefault.containsKey(name);
/*     */   }
/*     */   
/*     */   public String getName() {
/* 141 */     return this.name;
/*     */   }
/*     */ 
/*     */   
/*     */   public WithArgs getWithArgs() {
/* 146 */     return this.withArgs;
/*     */   }
/*     */   
/*     */   public Object getNamespaceLookupKey() {
/* 150 */     return this.namespaceLookupKey;
/*     */   }
/*     */ 
/*     */   
/*     */   TemplateElement[] accept(Environment env) {
/* 155 */     env.visitMacroDef(this);
/* 156 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   protected String dump(boolean canonical) {
/* 161 */     StringBuilder sb = new StringBuilder();
/* 162 */     if (canonical) sb.append('<'); 
/* 163 */     sb.append(getNodeTypeSymbol());
/* 164 */     if (this.withArgs != null)
/*     */     {
/* 166 */       sb.append('?')
/* 167 */         .append((getTemplate().getActualNamingConvention() == 12) ? "withArgs" : "with_args")
/*     */ 
/*     */         
/* 170 */         .append("(...)");
/*     */     }
/* 172 */     sb.append(' ');
/* 173 */     sb.append(_CoreStringUtils.toFTLTopLevelTragetIdentifier(this.name));
/* 174 */     if (this.function) sb.append('('); 
/* 175 */     int argCnt = this.paramNames.length;
/* 176 */     for (int i = 0; i < argCnt; i++) {
/* 177 */       if (this.function) {
/* 178 */         if (i != 0) {
/* 179 */           sb.append(", ");
/*     */         }
/*     */       } else {
/* 182 */         sb.append(' ');
/*     */       } 
/*     */       
/* 185 */       String paramName = this.paramNames[i];
/* 186 */       sb.append(_CoreStringUtils.toFTLTopLevelIdentifierReference(paramName));
/*     */       
/* 188 */       Expression paramDefaultExp = this.paramNamesWithDefault.get(paramName);
/* 189 */       if (paramDefaultExp != null) {
/* 190 */         sb.append('=');
/* 191 */         if (this.function) {
/* 192 */           sb.append(paramDefaultExp.getCanonicalForm());
/*     */         } else {
/* 194 */           _MessageUtil.appendExpressionAsUntearable(sb, paramDefaultExp);
/*     */         } 
/*     */       } 
/*     */     } 
/* 198 */     if (this.catchAllParamName != null) {
/* 199 */       if (this.function) {
/* 200 */         if (argCnt != 0) {
/* 201 */           sb.append(", ");
/*     */         }
/*     */       } else {
/* 204 */         sb.append(' ');
/*     */       } 
/* 206 */       sb.append(this.catchAllParamName);
/* 207 */       sb.append("...");
/*     */     } 
/* 209 */     if (this.function) sb.append(')'); 
/* 210 */     if (canonical) {
/* 211 */       sb.append('>');
/* 212 */       sb.append(getChildrenCanonicalForm());
/* 213 */       sb.append("</").append(getNodeTypeSymbol()).append('>');
/*     */     } 
/* 215 */     return sb.toString();
/*     */   }
/*     */ 
/*     */   
/*     */   String getNodeTypeSymbol() {
/* 220 */     return this.function ? "#function" : "#macro";
/*     */   }
/*     */   
/*     */   public boolean isFunction() {
/* 224 */     return this.function;
/*     */   }
/*     */   
/*     */   class Context
/*     */     implements LocalContext
/*     */   {
/*     */     final Environment.Namespace localVars;
/*     */     final TemplateObject callPlace;
/*     */     final Environment.Namespace nestedContentNamespace;
/*     */     final List<String> nestedContentParameterNames;
/*     */     final LocalContextStack prevLocalContextStack;
/*     */     final Context prevMacroContext;
/*     */     TemplateModel argsSpecialVariableValue;
/*     */     
/*     */     Context(Environment env, TemplateObject callPlace, List<String> nestedContentParameterNames) {
/* 239 */       env.getClass(); this.localVars = new Environment.Namespace(env);
/* 240 */       this.callPlace = callPlace;
/* 241 */       this.nestedContentNamespace = env.getCurrentNamespace();
/* 242 */       this.nestedContentParameterNames = nestedContentParameterNames;
/* 243 */       this.prevLocalContextStack = env.getLocalContextStack();
/* 244 */       this.prevMacroContext = env.getCurrentMacroContext();
/*     */     }
/*     */     
/*     */     Macro getMacro() {
/* 248 */       return Macro.this;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     void checkParamsSetAndApplyDefaults(Environment env) throws TemplateException {
/*     */       boolean resolvedADefaultValue;
/*     */       boolean hasUnresolvedDefaultValue;
/*     */       Expression firstUnresolvedDefaultValueExpression;
/*     */       InvalidReferenceException firstInvalidReferenceExceptionForDefaultValue;
/*     */       TemplateModel[] argsSpecVarDraft;
/* 261 */       if (Macro.this.requireArgsSpecialVariable) {
/* 262 */         argsSpecVarDraft = new TemplateModel[Macro.this.paramNames.length];
/*     */       } else {
/* 264 */         argsSpecVarDraft = null;
/*     */       } 
/*     */       do {
/* 267 */         firstUnresolvedDefaultValueExpression = null;
/* 268 */         firstInvalidReferenceExceptionForDefaultValue = null;
/* 269 */         resolvedADefaultValue = hasUnresolvedDefaultValue = false;
/* 270 */         for (int paramIndex = 0; paramIndex < Macro.this.paramNames.length; paramIndex++) {
/* 271 */           String argName = Macro.this.paramNames[paramIndex];
/* 272 */           TemplateModel argValue = this.localVars.get(argName);
/* 273 */           if (argValue == null) {
/* 274 */             Expression defaultValueExp = (Expression)Macro.this.paramNamesWithDefault.get(argName);
/* 275 */             if (defaultValueExp != null) {
/*     */               try {
/* 277 */                 TemplateModel defaultValue = defaultValueExp.eval(env);
/* 278 */                 if (defaultValue == null) {
/* 279 */                   if (!hasUnresolvedDefaultValue) {
/* 280 */                     firstUnresolvedDefaultValueExpression = defaultValueExp;
/* 281 */                     hasUnresolvedDefaultValue = true;
/*     */                   } 
/*     */                 } else {
/* 284 */                   this.localVars.put(argName, defaultValue);
/* 285 */                   resolvedADefaultValue = true;
/*     */                   
/* 287 */                   if (argsSpecVarDraft != null) {
/* 288 */                     argsSpecVarDraft[paramIndex] = defaultValue;
/*     */                   }
/*     */                 } 
/* 291 */               } catch (InvalidReferenceException e) {
/* 292 */                 if (!hasUnresolvedDefaultValue) {
/* 293 */                   hasUnresolvedDefaultValue = true;
/* 294 */                   firstInvalidReferenceExceptionForDefaultValue = e;
/*     */                 } 
/*     */               } 
/* 297 */             } else if (!env.isClassicCompatible()) {
/* 298 */               boolean argWasSpecified = this.localVars.containsKey(argName);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */               
/* 304 */               (new Object[1])[0] = "If the parameter value expression on the caller side is known to be legally null/missing, you may want to specify a default value for it with the \"!\" operator, like paramValue!defaultValue."; (new Object[3])[0] = "If the omission was deliberate, you may consider making the parameter optional in the macro by specifying a default value for it, like "; (new Object[3])[1] = "<#macro macroName paramName=defaultExpr>"; (new Object[3])[2] = ")"; throw new _MiscTemplateException(env, (new _ErrorDescriptionBuilder(new Object[] { "When calling ", this.this$0.isFunction() ? "function" : "macro", " ", new _DelayedJQuote(Macro.access$300(this.this$0)), ", required parameter ", new _DelayedJQuote(argName), " (parameter #", Integer.valueOf(paramIndex + 1), ") was ", argWasSpecified ? "specified, but had null/missing value." : "not specified."
/*     */ 
/*     */ 
/*     */                     
/* 308 */                     })).tip(argWasSpecified ? new Object[1] : new Object[3]));
/*     */ 
/*     */ 
/*     */ 
/*     */             
/*     */             }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */           
/*     */           }
/* 320 */           else if (argsSpecVarDraft != null) {
/*     */ 
/*     */             
/* 323 */             argsSpecVarDraft[paramIndex] = argValue;
/*     */           } 
/*     */         } 
/* 326 */       } while (hasUnresolvedDefaultValue && resolvedADefaultValue);
/* 327 */       if (hasUnresolvedDefaultValue) {
/* 328 */         if (firstInvalidReferenceExceptionForDefaultValue != null)
/* 329 */           throw firstInvalidReferenceExceptionForDefaultValue; 
/* 330 */         if (!env.isClassicCompatible()) {
/* 331 */           throw InvalidReferenceException.getInstance(firstUnresolvedDefaultValueExpression, env);
/*     */         }
/*     */       } 
/*     */       
/* 335 */       if (argsSpecVarDraft != null) {
/* 336 */         String catchAllParamName = (getMacro()).catchAllParamName;
/*     */         
/* 338 */         TemplateModel catchAllArgValue = (catchAllParamName != null) ? this.localVars.get(catchAllParamName) : null;
/*     */         
/* 340 */         if (getMacro().isFunction()) {
/* 341 */           int lengthWithCatchAlls = argsSpecVarDraft.length;
/* 342 */           if (catchAllArgValue != null) {
/* 343 */             lengthWithCatchAlls += ((TemplateSequenceModel)catchAllArgValue).size();
/*     */           }
/*     */           
/* 346 */           SimpleSequence argsSpecVarValue = new SimpleSequence(lengthWithCatchAlls, (ObjectWrapper)_TemplateAPI.SAFE_OBJECT_WRAPPER);
/*     */           
/* 348 */           for (int paramIndex = 0; paramIndex < argsSpecVarDraft.length; paramIndex++) {
/* 349 */             argsSpecVarValue.add(argsSpecVarDraft[paramIndex]);
/*     */           }
/* 351 */           if (catchAllParamName != null) {
/* 352 */             TemplateSequenceModel catchAllSeq = (TemplateSequenceModel)catchAllArgValue;
/* 353 */             int catchAllSize = catchAllSeq.size();
/* 354 */             for (int j = 0; j < catchAllSize; j++) {
/* 355 */               argsSpecVarValue.add(catchAllSeq.get(j));
/*     */             }
/*     */           } 
/* 358 */           assert argsSpecVarValue.size() == lengthWithCatchAlls;
/*     */           
/* 360 */           this.argsSpecialVariableValue = (TemplateModel)argsSpecVarValue;
/*     */         } else {
/* 362 */           TemplateHashModelEx2 catchAllHash; int lengthWithCatchAlls = argsSpecVarDraft.length;
/*     */           
/* 364 */           if (catchAllParamName != null) {
/* 365 */             if (catchAllArgValue instanceof TemplateSequenceModel) {
/* 366 */               if (((TemplateSequenceModel)catchAllArgValue).size() != 0) {
/* 367 */                 throw new _MiscTemplateException(new Object[] { "The macro can only by called with named arguments, because it uses both .", "args", " and a non-empty catch-all parameter." });
/*     */               }
/*     */ 
/*     */               
/* 371 */               catchAllHash = Constants.EMPTY_HASH_EX2;
/*     */             } else {
/* 373 */               catchAllHash = (TemplateHashModelEx2)catchAllArgValue;
/*     */             } 
/* 375 */             lengthWithCatchAlls += catchAllHash.size();
/*     */           } else {
/* 377 */             catchAllHash = null;
/*     */           } 
/*     */           
/* 380 */           SimpleHash argsSpecVarValue = new SimpleHash(new LinkedHashMap<>(lengthWithCatchAlls * 4 / 3, 1.0F), (ObjectWrapper)_TemplateAPI.SAFE_OBJECT_WRAPPER, 0);
/*     */ 
/*     */           
/* 383 */           for (int paramIndex = 0; paramIndex < argsSpecVarDraft.length; paramIndex++) {
/* 384 */             argsSpecVarValue.put(Macro.this.paramNames[paramIndex], argsSpecVarDraft[paramIndex]);
/*     */           }
/* 386 */           if (catchAllArgValue != null) {
/* 387 */             TemplateHashModelEx2.KeyValuePairIterator iter = catchAllHash.keyValuePairIterator();
/* 388 */             while (iter.hasNext()) {
/* 389 */               TemplateHashModelEx2.KeyValuePair kvp = iter.next();
/* 390 */               argsSpecVarValue.put(((TemplateScalarModel)kvp
/* 391 */                   .getKey()).getAsString(), kvp
/* 392 */                   .getValue());
/*     */             } 
/*     */           } 
/* 395 */           assert argsSpecVarValue.size() == lengthWithCatchAlls;
/*     */           
/* 397 */           this.argsSpecialVariableValue = (TemplateModel)argsSpecVarValue;
/*     */         } 
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/*     */     public TemplateModel getLocalVariable(String name) throws TemplateModelException {
/* 404 */       return this.localVars.get(name);
/*     */     }
/*     */     
/*     */     Environment.Namespace getLocals() {
/* 408 */       return this.localVars;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     void setLocalVar(String name, TemplateModel var) {
/* 415 */       this.localVars.put(name, var);
/*     */     }
/*     */ 
/*     */     
/*     */     public Collection getLocalVariableNames() throws TemplateModelException {
/* 420 */       HashSet<String> result = new HashSet();
/* 421 */       for (TemplateModelIterator it = this.localVars.keys().iterator(); it.hasNext();) {
/* 422 */         result.add(((TemplateScalarModel)it.next()).getAsString());
/*     */       }
/* 424 */       return result;
/*     */     }
/*     */     
/*     */     TemplateModel getArgsSpecialVariableValue() {
/* 428 */       return this.argsSpecialVariableValue;
/*     */     }
/*     */     
/*     */     void setArgsSpecialVariableValue(TemplateModel argsSpecialVariableValue) {
/* 432 */       this.argsSpecialVariableValue = argsSpecialVariableValue;
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   int getParameterCount() {
/* 438 */     return 1 + this.paramNames.length * 2 + 1 + 1;
/*     */   }
/*     */ 
/*     */   
/*     */   Object getParameterValue(int idx) {
/* 443 */     if (idx == 0) {
/* 444 */       return this.name;
/*     */     }
/* 446 */     int argDescsEnd = this.paramNames.length * 2 + 1;
/* 447 */     if (idx < argDescsEnd) {
/* 448 */       String paramName = this.paramNames[(idx - 1) / 2];
/* 449 */       if (idx % 2 != 0) {
/* 450 */         return paramName;
/*     */       }
/* 452 */       return this.paramNamesWithDefault.get(paramName);
/*     */     } 
/* 454 */     if (idx == argDescsEnd)
/* 455 */       return this.catchAllParamName; 
/* 456 */     if (idx == argDescsEnd + 1) {
/* 457 */       return Integer.valueOf(this.function ? 1 : 0);
/*     */     }
/* 459 */     throw new IndexOutOfBoundsException();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   ParameterRole getParameterRole(int idx) {
/* 466 */     if (idx == 0) {
/* 467 */       return ParameterRole.ASSIGNMENT_TARGET;
/*     */     }
/* 469 */     int argDescsEnd = this.paramNames.length * 2 + 1;
/* 470 */     if (idx < argDescsEnd) {
/* 471 */       if (idx % 2 != 0) {
/* 472 */         return ParameterRole.PARAMETER_NAME;
/*     */       }
/* 474 */       return ParameterRole.PARAMETER_DEFAULT;
/*     */     } 
/* 476 */     if (idx == argDescsEnd)
/* 477 */       return ParameterRole.CATCH_ALL_PARAMETER_NAME; 
/* 478 */     if (idx == argDescsEnd + 1) {
/* 479 */       return ParameterRole.AST_NODE_SUBTYPE;
/*     */     }
/* 481 */     throw new IndexOutOfBoundsException();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   boolean isNestedBlockRepeater() {
/* 489 */     return true;
/*     */   }
/*     */   
/*     */   static final class WithArgs {
/*     */     private final TemplateHashModelEx byName;
/*     */     private final TemplateSequenceModel byPosition;
/*     */     private final boolean orderLast;
/*     */     
/*     */     WithArgs(TemplateHashModelEx byName, boolean orderLast) {
/* 498 */       this.byName = byName;
/* 499 */       this.byPosition = null;
/* 500 */       this.orderLast = orderLast;
/*     */     }
/*     */     
/*     */     WithArgs(TemplateSequenceModel byPosition, boolean orderLast) {
/* 504 */       this.byName = null;
/* 505 */       this.byPosition = byPosition;
/* 506 */       this.orderLast = orderLast;
/*     */     }
/*     */     
/*     */     public TemplateHashModelEx getByName() {
/* 510 */       return this.byName;
/*     */     }
/*     */     
/*     */     public TemplateSequenceModel getByPosition() {
/* 514 */       return this.byPosition;
/*     */     }
/*     */     
/*     */     public boolean isOrderLast() {
/* 518 */       return this.orderLast;
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\Macro.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */