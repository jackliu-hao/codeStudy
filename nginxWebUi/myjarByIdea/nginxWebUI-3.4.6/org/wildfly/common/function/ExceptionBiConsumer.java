package org.wildfly.common.function;

import org.wildfly.common.Assert;

@FunctionalInterface
public interface ExceptionBiConsumer<T, U, E extends Exception> {
   void accept(T var1, U var2) throws E;

   default ExceptionBiConsumer<T, U, E> andThen(ExceptionBiConsumer<? super T, ? super U, ? extends E> after) {
      Assert.checkNotNullParam("after", after);
      return (t, u) -> {
         this.accept(t, u);
         after.accept(t, u);
      };
   }

   default ExceptionRunnable<E> compose(ExceptionSupplier<? extends T, ? extends E> before1, ExceptionSupplier<? extends U, ? extends E> before2) {
      Assert.checkNotNullParam("before1", before1);
      Assert.checkNotNullParam("before2", before2);
      return () -> {
         this.accept(before1.get(), before2.get());
      };
   }
}
