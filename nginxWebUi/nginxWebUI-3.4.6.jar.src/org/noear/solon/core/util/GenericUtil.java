/*     */ package org.noear.solon.core.util;
/*     */ 
/*     */ import java.lang.reflect.ParameterizedType;
/*     */ import java.lang.reflect.Type;
/*     */ import java.lang.reflect.TypeVariable;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ 
/*     */ 
/*     */ public class GenericUtil
/*     */ {
/*     */   public static Class<?>[] resolveTypeArguments(Class<?> clazz, Class<?> genericIfc) {
/*  16 */     for (Type type0 : clazz.getGenericInterfaces()) {
/*  17 */       if (type0 instanceof ParameterizedType) {
/*  18 */         ParameterizedType type = (ParameterizedType)type0;
/*  19 */         Class<?> rawType = (Class)type.getRawType();
/*     */         
/*  21 */         if (rawType == genericIfc || getGenericInterfaces(rawType).contains(genericIfc)) {
/*  22 */           return (Class[])Arrays.<Type>stream(type.getActualTypeArguments())
/*  23 */             .map(item -> (Class)item)
/*  24 */             .toArray(x$0 -> new Class[x$0]);
/*     */         }
/*     */       } 
/*     */     } 
/*     */     
/*  29 */     Type type1 = clazz.getGenericSuperclass();
/*  30 */     if (type1 instanceof ParameterizedType) {
/*  31 */       ParameterizedType type = (ParameterizedType)type1;
/*  32 */       return (Class[])Arrays.<Type>stream(type.getActualTypeArguments())
/*  33 */         .map(item -> (Class)item)
/*  34 */         .toArray(x$0 -> new Class[x$0]);
/*     */     } 
/*     */     
/*  37 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static List<Class<?>> getGenericInterfaces(Class<?> clazz) {
/*  47 */     return getGenericInterfaces(clazz, new ArrayList<>());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static List<Class<?>> getGenericInterfaces(Class<?> clazz, List<Class<?>> classes) {
/*  57 */     Type[] interfaces = clazz.getGenericInterfaces();
/*  58 */     for (Type type : interfaces) {
/*  59 */       if (type instanceof ParameterizedType) {
/*  60 */         Class<?> aClass = (Class)((ParameterizedType)type).getRawType();
/*  61 */         classes.add(aClass);
/*  62 */         for (Type type0 : aClass.getGenericInterfaces()) {
/*  63 */           if (type0 instanceof ParameterizedType) {
/*  64 */             getGenericInterfaces((Class)((ParameterizedType)type0).getRawType(), classes);
/*     */           }
/*     */         } 
/*     */       } 
/*     */     } 
/*  69 */     return classes;
/*     */   }
/*     */   
/*     */   public static ParameterizedType toParameterizedType(Type type) {
/*  73 */     ParameterizedType result = null;
/*  74 */     if (type instanceof ParameterizedType) {
/*  75 */       result = (ParameterizedType)type;
/*  76 */     } else if (type instanceof Class) {
/*  77 */       Class<?> clazz = (Class)type;
/*  78 */       Type genericSuper = clazz.getGenericSuperclass();
/*  79 */       if (null == genericSuper || Object.class.equals(genericSuper)) {
/*     */         
/*  81 */         Type[] genericInterfaces = clazz.getGenericInterfaces();
/*  82 */         if (genericInterfaces != null && genericInterfaces.length > 0)
/*     */         {
/*  84 */           genericSuper = genericInterfaces[0];
/*     */         }
/*     */       } 
/*  87 */       result = toParameterizedType(genericSuper);
/*     */     } 
/*  89 */     return result;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*  94 */   private static final Map<Type, Map<String, Type>> genericInfoCached = new HashMap<>();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Map<String, Type> getGenericInfo(Type type) {
/* 103 */     Map<String, Type> tmp = genericInfoCached.get(type);
/* 104 */     if (tmp == null) {
/* 105 */       synchronized (type) {
/* 106 */         tmp = genericInfoCached.get(type);
/*     */         
/* 108 */         if (tmp == null) {
/* 109 */           tmp = createTypeGenericMap(type);
/* 110 */           genericInfoCached.put(type, tmp);
/*     */         } 
/*     */       } 
/*     */     }
/*     */     
/* 115 */     return tmp;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static Map<String, Type> createTypeGenericMap(Type<?> type) {
/* 126 */     Map<String, Type> typeMap = new HashMap<>();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 135 */     while (null != type) {
/* 136 */       ParameterizedType parameterizedType = toParameterizedType(type);
/* 137 */       if (null == parameterizedType) {
/*     */         break;
/*     */       }
/* 140 */       Type[] typeArguments = parameterizedType.getActualTypeArguments();
/* 141 */       Class<?> rawType = (Class)parameterizedType.getRawType();
/* 142 */       TypeVariable[] typeParameters = (TypeVariable[])rawType.getTypeParameters();
/*     */ 
/*     */       
/* 145 */       for (int i = 0; i < typeParameters.length; i++) {
/* 146 */         Type value = typeArguments[i];
/*     */         
/* 148 */         if (false == value instanceof TypeVariable) {
/* 149 */           typeMap.put(typeParameters[i].getTypeName(), value);
/*     */         }
/*     */       } 
/*     */       
/* 153 */       type = rawType;
/*     */     } 
/*     */     
/* 156 */     return typeMap;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\cor\\util\GenericUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */