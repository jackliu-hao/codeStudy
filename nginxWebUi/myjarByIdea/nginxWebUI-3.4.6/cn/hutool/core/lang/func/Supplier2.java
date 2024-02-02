package cn.hutool.core.lang.func;

import java.util.function.Supplier;

@FunctionalInterface
public interface Supplier2<T, P1, P2> {
   T get(P1 var1, P2 var2);

   default Supplier<T> toSupplier(P1 p1, P2 p2) {
      return () -> {
         return this.get(p1, p2);
      };
   }
}
