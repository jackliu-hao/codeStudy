package cn.hutool.core.util;

import cn.hutool.core.collection.IterUtil;
import cn.hutool.core.comparator.CompareUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.map.MapUtil;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

public class ObjectUtil {
   public static boolean equals(Object obj1, Object obj2) {
      return equal(obj1, obj2);
   }

   public static boolean equal(Object obj1, Object obj2) {
      return obj1 instanceof BigDecimal && obj2 instanceof BigDecimal ? NumberUtil.equals((BigDecimal)obj1, (BigDecimal)obj2) : Objects.equals(obj1, obj2);
   }

   public static boolean notEqual(Object obj1, Object obj2) {
      return !equal(obj1, obj2);
   }

   public static int length(Object obj) {
      if (obj == null) {
         return 0;
      } else if (obj instanceof CharSequence) {
         return ((CharSequence)obj).length();
      } else if (obj instanceof Collection) {
         return ((Collection)obj).size();
      } else if (obj instanceof Map) {
         return ((Map)obj).size();
      } else {
         int count;
         if (obj instanceof Iterator) {
            Iterator<?> iter = (Iterator)obj;
            count = 0;

            while(iter.hasNext()) {
               ++count;
               iter.next();
            }

            return count;
         } else if (!(obj instanceof Enumeration)) {
            return obj.getClass().isArray() ? Array.getLength(obj) : -1;
         } else {
            Enumeration<?> enumeration = (Enumeration)obj;
            count = 0;

            while(enumeration.hasMoreElements()) {
               ++count;
               enumeration.nextElement();
            }

            return count;
         }
      }
   }

   public static boolean contains(Object obj, Object element) {
      if (obj == null) {
         return false;
      } else if (obj instanceof String) {
         return element == null ? false : ((String)obj).contains(element.toString());
      } else if (obj instanceof Collection) {
         return ((Collection)obj).contains(element);
      } else if (obj instanceof Map) {
         return ((Map)obj).containsValue(element);
      } else {
         Object o;
         if (obj instanceof Iterator) {
            Iterator<?> iter = (Iterator)obj;

            do {
               if (!iter.hasNext()) {
                  return false;
               }

               o = iter.next();
            } while(!equal(o, element));

            return true;
         } else if (obj instanceof Enumeration) {
            Enumeration<?> enumeration = (Enumeration)obj;

            do {
               if (!enumeration.hasMoreElements()) {
                  return false;
               }

               o = enumeration.nextElement();
            } while(!equal(o, element));

            return true;
         } else {
            if (obj.getClass().isArray()) {
               int len = Array.getLength(obj);

               for(int i = 0; i < len; ++i) {
                  Object o = Array.get(obj, i);
                  if (equal(o, element)) {
                     return true;
                  }
               }
            }

            return false;
         }
      }
   }

   public static boolean isNull(Object obj) {
      return null == obj || obj.equals((Object)null);
   }

   public static boolean isNotNull(Object obj) {
      return !isNull(obj);
   }

   public static boolean isEmpty(Object obj) {
      if (null == obj) {
         return true;
      } else if (obj instanceof CharSequence) {
         return StrUtil.isEmpty((CharSequence)obj);
      } else if (obj instanceof Map) {
         return MapUtil.isEmpty((Map)obj);
      } else if (obj instanceof Iterable) {
         return IterUtil.isEmpty((Iterable)obj);
      } else if (obj instanceof Iterator) {
         return IterUtil.isEmpty((Iterator)obj);
      } else {
         return ArrayUtil.isArray(obj) ? ArrayUtil.isEmpty(obj) : false;
      }
   }

   public static boolean isNotEmpty(Object obj) {
      return !isEmpty(obj);
   }

   public static <T> T defaultIfNull(T object, T defaultValue) {
      return isNull(object) ? defaultValue : object;
   }

   public static <T> T defaultIfNull(T source, Supplier<? extends T> defaultValueSupplier) {
      return isNull(source) ? defaultValueSupplier.get() : source;
   }

   public static <T> T defaultIfNull(Object source, Supplier<? extends T> handle, T defaultValue) {
      return isNotNull(source) ? handle.get() : defaultValue;
   }

   public static <T> T defaultIfEmpty(String str, Supplier<? extends T> handle, T defaultValue) {
      return StrUtil.isNotEmpty(str) ? handle.get() : defaultValue;
   }

   public static <T extends CharSequence> T defaultIfEmpty(T str, T defaultValue) {
      return StrUtil.isEmpty(str) ? defaultValue : str;
   }

   public static <T extends CharSequence> T defaultIfEmpty(T str, Supplier<? extends T> defaultValueSupplier) {
      return StrUtil.isEmpty(str) ? (CharSequence)defaultValueSupplier.get() : str;
   }

   public static <T extends CharSequence> T defaultIfBlank(T str, T defaultValue) {
      return StrUtil.isBlank(str) ? defaultValue : str;
   }

   public static <T extends CharSequence> T defaultIfBlank(T str, Supplier<? extends T> defaultValueSupplier) {
      return StrUtil.isBlank(str) ? (CharSequence)defaultValueSupplier.get() : str;
   }

   public static <T> T clone(T obj) {
      T result = ArrayUtil.clone(obj);
      if (null == result) {
         if (obj instanceof Cloneable) {
            result = ReflectUtil.invoke(obj, "clone");
         } else {
            result = cloneByStream(obj);
         }
      }

      return result;
   }

   public static <T> T cloneIfPossible(T obj) {
      T clone = null;

      try {
         clone = clone(obj);
      } catch (Exception var3) {
      }

      return clone == null ? obj : clone;
   }

   public static <T> T cloneByStream(T obj) {
      return SerializeUtil.clone(obj);
   }

   public static <T> byte[] serialize(T obj) {
      return SerializeUtil.serialize(obj);
   }

   public static <T> T deserialize(byte[] bytes) {
      return SerializeUtil.deserialize(bytes);
   }

   public static boolean isBasicType(Object object) {
      return null == object ? false : ClassUtil.isBasicType(object.getClass());
   }

   public static boolean isValidIfNumber(Object obj) {
      return obj instanceof Number ? NumberUtil.isValidNumber((Number)obj) : true;
   }

   public static <T extends Comparable<? super T>> int compare(T c1, T c2) {
      return CompareUtil.compare(c1, c2);
   }

   public static <T extends Comparable<? super T>> int compare(T c1, T c2, boolean nullGreater) {
      return CompareUtil.compare(c1, c2, nullGreater);
   }

   public static Class<?> getTypeArgument(Object obj) {
      return getTypeArgument(obj, 0);
   }

   public static Class<?> getTypeArgument(Object obj, int index) {
      return ClassUtil.getTypeArgument(obj.getClass(), index);
   }

   public static String toString(Object obj) {
      if (null == obj) {
         return "null";
      } else {
         return obj instanceof Map ? obj.toString() : Convert.toStr(obj);
      }
   }

   public static int emptyCount(Object... objs) {
      return ArrayUtil.emptyCount(objs);
   }

   public static boolean hasNull(Object... objs) {
      return ArrayUtil.hasNull(objs);
   }

   public static boolean hasEmpty(Object... objs) {
      return ArrayUtil.hasEmpty(objs);
   }

   public static boolean isAllEmpty(Object... objs) {
      return ArrayUtil.isAllEmpty(objs);
   }

   public static boolean isAllNotEmpty(Object... objs) {
      return ArrayUtil.isAllNotEmpty(objs);
   }
}
