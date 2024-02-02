package cn.hutool.json;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.io.file.FileReader;
import cn.hutool.core.lang.TypeReference;
import cn.hutool.core.map.MapWrapper;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.HexUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.TypeUtil;
import cn.hutool.json.serialize.GlobalSerializeMapping;
import cn.hutool.json.serialize.JSONArraySerializer;
import cn.hutool.json.serialize.JSONDeserializer;
import cn.hutool.json.serialize.JSONObjectSerializer;
import cn.hutool.json.serialize.JSONSerializer;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.sql.SQLException;
import java.time.temporal.TemporalAccessor;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class JSONUtil {
   public static JSONObject createObj() {
      return new JSONObject();
   }

   public static JSONObject createObj(JSONConfig config) {
      return new JSONObject(config);
   }

   public static JSONArray createArray() {
      return new JSONArray();
   }

   public static JSONArray createArray(JSONConfig config) {
      return new JSONArray(config);
   }

   public static JSONObject parseObj(String jsonStr) {
      return new JSONObject(jsonStr);
   }

   public static JSONObject parseObj(Object obj) {
      return parseObj(obj, (JSONConfig)null);
   }

   public static JSONObject parseObj(Object obj, JSONConfig config) {
      return new JSONObject(obj, (JSONConfig)ObjectUtil.defaultIfNull(config, (Supplier)(JSONConfig::create)));
   }

   public static JSONObject parseObj(Object obj, boolean ignoreNullValue) {
      return new JSONObject(obj, ignoreNullValue);
   }

   /** @deprecated */
   @Deprecated
   public static JSONObject parseObj(Object obj, boolean ignoreNullValue, boolean isOrder) {
      return new JSONObject(obj, ignoreNullValue);
   }

   public static JSONArray parseArray(String jsonStr) {
      return new JSONArray(jsonStr);
   }

   public static JSONArray parseArray(Object arrayOrCollection) {
      return parseArray(arrayOrCollection, (JSONConfig)null);
   }

   public static JSONArray parseArray(Object arrayOrCollection, JSONConfig config) {
      return new JSONArray(arrayOrCollection, config);
   }

   public static JSONArray parseArray(Object arrayOrCollection, boolean ignoreNullValue) {
      return new JSONArray(arrayOrCollection, ignoreNullValue);
   }

   public static JSON parse(Object obj) {
      return parse(obj, (JSONConfig)null);
   }

   public static JSON parse(Object obj, JSONConfig config) {
      if (null == obj) {
         return null;
      } else {
         Object json;
         if (obj instanceof JSON) {
            json = (JSON)obj;
         } else if (obj instanceof CharSequence) {
            String jsonStr = StrUtil.trim((CharSequence)obj);
            json = isTypeJSONArray(jsonStr) ? parseArray(jsonStr, config) : parseObj(jsonStr, config);
         } else if (obj instanceof MapWrapper) {
            json = parseObj(obj, config);
         } else if (!(obj instanceof Iterable) && !(obj instanceof Iterator) && !ArrayUtil.isArray(obj)) {
            json = parseObj(obj, config);
         } else {
            json = parseArray(obj, config);
         }

         return (JSON)json;
      }
   }

   public static JSONObject parseFromXml(String xmlStr) {
      return XML.toJSONObject(xmlStr);
   }

   public static JSON readJSON(File file, Charset charset) throws IORuntimeException {
      return parse(FileReader.create(file, charset).readString());
   }

   public static JSONObject readJSONObject(File file, Charset charset) throws IORuntimeException {
      return parseObj(FileReader.create(file, charset).readString());
   }

   public static JSONArray readJSONArray(File file, Charset charset) throws IORuntimeException {
      return parseArray(FileReader.create(file, charset).readString());
   }

   public static String toJsonStr(JSON json, int indentFactor) {
      return null == json ? null : json.toJSONString(indentFactor);
   }

   public static String toJsonStr(JSON json) {
      return null == json ? null : json.toJSONString(0);
   }

   public static void toJsonStr(JSON json, Writer writer) {
      if (null != json) {
         json.write(writer);
      }

   }

   public static String toJsonPrettyStr(JSON json) {
      return null == json ? null : json.toJSONString(4);
   }

   public static String toJsonStr(Object obj) {
      return toJsonStr(obj, (JSONConfig)null);
   }

   public static String toJsonStr(Object obj, JSONConfig jsonConfig) {
      if (null == obj) {
         return null;
      } else {
         return obj instanceof CharSequence ? StrUtil.str((CharSequence)obj) : toJsonStr(parse(obj, jsonConfig));
      }
   }

   public static void toJsonStr(Object obj, Writer writer) {
      if (null != obj) {
         toJsonStr(parse(obj), writer);
      }

   }

   public static String toJsonPrettyStr(Object obj) {
      return toJsonPrettyStr(parse(obj));
   }

   public static String toXmlStr(JSON json) {
      return XML.toXml(json);
   }

   public static <T> T toBean(String jsonString, Class<T> beanClass) {
      return toBean(parseObj(jsonString), beanClass);
   }

   public static <T> T toBean(String jsonString, JSONConfig config, Class<T> beanClass) {
      return toBean(parseObj(jsonString, config), beanClass);
   }

   public static <T> T toBean(JSONObject json, Class<T> beanClass) {
      return null == json ? null : json.toBean(beanClass);
   }

   public static <T> T toBean(String jsonString, TypeReference<T> typeReference, boolean ignoreError) {
      return toBean(jsonString, typeReference.getType(), ignoreError);
   }

   public static <T> T toBean(String jsonString, Type beanType, boolean ignoreError) {
      return parse(jsonString, JSONConfig.create().setIgnoreError(ignoreError)).toBean(beanType);
   }

   public static <T> T toBean(JSON json, TypeReference<T> typeReference, boolean ignoreError) {
      return toBean(json, typeReference.getType(), ignoreError);
   }

   public static <T> T toBean(JSON json, Type beanType, boolean ignoreError) {
      return null == json ? null : json.toBean(beanType, ignoreError);
   }

   public static <T> List<T> toList(String jsonArray, Class<T> elementType) {
      return toList(parseArray(jsonArray), elementType);
   }

   public static <T> List<T> toList(JSONArray jsonArray, Class<T> elementType) {
      return null == jsonArray ? null : jsonArray.toList(elementType);
   }

   public static Object getByPath(JSON json, String expression) {
      return getByPath(json, expression, (Object)null);
   }

   public static <T> T getByPath(JSON json, String expression, T defaultValue) {
      if (null != json && !StrUtil.isBlank(expression)) {
         if (null != defaultValue) {
            Class<T> type = defaultValue.getClass();
            return ObjectUtil.defaultIfNull(json.getByPath(expression, type), defaultValue);
         } else {
            return json.getByPath(expression);
         }
      } else {
         return defaultValue;
      }
   }

   public static void putByPath(JSON json, String expression, Object value) {
      json.putByPath(expression, value);
   }

   public static String quote(String string) {
      return quote(string, true);
   }

   public static String quote(String string, boolean isWrap) {
      StringWriter sw = new StringWriter();

      try {
         return quote(string, sw, isWrap).toString();
      } catch (IOException var4) {
         return "";
      }
   }

   public static Writer quote(String str, Writer writer) throws IOException {
      return quote(str, writer, true);
   }

   public static Writer quote(String str, Writer writer, boolean isWrap) throws IOException {
      if (StrUtil.isEmpty(str)) {
         if (isWrap) {
            writer.write("\"\"");
         }

         return writer;
      } else {
         int len = str.length();
         if (isWrap) {
            writer.write(34);
         }

         for(int i = 0; i < len; ++i) {
            char c = str.charAt(i);
            switch (c) {
               case '"':
               case '\\':
                  writer.write("\\");
                  writer.write(c);
                  break;
               default:
                  writer.write(escape(c));
            }
         }

         if (isWrap) {
            writer.write(34);
         }

         return writer;
      }
   }

   public static String escape(String str) {
      if (StrUtil.isEmpty(str)) {
         return str;
      } else {
         int len = str.length();
         StringBuilder builder = new StringBuilder(len);

         for(int i = 0; i < len; ++i) {
            char c = str.charAt(i);
            builder.append(escape(c));
         }

         return builder.toString();
      }
   }

   public static Object wrap(Object object, JSONConfig jsonConfig) {
      if (object == null) {
         return jsonConfig.isIgnoreNullValue() ? null : JSONNull.NULL;
      } else if (!(object instanceof JSON) && !ObjectUtil.isNull(object) && !(object instanceof JSONString) && !(object instanceof CharSequence) && !(object instanceof Number) && !ObjectUtil.isBasicType(object)) {
         JSONSerializer serializer = GlobalSerializeMapping.getSerializer(object.getClass());
         if (null != serializer) {
            Type jsonType = TypeUtil.getTypeArgument(serializer.getClass());
            if (null != jsonType) {
               if (serializer instanceof JSONObjectSerializer) {
                  serializer.serialize(new JSONObject(jsonConfig), object);
               } else if (serializer instanceof JSONArraySerializer) {
                  serializer.serialize(new JSONArray(jsonConfig), object);
               }
            }
         }

         try {
            if (object instanceof SQLException) {
               return object.toString();
            } else if (!(object instanceof Iterable) && !ArrayUtil.isArray(object)) {
               if (!(object instanceof Map) && !(object instanceof Map.Entry)) {
                  if (!(object instanceof Date) && !(object instanceof Calendar) && !(object instanceof TemporalAccessor)) {
                     if (object instanceof Enum) {
                        return object.toString();
                     } else {
                        return ClassUtil.isJdkClass(object.getClass()) ? object.toString() : new JSONObject(object, jsonConfig);
                     }
                  } else {
                     return object;
                  }
               } else {
                  return new JSONObject(object, jsonConfig);
               }
            } else {
               return object instanceof byte[] ? Base64.encode((byte[])((byte[])object)) : new JSONArray(object, jsonConfig);
            }
         } catch (Exception var4) {
            return null;
         }
      } else {
         return object;
      }
   }

   public static String formatJsonStr(String jsonStr) {
      return JSONStrFormatter.format(jsonStr);
   }

   /** @deprecated */
   @Deprecated
   public static boolean isJson(String str) {
      return isTypeJSON(str);
   }

   public static boolean isTypeJSON(String str) {
      return isTypeJSONObject(str) || isTypeJSONArray(str);
   }

   /** @deprecated */
   @Deprecated
   public static boolean isJsonObj(String str) {
      return isTypeJSONObject(str);
   }

   public static boolean isTypeJSONObject(String str) {
      return StrUtil.isBlank(str) ? false : StrUtil.isWrap(StrUtil.trim(str), '{', '}');
   }

   /** @deprecated */
   @Deprecated
   public static boolean isJsonArray(String str) {
      return isTypeJSONArray(str);
   }

   public static boolean isTypeJSONArray(String str) {
      return StrUtil.isBlank(str) ? false : StrUtil.isWrap(StrUtil.trim(str), '[', ']');
   }

   public static boolean isNull(Object obj) {
      return null == obj || obj instanceof JSONNull;
   }

   public static JSONObject xmlToJson(String xml) {
      return XML.toJSONObject(xml);
   }

   public static void putSerializer(Type type, JSONArraySerializer<?> serializer) {
      GlobalSerializeMapping.put(type, serializer);
   }

   public static void putSerializer(Type type, JSONObjectSerializer<?> serializer) {
      GlobalSerializeMapping.put(type, serializer);
   }

   public static void putDeserializer(Type type, JSONDeserializer<?> deserializer) {
      GlobalSerializeMapping.put(type, deserializer);
   }

   private static String escape(char c) {
      switch (c) {
         case '\b':
            return "\\b";
         case '\t':
            return "\\t";
         case '\n':
            return "\\n";
         case '\u000b':
         default:
            return c >= ' ' && (c < 128 || c > 160) && (c < 8192 || c > 8208) && (c < 8232 || c > 8239) && (c < 8294 || c > 8303) ? Character.toString(c) : HexUtil.toUnicodeHex(c);
         case '\f':
            return "\\f";
         case '\r':
            return "\\r";
      }
   }
}
