package cn.hutool.core.stream;

import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

public class SimpleCollector<T, A, R> implements Collector<T, A, R> {
   private final Supplier<A> supplier;
   private final BiConsumer<A, T> accumulator;
   private final BinaryOperator<A> combiner;
   private final Function<A, R> finisher;
   private final Set<Collector.Characteristics> characteristics;

   public SimpleCollector(Supplier<A> supplier, BiConsumer<A, T> accumulator, BinaryOperator<A> combiner, Function<A, R> finisher, Set<Collector.Characteristics> characteristics) {
      this.supplier = supplier;
      this.accumulator = accumulator;
      this.combiner = combiner;
      this.finisher = finisher;
      this.characteristics = characteristics;
   }

   public SimpleCollector(Supplier<A> supplier, BiConsumer<A, T> accumulator, BinaryOperator<A> combiner, Set<Collector.Characteristics> characteristics) {
      this(supplier, accumulator, combiner, (i) -> {
         return i;
      }, characteristics);
   }

   public BiConsumer<A, T> accumulator() {
      return this.accumulator;
   }

   public Supplier<A> supplier() {
      return this.supplier;
   }

   public BinaryOperator<A> combiner() {
      return this.combiner;
   }

   public Function<A, R> finisher() {
      return this.finisher;
   }

   public Set<Collector.Characteristics> characteristics() {
      return this.characteristics;
   }
}
