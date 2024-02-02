package io.undertow;

import io.undertow.protocols.ssl.SslConduit;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.ServerConnection;
import io.undertow.server.handlers.sse.ServerSentEventConnection;
import io.undertow.util.HeaderMap;
import io.undertow.util.HttpString;
import java.io.IOException;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.URI;
import java.nio.file.Path;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.RejectedExecutionException;
import org.jboss.logging.BasicLogger;
import org.jboss.logging.DelegatingBasicLogger;
import org.jboss.logging.Logger;
import org.xnio.channels.ReadTimeoutException;
import org.xnio.channels.WriteTimeoutException;
import org.xnio.ssl.SslConnection;

public class UndertowLogger_$logger extends DelegatingBasicLogger implements UndertowLogger, BasicLogger, Serializable {
   private static final long serialVersionUID = 1L;
   private static final String FQCN = UndertowLogger_$logger.class.getName();
   private static final Locale LOCALE;

   public UndertowLogger_$logger(Logger log) {
      super(log);
   }

   protected Locale getLoggingLocale() {
      return LOCALE;
   }

   public final void exceptionProcessingRequest(Throwable cause) {
      super.log.logf(FQCN, Logger.Level.ERROR, cause, this.exceptionProcessingRequest$str());
   }

   protected String exceptionProcessingRequest$str() {
      return "UT005001: An exception occurred processing the request";
   }

   public final void ioExceptionReadingFromChannel(IOException e) {
      super.log.logf((String)FQCN, (Logger.Level)Logger.Level.ERROR, (Throwable)e, (String)this.ioExceptionReadingFromChannel$str(), (Object[])());
   }

   protected String ioExceptionReadingFromChannel$str() {
      return "UT005003: IOException reading from channel";
   }

   public final void cannotRemoveUploadedFile(Path file) {
      super.log.logf((String)FQCN, (Logger.Level)Logger.Level.ERROR, (Throwable)null, (String)this.cannotRemoveUploadedFile$str(), (Object)file);
   }

   protected String cannotRemoveUploadedFile$str() {
      return "UT005005: Cannot remove uploaded file %s";
   }

   public final void requestHeaderWasTooLarge(SocketAddress address, int size) {
      super.log.logf((String)FQCN, (Logger.Level)Logger.Level.DEBUG, (Throwable)null, (String)this.requestHeaderWasTooLarge$str(), address, size);
   }

   protected String requestHeaderWasTooLarge$str() {
      return "UT005006: Connection from %s terminated as request header was larger than %s";
   }

   public final void requestWasNotFullyConsumed() {
      super.log.logf((String)FQCN, (Logger.Level)Logger.Level.DEBUG, (Throwable)null, (String)this.requestWasNotFullyConsumed$str(), (Object[])());
   }

   protected String requestWasNotFullyConsumed$str() {
      return "UT005007: Request was not fully consumed";
   }

   public final void invalidTokenReceived(String tokenName, String tokenValue) {
      super.log.logf((String)FQCN, (Logger.Level)Logger.Level.DEBUG, (Throwable)null, (String)this.invalidTokenReceived$str(), tokenName, tokenValue);
   }

   protected String invalidTokenReceived$str() {
      return "UT005008: An invalid token '%s' with value '%s' has been received.";
   }

   public final void missingAuthorizationToken(String tokenName) {
      super.log.logf((String)FQCN, (Logger.Level)Logger.Level.DEBUG, (Throwable)null, (String)this.missingAuthorizationToken$str(), (Object)tokenName);
   }

   protected String missingAuthorizationToken$str() {
      return "UT005009: A mandatory token %s is missing from the request.";
   }

   public final void authenticationFailed(String userName, String mechanism) {
      super.log.logf((String)FQCN, (Logger.Level)Logger.Level.DEBUG, (Throwable)null, (String)this.authenticationFailed$str(), userName, mechanism);
   }

   protected String authenticationFailed$str() {
      return "UT005010: Verification of authentication tokens for user '%s' has failed using mechanism '%s'.";
   }

   public final void ignoringAjpRequestWithPrefixCode(byte prefix) {
      super.log.logf((String)FQCN, (Logger.Level)Logger.Level.ERROR, (Throwable)null, (String)this.ignoringAjpRequestWithPrefixCode$str(), (Object)prefix);
   }

   protected String ignoringAjpRequestWithPrefixCode$str() {
      return "UT005011: Ignoring AJP request with prefix %s";
   }

   public final void ioException(IOException e) {
      super.log.logf((String)FQCN, (Logger.Level)Logger.Level.DEBUG, (Throwable)e, (String)this.ioException$str(), (Object[])());
   }

   protected String ioException$str() {
      return "UT005013: An IOException occurred";
   }

   public final void failedToParseRequest(Throwable e) {
      super.log.logf(FQCN, Logger.Level.DEBUG, e, this.failedToParseRequest$str());
   }

   protected String failedToParseRequest$str() {
      return "UT005014: Failed to parse request";
   }

   public final void errorRotatingAccessLog(IOException e) {
      super.log.logf((String)FQCN, (Logger.Level)Logger.Level.ERROR, (Throwable)e, (String)this.errorRotatingAccessLog$str(), (Object[])());
   }

   protected String errorRotatingAccessLog$str() {
      return "UT005015: Error rotating access log";
   }

   public final void errorWritingAccessLog(IOException e) {
      super.log.logf((String)FQCN, (Logger.Level)Logger.Level.ERROR, (Throwable)e, (String)this.errorWritingAccessLog$str(), (Object[])());
   }

   protected String errorWritingAccessLog$str() {
      return "UT005016: Error writing access log";
   }

   public final void unknownVariable(String token) {
      super.log.logf((String)FQCN, (Logger.Level)Logger.Level.ERROR, (Throwable)null, (String)this.unknownVariable$str(), (Object)token);
   }

   protected String unknownVariable$str() {
      return "UT005017: Unknown variable %s. For the literal percent character use two percent characters: '%%'";
   }

   public final void exceptionInvokingCloseListener(ServerConnection.CloseListener l, Throwable e) {
      super.log.logf((String)FQCN, (Logger.Level)Logger.Level.ERROR, (Throwable)e, (String)this.exceptionInvokingCloseListener$str(), (Object)l);
   }

   protected String exceptionInvokingCloseListener$str() {
      return "UT005018: Exception invoking close listener %s";
   }

   public final void errorWritingJDBCLog(Exception e) {
      super.log.logf((String)FQCN, (Logger.Level)Logger.Level.ERROR, (Throwable)e, (String)this.errorWritingJDBCLog$str(), (Object[])());
   }

   protected String errorWritingJDBCLog$str() {
      return "UT005020: Error writing JDBC log";
   }

   public final void exceptionGeneratingErrorPage(Exception e, String location) {
      super.log.logf((String)FQCN, (Logger.Level)Logger.Level.ERROR, (Throwable)e, (String)this.exceptionGeneratingErrorPage$str(), (Object)location);
   }

   protected String exceptionGeneratingErrorPage$str() {
      return "UT005022: Exception generating error page %s";
   }

   public final void exceptionHandlingRequest(Throwable t, String requestURI) {
      super.log.logf((String)FQCN, (Logger.Level)Logger.Level.ERROR, (Throwable)t, (String)this.exceptionHandlingRequest$str(), (Object)requestURI);
   }

   protected String exceptionHandlingRequest$str() {
      return "UT005023: Exception handling request to %s";
   }

   public final void couldNotRegisterChangeListener(Exception e) {
      super.log.logf((String)FQCN, (Logger.Level)Logger.Level.ERROR, (Throwable)e, (String)this.couldNotRegisterChangeListener$str(), (Object[])());
   }

   protected String couldNotRegisterChangeListener$str() {
      return "UT005024: Could not register resource change listener for caching resource manager, automatic invalidation of cached resource will not work";
   }

   public final void timingOutRequest(String requestURI) {
      super.log.logf((String)FQCN, (Logger.Level)Logger.Level.ERROR, (Throwable)null, (String)this.timingOutRequest$str(), (Object)requestURI);
   }

   protected String timingOutRequest$str() {
      return "UT005027: Timing out request to %s";
   }

   public final void proxyRequestFailed(String requestURI, Exception e) {
      super.log.logf((String)FQCN, (Logger.Level)Logger.Level.ERROR, (Throwable)e, (String)this.proxyRequestFailed$str(), (Object)requestURI);
   }

   protected String proxyRequestFailed$str() {
      return "UT005028: Proxy request to %s failed";
   }

   public final void proxyFailedToConnectToBackend(String requestURI, URI uri) {
      super.log.logf((String)FQCN, (Logger.Level)Logger.Level.ERROR, (Throwable)null, (String)this.proxyFailedToConnectToBackend$str(), requestURI, uri);
   }

   protected String proxyFailedToConnectToBackend$str() {
      return "UT005031: Proxy request to %s could not connect to backend server %s";
   }

   public final void listenerNotProgressing() {
      super.log.logf((String)FQCN, (Logger.Level)Logger.Level.ERROR, (Throwable)null, (String)this.listenerNotProgressing$str(), (Object[])());
   }

   protected String listenerNotProgressing$str() {
      return "UT005032: Listener not making progress on framed channel, closing channel to prevent infinite loop";
   }

   public final void remoteEndpointFailedToSendInitialSettings(int type) {
      super.log.logf((String)FQCN, (Logger.Level)Logger.Level.ERROR, (Throwable)null, (String)this.remoteEndpointFailedToSendInitialSettings$str(), (Object)type);
   }

   protected String remoteEndpointFailedToSendInitialSettings$str() {
      return "UT005034: Remote endpoint failed to send initial settings frame in HTTP2 connection, frame type %s";
   }

   public final void parseRequestTimedOut(SocketAddress remoteAddress) {
      super.log.logf((String)FQCN, (Logger.Level)Logger.Level.DEBUG, (Throwable)null, (String)this.parseRequestTimedOut$str(), (Object)remoteAddress);
   }

   protected String parseRequestTimedOut$str() {
      return "UT005035: Closing channel because of parse timeout for remote address %s";
   }

   public final void noALPNFallback(SocketAddress address) {
      super.log.logf((String)FQCN, (Logger.Level)Logger.Level.ERROR, (Throwable)null, (String)this.noALPNFallback$str(), (Object)address);
   }

   protected String noALPNFallback$str() {
      return "UT005036: ALPN negotiation failed for %s and no fallback defined, closing connection";
   }

   public final void stickySessionCookieLengthTruncated(String original, String current) {
      super.log.logf((String)FQCN, (Logger.Level)Logger.Level.WARN, (Throwable)null, (String)this.stickySessionCookieLengthTruncated$str(), original, current);
   }

   protected String stickySessionCookieLengthTruncated$str() {
      return "UT005037: Name of the cookie containing the session id, %s, had been too long and was truncated to: %s";
   }

   public final void balancerCreated(int id, String name, boolean stickySession, String stickySessionCookie, String stickySessionPath, boolean stickySessionRemove, boolean stickySessionForce, int waitWorker, int maxattempts) {
      super.log.logf((String)FQCN, (Logger.Level)Logger.Level.DEBUG, (Throwable)null, (String)this.balancerCreated$str(), (Object[])(id, name, stickySession, stickySessionCookie, stickySessionPath, stickySessionRemove, stickySessionForce, waitWorker, maxattempts));
   }

   protected String balancerCreated$str() {
      return "UT005038: Balancer created: id: %s, name: %s, stickySession: %s, stickySessionCookie: %s, stickySessionPath: %s, stickySessionRemove: %s, stickySessionForce: %s, waitWorker: %s, maxattempts: %s";
   }

   public final void proxyAdvertisementsStarted(String address, int frequency) {
      super.log.logf((String)FQCN, (Logger.Level)Logger.Level.INFO, (Throwable)null, (String)this.proxyAdvertisementsStarted$str(), address, frequency);
   }

   protected String proxyAdvertisementsStarted$str() {
      return "UT005039: Undertow starts mod_cluster proxy advertisements on %s with frequency %s ms";
   }

   public final void proxyAdvertiseMessagePayload(String payload) {
      super.log.logf((String)FQCN, (Logger.Level)Logger.Level.DEBUG, (Throwable)null, (String)this.proxyAdvertiseMessagePayload$str(), (Object)payload);
   }

   protected String proxyAdvertiseMessagePayload$str() {
      return "UT005040: Gonna send payload:\n%s";
   }

   public final void proxyAdvertiseCannotSendMessage(Exception e, InetSocketAddress address) {
      super.log.logf((String)FQCN, (Logger.Level)Logger.Level.ERROR, (Throwable)e, (String)this.proxyAdvertiseCannotSendMessage$str(), (Object)address);
   }

   protected String proxyAdvertiseCannotSendMessage$str() {
      return "UT005041: Cannot send advertise message. Address: %s";
   }

   public final void mcmpHandlerCreated() {
      super.log.logf((String)FQCN, (Logger.Level)Logger.Level.DEBUG, (Throwable)null, (String)this.mcmpHandlerCreated$str(), (Object[])());
   }

   protected String mcmpHandlerCreated$str() {
      return "UT005042: Undertow mod_cluster proxy MCMPHandler created";
   }

   public final void mcmpProcessingError(String type, String errString) {
      super.log.logf((String)FQCN, (Logger.Level)Logger.Level.ERROR, (Throwable)null, (String)this.mcmpProcessingError$str(), type, errString);
   }

   protected String mcmpProcessingError$str() {
      return "UT005043: Error in processing MCMP commands: Type:%s, Mess: %s";
   }

   public final void removingNode(String jvmRoute) {
      super.log.logf((String)FQCN, (Logger.Level)Logger.Level.INFO, (Throwable)null, (String)this.removingNode$str(), (Object)jvmRoute);
   }

   protected String removingNode$str() {
      return "UT005044: Removing node %s";
   }

   public final void registeringContext(String contextPath, String jvmRoute) {
      super.log.logf((String)FQCN, (Logger.Level)Logger.Level.INFO, (Throwable)null, (String)this.registeringContext2$str(), contextPath, jvmRoute);
   }

   protected String registeringContext2$str() {
      return "UT005045: Registering context %s, for node %s";
   }

   public final void registeringContext(String contextPath, String jvmRoute, List<String> aliases) {
      super.log.logf(FQCN, Logger.Level.DEBUG, (Throwable)null, this.registeringContext3$str(), contextPath, jvmRoute, aliases);
   }

   protected String registeringContext3$str() {
      return "UT005046: Registering context %s, for node %s, with aliases %s";
   }

   public final void unregisteringContext(String contextPath, String jvmRoute) {
      super.log.logf((String)FQCN, (Logger.Level)Logger.Level.INFO, (Throwable)null, (String)this.unregisteringContext$str(), contextPath, jvmRoute);
   }

   protected String unregisteringContext$str() {
      return "UT005047: Unregistering context %s, from node %s";
   }

   public final void nodeIsInError(String jvmRoute) {
      super.log.logf((String)FQCN, (Logger.Level)Logger.Level.DEBUG, (Throwable)null, (String)this.nodeIsInError$str(), (Object)jvmRoute);
   }

   protected String nodeIsInError$str() {
      return "UT005048: Node %s in error";
   }

   public final void nodeConfigCreated(URI connectionURI, String balancer, String domain, String jvmRoute, boolean flushPackets, int flushwait, int ping, long ttl, int timeout, int maxConnections, int cacheConnections, int requestQueueSize, boolean queueNewRequests) {
      super.log.logf((String)FQCN, (Logger.Level)Logger.Level.DEBUG, (Throwable)null, (String)this.nodeConfigCreated$str(), (Object[])(connectionURI, balancer, domain, jvmRoute, flushPackets, flushwait, ping, ttl, timeout, maxConnections, cacheConnections, requestQueueSize, queueNewRequests));
   }

   protected String nodeConfigCreated$str() {
      return "UT005049: NodeConfig created: connectionURI: %s, balancer: %s, load balancing group: %s, jvmRoute: %s, flushPackets: %s, flushwait: %s, ping: %s,ttl: %s, timeout: %s, maxConnections: %s, cacheConnections: %s, requestQueueSize: %s, queueNewRequests: %s";
   }

   public final void failedToProcessManagementReq(Exception e) {
      super.log.logf((String)FQCN, (Logger.Level)Logger.Level.ERROR, (Throwable)e, (String)this.failedToProcessManagementReq$str(), (Object[])());
   }

   protected String failedToProcessManagementReq$str() {
      return "UT005050: Failed to process management request";
   }

   public final void failedToSendPingResponse(Exception e) {
      super.log.logf((String)FQCN, (Logger.Level)Logger.Level.ERROR, (Throwable)e, (String)this.failedToSendPingResponse$str(), (Object[])());
   }

   protected String failedToSendPingResponse$str() {
      return "UT005051: Failed to send ping response";
   }

   public final void failedToSendPingResponseDBG(Exception e, String node, String jvmRoute) {
      super.log.logf((String)FQCN, (Logger.Level)Logger.Level.DEBUG, (Throwable)e, (String)this.failedToSendPingResponseDBG$str(), node, jvmRoute);
   }

   protected String failedToSendPingResponseDBG$str() {
      return "UT005052: Failed to send ping response, node.getJvmRoute(): %s, jvmRoute: %s";
   }

   public final void registeringNode(String jvmRoute, URI connectionURI) {
      super.log.logf((String)FQCN, (Logger.Level)Logger.Level.INFO, (Throwable)null, (String)this.registeringNode$str(), jvmRoute, connectionURI);
   }

   protected String registeringNode$str() {
      return "UT005053: Registering node %s, connection: %s";
   }

   public final void mcmpKeyValue(HttpString name, String value) {
      super.log.logf((String)FQCN, (Logger.Level)Logger.Level.DEBUG, (Throwable)null, (String)this.mcmpKeyValue$str(), name, value);
   }

   protected String mcmpKeyValue$str() {
      return "UT005054: MCMP processing, key: %s, value: %s";
   }

   public final void httpClientPingTask(URI connection) {
      super.log.logf((String)FQCN, (Logger.Level)Logger.Level.DEBUG, (Throwable)null, (String)this.httpClientPingTask$str(), (Object)connection);
   }

   protected String httpClientPingTask$str() {
      return "UT005055: HttpClientPingTask run for connection: %s";
   }

   public final void receivedNodeLoad(String jvmRoute, String loadValue) {
      super.log.logf((String)FQCN, (Logger.Level)Logger.Level.DEBUG, (Throwable)null, (String)this.receivedNodeLoad$str(), jvmRoute, loadValue);
   }

   protected String receivedNodeLoad$str() {
      return "UT005056: Received node load in STATUS message, node jvmRoute: %s, load: %s";
   }

   public final void mcmpSendingResponse(InetSocketAddress destination, int status, HeaderMap headers, String response) {
      super.log.logf((String)FQCN, (Logger.Level)Logger.Level.DEBUG, (Throwable)null, (String)this.mcmpSendingResponse$str(), (Object[])(destination, status, headers, response));
   }

   protected String mcmpSendingResponse$str() {
      return "UT005057: Sending MCMP response to destination: %s, HTTP status: %s, Headers: %s, response: %s";
   }

   public final void potentialCrossTalking(InetAddress group, String s, String localizedMessage) {
      super.log.logf(FQCN, Logger.Level.WARN, (Throwable)null, this.potentialCrossTalking$str(), group, s, localizedMessage);
   }

   protected String potentialCrossTalking$str() {
      return "UT005058: Could not bind multicast socket to %s (%s address): %s; make sure your multicast address is of the same type as the IP stack (IPv4 or IPv6). Multicast socket will not be bound to an address, but this may lead to cross talking (see http://www.jboss.org/community/docs/DOC-9469 for details).";
   }

   public final void oldStylePredicateSyntax(String string) {
      super.log.logf((String)FQCN, (Logger.Level)Logger.Level.WARN, (Throwable)null, (String)this.oldStylePredicateSyntax$str(), (Object)string);
   }

   protected String oldStylePredicateSyntax$str() {
      return "UT005060: Predicate %s uses old style square braces to define predicates, which will be removed in a future release. predicate[value] should be changed to predicate(value)";
   }

   protected String maxRestartsExceeded$str() {
      return "UT005061: More than %s restarts detected, breaking assumed infinite loop";
   }

   public final IllegalStateException maxRestartsExceeded(int maxRestarts) {
      IllegalStateException result = new IllegalStateException(String.format(this.getLoggingLocale(), this.maxRestartsExceeded$str(), maxRestarts));
      _copyStackTraceMinusOne(result);
      return result;
   }

   private static void _copyStackTraceMinusOne(Throwable e) {
      StackTraceElement[] st = e.getStackTrace();
      e.setStackTrace((StackTraceElement[])Arrays.copyOfRange(st, 1, st.length));
   }

   public final void extendedAccessLogPatternParseError(Throwable t) {
      super.log.logf(FQCN, Logger.Level.ERROR, t, this.extendedAccessLogPatternParseError$str());
   }

   protected String extendedAccessLogPatternParseError$str() {
      return "UT005062: Pattern parse error";
   }

   public final void extendedAccessLogUnknownToken(String token) {
      super.log.logf((String)FQCN, (Logger.Level)Logger.Level.ERROR, (Throwable)null, (String)this.extendedAccessLogUnknownToken$str(), (Object)token);
   }

   protected String extendedAccessLogUnknownToken$str() {
      return "UT005063: Unable to decode with rest of chars starting: %s";
   }

   public final void extendedAccessLogMissingClosing() {
      super.log.logf((String)FQCN, (Logger.Level)Logger.Level.ERROR, (Throwable)null, (String)this.extendedAccessLogMissingClosing$str(), (Object[])());
   }

   protected String extendedAccessLogMissingClosing$str() {
      return "UT005064: No closing ) found for in decode";
   }

   public final void extendedAccessLogCannotDecode(String chars) {
      super.log.logf((String)FQCN, (Logger.Level)Logger.Level.ERROR, (Throwable)null, (String)this.extendedAccessLogCannotDecode$str(), (Object)chars);
   }

   protected String extendedAccessLogCannotDecode$str() {
      return "UT005065: The next characters couldn't be decoded: %s";
   }

   public final void extendedAccessLogCannotDecodeXParamValue(String value) {
      super.log.logf((String)FQCN, (Logger.Level)Logger.Level.ERROR, (Throwable)null, (String)this.extendedAccessLogCannotDecodeXParamValue$str(), (Object)value);
   }

   protected String extendedAccessLogCannotDecodeXParamValue$str() {
      return "UT005066: X param for servlet request, couldn't decode value: %s";
   }

   public final void extendedAccessLogBadXParam() {
      super.log.logf((String)FQCN, (Logger.Level)Logger.Level.ERROR, (Throwable)null, (String)this.extendedAccessLogBadXParam$str(), (Object[])());
   }

   protected String extendedAccessLogBadXParam$str() {
      return "UT005067: X param in wrong format. Needs to be 'x-#(...)'";
   }

   public final void extendedAccessLogEmptyPattern() {
      super.log.logf((String)FQCN, (Logger.Level)Logger.Level.INFO, (Throwable)null, (String)this.extendedAccessLogEmptyPattern$str(), (Object[])());
   }

   protected String extendedAccessLogEmptyPattern$str() {
      return "UT005068: Pattern was just empty or whitespace";
   }

   public final void failedToWriteJdbcAccessLog(Exception e) {
      super.log.logf((String)FQCN, (Logger.Level)Logger.Level.ERROR, (Throwable)e, (String)this.failedToWriteJdbcAccessLog$str(), (Object[])());
   }

   protected String failedToWriteJdbcAccessLog$str() {
      return "UT005069: Failed to write JDBC access log";
   }

   public final void failedToWritePreCachedFile() {
      super.log.logf((String)FQCN, (Logger.Level)Logger.Level.ERROR, (Throwable)null, (String)this.failedToWritePreCachedFile$str(), (Object[])());
   }

   protected String failedToWritePreCachedFile$str() {
      return "UT005070: Failed to write pre-cached file";
   }

   public final void undertowRequestFailed(Throwable t, HttpServerExchange exchange) {
      super.log.logf((String)FQCN, (Logger.Level)Logger.Level.ERROR, (Throwable)t, (String)this.undertowRequestFailed$str(), (Object)exchange);
   }

   protected String undertowRequestFailed$str() {
      return "UT005071: Undertow request failed %s";
   }

   public final void stuckThreadDetected(String threadName, long threadId, long active, Date start, String requestUri, int threshold, int stuckCount, Throwable stackTrace) {
      super.log.logf(FQCN, Logger.Level.WARN, stackTrace, this.stuckThreadDetected$str(), threadName, threadId, active, start, requestUri, threshold, stuckCount);
   }

   protected String stuckThreadDetected$str() {
      return "UT005072: Thread %s (id=%s) has been active for %s milliseconds (since %s) to serve the same request for %s and may be stuck (configured threshold for this StuckThreadDetectionValve is %s seconds). There is/are %s thread(s) in total that are monitored by this Valve and may be stuck.";
   }

   public final void stuckThreadCompleted(String threadName, long threadId, long active, int stuckCount) {
      super.log.logf((String)FQCN, (Logger.Level)Logger.Level.WARN, (Throwable)null, (String)this.stuckThreadCompleted$str(), (Object[])(threadName, threadId, active, stuckCount));
   }

   protected String stuckThreadCompleted$str() {
      return "UT005073: Thread %s (id=%s) was previously reported to be stuck but has completed. It was active for approximately %s milliseconds. There is/are still %s thread(s) that are monitored by this Valve and may be stuck.";
   }

   public final void failedToInvokeFailedCallback(ServerSentEventConnection.EventCallback callback, Exception e) {
      super.log.logf((String)FQCN, (Logger.Level)Logger.Level.ERROR, (Throwable)e, (String)this.failedToInvokeFailedCallback$str(), (Object)callback);
   }

   protected String failedToInvokeFailedCallback$str() {
      return "UT005074: Failed to invoke error callback %s for SSE task";
   }

   protected String unableToResolveModClusterManagementHost$str() {
      return "UT005075: Unable to resolve mod_cluster management host's address for '%s'";
   }

   public final IllegalStateException unableToResolveModClusterManagementHost(String providedHost) {
      IllegalStateException result = new IllegalStateException(String.format(this.getLoggingLocale(), this.unableToResolveModClusterManagementHost$str(), providedHost));
      _copyStackTraceMinusOne(result);
      return result;
   }

   public final void sslReadLoopDetected(SslConduit sslConduit) {
      super.log.logf((String)FQCN, (Logger.Level)Logger.Level.ERROR, (Throwable)null, (String)this.sslReadLoopDetected$str(), (Object)sslConduit);
   }

   protected String sslReadLoopDetected$str() {
      return "UT005076: SSL read loop detected. This should not happen, please report this to the Undertow developers. Current state %s";
   }

   public final void sslBufferOverflow(SslConduit sslConduit) {
      super.log.logf((String)FQCN, (Logger.Level)Logger.Level.ERROR, (Throwable)null, (String)this.sslBufferOverflow$str(), (Object)sslConduit);
   }

   protected String sslBufferOverflow$str() {
      return "UT005077: SSL unwrap buffer overflow detected. This should not happen, please report this to the Undertow developers. Current state %s";
   }

   public final void alpnConnectionFailed(SslConnection connection) {
      super.log.logf((String)FQCN, (Logger.Level)Logger.Level.ERROR, (Throwable)null, (String)this.alpnConnectionFailed$str(), (Object)connection);
   }

   protected String alpnConnectionFailed$str() {
      return "UT005079: ALPN negotiation on %s failed";
   }

   public final void resumedAndDispatched() {
      super.log.logf((String)FQCN, (Logger.Level)Logger.Level.ERROR, (Throwable)null, (String)this.resumedAndDispatched$str(), (Object[])());
   }

   protected String resumedAndDispatched$str() {
      return "UT005080: HttpServerExchange cannot have both async IO resumed and dispatch() called in the same cycle";
   }

   public final void cannotProxyStartedRequest(HttpServerExchange exchange) {
      super.log.logf((String)FQCN, (Logger.Level)Logger.Level.ERROR, (Throwable)null, (String)this.cannotProxyStartedRequest$str(), (Object)exchange);
   }

   protected String cannotProxyStartedRequest$str() {
      return "UT005081: Response has already been started, cannot proxy request %s";
   }

   protected String cannotUseWildcardAddressAsModClusterManagementHost$str() {
      return "UT005082: Configured mod_cluster management host address cannot be a wildcard address (%s)!";
   }

   public final IllegalArgumentException cannotUseWildcardAddressAsModClusterManagementHost(String providedAddress) {
      IllegalArgumentException result = new IllegalArgumentException(String.format(this.getLoggingLocale(), this.cannotUseWildcardAddressAsModClusterManagementHost$str(), providedAddress));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String unexpectedEndOfCompressedInput$str() {
      return "UT005083: Unexpected end of compressed input";
   }

   public final IOException unexpectedEndOfCompressedInput() {
      IOException result = new IOException(String.format(this.getLoggingLocale(), this.unexpectedEndOfCompressedInput$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String dataLargerThanContentLength$str() {
      return "UT005084: Attempted to write %s bytes however content-length has been set to %s";
   }

   public final IOException dataLargerThanContentLength(long totalToWrite, long responseContentLength) {
      IOException result = new IOException(String.format(this.getLoggingLocale(), this.dataLargerThanContentLength$str(), totalToWrite, responseContentLength));
      _copyStackTraceMinusOne(result);
      return result;
   }

   public final void responseWasNotTerminated(ServerConnection connection, HttpServerExchange exchange) {
      super.log.logf((String)FQCN, (Logger.Level)Logger.Level.DEBUG, (Throwable)null, (String)this.responseWasNotTerminated$str(), connection, exchange);
   }

   protected String responseWasNotTerminated$str() {
      return "UT005085: Connection %s for exchange %s was not closed cleanly, forcibly closing connection";
   }

   public final void failedToAcceptSSLRequest(Exception e) {
      super.log.logf((String)FQCN, (Logger.Level)Logger.Level.ERROR, (Throwable)e, (String)this.failedToAcceptSSLRequest$str(), (Object[])());
   }

   protected String failedToAcceptSSLRequest$str() {
      return "UT005086: Failed to accept SSL request";
   }

   public final void closeAsyncFailed(IOException e) {
      super.log.logf((String)FQCN, (Logger.Level)Logger.Level.ERROR, (Throwable)e, (String)this.closeAsyncFailed$str(), (Object[])());
   }

   protected String closeAsyncFailed$str() {
      return "UT005088: Failed to execute ServletOutputStream.closeAsync() on IO thread";
   }

   protected String nullParameter$str() {
      return "UT005089: Method parameter '%s' cannot be null";
   }

   public final IllegalArgumentException nullParameter(String name) {
      IllegalArgumentException result = new IllegalArgumentException(String.format(this.getLoggingLocale(), this.nullParameter$str(), name));
      _copyStackTraceMinusOne(result);
      return result;
   }

   public final void handleUnexpectedFailure(Throwable t) {
      super.log.logf(FQCN, Logger.Level.ERROR, t, this.handleUnexpectedFailure$str());
   }

   protected String handleUnexpectedFailure$str() {
      return "UT005090: Unexpected failure";
   }

   public final void directBufferDeallocatorInitializationFailed(Throwable t) {
      super.log.logf(FQCN, Logger.Level.ERROR, t, this.directBufferDeallocatorInitializationFailed$str());
   }

   protected String directBufferDeallocatorInitializationFailed$str() {
      return "UT005091: Failed to initialize DirectByteBufferDeallocator";
   }

   public final void directBufferDeallocationFailed(Throwable t) {
      super.log.logf(FQCN, Logger.Level.DEBUG, t, this.directBufferDeallocationFailed$str());
   }

   protected String directBufferDeallocationFailed$str() {
      return "UT005092: Failed to free direct buffer";
   }

   public final void blockingReadTimedOut(ReadTimeoutException rte) {
      super.log.logf((String)FQCN, (Logger.Level)Logger.Level.DEBUG, (Throwable)rte, (String)this.blockingReadTimedOut$str(), (Object[])());
   }

   protected String blockingReadTimedOut$str() {
      return "UT005093: Blocking read timed out";
   }

   public final void blockingWriteTimedOut(WriteTimeoutException rte) {
      super.log.logf((String)FQCN, (Logger.Level)Logger.Level.DEBUG, (Throwable)rte, (String)this.blockingWriteTimedOut$str(), (Object[])());
   }

   protected String blockingWriteTimedOut$str() {
      return "UT005094: Blocking write timed out";
   }

   public final void sslEngineDelegatedTaskRejected(RejectedExecutionException ree) {
      super.log.logf((String)FQCN, (Logger.Level)Logger.Level.DEBUG, (Throwable)ree, (String)this.sslEngineDelegatedTaskRejected$str(), (Object[])());
   }

   protected String sslEngineDelegatedTaskRejected$str() {
      return "UT005095: SSLEngine delegated task was rejected";
   }

   public final void authenticationFailedFor(String header, HttpServerExchange exchange, Exception e) {
      super.log.logf((String)FQCN, (Logger.Level)Logger.Level.DEBUG, (Throwable)e, (String)this.authenticationFailedFor$str(), header, exchange);
   }

   protected String authenticationFailedFor$str() {
      return "UT005096: Authentication failed for digest header %s in %s";
   }

   public final void failedToObtainSubject(HttpServerExchange exchange, GeneralSecurityException e) {
      super.log.logf((String)FQCN, (Logger.Level)Logger.Level.DEBUG, (Throwable)e, (String)this.failedToObtainSubject$str(), (Object)exchange);
   }

   protected String failedToObtainSubject$str() {
      return "UT005097: Failed to obtain subject for %s";
   }

   public final void failedToNegotiateAtGSSAPI(HttpServerExchange exchange, Throwable e) {
      super.log.logf((String)FQCN, (Logger.Level)Logger.Level.DEBUG, (Throwable)e, (String)this.failedToNegotiateAtGSSAPI$str(), (Object)exchange);
   }

   protected String failedToNegotiateAtGSSAPI$str() {
      return "UT005098: GSSAPI negotiation failed for %s";
   }

   static {
      LOCALE = Locale.ROOT;
   }
}
