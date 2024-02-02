/*      */ package cn.hutool.core.collection;
/*      */ 
/*      */ import cn.hutool.core.exceptions.UtilException;
/*      */ import cn.hutool.core.lang.Assert;
/*      */ import cn.hutool.core.lang.Editor;
/*      */ import cn.hutool.core.lang.Filter;
/*      */ import cn.hutool.core.lang.Matcher;
/*      */ import cn.hutool.core.lang.func.Func1;
/*      */ import cn.hutool.core.map.MapUtil;
/*      */ import cn.hutool.core.text.StrJoiner;
/*      */ import cn.hutool.core.util.ArrayUtil;
/*      */ import cn.hutool.core.util.ObjectUtil;
/*      */ import cn.hutool.core.util.ReflectUtil;
/*      */ import java.lang.invoke.SerializedLambda;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Collection;
/*      */ import java.util.Collections;
/*      */ import java.util.Dictionary;
/*      */ import java.util.Enumeration;
/*      */ import java.util.HashMap;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Objects;
/*      */ import java.util.function.Consumer;
/*      */ import java.util.function.Function;
/*      */ import org.w3c.dom.Node;
/*      */ import org.w3c.dom.NodeList;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class IterUtil
/*      */ {
/*      */   public static <T> Iterator<T> getIter(Iterable<T> iterable) {
/*   48 */     return (null == iterable) ? null : iterable.iterator();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean isEmpty(Iterable<?> iterable) {
/*   58 */     return (null == iterable || isEmpty(iterable.iterator()));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean isEmpty(Iterator<?> iterator) {
/*   68 */     return (null == iterator || false == iterator.hasNext());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean isNotEmpty(Iterable<?> iterable) {
/*   78 */     return (null != iterable && isNotEmpty(iterable.iterator()));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean isNotEmpty(Iterator<?> iterator) {
/*   88 */     return (null != iterator && iterator.hasNext());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean hasNull(Iterable<?> iter) {
/*   98 */     return hasNull((null == iter) ? null : iter.iterator());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean hasNull(Iterator<?> iter) {
/*  108 */     if (null == iter) {
/*  109 */       return true;
/*      */     }
/*  111 */     while (iter.hasNext()) {
/*  112 */       if (null == iter.next()) {
/*  113 */         return true;
/*      */       }
/*      */     } 
/*      */     
/*  117 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean isAllNull(Iterable<?> iter) {
/*  128 */     return isAllNull((null == iter) ? null : iter.iterator());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean isAllNull(Iterator<?> iter) {
/*  139 */     return (null == getFirstNoneNull((Iterator)iter));
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
/*      */   public static <T> Map<T, Integer> countMap(Iterator<T> iter) {
/*  155 */     HashMap<T, Integer> countMap = new HashMap<>();
/*  156 */     if (null != iter)
/*      */     {
/*  158 */       while (iter.hasNext()) {
/*  159 */         T t = iter.next();
/*  160 */         countMap.put(t, Integer.valueOf(((Integer)countMap.getOrDefault(t, Integer.valueOf(0))).intValue() + 1));
/*      */       } 
/*      */     }
/*  163 */     return countMap;
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
/*      */   public static <K, V> Map<K, V> fieldValueMap(Iterator<V> iter, String fieldName) {
/*  179 */     return toMap(iter, new HashMap<>(), value -> ReflectUtil.getFieldValue(value, fieldName));
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
/*      */   public static <K, V> Map<K, V> fieldValueAsMap(Iterator<?> iter, String fieldNameForKey, String fieldNameForValue) {
/*  195 */     return toMap(iter, new HashMap<>(), value -> ReflectUtil.getFieldValue(value, fieldNameForKey), value -> ReflectUtil.getFieldValue(value, fieldNameForValue));
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
/*      */   public static <V> List<Object> fieldValueList(Iterable<V> iterable, String fieldName) {
/*  211 */     return fieldValueList(getIter(iterable), fieldName);
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
/*      */   public static <V> List<Object> fieldValueList(Iterator<V> iter, String fieldName) {
/*  224 */     List<Object> result = new ArrayList();
/*  225 */     if (null != iter)
/*      */     {
/*  227 */       while (iter.hasNext()) {
/*  228 */         V value = iter.next();
/*  229 */         result.add(ReflectUtil.getFieldValue(value, fieldName));
/*      */       } 
/*      */     }
/*  232 */     return result;
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
/*      */   public static <T> String join(Iterator<T> iterator, CharSequence conjunction) {
/*  245 */     return StrJoiner.of(conjunction).append(iterator).toString();
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
/*      */   public static <T> String join(Iterator<T> iterator, CharSequence conjunction, String prefix, String suffix) {
/*  261 */     return StrJoiner.of(conjunction, prefix, suffix)
/*      */       
/*  263 */       .setWrapElement(true)
/*  264 */       .append(iterator)
/*  265 */       .toString();
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
/*      */   public static <T> String join(Iterator<T> iterator, CharSequence conjunction, Function<T, ? extends CharSequence> func) {
/*  280 */     if (null == iterator) {
/*  281 */       return null;
/*      */     }
/*      */     
/*  284 */     return StrJoiner.of(conjunction).append(iterator, func).toString();
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
/*      */   public static <K, V> HashMap<K, V> toMap(Iterable<Map.Entry<K, V>> entryIter) {
/*  296 */     HashMap<K, V> map = new HashMap<>();
/*  297 */     if (isNotEmpty(entryIter)) {
/*  298 */       for (Map.Entry<K, V> entry : entryIter) {
/*  299 */         map.put(entry.getKey(), entry.getValue());
/*      */       }
/*      */     }
/*  302 */     return map;
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
/*      */   public static <K, V> Map<K, V> toMap(Iterable<K> keys, Iterable<V> values) {
/*  318 */     return toMap(keys, values, false);
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
/*      */   public static <K, V> Map<K, V> toMap(Iterable<K> keys, Iterable<V> values, boolean isOrder) {
/*  335 */     return toMap((null == keys) ? null : keys.iterator(), (null == values) ? null : values.iterator(), isOrder);
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
/*      */   public static <K, V> Map<K, V> toMap(Iterator<K> keys, Iterator<V> values) {
/*  351 */     return toMap(keys, values, false);
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
/*      */   public static <K, V> Map<K, V> toMap(Iterator<K> keys, Iterator<V> values, boolean isOrder) {
/*  368 */     Map<K, V> resultMap = MapUtil.newHashMap(isOrder);
/*  369 */     if (isNotEmpty(keys)) {
/*  370 */       while (keys.hasNext()) {
/*  371 */         resultMap.put(keys.next(), (null != values && values.hasNext()) ? values.next() : null);
/*      */       }
/*      */     }
/*  374 */     return resultMap;
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
/*      */   public static <K, V> Map<K, List<V>> toListMap(Iterable<V> iterable, Function<V, K> keyMapper) {
/*  388 */     return toListMap(iterable, keyMapper, v -> v);
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
/*      */   public static <T, K, V> Map<K, List<V>> toListMap(Iterable<T> iterable, Function<T, K> keyMapper, Function<T, V> valueMapper) {
/*  404 */     return toListMap(MapUtil.newHashMap(), iterable, keyMapper, valueMapper);
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
/*      */   public static <T, K, V> Map<K, List<V>> toListMap(Map<K, List<V>> resultMap, Iterable<T> iterable, Function<T, K> keyMapper, Function<T, V> valueMapper) {
/*  421 */     if (null == resultMap) {
/*  422 */       resultMap = MapUtil.newHashMap();
/*      */     }
/*  424 */     if (ObjectUtil.isNull(iterable)) {
/*  425 */       return resultMap;
/*      */     }
/*      */     
/*  428 */     for (T value : iterable) {
/*  429 */       ((List)resultMap.computeIfAbsent(keyMapper.apply(value), k -> new ArrayList())).add(valueMapper.apply(value));
/*      */     }
/*      */     
/*  432 */     return resultMap;
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
/*      */   public static <K, V> Map<K, V> toMap(Iterable<V> iterable, Function<V, K> keyMapper) {
/*  446 */     return toMap(iterable, keyMapper, v -> v);
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
/*      */   public static <T, K, V> Map<K, V> toMap(Iterable<T> iterable, Function<T, K> keyMapper, Function<T, V> valueMapper) {
/*  462 */     return toMap(MapUtil.newHashMap(), iterable, keyMapper, valueMapper);
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
/*      */   public static <T, K, V> Map<K, V> toMap(Map<K, V> resultMap, Iterable<T> iterable, Function<T, K> keyMapper, Function<T, V> valueMapper) {
/*  479 */     if (null == resultMap) {
/*  480 */       resultMap = MapUtil.newHashMap();
/*      */     }
/*  482 */     if (ObjectUtil.isNull(iterable)) {
/*  483 */       return resultMap;
/*      */     }
/*      */     
/*  486 */     for (T value : iterable) {
/*  487 */       resultMap.put(keyMapper.apply(value), valueMapper.apply(value));
/*      */     }
/*      */     
/*  490 */     return resultMap;
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
/*      */   public static <E> List<E> toList(Iterable<E> iter) {
/*  503 */     if (null == iter) {
/*  504 */       return null;
/*      */     }
/*  506 */     return toList(iter.iterator());
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
/*      */   public static <E> List<E> toList(Iterator<E> iter) {
/*  519 */     return ListUtil.toList(iter);
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
/*      */   public static <E> Iterator<E> asIterator(Enumeration<E> e) {
/*  532 */     return new EnumerationIter<>(e);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <E> Iterable<E> asIterable(Iterator<E> iter) {
/*  543 */     return () -> iter;
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
/*      */   public static <E> E get(Iterator<E> iterator, int index) throws IndexOutOfBoundsException {
/*  556 */     if (null == iterator) {
/*  557 */       return null;
/*      */     }
/*  559 */     Assert.isTrue((index >= 0), "[index] must be >= 0", new Object[0]);
/*  560 */     while (iterator.hasNext()) {
/*  561 */       index--;
/*  562 */       if (-1 == index) {
/*  563 */         return iterator.next();
/*      */       }
/*  565 */       iterator.next();
/*      */     } 
/*  567 */     return null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <T> T getFirst(Iterable<T> iterable) {
/*  578 */     return getFirst(getIter(iterable));
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
/*      */   public static <T> T getFirstNoneNull(Iterable<T> iterable) {
/*  590 */     if (null == iterable) {
/*  591 */       return null;
/*      */     }
/*  593 */     return getFirstNoneNull(iterable.iterator());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <T> T getFirst(Iterator<T> iterator) {
/*  604 */     return get(iterator, 0);
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
/*      */   public static <T> T getFirstNoneNull(Iterator<T> iterator) {
/*  616 */     return firstMatch(iterator, Objects::nonNull);
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
/*      */   public static <T> T firstMatch(Iterator<T> iterator, Matcher<T> matcher) {
/*  629 */     Assert.notNull(matcher, "Matcher must be not null !", new Object[0]);
/*  630 */     if (null != iterator) {
/*  631 */       while (iterator.hasNext()) {
/*  632 */         T next = iterator.next();
/*  633 */         if (matcher.match(next)) {
/*  634 */           return next;
/*      */         }
/*      */       } 
/*      */     }
/*  638 */     return null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Class<?> getElementType(Iterable<?> iterable) {
/*  649 */     return getElementType(getIter(iterable));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Class<?> getElementType(Iterator<?> iterator) {
/*  660 */     if (null == iterator) {
/*  661 */       return null;
/*      */     }
/*  663 */     Object ele = getFirstNoneNull(iterator);
/*  664 */     return (null == ele) ? null : ele.getClass();
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
/*      */   public static <T> List<T> edit(Iterable<T> iter, Editor<T> editor) {
/*  683 */     List<T> result = new ArrayList<>();
/*  684 */     if (null == iter) {
/*  685 */       return result;
/*      */     }
/*      */ 
/*      */     
/*  689 */     for (T t : iter) {
/*  690 */       T modified = (null == editor) ? t : (T)editor.edit(t);
/*  691 */       if (null != modified) {
/*  692 */         result.add(t);
/*      */       }
/*      */     } 
/*  695 */     return result;
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
/*      */   public static <T extends Iterable<E>, E> T filter(T iter, Filter<E> filter) {
/*  714 */     if (null == iter) {
/*  715 */       return null;
/*      */     }
/*      */     
/*  718 */     filter(iter.iterator(), filter);
/*      */     
/*  720 */     return iter;
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
/*      */   public static <E> Iterator<E> filter(Iterator<E> iter, Filter<E> filter) {
/*  738 */     if (null == iter || null == filter) {
/*  739 */       return iter;
/*      */     }
/*      */     
/*  742 */     while (iter.hasNext()) {
/*  743 */       if (false == filter.accept(iter.next())) {
/*  744 */         iter.remove();
/*      */       }
/*      */     } 
/*  747 */     return iter;
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
/*      */   public static <E> List<E> filterToList(Iterator<E> iter, Filter<E> filter) {
/*  760 */     return toList(filtered(iter, filter));
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
/*      */   public static <E> FilterIter<E> filtered(Iterator<? extends E> iterator, Filter<? super E> filter) {
/*  773 */     return new FilterIter<>(iterator, filter);
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
/*      */   public static <K, V> Map<K, V> toMap(Iterator<V> iterator, Map<K, V> map, Func1<V, K> keyFunc) {
/*  789 */     return toMap(iterator, map, keyFunc, value -> value);
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
/*      */   public static <K, V, E> Map<K, V> toMap(Iterator<E> iterator, Map<K, V> map, Func1<E, K> keyFunc, Func1<E, V> valueFunc) {
/*  807 */     if (null == iterator) {
/*  808 */       return map;
/*      */     }
/*      */     
/*  811 */     if (null == map) {
/*  812 */       map = MapUtil.newHashMap(true);
/*      */     }
/*      */ 
/*      */     
/*  816 */     while (iterator.hasNext()) {
/*  817 */       E element = iterator.next();
/*      */       try {
/*  819 */         map.put((K)keyFunc.call(element), (V)valueFunc.call(element));
/*  820 */       } catch (Exception e) {
/*  821 */         throw new UtilException(e);
/*      */       } 
/*      */     } 
/*  824 */     return map;
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
/*      */   public static <T> Iterator<T> empty() {
/*  836 */     return Collections.emptyIterator();
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
/*      */   public static <F, T> Iterator<T> trans(Iterator<F> iterator, Function<? super F, ? extends T> function) {
/*  850 */     return new TransIter<>(iterator, function);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static int size(Iterable<?> iterable) {
/*  861 */     if (null == iterable) {
/*  862 */       return 0;
/*      */     }
/*      */     
/*  865 */     if (iterable instanceof Collection) {
/*  866 */       return ((Collection)iterable).size();
/*      */     }
/*  868 */     return size(iterable.iterator());
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
/*      */   public static int size(Iterator<?> iterator) {
/*  880 */     int size = 0;
/*  881 */     if (iterator != null) {
/*  882 */       while (iterator.hasNext()) {
/*  883 */         iterator.next();
/*  884 */         size++;
/*      */       } 
/*      */     }
/*  887 */     return size;
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
/*      */   public static boolean isEqualList(Iterable<?> list1, Iterable<?> list2) {
/*  904 */     if (list1 == list2) {
/*  905 */       return true;
/*      */     }
/*      */     
/*  908 */     Iterator<?> it1 = list1.iterator();
/*  909 */     Iterator<?> it2 = list2.iterator();
/*      */ 
/*      */     
/*  912 */     while (it1.hasNext() && it2.hasNext()) {
/*  913 */       Object obj1 = it1.next();
/*  914 */       Object obj2 = it2.next();
/*      */       
/*  916 */       if (false == Objects.equals(obj1, obj2)) {
/*  917 */         return false;
/*      */       }
/*      */     } 
/*      */ 
/*      */     
/*  922 */     return (false == ((it1.hasNext() || it2.hasNext()) ? true : false));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void clear(Iterator<?> iterator) {
/*  932 */     if (null != iterator) {
/*  933 */       while (iterator.hasNext()) {
/*  934 */         iterator.next();
/*  935 */         iterator.remove();
/*      */       } 
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
/*      */   public static <E> void forEach(Iterator<E> iterator, Consumer<? super E> consumer) {
/*  950 */     if (iterator != null) {
/*  951 */       while (iterator.hasNext()) {
/*  952 */         E element = iterator.next();
/*  953 */         if (null != consumer) {
/*  954 */           consumer.accept(element);
/*      */         }
/*      */       } 
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
/*      */   public static <E> String toStr(Iterator<E> iterator) {
/*  969 */     return toStr(iterator, ObjectUtil::toString);
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
/*      */   public static <E> String toStr(Iterator<E> iterator, Function<? super E, String> transFunc) {
/*  982 */     return toStr(iterator, transFunc, ", ", "[", "]");
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
/*      */   public static <E> String toStr(Iterator<E> iterator, Function<? super E, String> transFunc, String delimiter, String prefix, String suffix) {
/* 1002 */     StrJoiner strJoiner = StrJoiner.of(delimiter, prefix, suffix);
/* 1003 */     strJoiner.append(iterator, transFunc);
/* 1004 */     return strJoiner.toString();
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
/*      */   public static Iterator<?> getIter(Object obj) {
/* 1027 */     if (obj == null)
/* 1028 */       return null; 
/* 1029 */     if (obj instanceof Iterator)
/* 1030 */       return (Iterator)obj; 
/* 1031 */     if (obj instanceof Iterable)
/* 1032 */       return ((Iterable)obj).iterator(); 
/* 1033 */     if (ArrayUtil.isArray(obj))
/* 1034 */       return new ArrayIter(obj); 
/* 1035 */     if (obj instanceof Enumeration)
/* 1036 */       return new EnumerationIter((Enumeration)obj); 
/* 1037 */     if (obj instanceof Map)
/* 1038 */       return ((Map)obj).entrySet().iterator(); 
/* 1039 */     if (obj instanceof NodeList)
/* 1040 */       return new NodeListIter((NodeList)obj); 
/* 1041 */     if (obj instanceof Node)
/*      */     {
/* 1043 */       return new NodeListIter(((Node)obj).getChildNodes()); } 
/* 1044 */     if (obj instanceof Dictionary) {
/* 1045 */       return new EnumerationIter(((Dictionary)obj).elements());
/*      */     }
/*      */ 
/*      */     
/*      */     try {
/* 1050 */       Object iterator = ReflectUtil.invoke(obj, "iterator", new Object[0]);
/* 1051 */       if (iterator instanceof Iterator) {
/* 1052 */         return (Iterator)iterator;
/*      */       }
/* 1054 */     } catch (RuntimeException runtimeException) {}
/*      */ 
/*      */     
/* 1057 */     return new ArrayIter(new Object[] { obj });
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\collection\IterUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */