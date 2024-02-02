package org.wildfly.common.function;

import org.wildfly.common.Assert;

@FunctionalInterface
public interface ExceptionSupplier<T, E extends Exception> {
   T get() throws E;

   default ExceptionRunnable<E> andThen(ExceptionConsumer<? super T, ? extends E> after) {
      Assert.checkNotNullParam("after", after);
      return () -> {
         after.accept(this.get());
      };
   }

   default <R> ExceptionSupplier<R, E> andThen(ExceptionFunction<? super T, ? extends R, ? extends E> after) {
      Assert.checkNotNullParam("after", after);
      return () -> {
         return after.apply(this.get());
      };
   }
}
