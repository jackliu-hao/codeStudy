/*     */ package cn.hutool.core.lang;
/*     */ 
/*     */ import cn.hutool.core.bean.BeanPath;
/*     */ import cn.hutool.core.bean.BeanUtil;
/*     */ import cn.hutool.core.collection.CollUtil;
/*     */ import cn.hutool.core.convert.Convert;
/*     */ import cn.hutool.core.getter.BasicTypeGetter;
/*     */ import cn.hutool.core.lang.func.Func0;
/*     */ import cn.hutool.core.lang.func.LambdaUtil;
/*     */ import java.math.BigDecimal;
/*     */ import java.math.BigInteger;
/*     */ import java.sql.Time;
/*     */ import java.sql.Timestamp;
/*     */ import java.util.Arrays;
/*     */ import java.util.Date;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.LinkedHashMap;
/*     */ import java.util.Map;
/*     */ import java.util.Objects;
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
/*     */ public class Dict
/*     */   extends LinkedHashMap<String, Object>
/*     */   implements BasicTypeGetter<String>
/*     */ {
/*     */   private static final long serialVersionUID = 6135423866861206530L;
/*     */   static final float DEFAULT_LOAD_FACTOR = 0.75F;
/*     */   static final int DEFAULT_INITIAL_CAPACITY = 16;
/*     */   private boolean caseInsensitive;
/*     */   
/*     */   public static Dict create() {
/*  47 */     return new Dict();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static <T> Dict parse(T bean) {
/*  58 */     return create().parseBean(bean);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @SafeVarargs
/*     */   public static Dict of(Pair<String, Object>... pairs) {
/*  70 */     Dict dict = create();
/*  71 */     for (Pair<String, Object> pair : pairs) {
/*  72 */       dict.put(pair.getKey(), pair.getValue());
/*     */     }
/*  74 */     return dict;
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
/*     */   public static Dict of(Object... keysAndValues) {
/*  96 */     Dict dict = create();
/*     */     
/*  98 */     String key = null;
/*  99 */     for (int i = 0; i < keysAndValues.length; i++) {
/* 100 */       if (i % 2 == 0) {
/* 101 */         key = Convert.toStr(keysAndValues[i]);
/*     */       } else {
/* 103 */         dict.put(key, keysAndValues[i]);
/*     */       } 
/*     */     } 
/*     */     
/* 107 */     return dict;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Dict() {
/* 117 */     this(false);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Dict(boolean caseInsensitive) {
/* 126 */     this(16, caseInsensitive);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Dict(int initialCapacity) {
/* 135 */     this(initialCapacity, false);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Dict(int initialCapacity, boolean caseInsensitive) {
/* 145 */     this(initialCapacity, 0.75F, caseInsensitive);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Dict(int initialCapacity, float loadFactor) {
/* 155 */     this(initialCapacity, loadFactor, false);
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
/*     */   public Dict(int initialCapacity, float loadFactor, boolean caseInsensitive) {
/* 167 */     super(initialCapacity, loadFactor);
/* 168 */     this.caseInsensitive = caseInsensitive;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Dict(Map<String, Object> m) {
/* 177 */     super((null == m) ? new HashMap<>() : m);
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
/*     */   public <T> T toBean(T bean) {
/* 189 */     return toBean(bean, false);
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
/*     */   public <T> T toBeanIgnoreCase(T bean) {
/* 201 */     BeanUtil.fillBeanWithMapIgnoreCase(this, bean, false);
/* 202 */     return bean;
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
/*     */   public <T> T toBean(T bean, boolean isToCamelCase) {
/* 214 */     BeanUtil.fillBeanWithMap(this, bean, isToCamelCase, false);
/* 215 */     return bean;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public <T> T toBeanWithCamelCase(T bean) {
/* 226 */     BeanUtil.fillBeanWithMap(this, bean, true, false);
/* 227 */     return bean;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public <T> T toBean(Class<T> clazz) {
/* 238 */     return (T)BeanUtil.toBean(this, clazz);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public <T> T toBeanIgnoreCase(Class<T> clazz) {
/* 249 */     return (T)BeanUtil.toBeanIgnoreCase(this, clazz, false);
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
/*     */   public <T> Dict parseBean(T bean) {
/* 261 */     Assert.notNull(bean, "Bean class must be not null", new Object[0]);
/* 262 */     putAll(BeanUtil.beanToMap(bean, new String[0]));
/* 263 */     return this;
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
/*     */   public <T> Dict parseBean(T bean, boolean isToUnderlineCase, boolean ignoreNullValue) {
/* 277 */     Assert.notNull(bean, "Bean class must be not null", new Object[0]);
/* 278 */     putAll(BeanUtil.beanToMap(bean, isToUnderlineCase, ignoreNullValue));
/* 279 */     return this;
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
/*     */   public <T extends Dict> void removeEqual(T dict, String... withoutNames) {
/* 291 */     HashSet<String> withoutSet = CollUtil.newHashSet((Object[])withoutNames);
/* 292 */     for (Map.Entry<String, Object> entry : dict.entrySet()) {
/* 293 */       if (withoutSet.contains(entry.getKey())) {
/*     */         continue;
/*     */       }
/*     */       
/* 297 */       Object value = get(entry.getKey());
/* 298 */       if (Objects.equals(value, entry.getValue())) {
/* 299 */         remove(entry.getKey());
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
/*     */   public Dict filter(String... keys) {
/* 312 */     Dict result = new Dict(keys.length, 1.0F);
/*     */     
/* 314 */     for (String key : keys) {
/* 315 */       if (containsKey(key)) {
/* 316 */         result.put(key, get(key));
/*     */       }
/*     */     } 
/* 319 */     return result;
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
/*     */   public Dict set(String attr, Object value) {
/* 332 */     put(attr, value);
/* 333 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Dict setIgnoreNull(String attr, Object value) {
/* 344 */     if (null != attr && null != value) {
/* 345 */       set(attr, value);
/*     */     }
/* 347 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Object getObj(String key) {
/* 355 */     return super.get(key);
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
/*     */   public <T> T getBean(String attr) {
/* 367 */     return get(attr, (T)null);
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
/*     */   public <T> T get(String attr, T defaultValue) {
/* 380 */     Object result = get(attr);
/* 381 */     return (result != null) ? (T)result : defaultValue;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getStr(String attr) {
/* 390 */     return Convert.toStr(get(attr), null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Integer getInt(String attr) {
/* 399 */     return Convert.toInt(get(attr), null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Long getLong(String attr) {
/* 408 */     return Convert.toLong(get(attr), null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Float getFloat(String attr) {
/* 417 */     return Convert.toFloat(get(attr), null);
/*     */   }
/*     */ 
/*     */   
/*     */   public Short getShort(String attr) {
/* 422 */     return Convert.toShort(get(attr), null);
/*     */   }
/*     */ 
/*     */   
/*     */   public Character getChar(String attr) {
/* 427 */     return Convert.toChar(get(attr), null);
/*     */   }
/*     */ 
/*     */   
/*     */   public Double getDouble(String attr) {
/* 432 */     return Convert.toDouble(get(attr), null);
/*     */   }
/*     */ 
/*     */   
/*     */   public Byte getByte(String attr) {
/* 437 */     return Convert.toByte(get(attr), null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Boolean getBool(String attr) {
/* 446 */     return Convert.toBool(get(attr), null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public BigDecimal getBigDecimal(String attr) {
/* 455 */     return Convert.toBigDecimal(get(attr));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public BigInteger getBigInteger(String attr) {
/* 464 */     return Convert.toBigInteger(get(attr));
/*     */   }
/*     */ 
/*     */   
/*     */   public <E extends Enum<E>> E getEnum(Class<E> clazz, String key) {
/* 469 */     return (E)Convert.toEnum(clazz, get(key));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public byte[] getBytes(String attr) {
/* 477 */     return get(attr, (byte[])null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Date getDate(String attr) {
/* 486 */     return get(attr, (Date)null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Time getTime(String attr) {
/* 494 */     return get(attr, (Time)null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Timestamp getTimestamp(String attr) {
/* 502 */     return get(attr, (Timestamp)null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Number getNumber(String attr) {
/* 510 */     return get(attr, (Number)null);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public <T> T getByPath(String expression) {
/* 537 */     return (T)BeanPath.create(expression).get(this);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public <T> T getByPath(String expression, Class<T> resultType) {
/* 566 */     return (T)Convert.convert(resultType, getByPath(expression));
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Object get(Object key) {
/* 572 */     return super.get(customKey((String)key));
/*     */   }
/*     */ 
/*     */   
/*     */   public Object put(String key, Object value) {
/* 577 */     return super.put(customKey(key), value);
/*     */   }
/*     */ 
/*     */   
/*     */   public void putAll(Map<? extends String, ?> m) {
/* 582 */     m.forEach(this::put);
/*     */   }
/*     */ 
/*     */   
/*     */   public Dict clone() {
/* 587 */     return (Dict)super.clone();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private String customKey(String key) {
/* 597 */     if (this.caseInsensitive && null != key) {
/* 598 */       key = key.toLowerCase();
/*     */     }
/* 600 */     return key;
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
/*     */   public Dict setFields(Func0<?>... fields) {
/* 616 */     Arrays.<Func0<?>>stream(fields).forEach(f -> set(LambdaUtil.getFieldName(f), f.callWithRuntimeException()));
/* 617 */     return this;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\lang\Dict.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */