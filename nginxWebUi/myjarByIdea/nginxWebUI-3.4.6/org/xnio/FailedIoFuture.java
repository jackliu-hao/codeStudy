package org.xnio;

import java.io.IOException;

public class FailedIoFuture<T> extends AbstractIoFuture<T> {
   public FailedIoFuture(IOException e) {
      this.setException(e);
   }

   public IoFuture<T> cancel() {
      return this;
   }
}
