package cn.hutool.core.lang.func;

import java.io.Serializable;

@FunctionalInterface
public interface VoidFunc<P> extends Serializable {
   void call(P... var1) throws Exception;

   default void callWithRuntimeException(P... parameters) {
      try {
         this.call(parameters);
      } catch (Exception var3) {
         throw new RuntimeException(var3);
      }
   }
}
