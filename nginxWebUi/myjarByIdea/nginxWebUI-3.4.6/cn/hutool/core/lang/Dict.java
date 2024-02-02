package cn.hutool.core.lang;

import cn.hutool.core.bean.BeanPath;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.getter.BasicTypeGetter;
import cn.hutool.core.lang.func.Func0;
import cn.hutool.core.lang.func.LambdaUtil;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public class Dict extends LinkedHashMap<String, Object> implements BasicTypeGetter<String> {
   private static final long serialVersionUID = 6135423866861206530L;
   static final float DEFAULT_LOAD_FACTOR = 0.75F;
   static final int DEFAULT_INITIAL_CAPACITY = 16;
   private boolean caseInsensitive;

   public static Dict create() {
      return new Dict();
   }

   public static <T> Dict parse(T bean) {
      return create().parseBean(bean);
   }

   @SafeVarargs
   public static Dict of(Pair<String, Object>... pairs) {
      Dict dict = create();
      Pair[] var2 = pairs;
      int var3 = pairs.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Pair<String, Object> pair = var2[var4];
         dict.put((String)pair.getKey(), pair.getValue());
      }

      return dict;
   }

   public static Dict of(Object... keysAndValues) {
      Dict dict = create();
      String key = null;

      for(int i = 0; i < keysAndValues.length; ++i) {
         if (i % 2 == 0) {
            key = Convert.toStr(keysAndValues[i]);
         } else {
            dict.put(key, keysAndValues[i]);
         }
      }

      return dict;
   }

   public Dict() {
      this(false);
   }

   public Dict(boolean caseInsensitive) {
      this(16, caseInsensitive);
   }

   public Dict(int initialCapacity) {
      this(initialCapacity, false);
   }

   public Dict(int initialCapacity, boolean caseInsensitive) {
      this(initialCapacity, 0.75F, caseInsensitive);
   }

   public Dict(int initialCapacity, float loadFactor) {
      this(initialCapacity, loadFactor, false);
   }

   public Dict(int initialCapacity, float loadFactor, boolean caseInsensitive) {
      super(initialCapacity, loadFactor);
      this.caseInsensitive = caseInsensitive;
   }

   public Dict(Map<String, Object> m) {
      super((Map)(null == m ? new HashMap() : m));
   }

   public <T> T toBean(T bean) {
      return this.toBean(bean, false);
   }

   public <T> T toBeanIgnoreCase(T bean) {
      BeanUtil.fillBeanWithMapIgnoreCase(this, bean, false);
      return bean;
   }

   public <T> T toBean(T bean, boolean isToCamelCase) {
      BeanUtil.fillBeanWithMap(this, bean, isToCamelCase, false);
      return bean;
   }

   public <T> T toBeanWithCamelCase(T bean) {
      BeanUtil.fillBeanWithMap(this, bean, true, false);
      return bean;
   }

   public <T> T toBean(Class<T> clazz) {
      return BeanUtil.toBean(this, clazz);
   }

   public <T> T toBeanIgnoreCase(Class<T> clazz) {
      return BeanUtil.toBeanIgnoreCase(this, clazz, false);
   }

   public <T> Dict parseBean(T bean) {
      Assert.notNull(bean, "Bean class must be not null");
      this.putAll(BeanUtil.beanToMap(bean));
      return this;
   }

   public <T> Dict parseBean(T bean, boolean isToUnderlineCase, boolean ignoreNullValue) {
      Assert.notNull(bean, "Bean class must be not null");
      this.putAll(BeanUtil.beanToMap(bean, isToUnderlineCase, ignoreNullValue));
      return this;
   }

   public <T extends Dict> void removeEqual(T dict, String... withoutNames) {
      HashSet<String> withoutSet = CollUtil.newHashSet((Object[])withoutNames);
      Iterator var4 = dict.entrySet().iterator();

      while(var4.hasNext()) {
         Map.Entry<String, Object> entry = (Map.Entry)var4.next();
         if (!withoutSet.contains(entry.getKey())) {
            Object value = this.get(entry.getKey());
            if (Objects.equals(value, entry.getValue())) {
               this.remove(entry.getKey());
            }
         }
      }

   }

   public Dict filter(String... keys) {
      Dict result = new Dict(keys.length, 1.0F);
      String[] var3 = keys;
      int var4 = keys.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         String key = var3[var5];
         if (this.containsKey(key)) {
            result.put(key, this.get(key));
         }
      }

      return result;
   }

   public Dict set(String attr, Object value) {
      this.put(attr, value);
      return this;
   }

   public Dict setIgnoreNull(String attr, Object value) {
      if (null != attr && null != value) {
         this.set(attr, value);
      }

      return this;
   }

   public Object getObj(String key) {
      return super.get(key);
   }

   public <T> T getBean(String attr) {
      return this.get(attr, (Object)null);
   }

   public <T> T get(String attr, T defaultValue) {
      Object result = this.get(attr);
      return result != null ? result : defaultValue;
   }

   public String getStr(String attr) {
      return Convert.toStr(this.get(attr), (String)null);
   }

   public Integer getInt(String attr) {
      return Convert.toInt(this.get(attr), (Integer)null);
   }

   public Long getLong(String attr) {
      return Convert.toLong(this.get(attr), (Long)null);
   }

   public Float getFloat(String attr) {
      return Convert.toFloat(this.get(attr), (Float)null);
   }

   public Short getShort(String attr) {
      return Convert.toShort(this.get(attr), (Short)null);
   }

   public Character getChar(String attr) {
      return Convert.toChar(this.get(attr), (Character)null);
   }

   public Double getDouble(String attr) {
      return Convert.toDouble(this.get(attr), (Double)null);
   }

   public Byte getByte(String attr) {
      return Convert.toByte(this.get(attr), (Byte)null);
   }

   public Boolean getBool(String attr) {
      return Convert.toBool(this.get(attr), (Boolean)null);
   }

   public BigDecimal getBigDecimal(String attr) {
      return Convert.toBigDecimal(this.get(attr));
   }

   public BigInteger getBigInteger(String attr) {
      return Convert.toBigInteger(this.get(attr));
   }

   public <E extends Enum<E>> E getEnum(Class<E> clazz, String key) {
      return Convert.toEnum(clazz, this.get(key));
   }

   public byte[] getBytes(String attr) {
      return (byte[])this.get(attr, (Object)null);
   }

   public Date getDate(String attr) {
      return (Date)this.get(attr, (Object)null);
   }

   public Time getTime(String attr) {
      return (Time)this.get(attr, (Object)null);
   }

   public Timestamp getTimestamp(String attr) {
      return (Timestamp)this.get(attr, (Object)null);
   }

   public Number getNumber(String attr) {
      return (Number)this.get(attr, (Object)null);
   }

   public <T> T getByPath(String expression) {
      return BeanPath.create(expression).get(this);
   }

   public <T> T getByPath(String expression, Class<T> resultType) {
      return Convert.convert(resultType, this.getByPath(expression));
   }

   public Object get(Object key) {
      return super.get(this.customKey((String)key));
   }

   public Object put(String key, Object value) {
      return super.put(this.customKey(key), value);
   }

   public void putAll(Map<? extends String, ?> m) {
      m.forEach(this::put);
   }

   public Dict clone() {
      return (Dict)super.clone();
   }

   private String customKey(String key) {
      if (this.caseInsensitive && null != key) {
         key = key.toLowerCase();
      }

      return key;
   }

   public Dict setFields(Func0<?>... fields) {
      Arrays.stream(fields).forEach((f) -> {
         this.set(LambdaUtil.getFieldName(f), f.callWithRuntimeException());
      });
      return this;
   }
}
