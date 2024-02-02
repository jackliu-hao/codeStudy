package cn.hutool.core.collection;

import java.util.Spliterator;
import java.util.function.Function;

public class SpliteratorUtil {
   public static <F, T> Spliterator<T> trans(Spliterator<F> fromSpliterator, Function<? super F, ? extends T> function) {
      return new TransSpliterator(fromSpliterator, function);
   }
}
