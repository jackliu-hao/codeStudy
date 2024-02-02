package cn.hutool.core.util;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.lang.func.Func1;
import cn.hutool.core.lang.func.LambdaUtil;
import cn.hutool.core.map.MapUtil;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

public class EnumUtil {
   public static boolean isEnum(Class<?> clazz) {
      Assert.notNull(clazz);
      return clazz.isEnum();
   }

   public static boolean isEnum(Object obj) {
      Assert.notNull(obj);
      return obj.getClass().isEnum();
   }

   public static String toString(Enum<?> e) {
      return null != e ? e.name() : null;
   }

   public static <E extends Enum<E>> E getEnumAt(Class<E> enumClass, int index) {
      E[] enumConstants = (Enum[])enumClass.getEnumConstants();
      return index >= 0 && index < enumConstants.length ? enumConstants[index] : null;
   }

   public static <E extends Enum<E>> E fromString(Class<E> enumClass, String value) {
      return Enum.valueOf(enumClass, value);
   }

   public static <E extends Enum<E>> E fromString(Class<E> enumClass, String value, E defaultValue) {
      return (Enum)ObjectUtil.defaultIfNull(fromStringQuietly(enumClass, value), (Object)defaultValue);
   }

   public static <E extends Enum<E>> E fromStringQuietly(Class<E> enumClass, String value) {
      if (null != enumClass && !StrUtil.isBlank(value)) {
         try {
            return fromString(enumClass, value);
         } catch (IllegalArgumentException var3) {
            return null;
         }
      } else {
         return null;
      }
   }

   public static <E extends Enum<E>> E likeValueOf(Class<E> enumClass, Object value) {
      if (value instanceof CharSequence) {
         value = value.toString().trim();
      }

      Field[] fields = ReflectUtil.getFields(enumClass);
      Enum<?>[] enums = (Enum[])enumClass.getEnumConstants();
      Field[] var5 = fields;
      int var6 = fields.length;

      for(int var7 = 0; var7 < var6; ++var7) {
         Field field = var5[var7];
         String fieldName = field.getName();
         if (!field.getType().isEnum() && !"ENUM$VALUES".equals(fieldName) && !"ordinal".equals(fieldName)) {
            Enum[] var9 = enums;
            int var10 = enums.length;

            for(int var11 = 0; var11 < var10; ++var11) {
               Enum<?> enumObj = var9[var11];
               if (ObjectUtil.equal(value, ReflectUtil.getFieldValue(enumObj, (Field)field))) {
                  return enumObj;
               }
            }
         }
      }

      return null;
   }

   public static List<String> getNames(Class<? extends Enum<?>> clazz) {
      Enum<?>[] enums = (Enum[])clazz.getEnumConstants();
      if (null == enums) {
         return null;
      } else {
         List<String> list = new ArrayList(enums.length);
         Enum[] var3 = enums;
         int var4 = enums.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            Enum<?> e = var3[var5];
            list.add(e.name());
         }

         return list;
      }
   }

   public static List<Object> getFieldValues(Class<? extends Enum<?>> clazz, String fieldName) {
      Enum<?>[] enums = (Enum[])clazz.getEnumConstants();
      if (null == enums) {
         return null;
      } else {
         List<Object> list = new ArrayList(enums.length);
         Enum[] var4 = enums;
         int var5 = enums.length;

         for(int var6 = 0; var6 < var5; ++var6) {
            Enum<?> e = var4[var6];
            list.add(ReflectUtil.getFieldValue(e, (String)fieldName));
         }

         return list;
      }
   }

   public static List<String> getFieldNames(Class<? extends Enum<?>> clazz) {
      List<String> names = new ArrayList();
      Field[] fields = ReflectUtil.getFields(clazz);
      Field[] var4 = fields;
      int var5 = fields.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         Field field = var4[var6];
         String name = field.getName();
         if (!field.getType().isEnum() && !name.contains("$VALUES") && !"ordinal".equals(name) && !names.contains(name)) {
            names.add(name);
         }
      }

      return names;
   }

   public static <E extends Enum<E>> E getBy(Class<E> enumClass, Predicate<? super E> predicate) {
      return (Enum)Arrays.stream(enumClass.getEnumConstants()).filter(predicate).findFirst().orElse((Object)null);
   }

   public static <E extends Enum<E>, C> E getBy(Func1<E, C> condition, C value) {
      Class<E> implClass = LambdaUtil.getRealClass(condition);
      if (Enum.class.equals(implClass)) {
         implClass = LambdaUtil.getRealClass(condition);
      }

      return (Enum)Arrays.stream(implClass.getEnumConstants()).filter((e) -> {
         return condition.callWithRuntimeException(e).equals(value);
      }).findAny().orElse((Object)null);
   }

   public static <E extends Enum<E>, F, C> F getFieldBy(Func1<E, F> field, Function<E, C> condition, C value) {
      Class<E> implClass = LambdaUtil.getRealClass(field);
      if (Enum.class.equals(implClass)) {
         implClass = LambdaUtil.getRealClass(field);
      }

      Optional var10000 = Arrays.stream(implClass.getEnumConstants()).filter((e) -> {
         return condition.apply(e).equals(value);
      }).findFirst();
      field.getClass();
      return var10000.map(field::callWithRuntimeException).orElse((Object)null);
   }

   public static <E extends Enum<E>> LinkedHashMap<String, E> getEnumMap(Class<E> enumClass) {
      LinkedHashMap<String, E> map = new LinkedHashMap();
      Enum[] var2 = (Enum[])enumClass.getEnumConstants();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         E e = var2[var4];
         map.put(e.name(), e);
      }

      return map;
   }

   public static Map<String, Object> getNameFieldMap(Class<? extends Enum<?>> clazz, String fieldName) {
      Enum<?>[] enums = (Enum[])clazz.getEnumConstants();
      if (null == enums) {
         return null;
      } else {
         Map<String, Object> map = MapUtil.newHashMap(enums.length, true);
         Enum[] var4 = enums;
         int var5 = enums.length;

         for(int var6 = 0; var6 < var5; ++var6) {
            Enum<?> e = var4[var6];
            map.put(e.name(), ReflectUtil.getFieldValue(e, (String)fieldName));
         }

         return map;
      }
   }

   public static <E extends Enum<E>> boolean contains(Class<E> enumClass, String val) {
      return getEnumMap(enumClass).containsKey(val);
   }

   public static <E extends Enum<E>> boolean notContains(Class<E> enumClass, String val) {
      return !contains(enumClass, val);
   }

   public static boolean equalsIgnoreCase(Enum<?> e, String val) {
      return StrUtil.equalsIgnoreCase(toString(e), val);
   }

   public static boolean equals(Enum<?> e, String val) {
      return StrUtil.equals(toString(e), val);
   }
}
