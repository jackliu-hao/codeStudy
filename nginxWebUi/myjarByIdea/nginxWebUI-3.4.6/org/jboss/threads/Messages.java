package org.jboss.threads;

import org.jboss.logging.BasicLogger;
import org.jboss.logging.Logger;
import org.jboss.logging.annotations.Cause;
import org.jboss.logging.annotations.LogMessage;
import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageLogger;

@MessageLogger(
   projectCode = "JBTHR",
   length = 5
)
interface Messages extends BasicLogger {
   Messages msg = (Messages)Logger.getMessageLogger(Messages.class, "org.jboss.threads");
   Messages intMsg = (Messages)Logger.getMessageLogger(Messages.class, "org.jboss.threads.interrupt-handler");

   @Message("JBoss Threads version %s")
   @LogMessage(
      level = Logger.Level.INFO
   )
   void version(String var1);

   @Message(
      id = 9,
      value = "Executor has been shut down"
   )
   StoppedExecutorException shutDownInitiated();

   @Message(
      id = 12,
      value = "Cannot await termination of a thread pool from one of its own threads"
   )
   IllegalStateException cannotAwaitWithin();

   @Message(
      id = 103,
      value = "The current thread does not support interrupt handlers"
   )
   IllegalStateException noInterruptHandlers();

   /** @deprecated */
   @Deprecated
   @Message(
      id = 104,
      value = "Executor is not shut down"
   )
   IllegalStateException notShutDown();

   @Message(
      id = 108,
      value = "Interrupt handler %s threw an exception"
   )
   @LogMessage(
      level = Logger.Level.ERROR
   )
   void interruptHandlerThrew(@Cause Throwable var1, InterruptHandler var2);

   @Message(
      id = 200,
      value = "%s() not allowed on container-managed executor"
   )
   SecurityException notAllowedContainerManaged(String var1);
}
