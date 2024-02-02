/*     */ package io.undertow.server.handlers.proxy;
/*     */ 
/*     */ import io.undertow.UndertowLogger;
/*     */ import io.undertow.UndertowMessages;
/*     */ import io.undertow.attribute.ExchangeAttribute;
/*     */ import io.undertow.attribute.ExchangeAttributes;
/*     */ import io.undertow.client.ClientCallback;
/*     */ import io.undertow.client.ClientConnection;
/*     */ import io.undertow.client.ClientExchange;
/*     */ import io.undertow.client.ClientRequest;
/*     */ import io.undertow.client.ClientResponse;
/*     */ import io.undertow.client.ContinueNotification;
/*     */ import io.undertow.client.ProxiedRequestAttachments;
/*     */ import io.undertow.client.PushCallback;
/*     */ import io.undertow.io.IoCallback;
/*     */ import io.undertow.io.Sender;
/*     */ import io.undertow.predicate.IdempotentPredicate;
/*     */ import io.undertow.predicate.Predicate;
/*     */ import io.undertow.server.ExchangeCompletionListener;
/*     */ import io.undertow.server.HttpHandler;
/*     */ import io.undertow.server.HttpServerExchange;
/*     */ import io.undertow.server.HttpUpgradeListener;
/*     */ import io.undertow.server.SSLSessionInfo;
/*     */ import io.undertow.server.handlers.ResponseCodeHandler;
/*     */ import io.undertow.server.protocol.http.HttpAttachments;
/*     */ import io.undertow.server.protocol.http.HttpContinue;
/*     */ import io.undertow.util.Attachable;
/*     */ import io.undertow.util.AttachmentKey;
/*     */ import io.undertow.util.Certificates;
/*     */ import io.undertow.util.CopyOnWriteMap;
/*     */ import io.undertow.util.HeaderMap;
/*     */ import io.undertow.util.HeaderValues;
/*     */ import io.undertow.util.Headers;
/*     */ import io.undertow.util.HttpString;
/*     */ import io.undertow.util.NetworkUtils;
/*     */ import io.undertow.util.SameThreadExecutor;
/*     */ import io.undertow.util.Transfer;
/*     */ import io.undertow.util.WorkerUtils;
/*     */ import java.io.Closeable;
/*     */ import java.io.IOException;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.net.SocketAddress;
/*     */ import java.nio.channels.Channel;
/*     */ import java.nio.charset.StandardCharsets;
/*     */ import java.security.cert.Certificate;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.concurrent.Executor;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import java.util.stream.Collectors;
/*     */ import javax.net.ssl.SSLPeerUnverifiedException;
/*     */ import org.jboss.logging.Logger;
/*     */ import org.xnio.ChannelExceptionHandler;
/*     */ import org.xnio.ChannelListener;
/*     */ import org.xnio.ChannelListeners;
/*     */ import org.xnio.IoUtils;
/*     */ import org.xnio.StreamConnection;
/*     */ import org.xnio.XnioExecutor;
/*     */ import org.xnio.channels.StreamSinkChannel;
/*     */ import org.xnio.channels.StreamSourceChannel;
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
/*     */ public final class ProxyHandler
/*     */   implements HttpHandler
/*     */ {
/*  97 */   private static final int DEFAULT_MAX_RETRY_ATTEMPTS = Integer.getInteger("io.undertow.server.handlers.proxy.maxRetries", 1).intValue();
/*     */   
/*  99 */   private static final Logger log = Logger.getLogger(ProxyHandler.class.getPackage().getName());
/*     */   
/* 101 */   public static final String UTF_8 = StandardCharsets.UTF_8.name();
/*     */   
/* 103 */   private static final AttachmentKey<ProxyConnection> CONNECTION = AttachmentKey.create(ProxyConnection.class);
/* 104 */   private static final AttachmentKey<HttpServerExchange> EXCHANGE = AttachmentKey.create(HttpServerExchange.class);
/* 105 */   private static final AttachmentKey<XnioExecutor.Key> TIMEOUT_KEY = AttachmentKey.create(XnioExecutor.Key.class);
/*     */ 
/*     */   
/*     */   private final ProxyClient proxyClient;
/*     */ 
/*     */   
/*     */   private final int maxRequestTime;
/*     */   
/* 113 */   private final Map<HttpString, ExchangeAttribute> requestHeaders = (Map<HttpString, ExchangeAttribute>)new CopyOnWriteMap();
/*     */   
/*     */   private final HttpHandler next;
/*     */   
/*     */   private volatile boolean rewriteHostHeader;
/*     */   
/*     */   private volatile boolean reuseXForwarded;
/*     */   private volatile int maxConnectionRetries;
/*     */   private final Predicate idempotentRequestPredicate;
/*     */   
/*     */   @Deprecated
/*     */   public ProxyHandler(ProxyClient proxyClient, int maxRequestTime, HttpHandler next) {
/* 125 */     this(proxyClient, maxRequestTime, next, false, false);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public ProxyHandler(ProxyClient proxyClient, int maxRequestTime, HttpHandler next, boolean rewriteHostHeader, boolean reuseXForwarded) {
/* 138 */     this(proxyClient, maxRequestTime, next, rewriteHostHeader, reuseXForwarded, DEFAULT_MAX_RETRY_ATTEMPTS);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public ProxyHandler(ProxyClient proxyClient, int maxRequestTime, HttpHandler next, boolean rewriteHostHeader, boolean reuseXForwarded, int maxConnectionRetries) {
/* 151 */     this.proxyClient = proxyClient;
/* 152 */     this.maxRequestTime = maxRequestTime;
/* 153 */     this.next = next;
/* 154 */     this.rewriteHostHeader = rewriteHostHeader;
/* 155 */     this.reuseXForwarded = reuseXForwarded;
/* 156 */     this.maxConnectionRetries = maxConnectionRetries;
/* 157 */     this.idempotentRequestPredicate = (Predicate)IdempotentPredicate.INSTANCE;
/*     */   }
/*     */   
/*     */   @Deprecated
/*     */   public ProxyHandler(ProxyClient proxyClient, HttpHandler next) {
/* 162 */     this(proxyClient, -1, next);
/*     */   }
/*     */   
/*     */   ProxyHandler(Builder builder) {
/* 166 */     this.proxyClient = builder.proxyClient;
/* 167 */     this.maxRequestTime = builder.maxRequestTime;
/* 168 */     this.next = builder.next;
/* 169 */     this.rewriteHostHeader = builder.rewriteHostHeader;
/* 170 */     this.reuseXForwarded = builder.reuseXForwarded;
/* 171 */     this.maxConnectionRetries = builder.maxConnectionRetries;
/* 172 */     this.idempotentRequestPredicate = builder.idempotentRequestPredicate;
/* 173 */     for (Map.Entry<HttpString, ExchangeAttribute> e : (Iterable<Map.Entry<HttpString, ExchangeAttribute>>)builder.requestHeaders.entrySet()) {
/* 174 */       this.requestHeaders.put(e.getKey(), e.getValue());
/*     */     }
/*     */   }
/*     */   
/*     */   public void handleRequest(final HttpServerExchange exchange) throws Exception {
/* 179 */     ProxyClient.ProxyTarget target = this.proxyClient.findTarget(exchange);
/* 180 */     if (target == null) {
/* 181 */       log.debugf("No proxy target for request to %s", exchange.getRequestURL());
/* 182 */       this.next.handleRequest(exchange);
/*     */       return;
/*     */     } 
/* 185 */     if (exchange.isResponseStarted()) {
/*     */       
/* 187 */       UndertowLogger.REQUEST_LOGGER.cannotProxyStartedRequest(exchange);
/* 188 */       exchange.setStatusCode(500);
/* 189 */       exchange.endExchange();
/*     */       return;
/*     */     } 
/* 192 */     long timeout = (this.maxRequestTime > 0) ? (System.currentTimeMillis() + this.maxRequestTime) : 0L;
/* 193 */     int maxRetries = this.maxConnectionRetries;
/* 194 */     if (target instanceof ProxyClient.MaxRetriesProxyTarget) {
/* 195 */       maxRetries = Math.max(maxRetries, ((ProxyClient.MaxRetriesProxyTarget)target).getMaxRetries());
/*     */     }
/* 197 */     final ProxyClientHandler clientHandler = new ProxyClientHandler(exchange, target, timeout, maxRetries, this.idempotentRequestPredicate);
/* 198 */     if (timeout > 0L) {
/* 199 */       final XnioExecutor.Key key = WorkerUtils.executeAfter(exchange.getIoThread(), new Runnable()
/*     */           {
/*     */             public void run() {
/* 202 */               clientHandler.cancel(exchange);
/*     */             }
/*     */           },  this.maxRequestTime, TimeUnit.MILLISECONDS);
/* 205 */       exchange.putAttachment(TIMEOUT_KEY, key);
/* 206 */       exchange.addExchangeCompleteListener(new ExchangeCompletionListener()
/*     */           {
/*     */             public void exchangeEvent(HttpServerExchange exchange, ExchangeCompletionListener.NextListener nextListener) {
/* 209 */               key.remove();
/* 210 */               nextListener.proceed();
/*     */             }
/*     */           });
/*     */     } 
/* 214 */     exchange.dispatch(exchange.isInIoThread() ? SameThreadExecutor.INSTANCE : (Executor)exchange.getIoThread(), clientHandler);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public ProxyHandler addRequestHeader(HttpString header, ExchangeAttribute attribute) {
/* 227 */     this.requestHeaders.put(header, attribute);
/* 228 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public ProxyHandler addRequestHeader(HttpString header, String value) {
/* 241 */     this.requestHeaders.put(header, ExchangeAttributes.constant(value));
/* 242 */     return this;
/*     */   }
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
/*     */   @Deprecated
/*     */   public ProxyHandler addRequestHeader(HttpString header, String attribute, ClassLoader classLoader) {
/* 258 */     this.requestHeaders.put(header, ExchangeAttributes.parser(classLoader).parse(attribute));
/* 259 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public ProxyHandler removeRequestHeader(HttpString header) {
/* 270 */     this.requestHeaders.remove(header);
/* 271 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   static void copyHeaders(HeaderMap to, HeaderMap from) {
/* 276 */     long f = from.fastIterateNonEmpty();
/*     */     
/* 278 */     while (f != -1L) {
/* 279 */       HeaderValues values = from.fiCurrent(f);
/* 280 */       if (!to.contains(values.getHeaderName()))
/*     */       {
/* 282 */         to.putAll(values.getHeaderName(), (Collection)values);
/*     */       }
/* 284 */       f = from.fiNextNonEmpty(f);
/*     */     } 
/*     */   }
/*     */   
/*     */   public ProxyClient getProxyClient() {
/* 289 */     return this.proxyClient;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 294 */     List<ProxyClient.ProxyTarget> proxyTargets = this.proxyClient.getAllTargets();
/* 295 */     if (proxyTargets.isEmpty()) {
/* 296 */       return "ProxyHandler - " + this.proxyClient.getClass().getSimpleName();
/*     */     }
/* 298 */     if (proxyTargets.size() == 1 && !this.rewriteHostHeader) {
/* 299 */       return "reverse-proxy( '" + ((ProxyClient.ProxyTarget)proxyTargets.get(0)).toString() + "' )";
/*     */     }
/* 301 */     String outputResult = "reverse-proxy( { '" + (String)proxyTargets.stream().map(s -> s.toString()).collect(Collectors.joining("', '")) + "' }";
/* 302 */     if (this.rewriteHostHeader) {
/* 303 */       outputResult = outputResult + ", rewrite-host-header=true";
/*     */     }
/* 305 */     return outputResult + " )";
/*     */   }
/*     */ 
/*     */   
/*     */   private final class ProxyClientHandler
/*     */     implements ProxyCallback<ProxyConnection>, Runnable
/*     */   {
/*     */     private int tries;
/*     */     private final long timeout;
/*     */     private final int maxRetryAttempts;
/*     */     private final HttpServerExchange exchange;
/*     */     private final Predicate idempotentPredicate;
/*     */     private ProxyClient.ProxyTarget target;
/*     */     
/*     */     ProxyClientHandler(HttpServerExchange exchange, ProxyClient.ProxyTarget target, long timeout, int maxRetryAttempts, Predicate idempotentPredicate) {
/* 320 */       this.exchange = exchange;
/* 321 */       this.timeout = timeout;
/* 322 */       this.maxRetryAttempts = maxRetryAttempts;
/* 323 */       this.target = target;
/* 324 */       this.idempotentPredicate = idempotentPredicate;
/*     */     }
/*     */ 
/*     */     
/*     */     public void run() {
/* 329 */       ProxyHandler.this.proxyClient.getConnection(this.target, this.exchange, this, -1L, TimeUnit.MILLISECONDS);
/*     */     }
/*     */ 
/*     */     
/*     */     public void completed(HttpServerExchange exchange, ProxyConnection connection) {
/* 334 */       exchange.putAttachment(ProxyHandler.CONNECTION, connection);
/* 335 */       exchange.dispatch(SameThreadExecutor.INSTANCE, new ProxyHandler.ProxyAction(connection, exchange, ProxyHandler.this.requestHeaders, ProxyHandler.this.rewriteHostHeader, ProxyHandler.this.reuseXForwarded, exchange.isRequestComplete() ? this : null, this.idempotentPredicate));
/*     */     }
/*     */ 
/*     */     
/*     */     public void failed(HttpServerExchange exchange) {
/* 340 */       long time = System.currentTimeMillis();
/* 341 */       if (this.tries++ < this.maxRetryAttempts) {
/* 342 */         if (this.timeout > 0L && time > this.timeout) {
/* 343 */           cancel(exchange);
/*     */         } else {
/* 345 */           this.target = ProxyHandler.this.proxyClient.findTarget(exchange);
/* 346 */           if (this.target != null) {
/* 347 */             long remaining = (this.timeout > 0L) ? (this.timeout - time) : -1L;
/* 348 */             ProxyHandler.this.proxyClient.getConnection(this.target, exchange, this, remaining, TimeUnit.MILLISECONDS);
/*     */           } else {
/* 350 */             couldNotResolveBackend(exchange);
/*     */           } 
/*     */         } 
/*     */       } else {
/* 354 */         couldNotResolveBackend(exchange);
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/*     */     public void queuedRequestFailed(HttpServerExchange exchange) {
/* 360 */       failed(exchange);
/*     */     }
/*     */ 
/*     */     
/*     */     public void couldNotResolveBackend(HttpServerExchange exchange) {
/* 365 */       if (exchange.isResponseStarted()) {
/* 366 */         IoUtils.safeClose((Closeable)exchange.getConnection());
/*     */       } else {
/* 368 */         exchange.setStatusCode(503);
/* 369 */         exchange.endExchange();
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/*     */     void cancel(HttpServerExchange exchange) {
/* 375 */       ProxyConnection connectionAttachment = (ProxyConnection)exchange.getAttachment(ProxyHandler.CONNECTION);
/* 376 */       if (connectionAttachment != null) {
/* 377 */         ClientConnection clientConnection = connectionAttachment.getConnection();
/* 378 */         UndertowLogger.PROXY_REQUEST_LOGGER.timingOutRequest(clientConnection.getPeerAddress() + "" + exchange.getRequestURI());
/* 379 */         IoUtils.safeClose((Closeable)clientConnection);
/*     */       } else {
/* 381 */         UndertowLogger.PROXY_REQUEST_LOGGER.timingOutRequest(exchange.getRequestURI());
/*     */       } 
/* 383 */       if (exchange.isResponseStarted()) {
/* 384 */         IoUtils.safeClose((Closeable)exchange.getConnection());
/*     */       } else {
/* 386 */         exchange.setStatusCode(504);
/* 387 */         exchange.endExchange();
/*     */       } 
/*     */     }
/*     */   }
/*     */   
/*     */   private static class ProxyAction
/*     */     implements Runnable
/*     */   {
/*     */     private final ProxyConnection clientConnection;
/*     */     private final HttpServerExchange exchange;
/*     */     private final Map<HttpString, ExchangeAttribute> requestHeaders;
/*     */     private final boolean rewriteHostHeader;
/*     */     private final boolean reuseXForwarded;
/*     */     private final ProxyHandler.ProxyClientHandler proxyClientHandler;
/*     */     private final Predicate idempotentPredicate;
/*     */     
/*     */     ProxyAction(ProxyConnection clientConnection, HttpServerExchange exchange, Map<HttpString, ExchangeAttribute> requestHeaders, boolean rewriteHostHeader, boolean reuseXForwarded, ProxyHandler.ProxyClientHandler proxyClientHandler, Predicate idempotentPredicate) {
/* 404 */       this.clientConnection = clientConnection;
/* 405 */       this.exchange = exchange;
/* 406 */       this.requestHeaders = requestHeaders;
/* 407 */       this.rewriteHostHeader = rewriteHostHeader;
/* 408 */       this.reuseXForwarded = reuseXForwarded;
/* 409 */       this.proxyClientHandler = proxyClientHandler;
/* 410 */       this.idempotentPredicate = idempotentPredicate;
/*     */     }
/*     */     
/*     */     public void run() {
/*     */       final String remoteHost;
/* 415 */       final ClientRequest request = new ClientRequest();
/*     */       
/* 417 */       String targetURI = this.exchange.getRequestURI();
/* 418 */       if (this.exchange.isHostIncludedInRequestURI()) {
/* 419 */         int uriPart = targetURI.indexOf("//");
/* 420 */         if (uriPart != -1) {
/* 421 */           uriPart = targetURI.indexOf("/", uriPart + 2);
/* 422 */           if (uriPart != -1) {
/* 423 */             targetURI = targetURI.substring(uriPart);
/*     */           }
/*     */         } 
/*     */       } 
/*     */       
/* 428 */       if (!this.exchange.getResolvedPath().isEmpty() && targetURI.startsWith(this.exchange.getResolvedPath())) {
/* 429 */         targetURI = targetURI.substring(this.exchange.getResolvedPath().length());
/*     */       }
/*     */       
/* 432 */       StringBuilder requestURI = new StringBuilder();
/* 433 */       if (!this.clientConnection.getTargetPath().isEmpty() && (
/* 434 */         !this.clientConnection.getTargetPath().equals("/") || targetURI.isEmpty())) {
/* 435 */         requestURI.append(this.clientConnection.getTargetPath());
/*     */       }
/* 437 */       requestURI.append(targetURI);
/*     */       
/* 439 */       String qs = this.exchange.getQueryString();
/* 440 */       if (qs != null && !qs.isEmpty()) {
/* 441 */         requestURI.append('?');
/* 442 */         requestURI.append(qs);
/*     */       } 
/* 444 */       request.setPath(requestURI.toString())
/* 445 */         .setMethod(this.exchange.getRequestMethod());
/* 446 */       HeaderMap inboundRequestHeaders = this.exchange.getRequestHeaders();
/* 447 */       HeaderMap outboundRequestHeaders = request.getRequestHeaders();
/* 448 */       ProxyHandler.copyHeaders(outboundRequestHeaders, inboundRequestHeaders);
/*     */       
/* 450 */       if (!this.exchange.isPersistent())
/*     */       {
/*     */         
/* 453 */         outboundRequestHeaders.put(Headers.CONNECTION, "keep-alive");
/*     */       }
/* 455 */       if ("h2c".equals(this.exchange.getRequestHeaders().getFirst(Headers.UPGRADE))) {
/*     */         
/* 457 */         this.exchange.getRequestHeaders().remove(Headers.UPGRADE);
/* 458 */         outboundRequestHeaders.put(Headers.CONNECTION, "keep-alive");
/*     */       } 
/*     */       
/* 461 */       for (Map.Entry<HttpString, ExchangeAttribute> entry : this.requestHeaders.entrySet()) {
/* 462 */         String headerValue = ((ExchangeAttribute)entry.getValue()).readAttribute(this.exchange);
/* 463 */         if (headerValue == null || headerValue.isEmpty()) {
/* 464 */           outboundRequestHeaders.remove(entry.getKey()); continue;
/*     */         } 
/* 466 */         outboundRequestHeaders.put(entry.getKey(), headerValue.replace('\n', ' '));
/*     */       } 
/*     */ 
/*     */       
/* 470 */       SocketAddress address = this.exchange.getSourceAddress();
/* 471 */       if (address != null) {
/* 472 */         remoteHost = ((InetSocketAddress)address).getHostString();
/* 473 */         if (!((InetSocketAddress)address).isUnresolved()) {
/* 474 */           request.putAttachment(ProxiedRequestAttachments.REMOTE_ADDRESS, ((InetSocketAddress)address).getAddress().getHostAddress());
/*     */         }
/*     */       } else {
/*     */         
/* 478 */         remoteHost = "localhost";
/*     */       } 
/*     */       
/* 481 */       request.putAttachment(ProxiedRequestAttachments.REMOTE_HOST, remoteHost);
/*     */       
/* 483 */       if (this.reuseXForwarded && request.getRequestHeaders().contains(Headers.X_FORWARDED_FOR)) {
/*     */         
/* 485 */         String current = request.getRequestHeaders().getFirst(Headers.X_FORWARDED_FOR);
/* 486 */         if (current == null || current.isEmpty())
/*     */         {
/* 488 */           request.getRequestHeaders().put(Headers.X_FORWARDED_FOR, remoteHost);
/*     */         }
/*     */         else
/*     */         {
/* 492 */           request.getRequestHeaders().put(Headers.X_FORWARDED_FOR, current + "," + remoteHost);
/*     */         }
/*     */       
/*     */       } else {
/*     */         
/* 497 */         request.getRequestHeaders().put(Headers.X_FORWARDED_FOR, remoteHost);
/*     */       } 
/*     */ 
/*     */ 
/*     */       
/* 502 */       if (!this.exchange.getConnection().isPushSupported() && this.clientConnection.getConnection().isPushSupported()) {
/* 503 */         request.getRequestHeaders().put(Headers.X_DISABLE_PUSH, "true");
/*     */       }
/*     */ 
/*     */       
/* 507 */       if (this.reuseXForwarded && this.exchange.getRequestHeaders().contains(Headers.X_FORWARDED_PROTO)) {
/* 508 */         String proto = this.exchange.getRequestHeaders().getFirst(Headers.X_FORWARDED_PROTO);
/* 509 */         request.putAttachment(ProxiedRequestAttachments.IS_SSL, Boolean.valueOf(proto.equals("https")));
/*     */       } else {
/* 511 */         String proto = this.exchange.getRequestScheme().equals("https") ? "https" : "http";
/* 512 */         request.getRequestHeaders().put(Headers.X_FORWARDED_PROTO, proto);
/* 513 */         request.putAttachment(ProxiedRequestAttachments.IS_SSL, Boolean.valueOf(proto.equals("https")));
/*     */       } 
/*     */ 
/*     */       
/* 517 */       if (this.reuseXForwarded && this.exchange.getRequestHeaders().contains(Headers.X_FORWARDED_SERVER)) {
/* 518 */         String hostName = this.exchange.getRequestHeaders().getFirst(Headers.X_FORWARDED_SERVER);
/* 519 */         request.putAttachment(ProxiedRequestAttachments.SERVER_NAME, hostName);
/*     */       } else {
/* 521 */         String hostName = this.exchange.getHostName();
/* 522 */         request.getRequestHeaders().put(Headers.X_FORWARDED_SERVER, hostName);
/* 523 */         request.putAttachment(ProxiedRequestAttachments.SERVER_NAME, hostName);
/*     */       } 
/* 525 */       if (!this.exchange.getRequestHeaders().contains(Headers.X_FORWARDED_HOST)) {
/* 526 */         String hostName = this.exchange.getHostName();
/* 527 */         if (hostName != null) {
/* 528 */           request.getRequestHeaders().put(Headers.X_FORWARDED_HOST, NetworkUtils.formatPossibleIpv6Address(hostName));
/*     */         }
/*     */       } 
/*     */ 
/*     */       
/* 533 */       if (this.reuseXForwarded && this.exchange.getRequestHeaders().contains(Headers.X_FORWARDED_PORT)) {
/*     */         try {
/* 535 */           int port = Integer.parseInt(this.exchange.getRequestHeaders().getFirst(Headers.X_FORWARDED_PORT));
/* 536 */           request.putAttachment(ProxiedRequestAttachments.SERVER_PORT, Integer.valueOf(port));
/* 537 */         } catch (NumberFormatException e) {
/* 538 */           int port = ((InetSocketAddress)this.exchange.getConnection().getLocalAddress(InetSocketAddress.class)).getPort();
/* 539 */           request.getRequestHeaders().put(Headers.X_FORWARDED_PORT, port);
/* 540 */           request.putAttachment(ProxiedRequestAttachments.SERVER_PORT, Integer.valueOf(port));
/*     */         } 
/*     */       } else {
/* 543 */         int port = this.exchange.getHostPort();
/* 544 */         request.getRequestHeaders().put(Headers.X_FORWARDED_PORT, port);
/* 545 */         request.putAttachment(ProxiedRequestAttachments.SERVER_PORT, Integer.valueOf(port));
/*     */       } 
/*     */       
/* 548 */       SSLSessionInfo sslSessionInfo = this.exchange.getConnection().getSslSessionInfo();
/* 549 */       if (sslSessionInfo != null) {
/*     */         
/*     */         try {
/* 552 */           Certificate[] peerCertificates = sslSessionInfo.getPeerCertificates();
/* 553 */           if (peerCertificates.length > 0) {
/* 554 */             request.putAttachment(ProxiedRequestAttachments.SSL_CERT, Certificates.toPem(peerCertificates[0]));
/*     */           }
/* 556 */         } catch (SSLPeerUnverifiedException|java.security.cert.CertificateEncodingException|io.undertow.server.RenegotiationRequiredException sSLPeerUnverifiedException) {}
/*     */ 
/*     */         
/* 559 */         request.putAttachment(ProxiedRequestAttachments.SSL_CYPHER, sslSessionInfo.getCipherSuite());
/* 560 */         request.putAttachment(ProxiedRequestAttachments.SSL_SESSION_ID, sslSessionInfo.getSessionId());
/* 561 */         request.putAttachment(ProxiedRequestAttachments.SSL_KEY_SIZE, Integer.valueOf(sslSessionInfo.getKeySize()));
/*     */       } 
/*     */       
/* 564 */       if (this.rewriteHostHeader) {
/* 565 */         InetSocketAddress targetAddress = (InetSocketAddress)this.clientConnection.getConnection().getPeerAddress(InetSocketAddress.class);
/* 566 */         request.getRequestHeaders().put(Headers.HOST, targetAddress.getHostString() + ":" + targetAddress.getPort());
/* 567 */         request.getRequestHeaders().put(Headers.X_FORWARDED_HOST, this.exchange.getRequestHeaders().getFirst(Headers.HOST));
/*     */       } 
/* 569 */       if (ProxyHandler.log.isDebugEnabled()) {
/* 570 */         ProxyHandler.log.debugf("Sending request %s to target %s for exchange %s", request, this.clientConnection.getConnection().getPeerAddress(), this.exchange);
/*     */       }
/*     */ 
/*     */ 
/*     */       
/* 575 */       if (!request.getRequestHeaders().contains(Headers.TRANSFER_ENCODING) && !request.getRequestHeaders().contains(Headers.CONTENT_LENGTH) && 
/* 576 */         !this.exchange.isRequestComplete()) {
/* 577 */         request.getRequestHeaders().put(Headers.TRANSFER_ENCODING, Headers.CHUNKED.toString());
/*     */       }
/*     */ 
/*     */ 
/*     */       
/* 582 */       this.clientConnection.getConnection().sendRequest(request, new ClientCallback<ClientExchange>()
/*     */           {
/*     */             public void completed(final ClientExchange result)
/*     */             {
/* 586 */               if (ProxyHandler.log.isDebugEnabled()) {
/* 587 */                 ProxyHandler.log.debugf("Sent request %s to target %s for exchange %s", request, remoteHost, ProxyHandler.ProxyAction.this.exchange);
/*     */               }
/* 589 */               result.putAttachment(ProxyHandler.EXCHANGE, ProxyHandler.ProxyAction.this.exchange);
/*     */               
/* 591 */               boolean requiresContinueResponse = HttpContinue.requiresContinueResponse(ProxyHandler.ProxyAction.this.exchange);
/* 592 */               if (requiresContinueResponse) {
/* 593 */                 result.setContinueHandler(new ContinueNotification()
/*     */                     {
/*     */                       public void handleContinue(ClientExchange clientExchange) {
/* 596 */                         if (ProxyHandler.log.isDebugEnabled()) {
/* 597 */                           ProxyHandler.log.debugf("Received continue response to request %s to target %s for exchange %s", request, ProxyHandler.ProxyAction.this.clientConnection.getConnection().getPeerAddress(), ProxyHandler.ProxyAction.this.exchange);
/*     */                         }
/* 599 */                         HttpContinue.sendContinueResponse(ProxyHandler.ProxyAction.this.exchange, new IoCallback()
/*     */                             {
/*     */                               public void onComplete(HttpServerExchange exchange, Sender sender) {}
/*     */ 
/*     */ 
/*     */ 
/*     */                               
/*     */                               public void onException(HttpServerExchange exchange, Sender sender, IOException exception) {
/* 607 */                                 IoUtils.safeClose((Closeable)ProxyHandler.ProxyAction.this.clientConnection.getConnection());
/* 608 */                                 exchange.endExchange();
/* 609 */                                 UndertowLogger.REQUEST_IO_LOGGER.ioException(exception);
/*     */                               }
/*     */                             });
/*     */                       }
/*     */                     });
/*     */               }
/*     */ 
/*     */               
/* 617 */               if (ProxyHandler.ProxyAction.this.exchange.getConnection().isPushSupported() && result.getConnection().isPushSupported()) {
/* 618 */                 result.setPushHandler(new PushCallback()
/*     */                     {
/*     */                       public boolean handlePush(ClientExchange originalRequest, final ClientExchange pushedRequest)
/*     */                       {
/* 622 */                         if (ProxyHandler.log.isDebugEnabled()) {
/* 623 */                           ProxyHandler.log.debugf("Sending push request %s received from %s to target %s for exchange %s", new Object[] { pushedRequest.getRequest(), this.this$1.val$request, this.this$1.val$remoteHost, ProxyHandler.ProxyAction.access$1400(this.this$1.this$0) });
/*     */                         }
/* 625 */                         final ClientRequest request = pushedRequest.getRequest();
/* 626 */                         ProxyHandler.ProxyAction.this.exchange.getConnection().pushResource(request.getPath(), request.getMethod(), request.getRequestHeaders(), new HttpHandler()
/*     */                             {
/*     */                               public void handleRequest(HttpServerExchange exchange) throws Exception {
/* 629 */                                 String path = request.getPath();
/* 630 */                                 int i = path.indexOf("?");
/* 631 */                                 if (i > 0) {
/* 632 */                                   path = path.substring(0, i);
/*     */                                 }
/*     */                                 
/* 635 */                                 exchange.dispatch(SameThreadExecutor.INSTANCE, new ProxyHandler.ProxyAction(new ProxyConnection(pushedRequest.getConnection(), path), exchange, ProxyHandler.ProxyAction.this.requestHeaders, ProxyHandler.ProxyAction.this.rewriteHostHeader, ProxyHandler.ProxyAction.this.reuseXForwarded, null, ProxyHandler.ProxyAction.this.idempotentPredicate));
/*     */                               }
/*     */                             });
/* 638 */                         return true;
/*     */                       }
/*     */                     });
/*     */               }
/*     */ 
/*     */               
/* 644 */               result.setResponseListener(new ProxyHandler.ResponseCallback(ProxyHandler.ProxyAction.this.exchange, ProxyHandler.ProxyAction.this.proxyClientHandler, ProxyHandler.ProxyAction.this.idempotentPredicate));
/* 645 */               final ProxyHandler.IoExceptionHandler handler = new ProxyHandler.IoExceptionHandler(ProxyHandler.ProxyAction.this.exchange, ProxyHandler.ProxyAction.this.clientConnection.getConnection());
/* 646 */               if (requiresContinueResponse) {
/*     */                 try {
/* 648 */                   if (!result.getRequestChannel().flush()) {
/* 649 */                     result.getRequestChannel().getWriteSetter().set(ChannelListeners.flushingChannelListener(new ChannelListener<StreamSinkChannel>()
/*     */                           {
/*     */                             public void handleEvent(StreamSinkChannel channel) {
/* 652 */                               Transfer.initiateTransfer(ProxyHandler.ProxyAction.this.exchange.getRequestChannel(), result.getRequestChannel(), ChannelListeners.closingChannelListener(), new ProxyHandler.HTTPTrailerChannelListener((Attachable)ProxyHandler.ProxyAction.this.exchange, (Attachable)result, ProxyHandler.ProxyAction.this.exchange, ProxyHandler.ProxyAction.this.proxyClientHandler, ProxyHandler.ProxyAction.this.idempotentPredicate), handler, handler, ProxyHandler.ProxyAction.this.exchange.getConnection().getByteBufferPool());
/*     */                             }
/*     */                           }handler));
/*     */                     
/* 656 */                     result.getRequestChannel().resumeWrites();
/*     */                     return;
/*     */                   } 
/* 659 */                 } catch (IOException e) {
/* 660 */                   handler.handleException((Channel)result.getRequestChannel(), e);
/*     */                 } 
/*     */               }
/* 663 */               ProxyHandler.HTTPTrailerChannelListener trailerListener = new ProxyHandler.HTTPTrailerChannelListener((Attachable)ProxyHandler.ProxyAction.this.exchange, (Attachable)result, ProxyHandler.ProxyAction.this.exchange, ProxyHandler.ProxyAction.this.proxyClientHandler, ProxyHandler.ProxyAction.this.idempotentPredicate);
/* 664 */               if (!ProxyHandler.ProxyAction.this.exchange.isRequestComplete()) {
/* 665 */                 Transfer.initiateTransfer(ProxyHandler.ProxyAction.this.exchange.getRequestChannel(), result.getRequestChannel(), ChannelListeners.closingChannelListener(), trailerListener, handler, handler, ProxyHandler.ProxyAction.this.exchange.getConnection().getByteBufferPool());
/*     */               } else {
/* 667 */                 trailerListener.handleEvent(result.getRequestChannel());
/*     */               } 
/*     */             }
/*     */ 
/*     */ 
/*     */             
/*     */             public void failed(IOException e) {
/* 674 */               ProxyHandler.handleFailure(ProxyHandler.ProxyAction.this.exchange, ProxyHandler.ProxyAction.this.proxyClientHandler, ProxyHandler.ProxyAction.this.idempotentPredicate, e);
/*     */             }
/*     */           });
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   static void handleFailure(HttpServerExchange exchange, ProxyClientHandler proxyClientHandler, Predicate idempotentRequestPredicate, IOException e) {
/* 683 */     UndertowLogger.PROXY_REQUEST_LOGGER.proxyRequestFailed(exchange.getRequestURI(), e);
/* 684 */     if (exchange.isResponseStarted()) {
/* 685 */       IoUtils.safeClose((Closeable)exchange.getConnection());
/* 686 */     } else if (idempotentRequestPredicate.resolve(exchange) && proxyClientHandler != null) {
/* 687 */       proxyClientHandler.failed(exchange);
/*     */     } else {
/* 689 */       exchange.setStatusCode(503);
/* 690 */       exchange.endExchange();
/*     */     } 
/*     */   }
/*     */   
/*     */   private static final class ResponseCallback
/*     */     implements ClientCallback<ClientExchange> {
/*     */     private final HttpServerExchange exchange;
/*     */     private final ProxyHandler.ProxyClientHandler proxyClientHandler;
/*     */     private final Predicate idempotentPredicate;
/*     */     
/*     */     private ResponseCallback(HttpServerExchange exchange, ProxyHandler.ProxyClientHandler proxyClientHandler, Predicate idempotentPredicate) {
/* 701 */       this.exchange = exchange;
/* 702 */       this.proxyClientHandler = proxyClientHandler;
/* 703 */       this.idempotentPredicate = idempotentPredicate;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public void completed(final ClientExchange result) {
/* 709 */       ClientResponse response = result.getResponse();
/*     */       
/* 711 */       if (ProxyHandler.log.isDebugEnabled()) {
/* 712 */         ProxyHandler.log.debugf("Received response %s for request %s for exchange %s", response, result.getRequest(), this.exchange);
/*     */       }
/* 714 */       HeaderMap inboundResponseHeaders = response.getResponseHeaders();
/* 715 */       HeaderMap outboundResponseHeaders = this.exchange.getResponseHeaders();
/* 716 */       this.exchange.setStatusCode(response.getResponseCode());
/* 717 */       ProxyHandler.copyHeaders(outboundResponseHeaders, inboundResponseHeaders);
/*     */       
/* 719 */       if (this.exchange.isUpgrade())
/*     */       {
/* 721 */         this.exchange.upgradeChannel(new HttpUpgradeListener()
/*     */             {
/*     */               public void handleUpgrade(StreamConnection streamConnection, HttpServerExchange exchange)
/*     */               {
/* 725 */                 if (ProxyHandler.log.isDebugEnabled()) {
/* 726 */                   ProxyHandler.log.debugf("Upgraded request %s to for exchange %s", result.getRequest(), exchange);
/*     */                 }
/* 728 */                 StreamConnection clientChannel = null;
/*     */                 try {
/* 730 */                   clientChannel = result.getConnection().performUpgrade();
/*     */                   
/* 732 */                   ProxyHandler.ClosingExceptionHandler handler = new ProxyHandler.ClosingExceptionHandler(new Closeable[] { (Closeable)streamConnection, (Closeable)clientChannel });
/* 733 */                   Transfer.initiateTransfer((StreamSourceChannel)clientChannel.getSourceChannel(), (StreamSinkChannel)streamConnection.getSinkChannel(), ChannelListeners.closingChannelListener(), ChannelListeners.writeShutdownChannelListener(ChannelListeners.flushingChannelListener(ChannelListeners.closingChannelListener(), ChannelListeners.closingChannelExceptionHandler()), ChannelListeners.closingChannelExceptionHandler()), handler, handler, result.getConnection().getBufferPool());
/* 734 */                   Transfer.initiateTransfer((StreamSourceChannel)streamConnection.getSourceChannel(), (StreamSinkChannel)clientChannel.getSinkChannel(), ChannelListeners.closingChannelListener(), ChannelListeners.writeShutdownChannelListener(ChannelListeners.flushingChannelListener(ChannelListeners.closingChannelListener(), ChannelListeners.closingChannelExceptionHandler()), ChannelListeners.closingChannelExceptionHandler()), handler, handler, result.getConnection().getBufferPool());
/*     */                 }
/* 736 */                 catch (IOException e) {
/* 737 */                   IoUtils.safeClose(new Closeable[] { (Closeable)streamConnection, (Closeable)clientChannel });
/*     */                 } 
/*     */               }
/*     */             });
/*     */       }
/* 742 */       ProxyHandler.IoExceptionHandler handler = new ProxyHandler.IoExceptionHandler(this.exchange, result.getConnection());
/* 743 */       Transfer.initiateTransfer(result.getResponseChannel(), this.exchange.getResponseChannel(), ChannelListeners.closingChannelListener(), new ProxyHandler.HTTPTrailerChannelListener((Attachable)result, (Attachable)this.exchange, this.exchange, this.proxyClientHandler, this.idempotentPredicate), handler, handler, this.exchange.getConnection().getByteBufferPool());
/*     */     }
/*     */ 
/*     */     
/*     */     public void failed(IOException e) {
/* 748 */       ProxyHandler.handleFailure(this.exchange, this.proxyClientHandler, this.idempotentPredicate, e);
/*     */     }
/*     */   }
/*     */   
/*     */   private static final class HTTPTrailerChannelListener
/*     */     implements ChannelListener<StreamSinkChannel> {
/*     */     private final Attachable source;
/*     */     private final Attachable target;
/*     */     private final HttpServerExchange exchange;
/*     */     private final ProxyHandler.ProxyClientHandler proxyClientHandler;
/*     */     private final Predicate idempotentPredicate;
/*     */     
/*     */     private HTTPTrailerChannelListener(Attachable source, Attachable target, HttpServerExchange exchange, ProxyHandler.ProxyClientHandler proxyClientHandler, Predicate idempotentPredicate) {
/* 761 */       this.source = source;
/* 762 */       this.target = target;
/* 763 */       this.exchange = exchange;
/* 764 */       this.proxyClientHandler = proxyClientHandler;
/* 765 */       this.idempotentPredicate = idempotentPredicate;
/*     */     }
/*     */ 
/*     */     
/*     */     public void handleEvent(StreamSinkChannel channel) {
/* 770 */       HeaderMap trailers = (HeaderMap)this.source.getAttachment(HttpAttachments.REQUEST_TRAILERS);
/* 771 */       if (trailers != null) {
/* 772 */         this.target.putAttachment(HttpAttachments.RESPONSE_TRAILERS, trailers);
/*     */       }
/*     */       try {
/* 775 */         channel.shutdownWrites();
/* 776 */         if (!channel.flush()) {
/* 777 */           channel.getWriteSetter().set(ChannelListeners.flushingChannelListener(new ChannelListener<StreamSinkChannel>()
/*     */                 {
/*     */                   public void handleEvent(StreamSinkChannel channel) {
/* 780 */                     channel.suspendWrites();
/* 781 */                     channel.getWriteSetter().set(null);
/*     */                   }
/* 783 */                 },  ChannelListeners.closingChannelExceptionHandler()));
/* 784 */           channel.resumeWrites();
/*     */         } else {
/* 786 */           channel.getWriteSetter().set(null);
/* 787 */           channel.shutdownWrites();
/*     */         } 
/* 789 */       } catch (IOException e) {
/* 790 */         ProxyHandler.handleFailure(this.exchange, this.proxyClientHandler, this.idempotentPredicate, e);
/* 791 */       } catch (Exception e) {
/* 792 */         ProxyHandler.handleFailure(this.exchange, this.proxyClientHandler, this.idempotentPredicate, new IOException(e));
/*     */       } 
/*     */     }
/*     */   }
/*     */   
/*     */   private static final class IoExceptionHandler
/*     */     implements ChannelExceptionHandler<Channel>
/*     */   {
/*     */     private final HttpServerExchange exchange;
/*     */     private final ClientConnection clientConnection;
/*     */     
/*     */     private IoExceptionHandler(HttpServerExchange exchange, ClientConnection clientConnection) {
/* 804 */       this.exchange = exchange;
/* 805 */       this.clientConnection = clientConnection;
/*     */     }
/*     */ 
/*     */     
/*     */     public void handleException(Channel channel, IOException exception) {
/* 810 */       IoUtils.safeClose(channel);
/* 811 */       IoUtils.safeClose((Closeable)this.clientConnection);
/* 812 */       if (this.exchange.isResponseStarted()) {
/* 813 */         UndertowLogger.REQUEST_IO_LOGGER.debug("Exception reading from target server", exception);
/* 814 */         if (!this.exchange.isResponseStarted()) {
/* 815 */           this.exchange.setStatusCode(500);
/* 816 */           this.exchange.endExchange();
/*     */         } else {
/* 818 */           IoUtils.safeClose((Closeable)this.exchange.getConnection());
/*     */         } 
/*     */       } else {
/* 821 */         UndertowLogger.REQUEST_IO_LOGGER.ioException(exception);
/* 822 */         this.exchange.setStatusCode(500);
/* 823 */         this.exchange.endExchange();
/*     */       } 
/*     */     }
/*     */   }
/*     */   
/*     */   @Deprecated
/*     */   public ProxyHandler setMaxConnectionRetries(int maxConnectionRetries) {
/* 830 */     this.maxConnectionRetries = maxConnectionRetries;
/* 831 */     return this;
/*     */   }
/*     */   
/*     */   public boolean isRewriteHostHeader() {
/* 835 */     return this.rewriteHostHeader;
/*     */   }
/*     */   
/*     */   @Deprecated
/*     */   public ProxyHandler setRewriteHostHeader(boolean rewriteHostHeader) {
/* 840 */     this.rewriteHostHeader = rewriteHostHeader;
/* 841 */     return this;
/*     */   }
/*     */   
/*     */   public boolean isReuseXForwarded() {
/* 845 */     return this.reuseXForwarded;
/*     */   }
/*     */   
/*     */   @Deprecated
/*     */   public ProxyHandler setReuseXForwarded(boolean reuseXForwarded) {
/* 850 */     this.reuseXForwarded = reuseXForwarded;
/* 851 */     return this;
/*     */   }
/*     */   
/*     */   public int getMaxConnectionRetries() {
/* 855 */     return this.maxConnectionRetries;
/*     */   }
/*     */   
/*     */   public Predicate getIdempotentRequestPredicate() {
/* 859 */     return this.idempotentRequestPredicate;
/*     */   }
/*     */   
/*     */   private static final class ClosingExceptionHandler
/*     */     implements ChannelExceptionHandler<Channel> {
/*     */     private final Closeable[] toClose;
/*     */     
/*     */     private ClosingExceptionHandler(Closeable... toClose) {
/* 867 */       this.toClose = toClose;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public void handleException(Channel channel, IOException exception) {
/* 873 */       IoUtils.safeClose(channel);
/* 874 */       IoUtils.safeClose(this.toClose);
/*     */     }
/*     */   }
/*     */   
/*     */   public static Builder builder() {
/* 879 */     return new Builder();
/*     */   }
/*     */   
/*     */   public static class Builder
/*     */   {
/*     */     private ProxyClient proxyClient;
/* 885 */     private int maxRequestTime = -1;
/* 886 */     private final Map<HttpString, ExchangeAttribute> requestHeaders = (Map<HttpString, ExchangeAttribute>)new CopyOnWriteMap();
/* 887 */     private HttpHandler next = (HttpHandler)ResponseCodeHandler.HANDLE_404;
/*     */     private boolean rewriteHostHeader;
/*     */     private boolean reuseXForwarded;
/* 890 */     private int maxConnectionRetries = ProxyHandler.DEFAULT_MAX_RETRY_ATTEMPTS;
/* 891 */     private Predicate idempotentRequestPredicate = (Predicate)IdempotentPredicate.INSTANCE;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public ProxyClient getProxyClient() {
/* 897 */       return this.proxyClient;
/*     */     }
/*     */     
/*     */     public Builder setProxyClient(ProxyClient proxyClient) {
/* 901 */       if (proxyClient == null) {
/* 902 */         throw UndertowMessages.MESSAGES.argumentCannotBeNull("proxyClient");
/*     */       }
/* 904 */       this.proxyClient = proxyClient;
/* 905 */       return this;
/*     */     }
/*     */     
/*     */     public int getMaxRequestTime() {
/* 909 */       return this.maxRequestTime;
/*     */     }
/*     */     
/*     */     public Builder setMaxRequestTime(int maxRequestTime) {
/* 913 */       this.maxRequestTime = maxRequestTime;
/* 914 */       return this;
/*     */     }
/*     */     
/*     */     public Map<HttpString, ExchangeAttribute> getRequestHeaders() {
/* 918 */       return Collections.unmodifiableMap(this.requestHeaders);
/*     */     }
/*     */     
/*     */     public Builder addRequestHeader(HttpString header, ExchangeAttribute value) {
/* 922 */       this.requestHeaders.put(header, value);
/* 923 */       return this;
/*     */     }
/*     */     
/*     */     public HttpHandler getNext() {
/* 927 */       return this.next;
/*     */     }
/*     */     
/*     */     public Builder setNext(HttpHandler next) {
/* 931 */       this.next = next;
/* 932 */       return this;
/*     */     }
/*     */     
/*     */     public boolean isRewriteHostHeader() {
/* 936 */       return this.rewriteHostHeader;
/*     */     }
/*     */     
/*     */     public Builder setRewriteHostHeader(boolean rewriteHostHeader) {
/* 940 */       this.rewriteHostHeader = rewriteHostHeader;
/* 941 */       return this;
/*     */     }
/*     */     
/*     */     public boolean isReuseXForwarded() {
/* 945 */       return this.reuseXForwarded;
/*     */     }
/*     */     
/*     */     public Builder setReuseXForwarded(boolean reuseXForwarded) {
/* 949 */       this.reuseXForwarded = reuseXForwarded;
/* 950 */       return this;
/*     */     }
/*     */     
/*     */     public int getMaxConnectionRetries() {
/* 954 */       return this.maxConnectionRetries;
/*     */     }
/*     */     
/*     */     public Builder setMaxConnectionRetries(int maxConnectionRetries) {
/* 958 */       this.maxConnectionRetries = maxConnectionRetries;
/* 959 */       return this;
/*     */     }
/*     */     
/*     */     public Predicate getIdempotentRequestPredicate() {
/* 963 */       return this.idempotentRequestPredicate;
/*     */     }
/*     */     
/*     */     public Builder setIdempotentRequestPredicate(Predicate idempotentRequestPredicate) {
/* 967 */       if (idempotentRequestPredicate == null) {
/* 968 */         throw UndertowMessages.MESSAGES.argumentCannotBeNull("idempotentRequestPredicate");
/*     */       }
/* 970 */       this.idempotentRequestPredicate = idempotentRequestPredicate;
/* 971 */       return this;
/*     */     }
/*     */     
/*     */     public ProxyHandler build() {
/* 975 */       return new ProxyHandler(this);
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\handlers\proxy\ProxyHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */