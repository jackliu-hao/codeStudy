package cn.hutool.core.lang.func;

import java.io.Serializable;

@FunctionalInterface
public interface Func1<P, R> extends Serializable {
   R call(P var1) throws Exception;

   default R callWithRuntimeException(P parameter) {
      try {
         return this.call(parameter);
      } catch (Exception var3) {
         throw new RuntimeException(var3);
      }
   }
}
