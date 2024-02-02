package org.xnio._private;

import java.io.CharConversionException;
import java.io.EOFException;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;
import java.nio.ReadOnlyBufferException;
import java.nio.channels.Channel;
import java.util.Arrays;
import java.util.Locale;
import java.util.concurrent.CancellationException;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeoutException;
import javax.net.ssl.SSLEngineResult;
import javax.security.sasl.SaslException;
import org.jboss.logging.BasicLogger;
import org.jboss.logging.DelegatingBasicLogger;
import org.jboss.logging.Logger;
import org.xnio.IoFuture;
import org.xnio.channels.AcceptingChannel;
import org.xnio.channels.ConcurrentStreamChannelAccessException;
import org.xnio.channels.ConnectedChannel;
import org.xnio.channels.FixedLengthOverflowException;
import org.xnio.channels.FixedLengthUnderflowException;
import org.xnio.channels.ReadTimeoutException;
import org.xnio.channels.WriteTimeoutException;

public class Messages_$logger extends DelegatingBasicLogger implements Messages, BasicLogger, Serializable {
   private static final long serialVersionUID = 1L;
   private static final String FQCN = Messages_$logger.class.getName();
   private static final Locale LOCALE;

   public Messages_$logger(Logger log) {
      super(log);
   }

   protected Locale getLoggingLocale() {
      return LOCALE;
   }

   public final void greeting(String version) {
      super.log.logf((String)FQCN, (Logger.Level)Logger.Level.INFO, (Throwable)null, (String)this.greeting$str(), (Object)version);
   }

   protected String greeting$str() {
      return "XNIO version %s";
   }

   protected String nullParameter$str() {
      return "Method parameter '%s' cannot be null";
   }

   public final IllegalArgumentException nullParameter(String name) {
      IllegalArgumentException result = new IllegalArgumentException(String.format(this.getLoggingLocale(), this.nullParameter$str(), name));
      _copyStackTraceMinusOne(result);
      return result;
   }

   private static void _copyStackTraceMinusOne(Throwable e) {
      StackTraceElement[] st = e.getStackTrace();
      e.setStackTrace((StackTraceElement[])Arrays.copyOfRange(st, 1, st.length));
   }

   protected String minRange$str() {
      return "XNIO000001: Method parameter '%s' must be greater than %d";
   }

   public final IllegalArgumentException minRange(String paramName, int minimumValue) {
      IllegalArgumentException result = new IllegalArgumentException(String.format(this.getLoggingLocale(), this.minRange$str(), paramName, minimumValue));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String badSockType$str() {
      return "XNIO000002: Unsupported socket address %s";
   }

   public final IllegalArgumentException badSockType(Class<? extends SocketAddress> type) {
      IllegalArgumentException result = new IllegalArgumentException(String.format(this.getLoggingLocale(), this.badSockType$str(), type));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String nullArrayIndex$str() {
      return "XNIO000003: Method parameter '%s' contains disallowed null value at index %d";
   }

   public final IllegalArgumentException nullArrayIndex(String paramName, int index) {
      IllegalArgumentException result = new IllegalArgumentException(String.format(this.getLoggingLocale(), this.nullArrayIndex$str(), paramName, index));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String mismatchSockType$str() {
      return "XNIO000004: Bind address %s is not the same type as destination address %s";
   }

   public final IllegalArgumentException mismatchSockType(Class<? extends SocketAddress> bindType, Class<? extends SocketAddress> destType) {
      IllegalArgumentException result = new IllegalArgumentException(String.format(this.getLoggingLocale(), this.mismatchSockType$str(), bindType, destType));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String noField$str() {
      return "XNIO000005: No such field named \"%s\" in %s";
   }

   public final IllegalArgumentException noField(String fieldName, Class<?> clazz) {
      IllegalArgumentException result = new IllegalArgumentException(String.format(this.getLoggingLocale(), this.noField$str(), fieldName, clazz));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String optionClassNotFound$str() {
      return "XNIO000006: Class \"%s\" not found in %s";
   }

   public final IllegalArgumentException optionClassNotFound(String className, ClassLoader classLoader) {
      IllegalArgumentException result = new IllegalArgumentException(String.format(this.getLoggingLocale(), this.optionClassNotFound$str(), className, classLoader));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String fieldNotAccessible$str() {
      return "XNIO000007: Field named \"%s\" is not accessible (public) in %s";
   }

   public final IllegalArgumentException fieldNotAccessible(String fieldName, Class<?> clazz) {
      IllegalArgumentException result = new IllegalArgumentException(String.format(this.getLoggingLocale(), this.fieldNotAccessible$str(), fieldName, clazz));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String fieldNotStatic$str() {
      return "XNIO000008: Field named \"%s\" is not static in %s";
   }

   public final IllegalArgumentException fieldNotStatic(String fieldName, Class<?> clazz) {
      IllegalArgumentException result = new IllegalArgumentException(String.format(this.getLoggingLocale(), this.fieldNotStatic$str(), fieldName, clazz));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String copyNegative$str() {
      return "XNIO000009: Copy with negative count is not supported";
   }

   public final UnsupportedOperationException copyNegative() {
      UnsupportedOperationException result = new UnsupportedOperationException(String.format(this.getLoggingLocale(), this.copyNegative$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   public final void invalidOptionInProperty(String optionName, String name, Throwable problem) {
      super.log.logf(FQCN, Logger.Level.WARN, (Throwable)null, this.invalidOptionInProperty$str(), optionName, name, problem);
   }

   protected String invalidOptionInProperty$str() {
      return "XNIO000010: Invalid option '%s' in property '%s': %s";
   }

   protected String readOnlyBuffer$str() {
      return "XNIO000012: Attempt to write to a read-only buffer";
   }

   public final ReadOnlyBufferException readOnlyBuffer() {
      ReadOnlyBufferException result = new ReadOnlyBufferException();
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String bufferUnderflow$str() {
      return "XNIO000013: Buffer underflow";
   }

   public final BufferUnderflowException bufferUnderflow() {
      BufferUnderflowException result = new BufferUnderflowException();
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String bufferOverflow$str() {
      return "XNIO000014: Buffer overflow";
   }

   public final BufferOverflowException bufferOverflow() {
      BufferOverflowException result = new BufferOverflowException();
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String parameterOutOfRange$str() {
      return "XNIO000015: Parameter '%s' is out of range";
   }

   public final IllegalArgumentException parameterOutOfRange(String name) {
      IllegalArgumentException result = new IllegalArgumentException(String.format(this.getLoggingLocale(), this.parameterOutOfRange$str(), name));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String mixedDirectAndHeap$str() {
      return "XNIO000016: Mixed direct and heap buffers not allowed";
   }

   public final IllegalArgumentException mixedDirectAndHeap() {
      IllegalArgumentException result = new IllegalArgumentException(String.format(this.getLoggingLocale(), this.mixedDirectAndHeap$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String bufferFreed$str() {
      return "XNIO000017: Buffer was already freed";
   }

   public final IllegalStateException bufferFreed() {
      IllegalStateException result = new IllegalStateException(String.format(this.getLoggingLocale(), this.bufferFreed$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String randomWrongThread$str() {
      return "XNIO000018: Access a thread-local random from the wrong thread";
   }

   public final IllegalStateException randomWrongThread() {
      IllegalStateException result = new IllegalStateException(String.format(this.getLoggingLocale(), this.randomWrongThread$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String channelNotAvailable$str() {
      return "XNIO000019: Channel not available from connection";
   }

   public final IllegalStateException channelNotAvailable() {
      IllegalStateException result = new IllegalStateException(String.format(this.getLoggingLocale(), this.channelNotAvailable$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String noOptionParser$str() {
      return "XNIO000020: No parser for this option value type";
   }

   public final IllegalArgumentException noOptionParser() {
      IllegalArgumentException result = new IllegalArgumentException(String.format(this.getLoggingLocale(), this.noOptionParser$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String invalidOptionPropertyFormat$str() {
      return "XNIO000021: Invalid format for property value '%s'";
   }

   public final IllegalArgumentException invalidOptionPropertyFormat(String value) {
      IllegalArgumentException result = new IllegalArgumentException(String.format(this.getLoggingLocale(), this.invalidOptionPropertyFormat$str(), value));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String classNotFound$str() {
      return "XNIO000022: Class %s not found";
   }

   public final IllegalArgumentException classNotFound(String name, ClassNotFoundException cause) {
      IllegalArgumentException result = new IllegalArgumentException(String.format(this.getLoggingLocale(), this.classNotFound$str(), name), cause);
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String classNotInstance$str() {
      return "XNIO000023: Class %s is not an instance of %s";
   }

   public final IllegalArgumentException classNotInstance(String name, Class<?> expectedType) {
      IllegalArgumentException result = new IllegalArgumentException(String.format(this.getLoggingLocale(), this.classNotInstance$str(), name, expectedType));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String invalidOptionName$str() {
      return "XNIO000024: Invalid option name '%s'";
   }

   public final IllegalArgumentException invalidOptionName(String name) {
      IllegalArgumentException result = new IllegalArgumentException(String.format(this.getLoggingLocale(), this.invalidOptionName$str(), name));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String invalidNullOption$str() {
      return "XNIO000025: Invalid null option '%s'";
   }

   public final IllegalArgumentException invalidNullOption(String name) {
      IllegalArgumentException result = new IllegalArgumentException(String.format(this.getLoggingLocale(), this.invalidNullOption$str(), name));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String readAppendNotSupported$str() {
      return "XNIO000026: Read with append is not supported";
   }

   public final IOException readAppendNotSupported() {
      IOException result = new IOException(String.format(this.getLoggingLocale(), this.readAppendNotSupported$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String openModeRequires7$str() {
      return "XNIO000027: Requested file open mode requires Java 7 or higher";
   }

   public final IOException openModeRequires7() {
      IOException result = new IOException(String.format(this.getLoggingLocale(), this.openModeRequires7$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String xnioThreadRequired$str() {
      return "XNIO000028: Current thread is not an XNIO I/O thread";
   }

   public final IllegalStateException xnioThreadRequired() {
      IllegalStateException result = new IllegalStateException(String.format(this.getLoggingLocale(), this.xnioThreadRequired$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String badCompressionFormat$str() {
      return "XNIO000029: Compression format not supported";
   }

   public final IllegalArgumentException badCompressionFormat() {
      IllegalArgumentException result = new IllegalArgumentException(String.format(this.getLoggingLocale(), this.badCompressionFormat$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String differentWorkers$str() {
      return "XNIO000030: Both channels must come from the same worker";
   }

   public final IllegalArgumentException differentWorkers() {
      IllegalArgumentException result = new IllegalArgumentException(String.format(this.getLoggingLocale(), this.differentWorkers$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String oneChannelMustBeConnection$str() {
      return "XNIO000031: At least one channel must have a connection";
   }

   public final IllegalArgumentException oneChannelMustBeConnection() {
      IllegalArgumentException result = new IllegalArgumentException(String.format(this.getLoggingLocale(), this.oneChannelMustBeConnection$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String oneChannelMustBeSSL$str() {
      return "XNIO000032: At least one channel must be an SSL channel";
   }

   public final IllegalArgumentException oneChannelMustBeSSL() {
      IllegalArgumentException result = new IllegalArgumentException(String.format(this.getLoggingLocale(), this.oneChannelMustBeSSL$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String invalidQop$str() {
      return "XNIO000033: '%s' is not a valid QOP value";
   }

   public final IllegalArgumentException invalidQop(String name) {
      IllegalArgumentException result = new IllegalArgumentException(String.format(this.getLoggingLocale(), this.invalidQop$str(), name));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String cantInstantiate$str() {
      return "XNIO000034: Failed to instantiate %s";
   }

   public final IllegalArgumentException cantInstantiate(Class<?> clazz, Throwable cause) {
      IllegalArgumentException result = new IllegalArgumentException(String.format(this.getLoggingLocale(), this.cantInstantiate$str(), clazz), cause);
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String concurrentAccess$str() {
      return "XNIO000035: Stream channel was accessed concurrently";
   }

   public final ConcurrentStreamChannelAccessException concurrentAccess() {
      ConcurrentStreamChannelAccessException result = new ConcurrentStreamChannelAccessException(String.format(this.getLoggingLocale(), this.concurrentAccess$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String malformedInput$str() {
      return "XNIO000036: Malformed input";
   }

   public final CharConversionException malformedInput() {
      CharConversionException result = new CharConversionException(String.format(this.getLoggingLocale(), this.malformedInput$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String unmappableCharacter$str() {
      return "XNIO000037: Unmappable character";
   }

   public final CharConversionException unmappableCharacter() {
      CharConversionException result = new CharConversionException(String.format(this.getLoggingLocale(), this.unmappableCharacter$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String characterDecodingProblem$str() {
      return "XNIO000038: Character decoding problem";
   }

   public final CharConversionException characterDecodingProblem() {
      CharConversionException result = new CharConversionException(String.format(this.getLoggingLocale(), this.characterDecodingProblem$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String mismatchAddressType$str() {
      return "XNIO000040: Mismatched IP address type; expected %s but got %s";
   }

   public final IllegalArgumentException mismatchAddressType(Class<? extends InetAddress> expected, Class<? extends InetAddress> actual) {
      IllegalArgumentException result = new IllegalArgumentException(String.format(this.getLoggingLocale(), this.mismatchAddressType$str(), expected, actual));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String invalidStrength$str() {
      return "XNIO000041: '%s' is not a valid Strength value";
   }

   public final IllegalArgumentException invalidStrength(String name) {
      IllegalArgumentException result = new IllegalArgumentException(String.format(this.getLoggingLocale(), this.invalidStrength$str(), name));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String addressUnresolved$str() {
      return "XNIO000042: Cannot add unresolved address '%s'";
   }

   public final IllegalArgumentException addressUnresolved(InetSocketAddress bindAddress) {
      IllegalArgumentException result = new IllegalArgumentException(String.format(this.getLoggingLocale(), this.addressUnresolved$str(), bindAddress));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String missingSslProvider$str() {
      return "XNIO000100: 'https' URL scheme chosen but no SSL provider given";
   }

   public final IllegalArgumentException missingSslProvider() {
      IllegalArgumentException result = new IllegalArgumentException(String.format(this.getLoggingLocale(), this.missingSslProvider$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String invalidURLScheme$str() {
      return "XNIO000101: Unknown URL scheme '%s' given; must be one of 'http' or 'https'";
   }

   public final IllegalArgumentException invalidURLScheme(String scheme) {
      IllegalArgumentException result = new IllegalArgumentException(String.format(this.getLoggingLocale(), this.invalidURLScheme$str(), scheme));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String extraChallenge$str() {
      return "XNIO000200: Unexpected extra SASL challenge data received";
   }

   public final SaslException extraChallenge() {
      SaslException result = new SaslException(String.format(this.getLoggingLocale(), this.extraChallenge$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String extraResponse$str() {
      return "XNIO000201: Unexpected extra SASL response data received";
   }

   public final SaslException extraResponse() {
      SaslException result = new SaslException(String.format(this.getLoggingLocale(), this.extraResponse$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String socketBufferTooSmall$str() {
      return "XNIO000300: Socket buffer is too small";
   }

   public final IllegalArgumentException socketBufferTooSmall() {
      IllegalArgumentException result = new IllegalArgumentException(String.format(this.getLoggingLocale(), this.socketBufferTooSmall$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String appBufferTooSmall$str() {
      return "XNIO000301: Application buffer is too small";
   }

   public final IllegalArgumentException appBufferTooSmall() {
      IllegalArgumentException result = new IllegalArgumentException(String.format(this.getLoggingLocale(), this.appBufferTooSmall$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String wrongBufferExpansion$str() {
      return "XNIO000302: SSLEngine required a bigger send buffer but our buffer was already big enough";
   }

   public final IOException wrongBufferExpansion() {
      IOException result = new IOException(String.format(this.getLoggingLocale(), this.wrongBufferExpansion$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String unexpectedWrapResult$str() {
      return "XNIO000303: Unexpected wrap result status: %s";
   }

   public final IOException unexpectedWrapResult(SSLEngineResult.Status status) {
      IOException result = new IOException(String.format(this.getLoggingLocale(), this.unexpectedWrapResult$str(), status));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String unexpectedHandshakeStatus$str() {
      return "XNIO000304: Unexpected handshake status: %s";
   }

   public final IOException unexpectedHandshakeStatus(SSLEngineResult.HandshakeStatus status) {
      IOException result = new IOException(String.format(this.getLoggingLocale(), this.unexpectedHandshakeStatus$str(), status));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String unexpectedUnwrapResult$str() {
      return "XNIO000305: Unexpected unwrap result status: %s";
   }

   public final IOException unexpectedUnwrapResult(SSLEngineResult.Status status) {
      IOException result = new IOException(String.format(this.getLoggingLocale(), this.unexpectedUnwrapResult$str(), status));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String notFromThisProvider$str() {
      return "XNIO000306: SSL connection is not from this provider";
   }

   public final IllegalArgumentException notFromThisProvider() {
      IllegalArgumentException result = new IllegalArgumentException(String.format(this.getLoggingLocale(), this.notFromThisProvider$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   public final void failedToCloseSSLEngine(Throwable cause, Exception originalException) {
      super.log.logf((String)FQCN, (Logger.Level)Logger.Level.WARN, (Throwable)cause, (String)this.failedToCloseSSLEngine$str(), (Object)originalException);
   }

   protected String failedToCloseSSLEngine$str() {
      return "XNIO000307: Failed to close ssl engine when handling exception %s";
   }

   protected String readTimeout$str() {
      return "XNIO000800: Read timed out";
   }

   public final ReadTimeoutException readTimeout() {
      ReadTimeoutException result = new ReadTimeoutException(String.format(this.getLoggingLocale(), this.readTimeout$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String writeTimeout$str() {
      return "XNIO000801: Write timed out";
   }

   public final WriteTimeoutException writeTimeout() {
      WriteTimeoutException result = new WriteTimeoutException(String.format(this.getLoggingLocale(), this.writeTimeout$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String fixedOverflow$str() {
      return "XNIO000802: Write past the end of a fixed-length channel";
   }

   public final FixedLengthOverflowException fixedOverflow() {
      FixedLengthOverflowException result = new FixedLengthOverflowException(String.format(this.getLoggingLocale(), this.fixedOverflow$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String fixedUnderflow$str() {
      return "XNIO000803: Close before all bytes were written to a fixed-length channel (%d bytes remaining)";
   }

   public final FixedLengthUnderflowException fixedUnderflow(long remaining) {
      FixedLengthUnderflowException result = new FixedLengthUnderflowException(String.format(this.getLoggingLocale(), this.fixedUnderflow$str(), remaining));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String recvInvalidMsgLength$str() {
      return "XNIO000804: Received an invalid message length of %d";
   }

   public final IOException recvInvalidMsgLength(int length) {
      IOException result = new IOException(String.format(this.getLoggingLocale(), this.recvInvalidMsgLength$str(), length));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String writeShutDown$str() {
      return "XNIO000805: Writes have been shut down";
   }

   public final EOFException writeShutDown() {
      EOFException result = new EOFException(String.format(this.getLoggingLocale(), this.writeShutDown$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String txMsgTooLarge$str() {
      return "XNIO000806: Transmitted message is too large";
   }

   public final IOException txMsgTooLarge() {
      IOException result = new IOException(String.format(this.getLoggingLocale(), this.txMsgTooLarge$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String unflushedData$str() {
      return "XNIO000807: Unflushed data truncated";
   }

   public final IOException unflushedData() {
      IOException result = new IOException(String.format(this.getLoggingLocale(), this.unflushedData$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String interruptedIO$str() {
      return "XNIO000808: I/O operation was interrupted";
   }

   public final InterruptedIOException interruptedIO() {
      InterruptedIOException result = new InterruptedIOException(String.format(this.getLoggingLocale(), this.interruptedIO$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   public final InterruptedIOException interruptedIO(int bytesTransferred) {
      InterruptedIOException result = new InterruptedIOException(String.format(this.getLoggingLocale(), this.interruptedIO$str()));
      _copyStackTraceMinusOne(result);
      result.bytesTransferred = bytesTransferred;
      return result;
   }

   protected String flushSmallBuffer$str() {
      return "XNIO000809: Cannot flush due to insufficient buffer space";
   }

   public final IOException flushSmallBuffer() {
      IOException result = new IOException(String.format(this.getLoggingLocale(), this.flushSmallBuffer$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String deflaterState$str() {
      return "XNIO000810: Deflater doesn't need input, but won't produce output";
   }

   public final IOException deflaterState() {
      IOException result = new IOException(String.format(this.getLoggingLocale(), this.deflaterState$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String inflaterNeedsDictionary$str() {
      return "XNIO000811: Inflater needs dictionary";
   }

   public final IOException inflaterNeedsDictionary() {
      IOException result = new IOException(String.format(this.getLoggingLocale(), this.inflaterNeedsDictionary$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String connectionClosedEarly$str() {
      return "XNIO000812: Connection closed unexpectedly";
   }

   public final EOFException connectionClosedEarly() {
      EOFException result = new EOFException(String.format(this.getLoggingLocale(), this.connectionClosedEarly$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String streamClosed$str() {
      return "XNIO000813: The stream is closed";
   }

   public final IOException streamClosed() {
      IOException result = new IOException(String.format(this.getLoggingLocale(), this.streamClosed$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String markNotSet$str() {
      return "XNIO000814: Mark not set";
   }

   public final IOException markNotSet() {
      IOException result = new IOException(String.format(this.getLoggingLocale(), this.markNotSet$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String redirect$str() {
      return "XNIO000816: Redirect encountered establishing connection";
   }

   public final String redirect() {
      return String.format(this.getLoggingLocale(), this.redirect$str());
   }

   protected String unsupported$str() {
      return "XNIO000900: Method '%s' is not supported on this implementation";
   }

   public final UnsupportedOperationException unsupported(String methodName) {
      UnsupportedOperationException result = new UnsupportedOperationException(String.format(this.getLoggingLocale(), this.unsupported$str(), methodName));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String blockingNotAllowed$str() {
      return "XNIO001000: Blocking I/O is not allowed on the current thread";
   }

   public final IllegalStateException blockingNotAllowed() {
      IllegalStateException result = new IllegalStateException(String.format(this.getLoggingLocale(), this.blockingNotAllowed$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String noProviderFound$str() {
      return "XNIO001001: No XNIO provider found";
   }

   public final IllegalArgumentException noProviderFound() {
      IllegalArgumentException result = new IllegalArgumentException(String.format(this.getLoggingLocale(), this.noProviderFound$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String opCancelled$str() {
      return "XNIO001002: Operation was cancelled";
   }

   public final CancellationException opCancelled() {
      CancellationException result = new CancellationException(String.format(this.getLoggingLocale(), this.opCancelled$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   public final void notifierFailed(Throwable cause, IoFuture.Notifier<?, ?> notifier, Object attachment) {
      super.log.logf((String)FQCN, (Logger.Level)Logger.Level.WARN, (Throwable)cause, (String)this.notifierFailed$str(), notifier, attachment);
   }

   protected String notifierFailed$str() {
      return "XNIO001003: Running IoFuture notifier %s (with attachment %s) failed";
   }

   protected String opTimedOut$str() {
      return "XNIO001004: Operation timed out";
   }

   public final TimeoutException opTimedOut() {
      TimeoutException result = new TimeoutException(String.format(this.getLoggingLocale(), this.opTimedOut$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String propReadForbidden$str() {
      return "XNIO001005: Not allowed to read non-XNIO properties";
   }

   public final SecurityException propReadForbidden() {
      SecurityException result = new SecurityException(String.format(this.getLoggingLocale(), this.propReadForbidden$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   public final void failedToInvokeFileWatchCallback(Throwable cause) {
      super.log.logf(FQCN, Logger.Level.ERROR, cause, this.failedToInvokeFileWatchCallback$str());
   }

   protected String failedToInvokeFileWatchCallback$str() {
      return "XNIO001006: Failed to invoke file watch callback";
   }

   public final void listenerException(Throwable cause) {
      super.log.logf(FQCN, Logger.Level.ERROR, cause, this.listenerException$str());
   }

   protected String listenerException$str() {
      return "XNIO001007: A channel event listener threw an exception";
   }

   public final void exceptionHandlerException(Throwable cause) {
      super.log.logf(FQCN, Logger.Level.ERROR, cause, this.exceptionHandlerException$str());
   }

   protected String exceptionHandlerException$str() {
      return "XNIO001008: A channel exception handler threw an exception";
   }

   public final void acceptFailed(AcceptingChannel<? extends ConnectedChannel> channel, IOException reason) {
      super.log.logf((String)FQCN, (Logger.Level)Logger.Level.ERROR, (Throwable)reason, (String)this.acceptFailed$str(), (Object)channel);
   }

   protected String acceptFailed$str() {
      return "XNIO001009: Failed to accept a connection on %s";
   }

   public final void executorSubmitFailed(RejectedExecutionException cause, Channel channel) {
      super.log.logf((String)FQCN, (Logger.Level)Logger.Level.ERROR, (Throwable)cause, (String)this.executorSubmitFailed$str(), (Object)channel);
   }

   protected String executorSubmitFailed$str() {
      return "XNIO001010: Failed to submit task to executor: (closing %s)";
   }

   public final void closingResource(Object resource) {
      super.log.logf((String)FQCN, (Logger.Level)Logger.Level.TRACE, (Throwable)null, (String)this.closingResource$str(), (Object)resource);
   }

   protected String closingResource$str() {
      return "Closing resource %s";
   }

   public final void resourceCloseFailed(Throwable cause, Object resource) {
      super.log.logf(FQCN, Logger.Level.TRACE, cause, this.resourceCloseFailed$str(), resource);
   }

   protected String resourceCloseFailed$str() {
      return "Closing resource %s failed";
   }

   public final void resourceReadShutdownFailed(Throwable cause, Object resource) {
      super.log.logf(FQCN, Logger.Level.TRACE, cause, this.resourceReadShutdownFailed$str(), resource);
   }

   protected String resourceReadShutdownFailed$str() {
      return "Shutting down reads on %s failed";
   }

   public final void expandedSslBufferEnabled(int bufferSize) {
      super.log.logf((String)FQCN, (Logger.Level)Logger.Level.TRACE, (Throwable)null, (String)this.expandedSslBufferEnabled$str(), (Object)bufferSize);
   }

   protected String expandedSslBufferEnabled$str() {
      return "Expanded buffer enabled due to overflow with empty buffer, expanded buffer size is %s";
   }

   static {
      LOCALE = Locale.ROOT;
   }
}
