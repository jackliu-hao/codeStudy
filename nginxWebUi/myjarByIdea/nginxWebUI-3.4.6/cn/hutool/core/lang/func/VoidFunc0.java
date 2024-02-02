package cn.hutool.core.lang.func;

import java.io.Serializable;

@FunctionalInterface
public interface VoidFunc0 extends Serializable {
   void call() throws Exception;

   default void callWithRuntimeException() {
      try {
         this.call();
      } catch (Exception var2) {
         throw new RuntimeException(var2);
      }
   }
}
