/*     */ package cn.hutool.json;
/*     */ 
/*     */ import cn.hutool.core.bean.BeanPath;
/*     */ import cn.hutool.core.collection.CollectionUtil;
/*     */ import cn.hutool.core.lang.Filter;
/*     */ import cn.hutool.core.lang.mutable.MutablePair;
/*     */ import cn.hutool.core.map.MapWrapper;
/*     */ import cn.hutool.core.util.ArrayUtil;
/*     */ import cn.hutool.core.util.ObjectUtil;
/*     */ import cn.hutool.core.util.ReflectUtil;
/*     */ import cn.hutool.json.serialize.JSONWriter;
/*     */ import java.io.StringWriter;
/*     */ import java.io.Writer;
/*     */ import java.lang.reflect.Type;
/*     */ import java.math.BigDecimal;
/*     */ import java.math.BigInteger;
/*     */ import java.util.Collection;
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
/*     */ public class JSONObject
/*     */   extends MapWrapper<String, Object>
/*     */   implements JSON, JSONGetter<String>
/*     */ {
/*     */   private static final long serialVersionUID = -330220388580734346L;
/*     */   public static final int DEFAULT_CAPACITY = 16;
/*     */   private JSONConfig config;
/*     */   
/*     */   public JSONObject() {
/*  52 */     this(16, false);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JSONObject(boolean isOrder) {
/*  62 */     this(16, isOrder);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JSONObject(int capacity, boolean isOrder) {
/*  73 */     this(capacity, false, isOrder);
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
/*     */   @Deprecated
/*     */   public JSONObject(int capacity, boolean isIgnoreCase, boolean isOrder) {
/*  88 */     this(capacity, JSONConfig.create().setIgnoreCase(isIgnoreCase));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JSONObject(JSONConfig config) {
/*  98 */     this(16, config);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JSONObject(int capacity, JSONConfig config) {
/* 109 */     super(InternalJSONUtil.createRawMap(capacity, (JSONConfig)ObjectUtil.defaultIfNull(config, JSONConfig.create())));
/* 110 */     this.config = (JSONConfig)ObjectUtil.defaultIfNull(config, JSONConfig.create());
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
/*     */   public JSONObject(Object source) {
/* 126 */     this(source, InternalJSONUtil.defaultIgnoreNullValue(source));
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
/*     */   public JSONObject(Object source, boolean ignoreNullValue) {
/* 143 */     this(source, JSONConfig.create().setIgnoreNullValue(ignoreNullValue));
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
/*     */   @Deprecated
/*     */   public JSONObject(Object source, boolean ignoreNullValue, boolean isOrder) {
/* 164 */     this(source, JSONConfig.create()
/* 165 */         .setIgnoreCase(source instanceof cn.hutool.core.map.CaseInsensitiveMap)
/* 166 */         .setIgnoreNullValue(ignoreNullValue));
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
/*     */   public JSONObject(Object source, JSONConfig config) {
/* 188 */     this(source, config, (Filter<MutablePair<String, Object>>)null);
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
/*     */   public JSONObject(Object source, JSONConfig config, Filter<MutablePair<String, Object>> filter) {
/* 210 */     this(16, config);
/* 211 */     ObjectMapper.of(source).map(this, filter);
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
/*     */   public JSONObject(Object source, String... names) {
/* 229 */     this();
/* 230 */     if (ArrayUtil.isEmpty((Object[])names)) {
/* 231 */       ObjectMapper.of(source).map(this, (Filter<MutablePair<String, Object>>)null);
/*     */       
/*     */       return;
/*     */     } 
/* 235 */     if (source instanceof Map) {
/*     */       
/* 237 */       for (String name : names) {
/* 238 */         Object value = ((Map)source).get(name);
/* 239 */         putOnce(name, value);
/*     */       } 
/*     */     } else {
/* 242 */       for (String name : names) {
/*     */         try {
/* 244 */           putOpt(name, ReflectUtil.getFieldValue(source, name));
/* 245 */         } catch (Exception exception) {}
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
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public JSONObject(CharSequence source, boolean isOrder) throws JSONException {
/* 264 */     this(source, JSONConfig.create());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JSONConfig getConfig() {
/* 271 */     return this.config;
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
/*     */   public JSONObject setDateFormat(String format) {
/* 283 */     this.config.setDateFormat(format);
/* 284 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public <T> T toBean(Type type) {
/* 289 */     return toBean(type, this.config.isIgnoreError());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JSONArray toJSONArray(Collection<String> names) throws JSONException {
/* 300 */     if (CollectionUtil.isEmpty(names)) {
/* 301 */       return null;
/*     */     }
/* 303 */     JSONArray ja = new JSONArray(this.config);
/*     */     
/* 305 */     for (String name : names) {
/* 306 */       Object value = get(name);
/* 307 */       if (null != value) {
/* 308 */         ja.set(value);
/*     */       }
/*     */     } 
/* 311 */     return ja;
/*     */   }
/*     */ 
/*     */   
/*     */   public Object getObj(String key, Object defaultValue) {
/* 316 */     return getOrDefault(key, defaultValue);
/*     */   }
/*     */ 
/*     */   
/*     */   public Object getByPath(String expression) {
/* 321 */     return BeanPath.create(expression).get(this);
/*     */   }
/*     */ 
/*     */   
/*     */   public <T> T getByPath(String expression, Class<T> resultType) {
/* 326 */     return JSONConverter.jsonConvert(resultType, getByPath(expression), true);
/*     */   }
/*     */ 
/*     */   
/*     */   public void putByPath(String expression, Object value) {
/* 331 */     BeanPath.create(expression).set(this, value);
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
/*     */   @Deprecated
/*     */   public JSONObject put(String key, Object value) throws JSONException {
/* 346 */     return set(key, value);
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
/*     */   public JSONObject set(String key, Object value) throws JSONException {
/* 358 */     return set(key, value, (Filter<MutablePair<String, Object>>)null, false);
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
/*     */   public JSONObject set(String key, Object value, Filter<MutablePair<String, Object>> filter, boolean checkDuplicate) throws JSONException {
/* 373 */     if (null == key) {
/* 374 */       return this;
/*     */     }
/*     */ 
/*     */     
/* 378 */     if (null != filter) {
/* 379 */       MutablePair<String, Object> pair = new MutablePair(key, value);
/* 380 */       if (filter.accept(pair)) {
/*     */         
/* 382 */         key = (String)pair.getKey();
/* 383 */         value = pair.getValue();
/*     */       } else {
/*     */         
/* 386 */         return this;
/*     */       } 
/*     */     } 
/*     */     
/* 390 */     boolean ignoreNullValue = this.config.isIgnoreNullValue();
/* 391 */     if (ObjectUtil.isNull(value) && ignoreNullValue) {
/*     */       
/* 393 */       remove(key);
/*     */     } else {
/* 395 */       if (checkDuplicate && containsKey(key)) {
/* 396 */         throw new JSONException("Duplicate key \"{}\"", new Object[] { key });
/*     */       }
/*     */       
/* 399 */       super.put(key, JSONUtil.wrap(InternalJSONUtil.testValidity(value), this.config));
/*     */     } 
/* 401 */     return this;
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
/*     */   public JSONObject putOnce(String key, Object value) throws JSONException {
/* 413 */     return setOnce(key, value, (Filter<MutablePair<String, Object>>)null);
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
/*     */   public JSONObject setOnce(String key, Object value, Filter<MutablePair<String, Object>> filter) throws JSONException {
/* 427 */     return set(key, value, filter, true);
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
/*     */   public JSONObject putOpt(String key, Object value) throws JSONException {
/* 439 */     if (key != null && value != null) {
/* 440 */       set(key, value);
/*     */     }
/* 442 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public void putAll(Map<? extends String, ?> m) {
/* 447 */     for (Map.Entry<? extends String, ?> entry : m.entrySet()) {
/* 448 */       set(entry.getKey(), entry.getValue());
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
/*     */   public JSONObject accumulate(String key, Object value) throws JSONException {
/* 462 */     InternalJSONUtil.testValidity(value);
/* 463 */     Object object = getObj(key);
/* 464 */     if (object == null) {
/* 465 */       set(key, value);
/* 466 */     } else if (object instanceof JSONArray) {
/* 467 */       ((JSONArray)object).set(value);
/*     */     } else {
/* 469 */       set(key, JSONUtil.createArray(this.config).set(object).set(value));
/*     */     } 
/* 471 */     return this;
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
/*     */   public JSONObject append(String key, Object value) throws JSONException {
/* 483 */     InternalJSONUtil.testValidity(value);
/* 484 */     Object object = getObj(key);
/* 485 */     if (object == null) {
/* 486 */       set(key, (new JSONArray(this.config)).set(value));
/* 487 */     } else if (object instanceof JSONArray) {
/* 488 */       set(key, ((JSONArray)object).set(value));
/*     */     } else {
/* 490 */       throw new JSONException("JSONObject [" + key + "] is not a JSONArray.");
/*     */     } 
/* 492 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JSONObject increment(String key) throws JSONException {
/* 503 */     Object value = getObj(key);
/* 504 */     if (value == null) {
/* 505 */       set(key, Integer.valueOf(1));
/* 506 */     } else if (value instanceof BigInteger) {
/* 507 */       set(key, ((BigInteger)value).add(BigInteger.ONE));
/* 508 */     } else if (value instanceof BigDecimal) {
/* 509 */       set(key, ((BigDecimal)value).add(BigDecimal.ONE));
/* 510 */     } else if (value instanceof Integer) {
/* 511 */       set(key, Integer.valueOf(((Integer)value).intValue() + 1));
/* 512 */     } else if (value instanceof Long) {
/* 513 */       set(key, Long.valueOf(((Long)value).longValue() + 1L));
/* 514 */     } else if (value instanceof Double) {
/* 515 */       set(key, Double.valueOf(((Double)value).doubleValue() + 1.0D));
/* 516 */     } else if (value instanceof Float) {
/* 517 */       set(key, Float.valueOf(((Float)value).floatValue() + 1.0F));
/*     */     } else {
/* 519 */       throw new JSONException("Unable to increment [" + JSONUtil.quote(key) + "].");
/*     */     } 
/* 521 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/* 532 */     return toJSONString(0);
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
/*     */   public String toJSONString(int indentFactor, Filter<MutablePair<String, Object>> filter) {
/* 545 */     StringWriter sw = new StringWriter();
/* 546 */     synchronized (sw.getBuffer()) {
/* 547 */       return write(sw, indentFactor, 0, filter).toString();
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public Writer write(Writer writer, int indentFactor, int indent) throws JSONException {
/* 553 */     return write(writer, indentFactor, indent, (Filter<MutablePair<String, Object>>)null);
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
/*     */   public Writer write(Writer writer, int indentFactor, int indent, Filter<MutablePair<String, Object>> filter) throws JSONException {
/* 570 */     JSONWriter jsonWriter = JSONWriter.of(writer, indentFactor, indent, this.config).beginObj();
/* 571 */     forEach((key, value) -> {
/*     */           if (null != filter) {
/*     */             MutablePair<String, Object> pair = new MutablePair(key, value);
/*     */             
/*     */             if (filter.accept(pair)) {
/*     */               jsonWriter.writeField((String)pair.getKey(), pair.getValue());
/*     */             }
/*     */           } else {
/*     */             jsonWriter.writeField(key, value);
/*     */           } 
/*     */         });
/* 582 */     jsonWriter.end();
/*     */     
/* 584 */     return writer;
/*     */   }
/*     */ 
/*     */   
/*     */   public JSONObject clone() throws CloneNotSupportedException {
/* 589 */     JSONObject clone = (JSONObject)super.clone();
/* 590 */     clone.config = this.config;
/* 591 */     return clone;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\json\JSONObject.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */