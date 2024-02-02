package cn.hutool.core.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.UniqueKeySet;
import cn.hutool.core.comparator.CompareUtil;
import cn.hutool.core.exceptions.UtilException;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.lang.Editor;
import cn.hutool.core.lang.Filter;
import cn.hutool.core.lang.Matcher;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.text.StrJoiner;
import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ArrayUtil extends PrimitiveArrayUtil {
   public static <T> boolean isEmpty(T[] array) {
      return array == null || array.length == 0;
   }

   public static <T> T[] defaultIfEmpty(T[] array, T[] defaultArray) {
      return isEmpty(array) ? defaultArray : array;
   }

   public static boolean isEmpty(Object array) {
      if (array != null) {
         if (isArray(array)) {
            return 0 == Array.getLength(array);
         } else {
            return false;
         }
      } else {
         return true;
      }
   }

   public static <T> boolean isNotEmpty(T[] array) {
      return null != array && array.length != 0;
   }

   public static boolean isNotEmpty(Object array) {
      return !isEmpty(array);
   }

   public static <T> boolean hasNull(T... array) {
      if (isNotEmpty(array)) {
         Object[] var1 = array;
         int var2 = array.length;

         for(int var3 = 0; var3 < var2; ++var3) {
            T element = var1[var3];
            if (ObjectUtil.isNull(element)) {
               return true;
            }
         }
      }

      return array == null;
   }

   public static <T> boolean isAllNull(T... array) {
      return null == firstNonNull(array);
   }

   public static <T> T firstNonNull(T... array) {
      return firstMatch(ObjectUtil::isNotNull, array);
   }

   public static <T> T firstMatch(Matcher<T> matcher, T... array) {
      int index = matchIndex(matcher, array);
      return index < 0 ? null : array[index];
   }

   public static <T> int matchIndex(Matcher<T> matcher, T... array) {
      return matchIndex(matcher, 0, array);
   }

   public static <T> int matchIndex(Matcher<T> matcher, int beginIndexInclude, T... array) {
      Assert.notNull(matcher, "Matcher must be not null !");
      if (isNotEmpty(array)) {
         for(int i = beginIndexInclude; i < array.length; ++i) {
            if (matcher.match(array[i])) {
               return i;
            }
         }
      }

      return -1;
   }

   public static <T> T[] newArray(Class<?> componentType, int newSize) {
      return (Object[])((Object[])Array.newInstance(componentType, newSize));
   }

   public static Object[] newArray(int newSize) {
      return new Object[newSize];
   }

   public static Class<?> getComponentType(Object array) {
      return null == array ? null : array.getClass().getComponentType();
   }

   public static Class<?> getComponentType(Class<?> arrayClass) {
      return null == arrayClass ? null : arrayClass.getComponentType();
   }

   public static Class<?> getArrayType(Class<?> componentType) {
      return Array.newInstance(componentType, 0).getClass();
   }

   public static Object[] cast(Class<?> type, Object arrayObj) throws NullPointerException, IllegalArgumentException {
      if (null == arrayObj) {
         throw new NullPointerException("Argument [arrayObj] is null !");
      } else if (!arrayObj.getClass().isArray()) {
         throw new IllegalArgumentException("Argument [arrayObj] is not array !");
      } else if (null == type) {
         return (Object[])((Object[])arrayObj);
      } else {
         Class<?> componentType = type.isArray() ? type.getComponentType() : type;
         Object[] array = (Object[])((Object[])arrayObj);
         Object[] result = newArray(componentType, array.length);
         System.arraycopy(array, 0, result, 0, array.length);
         return result;
      }
   }

   @SafeVarargs
   public static <T> T[] append(T[] buffer, T... newElements) {
      return isEmpty(buffer) ? newElements : insert(buffer, buffer.length, newElements);
   }

   @SafeVarargs
   public static <T> Object append(Object array, T... newElements) {
      return isEmpty(array) ? newElements : insert(array, length(array), newElements);
   }

   public static <T> T[] setOrAppend(T[] buffer, int index, T value) {
      if (index < buffer.length) {
         Array.set(buffer, index, value);
         return buffer;
      } else if (isEmpty(buffer)) {
         T[] values = newArray(value.getClass(), 1);
         values[0] = value;
         return append(buffer, values);
      } else {
         return append(buffer, value);
      }
   }

   public static Object setOrAppend(Object array, int index, Object value) {
      if (index < length(array)) {
         Array.set(array, index, value);
         return array;
      } else {
         return append(array, value);
      }
   }

   public static <T> T[] replace(T[] buffer, int index, T... values) {
      if (isEmpty(values)) {
         return buffer;
      } else if (isEmpty(buffer)) {
         return values;
      } else if (index < 0) {
         return insert((Object[])buffer, 0, values);
      } else if (index >= buffer.length) {
         return append(buffer, values);
      } else if (buffer.length >= values.length + index) {
         System.arraycopy(values, 0, buffer, index, values.length);
         return buffer;
      } else {
         int newArrayLength = index + values.length;
         T[] result = newArray(buffer.getClass().getComponentType(), newArrayLength);
         System.arraycopy(buffer, 0, result, 0, index);
         System.arraycopy(values, 0, result, index, values.length);
         return result;
      }
   }

   public static <T> T[] insert(T[] buffer, int index, T... newElements) {
      return (Object[])((Object[])insert((Object)buffer, index, newElements));
   }

   public static <T> Object insert(Object array, int index, T... newElements) {
      if (isEmpty(newElements)) {
         return array;
      } else if (isEmpty(array)) {
         return newElements;
      } else {
         int len = length(array);
         if (index < 0) {
            index = index % len + len;
         }

         T[] result = newArray(array.getClass().getComponentType(), Math.max(len, index) + newElements.length);
         System.arraycopy(array, 0, result, 0, Math.min(len, index));
         System.arraycopy(newElements, 0, result, index, newElements.length);
         if (index < len) {
            System.arraycopy(array, index, result, index + newElements.length, len - index);
         }

         return result;
      }
   }

   public static <T> T[] resize(T[] data, int newSize, Class<?> componentType) {
      if (newSize < 0) {
         return data;
      } else {
         T[] newArray = newArray(componentType, newSize);
         if (newSize > 0 && isNotEmpty(data)) {
            System.arraycopy(data, 0, newArray, 0, Math.min(data.length, newSize));
         }

         return newArray;
      }
   }

   public static Object resize(Object array, int newSize) {
      if (newSize < 0) {
         return array;
      } else if (null == array) {
         return null;
      } else {
         int length = length(array);
         Object newArray = Array.newInstance(array.getClass().getComponentType(), newSize);
         if (newSize > 0 && isNotEmpty(array)) {
            System.arraycopy(array, 0, newArray, 0, Math.min(length, newSize));
         }

         return newArray;
      }
   }

   public static <T> T[] resize(T[] buffer, int newSize) {
      return resize(buffer, newSize, buffer.getClass().getComponentType());
   }

   @SafeVarargs
   public static <T> T[] addAll(T[]... arrays) {
      if (arrays.length == 1) {
         return arrays[0];
      } else {
         int length = 0;
         Object[][] var2 = arrays;
         int var3 = arrays.length;

         int var4;
         for(var4 = 0; var4 < var3; ++var4) {
            T[] array = var2[var4];
            if (null != array) {
               length += array.length;
            }
         }

         T[] result = newArray(arrays.getClass().getComponentType().getComponentType(), length);
         length = 0;
         Object[][] var8 = arrays;
         var4 = arrays.length;

         for(int var9 = 0; var9 < var4; ++var9) {
            T[] array = var8[var9];
            if (null != array) {
               System.arraycopy(array, 0, result, length, array.length);
               length += array.length;
            }
         }

         return result;
      }
   }

   public static Object copy(Object src, int srcPos, Object dest, int destPos, int length) {
      System.arraycopy(src, srcPos, dest, destPos, length);
      return dest;
   }

   public static Object copy(Object src, Object dest, int length) {
      System.arraycopy(src, 0, dest, 0, length);
      return dest;
   }

   public static <T> T[] clone(T[] array) {
      return array == null ? null : (Object[])array.clone();
   }

   public static <T> T clone(T obj) {
      if (null == obj) {
         return null;
      } else if (!isArray(obj)) {
         return null;
      } else {
         Class<?> componentType = obj.getClass().getComponentType();
         Object result;
         if (componentType.isPrimitive()) {
            int length = Array.getLength(obj);
            result = Array.newInstance(componentType, length);

            while(length-- > 0) {
               Array.set(result, length, Array.get(obj, length));
            }
         } else {
            result = ((Object[])((Object[])obj)).clone();
         }

         return result;
      }
   }

   public static <T> T[] edit(T[] array, Editor<T> editor) {
      if (null == editor) {
         return array;
      } else {
         ArrayList<T> list = new ArrayList(array.length);
         Object[] result = array;
         int var5 = array.length;

         for(int var6 = 0; var6 < var5; ++var6) {
            T t = result[var6];
            T modified = editor.edit(t);
            if (null != modified) {
               list.add(modified);
            }
         }

         result = newArray(array.getClass().getComponentType(), list.size());
         return list.toArray(result);
      }
   }

   public static <T> T[] filter(T[] array, Filter<T> filter) {
      return null != array && null != filter ? edit(array, (t) -> {
         return filter.accept(t) ? t : null;
      }) : array;
   }

   public static <T> T[] removeNull(T[] array) {
      return edit(array, (t) -> {
         return t;
      });
   }

   public static <T extends CharSequence> T[] removeEmpty(T[] array) {
      return (CharSequence[])filter(array, CharSequenceUtil::isNotEmpty);
   }

   public static <T extends CharSequence> T[] removeBlank(T[] array) {
      return (CharSequence[])filter(array, CharSequenceUtil::isNotBlank);
   }

   public static String[] nullToEmpty(String[] array) {
      return (String[])edit(array, (t) -> {
         return null == t ? "" : t;
      });
   }

   public static <K, V> Map<K, V> zip(K[] keys, V[] values, boolean isOrder) {
      if (!isEmpty(keys) && !isEmpty(values)) {
         int size = Math.min(keys.length, values.length);
         Map<K, V> map = MapUtil.newHashMap(size, isOrder);

         for(int i = 0; i < size; ++i) {
            map.put(keys[i], values[i]);
         }

         return map;
      } else {
         return null;
      }
   }

   public static <K, V> Map<K, V> zip(K[] keys, V[] values) {
      return zip(keys, values, false);
   }

   public static <T> int indexOf(T[] array, Object value, int beginIndexInclude) {
      return matchIndex((obj) -> {
         return ObjectUtil.equal(value, obj);
      }, beginIndexInclude, array);
   }

   public static <T> int indexOf(T[] array, Object value) {
      return matchIndex((obj) -> {
         return ObjectUtil.equal(value, obj);
      }, array);
   }

   public static int indexOfIgnoreCase(CharSequence[] array, CharSequence value) {
      if (null != array) {
         for(int i = 0; i < array.length; ++i) {
            if (StrUtil.equalsIgnoreCase(array[i], value)) {
               return i;
            }
         }
      }

      return -1;
   }

   public static <T> int lastIndexOf(T[] array, Object value) {
      return isEmpty(array) ? -1 : lastIndexOf(array, value, array.length - 1);
   }

   public static <T> int lastIndexOf(T[] array, Object value, int endInclude) {
      if (isNotEmpty(array)) {
         for(int i = endInclude; i >= 0; --i) {
            if (ObjectUtil.equal(value, array[i])) {
               return i;
            }
         }
      }

      return -1;
   }

   public static <T> boolean contains(T[] array, T value) {
      return indexOf(array, value) > -1;
   }

   public static <T> boolean containsAny(T[] array, T... values) {
      Object[] var2 = values;
      int var3 = values.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         T value = var2[var4];
         if (contains(array, value)) {
            return true;
         }
      }

      return false;
   }

   public static <T> boolean containsAll(T[] array, T... values) {
      Object[] var2 = values;
      int var3 = values.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         T value = var2[var4];
         if (!contains(array, value)) {
            return false;
         }
      }

      return true;
   }

   public static boolean containsIgnoreCase(CharSequence[] array, CharSequence value) {
      return indexOfIgnoreCase(array, value) > -1;
   }

   public static Object[] wrap(Object obj) {
      if (null == obj) {
         return null;
      } else if (isArray(obj)) {
         try {
            return (Object[])((Object[])obj);
         } catch (Exception var5) {
            switch (obj.getClass().getComponentType().getName()) {
               case "long":
                  return wrap((long[])((long[])obj));
               case "int":
                  return wrap((int[])((int[])obj));
               case "short":
                  return wrap((short[])((short[])obj));
               case "char":
                  return wrap((char[])((char[])obj));
               case "byte":
                  return wrap((byte[])((byte[])obj));
               case "boolean":
                  return wrap((boolean[])((boolean[])obj));
               case "float":
                  return wrap((float[])((float[])obj));
               case "double":
                  return wrap((double[])((double[])obj));
               default:
                  throw new UtilException(var5);
            }
         }
      } else {
         throw new UtilException(StrUtil.format("[{}] is not Array!", new Object[]{obj.getClass()}));
      }
   }

   public static boolean isArray(Object obj) {
      return null != obj && obj.getClass().isArray();
   }

   public static <T> T get(Object array, int index) {
      if (null == array) {
         return null;
      } else {
         if (index < 0) {
            index += Array.getLength(array);
         }

         try {
            return Array.get(array, index);
         } catch (ArrayIndexOutOfBoundsException var3) {
            return null;
         }
      }
   }

   public static <T> T[] getAny(Object array, int... indexes) {
      if (null == array) {
         return null;
      } else {
         T[] result = newArray(array.getClass().getComponentType(), indexes.length);
         int[] var3 = indexes;
         int var4 = indexes.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            int i = var3[var5];
            result[i] = get(array, i);
         }

         return result;
      }
   }

   public static <T> T[] sub(T[] array, int start, int end) {
      int length = length(array);
      if (start < 0) {
         start += length;
      }

      if (end < 0) {
         end += length;
      }

      if (start == length) {
         return newArray(array.getClass().getComponentType(), 0);
      } else {
         if (start > end) {
            int tmp = start;
            start = end;
            end = tmp;
         }

         if (end > length) {
            if (start >= length) {
               return newArray(array.getClass().getComponentType(), 0);
            }

            end = length;
         }

         return Arrays.copyOfRange(array, start, end);
      }
   }

   public static Object[] sub(Object array, int start, int end) {
      return sub(array, start, end, 1);
   }

   public static Object[] sub(Object array, int start, int end, int step) {
      int length = length(array);
      if (start < 0) {
         start += length;
      }

      if (end < 0) {
         end += length;
      }

      if (start == length) {
         return new Object[0];
      } else {
         if (start > end) {
            int tmp = start;
            start = end;
            end = tmp;
         }

         if (end > length) {
            if (start >= length) {
               return new Object[0];
            }

            end = length;
         }

         if (step <= 1) {
            step = 1;
         }

         ArrayList<Object> list = new ArrayList();

         for(int i = start; i < end; i += step) {
            list.add(get(array, i));
         }

         return list.toArray();
      }
   }

   public static String toString(Object obj) {
      if (null == obj) {
         return null;
      } else if (obj instanceof long[]) {
         return Arrays.toString((long[])((long[])obj));
      } else if (obj instanceof int[]) {
         return Arrays.toString((int[])((int[])obj));
      } else if (obj instanceof short[]) {
         return Arrays.toString((short[])((short[])obj));
      } else if (obj instanceof char[]) {
         return Arrays.toString((char[])((char[])obj));
      } else if (obj instanceof byte[]) {
         return Arrays.toString((byte[])((byte[])obj));
      } else if (obj instanceof boolean[]) {
         return Arrays.toString((boolean[])((boolean[])obj));
      } else if (obj instanceof float[]) {
         return Arrays.toString((float[])((float[])obj));
      } else if (obj instanceof double[]) {
         return Arrays.toString((double[])((double[])obj));
      } else {
         if (isArray(obj)) {
            try {
               return Arrays.deepToString((Object[])((Object[])obj));
            } catch (Exception var2) {
            }
         }

         return obj.toString();
      }
   }

   public static int length(Object array) throws IllegalArgumentException {
      return null == array ? 0 : Array.getLength(array);
   }

   public static <T> String join(T[] array, CharSequence conjunction) {
      return join(array, conjunction, (String)null, (String)null);
   }

   public static <T> String join(T[] array, CharSequence delimiter, String prefix, String suffix) {
      return null == array ? null : StrJoiner.of(delimiter, prefix, suffix).setWrapElement(true).append(array).toString();
   }

   public static <T> String join(T[] array, CharSequence conjunction, Editor<T> editor) {
      return StrJoiner.of(conjunction).append(array, (t) -> {
         return String.valueOf(editor.edit(t));
      }).toString();
   }

   public static String join(Object array, CharSequence conjunction) {
      if (null == array) {
         return null;
      } else if (!isArray(array)) {
         throw new IllegalArgumentException(StrUtil.format("[{}] is not a Array!", new Object[]{array.getClass()}));
      } else {
         return StrJoiner.of(conjunction).append(array).toString();
      }
   }

   public static byte[] toArray(ByteBuffer bytebuffer) {
      if (bytebuffer.hasArray()) {
         return Arrays.copyOfRange(bytebuffer.array(), bytebuffer.position(), bytebuffer.limit());
      } else {
         int oldPosition = bytebuffer.position();
         bytebuffer.position(0);
         int size = bytebuffer.limit();
         byte[] buffers = new byte[size];
         bytebuffer.get(buffers);
         bytebuffer.position(oldPosition);
         return buffers;
      }
   }

   public static <T> T[] toArray(Iterator<T> iterator, Class<T> componentType) {
      return toArray((Collection)CollUtil.newArrayList(iterator), componentType);
   }

   public static <T> T[] toArray(Iterable<T> iterable, Class<T> componentType) {
      return toArray(CollectionUtil.toCollection(iterable), componentType);
   }

   public static <T> T[] toArray(Collection<T> collection, Class<T> componentType) {
      return collection.toArray(newArray(componentType, 0));
   }

   public static <T> T[] remove(T[] array, int index) throws IllegalArgumentException {
      return (Object[])((Object[])remove(array, index));
   }

   public static <T> T[] removeEle(T[] array, T element) throws IllegalArgumentException {
      return remove(array, indexOf(array, element));
   }

   public static <T> T[] reverse(T[] array, int startIndexInclusive, int endIndexExclusive) {
      if (isEmpty(array)) {
         return array;
      } else {
         int i = Math.max(startIndexInclusive, 0);

         for(int j = Math.min(array.length, endIndexExclusive) - 1; j > i; ++i) {
            T tmp = array[j];
            array[j] = array[i];
            array[i] = tmp;
            --j;
         }

         return array;
      }
   }

   public static <T> T[] reverse(T[] array) {
      return reverse(array, 0, array.length);
   }

   public static <T extends Comparable<? super T>> T min(T[] numberArray) {
      return min(numberArray, (Comparator)null);
   }

   public static <T extends Comparable<? super T>> T min(T[] numberArray, Comparator<T> comparator) {
      if (isEmpty((Object[])numberArray)) {
         throw new IllegalArgumentException("Number array must not empty !");
      } else {
         T min = numberArray[0];
         Comparable[] var3 = numberArray;
         int var4 = numberArray.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            T t = var3[var5];
            if (CompareUtil.compare(min, t, comparator) > 0) {
               min = t;
            }
         }

         return min;
      }
   }

   public static <T extends Comparable<? super T>> T max(T[] numberArray) {
      return max(numberArray, (Comparator)null);
   }

   public static <T extends Comparable<? super T>> T max(T[] numberArray, Comparator<T> comparator) {
      if (isEmpty((Object[])numberArray)) {
         throw new IllegalArgumentException("Number array must not empty !");
      } else {
         T max = numberArray[0];

         for(int i = 1; i < numberArray.length; ++i) {
            if (CompareUtil.compare(max, numberArray[i], comparator) < 0) {
               max = numberArray[i];
            }
         }

         return max;
      }
   }

   public static <T> T[] shuffle(T[] array) {
      return shuffle(array, RandomUtil.getRandom());
   }

   public static <T> T[] shuffle(T[] array, Random random) {
      if (array != null && random != null && array.length > 1) {
         for(int i = array.length; i > 1; --i) {
            swap(array, i - 1, random.nextInt(i));
         }

         return array;
      } else {
         return array;
      }
   }

   public static <T> T[] swap(T[] array, int index1, int index2) {
      if (isEmpty(array)) {
         throw new IllegalArgumentException("Array must not empty !");
      } else {
         T tmp = array[index1];
         array[index1] = array[index2];
         array[index2] = tmp;
         return array;
      }
   }

   public static Object swap(Object array, int index1, int index2) {
      if (isEmpty(array)) {
         throw new IllegalArgumentException("Array must not empty !");
      } else {
         Object tmp = get(array, index1);
         Array.set(array, index1, Array.get(array, index2));
         Array.set(array, index2, tmp);
         return array;
      }
   }

   public static int emptyCount(Object... args) {
      int count = 0;
      if (isNotEmpty(args)) {
         Object[] var2 = args;
         int var3 = args.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            Object element = var2[var4];
            if (ObjectUtil.isEmpty(element)) {
               ++count;
            }
         }
      }

      return count;
   }

   public static boolean hasEmpty(Object... args) {
      if (isNotEmpty(args)) {
         Object[] var1 = args;
         int var2 = args.length;

         for(int var3 = 0; var3 < var2; ++var3) {
            Object element = var1[var3];
            if (ObjectUtil.isEmpty(element)) {
               return true;
            }
         }
      }

      return false;
   }

   public static boolean isAllEmpty(Object... args) {
      Object[] var1 = args;
      int var2 = args.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         Object obj = var1[var3];
         if (!ObjectUtil.isEmpty(obj)) {
            return false;
         }
      }

      return true;
   }

   public static boolean isAllNotEmpty(Object... args) {
      return !hasEmpty(args);
   }

   public static <T> boolean isAllNotNull(T... array) {
      return !hasNull(array);
   }

   public static <T> T[] distinct(T[] array) {
      if (isEmpty(array)) {
         return array;
      } else {
         Set<T> set = new LinkedHashSet(array.length, 1.0F);
         Collections.addAll(set, array);
         return toArray((Collection)set, getComponentType((Object)array));
      }
   }

   public static <T, K> T[] distinct(T[] array, Function<T, K> uniqueGenerator, boolean override) {
      if (isEmpty(array)) {
         return array;
      } else {
         UniqueKeySet<K, T> set = new UniqueKeySet(true, uniqueGenerator);
         if (override) {
            Collections.addAll(set, array);
         } else {
            Object[] var4 = array;
            int var5 = array.length;

            for(int var6 = 0; var6 < var5; ++var6) {
               T t = var4[var6];
               set.addIfAbsent(t);
            }
         }

         return toArray((Collection)set, getComponentType((Object)array));
      }
   }

   public static <T, R> R[] map(T[] array, Class<R> targetComponentType, Function<? super T, ? extends R> func) {
      R[] result = newArray(targetComponentType, array.length);

      for(int i = 0; i < array.length; ++i) {
         result[i] = func.apply(array[i]);
      }

      return result;
   }

   public static <T, R> R[] map(Object array, Class<R> targetComponentType, Function<? super T, ? extends R> func) {
      int length = length(array);
      R[] result = newArray(targetComponentType, length);

      for(int i = 0; i < length; ++i) {
         result[i] = func.apply(get(array, i));
      }

      return result;
   }

   public static <T, R> List<R> map(T[] array, Function<? super T, ? extends R> func) {
      return (List)Arrays.stream(array).map(func).collect(Collectors.toList());
   }

   public static <T, R> Set<R> mapToSet(T[] array, Function<? super T, ? extends R> func) {
      return (Set)Arrays.stream(array).map(func).collect(Collectors.toSet());
   }

   public static boolean equals(Object array1, Object array2) {
      if (array1 == array2) {
         return true;
      } else if (hasNull(array1, array2)) {
         return false;
      } else {
         Assert.isTrue(isArray(array1), "First is not a Array !");
         Assert.isTrue(isArray(array2), "Second is not a Array !");
         if (array1 instanceof long[]) {
            return Arrays.equals((long[])((long[])array1), (long[])((long[])array2));
         } else if (array1 instanceof int[]) {
            return Arrays.equals((int[])((int[])array1), (int[])((int[])array2));
         } else if (array1 instanceof short[]) {
            return Arrays.equals((short[])((short[])array1), (short[])((short[])array2));
         } else if (array1 instanceof char[]) {
            return Arrays.equals((char[])((char[])array1), (char[])((char[])array2));
         } else if (array1 instanceof byte[]) {
            return Arrays.equals((byte[])((byte[])array1), (byte[])((byte[])array2));
         } else if (array1 instanceof double[]) {
            return Arrays.equals((double[])((double[])array1), (double[])((double[])array2));
         } else if (array1 instanceof float[]) {
            return Arrays.equals((float[])((float[])array1), (float[])((float[])array2));
         } else {
            return array1 instanceof boolean[] ? Arrays.equals((boolean[])((boolean[])array1), (boolean[])((boolean[])array2)) : Arrays.deepEquals((Object[])((Object[])array1), (Object[])((Object[])array2));
         }
      }
   }

   public static <T> boolean isSub(T[] array, T[] subArray) {
      return indexOfSub(array, subArray) > -1;
   }

   public static <T> int indexOfSub(T[] array, T[] subArray) {
      return indexOfSub(array, 0, subArray);
   }

   public static <T> int indexOfSub(T[] array, int beginInclude, T[] subArray) {
      if (!isEmpty(array) && !isEmpty(subArray) && subArray.length <= array.length) {
         int firstIndex = indexOf(array, subArray[0], beginInclude);
         if (firstIndex >= 0 && firstIndex + subArray.length <= array.length) {
            for(int i = 0; i < subArray.length; ++i) {
               if (!ObjectUtil.equal(array[i + firstIndex], subArray[i])) {
                  return indexOfSub(array, firstIndex + 1, subArray);
               }
            }

            return firstIndex;
         } else {
            return -1;
         }
      } else {
         return -1;
      }
   }

   public static <T> int lastIndexOfSub(T[] array, T[] subArray) {
      return !isEmpty(array) && !isEmpty(subArray) ? lastIndexOfSub(array, array.length - 1, subArray) : -1;
   }

   public static <T> int lastIndexOfSub(T[] array, int endInclude, T[] subArray) {
      if (!isEmpty(array) && !isEmpty(subArray) && subArray.length <= array.length && endInclude >= 0) {
         int firstIndex = lastIndexOf(array, subArray[0]);
         if (firstIndex >= 0 && firstIndex + subArray.length <= array.length) {
            for(int i = 0; i < subArray.length; ++i) {
               if (!ObjectUtil.equal(array[i + firstIndex], subArray[i])) {
                  return lastIndexOfSub(array, firstIndex - 1, subArray);
               }
            }

            return firstIndex;
         } else {
            return -1;
         }
      } else {
         return -1;
      }
   }

   public static <T> boolean isSorted(T[] array, Comparator<? super T> comparator) {
      if (array != null && comparator != null) {
         for(int i = 0; i < array.length - 1; ++i) {
            if (comparator.compare(array[i], array[i + 1]) > 0) {
               return false;
            }
         }

         return true;
      } else {
         return false;
      }
   }

   public static <T extends Comparable<? super T>> boolean isSorted(T[] array) {
      return isSortedASC(array);
   }

   public static <T extends Comparable<? super T>> boolean isSortedASC(T[] array) {
      if (array == null) {
         return false;
      } else {
         for(int i = 0; i < array.length - 1; ++i) {
            if (array[i].compareTo(array[i + 1]) > 0) {
               return false;
            }
         }

         return true;
      }
   }

   public static <T extends Comparable<? super T>> boolean isSortedDESC(T[] array) {
      if (array == null) {
         return false;
      } else {
         for(int i = 0; i < array.length - 1; ++i) {
            if (array[i].compareTo(array[i + 1]) < 0) {
               return false;
            }
         }

         return true;
      }
   }
}
