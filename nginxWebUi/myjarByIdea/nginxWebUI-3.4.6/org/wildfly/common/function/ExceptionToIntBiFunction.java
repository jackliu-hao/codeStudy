package org.wildfly.common.function;

import org.wildfly.common.Assert;

@FunctionalInterface
public interface ExceptionToIntBiFunction<T, U, E extends Exception> {
   int apply(T var1, U var2) throws E;

   default <R> ExceptionBiFunction<T, U, R, E> andThen(ExceptionIntFunction<R, E> after) {
      Assert.checkNotNullParam("after", after);
      return (t, u) -> {
         return after.apply(this.apply(t, u));
      };
   }

   default <R> ExceptionBiFunction<T, U, R, E> andThen(ExceptionLongFunction<R, E> after) {
      Assert.checkNotNullParam("after", after);
      return (t, u) -> {
         return after.apply((long)this.apply(t, u));
      };
   }
}
