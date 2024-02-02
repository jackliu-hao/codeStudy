/*      */ package io.undertow;
/*      */ 
/*      */ import io.undertow.predicate.PredicateBuilder;
/*      */ import io.undertow.protocols.http2.HpackException;
/*      */ import io.undertow.security.api.AuthenticationMechanism;
/*      */ import io.undertow.server.RequestTooBigException;
/*      */ import io.undertow.server.handlers.builder.HandlerBuilder;
/*      */ import io.undertow.server.handlers.form.MultiPartParserDefinition;
/*      */ import io.undertow.util.BadRequestException;
/*      */ import io.undertow.util.HttpString;
/*      */ import io.undertow.util.ParameterLimitException;
/*      */ import io.undertow.util.UrlDecodeException;
/*      */ import java.io.IOException;
/*      */ import java.io.Serializable;
/*      */ import java.nio.channels.ClosedChannelException;
/*      */ import java.nio.file.Path;
/*      */ import java.util.Arrays;
/*      */ import java.util.Locale;
/*      */ import javax.net.ssl.SSLException;
/*      */ import javax.net.ssl.SSLHandshakeException;
/*      */ import javax.net.ssl.SSLPeerUnverifiedException;
/*      */ import org.xnio.channels.ReadTimeoutException;
/*      */ import org.xnio.channels.WriteTimeoutException;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class UndertowMessages_$bundle
/*      */   implements UndertowMessages, Serializable
/*      */ {
/*      */   private static final long serialVersionUID = 1L;
/*   40 */   public static final UndertowMessages_$bundle INSTANCE = new UndertowMessages_$bundle();
/*      */   protected Object readResolve() {
/*   42 */     return INSTANCE;
/*      */   }
/*   44 */   private static final Locale LOCALE = Locale.ROOT;
/*      */   protected Locale getLoggingLocale() {
/*   46 */     return LOCALE;
/*      */   }
/*      */   protected String maximumConcurrentRequestsMustBeLargerThanZero$str() {
/*   49 */     return "UT000001: Maximum concurrent requests must be larger than zero.";
/*      */   }
/*      */   
/*      */   public final IllegalArgumentException maximumConcurrentRequestsMustBeLargerThanZero() {
/*   53 */     IllegalArgumentException result = new IllegalArgumentException(String.format(getLoggingLocale(), maximumConcurrentRequestsMustBeLargerThanZero$str(), new Object[0]));
/*   54 */     _copyStackTraceMinusOne(result);
/*   55 */     return result;
/*      */   }
/*      */   private static void _copyStackTraceMinusOne(Throwable e) {
/*   58 */     StackTraceElement[] st = e.getStackTrace();
/*   59 */     e.setStackTrace(Arrays.<StackTraceElement>copyOfRange(st, 1, st.length));
/*      */   }
/*      */   protected String responseAlreadyStarted$str() {
/*   62 */     return "UT000002: The response has already been started";
/*      */   }
/*      */   
/*      */   public final IllegalStateException responseAlreadyStarted() {
/*   66 */     IllegalStateException result = new IllegalStateException(String.format(getLoggingLocale(), responseAlreadyStarted$str(), new Object[0]));
/*   67 */     _copyStackTraceMinusOne(result);
/*   68 */     return result;
/*      */   }
/*      */   protected String responseChannelAlreadyProvided$str() {
/*   71 */     return "UT000004: getResponseChannel() has already been called";
/*      */   }
/*      */   
/*      */   public final IllegalStateException responseChannelAlreadyProvided() {
/*   75 */     IllegalStateException result = new IllegalStateException(String.format(getLoggingLocale(), responseChannelAlreadyProvided$str(), new Object[0]));
/*   76 */     _copyStackTraceMinusOne(result);
/*   77 */     return result;
/*      */   }
/*      */   protected String requestChannelAlreadyProvided$str() {
/*   80 */     return "UT000005: getRequestChannel() has already been called";
/*      */   }
/*      */   
/*      */   public final IllegalStateException requestChannelAlreadyProvided() {
/*   84 */     IllegalStateException result = new IllegalStateException(String.format(getLoggingLocale(), requestChannelAlreadyProvided$str(), new Object[0]));
/*   85 */     _copyStackTraceMinusOne(result);
/*   86 */     return result;
/*      */   }
/*      */   protected String handlerCannotBeNull$str() {
/*   89 */     return "UT000008: Handler cannot be null";
/*      */   }
/*      */   
/*      */   public final IllegalArgumentException handlerCannotBeNull() {
/*   93 */     IllegalArgumentException result = new IllegalArgumentException(String.format(getLoggingLocale(), handlerCannotBeNull$str(), new Object[0]));
/*   94 */     _copyStackTraceMinusOne(result);
/*   95 */     return result;
/*      */   }
/*      */   protected String pathMustBeSpecified$str() {
/*   98 */     return "UT000009: Path must be specified";
/*      */   }
/*      */   
/*      */   public final IllegalArgumentException pathMustBeSpecified() {
/*  102 */     IllegalArgumentException result = new IllegalArgumentException(String.format(getLoggingLocale(), pathMustBeSpecified$str(), new Object[0]));
/*  103 */     _copyStackTraceMinusOne(result);
/*  104 */     return result;
/*      */   }
/*      */   protected String sessionIsInvalid$str() {
/*  107 */     return "UT000010: Session is invalid %s";
/*      */   }
/*      */   
/*      */   public final IllegalStateException sessionIsInvalid(String sessionId) {
/*  111 */     IllegalStateException result = new IllegalStateException(String.format(getLoggingLocale(), sessionIsInvalid$str(), new Object[] { sessionId }));
/*  112 */     _copyStackTraceMinusOne(result);
/*  113 */     return result;
/*      */   }
/*      */   protected String sessionManagerMustNotBeNull$str() {
/*  116 */     return "UT000011: Session manager must not be null";
/*      */   }
/*      */   
/*      */   public final IllegalStateException sessionManagerMustNotBeNull() {
/*  120 */     IllegalStateException result = new IllegalStateException(String.format(getLoggingLocale(), sessionManagerMustNotBeNull$str(), new Object[0]));
/*  121 */     _copyStackTraceMinusOne(result);
/*  122 */     return result;
/*      */   }
/*      */   protected String sessionManagerNotFound$str() {
/*  125 */     return "UT000012: Session manager was not attached to the request. Make sure that the SessionAttachmentHandler is installed in the handler chain";
/*      */   }
/*      */   
/*      */   public final IllegalStateException sessionManagerNotFound() {
/*  129 */     IllegalStateException result = new IllegalStateException(String.format(getLoggingLocale(), sessionManagerNotFound$str(), new Object[0]));
/*  130 */     _copyStackTraceMinusOne(result);
/*  131 */     return result;
/*      */   }
/*      */   protected String argumentCannotBeNull$str() {
/*  134 */     return "UT000013: Argument %s cannot be null";
/*      */   }
/*      */   
/*      */   public final IllegalArgumentException argumentCannotBeNull(String argument) {
/*  138 */     IllegalArgumentException result = new IllegalArgumentException(String.format(getLoggingLocale(), argumentCannotBeNull$str(), new Object[] { argument }));
/*  139 */     _copyStackTraceMinusOne(result);
/*  140 */     return result;
/*      */   }
/*      */   protected String formValueIsAFile$str() {
/*  143 */     return "UT000017: Form value is a file, use getFileItem() instead";
/*      */   }
/*      */   
/*      */   public final IllegalStateException formValueIsAFile() {
/*  147 */     IllegalStateException result = new IllegalStateException(String.format(getLoggingLocale(), formValueIsAFile$str(), new Object[0]));
/*  148 */     _copyStackTraceMinusOne(result);
/*  149 */     return result;
/*      */   }
/*      */   protected String formValueIsAString$str() {
/*  152 */     return "UT000018: Form value is a String, use getValue() instead";
/*      */   }
/*      */   
/*      */   public final IllegalStateException formValueIsAString() {
/*  156 */     IllegalStateException result = new IllegalStateException(String.format(getLoggingLocale(), formValueIsAString$str(), new Object[0]));
/*  157 */     _copyStackTraceMinusOne(result);
/*  158 */     return result;
/*      */   }
/*      */   protected String requestEntityWasTooLarge$str() {
/*  161 */     return "UT000020: Connection terminated as request was larger than %s";
/*      */   }
/*      */   
/*      */   public final RequestTooBigException requestEntityWasTooLarge(long size) {
/*  165 */     RequestTooBigException result = new RequestTooBigException(String.format(getLoggingLocale(), requestEntityWasTooLarge$str(), new Object[] { Long.valueOf(size) }));
/*  166 */     _copyStackTraceMinusOne((Throwable)result);
/*  167 */     return result;
/*      */   }
/*      */   protected String sessionAlreadyInvalidated$str() {
/*  170 */     return "UT000021: Session already invalidated";
/*      */   }
/*      */   
/*      */   public final IllegalStateException sessionAlreadyInvalidated() {
/*  174 */     IllegalStateException result = new IllegalStateException(String.format(getLoggingLocale(), sessionAlreadyInvalidated$str(), new Object[0]));
/*  175 */     _copyStackTraceMinusOne(result);
/*  176 */     return result;
/*      */   }
/*      */   protected String hashAlgorithmNotFound$str() {
/*  179 */     return "UT000022: The specified hash algorithm '%s' can not be found.";
/*      */   }
/*      */   
/*      */   public final IllegalArgumentException hashAlgorithmNotFound(String algorithmName) {
/*  183 */     IllegalArgumentException result = new IllegalArgumentException(String.format(getLoggingLocale(), hashAlgorithmNotFound$str(), new Object[] { algorithmName }));
/*  184 */     _copyStackTraceMinusOne(result);
/*  185 */     return result;
/*      */   }
/*      */   protected String invalidBase64Token$str() {
/*  188 */     return "UT000023: An invalid Base64 token has been received.";
/*      */   }
/*      */   
/*      */   public final IllegalArgumentException invalidBase64Token(IOException cause) {
/*  192 */     IllegalArgumentException result = new IllegalArgumentException(String.format(getLoggingLocale(), invalidBase64Token$str(), new Object[0]), cause);
/*  193 */     _copyStackTraceMinusOne(result);
/*  194 */     return result;
/*      */   }
/*      */   protected String invalidNonceReceived$str() {
/*  197 */     return "UT000024: An invalidly formatted nonce has been received.";
/*      */   }
/*      */   
/*      */   public final IllegalArgumentException invalidNonceReceived() {
/*  201 */     IllegalArgumentException result = new IllegalArgumentException(String.format(getLoggingLocale(), invalidNonceReceived$str(), new Object[0]));
/*  202 */     _copyStackTraceMinusOne(result);
/*  203 */     return result;
/*      */   }
/*      */   protected String unexpectedTokenInHeader$str() {
/*  206 */     return "UT000025: Unexpected token '%s' within header.";
/*      */   }
/*      */   
/*      */   public final IllegalArgumentException unexpectedTokenInHeader(String name) {
/*  210 */     IllegalArgumentException result = new IllegalArgumentException(String.format(getLoggingLocale(), unexpectedTokenInHeader$str(), new Object[] { name }));
/*  211 */     _copyStackTraceMinusOne(result);
/*  212 */     return result;
/*      */   }
/*      */   protected String invalidHeader$str() {
/*  215 */     return "UT000026: Invalid header received.";
/*      */   }
/*      */   
/*      */   public final IllegalArgumentException invalidHeader() {
/*  219 */     IllegalArgumentException result = new IllegalArgumentException(String.format(getLoggingLocale(), invalidHeader$str(), new Object[0]));
/*  220 */     _copyStackTraceMinusOne(result);
/*  221 */     return result;
/*      */   }
/*      */   protected String couldNotFindSessionCookieConfig$str() {
/*  224 */     return "UT000027: Could not find session cookie config in the request";
/*      */   }
/*      */   
/*      */   public final IllegalStateException couldNotFindSessionCookieConfig() {
/*  228 */     IllegalStateException result = new IllegalStateException(String.format(getLoggingLocale(), couldNotFindSessionCookieConfig$str(), new Object[0]));
/*  229 */     _copyStackTraceMinusOne(result);
/*  230 */     return result;
/*      */   }
/*      */   protected String chunkedChannelClosedMidChunk$str() {
/*  233 */     return "UT000029: Channel was closed mid chunk, if you have attempted to write chunked data you cannot shutdown the channel until after it has all been written.";
/*      */   }
/*      */   
/*      */   public final IOException chunkedChannelClosedMidChunk() {
/*  237 */     IOException result = new IOException(String.format(getLoggingLocale(), chunkedChannelClosedMidChunk$str(), new Object[0]));
/*  238 */     _copyStackTraceMinusOne(result);
/*  239 */     return result;
/*      */   }
/*      */   protected String userAuthenticated$str() {
/*  242 */     return "UT000030: User %s successfully authenticated.";
/*      */   }
/*      */   
/*      */   public final String userAuthenticated(String userName) {
/*  246 */     return String.format(getLoggingLocale(), userAuthenticated$str(), new Object[] { userName });
/*      */   }
/*      */   protected String userLoggedOut$str() {
/*  249 */     return "UT000031: User %s has logged out.";
/*      */   }
/*      */   
/*      */   public final String userLoggedOut(String userName) {
/*  253 */     return String.format(getLoggingLocale(), userLoggedOut$str(), new Object[] { userName });
/*      */   }
/*      */   protected String streamIsClosed$str() {
/*  256 */     return "UT000034: Stream is closed";
/*      */   }
/*      */   
/*      */   public final IOException streamIsClosed() {
/*  260 */     IOException result = new IOException(String.format(getLoggingLocale(), streamIsClosed$str(), new Object[0]));
/*  261 */     _copyStackTraceMinusOne(result);
/*  262 */     return result;
/*      */   }
/*      */   protected String startBlockingHasNotBeenCalled$str() {
/*  265 */     return "UT000035: Cannot get stream as startBlocking has not been invoked";
/*      */   }
/*      */   
/*      */   public final IllegalStateException startBlockingHasNotBeenCalled() {
/*  269 */     IllegalStateException result = new IllegalStateException(String.format(getLoggingLocale(), startBlockingHasNotBeenCalled$str(), new Object[0]));
/*  270 */     _copyStackTraceMinusOne(result);
/*  271 */     return result;
/*      */   }
/*      */   protected String connectionTerminatedReadingMultiPartData$str() {
/*  274 */     return "UT000036: Connection terminated parsing multipart data";
/*      */   }
/*      */   
/*      */   public final IOException connectionTerminatedReadingMultiPartData() {
/*  278 */     IOException result = new IOException(String.format(getLoggingLocale(), connectionTerminatedReadingMultiPartData$str(), new Object[0]));
/*  279 */     _copyStackTraceMinusOne(result);
/*  280 */     return result;
/*      */   }
/*      */   protected String failedToParsePath$str() {
/*  283 */     return "UT000037: Failed to parse path in HTTP request";
/*      */   }
/*      */   
/*      */   public final RuntimeException failedToParsePath() {
/*  287 */     RuntimeException result = new RuntimeException(String.format(getLoggingLocale(), failedToParsePath$str(), new Object[0]));
/*  288 */     _copyStackTraceMinusOne(result);
/*  289 */     return result;
/*      */   }
/*      */   protected String authenticationFailed$str() {
/*  292 */     return "UT000038: Authentication failed, requested user name '%s'";
/*      */   }
/*      */   
/*      */   public final String authenticationFailed(String userName) {
/*  296 */     return String.format(getLoggingLocale(), authenticationFailed$str(), new Object[] { userName });
/*      */   }
/*      */   protected String tooManyQueryParameters$str() {
/*  299 */     return "UT000039: Too many query parameters, cannot have more than %s query parameters";
/*      */   }
/*      */   
/*      */   public final BadRequestException tooManyQueryParameters(int noParams) {
/*  303 */     BadRequestException result = new BadRequestException(String.format(getLoggingLocale(), tooManyQueryParameters$str(), new Object[] { Integer.valueOf(noParams) }));
/*  304 */     _copyStackTraceMinusOne((Throwable)result);
/*  305 */     return result;
/*      */   }
/*      */   protected String tooManyHeaders$str() {
/*  308 */     return "UT000040: Too many headers, cannot have more than %s header";
/*      */   }
/*      */   
/*      */   public final String tooManyHeaders(int noParams) {
/*  312 */     return String.format(getLoggingLocale(), tooManyHeaders$str(), new Object[] { Integer.valueOf(noParams) });
/*      */   }
/*      */   protected String channelIsClosed$str() {
/*  315 */     return "UT000041: Channel is closed";
/*      */   }
/*      */   
/*      */   public final ClosedChannelException channelIsClosed() {
/*  319 */     ClosedChannelException result = new ClosedChannelException();
/*  320 */     _copyStackTraceMinusOne(result);
/*  321 */     return result;
/*      */   }
/*      */   protected String couldNotDecodeTrailers$str() {
/*  324 */     return "UT000042: Could not decode trailers in HTTP request";
/*      */   }
/*      */   
/*      */   public final IOException couldNotDecodeTrailers() {
/*  328 */     IOException result = new IOException(String.format(getLoggingLocale(), couldNotDecodeTrailers$str(), new Object[0]));
/*  329 */     _copyStackTraceMinusOne(result);
/*  330 */     return result;
/*      */   }
/*      */   protected String dataAlreadyQueued$str() {
/*  333 */     return "UT000043: Data is already being sent. You must wait for the completion callback to be be invoked before calling send() again";
/*      */   }
/*      */   
/*      */   public final IllegalStateException dataAlreadyQueued() {
/*  337 */     IllegalStateException result = new IllegalStateException(String.format(getLoggingLocale(), dataAlreadyQueued$str(), new Object[0]));
/*  338 */     _copyStackTraceMinusOne(result);
/*  339 */     return result;
/*      */   }
/*      */   protected String moreThanOnePredicateWithName$str() {
/*  342 */     return "UT000044: More than one predicate with name %s. Builder class %s and %s";
/*      */   }
/*      */   
/*      */   public final IllegalStateException moreThanOnePredicateWithName(String name, Class<? extends PredicateBuilder> aClass, Class<? extends PredicateBuilder> existing) {
/*  346 */     IllegalStateException result = new IllegalStateException(String.format(getLoggingLocale(), moreThanOnePredicateWithName$str(), new Object[] { name, aClass, existing }));
/*  347 */     _copyStackTraceMinusOne(result);
/*  348 */     return result;
/*      */   }
/*      */   protected String errorParsingPredicateString$str() {
/*  351 */     return "UT000045: Error parsing predicated handler string %s:%n%s";
/*      */   }
/*      */   
/*      */   public final IllegalArgumentException errorParsingPredicateString(String reason, String s) {
/*  355 */     IllegalArgumentException result = new IllegalArgumentException(String.format(getLoggingLocale(), errorParsingPredicateString$str(), new Object[] { reason, s }));
/*  356 */     _copyStackTraceMinusOne(result);
/*  357 */     return result;
/*      */   }
/*      */   protected String tooManyCookies$str() {
/*  360 */     return "UT000046: The number of cookies sent exceeded the maximum of %s";
/*      */   }
/*      */   
/*      */   public final IllegalStateException tooManyCookies(int maxCookies) {
/*  364 */     IllegalStateException result = new IllegalStateException(String.format(getLoggingLocale(), tooManyCookies$str(), new Object[] { Integer.valueOf(maxCookies) }));
/*  365 */     _copyStackTraceMinusOne(result);
/*  366 */     return result;
/*      */   }
/*      */   protected String tooManyParameters$str() {
/*  369 */     return "UT000047: The number of parameters exceeded the maximum of %s";
/*      */   }
/*      */   
/*      */   public final ParameterLimitException tooManyParameters(int maxValues) {
/*  373 */     ParameterLimitException result = new ParameterLimitException(String.format(getLoggingLocale(), tooManyParameters$str(), new Object[] { Integer.valueOf(maxValues) }));
/*  374 */     _copyStackTraceMinusOne((Throwable)result);
/*  375 */     return result;
/*      */   }
/*      */   protected String noRequestActive$str() {
/*  378 */     return "UT000048: No request is currently active";
/*      */   }
/*      */   
/*      */   public final IllegalStateException noRequestActive() {
/*  382 */     IllegalStateException result = new IllegalStateException(String.format(getLoggingLocale(), noRequestActive$str(), new Object[0]));
/*  383 */     _copyStackTraceMinusOne(result);
/*  384 */     return result;
/*      */   }
/*      */   protected String authMechanismOutcomeNull$str() {
/*  387 */     return "UT000050: AuthenticationMechanism Outcome is null";
/*      */   }
/*      */   
/*      */   public final IllegalStateException authMechanismOutcomeNull() {
/*  391 */     IllegalStateException result = new IllegalStateException(String.format(getLoggingLocale(), authMechanismOutcomeNull$str(), new Object[0]));
/*  392 */     _copyStackTraceMinusOne(result);
/*  393 */     return result;
/*      */   }
/*      */   protected String notAValidIpPattern$str() {
/*  396 */     return "UT000051: Not a valid IP pattern %s";
/*      */   }
/*      */   
/*      */   public final IllegalArgumentException notAValidIpPattern(String peer) {
/*  400 */     IllegalArgumentException result = new IllegalArgumentException(String.format(getLoggingLocale(), notAValidIpPattern$str(), new Object[] { peer }));
/*  401 */     _copyStackTraceMinusOne(result);
/*  402 */     return result;
/*      */   }
/*      */   protected String noSessionData$str() {
/*  405 */     return "UT000052: Session data requested when non session based authentication in use";
/*      */   }
/*      */   
/*      */   public final IllegalStateException noSessionData() {
/*  409 */     IllegalStateException result = new IllegalStateException(String.format(getLoggingLocale(), noSessionData$str(), new Object[0]));
/*  410 */     _copyStackTraceMinusOne(result);
/*  411 */     return result;
/*      */   }
/*      */   protected String listenerAlreadyRegistered$str() {
/*  414 */     return "UT000053: Listener %s already registered";
/*      */   }
/*      */   
/*      */   public final IllegalArgumentException listenerAlreadyRegistered(String name) {
/*  418 */     IllegalArgumentException result = new IllegalArgumentException(String.format(getLoggingLocale(), listenerAlreadyRegistered$str(), new Object[] { name }));
/*  419 */     _copyStackTraceMinusOne(result);
/*  420 */     return result;
/*      */   }
/*      */   protected String maxFileSizeExceeded$str() {
/*  423 */     return "UT000054: The maximum size %s for an individual file in a multipart request was exceeded";
/*      */   }
/*      */   
/*      */   public final MultiPartParserDefinition.FileTooLargeException maxFileSizeExceeded(long maxIndividualFileSize) {
/*  427 */     MultiPartParserDefinition.FileTooLargeException result = new MultiPartParserDefinition.FileTooLargeException(String.format(getLoggingLocale(), maxFileSizeExceeded$str(), new Object[] { Long.valueOf(maxIndividualFileSize) }));
/*  428 */     _copyStackTraceMinusOne((Throwable)result);
/*  429 */     return result;
/*      */   }
/*      */   protected String couldNotSetAttribute$str() {
/*  432 */     return "UT000055: Could not set attribute %s to %s as it is read only";
/*      */   }
/*      */   
/*      */   public final String couldNotSetAttribute(String attributeName, String newValue) {
/*  436 */     return String.format(getLoggingLocale(), couldNotSetAttribute$str(), new Object[] { attributeName, newValue });
/*      */   }
/*      */   protected String couldNotParseUriTemplate$str() {
/*  439 */     return "UT000056: Could not parse URI template %s, exception at char %s";
/*      */   }
/*      */   
/*      */   public final RuntimeException couldNotParseUriTemplate(String path, int i) {
/*  443 */     RuntimeException result = new RuntimeException(String.format(getLoggingLocale(), couldNotParseUriTemplate$str(), new Object[] { path, Integer.valueOf(i) }));
/*  444 */     _copyStackTraceMinusOne(result);
/*  445 */     return result;
/*      */   }
/*      */   protected String mismatchedBraces$str() {
/*  448 */     return "UT000057: Mismatched braces in attribute string %s";
/*      */   }
/*      */   
/*      */   public final RuntimeException mismatchedBraces(String valueString) {
/*  452 */     RuntimeException result = new RuntimeException(String.format(getLoggingLocale(), mismatchedBraces$str(), new Object[] { valueString }));
/*  453 */     _copyStackTraceMinusOne(result);
/*  454 */     return result;
/*      */   }
/*      */   protected String moreThanOneHandlerWithName$str() {
/*  457 */     return "UT000058: More than one handler with name %s. Builder class %s and %s";
/*      */   }
/*      */   
/*      */   public final IllegalStateException moreThanOneHandlerWithName(String name, Class<? extends HandlerBuilder> aClass, Class<? extends HandlerBuilder> existing) {
/*  461 */     IllegalStateException result = new IllegalStateException(String.format(getLoggingLocale(), moreThanOneHandlerWithName$str(), new Object[] { name, aClass, existing }));
/*  462 */     _copyStackTraceMinusOne(result);
/*  463 */     return result;
/*      */   }
/*      */   protected String outOfBandResponseOnlyAllowedFor100Continue$str() {
/*  466 */     return "UT000061: Out of band responses only allowed for 100-continue requests";
/*      */   }
/*      */   
/*      */   public final IllegalArgumentException outOfBandResponseOnlyAllowedFor100Continue() {
/*  470 */     IllegalArgumentException result = new IllegalArgumentException(String.format(getLoggingLocale(), outOfBandResponseOnlyAllowedFor100Continue$str(), new Object[0]));
/*  471 */     _copyStackTraceMinusOne(result);
/*  472 */     return result;
/*      */   }
/*      */   protected String sslWasNull$str() {
/*  475 */     return "UT000065: SSL must be specified to connect to a https URL";
/*      */   }
/*      */   
/*      */   public final IOException sslWasNull() {
/*  479 */     IOException result = new IOException(String.format(getLoggingLocale(), sslWasNull$str(), new Object[0]));
/*  480 */     _copyStackTraceMinusOne(result);
/*  481 */     return result;
/*      */   }
/*      */   protected String wrongMagicNumber$str() {
/*  484 */     return "UT000066: Incorrect magic number %s for AJP packet header";
/*      */   }
/*      */   
/*      */   public final IOException wrongMagicNumber(int number) {
/*  488 */     IOException result = new IOException(String.format(getLoggingLocale(), wrongMagicNumber$str(), new Object[] { Integer.valueOf(number) }));
/*  489 */     _copyStackTraceMinusOne(result);
/*  490 */     return result;
/*      */   }
/*      */   protected String peerUnverified$str() {
/*  493 */     return "UT000067: No client cert was provided";
/*      */   }
/*      */   
/*      */   public final SSLPeerUnverifiedException peerUnverified() {
/*  497 */     SSLPeerUnverifiedException result = new SSLPeerUnverifiedException(String.format(getLoggingLocale(), peerUnverified$str(), new Object[0]));
/*  498 */     _copyStackTraceMinusOne(result);
/*  499 */     return result;
/*      */   }
/*      */   protected String servletPathMatchFailed$str() {
/*  502 */     return "UT000068: Servlet path match failed";
/*      */   }
/*      */   
/*      */   public final IllegalArgumentException servletPathMatchFailed() {
/*  506 */     IllegalArgumentException result = new IllegalArgumentException(String.format(getLoggingLocale(), servletPathMatchFailed$str(), new Object[0]));
/*  507 */     _copyStackTraceMinusOne(result);
/*  508 */     return result;
/*      */   }
/*      */   protected String couldNotParseCookie$str() {
/*  511 */     return "UT000069: Could not parse set cookie header %s";
/*      */   }
/*      */   
/*      */   public final IllegalArgumentException couldNotParseCookie(String headerValue) {
/*  515 */     IllegalArgumentException result = new IllegalArgumentException(String.format(getLoggingLocale(), couldNotParseCookie$str(), new Object[] { headerValue }));
/*  516 */     _copyStackTraceMinusOne(result);
/*  517 */     return result;
/*      */   }
/*      */   protected String canOnlyBeCalledByIoThread$str() {
/*  520 */     return "UT000070: method can only be called by IO thread";
/*      */   }
/*      */   
/*      */   public final IllegalStateException canOnlyBeCalledByIoThread() {
/*  524 */     IllegalStateException result = new IllegalStateException(String.format(getLoggingLocale(), canOnlyBeCalledByIoThread$str(), new Object[0]));
/*  525 */     _copyStackTraceMinusOne(result);
/*  526 */     return result;
/*      */   }
/*      */   protected String matcherAlreadyContainsTemplate$str() {
/*  529 */     return "UT000071: Cannot add path template %s, matcher already contains an equivalent pattern %s";
/*      */   }
/*      */   
/*      */   public final IllegalStateException matcherAlreadyContainsTemplate(String templateString, String templateString1) {
/*  533 */     IllegalStateException result = new IllegalStateException(String.format(getLoggingLocale(), matcherAlreadyContainsTemplate$str(), new Object[] { templateString, templateString1 }));
/*  534 */     _copyStackTraceMinusOne(result);
/*  535 */     return result;
/*      */   }
/*      */   protected String failedToDecodeURL$str() {
/*  538 */     return "UT000072: Failed to decode url %s to charset %s";
/*      */   }
/*      */   
/*      */   public final UrlDecodeException failedToDecodeURL(String s, String enc, Exception e) {
/*  542 */     UrlDecodeException result = new UrlDecodeException(String.format(getLoggingLocale(), failedToDecodeURL$str(), new Object[] { s, enc }), e);
/*  543 */     _copyStackTraceMinusOne((Throwable)result);
/*  544 */     return result;
/*      */   }
/*      */   protected String resourceChangeListenerNotSupported$str() {
/*  547 */     return "UT000073: Resource change listeners are not supported";
/*      */   }
/*      */   
/*      */   public final IllegalArgumentException resourceChangeListenerNotSupported() {
/*  551 */     IllegalArgumentException result = new IllegalArgumentException(String.format(getLoggingLocale(), resourceChangeListenerNotSupported$str(), new Object[0]));
/*  552 */     _copyStackTraceMinusOne(result);
/*  553 */     return result;
/*      */   }
/*      */   protected String objectWasFreed$str() {
/*  556 */     return "UT000075: Object was freed";
/*      */   }
/*      */   
/*      */   public final IllegalStateException objectWasFreed() {
/*  560 */     IllegalStateException result = new IllegalStateException(String.format(getLoggingLocale(), objectWasFreed$str(), new Object[0]));
/*  561 */     _copyStackTraceMinusOne(result);
/*  562 */     return result;
/*      */   }
/*      */   protected String handlerNotShutdown$str() {
/*  565 */     return "UT000076: Handler not shutdown";
/*      */   }
/*      */   
/*      */   public final IllegalStateException handlerNotShutdown() {
/*  569 */     IllegalStateException result = new IllegalStateException(String.format(getLoggingLocale(), handlerNotShutdown$str(), new Object[0]));
/*  570 */     _copyStackTraceMinusOne(result);
/*  571 */     return result;
/*      */   }
/*      */   protected String upgradeNotSupported$str() {
/*  574 */     return "UT000077: The underlying transport does not support HTTP upgrade";
/*      */   }
/*      */   
/*      */   public final IllegalStateException upgradeNotSupported() {
/*  578 */     IllegalStateException result = new IllegalStateException(String.format(getLoggingLocale(), upgradeNotSupported$str(), new Object[0]));
/*  579 */     _copyStackTraceMinusOne(result);
/*  580 */     return result;
/*      */   }
/*      */   protected String renegotiationNotSupported$str() {
/*  583 */     return "UT000078: Renegotiation not supported";
/*      */   }
/*      */   
/*      */   public final IOException renegotiationNotSupported() {
/*  587 */     IOException result = new IOException(String.format(getLoggingLocale(), renegotiationNotSupported$str(), new Object[0]));
/*  588 */     _copyStackTraceMinusOne(result);
/*  589 */     return result;
/*      */   }
/*      */   protected String notAValidRegularExpressionPattern$str() {
/*  592 */     return "UT000080: Not a valid regular expression pattern %s";
/*      */   }
/*      */   
/*      */   public final IllegalArgumentException notAValidRegularExpressionPattern(String pattern) {
/*  596 */     IllegalArgumentException result = new IllegalArgumentException(String.format(getLoggingLocale(), notAValidRegularExpressionPattern$str(), new Object[] { pattern }));
/*  597 */     _copyStackTraceMinusOne(result);
/*  598 */     return result;
/*      */   }
/*      */   protected String badRequest$str() {
/*  601 */     return "UT000081: Bad request";
/*      */   }
/*      */   
/*      */   public final BadRequestException badRequest() {
/*  605 */     BadRequestException result = new BadRequestException(String.format(getLoggingLocale(), badRequest$str(), new Object[0]));
/*  606 */     _copyStackTraceMinusOne((Throwable)result);
/*  607 */     return result;
/*      */   }
/*      */   protected String hostAlreadyRegistered$str() {
/*  610 */     return "UT000082: Host %s already registered";
/*      */   }
/*      */   
/*      */   public final RuntimeException hostAlreadyRegistered(Object host) {
/*  614 */     RuntimeException result = new RuntimeException(String.format(getLoggingLocale(), hostAlreadyRegistered$str(), new Object[] { host }));
/*  615 */     _copyStackTraceMinusOne(result);
/*  616 */     return result;
/*      */   }
/*      */   protected String hostHasNotBeenRegistered$str() {
/*  619 */     return "UT000083: Host %s has not been registered";
/*      */   }
/*      */   
/*      */   public final RuntimeException hostHasNotBeenRegistered(Object host) {
/*  623 */     RuntimeException result = new RuntimeException(String.format(getLoggingLocale(), hostHasNotBeenRegistered$str(), new Object[] { host }));
/*  624 */     _copyStackTraceMinusOne(result);
/*  625 */     return result;
/*      */   }
/*      */   protected String extraDataWrittenAfterChunkEnd$str() {
/*  628 */     return "UT000084: Attempted to write additional data after the last chunk";
/*      */   }
/*      */   
/*      */   public final IOException extraDataWrittenAfterChunkEnd() {
/*  632 */     IOException result = new IOException(String.format(getLoggingLocale(), extraDataWrittenAfterChunkEnd$str(), new Object[0]));
/*  633 */     _copyStackTraceMinusOne(result);
/*  634 */     return result;
/*      */   }
/*      */   protected String couldNotGenerateUniqueSessionId$str() {
/*  637 */     return "UT000085: Could not generate unique session id";
/*      */   }
/*      */   
/*      */   public final RuntimeException couldNotGenerateUniqueSessionId() {
/*  641 */     RuntimeException result = new RuntimeException(String.format(getLoggingLocale(), couldNotGenerateUniqueSessionId$str(), new Object[0]));
/*  642 */     _copyStackTraceMinusOne(result);
/*  643 */     return result;
/*      */   }
/*      */   protected String controlFrameCannotHaveBodyContent$str() {
/*  646 */     return "UT000088: SPDY control frames cannot have body content";
/*      */   }
/*      */   
/*      */   public final IOException controlFrameCannotHaveBodyContent() {
/*  650 */     IOException result = new IOException(String.format(getLoggingLocale(), controlFrameCannotHaveBodyContent$str(), new Object[0]));
/*  651 */     _copyStackTraceMinusOne(result);
/*  652 */     return result;
/*      */   }
/*      */   protected String bufferAlreadyFreed$str() {
/*  655 */     return "UT000091: Buffer has already been freed";
/*      */   }
/*      */   
/*      */   public final IllegalStateException bufferAlreadyFreed() {
/*  659 */     IllegalStateException result = new IllegalStateException(String.format(getLoggingLocale(), bufferAlreadyFreed$str(), new Object[0]));
/*  660 */     _copyStackTraceMinusOne(result);
/*  661 */     return result;
/*      */   }
/*      */   protected String awaitCalledFromIoThread$str() {
/*  664 */     return "UT000094: Blocking await method called from IO thread. Blocking IO must be dispatched to a worker thread or deadlocks will result.";
/*      */   }
/*      */   
/*      */   public final IOException awaitCalledFromIoThread() {
/*  668 */     IOException result = new IOException(String.format(getLoggingLocale(), awaitCalledFromIoThread$str(), new Object[0]));
/*  669 */     _copyStackTraceMinusOne(result);
/*  670 */     return result;
/*      */   }
/*      */   protected String recursiveCallToFlushingSenders$str() {
/*  673 */     return "UT000095: Recursive call to flushSenders()";
/*      */   }
/*      */   
/*      */   public final RuntimeException recursiveCallToFlushingSenders() {
/*  677 */     RuntimeException result = new RuntimeException(String.format(getLoggingLocale(), recursiveCallToFlushingSenders$str(), new Object[0]));
/*  678 */     _copyStackTraceMinusOne(result);
/*  679 */     return result;
/*      */   }
/*      */   protected String fixedLengthOverflow$str() {
/*  682 */     return "UT000096: More data was written to the channel than specified in the content-length";
/*      */   }
/*      */   
/*      */   public final IllegalStateException fixedLengthOverflow() {
/*  686 */     IllegalStateException result = new IllegalStateException(String.format(getLoggingLocale(), fixedLengthOverflow$str(), new Object[0]));
/*  687 */     _copyStackTraceMinusOne(result);
/*  688 */     return result;
/*      */   }
/*      */   protected String ajpRequestAlreadyInProgress$str() {
/*  691 */     return "UT000097: AJP request already in progress";
/*      */   }
/*      */   
/*      */   public final IllegalStateException ajpRequestAlreadyInProgress() {
/*  695 */     IllegalStateException result = new IllegalStateException(String.format(getLoggingLocale(), ajpRequestAlreadyInProgress$str(), new Object[0]));
/*  696 */     _copyStackTraceMinusOne(result);
/*  697 */     return result;
/*      */   }
/*      */   protected String httpPingDataMustBeLength8$str() {
/*  700 */     return "UT000098: HTTP ping data must be 8 bytes in length";
/*      */   }
/*      */   
/*      */   public final String httpPingDataMustBeLength8() {
/*  704 */     return String.format(getLoggingLocale(), httpPingDataMustBeLength8$str(), new Object[0]);
/*      */   }
/*      */   protected String invalidPingSize$str() {
/*  707 */     return "UT000099: Received a ping of size other than 8";
/*      */   }
/*      */   
/*      */   public final String invalidPingSize() {
/*  711 */     return String.format(getLoggingLocale(), invalidPingSize$str(), new Object[0]);
/*      */   }
/*      */   protected String streamIdMustBeZeroForFrameType$str() {
/*  714 */     return "UT000100: stream id must be zero for frame type %s";
/*      */   }
/*      */   
/*      */   public final String streamIdMustBeZeroForFrameType(int frameType) {
/*  718 */     return String.format(getLoggingLocale(), streamIdMustBeZeroForFrameType$str(), new Object[] { Integer.valueOf(frameType) });
/*      */   }
/*      */   protected String streamIdMustNotBeZeroForFrameType$str() {
/*  721 */     return "UT000101: stream id must not be zero for frame type %s";
/*      */   }
/*      */   
/*      */   public final String streamIdMustNotBeZeroForFrameType(int frameType) {
/*  725 */     return String.format(getLoggingLocale(), streamIdMustNotBeZeroForFrameType$str(), new Object[] { Integer.valueOf(frameType) });
/*      */   }
/*      */   protected String http2StreamWasReset$str() {
/*  728 */     return "UT000103: Http2 stream was reset";
/*      */   }
/*      */   
/*      */   public final IOException http2StreamWasReset() {
/*  732 */     IOException result = new IOException(String.format(getLoggingLocale(), http2StreamWasReset$str(), new Object[0]));
/*  733 */     _copyStackTraceMinusOne(result);
/*  734 */     return result;
/*      */   }
/*      */   protected String incorrectHttp2Preface$str() {
/*  737 */     return "UT000104: Incorrect HTTP2 preface";
/*      */   }
/*      */   
/*      */   public final IOException incorrectHttp2Preface() {
/*  741 */     IOException result = new IOException(String.format(getLoggingLocale(), incorrectHttp2Preface$str(), new Object[0]));
/*  742 */     _copyStackTraceMinusOne(result);
/*  743 */     return result;
/*      */   }
/*      */   protected String http2FrameTooLarge$str() {
/*  746 */     return "UT000105: HTTP2 frame to large";
/*      */   }
/*      */   
/*      */   public final IOException http2FrameTooLarge() {
/*  750 */     IOException result = new IOException(String.format(getLoggingLocale(), http2FrameTooLarge$str(), new Object[0]));
/*  751 */     _copyStackTraceMinusOne(result);
/*  752 */     return result;
/*      */   }
/*      */   protected String http2ContinuationFrameNotExpected$str() {
/*  755 */     return "UT000106: HTTP2 continuation frame received without a corresponding headers or push promise frame";
/*      */   }
/*      */   
/*      */   public final IOException http2ContinuationFrameNotExpected() {
/*  759 */     IOException result = new IOException(String.format(getLoggingLocale(), http2ContinuationFrameNotExpected$str(), new Object[0]));
/*  760 */     _copyStackTraceMinusOne(result);
/*  761 */     return result;
/*      */   }
/*      */   protected String huffmanEncodedHpackValueDidNotEndWithEOS$str() {
/*  764 */     return "UT000107: Huffman encoded value in HPACK headers did not end with EOS padding";
/*      */   }
/*      */   
/*      */   public final HpackException huffmanEncodedHpackValueDidNotEndWithEOS() {
/*  768 */     HpackException result = new HpackException();
/*  769 */     _copyStackTraceMinusOne((Throwable)result);
/*  770 */     return result;
/*      */   }
/*      */   protected String integerEncodedOverTooManyOctets$str() {
/*  773 */     return "UT000108: HPACK variable length integer encoded over too many octects, max is %s";
/*      */   }
/*      */   
/*      */   public final HpackException integerEncodedOverTooManyOctets(int maxIntegerOctets) {
/*  777 */     HpackException result = new HpackException();
/*  778 */     _copyStackTraceMinusOne((Throwable)result);
/*  779 */     return result;
/*      */   }
/*      */   protected String zeroNotValidHeaderTableIndex$str() {
/*  782 */     return "UT000109: Zero is not a valid header table index";
/*      */   }
/*      */   
/*      */   public final HpackException zeroNotValidHeaderTableIndex() {
/*  786 */     HpackException result = new HpackException();
/*  787 */     _copyStackTraceMinusOne((Throwable)result);
/*  788 */     return result;
/*      */   }
/*      */   protected String cannotSendContinueResponse$str() {
/*  791 */     return "UT000110: Cannot send 100-Continue, getResponseChannel() has already been called";
/*      */   }
/*      */   
/*      */   public final IOException cannotSendContinueResponse() {
/*  795 */     IOException result = new IOException(String.format(getLoggingLocale(), cannotSendContinueResponse$str(), new Object[0]));
/*  796 */     _copyStackTraceMinusOne(result);
/*  797 */     return result;
/*      */   }
/*      */   protected String parserDidNotMakeProgress$str() {
/*  800 */     return "UT000111: Parser did not make progress";
/*      */   }
/*      */   
/*      */   public final IOException parserDidNotMakeProgress() {
/*  804 */     IOException result = new IOException(String.format(getLoggingLocale(), parserDidNotMakeProgress$str(), new Object[0]));
/*  805 */     _copyStackTraceMinusOne(result);
/*  806 */     return result;
/*      */   }
/*      */   protected String headersStreamCanOnlyBeCreatedByClient$str() {
/*  809 */     return "UT000112: Only client side can call createStream, if you wish to send a PUSH_PROMISE frame use createPushPromiseStream instead";
/*      */   }
/*      */   
/*      */   public final IOException headersStreamCanOnlyBeCreatedByClient() {
/*  813 */     IOException result = new IOException(String.format(getLoggingLocale(), headersStreamCanOnlyBeCreatedByClient$str(), new Object[0]));
/*  814 */     _copyStackTraceMinusOne(result);
/*  815 */     return result;
/*      */   }
/*      */   protected String pushPromiseCanOnlyBeCreatedByServer$str() {
/*  818 */     return "UT000113: Only the server side can send a push promise stream";
/*      */   }
/*      */   
/*      */   public final IOException pushPromiseCanOnlyBeCreatedByServer() {
/*  822 */     IOException result = new IOException(String.format(getLoggingLocale(), pushPromiseCanOnlyBeCreatedByServer$str(), new Object[0]));
/*  823 */     _copyStackTraceMinusOne(result);
/*  824 */     return result;
/*      */   }
/*      */   protected String invalidAclRule$str() {
/*  827 */     return "UT000114: Invalid IP access control rule %s. Format is: [ip-match] allow|deny";
/*      */   }
/*      */   
/*      */   public final IllegalArgumentException invalidAclRule(String rule) {
/*  831 */     IllegalArgumentException result = new IllegalArgumentException(String.format(getLoggingLocale(), invalidAclRule$str(), new Object[] { rule }));
/*  832 */     _copyStackTraceMinusOne(result);
/*  833 */     return result;
/*      */   }
/*      */   protected String serverReceivedPushPromise$str() {
/*  836 */     return "UT000115: Server received PUSH_PROMISE frame from client";
/*      */   }
/*      */   
/*      */   public final IOException serverReceivedPushPromise() {
/*  840 */     IOException result = new IOException(String.format(getLoggingLocale(), serverReceivedPushPromise$str(), new Object[0]));
/*  841 */     _copyStackTraceMinusOne(result);
/*  842 */     return result;
/*      */   }
/*      */   protected String connectNotSupported$str() {
/*  845 */     return "UT000116: CONNECT not supported by this connector";
/*      */   }
/*      */   
/*      */   public final IllegalStateException connectNotSupported() {
/*  849 */     IllegalStateException result = new IllegalStateException(String.format(getLoggingLocale(), connectNotSupported$str(), new Object[0]));
/*  850 */     _copyStackTraceMinusOne(result);
/*  851 */     return result;
/*      */   }
/*      */   protected String notAConnectRequest$str() {
/*  854 */     return "UT000117: Request was not a CONNECT request";
/*      */   }
/*      */   
/*      */   public final IllegalStateException notAConnectRequest() {
/*  858 */     IllegalStateException result = new IllegalStateException(String.format(getLoggingLocale(), notAConnectRequest$str(), new Object[0]));
/*  859 */     _copyStackTraceMinusOne(result);
/*  860 */     return result;
/*      */   }
/*      */   protected String cannotResetBuffer$str() {
/*  863 */     return "UT000118: Cannot reset buffer, response has already been commited";
/*      */   }
/*      */   
/*      */   public final IllegalStateException cannotResetBuffer() {
/*  867 */     IllegalStateException result = new IllegalStateException(String.format(getLoggingLocale(), cannotResetBuffer$str(), new Object[0]));
/*  868 */     _copyStackTraceMinusOne(result);
/*  869 */     return result;
/*      */   }
/*      */   protected String http2PriRequestFailed$str() {
/*  872 */     return "UT000119: HTTP2 via prior knowledge failed";
/*      */   }
/*      */   
/*      */   public final IOException http2PriRequestFailed() {
/*  876 */     IOException result = new IOException(String.format(getLoggingLocale(), http2PriRequestFailed$str(), new Object[0]));
/*  877 */     _copyStackTraceMinusOne(result);
/*  878 */     return result;
/*      */   }
/*      */   protected String outOfBandResponseNotSupported$str() {
/*  881 */     return "UT000120: Out of band responses are not allowed for this connector";
/*      */   }
/*      */   
/*      */   public final IllegalStateException outOfBandResponseNotSupported() {
/*  885 */     IllegalStateException result = new IllegalStateException(String.format(getLoggingLocale(), outOfBandResponseNotSupported$str(), new Object[0]));
/*  886 */     _copyStackTraceMinusOne(result);
/*  887 */     return result;
/*      */   }
/*      */   protected String tooManySessions$str() {
/*  890 */     return "UT000121: Session was rejected as the maximum number of sessions (%s) has been hit";
/*      */   }
/*      */   
/*      */   public final IllegalStateException tooManySessions(int maxSessions) {
/*  894 */     IllegalStateException result = new IllegalStateException(String.format(getLoggingLocale(), tooManySessions$str(), new Object[] { Integer.valueOf(maxSessions) }));
/*  895 */     _copyStackTraceMinusOne(result);
/*  896 */     return result;
/*      */   }
/*      */   protected String proxyConnectionFailed$str() {
/*  899 */     return "UT000122: CONNECT attempt failed as target proxy returned %s";
/*      */   }
/*      */   
/*      */   public final IOException proxyConnectionFailed(int responseCode) {
/*  903 */     IOException result = new IOException(String.format(getLoggingLocale(), proxyConnectionFailed$str(), new Object[] { Integer.valueOf(responseCode) }));
/*  904 */     _copyStackTraceMinusOne(result);
/*  905 */     return result;
/*      */   }
/*      */   protected String mcmpMessageRejectedDueToSuspiciousCharacters$str() {
/*  908 */     return "UT000123: MCMP message %s rejected due to suspicious characters";
/*      */   }
/*      */   
/*      */   public final RuntimeException mcmpMessageRejectedDueToSuspiciousCharacters(String data) {
/*  912 */     RuntimeException result = new RuntimeException(String.format(getLoggingLocale(), mcmpMessageRejectedDueToSuspiciousCharacters$str(), new Object[] { data }));
/*  913 */     _copyStackTraceMinusOne(result);
/*  914 */     return result;
/*      */   }
/*      */   protected String rengotiationTimedOut$str() {
/*  917 */     return "UT000124: renegotiation timed out";
/*      */   }
/*      */   
/*      */   public final IllegalStateException rengotiationTimedOut() {
/*  921 */     IllegalStateException result = new IllegalStateException(String.format(getLoggingLocale(), rengotiationTimedOut$str(), new Object[0]));
/*  922 */     _copyStackTraceMinusOne(result);
/*  923 */     return result;
/*      */   }
/*      */   protected String requestBodyAlreadyRead$str() {
/*  926 */     return "UT000125: Request body already read";
/*      */   }
/*      */   
/*      */   public final IllegalStateException requestBodyAlreadyRead() {
/*  930 */     IllegalStateException result = new IllegalStateException(String.format(getLoggingLocale(), requestBodyAlreadyRead$str(), new Object[0]));
/*  931 */     _copyStackTraceMinusOne(result);
/*  932 */     return result;
/*      */   }
/*      */   protected String blockingIoFromIOThread$str() {
/*  935 */     return "UT000126: Attempted to do blocking IO from the IO thread. This is prohibited as it may result in deadlocks";
/*      */   }
/*      */   
/*      */   public final IllegalStateException blockingIoFromIOThread() {
/*  939 */     IllegalStateException result = new IllegalStateException(String.format(getLoggingLocale(), blockingIoFromIOThread$str(), new Object[0]));
/*  940 */     _copyStackTraceMinusOne(result);
/*  941 */     return result;
/*      */   }
/*      */   protected String responseComplete$str() {
/*  944 */     return "UT000127: Response has already been sent";
/*      */   }
/*      */   
/*      */   public final IllegalStateException responseComplete() {
/*  948 */     IllegalStateException result = new IllegalStateException(String.format(getLoggingLocale(), responseComplete$str(), new Object[0]));
/*  949 */     _copyStackTraceMinusOne(result);
/*  950 */     return result;
/*      */   }
/*      */   protected String couldNotReadContentLengthData$str() {
/*  953 */     return "UT000128: Remote peer closed connection before all data could be read";
/*      */   }
/*      */   
/*      */   public final IOException couldNotReadContentLengthData() {
/*  957 */     IOException result = new IOException(String.format(getLoggingLocale(), couldNotReadContentLengthData$str(), new Object[0]));
/*  958 */     _copyStackTraceMinusOne(result);
/*  959 */     return result;
/*      */   }
/*      */   protected String failedToSendAfterBeingSafe$str() {
/*  962 */     return "UT000129: Failed to send after being safe to send";
/*      */   }
/*      */   
/*      */   public final IllegalStateException failedToSendAfterBeingSafe() {
/*  966 */     IllegalStateException result = new IllegalStateException(String.format(getLoggingLocale(), failedToSendAfterBeingSafe$str(), new Object[0]));
/*  967 */     _copyStackTraceMinusOne(result);
/*  968 */     return result;
/*      */   }
/*      */   protected String reasonPhraseToLargeForBuffer$str() {
/*  971 */     return "UT000130: HTTP reason phrase was too large for the buffer. Either provide a smaller message or a bigger buffer. Phrase: %s";
/*      */   }
/*      */   
/*      */   public final IllegalStateException reasonPhraseToLargeForBuffer(String phrase) {
/*  975 */     IllegalStateException result = new IllegalStateException(String.format(getLoggingLocale(), reasonPhraseToLargeForBuffer$str(), new Object[] { phrase }));
/*  976 */     _copyStackTraceMinusOne(result);
/*  977 */     return result;
/*      */   }
/*      */   protected String poolIsClosed$str() {
/*  980 */     return "UT000131: Buffer pool is closed";
/*      */   }
/*      */   
/*      */   public final IllegalStateException poolIsClosed() {
/*  984 */     IllegalStateException result = new IllegalStateException(String.format(getLoggingLocale(), poolIsClosed$str(), new Object[0]));
/*  985 */     _copyStackTraceMinusOne(result);
/*  986 */     return result;
/*      */   }
/*      */   protected String hpackFailed$str() {
/*  989 */     return "UT000132: HPACK decode failed";
/*      */   }
/*      */   
/*      */   public final HpackException hpackFailed() {
/*  993 */     HpackException result = new HpackException();
/*  994 */     _copyStackTraceMinusOne((Throwable)result);
/*  995 */     return result;
/*      */   }
/*      */   protected String notAnUpgradeRequest$str() {
/*  998 */     return "UT000133: Request did not contain an Upgrade header, upgrade is not permitted";
/*      */   }
/*      */   
/*      */   public final IllegalStateException notAnUpgradeRequest() {
/* 1002 */     IllegalStateException result = new IllegalStateException(String.format(getLoggingLocale(), notAnUpgradeRequest$str(), new Object[0]));
/* 1003 */     _copyStackTraceMinusOne(result);
/* 1004 */     return result;
/*      */   }
/*      */   protected String authenticationPropertyNotSet$str() {
/* 1007 */     return "UT000134: Authentication mechanism %s requires property %s to be set";
/*      */   }
/*      */   
/*      */   public final IllegalStateException authenticationPropertyNotSet(String name, String header) {
/* 1011 */     IllegalStateException result = new IllegalStateException(String.format(getLoggingLocale(), authenticationPropertyNotSet$str(), new Object[] { name, header }));
/* 1012 */     _copyStackTraceMinusOne(result);
/* 1013 */     return result;
/*      */   }
/*      */   protected String rengotiationFailed$str() {
/* 1016 */     return "UT000135: renegotiation failed";
/*      */   }
/*      */   
/*      */   public final IllegalStateException rengotiationFailed() {
/* 1020 */     IllegalStateException result = new IllegalStateException(String.format(getLoggingLocale(), rengotiationFailed$str(), new Object[0]));
/* 1021 */     _copyStackTraceMinusOne(result);
/* 1022 */     return result;
/*      */   }
/*      */   protected String userAgentCharsetMustHaveEvenNumberOfItems$str() {
/* 1025 */     return "UT000136: User agent charset string must have an even number of items, in the form pattern,charset,pattern,charset,... Instead got: %s";
/*      */   }
/*      */   
/*      */   public final IllegalArgumentException userAgentCharsetMustHaveEvenNumberOfItems(String supplied) {
/* 1029 */     IllegalArgumentException result = new IllegalArgumentException(String.format(getLoggingLocale(), userAgentCharsetMustHaveEvenNumberOfItems$str(), new Object[] { supplied }));
/* 1030 */     _copyStackTraceMinusOne(result);
/* 1031 */     return result;
/*      */   }
/*      */   protected String datasourceNotFound$str() {
/* 1034 */     return "UT000137: Could not find the datasource called %s";
/*      */   }
/*      */   
/*      */   public final IllegalArgumentException datasourceNotFound(String ds) {
/* 1038 */     IllegalArgumentException result = new IllegalArgumentException(String.format(getLoggingLocale(), datasourceNotFound$str(), new Object[] { ds }));
/* 1039 */     _copyStackTraceMinusOne(result);
/* 1040 */     return result;
/*      */   }
/*      */   protected String serverNotStarted$str() {
/* 1043 */     return "UT000138: Server not started";
/*      */   }
/*      */   
/*      */   public final IllegalStateException serverNotStarted() {
/* 1047 */     IllegalStateException result = new IllegalStateException(String.format(getLoggingLocale(), serverNotStarted$str(), new Object[0]));
/* 1048 */     _copyStackTraceMinusOne(result);
/* 1049 */     return result;
/*      */   }
/*      */   protected String exchangeAlreadyComplete$str() {
/* 1052 */     return "UT000139: Exchange already complete";
/*      */   }
/*      */   
/*      */   public final IllegalStateException exchangeAlreadyComplete() {
/* 1056 */     IllegalStateException result = new IllegalStateException(String.format(getLoggingLocale(), exchangeAlreadyComplete$str(), new Object[0]));
/* 1057 */     _copyStackTraceMinusOne(result);
/* 1058 */     return result;
/*      */   }
/*      */   protected String notHandshakeRecord$str() {
/* 1061 */     return "UT000140: Initial SSL/TLS data is not a handshake record";
/*      */   }
/*      */   
/*      */   public final SSLHandshakeException notHandshakeRecord() {
/* 1065 */     SSLHandshakeException result = new SSLHandshakeException(String.format(getLoggingLocale(), notHandshakeRecord$str(), new Object[0]));
/* 1066 */     _copyStackTraceMinusOne(result);
/* 1067 */     return result;
/*      */   }
/*      */   protected String invalidHandshakeRecord$str() {
/* 1070 */     return "UT000141: Initial SSL/TLS handshake record is invalid";
/*      */   }
/*      */   
/*      */   public final SSLHandshakeException invalidHandshakeRecord() {
/* 1074 */     SSLHandshakeException result = new SSLHandshakeException(String.format(getLoggingLocale(), invalidHandshakeRecord$str(), new Object[0]));
/* 1075 */     _copyStackTraceMinusOne(result);
/* 1076 */     return result;
/*      */   }
/*      */   protected String multiRecordSSLHandshake$str() {
/* 1079 */     return "UT000142: Initial SSL/TLS handshake spans multiple records";
/*      */   }
/*      */   
/*      */   public final SSLHandshakeException multiRecordSSLHandshake() {
/* 1083 */     SSLHandshakeException result = new SSLHandshakeException(String.format(getLoggingLocale(), multiRecordSSLHandshake$str(), new Object[0]));
/* 1084 */     _copyStackTraceMinusOne(result);
/* 1085 */     return result;
/*      */   }
/*      */   protected String expectedClientHello$str() {
/* 1088 */     return "UT000143: Expected \"client hello\" record";
/*      */   }
/*      */   
/*      */   public final SSLHandshakeException expectedClientHello() {
/* 1092 */     SSLHandshakeException result = new SSLHandshakeException(String.format(getLoggingLocale(), expectedClientHello$str(), new Object[0]));
/* 1093 */     _copyStackTraceMinusOne(result);
/* 1094 */     return result;
/*      */   }
/*      */   protected String expectedServerHello$str() {
/* 1097 */     return "UT000144: Expected server hello";
/*      */   }
/*      */   
/*      */   public final SSLHandshakeException expectedServerHello() {
/* 1101 */     SSLHandshakeException result = new SSLHandshakeException(String.format(getLoggingLocale(), expectedServerHello$str(), new Object[0]));
/* 1102 */     _copyStackTraceMinusOne(result);
/* 1103 */     return result;
/*      */   }
/*      */   protected String tooManyRedirects$str() {
/* 1106 */     return "UT000145: Too many redirects";
/*      */   }
/*      */   
/*      */   public final IOException tooManyRedirects(IOException exception) {
/* 1110 */     IOException result = new IOException(String.format(getLoggingLocale(), tooManyRedirects$str(), new Object[0]), exception);
/* 1111 */     _copyStackTraceMinusOne(result);
/* 1112 */     return result;
/*      */   }
/*      */   protected String resumedAndDispatched$str() {
/* 1115 */     return "UT000146: HttpServerExchange cannot have both async IO resumed and dispatch() called in the same cycle";
/*      */   }
/*      */   
/*      */   public final IllegalStateException resumedAndDispatched() {
/* 1119 */     IllegalStateException result = new IllegalStateException(String.format(getLoggingLocale(), resumedAndDispatched$str(), new Object[0]));
/* 1120 */     _copyStackTraceMinusOne(result);
/* 1121 */     return result;
/*      */   }
/*      */   protected String noHostInHttp11Request$str() {
/* 1124 */     return "UT000147: No host header in a HTTP/1.1 request";
/*      */   }
/*      */   
/*      */   public final IOException noHostInHttp11Request() {
/* 1128 */     IOException result = new IOException(String.format(getLoggingLocale(), noHostInHttp11Request$str(), new Object[0]));
/* 1129 */     _copyStackTraceMinusOne(result);
/* 1130 */     return result;
/*      */   }
/*      */   protected String invalidHpackEncoding$str() {
/* 1133 */     return "UT000148: Invalid HPack encoding. First byte: %s";
/*      */   }
/*      */   
/*      */   public final HpackException invalidHpackEncoding(byte b) {
/* 1137 */     HpackException result = new HpackException();
/* 1138 */     _copyStackTraceMinusOne((Throwable)result);
/* 1139 */     return result;
/*      */   }
/*      */   protected String newlineNotSupportedInHttpString$str() {
/* 1142 */     return "UT000149: HttpString is not allowed to contain newlines. value: %s";
/*      */   }
/*      */   
/*      */   public final IllegalArgumentException newlineNotSupportedInHttpString(String value) {
/* 1146 */     IllegalArgumentException result = new IllegalArgumentException(String.format(getLoggingLocale(), newlineNotSupportedInHttpString$str(), new Object[] { value }));
/* 1147 */     _copyStackTraceMinusOne(result);
/* 1148 */     return result;
/*      */   }
/*      */   protected String pseudoHeaderInWrongOrder$str() {
/* 1151 */     return "UT000150: Pseudo header %s received after receiving normal headers. Pseudo headers must be the first headers in a HTTP/2 header block.";
/*      */   }
/*      */   
/*      */   public final String pseudoHeaderInWrongOrder(HttpString header) {
/* 1155 */     return String.format(getLoggingLocale(), pseudoHeaderInWrongOrder$str(), new Object[] { header });
/*      */   }
/*      */   protected String expectedContinuationFrame$str() {
/* 1158 */     return "UT000151: Expected to receive a continuation frame";
/*      */   }
/*      */   
/*      */   public final String expectedContinuationFrame() {
/* 1162 */     return String.format(getLoggingLocale(), expectedContinuationFrame$str(), new Object[0]);
/*      */   }
/*      */   protected String incorrectFrameSize$str() {
/* 1165 */     return "UT000152: Incorrect frame size";
/*      */   }
/*      */   
/*      */   public final String incorrectFrameSize() {
/* 1169 */     return String.format(getLoggingLocale(), incorrectFrameSize$str(), new Object[0]);
/*      */   }
/*      */   protected String streamNotRegistered$str() {
/* 1172 */     return "UT000153: Stream id not registered";
/*      */   }
/*      */   
/*      */   public final IllegalStateException streamNotRegistered() {
/* 1176 */     IllegalStateException result = new IllegalStateException(String.format(getLoggingLocale(), streamNotRegistered$str(), new Object[0]));
/* 1177 */     _copyStackTraceMinusOne(result);
/* 1178 */     return result;
/*      */   }
/*      */   protected String sendChallengeReturnedNull$str() {
/* 1181 */     return "UT000154: Mechanism %s returned a null result from sendChallenge()";
/*      */   }
/*      */   
/*      */   public final NullPointerException sendChallengeReturnedNull(AuthenticationMechanism mechanism) {
/* 1185 */     NullPointerException result = new NullPointerException(String.format(getLoggingLocale(), sendChallengeReturnedNull$str(), new Object[] { mechanism }));
/* 1186 */     _copyStackTraceMinusOne(result);
/* 1187 */     return result;
/*      */   }
/*      */   protected String bodyIsSetAndNotReadyForFlush$str() {
/* 1190 */     return "UT000155: Framed channel body was set when it was not ready for flush";
/*      */   }
/*      */   
/*      */   public final IllegalStateException bodyIsSetAndNotReadyForFlush() {
/* 1194 */     IllegalStateException result = new IllegalStateException(String.format(getLoggingLocale(), bodyIsSetAndNotReadyForFlush$str(), new Object[0]));
/* 1195 */     _copyStackTraceMinusOne(result);
/* 1196 */     return result;
/*      */   }
/*      */   protected String invalidGzipHeader$str() {
/* 1199 */     return "UT000156: Invalid GZIP header";
/*      */   }
/*      */   
/*      */   public final IOException invalidGzipHeader() {
/* 1203 */     IOException result = new IOException(String.format(getLoggingLocale(), invalidGzipHeader$str(), new Object[0]));
/* 1204 */     _copyStackTraceMinusOne(result);
/* 1205 */     return result;
/*      */   }
/*      */   protected String invalidGZIPFooter$str() {
/* 1208 */     return "UT000157: Invalid GZIP footer";
/*      */   }
/*      */   
/*      */   public final IOException invalidGZIPFooter() {
/* 1212 */     IOException result = new IOException(String.format(getLoggingLocale(), invalidGZIPFooter$str(), new Object[0]));
/* 1213 */     _copyStackTraceMinusOne(result);
/* 1214 */     return result;
/*      */   }
/*      */   protected String responseTooLargeToBuffer$str() {
/* 1217 */     return "UT000158: Response of length %s is too large to buffer";
/*      */   }
/*      */   
/*      */   public final IllegalStateException responseTooLargeToBuffer(Long length) {
/* 1221 */     IllegalStateException result = new IllegalStateException(String.format(getLoggingLocale(), responseTooLargeToBuffer$str(), new Object[] { length }));
/* 1222 */     _copyStackTraceMinusOne(result);
/* 1223 */     return result;
/*      */   }
/*      */   protected String headerBlockTooLarge$str() {
/* 1226 */     return "UT000161: HTTP/2 header block is too large";
/*      */   }
/*      */   
/*      */   public final String headerBlockTooLarge() {
/* 1230 */     return String.format(getLoggingLocale(), headerBlockTooLarge$str(), new Object[0]);
/*      */   }
/*      */   protected String invalidSameSiteMode$str() {
/* 1233 */     return "UT000162: An invalid SameSite attribute [%s] is specified. It must be one of %s";
/*      */   }
/*      */   
/*      */   public final IllegalArgumentException invalidSameSiteMode(String mode, String validAttributes) {
/* 1237 */     IllegalArgumentException result = new IllegalArgumentException(String.format(getLoggingLocale(), invalidSameSiteMode$str(), new Object[] { mode, validAttributes }));
/* 1238 */     _copyStackTraceMinusOne(result);
/* 1239 */     return result;
/*      */   }
/*      */   protected String invalidToken$str() {
/* 1242 */     return "UT000163: Invalid token %s";
/*      */   }
/*      */   
/*      */   public final IllegalArgumentException invalidToken(byte c) {
/* 1246 */     IllegalArgumentException result = new IllegalArgumentException(String.format(getLoggingLocale(), invalidToken$str(), new Object[] { Byte.valueOf(c) }));
/* 1247 */     _copyStackTraceMinusOne(result);
/* 1248 */     return result;
/*      */   }
/*      */   protected String invalidHeaders$str() {
/* 1251 */     return "UT000164: Request contained invalid headers";
/*      */   }
/*      */   
/*      */   public final IllegalArgumentException invalidHeaders() {
/* 1255 */     IllegalArgumentException result = new IllegalArgumentException(String.format(getLoggingLocale(), invalidHeaders$str(), new Object[0]));
/* 1256 */     _copyStackTraceMinusOne(result);
/* 1257 */     return result;
/*      */   }
/*      */   protected String invalidCharacterInRequestTarget$str() {
/* 1260 */     return "UT000165: Invalid character %s in request-target";
/*      */   }
/*      */   
/*      */   public final String invalidCharacterInRequestTarget(char next) {
/* 1264 */     return String.format(getLoggingLocale(), invalidCharacterInRequestTarget$str(), new Object[] { Character.valueOf(next) });
/*      */   }
/*      */   protected String objectIsClosed$str() {
/* 1267 */     return "UT000166: Pooled object is closed";
/*      */   }
/*      */   
/*      */   public final IllegalStateException objectIsClosed() {
/* 1271 */     IllegalStateException result = new IllegalStateException(String.format(getLoggingLocale(), objectIsClosed$str(), new Object[0]));
/* 1272 */     _copyStackTraceMinusOne(result);
/* 1273 */     return result;
/*      */   }
/*      */   protected String moreThanOneHostHeader$str() {
/* 1276 */     return "UT000167: More than one host header in request";
/*      */   }
/*      */   
/*      */   public final IOException moreThanOneHostHeader() {
/* 1280 */     IOException result = new IOException(String.format(getLoggingLocale(), moreThanOneHostHeader$str(), new Object[0]));
/* 1281 */     _copyStackTraceMinusOne(result);
/* 1282 */     return result;
/*      */   }
/*      */   protected String invalidCookieValue$str() {
/* 1285 */     return "UT000168: An invalid character [ASCII code: %s] was present in the cookie value";
/*      */   }
/*      */   
/*      */   public final IllegalArgumentException invalidCookieValue(String value) {
/* 1289 */     IllegalArgumentException result = new IllegalArgumentException(String.format(getLoggingLocale(), invalidCookieValue$str(), new Object[] { value }));
/* 1290 */     _copyStackTraceMinusOne(result);
/* 1291 */     return result;
/*      */   }
/*      */   protected String invalidCookieDomain$str() {
/* 1294 */     return "UT000169: An invalid domain [%s] was specified for this cookie";
/*      */   }
/*      */   
/*      */   public final IllegalArgumentException invalidCookieDomain(String value) {
/* 1298 */     IllegalArgumentException result = new IllegalArgumentException(String.format(getLoggingLocale(), invalidCookieDomain$str(), new Object[] { value }));
/* 1299 */     _copyStackTraceMinusOne(result);
/* 1300 */     return result;
/*      */   }
/*      */   protected String invalidCookiePath$str() {
/* 1303 */     return "UT000170: An invalid path [%s] was specified for this cookie";
/*      */   }
/*      */   
/*      */   public final IllegalArgumentException invalidCookiePath(String value) {
/* 1307 */     IllegalArgumentException result = new IllegalArgumentException(String.format(getLoggingLocale(), invalidCookiePath$str(), new Object[] { value }));
/* 1308 */     _copyStackTraceMinusOne(result);
/* 1309 */     return result;
/*      */   }
/*      */   protected String invalidControlCharacter$str() {
/* 1312 */     return "UT000173: An invalid control character [%s] was present in the cookie value or attribute";
/*      */   }
/*      */   
/*      */   public final IllegalArgumentException invalidControlCharacter(String value) {
/* 1316 */     IllegalArgumentException result = new IllegalArgumentException(String.format(getLoggingLocale(), invalidControlCharacter$str(), new Object[] { value }));
/* 1317 */     _copyStackTraceMinusOne(result);
/* 1318 */     return result;
/*      */   }
/*      */   protected String invalidEscapeCharacter$str() {
/* 1321 */     return "UT000174: An invalid escape character in cookie value";
/*      */   }
/*      */   
/*      */   public final IllegalArgumentException invalidEscapeCharacter() {
/* 1325 */     IllegalArgumentException result = new IllegalArgumentException(String.format(getLoggingLocale(), invalidEscapeCharacter$str(), new Object[0]));
/* 1326 */     _copyStackTraceMinusOne(result);
/* 1327 */     return result;
/*      */   }
/*      */   protected String invalidHpackIndex$str() {
/* 1330 */     return "UT000175: Invalid Hpack index %s";
/*      */   }
/*      */   
/*      */   public final HpackException invalidHpackIndex(int index) {
/* 1334 */     HpackException result = new HpackException();
/* 1335 */     _copyStackTraceMinusOne((Throwable)result);
/* 1336 */     return result;
/*      */   }
/*      */   protected String bufferPoolTooSmall$str() {
/* 1339 */     return "UT000178: Buffer pool is too small, min size is %s";
/*      */   }
/*      */   
/*      */   public final IllegalArgumentException bufferPoolTooSmall(int minSize) {
/* 1343 */     IllegalArgumentException result = new IllegalArgumentException(String.format(getLoggingLocale(), bufferPoolTooSmall$str(), new Object[] { Integer.valueOf(minSize) }));
/* 1344 */     _copyStackTraceMinusOne(result);
/* 1345 */     return result;
/*      */   }
/*      */   protected String invalidProxyHeader$str() {
/* 1348 */     return "UT000179: Invalid PROXY protocol header";
/*      */   }
/*      */   
/*      */   public final IOException invalidProxyHeader() {
/* 1352 */     IOException result = new IOException(String.format(getLoggingLocale(), invalidProxyHeader$str(), new Object[0]));
/* 1353 */     _copyStackTraceMinusOne(result);
/* 1354 */     return result;
/*      */   }
/*      */   protected String headerSizeToLarge$str() {
/* 1357 */     return "UT000180: PROXY protocol header exceeded max size of 107 bytes";
/*      */   }
/*      */   
/*      */   public final IOException headerSizeToLarge() {
/* 1361 */     IOException result = new IOException(String.format(getLoggingLocale(), headerSizeToLarge$str(), new Object[0]));
/* 1362 */     _copyStackTraceMinusOne(result);
/* 1363 */     return result;
/*      */   }
/*      */   protected String http2TrailerToLargeForSingleBuffer$str() {
/* 1366 */     return "UT000181: HTTP/2 trailers too large for single buffer";
/*      */   }
/*      */   
/*      */   public final RuntimeException http2TrailerToLargeForSingleBuffer() {
/* 1370 */     RuntimeException result = new RuntimeException(String.format(getLoggingLocale(), http2TrailerToLargeForSingleBuffer$str(), new Object[0]));
/* 1371 */     _copyStackTraceMinusOne(result);
/* 1372 */     return result;
/*      */   }
/*      */   protected String pingNotSupported$str() {
/* 1375 */     return "UT000182: Ping not supported";
/*      */   }
/*      */   
/*      */   public final IOException pingNotSupported() {
/* 1379 */     IOException result = new IOException(String.format(getLoggingLocale(), pingNotSupported$str(), new Object[0]));
/* 1380 */     _copyStackTraceMinusOne(result);
/* 1381 */     return result;
/*      */   }
/*      */   protected String pingTimeout$str() {
/* 1384 */     return "UT000183: Ping timed out";
/*      */   }
/*      */   
/*      */   public final IOException pingTimeout() {
/* 1388 */     IOException result = new IOException(String.format(getLoggingLocale(), pingTimeout$str(), new Object[0]));
/* 1389 */     _copyStackTraceMinusOne(result);
/* 1390 */     return result;
/*      */   }
/*      */   protected String streamLimitExceeded$str() {
/* 1393 */     return "UT000184: Stream limit exceeded";
/*      */   }
/*      */   
/*      */   public final IOException streamLimitExceeded() {
/* 1397 */     IOException result = new IOException(String.format(getLoggingLocale(), streamLimitExceeded$str(), new Object[0]));
/* 1398 */     _copyStackTraceMinusOne(result);
/* 1399 */     return result;
/*      */   }
/*      */   protected String invalidIpAddress$str() {
/* 1402 */     return "UT000185: Invalid IP address %s";
/*      */   }
/*      */   
/*      */   public final IOException invalidIpAddress(String addressString) {
/* 1406 */     IOException result = new IOException(String.format(getLoggingLocale(), invalidIpAddress$str(), new Object[] { addressString }));
/* 1407 */     _copyStackTraceMinusOne(result);
/* 1408 */     return result;
/*      */   }
/*      */   protected String invalidTlsExt$str() {
/* 1411 */     return "UT000186: Invalid TLS extension";
/*      */   }
/*      */   
/*      */   public final SSLException invalidTlsExt() {
/* 1415 */     SSLException result = new SSLException(String.format(getLoggingLocale(), invalidTlsExt$str(), new Object[0]));
/* 1416 */     _copyStackTraceMinusOne(result);
/* 1417 */     return result;
/*      */   }
/*      */   protected String notEnoughData$str() {
/* 1420 */     return "UT000187: Not enough data";
/*      */   }
/*      */   
/*      */   public final SSLException notEnoughData() {
/* 1424 */     SSLException result = new SSLException(String.format(getLoggingLocale(), notEnoughData$str(), new Object[0]));
/* 1425 */     _copyStackTraceMinusOne(result);
/* 1426 */     return result;
/*      */   }
/*      */   protected String emptyHostNameSni$str() {
/* 1429 */     return "UT000188: Empty host name in SNI extension";
/*      */   }
/*      */   
/*      */   public final SSLException emptyHostNameSni() {
/* 1433 */     SSLException result = new SSLException(String.format(getLoggingLocale(), emptyHostNameSni$str(), new Object[0]));
/* 1434 */     _copyStackTraceMinusOne(result);
/* 1435 */     return result;
/*      */   }
/*      */   protected String duplicatedSniServerName$str() {
/* 1438 */     return "UT000189: Duplicated host name of type %s";
/*      */   }
/*      */   
/*      */   public final SSLException duplicatedSniServerName(int type) {
/* 1442 */     SSLException result = new SSLException(String.format(getLoggingLocale(), duplicatedSniServerName$str(), new Object[] { Integer.valueOf(type) }));
/* 1443 */     _copyStackTraceMinusOne(result);
/* 1444 */     return result;
/*      */   }
/*      */   protected String noContextForSslConnection$str() {
/* 1447 */     return "UT000190: No context for SSL connection";
/*      */   }
/*      */   
/*      */   public final SSLException noContextForSslConnection() {
/* 1451 */     SSLException result = new SSLException(String.format(getLoggingLocale(), noContextForSslConnection$str(), new Object[0]));
/* 1452 */     _copyStackTraceMinusOne(result);
/* 1453 */     return result;
/*      */   }
/*      */   protected String defaultContextCannotBeNull$str() {
/* 1456 */     return "UT000191: Default context cannot be null";
/*      */   }
/*      */   
/*      */   public final IllegalStateException defaultContextCannotBeNull() {
/* 1460 */     IllegalStateException result = new IllegalStateException(String.format(getLoggingLocale(), defaultContextCannotBeNull$str(), new Object[0]));
/* 1461 */     _copyStackTraceMinusOne(result);
/* 1462 */     return result;
/*      */   }
/*      */   protected String formValueIsInMemoryFile$str() {
/* 1465 */     return "UT000192: Form value is a in-memory file, use getFileItem() instead";
/*      */   }
/*      */   
/*      */   public final IllegalStateException formValueIsInMemoryFile() {
/* 1469 */     IllegalStateException result = new IllegalStateException(String.format(getLoggingLocale(), formValueIsInMemoryFile$str(), new Object[0]));
/* 1470 */     _copyStackTraceMinusOne(result);
/* 1471 */     return result;
/*      */   }
/*      */   protected String failedToDecodeParameterValue$str() {
/* 1474 */     return "UT000193: Character decoding failed. Parameter [%s] with value [%s] has been ignored. Note: further occurrences of Parameter errors will be logged at DEBUG level.";
/*      */   }
/*      */   
/*      */   public final String failedToDecodeParameterValue(String parameter, String value, Exception e) {
/* 1478 */     return String.format(getLoggingLocale(), failedToDecodeParameterValue$str(), new Object[] { parameter, value });
/*      */   }
/*      */   protected String failedToDecodeParameterName$str() {
/* 1481 */     return "UT000194: Character decoding failed. Parameter with name [%s] has been ignored. Note: further occurrences of Parameter errors will be logged at DEBUG level.";
/*      */   }
/*      */   
/*      */   public final String failedToDecodeParameterName(String parameter, Exception e) {
/* 1485 */     return String.format(getLoggingLocale(), failedToDecodeParameterName$str(), new Object[] { parameter });
/*      */   }
/*      */   protected String chunkSizeTooLarge$str() {
/* 1488 */     return "UT000195: Chunk size too large";
/*      */   }
/*      */   
/*      */   public final IOException chunkSizeTooLarge() {
/* 1492 */     IOException result = new IOException(String.format(getLoggingLocale(), chunkSizeTooLarge$str(), new Object[0]));
/* 1493 */     _copyStackTraceMinusOne(result);
/* 1494 */     return result;
/*      */   }
/*      */   protected String sessionWithIdAlreadyExists$str() {
/* 1497 */     return "UT000196: Session with id %s already exists";
/*      */   }
/*      */   
/*      */   public final IllegalStateException sessionWithIdAlreadyExists(String sessionID) {
/* 1501 */     IllegalStateException result = new IllegalStateException(String.format(getLoggingLocale(), sessionWithIdAlreadyExists$str(), new Object[] { sessionID }));
/* 1502 */     _copyStackTraceMinusOne(result);
/* 1503 */     return result;
/*      */   }
/*      */   protected String blockingReadTimedOut$str() {
/* 1506 */     return "UT000197: Blocking read timed out after %s nanoseconds.";
/*      */   }
/*      */   
/*      */   public final ReadTimeoutException blockingReadTimedOut(long timeoutNanoseconds) {
/* 1510 */     ReadTimeoutException result = new ReadTimeoutException(String.format(getLoggingLocale(), blockingReadTimedOut$str(), new Object[] { Long.valueOf(timeoutNanoseconds) }));
/* 1511 */     _copyStackTraceMinusOne((Throwable)result);
/* 1512 */     return result;
/*      */   }
/*      */   protected String blockingWriteTimedOut$str() {
/* 1515 */     return "UT000198: Blocking write timed out after %s nanoseconds.";
/*      */   }
/*      */   
/*      */   public final WriteTimeoutException blockingWriteTimedOut(long timeoutNanoseconds) {
/* 1519 */     WriteTimeoutException result = new WriteTimeoutException(String.format(getLoggingLocale(), blockingWriteTimedOut$str(), new Object[] { Long.valueOf(timeoutNanoseconds) }));
/* 1520 */     _copyStackTraceMinusOne((Throwable)result);
/* 1521 */     return result;
/*      */   }
/*      */   protected String readTimedOut$str() {
/* 1524 */     return "UT000199: Read timed out after %s milliseconds.";
/*      */   }
/*      */   
/*      */   public final ReadTimeoutException readTimedOut(long timeoutMilliseconds) {
/* 1528 */     ReadTimeoutException result = new ReadTimeoutException(String.format(getLoggingLocale(), readTimedOut$str(), new Object[] { Long.valueOf(timeoutMilliseconds) }));
/* 1529 */     _copyStackTraceMinusOne((Throwable)result);
/* 1530 */     return result;
/*      */   }
/*      */   protected String failedToReplaceHashOutputStream$str() {
/* 1533 */     return "UT000200: Failed to replace hash output stream ";
/*      */   }
/*      */   
/*      */   public final SSLException failedToReplaceHashOutputStream(Exception e) {
/* 1537 */     SSLException result = new SSLException(String.format(getLoggingLocale(), failedToReplaceHashOutputStream$str(), new Object[0]), e);
/* 1538 */     _copyStackTraceMinusOne(result);
/* 1539 */     return result;
/*      */   }
/*      */   protected String failedToReplaceHashOutputStreamOnWrite$str() {
/* 1542 */     return "UT000201: Failed to replace hash output stream ";
/*      */   }
/*      */   
/*      */   public final RuntimeException failedToReplaceHashOutputStreamOnWrite(Exception e) {
/* 1546 */     RuntimeException result = new RuntimeException(String.format(getLoggingLocale(), failedToReplaceHashOutputStreamOnWrite$str(), new Object[0]), e);
/* 1547 */     _copyStackTraceMinusOne(result);
/* 1548 */     return result;
/*      */   }
/*      */   protected String failedToInitializePathManager$str() {
/* 1551 */     return "UT000202: Failed to initialize path manager for '%s' path.";
/*      */   }
/*      */   
/*      */   public final RuntimeException failedToInitializePathManager(String path, IOException ioe) {
/* 1555 */     RuntimeException result = new RuntimeException(String.format(getLoggingLocale(), failedToInitializePathManager$str(), new Object[] { path }), ioe);
/* 1556 */     _copyStackTraceMinusOne(result);
/* 1557 */     return result;
/*      */   }
/*      */   protected String invalidACLAddress$str() {
/* 1560 */     return "UT000203: Invalid ACL entry";
/*      */   }
/*      */   
/*      */   public final IllegalArgumentException invalidACLAddress(Exception e) {
/* 1564 */     IllegalArgumentException result = new IllegalArgumentException(String.format(getLoggingLocale(), invalidACLAddress$str(), new Object[0]), e);
/* 1565 */     _copyStackTraceMinusOne(result);
/* 1566 */     return result;
/*      */   }
/*      */   protected String noWindowUpdate$str() {
/* 1569 */     return "UT000204: Out of flow control window: no WINDOW_UPDATE received from peer within %s miliseconds";
/*      */   }
/*      */   
/*      */   public final IOException noWindowUpdate(long timeoutMiliseconds) {
/* 1573 */     IOException result = new IOException(String.format(getLoggingLocale(), noWindowUpdate$str(), new Object[] { Long.valueOf(timeoutMiliseconds) }));
/* 1574 */     _copyStackTraceMinusOne(result);
/* 1575 */     return result;
/*      */   }
/*      */   protected String pathNotADirectory$str() {
/* 1578 */     return "UT000205: Path is not a directory '%s'";
/*      */   }
/*      */   
/*      */   public final IOException pathNotADirectory(Path path) {
/* 1582 */     IOException result = new IOException(String.format(getLoggingLocale(), pathNotADirectory$str(), new Object[] { path }));
/* 1583 */     _copyStackTraceMinusOne(result);
/* 1584 */     return result;
/*      */   }
/*      */   protected String pathElementIsRegularFile$str() {
/* 1587 */     return "UT000206: Path '%s' is not a directory";
/*      */   }
/*      */   
/*      */   public final IOException pathElementIsRegularFile(Path path) {
/* 1591 */     IOException result = new IOException(String.format(getLoggingLocale(), pathElementIsRegularFile$str(), new Object[] { path }));
/* 1592 */     _copyStackTraceMinusOne(result);
/* 1593 */     return result;
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\UndertowMessages_$bundle.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */