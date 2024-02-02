/*     */ package io.undertow.server.protocol.http2;
/*     */ 
/*     */ import io.undertow.UndertowLogger;
/*     */ import io.undertow.UndertowOptions;
/*     */ import io.undertow.conduits.HeadStreamSinkConduit;
/*     */ import io.undertow.connector.PooledByteBuffer;
/*     */ import io.undertow.protocols.http2.AbstractHttp2StreamSourceChannel;
/*     */ import io.undertow.protocols.http2.Http2Channel;
/*     */ import io.undertow.protocols.http2.Http2DataStreamSinkChannel;
/*     */ import io.undertow.protocols.http2.Http2HeadersStreamSinkChannel;
/*     */ import io.undertow.protocols.http2.Http2StreamSourceChannel;
/*     */ import io.undertow.server.ConduitWrapper;
/*     */ import io.undertow.server.ConnectorStatisticsImpl;
/*     */ import io.undertow.server.Connectors;
/*     */ import io.undertow.server.HttpHandler;
/*     */ import io.undertow.server.HttpServerExchange;
/*     */ import io.undertow.server.protocol.http.HttpAttachments;
/*     */ import io.undertow.server.protocol.http.HttpContinue;
/*     */ import io.undertow.server.protocol.http.HttpRequestParser;
/*     */ import io.undertow.util.ConduitFactory;
/*     */ import io.undertow.util.HeaderMap;
/*     */ import io.undertow.util.HeaderValues;
/*     */ import io.undertow.util.Headers;
/*     */ import io.undertow.util.ImmediatePooledByteBuffer;
/*     */ import io.undertow.util.Methods;
/*     */ import io.undertow.util.ParameterLimitException;
/*     */ import io.undertow.util.Protocols;
/*     */ import java.io.Closeable;
/*     */ import java.io.IOException;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.channels.Channel;
/*     */ import java.nio.charset.StandardCharsets;
/*     */ import java.util.Collection;
/*     */ import java.util.function.Supplier;
/*     */ import javax.net.ssl.SSLSession;
/*     */ import org.xnio.ChannelListener;
/*     */ import org.xnio.IoUtils;
/*     */ import org.xnio.OptionMap;
/*     */ import org.xnio.channels.Channels;
/*     */ import org.xnio.channels.StreamSourceChannel;
/*     */ import org.xnio.conduits.Conduit;
/*     */ import org.xnio.conduits.StreamSinkConduit;
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
/*     */ public class Http2ReceiveListener
/*     */   implements ChannelListener<Http2Channel>
/*     */ {
/*     */   private final HttpHandler rootHandler;
/*     */   private final long maxEntitySize;
/*     */   private final OptionMap undertowOptions;
/*     */   private final String encoding;
/*     */   private final boolean decode;
/*  80 */   private final StringBuilder decodeBuffer = new StringBuilder();
/*     */   
/*     */   private final boolean allowEncodingSlash;
/*     */   private final int bufferSize;
/*     */   private final int maxParameters;
/*     */   private final boolean recordRequestStartTime;
/*     */   private final boolean allowUnescapedCharactersInUrl;
/*     */   private final ConnectorStatisticsImpl connectorStatistics;
/*     */   
/*     */   public Http2ReceiveListener(HttpHandler rootHandler, OptionMap undertowOptions, int bufferSize, ConnectorStatisticsImpl connectorStatistics) {
/*  90 */     this.rootHandler = rootHandler;
/*  91 */     this.undertowOptions = undertowOptions;
/*  92 */     this.bufferSize = bufferSize;
/*  93 */     this.connectorStatistics = connectorStatistics;
/*  94 */     this.maxEntitySize = undertowOptions.get(UndertowOptions.MAX_ENTITY_SIZE, -1L);
/*  95 */     this.allowEncodingSlash = undertowOptions.get(UndertowOptions.ALLOW_ENCODED_SLASH, false);
/*  96 */     this.decode = undertowOptions.get(UndertowOptions.DECODE_URL, true);
/*  97 */     this.maxParameters = undertowOptions.get(UndertowOptions.MAX_PARAMETERS, 1000);
/*  98 */     this.recordRequestStartTime = undertowOptions.get(UndertowOptions.RECORD_REQUEST_START_TIME, false);
/*  99 */     if (undertowOptions.get(UndertowOptions.DECODE_URL, true)) {
/* 100 */       this.encoding = (String)undertowOptions.get(UndertowOptions.URL_CHARSET, StandardCharsets.UTF_8.name());
/*     */     } else {
/* 102 */       this.encoding = null;
/*     */     } 
/* 104 */     this.allowUnescapedCharactersInUrl = undertowOptions.get(UndertowOptions.ALLOW_UNESCAPED_CHARACTERS_IN_URL, false);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void handleEvent(Http2Channel channel) {
/*     */     try {
/* 111 */       AbstractHttp2StreamSourceChannel frame = (AbstractHttp2StreamSourceChannel)channel.receive();
/* 112 */       if (frame == null) {
/*     */         return;
/*     */       }
/* 115 */       if (frame instanceof Http2StreamSourceChannel)
/*     */       {
/* 117 */         handleRequests(channel, (Http2StreamSourceChannel)frame);
/*     */       
/*     */       }
/*     */     }
/* 121 */     catch (IOException e) {
/* 122 */       UndertowLogger.REQUEST_IO_LOGGER.ioException(e);
/* 123 */       IoUtils.safeClose((Closeable)channel);
/* 124 */     } catch (Throwable t) {
/* 125 */       UndertowLogger.REQUEST_IO_LOGGER.handleUnexpectedFailure(t);
/* 126 */       IoUtils.safeClose((Closeable)channel);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private void handleRequests(Http2Channel channel, Http2StreamSourceChannel frame) {
/* 132 */     Http2StreamSourceChannel dataChannel = frame;
/* 133 */     Http2ServerConnection connection = new Http2ServerConnection(channel, dataChannel, this.undertowOptions, this.bufferSize, this.rootHandler);
/*     */ 
/*     */     
/* 136 */     if (!checkRequestHeaders(dataChannel.getHeaders())) {
/* 137 */       channel.sendRstStream(frame.getStreamId(), 1);
/*     */       try {
/* 139 */         Channels.drain((StreamSourceChannel)frame, Long.MAX_VALUE);
/* 140 */       } catch (IOException iOException) {}
/*     */ 
/*     */       
/*     */       return;
/*     */     } 
/*     */ 
/*     */     
/* 147 */     final HttpServerExchange exchange = new HttpServerExchange(connection, dataChannel.getHeaders(), dataChannel.getResponseChannel().getHeaders(), this.maxEntitySize);
/*     */ 
/*     */     
/* 150 */     dataChannel.setTrailersHandler(new Http2StreamSourceChannel.TrailersHandler()
/*     */         {
/*     */           public void handleTrailers(HeaderMap headerMap) {
/* 153 */             exchange.putAttachment(HttpAttachments.REQUEST_TRAILERS, headerMap);
/*     */           }
/*     */         });
/* 156 */     connection.setExchange(exchange);
/* 157 */     dataChannel.setMaxStreamSize(this.maxEntitySize);
/* 158 */     exchange.setRequestScheme(exchange.getRequestHeaders().getFirst(Http2Channel.SCHEME));
/* 159 */     exchange.setRequestMethod(Methods.fromString(exchange.getRequestHeaders().getFirst(Http2Channel.METHOD)));
/* 160 */     exchange.getRequestHeaders().put(Headers.HOST, exchange.getRequestHeaders().getFirst(Http2Channel.AUTHORITY));
/* 161 */     if (!Connectors.areRequestHeadersValid(exchange.getRequestHeaders())) {
/* 162 */       UndertowLogger.REQUEST_IO_LOGGER.debugf("Invalid headers in HTTP/2 request, closing connection. Remote peer %s", connection.getPeerAddress());
/* 163 */       channel.sendGoAway(1);
/*     */       
/*     */       return;
/*     */     } 
/* 167 */     String path = exchange.getRequestHeaders().getFirst(Http2Channel.PATH);
/* 168 */     if (path == null || path.isEmpty()) {
/* 169 */       UndertowLogger.REQUEST_IO_LOGGER.debugf("No :path header sent in HTTP/2 request, closing connection. Remote peer %s", connection.getPeerAddress());
/* 170 */       channel.sendGoAway(1);
/*     */       
/*     */       return;
/*     */     } 
/* 174 */     if (this.recordRequestStartTime) {
/* 175 */       Connectors.setRequestStartTime(exchange);
/*     */     }
/* 177 */     handleCommonSetup(dataChannel.getResponseChannel(), exchange, connection);
/* 178 */     if (!dataChannel.isOpen()) {
/* 179 */       Connectors.terminateRequest(exchange);
/*     */     } else {
/* 181 */       dataChannel.setCompletionListener(new ChannelListener<Http2StreamSourceChannel>()
/*     */           {
/*     */             public void handleEvent(Http2StreamSourceChannel channel) {
/* 184 */               Connectors.terminateRequest(exchange);
/*     */             }
/*     */           });
/*     */     } 
/* 188 */     if (this.connectorStatistics != null) {
/* 189 */       this.connectorStatistics.setup(exchange);
/*     */     }
/*     */     
/*     */     try {
/* 193 */       Connectors.setExchangeRequestPath(exchange, path, this.encoding, this.decode, this.allowEncodingSlash, this.decodeBuffer, this.maxParameters);
/* 194 */     } catch (ParameterLimitException e) {
/*     */       
/* 196 */       UndertowLogger.REQUEST_IO_LOGGER.debug("Failed to set request path", (Throwable)e);
/* 197 */       exchange.setStatusCode(400);
/* 198 */       exchange.endExchange();
/*     */       
/*     */       return;
/*     */     } 
/*     */     
/* 203 */     exchange.getRequestHeaders().remove(Http2Channel.AUTHORITY);
/* 204 */     exchange.getRequestHeaders().remove(Http2Channel.PATH);
/* 205 */     exchange.getRequestHeaders().remove(Http2Channel.SCHEME);
/* 206 */     exchange.getRequestHeaders().remove(Http2Channel.METHOD);
/*     */ 
/*     */     
/* 209 */     Connectors.executeRootHandler(this.rootHandler, exchange);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   void handleInitialRequest(HttpServerExchange initial, Http2Channel channel, byte[] data) {
/* 219 */     Http2HeadersStreamSinkChannel sink = channel.createInitialUpgradeResponseStream();
/* 220 */     Http2ServerConnection connection = new Http2ServerConnection(channel, (Http2DataStreamSinkChannel)sink, this.undertowOptions, this.bufferSize, this.rootHandler);
/*     */     
/* 222 */     HeaderMap requestHeaders = new HeaderMap();
/* 223 */     for (HeaderValues hv : initial.getRequestHeaders()) {
/* 224 */       requestHeaders.putAll(hv.getHeaderName(), (Collection)hv);
/*     */     }
/* 226 */     HttpServerExchange exchange = new HttpServerExchange(connection, requestHeaders, sink.getHeaders(), this.maxEntitySize);
/* 227 */     if (initial.getRequestHeaders().contains(Headers.EXPECT)) {
/* 228 */       HttpContinue.markContinueResponseSent(exchange);
/*     */     }
/* 230 */     if (initial.getAttachment(HttpAttachments.REQUEST_TRAILERS) != null) {
/* 231 */       exchange.putAttachment(HttpAttachments.REQUEST_TRAILERS, initial.getAttachment(HttpAttachments.REQUEST_TRAILERS));
/*     */     }
/* 233 */     Connectors.setRequestStartTime(initial, exchange);
/* 234 */     connection.setExchange(exchange);
/* 235 */     exchange.setRequestScheme(initial.getRequestScheme());
/* 236 */     exchange.setRequestMethod(initial.getRequestMethod());
/* 237 */     exchange.setQueryString(initial.getQueryString());
/* 238 */     if (data != null) {
/* 239 */       Connectors.ungetRequestBytes(exchange, new PooledByteBuffer[] { (PooledByteBuffer)new ImmediatePooledByteBuffer(ByteBuffer.wrap(data)) });
/*     */     }
/* 241 */     Connectors.terminateRequest(exchange);
/* 242 */     String uri = exchange.getQueryString().isEmpty() ? initial.getRequestURI() : (initial.getRequestURI() + '?' + exchange.getQueryString());
/*     */     try {
/* 244 */       Connectors.setExchangeRequestPath(exchange, uri, this.encoding, this.decode, this.allowEncodingSlash, this.decodeBuffer, this.maxParameters);
/* 245 */     } catch (ParameterLimitException e) {
/* 246 */       exchange.setStatusCode(400);
/* 247 */       exchange.endExchange();
/*     */       
/*     */       return;
/*     */     } 
/* 251 */     handleCommonSetup(sink, exchange, connection);
/* 252 */     Connectors.executeRootHandler(this.rootHandler, exchange);
/*     */   }
/*     */   
/*     */   private void handleCommonSetup(Http2HeadersStreamSinkChannel sink, final HttpServerExchange exchange, Http2ServerConnection connection) {
/* 256 */     Http2Channel channel = (Http2Channel)sink.getChannel();
/* 257 */     SSLSession session = channel.getSslSession();
/* 258 */     if (session != null) {
/* 259 */       connection.setSslSessionInfo(new Http2SslSessionInfo(channel));
/*     */     }
/* 261 */     sink.setTrailersProducer(new Http2DataStreamSinkChannel.TrailersProducer()
/*     */         {
/*     */           public HeaderMap getTrailers() {
/* 264 */             Supplier<HeaderMap> supplier = (Supplier<HeaderMap>)exchange.getAttachment(HttpAttachments.RESPONSE_TRAILER_SUPPLIER);
/* 265 */             if (supplier != null) {
/* 266 */               return supplier.get();
/*     */             }
/* 268 */             return (HeaderMap)exchange.getAttachment(HttpAttachments.RESPONSE_TRAILERS);
/*     */           }
/*     */         });
/* 271 */     sink.setCompletionListener(new ChannelListener<Http2DataStreamSinkChannel>()
/*     */         {
/*     */           public void handleEvent(Http2DataStreamSinkChannel channel) {
/* 274 */             Connectors.terminateResponse(exchange);
/*     */           }
/*     */         });
/* 277 */     exchange.setProtocol(Protocols.HTTP_2_0);
/* 278 */     if (exchange.getRequestMethod().equals(Methods.HEAD)) {
/* 279 */       exchange.addResponseWrapper(new ConduitWrapper<StreamSinkConduit>()
/*     */           {
/*     */             public StreamSinkConduit wrap(ConduitFactory<StreamSinkConduit> factory, HttpServerExchange exchange) {
/* 282 */               return (StreamSinkConduit)new HeadStreamSinkConduit((StreamSinkConduit)factory.create(), null, true);
/*     */             }
/*     */           });
/*     */     }
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
/*     */   private boolean checkRequestHeaders(HeaderMap headers) {
/* 297 */     if (headers.count(Http2Channel.METHOD) != 1 || headers.contains(Headers.CONNECTION)) {
/* 298 */       return false;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 303 */     if (headers.get(Http2Channel.METHOD).contains("CONNECT")) {
/* 304 */       if (headers.contains(Http2Channel.SCHEME) || headers.contains(Http2Channel.PATH) || headers.count(Http2Channel.AUTHORITY) != 1) {
/* 305 */         return false;
/*     */       
/*     */       }
/*     */     }
/* 309 */     else if (headers.count(Http2Channel.SCHEME) != 1 || headers.count(Http2Channel.PATH) != 1) {
/* 310 */       return false;
/*     */     } 
/*     */ 
/*     */     
/* 314 */     if (headers.contains(Headers.TE)) {
/* 315 */       for (String value : headers.get(Headers.TE)) {
/* 316 */         if (!value.equals("trailers")) {
/* 317 */           return false;
/*     */         }
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/* 323 */     if (headers.contains(Http2Channel.PATH)) {
/* 324 */       for (byte b : headers.get(Http2Channel.PATH).getFirst().getBytes(StandardCharsets.ISO_8859_1)) {
/* 325 */         if (!this.allowUnescapedCharactersInUrl && !HttpRequestParser.isTargetCharacterAllowed((char)b)) {
/* 326 */           return false;
/*     */         }
/*     */       } 
/*     */     }
/*     */     
/* 331 */     if (headers.contains(Http2Channel.SCHEME)) {
/* 332 */       for (byte b : headers.get(Http2Channel.SCHEME).getFirst().getBytes(StandardCharsets.ISO_8859_1)) {
/* 333 */         if (!Connectors.isValidSchemeCharacter(b)) {
/* 334 */           return false;
/*     */         }
/*     */       } 
/*     */     }
/*     */     
/* 339 */     if (headers.contains(Http2Channel.AUTHORITY)) {
/* 340 */       for (byte b : headers.get(Http2Channel.AUTHORITY).getFirst().getBytes(StandardCharsets.ISO_8859_1)) {
/* 341 */         if (!HttpRequestParser.isTargetCharacterAllowed((char)b)) {
/* 342 */           return false;
/*     */         }
/*     */       } 
/*     */     }
/*     */     
/* 347 */     if (headers.contains(Http2Channel.METHOD)) {
/* 348 */       for (byte b : headers.get(Http2Channel.METHOD).getFirst().getBytes(StandardCharsets.ISO_8859_1)) {
/* 349 */         if (!Connectors.isValidTokenCharacter(b)) {
/* 350 */           return false;
/*     */         }
/*     */       } 
/*     */     }
/* 354 */     return true;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\protocol\http2\Http2ReceiveListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */