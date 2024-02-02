/*     */ package io.undertow.protocols.ssl;
/*     */ 
/*     */ import io.undertow.UndertowOptions;
/*     */ import io.undertow.connector.ByteBufferPool;
/*     */ import io.undertow.server.DefaultByteBufferPool;
/*     */ import java.io.Closeable;
/*     */ import java.io.IOException;
/*     */ import java.net.InetAddress;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.net.SocketAddress;
/*     */ import java.net.URI;
/*     */ import java.nio.channels.Channel;
/*     */ import java.security.KeyManagementException;
/*     */ import java.security.NoSuchAlgorithmException;
/*     */ import java.security.NoSuchProviderException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collections;
/*     */ import java.util.HashSet;
/*     */ import java.util.List;
/*     */ import java.util.Set;
/*     */ import java.util.concurrent.Executor;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import javax.net.ssl.SNIHostName;
/*     */ import javax.net.ssl.SSLContext;
/*     */ import javax.net.ssl.SSLEngine;
/*     */ import javax.net.ssl.SSLParameters;
/*     */ import org.xnio.Cancellable;
/*     */ import org.xnio.ChannelListener;
/*     */ import org.xnio.ChannelListeners;
/*     */ import org.xnio.FutureResult;
/*     */ import org.xnio.IoFuture;
/*     */ import org.xnio.IoUtils;
/*     */ import org.xnio.Option;
/*     */ import org.xnio.OptionMap;
/*     */ import org.xnio.Options;
/*     */ import org.xnio.Sequence;
/*     */ import org.xnio.SslClientAuthMode;
/*     */ import org.xnio.StreamConnection;
/*     */ import org.xnio.Xnio;
/*     */ import org.xnio.XnioExecutor;
/*     */ import org.xnio.XnioIoThread;
/*     */ import org.xnio.XnioWorker;
/*     */ import org.xnio.channels.AcceptingChannel;
/*     */ import org.xnio.channels.AssembledConnectedSslStreamChannel;
/*     */ import org.xnio.channels.BoundChannel;
/*     */ import org.xnio.channels.CloseableChannel;
/*     */ import org.xnio.channels.ConnectedChannel;
/*     */ import org.xnio.channels.ConnectedSslStreamChannel;
/*     */ import org.xnio.channels.ConnectedStreamChannel;
/*     */ import org.xnio.channels.SslChannel;
/*     */ import org.xnio.channels.StreamSinkChannel;
/*     */ import org.xnio.channels.StreamSourceChannel;
/*     */ import org.xnio.ssl.JsseSslUtils;
/*     */ import org.xnio.ssl.JsseXnioSsl;
/*     */ import org.xnio.ssl.SslConnection;
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
/*     */ public class UndertowXnioSsl
/*     */   extends XnioSsl
/*     */ {
/*  79 */   private static final ByteBufferPool DEFAULT_BUFFER_POOL = (ByteBufferPool)new DefaultByteBufferPool(true, 17408, -1, 12);
/*     */ 
/*     */ 
/*     */   
/*     */   private final ByteBufferPool bufferPool;
/*     */ 
/*     */ 
/*     */   
/*     */   private final Executor delegatedTaskExecutor;
/*     */ 
/*     */   
/*     */   private volatile SSLContext sslContext;
/*     */ 
/*     */ 
/*     */   
/*     */   public UndertowXnioSsl(Xnio xnio, OptionMap optionMap) throws NoSuchProviderException, NoSuchAlgorithmException, KeyManagementException {
/*  95 */     this(xnio, optionMap, DEFAULT_BUFFER_POOL, JsseSslUtils.createSSLContext(optionMap));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public UndertowXnioSsl(Xnio xnio, OptionMap optionMap, SSLContext sslContext) {
/* 105 */     this(xnio, optionMap, DEFAULT_BUFFER_POOL, sslContext);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public UndertowXnioSsl(Xnio xnio, OptionMap optionMap, SSLContext sslContext, Executor delegatedTaskExecutor) {
/* 116 */     this(xnio, optionMap, DEFAULT_BUFFER_POOL, sslContext, delegatedTaskExecutor);
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
/*     */   public UndertowXnioSsl(Xnio xnio, OptionMap optionMap, ByteBufferPool bufferPool) throws NoSuchProviderException, NoSuchAlgorithmException, KeyManagementException {
/* 130 */     this(xnio, optionMap, bufferPool, JsseSslUtils.createSSLContext(optionMap));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public UndertowXnioSsl(Xnio xnio, OptionMap optionMap, ByteBufferPool bufferPool, SSLContext sslContext) {
/* 141 */     this(xnio, optionMap, bufferPool, sslContext, null);
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
/*     */   public UndertowXnioSsl(Xnio xnio, OptionMap optionMap, ByteBufferPool bufferPool, SSLContext sslContext, Executor delegatedTaskExecutor) {
/* 153 */     super(xnio, sslContext, optionMap);
/* 154 */     this.bufferPool = bufferPool;
/* 155 */     this.sslContext = sslContext;
/* 156 */     this.delegatedTaskExecutor = delegatedTaskExecutor;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SSLContext getSslContext() {
/* 166 */     return this.sslContext;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   Executor getDelegatedTaskExecutor() {
/* 175 */     return this.delegatedTaskExecutor;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static SSLEngine getSslEngine(SslConnection connection) {
/* 184 */     if (connection instanceof UndertowSslConnection) {
/* 185 */       return ((UndertowSslConnection)connection).getSSLEngine();
/*     */     }
/* 187 */     return JsseXnioSsl.getSslEngine(connection);
/*     */   }
/*     */ 
/*     */   
/*     */   public static SslConduit getSslConduit(SslConnection connection) {
/* 192 */     return ((UndertowSslConnection)connection).getSslConduit();
/*     */   }
/*     */ 
/*     */   
/*     */   public IoFuture<ConnectedSslStreamChannel> connectSsl(XnioWorker worker, InetSocketAddress bindAddress, InetSocketAddress destination, final ChannelListener<? super ConnectedSslStreamChannel> openListener, ChannelListener<? super BoundChannel> bindListener, OptionMap optionMap) {
/* 197 */     final FutureResult<ConnectedSslStreamChannel> futureResult = new FutureResult(IoUtils.directExecutor());
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 207 */     IoFuture<SslConnection> futureSslConnection = openSslConnection(worker, bindAddress, destination, new ChannelListener<SslConnection>() { public void handleEvent(SslConnection sslConnection) { AssembledConnectedSslStreamChannel assembledConnectedSslStreamChannel = new AssembledConnectedSslStreamChannel((SslChannel)sslConnection, (StreamSourceChannel)sslConnection.getSourceChannel(), (StreamSinkChannel)sslConnection.getSinkChannel()); if (!futureResult.setResult(assembledConnectedSslStreamChannel)) { IoUtils.safeClose((Closeable)assembledConnectedSslStreamChannel); } else { ChannelListeners.invokeChannelListener((Channel)assembledConnectedSslStreamChannel, openListener); }  } }bindListener, optionMap).addNotifier((IoFuture.Notifier)new IoFuture.HandlingNotifier<SslConnection, FutureResult<ConnectedSslStreamChannel>>() {
/*     */           public void handleCancelled(FutureResult<ConnectedSslStreamChannel> result) {
/* 209 */             result.setCancelled();
/*     */           }
/*     */           
/*     */           public void handleFailed(IOException exception, FutureResult<ConnectedSslStreamChannel> result) {
/* 213 */             result.setException(exception);
/*     */           }
/*     */         },  futureResult);
/* 216 */     futureResult.getIoFuture().addNotifier((IoFuture.Notifier)new IoFuture.HandlingNotifier<ConnectedStreamChannel, IoFuture<SslConnection>>() {
/*     */           public void handleCancelled(IoFuture<SslConnection> result) {
/* 218 */             result.cancel();
/*     */           }
/*     */         },  futureSslConnection);
/* 221 */     futureResult.addCancelHandler((Cancellable)futureSslConnection);
/* 222 */     return futureResult.getIoFuture();
/*     */   }
/*     */   
/*     */   public IoFuture<SslConnection> openSslConnection(XnioWorker worker, InetSocketAddress bindAddress, InetSocketAddress destination, ChannelListener<? super SslConnection> openListener, ChannelListener<? super BoundChannel> bindListener, OptionMap optionMap) {
/* 226 */     FutureResult<SslConnection> futureResult = new FutureResult((Executor)worker);
/* 227 */     IoFuture<StreamConnection> connection = worker.openStreamConnection(bindAddress, destination, new StreamConnectionChannelListener(optionMap, destination, futureResult, openListener), bindListener, optionMap);
/* 228 */     return setupSslConnection(futureResult, connection);
/*     */   }
/*     */   
/*     */   public IoFuture<SslConnection> openSslConnection(XnioIoThread ioThread, InetSocketAddress bindAddress, InetSocketAddress destination, ChannelListener<? super SslConnection> openListener, ChannelListener<? super BoundChannel> bindListener, OptionMap optionMap) {
/* 232 */     FutureResult<SslConnection> futureResult = new FutureResult((Executor)ioThread);
/* 233 */     IoFuture<StreamConnection> connection = ioThread.openStreamConnection(bindAddress, destination, new StreamConnectionChannelListener(optionMap, destination, futureResult, openListener), bindListener, optionMap);
/* 234 */     return setupSslConnection(futureResult, connection);
/*     */   }
/*     */   
/*     */   public SslConnection wrapExistingConnection(StreamConnection connection, OptionMap optionMap) {
/* 238 */     return new UndertowSslConnection(connection, createSSLEngine(this.sslContext, optionMap, (InetSocketAddress)connection.getPeerAddress(), true), this.bufferPool, this.delegatedTaskExecutor);
/*     */   }
/*     */   
/*     */   public SslConnection wrapExistingConnection(StreamConnection connection, OptionMap optionMap, boolean clientMode) {
/* 242 */     return new UndertowSslConnection(connection, createSSLEngine(this.sslContext, optionMap, (InetSocketAddress)connection.getPeerAddress(), clientMode), this.bufferPool, this.delegatedTaskExecutor);
/*     */   }
/*     */   
/*     */   public SslConnection wrapExistingConnection(StreamConnection connection, OptionMap optionMap, URI destinationURI) {
/* 246 */     SSLEngine sslEngine = createSSLEngine(this.sslContext, optionMap, getPeerAddress(destinationURI), true);
/* 247 */     SSLParameters sslParameters = sslEngine.getSSLParameters();
/* 248 */     if (sslParameters.getServerNames() == null || sslParameters.getServerNames().isEmpty()) {
/* 249 */       sslParameters.setServerNames(Collections.singletonList(new SNIHostName(destinationURI.getHost())));
/* 250 */       sslEngine.setSSLParameters(sslParameters);
/*     */     } 
/* 252 */     return new UndertowSslConnection(connection, sslEngine, this.bufferPool, this.delegatedTaskExecutor);
/*     */   }
/*     */   
/*     */   private InetSocketAddress getPeerAddress(URI destinationURI) {
/* 256 */     String hostname = destinationURI.getHost();
/* 257 */     int port = destinationURI.getPort();
/* 258 */     if (port == -1) {
/* 259 */       port = destinationURI.getScheme().equals("wss") ? 443 : 80;
/*     */     }
/* 261 */     return new InetSocketAddress(hostname, port);
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
/*     */   private static SSLEngine createSSLEngine(SSLContext sslContext, OptionMap optionMap, InetSocketAddress peerAddress, boolean client) {
/* 274 */     SSLEngine engine = sslContext.createSSLEngine((String)optionMap
/* 275 */         .get(Options.SSL_PEER_HOST_NAME, peerAddress.getHostString()), optionMap
/* 276 */         .get(Options.SSL_PEER_PORT, peerAddress.getPort()));
/*     */     
/* 278 */     engine.setUseClientMode(client);
/* 279 */     engine.setEnableSessionCreation(optionMap.get(Options.SSL_ENABLE_SESSION_CREATION, true));
/* 280 */     Sequence<String> cipherSuites = (Sequence<String>)optionMap.get(Options.SSL_ENABLED_CIPHER_SUITES);
/* 281 */     if (cipherSuites != null) {
/* 282 */       Set<String> supported = new HashSet<>(Arrays.asList(engine.getSupportedCipherSuites()));
/* 283 */       List<String> finalList = new ArrayList<>();
/* 284 */       for (String name : cipherSuites) {
/* 285 */         if (supported.contains(name)) {
/* 286 */           finalList.add(name);
/*     */         }
/*     */       } 
/* 289 */       engine.setEnabledCipherSuites(finalList.<String>toArray(new String[finalList.size()]));
/*     */     } 
/* 291 */     Sequence<String> protocols = (Sequence<String>)optionMap.get(Options.SSL_ENABLED_PROTOCOLS);
/* 292 */     if (protocols != null) {
/* 293 */       Set<String> supported = new HashSet<>(Arrays.asList(engine.getSupportedProtocols()));
/* 294 */       List<String> finalList = new ArrayList<>();
/* 295 */       for (String name : protocols) {
/* 296 */         if (supported.contains(name)) {
/* 297 */           finalList.add(name);
/*     */         }
/*     */       } 
/* 300 */       engine.setEnabledProtocols(finalList.<String>toArray(new String[finalList.size()]));
/*     */     } 
/* 302 */     if (!client) {
/* 303 */       SslClientAuthMode clientAuthMode = (SslClientAuthMode)optionMap.get(Options.SSL_CLIENT_AUTH_MODE);
/* 304 */       if (clientAuthMode != null) {
/* 305 */         switch (clientAuthMode) {
/*     */           case NOT_REQUESTED:
/* 307 */             engine.setNeedClientAuth(false);
/* 308 */             engine.setWantClientAuth(false);
/*     */             break;
/*     */           case REQUESTED:
/* 311 */             engine.setWantClientAuth(true);
/*     */             break;
/*     */           case REQUIRED:
/* 314 */             engine.setNeedClientAuth(true);
/*     */             break;
/*     */           default:
/* 317 */             throw new IllegalStateException();
/*     */         } 
/*     */       }
/*     */     } 
/* 321 */     boolean useCipherSuitesOrder = optionMap.get(UndertowOptions.SSL_USER_CIPHER_SUITES_ORDER, false);
/* 322 */     if (useCipherSuitesOrder) {
/* 323 */       SSLParameters sslParameters = engine.getSSLParameters();
/* 324 */       sslParameters.setUseCipherSuitesOrder(true);
/* 325 */       engine.setSSLParameters(sslParameters);
/*     */     } 
/* 327 */     String endpointIdentificationAlgorithm = (String)optionMap.get(UndertowOptions.ENDPOINT_IDENTIFICATION_ALGORITHM, null);
/* 328 */     if (endpointIdentificationAlgorithm != null) {
/* 329 */       SSLParameters sslParameters = engine.getSSLParameters();
/* 330 */       sslParameters.setEndpointIdentificationAlgorithm(endpointIdentificationAlgorithm);
/* 331 */       engine.setSSLParameters(sslParameters);
/*     */     } 
/* 333 */     return engine;
/*     */   }
/*     */   
/*     */   private IoFuture<SslConnection> setupSslConnection(FutureResult<SslConnection> futureResult, IoFuture<StreamConnection> connection) {
/* 337 */     connection.addNotifier((IoFuture.Notifier)new IoFuture.HandlingNotifier<StreamConnection, FutureResult<SslConnection>>() {
/*     */           public void handleCancelled(FutureResult<SslConnection> attachment) {
/* 339 */             attachment.setCancelled();
/*     */           }
/*     */           
/*     */           public void handleFailed(IOException exception, FutureResult<SslConnection> attachment) {
/* 343 */             attachment.setException(exception);
/*     */           }
/*     */         },  futureResult);
/* 346 */     futureResult.addCancelHandler((Cancellable)connection);
/* 347 */     return futureResult.getIoFuture();
/*     */   }
/*     */ 
/*     */   
/*     */   public AcceptingChannel<ConnectedSslStreamChannel> createSslTcpServer(XnioWorker worker, InetSocketAddress bindAddress, ChannelListener<? super AcceptingChannel<ConnectedSslStreamChannel>> acceptListener, OptionMap optionMap) throws IOException {
/* 352 */     final AcceptingChannel<SslConnection> server = createSslConnectionServer(worker, bindAddress, null, optionMap);
/* 353 */     AcceptingChannel<ConnectedSslStreamChannel> acceptingChannel = new AcceptingChannel<ConnectedSslStreamChannel>() {
/*     */         public ConnectedSslStreamChannel accept() throws IOException {
/* 355 */           SslConnection connection = (SslConnection)server.accept();
/* 356 */           return (connection == null) ? null : (ConnectedSslStreamChannel)new AssembledConnectedSslStreamChannel((SslChannel)connection, (StreamSourceChannel)connection.getSourceChannel(), (StreamSinkChannel)connection.getSinkChannel());
/*     */         }
/*     */         
/*     */         public ChannelListener.Setter<? extends AcceptingChannel<ConnectedSslStreamChannel>> getAcceptSetter() {
/* 360 */           return ChannelListeners.getDelegatingSetter(server.getAcceptSetter(), (Channel)this);
/*     */         }
/*     */         
/*     */         public ChannelListener.Setter<? extends AcceptingChannel<ConnectedSslStreamChannel>> getCloseSetter() {
/* 364 */           return ChannelListeners.getDelegatingSetter(server.getCloseSetter(), (Channel)this);
/*     */         }
/*     */         
/*     */         public SocketAddress getLocalAddress() {
/* 368 */           return server.getLocalAddress();
/*     */         }
/*     */         
/*     */         public <A extends SocketAddress> A getLocalAddress(Class<A> type) {
/* 372 */           return (A)server.getLocalAddress(type);
/*     */         }
/*     */         
/*     */         public void suspendAccepts() {
/* 376 */           server.suspendAccepts();
/*     */         }
/*     */         
/*     */         public void resumeAccepts() {
/* 380 */           server.resumeAccepts();
/*     */         }
/*     */         
/*     */         public boolean isAcceptResumed() {
/* 384 */           return server.isAcceptResumed();
/*     */         }
/*     */         
/*     */         public void wakeupAccepts() {
/* 388 */           server.wakeupAccepts();
/*     */         }
/*     */         
/*     */         public void awaitAcceptable() throws IOException {
/* 392 */           server.awaitAcceptable();
/*     */         }
/*     */         
/*     */         public void awaitAcceptable(long time, TimeUnit timeUnit) throws IOException {
/* 396 */           server.awaitAcceptable(time, timeUnit);
/*     */         }
/*     */         
/*     */         public XnioWorker getWorker() {
/* 400 */           return server.getWorker();
/*     */         }
/*     */         
/*     */         @Deprecated
/*     */         public XnioExecutor getAcceptThread() {
/* 405 */           return server.getAcceptThread();
/*     */         }
/*     */         
/*     */         public XnioIoThread getIoThread() {
/* 409 */           return server.getIoThread();
/*     */         }
/*     */         
/*     */         public void close() throws IOException {
/* 413 */           server.close();
/*     */         }
/*     */         
/*     */         public boolean isOpen() {
/* 417 */           return server.isOpen();
/*     */         }
/*     */         
/*     */         public boolean supportsOption(Option<?> option) {
/* 421 */           return server.supportsOption(option);
/*     */         }
/*     */         
/*     */         public <T> T getOption(Option<T> option) throws IOException {
/* 425 */           return (T)server.getOption(option);
/*     */         }
/*     */         
/*     */         public <T> T setOption(Option<T> option, T value) throws IllegalArgumentException, IOException {
/* 429 */           return (T)server.setOption(option, value);
/*     */         }
/*     */       };
/* 432 */     acceptingChannel.getAcceptSetter().set(acceptListener);
/* 433 */     return acceptingChannel;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void updateSSLContext(SSLContext context) {
/* 443 */     this.sslContext = context;
/*     */   }
/*     */   
/*     */   public AcceptingChannel<SslConnection> createSslConnectionServer(XnioWorker worker, InetSocketAddress bindAddress, ChannelListener<? super AcceptingChannel<SslConnection>> acceptListener, OptionMap optionMap) throws IOException {
/* 447 */     UndertowAcceptingSslChannel server = new UndertowAcceptingSslChannel(this, worker.createStreamConnectionServer(bindAddress, null, optionMap), optionMap, this.bufferPool, false);
/* 448 */     if (acceptListener != null) server.getAcceptSetter().set(acceptListener); 
/* 449 */     return server;
/*     */   }
/*     */   
/*     */   private class StreamConnectionChannelListener implements ChannelListener<StreamConnection> {
/*     */     private final OptionMap optionMap;
/*     */     private final InetSocketAddress destination;
/*     */     private final FutureResult<SslConnection> futureResult;
/*     */     private final ChannelListener<? super SslConnection> openListener;
/*     */     
/*     */     StreamConnectionChannelListener(OptionMap optionMap, InetSocketAddress destination, FutureResult<SslConnection> futureResult, ChannelListener<? super SslConnection> openListener) {
/* 459 */       this.optionMap = optionMap;
/* 460 */       this.destination = destination;
/* 461 */       this.futureResult = futureResult;
/* 462 */       this.openListener = openListener;
/*     */     }
/*     */ 
/*     */     
/*     */     public void handleEvent(StreamConnection connection) {
/*     */       try {
/* 468 */         SSLEngine sslEngine = JsseSslUtils.createSSLEngine(UndertowXnioSsl.this.sslContext, this.optionMap, this.destination);
/* 469 */         SSLParameters params = sslEngine.getSSLParameters();
/* 470 */         InetAddress address = this.destination.getAddress();
/* 471 */         String hostnameValue = this.destination.getHostString();
/* 472 */         if (address instanceof java.net.Inet6Address && hostnameValue.contains(":"))
/*     */         {
/*     */           
/* 475 */           hostnameValue = address.getHostName();
/*     */         }
/* 477 */         params.setServerNames(Collections.singletonList(new SNIHostName(hostnameValue)));
/* 478 */         String endpointIdentificationAlgorithm = (String)this.optionMap.get(UndertowOptions.ENDPOINT_IDENTIFICATION_ALGORITHM, null);
/* 479 */         if (endpointIdentificationAlgorithm != null) {
/* 480 */           params.setEndpointIdentificationAlgorithm(endpointIdentificationAlgorithm);
/*     */         }
/*     */         
/* 483 */         sslEngine.setSSLParameters(params);
/*     */         
/* 485 */         SslConnection wrappedConnection = new UndertowSslConnection(connection, sslEngine, UndertowXnioSsl.this.bufferPool, UndertowXnioSsl.this.delegatedTaskExecutor);
/* 486 */         if (!this.futureResult.setResult(wrappedConnection)) {
/* 487 */           IoUtils.safeClose((Closeable)connection);
/*     */         } else {
/* 489 */           ChannelListeners.invokeChannelListener((Channel)wrappedConnection, this.openListener);
/*     */         } 
/* 491 */       } catch (Throwable e) {
/* 492 */         this.futureResult.setException(new IOException(e));
/*     */       } 
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\protocols\ssl\UndertowXnioSsl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */