/*     */ package org.noear.solon.aspect.asm;
/*     */ 
/*     */ import java.io.InputStream;
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.lang.reflect.InvocationHandler;
/*     */ import java.lang.reflect.Method;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import org.noear.solon.core.AopContext;
/*     */ import org.objectweb.asm.ClassReader;
/*     */ import org.objectweb.asm.ClassWriter;
/*     */ import org.objectweb.asm.FieldVisitor;
/*     */ import org.objectweb.asm.Label;
/*     */ import org.objectweb.asm.MethodVisitor;
/*     */ import org.objectweb.asm.Type;
/*     */ 
/*     */ 
/*     */ public class AsmProxy
/*     */ {
/*     */   public static final int ASM_VERSION = 524288;
/*     */   public static final int ASM_JDK_VERSION = 52;
/*     */   public static final String PROXY_CLASSNAME_PREFIX = "$Proxy_";
/*     */   private static final String FIELD_INVOCATIONHANDLER = "invocationHandler";
/*     */   private static final String METHOD_SETTER = "setInvocationHandler";
/*     */   private static final String METHOD_INVOKE = "invokeInvocationHandler";
/*     */   private static final String METHOD_INVOKE_DESC = "(Ljava/lang/Object;Ljava/lang/reflect/Method;[Ljava/lang/Object;)Ljava/lang/Object;";
/*     */   private static final String METHOD_FIELD_PREFIX = "method";
/*  29 */   private static final Map<String, Class<?>> proxyClassCache = new HashMap<>();
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static void saveProxyClassCache(ClassLoader classLoader, Class<?> targetClass, Class<?> proxyClass) {
/*  35 */     String key = classLoader.toString() + "_" + targetClass.getName();
/*  36 */     proxyClassCache.put(key, proxyClass);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static Class<?> getProxyClassCache(ClassLoader classLoader, Class<?> targetClass) {
/*  43 */     String key = classLoader.toString() + "_" + targetClass.getName();
/*  44 */     return proxyClassCache.get(key);
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
/*     */   public static Object newProxyInstance(AopContext context, InvocationHandler invocationHandler, Class<?> targetClass, Constructor<?> targetConstructor, Object... targetParam) {
/*  61 */     if (targetClass == null || invocationHandler == null) {
/*  62 */       throw new IllegalArgumentException("argument is null");
/*     */     }
/*     */ 
/*     */     
/*  66 */     AsmProxyClassLoader classLoader = (AsmProxyClassLoader)context.getAttrs().get(AsmProxyClassLoader.class);
/*  67 */     if (classLoader == null) {
/*  68 */       classLoader = new AsmProxyClassLoader(context.getClassLoader());
/*  69 */       context.getAttrs().put(AsmProxyClassLoader.class, classLoader);
/*     */     } 
/*     */ 
/*     */     
/*     */     try {
/*  74 */       Class<?> proxyClass = getProxyClassCache(classLoader, targetClass);
/*  75 */       if (proxyClass != null)
/*     */       {
/*  77 */         return newInstance(proxyClass, invocationHandler, targetConstructor, targetParam);
/*     */       }
/*     */ 
/*     */       
/*  81 */       ClassReader reader = null;
/*  82 */       String resourceName = targetClass.getName().replace('.', '/') + ".class";
/*  83 */       try (InputStream resourceStream = classLoader.getResourceAsStream(resourceName)) {
/*  84 */         reader = new ClassReader(resourceStream);
/*     */       } 
/*     */       
/*  87 */       TargetClassVisitor targetClassVisitor = new TargetClassVisitor();
/*  88 */       reader.accept(targetClassVisitor, 2);
/*     */       
/*  90 */       if (targetClassVisitor.isFinal()) {
/*  91 */         throw new IllegalArgumentException("class is final");
/*     */       }
/*     */       
/*  94 */       ClassWriter writer = new ClassWriter(3);
/*  95 */       String newClassName = generateProxyClassName(targetClass);
/*  96 */       String newClassInnerName = newClassName.replace(".", "/");
/*  97 */       String targetClassName = targetClass.getName();
/*  98 */       String targetClassInnerName = Type.getInternalName(targetClass);
/*     */       
/* 100 */       newClass(writer, newClassInnerName, targetClassInnerName);
/*     */       
/* 102 */       addField(writer);
/*     */       
/* 104 */       addSetterMethod(writer, newClassInnerName);
/*     */       
/* 106 */       List<MethodBean> constructors = targetClassVisitor.getConstructors();
/* 107 */       addConstructor(writer, constructors, targetClassInnerName);
/*     */       
/* 109 */       addInvokeMethod(writer, newClassInnerName);
/*     */       
/* 111 */       List<MethodBean> methods = targetClassVisitor.getMethods();
/* 112 */       List<MethodBean> declaredMethods = targetClassVisitor.getDeclaredMethods();
/* 113 */       Map<Integer, Integer> methodsMap = new HashMap<>();
/* 114 */       Map<Integer, Integer> declaredMethodsMap = new HashMap<>();
/* 115 */       int methodNameIndex = 0;
/* 116 */       methodNameIndex = addMethod(writer, newClassInnerName, targetClass.getMethods(), methods, true, methodNameIndex, methodsMap);
/*     */       
/* 118 */       addMethod(writer, newClassInnerName, targetClass.getDeclaredMethods(), declaredMethods, false, methodNameIndex, declaredMethodsMap);
/*     */ 
/*     */       
/* 121 */       addStaticInitBlock(writer, targetClassName, newClassInnerName, methodsMap, declaredMethodsMap);
/*     */       
/* 123 */       byte[] bytes = writer.toByteArray();
/*     */ 
/*     */       
/* 126 */       proxyClass = classLoader.transfer2Class(bytes);
/*     */       
/* 128 */       saveProxyClassCache(classLoader, targetClass, proxyClass);
/*     */       
/* 130 */       return newInstance(proxyClass, invocationHandler, targetConstructor, targetParam);
/* 131 */     } catch (Exception e) {
/* 132 */       e.printStackTrace();
/*     */       
/* 134 */       return null;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static String generateProxyClassName(Class<?> targetClass) {
/* 141 */     return targetClass.getPackage().getName() + "." + "$Proxy_" + targetClass.getSimpleName();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static Object newInstance(Class<?> proxyClass, InvocationHandler invocationHandler, Constructor<?> targetConstructor, Object... targetParam) throws Exception {
/* 151 */     Class<?>[] parameterTypes = targetConstructor.getParameterTypes();
/* 152 */     Constructor<?> constructor = proxyClass.getConstructor(parameterTypes);
/* 153 */     Object instance = constructor.newInstance(targetParam);
/* 154 */     Method setterMethod = proxyClass.getDeclaredMethod("setInvocationHandler", new Class[] { InvocationHandler.class });
/* 155 */     setterMethod.setAccessible(true);
/* 156 */     setterMethod.invoke(instance, new Object[] { invocationHandler });
/* 157 */     return instance;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static void newClass(ClassWriter writer, String newClassName, String targetClassName) throws Exception {
/* 164 */     int access = 17;
/* 165 */     writer.visit(52, access, newClassName, null, targetClassName, null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static void addField(ClassWriter writer) throws Exception {
/* 172 */     FieldVisitor fieldVisitor = writer.visitField(2, "invocationHandler", 
/* 173 */         Type.getDescriptor(InvocationHandler.class), null, null);
/* 174 */     fieldVisitor.visitEnd();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static void addSetterMethod(ClassWriter writer, String owner) throws Exception {
/* 181 */     String methodDesc = "(" + Type.getDescriptor(InvocationHandler.class) + ")V";
/* 182 */     MethodVisitor methodVisitor = writer.visitMethod(1, "setInvocationHandler", methodDesc, null, null);
/* 183 */     methodVisitor.visitCode();
/* 184 */     methodVisitor.visitVarInsn(25, 0);
/* 185 */     methodVisitor.visitVarInsn(25, 1);
/* 186 */     methodVisitor.visitFieldInsn(181, owner, "invocationHandler", 
/* 187 */         Type.getDescriptor(InvocationHandler.class));
/* 188 */     methodVisitor.visitInsn(177);
/* 189 */     methodVisitor.visitMaxs(2, 2);
/* 190 */     methodVisitor.visitEnd();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static void addConstructor(ClassWriter writer, List<MethodBean> constructors, String targetClassInnerName) throws Exception {
/* 198 */     for (MethodBean constructor : constructors) {
/* 199 */       Type[] argumentTypes = Type.getArgumentTypes(constructor.methodDesc);
/* 200 */       MethodVisitor methodVisitor = writer.visitMethod(1, "<init>", constructor.methodDesc, null, null);
/*     */       
/* 202 */       methodVisitor.visitCode();
/* 203 */       methodVisitor.visitVarInsn(25, 0);
/*     */ 
/*     */       
/* 206 */       for (int i = 0; i < argumentTypes.length; i++) {
/* 207 */         Type argumentType = argumentTypes[i];
/* 208 */         if (argumentType.equals(Type.BYTE_TYPE) || argumentType
/* 209 */           .equals(Type.BOOLEAN_TYPE) || argumentType
/* 210 */           .equals(Type.CHAR_TYPE) || argumentType
/* 211 */           .equals(Type.SHORT_TYPE) || argumentType
/* 212 */           .equals(Type.INT_TYPE)) {
/* 213 */           methodVisitor.visitVarInsn(21, i + 1);
/* 214 */         } else if (argumentType.equals(Type.LONG_TYPE)) {
/* 215 */           methodVisitor.visitVarInsn(22, i + 1);
/* 216 */         } else if (argumentType.equals(Type.FLOAT_TYPE)) {
/* 217 */           methodVisitor.visitVarInsn(23, i + 1);
/* 218 */         } else if (argumentType.equals(Type.DOUBLE_TYPE)) {
/* 219 */           methodVisitor.visitVarInsn(24, i + 1);
/*     */         } else {
/* 221 */           methodVisitor.visitVarInsn(25, i + 1);
/*     */         } 
/*     */       } 
/*     */ 
/*     */       
/* 226 */       methodVisitor.visitMethodInsn(183, targetClassInnerName, "<init>", constructor.methodDesc, false);
/* 227 */       methodVisitor.visitInsn(177);
/* 228 */       methodVisitor.visitMaxs(argumentTypes.length + 1, argumentTypes.length + 1);
/* 229 */       methodVisitor.visitEnd();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static void addInvokeMethod(ClassWriter writer, String owner) throws Exception {
/* 237 */     MethodVisitor methodVisitor = writer.visitMethod(130, "invokeInvocationHandler", "(Ljava/lang/Object;Ljava/lang/reflect/Method;[Ljava/lang/Object;)Ljava/lang/Object;", null, null);
/*     */     
/* 239 */     methodVisitor.visitCode();
/*     */     
/* 241 */     Label start0 = new Label();
/* 242 */     Label end0 = new Label();
/*     */     
/* 244 */     methodVisitor.visitLabel(start0);
/*     */     
/* 246 */     methodVisitor.visitVarInsn(25, 0);
/* 247 */     methodVisitor.visitFieldInsn(180, owner, "invocationHandler", 
/* 248 */         Type.getDescriptor(InvocationHandler.class));
/*     */     
/* 250 */     methodVisitor.visitVarInsn(25, 1);
/* 251 */     methodVisitor.visitVarInsn(25, 2);
/* 252 */     methodVisitor.visitVarInsn(25, 3);
/* 253 */     String handlerName = Type.getInternalName(InvocationHandler.class);
/* 254 */     String handlerMethodName = "invoke";
/* 255 */     String handlerDesc = "(Ljava/lang/Object;Ljava/lang/reflect/Method;[Ljava/lang/Object;)Ljava/lang/Object;";
/*     */     
/* 257 */     methodVisitor.visitMethodInsn(185, handlerName, handlerMethodName, handlerDesc, true);
/* 258 */     methodVisitor.visitInsn(176);
/*     */     
/* 260 */     methodVisitor.visitLabel(end0);
/* 261 */     methodVisitor.visitMaxs(4, 5);
/* 262 */     methodVisitor.visitEnd();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static int addMethod(ClassWriter writer, String newClassInnerName, Method[] methods, List<MethodBean> methodBeans, boolean isPublic, int methodNameIndex, Map<Integer, Integer> map) throws Exception {
/* 273 */     for (int i = 0; i < methodBeans.size(); i++) {
/* 274 */       MethodBean methodBean = methodBeans.get(i);
/*     */       
/* 276 */       if ((methodBean.access & 0x10) != 16 && (methodBean.access & 0x8) != 8) {
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 281 */         int access = -1;
/* 282 */         if (isPublic) {
/*     */           
/* 284 */           if ((methodBean.access & 0x1) == 1) {
/* 285 */             access = 1;
/*     */           
/*     */           }
/*     */         }
/* 289 */         else if ((methodBean.access & 0x4) == 4) {
/* 290 */           access = 4;
/* 291 */         } else if ((methodBean.access & 0x1) == 0 && (methodBean.access & 0x4) == 0 && (methodBean.access & 0x2) == 0) {
/*     */ 
/*     */           
/* 294 */           access = 0;
/*     */         } 
/*     */         
/* 297 */         if (access != -1)
/*     */         
/*     */         { 
/*     */           
/* 301 */           int methodIndex = findSomeMethod(methods, methodBean);
/* 302 */           if (methodIndex != -1)
/*     */           
/*     */           { 
/*     */             
/* 306 */             map.put(Integer.valueOf(methodNameIndex), Integer.valueOf(methodIndex));
/*     */             
/* 308 */             String fieldName = "method" + methodNameIndex;
/* 309 */             FieldVisitor fieldVisitor = writer.visitField(10, fieldName, 
/* 310 */                 Type.getDescriptor(Method.class), null, null);
/* 311 */             fieldVisitor.visitEnd();
/*     */             
/* 313 */             addMethod(writer, newClassInnerName, methodBean, access, methodNameIndex);
/* 314 */             methodNameIndex++; }  } 
/*     */       } 
/* 316 */     }  return methodNameIndex;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static void addMethod(ClassWriter writer, String newClassInnerName, MethodBean methodBean, int access, int methodNameIndex) throws Exception {
/* 325 */     MethodVisitor methodVisitor = writer.visitMethod(access, methodBean.methodName, methodBean.methodDesc, null, null);
/*     */     
/* 327 */     methodVisitor.visitCode();
/* 328 */     methodVisitor.visitVarInsn(25, 0);
/*     */     
/* 330 */     if ((methodBean.access & 0x8) == 8) {
/* 331 */       methodVisitor.visitInsn(1);
/*     */     } else {
/* 333 */       methodVisitor.visitVarInsn(25, 0);
/*     */     } 
/*     */     
/* 336 */     methodVisitor.visitFieldInsn(178, newClassInnerName, "method" + methodNameIndex, 
/* 337 */         Type.getDescriptor(Method.class));
/* 338 */     Type[] argumentTypes = Type.getArgumentTypes(methodBean.methodDesc);
/*     */     
/* 340 */     methodVisitor.visitIntInsn(16, argumentTypes.length);
/* 341 */     methodVisitor.visitTypeInsn(189, Type.getInternalName(Object.class));
/*     */     
/* 343 */     int start = 1;
/* 344 */     int stop = start;
/*     */     
/* 346 */     for (int i = 0; i < argumentTypes.length; i++) {
/* 347 */       Type type = argumentTypes[i];
/* 348 */       if (type.equals(Type.BYTE_TYPE)) {
/* 349 */         stop = start + 1;
/* 350 */         methodVisitor.visitInsn(89);
/*     */         
/* 352 */         methodVisitor.visitIntInsn(16, i);
/*     */         
/* 354 */         methodVisitor.visitVarInsn(21, start);
/* 355 */         methodVisitor.visitMethodInsn(184, Type.getInternalName(Byte.class), "valueOf", "(B)Ljava/lang/Byte;", false);
/*     */         
/* 357 */         methodVisitor.visitInsn(83);
/* 358 */       } else if (type.equals(Type.SHORT_TYPE)) {
/* 359 */         stop = start + 1;
/* 360 */         methodVisitor.visitInsn(89);
/*     */         
/* 362 */         methodVisitor.visitIntInsn(16, i);
/*     */         
/* 364 */         methodVisitor.visitVarInsn(21, start);
/* 365 */         methodVisitor.visitMethodInsn(184, Type.getInternalName(Short.class), "valueOf", "(S)Ljava/lang/Short;", false);
/*     */         
/* 367 */         methodVisitor.visitInsn(83);
/* 368 */       } else if (type.equals(Type.CHAR_TYPE)) {
/* 369 */         stop = start + 1;
/* 370 */         methodVisitor.visitInsn(89);
/*     */         
/* 372 */         methodVisitor.visitIntInsn(16, i);
/*     */         
/* 374 */         methodVisitor.visitVarInsn(21, start);
/* 375 */         methodVisitor.visitMethodInsn(184, Type.getInternalName(Character.class), "valueOf", "(C)Ljava/lang/Character;", false);
/*     */         
/* 377 */         methodVisitor.visitInsn(83);
/* 378 */       } else if (type.equals(Type.INT_TYPE)) {
/* 379 */         stop = start + 1;
/* 380 */         methodVisitor.visitInsn(89);
/*     */         
/* 382 */         methodVisitor.visitIntInsn(16, i);
/*     */         
/* 384 */         methodVisitor.visitVarInsn(21, start);
/* 385 */         methodVisitor.visitMethodInsn(184, Type.getInternalName(Integer.class), "valueOf", "(I)Ljava/lang/Integer;", false);
/*     */         
/* 387 */         methodVisitor.visitInsn(83);
/* 388 */       } else if (type.equals(Type.FLOAT_TYPE)) {
/* 389 */         stop = start + 1;
/* 390 */         methodVisitor.visitInsn(89);
/*     */         
/* 392 */         methodVisitor.visitIntInsn(16, i);
/*     */         
/* 394 */         methodVisitor.visitVarInsn(23, start);
/* 395 */         methodVisitor.visitMethodInsn(184, Type.getInternalName(Float.class), "valueOf", "(F)Ljava/lang/Float;", false);
/*     */         
/* 397 */         methodVisitor.visitInsn(83);
/* 398 */       } else if (type.equals(Type.DOUBLE_TYPE)) {
/* 399 */         stop = start + 2;
/* 400 */         methodVisitor.visitInsn(89);
/*     */         
/* 402 */         methodVisitor.visitIntInsn(16, i);
/*     */         
/* 404 */         methodVisitor.visitVarInsn(24, start);
/* 405 */         methodVisitor.visitMethodInsn(184, Type.getInternalName(Double.class), "valueOf", "(D)Ljava/lang/Double;", false);
/*     */         
/* 407 */         methodVisitor.visitInsn(83);
/* 408 */       } else if (type.equals(Type.LONG_TYPE)) {
/* 409 */         stop = start + 2;
/* 410 */         methodVisitor.visitInsn(89);
/*     */         
/* 412 */         methodVisitor.visitIntInsn(16, i);
/*     */         
/* 414 */         methodVisitor.visitVarInsn(22, start);
/* 415 */         methodVisitor.visitMethodInsn(184, Type.getInternalName(Long.class), "valueOf", "(J)Ljava/lang/Long;", false);
/*     */         
/* 417 */         methodVisitor.visitInsn(83);
/* 418 */       } else if (type.equals(Type.BOOLEAN_TYPE)) {
/* 419 */         stop = start + 1;
/* 420 */         methodVisitor.visitInsn(89);
/*     */         
/* 422 */         methodVisitor.visitIntInsn(16, i);
/*     */         
/* 424 */         methodVisitor.visitVarInsn(21, start);
/* 425 */         methodVisitor.visitMethodInsn(184, Type.getInternalName(Boolean.class), "valueOf", "(Z)Ljava/lang/Boolean;", false);
/*     */         
/* 427 */         methodVisitor.visitInsn(83);
/*     */       } else {
/* 429 */         stop = start + 1;
/* 430 */         methodVisitor.visitInsn(89);
/*     */         
/* 432 */         methodVisitor.visitIntInsn(16, i);
/*     */         
/* 434 */         methodVisitor.visitVarInsn(25, start);
/* 435 */         methodVisitor.visitInsn(83);
/*     */       } 
/* 437 */       start = stop;
/*     */     } 
/*     */     
/* 440 */     methodVisitor.visitMethodInsn(183, newClassInnerName, "invokeInvocationHandler", "(Ljava/lang/Object;Ljava/lang/reflect/Method;[Ljava/lang/Object;)Ljava/lang/Object;", false);
/*     */ 
/*     */     
/* 443 */     Type returnType = Type.getReturnType(methodBean.methodDesc);
/* 444 */     if (returnType.equals(Type.BYTE_TYPE)) {
/* 445 */       methodVisitor.visitTypeInsn(192, Type.getInternalName(Byte.class));
/* 446 */       methodVisitor.visitMethodInsn(182, Type.getInternalName(Byte.class), "byteValue", "()B", false);
/*     */       
/* 448 */       methodVisitor.visitInsn(172);
/* 449 */     } else if (returnType.equals(Type.BOOLEAN_TYPE)) {
/* 450 */       methodVisitor.visitTypeInsn(192, Type.getInternalName(Boolean.class));
/* 451 */       methodVisitor.visitMethodInsn(182, Type.getInternalName(Boolean.class), "booleanValue", "()Z", false);
/*     */       
/* 453 */       methodVisitor.visitInsn(172);
/* 454 */     } else if (returnType.equals(Type.CHAR_TYPE)) {
/* 455 */       methodVisitor.visitTypeInsn(192, Type.getInternalName(Character.class));
/* 456 */       methodVisitor.visitMethodInsn(182, Type.getInternalName(Character.class), "charValue", "()C", false);
/*     */       
/* 458 */       methodVisitor.visitInsn(172);
/* 459 */     } else if (returnType.equals(Type.SHORT_TYPE)) {
/* 460 */       methodVisitor.visitTypeInsn(192, Type.getInternalName(Short.class));
/* 461 */       methodVisitor.visitMethodInsn(182, Type.getInternalName(Short.class), "shortValue", "()S", false);
/*     */       
/* 463 */       methodVisitor.visitInsn(172);
/* 464 */     } else if (returnType.equals(Type.INT_TYPE)) {
/* 465 */       methodVisitor.visitTypeInsn(192, Type.getInternalName(Integer.class));
/* 466 */       methodVisitor.visitMethodInsn(182, Type.getInternalName(Integer.class), "intValue", "()I", false);
/*     */       
/* 468 */       methodVisitor.visitInsn(172);
/* 469 */     } else if (returnType.equals(Type.LONG_TYPE)) {
/* 470 */       methodVisitor.visitTypeInsn(192, Type.getInternalName(Long.class));
/* 471 */       methodVisitor.visitMethodInsn(182, Type.getInternalName(Long.class), "longValue", "()J", false);
/*     */       
/* 473 */       methodVisitor.visitInsn(173);
/* 474 */     } else if (returnType.equals(Type.FLOAT_TYPE)) {
/* 475 */       methodVisitor.visitTypeInsn(192, Type.getInternalName(Float.class));
/* 476 */       methodVisitor.visitMethodInsn(182, Type.getInternalName(Float.class), "floatValue", "()F", false);
/*     */       
/* 478 */       methodVisitor.visitInsn(174);
/* 479 */     } else if (returnType.equals(Type.DOUBLE_TYPE)) {
/* 480 */       methodVisitor.visitTypeInsn(192, Type.getInternalName(Double.class));
/* 481 */       methodVisitor.visitMethodInsn(182, Type.getInternalName(Double.class), "doubleValue", "()D", false);
/*     */       
/* 483 */       methodVisitor.visitInsn(175);
/* 484 */     } else if (returnType.equals(Type.VOID_TYPE)) {
/* 485 */       methodVisitor.visitInsn(177);
/*     */     } else {
/* 487 */       methodVisitor.visitTypeInsn(192, returnType.getInternalName());
/* 488 */       methodVisitor.visitInsn(176);
/*     */     } 
/* 490 */     methodVisitor.visitMaxs(8, 37);
/* 491 */     methodVisitor.visitEnd();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static void addStaticInitBlock(ClassWriter writer, String targetClassName, String newClassInnerName, Map<Integer, Integer> methodsMap, Map<Integer, Integer> declaredMethodsMap) throws Exception {
/* 500 */     String exceptionClassName = Type.getInternalName(ClassNotFoundException.class);
/* 501 */     MethodVisitor methodVisitor = writer.visitMethod(8, "<clinit>", "()V", null, null);
/*     */     
/* 503 */     methodVisitor.visitCode();
/*     */     
/* 505 */     Label label0 = new Label();
/* 506 */     Label label1 = new Label();
/* 507 */     Label label2 = new Label();
/* 508 */     methodVisitor.visitTryCatchBlock(label0, label1, label2, exceptionClassName);
/* 509 */     methodVisitor.visitLabel(label0);
/*     */     
/* 511 */     for (Map.Entry<Integer, Integer> entry : methodsMap.entrySet()) {
/* 512 */       Integer key = entry.getKey();
/* 513 */       Integer value = entry.getValue();
/* 514 */       methodVisitor.visitLdcInsn(targetClassName);
/* 515 */       methodVisitor.visitMethodInsn(184, Type.getInternalName(Class.class), "forName", "(Ljava/lang/String;)Ljava/lang/Class;", false);
/*     */       
/* 517 */       methodVisitor.visitMethodInsn(182, Type.getInternalName(Class.class), "getMethods", "()[Ljava/lang/reflect/Method;", false);
/*     */       
/* 519 */       methodVisitor.visitIntInsn(16, value.intValue());
/* 520 */       methodVisitor.visitInsn(50);
/* 521 */       methodVisitor.visitFieldInsn(179, newClassInnerName, "method" + key, 
/* 522 */           Type.getDescriptor(Method.class));
/*     */     } 
/*     */     
/* 525 */     for (Map.Entry<Integer, Integer> entry : declaredMethodsMap.entrySet()) {
/* 526 */       Integer key = entry.getKey();
/* 527 */       Integer value = entry.getValue();
/* 528 */       methodVisitor.visitLdcInsn(targetClassName);
/* 529 */       methodVisitor.visitMethodInsn(184, Type.getInternalName(Class.class), "forName", "(Ljava/lang/String;)Ljava/lang/Class;", false);
/*     */       
/* 531 */       methodVisitor.visitMethodInsn(182, Type.getInternalName(Class.class), "getDeclaredMethods", "()[Ljava/lang/reflect/Method;", false);
/*     */       
/* 533 */       methodVisitor.visitIntInsn(16, value.intValue());
/* 534 */       methodVisitor.visitInsn(50);
/* 535 */       methodVisitor.visitFieldInsn(179, newClassInnerName, "method" + key, 
/* 536 */           Type.getDescriptor(Method.class));
/*     */     } 
/*     */     
/* 539 */     methodVisitor.visitLabel(label1);
/* 540 */     Label label3 = new Label();
/* 541 */     methodVisitor.visitJumpInsn(167, label3);
/* 542 */     methodVisitor.visitLabel(label2);
/* 543 */     methodVisitor.visitFrame(4, 0, null, 1, new Object[] { exceptionClassName });
/*     */     
/* 545 */     methodVisitor.visitVarInsn(58, 0);
/* 546 */     methodVisitor.visitVarInsn(25, 0);
/* 547 */     methodVisitor.visitMethodInsn(182, exceptionClassName, "printStackTrace", "()V", false);
/*     */     
/* 549 */     methodVisitor.visitLabel(label3);
/* 550 */     methodVisitor.visitFrame(3, 0, null, 0, null);
/* 551 */     methodVisitor.visitInsn(177);
/* 552 */     methodVisitor.visitMaxs(2, 1);
/* 553 */     methodVisitor.visitEnd();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static int findSomeMethod(Method[] methods, MethodBean methodBean) {
/* 560 */     for (int i = 0; i < methods.length; i++) {
/* 561 */       if (equalsMethod(methods[i], methodBean)) {
/* 562 */         return i;
/*     */       }
/*     */     } 
/* 565 */     return -1;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static boolean equalsMethod(Method method, MethodBean methodBean) {
/* 572 */     if (method == null && methodBean == null) {
/* 573 */       return true;
/*     */     }
/* 575 */     if (method == null || methodBean == null) {
/* 576 */       return false;
/*     */     }
/*     */     try {
/* 579 */       if (!method.getName().equals(methodBean.methodName)) {
/* 580 */         return false;
/*     */       }
/* 582 */       if (!Type.getReturnType(method).equals(Type.getReturnType(methodBean.methodDesc))) {
/* 583 */         return false;
/*     */       }
/* 585 */       Type[] argumentTypes1 = Type.getArgumentTypes(method);
/* 586 */       Type[] argumentTypes2 = Type.getArgumentTypes(methodBean.methodDesc);
/* 587 */       if (argumentTypes1.length != argumentTypes2.length) {
/* 588 */         return false;
/*     */       }
/* 590 */       for (int i = 0; i < argumentTypes1.length; i++) {
/* 591 */         if (!argumentTypes1[i].equals(argumentTypes2[i])) {
/* 592 */           return false;
/*     */         }
/*     */       } 
/* 595 */       return true;
/* 596 */     } catch (Exception e) {
/* 597 */       e.printStackTrace();
/*     */       
/* 599 */       return false;
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\aspect\asm\AsmProxy.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */