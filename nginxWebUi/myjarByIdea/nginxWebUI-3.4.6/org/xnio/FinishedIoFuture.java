package org.xnio;

import java.io.IOException;
import java.util.concurrent.CancellationException;
import java.util.concurrent.TimeUnit;

public class FinishedIoFuture<T> implements IoFuture<T> {
   private final T result;

   public FinishedIoFuture(T result) {
      this.result = result;
   }

   public IoFuture<T> cancel() {
      return this;
   }

   public IoFuture.Status getStatus() {
      return IoFuture.Status.DONE;
   }

   public IoFuture.Status await() {
      return IoFuture.Status.DONE;
   }

   public IoFuture.Status await(long time, TimeUnit timeUnit) {
      return IoFuture.Status.DONE;
   }

   public IoFuture.Status awaitInterruptibly() throws InterruptedException {
      return IoFuture.Status.DONE;
   }

   public IoFuture.Status awaitInterruptibly(long time, TimeUnit timeUnit) throws InterruptedException {
      return IoFuture.Status.DONE;
   }

   public T get() throws IOException, CancellationException {
      return this.result;
   }

   public T getInterruptibly() throws IOException, InterruptedException, CancellationException {
      return this.result;
   }

   public IOException getException() throws IllegalStateException {
      throw new IllegalStateException();
   }

   public <A> IoFuture<T> addNotifier(IoFuture.Notifier<? super T, A> notifier, A attachment) {
      notifier.notify(this, attachment);
      return this;
   }
}
