package io.undertow.websockets.core;

import io.undertow.websockets.WebSocketExtension;
import org.jboss.logging.BasicLogger;
import org.jboss.logging.Logger;
import org.jboss.logging.annotations.Cause;
import org.jboss.logging.annotations.LogMessage;
import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageLogger;

@MessageLogger(
   projectCode = "UT"
)
public interface WebSocketLogger extends BasicLogger {
   WebSocketLogger ROOT_LOGGER = (WebSocketLogger)Logger.getMessageLogger(WebSocketLogger.class, WebSocketLogger.class.getPackage().getName());
   WebSocketLogger REQUEST_LOGGER = (WebSocketLogger)Logger.getMessageLogger(WebSocketLogger.class, WebSocketLogger.class.getPackage().getName() + ".request");
   WebSocketLogger EXTENSION_LOGGER = (WebSocketLogger)Logger.getMessageLogger(WebSocketLogger.class, WebSocketLogger.class.getPackage().getName() + ".extension");

   @LogMessage(
      level = Logger.Level.DEBUG
   )
   @Message(
      id = 25003,
      value = "Decoding WebSocket Frame with opCode %s"
   )
   void decodingFrameWithOpCode(int var1);

   @LogMessage(
      level = Logger.Level.ERROR
   )
   @Message(
      id = 25007,
      value = "Unhandled exception for annotated endpoint %s"
   )
   void unhandledErrorInAnnotatedEndpoint(Object var1, @Cause Throwable var2);

   @LogMessage(
      level = Logger.Level.WARN
   )
   @Message(
      id = 25008,
      value = "Incorrect parameter %s for extension"
   )
   void incorrectExtensionParameter(WebSocketExtension.Parameter var1);
}
