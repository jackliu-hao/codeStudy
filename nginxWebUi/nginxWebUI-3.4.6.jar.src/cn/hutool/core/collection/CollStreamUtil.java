/*     */ package cn.hutool.core.collection;
/*     */ 
/*     */ import cn.hutool.core.lang.Opt;
/*     */ import cn.hutool.core.map.MapUtil;
/*     */ import cn.hutool.core.stream.CollectorUtil;
/*     */ import cn.hutool.core.stream.StreamUtil;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Objects;
/*     */ import java.util.Set;
/*     */ import java.util.function.BiFunction;
/*     */ import java.util.function.Function;
/*     */ import java.util.function.Supplier;
/*     */ import java.util.stream.Collector;
/*     */ import java.util.stream.Collectors;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class CollStreamUtil
/*     */ {
/*     */   public static <V, K> Map<K, V> toIdentityMap(Collection<V> collection, Function<V, K> key) {
/*  41 */     return toIdentityMap(collection, key, false);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static <V, K> Map<K, V> toIdentityMap(Collection<V> collection, Function<V, K> key, boolean isParallel) {
/*  57 */     if (CollUtil.isEmpty(collection)) {
/*  58 */       return Collections.emptyMap();
/*     */     }
/*  60 */     return toMap(collection, v -> Opt.ofNullable(v).map(key).get(), Function.identity(), isParallel);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static <E, K, V> Map<K, V> toMap(Collection<E> collection, Function<E, K> key, Function<E, V> value) {
/*  76 */     return toMap(collection, key, value, false);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static <E, K, V> Map<K, V> toMap(Collection<E> collection, Function<E, K> key, Function<E, V> value, boolean isParallel) {
/*  90 */     if (CollUtil.isEmpty(collection)) {
/*  91 */       return Collections.emptyMap();
/*     */     }
/*  93 */     return (Map<K, V>)StreamUtil.of(collection, isParallel)
/*  94 */       .collect(HashMap::new, (m, v) -> m.put(key.apply(v), value.apply(v)), HashMap::putAll);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static <E, K> Map<K, List<E>> groupByKey(Collection<E> collection, Function<E, K> key) {
/* 109 */     return groupByKey(collection, key, false);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static <E, K> Map<K, List<E>> groupByKey(Collection<E> collection, Function<E, K> key, boolean isParallel) {
/* 124 */     if (CollUtil.isEmpty(collection)) {
/* 125 */       return Collections.emptyMap();
/*     */     }
/* 127 */     return groupBy(collection, key, Collectors.toList(), isParallel);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static <E, K, U> Map<K, Map<U, List<E>>> groupBy2Key(Collection<E> collection, Function<E, K> key1, Function<E, U> key2) {
/* 143 */     return groupBy2Key(collection, key1, key2, false);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static <E, K, U> Map<K, Map<U, List<E>>> groupBy2Key(Collection<E> collection, Function<E, K> key1, Function<E, U> key2, boolean isParallel) {
/* 162 */     if (CollUtil.isEmpty(collection)) {
/* 163 */       return Collections.emptyMap();
/*     */     }
/* 165 */     return groupBy(collection, key1, CollectorUtil.groupingBy(key2, Collectors.toList()), isParallel);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static <E, T, U> Map<T, Map<U, E>> group2Map(Collection<E> collection, Function<E, T> key1, Function<E, U> key2) {
/* 181 */     return group2Map(collection, key1, key2, false);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static <E, T, U> Map<T, Map<U, E>> group2Map(Collection<E> collection, Function<E, T> key1, Function<E, U> key2, boolean isParallel) {
/* 199 */     if (CollUtil.isEmpty(collection) || key1 == null || key2 == null) {
/* 200 */       return Collections.emptyMap();
/*     */     }
/* 202 */     return groupBy(collection, key1, CollectorUtil.toMap(key2, Function.identity(), (l, r) -> l), isParallel);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static <E, K, V> Map<K, List<V>> groupKeyValue(Collection<E> collection, Function<E, K> key, Function<E, V> value) {
/* 219 */     return groupKeyValue(collection, key, value, false);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static <E, K, V> Map<K, List<V>> groupKeyValue(Collection<E> collection, Function<E, K> key, Function<E, V> value, boolean isParallel) {
/* 237 */     if (CollUtil.isEmpty(collection)) {
/* 238 */       return Collections.emptyMap();
/*     */     }
/* 240 */     return groupBy(collection, key, Collectors.mapping(v -> Opt.ofNullable(v).map(value).orElse(null), (Collector)Collectors.toList()), isParallel);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static <E, K, D> Map<K, D> groupBy(Collection<E> collection, Function<E, K> key, Collector<E, ?, D> downstream) {
/* 256 */     if (CollUtil.isEmpty(collection)) {
/* 257 */       return Collections.emptyMap();
/*     */     }
/* 259 */     return groupBy(collection, key, downstream, false);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static <E, K, D> Map<K, D> groupBy(Collection<E> collection, Function<E, K> key, Collector<E, ?, D> downstream, boolean isParallel) {
/* 277 */     if (CollUtil.isEmpty(collection)) {
/* 278 */       return Collections.emptyMap();
/*     */     }
/* 280 */     return (Map<K, D>)StreamUtil.of(collection, isParallel).collect(CollectorUtil.groupingBy(key, downstream));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static <E, T> List<T> toList(Collection<E> collection, Function<E, T> function) {
/* 294 */     return toList(collection, function, false);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static <E, T> List<T> toList(Collection<E> collection, Function<E, T> function, boolean isParallel) {
/* 309 */     if (CollUtil.isEmpty(collection)) {
/* 310 */       return Collections.emptyList();
/*     */     }
/* 312 */     return (List<T>)StreamUtil.of(collection, isParallel)
/* 313 */       .<T>map(function)
/* 314 */       .filter(Objects::nonNull)
/* 315 */       .collect(Collectors.toList());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static <E, T> Set<T> toSet(Collection<E> collection, Function<E, T> function) {
/* 329 */     return toSet(collection, function, false);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static <E, T> Set<T> toSet(Collection<E> collection, Function<E, T> function, boolean isParallel) {
/* 344 */     if (CollUtil.isEmpty(collection)) {
/* 345 */       return Collections.emptySet();
/*     */     }
/* 347 */     return (Set<T>)StreamUtil.of(collection, isParallel)
/* 348 */       .<T>map(function)
/* 349 */       .filter(Objects::nonNull)
/* 350 */       .collect(Collectors.toSet());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static <K, X, Y, V> Map<K, V> merge(Map<K, X> map1, Map<K, Y> map2, BiFunction<X, Y, V> merge) {
/* 367 */     if (MapUtil.isEmpty(map1) && MapUtil.isEmpty(map2))
/* 368 */       return Collections.emptyMap(); 
/* 369 */     if (MapUtil.isEmpty(map1)) {
/* 370 */       map1 = Collections.emptyMap();
/* 371 */     } else if (MapUtil.isEmpty(map2)) {
/* 372 */       map2 = Collections.emptyMap();
/*     */     } 
/* 374 */     Set<K> key = new HashSet<>();
/* 375 */     key.addAll(map1.keySet());
/* 376 */     key.addAll(map2.keySet());
/* 377 */     Map<K, V> map = MapUtil.newHashMap(key.size());
/* 378 */     for (K t : key) {
/* 379 */       X x = map1.get(t);
/* 380 */       Y y = map2.get(t);
/* 381 */       V z = merge.apply(x, y);
/* 382 */       if (z != null) {
/* 383 */         map.put(t, z);
/*     */       }
/*     */     } 
/* 386 */     return map;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\collection\CollStreamUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */