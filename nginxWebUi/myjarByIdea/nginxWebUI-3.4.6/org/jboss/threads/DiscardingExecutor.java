package org.jboss.threads;

import java.util.concurrent.Executor;

class DiscardingExecutor implements Executor {
   static final DiscardingExecutor INSTANCE = new DiscardingExecutor();

   private DiscardingExecutor() {
   }

   public void execute(Runnable command) {
   }

   public String toString() {
      return "Discarding executor";
   }
}
