package io.undertow;

import io.undertow.predicate.PredicateBuilder;
import io.undertow.protocols.http2.HpackException;
import io.undertow.security.api.AuthenticationMechanism;
import io.undertow.server.RequestTooBigException;
import io.undertow.server.handlers.builder.HandlerBuilder;
import io.undertow.server.handlers.form.MultiPartParserDefinition;
import io.undertow.util.BadRequestException;
import io.undertow.util.HttpString;
import io.undertow.util.ParameterLimitException;
import io.undertow.util.UrlDecodeException;
import java.io.IOException;
import java.io.Serializable;
import java.nio.channels.ClosedChannelException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Locale;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.SSLPeerUnverifiedException;
import org.xnio.channels.ReadTimeoutException;
import org.xnio.channels.WriteTimeoutException;

public class UndertowMessages_$bundle implements UndertowMessages, Serializable {
   private static final long serialVersionUID = 1L;
   public static final UndertowMessages_$bundle INSTANCE = new UndertowMessages_$bundle();
   private static final Locale LOCALE;

   protected UndertowMessages_$bundle() {
   }

   protected Object readResolve() {
      return INSTANCE;
   }

   protected Locale getLoggingLocale() {
      return LOCALE;
   }

   protected String maximumConcurrentRequestsMustBeLargerThanZero$str() {
      return "UT000001: Maximum concurrent requests must be larger than zero.";
   }

   public final IllegalArgumentException maximumConcurrentRequestsMustBeLargerThanZero() {
      IllegalArgumentException result = new IllegalArgumentException(String.format(this.getLoggingLocale(), this.maximumConcurrentRequestsMustBeLargerThanZero$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   private static void _copyStackTraceMinusOne(Throwable e) {
      StackTraceElement[] st = e.getStackTrace();
      e.setStackTrace((StackTraceElement[])Arrays.copyOfRange(st, 1, st.length));
   }

   protected String responseAlreadyStarted$str() {
      return "UT000002: The response has already been started";
   }

   public final IllegalStateException responseAlreadyStarted() {
      IllegalStateException result = new IllegalStateException(String.format(this.getLoggingLocale(), this.responseAlreadyStarted$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String responseChannelAlreadyProvided$str() {
      return "UT000004: getResponseChannel() has already been called";
   }

   public final IllegalStateException responseChannelAlreadyProvided() {
      IllegalStateException result = new IllegalStateException(String.format(this.getLoggingLocale(), this.responseChannelAlreadyProvided$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String requestChannelAlreadyProvided$str() {
      return "UT000005: getRequestChannel() has already been called";
   }

   public final IllegalStateException requestChannelAlreadyProvided() {
      IllegalStateException result = new IllegalStateException(String.format(this.getLoggingLocale(), this.requestChannelAlreadyProvided$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String handlerCannotBeNull$str() {
      return "UT000008: Handler cannot be null";
   }

   public final IllegalArgumentException handlerCannotBeNull() {
      IllegalArgumentException result = new IllegalArgumentException(String.format(this.getLoggingLocale(), this.handlerCannotBeNull$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String pathMustBeSpecified$str() {
      return "UT000009: Path must be specified";
   }

   public final IllegalArgumentException pathMustBeSpecified() {
      IllegalArgumentException result = new IllegalArgumentException(String.format(this.getLoggingLocale(), this.pathMustBeSpecified$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String sessionIsInvalid$str() {
      return "UT000010: Session is invalid %s";
   }

   public final IllegalStateException sessionIsInvalid(String sessionId) {
      IllegalStateException result = new IllegalStateException(String.format(this.getLoggingLocale(), this.sessionIsInvalid$str(), sessionId));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String sessionManagerMustNotBeNull$str() {
      return "UT000011: Session manager must not be null";
   }

   public final IllegalStateException sessionManagerMustNotBeNull() {
      IllegalStateException result = new IllegalStateException(String.format(this.getLoggingLocale(), this.sessionManagerMustNotBeNull$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String sessionManagerNotFound$str() {
      return "UT000012: Session manager was not attached to the request. Make sure that the SessionAttachmentHandler is installed in the handler chain";
   }

   public final IllegalStateException sessionManagerNotFound() {
      IllegalStateException result = new IllegalStateException(String.format(this.getLoggingLocale(), this.sessionManagerNotFound$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String argumentCannotBeNull$str() {
      return "UT000013: Argument %s cannot be null";
   }

   public final IllegalArgumentException argumentCannotBeNull(String argument) {
      IllegalArgumentException result = new IllegalArgumentException(String.format(this.getLoggingLocale(), this.argumentCannotBeNull$str(), argument));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String formValueIsAFile$str() {
      return "UT000017: Form value is a file, use getFileItem() instead";
   }

   public final IllegalStateException formValueIsAFile() {
      IllegalStateException result = new IllegalStateException(String.format(this.getLoggingLocale(), this.formValueIsAFile$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String formValueIsAString$str() {
      return "UT000018: Form value is a String, use getValue() instead";
   }

   public final IllegalStateException formValueIsAString() {
      IllegalStateException result = new IllegalStateException(String.format(this.getLoggingLocale(), this.formValueIsAString$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String requestEntityWasTooLarge$str() {
      return "UT000020: Connection terminated as request was larger than %s";
   }

   public final RequestTooBigException requestEntityWasTooLarge(long size) {
      RequestTooBigException result = new RequestTooBigException(String.format(this.getLoggingLocale(), this.requestEntityWasTooLarge$str(), size));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String sessionAlreadyInvalidated$str() {
      return "UT000021: Session already invalidated";
   }

   public final IllegalStateException sessionAlreadyInvalidated() {
      IllegalStateException result = new IllegalStateException(String.format(this.getLoggingLocale(), this.sessionAlreadyInvalidated$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String hashAlgorithmNotFound$str() {
      return "UT000022: The specified hash algorithm '%s' can not be found.";
   }

   public final IllegalArgumentException hashAlgorithmNotFound(String algorithmName) {
      IllegalArgumentException result = new IllegalArgumentException(String.format(this.getLoggingLocale(), this.hashAlgorithmNotFound$str(), algorithmName));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String invalidBase64Token$str() {
      return "UT000023: An invalid Base64 token has been received.";
   }

   public final IllegalArgumentException invalidBase64Token(IOException cause) {
      IllegalArgumentException result = new IllegalArgumentException(String.format(this.getLoggingLocale(), this.invalidBase64Token$str()), cause);
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String invalidNonceReceived$str() {
      return "UT000024: An invalidly formatted nonce has been received.";
   }

   public final IllegalArgumentException invalidNonceReceived() {
      IllegalArgumentException result = new IllegalArgumentException(String.format(this.getLoggingLocale(), this.invalidNonceReceived$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String unexpectedTokenInHeader$str() {
      return "UT000025: Unexpected token '%s' within header.";
   }

   public final IllegalArgumentException unexpectedTokenInHeader(String name) {
      IllegalArgumentException result = new IllegalArgumentException(String.format(this.getLoggingLocale(), this.unexpectedTokenInHeader$str(), name));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String invalidHeader$str() {
      return "UT000026: Invalid header received.";
   }

   public final IllegalArgumentException invalidHeader() {
      IllegalArgumentException result = new IllegalArgumentException(String.format(this.getLoggingLocale(), this.invalidHeader$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String couldNotFindSessionCookieConfig$str() {
      return "UT000027: Could not find session cookie config in the request";
   }

   public final IllegalStateException couldNotFindSessionCookieConfig() {
      IllegalStateException result = new IllegalStateException(String.format(this.getLoggingLocale(), this.couldNotFindSessionCookieConfig$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String chunkedChannelClosedMidChunk$str() {
      return "UT000029: Channel was closed mid chunk, if you have attempted to write chunked data you cannot shutdown the channel until after it has all been written.";
   }

   public final IOException chunkedChannelClosedMidChunk() {
      IOException result = new IOException(String.format(this.getLoggingLocale(), this.chunkedChannelClosedMidChunk$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String userAuthenticated$str() {
      return "UT000030: User %s successfully authenticated.";
   }

   public final String userAuthenticated(String userName) {
      return String.format(this.getLoggingLocale(), this.userAuthenticated$str(), userName);
   }

   protected String userLoggedOut$str() {
      return "UT000031: User %s has logged out.";
   }

   public final String userLoggedOut(String userName) {
      return String.format(this.getLoggingLocale(), this.userLoggedOut$str(), userName);
   }

   protected String streamIsClosed$str() {
      return "UT000034: Stream is closed";
   }

   public final IOException streamIsClosed() {
      IOException result = new IOException(String.format(this.getLoggingLocale(), this.streamIsClosed$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String startBlockingHasNotBeenCalled$str() {
      return "UT000035: Cannot get stream as startBlocking has not been invoked";
   }

   public final IllegalStateException startBlockingHasNotBeenCalled() {
      IllegalStateException result = new IllegalStateException(String.format(this.getLoggingLocale(), this.startBlockingHasNotBeenCalled$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String connectionTerminatedReadingMultiPartData$str() {
      return "UT000036: Connection terminated parsing multipart data";
   }

   public final IOException connectionTerminatedReadingMultiPartData() {
      IOException result = new IOException(String.format(this.getLoggingLocale(), this.connectionTerminatedReadingMultiPartData$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String failedToParsePath$str() {
      return "UT000037: Failed to parse path in HTTP request";
   }

   public final RuntimeException failedToParsePath() {
      RuntimeException result = new RuntimeException(String.format(this.getLoggingLocale(), this.failedToParsePath$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String authenticationFailed$str() {
      return "UT000038: Authentication failed, requested user name '%s'";
   }

   public final String authenticationFailed(String userName) {
      return String.format(this.getLoggingLocale(), this.authenticationFailed$str(), userName);
   }

   protected String tooManyQueryParameters$str() {
      return "UT000039: Too many query parameters, cannot have more than %s query parameters";
   }

   public final BadRequestException tooManyQueryParameters(int noParams) {
      BadRequestException result = new BadRequestException(String.format(this.getLoggingLocale(), this.tooManyQueryParameters$str(), noParams));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String tooManyHeaders$str() {
      return "UT000040: Too many headers, cannot have more than %s header";
   }

   public final String tooManyHeaders(int noParams) {
      return String.format(this.getLoggingLocale(), this.tooManyHeaders$str(), noParams);
   }

   protected String channelIsClosed$str() {
      return "UT000041: Channel is closed";
   }

   public final ClosedChannelException channelIsClosed() {
      ClosedChannelException result = new ClosedChannelException();
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String couldNotDecodeTrailers$str() {
      return "UT000042: Could not decode trailers in HTTP request";
   }

   public final IOException couldNotDecodeTrailers() {
      IOException result = new IOException(String.format(this.getLoggingLocale(), this.couldNotDecodeTrailers$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String dataAlreadyQueued$str() {
      return "UT000043: Data is already being sent. You must wait for the completion callback to be be invoked before calling send() again";
   }

   public final IllegalStateException dataAlreadyQueued() {
      IllegalStateException result = new IllegalStateException(String.format(this.getLoggingLocale(), this.dataAlreadyQueued$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String moreThanOnePredicateWithName$str() {
      return "UT000044: More than one predicate with name %s. Builder class %s and %s";
   }

   public final IllegalStateException moreThanOnePredicateWithName(String name, Class<? extends PredicateBuilder> aClass, Class<? extends PredicateBuilder> existing) {
      IllegalStateException result = new IllegalStateException(String.format(this.getLoggingLocale(), this.moreThanOnePredicateWithName$str(), name, aClass, existing));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String errorParsingPredicateString$str() {
      return "UT000045: Error parsing predicated handler string %s:%n%s";
   }

   public final IllegalArgumentException errorParsingPredicateString(String reason, String s) {
      IllegalArgumentException result = new IllegalArgumentException(String.format(this.getLoggingLocale(), this.errorParsingPredicateString$str(), reason, s));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String tooManyCookies$str() {
      return "UT000046: The number of cookies sent exceeded the maximum of %s";
   }

   public final IllegalStateException tooManyCookies(int maxCookies) {
      IllegalStateException result = new IllegalStateException(String.format(this.getLoggingLocale(), this.tooManyCookies$str(), maxCookies));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String tooManyParameters$str() {
      return "UT000047: The number of parameters exceeded the maximum of %s";
   }

   public final ParameterLimitException tooManyParameters(int maxValues) {
      ParameterLimitException result = new ParameterLimitException(String.format(this.getLoggingLocale(), this.tooManyParameters$str(), maxValues));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String noRequestActive$str() {
      return "UT000048: No request is currently active";
   }

   public final IllegalStateException noRequestActive() {
      IllegalStateException result = new IllegalStateException(String.format(this.getLoggingLocale(), this.noRequestActive$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String authMechanismOutcomeNull$str() {
      return "UT000050: AuthenticationMechanism Outcome is null";
   }

   public final IllegalStateException authMechanismOutcomeNull() {
      IllegalStateException result = new IllegalStateException(String.format(this.getLoggingLocale(), this.authMechanismOutcomeNull$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String notAValidIpPattern$str() {
      return "UT000051: Not a valid IP pattern %s";
   }

   public final IllegalArgumentException notAValidIpPattern(String peer) {
      IllegalArgumentException result = new IllegalArgumentException(String.format(this.getLoggingLocale(), this.notAValidIpPattern$str(), peer));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String noSessionData$str() {
      return "UT000052: Session data requested when non session based authentication in use";
   }

   public final IllegalStateException noSessionData() {
      IllegalStateException result = new IllegalStateException(String.format(this.getLoggingLocale(), this.noSessionData$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String listenerAlreadyRegistered$str() {
      return "UT000053: Listener %s already registered";
   }

   public final IllegalArgumentException listenerAlreadyRegistered(String name) {
      IllegalArgumentException result = new IllegalArgumentException(String.format(this.getLoggingLocale(), this.listenerAlreadyRegistered$str(), name));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String maxFileSizeExceeded$str() {
      return "UT000054: The maximum size %s for an individual file in a multipart request was exceeded";
   }

   public final MultiPartParserDefinition.FileTooLargeException maxFileSizeExceeded(long maxIndividualFileSize) {
      MultiPartParserDefinition.FileTooLargeException result = new MultiPartParserDefinition.FileTooLargeException(String.format(this.getLoggingLocale(), this.maxFileSizeExceeded$str(), maxIndividualFileSize));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String couldNotSetAttribute$str() {
      return "UT000055: Could not set attribute %s to %s as it is read only";
   }

   public final String couldNotSetAttribute(String attributeName, String newValue) {
      return String.format(this.getLoggingLocale(), this.couldNotSetAttribute$str(), attributeName, newValue);
   }

   protected String couldNotParseUriTemplate$str() {
      return "UT000056: Could not parse URI template %s, exception at char %s";
   }

   public final RuntimeException couldNotParseUriTemplate(String path, int i) {
      RuntimeException result = new RuntimeException(String.format(this.getLoggingLocale(), this.couldNotParseUriTemplate$str(), path, i));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String mismatchedBraces$str() {
      return "UT000057: Mismatched braces in attribute string %s";
   }

   public final RuntimeException mismatchedBraces(String valueString) {
      RuntimeException result = new RuntimeException(String.format(this.getLoggingLocale(), this.mismatchedBraces$str(), valueString));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String moreThanOneHandlerWithName$str() {
      return "UT000058: More than one handler with name %s. Builder class %s and %s";
   }

   public final IllegalStateException moreThanOneHandlerWithName(String name, Class<? extends HandlerBuilder> aClass, Class<? extends HandlerBuilder> existing) {
      IllegalStateException result = new IllegalStateException(String.format(this.getLoggingLocale(), this.moreThanOneHandlerWithName$str(), name, aClass, existing));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String outOfBandResponseOnlyAllowedFor100Continue$str() {
      return "UT000061: Out of band responses only allowed for 100-continue requests";
   }

   public final IllegalArgumentException outOfBandResponseOnlyAllowedFor100Continue() {
      IllegalArgumentException result = new IllegalArgumentException(String.format(this.getLoggingLocale(), this.outOfBandResponseOnlyAllowedFor100Continue$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String sslWasNull$str() {
      return "UT000065: SSL must be specified to connect to a https URL";
   }

   public final IOException sslWasNull() {
      IOException result = new IOException(String.format(this.getLoggingLocale(), this.sslWasNull$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String wrongMagicNumber$str() {
      return "UT000066: Incorrect magic number %s for AJP packet header";
   }

   public final IOException wrongMagicNumber(int number) {
      IOException result = new IOException(String.format(this.getLoggingLocale(), this.wrongMagicNumber$str(), number));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String peerUnverified$str() {
      return "UT000067: No client cert was provided";
   }

   public final SSLPeerUnverifiedException peerUnverified() {
      SSLPeerUnverifiedException result = new SSLPeerUnverifiedException(String.format(this.getLoggingLocale(), this.peerUnverified$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String servletPathMatchFailed$str() {
      return "UT000068: Servlet path match failed";
   }

   public final IllegalArgumentException servletPathMatchFailed() {
      IllegalArgumentException result = new IllegalArgumentException(String.format(this.getLoggingLocale(), this.servletPathMatchFailed$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String couldNotParseCookie$str() {
      return "UT000069: Could not parse set cookie header %s";
   }

   public final IllegalArgumentException couldNotParseCookie(String headerValue) {
      IllegalArgumentException result = new IllegalArgumentException(String.format(this.getLoggingLocale(), this.couldNotParseCookie$str(), headerValue));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String canOnlyBeCalledByIoThread$str() {
      return "UT000070: method can only be called by IO thread";
   }

   public final IllegalStateException canOnlyBeCalledByIoThread() {
      IllegalStateException result = new IllegalStateException(String.format(this.getLoggingLocale(), this.canOnlyBeCalledByIoThread$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String matcherAlreadyContainsTemplate$str() {
      return "UT000071: Cannot add path template %s, matcher already contains an equivalent pattern %s";
   }

   public final IllegalStateException matcherAlreadyContainsTemplate(String templateString, String templateString1) {
      IllegalStateException result = new IllegalStateException(String.format(this.getLoggingLocale(), this.matcherAlreadyContainsTemplate$str(), templateString, templateString1));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String failedToDecodeURL$str() {
      return "UT000072: Failed to decode url %s to charset %s";
   }

   public final UrlDecodeException failedToDecodeURL(String s, String enc, Exception e) {
      UrlDecodeException result = new UrlDecodeException(String.format(this.getLoggingLocale(), this.failedToDecodeURL$str(), s, enc), e);
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String resourceChangeListenerNotSupported$str() {
      return "UT000073: Resource change listeners are not supported";
   }

   public final IllegalArgumentException resourceChangeListenerNotSupported() {
      IllegalArgumentException result = new IllegalArgumentException(String.format(this.getLoggingLocale(), this.resourceChangeListenerNotSupported$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String objectWasFreed$str() {
      return "UT000075: Object was freed";
   }

   public final IllegalStateException objectWasFreed() {
      IllegalStateException result = new IllegalStateException(String.format(this.getLoggingLocale(), this.objectWasFreed$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String handlerNotShutdown$str() {
      return "UT000076: Handler not shutdown";
   }

   public final IllegalStateException handlerNotShutdown() {
      IllegalStateException result = new IllegalStateException(String.format(this.getLoggingLocale(), this.handlerNotShutdown$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String upgradeNotSupported$str() {
      return "UT000077: The underlying transport does not support HTTP upgrade";
   }

   public final IllegalStateException upgradeNotSupported() {
      IllegalStateException result = new IllegalStateException(String.format(this.getLoggingLocale(), this.upgradeNotSupported$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String renegotiationNotSupported$str() {
      return "UT000078: Renegotiation not supported";
   }

   public final IOException renegotiationNotSupported() {
      IOException result = new IOException(String.format(this.getLoggingLocale(), this.renegotiationNotSupported$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String notAValidRegularExpressionPattern$str() {
      return "UT000080: Not a valid regular expression pattern %s";
   }

   public final IllegalArgumentException notAValidRegularExpressionPattern(String pattern) {
      IllegalArgumentException result = new IllegalArgumentException(String.format(this.getLoggingLocale(), this.notAValidRegularExpressionPattern$str(), pattern));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String badRequest$str() {
      return "UT000081: Bad request";
   }

   public final BadRequestException badRequest() {
      BadRequestException result = new BadRequestException(String.format(this.getLoggingLocale(), this.badRequest$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String hostAlreadyRegistered$str() {
      return "UT000082: Host %s already registered";
   }

   public final RuntimeException hostAlreadyRegistered(Object host) {
      RuntimeException result = new RuntimeException(String.format(this.getLoggingLocale(), this.hostAlreadyRegistered$str(), host));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String hostHasNotBeenRegistered$str() {
      return "UT000083: Host %s has not been registered";
   }

   public final RuntimeException hostHasNotBeenRegistered(Object host) {
      RuntimeException result = new RuntimeException(String.format(this.getLoggingLocale(), this.hostHasNotBeenRegistered$str(), host));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String extraDataWrittenAfterChunkEnd$str() {
      return "UT000084: Attempted to write additional data after the last chunk";
   }

   public final IOException extraDataWrittenAfterChunkEnd() {
      IOException result = new IOException(String.format(this.getLoggingLocale(), this.extraDataWrittenAfterChunkEnd$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String couldNotGenerateUniqueSessionId$str() {
      return "UT000085: Could not generate unique session id";
   }

   public final RuntimeException couldNotGenerateUniqueSessionId() {
      RuntimeException result = new RuntimeException(String.format(this.getLoggingLocale(), this.couldNotGenerateUniqueSessionId$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String controlFrameCannotHaveBodyContent$str() {
      return "UT000088: SPDY control frames cannot have body content";
   }

   public final IOException controlFrameCannotHaveBodyContent() {
      IOException result = new IOException(String.format(this.getLoggingLocale(), this.controlFrameCannotHaveBodyContent$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String bufferAlreadyFreed$str() {
      return "UT000091: Buffer has already been freed";
   }

   public final IllegalStateException bufferAlreadyFreed() {
      IllegalStateException result = new IllegalStateException(String.format(this.getLoggingLocale(), this.bufferAlreadyFreed$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String awaitCalledFromIoThread$str() {
      return "UT000094: Blocking await method called from IO thread. Blocking IO must be dispatched to a worker thread or deadlocks will result.";
   }

   public final IOException awaitCalledFromIoThread() {
      IOException result = new IOException(String.format(this.getLoggingLocale(), this.awaitCalledFromIoThread$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String recursiveCallToFlushingSenders$str() {
      return "UT000095: Recursive call to flushSenders()";
   }

   public final RuntimeException recursiveCallToFlushingSenders() {
      RuntimeException result = new RuntimeException(String.format(this.getLoggingLocale(), this.recursiveCallToFlushingSenders$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String fixedLengthOverflow$str() {
      return "UT000096: More data was written to the channel than specified in the content-length";
   }

   public final IllegalStateException fixedLengthOverflow() {
      IllegalStateException result = new IllegalStateException(String.format(this.getLoggingLocale(), this.fixedLengthOverflow$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String ajpRequestAlreadyInProgress$str() {
      return "UT000097: AJP request already in progress";
   }

   public final IllegalStateException ajpRequestAlreadyInProgress() {
      IllegalStateException result = new IllegalStateException(String.format(this.getLoggingLocale(), this.ajpRequestAlreadyInProgress$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String httpPingDataMustBeLength8$str() {
      return "UT000098: HTTP ping data must be 8 bytes in length";
   }

   public final String httpPingDataMustBeLength8() {
      return String.format(this.getLoggingLocale(), this.httpPingDataMustBeLength8$str());
   }

   protected String invalidPingSize$str() {
      return "UT000099: Received a ping of size other than 8";
   }

   public final String invalidPingSize() {
      return String.format(this.getLoggingLocale(), this.invalidPingSize$str());
   }

   protected String streamIdMustBeZeroForFrameType$str() {
      return "UT000100: stream id must be zero for frame type %s";
   }

   public final String streamIdMustBeZeroForFrameType(int frameType) {
      return String.format(this.getLoggingLocale(), this.streamIdMustBeZeroForFrameType$str(), frameType);
   }

   protected String streamIdMustNotBeZeroForFrameType$str() {
      return "UT000101: stream id must not be zero for frame type %s";
   }

   public final String streamIdMustNotBeZeroForFrameType(int frameType) {
      return String.format(this.getLoggingLocale(), this.streamIdMustNotBeZeroForFrameType$str(), frameType);
   }

   protected String http2StreamWasReset$str() {
      return "UT000103: Http2 stream was reset";
   }

   public final IOException http2StreamWasReset() {
      IOException result = new IOException(String.format(this.getLoggingLocale(), this.http2StreamWasReset$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String incorrectHttp2Preface$str() {
      return "UT000104: Incorrect HTTP2 preface";
   }

   public final IOException incorrectHttp2Preface() {
      IOException result = new IOException(String.format(this.getLoggingLocale(), this.incorrectHttp2Preface$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String http2FrameTooLarge$str() {
      return "UT000105: HTTP2 frame to large";
   }

   public final IOException http2FrameTooLarge() {
      IOException result = new IOException(String.format(this.getLoggingLocale(), this.http2FrameTooLarge$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String http2ContinuationFrameNotExpected$str() {
      return "UT000106: HTTP2 continuation frame received without a corresponding headers or push promise frame";
   }

   public final IOException http2ContinuationFrameNotExpected() {
      IOException result = new IOException(String.format(this.getLoggingLocale(), this.http2ContinuationFrameNotExpected$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String huffmanEncodedHpackValueDidNotEndWithEOS$str() {
      return "UT000107: Huffman encoded value in HPACK headers did not end with EOS padding";
   }

   public final HpackException huffmanEncodedHpackValueDidNotEndWithEOS() {
      HpackException result = new HpackException();
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String integerEncodedOverTooManyOctets$str() {
      return "UT000108: HPACK variable length integer encoded over too many octects, max is %s";
   }

   public final HpackException integerEncodedOverTooManyOctets(int maxIntegerOctets) {
      HpackException result = new HpackException();
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String zeroNotValidHeaderTableIndex$str() {
      return "UT000109: Zero is not a valid header table index";
   }

   public final HpackException zeroNotValidHeaderTableIndex() {
      HpackException result = new HpackException();
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String cannotSendContinueResponse$str() {
      return "UT000110: Cannot send 100-Continue, getResponseChannel() has already been called";
   }

   public final IOException cannotSendContinueResponse() {
      IOException result = new IOException(String.format(this.getLoggingLocale(), this.cannotSendContinueResponse$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String parserDidNotMakeProgress$str() {
      return "UT000111: Parser did not make progress";
   }

   public final IOException parserDidNotMakeProgress() {
      IOException result = new IOException(String.format(this.getLoggingLocale(), this.parserDidNotMakeProgress$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String headersStreamCanOnlyBeCreatedByClient$str() {
      return "UT000112: Only client side can call createStream, if you wish to send a PUSH_PROMISE frame use createPushPromiseStream instead";
   }

   public final IOException headersStreamCanOnlyBeCreatedByClient() {
      IOException result = new IOException(String.format(this.getLoggingLocale(), this.headersStreamCanOnlyBeCreatedByClient$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String pushPromiseCanOnlyBeCreatedByServer$str() {
      return "UT000113: Only the server side can send a push promise stream";
   }

   public final IOException pushPromiseCanOnlyBeCreatedByServer() {
      IOException result = new IOException(String.format(this.getLoggingLocale(), this.pushPromiseCanOnlyBeCreatedByServer$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String invalidAclRule$str() {
      return "UT000114: Invalid IP access control rule %s. Format is: [ip-match] allow|deny";
   }

   public final IllegalArgumentException invalidAclRule(String rule) {
      IllegalArgumentException result = new IllegalArgumentException(String.format(this.getLoggingLocale(), this.invalidAclRule$str(), rule));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String serverReceivedPushPromise$str() {
      return "UT000115: Server received PUSH_PROMISE frame from client";
   }

   public final IOException serverReceivedPushPromise() {
      IOException result = new IOException(String.format(this.getLoggingLocale(), this.serverReceivedPushPromise$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String connectNotSupported$str() {
      return "UT000116: CONNECT not supported by this connector";
   }

   public final IllegalStateException connectNotSupported() {
      IllegalStateException result = new IllegalStateException(String.format(this.getLoggingLocale(), this.connectNotSupported$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String notAConnectRequest$str() {
      return "UT000117: Request was not a CONNECT request";
   }

   public final IllegalStateException notAConnectRequest() {
      IllegalStateException result = new IllegalStateException(String.format(this.getLoggingLocale(), this.notAConnectRequest$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String cannotResetBuffer$str() {
      return "UT000118: Cannot reset buffer, response has already been commited";
   }

   public final IllegalStateException cannotResetBuffer() {
      IllegalStateException result = new IllegalStateException(String.format(this.getLoggingLocale(), this.cannotResetBuffer$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String http2PriRequestFailed$str() {
      return "UT000119: HTTP2 via prior knowledge failed";
   }

   public final IOException http2PriRequestFailed() {
      IOException result = new IOException(String.format(this.getLoggingLocale(), this.http2PriRequestFailed$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String outOfBandResponseNotSupported$str() {
      return "UT000120: Out of band responses are not allowed for this connector";
   }

   public final IllegalStateException outOfBandResponseNotSupported() {
      IllegalStateException result = new IllegalStateException(String.format(this.getLoggingLocale(), this.outOfBandResponseNotSupported$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String tooManySessions$str() {
      return "UT000121: Session was rejected as the maximum number of sessions (%s) has been hit";
   }

   public final IllegalStateException tooManySessions(int maxSessions) {
      IllegalStateException result = new IllegalStateException(String.format(this.getLoggingLocale(), this.tooManySessions$str(), maxSessions));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String proxyConnectionFailed$str() {
      return "UT000122: CONNECT attempt failed as target proxy returned %s";
   }

   public final IOException proxyConnectionFailed(int responseCode) {
      IOException result = new IOException(String.format(this.getLoggingLocale(), this.proxyConnectionFailed$str(), responseCode));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String mcmpMessageRejectedDueToSuspiciousCharacters$str() {
      return "UT000123: MCMP message %s rejected due to suspicious characters";
   }

   public final RuntimeException mcmpMessageRejectedDueToSuspiciousCharacters(String data) {
      RuntimeException result = new RuntimeException(String.format(this.getLoggingLocale(), this.mcmpMessageRejectedDueToSuspiciousCharacters$str(), data));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String rengotiationTimedOut$str() {
      return "UT000124: renegotiation timed out";
   }

   public final IllegalStateException rengotiationTimedOut() {
      IllegalStateException result = new IllegalStateException(String.format(this.getLoggingLocale(), this.rengotiationTimedOut$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String requestBodyAlreadyRead$str() {
      return "UT000125: Request body already read";
   }

   public final IllegalStateException requestBodyAlreadyRead() {
      IllegalStateException result = new IllegalStateException(String.format(this.getLoggingLocale(), this.requestBodyAlreadyRead$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String blockingIoFromIOThread$str() {
      return "UT000126: Attempted to do blocking IO from the IO thread. This is prohibited as it may result in deadlocks";
   }

   public final IllegalStateException blockingIoFromIOThread() {
      IllegalStateException result = new IllegalStateException(String.format(this.getLoggingLocale(), this.blockingIoFromIOThread$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String responseComplete$str() {
      return "UT000127: Response has already been sent";
   }

   public final IllegalStateException responseComplete() {
      IllegalStateException result = new IllegalStateException(String.format(this.getLoggingLocale(), this.responseComplete$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String couldNotReadContentLengthData$str() {
      return "UT000128: Remote peer closed connection before all data could be read";
   }

   public final IOException couldNotReadContentLengthData() {
      IOException result = new IOException(String.format(this.getLoggingLocale(), this.couldNotReadContentLengthData$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String failedToSendAfterBeingSafe$str() {
      return "UT000129: Failed to send after being safe to send";
   }

   public final IllegalStateException failedToSendAfterBeingSafe() {
      IllegalStateException result = new IllegalStateException(String.format(this.getLoggingLocale(), this.failedToSendAfterBeingSafe$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String reasonPhraseToLargeForBuffer$str() {
      return "UT000130: HTTP reason phrase was too large for the buffer. Either provide a smaller message or a bigger buffer. Phrase: %s";
   }

   public final IllegalStateException reasonPhraseToLargeForBuffer(String phrase) {
      IllegalStateException result = new IllegalStateException(String.format(this.getLoggingLocale(), this.reasonPhraseToLargeForBuffer$str(), phrase));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String poolIsClosed$str() {
      return "UT000131: Buffer pool is closed";
   }

   public final IllegalStateException poolIsClosed() {
      IllegalStateException result = new IllegalStateException(String.format(this.getLoggingLocale(), this.poolIsClosed$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String hpackFailed$str() {
      return "UT000132: HPACK decode failed";
   }

   public final HpackException hpackFailed() {
      HpackException result = new HpackException();
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String notAnUpgradeRequest$str() {
      return "UT000133: Request did not contain an Upgrade header, upgrade is not permitted";
   }

   public final IllegalStateException notAnUpgradeRequest() {
      IllegalStateException result = new IllegalStateException(String.format(this.getLoggingLocale(), this.notAnUpgradeRequest$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String authenticationPropertyNotSet$str() {
      return "UT000134: Authentication mechanism %s requires property %s to be set";
   }

   public final IllegalStateException authenticationPropertyNotSet(String name, String header) {
      IllegalStateException result = new IllegalStateException(String.format(this.getLoggingLocale(), this.authenticationPropertyNotSet$str(), name, header));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String rengotiationFailed$str() {
      return "UT000135: renegotiation failed";
   }

   public final IllegalStateException rengotiationFailed() {
      IllegalStateException result = new IllegalStateException(String.format(this.getLoggingLocale(), this.rengotiationFailed$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String userAgentCharsetMustHaveEvenNumberOfItems$str() {
      return "UT000136: User agent charset string must have an even number of items, in the form pattern,charset,pattern,charset,... Instead got: %s";
   }

   public final IllegalArgumentException userAgentCharsetMustHaveEvenNumberOfItems(String supplied) {
      IllegalArgumentException result = new IllegalArgumentException(String.format(this.getLoggingLocale(), this.userAgentCharsetMustHaveEvenNumberOfItems$str(), supplied));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String datasourceNotFound$str() {
      return "UT000137: Could not find the datasource called %s";
   }

   public final IllegalArgumentException datasourceNotFound(String ds) {
      IllegalArgumentException result = new IllegalArgumentException(String.format(this.getLoggingLocale(), this.datasourceNotFound$str(), ds));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String serverNotStarted$str() {
      return "UT000138: Server not started";
   }

   public final IllegalStateException serverNotStarted() {
      IllegalStateException result = new IllegalStateException(String.format(this.getLoggingLocale(), this.serverNotStarted$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String exchangeAlreadyComplete$str() {
      return "UT000139: Exchange already complete";
   }

   public final IllegalStateException exchangeAlreadyComplete() {
      IllegalStateException result = new IllegalStateException(String.format(this.getLoggingLocale(), this.exchangeAlreadyComplete$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String notHandshakeRecord$str() {
      return "UT000140: Initial SSL/TLS data is not a handshake record";
   }

   public final SSLHandshakeException notHandshakeRecord() {
      SSLHandshakeException result = new SSLHandshakeException(String.format(this.getLoggingLocale(), this.notHandshakeRecord$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String invalidHandshakeRecord$str() {
      return "UT000141: Initial SSL/TLS handshake record is invalid";
   }

   public final SSLHandshakeException invalidHandshakeRecord() {
      SSLHandshakeException result = new SSLHandshakeException(String.format(this.getLoggingLocale(), this.invalidHandshakeRecord$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String multiRecordSSLHandshake$str() {
      return "UT000142: Initial SSL/TLS handshake spans multiple records";
   }

   public final SSLHandshakeException multiRecordSSLHandshake() {
      SSLHandshakeException result = new SSLHandshakeException(String.format(this.getLoggingLocale(), this.multiRecordSSLHandshake$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String expectedClientHello$str() {
      return "UT000143: Expected \"client hello\" record";
   }

   public final SSLHandshakeException expectedClientHello() {
      SSLHandshakeException result = new SSLHandshakeException(String.format(this.getLoggingLocale(), this.expectedClientHello$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String expectedServerHello$str() {
      return "UT000144: Expected server hello";
   }

   public final SSLHandshakeException expectedServerHello() {
      SSLHandshakeException result = new SSLHandshakeException(String.format(this.getLoggingLocale(), this.expectedServerHello$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String tooManyRedirects$str() {
      return "UT000145: Too many redirects";
   }

   public final IOException tooManyRedirects(IOException exception) {
      IOException result = new IOException(String.format(this.getLoggingLocale(), this.tooManyRedirects$str()), exception);
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String resumedAndDispatched$str() {
      return "UT000146: HttpServerExchange cannot have both async IO resumed and dispatch() called in the same cycle";
   }

   public final IllegalStateException resumedAndDispatched() {
      IllegalStateException result = new IllegalStateException(String.format(this.getLoggingLocale(), this.resumedAndDispatched$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String noHostInHttp11Request$str() {
      return "UT000147: No host header in a HTTP/1.1 request";
   }

   public final IOException noHostInHttp11Request() {
      IOException result = new IOException(String.format(this.getLoggingLocale(), this.noHostInHttp11Request$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String invalidHpackEncoding$str() {
      return "UT000148: Invalid HPack encoding. First byte: %s";
   }

   public final HpackException invalidHpackEncoding(byte b) {
      HpackException result = new HpackException();
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String newlineNotSupportedInHttpString$str() {
      return "UT000149: HttpString is not allowed to contain newlines. value: %s";
   }

   public final IllegalArgumentException newlineNotSupportedInHttpString(String value) {
      IllegalArgumentException result = new IllegalArgumentException(String.format(this.getLoggingLocale(), this.newlineNotSupportedInHttpString$str(), value));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String pseudoHeaderInWrongOrder$str() {
      return "UT000150: Pseudo header %s received after receiving normal headers. Pseudo headers must be the first headers in a HTTP/2 header block.";
   }

   public final String pseudoHeaderInWrongOrder(HttpString header) {
      return String.format(this.getLoggingLocale(), this.pseudoHeaderInWrongOrder$str(), header);
   }

   protected String expectedContinuationFrame$str() {
      return "UT000151: Expected to receive a continuation frame";
   }

   public final String expectedContinuationFrame() {
      return String.format(this.getLoggingLocale(), this.expectedContinuationFrame$str());
   }

   protected String incorrectFrameSize$str() {
      return "UT000152: Incorrect frame size";
   }

   public final String incorrectFrameSize() {
      return String.format(this.getLoggingLocale(), this.incorrectFrameSize$str());
   }

   protected String streamNotRegistered$str() {
      return "UT000153: Stream id not registered";
   }

   public final IllegalStateException streamNotRegistered() {
      IllegalStateException result = new IllegalStateException(String.format(this.getLoggingLocale(), this.streamNotRegistered$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String sendChallengeReturnedNull$str() {
      return "UT000154: Mechanism %s returned a null result from sendChallenge()";
   }

   public final NullPointerException sendChallengeReturnedNull(AuthenticationMechanism mechanism) {
      NullPointerException result = new NullPointerException(String.format(this.getLoggingLocale(), this.sendChallengeReturnedNull$str(), mechanism));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String bodyIsSetAndNotReadyForFlush$str() {
      return "UT000155: Framed channel body was set when it was not ready for flush";
   }

   public final IllegalStateException bodyIsSetAndNotReadyForFlush() {
      IllegalStateException result = new IllegalStateException(String.format(this.getLoggingLocale(), this.bodyIsSetAndNotReadyForFlush$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String invalidGzipHeader$str() {
      return "UT000156: Invalid GZIP header";
   }

   public final IOException invalidGzipHeader() {
      IOException result = new IOException(String.format(this.getLoggingLocale(), this.invalidGzipHeader$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String invalidGZIPFooter$str() {
      return "UT000157: Invalid GZIP footer";
   }

   public final IOException invalidGZIPFooter() {
      IOException result = new IOException(String.format(this.getLoggingLocale(), this.invalidGZIPFooter$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String responseTooLargeToBuffer$str() {
      return "UT000158: Response of length %s is too large to buffer";
   }

   public final IllegalStateException responseTooLargeToBuffer(Long length) {
      IllegalStateException result = new IllegalStateException(String.format(this.getLoggingLocale(), this.responseTooLargeToBuffer$str(), length));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String headerBlockTooLarge$str() {
      return "UT000161: HTTP/2 header block is too large";
   }

   public final String headerBlockTooLarge() {
      return String.format(this.getLoggingLocale(), this.headerBlockTooLarge$str());
   }

   protected String invalidSameSiteMode$str() {
      return "UT000162: An invalid SameSite attribute [%s] is specified. It must be one of %s";
   }

   public final IllegalArgumentException invalidSameSiteMode(String mode, String validAttributes) {
      IllegalArgumentException result = new IllegalArgumentException(String.format(this.getLoggingLocale(), this.invalidSameSiteMode$str(), mode, validAttributes));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String invalidToken$str() {
      return "UT000163: Invalid token %s";
   }

   public final IllegalArgumentException invalidToken(byte c) {
      IllegalArgumentException result = new IllegalArgumentException(String.format(this.getLoggingLocale(), this.invalidToken$str(), c));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String invalidHeaders$str() {
      return "UT000164: Request contained invalid headers";
   }

   public final IllegalArgumentException invalidHeaders() {
      IllegalArgumentException result = new IllegalArgumentException(String.format(this.getLoggingLocale(), this.invalidHeaders$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String invalidCharacterInRequestTarget$str() {
      return "UT000165: Invalid character %s in request-target";
   }

   public final String invalidCharacterInRequestTarget(char next) {
      return String.format(this.getLoggingLocale(), this.invalidCharacterInRequestTarget$str(), next);
   }

   protected String objectIsClosed$str() {
      return "UT000166: Pooled object is closed";
   }

   public final IllegalStateException objectIsClosed() {
      IllegalStateException result = new IllegalStateException(String.format(this.getLoggingLocale(), this.objectIsClosed$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String moreThanOneHostHeader$str() {
      return "UT000167: More than one host header in request";
   }

   public final IOException moreThanOneHostHeader() {
      IOException result = new IOException(String.format(this.getLoggingLocale(), this.moreThanOneHostHeader$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String invalidCookieValue$str() {
      return "UT000168: An invalid character [ASCII code: %s] was present in the cookie value";
   }

   public final IllegalArgumentException invalidCookieValue(String value) {
      IllegalArgumentException result = new IllegalArgumentException(String.format(this.getLoggingLocale(), this.invalidCookieValue$str(), value));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String invalidCookieDomain$str() {
      return "UT000169: An invalid domain [%s] was specified for this cookie";
   }

   public final IllegalArgumentException invalidCookieDomain(String value) {
      IllegalArgumentException result = new IllegalArgumentException(String.format(this.getLoggingLocale(), this.invalidCookieDomain$str(), value));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String invalidCookiePath$str() {
      return "UT000170: An invalid path [%s] was specified for this cookie";
   }

   public final IllegalArgumentException invalidCookiePath(String value) {
      IllegalArgumentException result = new IllegalArgumentException(String.format(this.getLoggingLocale(), this.invalidCookiePath$str(), value));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String invalidControlCharacter$str() {
      return "UT000173: An invalid control character [%s] was present in the cookie value or attribute";
   }

   public final IllegalArgumentException invalidControlCharacter(String value) {
      IllegalArgumentException result = new IllegalArgumentException(String.format(this.getLoggingLocale(), this.invalidControlCharacter$str(), value));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String invalidEscapeCharacter$str() {
      return "UT000174: An invalid escape character in cookie value";
   }

   public final IllegalArgumentException invalidEscapeCharacter() {
      IllegalArgumentException result = new IllegalArgumentException(String.format(this.getLoggingLocale(), this.invalidEscapeCharacter$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String invalidHpackIndex$str() {
      return "UT000175: Invalid Hpack index %s";
   }

   public final HpackException invalidHpackIndex(int index) {
      HpackException result = new HpackException();
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String bufferPoolTooSmall$str() {
      return "UT000178: Buffer pool is too small, min size is %s";
   }

   public final IllegalArgumentException bufferPoolTooSmall(int minSize) {
      IllegalArgumentException result = new IllegalArgumentException(String.format(this.getLoggingLocale(), this.bufferPoolTooSmall$str(), minSize));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String invalidProxyHeader$str() {
      return "UT000179: Invalid PROXY protocol header";
   }

   public final IOException invalidProxyHeader() {
      IOException result = new IOException(String.format(this.getLoggingLocale(), this.invalidProxyHeader$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String headerSizeToLarge$str() {
      return "UT000180: PROXY protocol header exceeded max size of 107 bytes";
   }

   public final IOException headerSizeToLarge() {
      IOException result = new IOException(String.format(this.getLoggingLocale(), this.headerSizeToLarge$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String http2TrailerToLargeForSingleBuffer$str() {
      return "UT000181: HTTP/2 trailers too large for single buffer";
   }

   public final RuntimeException http2TrailerToLargeForSingleBuffer() {
      RuntimeException result = new RuntimeException(String.format(this.getLoggingLocale(), this.http2TrailerToLargeForSingleBuffer$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String pingNotSupported$str() {
      return "UT000182: Ping not supported";
   }

   public final IOException pingNotSupported() {
      IOException result = new IOException(String.format(this.getLoggingLocale(), this.pingNotSupported$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String pingTimeout$str() {
      return "UT000183: Ping timed out";
   }

   public final IOException pingTimeout() {
      IOException result = new IOException(String.format(this.getLoggingLocale(), this.pingTimeout$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String streamLimitExceeded$str() {
      return "UT000184: Stream limit exceeded";
   }

   public final IOException streamLimitExceeded() {
      IOException result = new IOException(String.format(this.getLoggingLocale(), this.streamLimitExceeded$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String invalidIpAddress$str() {
      return "UT000185: Invalid IP address %s";
   }

   public final IOException invalidIpAddress(String addressString) {
      IOException result = new IOException(String.format(this.getLoggingLocale(), this.invalidIpAddress$str(), addressString));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String invalidTlsExt$str() {
      return "UT000186: Invalid TLS extension";
   }

   public final SSLException invalidTlsExt() {
      SSLException result = new SSLException(String.format(this.getLoggingLocale(), this.invalidTlsExt$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String notEnoughData$str() {
      return "UT000187: Not enough data";
   }

   public final SSLException notEnoughData() {
      SSLException result = new SSLException(String.format(this.getLoggingLocale(), this.notEnoughData$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String emptyHostNameSni$str() {
      return "UT000188: Empty host name in SNI extension";
   }

   public final SSLException emptyHostNameSni() {
      SSLException result = new SSLException(String.format(this.getLoggingLocale(), this.emptyHostNameSni$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String duplicatedSniServerName$str() {
      return "UT000189: Duplicated host name of type %s";
   }

   public final SSLException duplicatedSniServerName(int type) {
      SSLException result = new SSLException(String.format(this.getLoggingLocale(), this.duplicatedSniServerName$str(), type));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String noContextForSslConnection$str() {
      return "UT000190: No context for SSL connection";
   }

   public final SSLException noContextForSslConnection() {
      SSLException result = new SSLException(String.format(this.getLoggingLocale(), this.noContextForSslConnection$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String defaultContextCannotBeNull$str() {
      return "UT000191: Default context cannot be null";
   }

   public final IllegalStateException defaultContextCannotBeNull() {
      IllegalStateException result = new IllegalStateException(String.format(this.getLoggingLocale(), this.defaultContextCannotBeNull$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String formValueIsInMemoryFile$str() {
      return "UT000192: Form value is a in-memory file, use getFileItem() instead";
   }

   public final IllegalStateException formValueIsInMemoryFile() {
      IllegalStateException result = new IllegalStateException(String.format(this.getLoggingLocale(), this.formValueIsInMemoryFile$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String failedToDecodeParameterValue$str() {
      return "UT000193: Character decoding failed. Parameter [%s] with value [%s] has been ignored. Note: further occurrences of Parameter errors will be logged at DEBUG level.";
   }

   public final String failedToDecodeParameterValue(String parameter, String value, Exception e) {
      return String.format(this.getLoggingLocale(), this.failedToDecodeParameterValue$str(), parameter, value);
   }

   protected String failedToDecodeParameterName$str() {
      return "UT000194: Character decoding failed. Parameter with name [%s] has been ignored. Note: further occurrences of Parameter errors will be logged at DEBUG level.";
   }

   public final String failedToDecodeParameterName(String parameter, Exception e) {
      return String.format(this.getLoggingLocale(), this.failedToDecodeParameterName$str(), parameter);
   }

   protected String chunkSizeTooLarge$str() {
      return "UT000195: Chunk size too large";
   }

   public final IOException chunkSizeTooLarge() {
      IOException result = new IOException(String.format(this.getLoggingLocale(), this.chunkSizeTooLarge$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String sessionWithIdAlreadyExists$str() {
      return "UT000196: Session with id %s already exists";
   }

   public final IllegalStateException sessionWithIdAlreadyExists(String sessionID) {
      IllegalStateException result = new IllegalStateException(String.format(this.getLoggingLocale(), this.sessionWithIdAlreadyExists$str(), sessionID));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String blockingReadTimedOut$str() {
      return "UT000197: Blocking read timed out after %s nanoseconds.";
   }

   public final ReadTimeoutException blockingReadTimedOut(long timeoutNanoseconds) {
      ReadTimeoutException result = new ReadTimeoutException(String.format(this.getLoggingLocale(), this.blockingReadTimedOut$str(), timeoutNanoseconds));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String blockingWriteTimedOut$str() {
      return "UT000198: Blocking write timed out after %s nanoseconds.";
   }

   public final WriteTimeoutException blockingWriteTimedOut(long timeoutNanoseconds) {
      WriteTimeoutException result = new WriteTimeoutException(String.format(this.getLoggingLocale(), this.blockingWriteTimedOut$str(), timeoutNanoseconds));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String readTimedOut$str() {
      return "UT000199: Read timed out after %s milliseconds.";
   }

   public final ReadTimeoutException readTimedOut(long timeoutMilliseconds) {
      ReadTimeoutException result = new ReadTimeoutException(String.format(this.getLoggingLocale(), this.readTimedOut$str(), timeoutMilliseconds));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String failedToReplaceHashOutputStream$str() {
      return "UT000200: Failed to replace hash output stream ";
   }

   public final SSLException failedToReplaceHashOutputStream(Exception e) {
      SSLException result = new SSLException(String.format(this.getLoggingLocale(), this.failedToReplaceHashOutputStream$str()), e);
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String failedToReplaceHashOutputStreamOnWrite$str() {
      return "UT000201: Failed to replace hash output stream ";
   }

   public final RuntimeException failedToReplaceHashOutputStreamOnWrite(Exception e) {
      RuntimeException result = new RuntimeException(String.format(this.getLoggingLocale(), this.failedToReplaceHashOutputStreamOnWrite$str()), e);
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String failedToInitializePathManager$str() {
      return "UT000202: Failed to initialize path manager for '%s' path.";
   }

   public final RuntimeException failedToInitializePathManager(String path, IOException ioe) {
      RuntimeException result = new RuntimeException(String.format(this.getLoggingLocale(), this.failedToInitializePathManager$str(), path), ioe);
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String invalidACLAddress$str() {
      return "UT000203: Invalid ACL entry";
   }

   public final IllegalArgumentException invalidACLAddress(Exception e) {
      IllegalArgumentException result = new IllegalArgumentException(String.format(this.getLoggingLocale(), this.invalidACLAddress$str()), e);
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String noWindowUpdate$str() {
      return "UT000204: Out of flow control window: no WINDOW_UPDATE received from peer within %s miliseconds";
   }

   public final IOException noWindowUpdate(long timeoutMiliseconds) {
      IOException result = new IOException(String.format(this.getLoggingLocale(), this.noWindowUpdate$str(), timeoutMiliseconds));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String pathNotADirectory$str() {
      return "UT000205: Path is not a directory '%s'";
   }

   public final IOException pathNotADirectory(Path path) {
      IOException result = new IOException(String.format(this.getLoggingLocale(), this.pathNotADirectory$str(), path));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String pathElementIsRegularFile$str() {
      return "UT000206: Path '%s' is not a directory";
   }

   public final IOException pathElementIsRegularFile(Path path) {
      IOException result = new IOException(String.format(this.getLoggingLocale(), this.pathElementIsRegularFile$str(), path));
      _copyStackTraceMinusOne(result);
      return result;
   }

   static {
      LOCALE = Locale.ROOT;
   }
}
