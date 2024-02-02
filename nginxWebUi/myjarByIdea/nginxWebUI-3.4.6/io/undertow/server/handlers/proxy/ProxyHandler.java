package io.undertow.server.handlers.proxy;

import io.undertow.UndertowLogger;
import io.undertow.UndertowMessages;
import io.undertow.attribute.ExchangeAttribute;
import io.undertow.attribute.ExchangeAttributes;
import io.undertow.client.ClientCallback;
import io.undertow.client.ClientConnection;
import io.undertow.client.ClientExchange;
import io.undertow.client.ClientRequest;
import io.undertow.client.ClientResponse;
import io.undertow.client.ContinueNotification;
import io.undertow.client.ProxiedRequestAttachments;
import io.undertow.client.PushCallback;
import io.undertow.io.IoCallback;
import io.undertow.io.Sender;
import io.undertow.predicate.IdempotentPredicate;
import io.undertow.predicate.Predicate;
import io.undertow.server.ExchangeCompletionListener;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.HttpUpgradeListener;
import io.undertow.server.RenegotiationRequiredException;
import io.undertow.server.SSLSessionInfo;
import io.undertow.server.handlers.ResponseCodeHandler;
import io.undertow.server.protocol.http.HttpAttachments;
import io.undertow.server.protocol.http.HttpContinue;
import io.undertow.util.Attachable;
import io.undertow.util.AttachmentKey;
import io.undertow.util.Certificates;
import io.undertow.util.CopyOnWriteMap;
import io.undertow.util.HeaderMap;
import io.undertow.util.HeaderValues;
import io.undertow.util.Headers;
import io.undertow.util.HttpString;
import io.undertow.util.NetworkUtils;
import io.undertow.util.SameThreadExecutor;
import io.undertow.util.Transfer;
import io.undertow.util.WorkerUtils;
import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.Channel;
import java.nio.charset.StandardCharsets;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import javax.net.ssl.SSLPeerUnverifiedException;
import org.jboss.logging.Logger;
import org.xnio.ChannelExceptionHandler;
import org.xnio.ChannelListener;
import org.xnio.ChannelListeners;
import org.xnio.IoUtils;
import org.xnio.StreamConnection;
import org.xnio.XnioExecutor;
import org.xnio.channels.StreamSinkChannel;

public final class ProxyHandler implements HttpHandler {
   private static final int DEFAULT_MAX_RETRY_ATTEMPTS = Integer.getInteger("io.undertow.server.handlers.proxy.maxRetries", 1);
   private static final Logger log = Logger.getLogger(ProxyHandler.class.getPackage().getName());
   public static final String UTF_8;
   private static final AttachmentKey<ProxyConnection> CONNECTION;
   private static final AttachmentKey<HttpServerExchange> EXCHANGE;
   private static final AttachmentKey<XnioExecutor.Key> TIMEOUT_KEY;
   private final ProxyClient proxyClient;
   private final int maxRequestTime;
   private final Map<HttpString, ExchangeAttribute> requestHeaders;
   private final HttpHandler next;
   private volatile boolean rewriteHostHeader;
   private volatile boolean reuseXForwarded;
   private volatile int maxConnectionRetries;
   private final Predicate idempotentRequestPredicate;

   /** @deprecated */
   @Deprecated
   public ProxyHandler(ProxyClient proxyClient, int maxRequestTime, HttpHandler next) {
      this(proxyClient, maxRequestTime, next, false, false);
   }

   /** @deprecated */
   @Deprecated
   public ProxyHandler(ProxyClient proxyClient, int maxRequestTime, HttpHandler next, boolean rewriteHostHeader, boolean reuseXForwarded) {
      this(proxyClient, maxRequestTime, next, rewriteHostHeader, reuseXForwarded, DEFAULT_MAX_RETRY_ATTEMPTS);
   }

   /** @deprecated */
   @Deprecated
   public ProxyHandler(ProxyClient proxyClient, int maxRequestTime, HttpHandler next, boolean rewriteHostHeader, boolean reuseXForwarded, int maxConnectionRetries) {
      this.requestHeaders = new CopyOnWriteMap();
      this.proxyClient = proxyClient;
      this.maxRequestTime = maxRequestTime;
      this.next = next;
      this.rewriteHostHeader = rewriteHostHeader;
      this.reuseXForwarded = reuseXForwarded;
      this.maxConnectionRetries = maxConnectionRetries;
      this.idempotentRequestPredicate = IdempotentPredicate.INSTANCE;
   }

   /** @deprecated */
   @Deprecated
   public ProxyHandler(ProxyClient proxyClient, HttpHandler next) {
      this(proxyClient, -1, next);
   }

   ProxyHandler(Builder builder) {
      this.requestHeaders = new CopyOnWriteMap();
      this.proxyClient = builder.proxyClient;
      this.maxRequestTime = builder.maxRequestTime;
      this.next = builder.next;
      this.rewriteHostHeader = builder.rewriteHostHeader;
      this.reuseXForwarded = builder.reuseXForwarded;
      this.maxConnectionRetries = builder.maxConnectionRetries;
      this.idempotentRequestPredicate = builder.idempotentRequestPredicate;
      Iterator var2 = builder.requestHeaders.entrySet().iterator();

      while(var2.hasNext()) {
         Map.Entry<HttpString, ExchangeAttribute> e = (Map.Entry)var2.next();
         this.requestHeaders.put(e.getKey(), e.getValue());
      }

   }

   public void handleRequest(final HttpServerExchange exchange) throws Exception {
      ProxyClient.ProxyTarget target = this.proxyClient.findTarget(exchange);
      if (target == null) {
         log.debugf("No proxy target for request to %s", (Object)exchange.getRequestURL());
         this.next.handleRequest(exchange);
      } else if (exchange.isResponseStarted()) {
         UndertowLogger.REQUEST_LOGGER.cannotProxyStartedRequest(exchange);
         exchange.setStatusCode(500);
         exchange.endExchange();
      } else {
         long timeout = this.maxRequestTime > 0 ? System.currentTimeMillis() + (long)this.maxRequestTime : 0L;
         int maxRetries = this.maxConnectionRetries;
         if (target instanceof ProxyClient.MaxRetriesProxyTarget) {
            maxRetries = Math.max(maxRetries, ((ProxyClient.MaxRetriesProxyTarget)target).getMaxRetries());
         }

         final ProxyClientHandler clientHandler = new ProxyClientHandler(exchange, target, timeout, maxRetries, this.idempotentRequestPredicate);
         if (timeout > 0L) {
            final XnioExecutor.Key key = WorkerUtils.executeAfter(exchange.getIoThread(), new Runnable() {
               public void run() {
                  clientHandler.cancel(exchange);
               }
            }, (long)this.maxRequestTime, TimeUnit.MILLISECONDS);
            exchange.putAttachment(TIMEOUT_KEY, key);
            exchange.addExchangeCompleteListener(new ExchangeCompletionListener() {
               public void exchangeEvent(HttpServerExchange exchange, ExchangeCompletionListener.NextListener nextListener) {
                  key.remove();
                  nextListener.proceed();
               }
            });
         }

         exchange.dispatch((Executor)(exchange.isInIoThread() ? SameThreadExecutor.INSTANCE : exchange.getIoThread()), (Runnable)clientHandler);
      }
   }

   /** @deprecated */
   @Deprecated
   public ProxyHandler addRequestHeader(HttpString header, ExchangeAttribute attribute) {
      this.requestHeaders.put(header, attribute);
      return this;
   }

   /** @deprecated */
   @Deprecated
   public ProxyHandler addRequestHeader(HttpString header, String value) {
      this.requestHeaders.put(header, ExchangeAttributes.constant(value));
      return this;
   }

   /** @deprecated */
   @Deprecated
   public ProxyHandler addRequestHeader(HttpString header, String attribute, ClassLoader classLoader) {
      this.requestHeaders.put(header, ExchangeAttributes.parser(classLoader).parse(attribute));
      return this;
   }

   /** @deprecated */
   @Deprecated
   public ProxyHandler removeRequestHeader(HttpString header) {
      this.requestHeaders.remove(header);
      return this;
   }

   static void copyHeaders(HeaderMap to, HeaderMap from) {
      for(long f = from.fastIterateNonEmpty(); f != -1L; f = from.fiNextNonEmpty(f)) {
         HeaderValues values = from.fiCurrent(f);
         if (!to.contains(values.getHeaderName())) {
            to.putAll(values.getHeaderName(), values);
         }
      }

   }

   public ProxyClient getProxyClient() {
      return this.proxyClient;
   }

   public String toString() {
      List<ProxyClient.ProxyTarget> proxyTargets = this.proxyClient.getAllTargets();
      if (proxyTargets.isEmpty()) {
         return "ProxyHandler - " + this.proxyClient.getClass().getSimpleName();
      } else if (proxyTargets.size() == 1 && !this.rewriteHostHeader) {
         return "reverse-proxy( '" + ((ProxyClient.ProxyTarget)proxyTargets.get(0)).toString() + "' )";
      } else {
         String outputResult = "reverse-proxy( { '" + (String)proxyTargets.stream().map((s) -> {
            return s.toString();
         }).collect(Collectors.joining("', '")) + "' }";
         if (this.rewriteHostHeader) {
            outputResult = outputResult + ", rewrite-host-header=true";
         }

         return outputResult + " )";
      }
   }

   static void handleFailure(HttpServerExchange exchange, ProxyClientHandler proxyClientHandler, Predicate idempotentRequestPredicate, IOException e) {
      UndertowLogger.PROXY_REQUEST_LOGGER.proxyRequestFailed(exchange.getRequestURI(), e);
      if (exchange.isResponseStarted()) {
         IoUtils.safeClose((Closeable)exchange.getConnection());
      } else if (idempotentRequestPredicate.resolve(exchange) && proxyClientHandler != null) {
         proxyClientHandler.failed(exchange);
      } else {
         exchange.setStatusCode(503);
         exchange.endExchange();
      }

   }

   /** @deprecated */
   @Deprecated
   public ProxyHandler setMaxConnectionRetries(int maxConnectionRetries) {
      this.maxConnectionRetries = maxConnectionRetries;
      return this;
   }

   public boolean isRewriteHostHeader() {
      return this.rewriteHostHeader;
   }

   /** @deprecated */
   @Deprecated
   public ProxyHandler setRewriteHostHeader(boolean rewriteHostHeader) {
      this.rewriteHostHeader = rewriteHostHeader;
      return this;
   }

   public boolean isReuseXForwarded() {
      return this.reuseXForwarded;
   }

   /** @deprecated */
   @Deprecated
   public ProxyHandler setReuseXForwarded(boolean reuseXForwarded) {
      this.reuseXForwarded = reuseXForwarded;
      return this;
   }

   public int getMaxConnectionRetries() {
      return this.maxConnectionRetries;
   }

   public Predicate getIdempotentRequestPredicate() {
      return this.idempotentRequestPredicate;
   }

   public static Builder builder() {
      return new Builder();
   }

   static {
      UTF_8 = StandardCharsets.UTF_8.name();
      CONNECTION = AttachmentKey.create(ProxyConnection.class);
      EXCHANGE = AttachmentKey.create(HttpServerExchange.class);
      TIMEOUT_KEY = AttachmentKey.create(XnioExecutor.Key.class);
   }

   public static class Builder {
      private ProxyClient proxyClient;
      private int maxRequestTime = -1;
      private final Map<HttpString, ExchangeAttribute> requestHeaders = new CopyOnWriteMap();
      private HttpHandler next;
      private boolean rewriteHostHeader;
      private boolean reuseXForwarded;
      private int maxConnectionRetries;
      private Predicate idempotentRequestPredicate;

      Builder() {
         this.next = ResponseCodeHandler.HANDLE_404;
         this.maxConnectionRetries = ProxyHandler.DEFAULT_MAX_RETRY_ATTEMPTS;
         this.idempotentRequestPredicate = IdempotentPredicate.INSTANCE;
      }

      public ProxyClient getProxyClient() {
         return this.proxyClient;
      }

      public Builder setProxyClient(ProxyClient proxyClient) {
         if (proxyClient == null) {
            throw UndertowMessages.MESSAGES.argumentCannotBeNull("proxyClient");
         } else {
            this.proxyClient = proxyClient;
            return this;
         }
      }

      public int getMaxRequestTime() {
         return this.maxRequestTime;
      }

      public Builder setMaxRequestTime(int maxRequestTime) {
         this.maxRequestTime = maxRequestTime;
         return this;
      }

      public Map<HttpString, ExchangeAttribute> getRequestHeaders() {
         return Collections.unmodifiableMap(this.requestHeaders);
      }

      public Builder addRequestHeader(HttpString header, ExchangeAttribute value) {
         this.requestHeaders.put(header, value);
         return this;
      }

      public HttpHandler getNext() {
         return this.next;
      }

      public Builder setNext(HttpHandler next) {
         this.next = next;
         return this;
      }

      public boolean isRewriteHostHeader() {
         return this.rewriteHostHeader;
      }

      public Builder setRewriteHostHeader(boolean rewriteHostHeader) {
         this.rewriteHostHeader = rewriteHostHeader;
         return this;
      }

      public boolean isReuseXForwarded() {
         return this.reuseXForwarded;
      }

      public Builder setReuseXForwarded(boolean reuseXForwarded) {
         this.reuseXForwarded = reuseXForwarded;
         return this;
      }

      public int getMaxConnectionRetries() {
         return this.maxConnectionRetries;
      }

      public Builder setMaxConnectionRetries(int maxConnectionRetries) {
         this.maxConnectionRetries = maxConnectionRetries;
         return this;
      }

      public Predicate getIdempotentRequestPredicate() {
         return this.idempotentRequestPredicate;
      }

      public Builder setIdempotentRequestPredicate(Predicate idempotentRequestPredicate) {
         if (idempotentRequestPredicate == null) {
            throw UndertowMessages.MESSAGES.argumentCannotBeNull("idempotentRequestPredicate");
         } else {
            this.idempotentRequestPredicate = idempotentRequestPredicate;
            return this;
         }
      }

      public ProxyHandler build() {
         return new ProxyHandler(this);
      }
   }

   private static final class ClosingExceptionHandler implements ChannelExceptionHandler<Channel> {
      private final Closeable[] toClose;

      private ClosingExceptionHandler(Closeable... toClose) {
         this.toClose = toClose;
      }

      public void handleException(Channel channel, IOException exception) {
         IoUtils.safeClose((Closeable)channel);
         IoUtils.safeClose(this.toClose);
      }

      // $FF: synthetic method
      ClosingExceptionHandler(Closeable[] x0, Object x1) {
         this(x0);
      }
   }

   private static final class IoExceptionHandler implements ChannelExceptionHandler<Channel> {
      private final HttpServerExchange exchange;
      private final ClientConnection clientConnection;

      private IoExceptionHandler(HttpServerExchange exchange, ClientConnection clientConnection) {
         this.exchange = exchange;
         this.clientConnection = clientConnection;
      }

      public void handleException(Channel channel, IOException exception) {
         IoUtils.safeClose((Closeable)channel);
         IoUtils.safeClose((Closeable)this.clientConnection);
         if (this.exchange.isResponseStarted()) {
            UndertowLogger.REQUEST_IO_LOGGER.debug("Exception reading from target server", exception);
            if (!this.exchange.isResponseStarted()) {
               this.exchange.setStatusCode(500);
               this.exchange.endExchange();
            } else {
               IoUtils.safeClose((Closeable)this.exchange.getConnection());
            }
         } else {
            UndertowLogger.REQUEST_IO_LOGGER.ioException(exception);
            this.exchange.setStatusCode(500);
            this.exchange.endExchange();
         }

      }

      // $FF: synthetic method
      IoExceptionHandler(HttpServerExchange x0, ClientConnection x1, Object x2) {
         this(x0, x1);
      }
   }

   private static final class HTTPTrailerChannelListener implements ChannelListener<StreamSinkChannel> {
      private final Attachable source;
      private final Attachable target;
      private final HttpServerExchange exchange;
      private final ProxyClientHandler proxyClientHandler;
      private final Predicate idempotentPredicate;

      private HTTPTrailerChannelListener(Attachable source, Attachable target, HttpServerExchange exchange, ProxyClientHandler proxyClientHandler, Predicate idempotentPredicate) {
         this.source = source;
         this.target = target;
         this.exchange = exchange;
         this.proxyClientHandler = proxyClientHandler;
         this.idempotentPredicate = idempotentPredicate;
      }

      public void handleEvent(StreamSinkChannel channel) {
         HeaderMap trailers = (HeaderMap)this.source.getAttachment(HttpAttachments.REQUEST_TRAILERS);
         if (trailers != null) {
            this.target.putAttachment(HttpAttachments.RESPONSE_TRAILERS, trailers);
         }

         try {
            channel.shutdownWrites();
            if (!channel.flush()) {
               channel.getWriteSetter().set(ChannelListeners.flushingChannelListener(new ChannelListener<StreamSinkChannel>() {
                  public void handleEvent(StreamSinkChannel channel) {
                     channel.suspendWrites();
                     channel.getWriteSetter().set((ChannelListener)null);
                  }
               }, ChannelListeners.closingChannelExceptionHandler()));
               channel.resumeWrites();
            } else {
               channel.getWriteSetter().set((ChannelListener)null);
               channel.shutdownWrites();
            }
         } catch (IOException var4) {
            ProxyHandler.handleFailure(this.exchange, this.proxyClientHandler, this.idempotentPredicate, var4);
         } catch (Exception var5) {
            ProxyHandler.handleFailure(this.exchange, this.proxyClientHandler, this.idempotentPredicate, new IOException(var5));
         }

      }

      // $FF: synthetic method
      HTTPTrailerChannelListener(Attachable x0, Attachable x1, HttpServerExchange x2, ProxyClientHandler x3, Predicate x4, Object x5) {
         this(x0, x1, x2, x3, x4);
      }
   }

   private static final class ResponseCallback implements ClientCallback<ClientExchange> {
      private final HttpServerExchange exchange;
      private final ProxyClientHandler proxyClientHandler;
      private final Predicate idempotentPredicate;

      private ResponseCallback(HttpServerExchange exchange, ProxyClientHandler proxyClientHandler, Predicate idempotentPredicate) {
         this.exchange = exchange;
         this.proxyClientHandler = proxyClientHandler;
         this.idempotentPredicate = idempotentPredicate;
      }

      public void completed(final ClientExchange result) {
         ClientResponse response = result.getResponse();
         if (ProxyHandler.log.isDebugEnabled()) {
            ProxyHandler.log.debugf((String)"Received response %s for request %s for exchange %s", (Object)response, result.getRequest(), this.exchange);
         }

         HeaderMap inboundResponseHeaders = response.getResponseHeaders();
         HeaderMap outboundResponseHeaders = this.exchange.getResponseHeaders();
         this.exchange.setStatusCode(response.getResponseCode());
         ProxyHandler.copyHeaders(outboundResponseHeaders, inboundResponseHeaders);
         if (this.exchange.isUpgrade()) {
            this.exchange.upgradeChannel(new HttpUpgradeListener() {
               public void handleUpgrade(StreamConnection streamConnection, HttpServerExchange exchange) {
                  if (ProxyHandler.log.isDebugEnabled()) {
                     ProxyHandler.log.debugf((String)"Upgraded request %s to for exchange %s", (Object)result.getRequest(), (Object)exchange);
                  }

                  StreamConnection clientChannel = null;

                  try {
                     clientChannel = result.getConnection().performUpgrade();
                     ClosingExceptionHandler handler = new ClosingExceptionHandler(new Closeable[]{streamConnection, clientChannel});
                     Transfer.initiateTransfer(clientChannel.getSourceChannel(), streamConnection.getSinkChannel(), ChannelListeners.closingChannelListener(), ChannelListeners.writeShutdownChannelListener(ChannelListeners.flushingChannelListener(ChannelListeners.closingChannelListener(), ChannelListeners.closingChannelExceptionHandler()), ChannelListeners.closingChannelExceptionHandler()), handler, handler, result.getConnection().getBufferPool());
                     Transfer.initiateTransfer(streamConnection.getSourceChannel(), clientChannel.getSinkChannel(), ChannelListeners.closingChannelListener(), ChannelListeners.writeShutdownChannelListener(ChannelListeners.flushingChannelListener(ChannelListeners.closingChannelListener(), ChannelListeners.closingChannelExceptionHandler()), ChannelListeners.closingChannelExceptionHandler()), handler, handler, result.getConnection().getBufferPool());
                  } catch (IOException var5) {
                     IoUtils.safeClose(streamConnection, clientChannel);
                  }

               }
            });
         }

         IoExceptionHandler handler = new IoExceptionHandler(this.exchange, result.getConnection());
         Transfer.initiateTransfer(result.getResponseChannel(), this.exchange.getResponseChannel(), ChannelListeners.closingChannelListener(), new HTTPTrailerChannelListener(result, this.exchange, this.exchange, this.proxyClientHandler, this.idempotentPredicate), handler, handler, this.exchange.getConnection().getByteBufferPool());
      }

      public void failed(IOException e) {
         ProxyHandler.handleFailure(this.exchange, this.proxyClientHandler, this.idempotentPredicate, e);
      }

      // $FF: synthetic method
      ResponseCallback(HttpServerExchange x0, ProxyClientHandler x1, Predicate x2, Object x3) {
         this(x0, x1, x2);
      }
   }

   private static class ProxyAction implements Runnable {
      private final ProxyConnection clientConnection;
      private final HttpServerExchange exchange;
      private final Map<HttpString, ExchangeAttribute> requestHeaders;
      private final boolean rewriteHostHeader;
      private final boolean reuseXForwarded;
      private final ProxyClientHandler proxyClientHandler;
      private final Predicate idempotentPredicate;

      ProxyAction(ProxyConnection clientConnection, HttpServerExchange exchange, Map<HttpString, ExchangeAttribute> requestHeaders, boolean rewriteHostHeader, boolean reuseXForwarded, ProxyClientHandler proxyClientHandler, Predicate idempotentPredicate) {
         this.clientConnection = clientConnection;
         this.exchange = exchange;
         this.requestHeaders = requestHeaders;
         this.rewriteHostHeader = rewriteHostHeader;
         this.reuseXForwarded = reuseXForwarded;
         this.proxyClientHandler = proxyClientHandler;
         this.idempotentPredicate = idempotentPredicate;
      }

      public void run() {
         final ClientRequest request = new ClientRequest();
         String targetURI = this.exchange.getRequestURI();
         if (this.exchange.isHostIncludedInRequestURI()) {
            int uriPart = targetURI.indexOf("//");
            if (uriPart != -1) {
               uriPart = targetURI.indexOf("/", uriPart + 2);
               if (uriPart != -1) {
                  targetURI = targetURI.substring(uriPart);
               }
            }
         }

         if (!this.exchange.getResolvedPath().isEmpty() && targetURI.startsWith(this.exchange.getResolvedPath())) {
            targetURI = targetURI.substring(this.exchange.getResolvedPath().length());
         }

         StringBuilder requestURI = new StringBuilder();
         if (!this.clientConnection.getTargetPath().isEmpty() && (!this.clientConnection.getTargetPath().equals("/") || targetURI.isEmpty())) {
            requestURI.append(this.clientConnection.getTargetPath());
         }

         requestURI.append(targetURI);
         String qs = this.exchange.getQueryString();
         if (qs != null && !qs.isEmpty()) {
            requestURI.append('?');
            requestURI.append(qs);
         }

         request.setPath(requestURI.toString()).setMethod(this.exchange.getRequestMethod());
         HeaderMap inboundRequestHeaders = this.exchange.getRequestHeaders();
         HeaderMap outboundRequestHeaders = request.getRequestHeaders();
         ProxyHandler.copyHeaders(outboundRequestHeaders, inboundRequestHeaders);
         if (!this.exchange.isPersistent()) {
            outboundRequestHeaders.put(Headers.CONNECTION, "keep-alive");
         }

         if ("h2c".equals(this.exchange.getRequestHeaders().getFirst(Headers.UPGRADE))) {
            this.exchange.getRequestHeaders().remove(Headers.UPGRADE);
            outboundRequestHeaders.put(Headers.CONNECTION, "keep-alive");
         }

         Iterator var7 = this.requestHeaders.entrySet().iterator();

         while(true) {
            String hostName;
            while(var7.hasNext()) {
               Map.Entry<HttpString, ExchangeAttribute> entry = (Map.Entry)var7.next();
               hostName = ((ExchangeAttribute)entry.getValue()).readAttribute(this.exchange);
               if (hostName != null && !hostName.isEmpty()) {
                  outboundRequestHeaders.put((HttpString)entry.getKey(), hostName.replace('\n', ' '));
               } else {
                  outboundRequestHeaders.remove((HttpString)entry.getKey());
               }
            }

            SocketAddress address = this.exchange.getSourceAddress();
            final String remoteHost;
            if (address != null) {
               remoteHost = ((InetSocketAddress)address).getHostString();
               if (!((InetSocketAddress)address).isUnresolved()) {
                  request.putAttachment(ProxiedRequestAttachments.REMOTE_ADDRESS, ((InetSocketAddress)address).getAddress().getHostAddress());
               }
            } else {
               remoteHost = "localhost";
            }

            request.putAttachment(ProxiedRequestAttachments.REMOTE_HOST, remoteHost);
            if (this.reuseXForwarded && request.getRequestHeaders().contains(Headers.X_FORWARDED_FOR)) {
               hostName = request.getRequestHeaders().getFirst(Headers.X_FORWARDED_FOR);
               if (hostName != null && !hostName.isEmpty()) {
                  request.getRequestHeaders().put(Headers.X_FORWARDED_FOR, hostName + "," + remoteHost);
               } else {
                  request.getRequestHeaders().put(Headers.X_FORWARDED_FOR, remoteHost);
               }
            } else {
               request.getRequestHeaders().put(Headers.X_FORWARDED_FOR, remoteHost);
            }

            if (!this.exchange.getConnection().isPushSupported() && this.clientConnection.getConnection().isPushSupported()) {
               request.getRequestHeaders().put(Headers.X_DISABLE_PUSH, "true");
            }

            if (this.reuseXForwarded && this.exchange.getRequestHeaders().contains(Headers.X_FORWARDED_PROTO)) {
               hostName = this.exchange.getRequestHeaders().getFirst(Headers.X_FORWARDED_PROTO);
               request.putAttachment(ProxiedRequestAttachments.IS_SSL, hostName.equals("https"));
            } else {
               hostName = this.exchange.getRequestScheme().equals("https") ? "https" : "http";
               request.getRequestHeaders().put(Headers.X_FORWARDED_PROTO, hostName);
               request.putAttachment(ProxiedRequestAttachments.IS_SSL, hostName.equals("https"));
            }

            if (this.reuseXForwarded && this.exchange.getRequestHeaders().contains(Headers.X_FORWARDED_SERVER)) {
               hostName = this.exchange.getRequestHeaders().getFirst(Headers.X_FORWARDED_SERVER);
               request.putAttachment(ProxiedRequestAttachments.SERVER_NAME, hostName);
            } else {
               hostName = this.exchange.getHostName();
               request.getRequestHeaders().put(Headers.X_FORWARDED_SERVER, hostName);
               request.putAttachment(ProxiedRequestAttachments.SERVER_NAME, hostName);
            }

            if (!this.exchange.getRequestHeaders().contains(Headers.X_FORWARDED_HOST)) {
               hostName = this.exchange.getHostName();
               if (hostName != null) {
                  request.getRequestHeaders().put(Headers.X_FORWARDED_HOST, NetworkUtils.formatPossibleIpv6Address(hostName));
               }
            }

            int port;
            if (this.reuseXForwarded && this.exchange.getRequestHeaders().contains(Headers.X_FORWARDED_PORT)) {
               try {
                  port = Integer.parseInt(this.exchange.getRequestHeaders().getFirst(Headers.X_FORWARDED_PORT));
                  request.putAttachment(ProxiedRequestAttachments.SERVER_PORT, port);
               } catch (NumberFormatException var13) {
                  int port = ((InetSocketAddress)this.exchange.getConnection().getLocalAddress(InetSocketAddress.class)).getPort();
                  request.getRequestHeaders().put(Headers.X_FORWARDED_PORT, (long)port);
                  request.putAttachment(ProxiedRequestAttachments.SERVER_PORT, port);
               }
            } else {
               port = this.exchange.getHostPort();
               request.getRequestHeaders().put(Headers.X_FORWARDED_PORT, (long)port);
               request.putAttachment(ProxiedRequestAttachments.SERVER_PORT, port);
            }

            SSLSessionInfo sslSessionInfo = this.exchange.getConnection().getSslSessionInfo();
            if (sslSessionInfo != null) {
               try {
                  Certificate[] peerCertificates = sslSessionInfo.getPeerCertificates();
                  if (peerCertificates.length > 0) {
                     request.putAttachment(ProxiedRequestAttachments.SSL_CERT, Certificates.toPem(peerCertificates[0]));
                  }
               } catch (CertificateEncodingException | RenegotiationRequiredException | SSLPeerUnverifiedException var12) {
               }

               request.putAttachment(ProxiedRequestAttachments.SSL_CYPHER, sslSessionInfo.getCipherSuite());
               request.putAttachment(ProxiedRequestAttachments.SSL_SESSION_ID, sslSessionInfo.getSessionId());
               request.putAttachment(ProxiedRequestAttachments.SSL_KEY_SIZE, sslSessionInfo.getKeySize());
            }

            if (this.rewriteHostHeader) {
               InetSocketAddress targetAddress = (InetSocketAddress)this.clientConnection.getConnection().getPeerAddress(InetSocketAddress.class);
               request.getRequestHeaders().put(Headers.HOST, targetAddress.getHostString() + ":" + targetAddress.getPort());
               request.getRequestHeaders().put(Headers.X_FORWARDED_HOST, this.exchange.getRequestHeaders().getFirst(Headers.HOST));
            }

            if (ProxyHandler.log.isDebugEnabled()) {
               ProxyHandler.log.debugf((String)"Sending request %s to target %s for exchange %s", (Object)request, this.clientConnection.getConnection().getPeerAddress(), this.exchange);
            }

            if (!request.getRequestHeaders().contains(Headers.TRANSFER_ENCODING) && !request.getRequestHeaders().contains(Headers.CONTENT_LENGTH) && !this.exchange.isRequestComplete()) {
               request.getRequestHeaders().put(Headers.TRANSFER_ENCODING, Headers.CHUNKED.toString());
            }

            this.clientConnection.getConnection().sendRequest(request, new ClientCallback<ClientExchange>() {
               public void completed(final ClientExchange result) {
                  if (ProxyHandler.log.isDebugEnabled()) {
                     ProxyHandler.log.debugf((String)"Sent request %s to target %s for exchange %s", (Object)request, remoteHost, ProxyAction.this.exchange);
                  }

                  result.putAttachment(ProxyHandler.EXCHANGE, ProxyAction.this.exchange);
                  boolean requiresContinueResponse = HttpContinue.requiresContinueResponse(ProxyAction.this.exchange);
                  if (requiresContinueResponse) {
                     result.setContinueHandler(new ContinueNotification() {
                        public void handleContinue(ClientExchange clientExchange) {
                           if (ProxyHandler.log.isDebugEnabled()) {
                              ProxyHandler.log.debugf((String)"Received continue response to request %s to target %s for exchange %s", (Object)request, ProxyAction.this.clientConnection.getConnection().getPeerAddress(), ProxyAction.this.exchange);
                           }

                           HttpContinue.sendContinueResponse(ProxyAction.this.exchange, new IoCallback() {
                              public void onComplete(HttpServerExchange exchange, Sender sender) {
                              }

                              public void onException(HttpServerExchange exchange, Sender sender, IOException exception) {
                                 IoUtils.safeClose((Closeable)ProxyAction.this.clientConnection.getConnection());
                                 exchange.endExchange();
                                 UndertowLogger.REQUEST_IO_LOGGER.ioException(exception);
                              }
                           });
                        }
                     });
                  }

                  if (ProxyAction.this.exchange.getConnection().isPushSupported() && result.getConnection().isPushSupported()) {
                     result.setPushHandler(new PushCallback() {
                        public boolean handlePush(ClientExchange originalRequest, final ClientExchange pushedRequest) {
                           if (ProxyHandler.log.isDebugEnabled()) {
                              ProxyHandler.log.debugf("Sending push request %s received from %s to target %s for exchange %s", pushedRequest.getRequest(), request, remoteHost, ProxyAction.this.exchange);
                           }

                           final ClientRequest requestx = pushedRequest.getRequest();
                           ProxyAction.this.exchange.getConnection().pushResource(requestx.getPath(), requestx.getMethod(), requestx.getRequestHeaders(), new HttpHandler() {
                              public void handleRequest(HttpServerExchange exchange) throws Exception {
                                 String path = requestx.getPath();
                                 int i = path.indexOf("?");
                                 if (i > 0) {
                                    path = path.substring(0, i);
                                 }

                                 exchange.dispatch(SameThreadExecutor.INSTANCE, (Runnable)(new ProxyAction(new ProxyConnection(pushedRequest.getConnection(), path), exchange, ProxyAction.this.requestHeaders, ProxyAction.this.rewriteHostHeader, ProxyAction.this.reuseXForwarded, (ProxyClientHandler)null, ProxyAction.this.idempotentPredicate)));
                              }
                           });
                           return true;
                        }
                     });
                  }

                  result.setResponseListener(new ResponseCallback(ProxyAction.this.exchange, ProxyAction.this.proxyClientHandler, ProxyAction.this.idempotentPredicate));
                  final IoExceptionHandler handler = new IoExceptionHandler(ProxyAction.this.exchange, ProxyAction.this.clientConnection.getConnection());
                  if (requiresContinueResponse) {
                     try {
                        if (!result.getRequestChannel().flush()) {
                           result.getRequestChannel().getWriteSetter().set(ChannelListeners.flushingChannelListener(new ChannelListener<StreamSinkChannel>() {
                              public void handleEvent(StreamSinkChannel channel) {
                                 Transfer.initiateTransfer(ProxyAction.this.exchange.getRequestChannel(), result.getRequestChannel(), ChannelListeners.closingChannelListener(), new HTTPTrailerChannelListener(ProxyAction.this.exchange, result, ProxyAction.this.exchange, ProxyAction.this.proxyClientHandler, ProxyAction.this.idempotentPredicate), handler, handler, ProxyAction.this.exchange.getConnection().getByteBufferPool());
                              }
                           }, handler));
                           result.getRequestChannel().resumeWrites();
                           return;
                        }
                     } catch (IOException var5) {
                        handler.handleException(result.getRequestChannel(), var5);
                     }
                  }

                  HTTPTrailerChannelListener trailerListener = new HTTPTrailerChannelListener(ProxyAction.this.exchange, result, ProxyAction.this.exchange, ProxyAction.this.proxyClientHandler, ProxyAction.this.idempotentPredicate);
                  if (!ProxyAction.this.exchange.isRequestComplete()) {
                     Transfer.initiateTransfer(ProxyAction.this.exchange.getRequestChannel(), result.getRequestChannel(), ChannelListeners.closingChannelListener(), trailerListener, handler, handler, ProxyAction.this.exchange.getConnection().getByteBufferPool());
                  } else {
                     trailerListener.handleEvent(result.getRequestChannel());
                  }

               }

               public void failed(IOException e) {
                  ProxyHandler.handleFailure(ProxyAction.this.exchange, ProxyAction.this.proxyClientHandler, ProxyAction.this.idempotentPredicate, e);
               }
            });
            return;
         }
      }
   }

   private final class ProxyClientHandler implements ProxyCallback<ProxyConnection>, Runnable {
      private int tries;
      private final long timeout;
      private final int maxRetryAttempts;
      private final HttpServerExchange exchange;
      private final Predicate idempotentPredicate;
      private ProxyClient.ProxyTarget target;

      ProxyClientHandler(HttpServerExchange exchange, ProxyClient.ProxyTarget target, long timeout, int maxRetryAttempts, Predicate idempotentPredicate) {
         this.exchange = exchange;
         this.timeout = timeout;
         this.maxRetryAttempts = maxRetryAttempts;
         this.target = target;
         this.idempotentPredicate = idempotentPredicate;
      }

      public void run() {
         ProxyHandler.this.proxyClient.getConnection(this.target, this.exchange, this, -1L, TimeUnit.MILLISECONDS);
      }

      public void completed(HttpServerExchange exchange, ProxyConnection connection) {
         exchange.putAttachment(ProxyHandler.CONNECTION, connection);
         exchange.dispatch(SameThreadExecutor.INSTANCE, (Runnable)(new ProxyAction(connection, exchange, ProxyHandler.this.requestHeaders, ProxyHandler.this.rewriteHostHeader, ProxyHandler.this.reuseXForwarded, exchange.isRequestComplete() ? this : null, this.idempotentPredicate)));
      }

      public void failed(HttpServerExchange exchange) {
         long time = System.currentTimeMillis();
         if (this.tries++ < this.maxRetryAttempts) {
            if (this.timeout > 0L && time > this.timeout) {
               this.cancel(exchange);
            } else {
               this.target = ProxyHandler.this.proxyClient.findTarget(exchange);
               if (this.target != null) {
                  long remaining = this.timeout > 0L ? this.timeout - time : -1L;
                  ProxyHandler.this.proxyClient.getConnection(this.target, exchange, this, remaining, TimeUnit.MILLISECONDS);
               } else {
                  this.couldNotResolveBackend(exchange);
               }
            }
         } else {
            this.couldNotResolveBackend(exchange);
         }

      }

      public void queuedRequestFailed(HttpServerExchange exchange) {
         this.failed(exchange);
      }

      public void couldNotResolveBackend(HttpServerExchange exchange) {
         if (exchange.isResponseStarted()) {
            IoUtils.safeClose((Closeable)exchange.getConnection());
         } else {
            exchange.setStatusCode(503);
            exchange.endExchange();
         }

      }

      void cancel(HttpServerExchange exchange) {
         ProxyConnection connectionAttachment = (ProxyConnection)exchange.getAttachment(ProxyHandler.CONNECTION);
         if (connectionAttachment != null) {
            ClientConnection clientConnection = connectionAttachment.getConnection();
            UndertowLogger.PROXY_REQUEST_LOGGER.timingOutRequest(clientConnection.getPeerAddress() + "" + exchange.getRequestURI());
            IoUtils.safeClose((Closeable)clientConnection);
         } else {
            UndertowLogger.PROXY_REQUEST_LOGGER.timingOutRequest(exchange.getRequestURI());
         }

         if (exchange.isResponseStarted()) {
            IoUtils.safeClose((Closeable)exchange.getConnection());
         } else {
            exchange.setStatusCode(504);
            exchange.endExchange();
         }

      }
   }
}
