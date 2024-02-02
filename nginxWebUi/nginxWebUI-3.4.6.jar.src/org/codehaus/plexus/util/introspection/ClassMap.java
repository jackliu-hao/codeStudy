/*     */ package org.codehaus.plexus.util.introspection;
/*     */ 
/*     */ import java.lang.reflect.Method;
/*     */ import java.lang.reflect.Modifier;
/*     */ import java.util.Hashtable;
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
/*     */ 
/*     */ public class ClassMap
/*     */ {
/*     */   private static final class CacheMiss
/*     */   {
/*     */     private CacheMiss() {}
/*     */   }
/*     */   
/*  41 */   private static final CacheMiss CACHE_MISS = new CacheMiss();
/*  42 */   private static final Object OBJECT = new Object();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private Class clazz;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  55 */   private Map methodCache = new Hashtable();
/*     */   
/*  57 */   private MethodMap methodMap = new MethodMap();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ClassMap(Class clazz) {
/*  64 */     this.clazz = clazz;
/*  65 */     populateMethodCache();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   Class getCachedClass() {
/*  73 */     return this.clazz;
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
/*     */   public Method findMethod(String name, Object[] params) throws MethodMap.AmbiguousException {
/*  91 */     String methodKey = makeMethodKey(name, params);
/*  92 */     Object cacheEntry = this.methodCache.get(methodKey);
/*     */     
/*  94 */     if (cacheEntry == CACHE_MISS)
/*     */     {
/*  96 */       return null;
/*     */     }
/*     */     
/*  99 */     if (cacheEntry == null) {
/*     */ 
/*     */       
/*     */       try {
/* 103 */         cacheEntry = this.methodMap.find(name, params);
/*     */       
/*     */       }
/* 106 */       catch (AmbiguousException ae) {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 112 */         this.methodCache.put(methodKey, CACHE_MISS);
/*     */ 
/*     */         
/* 115 */         throw ae;
/*     */       } 
/*     */       
/* 118 */       if (cacheEntry == null) {
/*     */         
/* 120 */         this.methodCache.put(methodKey, CACHE_MISS);
/*     */       
/*     */       }
/*     */       else {
/*     */         
/* 125 */         this.methodCache.put(methodKey, cacheEntry);
/*     */       } 
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 132 */     return (Method)cacheEntry;
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
/*     */   private void populateMethodCache() {
/* 148 */     Method[] methods = getAccessibleMethods(this.clazz);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 154 */     for (int i = 0; i < methods.length; i++) {
/*     */       
/* 156 */       Method method = methods[i];
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 164 */       Method publicMethod = getPublicMethod(method);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 173 */       if (publicMethod != null) {
/*     */         
/* 175 */         this.methodMap.add(publicMethod);
/* 176 */         this.methodCache.put(makeMethodKey(publicMethod), publicMethod);
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private String makeMethodKey(Method method) {
/* 188 */     Class[] parameterTypes = method.getParameterTypes();
/*     */     
/* 190 */     StringBuffer methodKey = new StringBuffer(method.getName());
/*     */     
/* 192 */     for (int j = 0; j < parameterTypes.length; j++) {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 200 */       if (parameterTypes[j].isPrimitive()) {
/*     */         
/* 202 */         if (parameterTypes[j].equals(boolean.class)) {
/* 203 */           methodKey.append("java.lang.Boolean");
/* 204 */         } else if (parameterTypes[j].equals(byte.class)) {
/* 205 */           methodKey.append("java.lang.Byte");
/* 206 */         } else if (parameterTypes[j].equals(char.class)) {
/* 207 */           methodKey.append("java.lang.Character");
/* 208 */         } else if (parameterTypes[j].equals(double.class)) {
/* 209 */           methodKey.append("java.lang.Double");
/* 210 */         } else if (parameterTypes[j].equals(float.class)) {
/* 211 */           methodKey.append("java.lang.Float");
/* 212 */         } else if (parameterTypes[j].equals(int.class)) {
/* 213 */           methodKey.append("java.lang.Integer");
/* 214 */         } else if (parameterTypes[j].equals(long.class)) {
/* 215 */           methodKey.append("java.lang.Long");
/* 216 */         } else if (parameterTypes[j].equals(short.class)) {
/* 217 */           methodKey.append("java.lang.Short");
/*     */         } 
/*     */       } else {
/*     */         
/* 221 */         methodKey.append(parameterTypes[j].getName());
/*     */       } 
/*     */     } 
/*     */     
/* 225 */     return methodKey.toString();
/*     */   }
/*     */ 
/*     */   
/*     */   private static String makeMethodKey(String method, Object[] params) {
/* 230 */     StringBuffer methodKey = (new StringBuffer()).append(method);
/*     */     
/* 232 */     for (int j = 0; j < params.length; j++) {
/*     */       
/* 234 */       Object arg = params[j];
/*     */       
/* 236 */       if (arg == null)
/*     */       {
/* 238 */         arg = OBJECT;
/*     */       }
/*     */       
/* 241 */       methodKey.append(arg.getClass().getName());
/*     */     } 
/*     */     
/* 244 */     return methodKey.toString();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static Method[] getAccessibleMethods(Class clazz) {
/* 255 */     Method[] methods = clazz.getMethods();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 262 */     if (Modifier.isPublic(clazz.getModifiers()))
/*     */     {
/* 264 */       return methods;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 271 */     MethodInfo[] methodInfos = new MethodInfo[methods.length];
/*     */     
/* 273 */     for (int i = methods.length; i-- > 0;)
/*     */     {
/* 275 */       methodInfos[i] = new MethodInfo(methods[i]);
/*     */     }
/*     */     
/* 278 */     int upcastCount = getAccessibleMethods(clazz, methodInfos, 0);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 284 */     if (upcastCount < methods.length)
/*     */     {
/* 286 */       methods = new Method[upcastCount];
/*     */     }
/*     */     
/* 289 */     int j = 0;
/* 290 */     for (int k = 0; k < methodInfos.length; k++) {
/*     */       
/* 292 */       MethodInfo methodInfo = methodInfos[k];
/* 293 */       if (methodInfo.upcast)
/*     */       {
/* 295 */         methods[j++] = methodInfo.method;
/*     */       }
/*     */     } 
/* 298 */     return methods;
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
/*     */   private static int getAccessibleMethods(Class clazz, MethodInfo[] methodInfos, int upcastCount) {
/* 312 */     int l = methodInfos.length;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 319 */     if (Modifier.isPublic(clazz.getModifiers())) {
/*     */       
/* 321 */       for (int j = 0; j < l && upcastCount < l; j++) {
/*     */ 
/*     */         
/*     */         try {
/* 325 */           MethodInfo methodInfo = methodInfos[j];
/*     */           
/* 327 */           if (!methodInfo.upcast)
/*     */           {
/* 329 */             methodInfo.tryUpcasting(clazz);
/* 330 */             upcastCount++;
/*     */           }
/*     */         
/* 333 */         } catch (NoSuchMethodException e) {}
/*     */       } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 346 */       if (upcastCount == l)
/*     */       {
/* 348 */         return upcastCount;
/*     */       }
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 356 */     Class superclazz = clazz.getSuperclass();
/*     */     
/* 358 */     if (superclazz != null) {
/*     */       
/* 360 */       upcastCount = getAccessibleMethods(superclazz, methodInfos, upcastCount);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 366 */       if (upcastCount == l)
/*     */       {
/* 368 */         return upcastCount;
/*     */       }
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 378 */     Class[] interfaces = clazz.getInterfaces();
/*     */     
/* 380 */     for (int i = interfaces.length; i-- > 0; ) {
/*     */       
/* 382 */       upcastCount = getAccessibleMethods(interfaces[i], methodInfos, upcastCount);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 388 */       if (upcastCount == l)
/*     */       {
/* 390 */         return upcastCount;
/*     */       }
/*     */     } 
/*     */     
/* 394 */     return upcastCount;
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
/*     */   public static Method getPublicMethod(Method method) {
/* 410 */     Class clazz = method.getDeclaringClass();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 417 */     if ((clazz.getModifiers() & 0x1) != 0)
/*     */     {
/* 419 */       return method;
/*     */     }
/*     */     
/* 422 */     return getPublicMethod(clazz, method.getName(), method.getParameterTypes());
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
/*     */   private static Method getPublicMethod(Class clazz, String name, Class[] paramTypes) {
/* 439 */     if ((clazz.getModifiers() & 0x1) != 0) {
/*     */       
/*     */       try {
/*     */         
/* 443 */         return clazz.getMethod(name, paramTypes);
/*     */       }
/* 445 */       catch (NoSuchMethodException e) {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 452 */         return null;
/*     */       } 
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 461 */     Class superclazz = clazz.getSuperclass();
/*     */     
/* 463 */     if (superclazz != null) {
/*     */       
/* 465 */       Method superclazzMethod = getPublicMethod(superclazz, name, paramTypes);
/*     */       
/* 467 */       if (superclazzMethod != null)
/*     */       {
/* 469 */         return superclazzMethod;
/*     */       }
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 477 */     Class[] interfaces = clazz.getInterfaces();
/*     */     
/* 479 */     for (int i = 0; i < interfaces.length; i++) {
/*     */       
/* 481 */       Method interfaceMethod = getPublicMethod(interfaces[i], name, paramTypes);
/*     */       
/* 483 */       if (interfaceMethod != null)
/*     */       {
/* 485 */         return interfaceMethod;
/*     */       }
/*     */     } 
/*     */     
/* 489 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   private static final class MethodInfo
/*     */   {
/*     */     Method method;
/*     */     
/*     */     String name;
/*     */     
/*     */     Class[] parameterTypes;
/*     */     
/*     */     boolean upcast;
/*     */     
/*     */     MethodInfo(Method method) {
/* 504 */       this.method = null;
/* 505 */       this.name = method.getName();
/* 506 */       this.parameterTypes = method.getParameterTypes();
/* 507 */       this.upcast = false;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     void tryUpcasting(Class clazz) throws NoSuchMethodException {
/* 513 */       this.method = clazz.getMethod(this.name, this.parameterTypes);
/* 514 */       this.name = null;
/* 515 */       this.parameterTypes = null;
/* 516 */       this.upcast = true;
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\codehaus\plexu\\util\introspection\ClassMap.class
 * Java compiler version: 3 (47.0)
 * JD-Core Version:       1.1.3
 */