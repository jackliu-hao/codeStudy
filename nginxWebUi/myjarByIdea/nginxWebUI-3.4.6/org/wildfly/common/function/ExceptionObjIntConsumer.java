package org.wildfly.common.function;

import org.wildfly.common.Assert;

@FunctionalInterface
public interface ExceptionObjIntConsumer<T, E extends Exception> {
   void accept(T var1, int var2) throws E;

   default ExceptionObjIntConsumer<T, E> andThen(ExceptionObjIntConsumer<? super T, ? extends E> after) {
      Assert.checkNotNullParam("after", after);
      return (t, v) -> {
         this.accept(t, v);
         after.accept(t, v);
      };
   }

   default ExceptionObjIntConsumer<T, E> compose(ExceptionObjIntConsumer<? super T, ? extends E> before) {
      Assert.checkNotNullParam("before", before);
      return (t, v) -> {
         before.accept(t, v);
         this.accept(t, v);
      };
   }

   default ExceptionObjIntConsumer<T, E> andThen(ExceptionObjLongConsumer<? super T, ? extends E> after) {
      Assert.checkNotNullParam("after", after);
      return (t, v) -> {
         this.accept(t, v);
         after.accept(t, (long)v);
      };
   }

   default ExceptionObjIntConsumer<T, E> compose(ExceptionObjLongConsumer<? super T, ? extends E> before) {
      Assert.checkNotNullParam("before", before);
      return (t, v) -> {
         before.accept(t, (long)v);
         this.accept(t, v);
      };
   }
}
