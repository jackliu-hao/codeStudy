package io.undertow.websockets.core;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.zip.DataFormatException;
import org.jboss.logging.Messages;
import org.jboss.logging.annotations.Cause;
import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageBundle;

@MessageBundle(
   projectCode = "UT"
)
public interface WebSocketMessages {
   WebSocketMessages MESSAGES = (WebSocketMessages)Messages.getBundle(WebSocketMessages.class);

   @Message(
      id = 2002,
      value = "Channel is closed"
   )
   IOException channelClosed();

   @Message(
      id = 2003,
      value = "Text frame contains non UTF-8 data"
   )
   UnsupportedEncodingException invalidTextFrameEncoding();

   @Message(
      id = 2005,
      value = "Fragmented control frame"
   )
   WebSocketFrameCorruptedException fragmentedControlFrame();

   @Message(
      id = 2006,
      value = "Control frame with payload length > 125 octets"
   )
   WebSocketFrameCorruptedException toBigControlFrame();

   @Message(
      id = 2007,
      value = "Control frame using reserved opcode = %s"
   )
   WebSocketFrameCorruptedException reservedOpCodeInControlFrame(int var1);

   @Message(
      id = 2008,
      value = "Received close control frame with payload len 1"
   )
   WebSocketFrameCorruptedException controlFrameWithPayloadLen1();

   @Message(
      id = 2009,
      value = "Data frame using reserved opcode = %s"
   )
   WebSocketFrameCorruptedException reservedOpCodeInDataFrame(int var1);

   @Message(
      id = 2010,
      value = "Received continuation data frame outside fragmented message"
   )
   WebSocketFrameCorruptedException continuationFrameOutsideFragmented();

   @Message(
      id = 2011,
      value = "Received non-continuation data frame while inside fragmented message"
   )
   WebSocketFrameCorruptedException nonContinuationFrameInsideFragmented();

   @Message(
      id = 2013,
      value = "Cannot decode web socket frame with opcode: %s"
   )
   IllegalStateException unsupportedOpCode(int var1);

   @Message(
      id = 2014,
      value = "WebSocketFrameType %s is not supported by this WebSocketChannel\""
   )
   IllegalArgumentException unsupportedFrameType(WebSocketFrameType var1);

   @Message(
      id = 2015,
      value = "Extensions not allowed but received rsv of %s"
   )
   WebSocketFrameCorruptedException extensionsNotAllowed(int var1);

   @Message(
      id = 2016,
      value = "Could not find supported protocol in request list %s. Supported protocols are %s"
   )
   WebSocketHandshakeException unsupportedProtocol(String var1, Collection<String> var2);

   @Message(
      id = 2019,
      value = "Invalid payload for PING (payload length must be <= 125, was %s)"
   )
   IllegalArgumentException invalidPayloadLengthForPing(long var1);

   @Message(
      id = 2023,
      value = "Extensions not supported"
   )
   UnsupportedOperationException extensionsNotSupported();

   @Message(
      id = 2026,
      value = "Invalid close frame status code: %s"
   )
   WebSocketInvalidCloseCodeException invalidCloseFrameStatusCode(int var1);

   @Message(
      id = 2027,
      value = "Could not send data, as the underlying web socket connection has been broken"
   )
   IOException streamIsBroken();

   @Message(
      id = 2034,
      value = "Web socket frame was not masked"
   )
   WebSocketFrameCorruptedException frameNotMasked();

   @Message(
      id = 2035,
      value = "The response did not contain an 'Upgrade: websocket' header"
   )
   IOException noWebSocketUpgradeHeader();

   @Message(
      id = 2036,
      value = "The response did not contain a 'Connection: upgrade' header"
   )
   IOException noWebSocketConnectionHeader();

   @Message(
      id = 2037,
      value = "Sec-WebSocket-Accept mismatch, expecting %s, received %s"
   )
   IOException webSocketAcceptKeyMismatch(String var1, String var2);

   @Message(
      id = 2040,
      value = "Message exceeded max message size of %s"
   )
   String messageToBig(long var1);

   @Message(
      id = 2044,
      value = "Compressed message payload is corrupted"
   )
   IOException badCompressedPayload(@Cause DataFormatException var1);

   @Message(
      id = 2045,
      value = "Unable to send on newly created channel!"
   )
   IllegalStateException unableToSendOnNewChannel();
}
