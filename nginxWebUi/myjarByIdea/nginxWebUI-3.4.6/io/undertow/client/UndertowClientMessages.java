package io.undertow.client;

import io.undertow.util.HttpString;
import java.io.IOException;
import java.net.URI;
import org.jboss.logging.Messages;
import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageBundle;

@MessageBundle(
   projectCode = "UT"
)
public interface UndertowClientMessages {
   UndertowClientMessages MESSAGES = (UndertowClientMessages)Messages.getBundle(UndertowClientMessages.class);

   @Message(
      id = 1000,
      value = "Connection closed"
   )
   String connectionClosed();

   @Message(
      id = 1001,
      value = "Request already written"
   )
   IllegalStateException requestAlreadyWritten();

   @Message(
      id = 1020,
      value = "Failed to upgrade channel due to response %s (%s)"
   )
   String failedToUpgradeChannel(int var1, String var2);

   @Message(
      id = 1030,
      value = "invalid content length %d"
   )
   IllegalArgumentException illegalContentLength(long var1);

   @Message(
      id = 1031,
      value = "Unknown scheme in URI %s"
   )
   IllegalArgumentException unknownScheme(URI var1);

   @Message(
      id = 1032,
      value = "Unknown transfer encoding %s"
   )
   IOException unknownTransferEncoding(String var1);

   @Message(
      id = 1033,
      value = "Invalid connection state"
   )
   IOException invalidConnectionState();

   @Message(
      id = 1034,
      value = "Unknown AJP packet type %s"
   )
   IOException unknownAjpMessageType(byte var1);

   @Message(
      id = 1035,
      value = "Unknown method type for AJP request %s"
   )
   IOException unknownMethod(HttpString var1);

   @Message(
      id = 1036,
      value = "Data still remaining in chunk %s"
   )
   IOException dataStillRemainingInChunk(long var1);

   @Message(
      id = 1037,
      value = "Wrong magic number, expected %s, actual %s"
   )
   IOException wrongMagicNumber(String var1, String var2);

   @Message(
      id = 1038,
      value = "Received invalid AJP chunk %s with response already complete"
   )
   IOException receivedInvalidChunk(byte var1);
}
