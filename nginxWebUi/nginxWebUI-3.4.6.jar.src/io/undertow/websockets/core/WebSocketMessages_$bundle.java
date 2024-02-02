/*     */ package io.undertow.websockets.core;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.Serializable;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collection;
/*     */ import java.util.Locale;
/*     */ import java.util.zip.DataFormatException;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class WebSocketMessages_$bundle
/*     */   implements WebSocketMessages, Serializable
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*  27 */   public static final WebSocketMessages_$bundle INSTANCE = new WebSocketMessages_$bundle();
/*     */   protected Object readResolve() {
/*  29 */     return INSTANCE;
/*     */   }
/*  31 */   private static final Locale LOCALE = Locale.ROOT;
/*     */   protected Locale getLoggingLocale() {
/*  33 */     return LOCALE;
/*     */   }
/*     */   protected String channelClosed$str() {
/*  36 */     return "UT002002: Channel is closed";
/*     */   }
/*     */   
/*     */   public final IOException channelClosed() {
/*  40 */     IOException result = new IOException(String.format(getLoggingLocale(), channelClosed$str(), new Object[0]));
/*  41 */     _copyStackTraceMinusOne(result);
/*  42 */     return result;
/*     */   }
/*     */   private static void _copyStackTraceMinusOne(Throwable e) {
/*  45 */     StackTraceElement[] st = e.getStackTrace();
/*  46 */     e.setStackTrace(Arrays.<StackTraceElement>copyOfRange(st, 1, st.length));
/*     */   }
/*     */   protected String invalidTextFrameEncoding$str() {
/*  49 */     return "UT002003: Text frame contains non UTF-8 data";
/*     */   }
/*     */   
/*     */   public final UnsupportedEncodingException invalidTextFrameEncoding() {
/*  53 */     UnsupportedEncodingException result = new UnsupportedEncodingException(String.format(getLoggingLocale(), invalidTextFrameEncoding$str(), new Object[0]));
/*  54 */     _copyStackTraceMinusOne(result);
/*  55 */     return result;
/*     */   }
/*     */   protected String fragmentedControlFrame$str() {
/*  58 */     return "UT002005: Fragmented control frame";
/*     */   }
/*     */   
/*     */   public final WebSocketFrameCorruptedException fragmentedControlFrame() {
/*  62 */     WebSocketFrameCorruptedException result = new WebSocketFrameCorruptedException(String.format(getLoggingLocale(), fragmentedControlFrame$str(), new Object[0]));
/*  63 */     _copyStackTraceMinusOne(result);
/*  64 */     return result;
/*     */   }
/*     */   protected String toBigControlFrame$str() {
/*  67 */     return "UT002006: Control frame with payload length > 125 octets";
/*     */   }
/*     */   
/*     */   public final WebSocketFrameCorruptedException toBigControlFrame() {
/*  71 */     WebSocketFrameCorruptedException result = new WebSocketFrameCorruptedException(String.format(getLoggingLocale(), toBigControlFrame$str(), new Object[0]));
/*  72 */     _copyStackTraceMinusOne(result);
/*  73 */     return result;
/*     */   }
/*     */   protected String reservedOpCodeInControlFrame$str() {
/*  76 */     return "UT002007: Control frame using reserved opcode = %s";
/*     */   }
/*     */   
/*     */   public final WebSocketFrameCorruptedException reservedOpCodeInControlFrame(int opCode) {
/*  80 */     WebSocketFrameCorruptedException result = new WebSocketFrameCorruptedException(String.format(getLoggingLocale(), reservedOpCodeInControlFrame$str(), new Object[] { Integer.valueOf(opCode) }));
/*  81 */     _copyStackTraceMinusOne(result);
/*  82 */     return result;
/*     */   }
/*     */   protected String controlFrameWithPayloadLen1$str() {
/*  85 */     return "UT002008: Received close control frame with payload len 1";
/*     */   }
/*     */   
/*     */   public final WebSocketFrameCorruptedException controlFrameWithPayloadLen1() {
/*  89 */     WebSocketFrameCorruptedException result = new WebSocketFrameCorruptedException(String.format(getLoggingLocale(), controlFrameWithPayloadLen1$str(), new Object[0]));
/*  90 */     _copyStackTraceMinusOne(result);
/*  91 */     return result;
/*     */   }
/*     */   protected String reservedOpCodeInDataFrame$str() {
/*  94 */     return "UT002009: Data frame using reserved opcode = %s";
/*     */   }
/*     */   
/*     */   public final WebSocketFrameCorruptedException reservedOpCodeInDataFrame(int opCode) {
/*  98 */     WebSocketFrameCorruptedException result = new WebSocketFrameCorruptedException(String.format(getLoggingLocale(), reservedOpCodeInDataFrame$str(), new Object[] { Integer.valueOf(opCode) }));
/*  99 */     _copyStackTraceMinusOne(result);
/* 100 */     return result;
/*     */   }
/*     */   protected String continuationFrameOutsideFragmented$str() {
/* 103 */     return "UT002010: Received continuation data frame outside fragmented message";
/*     */   }
/*     */   
/*     */   public final WebSocketFrameCorruptedException continuationFrameOutsideFragmented() {
/* 107 */     WebSocketFrameCorruptedException result = new WebSocketFrameCorruptedException(String.format(getLoggingLocale(), continuationFrameOutsideFragmented$str(), new Object[0]));
/* 108 */     _copyStackTraceMinusOne(result);
/* 109 */     return result;
/*     */   }
/*     */   protected String nonContinuationFrameInsideFragmented$str() {
/* 112 */     return "UT002011: Received non-continuation data frame while inside fragmented message";
/*     */   }
/*     */   
/*     */   public final WebSocketFrameCorruptedException nonContinuationFrameInsideFragmented() {
/* 116 */     WebSocketFrameCorruptedException result = new WebSocketFrameCorruptedException(String.format(getLoggingLocale(), nonContinuationFrameInsideFragmented$str(), new Object[0]));
/* 117 */     _copyStackTraceMinusOne(result);
/* 118 */     return result;
/*     */   }
/*     */   protected String unsupportedOpCode$str() {
/* 121 */     return "UT002013: Cannot decode web socket frame with opcode: %s";
/*     */   }
/*     */   
/*     */   public final IllegalStateException unsupportedOpCode(int opCode) {
/* 125 */     IllegalStateException result = new IllegalStateException(String.format(getLoggingLocale(), unsupportedOpCode$str(), new Object[] { Integer.valueOf(opCode) }));
/* 126 */     _copyStackTraceMinusOne(result);
/* 127 */     return result;
/*     */   }
/*     */   protected String unsupportedFrameType$str() {
/* 130 */     return "UT002014: WebSocketFrameType %s is not supported by this WebSocketChannel\"";
/*     */   }
/*     */   
/*     */   public final IllegalArgumentException unsupportedFrameType(WebSocketFrameType type) {
/* 134 */     IllegalArgumentException result = new IllegalArgumentException(String.format(getLoggingLocale(), unsupportedFrameType$str(), new Object[] { type }));
/* 135 */     _copyStackTraceMinusOne(result);
/* 136 */     return result;
/*     */   }
/*     */   protected String extensionsNotAllowed$str() {
/* 139 */     return "UT002015: Extensions not allowed but received rsv of %s";
/*     */   }
/*     */   
/*     */   public final WebSocketFrameCorruptedException extensionsNotAllowed(int rsv) {
/* 143 */     WebSocketFrameCorruptedException result = new WebSocketFrameCorruptedException(String.format(getLoggingLocale(), extensionsNotAllowed$str(), new Object[] { Integer.valueOf(rsv) }));
/* 144 */     _copyStackTraceMinusOne(result);
/* 145 */     return result;
/*     */   }
/*     */   protected String unsupportedProtocol$str() {
/* 148 */     return "UT002016: Could not find supported protocol in request list %s. Supported protocols are %s";
/*     */   }
/*     */   
/*     */   public final WebSocketHandshakeException unsupportedProtocol(String requestedSubprotocols, Collection<String> subprotocols) {
/* 152 */     WebSocketHandshakeException result = new WebSocketHandshakeException(String.format(getLoggingLocale(), unsupportedProtocol$str(), new Object[] { requestedSubprotocols, subprotocols }));
/* 153 */     _copyStackTraceMinusOne(result);
/* 154 */     return result;
/*     */   }
/*     */   protected String invalidPayloadLengthForPing$str() {
/* 157 */     return "UT002019: Invalid payload for PING (payload length must be <= 125, was %s)";
/*     */   }
/*     */   
/*     */   public final IllegalArgumentException invalidPayloadLengthForPing(long payloadLength) {
/* 161 */     IllegalArgumentException result = new IllegalArgumentException(String.format(getLoggingLocale(), invalidPayloadLengthForPing$str(), new Object[] { Long.valueOf(payloadLength) }));
/* 162 */     _copyStackTraceMinusOne(result);
/* 163 */     return result;
/*     */   }
/*     */   protected String extensionsNotSupported$str() {
/* 166 */     return "UT002023: Extensions not supported";
/*     */   }
/*     */   
/*     */   public final UnsupportedOperationException extensionsNotSupported() {
/* 170 */     UnsupportedOperationException result = new UnsupportedOperationException(String.format(getLoggingLocale(), extensionsNotSupported$str(), new Object[0]));
/* 171 */     _copyStackTraceMinusOne(result);
/* 172 */     return result;
/*     */   }
/*     */   protected String invalidCloseFrameStatusCode$str() {
/* 175 */     return "UT002026: Invalid close frame status code: %s";
/*     */   }
/*     */   
/*     */   public final WebSocketInvalidCloseCodeException invalidCloseFrameStatusCode(int statusCode) {
/* 179 */     WebSocketInvalidCloseCodeException result = new WebSocketInvalidCloseCodeException(String.format(getLoggingLocale(), invalidCloseFrameStatusCode$str(), new Object[] { Integer.valueOf(statusCode) }));
/* 180 */     _copyStackTraceMinusOne(result);
/* 181 */     return result;
/*     */   }
/*     */   protected String streamIsBroken$str() {
/* 184 */     return "UT002027: Could not send data, as the underlying web socket connection has been broken";
/*     */   }
/*     */   
/*     */   public final IOException streamIsBroken() {
/* 188 */     IOException result = new IOException(String.format(getLoggingLocale(), streamIsBroken$str(), new Object[0]));
/* 189 */     _copyStackTraceMinusOne(result);
/* 190 */     return result;
/*     */   }
/*     */   protected String frameNotMasked$str() {
/* 193 */     return "UT002034: Web socket frame was not masked";
/*     */   }
/*     */   
/*     */   public final WebSocketFrameCorruptedException frameNotMasked() {
/* 197 */     WebSocketFrameCorruptedException result = new WebSocketFrameCorruptedException(String.format(getLoggingLocale(), frameNotMasked$str(), new Object[0]));
/* 198 */     _copyStackTraceMinusOne(result);
/* 199 */     return result;
/*     */   }
/*     */   protected String noWebSocketUpgradeHeader$str() {
/* 202 */     return "UT002035: The response did not contain an 'Upgrade: websocket' header";
/*     */   }
/*     */   
/*     */   public final IOException noWebSocketUpgradeHeader() {
/* 206 */     IOException result = new IOException(String.format(getLoggingLocale(), noWebSocketUpgradeHeader$str(), new Object[0]));
/* 207 */     _copyStackTraceMinusOne(result);
/* 208 */     return result;
/*     */   }
/*     */   protected String noWebSocketConnectionHeader$str() {
/* 211 */     return "UT002036: The response did not contain a 'Connection: upgrade' header";
/*     */   }
/*     */   
/*     */   public final IOException noWebSocketConnectionHeader() {
/* 215 */     IOException result = new IOException(String.format(getLoggingLocale(), noWebSocketConnectionHeader$str(), new Object[0]));
/* 216 */     _copyStackTraceMinusOne(result);
/* 217 */     return result;
/*     */   }
/*     */   protected String webSocketAcceptKeyMismatch$str() {
/* 220 */     return "UT002037: Sec-WebSocket-Accept mismatch, expecting %s, received %s";
/*     */   }
/*     */   
/*     */   public final IOException webSocketAcceptKeyMismatch(String dKey, String acceptKey) {
/* 224 */     IOException result = new IOException(String.format(getLoggingLocale(), webSocketAcceptKeyMismatch$str(), new Object[] { dKey, acceptKey }));
/* 225 */     _copyStackTraceMinusOne(result);
/* 226 */     return result;
/*     */   }
/*     */   protected String messageToBig$str() {
/* 229 */     return "UT002040: Message exceeded max message size of %s";
/*     */   }
/*     */   
/*     */   public final String messageToBig(long maxMessageSize) {
/* 233 */     return String.format(getLoggingLocale(), messageToBig$str(), new Object[] { Long.valueOf(maxMessageSize) });
/*     */   }
/*     */   protected String badCompressedPayload$str() {
/* 236 */     return "UT002044: Compressed message payload is corrupted";
/*     */   }
/*     */   
/*     */   public final IOException badCompressedPayload(DataFormatException cause) {
/* 240 */     IOException result = new IOException(String.format(getLoggingLocale(), badCompressedPayload$str(), new Object[0]), cause);
/* 241 */     _copyStackTraceMinusOne(result);
/* 242 */     return result;
/*     */   }
/*     */   protected String unableToSendOnNewChannel$str() {
/* 245 */     return "UT002045: Unable to send on newly created channel!";
/*     */   }
/*     */   
/*     */   public final IllegalStateException unableToSendOnNewChannel() {
/* 249 */     IllegalStateException result = new IllegalStateException(String.format(getLoggingLocale(), unableToSendOnNewChannel$str(), new Object[0]));
/* 250 */     _copyStackTraceMinusOne(result);
/* 251 */     return result;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\websockets\core\WebSocketMessages_$bundle.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */