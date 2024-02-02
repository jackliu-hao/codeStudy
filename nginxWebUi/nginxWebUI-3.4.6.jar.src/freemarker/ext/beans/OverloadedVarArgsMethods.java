/*     */ package freemarker.ext.beans;
/*     */ 
/*     */ import freemarker.core.BugException;
/*     */ import freemarker.template.ObjectWrapperAndUnwrapper;
/*     */ import freemarker.template.TemplateModel;
/*     */ import freemarker.template.TemplateModelException;
/*     */ import java.lang.reflect.Array;
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
/*     */ class OverloadedVarArgsMethods
/*     */   extends OverloadedMethodsSubset
/*     */ {
/*     */   OverloadedVarArgsMethods(boolean bugfixed) {
/*  37 */     super(bugfixed);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   Class[] preprocessParameterTypes(CallableMemberDescriptor memberDesc) {
/*  45 */     Class[] preprocessedParamTypes = (Class[])memberDesc.getParamTypes().clone();
/*  46 */     int ln = preprocessedParamTypes.length;
/*  47 */     Class<?> varArgsCompType = preprocessedParamTypes[ln - 1].getComponentType();
/*  48 */     if (varArgsCompType == null) {
/*  49 */       throw new BugException("Only varargs methods should be handled here");
/*     */     }
/*  51 */     preprocessedParamTypes[ln - 1] = varArgsCompType;
/*  52 */     return preprocessedParamTypes;
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
/*     */ 
/*     */ 
/*     */   
/*     */   void afterWideningUnwrappingHints(Class[] paramTypes, int[] paramNumericalTypes) {
/*  69 */     int paramCount = paramTypes.length;
/*  70 */     Class[][] unwrappingHintsByParamCount = getUnwrappingHintsByParamCount();
/*     */ 
/*     */ 
/*     */     
/*     */     int i;
/*     */ 
/*     */     
/*  77 */     for (i = paramCount - 1; i >= 0; i--) {
/*  78 */       Class[] previousHints = unwrappingHintsByParamCount[i];
/*  79 */       if (previousHints != null) {
/*  80 */         widenHintsToCommonSupertypes(paramCount, previousHints, 
/*     */             
/*  82 */             getTypeFlags(i));
/*     */ 
/*     */         
/*     */         break;
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/*  90 */     if (paramCount + 1 < unwrappingHintsByParamCount.length) {
/*  91 */       Class[] oneLongerHints = unwrappingHintsByParamCount[paramCount + 1];
/*  92 */       if (oneLongerHints != null) {
/*  93 */         widenHintsToCommonSupertypes(paramCount, oneLongerHints, 
/*     */             
/*  95 */             getTypeFlags(paramCount + 1));
/*     */       }
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 101 */     for (i = paramCount + 1; i < unwrappingHintsByParamCount.length; i++) {
/* 102 */       widenHintsToCommonSupertypes(i, paramTypes, paramNumericalTypes);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 108 */     if (paramCount > 0) {
/* 109 */       widenHintsToCommonSupertypes(paramCount - 1, paramTypes, paramNumericalTypes);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void widenHintsToCommonSupertypes(int paramCountOfWidened, Class[] wideningTypes, int[] wideningTypeFlags) {
/* 118 */     Class[] typesToWiden = getUnwrappingHintsByParamCount()[paramCountOfWidened];
/* 119 */     if (typesToWiden == null) {
/*     */       return;
/*     */     }
/*     */     
/* 123 */     int typesToWidenLen = typesToWiden.length;
/* 124 */     int wideningTypesLen = wideningTypes.length;
/* 125 */     int min = Math.min(wideningTypesLen, typesToWidenLen);
/* 126 */     for (int i = 0; i < min; i++) {
/* 127 */       typesToWiden[i] = getCommonSupertypeForUnwrappingHint(typesToWiden[i], wideningTypes[i]);
/*     */     }
/* 129 */     if (typesToWidenLen > wideningTypesLen) {
/* 130 */       Class varargsComponentType = wideningTypes[wideningTypesLen - 1];
/* 131 */       for (int j = wideningTypesLen; j < typesToWidenLen; j++) {
/* 132 */         typesToWiden[j] = getCommonSupertypeForUnwrappingHint(typesToWiden[j], varargsComponentType);
/*     */       }
/*     */     } 
/*     */     
/* 136 */     if (this.bugfixed) {
/* 137 */       mergeInTypesFlags(paramCountOfWidened, wideningTypeFlags);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   MaybeEmptyMemberAndArguments getMemberAndArguments(List tmArgs, BeansWrapper unwrapper) throws TemplateModelException {
/* 144 */     if (tmArgs == null)
/*     */     {
/* 146 */       tmArgs = Collections.EMPTY_LIST;
/*     */     }
/* 148 */     int argsLen = tmArgs.size();
/* 149 */     Class[][] unwrappingHintsByParamCount = getUnwrappingHintsByParamCount();
/* 150 */     Object[] pojoArgs = new Object[argsLen];
/* 151 */     int[] typesFlags = null;
/*     */     
/*     */     int paramCount;
/* 154 */     label46: for (paramCount = Math.min(argsLen + 1, unwrappingHintsByParamCount.length - 1); paramCount >= 0; paramCount--) {
/* 155 */       Class[] unwarappingHints = unwrappingHintsByParamCount[paramCount];
/* 156 */       if (unwarappingHints == null) {
/* 157 */         if (paramCount == 0) {
/* 158 */           return EmptyMemberAndArguments.WRONG_NUMBER_OF_ARGUMENTS;
/*     */         }
/*     */       }
/*     */       else {
/*     */         
/* 163 */         typesFlags = getTypeFlags(paramCount);
/* 164 */         if (typesFlags == ALL_ZEROS_ARRAY) {
/* 165 */           typesFlags = null;
/*     */         }
/*     */ 
/*     */         
/* 169 */         Iterator<TemplateModel> it = tmArgs.iterator();
/* 170 */         for (int i = 0; i < argsLen; i++) {
/* 171 */           int paramIdx = (i < paramCount) ? i : (paramCount - 1);
/* 172 */           Object pojo = unwrapper.tryUnwrapTo(it
/* 173 */               .next(), unwarappingHints[paramIdx], (typesFlags != null) ? typesFlags[paramIdx] : 0);
/*     */ 
/*     */           
/* 176 */           if (pojo == ObjectWrapperAndUnwrapper.CANT_UNWRAP_TO_TARGET_CLASS) {
/*     */             continue label46;
/*     */           }
/* 179 */           pojoArgs[i] = pojo;
/*     */         } 
/*     */         break;
/*     */       } 
/*     */     } 
/* 184 */     MaybeEmptyCallableMemberDescriptor maybeEmtpyMemberDesc = getMemberDescriptorForArgs(pojoArgs, true);
/* 185 */     if (maybeEmtpyMemberDesc instanceof CallableMemberDescriptor) {
/* 186 */       Object[] pojoArgsWithArray; CallableMemberDescriptor memberDesc = (CallableMemberDescriptor)maybeEmtpyMemberDesc;
/*     */       
/* 188 */       Object argsOrErrorIdx = replaceVarargsSectionWithArray(pojoArgs, tmArgs, memberDesc, unwrapper);
/* 189 */       if (argsOrErrorIdx instanceof Object[]) {
/* 190 */         pojoArgsWithArray = (Object[])argsOrErrorIdx;
/*     */       } else {
/* 192 */         return EmptyMemberAndArguments.noCompatibleOverload(((Integer)argsOrErrorIdx).intValue());
/*     */       } 
/* 194 */       if (this.bugfixed) {
/* 195 */         if (typesFlags != null)
/*     */         {
/*     */ 
/*     */           
/* 199 */           forceNumberArgumentsToParameterTypes(pojoArgsWithArray, memberDesc.getParamTypes(), typesFlags);
/*     */         }
/*     */       } else {
/* 202 */         BeansWrapper.coerceBigDecimals(memberDesc.getParamTypes(), pojoArgsWithArray);
/*     */       } 
/* 204 */       return new MemberAndArguments(memberDesc, pojoArgsWithArray);
/*     */     } 
/* 206 */     return EmptyMemberAndArguments.from((EmptyCallableMemberDescriptor)maybeEmtpyMemberDesc, pojoArgs);
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
/*     */   
/*     */   private Object replaceVarargsSectionWithArray(Object[] args, List<TemplateModel> modelArgs, CallableMemberDescriptor memberDesc, BeansWrapper unwrapper) throws TemplateModelException {
/* 221 */     Class[] paramTypes = memberDesc.getParamTypes();
/* 222 */     int paramCount = paramTypes.length;
/* 223 */     Class<?> varArgsCompType = paramTypes[paramCount - 1].getComponentType();
/* 224 */     int totalArgCount = args.length;
/* 225 */     int fixArgCount = paramCount - 1;
/* 226 */     if (args.length != paramCount) {
/* 227 */       Object[] packedArgs = new Object[paramCount];
/* 228 */       System.arraycopy(args, 0, packedArgs, 0, fixArgCount);
/* 229 */       Object varargs = Array.newInstance(varArgsCompType, totalArgCount - fixArgCount);
/* 230 */       for (int i = fixArgCount; i < totalArgCount; i++) {
/* 231 */         Object object = unwrapper.tryUnwrapTo(modelArgs.get(i), varArgsCompType);
/* 232 */         if (object == ObjectWrapperAndUnwrapper.CANT_UNWRAP_TO_TARGET_CLASS) {
/* 233 */           return Integer.valueOf(i + 1);
/*     */         }
/* 235 */         Array.set(varargs, i - fixArgCount, object);
/*     */       } 
/* 237 */       packedArgs[fixArgCount] = varargs;
/* 238 */       return packedArgs;
/*     */     } 
/* 240 */     Object val = unwrapper.tryUnwrapTo(modelArgs.get(fixArgCount), varArgsCompType);
/* 241 */     if (val == ObjectWrapperAndUnwrapper.CANT_UNWRAP_TO_TARGET_CLASS) {
/* 242 */       return Integer.valueOf(fixArgCount + 1);
/*     */     }
/* 244 */     Object array = Array.newInstance(varArgsCompType, 1);
/* 245 */     Array.set(array, 0, val);
/* 246 */     args[fixArgCount] = array;
/* 247 */     return args;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\ext\beans\OverloadedVarArgsMethods.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */