package io.undertow.server;

import io.undertow.UndertowLogger;
import io.undertow.UndertowMessages;
import io.undertow.UndertowOptions;
import io.undertow.channels.DetachableStreamSinkChannel;
import io.undertow.channels.DetachableStreamSourceChannel;
import io.undertow.conduits.EmptyStreamSourceConduit;
import io.undertow.connector.PooledByteBuffer;
import io.undertow.io.AsyncReceiverImpl;
import io.undertow.io.AsyncSenderImpl;
import io.undertow.io.BlockingReceiverImpl;
import io.undertow.io.BlockingSenderImpl;
import io.undertow.io.Receiver;
import io.undertow.io.Sender;
import io.undertow.io.UndertowInputStream;
import io.undertow.io.UndertowOutputStream;
import io.undertow.security.api.SecurityContext;
import io.undertow.server.handlers.Cookie;
import io.undertow.util.AbstractAttachable;
import io.undertow.util.AttachmentKey;
import io.undertow.util.ConduitFactory;
import io.undertow.util.Cookies;
import io.undertow.util.HeaderMap;
import io.undertow.util.Headers;
import io.undertow.util.HttpString;
import io.undertow.util.Methods;
import io.undertow.util.NetworkUtils;
import io.undertow.util.Protocols;
import io.undertow.util.Rfc6265CookieSupport;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.Channel;
import java.nio.channels.FileChannel;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import org.jboss.logging.Logger;
import org.xnio.Bits;
import org.xnio.Buffers;
import org.xnio.ChannelExceptionHandler;
import org.xnio.ChannelListener;
import org.xnio.ChannelListeners;
import org.xnio.IoUtils;
import org.xnio.XnioIoThread;
import org.xnio.channels.Channels;
import org.xnio.channels.Configurable;
import org.xnio.channels.StreamSinkChannel;
import org.xnio.channels.StreamSourceChannel;
import org.xnio.conduits.Conduit;
import org.xnio.conduits.ConduitStreamSinkChannel;
import org.xnio.conduits.ConduitStreamSourceChannel;
import org.xnio.conduits.StreamSinkConduit;
import org.xnio.conduits.StreamSourceConduit;

public final class HttpServerExchange extends AbstractAttachable {
   private static final Logger log = Logger.getLogger(HttpServerExchange.class);
   private static final RuntimePermission SET_SECURITY_CONTEXT = new RuntimePermission("io.undertow.SET_SECURITY_CONTEXT");
   private static final String ISO_8859_1 = "ISO-8859-1";
   private static final String HTTPS = "https";
   private static final AttachmentKey<String> REASON_PHRASE = AttachmentKey.create(String.class);
   static final AttachmentKey<PooledByteBuffer[]> BUFFERED_REQUEST_DATA = AttachmentKey.create(PooledByteBuffer[].class);
   public static final AttachmentKey<Map<String, String>> REQUEST_ATTRIBUTES = AttachmentKey.create(Map.class);
   public static final AttachmentKey<String> REMOTE_USER = AttachmentKey.create(String.class);
   public static final AttachmentKey<Boolean> SECURE_REQUEST = AttachmentKey.create(Boolean.class);
   private final ServerConnection connection;
   private final HeaderMap requestHeaders;
   private final HeaderMap responseHeaders;
   private int exchangeCompletionListenersCount;
   private ExchangeCompletionListener[] exchangeCompleteListeners;
   private DefaultResponseListener[] defaultResponseListeners;
   private Map<String, Deque<String>> queryParameters;
   private Map<String, Deque<String>> pathParameters;
   private DelegatingIterable<Cookie> requestCookies;
   private DelegatingIterable<Cookie> responseCookies;
   private Map<String, Cookie> deprecatedRequestCookies;
   private Map<String, Cookie> deprecatedResponseCookies;
   private WriteDispatchChannel responseChannel;
   protected ReadDispatchChannel requestChannel;
   private BlockingHttpExchange blockingHttpExchange;
   private HttpString protocol;
   private SecurityContext securityContext;
   private int state;
   private HttpString requestMethod;
   private String requestScheme;
   private String requestURI;
   private String requestPath;
   private String relativePath;
   private String resolvedPath;
   private String queryString;
   private int requestWrapperCount;
   private ConduitWrapper<StreamSourceConduit>[] requestWrappers;
   private int responseWrapperCount;
   private ConduitWrapper<StreamSinkConduit>[] responseWrappers;
   private Sender sender;
   private Receiver receiver;
   private long requestStartTime;
   private long maxEntitySize;
   private Runnable dispatchTask;
   private Executor dispatchExecutor;
   private long responseBytesSent;
   private static final int MASK_RESPONSE_CODE = Bits.intBitMask(0, 9);
   private static final int FLAG_RESPONSE_SENT = 1024;
   private static final int FLAG_RESPONSE_TERMINATED = 2048;
   private static final int FLAG_REQUEST_TERMINATED = 4096;
   private static final int FLAG_PERSISTENT = 16384;
   private static final int FLAG_DISPATCHED = 32768;
   private static final int FLAG_URI_CONTAINS_HOST = 65536;
   private static final int FLAG_IN_CALL = 131072;
   private static final int FLAG_SHOULD_RESUME_READS = 262144;
   private static final int FLAG_SHOULD_RESUME_WRITES = 524288;
   private static final int FLAG_REQUEST_RESET = 1048576;
   private InetSocketAddress sourceAddress;
   private InetSocketAddress destinationAddress;

   public HttpServerExchange(ServerConnection connection, long maxEntitySize) {
      this(connection, new HeaderMap(), new HeaderMap(), maxEntitySize);
   }

   public HttpServerExchange(ServerConnection connection) {
      this(connection, 0L);
   }

   public HttpServerExchange(ServerConnection connection, HeaderMap requestHeaders, HeaderMap responseHeaders, long maxEntitySize) {
      this.exchangeCompletionListenersCount = 0;
      this.state = 200;
      this.requestMethod = HttpString.EMPTY;
      this.resolvedPath = "";
      this.queryString = "";
      this.requestWrapperCount = 0;
      this.responseWrapperCount = 0;
      this.requestStartTime = -1L;
      this.responseBytesSent = 0L;
      this.connection = connection;
      this.maxEntitySize = maxEntitySize;
      this.requestHeaders = requestHeaders;
      this.responseHeaders = responseHeaders;
   }

   public HttpString getProtocol() {
      return this.protocol;
   }

   public HttpServerExchange setProtocol(HttpString protocol) {
      this.protocol = protocol;
      return this;
   }

   public boolean isHttp09() {
      return this.protocol.equals(Protocols.HTTP_0_9);
   }

   public boolean isHttp10() {
      return this.protocol.equals(Protocols.HTTP_1_0);
   }

   public boolean isHttp11() {
      return this.protocol.equals(Protocols.HTTP_1_1);
   }

   public boolean isSecure() {
      Boolean secure = (Boolean)this.getAttachment(SECURE_REQUEST);
      if (secure != null && secure) {
         return true;
      } else {
         String scheme = this.getRequestScheme();
         return scheme != null && scheme.equalsIgnoreCase("https");
      }
   }

   public HttpString getRequestMethod() {
      return this.requestMethod;
   }

   public HttpServerExchange setRequestMethod(HttpString requestMethod) {
      this.requestMethod = requestMethod;
      return this;
   }

   public String getRequestScheme() {
      return this.requestScheme;
   }

   public HttpServerExchange setRequestScheme(String requestScheme) {
      this.requestScheme = requestScheme;
      return this;
   }

   public String getRequestURI() {
      return this.requestURI;
   }

   public HttpServerExchange setRequestURI(String requestURI) {
      this.requestURI = requestURI;
      return this;
   }

   public HttpServerExchange setRequestURI(String requestURI, boolean containsHost) {
      this.requestURI = requestURI;
      if (containsHost) {
         this.state |= 65536;
      } else {
         this.state &= -65537;
      }

      return this;
   }

   public boolean isHostIncludedInRequestURI() {
      return Bits.anyAreSet(this.state, 65536);
   }

   public String getRequestPath() {
      return this.requestPath;
   }

   public HttpServerExchange setRequestPath(String requestPath) {
      this.requestPath = requestPath;
      return this;
   }

   public String getRelativePath() {
      return this.relativePath;
   }

   public HttpServerExchange setRelativePath(String relativePath) {
      this.relativePath = relativePath;
      return this;
   }

   public String getResolvedPath() {
      return this.resolvedPath;
   }

   public HttpServerExchange setResolvedPath(String resolvedPath) {
      this.resolvedPath = resolvedPath;
      return this;
   }

   public String getQueryString() {
      return this.queryString;
   }

   public HttpServerExchange setQueryString(String queryString) {
      if (queryString.length() > 0 && queryString.charAt(0) == '?') {
         this.queryString = queryString.substring(1);
      } else {
         this.queryString = queryString;
      }

      return this;
   }

   public String getRequestURL() {
      return this.isHostIncludedInRequestURI() ? this.getRequestURI() : this.getRequestScheme() + "://" + this.getHostAndPort() + this.getRequestURI();
   }

   public String getRequestCharset() {
      return this.extractCharset(this.requestHeaders);
   }

   public String getResponseCharset() {
      HeaderMap headers = this.responseHeaders;
      return this.extractCharset(headers);
   }

   private String extractCharset(HeaderMap headers) {
      String contentType = headers.getFirst(Headers.CONTENT_TYPE);
      if (contentType != null) {
         String value = Headers.extractQuotedValueFromHeader(contentType, "charset");
         if (value != null) {
            return value;
         }
      }

      return "ISO-8859-1";
   }

   public String getHostName() {
      String host = this.requestHeaders.getFirst(Headers.HOST);
      if (host != null && !"".equals(host.trim())) {
         if (host.startsWith("[")) {
            host = host.substring(1, host.indexOf(93));
         } else if (host.indexOf(58) != -1) {
            host = host.substring(0, host.indexOf(58));
         }
      } else {
         host = this.getDestinationAddress().getHostString();
      }

      return host;
   }

   public String getHostAndPort() {
      String host = this.requestHeaders.getFirst(Headers.HOST);
      if (host == null || "".equals(host.trim())) {
         InetSocketAddress address = this.getDestinationAddress();
         host = NetworkUtils.formatPossibleIpv6Address(address.getHostString());
         int port = address.getPort();
         if ((!this.getRequestScheme().equals("http") || port != 80) && (!this.getRequestScheme().equals("https") || port != 443)) {
            host = host + ":" + port;
         }
      }

      return host;
   }

   public int getHostPort() {
      String host = this.requestHeaders.getFirst(Headers.HOST);
      if (host != null) {
         int colonIndex;
         if (host.startsWith("[")) {
            colonIndex = host.indexOf(58, host.indexOf(93));
         } else {
            colonIndex = host.indexOf(58);
         }

         if (colonIndex != -1) {
            try {
               return Integer.parseInt(host.substring(colonIndex + 1));
            } catch (NumberFormatException var4) {
            }
         }

         if (this.getRequestScheme().equals("https")) {
            return 443;
         }

         if (this.getRequestScheme().equals("http")) {
            return 80;
         }
      }

      return this.getDestinationAddress().getPort();
   }

   public ServerConnection getConnection() {
      return this.connection;
   }

   public boolean isPersistent() {
      return Bits.anyAreSet(this.state, 16384);
   }

   public boolean isInIoThread() {
      return this.getIoThread() == Thread.currentThread();
   }

   public boolean isUpgrade() {
      return this.getStatusCode() == 101;
   }

   public long getResponseBytesSent() {
      return Connectors.isEntityBodyAllowed(this) && !this.getRequestMethod().equals(Methods.HEAD) ? this.responseBytesSent : 0L;
   }

   void updateBytesSent(long bytes) {
      if (Connectors.isEntityBodyAllowed(this) && !this.getRequestMethod().equals(Methods.HEAD)) {
         this.responseBytesSent += bytes;
      }

   }

   public HttpServerExchange setPersistent(boolean persistent) {
      if (persistent) {
         this.state |= 16384;
      } else {
         this.state &= -16385;
      }

      return this;
   }

   public boolean isDispatched() {
      return Bits.anyAreSet(this.state, 32768);
   }

   public HttpServerExchange unDispatch() {
      this.state &= -32769;
      this.dispatchTask = null;
      return this;
   }

   /** @deprecated */
   @Deprecated
   public HttpServerExchange dispatch() {
      this.state |= 32768;
      return this;
   }

   public HttpServerExchange dispatch(Runnable runnable) {
      this.dispatch((Executor)null, (Runnable)runnable);
      return this;
   }

   public HttpServerExchange dispatch(Executor executor, Runnable runnable) {
      if (this.isInCall()) {
         if (executor != null) {
            this.dispatchExecutor = executor;
         }

         this.state |= 32768;
         if (Bits.anyAreSet(this.state, 786432)) {
            throw UndertowMessages.MESSAGES.resumedAndDispatched();
         }

         this.dispatchTask = runnable;
      } else if (executor == null) {
         this.getConnection().getWorker().execute(runnable);
      } else {
         executor.execute(runnable);
      }

      return this;
   }

   public HttpServerExchange dispatch(HttpHandler handler) {
      this.dispatch((Executor)null, (HttpHandler)handler);
      return this;
   }

   public HttpServerExchange dispatch(Executor executor, final HttpHandler handler) {
      Runnable runnable = new Runnable() {
         public void run() {
            Connectors.executeRootHandler(handler, HttpServerExchange.this);
         }
      };
      this.dispatch(executor, runnable);
      return this;
   }

   public HttpServerExchange setDispatchExecutor(Executor executor) {
      if (executor == null) {
         this.dispatchExecutor = null;
      } else {
         this.dispatchExecutor = executor;
      }

      return this;
   }

   public Executor getDispatchExecutor() {
      return this.dispatchExecutor;
   }

   Runnable getDispatchTask() {
      return this.dispatchTask;
   }

   boolean isInCall() {
      return Bits.anyAreSet(this.state, 131072);
   }

   HttpServerExchange setInCall(boolean value) {
      if (value) {
         this.state |= 131072;
      } else {
         this.state &= -131073;
      }

      return this;
   }

   public HttpServerExchange upgradeChannel(HttpUpgradeListener listener) {
      if (!this.connection.isUpgradeSupported()) {
         throw UndertowMessages.MESSAGES.upgradeNotSupported();
      } else if (!this.getRequestHeaders().contains(Headers.UPGRADE)) {
         throw UndertowMessages.MESSAGES.notAnUpgradeRequest();
      } else {
         UndertowLogger.REQUEST_LOGGER.debugf("Upgrading request %s", this);
         this.connection.setUpgradeListener(listener);
         this.setStatusCode(101);
         this.getResponseHeaders().put(Headers.CONNECTION, "Upgrade");
         return this;
      }
   }

   public HttpServerExchange upgradeChannel(String productName, HttpUpgradeListener listener) {
      if (!this.connection.isUpgradeSupported()) {
         throw UndertowMessages.MESSAGES.upgradeNotSupported();
      } else {
         UndertowLogger.REQUEST_LOGGER.debugf("Upgrading request %s", this);
         this.connection.setUpgradeListener(listener);
         this.setStatusCode(101);
         HeaderMap headers = this.getResponseHeaders();
         headers.put(Headers.UPGRADE, productName);
         headers.put(Headers.CONNECTION, "Upgrade");
         return this;
      }
   }

   public HttpServerExchange acceptConnectRequest(HttpUpgradeListener connectListener) {
      if (!this.getRequestMethod().equals(Methods.CONNECT)) {
         throw UndertowMessages.MESSAGES.notAConnectRequest();
      } else {
         this.connection.setConnectListener(connectListener);
         return this;
      }
   }

   public HttpServerExchange addExchangeCompleteListener(ExchangeCompletionListener listener) {
      if (!this.isComplete() && this.exchangeCompletionListenersCount != -1) {
         int exchangeCompletionListenersCount = this.exchangeCompletionListenersCount++;
         ExchangeCompletionListener[] exchangeCompleteListeners = this.exchangeCompleteListeners;
         if (exchangeCompleteListeners == null || exchangeCompleteListeners.length == exchangeCompletionListenersCount) {
            ExchangeCompletionListener[] old = exchangeCompleteListeners;
            this.exchangeCompleteListeners = exchangeCompleteListeners = new ExchangeCompletionListener[exchangeCompletionListenersCount + 2];
            if (old != null) {
               System.arraycopy(old, 0, exchangeCompleteListeners, 0, exchangeCompletionListenersCount);
            }
         }

         exchangeCompleteListeners[exchangeCompletionListenersCount] = listener;
         return this;
      } else {
         throw UndertowMessages.MESSAGES.exchangeAlreadyComplete();
      }
   }

   public HttpServerExchange addDefaultResponseListener(DefaultResponseListener listener) {
      int i = 0;
      if (this.defaultResponseListeners == null) {
         this.defaultResponseListeners = new DefaultResponseListener[2];
      } else {
         while(true) {
            if (i == this.defaultResponseListeners.length || this.defaultResponseListeners[i] == null) {
               if (i == this.defaultResponseListeners.length) {
                  DefaultResponseListener[] old = this.defaultResponseListeners;
                  this.defaultResponseListeners = new DefaultResponseListener[this.defaultResponseListeners.length + 2];
                  System.arraycopy(old, 0, this.defaultResponseListeners, 0, old.length);
               }
               break;
            }

            ++i;
         }
      }

      this.defaultResponseListeners[i] = listener;
      return this;
   }

   public InetSocketAddress getSourceAddress() {
      return this.sourceAddress != null ? this.sourceAddress : (InetSocketAddress)this.connection.getPeerAddress(InetSocketAddress.class);
   }

   public HttpServerExchange setSourceAddress(InetSocketAddress sourceAddress) {
      this.sourceAddress = sourceAddress;
      return this;
   }

   public InetSocketAddress getDestinationAddress() {
      return this.destinationAddress != null ? this.destinationAddress : (InetSocketAddress)this.connection.getLocalAddress(InetSocketAddress.class);
   }

   public HttpServerExchange setDestinationAddress(InetSocketAddress destinationAddress) {
      this.destinationAddress = destinationAddress;
      return this;
   }

   public HeaderMap getRequestHeaders() {
      return this.requestHeaders;
   }

   public long getRequestContentLength() {
      String contentLengthString = this.requestHeaders.getFirst(Headers.CONTENT_LENGTH);
      return contentLengthString == null ? -1L : Long.parseLong(contentLengthString);
   }

   public HeaderMap getResponseHeaders() {
      return this.responseHeaders;
   }

   public long getResponseContentLength() {
      String contentLengthString = this.responseHeaders.getFirst(Headers.CONTENT_LENGTH);
      return contentLengthString == null ? -1L : Long.parseLong(contentLengthString);
   }

   public HttpServerExchange setResponseContentLength(long length) {
      if (length == -1L) {
         this.responseHeaders.remove(Headers.CONTENT_LENGTH);
      } else {
         this.responseHeaders.put(Headers.CONTENT_LENGTH, Long.toString(length));
      }

      return this;
   }

   public Map<String, Deque<String>> getQueryParameters() {
      if (this.queryParameters == null) {
         this.queryParameters = new TreeMap();
      }

      return this.queryParameters;
   }

   public HttpServerExchange addQueryParam(String name, String param) {
      if (this.queryParameters == null) {
         this.queryParameters = new TreeMap();
      }

      Deque<String> list = (Deque)this.queryParameters.get(name);
      if (list == null) {
         this.queryParameters.put(name, list = new ArrayDeque(2));
      }

      ((Deque)list).add(param);
      return this;
   }

   public Map<String, Deque<String>> getPathParameters() {
      if (this.pathParameters == null) {
         this.pathParameters = new TreeMap();
      }

      return this.pathParameters;
   }

   public HttpServerExchange addPathParam(String name, String param) {
      if (this.pathParameters == null) {
         this.pathParameters = new TreeMap();
      }

      Deque<String> list = (Deque)this.pathParameters.get(name);
      if (list == null) {
         this.pathParameters.put(name, list = new ArrayDeque(2));
      }

      ((Deque)list).add(param);
      return this;
   }

   /** @deprecated */
   @Deprecated
   public Map<String, Cookie> getRequestCookies() {
      if (this.deprecatedRequestCookies == null) {
         this.deprecatedRequestCookies = new MapDelegatingToSet((Set)((DelegatingIterable)this.requestCookies()).getDelegate());
      }

      return this.deprecatedRequestCookies;
   }

   public HttpServerExchange setRequestCookie(Cookie cookie) {
      if (cookie == null) {
         return this;
      } else {
         if (this.getConnection().getUndertowOptions().get(UndertowOptions.ENABLE_RFC6265_COOKIE_VALIDATION, false)) {
            if (cookie.getValue() != null && !cookie.getValue().isEmpty()) {
               Rfc6265CookieSupport.validateCookieValue(cookie.getValue());
            }

            if (cookie.getPath() != null && !cookie.getPath().isEmpty()) {
               Rfc6265CookieSupport.validatePath(cookie.getPath());
            }

            if (cookie.getDomain() != null && !cookie.getDomain().isEmpty()) {
               Rfc6265CookieSupport.validateDomain(cookie.getDomain());
            }
         }

         ((Set)((DelegatingIterable)this.requestCookies()).getDelegate()).add(cookie);
         return this;
      }
   }

   public Cookie getRequestCookie(String name) {
      if (name == null) {
         return null;
      } else {
         Iterator var2 = this.requestCookies().iterator();

         Cookie cookie;
         do {
            if (!var2.hasNext()) {
               return null;
            }

            cookie = (Cookie)var2.next();
         } while(!name.equals(cookie.getName()));

         return cookie;
      }
   }

   public Iterable<Cookie> requestCookies() {
      if (this.requestCookies == null) {
         Set<Cookie> requestCookiesParam = new OverridableTreeSet();
         this.requestCookies = new DelegatingIterable(requestCookiesParam);
         Cookies.parseRequestCookies(this.getConnection().getUndertowOptions().get(UndertowOptions.MAX_COOKIES, 200), this.getConnection().getUndertowOptions().get(UndertowOptions.ALLOW_EQUALS_IN_COOKIE_VALUE, false), this.requestHeaders.get(Headers.COOKIE), requestCookiesParam);
      }

      return this.requestCookies;
   }

   public HttpServerExchange setResponseCookie(Cookie cookie) {
      if (cookie == null) {
         return this;
      } else {
         if (this.getConnection().getUndertowOptions().get(UndertowOptions.ENABLE_RFC6265_COOKIE_VALIDATION, false)) {
            if (cookie.getValue() != null && !cookie.getValue().isEmpty()) {
               Rfc6265CookieSupport.validateCookieValue(cookie.getValue());
            }

            if (cookie.getPath() != null && !cookie.getPath().isEmpty()) {
               Rfc6265CookieSupport.validatePath(cookie.getPath());
            }

            if (cookie.getDomain() != null && !cookie.getDomain().isEmpty()) {
               Rfc6265CookieSupport.validateDomain(cookie.getDomain());
            }
         }

         ((Set)((DelegatingIterable)this.responseCookies()).getDelegate()).add(cookie);
         return this;
      }
   }

   /** @deprecated */
   @Deprecated
   public Map<String, Cookie> getResponseCookies() {
      if (this.deprecatedResponseCookies == null) {
         this.deprecatedResponseCookies = new MapDelegatingToSet((Set)((DelegatingIterable)this.responseCookies()).getDelegate());
      }

      return this.deprecatedResponseCookies;
   }

   public Iterable<Cookie> responseCookies() {
      if (this.responseCookies == null) {
         this.responseCookies = new DelegatingIterable(new OverridableTreeSet());
      }

      return this.responseCookies;
   }

   public boolean isResponseStarted() {
      return Bits.allAreSet(this.state, 1024);
   }

   public StreamSourceChannel getRequestChannel() {
      if (this.requestChannel != null) {
         if (Bits.anyAreSet(this.state, 1048576)) {
            this.state &= -1048577;
            return this.requestChannel;
         } else {
            return null;
         }
      } else if (Bits.anyAreSet(this.state, 4096)) {
         return this.requestChannel = new ReadDispatchChannel(new ConduitStreamSourceChannel(Configurable.EMPTY, new EmptyStreamSourceConduit(this.getIoThread())));
      } else {
         ConduitWrapper<StreamSourceConduit>[] wrappers = this.requestWrappers;
         ConduitStreamSourceChannel sourceChannel = this.connection.getSourceChannel();
         if (wrappers != null) {
            this.requestWrappers = null;
            WrapperConduitFactory<StreamSourceConduit> factory = new WrapperConduitFactory(wrappers, this.requestWrapperCount, sourceChannel.getConduit(), this);
            sourceChannel.setConduit((StreamSourceConduit)factory.create());
         }

         return this.requestChannel = new ReadDispatchChannel(sourceChannel);
      }
   }

   void resetRequestChannel() {
      this.state |= 1048576;
   }

   public boolean isRequestChannelAvailable() {
      return this.requestChannel == null || Bits.anyAreSet(this.state, 1048576);
   }

   public boolean isComplete() {
      return Bits.allAreSet(this.state, 6144);
   }

   public boolean isRequestComplete() {
      PooledByteBuffer[] data = (PooledByteBuffer[])this.getAttachment(BUFFERED_REQUEST_DATA);
      return data != null ? false : Bits.allAreSet(this.state, 4096);
   }

   public boolean isResponseComplete() {
      return Bits.allAreSet(this.state, 2048);
   }

   void terminateRequest() {
      int oldVal = this.state;
      if (!Bits.allAreSet(oldVal, 4096)) {
         if (this.requestChannel != null) {
            this.requestChannel.requestDone();
         }

         this.state = oldVal | 4096;
         if (Bits.anyAreSet(oldVal, 2048)) {
            this.invokeExchangeCompleteListeners();
         }

      }
   }

   private void invokeExchangeCompleteListeners() {
      if (this.exchangeCompletionListenersCount > 0) {
         int i = this.exchangeCompletionListenersCount - 1;
         ExchangeCompletionListener next = this.exchangeCompleteListeners[i];
         this.exchangeCompletionListenersCount = -1;
         next.exchangeEvent(this, new ExchangeCompleteNextListener(this.exchangeCompleteListeners, this, i));
      } else if (this.exchangeCompletionListenersCount == 0) {
         this.exchangeCompletionListenersCount = -1;
         this.connection.exchangeComplete(this);
      }

   }

   public StreamSinkChannel getResponseChannel() {
      if (this.responseChannel != null) {
         return null;
      } else {
         ConduitWrapper<StreamSinkConduit>[] wrappers = this.responseWrappers;
         this.responseWrappers = null;
         ConduitStreamSinkChannel sinkChannel = this.connection.getSinkChannel();
         if (sinkChannel == null) {
            return null;
         } else {
            if (wrappers != null) {
               WrapperStreamSinkConduitFactory factory = new WrapperStreamSinkConduitFactory(wrappers, this.responseWrapperCount, this, sinkChannel.getConduit());
               sinkChannel.setConduit(factory.create());
            } else {
               sinkChannel.setConduit(this.connection.getSinkConduit(this, sinkChannel.getConduit()));
            }

            this.responseChannel = new WriteDispatchChannel(sinkChannel);
            this.startResponse();
            return this.responseChannel;
         }
      }
   }

   public Sender getResponseSender() {
      if (this.blockingHttpExchange != null) {
         return this.blockingHttpExchange.getSender();
      } else {
         return this.sender != null ? this.sender : (this.sender = new AsyncSenderImpl(this));
      }
   }

   public Receiver getRequestReceiver() {
      if (this.blockingHttpExchange != null) {
         return this.blockingHttpExchange.getReceiver();
      } else {
         return this.receiver != null ? this.receiver : (this.receiver = new AsyncReceiverImpl(this));
      }
   }

   public boolean isResponseChannelAvailable() {
      return this.responseChannel == null;
   }

   /** @deprecated */
   @Deprecated
   public int getResponseCode() {
      return this.state & MASK_RESPONSE_CODE;
   }

   /** @deprecated */
   @Deprecated
   public HttpServerExchange setResponseCode(int statusCode) {
      return this.setStatusCode(statusCode);
   }

   public int getStatusCode() {
      return this.state & MASK_RESPONSE_CODE;
   }

   public HttpServerExchange setStatusCode(int statusCode) {
      if (statusCode >= 0 && statusCode <= 999) {
         int oldVal = this.state;
         if (Bits.allAreSet(oldVal, 1024)) {
            throw UndertowMessages.MESSAGES.responseAlreadyStarted();
         } else {
            if (statusCode >= 500 && UndertowLogger.ERROR_RESPONSE.isDebugEnabled()) {
               UndertowLogger.ERROR_RESPONSE.debugf(new RuntimeException(), "Setting error code %s for exchange %s", statusCode, this);
            }

            this.state = oldVal & ~MASK_RESPONSE_CODE | statusCode & MASK_RESPONSE_CODE;
            return this;
         }
      } else {
         throw new IllegalArgumentException("Invalid response code");
      }
   }

   public HttpServerExchange setReasonPhrase(String message) {
      this.putAttachment(REASON_PHRASE, message);
      return this;
   }

   public String getReasonPhrase() {
      return (String)this.getAttachment(REASON_PHRASE);
   }

   public HttpServerExchange addRequestWrapper(ConduitWrapper<StreamSourceConduit> wrapper) {
      ConduitWrapper<StreamSourceConduit>[] wrappers = this.requestWrappers;
      if (this.requestChannel != null) {
         throw UndertowMessages.MESSAGES.requestChannelAlreadyProvided();
      } else {
         if (wrappers == null) {
            wrappers = this.requestWrappers = new ConduitWrapper[2];
         } else if (wrappers.length == this.requestWrapperCount) {
            this.requestWrappers = new ConduitWrapper[wrappers.length + 2];
            System.arraycopy(wrappers, 0, this.requestWrappers, 0, wrappers.length);
            wrappers = this.requestWrappers;
         }

         wrappers[this.requestWrapperCount++] = wrapper;
         return this;
      }
   }

   public HttpServerExchange addResponseWrapper(ConduitWrapper<StreamSinkConduit> wrapper) {
      ConduitWrapper<StreamSinkConduit>[] wrappers = this.responseWrappers;
      if (this.responseChannel != null) {
         throw UndertowMessages.MESSAGES.responseChannelAlreadyProvided();
      } else {
         if (wrappers == null) {
            this.responseWrappers = wrappers = new ConduitWrapper[2];
         } else if (wrappers.length == this.responseWrapperCount) {
            this.responseWrappers = new ConduitWrapper[wrappers.length + 2];
            System.arraycopy(wrappers, 0, this.responseWrappers, 0, wrappers.length);
            wrappers = this.responseWrappers;
         }

         wrappers[this.responseWrapperCount++] = wrapper;
         return this;
      }
   }

   public BlockingHttpExchange startBlocking() {
      BlockingHttpExchange old = this.blockingHttpExchange;
      this.blockingHttpExchange = new DefaultBlockingHttpExchange(this);
      return old;
   }

   public BlockingHttpExchange startBlocking(BlockingHttpExchange httpExchange) {
      BlockingHttpExchange old = this.blockingHttpExchange;
      this.blockingHttpExchange = httpExchange;
      return old;
   }

   public boolean isBlocking() {
      return this.blockingHttpExchange != null;
   }

   public InputStream getInputStream() {
      if (this.blockingHttpExchange == null) {
         throw UndertowMessages.MESSAGES.startBlockingHasNotBeenCalled();
      } else {
         return this.blockingHttpExchange.getInputStream();
      }
   }

   public OutputStream getOutputStream() {
      if (this.blockingHttpExchange == null) {
         throw UndertowMessages.MESSAGES.startBlockingHasNotBeenCalled();
      } else {
         return this.blockingHttpExchange.getOutputStream();
      }
   }

   HttpServerExchange terminateResponse() {
      int oldVal = this.state;
      if (Bits.allAreSet(oldVal, 2048)) {
         return this;
      } else {
         if (this.responseChannel != null) {
            this.responseChannel.responseDone();
         }

         this.state = oldVal | 2048;
         if (Bits.anyAreSet(oldVal, 4096)) {
            this.invokeExchangeCompleteListeners();
         }

         return this;
      }
   }

   public long getRequestStartTime() {
      return this.requestStartTime;
   }

   HttpServerExchange setRequestStartTime(long requestStartTime) {
      this.requestStartTime = requestStartTime;
      return this;
   }

   public HttpServerExchange endExchange() {
      final int state = this.state;
      if (Bits.allAreSet(state, 6144)) {
         if (this.blockingHttpExchange != null) {
            IoUtils.safeClose((Closeable)this.blockingHttpExchange);
         }

         return this;
      } else {
         int totalRead;
         if (this.defaultResponseListeners != null) {
            for(totalRead = this.defaultResponseListeners.length - 1; totalRead >= 0; --totalRead) {
               DefaultResponseListener listener = this.defaultResponseListeners[totalRead];
               if (listener != null) {
                  this.defaultResponseListeners[totalRead] = null;

                  try {
                     if (listener.handleDefaultResponse(this)) {
                        return this;
                     }
                  } catch (Throwable var7) {
                     UndertowLogger.REQUEST_LOGGER.debug("Exception running default response listener", var7);
                  }
               }
            }
         }

         if (Bits.anyAreClear(state, 4096)) {
            this.connection.terminateRequestChannel(this);
         }

         if (this.blockingHttpExchange != null) {
            try {
               this.blockingHttpExchange.close();
            } catch (IOException var5) {
               UndertowLogger.REQUEST_IO_LOGGER.ioException(var5);
               IoUtils.safeClose((Closeable)this.connection);
            } catch (Throwable var6) {
               UndertowLogger.REQUEST_IO_LOGGER.handleUnexpectedFailure(var6);
               IoUtils.safeClose((Closeable)this.connection);
            }
         }

         if (Bits.anyAreClear(state, 4096)) {
            if (this.requestChannel == null) {
               this.getRequestChannel();
            }

            totalRead = 0;

            while(true) {
               try {
                  long read = Channels.drain((StreamSourceChannel)this.requestChannel, Long.MAX_VALUE);
                  totalRead = (int)((long)totalRead + read);
                  if (read == 0L) {
                     if (this.getStatusCode() == 417 && totalRead <= 0) {
                        break;
                     }

                     this.requestChannel.getReadSetter().set(ChannelListeners.drainListener(Long.MAX_VALUE, new ChannelListener<StreamSourceChannel>() {
                        public void handleEvent(StreamSourceChannel channel) {
                           if (Bits.anyAreClear(state, 2048)) {
                              HttpServerExchange.this.closeAndFlushResponse();
                           }

                        }
                     }, new ChannelExceptionHandler<StreamSourceChannel>() {
                        public void handleException(StreamSourceChannel channel, IOException e) {
                           HttpServerExchange.this.terminateRequest();
                           HttpServerExchange.this.terminateResponse();
                           UndertowLogger.REQUEST_LOGGER.debug("Exception draining request stream", e);
                           IoUtils.safeClose((Closeable)HttpServerExchange.this.connection);
                        }
                     }));
                     this.requestChannel.resumeReads();
                     return this;
                  }

                  if (read == -1L) {
                     break;
                  }
               } catch (Throwable var8) {
                  if (var8 instanceof IOException) {
                     UndertowLogger.REQUEST_IO_LOGGER.ioException((IOException)var8);
                  } else {
                     UndertowLogger.REQUEST_IO_LOGGER.handleUnexpectedFailure(var8);
                  }

                  this.invokeExchangeCompleteListeners();
                  IoUtils.safeClose((Closeable)this.connection);
                  return this;
               }
            }
         }

         if (Bits.anyAreClear(state, 2048)) {
            this.closeAndFlushResponse();
         }

         return this;
      }
   }

   private void closeAndFlushResponse() {
      if (!this.connection.isOpen()) {
         this.terminateRequest();
         this.terminateResponse();
      } else {
         try {
            if (!this.isResponseChannelAvailable()) {
               if (Bits.anyAreClear(this.state, 2048) && !this.responseChannel.isOpen()) {
                  this.invokeExchangeCompleteListeners();
                  IoUtils.safeClose((Closeable)this.connection);
                  return;
               }
            } else {
               if (!this.getRequestMethod().equals(Methods.CONNECT) && (!this.getRequestMethod().equals(Methods.HEAD) || !this.getResponseHeaders().contains(Headers.CONTENT_LENGTH)) && Connectors.isEntityBodyAllowed(this)) {
                  this.getResponseHeaders().put(Headers.CONTENT_LENGTH, "0");
               }

               this.getResponseChannel();
            }

            this.responseChannel.shutdownWrites();
            if (!this.responseChannel.flush()) {
               this.responseChannel.getWriteSetter().set(ChannelListeners.flushingChannelListener(new ChannelListener<StreamSinkChannel>() {
                  public void handleEvent(StreamSinkChannel channel) {
                     channel.suspendWrites();
                     channel.getWriteSetter().set((ChannelListener)null);
                     if (Bits.anyAreClear(HttpServerExchange.this.state, 2048)) {
                        HttpServerExchange.this.invokeExchangeCompleteListeners();
                        UndertowLogger.ROOT_LOGGER.responseWasNotTerminated(HttpServerExchange.this.connection, HttpServerExchange.this);
                        IoUtils.safeClose((Closeable)HttpServerExchange.this.connection);
                     }

                  }
               }, new ChannelExceptionHandler<Channel>() {
                  public void handleException(Channel channel, IOException exception) {
                     HttpServerExchange.this.invokeExchangeCompleteListeners();
                     UndertowLogger.REQUEST_LOGGER.debug("Exception ending request", exception);
                     IoUtils.safeClose((Closeable)HttpServerExchange.this.connection);
                  }
               }));
               this.responseChannel.resumeWrites();
            } else if (Bits.anyAreClear(this.state, 2048)) {
               this.invokeExchangeCompleteListeners();
               UndertowLogger.ROOT_LOGGER.responseWasNotTerminated(this.connection, this);
               IoUtils.safeClose((Closeable)this.connection);
            }
         } catch (Throwable var2) {
            if (var2 instanceof IOException) {
               UndertowLogger.REQUEST_IO_LOGGER.ioException((IOException)var2);
            } else {
               UndertowLogger.REQUEST_IO_LOGGER.handleUnexpectedFailure(var2);
            }

            this.invokeExchangeCompleteListeners();
            IoUtils.safeClose((Closeable)this.connection);
         }

      }
   }

   HttpServerExchange startResponse() throws IllegalStateException {
      int oldVal = this.state;
      if (Bits.allAreSet(oldVal, 1024)) {
         throw UndertowMessages.MESSAGES.responseAlreadyStarted();
      } else {
         this.state = oldVal | 1024;
         log.tracef("Starting to write response for %s", (Object)this);
         return this;
      }
   }

   public XnioIoThread getIoThread() {
      return this.connection.getIoThread();
   }

   public long getMaxEntitySize() {
      return this.maxEntitySize;
   }

   public HttpServerExchange setMaxEntitySize(long maxEntitySize) {
      if (!this.isRequestChannelAvailable()) {
         throw UndertowMessages.MESSAGES.requestChannelAlreadyProvided();
      } else {
         this.maxEntitySize = maxEntitySize;
         this.connection.maxEntitySizeUpdated(this);
         return this;
      }
   }

   public SecurityContext getSecurityContext() {
      return this.securityContext;
   }

   public void setSecurityContext(SecurityContext securityContext) {
      SecurityManager sm = System.getSecurityManager();
      if (sm != null) {
         sm.checkPermission(SET_SECURITY_CONTEXT);
      }

      this.securityContext = securityContext;
   }

   public void addResponseCommitListener(final ResponseCommitListener listener) {
      this.addResponseWrapper(new ConduitWrapper<StreamSinkConduit>() {
         public StreamSinkConduit wrap(ConduitFactory<StreamSinkConduit> factory, HttpServerExchange exchange) {
            listener.beforeCommit(exchange);
            return (StreamSinkConduit)factory.create();
         }
      });
   }

   boolean runResumeReadWrite() {
      boolean ret = false;
      if (Bits.anyAreSet(this.state, 524288)) {
         this.responseChannel.runResume();
         ret = true;
      }

      if (Bits.anyAreSet(this.state, 262144)) {
         this.requestChannel.runResume();
         ret = true;
      }

      return ret;
   }

   boolean isResumed() {
      return Bits.anyAreSet(this.state, 786432);
   }

   public String toString() {
      return "HttpServerExchange{ " + this.getRequestMethod().toString() + " " + this.getRequestURI() + '}';
   }

   public static class WrapperConduitFactory<T extends Conduit> implements ConduitFactory<T> {
      private final HttpServerExchange exchange;
      private final ConduitWrapper<T>[] wrappers;
      private int position;
      private T first;

      public WrapperConduitFactory(ConduitWrapper<T>[] wrappers, int wrapperCount, T first, HttpServerExchange exchange) {
         this.wrappers = wrappers;
         this.exchange = exchange;
         this.position = wrapperCount - 1;
         this.first = first;
      }

      public T create() {
         return this.position == -1 ? this.first : this.wrappers[this.position--].wrap(this, this.exchange);
      }
   }

   public static class WrapperStreamSinkConduitFactory implements ConduitFactory<StreamSinkConduit> {
      private final HttpServerExchange exchange;
      private final ConduitWrapper<StreamSinkConduit>[] wrappers;
      private int position;
      private final StreamSinkConduit first;

      public WrapperStreamSinkConduitFactory(ConduitWrapper<StreamSinkConduit>[] wrappers, int wrapperCount, HttpServerExchange exchange, StreamSinkConduit first) {
         this.wrappers = wrappers;
         this.exchange = exchange;
         this.first = first;
         this.position = wrapperCount - 1;
      }

      public StreamSinkConduit create() {
         return this.position == -1 ? this.exchange.getConnection().getSinkConduit(this.exchange, this.first) : (StreamSinkConduit)this.wrappers[this.position--].wrap(this, this.exchange);
      }
   }

   private final class ReadDispatchChannel extends DetachableStreamSourceChannel implements StreamSourceChannel {
      private boolean wakeup = true;
      private boolean readsResumed = false;

      ReadDispatchChannel(ConduitStreamSourceChannel delegate) {
         super(delegate);
      }

      protected boolean isFinished() {
         return Bits.allAreSet(HttpServerExchange.this.state, 4096);
      }

      public void resumeReads() {
         this.readsResumed = true;
         if (HttpServerExchange.this.isInCall()) {
            HttpServerExchange.this.state = HttpServerExchange.this.state | 262144;
            if (Bits.anyAreSet(HttpServerExchange.this.state, 32768)) {
               throw UndertowMessages.MESSAGES.resumedAndDispatched();
            }
         } else if (!this.isFinished()) {
            this.delegate.resumeReads();
         }

      }

      public void wakeupReads() {
         if (HttpServerExchange.this.isInCall()) {
            this.wakeup = true;
            HttpServerExchange.this.state = HttpServerExchange.this.state | 262144;
            if (Bits.anyAreSet(HttpServerExchange.this.state, 32768)) {
               throw UndertowMessages.MESSAGES.resumedAndDispatched();
            }
         } else if (this.isFinished()) {
            this.invokeListener();
         } else {
            this.delegate.wakeupReads();
         }

      }

      private void invokeListener() {
         if (this.readSetter != null) {
            super.getIoThread().execute(new Runnable() {
               public void run() {
                  ChannelListeners.invokeChannelListener(ReadDispatchChannel.this, ReadDispatchChannel.this.readSetter.get());
               }
            });
         }

      }

      public void requestDone() {
         if (this.delegate instanceof ConduitStreamSourceChannel) {
            ((ConduitStreamSourceChannel)this.delegate).setReadListener((ChannelListener)null);
            ((ConduitStreamSourceChannel)this.delegate).setCloseListener((ChannelListener)null);
         } else {
            this.delegate.getReadSetter().set((ChannelListener)null);
            this.delegate.getCloseSetter().set((ChannelListener)null);
         }

      }

      public long transferTo(long position, long count, FileChannel target) throws IOException {
         PooledByteBuffer[] buffered = (PooledByteBuffer[])HttpServerExchange.this.getAttachment(HttpServerExchange.BUFFERED_REQUEST_DATA);
         return buffered == null ? super.transferTo(position, count, target) : target.transferFrom(this, position, count);
      }

      public void awaitReadable() throws IOException {
         if (Thread.currentThread() == super.getIoThread()) {
            throw UndertowMessages.MESSAGES.awaitCalledFromIoThread();
         } else {
            PooledByteBuffer[] buffered = (PooledByteBuffer[])HttpServerExchange.this.getAttachment(HttpServerExchange.BUFFERED_REQUEST_DATA);
            if (buffered == null) {
               super.awaitReadable();
            }

         }
      }

      public void suspendReads() {
         this.readsResumed = false;
         HttpServerExchange.this.state = HttpServerExchange.this.state & -262145;
         super.suspendReads();
      }

      public long transferTo(long count, ByteBuffer throughBuffer, StreamSinkChannel target) throws IOException {
         PooledByteBuffer[] buffered = (PooledByteBuffer[])HttpServerExchange.this.getAttachment(HttpServerExchange.BUFFERED_REQUEST_DATA);
         if (buffered == null) {
            return super.transferTo(count, throughBuffer, target);
         } else {
            throughBuffer.position(0);
            throughBuffer.limit(0);
            long copied = 0L;

            for(int i = 0; i < buffered.length; ++i) {
               PooledByteBuffer pooled = buffered[i];
               if (pooled != null) {
                  ByteBuffer buf = pooled.getBuffer();
                  if (buf.hasRemaining()) {
                     int res = target.write(buf);
                     if (!buf.hasRemaining()) {
                        pooled.close();
                        buffered[i] = null;
                     }

                     if (res == 0) {
                        return copied;
                     }

                     copied += (long)res;
                  } else {
                     pooled.close();
                     buffered[i] = null;
                  }
               }
            }

            HttpServerExchange.this.removeAttachment(HttpServerExchange.BUFFERED_REQUEST_DATA);
            if (copied == 0L) {
               return super.transferTo(count, throughBuffer, target);
            } else {
               return copied;
            }
         }
      }

      public void awaitReadable(long time, TimeUnit timeUnit) throws IOException {
         if (Thread.currentThread() == super.getIoThread()) {
            throw UndertowMessages.MESSAGES.awaitCalledFromIoThread();
         } else {
            PooledByteBuffer[] buffered = (PooledByteBuffer[])HttpServerExchange.this.getAttachment(HttpServerExchange.BUFFERED_REQUEST_DATA);
            if (buffered == null) {
               super.awaitReadable(time, timeUnit);
            }

         }
      }

      public long read(ByteBuffer[] dsts, int offset, int length) throws IOException {
         PooledByteBuffer[] buffered = (PooledByteBuffer[])HttpServerExchange.this.getAttachment(HttpServerExchange.BUFFERED_REQUEST_DATA);
         if (buffered == null) {
            return super.read(dsts, offset, length);
         } else {
            long copied = 0L;

            for(int i = 0; i < buffered.length; ++i) {
               PooledByteBuffer pooled = buffered[i];
               if (pooled != null) {
                  ByteBuffer buf = pooled.getBuffer();
                  if (buf.hasRemaining()) {
                     copied += (long)Buffers.copy(dsts, offset, length, buf);
                     if (!buf.hasRemaining()) {
                        pooled.close();
                        buffered[i] = null;
                     }

                     if (!Buffers.hasRemaining(dsts, offset, length)) {
                        return copied;
                     }
                  } else {
                     pooled.close();
                     buffered[i] = null;
                  }
               }
            }

            HttpServerExchange.this.removeAttachment(HttpServerExchange.BUFFERED_REQUEST_DATA);
            if (copied == 0L) {
               return super.read(dsts, offset, length);
            } else {
               return copied;
            }
         }
      }

      public long read(ByteBuffer[] dsts) throws IOException {
         return this.read(dsts, 0, dsts.length);
      }

      public boolean isOpen() {
         PooledByteBuffer[] buffered = (PooledByteBuffer[])HttpServerExchange.this.getAttachment(HttpServerExchange.BUFFERED_REQUEST_DATA);
         return buffered != null ? true : super.isOpen();
      }

      public void close() throws IOException {
         PooledByteBuffer[] buffered = (PooledByteBuffer[])HttpServerExchange.this.getAttachment(HttpServerExchange.BUFFERED_REQUEST_DATA);
         if (buffered != null) {
            PooledByteBuffer[] var2 = buffered;
            int var3 = buffered.length;

            for(int var4 = 0; var4 < var3; ++var4) {
               PooledByteBuffer pooled = var2[var4];
               if (pooled != null) {
                  pooled.close();
               }
            }
         }

         HttpServerExchange.this.removeAttachment(HttpServerExchange.BUFFERED_REQUEST_DATA);
         super.close();
      }

      public boolean isReadResumed() {
         PooledByteBuffer[] buffered = (PooledByteBuffer[])HttpServerExchange.this.getAttachment(HttpServerExchange.BUFFERED_REQUEST_DATA);
         if (buffered != null) {
            return this.readsResumed;
         } else if (this.isFinished()) {
            return false;
         } else {
            return Bits.anyAreSet(HttpServerExchange.this.state, 262144) || super.isReadResumed();
         }
      }

      public int read(ByteBuffer dst) throws IOException {
         PooledByteBuffer[] buffered = (PooledByteBuffer[])HttpServerExchange.this.getAttachment(HttpServerExchange.BUFFERED_REQUEST_DATA);
         if (buffered == null) {
            return super.read(dst);
         } else {
            int copied = 0;

            for(int i = 0; i < buffered.length; ++i) {
               PooledByteBuffer pooled = buffered[i];
               if (pooled != null) {
                  ByteBuffer buf = pooled.getBuffer();
                  if (buf.hasRemaining()) {
                     copied += Buffers.copy(dst, buf);
                     if (!buf.hasRemaining()) {
                        pooled.close();
                        buffered[i] = null;
                     }

                     if (!dst.hasRemaining()) {
                        return copied;
                     }
                  } else {
                     pooled.close();
                     buffered[i] = null;
                  }
               }
            }

            HttpServerExchange.this.removeAttachment(HttpServerExchange.BUFFERED_REQUEST_DATA);
            if (copied == 0) {
               return super.read(dst);
            } else {
               return copied;
            }
         }
      }

      public void runResume() {
         if (this.isReadResumed()) {
            if (this.isFinished()) {
               this.invokeListener();
            } else if (this.wakeup) {
               this.wakeup = false;
               HttpServerExchange.this.state = HttpServerExchange.this.state & -262145;
               this.delegate.wakeupReads();
            } else {
               HttpServerExchange.this.state = HttpServerExchange.this.state & -262145;
               this.delegate.resumeReads();
            }
         } else if (this.wakeup) {
            this.wakeup = false;
            this.invokeListener();
         }

      }
   }

   private class WriteDispatchChannel extends DetachableStreamSinkChannel implements StreamSinkChannel {
      private boolean wakeup;

      WriteDispatchChannel(ConduitStreamSinkChannel delegate) {
         super(delegate);
      }

      protected boolean isFinished() {
         return Bits.allAreSet(HttpServerExchange.this.state, 2048);
      }

      public void resumeWrites() {
         if (HttpServerExchange.this.isInCall()) {
            HttpServerExchange.this.state = HttpServerExchange.this.state | 524288;
            if (Bits.anyAreSet(HttpServerExchange.this.state, 32768)) {
               throw UndertowMessages.MESSAGES.resumedAndDispatched();
            }
         } else if (!this.isFinished()) {
            this.delegate.resumeWrites();
         }

      }

      public void suspendWrites() {
         HttpServerExchange.this.state = HttpServerExchange.this.state & -524289;
         super.suspendWrites();
      }

      public void wakeupWrites() {
         if (!this.isFinished()) {
            if (HttpServerExchange.this.isInCall()) {
               this.wakeup = true;
               HttpServerExchange.this.state = HttpServerExchange.this.state | 524288;
               if (Bits.anyAreSet(HttpServerExchange.this.state, 32768)) {
                  throw UndertowMessages.MESSAGES.resumedAndDispatched();
               }
            } else {
               this.delegate.wakeupWrites();
            }

         }
      }

      public boolean isWriteResumed() {
         return Bits.anyAreSet(HttpServerExchange.this.state, 524288) || super.isWriteResumed();
      }

      public void runResume() {
         if (this.isWriteResumed()) {
            if (this.isFinished()) {
               this.invokeListener();
            } else if (this.wakeup) {
               this.wakeup = false;
               HttpServerExchange.this.state = HttpServerExchange.this.state & -524289;
               this.delegate.wakeupWrites();
            } else {
               HttpServerExchange.this.state = HttpServerExchange.this.state & -524289;
               this.delegate.resumeWrites();
            }
         } else if (this.wakeup) {
            this.wakeup = false;
            this.invokeListener();
         }

      }

      private void invokeListener() {
         if (this.writeSetter != null) {
            super.getIoThread().execute(new Runnable() {
               public void run() {
                  ChannelListeners.invokeChannelListener(WriteDispatchChannel.this, WriteDispatchChannel.this.writeSetter.get());
               }
            });
         }

      }

      public void awaitWritable() throws IOException {
         if (Thread.currentThread() == super.getIoThread()) {
            throw UndertowMessages.MESSAGES.awaitCalledFromIoThread();
         } else {
            super.awaitWritable();
         }
      }

      public void awaitWritable(long time, TimeUnit timeUnit) throws IOException {
         if (Thread.currentThread() == super.getIoThread()) {
            throw UndertowMessages.MESSAGES.awaitCalledFromIoThread();
         } else {
            super.awaitWritable(time, timeUnit);
         }
      }

      public long transferFrom(FileChannel src, long position, long count) throws IOException {
         long l = super.transferFrom(src, position, count);
         if (l > 0L) {
            HttpServerExchange.this.responseBytesSent = HttpServerExchange.this.responseBytesSent + l;
         }

         return l;
      }

      public long transferFrom(StreamSourceChannel source, long count, ByteBuffer throughBuffer) throws IOException {
         long l = super.transferFrom(source, count, throughBuffer);
         if (l > 0L) {
            HttpServerExchange.this.responseBytesSent = HttpServerExchange.this.responseBytesSent + l;
         }

         return l;
      }

      public long write(ByteBuffer[] srcs, int offset, int length) throws IOException {
         long l = super.write(srcs, offset, length);
         HttpServerExchange.this.responseBytesSent = HttpServerExchange.this.responseBytesSent + l;
         return l;
      }

      public long write(ByteBuffer[] srcs) throws IOException {
         long l = super.write(srcs);
         HttpServerExchange.this.responseBytesSent = HttpServerExchange.this.responseBytesSent + l;
         return l;
      }

      public int writeFinal(ByteBuffer src) throws IOException {
         int l = super.writeFinal(src);
         HttpServerExchange.this.responseBytesSent = HttpServerExchange.this.responseBytesSent + (long)l;
         return l;
      }

      public long writeFinal(ByteBuffer[] srcs, int offset, int length) throws IOException {
         long l = super.writeFinal(srcs, offset, length);
         HttpServerExchange.this.responseBytesSent = HttpServerExchange.this.responseBytesSent + l;
         return l;
      }

      public long writeFinal(ByteBuffer[] srcs) throws IOException {
         long l = super.writeFinal(srcs);
         HttpServerExchange.this.responseBytesSent = HttpServerExchange.this.responseBytesSent + l;
         return l;
      }

      public int write(ByteBuffer src) throws IOException {
         int l = super.write(src);
         HttpServerExchange.this.responseBytesSent = HttpServerExchange.this.responseBytesSent + (long)l;
         return l;
      }
   }

   private static class DefaultBlockingHttpExchange implements BlockingHttpExchange {
      private InputStream inputStream;
      private UndertowOutputStream outputStream;
      private Sender sender;
      private final HttpServerExchange exchange;

      DefaultBlockingHttpExchange(HttpServerExchange exchange) {
         this.exchange = exchange;
      }

      public InputStream getInputStream() {
         if (this.inputStream == null) {
            this.inputStream = new UndertowInputStream(this.exchange);
         }

         return this.inputStream;
      }

      public UndertowOutputStream getOutputStream() {
         if (this.outputStream == null) {
            this.outputStream = new UndertowOutputStream(this.exchange);
         }

         return this.outputStream;
      }

      public Sender getSender() {
         if (this.sender == null) {
            this.sender = new BlockingSenderImpl(this.exchange, this.getOutputStream());
         }

         return this.sender;
      }

      public void close() throws IOException {
         try {
            this.getInputStream().close();
         } finally {
            this.getOutputStream().close();
         }

      }

      public Receiver getReceiver() {
         return new BlockingReceiverImpl(this.exchange, this.getInputStream());
      }
   }

   private static class ExchangeCompleteNextListener implements ExchangeCompletionListener.NextListener {
      private final ExchangeCompletionListener[] list;
      private final HttpServerExchange exchange;
      private int i;

      ExchangeCompleteNextListener(ExchangeCompletionListener[] list, HttpServerExchange exchange, int i) {
         this.list = list;
         this.exchange = exchange;
         this.i = i;
      }

      public void proceed() {
         if (--this.i >= 0) {
            ExchangeCompletionListener next = this.list[this.i];
            next.exchangeEvent(this.exchange, this);
         } else if (this.i == -1) {
            this.exchange.connection.exchangeComplete(this.exchange);
         }

      }
   }
}
