/*     */ package cn.hutool.core.convert.impl;
/*     */ 
/*     */ import cn.hutool.core.convert.AbstractConverter;
/*     */ import cn.hutool.core.convert.ConvertException;
/*     */ import cn.hutool.core.lang.EnumItem;
/*     */ import cn.hutool.core.map.MapUtil;
/*     */ import cn.hutool.core.map.WeakConcurrentMap;
/*     */ import cn.hutool.core.util.ClassUtil;
/*     */ import cn.hutool.core.util.EnumUtil;
/*     */ import cn.hutool.core.util.ModifierUtil;
/*     */ import cn.hutool.core.util.ReflectUtil;
/*     */ import java.lang.reflect.Method;
/*     */ import java.util.Arrays;
/*     */ import java.util.Map;
/*     */ import java.util.stream.Collectors;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class EnumConverter
/*     */   extends AbstractConverter<Object>
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*  28 */   private static final WeakConcurrentMap<Class<?>, Map<Class<?>, Method>> VALUE_OF_METHOD_CACHE = new WeakConcurrentMap();
/*     */ 
/*     */ 
/*     */   
/*     */   private final Class enumClass;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public EnumConverter(Class enumClass) {
/*  38 */     this.enumClass = enumClass;
/*     */   }
/*     */ 
/*     */   
/*     */   protected Object convertInternal(Object value) {
/*  43 */     Enum enumValue = tryConvertEnum(value, this.enumClass);
/*  44 */     if (null == enumValue && false == value instanceof String)
/*     */     {
/*  46 */       enumValue = Enum.valueOf(this.enumClass, convertToStr(value));
/*     */     }
/*     */     
/*  49 */     if (null != enumValue) {
/*  50 */       return enumValue;
/*     */     }
/*     */     
/*  53 */     throw new ConvertException("Can not convert {} to {}", new Object[] { value, this.enumClass });
/*     */   }
/*     */ 
/*     */   
/*     */   public Class getTargetType() {
/*  58 */     return this.enumClass;
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
/*     */   protected static Enum tryConvertEnum(Object value, Class<?> enumClass) {
/*  75 */     if (value == null) {
/*  76 */       return null;
/*     */     }
/*     */ 
/*     */     
/*  80 */     if (EnumItem.class.isAssignableFrom(enumClass)) {
/*  81 */       EnumItem first = (EnumItem)EnumUtil.getEnumAt(enumClass, 0);
/*  82 */       if (null != first) {
/*  83 */         if (value instanceof Integer)
/*  84 */           return (Enum)first.fromInt((Integer)value); 
/*  85 */         if (value instanceof String) {
/*  86 */           return (Enum)first.fromStr(value.toString());
/*     */         }
/*     */       } 
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/*     */     try {
/*  94 */       Map<Class<?>, Method> methodMap = getMethodMap(enumClass);
/*  95 */       if (MapUtil.isNotEmpty(methodMap)) {
/*  96 */         Class<?> valueClass = value.getClass();
/*  97 */         for (Map.Entry<Class<?>, Method> entry : methodMap.entrySet()) {
/*  98 */           if (ClassUtil.isAssignable(entry.getKey(), valueClass)) {
/*  99 */             return (Enum)ReflectUtil.invokeStatic(entry.getValue(), new Object[] { value });
/*     */           }
/*     */         } 
/*     */       } 
/* 103 */     } catch (Exception exception) {}
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 114 */     Enum enumResult = null;
/* 115 */     if (value instanceof Integer) {
/* 116 */       enumResult = EnumUtil.getEnumAt(enumClass, ((Integer)value).intValue());
/* 117 */     } else if (value instanceof String) {
/*     */       try {
/* 119 */         enumResult = Enum.valueOf(enumClass, (String)value);
/* 120 */       } catch (IllegalArgumentException illegalArgumentException) {}
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 125 */     return enumResult;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static Map<Class<?>, Method> getMethodMap(Class<?> enumClass) {
/* 135 */     return (Map<Class<?>, Method>)VALUE_OF_METHOD_CACHE.computeIfAbsent(enumClass, key -> (Map)Arrays.<Method>stream(enumClass.getMethods()).filter(ModifierUtil::isStatic).filter(()).filter(()).filter(()).collect(Collectors.toMap((), (), ())));
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\convert\impl\EnumConverter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */