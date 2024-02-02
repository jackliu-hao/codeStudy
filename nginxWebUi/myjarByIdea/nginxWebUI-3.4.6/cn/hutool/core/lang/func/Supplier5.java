package cn.hutool.core.lang.func;

import java.util.function.Supplier;

@FunctionalInterface
public interface Supplier5<T, P1, P2, P3, P4, P5> {
   T get(P1 var1, P2 var2, P3 var3, P4 var4, P5 var5);

   default Supplier<T> toSupplier(P1 p1, P2 p2, P3 p3, P4 p4, P5 p5) {
      return () -> {
         return this.get(p1, p2, p3, p4, p5);
      };
   }
}
