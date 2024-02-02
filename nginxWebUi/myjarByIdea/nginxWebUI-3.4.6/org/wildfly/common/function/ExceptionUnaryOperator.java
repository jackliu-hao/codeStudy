package org.wildfly.common.function;

import java.util.Objects;
import org.wildfly.common.Assert;

@FunctionalInterface
public interface ExceptionUnaryOperator<T, E extends Exception> extends ExceptionFunction<T, T, E> {
   default ExceptionUnaryOperator<T, E> andThen(ExceptionUnaryOperator<T, ? extends E> after) {
      Assert.checkNotNullParam("after", after);
      return (t) -> {
         return after.apply(this.apply(t));
      };
   }

   default ExceptionUnaryOperator<T, E> compose(ExceptionUnaryOperator<T, ? extends E> before) {
      Assert.checkNotNullParam("before", before);
      return (t) -> {
         return this.apply(before.apply(t));
      };
   }

   static <T, E extends Exception> ExceptionUnaryOperator<T, E> of(ExceptionFunction<T, T, E> func) {
      ExceptionUnaryOperator var10000;
      if (func instanceof ExceptionUnaryOperator) {
         var10000 = (ExceptionUnaryOperator)func;
      } else {
         Objects.requireNonNull(func);
         var10000 = func::apply;
      }

      return var10000;
   }

   static <T, E extends Exception> ExceptionUnaryOperator<T, E> identity() {
      return (t) -> {
         return t;
      };
   }
}
