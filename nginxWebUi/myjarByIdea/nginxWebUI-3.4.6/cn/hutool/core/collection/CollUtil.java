package cn.hutool.core.collection;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.comparator.CompareUtil;
import cn.hutool.core.comparator.PinyinComparator;
import cn.hutool.core.comparator.PropertyComparator;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.convert.ConverterRegistry;
import cn.hutool.core.exceptions.UtilException;
import cn.hutool.core.lang.Editor;
import cn.hutool.core.lang.Filter;
import cn.hutool.core.lang.Matcher;
import cn.hutool.core.lang.func.Func1;
import cn.hutool.core.lang.hash.Hash32;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.TypeUtil;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;
import java.util.EnumSet;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NavigableSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.SortedSet;
import java.util.Stack;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class CollUtil {
   public static <T> Set<T> emptyIfNull(Set<T> set) {
      return null == set ? Collections.emptySet() : set;
   }

   public static <T> List<T> emptyIfNull(List<T> list) {
      return null == list ? Collections.emptyList() : list;
   }

   public static <T> Collection<T> union(Collection<T> coll1, Collection<T> coll2) {
      if (isEmpty(coll1)) {
         return new ArrayList(coll2);
      } else if (isEmpty(coll2)) {
         return new ArrayList(coll1);
      } else {
         ArrayList<T> list = new ArrayList(Math.max(coll1.size(), coll2.size()));
         Map<T, Integer> map1 = countMap(coll1);
         Map<T, Integer> map2 = countMap(coll2);
         Set<T> elts = newHashSet(coll2);
         elts.addAll(coll1);
         Iterator var7 = elts.iterator();

         while(var7.hasNext()) {
            T t = var7.next();
            int m = Math.max(Convert.toInt(map1.get(t), 0), Convert.toInt(map2.get(t), 0));

            for(int i = 0; i < m; ++i) {
               list.add(t);
            }
         }

         return list;
      }
   }

   @SafeVarargs
   public static <T> Collection<T> union(Collection<T> coll1, Collection<T> coll2, Collection<T>... otherColls) {
      Collection<T> union = union(coll1, coll2);
      Collection[] var4 = otherColls;
      int var5 = otherColls.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         Collection<T> coll = var4[var6];
         union = union(union, coll);
      }

      return union;
   }

   @SafeVarargs
   public static <T> Set<T> unionDistinct(Collection<T> coll1, Collection<T> coll2, Collection<T>... otherColls) {
      LinkedHashSet result;
      if (isEmpty(coll1)) {
         result = new LinkedHashSet();
      } else {
         result = new LinkedHashSet(coll1);
      }

      if (isNotEmpty(coll2)) {
         result.addAll(coll2);
      }

      if (ArrayUtil.isNotEmpty((Object[])otherColls)) {
         Collection[] var4 = otherColls;
         int var5 = otherColls.length;

         for(int var6 = 0; var6 < var5; ++var6) {
            Collection<T> otherColl = var4[var6];
            result.addAll(otherColl);
         }
      }

      return result;
   }

   @SafeVarargs
   public static <T> List<T> unionAll(Collection<T> coll1, Collection<T> coll2, Collection<T>... otherColls) {
      ArrayList result;
      if (isEmpty(coll1)) {
         result = new ArrayList();
      } else {
         result = new ArrayList(coll1);
      }

      if (isNotEmpty(coll2)) {
         result.addAll(coll2);
      }

      if (ArrayUtil.isNotEmpty((Object[])otherColls)) {
         Collection[] var4 = otherColls;
         int var5 = otherColls.length;

         for(int var6 = 0; var6 < var5; ++var6) {
            Collection<T> otherColl = var4[var6];
            result.addAll(otherColl);
         }
      }

      return result;
   }

   public static <T> Collection<T> intersection(Collection<T> coll1, Collection<T> coll2) {
      if (isNotEmpty(coll1) && isNotEmpty(coll2)) {
         ArrayList<T> list = new ArrayList(Math.min(coll1.size(), coll2.size()));
         Map<T, Integer> map1 = countMap(coll1);
         Map<T, Integer> map2 = countMap(coll2);
         Set<T> elts = newHashSet(coll2);
         Iterator var7 = elts.iterator();

         while(var7.hasNext()) {
            T t = var7.next();
            int m = Math.min(Convert.toInt(map1.get(t), 0), Convert.toInt(map2.get(t), 0));

            for(int i = 0; i < m; ++i) {
               list.add(t);
            }
         }

         return list;
      } else {
         return new ArrayList();
      }
   }

   @SafeVarargs
   public static <T> Collection<T> intersection(Collection<T> coll1, Collection<T> coll2, Collection<T>... otherColls) {
      Collection<T> intersection = intersection(coll1, coll2);
      if (isEmpty(intersection)) {
         return intersection;
      } else {
         Collection[] var4 = otherColls;
         int var5 = otherColls.length;

         for(int var6 = 0; var6 < var5; ++var6) {
            Collection<T> coll = var4[var6];
            intersection = intersection(intersection, coll);
            if (isEmpty(intersection)) {
               return intersection;
            }
         }

         return intersection;
      }
   }

   @SafeVarargs
   public static <T> Set<T> intersectionDistinct(Collection<T> coll1, Collection<T> coll2, Collection<T>... otherColls) {
      if (!isEmpty(coll1) && !isEmpty(coll2)) {
         Set<T> result = new LinkedHashSet(coll1);
         if (ArrayUtil.isNotEmpty((Object[])otherColls)) {
            Collection[] var4 = otherColls;
            int var5 = otherColls.length;

            for(int var6 = 0; var6 < var5; ++var6) {
               Collection<T> otherColl = var4[var6];
               if (!isNotEmpty(otherColl)) {
                  return new LinkedHashSet();
               }

               result.retainAll(otherColl);
            }
         }

         result.retainAll(coll2);
         return result;
      } else {
         return new LinkedHashSet();
      }
   }

   public static <T> Collection<T> disjunction(Collection<T> coll1, Collection<T> coll2) {
      if (isEmpty(coll1)) {
         return coll2;
      } else if (isEmpty(coll2)) {
         return coll1;
      } else {
         List<T> result = new ArrayList();
         Map<T, Integer> map1 = countMap(coll1);
         Map<T, Integer> map2 = countMap(coll2);
         Set<T> elts = newHashSet(coll2);
         elts.addAll(coll1);
         Iterator var7 = elts.iterator();

         while(var7.hasNext()) {
            T t = var7.next();
            int m = Math.abs(Convert.toInt(map1.get(t), 0) - Convert.toInt(map2.get(t), 0));

            for(int i = 0; i < m; ++i) {
               result.add(t);
            }
         }

         return result;
      }
   }

   public static <T> Collection<T> subtract(Collection<T> coll1, Collection<T> coll2) {
      Collection<T> result = (Collection)ObjectUtil.clone(coll1);
      if (null == result) {
         result = create(coll1.getClass());
         result.addAll(coll1);
      }

      result.removeAll(coll2);
      return result;
   }

   public static <T> List<T> subtractToList(Collection<T> coll1, Collection<T> coll2) {
      if (isEmpty(coll1)) {
         return ListUtil.empty();
      } else if (isEmpty(coll2)) {
         return ListUtil.list(true, coll1);
      } else {
         List<T> result = new LinkedList();
         Set<T> set = new HashSet(coll2);
         Iterator var4 = coll1.iterator();

         while(var4.hasNext()) {
            T t = var4.next();
            if (!set.contains(t)) {
               result.add(t);
            }
         }

         return result;
      }
   }

   public static boolean contains(Collection<?> collection, Object value) {
      return isNotEmpty(collection) && collection.contains(value);
   }

   public static boolean safeContains(Collection<?> collection, Object value) {
      try {
         return contains(collection, value);
      } catch (NullPointerException | ClassCastException var3) {
         return false;
      }
   }

   public static <T> boolean contains(Collection<T> collection, Predicate<? super T> containFunc) {
      if (isEmpty(collection)) {
         return false;
      } else {
         Iterator var2 = collection.iterator();

         Object t;
         do {
            if (!var2.hasNext()) {
               return false;
            }

            t = var2.next();
         } while(!containFunc.test(t));

         return true;
      }
   }

   public static boolean containsAny(Collection<?> coll1, Collection<?> coll2) {
      if (!isEmpty(coll1) && !isEmpty(coll2)) {
         Iterator var2;
         Object object;
         if (coll1.size() < coll2.size()) {
            var2 = coll1.iterator();

            while(var2.hasNext()) {
               object = var2.next();
               if (coll2.contains(object)) {
                  return true;
               }
            }
         } else {
            var2 = coll2.iterator();

            while(var2.hasNext()) {
               object = var2.next();
               if (coll1.contains(object)) {
                  return true;
               }
            }
         }

         return false;
      } else {
         return false;
      }
   }

   public static boolean containsAll(Collection<?> coll1, Collection<?> coll2) {
      if (isEmpty(coll1)) {
         return isEmpty(coll2);
      } else if (isEmpty(coll2)) {
         return true;
      } else if (coll1.size() < coll2.size()) {
         return false;
      } else {
         Iterator var2 = coll2.iterator();

         Object object;
         do {
            if (!var2.hasNext()) {
               return true;
            }

            object = var2.next();
         } while(coll1.contains(object));

         return false;
      }
   }

   public static <T> Map<T, Integer> countMap(Iterable<T> collection) {
      return IterUtil.countMap(null == collection ? null : collection.iterator());
   }

   public static <T> String join(Iterable<T> iterable, CharSequence conjunction, Function<T, ? extends CharSequence> func) {
      return null == iterable ? null : IterUtil.join(iterable.iterator(), conjunction, func);
   }

   public static <T> String join(Iterable<T> iterable, CharSequence conjunction) {
      return null == iterable ? null : IterUtil.join(iterable.iterator(), conjunction);
   }

   public static <T> String join(Iterable<T> iterable, CharSequence conjunction, String prefix, String suffix) {
      return null == iterable ? null : IterUtil.join(iterable.iterator(), conjunction, prefix, suffix);
   }

   /** @deprecated */
   @Deprecated
   public static <T> String join(Iterator<T> iterator, CharSequence conjunction) {
      return IterUtil.join(iterator, conjunction);
   }

   public static <T> List<T> popPart(Stack<T> surplusAlaDatas, int partSize) {
      if (isEmpty((Collection)surplusAlaDatas)) {
         return ListUtil.empty();
      } else {
         List<T> currentAlaDatas = new ArrayList();
         int size = surplusAlaDatas.size();
         int i;
         if (size > partSize) {
            for(i = 0; i < partSize; ++i) {
               currentAlaDatas.add(surplusAlaDatas.pop());
            }
         } else {
            for(i = 0; i < size; ++i) {
               currentAlaDatas.add(surplusAlaDatas.pop());
            }
         }

         return currentAlaDatas;
      }
   }

   public static <T> List<T> popPart(Deque<T> surplusAlaDatas, int partSize) {
      if (isEmpty((Collection)surplusAlaDatas)) {
         return ListUtil.empty();
      } else {
         List<T> currentAlaDatas = new ArrayList();
         int size = surplusAlaDatas.size();
         int i;
         if (size > partSize) {
            for(i = 0; i < partSize; ++i) {
               currentAlaDatas.add(surplusAlaDatas.pop());
            }
         } else {
            for(i = 0; i < size; ++i) {
               currentAlaDatas.add(surplusAlaDatas.pop());
            }
         }

         return currentAlaDatas;
      }
   }

   @SafeVarargs
   public static <T> HashSet<T> newHashSet(T... ts) {
      return set(false, ts);
   }

   @SafeVarargs
   public static <T> LinkedHashSet<T> newLinkedHashSet(T... ts) {
      return (LinkedHashSet)set(true, ts);
   }

   @SafeVarargs
   public static <T> HashSet<T> set(boolean isSorted, T... ts) {
      if (null == ts) {
         return (HashSet)(isSorted ? new LinkedHashSet() : new HashSet());
      } else {
         int initialCapacity = Math.max((int)((float)ts.length / 0.75F) + 1, 16);
         HashSet<T> set = isSorted ? new LinkedHashSet(initialCapacity) : new HashSet(initialCapacity);
         Collections.addAll((Collection)set, ts);
         return (HashSet)set;
      }
   }

   public static <T> HashSet<T> newHashSet(Collection<T> collection) {
      return newHashSet(false, collection);
   }

   public static <T> HashSet<T> newHashSet(boolean isSorted, Collection<T> collection) {
      return (HashSet)(isSorted ? new LinkedHashSet(collection) : new HashSet(collection));
   }

   public static <T> HashSet<T> newHashSet(boolean isSorted, Iterator<T> iter) {
      if (null == iter) {
         return set(isSorted, (Object[])null);
      } else {
         HashSet<T> set = isSorted ? new LinkedHashSet() : new HashSet();

         while(iter.hasNext()) {
            ((HashSet)set).add(iter.next());
         }

         return (HashSet)set;
      }
   }

   public static <T> HashSet<T> newHashSet(boolean isSorted, Enumeration<T> enumeration) {
      if (null == enumeration) {
         return set(isSorted, (Object[])null);
      } else {
         HashSet<T> set = isSorted ? new LinkedHashSet() : new HashSet();

         while(enumeration.hasMoreElements()) {
            ((HashSet)set).add(enumeration.nextElement());
         }

         return (HashSet)set;
      }
   }

   public static <T> List<T> list(boolean isLinked) {
      return ListUtil.list(isLinked);
   }

   @SafeVarargs
   public static <T> List<T> list(boolean isLinked, T... values) {
      return ListUtil.list(isLinked, values);
   }

   public static <T> List<T> list(boolean isLinked, Collection<T> collection) {
      return ListUtil.list(isLinked, collection);
   }

   public static <T> List<T> list(boolean isLinked, Iterable<T> iterable) {
      return ListUtil.list(isLinked, iterable);
   }

   public static <T> List<T> list(boolean isLinked, Iterator<T> iter) {
      return ListUtil.list(isLinked, iter);
   }

   public static <T> List<T> list(boolean isLinked, Enumeration<T> enumeration) {
      return ListUtil.list(isLinked, enumeration);
   }

   @SafeVarargs
   public static <T> ArrayList<T> newArrayList(T... values) {
      return ListUtil.toList(values);
   }

   @SafeVarargs
   public static <T> ArrayList<T> toList(T... values) {
      return ListUtil.toList(values);
   }

   public static <T> ArrayList<T> newArrayList(Collection<T> collection) {
      return ListUtil.toList(collection);
   }

   public static <T> ArrayList<T> newArrayList(Iterable<T> iterable) {
      return ListUtil.toList(iterable);
   }

   public static <T> ArrayList<T> newArrayList(Iterator<T> iterator) {
      return ListUtil.toList(iterator);
   }

   public static <T> ArrayList<T> newArrayList(Enumeration<T> enumeration) {
      return ListUtil.toList(enumeration);
   }

   @SafeVarargs
   public static <T> LinkedList<T> newLinkedList(T... values) {
      return ListUtil.toLinkedList(values);
   }

   public static <T> CopyOnWriteArrayList<T> newCopyOnWriteArrayList(Collection<T> collection) {
      return ListUtil.toCopyOnWriteArrayList(collection);
   }

   public static <T> BlockingQueue<T> newBlockingQueue(int capacity, boolean isLinked) {
      Object queue;
      if (isLinked) {
         queue = new LinkedBlockingDeque(capacity);
      } else {
         queue = new ArrayBlockingQueue(capacity);
      }

      return (BlockingQueue)queue;
   }

   public static <T> Collection<T> create(Class<?> collectionType) {
      Object list;
      if (collectionType.isAssignableFrom(AbstractCollection.class)) {
         list = new ArrayList();
      } else if (collectionType.isAssignableFrom(HashSet.class)) {
         list = new HashSet();
      } else if (collectionType.isAssignableFrom(LinkedHashSet.class)) {
         list = new LinkedHashSet();
      } else if (collectionType.isAssignableFrom(TreeSet.class)) {
         list = new TreeSet((o1, o2) -> {
            return o1 instanceof Comparable ? ((Comparable)o1).compareTo(o2) : CompareUtil.compare(o1.toString(), o2.toString());
         });
      } else if (collectionType.isAssignableFrom(EnumSet.class)) {
         list = EnumSet.noneOf(ClassUtil.getTypeArgument(collectionType));
      } else if (collectionType.isAssignableFrom(ArrayList.class)) {
         list = new ArrayList();
      } else if (collectionType.isAssignableFrom(LinkedList.class)) {
         list = new LinkedList();
      } else {
         try {
            list = (Collection)ReflectUtil.newInstance(collectionType);
         } catch (Exception var4) {
            Class<?> superclass = collectionType.getSuperclass();
            if (null != superclass && collectionType != superclass) {
               return create(superclass);
            }

            throw new UtilException(var4);
         }
      }

      return (Collection)list;
   }

   public static <T> ArrayList<T> distinct(Collection<T> collection) {
      if (isEmpty(collection)) {
         return new ArrayList();
      } else {
         return collection instanceof Set ? new ArrayList(collection) : new ArrayList(new LinkedHashSet(collection));
      }
   }

   public static <T, K> List<T> distinct(Collection<T> collection, Function<T, K> uniqueGenerator, boolean override) {
      if (isEmpty(collection)) {
         return new ArrayList();
      } else {
         UniqueKeySet<K, T> set = new UniqueKeySet(true, uniqueGenerator);
         if (override) {
            set.addAll(collection);
         } else {
            set.addAllIfAbsent(collection);
         }

         return new ArrayList(set);
      }
   }

   public static <T> List<T> sub(List<T> list, int start, int end) {
      return ListUtil.sub(list, start, end);
   }

   public static <T> List<T> sub(List<T> list, int start, int end, int step) {
      return ListUtil.sub(list, start, end, step);
   }

   public static <T> List<T> sub(Collection<T> collection, int start, int end) {
      return sub((Collection)collection, start, end, 1);
   }

   public static <T> List<T> sub(Collection<T> collection, int start, int end, int step) {
      if (isEmpty(collection)) {
         return ListUtil.empty();
      } else {
         List<T> list = collection instanceof List ? (List)collection : ListUtil.toList(collection);
         return sub((List)list, start, end, step);
      }
   }

   /** @deprecated */
   @Deprecated
   public static <T> List<List<T>> splitList(List<T> list, int size) {
      return ListUtil.partition(list, size);
   }

   public static <T> List<List<T>> split(Collection<T> collection, int size) {
      List<List<T>> result = new ArrayList();
      if (isEmpty(collection)) {
         return result;
      } else {
         ArrayList<T> subList = new ArrayList(size);

         Object t;
         for(Iterator var4 = collection.iterator(); var4.hasNext(); subList.add(t)) {
            t = var4.next();
            if (subList.size() >= size) {
               result.add(subList);
               subList = new ArrayList(size);
            }
         }

         result.add(subList);
         return result;
      }
   }

   public static <T> Collection<T> edit(Collection<T> collection, Editor<T> editor) {
      if (null != collection && null != editor) {
         Collection<T> collection2 = create(collection.getClass());
         if (isEmpty(collection)) {
            return collection2;
         } else {
            Iterator var4 = collection.iterator();

            while(var4.hasNext()) {
               T t = var4.next();
               T modified = editor.edit(t);
               if (null != modified) {
                  collection2.add(modified);
               }
            }

            return collection2;
         }
      } else {
         return collection;
      }
   }

   public static <T> Collection<T> filterNew(Collection<T> collection, Filter<T> filter) {
      return null != collection && null != filter ? edit(collection, (t) -> {
         return filter.accept(t) ? t : null;
      }) : collection;
   }

   public static <T extends Collection<E>, E> T removeAny(T collection, E... elesRemoved) {
      collection.removeAll(newHashSet(elesRemoved));
      return collection;
   }

   public static <T extends Collection<E>, E> T filter(T collection, Filter<E> filter) {
      return (Collection)IterUtil.filter((Iterable)collection, filter);
   }

   public static <T extends Collection<E>, E> T removeNull(T collection) {
      return filter(collection, Objects::nonNull);
   }

   public static <T extends Collection<E>, E extends CharSequence> T removeEmpty(T collection) {
      return filter(collection, CharSequenceUtil::isNotEmpty);
   }

   public static <T extends Collection<E>, E extends CharSequence> T removeBlank(T collection) {
      return filter(collection, CharSequenceUtil::isNotBlank);
   }

   public static <T extends Collection<E>, E> T removeWithAddIf(T targetCollection, T resultCollection, Predicate<? super E> predicate) {
      Objects.requireNonNull(predicate);
      Iterator<E> each = targetCollection.iterator();

      while(each.hasNext()) {
         E next = each.next();
         if (predicate.test(next)) {
            resultCollection.add(next);
            each.remove();
         }
      }

      return resultCollection;
   }

   public static <T extends Collection<E>, E> List<E> removeWithAddIf(T targetCollection, Predicate<? super E> predicate) {
      List<E> removed = new ArrayList();
      removeWithAddIf(targetCollection, removed, predicate);
      return removed;
   }

   public static List<Object> extract(Iterable<?> collection, Editor<Object> editor) {
      return extract(collection, editor, false);
   }

   public static List<Object> extract(Iterable<?> collection, Editor<Object> editor, boolean ignoreNull) {
      return map(collection, editor::edit, ignoreNull);
   }

   public static <T, R> List<R> map(Iterable<T> collection, Function<? super T, ? extends R> func, boolean ignoreNull) {
      List<R> fieldValueList = new ArrayList();
      if (null == collection) {
         return fieldValueList;
      } else {
         Iterator var5 = collection.iterator();

         while(true) {
            Object value;
            do {
               Object t;
               do {
                  if (!var5.hasNext()) {
                     return fieldValueList;
                  }

                  t = var5.next();
               } while(null == t && ignoreNull);

               value = func.apply(t);
            } while(null == value && ignoreNull);

            fieldValueList.add(value);
         }
      }
   }

   public static List<Object> getFieldValues(Iterable<?> collection, String fieldName) {
      return getFieldValues(collection, fieldName, false);
   }

   public static List<Object> getFieldValues(Iterable<?> collection, String fieldName, boolean ignoreNull) {
      return map(collection, (bean) -> {
         return bean instanceof Map ? ((Map)bean).get(fieldName) : ReflectUtil.getFieldValue(bean, fieldName);
      }, ignoreNull);
   }

   public static <T> List<T> getFieldValues(Iterable<?> collection, String fieldName, Class<T> elementType) {
      List<Object> fieldValues = getFieldValues(collection, fieldName);
      return Convert.toList(elementType, fieldValues);
   }

   public static <K, V> Map<K, V> fieldValueMap(Iterable<V> iterable, String fieldName) {
      return IterUtil.fieldValueMap(IterUtil.getIter(iterable), fieldName);
   }

   public static <K, V> Map<K, V> fieldValueAsMap(Iterable<?> iterable, String fieldNameForKey, String fieldNameForValue) {
      return IterUtil.fieldValueAsMap(IterUtil.getIter(iterable), fieldNameForKey, fieldNameForValue);
   }

   public static <T> T findOne(Iterable<T> collection, Filter<T> filter) {
      if (null != collection) {
         Iterator var2 = collection.iterator();

         while(var2.hasNext()) {
            T t = var2.next();
            if (filter.accept(t)) {
               return t;
            }
         }
      }

      return null;
   }

   public static <T> T findOneByField(Iterable<T> collection, String fieldName, Object fieldValue) {
      return findOne(collection, (t) -> {
         if (t instanceof Map) {
            Map<?, ?> map = (Map)t;
            Object valuex = map.get(fieldName);
            return ObjectUtil.equal(valuex, fieldValue);
         } else {
            Object value = ReflectUtil.getFieldValue(t, fieldName);
            return ObjectUtil.equal(value, fieldValue);
         }
      });
   }

   public static <T> int count(Iterable<T> iterable, Matcher<T> matcher) {
      int count = 0;
      if (null != iterable) {
         Iterator var3 = iterable.iterator();

         while(true) {
            Object t;
            do {
               if (!var3.hasNext()) {
                  return count;
               }

               t = var3.next();
            } while(null != matcher && !matcher.match(t));

            ++count;
         }
      } else {
         return count;
      }
   }

   public static <T> int indexOf(Collection<T> collection, Matcher<T> matcher) {
      if (isNotEmpty(collection)) {
         int index = 0;

         for(Iterator var3 = collection.iterator(); var3.hasNext(); ++index) {
            T t = var3.next();
            if (null == matcher || matcher.match(t)) {
               return index;
            }
         }
      }

      return -1;
   }

   public static <T> int lastIndexOf(Collection<T> collection, Matcher<T> matcher) {
      if (collection instanceof List) {
         return ListUtil.lastIndexOf((List)collection, matcher);
      } else {
         int matchIndex = -1;
         if (isNotEmpty(collection)) {
            int index = collection.size();

            for(Iterator var4 = collection.iterator(); var4.hasNext(); --index) {
               T t = var4.next();
               if (null == matcher || matcher.match(t)) {
                  matchIndex = index;
               }
            }
         }

         return matchIndex;
      }
   }

   public static <T> int[] indexOfAll(Collection<T> collection, Matcher<T> matcher) {
      List<Integer> indexList = new ArrayList();
      if (null != collection) {
         int index = 0;

         for(Iterator var4 = collection.iterator(); var4.hasNext(); ++index) {
            T t = var4.next();
            if (null == matcher || matcher.match(t)) {
               indexList.add(index);
            }
         }
      }

      return (int[])Convert.convert((Class)int[].class, indexList);
   }

   public static boolean isEmpty(Collection<?> collection) {
      return collection == null || collection.isEmpty();
   }

   public static <T extends Collection<E>, E> T defaultIfEmpty(T collection, T defaultCollection) {
      return isEmpty(collection) ? defaultCollection : collection;
   }

   public static <T extends Collection<E>, E> T defaultIfEmpty(T collection, Supplier<? extends T> supplier) {
      return isEmpty(collection) ? (Collection)supplier.get() : collection;
   }

   public static boolean isEmpty(Iterable<?> iterable) {
      return IterUtil.isEmpty(iterable);
   }

   public static boolean isEmpty(Iterator<?> Iterator) {
      return IterUtil.isEmpty(Iterator);
   }

   public static boolean isEmpty(Enumeration<?> enumeration) {
      return null == enumeration || !enumeration.hasMoreElements();
   }

   public static boolean isEmpty(Map<?, ?> map) {
      return MapUtil.isEmpty(map);
   }

   public static boolean isNotEmpty(Collection<?> collection) {
      return !isEmpty(collection);
   }

   public static boolean isNotEmpty(Iterable<?> iterable) {
      return IterUtil.isNotEmpty(iterable);
   }

   public static boolean isNotEmpty(Iterator<?> Iterator) {
      return IterUtil.isNotEmpty(Iterator);
   }

   public static boolean isNotEmpty(Enumeration<?> enumeration) {
      return null != enumeration && enumeration.hasMoreElements();
   }

   public static boolean hasNull(Iterable<?> iterable) {
      return IterUtil.hasNull(iterable);
   }

   public static boolean isNotEmpty(Map<?, ?> map) {
      return MapUtil.isNotEmpty(map);
   }

   public static Map<String, String> zip(String keys, String values, String delimiter, boolean isOrder) {
      return ArrayUtil.zip(StrUtil.splitToArray(keys, delimiter), StrUtil.splitToArray(values, delimiter), isOrder);
   }

   public static Map<String, String> zip(String keys, String values, String delimiter) {
      return zip(keys, values, delimiter, false);
   }

   public static <K, V> Map<K, V> zip(Collection<K> keys, Collection<V> values) {
      if (!isEmpty(keys) && !isEmpty(values)) {
         int entryCount = Math.min(keys.size(), values.size());
         Map<K, V> map = MapUtil.newHashMap(entryCount);
         Iterator<K> keyIterator = keys.iterator();

         for(Iterator<V> valueIterator = values.iterator(); entryCount > 0; --entryCount) {
            map.put(keyIterator.next(), valueIterator.next());
         }

         return map;
      } else {
         return MapUtil.empty();
      }
   }

   public static <K, V> HashMap<K, V> toMap(Iterable<Map.Entry<K, V>> entryIter) {
      return IterUtil.toMap(entryIter);
   }

   public static HashMap<Object, Object> toMap(Object[] array) {
      return MapUtil.of(array);
   }

   public static <T> TreeSet<T> toTreeSet(Collection<T> collection, Comparator<T> comparator) {
      TreeSet<T> treeSet = new TreeSet(comparator);
      treeSet.addAll(collection);
      return treeSet;
   }

   public static <E> Enumeration<E> asEnumeration(Iterator<E> iter) {
      return new IteratorEnumeration(iter);
   }

   public static <E> Iterator<E> asIterator(Enumeration<E> e) {
      return IterUtil.asIterator(e);
   }

   public static <E> Iterable<E> asIterable(Iterator<E> iter) {
      return IterUtil.asIterable(iter);
   }

   public static <E> Collection<E> toCollection(Iterable<E> iterable) {
      return (Collection)(iterable instanceof Collection ? (Collection)iterable : newArrayList(iterable.iterator()));
   }

   public static <K, V> Map<K, List<V>> toListMap(Iterable<? extends Map<K, V>> mapList) {
      return MapUtil.toListMap(mapList);
   }

   public static <K, V> List<Map<K, V>> toMapList(Map<K, ? extends Iterable<V>> listMap) {
      return MapUtil.toMapList(listMap);
   }

   public static <K, V> Map<K, V> toMap(Iterable<V> values, Map<K, V> map, Func1<V, K> keyFunc) {
      return IterUtil.toMap(null == values ? null : values.iterator(), map, keyFunc);
   }

   public static <K, V, E> Map<K, V> toMap(Iterable<E> values, Map<K, V> map, Func1<E, K> keyFunc, Func1<E, V> valueFunc) {
      return IterUtil.toMap(null == values ? null : values.iterator(), map, keyFunc, valueFunc);
   }

   public static <T> Collection<T> addAll(Collection<T> collection, Object value) {
      return addAll(collection, value, TypeUtil.getTypeArgument(collection.getClass()));
   }

   public static <T> Collection<T> addAll(Collection<T> collection, Object value, Type elementType) {
      if (null != collection && null != value) {
         if (TypeUtil.isUnknown((Type)elementType)) {
            elementType = Object.class;
         }

         Object iter;
         if (value instanceof Iterator) {
            iter = (Iterator)value;
         } else if (value instanceof Iterable) {
            iter = ((Iterable)value).iterator();
         } else if (value instanceof Enumeration) {
            iter = new EnumerationIter((Enumeration)value);
         } else if (ArrayUtil.isArray(value)) {
            iter = new ArrayIter(value);
         } else if (value instanceof CharSequence) {
            String ArrayStr = StrUtil.unWrap((CharSequence)value, '[', ']');
            iter = StrUtil.splitTrim(ArrayStr, ',').iterator();
         } else {
            iter = newArrayList(value).iterator();
         }

         ConverterRegistry convert = ConverterRegistry.getInstance();

         while(((Iterator)iter).hasNext()) {
            collection.add(convert.convert((Type)elementType, ((Iterator)iter).next()));
         }

         return collection;
      } else {
         return collection;
      }
   }

   public static <T> Collection<T> addAll(Collection<T> collection, Iterator<T> iterator) {
      if (null != collection && null != iterator) {
         while(iterator.hasNext()) {
            collection.add(iterator.next());
         }
      }

      return collection;
   }

   public static <T> Collection<T> addAll(Collection<T> collection, Iterable<T> iterable) {
      return iterable == null ? collection : addAll(collection, iterable.iterator());
   }

   public static <T> Collection<T> addAll(Collection<T> collection, Enumeration<T> enumeration) {
      if (null != collection && null != enumeration) {
         while(enumeration.hasMoreElements()) {
            collection.add(enumeration.nextElement());
         }
      }

      return collection;
   }

   public static <T> Collection<T> addAll(Collection<T> collection, T[] values) {
      if (null != collection && null != values) {
         Collections.addAll(collection, values);
      }

      return collection;
   }

   public static <T> List<T> addAllIfNotContains(List<T> list, List<T> otherList) {
      Iterator var2 = otherList.iterator();

      while(var2.hasNext()) {
         T t = var2.next();
         if (!list.contains(t)) {
            list.add(t);
         }
      }

      return list;
   }

   public static <T> T get(Collection<T> collection, int index) {
      if (null == collection) {
         return null;
      } else {
         int size = collection.size();
         if (0 == size) {
            return null;
         } else {
            if (index < 0) {
               index += size;
            }

            if (index < size && index >= 0) {
               if (collection instanceof List) {
                  List<T> list = (List)collection;
                  return list.get(index);
               } else {
                  return IterUtil.get(collection.iterator(), index);
               }
            } else {
               return null;
            }
         }
      }
   }

   public static <T> List<T> getAny(Collection<T> collection, int... indexes) {
      int size = collection.size();
      ArrayList<T> result = new ArrayList();
      int[] var5;
      int var6;
      int var7;
      int index;
      if (collection instanceof List) {
         List<T> list = (List)collection;
         var5 = indexes;
         var6 = indexes.length;

         for(var7 = 0; var7 < var6; ++var7) {
            index = var5[var7];
            if (index < 0) {
               index += size;
            }

            result.add(list.get(index));
         }
      } else {
         Object[] array = collection.toArray();
         var5 = indexes;
         var6 = indexes.length;

         for(var7 = 0; var7 < var6; ++var7) {
            index = var5[var7];
            if (index < 0) {
               index += size;
            }

            result.add(array[index]);
         }
      }

      return result;
   }

   public static <T> T getFirst(Iterable<T> iterable) {
      return IterUtil.getFirst(iterable);
   }

   public static <T> T getFirst(Iterator<T> iterator) {
      return IterUtil.getFirst(iterator);
   }

   public static <T> T getLast(Collection<T> collection) {
      return get(collection, -1);
   }

   /** @deprecated */
   @Deprecated
   public static Class<?> getElementType(Iterable<?> iterable) {
      return IterUtil.getElementType(iterable);
   }

   /** @deprecated */
   @Deprecated
   public static Class<?> getElementType(Iterator<?> iterator) {
      return IterUtil.getElementType(iterator);
   }

   public static <K, V> ArrayList<V> valuesOfKeys(Map<K, V> map, K... keys) {
      return MapUtil.valuesOfKeys(map, new ArrayIter(keys));
   }

   public static <K, V> ArrayList<V> valuesOfKeys(Map<K, V> map, Iterable<K> keys) {
      return valuesOfKeys(map, keys.iterator());
   }

   public static <K, V> ArrayList<V> valuesOfKeys(Map<K, V> map, Iterator<K> keys) {
      return MapUtil.valuesOfKeys(map, keys);
   }

   @SafeVarargs
   public static <T> List<T> sortPageAll(int pageNo, int pageSize, Comparator<T> comparator, Collection<T>... colls) {
      List<T> list = new ArrayList(pageNo * pageSize);
      Collection[] var5 = colls;
      int var6 = colls.length;

      for(int var7 = 0; var7 < var6; ++var7) {
         Collection<T> coll = var5[var7];
         list.addAll(coll);
      }

      if (null != comparator) {
         list.sort(comparator);
      }

      return page(pageNo, pageSize, list);
   }

   public static <T> List<T> page(int pageNo, int pageSize, List<T> list) {
      return ListUtil.page(pageNo, pageSize, list);
   }

   public static <T> List<T> sort(Collection<T> collection, Comparator<? super T> comparator) {
      List<T> list = new ArrayList(collection);
      list.sort(comparator);
      return list;
   }

   public static <T> List<T> sort(List<T> list, Comparator<? super T> c) {
      return ListUtil.sort(list, c);
   }

   public static <T> List<T> sortByProperty(Collection<T> collection, String property) {
      return sort((Collection)collection, new PropertyComparator(property));
   }

   public static <T> List<T> sortByProperty(List<T> list, String property) {
      return ListUtil.sortByProperty(list, property);
   }

   public static List<String> sortByPinyin(Collection<String> collection) {
      return sort((Collection)collection, new PinyinComparator());
   }

   public static List<String> sortByPinyin(List<String> list) {
      return ListUtil.sortByPinyin(list);
   }

   public static <K, V> TreeMap<K, V> sort(Map<K, V> map, Comparator<? super K> comparator) {
      TreeMap<K, V> result = new TreeMap(comparator);
      result.putAll(map);
      return result;
   }

   public static <K, V> LinkedHashMap<K, V> sortToMap(Collection<Map.Entry<K, V>> entryCollection, Comparator<Map.Entry<K, V>> comparator) {
      List<Map.Entry<K, V>> list = new LinkedList(entryCollection);
      list.sort(comparator);
      LinkedHashMap<K, V> result = new LinkedHashMap();
      Iterator var4 = list.iterator();

      while(var4.hasNext()) {
         Map.Entry<K, V> entry = (Map.Entry)var4.next();
         result.put(entry.getKey(), entry.getValue());
      }

      return result;
   }

   public static <K, V> LinkedHashMap<K, V> sortByEntry(Map<K, V> map, Comparator<Map.Entry<K, V>> comparator) {
      return sortToMap(map.entrySet(), comparator);
   }

   public static <K, V> List<Map.Entry<K, V>> sortEntryToList(Collection<Map.Entry<K, V>> collection) {
      List<Map.Entry<K, V>> list = new LinkedList(collection);
      list.sort((o1, o2) -> {
         V v1 = o1.getValue();
         V v2 = o2.getValue();
         return v1 instanceof Comparable ? ((Comparable)v1).compareTo(v2) : v1.toString().compareTo(v2.toString());
      });
      return list;
   }

   public static <T> void forEach(Iterable<T> iterable, Consumer<T> consumer) {
      if (iterable != null) {
         forEach(iterable.iterator(), consumer);
      }
   }

   public static <T> void forEach(Iterator<T> iterator, Consumer<T> consumer) {
      if (iterator != null) {
         for(int index = 0; iterator.hasNext(); ++index) {
            consumer.accept(iterator.next(), index);
         }

      }
   }

   public static <T> void forEach(Enumeration<T> enumeration, Consumer<T> consumer) {
      if (enumeration != null) {
         for(int index = 0; enumeration.hasMoreElements(); ++index) {
            consumer.accept(enumeration.nextElement(), index);
         }

      }
   }

   public static <K, V> void forEach(Map<K, V> map, KVConsumer<K, V> kvConsumer) {
      if (map != null) {
         int index = 0;

         for(Iterator var3 = map.entrySet().iterator(); var3.hasNext(); ++index) {
            Map.Entry<K, V> entry = (Map.Entry)var3.next();
            kvConsumer.accept(entry.getKey(), entry.getValue(), index);
         }

      }
   }

   public static <T> List<List<T>> group(Collection<T> collection, Hash32<T> hash) {
      List<List<T>> result = new ArrayList();
      if (isEmpty(collection)) {
         return result;
      } else {
         if (null == hash) {
            hash = (tx) -> {
               return null == tx ? 0 : tx.hashCode();
            };
         }

         Iterator var5 = collection.iterator();

         while(true) {
            while(var5.hasNext()) {
               T t = var5.next();
               int index = hash.hash32(t);
               if (result.size() - 1 < index) {
                  while(result.size() - 1 < index) {
                     result.add((Object)null);
                  }

                  result.set(index, newArrayList(t));
               } else {
                  List<T> subList = (List)result.get(index);
                  if (null == subList) {
                     result.set(index, newArrayList(t));
                  } else {
                     subList.add(t);
                  }
               }
            }

            return result;
         }
      }
   }

   public static <T> List<List<T>> groupByField(Collection<T> collection, final String fieldName) {
      return group(collection, new Hash32<T>() {
         private final List<Object> fieldNameList = new ArrayList();

         public int hash32(T t) {
            if (null != t && BeanUtil.isBean(t.getClass())) {
               Object value = ReflectUtil.getFieldValue(t, fieldName);
               int hash = this.fieldNameList.indexOf(value);
               if (hash < 0) {
                  this.fieldNameList.add(value);
                  return this.fieldNameList.size() - 1;
               } else {
                  return hash;
               }
            } else {
               return 0;
            }
         }
      });
   }

   public static <T> List<T> reverse(List<T> list) {
      return ListUtil.reverse(list);
   }

   public static <T> List<T> reverseNew(List<T> list) {
      return ListUtil.reverseNew(list);
   }

   public static <T> List<T> setOrAppend(List<T> list, int index, T element) {
      return ListUtil.setOrAppend(list, index, element);
   }

   public static <K> Set<K> keySet(Collection<Map<K, ?>> mapCollection) {
      if (isEmpty(mapCollection)) {
         return new HashSet();
      } else {
         HashSet<K> set = new HashSet(mapCollection.size() * 16);
         Iterator var2 = mapCollection.iterator();

         while(var2.hasNext()) {
            Map<K, ?> map = (Map)var2.next();
            set.addAll(map.keySet());
         }

         return set;
      }
   }

   public static <V> List<V> values(Collection<Map<?, V>> mapCollection) {
      List<V> values = new ArrayList();
      Iterator var2 = mapCollection.iterator();

      while(var2.hasNext()) {
         Map<?, V> map = (Map)var2.next();
         values.addAll(map.values());
      }

      return values;
   }

   public static <T extends Comparable<? super T>> T max(Collection<T> coll) {
      return (Comparable)Collections.max(coll);
   }

   public static <T extends Comparable<? super T>> T min(Collection<T> coll) {
      return (Comparable)Collections.min(coll);
   }

   public static <T> Collection<T> unmodifiable(Collection<? extends T> c) {
      return Collections.unmodifiableCollection(c);
   }

   public static <E, T extends Collection<E>> T empty(Class<?> collectionClass) {
      if (null == collectionClass) {
         return Collections.emptyList();
      } else if (Set.class.isAssignableFrom(collectionClass)) {
         if (NavigableSet.class == collectionClass) {
            return Collections.emptyNavigableSet();
         } else {
            return (Collection)(SortedSet.class == collectionClass ? Collections.emptySortedSet() : Collections.emptySet());
         }
      } else if (List.class.isAssignableFrom(collectionClass)) {
         return Collections.emptyList();
      } else {
         throw new IllegalArgumentException(StrUtil.format("[{}] is not support to get empty!", new Object[]{collectionClass}));
      }
   }

   public static void clear(Collection<?>... collections) {
      Collection[] var1 = collections;
      int var2 = collections.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         Collection<?> collection = var1[var3];
         if (isNotEmpty(collection)) {
            collection.clear();
         }
      }

   }

   public static <T> void padLeft(List<T> list, int minLen, T padObj) {
      Objects.requireNonNull(list);
      if (list.isEmpty()) {
         padRight(list, minLen, padObj);
      } else {
         for(int i = list.size(); i < minLen; ++i) {
            list.add(0, padObj);
         }

      }
   }

   public static <T> void padRight(Collection<T> list, int minLen, T padObj) {
      Objects.requireNonNull(list);

      for(int i = list.size(); i < minLen; ++i) {
         list.add(padObj);
      }

   }

   public static <F, T> Collection<T> trans(Collection<F> collection, Function<? super F, ? extends T> function) {
      return new TransCollection(collection, function);
   }

   public static <E, K, V> void setValueByMap(Iterable<E> iterable, Map<K, V> map, Function<E, K> keyGenerate, BiConsumer<E, V> biConsumer) {
      iterable.forEach((x) -> {
         Optional.ofNullable(map.get(keyGenerate.apply(x))).ifPresent((y) -> {
            biConsumer.accept(x, y);
         });
      });
   }

   public static int size(Object object) {
      if (object == null) {
         return 0;
      } else {
         int total = 0;
         if (object instanceof Map) {
            total = ((Map)object).size();
         } else if (object instanceof Collection) {
            total = ((Collection)object).size();
         } else if (object instanceof Iterable) {
            total = IterUtil.size((Iterable)object);
         } else if (object instanceof Iterator) {
            total = IterUtil.size((Iterator)object);
         } else if (object instanceof Enumeration) {
            Enumeration<?> it = (Enumeration)object;

            while(it.hasMoreElements()) {
               ++total;
               it.nextElement();
            }
         } else {
            if (!ArrayUtil.isArray(object)) {
               throw new IllegalArgumentException("Unsupported object type: " + object.getClass().getName());
            }

            total = ArrayUtil.length(object);
         }

         return total;
      }
   }

   public static boolean isEqualList(Collection<?> list1, Collection<?> list2) {
      if (list1 == list2) {
         return true;
      } else {
         return list1 != null && list2 != null && list1.size() == list2.size() ? IterUtil.isEqualList(list1, list2) : false;
      }
   }

   @FunctionalInterface
   public interface KVConsumer<K, V> extends Serializable {
      void accept(K var1, V var2, int var3);
   }

   @FunctionalInterface
   public interface Consumer<T> extends Serializable {
      void accept(T var1, int var2);
   }
}
