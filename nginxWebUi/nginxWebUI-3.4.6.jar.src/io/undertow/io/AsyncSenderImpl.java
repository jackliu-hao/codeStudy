/*     */ package io.undertow.io;
/*     */ 
/*     */ import io.undertow.UndertowLogger;
/*     */ import io.undertow.UndertowMessages;
/*     */ import io.undertow.connector.PooledByteBuffer;
/*     */ import io.undertow.server.HttpServerExchange;
/*     */ import io.undertow.util.Headers;
/*     */ import java.io.Closeable;
/*     */ import java.io.IOException;
/*     */ import java.nio.Buffer;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.channels.Channel;
/*     */ import java.nio.channels.ClosedChannelException;
/*     */ import java.nio.channels.FileChannel;
/*     */ import java.nio.charset.Charset;
/*     */ import java.nio.charset.StandardCharsets;
/*     */ import org.xnio.Buffers;
/*     */ import org.xnio.ChannelExceptionHandler;
/*     */ import org.xnio.ChannelListener;
/*     */ import org.xnio.ChannelListeners;
/*     */ import org.xnio.IoUtils;
/*     */ import org.xnio.channels.StreamSinkChannel;
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
/*     */ public class AsyncSenderImpl
/*     */   implements Sender
/*     */ {
/*     */   private StreamSinkChannel channel;
/*     */   private final HttpServerExchange exchange;
/*  48 */   private PooledByteBuffer[] pooledBuffers = null;
/*     */ 
/*     */   
/*     */   private FileChannel fileChannel;
/*     */ 
/*     */   
/*     */   private IoCallback callback;
/*     */ 
/*     */   
/*     */   private ByteBuffer[] buffer;
/*     */ 
/*     */   
/*     */   private volatile Thread writeThread;
/*     */ 
/*     */   
/*     */   private volatile Thread inCallback;
/*     */ 
/*     */   
/*     */   private ChannelListener<StreamSinkChannel> writeListener;
/*     */   
/*     */   private TransferTask transferTask;
/*     */ 
/*     */   
/*     */   public class TransferTask
/*     */     implements Runnable, ChannelListener<StreamSinkChannel>
/*     */   {
/*     */     public boolean run(boolean complete) {
/*     */       try {
/*  76 */         FileChannel source = AsyncSenderImpl.this.fileChannel;
/*  77 */         long pos = source.position();
/*  78 */         long size = source.size();
/*     */         
/*  80 */         StreamSinkChannel dest = AsyncSenderImpl.this.channel;
/*  81 */         if (dest == null) {
/*  82 */           if (AsyncSenderImpl.this.callback == IoCallback.END_EXCHANGE && 
/*  83 */             AsyncSenderImpl.this.exchange.getResponseContentLength() == -1L && !AsyncSenderImpl.this.exchange.getResponseHeaders().contains(Headers.TRANSFER_ENCODING)) {
/*  84 */             AsyncSenderImpl.this.exchange.setResponseContentLength(size);
/*     */           }
/*     */           
/*  87 */           AsyncSenderImpl.this.channel = dest = AsyncSenderImpl.this.exchange.getResponseChannel();
/*  88 */           if (dest == null) {
/*  89 */             throw UndertowMessages.MESSAGES.responseChannelAlreadyProvided();
/*     */           }
/*     */         } 
/*     */         
/*  93 */         while (size - pos > 0L) {
/*  94 */           long ret = dest.transferFrom(source, pos, size - pos);
/*  95 */           pos += ret;
/*  96 */           if (ret == 0L) {
/*  97 */             source.position(pos);
/*  98 */             dest.getWriteSetter().set(this);
/*  99 */             dest.resumeWrites();
/* 100 */             return false;
/*     */           } 
/*     */         } 
/*     */         
/* 104 */         if (complete) {
/* 105 */           AsyncSenderImpl.this.invokeOnComplete();
/*     */         }
/* 107 */       } catch (IOException e) {
/* 108 */         AsyncSenderImpl.this.invokeOnException(AsyncSenderImpl.this.callback, e);
/*     */       } 
/*     */       
/* 111 */       return true;
/*     */     }
/*     */ 
/*     */     
/*     */     public void handleEvent(StreamSinkChannel channel) {
/* 116 */       channel.suspendWrites();
/* 117 */       channel.getWriteSetter().set(null);
/* 118 */       AsyncSenderImpl.this.exchange.dispatch(this);
/*     */     }
/*     */ 
/*     */     
/*     */     public void run() {
/* 123 */       run(true);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public AsyncSenderImpl(HttpServerExchange exchange) {
/* 131 */     this.exchange = exchange;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void send(ByteBuffer buffer, IoCallback callback) {
/* 137 */     this.writeThread = Thread.currentThread();
/* 138 */     if (callback == null) {
/* 139 */       throw UndertowMessages.MESSAGES.argumentCannotBeNull("callback");
/*     */     }
/* 141 */     if (!this.exchange.getConnection().isOpen()) {
/* 142 */       invokeOnException(callback, new ClosedChannelException());
/*     */       return;
/*     */     } 
/* 145 */     if (this.exchange.isResponseComplete()) {
/* 146 */       invokeOnException(callback, new IOException(UndertowMessages.MESSAGES.responseComplete()));
/*     */     }
/* 148 */     if (this.buffer != null || this.fileChannel != null) {
/* 149 */       throw UndertowMessages.MESSAGES.dataAlreadyQueued();
/*     */     }
/* 151 */     long responseContentLength = this.exchange.getResponseContentLength();
/* 152 */     if (responseContentLength > 0L && buffer.remaining() > responseContentLength) {
/* 153 */       invokeOnException(callback, UndertowLogger.ROOT_LOGGER.dataLargerThanContentLength(buffer.remaining(), responseContentLength));
/*     */       return;
/*     */     } 
/* 156 */     StreamSinkChannel channel = this.channel;
/* 157 */     if (channel == null) {
/* 158 */       if (callback == IoCallback.END_EXCHANGE && 
/* 159 */         responseContentLength == -1L && !this.exchange.getResponseHeaders().contains(Headers.TRANSFER_ENCODING)) {
/* 160 */         this.exchange.setResponseContentLength(buffer.remaining());
/*     */       }
/*     */       
/* 163 */       this.channel = channel = this.exchange.getResponseChannel();
/* 164 */       if (channel == null) {
/* 165 */         throw UndertowMessages.MESSAGES.responseChannelAlreadyProvided();
/*     */       }
/*     */     } 
/* 168 */     this.callback = callback;
/* 169 */     if (this.inCallback == Thread.currentThread()) {
/* 170 */       this.buffer = new ByteBuffer[] { buffer };
/*     */       return;
/*     */     } 
/*     */     try {
/*     */       while (true) {
/* 175 */         if (buffer.remaining() == 0) {
/* 176 */           callback.onComplete(this.exchange, this);
/*     */           return;
/*     */         } 
/* 179 */         int res = channel.write(buffer);
/* 180 */         if (res == 0) {
/* 181 */           this.buffer = new ByteBuffer[] { buffer };
/* 182 */           this.callback = callback;
/*     */           
/* 184 */           if (this.writeListener == null) {
/* 185 */             initWriteListener();
/*     */           }
/* 187 */           channel.getWriteSetter().set(this.writeListener);
/* 188 */           channel.resumeWrites();
/*     */           return;
/*     */         } 
/* 191 */         if (!buffer.hasRemaining())
/* 192 */         { invokeOnComplete(); return; } 
/*     */       } 
/* 194 */     } catch (IOException e) {
/*     */       
/* 196 */       invokeOnException(callback, e);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void send(ByteBuffer[] buffer, IoCallback callback) {
/* 202 */     this.writeThread = Thread.currentThread();
/* 203 */     if (callback == null) {
/* 204 */       throw UndertowMessages.MESSAGES.argumentCannotBeNull("callback");
/*     */     }
/*     */     
/* 207 */     if (!this.exchange.getConnection().isOpen()) {
/* 208 */       invokeOnException(callback, new ClosedChannelException());
/*     */       return;
/*     */     } 
/* 211 */     if (this.exchange.isResponseComplete()) {
/* 212 */       invokeOnException(callback, new IOException(UndertowMessages.MESSAGES.responseComplete()));
/*     */     }
/* 214 */     if (this.buffer != null) {
/* 215 */       throw UndertowMessages.MESSAGES.dataAlreadyQueued();
/*     */     }
/* 217 */     this.callback = callback;
/* 218 */     if (this.inCallback == Thread.currentThread()) {
/* 219 */       this.buffer = buffer;
/*     */       
/*     */       return;
/*     */     } 
/* 223 */     long totalToWrite = Buffers.remaining((Buffer[])buffer);
/* 224 */     long responseContentLength = this.exchange.getResponseContentLength();
/* 225 */     if (responseContentLength > 0L && totalToWrite > responseContentLength) {
/* 226 */       invokeOnException(callback, UndertowLogger.ROOT_LOGGER.dataLargerThanContentLength(totalToWrite, responseContentLength));
/*     */       
/*     */       return;
/*     */     } 
/* 230 */     StreamSinkChannel channel = this.channel;
/* 231 */     if (channel == null) {
/* 232 */       if (callback == IoCallback.END_EXCHANGE && 
/* 233 */         responseContentLength == -1L && !this.exchange.getResponseHeaders().contains(Headers.TRANSFER_ENCODING)) {
/* 234 */         this.exchange.setResponseContentLength(totalToWrite);
/*     */       }
/*     */       
/* 237 */       this.channel = channel = this.exchange.getResponseChannel();
/* 238 */       if (channel == null) {
/* 239 */         throw UndertowMessages.MESSAGES.responseChannelAlreadyProvided();
/*     */       }
/*     */     } 
/*     */     
/* 243 */     long total = totalToWrite;
/* 244 */     long written = 0L;
/*     */     
/*     */     try {
/*     */       while (true) {
/* 248 */         long res = channel.write(buffer);
/* 249 */         written += res;
/* 250 */         if (res == 0L) {
/* 251 */           this.buffer = buffer;
/* 252 */           this.callback = callback;
/*     */           
/* 254 */           if (this.writeListener == null) {
/* 255 */             initWriteListener();
/*     */           }
/* 257 */           channel.getWriteSetter().set(this.writeListener);
/* 258 */           channel.resumeWrites();
/*     */           return;
/*     */         } 
/* 261 */         if (written >= total)
/* 262 */         { invokeOnComplete(); return; } 
/*     */       } 
/* 264 */     } catch (IOException e) {
/* 265 */       invokeOnException(callback, e);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void transferFrom(FileChannel source, IoCallback callback) {
/* 272 */     this.writeThread = Thread.currentThread();
/* 273 */     if (callback == null) {
/* 274 */       throw UndertowMessages.MESSAGES.argumentCannotBeNull("callback");
/*     */     }
/*     */     
/* 277 */     if (!this.exchange.getConnection().isOpen()) {
/* 278 */       invokeOnException(callback, new ClosedChannelException());
/*     */       return;
/*     */     } 
/* 281 */     if (this.exchange.isResponseComplete()) {
/* 282 */       invokeOnException(callback, new IOException(UndertowMessages.MESSAGES.responseComplete()));
/*     */     }
/* 284 */     if (this.fileChannel != null || this.buffer != null) {
/* 285 */       throw UndertowMessages.MESSAGES.dataAlreadyQueued();
/*     */     }
/*     */     
/* 288 */     this.callback = callback;
/* 289 */     this.fileChannel = source;
/* 290 */     if (this.inCallback == Thread.currentThread()) {
/*     */       return;
/*     */     }
/* 293 */     if (this.transferTask == null) {
/* 294 */       this.transferTask = new TransferTask();
/*     */     }
/* 296 */     if (this.exchange.isInIoThread()) {
/* 297 */       this.exchange.dispatch(this.transferTask);
/*     */       
/*     */       return;
/*     */     } 
/* 301 */     this.transferTask.run();
/*     */   }
/*     */ 
/*     */   
/*     */   public void send(ByteBuffer buffer) {
/* 306 */     send(buffer, IoCallback.END_EXCHANGE);
/*     */   }
/*     */ 
/*     */   
/*     */   public void send(ByteBuffer[] buffer) {
/* 311 */     send(buffer, IoCallback.END_EXCHANGE);
/*     */   }
/*     */ 
/*     */   
/*     */   public void send(String data, IoCallback callback) {
/* 316 */     send(data, StandardCharsets.UTF_8, callback);
/*     */   }
/*     */ 
/*     */   
/*     */   public void send(String data, Charset charset, IoCallback callback) {
/* 321 */     this.writeThread = Thread.currentThread();
/* 322 */     if (!this.exchange.getConnection().isOpen()) {
/* 323 */       invokeOnException(callback, new ClosedChannelException());
/*     */       return;
/*     */     } 
/* 326 */     if (this.exchange.isResponseComplete()) {
/* 327 */       invokeOnException(callback, new IOException(UndertowMessages.MESSAGES.responseComplete()));
/*     */     }
/* 329 */     ByteBuffer bytes = ByteBuffer.wrap(data.getBytes(charset));
/* 330 */     if (bytes.remaining() == 0) {
/* 331 */       callback.onComplete(this.exchange, this);
/*     */     } else {
/* 333 */       int i = 0;
/* 334 */       ByteBuffer[] bufs = null;
/* 335 */       while (bytes.hasRemaining()) {
/* 336 */         PooledByteBuffer pooled = this.exchange.getConnection().getByteBufferPool().allocate();
/* 337 */         if (bufs == null) {
/* 338 */           int noBufs = (bytes.remaining() + pooled.getBuffer().remaining() - 1) / pooled.getBuffer().remaining();
/* 339 */           this.pooledBuffers = new PooledByteBuffer[noBufs];
/* 340 */           bufs = new ByteBuffer[noBufs];
/*     */         } 
/* 342 */         this.pooledBuffers[i] = pooled;
/* 343 */         bufs[i] = pooled.getBuffer();
/* 344 */         Buffers.copy(pooled.getBuffer(), bytes);
/* 345 */         pooled.getBuffer().flip();
/* 346 */         i++;
/*     */       } 
/* 348 */       send(bufs, callback);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void send(String data) {
/* 354 */     send(data, IoCallback.END_EXCHANGE);
/*     */   }
/*     */ 
/*     */   
/*     */   public void send(String data, Charset charset) {
/* 359 */     send(data, charset, IoCallback.END_EXCHANGE);
/*     */   }
/*     */ 
/*     */   
/*     */   public void close(final IoCallback callback) {
/*     */     try {
/* 365 */       StreamSinkChannel channel = this.channel;
/* 366 */       if (channel == null) {
/* 367 */         if (this.exchange.getResponseContentLength() == -1L && !this.exchange.getResponseHeaders().contains(Headers.TRANSFER_ENCODING)) {
/* 368 */           this.exchange.setResponseContentLength(0L);
/*     */         }
/* 370 */         this.channel = channel = this.exchange.getResponseChannel();
/* 371 */         if (channel == null) {
/* 372 */           throw UndertowMessages.MESSAGES.responseChannelAlreadyProvided();
/*     */         }
/*     */       } 
/* 375 */       channel.shutdownWrites();
/* 376 */       if (!channel.flush()) {
/* 377 */         channel.getWriteSetter().set(ChannelListeners.flushingChannelListener(new ChannelListener<StreamSinkChannel>()
/*     */               {
/*     */                 public void handleEvent(StreamSinkChannel channel)
/*     */                 {
/* 381 */                   if (callback != null) {
/* 382 */                     callback.onComplete(AsyncSenderImpl.this.exchange, AsyncSenderImpl.this);
/*     */                   }
/*     */                 }
/*     */               }new ChannelExceptionHandler<StreamSinkChannel>()
/*     */               {
/*     */                 public void handleException(StreamSinkChannel channel, IOException exception) {
/*     */                   try {
/* 389 */                     if (callback != null) {
/* 390 */                       AsyncSenderImpl.this.invokeOnException(callback, exception);
/*     */                     }
/*     */                   } finally {
/* 393 */                     IoUtils.safeClose((Closeable)channel);
/*     */                   } 
/*     */                 }
/*     */               }));
/*     */         
/* 398 */         channel.resumeWrites();
/*     */       }
/* 400 */       else if (callback != null) {
/* 401 */         callback.onComplete(this.exchange, this);
/*     */       }
/*     */     
/* 404 */     } catch (IOException e) {
/* 405 */       if (callback != null) {
/* 406 */         invokeOnException(callback, e);
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void close() {
/* 413 */     close(null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void invokeOnComplete() {
/*     */     label39: while (true) {
/* 422 */       if (this.pooledBuffers != null) {
/* 423 */         for (PooledByteBuffer buffer : this.pooledBuffers) {
/* 424 */           buffer.close();
/*     */         }
/* 426 */         this.pooledBuffers = null;
/*     */       } 
/* 428 */       IoCallback callback = this.callback;
/* 429 */       this.buffer = null;
/* 430 */       this.fileChannel = null;
/* 431 */       this.callback = null;
/* 432 */       this.writeThread = null;
/* 433 */       this.inCallback = Thread.currentThread();
/*     */       
/* 435 */       try { callback.onComplete(this.exchange, this);
/*     */         
/* 437 */         this.inCallback = null; } finally { this.inCallback = null; }
/*     */       
/* 439 */       if (Thread.currentThread() != this.writeThread) {
/*     */         return;
/*     */       }
/*     */       
/* 443 */       StreamSinkChannel channel = this.channel;
/* 444 */       if (this.buffer != null) {
/* 445 */         long t = Buffers.remaining((Buffer[])this.buffer);
/* 446 */         long total = t;
/* 447 */         long written = 0L;
/*     */         
/*     */         try {
/*     */           while (true)
/* 451 */           { long res = channel.write(this.buffer);
/* 452 */             written += res;
/* 453 */             if (res == 0L) {
/* 454 */               if (this.writeListener == null) {
/* 455 */                 initWriteListener();
/*     */               }
/* 457 */               channel.getWriteSetter().set(this.writeListener);
/* 458 */               channel.resumeWrites();
/*     */               return;
/*     */             } 
/* 461 */             if (written >= total)
/*     */               continue label39;  } 
/* 463 */         } catch (IOException e) {
/* 464 */           invokeOnException(callback, e); continue;
/*     */         } 
/* 466 */       }  if (this.fileChannel != null) {
/* 467 */         if (this.transferTask == null) {
/* 468 */           this.transferTask = new TransferTask();
/*     */         }
/* 470 */         if (!this.transferTask.run(false)) {
/*     */           return;
/*     */         }
/*     */         continue;
/*     */       } 
/*     */       break;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void invokeOnException(IoCallback callback, IOException e) {
/* 483 */     if (this.pooledBuffers != null) {
/* 484 */       for (PooledByteBuffer buffer : this.pooledBuffers) {
/* 485 */         buffer.close();
/*     */       }
/* 487 */       this.pooledBuffers = null;
/*     */     } 
/* 489 */     callback.onException(this.exchange, this, e);
/*     */   }
/*     */   
/*     */   private void initWriteListener() {
/* 493 */     this.writeListener = new ChannelListener<StreamSinkChannel>()
/*     */       {
/*     */         public void handleEvent(StreamSinkChannel streamSinkChannel) {
/*     */           try {
/* 497 */             long toWrite = Buffers.remaining((Buffer[])AsyncSenderImpl.this.buffer);
/* 498 */             long written = 0L;
/* 499 */             while (written < toWrite) {
/* 500 */               long res = streamSinkChannel.write(AsyncSenderImpl.this.buffer, 0, AsyncSenderImpl.this.buffer.length);
/* 501 */               written += res;
/* 502 */               if (res == 0L) {
/*     */                 return;
/*     */               }
/*     */             } 
/* 506 */             streamSinkChannel.suspendWrites();
/* 507 */             AsyncSenderImpl.this.invokeOnComplete();
/* 508 */           } catch (IOException e) {
/* 509 */             streamSinkChannel.suspendWrites();
/* 510 */             AsyncSenderImpl.this.invokeOnException(AsyncSenderImpl.this.callback, e);
/*     */           } 
/*     */         }
/*     */       };
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\io\AsyncSenderImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */