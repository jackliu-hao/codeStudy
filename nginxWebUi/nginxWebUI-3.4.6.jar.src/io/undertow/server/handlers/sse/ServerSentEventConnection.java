/*     */ package io.undertow.server.handlers.sse;
/*     */ 
/*     */ import io.undertow.UndertowLogger;
/*     */ import io.undertow.connector.PooledByteBuffer;
/*     */ import io.undertow.security.api.SecurityContext;
/*     */ import io.undertow.security.idm.Account;
/*     */ import io.undertow.server.HttpServerExchange;
/*     */ import io.undertow.util.Attachable;
/*     */ import io.undertow.util.AttachmentKey;
/*     */ import io.undertow.util.AttachmentList;
/*     */ import io.undertow.util.HeaderMap;
/*     */ import java.io.Closeable;
/*     */ import java.io.IOException;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.channels.Channel;
/*     */ import java.nio.channels.ClosedChannelException;
/*     */ import java.nio.charset.StandardCharsets;
/*     */ import java.security.Principal;
/*     */ import java.util.ArrayDeque;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Deque;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Queue;
/*     */ import java.util.concurrent.ConcurrentLinkedDeque;
/*     */ import java.util.concurrent.CopyOnWriteArrayList;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
/*     */ import org.xnio.ChannelExceptionHandler;
/*     */ import org.xnio.ChannelListener;
/*     */ import org.xnio.ChannelListeners;
/*     */ import org.xnio.IoUtils;
/*     */ import org.xnio.XnioExecutor;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ServerSentEventConnection
/*     */   implements Channel, Attachable
/*     */ {
/*     */   private final HttpServerExchange exchange;
/*     */   private final StreamSinkChannel sink;
/*  66 */   private final SseWriteListener writeListener = new SseWriteListener();
/*     */   
/*     */   private PooledByteBuffer pooled;
/*     */   
/*  70 */   private final Deque<SSEData> queue = new ConcurrentLinkedDeque<>();
/*  71 */   private final Queue<SSEData> buffered = new ConcurrentLinkedDeque<>();
/*     */ 
/*     */ 
/*     */   
/*  75 */   private final Queue<SSEData> flushingMessages = new ArrayDeque<>();
/*  76 */   private final List<ChannelListener<ServerSentEventConnection>> closeTasks = new CopyOnWriteArrayList<>();
/*     */   private Map<String, String> parameters;
/*  78 */   private Map<String, Object> properties = new HashMap<>();
/*     */   
/*  80 */   private static final AtomicIntegerFieldUpdater<ServerSentEventConnection> openUpdater = AtomicIntegerFieldUpdater.newUpdater(ServerSentEventConnection.class, "open");
/*  81 */   private volatile int open = 1;
/*     */   private volatile boolean shutdown = false;
/*  83 */   private volatile long keepAliveTime = -1L;
/*     */   
/*     */   private XnioExecutor.Key timerKey;
/*     */   
/*     */   public ServerSentEventConnection(HttpServerExchange exchange, StreamSinkChannel sink) {
/*  88 */     this.exchange = exchange;
/*  89 */     this.sink = sink;
/*  90 */     this.sink.getCloseSetter().set(new ChannelListener<StreamSinkChannel>()
/*     */         {
/*     */           public void handleEvent(StreamSinkChannel channel) {
/*  93 */             if (ServerSentEventConnection.this.timerKey != null) {
/*  94 */               ServerSentEventConnection.this.timerKey.remove();
/*     */             }
/*  96 */             for (ChannelListener<ServerSentEventConnection> listener : (Iterable<ChannelListener<ServerSentEventConnection>>)ServerSentEventConnection.this.closeTasks) {
/*  97 */               ChannelListeners.invokeChannelListener(ServerSentEventConnection.this, listener);
/*     */             }
/*  99 */             IoUtils.safeClose(ServerSentEventConnection.this);
/*     */           }
/*     */         });
/* 102 */     this.sink.getWriteSetter().set(this.writeListener);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized void addCloseTask(ChannelListener<ServerSentEventConnection> listener) {
/* 111 */     this.closeTasks.add(listener);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Principal getPrincipal() {
/* 119 */     Account account = getAccount();
/* 120 */     if (account != null) {
/* 121 */       return account.getPrincipal();
/*     */     }
/* 123 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Account getAccount() {
/* 131 */     SecurityContext sc = this.exchange.getSecurityContext();
/* 132 */     if (sc != null) {
/* 133 */       return sc.getAuthenticatedAccount();
/*     */     }
/* 135 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public HeaderMap getRequestHeaders() {
/* 143 */     return this.exchange.getRequestHeaders();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public HeaderMap getResponseHeaders() {
/* 151 */     return this.exchange.getResponseHeaders();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getRequestURI() {
/* 159 */     return this.exchange.getRequestURI();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Map<String, Deque<String>> getQueryParameters() {
/* 167 */     return this.exchange.getQueryParameters();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getQueryString() {
/* 175 */     return this.exchange.getQueryString();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void send(String data) {
/* 184 */     send(data, null, null, null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void send(String data, EventCallback callback) {
/* 194 */     send(data, null, null, callback);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void sendRetry(long retry) {
/* 203 */     sendRetry(retry, null);
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
/*     */   public synchronized void sendRetry(long retry, EventCallback callback) {
/* 215 */     if (this.open == 0 || this.shutdown) {
/* 216 */       if (callback != null) {
/* 217 */         callback.failed(this, null, null, null, new ClosedChannelException());
/*     */       }
/*     */       return;
/*     */     } 
/* 221 */     this.queue.add(new SSEData(retry, callback));
/* 222 */     this.sink.getIoThread().execute(new Runnable()
/*     */         {
/*     */           public void run() {
/* 225 */             synchronized (ServerSentEventConnection.this) {
/* 226 */               if (ServerSentEventConnection.this.pooled == null) {
/* 227 */                 ServerSentEventConnection.this.fillBuffer();
/* 228 */                 ServerSentEventConnection.this.writeListener.handleEvent(ServerSentEventConnection.this.sink);
/*     */               } 
/*     */             } 
/*     */           }
/*     */         });
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
/*     */   public synchronized void send(String data, String event, String id, EventCallback callback) {
/* 244 */     if (this.open == 0 || this.shutdown) {
/* 245 */       if (callback != null) {
/* 246 */         callback.failed(this, data, event, id, new ClosedChannelException());
/*     */       }
/*     */       return;
/*     */     } 
/* 250 */     this.queue.add(new SSEData(event, data, id, callback));
/* 251 */     this.sink.getIoThread().execute(new Runnable()
/*     */         {
/*     */           public void run() {
/* 254 */             synchronized (ServerSentEventConnection.this) {
/* 255 */               if (ServerSentEventConnection.this.pooled == null) {
/* 256 */                 ServerSentEventConnection.this.fillBuffer();
/* 257 */                 ServerSentEventConnection.this.writeListener.handleEvent(ServerSentEventConnection.this.sink);
/*     */               } 
/*     */             } 
/*     */           }
/*     */         });
/*     */   }
/*     */   
/*     */   public String getParameter(String name) {
/* 265 */     if (this.parameters == null) {
/* 266 */       return null;
/*     */     }
/* 268 */     return this.parameters.get(name);
/*     */   }
/*     */   
/*     */   public void setParameter(String name, String value) {
/* 272 */     if (this.parameters == null) {
/* 273 */       this.parameters = new HashMap<>();
/*     */     }
/* 275 */     this.parameters.put(name, value);
/*     */   }
/*     */   
/*     */   public Map<String, Object> getProperties() {
/* 279 */     return this.properties;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long getKeepAliveTime() {
/* 288 */     return this.keepAliveTime;
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
/*     */   public void setKeepAliveTime(long keepAliveTime) {
/* 300 */     this.keepAliveTime = keepAliveTime;
/* 301 */     if (this.timerKey != null) {
/* 302 */       this.timerKey.remove();
/*     */     }
/* 304 */     this.timerKey = this.sink.getIoThread().executeAtInterval(new Runnable()
/*     */         {
/*     */           public void run() {
/* 307 */             if (ServerSentEventConnection.this.shutdown || ServerSentEventConnection.this.open == 0) {
/* 308 */               if (ServerSentEventConnection.this.timerKey != null) {
/* 309 */                 ServerSentEventConnection.this.timerKey.remove();
/*     */               }
/*     */               return;
/*     */             } 
/* 313 */             if (ServerSentEventConnection.this.pooled == null) {
/* 314 */               ServerSentEventConnection.this.pooled = ServerSentEventConnection.this.exchange.getConnection().getByteBufferPool().allocate();
/* 315 */               ServerSentEventConnection.this.pooled.getBuffer().put(":\n".getBytes(StandardCharsets.UTF_8));
/* 316 */               ServerSentEventConnection.this.pooled.getBuffer().flip();
/* 317 */               ServerSentEventConnection.this.writeListener.handleEvent(ServerSentEventConnection.this.sink);
/*     */             } 
/*     */           }
/*     */         },  keepAliveTime, TimeUnit.MILLISECONDS);
/*     */   }
/*     */   
/*     */   private void fillBuffer() {
/* 324 */     if (this.queue.isEmpty()) {
/* 325 */       if (this.pooled != null) {
/* 326 */         this.pooled.close();
/* 327 */         this.pooled = null;
/* 328 */         this.sink.suspendWrites();
/*     */       } 
/*     */       
/*     */       return;
/*     */     } 
/* 333 */     if (this.pooled == null) {
/* 334 */       this.pooled = this.exchange.getConnection().getByteBufferPool().allocate();
/*     */     } else {
/* 336 */       this.pooled.getBuffer().clear();
/*     */     } 
/* 338 */     ByteBuffer buffer = this.pooled.getBuffer();
/*     */     
/* 340 */     while (!this.queue.isEmpty() && buffer.hasRemaining()) {
/* 341 */       SSEData data = this.queue.poll();
/* 342 */       this.buffered.add(data);
/* 343 */       if (data.leftOverData == null) {
/* 344 */         StringBuilder message = new StringBuilder();
/* 345 */         if (data.retry > 0L) {
/* 346 */           message.append("retry:");
/* 347 */           message.append(data.retry);
/* 348 */           message.append('\n');
/*     */         } else {
/* 350 */           if (data.id != null) {
/* 351 */             message.append("id:");
/* 352 */             message.append(data.id);
/* 353 */             message.append('\n');
/*     */           } 
/* 355 */           if (data.event != null) {
/* 356 */             message.append("event:");
/* 357 */             message.append(data.event);
/* 358 */             message.append('\n');
/*     */           } 
/* 360 */           if (data.data != null) {
/* 361 */             message.append("data:");
/* 362 */             for (int i = 0; i < data.data.length(); i++) {
/* 363 */               char c = data.data.charAt(i);
/* 364 */               if (c == '\n') {
/* 365 */                 message.append("\ndata:");
/*     */               } else {
/* 367 */                 message.append(c);
/*     */               } 
/*     */             } 
/* 370 */             message.append('\n');
/*     */           } 
/*     */         } 
/* 373 */         message.append('\n');
/* 374 */         byte[] messageBytes = message.toString().getBytes(StandardCharsets.UTF_8);
/* 375 */         if (messageBytes.length < buffer.remaining()) {
/* 376 */           buffer.put(messageBytes);
/* 377 */           data.endBufferPosition = buffer.position(); continue;
/*     */         } 
/* 379 */         this.queue.addFirst(data);
/* 380 */         int rem = buffer.remaining();
/* 381 */         buffer.put(messageBytes, 0, rem);
/* 382 */         data.leftOverData = messageBytes;
/* 383 */         data.leftOverDataOffset = rem;
/*     */         continue;
/*     */       } 
/* 386 */       int remainingData = data.leftOverData.length - data.leftOverDataOffset;
/* 387 */       if (remainingData > buffer.remaining()) {
/* 388 */         this.queue.addFirst(data);
/* 389 */         int toWrite = buffer.remaining();
/* 390 */         buffer.put(data.leftOverData, data.leftOverDataOffset, toWrite);
/* 391 */         SSEData sSEData = data; sSEData.leftOverDataOffset = sSEData.leftOverDataOffset + toWrite; continue;
/*     */       } 
/* 393 */       buffer.put(data.leftOverData, data.leftOverDataOffset, remainingData);
/* 394 */       data.endBufferPosition = buffer.position();
/* 395 */       data.leftOverData = null;
/*     */     } 
/*     */ 
/*     */     
/* 399 */     buffer.flip();
/* 400 */     this.sink.resumeWrites();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void shutdown() {
/* 407 */     if (this.open == 0 || this.shutdown) {
/*     */       return;
/*     */     }
/* 410 */     this.shutdown = true;
/* 411 */     this.sink.getIoThread().execute(new Runnable()
/*     */         {
/*     */           public void run()
/*     */           {
/* 415 */             synchronized (ServerSentEventConnection.this) {
/* 416 */               if (ServerSentEventConnection.this.queue.isEmpty() && ServerSentEventConnection.this.pooled == null) {
/* 417 */                 ServerSentEventConnection.this.exchange.endExchange();
/*     */               }
/*     */             } 
/*     */           }
/*     */         });
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isOpen() {
/* 426 */     return (this.open != 0);
/*     */   }
/*     */ 
/*     */   
/*     */   public void close() throws IOException {
/* 431 */     close(new ClosedChannelException());
/*     */   }
/*     */   
/*     */   private synchronized void close(IOException e) throws IOException {
/* 435 */     if (openUpdater.compareAndSet(this, 1, 0)) {
/* 436 */       if (this.pooled != null) {
/* 437 */         this.pooled.close();
/* 438 */         this.pooled = null;
/*     */       } 
/* 440 */       List<SSEData> cb = new ArrayList<>(this.buffered.size() + this.queue.size() + this.flushingMessages.size());
/* 441 */       cb.addAll(this.buffered);
/* 442 */       cb.addAll(this.queue);
/* 443 */       cb.addAll(this.flushingMessages);
/* 444 */       this.queue.clear();
/* 445 */       this.buffered.clear();
/* 446 */       this.flushingMessages.clear();
/* 447 */       for (SSEData i : cb) {
/* 448 */         if (i.callback != null) {
/*     */           try {
/* 450 */             i.callback.failed(this, i.data, i.event, i.id, e);
/* 451 */           } catch (Exception ex) {
/* 452 */             UndertowLogger.REQUEST_LOGGER.failedToInvokeFailedCallback(i.callback, ex);
/*     */           } 
/*     */         }
/*     */       } 
/* 456 */       this.sink.shutdownWrites();
/* 457 */       if (!this.sink.flush()) {
/* 458 */         this.sink.getWriteSetter().set(ChannelListeners.flushingChannelListener(null, new ChannelExceptionHandler<StreamSinkChannel>()
/*     */               {
/*     */                 public void handleException(StreamSinkChannel channel, IOException exception) {
/* 461 */                   IoUtils.safeClose((Closeable)ServerSentEventConnection.this.sink);
/*     */                 }
/*     */               }));
/* 464 */         this.sink.resumeWrites();
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public <T> T getAttachment(AttachmentKey<T> key) {
/* 471 */     return (T)this.exchange.getAttachment(key);
/*     */   }
/*     */ 
/*     */   
/*     */   public <T> List<T> getAttachmentList(AttachmentKey<? extends List<T>> key) {
/* 476 */     return this.exchange.getAttachmentList(key);
/*     */   }
/*     */ 
/*     */   
/*     */   public <T> T putAttachment(AttachmentKey<T> key, T value) {
/* 481 */     return (T)this.exchange.putAttachment(key, value);
/*     */   }
/*     */ 
/*     */   
/*     */   public <T> T removeAttachment(AttachmentKey<T> key) {
/* 486 */     return (T)this.exchange.removeAttachment(key);
/*     */   }
/*     */ 
/*     */   
/*     */   public <T> void addToAttachmentList(AttachmentKey<AttachmentList<T>> key, T value) {
/* 491 */     this.exchange.addToAttachmentList(key, value);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static interface EventCallback
/*     */   {
/*     */     void done(ServerSentEventConnection param1ServerSentEventConnection, String param1String1, String param1String2, String param1String3);
/*     */ 
/*     */ 
/*     */     
/*     */     void failed(ServerSentEventConnection param1ServerSentEventConnection, String param1String1, String param1String2, String param1String3, IOException param1IOException);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static class SSEData
/*     */   {
/*     */     final String event;
/*     */ 
/*     */     
/*     */     final String data;
/*     */ 
/*     */     
/*     */     final String id;
/*     */ 
/*     */     
/*     */     final long retry;
/*     */ 
/*     */     
/*     */     final ServerSentEventConnection.EventCallback callback;
/*     */ 
/*     */     
/* 525 */     private int endBufferPosition = -1;
/*     */     private byte[] leftOverData;
/*     */     private int leftOverDataOffset;
/*     */     
/*     */     private SSEData(String event, String data, String id, ServerSentEventConnection.EventCallback callback) {
/* 530 */       this.event = event;
/* 531 */       this.data = data;
/* 532 */       this.id = id;
/* 533 */       this.callback = callback;
/* 534 */       this.retry = -1L;
/*     */     }
/*     */     
/*     */     private SSEData(long retry, ServerSentEventConnection.EventCallback callback) {
/* 538 */       this.event = null;
/* 539 */       this.data = null;
/* 540 */       this.id = null;
/* 541 */       this.callback = callback;
/* 542 */       this.retry = retry;
/*     */     }
/*     */   }
/*     */   
/*     */   private class SseWriteListener
/*     */     implements ChannelListener<StreamSinkChannel> {
/*     */     private SseWriteListener() {}
/*     */     
/*     */     public void handleEvent(StreamSinkChannel channel) {
/* 551 */       synchronized (ServerSentEventConnection.this) {
/*     */         try {
/* 553 */           int res; if (!ServerSentEventConnection.this.flushingMessages.isEmpty()) {
/* 554 */             if (!channel.flush()) {
/*     */               return;
/*     */             }
/* 557 */             for (ServerSentEventConnection.SSEData data : ServerSentEventConnection.this.flushingMessages) {
/* 558 */               if (data.callback != null && data.leftOverData == null) {
/* 559 */                 data.callback.done(ServerSentEventConnection.this, data.data, data.event, data.id);
/*     */               }
/*     */             } 
/* 562 */             ServerSentEventConnection.this.flushingMessages.clear();
/* 563 */             ByteBuffer byteBuffer = ServerSentEventConnection.this.pooled.getBuffer();
/* 564 */             if (!byteBuffer.hasRemaining()) {
/* 565 */               ServerSentEventConnection.this.fillBuffer();
/* 566 */               if (ServerSentEventConnection.this.pooled == null) {
/* 567 */                 if (channel.flush()) {
/* 568 */                   channel.suspendWrites();
/*     */                 }
/*     */                 return;
/*     */               } 
/*     */             } 
/* 573 */           } else if (ServerSentEventConnection.this.pooled == null) {
/* 574 */             if (channel.flush()) {
/* 575 */               channel.suspendWrites();
/*     */             }
/*     */             
/*     */             return;
/*     */           } 
/* 580 */           ByteBuffer buffer = ServerSentEventConnection.this.pooled.getBuffer();
/*     */           
/*     */           do {
/* 583 */             res = channel.write(buffer);
/* 584 */             boolean flushed = channel.flush();
/* 585 */             while (!ServerSentEventConnection.this.buffered.isEmpty()) {
/*     */               
/* 587 */               ServerSentEventConnection.SSEData data = ServerSentEventConnection.this.buffered.peek();
/* 588 */               if (data.endBufferPosition > 0 && buffer.position() >= data.endBufferPosition) {
/* 589 */                 ServerSentEventConnection.this.buffered.poll();
/* 590 */                 if (flushed) {
/* 591 */                   if (data.callback != null && data.leftOverData == null) {
/* 592 */                     data.callback.done(ServerSentEventConnection.this, data.data, data.event, data.id);
/*     */                   }
/*     */                   continue;
/*     */                 } 
/* 596 */                 ServerSentEventConnection.this.flushingMessages.add(data);
/*     */                 
/*     */                 continue;
/*     */               } 
/* 600 */               if (data.endBufferPosition <= 0) {
/* 601 */                 ServerSentEventConnection.this.buffered.poll();
/*     */               }
/*     */             } 
/*     */ 
/*     */             
/* 606 */             if (!flushed && !ServerSentEventConnection.this.flushingMessages.isEmpty()) {
/* 607 */               ServerSentEventConnection.this.sink.resumeWrites();
/*     */               
/*     */               return;
/*     */             } 
/* 611 */             if (!buffer.hasRemaining()) {
/* 612 */               ServerSentEventConnection.this.fillBuffer();
/* 613 */               if (ServerSentEventConnection.this.pooled == null) {
/*     */                 return;
/*     */               }
/* 616 */             } else if (res == 0) {
/* 617 */               ServerSentEventConnection.this.sink.resumeWrites();
/*     */               
/*     */               return;
/*     */             } 
/* 621 */           } while (res > 0);
/* 622 */         } catch (IOException e) {
/* 623 */           ServerSentEventConnection.this.handleException(e);
/*     */         } 
/*     */       } 
/*     */     }
/*     */   }
/*     */   
/*     */   private void handleException(IOException e) {
/* 630 */     IoUtils.safeClose(new Closeable[] { this, (Closeable)this.sink, (Closeable)this.exchange.getConnection() });
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\handlers\sse\ServerSentEventConnection.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */