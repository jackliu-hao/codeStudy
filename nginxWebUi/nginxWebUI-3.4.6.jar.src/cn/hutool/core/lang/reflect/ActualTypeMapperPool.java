/*     */ package cn.hutool.core.lang.reflect;
/*     */ 
/*     */ import cn.hutool.core.convert.Convert;
/*     */ import cn.hutool.core.map.WeakConcurrentMap;
/*     */ import cn.hutool.core.util.TypeUtil;
/*     */ import java.lang.reflect.ParameterizedType;
/*     */ import java.lang.reflect.Type;
/*     */ import java.lang.reflect.TypeVariable;
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
/*     */ public class ActualTypeMapperPool
/*     */ {
/*  21 */   private static final WeakConcurrentMap<Type, Map<Type, Type>> CACHE = new WeakConcurrentMap();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Map<Type, Type> get(Type type) {
/*  30 */     return (Map<Type, Type>)CACHE.computeIfAbsent(type, key -> createTypeMap(type));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Map<String, Type> getStrKeyMap(Type type) {
/*  41 */     return Convert.toMap(String.class, Type.class, get(type));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Type getActualType(Type type, TypeVariable<?> typeVariable) {
/*  52 */     Map<Type, Type> typeTypeMap = get(type);
/*  53 */     Type result = typeTypeMap.get(typeVariable);
/*  54 */     while (result instanceof TypeVariable) {
/*  55 */       result = typeTypeMap.get(result);
/*     */     }
/*  57 */     return result;
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
/*     */   public static Type[] getActualTypes(Type type, Type... typeVariables) {
/*  70 */     Type[] result = new Type[typeVariables.length];
/*  71 */     for (int i = 0; i < typeVariables.length; i++) {
/*  72 */       result[i] = (typeVariables[i] instanceof TypeVariable) ? 
/*  73 */         getActualType(type, (TypeVariable)typeVariables[i]) : typeVariables[i];
/*     */     }
/*     */     
/*  76 */     return result;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static Map<Type, Type> createTypeMap(Type<?> type) {
/*  86 */     Map<Type, Type> typeMap = new HashMap<>();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  95 */     while (null != type) {
/*  96 */       ParameterizedType parameterizedType = TypeUtil.toParameterizedType(type);
/*  97 */       if (null == parameterizedType) {
/*     */         break;
/*     */       }
/* 100 */       Type[] typeArguments = parameterizedType.getActualTypeArguments();
/* 101 */       Class<?> rawType = (Class)parameterizedType.getRawType();
/* 102 */       TypeVariable[] arrayOfTypeVariable = (TypeVariable[])rawType.getTypeParameters();
/*     */ 
/*     */       
/* 105 */       for (int i = 0; i < arrayOfTypeVariable.length; i++) {
/* 106 */         Type value = typeArguments[i];
/*     */         
/* 108 */         if (false == value instanceof TypeVariable) {
/* 109 */           typeMap.put(arrayOfTypeVariable[i], value);
/*     */         }
/*     */       } 
/*     */       
/* 113 */       type = rawType;
/*     */     } 
/*     */     
/* 116 */     return typeMap;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\lang\reflect\ActualTypeMapperPool.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */