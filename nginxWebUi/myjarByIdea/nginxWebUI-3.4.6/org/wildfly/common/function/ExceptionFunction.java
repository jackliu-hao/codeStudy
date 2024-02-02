package org.wildfly.common.function;

import org.wildfly.common.Assert;

@FunctionalInterface
public interface ExceptionFunction<T, R, E extends Exception> {
   R apply(T var1) throws E;

   default <R2> ExceptionFunction<T, R2, E> andThen(ExceptionFunction<? super R, ? extends R2, ? extends E> after) {
      Assert.checkNotNullParam("after", after);
      return (t) -> {
         return after.apply(this.apply(t));
      };
   }

   default <R2> ExceptionFunction<T, R2, E> andThen(ExceptionBiFunction<? super T, ? super R, ? extends R2, ? extends E> after) {
      Assert.checkNotNullParam("after", after);
      return (t) -> {
         return after.apply(t, this.apply(t));
      };
   }

   default <T2> ExceptionFunction<T2, R, E> compose(ExceptionFunction<? super T2, ? extends T, ? extends E> before) {
      Assert.checkNotNullParam("before", before);
      return (t) -> {
         return this.apply(before.apply(t));
      };
   }

   default ExceptionConsumer<T, E> andThen(ExceptionConsumer<? super R, ? extends E> after) {
      Assert.checkNotNullParam("after", after);
      return (t) -> {
         after.accept(this.apply(t));
      };
   }

   default ExceptionConsumer<T, E> andThen(ExceptionBiConsumer<? super T, ? super R, ? extends E> after) {
      Assert.checkNotNullParam("after", after);
      return (t) -> {
         after.accept(t, this.apply(t));
      };
   }

   default ExceptionPredicate<T, E> andThen(ExceptionPredicate<? super R, ? extends E> after) {
      Assert.checkNotNullParam("after", after);
      return (t) -> {
         return after.test(this.apply(t));
      };
   }

   default ExceptionPredicate<T, E> andThen(ExceptionBiPredicate<? super T, ? super R, ? extends E> after) {
      Assert.checkNotNullParam("after", after);
      return (t) -> {
         return after.test(t, this.apply(t));
      };
   }

   default ExceptionSupplier<R, E> compose(ExceptionSupplier<? extends T, ? extends E> before) {
      Assert.checkNotNullParam("before", before);
      return () -> {
         return this.apply(before.get());
      };
   }
}
