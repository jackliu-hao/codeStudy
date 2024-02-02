package io.undertow.websockets.core;

import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Locale;
import java.util.zip.DataFormatException;

public class WebSocketMessages_$bundle implements WebSocketMessages, Serializable {
   private static final long serialVersionUID = 1L;
   public static final WebSocketMessages_$bundle INSTANCE = new WebSocketMessages_$bundle();
   private static final Locale LOCALE;

   protected WebSocketMessages_$bundle() {
   }

   protected Object readResolve() {
      return INSTANCE;
   }

   protected Locale getLoggingLocale() {
      return LOCALE;
   }

   protected String channelClosed$str() {
      return "UT002002: Channel is closed";
   }

   public final IOException channelClosed() {
      IOException result = new IOException(String.format(this.getLoggingLocale(), this.channelClosed$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   private static void _copyStackTraceMinusOne(Throwable e) {
      StackTraceElement[] st = e.getStackTrace();
      e.setStackTrace((StackTraceElement[])Arrays.copyOfRange(st, 1, st.length));
   }

   protected String invalidTextFrameEncoding$str() {
      return "UT002003: Text frame contains non UTF-8 data";
   }

   public final UnsupportedEncodingException invalidTextFrameEncoding() {
      UnsupportedEncodingException result = new UnsupportedEncodingException(String.format(this.getLoggingLocale(), this.invalidTextFrameEncoding$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String fragmentedControlFrame$str() {
      return "UT002005: Fragmented control frame";
   }

   public final WebSocketFrameCorruptedException fragmentedControlFrame() {
      WebSocketFrameCorruptedException result = new WebSocketFrameCorruptedException(String.format(this.getLoggingLocale(), this.fragmentedControlFrame$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String toBigControlFrame$str() {
      return "UT002006: Control frame with payload length > 125 octets";
   }

   public final WebSocketFrameCorruptedException toBigControlFrame() {
      WebSocketFrameCorruptedException result = new WebSocketFrameCorruptedException(String.format(this.getLoggingLocale(), this.toBigControlFrame$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String reservedOpCodeInControlFrame$str() {
      return "UT002007: Control frame using reserved opcode = %s";
   }

   public final WebSocketFrameCorruptedException reservedOpCodeInControlFrame(int opCode) {
      WebSocketFrameCorruptedException result = new WebSocketFrameCorruptedException(String.format(this.getLoggingLocale(), this.reservedOpCodeInControlFrame$str(), opCode));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String controlFrameWithPayloadLen1$str() {
      return "UT002008: Received close control frame with payload len 1";
   }

   public final WebSocketFrameCorruptedException controlFrameWithPayloadLen1() {
      WebSocketFrameCorruptedException result = new WebSocketFrameCorruptedException(String.format(this.getLoggingLocale(), this.controlFrameWithPayloadLen1$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String reservedOpCodeInDataFrame$str() {
      return "UT002009: Data frame using reserved opcode = %s";
   }

   public final WebSocketFrameCorruptedException reservedOpCodeInDataFrame(int opCode) {
      WebSocketFrameCorruptedException result = new WebSocketFrameCorruptedException(String.format(this.getLoggingLocale(), this.reservedOpCodeInDataFrame$str(), opCode));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String continuationFrameOutsideFragmented$str() {
      return "UT002010: Received continuation data frame outside fragmented message";
   }

   public final WebSocketFrameCorruptedException continuationFrameOutsideFragmented() {
      WebSocketFrameCorruptedException result = new WebSocketFrameCorruptedException(String.format(this.getLoggingLocale(), this.continuationFrameOutsideFragmented$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String nonContinuationFrameInsideFragmented$str() {
      return "UT002011: Received non-continuation data frame while inside fragmented message";
   }

   public final WebSocketFrameCorruptedException nonContinuationFrameInsideFragmented() {
      WebSocketFrameCorruptedException result = new WebSocketFrameCorruptedException(String.format(this.getLoggingLocale(), this.nonContinuationFrameInsideFragmented$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String unsupportedOpCode$str() {
      return "UT002013: Cannot decode web socket frame with opcode: %s";
   }

   public final IllegalStateException unsupportedOpCode(int opCode) {
      IllegalStateException result = new IllegalStateException(String.format(this.getLoggingLocale(), this.unsupportedOpCode$str(), opCode));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String unsupportedFrameType$str() {
      return "UT002014: WebSocketFrameType %s is not supported by this WebSocketChannel\"";
   }

   public final IllegalArgumentException unsupportedFrameType(WebSocketFrameType type) {
      IllegalArgumentException result = new IllegalArgumentException(String.format(this.getLoggingLocale(), this.unsupportedFrameType$str(), type));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String extensionsNotAllowed$str() {
      return "UT002015: Extensions not allowed but received rsv of %s";
   }

   public final WebSocketFrameCorruptedException extensionsNotAllowed(int rsv) {
      WebSocketFrameCorruptedException result = new WebSocketFrameCorruptedException(String.format(this.getLoggingLocale(), this.extensionsNotAllowed$str(), rsv));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String unsupportedProtocol$str() {
      return "UT002016: Could not find supported protocol in request list %s. Supported protocols are %s";
   }

   public final WebSocketHandshakeException unsupportedProtocol(String requestedSubprotocols, Collection<String> subprotocols) {
      WebSocketHandshakeException result = new WebSocketHandshakeException(String.format(this.getLoggingLocale(), this.unsupportedProtocol$str(), requestedSubprotocols, subprotocols));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String invalidPayloadLengthForPing$str() {
      return "UT002019: Invalid payload for PING (payload length must be <= 125, was %s)";
   }

   public final IllegalArgumentException invalidPayloadLengthForPing(long payloadLength) {
      IllegalArgumentException result = new IllegalArgumentException(String.format(this.getLoggingLocale(), this.invalidPayloadLengthForPing$str(), payloadLength));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String extensionsNotSupported$str() {
      return "UT002023: Extensions not supported";
   }

   public final UnsupportedOperationException extensionsNotSupported() {
      UnsupportedOperationException result = new UnsupportedOperationException(String.format(this.getLoggingLocale(), this.extensionsNotSupported$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String invalidCloseFrameStatusCode$str() {
      return "UT002026: Invalid close frame status code: %s";
   }

   public final WebSocketInvalidCloseCodeException invalidCloseFrameStatusCode(int statusCode) {
      WebSocketInvalidCloseCodeException result = new WebSocketInvalidCloseCodeException(String.format(this.getLoggingLocale(), this.invalidCloseFrameStatusCode$str(), statusCode));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String streamIsBroken$str() {
      return "UT002027: Could not send data, as the underlying web socket connection has been broken";
   }

   public final IOException streamIsBroken() {
      IOException result = new IOException(String.format(this.getLoggingLocale(), this.streamIsBroken$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String frameNotMasked$str() {
      return "UT002034: Web socket frame was not masked";
   }

   public final WebSocketFrameCorruptedException frameNotMasked() {
      WebSocketFrameCorruptedException result = new WebSocketFrameCorruptedException(String.format(this.getLoggingLocale(), this.frameNotMasked$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String noWebSocketUpgradeHeader$str() {
      return "UT002035: The response did not contain an 'Upgrade: websocket' header";
   }

   public final IOException noWebSocketUpgradeHeader() {
      IOException result = new IOException(String.format(this.getLoggingLocale(), this.noWebSocketUpgradeHeader$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String noWebSocketConnectionHeader$str() {
      return "UT002036: The response did not contain a 'Connection: upgrade' header";
   }

   public final IOException noWebSocketConnectionHeader() {
      IOException result = new IOException(String.format(this.getLoggingLocale(), this.noWebSocketConnectionHeader$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String webSocketAcceptKeyMismatch$str() {
      return "UT002037: Sec-WebSocket-Accept mismatch, expecting %s, received %s";
   }

   public final IOException webSocketAcceptKeyMismatch(String dKey, String acceptKey) {
      IOException result = new IOException(String.format(this.getLoggingLocale(), this.webSocketAcceptKeyMismatch$str(), dKey, acceptKey));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String messageToBig$str() {
      return "UT002040: Message exceeded max message size of %s";
   }

   public final String messageToBig(long maxMessageSize) {
      return String.format(this.getLoggingLocale(), this.messageToBig$str(), maxMessageSize);
   }

   protected String badCompressedPayload$str() {
      return "UT002044: Compressed message payload is corrupted";
   }

   public final IOException badCompressedPayload(DataFormatException cause) {
      IOException result = new IOException(String.format(this.getLoggingLocale(), this.badCompressedPayload$str()), cause);
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String unableToSendOnNewChannel$str() {
      return "UT002045: Unable to send on newly created channel!";
   }

   public final IllegalStateException unableToSendOnNewChannel() {
      IllegalStateException result = new IllegalStateException(String.format(this.getLoggingLocale(), this.unableToSendOnNewChannel$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   static {
      LOCALE = Locale.ROOT;
   }
}
