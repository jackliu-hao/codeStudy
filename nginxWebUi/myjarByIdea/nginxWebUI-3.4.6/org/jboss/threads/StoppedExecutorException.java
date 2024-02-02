package org.jboss.threads;

import java.util.concurrent.RejectedExecutionException;

public class StoppedExecutorException extends RejectedExecutionException {
   private static final long serialVersionUID = 4815103522815471074L;

   public StoppedExecutorException() {
   }

   public StoppedExecutorException(String msg) {
      super(msg);
   }

   public StoppedExecutorException(Throwable cause) {
      super(cause);
   }

   public StoppedExecutorException(String msg, Throwable cause) {
      super(msg, cause);
   }
}
