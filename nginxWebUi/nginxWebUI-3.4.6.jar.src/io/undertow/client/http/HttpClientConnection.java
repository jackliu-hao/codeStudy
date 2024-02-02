/*     */ package io.undertow.client.http;
/*     */ 
/*     */ import io.undertow.UndertowLogger;
/*     */ import io.undertow.UndertowOptions;
/*     */ import io.undertow.client.ClientCallback;
/*     */ import io.undertow.client.ClientConnection;
/*     */ import io.undertow.client.ClientExchange;
/*     */ import io.undertow.client.ClientRequest;
/*     */ import io.undertow.client.ClientResponse;
/*     */ import io.undertow.client.ClientStatistics;
/*     */ import io.undertow.client.UndertowClientMessages;
/*     */ import io.undertow.client.http2.Http2ClearClientProvider;
/*     */ import io.undertow.client.http2.Http2ClientConnection;
/*     */ import io.undertow.conduits.ByteActivityCallback;
/*     */ import io.undertow.conduits.BytesReceivedStreamSourceConduit;
/*     */ import io.undertow.conduits.BytesSentStreamSinkConduit;
/*     */ import io.undertow.conduits.ChunkedStreamSinkConduit;
/*     */ import io.undertow.conduits.ChunkedStreamSourceConduit;
/*     */ import io.undertow.conduits.ConduitListener;
/*     */ import io.undertow.conduits.FinishableStreamSourceConduit;
/*     */ import io.undertow.conduits.FixedLengthStreamSourceConduit;
/*     */ import io.undertow.conduits.ReadTimeoutStreamSourceConduit;
/*     */ import io.undertow.conduits.WriteTimeoutStreamSinkConduit;
/*     */ import io.undertow.connector.ByteBufferPool;
/*     */ import io.undertow.connector.PooledByteBuffer;
/*     */ import io.undertow.protocols.http2.Http2Channel;
/*     */ import io.undertow.server.Connectors;
/*     */ import io.undertow.server.OpenListener;
/*     */ import io.undertow.server.protocol.http.HttpContinue;
/*     */ import io.undertow.server.protocol.http.HttpOpenListener;
/*     */ import io.undertow.util.AbstractAttachable;
/*     */ import io.undertow.util.Attachable;
/*     */ import io.undertow.util.ConnectionUtils;
/*     */ import io.undertow.util.Headers;
/*     */ import io.undertow.util.HttpString;
/*     */ import io.undertow.util.Methods;
/*     */ import io.undertow.util.PooledAdaptor;
/*     */ import io.undertow.util.Protocols;
/*     */ import java.io.Closeable;
/*     */ import java.io.IOException;
/*     */ import java.net.SocketAddress;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.channels.Channel;
/*     */ import java.nio.channels.ClosedChannelException;
/*     */ import java.util.ArrayDeque;
/*     */ import java.util.Deque;
/*     */ import java.util.List;
/*     */ import java.util.Locale;
/*     */ import java.util.concurrent.CopyOnWriteArrayList;
/*     */ import org.jboss.logging.Logger;
/*     */ import org.xnio.Bits;
/*     */ import org.xnio.ChannelExceptionHandler;
/*     */ import org.xnio.ChannelListener;
/*     */ import org.xnio.ChannelListeners;
/*     */ import org.xnio.IoUtils;
/*     */ import org.xnio.Option;
/*     */ import org.xnio.OptionMap;
/*     */ import org.xnio.Options;
/*     */ import org.xnio.Pooled;
/*     */ import org.xnio.StreamConnection;
/*     */ import org.xnio.XnioIoThread;
/*     */ import org.xnio.XnioWorker;
/*     */ import org.xnio.channels.StreamSourceChannel;
/*     */ import org.xnio.conduits.Conduit;
/*     */ import org.xnio.conduits.ConduitStreamSinkChannel;
/*     */ import org.xnio.conduits.ConduitStreamSourceChannel;
/*     */ import org.xnio.conduits.PushBackStreamSourceConduit;
/*     */ import org.xnio.conduits.StreamSinkConduit;
/*     */ import org.xnio.conduits.StreamSourceConduit;
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
/*     */ class HttpClientConnection
/*     */   extends AbstractAttachable
/*     */   implements Closeable, ClientConnection
/*     */ {
/*  98 */   public final ConduitListener<StreamSinkConduit> requestFinishListener = new ConduitListener<StreamSinkConduit>()
/*     */     {
/*     */       public void handleEvent(StreamSinkConduit channel) {
/* 101 */         if (HttpClientConnection.this.currentRequest != null)
/* 102 */           HttpClientConnection.this.currentRequest.terminateRequest(); 
/*     */       }
/*     */     };
/*     */   
/* 106 */   public final ConduitListener<StreamSourceConduit> responseFinishedListener = new ConduitListener<StreamSourceConduit>()
/*     */     {
/*     */       public void handleEvent(StreamSourceConduit channel) {
/* 109 */         if (HttpClientConnection.this.currentRequest != null) {
/* 110 */           HttpClientConnection.this.currentRequest.terminateResponse();
/*     */         }
/*     */       }
/*     */     };
/*     */   
/* 115 */   private static final Logger log = Logger.getLogger(HttpClientConnection.class);
/*     */   
/* 117 */   private final Deque<HttpClientExchange> pendingQueue = new ArrayDeque<>();
/*     */   
/*     */   private HttpClientExchange currentRequest;
/*     */   private HttpResponseBuilder pendingResponse;
/*     */   private final OptionMap options;
/*     */   private final StreamConnection connection;
/*     */   private final PushBackStreamSourceConduit pushBackStreamSourceConduit;
/* 124 */   private final ClientReadListener clientReadListener = new ClientReadListener();
/*     */   
/*     */   private final ByteBufferPool bufferPool;
/*     */   
/*     */   private PooledByteBuffer pooledBuffer;
/*     */   
/*     */   private final StreamSinkConduit originalSinkConduit;
/*     */   private static final int UPGRADED = 268435456;
/*     */   private static final int UPGRADE_REQUESTED = 536870912;
/*     */   private static final int CLOSE_REQ = 1073741824;
/*     */   private static final int CLOSED = -2147483648;
/*     */   private int state;
/* 136 */   private final ChannelListener.SimpleSetter<HttpClientConnection> closeSetter = new ChannelListener.SimpleSetter();
/*     */   
/*     */   private final ClientStatistics clientStatistics;
/*     */   
/*     */   private int requestCount;
/*     */   
/*     */   private int read;
/*     */   
/*     */   private int written;
/*     */   private boolean http2Tried = false;
/*     */   private boolean http2UpgradeReceived = false;
/*     */   private ClientConnection http2Delegate;
/* 148 */   private final List<ChannelListener<ClientConnection>> closeListeners = new CopyOnWriteArrayList<>();
/*     */ 
/*     */ 
/*     */   
/*     */   HttpClientConnection(StreamConnection connection, OptionMap options, ByteBufferPool bufferPool) {
/* 153 */     if (options.get(UndertowOptions.ENABLE_STATISTICS, false)) {
/* 154 */       this.clientStatistics = new ClientStatisticsImpl();
/* 155 */       connection.getSinkChannel().setConduit((StreamSinkConduit)new BytesSentStreamSinkConduit(connection.getSinkChannel().getConduit(), new ByteActivityCallback()
/*     */             {
/*     */               public void activity(long bytes) {
/* 158 */                 HttpClientConnection.this.written = (int)(HttpClientConnection.this.written + bytes);
/*     */               }
/*     */             }));
/* 161 */       connection.getSourceChannel().setConduit((StreamSourceConduit)new BytesReceivedStreamSourceConduit(connection.getSourceChannel().getConduit(), new ByteActivityCallback()
/*     */             {
/*     */               public void activity(long bytes) {
/* 164 */                 HttpClientConnection.this.read = (int)(HttpClientConnection.this.read + bytes);
/*     */               }
/*     */             }));
/*     */     } else {
/* 168 */       this.clientStatistics = null;
/*     */     } 
/* 170 */     this.options = options;
/* 171 */     this.connection = connection;
/* 172 */     this.pushBackStreamSourceConduit = new PushBackStreamSourceConduit(connection.getSourceChannel().getConduit());
/* 173 */     this.connection.getSourceChannel().setConduit((StreamSourceConduit)this.pushBackStreamSourceConduit);
/* 174 */     this.bufferPool = bufferPool;
/* 175 */     this.originalSinkConduit = connection.getSinkChannel().getConduit();
/*     */     
/* 177 */     connection.getCloseSetter().set(new ChannelListener<StreamConnection>()
/*     */         {
/*     */           public void handleEvent(StreamConnection channel) {
/* 180 */             HttpClientConnection.log.debugf("connection to %s closed", HttpClientConnection.this.getPeerAddress());
/* 181 */             HttpClientConnection httpClientConnection = HttpClientConnection.this; httpClientConnection.state = httpClientConnection.state | Integer.MIN_VALUE;
/* 182 */             ChannelListeners.invokeChannelListener((Channel)HttpClientConnection.this, HttpClientConnection.this.closeSetter.get());
/*     */             try {
/* 184 */               if (HttpClientConnection.this.pooledBuffer != null) {
/* 185 */                 HttpClientConnection.this.pooledBuffer.close();
/*     */               }
/* 187 */             } catch (Throwable throwable) {}
/*     */             
/* 189 */             for (ChannelListener<ClientConnection> listener : (Iterable<ChannelListener<ClientConnection>>)HttpClientConnection.this.closeListeners) {
/* 190 */               listener.handleEvent((Channel)HttpClientConnection.this);
/*     */             }
/* 192 */             HttpClientExchange pending = HttpClientConnection.this.pendingQueue.poll();
/* 193 */             while (pending != null) {
/* 194 */               pending.setFailed(new ClosedChannelException());
/* 195 */               pending = HttpClientConnection.this.pendingQueue.poll();
/*     */             } 
/* 197 */             if (HttpClientConnection.this.currentRequest != null) {
/* 198 */               HttpClientConnection.this.currentRequest.setFailed(new ClosedChannelException());
/* 199 */               HttpClientConnection.this.currentRequest = null;
/* 200 */               HttpClientConnection.this.pendingResponse = null;
/*     */             } 
/*     */           }
/*     */         });
/*     */     
/* 205 */     connection.getSourceChannel().setReadListener(this.clientReadListener);
/* 206 */     connection.getSourceChannel().resumeReads();
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBufferPool getBufferPool() {
/* 211 */     return this.bufferPool;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public SocketAddress getPeerAddress() {
/* 217 */     return this.connection.getPeerAddress();
/*     */   }
/*     */   
/*     */   StreamConnection getConnection() {
/* 221 */     return this.connection;
/*     */   }
/*     */ 
/*     */   
/*     */   public <A extends SocketAddress> A getPeerAddress(Class<A> type) {
/* 226 */     return (A)this.connection.getPeerAddress(type);
/*     */   }
/*     */ 
/*     */   
/*     */   public ChannelListener.Setter<? extends HttpClientConnection> getCloseSetter() {
/* 231 */     return (ChannelListener.Setter<? extends HttpClientConnection>)this.closeSetter;
/*     */   }
/*     */ 
/*     */   
/*     */   public SocketAddress getLocalAddress() {
/* 236 */     return this.connection.getLocalAddress();
/*     */   }
/*     */ 
/*     */   
/*     */   public <A extends SocketAddress> A getLocalAddress(Class<A> type) {
/* 241 */     return (A)this.connection.getLocalAddress(type);
/*     */   }
/*     */ 
/*     */   
/*     */   public XnioWorker getWorker() {
/* 246 */     return this.connection.getWorker();
/*     */   }
/*     */ 
/*     */   
/*     */   public XnioIoThread getIoThread() {
/* 251 */     return this.connection.getIoThread();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isOpen() {
/* 256 */     if (this.http2Delegate != null) {
/* 257 */       return this.http2Delegate.isOpen();
/*     */     }
/* 259 */     return (this.connection.isOpen() && Bits.allAreClear(this.state, -1073741824));
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean supportsOption(Option<?> option) {
/* 264 */     if (this.http2Delegate != null) {
/* 265 */       return this.http2Delegate.supportsOption(option);
/*     */     }
/* 267 */     return this.connection.supportsOption(option);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public <T> T getOption(Option<T> option) throws IOException {
/* 273 */     if (this.http2Delegate != null) {
/* 274 */       return (T)this.http2Delegate.getOption(option);
/*     */     }
/* 276 */     return (T)this.connection.getOption(option);
/*     */   }
/*     */ 
/*     */   
/*     */   public <T> T setOption(Option<T> option, T value) throws IllegalArgumentException, IOException {
/* 281 */     if (this.http2Delegate != null) {
/* 282 */       return (T)this.http2Delegate.setOption(option, value);
/*     */     }
/* 284 */     return (T)this.connection.setOption(option, value);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isUpgraded() {
/* 289 */     if (this.http2Delegate != null) {
/* 290 */       return this.http2Delegate.isUpgraded();
/*     */     }
/* 292 */     return Bits.anyAreSet(this.state, 805306368);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isPushSupported() {
/* 297 */     if (this.http2Delegate != null) {
/* 298 */       return this.http2Delegate.isPushSupported();
/*     */     }
/* 300 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isMultiplexingSupported() {
/* 305 */     if (this.http2Delegate != null) {
/* 306 */       return this.http2Delegate.isMultiplexingSupported();
/*     */     }
/* 308 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public ClientStatistics getStatistics() {
/* 313 */     if (this.http2Delegate != null) {
/* 314 */       return this.http2Delegate.getStatistics();
/*     */     }
/* 316 */     return this.clientStatistics;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isUpgradeSupported() {
/* 321 */     if (this.http2Delegate != null) {
/* 322 */       return false;
/*     */     }
/* 324 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public void addCloseListener(ChannelListener<ClientConnection> listener) {
/* 329 */     this.closeListeners.add(listener);
/*     */   }
/*     */ 
/*     */   
/*     */   public void sendRequest(ClientRequest request, ClientCallback<ClientExchange> clientCallback) {
/*     */     try {
/* 335 */       Integer readTimeout = (Integer)this.connection.getOption(Options.READ_TIMEOUT);
/* 336 */       if (readTimeout != null && readTimeout.intValue() > 0) {
/* 337 */         this.connection.getSourceChannel().setConduit((StreamSourceConduit)new ReadTimeoutStreamSourceConduit(this.connection.getSourceChannel().getConduit(), this.connection, (OpenListener)new HttpOpenListener(this.bufferPool)));
/*     */       }
/* 339 */       Integer writeTimeout = (Integer)this.connection.getOption(Options.WRITE_TIMEOUT);
/* 340 */       if (writeTimeout != null && writeTimeout.intValue() > 0) {
/* 341 */         this.connection.getSinkChannel().setConduit((StreamSinkConduit)new WriteTimeoutStreamSinkConduit(this.connection.getSinkChannel().getConduit(), this.connection, (OpenListener)new HttpOpenListener(this.bufferPool)));
/*     */       }
/* 343 */     } catch (IOException e) {
/* 344 */       IoUtils.safeClose((Closeable)this.connection);
/* 345 */       UndertowLogger.REQUEST_IO_LOGGER.ioException(e);
/*     */     } 
/* 347 */     if (this.http2Delegate != null) {
/* 348 */       this.http2Delegate.sendRequest(request, clientCallback);
/*     */       return;
/*     */     } 
/* 351 */     if (Bits.anyAreSet(this.state, -268435456)) {
/* 352 */       clientCallback.failed(UndertowClientMessages.MESSAGES.invalidConnectionState());
/*     */       return;
/*     */     } 
/* 355 */     HttpClientExchange httpClientExchange = new HttpClientExchange(clientCallback, request, this);
/* 356 */     boolean ssl = this.connection instanceof org.xnio.ssl.SslConnection;
/* 357 */     if (!ssl && !this.http2Tried && this.options.get(UndertowOptions.ENABLE_HTTP2, false) && !request.getRequestHeaders().contains(Headers.UPGRADE)) {
/*     */       
/* 359 */       request.getRequestHeaders().put(new HttpString("HTTP2-Settings"), Http2ClearClientProvider.createSettingsFrame(this.options, this.bufferPool));
/* 360 */       request.getRequestHeaders().put(Headers.UPGRADE, "h2c");
/* 361 */       request.getRequestHeaders().put(Headers.CONNECTION, "Upgrade, HTTP2-Settings");
/* 362 */       this.http2Tried = true;
/*     */     } 
/*     */     
/* 365 */     if (this.currentRequest == null) {
/* 366 */       initiateRequest(httpClientExchange);
/*     */     } else {
/* 368 */       this.pendingQueue.add(httpClientExchange);
/*     */     } 
/*     */   }
/*     */   private void initiateRequest(HttpClientExchange httpClientExchange) {
/*     */     ClientFixedLengthStreamSinkConduit clientFixedLengthStreamSinkConduit;
/* 373 */     this.requestCount++;
/* 374 */     this.currentRequest = httpClientExchange;
/* 375 */     this.pendingResponse = new HttpResponseBuilder();
/* 376 */     ClientRequest request = httpClientExchange.getRequest();
/*     */     
/* 378 */     String connectionString = request.getRequestHeaders().getFirst(Headers.CONNECTION);
/* 379 */     if (connectionString != null) {
/* 380 */       if (Headers.CLOSE.equalToString(connectionString)) {
/* 381 */         this.state |= 0x40000000;
/* 382 */       } else if (Headers.UPGRADE.equalToString(connectionString)) {
/* 383 */         this.state |= 0x20000000;
/*     */       } 
/* 385 */     } else if (request.getProtocol() != Protocols.HTTP_1_1) {
/* 386 */       this.state |= 0x40000000;
/*     */     } 
/* 388 */     if (request.getRequestHeaders().contains(Headers.UPGRADE)) {
/* 389 */       this.state |= 0x20000000;
/*     */     }
/* 391 */     if (request.getMethod().equals(Methods.CONNECT))
/*     */     {
/* 393 */       this.state |= 0x20000000;
/*     */     }
/*     */ 
/*     */     
/* 397 */     ConduitStreamSourceChannel sourceChannel = this.connection.getSourceChannel();
/* 398 */     sourceChannel.setReadListener(this.clientReadListener);
/* 399 */     sourceChannel.resumeReads();
/*     */     
/* 401 */     ConduitStreamSinkChannel sinkChannel = this.connection.getSinkChannel();
/* 402 */     StreamSinkConduit conduit = this.originalSinkConduit;
/* 403 */     HttpRequestConduit httpRequestConduit = new HttpRequestConduit(conduit, this.bufferPool, request);
/* 404 */     httpClientExchange.setRequestConduit(httpRequestConduit);
/* 405 */     HttpRequestConduit httpRequestConduit1 = httpRequestConduit;
/*     */     
/* 407 */     String fixedLengthString = request.getRequestHeaders().getFirst(Headers.CONTENT_LENGTH);
/* 408 */     String transferEncodingString = request.getRequestHeaders().getLast(Headers.TRANSFER_ENCODING);
/*     */     
/* 410 */     boolean hasContent = true;
/*     */     
/* 412 */     if (fixedLengthString != null) {
/*     */       try {
/* 414 */         long length = Long.parseLong(fixedLengthString);
/* 415 */         clientFixedLengthStreamSinkConduit = new ClientFixedLengthStreamSinkConduit((StreamSinkConduit)httpRequestConduit1, length, false, false, this.currentRequest);
/* 416 */         hasContent = (length != 0L);
/* 417 */       } catch (NumberFormatException e) {
/* 418 */         handleError(e); return;
/*     */       } 
/*     */     } else {
/* 421 */       ChunkedStreamSinkConduit chunkedStreamSinkConduit; if (transferEncodingString != null) {
/* 422 */         if (!transferEncodingString.toLowerCase(Locale.ENGLISH).contains(Headers.CHUNKED.toString())) {
/* 423 */           handleError(UndertowClientMessages.MESSAGES.unknownTransferEncoding(transferEncodingString));
/*     */           return;
/*     */         } 
/* 426 */         chunkedStreamSinkConduit = new ChunkedStreamSinkConduit((StreamSinkConduit)clientFixedLengthStreamSinkConduit, httpClientExchange.getConnection().getBufferPool(), false, false, httpClientExchange.getRequest().getRequestHeaders(), this.requestFinishListener, (Attachable)httpClientExchange);
/*     */       } else {
/* 428 */         clientFixedLengthStreamSinkConduit = new ClientFixedLengthStreamSinkConduit((StreamSinkConduit)chunkedStreamSinkConduit, 0L, false, false, this.currentRequest);
/* 429 */         hasContent = false;
/*     */       } 
/* 431 */     }  sinkChannel.setConduit((StreamSinkConduit)clientFixedLengthStreamSinkConduit);
/*     */     
/* 433 */     httpClientExchange.invokeReadReadyCallback();
/* 434 */     if (!hasContent) {
/*     */       
/*     */       try {
/*     */         
/* 438 */         sinkChannel.shutdownWrites();
/* 439 */         if (!sinkChannel.flush()) {
/* 440 */           sinkChannel.setWriteListener(ChannelListeners.flushingChannelListener(null, new ChannelExceptionHandler<ConduitStreamSinkChannel>()
/*     */                 {
/*     */                   public void handleException(ConduitStreamSinkChannel channel, IOException exception) {
/* 443 */                     HttpClientConnection.this.handleError(exception);
/*     */                   }
/*     */                 }));
/* 446 */           sinkChannel.resumeWrites();
/*     */         } 
/* 448 */       } catch (Throwable t) {
/* 449 */         handleError(t);
/*     */       } 
/*     */     }
/*     */   }
/*     */   
/*     */   private void handleError(Throwable exception) {
/* 455 */     if (exception instanceof IOException) {
/* 456 */       handleError((IOException)exception);
/*     */     } else {
/* 458 */       handleError(new IOException(exception));
/*     */     } 
/*     */   }
/*     */   private void handleError(IOException exception) {
/* 462 */     UndertowLogger.REQUEST_IO_LOGGER.ioException(exception);
/* 463 */     this.currentRequest.setFailed(exception);
/* 464 */     this.currentRequest = null;
/* 465 */     this.pendingResponse = null;
/* 466 */     IoUtils.safeClose((Closeable)this.connection);
/*     */   }
/*     */   
/*     */   public StreamConnection performUpgrade() throws IOException {
/* 470 */     log.debugf("connection to %s is being upgraded", getPeerAddress());
/*     */ 
/*     */     
/* 473 */     if (Bits.allAreSet(this.state, -805306368)) {
/* 474 */       throw new IOException(UndertowClientMessages.MESSAGES.connectionClosed());
/*     */     }
/* 476 */     this.state |= 0x10000000;
/* 477 */     this.connection.getSinkChannel().setConduit(this.originalSinkConduit);
/* 478 */     this.connection.getSourceChannel().setConduit((StreamSourceConduit)this.pushBackStreamSourceConduit);
/* 479 */     return this.connection;
/*     */   }
/*     */   
/*     */   public void close() throws IOException {
/* 483 */     log.debugf("close called on connection to %s", getPeerAddress());
/* 484 */     if (this.http2Delegate != null) {
/* 485 */       this.http2Delegate.close();
/*     */     }
/* 487 */     if (Bits.anyAreSet(this.state, -2147483648)) {
/*     */       return;
/*     */     }
/* 490 */     this.state |= 0xC0000000;
/* 491 */     ConnectionUtils.cleanClose(this.connection, new Closeable[0]);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void exchangeDone() {
/* 498 */     log.debugf("exchange complete in connection to %s", getPeerAddress());
/*     */     
/* 500 */     this.connection.getSinkChannel().setConduit(this.originalSinkConduit);
/* 501 */     this.connection.getSourceChannel().setConduit((StreamSourceConduit)this.pushBackStreamSourceConduit);
/* 502 */     this.connection.getSinkChannel().suspendWrites();
/* 503 */     this.connection.getSinkChannel().setWriteListener(null);
/*     */     
/* 505 */     if (Bits.anyAreSet(this.state, 1073741824)) {
/* 506 */       this.currentRequest = null;
/* 507 */       this.pendingResponse = null;
/* 508 */       this.state |= Integer.MIN_VALUE;
/* 509 */       IoUtils.safeClose((Closeable)this.connection);
/* 510 */     } else if (Bits.anyAreSet(this.state, 536870912)) {
/* 511 */       this.connection.getSourceChannel().suspendReads();
/* 512 */       this.currentRequest = null;
/* 513 */       this.pendingResponse = null;
/*     */       return;
/*     */     } 
/* 516 */     this.currentRequest = null;
/* 517 */     this.pendingResponse = null;
/*     */     
/* 519 */     HttpClientExchange next = this.pendingQueue.poll();
/* 520 */     if (next == null) {
/*     */       
/* 522 */       this.connection.getSourceChannel().setReadListener(this.clientReadListener);
/* 523 */       this.connection.getSourceChannel().resumeReads();
/*     */     } else {
/* 525 */       initiateRequest(next);
/*     */     } 
/*     */   }
/*     */   
/*     */   public void requestDataSent() {
/* 530 */     if (this.http2UpgradeReceived) {
/* 531 */       doHttp2Upgrade();
/*     */     }
/*     */   }
/*     */   
/*     */   class ClientReadListener
/*     */     implements ChannelListener<StreamSourceChannel>
/*     */   {
/*     */     public void handleEvent(StreamSourceChannel channel) {
/* 539 */       HttpResponseBuilder builder = HttpClientConnection.this.pendingResponse;
/* 540 */       PooledByteBuffer pooled = HttpClientConnection.this.bufferPool.allocate();
/* 541 */       ByteBuffer buffer = pooled.getBuffer();
/* 542 */       boolean free = true;
/*     */ 
/*     */       
/*     */       try {
/* 546 */         if (builder == null) {
/*     */           
/* 548 */           buffer.clear();
/*     */           try {
/* 550 */             int res = channel.read(buffer);
/* 551 */             if (res == -1) {
/* 552 */               UndertowLogger.CLIENT_LOGGER.debugf("Connection to %s was closed by the target server", HttpClientConnection.this.connection.getPeerAddress());
/* 553 */               IoUtils.safeClose(HttpClientConnection.this);
/* 554 */             } else if (res != 0) {
/* 555 */               UndertowLogger.CLIENT_LOGGER.debugf("Target server %s sent unexpected data when no request pending, closing connection", HttpClientConnection.this.connection.getPeerAddress());
/* 556 */               IoUtils.safeClose(HttpClientConnection.this);
/*     */             }
/*     */           
/* 559 */           } catch (IOException e) {
/* 560 */             if (UndertowLogger.CLIENT_LOGGER.isDebugEnabled()) {
/* 561 */               UndertowLogger.CLIENT_LOGGER.debugf(e, "Connection closed with IOException", new Object[0]);
/*     */             }
/* 563 */             IoUtils.safeClose((Closeable)HttpClientConnection.this.connection);
/*     */           } 
/*     */           return;
/*     */         } 
/* 567 */         ResponseParseState state = builder.getParseState();
/*     */         do {
/*     */           int res;
/* 570 */           buffer.clear();
/*     */           try {
/* 572 */             res = channel.read(buffer);
/* 573 */           } catch (IOException e) {
/* 574 */             if (UndertowLogger.CLIENT_LOGGER.isDebugEnabled()) {
/* 575 */               UndertowLogger.CLIENT_LOGGER.debugf(e, "Connection closed with IOException", new Object[0]);
/*     */             }
/*     */             try {
/* 578 */               if (HttpClientConnection.this.currentRequest != null) {
/* 579 */                 HttpClientConnection.this.currentRequest.setFailed(e);
/* 580 */                 HttpClientConnection.this.currentRequest = null;
/*     */               } 
/* 582 */               HttpClientConnection.this.pendingResponse = null;
/*     */             } finally {
/* 584 */               IoUtils.safeClose(new Closeable[] { (Closeable)channel, this.this$0 });
/*     */             } 
/*     */             
/*     */             return;
/*     */           } 
/* 589 */           if (res == 0) {
/* 590 */             if (!channel.isReadResumed()) {
/* 591 */               channel.getReadSetter().set(this);
/* 592 */               channel.resumeReads();
/*     */             }  return;
/*     */           } 
/* 595 */           if (res == -1) {
/* 596 */             channel.suspendReads();
/*     */             
/*     */             try {
/* 599 */               if (HttpClientConnection.this.currentRequest != null) {
/* 600 */                 HttpClientConnection.this.currentRequest.setFailed(new IOException(UndertowClientMessages.MESSAGES.connectionClosed()));
/* 601 */                 HttpClientConnection.this.currentRequest = null;
/*     */               } 
/* 603 */               HttpClientConnection.this.pendingResponse = null;
/*     */             } finally {
/* 605 */               IoUtils.safeClose(HttpClientConnection.this);
/*     */             } 
/*     */             
/*     */             return;
/*     */           } 
/* 610 */           buffer.flip();
/*     */           
/* 612 */           HttpResponseParser.INSTANCE.handle(buffer, state, builder);
/* 613 */           if (!buffer.hasRemaining())
/* 614 */             continue;  free = false;
/* 615 */           HttpClientConnection.this.pushBackStreamSourceConduit.pushBack((Pooled)new PooledAdaptor(pooled));
/* 616 */           HttpClientConnection.this.pushBackStreamSourceConduit.wakeupReads();
/*     */         
/*     */         }
/* 619 */         while (!state.isComplete());
/*     */         
/* 621 */         ClientResponse response = builder.build();
/*     */         
/* 623 */         String connectionString = response.getResponseHeaders().getFirst(Headers.CONNECTION);
/*     */ 
/*     */         
/* 626 */         if (Bits.anyAreSet(HttpClientConnection.this.state, 536870912) && (
/* 627 */           connectionString == null || !Headers.UPGRADE.equalToString(connectionString)) && !response.getResponseHeaders().contains(Headers.UPGRADE) && (
/* 628 */           !HttpClientConnection.this.currentRequest.getRequest().getMethod().equals(Methods.CONNECT) || response.getResponseCode() != 200)) {
/*     */           
/* 630 */           HttpClientConnection httpClientConnection = HttpClientConnection.this; httpClientConnection.state = httpClientConnection.state & 0xDFFFFFFF;
/*     */         } 
/*     */ 
/*     */         
/* 634 */         boolean close = false;
/* 635 */         if (connectionString != null) {
/* 636 */           if (Headers.CLOSE.equalToString(connectionString)) {
/* 637 */             close = true;
/* 638 */           } else if (!response.getProtocol().equals(Protocols.HTTP_1_1) && 
/* 639 */             !Headers.KEEP_ALIVE.equalToString(connectionString)) {
/* 640 */             close = true;
/*     */           }
/*     */         
/* 643 */         } else if (!response.getProtocol().equals(Protocols.HTTP_1_1)) {
/* 644 */           close = true;
/*     */         } 
/* 646 */         if (close) {
/* 647 */           HttpClientConnection httpClientConnection = HttpClientConnection.this; httpClientConnection.state = httpClientConnection.state | 0x40000000;
/*     */           
/* 649 */           HttpClientExchange ex = HttpClientConnection.this.pendingQueue.poll();
/* 650 */           while (ex != null) {
/* 651 */             ex.setFailed(new IOException(UndertowClientMessages.MESSAGES.connectionClosed()));
/* 652 */             ex = HttpClientConnection.this.pendingQueue.poll();
/*     */           } 
/*     */         } 
/* 655 */         if (response.getResponseCode() == 101 && "h2c".equals(response.getResponseHeaders().getFirst(Headers.UPGRADE))) {
/*     */ 
/*     */           
/* 658 */           HttpClientConnection.this.http2UpgradeReceived = true;
/* 659 */           if (HttpClientConnection.this.currentRequest.isRequestDataSent()) {
/* 660 */             HttpClientConnection.this.doHttp2Upgrade();
/*     */           }
/* 662 */         } else if (builder.getStatusCode() == 100) {
/* 663 */           HttpClientConnection.this.pendingResponse = new HttpResponseBuilder();
/* 664 */           HttpClientConnection.this.currentRequest.setContinueResponse(response);
/*     */         } else {
/* 666 */           HttpClientConnection.this.prepareResponseChannel(response, HttpClientConnection.this.currentRequest);
/* 667 */           channel.getReadSetter().set(null);
/* 668 */           channel.suspendReads();
/* 669 */           HttpClientConnection.this.pendingResponse = null;
/* 670 */           HttpClientConnection.this.currentRequest.setResponse(response);
/* 671 */           if (response.getResponseCode() == 417 && 
/* 672 */             HttpContinue.requiresContinueResponse(HttpClientConnection.this.currentRequest.getRequest().getRequestHeaders())) {
/* 673 */             HttpClientConnection httpClientConnection = HttpClientConnection.this; httpClientConnection.state = httpClientConnection.state | 0x40000000;
/* 674 */             ConduitStreamSinkChannel sinkChannel = HttpClientConnection.this.connection.getSinkChannel();
/* 675 */             sinkChannel.shutdownWrites();
/* 676 */             if (!sinkChannel.flush()) {
/* 677 */               sinkChannel.setWriteListener(ChannelListeners.flushingChannelListener(null, null));
/* 678 */               sinkChannel.resumeWrites();
/*     */             } 
/* 680 */             if (HttpClientConnection.this.currentRequest != null)
/*     */             {
/* 682 */               HttpClientConnection.this.currentRequest.terminateRequest();
/*     */             }
/*     */           }
/*     */         
/*     */         }
/*     */       
/*     */       }
/* 689 */       catch (Throwable t) {
/* 690 */         UndertowLogger.CLIENT_LOGGER.exceptionProcessingRequest(t);
/* 691 */         IoUtils.safeClose((Closeable)HttpClientConnection.this.connection);
/* 692 */         if (HttpClientConnection.this.currentRequest != null) {
/* 693 */           HttpClientConnection.this.currentRequest.setFailed(new IOException(t));
/*     */         }
/*     */       } finally {
/* 696 */         if (free) {
/* 697 */           pooled.close();
/* 698 */           HttpClientConnection.this.pooledBuffer = null;
/*     */         } else {
/* 700 */           HttpClientConnection.this.pooledBuffer = pooled;
/*     */         } 
/*     */       } 
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected void doHttp2Upgrade() {
/*     */     try {
/* 710 */       StreamConnection connectedStreamChannel = performUpgrade();
/* 711 */       Http2Channel http2Channel = new Http2Channel(connectedStreamChannel, null, this.bufferPool, null, true, true, this.options);
/* 712 */       Http2ClientConnection http2ClientConnection = new Http2ClientConnection(http2Channel, this.currentRequest.getResponseCallback(), this.currentRequest.getRequest(), this.currentRequest.getRequest().getRequestHeaders().getFirst(Headers.HOST), this.clientStatistics, false);
/* 713 */       http2ClientConnection.getCloseSetter().set(new ChannelListener<ClientConnection>()
/*     */           {
/*     */             public void handleEvent(ClientConnection channel) {
/* 716 */               ChannelListeners.invokeChannelListener((Channel)HttpClientConnection.this, HttpClientConnection.this.closeSetter.get());
/*     */             }
/*     */           });
/* 719 */       this.http2Delegate = (ClientConnection)http2ClientConnection;
/* 720 */       connectedStreamChannel.getSourceChannel().wakeupReads();
/* 721 */       this.currentRequest = null;
/* 722 */       this.pendingResponse = null;
/* 723 */     } catch (IOException e) {
/* 724 */       UndertowLogger.REQUEST_IO_LOGGER.ioException(e);
/* 725 */       IoUtils.safeClose(this);
/*     */     } 
/*     */   }
/*     */   
/*     */   private void prepareResponseChannel(ClientResponse response, ClientExchange exchange) {
/* 730 */     String encoding = response.getResponseHeaders().getLast(Headers.TRANSFER_ENCODING);
/* 731 */     boolean chunked = (encoding != null && Headers.CHUNKED.equals(new HttpString(encoding)));
/* 732 */     String length = response.getResponseHeaders().getFirst(Headers.CONTENT_LENGTH);
/* 733 */     if (exchange.getRequest().getMethod().equals(Methods.HEAD)) {
/* 734 */       this.connection.getSourceChannel().setConduit((StreamSourceConduit)new FixedLengthStreamSourceConduit(this.connection.getSourceChannel().getConduit(), 0L, this.responseFinishedListener));
/* 735 */     } else if (chunked) {
/* 736 */       this.connection.getSourceChannel().setConduit((StreamSourceConduit)new ChunkedStreamSourceConduit(this.connection.getSourceChannel().getConduit(), this.pushBackStreamSourceConduit, this.bufferPool, this.responseFinishedListener, (Attachable)exchange, (Closeable)this.connection));
/* 737 */     } else if (length != null) {
/*     */       try {
/* 739 */         long contentLength = Long.parseLong(length);
/* 740 */         this.connection.getSourceChannel().setConduit((StreamSourceConduit)new FixedLengthStreamSourceConduit(this.connection.getSourceChannel().getConduit(), contentLength, this.responseFinishedListener));
/* 741 */       } catch (NumberFormatException e) {
/* 742 */         handleError(e);
/* 743 */         throw e;
/*     */       } 
/* 745 */     } else if (response.getProtocol().equals(Protocols.HTTP_1_1) && !Connectors.isEntityBodyAllowed(response.getResponseCode())) {
/* 746 */       this.connection.getSourceChannel().setConduit((StreamSourceConduit)new FixedLengthStreamSourceConduit(this.connection.getSourceChannel().getConduit(), 0L, this.responseFinishedListener));
/*     */     } else {
/* 748 */       this.connection.getSourceChannel().setConduit((StreamSourceConduit)new FinishableStreamSourceConduit(this.connection.getSourceChannel().getConduit(), this.responseFinishedListener));
/* 749 */       this.state |= 0x40000000;
/*     */     } 
/*     */   }
/*     */   
/*     */   private class ClientStatisticsImpl implements ClientStatistics {
/*     */     private ClientStatisticsImpl() {}
/*     */     
/*     */     public long getRequests() {
/* 757 */       return HttpClientConnection.this.requestCount;
/*     */     }
/*     */ 
/*     */     
/*     */     public long getRead() {
/* 762 */       return HttpClientConnection.this.read;
/*     */     }
/*     */ 
/*     */     
/*     */     public long getWritten() {
/* 767 */       return HttpClientConnection.this.written;
/*     */     }
/*     */ 
/*     */     
/*     */     public void reset() {
/* 772 */       HttpClientConnection.this.read = 0;
/* 773 */       HttpClientConnection.this.written = 0;
/* 774 */       HttpClientConnection.this.requestCount = 0;
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\client\http\HttpClientConnection.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */