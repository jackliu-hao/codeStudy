/*     */ package io.undertow.server.protocol.ajp;
/*     */ 
/*     */ import io.undertow.UndertowLogger;
/*     */ import io.undertow.UndertowOptions;
/*     */ import io.undertow.conduits.ConduitListener;
/*     */ import io.undertow.conduits.EmptyStreamSourceConduit;
/*     */ import io.undertow.conduits.ReadDataStreamSourceConduit;
/*     */ import io.undertow.connector.PooledByteBuffer;
/*     */ import io.undertow.server.AbstractServerConnection;
/*     */ import io.undertow.server.ConnectorStatisticsImpl;
/*     */ import io.undertow.server.Connectors;
/*     */ import io.undertow.server.HttpServerExchange;
/*     */ import io.undertow.server.ServerConnection;
/*     */ import io.undertow.server.protocol.ParseTimeoutUpdater;
/*     */ import io.undertow.util.BadRequestException;
/*     */ import io.undertow.util.HeaderMap;
/*     */ import io.undertow.util.Headers;
/*     */ import io.undertow.util.HttpString;
/*     */ import io.undertow.util.Methods;
/*     */ import java.io.Closeable;
/*     */ import java.io.IOException;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.channels.Channel;
/*     */ import org.xnio.ChannelListener;
/*     */ import org.xnio.IoUtils;
/*     */ import org.xnio.StreamConnection;
/*     */ import org.xnio.channels.ConnectedChannel;
/*     */ import org.xnio.channels.StreamSourceChannel;
/*     */ import org.xnio.channels.SuspendableWriteChannel;
/*     */ import org.xnio.conduits.Conduit;
/*     */ import org.xnio.conduits.ConduitStreamSinkChannel;
/*     */ import org.xnio.conduits.ConduitStreamSourceChannel;
/*     */ import org.xnio.conduits.StreamSinkConduit;
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
/*     */ final class AjpReadListener
/*     */   implements ChannelListener<StreamSourceChannel>
/*     */ {
/*  58 */   private static final byte[] CPONG = new byte[] { 65, 66, 0, 1, 9 };
/*  59 */   private static final byte[] SEND_HEADERS_INTERNAL_SERVER_ERROR_MSG = new byte[] { 65, 66, 0, 8, 4, 1, -12, 0, 0, 0, 0, 0 };
/*  60 */   private static final byte[] SEND_HEADERS_BAD_REQUEST_MSG = new byte[] { 65, 66, 0, 8, 4, 1, -112, 0, 0, 0, 0, 0 };
/*  61 */   private static final byte[] END_RESPONSE = new byte[] { 65, 66, 0, 2, 5, 1 };
/*     */   
/*     */   private final AjpServerConnection connection;
/*     */   private final String scheme;
/*     */   private final boolean recordRequestStartTime;
/*  66 */   private AjpRequestParseState state = new AjpRequestParseState();
/*     */   
/*     */   private HttpServerExchange httpServerExchange;
/*  69 */   private volatile int read = 0;
/*     */   
/*     */   private final int maxRequestSize;
/*     */   private final long maxEntitySize;
/*     */   private final AjpRequestParser parser;
/*     */   private final ConnectorStatisticsImpl connectorStatistics;
/*     */   private WriteReadyHandler.ChannelListenerHandler<ConduitStreamSinkChannel> writeReadyHandler;
/*     */   private ParseTimeoutUpdater parseTimeoutUpdater;
/*     */   
/*     */   AjpReadListener(AjpServerConnection connection, String scheme, AjpRequestParser parser, ConnectorStatisticsImpl connectorStatistics) {
/*  79 */     this.connection = connection;
/*  80 */     this.scheme = scheme;
/*  81 */     this.parser = parser;
/*  82 */     this.connectorStatistics = connectorStatistics;
/*  83 */     this.maxRequestSize = connection.getUndertowOptions().get(UndertowOptions.MAX_HEADER_SIZE, 1048576);
/*  84 */     this.maxEntitySize = connection.getUndertowOptions().get(UndertowOptions.MAX_ENTITY_SIZE, -1L);
/*  85 */     this.writeReadyHandler = new WriteReadyHandler.ChannelListenerHandler((SuspendableWriteChannel)connection.getChannel().getSinkChannel());
/*  86 */     this.recordRequestStartTime = connection.getUndertowOptions().get(UndertowOptions.RECORD_REQUEST_START_TIME, false);
/*  87 */     int requestParseTimeout = connection.getUndertowOptions().get(UndertowOptions.REQUEST_PARSE_TIMEOUT, -1);
/*  88 */     int requestIdleTimeout = connection.getUndertowOptions().get(UndertowOptions.NO_REQUEST_TIMEOUT, -1);
/*  89 */     if (requestIdleTimeout < 0 && requestParseTimeout < 0) {
/*  90 */       this.parseTimeoutUpdater = null;
/*     */     } else {
/*  92 */       this.parseTimeoutUpdater = new ParseTimeoutUpdater((ConnectedChannel)connection, requestParseTimeout, requestIdleTimeout);
/*  93 */       connection.addCloseListener((ServerConnection.CloseListener)this.parseTimeoutUpdater);
/*     */     } 
/*     */   }
/*     */   
/*     */   public void startRequest() {
/*  98 */     this.connection.resetChannel();
/*  99 */     this.state = new AjpRequestParseState();
/* 100 */     this.read = 0;
/* 101 */     if (this.parseTimeoutUpdater != null) {
/* 102 */       this.parseTimeoutUpdater.connectionIdle();
/*     */     }
/* 104 */     this.connection.setCurrentExchange((HttpServerExchange)null);
/*     */   }
/*     */   
/*     */   public void handleEvent(StreamSourceChannel channel) {
/* 108 */     if (this.connection.getOriginalSinkConduit().isWriteShutdown() || this.connection.getOriginalSourceConduit().isReadShutdown()) {
/* 109 */       IoUtils.safeClose((Closeable)this.connection);
/* 110 */       channel.suspendReads();
/*     */       
/*     */       return;
/*     */     } 
/* 114 */     PooledByteBuffer existing = this.connection.getExtraBytes();
/*     */     
/* 116 */     PooledByteBuffer pooled = (existing == null) ? this.connection.getByteBufferPool().allocate() : existing;
/* 117 */     ByteBuffer buffer = pooled.getBuffer();
/* 118 */     boolean free = true;
/* 119 */     boolean bytesRead = false;
/*     */     try {
/*     */       do {
/*     */         int res;
/* 123 */         if (existing == null) {
/* 124 */           buffer.clear();
/* 125 */           res = channel.read(buffer);
/*     */         } else {
/* 127 */           res = buffer.remaining();
/*     */         } 
/* 129 */         if (res == 0) {
/*     */           
/* 131 */           if (bytesRead && this.parseTimeoutUpdater != null) {
/* 132 */             this.parseTimeoutUpdater.failedParse();
/*     */           }
/* 134 */           if (!channel.isReadResumed()) {
/* 135 */             channel.getReadSetter().set(this);
/* 136 */             channel.resumeReads();
/*     */           } 
/*     */           return;
/*     */         } 
/* 140 */         if (res == -1) {
/* 141 */           channel.shutdownReads();
/* 142 */           ConduitStreamSinkChannel conduitStreamSinkChannel = this.connection.getChannel().getSinkChannel();
/* 143 */           conduitStreamSinkChannel.shutdownWrites();
/* 144 */           IoUtils.safeClose((Closeable)this.connection);
/*     */           return;
/*     */         } 
/* 147 */         bytesRead = true;
/*     */         
/* 149 */         if (existing != null) {
/* 150 */           existing = null;
/* 151 */           this.connection.setExtraBytes(null);
/*     */         } else {
/* 153 */           buffer.flip();
/*     */         } 
/* 155 */         int begin = buffer.remaining();
/* 156 */         if (this.httpServerExchange == null) {
/* 157 */           this.httpServerExchange = new HttpServerExchange((ServerConnection)this.connection, this.maxEntitySize);
/*     */         }
/* 159 */         this.parser.parse(buffer, this.state, this.httpServerExchange);
/*     */         
/* 161 */         this.read += begin - buffer.remaining();
/* 162 */         if (buffer.hasRemaining()) {
/* 163 */           free = false;
/* 164 */           this.connection.setExtraBytes(pooled);
/*     */         } 
/* 166 */         if (this.read > this.maxRequestSize) {
/* 167 */           UndertowLogger.REQUEST_LOGGER.requestHeaderWasTooLarge(this.connection.getPeerAddress(), this.maxRequestSize);
/* 168 */           IoUtils.safeClose((Closeable)this.connection);
/*     */           return;
/*     */         } 
/* 171 */       } while (!this.state.isComplete());
/*     */       
/* 173 */       if (this.parseTimeoutUpdater != null) {
/* 174 */         this.parseTimeoutUpdater.requestStarted();
/*     */       }
/* 176 */       if (this.state.prefix != 2) {
/* 177 */         if (this.state.prefix == 10) {
/* 178 */           UndertowLogger.REQUEST_LOGGER.debug("Received CPING, sending CPONG");
/* 179 */           handleCPing();
/* 180 */         } else if (this.state.prefix == 9) {
/* 181 */           UndertowLogger.REQUEST_LOGGER.debug("Received CPONG, starting next request");
/* 182 */           this.state = new AjpRequestParseState();
/* 183 */           channel.getReadSetter().set(this);
/* 184 */           channel.resumeReads();
/*     */         } else {
/* 186 */           UndertowLogger.REQUEST_LOGGER.ignoringAjpRequestWithPrefixCode(this.state.prefix);
/* 187 */           IoUtils.safeClose((Closeable)this.connection);
/*     */         } 
/*     */ 
/*     */         
/*     */         return;
/*     */       } 
/*     */       
/* 194 */       channel.getReadSetter().set(null);
/* 195 */       channel.suspendReads();
/*     */       
/* 197 */       final HttpServerExchange httpServerExchange = this.httpServerExchange;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 203 */       AjpServerResponseConduit responseConduit = new AjpServerResponseConduit(this.connection.getChannel().getSinkChannel().getConduit(), this.connection.getByteBufferPool(), httpServerExchange, new ConduitListener<AjpServerResponseConduit>() { public void handleEvent(AjpServerResponseConduit channel) { Connectors.terminateResponse(httpServerExchange); } }, httpServerExchange.getRequestMethod().equals(Methods.HEAD));
/* 204 */       this.connection.getChannel().getSinkChannel().setConduit((StreamSinkConduit)responseConduit);
/* 205 */       this.connection.getChannel().getSourceChannel().setConduit(createSourceConduit(this.connection.getChannel().getSourceChannel().getConduit(), responseConduit, httpServerExchange));
/*     */       
/* 207 */       responseConduit.setWriteReadyHandler((WriteReadyHandler)this.writeReadyHandler);
/*     */       
/* 209 */       this.connection.setSSLSessionInfo(this.state.createSslSessionInfo());
/* 210 */       httpServerExchange.setSourceAddress(this.state.createPeerAddress());
/* 211 */       httpServerExchange.setDestinationAddress(this.state.createDestinationAddress());
/* 212 */       if (this.scheme != null) {
/* 213 */         httpServerExchange.setRequestScheme(this.scheme);
/*     */       }
/* 215 */       if (this.state.attributes != null) {
/* 216 */         httpServerExchange.putAttachment(HttpServerExchange.REQUEST_ATTRIBUTES, this.state.attributes);
/*     */       }
/* 218 */       AjpRequestParseState oldState = this.state;
/* 219 */       this.state = null;
/* 220 */       this.httpServerExchange = null;
/* 221 */       httpServerExchange.setPersistent(true);
/*     */       
/* 223 */       if (this.recordRequestStartTime) {
/* 224 */         Connectors.setRequestStartTime(httpServerExchange);
/*     */       }
/* 226 */       this.connection.setCurrentExchange(httpServerExchange);
/* 227 */       if (this.connectorStatistics != null) {
/* 228 */         this.connectorStatistics.setup(httpServerExchange);
/*     */       }
/* 230 */       if (!Connectors.areRequestHeadersValid(httpServerExchange.getRequestHeaders())) {
/* 231 */         oldState.badRequest = true;
/* 232 */         UndertowLogger.REQUEST_IO_LOGGER.debugf("Invalid AJP request from %s, request contained invalid headers", this.connection.getPeerAddress());
/*     */       } 
/*     */       
/* 235 */       if (oldState.badRequest) {
/* 236 */         httpServerExchange.setStatusCode(400);
/* 237 */         httpServerExchange.endExchange();
/* 238 */         handleBadRequest();
/* 239 */         IoUtils.safeClose((Closeable)this.connection);
/*     */       } else {
/* 241 */         Connectors.executeRootHandler(this.connection.getRootHandler(), httpServerExchange);
/*     */       } 
/* 243 */     } catch (BadRequestException e) {
/* 244 */       UndertowLogger.REQUEST_IO_LOGGER.failedToParseRequest((Throwable)e);
/* 245 */       handleBadRequest();
/* 246 */       IoUtils.safeClose((Closeable)this.connection);
/* 247 */     } catch (IOException e) {
/* 248 */       UndertowLogger.REQUEST_IO_LOGGER.ioException(e);
/* 249 */       handleInternalServerError();
/* 250 */       IoUtils.safeClose((Closeable)this.connection);
/* 251 */     } catch (Throwable t) {
/* 252 */       UndertowLogger.REQUEST_LOGGER.exceptionProcessingRequest(t);
/* 253 */       handleInternalServerError();
/* 254 */       IoUtils.safeClose((Closeable)this.connection);
/*     */     } finally {
/* 256 */       if (free) pooled.close(); 
/*     */     } 
/*     */   }
/*     */   
/*     */   private void handleInternalServerError() {
/* 261 */     sendMessages(new byte[][] { SEND_HEADERS_INTERNAL_SERVER_ERROR_MSG, END_RESPONSE });
/*     */   }
/*     */   
/*     */   private void handleBadRequest() {
/* 265 */     sendMessages(new byte[][] { SEND_HEADERS_BAD_REQUEST_MSG, END_RESPONSE });
/*     */   }
/*     */   
/*     */   private void handleCPing() {
/* 269 */     if (sendMessages(new byte[][] { CPONG })) {
/* 270 */       handleEvent((StreamSourceChannel)this.connection.getChannel().getSourceChannel());
/*     */     }
/*     */   }
/*     */   
/*     */   private boolean sendMessages(byte[]... rawMessages) {
/* 275 */     this.state = new AjpRequestParseState();
/* 276 */     final StreamConnection underlyingChannel = this.connection.getChannel();
/* 277 */     underlyingChannel.getSourceChannel().suspendReads();
/*     */     
/* 279 */     int bufferSize = 0;
/* 280 */     for (int i = 0; i < rawMessages.length; i++) {
/* 281 */       bufferSize += (rawMessages[i]).length;
/*     */     }
/*     */     
/* 284 */     final ByteBuffer buffer = ByteBuffer.allocate(bufferSize);
/* 285 */     for (int j = 0; j < rawMessages.length; j++) {
/* 286 */       buffer.put(rawMessages[j]);
/*     */     }
/* 288 */     buffer.flip();
/*     */ 
/*     */     
/*     */     try {
/*     */       while (true)
/* 293 */       { int res = underlyingChannel.getSinkChannel().write(buffer);
/* 294 */         if (res == 0) {
/* 295 */           underlyingChannel.getSinkChannel().setWriteListener(new ChannelListener<ConduitStreamSinkChannel>()
/*     */               {
/*     */                 public void handleEvent(ConduitStreamSinkChannel channel)
/*     */                 {
/*     */                   while (true) {
/*     */                     try {
/* 301 */                       int res = channel.write(buffer);
/* 302 */                       if (res == 0) {
/*     */                         return;
/*     */                       }
/* 305 */                     } catch (IOException e) {
/* 306 */                       UndertowLogger.REQUEST_IO_LOGGER.ioException(e);
/* 307 */                       IoUtils.safeClose((Closeable)AjpReadListener.this.connection);
/*     */                     } 
/* 309 */                     if (!buffer.hasRemaining()) {
/* 310 */                       channel.suspendWrites();
/* 311 */                       AjpReadListener.this.handleEvent((StreamSourceChannel)underlyingChannel.getSourceChannel()); return;
/*     */                     } 
/*     */                   }  } });
/* 314 */           underlyingChannel.getSinkChannel().resumeWrites();
/* 315 */           return false;
/*     */         } 
/* 317 */         if (!buffer.hasRemaining())
/* 318 */           return true;  } 
/* 319 */     } catch (IOException e) {
/* 320 */       UndertowLogger.REQUEST_IO_LOGGER.ioException(e);
/* 321 */       IoUtils.safeClose((Closeable)this.connection);
/* 322 */       return false;
/*     */     } 
/*     */   }
/*     */   
/*     */   public void exchangeComplete(HttpServerExchange exchange) {
/* 327 */     if (!exchange.isUpgrade() && exchange.isPersistent()) {
/* 328 */       startRequest();
/* 329 */       ConduitStreamSourceChannel channel = ((AjpServerConnection)exchange.getConnection()).getChannel().getSourceChannel();
/* 330 */       channel.getReadSetter().set(this);
/* 331 */       channel.wakeupReads();
/* 332 */     } else if (!exchange.isPersistent()) {
/* 333 */       IoUtils.safeClose((Closeable)exchange.getConnection());
/*     */     } 
/*     */   }
/*     */   
/*     */   private StreamSourceConduit createSourceConduit(StreamSourceConduit underlyingConduit, AjpServerResponseConduit responseConduit, final HttpServerExchange exchange) throws BadRequestException {
/*     */     Long length;
/* 339 */     ReadDataStreamSourceConduit conduit = new ReadDataStreamSourceConduit(underlyingConduit, (AbstractServerConnection)exchange.getConnection());
/*     */     
/* 341 */     HeaderMap requestHeaders = exchange.getRequestHeaders();
/* 342 */     HttpString transferEncoding = Headers.IDENTITY;
/*     */     
/* 344 */     String teHeader = requestHeaders.getLast(Headers.TRANSFER_ENCODING);
/* 345 */     boolean hasTransferEncoding = (teHeader != null);
/* 346 */     if (hasTransferEncoding) {
/* 347 */       transferEncoding = new HttpString(teHeader);
/*     */     }
/* 349 */     String requestContentLength = requestHeaders.getFirst(Headers.CONTENT_LENGTH);
/* 350 */     if (hasTransferEncoding && !transferEncoding.equals(Headers.IDENTITY)) {
/* 351 */       length = null;
/* 352 */     } else if (requestContentLength != null) {
/*     */       try {
/* 354 */         long contentLength = Long.parseLong(requestContentLength);
/* 355 */         if (contentLength == 0L) {
/* 356 */           UndertowLogger.REQUEST_LOGGER.trace("No content, starting next request");
/*     */           
/* 358 */           Connectors.terminateRequest(this.httpServerExchange);
/* 359 */           return (StreamSourceConduit)new EmptyStreamSourceConduit(conduit.getReadThread());
/*     */         } 
/* 361 */         length = Long.valueOf(contentLength);
/*     */       }
/* 363 */       catch (NumberFormatException e) {
/* 364 */         throw new BadRequestException("Invalid Content-Length header", e);
/*     */       } 
/*     */     } else {
/* 367 */       UndertowLogger.REQUEST_LOGGER.trace("No content length or transfer coding, starting next request");
/*     */       
/* 369 */       Connectors.terminateRequest(exchange);
/* 370 */       return (StreamSourceConduit)new EmptyStreamSourceConduit(conduit.getReadThread());
/*     */     } 
/* 372 */     return (StreamSourceConduit)new AjpServerRequestConduit((StreamSourceConduit)conduit, exchange, responseConduit, length, new ConduitListener<AjpServerRequestConduit>()
/*     */         {
/*     */           public void handleEvent(AjpServerRequestConduit channel) {
/* 375 */             Connectors.terminateRequest(exchange);
/*     */           }
/*     */         });
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\protocol\ajp\AjpReadListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */