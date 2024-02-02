package org.xnio;

import java.io.IOException;
import java.util.concurrent.Executor;

public class FutureResult<T> implements Result<T> {
   private final AbstractIoFuture<T> ioFuture;

   public FutureResult(Executor executor) {
      this.ioFuture = new AbstractIoFuture<T>() {
      };
   }

   public FutureResult() {
      this(IoUtils.directExecutor());
   }

   public IoFuture<T> getIoFuture() {
      return this.ioFuture;
   }

   public void addCancelHandler(Cancellable cancellable) {
      this.ioFuture.addCancelHandler(cancellable);
   }

   public boolean setResult(T result) {
      return this.ioFuture.setResult(result);
   }

   public boolean setException(IOException exception) {
      return this.ioFuture.setException(exception);
   }

   public boolean setCancelled() {
      return this.ioFuture.setCancelled();
   }
}
