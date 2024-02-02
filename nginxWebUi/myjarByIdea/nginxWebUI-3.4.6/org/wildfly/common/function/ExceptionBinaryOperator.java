package org.wildfly.common.function;

import org.wildfly.common.Assert;

@FunctionalInterface
public interface ExceptionBinaryOperator<T, E extends Exception> extends ExceptionBiFunction<T, T, T, E> {
   default ExceptionBinaryOperator<T, E> andThen(ExceptionUnaryOperator<T, ? extends E> after) {
      Assert.checkNotNullParam("after", after);
      return (t, u) -> {
         return after.apply(this.apply(t, u));
      };
   }
}
