package cn.hutool.core.collection;

import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Function;

public class TransSpliterator<F, T> implements Spliterator<T> {
   private final Spliterator<F> fromSpliterator;
   private final Function<? super F, ? extends T> function;

   public TransSpliterator(Spliterator<F> fromSpliterator, Function<? super F, ? extends T> function) {
      this.fromSpliterator = fromSpliterator;
      this.function = function;
   }

   public boolean tryAdvance(Consumer<? super T> action) {
      return this.fromSpliterator.tryAdvance((fromElement) -> {
         action.accept(this.function.apply(fromElement));
      });
   }

   public void forEachRemaining(Consumer<? super T> action) {
      this.fromSpliterator.forEachRemaining((fromElement) -> {
         action.accept(this.function.apply(fromElement));
      });
   }

   public Spliterator<T> trySplit() {
      Spliterator<F> fromSplit = this.fromSpliterator.trySplit();
      return fromSplit != null ? new TransSpliterator(fromSplit, this.function) : null;
   }

   public long estimateSize() {
      return this.fromSpliterator.estimateSize();
   }

   public int characteristics() {
      return this.fromSpliterator.characteristics() & -262;
   }
}
