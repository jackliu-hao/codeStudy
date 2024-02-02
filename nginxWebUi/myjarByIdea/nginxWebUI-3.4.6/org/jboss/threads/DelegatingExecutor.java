package org.jboss.threads;

import java.util.concurrent.Executor;

class DelegatingExecutor implements Executor {
   private final Executor delegate;

   DelegatingExecutor(Executor delegate) {
      this.delegate = delegate;
   }

   public void execute(Runnable command) {
      this.delegate.execute(command);
   }

   public String toString() {
      return String.format("%s -> %s", super.toString(), this.delegate);
   }
}
