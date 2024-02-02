/*     */ package io.undertow;
/*     */ 
/*     */ import io.undertow.connector.ByteBufferPool;
/*     */ import io.undertow.protocols.ssl.UndertowXnioSsl;
/*     */ import io.undertow.server.ConnectorStatistics;
/*     */ import io.undertow.server.DefaultByteBufferPool;
/*     */ import io.undertow.server.DelegateOpenListener;
/*     */ import io.undertow.server.HttpHandler;
/*     */ import io.undertow.server.OpenListener;
/*     */ import io.undertow.server.protocol.ajp.AjpOpenListener;
/*     */ import io.undertow.server.protocol.http.AlpnOpenListener;
/*     */ import io.undertow.server.protocol.http.HttpOpenListener;
/*     */ import io.undertow.server.protocol.http2.Http2OpenListener;
/*     */ import io.undertow.server.protocol.http2.Http2UpgradeHandler;
/*     */ import io.undertow.server.protocol.proxy.ProxyProtocolOpenListener;
/*     */ import java.io.Closeable;
/*     */ import java.io.IOException;
/*     */ import java.net.Inet4Address;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.net.SocketAddress;
/*     */ import java.security.SecureRandom;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ import java.util.concurrent.CountDownLatch;
/*     */ import java.util.concurrent.Executor;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import javax.net.ssl.KeyManager;
/*     */ import javax.net.ssl.SSLContext;
/*     */ import javax.net.ssl.TrustManager;
/*     */ import org.xnio.ChannelListener;
/*     */ import org.xnio.ChannelListeners;
/*     */ import org.xnio.IoUtils;
/*     */ import org.xnio.Option;
/*     */ import org.xnio.OptionMap;
/*     */ import org.xnio.Options;
/*     */ import org.xnio.StreamConnection;
/*     */ import org.xnio.Xnio;
/*     */ import org.xnio.XnioWorker;
/*     */ import org.xnio.channels.AcceptingChannel;
/*     */ import org.xnio.ssl.JsseSslUtils;
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
/*     */ public final class Undertow
/*     */ {
/*     */   private final int bufferSize;
/*     */   private final int ioThreads;
/*     */   private final int workerThreads;
/*     */   private final boolean directBuffers;
/*  72 */   private final List<ListenerConfig> listeners = new ArrayList<>();
/*     */   
/*     */   private volatile List<ListenerInfo> listenerInfo;
/*     */   
/*     */   private final HttpHandler rootHandler;
/*     */   
/*     */   private final OptionMap workerOptions;
/*     */   
/*     */   private final OptionMap socketOptions;
/*     */   
/*     */   private final OptionMap serverOptions;
/*     */   
/*     */   private final boolean internalWorker;
/*     */   
/*     */   private ByteBufferPool byteBufferPool;
/*     */   
/*     */   private XnioWorker worker;
/*     */   
/*     */   private Executor sslEngineDelegatedTaskExecutor;
/*     */   
/*     */   private List<AcceptingChannel<? extends StreamConnection>> channels;
/*     */   
/*     */   private Xnio xnio;
/*     */   
/*     */   private Undertow(Builder builder) {
/*  97 */     this.byteBufferPool = builder.byteBufferPool;
/*  98 */     this.bufferSize = (this.byteBufferPool != null) ? this.byteBufferPool.getBufferSize() : builder.bufferSize;
/*  99 */     this.directBuffers = (this.byteBufferPool != null) ? this.byteBufferPool.isDirect() : builder.directBuffers;
/* 100 */     this.ioThreads = builder.ioThreads;
/* 101 */     this.workerThreads = builder.workerThreads;
/* 102 */     this.listeners.addAll(builder.listeners);
/* 103 */     this.rootHandler = builder.handler;
/* 104 */     this.worker = builder.worker;
/* 105 */     this.sslEngineDelegatedTaskExecutor = builder.sslEngineDelegatedTaskExecutor;
/* 106 */     this.internalWorker = (builder.worker == null);
/* 107 */     this.workerOptions = builder.workerOptions.getMap();
/* 108 */     this.socketOptions = builder.socketOptions.getMap();
/* 109 */     this.serverOptions = builder.serverOptions.getMap();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Builder builder() {
/* 116 */     return new Builder();
/*     */   }
/*     */   
/*     */   public synchronized void start() {
/* 120 */     UndertowLogger.ROOT_LOGGER.infof("starting server: %s", Version.getFullVersionString());
/* 121 */     this.xnio = Xnio.getInstance(Undertow.class.getClassLoader());
/* 122 */     this.channels = new ArrayList<>(); try {
/*     */       DefaultByteBufferPool defaultByteBufferPool;
/* 124 */       if (this.internalWorker) {
/* 125 */         this.worker = this.xnio.createWorker(OptionMap.builder()
/* 126 */             .set(Options.WORKER_IO_THREADS, this.ioThreads)
/* 127 */             .set(Options.CONNECTION_HIGH_WATER, 1000000)
/* 128 */             .set(Options.CONNECTION_LOW_WATER, 1000000)
/* 129 */             .set(Options.WORKER_TASK_CORE_THREADS, this.workerThreads)
/* 130 */             .set(Options.WORKER_TASK_MAX_THREADS, this.workerThreads)
/* 131 */             .set(Options.TCP_NODELAY, true)
/* 132 */             .set(Options.CORK, true)
/* 133 */             .addAll(this.workerOptions)
/* 134 */             .getMap());
/*     */       }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 145 */       OptionMap socketOptions = OptionMap.builder().set(Options.WORKER_IO_THREADS, this.worker.getIoThreadCount()).set(Options.TCP_NODELAY, true).set(Options.REUSE_ADDRESSES, true).set(Options.BALANCING_TOKENS, 1).set(Options.BALANCING_CONNECTIONS, 2).set(Options.BACKLOG, 1000).addAll(this.socketOptions).getMap();
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 150 */       OptionMap serverOptions = OptionMap.builder().set(UndertowOptions.NO_REQUEST_TIMEOUT, 60000).addAll(this.serverOptions).getMap();
/*     */ 
/*     */       
/* 153 */       ByteBufferPool buffers = this.byteBufferPool;
/* 154 */       if (buffers == null) {
/* 155 */         defaultByteBufferPool = new DefaultByteBufferPool(this.directBuffers, this.bufferSize, -1, 4);
/*     */       }
/*     */       
/* 158 */       this.listenerInfo = new ArrayList<>();
/* 159 */       for (ListenerConfig listener : this.listeners) {
/* 160 */         UndertowLogger.ROOT_LOGGER.debugf("Configuring listener with protocol %s for interface %s and port %s", listener.type, listener.host, Integer.valueOf(listener.port));
/* 161 */         HttpHandler rootHandler = (listener.rootHandler != null) ? listener.rootHandler : this.rootHandler;
/* 162 */         OptionMap socketOptionsWithOverrides = OptionMap.builder().addAll(socketOptions).addAll(listener.overrideSocketOptions).getMap();
/* 163 */         if (listener.type == ListenerType.AJP) {
/* 164 */           AjpOpenListener ajpOpenListener1, openListener = new AjpOpenListener((ByteBufferPool)defaultByteBufferPool, serverOptions);
/* 165 */           openListener.setRootHandler(rootHandler);
/*     */ 
/*     */           
/* 168 */           if (listener.useProxyProtocol) {
/* 169 */             ProxyProtocolOpenListener proxyProtocolOpenListener = new ProxyProtocolOpenListener((OpenListener)openListener, null, (ByteBufferPool)defaultByteBufferPool, OptionMap.EMPTY);
/*     */           } else {
/* 171 */             ajpOpenListener1 = openListener;
/*     */           } 
/* 173 */           ChannelListener<AcceptingChannel<StreamConnection>> acceptListener = ChannelListeners.openListenerAdapter((ChannelListener)ajpOpenListener1);
/* 174 */           AcceptingChannel<? extends StreamConnection> server = this.worker.createStreamConnectionServer(new InetSocketAddress(Inet4Address.getByName(listener.host), listener.port), acceptListener, socketOptionsWithOverrides);
/* 175 */           server.resumeAccepts();
/* 176 */           this.channels.add(server);
/* 177 */           this.listenerInfo.add(new ListenerInfo("ajp", server.getLocalAddress(), (OpenListener)openListener, null, server)); continue;
/*     */         } 
/* 179 */         OptionMap undertowOptions = OptionMap.builder().set(UndertowOptions.BUFFER_PIPELINED_DATA, true).addAll(serverOptions).getMap();
/* 180 */         boolean http2 = serverOptions.get(UndertowOptions.ENABLE_HTTP2, false);
/* 181 */         if (listener.type == ListenerType.HTTP) {
/* 182 */           Http2UpgradeHandler http2UpgradeHandler; HttpOpenListener httpOpenListener1, openListener = new HttpOpenListener((ByteBufferPool)defaultByteBufferPool, undertowOptions);
/* 183 */           HttpHandler handler = rootHandler;
/* 184 */           if (http2) {
/* 185 */             http2UpgradeHandler = new Http2UpgradeHandler(handler);
/*     */           }
/* 187 */           openListener.setRootHandler((HttpHandler)http2UpgradeHandler);
/*     */           
/* 189 */           if (listener.useProxyProtocol) {
/* 190 */             ProxyProtocolOpenListener proxyProtocolOpenListener = new ProxyProtocolOpenListener((OpenListener)openListener, null, (ByteBufferPool)defaultByteBufferPool, OptionMap.EMPTY);
/*     */           } else {
/* 192 */             httpOpenListener1 = openListener;
/*     */           } 
/*     */           
/* 195 */           ChannelListener<AcceptingChannel<StreamConnection>> acceptListener = ChannelListeners.openListenerAdapter((ChannelListener)httpOpenListener1);
/* 196 */           AcceptingChannel<? extends StreamConnection> server = this.worker.createStreamConnectionServer(new InetSocketAddress(Inet4Address.getByName(listener.host), listener.port), acceptListener, socketOptionsWithOverrides);
/* 197 */           server.resumeAccepts();
/* 198 */           this.channels.add(server);
/* 199 */           this.listenerInfo.add(new ListenerInfo("http", server.getLocalAddress(), (OpenListener)openListener, null, server)); continue;
/* 200 */         }  if (listener.type == ListenerType.HTTPS) {
/*     */           HttpOpenListener httpOpenListener1; UndertowXnioSsl xnioSsl;
/*     */           AcceptingChannel<? extends StreamConnection> sslServer;
/* 203 */           HttpOpenListener httpOpenListener = new HttpOpenListener((ByteBufferPool)defaultByteBufferPool, undertowOptions);
/* 204 */           httpOpenListener.setRootHandler(rootHandler);
/*     */           
/* 206 */           if (http2) {
/* 207 */             AlpnOpenListener alpn = new AlpnOpenListener((ByteBufferPool)defaultByteBufferPool, undertowOptions, (DelegateOpenListener)httpOpenListener);
/* 208 */             Http2OpenListener http2Listener = new Http2OpenListener((ByteBufferPool)defaultByteBufferPool, undertowOptions);
/* 209 */             http2Listener.setRootHandler(rootHandler);
/* 210 */             alpn.addProtocol("h2", (DelegateOpenListener)http2Listener, 10);
/* 211 */             alpn.addProtocol("h2-14", (DelegateOpenListener)http2Listener, 7);
/* 212 */             AlpnOpenListener alpnOpenListener1 = alpn;
/*     */           } else {
/* 214 */             httpOpenListener1 = httpOpenListener;
/*     */           } 
/*     */ 
/*     */           
/* 218 */           if (listener.sslContext != null) {
/* 219 */             xnioSsl = new UndertowXnioSsl(this.xnio, OptionMap.create(Options.USE_DIRECT_BUFFERS, Boolean.valueOf(true)), listener.sslContext, this.sslEngineDelegatedTaskExecutor);
/*     */           } else {
/*     */             
/* 222 */             OptionMap.Builder builder = OptionMap.builder().addAll(socketOptionsWithOverrides);
/* 223 */             if (!socketOptionsWithOverrides.contains(Options.SSL_PROTOCOL)) {
/* 224 */               builder.set(Options.SSL_PROTOCOL, "TLSv1.2");
/*     */             }
/*     */ 
/*     */ 
/*     */             
/* 229 */             xnioSsl = new UndertowXnioSsl(this.xnio, OptionMap.create(Options.USE_DIRECT_BUFFERS, Boolean.valueOf(true)), JsseSslUtils.createSSLContext(listener.keyManagers, listener.trustManagers, new SecureRandom(), builder.getMap()), this.sslEngineDelegatedTaskExecutor);
/*     */           } 
/*     */ 
/*     */ 
/*     */           
/* 234 */           if (listener.useProxyProtocol) {
/* 235 */             ChannelListener<AcceptingChannel<StreamConnection>> acceptListener = ChannelListeners.openListenerAdapter((ChannelListener)new ProxyProtocolOpenListener((OpenListener)httpOpenListener1, xnioSsl, (ByteBufferPool)defaultByteBufferPool, socketOptionsWithOverrides));
/* 236 */             sslServer = this.worker.createStreamConnectionServer(new InetSocketAddress(Inet4Address.getByName(listener.host), listener.port), acceptListener, socketOptionsWithOverrides);
/*     */           } else {
/* 238 */             ChannelListener<AcceptingChannel<StreamConnection>> acceptListener = ChannelListeners.openListenerAdapter((ChannelListener)httpOpenListener1);
/* 239 */             sslServer = xnioSsl.createSslConnectionServer(this.worker, new InetSocketAddress(Inet4Address.getByName(listener.host), listener.port), acceptListener, socketOptionsWithOverrides);
/*     */           } 
/*     */           
/* 242 */           sslServer.resumeAccepts();
/* 243 */           this.channels.add(sslServer);
/* 244 */           this.listenerInfo.add(new ListenerInfo("https", sslServer.getLocalAddress(), (OpenListener)httpOpenListener1, xnioSsl, sslServer));
/*     */         }
/*     */       
/*     */       }
/*     */     
/*     */     }
/* 250 */     catch (Exception e) {
/* 251 */       if (this.internalWorker && this.worker != null) {
/* 252 */         this.worker.shutdownNow();
/*     */       }
/* 254 */       throw new RuntimeException(e);
/*     */     } 
/*     */   }
/*     */   
/*     */   public synchronized void stop() {
/* 259 */     UndertowLogger.ROOT_LOGGER.infof("stopping server: %s", Version.getFullVersionString());
/* 260 */     if (this.channels != null) {
/* 261 */       for (AcceptingChannel<? extends StreamConnection> channel : this.channels) {
/* 262 */         IoUtils.safeClose((Closeable)channel);
/*     */       }
/* 264 */       this.channels = null;
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 270 */     if (this.internalWorker && this.worker != null) {
/* 271 */       Integer shutdownTimeoutMillis = (Integer)this.serverOptions.get(UndertowOptions.SHUTDOWN_TIMEOUT);
/* 272 */       this.worker.shutdown();
/*     */       try {
/* 274 */         if (shutdownTimeoutMillis == null) {
/* 275 */           this.worker.awaitTermination();
/*     */         }
/* 277 */         else if (!this.worker.awaitTermination(shutdownTimeoutMillis.intValue(), TimeUnit.MILLISECONDS)) {
/* 278 */           this.worker.shutdownNow();
/*     */         }
/*     */       
/* 281 */       } catch (InterruptedException e) {
/* 282 */         this.worker.shutdownNow();
/* 283 */         Thread.currentThread().interrupt();
/* 284 */         throw new RuntimeException(e);
/*     */       } 
/* 286 */       this.worker = null;
/*     */     } 
/* 288 */     this.xnio = null;
/* 289 */     this.listenerInfo = null;
/*     */   }
/*     */   
/*     */   public Xnio getXnio() {
/* 293 */     return this.xnio;
/*     */   }
/*     */   
/*     */   public XnioWorker getWorker() {
/* 297 */     return this.worker;
/*     */   }
/*     */   
/*     */   public List<ListenerInfo> getListenerInfo() {
/* 301 */     if (this.listenerInfo == null) {
/* 302 */       throw UndertowMessages.MESSAGES.serverNotStarted();
/*     */     }
/* 304 */     return Collections.unmodifiableList(this.listenerInfo);
/*     */   }
/*     */   
/*     */   public enum ListenerType
/*     */   {
/* 309 */     HTTP,
/* 310 */     HTTPS,
/* 311 */     AJP;
/*     */   }
/*     */   
/*     */   private static class ListenerConfig
/*     */   {
/*     */     final Undertow.ListenerType type;
/*     */     final int port;
/*     */     final String host;
/*     */     final KeyManager[] keyManagers;
/*     */     final TrustManager[] trustManagers;
/*     */     final SSLContext sslContext;
/*     */     final HttpHandler rootHandler;
/*     */     final OptionMap overrideSocketOptions;
/*     */     final boolean useProxyProtocol;
/*     */     
/*     */     private ListenerConfig(Undertow.ListenerType type, int port, String host, KeyManager[] keyManagers, TrustManager[] trustManagers, HttpHandler rootHandler) {
/* 327 */       this.type = type;
/* 328 */       this.port = port;
/* 329 */       this.host = host;
/* 330 */       this.keyManagers = keyManagers;
/* 331 */       this.trustManagers = trustManagers;
/* 332 */       this.rootHandler = rootHandler;
/* 333 */       this.sslContext = null;
/* 334 */       this.overrideSocketOptions = OptionMap.EMPTY;
/* 335 */       this.useProxyProtocol = false;
/*     */     }
/*     */     
/*     */     private ListenerConfig(Undertow.ListenerType type, int port, String host, SSLContext sslContext, HttpHandler rootHandler) {
/* 339 */       this.type = type;
/* 340 */       this.port = port;
/* 341 */       this.host = host;
/* 342 */       this.rootHandler = rootHandler;
/* 343 */       this.keyManagers = null;
/* 344 */       this.trustManagers = null;
/* 345 */       this.sslContext = sslContext;
/* 346 */       this.overrideSocketOptions = OptionMap.EMPTY;
/* 347 */       this.useProxyProtocol = false;
/*     */     }
/*     */     
/*     */     private ListenerConfig(Undertow.ListenerBuilder listenerBuilder) {
/* 351 */       this.type = listenerBuilder.type;
/* 352 */       this.port = listenerBuilder.port;
/* 353 */       this.host = listenerBuilder.host;
/* 354 */       this.rootHandler = listenerBuilder.rootHandler;
/* 355 */       this.keyManagers = listenerBuilder.keyManagers;
/* 356 */       this.trustManagers = listenerBuilder.trustManagers;
/* 357 */       this.sslContext = listenerBuilder.sslContext;
/* 358 */       this.overrideSocketOptions = listenerBuilder.overrideSocketOptions;
/* 359 */       this.useProxyProtocol = listenerBuilder.useProxyProtocol;
/*     */     }
/*     */   }
/*     */   
/*     */   public static final class ListenerBuilder
/*     */   {
/*     */     Undertow.ListenerType type;
/*     */     int port;
/*     */     String host;
/*     */     KeyManager[] keyManagers;
/*     */     TrustManager[] trustManagers;
/*     */     SSLContext sslContext;
/*     */     HttpHandler rootHandler;
/* 372 */     OptionMap overrideSocketOptions = OptionMap.EMPTY;
/*     */     boolean useProxyProtocol;
/*     */     
/*     */     public ListenerBuilder setType(Undertow.ListenerType type) {
/* 376 */       this.type = type;
/* 377 */       return this;
/*     */     }
/*     */     
/*     */     public ListenerBuilder setPort(int port) {
/* 381 */       this.port = port;
/* 382 */       return this;
/*     */     }
/*     */     
/*     */     public ListenerBuilder setHost(String host) {
/* 386 */       this.host = host;
/* 387 */       return this;
/*     */     }
/*     */     
/*     */     public ListenerBuilder setKeyManagers(KeyManager[] keyManagers) {
/* 391 */       this.keyManagers = keyManagers;
/* 392 */       return this;
/*     */     }
/*     */     
/*     */     public ListenerBuilder setTrustManagers(TrustManager[] trustManagers) {
/* 396 */       this.trustManagers = trustManagers;
/* 397 */       return this;
/*     */     }
/*     */     
/*     */     public ListenerBuilder setSslContext(SSLContext sslContext) {
/* 401 */       this.sslContext = sslContext;
/* 402 */       return this;
/*     */     }
/*     */     
/*     */     public ListenerBuilder setRootHandler(HttpHandler rootHandler) {
/* 406 */       this.rootHandler = rootHandler;
/* 407 */       return this;
/*     */     }
/*     */     
/*     */     public ListenerBuilder setOverrideSocketOptions(OptionMap overrideSocketOptions) {
/* 411 */       this.overrideSocketOptions = overrideSocketOptions;
/* 412 */       return this;
/*     */     }
/*     */     
/*     */     public ListenerBuilder setUseProxyProtocol(boolean useProxyProtocol) {
/* 416 */       this.useProxyProtocol = useProxyProtocol;
/* 417 */       return this;
/*     */     }
/*     */   }
/*     */   
/*     */   public static final class Builder
/*     */   {
/*     */     private int bufferSize;
/*     */     private int ioThreads;
/*     */     private int workerThreads;
/*     */     private boolean directBuffers;
/* 427 */     private final List<Undertow.ListenerConfig> listeners = new ArrayList<>();
/*     */     
/*     */     private HttpHandler handler;
/*     */     private XnioWorker worker;
/*     */     private Executor sslEngineDelegatedTaskExecutor;
/*     */     private ByteBufferPool byteBufferPool;
/* 433 */     private final OptionMap.Builder workerOptions = OptionMap.builder();
/* 434 */     private final OptionMap.Builder socketOptions = OptionMap.builder();
/* 435 */     private final OptionMap.Builder serverOptions = OptionMap.builder();
/*     */     
/*     */     private Builder() {
/* 438 */       this.ioThreads = Math.max(Runtime.getRuntime().availableProcessors(), 2);
/* 439 */       this.workerThreads = this.ioThreads * 8;
/* 440 */       long maxMemory = Runtime.getRuntime().maxMemory();
/*     */       
/* 442 */       if (maxMemory < 67108864L) {
/*     */         
/* 444 */         this.directBuffers = false;
/* 445 */         this.bufferSize = 512;
/* 446 */       } else if (maxMemory < 134217728L) {
/*     */         
/* 448 */         this.directBuffers = true;
/* 449 */         this.bufferSize = 1024;
/*     */       }
/*     */       else {
/*     */         
/* 453 */         this.directBuffers = true;
/* 454 */         this.bufferSize = 16364;
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/*     */     public Undertow build() {
/* 460 */       return new Undertow(this);
/*     */     }
/*     */     
/*     */     @Deprecated
/*     */     public Builder addListener(int port, String host) {
/* 465 */       this.listeners.add(new Undertow.ListenerConfig(Undertow.ListenerType.HTTP, port, host, null, null, null));
/* 466 */       return this;
/*     */     }
/*     */     
/*     */     @Deprecated
/*     */     public Builder addListener(int port, String host, Undertow.ListenerType listenerType) {
/* 471 */       this.listeners.add(new Undertow.ListenerConfig(listenerType, port, host, null, null, null));
/* 472 */       return this;
/*     */     }
/*     */     
/*     */     public Builder addListener(Undertow.ListenerBuilder listenerBuilder) {
/* 476 */       this.listeners.add(new Undertow.ListenerConfig(listenerBuilder));
/* 477 */       return this;
/*     */     }
/*     */     
/*     */     public Builder addHttpListener(int port, String host) {
/* 481 */       this.listeners.add(new Undertow.ListenerConfig(Undertow.ListenerType.HTTP, port, host, null, null, null));
/* 482 */       return this;
/*     */     }
/*     */     
/*     */     public Builder addHttpsListener(int port, String host, KeyManager[] keyManagers, TrustManager[] trustManagers) {
/* 486 */       this.listeners.add(new Undertow.ListenerConfig(Undertow.ListenerType.HTTPS, port, host, keyManagers, trustManagers, null));
/* 487 */       return this;
/*     */     }
/*     */     
/*     */     public Builder addHttpsListener(int port, String host, SSLContext sslContext) {
/* 491 */       this.listeners.add(new Undertow.ListenerConfig(Undertow.ListenerType.HTTPS, port, host, sslContext, null));
/* 492 */       return this;
/*     */     }
/*     */     
/*     */     public Builder addAjpListener(int port, String host) {
/* 496 */       this.listeners.add(new Undertow.ListenerConfig(Undertow.ListenerType.AJP, port, host, null, null, null));
/* 497 */       return this;
/*     */     }
/*     */     
/*     */     public Builder addHttpListener(int port, String host, HttpHandler rootHandler) {
/* 501 */       this.listeners.add(new Undertow.ListenerConfig(Undertow.ListenerType.HTTP, port, host, null, null, rootHandler));
/* 502 */       return this;
/*     */     }
/*     */     
/*     */     public Builder addHttpsListener(int port, String host, KeyManager[] keyManagers, TrustManager[] trustManagers, HttpHandler rootHandler) {
/* 506 */       this.listeners.add(new Undertow.ListenerConfig(Undertow.ListenerType.HTTPS, port, host, keyManagers, trustManagers, rootHandler));
/* 507 */       return this;
/*     */     }
/*     */     
/*     */     public Builder addHttpsListener(int port, String host, SSLContext sslContext, HttpHandler rootHandler) {
/* 511 */       this.listeners.add(new Undertow.ListenerConfig(Undertow.ListenerType.HTTPS, port, host, sslContext, rootHandler));
/* 512 */       return this;
/*     */     }
/*     */     
/*     */     public Builder addAjpListener(int port, String host, HttpHandler rootHandler) {
/* 516 */       this.listeners.add(new Undertow.ListenerConfig(Undertow.ListenerType.AJP, port, host, null, null, rootHandler));
/* 517 */       return this;
/*     */     }
/*     */     
/*     */     public Builder setBufferSize(int bufferSize) {
/* 521 */       this.bufferSize = bufferSize;
/* 522 */       return this;
/*     */     }
/*     */     
/*     */     @Deprecated
/*     */     public Builder setBuffersPerRegion(int buffersPerRegion) {
/* 527 */       return this;
/*     */     }
/*     */     
/*     */     public Builder setIoThreads(int ioThreads) {
/* 531 */       this.ioThreads = ioThreads;
/* 532 */       return this;
/*     */     }
/*     */     
/*     */     public Builder setWorkerThreads(int workerThreads) {
/* 536 */       this.workerThreads = workerThreads;
/* 537 */       return this;
/*     */     }
/*     */     
/*     */     public Builder setDirectBuffers(boolean directBuffers) {
/* 541 */       this.directBuffers = directBuffers;
/* 542 */       return this;
/*     */     }
/*     */     
/*     */     public Builder setHandler(HttpHandler handler) {
/* 546 */       this.handler = handler;
/* 547 */       return this;
/*     */     }
/*     */     
/*     */     public <T> Builder setServerOption(Option<T> option, T value) {
/* 551 */       this.serverOptions.set(option, value);
/* 552 */       return this;
/*     */     }
/*     */     
/*     */     public <T> Builder setSocketOption(Option<T> option, T value) {
/* 556 */       this.socketOptions.set(option, value);
/* 557 */       return this;
/*     */     }
/*     */     
/*     */     public <T> Builder setWorkerOption(Option<T> option, T value) {
/* 561 */       this.workerOptions.set(option, value);
/* 562 */       return this;
/*     */     }
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
/*     */     public Builder setWorker(XnioWorker worker) {
/* 578 */       this.worker = worker;
/* 579 */       return this;
/*     */     }
/*     */     
/*     */     public Builder setSslEngineDelegatedTaskExecutor(Executor sslEngineDelegatedTaskExecutor) {
/* 583 */       this.sslEngineDelegatedTaskExecutor = sslEngineDelegatedTaskExecutor;
/* 584 */       return this;
/*     */     }
/*     */     
/*     */     public Builder setByteBufferPool(ByteBufferPool byteBufferPool) {
/* 588 */       this.byteBufferPool = byteBufferPool;
/* 589 */       return this;
/*     */     }
/*     */   }
/*     */   
/*     */   public static class ListenerInfo
/*     */   {
/*     */     private final String protcol;
/*     */     private final SocketAddress address;
/*     */     private final OpenListener openListener;
/*     */     private final UndertowXnioSsl ssl;
/*     */     private final AcceptingChannel<? extends StreamConnection> channel;
/*     */     private volatile boolean suspended = false;
/*     */     
/*     */     public ListenerInfo(String protcol, SocketAddress address, OpenListener openListener, UndertowXnioSsl ssl, AcceptingChannel<? extends StreamConnection> channel) {
/* 603 */       this.protcol = protcol;
/* 604 */       this.address = address;
/* 605 */       this.openListener = openListener;
/* 606 */       this.ssl = ssl;
/* 607 */       this.channel = channel;
/*     */     }
/*     */     
/*     */     public String getProtcol() {
/* 611 */       return this.protcol;
/*     */     }
/*     */     
/*     */     public SocketAddress getAddress() {
/* 615 */       return this.address;
/*     */     }
/*     */     
/*     */     public SSLContext getSslContext() {
/* 619 */       if (this.ssl == null) {
/* 620 */         return null;
/*     */       }
/* 622 */       return this.ssl.getSslContext();
/*     */     }
/*     */     
/*     */     public void setSslContext(SSLContext sslContext) {
/* 626 */       if (this.ssl != null)
/*     */       {
/* 628 */         this.ssl.updateSSLContext(sslContext);
/*     */       }
/*     */     }
/*     */     
/*     */     public synchronized void suspend() {
/* 633 */       this.suspended = true;
/* 634 */       this.channel.suspendAccepts();
/* 635 */       final CountDownLatch latch = new CountDownLatch(1);
/*     */       
/* 637 */       this.channel.getIoThread().execute(new Runnable()
/*     */           {
/*     */             public void run() {
/*     */               try {
/* 641 */                 Undertow.ListenerInfo.this.openListener.closeConnections();
/*     */               } finally {
/* 643 */                 latch.countDown();
/*     */               } 
/*     */             }
/*     */           });
/*     */       try {
/* 648 */         latch.await();
/* 649 */       } catch (InterruptedException e) {
/* 650 */         throw new RuntimeException(e);
/*     */       } 
/*     */     }
/*     */     
/*     */     public synchronized void resume() {
/* 655 */       this.suspended = false;
/* 656 */       this.channel.resumeAccepts();
/*     */     }
/*     */     
/*     */     public boolean isSuspended() {
/* 660 */       return this.suspended;
/*     */     }
/*     */     
/*     */     public ConnectorStatistics getConnectorStatistics() {
/* 664 */       return this.openListener.getConnectorStatistics();
/*     */     }
/*     */     
/*     */     public <T> void setSocketOption(Option<T> option, T value) throws IOException {
/* 668 */       this.channel.setOption(option, value);
/*     */     }
/*     */     
/*     */     public void setServerOptions(OptionMap options) {
/* 672 */       this.openListener.setUndertowOptions(options);
/*     */     }
/*     */ 
/*     */     
/*     */     public String toString() {
/* 677 */       return "ListenerInfo{protcol='" + this.protcol + '\'' + ", address=" + this.address + ", sslContext=" + 
/*     */ 
/*     */         
/* 680 */         getSslContext() + '}';
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\Undertow.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */