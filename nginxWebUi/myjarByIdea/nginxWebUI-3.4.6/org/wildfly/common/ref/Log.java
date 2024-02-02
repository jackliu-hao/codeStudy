package org.wildfly.common.ref;

import org.jboss.logging.Logger;
import org.jboss.logging.annotations.Cause;
import org.jboss.logging.annotations.LogMessage;
import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageLogger;

@MessageLogger(
   projectCode = "COM",
   length = 5
)
interface Log {
   Log log = (Log)Logger.getMessageLogger(Log.class, "org.wildfly.common.ref");

   @LogMessage(
      level = Logger.Level.DEBUG
   )
   @Message(
      id = 3000,
      value = "Reaping a reference failed"
   )
   void reapFailed(@Cause Throwable var1);
}
