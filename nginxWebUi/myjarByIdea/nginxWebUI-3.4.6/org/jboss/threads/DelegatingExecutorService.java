package org.jboss.threads;

import java.util.List;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

class DelegatingExecutorService extends AbstractExecutorService implements ExecutorService {
   private final Executor delegate;

   DelegatingExecutorService(Executor delegate) {
      this.delegate = delegate;
   }

   public void execute(Runnable command) {
      this.delegate.execute(command);
   }

   public boolean isShutdown() {
      return false;
   }

   public boolean isTerminated() {
      return false;
   }

   public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
      return false;
   }

   public void shutdown() {
      throw Messages.msg.notAllowedContainerManaged("shutdown");
   }

   public List<Runnable> shutdownNow() {
      throw Messages.msg.notAllowedContainerManaged("shutdownNow");
   }

   public String toString() {
      return String.format("%s -> %s", super.toString(), this.delegate);
   }
}
