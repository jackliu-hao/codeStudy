package org.wildfly.common.function;

import org.wildfly.common.Assert;

public interface ExceptionRunnable<E extends Exception> {
   void run() throws E;

   default ExceptionRunnable<E> andThen(ExceptionRunnable<? extends E> after) {
      Assert.checkNotNullParam("after", after);
      return () -> {
         this.run();
         after.run();
      };
   }

   default ExceptionRunnable<E> compose(ExceptionRunnable<? extends E> before) {
      Assert.checkNotNullParam("before", before);
      return () -> {
         before.run();
         this.run();
      };
   }
}
