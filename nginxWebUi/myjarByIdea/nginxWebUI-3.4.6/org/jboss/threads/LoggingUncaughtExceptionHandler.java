package org.jboss.threads;

import org.jboss.logging.Logger;

class LoggingUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {
   private final Logger log;

   LoggingUncaughtExceptionHandler(Logger log) {
      this.log = log;
   }

   public void uncaughtException(Thread thread, Throwable throwable) {
      this.log.errorf((Throwable)throwable, (String)"Thread %s threw an uncaught exception", (Object)thread);
   }

   public String toString() {
      return String.format("%s to \"%s\"", super.toString(), this.log.getName());
   }
}
