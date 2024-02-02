/*      */ package cn.hutool.core.util;
/*      */ 
/*      */ import cn.hutool.core.collection.CollUtil;
/*      */ import cn.hutool.core.collection.CollectionUtil;
/*      */ import cn.hutool.core.collection.UniqueKeySet;
/*      */ import cn.hutool.core.comparator.CompareUtil;
/*      */ import cn.hutool.core.exceptions.UtilException;
/*      */ import cn.hutool.core.lang.Assert;
/*      */ import cn.hutool.core.lang.Editor;
/*      */ import cn.hutool.core.lang.Filter;
/*      */ import cn.hutool.core.lang.Matcher;
/*      */ import cn.hutool.core.map.MapUtil;
/*      */ import cn.hutool.core.text.CharSequenceUtil;
/*      */ import cn.hutool.core.text.StrJoiner;
/*      */ import java.lang.reflect.Array;
/*      */ import java.nio.ByteBuffer;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Arrays;
/*      */ import java.util.Collection;
/*      */ import java.util.Collections;
/*      */ import java.util.Comparator;
/*      */ import java.util.Iterator;
/*      */ import java.util.LinkedHashSet;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Random;
/*      */ import java.util.Set;
/*      */ import java.util.function.Function;
/*      */ import java.util.stream.Collectors;
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
/*      */ public class ArrayUtil
/*      */   extends PrimitiveArrayUtil
/*      */ {
/*      */   public static <T> boolean isEmpty(T[] array) {
/*   48 */     return (array == null || array.length == 0);
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
/*      */   public static <T> T[] defaultIfEmpty(T[] array, T[] defaultArray) {
/*   61 */     return isEmpty(array) ? defaultArray : array;
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
/*      */   public static boolean isEmpty(Object array) {
/*   74 */     if (array != null) {
/*   75 */       if (isArray(array)) {
/*   76 */         return (0 == Array.getLength(array));
/*      */       }
/*   78 */       return false;
/*      */     } 
/*   80 */     return true;
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
/*      */   public static <T> boolean isNotEmpty(T[] array) {
/*   93 */     return (null != array && array.length != 0);
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
/*      */   public static boolean isNotEmpty(Object array) {
/*  106 */     return (false == isEmpty(array));
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
/*      */   public static <T> boolean hasNull(T... array) {
/*  119 */     if (isNotEmpty(array)) {
/*  120 */       for (T element : array) {
/*  121 */         if (ObjectUtil.isNull(element)) {
/*  122 */           return true;
/*      */         }
/*      */       } 
/*      */     }
/*  126 */     return (array == null);
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
/*      */   public static <T> boolean isAllNull(T... array) {
/*  140 */     return (null == firstNonNull((Object[])array));
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
/*      */   public static <T> T firstNonNull(T... array) {
/*  153 */     return firstMatch(ObjectUtil::isNotNull, array);
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
/*      */   public static <T> T firstMatch(Matcher<T> matcher, T... array) {
/*  167 */     int index = matchIndex(matcher, array);
/*  168 */     if (index < 0) {
/*  169 */       return null;
/*      */     }
/*      */     
/*  172 */     return array[index];
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
/*      */   public static <T> int matchIndex(Matcher<T> matcher, T... array) {
/*  186 */     return matchIndex(matcher, 0, array);
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
/*      */   public static <T> int matchIndex(Matcher<T> matcher, int beginIndexInclude, T... array) {
/*  201 */     Assert.notNull(matcher, "Matcher must be not null !", new Object[0]);
/*  202 */     if (isNotEmpty(array)) {
/*  203 */       for (int i = beginIndexInclude; i < array.length; i++) {
/*  204 */         if (matcher.match(array[i])) {
/*  205 */           return i;
/*      */         }
/*      */       } 
/*      */     }
/*      */     
/*  210 */     return -1;
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
/*      */   public static <T> T[] newArray(Class<?> componentType, int newSize) {
/*  223 */     return (T[])Array.newInstance(componentType, newSize);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Object[] newArray(int newSize) {
/*  234 */     return new Object[newSize];
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Class<?> getComponentType(Object array) {
/*  245 */     return (null == array) ? null : array.getClass().getComponentType();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Class<?> getComponentType(Class<?> arrayClass) {
/*  256 */     return (null == arrayClass) ? null : arrayClass.getComponentType();
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
/*      */   public static Class<?> getArrayType(Class<?> componentType) {
/*  268 */     return Array.newInstance(componentType, 0).getClass();
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
/*      */   public static Object[] cast(Class<?> type, Object arrayObj) throws NullPointerException, IllegalArgumentException {
/*  284 */     if (null == arrayObj) {
/*  285 */       throw new NullPointerException("Argument [arrayObj] is null !");
/*      */     }
/*  287 */     if (false == arrayObj.getClass().isArray()) {
/*  288 */       throw new IllegalArgumentException("Argument [arrayObj] is not array !");
/*      */     }
/*  290 */     if (null == type) {
/*  291 */       return (Object[])arrayObj;
/*      */     }
/*      */     
/*  294 */     Class<?> componentType = type.isArray() ? type.getComponentType() : type;
/*  295 */     Object[] array = (Object[])arrayObj;
/*  296 */     Object[] result = newArray(componentType, array.length);
/*  297 */     System.arraycopy(array, 0, result, 0, array.length);
/*  298 */     return result;
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
/*      */   @SafeVarargs
/*      */   public static <T> T[] append(T[] buffer, T... newElements) {
/*  312 */     if (isEmpty(buffer)) {
/*  313 */       return newElements;
/*      */     }
/*  315 */     return insert(buffer, buffer.length, newElements);
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
/*      */   @SafeVarargs
/*      */   public static <T> Object append(Object array, T... newElements) {
/*  329 */     if (isEmpty(array)) {
/*  330 */       return newElements;
/*      */     }
/*  332 */     return insert(array, length(array), newElements);
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
/*      */   public static <T> T[] setOrAppend(T[] buffer, int index, T value) {
/*  346 */     if (index < buffer.length) {
/*  347 */       Array.set(buffer, index, value);
/*  348 */       return buffer;
/*      */     } 
/*  350 */     if (isEmpty(buffer)) {
/*      */ 
/*      */       
/*  353 */       T[] values = newArray(value.getClass(), 1);
/*  354 */       values[0] = value;
/*  355 */       return append(buffer, values);
/*      */     } 
/*  357 */     return append(buffer, (T[])new Object[] { value });
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
/*      */   public static Object setOrAppend(Object array, int index, Object value) {
/*  371 */     if (index < length(array)) {
/*  372 */       Array.set(array, index, value);
/*  373 */       return array;
/*      */     } 
/*  375 */     return append(array, new Object[] { value });
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
/*      */   public static <T> T[] replace(T[] buffer, int index, T... values) {
/*  393 */     if (isEmpty(values)) {
/*  394 */       return buffer;
/*      */     }
/*  396 */     if (isEmpty(buffer)) {
/*  397 */       return values;
/*      */     }
/*  399 */     if (index < 0)
/*      */     {
/*  401 */       return insert(buffer, 0, values);
/*      */     }
/*  403 */     if (index >= buffer.length)
/*      */     {
/*  405 */       return append(buffer, values);
/*      */     }
/*      */     
/*  408 */     if (buffer.length >= values.length + index) {
/*  409 */       System.arraycopy(values, 0, buffer, index, values.length);
/*  410 */       return buffer;
/*      */     } 
/*      */ 
/*      */     
/*  414 */     int newArrayLength = index + values.length;
/*  415 */     T[] result = newArray(buffer.getClass().getComponentType(), newArrayLength);
/*  416 */     System.arraycopy(buffer, 0, result, 0, index);
/*  417 */     System.arraycopy(values, 0, result, index, values.length);
/*  418 */     return result;
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
/*      */   public static <T> T[] insert(T[] buffer, int index, T... newElements) {
/*  435 */     return (T[])insert(buffer, index, newElements);
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
/*      */   public static <T> Object insert(Object array, int index, T... newElements) {
/*  452 */     if (isEmpty(newElements)) {
/*  453 */       return array;
/*      */     }
/*  455 */     if (isEmpty(array)) {
/*  456 */       return newElements;
/*      */     }
/*      */     
/*  459 */     int len = length(array);
/*  460 */     if (index < 0) {
/*  461 */       index = index % len + len;
/*      */     }
/*      */     
/*  464 */     T[] result = newArray(array.getClass().getComponentType(), Math.max(len, index) + newElements.length);
/*  465 */     System.arraycopy(array, 0, result, 0, Math.min(len, index));
/*  466 */     System.arraycopy(newElements, 0, result, index, newElements.length);
/*  467 */     if (index < len) {
/*  468 */       System.arraycopy(array, index, result, index + newElements.length, len - index);
/*      */     }
/*  470 */     return result;
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
/*      */   public static <T> T[] resize(T[] data, int newSize, Class<?> componentType) {
/*  484 */     if (newSize < 0) {
/*  485 */       return data;
/*      */     }
/*      */     
/*  488 */     T[] newArray = newArray(componentType, newSize);
/*  489 */     if (newSize > 0 && isNotEmpty(data)) {
/*  490 */       System.arraycopy(data, 0, newArray, 0, Math.min(data.length, newSize));
/*      */     }
/*  492 */     return newArray;
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
/*      */   public static Object resize(Object array, int newSize) {
/*  505 */     if (newSize < 0) {
/*  506 */       return array;
/*      */     }
/*  508 */     if (null == array) {
/*  509 */       return null;
/*      */     }
/*  511 */     int length = length(array);
/*  512 */     Object newArray = Array.newInstance(array.getClass().getComponentType(), newSize);
/*  513 */     if (newSize > 0 && isNotEmpty(array))
/*      */     {
/*  515 */       System.arraycopy(array, 0, newArray, 0, Math.min(length, newSize));
/*      */     }
/*  517 */     return newArray;
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
/*      */   public static <T> T[] resize(T[] buffer, int newSize) {
/*  530 */     return resize(buffer, newSize, buffer.getClass().getComponentType());
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
/*      */   @SafeVarargs
/*      */   public static <T> T[] addAll(T[]... arrays) {
/*  543 */     if (arrays.length == 1) {
/*  544 */       return arrays[0];
/*      */     }
/*      */     
/*  547 */     int length = 0;
/*  548 */     for (T[] array : arrays) {
/*  549 */       if (null != array) {
/*  550 */         length += array.length;
/*      */       }
/*      */     } 
/*  553 */     T[] result = newArray(arrays.getClass().getComponentType().getComponentType(), length);
/*      */     
/*  555 */     length = 0;
/*  556 */     for (T[] array : arrays) {
/*  557 */       if (null != array) {
/*  558 */         System.arraycopy(array, 0, result, length, array.length);
/*  559 */         length += array.length;
/*      */       } 
/*      */     } 
/*  562 */     return result;
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
/*      */   public static Object copy(Object src, int srcPos, Object dest, int destPos, int length) {
/*  579 */     System.arraycopy(src, srcPos, dest, destPos, length);
/*  580 */     return dest;
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
/*      */   public static Object copy(Object src, Object dest, int length) {
/*  595 */     System.arraycopy(src, 0, dest, 0, length);
/*  596 */     return dest;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <T> T[] clone(T[] array) {
/*  607 */     if (array == null) {
/*  608 */       return null;
/*      */     }
/*  610 */     return (T[])array.clone();
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
/*      */   public static <T> T clone(T obj) {
/*  622 */     if (null == obj) {
/*  623 */       return null;
/*      */     }
/*  625 */     if (isArray(obj)) {
/*      */       Object result;
/*  627 */       Class<?> componentType = obj.getClass().getComponentType();
/*  628 */       if (componentType.isPrimitive()) {
/*  629 */         int length = Array.getLength(obj);
/*  630 */         result = Array.newInstance(componentType, length);
/*  631 */         while (length-- > 0) {
/*  632 */           Array.set(result, length, Array.get(obj, length));
/*      */         }
/*      */       } else {
/*  635 */         result = ((Object[])obj).clone();
/*      */       } 
/*  637 */       return (T)result;
/*      */     } 
/*  639 */     return null;
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
/*      */   public static <T> T[] edit(T[] array, Editor<T> editor) {
/*  659 */     if (null == editor) {
/*  660 */       return array;
/*      */     }
/*      */     
/*  663 */     ArrayList<T> list = new ArrayList<>(array.length);
/*      */     
/*  665 */     for (T t : array) {
/*  666 */       T modified = (T)editor.edit(t);
/*  667 */       if (null != modified) {
/*  668 */         list.add(modified);
/*      */       }
/*      */     } 
/*  671 */     T[] result = newArray(array.getClass().getComponentType(), list.size());
/*  672 */     return list.toArray(result);
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
/*      */   public static <T> T[] filter(T[] array, Filter<T> filter) {
/*  690 */     if (null == array || null == filter) {
/*  691 */       return array;
/*      */     }
/*  693 */     return edit(array, t -> filter.accept(t) ? t : null);
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
/*      */   public static <T> T[] removeNull(T[] array) {
/*  705 */     return edit(array, t -> t);
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
/*      */   public static <T extends CharSequence> T[] removeEmpty(T[] array) {
/*  720 */     return (T[])filter((CharSequence[])array, CharSequenceUtil::isNotEmpty);
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
/*      */   public static <T extends CharSequence> T[] removeBlank(T[] array) {
/*  732 */     return (T[])filter((CharSequence[])array, CharSequenceUtil::isNotBlank);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String[] nullToEmpty(String[] array) {
/*  743 */     return edit(array, t -> (null == t) ? "" : t);
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
/*      */   public static <K, V> Map<K, V> zip(K[] keys, V[] values, boolean isOrder) {
/*  763 */     if (isEmpty(keys) || isEmpty(values)) {
/*  764 */       return null;
/*      */     }
/*      */     
/*  767 */     int size = Math.min(keys.length, values.length);
/*  768 */     Map<K, V> map = MapUtil.newHashMap(size, isOrder);
/*  769 */     for (int i = 0; i < size; i++) {
/*  770 */       map.put(keys[i], values[i]);
/*      */     }
/*      */     
/*  773 */     return map;
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
/*      */   public static <K, V> Map<K, V> zip(K[] keys, V[] values) {
/*  791 */     return zip(keys, values, false);
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
/*      */   public static <T> int indexOf(T[] array, Object value, int beginIndexInclude) {
/*  807 */     return matchIndex(obj -> ObjectUtil.equal(value, obj), beginIndexInclude, array);
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
/*      */   public static <T> int indexOf(T[] array, Object value) {
/*  820 */     return matchIndex(obj -> ObjectUtil.equal(value, obj), array);
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
/*      */   public static int indexOfIgnoreCase(CharSequence[] array, CharSequence value) {
/*  832 */     if (null != array) {
/*  833 */       for (int i = 0; i < array.length; i++) {
/*  834 */         if (StrUtil.equalsIgnoreCase(array[i], value)) {
/*  835 */           return i;
/*      */         }
/*      */       } 
/*      */     }
/*  839 */     return -1;
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
/*      */   public static <T> int lastIndexOf(T[] array, Object value) {
/*  852 */     if (isEmpty(array)) {
/*  853 */       return -1;
/*      */     }
/*  855 */     return lastIndexOf(array, value, array.length - 1);
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
/*      */   public static <T> int lastIndexOf(T[] array, Object value, int endInclude) {
/*  869 */     if (isNotEmpty(array)) {
/*  870 */       for (int i = endInclude; i >= 0; i--) {
/*  871 */         if (ObjectUtil.equal(value, array[i])) {
/*  872 */           return i;
/*      */         }
/*      */       } 
/*      */     }
/*  876 */     return -1;
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
/*      */   public static <T> boolean contains(T[] array, T value) {
/*  888 */     return (indexOf(array, value) > -1);
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
/*      */   public static <T> boolean containsAny(T[] array, T... values) {
/*  902 */     for (T value : values) {
/*  903 */       if (contains(array, value)) {
/*  904 */         return true;
/*      */       }
/*      */     } 
/*  907 */     return false;
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
/*      */   public static <T> boolean containsAll(T[] array, T... values) {
/*  921 */     for (T value : values) {
/*  922 */       if (false == contains(array, value)) {
/*  923 */         return false;
/*      */       }
/*      */     } 
/*  926 */     return true;
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
/*      */   public static boolean containsIgnoreCase(CharSequence[] array, CharSequence value) {
/*  938 */     return (indexOfIgnoreCase(array, value) > -1);
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
/*      */   public static Object[] wrap(Object obj) {
/*  951 */     if (null == obj) {
/*  952 */       return null;
/*      */     }
/*  954 */     if (isArray(obj)) {
/*      */       try {
/*  956 */         return (Object[])obj;
/*  957 */       } catch (Exception e) {
/*  958 */         String className = obj.getClass().getComponentType().getName();
/*  959 */         switch (className) {
/*      */           case "long":
/*  961 */             return (Object[])wrap((long[])obj);
/*      */           case "int":
/*  963 */             return (Object[])wrap((int[])obj);
/*      */           case "short":
/*  965 */             return (Object[])wrap((short[])obj);
/*      */           case "char":
/*  967 */             return (Object[])wrap((char[])obj);
/*      */           case "byte":
/*  969 */             return (Object[])wrap((byte[])obj);
/*      */           case "boolean":
/*  971 */             return (Object[])wrap((boolean[])obj);
/*      */           case "float":
/*  973 */             return (Object[])wrap((float[])obj);
/*      */           case "double":
/*  975 */             return (Object[])wrap((double[])obj);
/*      */         } 
/*  977 */         throw new UtilException(e);
/*      */       } 
/*      */     }
/*      */     
/*  981 */     throw new UtilException(StrUtil.format("[{}] is not Array!", new Object[] { obj.getClass() }));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean isArray(Object obj) {
/*  991 */     return (null != obj && obj.getClass().isArray());
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
/*      */   public static <T> T get(Object array, int index) {
/* 1006 */     if (null == array) {
/* 1007 */       return null;
/*      */     }
/*      */     
/* 1010 */     if (index < 0) {
/* 1011 */       index += Array.getLength(array);
/*      */     }
/*      */     try {
/* 1014 */       return (T)Array.get(array, index);
/* 1015 */     } catch (ArrayIndexOutOfBoundsException e) {
/* 1016 */       return null;
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
/*      */   public static <T> T[] getAny(Object array, int... indexes) {
/* 1029 */     if (null == array) {
/* 1030 */       return null;
/*      */     }
/*      */     
/* 1033 */     T[] result = newArray(array.getClass().getComponentType(), indexes.length);
/* 1034 */     for (int i : indexes) {
/* 1035 */       result[i] = get(array, i);
/*      */     }
/* 1037 */     return result;
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
/*      */   public static <T> T[] sub(T[] array, int start, int end) {
/* 1052 */     int length = length(array);
/* 1053 */     if (start < 0) {
/* 1054 */       start += length;
/*      */     }
/* 1056 */     if (end < 0) {
/* 1057 */       end += length;
/*      */     }
/* 1059 */     if (start == length) {
/* 1060 */       return newArray(array.getClass().getComponentType(), 0);
/*      */     }
/* 1062 */     if (start > end) {
/* 1063 */       int tmp = start;
/* 1064 */       start = end;
/* 1065 */       end = tmp;
/*      */     } 
/* 1067 */     if (end > length) {
/* 1068 */       if (start >= length) {
/* 1069 */         return newArray(array.getClass().getComponentType(), 0);
/*      */       }
/* 1071 */       end = length;
/*      */     } 
/* 1073 */     return Arrays.copyOfRange(array, start, end);
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
/*      */   public static Object[] sub(Object array, int start, int end) {
/* 1086 */     return sub(array, start, end, 1);
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
/*      */   public static Object[] sub(Object array, int start, int end, int step) {
/* 1100 */     int length = length(array);
/* 1101 */     if (start < 0) {
/* 1102 */       start += length;
/*      */     }
/* 1104 */     if (end < 0) {
/* 1105 */       end += length;
/*      */     }
/* 1107 */     if (start == length) {
/* 1108 */       return new Object[0];
/*      */     }
/* 1110 */     if (start > end) {
/* 1111 */       int tmp = start;
/* 1112 */       start = end;
/* 1113 */       end = tmp;
/*      */     } 
/* 1115 */     if (end > length) {
/* 1116 */       if (start >= length) {
/* 1117 */         return new Object[0];
/*      */       }
/* 1119 */       end = length;
/*      */     } 
/*      */     
/* 1122 */     if (step <= 1) {
/* 1123 */       step = 1;
/*      */     }
/*      */     
/* 1126 */     ArrayList<Object> list = new ArrayList(); int i;
/* 1127 */     for (i = start; i < end; i += step) {
/* 1128 */       list.add(get(array, i));
/*      */     }
/*      */     
/* 1131 */     return list.toArray();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String toString(Object obj) {
/* 1141 */     if (null == obj) {
/* 1142 */       return null;
/*      */     }
/*      */     
/* 1145 */     if (obj instanceof long[])
/* 1146 */       return Arrays.toString((long[])obj); 
/* 1147 */     if (obj instanceof int[])
/* 1148 */       return Arrays.toString((int[])obj); 
/* 1149 */     if (obj instanceof short[])
/* 1150 */       return Arrays.toString((short[])obj); 
/* 1151 */     if (obj instanceof char[])
/* 1152 */       return Arrays.toString((char[])obj); 
/* 1153 */     if (obj instanceof byte[])
/* 1154 */       return Arrays.toString((byte[])obj); 
/* 1155 */     if (obj instanceof boolean[])
/* 1156 */       return Arrays.toString((boolean[])obj); 
/* 1157 */     if (obj instanceof float[])
/* 1158 */       return Arrays.toString((float[])obj); 
/* 1159 */     if (obj instanceof double[])
/* 1160 */       return Arrays.toString((double[])obj); 
/* 1161 */     if (isArray(obj)) {
/*      */       
/*      */       try {
/* 1164 */         return Arrays.deepToString((Object[])obj);
/* 1165 */       } catch (Exception exception) {}
/*      */     }
/*      */ 
/*      */ 
/*      */     
/* 1170 */     return obj.toString();
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
/*      */   public static int length(Object array) throws IllegalArgumentException {
/* 1193 */     if (null == array) {
/* 1194 */       return 0;
/*      */     }
/* 1196 */     return Array.getLength(array);
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
/*      */   public static <T> String join(T[] array, CharSequence conjunction) {
/* 1208 */     return join(array, conjunction, (String)null, (String)null);
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
/*      */   public static <T> String join(T[] array, CharSequence delimiter, String prefix, String suffix) {
/* 1223 */     if (null == array) {
/* 1224 */       return null;
/*      */     }
/*      */     
/* 1227 */     return StrJoiner.of(delimiter, prefix, suffix)
/*      */       
/* 1229 */       .setWrapElement(true)
/* 1230 */       .append((Object[])array)
/* 1231 */       .toString();
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
/*      */   public static <T> String join(T[] array, CharSequence conjunction, Editor<T> editor) {
/* 1245 */     return StrJoiner.of(conjunction).append((Object[])array, t -> String.valueOf(editor.edit(t))).toString();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String join(Object array, CharSequence conjunction) {
/* 1256 */     if (null == array) {
/* 1257 */       return null;
/*      */     }
/* 1259 */     if (false == isArray(array)) {
/* 1260 */       throw new IllegalArgumentException(StrUtil.format("[{}] is not a Array!", new Object[] { array.getClass() }));
/*      */     }
/*      */     
/* 1263 */     return StrJoiner.of(conjunction).append(array).toString();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static byte[] toArray(ByteBuffer bytebuffer) {
/* 1274 */     if (bytebuffer.hasArray()) {
/* 1275 */       return Arrays.copyOfRange(bytebuffer.array(), bytebuffer.position(), bytebuffer.limit());
/*      */     }
/* 1277 */     int oldPosition = bytebuffer.position();
/* 1278 */     bytebuffer.position(0);
/* 1279 */     int size = bytebuffer.limit();
/* 1280 */     byte[] buffers = new byte[size];
/* 1281 */     bytebuffer.get(buffers);
/* 1282 */     bytebuffer.position(oldPosition);
/* 1283 */     return buffers;
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
/*      */   public static <T> T[] toArray(Iterator<T> iterator, Class<T> componentType) {
/* 1297 */     return toArray(CollUtil.newArrayList(iterator), componentType);
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
/*      */   public static <T> T[] toArray(Iterable<T> iterable, Class<T> componentType) {
/* 1310 */     return toArray(CollectionUtil.toCollection(iterable), componentType);
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
/*      */   public static <T> T[] toArray(Collection<T> collection, Class<T> componentType) {
/* 1323 */     return collection.toArray(newArray(componentType, 0));
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
/*      */   public static <T> T[] remove(T[] array, int index) throws IllegalArgumentException {
/* 1341 */     return (T[])remove(array, index);
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
/*      */   public static <T> T[] removeEle(T[] array, T element) throws IllegalArgumentException {
/* 1358 */     return remove(array, indexOf(array, element));
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
/*      */   public static <T> T[] reverse(T[] array, int startIndexInclusive, int endIndexExclusive) {
/* 1374 */     if (isEmpty(array)) {
/* 1375 */       return array;
/*      */     }
/* 1377 */     int i = Math.max(startIndexInclusive, 0);
/* 1378 */     int j = Math.min(array.length, endIndexExclusive) - 1;
/*      */     
/* 1380 */     while (j > i) {
/* 1381 */       T tmp = array[j];
/* 1382 */       array[j] = array[i];
/* 1383 */       array[i] = tmp;
/* 1384 */       j--;
/* 1385 */       i++;
/*      */     } 
/* 1387 */     return array;
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
/*      */   public static <T> T[] reverse(T[] array) {
/* 1399 */     return reverse(array, 0, array.length);
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
/*      */   public static <T extends Comparable<? super T>> T min(T[] numberArray) {
/* 1413 */     return min(numberArray, (Comparator<T>)null);
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
/*      */   public static <T extends Comparable<? super T>> T min(T[] numberArray, Comparator<T> comparator) {
/* 1426 */     if (isEmpty(numberArray)) {
/* 1427 */       throw new IllegalArgumentException("Number array must not empty !");
/*      */     }
/* 1429 */     T min = numberArray[0];
/* 1430 */     for (T t : numberArray) {
/* 1431 */       if (CompareUtil.compare(min, t, comparator) > 0) {
/* 1432 */         min = t;
/*      */       }
/*      */     } 
/* 1435 */     return min;
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
/*      */   public static <T extends Comparable<? super T>> T max(T[] numberArray) {
/* 1447 */     return max(numberArray, (Comparator<T>)null);
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
/*      */   public static <T extends Comparable<? super T>> T max(T[] numberArray, Comparator<T> comparator) {
/* 1460 */     if (isEmpty(numberArray)) {
/* 1461 */       throw new IllegalArgumentException("Number array must not empty !");
/*      */     }
/* 1463 */     T max = numberArray[0];
/* 1464 */     for (int i = 1; i < numberArray.length; i++) {
/* 1465 */       if (CompareUtil.compare(max, numberArray[i], comparator) < 0) {
/* 1466 */         max = numberArray[i];
/*      */       }
/*      */     } 
/* 1469 */     return max;
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
/*      */   public static <T> T[] shuffle(T[] array) {
/* 1484 */     return shuffle(array, RandomUtil.getRandom());
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
/*      */   public static <T> T[] shuffle(T[] array, Random random) {
/* 1498 */     if (array == null || random == null || array.length <= 1) {
/* 1499 */       return array;
/*      */     }
/*      */     
/* 1502 */     for (int i = array.length; i > 1; i--) {
/* 1503 */       swap(array, i - 1, random.nextInt(i));
/*      */     }
/*      */     
/* 1506 */     return array;
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
/*      */   public static <T> T[] swap(T[] array, int index1, int index2) {
/* 1520 */     if (isEmpty(array)) {
/* 1521 */       throw new IllegalArgumentException("Array must not empty !");
/*      */     }
/* 1523 */     T tmp = array[index1];
/* 1524 */     array[index1] = array[index2];
/* 1525 */     array[index2] = tmp;
/* 1526 */     return array;
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
/*      */   public static Object swap(Object array, int index1, int index2) {
/* 1539 */     if (isEmpty(array)) {
/* 1540 */       throw new IllegalArgumentException("Array must not empty !");
/*      */     }
/* 1542 */     Object tmp = get(array, index1);
/* 1543 */     Array.set(array, index1, Array.get(array, index2));
/* 1544 */     Array.set(array, index2, tmp);
/* 1545 */     return array;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static int emptyCount(Object... args) {
/* 1556 */     int count = 0;
/* 1557 */     if (isNotEmpty(args)) {
/* 1558 */       for (Object element : args) {
/* 1559 */         if (ObjectUtil.isEmpty(element)) {
/* 1560 */           count++;
/*      */         }
/*      */       } 
/*      */     }
/* 1564 */     return count;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean hasEmpty(Object... args) {
/* 1575 */     if (isNotEmpty(args)) {
/* 1576 */       for (Object element : args) {
/* 1577 */         if (ObjectUtil.isEmpty(element)) {
/* 1578 */           return true;
/*      */         }
/*      */       } 
/*      */     }
/* 1582 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean isAllEmpty(Object... args) {
/* 1593 */     for (Object obj : args) {
/* 1594 */       if (false == ObjectUtil.isEmpty(obj)) {
/* 1595 */         return false;
/*      */       }
/*      */     } 
/* 1598 */     return true;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean isAllNotEmpty(Object... args) {
/* 1609 */     return (false == hasEmpty(args));
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
/*      */   public static <T> boolean isAllNotNull(T... array) {
/* 1622 */     return (false == hasNull(array));
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
/*      */   public static <T> T[] distinct(T[] array) {
/* 1635 */     if (isEmpty(array)) {
/* 1636 */       return array;
/*      */     }
/*      */     
/* 1639 */     Set<T> set = new LinkedHashSet<>(array.length, 1.0F);
/* 1640 */     Collections.addAll(set, array);
/* 1641 */     return toArray(set, (Class)getComponentType(array));
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
/*      */   public static <T, K> T[] distinct(T[] array, Function<T, K> uniqueGenerator, boolean override) {
/* 1658 */     if (isEmpty(array)) {
/* 1659 */       return array;
/*      */     }
/*      */     
/* 1662 */     UniqueKeySet<K, T> set = new UniqueKeySet(true, uniqueGenerator);
/* 1663 */     if (override) {
/* 1664 */       Collections.addAll((Collection)set, (K[])array);
/*      */     } else {
/* 1666 */       for (T t : array) {
/* 1667 */         set.addIfAbsent(t);
/*      */       }
/*      */     } 
/* 1670 */     return toArray((Collection)set, (Class)getComponentType(array));
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
/*      */   public static <T, R> R[] map(T[] array, Class<R> targetComponentType, Function<? super T, ? extends R> func) {
/* 1685 */     R[] result = newArray(targetComponentType, array.length);
/* 1686 */     for (int i = 0; i < array.length; i++) {
/* 1687 */       result[i] = func.apply(array[i]);
/*      */     }
/* 1689 */     return result;
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
/*      */   public static <T, R> R[] map(Object array, Class<R> targetComponentType, Function<? super T, ? extends R> func) {
/* 1704 */     int length = length(array);
/* 1705 */     R[] result = newArray(targetComponentType, length);
/* 1706 */     for (int i = 0; i < length; i++) {
/* 1707 */       result[i] = func.apply(get(array, i));
/*      */     }
/* 1709 */     return result;
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
/*      */   public static <T, R> List<R> map(T[] array, Function<? super T, ? extends R> func) {
/* 1723 */     return (List<R>)Arrays.<T>stream(array).<R>map(func).collect(Collectors.toList());
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
/*      */   public static <T, R> Set<R> mapToSet(T[] array, Function<? super T, ? extends R> func) {
/* 1737 */     return (Set<R>)Arrays.<T>stream(array).<R>map(func).collect(Collectors.toSet());
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
/*      */   public static boolean equals(Object array1, Object array2) {
/* 1749 */     if (array1 == array2) {
/* 1750 */       return true;
/*      */     }
/* 1752 */     if (hasNull(new Object[] { array1, array2 })) {
/* 1753 */       return false;
/*      */     }
/*      */     
/* 1756 */     Assert.isTrue(isArray(array1), "First is not a Array !", new Object[0]);
/* 1757 */     Assert.isTrue(isArray(array2), "Second is not a Array !", new Object[0]);
/*      */     
/* 1759 */     if (array1 instanceof long[])
/* 1760 */       return Arrays.equals((long[])array1, (long[])array2); 
/* 1761 */     if (array1 instanceof int[])
/* 1762 */       return Arrays.equals((int[])array1, (int[])array2); 
/* 1763 */     if (array1 instanceof short[])
/* 1764 */       return Arrays.equals((short[])array1, (short[])array2); 
/* 1765 */     if (array1 instanceof char[])
/* 1766 */       return Arrays.equals((char[])array1, (char[])array2); 
/* 1767 */     if (array1 instanceof byte[])
/* 1768 */       return Arrays.equals((byte[])array1, (byte[])array2); 
/* 1769 */     if (array1 instanceof double[])
/* 1770 */       return Arrays.equals((double[])array1, (double[])array2); 
/* 1771 */     if (array1 instanceof float[])
/* 1772 */       return Arrays.equals((float[])array1, (float[])array2); 
/* 1773 */     if (array1 instanceof boolean[]) {
/* 1774 */       return Arrays.equals((boolean[])array1, (boolean[])array2);
/*      */     }
/*      */     
/* 1777 */     return Arrays.deepEquals((Object[])array1, (Object[])array2);
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
/*      */   public static <T> boolean isSub(T[] array, T[] subArray) {
/* 1791 */     return (indexOfSub(array, subArray) > -1);
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
/*      */   public static <T> int indexOfSub(T[] array, T[] subArray) {
/* 1804 */     return indexOfSub(array, 0, subArray);
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
/*      */   public static <T> int indexOfSub(T[] array, int beginInclude, T[] subArray) {
/* 1818 */     if (isEmpty(array) || isEmpty(subArray) || subArray.length > array.length) {
/* 1819 */       return -1;
/*      */     }
/* 1821 */     int firstIndex = indexOf(array, subArray[0], beginInclude);
/* 1822 */     if (firstIndex < 0 || firstIndex + subArray.length > array.length) {
/* 1823 */       return -1;
/*      */     }
/*      */     
/* 1826 */     for (int i = 0; i < subArray.length; i++) {
/* 1827 */       if (false == ObjectUtil.equal(array[i + firstIndex], subArray[i])) {
/* 1828 */         return indexOfSub(array, firstIndex + 1, subArray);
/*      */       }
/*      */     } 
/*      */     
/* 1832 */     return firstIndex;
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
/*      */   public static <T> int lastIndexOfSub(T[] array, T[] subArray) {
/* 1845 */     if (isEmpty(array) || isEmpty(subArray)) {
/* 1846 */       return -1;
/*      */     }
/* 1848 */     return lastIndexOfSub(array, array.length - 1, subArray);
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
/*      */   public static <T> int lastIndexOfSub(T[] array, int endInclude, T[] subArray) {
/* 1862 */     if (isEmpty(array) || isEmpty(subArray) || subArray.length > array.length || endInclude < 0) {
/* 1863 */       return -1;
/*      */     }
/*      */     
/* 1866 */     int firstIndex = lastIndexOf(array, subArray[0]);
/* 1867 */     if (firstIndex < 0 || firstIndex + subArray.length > array.length) {
/* 1868 */       return -1;
/*      */     }
/*      */     
/* 1871 */     for (int i = 0; i < subArray.length; i++) {
/* 1872 */       if (false == ObjectUtil.equal(array[i + firstIndex], subArray[i])) {
/* 1873 */         return lastIndexOfSub(array, firstIndex - 1, subArray);
/*      */       }
/*      */     } 
/*      */     
/* 1877 */     return firstIndex;
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
/*      */   public static <T> boolean isSorted(T[] array, Comparator<? super T> comparator) {
/* 1893 */     if (array == null || comparator == null) {
/* 1894 */       return false;
/*      */     }
/*      */     
/* 1897 */     for (int i = 0; i < array.length - 1; i++) {
/* 1898 */       if (comparator.compare(array[i], array[i + 1]) > 0) {
/* 1899 */         return false;
/*      */       }
/*      */     } 
/* 1902 */     return true;
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
/*      */   public static <T extends Comparable<? super T>> boolean isSorted(T[] array) {
/* 1915 */     return isSortedASC(array);
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
/*      */   public static <T extends Comparable<? super T>> boolean isSortedASC(T[] array) {
/* 1929 */     if (array == null) {
/* 1930 */       return false;
/*      */     }
/*      */     
/* 1933 */     for (int i = 0; i < array.length - 1; i++) {
/* 1934 */       if (array[i].compareTo(array[i + 1]) > 0) {
/* 1935 */         return false;
/*      */       }
/*      */     } 
/*      */     
/* 1939 */     return true;
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
/*      */   public static <T extends Comparable<? super T>> boolean isSortedDESC(T[] array) {
/* 1952 */     if (array == null) {
/* 1953 */       return false;
/*      */     }
/*      */     
/* 1956 */     for (int i = 0; i < array.length - 1; i++) {
/* 1957 */       if (array[i].compareTo(array[i + 1]) < 0) {
/* 1958 */         return false;
/*      */       }
/*      */     } 
/*      */     
/* 1962 */     return true;
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\cor\\util\ArrayUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */