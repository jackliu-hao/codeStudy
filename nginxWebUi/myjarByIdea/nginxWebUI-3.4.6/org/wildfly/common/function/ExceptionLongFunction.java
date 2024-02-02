package org.wildfly.common.function;

import org.wildfly.common.Assert;

@FunctionalInterface
public interface ExceptionLongFunction<R, E extends Exception> {
   R apply(long var1) throws E;

   default <R2> ExceptionLongFunction<R2, E> andThen(ExceptionFunction<? super R, ? extends R2, ? extends E> after) {
      Assert.checkNotNullParam("after", after);
      return (t) -> {
         return after.apply(this.apply(t));
      };
   }

   default <T> ExceptionFunction<T, R, E> compose(ExceptionToLongFunction<? super T, ? extends E> before) {
      Assert.checkNotNullParam("before", before);
      return (t) -> {
         return this.apply(before.apply(t));
      };
   }
}
