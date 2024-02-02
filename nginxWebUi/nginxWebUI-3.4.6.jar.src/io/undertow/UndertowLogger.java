/*    */ package io.undertow;
/*    */ 
/*    */ import io.undertow.client.ClientConnection;
/*    */ import io.undertow.protocols.ssl.SslConduit;
/*    */ import io.undertow.server.HttpServerExchange;
/*    */ import io.undertow.server.ServerConnection;
/*    */ import io.undertow.server.handlers.sse.ServerSentEventConnection;
/*    */ import io.undertow.util.HeaderMap;
/*    */ import io.undertow.util.HttpString;
/*    */ import java.io.IOException;
/*    */ import java.net.InetAddress;
/*    */ import java.net.InetSocketAddress;
/*    */ import java.net.SocketAddress;
/*    */ import java.net.URI;
/*    */ import java.nio.file.Path;
/*    */ import java.security.GeneralSecurityException;
/*    */ import java.util.Date;
/*    */ import java.util.List;
/*    */ import java.util.concurrent.RejectedExecutionException;
/*    */ import org.jboss.logging.BasicLogger;
/*    */ import org.jboss.logging.Logger;
/*    */ import org.jboss.logging.annotations.Cause;
/*    */ import org.jboss.logging.annotations.LogMessage;
/*    */ import org.jboss.logging.annotations.Message;
/*    */ import org.jboss.logging.annotations.MessageLogger;
/*    */ import org.xnio.channels.ReadTimeoutException;
/*    */ import org.xnio.channels.WriteTimeoutException;
/*    */ import org.xnio.ssl.SslConnection;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ @MessageLogger(projectCode = "UT")
/*    */ public interface UndertowLogger
/*    */   extends BasicLogger
/*    */ {
/* 62 */   public static final UndertowLogger ROOT_LOGGER = (UndertowLogger)Logger.getMessageLogger(UndertowLogger.class, UndertowLogger.class.getPackage().getName());
/* 63 */   public static final UndertowLogger CLIENT_LOGGER = (UndertowLogger)Logger.getMessageLogger(UndertowLogger.class, ClientConnection.class.getPackage().getName());
/*    */   
/* 65 */   public static final UndertowLogger PREDICATE_LOGGER = (UndertowLogger)Logger.getMessageLogger(UndertowLogger.class, UndertowLogger.class.getPackage().getName() + ".predicate");
/* 66 */   public static final UndertowLogger REQUEST_LOGGER = (UndertowLogger)Logger.getMessageLogger(UndertowLogger.class, UndertowLogger.class.getPackage().getName() + ".request");
/* 67 */   public static final UndertowLogger SESSION_LOGGER = (UndertowLogger)Logger.getMessageLogger(UndertowLogger.class, UndertowLogger.class.getPackage().getName() + ".session");
/* 68 */   public static final UndertowLogger SECURITY_LOGGER = (UndertowLogger)Logger.getMessageLogger(UndertowLogger.class, UndertowLogger.class.getPackage().getName() + ".request.security");
/* 69 */   public static final UndertowLogger PROXY_REQUEST_LOGGER = (UndertowLogger)Logger.getMessageLogger(UndertowLogger.class, UndertowLogger.class.getPackage().getName() + ".proxy");
/* 70 */   public static final UndertowLogger REQUEST_DUMPER_LOGGER = (UndertowLogger)Logger.getMessageLogger(UndertowLogger.class, UndertowLogger.class.getPackage().getName() + ".request.dump");
/*    */ 
/*    */ 
/*    */ 
/*    */   
/* 75 */   public static final UndertowLogger REQUEST_IO_LOGGER = (UndertowLogger)Logger.getMessageLogger(UndertowLogger.class, UndertowLogger.class.getPackage().getName() + ".request.io");
/* 76 */   public static final UndertowLogger ERROR_RESPONSE = (UndertowLogger)Logger.getMessageLogger(UndertowLogger.class, UndertowLogger.class.getPackage().getName() + ".request.error-response");
/*    */   
/*    */   @LogMessage(level = Logger.Level.ERROR)
/*    */   @Message(id = 5001, value = "An exception occurred processing the request")
/*    */   void exceptionProcessingRequest(@Cause Throwable paramThrowable);
/*    */   
/*    */   @LogMessage(level = Logger.Level.ERROR)
/*    */   @Message(id = 5003, value = "IOException reading from channel")
/*    */   void ioExceptionReadingFromChannel(@Cause IOException paramIOException);
/*    */   
/*    */   @LogMessage(level = Logger.Level.ERROR)
/*    */   @Message(id = 5005, value = "Cannot remove uploaded file %s")
/*    */   void cannotRemoveUploadedFile(Path paramPath);
/*    */   
/*    */   @LogMessage(level = Logger.Level.DEBUG)
/*    */   @Message(id = 5006, value = "Connection from %s terminated as request header was larger than %s")
/*    */   void requestHeaderWasTooLarge(SocketAddress paramSocketAddress, int paramInt);
/*    */   
/*    */   @LogMessage(level = Logger.Level.DEBUG)
/*    */   @Message(id = 5007, value = "Request was not fully consumed")
/*    */   void requestWasNotFullyConsumed();
/*    */   
/*    */   @LogMessage(level = Logger.Level.DEBUG)
/*    */   @Message(id = 5008, value = "An invalid token '%s' with value '%s' has been received.")
/*    */   void invalidTokenReceived(String paramString1, String paramString2);
/*    */   
/*    */   @LogMessage(level = Logger.Level.DEBUG)
/*    */   @Message(id = 5009, value = "A mandatory token %s is missing from the request.")
/*    */   void missingAuthorizationToken(String paramString);
/*    */   
/*    */   @LogMessage(level = Logger.Level.DEBUG)
/*    */   @Message(id = 5010, value = "Verification of authentication tokens for user '%s' has failed using mechanism '%s'.")
/*    */   void authenticationFailed(String paramString1, String paramString2);
/*    */   
/*    */   @LogMessage(level = Logger.Level.ERROR)
/*    */   @Message(id = 5011, value = "Ignoring AJP request with prefix %s")
/*    */   void ignoringAjpRequestWithPrefixCode(byte paramByte);
/*    */   
/*    */   @LogMessage(level = Logger.Level.DEBUG)
/*    */   @Message(id = 5013, value = "An IOException occurred")
/*    */   void ioException(@Cause IOException paramIOException);
/*    */   
/*    */   @LogMessage(level = Logger.Level.DEBUG)
/*    */   @Message(id = 5014, value = "Failed to parse request")
/*    */   void failedToParseRequest(@Cause Throwable paramThrowable);
/*    */   
/*    */   @LogMessage(level = Logger.Level.ERROR)
/*    */   @Message(id = 5015, value = "Error rotating access log")
/*    */   void errorRotatingAccessLog(@Cause IOException paramIOException);
/*    */   
/*    */   @LogMessage(level = Logger.Level.ERROR)
/*    */   @Message(id = 5016, value = "Error writing access log")
/*    */   void errorWritingAccessLog(@Cause IOException paramIOException);
/*    */   
/*    */   @LogMessage(level = Logger.Level.ERROR)
/*    */   @Message(id = 5017, value = "Unknown variable %s. For the literal percent character use two percent characters: '%%'")
/*    */   void unknownVariable(String paramString);
/*    */   
/*    */   @LogMessage(level = Logger.Level.ERROR)
/*    */   @Message(id = 5018, value = "Exception invoking close listener %s")
/*    */   void exceptionInvokingCloseListener(ServerConnection.CloseListener paramCloseListener, @Cause Throwable paramThrowable);
/*    */   
/*    */   @LogMessage(level = Logger.Level.ERROR)
/*    */   @Message(id = 5020, value = "Error writing JDBC log")
/*    */   void errorWritingJDBCLog(@Cause Exception paramException);
/*    */   
/*    */   @LogMessage(level = Logger.Level.ERROR)
/*    */   @Message(id = 5022, value = "Exception generating error page %s")
/*    */   void exceptionGeneratingErrorPage(@Cause Exception paramException, String paramString);
/*    */   
/*    */   @LogMessage(level = Logger.Level.ERROR)
/*    */   @Message(id = 5023, value = "Exception handling request to %s")
/*    */   void exceptionHandlingRequest(@Cause Throwable paramThrowable, String paramString);
/*    */   
/*    */   @LogMessage(level = Logger.Level.ERROR)
/*    */   @Message(id = 5024, value = "Could not register resource change listener for caching resource manager, automatic invalidation of cached resource will not work")
/*    */   void couldNotRegisterChangeListener(@Cause Exception paramException);
/*    */   
/*    */   @LogMessage(level = Logger.Level.ERROR)
/*    */   @Message(id = 5027, value = "Timing out request to %s")
/*    */   void timingOutRequest(String paramString);
/*    */   
/*    */   @LogMessage(level = Logger.Level.ERROR)
/*    */   @Message(id = 5028, value = "Proxy request to %s failed")
/*    */   void proxyRequestFailed(String paramString, @Cause Exception paramException);
/*    */   
/*    */   @LogMessage(level = Logger.Level.ERROR)
/*    */   @Message(id = 5031, value = "Proxy request to %s could not connect to backend server %s")
/*    */   void proxyFailedToConnectToBackend(String paramString, URI paramURI);
/*    */   
/*    */   @LogMessage(level = Logger.Level.ERROR)
/*    */   @Message(id = 5032, value = "Listener not making progress on framed channel, closing channel to prevent infinite loop")
/*    */   void listenerNotProgressing();
/*    */   
/*    */   @LogMessage(level = Logger.Level.ERROR)
/*    */   @Message(id = 5034, value = "Remote endpoint failed to send initial settings frame in HTTP2 connection, frame type %s")
/*    */   void remoteEndpointFailedToSendInitialSettings(int paramInt);
/*    */   
/*    */   @LogMessage(level = Logger.Level.DEBUG)
/*    */   @Message(id = 5035, value = "Closing channel because of parse timeout for remote address %s")
/*    */   void parseRequestTimedOut(SocketAddress paramSocketAddress);
/*    */   
/*    */   @LogMessage(level = Logger.Level.ERROR)
/*    */   @Message(id = 5036, value = "ALPN negotiation failed for %s and no fallback defined, closing connection")
/*    */   void noALPNFallback(SocketAddress paramSocketAddress);
/*    */   
/*    */   @LogMessage(level = Logger.Level.WARN)
/*    */   @Message(id = 5037, value = "Name of the cookie containing the session id, %s, had been too long and was truncated to: %s")
/*    */   void stickySessionCookieLengthTruncated(String paramString1, String paramString2);
/*    */   
/*    */   @LogMessage(level = Logger.Level.DEBUG)
/*    */   @Message(id = 5038, value = "Balancer created: id: %s, name: %s, stickySession: %s, stickySessionCookie: %s, stickySessionPath: %s, stickySessionRemove: %s, stickySessionForce: %s, waitWorker: %s, maxattempts: %s")
/*    */   void balancerCreated(int paramInt1, String paramString1, boolean paramBoolean1, String paramString2, String paramString3, boolean paramBoolean2, boolean paramBoolean3, int paramInt2, int paramInt3);
/*    */   
/*    */   @LogMessage(level = Logger.Level.INFO)
/*    */   @Message(id = 5039, value = "Undertow starts mod_cluster proxy advertisements on %s with frequency %s ms")
/*    */   void proxyAdvertisementsStarted(String paramString, int paramInt);
/*    */   
/*    */   @LogMessage(level = Logger.Level.DEBUG)
/*    */   @Message(id = 5040, value = "Gonna send payload:\n%s")
/*    */   void proxyAdvertiseMessagePayload(String paramString);
/*    */   
/*    */   @LogMessage(level = Logger.Level.ERROR)
/*    */   @Message(id = 5041, value = "Cannot send advertise message. Address: %s")
/*    */   void proxyAdvertiseCannotSendMessage(@Cause Exception paramException, InetSocketAddress paramInetSocketAddress);
/*    */   
/*    */   @LogMessage(level = Logger.Level.DEBUG)
/*    */   @Message(id = 5042, value = "Undertow mod_cluster proxy MCMPHandler created")
/*    */   void mcmpHandlerCreated();
/*    */   
/*    */   @LogMessage(level = Logger.Level.ERROR)
/*    */   @Message(id = 5043, value = "Error in processing MCMP commands: Type:%s, Mess: %s")
/*    */   void mcmpProcessingError(String paramString1, String paramString2);
/*    */   
/*    */   @LogMessage(level = Logger.Level.INFO)
/*    */   @Message(id = 5044, value = "Removing node %s")
/*    */   void removingNode(String paramString);
/*    */   
/*    */   @LogMessage(level = Logger.Level.INFO)
/*    */   @Message(id = 5045, value = "Registering context %s, for node %s")
/*    */   void registeringContext(String paramString1, String paramString2);
/*    */   
/*    */   @LogMessage(level = Logger.Level.DEBUG)
/*    */   @Message(id = 5046, value = "Registering context %s, for node %s, with aliases %s")
/*    */   void registeringContext(String paramString1, String paramString2, List<String> paramList);
/*    */   
/*    */   @LogMessage(level = Logger.Level.INFO)
/*    */   @Message(id = 5047, value = "Unregistering context %s, from node %s")
/*    */   void unregisteringContext(String paramString1, String paramString2);
/*    */   
/*    */   @LogMessage(level = Logger.Level.DEBUG)
/*    */   @Message(id = 5048, value = "Node %s in error")
/*    */   void nodeIsInError(String paramString);
/*    */   
/*    */   @LogMessage(level = Logger.Level.DEBUG)
/*    */   @Message(id = 5049, value = "NodeConfig created: connectionURI: %s, balancer: %s, load balancing group: %s, jvmRoute: %s, flushPackets: %s, flushwait: %s, ping: %s,ttl: %s, timeout: %s, maxConnections: %s, cacheConnections: %s, requestQueueSize: %s, queueNewRequests: %s")
/*    */   void nodeConfigCreated(URI paramURI, String paramString1, String paramString2, String paramString3, boolean paramBoolean1, int paramInt1, int paramInt2, long paramLong, int paramInt3, int paramInt4, int paramInt5, int paramInt6, boolean paramBoolean2);
/*    */   
/*    */   @LogMessage(level = Logger.Level.ERROR)
/*    */   @Message(id = 5050, value = "Failed to process management request")
/*    */   void failedToProcessManagementReq(@Cause Exception paramException);
/*    */   
/*    */   @LogMessage(level = Logger.Level.ERROR)
/*    */   @Message(id = 5051, value = "Failed to send ping response")
/*    */   void failedToSendPingResponse(@Cause Exception paramException);
/*    */   
/*    */   @LogMessage(level = Logger.Level.DEBUG)
/*    */   @Message(id = 5052, value = "Failed to send ping response, node.getJvmRoute(): %s, jvmRoute: %s")
/*    */   void failedToSendPingResponseDBG(@Cause Exception paramException, String paramString1, String paramString2);
/*    */   
/*    */   @LogMessage(level = Logger.Level.INFO)
/*    */   @Message(id = 5053, value = "Registering node %s, connection: %s")
/*    */   void registeringNode(String paramString, URI paramURI);
/*    */   
/*    */   @LogMessage(level = Logger.Level.DEBUG)
/*    */   @Message(id = 5054, value = "MCMP processing, key: %s, value: %s")
/*    */   void mcmpKeyValue(HttpString paramHttpString, String paramString);
/*    */   
/*    */   @LogMessage(level = Logger.Level.DEBUG)
/*    */   @Message(id = 5055, value = "HttpClientPingTask run for connection: %s")
/*    */   void httpClientPingTask(URI paramURI);
/*    */   
/*    */   @LogMessage(level = Logger.Level.DEBUG)
/*    */   @Message(id = 5056, value = "Received node load in STATUS message, node jvmRoute: %s, load: %s")
/*    */   void receivedNodeLoad(String paramString1, String paramString2);
/*    */   
/*    */   @LogMessage(level = Logger.Level.DEBUG)
/*    */   @Message(id = 5057, value = "Sending MCMP response to destination: %s, HTTP status: %s, Headers: %s, response: %s")
/*    */   void mcmpSendingResponse(InetSocketAddress paramInetSocketAddress, int paramInt, HeaderMap paramHeaderMap, String paramString);
/*    */   
/*    */   @LogMessage(level = Logger.Level.WARN)
/*    */   @Message(id = 5058, value = "Could not bind multicast socket to %s (%s address): %s; make sure your multicast address is of the same type as the IP stack (IPv4 or IPv6). Multicast socket will not be bound to an address, but this may lead to cross talking (see http://www.jboss.org/community/docs/DOC-9469 for details).")
/*    */   void potentialCrossTalking(InetAddress paramInetAddress, String paramString1, String paramString2);
/*    */   
/*    */   @LogMessage(level = Logger.Level.WARN)
/*    */   @Message(id = 5060, value = "Predicate %s uses old style square braces to define predicates, which will be removed in a future release. predicate[value] should be changed to predicate(value)")
/*    */   void oldStylePredicateSyntax(String paramString);
/*    */   
/*    */   @Message(id = 5061, value = "More than %s restarts detected, breaking assumed infinite loop")
/*    */   IllegalStateException maxRestartsExceeded(int paramInt);
/*    */   
/*    */   @LogMessage(level = Logger.Level.ERROR)
/*    */   @Message(id = 5062, value = "Pattern parse error")
/*    */   void extendedAccessLogPatternParseError(@Cause Throwable paramThrowable);
/*    */   
/*    */   @LogMessage(level = Logger.Level.ERROR)
/*    */   @Message(id = 5063, value = "Unable to decode with rest of chars starting: %s")
/*    */   void extendedAccessLogUnknownToken(String paramString);
/*    */   
/*    */   @LogMessage(level = Logger.Level.ERROR)
/*    */   @Message(id = 5064, value = "No closing ) found for in decode")
/*    */   void extendedAccessLogMissingClosing();
/*    */   
/*    */   @LogMessage(level = Logger.Level.ERROR)
/*    */   @Message(id = 5065, value = "The next characters couldn't be decoded: %s")
/*    */   void extendedAccessLogCannotDecode(String paramString);
/*    */   
/*    */   @LogMessage(level = Logger.Level.ERROR)
/*    */   @Message(id = 5066, value = "X param for servlet request, couldn't decode value: %s")
/*    */   void extendedAccessLogCannotDecodeXParamValue(String paramString);
/*    */   
/*    */   @LogMessage(level = Logger.Level.ERROR)
/*    */   @Message(id = 5067, value = "X param in wrong format. Needs to be 'x-#(...)'")
/*    */   void extendedAccessLogBadXParam();
/*    */   
/*    */   @LogMessage(level = Logger.Level.INFO)
/*    */   @Message(id = 5068, value = "Pattern was just empty or whitespace")
/*    */   void extendedAccessLogEmptyPattern();
/*    */   
/*    */   @LogMessage(level = Logger.Level.ERROR)
/*    */   @Message(id = 5069, value = "Failed to write JDBC access log")
/*    */   void failedToWriteJdbcAccessLog(@Cause Exception paramException);
/*    */   
/*    */   @LogMessage(level = Logger.Level.ERROR)
/*    */   @Message(id = 5070, value = "Failed to write pre-cached file")
/*    */   void failedToWritePreCachedFile();
/*    */   
/*    */   @LogMessage(level = Logger.Level.ERROR)
/*    */   @Message(id = 5071, value = "Undertow request failed %s")
/*    */   void undertowRequestFailed(@Cause Throwable paramThrowable, HttpServerExchange paramHttpServerExchange);
/*    */   
/*    */   @LogMessage(level = Logger.Level.WARN)
/*    */   @Message(id = 5072, value = "Thread %s (id=%s) has been active for %s milliseconds (since %s) to serve the same request for %s and may be stuck (configured threshold for this StuckThreadDetectionValve is %s seconds). There is/are %s thread(s) in total that are monitored by this Valve and may be stuck.")
/*    */   void stuckThreadDetected(String paramString1, long paramLong1, long paramLong2, Date paramDate, String paramString2, int paramInt1, int paramInt2, @Cause Throwable paramThrowable);
/*    */   
/*    */   @LogMessage(level = Logger.Level.WARN)
/*    */   @Message(id = 5073, value = "Thread %s (id=%s) was previously reported to be stuck but has completed. It was active for approximately %s milliseconds. There is/are still %s thread(s) that are monitored by this Valve and may be stuck.")
/*    */   void stuckThreadCompleted(String paramString, long paramLong1, long paramLong2, int paramInt);
/*    */   
/*    */   @LogMessage(level = Logger.Level.ERROR)
/*    */   @Message(id = 5074, value = "Failed to invoke error callback %s for SSE task")
/*    */   void failedToInvokeFailedCallback(ServerSentEventConnection.EventCallback paramEventCallback, @Cause Exception paramException);
/*    */   
/*    */   @Message(id = 5075, value = "Unable to resolve mod_cluster management host's address for '%s'")
/*    */   IllegalStateException unableToResolveModClusterManagementHost(String paramString);
/*    */   
/*    */   @LogMessage(level = Logger.Level.ERROR)
/*    */   @Message(id = 5076, value = "SSL read loop detected. This should not happen, please report this to the Undertow developers. Current state %s")
/*    */   void sslReadLoopDetected(SslConduit paramSslConduit);
/*    */   
/*    */   @LogMessage(level = Logger.Level.ERROR)
/*    */   @Message(id = 5077, value = "SSL unwrap buffer overflow detected. This should not happen, please report this to the Undertow developers. Current state %s")
/*    */   void sslBufferOverflow(SslConduit paramSslConduit);
/*    */   
/*    */   @LogMessage(level = Logger.Level.ERROR)
/*    */   @Message(id = 5079, value = "ALPN negotiation on %s failed")
/*    */   void alpnConnectionFailed(SslConnection paramSslConnection);
/*    */   
/*    */   @LogMessage(level = Logger.Level.ERROR)
/*    */   @Message(id = 5080, value = "HttpServerExchange cannot have both async IO resumed and dispatch() called in the same cycle")
/*    */   void resumedAndDispatched();
/*    */   
/*    */   @LogMessage(level = Logger.Level.ERROR)
/*    */   @Message(id = 5081, value = "Response has already been started, cannot proxy request %s")
/*    */   void cannotProxyStartedRequest(HttpServerExchange paramHttpServerExchange);
/*    */   
/*    */   @Message(id = 5082, value = "Configured mod_cluster management host address cannot be a wildcard address (%s)!")
/*    */   IllegalArgumentException cannotUseWildcardAddressAsModClusterManagementHost(String paramString);
/*    */   
/*    */   @Message(id = 5083, value = "Unexpected end of compressed input")
/*    */   IOException unexpectedEndOfCompressedInput();
/*    */   
/*    */   @Message(id = 5084, value = "Attempted to write %s bytes however content-length has been set to %s")
/*    */   IOException dataLargerThanContentLength(long paramLong1, long paramLong2);
/*    */   
/*    */   @LogMessage(level = Logger.Level.DEBUG)
/*    */   @Message(id = 5085, value = "Connection %s for exchange %s was not closed cleanly, forcibly closing connection")
/*    */   void responseWasNotTerminated(ServerConnection paramServerConnection, HttpServerExchange paramHttpServerExchange);
/*    */   
/*    */   @LogMessage(level = Logger.Level.ERROR)
/*    */   @Message(id = 5086, value = "Failed to accept SSL request")
/*    */   void failedToAcceptSSLRequest(@Cause Exception paramException);
/*    */   
/*    */   @LogMessage(level = Logger.Level.ERROR)
/*    */   @Message(id = 5088, value = "Failed to execute ServletOutputStream.closeAsync() on IO thread")
/*    */   void closeAsyncFailed(@Cause IOException paramIOException);
/*    */   
/*    */   @Message(id = 5089, value = "Method parameter '%s' cannot be null")
/*    */   IllegalArgumentException nullParameter(String paramString);
/*    */   
/*    */   @LogMessage(level = Logger.Level.ERROR)
/*    */   @Message(id = 5090, value = "Unexpected failure")
/*    */   void handleUnexpectedFailure(@Cause Throwable paramThrowable);
/*    */   
/*    */   @LogMessage(level = Logger.Level.ERROR)
/*    */   @Message(id = 5091, value = "Failed to initialize DirectByteBufferDeallocator")
/*    */   void directBufferDeallocatorInitializationFailed(@Cause Throwable paramThrowable);
/*    */   
/*    */   @LogMessage(level = Logger.Level.DEBUG)
/*    */   @Message(id = 5092, value = "Failed to free direct buffer")
/*    */   void directBufferDeallocationFailed(@Cause Throwable paramThrowable);
/*    */   
/*    */   @LogMessage(level = Logger.Level.DEBUG)
/*    */   @Message(id = 5093, value = "Blocking read timed out")
/*    */   void blockingReadTimedOut(@Cause ReadTimeoutException paramReadTimeoutException);
/*    */   
/*    */   @LogMessage(level = Logger.Level.DEBUG)
/*    */   @Message(id = 5094, value = "Blocking write timed out")
/*    */   void blockingWriteTimedOut(@Cause WriteTimeoutException paramWriteTimeoutException);
/*    */   
/*    */   @LogMessage(level = Logger.Level.DEBUG)
/*    */   @Message(id = 5095, value = "SSLEngine delegated task was rejected")
/*    */   void sslEngineDelegatedTaskRejected(@Cause RejectedExecutionException paramRejectedExecutionException);
/*    */   
/*    */   @LogMessage(level = Logger.Level.DEBUG)
/*    */   @Message(id = 5096, value = "Authentication failed for digest header %s in %s")
/*    */   void authenticationFailedFor(String paramString, HttpServerExchange paramHttpServerExchange, @Cause Exception paramException);
/*    */   
/*    */   @LogMessage(level = Logger.Level.DEBUG)
/*    */   @Message(id = 5097, value = "Failed to obtain subject for %s")
/*    */   void failedToObtainSubject(HttpServerExchange paramHttpServerExchange, @Cause GeneralSecurityException paramGeneralSecurityException);
/*    */   
/*    */   @LogMessage(level = Logger.Level.DEBUG)
/*    */   @Message(id = 5098, value = "GSSAPI negotiation failed for %s")
/*    */   void failedToNegotiateAtGSSAPI(HttpServerExchange paramHttpServerExchange, @Cause Throwable paramThrowable);
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\UndertowLogger.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */