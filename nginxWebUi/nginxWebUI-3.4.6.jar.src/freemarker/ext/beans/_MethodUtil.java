/*     */ package freemarker.ext.beans;
/*     */ 
/*     */ import freemarker.core.BugException;
/*     */ import freemarker.core._DelayedConversionToString;
/*     */ import freemarker.core._DelayedJQuote;
/*     */ import freemarker.core._TemplateModelException;
/*     */ import freemarker.template.TemplateModelException;
/*     */ import freemarker.template.utility.ClassUtil;
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.lang.reflect.Field;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.lang.reflect.Member;
/*     */ import java.lang.reflect.Method;
/*     */ import java.util.Collection;
/*     */ import java.util.HashSet;
/*     */ import java.util.Set;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class _MethodUtil
/*     */ {
/*     */   public static int isMoreOrSameSpecificParameterType(Class<?> specific, Class<?> generic, boolean bugfixed, int ifHigherThan) {
/*  73 */     if (ifHigherThan >= 4) return 0; 
/*  74 */     if (generic.isAssignableFrom(specific))
/*     */     {
/*  76 */       return (generic == specific) ? 1 : 4;
/*     */     }
/*  78 */     boolean specificIsPrim = specific.isPrimitive();
/*  79 */     boolean genericIsPrim = generic.isPrimitive();
/*  80 */     if (specificIsPrim) {
/*  81 */       if (genericIsPrim) {
/*  82 */         if (ifHigherThan >= 3) return 0; 
/*  83 */         return isWideningPrimitiveNumberConversion(specific, generic) ? 3 : 0;
/*     */       } 
/*  85 */       if (bugfixed) {
/*  86 */         Class<?> specificAsBoxed = ClassUtil.primitiveClassToBoxingClass(specific);
/*  87 */         if (specificAsBoxed == generic)
/*     */         {
/*  89 */           return 2; } 
/*  90 */         if (generic.isAssignableFrom(specificAsBoxed))
/*     */         {
/*  92 */           return 4; } 
/*  93 */         if (ifHigherThan >= 3)
/*  94 */           return 0; 
/*  95 */         if (Number.class.isAssignableFrom(specificAsBoxed) && Number.class
/*  96 */           .isAssignableFrom(generic)) {
/*  97 */           return isWideningBoxedNumberConversion(specificAsBoxed, generic) ? 3 : 0;
/*     */         }
/*  99 */         return 0;
/*     */       } 
/*     */       
/* 102 */       return 0;
/*     */     } 
/*     */ 
/*     */     
/* 106 */     if (ifHigherThan >= 3) return 0; 
/* 107 */     if (bugfixed && !genericIsPrim && Number.class
/* 108 */       .isAssignableFrom(specific) && Number.class.isAssignableFrom(generic)) {
/* 109 */       return isWideningBoxedNumberConversion(specific, generic) ? 3 : 0;
/*     */     }
/* 111 */     return 0;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static boolean isWideningPrimitiveNumberConversion(Class<byte> source, Class<short> target) {
/* 118 */     if (target == short.class && source == byte.class)
/* 119 */       return true; 
/* 120 */     if (target == int.class && (source == short.class || source == byte.class))
/*     */     {
/* 122 */       return true; } 
/* 123 */     if (target == long.class && (source == int.class || source == short.class || source == byte.class))
/*     */     {
/*     */       
/* 126 */       return true; } 
/* 127 */     if (target == float.class && (source == long.class || source == int.class || source == short.class || source == byte.class))
/*     */     {
/*     */       
/* 130 */       return true; } 
/* 131 */     if (target == double.class && (source == float.class || source == long.class || source == int.class || source == short.class || source == byte.class))
/*     */     {
/*     */ 
/*     */       
/* 135 */       return true;
/*     */     }
/* 137 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   private static boolean isWideningBoxedNumberConversion(Class<Byte> source, Class<Short> target) {
/* 142 */     if (target == Short.class && source == Byte.class)
/* 143 */       return true; 
/* 144 */     if (target == Integer.class && (source == Short.class || source == Byte.class))
/*     */     {
/* 146 */       return true; } 
/* 147 */     if (target == Long.class && (source == Integer.class || source == Short.class || source == Byte.class))
/*     */     {
/*     */       
/* 150 */       return true; } 
/* 151 */     if (target == Float.class && (source == Long.class || source == Integer.class || source == Short.class || source == Byte.class))
/*     */     {
/*     */       
/* 154 */       return true; } 
/* 155 */     if (target == Double.class && (source == Float.class || source == Long.class || source == Integer.class || source == Short.class || source == Byte.class))
/*     */     {
/*     */ 
/*     */       
/* 159 */       return true;
/*     */     }
/* 161 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Set getAssignables(Class c1, Class c2) {
/* 169 */     Set s = new HashSet();
/* 170 */     collectAssignables(c1, c2, s);
/* 171 */     return s;
/*     */   }
/*     */   
/*     */   private static void collectAssignables(Class<?> c1, Class<?> c2, Set<Class<?>> s) {
/* 175 */     if (c1.isAssignableFrom(c2)) {
/* 176 */       s.add(c1);
/*     */     }
/* 178 */     Class<?> sc = c1.getSuperclass();
/* 179 */     if (sc != null) {
/* 180 */       collectAssignables(sc, c2, s);
/*     */     }
/* 182 */     Class[] itf = c1.getInterfaces();
/* 183 */     for (int i = 0; i < itf.length; i++) {
/* 184 */       collectAssignables(itf[i], c2, s);
/*     */     }
/*     */   }
/*     */   
/*     */   public static Class[] getParameterTypes(Member member) {
/* 189 */     if (member instanceof Method) {
/* 190 */       return ((Method)member).getParameterTypes();
/*     */     }
/* 192 */     if (member instanceof Constructor) {
/* 193 */       return ((Constructor)member).getParameterTypes();
/*     */     }
/* 195 */     throw new IllegalArgumentException("\"member\" must be Method or Constructor");
/*     */   }
/*     */   
/*     */   public static boolean isVarargs(Member member) {
/* 199 */     if (member instanceof Method) {
/* 200 */       return ((Method)member).isVarArgs();
/*     */     }
/* 202 */     if (member instanceof Constructor) {
/* 203 */       return ((Constructor)member).isVarArgs();
/*     */     }
/* 205 */     throw new BugException();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String toString(Member member) {
/* 212 */     if (!(member instanceof Method) && !(member instanceof Constructor)) {
/* 213 */       throw new IllegalArgumentException("\"member\" must be a Method or Constructor");
/*     */     }
/*     */     
/* 216 */     StringBuilder sb = new StringBuilder();
/*     */     
/* 218 */     if ((member.getModifiers() & 0x8) != 0) {
/* 219 */       sb.append("static ");
/*     */     }
/*     */     
/* 222 */     String className = ClassUtil.getShortClassName(member.getDeclaringClass());
/* 223 */     if (className != null) {
/* 224 */       sb.append(className);
/* 225 */       sb.append('.');
/*     */     } 
/* 227 */     sb.append(member.getName());
/*     */     
/* 229 */     sb.append('(');
/* 230 */     Class[] paramTypes = getParameterTypes(member);
/* 231 */     for (int i = 0; i < paramTypes.length; i++) {
/* 232 */       if (i != 0) sb.append(", "); 
/* 233 */       String paramTypeDecl = ClassUtil.getShortClassName(paramTypes[i]);
/* 234 */       if (i == paramTypes.length - 1 && paramTypeDecl.endsWith("[]") && isVarargs(member)) {
/* 235 */         sb.append(paramTypeDecl.substring(0, paramTypeDecl.length() - 2));
/* 236 */         sb.append("...");
/*     */       } else {
/* 238 */         sb.append(paramTypeDecl);
/*     */       } 
/*     */     } 
/* 241 */     sb.append(')');
/*     */     
/* 243 */     return sb.toString();
/*     */   }
/*     */   
/*     */   public static Object[] invocationErrorMessageStart(Member member) {
/* 247 */     return invocationErrorMessageStart(member, member instanceof Constructor);
/*     */   }
/*     */   
/*     */   private static Object[] invocationErrorMessageStart(Object member, boolean isConstructor) {
/* 251 */     return new Object[] { "Java ", isConstructor ? "constructor " : "method ", new _DelayedJQuote(member) };
/*     */   }
/*     */   
/*     */   public static TemplateModelException newInvocationTemplateModelException(Object object, Member member, Throwable e) {
/* 255 */     return newInvocationTemplateModelException(object, member, 
/*     */ 
/*     */         
/* 258 */         ((member.getModifiers() & 0x8) != 0), member instanceof Constructor, e);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static TemplateModelException newInvocationTemplateModelException(Object object, CallableMemberDescriptor callableMemberDescriptor, Throwable e) {
/* 264 */     return newInvocationTemplateModelException(object, new _DelayedConversionToString(callableMemberDescriptor)
/*     */         {
/*     */           
/*     */           protected String doConversion(Object callableMemberDescriptor)
/*     */           {
/* 269 */             return ((CallableMemberDescriptor)callableMemberDescriptor).getDeclaration();
/*     */           }
/*     */         }, 
/* 272 */         callableMemberDescriptor.isStatic(), callableMemberDescriptor
/* 273 */         .isConstructor(), e);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static TemplateModelException newInvocationTemplateModelException(Object parentObject, Object member, boolean isStatic, boolean isConstructor, Throwable e) {
/* 279 */     while (e instanceof InvocationTargetException) {
/* 280 */       Throwable cause = ((InvocationTargetException)e).getTargetException();
/* 281 */       if (cause != null) {
/* 282 */         e = cause;
/*     */       }
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 288 */     (new Object[4])[0] = 
/* 289 */       invocationErrorMessageStart(member, isConstructor); (new Object[4])[1] = " threw an exception"; (new Object[4])[0] = " when invoked on "; (new Object[4])[1] = parentObject
/*     */ 
/*     */       
/* 292 */       .getClass(); (new Object[4])[2] = " object "; (new Object[4])[3] = new _DelayedJQuote(parentObject); return (TemplateModelException)new _TemplateModelException(e, new Object[] { null, null, (isStatic || isConstructor) ? "" : new Object[4], "; see cause exception in the Java stack trace." });
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String getBeanPropertyNameFromReaderMethodName(String name, Class<?> returnType) {
/*     */     int start;
/* 303 */     if (name.startsWith("get")) {
/* 304 */       start = 3;
/* 305 */     } else if (returnType == boolean.class && name.startsWith("is")) {
/* 306 */       start = 2;
/*     */     } else {
/* 308 */       return null;
/*     */     } 
/* 310 */     int ln = name.length();
/*     */     
/* 312 */     if (start == ln) {
/* 313 */       return null;
/*     */     }
/* 315 */     char c1 = name.charAt(start);
/*     */     
/* 317 */     return (start + 1 < ln && Character.isUpperCase(name.charAt(start + 1)) && Character.isUpperCase(c1)) ? name
/* 318 */       .substring(start) : (new StringBuilder(ln - start))
/* 319 */       .append(Character.toLowerCase(c1)).append(name, start + 1, ln)
/* 320 */       .toString();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static <T extends java.lang.annotation.Annotation> T getInheritableAnnotation(Class<?> contextClass, Method method, Class<T> annotationClass) {
/* 328 */     T result = method.getAnnotation(annotationClass);
/* 329 */     if (result != null) {
/* 330 */       return result;
/*     */     }
/* 332 */     return getInheritableMethodAnnotation(contextClass, method
/* 333 */         .getName(), method.getParameterTypes(), true, annotationClass);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static <T extends java.lang.annotation.Annotation> T getInheritableMethodAnnotation(Class<?> contextClass, String methodName, Class<?>[] methodParamTypes, boolean skipCheckingDirectMethod, Class<T> annotationClass) {
/* 340 */     if (!skipCheckingDirectMethod) {
/*     */       Method similarMethod;
/*     */       try {
/* 343 */         similarMethod = contextClass.getMethod(methodName, methodParamTypes);
/* 344 */       } catch (NoSuchMethodException e) {
/* 345 */         similarMethod = null;
/*     */       } 
/* 347 */       if (similarMethod != null) {
/* 348 */         T result = similarMethod.getAnnotation(annotationClass);
/* 349 */         if (result != null) {
/* 350 */           return result;
/*     */         }
/*     */       } 
/*     */     } 
/* 354 */     for (Class<?> anInterface : contextClass.getInterfaces()) {
/* 355 */       if (!anInterface.getName().startsWith("java.")) {
/*     */         Method similarInterfaceMethod;
/*     */         try {
/* 358 */           similarInterfaceMethod = anInterface.getMethod(methodName, methodParamTypes);
/* 359 */         } catch (NoSuchMethodException e) {
/* 360 */           similarInterfaceMethod = null;
/*     */         } 
/* 362 */         if (similarInterfaceMethod != null) {
/* 363 */           T result = similarInterfaceMethod.getAnnotation(annotationClass);
/* 364 */           if (result != null) {
/* 365 */             return result;
/*     */           }
/*     */         } 
/*     */       } 
/*     */     } 
/* 370 */     Class<?> superClass = contextClass.getSuperclass();
/* 371 */     if (superClass == Object.class || superClass == null) {
/* 372 */       return null;
/*     */     }
/* 374 */     return getInheritableMethodAnnotation(superClass, methodName, methodParamTypes, false, annotationClass);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static <T extends java.lang.annotation.Annotation> T getInheritableAnnotation(Class<?> contextClass, Constructor<?> constructor, Class<T> annotationClass) {
/* 383 */     T result = constructor.getAnnotation(annotationClass);
/* 384 */     if (result != null) {
/* 385 */       return result;
/*     */     }
/*     */     
/* 388 */     Class<?>[] paramTypes = constructor.getParameterTypes();
/*     */     while (true) {
/* 390 */       contextClass = contextClass.getSuperclass();
/* 391 */       if (contextClass == Object.class || contextClass == null) {
/* 392 */         return null;
/*     */       }
/*     */       try {
/* 395 */         constructor = contextClass.getConstructor(paramTypes);
/* 396 */       } catch (NoSuchMethodException e) {
/* 397 */         constructor = null;
/*     */       } 
/* 399 */       if (constructor != null) {
/* 400 */         result = constructor.getAnnotation(annotationClass);
/* 401 */         if (result != null) {
/* 402 */           return result;
/*     */         }
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static <T extends java.lang.annotation.Annotation> T getInheritableAnnotation(Class<?> contextClass, Field field, Class<T> annotationClass) {
/* 413 */     T result = field.getAnnotation(annotationClass);
/* 414 */     if (result != null) {
/* 415 */       return result;
/*     */     }
/* 417 */     return getInheritableFieldAnnotation(contextClass, field
/* 418 */         .getName(), true, annotationClass);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static <T extends java.lang.annotation.Annotation> T getInheritableFieldAnnotation(Class<?> contextClass, String fieldName, boolean skipCheckingDirectField, Class<T> annotationClass) {
/* 425 */     if (!skipCheckingDirectField) {
/*     */       Field similarField;
/*     */       try {
/* 428 */         similarField = contextClass.getField(fieldName);
/* 429 */       } catch (NoSuchFieldException e) {
/* 430 */         similarField = null;
/*     */       } 
/* 432 */       if (similarField != null) {
/* 433 */         T result = similarField.getAnnotation(annotationClass);
/* 434 */         if (result != null) {
/* 435 */           return result;
/*     */         }
/*     */       } 
/*     */     } 
/* 439 */     for (Class<?> anInterface : contextClass.getInterfaces()) {
/* 440 */       if (!anInterface.getName().startsWith("java.")) {
/*     */         Field similarInterfaceField;
/*     */         try {
/* 443 */           similarInterfaceField = anInterface.getField(fieldName);
/* 444 */         } catch (NoSuchFieldException e) {
/* 445 */           similarInterfaceField = null;
/*     */         } 
/* 447 */         if (similarInterfaceField != null) {
/* 448 */           T result = similarInterfaceField.getAnnotation(annotationClass);
/* 449 */           if (result != null) {
/* 450 */             return result;
/*     */           }
/*     */         } 
/*     */       } 
/*     */     } 
/* 455 */     Class<?> superClass = contextClass.getSuperclass();
/* 456 */     if (superClass == Object.class || superClass == null) {
/* 457 */       return null;
/*     */     }
/* 459 */     return getInheritableFieldAnnotation(superClass, fieldName, false, annotationClass);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static Method getMethodWithClosestNonSubReturnType(Class<?> returnType, Collection<Method> methods) {
/* 465 */     for (Method method : methods) {
/* 466 */       if (method.getReturnType() == returnType) {
/* 467 */         return method;
/*     */       }
/*     */     } 
/*     */     
/* 471 */     if (returnType == Object.class || returnType.isPrimitive())
/*     */     {
/* 473 */       return null;
/*     */     }
/*     */ 
/*     */     
/* 477 */     Class<?> superClass = returnType.getSuperclass();
/* 478 */     while (superClass != null && superClass != Object.class) {
/* 479 */       for (Method method : methods) {
/* 480 */         if (method.getReturnType() == superClass) {
/* 481 */           return method;
/*     */         }
/*     */       } 
/* 484 */       superClass = superClass.getSuperclass();
/*     */     } 
/*     */ 
/*     */     
/* 488 */     Method result = getMethodWithClosestNonSubInterfaceReturnType(returnType, methods);
/* 489 */     if (result != null) {
/* 490 */       return result;
/*     */     }
/*     */ 
/*     */     
/* 494 */     for (Method method : methods) {
/* 495 */       if (method.getReturnType() == Object.class) {
/* 496 */         return method;
/*     */       }
/*     */     } 
/*     */     
/* 500 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   private static Method getMethodWithClosestNonSubInterfaceReturnType(Class<?> returnType, Collection<Method> methods) {
/* 505 */     HashSet<Class<?>> nullResultReturnTypeInterfaces = new HashSet<>();
/*     */     
/*     */     while (true) {
/* 508 */       Method result = getMethodWithClosestNonSubInterfaceReturnType(returnType, methods, nullResultReturnTypeInterfaces);
/* 509 */       if (result != null) {
/* 510 */         return result;
/*     */       }
/*     */       
/* 513 */       returnType = returnType.getSuperclass();
/* 514 */       if (returnType == null)
/* 515 */         return null; 
/*     */     } 
/*     */   }
/*     */   
/*     */   private static Method getMethodWithClosestNonSubInterfaceReturnType(Class<?> returnType, Collection<Method> methods, Set<Class<?>> nullResultReturnTypeInterfaces) {
/* 520 */     boolean returnTypeIsInterface = returnType.isInterface();
/* 521 */     if (returnTypeIsInterface) {
/* 522 */       if (nullResultReturnTypeInterfaces.contains(returnType)) {
/* 523 */         return null;
/*     */       }
/* 525 */       for (Method method : methods) {
/* 526 */         if (method.getReturnType() == returnType) {
/* 527 */           return method;
/*     */         }
/*     */       } 
/*     */     } 
/* 531 */     for (Class<?> subInterface : returnType.getInterfaces()) {
/* 532 */       Method result = getMethodWithClosestNonSubInterfaceReturnType(subInterface, methods, nullResultReturnTypeInterfaces);
/* 533 */       if (result != null) {
/* 534 */         return result;
/*     */       }
/*     */     } 
/* 537 */     if (returnTypeIsInterface) {
/* 538 */       nullResultReturnTypeInterfaces.add(returnType);
/*     */     }
/* 540 */     return null;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\ext\beans\_MethodUtil.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */