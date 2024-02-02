package io.undertow;

import io.undertow.connector.ByteBufferPool;
import io.undertow.protocols.ssl.UndertowXnioSsl;
import io.undertow.server.ConnectorStatistics;
import io.undertow.server.DefaultByteBufferPool;
import io.undertow.server.HttpHandler;
import io.undertow.server.OpenListener;
import io.undertow.server.protocol.ajp.AjpOpenListener;
import io.undertow.server.protocol.http.AlpnOpenListener;
import io.undertow.server.protocol.http.HttpOpenListener;
import io.undertow.server.protocol.http2.Http2OpenListener;
import io.undertow.server.protocol.http2.Http2UpgradeHandler;
import io.undertow.server.protocol.proxy.ProxyProtocolOpenListener;
import java.io.Closeable;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import org.xnio.ChannelListener;
import org.xnio.ChannelListeners;
import org.xnio.IoUtils;
import org.xnio.Option;
import org.xnio.OptionMap;
import org.xnio.Options;
import org.xnio.StreamConnection;
import org.xnio.Xnio;
import org.xnio.XnioWorker;
import org.xnio.channels.AcceptingChannel;
import org.xnio.ssl.JsseSslUtils;

public final class Undertow {
   private final int bufferSize;
   private final int ioThreads;
   private final int workerThreads;
   private final boolean directBuffers;
   private final List<ListenerConfig> listeners;
   private volatile List<ListenerInfo> listenerInfo;
   private final HttpHandler rootHandler;
   private final OptionMap workerOptions;
   private final OptionMap socketOptions;
   private final OptionMap serverOptions;
   private final boolean internalWorker;
   private ByteBufferPool byteBufferPool;
   private XnioWorker worker;
   private Executor sslEngineDelegatedTaskExecutor;
   private List<AcceptingChannel<? extends StreamConnection>> channels;
   private Xnio xnio;

   private Undertow(Builder builder) {
      this.listeners = new ArrayList();
      this.byteBufferPool = builder.byteBufferPool;
      this.bufferSize = this.byteBufferPool != null ? this.byteBufferPool.getBufferSize() : builder.bufferSize;
      this.directBuffers = this.byteBufferPool != null ? this.byteBufferPool.isDirect() : builder.directBuffers;
      this.ioThreads = builder.ioThreads;
      this.workerThreads = builder.workerThreads;
      this.listeners.addAll(builder.listeners);
      this.rootHandler = builder.handler;
      this.worker = builder.worker;
      this.sslEngineDelegatedTaskExecutor = builder.sslEngineDelegatedTaskExecutor;
      this.internalWorker = builder.worker == null;
      this.workerOptions = builder.workerOptions.getMap();
      this.socketOptions = builder.socketOptions.getMap();
      this.serverOptions = builder.serverOptions.getMap();
   }

   public static Builder builder() {
      return new Builder();
   }

   public synchronized void start() {
      UndertowLogger.ROOT_LOGGER.infof("starting server: %s", Version.getFullVersionString());
      this.xnio = Xnio.getInstance(Undertow.class.getClassLoader());
      this.channels = new ArrayList();

      try {
         if (this.internalWorker) {
            this.worker = this.xnio.createWorker(OptionMap.builder().set(Options.WORKER_IO_THREADS, this.ioThreads).set(Options.CONNECTION_HIGH_WATER, 1000000).set(Options.CONNECTION_LOW_WATER, 1000000).set(Options.WORKER_TASK_CORE_THREADS, this.workerThreads).set(Options.WORKER_TASK_MAX_THREADS, this.workerThreads).set(Options.TCP_NODELAY, true).set(Options.CORK, true).addAll(this.workerOptions).getMap());
         }

         OptionMap socketOptions = OptionMap.builder().set(Options.WORKER_IO_THREADS, this.worker.getIoThreadCount()).set(Options.TCP_NODELAY, true).set(Options.REUSE_ADDRESSES, true).set(Options.BALANCING_TOKENS, 1).set(Options.BALANCING_CONNECTIONS, 2).set(Options.BACKLOG, 1000).addAll(this.socketOptions).getMap();
         OptionMap serverOptions = OptionMap.builder().set(UndertowOptions.NO_REQUEST_TIMEOUT, 60000).addAll(this.serverOptions).getMap();
         ByteBufferPool buffers = this.byteBufferPool;
         if (buffers == null) {
            buffers = new DefaultByteBufferPool(this.directBuffers, this.bufferSize, -1, 4);
         }

         this.listenerInfo = new ArrayList();
         Iterator var4 = this.listeners.iterator();

         while(var4.hasNext()) {
            ListenerConfig listener = (ListenerConfig)var4.next();
            UndertowLogger.ROOT_LOGGER.debugf("Configuring listener with protocol %s for interface %s and port %s", listener.type, listener.host, listener.port);
            HttpHandler rootHandler = listener.rootHandler != null ? listener.rootHandler : this.rootHandler;
            OptionMap socketOptionsWithOverrides = OptionMap.builder().addAll(socketOptions).addAll(listener.overrideSocketOptions).getMap();
            if (listener.type == Undertow.ListenerType.AJP) {
               AjpOpenListener openListener = new AjpOpenListener((ByteBufferPool)buffers, serverOptions);
               openListener.setRootHandler(rootHandler);
               Object finalListener;
               if (listener.useProxyProtocol) {
                  finalListener = new ProxyProtocolOpenListener(openListener, (UndertowXnioSsl)null, (ByteBufferPool)buffers, OptionMap.EMPTY);
               } else {
                  finalListener = openListener;
               }

               ChannelListener<AcceptingChannel<StreamConnection>> acceptListener = ChannelListeners.openListenerAdapter((ChannelListener)finalListener);
               AcceptingChannel<? extends StreamConnection> server = this.worker.createStreamConnectionServer(new InetSocketAddress(Inet4Address.getByName(listener.host), listener.port), acceptListener, socketOptionsWithOverrides);
               server.resumeAccepts();
               this.channels.add(server);
               this.listenerInfo.add(new ListenerInfo("ajp", server.getLocalAddress(), openListener, (UndertowXnioSsl)null, server));
            } else {
               OptionMap undertowOptions = OptionMap.builder().set(UndertowOptions.BUFFER_PIPELINED_DATA, true).addAll(serverOptions).getMap();
               boolean http2 = serverOptions.get(UndertowOptions.ENABLE_HTTP2, false);
               if (listener.type == Undertow.ListenerType.HTTP) {
                  HttpOpenListener openListener = new HttpOpenListener((ByteBufferPool)buffers, undertowOptions);
                  HttpHandler handler = rootHandler;
                  if (http2) {
                     handler = new Http2UpgradeHandler(rootHandler);
                  }

                  openListener.setRootHandler((HttpHandler)handler);
                  Object finalListener;
                  if (listener.useProxyProtocol) {
                     finalListener = new ProxyProtocolOpenListener(openListener, (UndertowXnioSsl)null, (ByteBufferPool)buffers, OptionMap.EMPTY);
                  } else {
                     finalListener = openListener;
                  }

                  ChannelListener<AcceptingChannel<StreamConnection>> acceptListener = ChannelListeners.openListenerAdapter((ChannelListener)finalListener);
                  AcceptingChannel<? extends StreamConnection> server = this.worker.createStreamConnectionServer(new InetSocketAddress(Inet4Address.getByName(listener.host), listener.port), acceptListener, socketOptionsWithOverrides);
                  server.resumeAccepts();
                  this.channels.add(server);
                  this.listenerInfo.add(new ListenerInfo("http", server.getLocalAddress(), openListener, (UndertowXnioSsl)null, server));
               } else if (listener.type == Undertow.ListenerType.HTTPS) {
                  HttpOpenListener httpOpenListener = new HttpOpenListener((ByteBufferPool)buffers, undertowOptions);
                  httpOpenListener.setRootHandler(rootHandler);
                  Object openListener;
                  if (http2) {
                     AlpnOpenListener alpn = new AlpnOpenListener((ByteBufferPool)buffers, undertowOptions, httpOpenListener);
                     Http2OpenListener http2Listener = new Http2OpenListener((ByteBufferPool)buffers, undertowOptions);
                     http2Listener.setRootHandler(rootHandler);
                     alpn.addProtocol("h2", http2Listener, 10);
                     alpn.addProtocol("h2-14", http2Listener, 7);
                     openListener = alpn;
                  } else {
                     openListener = httpOpenListener;
                  }

                  UndertowXnioSsl xnioSsl;
                  if (listener.sslContext != null) {
                     xnioSsl = new UndertowXnioSsl(this.xnio, OptionMap.create(Options.USE_DIRECT_BUFFERS, true), listener.sslContext, this.sslEngineDelegatedTaskExecutor);
                  } else {
                     OptionMap.Builder builder = OptionMap.builder().addAll(socketOptionsWithOverrides);
                     if (!socketOptionsWithOverrides.contains(Options.SSL_PROTOCOL)) {
                        builder.set(Options.SSL_PROTOCOL, "TLSv1.2");
                     }

                     xnioSsl = new UndertowXnioSsl(this.xnio, OptionMap.create(Options.USE_DIRECT_BUFFERS, true), JsseSslUtils.createSSLContext(listener.keyManagers, listener.trustManagers, new SecureRandom(), builder.getMap()), this.sslEngineDelegatedTaskExecutor);
                  }

                  AcceptingChannel sslServer;
                  ChannelListener acceptListener;
                  if (listener.useProxyProtocol) {
                     acceptListener = ChannelListeners.openListenerAdapter(new ProxyProtocolOpenListener((OpenListener)openListener, xnioSsl, (ByteBufferPool)buffers, socketOptionsWithOverrides));
                     sslServer = this.worker.createStreamConnectionServer(new InetSocketAddress(Inet4Address.getByName(listener.host), listener.port), acceptListener, socketOptionsWithOverrides);
                  } else {
                     acceptListener = ChannelListeners.openListenerAdapter((ChannelListener)openListener);
                     sslServer = xnioSsl.createSslConnectionServer(this.worker, new InetSocketAddress(Inet4Address.getByName(listener.host), listener.port), acceptListener, socketOptionsWithOverrides);
                  }

                  sslServer.resumeAccepts();
                  this.channels.add(sslServer);
                  this.listenerInfo.add(new ListenerInfo("https", sslServer.getLocalAddress(), (OpenListener)openListener, xnioSsl, sslServer));
               }
            }
         }

      } catch (Exception var15) {
         if (this.internalWorker && this.worker != null) {
            this.worker.shutdownNow();
         }

         throw new RuntimeException(var15);
      }
   }

   public synchronized void stop() {
      UndertowLogger.ROOT_LOGGER.infof("stopping server: %s", Version.getFullVersionString());
      if (this.channels != null) {
         Iterator var1 = this.channels.iterator();

         while(var1.hasNext()) {
            AcceptingChannel<? extends StreamConnection> channel = (AcceptingChannel)var1.next();
            IoUtils.safeClose((Closeable)channel);
         }

         this.channels = null;
      }

      if (this.internalWorker && this.worker != null) {
         Integer shutdownTimeoutMillis = (Integer)this.serverOptions.get(UndertowOptions.SHUTDOWN_TIMEOUT);
         this.worker.shutdown();

         try {
            if (shutdownTimeoutMillis == null) {
               this.worker.awaitTermination();
            } else if (!this.worker.awaitTermination((long)shutdownTimeoutMillis, TimeUnit.MILLISECONDS)) {
               this.worker.shutdownNow();
            }
         } catch (InterruptedException var3) {
            this.worker.shutdownNow();
            Thread.currentThread().interrupt();
            throw new RuntimeException(var3);
         }

         this.worker = null;
      }

      this.xnio = null;
      this.listenerInfo = null;
   }

   public Xnio getXnio() {
      return this.xnio;
   }

   public XnioWorker getWorker() {
      return this.worker;
   }

   public List<ListenerInfo> getListenerInfo() {
      if (this.listenerInfo == null) {
         throw UndertowMessages.MESSAGES.serverNotStarted();
      } else {
         return Collections.unmodifiableList(this.listenerInfo);
      }
   }

   // $FF: synthetic method
   Undertow(Builder x0, Object x1) {
      this(x0);
   }

   public static class ListenerInfo {
      private final String protcol;
      private final SocketAddress address;
      private final OpenListener openListener;
      private final UndertowXnioSsl ssl;
      private final AcceptingChannel<? extends StreamConnection> channel;
      private volatile boolean suspended = false;

      public ListenerInfo(String protcol, SocketAddress address, OpenListener openListener, UndertowXnioSsl ssl, AcceptingChannel<? extends StreamConnection> channel) {
         this.protcol = protcol;
         this.address = address;
         this.openListener = openListener;
         this.ssl = ssl;
         this.channel = channel;
      }

      public String getProtcol() {
         return this.protcol;
      }

      public SocketAddress getAddress() {
         return this.address;
      }

      public SSLContext getSslContext() {
         return this.ssl == null ? null : this.ssl.getSslContext();
      }

      public void setSslContext(SSLContext sslContext) {
         if (this.ssl != null) {
            this.ssl.updateSSLContext(sslContext);
         }

      }

      public synchronized void suspend() {
         this.suspended = true;
         this.channel.suspendAccepts();
         final CountDownLatch latch = new CountDownLatch(1);
         this.channel.getIoThread().execute(new Runnable() {
            public void run() {
               try {
                  ListenerInfo.this.openListener.closeConnections();
               } finally {
                  latch.countDown();
               }

            }
         });

         try {
            latch.await();
         } catch (InterruptedException var3) {
            throw new RuntimeException(var3);
         }
      }

      public synchronized void resume() {
         this.suspended = false;
         this.channel.resumeAccepts();
      }

      public boolean isSuspended() {
         return this.suspended;
      }

      public ConnectorStatistics getConnectorStatistics() {
         return this.openListener.getConnectorStatistics();
      }

      public <T> void setSocketOption(Option<T> option, T value) throws IOException {
         this.channel.setOption(option, value);
      }

      public void setServerOptions(OptionMap options) {
         this.openListener.setUndertowOptions(options);
      }

      public String toString() {
         return "ListenerInfo{protcol='" + this.protcol + '\'' + ", address=" + this.address + ", sslContext=" + this.getSslContext() + '}';
      }
   }

   public static final class Builder {
      private int bufferSize;
      private int ioThreads;
      private int workerThreads;
      private boolean directBuffers;
      private final List<ListenerConfig> listeners;
      private HttpHandler handler;
      private XnioWorker worker;
      private Executor sslEngineDelegatedTaskExecutor;
      private ByteBufferPool byteBufferPool;
      private final OptionMap.Builder workerOptions;
      private final OptionMap.Builder socketOptions;
      private final OptionMap.Builder serverOptions;

      private Builder() {
         this.listeners = new ArrayList();
         this.workerOptions = OptionMap.builder();
         this.socketOptions = OptionMap.builder();
         this.serverOptions = OptionMap.builder();
         this.ioThreads = Math.max(Runtime.getRuntime().availableProcessors(), 2);
         this.workerThreads = this.ioThreads * 8;
         long maxMemory = Runtime.getRuntime().maxMemory();
         if (maxMemory < 67108864L) {
            this.directBuffers = false;
            this.bufferSize = 512;
         } else if (maxMemory < 134217728L) {
            this.directBuffers = true;
            this.bufferSize = 1024;
         } else {
            this.directBuffers = true;
            this.bufferSize = 16364;
         }

      }

      public Undertow build() {
         return new Undertow(this);
      }

      /** @deprecated */
      @Deprecated
      public Builder addListener(int port, String host) {
         this.listeners.add(new ListenerConfig(Undertow.ListenerType.HTTP, port, host, (KeyManager[])null, (TrustManager[])null, (HttpHandler)null));
         return this;
      }

      /** @deprecated */
      @Deprecated
      public Builder addListener(int port, String host, ListenerType listenerType) {
         this.listeners.add(new ListenerConfig(listenerType, port, host, (KeyManager[])null, (TrustManager[])null, (HttpHandler)null));
         return this;
      }

      public Builder addListener(ListenerBuilder listenerBuilder) {
         this.listeners.add(new ListenerConfig(listenerBuilder));
         return this;
      }

      public Builder addHttpListener(int port, String host) {
         this.listeners.add(new ListenerConfig(Undertow.ListenerType.HTTP, port, host, (KeyManager[])null, (TrustManager[])null, (HttpHandler)null));
         return this;
      }

      public Builder addHttpsListener(int port, String host, KeyManager[] keyManagers, TrustManager[] trustManagers) {
         this.listeners.add(new ListenerConfig(Undertow.ListenerType.HTTPS, port, host, keyManagers, trustManagers, (HttpHandler)null));
         return this;
      }

      public Builder addHttpsListener(int port, String host, SSLContext sslContext) {
         this.listeners.add(new ListenerConfig(Undertow.ListenerType.HTTPS, port, host, sslContext, (HttpHandler)null));
         return this;
      }

      public Builder addAjpListener(int port, String host) {
         this.listeners.add(new ListenerConfig(Undertow.ListenerType.AJP, port, host, (KeyManager[])null, (TrustManager[])null, (HttpHandler)null));
         return this;
      }

      public Builder addHttpListener(int port, String host, HttpHandler rootHandler) {
         this.listeners.add(new ListenerConfig(Undertow.ListenerType.HTTP, port, host, (KeyManager[])null, (TrustManager[])null, rootHandler));
         return this;
      }

      public Builder addHttpsListener(int port, String host, KeyManager[] keyManagers, TrustManager[] trustManagers, HttpHandler rootHandler) {
         this.listeners.add(new ListenerConfig(Undertow.ListenerType.HTTPS, port, host, keyManagers, trustManagers, rootHandler));
         return this;
      }

      public Builder addHttpsListener(int port, String host, SSLContext sslContext, HttpHandler rootHandler) {
         this.listeners.add(new ListenerConfig(Undertow.ListenerType.HTTPS, port, host, sslContext, rootHandler));
         return this;
      }

      public Builder addAjpListener(int port, String host, HttpHandler rootHandler) {
         this.listeners.add(new ListenerConfig(Undertow.ListenerType.AJP, port, host, (KeyManager[])null, (TrustManager[])null, rootHandler));
         return this;
      }

      public Builder setBufferSize(int bufferSize) {
         this.bufferSize = bufferSize;
         return this;
      }

      /** @deprecated */
      @Deprecated
      public Builder setBuffersPerRegion(int buffersPerRegion) {
         return this;
      }

      public Builder setIoThreads(int ioThreads) {
         this.ioThreads = ioThreads;
         return this;
      }

      public Builder setWorkerThreads(int workerThreads) {
         this.workerThreads = workerThreads;
         return this;
      }

      public Builder setDirectBuffers(boolean directBuffers) {
         this.directBuffers = directBuffers;
         return this;
      }

      public Builder setHandler(HttpHandler handler) {
         this.handler = handler;
         return this;
      }

      public <T> Builder setServerOption(Option<T> option, T value) {
         this.serverOptions.set(option, value);
         return this;
      }

      public <T> Builder setSocketOption(Option<T> option, T value) {
         this.socketOptions.set(option, value);
         return this;
      }

      public <T> Builder setWorkerOption(Option<T> option, T value) {
         this.workerOptions.set(option, value);
         return this;
      }

      public Builder setWorker(XnioWorker worker) {
         this.worker = worker;
         return this;
      }

      public Builder setSslEngineDelegatedTaskExecutor(Executor sslEngineDelegatedTaskExecutor) {
         this.sslEngineDelegatedTaskExecutor = sslEngineDelegatedTaskExecutor;
         return this;
      }

      public Builder setByteBufferPool(ByteBufferPool byteBufferPool) {
         this.byteBufferPool = byteBufferPool;
         return this;
      }

      // $FF: synthetic method
      Builder(Object x0) {
         this();
      }
   }

   public static final class ListenerBuilder {
      ListenerType type;
      int port;
      String host;
      KeyManager[] keyManagers;
      TrustManager[] trustManagers;
      SSLContext sslContext;
      HttpHandler rootHandler;
      OptionMap overrideSocketOptions;
      boolean useProxyProtocol;

      public ListenerBuilder() {
         this.overrideSocketOptions = OptionMap.EMPTY;
      }

      public ListenerBuilder setType(ListenerType type) {
         this.type = type;
         return this;
      }

      public ListenerBuilder setPort(int port) {
         this.port = port;
         return this;
      }

      public ListenerBuilder setHost(String host) {
         this.host = host;
         return this;
      }

      public ListenerBuilder setKeyManagers(KeyManager[] keyManagers) {
         this.keyManagers = keyManagers;
         return this;
      }

      public ListenerBuilder setTrustManagers(TrustManager[] trustManagers) {
         this.trustManagers = trustManagers;
         return this;
      }

      public ListenerBuilder setSslContext(SSLContext sslContext) {
         this.sslContext = sslContext;
         return this;
      }

      public ListenerBuilder setRootHandler(HttpHandler rootHandler) {
         this.rootHandler = rootHandler;
         return this;
      }

      public ListenerBuilder setOverrideSocketOptions(OptionMap overrideSocketOptions) {
         this.overrideSocketOptions = overrideSocketOptions;
         return this;
      }

      public ListenerBuilder setUseProxyProtocol(boolean useProxyProtocol) {
         this.useProxyProtocol = useProxyProtocol;
         return this;
      }
   }

   private static class ListenerConfig {
      final ListenerType type;
      final int port;
      final String host;
      final KeyManager[] keyManagers;
      final TrustManager[] trustManagers;
      final SSLContext sslContext;
      final HttpHandler rootHandler;
      final OptionMap overrideSocketOptions;
      final boolean useProxyProtocol;

      private ListenerConfig(ListenerType type, int port, String host, KeyManager[] keyManagers, TrustManager[] trustManagers, HttpHandler rootHandler) {
         this.type = type;
         this.port = port;
         this.host = host;
         this.keyManagers = keyManagers;
         this.trustManagers = trustManagers;
         this.rootHandler = rootHandler;
         this.sslContext = null;
         this.overrideSocketOptions = OptionMap.EMPTY;
         this.useProxyProtocol = false;
      }

      private ListenerConfig(ListenerType type, int port, String host, SSLContext sslContext, HttpHandler rootHandler) {
         this.type = type;
         this.port = port;
         this.host = host;
         this.rootHandler = rootHandler;
         this.keyManagers = null;
         this.trustManagers = null;
         this.sslContext = sslContext;
         this.overrideSocketOptions = OptionMap.EMPTY;
         this.useProxyProtocol = false;
      }

      private ListenerConfig(ListenerBuilder listenerBuilder) {
         this.type = listenerBuilder.type;
         this.port = listenerBuilder.port;
         this.host = listenerBuilder.host;
         this.rootHandler = listenerBuilder.rootHandler;
         this.keyManagers = listenerBuilder.keyManagers;
         this.trustManagers = listenerBuilder.trustManagers;
         this.sslContext = listenerBuilder.sslContext;
         this.overrideSocketOptions = listenerBuilder.overrideSocketOptions;
         this.useProxyProtocol = listenerBuilder.useProxyProtocol;
      }

      // $FF: synthetic method
      ListenerConfig(ListenerType x0, int x1, String x2, KeyManager[] x3, TrustManager[] x4, HttpHandler x5, Object x6) {
         this(x0, x1, x2, x3, x4, x5);
      }

      // $FF: synthetic method
      ListenerConfig(ListenerBuilder x0, Object x1) {
         this(x0);
      }

      // $FF: synthetic method
      ListenerConfig(ListenerType x0, int x1, String x2, SSLContext x3, HttpHandler x4, Object x5) {
         this(x0, x1, x2, x3, x4);
      }
   }

   public static enum ListenerType {
      HTTP,
      HTTPS,
      AJP;
   }
}
