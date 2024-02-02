/*     */ package ch.qos.logback.core.joran.util;
/*     */ 
/*     */ import ch.qos.logback.core.joran.spi.DefaultClass;
/*     */ import ch.qos.logback.core.joran.spi.DefaultNestedComponentRegistry;
/*     */ import ch.qos.logback.core.joran.util.beans.BeanDescription;
/*     */ import ch.qos.logback.core.joran.util.beans.BeanDescriptionCache;
/*     */ import ch.qos.logback.core.joran.util.beans.BeanUtil;
/*     */ import ch.qos.logback.core.spi.ContextAware;
/*     */ import ch.qos.logback.core.spi.ContextAwareBase;
/*     */ import ch.qos.logback.core.util.AggregationType;
/*     */ import ch.qos.logback.core.util.PropertySetterException;
/*     */ import java.lang.reflect.Method;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class PropertySetter
/*     */   extends ContextAwareBase
/*     */ {
/*     */   protected final Object obj;
/*     */   protected final Class<?> objClass;
/*     */   protected final BeanDescription beanDescription;
/*     */   
/*     */   public PropertySetter(BeanDescriptionCache beanDescriptionCache, Object obj) {
/*  66 */     this.obj = obj;
/*  67 */     this.objClass = obj.getClass();
/*  68 */     this.beanDescription = beanDescriptionCache.getBeanDescription(this.objClass);
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
/*     */   public void setProperty(String name, String value) {
/*  90 */     if (value == null) {
/*     */       return;
/*     */     }
/*  93 */     Method setter = findSetterMethod(name);
/*  94 */     if (setter == null) {
/*  95 */       addWarn("No setter for property [" + name + "] in " + this.objClass.getName() + ".");
/*     */     } else {
/*     */       try {
/*  98 */         setProperty(setter, name, value);
/*  99 */       } catch (PropertySetterException ex) {
/* 100 */         addWarn("Failed to set property [" + name + "] to value \"" + value + "\". ", (Throwable)ex);
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
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void setProperty(Method setter, String name, String value) throws PropertySetterException {
/*     */     Object arg;
/* 117 */     Class<?>[] paramTypes = setter.getParameterTypes();
/*     */ 
/*     */ 
/*     */     
/*     */     try {
/* 122 */       arg = StringToObjectConverter.convertArg((ContextAware)this, value, paramTypes[0]);
/* 123 */     } catch (Throwable t) {
/* 124 */       throw new PropertySetterException("Conversion to type [" + paramTypes[0] + "] failed. ", t);
/*     */     } 
/*     */     
/* 127 */     if (arg == null) {
/* 128 */       throw new PropertySetterException("Conversion to type [" + paramTypes[0] + "] failed.");
/*     */     }
/*     */     try {
/* 131 */       setter.invoke(this.obj, new Object[] { arg });
/* 132 */     } catch (Exception ex) {
/* 133 */       throw new PropertySetterException(ex);
/*     */     } 
/*     */   }
/*     */   
/*     */   public AggregationType computeAggregationType(String name) {
/* 138 */     String cName = capitalizeFirstLetter(name);
/*     */     
/* 140 */     Method addMethod = findAdderMethod(cName);
/*     */     
/* 142 */     if (addMethod != null) {
/* 143 */       AggregationType type = computeRawAggregationType(addMethod);
/* 144 */       switch (type) {
/*     */         case NOT_FOUND:
/* 146 */           return AggregationType.NOT_FOUND;
/*     */         case AS_BASIC_PROPERTY:
/* 148 */           return AggregationType.AS_BASIC_PROPERTY_COLLECTION;
/*     */         
/*     */         case AS_COMPLEX_PROPERTY:
/* 151 */           return AggregationType.AS_COMPLEX_PROPERTY_COLLECTION;
/*     */         case AS_BASIC_PROPERTY_COLLECTION:
/*     */         case AS_COMPLEX_PROPERTY_COLLECTION:
/* 154 */           addError("Unexpected AggregationType " + type);
/*     */           break;
/*     */       } 
/*     */     } 
/* 158 */     Method setter = findSetterMethod(name);
/* 159 */     if (setter != null) {
/* 160 */       return computeRawAggregationType(setter);
/*     */     }
/*     */     
/* 163 */     return AggregationType.NOT_FOUND;
/*     */   }
/*     */ 
/*     */   
/*     */   private Method findAdderMethod(String name) {
/* 168 */     String propertyName = BeanUtil.toLowerCamelCase(name);
/* 169 */     return this.beanDescription.getAdder(propertyName);
/*     */   }
/*     */   
/*     */   private Method findSetterMethod(String name) {
/* 173 */     String propertyName = BeanUtil.toLowerCamelCase(name);
/* 174 */     return this.beanDescription.getSetter(propertyName);
/*     */   }
/*     */   
/*     */   private Class<?> getParameterClassForMethod(Method method) {
/* 178 */     if (method == null) {
/* 179 */       return null;
/*     */     }
/* 181 */     Class<?>[] classArray = method.getParameterTypes();
/* 182 */     if (classArray.length != 1) {
/* 183 */       return null;
/*     */     }
/* 185 */     return classArray[0];
/*     */   }
/*     */ 
/*     */   
/*     */   private AggregationType computeRawAggregationType(Method method) {
/* 190 */     Class<?> parameterClass = getParameterClassForMethod(method);
/* 191 */     if (parameterClass == null) {
/* 192 */       return AggregationType.NOT_FOUND;
/*     */     }
/* 194 */     if (StringToObjectConverter.canBeBuiltFromSimpleString(parameterClass)) {
/* 195 */       return AggregationType.AS_BASIC_PROPERTY;
/*     */     }
/* 197 */     return AggregationType.AS_COMPLEX_PROPERTY;
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
/*     */   private boolean isUnequivocallyInstantiable(Class<?> clazz) {
/* 209 */     if (clazz.isInterface()) {
/* 210 */       return false;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     try {
/* 217 */       Object o = clazz.newInstance();
/* 218 */       if (o != null) {
/* 219 */         return true;
/*     */       }
/* 221 */       return false;
/*     */     }
/* 223 */     catch (InstantiationException e) {
/* 224 */       return false;
/* 225 */     } catch (IllegalAccessException e) {
/* 226 */       return false;
/*     */     } 
/*     */   }
/*     */   
/*     */   public Class<?> getObjClass() {
/* 231 */     return this.objClass;
/*     */   }
/*     */   
/*     */   public void addComplexProperty(String name, Object complexProperty) {
/* 235 */     Method adderMethod = findAdderMethod(name);
/*     */     
/* 237 */     if (adderMethod != null) {
/* 238 */       Class<?>[] paramTypes = adderMethod.getParameterTypes();
/* 239 */       if (!isSanityCheckSuccessful(name, adderMethod, paramTypes, complexProperty)) {
/*     */         return;
/*     */       }
/* 242 */       invokeMethodWithSingleParameterOnThisObject(adderMethod, complexProperty);
/*     */     } else {
/* 244 */       addError("Could not find method [add" + name + "] in class [" + this.objClass.getName() + "].");
/*     */     } 
/*     */   }
/*     */   
/*     */   void invokeMethodWithSingleParameterOnThisObject(Method method, Object parameter) {
/* 249 */     Class<?> ccc = parameter.getClass();
/*     */     try {
/* 251 */       method.invoke(this.obj, new Object[] { parameter });
/* 252 */     } catch (Exception e) {
/* 253 */       addError("Could not invoke method " + method.getName() + " in class " + this.obj.getClass().getName() + " with parameter of type " + ccc.getName(), e);
/*     */     } 
/*     */   }
/*     */   
/*     */   public void addBasicProperty(String name, String strValue) {
/*     */     Object arg;
/* 259 */     if (strValue == null) {
/*     */       return;
/*     */     }
/*     */     
/* 263 */     name = capitalizeFirstLetter(name);
/* 264 */     Method adderMethod = findAdderMethod(name);
/*     */     
/* 266 */     if (adderMethod == null) {
/* 267 */       addError("No adder for property [" + name + "].");
/*     */       
/*     */       return;
/*     */     } 
/* 271 */     Class<?>[] paramTypes = adderMethod.getParameterTypes();
/* 272 */     isSanityCheckSuccessful(name, adderMethod, paramTypes, strValue);
/*     */ 
/*     */     
/*     */     try {
/* 276 */       arg = StringToObjectConverter.convertArg((ContextAware)this, strValue, paramTypes[0]);
/* 277 */     } catch (Throwable t) {
/* 278 */       addError("Conversion to type [" + paramTypes[0] + "] failed. ", t);
/*     */       return;
/*     */     } 
/* 281 */     if (arg != null) {
/* 282 */       invokeMethodWithSingleParameterOnThisObject(adderMethod, strValue);
/*     */     }
/*     */   }
/*     */   
/*     */   public void setComplexProperty(String name, Object complexProperty) {
/* 287 */     Method setter = findSetterMethod(name);
/*     */     
/* 289 */     if (setter == null) {
/* 290 */       addWarn("Not setter method for property [" + name + "] in " + this.obj.getClass().getName());
/*     */       
/*     */       return;
/*     */     } 
/*     */     
/* 295 */     Class<?>[] paramTypes = setter.getParameterTypes();
/*     */     
/* 297 */     if (!isSanityCheckSuccessful(name, setter, paramTypes, complexProperty)) {
/*     */       return;
/*     */     }
/*     */     try {
/* 301 */       invokeMethodWithSingleParameterOnThisObject(setter, complexProperty);
/*     */     }
/* 303 */     catch (Exception e) {
/* 304 */       addError("Could not set component " + this.obj + " for parent component " + this.obj, e);
/*     */     } 
/*     */   }
/*     */   
/*     */   private boolean isSanityCheckSuccessful(String name, Method method, Class<?>[] params, Object complexProperty) {
/* 309 */     Class<?> ccc = complexProperty.getClass();
/* 310 */     if (params.length != 1) {
/* 311 */       addError("Wrong number of parameters in setter method for property [" + name + "] in " + this.obj.getClass().getName());
/*     */       
/* 313 */       return false;
/*     */     } 
/*     */     
/* 316 */     if (!params[0].isAssignableFrom(complexProperty.getClass())) {
/* 317 */       addError("A \"" + ccc.getName() + "\" object is not assignable to a \"" + params[0].getName() + "\" variable.");
/* 318 */       addError("The class \"" + params[0].getName() + "\" was loaded by ");
/* 319 */       addError("[" + params[0].getClassLoader() + "] whereas object of type ");
/* 320 */       addError("\"" + ccc.getName() + "\" was loaded by [" + ccc.getClassLoader() + "].");
/* 321 */       return false;
/*     */     } 
/*     */     
/* 324 */     return true;
/*     */   }
/*     */   
/*     */   private String capitalizeFirstLetter(String name) {
/* 328 */     return name.substring(0, 1).toUpperCase() + name.substring(1);
/*     */   }
/*     */   
/*     */   public Object getObj() {
/* 332 */     return this.obj;
/*     */   }
/*     */   
/*     */   Method getRelevantMethod(String name, AggregationType aggregationType) {
/*     */     Method relevantMethod;
/* 337 */     if (aggregationType == AggregationType.AS_COMPLEX_PROPERTY_COLLECTION) {
/* 338 */       relevantMethod = findAdderMethod(name);
/* 339 */     } else if (aggregationType == AggregationType.AS_COMPLEX_PROPERTY) {
/* 340 */       relevantMethod = findSetterMethod(name);
/*     */     } else {
/* 342 */       throw new IllegalStateException(aggregationType + " not allowed here");
/*     */     } 
/* 344 */     return relevantMethod;
/*     */   }
/*     */ 
/*     */   
/*     */   <T extends java.lang.annotation.Annotation> T getAnnotation(String name, Class<T> annonationClass, Method relevantMethod) {
/* 349 */     if (relevantMethod != null) {
/* 350 */       return relevantMethod.getAnnotation(annonationClass);
/*     */     }
/* 352 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   Class<?> getDefaultClassNameByAnnonation(String name, Method relevantMethod) {
/* 357 */     DefaultClass defaultClassAnnon = getAnnotation(name, DefaultClass.class, relevantMethod);
/* 358 */     if (defaultClassAnnon != null) {
/* 359 */       return defaultClassAnnon.value();
/*     */     }
/* 361 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   Class<?> getByConcreteType(String name, Method relevantMethod) {
/* 366 */     Class<?> paramType = getParameterClassForMethod(relevantMethod);
/* 367 */     if (paramType == null) {
/* 368 */       return null;
/*     */     }
/*     */     
/* 371 */     boolean isUnequivocallyInstantiable = isUnequivocallyInstantiable(paramType);
/* 372 */     if (isUnequivocallyInstantiable) {
/* 373 */       return paramType;
/*     */     }
/* 375 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Class<?> getClassNameViaImplicitRules(String name, AggregationType aggregationType, DefaultNestedComponentRegistry registry) {
/* 382 */     Class<?> registryResult = registry.findDefaultComponentType(this.obj.getClass(), name);
/* 383 */     if (registryResult != null) {
/* 384 */       return registryResult;
/*     */     }
/*     */     
/* 387 */     Method relevantMethod = getRelevantMethod(name, aggregationType);
/* 388 */     if (relevantMethod == null) {
/* 389 */       return null;
/*     */     }
/* 391 */     Class<?> byAnnotation = getDefaultClassNameByAnnonation(name, relevantMethod);
/* 392 */     if (byAnnotation != null) {
/* 393 */       return byAnnotation;
/*     */     }
/* 395 */     return getByConcreteType(name, relevantMethod);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\core\jora\\util\PropertySetter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */