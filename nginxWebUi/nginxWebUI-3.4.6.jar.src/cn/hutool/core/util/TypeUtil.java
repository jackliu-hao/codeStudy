/*     */ package cn.hutool.core.util;
/*     */ 
/*     */ import cn.hutool.core.lang.ParameterizedTypeImpl;
/*     */ import cn.hutool.core.lang.reflect.ActualTypeMapperPool;
/*     */ import java.lang.reflect.Field;
/*     */ import java.lang.reflect.Method;
/*     */ import java.lang.reflect.ParameterizedType;
/*     */ import java.lang.reflect.Type;
/*     */ import java.lang.reflect.TypeVariable;
/*     */ import java.lang.reflect.WildcardType;
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
/*     */ public class TypeUtil
/*     */ {
/*     */   public static Class<?> getClass(Type type) {
/*  35 */     if (null != type) {
/*  36 */       if (type instanceof Class)
/*  37 */         return (Class)type; 
/*  38 */       if (type instanceof ParameterizedType)
/*  39 */         return (Class)((ParameterizedType)type).getRawType(); 
/*  40 */       if (type instanceof TypeVariable)
/*  41 */         return (Class)((TypeVariable)type).getBounds()[0]; 
/*  42 */       if (type instanceof WildcardType) {
/*  43 */         Type[] upperBounds = ((WildcardType)type).getUpperBounds();
/*  44 */         if (upperBounds.length == 1) {
/*  45 */           return getClass(upperBounds[0]);
/*     */         }
/*     */       } 
/*     */     } 
/*  49 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Type getType(Field field) {
/*  60 */     if (null == field) {
/*  61 */       return null;
/*     */     }
/*  63 */     return field.getGenericType();
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
/*     */   public static Type getFieldType(Class<?> clazz, String fieldName) {
/*  75 */     return getType(ReflectUtil.getField(clazz, fieldName));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Class<?> getClass(Field field) {
/*  86 */     return (null == field) ? null : field.getType();
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
/*     */   public static Type getFirstParamType(Method method) {
/* 100 */     return getParamType(method, 0);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Class<?> getFirstParamClass(Method method) {
/* 111 */     return getParamClass(method, 0);
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
/*     */   public static Type getParamType(Method method, int index) {
/* 123 */     Type[] types = getParamTypes(method);
/* 124 */     if (null != types && types.length > index) {
/* 125 */       return types[index];
/*     */     }
/* 127 */     return null;
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
/*     */   public static Class<?> getParamClass(Method method, int index) {
/* 139 */     Class<?>[] classes = getParamClasses(method);
/* 140 */     if (null != classes && classes.length > index) {
/* 141 */       return classes[index];
/*     */     }
/* 143 */     return null;
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
/*     */   public static Type[] getParamTypes(Method method) {
/* 156 */     return (null == method) ? null : method.getGenericParameterTypes();
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
/*     */   public static Class<?>[] getParamClasses(Method method) {
/* 170 */     return (null == method) ? null : method.getParameterTypes();
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
/*     */   public static Type getReturnType(Method method) {
/* 185 */     return (null == method) ? null : method.getGenericReturnType();
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
/*     */   public static Class<?> getReturnClass(Method method) {
/* 198 */     return (null == method) ? null : method.getReturnType();
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
/*     */   public static Type getTypeArgument(Type type) {
/* 210 */     return getTypeArgument(type, 0);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Type getTypeArgument(Type type, int index) {
/* 221 */     Type[] typeArguments = getTypeArguments(type);
/* 222 */     if (null != typeArguments && typeArguments.length > index) {
/* 223 */       return typeArguments[index];
/*     */     }
/* 225 */     return null;
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
/*     */   public static Type[] getTypeArguments(Type type) {
/* 242 */     if (null == type) {
/* 243 */       return null;
/*     */     }
/*     */     
/* 246 */     ParameterizedType parameterizedType = toParameterizedType(type);
/* 247 */     return (null == parameterizedType) ? null : parameterizedType.getActualTypeArguments();
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
/*     */   public static ParameterizedType toParameterizedType(Type type) {
/* 267 */     ParameterizedType result = null;
/* 268 */     if (type instanceof ParameterizedType) {
/* 269 */       result = (ParameterizedType)type;
/* 270 */     } else if (type instanceof Class) {
/* 271 */       Class<?> clazz = (Class)type;
/* 272 */       Type genericSuper = clazz.getGenericSuperclass();
/* 273 */       if (null == genericSuper || Object.class.equals(genericSuper)) {
/*     */         
/* 275 */         Type[] genericInterfaces = clazz.getGenericInterfaces();
/* 276 */         if (ArrayUtil.isNotEmpty(genericInterfaces))
/*     */         {
/* 278 */           genericSuper = genericInterfaces[0];
/*     */         }
/*     */       } 
/* 281 */       result = toParameterizedType(genericSuper);
/*     */     } 
/* 283 */     return result;
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
/*     */   public static boolean isUnknown(Type type) {
/* 295 */     return (null == type || type instanceof TypeVariable);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean hasTypeVariable(Type... types) {
/* 306 */     for (Type type : types) {
/* 307 */       if (type instanceof TypeVariable) {
/* 308 */         return true;
/*     */       }
/*     */     } 
/* 311 */     return false;
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
/*     */   public static Map<Type, Type> getTypeMap(Class<?> clazz) {
/* 326 */     return ActualTypeMapperPool.get(clazz);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Type getActualType(Type type, Field field) {
/* 337 */     if (null == field) {
/* 338 */       return null;
/*     */     }
/* 340 */     return getActualType(ObjectUtil.<Type>defaultIfNull(type, field.getDeclaringClass()), field.getGenericType());
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
/*     */   public static Type getActualType(Type type, Type typeVariable) {
/* 357 */     if (typeVariable instanceof ParameterizedType) {
/* 358 */       return getActualType(type, (ParameterizedType)typeVariable);
/*     */     }
/*     */     
/* 361 */     if (typeVariable instanceof TypeVariable) {
/* 362 */       return ActualTypeMapperPool.getActualType(type, (TypeVariable)typeVariable);
/*     */     }
/*     */ 
/*     */     
/* 366 */     return typeVariable;
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
/*     */   public static Type getActualType(Type type, ParameterizedType parameterizedType) {
/*     */     ParameterizedTypeImpl parameterizedTypeImpl;
/* 379 */     Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
/*     */ 
/*     */     
/* 382 */     if (hasTypeVariable(actualTypeArguments)) {
/* 383 */       actualTypeArguments = getActualTypes(type, parameterizedType.getActualTypeArguments());
/* 384 */       if (ArrayUtil.isNotEmpty(actualTypeArguments))
/*     */       {
/* 386 */         parameterizedTypeImpl = new ParameterizedTypeImpl(actualTypeArguments, parameterizedType.getOwnerType(), parameterizedType.getRawType());
/*     */       }
/*     */     } 
/*     */     
/* 390 */     return (Type)parameterizedTypeImpl;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Type[] getActualTypes(Type type, Type... typeVariables) {
/* 401 */     return ActualTypeMapperPool.getActualTypes(type, typeVariables);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\cor\\util\TypeUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */