package cn.hutool.json;

import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.lang.Filter;
import cn.hutool.core.lang.mutable.MutablePair;
import cn.hutool.core.map.CaseInsensitiveLinkedMap;
import cn.hutool.core.map.CaseInsensitiveTreeMap;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

public final class InternalJSONUtil {
   private InternalJSONUtil() {
   }

   static Object testValidity(Object obj) throws JSONException {
      if (!ObjectUtil.isValidIfNumber(obj)) {
         throw new JSONException("JSON does not allow non-finite numbers.");
      } else {
         return obj;
      }
   }

   static String valueToString(Object value) throws JSONException {
      if (value != null && !(value instanceof JSONNull)) {
         if (value instanceof JSONString) {
            try {
               return ((JSONString)value).toJSONString();
            } catch (Exception var2) {
               throw new JSONException(var2);
            }
         } else if (value instanceof Number) {
            return NumberUtil.toStr((Number)value);
         } else if (!(value instanceof Boolean) && !(value instanceof JSONObject) && !(value instanceof JSONArray)) {
            if (value instanceof Map) {
               Map<?, ?> map = (Map)value;
               return (new JSONObject(map)).toString();
            } else if (value instanceof Collection) {
               Collection<?> coll = (Collection)value;
               return (new JSONArray(coll)).toString();
            } else {
               return ArrayUtil.isArray(value) ? (new JSONArray(value)).toString() : JSONUtil.quote(value.toString());
            }
         } else {
            return value.toString();
         }
      } else {
         return JSONNull.NULL.toString();
      }
   }

   public static Object stringToValue(String string) {
      if (!StrUtil.isEmpty(string) && !"null".equalsIgnoreCase(string)) {
         if ("true".equalsIgnoreCase(string)) {
            return Boolean.TRUE;
         } else if ("false".equalsIgnoreCase(string)) {
            return Boolean.FALSE;
         } else {
            char b = string.charAt(0);
            if (b >= '0' && b <= '9' || b == '-') {
               try {
                  if (StrUtil.containsAnyIgnoreCase(string, new CharSequence[]{".", "e"})) {
                     return new BigDecimal(string);
                  }

                  long myLong = Long.parseLong(string);
                  if (string.equals(Long.toString(myLong))) {
                     if (myLong == (long)((int)myLong)) {
                        return (int)myLong;
                     }

                     return myLong;
                  }
               } catch (Exception var4) {
               }
            }

            return string;
         }
      } else {
         return JSONNull.NULL;
      }
   }

   static JSONObject propertyPut(JSONObject jsonObject, Object key, Object value, Filter<MutablePair<String, Object>> filter) {
      String[] path = StrUtil.splitToArray(Convert.toStr(key), '.');
      int last = path.length - 1;
      JSONObject target = jsonObject;

      for(int i = 0; i < last; ++i) {
         String segment = path[i];
         JSONObject nextTarget = target.getJSONObject(segment);
         if (nextTarget == null) {
            nextTarget = new JSONObject(target.getConfig());
            target.setOnce(segment, nextTarget, filter);
         }

         target = nextTarget;
      }

      target.setOnce(path[last], value, filter);
      return jsonObject;
   }

   static boolean defaultIgnoreNullValue(Object obj) {
      return !(obj instanceof CharSequence) && !(obj instanceof JSONTokener) && !(obj instanceof Map);
   }

   static CopyOptions toCopyOptions(JSONConfig config) {
      return CopyOptions.create().setIgnoreCase(config.isIgnoreCase()).setIgnoreError(config.isIgnoreError()).setIgnoreNullValue(config.isIgnoreNullValue()).setTransientSupport(config.isTransientSupport());
   }

   static Map<String, Object> createRawMap(int capacity, JSONConfig config) {
      if (null == config) {
         config = JSONConfig.create();
      }

      Comparator<String> keyComparator = config.getKeyComparator();
      Object rawHashMap;
      if (config.isIgnoreCase()) {
         if (null != keyComparator) {
            rawHashMap = new CaseInsensitiveTreeMap(keyComparator);
         } else {
            rawHashMap = new CaseInsensitiveLinkedMap(capacity);
         }
      } else if (null != keyComparator) {
         rawHashMap = new TreeMap(keyComparator);
      } else {
         rawHashMap = new LinkedHashMap(capacity);
      }

      return (Map)rawHashMap;
   }
}
