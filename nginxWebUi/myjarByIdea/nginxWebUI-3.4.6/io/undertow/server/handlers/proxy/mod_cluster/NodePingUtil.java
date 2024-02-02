package io.undertow.server.handlers.proxy.mod_cluster;

import io.undertow.UndertowLogger;
import io.undertow.client.ClientCallback;
import io.undertow.client.ClientConnection;
import io.undertow.client.ClientExchange;
import io.undertow.client.ClientRequest;
import io.undertow.client.UndertowClient;
import io.undertow.connector.ByteBufferPool;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.proxy.ProxyCallback;
import io.undertow.server.handlers.proxy.ProxyClient;
import io.undertow.server.handlers.proxy.ProxyConnection;
import io.undertow.util.Headers;
import io.undertow.util.Methods;
import io.undertow.util.SameThreadExecutor;
import io.undertow.util.WorkerUtils;
import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import org.xnio.ChannelExceptionHandler;
import org.xnio.ChannelListener;
import org.xnio.ChannelListeners;
import org.xnio.IoFuture;
import org.xnio.IoUtils;
import org.xnio.OptionMap;
import org.xnio.StreamConnection;
import org.xnio.XnioExecutor;
import org.xnio.XnioIoThread;
import org.xnio.XnioWorker;
import org.xnio.channels.StreamSinkChannel;
import org.xnio.channels.StreamSourceChannel;
import org.xnio.ssl.XnioSsl;

class NodePingUtil {
   static void pingHost(InetSocketAddress address, HttpServerExchange exchange, PingCallback callback, OptionMap options) {
      XnioIoThread thread = exchange.getIoThread();
      XnioWorker worker = thread.getWorker();
      HostPingTask r = new HostPingTask(address, worker, callback, options);
      scheduleCancelTask(exchange.getIoThread(), r, 5L, TimeUnit.SECONDS);
      exchange.dispatch((Executor)(exchange.isInIoThread() ? SameThreadExecutor.INSTANCE : thread), (Runnable)r);
   }

   static void pingHttpClient(URI connection, PingCallback callback, HttpServerExchange exchange, UndertowClient client, XnioSsl xnioSsl, OptionMap options) {
      XnioIoThread thread = exchange.getIoThread();
      RequestExchangeListener exchangeListener = new RequestExchangeListener(callback, NodeHealthChecker.NO_CHECK, true);
      Runnable r = new HttpClientPingTask(connection, exchangeListener, thread, client, xnioSsl, exchange.getConnection().getByteBufferPool(), options);
      exchange.dispatch((Executor)(exchange.isInIoThread() ? SameThreadExecutor.INSTANCE : thread), (Runnable)r);
      scheduleCancelTask(exchange.getIoThread(), exchangeListener, 5L, TimeUnit.SECONDS);
   }

   static void pingNode(final Node node, final HttpServerExchange exchange, final PingCallback callback) {
      if (node == null) {
         callback.failed();
      } else {
         final int timeout = node.getNodeConfig().getPing();
         exchange.dispatch((Executor)(exchange.isInIoThread() ? SameThreadExecutor.INSTANCE : exchange.getIoThread()), (Runnable)(new Runnable() {
            public void run() {
               node.getConnectionPool().connect((ProxyClient.ProxyTarget)null, exchange, new ProxyCallback<ProxyConnection>() {
                  public void completed(HttpServerExchange exchangex, ProxyConnection result) {
                     RequestExchangeListener exchangeListener = new RequestExchangeListener(callback, NodeHealthChecker.NO_CHECK, false);
                     exchangex.dispatch(SameThreadExecutor.INSTANCE, (Runnable)(new ConnectionPoolPingTask(result, exchangeListener, node.getNodeConfig().getConnectionURI())));
                     NodePingUtil.scheduleCancelTask(exchangex.getIoThread(), exchangeListener, (long)timeout, TimeUnit.SECONDS);
                  }

                  public void failed(HttpServerExchange exchangex) {
                     callback.failed();
                  }

                  public void queuedRequestFailed(HttpServerExchange exchangex) {
                     callback.failed();
                  }

                  public void couldNotResolveBackend(HttpServerExchange exchangex) {
                     callback.failed();
                  }
               }, (long)timeout, TimeUnit.SECONDS, false);
            }
         }));
      }
   }

   static void internalPingNode(Node node, PingCallback callback, NodeHealthChecker healthChecker, XnioIoThread ioThread, ByteBufferPool bufferPool, UndertowClient client, XnioSsl xnioSsl, OptionMap options) {
      URI uri = node.getNodeConfig().getConnectionURI();
      long timeout = (long)node.getNodeConfig().getPing();
      RequestExchangeListener exchangeListener = new RequestExchangeListener(callback, healthChecker, true);
      HttpClientPingTask r = new HttpClientPingTask(uri, exchangeListener, ioThread, client, xnioSsl, bufferPool, options);
      scheduleCancelTask(ioThread, exchangeListener, timeout, TimeUnit.SECONDS);
      ioThread.execute(r);
   }

