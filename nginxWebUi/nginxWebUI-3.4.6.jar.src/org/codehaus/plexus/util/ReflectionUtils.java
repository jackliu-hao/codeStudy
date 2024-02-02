/*     */ package org.codehaus.plexus.util;
/*     */ 
/*     */ import java.lang.reflect.AccessibleObject;
/*     */ import java.lang.reflect.Field;
/*     */ import java.lang.reflect.Method;
/*     */ import java.lang.reflect.Modifier;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.HashMap;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class ReflectionUtils
/*     */ {
/*     */   public static Field getFieldByNameIncludingSuperclasses(String fieldName, Class clazz) {
/*  43 */     Field retValue = null;
/*     */ 
/*     */     
/*     */     try {
/*  47 */       retValue = clazz.getDeclaredField(fieldName);
/*     */     }
/*  49 */     catch (NoSuchFieldException e) {
/*     */       
/*  51 */       Class superclass = clazz.getSuperclass();
/*     */       
/*  53 */       if (superclass != null)
/*     */       {
/*  55 */         retValue = getFieldByNameIncludingSuperclasses(fieldName, superclass);
/*     */       }
/*     */     } 
/*     */     
/*  59 */     return retValue;
/*     */   }
/*     */ 
/*     */   
/*     */   public static List getFieldsIncludingSuperclasses(Class clazz) {
/*  64 */     List fields = new ArrayList(Arrays.asList((Object[])clazz.getDeclaredFields()));
/*     */     
/*  66 */     Class superclass = clazz.getSuperclass();
/*     */     
/*  68 */     if (superclass != null)
/*     */     {
/*  70 */       fields.addAll(getFieldsIncludingSuperclasses(superclass));
/*     */     }
/*     */     
/*  73 */     return fields;
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
/*     */   public static Method getSetter(String fieldName, Class clazz) {
/*  90 */     Method[] methods = clazz.getMethods();
/*     */     
/*  92 */     fieldName = "set" + StringUtils.capitalizeFirstLetter(fieldName);
/*     */     
/*  94 */     for (int i = 0; i < methods.length; i++) {
/*     */       
/*  96 */       Method method = methods[i];
/*     */       
/*  98 */       if (method.getName().equals(fieldName) && isSetter(method))
/*     */       {
/* 100 */         return method;
/*     */       }
/*     */     } 
/*     */     
/* 104 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static List getSetters(Class clazz) {
/* 112 */     Method[] methods = clazz.getMethods();
/*     */     
/* 114 */     List list = new ArrayList();
/*     */     
/* 116 */     for (int i = 0; i < methods.length; i++) {
/*     */       
/* 118 */       Method method = methods[i];
/*     */       
/* 120 */       if (isSetter(method))
/*     */       {
/* 122 */         list.add(method);
/*     */       }
/*     */     } 
/*     */     
/* 126 */     return list;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Class getSetterType(Method method) {
/* 136 */     if (!isSetter(method))
/*     */     {
/* 138 */       throw new RuntimeException("The method " + method.getDeclaringClass().getName() + "." + method.getName() + " is not a setter.");
/*     */     }
/*     */     
/* 141 */     return method.getParameterTypes()[0];
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
/*     */   public static void setVariableValueInObject(Object object, String variable, Object value) throws IllegalAccessException {
/* 159 */     Field field = getFieldByNameIncludingSuperclasses(variable, object.getClass());
/*     */     
/* 161 */     field.setAccessible(true);
/*     */     
/* 163 */     field.set(object, value);
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
/*     */   public static Object getValueIncludingSuperclasses(String variable, Object object) throws IllegalAccessException {
/* 177 */     Field field = getFieldByNameIncludingSuperclasses(variable, object.getClass());
/*     */     
/* 179 */     field.setAccessible(true);
/*     */     
/* 181 */     return field.get(object);
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
/*     */   public static Map getVariablesAndValuesIncludingSuperclasses(Object object) throws IllegalAccessException {
/* 194 */     HashMap map = new HashMap();
/*     */     
/* 196 */     gatherVariablesAndValuesIncludingSuperclasses(object, map);
/*     */     
/* 198 */     return map;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean isSetter(Method method) {
/* 207 */     return (method.getReturnType().equals(void.class) && !Modifier.isStatic(method.getModifiers()) && (method.getParameterTypes()).length == 1);
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
/*     */   private static void gatherVariablesAndValuesIncludingSuperclasses(Object object, Map map) throws IllegalAccessException {
/* 223 */     Class clazz = object.getClass();
/*     */     
/* 225 */     Field[] fields = clazz.getDeclaredFields();
/*     */     
/* 227 */     AccessibleObject.setAccessible((AccessibleObject[])fields, true);
/*     */     
/* 229 */     for (int i = 0; i < fields.length; i++) {
/*     */       
/* 231 */       Field field = fields[i];
/*     */       
/* 233 */       map.put(field.getName(), field.get(object));
/*     */     } 
/*     */ 
/*     */     
/* 237 */     Class superclass = clazz.getSuperclass();
/*     */     
/* 239 */     if (!Object.class.equals(superclass))
/*     */     {
/* 241 */       gatherVariablesAndValuesIncludingSuperclasses(superclass, map);
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\codehaus\plexu\\util\ReflectionUtils.class
 * Java compiler version: 3 (47.0)
 * JD-Core Version:       1.1.3
 */