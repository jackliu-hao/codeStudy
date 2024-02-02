/*     */ package freemarker.ext.beans;
/*     */ 
/*     */ import freemarker.core.BugException;
/*     */ import freemarker.template.TemplateModel;
/*     */ import freemarker.template.TemplateModelException;
/*     */ import freemarker.template.utility.ClassUtil;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.math.BigDecimal;
/*     */ import java.util.Collection;
/*     */ import java.util.Iterator;
/*     */ import java.util.LinkedList;
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
/*     */ final class ArgumentTypes
/*     */ {
/*     */   private static final int CONVERSION_DIFFICULTY_REFLECTION = 0;
/*     */   private static final int CONVERSION_DIFFICULTY_FREEMARKER = 1;
/*     */   private static final int CONVERSION_DIFFICULTY_IMPOSSIBLE = 2;
/*     */   private final Class<?>[] types;
/*     */   private final boolean bugfixed;
/*     */   
/*     */   ArgumentTypes(Object[] args, boolean bugfixed) {
/*  67 */     int ln = args.length;
/*  68 */     Class<?>[] typesTmp = new Class[ln];
/*  69 */     for (int i = 0; i < ln; i++) {
/*  70 */       Object arg = args[i];
/*  71 */       typesTmp[i] = (arg == null) ? (bugfixed ? Null.class : Object.class) : arg
/*     */         
/*  73 */         .getClass();
/*     */     } 
/*     */ 
/*     */     
/*  77 */     this.types = typesTmp;
/*  78 */     this.bugfixed = bugfixed;
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/*  83 */     int hash = 0;
/*  84 */     for (int i = 0; i < this.types.length; i++) {
/*  85 */       hash ^= this.types[i].hashCode();
/*     */     }
/*  87 */     return hash;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object o) {
/*  92 */     if (o instanceof ArgumentTypes) {
/*  93 */       ArgumentTypes cs = (ArgumentTypes)o;
/*  94 */       if (cs.types.length != this.types.length) {
/*  95 */         return false;
/*     */       }
/*  97 */       for (int i = 0; i < this.types.length; i++) {
/*  98 */         if (cs.types[i] != this.types[i]) {
/*  99 */           return false;
/*     */         }
/*     */       } 
/* 102 */       return true;
/*     */     } 
/* 104 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   MaybeEmptyCallableMemberDescriptor getMostSpecific(List<ReflectionCallableMemberDescriptor> memberDescs, boolean varArg) {
/* 113 */     LinkedList<CallableMemberDescriptor> applicables = getApplicables(memberDescs, varArg);
/* 114 */     if (applicables.isEmpty()) {
/* 115 */       return EmptyCallableMemberDescriptor.NO_SUCH_METHOD;
/*     */     }
/* 117 */     if (applicables.size() == 1) {
/* 118 */       return applicables.getFirst();
/*     */     }
/*     */     
/* 121 */     LinkedList<CallableMemberDescriptor> maximals = new LinkedList<>();
/* 122 */     for (CallableMemberDescriptor applicable : applicables) {
/* 123 */       boolean lessSpecific = false;
/* 124 */       Iterator<CallableMemberDescriptor> maximalsIter = maximals.iterator();
/* 125 */       while (maximalsIter.hasNext()) {
/* 126 */         CallableMemberDescriptor maximal = maximalsIter.next();
/* 127 */         int cmpRes = compareParameterListPreferability(applicable
/* 128 */             .getParamTypes(), maximal.getParamTypes(), varArg);
/* 129 */         if (cmpRes > 0) {
/* 130 */           maximalsIter.remove(); continue;
/* 131 */         }  if (cmpRes < 0) {
/* 132 */           lessSpecific = true;
/*     */         }
/*     */       } 
/* 135 */       if (!lessSpecific) {
/* 136 */         maximals.addLast(applicable);
/*     */       }
/*     */     } 
/* 139 */     if (maximals.size() > 1) {
/* 140 */       return EmptyCallableMemberDescriptor.AMBIGUOUS_METHOD;
/*     */     }
/* 142 */     return maximals.getFirst();
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
/*     */   int compareParameterListPreferability(Class<?>[] paramTypes1, Class<?>[] paramTypes2, boolean varArg) {
/* 177 */     int argTypesLen = this.types.length;
/* 178 */     int paramTypes1Len = paramTypes1.length;
/* 179 */     int paramTypes2Len = paramTypes2.length;
/*     */ 
/*     */     
/* 182 */     if (this.bugfixed) {
/* 183 */       int paramList1WeakWinCnt = 0;
/* 184 */       int paramList2WeakWinCnt = 0;
/* 185 */       int paramList1WinCnt = 0;
/* 186 */       int paramList2WinCnt = 0;
/* 187 */       int paramList1StrongWinCnt = 0;
/* 188 */       int paramList2StrongWinCnt = 0;
/* 189 */       int paramList1VeryStrongWinCnt = 0;
/* 190 */       int paramList2VeryStrongWinCnt = 0;
/* 191 */       int firstWinerParamList = 0;
/* 192 */       for (int j = 0; j < argTypesLen; j++) {
/* 193 */         int winerParam; Class<?> paramType1 = getParamType(paramTypes1, paramTypes1Len, j, varArg);
/* 194 */         Class<?> paramType2 = getParamType(paramTypes2, paramTypes2Len, j, varArg);
/*     */ 
/*     */         
/* 197 */         if (paramType1 == paramType2) {
/* 198 */           winerParam = 0;
/*     */         } else {
/* 200 */           int numConvPrice1, numConvPrice2; Class<?> argType = this.types[j];
/* 201 */           boolean argIsNum = Number.class.isAssignableFrom(argType);
/*     */ 
/*     */           
/* 204 */           if (argIsNum && ClassUtil.isNumerical(paramType1)) {
/*     */             
/* 206 */             Class<?> nonPrimParamType1 = paramType1.isPrimitive() ? ClassUtil.primitiveClassToBoxingClass(paramType1) : paramType1;
/* 207 */             numConvPrice1 = OverloadedNumberUtil.getArgumentConversionPrice(argType, nonPrimParamType1);
/*     */           } else {
/* 209 */             numConvPrice1 = Integer.MAX_VALUE;
/*     */           } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */           
/* 216 */           if (argIsNum && ClassUtil.isNumerical(paramType2)) {
/*     */             
/* 218 */             Class<?> nonPrimParamType2 = paramType2.isPrimitive() ? ClassUtil.primitiveClassToBoxingClass(paramType2) : paramType2;
/* 219 */             numConvPrice2 = OverloadedNumberUtil.getArgumentConversionPrice(argType, nonPrimParamType2);
/*     */           } else {
/* 221 */             numConvPrice2 = Integer.MAX_VALUE;
/*     */           } 
/*     */           
/* 224 */           if (numConvPrice1 == Integer.MAX_VALUE) {
/* 225 */             if (numConvPrice2 == Integer.MAX_VALUE) {
/*     */               
/* 227 */               if (List.class.isAssignableFrom(argType) && (paramType1
/* 228 */                 .isArray() || paramType2.isArray())) {
/* 229 */                 if (paramType1.isArray()) {
/* 230 */                   if (paramType2.isArray()) {
/* 231 */                     int r = compareParameterListPreferability_cmpTypeSpecificty(paramType1
/* 232 */                         .getComponentType(), paramType2.getComponentType());
/*     */ 
/*     */                     
/* 235 */                     if (r > 0) {
/* 236 */                       winerParam = 2;
/* 237 */                       paramList2StrongWinCnt++;
/* 238 */                     } else if (r < 0) {
/* 239 */                       winerParam = 1;
/* 240 */                       paramList1StrongWinCnt++;
/*     */                     } else {
/* 242 */                       winerParam = 0;
/*     */                     }
/*     */                   
/*     */                   }
/* 246 */                   else if (Collection.class.isAssignableFrom(paramType2)) {
/* 247 */                     winerParam = 2;
/* 248 */                     paramList2StrongWinCnt++;
/*     */                   } else {
/* 250 */                     winerParam = 1;
/* 251 */                     paramList1WeakWinCnt++;
/*     */                   
/*     */                   }
/*     */                 
/*     */                 }
/* 256 */                 else if (Collection.class.isAssignableFrom(paramType1)) {
/* 257 */                   winerParam = 1;
/* 258 */                   paramList1StrongWinCnt++;
/*     */                 } else {
/* 260 */                   winerParam = 2;
/* 261 */                   paramList2WeakWinCnt++;
/*     */                 }
/*     */               
/* 264 */               } else if (argType.isArray() && (List.class
/* 265 */                 .isAssignableFrom(paramType1) || List.class
/* 266 */                 .isAssignableFrom(paramType2))) {
/*     */                 
/* 268 */                 if (List.class.isAssignableFrom(paramType1)) {
/* 269 */                   if (List.class.isAssignableFrom(paramType2)) {
/*     */                     
/* 271 */                     winerParam = 0;
/*     */                   } else {
/*     */                     
/* 274 */                     winerParam = 2;
/* 275 */                     paramList2VeryStrongWinCnt++;
/*     */                   } 
/*     */                 } else {
/*     */                   
/* 279 */                   winerParam = 1;
/* 280 */                   paramList1VeryStrongWinCnt++;
/*     */                 } 
/*     */               } else {
/* 283 */                 int r = compareParameterListPreferability_cmpTypeSpecificty(paramType1, paramType2);
/*     */                 
/* 285 */                 if (r > 0) {
/* 286 */                   winerParam = 1;
/* 287 */                   if (r > 1) {
/* 288 */                     paramList1WinCnt++;
/*     */                   } else {
/* 290 */                     paramList1WeakWinCnt++;
/*     */                   } 
/* 292 */                 } else if (r < 0) {
/* 293 */                   winerParam = -1;
/* 294 */                   if (r < -1) {
/* 295 */                     paramList2WinCnt++;
/*     */                   } else {
/* 297 */                     paramList2WeakWinCnt++;
/*     */                   } 
/*     */                 } else {
/* 300 */                   winerParam = 0;
/*     */                 } 
/*     */               } 
/*     */             } else {
/* 304 */               winerParam = -1;
/* 305 */               paramList2WinCnt++;
/*     */             } 
/* 307 */           } else if (numConvPrice2 == Integer.MAX_VALUE) {
/* 308 */             winerParam = 1;
/* 309 */             paramList1WinCnt++;
/*     */           }
/* 311 */           else if (numConvPrice1 != numConvPrice2) {
/* 312 */             if (numConvPrice1 < numConvPrice2) {
/* 313 */               winerParam = 1;
/* 314 */               if (numConvPrice1 < 40000 && numConvPrice2 > 40000) {
/*     */                 
/* 316 */                 paramList1StrongWinCnt++;
/*     */               } else {
/* 318 */                 paramList1WinCnt++;
/*     */               } 
/*     */             } else {
/* 321 */               winerParam = -1;
/* 322 */               if (numConvPrice2 < 40000 && numConvPrice1 > 40000) {
/*     */                 
/* 324 */                 paramList2StrongWinCnt++;
/*     */               } else {
/* 326 */                 paramList2WinCnt++;
/*     */               } 
/*     */             } 
/*     */           } else {
/* 330 */             winerParam = (paramType1.isPrimitive() ? 1 : 0) - (paramType2.isPrimitive() ? 1 : 0);
/* 331 */             if (winerParam == 1) { paramList1WeakWinCnt++; }
/* 332 */             else if (winerParam == -1) { paramList2WeakWinCnt++; }
/*     */           
/*     */           } 
/*     */         } 
/*     */         
/* 337 */         if (firstWinerParamList == 0 && winerParam != 0) {
/* 338 */           firstWinerParamList = winerParam;
/*     */         }
/*     */       } 
/*     */       
/* 342 */       if (paramList1VeryStrongWinCnt != paramList2VeryStrongWinCnt)
/* 343 */         return paramList1VeryStrongWinCnt - paramList2VeryStrongWinCnt; 
/* 344 */       if (paramList1StrongWinCnt != paramList2StrongWinCnt)
/* 345 */         return paramList1StrongWinCnt - paramList2StrongWinCnt; 
/* 346 */       if (paramList1WinCnt != paramList2WinCnt)
/* 347 */         return paramList1WinCnt - paramList2WinCnt; 
/* 348 */       if (paramList1WeakWinCnt != paramList2WeakWinCnt)
/* 349 */         return paramList1WeakWinCnt - paramList2WeakWinCnt; 
/* 350 */       if (firstWinerParamList != 0) {
/* 351 */         return firstWinerParamList;
/*     */       }
/* 353 */       if (varArg) {
/* 354 */         if (paramTypes1Len == paramTypes2Len) {
/*     */ 
/*     */ 
/*     */           
/* 358 */           if (argTypesLen == paramTypes1Len - 1) {
/* 359 */             Class<?> paramType1 = getParamType(paramTypes1, paramTypes1Len, argTypesLen, true);
/* 360 */             Class<?> paramType2 = getParamType(paramTypes2, paramTypes2Len, argTypesLen, true);
/* 361 */             if (ClassUtil.isNumerical(paramType1) && ClassUtil.isNumerical(paramType2)) {
/* 362 */               int r = OverloadedNumberUtil.compareNumberTypeSpecificity(paramType1, paramType2);
/* 363 */               if (r != 0) return r;
/*     */             
/*     */             } 
/* 366 */             return compareParameterListPreferability_cmpTypeSpecificty(paramType1, paramType2);
/*     */           } 
/* 368 */           return 0;
/*     */         } 
/*     */ 
/*     */         
/* 372 */         return paramTypes1Len - paramTypes2Len;
/*     */       } 
/*     */       
/* 375 */       return 0;
/*     */     } 
/*     */ 
/*     */     
/* 379 */     boolean paramTypes1HasAMoreSpecific = false;
/* 380 */     boolean paramTypes2HasAMoreSpecific = false;
/* 381 */     for (int i = 0; i < paramTypes1Len; i++) {
/* 382 */       Class<?> paramType1 = getParamType(paramTypes1, paramTypes1Len, i, varArg);
/* 383 */       Class<?> paramType2 = getParamType(paramTypes2, paramTypes2Len, i, varArg);
/* 384 */       if (paramType1 != paramType2) {
/*     */ 
/*     */         
/* 387 */         paramTypes1HasAMoreSpecific = (paramTypes1HasAMoreSpecific || _MethodUtil.isMoreOrSameSpecificParameterType(paramType1, paramType2, false, 0) != 0);
/*     */ 
/*     */         
/* 390 */         paramTypes2HasAMoreSpecific = (paramTypes2HasAMoreSpecific || _MethodUtil.isMoreOrSameSpecificParameterType(paramType2, paramType1, false, 0) != 0);
/*     */       } 
/*     */     } 
/*     */     
/* 394 */     if (paramTypes1HasAMoreSpecific)
/* 395 */       return paramTypes2HasAMoreSpecific ? 0 : 1; 
/* 396 */     if (paramTypes2HasAMoreSpecific) {
/* 397 */       return -1;
/*     */     }
/* 399 */     return 0;
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
/*     */   private int compareParameterListPreferability_cmpTypeSpecificty(Class<?> paramType1, Class<?> paramType2) {
/* 415 */     Class<?> nonPrimParamType1 = paramType1.isPrimitive() ? ClassUtil.primitiveClassToBoxingClass(paramType1) : paramType1;
/*     */     
/* 417 */     Class<?> nonPrimParamType2 = paramType2.isPrimitive() ? ClassUtil.primitiveClassToBoxingClass(paramType2) : paramType2;
/*     */     
/* 419 */     if (nonPrimParamType1 == nonPrimParamType2) {
/* 420 */       if (nonPrimParamType1 != paramType1) {
/* 421 */         if (nonPrimParamType2 != paramType2) {
/* 422 */           return 0;
/*     */         }
/* 424 */         return 1;
/*     */       } 
/* 426 */       if (nonPrimParamType2 != paramType2) {
/* 427 */         return -1;
/*     */       }
/* 429 */       return 0;
/*     */     } 
/* 431 */     if (nonPrimParamType2.isAssignableFrom(nonPrimParamType1))
/* 432 */       return 2; 
/* 433 */     if (nonPrimParamType1.isAssignableFrom(nonPrimParamType2))
/* 434 */       return -2; 
/* 435 */     if (nonPrimParamType1 == Character.class && nonPrimParamType2.isAssignableFrom(String.class))
/* 436 */       return 2; 
/* 437 */     if (nonPrimParamType2 == Character.class && nonPrimParamType1.isAssignableFrom(String.class)) {
/* 438 */       return -2;
/*     */     }
/* 440 */     return 0;
/*     */   }
/*     */ 
/*     */   
/*     */   private static Class<?> getParamType(Class<?>[] paramTypes, int paramTypesLen, int i, boolean varArg) {
/* 445 */     return (varArg && i >= paramTypesLen - 1) ? paramTypes[paramTypesLen - 1]
/* 446 */       .getComponentType() : paramTypes[i];
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   LinkedList<CallableMemberDescriptor> getApplicables(List<ReflectionCallableMemberDescriptor> memberDescs, boolean varArg) {
/* 456 */     LinkedList<CallableMemberDescriptor> applicables = new LinkedList<>();
/* 457 */     for (ReflectionCallableMemberDescriptor memberDesc : memberDescs) {
/* 458 */       int difficulty = isApplicable(memberDesc, varArg);
/* 459 */       if (difficulty != 2) {
/* 460 */         if (difficulty == 0) {
/* 461 */           applicables.add(memberDesc); continue;
/* 462 */         }  if (difficulty == 1) {
/* 463 */           applicables.add(new SpecialConversionCallableMemberDescriptor(memberDesc)); continue;
/*     */         } 
/* 465 */         throw new BugException();
/*     */       } 
/*     */     } 
/*     */     
/* 469 */     return applicables;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private int isApplicable(ReflectionCallableMemberDescriptor memberDesc, boolean varArg) {
/* 480 */     Class<?>[] paramTypes = memberDesc.getParamTypes();
/* 481 */     int cl = this.types.length;
/* 482 */     int fl = paramTypes.length - (varArg ? 1 : 0);
/* 483 */     if (varArg) {
/* 484 */       if (cl < fl) {
/* 485 */         return 2;
/*     */       }
/*     */     }
/* 488 */     else if (cl != fl) {
/* 489 */       return 2;
/*     */     } 
/*     */ 
/*     */     
/* 493 */     int maxDifficulty = 0;
/* 494 */     for (int i = 0; i < fl; i++) {
/* 495 */       int difficulty = isMethodInvocationConvertible(paramTypes[i], this.types[i]);
/* 496 */       if (difficulty == 2) {
/* 497 */         return 2;
/*     */       }
/* 499 */       if (maxDifficulty < difficulty) {
/* 500 */         maxDifficulty = difficulty;
/*     */       }
/*     */     } 
/* 503 */     if (varArg) {
/* 504 */       Class<?> varArgParamType = paramTypes[fl].getComponentType();
/* 505 */       for (int j = fl; j < cl; j++) {
/* 506 */         int difficulty = isMethodInvocationConvertible(varArgParamType, this.types[j]);
/* 507 */         if (difficulty == 2) {
/* 508 */           return 2;
/*     */         }
/* 510 */         if (maxDifficulty < difficulty) {
/* 511 */           maxDifficulty = difficulty;
/*     */         }
/*     */       } 
/*     */     } 
/* 515 */     return maxDifficulty;
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
/*     */   private int isMethodInvocationConvertible(Class<?> formal, Class<?> actual) {
/* 534 */     if (formal.isAssignableFrom(actual) && actual != CharacterOrString.class)
/* 535 */       return 0; 
/* 536 */     if (this.bugfixed) {
/*     */       Class<?> formalNP;
/* 538 */       if (formal.isPrimitive()) {
/* 539 */         if (actual == Null.class) {
/* 540 */           return 2;
/*     */         }
/*     */         
/* 543 */         formalNP = ClassUtil.primitiveClassToBoxingClass(formal);
/* 544 */         if (actual == formalNP)
/*     */         {
/* 546 */           return 0;
/*     */         }
/*     */       } else {
/* 549 */         if (actual == Null.class) {
/* 550 */           return 0;
/*     */         }
/*     */         
/* 553 */         formalNP = formal;
/*     */       } 
/* 555 */       if (Number.class.isAssignableFrom(actual) && Number.class.isAssignableFrom(formalNP)) {
/* 556 */         return (OverloadedNumberUtil.getArgumentConversionPrice(actual, formalNP) == Integer.MAX_VALUE) ? 2 : 0;
/*     */       }
/* 558 */       if (formal.isArray())
/*     */       {
/* 560 */         return List.class.isAssignableFrom(actual) ? 1 : 2;
/*     */       }
/* 562 */       if (actual.isArray() && formal.isAssignableFrom(List.class))
/*     */       {
/* 564 */         return 1; } 
/* 565 */       if (actual == CharacterOrString.class && (formal
/* 566 */         .isAssignableFrom(String.class) || formal
/* 567 */         .isAssignableFrom(Character.class) || formal == char.class)) {
/* 568 */         return 1;
/*     */       }
/* 570 */       return 2;
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 580 */     if (formal.isPrimitive()) {
/*     */ 
/*     */ 
/*     */       
/* 584 */       if (formal == boolean.class) {
/* 585 */         return (actual == Boolean.class) ? 0 : 2;
/*     */       }
/* 587 */       if (formal == double.class && (actual == Double.class || actual == Float.class || actual == Long.class || actual == Integer.class || actual == Short.class || actual == Byte.class))
/*     */       {
/*     */ 
/*     */         
/* 591 */         return 0; } 
/* 592 */       if (formal == int.class && (actual == Integer.class || actual == Short.class || actual == Byte.class))
/*     */       {
/*     */         
/* 595 */         return 0; } 
/* 596 */       if (formal == long.class && (actual == Long.class || actual == Integer.class || actual == Short.class || actual == Byte.class))
/*     */       {
/*     */         
/* 599 */         return 0; } 
/* 600 */       if (formal == float.class && (actual == Float.class || actual == Long.class || actual == Integer.class || actual == Short.class || actual == Byte.class))
/*     */       {
/*     */ 
/*     */         
/* 604 */         return 0; } 
/* 605 */       if (formal == char.class) {
/* 606 */         return (actual == Character.class) ? 0 : 2;
/*     */       }
/* 608 */       if (formal == byte.class && actual == Byte.class)
/* 609 */         return 0; 
/* 610 */       if (formal == short.class && (actual == Short.class || actual == Byte.class))
/*     */       {
/* 612 */         return 0; } 
/* 613 */       if (BigDecimal.class.isAssignableFrom(actual) && ClassUtil.isNumerical(formal))
/*     */       {
/*     */ 
/*     */ 
/*     */         
/* 618 */         return 0;
/*     */       }
/* 620 */       return 2;
/*     */     } 
/*     */     
/* 623 */     return 2;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static class Null {}
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static final class SpecialConversionCallableMemberDescriptor
/*     */     extends CallableMemberDescriptor
/*     */   {
/*     */     private final ReflectionCallableMemberDescriptor callableMemberDesc;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     SpecialConversionCallableMemberDescriptor(ReflectionCallableMemberDescriptor callableMemberDesc) {
/* 648 */       this.callableMemberDesc = callableMemberDesc;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     TemplateModel invokeMethod(BeansWrapper bw, Object obj, Object[] args) throws TemplateModelException, InvocationTargetException, IllegalAccessException {
/* 654 */       convertArgsToReflectionCompatible(bw, args);
/* 655 */       return this.callableMemberDesc.invokeMethod(bw, obj, args);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     Object invokeConstructor(BeansWrapper bw, Object[] args) throws IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException, TemplateModelException {
/* 661 */       convertArgsToReflectionCompatible(bw, args);
/* 662 */       return this.callableMemberDesc.invokeConstructor(bw, args);
/*     */     }
/*     */ 
/*     */     
/*     */     String getDeclaration() {
/* 667 */       return this.callableMemberDesc.getDeclaration();
/*     */     }
/*     */ 
/*     */     
/*     */     boolean isConstructor() {
/* 672 */       return this.callableMemberDesc.isConstructor();
/*     */     }
/*     */ 
/*     */     
/*     */     boolean isStatic() {
/* 677 */       return this.callableMemberDesc.isStatic();
/*     */     }
/*     */ 
/*     */     
/*     */     boolean isVarargs() {
/* 682 */       return this.callableMemberDesc.isVarargs();
/*     */     }
/*     */ 
/*     */     
/*     */     Class<?>[] getParamTypes() {
/* 687 */       return this.callableMemberDesc.getParamTypes();
/*     */     }
/*     */ 
/*     */     
/*     */     String getName() {
/* 692 */       return this.callableMemberDesc.getName();
/*     */     }
/*     */     
/*     */     private void convertArgsToReflectionCompatible(BeansWrapper bw, Object[] args) throws TemplateModelException {
/* 696 */       Class<?>[] paramTypes = this.callableMemberDesc.getParamTypes();
/* 697 */       int ln = paramTypes.length;
/* 698 */       for (int i = 0; i < ln; i++) {
/* 699 */         Class<?> paramType = paramTypes[i];
/* 700 */         Object arg = args[i];
/* 701 */         if (arg != null) {
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
/* 713 */           if (paramType.isArray() && arg instanceof List) {
/* 714 */             args[i] = bw.listToArray((List)arg, paramType, null);
/*     */           }
/* 716 */           if (arg.getClass().isArray() && paramType.isAssignableFrom(List.class)) {
/* 717 */             args[i] = bw.arrayToList(arg);
/*     */           }
/*     */ 
/*     */           
/* 721 */           if (arg instanceof CharacterOrString)
/* 722 */             if (paramType == Character.class || paramType == char.class || (
/* 723 */               !paramType.isAssignableFrom(String.class) && paramType
/* 724 */               .isAssignableFrom(Character.class))) {
/* 725 */               args[i] = Character.valueOf(((CharacterOrString)arg).getAsChar());
/*     */             } else {
/* 727 */               args[i] = ((CharacterOrString)arg).getAsString();
/*     */             }  
/*     */         } 
/*     */       } 
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\ext\beans\ArgumentTypes.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */