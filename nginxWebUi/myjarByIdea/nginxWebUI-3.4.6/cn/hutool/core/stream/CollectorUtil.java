package cn.hutool.core.stream;

import cn.hutool.core.lang.Opt;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Collector.Characteristics;

public class CollectorUtil {
   public static final Set<Collector.Characteristics> CH_ID;
   public static final Set<Collector.Characteristics> CH_NOID;

   public static <T> Collector<T, ?, String> joining(CharSequence delimiter) {
      return joining(delimiter, Object::toString);
   }

   public static <T> Collector<T, ?, String> joining(CharSequence delimiter, Function<T, ? extends CharSequence> toStringFunc) {
      return joining(delimiter, "", "", toStringFunc);
   }

   public static <T> Collector<T, ?, String> joining(CharSequence delimiter, CharSequence prefix, CharSequence suffix, Function<T, ? extends CharSequence> toStringFunc) {
      return new SimpleCollector(() -> {
         return new StringJoiner(delimiter, prefix, suffix);
      }, (joiner, ele) -> {
         joiner.add((CharSequence)toStringFunc.apply(ele));
      }, StringJoiner::merge, StringJoiner::toString, Collections.emptySet());
   }

   public static <T, K, D, A, M extends Map<K, D>> Collector<T, ?, M> groupingBy(Function<? super T, ? extends K> classifier, Supplier<M> mapFactory, Collector<? super T, A, D> downstream) {
      Supplier<A> downstreamSupplier = downstream.supplier();
      BiConsumer<A, ? super T> downstreamAccumulator = downstream.accumulator();
      BiConsumer<Map<K, A>, T> accumulator = (m, t) -> {
         K key = Opt.ofNullable(t).map(classifier).orElse((Object)null);
         A container = m.computeIfAbsent(key, (k) -> {
            return downstreamSupplier.get();
         });
         downstreamAccumulator.accept(container, t);
      };
      BinaryOperator<Map<K, A>> merger = mapMerger(downstream.combiner());
      if (downstream.characteristics().contains(Characteristics.IDENTITY_FINISH)) {
         return new SimpleCollector(mapFactory, accumulator, merger, CH_ID);
      } else {
         Function<A, A> downstreamFinisher = downstream.finisher();
         Function<Map<K, A>, M> finisher = (intermediate) -> {
            intermediate.replaceAll((k, v) -> {
               return downstreamFinisher.apply(v);
            });
            return intermediate;
         };
         return new SimpleCollector(mapFactory, accumulator, merger, finisher, CH_NOID);
      }
   }

   public static <T, K, A, D> Collector<T, ?, Map<K, D>> groupingBy(Function<? super T, ? extends K> classifier, Collector<? super T, A, D> downstream) {
      return groupingBy(classifier, HashMap::new, downstream);
   }

   public static <T, K> Collector<T, ?, Map<K, List<T>>> groupingBy(Function<? super T, ? extends K> classifier) {
      return groupingBy(classifier, Collectors.toList());
   }

   public static <T, K, U> Collector<T, ?, Map<K, U>> toMap(Function<? super T, ? extends K> keyMapper, Function<? super T, ? extends U> valueMapper, BinaryOperator<U> mergeFunction) {
      return toMap(keyMapper, valueMapper, mergeFunction, HashMap::new);
   }

   public static <T, K, U, M extends Map<K, U>> Collector<T, ?, M> toMap(Function<? super T, ? extends K> keyMapper, Function<? super T, ? extends U> valueMapper, BinaryOperator<U> mergeFunction, Supplier<M> mapSupplier) {
      BiConsumer<M, T> accumulator = (map, element) -> {
         map.put(Opt.ofNullable(element).map(keyMapper).get(), Opt.ofNullable(element).map(valueMapper).get());
      };
      return new SimpleCollector(mapSupplier, accumulator, mapMerger(mergeFunction), CH_ID);
   }

   public static <K, V, M extends Map<K, V>> BinaryOperator<M> mapMerger(BinaryOperator<V> mergeFunction) {
      return (m1, m2) -> {
         Iterator var3 = m2.entrySet().iterator();

         while(var3.hasNext()) {
            Map.Entry<K, V> e = (Map.Entry)var3.next();
            m1.merge(e.getKey(), e.getValue(), mergeFunction);
         }

         return m1;
      };
   }

   static {
      CH_ID = Collections.unmodifiableSet(EnumSet.of(Characteristics.IDENTITY_FINISH));
      CH_NOID = Collections.emptySet();
   }
}
