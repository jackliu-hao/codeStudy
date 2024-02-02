package org.wildfly.common.function;

import org.wildfly.common.Assert;

@FunctionalInterface
public interface ExceptionObjLongConsumer<T, E extends Exception> {
   void accept(T var1, long var2) throws E;

   default ExceptionObjLongConsumer<T, E> andThen(ExceptionObjLongConsumer<? super T, ? extends E> after) {
      Assert.checkNotNullParam("after", after);
      return (t, v) -> {
         this.accept(t, v);
         after.accept(t, v);
      };
   }

   default ExceptionObjLongConsumer<T, E> compose(ExceptionObjLongConsumer<? super T, ? extends E> before) {
      Assert.checkNotNullParam("before", before);
      return (t, v) -> {
         before.accept(t, v);
         this.accept(t, v);
      };
   }
}
