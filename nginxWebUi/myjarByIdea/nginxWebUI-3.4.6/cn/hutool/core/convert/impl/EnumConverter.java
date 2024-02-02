package cn.hutool.core.convert.impl;

import cn.hutool.core.convert.AbstractConverter;
import cn.hutool.core.convert.ConvertException;
import cn.hutool.core.lang.EnumItem;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.map.WeakConcurrentMap;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.EnumUtil;
import cn.hutool.core.util.ModifierUtil;
import cn.hutool.core.util.ReflectUtil;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.Collectors;

public class EnumConverter extends AbstractConverter<Object> {
   private static final long serialVersionUID = 1L;
   private static final WeakConcurrentMap<Class<?>, Map<Class<?>, Method>> VALUE_OF_METHOD_CACHE = new WeakConcurrentMap();
   private final Class enumClass;

   public EnumConverter(Class enumClass) {
      this.enumClass = enumClass;
   }

   protected Object convertInternal(Object value) {
      Enum enumValue = tryConvertEnum(value, this.enumClass);
      if (null == enumValue && !(value instanceof String)) {
         enumValue = Enum.valueOf(this.enumClass, this.convertToStr(value));
      }

      if (null != enumValue) {
         return enumValue;
      } else {
         throw new ConvertException("Can not convert {} to {}", new Object[]{value, this.enumClass});
      }
   }

   public Class getTargetType() {
      return this.enumClass;
   }

   protected static Enum tryConvertEnum(Object value, Class enumClass) {
      if (value == null) {
         return null;
      } else {
         if (EnumItem.class.isAssignableFrom(enumClass)) {
            EnumItem first = (EnumItem)EnumUtil.getEnumAt(enumClass, 0);
            if (null != first) {
               if (value instanceof Integer) {
                  return (Enum)first.fromInt((Integer)value);
               }

               if (value instanceof String) {
                  return (Enum)first.fromStr(value.toString());
               }
            }
         }

         try {
            Map<Class<?>, Method> methodMap = getMethodMap(enumClass);
            if (MapUtil.isNotEmpty(methodMap)) {
               Class<?> valueClass = value.getClass();
               Iterator var4 = methodMap.entrySet().iterator();

               while(var4.hasNext()) {
                  Map.Entry<Class<?>, Method> entry = (Map.Entry)var4.next();
                  if (ClassUtil.isAssignable((Class)entry.getKey(), valueClass)) {
                     return (Enum)ReflectUtil.invokeStatic((Method)entry.getValue(), value);
                  }
               }
            }
         } catch (Exception var7) {
         }

         Enum enumResult = null;
         if (value instanceof Integer) {
            enumResult = EnumUtil.getEnumAt(enumClass, (Integer)value);
         } else if (value instanceof String) {
            try {
               enumResult = Enum.valueOf(enumClass, (String)value);
            } catch (IllegalArgumentException var6) {
            }
         }

         return enumResult;
      }
   }

   private static Map<Class<?>, Method> getMethodMap(Class<?> enumClass) {
      return (Map)VALUE_OF_METHOD_CACHE.computeIfAbsent(enumClass, (key) -> {
         return (Map)Arrays.stream(enumClass.getMethods()).filter(ModifierUtil::isStatic).filter((m) -> {
            return m.getReturnType() == enumClass;
         }).filter((m) -> {
            return m.getParameterCount() == 1;
         }).filter((m) -> {
            return !"valueOf".equals(m.getName());
         }).collect(Collectors.toMap((m) -> {
            return m.getParameterTypes()[0];
         }, (m) -> {
            return m;
         }, (k1, k2) -> {
            return k1;
         }));
      });
   }
}
