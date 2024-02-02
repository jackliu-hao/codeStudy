/*     */ package io.undertow.server.handlers.proxy;
/*     */ 
/*     */ import io.undertow.UndertowLogger;
/*     */ import io.undertow.UndertowMessages;
/*     */ import io.undertow.client.ClientCallback;
/*     */ import io.undertow.client.ClientConnection;
/*     */ import io.undertow.client.ClientStatistics;
/*     */ import io.undertow.client.UndertowClient;
/*     */ import io.undertow.server.ExchangeCompletionListener;
/*     */ import io.undertow.server.HttpServerExchange;
/*     */ import io.undertow.util.CopyOnWriteMap;
/*     */ import io.undertow.util.Headers;
/*     */ import io.undertow.util.WorkerUtils;
/*     */ import java.io.Closeable;
/*     */ import java.io.IOException;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.net.URI;
/*     */ import java.nio.channels.Channel;
/*     */ import java.util.ArrayDeque;
/*     */ import java.util.Deque;
/*     */ import java.util.Map;
/*     */ import java.util.concurrent.ConcurrentMap;
/*     */ import java.util.concurrent.CountDownLatch;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import java.util.concurrent.atomic.AtomicInteger;
/*     */ import java.util.concurrent.atomic.AtomicLong;
/*     */ import org.xnio.ChannelListener;
/*     */ import org.xnio.IoUtils;
/*     */ import org.xnio.OptionMap;
/*     */ import org.xnio.XnioExecutor;
/*     */ import org.xnio.XnioIoThread;
/*     */ import org.xnio.ssl.XnioSsl;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ProxyConnectionPool
/*     */   implements Closeable
/*     */ {
/*     */   private final URI uri;
/*     */   private final InetSocketAddress bindAddress;
/*     */   private final XnioSsl ssl;
/*     */   private final UndertowClient client;
/*     */   private final ConnectionPoolManager connectionPoolManager;
/*     */   private final OptionMap options;
/*     */   private volatile boolean closed;
/*     */   private final int maxConnections;
/*     */   private final int maxCachedConnections;
/*     */   private final int coreCachedConnections;
/*     */   private final long timeToLive;
/* 110 */   private final AtomicInteger openConnections = new AtomicInteger(0);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 115 */   private final AtomicLong requestCount = new AtomicLong();
/*     */ 
/*     */ 
/*     */   
/* 119 */   private final AtomicLong read = new AtomicLong();
/*     */ 
/*     */ 
/*     */   
/* 123 */   private final AtomicLong written = new AtomicLong();
/*     */   
/* 125 */   private final ConcurrentMap<XnioIoThread, HostThreadData> hostThreadData = (ConcurrentMap<XnioIoThread, HostThreadData>)new CopyOnWriteMap();
/*     */   
/*     */   public ProxyConnectionPool(ConnectionPoolManager connectionPoolManager, URI uri, UndertowClient client, OptionMap options) {
/* 128 */     this(connectionPoolManager, uri, (XnioSsl)null, client, options);
/*     */   }
/*     */   
/*     */   public ProxyConnectionPool(ConnectionPoolManager connectionPoolManager, InetSocketAddress bindAddress, URI uri, UndertowClient client, OptionMap options) {
/* 132 */     this(connectionPoolManager, bindAddress, uri, null, client, options);
/*     */   }
/*     */   
/*     */   public ProxyConnectionPool(ConnectionPoolManager connectionPoolManager, URI uri, XnioSsl ssl, UndertowClient client, OptionMap options) {
/* 136 */     this(connectionPoolManager, null, uri, ssl, client, options);
/*     */   }
/*     */   
/*     */   public ProxyConnectionPool(ConnectionPoolManager connectionPoolManager, InetSocketAddress bindAddress, URI uri, XnioSsl ssl, UndertowClient client, OptionMap options) {
/* 140 */     this.connectionPoolManager = connectionPoolManager;
/* 141 */     this.maxConnections = Math.max(connectionPoolManager.getMaxConnections(), 1);
/* 142 */     this.maxCachedConnections = Math.max(connectionPoolManager.getMaxCachedConnections(), 0);
/* 143 */     this.coreCachedConnections = Math.max(connectionPoolManager.getSMaxConnections(), 0);
/* 144 */     this.timeToLive = connectionPoolManager.getTtl();
/* 145 */     this.bindAddress = bindAddress;
/* 146 */     this.uri = uri;
/* 147 */     this.ssl = ssl;
/* 148 */     this.client = client;
/* 149 */     this.options = options;
/*     */   }
/*     */   
/*     */   public URI getUri() {
/* 153 */     return this.uri;
/*     */   }
/*     */   
/*     */   public InetSocketAddress getBindAddress() {
/* 157 */     return this.bindAddress;
/*     */   }
/*     */   
/*     */   public void close() {
/* 161 */     this.closed = true;
/* 162 */     for (HostThreadData data : this.hostThreadData.values()) {
/* 163 */       final ConnectionHolder holder = data.availableConnections.poll();
/* 164 */       if (holder != null) {
/* 165 */         holder.clientConnection.getIoThread().execute(new Runnable()
/*     */             {
/*     */               public void run() {
/* 168 */                 IoUtils.safeClose((Closeable)holder.clientConnection);
/*     */               }
/*     */             });
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void returnConnection(ConnectionHolder connectionHolder) {
/* 182 */     ClientStatistics stats = connectionHolder.clientConnection.getStatistics();
/* 183 */     this.requestCount.incrementAndGet();
/* 184 */     if (stats != null) {
/*     */       
/* 186 */       this.read.addAndGet(stats.getRead());
/* 187 */       this.written.addAndGet(stats.getWritten());
/* 188 */       stats.reset();
/*     */     } 
/*     */     
/* 191 */     HostThreadData hostData = getData();
/* 192 */     if (this.closed) {
/*     */       
/* 194 */       IoUtils.safeClose((Closeable)connectionHolder.clientConnection);
/* 195 */       ConnectionHolder con = hostData.availableConnections.poll();
/* 196 */       while (con != null) {
/* 197 */         IoUtils.safeClose((Closeable)con.clientConnection);
/* 198 */         con = hostData.availableConnections.poll();
/*     */       } 
/* 200 */       redistributeQueued(hostData);
/*     */ 
/*     */       
/*     */       return;
/*     */     } 
/*     */ 
/*     */     
/* 207 */     ClientConnection connection = connectionHolder.clientConnection;
/* 208 */     if (connection.isOpen() && !connection.isUpgraded()) {
/* 209 */       CallbackHolder callback = hostData.awaitingConnections.poll();
/* 210 */       while (callback != null && callback.isCancelled()) {
/* 211 */         callback = hostData.awaitingConnections.poll();
/*     */       }
/* 213 */       if (callback != null) {
/* 214 */         if (callback.getTimeoutKey() != null) {
/* 215 */           callback.getTimeoutKey().remove();
/*     */         }
/*     */         
/* 218 */         connectionReady(connectionHolder, callback.getCallback(), callback.getExchange(), false);
/*     */       } else {
/* 220 */         int cachedConnectionCount = hostData.availableConnections.size();
/* 221 */         if (cachedConnectionCount >= this.maxCachedConnections) {
/*     */           
/* 223 */           ConnectionHolder holder = hostData.availableConnections.poll();
/* 224 */           if (holder != null) {
/* 225 */             IoUtils.safeClose((Closeable)holder.clientConnection);
/*     */           }
/*     */         } 
/* 228 */         hostData.availableConnections.add(connectionHolder);
/*     */         
/* 230 */         if (this.timeToLive > 0L) {
/*     */ 
/*     */ 
/*     */ 
/*     */           
/* 235 */           long currentTime = System.currentTimeMillis();
/* 236 */           connectionHolder.timeout = currentTime + this.timeToLive;
/* 237 */           if (hostData.availableConnections.size() > this.coreCachedConnections && 
/* 238 */             hostData.nextTimeout <= 0L) {
/* 239 */             hostData.timeoutKey = WorkerUtils.executeAfter(connection.getIoThread(), hostData.timeoutTask, this.timeToLive, TimeUnit.MILLISECONDS);
/* 240 */             hostData.nextTimeout = connectionHolder.timeout;
/*     */           }
/*     */         
/*     */         } 
/*     */       } 
/* 245 */     } else if (connection.isOpen() && connection.isUpgraded()) {
/*     */ 
/*     */ 
/*     */       
/* 249 */       connection.getCloseSetter().set(null);
/* 250 */       handleClosedConnection(hostData, connectionHolder);
/*     */     } 
/*     */   }
/*     */   
/*     */   private void handleClosedConnection(HostThreadData hostData, ConnectionHolder connection) {
/* 255 */     this.openConnections.decrementAndGet();
/* 256 */     int connections = --hostData.connections;
/* 257 */     hostData.availableConnections.remove(connection);
/* 258 */     if (connections < this.maxConnections) {
/* 259 */       CallbackHolder task = hostData.awaitingConnections.poll();
/* 260 */       while (task != null && task.isCancelled()) {
/* 261 */         task = hostData.awaitingConnections.poll();
/*     */       }
/* 263 */       if (task != null) {
/* 264 */         openConnection(task.exchange, task.callback, hostData, false);
/*     */       }
/*     */     } 
/*     */   }
/*     */   
/*     */   private void openConnection(final HttpServerExchange exchange, final ProxyCallback<ProxyConnection> callback, final HostThreadData data, final boolean exclusive) {
/* 270 */     if (!exclusive) {
/* 271 */       data.connections++;
/*     */     }
/*     */     try {
/* 274 */       this.client.connect(new ClientCallback<ClientConnection>()
/*     */           {
/*     */             public void completed(ClientConnection result) {
/* 277 */               ProxyConnectionPool.this.openConnections.incrementAndGet();
/* 278 */               final ProxyConnectionPool.ConnectionHolder connectionHolder = new ProxyConnectionPool.ConnectionHolder(result);
/* 279 */               if (!exclusive) {
/* 280 */                 result.getCloseSetter().set(new ChannelListener<ClientConnection>()
/*     */                     {
/*     */                       public void handleEvent(ClientConnection channel) {
/* 283 */                         ProxyConnectionPool.this.handleClosedConnection(data, connectionHolder);
/*     */                       }
/*     */                     });
/*     */               }
/* 287 */               ProxyConnectionPool.this.connectionReady(connectionHolder, callback, exchange, exclusive);
/*     */             }
/*     */ 
/*     */             
/*     */             public void failed(IOException e) {
/* 292 */               if (!exclusive) {
/* 293 */                 data.connections--;
/*     */               }
/* 295 */               UndertowLogger.REQUEST_LOGGER.debug("Failed to connect", e);
/* 296 */               if (!ProxyConnectionPool.this.connectionPoolManager.handleError()) {
/* 297 */                 ProxyConnectionPool.this.redistributeQueued(ProxyConnectionPool.this.getData());
/* 298 */                 ProxyConnectionPool.this.scheduleFailedHostRetry(exchange);
/*     */               } 
/* 300 */               callback.failed(exchange);
/*     */             }
/* 302 */           }this.bindAddress, getUri(), exchange.getIoThread(), this.ssl, exchange.getConnection().getByteBufferPool(), this.options);
/* 303 */     } catch (RuntimeException e) {
/*     */ 
/*     */ 
/*     */       
/* 307 */       if (!exclusive) {
/* 308 */         data.connections--;
/*     */       }
/*     */ 
/*     */ 
/*     */       
/* 313 */       this.connectionPoolManager.handleError();
/* 314 */       callback.failed(exchange);
/* 315 */       throw e;
/*     */     } 
/*     */   }
/*     */   
/*     */   private void redistributeQueued(HostThreadData hostData) {
/* 320 */     CallbackHolder callback = hostData.awaitingConnections.poll();
/* 321 */     while (callback != null) {
/* 322 */       if (callback.getTimeoutKey() != null) {
/* 323 */         callback.getTimeoutKey().remove();
/*     */       }
/* 325 */       if (!callback.isCancelled()) {
/* 326 */         long time = System.currentTimeMillis();
/* 327 */         if (callback.getExpireTime() > 0L && callback.getExpireTime() < time) {
/* 328 */           callback.getCallback().failed(callback.getExchange());
/*     */         } else {
/* 330 */           callback.getCallback().queuedRequestFailed(callback.getExchange());
/*     */         } 
/*     */       } 
/* 333 */       callback = hostData.awaitingConnections.poll();
/*     */     } 
/*     */   }
/*     */   
/*     */   private void connectionReady(final ConnectionHolder result, ProxyCallback<ProxyConnection> callback, HttpServerExchange exchange, final boolean exclusive) {
/*     */     try {
/* 339 */       exchange.addExchangeCompleteListener(new ExchangeCompletionListener()
/*     */           {
/*     */             public void exchangeEvent(HttpServerExchange exchange, ExchangeCompletionListener.NextListener nextListener) {
/* 342 */               if (!exclusive) {
/* 343 */                 ProxyConnectionPool.this.returnConnection(result);
/*     */               }
/* 345 */               nextListener.proceed();
/*     */             }
/*     */           });
/* 348 */     } catch (Exception e) {
/* 349 */       returnConnection(result);
/* 350 */       callback.failed(exchange);
/*     */       return;
/*     */     } 
/* 353 */     callback.completed(exchange, new ProxyConnection(result.clientConnection, (this.uri.getPath() == null) ? "/" : this.uri.getPath()));
/*     */   }
/*     */   
/*     */   public AvailabilityType available() {
/* 357 */     if (this.closed) {
/* 358 */       return AvailabilityType.CLOSED;
/*     */     }
/* 360 */     if (!this.connectionPoolManager.isAvailable()) {
/* 361 */       return AvailabilityType.PROBLEM;
/*     */     }
/* 363 */     HostThreadData data = getData();
/* 364 */     if (data.connections < this.maxConnections) {
/* 365 */       return AvailabilityType.AVAILABLE;
/*     */     }
/* 367 */     if (!data.availableConnections.isEmpty()) {
/* 368 */       return AvailabilityType.AVAILABLE;
/*     */     }
/* 370 */     if (data.awaitingConnections.size() >= this.connectionPoolManager.getMaxQueueSize()) {
/* 371 */       return AvailabilityType.FULL_QUEUE;
/*     */     }
/* 373 */     return AvailabilityType.FULL;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void scheduleFailedHostRetry(final HttpServerExchange exchange) {
/* 382 */     int retry = this.connectionPoolManager.getProblemServerRetry();
/*     */     
/* 384 */     if (retry > 0 && !this.connectionPoolManager.isAvailable()) {
/* 385 */       WorkerUtils.executeAfter(exchange.getIoThread(), new Runnable()
/*     */           {
/*     */             public void run() {
/* 388 */               if (ProxyConnectionPool.this.closed) {
/*     */                 return;
/*     */               }
/*     */               
/* 392 */               UndertowLogger.PROXY_REQUEST_LOGGER.debugf("Attempting to reconnect to failed host %s", ProxyConnectionPool.this.getUri());
/*     */               try {
/* 394 */                 ProxyConnectionPool.this.client.connect(new ClientCallback<ClientConnection>()
/*     */                     {
/*     */                       public void completed(ClientConnection result) {
/* 397 */                         UndertowLogger.PROXY_REQUEST_LOGGER.debugf("Connected to previously failed host %s, returning to service", ProxyConnectionPool.this.getUri());
/* 398 */                         if (ProxyConnectionPool.this.connectionPoolManager.clearError()) {
/*     */                           
/* 400 */                           final ProxyConnectionPool.ConnectionHolder connectionHolder = new ProxyConnectionPool.ConnectionHolder(result);
/* 401 */                           final ProxyConnectionPool.HostThreadData data = ProxyConnectionPool.this.getData();
/* 402 */                           result.getCloseSetter().set(new ChannelListener<ClientConnection>()
/*     */                               {
/*     */                                 public void handleEvent(ClientConnection channel) {
/* 405 */                                   ProxyConnectionPool.this.handleClosedConnection(data, connectionHolder);
/*     */                                 }
/*     */                               });
/* 408 */                           data.connections++;
/* 409 */                           ProxyConnectionPool.this.returnConnection(connectionHolder);
/*     */                         } else {
/*     */                           
/* 412 */                           ProxyConnectionPool.this.scheduleFailedHostRetry(exchange);
/*     */                         } 
/*     */                       }
/*     */ 
/*     */                       
/*     */                       public void failed(IOException e) {
/* 418 */                         UndertowLogger.PROXY_REQUEST_LOGGER.debugf("Failed to reconnect to failed host %s", ProxyConnectionPool.this.getUri());
/* 419 */                         ProxyConnectionPool.this.connectionPoolManager.handleError();
/* 420 */                         ProxyConnectionPool.this.scheduleFailedHostRetry(exchange);
/*     */                       }
/* 422 */                     }ProxyConnectionPool.this.bindAddress, ProxyConnectionPool.this.getUri(), exchange.getIoThread(), ProxyConnectionPool.this.ssl, exchange.getConnection().getByteBufferPool(), ProxyConnectionPool.this.options);
/* 423 */               } catch (RuntimeException e) {
/*     */ 
/*     */ 
/*     */                 
/* 427 */                 ProxyConnectionPool.this.connectionPoolManager.handleError();
/* 428 */                 ProxyConnectionPool.this.scheduleFailedHostRetry(exchange);
/*     */               } 
/*     */             }
/*     */           }retry, TimeUnit.SECONDS);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void timeoutConnections(long currentTime, HostThreadData data) {
/* 442 */     int idleConnections = data.availableConnections.size();
/*     */     
/*     */     ConnectionHolder holder;
/* 445 */     while (idleConnections > 0 && idleConnections > this.coreCachedConnections && (holder = data.availableConnections.peek()) != null) {
/* 446 */       if (!holder.clientConnection.isOpen()) {
/*     */         
/* 448 */         idleConnections--; continue;
/* 449 */       }  if (currentTime >= holder.timeout) {
/*     */         
/* 451 */         holder = data.availableConnections.poll();
/* 452 */         IoUtils.safeClose((Closeable)holder.clientConnection);
/* 453 */         idleConnections--; continue;
/*     */       } 
/* 455 */       if (data.timeoutKey != null) {
/* 456 */         data.timeoutKey.remove();
/* 457 */         data.timeoutKey = null;
/*     */       } 
/*     */       
/* 460 */       long remaining = holder.timeout - currentTime + 1L;
/* 461 */       data.nextTimeout = holder.timeout;
/* 462 */       data.timeoutKey = WorkerUtils.executeAfter(holder.clientConnection.getIoThread(), data.timeoutTask, remaining, TimeUnit.MILLISECONDS);
/*     */       
/*     */       return;
/*     */     } 
/*     */     
/* 467 */     if (data.timeoutKey != null) {
/* 468 */       data.timeoutKey.remove();
/* 469 */       data.timeoutKey = null;
/*     */     } 
/* 471 */     data.nextTimeout = -1L;
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
/*     */   private HostThreadData getData() {
/* 483 */     Thread thread = Thread.currentThread();
/* 484 */     if (!(thread instanceof XnioIoThread)) {
/* 485 */       throw UndertowMessages.MESSAGES.canOnlyBeCalledByIoThread();
/*     */     }
/* 487 */     XnioIoThread ioThread = (XnioIoThread)thread;
/* 488 */     HostThreadData data = this.hostThreadData.get(ioThread);
/* 489 */     if (data != null) {
/* 490 */       return data;
/*     */     }
/* 492 */     data = new HostThreadData();
/* 493 */     HostThreadData existing = this.hostThreadData.putIfAbsent(ioThread, data);
/* 494 */     if (existing != null) {
/* 495 */       return existing;
/*     */     }
/* 497 */     return data;
/*     */   }
/*     */   
/*     */   public ClientStatistics getClientStatistics() {
/* 501 */     return new ClientStatistics()
/*     */       {
/*     */         public long getRequests() {
/* 504 */           return ProxyConnectionPool.this.requestCount.get();
/*     */         }
/*     */ 
/*     */         
/*     */         public long getRead() {
/* 509 */           return ProxyConnectionPool.this.read.get();
/*     */         }
/*     */ 
/*     */         
/*     */         public long getWritten() {
/* 514 */           return ProxyConnectionPool.this.written.get();
/*     */         }
/*     */ 
/*     */         
/*     */         public void reset() {
/* 519 */           ProxyConnectionPool.this.requestCount.set(0L);
/* 520 */           ProxyConnectionPool.this.read.set(0L);
/* 521 */           ProxyConnectionPool.this.written.set(0L);
/*     */         }
/*     */       };
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getOpenConnections() {
/* 531 */     return this.openConnections.get();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void connect(ProxyClient.ProxyTarget proxyTarget, HttpServerExchange exchange, ProxyCallback<ProxyConnection> callback, long timeout, TimeUnit timeUnit, boolean exclusive) {
/* 538 */     HostThreadData data = getData();
/* 539 */     ConnectionHolder connectionHolder = data.availableConnections.poll();
/* 540 */     while (connectionHolder != null && !connectionHolder.clientConnection.isOpen()) {
/* 541 */       connectionHolder = data.availableConnections.poll();
/*     */     }
/* 543 */     boolean upgradeRequest = exchange.getRequestHeaders().contains(Headers.UPGRADE);
/* 544 */     if (connectionHolder != null && (!upgradeRequest || connectionHolder.clientConnection.isUpgradeSupported())) {
/* 545 */       if (exclusive) {
/* 546 */         data.connections--;
/*     */       }
/* 548 */       connectionReady(connectionHolder, callback, exchange, exclusive);
/* 549 */     } else if (exclusive || data.connections < this.maxConnections) {
/* 550 */       openConnection(exchange, callback, data, exclusive);
/*     */     } else {
/*     */       CallbackHolder holder;
/* 553 */       if (data.awaitingConnections.size() >= this.connectionPoolManager.getMaxQueueSize()) {
/* 554 */         callback.queuedRequestFailed(exchange);
/*     */         
/*     */         return;
/*     */       } 
/* 558 */       if (timeout > 0L) {
/* 559 */         long time = System.currentTimeMillis();
/* 560 */         holder = new CallbackHolder(proxyTarget, callback, exchange, time + timeUnit.toMillis(timeout));
/* 561 */         holder.setTimeoutKey(WorkerUtils.executeAfter(exchange.getIoThread(), holder, timeout, timeUnit));
/*     */       } else {
/* 563 */         holder = new CallbackHolder(proxyTarget, callback, exchange, -1L);
/*     */       } 
/* 565 */       data.awaitingConnections.add(holder);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   void closeCurrentConnections() {
/* 574 */     final CountDownLatch latch = new CountDownLatch(this.hostThreadData.size());
/* 575 */     for (Map.Entry<XnioIoThread, HostThreadData> data : this.hostThreadData.entrySet()) {
/* 576 */       ((XnioIoThread)data.getKey()).execute(new Runnable()
/*     */           {
/*     */             public void run() {
/* 579 */               ProxyConnectionPool.ConnectionHolder d = ((ProxyConnectionPool.HostThreadData)data.getValue()).availableConnections.poll();
/* 580 */               while (d != null) {
/* 581 */                 IoUtils.safeClose((Closeable)d.clientConnection);
/* 582 */                 d = ((ProxyConnectionPool.HostThreadData)data.getValue()).availableConnections.poll();
/*     */               } 
/* 584 */               ((ProxyConnectionPool.HostThreadData)data.getValue()).connections = 0;
/* 585 */               latch.countDown();
/*     */             }
/*     */           });
/*     */     } 
/*     */     try {
/* 590 */       latch.await(10L, TimeUnit.SECONDS);
/* 591 */     } catch (InterruptedException e) {
/* 592 */       Thread.currentThread().interrupt();
/* 593 */       throw new RuntimeException(e);
/*     */     } 
/*     */   }
/*     */   
/*     */   private final class HostThreadData { private HostThreadData() {}
/*     */     
/* 599 */     int connections = 0;
/*     */     XnioExecutor.Key timeoutKey;
/* 601 */     long nextTimeout = -1L;
/*     */     
/* 603 */     final Deque<ProxyConnectionPool.ConnectionHolder> availableConnections = new ArrayDeque<>();
/* 604 */     final Deque<ProxyConnectionPool.CallbackHolder> awaitingConnections = new ArrayDeque<>();
/* 605 */     final Runnable timeoutTask = new Runnable()
/*     */       {
/*     */         public void run() {
/* 608 */           long currentTime = System.currentTimeMillis();
/* 609 */           ProxyConnectionPool.this.timeoutConnections(currentTime, ProxyConnectionPool.HostThreadData.this);
/*     */         }
/*     */       }; }
/*     */ 
/*     */ 
/*     */   
/*     */   private static final class ConnectionHolder
/*     */   {
/*     */     private long timeout;
/*     */     private final ClientConnection clientConnection;
/*     */     
/*     */     private ConnectionHolder(ClientConnection clientConnection) {
/* 621 */       this.clientConnection = clientConnection;
/*     */     }
/*     */   }
/*     */   
/*     */   private static final class CallbackHolder
/*     */     implements Runnable
/*     */   {
/*     */     final ProxyClient.ProxyTarget proxyTarget;
/*     */     final ProxyCallback<ProxyConnection> callback;
/*     */     final HttpServerExchange exchange;
/*     */     final long expireTime;
/*     */     XnioExecutor.Key timeoutKey;
/*     */     boolean cancelled = false;
/*     */     
/*     */     private CallbackHolder(ProxyClient.ProxyTarget proxyTarget, ProxyCallback<ProxyConnection> callback, HttpServerExchange exchange, long expireTime) {
/* 636 */       this.proxyTarget = proxyTarget;
/* 637 */       this.callback = callback;
/* 638 */       this.exchange = exchange;
/* 639 */       this.expireTime = expireTime;
/*     */     }
/*     */     
/*     */     private ProxyCallback<ProxyConnection> getCallback() {
/* 643 */       return this.callback;
/*     */     }
/*     */     
/*     */     private HttpServerExchange getExchange() {
/* 647 */       return this.exchange;
/*     */     }
/*     */     
/*     */     private long getExpireTime() {
/* 651 */       return this.expireTime;
/*     */     }
/*     */     
/*     */     private XnioExecutor.Key getTimeoutKey() {
/* 655 */       return this.timeoutKey;
/*     */     }
/*     */     
/*     */     private boolean isCancelled() {
/* 659 */       return (this.cancelled || this.exchange.isResponseStarted());
/*     */     }
/*     */     
/*     */     private void setTimeoutKey(XnioExecutor.Key timeoutKey) {
/* 663 */       this.timeoutKey = timeoutKey;
/*     */     }
/*     */ 
/*     */     
/*     */     public void run() {
/* 668 */       this.cancelled = true;
/* 669 */       this.callback.failed(this.exchange);
/*     */     }
/*     */     
/*     */     public ProxyClient.ProxyTarget getProxyTarget() {
/* 673 */       return this.proxyTarget;
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public enum AvailabilityType
/*     */   {
/* 681 */     AVAILABLE,
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 686 */     DRAIN,
/*     */ 
/*     */ 
/*     */     
/* 690 */     FULL,
/*     */ 
/*     */ 
/*     */     
/* 694 */     FULL_QUEUE,
/*     */ 
/*     */ 
/*     */     
/* 698 */     PROBLEM,
/*     */ 
/*     */ 
/*     */     
/* 702 */     CLOSED;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\handlers\proxy\ProxyConnectionPool.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */