/*     */ package io.undertow.server.protocol.http2;
/*     */ 
/*     */ import io.undertow.UndertowLogger;
/*     */ import io.undertow.UndertowMessages;
/*     */ import io.undertow.UndertowOptions;
/*     */ import io.undertow.connector.ByteBufferPool;
/*     */ import io.undertow.protocols.http2.Http2Channel;
/*     */ import io.undertow.protocols.http2.Http2DataStreamSinkChannel;
/*     */ import io.undertow.protocols.http2.Http2HeadersStreamSinkChannel;
/*     */ import io.undertow.protocols.http2.Http2StreamSourceChannel;
/*     */ import io.undertow.server.ConduitWrapper;
/*     */ import io.undertow.server.Connectors;
/*     */ import io.undertow.server.ExchangeCompletionListener;
/*     */ import io.undertow.server.HttpHandler;
/*     */ import io.undertow.server.HttpServerExchange;
/*     */ import io.undertow.server.HttpUpgradeListener;
/*     */ import io.undertow.server.SSLSessionInfo;
/*     */ import io.undertow.server.ServerConnection;
/*     */ import io.undertow.server.XnioBufferPoolAdaptor;
/*     */ import io.undertow.server.protocol.http.HttpContinue;
/*     */ import io.undertow.util.AttachmentKey;
/*     */ import io.undertow.util.AttachmentList;
/*     */ import io.undertow.util.ConduitFactory;
/*     */ import io.undertow.util.DateUtils;
/*     */ import io.undertow.util.HeaderMap;
/*     */ import io.undertow.util.Headers;
/*     */ import io.undertow.util.HttpString;
/*     */ import io.undertow.util.ParameterLimitException;
/*     */ import io.undertow.util.Protocols;
/*     */ import java.io.IOException;
/*     */ import java.net.SocketAddress;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.channels.Channel;
/*     */ import java.nio.charset.StandardCharsets;
/*     */ import java.util.Collection;
/*     */ import java.util.List;
/*     */ import javax.net.ssl.SSLSession;
/*     */ import org.xnio.ChannelListener;
/*     */ import org.xnio.Option;
/*     */ import org.xnio.OptionMap;
/*     */ import org.xnio.Pool;
/*     */ import org.xnio.StreamConnection;
/*     */ import org.xnio.XnioIoThread;
/*     */ import org.xnio.XnioWorker;
/*     */ import org.xnio.channels.Configurable;
/*     */ import org.xnio.channels.ConnectedChannel;
/*     */ import org.xnio.channels.StreamSinkChannel;
/*     */ import org.xnio.channels.StreamSourceChannel;
/*     */ import org.xnio.channels.SuspendableWriteChannel;
/*     */ import org.xnio.conduits.Conduit;
/*     */ import org.xnio.conduits.ConduitStreamSinkChannel;
/*     */ import org.xnio.conduits.ConduitStreamSourceChannel;
/*     */ import org.xnio.conduits.EmptyStreamSourceConduit;
/*     */ import org.xnio.conduits.StreamSinkChannelWrappingConduit;
/*     */ import org.xnio.conduits.StreamSinkConduit;
/*     */ import org.xnio.conduits.StreamSourceChannelWrappingConduit;
/*     */ import org.xnio.conduits.StreamSourceConduit;
/*     */ import org.xnio.conduits.WriteReadyHandler;
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
/*     */ public class Http2ServerConnection
/*     */   extends ServerConnection
/*     */ {
/*  90 */   private static final HttpString STATUS = new HttpString(":status");
/*     */   
/*     */   private final Http2Channel channel;
/*     */   private final Http2StreamSourceChannel requestChannel;
/*     */   private final Http2DataStreamSinkChannel responseChannel;
/*     */   private final ConduitStreamSinkChannel conduitStreamSinkChannel;
/*     */   private final ConduitStreamSourceChannel conduitStreamSourceChannel;
/*     */   private final StreamSinkConduit originalSinkConduit;
/*     */   private final StreamSourceConduit originalSourceConduit;
/*     */   private final OptionMap undertowOptions;
/*     */   private final int bufferSize;
/*     */   private SSLSessionInfo sessionInfo;
/*     */   private final HttpHandler rootHandler;
/*     */   private HttpServerExchange exchange;
/*     */   private boolean continueSent = false;
/*     */   private XnioBufferPoolAdaptor poolAdaptor;
/*     */   
/*     */   public Http2ServerConnection(Http2Channel channel, Http2StreamSourceChannel requestChannel, OptionMap undertowOptions, int bufferSize, HttpHandler rootHandler) {
/* 108 */     this.channel = channel;
/* 109 */     this.requestChannel = requestChannel;
/* 110 */     this.undertowOptions = undertowOptions;
/* 111 */     this.bufferSize = bufferSize;
/* 112 */     this.rootHandler = rootHandler;
/* 113 */     this.responseChannel = (Http2DataStreamSinkChannel)requestChannel.getResponseChannel();
/* 114 */     this.originalSinkConduit = (StreamSinkConduit)new StreamSinkChannelWrappingConduit((StreamSinkChannel)this.responseChannel);
/* 115 */     this.originalSourceConduit = (StreamSourceConduit)new StreamSourceChannelWrappingConduit((StreamSourceChannel)requestChannel);
/* 116 */     this.conduitStreamSinkChannel = new ConduitStreamSinkChannel((Configurable)this.responseChannel, this.originalSinkConduit);
/* 117 */     this.conduitStreamSourceChannel = new ConduitStreamSourceChannel((Configurable)channel, this.originalSourceConduit);
/*     */   }
/*     */   
/*     */   void setExchange(HttpServerExchange exchange) {
/* 121 */     this.exchange = exchange;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Http2ServerConnection(Http2Channel channel, Http2DataStreamSinkChannel sinkChannel, OptionMap undertowOptions, int bufferSize, HttpHandler rootHandler) {
/* 132 */     this.channel = channel;
/* 133 */     this.rootHandler = rootHandler;
/* 134 */     this.requestChannel = null;
/* 135 */     this.undertowOptions = undertowOptions;
/* 136 */     this.bufferSize = bufferSize;
/* 137 */     this.responseChannel = sinkChannel;
/* 138 */     this.originalSinkConduit = (StreamSinkConduit)new StreamSinkChannelWrappingConduit((StreamSinkChannel)this.responseChannel);
/* 139 */     this.originalSourceConduit = (StreamSourceConduit)new StreamSourceChannelWrappingConduit((StreamSourceChannel)this.requestChannel);
/* 140 */     this.conduitStreamSinkChannel = new ConduitStreamSinkChannel((Configurable)this.responseChannel, this.originalSinkConduit);
/* 141 */     this.conduitStreamSourceChannel = new ConduitStreamSourceChannel(Configurable.EMPTY, (StreamSourceConduit)new EmptyStreamSourceConduit(getIoThread()));
/*     */   }
/*     */   
/*     */   public Pool<ByteBuffer> getBufferPool() {
/* 145 */     if (this.poolAdaptor == null) {
/* 146 */       this.poolAdaptor = new XnioBufferPoolAdaptor(getByteBufferPool());
/*     */     }
/* 148 */     return (Pool<ByteBuffer>)this.poolAdaptor;
/*     */   }
/*     */   
/*     */   public SSLSession getSslSession() {
/* 152 */     return this.channel.getSslSession();
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBufferPool getByteBufferPool() {
/* 157 */     return this.channel.getBufferPool();
/*     */   }
/*     */ 
/*     */   
/*     */   public XnioWorker getWorker() {
/* 162 */     return this.channel.getWorker();
/*     */   }
/*     */ 
/*     */   
/*     */   public XnioIoThread getIoThread() {
/* 167 */     return this.channel.getIoThread();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public HttpServerExchange sendOutOfBandResponse(HttpServerExchange exchange) {
/* 173 */     if (exchange == null || !HttpContinue.requiresContinueResponse(exchange)) {
/* 174 */       throw UndertowMessages.MESSAGES.outOfBandResponseOnlyAllowedFor100Continue();
/*     */     }
/* 176 */     final HttpServerExchange newExchange = new HttpServerExchange(this);
/* 177 */     for (HttpString header : exchange.getRequestHeaders().getHeaderNames()) {
/* 178 */       newExchange.getRequestHeaders().putAll(header, (Collection)exchange.getRequestHeaders().get(header));
/*     */     }
/* 180 */     newExchange.setProtocol(exchange.getProtocol());
/* 181 */     newExchange.setRequestMethod(exchange.getRequestMethod());
/* 182 */     exchange.setRequestURI(exchange.getRequestURI(), exchange.isHostIncludedInRequestURI());
/* 183 */     exchange.setRequestPath(exchange.getRequestPath());
/* 184 */     exchange.setRelativePath(exchange.getRelativePath());
/* 185 */     newExchange.setPersistent(true);
/*     */     
/* 187 */     Connectors.terminateRequest(newExchange);
/* 188 */     newExchange.addResponseWrapper(new ConduitWrapper<StreamSinkConduit>()
/*     */         {
/*     */           public StreamSinkConduit wrap(ConduitFactory<StreamSinkConduit> factory, HttpServerExchange exchange)
/*     */           {
/* 192 */             HeaderMap headers = newExchange.getResponseHeaders();
/* 193 */             DateUtils.addDateHeaderIfRequired(exchange);
/* 194 */             headers.add(Http2ServerConnection.STATUS, exchange.getStatusCode());
/* 195 */             Connectors.flattenCookies(exchange);
/* 196 */             Http2HeadersStreamSinkChannel sink = new Http2HeadersStreamSinkChannel(Http2ServerConnection.this.channel, Http2ServerConnection.this.requestChannel.getStreamId(), headers);
/*     */             
/* 198 */             StreamSinkChannelWrappingConduit ret = new StreamSinkChannelWrappingConduit((StreamSinkChannel)sink);
/* 199 */             ret.setWriteReadyHandler((WriteReadyHandler)new WriteReadyHandler.ChannelListenerHandler((SuspendableWriteChannel)Connectors.getConduitSinkChannel(exchange)));
/* 200 */             return (StreamSinkConduit)ret;
/*     */           }
/*     */         });
/* 203 */     this.continueSent = true;
/* 204 */     return newExchange;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isContinueResponseSupported() {
/* 210 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public void terminateRequestChannel(HttpServerExchange exchange) {
/* 215 */     if (HttpContinue.requiresContinueResponse(exchange.getRequestHeaders()) && !this.continueSent && 
/* 216 */       this.requestChannel != null) {
/* 217 */       this.requestChannel.setIgnoreForceClose(true);
/* 218 */       this.requestChannel.close();
/*     */ 
/*     */       
/* 221 */       exchange.addExchangeCompleteListener(new ExchangeCompletionListener()
/*     */           {
/*     */             public void exchangeEvent(HttpServerExchange exchange, ExchangeCompletionListener.NextListener nextListener) {
/*     */               try {
/* 225 */                 Http2ServerConnection.this.channel.sendRstStream(Http2ServerConnection.this.responseChannel.getStreamId(), 8);
/*     */               } finally {
/* 227 */                 nextListener.proceed();
/*     */               } 
/*     */             }
/*     */           });
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isOpen() {
/* 237 */     return this.channel.isOpen();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean supportsOption(Option<?> option) {
/* 242 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public <T> T getOption(Option<T> option) throws IOException {
/* 247 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public <T> T setOption(Option<T> option, T value) throws IllegalArgumentException, IOException {
/* 252 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public void close() throws IOException {
/* 257 */     this.channel.sendRstStream(this.requestChannel.getStreamId(), 8);
/*     */   }
/*     */ 
/*     */   
/*     */   public SocketAddress getPeerAddress() {
/* 262 */     return this.channel.getPeerAddress();
/*     */   }
/*     */ 
/*     */   
/*     */   public <A extends SocketAddress> A getPeerAddress(Class<A> type) {
/* 267 */     return (A)this.channel.getPeerAddress(type);
/*     */   }
/*     */ 
/*     */   
/*     */   public ChannelListener.Setter<? extends ConnectedChannel> getCloseSetter() {
/* 272 */     return this.channel.getCloseSetter();
/*     */   }
/*     */ 
/*     */   
/*     */   public SocketAddress getLocalAddress() {
/* 277 */     return this.channel.getLocalAddress();
/*     */   }
/*     */ 
/*     */   
/*     */   public <A extends SocketAddress> A getLocalAddress(Class<A> type) {
/* 282 */     return (A)this.channel.getLocalAddress(type);
/*     */   }
/*     */ 
/*     */   
/*     */   public OptionMap getUndertowOptions() {
/* 287 */     return this.undertowOptions;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getBufferSize() {
/* 292 */     return this.bufferSize;
/*     */   }
/*     */ 
/*     */   
/*     */   public SSLSessionInfo getSslSessionInfo() {
/* 297 */     return this.sessionInfo;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setSslSessionInfo(SSLSessionInfo sessionInfo) {
/* 302 */     this.sessionInfo = sessionInfo;
/*     */   }
/*     */ 
/*     */   
/*     */   public void addCloseListener(final ServerConnection.CloseListener listener) {
/* 307 */     this.channel.addCloseTask(new ChannelListener<Http2Channel>()
/*     */         {
/*     */           public void handleEvent(Http2Channel channel) {
/* 310 */             listener.closed(Http2ServerConnection.this);
/*     */           }
/*     */         });
/*     */   }
/*     */ 
/*     */   
/*     */   protected StreamConnection upgradeChannel() {
/* 317 */     throw UndertowMessages.MESSAGES.upgradeNotSupported();
/*     */   }
/*     */ 
/*     */   
/*     */   protected ConduitStreamSinkChannel getSinkChannel() {
/* 322 */     return this.conduitStreamSinkChannel;
/*     */   }
/*     */ 
/*     */   
/*     */   protected ConduitStreamSourceChannel getSourceChannel() {
/* 327 */     return this.conduitStreamSourceChannel;
/*     */   }
/*     */ 
/*     */   
/*     */   protected StreamSinkConduit getSinkConduit(HttpServerExchange exchange, StreamSinkConduit conduit) {
/* 332 */     HeaderMap headers = this.responseChannel.getHeaders();
/* 333 */     DateUtils.addDateHeaderIfRequired(exchange);
/* 334 */     headers.add(STATUS, exchange.getStatusCode());
/* 335 */     Connectors.flattenCookies(exchange);
/* 336 */     if (!Connectors.isEntityBodyAllowed(exchange)) {
/*     */       
/* 338 */       exchange.getResponseHeaders().remove(Headers.CONTENT_LENGTH);
/* 339 */       exchange.getResponseHeaders().remove(Headers.TRANSFER_ENCODING);
/*     */     } 
/* 341 */     return this.originalSinkConduit;
/*     */   }
/*     */ 
/*     */   
/*     */   protected boolean isUpgradeSupported() {
/* 346 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   protected boolean isConnectSupported() {
/* 351 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected void exchangeComplete(HttpServerExchange exchange) {}
/*     */ 
/*     */   
/*     */   protected void setUpgradeListener(HttpUpgradeListener upgradeListener) {
/* 360 */     throw UndertowMessages.MESSAGES.upgradeNotSupported();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected void setConnectListener(HttpUpgradeListener connectListener) {}
/*     */ 
/*     */ 
/*     */   
/*     */   protected void maxEntitySizeUpdated(HttpServerExchange exchange) {
/* 370 */     if (this.requestChannel != null) {
/* 371 */       this.requestChannel.setMaxStreamSize(exchange.getMaxEntitySize());
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public <T> void addToAttachmentList(AttachmentKey<AttachmentList<T>> key, T value) {
/* 377 */     this.channel.addToAttachmentList(key, value);
/*     */   }
/*     */ 
/*     */   
/*     */   public <T> T removeAttachment(AttachmentKey<T> key) {
/* 382 */     return (T)this.channel.removeAttachment(key);
/*     */   }
/*     */ 
/*     */   
/*     */   public <T> T putAttachment(AttachmentKey<T> key, T value) {
/* 387 */     return (T)this.channel.putAttachment(key, value);
/*     */   }
/*     */ 
/*     */   
/*     */   public <T> List<T> getAttachmentList(AttachmentKey<? extends List<T>> key) {
/* 392 */     return this.channel.getAttachmentList(key);
/*     */   }
/*     */ 
/*     */   
/*     */   public <T> T getAttachment(AttachmentKey<T> key) {
/* 397 */     return (T)this.channel.getAttachment(key);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isPushSupported() {
/* 402 */     return (this.channel.isPushEnabled() && 
/* 403 */       !this.exchange.getRequestHeaders().contains(Headers.X_DISABLE_PUSH) && this.responseChannel
/*     */       
/* 405 */       .getStreamId() % 2 != 0);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isRequestTrailerFieldsSupported() {
/* 410 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean pushResource(String path, HttpString method, HeaderMap requestHeaders) {
/* 415 */     return pushResource(path, method, requestHeaders, this.rootHandler);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean pushResource(String path, HttpString method, HeaderMap requestHeaders, final HttpHandler handler) {
/* 420 */     HeaderMap responseHeaders = new HeaderMap();
/*     */     try {
/* 422 */       requestHeaders.put(Http2Channel.METHOD, method.toString());
/* 423 */       requestHeaders.put(Http2Channel.PATH, path.toString());
/* 424 */       requestHeaders.put(Http2Channel.AUTHORITY, this.exchange.getHostAndPort());
/* 425 */       requestHeaders.put(Http2Channel.SCHEME, this.exchange.getRequestScheme());
/*     */       
/* 427 */       Http2HeadersStreamSinkChannel sink = this.channel.sendPushPromise(this.responseChannel.getStreamId(), requestHeaders, responseHeaders);
/* 428 */       Http2ServerConnection newConnection = new Http2ServerConnection(this.channel, (Http2DataStreamSinkChannel)sink, getUndertowOptions(), getBufferSize(), this.rootHandler);
/* 429 */       final HttpServerExchange exchange = new HttpServerExchange(newConnection, requestHeaders, responseHeaders, getUndertowOptions().get(UndertowOptions.MAX_ENTITY_SIZE, -1L));
/* 430 */       newConnection.setExchange(exchange);
/* 431 */       exchange.setRequestMethod(method);
/* 432 */       exchange.setProtocol(Protocols.HTTP_1_1);
/* 433 */       exchange.setRequestScheme(this.exchange.getRequestScheme());
/*     */       try {
/* 435 */         Connectors.setExchangeRequestPath(exchange, path, (String)getUndertowOptions().get(UndertowOptions.URL_CHARSET, StandardCharsets.UTF_8.name()), getUndertowOptions().get(UndertowOptions.DECODE_URL, true), getUndertowOptions().get(UndertowOptions.ALLOW_ENCODED_SLASH, false), new StringBuilder(), getUndertowOptions().get(UndertowOptions.MAX_PARAMETERS, 200));
/* 436 */       } catch (ParameterLimitException e) {
/* 437 */         UndertowLogger.REQUEST_IO_LOGGER.debug("Too many parameters in HTTP/2 request", (Throwable)e);
/* 438 */         exchange.setStatusCode(400);
/* 439 */         exchange.endExchange();
/* 440 */         return false;
/*     */       } 
/*     */       
/* 443 */       sink.setCompletionListener(new ChannelListener<Http2DataStreamSinkChannel>()
/*     */           {
/*     */             public void handleEvent(Http2DataStreamSinkChannel channel) {
/* 446 */               Connectors.terminateResponse(exchange);
/*     */             }
/*     */           });
/* 449 */       Connectors.terminateRequest(exchange);
/* 450 */       getIoThread().execute(new Runnable()
/*     */           {
/*     */             public void run() {
/* 453 */               Connectors.executeRootHandler(handler, exchange);
/*     */             }
/*     */           });
/* 456 */       return true;
/* 457 */     } catch (IOException e) {
/* 458 */       UndertowLogger.REQUEST_IO_LOGGER.ioException(e);
/* 459 */       return false;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public String getTransportProtocol() {
/* 465 */     return this.channel.getProtocol();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\protocol\http2\Http2ServerConnection.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */