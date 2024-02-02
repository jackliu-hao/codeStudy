package cn.hutool.json;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.codec.Base64;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.convert.ConvertException;
import cn.hutool.core.convert.Converter;
import cn.hutool.core.convert.ConverterRegistry;
import cn.hutool.core.convert.impl.ArrayConverter;
import cn.hutool.core.convert.impl.BeanConverter;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.TypeUtil;
import cn.hutool.json.serialize.GlobalSerializeMapping;
import cn.hutool.json.serialize.JSONDeserializer;
import java.lang.reflect.Type;
import java.util.List;

public class JSONConverter implements Converter<JSON> {
   protected static Object toArray(JSONArray jsonArray, Class<?> arrayClass) {
      return (new ArrayConverter(arrayClass)).convert(jsonArray, (Object)null);
   }

   protected static <T> List<T> toList(JSONArray jsonArray, Class<T> elementType) {
      return Convert.toList(elementType, jsonArray);
   }

   protected static <T> T jsonConvert(Type targetType, Object value, boolean ignoreError) throws ConvertException {
      if (JSONUtil.isNull(value)) {
         return null;
      } else {
         if (targetType instanceof Class) {
            Class<?> clazz = (Class)targetType;
            if (JSONBeanParser.class.isAssignableFrom(clazz)) {
               JSONBeanParser target = (JSONBeanParser)ReflectUtil.newInstanceIfPossible(clazz);
               if (null == target) {
                  throw new ConvertException("Can not instance [{}]", new Object[]{targetType});
               }

               target.parse(value);
               return target;
            }

            if (targetType == byte[].class && value instanceof CharSequence) {
               return Base64.decode((CharSequence)value);
            }
         }

         return jsonToBean(targetType, value, ignoreError);
      }
   }

   protected static <T> T jsonToBean(Type targetType, Object value, boolean ignoreError) throws ConvertException {
      if (JSONUtil.isNull(value)) {
         return null;
      } else {
         if (value instanceof JSON) {
            JSONDeserializer<?> deserializer = GlobalSerializeMapping.getDeserializer(targetType);
            if (null != deserializer) {
               return deserializer.deserialize((JSON)value);
            }

            if (value instanceof JSONGetter && targetType instanceof Class && BeanUtil.hasSetter((Class)targetType)) {
               JSONConfig config = ((JSONGetter)value).getConfig();
               Converter<T> converter = new BeanConverter(targetType, InternalJSONUtil.toCopyOptions(config).setIgnoreError(ignoreError));
               return converter.convertWithCheck(value, (Object)null, ignoreError);
            }
         }

         T targetValue = Convert.convertWithCheck(targetType, value, (Object)null, ignoreError);
         if (null == targetValue && !ignoreError) {
            if (StrUtil.isBlankIfStr(value)) {
               return null;
            } else {
               throw new ConvertException("Can not convert {} to type {}", new Object[]{value, ObjectUtil.defaultIfNull(TypeUtil.getClass(targetType), (Object)targetType)});
            }
         } else {
            return targetValue;
         }
      }
   }

   public JSON convert(Object value, JSON defaultValue) throws IllegalArgumentException {
      return JSONUtil.parse(value);
   }

   static {
      ConverterRegistry registry = ConverterRegistry.getInstance();
      registry.putCustom(JSON.class, (Class)JSONConverter.class);
      registry.putCustom(JSONObject.class, (Class)JSONConverter.class);
      registry.putCustom(JSONArray.class, (Class)JSONConverter.class);
   }
}
