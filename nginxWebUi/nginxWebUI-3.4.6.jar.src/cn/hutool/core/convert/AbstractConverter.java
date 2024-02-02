/*     */ package cn.hutool.core.convert;
/*     */ 
/*     */ import cn.hutool.core.util.ArrayUtil;
/*     */ import cn.hutool.core.util.CharUtil;
/*     */ import cn.hutool.core.util.ClassUtil;
/*     */ import cn.hutool.core.util.StrUtil;
/*     */ import java.io.Serializable;
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
/*     */ public abstract class AbstractConverter<T>
/*     */   implements Converter<T>, Serializable
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   
/*     */   public T convertQuietly(Object value, T defaultValue) {
/*     */     try {
/*  32 */       return convert(value, defaultValue);
/*  33 */     } catch (Exception e) {
/*  34 */       return defaultValue;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public T convert(Object value, T defaultValue) {
/*  41 */     Class<T> targetType = getTargetType();
/*  42 */     if (null == targetType && null == defaultValue) {
/*  43 */       throw new NullPointerException(StrUtil.format("[type] and [defaultValue] are both null for Converter [{}], we can not know what type to convert !", new Object[] { getClass().getName() }));
/*     */     }
/*  45 */     if (null == targetType)
/*     */     {
/*  47 */       targetType = (Class)defaultValue.getClass();
/*     */     }
/*  49 */     if (null == value) {
/*  50 */       return defaultValue;
/*     */     }
/*     */     
/*  53 */     if (null == defaultValue || targetType.isInstance(defaultValue)) {
/*  54 */       if (targetType.isInstance(value) && false == Map.class.isAssignableFrom(targetType))
/*     */       {
/*  56 */         return targetType.cast(value);
/*     */       }
/*  58 */       T result = convertInternal(value);
/*  59 */       return (null == result) ? defaultValue : result;
/*     */     } 
/*  61 */     throw new IllegalArgumentException(
/*  62 */         StrUtil.format("Default value [{}]({}) is not the instance of [{}]", new Object[] { defaultValue, defaultValue.getClass(), targetType }));
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
/*     */   protected abstract T convertInternal(Object paramObject);
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
/*     */   protected String convertToStr(Object value) {
/*  94 */     if (null == value) {
/*  95 */       return null;
/*     */     }
/*  97 */     if (value instanceof CharSequence)
/*  98 */       return value.toString(); 
/*  99 */     if (ArrayUtil.isArray(value))
/* 100 */       return ArrayUtil.toString(value); 
/* 101 */     if (CharUtil.isChar(value))
/*     */     {
/* 103 */       return CharUtil.toString(((Character)value).charValue());
/*     */     }
/* 105 */     return value.toString();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Class<T> getTargetType() {
/* 115 */     return ClassUtil.getTypeArgument(getClass());
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\convert\AbstractConverter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */