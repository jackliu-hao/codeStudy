package org.xnio;

import java.io.IOException;
import java.util.EventListener;
import java.util.concurrent.CancellationException;
import java.util.concurrent.TimeUnit;

public interface IoFuture<T> extends Cancellable {
   IoFuture<T> cancel();

   Status getStatus();

   Status await();

   Status await(long var1, TimeUnit var3);

   Status awaitInterruptibly() throws InterruptedException;

   Status awaitInterruptibly(long var1, TimeUnit var3) throws InterruptedException;

   T get() throws IOException, CancellationException;

   T getInterruptibly() throws IOException, InterruptedException, CancellationException;

   IOException getException() throws IllegalStateException;

   <A> IoFuture<T> addNotifier(Notifier<? super T, A> var1, A var2);

   public abstract static class HandlingNotifier<T, A> implements Notifier<T, A> {
      public void notify(IoFuture<? extends T> future, A attachment) {
         switch (future.getStatus()) {
            case CANCELLED:
               this.handleCancelled(attachment);
               break;
            case DONE:
               try {
                  this.handleDone(future.get(), attachment);
                  break;
               } catch (IOException var4) {
                  throw new IllegalStateException();
               }
            case FAILED:
               this.handleFailed(future.getException(), attachment);
               break;
            default:
               throw new IllegalStateException();
         }

      }

      public void handleCancelled(A attachment) {
      }

      public void handleFailed(IOException exception, A attachment) {
      }

      public void handleDone(T data, A attachment) {
      }
   }

   public interface Notifier<T, A> extends EventListener {
      void notify(IoFuture<? extends T> var1, A var2);
   }

   public static enum Status {
      WAITING,
      DONE,
      CANCELLED,
      FAILED;
   }
}
