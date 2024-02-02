/*     */ package io.undertow.server.handlers.proxy.mod_cluster;
/*     */ 
/*     */ import io.undertow.UndertowLogger;
/*     */ import io.undertow.client.ClientCallback;
/*     */ import io.undertow.client.ClientConnection;
/*     */ import io.undertow.client.ClientExchange;
/*     */ import io.undertow.client.ClientRequest;
/*     */ import io.undertow.client.UndertowClient;
/*     */ import io.undertow.connector.ByteBufferPool;
/*     */ import io.undertow.server.HttpServerExchange;
/*     */ import io.undertow.server.handlers.proxy.ProxyCallback;
/*     */ import io.undertow.server.handlers.proxy.ProxyConnection;
/*     */ import io.undertow.util.Headers;
/*     */ import io.undertow.util.Methods;
/*     */ import io.undertow.util.SameThreadExecutor;
/*     */ import io.undertow.util.WorkerUtils;
/*     */ import java.io.Closeable;
/*     */ import java.io.IOException;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.net.URI;
/*     */ import java.nio.channels.Channel;
/*     */ import java.util.concurrent.Executor;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import org.xnio.ChannelExceptionHandler;
/*     */ import org.xnio.ChannelListener;
/*     */ import org.xnio.ChannelListeners;
/*     */ import org.xnio.IoFuture;
/*     */ import org.xnio.IoUtils;
/*     */ import org.xnio.OptionMap;
/*     */ import org.xnio.StreamConnection;
/*     */ import org.xnio.XnioExecutor;
/*     */ import org.xnio.XnioIoThread;
/*     */ import org.xnio.XnioWorker;
/*     */ import org.xnio.channels.StreamSinkChannel;
/*     */ import org.xnio.channels.StreamSourceChannel;
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
/*     */ class NodePingUtil
/*     */ {
/*     */   static void pingHost(InetSocketAddress address, HttpServerExchange exchange, PingCallback callback, OptionMap options) {
/*  85 */     XnioIoThread thread = exchange.getIoThread();
/*  86 */     XnioWorker worker = thread.getWorker();
/*  87 */     HostPingTask r = new HostPingTask(address, worker, callback, options);
/*     */     
/*  89 */     scheduleCancelTask(exchange.getIoThread(), r, 5L, TimeUnit.SECONDS);
/*  90 */     exchange.dispatch(exchange.isInIoThread() ? SameThreadExecutor.INSTANCE : (Executor)thread, r);
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
/*     */ 
/*     */ 
/*     */   
/*     */   static void pingHttpClient(URI connection, PingCallback callback, HttpServerExchange exchange, UndertowClient client, XnioSsl xnioSsl, OptionMap options) {
/* 105 */     XnioIoThread thread = exchange.getIoThread();
/* 106 */     RequestExchangeListener exchangeListener = new RequestExchangeListener(callback, NodeHealthChecker.NO_CHECK, true);
/* 107 */     Runnable r = new HttpClientPingTask(connection, exchangeListener, thread, client, xnioSsl, exchange.getConnection().getByteBufferPool(), options);
/* 108 */     exchange.dispatch(exchange.isInIoThread() ? SameThreadExecutor.INSTANCE : (Executor)thread, r);
/*     */     
/* 110 */     scheduleCancelTask(exchange.getIoThread(), exchangeListener, 5L, TimeUnit.SECONDS);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static void pingNode(final Node node, final HttpServerExchange exchange, final PingCallback callback) {
/* 121 */     if (node == null) {
/* 122 */       callback.failed();
/*     */       
/*     */       return;
/*     */     } 
/* 126 */     final int timeout = node.getNodeConfig().getPing();
/* 127 */     exchange.dispatch(exchange.isInIoThread() ? SameThreadExecutor.INSTANCE : (Executor)exchange.getIoThread(), new Runnable()
/*     */         {
/*     */           public void run() {
/* 130 */             node.getConnectionPool().connect(null, exchange, new ProxyCallback<ProxyConnection>()
/*     */                 {
/*     */                   public void completed(HttpServerExchange exchange, ProxyConnection result) {
/* 133 */                     NodePingUtil.RequestExchangeListener exchangeListener = new NodePingUtil.RequestExchangeListener(callback, NodeHealthChecker.NO_CHECK, false);
/* 134 */                     exchange.dispatch(SameThreadExecutor.INSTANCE, new NodePingUtil.ConnectionPoolPingTask(result, exchangeListener, node.getNodeConfig().getConnectionURI()));
/*     */                     
/* 136 */                     NodePingUtil.scheduleCancelTask(exchange.getIoThread(), exchangeListener, timeout, TimeUnit.SECONDS);
/*     */                   }
/*     */ 
/*     */                   
/*     */                   public void failed(HttpServerExchange exchange) {
/* 141 */                     callback.failed();
/*     */                   }
/*     */ 
/*     */                   
/*     */                   public void queuedRequestFailed(HttpServerExchange exchange) {
/* 146 */                     callback.failed();
/*     */                   }
/*     */ 
/*     */                   
/*     */                   public void couldNotResolveBackend(HttpServerExchange exchange) {
/* 151 */                     callback.failed();
/*     */                   }
/*     */                 }timeout, TimeUnit.SECONDS, false);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static void internalPingNode(Node node, PingCallback callback, NodeHealthChecker healthChecker, XnioIoThread ioThread, ByteBufferPool bufferPool, UndertowClient client, XnioSsl xnioSsl, OptionMap options) {
/* 172 */     URI uri = node.getNodeConfig().getConnectionURI();
/* 173 */     long timeout = node.getNodeConfig().getPing();
/* 174 */     RequestExchangeListener exchangeListener = new RequestExchangeListener(callback, healthChecker, true);
/* 175 */     HttpClientPingTask r = new HttpClientPingTask(uri, exchangeListener, ioThread, client, xnioSsl, bufferPool, options);
/*     */     
/* 177 */     scheduleCancelTask(ioThread, exchangeListener, timeout, TimeUnit.SECONDS);
/* 178 */     ioThread.execute(r);
/*     */   }
/*     */   
/*     */   static class ConnectionPoolPingTask
/*     */     implements Runnable {
/*     */     private final NodePingUtil.RequestExchangeListener exchangeListener;
/*     */     private final ProxyConnection proxyConnection;
/*     */     private final URI uri;
/*     */     
/*     */     ConnectionPoolPingTask(ProxyConnection proxyConnection, NodePingUtil.RequestExchangeListener exchangeListener, URI uri) {
/* 188 */       this.proxyConnection = proxyConnection;
/* 189 */       this.exchangeListener = exchangeListener;
/* 190 */       this.uri = uri;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public void run() {
/* 198 */       ClientRequest request = new ClientRequest();
/* 199 */       request.setMethod(Methods.OPTIONS);
/* 200 */       request.setPath("*");
/* 201 */       request.getRequestHeaders()
/* 202 */         .add(Headers.USER_AGENT, "mod_cluster ping")
/* 203 */         .add(Headers.HOST, this.uri.getHost());
/* 204 */       this.proxyConnection.getConnection().sendRequest(request, new ClientCallback<ClientExchange>()
/*     */           {
/*     */             public void completed(ClientExchange result) {
/* 207 */               if (NodePingUtil.ConnectionPoolPingTask.this.exchangeListener.isDone()) {
/* 208 */                 IoUtils.safeClose((Closeable)NodePingUtil.ConnectionPoolPingTask.this.proxyConnection.getConnection());
/*     */                 return;
/*     */               } 
/* 211 */               NodePingUtil.ConnectionPoolPingTask.this.exchangeListener.exchange = result;
/* 212 */               result.setResponseListener(NodePingUtil.ConnectionPoolPingTask.this.exchangeListener);
/*     */               try {
/* 214 */                 result.getRequestChannel().shutdownWrites();
/* 215 */                 if (!result.getRequestChannel().flush()) {
/* 216 */                   result.getRequestChannel().getWriteSetter().set(ChannelListeners.flushingChannelListener(null, new ChannelExceptionHandler<StreamSinkChannel>()
/*     */                         {
/*     */                           public void handleException(StreamSinkChannel channel, IOException exception) {
/* 219 */                             IoUtils.safeClose((Closeable)NodePingUtil.ConnectionPoolPingTask.this.proxyConnection.getConnection());
/* 220 */                             NodePingUtil.ConnectionPoolPingTask.this.exchangeListener.taskFailed();
/*     */                           }
/*     */                         }));
/* 223 */                   result.getRequestChannel().resumeWrites();
/*     */                 } 
/* 225 */               } catch (IOException e) {
/* 226 */                 IoUtils.safeClose((Closeable)NodePingUtil.ConnectionPoolPingTask.this.proxyConnection.getConnection());
/* 227 */                 NodePingUtil.ConnectionPoolPingTask.this.exchangeListener.taskFailed();
/*     */               } 
/*     */             }
/*     */ 
/*     */             
/*     */             public void failed(IOException e) {
/* 233 */               NodePingUtil.ConnectionPoolPingTask.this.exchangeListener.taskFailed();
/*     */             }
/*     */           });
/*     */     }
/*     */   }
/*     */   
/*     */   static class HostPingTask
/*     */     extends CancellableTask
/*     */     implements Runnable {
/*     */     private final InetSocketAddress address;
/*     */     private final XnioWorker worker;
/*     */     private final OptionMap options;
/*     */     
/*     */     HostPingTask(InetSocketAddress address, XnioWorker worker, NodePingUtil.PingCallback callback, OptionMap options) {
/* 247 */       super(callback);
/* 248 */       this.address = address;
/* 249 */       this.worker = worker;
/* 250 */       this.options = options;
/*     */     }
/*     */ 
/*     */     
/*     */     public void run() {
/*     */       try {
/* 256 */         IoFuture<StreamConnection> future = this.worker.openStreamConnection(this.address, new ChannelListener<StreamConnection>()
/*     */             {
/*     */               public void handleEvent(StreamConnection channel) {
/* 259 */                 IoUtils.safeClose((Closeable)channel);
/*     */               }
/*     */             },  this.options);
/*     */         
/* 263 */         future.addNotifier((IoFuture.Notifier)new IoFuture.HandlingNotifier<StreamConnection, Void>()
/*     */             {
/*     */               public void handleCancelled(Void attachment)
/*     */               {
/* 267 */                 NodePingUtil.HostPingTask.this.cancel();
/*     */               }
/*     */ 
/*     */               
/*     */               public void handleFailed(IOException exception, Void attachment) {
/* 272 */                 NodePingUtil.HostPingTask.this.taskFailed();
/*     */               }
/*     */ 
/*     */               
/*     */               public void handleDone(StreamConnection data, Void attachment) {
/* 277 */                 NodePingUtil.HostPingTask.this.taskCompleted();
/*     */               }
/*     */             }, 
/*     */             null);
/* 281 */       } catch (Exception e) {
/* 282 */         taskFailed();
/*     */       } 
/*     */     }
/*     */   }
/*     */   
/*     */   static class HttpClientPingTask
/*     */     implements Runnable
/*     */   {
/*     */     private final URI connection;
/*     */     private final XnioIoThread thread;
/*     */     private final UndertowClient client;
/*     */     private final XnioSsl xnioSsl;
/*     */     private final ByteBufferPool bufferPool;
/*     */     private final OptionMap options;
/*     */     private final NodePingUtil.RequestExchangeListener exchangeListener;
/*     */     
/*     */     HttpClientPingTask(URI connection, NodePingUtil.RequestExchangeListener exchangeListener, XnioIoThread thread, UndertowClient client, XnioSsl xnioSsl, ByteBufferPool bufferPool, OptionMap options) {
/* 299 */       this.connection = connection;
/* 300 */       this.thread = thread;
/* 301 */       this.client = client;
/* 302 */       this.xnioSsl = xnioSsl;
/* 303 */       this.bufferPool = bufferPool;
/* 304 */       this.options = options;
/* 305 */       this.exchangeListener = exchangeListener;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public void run() {
/* 311 */       UndertowLogger.ROOT_LOGGER.httpClientPingTask(this.connection);
/*     */       
/* 313 */       this.client.connect(new ClientCallback<ClientConnection>()
/*     */           {
/*     */             public void completed(final ClientConnection clientConnection) {
/* 316 */               if (NodePingUtil.HttpClientPingTask.this.exchangeListener.isDone()) {
/* 317 */                 IoUtils.safeClose((Closeable)clientConnection);
/*     */                 
/*     */                 return;
/*     */               } 
/* 321 */               ClientRequest request = new ClientRequest();
/* 322 */               request.setMethod(Methods.OPTIONS);
/* 323 */               request.setPath("*");
/* 324 */               request.getRequestHeaders()
/* 325 */                 .add(Headers.USER_AGENT, "mod_cluster ping")
/* 326 */                 .add(Headers.HOST, NodePingUtil.HttpClientPingTask.this.connection.getHost());
/* 327 */               clientConnection.sendRequest(request, new ClientCallback<ClientExchange>()
/*     */                   {
/*     */                     public void completed(ClientExchange result)
/*     */                     {
/* 331 */                       NodePingUtil.HttpClientPingTask.this.exchangeListener.exchange = result;
/* 332 */                       if (NodePingUtil.HttpClientPingTask.this.exchangeListener.isDone()) {
/*     */                         return;
/*     */                       }
/* 335 */                       result.setResponseListener(NodePingUtil.HttpClientPingTask.this.exchangeListener);
/*     */                       try {
/* 337 */                         result.getRequestChannel().shutdownWrites();
/* 338 */                         if (!result.getRequestChannel().flush()) {
/* 339 */                           result.getRequestChannel().getWriteSetter().set(ChannelListeners.flushingChannelListener(null, new ChannelExceptionHandler<StreamSinkChannel>()
/*     */                                 {
/*     */                                   public void handleException(StreamSinkChannel channel, IOException exception) {
/* 342 */                                     IoUtils.safeClose((Closeable)clientConnection);
/* 343 */                                     NodePingUtil.HttpClientPingTask.this.exchangeListener.taskFailed();
/*     */                                   }
/*     */                                 }));
/* 346 */                           result.getRequestChannel().resumeWrites();
/*     */                         } 
/* 348 */                       } catch (IOException e) {
/* 349 */                         IoUtils.safeClose((Closeable)clientConnection);
/* 350 */                         NodePingUtil.HttpClientPingTask.this.exchangeListener.taskFailed();
/*     */                       } 
/*     */                     }
/*     */ 
/*     */                     
/*     */                     public void failed(IOException e) {
/* 356 */                       NodePingUtil.HttpClientPingTask.this.exchangeListener.taskFailed();
/* 357 */                       IoUtils.safeClose((Closeable)clientConnection);
/*     */                     }
/*     */                   });
/*     */             }
/*     */ 
/*     */             
/*     */             public void failed(IOException e) {
/* 364 */               NodePingUtil.HttpClientPingTask.this.exchangeListener.taskFailed();
/*     */             }
/*     */           }this.connection, this.thread, this.xnioSsl, this.bufferPool, this.options);
/*     */     }
/*     */   }
/*     */   
/*     */   static class RequestExchangeListener
/*     */     extends CancellableTask
/*     */     implements ClientCallback<ClientExchange> {
/*     */     private ClientExchange exchange;
/*     */     private final boolean closeConnection;
/*     */     private final NodeHealthChecker healthChecker;
/*     */     
/*     */     RequestExchangeListener(NodePingUtil.PingCallback callback, NodeHealthChecker healthChecker, boolean closeConnection) {
/* 378 */       super(callback);
/* 379 */       assert healthChecker != null;
/* 380 */       this.closeConnection = closeConnection;
/* 381 */       this.healthChecker = healthChecker;
/*     */     }
/*     */ 
/*     */     
/*     */     public void completed(final ClientExchange result) {
/* 386 */       if (isDone()) {
/* 387 */         IoUtils.safeClose((Closeable)result.getConnection());
/*     */         return;
/*     */       } 
/* 390 */       ChannelListener<StreamSourceChannel> listener = ChannelListeners.drainListener(Long.MAX_VALUE, new ChannelListener<StreamSourceChannel>()
/*     */           {
/*     */             public void handleEvent(StreamSourceChannel channel) {
/*     */               try {
/* 394 */                 if (NodePingUtil.RequestExchangeListener.this.healthChecker.checkResponse(result.getResponse())) {
/* 395 */                   NodePingUtil.RequestExchangeListener.this.taskCompleted();
/*     */                 } else {
/* 397 */                   NodePingUtil.RequestExchangeListener.this.taskFailed();
/*     */                 } 
/*     */               } finally {
/* 400 */                 if (NodePingUtil.RequestExchangeListener.this.closeConnection && 
/* 401 */                   NodePingUtil.RequestExchangeListener.this.exchange != null) {
/* 402 */                   IoUtils.safeClose((Closeable)NodePingUtil.RequestExchangeListener.this.exchange.getConnection());
/*     */                 }
/*     */               } 
/*     */             }
/*     */           },  new ChannelExceptionHandler<StreamSourceChannel>()
/*     */           {
/*     */             public void handleException(StreamSourceChannel channel, IOException exception)
/*     */             {
/* 410 */               NodePingUtil.RequestExchangeListener.this.taskFailed();
/* 411 */               if (exception != null) {
/* 412 */                 IoUtils.safeClose((Closeable)NodePingUtil.RequestExchangeListener.this.exchange.getConnection());
/*     */               }
/*     */             }
/*     */           });
/* 416 */       StreamSourceChannel responseChannel = result.getResponseChannel();
/* 417 */       responseChannel.getReadSetter().set(listener);
/* 418 */       responseChannel.resumeReads();
/* 419 */       listener.handleEvent((Channel)responseChannel);
/*     */     }
/*     */ 
/*     */     
/*     */     public void failed(IOException e) {
/* 424 */       taskFailed();
/* 425 */       if (this.exchange != null)
/* 426 */         IoUtils.safeClose((Closeable)this.exchange.getConnection()); 
/*     */     }
/*     */   }
/*     */   
/*     */   enum State
/*     */   {
/* 432 */     WAITING, DONE, CANCELLED;
/*     */   }
/*     */   
/*     */   static class CancellableTask
/*     */   {
/*     */     private final NodePingUtil.PingCallback delegate;
/* 438 */     private volatile NodePingUtil.State state = NodePingUtil.State.WAITING;
/*     */     private volatile XnioExecutor.Key cancelKey;
/*     */     
/*     */     CancellableTask(NodePingUtil.PingCallback callback) {
/* 442 */       this.delegate = callback;
/*     */     }
/*     */     
/*     */     boolean isDone() {
/* 446 */       return (this.state != NodePingUtil.State.WAITING);
/*     */     }
/*     */     
/*     */     void setCancelKey(XnioExecutor.Key cancelKey) {
/* 450 */       if (this.state == NodePingUtil.State.WAITING) {
/* 451 */         this.cancelKey = cancelKey;
/*     */       } else {
/* 453 */         cancelKey.remove();
/*     */       } 
/*     */     }
/*     */     
/*     */     void taskCompleted() {
/* 458 */       if (this.state == NodePingUtil.State.WAITING) {
/* 459 */         this.state = NodePingUtil.State.DONE;
/* 460 */         if (this.cancelKey != null) {
/* 461 */           this.cancelKey.remove();
/*     */         }
/* 463 */         this.delegate.completed();
/*     */       } 
/*     */     }
/*     */     
/*     */     void taskFailed() {
/* 468 */       if (this.state == NodePingUtil.State.WAITING) {
/* 469 */         this.state = NodePingUtil.State.DONE;
/* 470 */         if (this.cancelKey != null) {
/* 471 */           this.cancelKey.remove();
/*     */         }
/* 473 */         this.delegate.failed();
/*     */       } 
/*     */     }
/*     */     
/*     */     void cancel() {
/* 478 */       if (this.state == NodePingUtil.State.WAITING) {
/* 479 */         this.state = NodePingUtil.State.CANCELLED;
/* 480 */         if (this.cancelKey != null) {
/* 481 */           this.cancelKey.remove();
/*     */         }
/* 483 */         this.delegate.failed();
/*     */       } 
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   static void scheduleCancelTask(XnioIoThread ioThread, final CancellableTask cancellable, long timeout, TimeUnit timeUnit) {
/* 490 */     XnioExecutor.Key key = WorkerUtils.executeAfter(ioThread, new Runnable()
/*     */         {
/*     */           public void run() {
/* 493 */             cancellable.cancel();
/*     */           }
/*     */         },  timeout, timeUnit);
/* 496 */     cancellable.setCancelKey(key);
/*     */   }
/*     */   
/*     */   static interface PingCallback {
/*     */     void completed();
/*     */     
/*     */     void failed();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\handlers\proxy\mod_cluster\NodePingUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */