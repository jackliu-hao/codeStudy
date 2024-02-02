/*     */ package cn.hutool.core.stream;
/*     */ 
/*     */ import cn.hutool.core.lang.Opt;
/*     */ import java.util.Collections;
/*     */ import java.util.EnumSet;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.StringJoiner;
/*     */ import java.util.function.BiConsumer;
/*     */ import java.util.function.BinaryOperator;
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
/*     */ public class CollectorUtil
/*     */ {
/*  32 */   public static final Set<Collector.Characteristics> CH_ID = Collections.unmodifiableSet(EnumSet.of(Collector.Characteristics.IDENTITY_FINISH));
/*     */ 
/*     */ 
/*     */   
/*  36 */   public static final Set<Collector.Characteristics> CH_NOID = Collections.emptySet();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static <T> Collector<T, ?, String> joining(CharSequence delimiter) {
/*  46 */     return joining(delimiter, Object::toString);
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
/*     */   public static <T> Collector<T, ?, String> joining(CharSequence delimiter, Function<T, ? extends CharSequence> toStringFunc) {
/*  59 */     return joining(delimiter, "", "", toStringFunc);
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
/*     */   public static <T> Collector<T, ?, String> joining(CharSequence delimiter, CharSequence prefix, CharSequence suffix, Function<T, ? extends CharSequence> toStringFunc) {
/*  76 */     return new SimpleCollector<>(() -> new StringJoiner(delimiter, prefix, suffix), (joiner, ele) -> joiner.add(toStringFunc.apply(ele)), StringJoiner::merge, StringJoiner::toString, 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/*  81 */         Collections.emptySet());
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
/*     */ 
/*     */   
/*     */   public static <T, K, D, A, M extends Map<K, D>> Collector<T, ?, M> groupingBy(Function<? super T, ? extends K> classifier, Supplier<M> mapFactory, Collector<? super T, A, D> downstream) {
/* 102 */     Supplier<A> downstreamSupplier = downstream.supplier();
/* 103 */     BiConsumer<A, ? super T> downstreamAccumulator = downstream.accumulator();
/* 104 */     BiConsumer<Map<K, A>, T> accumulator = (m, t) -> {
/*     */         K key = (K)Opt.ofNullable(t).map(classifier).orElse(null);
/*     */         A container = m.computeIfAbsent(key, ());
/*     */         downstreamAccumulator.accept(container, t);
/*     */       };
/* 109 */     BinaryOperator<Map<K, A>> merger = mapMerger(downstream.combiner());
/*     */     
/* 111 */     Supplier<Map<K, A>> mangledFactory = mapFactory;
/*     */     
/* 113 */     if (downstream.characteristics().contains(Collector.Characteristics.IDENTITY_FINISH)) {
/* 114 */       return new SimpleCollector<>(mangledFactory, accumulator, merger, CH_ID);
/*     */     }
/*     */     
/* 117 */     Function<A, A> downstreamFinisher = downstream.finisher();
/* 118 */     Function<Map<K, A>, M> finisher = intermediate -> {
/*     */         intermediate.replaceAll(());
/*     */         
/*     */         return intermediate;
/*     */       };
/*     */     
/* 124 */     return new SimpleCollector<>(mangledFactory, accumulator, merger, finisher, CH_NOID);
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
/*     */   public static <T, K, A, D> Collector<T, ?, Map<K, D>> groupingBy(Function<? super T, ? extends K> classifier, Collector<? super T, A, D> downstream) {
/* 142 */     return groupingBy(classifier, java.util.HashMap::new, downstream);
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
/*     */   public static <T, K> Collector<T, ?, Map<K, List<T>>> groupingBy(Function<? super T, ? extends K> classifier) {
/* 155 */     return groupingBy(classifier, (Collector)Collectors.toList());
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
/*     */   public static <T, K, U> Collector<T, ?, Map<K, U>> toMap(Function<? super T, ? extends K> keyMapper, Function<? super T, ? extends U> valueMapper, BinaryOperator<U> mergeFunction) {
/* 173 */     return toMap(keyMapper, valueMapper, mergeFunction, java.util.HashMap::new);
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
/*     */ 
/*     */   
/*     */   public static <T, K, U, M extends Map<K, U>> Collector<T, ?, M> toMap(Function<? super T, ? extends K> keyMapper, Function<? super T, ? extends U> valueMapper, BinaryOperator<U> mergeFunction, Supplier<M> mapSupplier) {
/* 194 */     BiConsumer<M, T> accumulator = (map, element) -> map.put(Opt.ofNullable(element).map(keyMapper).get(), Opt.ofNullable(element).map(valueMapper).get());
/*     */     
/* 196 */     return new SimpleCollector<>(mapSupplier, accumulator, mapMerger(mergeFunction), CH_ID);
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
/*     */   public static <K, V, M extends Map<K, V>> BinaryOperator<M> mapMerger(BinaryOperator<V> mergeFunction) {
/* 209 */     return (m1, m2) -> {
/*     */         for (Map.Entry<K, V> e : (Iterable<Map.Entry<K, V>>)m2.entrySet())
/*     */           m1.merge(e.getKey(), e.getValue(), mergeFunction); 
/*     */         return m1;
/*     */       };
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\stream\CollectorUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */