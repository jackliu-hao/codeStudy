/*     */ package io.undertow.client.http2;
/*     */ 
/*     */ import io.undertow.UndertowLogger;
/*     */ import io.undertow.UndertowMessages;
/*     */ import io.undertow.client.ClientCallback;
/*     */ import io.undertow.client.ClientConnection;
/*     */ import io.undertow.client.ClientExchange;
/*     */ import io.undertow.client.ClientRequest;
/*     */ import io.undertow.client.ClientStatistics;
/*     */ import io.undertow.connector.ByteBufferPool;
/*     */ import io.undertow.protocols.http2.AbstractHttp2StreamSinkChannel;
/*     */ import io.undertow.protocols.http2.AbstractHttp2StreamSourceChannel;
/*     */ import io.undertow.protocols.http2.Http2Channel;
/*     */ import io.undertow.protocols.http2.Http2DataStreamSinkChannel;
/*     */ import io.undertow.protocols.http2.Http2HeadersStreamSinkChannel;
/*     */ import io.undertow.protocols.http2.Http2PingStreamSourceChannel;
/*     */ import io.undertow.protocols.http2.Http2PushPromiseStreamSourceChannel;
/*     */ import io.undertow.protocols.http2.Http2RstStreamStreamSourceChannel;
/*     */ import io.undertow.protocols.http2.Http2StreamSinkChannel;
/*     */ import io.undertow.protocols.http2.Http2StreamSourceChannel;
/*     */ import io.undertow.server.protocol.http.HttpAttachments;
/*     */ import io.undertow.util.HeaderMap;
/*     */ import io.undertow.util.HeaderValues;
/*     */ import io.undertow.util.Headers;
/*     */ import io.undertow.util.HttpString;
/*     */ import io.undertow.util.Methods;
/*     */ import io.undertow.util.Protocols;
/*     */ import java.io.Closeable;
/*     */ import java.io.IOException;
/*     */ import java.net.SocketAddress;
/*     */ import java.nio.channels.Channel;
/*     */ import java.nio.channels.ClosedChannelException;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collection;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.concurrent.ConcurrentHashMap;
/*     */ import java.util.concurrent.CopyOnWriteArrayList;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import java.util.concurrent.atomic.AtomicLong;
/*     */ import java.util.function.Supplier;
/*     */ import org.xnio.ChannelExceptionHandler;
/*     */ import org.xnio.ChannelListener;
/*     */ import org.xnio.ChannelListeners;
/*     */ import org.xnio.IoUtils;
/*     */ import org.xnio.Option;
/*     */ import org.xnio.StreamConnection;
/*     */ import org.xnio.XnioIoThread;
/*     */ import org.xnio.XnioWorker;
/*     */ import org.xnio.channels.Channels;
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
/*     */ public class Http2ClientConnection
/*     */   implements ClientConnection
/*     */ {
/*     */   private final Http2Channel http2Channel;
/*  84 */   private final ChannelListener.SimpleSetter<ClientConnection> closeSetter = new ChannelListener.SimpleSetter();
/*     */   
/*  86 */   private final Map<Integer, Http2ClientExchange> currentExchanges = new ConcurrentHashMap<>();
/*     */   
/*  88 */   private static final AtomicLong PING_COUNTER = new AtomicLong();
/*     */   
/*     */   private boolean initialUpgradeRequest;
/*     */   
/*     */   private final String defaultHost;
/*     */   private final ClientStatistics clientStatistics;
/*  94 */   private final List<ChannelListener<ClientConnection>> closeListeners = new CopyOnWriteArrayList<>();
/*     */   
/*     */   private final boolean secure;
/*  97 */   private final Map<PingKey, ClientConnection.PingListener> outstandingPings = new HashMap<>();
/*     */   
/*  99 */   private final ChannelListener<Http2Channel> closeTask = new ChannelListener<Http2Channel>()
/*     */     {
/*     */       public void handleEvent(Http2Channel channel) {
/* 102 */         ChannelListeners.invokeChannelListener((Channel)Http2ClientConnection.this, Http2ClientConnection.this.closeSetter.get());
/* 103 */         for (ChannelListener<ClientConnection> listener : (Iterable<ChannelListener<ClientConnection>>)Http2ClientConnection.this.closeListeners) {
/* 104 */           listener.handleEvent((Channel)Http2ClientConnection.this);
/*     */         }
/* 106 */         for (Map.Entry<Integer, Http2ClientExchange> entry : (Iterable<Map.Entry<Integer, Http2ClientExchange>>)Http2ClientConnection.this.currentExchanges.entrySet()) {
/* 107 */           ((Http2ClientExchange)entry.getValue()).failed(new ClosedChannelException());
/*     */         }
/* 109 */         Http2ClientConnection.this.currentExchanges.clear();
/*     */       }
/*     */     };
/*     */ 
/*     */   
/*     */   public Http2ClientConnection(Http2Channel http2Channel, boolean initialUpgradeRequest, String defaultHost, ClientStatistics clientStatistics, boolean secure) {
/* 115 */     this.http2Channel = http2Channel;
/* 116 */     this.defaultHost = defaultHost;
/* 117 */     this.clientStatistics = clientStatistics;
/* 118 */     this.secure = secure;
/* 119 */     http2Channel.getReceiveSetter().set(new Http2ReceiveListener());
/* 120 */     http2Channel.resumeReceives();
/* 121 */     http2Channel.addCloseTask(this.closeTask);
/* 122 */     this.initialUpgradeRequest = initialUpgradeRequest;
/*     */   }
/*     */ 
/*     */   
/*     */   public Http2ClientConnection(Http2Channel http2Channel, ClientCallback<ClientExchange> upgradeReadyCallback, ClientRequest clientRequest, String defaultHost, ClientStatistics clientStatistics, boolean secure) {
/* 127 */     this.http2Channel = http2Channel;
/* 128 */     this.defaultHost = defaultHost;
/* 129 */     this.clientStatistics = clientStatistics;
/* 130 */     this.secure = secure;
/* 131 */     http2Channel.getReceiveSetter().set(new Http2ReceiveListener());
/* 132 */     http2Channel.resumeReceives();
/* 133 */     http2Channel.addCloseTask(this.closeTask);
/* 134 */     this.initialUpgradeRequest = false;
/*     */     
/* 136 */     Http2ClientExchange exchange = new Http2ClientExchange(this, null, clientRequest);
/* 137 */     exchange.setResponseListener(upgradeReadyCallback);
/* 138 */     this.currentExchanges.put(Integer.valueOf(1), exchange);
/*     */   }
/*     */   
/*     */   public void sendRequest(ClientRequest request, ClientCallback<ClientExchange> clientCallback) {
/*     */     Http2HeadersStreamSinkChannel sinkChannel;
/* 143 */     if (!this.http2Channel.isOpen()) {
/* 144 */       clientCallback.failed(new ClosedChannelException());
/*     */       return;
/*     */     } 
/* 147 */     request.getRequestHeaders().put(Http2Channel.METHOD, request.getMethod().toString());
/* 148 */     boolean connectRequest = request.getMethod().equals(Methods.CONNECT);
/* 149 */     if (!connectRequest) {
/* 150 */       request.getRequestHeaders().put(Http2Channel.PATH, request.getPath());
/* 151 */       request.getRequestHeaders().put(Http2Channel.SCHEME, this.secure ? "https" : "http");
/*     */     } 
/* 153 */     String host = request.getRequestHeaders().getFirst(Headers.HOST);
/* 154 */     if (host != null) {
/* 155 */       request.getRequestHeaders().put(Http2Channel.AUTHORITY, host);
/*     */     } else {
/* 157 */       request.getRequestHeaders().put(Http2Channel.AUTHORITY, this.defaultHost);
/*     */     } 
/* 159 */     request.getRequestHeaders().remove(Headers.HOST);
/*     */ 
/*     */     
/* 162 */     boolean hasContent = true;
/*     */     
/* 164 */     String fixedLengthString = request.getRequestHeaders().getFirst(Headers.CONTENT_LENGTH);
/* 165 */     String transferEncodingString = request.getRequestHeaders().getLast(Headers.TRANSFER_ENCODING);
/* 166 */     if (fixedLengthString != null) {
/*     */       try {
/* 168 */         long length = Long.parseLong(fixedLengthString);
/* 169 */         hasContent = (length != 0L);
/* 170 */       } catch (NumberFormatException e) {
/* 171 */         handleError(new IOException(e));
/*     */         return;
/*     */       } 
/* 174 */     } else if (transferEncodingString == null && !connectRequest) {
/* 175 */       hasContent = false;
/*     */     } 
/*     */     
/* 178 */     request.getRequestHeaders().remove(Headers.CONNECTION);
/* 179 */     request.getRequestHeaders().remove(Headers.KEEP_ALIVE);
/* 180 */     request.getRequestHeaders().remove(Headers.TRANSFER_ENCODING);
/*     */ 
/*     */     
/*     */     try {
/* 184 */       sinkChannel = this.http2Channel.createStream(request.getRequestHeaders());
/* 185 */     } catch (Throwable t) {
/* 186 */       IOException e = (t instanceof IOException) ? (IOException)t : new IOException(t);
/* 187 */       clientCallback.failed(e);
/*     */       return;
/*     */     } 
/* 190 */     final Http2ClientExchange exchange = new Http2ClientExchange(this, (Http2StreamSinkChannel)sinkChannel, request);
/* 191 */     this.currentExchanges.put(Integer.valueOf(sinkChannel.getStreamId()), exchange);
/*     */     
/* 193 */     sinkChannel.setTrailersProducer(new Http2DataStreamSinkChannel.TrailersProducer()
/*     */         {
/*     */           public HeaderMap getTrailers() {
/* 196 */             HeaderMap attachment = (HeaderMap)exchange.getAttachment(HttpAttachments.RESPONSE_TRAILERS);
/* 197 */             Supplier<HeaderMap> supplier = (Supplier<HeaderMap>)exchange.getAttachment(HttpAttachments.RESPONSE_TRAILER_SUPPLIER);
/* 198 */             if (attachment != null && supplier == null)
/* 199 */               return attachment; 
/* 200 */             if (attachment == null && supplier != null)
/* 201 */               return supplier.get(); 
/* 202 */             if (attachment != null) {
/* 203 */               HeaderMap supplied = supplier.get();
/* 204 */               for (HeaderValues k : supplied) {
/* 205 */                 attachment.putAll(k.getHeaderName(), (Collection)k);
/*     */               }
/* 207 */               return attachment;
/*     */             } 
/* 209 */             return null;
/*     */           }
/*     */         });
/*     */ 
/*     */     
/* 214 */     if (clientCallback != null) {
/* 215 */       clientCallback.completed(exchange);
/*     */     }
/* 217 */     if (!hasContent) {
/*     */       
/*     */       try {
/*     */         
/* 221 */         sinkChannel.shutdownWrites();
/* 222 */         if (!sinkChannel.flush()) {
/* 223 */           sinkChannel.getWriteSetter().set(ChannelListeners.flushingChannelListener(null, new ChannelExceptionHandler<StreamSinkChannel>()
/*     */                 {
/*     */                   public void handleException(StreamSinkChannel channel, IOException exception) {
/* 226 */                     Http2ClientConnection.this.handleError(exception);
/*     */                   }
/*     */                 }));
/* 229 */           sinkChannel.resumeWrites();
/*     */         } 
/* 231 */       } catch (Throwable e) {
/* 232 */         handleError(e);
/*     */       } 
/*     */     }
/*     */   }
/*     */   
/*     */   private void handleError(Throwable t) {
/* 238 */     IOException e = (t instanceof IOException) ? (IOException)t : new IOException(t);
/* 239 */     UndertowLogger.REQUEST_IO_LOGGER.ioException(e);
/* 240 */     IoUtils.safeClose((Closeable)this);
/* 241 */     for (Map.Entry<Integer, Http2ClientExchange> entry : this.currentExchanges.entrySet()) {
/*     */       try {
/* 243 */         ((Http2ClientExchange)entry.getValue()).failed(e);
/* 244 */       } catch (Exception ex) {
/* 245 */         UndertowLogger.REQUEST_IO_LOGGER.ioException(new IOException(ex));
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public StreamConnection performUpgrade() throws IOException {
/* 252 */     throw UndertowMessages.MESSAGES.upgradeNotSupported();
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBufferPool getBufferPool() {
/* 257 */     return this.http2Channel.getBufferPool();
/*     */   }
/*     */ 
/*     */   
/*     */   public SocketAddress getPeerAddress() {
/* 262 */     return this.http2Channel.getPeerAddress();
/*     */   }
/*     */ 
/*     */   
/*     */   public <A extends SocketAddress> A getPeerAddress(Class<A> type) {
/* 267 */     return (A)this.http2Channel.getPeerAddress(type);
/*     */   }
/*     */ 
/*     */   
/*     */   public ChannelListener.Setter<? extends ClientConnection> getCloseSetter() {
/* 272 */     return (ChannelListener.Setter<? extends ClientConnection>)this.closeSetter;
/*     */   }
/*     */ 
/*     */   
/*     */   public SocketAddress getLocalAddress() {
/* 277 */     return this.http2Channel.getLocalAddress();
/*     */   }
/*     */ 
/*     */   
/*     */   public <A extends SocketAddress> A getLocalAddress(Class<A> type) {
/* 282 */     return (A)this.http2Channel.getLocalAddress(type);
/*     */   }
/*     */ 
/*     */   
/*     */   public XnioWorker getWorker() {
/* 287 */     return this.http2Channel.getWorker();
/*     */   }
/*     */ 
/*     */   
/*     */   public XnioIoThread getIoThread() {
/* 292 */     return this.http2Channel.getIoThread();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isOpen() {
/* 297 */     return (this.http2Channel.isOpen() && !this.http2Channel.isPeerGoneAway() && !this.http2Channel.isThisGoneAway());
/*     */   }
/*     */ 
/*     */   
/*     */   public void close() throws IOException {
/*     */     try {
/* 303 */       this.http2Channel.sendGoAway(0);
/*     */     } finally {
/* 305 */       for (Map.Entry<Integer, Http2ClientExchange> entry : this.currentExchanges.entrySet()) {
/* 306 */         ((Http2ClientExchange)entry.getValue()).failed(new ClosedChannelException());
/*     */       }
/* 308 */       this.currentExchanges.clear();
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean supportsOption(Option<?> option) {
/* 314 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public <T> T getOption(Option<T> option) throws IOException {
/* 319 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public <T> T setOption(Option<T> option, T value) throws IllegalArgumentException, IOException {
/* 324 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isUpgraded() {
/* 329 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isPushSupported() {
/* 334 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isMultiplexingSupported() {
/* 339 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public ClientStatistics getStatistics() {
/* 344 */     return this.clientStatistics;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isUpgradeSupported() {
/* 349 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public void addCloseListener(ChannelListener<ClientConnection> listener) {
/* 354 */     this.closeListeners.add(listener);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isPingSupported() {
/* 359 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public void sendPing(ClientConnection.PingListener listener, long timeout, TimeUnit timeUnit) {
/* 364 */     long count = PING_COUNTER.incrementAndGet();
/* 365 */     byte[] data = new byte[8];
/* 366 */     data[0] = (byte)(int)count;
/* 367 */     data[1] = (byte)(int)(count << 8L);
/* 368 */     data[2] = (byte)(int)(count << 16L);
/* 369 */     data[3] = (byte)(int)(count << 24L);
/* 370 */     data[4] = (byte)(int)(count << 32L);
/* 371 */     data[5] = (byte)(int)(count << 40L);
/* 372 */     data[6] = (byte)(int)(count << 48L);
/* 373 */     data[7] = (byte)(int)(count << 54L);
/* 374 */     PingKey key = new PingKey(data);
/* 375 */     this.outstandingPings.put(key, listener);
/* 376 */     if (timeout > 0L) {
/* 377 */       this.http2Channel.getIoThread().executeAfter(() -> { ClientConnection.PingListener listener1 = this.outstandingPings.remove(key); if (listener1 != null) listener1.failed(UndertowMessages.MESSAGES.pingTimeout());  }timeout, timeUnit);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 384 */     this.http2Channel.sendPing(data, (channel, exception) -> listener.failed(exception));
/*     */   }
/*     */   
/*     */   private class Http2ReceiveListener implements ChannelListener<Http2Channel> {
/*     */     private Http2ReceiveListener() {}
/*     */     
/*     */     public void handleEvent(Http2Channel channel) {
/*     */       try {
/* 392 */         AbstractHttp2StreamSourceChannel result = (AbstractHttp2StreamSourceChannel)channel.receive();
/* 393 */         if (result instanceof Http2StreamSourceChannel) {
/* 394 */           final Http2StreamSourceChannel streamSourceChannel = (Http2StreamSourceChannel)result;
/*     */           
/* 396 */           int statusCode = Integer.parseInt(streamSourceChannel.getHeaders().getFirst(Http2Channel.STATUS));
/* 397 */           final Http2ClientExchange request = (Http2ClientExchange)Http2ClientConnection.this.currentExchanges.get(Integer.valueOf(streamSourceChannel.getStreamId()));
/* 398 */           if (statusCode < 200) {
/*     */             
/* 400 */             if (statusCode == 100)
/*     */             {
/* 402 */               request.setContinueResponse(request.createResponse(streamSourceChannel));
/*     */             }
/* 404 */             Channels.drain((StreamSourceChannel)result, Long.MAX_VALUE);
/*     */             return;
/*     */           } 
/* 407 */           ((Http2StreamSourceChannel)result).setTrailersHandler(new Http2StreamSourceChannel.TrailersHandler()
/*     */               {
/*     */                 public void handleTrailers(HeaderMap headerMap) {
/* 410 */                   request.putAttachment(HttpAttachments.REQUEST_TRAILERS, headerMap);
/*     */                 }
/*     */               });
/*     */           
/* 414 */           result.addCloseTask(new ChannelListener<AbstractHttp2StreamSourceChannel>()
/*     */               {
/*     */                 public void handleEvent(AbstractHttp2StreamSourceChannel channel) {
/* 417 */                   Http2ClientConnection.this.currentExchanges.remove(Integer.valueOf(streamSourceChannel.getStreamId()));
/*     */                 }
/*     */               });
/* 420 */           streamSourceChannel.setCompletionListener(new ChannelListener<Http2StreamSourceChannel>()
/*     */               {
/*     */                 public void handleEvent(Http2StreamSourceChannel channel) {
/* 423 */                   Http2ClientConnection.this.currentExchanges.remove(Integer.valueOf(streamSourceChannel.getStreamId()));
/*     */                 }
/*     */               });
/* 426 */           if (request == null && Http2ClientConnection.this.initialUpgradeRequest) {
/* 427 */             Channels.drain((StreamSourceChannel)result, Long.MAX_VALUE);
/* 428 */             Http2ClientConnection.this.initialUpgradeRequest = false; return;
/*     */           } 
/* 430 */           if (request == null) {
/* 431 */             channel.sendGoAway(1);
/* 432 */             IoUtils.safeClose((Closeable)Http2ClientConnection.this);
/*     */             return;
/*     */           } 
/* 435 */           request.responseReady(streamSourceChannel);
/* 436 */         } else if (result instanceof Http2PingStreamSourceChannel) {
/* 437 */           handlePing((Http2PingStreamSourceChannel)result);
/* 438 */         } else if (result instanceof Http2RstStreamStreamSourceChannel) {
/* 439 */           Http2RstStreamStreamSourceChannel rstStream = (Http2RstStreamStreamSourceChannel)result;
/* 440 */           int stream = rstStream.getStreamId();
/* 441 */           UndertowLogger.REQUEST_LOGGER.debugf("Client received RST_STREAM for stream %s", stream);
/* 442 */           Http2ClientExchange exchange = (Http2ClientExchange)Http2ClientConnection.this.currentExchanges.remove(Integer.valueOf(stream));
/*     */           
/* 444 */           if (exchange != null)
/*     */           {
/* 446 */             exchange.failed(UndertowMessages.MESSAGES.http2StreamWasReset());
/*     */           }
/* 448 */           Channels.drain((StreamSourceChannel)result, Long.MAX_VALUE);
/* 449 */         } else if (result instanceof Http2PushPromiseStreamSourceChannel) {
/* 450 */           Http2PushPromiseStreamSourceChannel stream = (Http2PushPromiseStreamSourceChannel)result;
/* 451 */           final Http2ClientExchange request = (Http2ClientExchange)Http2ClientConnection.this.currentExchanges.get(Integer.valueOf(stream.getAssociatedStreamId()));
/* 452 */           if (request == null) {
/* 453 */             channel.sendGoAway(1);
/* 454 */           } else if (request.getPushCallback() == null) {
/* 455 */             channel.sendRstStream(stream.getPushedStreamId(), 7);
/*     */           } else {
/* 457 */             ClientRequest cr = new ClientRequest();
/* 458 */             cr.setMethod(new HttpString(stream.getHeaders().getFirst(Http2Channel.METHOD)));
/* 459 */             cr.setPath(stream.getHeaders().getFirst(Http2Channel.PATH));
/* 460 */             cr.setProtocol(Protocols.HTTP_1_1);
/* 461 */             for (HeaderValues header : stream.getHeaders()) {
/* 462 */               cr.getRequestHeaders().putAll(header.getHeaderName(), (Collection)header);
/*     */             }
/*     */             
/* 465 */             Http2ClientExchange newExchange = new Http2ClientExchange(Http2ClientConnection.this, null, cr);
/*     */             
/* 467 */             if (!request.getPushCallback().handlePush(request, newExchange)) {
/*     */               
/* 469 */               channel.sendRstStream(stream.getPushedStreamId(), 7);
/* 470 */               IoUtils.safeClose((Closeable)stream);
/* 471 */             } else if (!Http2ClientConnection.this.http2Channel.addPushPromiseStream(stream.getPushedStreamId())) {
/*     */               
/* 473 */               channel.sendGoAway(1);
/*     */             } else {
/*     */               
/* 476 */               Http2ClientConnection.this.currentExchanges.put(Integer.valueOf(stream.getPushedStreamId()), newExchange);
/*     */             } 
/*     */           } 
/* 479 */           Channels.drain((StreamSourceChannel)result, Long.MAX_VALUE);
/*     */         }
/* 481 */         else if (result instanceof io.undertow.protocols.http2.Http2GoAwayStreamSourceChannel) {
/* 482 */           Http2ClientConnection.this.close();
/* 483 */         } else if (result != null) {
/* 484 */           Channels.drain((StreamSourceChannel)result, Long.MAX_VALUE);
/*     */         }
/*     */       
/* 487 */       } catch (Throwable t) {
/* 488 */         IOException e = (t instanceof IOException) ? (IOException)t : new IOException(t);
/* 489 */         UndertowLogger.REQUEST_IO_LOGGER.ioException(e);
/* 490 */         IoUtils.safeClose((Closeable)Http2ClientConnection.this);
/* 491 */         for (Map.Entry<Integer, Http2ClientExchange> entry : (Iterable<Map.Entry<Integer, Http2ClientExchange>>)Http2ClientConnection.this.currentExchanges.entrySet()) {
/*     */           try {
/* 493 */             ((Http2ClientExchange)entry.getValue()).failed(e);
/* 494 */           } catch (Throwable ex) {
/* 495 */             UndertowLogger.REQUEST_IO_LOGGER.ioException(new IOException(ex));
/*     */           } 
/*     */         } 
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/*     */     private void handlePing(Http2PingStreamSourceChannel frame) {
/* 503 */       byte[] id = frame.getData();
/* 504 */       if (!frame.isAck()) {
/*     */         
/* 506 */         frame.getHttp2Channel().sendPing(id);
/*     */       } else {
/* 508 */         ClientConnection.PingListener listener = (ClientConnection.PingListener)Http2ClientConnection.this.outstandingPings.remove(new Http2ClientConnection.PingKey(id));
/* 509 */         if (listener != null) {
/* 510 */           listener.acknowledged();
/*     */         }
/*     */       } 
/*     */     }
/*     */   }
/*     */   
/*     */   private static final class PingKey
/*     */   {
/*     */     private final byte[] data;
/*     */     
/*     */     private PingKey(byte[] data) {
/* 521 */       this.data = data;
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean equals(Object o) {
/* 526 */       if (this == o) return true; 
/* 527 */       if (o == null || getClass() != o.getClass()) return false;
/*     */       
/* 529 */       PingKey pingKey = (PingKey)o;
/*     */       
/* 531 */       return Arrays.equals(this.data, pingKey.data);
/*     */     }
/*     */ 
/*     */     
/*     */     public int hashCode() {
/* 536 */       return Arrays.hashCode(this.data);
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\client\http2\Http2ClientConnection.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */