package org.wildfly.common.function;

import org.wildfly.common.Assert;

@FunctionalInterface
public interface ExceptionIntFunction<R, E extends Exception> {
   R apply(int var1) throws E;

   default <R2> ExceptionIntFunction<R2, E> andThen(ExceptionFunction<? super R, ? extends R2, ? extends E> after) {
      Assert.checkNotNullParam("after", after);
      return (t) -> {
         return after.apply(this.apply(t));
      };
   }

   default <T> ExceptionFunction<T, R, E> compose(ExceptionToIntFunction<? super T, ? extends E> before) {
      Assert.checkNotNullParam("before", before);
      return (t) -> {
         return this.apply(before.apply(t));
      };
   }
}
