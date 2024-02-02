package cn.hutool.core.lang.func;

import java.io.Serializable;

@FunctionalInterface
public interface VoidFunc1<P> extends Serializable {
   void call(P var1) throws Exception;

   default void callWithRuntimeException(P parameter) {
      try {
         this.call(parameter);
      } catch (Exception var3) {
         throw new RuntimeException(var3);
      }
   }
}
