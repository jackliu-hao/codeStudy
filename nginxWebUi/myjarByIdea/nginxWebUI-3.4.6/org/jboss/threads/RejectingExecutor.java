package org.jboss.threads;

import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;

class RejectingExecutor implements Executor {
   static final RejectingExecutor INSTANCE = new RejectingExecutor();
   private final String message;

   private RejectingExecutor() {
      this.message = null;
   }

   RejectingExecutor(String message) {
      this.message = message;
   }

   public void execute(Runnable command) {
      throw new RejectedExecutionException(this.message);
   }

   public String toString() {
      return "Rejecting executor";
   }
}
