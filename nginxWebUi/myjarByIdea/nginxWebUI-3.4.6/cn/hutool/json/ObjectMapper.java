package cn.hutool.json;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.ArrayIter;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.lang.Filter;
import cn.hutool.core.lang.mutable.Mutable;
import cn.hutool.core.lang.mutable.MutablePair;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.TypeUtil;
import cn.hutool.json.serialize.GlobalSerializeMapping;
import cn.hutool.json.serialize.JSONObjectSerializer;
import cn.hutool.json.serialize.JSONSerializer;
import java.io.InputStream;
import java.io.Reader;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.ResourceBundle;

public class ObjectMapper {
   private final Object source;

   public static ObjectMapper of(Object source) {
      return new ObjectMapper(source);
   }

   public ObjectMapper(Object source) {
      this.source = source;
   }

   public void map(JSONObject jsonObject, Filter<MutablePair<String, Object>> filter) {
      Object source = this.source;
      if (null != source) {
         JSONSerializer serializer = GlobalSerializeMapping.getSerializer(source.getClass());
         if (serializer instanceof JSONObjectSerializer) {
            serializer.serialize(jsonObject, source);
         } else if (source instanceof JSONArray) {
            throw new JSONException("Unsupported type [{}] to JSONObject!", new Object[]{source.getClass()});
         } else {
            if (source instanceof Map) {
               Iterator var5 = ((Map)source).entrySet().iterator();

               while(var5.hasNext()) {
                  Map.Entry<?, ?> e = (Map.Entry)var5.next();
                  jsonObject.set(Convert.toStr(e.getKey()), e.getValue(), filter, false);
               }
            } else if (source instanceof Map.Entry) {
               Map.Entry entry = (Map.Entry)source;
               jsonObject.set(Convert.toStr(entry.getKey()), entry.getValue(), filter, false);
            } else if (source instanceof CharSequence) {
               mapFromStr((CharSequence)source, jsonObject, filter);
            } else if (source instanceof Reader) {
               mapFromTokener(new JSONTokener((Reader)source, jsonObject.getConfig()), jsonObject, filter);
            } else if (source instanceof InputStream) {
               mapFromTokener(new JSONTokener((InputStream)source, jsonObject.getConfig()), jsonObject, filter);
            } else if (source instanceof byte[]) {
               mapFromTokener(new JSONTokener(IoUtil.toStream((byte[])((byte[])source)), jsonObject.getConfig()), jsonObject, filter);
            } else if (source instanceof JSONTokener) {
               mapFromTokener((JSONTokener)source, jsonObject, filter);
            } else if (source instanceof ResourceBundle) {
               mapFromResourceBundle((ResourceBundle)source, jsonObject, filter);
            } else {
               if (!BeanUtil.isReadableBean(source.getClass())) {
                  throw new JSONException("Unsupported type [{}] to JSONObject!", new Object[]{source.getClass()});
               }

               mapFromBean(source, jsonObject);
            }

         }
      }
   }

   public void map(JSONArray jsonArray, Filter<Mutable<Object>> filter) throws JSONException {
      Object source = this.source;
      if (null != source) {
         JSONSerializer serializer = GlobalSerializeMapping.getSerializer(source.getClass());
         if (null != serializer && JSONArray.class.equals(TypeUtil.getTypeArgument(serializer.getClass()))) {
            serializer.serialize(jsonArray, source);
         } else if (source instanceof CharSequence) {
            this.mapFromStr((CharSequence)source, jsonArray, filter);
         } else if (source instanceof Reader) {
            mapFromTokener(new JSONTokener((Reader)source, jsonArray.getConfig()), jsonArray, filter);
         } else if (source instanceof InputStream) {
            mapFromTokener(new JSONTokener((InputStream)source, jsonArray.getConfig()), jsonArray, filter);
         } else if (source instanceof byte[]) {
            try {
               mapFromTokener(new JSONTokener(IoUtil.toStream((byte[])((byte[])source)), jsonArray.getConfig()), jsonArray, filter);
            } catch (JSONException var10) {
               byte[] var6 = (byte[])((byte[])source);
               int var7 = var6.length;

               for(int var8 = 0; var8 < var7; ++var8) {
                  byte b = var6[var8];
                  jsonArray.add(b);
               }
            }
         } else if (source instanceof JSONTokener) {
            mapFromTokener((JSONTokener)source, jsonArray, filter);
         } else {
            Object iter;
            if (ArrayUtil.isArray(source)) {
               iter = new ArrayIter(source);
            } else if (source instanceof Iterator) {
               iter = (Iterator)source;
            } else {
               if (!(source instanceof Iterable)) {
                  throw new JSONException("JSONArray initial value should be a string or collection or array.");
               }

               iter = ((Iterable)source).iterator();
            }

            JSONConfig config = jsonArray.getConfig();

            while(((Iterator)iter).hasNext()) {
               Object next = ((Iterator)iter).next();
               if (next != source) {
                  jsonArray.addRaw(JSONUtil.wrap(next, config), filter);
               }
            }
         }

      }
   }

   private static void mapFromResourceBundle(ResourceBundle bundle, JSONObject jsonObject, Filter<MutablePair<String, Object>> filter) {
      Enumeration<String> keys = bundle.getKeys();

      while(keys.hasMoreElements()) {
         String key = (String)keys.nextElement();
         if (key != null) {
            InternalJSONUtil.propertyPut(jsonObject, key, bundle.getString(key), filter);
         }
      }

   }

   private static void mapFromStr(CharSequence source, JSONObject jsonObject, Filter<MutablePair<String, Object>> filter) {
      String jsonStr = StrUtil.trim(source);
      if (StrUtil.startWith(jsonStr, '<')) {
         XML.toJSONObject(jsonObject, jsonStr, false);
      } else {
         mapFromTokener(new JSONTokener(StrUtil.trim(source), jsonObject.getConfig()), jsonObject, filter);
      }
   }

   private void mapFromStr(CharSequence source, JSONArray jsonArray, Filter<Mutable<Object>> filter) {
      if (null != source) {
         mapFromTokener(new JSONTokener(StrUtil.trim(source), jsonArray.getConfig()), jsonArray, filter);
      }

   }

   private static void mapFromTokener(JSONTokener x, JSONObject jsonObject, Filter<MutablePair<String, Object>> filter) {
      JSONParser.of(x).parseTo(jsonObject, filter);
   }

   private static void mapFromTokener(JSONTokener x, JSONArray jsonArray, Filter<Mutable<Object>> filter) {
      JSONParser.of(x).parseTo(jsonArray, filter);
   }

   private static void mapFromBean(Object bean, JSONObject jsonObject) {
      BeanUtil.beanToMap(bean, jsonObject, InternalJSONUtil.toCopyOptions(jsonObject.getConfig()));
   }
}
