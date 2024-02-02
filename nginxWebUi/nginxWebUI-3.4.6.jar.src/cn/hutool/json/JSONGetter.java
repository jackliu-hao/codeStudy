/*     */ package cn.hutool.json;
/*     */ 
/*     */ import cn.hutool.core.convert.Convert;
/*     */ import cn.hutool.core.convert.ConvertException;
/*     */ import cn.hutool.core.date.DateUtil;
/*     */ import cn.hutool.core.date.LocalDateTimeUtil;
/*     */ import cn.hutool.core.getter.OptNullBasicTypeFromObjectGetter;
/*     */ import cn.hutool.core.util.StrUtil;
/*     */ import java.time.LocalDateTime;
/*     */ import java.util.Date;
/*     */ import java.util.List;
/*     */ import java.util.Optional;
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
/*     */ public interface JSONGetter<K>
/*     */   extends OptNullBasicTypeFromObjectGetter<K>
/*     */ {
/*     */   JSONConfig getConfig();
/*     */   
/*     */   default boolean isNull(K key) {
/*  38 */     return JSONUtil.isNull(getObj(key));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   default String getStrEscaped(K key) {
/*  49 */     return getStrEscaped(key, (String)null);
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
/*     */   default String getStrEscaped(K key, String defaultValue) {
/*  61 */     return JSONUtil.escape(getStr(key, defaultValue));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   default JSONArray getJSONArray(K key) {
/*  72 */     Object object = getObj(key);
/*  73 */     if (JSONUtil.isNull(object)) {
/*  74 */       return null;
/*     */     }
/*     */     
/*  77 */     if (object instanceof JSON) {
/*  78 */       return (JSONArray)object;
/*     */     }
/*  80 */     return new JSONArray(object, getConfig());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   default JSONObject getJSONObject(K key) {
/*  91 */     Object object = getObj(key);
/*  92 */     if (JSONUtil.isNull(object)) {
/*  93 */       return null;
/*     */     }
/*     */     
/*  96 */     if (object instanceof JSON) {
/*  97 */       return (JSONObject)object;
/*     */     }
/*  99 */     return new JSONObject(object, getConfig());
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
/*     */   default <T> T getBean(K key, Class<T> beanType) {
/* 113 */     JSONObject obj = getJSONObject(key);
/* 114 */     return (null == obj) ? null : obj.<T>toBean(beanType);
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
/*     */   default <T> List<T> getBeanList(K key, Class<T> beanType) {
/* 128 */     JSONArray jsonArray = getJSONArray(key);
/* 129 */     return (null == jsonArray) ? null : jsonArray.<T>toList(beanType);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   default Date getDate(K key, Date defaultValue) {
/* 135 */     Object obj = getObj(key);
/* 136 */     if (JSONUtil.isNull(obj)) {
/* 137 */       return defaultValue;
/*     */     }
/* 139 */     if (obj instanceof Date) {
/* 140 */       return (Date)obj;
/*     */     }
/*     */     
/* 143 */     Optional<String> formatOps = Optional.<JSONConfig>ofNullable(getConfig()).map(JSONConfig::getDateFormat);
/* 144 */     if (formatOps.isPresent()) {
/* 145 */       String format = formatOps.get();
/* 146 */       if (StrUtil.isNotBlank(format)) {
/*     */         
/* 148 */         String str = Convert.toStr(obj);
/* 149 */         if (null == str) {
/* 150 */           return defaultValue;
/*     */         }
/* 152 */         return (Date)DateUtil.parse(str, format);
/*     */       } 
/*     */     } 
/*     */     
/* 156 */     return Convert.toDate(obj, defaultValue);
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
/*     */   default LocalDateTime getLocalDateTime(K key, LocalDateTime defaultValue) {
/* 169 */     Object obj = getObj(key);
/* 170 */     if (JSONUtil.isNull(obj)) {
/* 171 */       return defaultValue;
/*     */     }
/* 173 */     if (obj instanceof LocalDateTime) {
/* 174 */       return (LocalDateTime)obj;
/*     */     }
/*     */     
/* 177 */     Optional<String> formatOps = Optional.<JSONConfig>ofNullable(getConfig()).map(JSONConfig::getDateFormat);
/* 178 */     if (formatOps.isPresent()) {
/* 179 */       String format = formatOps.get();
/* 180 */       if (StrUtil.isNotBlank(format)) {
/*     */         
/* 182 */         String str = Convert.toStr(obj);
/* 183 */         if (null == str) {
/* 184 */           return defaultValue;
/*     */         }
/* 186 */         return LocalDateTimeUtil.parse(str, format);
/*     */       } 
/*     */     } 
/*     */     
/* 190 */     return Convert.toLocalDateTime(obj, defaultValue);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   default byte[] getBytes(K key) {
/* 201 */     return get(key, (Class)byte[].class);
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
/*     */   default <T> T get(K key, Class<T> type) throws ConvertException {
/* 216 */     return get(key, type, false);
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
/*     */   default <T> T get(K key, Class<T> type, boolean ignoreError) throws ConvertException {
/* 231 */     Object value = getObj(key);
/* 232 */     if (JSONUtil.isNull(value)) {
/* 233 */       return null;
/*     */     }
/* 235 */     return JSONConverter.jsonConvert(type, value, ignoreError);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\json\JSONGetter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */