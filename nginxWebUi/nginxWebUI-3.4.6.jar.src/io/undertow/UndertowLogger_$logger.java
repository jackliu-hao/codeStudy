/*     */ package io.undertow;
/*     */ 
/*     */ import io.undertow.protocols.ssl.SslConduit;
/*     */ import io.undertow.server.HttpServerExchange;
/*     */ import io.undertow.server.ServerConnection;
/*     */ import io.undertow.server.handlers.sse.ServerSentEventConnection;
/*     */ import io.undertow.util.HeaderMap;
/*     */ import io.undertow.util.HttpString;
/*     */ import java.io.IOException;
/*     */ import java.io.Serializable;
/*     */ import java.net.InetAddress;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.net.SocketAddress;
/*     */ import java.net.URI;
/*     */ import java.nio.file.Path;
/*     */ import java.security.GeneralSecurityException;
/*     */ import java.util.Arrays;
/*     */ import java.util.Date;
/*     */ import java.util.List;
/*     */ import java.util.Locale;
/*     */ import java.util.concurrent.RejectedExecutionException;
/*     */ import org.jboss.logging.BasicLogger;
/*     */ import org.jboss.logging.DelegatingBasicLogger;
/*     */ import org.jboss.logging.Logger;
/*     */ import org.xnio.channels.ReadTimeoutException;
/*     */ import org.xnio.channels.WriteTimeoutException;
/*     */ import org.xnio.ssl.SslConnection;
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
/*     */ public class UndertowLogger_$logger
/*     */   extends DelegatingBasicLogger
/*     */   implements UndertowLogger, BasicLogger, Serializable
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*  48 */   private static final String FQCN = UndertowLogger_$logger.class.getName();
/*     */   public UndertowLogger_$logger(Logger log) {
/*  50 */     super(log);
/*     */   }
/*  52 */   private static final Locale LOCALE = Locale.ROOT;
/*     */   protected Locale getLoggingLocale() {
/*  54 */     return LOCALE;
/*     */   }
/*     */   
/*     */   public final void exceptionProcessingRequest(Throwable cause) {
/*  58 */     this.log.logf(FQCN, Logger.Level.ERROR, cause, exceptionProcessingRequest$str(), new Object[0]);
/*     */   }
/*     */   protected String exceptionProcessingRequest$str() {
/*  61 */     return "UT005001: An exception occurred processing the request";
/*     */   }
/*     */   
/*     */   public final void ioExceptionReadingFromChannel(IOException e) {
/*  65 */     this.log.logf(FQCN, Logger.Level.ERROR, e, ioExceptionReadingFromChannel$str(), new Object[0]);
/*     */   }
/*     */   protected String ioExceptionReadingFromChannel$str() {
/*  68 */     return "UT005003: IOException reading from channel";
/*     */   }
/*     */   
/*     */   public final void cannotRemoveUploadedFile(Path file) {
/*  72 */     this.log.logf(FQCN, Logger.Level.ERROR, null, cannotRemoveUploadedFile$str(), file);
/*     */   }
/*     */   protected String cannotRemoveUploadedFile$str() {
/*  75 */     return "UT005005: Cannot remove uploaded file %s";
/*     */   }
/*     */   
/*     */   public final void requestHeaderWasTooLarge(SocketAddress address, int size) {
/*  79 */     this.log.logf(FQCN, Logger.Level.DEBUG, null, requestHeaderWasTooLarge$str(), address, Integer.valueOf(size));
/*     */   }
/*     */   protected String requestHeaderWasTooLarge$str() {
/*  82 */     return "UT005006: Connection from %s terminated as request header was larger than %s";
/*     */   }
/*     */   
/*     */   public final void requestWasNotFullyConsumed() {
/*  86 */     this.log.logf(FQCN, Logger.Level.DEBUG, null, requestWasNotFullyConsumed$str(), new Object[0]);
/*     */   }
/*     */   protected String requestWasNotFullyConsumed$str() {
/*  89 */     return "UT005007: Request was not fully consumed";
/*     */   }
/*     */   
/*     */   public final void invalidTokenReceived(String tokenName, String tokenValue) {
/*  93 */     this.log.logf(FQCN, Logger.Level.DEBUG, null, invalidTokenReceived$str(), tokenName, tokenValue);
/*     */   }
/*     */   protected String invalidTokenReceived$str() {
/*  96 */     return "UT005008: An invalid token '%s' with value '%s' has been received.";
/*     */   }
/*     */   
/*     */   public final void missingAuthorizationToken(String tokenName) {
/* 100 */     this.log.logf(FQCN, Logger.Level.DEBUG, null, missingAuthorizationToken$str(), tokenName);
/*     */   }
/*     */   protected String missingAuthorizationToken$str() {
/* 103 */     return "UT005009: A mandatory token %s is missing from the request.";
/*     */   }
/*     */   
/*     */   public final void authenticationFailed(String userName, String mechanism) {
/* 107 */     this.log.logf(FQCN, Logger.Level.DEBUG, null, authenticationFailed$str(), userName, mechanism);
/*     */   }
/*     */   protected String authenticationFailed$str() {
/* 110 */     return "UT005010: Verification of authentication tokens for user '%s' has failed using mechanism '%s'.";
/*     */   }
/*     */   
/*     */   public final void ignoringAjpRequestWithPrefixCode(byte prefix) {
/* 114 */     this.log.logf(FQCN, Logger.Level.ERROR, null, ignoringAjpRequestWithPrefixCode$str(), Byte.valueOf(prefix));
/*     */   }
/*     */   protected String ignoringAjpRequestWithPrefixCode$str() {
/* 117 */     return "UT005011: Ignoring AJP request with prefix %s";
/*     */   }
/*     */   
/*     */   public final void ioException(IOException e) {
/* 121 */     this.log.logf(FQCN, Logger.Level.DEBUG, e, ioException$str(), new Object[0]);
/*     */   }
/*     */   protected String ioException$str() {
/* 124 */     return "UT005013: An IOException occurred";
/*     */   }
/*     */   
/*     */   public final void failedToParseRequest(Throwable e) {
/* 128 */     this.log.logf(FQCN, Logger.Level.DEBUG, e, failedToParseRequest$str(), new Object[0]);
/*     */   }
/*     */   protected String failedToParseRequest$str() {
/* 131 */     return "UT005014: Failed to parse request";
/*     */   }
/*     */   
/*     */   public final void errorRotatingAccessLog(IOException e) {
/* 135 */     this.log.logf(FQCN, Logger.Level.ERROR, e, errorRotatingAccessLog$str(), new Object[0]);
/*     */   }
/*     */   protected String errorRotatingAccessLog$str() {
/* 138 */     return "UT005015: Error rotating access log";
/*     */   }
/*     */   
/*     */   public final void errorWritingAccessLog(IOException e) {
/* 142 */     this.log.logf(FQCN, Logger.Level.ERROR, e, errorWritingAccessLog$str(), new Object[0]);
/*     */   }
/*     */   protected String errorWritingAccessLog$str() {
/* 145 */     return "UT005016: Error writing access log";
/*     */   }
/*     */   
/*     */   public final void unknownVariable(String token) {
/* 149 */     this.log.logf(FQCN, Logger.Level.ERROR, null, unknownVariable$str(), token);
/*     */   }
/*     */   protected String unknownVariable$str() {
/* 152 */     return "UT005017: Unknown variable %s. For the literal percent character use two percent characters: '%%'";
/*     */   }
/*     */   
/*     */   public final void exceptionInvokingCloseListener(ServerConnection.CloseListener l, Throwable e) {
/* 156 */     this.log.logf(FQCN, Logger.Level.ERROR, e, exceptionInvokingCloseListener$str(), l);
/*     */   }
/*     */   protected String exceptionInvokingCloseListener$str() {
/* 159 */     return "UT005018: Exception invoking close listener %s";
/*     */   }
/*     */   
/*     */   public final void errorWritingJDBCLog(Exception e) {
/* 163 */     this.log.logf(FQCN, Logger.Level.ERROR, e, errorWritingJDBCLog$str(), new Object[0]);
/*     */   }
/*     */   protected String errorWritingJDBCLog$str() {
/* 166 */     return "UT005020: Error writing JDBC log";
/*     */   }
/*     */   
/*     */   public final void exceptionGeneratingErrorPage(Exception e, String location) {
/* 170 */     this.log.logf(FQCN, Logger.Level.ERROR, e, exceptionGeneratingErrorPage$str(), location);
/*     */   }
/*     */   protected String exceptionGeneratingErrorPage$str() {
/* 173 */     return "UT005022: Exception generating error page %s";
/*     */   }
/*     */   
/*     */   public final void exceptionHandlingRequest(Throwable t, String requestURI) {
/* 177 */     this.log.logf(FQCN, Logger.Level.ERROR, t, exceptionHandlingRequest$str(), requestURI);
/*     */   }
/*     */   protected String exceptionHandlingRequest$str() {
/* 180 */     return "UT005023: Exception handling request to %s";
/*     */   }
/*     */   
/*     */   public final void couldNotRegisterChangeListener(Exception e) {
/* 184 */     this.log.logf(FQCN, Logger.Level.ERROR, e, couldNotRegisterChangeListener$str(), new Object[0]);
/*     */   }
/*     */   protected String couldNotRegisterChangeListener$str() {
/* 187 */     return "UT005024: Could not register resource change listener for caching resource manager, automatic invalidation of cached resource will not work";
/*     */   }
/*     */   
/*     */   public final void timingOutRequest(String requestURI) {
/* 191 */     this.log.logf(FQCN, Logger.Level.ERROR, null, timingOutRequest$str(), requestURI);
/*     */   }
/*     */   protected String timingOutRequest$str() {
/* 194 */     return "UT005027: Timing out request to %s";
/*     */   }
/*     */   
/*     */   public final void proxyRequestFailed(String requestURI, Exception e) {
/* 198 */     this.log.logf(FQCN, Logger.Level.ERROR, e, proxyRequestFailed$str(), requestURI);
/*     */   }
/*     */   protected String proxyRequestFailed$str() {
/* 201 */     return "UT005028: Proxy request to %s failed";
/*     */   }
/*     */   
/*     */   public final void proxyFailedToConnectToBackend(String requestURI, URI uri) {
/* 205 */     this.log.logf(FQCN, Logger.Level.ERROR, null, proxyFailedToConnectToBackend$str(), requestURI, uri);
/*     */   }
/*     */   protected String proxyFailedToConnectToBackend$str() {
/* 208 */     return "UT005031: Proxy request to %s could not connect to backend server %s";
/*     */   }
/*     */   
/*     */   public final void listenerNotProgressing() {
/* 212 */     this.log.logf(FQCN, Logger.Level.ERROR, null, listenerNotProgressing$str(), new Object[0]);
/*     */   }
/*     */   protected String listenerNotProgressing$str() {
/* 215 */     return "UT005032: Listener not making progress on framed channel, closing channel to prevent infinite loop";
/*     */   }
/*     */   
/*     */   public final void remoteEndpointFailedToSendInitialSettings(int type) {
/* 219 */     this.log.logf(FQCN, Logger.Level.ERROR, null, remoteEndpointFailedToSendInitialSettings$str(), Integer.valueOf(type));
/*     */   }
/*     */   protected String remoteEndpointFailedToSendInitialSettings$str() {
/* 222 */     return "UT005034: Remote endpoint failed to send initial settings frame in HTTP2 connection, frame type %s";
/*     */   }
/*     */   
/*     */   public final void parseRequestTimedOut(SocketAddress remoteAddress) {
/* 226 */     this.log.logf(FQCN, Logger.Level.DEBUG, null, parseRequestTimedOut$str(), remoteAddress);
/*     */   }
/*     */   protected String parseRequestTimedOut$str() {
/* 229 */     return "UT005035: Closing channel because of parse timeout for remote address %s";
/*     */   }
/*     */   
/*     */   public final void noALPNFallback(SocketAddress address) {
/* 233 */     this.log.logf(FQCN, Logger.Level.ERROR, null, noALPNFallback$str(), address);
/*     */   }
/*     */   protected String noALPNFallback$str() {
/* 236 */     return "UT005036: ALPN negotiation failed for %s and no fallback defined, closing connection";
/*     */   }
/*     */   
/*     */   public final void stickySessionCookieLengthTruncated(String original, String current) {
/* 240 */     this.log.logf(FQCN, Logger.Level.WARN, null, stickySessionCookieLengthTruncated$str(), original, current);
/*     */   }
/*     */   protected String stickySessionCookieLengthTruncated$str() {
/* 243 */     return "UT005037: Name of the cookie containing the session id, %s, had been too long and was truncated to: %s";
/*     */   }
/*     */   
/*     */   public final void balancerCreated(int id, String name, boolean stickySession, String stickySessionCookie, String stickySessionPath, boolean stickySessionRemove, boolean stickySessionForce, int waitWorker, int maxattempts) {
/* 247 */     this.log.logf(FQCN, Logger.Level.DEBUG, null, balancerCreated$str(), new Object[] { Integer.valueOf(id), name, Boolean.valueOf(stickySession), stickySessionCookie, stickySessionPath, Boolean.valueOf(stickySessionRemove), Boolean.valueOf(stickySessionForce), Integer.valueOf(waitWorker), Integer.valueOf(maxattempts) });
/*     */   }
/*     */   protected String balancerCreated$str() {
/* 250 */     return "UT005038: Balancer created: id: %s, name: %s, stickySession: %s, stickySessionCookie: %s, stickySessionPath: %s, stickySessionRemove: %s, stickySessionForce: %s, waitWorker: %s, maxattempts: %s";
/*     */   }
/*     */   
/*     */   public final void proxyAdvertisementsStarted(String address, int frequency) {
/* 254 */     this.log.logf(FQCN, Logger.Level.INFO, null, proxyAdvertisementsStarted$str(), address, Integer.valueOf(frequency));
/*     */   }
/*     */   protected String proxyAdvertisementsStarted$str() {
/* 257 */     return "UT005039: Undertow starts mod_cluster proxy advertisements on %s with frequency %s ms";
/*     */   }
/*     */   
/*     */   public final void proxyAdvertiseMessagePayload(String payload) {
/* 261 */     this.log.logf(FQCN, Logger.Level.DEBUG, null, proxyAdvertiseMessagePayload$str(), payload);
/*     */   }
/*     */   protected String proxyAdvertiseMessagePayload$str() {
/* 264 */     return "UT005040: Gonna send payload:\n%s";
/*     */   }
/*     */   
/*     */   public final void proxyAdvertiseCannotSendMessage(Exception e, InetSocketAddress address) {
/* 268 */     this.log.logf(FQCN, Logger.Level.ERROR, e, proxyAdvertiseCannotSendMessage$str(), address);
/*     */   }
/*     */   protected String proxyAdvertiseCannotSendMessage$str() {
/* 271 */     return "UT005041: Cannot send advertise message. Address: %s";
/*     */   }
/*     */   
/*     */   public final void mcmpHandlerCreated() {
/* 275 */     this.log.logf(FQCN, Logger.Level.DEBUG, null, mcmpHandlerCreated$str(), new Object[0]);
/*     */   }
/*     */   protected String mcmpHandlerCreated$str() {
/* 278 */     return "UT005042: Undertow mod_cluster proxy MCMPHandler created";
/*     */   }
/*     */   
/*     */   public final void mcmpProcessingError(String type, String errString) {
/* 282 */     this.log.logf(FQCN, Logger.Level.ERROR, null, mcmpProcessingError$str(), type, errString);
/*     */   }
/*     */   protected String mcmpProcessingError$str() {
/* 285 */     return "UT005043: Error in processing MCMP commands: Type:%s, Mess: %s";
/*     */   }
/*     */   
/*     */   public final void removingNode(String jvmRoute) {
/* 289 */     this.log.logf(FQCN, Logger.Level.INFO, null, removingNode$str(), jvmRoute);
/*     */   }
/*     */   protected String removingNode$str() {
/* 292 */     return "UT005044: Removing node %s";
/*     */   }
/*     */   
/*     */   public final void registeringContext(String contextPath, String jvmRoute) {
/* 296 */     this.log.logf(FQCN, Logger.Level.INFO, null, registeringContext2$str(), contextPath, jvmRoute);
/*     */   }
/*     */   protected String registeringContext2$str() {
/* 299 */     return "UT005045: Registering context %s, for node %s";
/*     */   }
/*     */   
/*     */   public final void registeringContext(String contextPath, String jvmRoute, List<String> aliases) {
/* 303 */     this.log.logf(FQCN, Logger.Level.DEBUG, null, registeringContext3$str(), contextPath, jvmRoute, aliases);
/*     */   }
/*     */   protected String registeringContext3$str() {
/* 306 */     return "UT005046: Registering context %s, for node %s, with aliases %s";
/*     */   }
/*     */   
/*     */   public final void unregisteringContext(String contextPath, String jvmRoute) {
/* 310 */     this.log.logf(FQCN, Logger.Level.INFO, null, unregisteringContext$str(), contextPath, jvmRoute);
/*     */   }
/*     */   protected String unregisteringContext$str() {
/* 313 */     return "UT005047: Unregistering context %s, from node %s";
/*     */   }
/*     */   
/*     */   public final void nodeIsInError(String jvmRoute) {
/* 317 */     this.log.logf(FQCN, Logger.Level.DEBUG, null, nodeIsInError$str(), jvmRoute);
/*     */   }
/*     */   protected String nodeIsInError$str() {
/* 320 */     return "UT005048: Node %s in error";
/*     */   }
/*     */   
/*     */   public final void nodeConfigCreated(URI connectionURI, String balancer, String domain, String jvmRoute, boolean flushPackets, int flushwait, int ping, long ttl, int timeout, int maxConnections, int cacheConnections, int requestQueueSize, boolean queueNewRequests) {
/* 324 */     this.log.logf(FQCN, Logger.Level.DEBUG, null, nodeConfigCreated$str(), new Object[] { connectionURI, balancer, domain, jvmRoute, Boolean.valueOf(flushPackets), Integer.valueOf(flushwait), Integer.valueOf(ping), Long.valueOf(ttl), Integer.valueOf(timeout), Integer.valueOf(maxConnections), Integer.valueOf(cacheConnections), Integer.valueOf(requestQueueSize), Boolean.valueOf(queueNewRequests) });
/*     */   }
/*     */   protected String nodeConfigCreated$str() {
/* 327 */     return "UT005049: NodeConfig created: connectionURI: %s, balancer: %s, load balancing group: %s, jvmRoute: %s, flushPackets: %s, flushwait: %s, ping: %s,ttl: %s, timeout: %s, maxConnections: %s, cacheConnections: %s, requestQueueSize: %s, queueNewRequests: %s";
/*     */   }
/*     */   
/*     */   public final void failedToProcessManagementReq(Exception e) {
/* 331 */     this.log.logf(FQCN, Logger.Level.ERROR, e, failedToProcessManagementReq$str(), new Object[0]);
/*     */   }
/*     */   protected String failedToProcessManagementReq$str() {
/* 334 */     return "UT005050: Failed to process management request";
/*     */   }
/*     */   
/*     */   public final void failedToSendPingResponse(Exception e) {
/* 338 */     this.log.logf(FQCN, Logger.Level.ERROR, e, failedToSendPingResponse$str(), new Object[0]);
/*     */   }
/*     */   protected String failedToSendPingResponse$str() {
/* 341 */     return "UT005051: Failed to send ping response";
/*     */   }
/*     */   
/*     */   public final void failedToSendPingResponseDBG(Exception e, String node, String jvmRoute) {
/* 345 */     this.log.logf(FQCN, Logger.Level.DEBUG, e, failedToSendPingResponseDBG$str(), node, jvmRoute);
/*     */   }
/*     */   protected String failedToSendPingResponseDBG$str() {
/* 348 */     return "UT005052: Failed to send ping response, node.getJvmRoute(): %s, jvmRoute: %s";
/*     */   }
/*     */   
/*     */   public final void registeringNode(String jvmRoute, URI connectionURI) {
/* 352 */     this.log.logf(FQCN, Logger.Level.INFO, null, registeringNode$str(), jvmRoute, connectionURI);
/*     */   }
/*     */   protected String registeringNode$str() {
/* 355 */     return "UT005053: Registering node %s, connection: %s";
/*     */   }
/*     */   
/*     */   public final void mcmpKeyValue(HttpString name, String value) {
/* 359 */     this.log.logf(FQCN, Logger.Level.DEBUG, null, mcmpKeyValue$str(), name, value);
/*     */   }
/*     */   protected String mcmpKeyValue$str() {
/* 362 */     return "UT005054: MCMP processing, key: %s, value: %s";
/*     */   }
/*     */   
/*     */   public final void httpClientPingTask(URI connection) {
/* 366 */     this.log.logf(FQCN, Logger.Level.DEBUG, null, httpClientPingTask$str(), connection);
/*     */   }
/*     */   protected String httpClientPingTask$str() {
/* 369 */     return "UT005055: HttpClientPingTask run for connection: %s";
/*     */   }
/*     */   
/*     */   public final void receivedNodeLoad(String jvmRoute, String loadValue) {
/* 373 */     this.log.logf(FQCN, Logger.Level.DEBUG, null, receivedNodeLoad$str(), jvmRoute, loadValue);
/*     */   }
/*     */   protected String receivedNodeLoad$str() {
/* 376 */     return "UT005056: Received node load in STATUS message, node jvmRoute: %s, load: %s";
/*     */   }
/*     */   
/*     */   public final void mcmpSendingResponse(InetSocketAddress destination, int status, HeaderMap headers, String response) {
/* 380 */     this.log.logf(FQCN, Logger.Level.DEBUG, null, mcmpSendingResponse$str(), new Object[] { destination, Integer.valueOf(status), headers, response });
/*     */   }
/*     */   protected String mcmpSendingResponse$str() {
/* 383 */     return "UT005057: Sending MCMP response to destination: %s, HTTP status: %s, Headers: %s, response: %s";
/*     */   }
/*     */   
/*     */   public final void potentialCrossTalking(InetAddress group, String s, String localizedMessage) {
/* 387 */     this.log.logf(FQCN, Logger.Level.WARN, null, potentialCrossTalking$str(), group, s, localizedMessage);
/*     */   }
/*     */   protected String potentialCrossTalking$str() {
/* 390 */     return "UT005058: Could not bind multicast socket to %s (%s address): %s; make sure your multicast address is of the same type as the IP stack (IPv4 or IPv6). Multicast socket will not be bound to an address, but this may lead to cross talking (see http://www.jboss.org/community/docs/DOC-9469 for details).";
/*     */   }
/*     */   
/*     */   public final void oldStylePredicateSyntax(String string) {
/* 394 */     this.log.logf(FQCN, Logger.Level.WARN, null, oldStylePredicateSyntax$str(), string);
/*     */   }
/*     */   protected String oldStylePredicateSyntax$str() {
/* 397 */     return "UT005060: Predicate %s uses old style square braces to define predicates, which will be removed in a future release. predicate[value] should be changed to predicate(value)";
/*     */   }
/*     */   protected String maxRestartsExceeded$str() {
/* 400 */     return "UT005061: More than %s restarts detected, breaking assumed infinite loop";
/*     */   }
/*     */   
/*     */   public final IllegalStateException maxRestartsExceeded(int maxRestarts) {
/* 404 */     IllegalStateException result = new IllegalStateException(String.format(getLoggingLocale(), maxRestartsExceeded$str(), new Object[] { Integer.valueOf(maxRestarts) }));
/* 405 */     _copyStackTraceMinusOne(result);
/* 406 */     return result;
/*     */   }
/*     */   private static void _copyStackTraceMinusOne(Throwable e) {
/* 409 */     StackTraceElement[] st = e.getStackTrace();
/* 410 */     e.setStackTrace(Arrays.<StackTraceElement>copyOfRange(st, 1, st.length));
/*     */   }
/*     */   
/*     */   public final void extendedAccessLogPatternParseError(Throwable t) {
/* 414 */     this.log.logf(FQCN, Logger.Level.ERROR, t, extendedAccessLogPatternParseError$str(), new Object[0]);
/*     */   }
/*     */   protected String extendedAccessLogPatternParseError$str() {
/* 417 */     return "UT005062: Pattern parse error";
/*     */   }
/*     */   
/*     */   public final void extendedAccessLogUnknownToken(String token) {
/* 421 */     this.log.logf(FQCN, Logger.Level.ERROR, null, extendedAccessLogUnknownToken$str(), token);
/*     */   }
/*     */   protected String extendedAccessLogUnknownToken$str() {
/* 424 */     return "UT005063: Unable to decode with rest of chars starting: %s";
/*     */   }
/*     */   
/*     */   public final void extendedAccessLogMissingClosing() {
/* 428 */     this.log.logf(FQCN, Logger.Level.ERROR, null, extendedAccessLogMissingClosing$str(), new Object[0]);
/*     */   }
/*     */   protected String extendedAccessLogMissingClosing$str() {
/* 431 */     return "UT005064: No closing ) found for in decode";
/*     */   }
/*     */   
/*     */   public final void extendedAccessLogCannotDecode(String chars) {
/* 435 */     this.log.logf(FQCN, Logger.Level.ERROR, null, extendedAccessLogCannotDecode$str(), chars);
/*     */   }
/*     */   protected String extendedAccessLogCannotDecode$str() {
/* 438 */     return "UT005065: The next characters couldn't be decoded: %s";
/*     */   }
/*     */   
/*     */   public final void extendedAccessLogCannotDecodeXParamValue(String value) {
/* 442 */     this.log.logf(FQCN, Logger.Level.ERROR, null, extendedAccessLogCannotDecodeXParamValue$str(), value);
/*     */   }
/*     */   protected String extendedAccessLogCannotDecodeXParamValue$str() {
/* 445 */     return "UT005066: X param for servlet request, couldn't decode value: %s";
/*     */   }
/*     */   
/*     */   public final void extendedAccessLogBadXParam() {
/* 449 */     this.log.logf(FQCN, Logger.Level.ERROR, null, extendedAccessLogBadXParam$str(), new Object[0]);
/*     */   }
/*     */   protected String extendedAccessLogBadXParam$str() {
/* 452 */     return "UT005067: X param in wrong format. Needs to be 'x-#(...)'";
/*     */   }
/*     */   
/*     */   public final void extendedAccessLogEmptyPattern() {
/* 456 */     this.log.logf(FQCN, Logger.Level.INFO, null, extendedAccessLogEmptyPattern$str(), new Object[0]);
/*     */   }
/*     */   protected String extendedAccessLogEmptyPattern$str() {
/* 459 */     return "UT005068: Pattern was just empty or whitespace";
/*     */   }
/*     */   
/*     */   public final void failedToWriteJdbcAccessLog(Exception e) {
/* 463 */     this.log.logf(FQCN, Logger.Level.ERROR, e, failedToWriteJdbcAccessLog$str(), new Object[0]);
/*     */   }
/*     */   protected String failedToWriteJdbcAccessLog$str() {
/* 466 */     return "UT005069: Failed to write JDBC access log";
/*     */   }
/*     */   
/*     */   public final void failedToWritePreCachedFile() {
/* 470 */     this.log.logf(FQCN, Logger.Level.ERROR, null, failedToWritePreCachedFile$str(), new Object[0]);
/*     */   }
/*     */   protected String failedToWritePreCachedFile$str() {
/* 473 */     return "UT005070: Failed to write pre-cached file";
/*     */   }
/*     */   
/*     */   public final void undertowRequestFailed(Throwable t, HttpServerExchange exchange) {
/* 477 */     this.log.logf(FQCN, Logger.Level.ERROR, t, undertowRequestFailed$str(), exchange);
/*     */   }
/*     */   protected String undertowRequestFailed$str() {
/* 480 */     return "UT005071: Undertow request failed %s";
/*     */   }
/*     */   
/*     */   public final void stuckThreadDetected(String threadName, long threadId, long active, Date start, String requestUri, int threshold, int stuckCount, Throwable stackTrace) {
/* 484 */     this.log.logf(FQCN, Logger.Level.WARN, stackTrace, stuckThreadDetected$str(), new Object[] { threadName, Long.valueOf(threadId), Long.valueOf(active), start, requestUri, Integer.valueOf(threshold), Integer.valueOf(stuckCount) });
/*     */   }
/*     */   protected String stuckThreadDetected$str() {
/* 487 */     return "UT005072: Thread %s (id=%s) has been active for %s milliseconds (since %s) to serve the same request for %s and may be stuck (configured threshold for this StuckThreadDetectionValve is %s seconds). There is/are %s thread(s) in total that are monitored by this Valve and may be stuck.";
/*     */   }
/*     */   
/*     */   public final void stuckThreadCompleted(String threadName, long threadId, long active, int stuckCount) {
/* 491 */     this.log.logf(FQCN, Logger.Level.WARN, null, stuckThreadCompleted$str(), new Object[] { threadName, Long.valueOf(threadId), Long.valueOf(active), Integer.valueOf(stuckCount) });
/*     */   }
/*     */   protected String stuckThreadCompleted$str() {
/* 494 */     return "UT005073: Thread %s (id=%s) was previously reported to be stuck but has completed. It was active for approximately %s milliseconds. There is/are still %s thread(s) that are monitored by this Valve and may be stuck.";
/*     */   }
/*     */   
/*     */   public final void failedToInvokeFailedCallback(ServerSentEventConnection.EventCallback callback, Exception e) {
/* 498 */     this.log.logf(FQCN, Logger.Level.ERROR, e, failedToInvokeFailedCallback$str(), callback);
/*     */   }
/*     */   protected String failedToInvokeFailedCallback$str() {
/* 501 */     return "UT005074: Failed to invoke error callback %s for SSE task";
/*     */   }
/*     */   protected String unableToResolveModClusterManagementHost$str() {
/* 504 */     return "UT005075: Unable to resolve mod_cluster management host's address for '%s'";
/*     */   }
/*     */   
/*     */   public final IllegalStateException unableToResolveModClusterManagementHost(String providedHost) {
/* 508 */     IllegalStateException result = new IllegalStateException(String.format(getLoggingLocale(), unableToResolveModClusterManagementHost$str(), new Object[] { providedHost }));
/* 509 */     _copyStackTraceMinusOne(result);
/* 510 */     return result;
/*     */   }
/*     */   
/*     */   public final void sslReadLoopDetected(SslConduit sslConduit) {
/* 514 */     this.log.logf(FQCN, Logger.Level.ERROR, null, sslReadLoopDetected$str(), sslConduit);
/*     */   }
/*     */   protected String sslReadLoopDetected$str() {
/* 517 */     return "UT005076: SSL read loop detected. This should not happen, please report this to the Undertow developers. Current state %s";
/*     */   }
/*     */   
/*     */   public final void sslBufferOverflow(SslConduit sslConduit) {
/* 521 */     this.log.logf(FQCN, Logger.Level.ERROR, null, sslBufferOverflow$str(), sslConduit);
/*     */   }
/*     */   protected String sslBufferOverflow$str() {
/* 524 */     return "UT005077: SSL unwrap buffer overflow detected. This should not happen, please report this to the Undertow developers. Current state %s";
/*     */   }
/*     */   
/*     */   public final void alpnConnectionFailed(SslConnection connection) {
/* 528 */     this.log.logf(FQCN, Logger.Level.ERROR, null, alpnConnectionFailed$str(), connection);
/*     */   }
/*     */   protected String alpnConnectionFailed$str() {
/* 531 */     return "UT005079: ALPN negotiation on %s failed";
/*     */   }
/*     */   
/*     */   public final void resumedAndDispatched() {
/* 535 */     this.log.logf(FQCN, Logger.Level.ERROR, null, resumedAndDispatched$str(), new Object[0]);
/*     */   }
/*     */   protected String resumedAndDispatched$str() {
/* 538 */     return "UT005080: HttpServerExchange cannot have both async IO resumed and dispatch() called in the same cycle";
/*     */   }
/*     */   
/*     */   public final void cannotProxyStartedRequest(HttpServerExchange exchange) {
/* 542 */     this.log.logf(FQCN, Logger.Level.ERROR, null, cannotProxyStartedRequest$str(), exchange);
/*     */   }
/*     */   protected String cannotProxyStartedRequest$str() {
/* 545 */     return "UT005081: Response has already been started, cannot proxy request %s";
/*     */   }
/*     */   protected String cannotUseWildcardAddressAsModClusterManagementHost$str() {
/* 548 */     return "UT005082: Configured mod_cluster management host address cannot be a wildcard address (%s)!";
/*     */   }
/*     */   
/*     */   public final IllegalArgumentException cannotUseWildcardAddressAsModClusterManagementHost(String providedAddress) {
/* 552 */     IllegalArgumentException result = new IllegalArgumentException(String.format(getLoggingLocale(), cannotUseWildcardAddressAsModClusterManagementHost$str(), new Object[] { providedAddress }));
/* 553 */     _copyStackTraceMinusOne(result);
/* 554 */     return result;
/*     */   }
/*     */   protected String unexpectedEndOfCompressedInput$str() {
/* 557 */     return "UT005083: Unexpected end of compressed input";
/*     */   }
/*     */   
/*     */   public final IOException unexpectedEndOfCompressedInput() {
/* 561 */     IOException result = new IOException(String.format(getLoggingLocale(), unexpectedEndOfCompressedInput$str(), new Object[0]));
/* 562 */     _copyStackTraceMinusOne(result);
/* 563 */     return result;
/*     */   }
/*     */   protected String dataLargerThanContentLength$str() {
/* 566 */     return "UT005084: Attempted to write %s bytes however content-length has been set to %s";
/*     */   }
/*     */   
/*     */   public final IOException dataLargerThanContentLength(long totalToWrite, long responseContentLength) {
/* 570 */     IOException result = new IOException(String.format(getLoggingLocale(), dataLargerThanContentLength$str(), new Object[] { Long.valueOf(totalToWrite), Long.valueOf(responseContentLength) }));
/* 571 */     _copyStackTraceMinusOne(result);
/* 572 */     return result;
/*     */   }
/*     */   
/*     */   public final void responseWasNotTerminated(ServerConnection connection, HttpServerExchange exchange) {
/* 576 */     this.log.logf(FQCN, Logger.Level.DEBUG, null, responseWasNotTerminated$str(), connection, exchange);
/*     */   }
/*     */   protected String responseWasNotTerminated$str() {
/* 579 */     return "UT005085: Connection %s for exchange %s was not closed cleanly, forcibly closing connection";
/*     */   }
/*     */   
/*     */   public final void failedToAcceptSSLRequest(Exception e) {
/* 583 */     this.log.logf(FQCN, Logger.Level.ERROR, e, failedToAcceptSSLRequest$str(), new Object[0]);
/*     */   }
/*     */   protected String failedToAcceptSSLRequest$str() {
/* 586 */     return "UT005086: Failed to accept SSL request";
/*     */   }
/*     */   
/*     */   public final void closeAsyncFailed(IOException e) {
/* 590 */     this.log.logf(FQCN, Logger.Level.ERROR, e, closeAsyncFailed$str(), new Object[0]);
/*     */   }
/*     */   protected String closeAsyncFailed$str() {
/* 593 */     return "UT005088: Failed to execute ServletOutputStream.closeAsync() on IO thread";
/*     */   }
/*     */   protected String nullParameter$str() {
/* 596 */     return "UT005089: Method parameter '%s' cannot be null";
/*     */   }
/*     */   
/*     */   public final IllegalArgumentException nullParameter(String name) {
/* 600 */     IllegalArgumentException result = new IllegalArgumentException(String.format(getLoggingLocale(), nullParameter$str(), new Object[] { name }));
/* 601 */     _copyStackTraceMinusOne(result);
/* 602 */     return result;
/*     */   }
/*     */   
/*     */   public final void handleUnexpectedFailure(Throwable t) {
/* 606 */     this.log.logf(FQCN, Logger.Level.ERROR, t, handleUnexpectedFailure$str(), new Object[0]);
/*     */   }
/*     */   protected String handleUnexpectedFailure$str() {
/* 609 */     return "UT005090: Unexpected failure";
/*     */   }
/*     */   
/*     */   public final void directBufferDeallocatorInitializationFailed(Throwable t) {
/* 613 */     this.log.logf(FQCN, Logger.Level.ERROR, t, directBufferDeallocatorInitializationFailed$str(), new Object[0]);
/*     */   }
/*     */   protected String directBufferDeallocatorInitializationFailed$str() {
/* 616 */     return "UT005091: Failed to initialize DirectByteBufferDeallocator";
/*     */   }
/*     */   
/*     */   public final void directBufferDeallocationFailed(Throwable t) {
/* 620 */     this.log.logf(FQCN, Logger.Level.DEBUG, t, directBufferDeallocationFailed$str(), new Object[0]);
/*     */   }
/*     */   protected String directBufferDeallocationFailed$str() {
/* 623 */     return "UT005092: Failed to free direct buffer";
/*     */   }
/*     */   
/*     */   public final void blockingReadTimedOut(ReadTimeoutException rte) {
/* 627 */     this.log.logf(FQCN, Logger.Level.DEBUG, (Throwable)rte, blockingReadTimedOut$str(), new Object[0]);
/*     */   }
/*     */   protected String blockingReadTimedOut$str() {
/* 630 */     return "UT005093: Blocking read timed out";
/*     */   }
/*     */   
/*     */   public final void blockingWriteTimedOut(WriteTimeoutException rte) {
/* 634 */     this.log.logf(FQCN, Logger.Level.DEBUG, (Throwable)rte, blockingWriteTimedOut$str(), new Object[0]);
/*     */   }
/*     */   protected String blockingWriteTimedOut$str() {
/* 637 */     return "UT005094: Blocking write timed out";
/*     */   }
/*     */   
/*     */   public final void sslEngineDelegatedTaskRejected(RejectedExecutionException ree) {
/* 641 */     this.log.logf(FQCN, Logger.Level.DEBUG, ree, sslEngineDelegatedTaskRejected$str(), new Object[0]);
/*     */   }
/*     */   protected String sslEngineDelegatedTaskRejected$str() {
/* 644 */     return "UT005095: SSLEngine delegated task was rejected";
/*     */   }
/*     */   
/*     */   public final void authenticationFailedFor(String header, HttpServerExchange exchange, Exception e) {
/* 648 */     this.log.logf(FQCN, Logger.Level.DEBUG, e, authenticationFailedFor$str(), header, exchange);
/*     */   }
/*     */   protected String authenticationFailedFor$str() {
/* 651 */     return "UT005096: Authentication failed for digest header %s in %s";
/*     */   }
/*     */   
/*     */   public final void failedToObtainSubject(HttpServerExchange exchange, GeneralSecurityException e) {
/* 655 */     this.log.logf(FQCN, Logger.Level.DEBUG, e, failedToObtainSubject$str(), exchange);
/*     */   }
/*     */   protected String failedToObtainSubject$str() {
/* 658 */     return "UT005097: Failed to obtain subject for %s";
/*     */   }
/*     */   
/*     */   public final void failedToNegotiateAtGSSAPI(HttpServerExchange exchange, Throwable e) {
/* 662 */     this.log.logf(FQCN, Logger.Level.DEBUG, e, failedToNegotiateAtGSSAPI$str(), exchange);
/*     */   }
/*     */   protected String failedToNegotiateAtGSSAPI$str() {
/* 665 */     return "UT005098: GSSAPI negotiation failed for %s";
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\UndertowLogger_$logger.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */