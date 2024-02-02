/*      */ package io.undertow.server;
/*      */ 
/*      */ import io.undertow.UndertowLogger;
/*      */ import io.undertow.UndertowMessages;
/*      */ import io.undertow.UndertowOptions;
/*      */ import io.undertow.channels.DetachableStreamSinkChannel;
/*      */ import io.undertow.channels.DetachableStreamSourceChannel;
/*      */ import io.undertow.conduits.EmptyStreamSourceConduit;
/*      */ import io.undertow.connector.PooledByteBuffer;
/*      */ import io.undertow.io.AsyncReceiverImpl;
/*      */ import io.undertow.io.AsyncSenderImpl;
/*      */ import io.undertow.io.BlockingReceiverImpl;
/*      */ import io.undertow.io.BlockingSenderImpl;
/*      */ import io.undertow.io.Receiver;
/*      */ import io.undertow.io.Sender;
/*      */ import io.undertow.io.UndertowInputStream;
/*      */ import io.undertow.io.UndertowOutputStream;
/*      */ import io.undertow.security.api.SecurityContext;
/*      */ import io.undertow.server.handlers.Cookie;
/*      */ import io.undertow.util.AbstractAttachable;
/*      */ import io.undertow.util.AttachmentKey;
/*      */ import io.undertow.util.ConduitFactory;
/*      */ import io.undertow.util.Cookies;
/*      */ import io.undertow.util.HeaderMap;
/*      */ import io.undertow.util.Headers;
/*      */ import io.undertow.util.HttpString;
/*      */ import io.undertow.util.Methods;
/*      */ import io.undertow.util.NetworkUtils;
/*      */ import io.undertow.util.Protocols;
/*      */ import io.undertow.util.Rfc6265CookieSupport;
/*      */ import java.io.Closeable;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.io.OutputStream;
/*      */ import java.net.InetSocketAddress;
/*      */ import java.nio.Buffer;
/*      */ import java.nio.ByteBuffer;
/*      */ import java.nio.channels.Channel;
/*      */ import java.nio.channels.FileChannel;
/*      */ import java.nio.channels.ReadableByteChannel;
/*      */ import java.util.ArrayDeque;
/*      */ import java.util.Deque;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Set;
/*      */ import java.util.TreeMap;
/*      */ import java.util.concurrent.Executor;
/*      */ import java.util.concurrent.TimeUnit;
/*      */ import org.jboss.logging.Logger;
/*      */ import org.xnio.Bits;
/*      */ import org.xnio.Buffers;
/*      */ import org.xnio.ChannelExceptionHandler;
/*      */ import org.xnio.ChannelListener;
/*      */ import org.xnio.ChannelListeners;
/*      */ import org.xnio.IoUtils;
/*      */ import org.xnio.XnioIoThread;
/*      */ import org.xnio.channels.Channels;
/*      */ import org.xnio.channels.Configurable;
/*      */ import org.xnio.channels.StreamSinkChannel;
/*      */ import org.xnio.channels.StreamSourceChannel;
/*      */ import org.xnio.conduits.Conduit;
/*      */ import org.xnio.conduits.ConduitStreamSinkChannel;
/*      */ import org.xnio.conduits.ConduitStreamSourceChannel;
/*      */ import org.xnio.conduits.StreamSinkConduit;
/*      */ import org.xnio.conduits.StreamSourceConduit;
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public final class HttpServerExchange
/*      */   extends AbstractAttachable
/*      */ {
/*   98 */   private static final Logger log = Logger.getLogger(HttpServerExchange.class);
/*      */   
/*  100 */   private static final RuntimePermission SET_SECURITY_CONTEXT = new RuntimePermission("io.undertow.SET_SECURITY_CONTEXT");
/*      */ 
/*      */   
/*      */   private static final String ISO_8859_1 = "ISO-8859-1";
/*      */ 
/*      */   
/*      */   private static final String HTTPS = "https";
/*      */   
/*  108 */   private static final AttachmentKey<String> REASON_PHRASE = AttachmentKey.create(String.class);
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  113 */   static final AttachmentKey<PooledByteBuffer[]> BUFFERED_REQUEST_DATA = AttachmentKey.create(PooledByteBuffer[].class);
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  118 */   public static final AttachmentKey<Map<String, String>> REQUEST_ATTRIBUTES = AttachmentKey.create(Map.class);
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  123 */   public static final AttachmentKey<String> REMOTE_USER = AttachmentKey.create(String.class);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  129 */   public static final AttachmentKey<Boolean> SECURE_REQUEST = AttachmentKey.create(Boolean.class);
/*      */   
/*      */   private final ServerConnection connection;
/*      */   
/*      */   private final HeaderMap requestHeaders;
/*      */   private final HeaderMap responseHeaders;
/*  135 */   private int exchangeCompletionListenersCount = 0;
/*      */ 
/*      */   
/*      */   private ExchangeCompletionListener[] exchangeCompleteListeners;
/*      */ 
/*      */   
/*      */   private DefaultResponseListener[] defaultResponseListeners;
/*      */ 
/*      */   
/*      */   private Map<String, Deque<String>> queryParameters;
/*      */ 
/*      */   
/*      */   private Map<String, Deque<String>> pathParameters;
/*      */ 
/*      */   
/*      */   private DelegatingIterable<Cookie> requestCookies;
/*      */   
/*      */   private DelegatingIterable<Cookie> responseCookies;
/*      */   
/*      */   private Map<String, Cookie> deprecatedRequestCookies;
/*      */   
/*      */   private Map<String, Cookie> deprecatedResponseCookies;
/*      */   
/*      */   private WriteDispatchChannel responseChannel;
/*      */   
/*      */   protected ReadDispatchChannel requestChannel;
/*      */   
/*      */   private BlockingHttpExchange blockingHttpExchange;
/*      */   
/*      */   private HttpString protocol;
/*      */   
/*      */   private SecurityContext securityContext;
/*      */   
/*  168 */   private int state = 200;
/*  169 */   private HttpString requestMethod = HttpString.EMPTY;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private String requestScheme;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private String requestURI;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private String requestPath;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private String relativePath;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  205 */   private String resolvedPath = "";
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  210 */   private String queryString = "";
/*      */   
/*  212 */   private int requestWrapperCount = 0;
/*      */   
/*      */   private ConduitWrapper<StreamSourceConduit>[] requestWrappers;
/*  215 */   private int responseWrapperCount = 0;
/*      */   
/*      */   private ConduitWrapper<StreamSinkConduit>[] responseWrappers;
/*      */   
/*      */   private Sender sender;
/*      */   private Receiver receiver;
/*  221 */   private long requestStartTime = -1L;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private long maxEntitySize;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private Runnable dispatchTask;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private Executor dispatchExecutor;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  260 */   private long responseBytesSent = 0L;
/*      */ 
/*      */   
/*  263 */   private static final int MASK_RESPONSE_CODE = Bits.intBitMask(0, 9);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static final int FLAG_RESPONSE_SENT = 1024;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static final int FLAG_RESPONSE_TERMINATED = 2048;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static final int FLAG_REQUEST_TERMINATED = 4096;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static final int FLAG_PERSISTENT = 16384;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static final int FLAG_DISPATCHED = 32768;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static final int FLAG_URI_CONTAINS_HOST = 65536;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static final int FLAG_IN_CALL = 131072;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static final int FLAG_SHOULD_RESUME_READS = 262144;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static final int FLAG_SHOULD_RESUME_WRITES = 524288;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static final int FLAG_REQUEST_RESET = 1048576;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private InetSocketAddress sourceAddress;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private InetSocketAddress destinationAddress;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public HttpServerExchange(ServerConnection connection, long maxEntitySize) {
/*  340 */     this(connection, new HeaderMap(), new HeaderMap(), maxEntitySize);
/*      */   }
/*      */   
/*      */   public HttpServerExchange(ServerConnection connection) {
/*  344 */     this(connection, 0L);
/*      */   }
/*      */   
/*      */   public HttpServerExchange(ServerConnection connection, HeaderMap requestHeaders, HeaderMap responseHeaders, long maxEntitySize) {
/*  348 */     this.connection = connection;
/*  349 */     this.maxEntitySize = maxEntitySize;
/*  350 */     this.requestHeaders = requestHeaders;
/*  351 */     this.responseHeaders = responseHeaders;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public HttpString getProtocol() {
/*  360 */     return this.protocol;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public HttpServerExchange setProtocol(HttpString protocol) {
/*  369 */     this.protocol = protocol;
/*  370 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isHttp09() {
/*  379 */     return this.protocol.equals(Protocols.HTTP_0_9);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isHttp10() {
/*  388 */     return this.protocol.equals(Protocols.HTTP_1_0);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isHttp11() {
/*  397 */     return this.protocol.equals(Protocols.HTTP_1_1);
/*      */   }
/*      */   
/*      */   public boolean isSecure() {
/*  401 */     Boolean secure = (Boolean)getAttachment(SECURE_REQUEST);
/*  402 */     if (secure != null && secure.booleanValue()) {
/*  403 */       return true;
/*      */     }
/*  405 */     String scheme = getRequestScheme();
/*  406 */     if (scheme != null && scheme.equalsIgnoreCase("https")) {
/*  407 */       return true;
/*      */     }
/*  409 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public HttpString getRequestMethod() {
/*  418 */     return this.requestMethod;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public HttpServerExchange setRequestMethod(HttpString requestMethod) {
/*  427 */     this.requestMethod = requestMethod;
/*  428 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String getRequestScheme() {
/*  437 */     return this.requestScheme;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public HttpServerExchange setRequestScheme(String requestScheme) {
/*  446 */     this.requestScheme = requestScheme;
/*  447 */     return this;
/*      */   }
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
/*      */   public String getRequestURI() {
/*  461 */     return this.requestURI;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public HttpServerExchange setRequestURI(String requestURI) {
/*  470 */     this.requestURI = requestURI;
/*  471 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public HttpServerExchange setRequestURI(String requestURI, boolean containsHost) {
/*  481 */     this.requestURI = requestURI;
/*  482 */     if (containsHost) {
/*  483 */       this.state |= 0x10000;
/*      */     } else {
/*  485 */       this.state &= 0xFFFEFFFF;
/*      */     } 
/*  487 */     return this;
/*      */   }
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
/*      */   public boolean isHostIncludedInRequestURI() {
/*  500 */     return Bits.anyAreSet(this.state, 65536);
/*      */   }
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
/*      */   public String getRequestPath() {
/*  514 */     return this.requestPath;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public HttpServerExchange setRequestPath(String requestPath) {
/*  523 */     this.requestPath = requestPath;
/*  524 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String getRelativePath() {
/*  536 */     return this.relativePath;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public HttpServerExchange setRelativePath(String relativePath) {
/*  545 */     this.relativePath = relativePath;
/*  546 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String getResolvedPath() {
/*  555 */     return this.resolvedPath;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public HttpServerExchange setResolvedPath(String resolvedPath) {
/*  564 */     this.resolvedPath = resolvedPath;
/*  565 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String getQueryString() {
/*  573 */     return this.queryString;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public HttpServerExchange setQueryString(String queryString) {
/*  581 */     if (queryString.length() > 0 && queryString.charAt(0) == '?') {
/*  582 */       this.queryString = queryString.substring(1);
/*      */     } else {
/*  584 */       this.queryString = queryString;
/*      */     } 
/*  586 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String getRequestURL() {
/*  596 */     if (isHostIncludedInRequestURI()) {
/*  597 */       return getRequestURI();
/*      */     }
/*  599 */     return getRequestScheme() + "://" + getHostAndPort() + getRequestURI();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String getRequestCharset() {
/*  610 */     return extractCharset(this.requestHeaders);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String getResponseCharset() {
/*  620 */     HeaderMap headers = this.responseHeaders;
/*  621 */     return extractCharset(headers);
/*      */   }
/*      */   
/*      */   private String extractCharset(HeaderMap headers) {
/*  625 */     String contentType = headers.getFirst(Headers.CONTENT_TYPE);
/*  626 */     if (contentType != null) {
/*  627 */       String value = Headers.extractQuotedValueFromHeader(contentType, "charset");
/*  628 */       if (value != null) {
/*  629 */         return value;
/*      */       }
/*      */     } 
/*  632 */     return "ISO-8859-1";
/*      */   }
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
/*      */   public String getHostName() {
/*  646 */     String host = this.requestHeaders.getFirst(Headers.HOST);
/*  647 */     if (host == null || "".equals(host.trim())) {
/*  648 */       host = getDestinationAddress().getHostString();
/*      */     }
/*  650 */     else if (host.startsWith("[")) {
/*  651 */       host = host.substring(1, host.indexOf(']'));
/*  652 */     } else if (host.indexOf(':') != -1) {
/*  653 */       host = host.substring(0, host.indexOf(':'));
/*      */     } 
/*      */     
/*  656 */     return host;
/*      */   }
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
/*      */   public String getHostAndPort() {
/*  669 */     String host = this.requestHeaders.getFirst(Headers.HOST);
/*  670 */     if (host == null || "".equals(host.trim())) {
/*  671 */       InetSocketAddress address = getDestinationAddress();
/*  672 */       host = NetworkUtils.formatPossibleIpv6Address(address.getHostString());
/*  673 */       int port = address.getPort();
/*  674 */       if ((!getRequestScheme().equals("http") || port != 80) && (
/*  675 */         !getRequestScheme().equals("https") || port != 443)) {
/*  676 */         host = host + ":" + port;
/*      */       }
/*      */     } 
/*  679 */     return host;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getHostPort() {
/*  689 */     String host = this.requestHeaders.getFirst(Headers.HOST);
/*  690 */     if (host != null) {
/*      */       int colonIndex;
/*      */       
/*  693 */       if (host.startsWith("[")) {
/*  694 */         colonIndex = host.indexOf(':', host.indexOf(']'));
/*      */       } else {
/*  696 */         colonIndex = host.indexOf(':');
/*      */       } 
/*  698 */       if (colonIndex != -1) {
/*      */         try {
/*  700 */           return Integer.parseInt(host.substring(colonIndex + 1));
/*  701 */         } catch (NumberFormatException numberFormatException) {}
/*      */       }
/*  703 */       if (getRequestScheme().equals("https"))
/*  704 */         return 443; 
/*  705 */       if (getRequestScheme().equals("http")) {
/*  706 */         return 80;
/*      */       }
/*      */     } 
/*      */     
/*  710 */     return getDestinationAddress().getPort();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ServerConnection getConnection() {
/*  719 */     return this.connection;
/*      */   }
/*      */   
/*      */   public boolean isPersistent() {
/*  723 */     return Bits.anyAreSet(this.state, 16384);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isInIoThread() {
/*  731 */     return (getIoThread() == Thread.currentThread());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isUpgrade() {
/*  739 */     return (getStatusCode() == 101);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public long getResponseBytesSent() {
/*  747 */     if (Connectors.isEntityBodyAllowed(this) && !getRequestMethod().equals(Methods.HEAD)) {
/*  748 */       return this.responseBytesSent;
/*      */     }
/*  750 */     return 0L;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   void updateBytesSent(long bytes) {
/*  759 */     if (Connectors.isEntityBodyAllowed(this) && !getRequestMethod().equals(Methods.HEAD)) {
/*  760 */       this.responseBytesSent += bytes;
/*      */     }
/*      */   }
/*      */   
/*      */   public HttpServerExchange setPersistent(boolean persistent) {
/*  765 */     if (persistent) {
/*  766 */       this.state |= 0x4000;
/*      */     } else {
/*  768 */       this.state &= 0xFFFFBFFF;
/*      */     } 
/*  770 */     return this;
/*      */   }
/*      */   
/*      */   public boolean isDispatched() {
/*  774 */     return Bits.anyAreSet(this.state, 32768);
/*      */   }
/*      */   
/*      */   public HttpServerExchange unDispatch() {
/*  778 */     this.state &= 0xFFFF7FFF;
/*  779 */     this.dispatchTask = null;
/*  780 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public HttpServerExchange dispatch() {
/*  792 */     this.state |= 0x8000;
/*  793 */     return this;
/*      */   }
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
/*      */   public HttpServerExchange dispatch(Runnable runnable) {
/*  808 */     dispatch((Executor)null, runnable);
/*  809 */     return this;
/*      */   }
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
/*      */   public HttpServerExchange dispatch(Executor executor, Runnable runnable) {
/*  824 */     if (isInCall()) {
/*  825 */       if (executor != null) {
/*  826 */         this.dispatchExecutor = executor;
/*      */       }
/*  828 */       this.state |= 0x8000;
/*  829 */       if (Bits.anyAreSet(this.state, 786432)) {
/*  830 */         throw UndertowMessages.MESSAGES.resumedAndDispatched();
/*      */       }
/*  832 */       this.dispatchTask = runnable;
/*      */     }
/*  834 */     else if (executor == null) {
/*  835 */       getConnection().getWorker().execute(runnable);
/*      */     } else {
/*  837 */       executor.execute(runnable);
/*      */     } 
/*      */     
/*  840 */     return this;
/*      */   }
/*      */   
/*      */   public HttpServerExchange dispatch(HttpHandler handler) {
/*  844 */     dispatch((Executor)null, handler);
/*  845 */     return this;
/*      */   }
/*      */   
/*      */   public HttpServerExchange dispatch(Executor executor, final HttpHandler handler) {
/*  849 */     Runnable runnable = new Runnable()
/*      */       {
/*      */         public void run() {
/*  852 */           Connectors.executeRootHandler(handler, HttpServerExchange.this);
/*      */         }
/*      */       };
/*  855 */     dispatch(executor, runnable);
/*  856 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public HttpServerExchange setDispatchExecutor(Executor executor) {
/*  865 */     if (executor == null) {
/*  866 */       this.dispatchExecutor = null;
/*      */     } else {
/*  868 */       this.dispatchExecutor = executor;
/*      */     } 
/*  870 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Executor getDispatchExecutor() {
/*  879 */     return this.dispatchExecutor;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   Runnable getDispatchTask() {
/*  886 */     return this.dispatchTask;
/*      */   }
/*      */   
/*      */   boolean isInCall() {
/*  890 */     return Bits.anyAreSet(this.state, 131072);
/*      */   }
/*      */   
/*      */   HttpServerExchange setInCall(boolean value) {
/*  894 */     if (value) {
/*  895 */       this.state |= 0x20000;
/*      */     } else {
/*  897 */       this.state &= 0xFFFDFFFF;
/*      */     } 
/*  899 */     return this;
/*      */   }
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
/*      */   public HttpServerExchange upgradeChannel(HttpUpgradeListener listener) {
/*  912 */     if (!this.connection.isUpgradeSupported()) {
/*  913 */       throw UndertowMessages.MESSAGES.upgradeNotSupported();
/*      */     }
/*  915 */     if (!getRequestHeaders().contains(Headers.UPGRADE)) {
/*  916 */       throw UndertowMessages.MESSAGES.notAnUpgradeRequest();
/*      */     }
/*  918 */     UndertowLogger.REQUEST_LOGGER.debugf("Upgrading request %s", this);
/*  919 */     this.connection.setUpgradeListener(listener);
/*  920 */     setStatusCode(101);
/*  921 */     getResponseHeaders().put(Headers.CONNECTION, "Upgrade");
/*  922 */     return this;
/*      */   }
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
/*      */   public HttpServerExchange upgradeChannel(String productName, HttpUpgradeListener listener) {
/*  935 */     if (!this.connection.isUpgradeSupported()) {
/*  936 */       throw UndertowMessages.MESSAGES.upgradeNotSupported();
/*      */     }
/*  938 */     UndertowLogger.REQUEST_LOGGER.debugf("Upgrading request %s", this);
/*  939 */     this.connection.setUpgradeListener(listener);
/*  940 */     setStatusCode(101);
/*  941 */     HeaderMap headers = getResponseHeaders();
/*  942 */     headers.put(Headers.UPGRADE, productName);
/*  943 */     headers.put(Headers.CONNECTION, "Upgrade");
/*  944 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public HttpServerExchange acceptConnectRequest(HttpUpgradeListener connectListener) {
/*  953 */     if (!getRequestMethod().equals(Methods.CONNECT)) {
/*  954 */       throw UndertowMessages.MESSAGES.notAConnectRequest();
/*      */     }
/*  956 */     this.connection.setConnectListener(connectListener);
/*  957 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public HttpServerExchange addExchangeCompleteListener(ExchangeCompletionListener listener) {
/*  962 */     if (isComplete() || this.exchangeCompletionListenersCount == -1) {
/*  963 */       throw UndertowMessages.MESSAGES.exchangeAlreadyComplete();
/*      */     }
/*  965 */     int exchangeCompletionListenersCount = this.exchangeCompletionListenersCount++;
/*  966 */     ExchangeCompletionListener[] exchangeCompleteListeners = this.exchangeCompleteListeners;
/*  967 */     if (exchangeCompleteListeners == null || exchangeCompleteListeners.length == exchangeCompletionListenersCount) {
/*  968 */       ExchangeCompletionListener[] old = exchangeCompleteListeners;
/*  969 */       this.exchangeCompleteListeners = exchangeCompleteListeners = new ExchangeCompletionListener[exchangeCompletionListenersCount + 2];
/*  970 */       if (old != null) {
/*  971 */         System.arraycopy(old, 0, exchangeCompleteListeners, 0, exchangeCompletionListenersCount);
/*      */       }
/*      */     } 
/*  974 */     exchangeCompleteListeners[exchangeCompletionListenersCount] = listener;
/*  975 */     return this;
/*      */   }
/*      */   
/*      */   public HttpServerExchange addDefaultResponseListener(DefaultResponseListener listener) {
/*  979 */     int i = 0;
/*  980 */     if (this.defaultResponseListeners == null) {
/*  981 */       this.defaultResponseListeners = new DefaultResponseListener[2];
/*      */     } else {
/*  983 */       while (i != this.defaultResponseListeners.length && this.defaultResponseListeners[i] != null) {
/*  984 */         i++;
/*      */       }
/*  986 */       if (i == this.defaultResponseListeners.length) {
/*  987 */         DefaultResponseListener[] old = this.defaultResponseListeners;
/*  988 */         this.defaultResponseListeners = new DefaultResponseListener[this.defaultResponseListeners.length + 2];
/*  989 */         System.arraycopy(old, 0, this.defaultResponseListeners, 0, old.length);
/*      */       } 
/*      */     } 
/*  992 */     this.defaultResponseListeners[i] = listener;
/*  993 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public InetSocketAddress getSourceAddress() {
/* 1002 */     if (this.sourceAddress != null) {
/* 1003 */       return this.sourceAddress;
/*      */     }
/* 1005 */     return this.connection.<InetSocketAddress>getPeerAddress(InetSocketAddress.class);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public HttpServerExchange setSourceAddress(InetSocketAddress sourceAddress) {
/* 1015 */     this.sourceAddress = sourceAddress;
/* 1016 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public InetSocketAddress getDestinationAddress() {
/* 1025 */     if (this.destinationAddress != null) {
/* 1026 */       return this.destinationAddress;
/*      */     }
/* 1028 */     return this.connection.<InetSocketAddress>getLocalAddress(InetSocketAddress.class);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public HttpServerExchange setDestinationAddress(InetSocketAddress destinationAddress) {
/* 1038 */     this.destinationAddress = destinationAddress;
/* 1039 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public HeaderMap getRequestHeaders() {
/* 1048 */     return this.requestHeaders;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public long getRequestContentLength() {
/* 1055 */     String contentLengthString = this.requestHeaders.getFirst(Headers.CONTENT_LENGTH);
/* 1056 */     if (contentLengthString == null) {
/* 1057 */       return -1L;
/*      */     }
/* 1059 */     return Long.parseLong(contentLengthString);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public HeaderMap getResponseHeaders() {
/* 1068 */     return this.responseHeaders;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public long getResponseContentLength() {
/* 1075 */     String contentLengthString = this.responseHeaders.getFirst(Headers.CONTENT_LENGTH);
/* 1076 */     if (contentLengthString == null) {
/* 1077 */       return -1L;
/*      */     }
/* 1079 */     return Long.parseLong(contentLengthString);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public HttpServerExchange setResponseContentLength(long length) {
/* 1088 */     if (length == -1L) {
/* 1089 */       this.responseHeaders.remove(Headers.CONTENT_LENGTH);
/*      */     } else {
/* 1091 */       this.responseHeaders.put(Headers.CONTENT_LENGTH, Long.toString(length));
/*      */     } 
/* 1093 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Map<String, Deque<String>> getQueryParameters() {
/* 1102 */     if (this.queryParameters == null) {
/* 1103 */       this.queryParameters = new TreeMap<>();
/*      */     }
/* 1105 */     return this.queryParameters;
/*      */   }
/*      */   
/*      */   public HttpServerExchange addQueryParam(String name, String param) {
/* 1109 */     if (this.queryParameters == null) {
/* 1110 */       this.queryParameters = new TreeMap<>();
/*      */     }
/* 1112 */     Deque<String> list = this.queryParameters.get(name);
/* 1113 */     if (list == null) {
/* 1114 */       this.queryParameters.put(name, list = new ArrayDeque<>(2));
/*      */     }
/* 1116 */     list.add(param);
/* 1117 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Map<String, Deque<String>> getPathParameters() {
/* 1127 */     if (this.pathParameters == null) {
/* 1128 */       this.pathParameters = new TreeMap<>();
/*      */     }
/* 1130 */     return this.pathParameters;
/*      */   }
/*      */   
/*      */   public HttpServerExchange addPathParam(String name, String param) {
/* 1134 */     if (this.pathParameters == null) {
/* 1135 */       this.pathParameters = new TreeMap<>();
/*      */     }
/* 1137 */     Deque<String> list = this.pathParameters.get(name);
/* 1138 */     if (list == null) {
/* 1139 */       this.pathParameters.put(name, list = new ArrayDeque<>(2));
/*      */     }
/* 1141 */     list.add(param);
/* 1142 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public Map<String, Cookie> getRequestCookies() {
/* 1151 */     if (this.deprecatedRequestCookies == null) {
/* 1152 */       this.deprecatedRequestCookies = new MapDelegatingToSet((Set)((DelegatingIterable)requestCookies()).getDelegate());
/*      */     }
/* 1154 */     return this.deprecatedRequestCookies;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public HttpServerExchange setRequestCookie(Cookie cookie) {
/* 1163 */     if (cookie == null) return this; 
/* 1164 */     if (getConnection().getUndertowOptions().get(UndertowOptions.ENABLE_RFC6265_COOKIE_VALIDATION, false)) {
/* 1165 */       if (cookie.getValue() != null && !cookie.getValue().isEmpty()) {
/* 1166 */         Rfc6265CookieSupport.validateCookieValue(cookie.getValue());
/*      */       }
/* 1168 */       if (cookie.getPath() != null && !cookie.getPath().isEmpty()) {
/* 1169 */         Rfc6265CookieSupport.validatePath(cookie.getPath());
/*      */       }
/* 1171 */       if (cookie.getDomain() != null && !cookie.getDomain().isEmpty()) {
/* 1172 */         Rfc6265CookieSupport.validateDomain(cookie.getDomain());
/*      */       }
/*      */     } 
/* 1175 */     ((Set)((DelegatingIterable)requestCookies()).getDelegate()).add(cookie);
/* 1176 */     return this;
/*      */   }
/*      */   
/*      */   public Cookie getRequestCookie(String name) {
/* 1180 */     if (name == null) return null; 
/* 1181 */     for (Cookie cookie : requestCookies()) {
/* 1182 */       if (name.equals(cookie.getName()))
/*      */       {
/*      */ 
/*      */         
/* 1186 */         return cookie;
/*      */       }
/*      */     } 
/* 1189 */     return null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Iterable<Cookie> requestCookies() {
/* 1197 */     if (this.requestCookies == null) {
/* 1198 */       Set<Cookie> requestCookiesParam = new OverridableTreeSet<>();
/* 1199 */       this.requestCookies = new DelegatingIterable<>(requestCookiesParam);
/* 1200 */       Cookies.parseRequestCookies(
/* 1201 */           getConnection().getUndertowOptions().get(UndertowOptions.MAX_COOKIES, 200), 
/* 1202 */           getConnection().getUndertowOptions().get(UndertowOptions.ALLOW_EQUALS_IN_COOKIE_VALUE, false), (List)this.requestHeaders
/* 1203 */           .get(Headers.COOKIE), requestCookiesParam);
/*      */     } 
/* 1205 */     return this.requestCookies;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public HttpServerExchange setResponseCookie(Cookie cookie) {
/* 1214 */     if (cookie == null) return this; 
/* 1215 */     if (getConnection().getUndertowOptions().get(UndertowOptions.ENABLE_RFC6265_COOKIE_VALIDATION, false)) {
/* 1216 */       if (cookie.getValue() != null && !cookie.getValue().isEmpty()) {
/* 1217 */         Rfc6265CookieSupport.validateCookieValue(cookie.getValue());
/*      */       }
/* 1219 */       if (cookie.getPath() != null && !cookie.getPath().isEmpty()) {
/* 1220 */         Rfc6265CookieSupport.validatePath(cookie.getPath());
/*      */       }
/* 1222 */       if (cookie.getDomain() != null && !cookie.getDomain().isEmpty()) {
/* 1223 */         Rfc6265CookieSupport.validateDomain(cookie.getDomain());
/*      */       }
/*      */     } 
/* 1226 */     ((Set)((DelegatingIterable)responseCookies()).getDelegate()).add(cookie);
/* 1227 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public Map<String, Cookie> getResponseCookies() {
/* 1236 */     if (this.deprecatedResponseCookies == null) {
/* 1237 */       this.deprecatedResponseCookies = new MapDelegatingToSet((Set)((DelegatingIterable)responseCookies()).getDelegate());
/*      */     }
/* 1239 */     return this.deprecatedResponseCookies;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Iterable<Cookie> responseCookies() {
/* 1247 */     if (this.responseCookies == null) {
/* 1248 */       this.responseCookies = new DelegatingIterable<>(new OverridableTreeSet<>());
/*      */     }
/* 1250 */     return this.responseCookies;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isResponseStarted() {
/* 1257 */     return Bits.allAreSet(this.state, 1024);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public StreamSourceChannel getRequestChannel() {
/* 1269 */     if (this.requestChannel != null) {
/* 1270 */       if (Bits.anyAreSet(this.state, 1048576)) {
/* 1271 */         this.state &= 0xFFEFFFFF;
/* 1272 */         return this.requestChannel;
/*      */       } 
/* 1274 */       return null;
/*      */     } 
/* 1276 */     if (Bits.anyAreSet(this.state, 4096)) {
/* 1277 */       return this.requestChannel = new ReadDispatchChannel(new ConduitStreamSourceChannel(Configurable.EMPTY, (StreamSourceConduit)new EmptyStreamSourceConduit(getIoThread())));
/*      */     }
/* 1279 */     ConduitWrapper<StreamSourceConduit>[] wrappers = this.requestWrappers;
/* 1280 */     ConduitStreamSourceChannel sourceChannel = this.connection.getSourceChannel();
/* 1281 */     if (wrappers != null) {
/* 1282 */       this.requestWrappers = null;
/* 1283 */       WrapperConduitFactory<StreamSourceConduit> factory = new WrapperConduitFactory<>(wrappers, this.requestWrapperCount, sourceChannel.getConduit(), this);
/* 1284 */       sourceChannel.setConduit(factory.create());
/*      */     } 
/* 1286 */     return this.requestChannel = new ReadDispatchChannel(sourceChannel);
/*      */   }
/*      */   
/*      */   void resetRequestChannel() {
/* 1290 */     this.state |= 0x100000;
/*      */   }
/*      */   
/*      */   public boolean isRequestChannelAvailable() {
/* 1294 */     return (this.requestChannel == null || Bits.anyAreSet(this.state, 1048576));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isComplete() {
/* 1302 */     return Bits.allAreSet(this.state, 6144);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isRequestComplete() {
/* 1312 */     PooledByteBuffer[] data = (PooledByteBuffer[])getAttachment(BUFFERED_REQUEST_DATA);
/* 1313 */     if (data != null) {
/* 1314 */       return false;
/*      */     }
/* 1316 */     return Bits.allAreSet(this.state, 4096);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isResponseComplete() {
/* 1323 */     return Bits.allAreSet(this.state, 2048);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   void terminateRequest() {
/* 1331 */     int oldVal = this.state;
/* 1332 */     if (Bits.allAreSet(oldVal, 4096)) {
/*      */       return;
/*      */     }
/*      */     
/* 1336 */     if (this.requestChannel != null) {
/* 1337 */       this.requestChannel.requestDone();
/*      */     }
/* 1339 */     this.state = oldVal | 0x1000;
/* 1340 */     if (Bits.anyAreSet(oldVal, 2048)) {
/* 1341 */       invokeExchangeCompleteListeners();
/*      */     }
/*      */   }
/*      */   
/*      */   private void invokeExchangeCompleteListeners() {
/* 1346 */     if (this.exchangeCompletionListenersCount > 0) {
/* 1347 */       int i = this.exchangeCompletionListenersCount - 1;
/* 1348 */       ExchangeCompletionListener next = this.exchangeCompleteListeners[i];
/* 1349 */       this.exchangeCompletionListenersCount = -1;
/* 1350 */       next.exchangeEvent(this, new ExchangeCompleteNextListener(this.exchangeCompleteListeners, this, i));
/* 1351 */     } else if (this.exchangeCompletionListenersCount == 0) {
/* 1352 */       this.exchangeCompletionListenersCount = -1;
/* 1353 */       this.connection.exchangeComplete(this);
/*      */     } 
/*      */   }
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public StreamSinkChannel getResponseChannel() {
/* 1378 */     if (this.responseChannel != null) {
/* 1379 */       return null;
/*      */     }
/* 1381 */     ConduitWrapper<StreamSinkConduit>[] wrappers = this.responseWrappers;
/* 1382 */     this.responseWrappers = null;
/* 1383 */     ConduitStreamSinkChannel sinkChannel = this.connection.getSinkChannel();
/* 1384 */     if (sinkChannel == null) {
/* 1385 */       return null;
/*      */     }
/* 1387 */     if (wrappers != null) {
/* 1388 */       WrapperStreamSinkConduitFactory factory = new WrapperStreamSinkConduitFactory(wrappers, this.responseWrapperCount, this, sinkChannel.getConduit());
/* 1389 */       sinkChannel.setConduit(factory.create());
/*      */     } else {
/* 1391 */       sinkChannel.setConduit(this.connection.getSinkConduit(this, sinkChannel.getConduit()));
/*      */     } 
/* 1393 */     this.responseChannel = new WriteDispatchChannel(sinkChannel);
/* 1394 */     startResponse();
/* 1395 */     return this.responseChannel;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Sender getResponseSender() {
/* 1407 */     if (this.blockingHttpExchange != null) {
/* 1408 */       return this.blockingHttpExchange.getSender();
/*      */     }
/* 1410 */     if (this.sender != null) {
/* 1411 */       return this.sender;
/*      */     }
/* 1413 */     return this.sender = (Sender)new AsyncSenderImpl(this);
/*      */   }
/*      */   
/*      */   public Receiver getRequestReceiver() {
/* 1417 */     if (this.blockingHttpExchange != null) {
/* 1418 */       return this.blockingHttpExchange.getReceiver();
/*      */     }
/* 1420 */     if (this.receiver != null) {
/* 1421 */       return this.receiver;
/*      */     }
/* 1423 */     return this.receiver = (Receiver)new AsyncReceiverImpl(this);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isResponseChannelAvailable() {
/* 1430 */     return (this.responseChannel == null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public int getResponseCode() {
/* 1442 */     return this.state & MASK_RESPONSE_CODE;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public HttpServerExchange setResponseCode(int statusCode) {
/* 1455 */     return setStatusCode(statusCode);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getStatusCode() {
/* 1464 */     return this.state & MASK_RESPONSE_CODE;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public HttpServerExchange setStatusCode(int statusCode) {
/* 1475 */     if (statusCode < 0 || statusCode > 999) {
/* 1476 */       throw new IllegalArgumentException("Invalid response code");
/*      */     }
/* 1478 */     int oldVal = this.state;
/* 1479 */     if (Bits.allAreSet(oldVal, 1024)) {
/* 1480 */       throw UndertowMessages.MESSAGES.responseAlreadyStarted();
/*      */     }
/* 1482 */     if (statusCode >= 500 && 
/* 1483 */       UndertowLogger.ERROR_RESPONSE.isDebugEnabled()) {
/* 1484 */       UndertowLogger.ERROR_RESPONSE.debugf(new RuntimeException(), "Setting error code %s for exchange %s", statusCode, this);
/*      */     }
/*      */     
/* 1487 */     this.state = oldVal & (MASK_RESPONSE_CODE ^ 0xFFFFFFFF) | statusCode & MASK_RESPONSE_CODE;
/* 1488 */     return this;
/*      */   }
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
/*      */   public HttpServerExchange setReasonPhrase(String message) {
/* 1501 */     putAttachment(REASON_PHRASE, message);
/* 1502 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String getReasonPhrase() {
/* 1510 */     return (String)getAttachment(REASON_PHRASE);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public HttpServerExchange addRequestWrapper(ConduitWrapper<StreamSourceConduit> wrapper) {
/* 1519 */     ConduitWrapper<StreamSourceConduit>[] wrappers = this.requestWrappers;
/* 1520 */     if (this.requestChannel != null) {
/* 1521 */       throw UndertowMessages.MESSAGES.requestChannelAlreadyProvided();
/*      */     }
/* 1523 */     if (wrappers == null) {
/* 1524 */       wrappers = this.requestWrappers = (ConduitWrapper<StreamSourceConduit>[])new ConduitWrapper[2];
/* 1525 */     } else if (wrappers.length == this.requestWrapperCount) {
/* 1526 */       this.requestWrappers = (ConduitWrapper<StreamSourceConduit>[])new ConduitWrapper[wrappers.length + 2];
/* 1527 */       System.arraycopy(wrappers, 0, this.requestWrappers, 0, wrappers.length);
/* 1528 */       wrappers = this.requestWrappers;
/*      */     } 
/* 1530 */     wrappers[this.requestWrapperCount++] = wrapper;
/* 1531 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public HttpServerExchange addResponseWrapper(ConduitWrapper<StreamSinkConduit> wrapper) {
/*      */     ConduitWrapper[] arrayOfConduitWrapper;
/* 1540 */     ConduitWrapper<StreamSinkConduit>[] arrayOfConduitWrapper1, wrappers = this.responseWrappers;
/* 1541 */     if (this.responseChannel != null) {
/* 1542 */       throw UndertowMessages.MESSAGES.responseChannelAlreadyProvided();
/*      */     }
/* 1544 */     if (wrappers == null) {
/* 1545 */       this.responseWrappers = (ConduitWrapper<StreamSinkConduit>[])(arrayOfConduitWrapper = new ConduitWrapper[2]);
/* 1546 */     } else if (arrayOfConduitWrapper.length == this.responseWrapperCount) {
/* 1547 */       this.responseWrappers = (ConduitWrapper<StreamSinkConduit>[])new ConduitWrapper[arrayOfConduitWrapper.length + 2];
/* 1548 */       System.arraycopy(arrayOfConduitWrapper, 0, this.responseWrappers, 0, arrayOfConduitWrapper.length);
/* 1549 */       arrayOfConduitWrapper1 = this.responseWrappers;
/*      */     } 
/* 1551 */     arrayOfConduitWrapper1[this.responseWrapperCount++] = wrapper;
/* 1552 */     return this;
/*      */   }
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
/*      */   public BlockingHttpExchange startBlocking() {
/* 1566 */     BlockingHttpExchange old = this.blockingHttpExchange;
/* 1567 */     this.blockingHttpExchange = new DefaultBlockingHttpExchange(this);
/* 1568 */     return old;
/*      */   }
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
/*      */ 
/*      */ 
/*      */   
/*      */   public BlockingHttpExchange startBlocking(BlockingHttpExchange httpExchange) {
/* 1586 */     BlockingHttpExchange old = this.blockingHttpExchange;
/* 1587 */     this.blockingHttpExchange = httpExchange;
/* 1588 */     return old;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isBlocking() {
/* 1597 */     return (this.blockingHttpExchange != null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public InputStream getInputStream() {
/* 1605 */     if (this.blockingHttpExchange == null) {
/* 1606 */       throw UndertowMessages.MESSAGES.startBlockingHasNotBeenCalled();
/*      */     }
/* 1608 */     return this.blockingHttpExchange.getInputStream();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public OutputStream getOutputStream() {
/* 1616 */     if (this.blockingHttpExchange == null) {
/* 1617 */       throw UndertowMessages.MESSAGES.startBlockingHasNotBeenCalled();
/*      */     }
/* 1619 */     return this.blockingHttpExchange.getOutputStream();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   HttpServerExchange terminateResponse() {
/* 1627 */     int oldVal = this.state;
/* 1628 */     if (Bits.allAreSet(oldVal, 2048))
/*      */     {
/* 1630 */       return this;
/*      */     }
/* 1632 */     if (this.responseChannel != null) {
/* 1633 */       this.responseChannel.responseDone();
/*      */     }
/* 1635 */     this.state = oldVal | 0x800;
/* 1636 */     if (Bits.anyAreSet(oldVal, 4096)) {
/* 1637 */       invokeExchangeCompleteListeners();
/*      */     }
/* 1639 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public long getRequestStartTime() {
/* 1649 */     return this.requestStartTime;
/*      */   }
/*      */ 
/*      */   
/*      */   HttpServerExchange setRequestStartTime(long requestStartTime) {
/* 1654 */     this.requestStartTime = requestStartTime;
/* 1655 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public HttpServerExchange endExchange() {
/* 1667 */     final int state = this.state;
/* 1668 */     if (Bits.allAreSet(state, 6144)) {
/* 1669 */       if (this.blockingHttpExchange != null)
/*      */       {
/* 1671 */         IoUtils.safeClose(this.blockingHttpExchange);
/*      */       }
/* 1673 */       return this;
/*      */     } 
/* 1675 */     if (this.defaultResponseListeners != null) {
/* 1676 */       int i = this.defaultResponseListeners.length - 1;
/* 1677 */       while (i >= 0) {
/* 1678 */         DefaultResponseListener listener = this.defaultResponseListeners[i];
/* 1679 */         if (listener != null) {
/* 1680 */           this.defaultResponseListeners[i] = null;
/*      */           try {
/* 1682 */             if (listener.handleDefaultResponse(this)) {
/* 1683 */               return this;
/*      */             }
/* 1685 */           } catch (Throwable e) {
/* 1686 */             UndertowLogger.REQUEST_LOGGER.debug("Exception running default response listener", e);
/*      */           } 
/*      */         } 
/* 1689 */         i--;
/*      */       } 
/*      */     } 
/*      */     
/* 1693 */     if (Bits.anyAreClear(state, 4096)) {
/* 1694 */       this.connection.terminateRequestChannel(this);
/*      */     }
/*      */     
/* 1697 */     if (this.blockingHttpExchange != null) {
/*      */       
/*      */       try {
/* 1700 */         this.blockingHttpExchange.close();
/* 1701 */       } catch (IOException e) {
/* 1702 */         UndertowLogger.REQUEST_IO_LOGGER.ioException(e);
/* 1703 */         IoUtils.safeClose((Closeable)this.connection);
/* 1704 */       } catch (Throwable t) {
/* 1705 */         UndertowLogger.REQUEST_IO_LOGGER.handleUnexpectedFailure(t);
/* 1706 */         IoUtils.safeClose((Closeable)this.connection);
/*      */       } 
/*      */     }
/*      */ 
/*      */ 
/*      */     
/* 1712 */     if (Bits.anyAreClear(state, 4096)) {
/*      */ 
/*      */ 
/*      */       
/* 1716 */       if (this.requestChannel == null) {
/* 1717 */         getRequestChannel();
/*      */       }
/* 1719 */       int totalRead = 0; try {
/*      */         long read;
/*      */         do {
/* 1722 */           read = Channels.drain(this.requestChannel, Long.MAX_VALUE);
/* 1723 */           totalRead = (int)(totalRead + read);
/* 1724 */           if (read == 0L) {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */             
/* 1730 */             if (getStatusCode() != 417 || totalRead > 0) {
/* 1731 */               this.requestChannel.getReadSetter().set(ChannelListeners.drainListener(Long.MAX_VALUE, new ChannelListener<StreamSourceChannel>()
/*      */                     {
/*      */                       public void handleEvent(StreamSourceChannel channel)
/*      */                       {
/* 1735 */                         if (Bits.anyAreClear(state, 2048)) {
/* 1736 */                           HttpServerExchange.this.closeAndFlushResponse();
/*      */                         }
/*      */                       }
/*      */                     }new ChannelExceptionHandler<StreamSourceChannel>()
/*      */                     {
/*      */ 
/*      */                       
/*      */                       public void handleException(StreamSourceChannel channel, IOException e)
/*      */                       {
/* 1745 */                         HttpServerExchange.this.terminateRequest();
/* 1746 */                         HttpServerExchange.this.terminateResponse();
/* 1747 */                         UndertowLogger.REQUEST_LOGGER.debug("Exception draining request stream", e);
/* 1748 */                         IoUtils.safeClose((Closeable)HttpServerExchange.this.connection);
/*      */                       }
/*      */                     }));
/*      */               
/* 1752 */               this.requestChannel.resumeReads();
/* 1753 */               return this;
/*      */             } 
/*      */             break;
/*      */           } 
/* 1757 */         } while (read != -1L);
/*      */       
/*      */       }
/* 1760 */       catch (Throwable t) {
/* 1761 */         if (t instanceof IOException) {
/* 1762 */           UndertowLogger.REQUEST_IO_LOGGER.ioException((IOException)t);
/*      */         } else {
/* 1764 */           UndertowLogger.REQUEST_IO_LOGGER.handleUnexpectedFailure(t);
/*      */         } 
/* 1766 */         invokeExchangeCompleteListeners();
/* 1767 */         IoUtils.safeClose((Closeable)this.connection);
/* 1768 */         return this;
/*      */       } 
/*      */     } 
/*      */ 
/*      */     
/* 1773 */     if (Bits.anyAreClear(state, 2048)) {
/* 1774 */       closeAndFlushResponse();
/*      */     }
/* 1776 */     return this;
/*      */   }
/*      */   
/*      */   private void closeAndFlushResponse() {
/* 1780 */     if (!this.connection.isOpen()) {
/*      */ 
/*      */ 
/*      */       
/* 1784 */       terminateRequest();
/* 1785 */       terminateResponse();
/*      */       return;
/*      */     } 
/*      */     try {
/* 1789 */       if (isResponseChannelAvailable()) {
/* 1790 */         if (!getRequestMethod().equals(Methods.CONNECT) && (!getRequestMethod().equals(Methods.HEAD) || !getResponseHeaders().contains(Headers.CONTENT_LENGTH)) && Connectors.isEntityBodyAllowed(this))
/*      */         {
/* 1792 */           getResponseHeaders().put(Headers.CONTENT_LENGTH, "0");
/*      */         }
/* 1794 */         getResponseChannel();
/* 1795 */       } else if (Bits.anyAreClear(this.state, 2048) && !this.responseChannel.isOpen()) {
/*      */ 
/*      */ 
/*      */         
/* 1799 */         invokeExchangeCompleteListeners();
/* 1800 */         IoUtils.safeClose((Closeable)this.connection);
/*      */         return;
/*      */       } 
/* 1803 */       this.responseChannel.shutdownWrites();
/* 1804 */       if (!this.responseChannel.flush()) {
/* 1805 */         this.responseChannel.getWriteSetter().set(ChannelListeners.flushingChannelListener(new ChannelListener<StreamSinkChannel>()
/*      */               {
/*      */                 public void handleEvent(StreamSinkChannel channel)
/*      */                 {
/* 1809 */                   channel.suspendWrites();
/* 1810 */                   channel.getWriteSetter().set(null);
/*      */                   
/* 1812 */                   if (Bits.anyAreClear(HttpServerExchange.this.state, 2048)) {
/*      */                     
/* 1814 */                     HttpServerExchange.this.invokeExchangeCompleteListeners();
/* 1815 */                     UndertowLogger.ROOT_LOGGER.responseWasNotTerminated(HttpServerExchange.this.connection, HttpServerExchange.this);
/* 1816 */                     IoUtils.safeClose((Closeable)HttpServerExchange.this.connection);
/*      */                   } 
/*      */                 }
/*      */               }new ChannelExceptionHandler<Channel>()
/*      */               {
/*      */                 public void handleException(Channel channel, IOException exception)
/*      */                 {
/* 1823 */                   HttpServerExchange.this.invokeExchangeCompleteListeners();
/* 1824 */                   UndertowLogger.REQUEST_LOGGER.debug("Exception ending request", exception);
/* 1825 */                   IoUtils.safeClose((Closeable)HttpServerExchange.this.connection);
/*      */                 }
/*      */               }));
/*      */         
/* 1829 */         this.responseChannel.resumeWrites();
/*      */       
/*      */       }
/* 1832 */       else if (Bits.anyAreClear(this.state, 2048)) {
/*      */         
/* 1834 */         invokeExchangeCompleteListeners();
/* 1835 */         UndertowLogger.ROOT_LOGGER.responseWasNotTerminated(this.connection, this);
/* 1836 */         IoUtils.safeClose((Closeable)this.connection);
/*      */       }
/*      */     
/* 1839 */     } catch (Throwable t) {
/* 1840 */       if (t instanceof IOException) {
/* 1841 */         UndertowLogger.REQUEST_IO_LOGGER.ioException((IOException)t);
/*      */       } else {
/* 1843 */         UndertowLogger.REQUEST_IO_LOGGER.handleUnexpectedFailure(t);
/*      */       } 
/* 1845 */       invokeExchangeCompleteListeners();
/*      */       
/* 1847 */       IoUtils.safeClose((Closeable)this.connection);
/*      */     } 
/*      */   }
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   HttpServerExchange startResponse() throws IllegalStateException {
/* 1871 */     int oldVal = this.state;
/* 1872 */     if (Bits.allAreSet(oldVal, 1024)) {
/* 1873 */       throw UndertowMessages.MESSAGES.responseAlreadyStarted();
/*      */     }
/* 1875 */     this.state = oldVal | 0x400;
/*      */     
/* 1877 */     log.tracef("Starting to write response for %s", this);
/* 1878 */     return this;
/*      */   }
/*      */   
/*      */   public XnioIoThread getIoThread() {
/* 1882 */     return this.connection.getIoThread();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public long getMaxEntitySize() {
/* 1889 */     return this.maxEntitySize;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public HttpServerExchange setMaxEntitySize(long maxEntitySize) {
/* 1898 */     if (!isRequestChannelAvailable()) {
/* 1899 */       throw UndertowMessages.MESSAGES.requestChannelAlreadyProvided();
/*      */     }
/* 1901 */     this.maxEntitySize = maxEntitySize;
/* 1902 */     this.connection.maxEntitySizeUpdated(this);
/* 1903 */     return this;
/*      */   }
/*      */   
/*      */   public SecurityContext getSecurityContext() {
/* 1907 */     return this.securityContext;
/*      */   }
/*      */   
/*      */   public void setSecurityContext(SecurityContext securityContext) {
/* 1911 */     SecurityManager sm = System.getSecurityManager();
/* 1912 */     if (sm != null) {
/* 1913 */       sm.checkPermission(SET_SECURITY_CONTEXT);
/*      */     }
/* 1915 */     this.securityContext = securityContext;
/*      */   }
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
/*      */   public void addResponseCommitListener(final ResponseCommitListener listener) {
/* 1928 */     addResponseWrapper(new ConduitWrapper<StreamSinkConduit>()
/*      */         {
/*      */           public StreamSinkConduit wrap(ConduitFactory<StreamSinkConduit> factory, HttpServerExchange exchange) {
/* 1931 */             listener.beforeCommit(exchange);
/* 1932 */             return (StreamSinkConduit)factory.create();
/*      */           }
/*      */         });
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   boolean runResumeReadWrite() {
/* 1943 */     boolean ret = false;
/* 1944 */     if (Bits.anyAreSet(this.state, 524288)) {
/* 1945 */       this.responseChannel.runResume();
/* 1946 */       ret = true;
/*      */     } 
/* 1948 */     if (Bits.anyAreSet(this.state, 262144)) {
/* 1949 */       this.requestChannel.runResume();
/* 1950 */       ret = true;
/*      */     } 
/* 1952 */     return ret;
/*      */   }
/*      */   
/*      */   boolean isResumed() {
/* 1956 */     return Bits.anyAreSet(this.state, 786432);
/*      */   }
/*      */   
/*      */   private static class ExchangeCompleteNextListener implements ExchangeCompletionListener.NextListener {
/*      */     private final ExchangeCompletionListener[] list;
/*      */     private final HttpServerExchange exchange;
/*      */     private int i;
/*      */     
/*      */     ExchangeCompleteNextListener(ExchangeCompletionListener[] list, HttpServerExchange exchange, int i) {
/* 1965 */       this.list = list;
/* 1966 */       this.exchange = exchange;
/* 1967 */       this.i = i;
/*      */     }
/*      */ 
/*      */     
/*      */     public void proceed() {
/* 1972 */       if (--this.i >= 0) {
/* 1973 */         ExchangeCompletionListener next = this.list[this.i];
/* 1974 */         next.exchangeEvent(this.exchange, this);
/* 1975 */       } else if (this.i == -1) {
/* 1976 */         this.exchange.connection.exchangeComplete(this.exchange);
/*      */       } 
/*      */     }
/*      */   }
/*      */   
/*      */   private static class DefaultBlockingHttpExchange
/*      */     implements BlockingHttpExchange {
/*      */     private InputStream inputStream;
/*      */     private UndertowOutputStream outputStream;
/*      */     private Sender sender;
/*      */     private final HttpServerExchange exchange;
/*      */     
/*      */     DefaultBlockingHttpExchange(HttpServerExchange exchange) {
/* 1989 */       this.exchange = exchange;
/*      */     }
/*      */     
/*      */     public InputStream getInputStream() {
/* 1993 */       if (this.inputStream == null) {
/* 1994 */         this.inputStream = (InputStream)new UndertowInputStream(this.exchange);
/*      */       }
/* 1996 */       return this.inputStream;
/*      */     }
/*      */     
/*      */     public UndertowOutputStream getOutputStream() {
/* 2000 */       if (this.outputStream == null) {
/* 2001 */         this.outputStream = new UndertowOutputStream(this.exchange);
/*      */       }
/* 2003 */       return this.outputStream;
/*      */     }
/*      */ 
/*      */     
/*      */     public Sender getSender() {
/* 2008 */       if (this.sender == null) {
/* 2009 */         this.sender = (Sender)new BlockingSenderImpl(this.exchange, (OutputStream)getOutputStream());
/*      */       }
/* 2011 */       return this.sender;
/*      */     }
/*      */ 
/*      */     
/*      */     public void close() throws IOException {
/*      */       try {
/* 2017 */         getInputStream().close();
/*      */       } finally {
/* 2019 */         getOutputStream().close();
/*      */       } 
/*      */     }
/*      */ 
/*      */     
/*      */     public Receiver getReceiver() {
/* 2025 */       return (Receiver)new BlockingReceiverImpl(this.exchange, getInputStream());
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private class WriteDispatchChannel
/*      */     extends DetachableStreamSinkChannel
/*      */     implements StreamSinkChannel
/*      */   {
/*      */     private boolean wakeup;
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     WriteDispatchChannel(ConduitStreamSinkChannel delegate) {
/* 2043 */       super((StreamSinkChannel)delegate);
/*      */     }
/*      */ 
/*      */     
/*      */     protected boolean isFinished() {
/* 2048 */       return Bits.allAreSet(HttpServerExchange.this.state, 2048);
/*      */     }
/*      */ 
/*      */     
/*      */     public void resumeWrites() {
/* 2053 */       if (HttpServerExchange.this.isInCall()) {
/* 2054 */         HttpServerExchange.this.state = HttpServerExchange.this.state | 0x80000;
/* 2055 */         if (Bits.anyAreSet(HttpServerExchange.this.state, 32768)) {
/* 2056 */           throw UndertowMessages.MESSAGES.resumedAndDispatched();
/*      */         }
/* 2058 */       } else if (!isFinished()) {
/* 2059 */         this.delegate.resumeWrites();
/*      */       } 
/*      */     }
/*      */ 
/*      */     
/*      */     public void suspendWrites() {
/* 2065 */       HttpServerExchange.this.state = HttpServerExchange.this.state & 0xFFF7FFFF;
/* 2066 */       super.suspendWrites();
/*      */     }
/*      */ 
/*      */     
/*      */     public void wakeupWrites() {
/* 2071 */       if (isFinished()) {
/*      */         return;
/*      */       }
/* 2074 */       if (HttpServerExchange.this.isInCall()) {
/* 2075 */         this.wakeup = true;
/* 2076 */         HttpServerExchange.this.state = HttpServerExchange.this.state | 0x80000;
/* 2077 */         if (Bits.anyAreSet(HttpServerExchange.this.state, 32768)) {
/* 2078 */           throw UndertowMessages.MESSAGES.resumedAndDispatched();
/*      */         }
/*      */       } else {
/* 2081 */         this.delegate.wakeupWrites();
/*      */       } 
/*      */     }
/*      */ 
/*      */     
/*      */     public boolean isWriteResumed() {
/* 2087 */       return (Bits.anyAreSet(HttpServerExchange.this.state, 524288) || super.isWriteResumed());
/*      */     }
/*      */     
/*      */     public void runResume() {
/* 2091 */       if (isWriteResumed()) {
/* 2092 */         if (isFinished()) {
/* 2093 */           invokeListener();
/*      */         }
/* 2095 */         else if (this.wakeup) {
/* 2096 */           this.wakeup = false;
/* 2097 */           HttpServerExchange.this.state = HttpServerExchange.this.state & 0xFFF7FFFF;
/* 2098 */           this.delegate.wakeupWrites();
/*      */         } else {
/* 2100 */           HttpServerExchange.this.state = HttpServerExchange.this.state & 0xFFF7FFFF;
/* 2101 */           this.delegate.resumeWrites();
/*      */         }
/*      */       
/* 2104 */       } else if (this.wakeup) {
/* 2105 */         this.wakeup = false;
/* 2106 */         invokeListener();
/*      */       } 
/*      */     }
/*      */     
/*      */     private void invokeListener() {
/* 2111 */       if (this.writeSetter != null) {
/* 2112 */         getIoThread().execute(new Runnable()
/*      */             {
/*      */               public void run() {
/* 2115 */                 ChannelListeners.invokeChannelListener((Channel)HttpServerExchange.WriteDispatchChannel.this, HttpServerExchange.WriteDispatchChannel.this.writeSetter.get());
/*      */               }
/*      */             });
/*      */       }
/*      */     }
/*      */ 
/*      */     
/*      */     public void awaitWritable() throws IOException {
/* 2123 */       if (Thread.currentThread() == getIoThread()) {
/* 2124 */         throw UndertowMessages.MESSAGES.awaitCalledFromIoThread();
/*      */       }
/* 2126 */       super.awaitWritable();
/*      */     }
/*      */ 
/*      */     
/*      */     public void awaitWritable(long time, TimeUnit timeUnit) throws IOException {
/* 2131 */       if (Thread.currentThread() == getIoThread()) {
/* 2132 */         throw UndertowMessages.MESSAGES.awaitCalledFromIoThread();
/*      */       }
/* 2134 */       super.awaitWritable(time, timeUnit);
/*      */     }
/*      */ 
/*      */     
/*      */     public long transferFrom(FileChannel src, long position, long count) throws IOException {
/* 2139 */       long l = super.transferFrom(src, position, count);
/* 2140 */       if (l > 0L) {
/* 2141 */         HttpServerExchange.this.responseBytesSent = HttpServerExchange.this.responseBytesSent + l;
/*      */       }
/* 2143 */       return l;
/*      */     }
/*      */ 
/*      */     
/*      */     public long transferFrom(StreamSourceChannel source, long count, ByteBuffer throughBuffer) throws IOException {
/* 2148 */       long l = super.transferFrom(source, count, throughBuffer);
/* 2149 */       if (l > 0L) {
/* 2150 */         HttpServerExchange.this.responseBytesSent = HttpServerExchange.this.responseBytesSent + l;
/*      */       }
/* 2152 */       return l;
/*      */     }
/*      */ 
/*      */     
/*      */     public long write(ByteBuffer[] srcs, int offset, int length) throws IOException {
/* 2157 */       long l = super.write(srcs, offset, length);
/* 2158 */       HttpServerExchange.this.responseBytesSent = HttpServerExchange.this.responseBytesSent + l;
/* 2159 */       return l;
/*      */     }
/*      */ 
/*      */     
/*      */     public long write(ByteBuffer[] srcs) throws IOException {
/* 2164 */       long l = super.write(srcs);
/* 2165 */       HttpServerExchange.this.responseBytesSent = HttpServerExchange.this.responseBytesSent + l;
/* 2166 */       return l;
/*      */     }
/*      */ 
/*      */     
/*      */     public int writeFinal(ByteBuffer src) throws IOException {
/* 2171 */       int l = super.writeFinal(src);
/* 2172 */       HttpServerExchange.this.responseBytesSent = HttpServerExchange.this.responseBytesSent + l;
/* 2173 */       return l;
/*      */     }
/*      */ 
/*      */     
/*      */     public long writeFinal(ByteBuffer[] srcs, int offset, int length) throws IOException {
/* 2178 */       long l = super.writeFinal(srcs, offset, length);
/* 2179 */       HttpServerExchange.this.responseBytesSent = HttpServerExchange.this.responseBytesSent + l;
/* 2180 */       return l;
/*      */     }
/*      */ 
/*      */     
/*      */     public long writeFinal(ByteBuffer[] srcs) throws IOException {
/* 2185 */       long l = super.writeFinal(srcs);
/* 2186 */       HttpServerExchange.this.responseBytesSent = HttpServerExchange.this.responseBytesSent + l;
/* 2187 */       return l;
/*      */     }
/*      */ 
/*      */     
/*      */     public int write(ByteBuffer src) throws IOException {
/* 2192 */       int l = super.write(src);
/* 2193 */       HttpServerExchange.this.responseBytesSent = HttpServerExchange.this.responseBytesSent + l;
/* 2194 */       return l;
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private final class ReadDispatchChannel
/*      */     extends DetachableStreamSourceChannel
/*      */     implements StreamSourceChannel
/*      */   {
/*      */     private boolean wakeup = true;
/*      */ 
/*      */ 
/*      */     
/*      */     private boolean readsResumed = false;
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     ReadDispatchChannel(ConduitStreamSourceChannel delegate) {
/* 2215 */       super((StreamSourceChannel)delegate);
/*      */     }
/*      */ 
/*      */     
/*      */     protected boolean isFinished() {
/* 2220 */       return Bits.allAreSet(HttpServerExchange.this.state, 4096);
/*      */     }
/*      */ 
/*      */     
/*      */     public void resumeReads() {
/* 2225 */       this.readsResumed = true;
/* 2226 */       if (HttpServerExchange.this.isInCall()) {
/* 2227 */         HttpServerExchange.this.state = HttpServerExchange.this.state | 0x40000;
/* 2228 */         if (Bits.anyAreSet(HttpServerExchange.this.state, 32768)) {
/* 2229 */           throw UndertowMessages.MESSAGES.resumedAndDispatched();
/*      */         }
/* 2231 */       } else if (!isFinished()) {
/* 2232 */         this.delegate.resumeReads();
/*      */       } 
/*      */     }
/*      */ 
/*      */     
/*      */     public void wakeupReads() {
/* 2238 */       if (HttpServerExchange.this.isInCall()) {
/* 2239 */         this.wakeup = true;
/* 2240 */         HttpServerExchange.this.state = HttpServerExchange.this.state | 0x40000;
/* 2241 */         if (Bits.anyAreSet(HttpServerExchange.this.state, 32768)) {
/* 2242 */           throw UndertowMessages.MESSAGES.resumedAndDispatched();
/*      */         }
/*      */       }
/* 2245 */       else if (isFinished()) {
/* 2246 */         invokeListener();
/*      */       } else {
/* 2248 */         this.delegate.wakeupReads();
/*      */       } 
/*      */     }
/*      */ 
/*      */     
/*      */     private void invokeListener() {
/* 2254 */       if (this.readSetter != null) {
/* 2255 */         getIoThread().execute(new Runnable()
/*      */             {
/*      */               public void run() {
/* 2258 */                 ChannelListeners.invokeChannelListener((Channel)HttpServerExchange.ReadDispatchChannel.this, HttpServerExchange.ReadDispatchChannel.this.readSetter.get());
/*      */               }
/*      */             });
/*      */       }
/*      */     }
/*      */     
/*      */     public void requestDone() {
/* 2265 */       if (this.delegate instanceof ConduitStreamSourceChannel) {
/* 2266 */         ((ConduitStreamSourceChannel)this.delegate).setReadListener(null);
/* 2267 */         ((ConduitStreamSourceChannel)this.delegate).setCloseListener(null);
/*      */       } else {
/* 2269 */         this.delegate.getReadSetter().set(null);
/* 2270 */         this.delegate.getCloseSetter().set(null);
/*      */       } 
/*      */     }
/*      */ 
/*      */     
/*      */     public long transferTo(long position, long count, FileChannel target) throws IOException {
/* 2276 */       PooledByteBuffer[] buffered = (PooledByteBuffer[])HttpServerExchange.this.getAttachment(HttpServerExchange.BUFFERED_REQUEST_DATA);
/* 2277 */       if (buffered == null) {
/* 2278 */         return super.transferTo(position, count, target);
/*      */       }
/* 2280 */       return target.transferFrom((ReadableByteChannel)this, position, count);
/*      */     }
/*      */ 
/*      */     
/*      */     public void awaitReadable() throws IOException {
/* 2285 */       if (Thread.currentThread() == getIoThread()) {
/* 2286 */         throw UndertowMessages.MESSAGES.awaitCalledFromIoThread();
/*      */       }
/* 2288 */       PooledByteBuffer[] buffered = (PooledByteBuffer[])HttpServerExchange.this.getAttachment(HttpServerExchange.BUFFERED_REQUEST_DATA);
/* 2289 */       if (buffered == null) {
/* 2290 */         super.awaitReadable();
/*      */       }
/*      */     }
/*      */ 
/*      */     
/*      */     public void suspendReads() {
/* 2296 */       this.readsResumed = false;
/* 2297 */       HttpServerExchange.this.state = HttpServerExchange.this.state & 0xFFFBFFFF;
/* 2298 */       super.suspendReads();
/*      */     }
/*      */ 
/*      */     
/*      */     public long transferTo(long count, ByteBuffer throughBuffer, StreamSinkChannel target) throws IOException {
/* 2303 */       PooledByteBuffer[] buffered = (PooledByteBuffer[])HttpServerExchange.this.getAttachment(HttpServerExchange.BUFFERED_REQUEST_DATA);
/* 2304 */       if (buffered == null) {
/* 2305 */         return super.transferTo(count, throughBuffer, target);
/*      */       }
/*      */       
/* 2308 */       throughBuffer.position(0);
/* 2309 */       throughBuffer.limit(0);
/* 2310 */       long copied = 0L;
/* 2311 */       for (int i = 0; i < buffered.length; i++) {
/* 2312 */         PooledByteBuffer pooled = buffered[i];
/* 2313 */         if (pooled != null) {
/* 2314 */           ByteBuffer buf = pooled.getBuffer();
/* 2315 */           if (buf.hasRemaining()) {
/* 2316 */             int res = target.write(buf);
/*      */             
/* 2318 */             if (!buf.hasRemaining()) {
/* 2319 */               pooled.close();
/* 2320 */               buffered[i] = null;
/*      */             } 
/* 2322 */             if (res == 0) {
/* 2323 */               return copied;
/*      */             }
/* 2325 */             copied += res;
/*      */           } else {
/*      */             
/* 2328 */             pooled.close();
/* 2329 */             buffered[i] = null;
/*      */           } 
/*      */         } 
/*      */       } 
/* 2333 */       HttpServerExchange.this.removeAttachment(HttpServerExchange.BUFFERED_REQUEST_DATA);
/* 2334 */       if (copied == 0L) {
/* 2335 */         return super.transferTo(count, throughBuffer, target);
/*      */       }
/* 2337 */       return copied;
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public void awaitReadable(long time, TimeUnit timeUnit) throws IOException {
/* 2343 */       if (Thread.currentThread() == getIoThread()) {
/* 2344 */         throw UndertowMessages.MESSAGES.awaitCalledFromIoThread();
/*      */       }
/* 2346 */       PooledByteBuffer[] buffered = (PooledByteBuffer[])HttpServerExchange.this.getAttachment(HttpServerExchange.BUFFERED_REQUEST_DATA);
/* 2347 */       if (buffered == null) {
/* 2348 */         super.awaitReadable(time, timeUnit);
/*      */       }
/*      */     }
/*      */ 
/*      */     
/*      */     public long read(ByteBuffer[] dsts, int offset, int length) throws IOException {
/* 2354 */       PooledByteBuffer[] buffered = (PooledByteBuffer[])HttpServerExchange.this.getAttachment(HttpServerExchange.BUFFERED_REQUEST_DATA);
/* 2355 */       if (buffered == null) {
/* 2356 */         return super.read(dsts, offset, length);
/*      */       }
/* 2358 */       long copied = 0L;
/* 2359 */       for (int i = 0; i < buffered.length; i++) {
/* 2360 */         PooledByteBuffer pooled = buffered[i];
/* 2361 */         if (pooled != null) {
/* 2362 */           ByteBuffer buf = pooled.getBuffer();
/* 2363 */           if (buf.hasRemaining()) {
/* 2364 */             copied += Buffers.copy(dsts, offset, length, buf);
/* 2365 */             if (!buf.hasRemaining()) {
/* 2366 */               pooled.close();
/* 2367 */               buffered[i] = null;
/*      */             } 
/* 2369 */             if (!Buffers.hasRemaining((Buffer[])dsts, offset, length)) {
/* 2370 */               return copied;
/*      */             }
/*      */           } else {
/* 2373 */             pooled.close();
/* 2374 */             buffered[i] = null;
/*      */           } 
/*      */         } 
/*      */       } 
/* 2378 */       HttpServerExchange.this.removeAttachment(HttpServerExchange.BUFFERED_REQUEST_DATA);
/* 2379 */       if (copied == 0L) {
/* 2380 */         return super.read(dsts, offset, length);
/*      */       }
/* 2382 */       return copied;
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public long read(ByteBuffer[] dsts) throws IOException {
/* 2388 */       return read(dsts, 0, dsts.length);
/*      */     }
/*      */ 
/*      */     
/*      */     public boolean isOpen() {
/* 2393 */       PooledByteBuffer[] buffered = (PooledByteBuffer[])HttpServerExchange.this.getAttachment(HttpServerExchange.BUFFERED_REQUEST_DATA);
/* 2394 */       if (buffered != null) {
/* 2395 */         return true;
/*      */       }
/* 2397 */       return super.isOpen();
/*      */     }
/*      */ 
/*      */     
/*      */     public void close() throws IOException {
/* 2402 */       PooledByteBuffer[] buffered = (PooledByteBuffer[])HttpServerExchange.this.getAttachment(HttpServerExchange.BUFFERED_REQUEST_DATA);
/* 2403 */       if (buffered != null) {
/* 2404 */         for (PooledByteBuffer pooled : buffered) {
/* 2405 */           if (pooled != null) {
/* 2406 */             pooled.close();
/*      */           }
/*      */         } 
/*      */       }
/* 2410 */       HttpServerExchange.this.removeAttachment(HttpServerExchange.BUFFERED_REQUEST_DATA);
/* 2411 */       super.close();
/*      */     }
/*      */ 
/*      */     
/*      */     public boolean isReadResumed() {
/* 2416 */       PooledByteBuffer[] buffered = (PooledByteBuffer[])HttpServerExchange.this.getAttachment(HttpServerExchange.BUFFERED_REQUEST_DATA);
/* 2417 */       if (buffered != null) {
/* 2418 */         return this.readsResumed;
/*      */       }
/* 2420 */       if (isFinished()) {
/* 2421 */         return false;
/*      */       }
/* 2423 */       return (Bits.anyAreSet(HttpServerExchange.this.state, 262144) || super.isReadResumed());
/*      */     }
/*      */ 
/*      */     
/*      */     public int read(ByteBuffer dst) throws IOException {
/* 2428 */       PooledByteBuffer[] buffered = (PooledByteBuffer[])HttpServerExchange.this.getAttachment(HttpServerExchange.BUFFERED_REQUEST_DATA);
/* 2429 */       if (buffered == null) {
/* 2430 */         return super.read(dst);
/*      */       }
/* 2432 */       int copied = 0;
/* 2433 */       for (int i = 0; i < buffered.length; i++) {
/* 2434 */         PooledByteBuffer pooled = buffered[i];
/* 2435 */         if (pooled != null) {
/* 2436 */           ByteBuffer buf = pooled.getBuffer();
/* 2437 */           if (buf.hasRemaining()) {
/* 2438 */             copied += Buffers.copy(dst, buf);
/* 2439 */             if (!buf.hasRemaining()) {
/* 2440 */               pooled.close();
/* 2441 */               buffered[i] = null;
/*      */             } 
/* 2443 */             if (!dst.hasRemaining()) {
/* 2444 */               return copied;
/*      */             }
/*      */           } else {
/* 2447 */             pooled.close();
/* 2448 */             buffered[i] = null;
/*      */           } 
/*      */         } 
/*      */       } 
/* 2452 */       HttpServerExchange.this.removeAttachment(HttpServerExchange.BUFFERED_REQUEST_DATA);
/* 2453 */       if (copied == 0) {
/* 2454 */         return super.read(dst);
/*      */       }
/* 2456 */       return copied;
/*      */     }
/*      */ 
/*      */     
/*      */     public void runResume() {
/* 2461 */       if (isReadResumed()) {
/* 2462 */         if (isFinished()) {
/* 2463 */           invokeListener();
/*      */         }
/* 2465 */         else if (this.wakeup) {
/* 2466 */           this.wakeup = false;
/* 2467 */           HttpServerExchange.this.state = HttpServerExchange.this.state & 0xFFFBFFFF;
/* 2468 */           this.delegate.wakeupReads();
/*      */         } else {
/* 2470 */           HttpServerExchange.this.state = HttpServerExchange.this.state & 0xFFFBFFFF;
/* 2471 */           this.delegate.resumeReads();
/*      */         }
/*      */       
/* 2474 */       } else if (this.wakeup) {
/* 2475 */         this.wakeup = false;
/* 2476 */         invokeListener();
/*      */       } 
/*      */     }
/*      */   }
/*      */   
/*      */   public static class WrapperStreamSinkConduitFactory
/*      */     implements ConduitFactory<StreamSinkConduit>
/*      */   {
/*      */     private final HttpServerExchange exchange;
/*      */     private final ConduitWrapper<StreamSinkConduit>[] wrappers;
/*      */     private int position;
/*      */     private final StreamSinkConduit first;
/*      */     
/*      */     public WrapperStreamSinkConduitFactory(ConduitWrapper<StreamSinkConduit>[] wrappers, int wrapperCount, HttpServerExchange exchange, StreamSinkConduit first) {
/* 2490 */       this.wrappers = wrappers;
/* 2491 */       this.exchange = exchange;
/* 2492 */       this.first = first;
/* 2493 */       this.position = wrapperCount - 1;
/*      */     }
/*      */ 
/*      */     
/*      */     public StreamSinkConduit create() {
/* 2498 */       if (this.position == -1) {
/* 2499 */         return this.exchange.getConnection().getSinkConduit(this.exchange, this.first);
/*      */       }
/* 2501 */       return this.wrappers[this.position--].wrap(this, this.exchange);
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   public static class WrapperConduitFactory<T extends Conduit>
/*      */     implements ConduitFactory<T>
/*      */   {
/*      */     private final HttpServerExchange exchange;
/*      */     private final ConduitWrapper<T>[] wrappers;
/*      */     private int position;
/*      */     private T first;
/*      */     
/*      */     public WrapperConduitFactory(ConduitWrapper<T>[] wrappers, int wrapperCount, T first, HttpServerExchange exchange) {
/* 2515 */       this.wrappers = wrappers;
/* 2516 */       this.exchange = exchange;
/* 2517 */       this.position = wrapperCount - 1;
/* 2518 */       this.first = first;
/*      */     }
/*      */ 
/*      */     
/*      */     public T create() {
/* 2523 */       if (this.position == -1) {
/* 2524 */         return this.first;
/*      */       }
/* 2526 */       return this.wrappers[this.position--].wrap(this, this.exchange);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public String toString() {
/* 2533 */     return "HttpServerExchange{ " + getRequestMethod().toString() + " " + getRequestURI() + '}';
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\HttpServerExchange.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */