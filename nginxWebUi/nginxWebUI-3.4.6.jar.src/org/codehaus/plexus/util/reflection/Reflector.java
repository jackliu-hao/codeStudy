/*     */ package org.codehaus.plexus.util.reflection;
/*     */ 
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.lang.reflect.Field;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.lang.reflect.Method;
/*     */ import java.util.HashMap;
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
/*     */ public final class Reflector
/*     */ {
/*     */   private static final String CONSTRUCTOR_METHOD_NAME = "$$CONSTRUCTOR$$";
/*     */   private static final String GET_INSTANCE_METHOD_NAME = "getInstance";
/*  39 */   private HashMap classMaps = new HashMap();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Object newInstance(Class theClass, Object[] params) throws ReflectorException {
/*  62 */     if (params == null)
/*     */     {
/*  64 */       params = new Object[0];
/*     */     }
/*     */     
/*  67 */     Class[] paramTypes = new Class[params.length];
/*     */     
/*  69 */     for (int i = 0, len = params.length; i < len; i++)
/*     */     {
/*  71 */       paramTypes[i] = params[i].getClass();
/*     */     }
/*     */ 
/*     */     
/*     */     try {
/*  76 */       Constructor con = getConstructor(theClass, paramTypes);
/*     */       
/*  78 */       if (con == null) {
/*     */         
/*  80 */         StringBuffer buffer = new StringBuffer();
/*     */         
/*  82 */         buffer.append("Constructor not found for class: ");
/*  83 */         buffer.append(theClass.getName());
/*  84 */         buffer.append(" with specified or ancestor parameter classes: ");
/*     */         
/*  86 */         for (int j = 0; j < paramTypes.length; j++) {
/*     */           
/*  88 */           buffer.append(paramTypes[j].getName());
/*  89 */           buffer.append(',');
/*     */         } 
/*     */         
/*  92 */         buffer.setLength(buffer.length() - 1);
/*     */         
/*  94 */         throw new ReflectorException(buffer.toString());
/*     */       } 
/*     */       
/*  97 */       return con.newInstance(params);
/*     */     }
/*  99 */     catch (InstantiationException ex) {
/*     */       
/* 101 */       throw new ReflectorException(ex);
/*     */     }
/* 103 */     catch (InvocationTargetException ex) {
/*     */       
/* 105 */       throw new ReflectorException(ex);
/*     */     }
/* 107 */     catch (IllegalAccessException ex) {
/*     */       
/* 109 */       throw new ReflectorException(ex);
/*     */     } 
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
/*     */   public Object getSingleton(Class theClass, Object[] initParams) throws ReflectorException {
/* 129 */     Class[] paramTypes = new Class[initParams.length];
/*     */     
/* 131 */     for (int i = 0, len = initParams.length; i < len; i++)
/*     */     {
/* 133 */       paramTypes[i] = initParams[i].getClass();
/*     */     }
/*     */ 
/*     */     
/*     */     try {
/* 138 */       Method method = getMethod(theClass, "getInstance", paramTypes);
/*     */       
/* 140 */       return method.invoke(null, initParams);
/*     */     }
/* 142 */     catch (InvocationTargetException ex) {
/*     */       
/* 144 */       throw new ReflectorException(ex);
/*     */     }
/* 146 */     catch (IllegalAccessException ex) {
/*     */       
/* 148 */       throw new ReflectorException(ex);
/*     */     } 
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
/*     */   public Object invoke(Object target, String methodName, Object[] params) throws ReflectorException {
/* 169 */     if (params == null)
/*     */     {
/* 171 */       params = new Object[0];
/*     */     }
/*     */     
/* 174 */     Class[] paramTypes = new Class[params.length];
/*     */     
/* 176 */     for (int i = 0, len = params.length; i < len; i++)
/*     */     {
/* 178 */       paramTypes[i] = params[i].getClass();
/*     */     }
/*     */ 
/*     */     
/*     */     try {
/* 183 */       Method method = getMethod(target.getClass(), methodName, paramTypes);
/*     */       
/* 185 */       if (method == null) {
/*     */         
/* 187 */         StringBuffer buffer = new StringBuffer();
/*     */         
/* 189 */         buffer.append("Singleton-producing method named '").append(methodName).append("' not found with specified parameter classes: ");
/*     */ 
/*     */         
/* 192 */         for (int j = 0; j < paramTypes.length; j++) {
/*     */           
/* 194 */           buffer.append(paramTypes[j].getName());
/* 195 */           buffer.append(',');
/*     */         } 
/*     */         
/* 198 */         buffer.setLength(buffer.length() - 1);
/*     */         
/* 200 */         throw new ReflectorException(buffer.toString());
/*     */       } 
/*     */       
/* 203 */       return method.invoke(target, params);
/*     */     }
/* 205 */     catch (InvocationTargetException ex) {
/*     */       
/* 207 */       throw new ReflectorException(ex);
/*     */     }
/* 209 */     catch (IllegalAccessException ex) {
/*     */       
/* 211 */       throw new ReflectorException(ex);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Object getStaticField(Class targetClass, String fieldName) throws ReflectorException {
/*     */     try {
/* 220 */       Field field = targetClass.getField(fieldName);
/*     */       
/* 222 */       return field.get((Object)null);
/*     */     }
/* 224 */     catch (SecurityException e) {
/*     */       
/* 226 */       throw new ReflectorException(e);
/*     */     }
/* 228 */     catch (NoSuchFieldException e) {
/*     */       
/* 230 */       throw new ReflectorException(e);
/*     */     }
/* 232 */     catch (IllegalArgumentException e) {
/*     */       
/* 234 */       throw new ReflectorException(e);
/*     */     }
/* 236 */     catch (IllegalAccessException e) {
/*     */       
/* 238 */       throw new ReflectorException(e);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Object getField(Object target, String fieldName) throws ReflectorException {
/* 245 */     return getField(target, fieldName, false);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Object getField(Object target, String fieldName, boolean breakAccessibility) throws ReflectorException {
/* 251 */     Class targetClass = target.getClass();
/* 252 */     while (targetClass != null) {
/*     */ 
/*     */       
/*     */       try {
/* 256 */         Field field = targetClass.getDeclaredField(fieldName);
/*     */         
/* 258 */         boolean accessibilityBroken = false;
/* 259 */         if (!field.isAccessible() && breakAccessibility) {
/*     */           
/* 261 */           field.setAccessible(true);
/* 262 */           accessibilityBroken = true;
/*     */         } 
/*     */         
/* 265 */         Object result = field.get(target);
/*     */         
/* 267 */         if (accessibilityBroken)
/*     */         {
/* 269 */           field.setAccessible(false);
/*     */         }
/*     */         
/* 272 */         return result;
/*     */       }
/* 274 */       catch (SecurityException e) {
/*     */         
/* 276 */         throw new ReflectorException(e);
/*     */       }
/* 278 */       catch (NoSuchFieldException e) {
/*     */         
/* 280 */         if (targetClass == Object.class)
/* 281 */           throw new ReflectorException(e); 
/* 282 */         targetClass = targetClass.getSuperclass();
/*     */       }
/* 284 */       catch (IllegalAccessException e) {
/*     */         
/* 286 */         throw new ReflectorException(e);
/*     */       } 
/*     */     } 
/*     */     
/* 290 */     return null;
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
/*     */   public Object invokeStatic(Class targetClass, String methodName, Object[] params) throws ReflectorException {
/* 309 */     if (params == null)
/*     */     {
/* 311 */       params = new Object[0];
/*     */     }
/*     */     
/* 314 */     Class[] paramTypes = new Class[params.length];
/*     */     
/* 316 */     for (int i = 0, len = params.length; i < len; i++)
/*     */     {
/* 318 */       paramTypes[i] = params[i].getClass();
/*     */     }
/*     */ 
/*     */     
/*     */     try {
/* 323 */       Method method = getMethod(targetClass, methodName, paramTypes);
/*     */       
/* 325 */       if (method == null) {
/*     */         
/* 327 */         StringBuffer buffer = new StringBuffer();
/*     */         
/* 329 */         buffer.append("Singleton-producing method named '" + methodName + "' not found with specified parameter classes: ");
/*     */ 
/*     */         
/* 332 */         for (int j = 0; j < paramTypes.length; j++) {
/*     */           
/* 334 */           buffer.append(paramTypes[j].getName());
/* 335 */           buffer.append(',');
/*     */         } 
/*     */         
/* 338 */         buffer.setLength(buffer.length() - 1);
/*     */         
/* 340 */         throw new ReflectorException(buffer.toString());
/*     */       } 
/*     */       
/* 343 */       return method.invoke(null, params);
/*     */     }
/* 345 */     catch (InvocationTargetException ex) {
/*     */       
/* 347 */       throw new ReflectorException(ex);
/*     */     }
/* 349 */     catch (IllegalAccessException ex) {
/*     */       
/* 351 */       throw new ReflectorException(ex);
/*     */     } 
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
/*     */   public Constructor getConstructor(Class targetClass, Class[] params) throws ReflectorException {
/* 371 */     Map constructorMap = getConstructorMap(targetClass);
/*     */     
/* 373 */     StringBuffer key = new StringBuffer(200);
/*     */     
/* 375 */     key.append("(");
/*     */     
/* 377 */     for (int i = 0, len = params.length; i < len; i++) {
/*     */       
/* 379 */       key.append(params[i].getName());
/* 380 */       key.append(",");
/*     */     } 
/*     */     
/* 383 */     if (params.length > 0)
/*     */     {
/* 385 */       key.setLength(key.length() - 1);
/*     */     }
/*     */     
/* 388 */     key.append(")");
/*     */     
/* 390 */     Constructor constructor = null;
/*     */     
/* 392 */     String paramKey = key.toString();
/*     */     
/* 394 */     synchronized (paramKey.intern()) {
/*     */       
/* 396 */       constructor = (Constructor)constructorMap.get(paramKey);
/*     */       
/* 398 */       if (constructor == null) {
/*     */         
/* 400 */         Constructor[] cands = (Constructor[])targetClass.getConstructors();
/*     */         
/* 402 */         for (int j = 0, k = cands.length; j < k; j++) {
/*     */           
/* 404 */           Class[] types = cands[j].getParameterTypes();
/*     */           
/* 406 */           if (params.length == types.length) {
/*     */ 
/*     */ 
/*     */ 
/*     */             
/* 411 */             for (int m = 0, len2 = params.length; m < len2; m++) {
/*     */               
/* 413 */               if (!types[m].isAssignableFrom(params[m]));
/*     */             } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */             
/* 420 */             constructor = cands[j];
/* 421 */             constructorMap.put(paramKey, constructor);
/*     */           } 
/*     */         } 
/*     */       } 
/*     */     } 
/* 426 */     if (constructor == null)
/*     */     {
/* 428 */       throw new ReflectorException("Error retrieving constructor object for: " + targetClass.getName() + paramKey);
/*     */     }
/*     */ 
/*     */     
/* 432 */     return constructor;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Object getObjectProperty(Object target, String propertyName) throws ReflectorException {
/* 438 */     Object returnValue = null;
/*     */     
/* 440 */     if (propertyName == null || propertyName.trim().length() < 1)
/*     */     {
/* 442 */       throw new ReflectorException("Cannot retrieve value for empty property.");
/*     */     }
/*     */     
/* 445 */     String beanAccessor = "get" + Character.toUpperCase(propertyName.charAt(0));
/* 446 */     if (propertyName.trim().length() > 1)
/*     */     {
/* 448 */       beanAccessor = beanAccessor + propertyName.substring(1).trim();
/*     */     }
/*     */     
/* 451 */     Class targetClass = target.getClass();
/* 452 */     Class[] emptyParams = new Class[0];
/*     */     
/* 454 */     Method method = _getMethod(targetClass, beanAccessor, emptyParams);
/* 455 */     if (method == null)
/*     */     {
/* 457 */       method = _getMethod(targetClass, propertyName, emptyParams);
/*     */     }
/* 459 */     if (method != null) {
/*     */       
/*     */       try {
/*     */         
/* 463 */         returnValue = method.invoke(target, new Object[0]);
/*     */       }
/* 465 */       catch (IllegalAccessException e) {
/*     */         
/* 467 */         throw new ReflectorException("Error retrieving property '" + propertyName + "' from '" + targetClass + "'", e);
/*     */       
/*     */       }
/* 470 */       catch (InvocationTargetException e) {
/*     */         
/* 472 */         throw new ReflectorException("Error retrieving property '" + propertyName + "' from '" + targetClass + "'", e);
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/* 477 */     if (method != null) {
/*     */       
/*     */       try
/*     */       {
/* 481 */         returnValue = method.invoke(target, new Object[0]);
/*     */       }
/* 483 */       catch (IllegalAccessException e)
/*     */       {
/* 485 */         throw new ReflectorException("Error retrieving property '" + propertyName + "' from '" + targetClass + "'", e);
/*     */       
/*     */       }
/* 488 */       catch (InvocationTargetException e)
/*     */       {
/* 490 */         throw new ReflectorException("Error retrieving property '" + propertyName + "' from '" + targetClass + "'", e);
/*     */       }
/*     */     
/*     */     }
/*     */     else {
/*     */       
/* 496 */       returnValue = getField(target, propertyName, true);
/* 497 */       if (method == null && returnValue == null)
/*     */       {
/*     */         
/* 500 */         throw new ReflectorException("Neither method: '" + propertyName + "' nor bean accessor: '" + beanAccessor + "' can be found for class: '" + targetClass + "', and retrieval of field: '" + propertyName + "' returned null as value.");
/*     */       }
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 506 */     return returnValue;
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
/*     */   public Method getMethod(Class targetClass, String methodName, Class[] params) throws ReflectorException {
/* 524 */     Method method = _getMethod(targetClass, methodName, params);
/*     */     
/* 526 */     if (method == null)
/*     */     {
/* 528 */       throw new ReflectorException("Method: '" + methodName + "' not found in class: '" + targetClass + "'");
/*     */     }
/*     */ 
/*     */     
/* 532 */     return method;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private Method _getMethod(Class targetClass, String methodName, Class[] params) throws ReflectorException {
/* 538 */     Map methodMap = getMethodMap(targetClass, methodName);
/*     */     
/* 540 */     StringBuffer key = new StringBuffer(200);
/*     */     
/* 542 */     key.append("(");
/*     */     
/* 544 */     for (int i = 0, len = params.length; i < len; i++) {
/*     */       
/* 546 */       key.append(params[i].getName());
/* 547 */       key.append(",");
/*     */     } 
/*     */     
/* 550 */     key.append(")");
/*     */     
/* 552 */     Method method = null;
/*     */     
/* 554 */     String paramKey = key.toString();
/*     */     
/* 556 */     synchronized (paramKey.intern()) {
/*     */       
/* 558 */       method = (Method)methodMap.get(paramKey);
/*     */       
/* 560 */       if (method == null) {
/*     */         
/* 562 */         Method[] cands = targetClass.getMethods();
/*     */         
/* 564 */         for (int j = 0, k = cands.length; j < k; j++) {
/*     */           
/* 566 */           String name = cands[j].getName();
/*     */           
/* 568 */           if (methodName.equals(name)) {
/*     */ 
/*     */ 
/*     */ 
/*     */             
/* 573 */             Class[] types = cands[j].getParameterTypes();
/*     */             
/* 575 */             if (params.length == types.length) {
/*     */ 
/*     */ 
/*     */ 
/*     */               
/* 580 */               for (int m = 0, len2 = params.length; m < len2; m++) {
/*     */                 
/* 582 */                 if (!types[m].isAssignableFrom(params[m]));
/*     */               } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */               
/* 589 */               method = cands[j];
/* 590 */               methodMap.put(paramKey, method);
/*     */             } 
/*     */           } 
/*     */         } 
/*     */       } 
/* 595 */     }  return method;
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
/*     */   private Map getConstructorMap(Class theClass) throws ReflectorException {
/* 610 */     return getMethodMap(theClass, "$$CONSTRUCTOR$$");
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
/*     */   private Map getMethodMap(Class theClass, String methodName) throws ReflectorException {
/* 627 */     Map methodMap = null;
/*     */     
/* 629 */     if (theClass == null)
/*     */     {
/* 631 */       return null;
/*     */     }
/*     */     
/* 634 */     String className = theClass.getName();
/*     */     
/* 636 */     synchronized (className.intern()) {
/*     */       
/* 638 */       Map classMethods = (Map)this.classMaps.get(className);
/*     */       
/* 640 */       if (classMethods == null) {
/*     */         
/* 642 */         classMethods = new HashMap();
/* 643 */         methodMap = new HashMap();
/* 644 */         classMethods.put(methodName, methodMap);
/*     */         
/* 646 */         this.classMaps.put(className, classMethods);
/*     */       }
/*     */       else {
/*     */         
/* 650 */         String key = className + "::" + methodName;
/*     */         
/* 652 */         synchronized (key.intern()) {
/*     */           
/* 654 */           methodMap = (Map)classMethods.get(methodName);
/*     */           
/* 656 */           if (methodMap == null) {
/*     */             
/* 658 */             methodMap = new HashMap();
/* 659 */             classMethods.put(methodName, methodMap);
/*     */           } 
/*     */         } 
/*     */       } 
/*     */     } 
/*     */     
/* 665 */     return methodMap;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\codehaus\plexu\\util\reflection\Reflector.class
 * Java compiler version: 3 (47.0)
 * JD-Core Version:       1.1.3
 */