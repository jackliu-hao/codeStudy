package cn.hutool.json;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.convert.ConvertException;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.getter.OptNullBasicTypeFromObjectGetter;
import cn.hutool.core.util.StrUtil;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface JSONGetter<K> extends OptNullBasicTypeFromObjectGetter<K> {
   JSONConfig getConfig();

   default boolean isNull(K key) {
      return JSONUtil.isNull(this.getObj(key));
   }

   default String getStrEscaped(K key) {
      return this.getStrEscaped(key, (String)null);
   }

   default String getStrEscaped(K key, String defaultValue) {
      return JSONUtil.escape(this.getStr(key, defaultValue));
   }

   default JSONArray getJSONArray(K key) {
      Object object = this.getObj(key);
      if (JSONUtil.isNull(object)) {
         return null;
      } else {
         return object instanceof JSON ? (JSONArray)object : new JSONArray(object, this.getConfig());
      }
   }

   default JSONObject getJSONObject(K key) {
      Object object = this.getObj(key);
      if (JSONUtil.isNull(object)) {
         return null;
      } else {
         return object instanceof JSON ? (JSONObject)object : new JSONObject(object, this.getConfig());
      }
   }

   default <T> T getBean(K key, Class<T> beanType) {
      JSONObject obj = this.getJSONObject(key);
      return null == obj ? null : obj.toBean(beanType);
   }

   default <T> List<T> getBeanList(K key, Class<T> beanType) {
      JSONArray jsonArray = this.getJSONArray(key);
      return null == jsonArray ? null : jsonArray.toList(beanType);
   }

   default Date getDate(K key, Date defaultValue) {
      Object obj = this.getObj(key);
      if (JSONUtil.isNull(obj)) {
         return defaultValue;
      } else if (obj instanceof Date) {
         return (Date)obj;
      } else {
         Optional<String> formatOps = Optional.ofNullable(this.getConfig()).map(JSONConfig::getDateFormat);
         if (formatOps.isPresent()) {
            String format = (String)formatOps.get();
            if (StrUtil.isNotBlank(format)) {
               String str = Convert.toStr(obj);
               if (null == str) {
                  return defaultValue;
               }

               return DateUtil.parse((CharSequence)str, (String)format);
            }
         }

         return Convert.toDate(obj, defaultValue);
      }
   }

   default LocalDateTime getLocalDateTime(K key, LocalDateTime defaultValue) {
      Object obj = this.getObj(key);
      if (JSONUtil.isNull(obj)) {
         return defaultValue;
      } else if (obj instanceof LocalDateTime) {
         return (LocalDateTime)obj;
      } else {
         Optional<String> formatOps = Optional.ofNullable(this.getConfig()).map(JSONConfig::getDateFormat);
         if (formatOps.isPresent()) {
            String format = (String)formatOps.get();
            if (StrUtil.isNotBlank(format)) {
               String str = Convert.toStr(obj);
               if (null == str) {
                  return defaultValue;
               }

               return LocalDateTimeUtil.parse(str, (String)format);
            }
         }

         return Convert.toLocalDateTime(obj, defaultValue);
      }
   }

   default byte[] getBytes(K key) {
      return (byte[])this.get(key, byte[].class);
   }

   default <T> T get(K key, Class<T> type) throws ConvertException {
      return this.get(key, type, false);
   }

   default <T> T get(K key, Class<T> type, boolean ignoreError) throws ConvertException {
      Object value = this.getObj(key);
      return JSONUtil.isNull(value) ? null : JSONConverter.jsonConvert(type, value, ignoreError);
   }
}
