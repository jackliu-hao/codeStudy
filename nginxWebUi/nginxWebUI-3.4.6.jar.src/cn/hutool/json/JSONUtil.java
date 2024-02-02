/*      */ package cn.hutool.json;
/*      */ 
/*      */ import cn.hutool.core.codec.Base64;
/*      */ import cn.hutool.core.io.IORuntimeException;
/*      */ import cn.hutool.core.io.file.FileReader;
/*      */ import cn.hutool.core.lang.TypeReference;
/*      */ import cn.hutool.core.util.ArrayUtil;
/*      */ import cn.hutool.core.util.ClassUtil;
/*      */ import cn.hutool.core.util.HexUtil;
/*      */ import cn.hutool.core.util.ObjectUtil;
/*      */ import cn.hutool.core.util.StrUtil;
/*      */ import cn.hutool.core.util.TypeUtil;
/*      */ import cn.hutool.json.serialize.GlobalSerializeMapping;
/*      */ import cn.hutool.json.serialize.JSONArraySerializer;
/*      */ import cn.hutool.json.serialize.JSONDeserializer;
/*      */ import cn.hutool.json.serialize.JSONObjectSerializer;
/*      */ import cn.hutool.json.serialize.JSONSerializer;
/*      */ import java.io.File;
/*      */ import java.io.IOException;
/*      */ import java.io.StringWriter;
/*      */ import java.io.Writer;
/*      */ import java.lang.reflect.Type;
/*      */ import java.nio.charset.Charset;
/*      */ import java.util.List;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class JSONUtil
/*      */ {
/*      */   public static JSONObject createObj() {
/*   49 */     return new JSONObject();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static JSONObject createObj(JSONConfig config) {
/*   60 */     return new JSONObject(config);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static JSONArray createArray() {
/*   69 */     return new JSONArray();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static JSONArray createArray(JSONConfig config) {
/*   80 */     return new JSONArray(config);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static JSONObject parseObj(String jsonStr) {
/*   90 */     return new JSONObject(jsonStr);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static JSONObject parseObj(Object obj) {
/*  101 */     return parseObj(obj, (JSONConfig)null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static JSONObject parseObj(Object obj, JSONConfig config) {
/*  114 */     return new JSONObject(obj, (JSONConfig)ObjectUtil.defaultIfNull(config, JSONConfig::create));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static JSONObject parseObj(Object obj, boolean ignoreNullValue) {
/*  126 */     return new JSONObject(obj, ignoreNullValue);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public static JSONObject parseObj(Object obj, boolean ignoreNullValue, boolean isOrder) {
/*  142 */     return new JSONObject(obj, ignoreNullValue);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static JSONArray parseArray(String jsonStr) {
/*  152 */     return new JSONArray(jsonStr);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static JSONArray parseArray(Object arrayOrCollection) {
/*  163 */     return parseArray(arrayOrCollection, (JSONConfig)null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static JSONArray parseArray(Object arrayOrCollection, JSONConfig config) {
/*  175 */     return new JSONArray(arrayOrCollection, config);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static JSONArray parseArray(Object arrayOrCollection, boolean ignoreNullValue) {
/*  187 */     return new JSONArray(arrayOrCollection, ignoreNullValue);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static JSON parse(Object obj) {
/*  203 */     return parse(obj, null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static JSON parse(Object obj, JSONConfig config) {
/*      */     JSON json;
/*  221 */     if (null == obj) {
/*  222 */       return null;
/*      */     }
/*      */     
/*  225 */     if (obj instanceof JSON) {
/*  226 */       json = (JSON)obj;
/*  227 */     } else if (obj instanceof CharSequence) {
/*  228 */       String jsonStr = StrUtil.trim((CharSequence)obj);
/*  229 */       json = isTypeJSONArray(jsonStr) ? parseArray(jsonStr, config) : parseObj(jsonStr, config);
/*  230 */     } else if (obj instanceof cn.hutool.core.map.MapWrapper) {
/*      */       
/*  232 */       json = parseObj(obj, config);
/*  233 */     } else if (obj instanceof Iterable || obj instanceof java.util.Iterator || ArrayUtil.isArray(obj)) {
/*  234 */       json = parseArray(obj, config);
/*      */     } else {
/*  236 */       json = parseObj(obj, config);
/*      */     } 
/*      */     
/*  239 */     return json;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static JSONObject parseFromXml(String xmlStr) {
/*  249 */     return XML.toJSONObject(xmlStr);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static JSON readJSON(File file, Charset charset) throws IORuntimeException {
/*  265 */     return parse(FileReader.create(file, charset).readString());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static JSONObject readJSONObject(File file, Charset charset) throws IORuntimeException {
/*  277 */     return parseObj(FileReader.create(file, charset).readString());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static JSONArray readJSONArray(File file, Charset charset) throws IORuntimeException {
/*  289 */     return parseArray(FileReader.create(file, charset).readString());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String toJsonStr(JSON json, int indentFactor) {
/*  303 */     if (null == json) {
/*  304 */       return null;
/*      */     }
/*  306 */     return json.toJSONString(indentFactor);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String toJsonStr(JSON json) {
/*  316 */     if (null == json) {
/*  317 */       return null;
/*      */     }
/*  319 */     return json.toJSONString(0);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void toJsonStr(JSON json, Writer writer) {
/*  330 */     if (null != json) {
/*  331 */       json.write(writer);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String toJsonPrettyStr(JSON json) {
/*  342 */     if (null == json) {
/*  343 */       return null;
/*      */     }
/*  345 */     return json.toJSONString(4);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String toJsonStr(Object obj) {
/*  355 */     return toJsonStr(obj, (JSONConfig)null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String toJsonStr(Object obj, JSONConfig jsonConfig) {
/*  367 */     if (null == obj) {
/*  368 */       return null;
/*      */     }
/*  370 */     if (obj instanceof CharSequence) {
/*  371 */       return StrUtil.str((CharSequence)obj);
/*      */     }
/*  373 */     return toJsonStr(parse(obj, jsonConfig));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void toJsonStr(Object obj, Writer writer) {
/*  384 */     if (null != obj) {
/*  385 */       toJsonStr(parse(obj), writer);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String toJsonPrettyStr(Object obj) {
/*  396 */     return toJsonPrettyStr(parse(obj));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String toXmlStr(JSON json) {
/*  406 */     return XML.toXml(json);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <T> T toBean(String jsonString, Class<T> beanClass) {
/*  422 */     return toBean(parseObj(jsonString), beanClass);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <T> T toBean(String jsonString, JSONConfig config, Class<T> beanClass) {
/*  437 */     return toBean(parseObj(jsonString, config), beanClass);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <T> T toBean(JSONObject json, Class<T> beanClass) {
/*  449 */     return (null == json) ? null : json.<T>toBean(beanClass);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <T> T toBean(String jsonString, TypeReference<T> typeReference, boolean ignoreError) {
/*  463 */     return toBean(jsonString, typeReference.getType(), ignoreError);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <T> T toBean(String jsonString, Type beanType, boolean ignoreError) {
/*  477 */     return parse(jsonString, JSONConfig.create().setIgnoreError(ignoreError)).toBean(beanType);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <T> T toBean(JSON json, TypeReference<T> typeReference, boolean ignoreError) {
/*  491 */     return toBean(json, typeReference.getType(), ignoreError);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <T> T toBean(JSON json, Type beanType, boolean ignoreError) {
/*  505 */     if (null == json) {
/*  506 */       return null;
/*      */     }
/*  508 */     return json.toBean(beanType, ignoreError);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <T> List<T> toList(String jsonArray, Class<T> elementType) {
/*  522 */     return toList(parseArray(jsonArray), elementType);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <T> List<T> toList(JSONArray jsonArray, Class<T> elementType) {
/*  535 */     return (null == jsonArray) ? null : jsonArray.<T>toList(elementType);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Object getByPath(JSON json, String expression) {
/*  560 */     return getByPath(json, expression, null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <T> T getByPath(JSON json, String expression, T defaultValue) {
/*  589 */     if (null == json || StrUtil.isBlank(expression)) {
/*  590 */       return defaultValue;
/*      */     }
/*      */     
/*  593 */     if (null != defaultValue) {
/*  594 */       Class<T> type = (Class)defaultValue.getClass();
/*  595 */       return (T)ObjectUtil.defaultIfNull(json.getByPath(expression, type), defaultValue);
/*      */     } 
/*  597 */     return (T)json.getByPath(expression);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void putByPath(JSON json, String expression, Object value) {
/*  623 */     json.putByPath(expression, value);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String quote(String string) {
/*  635 */     return quote(string, true);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String quote(String string, boolean isWrap) {
/*  649 */     StringWriter sw = new StringWriter();
/*      */     try {
/*  651 */       return quote(string, sw, isWrap).toString();
/*  652 */     } catch (IOException ignored) {
/*      */       
/*  654 */       return "";
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Writer quote(String str, Writer writer) throws IOException {
/*  669 */     return quote(str, writer, true);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Writer quote(String str, Writer writer, boolean isWrap) throws IOException {
/*  685 */     if (StrUtil.isEmpty(str)) {
/*  686 */       if (isWrap) {
/*  687 */         writer.write("\"\"");
/*      */       }
/*  689 */       return writer;
/*      */     } 
/*      */ 
/*      */     
/*  693 */     int len = str.length();
/*  694 */     if (isWrap) {
/*  695 */       writer.write(34);
/*      */     }
/*  697 */     for (int i = 0; i < len; i++) {
/*  698 */       char c = str.charAt(i);
/*  699 */       switch (c) {
/*      */         case '"':
/*      */         case '\\':
/*  702 */           writer.write("\\");
/*  703 */           writer.write(c);
/*      */           break;
/*      */         default:
/*  706 */           writer.write(escape(c)); break;
/*      */       } 
/*      */     } 
/*  709 */     if (isWrap) {
/*  710 */       writer.write(34);
/*      */     }
/*  712 */     return writer;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String escape(String str) {
/*  722 */     if (StrUtil.isEmpty(str)) {
/*  723 */       return str;
/*      */     }
/*      */     
/*  726 */     int len = str.length();
/*  727 */     StringBuilder builder = new StringBuilder(len);
/*      */     
/*  729 */     for (int i = 0; i < len; i++) {
/*  730 */       char c = str.charAt(i);
/*  731 */       builder.append(escape(c));
/*      */     } 
/*  733 */     return builder.toString();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Object wrap(Object object, JSONConfig jsonConfig) {
/*  754 */     if (object == null) {
/*  755 */       return jsonConfig.isIgnoreNullValue() ? null : JSONNull.NULL;
/*      */     }
/*  757 */     if (object instanceof JSON || 
/*  758 */       ObjectUtil.isNull(object) || object instanceof JSONString || object instanceof CharSequence || object instanceof Number || 
/*      */ 
/*      */ 
/*      */       
/*  762 */       ObjectUtil.isBasicType(object))
/*      */     {
/*  764 */       return object;
/*      */     }
/*      */ 
/*      */     
/*  768 */     JSONSerializer serializer = GlobalSerializeMapping.getSerializer(object.getClass());
/*  769 */     if (null != serializer) {
/*  770 */       Type jsonType = TypeUtil.getTypeArgument(serializer.getClass());
/*  771 */       if (null != jsonType) {
/*  772 */         if (serializer instanceof JSONObjectSerializer) {
/*  773 */           serializer.serialize(new JSONObject(jsonConfig), object);
/*  774 */         } else if (serializer instanceof JSONArraySerializer) {
/*  775 */           serializer.serialize(new JSONArray(jsonConfig), object);
/*      */         } 
/*      */       }
/*      */     } 
/*      */ 
/*      */     
/*      */     try {
/*  782 */       if (object instanceof java.sql.SQLException) {
/*  783 */         return object.toString();
/*      */       }
/*      */ 
/*      */       
/*  787 */       if (object instanceof Iterable || ArrayUtil.isArray(object)) {
/*  788 */         if (object instanceof byte[])
/*      */         {
/*      */           
/*  791 */           return Base64.encode((byte[])object);
/*      */         }
/*      */         
/*  794 */         return new JSONArray(object, jsonConfig);
/*      */       } 
/*      */       
/*  797 */       if (object instanceof java.util.Map || object instanceof java.util.Map.Entry) {
/*  798 */         return new JSONObject(object, jsonConfig);
/*      */       }
/*      */ 
/*      */       
/*  802 */       if (object instanceof java.util.Date || object instanceof java.util.Calendar || object instanceof java.time.temporal.TemporalAccessor)
/*      */       {
/*      */ 
/*      */         
/*  806 */         return object;
/*      */       }
/*      */       
/*  809 */       if (object instanceof Enum) {
/*  810 */         return object.toString();
/*      */       }
/*      */ 
/*      */       
/*  814 */       if (ClassUtil.isJdkClass(object.getClass())) {
/*  815 */         return object.toString();
/*      */       }
/*      */ 
/*      */       
/*  819 */       return new JSONObject(object, jsonConfig);
/*  820 */     } catch (Exception exception) {
/*  821 */       return null;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String formatJsonStr(String jsonStr) {
/*  833 */     return JSONStrFormatter.format(jsonStr);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public static boolean isJson(String str) {
/*  846 */     return isTypeJSON(str);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean isTypeJSON(String str) {
/*  857 */     return (isTypeJSONObject(str) || isTypeJSONArray(str));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public static boolean isJsonObj(String str) {
/*  870 */     return isTypeJSONObject(str);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean isTypeJSONObject(String str) {
/*  881 */     if (StrUtil.isBlank(str)) {
/*  882 */       return false;
/*      */     }
/*  884 */     return StrUtil.isWrap(StrUtil.trim(str), '{', '}');
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public static boolean isJsonArray(String str) {
/*  897 */     return isTypeJSONArray(str);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean isTypeJSONArray(String str) {
/*  908 */     if (StrUtil.isBlank(str)) {
/*  909 */       return false;
/*      */     }
/*  911 */     return StrUtil.isWrap(StrUtil.trim(str), '[', ']');
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean isNull(Object obj) {
/*  927 */     return (null == obj || obj instanceof JSONNull);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static JSONObject xmlToJson(String xml) {
/*  939 */     return XML.toJSONObject(xml);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void putSerializer(Type type, JSONArraySerializer<?> serializer) {
/*  951 */     GlobalSerializeMapping.put(type, serializer);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void putSerializer(Type type, JSONObjectSerializer<?> serializer) {
/*  963 */     GlobalSerializeMapping.put(type, serializer);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void putDeserializer(Type type, JSONDeserializer<?> deserializer) {
/*  975 */     GlobalSerializeMapping.put(type, deserializer);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static String escape(char c) {
/*  988 */     switch (c) {
/*      */       case '\b':
/*  990 */         return "\\b";
/*      */       case '\t':
/*  992 */         return "\\t";
/*      */       case '\n':
/*  994 */         return "\\n";
/*      */       case '\f':
/*  996 */         return "\\f";
/*      */       case '\r':
/*  998 */         return "\\r";
/*      */     } 
/* 1000 */     if (c < ' ' || (c >= '' && c <= ' ') || (c >= ' ' && c <= '‐') || (c >= ' ' && c <= ' ') || (c >= '⁦' && c <= '⁯'))
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 1006 */       return HexUtil.toUnicodeHex(c);
/*      */     }
/* 1008 */     return Character.toString(c);
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\json\JSONUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */