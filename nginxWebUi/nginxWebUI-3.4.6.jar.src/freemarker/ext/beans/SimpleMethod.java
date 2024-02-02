/*     */ package freemarker.ext.beans;
/*     */ 
/*     */ import freemarker.core._DelayedFTLTypeDescription;
/*     */ import freemarker.core._DelayedOrdinal;
/*     */ import freemarker.core._ErrorDescriptionBuilder;
/*     */ import freemarker.core._TemplateModelException;
/*     */ import freemarker.template.ObjectWrapperAndUnwrapper;
/*     */ import freemarker.template.TemplateModel;
/*     */ import freemarker.template.TemplateModelException;
/*     */ import freemarker.template.utility.ClassUtil;
/*     */ import java.lang.reflect.Array;
/*     */ import java.lang.reflect.Member;
/*     */ import java.util.Collections;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
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
/*     */ class SimpleMethod
/*     */ {
/*     */   static final String MARKUP_OUTPUT_TO_STRING_TIP = "A markup output value can be converted to markup string like value?markup_string. But consider if the Java method whose argument it will be can handle markup strings properly.";
/*     */   private final Member member;
/*     */   private final Class[] argTypes;
/*     */   
/*     */   protected SimpleMethod(Member member, Class[] argTypes) {
/*  51 */     this.member = member;
/*  52 */     this.argTypes = argTypes;
/*     */   }
/*     */   
/*     */   Object[] unwrapArguments(List arguments, BeansWrapper wrapper) throws TemplateModelException {
/*  56 */     if (arguments == null) {
/*  57 */       arguments = Collections.EMPTY_LIST;
/*     */     }
/*  59 */     boolean isVarArg = _MethodUtil.isVarargs(this.member);
/*  60 */     int typesLen = this.argTypes.length;
/*  61 */     if (isVarArg) {
/*  62 */       if (typesLen - 1 > arguments.size())
/*  63 */         throw new _TemplateModelException(new Object[] {
/*  64 */               _MethodUtil.invocationErrorMessageStart(this.member), " takes at least ", 
/*  65 */               Integer.valueOf(typesLen - 1), (typesLen - 1 == 1) ? " argument" : " arguments", ", but ", 
/*     */               
/*  67 */               Integer.valueOf(arguments.size()), " was given."
/*     */             }); 
/*  69 */     } else if (typesLen != arguments.size()) {
/*  70 */       throw new _TemplateModelException(new Object[] {
/*  71 */             _MethodUtil.invocationErrorMessageStart(this.member), " takes ", 
/*  72 */             Integer.valueOf(typesLen), (typesLen == 1) ? " argument" : " arguments", ", but ", 
/*  73 */             Integer.valueOf(arguments.size()), " was given."
/*     */           });
/*     */     } 
/*  76 */     Object[] args = unwrapArguments(arguments, this.argTypes, isVarArg, wrapper);
/*  77 */     return args;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private Object[] unwrapArguments(List args, Class[] argTypes, boolean isVarargs, BeansWrapper w) throws TemplateModelException {
/*  83 */     if (args == null) return null;
/*     */     
/*  85 */     int typesLen = argTypes.length;
/*  86 */     int argsLen = args.size();
/*     */     
/*  88 */     Object[] unwrappedArgs = new Object[typesLen];
/*     */ 
/*     */     
/*  91 */     Iterator<TemplateModel> it = args.iterator();
/*  92 */     int normalArgCnt = isVarargs ? (typesLen - 1) : typesLen;
/*  93 */     int argIdx = 0;
/*  94 */     while (argIdx < normalArgCnt) {
/*  95 */       Class<?> argType = argTypes[argIdx];
/*  96 */       TemplateModel argVal = it.next();
/*  97 */       Object unwrappedArgVal = w.tryUnwrapTo(argVal, argType);
/*  98 */       if (unwrappedArgVal == ObjectWrapperAndUnwrapper.CANT_UNWRAP_TO_TARGET_CLASS) {
/*  99 */         throw createArgumentTypeMismarchException(argIdx, argVal, argType);
/*     */       }
/* 101 */       if (unwrappedArgVal == null && argType.isPrimitive()) {
/* 102 */         throw createNullToPrimitiveArgumentException(argIdx, argType);
/*     */       }
/*     */       
/* 105 */       unwrappedArgs[argIdx++] = unwrappedArgVal;
/*     */     } 
/* 107 */     if (isVarargs) {
/*     */ 
/*     */       
/* 110 */       Class<?> varargType = argTypes[typesLen - 1];
/* 111 */       Class<?> varargItemType = varargType.getComponentType();
/* 112 */       if (!it.hasNext()) {
/* 113 */         unwrappedArgs[argIdx++] = Array.newInstance(varargItemType, 0);
/*     */       } else {
/* 115 */         TemplateModel argVal = it.next();
/*     */ 
/*     */         
/*     */         Object unwrappedArgVal;
/*     */         
/* 120 */         if (argsLen - argIdx == 1 && (
/* 121 */           unwrappedArgVal = w.tryUnwrapTo(argVal, varargType)) != ObjectWrapperAndUnwrapper.CANT_UNWRAP_TO_TARGET_CLASS) {
/*     */ 
/*     */           
/* 124 */           unwrappedArgs[argIdx++] = unwrappedArgVal;
/*     */         }
/*     */         else {
/*     */           
/* 128 */           int varargArrayLen = argsLen - argIdx;
/* 129 */           Object varargArray = Array.newInstance(varargItemType, varargArrayLen);
/* 130 */           for (int varargIdx = 0; varargIdx < varargArrayLen; varargIdx++) {
/* 131 */             TemplateModel varargVal = (varargIdx == 0) ? argVal : it.next();
/* 132 */             Object unwrappedVarargVal = w.tryUnwrapTo(varargVal, varargItemType);
/* 133 */             if (unwrappedVarargVal == ObjectWrapperAndUnwrapper.CANT_UNWRAP_TO_TARGET_CLASS) {
/* 134 */               throw createArgumentTypeMismarchException(argIdx + varargIdx, varargVal, varargItemType);
/*     */             }
/*     */ 
/*     */             
/* 138 */             if (unwrappedVarargVal == null && varargItemType.isPrimitive()) {
/* 139 */               throw createNullToPrimitiveArgumentException(argIdx + varargIdx, varargItemType);
/*     */             }
/* 141 */             Array.set(varargArray, varargIdx, unwrappedVarargVal);
/*     */           } 
/* 143 */           unwrappedArgs[argIdx++] = varargArray;
/*     */         } 
/*     */       } 
/*     */     } 
/*     */     
/* 148 */     return unwrappedArgs;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private TemplateModelException createArgumentTypeMismarchException(int argIdx, TemplateModel argVal, Class targetType) {
/* 156 */     _ErrorDescriptionBuilder desc = new _ErrorDescriptionBuilder(new Object[] { _MethodUtil.invocationErrorMessageStart(this.member), " couldn't be called: Can't convert the ", new _DelayedOrdinal(Integer.valueOf(argIdx + 1)), " argument's value to the target Java type, ", ClassUtil.getShortClassName(targetType), ". The type of the actual value was: ", new _DelayedFTLTypeDescription(argVal) });
/*     */     
/* 158 */     if (argVal instanceof freemarker.core.TemplateMarkupOutputModel && targetType.isAssignableFrom(String.class)) {
/* 159 */       desc.tip("A markup output value can be converted to markup string like value?markup_string. But consider if the Java method whose argument it will be can handle markup strings properly.");
/*     */     }
/* 161 */     return (TemplateModelException)new _TemplateModelException(desc);
/*     */   }
/*     */   
/*     */   private TemplateModelException createNullToPrimitiveArgumentException(int argIdx, Class targetType) {
/* 165 */     return (TemplateModelException)new _TemplateModelException(new Object[] {
/* 166 */           _MethodUtil.invocationErrorMessageStart(this.member), " couldn't be called: The value of the ", new _DelayedOrdinal(
/* 167 */             Integer.valueOf(argIdx + 1)), " argument was null, but the target Java parameter type (", 
/* 168 */           ClassUtil.getShortClassName(targetType), ") is primitive and so can't store null."
/*     */         });
/*     */   }
/*     */   
/*     */   protected Member getMember() {
/* 173 */     return this.member;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\ext\beans\SimpleMethod.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */