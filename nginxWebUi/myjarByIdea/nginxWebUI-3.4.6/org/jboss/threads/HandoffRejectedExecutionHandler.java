package org.jboss.threads;

import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

class HandoffRejectedExecutionHandler implements RejectedExecutionHandler {
   private final Executor target;

   HandoffRejectedExecutionHandler(Executor target) {
      this.target = target;
   }

   public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
      this.target.execute(r);
   }

   public String toString() {
      return String.format("%s -> %s", super.toString(), this.target);
   }
}