   static void scheduleCancelTask(XnioIoThread ioThread, final CancellableTask cancellable, long timeout, TimeUnit timeUnit) {
      XnioExecutor.Key key = WorkerUtils.executeAfter(ioThread, new Runnable() {
         public void run() {
            cancellable.cancel();
         }
      }, timeout, timeUnit);
      cancellable.setCancelKey(key);
   }

   static class CancellableTask {
      private final PingCallback delegate;
      private volatile State state;
      private volatile XnioExecutor.Key cancelKey;

      CancellableTask(PingCallback callback) {
         this.state = NodePingUtil.State.WAITING;
         this.delegate = callback;
      }

      boolean isDone() {
         return this.state != NodePingUtil.State.WAITING;
      }

      void setCancelKey(XnioExecutor.Key cancelKey) {
         if (this.state == NodePingUtil.State.WAITING) {
            this.cancelKey = cancelKey;
         } else {
            cancelKey.remove();
         }

      }

      void taskCompleted() {
         if (this.state == NodePingUtil.State.WAITING) {
            this.state = NodePingUtil.State.DONE;
            if (this.cancelKey != null) {
               this.cancelKey.remove();
            }

            this.delegate.completed();
         }

      }

      void taskFailed() {
         if (this.state == NodePingUtil.State.WAITING) {
            this.state = NodePingUtil.State.DONE;
            if (this.cancelKey != null) {
               this.cancelKey.remove();
            }

            this.delegate.failed();
         }

      }

      void cancel() {
         if (this.state == NodePingUtil.State.WAITING) {
            this.state = NodePingUtil.State.CANCELLED;
            if (this.cancelKey != null) {
               this.cancelKey.remove();
            }

            this.delegate.failed();
         }

      }
   }

   static enum State {
      WAITING,
      DONE,
      CANCELLED;
   }

   static class RequestExchangeListener extends CancellableTask implements ClientCallback<ClientExchange> {
      private ClientExchange exchange;
      private final boolean closeConnection;
      private final NodeHealthChecker healthChecker;

      RequestExchangeListener(PingCallback callback, NodeHealthChecker healthChecker, boolean closeConnection) {
         super(callback);

         assert healthChecker != null;

         this.closeConnection = closeConnection;
         this.healthChecker = healthChecker;
      }

      public void completed(final ClientExchange result) {
         if (this.isDone()) {
            IoUtils.safeClose((Closeable)result.getConnection());
         } else {
            ChannelListener<StreamSourceChannel> listener = ChannelListeners.drainListener(Long.MAX_VALUE, new ChannelListener<StreamSourceChannel>() {
               public void handleEvent(StreamSourceChannel channel) {
                  try {
                     if (RequestExchangeListener.this.healthChecker.checkResponse(result.getResponse())) {
                        RequestExchangeListener.this.taskCompleted();
                     } else {
                        RequestExchangeListener.this.taskFailed();
                     }
                  } finally {
                     if (RequestExchangeListener.this.closeConnection && RequestExchangeListener.this.exchange != null) {
                        IoUtils.safeClose((Closeable)RequestExchangeListener.this.exchange.getConnection());
                     }

                  }

               }
            }, new ChannelExceptionHandler<StreamSourceChannel>() {
               public void handleException(StreamSourceChannel channel, IOException exception) {
                  RequestExchangeListener.this.taskFailed();
                  if (exception != null) {
                     IoUtils.safeClose((Closeable)RequestExchangeListener.this.exchange.getConnection());
                  }

               }
            });
            StreamSourceChannel responseChannel = result.getResponseChannel();
            responseChannel.getReadSetter().set(listener);
            responseChannel.resumeReads();
            listener.handleEvent(responseChannel);
         }
      }

      public void failed(IOException e) {
         this.taskFailed();
         if (this.exchange != null) {
            IoUtils.safeClose((Closeable)this.exchange.getConnection());
         }

      }
   }

   static class HttpClientPingTask implements Runnable {
      private final URI connection;
      private final XnioIoThread thread;
      private final UndertowClient client;
      private final XnioSsl xnioSsl;
      private final ByteBufferPool bufferPool;
      private final OptionMap options;
      private final RequestExchangeListener exchangeListener;

      HttpClientPingTask(URI connection, RequestExchangeListener exchangeListener, XnioIoThread thread, UndertowClient client, XnioSsl xnioSsl, ByteBufferPool bufferPool, OptionMap options) {
         this.connection = connection;
         this.thread = thread;
         this.client = client;
         this.xnioSsl = xnioSsl;
         this.bufferPool = bufferPool;
         this.options = options;
         this.exchangeListener = exchangeListener;
      }

      public void run() {
         UndertowLogger.ROOT_LOGGER.httpClientPingTask(this.connection);
         this.client.connect(new ClientCallback<ClientConnection>() {
            public void completed(final ClientConnection clientConnection) {
               if (HttpClientPingTask.this.exchangeListener.isDone()) {
                  IoUtils.safeClose((Closeable)clientConnection);
               } else {
                  ClientRequest request = new ClientRequest();
                  request.setMethod(Methods.OPTIONS);
                  request.setPath("*");
                  request.getRequestHeaders().add(Headers.USER_AGENT, "mod_cluster ping").add(Headers.HOST, HttpClientPingTask.this.connection.getHost());
                  clientConnection.sendRequest(request, new ClientCallback<ClientExchange>() {
                     public void completed(ClientExchange result) {
                        HttpClientPingTask.this.exchangeListener.exchange = result;
                        if (!HttpClientPingTask.this.exchangeListener.isDone()) {
                           result.setResponseListener(HttpClientPingTask.this.exchangeListener);

                           try {
                              result.getRequestChannel().shutdownWrites();
                              if (!result.getRequestChannel().flush()) {
                                 result.getRequestChannel().getWriteSetter().set(ChannelListeners.flushingChannelListener((ChannelListener)null, new ChannelExceptionHandler<StreamSinkChannel>() {
                                    public void handleException(StreamSinkChannel channel, IOException exception) {
                                       IoUtils.safeClose((Closeable)clientConnection);
                                       HttpClientPingTask.this.exchangeListener.taskFailed();
                                    }
                                 }));
                                 result.getRequestChannel().resumeWrites();
                              }
                           } catch (IOException var3) {
                              IoUtils.safeClose((Closeable)clientConnection);
                              HttpClientPingTask.this.exchangeListener.taskFailed();
                           }

                        }
                     }

                     public void failed(IOException e) {
                        HttpClientPingTask.this.exchangeListener.taskFailed();
                        IoUtils.safeClose((Closeable)clientConnection);
                     }
                  });
               }
            }

            public void failed(IOException e) {
               HttpClientPingTask.this.exchangeListener.taskFailed();
            }
         }, this.connection, this.thread, this.xnioSsl, this.bufferPool, this.options);
      }
   }

   static class HostPingTask extends CancellableTask implements Runnable {
      private final InetSocketAddress address;
      private final XnioWorker worker;
      private final OptionMap options;

      HostPingTask(InetSocketAddress address, XnioWorker worker, PingCallback callback, OptionMap options) {
         super(callback);
         this.address = address;
         this.worker = worker;
         this.options = options;
      }

      public void run() {
         try {
            IoFuture<StreamConnection> future = this.worker.openStreamConnection(this.address, new ChannelListener<StreamConnection>() {
               public void handleEvent(StreamConnection channel) {
                  IoUtils.safeClose((Closeable)channel);
               }
            }, this.options);
            future.addNotifier(new IoFuture.HandlingNotifier<StreamConnection, Void>() {
               public void handleCancelled(Void attachment) {
                  HostPingTask.this.cancel();
               }

               public void handleFailed(IOException exception, Void attachment) {
                  HostPingTask.this.taskFailed();
               }

               public void handleDone(StreamConnection data, Void attachment) {
                  HostPingTask.this.taskCompleted();
               }
            }, (Object)null);
         } catch (Exception var2) {
            this.taskFailed();
         }

      }
   }

   static class ConnectionPoolPingTask implements Runnable {
      private final RequestExchangeListener exchangeListener;
      private final ProxyConnection proxyConnection;
      private final URI uri;

      ConnectionPoolPingTask(ProxyConnection proxyConnection, RequestExchangeListener exchangeListener, URI uri) {
         this.proxyConnection = proxyConnection;
         this.exchangeListener = exchangeListener;
         this.uri = uri;
      }

      public void run() {
         ClientRequest request = new ClientRequest();
         request.setMethod(Methods.OPTIONS);
         request.setPath("*");
         request.getRequestHeaders().add(Headers.USER_AGENT, "mod_cluster ping").add(Headers.HOST, this.uri.getHost());
         this.proxyConnection.getConnection().sendRequest(request, new ClientCallback<ClientExchange>() {
            public void completed(ClientExchange result) {
               if (ConnectionPoolPingTask.this.exchangeListener.isDone()) {
                  IoUtils.safeClose((Closeable)ConnectionPoolPingTask.this.proxyConnection.getConnection());
               } else {
                  ConnectionPoolPingTask.this.exchangeListener.exchange = result;
                  result.setResponseListener(ConnectionPoolPingTask.this.exchangeListener);

                  try {
                     result.getRequestChannel().shutdownWrites();
                     if (!result.getRequestChannel().flush()) {
                        result.getRequestChannel().getWriteSetter().set(ChannelListeners.flushingChannelListener((ChannelListener)null, new ChannelExceptionHandler<StreamSinkChannel>() {
                           public void handleException(StreamSinkChannel channel, IOException exception) {
                              IoUtils.safeClose((Closeable)ConnectionPoolPingTask.this.proxyConnection.getConnection());
                              ConnectionPoolPingTask.this.exchangeListener.taskFailed();
                           }
                        }));
                        result.getRequestChannel().resumeWrites();
                     }
                  } catch (IOException var3) {
                     IoUtils.safeClose((Closeable)ConnectionPoolPingTask.this.proxyConnection.getConnection());
                     ConnectionPoolPingTask.this.exchangeListener.taskFailed();
                  }

               }
            }

            public void failed(IOException e) {
               ConnectionPoolPingTask.this.exchangeListener.taskFailed();
            }
         });
      }
   }

   interface PingCallback {
      void completed();

      void failed();
   }
}
