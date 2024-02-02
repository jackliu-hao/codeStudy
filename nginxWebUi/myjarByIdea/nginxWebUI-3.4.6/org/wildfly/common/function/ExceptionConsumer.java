package org.wildfly.common.function;

import org.wildfly.common.Assert;

@FunctionalInterface
public interface ExceptionConsumer<T, E extends Exception> {
   void accept(T var1) throws E;

   default ExceptionConsumer<T, E> andThen(ExceptionConsumer<? super T, ? extends E> after) {
      Assert.checkNotNullParam("after", after);
      return (t) -> {
         this.accept(t);
         after.accept(t);
      };
   }

   default ExceptionConsumer<T, E> compose(ExceptionConsumer<? super T, ? extends E> before) {
      Assert.checkNotNullParam("before", before);
      return (t) -> {
         this.accept(t);
         before.accept(t);
      };
   }

   default ExceptionRunnable<E> compose(ExceptionSupplier<? extends T, ? extends E> before) {
      Assert.checkNotNullParam("before", before);
      return () -> {
         this.accept(before.get());
      };
   }
}
