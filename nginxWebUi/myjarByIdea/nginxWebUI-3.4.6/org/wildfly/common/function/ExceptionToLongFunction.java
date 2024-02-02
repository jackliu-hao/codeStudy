package org.wildfly.common.function;

import org.wildfly.common.Assert;

@FunctionalInterface
public interface ExceptionToLongFunction<T, E extends Exception> {
   long apply(T var1) throws E;

   default <R> ExceptionFunction<T, R, E> andThen(ExceptionLongFunction<R, E> after) {
      Assert.checkNotNullParam("after", after);
      return (t) -> {
         return after.apply(this.apply(t));
      };
   }

   default <T2> ExceptionToLongFunction<T2, E> compose(ExceptionFunction<? super T2, ? extends T, ? extends E> before) {
      Assert.checkNotNullParam("before", before);
      return (t) -> {
         return this.apply(before.apply(t));
      };
   }
}
