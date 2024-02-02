package io.undertow.server.protocol.http2;

import io.undertow.UndertowLogger;
import io.undertow.UndertowMessages;
import io.undertow.UndertowOptions;
import io.undertow.connector.ByteBufferPool;
import io.undertow.protocols.http2.Http2Channel;
import io.undertow.protocols.http2.Http2DataStreamSinkChannel;
import io.undertow.protocols.http2.Http2HeadersStreamSinkChannel;
import io.undertow.protocols.http2.Http2StreamSourceChannel;
import io.undertow.server.ConduitWrapper;
import io.undertow.server.Connectors;
import io.undertow.server.ExchangeCompletionListener;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.HttpUpgradeListener;
import io.undertow.server.SSLSessionInfo;
import io.undertow.server.ServerConnection;
import io.undertow.server.XnioBufferPoolAdaptor;
import io.undertow.server.protocol.http.HttpContinue;
import io.undertow.util.AttachmentKey;
import io.undertow.util.AttachmentList;
import io.undertow.util.ConduitFactory;
import io.undertow.util.DateUtils;
import io.undertow.util.HeaderMap;
import io.undertow.util.Headers;
import io.undertow.util.HttpString;
import io.undertow.util.ParameterLimitException;
import io.undertow.util.Protocols;
import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.List;
import javax.net.ssl.SSLSession;
import org.xnio.ChannelListener;
import org.xnio.Option;
import org.xnio.OptionMap;
import org.xnio.Pool;
import org.xnio.StreamConnection;
import org.xnio.XnioIoThread;
import org.xnio.XnioWorker;
import org.xnio.channels.Configurable;
import org.xnio.channels.ConnectedChannel;
import org.xnio.conduits.ConduitStreamSinkChannel;
import org.xnio.conduits.ConduitStreamSourceChannel;
import org.xnio.conduits.EmptyStreamSourceConduit;
import org.xnio.conduits.StreamSinkChannelWrappingConduit;
import org.xnio.conduits.StreamSinkConduit;
import org.xnio.conduits.StreamSourceChannelWrappingConduit;
import org.xnio.conduits.StreamSourceConduit;
import org.xnio.conduits.WriteReadyHandler;

public class Http2ServerConnection extends ServerConnection {
   private static final HttpString STATUS = new HttpString(":status");
   private final Http2Channel channel;
   private final Http2StreamSourceChannel requestChannel;
   private final Http2DataStreamSinkChannel responseChannel;
   private final ConduitStreamSinkChannel conduitStreamSinkChannel;
   private final ConduitStreamSourceChannel conduitStreamSourceChannel;
   private final StreamSinkConduit originalSinkConduit;
   private final StreamSourceConduit originalSourceConduit;
   private final OptionMap undertowOptions;
   private final int bufferSize;
   private SSLSessionInfo sessionInfo;
   private final HttpHandler rootHandler;
   private HttpServerExchange exchange;
   private boolean continueSent = false;
   private XnioBufferPoolAdaptor poolAdaptor;

   public Http2ServerConnection(Http2Channel channel, Http2StreamSourceChannel requestChannel, OptionMap undertowOptions, int bufferSize, HttpHandler rootHandler) {
      this.channel = channel;
      this.requestChannel = requestChannel;
      this.undertowOptions = undertowOptions;
      this.bufferSize = bufferSize;
      this.rootHandler = rootHandler;
      this.responseChannel = requestChannel.getResponseChannel();
      this.originalSinkConduit = new StreamSinkChannelWrappingConduit(this.responseChannel);
      this.originalSourceConduit = new StreamSourceChannelWrappingConduit(requestChannel);
      this.conduitStreamSinkChannel = new ConduitStreamSinkChannel(this.responseChannel, this.originalSinkConduit);
      this.conduitStreamSourceChannel = new ConduitStreamSourceChannel(channel, this.originalSourceConduit);
   }

   void setExchange(HttpServerExchange exchange) {
      this.exchange = exchange;
   }

   public Http2ServerConnection(Http2Channel channel, Http2DataStreamSinkChannel sinkChannel, OptionMap undertowOptions, int bufferSize, HttpHandler rootHandler) {
      this.channel = channel;
      this.rootHandler = rootHandler;
      this.requestChannel = null;
      this.undertowOptions = undertowOptions;
      this.bufferSize = bufferSize;
      this.responseChannel = sinkChannel;
      this.originalSinkConduit = new StreamSinkChannelWrappingConduit(this.responseChannel);
      this.originalSourceConduit = new StreamSourceChannelWrappingConduit(this.requestChannel);
      this.conduitStreamSinkChannel = new ConduitStreamSinkChannel(this.responseChannel, this.originalSinkConduit);
      this.conduitStreamSourceChannel = new ConduitStreamSourceChannel(Configurable.EMPTY, new EmptyStreamSourceConduit(this.getIoThread()));
   }

   public Pool<ByteBuffer> getBufferPool() {
      if (this.poolAdaptor == null) {
         this.poolAdaptor = new XnioBufferPoolAdaptor(this.getByteBufferPool());
      }

      return this.poolAdaptor;
   }

   public SSLSession getSslSession() {
      return this.channel.getSslSession();
   }

   public ByteBufferPool getByteBufferPool() {
      return this.channel.getBufferPool();
   }

   public XnioWorker getWorker() {
      return this.channel.getWorker();
   }

   public XnioIoThread getIoThread() {
      return this.channel.getIoThread();
   }

   public HttpServerExchange sendOutOfBandResponse(HttpServerExchange exchange) {
      if (exchange != null && HttpContinue.requiresContinueResponse(exchange)) {
         final HttpServerExchange newExchange = new HttpServerExchange(this);
         Iterator var3 = exchange.getRequestHeaders().getHeaderNames().iterator();

         while(var3.hasNext()) {
            HttpString header = (HttpString)var3.next();
            newExchange.getRequestHeaders().putAll(header, exchange.getRequestHeaders().get(header));
         }

         newExchange.setProtocol(exchange.getProtocol());
         newExchange.setRequestMethod(exchange.getRequestMethod());
         exchange.setRequestURI(exchange.getRequestURI(), exchange.isHostIncludedInRequestURI());
         exchange.setRequestPath(exchange.getRequestPath());
         exchange.setRelativePath(exchange.getRelativePath());
         newExchange.setPersistent(true);
         Connectors.terminateRequest(newExchange);
         newExchange.addResponseWrapper(new ConduitWrapper<StreamSinkConduit>() {
            public StreamSinkConduit wrap(ConduitFactory<StreamSinkConduit> factory, HttpServerExchange exchange) {
               HeaderMap headers = newExchange.getResponseHeaders();
               DateUtils.addDateHeaderIfRequired(exchange);
               headers.add(Http2ServerConnection.STATUS, (long)exchange.getStatusCode());
               Connectors.flattenCookies(exchange);
               Http2HeadersStreamSinkChannel sink = new Http2HeadersStreamSinkChannel(Http2ServerConnection.this.channel, Http2ServerConnection.this.requestChannel.getStreamId(), headers);
               StreamSinkChannelWrappingConduit ret = new StreamSinkChannelWrappingConduit(sink);
               ret.setWriteReadyHandler(new WriteReadyHandler.ChannelListenerHandler(Connectors.getConduitSinkChannel(exchange)));
               return ret;
            }
         });
         this.continueSent = true;
         return newExchange;
      } else {
         throw UndertowMessages.MESSAGES.outOfBandResponseOnlyAllowedFor100Continue();
      }
   }

   public boolean isContinueResponseSupported() {
      return true;
   }

   public void terminateRequestChannel(HttpServerExchange exchange) {
      if (HttpContinue.requiresContinueResponse(exchange.getRequestHeaders()) && !this.continueSent && this.requestChannel != null) {
         this.requestChannel.setIgnoreForceClose(true);
         this.requestChannel.close();
         exchange.addExchangeCompleteListener(new ExchangeCompletionListener() {
            public void exchangeEvent(HttpServerExchange exchange, ExchangeCompletionListener.NextListener nextListener) {
               try {
                  Http2ServerConnection.this.channel.sendRstStream(Http2ServerConnection.this.responseChannel.getStreamId(), 8);
               } finally {
                  nextListener.proceed();
               }

            }
         });
      }

   }

   public boolean isOpen() {
      return this.channel.isOpen();
   }

   public boolean supportsOption(Option<?> option) {
      return false;
   }

   public <T> T getOption(Option<T> option) throws IOException {
      return null;
   }

   public <T> T setOption(Option<T> option, T value) throws IllegalArgumentException, IOException {
      return null;
   }

   public void close() throws IOException {
      this.channel.sendRstStream(this.requestChannel.getStreamId(), 8);
   }

   public SocketAddress getPeerAddress() {
      return this.channel.getPeerAddress();
   }

   public <A extends SocketAddress> A getPeerAddress(Class<A> type) {
      return this.channel.getPeerAddress(type);
   }

   public ChannelListener.Setter<? extends ConnectedChannel> getCloseSetter() {
      return this.channel.getCloseSetter();
   }

   public SocketAddress getLocalAddress() {
      return this.channel.getLocalAddress();
   }

   public <A extends SocketAddress> A getLocalAddress(Class<A> type) {
      return this.channel.getLocalAddress(type);
   }

   public OptionMap getUndertowOptions() {
      return this.undertowOptions;
   }

   public int getBufferSize() {
      return this.bufferSize;
   }

   public SSLSessionInfo getSslSessionInfo() {
      return this.sessionInfo;
   }

   public void setSslSessionInfo(SSLSessionInfo sessionInfo) {
      this.sessionInfo = sessionInfo;
   }

   public void addCloseListener(final ServerConnection.CloseListener listener) {
      this.channel.addCloseTask(new ChannelListener<Http2Channel>() {
         public void handleEvent(Http2Channel channel) {
            listener.closed(Http2ServerConnection.this);
         }
      });
   }

   protected StreamConnection upgradeChannel() {
      throw UndertowMessages.MESSAGES.upgradeNotSupported();
   }

   protected ConduitStreamSinkChannel getSinkChannel() {
      return this.conduitStreamSinkChannel;
   }

   protected ConduitStreamSourceChannel getSourceChannel() {
      return this.conduitStreamSourceChannel;
   }

   protected StreamSinkConduit getSinkConduit(HttpServerExchange exchange, StreamSinkConduit conduit) {
      HeaderMap headers = this.responseChannel.getHeaders();
      DateUtils.addDateHeaderIfRequired(exchange);
      headers.add(STATUS, (long)exchange.getStatusCode());
      Connectors.flattenCookies(exchange);
      if (!Connectors.isEntityBodyAllowed(exchange)) {
         exchange.getResponseHeaders().remove(Headers.CONTENT_LENGTH);
         exchange.getResponseHeaders().remove(Headers.TRANSFER_ENCODING);
      }

      return this.originalSinkConduit;
   }

   protected boolean isUpgradeSupported() {
      return false;
   }

   protected boolean isConnectSupported() {
      return false;
   }

   protected void exchangeComplete(HttpServerExchange exchange) {
   }

   protected void setUpgradeListener(HttpUpgradeListener upgradeListener) {
      throw UndertowMessages.MESSAGES.upgradeNotSupported();
   }

   protected void setConnectListener(HttpUpgradeListener connectListener) {
   }

   protected void maxEntitySizeUpdated(HttpServerExchange exchange) {
      if (this.requestChannel != null) {
         this.requestChannel.setMaxStreamSize(exchange.getMaxEntitySize());
      }

   }

   public <T> void addToAttachmentList(AttachmentKey<AttachmentList<T>> key, T value) {
      this.channel.addToAttachmentList(key, value);
   }

   public <T> T removeAttachment(AttachmentKey<T> key) {
      return this.channel.removeAttachment(key);
   }

   public <T> T putAttachment(AttachmentKey<T> key, T value) {
      return this.channel.putAttachment(key, value);
   }

   public <T> List<T> getAttachmentList(AttachmentKey<? extends List<T>> key) {
      return this.channel.getAttachmentList(key);
   }

   public <T> T getAttachment(AttachmentKey<T> key) {
      return this.channel.getAttachment(key);
   }

   public boolean isPushSupported() {
      return this.channel.isPushEnabled() && !this.exchange.getRequestHeaders().contains(Headers.X_DISABLE_PUSH) && this.responseChannel.getStreamId() % 2 != 0;
   }

   public boolean isRequestTrailerFieldsSupported() {
      return true;
   }

   public boolean pushResource(String path, HttpString method, HeaderMap requestHeaders) {
      return this.pushResource(path, method, requestHeaders, this.rootHandler);
   }

   public boolean pushResource(String path, HttpString method, HeaderMap requestHeaders, final HttpHandler handler) {
      HeaderMap responseHeaders = new HeaderMap();

      try {
         requestHeaders.put(Http2Channel.METHOD, method.toString());
         requestHeaders.put(Http2Channel.PATH, path.toString());
         requestHeaders.put(Http2Channel.AUTHORITY, this.exchange.getHostAndPort());
         requestHeaders.put(Http2Channel.SCHEME, this.exchange.getRequestScheme());
         Http2HeadersStreamSinkChannel sink = this.channel.sendPushPromise(this.responseChannel.getStreamId(), requestHeaders, responseHeaders);
         Http2ServerConnection newConnection = new Http2ServerConnection(this.channel, sink, this.getUndertowOptions(), this.getBufferSize(), this.rootHandler);
         final HttpServerExchange exchange = new HttpServerExchange(newConnection, requestHeaders, responseHeaders, this.getUndertowOptions().get(UndertowOptions.MAX_ENTITY_SIZE, -1L));
         newConnection.setExchange(exchange);
         exchange.setRequestMethod(method);
         exchange.setProtocol(Protocols.HTTP_1_1);
         exchange.setRequestScheme(this.exchange.getRequestScheme());

         try {
            Connectors.setExchangeRequestPath(exchange, path, (String)this.getUndertowOptions().get(UndertowOptions.URL_CHARSET, StandardCharsets.UTF_8.name()), this.getUndertowOptions().get(UndertowOptions.DECODE_URL, true), this.getUndertowOptions().get(UndertowOptions.ALLOW_ENCODED_SLASH, false), new StringBuilder(), this.getUndertowOptions().get(UndertowOptions.MAX_PARAMETERS, 200));
         } catch (ParameterLimitException var10) {
            UndertowLogger.REQUEST_IO_LOGGER.debug("Too many parameters in HTTP/2 request", var10);
            exchange.setStatusCode(400);
            exchange.endExchange();
            return false;
         }

         sink.setCompletionListener(new ChannelListener<Http2DataStreamSinkChannel>() {
            public void handleEvent(Http2DataStreamSinkChannel channel) {
               Connectors.terminateResponse(exchange);
            }
         });
         Connectors.terminateRequest(exchange);
         this.getIoThread().execute(new Runnable() {
            public void run() {
               Connectors.executeRootHandler(handler, exchange);
            }
         });
         return true;
      } catch (IOException var11) {
         UndertowLogger.REQUEST_IO_LOGGER.ioException(var11);
         return false;
      }
   }

   public String getTransportProtocol() {
      return this.channel.getProtocol();
   }
}
