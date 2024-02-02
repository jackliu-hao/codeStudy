/*     */ package org.xnio._private;
/*     */ 
/*     */ import java.io.CharConversionException;
/*     */ import java.io.EOFException;
/*     */ import java.io.IOException;
/*     */ import java.io.InterruptedIOException;
/*     */ import java.io.Serializable;
/*     */ import java.net.InetAddress;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.net.SocketAddress;
/*     */ import java.nio.BufferOverflowException;
/*     */ import java.nio.BufferUnderflowException;
/*     */ import java.nio.ReadOnlyBufferException;
/*     */ import java.nio.channels.Channel;
/*     */ import java.util.Arrays;
/*     */ import java.util.Locale;
/*     */ import java.util.concurrent.CancellationException;
/*     */ import java.util.concurrent.RejectedExecutionException;
/*     */ import java.util.concurrent.TimeoutException;
/*     */ import javax.net.ssl.SSLEngineResult;
/*     */ import javax.security.sasl.SaslException;
/*     */ import org.jboss.logging.BasicLogger;
/*     */ import org.jboss.logging.DelegatingBasicLogger;
/*     */ import org.jboss.logging.Logger;
/*     */ import org.xnio.IoFuture;
/*     */ import org.xnio.channels.AcceptingChannel;
/*     */ import org.xnio.channels.ConcurrentStreamChannelAccessException;
/*     */ import org.xnio.channels.ConnectedChannel;
/*     */ import org.xnio.channels.FixedLengthOverflowException;
/*     */ import org.xnio.channels.FixedLengthUnderflowException;
/*     */ import org.xnio.channels.ReadTimeoutException;
/*     */ import org.xnio.channels.WriteTimeoutException;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Messages_$logger
/*     */   extends DelegatingBasicLogger
/*     */   implements Messages, BasicLogger, Serializable
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*  56 */   private static final String FQCN = Messages_$logger.class.getName();
/*     */   public Messages_$logger(Logger log) {
/*  58 */     super(log);
/*     */   }
/*  60 */   private static final Locale LOCALE = Locale.ROOT;
/*     */   protected Locale getLoggingLocale() {
/*  62 */     return LOCALE;
/*     */   }
/*     */   
/*     */   public final void greeting(String version) {
/*  66 */     this.log.logf(FQCN, Logger.Level.INFO, null, greeting$str(), version);
/*     */   }
/*     */   protected String greeting$str() {
/*  69 */     return "XNIO version %s";
/*     */   }
/*     */   protected String nullParameter$str() {
/*  72 */     return "Method parameter '%s' cannot be null";
/*     */   }
/*     */   
/*     */   public final IllegalArgumentException nullParameter(String name) {
/*  76 */     IllegalArgumentException result = new IllegalArgumentException(String.format(getLoggingLocale(), nullParameter$str(), new Object[] { name }));
/*  77 */     _copyStackTraceMinusOne(result);
/*  78 */     return result;
/*     */   }
/*     */   private static void _copyStackTraceMinusOne(Throwable e) {
/*  81 */     StackTraceElement[] st = e.getStackTrace();
/*  82 */     e.setStackTrace(Arrays.<StackTraceElement>copyOfRange(st, 1, st.length));
/*     */   }
/*     */   protected String minRange$str() {
/*  85 */     return "XNIO000001: Method parameter '%s' must be greater than %d";
/*     */   }
/*     */   
/*     */   public final IllegalArgumentException minRange(String paramName, int minimumValue) {
/*  89 */     IllegalArgumentException result = new IllegalArgumentException(String.format(getLoggingLocale(), minRange$str(), new Object[] { paramName, Integer.valueOf(minimumValue) }));
/*  90 */     _copyStackTraceMinusOne(result);
/*  91 */     return result;
/*     */   }
/*     */   protected String badSockType$str() {
/*  94 */     return "XNIO000002: Unsupported socket address %s";
/*     */   }
/*     */   
/*     */   public final IllegalArgumentException badSockType(Class<? extends SocketAddress> type) {
/*  98 */     IllegalArgumentException result = new IllegalArgumentException(String.format(getLoggingLocale(), badSockType$str(), new Object[] { type }));
/*  99 */     _copyStackTraceMinusOne(result);
/* 100 */     return result;
/*     */   }
/*     */   protected String nullArrayIndex$str() {
/* 103 */     return "XNIO000003: Method parameter '%s' contains disallowed null value at index %d";
/*     */   }
/*     */   
/*     */   public final IllegalArgumentException nullArrayIndex(String paramName, int index) {
/* 107 */     IllegalArgumentException result = new IllegalArgumentException(String.format(getLoggingLocale(), nullArrayIndex$str(), new Object[] { paramName, Integer.valueOf(index) }));
/* 108 */     _copyStackTraceMinusOne(result);
/* 109 */     return result;
/*     */   }
/*     */   protected String mismatchSockType$str() {
/* 112 */     return "XNIO000004: Bind address %s is not the same type as destination address %s";
/*     */   }
/*     */   
/*     */   public final IllegalArgumentException mismatchSockType(Class<? extends SocketAddress> bindType, Class<? extends SocketAddress> destType) {
/* 116 */     IllegalArgumentException result = new IllegalArgumentException(String.format(getLoggingLocale(), mismatchSockType$str(), new Object[] { bindType, destType }));
/* 117 */     _copyStackTraceMinusOne(result);
/* 118 */     return result;
/*     */   }
/*     */   protected String noField$str() {
/* 121 */     return "XNIO000005: No such field named \"%s\" in %s";
/*     */   }
/*     */   
/*     */   public final IllegalArgumentException noField(String fieldName, Class<?> clazz) {
/* 125 */     IllegalArgumentException result = new IllegalArgumentException(String.format(getLoggingLocale(), noField$str(), new Object[] { fieldName, clazz }));
/* 126 */     _copyStackTraceMinusOne(result);
/* 127 */     return result;
/*     */   }
/*     */   protected String optionClassNotFound$str() {
/* 130 */     return "XNIO000006: Class \"%s\" not found in %s";
/*     */   }
/*     */   
/*     */   public final IllegalArgumentException optionClassNotFound(String className, ClassLoader classLoader) {
/* 134 */     IllegalArgumentException result = new IllegalArgumentException(String.format(getLoggingLocale(), optionClassNotFound$str(), new Object[] { className, classLoader }));
/* 135 */     _copyStackTraceMinusOne(result);
/* 136 */     return result;
/*     */   }
/*     */   protected String fieldNotAccessible$str() {
/* 139 */     return "XNIO000007: Field named \"%s\" is not accessible (public) in %s";
/*     */   }
/*     */   
/*     */   public final IllegalArgumentException fieldNotAccessible(String fieldName, Class<?> clazz) {
/* 143 */     IllegalArgumentException result = new IllegalArgumentException(String.format(getLoggingLocale(), fieldNotAccessible$str(), new Object[] { fieldName, clazz }));
/* 144 */     _copyStackTraceMinusOne(result);
/* 145 */     return result;
/*     */   }
/*     */   protected String fieldNotStatic$str() {
/* 148 */     return "XNIO000008: Field named \"%s\" is not static in %s";
/*     */   }
/*     */   
/*     */   public final IllegalArgumentException fieldNotStatic(String fieldName, Class<?> clazz) {
/* 152 */     IllegalArgumentException result = new IllegalArgumentException(String.format(getLoggingLocale(), fieldNotStatic$str(), new Object[] { fieldName, clazz }));
/* 153 */     _copyStackTraceMinusOne(result);
/* 154 */     return result;
/*     */   }
/*     */   protected String copyNegative$str() {
/* 157 */     return "XNIO000009: Copy with negative count is not supported";
/*     */   }
/*     */   
/*     */   public final UnsupportedOperationException copyNegative() {
/* 161 */     UnsupportedOperationException result = new UnsupportedOperationException(String.format(getLoggingLocale(), copyNegative$str(), new Object[0]));
/* 162 */     _copyStackTraceMinusOne(result);
/* 163 */     return result;
/*     */   }
/*     */   
/*     */   public final void invalidOptionInProperty(String optionName, String name, Throwable problem) {
/* 167 */     this.log.logf(FQCN, Logger.Level.WARN, null, invalidOptionInProperty$str(), optionName, name, problem);
/*     */   }
/*     */   protected String invalidOptionInProperty$str() {
/* 170 */     return "XNIO000010: Invalid option '%s' in property '%s': %s";
/*     */   }
/*     */   protected String readOnlyBuffer$str() {
/* 173 */     return "XNIO000012: Attempt to write to a read-only buffer";
/*     */   }
/*     */   
/*     */   public final ReadOnlyBufferException readOnlyBuffer() {
/* 177 */     ReadOnlyBufferException result = new ReadOnlyBufferException();
/* 178 */     _copyStackTraceMinusOne(result);
/* 179 */     return result;
/*     */   }
/*     */   protected String bufferUnderflow$str() {
/* 182 */     return "XNIO000013: Buffer underflow";
/*     */   }
/*     */   
/*     */   public final BufferUnderflowException bufferUnderflow() {
/* 186 */     BufferUnderflowException result = new BufferUnderflowException();
/* 187 */     _copyStackTraceMinusOne(result);
/* 188 */     return result;
/*     */   }
/*     */   protected String bufferOverflow$str() {
/* 191 */     return "XNIO000014: Buffer overflow";
/*     */   }
/*     */   
/*     */   public final BufferOverflowException bufferOverflow() {
/* 195 */     BufferOverflowException result = new BufferOverflowException();
/* 196 */     _copyStackTraceMinusOne(result);
/* 197 */     return result;
/*     */   }
/*     */   protected String parameterOutOfRange$str() {
/* 200 */     return "XNIO000015: Parameter '%s' is out of range";
/*     */   }
/*     */   
/*     */   public final IllegalArgumentException parameterOutOfRange(String name) {
/* 204 */     IllegalArgumentException result = new IllegalArgumentException(String.format(getLoggingLocale(), parameterOutOfRange$str(), new Object[] { name }));
/* 205 */     _copyStackTraceMinusOne(result);
/* 206 */     return result;
/*     */   }
/*     */   protected String mixedDirectAndHeap$str() {
/* 209 */     return "XNIO000016: Mixed direct and heap buffers not allowed";
/*     */   }
/*     */   
/*     */   public final IllegalArgumentException mixedDirectAndHeap() {
/* 213 */     IllegalArgumentException result = new IllegalArgumentException(String.format(getLoggingLocale(), mixedDirectAndHeap$str(), new Object[0]));
/* 214 */     _copyStackTraceMinusOne(result);
/* 215 */     return result;
/*     */   }
/*     */   protected String bufferFreed$str() {
/* 218 */     return "XNIO000017: Buffer was already freed";
/*     */   }
/*     */   
/*     */   public final IllegalStateException bufferFreed() {
/* 222 */     IllegalStateException result = new IllegalStateException(String.format(getLoggingLocale(), bufferFreed$str(), new Object[0]));
/* 223 */     _copyStackTraceMinusOne(result);
/* 224 */     return result;
/*     */   }
/*     */   protected String randomWrongThread$str() {
/* 227 */     return "XNIO000018: Access a thread-local random from the wrong thread";
/*     */   }
/*     */   
/*     */   public final IllegalStateException randomWrongThread() {
/* 231 */     IllegalStateException result = new IllegalStateException(String.format(getLoggingLocale(), randomWrongThread$str(), new Object[0]));
/* 232 */     _copyStackTraceMinusOne(result);
/* 233 */     return result;
/*     */   }
/*     */   protected String channelNotAvailable$str() {
/* 236 */     return "XNIO000019: Channel not available from connection";
/*     */   }
/*     */   
/*     */   public final IllegalStateException channelNotAvailable() {
/* 240 */     IllegalStateException result = new IllegalStateException(String.format(getLoggingLocale(), channelNotAvailable$str(), new Object[0]));
/* 241 */     _copyStackTraceMinusOne(result);
/* 242 */     return result;
/*     */   }
/*     */   protected String noOptionParser$str() {
/* 245 */     return "XNIO000020: No parser for this option value type";
/*     */   }
/*     */   
/*     */   public final IllegalArgumentException noOptionParser() {
/* 249 */     IllegalArgumentException result = new IllegalArgumentException(String.format(getLoggingLocale(), noOptionParser$str(), new Object[0]));
/* 250 */     _copyStackTraceMinusOne(result);
/* 251 */     return result;
/*     */   }
/*     */   protected String invalidOptionPropertyFormat$str() {
/* 254 */     return "XNIO000021: Invalid format for property value '%s'";
/*     */   }
/*     */   
/*     */   public final IllegalArgumentException invalidOptionPropertyFormat(String value) {
/* 258 */     IllegalArgumentException result = new IllegalArgumentException(String.format(getLoggingLocale(), invalidOptionPropertyFormat$str(), new Object[] { value }));
/* 259 */     _copyStackTraceMinusOne(result);
/* 260 */     return result;
/*     */   }
/*     */   protected String classNotFound$str() {
/* 263 */     return "XNIO000022: Class %s not found";
/*     */   }
/*     */   
/*     */   public final IllegalArgumentException classNotFound(String name, ClassNotFoundException cause) {
/* 267 */     IllegalArgumentException result = new IllegalArgumentException(String.format(getLoggingLocale(), classNotFound$str(), new Object[] { name }), cause);
/* 268 */     _copyStackTraceMinusOne(result);
/* 269 */     return result;
/*     */   }
/*     */   protected String classNotInstance$str() {
/* 272 */     return "XNIO000023: Class %s is not an instance of %s";
/*     */   }
/*     */   
/*     */   public final IllegalArgumentException classNotInstance(String name, Class<?> expectedType) {
/* 276 */     IllegalArgumentException result = new IllegalArgumentException(String.format(getLoggingLocale(), classNotInstance$str(), new Object[] { name, expectedType }));
/* 277 */     _copyStackTraceMinusOne(result);
/* 278 */     return result;
/*     */   }
/*     */   protected String invalidOptionName$str() {
/* 281 */     return "XNIO000024: Invalid option name '%s'";
/*     */   }
/*     */   
/*     */   public final IllegalArgumentException invalidOptionName(String name) {
/* 285 */     IllegalArgumentException result = new IllegalArgumentException(String.format(getLoggingLocale(), invalidOptionName$str(), new Object[] { name }));
/* 286 */     _copyStackTraceMinusOne(result);
/* 287 */     return result;
/*     */   }
/*     */   protected String invalidNullOption$str() {
/* 290 */     return "XNIO000025: Invalid null option '%s'";
/*     */   }
/*     */   
/*     */   public final IllegalArgumentException invalidNullOption(String name) {
/* 294 */     IllegalArgumentException result = new IllegalArgumentException(String.format(getLoggingLocale(), invalidNullOption$str(), new Object[] { name }));
/* 295 */     _copyStackTraceMinusOne(result);
/* 296 */     return result;
/*     */   }
/*     */   protected String readAppendNotSupported$str() {
/* 299 */     return "XNIO000026: Read with append is not supported";
/*     */   }
/*     */   
/*     */   public final IOException readAppendNotSupported() {
/* 303 */     IOException result = new IOException(String.format(getLoggingLocale(), readAppendNotSupported$str(), new Object[0]));
/* 304 */     _copyStackTraceMinusOne(result);
/* 305 */     return result;
/*     */   }
/*     */   protected String openModeRequires7$str() {
/* 308 */     return "XNIO000027: Requested file open mode requires Java 7 or higher";
/*     */   }
/*     */   
/*     */   public final IOException openModeRequires7() {
/* 312 */     IOException result = new IOException(String.format(getLoggingLocale(), openModeRequires7$str(), new Object[0]));
/* 313 */     _copyStackTraceMinusOne(result);
/* 314 */     return result;
/*     */   }
/*     */   protected String xnioThreadRequired$str() {
/* 317 */     return "XNIO000028: Current thread is not an XNIO I/O thread";
/*     */   }
/*     */   
/*     */   public final IllegalStateException xnioThreadRequired() {
/* 321 */     IllegalStateException result = new IllegalStateException(String.format(getLoggingLocale(), xnioThreadRequired$str(), new Object[0]));
/* 322 */     _copyStackTraceMinusOne(result);
/* 323 */     return result;
/*     */   }
/*     */   protected String badCompressionFormat$str() {
/* 326 */     return "XNIO000029: Compression format not supported";
/*     */   }
/*     */   
/*     */   public final IllegalArgumentException badCompressionFormat() {
/* 330 */     IllegalArgumentException result = new IllegalArgumentException(String.format(getLoggingLocale(), badCompressionFormat$str(), new Object[0]));
/* 331 */     _copyStackTraceMinusOne(result);
/* 332 */     return result;
/*     */   }
/*     */   protected String differentWorkers$str() {
/* 335 */     return "XNIO000030: Both channels must come from the same worker";
/*     */   }
/*     */   
/*     */   public final IllegalArgumentException differentWorkers() {
/* 339 */     IllegalArgumentException result = new IllegalArgumentException(String.format(getLoggingLocale(), differentWorkers$str(), new Object[0]));
/* 340 */     _copyStackTraceMinusOne(result);
/* 341 */     return result;
/*     */   }
/*     */   protected String oneChannelMustBeConnection$str() {
/* 344 */     return "XNIO000031: At least one channel must have a connection";
/*     */   }
/*     */   
/*     */   public final IllegalArgumentException oneChannelMustBeConnection() {
/* 348 */     IllegalArgumentException result = new IllegalArgumentException(String.format(getLoggingLocale(), oneChannelMustBeConnection$str(), new Object[0]));
/* 349 */     _copyStackTraceMinusOne(result);
/* 350 */     return result;
/*     */   }
/*     */   protected String oneChannelMustBeSSL$str() {
/* 353 */     return "XNIO000032: At least one channel must be an SSL channel";
/*     */   }
/*     */   
/*     */   public final IllegalArgumentException oneChannelMustBeSSL() {
/* 357 */     IllegalArgumentException result = new IllegalArgumentException(String.format(getLoggingLocale(), oneChannelMustBeSSL$str(), new Object[0]));
/* 358 */     _copyStackTraceMinusOne(result);
/* 359 */     return result;
/*     */   }
/*     */   protected String invalidQop$str() {
/* 362 */     return "XNIO000033: '%s' is not a valid QOP value";
/*     */   }
/*     */   
/*     */   public final IllegalArgumentException invalidQop(String name) {
/* 366 */     IllegalArgumentException result = new IllegalArgumentException(String.format(getLoggingLocale(), invalidQop$str(), new Object[] { name }));
/* 367 */     _copyStackTraceMinusOne(result);
/* 368 */     return result;
/*     */   }
/*     */   protected String cantInstantiate$str() {
/* 371 */     return "XNIO000034: Failed to instantiate %s";
/*     */   }
/*     */   
/*     */   public final IllegalArgumentException cantInstantiate(Class<?> clazz, Throwable cause) {
/* 375 */     IllegalArgumentException result = new IllegalArgumentException(String.format(getLoggingLocale(), cantInstantiate$str(), new Object[] { clazz }), cause);
/* 376 */     _copyStackTraceMinusOne(result);
/* 377 */     return result;
/*     */   }
/*     */   protected String concurrentAccess$str() {
/* 380 */     return "XNIO000035: Stream channel was accessed concurrently";
/*     */   }
/*     */   
/*     */   public final ConcurrentStreamChannelAccessException concurrentAccess() {
/* 384 */     ConcurrentStreamChannelAccessException result = new ConcurrentStreamChannelAccessException(String.format(getLoggingLocale(), concurrentAccess$str(), new Object[0]));
/* 385 */     _copyStackTraceMinusOne((Throwable)result);
/* 386 */     return result;
/*     */   }
/*     */   protected String malformedInput$str() {
/* 389 */     return "XNIO000036: Malformed input";
/*     */   }
/*     */   
/*     */   public final CharConversionException malformedInput() {
/* 393 */     CharConversionException result = new CharConversionException(String.format(getLoggingLocale(), malformedInput$str(), new Object[0]));
/* 394 */     _copyStackTraceMinusOne(result);
/* 395 */     return result;
/*     */   }
/*     */   protected String unmappableCharacter$str() {
/* 398 */     return "XNIO000037: Unmappable character";
/*     */   }
/*     */   
/*     */   public final CharConversionException unmappableCharacter() {
/* 402 */     CharConversionException result = new CharConversionException(String.format(getLoggingLocale(), unmappableCharacter$str(), new Object[0]));
/* 403 */     _copyStackTraceMinusOne(result);
/* 404 */     return result;
/*     */   }
/*     */   protected String characterDecodingProblem$str() {
/* 407 */     return "XNIO000038: Character decoding problem";
/*     */   }
/*     */   
/*     */   public final CharConversionException characterDecodingProblem() {
/* 411 */     CharConversionException result = new CharConversionException(String.format(getLoggingLocale(), characterDecodingProblem$str(), new Object[0]));
/* 412 */     _copyStackTraceMinusOne(result);
/* 413 */     return result;
/*     */   }
/*     */   protected String mismatchAddressType$str() {
/* 416 */     return "XNIO000040: Mismatched IP address type; expected %s but got %s";
/*     */   }
/*     */   
/*     */   public final IllegalArgumentException mismatchAddressType(Class<? extends InetAddress> expected, Class<? extends InetAddress> actual) {
/* 420 */     IllegalArgumentException result = new IllegalArgumentException(String.format(getLoggingLocale(), mismatchAddressType$str(), new Object[] { expected, actual }));
/* 421 */     _copyStackTraceMinusOne(result);
/* 422 */     return result;
/*     */   }
/*     */   protected String invalidStrength$str() {
/* 425 */     return "XNIO000041: '%s' is not a valid Strength value";
/*     */   }
/*     */   
/*     */   public final IllegalArgumentException invalidStrength(String name) {
/* 429 */     IllegalArgumentException result = new IllegalArgumentException(String.format(getLoggingLocale(), invalidStrength$str(), new Object[] { name }));
/* 430 */     _copyStackTraceMinusOne(result);
/* 431 */     return result;
/*     */   }
/*     */   protected String addressUnresolved$str() {
/* 434 */     return "XNIO000042: Cannot add unresolved address '%s'";
/*     */   }
/*     */   
/*     */   public final IllegalArgumentException addressUnresolved(InetSocketAddress bindAddress) {
/* 438 */     IllegalArgumentException result = new IllegalArgumentException(String.format(getLoggingLocale(), addressUnresolved$str(), new Object[] { bindAddress }));
/* 439 */     _copyStackTraceMinusOne(result);
/* 440 */     return result;
/*     */   }
/*     */   protected String missingSslProvider$str() {
/* 443 */     return "XNIO000100: 'https' URL scheme chosen but no SSL provider given";
/*     */   }
/*     */   
/*     */   public final IllegalArgumentException missingSslProvider() {
/* 447 */     IllegalArgumentException result = new IllegalArgumentException(String.format(getLoggingLocale(), missingSslProvider$str(), new Object[0]));
/* 448 */     _copyStackTraceMinusOne(result);
/* 449 */     return result;
/*     */   }
/*     */   protected String invalidURLScheme$str() {
/* 452 */     return "XNIO000101: Unknown URL scheme '%s' given; must be one of 'http' or 'https'";
/*     */   }
/*     */   
/*     */   public final IllegalArgumentException invalidURLScheme(String scheme) {
/* 456 */     IllegalArgumentException result = new IllegalArgumentException(String.format(getLoggingLocale(), invalidURLScheme$str(), new Object[] { scheme }));
/* 457 */     _copyStackTraceMinusOne(result);
/* 458 */     return result;
/*     */   }
/*     */   protected String extraChallenge$str() {
/* 461 */     return "XNIO000200: Unexpected extra SASL challenge data received";
/*     */   }
/*     */   
/*     */   public final SaslException extraChallenge() {
/* 465 */     SaslException result = new SaslException(String.format(getLoggingLocale(), extraChallenge$str(), new Object[0]));
/* 466 */     _copyStackTraceMinusOne(result);
/* 467 */     return result;
/*     */   }
/*     */   protected String extraResponse$str() {
/* 470 */     return "XNIO000201: Unexpected extra SASL response data received";
/*     */   }
/*     */   
/*     */   public final SaslException extraResponse() {
/* 474 */     SaslException result = new SaslException(String.format(getLoggingLocale(), extraResponse$str(), new Object[0]));
/* 475 */     _copyStackTraceMinusOne(result);
/* 476 */     return result;
/*     */   }
/*     */   protected String socketBufferTooSmall$str() {
/* 479 */     return "XNIO000300: Socket buffer is too small";
/*     */   }
/*     */   
/*     */   public final IllegalArgumentException socketBufferTooSmall() {
/* 483 */     IllegalArgumentException result = new IllegalArgumentException(String.format(getLoggingLocale(), socketBufferTooSmall$str(), new Object[0]));
/* 484 */     _copyStackTraceMinusOne(result);
/* 485 */     return result;
/*     */   }
/*     */   protected String appBufferTooSmall$str() {
/* 488 */     return "XNIO000301: Application buffer is too small";
/*     */   }
/*     */   
/*     */   public final IllegalArgumentException appBufferTooSmall() {
/* 492 */     IllegalArgumentException result = new IllegalArgumentException(String.format(getLoggingLocale(), appBufferTooSmall$str(), new Object[0]));
/* 493 */     _copyStackTraceMinusOne(result);
/* 494 */     return result;
/*     */   }
/*     */   protected String wrongBufferExpansion$str() {
/* 497 */     return "XNIO000302: SSLEngine required a bigger send buffer but our buffer was already big enough";
/*     */   }
/*     */   
/*     */   public final IOException wrongBufferExpansion() {
/* 501 */     IOException result = new IOException(String.format(getLoggingLocale(), wrongBufferExpansion$str(), new Object[0]));
/* 502 */     _copyStackTraceMinusOne(result);
/* 503 */     return result;
/*     */   }
/*     */   protected String unexpectedWrapResult$str() {
/* 506 */     return "XNIO000303: Unexpected wrap result status: %s";
/*     */   }
/*     */   
/*     */   public final IOException unexpectedWrapResult(SSLEngineResult.Status status) {
/* 510 */     IOException result = new IOException(String.format(getLoggingLocale(), unexpectedWrapResult$str(), new Object[] { status }));
/* 511 */     _copyStackTraceMinusOne(result);
/* 512 */     return result;
/*     */   }
/*     */   protected String unexpectedHandshakeStatus$str() {
/* 515 */     return "XNIO000304: Unexpected handshake status: %s";
/*     */   }
/*     */   
/*     */   public final IOException unexpectedHandshakeStatus(SSLEngineResult.HandshakeStatus status) {
/* 519 */     IOException result = new IOException(String.format(getLoggingLocale(), unexpectedHandshakeStatus$str(), new Object[] { status }));
/* 520 */     _copyStackTraceMinusOne(result);
/* 521 */     return result;
/*     */   }
/*     */   protected String unexpectedUnwrapResult$str() {
/* 524 */     return "XNIO000305: Unexpected unwrap result status: %s";
/*     */   }
/*     */   
/*     */   public final IOException unexpectedUnwrapResult(SSLEngineResult.Status status) {
/* 528 */     IOException result = new IOException(String.format(getLoggingLocale(), unexpectedUnwrapResult$str(), new Object[] { status }));
/* 529 */     _copyStackTraceMinusOne(result);
/* 530 */     return result;
/*     */   }
/*     */   protected String notFromThisProvider$str() {
/* 533 */     return "XNIO000306: SSL connection is not from this provider";
/*     */   }
/*     */   
/*     */   public final IllegalArgumentException notFromThisProvider() {
/* 537 */     IllegalArgumentException result = new IllegalArgumentException(String.format(getLoggingLocale(), notFromThisProvider$str(), new Object[0]));
/* 538 */     _copyStackTraceMinusOne(result);
/* 539 */     return result;
/*     */   }
/*     */   
/*     */   public final void failedToCloseSSLEngine(Throwable cause, Exception originalException) {
/* 543 */     this.log.logf(FQCN, Logger.Level.WARN, cause, failedToCloseSSLEngine$str(), originalException);
/*     */   }
/*     */   protected String failedToCloseSSLEngine$str() {
/* 546 */     return "XNIO000307: Failed to close ssl engine when handling exception %s";
/*     */   }
/*     */   protected String readTimeout$str() {
/* 549 */     return "XNIO000800: Read timed out";
/*     */   }
/*     */   
/*     */   public final ReadTimeoutException readTimeout() {
/* 553 */     ReadTimeoutException result = new ReadTimeoutException(String.format(getLoggingLocale(), readTimeout$str(), new Object[0]));
/* 554 */     _copyStackTraceMinusOne((Throwable)result);
/* 555 */     return result;
/*     */   }
/*     */   protected String writeTimeout$str() {
/* 558 */     return "XNIO000801: Write timed out";
/*     */   }
/*     */   
/*     */   public final WriteTimeoutException writeTimeout() {
/* 562 */     WriteTimeoutException result = new WriteTimeoutException(String.format(getLoggingLocale(), writeTimeout$str(), new Object[0]));
/* 563 */     _copyStackTraceMinusOne((Throwable)result);
/* 564 */     return result;
/*     */   }
/*     */   protected String fixedOverflow$str() {
/* 567 */     return "XNIO000802: Write past the end of a fixed-length channel";
/*     */   }
/*     */   
/*     */   public final FixedLengthOverflowException fixedOverflow() {
/* 571 */     FixedLengthOverflowException result = new FixedLengthOverflowException(String.format(getLoggingLocale(), fixedOverflow$str(), new Object[0]));
/* 572 */     _copyStackTraceMinusOne((Throwable)result);
/* 573 */     return result;
/*     */   }
/*     */   protected String fixedUnderflow$str() {
/* 576 */     return "XNIO000803: Close before all bytes were written to a fixed-length channel (%d bytes remaining)";
/*     */   }
/*     */   
/*     */   public final FixedLengthUnderflowException fixedUnderflow(long remaining) {
/* 580 */     FixedLengthUnderflowException result = new FixedLengthUnderflowException(String.format(getLoggingLocale(), fixedUnderflow$str(), new Object[] { Long.valueOf(remaining) }));
/* 581 */     _copyStackTraceMinusOne((Throwable)result);
/* 582 */     return result;
/*     */   }
/*     */   protected String recvInvalidMsgLength$str() {
/* 585 */     return "XNIO000804: Received an invalid message length of %d";
/*     */   }
/*     */   
/*     */   public final IOException recvInvalidMsgLength(int length) {
/* 589 */     IOException result = new IOException(String.format(getLoggingLocale(), recvInvalidMsgLength$str(), new Object[] { Integer.valueOf(length) }));
/* 590 */     _copyStackTraceMinusOne(result);
/* 591 */     return result;
/*     */   }
/*     */   protected String writeShutDown$str() {
/* 594 */     return "XNIO000805: Writes have been shut down";
/*     */   }
/*     */   
/*     */   public final EOFException writeShutDown() {
/* 598 */     EOFException result = new EOFException(String.format(getLoggingLocale(), writeShutDown$str(), new Object[0]));
/* 599 */     _copyStackTraceMinusOne(result);
/* 600 */     return result;
/*     */   }
/*     */   protected String txMsgTooLarge$str() {
/* 603 */     return "XNIO000806: Transmitted message is too large";
/*     */   }
/*     */   
/*     */   public final IOException txMsgTooLarge() {
/* 607 */     IOException result = new IOException(String.format(getLoggingLocale(), txMsgTooLarge$str(), new Object[0]));
/* 608 */     _copyStackTraceMinusOne(result);
/* 609 */     return result;
/*     */   }
/*     */   protected String unflushedData$str() {
/* 612 */     return "XNIO000807: Unflushed data truncated";
/*     */   }
/*     */   
/*     */   public final IOException unflushedData() {
/* 616 */     IOException result = new IOException(String.format(getLoggingLocale(), unflushedData$str(), new Object[0]));
/* 617 */     _copyStackTraceMinusOne(result);
/* 618 */     return result;
/*     */   }
/*     */   protected String interruptedIO$str() {
/* 621 */     return "XNIO000808: I/O operation was interrupted";
/*     */   }
/*     */   
/*     */   public final InterruptedIOException interruptedIO() {
/* 625 */     InterruptedIOException result = new InterruptedIOException(String.format(getLoggingLocale(), interruptedIO$str(), new Object[0]));
/* 626 */     _copyStackTraceMinusOne(result);
/* 627 */     return result;
/*     */   }
/*     */   
/*     */   public final InterruptedIOException interruptedIO(int bytesTransferred) {
/* 631 */     InterruptedIOException result = new InterruptedIOException(String.format(getLoggingLocale(), interruptedIO$str(), new Object[0]));
/* 632 */     _copyStackTraceMinusOne(result);
/* 633 */     result.bytesTransferred = bytesTransferred;
/* 634 */     return result;
/*     */   }
/*     */   protected String flushSmallBuffer$str() {
/* 637 */     return "XNIO000809: Cannot flush due to insufficient buffer space";
/*     */   }
/*     */   
/*     */   public final IOException flushSmallBuffer() {
/* 641 */     IOException result = new IOException(String.format(getLoggingLocale(), flushSmallBuffer$str(), new Object[0]));
/* 642 */     _copyStackTraceMinusOne(result);
/* 643 */     return result;
/*     */   }
/*     */   protected String deflaterState$str() {
/* 646 */     return "XNIO000810: Deflater doesn't need input, but won't produce output";
/*     */   }
/*     */   
/*     */   public final IOException deflaterState() {
/* 650 */     IOException result = new IOException(String.format(getLoggingLocale(), deflaterState$str(), new Object[0]));
/* 651 */     _copyStackTraceMinusOne(result);
/* 652 */     return result;
/*     */   }
/*     */   protected String inflaterNeedsDictionary$str() {
/* 655 */     return "XNIO000811: Inflater needs dictionary";
/*     */   }
/*     */   
/*     */   public final IOException inflaterNeedsDictionary() {
/* 659 */     IOException result = new IOException(String.format(getLoggingLocale(), inflaterNeedsDictionary$str(), new Object[0]));
/* 660 */     _copyStackTraceMinusOne(result);
/* 661 */     return result;
/*     */   }
/*     */   protected String connectionClosedEarly$str() {
/* 664 */     return "XNIO000812: Connection closed unexpectedly";
/*     */   }
/*     */   
/*     */   public final EOFException connectionClosedEarly() {
/* 668 */     EOFException result = new EOFException(String.format(getLoggingLocale(), connectionClosedEarly$str(), new Object[0]));
/* 669 */     _copyStackTraceMinusOne(result);
/* 670 */     return result;
/*     */   }
/*     */   protected String streamClosed$str() {
/* 673 */     return "XNIO000813: The stream is closed";
/*     */   }
/*     */   
/*     */   public final IOException streamClosed() {
/* 677 */     IOException result = new IOException(String.format(getLoggingLocale(), streamClosed$str(), new Object[0]));
/* 678 */     _copyStackTraceMinusOne(result);
/* 679 */     return result;
/*     */   }
/*     */   protected String markNotSet$str() {
/* 682 */     return "XNIO000814: Mark not set";
/*     */   }
/*     */   
/*     */   public final IOException markNotSet() {
/* 686 */     IOException result = new IOException(String.format(getLoggingLocale(), markNotSet$str(), new Object[0]));
/* 687 */     _copyStackTraceMinusOne(result);
/* 688 */     return result;
/*     */   }
/*     */   protected String redirect$str() {
/* 691 */     return "XNIO000816: Redirect encountered establishing connection";
/*     */   }
/*     */   
/*     */   public final String redirect() {
/* 695 */     return String.format(getLoggingLocale(), redirect$str(), new Object[0]);
/*     */   }
/*     */   protected String unsupported$str() {
/* 698 */     return "XNIO000900: Method '%s' is not supported on this implementation";
/*     */   }
/*     */   
/*     */   public final UnsupportedOperationException unsupported(String methodName) {
/* 702 */     UnsupportedOperationException result = new UnsupportedOperationException(String.format(getLoggingLocale(), unsupported$str(), new Object[] { methodName }));
/* 703 */     _copyStackTraceMinusOne(result);
/* 704 */     return result;
/*     */   }
/*     */   protected String blockingNotAllowed$str() {
/* 707 */     return "XNIO001000: Blocking I/O is not allowed on the current thread";
/*     */   }
/*     */   
/*     */   public final IllegalStateException blockingNotAllowed() {
/* 711 */     IllegalStateException result = new IllegalStateException(String.format(getLoggingLocale(), blockingNotAllowed$str(), new Object[0]));
/* 712 */     _copyStackTraceMinusOne(result);
/* 713 */     return result;
/*     */   }
/*     */   protected String noProviderFound$str() {
/* 716 */     return "XNIO001001: No XNIO provider found";
/*     */   }
/*     */   
/*     */   public final IllegalArgumentException noProviderFound() {
/* 720 */     IllegalArgumentException result = new IllegalArgumentException(String.format(getLoggingLocale(), noProviderFound$str(), new Object[0]));
/* 721 */     _copyStackTraceMinusOne(result);
/* 722 */     return result;
/*     */   }
/*     */   protected String opCancelled$str() {
/* 725 */     return "XNIO001002: Operation was cancelled";
/*     */   }
/*     */   
/*     */   public final CancellationException opCancelled() {
/* 729 */     CancellationException result = new CancellationException(String.format(getLoggingLocale(), opCancelled$str(), new Object[0]));
/* 730 */     _copyStackTraceMinusOne(result);
/* 731 */     return result;
/*     */   }
/*     */   
/*     */   public final void notifierFailed(Throwable cause, IoFuture.Notifier<?, ?> notifier, Object attachment) {
/* 735 */     this.log.logf(FQCN, Logger.Level.WARN, cause, notifierFailed$str(), notifier, attachment);
/*     */   }
/*     */   protected String notifierFailed$str() {
/* 738 */     return "XNIO001003: Running IoFuture notifier %s (with attachment %s) failed";
/*     */   }
/*     */   protected String opTimedOut$str() {
/* 741 */     return "XNIO001004: Operation timed out";
/*     */   }
/*     */   
/*     */   public final TimeoutException opTimedOut() {
/* 745 */     TimeoutException result = new TimeoutException(String.format(getLoggingLocale(), opTimedOut$str(), new Object[0]));
/* 746 */     _copyStackTraceMinusOne(result);
/* 747 */     return result;
/*     */   }
/*     */   protected String propReadForbidden$str() {
/* 750 */     return "XNIO001005: Not allowed to read non-XNIO properties";
/*     */   }
/*     */   
/*     */   public final SecurityException propReadForbidden() {
/* 754 */     SecurityException result = new SecurityException(String.format(getLoggingLocale(), propReadForbidden$str(), new Object[0]));
/* 755 */     _copyStackTraceMinusOne(result);
/* 756 */     return result;
/*     */   }
/*     */   
/*     */   public final void failedToInvokeFileWatchCallback(Throwable cause) {
/* 760 */     this.log.logf(FQCN, Logger.Level.ERROR, cause, failedToInvokeFileWatchCallback$str(), new Object[0]);
/*     */   }
/*     */   protected String failedToInvokeFileWatchCallback$str() {
/* 763 */     return "XNIO001006: Failed to invoke file watch callback";
/*     */   }
/*     */   
/*     */   public final void listenerException(Throwable cause) {
/* 767 */     this.log.logf(FQCN, Logger.Level.ERROR, cause, listenerException$str(), new Object[0]);
/*     */   }
/*     */   protected String listenerException$str() {
/* 770 */     return "XNIO001007: A channel event listener threw an exception";
/*     */   }
/*     */   
/*     */   public final void exceptionHandlerException(Throwable cause) {
/* 774 */     this.log.logf(FQCN, Logger.Level.ERROR, cause, exceptionHandlerException$str(), new Object[0]);
/*     */   }
/*     */   protected String exceptionHandlerException$str() {
/* 777 */     return "XNIO001008: A channel exception handler threw an exception";
/*     */   }
/*     */   
/*     */   public final void acceptFailed(AcceptingChannel<? extends ConnectedChannel> channel, IOException reason) {
/* 781 */     this.log.logf(FQCN, Logger.Level.ERROR, reason, acceptFailed$str(), channel);
/*     */   }
/*     */   protected String acceptFailed$str() {
/* 784 */     return "XNIO001009: Failed to accept a connection on %s";
/*     */   }
/*     */   
/*     */   public final void executorSubmitFailed(RejectedExecutionException cause, Channel channel) {
/* 788 */     this.log.logf(FQCN, Logger.Level.ERROR, cause, executorSubmitFailed$str(), channel);
/*     */   }
/*     */   protected String executorSubmitFailed$str() {
/* 791 */     return "XNIO001010: Failed to submit task to executor: (closing %s)";
/*     */   }
/*     */   
/*     */   public final void closingResource(Object resource) {
/* 795 */     this.log.logf(FQCN, Logger.Level.TRACE, null, closingResource$str(), resource);
/*     */   }
/*     */   protected String closingResource$str() {
/* 798 */     return "Closing resource %s";
/*     */   }
/*     */   
/*     */   public final void resourceCloseFailed(Throwable cause, Object resource) {
/* 802 */     this.log.logf(FQCN, Logger.Level.TRACE, cause, resourceCloseFailed$str(), resource);
/*     */   }
/*     */   protected String resourceCloseFailed$str() {
/* 805 */     return "Closing resource %s failed";
/*     */   }
/*     */   
/*     */   public final void resourceReadShutdownFailed(Throwable cause, Object resource) {
/* 809 */     this.log.logf(FQCN, Logger.Level.TRACE, cause, resourceReadShutdownFailed$str(), resource);
/*     */   }
/*     */   protected String resourceReadShutdownFailed$str() {
/* 812 */     return "Shutting down reads on %s failed";
/*     */   }
/*     */   
/*     */   public final void expandedSslBufferEnabled(int bufferSize) {
/* 816 */     this.log.logf(FQCN, Logger.Level.TRACE, null, expandedSslBufferEnabled$str(), Integer.valueOf(bufferSize));
/*     */   }
/*     */   protected String expandedSslBufferEnabled$str() {
/* 819 */     return "Expanded buffer enabled due to overflow with empty buffer, expanded buffer size is %s";
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\_private\Messages_$logger.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */