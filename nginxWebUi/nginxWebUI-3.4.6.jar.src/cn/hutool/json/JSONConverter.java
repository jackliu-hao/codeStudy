/*     */ package cn.hutool.json;
/*     */ 
/*     */ import cn.hutool.core.bean.BeanUtil;
/*     */ import cn.hutool.core.codec.Base64;
/*     */ import cn.hutool.core.convert.Convert;
/*     */ import cn.hutool.core.convert.ConvertException;
/*     */ import cn.hutool.core.convert.Converter;
/*     */ import cn.hutool.core.convert.ConverterRegistry;
/*     */ import cn.hutool.core.convert.impl.ArrayConverter;
/*     */ import cn.hutool.core.convert.impl.BeanConverter;
/*     */ import cn.hutool.core.util.ObjectUtil;
/*     */ import cn.hutool.core.util.ReflectUtil;
/*     */ import cn.hutool.core.util.StrUtil;
/*     */ import cn.hutool.core.util.TypeUtil;
/*     */ import cn.hutool.json.serialize.GlobalSerializeMapping;
/*     */ import cn.hutool.json.serialize.JSONDeserializer;
/*     */ import java.lang.reflect.Type;
/*     */ import java.util.List;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class JSONConverter
/*     */   implements Converter<JSON>
/*     */ {
/*     */   static {
/*  31 */     ConverterRegistry registry = ConverterRegistry.getInstance();
/*  32 */     registry.putCustom(JSON.class, JSONConverter.class);
/*  33 */     registry.putCustom(JSONObject.class, JSONConverter.class);
/*  34 */     registry.putCustom(JSONArray.class, JSONConverter.class);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected static Object toArray(JSONArray jsonArray, Class<?> arrayClass) {
/*  45 */     return (new ArrayConverter(arrayClass)).convert(jsonArray, null);
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
/*     */   protected static <T> List<T> toList(JSONArray jsonArray, Class<T> elementType) {
/*  57 */     return Convert.toList(elementType, jsonArray);
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
/*     */   protected static <T> T jsonConvert(Type targetType, Object value, boolean ignoreError) throws ConvertException {
/*  74 */     if (JSONUtil.isNull(value)) {
/*  75 */       return null;
/*     */     }
/*     */ 
/*     */     
/*  79 */     if (targetType instanceof Class) {
/*  80 */       Class<?> clazz = (Class)targetType;
/*  81 */       if (JSONBeanParser.class.isAssignableFrom(clazz)) {
/*     */         
/*  83 */         JSONBeanParser<Object> target = (JSONBeanParser)ReflectUtil.newInstanceIfPossible(clazz);
/*  84 */         if (null == target) {
/*  85 */           throw new ConvertException("Can not instance [{}]", new Object[] { targetType });
/*     */         }
/*  87 */         target.parse(value);
/*  88 */         return (T)target;
/*  89 */       }  if (targetType == byte[].class && value instanceof CharSequence)
/*     */       {
/*  91 */         return (T)Base64.decode((CharSequence)value);
/*     */       }
/*     */     } 
/*     */     
/*  95 */     return jsonToBean(targetType, value, ignoreError);
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
/*     */   protected static <T> T jsonToBean(Type targetType, Object value, boolean ignoreError) throws ConvertException {
/* 111 */     if (JSONUtil.isNull(value)) {
/* 112 */       return null;
/*     */     }
/*     */     
/* 115 */     if (value instanceof JSON) {
/* 116 */       JSONDeserializer<?> deserializer = GlobalSerializeMapping.getDeserializer(targetType);
/* 117 */       if (null != deserializer)
/*     */       {
/* 119 */         return (T)deserializer.deserialize((JSON)value);
/*     */       }
/*     */ 
/*     */ 
/*     */       
/* 124 */       if (value instanceof JSONGetter && targetType instanceof Class && 
/* 125 */         BeanUtil.hasSetter((Class)targetType)) {
/* 126 */         JSONConfig config = ((JSONGetter)value).getConfig();
/*     */         
/* 128 */         BeanConverter beanConverter = new BeanConverter(targetType, InternalJSONUtil.toCopyOptions(config).setIgnoreError(ignoreError));
/* 129 */         return (T)beanConverter.convertWithCheck(value, null, ignoreError);
/*     */       } 
/*     */     } 
/*     */     
/* 133 */     T targetValue = (T)Convert.convertWithCheck(targetType, value, null, ignoreError);
/*     */     
/* 135 */     if (null == targetValue && false == ignoreError) {
/* 136 */       if (StrUtil.isBlankIfStr(value))
/*     */       {
/*     */         
/* 139 */         return null;
/*     */       }
/*     */       
/* 142 */       throw new ConvertException("Can not convert {} to type {}", new Object[] { value, ObjectUtil.defaultIfNull(TypeUtil.getClass(targetType), targetType) });
/*     */     } 
/*     */     
/* 145 */     return targetValue;
/*     */   }
/*     */ 
/*     */   
/*     */   public JSON convert(Object value, JSON defaultValue) throws IllegalArgumentException {
/* 150 */     return JSONUtil.parse(value);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\json\JSONConverter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */