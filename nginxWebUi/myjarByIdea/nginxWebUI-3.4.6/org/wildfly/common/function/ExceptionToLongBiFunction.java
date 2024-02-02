package org.wildfly.common.function;

import org.wildfly.common.Assert;

@FunctionalInterface
public interface ExceptionToLongBiFunction<T, U, E extends Exception> {
   long apply(T var1, U var2) throws E;

   default <R> ExceptionBiFunction<T, U, R, E> andThen(ExceptionLongFunction<R, E> after) {
      Assert.checkNotNullParam("after", after);
      return (t, u) -> {
         return after.apply(this.apply(t, u));
      };
   }
}
