package cn.hutool.core.map;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.lang.Editor;
import cn.hutool.core.lang.Filter;
import cn.hutool.core.lang.Pair;
import cn.hutool.core.lang.TypeReference;
import cn.hutool.core.stream.CollectorUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import java.lang.reflect.Type;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;

public class MapUtil {
   public static final int DEFAULT_INITIAL_CAPACITY = 16;
   public static final float DEFAULT_LOAD_FACTOR = 0.75F;

   public static boolean isEmpty(Map<?, ?> map) {
      return null == map || map.isEmpty();
   }

   public static boolean isNotEmpty(Map<?, ?> map) {
      return null != map && !map.isEmpty();
   }

   public static <K, V> Map<K, V> emptyIfNull(Map<K, V> set) {
      return null == set ? Collections.emptyMap() : set;
   }

   public static <T extends Map<K, V>, K, V> T defaultIfEmpty(T map, T defaultMap) {
      return isEmpty(map) ? defaultMap : map;
   }

   public static <K, V> HashMap<K, V> newHashMap() {
      return new HashMap();
   }

   public static <K, V> HashMap<K, V> newHashMap(int size, boolean isLinked) {
      int initialCapacity = (int)((float)size / 0.75F) + 1;
      return (HashMap)(isLinked ? new LinkedHashMap(initialCapacity) : new HashMap(initialCapacity));
   }

   public static <K, V> HashMap<K, V> newHashMap(int size) {
      return newHashMap(size, false);
   }

   public static <K, V> HashMap<K, V> newHashMap(boolean isLinked) {
      return newHashMap(16, isLinked);
   }

   public static <K, V> TreeMap<K, V> newTreeMap(Comparator<? super K> comparator) {
      return new TreeMap(comparator);
   }

   public static <K, V> TreeMap<K, V> newTreeMap(Map<K, V> map, Comparator<? super K> comparator) {
      TreeMap<K, V> treeMap = new TreeMap(comparator);
      if (!isEmpty(map)) {
         treeMap.putAll(map);
      }

      return treeMap;
   }

   public static <K, V> Map<K, V> newIdentityMap(int size) {
      return new IdentityHashMap(size);
   }

   public static <K, V> ConcurrentHashMap<K, V> newConcurrentHashMap() {
      return new ConcurrentHashMap(16);
   }

   public static <K, V> ConcurrentHashMap<K, V> newConcurrentHashMap(int size) {
      int initCapacity = size <= 0 ? 16 : size;
      return new ConcurrentHashMap(initCapacity);
   }

   public static <K, V> ConcurrentHashMap<K, V> newConcurrentHashMap(Map<K, V> map) {
      return isEmpty(map) ? new ConcurrentHashMap(16) : new ConcurrentHashMap(map);
   }

   public static <K, V> Map<K, V> createMap(Class<?> mapType) {
      return (Map)(mapType.isAssignableFrom(AbstractMap.class) ? new HashMap() : (Map)ReflectUtil.newInstance(mapType));
   }

   public static <K, V> HashMap<K, V> of(K key, V value) {
      return of(key, value, false);
   }

   public static <K, V> HashMap<K, V> of(K key, V value, boolean isOrder) {
      HashMap<K, V> map = newHashMap(isOrder);
      map.put(key, value);
      return map;
   }

   /** @deprecated */
   @SafeVarargs
   @Deprecated
   public static <K, V> Map<K, V> of(Pair<K, V>... pairs) {
      Map<K, V> map = new HashMap();
      Pair[] var2 = pairs;
      int var3 = pairs.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Pair<K, V> pair = var2[var4];
         map.put(pair.getKey(), pair.getValue());
      }

      return map;
   }

   @SafeVarargs
   public static <K, V> Map<K, V> ofEntries(Map.Entry<K, V>... entries) {
      Map<K, V> map = new HashMap();
      Map.Entry[] var2 = entries;
      int var3 = entries.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Map.Entry<K, V> pair = var2[var4];
         map.put(pair.getKey(), pair.getValue());
      }

      return map;
   }

   public static HashMap<Object, Object> of(Object[] array) {
      if (array == null) {
         return null;
      } else {
         HashMap<Object, Object> map = new HashMap((int)((double)array.length * 1.5));

         for(int i = 0; i < array.length; ++i) {
            Object object = array[i];
            if (object instanceof Map.Entry) {
               Map.Entry entry = (Map.Entry)object;
               map.put(entry.getKey(), entry.getValue());
            } else if (object instanceof Object[]) {
               Object[] entry = (Object[])((Object[])object);
               if (entry.length > 1) {
                  map.put(entry[0], entry[1]);
               }
            } else {
               Object key;
               Object value;
               Iterator iter;
               if (object instanceof Iterable) {
                  iter = ((Iterable)object).iterator();
                  if (iter.hasNext()) {
                     key = iter.next();
                     if (iter.hasNext()) {
                        value = iter.next();
                        map.put(key, value);
                     }
                  }
               } else {
                  if (!(object instanceof Iterator)) {
                     throw new IllegalArgumentException(StrUtil.format("Array element {}, '{}', is not type of Map.Entry or Array or Iterable or Iterator", new Object[]{i, object}));
                  }

                  iter = (Iterator)object;
                  if (iter.hasNext()) {
                     key = iter.next();
                     if (iter.hasNext()) {
                        value = iter.next();
                        map.put(key, value);
                     }
                  }
               }
            }
         }

         return map;
      }
   }

   public static <K, V> Map<K, List<V>> toListMap(Iterable<? extends Map<K, V>> mapList) {
      HashMap<K, List<V>> resultMap = new HashMap();
      if (CollUtil.isEmpty(mapList)) {
         return resultMap;
      } else {
         Iterator var3 = mapList.iterator();

         while(var3.hasNext()) {
            Map<K, V> map = (Map)var3.next();
            Set<Map.Entry<K, V>> entrySet = map.entrySet();
            Iterator var7 = entrySet.iterator();

            while(var7.hasNext()) {
               Map.Entry<K, V> entry = (Map.Entry)var7.next();
               K key = entry.getKey();
               List<V> valueList = (List)resultMap.get(key);
               if (null == valueList) {
                  List<V> valueList = CollUtil.newArrayList(entry.getValue());
                  resultMap.put(key, valueList);
               } else {
                  valueList.add(entry.getValue());
               }
            }
         }

         return resultMap;
      }
   }

   public static <K, V> List<Map<K, V>> toMapList(Map<K, ? extends Iterable<V>> listMap) {
      List<Map<K, V>> resultList = new ArrayList();
      if (isEmpty(listMap)) {
         return resultList;
      } else {
         int index = 0;

         boolean isEnd;
         do {
            isEnd = true;
            Map<K, V> map = new HashMap();
            Iterator var7 = listMap.entrySet().iterator();

            while(var7.hasNext()) {
               Map.Entry<K, ? extends Iterable<V>> entry = (Map.Entry)var7.next();
               List<V> vList = CollUtil.newArrayList((Iterable)entry.getValue());
               int vListSize = vList.size();
               if (index < vListSize) {
                  map.put(entry.getKey(), vList.get(index));
                  if (index != vListSize - 1) {
                     isEnd = false;
                  }
               }
            }

            if (!map.isEmpty()) {
               resultList.add(map);
            }

            ++index;
         } while(!isEnd);

         return resultList;
      }
   }

   public static <K, V> Map<K, V> toCamelCaseMap(Map<K, V> map) {
      return (Map)(map instanceof LinkedHashMap ? new CamelCaseLinkedMap(map) : new CamelCaseMap(map));
   }

   public static Object[][] toObjectArray(Map<?, ?> map) {
      if (map == null) {
         return (Object[][])null;
      } else {
         Object[][] result = new Object[map.size()][2];
         if (map.isEmpty()) {
            return result;
         } else {
            int index = 0;

            for(Iterator var3 = map.entrySet().iterator(); var3.hasNext(); ++index) {
               Map.Entry<?, ?> entry = (Map.Entry)var3.next();
               result[index][0] = entry.getKey();
               result[index][1] = entry.getValue();
            }

            return result;
         }
      }
   }

   public static <K, V> String join(Map<K, V> map, String separator, String keyValueSeparator, String... otherParams) {
      return join(map, separator, keyValueSeparator, false, otherParams);
   }

   public static String sortJoin(Map<?, ?> params, String separator, String keyValueSeparator, boolean isIgnoreNull, String... otherParams) {
      return join(sort(params), separator, keyValueSeparator, isIgnoreNull, otherParams);
   }

   public static <K, V> String joinIgnoreNull(Map<K, V> map, String separator, String keyValueSeparator, String... otherParams) {
      return join(map, separator, keyValueSeparator, true, otherParams);
   }

   public static <K, V> String join(Map<K, V> map, String separator, String keyValueSeparator, boolean isIgnoreNull, String... otherParams) {
      StringBuilder strBuilder = StrUtil.builder();
      boolean isFirst = true;
      if (isNotEmpty(map)) {
         Iterator var7 = map.entrySet().iterator();

         label40:
         while(true) {
            Map.Entry entry;
            do {
               if (!var7.hasNext()) {
                  break label40;
               }

               entry = (Map.Entry)var7.next();
            } while(isIgnoreNull && (entry.getKey() == null || entry.getValue() == null));

            if (isFirst) {
               isFirst = false;
            } else {
               strBuilder.append(separator);
            }

            strBuilder.append(Convert.toStr(entry.getKey())).append(keyValueSeparator).append(Convert.toStr(entry.getValue()));
         }
      }

      if (ArrayUtil.isNotEmpty((Object[])otherParams)) {
         String[] var11 = otherParams;
         int var12 = otherParams.length;

         for(int var9 = 0; var9 < var12; ++var9) {
            String otherParam = var11[var9];
            strBuilder.append(otherParam);
         }
      }

      return strBuilder.toString();
   }

   public static <K, V> Map<K, V> edit(Map<K, V> map, Editor<Map.Entry<K, V>> editor) {
      if (null != map && null != editor) {
         Map<K, V> map2 = (Map)ReflectUtil.newInstanceIfPossible(map.getClass());
         if (null == map2) {
            map2 = new HashMap(map.size(), 1.0F);
         }

         if (isEmpty(map)) {
            return (Map)map2;
         } else {
            Iterator var4 = map.entrySet().iterator();

            while(var4.hasNext()) {
               Map.Entry<K, V> entry = (Map.Entry)var4.next();
               Map.Entry<K, V> modified = (Map.Entry)editor.edit(entry);
               if (null != modified) {
                  ((Map)map2).put(modified.getKey(), modified.getValue());
               }
            }

            return (Map)map2;
         }
      } else {
         return map;
      }
   }

   public static <K, V> Map<K, V> filter(Map<K, V> map, Filter<Map.Entry<K, V>> filter) {
      return null != map && null != filter ? edit(map, (t) -> {
         return filter.accept(t) ? t : null;
      }) : map;
   }

   public static <K, V, R> Map<K, R> map(Map<K, V> map, BiFunction<K, V, R> biFunction) {
      return (Map)(null != map && null != biFunction ? (Map)map.entrySet().stream().collect(CollectorUtil.toMap(Map.Entry::getKey, (m) -> {
         return biFunction.apply(m.getKey(), m.getValue());
      }, (l, r) -> {
         return l;
      })) : newHashMap());
   }

   public static <K, V> Map<K, V> filter(Map<K, V> map, K... keys) {
      if (null != map && null != keys) {
         Map<K, V> map2 = (Map)ReflectUtil.newInstanceIfPossible(map.getClass());
         if (null == map2) {
            map2 = new HashMap(map.size(), 1.0F);
         }

         if (isEmpty(map)) {
            return (Map)map2;
         } else {
            Object[] var3 = keys;
            int var4 = keys.length;

            for(int var5 = 0; var5 < var4; ++var5) {
               K key = var3[var5];
               if (map.containsKey(key)) {
                  ((Map)map2).put(key, map.get(key));
               }
            }

            return (Map)map2;
         }
      } else {
         return map;
      }
   }

   public static <T> Map<T, T> reverse(Map<T, T> map) {
      return edit(map, (t) -> {
         return new Map.Entry<T, T>() {
            public T getKey() {
               return t.getValue();
            }

            public T getValue() {
               return t.getKey();
            }

            public T setValue(T value) {
               throw new UnsupportedOperationException("Unsupported setValue method !");
            }
         };
      });
   }

   public static <K, V> Map<V, K> inverse(Map<K, V> map) {
      Map<V, K> result = createMap(map.getClass());
      map.forEach((key, value) -> {
         result.put(value, key);
      });
      return result;
   }

   public static <K, V> TreeMap<K, V> sort(Map<K, V> map) {
      return sort(map, (Comparator)null);
   }

   public static <K, V> TreeMap<K, V> sort(Map<K, V> map, Comparator<? super K> comparator) {
      if (null == map) {
         return null;
      } else {
         if (map instanceof TreeMap) {
            TreeMap<K, V> result = (TreeMap)map;
            if (null == comparator || comparator.equals(result.comparator())) {
               return result;
            }
         }

         return newTreeMap(map, comparator);
      }
   }

   public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map, boolean isDesc) {
      Map<K, V> result = new LinkedHashMap();
      Comparator<Map.Entry<K, V>> entryComparator = Entry.comparingByValue();
      if (isDesc) {
         entryComparator = entryComparator.reversed();
      }

      map.entrySet().stream().sorted(entryComparator).forEachOrdered((e) -> {
         Comparable var10000 = (Comparable)result.put(e.getKey(), e.getValue());
      });
      return result;
   }

   public static MapProxy createProxy(Map<?, ?> map) {
      return MapProxy.create(map);
   }

   public static <K, V> MapWrapper<K, V> wrap(Map<K, V> map) {
      return new MapWrapper(map);
   }

   public static <K, V> Map<K, V> unmodifiable(Map<K, V> map) {
      return Collections.unmodifiableMap(map);
   }

   public static <K, V> MapBuilder<K, V> builder() {
      return builder(new HashMap());
   }

   public static <K, V> MapBuilder<K, V> builder(Map<K, V> map) {
      return new MapBuilder(map);
   }

   public static <K, V> MapBuilder<K, V> builder(K k, V v) {
      return builder(new HashMap()).put(k, v);
   }

   public static <K, V> Map<K, V> getAny(Map<K, V> map, K... keys) {
      return filter(map, (entry) -> {
         return ArrayUtil.contains(keys, entry.getKey());
      });
   }

   public static <K, V> Map<K, V> removeAny(Map<K, V> map, K... keys) {
      Object[] var2 = keys;
      int var3 = keys.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         K key = var2[var4];
         map.remove(key);
      }

      return map;
   }

   public static String getStr(Map<?, ?> map, Object key) {
      return (String)get(map, key, String.class);
   }

   public static String getStr(Map<?, ?> map, Object key, String defaultValue) {
      return (String)get(map, key, (Class)String.class, defaultValue);
   }

   public static Integer getInt(Map<?, ?> map, Object key) {
      return (Integer)get(map, key, Integer.class);
   }

   public static Integer getInt(Map<?, ?> map, Object key, Integer defaultValue) {
      return (Integer)get(map, key, (Class)Integer.class, defaultValue);
   }

   public static Double getDouble(Map<?, ?> map, Object key) {
      return (Double)get(map, key, Double.class);
   }

   public static Double getDouble(Map<?, ?> map, Object key, Double defaultValue) {
      return (Double)get(map, key, (Class)Double.class, defaultValue);
   }

   public static Float getFloat(Map<?, ?> map, Object key) {
      return (Float)get(map, key, Float.class);
   }

   public static Float getFloat(Map<?, ?> map, Object key, Float defaultValue) {
      return (Float)get(map, key, (Class)Float.class, defaultValue);
   }

   public static Short getShort(Map<?, ?> map, Object key) {
      return (Short)get(map, key, Short.class);
   }

   public static Short getShort(Map<?, ?> map, Object key, Short defaultValue) {
      return (Short)get(map, key, (Class)Short.class, defaultValue);
   }

   public static Boolean getBool(Map<?, ?> map, Object key) {
      return (Boolean)get(map, key, Boolean.class);
   }

   public static Boolean getBool(Map<?, ?> map, Object key, Boolean defaultValue) {
      return (Boolean)get(map, key, (Class)Boolean.class, defaultValue);
   }

   public static Character getChar(Map<?, ?> map, Object key) {
      return (Character)get(map, key, Character.class);
   }

   public static Character getChar(Map<?, ?> map, Object key, Character defaultValue) {
      return (Character)get(map, key, (Class)Character.class, defaultValue);
   }

   public static Long getLong(Map<?, ?> map, Object key) {
      return (Long)get(map, key, Long.class);
   }

   public static Long getLong(Map<?, ?> map, Object key, Long defaultValue) {
      return (Long)get(map, key, (Class)Long.class, defaultValue);
   }

   public static Date getDate(Map<?, ?> map, Object key) {
      return (Date)get(map, key, Date.class);
   }

   public static Date getDate(Map<?, ?> map, Object key, Date defaultValue) {
      return (Date)get(map, key, (Class)Date.class, defaultValue);
   }

   public static <T> T get(Map<?, ?> map, Object key, Class<T> type) {
      return get(map, key, (Class)type, (Object)null);
   }

   public static <T> T get(Map<?, ?> map, Object key, Class<T> type, T defaultValue) {
      return null == map ? defaultValue : Convert.convert(type, map.get(key), defaultValue);
   }

   public static <T> T getQuietly(Map<?, ?> map, Object key, Class<T> type, T defaultValue) {
      return null == map ? defaultValue : Convert.convertQuietly(type, map.get(key), defaultValue);
   }

   public static <T> T get(Map<?, ?> map, Object key, TypeReference<T> type) {
      return get(map, key, (TypeReference)type, (Object)null);
   }

   public static <T> T get(Map<?, ?> map, Object key, TypeReference<T> type, T defaultValue) {
      return null == map ? defaultValue : Convert.convert((Type)type, map.get(key), defaultValue);
   }

   public static <T> T getQuietly(Map<?, ?> map, Object key, TypeReference<T> type, T defaultValue) {
      return null == map ? defaultValue : Convert.convertQuietly(type, map.get(key), defaultValue);
   }

   public static <K, V> Map<K, V> renameKey(Map<K, V> map, K oldKey, K newKey) {
      if (isNotEmpty(map) && map.containsKey(oldKey)) {
         if (map.containsKey(newKey)) {
            throw new IllegalArgumentException(StrUtil.format("The key '{}' exist !", new Object[]{newKey}));
         }

         map.put(newKey, map.remove(oldKey));
      }

      return map;
   }

   public static <K, V> Map<K, V> removeNullValue(Map<K, V> map) {
      if (isEmpty(map)) {
         return map;
      } else {
         Iterator<Map.Entry<K, V>> iter = map.entrySet().iterator();

         while(iter.hasNext()) {
            Map.Entry<K, V> entry = (Map.Entry)iter.next();
            if (null == entry.getValue()) {
               iter.remove();
            }
         }

         return map;
      }
   }

   public static <K, V> Map<K, V> empty() {
      return Collections.emptyMap();
   }

   public static <K, V, T extends Map<K, V>> T empty(Class<?> mapClass) {
      if (null == mapClass) {
         return Collections.emptyMap();
      } else if (NavigableMap.class == mapClass) {
         return Collections.emptyNavigableMap();
      } else if (SortedMap.class == mapClass) {
         return Collections.emptySortedMap();
      } else if (Map.class == mapClass) {
         return Collections.emptyMap();
      } else {
         throw new IllegalArgumentException(StrUtil.format("[{}] is not support to get empty!", new Object[]{mapClass}));
      }
   }

   public static void clear(Map<?, ?>... maps) {
      Map[] var1 = maps;
      int var2 = maps.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         Map<?, ?> map = var1[var3];
         if (isNotEmpty(map)) {
            map.clear();
         }
      }

   }

   public static <K, V> ArrayList<V> valuesOfKeys(Map<K, V> map, Iterator<K> keys) {
      ArrayList<V> values = new ArrayList();

      while(keys.hasNext()) {
         values.add(map.get(keys.next()));
      }

      return values;
   }

   public static <K, V> Map.Entry<K, V> entry(K key, V value) {
      return entry(key, value, true);
   }

   public static <K, V> Map.Entry<K, V> entry(K key, V value, boolean isImmutable) {
      return (Map.Entry)(isImmutable ? new AbstractMap.SimpleImmutableEntry(key, value) : new AbstractMap.SimpleEntry(key, value));
   }
}
