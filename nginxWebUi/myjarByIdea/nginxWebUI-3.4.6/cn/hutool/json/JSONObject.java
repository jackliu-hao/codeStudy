package cn.hutool.json;

import cn.hutool.core.bean.BeanPath;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Filter;
import cn.hutool.core.lang.mutable.MutablePair;
import cn.hutool.core.map.CaseInsensitiveMap;
import cn.hutool.core.map.MapWrapper;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.json.serialize.JSONWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

public class JSONObject extends MapWrapper<String, Object> implements JSON, JSONGetter<String> {
   private static final long serialVersionUID = -330220388580734346L;
   public static final int DEFAULT_CAPACITY = 16;
   private JSONConfig config;

   public JSONObject() {
      this(16, false);
   }

   public JSONObject(boolean isOrder) {
      this(16, isOrder);
   }

   public JSONObject(int capacity, boolean isOrder) {
      this(capacity, false, isOrder);
   }

   /** @deprecated */
   @Deprecated
   public JSONObject(int capacity, boolean isIgnoreCase, boolean isOrder) {
      this(capacity, JSONConfig.create().setIgnoreCase(isIgnoreCase));
   }

   public JSONObject(JSONConfig config) {
      this(16, config);
   }

   public JSONObject(int capacity, JSONConfig config) {
      super(InternalJSONUtil.createRawMap(capacity, (JSONConfig)ObjectUtil.defaultIfNull(config, (Object)JSONConfig.create())));
      this.config = (JSONConfig)ObjectUtil.defaultIfNull(config, (Object)JSONConfig.create());
   }

   public JSONObject(Object source) {
      this(source, InternalJSONUtil.defaultIgnoreNullValue(source));
   }

   public JSONObject(Object source, boolean ignoreNullValue) {
      this(source, JSONConfig.create().setIgnoreNullValue(ignoreNullValue));
   }

   /** @deprecated */
   @Deprecated
   public JSONObject(Object source, boolean ignoreNullValue, boolean isOrder) {
      this(source, JSONConfig.create().setIgnoreCase(source instanceof CaseInsensitiveMap).setIgnoreNullValue(ignoreNullValue));
   }

   public JSONObject(Object source, JSONConfig config) {
      this(source, config, (Filter)null);
   }

   public JSONObject(Object source, JSONConfig config, Filter<MutablePair<String, Object>> filter) {
      this(16, config);
      ObjectMapper.of(source).map(this, filter);
   }

   public JSONObject(Object source, String... names) {
      this();
      if (ArrayUtil.isEmpty((Object[])names)) {
         ObjectMapper.of(source).map((JSONObject)this, (Filter)null);
      } else {
         int var5;
         if (source instanceof Map) {
            String[] var4 = names;
            var5 = names.length;

            for(int var6 = 0; var6 < var5; ++var6) {
               String name = var4[var6];
               Object value = ((Map)source).get(name);
               this.putOnce(name, value);
            }
         } else {
            String[] var9 = names;
            int var10 = names.length;

            for(var5 = 0; var5 < var10; ++var5) {
               String name = var9[var5];

               try {
                  this.putOpt(name, ReflectUtil.getFieldValue(source, name));
               } catch (Exception var8) {
               }
            }
         }

      }
   }

   /** @deprecated */
   @Deprecated
   public JSONObject(CharSequence source, boolean isOrder) throws JSONException {
      this(source, (JSONConfig)JSONConfig.create());
   }

   public JSONConfig getConfig() {
      return this.config;
   }

   public JSONObject setDateFormat(String format) {
      this.config.setDateFormat(format);
      return this;
   }

   public <T> T toBean(Type type) {
      return JSON.super.toBean(type, this.config.isIgnoreError());
   }

   public JSONArray toJSONArray(Collection<String> names) throws JSONException {
      if (CollectionUtil.isEmpty(names)) {
         return null;
      } else {
         JSONArray ja = new JSONArray(this.config);
         Iterator var4 = names.iterator();

         while(var4.hasNext()) {
            String name = (String)var4.next();
            Object value = this.get(name);
            if (null != value) {
               ja.set(value);
            }
         }

         return ja;
      }
   }

   public Object getObj(String key, Object defaultValue) {
      return this.getOrDefault(key, defaultValue);
   }

   public Object getByPath(String expression) {
      return BeanPath.create(expression).get(this);
   }

   public <T> T getByPath(String expression, Class<T> resultType) {
      return JSONConverter.jsonConvert(resultType, this.getByPath(expression), true);
   }

   public void putByPath(String expression, Object value) {
      BeanPath.create(expression).set(this, value);
   }

   /** @deprecated */
   @Deprecated
   public JSONObject put(String key, Object value) throws JSONException {
      return this.set(key, value);
   }

   public JSONObject set(String key, Object value) throws JSONException {
      return this.set(key, value, (Filter)null, false);
   }

   public JSONObject set(String key, Object value, Filter<MutablePair<String, Object>> filter, boolean checkDuplicate) throws JSONException {
      if (null == key) {
         return this;
      } else {
         if (null != filter) {
            MutablePair<String, Object> pair = new MutablePair(key, value);
            if (!filter.accept(pair)) {
               return this;
            }

            key = (String)pair.getKey();
            value = pair.getValue();
         }

         boolean ignoreNullValue = this.config.isIgnoreNullValue();
         if (ObjectUtil.isNull(value) && ignoreNullValue) {
            this.remove(key);
         } else {
            if (checkDuplicate && this.containsKey(key)) {
               throw new JSONException("Duplicate key \"{}\"", new Object[]{key});
            }

            super.put(key, JSONUtil.wrap(InternalJSONUtil.testValidity(value), this.config));
         }

         return this;
      }
   }

   public JSONObject putOnce(String key, Object value) throws JSONException {
      return this.setOnce(key, value, (Filter)null);
   }

   public JSONObject setOnce(String key, Object value, Filter<MutablePair<String, Object>> filter) throws JSONException {
      return this.set(key, value, filter, true);
   }

   public JSONObject putOpt(String key, Object value) throws JSONException {
      if (key != null && value != null) {
         this.set(key, value);
      }

      return this;
   }

   public void putAll(Map<? extends String, ?> m) {
      Iterator var2 = m.entrySet().iterator();

      while(var2.hasNext()) {
         Map.Entry<? extends String, ?> entry = (Map.Entry)var2.next();
         this.set((String)entry.getKey(), entry.getValue());
      }

   }

   public JSONObject accumulate(String key, Object value) throws JSONException {
      InternalJSONUtil.testValidity(value);
      Object object = this.getObj(key);
      if (object == null) {
         this.set(key, value);
      } else if (object instanceof JSONArray) {
         ((JSONArray)object).set(value);
      } else {
         this.set(key, JSONUtil.createArray(this.config).set(object).set(value));
      }

      return this;
   }

   public JSONObject append(String key, Object value) throws JSONException {
      InternalJSONUtil.testValidity(value);
      Object object = this.getObj(key);
      if (object == null) {
         this.set(key, (new JSONArray(this.config)).set(value));
      } else {
         if (!(object instanceof JSONArray)) {
            throw new JSONException("JSONObject [" + key + "] is not a JSONArray.");
         }

         this.set(key, ((JSONArray)object).set(value));
      }

      return this;
   }

   public JSONObject increment(String key) throws JSONException {
      Object value = this.getObj(key);
      if (value == null) {
         this.set(key, 1);
      } else if (value instanceof BigInteger) {
         this.set(key, ((BigInteger)value).add(BigInteger.ONE));
      } else if (value instanceof BigDecimal) {
         this.set(key, ((BigDecimal)value).add(BigDecimal.ONE));
      } else if (value instanceof Integer) {
         this.set(key, (Integer)value + 1);
      } else if (value instanceof Long) {
         this.set(key, (Long)value + 1L);
      } else if (value instanceof Double) {
         this.set(key, (Double)value + 1.0);
      } else {
         if (!(value instanceof Float)) {
            throw new JSONException("Unable to increment [" + JSONUtil.quote(key) + "].");
         }

         this.set(key, (Float)value + 1.0F);
      }

      return this;
   }

   public String toString() {
      return this.toJSONString(0);
   }

   public String toJSONString(int indentFactor, Filter<MutablePair<String, Object>> filter) {
      StringWriter sw = new StringWriter();
      synchronized(sw.getBuffer()) {
         return this.write(sw, indentFactor, 0, filter).toString();
      }
   }

   public Writer write(Writer writer, int indentFactor, int indent) throws JSONException {
      return this.write(writer, indentFactor, indent, (Filter)null);
   }

   public Writer write(Writer writer, int indentFactor, int indent, Filter<MutablePair<String, Object>> filter) throws JSONException {
      JSONWriter jsonWriter = JSONWriter.of(writer, indentFactor, indent, this.config).beginObj();
      this.forEach((key, value) -> {
         if (null != filter) {
            MutablePair<String, Object> pair = new MutablePair(key, value);
            if (filter.accept(pair)) {
               jsonWriter.writeField((String)pair.getKey(), pair.getValue());
            }
         } else {
            jsonWriter.writeField(key, value);
         }

      });
      jsonWriter.end();
      return writer;
   }

   public JSONObject clone() throws CloneNotSupportedException {
      JSONObject clone = (JSONObject)super.clone();
      clone.config = this.config;
      return clone;
   }
}
