/*      */ package cn.hutool.core.collection;
/*      */ 
/*      */ import cn.hutool.core.bean.BeanUtil;
/*      */ import cn.hutool.core.comparator.CompareUtil;
/*      */ import cn.hutool.core.comparator.PinyinComparator;
/*      */ import cn.hutool.core.comparator.PropertyComparator;
/*      */ import cn.hutool.core.convert.Convert;
/*      */ import cn.hutool.core.convert.ConverterRegistry;
/*      */ import cn.hutool.core.exceptions.UtilException;
/*      */ import cn.hutool.core.lang.Editor;
/*      */ import cn.hutool.core.lang.Filter;
/*      */ import cn.hutool.core.lang.Matcher;
/*      */ import cn.hutool.core.lang.func.Func1;
/*      */ import cn.hutool.core.lang.hash.Hash32;
/*      */ import cn.hutool.core.map.MapUtil;
/*      */ import cn.hutool.core.text.CharSequenceUtil;
/*      */ import cn.hutool.core.util.ArrayUtil;
/*      */ import cn.hutool.core.util.ClassUtil;
/*      */ import cn.hutool.core.util.ObjectUtil;
/*      */ import cn.hutool.core.util.ReflectUtil;
/*      */ import cn.hutool.core.util.StrUtil;
/*      */ import cn.hutool.core.util.TypeUtil;
/*      */ import java.io.Serializable;
/*      */ import java.lang.reflect.Type;
/*      */ import java.util.AbstractCollection;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Collection;
/*      */ import java.util.Collections;
/*      */ import java.util.Comparator;
/*      */ import java.util.Deque;
/*      */ import java.util.EnumSet;
/*      */ import java.util.Enumeration;
/*      */ import java.util.HashMap;
/*      */ import java.util.HashSet;
/*      */ import java.util.Iterator;
/*      */ import java.util.LinkedHashMap;
/*      */ import java.util.LinkedHashSet;
/*      */ import java.util.LinkedList;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.NavigableSet;
/*      */ import java.util.Objects;
/*      */ import java.util.Optional;
/*      */ import java.util.Set;
/*      */ import java.util.SortedSet;
/*      */ import java.util.Stack;
/*      */ import java.util.TreeMap;
/*      */ import java.util.TreeSet;
/*      */ import java.util.concurrent.ArrayBlockingQueue;
/*      */ import java.util.concurrent.BlockingQueue;
/*      */ import java.util.concurrent.CopyOnWriteArrayList;
/*      */ import java.util.concurrent.LinkedBlockingDeque;
/*      */ import java.util.function.BiConsumer;
/*      */ import java.util.function.Function;
/*      */ import java.util.function.Predicate;
/*      */ import java.util.function.Supplier;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class CollUtil
/*      */ {
/*      */   public static <T> Set<T> emptyIfNull(Set<T> set) {
/*   83 */     return (null == set) ? Collections.<T>emptySet() : set;
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
/*      */   public static <T> List<T> emptyIfNull(List<T> list) {
/*   96 */     return (null == list) ? Collections.<T>emptyList() : list;
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
/*      */   public static <T> Collection<T> union(Collection<T> coll1, Collection<T> coll2) {
/*  111 */     if (isEmpty(coll1))
/*  112 */       return new ArrayList<>(coll2); 
/*  113 */     if (isEmpty(coll2)) {
/*  114 */       return new ArrayList<>(coll1);
/*      */     }
/*      */     
/*  117 */     ArrayList<T> list = new ArrayList<>(Math.max(coll1.size(), coll2.size()));
/*  118 */     Map<T, Integer> map1 = countMap(coll1);
/*  119 */     Map<T, Integer> map2 = countMap(coll2);
/*  120 */     Set<T> elts = newHashSet(coll2);
/*  121 */     elts.addAll(coll1);
/*      */     
/*  123 */     for (T t : elts) {
/*  124 */       int m = Math.max(Convert.toInt(map1.get(t), Integer.valueOf(0)).intValue(), Convert.toInt(map2.get(t), Integer.valueOf(0)).intValue());
/*  125 */       for (int i = 0; i < m; i++) {
/*  126 */         list.add(t);
/*      */       }
/*      */     } 
/*  129 */     return list;
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
/*      */   @SafeVarargs
/*      */   public static <T> Collection<T> union(Collection<T> coll1, Collection<T> coll2, Collection<T>... otherColls) {
/*  146 */     Collection<T> union = union(coll1, coll2);
/*  147 */     for (Collection<T> coll : otherColls) {
/*  148 */       union = union(union, coll);
/*      */     }
/*  150 */     return union;
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
/*      */   @SafeVarargs
/*      */   public static <T> Set<T> unionDistinct(Collection<T> coll1, Collection<T> coll2, Collection<T>... otherColls) {
/*      */     Set<T> result;
/*  168 */     if (isEmpty(coll1)) {
/*  169 */       result = new LinkedHashSet<>();
/*      */     } else {
/*  171 */       result = new LinkedHashSet<>(coll1);
/*      */     } 
/*      */     
/*  174 */     if (isNotEmpty(coll2)) {
/*  175 */       result.addAll(coll2);
/*      */     }
/*      */     
/*  178 */     if (ArrayUtil.isNotEmpty((Object[])otherColls)) {
/*  179 */       for (Collection<T> otherColl : otherColls) {
/*  180 */         result.addAll(otherColl);
/*      */       }
/*      */     }
/*      */     
/*  184 */     return result;
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
/*      */   @SafeVarargs
/*      */   public static <T> List<T> unionAll(Collection<T> coll1, Collection<T> coll2, Collection<T>... otherColls) {
/*      */     List<T> result;
/*  202 */     if (isEmpty(coll1)) {
/*  203 */       result = new ArrayList<>();
/*      */     } else {
/*  205 */       result = new ArrayList<>(coll1);
/*      */     } 
/*      */     
/*  208 */     if (isNotEmpty(coll2)) {
/*  209 */       result.addAll(coll2);
/*      */     }
/*      */     
/*  212 */     if (ArrayUtil.isNotEmpty((Object[])otherColls)) {
/*  213 */       for (Collection<T> otherColl : otherColls) {
/*  214 */         result.addAll(otherColl);
/*      */       }
/*      */     }
/*      */     
/*  218 */     return result;
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
/*      */   public static <T> Collection<T> intersection(Collection<T> coll1, Collection<T> coll2) {
/*  233 */     if (isNotEmpty(coll1) && isNotEmpty(coll2)) {
/*  234 */       ArrayList<T> list = new ArrayList<>(Math.min(coll1.size(), coll2.size()));
/*  235 */       Map<T, Integer> map1 = countMap(coll1);
/*  236 */       Map<T, Integer> map2 = countMap(coll2);
/*  237 */       Set<T> elts = newHashSet(coll2);
/*      */       
/*  239 */       for (T t : elts) {
/*  240 */         int m = Math.min(Convert.toInt(map1.get(t), Integer.valueOf(0)).intValue(), Convert.toInt(map2.get(t), Integer.valueOf(0)).intValue());
/*  241 */         for (int i = 0; i < m; i++) {
/*  242 */           list.add(t);
/*      */         }
/*      */       } 
/*  245 */       return list;
/*      */     } 
/*      */     
/*  248 */     return new ArrayList<>();
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
/*      */   @SafeVarargs
/*      */   public static <T> Collection<T> intersection(Collection<T> coll1, Collection<T> coll2, Collection<T>... otherColls) {
/*  265 */     Collection<T> intersection = intersection(coll1, coll2);
/*  266 */     if (isEmpty(intersection)) {
/*  267 */       return intersection;
/*      */     }
/*  269 */     for (Collection<T> coll : otherColls) {
/*  270 */       intersection = intersection(intersection, coll);
/*  271 */       if (isEmpty(intersection)) {
/*  272 */         return intersection;
/*      */       }
/*      */     } 
/*  275 */     return intersection;
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
/*      */   @SafeVarargs
/*      */   public static <T> Set<T> intersectionDistinct(Collection<T> coll1, Collection<T> coll2, Collection<T>... otherColls) {
/*  294 */     if (isEmpty(coll1) || isEmpty(coll2))
/*      */     {
/*  296 */       return new LinkedHashSet<>();
/*      */     }
/*  298 */     Set<T> result = new LinkedHashSet<>(coll1);
/*      */ 
/*      */     
/*  301 */     if (ArrayUtil.isNotEmpty((Object[])otherColls)) {
/*  302 */       for (Collection<T> otherColl : otherColls) {
/*  303 */         if (isNotEmpty(otherColl)) {
/*  304 */           result.retainAll(otherColl);
/*      */         } else {
/*      */           
/*  307 */           return new LinkedHashSet<>();
/*      */         } 
/*      */       } 
/*      */     }
/*      */     
/*  312 */     result.retainAll(coll2);
/*      */     
/*  314 */     return result;
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
/*      */   public static <T> Collection<T> disjunction(Collection<T> coll1, Collection<T> coll2) {
/*  336 */     if (isEmpty(coll1)) {
/*  337 */       return coll2;
/*      */     }
/*  339 */     if (isEmpty(coll2)) {
/*  340 */       return coll1;
/*      */     }
/*      */     
/*  343 */     List<T> result = new ArrayList<>();
/*  344 */     Map<T, Integer> map1 = countMap(coll1);
/*  345 */     Map<T, Integer> map2 = countMap(coll2);
/*  346 */     Set<T> elts = newHashSet(coll2);
/*  347 */     elts.addAll(coll1);
/*      */     
/*  349 */     for (T t : elts) {
/*  350 */       int m = Math.abs(Convert.toInt(map1.get(t), Integer.valueOf(0)).intValue() - Convert.toInt(map2.get(t), Integer.valueOf(0)).intValue());
/*  351 */       for (int i = 0; i < m; i++) {
/*  352 */         result.add(t);
/*      */       }
/*      */     } 
/*  355 */     return result;
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
/*      */   public static <T> Collection<T> subtract(Collection<T> coll1, Collection<T> coll2) {
/*  371 */     Collection<T> result = (Collection<T>)ObjectUtil.clone(coll1);
/*  372 */     if (null == result) {
/*  373 */       result = create(coll1.getClass());
/*  374 */       result.addAll(coll1);
/*      */     } 
/*  376 */     result.removeAll(coll2);
/*  377 */     return result;
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
/*      */   public static <T> List<T> subtractToList(Collection<T> coll1, Collection<T> coll2) {
/*  395 */     if (isEmpty(coll1)) {
/*  396 */       return ListUtil.empty();
/*      */     }
/*  398 */     if (isEmpty(coll2)) {
/*  399 */       return ListUtil.list(true, coll1);
/*      */     }
/*      */ 
/*      */     
/*  403 */     List<T> result = new LinkedList<>();
/*  404 */     Set<T> set = new HashSet<>(coll2);
/*  405 */     for (T t : coll1) {
/*  406 */       if (false == set.contains(t)) {
/*  407 */         result.add(t);
/*      */       }
/*      */     } 
/*  410 */     return result;
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
/*      */   public static boolean contains(Collection<?> collection, Object value) {
/*  425 */     return (isNotEmpty(collection) && collection.contains(value));
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
/*      */   public static boolean safeContains(Collection<?> collection, Object value) {
/*      */     try {
/*  439 */       return contains(collection, value);
/*  440 */     } catch (ClassCastException|NullPointerException e) {
/*  441 */       return false;
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
/*      */   public static <T> boolean contains(Collection<T> collection, Predicate<? super T> containFunc) {
/*  455 */     if (isEmpty(collection)) {
/*  456 */       return false;
/*      */     }
/*  458 */     for (T t : collection) {
/*  459 */       if (containFunc.test(t)) {
/*  460 */         return true;
/*      */       }
/*      */     } 
/*  463 */     return false;
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
/*      */   public static boolean containsAny(Collection<?> coll1, Collection<?> coll2) {
/*  476 */     if (isEmpty(coll1) || isEmpty(coll2)) {
/*  477 */       return false;
/*      */     }
/*  479 */     if (coll1.size() < coll2.size()) {
/*  480 */       for (Object object : coll1) {
/*  481 */         if (coll2.contains(object)) {
/*  482 */           return true;
/*      */         }
/*      */       } 
/*      */     } else {
/*  486 */       for (Object object : coll2) {
/*  487 */         if (coll1.contains(object)) {
/*  488 */           return true;
/*      */         }
/*      */       } 
/*      */     } 
/*  492 */     return false;
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
/*      */   public static boolean containsAll(Collection<?> coll1, Collection<?> coll2) {
/*  504 */     if (isEmpty(coll1)) {
/*  505 */       return isEmpty(coll2);
/*      */     }
/*      */     
/*  508 */     if (isEmpty(coll2)) {
/*  509 */       return true;
/*      */     }
/*      */     
/*  512 */     if (coll1.size() < coll2.size()) {
/*  513 */       return false;
/*      */     }
/*      */     
/*  516 */     for (Object object : coll2) {
/*  517 */       if (false == coll1.contains(object)) {
/*  518 */         return false;
/*      */       }
/*      */     } 
/*  521 */     return true;
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
/*      */   public static <T> Map<T, Integer> countMap(Iterable<T> collection) {
/*  538 */     return IterUtil.countMap((null == collection) ? null : collection.iterator());
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
/*      */   public static <T> String join(Iterable<T> iterable, CharSequence conjunction, Function<T, ? extends CharSequence> func) {
/*  553 */     if (null == iterable) {
/*  554 */       return null;
/*      */     }
/*  556 */     return IterUtil.join(iterable.iterator(), conjunction, func);
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
/*      */   public static <T> String join(Iterable<T> iterable, CharSequence conjunction) {
/*  570 */     if (null == iterable) {
/*  571 */       return null;
/*      */     }
/*  573 */     return IterUtil.join(iterable.iterator(), conjunction);
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
/*      */   public static <T> String join(Iterable<T> iterable, CharSequence conjunction, String prefix, String suffix) {
/*  588 */     if (null == iterable) {
/*  589 */       return null;
/*      */     }
/*  591 */     return IterUtil.join(iterable.iterator(), conjunction, prefix, suffix);
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
/*      */   @Deprecated
/*      */   public static <T> String join(Iterator<T> iterator, CharSequence conjunction) {
/*  606 */     return IterUtil.join(iterator, conjunction);
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
/*      */   public static <T> List<T> popPart(Stack<T> surplusAlaDatas, int partSize) {
/*  619 */     if (isEmpty(surplusAlaDatas)) {
/*  620 */       return ListUtil.empty();
/*      */     }
/*      */     
/*  623 */     List<T> currentAlaDatas = new ArrayList<>();
/*  624 */     int size = surplusAlaDatas.size();
/*      */     
/*  626 */     if (size > partSize) {
/*  627 */       for (int i = 0; i < partSize; i++) {
/*  628 */         currentAlaDatas.add(surplusAlaDatas.pop());
/*      */       }
/*      */     } else {
/*  631 */       for (int i = 0; i < size; i++) {
/*  632 */         currentAlaDatas.add(surplusAlaDatas.pop());
/*      */       }
/*      */     } 
/*  635 */     return currentAlaDatas;
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
/*      */   public static <T> List<T> popPart(Deque<T> surplusAlaDatas, int partSize) {
/*  648 */     if (isEmpty(surplusAlaDatas)) {
/*  649 */       return ListUtil.empty();
/*      */     }
/*      */     
/*  652 */     List<T> currentAlaDatas = new ArrayList<>();
/*  653 */     int size = surplusAlaDatas.size();
/*      */     
/*  655 */     if (size > partSize) {
/*  656 */       for (int i = 0; i < partSize; i++) {
/*  657 */         currentAlaDatas.add(surplusAlaDatas.pop());
/*      */       }
/*      */     } else {
/*  660 */       for (int i = 0; i < size; i++) {
/*  661 */         currentAlaDatas.add(surplusAlaDatas.pop());
/*      */       }
/*      */     } 
/*  664 */     return currentAlaDatas;
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
/*      */   public static <T> HashSet<T> newHashSet(T... ts) {
/*  678 */     return set(false, ts);
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
/*      */   public static <T> LinkedHashSet<T> newLinkedHashSet(T... ts) {
/*  691 */     return (LinkedHashSet<T>)set(true, ts);
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
/*      */   public static <T> HashSet<T> set(boolean isSorted, T... ts) {
/*  704 */     if (null == ts) {
/*  705 */       return isSorted ? new LinkedHashSet<>() : new HashSet<>();
/*      */     }
/*  707 */     int initialCapacity = Math.max((int)(ts.length / 0.75F) + 1, 16);
/*  708 */     HashSet<T> set = isSorted ? new LinkedHashSet<>(initialCapacity) : new HashSet<>(initialCapacity);
/*  709 */     Collections.addAll(set, ts);
/*  710 */     return set;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <T> HashSet<T> newHashSet(Collection<T> collection) {
/*  721 */     return newHashSet(false, collection);
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
/*      */   public static <T> HashSet<T> newHashSet(boolean isSorted, Collection<T> collection) {
/*  733 */     return isSorted ? new LinkedHashSet<>(collection) : new HashSet<>(collection);
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
/*      */   public static <T> HashSet<T> newHashSet(boolean isSorted, Iterator<T> iter) {
/*  746 */     if (null == iter) {
/*  747 */       return set(isSorted, (T[])null);
/*      */     }
/*  749 */     HashSet<T> set = isSorted ? new LinkedHashSet<>() : new HashSet<>();
/*  750 */     while (iter.hasNext()) {
/*  751 */       set.add(iter.next());
/*      */     }
/*  753 */     return set;
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
/*      */   public static <T> HashSet<T> newHashSet(boolean isSorted, Enumeration<T> enumeration) {
/*  766 */     if (null == enumeration) {
/*  767 */       return set(isSorted, (T[])null);
/*      */     }
/*  769 */     HashSet<T> set = isSorted ? new LinkedHashSet<>() : new HashSet<>();
/*  770 */     while (enumeration.hasMoreElements()) {
/*  771 */       set.add(enumeration.nextElement());
/*      */     }
/*  773 */     return set;
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
/*      */   public static <T> List<T> list(boolean isLinked) {
/*  787 */     return ListUtil.list(isLinked);
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
/*      */   public static <T> List<T> list(boolean isLinked, T... values) {
/*  801 */     return ListUtil.list(isLinked, values);
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
/*      */   public static <T> List<T> list(boolean isLinked, Collection<T> collection) {
/*  814 */     return ListUtil.list(isLinked, collection);
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
/*      */   public static <T> List<T> list(boolean isLinked, Iterable<T> iterable) {
/*  828 */     return ListUtil.list(isLinked, iterable);
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
/*      */   public static <T> List<T> list(boolean isLinked, Iterator<T> iter) {
/*  842 */     return ListUtil.list(isLinked, iter);
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
/*      */   public static <T> List<T> list(boolean isLinked, Enumeration<T> enumeration) {
/*  856 */     return ListUtil.list(isLinked, enumeration);
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
/*      */   public static <T> ArrayList<T> newArrayList(T... values) {
/*  869 */     return ListUtil.toList(values);
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
/*      */   public static <T> ArrayList<T> toList(T... values) {
/*  882 */     return ListUtil.toList(values);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <T> ArrayList<T> newArrayList(Collection<T> collection) {
/*  893 */     return ListUtil.toList(collection);
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
/*      */   public static <T> ArrayList<T> newArrayList(Iterable<T> iterable) {
/*  906 */     return ListUtil.toList(iterable);
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
/*      */   public static <T> ArrayList<T> newArrayList(Iterator<T> iterator) {
/*  919 */     return ListUtil.toList(iterator);
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
/*      */   public static <T> ArrayList<T> newArrayList(Enumeration<T> enumeration) {
/*  932 */     return ListUtil.toList(enumeration);
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
/*      */   public static <T> LinkedList<T> newLinkedList(T... values) {
/*  947 */     return ListUtil.toLinkedList(values);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <T> CopyOnWriteArrayList<T> newCopyOnWriteArrayList(Collection<T> collection) {
/*  958 */     return ListUtil.toCopyOnWriteArrayList(collection);
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
/*      */   public static <T> BlockingQueue<T> newBlockingQueue(int capacity, boolean isLinked) {
/*      */     BlockingQueue<T> queue;
/*  973 */     if (isLinked) {
/*  974 */       queue = new LinkedBlockingDeque<>(capacity);
/*      */     } else {
/*  976 */       queue = new ArrayBlockingQueue<>(capacity);
/*      */     } 
/*  978 */     return queue;
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
/*      */   public static <T> Collection<T> create(Class<?> collectionType) {
/*      */     Collection<T> list;
/*  992 */     if (collectionType.isAssignableFrom(AbstractCollection.class)) {
/*      */       
/*  994 */       list = new ArrayList<>();
/*      */ 
/*      */     
/*      */     }
/*  998 */     else if (collectionType.isAssignableFrom(HashSet.class)) {
/*  999 */       list = new HashSet<>();
/* 1000 */     } else if (collectionType.isAssignableFrom(LinkedHashSet.class)) {
/* 1001 */       list = new LinkedHashSet<>();
/* 1002 */     } else if (collectionType.isAssignableFrom(TreeSet.class)) {
/* 1003 */       list = new TreeSet<>((o1, o2) -> (o1 instanceof Comparable) ? ((Comparable<Object>)o1).compareTo(o2) : CompareUtil.compare(o1.toString(), o2.toString()));
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     }
/* 1010 */     else if (collectionType.isAssignableFrom(EnumSet.class)) {
/* 1011 */       list = EnumSet.noneOf(ClassUtil.getTypeArgument(collectionType));
/*      */ 
/*      */     
/*      */     }
/* 1015 */     else if (collectionType.isAssignableFrom(ArrayList.class)) {
/* 1016 */       list = new ArrayList<>();
/* 1017 */     } else if (collectionType.isAssignableFrom(LinkedList.class)) {
/* 1018 */       list = new LinkedList<>();
/*      */     } else {
/*      */ 
/*      */       
/*      */       try {
/*      */         
/* 1024 */         list = (Collection<T>)ReflectUtil.newInstance(collectionType, new Object[0]);
/* 1025 */       } catch (Exception e) {
/*      */         
/* 1027 */         Class<?> superclass = collectionType.getSuperclass();
/* 1028 */         if (null != superclass && collectionType != superclass) {
/* 1029 */           return create(superclass);
/*      */         }
/* 1031 */         throw new UtilException(e);
/*      */       } 
/*      */     } 
/* 1034 */     return list;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <T> ArrayList<T> distinct(Collection<T> collection) {
/* 1045 */     if (isEmpty(collection))
/* 1046 */       return new ArrayList<>(); 
/* 1047 */     if (collection instanceof Set) {
/* 1048 */       return new ArrayList<>(collection);
/*      */     }
/* 1050 */     return new ArrayList<>(new LinkedHashSet<>(collection));
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
/*      */   public static <T, K> List<T> distinct(Collection<T> collection, Function<T, K> uniqueGenerator, boolean override) {
/* 1067 */     if (isEmpty(collection)) {
/* 1068 */       return new ArrayList<>();
/*      */     }
/*      */     
/* 1071 */     UniqueKeySet<K, T> set = new UniqueKeySet<>(true, uniqueGenerator);
/* 1072 */     if (override) {
/* 1073 */       set.addAll(collection);
/*      */     } else {
/* 1075 */       set.addAllIfAbsent(collection);
/*      */     } 
/* 1077 */     return new ArrayList<>(set);
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
/*      */   public static <T> List<T> sub(List<T> list, int start, int end) {
/* 1091 */     return ListUtil.sub(list, start, end);
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
/*      */   public static <T> List<T> sub(List<T> list, int start, int end, int step) {
/* 1107 */     return ListUtil.sub(list, start, end, step);
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
/*      */   public static <T> List<T> sub(Collection<T> collection, int start, int end) {
/* 1120 */     return sub(collection, start, end, 1);
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
/*      */   public static <T> List<T> sub(Collection<T> collection, int start, int end, int step) {
/* 1135 */     if (isEmpty(collection)) {
/* 1136 */       return ListUtil.empty();
/*      */     }
/*      */     
/* 1139 */     List<T> list = (collection instanceof List) ? (List<T>)collection : ListUtil.<T>toList(collection);
/* 1140 */     return sub(list, start, end, step);
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
/*      */   @Deprecated
/*      */   public static <T> List<List<T>> splitList(List<T> list, int size) {
/* 1159 */     return ListUtil.partition(list, size);
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
/*      */   public static <T> List<List<T>> split(Collection<T> collection, int size) {
/* 1171 */     List<List<T>> result = new ArrayList<>();
/* 1172 */     if (isEmpty(collection)) {
/* 1173 */       return result;
/*      */     }
/*      */     
/* 1176 */     ArrayList<T> subList = new ArrayList<>(size);
/* 1177 */     for (T t : collection) {
/* 1178 */       if (subList.size() >= size) {
/* 1179 */         result.add(subList);
/* 1180 */         subList = new ArrayList<>(size);
/*      */       } 
/* 1182 */       subList.add(t);
/*      */     } 
/* 1184 */     result.add(subList);
/* 1185 */     return result;
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
/*      */   public static <T> Collection<T> edit(Collection<T> collection, Editor<T> editor) {
/* 1203 */     if (null == collection || null == editor) {
/* 1204 */       return collection;
/*      */     }
/*      */     
/* 1207 */     Collection<T> collection2 = create(collection.getClass());
/* 1208 */     if (isEmpty(collection)) {
/* 1209 */       return collection2;
/*      */     }
/*      */ 
/*      */     
/* 1213 */     for (T t : collection) {
/* 1214 */       T modified = (T)editor.edit(t);
/* 1215 */       if (null != modified) {
/* 1216 */         collection2.add(modified);
/*      */       }
/*      */     } 
/* 1219 */     return collection2;
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
/*      */   public static <T> Collection<T> filterNew(Collection<T> collection, Filter<T> filter) {
/* 1237 */     if (null == collection || null == filter) {
/* 1238 */       return collection;
/*      */     }
/* 1240 */     return edit(collection, t -> filter.accept(t) ? t : null);
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
/*      */   public static <T extends Collection<E>, E> T removeAny(T collection, E... elesRemoved) {
/* 1255 */     collection.removeAll(newHashSet((Object[])elesRemoved));
/* 1256 */     return collection;
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
/*      */   public static <T extends Collection<E>, E> T filter(T collection, Filter<E> filter) {
/* 1270 */     return (T)IterUtil.<Collection, E>filter((Collection)collection, filter);
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
/*      */   public static <T extends Collection<E>, E> T removeNull(T collection) {
/* 1283 */     return filter(collection, Objects::nonNull);
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
/*      */   public static <T extends Collection<E>, E extends CharSequence> T removeEmpty(T collection) {
/* 1296 */     return filter(collection, CharSequenceUtil::isNotEmpty);
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
/*      */   public static <T extends Collection<E>, E extends CharSequence> T removeBlank(T collection) {
/* 1309 */     return filter(collection, CharSequenceUtil::isNotBlank);
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
/*      */   public static <T extends Collection<E>, E> T removeWithAddIf(T targetCollection, T resultCollection, Predicate<? super E> predicate) {
/* 1325 */     Objects.requireNonNull(predicate);
/* 1326 */     Iterator<E> each = targetCollection.iterator();
/* 1327 */     while (each.hasNext()) {
/* 1328 */       E next = each.next();
/* 1329 */       if (predicate.test(next)) {
/* 1330 */         resultCollection.add(next);
/* 1331 */         each.remove();
/*      */       } 
/*      */     } 
/* 1334 */     return resultCollection;
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
/*      */   public static <T extends Collection<E>, E> List<E> removeWithAddIf(T targetCollection, Predicate<? super E> predicate) {
/* 1349 */     List<E> removed = new ArrayList<>();
/* 1350 */     removeWithAddIf(targetCollection, (T)removed, predicate);
/* 1351 */     return removed;
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
/*      */   public static List<Object> extract(Iterable<?> collection, Editor<Object> editor) {
/* 1363 */     return extract(collection, editor, false);
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
/*      */   public static List<Object> extract(Iterable<?> collection, Editor<Object> editor, boolean ignoreNull) {
/* 1378 */     return map(collection, editor::edit, ignoreNull);
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
/*      */   public static <T, R> List<R> map(Iterable<T> collection, Function<? super T, ? extends R> func, boolean ignoreNull) {
/* 1394 */     List<R> fieldValueList = new ArrayList<>();
/* 1395 */     if (null == collection) {
/* 1396 */       return fieldValueList;
/*      */     }
/*      */ 
/*      */     
/* 1400 */     for (T t : collection) {
/* 1401 */       if (null == t && ignoreNull) {
/*      */         continue;
/*      */       }
/* 1404 */       R value = func.apply(t);
/* 1405 */       if (null == value && ignoreNull) {
/*      */         continue;
/*      */       }
/* 1408 */       fieldValueList.add(value);
/*      */     } 
/* 1410 */     return fieldValueList;
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
/*      */   public static List<Object> getFieldValues(Iterable<?> collection, String fieldName) {
/* 1423 */     return getFieldValues(collection, fieldName, false);
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
/*      */   public static List<Object> getFieldValues(Iterable<?> collection, String fieldName, boolean ignoreNull) {
/* 1437 */     return map(collection, bean -> (bean instanceof Map) ? ((Map)bean).get(fieldName) : ReflectUtil.getFieldValue(bean, fieldName), ignoreNull);
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
/*      */   public static <T> List<T> getFieldValues(Iterable<?> collection, String fieldName, Class<T> elementType) {
/* 1458 */     List<Object> fieldValues = getFieldValues(collection, fieldName);
/* 1459 */     return Convert.toList(elementType, fieldValues);
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
/*      */   public static <K, V> Map<K, V> fieldValueMap(Iterable<V> iterable, String fieldName) {
/* 1474 */     return IterUtil.fieldValueMap(IterUtil.getIter(iterable), fieldName);
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
/*      */   public static <K, V> Map<K, V> fieldValueAsMap(Iterable<?> iterable, String fieldNameForKey, String fieldNameForValue) {
/* 1489 */     return IterUtil.fieldValueAsMap(IterUtil.getIter(iterable), fieldNameForKey, fieldNameForValue);
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
/*      */   public static <T> T findOne(Iterable<T> collection, Filter<T> filter) {
/* 1502 */     if (null != collection) {
/* 1503 */       for (T t : collection) {
/* 1504 */         if (filter.accept(t)) {
/* 1505 */           return t;
/*      */         }
/*      */       } 
/*      */     }
/* 1509 */     return null;
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
/*      */   public static <T> T findOneByField(Iterable<T> collection, String fieldName, Object fieldValue) {
/* 1526 */     return findOne(collection, t -> {
/*      */           if (t instanceof Map) {
/*      */             Map<?, ?> map = (Map<?, ?>)t;
/*      */             Object object = map.get(fieldName);
/*      */             return ObjectUtil.equal(object, fieldValue);
/*      */           } 
/*      */           Object value = ReflectUtil.getFieldValue(t, fieldName);
/*      */           return ObjectUtil.equal(value, fieldValue);
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
/*      */   public static <T> int count(Iterable<T> iterable, Matcher<T> matcher) {
/* 1548 */     int count = 0;
/* 1549 */     if (null != iterable) {
/* 1550 */       for (T t : iterable) {
/* 1551 */         if (null == matcher || matcher.match(t)) {
/* 1552 */           count++;
/*      */         }
/*      */       } 
/*      */     }
/* 1556 */     return count;
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
/*      */   public static <T> int indexOf(Collection<T> collection, Matcher<T> matcher) {
/* 1570 */     if (isNotEmpty(collection)) {
/* 1571 */       int index = 0;
/* 1572 */       for (T t : collection) {
/* 1573 */         if (null == matcher || matcher.match(t)) {
/* 1574 */           return index;
/*      */         }
/* 1576 */         index++;
/*      */       } 
/*      */     } 
/* 1579 */     return -1;
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
/*      */   public static <T> int lastIndexOf(Collection<T> collection, Matcher<T> matcher) {
/* 1593 */     if (collection instanceof List)
/*      */     {
/* 1595 */       return ListUtil.lastIndexOf((List<T>)collection, matcher);
/*      */     }
/* 1597 */     int matchIndex = -1;
/* 1598 */     if (isNotEmpty(collection)) {
/* 1599 */       int index = collection.size();
/* 1600 */       for (T t : collection) {
/* 1601 */         if (null == matcher || matcher.match(t)) {
/* 1602 */           matchIndex = index;
/*      */         }
/* 1604 */         index--;
/*      */       } 
/*      */     } 
/* 1607 */     return matchIndex;
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
/*      */   public static <T> int[] indexOfAll(Collection<T> collection, Matcher<T> matcher) {
/* 1621 */     List<Integer> indexList = new ArrayList<>();
/* 1622 */     if (null != collection) {
/* 1623 */       int index = 0;
/* 1624 */       for (T t : collection) {
/* 1625 */         if (null == matcher || matcher.match(t)) {
/* 1626 */           indexList.add(Integer.valueOf(index));
/*      */         }
/* 1628 */         index++;
/*      */       } 
/*      */     } 
/* 1631 */     return (int[])Convert.convert(int[].class, indexList);
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
/*      */   public static boolean isEmpty(Collection<?> collection) {
/* 1643 */     return (collection == null || collection.isEmpty());
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
/*      */   public static <T extends Collection<E>, E> T defaultIfEmpty(T collection, T defaultCollection) {
/* 1657 */     return isEmpty((Collection<?>)collection) ? defaultCollection : collection;
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
/*      */   public static <T extends Collection<E>, E> T defaultIfEmpty(T collection, Supplier<? extends T> supplier) {
/* 1671 */     return isEmpty((Collection<?>)collection) ? supplier.get() : collection;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean isEmpty(Iterable<?> iterable) {
/* 1682 */     return IterUtil.isEmpty(iterable);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean isEmpty(Iterator<?> iterator) {
/* 1693 */     return IterUtil.isEmpty(iterator);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean isEmpty(Enumeration<?> enumeration) {
/* 1703 */     return (null == enumeration || false == enumeration.hasMoreElements());
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
/*      */   public static boolean isEmpty(Map<?, ?> map) {
/* 1715 */     return MapUtil.isEmpty(map);
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
/*      */   public static boolean isNotEmpty(Collection<?> collection) {
/* 1727 */     return (false == isEmpty(collection));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean isNotEmpty(Iterable<?> iterable) {
/* 1738 */     return IterUtil.isNotEmpty(iterable);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean isNotEmpty(Iterator<?> iterator) {
/* 1749 */     return IterUtil.isNotEmpty(iterator);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean isNotEmpty(Enumeration<?> enumeration) {
/* 1759 */     return (null != enumeration && enumeration.hasMoreElements());
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
/*      */   public static boolean hasNull(Iterable<?> iterable) {
/* 1771 */     return IterUtil.hasNull(iterable);
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
/*      */   public static boolean isNotEmpty(Map<?, ?> map) {
/* 1783 */     return MapUtil.isNotEmpty(map);
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
/*      */   public static Map<String, String> zip(String keys, String values, String delimiter, boolean isOrder) {
/* 1804 */     return ArrayUtil.zip((Object[])StrUtil.splitToArray(keys, delimiter), (Object[])StrUtil.splitToArray(values, delimiter), isOrder);
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
/*      */   public static Map<String, String> zip(String keys, String values, String delimiter) {
/* 1821 */     return zip(keys, values, delimiter, false);
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
/*      */   public static <K, V> Map<K, V> zip(Collection<K> keys, Collection<V> values) {
/* 1839 */     if (isEmpty(keys) || isEmpty(values)) {
/* 1840 */       return MapUtil.empty();
/*      */     }
/*      */     
/* 1843 */     int entryCount = Math.min(keys.size(), values.size());
/* 1844 */     Map<K, V> map = MapUtil.newHashMap(entryCount);
/*      */     
/* 1846 */     Iterator<K> keyIterator = keys.iterator();
/* 1847 */     Iterator<V> valueIterator = values.iterator();
/* 1848 */     while (entryCount > 0) {
/* 1849 */       map.put(keyIterator.next(), valueIterator.next());
/* 1850 */       entryCount--;
/*      */     } 
/*      */     
/* 1853 */     return map;
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
/*      */   public static <K, V> HashMap<K, V> toMap(Iterable<Map.Entry<K, V>> entryIter) {
/* 1866 */     return IterUtil.toMap(entryIter);
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
/*      */   public static HashMap<Object, Object> toMap(Object[] array) {
/* 1894 */     return MapUtil.of(array);
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
/*      */   public static <T> TreeSet<T> toTreeSet(Collection<T> collection, Comparator<T> comparator) {
/* 1906 */     TreeSet<T> treeSet = new TreeSet<>(comparator);
/* 1907 */     treeSet.addAll(collection);
/* 1908 */     return treeSet;
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
/*      */   public static <E> Enumeration<E> asEnumeration(Iterator<E> iter) {
/* 1921 */     return new IteratorEnumeration<>(iter);
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
/*      */   public static <E> Iterator<E> asIterator(Enumeration<E> e) {
/* 1935 */     return IterUtil.asIterator(e);
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
/*      */   public static <E> Iterable<E> asIterable(Iterator<E> iter) {
/* 1947 */     return IterUtil.asIterable(iter);
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
/*      */   public static <E> Collection<E> toCollection(Iterable<E> iterable) {
/* 1960 */     return (iterable instanceof Collection) ? (Collection<E>)iterable : newArrayList(iterable.iterator());
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
/*      */   
/*      */   public static <K, V> Map<K, List<V>> toListMap(Iterable<? extends Map<K, V>> mapList) {
/* 1995 */     return MapUtil.toListMap(mapList);
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
/*      */   public static <K, V> List<Map<K, V>> toMapList(Map<K, ? extends Iterable<V>> listMap) {
/* 2029 */     return MapUtil.toMapList(listMap);
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
/*      */   public static <K, V> Map<K, V> toMap(Iterable<V> values, Map<K, V> map, Func1<V, K> keyFunc) {
/* 2045 */     return IterUtil.toMap((null == values) ? null : values.iterator(), map, keyFunc);
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
/*      */   public static <K, V, E> Map<K, V> toMap(Iterable<E> values, Map<K, V> map, Func1<E, K> keyFunc, Func1<E, V> valueFunc) {
/* 2063 */     return IterUtil.toMap((null == values) ? null : values.iterator(), map, keyFunc, valueFunc);
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
/*      */   public static <T> Collection<T> addAll(Collection<T> collection, Object value) {
/* 2076 */     return addAll(collection, value, TypeUtil.getTypeArgument(collection.getClass()));
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
/*      */   public static <T> Collection<T> addAll(Collection<T> collection, Object value, Type<Object> elementType) {
/*      */     Iterator iter;
/* 2092 */     if (null == collection || null == value) {
/* 2093 */       return collection;
/*      */     }
/* 2095 */     if (TypeUtil.isUnknown(elementType))
/*      */     {
/* 2097 */       elementType = Object.class;
/*      */     }
/*      */ 
/*      */     
/* 2101 */     if (value instanceof Iterator) {
/* 2102 */       iter = (Iterator)value;
/* 2103 */     } else if (value instanceof Iterable) {
/* 2104 */       iter = ((Iterable)value).iterator();
/* 2105 */     } else if (value instanceof Enumeration) {
/* 2106 */       iter = new EnumerationIter((Enumeration)value);
/* 2107 */     } else if (ArrayUtil.isArray(value)) {
/* 2108 */       iter = new ArrayIter(value);
/* 2109 */     } else if (value instanceof CharSequence) {
/*      */       
/* 2111 */       String ArrayStr = StrUtil.unWrap((CharSequence)value, '[', ']');
/* 2112 */       iter = StrUtil.splitTrim(ArrayStr, ',').iterator();
/*      */     } else {
/*      */       
/* 2115 */       iter = newArrayList(new Object[] { value }).iterator();
/*      */     } 
/*      */     
/* 2118 */     ConverterRegistry convert = ConverterRegistry.getInstance();
/* 2119 */     while (iter.hasNext()) {
/* 2120 */       collection.add((T)convert.convert(elementType, iter.next()));
/*      */     }
/*      */     
/* 2123 */     return collection;
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
/*      */   public static <T> Collection<T> addAll(Collection<T> collection, Iterator<T> iterator) {
/* 2135 */     if (null != collection && null != iterator) {
/* 2136 */       while (iterator.hasNext()) {
/* 2137 */         collection.add(iterator.next());
/*      */       }
/*      */     }
/* 2140 */     return collection;
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
/*      */   public static <T> Collection<T> addAll(Collection<T> collection, Iterable<T> iterable) {
/* 2152 */     if (iterable == null) {
/* 2153 */       return collection;
/*      */     }
/* 2155 */     return addAll(collection, iterable.iterator());
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
/*      */   public static <T> Collection<T> addAll(Collection<T> collection, Enumeration<T> enumeration) {
/* 2167 */     if (null != collection && null != enumeration) {
/* 2168 */       while (enumeration.hasMoreElements()) {
/* 2169 */         collection.add(enumeration.nextElement());
/*      */       }
/*      */     }
/* 2172 */     return collection;
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
/*      */   public static <T> Collection<T> addAll(Collection<T> collection, T[] values) {
/* 2185 */     if (null != collection && null != values) {
/* 2186 */       Collections.addAll(collection, values);
/*      */     }
/* 2188 */     return collection;
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
/*      */   public static <T> List<T> addAllIfNotContains(List<T> list, List<T> otherList) {
/* 2200 */     for (T t : otherList) {
/* 2201 */       if (false == list.contains(t)) {
/* 2202 */         list.add(t);
/*      */       }
/*      */     } 
/* 2205 */     return list;
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
/*      */   public static <T> T get(Collection<T> collection, int index) {
/* 2219 */     if (null == collection) {
/* 2220 */       return null;
/*      */     }
/*      */     
/* 2223 */     int size = collection.size();
/* 2224 */     if (0 == size) {
/* 2225 */       return null;
/*      */     }
/*      */     
/* 2228 */     if (index < 0) {
/* 2229 */       index += size;
/*      */     }
/*      */ 
/*      */     
/* 2233 */     if (index >= size || index < 0) {
/* 2234 */       return null;
/*      */     }
/*      */     
/* 2237 */     if (collection instanceof List) {
/* 2238 */       List<T> list = (List<T>)collection;
/* 2239 */       return list.get(index);
/*      */     } 
/* 2241 */     return IterUtil.get(collection.iterator(), index);
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
/*      */   public static <T> List<T> getAny(Collection<T> collection, int... indexes) {
/* 2256 */     int size = collection.size();
/* 2257 */     ArrayList<T> result = new ArrayList<>();
/* 2258 */     if (collection instanceof List) {
/* 2259 */       List<T> list = (List<T>)collection;
/* 2260 */       for (int index : indexes) {
/* 2261 */         if (index < 0) {
/* 2262 */           index += size;
/*      */         }
/* 2264 */         result.add(list.get(index));
/*      */       } 
/*      */     } else {
/* 2267 */       Object[] array = collection.toArray();
/* 2268 */       for (int index : indexes) {
/* 2269 */         if (index < 0) {
/* 2270 */           index += size;
/*      */         }
/* 2272 */         result.add((T)array[index]);
/*      */       } 
/*      */     } 
/* 2275 */     return result;
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
/*      */   public static <T> T getFirst(Iterable<T> iterable) {
/* 2288 */     return IterUtil.getFirst(iterable);
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
/*      */   public static <T> T getFirst(Iterator<T> iterator) {
/* 2301 */     return IterUtil.getFirst(iterator);
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
/*      */   public static <T> T getLast(Collection<T> collection) {
/* 2313 */     return get(collection, -1);
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
/*      */   @Deprecated
/*      */   public static Class<?> getElementType(Iterable<?> iterable) {
/* 2327 */     return IterUtil.getElementType(iterable);
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
/*      */   @Deprecated
/*      */   public static Class<?> getElementType(Iterator<?> iterator) {
/* 2341 */     return IterUtil.getElementType(iterator);
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
/*      */   public static <K, V> ArrayList<V> valuesOfKeys(Map<K, V> map, K... keys) {
/* 2357 */     return MapUtil.valuesOfKeys(map, new ArrayIter<>(keys));
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
/*      */   public static <K, V> ArrayList<V> valuesOfKeys(Map<K, V> map, Iterable<K> keys) {
/* 2372 */     return valuesOfKeys(map, keys.iterator());
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
/* 2387 */     return MapUtil.valuesOfKeys(map, keys);
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
/*      */   @SafeVarargs
/*      */   public static <T> List<T> sortPageAll(int pageNo, int pageSize, Comparator<T> comparator, Collection<T>... colls) {
/* 2405 */     List<T> list = new ArrayList<>(pageNo * pageSize);
/* 2406 */     for (Collection<T> coll : colls) {
/* 2407 */       list.addAll(coll);
/*      */     }
/* 2409 */     if (null != comparator) {
/* 2410 */       list.sort(comparator);
/*      */     }
/*      */     
/* 2413 */     return page(pageNo, pageSize, list);
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
/*      */   public static <T> List<T> page(int pageNo, int pageSize, List<T> list) {
/* 2427 */     return ListUtil.page(pageNo, pageSize, list);
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
/*      */   public static <T> List<T> sort(Collection<T> collection, Comparator<? super T> comparator) {
/* 2439 */     List<T> list = new ArrayList<>(collection);
/* 2440 */     list.sort(comparator);
/* 2441 */     return list;
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
/*      */   public static <T> List<T> sort(List<T> list, Comparator<? super T> c) {
/* 2454 */     return ListUtil.sort(list, c);
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
/*      */   public static <T> List<T> sortByProperty(Collection<T> collection, String property) {
/* 2467 */     return sort(collection, (Comparator<? super T>)new PropertyComparator(property));
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
/*      */   public static <T> List<T> sortByProperty(List<T> list, String property) {
/* 2480 */     return ListUtil.sortByProperty(list, property);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static List<String> sortByPinyin(Collection<String> collection) {
/* 2491 */     return sort(collection, (Comparator<? super String>)new PinyinComparator());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static List<String> sortByPinyin(List<String> list) {
/* 2502 */     return ListUtil.sortByPinyin(list);
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
/*      */   public static <K, V> TreeMap<K, V> sort(Map<K, V> map, Comparator<? super K> comparator) {
/* 2516 */     TreeMap<K, V> result = new TreeMap<>(comparator);
/* 2517 */     result.putAll(map);
/* 2518 */     return result;
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
/*      */   public static <K, V> LinkedHashMap<K, V> sortToMap(Collection<Map.Entry<K, V>> entryCollection, Comparator<Map.Entry<K, V>> comparator) {
/* 2532 */     List<Map.Entry<K, V>> list = new LinkedList<>(entryCollection);
/* 2533 */     list.sort(comparator);
/*      */     
/* 2535 */     LinkedHashMap<K, V> result = new LinkedHashMap<>();
/* 2536 */     for (Map.Entry<K, V> entry : list) {
/* 2537 */       result.put(entry.getKey(), entry.getValue());
/*      */     }
/* 2539 */     return result;
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
/*      */   public static <K, V> LinkedHashMap<K, V> sortByEntry(Map<K, V> map, Comparator<Map.Entry<K, V>> comparator) {
/* 2553 */     return sortToMap(map.entrySet(), comparator);
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
/*      */   public static <K, V> List<Map.Entry<K, V>> sortEntryToList(Collection<Map.Entry<K, V>> collection) {
/* 2566 */     List<Map.Entry<K, V>> list = new LinkedList<>(collection);
/* 2567 */     list.sort((o1, o2) -> {
/*      */           V v1 = (V)o1.getValue();
/*      */ 
/*      */           
/*      */           V v2 = (V)o2.getValue();
/*      */ 
/*      */           
/*      */           return (v1 instanceof Comparable) ? ((Comparable<V>)v1).compareTo(v2) : v1.toString().compareTo(v2.toString());
/*      */         });
/*      */     
/* 2577 */     return list;
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
/*      */   public static <T> void forEach(Iterable<T> iterable, Consumer<T> consumer) {
/* 2591 */     if (iterable == null) {
/*      */       return;
/*      */     }
/* 2594 */     forEach(iterable.iterator(), consumer);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <T> void forEach(Iterator<T> iterator, Consumer<T> consumer) {
/* 2605 */     if (iterator == null) {
/*      */       return;
/*      */     }
/* 2608 */     int index = 0;
/* 2609 */     while (iterator.hasNext()) {
/* 2610 */       consumer.accept(iterator.next(), index);
/* 2611 */       index++;
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
/*      */   public static <T> void forEach(Enumeration<T> enumeration, Consumer<T> consumer) {
/* 2623 */     if (enumeration == null) {
/*      */       return;
/*      */     }
/* 2626 */     int index = 0;
/* 2627 */     while (enumeration.hasMoreElements()) {
/* 2628 */       consumer.accept(enumeration.nextElement(), index);
/* 2629 */       index++;
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
/*      */   public static <K, V> void forEach(Map<K, V> map, KVConsumer<K, V> kvConsumer) {
/* 2643 */     if (map == null) {
/*      */       return;
/*      */     }
/* 2646 */     int index = 0;
/* 2647 */     for (Map.Entry<K, V> entry : map.entrySet()) {
/* 2648 */       kvConsumer.accept(entry.getKey(), entry.getValue(), index);
/* 2649 */       index++;
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
/*      */   public static <T> List<List<T>> group(Collection<T> collection, Hash32<T> hash) {
/* 2662 */     List<List<T>> result = new ArrayList<>();
/* 2663 */     if (isEmpty(collection)) {
/* 2664 */       return result;
/*      */     }
/* 2666 */     if (null == hash)
/*      */     {
/* 2668 */       hash = (t -> (null == t) ? 0 : t.hashCode());
/*      */     }
/*      */ 
/*      */ 
/*      */     
/* 2673 */     for (T t : collection) {
/* 2674 */       int index = hash.hash32(t);
/* 2675 */       if (result.size() - 1 < index) {
/* 2676 */         while (result.size() - 1 < index) {
/* 2677 */           result.add(null);
/*      */         }
/* 2679 */         result.set(index, newArrayList((T[])new Object[] { t })); continue;
/*      */       } 
/* 2681 */       List<T> subList = result.get(index);
/* 2682 */       if (null == subList) {
/* 2683 */         result.set(index, newArrayList((T[])new Object[] { t })); continue;
/*      */       } 
/* 2685 */       subList.add(t);
/*      */     } 
/*      */ 
/*      */     
/* 2689 */     return result;
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
/*      */   public static <T> List<List<T>> groupByField(Collection<T> collection, final String fieldName) {
/* 2701 */     return group(collection, new Hash32<T>() {
/* 2702 */           private final List<Object> fieldNameList = new ArrayList();
/*      */ 
/*      */           
/*      */           public int hash32(T t) {
/* 2706 */             if (null == t || false == BeanUtil.isBean(t.getClass()))
/*      */             {
/* 2708 */               return 0;
/*      */             }
/* 2710 */             Object value = ReflectUtil.getFieldValue(t, fieldName);
/* 2711 */             int hash = this.fieldNameList.indexOf(value);
/* 2712 */             if (hash < 0) {
/* 2713 */               this.fieldNameList.add(value);
/* 2714 */               return this.fieldNameList.size() - 1;
/*      */             } 
/* 2716 */             return hash;
/*      */           }
/*      */         });
/*      */   }
/*      */   
/*      */   @FunctionalInterface
/*      */   public static interface KVConsumer<K, V> extends Serializable {
/*      */     void accept(K param1K, V param1V, int param1Int); }
/*      */   
/*      */   @FunctionalInterface
/*      */   public static interface Consumer<T> extends Serializable {
/*      */     void accept(T param1T, int param1Int);
/*      */   }
/*      */   
/*      */   public static <T> List<T> reverse(List<T> list) {
/* 2731 */     return ListUtil.reverse(list);
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
/*      */   public static <T> List<T> reverseNew(List<T> list) {
/* 2743 */     return ListUtil.reverseNew(list);
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
/*      */   public static <T> List<T> setOrAppend(List<T> list, int index, T element) {
/* 2757 */     return ListUtil.setOrAppend(list, index, element);
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
/*      */   public static <K> Set<K> keySet(Collection<Map<K, ?>> mapCollection) {
/* 2769 */     if (isEmpty(mapCollection)) {
/* 2770 */       return new HashSet<>();
/*      */     }
/* 2772 */     HashSet<K> set = new HashSet<>(mapCollection.size() * 16);
/* 2773 */     for (Map<K, ?> map : mapCollection) {
/* 2774 */       set.addAll(map.keySet());
/*      */     }
/*      */     
/* 2777 */     return set;
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
/*      */   public static <V> List<V> values(Collection<Map<?, V>> mapCollection) {
/* 2789 */     List<V> values = new ArrayList<>();
/* 2790 */     for (Map<?, V> map : mapCollection) {
/* 2791 */       values.addAll(map.values());
/*      */     }
/*      */     
/* 2794 */     return values;
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
/*      */   public static <T extends Comparable<? super T>> T max(Collection<T> coll) {
/* 2807 */     return (T)Collections.<Comparable>max(coll);
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
/*      */   public static <T extends Comparable<? super T>> T min(Collection<T> coll) {
/* 2820 */     return (T)Collections.<Comparable>min(coll);
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
/*      */   public static <T> Collection<T> unmodifiable(Collection<? extends T> c) {
/* 2832 */     return Collections.unmodifiableCollection(c);
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
/*      */   public static <E, T extends Collection<E>> T empty(Class<?> collectionClass) {
/* 2853 */     if (null == collectionClass) {
/* 2854 */       return (T)Collections.emptyList();
/*      */     }
/*      */     
/* 2857 */     if (Set.class.isAssignableFrom(collectionClass)) {
/* 2858 */       if (NavigableSet.class == collectionClass)
/* 2859 */         return (T)Collections.emptyNavigableSet(); 
/* 2860 */       if (SortedSet.class == collectionClass) {
/* 2861 */         return (T)Collections.emptySortedSet();
/*      */       }
/* 2863 */       return (T)Collections.emptySet();
/*      */     } 
/* 2865 */     if (List.class.isAssignableFrom(collectionClass)) {
/* 2866 */       return (T)Collections.emptyList();
/*      */     }
/*      */ 
/*      */     
/* 2870 */     throw new IllegalArgumentException(StrUtil.format("[{}] is not support to get empty!", new Object[] { collectionClass }));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void clear(Collection<?>... collections) {
/* 2880 */     for (Collection<?> collection : collections) {
/* 2881 */       if (isNotEmpty(collection)) {
/* 2882 */         collection.clear();
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
/*      */   public static <T> void padLeft(List<T> list, int minLen, T padObj) {
/* 2897 */     Objects.requireNonNull(list);
/* 2898 */     if (list.isEmpty()) {
/* 2899 */       padRight(list, minLen, padObj);
/*      */       return;
/*      */     } 
/* 2902 */     for (int i = list.size(); i < minLen; i++) {
/* 2903 */       list.add(0, padObj);
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
/*      */   public static <T> void padRight(Collection<T> list, int minLen, T padObj) {
/* 2917 */     Objects.requireNonNull(list);
/* 2918 */     for (int i = list.size(); i < minLen; i++) {
/* 2919 */       list.add(padObj);
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
/*      */   public static <F, T> Collection<T> trans(Collection<F> collection, Function<? super F, ? extends T> function) {
/* 2934 */     return new TransCollection<>(collection, function);
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
/*      */   public static <E, K, V> void setValueByMap(Iterable<E> iterable, Map<K, V> map, Function<E, K> keyGenerate, BiConsumer<E, V> biConsumer) {
/* 2951 */     iterable.forEach(x -> Optional.ofNullable(map.get(keyGenerate.apply(x))).ifPresent(()));
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static int size(Object object) {
/* 3010 */     if (object == null) {
/* 3011 */       return 0;
/*      */     }
/*      */     
/* 3014 */     int total = 0;
/* 3015 */     if (object instanceof Map) {
/* 3016 */       total = ((Map)object).size();
/* 3017 */     } else if (object instanceof Collection) {
/* 3018 */       total = ((Collection)object).size();
/* 3019 */     } else if (object instanceof Iterable) {
/* 3020 */       total = IterUtil.size((Iterable)object);
/* 3021 */     } else if (object instanceof Iterator) {
/* 3022 */       total = IterUtil.size((Iterator)object);
/* 3023 */     } else if (object instanceof Enumeration) {
/* 3024 */       Enumeration<?> it = (Enumeration)object;
/* 3025 */       while (it.hasMoreElements()) {
/* 3026 */         total++;
/* 3027 */         it.nextElement();
/*      */       } 
/* 3029 */     } else if (ArrayUtil.isArray(object)) {
/* 3030 */       total = ArrayUtil.length(object);
/*      */     } else {
/* 3032 */       throw new IllegalArgumentException("Unsupported object type: " + object.getClass().getName());
/*      */     } 
/* 3034 */     return total;
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
/*      */   public static boolean isEqualList(Collection<?> list1, Collection<?> list2) {
/* 3051 */     if (list1 == list2) {
/* 3052 */       return true;
/*      */     }
/* 3054 */     if (list1 == null || list2 == null || list1.size() != list2.size()) {
/* 3055 */       return false;
/*      */     }
/*      */     
/* 3058 */     return IterUtil.isEqualList(list1, list2);
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\collection\CollUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */