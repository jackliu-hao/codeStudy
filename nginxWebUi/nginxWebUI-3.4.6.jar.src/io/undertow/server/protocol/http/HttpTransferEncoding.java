/*     */ package io.undertow.server.protocol.http;
/*     */ 
/*     */ import io.undertow.UndertowLogger;
/*     */ import io.undertow.UndertowOptions;
/*     */ import io.undertow.conduits.ChunkedStreamSinkConduit;
/*     */ import io.undertow.conduits.ChunkedStreamSourceConduit;
/*     */ import io.undertow.conduits.ConduitListener;
/*     */ import io.undertow.conduits.FinishableStreamSinkConduit;
/*     */ import io.undertow.conduits.FixedLengthStreamSourceConduit;
/*     */ import io.undertow.conduits.HeadStreamSinkConduit;
/*     */ import io.undertow.conduits.PreChunkedStreamSinkConduit;
/*     */ import io.undertow.server.Connectors;
/*     */ import io.undertow.server.HttpServerExchange;
/*     */ import io.undertow.util.Attachable;
/*     */ import io.undertow.util.DateUtils;
/*     */ import io.undertow.util.HeaderMap;
/*     */ import io.undertow.util.Headers;
/*     */ import io.undertow.util.HttpString;
/*     */ import io.undertow.util.Methods;
/*     */ import org.jboss.logging.Logger;
/*     */ import org.xnio.conduits.Conduit;
/*     */ import org.xnio.conduits.ConduitStreamSourceChannel;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ class HttpTransferEncoding
/*     */ {
/*  55 */   private static final Logger log = Logger.getLogger("io.undertow.server.handler.transfer-encoding");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void setupRequest(HttpServerExchange exchange) {
/*  65 */     HeaderMap requestHeaders = exchange.getRequestHeaders();
/*  66 */     String connectionHeader = requestHeaders.getFirst(Headers.CONNECTION);
/*  67 */     String transferEncodingHeader = requestHeaders.getLast(Headers.TRANSFER_ENCODING);
/*  68 */     String contentLengthHeader = requestHeaders.getFirst(Headers.CONTENT_LENGTH);
/*     */     
/*  70 */     HttpServerConnection connection = (HttpServerConnection)exchange.getConnection();
/*     */     
/*  72 */     PipeliningBufferingStreamSinkConduit pipeliningBuffer = connection.getPipelineBuffer();
/*  73 */     if (pipeliningBuffer != null) {
/*  74 */       pipeliningBuffer.setupPipelineBuffer(exchange);
/*     */     }
/*  76 */     ConduitStreamSourceChannel sourceChannel = connection.getChannel().getSourceChannel();
/*  77 */     sourceChannel.setConduit((StreamSourceConduit)connection.getReadDataStreamSourceConduit());
/*     */     
/*  79 */     boolean persistentConnection = persistentConnection(exchange, connectionHeader);
/*     */     
/*  81 */     if (transferEncodingHeader == null && contentLengthHeader == null) {
/*  82 */       if (persistentConnection && connection
/*  83 */         .getExtraBytes() != null && pipeliningBuffer == null && connection
/*     */         
/*  85 */         .getUndertowOptions().get(UndertowOptions.BUFFER_PIPELINED_DATA, false)) {
/*  86 */         pipeliningBuffer = new PipeliningBufferingStreamSinkConduit(connection.getOriginalSinkConduit(), connection.getByteBufferPool());
/*  87 */         connection.setPipelineBuffer(pipeliningBuffer);
/*  88 */         pipeliningBuffer.setupPipelineBuffer(exchange);
/*     */       } 
/*     */       
/*  91 */       Connectors.terminateRequest(exchange);
/*     */     } else {
/*  93 */       persistentConnection = handleRequestEncoding(exchange, transferEncodingHeader, contentLengthHeader, connection, pipeliningBuffer, persistentConnection);
/*     */     } 
/*     */     
/*  96 */     exchange.setPersistent(persistentConnection);
/*     */     
/*  98 */     if (!exchange.isRequestComplete() || connection.getExtraBytes() != null) {
/*     */       
/* 100 */       sourceChannel.setReadListener(null);
/* 101 */       sourceChannel.suspendReads();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static boolean handleRequestEncoding(HttpServerExchange exchange, String transferEncodingHeader, String contentLengthHeader, HttpServerConnection connection, PipeliningBufferingStreamSinkConduit pipeliningBuffer, boolean persistentConnection) {
/* 108 */     HttpString transferEncoding = Headers.IDENTITY;
/* 109 */     if (transferEncodingHeader != null) {
/* 110 */       transferEncoding = new HttpString(transferEncodingHeader);
/*     */     }
/* 112 */     if (transferEncodingHeader != null && !transferEncoding.equals(Headers.IDENTITY)) {
/* 113 */       ConduitStreamSourceChannel sourceChannel = ((HttpServerConnection)exchange.getConnection()).getChannel().getSourceChannel();
/* 114 */       sourceChannel.setConduit((StreamSourceConduit)new ChunkedStreamSourceConduit(sourceChannel.getConduit(), exchange, chunkedDrainListener(exchange)));
/* 115 */     } else if (contentLengthHeader != null) {
/*     */       
/* 117 */       long contentLength = parsePositiveLong(contentLengthHeader);
/* 118 */       if (contentLength == 0L) {
/* 119 */         log.trace("No content, starting next request");
/*     */         
/* 121 */         Connectors.terminateRequest(exchange);
/*     */       } else {
/*     */         
/* 124 */         ConduitStreamSourceChannel sourceChannel = ((HttpServerConnection)exchange.getConnection()).getChannel().getSourceChannel();
/* 125 */         sourceChannel.setConduit(fixedLengthStreamSourceConduitWrapper(contentLength, sourceChannel.getConduit(), exchange));
/*     */       } 
/* 127 */     } else if (transferEncodingHeader != null) {
/*     */       
/* 129 */       log.trace("Connection not persistent (no content length and identity transfer encoding)");
/*     */       
/* 131 */       persistentConnection = false;
/* 132 */     } else if (persistentConnection) {
/*     */ 
/*     */       
/* 135 */       if (connection.getExtraBytes() != null && pipeliningBuffer == null && connection
/*     */         
/* 137 */         .getUndertowOptions().get(UndertowOptions.BUFFER_PIPELINED_DATA, false)) {
/* 138 */         pipeliningBuffer = new PipeliningBufferingStreamSinkConduit(connection.getOriginalSinkConduit(), connection.getByteBufferPool());
/* 139 */         connection.setPipelineBuffer(pipeliningBuffer);
/* 140 */         pipeliningBuffer.setupPipelineBuffer(exchange);
/*     */       } 
/*     */ 
/*     */       
/* 144 */       Connectors.terminateRequest(exchange);
/*     */     }
/*     */     else {
/*     */       
/* 148 */       Connectors.terminateRequest(exchange);
/*     */     } 
/* 150 */     return persistentConnection;
/*     */   }
/*     */   
/*     */   private static boolean persistentConnection(HttpServerExchange exchange, String connectionHeader) {
/* 154 */     if (exchange.isHttp11())
/* 155 */       return (connectionHeader == null || !Headers.CLOSE.equalToString(connectionHeader)); 
/* 156 */     if (exchange.isHttp10() && 
/* 157 */       connectionHeader != null && 
/* 158 */       Headers.KEEP_ALIVE.equals(new HttpString(connectionHeader))) {
/* 159 */       return true;
/*     */     }
/*     */ 
/*     */     
/* 163 */     log.trace("Connection not persistent");
/* 164 */     return false;
/*     */   }
/*     */   
/*     */   private static StreamSourceConduit fixedLengthStreamSourceConduitWrapper(long contentLength, StreamSourceConduit conduit, HttpServerExchange exchange) {
/* 168 */     return (StreamSourceConduit)new FixedLengthStreamSourceConduit(conduit, contentLength, fixedLengthDrainListener(exchange), exchange);
/*     */   }
/*     */   
/*     */   private static ConduitListener<FixedLengthStreamSourceConduit> fixedLengthDrainListener(final HttpServerExchange exchange) {
/* 172 */     return new ConduitListener<FixedLengthStreamSourceConduit>() {
/*     */         public void handleEvent(FixedLengthStreamSourceConduit fixedLengthConduit) {
/* 174 */           long remaining = fixedLengthConduit.getRemaining();
/* 175 */           if (remaining > 0L) {
/* 176 */             UndertowLogger.REQUEST_LOGGER.requestWasNotFullyConsumed();
/* 177 */             exchange.setPersistent(false);
/*     */           } 
/* 179 */           Connectors.terminateRequest(exchange);
/*     */         }
/*     */       };
/*     */   }
/*     */   
/*     */   private static ConduitListener<ChunkedStreamSourceConduit> chunkedDrainListener(final HttpServerExchange exchange) {
/* 185 */     return new ConduitListener<ChunkedStreamSourceConduit>() {
/*     */         public void handleEvent(ChunkedStreamSourceConduit chunkedStreamSourceConduit) {
/* 187 */           if (!chunkedStreamSourceConduit.isFinished()) {
/* 188 */             UndertowLogger.REQUEST_LOGGER.requestWasNotFullyConsumed();
/* 189 */             exchange.setPersistent(false);
/*     */           } 
/* 191 */           Connectors.terminateRequest(exchange);
/*     */         }
/*     */       };
/*     */   }
/*     */   
/*     */   private static ConduitListener<StreamSinkConduit> terminateResponseListener(final HttpServerExchange exchange) {
/* 197 */     return new ConduitListener<StreamSinkConduit>() {
/*     */         public void handleEvent(StreamSinkConduit channel) {
/* 199 */           Connectors.terminateResponse(exchange);
/*     */         }
/*     */       };
/*     */   }
/*     */   static StreamSinkConduit createSinkConduit(HttpServerExchange exchange) {
/*     */     HeadStreamSinkConduit headStreamSinkConduit;
/* 205 */     DateUtils.addDateHeaderIfRequired(exchange);
/*     */     
/* 207 */     boolean headRequest = exchange.getRequestMethod().equals(Methods.HEAD);
/* 208 */     HttpServerConnection serverConnection = (HttpServerConnection)exchange.getConnection();
/*     */     
/* 210 */     HttpResponseConduit responseConduit = serverConnection.getResponseConduit();
/* 211 */     responseConduit.reset(exchange);
/* 212 */     HttpResponseConduit httpResponseConduit1 = responseConduit;
/* 213 */     if (headRequest) {
/*     */ 
/*     */ 
/*     */       
/* 217 */       headStreamSinkConduit = new HeadStreamSinkConduit((StreamSinkConduit)httpResponseConduit1, terminateResponseListener(exchange));
/* 218 */     } else if (!Connectors.isEntityBodyAllowed(exchange)) {
/*     */       
/* 220 */       exchange.getResponseHeaders().remove(Headers.CONTENT_LENGTH);
/* 221 */       exchange.getResponseHeaders().remove(Headers.TRANSFER_ENCODING);
/* 222 */       headStreamSinkConduit = new HeadStreamSinkConduit((StreamSinkConduit)headStreamSinkConduit, terminateResponseListener(exchange));
/* 223 */       return (StreamSinkConduit)headStreamSinkConduit;
/*     */     } 
/*     */     
/* 226 */     HeaderMap responseHeaders = exchange.getResponseHeaders();
/*     */     
/* 228 */     String connection = responseHeaders.getFirst(Headers.CONNECTION);
/* 229 */     if (exchange.getStatusCode() == 417)
/*     */     {
/*     */       
/* 232 */       exchange.setPersistent(false);
/*     */     }
/* 234 */     if (!exchange.isPersistent()) {
/* 235 */       responseHeaders.put(Headers.CONNECTION, Headers.CLOSE.toString());
/* 236 */     } else if (exchange.isPersistent() && connection != null) {
/* 237 */       if (HttpString.tryFromString(connection).equals(Headers.CLOSE)) {
/* 238 */         exchange.setPersistent(false);
/*     */       }
/* 240 */     } else if (exchange.getConnection().getUndertowOptions().get(UndertowOptions.ALWAYS_SET_KEEP_ALIVE, true)) {
/* 241 */       responseHeaders.put(Headers.CONNECTION, Headers.KEEP_ALIVE.toString());
/*     */     } 
/*     */     
/* 244 */     String transferEncodingHeader = responseHeaders.getLast(Headers.TRANSFER_ENCODING);
/* 245 */     if (transferEncodingHeader == null) {
/* 246 */       String contentLengthHeader = responseHeaders.getFirst(Headers.CONTENT_LENGTH);
/* 247 */       if (contentLengthHeader != null) {
/* 248 */         StreamSinkConduit res = handleFixedLength(exchange, headRequest, (StreamSinkConduit)headStreamSinkConduit, responseHeaders, contentLengthHeader, serverConnection);
/* 249 */         if (res != null) {
/* 250 */           return res;
/*     */         }
/*     */       } 
/*     */     } else {
/* 254 */       responseHeaders.remove(Headers.CONTENT_LENGTH);
/*     */     } 
/* 256 */     return handleResponseConduit(exchange, headRequest, (StreamSinkConduit)headStreamSinkConduit, responseHeaders, terminateResponseListener(exchange), transferEncodingHeader);
/*     */   }
/*     */   
/*     */   private static StreamSinkConduit handleFixedLength(HttpServerExchange exchange, boolean headRequest, StreamSinkConduit channel, HeaderMap responseHeaders, String contentLengthHeader, HttpServerConnection connection) {
/*     */     try {
/* 261 */       long contentLength = parsePositiveLong(contentLengthHeader);
/* 262 */       if (headRequest) {
/* 263 */         return channel;
/*     */       }
/*     */       
/* 266 */       ServerFixedLengthStreamSinkConduit fixed = connection.getFixedLengthStreamSinkConduit();
/* 267 */       fixed.reset(contentLength, exchange);
/* 268 */       return (StreamSinkConduit)fixed;
/* 269 */     } catch (NumberFormatException e) {
/*     */       
/* 271 */       responseHeaders.remove(Headers.CONTENT_LENGTH);
/*     */       
/* 273 */       return null;
/*     */     } 
/*     */   }
/*     */   
/*     */   private static StreamSinkConduit handleResponseConduit(HttpServerExchange exchange, boolean headRequest, StreamSinkConduit channel, HeaderMap responseHeaders, ConduitListener<StreamSinkConduit> finishListener, String transferEncodingHeader) {
/* 278 */     if (transferEncodingHeader == null) {
/* 279 */       if (exchange.isHttp11()) {
/* 280 */         if (exchange.isPersistent()) {
/* 281 */           responseHeaders.put(Headers.TRANSFER_ENCODING, Headers.CHUNKED.toString());
/*     */           
/* 283 */           if (headRequest) {
/* 284 */             return channel;
/*     */           }
/* 286 */           return (StreamSinkConduit)new ChunkedStreamSinkConduit(channel, exchange.getConnection().getByteBufferPool(), true, !exchange.isPersistent(), responseHeaders, finishListener, (Attachable)exchange);
/*     */         } 
/* 288 */         if (headRequest) {
/* 289 */           return channel;
/*     */         }
/* 291 */         return (StreamSinkConduit)new FinishableStreamSinkConduit(channel, finishListener);
/*     */       } 
/*     */       
/* 294 */       exchange.setPersistent(false);
/* 295 */       responseHeaders.put(Headers.CONNECTION, Headers.CLOSE.toString());
/* 296 */       if (headRequest) {
/* 297 */         return channel;
/*     */       }
/* 299 */       return (StreamSinkConduit)new FinishableStreamSinkConduit(channel, finishListener);
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 304 */     return handleExplicitTransferEncoding(exchange, channel, finishListener, responseHeaders, transferEncodingHeader, headRequest);
/*     */   }
/*     */ 
/*     */   
/*     */   private static StreamSinkConduit handleExplicitTransferEncoding(HttpServerExchange exchange, StreamSinkConduit channel, ConduitListener<StreamSinkConduit> finishListener, HeaderMap responseHeaders, String transferEncodingHeader, boolean headRequest) {
/* 309 */     HttpString transferEncoding = new HttpString(transferEncodingHeader);
/* 310 */     if (transferEncoding.equals(Headers.CHUNKED)) {
/* 311 */       if (headRequest) {
/* 312 */         return channel;
/*     */       }
/* 314 */       Boolean preChunked = (Boolean)exchange.getAttachment(HttpAttachments.PRE_CHUNKED_RESPONSE);
/* 315 */       if (preChunked != null && preChunked.booleanValue()) {
/* 316 */         return (StreamSinkConduit)new PreChunkedStreamSinkConduit(channel, finishListener, (Attachable)exchange);
/*     */       }
/* 318 */       return (StreamSinkConduit)new ChunkedStreamSinkConduit(channel, exchange.getConnection().getByteBufferPool(), true, !exchange.isPersistent(), responseHeaders, finishListener, (Attachable)exchange);
/*     */     } 
/*     */ 
/*     */     
/* 322 */     if (headRequest) {
/* 323 */       return channel;
/*     */     }
/* 325 */     log.trace("Cancelling persistence because response is identity with no content length");
/*     */     
/* 327 */     exchange.setPersistent(false);
/* 328 */     responseHeaders.put(Headers.CONNECTION, Headers.CLOSE.toString());
/* 329 */     return (StreamSinkConduit)new FinishableStreamSinkConduit(channel, terminateResponseListener(exchange));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static long parsePositiveLong(String str) {
/* 340 */     long value = 0L;
/* 341 */     int length = str.length();
/*     */     
/* 343 */     if (length == 0) {
/* 344 */       throw new NumberFormatException(str);
/*     */     }
/*     */     
/* 347 */     long multiplier = 1L;
/* 348 */     for (int i = length - 1; i >= 0; i--) {
/* 349 */       char c = str.charAt(i);
/*     */       
/* 351 */       if (c < '0' || c > '9') {
/* 352 */         throw new NumberFormatException(str);
/*     */       }
/* 354 */       long digit = (c - 48);
/* 355 */       value += digit * multiplier;
/* 356 */       multiplier *= 10L;
/*     */     } 
/* 358 */     return value;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\protocol\http\HttpTransferEncoding.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */