/*     */ package io.undertow.io;
/*     */ 
/*     */ import io.undertow.UndertowLogger;
/*     */ import io.undertow.UndertowMessages;
/*     */ import io.undertow.connector.PooledByteBuffer;
/*     */ import io.undertow.server.Connectors;
/*     */ import io.undertow.server.HttpHandler;
/*     */ import io.undertow.server.HttpServerExchange;
/*     */ import io.undertow.util.Headers;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.CharBuffer;
/*     */ import java.nio.channels.Channel;
/*     */ import java.nio.charset.Charset;
/*     */ import java.nio.charset.CharsetDecoder;
/*     */ import java.nio.charset.StandardCharsets;
/*     */ import org.xnio.ChannelListener;
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
/*     */ public class AsyncReceiverImpl
/*     */   implements Receiver
/*     */ {
/*  46 */   private static final Receiver.ErrorCallback END_EXCHANGE = new Receiver.ErrorCallback()
/*     */     {
/*     */       public void error(HttpServerExchange exchange, IOException e) {
/*  49 */         e.printStackTrace();
/*  50 */         exchange.setStatusCode(500);
/*  51 */         UndertowLogger.REQUEST_IO_LOGGER.ioException(e);
/*  52 */         exchange.endExchange();
/*     */       }
/*     */     };
/*  55 */   public static final byte[] EMPTY_BYTE_ARRAY = new byte[0];
/*     */   
/*     */   private final HttpServerExchange exchange;
/*     */   
/*     */   private final StreamSourceChannel channel;
/*  60 */   private int maxBufferSize = -1;
/*     */   private boolean paused = false;
/*     */   private boolean done = false;
/*     */   
/*     */   public AsyncReceiverImpl(HttpServerExchange exchange) {
/*  65 */     this.exchange = exchange;
/*  66 */     this.channel = exchange.getRequestChannel();
/*  67 */     if (this.channel == null) {
/*  68 */       throw UndertowMessages.MESSAGES.requestChannelAlreadyProvided();
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public void setMaxBufferSize(int maxBufferSize) {
/*  74 */     this.maxBufferSize = maxBufferSize;
/*     */   }
/*     */ 
/*     */   
/*     */   public void receiveFullString(Receiver.FullStringCallback callback, Receiver.ErrorCallback errorCallback) {
/*  79 */     receiveFullString(callback, errorCallback, StandardCharsets.ISO_8859_1);
/*     */   }
/*     */ 
/*     */   
/*     */   public void receiveFullString(Receiver.FullStringCallback callback) {
/*  84 */     receiveFullString(callback, END_EXCHANGE, StandardCharsets.ISO_8859_1);
/*     */   }
/*     */ 
/*     */   
/*     */   public void receivePartialString(Receiver.PartialStringCallback callback, Receiver.ErrorCallback errorCallback) {
/*  89 */     receivePartialString(callback, errorCallback, StandardCharsets.ISO_8859_1);
/*     */   }
/*     */ 
/*     */   
/*     */   public void receivePartialString(Receiver.PartialStringCallback callback) {
/*  94 */     receivePartialString(callback, END_EXCHANGE, StandardCharsets.ISO_8859_1);
/*     */   }
/*     */   public void receiveFullString(final Receiver.FullStringCallback callback, Receiver.ErrorCallback errorCallback, final Charset charset) {
/*     */     long contentLength;
/*     */     final ByteArrayOutputStream sb;
/*  99 */     if (this.done) {
/* 100 */       throw UndertowMessages.MESSAGES.requestBodyAlreadyRead();
/*     */     }
/* 102 */     final Receiver.ErrorCallback error = (errorCallback == null) ? END_EXCHANGE : errorCallback;
/* 103 */     if (callback == null) {
/* 104 */       throw UndertowMessages.MESSAGES.argumentCannotBeNull("callback");
/*     */     }
/* 106 */     if (this.exchange.isRequestComplete()) {
/* 107 */       callback.handle(this.exchange, "");
/*     */       return;
/*     */     } 
/* 110 */     String contentLengthString = this.exchange.getRequestHeaders().getFirst(Headers.CONTENT_LENGTH);
/*     */ 
/*     */     
/* 113 */     if (contentLengthString != null) {
/* 114 */       contentLength = Long.parseLong(contentLengthString);
/* 115 */       if (contentLength > 2147483647L) {
/* 116 */         error.error(this.exchange, new Receiver.RequestToLargeException());
/*     */         return;
/*     */       } 
/* 119 */       sb = new ByteArrayOutputStream((int)contentLength);
/*     */     } else {
/* 121 */       contentLength = -1L;
/* 122 */       sb = new ByteArrayOutputStream();
/*     */     } 
/* 124 */     if (this.maxBufferSize > 0 && 
/* 125 */       contentLength > this.maxBufferSize) {
/* 126 */       error.error(this.exchange, new Receiver.RequestToLargeException());
/*     */       
/*     */       return;
/*     */     } 
/* 130 */     PooledByteBuffer pooled = this.exchange.getConnection().getByteBufferPool().allocate();
/* 131 */     ByteBuffer buffer = pooled.getBuffer();
/*     */ 
/*     */     
/*     */     try {
/*     */       while (true) {
/* 136 */         buffer.clear();
/* 137 */         int res = this.channel.read(buffer);
/* 138 */         if (res == -1) {
/* 139 */           this.done = true;
/* 140 */           callback.handle(this.exchange, sb.toString(charset.name())); return;
/*     */         } 
/* 142 */         if (res == 0) {
/* 143 */           this.channel.getReadSetter().set(new ChannelListener<StreamSourceChannel>()
/*     */               {
/*     */                 public void handleEvent(StreamSourceChannel channel) {
/* 146 */                   if (AsyncReceiverImpl.this.done) {
/*     */                     return;
/*     */                   }
/* 149 */                   PooledByteBuffer pooled = AsyncReceiverImpl.this.exchange.getConnection().getByteBufferPool().allocate();
/* 150 */                   ByteBuffer buffer = pooled.getBuffer();
/*     */ 
/*     */                   
/*     */                   try {
/*     */                     while (true) {
/* 155 */                       buffer.clear();
/* 156 */                       int res = channel.read(buffer);
/* 157 */                       if (res == -1) {
/* 158 */                         AsyncReceiverImpl.this.done = true;
/* 159 */                         Connectors.executeRootHandler(new HttpHandler()
/*     */                             {
/*     */                               public void handleRequest(HttpServerExchange exchange) throws Exception {
/* 162 */                                 callback.handle(exchange, sb.toString(charset.name()));
/*     */                               }
/* 164 */                             }AsyncReceiverImpl.this.exchange); return;
/*     */                       } 
/* 166 */                       if (res == 0) {
/*     */                         return;
/*     */                       }
/* 169 */                       buffer.flip();
/* 170 */                       while (buffer.hasRemaining()) {
/* 171 */                         sb.write(buffer.get());
/*     */                       }
/* 173 */                       if (AsyncReceiverImpl.this.maxBufferSize > 0 && sb.size() > AsyncReceiverImpl.this.maxBufferSize) {
/* 174 */                         Connectors.executeRootHandler(new HttpHandler()
/*     */                             {
/*     */                               public void handleRequest(HttpServerExchange exchange) throws Exception {
/* 177 */                                 error.error(exchange, new Receiver.RequestToLargeException());
/*     */                               }
/* 179 */                             }AsyncReceiverImpl.this.exchange);
/*     */                         return;
/*     */                       } 
/*     */                     } 
/* 183 */                   } catch (IOException e) {
/*     */                     
/* 185 */                     Connectors.executeRootHandler(new HttpHandler()
/*     */                         {
/*     */                           public void handleRequest(HttpServerExchange exchange) throws Exception {
/* 188 */                             error.error(exchange, e);
/*     */                           }
/* 190 */                         }AsyncReceiverImpl.this.exchange);
/*     */ 
/*     */                     
/*     */                     return;
/*     */                   } finally {
/* 195 */                     pooled.close();
/*     */                   } 
/*     */                 }
/*     */               });
/* 199 */           this.channel.resumeReads();
/*     */           return;
/*     */         } 
/* 202 */         buffer.flip();
/* 203 */         while (buffer.hasRemaining()) {
/* 204 */           sb.write(buffer.get());
/*     */         }
/* 206 */         if (this.maxBufferSize > 0 && sb.size() > this.maxBufferSize) {
/* 207 */           error.error(this.exchange, new Receiver.RequestToLargeException());
/*     */           return;
/*     */         } 
/*     */       } 
/* 211 */     } catch (IOException e) {
/* 212 */       error.error(this.exchange, e);
/*     */ 
/*     */       
/*     */       return;
/*     */     } finally {
/* 217 */       pooled.close();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void receiveFullString(Receiver.FullStringCallback callback, Charset charset) {
/* 224 */     receiveFullString(callback, END_EXCHANGE, charset);
/*     */   }
/*     */   
/*     */   public void receivePartialString(final Receiver.PartialStringCallback callback, Receiver.ErrorCallback errorCallback, Charset charset) {
/*     */     long contentLength;
/* 229 */     if (this.done) {
/* 230 */       throw UndertowMessages.MESSAGES.requestBodyAlreadyRead();
/*     */     }
/* 232 */     final Receiver.ErrorCallback error = (errorCallback == null) ? END_EXCHANGE : errorCallback;
/* 233 */     if (callback == null) {
/* 234 */       throw UndertowMessages.MESSAGES.argumentCannotBeNull("callback");
/*     */     }
/* 236 */     if (this.exchange.isRequestComplete()) {
/* 237 */       callback.handle(this.exchange, "", true);
/*     */       return;
/*     */     } 
/* 240 */     String contentLengthString = this.exchange.getRequestHeaders().getFirst(Headers.CONTENT_LENGTH);
/*     */     
/* 242 */     if (contentLengthString != null) {
/* 243 */       contentLength = Long.parseLong(contentLengthString);
/* 244 */       if (contentLength > 2147483647L) {
/* 245 */         error.error(this.exchange, new Receiver.RequestToLargeException());
/*     */         return;
/*     */       } 
/*     */     } else {
/* 249 */       contentLength = -1L;
/*     */     } 
/* 251 */     if (this.maxBufferSize > 0 && 
/* 252 */       contentLength > this.maxBufferSize) {
/* 253 */       error.error(this.exchange, new Receiver.RequestToLargeException());
/*     */       
/*     */       return;
/*     */     } 
/* 257 */     final CharsetDecoder decoder = charset.newDecoder();
/* 258 */     PooledByteBuffer pooled = this.exchange.getConnection().getByteBufferPool().allocate();
/* 259 */     ByteBuffer buffer = pooled.getBuffer();
/* 260 */     this.channel.getReadSetter().set(new ChannelListener<StreamSourceChannel>()
/*     */         {
/*     */           public void handleEvent(final StreamSourceChannel channel) {
/* 263 */             if (AsyncReceiverImpl.this.done || AsyncReceiverImpl.this.paused) {
/*     */               return;
/*     */             }
/* 266 */             PooledByteBuffer pooled = AsyncReceiverImpl.this.exchange.getConnection().getByteBufferPool().allocate();
/* 267 */             ByteBuffer buffer = pooled.getBuffer();
/*     */             
/*     */             try {
/*     */               while (true) {
/* 271 */                 if (AsyncReceiverImpl.this.paused) {
/*     */                   return;
/*     */                 }
/*     */                 try {
/* 275 */                   buffer.clear();
/* 276 */                   int res = channel.read(buffer);
/* 277 */                   if (res == -1) {
/* 278 */                     AsyncReceiverImpl.this.done = true;
/* 279 */                     Connectors.executeRootHandler(new HttpHandler()
/*     */                         {
/*     */                           public void handleRequest(HttpServerExchange exchange) throws Exception {
/* 282 */                             callback.handle(exchange, "", true);
/*     */                           }
/* 284 */                         }AsyncReceiverImpl.this.exchange); return;
/*     */                   } 
/* 286 */                   if (res == 0) {
/*     */                     return;
/*     */                   }
/* 289 */                   buffer.flip();
/* 290 */                   final CharBuffer cb = decoder.decode(buffer);
/* 291 */                   Connectors.executeRootHandler(new HttpHandler()
/*     */                       {
/*     */                         public void handleRequest(HttpServerExchange exchange) throws Exception {
/* 294 */                           callback.handle(exchange, cb.toString(), false);
/* 295 */                           if (!AsyncReceiverImpl.this.paused) {
/* 296 */                             channel.resumeReads();
/*     */                           } else {
/* 298 */                             System.out.println("paused");
/*     */                           } 
/*     */                         }
/* 301 */                       }AsyncReceiverImpl.this.exchange);
/*     */                 }
/* 303 */                 catch (IOException e) {
/* 304 */                   Connectors.executeRootHandler(new HttpHandler()
/*     */                       {
/*     */                         public void handleRequest(HttpServerExchange exchange) throws Exception {
/* 307 */                           error.error(exchange, e);
/*     */                         }
/* 309 */                       }AsyncReceiverImpl.this.exchange);
/*     */                   return;
/*     */                 } 
/*     */               } 
/*     */             } finally {
/* 314 */               pooled.close();
/*     */             } 
/*     */           }
/*     */         });
/*     */ 
/*     */     
/*     */     try {
/*     */       do {
/* 322 */         buffer.clear();
/* 323 */         int res = this.channel.read(buffer);
/* 324 */         if (res == -1) {
/* 325 */           this.done = true;
/* 326 */           callback.handle(this.exchange, "", true); return;
/*     */         } 
/* 328 */         if (res == 0) {
/* 329 */           this.channel.resumeReads();
/*     */           return;
/*     */         } 
/* 332 */         buffer.flip();
/* 333 */         CharBuffer cb = decoder.decode(buffer);
/* 334 */         callback.handle(this.exchange, cb.toString(), false);
/* 335 */       } while (!this.paused);
/*     */ 
/*     */       
/*     */       return;
/* 339 */     } catch (IOException e) {
/* 340 */       error.error(this.exchange, e);
/*     */ 
/*     */       
/*     */       return;
/*     */     } finally {
/* 345 */       pooled.close();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void receivePartialString(Receiver.PartialStringCallback callback, Charset charset) {
/* 352 */     receivePartialString(callback, END_EXCHANGE, charset);
/*     */   }
/*     */   
/*     */   public void receiveFullBytes(final Receiver.FullBytesCallback callback, Receiver.ErrorCallback errorCallback) {
/*     */     long contentLength;
/*     */     final ByteArrayOutputStream sb;
/* 358 */     if (this.done) {
/* 359 */       throw UndertowMessages.MESSAGES.requestBodyAlreadyRead();
/*     */     }
/* 361 */     final Receiver.ErrorCallback error = (errorCallback == null) ? END_EXCHANGE : errorCallback;
/* 362 */     if (callback == null) {
/* 363 */       throw UndertowMessages.MESSAGES.argumentCannotBeNull("callback");
/*     */     }
/* 365 */     if (this.exchange.isRequestComplete()) {
/* 366 */       callback.handle(this.exchange, EMPTY_BYTE_ARRAY);
/*     */       return;
/*     */     } 
/* 369 */     String contentLengthString = this.exchange.getRequestHeaders().getFirst(Headers.CONTENT_LENGTH);
/*     */ 
/*     */     
/* 372 */     if (contentLengthString != null) {
/* 373 */       contentLength = Long.parseLong(contentLengthString);
/* 374 */       if (contentLength > 2147483647L) {
/* 375 */         error.error(this.exchange, new Receiver.RequestToLargeException());
/*     */         return;
/*     */       } 
/* 378 */       sb = new ByteArrayOutputStream((int)contentLength);
/*     */     } else {
/* 380 */       contentLength = -1L;
/* 381 */       sb = new ByteArrayOutputStream();
/*     */     } 
/* 383 */     if (this.maxBufferSize > 0 && 
/* 384 */       contentLength > this.maxBufferSize) {
/* 385 */       error.error(this.exchange, new Receiver.RequestToLargeException());
/*     */       
/*     */       return;
/*     */     } 
/* 389 */     PooledByteBuffer pooled = this.exchange.getConnection().getByteBufferPool().allocate();
/* 390 */     ByteBuffer buffer = pooled.getBuffer();
/*     */ 
/*     */     
/*     */     try {
/*     */       while (true) {
/* 395 */         buffer.clear();
/* 396 */         int res = this.channel.read(buffer);
/* 397 */         if (res == -1) {
/* 398 */           this.done = true;
/* 399 */           callback.handle(this.exchange, sb.toByteArray()); return;
/*     */         } 
/* 401 */         if (res == 0) {
/* 402 */           this.channel.getReadSetter().set(new ChannelListener<StreamSourceChannel>()
/*     */               {
/*     */                 public void handleEvent(StreamSourceChannel channel) {
/* 405 */                   if (AsyncReceiverImpl.this.done) {
/*     */                     return;
/*     */                   }
/* 408 */                   PooledByteBuffer pooled = AsyncReceiverImpl.this.exchange.getConnection().getByteBufferPool().allocate();
/* 409 */                   ByteBuffer buffer = pooled.getBuffer();
/*     */ 
/*     */                   
/*     */                   try {
/*     */                     while (true) {
/* 414 */                       buffer.clear();
/* 415 */                       int res = channel.read(buffer);
/* 416 */                       if (res == -1) {
/* 417 */                         AsyncReceiverImpl.this.done = true;
/* 418 */                         Connectors.executeRootHandler(new HttpHandler()
/*     */                             {
/*     */                               public void handleRequest(HttpServerExchange exchange) throws Exception {
/* 421 */                                 callback.handle(exchange, sb.toByteArray());
/*     */                               }
/* 423 */                             }AsyncReceiverImpl.this.exchange); return;
/*     */                       } 
/* 425 */                       if (res == 0) {
/*     */                         return;
/*     */                       }
/* 428 */                       buffer.flip();
/* 429 */                       while (buffer.hasRemaining()) {
/* 430 */                         sb.write(buffer.get());
/*     */                       }
/* 432 */                       if (AsyncReceiverImpl.this.maxBufferSize > 0 && sb.size() > AsyncReceiverImpl.this.maxBufferSize) {
/* 433 */                         Connectors.executeRootHandler(new HttpHandler()
/*     */                             {
/*     */                               public void handleRequest(HttpServerExchange exchange) throws Exception {
/* 436 */                                 error.error(exchange, new Receiver.RequestToLargeException());
/*     */                               }
/* 438 */                             }AsyncReceiverImpl.this.exchange);
/*     */                         return;
/*     */                       } 
/*     */                     } 
/* 442 */                   } catch (Exception e) {
/* 443 */                     Connectors.executeRootHandler(new HttpHandler()
/*     */                         {
/*     */                           public void handleRequest(HttpServerExchange exchange) throws Exception {
/* 446 */                             error.error(exchange, new IOException(e));
/*     */                           }
/* 448 */                         }AsyncReceiverImpl.this.exchange);
/*     */ 
/*     */                     
/*     */                     return;
/*     */                   } finally {
/* 453 */                     pooled.close();
/*     */                   } 
/*     */                 }
/*     */               });
/* 457 */           this.channel.resumeReads();
/*     */           return;
/*     */         } 
/* 460 */         buffer.flip();
/* 461 */         while (buffer.hasRemaining()) {
/* 462 */           sb.write(buffer.get());
/*     */         }
/* 464 */         if (this.maxBufferSize > 0 && sb.size() > this.maxBufferSize) {
/* 465 */           error.error(this.exchange, new Receiver.RequestToLargeException());
/*     */           return;
/*     */         } 
/*     */       } 
/* 469 */     } catch (IOException e) {
/* 470 */       error.error(this.exchange, e);
/*     */ 
/*     */       
/*     */       return;
/*     */     } finally {
/* 475 */       pooled.close();
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void receiveFullBytes(Receiver.FullBytesCallback callback) {
/* 481 */     receiveFullBytes(callback, END_EXCHANGE);
/*     */   }
/*     */   
/*     */   public void receivePartialBytes(final Receiver.PartialBytesCallback callback, Receiver.ErrorCallback errorCallback) {
/*     */     long contentLength;
/* 486 */     if (this.done) {
/* 487 */       throw UndertowMessages.MESSAGES.requestBodyAlreadyRead();
/*     */     }
/* 489 */     final Receiver.ErrorCallback error = (errorCallback == null) ? END_EXCHANGE : errorCallback;
/* 490 */     if (callback == null) {
/* 491 */       throw UndertowMessages.MESSAGES.argumentCannotBeNull("callback");
/*     */     }
/* 493 */     if (this.exchange.isRequestComplete()) {
/* 494 */       callback.handle(this.exchange, EMPTY_BYTE_ARRAY, true);
/*     */       return;
/*     */     } 
/* 497 */     String contentLengthString = this.exchange.getRequestHeaders().getFirst(Headers.CONTENT_LENGTH);
/*     */     
/* 499 */     if (contentLengthString != null) {
/* 500 */       contentLength = Long.parseLong(contentLengthString);
/* 501 */       if (contentLength > 2147483647L) {
/* 502 */         error.error(this.exchange, new Receiver.RequestToLargeException());
/*     */         return;
/*     */       } 
/*     */     } else {
/* 506 */       contentLength = -1L;
/*     */     } 
/* 508 */     if (this.maxBufferSize > 0 && 
/* 509 */       contentLength > this.maxBufferSize) {
/* 510 */       error.error(this.exchange, new Receiver.RequestToLargeException());
/*     */       
/*     */       return;
/*     */     } 
/* 514 */     PooledByteBuffer pooled = this.exchange.getConnection().getByteBufferPool().allocate();
/* 515 */     ByteBuffer buffer = pooled.getBuffer();
/* 516 */     this.channel.getReadSetter().set(new ChannelListener<StreamSourceChannel>()
/*     */         {
/*     */           public void handleEvent(final StreamSourceChannel channel) {
/* 519 */             if (AsyncReceiverImpl.this.done || AsyncReceiverImpl.this.paused) {
/*     */               return;
/*     */             }
/* 522 */             PooledByteBuffer pooled = AsyncReceiverImpl.this.exchange.getConnection().getByteBufferPool().allocate();
/* 523 */             ByteBuffer buffer = pooled.getBuffer();
/*     */             
/*     */             try {
/*     */               while (true) {
/* 527 */                 if (AsyncReceiverImpl.this.paused) {
/*     */                   return;
/*     */                 }
/*     */                 try {
/* 531 */                   buffer.clear();
/* 532 */                   int res = channel.read(buffer);
/* 533 */                   if (res == -1) {
/* 534 */                     AsyncReceiverImpl.this.done = true;
/* 535 */                     Connectors.executeRootHandler(new HttpHandler()
/*     */                         {
/*     */                           public void handleRequest(HttpServerExchange exchange) throws Exception {
/* 538 */                             callback.handle(exchange, AsyncReceiverImpl.EMPTY_BYTE_ARRAY, true);
/*     */                           }
/* 540 */                         }AsyncReceiverImpl.this.exchange); return;
/*     */                   } 
/* 542 */                   if (res == 0) {
/*     */                     return;
/*     */                   }
/* 545 */                   buffer.flip();
/* 546 */                   final byte[] data = new byte[buffer.remaining()];
/* 547 */                   buffer.get(data);
/*     */                   
/* 549 */                   Connectors.executeRootHandler(new HttpHandler()
/*     */                       {
/*     */                         public void handleRequest(HttpServerExchange exchange) throws Exception {
/* 552 */                           callback.handle(exchange, data, false);
/* 553 */                           if (!AsyncReceiverImpl.this.paused) {
/* 554 */                             channel.resumeReads();
/*     */                           }
/*     */                         }
/* 557 */                       }AsyncReceiverImpl.this.exchange);
/*     */                 }
/* 559 */                 catch (IOException e) {
/* 560 */                   Connectors.executeRootHandler(new HttpHandler()
/*     */                       {
/*     */                         public void handleRequest(HttpServerExchange exchange) throws Exception {
/* 563 */                           error.error(exchange, e);
/*     */                         }
/* 565 */                       }AsyncReceiverImpl.this.exchange);
/*     */                   return;
/*     */                 } 
/*     */               } 
/*     */             } finally {
/* 570 */               pooled.close();
/*     */             } 
/*     */           }
/*     */         });
/*     */ 
/*     */     
/*     */     try {
/*     */       do {
/* 578 */         buffer.clear();
/* 579 */         int res = this.channel.read(buffer);
/* 580 */         if (res == -1) {
/* 581 */           this.done = true;
/* 582 */           callback.handle(this.exchange, EMPTY_BYTE_ARRAY, true); return;
/*     */         } 
/* 584 */         if (res == 0) {
/*     */           
/* 586 */           this.channel.resumeReads();
/*     */           return;
/*     */         } 
/* 589 */         buffer.flip();
/* 590 */         byte[] data = new byte[buffer.remaining()];
/* 591 */         buffer.get(data);
/* 592 */         callback.handle(this.exchange, data, false);
/* 593 */       } while (!this.paused);
/*     */ 
/*     */       
/*     */       return;
/* 597 */     } catch (IOException e) {
/* 598 */       error.error(this.exchange, e);
/*     */ 
/*     */       
/*     */       return;
/*     */     } finally {
/* 603 */       pooled.close();
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void receivePartialBytes(Receiver.PartialBytesCallback callback) {
/* 609 */     receivePartialBytes(callback, END_EXCHANGE);
/*     */   }
/*     */ 
/*     */   
/*     */   public void pause() {
/* 614 */     this.paused = true;
/* 615 */     this.channel.suspendReads();
/*     */   }
/*     */ 
/*     */   
/*     */   public void resume() {
/* 620 */     this.paused = false;
/* 621 */     this.channel.wakeupReads();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\io\AsyncReceiverImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */