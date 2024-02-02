package org.wildfly.common.function;

import org.wildfly.common.Assert;

@FunctionalInterface
public interface ExceptionBiFunction<T, U, R, E extends Exception> {
   R apply(T var1, U var2) throws E;

   default <R2> ExceptionBiFunction<T, U, R2, E> andThen(ExceptionFunction<? super R, ? extends R2, ? extends E> after) {
      Assert.checkNotNullParam("after", after);
      return (t, u) -> {
         return after.apply(this.apply(t, u));
      };
   }

   default ExceptionBiConsumer<T, U, E> andThen(ExceptionConsumer<R, ? extends E> after) {
      Assert.checkNotNullParam("after", after);
      return (t, u) -> {
         after.accept(this.apply(t, u));
      };
   }

   default ExceptionSupplier<R, E> compose(ExceptionSupplier<? extends T, ? extends E> before1, ExceptionSupplier<? extends U, ? extends E> before2) {
      Assert.checkNotNullParam("before1", before1);
      Assert.checkNotNullParam("before2", before2);
      return () -> {
         return this.apply(before1.get(), before2.get());
      };
   }
}
