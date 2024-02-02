/*     */ package freemarker.ext.beans;
/*     */ 
/*     */ import freemarker.template.TemplateModelException;
/*     */ import freemarker.template.utility.ClassUtil;
/*     */ import freemarker.template.utility.NullArgumentException;
/*     */ import java.io.Serializable;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Iterator;
/*     */ import java.util.LinkedList;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.concurrent.ConcurrentHashMap;
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
/*     */ abstract class OverloadedMethodsSubset
/*     */ {
/*  47 */   static final int[] ALL_ZEROS_ARRAY = new int[0];
/*     */   
/*  49 */   private static final int[][] ZERO_PARAM_COUNT_TYPE_FLAGS_ARRAY = new int[1][];
/*     */   static {
/*  51 */     ZERO_PARAM_COUNT_TYPE_FLAGS_ARRAY[0] = ALL_ZEROS_ARRAY;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private Class[][] unwrappingHintsByParamCount;
/*     */ 
/*     */   
/*     */   private int[][] typeFlagsByParamCount;
/*     */ 
/*     */   
/*  63 */   private final Map argTypesToMemberDescCache = new ConcurrentHashMap<>(6, 0.75F, 1);
/*     */ 
/*     */   
/*  66 */   private final List memberDescs = new LinkedList();
/*     */   
/*     */   protected final boolean bugfixed;
/*     */ 
/*     */   
/*     */   OverloadedMethodsSubset(boolean bugfixed) {
/*  72 */     this.bugfixed = bugfixed;
/*     */   }
/*     */   
/*     */   void addCallableMemberDescriptor(ReflectionCallableMemberDescriptor memberDesc) {
/*  76 */     this.memberDescs.add(memberDesc);
/*     */ 
/*     */ 
/*     */     
/*  80 */     Class[] prepedParamTypes = preprocessParameterTypes(memberDesc);
/*  81 */     int paramCount = prepedParamTypes.length;
/*     */ 
/*     */     
/*  84 */     if (this.unwrappingHintsByParamCount == null) {
/*  85 */       this.unwrappingHintsByParamCount = new Class[paramCount + 1][];
/*  86 */       this.unwrappingHintsByParamCount[paramCount] = (Class[])prepedParamTypes.clone();
/*  87 */     } else if (this.unwrappingHintsByParamCount.length <= paramCount) {
/*  88 */       Class[][] newUnwrappingHintsByParamCount = new Class[paramCount + 1][];
/*  89 */       System.arraycopy(this.unwrappingHintsByParamCount, 0, newUnwrappingHintsByParamCount, 0, this.unwrappingHintsByParamCount.length);
/*     */       
/*  91 */       this.unwrappingHintsByParamCount = newUnwrappingHintsByParamCount;
/*  92 */       this.unwrappingHintsByParamCount[paramCount] = (Class[])prepedParamTypes.clone();
/*     */     } else {
/*  94 */       Class[] unwrappingHints = this.unwrappingHintsByParamCount[paramCount];
/*  95 */       if (unwrappingHints == null) {
/*  96 */         this.unwrappingHintsByParamCount[paramCount] = (Class[])prepedParamTypes.clone();
/*     */       } else {
/*  98 */         for (int paramIdx = 0; paramIdx < prepedParamTypes.length; paramIdx++)
/*     */         {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */           
/* 106 */           unwrappingHints[paramIdx] = getCommonSupertypeForUnwrappingHint(unwrappingHints[paramIdx], prepedParamTypes[paramIdx]);
/*     */         }
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/* 112 */     int[] typeFlagsByParamIdx = ALL_ZEROS_ARRAY;
/* 113 */     if (this.bugfixed) {
/*     */       
/* 115 */       for (int paramIdx = 0; paramIdx < paramCount; paramIdx++) {
/* 116 */         int typeFlags = TypeFlags.classToTypeFlags(prepedParamTypes[paramIdx]);
/* 117 */         if (typeFlags != 0) {
/* 118 */           if (typeFlagsByParamIdx == ALL_ZEROS_ARRAY) {
/* 119 */             typeFlagsByParamIdx = new int[paramCount];
/*     */           }
/* 121 */           typeFlagsByParamIdx[paramIdx] = typeFlags;
/*     */         } 
/*     */       } 
/* 124 */       mergeInTypesFlags(paramCount, typeFlagsByParamIdx);
/*     */     } 
/*     */     
/* 127 */     afterWideningUnwrappingHints(this.bugfixed ? prepedParamTypes : this.unwrappingHintsByParamCount[paramCount], typeFlagsByParamIdx);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   Class[][] getUnwrappingHintsByParamCount() {
/* 133 */     return this.unwrappingHintsByParamCount;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   final MaybeEmptyCallableMemberDescriptor getMemberDescriptorForArgs(Object[] args, boolean varArg) {
/* 139 */     ArgumentTypes argTypes = new ArgumentTypes(args, this.bugfixed);
/*     */     
/* 141 */     MaybeEmptyCallableMemberDescriptor memberDesc = (MaybeEmptyCallableMemberDescriptor)this.argTypesToMemberDescCache.get(argTypes);
/* 142 */     if (memberDesc == null)
/*     */     {
/* 144 */       synchronized (this.argTypesToMemberDescCache) {
/* 145 */         memberDesc = (MaybeEmptyCallableMemberDescriptor)this.argTypesToMemberDescCache.get(argTypes);
/* 146 */         if (memberDesc == null) {
/* 147 */           memberDesc = argTypes.getMostSpecific(this.memberDescs, varArg);
/* 148 */           this.argTypesToMemberDescCache.put(argTypes, memberDesc);
/*     */         } 
/*     */       } 
/*     */     }
/* 152 */     return memberDesc;
/*     */   }
/*     */   
/*     */   Iterator getMemberDescriptors() {
/* 156 */     return this.memberDescs.iterator();
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected Class getCommonSupertypeForUnwrappingHint(Class<?> c1, Class<?> c2) {
/* 178 */     if (c1 == c2) return c1;
/*     */ 
/*     */ 
/*     */     
/* 182 */     if (this.bugfixed) {
/*     */       boolean c1WasPrim;
/*     */       boolean c2WasPrim;
/* 185 */       if (c1.isPrimitive()) {
/* 186 */         c1 = ClassUtil.primitiveClassToBoxingClass(c1);
/* 187 */         c1WasPrim = true;
/*     */       } else {
/* 189 */         c1WasPrim = false;
/*     */       } 
/*     */ 
/*     */ 
/*     */       
/* 194 */       if (c2.isPrimitive()) {
/* 195 */         c2 = ClassUtil.primitiveClassToBoxingClass(c2);
/* 196 */         c2WasPrim = true;
/*     */       } else {
/* 198 */         c2WasPrim = false;
/*     */       } 
/*     */       
/* 201 */       if (c1 == c2)
/*     */       {
/*     */ 
/*     */         
/* 205 */         return c1; } 
/* 206 */       if (Number.class.isAssignableFrom(c1) && Number.class.isAssignableFrom(c2))
/*     */       {
/*     */ 
/*     */ 
/*     */         
/* 211 */         return Number.class; } 
/* 212 */       if (c1WasPrim || c2WasPrim)
/*     */       {
/*     */ 
/*     */ 
/*     */         
/* 217 */         return Object.class;
/*     */       
/*     */       }
/*     */     }
/* 221 */     else if (c2.isPrimitive()) {
/* 222 */       if (c2 == byte.class) { c2 = Byte.class; }
/* 223 */       else if (c2 == short.class) { c2 = Short.class; }
/* 224 */       else if (c2 == char.class) { c2 = Character.class; }
/* 225 */       else if (c2 == int.class) { c2 = Integer.class; }
/* 226 */       else if (c2 == float.class) { c2 = Float.class; }
/* 227 */       else if (c2 == long.class) { c2 = Long.class; }
/* 228 */       else if (c2 == double.class) { c2 = Double.class; }
/*     */     
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 236 */     Set commonTypes = _MethodUtil.getAssignables(c1, c2);
/* 237 */     commonTypes.retainAll(_MethodUtil.getAssignables(c2, c1));
/* 238 */     if (commonTypes.isEmpty())
/*     */     {
/*     */       
/* 241 */       return Object.class;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 248 */     List<Class<?>> max = new ArrayList(); Iterator<Class<?>> commonTypesIter;
/* 249 */     label79: for (commonTypesIter = commonTypes.iterator(); commonTypesIter.hasNext(); ) {
/* 250 */       Class<?> clazz = commonTypesIter.next();
/* 251 */       for (Iterator<Class<?>> maxIter = max.iterator(); maxIter.hasNext(); ) {
/* 252 */         Class maxClazz = maxIter.next();
/* 253 */         if (_MethodUtil.isMoreOrSameSpecificParameterType(maxClazz, clazz, false, 0) != 0) {
/*     */           continue label79;
/*     */         }
/*     */         
/* 257 */         if (_MethodUtil.isMoreOrSameSpecificParameterType(clazz, maxClazz, false, 0) != 0)
/*     */         {
/*     */           
/* 260 */           maxIter.remove();
/*     */         }
/*     */       } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 268 */       max.add(clazz);
/*     */     } 
/*     */     
/* 271 */     if (max.size() > 1) {
/* 272 */       if (this.bugfixed) {
/*     */         
/* 274 */         for (Iterator<Class<?>> it = max.iterator(); it.hasNext(); ) {
/* 275 */           Class<Object> maxCl = (Class)it.next();
/* 276 */           if (!maxCl.isInterface()) {
/* 277 */             if (maxCl != Object.class)
/*     */             {
/* 279 */               return maxCl;
/*     */             }
/*     */             
/* 282 */             it.remove();
/*     */           } 
/*     */         } 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 289 */         max.remove(Cloneable.class);
/* 290 */         if (max.size() > 1) {
/* 291 */           max.remove(Serializable.class);
/* 292 */           if (max.size() > 1) {
/* 293 */             max.remove(Comparable.class);
/* 294 */             if (max.size() > 1) {
/* 295 */               return Object.class;
/*     */             }
/*     */           } 
/*     */         } 
/*     */       } else {
/* 300 */         return Object.class;
/*     */       } 
/*     */     }
/*     */     
/* 304 */     return max.get(0);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected final int[] getTypeFlags(int paramCount) {
/* 313 */     return (this.typeFlagsByParamCount != null && this.typeFlagsByParamCount.length > paramCount) ? this.typeFlagsByParamCount[paramCount] : null;
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
/*     */   protected final void mergeInTypesFlags(int dstParamCount, int[] srcTypeFlagsByParamIdx) {
/* 328 */     NullArgumentException.check("srcTypesFlagsByParamIdx", srcTypeFlagsByParamIdx);
/*     */ 
/*     */     
/* 331 */     if (dstParamCount == 0) {
/* 332 */       if (this.typeFlagsByParamCount == null) {
/* 333 */         this.typeFlagsByParamCount = ZERO_PARAM_COUNT_TYPE_FLAGS_ARRAY;
/* 334 */       } else if (this.typeFlagsByParamCount != ZERO_PARAM_COUNT_TYPE_FLAGS_ARRAY) {
/* 335 */         this.typeFlagsByParamCount[0] = ALL_ZEROS_ARRAY;
/*     */       } 
/*     */       
/*     */       return;
/*     */     } 
/*     */     
/* 341 */     if (this.typeFlagsByParamCount == null) {
/* 342 */       this.typeFlagsByParamCount = new int[dstParamCount + 1][];
/* 343 */     } else if (this.typeFlagsByParamCount.length <= dstParamCount) {
/* 344 */       int[][] newTypeFlagsByParamCount = new int[dstParamCount + 1][];
/* 345 */       System.arraycopy(this.typeFlagsByParamCount, 0, newTypeFlagsByParamCount, 0, this.typeFlagsByParamCount.length);
/*     */       
/* 347 */       this.typeFlagsByParamCount = newTypeFlagsByParamCount;
/*     */     } 
/*     */     
/* 350 */     int[] dstTypeFlagsByParamIdx = this.typeFlagsByParamCount[dstParamCount];
/* 351 */     if (dstTypeFlagsByParamIdx == null) {
/*     */ 
/*     */       
/* 354 */       if (srcTypeFlagsByParamIdx != ALL_ZEROS_ARRAY) {
/* 355 */         int srcParamCount = srcTypeFlagsByParamIdx.length;
/* 356 */         dstTypeFlagsByParamIdx = new int[dstParamCount];
/* 357 */         for (int paramIdx = 0; paramIdx < dstParamCount; paramIdx++) {
/* 358 */           dstTypeFlagsByParamIdx[paramIdx] = srcTypeFlagsByParamIdx[(paramIdx < srcParamCount) ? paramIdx : (srcParamCount - 1)];
/*     */         }
/*     */       } else {
/*     */         
/* 362 */         dstTypeFlagsByParamIdx = ALL_ZEROS_ARRAY;
/*     */       } 
/*     */       
/* 365 */       this.typeFlagsByParamCount[dstParamCount] = dstTypeFlagsByParamIdx;
/*     */     }
/*     */     else {
/*     */       
/* 369 */       if (srcTypeFlagsByParamIdx == dstTypeFlagsByParamIdx) {
/*     */         return;
/*     */       }
/*     */ 
/*     */ 
/*     */       
/* 375 */       if (dstTypeFlagsByParamIdx == ALL_ZEROS_ARRAY && dstParamCount > 0) {
/* 376 */         dstTypeFlagsByParamIdx = new int[dstParamCount];
/* 377 */         this.typeFlagsByParamCount[dstParamCount] = dstTypeFlagsByParamIdx;
/*     */       } 
/*     */       
/* 380 */       for (int paramIdx = 0; paramIdx < dstParamCount; paramIdx++) {
/*     */         int srcParamTypeFlags;
/* 382 */         if (srcTypeFlagsByParamIdx != ALL_ZEROS_ARRAY) {
/* 383 */           int srcParamCount = srcTypeFlagsByParamIdx.length;
/* 384 */           srcParamTypeFlags = srcTypeFlagsByParamIdx[(paramIdx < srcParamCount) ? paramIdx : (srcParamCount - 1)];
/*     */         } else {
/* 386 */           srcParamTypeFlags = 0;
/*     */         } 
/*     */         
/* 389 */         int dstParamTypesFlags = dstTypeFlagsByParamIdx[paramIdx];
/* 390 */         if (dstParamTypesFlags != srcParamTypeFlags) {
/* 391 */           int mergedTypeFlags = dstParamTypesFlags | srcParamTypeFlags;
/* 392 */           if ((mergedTypeFlags & 0x7FC) != 0)
/*     */           {
/* 394 */             mergedTypeFlags |= 0x1;
/*     */           }
/* 396 */           dstTypeFlagsByParamIdx[paramIdx] = mergedTypeFlags;
/*     */         } 
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   protected void forceNumberArgumentsToParameterTypes(Object[] args, Class[] paramTypes, int[] typeFlagsByParamIndex) {
/* 404 */     int paramTypesLen = paramTypes.length;
/* 405 */     int argsLen = args.length;
/* 406 */     for (int argIdx = 0; argIdx < argsLen; argIdx++) {
/* 407 */       int paramTypeIdx = (argIdx < paramTypesLen) ? argIdx : (paramTypesLen - 1);
/* 408 */       int typeFlags = typeFlagsByParamIndex[paramTypeIdx];
/*     */ 
/*     */ 
/*     */       
/* 412 */       if ((typeFlags & 0x1) != 0) {
/* 413 */         Object arg = args[argIdx];
/*     */         
/* 415 */         if (arg instanceof Number) {
/* 416 */           Class<?> targetType = paramTypes[paramTypeIdx];
/* 417 */           Number convertedArg = BeansWrapper.forceUnwrappedNumberToType((Number)arg, targetType, this.bugfixed);
/*     */           
/* 419 */           if (convertedArg != null)
/* 420 */             args[argIdx] = convertedArg; 
/*     */         } 
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   abstract Class[] preprocessParameterTypes(CallableMemberDescriptor paramCallableMemberDescriptor);
/*     */   
/*     */   abstract void afterWideningUnwrappingHints(Class[] paramArrayOfClass, int[] paramArrayOfint);
/*     */   
/*     */   abstract MaybeEmptyMemberAndArguments getMemberAndArguments(List paramList, BeansWrapper paramBeansWrapper) throws TemplateModelException;
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\ext\beans\OverloadedMethodsSubset.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */