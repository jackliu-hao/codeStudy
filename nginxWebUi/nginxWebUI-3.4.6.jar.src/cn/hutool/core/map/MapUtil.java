/*      */ package cn.hutool.core.map;
/*      */ 
/*      */ import cn.hutool.core.collection.CollUtil;
/*      */ import cn.hutool.core.convert.Convert;
/*      */ import cn.hutool.core.lang.Editor;
/*      */ import cn.hutool.core.lang.Filter;
/*      */ import cn.hutool.core.lang.Pair;
/*      */ import cn.hutool.core.lang.TypeReference;
/*      */ import cn.hutool.core.stream.CollectorUtil;
/*      */ import cn.hutool.core.util.ArrayUtil;
/*      */ import cn.hutool.core.util.ReflectUtil;
/*      */ import cn.hutool.core.util.StrUtil;
/*      */ import java.lang.reflect.Type;
/*      */ import java.util.AbstractMap;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Collections;
/*      */ import java.util.Comparator;
/*      */ import java.util.Date;
/*      */ import java.util.HashMap;
/*      */ import java.util.IdentityHashMap;
/*      */ import java.util.Iterator;
/*      */ import java.util.LinkedHashMap;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.NavigableMap;
/*      */ import java.util.Set;
/*      */ import java.util.SortedMap;
/*      */ import java.util.TreeMap;
/*      */ import java.util.concurrent.ConcurrentHashMap;
/*      */ import java.util.function.BiFunction;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class MapUtil
/*      */ {
/*      */   public static final int DEFAULT_INITIAL_CAPACITY = 16;
/*      */   public static final float DEFAULT_LOAD_FACTOR = 0.75F;
/*      */   
/*      */   public static boolean isEmpty(Map<?, ?> map) {
/*   57 */     return (null == map || map.isEmpty());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean isNotEmpty(Map<?, ?> map) {
/*   67 */     return (null != map && false == map.isEmpty());
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
/*      */   public static <K, V> Map<K, V> emptyIfNull(Map<K, V> set) {
/*   81 */     return (null == set) ? Collections.<K, V>emptyMap() : set;
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
/*      */   public static <T extends Map<K, V>, K, V> T defaultIfEmpty(T map, T defaultMap) {
/*   96 */     return isEmpty((Map<?, ?>)map) ? defaultMap : map;
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
/*      */   public static <K, V> HashMap<K, V> newHashMap() {
/*  109 */     return new HashMap<>();
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
/*      */   public static <K, V> HashMap<K, V> newHashMap(int size, boolean isLinked) {
/*  123 */     int initialCapacity = (int)(size / 0.75F) + 1;
/*  124 */     return isLinked ? new LinkedHashMap<>(initialCapacity) : new HashMap<>(initialCapacity);
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
/*      */   public static <K, V> HashMap<K, V> newHashMap(int size) {
/*  136 */     return newHashMap(size, false);
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
/*      */   public static <K, V> HashMap<K, V> newHashMap(boolean isLinked) {
/*  148 */     return newHashMap(16, isLinked);
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
/*      */   public static <K, V> TreeMap<K, V> newTreeMap(Comparator<? super K> comparator) {
/*  161 */     return new TreeMap<>(comparator);
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
/*      */   public static <K, V> TreeMap<K, V> newTreeMap(Map<K, V> map, Comparator<? super K> comparator) {
/*  175 */     TreeMap<K, V> treeMap = new TreeMap<>(comparator);
/*  176 */     if (false == isEmpty(map)) {
/*  177 */       treeMap.putAll(map);
/*      */     }
/*  179 */     return treeMap;
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
/*      */   public static <K, V> Map<K, V> newIdentityMap(int size) {
/*  192 */     return new IdentityHashMap<>(size);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <K, V> ConcurrentHashMap<K, V> newConcurrentHashMap() {
/*  203 */     return new ConcurrentHashMap<>(16);
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
/*      */   public static <K, V> ConcurrentHashMap<K, V> newConcurrentHashMap(int size) {
/*  215 */     int initCapacity = (size <= 0) ? 16 : size;
/*  216 */     return new ConcurrentHashMap<>(initCapacity);
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
/*      */   public static <K, V> ConcurrentHashMap<K, V> newConcurrentHashMap(Map<K, V> map) {
/*  228 */     if (isEmpty(map)) {
/*  229 */       return new ConcurrentHashMap<>(16);
/*      */     }
/*  231 */     return new ConcurrentHashMap<>(map);
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
/*      */   public static <K, V> Map<K, V> createMap(Class<?> mapType) {
/*  245 */     if (mapType.isAssignableFrom(AbstractMap.class)) {
/*  246 */       return new HashMap<>();
/*      */     }
/*  248 */     return (Map<K, V>)ReflectUtil.newInstance(mapType, new Object[0]);
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
/*      */   public static <K, V> HashMap<K, V> of(K key, V value) {
/*  264 */     return of(key, value, false);
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
/*      */   public static <K, V> HashMap<K, V> of(K key, V value, boolean isOrder) {
/*  278 */     HashMap<K, V> map = newHashMap(isOrder);
/*  279 */     map.put(key, value);
/*  280 */     return map;
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
/*      */   @SafeVarargs
/*      */   @Deprecated
/*      */   public static <K, V> Map<K, V> of(Pair<K, V>... pairs) {
/*  296 */     Map<K, V> map = new HashMap<>();
/*  297 */     for (Pair<K, V> pair : pairs) {
/*  298 */       map.put((K)pair.getKey(), (V)pair.getValue());
/*      */     }
/*  300 */     return map;
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
/*      */   @SafeVarargs
/*      */   public static <K, V> Map<K, V> ofEntries(Map.Entry<K, V>... entries) {
/*  315 */     Map<K, V> map = new HashMap<>();
/*  316 */     for (Map.Entry<K, V> pair : entries) {
/*  317 */       map.put(pair.getKey(), pair.getValue());
/*      */     }
/*  319 */     return map;
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
/*      */   public static HashMap<Object, Object> of(Object[] array) {
/*  348 */     if (array == null) {
/*  349 */       return null;
/*      */     }
/*  351 */     HashMap<Object, Object> map = new HashMap<>((int)(array.length * 1.5D));
/*  352 */     for (int i = 0; i < array.length; i++) {
/*  353 */       Object object = array[i];
/*  354 */       if (object instanceof Map.Entry) {
/*  355 */         Map.Entry entry = (Map.Entry)object;
/*  356 */         map.put(entry.getKey(), entry.getValue());
/*  357 */       } else if (object instanceof Object[]) {
/*  358 */         Object[] entry = (Object[])object;
/*  359 */         if (entry.length > 1) {
/*  360 */           map.put(entry[0], entry[1]);
/*      */         }
/*  362 */       } else if (object instanceof Iterable) {
/*  363 */         Iterator iter = ((Iterable)object).iterator();
/*  364 */         if (iter.hasNext()) {
/*  365 */           Object key = iter.next();
/*  366 */           if (iter.hasNext()) {
/*  367 */             Object value = iter.next();
/*  368 */             map.put(key, value);
/*      */           } 
/*      */         } 
/*  371 */       } else if (object instanceof Iterator) {
/*  372 */         Iterator iter = (Iterator)object;
/*  373 */         if (iter.hasNext()) {
/*  374 */           Object key = iter.next();
/*  375 */           if (iter.hasNext()) {
/*  376 */             Object value = iter.next();
/*  377 */             map.put(key, value);
/*      */           } 
/*      */         } 
/*      */       } else {
/*  381 */         throw new IllegalArgumentException(StrUtil.format("Array element {}, '{}', is not type of Map.Entry or Array or Iterable or Iterator", new Object[] { Integer.valueOf(i), object }));
/*      */       } 
/*      */     } 
/*  384 */     return map;
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <K, V> Map<K, List<V>> toListMap(Iterable<? extends Map<K, V>> mapList) {
/*  418 */     HashMap<K, List<V>> resultMap = new HashMap<>();
/*  419 */     if (CollUtil.isEmpty(mapList)) {
/*  420 */       return resultMap;
/*      */     }
/*      */ 
/*      */     
/*  424 */     for (Map<K, V> map : mapList) {
/*  425 */       Set<Map.Entry<K, V>> entrySet = map.entrySet();
/*      */ 
/*      */       
/*  428 */       for (Map.Entry<K, V> entry : entrySet) {
/*  429 */         K key = entry.getKey();
/*  430 */         List<V> valueList = resultMap.get(key);
/*  431 */         if (null == valueList) {
/*  432 */           valueList = CollUtil.newArrayList(new Object[] { entry.getValue() });
/*  433 */           resultMap.put(key, valueList); continue;
/*      */         } 
/*  435 */         valueList.add(entry.getValue());
/*      */       } 
/*      */     } 
/*      */ 
/*      */     
/*  440 */     return resultMap;
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
/*      */ 
/*      */ 
/*      */   
/*      */   public static <K, V> List<Map<K, V>> toMapList(Map<K, ? extends Iterable<V>> listMap) {
/*      */     boolean isEnd;
/*  473 */     List<Map<K, V>> resultList = new ArrayList<>();
/*  474 */     if (isEmpty(listMap)) {
/*  475 */       return resultList;
/*      */     }
/*      */ 
/*      */     
/*  479 */     int index = 0;
/*      */     
/*      */     do {
/*  482 */       isEnd = true;
/*  483 */       Map<K, V> map = new HashMap<>();
/*      */ 
/*      */       
/*  486 */       for (Map.Entry<K, ? extends Iterable<V>> entry : listMap.entrySet()) {
/*  487 */         List<V> vList = CollUtil.newArrayList(entry.getValue());
/*  488 */         int vListSize = vList.size();
/*  489 */         if (index < vListSize) {
/*  490 */           map.put(entry.getKey(), vList.get(index));
/*  491 */           if (index != vListSize - 1)
/*      */           {
/*  493 */             isEnd = false;
/*      */           }
/*      */         } 
/*      */       } 
/*  497 */       if (false == map.isEmpty()) {
/*  498 */         resultList.add(map);
/*      */       }
/*  500 */       index++;
/*  501 */     } while (false == isEnd);
/*      */     
/*  503 */     return resultList;
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
/*      */   public static <K, V> Map<K, V> toCamelCaseMap(Map<K, V> map) {
/*  517 */     return (map instanceof LinkedHashMap) ? new CamelCaseLinkedMap<>(map) : new CamelCaseMap<>(map);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Object[][] toObjectArray(Map<?, ?> map) {
/*  528 */     if (map == null) {
/*  529 */       return (Object[][])null;
/*      */     }
/*  531 */     Object[][] result = new Object[map.size()][2];
/*  532 */     if (map.isEmpty()) {
/*  533 */       return result;
/*      */     }
/*  535 */     int index = 0;
/*  536 */     for (Map.Entry<?, ?> entry : map.entrySet()) {
/*  537 */       result[index][0] = entry.getKey();
/*  538 */       result[index][1] = entry.getValue();
/*  539 */       index++;
/*      */     } 
/*  541 */     return result;
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
/*      */   public static <K, V> String join(Map<K, V> map, String separator, String keyValueSeparator, String... otherParams) {
/*  559 */     return join(map, separator, keyValueSeparator, false, otherParams);
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
/*      */   public static String sortJoin(Map<?, ?> params, String separator, String keyValueSeparator, boolean isIgnoreNull, String... otherParams) {
/*  575 */     return join(sort(params), separator, keyValueSeparator, isIgnoreNull, otherParams);
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
/*      */   public static <K, V> String joinIgnoreNull(Map<K, V> map, String separator, String keyValueSeparator, String... otherParams) {
/*  591 */     return join(map, separator, keyValueSeparator, true, otherParams);
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
/*      */   public static <K, V> String join(Map<K, V> map, String separator, String keyValueSeparator, boolean isIgnoreNull, String... otherParams) {
/*  608 */     StringBuilder strBuilder = StrUtil.builder();
/*  609 */     boolean isFirst = true;
/*  610 */     if (isNotEmpty(map)) {
/*  611 */       for (Map.Entry<K, V> entry : map.entrySet()) {
/*  612 */         if (false == isIgnoreNull || (entry.getKey() != null && entry.getValue() != null)) {
/*  613 */           if (isFirst) {
/*  614 */             isFirst = false;
/*      */           } else {
/*  616 */             strBuilder.append(separator);
/*      */           } 
/*  618 */           strBuilder.append(Convert.toStr(entry.getKey())).append(keyValueSeparator).append(Convert.toStr(entry.getValue()));
/*      */         } 
/*      */       } 
/*      */     }
/*      */     
/*  623 */     if (ArrayUtil.isNotEmpty((Object[])otherParams)) {
/*  624 */       for (String otherParam : otherParams) {
/*  625 */         strBuilder.append(otherParam);
/*      */       }
/*      */     }
/*  628 */     return strBuilder.toString();
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
/*      */   public static <K, V> Map<K, V> edit(Map<K, V> map, Editor<Map.Entry<K, V>> editor) {
/*  650 */     if (null == map || null == editor) {
/*  651 */       return map;
/*      */     }
/*      */     
/*  654 */     Map<K, V> map2 = (Map<K, V>)ReflectUtil.newInstanceIfPossible(map.getClass());
/*  655 */     if (null == map2) {
/*  656 */       map2 = new HashMap<>(map.size(), 1.0F);
/*      */     }
/*  658 */     if (isEmpty(map)) {
/*  659 */       return map2;
/*      */     }
/*      */ 
/*      */     
/*  663 */     for (Map.Entry<K, V> entry : map.entrySet()) {
/*  664 */       Map.Entry<K, V> modified = (Map.Entry<K, V>)editor.edit(entry);
/*  665 */       if (null != modified) {
/*  666 */         map2.put(modified.getKey(), modified.getValue());
/*      */       }
/*      */     } 
/*  669 */     return map2;
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
/*      */   public static <K, V> Map<K, V> filter(Map<K, V> map, Filter<Map.Entry<K, V>> filter) {
/*  688 */     if (null == map || null == filter) {
/*  689 */       return map;
/*      */     }
/*  691 */     return edit(map, t -> filter.accept(t) ? t : null);
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
/*      */   public static <K, V, R> Map<K, R> map(Map<K, V> map, BiFunction<K, V, R> biFunction) {
/*  708 */     if (null == map || null == biFunction) {
/*  709 */       return newHashMap();
/*      */     }
/*  711 */     return (Map<K, R>)map.entrySet().stream().collect(CollectorUtil.toMap(Map.Entry::getKey, m -> biFunction.apply(m.getKey(), m.getValue()), (l, r) -> l));
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
/*      */   public static <K, V> Map<K, V> filter(Map<K, V> map, K... keys) {
/*  726 */     if (null == map || null == keys) {
/*  727 */       return map;
/*      */     }
/*      */     
/*  730 */     Map<K, V> map2 = (Map<K, V>)ReflectUtil.newInstanceIfPossible(map.getClass());
/*  731 */     if (null == map2) {
/*  732 */       map2 = new HashMap<>(map.size(), 1.0F);
/*      */     }
/*  734 */     if (isEmpty(map)) {
/*  735 */       return map2;
/*      */     }
/*      */     
/*  738 */     for (K key : keys) {
/*  739 */       if (map.containsKey(key)) {
/*  740 */         map2.put(key, map.get(key));
/*      */       }
/*      */     } 
/*  743 */     return map2;
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
/*      */   public static <T> Map<T, T> reverse(Map<T, T> map) {
/*  758 */     return edit(map, t -> new Map.Entry()
/*      */         {
/*      */           public T getKey()
/*      */           {
/*  762 */             return (T)t.getValue();
/*      */           }
/*      */ 
/*      */           
/*      */           public T getValue() {
/*  767 */             return (T)t.getKey();
/*      */           }
/*      */ 
/*      */           
/*      */           public T setValue(Object value) {
/*  772 */             throw new UnsupportedOperationException("Unsupported setValue method !");
/*      */           }
/*      */         });
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
/*      */   public static <K, V> Map<V, K> inverse(Map<K, V> map) {
/*  789 */     Map<V, K> result = createMap(map.getClass());
/*  790 */     map.forEach((key, value) -> result.put(value, key));
/*  791 */     return result;
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
/*      */   public static <K, V> TreeMap<K, V> sort(Map<K, V> map) {
/*  805 */     return sort(map, null);
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
/*      */   public static <K, V> TreeMap<K, V> sort(Map<K, V> map, Comparator<? super K> comparator) {
/*  820 */     if (null == map) {
/*  821 */       return null;
/*      */     }
/*      */     
/*  824 */     if (map instanceof TreeMap) {
/*      */       
/*  826 */       TreeMap<K, V> result = (TreeMap<K, V>)map;
/*  827 */       if (null == comparator || comparator.equals(result.comparator())) {
/*  828 */         return result;
/*      */       }
/*      */     } 
/*      */     
/*  832 */     return newTreeMap(map, comparator);
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
/*      */   public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map, boolean isDesc) {
/*  846 */     Map<K, V> result = new LinkedHashMap<>();
/*  847 */     Comparator<Map.Entry<K, V>> entryComparator = Map.Entry.comparingByValue();
/*  848 */     if (isDesc) {
/*  849 */       entryComparator = entryComparator.reversed();
/*      */     }
/*  851 */     map.entrySet().stream().sorted(entryComparator).forEachOrdered(e -> (Comparable)result.put(e.getKey(), e.getValue()));
/*  852 */     return result;
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
/*      */   public static MapProxy createProxy(Map<?, ?> map) {
/*  864 */     return MapProxy.create(map);
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
/*      */   public static <K, V> MapWrapper<K, V> wrap(Map<K, V> map) {
/*  878 */     return new MapWrapper<>(map);
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
/*      */   public static <K, V> Map<K, V> unmodifiable(Map<K, V> map) {
/*  891 */     return Collections.unmodifiableMap(map);
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
/*      */   public static <K, V> MapBuilder<K, V> builder() {
/*  904 */     return builder(new HashMap<>());
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
/*      */   public static <K, V> MapBuilder<K, V> builder(Map<K, V> map) {
/*  916 */     return new MapBuilder<>(map);
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
/*      */   public static <K, V> MapBuilder<K, V> builder(K k, V v) {
/*  929 */     return builder(new HashMap<>()).put(k, v);
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
/*      */   public static <K, V> Map<K, V> getAny(Map<K, V> map, K... keys) {
/*  944 */     return filter(map, entry -> ArrayUtil.contains(keys, entry.getKey()));
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
/*      */   public static <K, V> Map<K, V> removeAny(Map<K, V> map, K... keys) {
/*  959 */     for (K key : keys) {
/*  960 */       map.remove(key);
/*      */     }
/*  962 */     return map;
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
/*      */   public static String getStr(Map<?, ?> map, Object key) {
/*  974 */     return get(map, key, String.class);
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
/*      */   public static String getStr(Map<?, ?> map, Object key, String defaultValue) {
/*  987 */     return get(map, key, String.class, defaultValue);
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
/*      */   public static Integer getInt(Map<?, ?> map, Object key) {
/*  999 */     return get(map, key, Integer.class);
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
/*      */   public static Integer getInt(Map<?, ?> map, Object key, Integer defaultValue) {
/* 1012 */     return get(map, key, Integer.class, defaultValue);
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
/*      */   public static Double getDouble(Map<?, ?> map, Object key) {
/* 1024 */     return get(map, key, Double.class);
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
/*      */   public static Double getDouble(Map<?, ?> map, Object key, Double defaultValue) {
/* 1037 */     return get(map, key, Double.class, defaultValue);
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
/*      */   public static Float getFloat(Map<?, ?> map, Object key) {
/* 1049 */     return get(map, key, Float.class);
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
/*      */   public static Float getFloat(Map<?, ?> map, Object key, Float defaultValue) {
/* 1062 */     return get(map, key, Float.class, defaultValue);
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
/*      */   public static Short getShort(Map<?, ?> map, Object key) {
/* 1074 */     return get(map, key, Short.class);
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
/*      */   public static Short getShort(Map<?, ?> map, Object key, Short defaultValue) {
/* 1087 */     return get(map, key, Short.class, defaultValue);
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
/*      */   public static Boolean getBool(Map<?, ?> map, Object key) {
/* 1099 */     return get(map, key, Boolean.class);
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
/*      */   public static Boolean getBool(Map<?, ?> map, Object key, Boolean defaultValue) {
/* 1112 */     return get(map, key, Boolean.class, defaultValue);
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
/*      */   public static Character getChar(Map<?, ?> map, Object key) {
/* 1124 */     return get(map, key, Character.class);
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
/*      */   public static Character getChar(Map<?, ?> map, Object key, Character defaultValue) {
/* 1137 */     return get(map, key, Character.class, defaultValue);
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
/*      */   public static Long getLong(Map<?, ?> map, Object key) {
/* 1149 */     return get(map, key, Long.class);
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
/*      */   public static Long getLong(Map<?, ?> map, Object key, Long defaultValue) {
/* 1162 */     return get(map, key, Long.class, defaultValue);
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
/*      */   public static Date getDate(Map<?, ?> map, Object key) {
/* 1174 */     return get(map, key, Date.class);
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
/*      */   public static Date getDate(Map<?, ?> map, Object key, Date defaultValue) {
/* 1187 */     return get(map, key, Date.class, defaultValue);
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
/*      */   public static <T> T get(Map<?, ?> map, Object key, Class<T> type) {
/* 1201 */     return get(map, key, type, (T)null);
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
/*      */   public static <T> T get(Map<?, ?> map, Object key, Class<T> type, T defaultValue) {
/* 1216 */     return (null == map) ? defaultValue : (T)Convert.convert(type, map.get(key), defaultValue);
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
/*      */   public static <T> T getQuietly(Map<?, ?> map, Object key, Class<T> type, T defaultValue) {
/* 1231 */     return (null == map) ? defaultValue : (T)Convert.convertQuietly(type, map.get(key), defaultValue);
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
/*      */   public static <T> T get(Map<?, ?> map, Object key, TypeReference<T> type) {
/* 1245 */     return get(map, key, type, (T)null);
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
/*      */   public static <T> T get(Map<?, ?> map, Object key, TypeReference<T> type, T defaultValue) {
/* 1260 */     return (null == map) ? defaultValue : (T)Convert.convert((Type)type, map.get(key), defaultValue);
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
/*      */   public static <T> T getQuietly(Map<?, ?> map, Object key, TypeReference<T> type, T defaultValue) {
/* 1275 */     return (null == map) ? defaultValue : (T)Convert.convertQuietly((Type)type, map.get(key), defaultValue);
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
/*      */   public static <K, V> Map<K, V> renameKey(Map<K, V> map, K oldKey, K newKey) {
/* 1293 */     if (isNotEmpty(map) && map.containsKey(oldKey)) {
/* 1294 */       if (map.containsKey(newKey)) {
/* 1295 */         throw new IllegalArgumentException(StrUtil.format("The key '{}' exist !", new Object[] { newKey }));
/*      */       }
/* 1297 */       map.put(newKey, map.remove(oldKey));
/*      */     } 
/* 1299 */     return map;
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
/*      */   public static <K, V> Map<K, V> removeNullValue(Map<K, V> map) {
/* 1313 */     if (isEmpty(map)) {
/* 1314 */       return map;
/*      */     }
/*      */     
/* 1317 */     Iterator<Map.Entry<K, V>> iter = map.entrySet().iterator();
/*      */     
/* 1319 */     while (iter.hasNext()) {
/* 1320 */       Map.Entry<K, V> entry = iter.next();
/* 1321 */       if (null == entry.getValue()) {
/* 1322 */         iter.remove();
/*      */       }
/*      */     } 
/*      */     
/* 1326 */     return map;
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
/*      */   public static <K, V> Map<K, V> empty() {
/* 1339 */     return Collections.emptyMap();
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
/*      */   public static <K, V, T extends Map<K, V>> T empty(Class<?> mapClass) {
/* 1360 */     if (null == mapClass) {
/* 1361 */       return (T)Collections.emptyMap();
/*      */     }
/* 1363 */     if (NavigableMap.class == mapClass)
/* 1364 */       return (T)Collections.emptyNavigableMap(); 
/* 1365 */     if (SortedMap.class == mapClass)
/* 1366 */       return (T)Collections.emptySortedMap(); 
/* 1367 */     if (Map.class == mapClass) {
/* 1368 */       return (T)Collections.emptyMap();
/*      */     }
/*      */ 
/*      */     
/* 1372 */     throw new IllegalArgumentException(StrUtil.format("[{}] is not support to get empty!", new Object[] { mapClass }));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void clear(Map<?, ?>... maps) {
/* 1381 */     for (Map<?, ?> map : maps) {
/* 1382 */       if (isNotEmpty(map)) {
/* 1383 */         map.clear();
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
/*      */ 
/*      */   
/*      */   public static <K, V> ArrayList<V> valuesOfKeys(Map<K, V> map, Iterator<K> keys) {
/* 1400 */     ArrayList<V> values = new ArrayList<>();
/* 1401 */     while (keys.hasNext()) {
/* 1402 */       values.add(map.get(keys.next()));
/*      */     }
/* 1404 */     return values;
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
/*      */   public static <K, V> Map.Entry<K, V> entry(K key, V value) {
/* 1419 */     return entry(key, value, true);
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
/*      */   public static <K, V> Map.Entry<K, V> entry(K key, V value, boolean isImmutable) {
/* 1434 */     return isImmutable ? new AbstractMap.SimpleImmutableEntry<>(key, value) : new AbstractMap.SimpleEntry<>(key, value);
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\map\MapUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */