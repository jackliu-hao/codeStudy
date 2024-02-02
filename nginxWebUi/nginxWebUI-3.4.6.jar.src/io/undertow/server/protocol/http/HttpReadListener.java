/*     */ package io.undertow.server.protocol.http;
/*     */ 
/*     */ import io.undertow.UndertowLogger;
/*     */ import io.undertow.UndertowMessages;
/*     */ import io.undertow.UndertowOptions;
/*     */ import io.undertow.conduits.ReadDataStreamSourceConduit;
/*     */ import io.undertow.connector.PooledByteBuffer;
/*     */ import io.undertow.protocols.http2.Http2Channel;
/*     */ import io.undertow.server.ConnectorStatisticsImpl;
/*     */ import io.undertow.server.Connectors;
/*     */ import io.undertow.server.HttpServerExchange;
/*     */ import io.undertow.server.ServerConnection;
/*     */ import io.undertow.server.protocol.ParseTimeoutUpdater;
/*     */ import io.undertow.server.protocol.http2.Http2ReceiveListener;
/*     */ import io.undertow.util.ClosingChannelExceptionHandler;
/*     */ import io.undertow.util.ConnectionUtils;
/*     */ import io.undertow.util.HeaderValues;
/*     */ import io.undertow.util.Headers;
/*     */ import io.undertow.util.HttpString;
/*     */ import io.undertow.util.Methods;
/*     */ import io.undertow.util.Protocols;
/*     */ import io.undertow.util.StringWriteChannelListener;
/*     */ import java.io.Closeable;
/*     */ import java.io.IOException;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.channels.Channel;
/*     */ import java.util.concurrent.Executor;
/*     */ import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
/*     */ import org.xnio.ChannelExceptionHandler;
/*     */ import org.xnio.ChannelListener;
/*     */ import org.xnio.ChannelListeners;
/*     */ import org.xnio.IoUtils;
/*     */ import org.xnio.StreamConnection;
/*     */ import org.xnio.XnioWorker;
/*     */ import org.xnio.channels.ConnectedChannel;
/*     */ import org.xnio.channels.StreamSinkChannel;
/*     */ import org.xnio.channels.StreamSourceChannel;
/*     */ import org.xnio.conduits.ConduitStreamSinkChannel;
/*     */ import org.xnio.conduits.ConduitStreamSourceChannel;
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
/*     */ final class HttpReadListener
/*     */   implements ChannelListener<ConduitStreamSourceChannel>, Runnable
/*     */ {
/*  64 */   private static final HttpString PRI = new HttpString("PRI");
/*  65 */   private static final byte[] PRI_EXPECTED = new byte[] { 83, 77, 13, 10, 13, 10 };
/*     */   
/*     */   private static final String BAD_REQUEST = "HTTP/1.1 400 Bad Request\r\nContent-Length: 0\r\nConnection: close\r\n\r\n";
/*     */   
/*     */   private final HttpServerConnection connection;
/*     */   
/*     */   private final ParseState state;
/*     */   
/*     */   private final HttpRequestParser parser;
/*     */   
/*     */   private HttpServerExchange httpServerExchange;
/*  76 */   private int read = 0;
/*     */   
/*     */   private final int maxRequestSize;
/*     */   
/*     */   private final long maxEntitySize;
/*     */   
/*     */   private final boolean recordRequestStartTime;
/*     */   
/*     */   private final boolean allowUnknownProtocols;
/*     */   
/*     */   private final boolean requireHostHeader;
/*     */   private volatile int requestState;
/*  88 */   private static final AtomicIntegerFieldUpdater<HttpReadListener> requestStateUpdater = AtomicIntegerFieldUpdater.newUpdater(HttpReadListener.class, "requestState");
/*     */   
/*     */   private final ConnectorStatisticsImpl connectorStatistics;
/*     */   
/*     */   private ParseTimeoutUpdater parseTimeoutUpdater;
/*     */   
/*     */   HttpReadListener(HttpServerConnection connection, HttpRequestParser parser, ConnectorStatisticsImpl connectorStatistics) {
/*  95 */     this.connection = connection;
/*  96 */     this.parser = parser;
/*  97 */     this.connectorStatistics = connectorStatistics;
/*  98 */     this.maxRequestSize = connection.getUndertowOptions().get(UndertowOptions.MAX_HEADER_SIZE, 1048576);
/*  99 */     this.maxEntitySize = connection.getUndertowOptions().get(UndertowOptions.MAX_ENTITY_SIZE, -1L);
/* 100 */     this.recordRequestStartTime = connection.getUndertowOptions().get(UndertowOptions.RECORD_REQUEST_START_TIME, false);
/* 101 */     this.requireHostHeader = connection.getUndertowOptions().get(UndertowOptions.REQUIRE_HOST_HTTP11, true);
/* 102 */     this.allowUnknownProtocols = connection.getUndertowOptions().get(UndertowOptions.ALLOW_UNKNOWN_PROTOCOLS, false);
/* 103 */     int requestParseTimeout = connection.getUndertowOptions().get(UndertowOptions.REQUEST_PARSE_TIMEOUT, -1);
/* 104 */     int requestIdleTimeout = connection.getUndertowOptions().get(UndertowOptions.NO_REQUEST_TIMEOUT, -1);
/* 105 */     if (requestIdleTimeout < 0 && requestParseTimeout < 0) {
/* 106 */       this.parseTimeoutUpdater = null;
/*     */     } else {
/* 108 */       this.parseTimeoutUpdater = new ParseTimeoutUpdater((ConnectedChannel)connection, requestParseTimeout, requestIdleTimeout);
/* 109 */       connection.addCloseListener((ServerConnection.CloseListener)this.parseTimeoutUpdater);
/*     */     } 
/* 111 */     this.state = new ParseState(connection.getUndertowOptions().get(UndertowOptions.HTTP_HEADERS_CACHE_SIZE, 15));
/*     */   }
/*     */   
/*     */   public void newRequest() {
/* 115 */     this.state.reset();
/* 116 */     this.read = 0;
/* 117 */     if (this.parseTimeoutUpdater != null) {
/* 118 */       this.parseTimeoutUpdater.connectionIdle();
/*     */     }
/* 120 */     this.connection.setCurrentExchange((HttpServerExchange)null);
/*     */   }
/*     */   
/*     */   public void handleEvent(ConduitStreamSourceChannel channel) {
/* 124 */     while (requestStateUpdater.get(this) != 0) {
/*     */ 
/*     */       
/* 127 */       if (requestStateUpdater.compareAndSet(this, 1, 2)) {
/*     */         try {
/* 129 */           channel.suspendReads();
/*     */         } finally {
/* 131 */           requestStateUpdater.set(this, 1);
/*     */         } 
/*     */         return;
/*     */       } 
/*     */     } 
/* 136 */     handleEventWithNoRunningRequest(channel);
/*     */   }
/*     */   
/*     */   public void handleEventWithNoRunningRequest(ConduitStreamSourceChannel channel) {
/* 140 */     PooledByteBuffer existing = this.connection.getExtraBytes();
/* 141 */     if ((existing == null && this.connection.getOriginalSourceConduit().isReadShutdown()) || this.connection.getOriginalSinkConduit().isWriteShutdown()) {
/* 142 */       IoUtils.safeClose((Closeable)this.connection);
/* 143 */       channel.suspendReads();
/*     */       
/*     */       return;
/*     */     } 
/* 147 */     PooledByteBuffer pooled = (existing == null) ? this.connection.getByteBufferPool().allocate() : existing;
/* 148 */     ByteBuffer buffer = pooled.getBuffer();
/* 149 */     boolean free = true;
/*     */ 
/*     */     
/*     */     try {
/* 153 */       boolean bytesRead = false; while (true)
/*     */       { int res;
/* 155 */         if (existing == null) {
/* 156 */           buffer.clear();
/*     */           try {
/* 158 */             res = channel.read(buffer);
/* 159 */           } catch (IOException e) {
/* 160 */             UndertowLogger.REQUEST_IO_LOGGER.debug("Error reading request", e);
/* 161 */             IoUtils.safeClose((Closeable)this.connection);
/*     */             return;
/*     */           } 
/*     */         } else {
/* 165 */           res = buffer.remaining();
/*     */         } 
/*     */         
/* 168 */         if (res <= 0) {
/* 169 */           if (bytesRead && this.parseTimeoutUpdater != null) {
/* 170 */             this.parseTimeoutUpdater.failedParse();
/*     */           }
/* 172 */           handleFailedRead(channel, res);
/*     */           return;
/*     */         } 
/* 175 */         bytesRead = true;
/*     */         
/* 177 */         if (existing != null) {
/* 178 */           existing = null;
/* 179 */           this.connection.setExtraBytes(null);
/*     */         } else {
/* 181 */           buffer.flip();
/*     */         } 
/* 183 */         int begin = buffer.remaining();
/* 184 */         if (this.httpServerExchange == null) {
/* 185 */           this.httpServerExchange = new HttpServerExchange((ServerConnection)this.connection, this.maxEntitySize);
/*     */         }
/* 187 */         this.parser.handle(buffer, this.state, this.httpServerExchange);
/* 188 */         if (buffer.hasRemaining()) {
/* 189 */           free = false;
/* 190 */           this.connection.setExtraBytes(pooled);
/*     */         } 
/* 192 */         int total = this.read + begin - buffer.remaining();
/* 193 */         this.read = total;
/* 194 */         if (this.read > this.maxRequestSize) {
/* 195 */           UndertowLogger.REQUEST_LOGGER.requestHeaderWasTooLarge(this.connection.getPeerAddress(), this.maxRequestSize);
/* 196 */           sendBadRequestAndClose(this.connection.getChannel(), null);
/*     */           return;
/*     */         } 
/* 199 */         if (this.state.isComplete())
/* 200 */         { if (this.parseTimeoutUpdater != null) {
/* 201 */             this.parseTimeoutUpdater.requestStarted();
/*     */           }
/* 203 */           this.connection.getOriginalSourceConduit().suspendReads();
/*     */           
/* 205 */           HttpServerExchange httpServerExchange = this.httpServerExchange;
/* 206 */           httpServerExchange.setRequestScheme((this.connection.getSslSession() != null) ? "https" : "http");
/* 207 */           this.httpServerExchange = null;
/* 208 */           requestStateUpdater.set(this, 1);
/*     */           
/* 210 */           if (this.recordRequestStartTime) {
/* 211 */             Connectors.setRequestStartTime(httpServerExchange);
/*     */           }
/*     */           
/* 214 */           if (httpServerExchange.getProtocol() == Protocols.HTTP_2_0) {
/* 215 */             free = handleHttp2PriorKnowledge(pooled, httpServerExchange);
/*     */             
/*     */             return;
/*     */           } 
/* 219 */           if (!this.allowUnknownProtocols) {
/* 220 */             HttpString protocol = httpServerExchange.getProtocol();
/* 221 */             if (protocol != Protocols.HTTP_1_1 && protocol != Protocols.HTTP_1_0 && protocol != Protocols.HTTP_0_9) {
/* 222 */               UndertowLogger.REQUEST_IO_LOGGER.debugf("Closing connection from %s due to unknown protocol %s", this.connection.getChannel().getPeerAddress(), protocol);
/* 223 */               sendBadRequestAndClose(this.connection.getChannel(), new IOException());
/*     */               return;
/*     */             } 
/*     */           } 
/* 227 */           HttpTransferEncoding.setupRequest(httpServerExchange);
/* 228 */           this.connection.setCurrentExchange(httpServerExchange);
/* 229 */           if (this.connectorStatistics != null) {
/* 230 */             this.connectorStatistics.setup(httpServerExchange);
/*     */           }
/* 232 */           if (this.connection.getSslSession() != null)
/*     */           {
/*     */ 
/*     */ 
/*     */ 
/*     */             
/* 238 */             channel.suspendReads();
/*     */           }
/*     */           
/* 241 */           HeaderValues host = httpServerExchange.getRequestHeaders().get(Headers.HOST);
/* 242 */           if (host != null && host.size() > 1) {
/* 243 */             sendBadRequestAndClose(this.connection.getChannel(), UndertowMessages.MESSAGES.moreThanOneHostHeader());
/*     */             return;
/*     */           } 
/* 246 */           if (this.requireHostHeader && httpServerExchange.getProtocol().equals(Protocols.HTTP_1_1) && (
/* 247 */             host == null || host.size() == 0 || host.getFirst().isEmpty())) {
/* 248 */             sendBadRequestAndClose(this.connection.getChannel(), UndertowMessages.MESSAGES.noHostInHttp11Request());
/*     */             
/*     */             return;
/*     */           } 
/* 252 */           if (!Connectors.areRequestHeadersValid(httpServerExchange.getRequestHeaders())) {
/* 253 */             sendBadRequestAndClose(this.connection.getChannel(), UndertowMessages.MESSAGES.invalidHeaders());
/*     */             return;
/*     */           } 
/* 256 */           Connectors.executeRootHandler(this.connection.getRootHandler(), httpServerExchange); return; }  } 
/* 257 */     } catch (Throwable t) {
/* 258 */       sendBadRequestAndClose(this.connection.getChannel(), t);
/*     */       return;
/*     */     } finally {
/* 261 */       if (free) pooled.close(); 
/*     */     } 
/*     */   }
/*     */   
/*     */   private boolean handleHttp2PriorKnowledge(PooledByteBuffer pooled, HttpServerExchange httpServerExchange) throws IOException {
/* 266 */     if (httpServerExchange.getRequestMethod().equals(PRI) && this.connection.getUndertowOptions().get(UndertowOptions.ENABLE_HTTP2, false)) {
/* 267 */       handleHttp2PriorKnowledge(this.connection.getChannel(), this.connection, pooled);
/* 268 */       return false;
/*     */     } 
/* 270 */     sendBadRequestAndClose(this.connection.getChannel(), new IOException());
/* 271 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   private void handleFailedRead(ConduitStreamSourceChannel channel, int res) {
/* 276 */     if (res == 0) {
/* 277 */       channel.setReadListener(this);
/* 278 */       channel.resumeReads();
/* 279 */     } else if (res == -1) {
/* 280 */       IoUtils.safeClose((Closeable)this.connection);
/*     */     } 
/*     */   }
/*     */   
/*     */   private void sendBadRequestAndClose(final StreamConnection connection, Throwable exception) {
/* 285 */     UndertowLogger.REQUEST_IO_LOGGER.failedToParseRequest(exception);
/* 286 */     connection.getSourceChannel().suspendReads();
/* 287 */     (new StringWriteChannelListener("HTTP/1.1 400 Bad Request\r\nContent-Length: 0\r\nConnection: close\r\n\r\n")
/*     */       {
/*     */         protected void writeDone(StreamSinkChannel c) {
/* 290 */           super.writeDone(c);
/* 291 */           c.suspendWrites();
/* 292 */           IoUtils.safeClose((Closeable)connection);
/*     */         }
/*     */ 
/*     */         
/*     */         protected void handleError(StreamSinkChannel channel, IOException e) {
/* 297 */           IoUtils.safeClose((Closeable)connection);
/*     */         }
/* 299 */       }).setup((StreamSinkChannel)connection.getSinkChannel());
/*     */   }
/*     */   
/*     */   public void exchangeComplete(final HttpServerExchange exchange) {
/* 303 */     this.connection.clearChannel();
/* 304 */     this.connection.setCurrentExchange((HttpServerExchange)null);
/* 305 */     final HttpServerConnection connection = this.connection;
/* 306 */     if (exchange.isPersistent() && !isUpgradeOrConnect(exchange)) {
/* 307 */       StreamConnection channel = connection.getChannel();
/* 308 */       if (connection.getExtraBytes() == null) {
/*     */ 
/*     */         
/* 311 */         if (exchange.isInIoThread()) {
/*     */           
/* 313 */           newRequest();
/* 314 */           channel.getSourceChannel().setReadListener(this);
/* 315 */           channel.getSourceChannel().resumeReads();
/* 316 */           requestStateUpdater.set(this, 0);
/*     */         } else {
/*     */           do {
/* 319 */             if (connection.getOriginalSourceConduit().isReadShutdown() || connection.getOriginalSinkConduit().isWriteShutdown()) {
/* 320 */               channel.getSourceChannel().suspendReads();
/* 321 */               channel.getSinkChannel().suspendWrites();
/* 322 */               IoUtils.safeClose((Closeable)connection);
/*     */               return;
/*     */             } 
/* 325 */           } while (!requestStateUpdater.compareAndSet(this, 1, 2));
/*     */           try {
/* 327 */             newRequest();
/* 328 */             channel.getSourceChannel().setReadListener(this);
/* 329 */             channel.getSourceChannel().resumeReads();
/*     */           } finally {
/* 331 */             requestStateUpdater.set(this, 0);
/*     */           
/*     */           }
/*     */ 
/*     */         
/*     */         }
/*     */       
/*     */       }
/* 339 */       else if (exchange.isInIoThread()) {
/* 340 */         requestStateUpdater.set(this, 0);
/* 341 */         newRequest();
/*     */         
/* 343 */         channel.getIoThread().execute(this);
/*     */       } else {
/*     */         XnioWorker xnioWorker; do {
/* 346 */           if (connection.getOriginalSinkConduit().isWriteShutdown()) {
/* 347 */             channel.getSourceChannel().suspendReads();
/* 348 */             channel.getSinkChannel().suspendWrites();
/* 349 */             IoUtils.safeClose((Closeable)connection); return;
/*     */           } 
/* 351 */         } while (!requestStateUpdater.compareAndSet(this, 1, 2));
/*     */         try {
/* 353 */           newRequest();
/* 354 */           channel.getSourceChannel().suspendReads();
/*     */         } finally {
/* 356 */           requestStateUpdater.set(this, 0);
/*     */         } 
/*     */ 
/*     */ 
/*     */         
/* 361 */         Executor executor = exchange.getDispatchExecutor();
/* 362 */         if (executor == null) {
/* 363 */           xnioWorker = exchange.getConnection().getWorker();
/*     */         }
/* 365 */         xnioWorker.execute(this);
/*     */       }
/*     */     
/* 368 */     } else if (!exchange.isPersistent()) {
/* 369 */       if (connection.getExtraBytes() != null) {
/* 370 */         connection.getExtraBytes().close();
/* 371 */         connection.setExtraBytes(null);
/*     */       } 
/* 373 */       ConnectionUtils.cleanClose(connection.getChannel(), new Closeable[] { (Closeable)connection });
/*     */     } else {
/*     */       
/* 376 */       if (connection.getExtraBytes() != null) {
/* 377 */         connection.getChannel().getSourceChannel().setConduit((StreamSourceConduit)new ReadDataStreamSourceConduit(connection.getChannel().getSourceChannel().getConduit(), connection));
/*     */       }
/*     */       try {
/* 380 */         if (!connection.getChannel().getSinkChannel().flush()) {
/* 381 */           connection.getChannel().getSinkChannel().setWriteListener(ChannelListeners.flushingChannelListener(new ChannelListener<ConduitStreamSinkChannel>()
/*     */                 {
/*     */                   public void handleEvent(ConduitStreamSinkChannel conduitStreamSinkChannel) {
/* 384 */                     connection.getUpgradeListener().handleUpgrade(connection.getChannel(), exchange);
/*     */                   }
/*     */                 }(ChannelExceptionHandler)new ClosingChannelExceptionHandler(new Closeable[] { (Closeable)connection })));
/* 387 */           connection.getChannel().getSinkChannel().resumeWrites();
/*     */           return;
/*     */         } 
/* 390 */         connection.getUpgradeListener().handleUpgrade(connection.getChannel(), exchange);
/* 391 */       } catch (IOException e) {
/* 392 */         UndertowLogger.REQUEST_IO_LOGGER.ioException(e);
/* 393 */         IoUtils.safeClose((Closeable)connection);
/* 394 */       } catch (Throwable t) {
/* 395 */         UndertowLogger.REQUEST_IO_LOGGER.handleUnexpectedFailure(t);
/* 396 */         IoUtils.safeClose((Closeable)connection);
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   private boolean isUpgradeOrConnect(HttpServerExchange exchange) {
/* 402 */     return (exchange.isUpgrade() || (exchange.getRequestMethod().equals(Methods.CONNECT) && ((HttpServerConnection)exchange.getConnection()).isConnectHandled()));
/*     */   }
/*     */ 
/*     */   
/*     */   public void run() {
/* 407 */     handleEvent(this.connection.getChannel().getSourceChannel());
/*     */   }
/*     */ 
/*     */   
/*     */   private void handleHttp2PriorKnowledge(final StreamConnection connection, final HttpServerConnection serverConnection, PooledByteBuffer readData) throws IOException {
/*     */     final PooledByteBuffer extraData;
/* 413 */     ConduitStreamSourceChannel request = connection.getSourceChannel();
/*     */     
/* 415 */     byte[] data = new byte[PRI_EXPECTED.length];
/* 416 */     final ByteBuffer buffer = ByteBuffer.wrap(data);
/* 417 */     if (readData.getBuffer().hasRemaining()) {
/* 418 */       while (readData.getBuffer().hasRemaining() && buffer.hasRemaining()) {
/* 419 */         buffer.put(readData.getBuffer().get());
/*     */       }
/*     */     }
/*     */     
/* 423 */     if (readData.getBuffer().hasRemaining()) {
/* 424 */       extraData = readData;
/*     */     } else {
/* 426 */       readData.close();
/* 427 */       extraData = null;
/*     */     } 
/* 429 */     if (!doHttp2PriRead(connection, buffer, serverConnection, extraData)) {
/* 430 */       request.getReadSetter().set(new ChannelListener<StreamSourceChannel>()
/*     */           {
/*     */             public void handleEvent(StreamSourceChannel channel) {
/*     */               try {
/* 434 */                 HttpReadListener.this.doHttp2PriRead(connection, buffer, serverConnection, extraData);
/* 435 */               } catch (IOException e) {
/* 436 */                 UndertowLogger.REQUEST_IO_LOGGER.ioException(e);
/* 437 */                 IoUtils.safeClose((Closeable)connection);
/* 438 */               } catch (Throwable t) {
/* 439 */                 UndertowLogger.REQUEST_IO_LOGGER.handleUnexpectedFailure(t);
/* 440 */                 IoUtils.safeClose((Closeable)connection);
/*     */               } 
/*     */             }
/*     */           });
/* 444 */       request.resumeReads();
/*     */     } 
/*     */   }
/*     */   
/*     */   private boolean doHttp2PriRead(StreamConnection connection, ByteBuffer buffer, HttpServerConnection serverConnection, PooledByteBuffer extraData) throws IOException {
/* 449 */     if (buffer.hasRemaining()) {
/* 450 */       int res = connection.getSourceChannel().read(buffer);
/* 451 */       if (res == -1) {
/* 452 */         return true;
/*     */       }
/* 454 */       if (buffer.hasRemaining()) {
/* 455 */         return false;
/*     */       }
/*     */     } 
/* 458 */     buffer.flip();
/* 459 */     for (int i = 0; i < PRI_EXPECTED.length; i++) {
/* 460 */       if (buffer.get() != PRI_EXPECTED[i]) {
/* 461 */         throw UndertowMessages.MESSAGES.http2PriRequestFailed();
/*     */       }
/*     */     } 
/*     */     
/* 465 */     Http2Channel channel = new Http2Channel(connection, null, serverConnection.getByteBufferPool(), extraData, false, false, false, serverConnection.getUndertowOptions());
/* 466 */     Http2ReceiveListener receiveListener = new Http2ReceiveListener(serverConnection.getRootHandler(), serverConnection.getUndertowOptions(), serverConnection.getBufferSize(), null);
/* 467 */     channel.getReceiveSetter().set((ChannelListener)receiveListener);
/* 468 */     channel.resumeReceives();
/* 469 */     return true;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\protocol\http\HttpReadListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */