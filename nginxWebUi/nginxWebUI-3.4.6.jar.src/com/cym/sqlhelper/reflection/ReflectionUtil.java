/*     */ package com.cym.sqlhelper.reflection;
/*     */ 
/*     */ import java.beans.Introspector;
/*     */ import java.lang.invoke.SerializedLambda;
/*     */ import java.lang.reflect.Field;
/*     */ import java.lang.reflect.Method;
/*     */ import java.util.Map;
/*     */ import java.util.concurrent.ConcurrentHashMap;
/*     */ 
/*     */ public class ReflectionUtil
/*     */ {
/*  12 */   private static Map<SerializableFunction<?, ?>, Field> cache = new ConcurrentHashMap<>();
/*  13 */   private static final Map<Class<?>, Field[]> declaredFieldsCache = (Map)new ConcurrentHashMap<>(256);
/*  14 */   private static final Field[] EMPTY_FIELD_ARRAY = new Field[0];
/*     */   
/*     */   public static <T, R> String getFieldName(SerializableFunction<T, R> function) {
/*  17 */     Field field = getField(function);
/*  18 */     return field.getName();
/*     */   }
/*     */   
/*     */   public static Field getField(SerializableFunction<?, ?> function) {
/*  22 */     return cache.computeIfAbsent(function, ReflectionUtil::findField);
/*     */   }
/*     */   
/*     */   public static Field findField(SerializableFunction<?, ?> function) {
/*  26 */     Field field = null;
/*  27 */     String fieldName = null;
/*     */     
/*     */     try {
/*  30 */       Method method = function.getClass().getDeclaredMethod("writeReplace", new Class[0]);
/*  31 */       method.setAccessible(Boolean.TRUE.booleanValue());
/*  32 */       SerializedLambda serializedLambda = (SerializedLambda)method.invoke(function, new Object[0]);
/*     */       
/*  34 */       String implMethodName = serializedLambda.getImplMethodName();
/*  35 */       if (implMethodName.startsWith("get") && implMethodName.length() > 3)
/*  36 */       { fieldName = Introspector.decapitalize(implMethodName.substring(3)); }
/*     */       
/*  38 */       else if (implMethodName.startsWith("is") && implMethodName.length() > 2)
/*  39 */       { fieldName = Introspector.decapitalize(implMethodName.substring(2)); }
/*  40 */       else { if (implMethodName.startsWith("lambda$")) {
/*  41 */           throw new IllegalArgumentException("SerializableFunction不能传递lambda表达式,只能使用方法引用");
/*     */         }
/*     */         
/*  44 */         throw new IllegalArgumentException(implMethodName + "不是Getter方法引用"); }
/*     */ 
/*     */       
/*  47 */       String declaredClass = serializedLambda.getImplClass().replace("/", ".");
/*  48 */       Class<?> aClass = Class.forName(declaredClass, false, getDefaultClassLoader());
/*     */ 
/*     */       
/*  51 */       field = findField(aClass, fieldName, null);
/*     */     }
/*  53 */     catch (Exception e) {
/*  54 */       e.printStackTrace();
/*     */     } 
/*     */     
/*  57 */     if (field != null) {
/*  58 */       return field;
/*     */     }
/*  60 */     throw new NoSuchFieldError(fieldName);
/*     */   }
/*     */   
/*     */   public static Field findField(Class<?> clazz, String name, Class<?> type) {
/*  64 */     Class<?> searchType = clazz;
/*  65 */     while (Object.class != searchType && searchType != null) {
/*  66 */       Field[] fields = getDeclaredFields(searchType);
/*  67 */       for (Field field : fields) {
/*  68 */         if ((name == null || name.equals(field.getName())) && (type == null || type.equals(field.getType()))) {
/*  69 */           return field;
/*     */         }
/*     */       } 
/*  72 */       searchType = searchType.getSuperclass();
/*     */     } 
/*  74 */     return null;
/*     */   }
/*     */   
/*     */   private static Field[] getDeclaredFields(Class<?> clazz) {
/*  78 */     Field[] result = declaredFieldsCache.get(clazz);
/*  79 */     if (result == null) {
/*     */       try {
/*  81 */         result = clazz.getDeclaredFields();
/*  82 */         declaredFieldsCache.put(clazz, (result.length == 0) ? EMPTY_FIELD_ARRAY : result);
/*  83 */       } catch (Throwable ex) {
/*  84 */         throw new IllegalStateException("Failed to introspect Class [" + clazz.getName() + "] from ClassLoader [" + clazz.getClassLoader() + "]", ex);
/*     */       } 
/*     */     }
/*  87 */     return result;
/*     */   }
/*     */ 
/*     */   
/*     */   public static ClassLoader getDefaultClassLoader() {
/*  92 */     ClassLoader cl = null;
/*     */     try {
/*  94 */       cl = Thread.currentThread().getContextClassLoader();
/*     */     }
/*  96 */     catch (Throwable throwable) {}
/*     */ 
/*     */     
/*  99 */     if (cl == null) {
/*     */       
/* 101 */       cl = ReflectionUtil.class.getClassLoader();
/* 102 */       if (cl == null) {
/*     */         
/*     */         try {
/* 105 */           cl = ClassLoader.getSystemClassLoader();
/*     */         }
/* 107 */         catch (Throwable throwable) {}
/*     */       }
/*     */     } 
/*     */ 
/*     */     
/* 112 */     return cl;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\cym\sqlhelper\reflection\ReflectionUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */