package cn.hutool.core.lang.func;

import java.io.Serializable;

@FunctionalInterface
public interface Func0<R> extends Serializable {
   R call() throws Exception;

   default R callWithRuntimeException() {
      try {
         return this.call();
      } catch (Exception var2) {
         throw new RuntimeException(var2);
      }
   }
}
