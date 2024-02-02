package cn.hutool.core.lang.func;

import java.io.Serializable;

@FunctionalInterface
public interface Func<P, R> extends Serializable {
   R call(P... var1) throws Exception;

   default R callWithRuntimeException(P... parameters) {
      try {
         return this.call(parameters);
      } catch (Exception var3) {
         throw new RuntimeException(var3);
      }
   }
}
