package org.xnio;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public abstract class AbstractConvertingIoFuture<T, D> implements IoFuture<T> {
   protected final IoFuture<? extends D> delegate;

   protected AbstractConvertingIoFuture(IoFuture<? extends D> delegate) {
      this.delegate = delegate;
   }

   protected IoFuture<? extends D> getDelegate() {
      return this.delegate;
   }

   public IoFuture<T> cancel() {
      this.delegate.cancel();
      return this;
   }

   public IoFuture.Status getStatus() {
      return this.delegate.getStatus();
   }

   public IoFuture.Status await() {
      return this.delegate.await();
   }

   public IoFuture.Status await(long time, TimeUnit timeUnit) {
      return this.delegate.await(time, timeUnit);
   }

   public IoFuture.Status awaitInterruptibly() throws InterruptedException {
      return this.delegate.awaitInterruptibly();
   }

   public IoFuture.Status awaitInterruptibly(long time, TimeUnit timeUnit) throws InterruptedException {
      return this.delegate.awaitInterruptibly(time, timeUnit);
   }

   public IOException getException() throws IllegalStateException {
      return this.delegate.getException();
   }

   public T get() throws IOException {
      return this.convert(this.delegate.get());
   }

   public T getInterruptibly() throws IOException, InterruptedException {
      return this.convert(this.delegate.getInterruptibly());
   }

   protected abstract T convert(D var1) throws IOException;

   public <A> IoFuture<T> addNotifier(final IoFuture.Notifier<? super T, A> notifier, A attachment) {
      this.delegate.addNotifier(new IoFuture.Notifier<D, A>() {
         public void notify(IoFuture<? extends D> future, A attachment) {
            notifier.notify(AbstractConvertingIoFuture.this, attachment);
         }
      }, attachment);
      return this;
   }
}
