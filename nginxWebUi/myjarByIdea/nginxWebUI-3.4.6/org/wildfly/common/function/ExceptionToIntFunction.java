package org.wildfly.common.function;

import org.wildfly.common.Assert;

@FunctionalInterface
public interface ExceptionToIntFunction<T, E extends Exception> {
   int apply(T var1) throws E;

   default <R> ExceptionFunction<T, R, E> andThen(ExceptionIntFunction<? extends R, ? extends E> after) {
      Assert.checkNotNullParam("after", after);
      return (t) -> {
         return after.apply(this.apply(t));
      };
   }

   default <R> ExceptionFunction<T, R, E> andThen(ExceptionLongFunction<? extends R, ? extends E> after) {
      Assert.checkNotNullParam("after", after);
      return (t) -> {
         return after.apply((long)this.apply(t));
      };
   }

   default <T2> ExceptionToIntFunction<T2, E> compose(ExceptionFunction<? super T2, ? extends T, ? extends E> before) {
      Assert.checkNotNullParam("before", before);
      return (t) -> {
         return this.apply(before.apply(t));
      };
   }
}
