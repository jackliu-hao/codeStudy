package io.undertow.server.handlers.proxy;

import io.undertow.UndertowLogger;
import io.undertow.UndertowMessages;
import io.undertow.client.ClientCallback;
import io.undertow.client.ClientConnection;
import io.undertow.client.ClientStatistics;
import io.undertow.client.UndertowClient;
import io.undertow.server.ExchangeCompletionListener;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.CopyOnWriteMap;
import io.undertow.util.Headers;
import io.undertow.util.WorkerUtils;
import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import org.xnio.ChannelListener;
import org.xnio.IoUtils;
import org.xnio.OptionMap;
import org.xnio.XnioExecutor;
import org.xnio.XnioIoThread;
import org.xnio.ssl.XnioSsl;

public class ProxyConnectionPool implements Closeable {
   private final URI uri;
   private final InetSocketAddress bindAddress;
   private final XnioSsl ssl;
   private final UndertowClient client;
   private final ConnectionPoolManager connectionPoolManager;
   private final OptionMap options;
   private volatile boolean closed;
   private final int maxConnections;
   private final int maxCachedConnections;
   private final int coreCachedConnections;
   private final long timeToLive;
   private final AtomicInteger openConnections;
   private final AtomicLong requestCount;
   private final AtomicLong read;
   private final AtomicLong written;
   private final ConcurrentMap<XnioIoThread, HostThreadData> hostThreadData;

   public ProxyConnectionPool(ConnectionPoolManager connectionPoolManager, URI uri, UndertowClient client, OptionMap options) {
      this(connectionPoolManager, (URI)uri, (XnioSsl)null, client, options);
   }

   public ProxyConnectionPool(ConnectionPoolManager connectionPoolManager, InetSocketAddress bindAddress, URI uri, UndertowClient client, OptionMap options) {
      this(connectionPoolManager, bindAddress, uri, (XnioSsl)null, client, options);
   }

   public ProxyConnectionPool(ConnectionPoolManager connectionPoolManager, URI uri, XnioSsl ssl, UndertowClient client, OptionMap options) {
      this(connectionPoolManager, (InetSocketAddress)null, uri, ssl, client, options);
   }

   public ProxyConnectionPool(ConnectionPoolManager connectionPoolManager, InetSocketAddress bindAddress, URI uri, XnioSsl ssl, UndertowClient client, OptionMap options) {
      this.openConnections = new AtomicInteger(0);
      this.requestCount = new AtomicLong();
      this.read = new AtomicLong();
      this.written = new AtomicLong();
      this.hostThreadData = new CopyOnWriteMap();
      this.connectionPoolManager = connectionPoolManager;
      this.maxConnections = Math.max(connectionPoolManager.getMaxConnections(), 1);
      this.maxCachedConnections = Math.max(connectionPoolManager.getMaxCachedConnections(), 0);
      this.coreCachedConnections = Math.max(connectionPoolManager.getSMaxConnections(), 0);
      this.timeToLive = connectionPoolManager.getTtl();
      this.bindAddress = bindAddress;
      this.uri = uri;
      this.ssl = ssl;
      this.client = client;
      this.options = options;
   }

   public URI getUri() {
      return this.uri;
   }

   public InetSocketAddress getBindAddress() {
      return this.bindAddress;
   }

   public void close() {
      this.closed = true;
      Iterator var1 = this.hostThreadData.values().iterator();

      while(var1.hasNext()) {
         HostThreadData data = (HostThreadData)var1.next();
         final ConnectionHolder holder = (ConnectionHolder)data.availableConnections.poll();
         if (holder != null) {
            holder.clientConnection.getIoThread().execute(new Runnable() {
               public void run() {
                  IoUtils.safeClose((Closeable)holder.clientConnection);
               }
            });
         }
      }

   }

   private void returnConnection(ConnectionHolder connectionHolder) {
      ClientStatistics stats = connectionHolder.clientConnection.getStatistics();
      this.requestCount.incrementAndGet();
      if (stats != null) {
         this.read.addAndGet(stats.getRead());
         this.written.addAndGet(stats.getWritten());
         stats.reset();
      }

      HostThreadData hostData = this.getData();
      if (this.closed) {
         IoUtils.safeClose((Closeable)connectionHolder.clientConnection);

         for(ConnectionHolder con = (ConnectionHolder)hostData.availableConnections.poll(); con != null; con = (ConnectionHolder)hostData.availableConnections.poll()) {
            IoUtils.safeClose((Closeable)con.clientConnection);
         }

         this.redistributeQueued(hostData);
      } else {
         ClientConnection connection = connectionHolder.clientConnection;
         if (connection.isOpen() && !connection.isUpgraded()) {
            CallbackHolder callback;
            for(callback = (CallbackHolder)hostData.awaitingConnections.poll(); callback != null && callback.isCancelled(); callback = (CallbackHolder)hostData.awaitingConnections.poll()) {
            }

            if (callback != null) {
               if (callback.getTimeoutKey() != null) {
                  callback.getTimeoutKey().remove();
               }

               this.connectionReady(connectionHolder, callback.getCallback(), callback.getExchange(), false);
            } else {
               int cachedConnectionCount = hostData.availableConnections.size();
               if (cachedConnectionCount >= this.maxCachedConnections) {
                  ConnectionHolder holder = (ConnectionHolder)hostData.availableConnections.poll();
                  if (holder != null) {
                     IoUtils.safeClose((Closeable)holder.clientConnection);
                  }
               }

               hostData.availableConnections.add(connectionHolder);
               if (this.timeToLive > 0L) {
                  long currentTime = System.currentTimeMillis();
                  connectionHolder.timeout = currentTime + this.timeToLive;
                  if (hostData.availableConnections.size() > this.coreCachedConnections && hostData.nextTimeout <= 0L) {
                     hostData.timeoutKey = WorkerUtils.executeAfter(connection.getIoThread(), hostData.timeoutTask, this.timeToLive, TimeUnit.MILLISECONDS);
                     hostData.nextTimeout = connectionHolder.timeout;
                  }
               }
            }
         } else if (connection.isOpen() && connection.isUpgraded()) {
            connection.getCloseSetter().set((ChannelListener)null);
            this.handleClosedConnection(hostData, connectionHolder);
         }

      }
   }

   private void handleClosedConnection(HostThreadData hostData, ConnectionHolder connection) {
      this.openConnections.decrementAndGet();
      int connections = --hostData.connections;
      hostData.availableConnections.remove(connection);
      if (connections < this.maxConnections) {
         CallbackHolder task;
         for(task = (CallbackHolder)hostData.awaitingConnections.poll(); task != null && task.isCancelled(); task = (CallbackHolder)hostData.awaitingConnections.poll()) {
         }

         if (task != null) {
            this.openConnection(task.exchange, task.callback, hostData, false);
         }
      }

   }

   private void openConnection(final HttpServerExchange exchange, final ProxyCallback<ProxyConnection> callback, final HostThreadData data, final boolean exclusive) {
      if (!exclusive) {
         ++data.connections;
      }

      try {
         this.client.connect(new ClientCallback<ClientConnection>() {
            public void completed(ClientConnection result) {
               ProxyConnectionPool.this.openConnections.incrementAndGet();
               final ConnectionHolder connectionHolder = new ConnectionHolder(result);
               if (!exclusive) {
                  result.getCloseSetter().set(new ChannelListener<ClientConnection>() {
                     public void handleEvent(ClientConnection channel) {
                        ProxyConnectionPool.this.handleClosedConnection(data, connectionHolder);
                     }
                  });
               }

               ProxyConnectionPool.this.connectionReady(connectionHolder, callback, exchange, exclusive);
            }

            public void failed(IOException e) {
               if (!exclusive) {
                  --data.connections;
               }

               UndertowLogger.REQUEST_LOGGER.debug("Failed to connect", e);
               if (!ProxyConnectionPool.this.connectionPoolManager.handleError()) {
                  ProxyConnectionPool.this.redistributeQueued(ProxyConnectionPool.this.getData());
                  ProxyConnectionPool.this.scheduleFailedHostRetry(exchange);
               }

               callback.failed(exchange);
            }
         }, this.bindAddress, this.getUri(), exchange.getIoThread(), this.ssl, exchange.getConnection().getByteBufferPool(), this.options);
      } catch (RuntimeException var6) {
         if (!exclusive) {
            --data.connections;
         }

         this.connectionPoolManager.handleError();
         callback.failed(exchange);
         throw var6;
      }
   }

   private void redistributeQueued(HostThreadData hostData) {
      for(CallbackHolder callback = (CallbackHolder)hostData.awaitingConnections.poll(); callback != null; callback = (CallbackHolder)hostData.awaitingConnections.poll()) {
         if (callback.getTimeoutKey() != null) {
            callback.getTimeoutKey().remove();
         }

         if (!callback.isCancelled()) {
            long time = System.currentTimeMillis();
            if (callback.getExpireTime() > 0L && callback.getExpireTime() < time) {
               callback.getCallback().failed(callback.getExchange());
            } else {
               callback.getCallback().queuedRequestFailed(callback.getExchange());
            }
         }
      }

   }

   private void connectionReady(final ConnectionHolder result, ProxyCallback<ProxyConnection> callback, HttpServerExchange exchange, final boolean exclusive) {
      try {
         exchange.addExchangeCompleteListener(new ExchangeCompletionListener() {
            public void exchangeEvent(HttpServerExchange exchange, ExchangeCompletionListener.NextListener nextListener) {
               if (!exclusive) {
                  ProxyConnectionPool.this.returnConnection(result);
               }

               nextListener.proceed();
            }
         });
      } catch (Exception var6) {
         this.returnConnection(result);
         callback.failed(exchange);
         return;
      }

      callback.completed(exchange, new ProxyConnection(result.clientConnection, this.uri.getPath() == null ? "/" : this.uri.getPath()));
   }

   public AvailabilityType available() {
      if (this.closed) {
         return ProxyConnectionPool.AvailabilityType.CLOSED;
      } else if (!this.connectionPoolManager.isAvailable()) {
         return ProxyConnectionPool.AvailabilityType.PROBLEM;
      } else {
         HostThreadData data = this.getData();
         if (data.connections < this.maxConnections) {
            return ProxyConnectionPool.AvailabilityType.AVAILABLE;
         } else if (!data.availableConnections.isEmpty()) {
            return ProxyConnectionPool.AvailabilityType.AVAILABLE;
         } else {
            return data.awaitingConnections.size() >= this.connectionPoolManager.getMaxQueueSize() ? ProxyConnectionPool.AvailabilityType.FULL_QUEUE : ProxyConnectionPool.AvailabilityType.FULL;
         }
      }
   }

   private void scheduleFailedHostRetry(final HttpServerExchange exchange) {
      int retry = this.connectionPoolManager.getProblemServerRetry();
      if (retry > 0 && !this.connectionPoolManager.isAvailable()) {
         WorkerUtils.executeAfter(exchange.getIoThread(), new Runnable() {
            public void run() {
               if (!ProxyConnectionPool.this.closed) {
                  UndertowLogger.PROXY_REQUEST_LOGGER.debugf("Attempting to reconnect to failed host %s", ProxyConnectionPool.this.getUri());

                  try {
                     ProxyConnectionPool.this.client.connect(new ClientCallback<ClientConnection>() {
                        public void completed(ClientConnection result) {
                           UndertowLogger.PROXY_REQUEST_LOGGER.debugf("Connected to previously failed host %s, returning to service", ProxyConnectionPool.this.getUri());
                           if (ProxyConnectionPool.this.connectionPoolManager.clearError()) {
                              final ConnectionHolder connectionHolder = new ConnectionHolder(result);
                              final HostThreadData data = ProxyConnectionPool.this.getData();
                              result.getCloseSetter().set(new ChannelListener<ClientConnection>() {
                                 public void handleEvent(ClientConnection channel) {
                                    ProxyConnectionPool.this.handleClosedConnection(data, connectionHolder);
                                 }
                              });
                              ++data.connections;
                              ProxyConnectionPool.this.returnConnection(connectionHolder);
                           } else {
                              ProxyConnectionPool.this.scheduleFailedHostRetry(exchange);
                           }

                        }

                        public void failed(IOException e) {
                           UndertowLogger.PROXY_REQUEST_LOGGER.debugf("Failed to reconnect to failed host %s", ProxyConnectionPool.this.getUri());
                           ProxyConnectionPool.this.connectionPoolManager.handleError();
                           ProxyConnectionPool.this.scheduleFailedHostRetry(exchange);
                        }
                     }, ProxyConnectionPool.this.bindAddress, ProxyConnectionPool.this.getUri(), exchange.getIoThread(), ProxyConnectionPool.this.ssl, exchange.getConnection().getByteBufferPool(), ProxyConnectionPool.this.options);
                  } catch (RuntimeException var2) {
                     ProxyConnectionPool.this.connectionPoolManager.handleError();
                     ProxyConnectionPool.this.scheduleFailedHostRetry(exchange);
                  }

               }
            }
         }, (long)retry, TimeUnit.SECONDS);
      }

   }

   private void timeoutConnections(long currentTime, HostThreadData data) {
      int idleConnections = data.availableConnections.size();

      ConnectionHolder holder;
      while(idleConnections > 0 && idleConnections > this.coreCachedConnections && (holder = (ConnectionHolder)data.availableConnections.peek()) != null) {
         if (!holder.clientConnection.isOpen()) {
            --idleConnections;
         } else {
            if (currentTime < holder.timeout) {
               if (data.timeoutKey != null) {
                  data.timeoutKey.remove();
                  data.timeoutKey = null;
               }

               long remaining = holder.timeout - currentTime + 1L;
               data.nextTimeout = holder.timeout;
               data.timeoutKey = WorkerUtils.executeAfter(holder.clientConnection.getIoThread(), data.timeoutTask, remaining, TimeUnit.MILLISECONDS);
               return;
            }

            holder = (ConnectionHolder)data.availableConnections.poll();
            IoUtils.safeClose((Closeable)holder.clientConnection);
            --idleConnections;
         }
      }

      if (data.timeoutKey != null) {
         data.timeoutKey.remove();
         data.timeoutKey = null;
      }

      data.nextTimeout = -1L;
   }

   private HostThreadData getData() {
      Thread thread = Thread.currentThread();
      if (!(thread instanceof XnioIoThread)) {
         throw UndertowMessages.MESSAGES.canOnlyBeCalledByIoThread();
      } else {
         XnioIoThread ioThread = (XnioIoThread)thread;
         HostThreadData data = (HostThreadData)this.hostThreadData.get(ioThread);
         if (data != null) {
            return data;
         } else {
            data = new HostThreadData();
            HostThreadData existing = (HostThreadData)this.hostThreadData.putIfAbsent(ioThread, data);
            return existing != null ? existing : data;
         }
      }
   }

   public ClientStatistics getClientStatistics() {
      return new ClientStatistics() {
         public long getRequests() {
            return ProxyConnectionPool.this.requestCount.get();
         }

         public long getRead() {
            return ProxyConnectionPool.this.read.get();
         }

         public long getWritten() {
            return ProxyConnectionPool.this.written.get();
         }

         public void reset() {
            ProxyConnectionPool.this.requestCount.set(0L);
            ProxyConnectionPool.this.read.set(0L);
            ProxyConnectionPool.this.written.set(0L);
         }
      };
   }

   public int getOpenConnections() {
      return this.openConnections.get();
   }

   public void connect(ProxyClient.ProxyTarget proxyTarget, HttpServerExchange exchange, ProxyCallback<ProxyConnection> callback, long timeout, TimeUnit timeUnit, boolean exclusive) {
      HostThreadData data = this.getData();

      ConnectionHolder connectionHolder;
      for(connectionHolder = (ConnectionHolder)data.availableConnections.poll(); connectionHolder != null && !connectionHolder.clientConnection.isOpen(); connectionHolder = (ConnectionHolder)data.availableConnections.poll()) {
      }

      boolean upgradeRequest = exchange.getRequestHeaders().contains(Headers.UPGRADE);
      if (connectionHolder != null && (!upgradeRequest || connectionHolder.clientConnection.isUpgradeSupported())) {
         if (exclusive) {
            --data.connections;
         }

         this.connectionReady(connectionHolder, callback, exchange, exclusive);
      } else if (!exclusive && data.connections >= this.maxConnections) {
         if (data.awaitingConnections.size() >= this.connectionPoolManager.getMaxQueueSize()) {
            callback.queuedRequestFailed(exchange);
            return;
         }

         CallbackHolder holder;
         if (timeout > 0L) {
            long time = System.currentTimeMillis();
            holder = new CallbackHolder(proxyTarget, callback, exchange, time + timeUnit.toMillis(timeout));
            holder.setTimeoutKey(WorkerUtils.executeAfter(exchange.getIoThread(), holder, timeout, timeUnit));
         } else {
            holder = new CallbackHolder(proxyTarget, callback, exchange, -1L);
         }

         data.awaitingConnections.add(holder);
      } else {
         this.openConnection(exchange, callback, data, exclusive);
      }

   }

   void closeCurrentConnections() {
      final CountDownLatch latch = new CountDownLatch(this.hostThreadData.size());
      Iterator var2 = this.hostThreadData.entrySet().iterator();

      while(var2.hasNext()) {
         final Map.Entry<XnioIoThread, HostThreadData> data = (Map.Entry)var2.next();
         ((XnioIoThread)data.getKey()).execute(new Runnable() {
            public void run() {
               for(ConnectionHolder d = (ConnectionHolder)((HostThreadData)data.getValue()).availableConnections.poll(); d != null; d = (ConnectionHolder)((HostThreadData)data.getValue()).availableConnections.poll()) {
                  IoUtils.safeClose((Closeable)d.clientConnection);
               }

               ((HostThreadData)data.getValue()).connections = 0;
               latch.countDown();
            }
         });
      }

      try {
         latch.await(10L, TimeUnit.SECONDS);
      } catch (InterruptedException var4) {
         Thread.currentThread().interrupt();
         throw new RuntimeException(var4);
      }
   }

   public static enum AvailabilityType {
      AVAILABLE,
      DRAIN,
      FULL,
      FULL_QUEUE,
      PROBLEM,
      CLOSED;
   }

   private static final class CallbackHolder implements Runnable {
      final ProxyClient.ProxyTarget proxyTarget;
      final ProxyCallback<ProxyConnection> callback;
      final HttpServerExchange exchange;
      final long expireTime;
      XnioExecutor.Key timeoutKey;
      boolean cancelled;

      private CallbackHolder(ProxyClient.ProxyTarget proxyTarget, ProxyCallback<ProxyConnection> callback, HttpServerExchange exchange, long expireTime) {
         this.cancelled = false;
         this.proxyTarget = proxyTarget;
         this.callback = callback;
         this.exchange = exchange;
         this.expireTime = expireTime;
      }

      private ProxyCallback<ProxyConnection> getCallback() {
         return this.callback;
      }

      private HttpServerExchange getExchange() {
         return this.exchange;
      }

      private long getExpireTime() {
         return this.expireTime;
      }

      private XnioExecutor.Key getTimeoutKey() {
         return this.timeoutKey;
      }

      private boolean isCancelled() {
         return this.cancelled || this.exchange.isResponseStarted();
      }

      private void setTimeoutKey(XnioExecutor.Key timeoutKey) {
         this.timeoutKey = timeoutKey;
      }

      public void run() {
         this.cancelled = true;
         this.callback.failed(this.exchange);
      }

      public ProxyClient.ProxyTarget getProxyTarget() {
         return this.proxyTarget;
      }

      // $FF: synthetic method
      CallbackHolder(ProxyClient.ProxyTarget x0, ProxyCallback x1, HttpServerExchange x2, long x3, Object x4) {
         this(x0, x1, x2, x3);
      }
   }

   private static final class ConnectionHolder {
      private long timeout;
      private final ClientConnection clientConnection;

      private ConnectionHolder(ClientConnection clientConnection) {
         this.clientConnection = clientConnection;
      }

      // $FF: synthetic method
      ConnectionHolder(ClientConnection x0, Object x1) {
         this(x0);
      }
   }

   private final class HostThreadData {
      int connections;
      XnioExecutor.Key timeoutKey;
      long nextTimeout;
      final Deque<ConnectionHolder> availableConnections;
      final Deque<CallbackHolder> awaitingConnections;
      final Runnable timeoutTask;

      private HostThreadData() {
         this.connections = 0;
         this.nextTimeout = -1L;
         this.availableConnections = new ArrayDeque();
         this.awaitingConnections = new ArrayDeque();
         this.timeoutTask = new Runnable() {
            public void run() {
               long currentTime = System.currentTimeMillis();
               ProxyConnectionPool.this.timeoutConnections(currentTime, HostThreadData.this);
            }
         };
      }

      // $FF: synthetic method
      HostThreadData(Object x1) {
         this();
      }
   }
}
