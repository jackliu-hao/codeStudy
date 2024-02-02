/*     */ package org.xnio.ssl;
/*     */ 
/*     */ import java.io.Closeable;
/*     */ import java.io.IOException;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.net.SocketAddress;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.channels.Channel;
/*     */ import java.security.AccessController;
/*     */ import java.security.KeyManagementException;
/*     */ import java.security.NoSuchAlgorithmException;
/*     */ import java.security.NoSuchProviderException;
/*     */ import java.util.concurrent.Executor;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import javax.net.ssl.SSLContext;
/*     */ import javax.net.ssl.SSLEngine;
/*     */ import org.xnio.BufferAllocator;
/*     */ import org.xnio.ByteBufferSlicePool;
/*     */ import org.xnio.Cancellable;
/*     */ import org.xnio.ChannelListener;
/*     */ import org.xnio.ChannelListeners;
/*     */ import org.xnio.FutureResult;
/*     */ import org.xnio.IoFuture;
/*     */ import org.xnio.IoUtils;
/*     */ import org.xnio.Option;
/*     */ import org.xnio.OptionMap;
/*     */ import org.xnio.Options;
/*     */ import org.xnio.Pool;
/*     */ import org.xnio.StreamConnection;
/*     */ import org.xnio.Xnio;
/*     */ import org.xnio.XnioExecutor;
/*     */ import org.xnio.XnioIoThread;
/*     */ import org.xnio.XnioWorker;
/*     */ import org.xnio._private.Messages;
/*     */ import org.xnio.channels.AcceptingChannel;
/*     */ import org.xnio.channels.AssembledConnectedSslStreamChannel;
/*     */ import org.xnio.channels.BoundChannel;
/*     */ import org.xnio.channels.CloseableChannel;
/*     */ import org.xnio.channels.ConnectedChannel;
/*     */ import org.xnio.channels.ConnectedSslStreamChannel;
/*     */ import org.xnio.channels.ConnectedStreamChannel;
/*     */ import org.xnio.channels.StreamSinkChannel;
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
/*     */ public final class JsseXnioSsl
/*     */   extends XnioSsl
/*     */ {
/*  67 */   public static final boolean NEW_IMPL = ((Boolean)AccessController.<Boolean>doPrivileged(() -> Boolean.valueOf(Boolean.parseBoolean(System.getProperty("org.xnio.ssl.new", "false"))))).booleanValue();
/*     */   
/*  69 */   static final Pool<ByteBuffer> bufferPool = (Pool<ByteBuffer>)new ByteBufferSlicePool(BufferAllocator.DIRECT_BYTE_BUFFER_ALLOCATOR, 21504, 2752512);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final SSLContext sslContext;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JsseXnioSsl(Xnio xnio, OptionMap optionMap) throws NoSuchProviderException, NoSuchAlgorithmException, KeyManagementException {
/*  82 */     this(xnio, optionMap, JsseSslUtils.createSSLContext(optionMap));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JsseXnioSsl(Xnio xnio, OptionMap optionMap, SSLContext sslContext) {
/*  93 */     super(xnio, sslContext, optionMap);
/*  94 */     this.sslContext = sslContext;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SSLContext getSslContext() {
/* 104 */     return this.sslContext;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static SSLEngine getSslEngine(SslConnection connection) {
/* 113 */     if (connection instanceof JsseSslStreamConnection)
/* 114 */       return ((JsseSslStreamConnection)connection).getEngine(); 
/* 115 */     if (connection instanceof JsseSslConnection) {
/* 116 */       return ((JsseSslConnection)connection).getEngine();
/*     */     }
/* 118 */     throw Messages.msg.notFromThisProvider();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public IoFuture<ConnectedSslStreamChannel> connectSsl(XnioWorker worker, InetSocketAddress bindAddress, InetSocketAddress destination, final ChannelListener<? super ConnectedSslStreamChannel> openListener, ChannelListener<? super BoundChannel> bindListener, OptionMap optionMap) {
/* 124 */     final FutureResult<ConnectedSslStreamChannel> futureResult = new FutureResult(IoUtils.directExecutor());
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 134 */     IoFuture<SslConnection> futureSslConnection = openSslConnection(worker, bindAddress, destination, new ChannelListener<SslConnection>() { public void handleEvent(SslConnection sslConnection) { AssembledConnectedSslStreamChannel assembledConnectedSslStreamChannel = new AssembledConnectedSslStreamChannel(sslConnection, (StreamSourceChannel)sslConnection.getSourceChannel(), (StreamSinkChannel)sslConnection.getSinkChannel()); if (!futureResult.setResult(assembledConnectedSslStreamChannel)) { IoUtils.safeClose((Closeable)assembledConnectedSslStreamChannel); } else { ChannelListeners.invokeChannelListener((Channel)assembledConnectedSslStreamChannel, openListener); }  } }bindListener, optionMap).addNotifier((IoFuture.Notifier)new IoFuture.HandlingNotifier<SslConnection, FutureResult<ConnectedSslStreamChannel>>() {
/*     */           public void handleCancelled(FutureResult<ConnectedSslStreamChannel> result) {
/* 136 */             result.setCancelled();
/*     */           }
/*     */           
/*     */           public void handleFailed(IOException exception, FutureResult<ConnectedSslStreamChannel> result) {
/* 140 */             result.setException(exception);
/*     */           }
/*     */         },  futureResult);
/* 143 */     futureResult.getIoFuture().addNotifier((IoFuture.Notifier)new IoFuture.HandlingNotifier<ConnectedStreamChannel, IoFuture<SslConnection>>() {
/*     */           public void handleCancelled(IoFuture<SslConnection> result) {
/* 145 */             result.cancel();
/*     */           }
/*     */         },  futureSslConnection);
/* 148 */     futureResult.addCancelHandler((Cancellable)futureSslConnection);
/* 149 */     return futureResult.getIoFuture();
/*     */   }
/*     */   
/*     */   public IoFuture<SslConnection> openSslConnection(XnioWorker worker, InetSocketAddress bindAddress, InetSocketAddress destination, ChannelListener<? super SslConnection> openListener, ChannelListener<? super BoundChannel> bindListener, OptionMap optionMap) {
/* 153 */     return openSslConnection(worker.getIoThread(), bindAddress, destination, openListener, bindListener, optionMap);
/*     */   }
/*     */   
/*     */   public IoFuture<SslConnection> openSslConnection(XnioIoThread ioThread, InetSocketAddress bindAddress, final InetSocketAddress destination, final ChannelListener<? super SslConnection> openListener, ChannelListener<? super BoundChannel> bindListener, final OptionMap optionMap) {
/* 157 */     final FutureResult<SslConnection> futureResult = new FutureResult((Executor)ioThread);
/* 158 */     IoFuture<StreamConnection> connection = ioThread.openStreamConnection(bindAddress, destination, new ChannelListener<StreamConnection>() { public void handleEvent(StreamConnection connection) {
/*     */             SslConnection wrappedConnection;
/* 160 */             SSLEngine sslEngine = JsseSslUtils.createSSLEngine(JsseXnioSsl.this.sslContext, optionMap, destination);
/* 161 */             boolean startTls = optionMap.get(Options.SSL_STARTTLS, false);
/*     */             
/*     */             try {
/* 164 */               wrappedConnection = JsseXnioSsl.NEW_IMPL ? new JsseSslConnection(connection, sslEngine, JsseXnioSsl.bufferPool, JsseXnioSsl.bufferPool) : new JsseSslStreamConnection(connection, sslEngine, JsseXnioSsl.bufferPool, JsseXnioSsl.bufferPool, startTls);
/* 165 */             } catch (RuntimeException e) {
/* 166 */               futureResult.setCancelled();
/* 167 */               throw e;
/*     */             } 
/* 169 */             if (JsseXnioSsl.NEW_IMPL && !startTls) {
/*     */               try {
/* 171 */                 wrappedConnection.startHandshake();
/* 172 */               } catch (IOException e) {
/* 173 */                 if (futureResult.setException(e)) {
/* 174 */                   IoUtils.safeClose((Closeable)connection);
/*     */                 }
/*     */               } 
/*     */             }
/* 178 */             if (!futureResult.setResult(wrappedConnection)) {
/* 179 */               IoUtils.safeClose((Closeable)connection);
/*     */             } else {
/* 181 */               ChannelListeners.invokeChannelListener((Channel)wrappedConnection, openListener);
/*     */             } 
/*     */           } }
/*     */         bindListener, optionMap);
/* 185 */     connection.addNotifier((IoFuture.Notifier)new IoFuture.HandlingNotifier<StreamConnection, FutureResult<SslConnection>>() {
/*     */           public void handleCancelled(FutureResult<SslConnection> attachment) {
/* 187 */             attachment.setCancelled();
/*     */           }
/*     */           
/*     */           public void handleFailed(IOException exception, FutureResult<SslConnection> attachment) {
/* 191 */             attachment.setException(exception);
/*     */           }
/*     */         },  futureResult);
/* 194 */     futureResult.addCancelHandler((Cancellable)connection);
/* 195 */     return futureResult.getIoFuture();
/*     */   }
/*     */ 
/*     */   
/*     */   public AcceptingChannel<ConnectedSslStreamChannel> createSslTcpServer(XnioWorker worker, InetSocketAddress bindAddress, ChannelListener<? super AcceptingChannel<ConnectedSslStreamChannel>> acceptListener, OptionMap optionMap) throws IOException {
/* 200 */     final AcceptingChannel<SslConnection> server = createSslConnectionServer(worker, bindAddress, null, optionMap);
/* 201 */     AcceptingChannel<ConnectedSslStreamChannel> acceptingChannel = new AcceptingChannel<ConnectedSslStreamChannel>() {
/*     */         public ConnectedSslStreamChannel accept() throws IOException {
/* 203 */           SslConnection connection = (SslConnection)server.accept();
/* 204 */           return (connection == null) ? null : (ConnectedSslStreamChannel)new AssembledConnectedSslStreamChannel(connection, (StreamSourceChannel)connection.getSourceChannel(), (StreamSinkChannel)connection.getSinkChannel());
/*     */         }
/*     */         
/*     */         public ChannelListener.Setter<? extends AcceptingChannel<ConnectedSslStreamChannel>> getAcceptSetter() {
/* 208 */           return ChannelListeners.getDelegatingSetter(server.getAcceptSetter(), (Channel)this);
/*     */         }
/*     */         
/*     */         public ChannelListener.Setter<? extends AcceptingChannel<ConnectedSslStreamChannel>> getCloseSetter() {
/* 212 */           return ChannelListeners.getDelegatingSetter(server.getCloseSetter(), (Channel)this);
/*     */         }
/*     */         
/*     */         public SocketAddress getLocalAddress() {
/* 216 */           return server.getLocalAddress();
/*     */         }
/*     */         
/*     */         public <A extends SocketAddress> A getLocalAddress(Class<A> type) {
/* 220 */           return (A)server.getLocalAddress(type);
/*     */         }
/*     */         
/*     */         public void suspendAccepts() {
/* 224 */           server.suspendAccepts();
/*     */         }
/*     */         
/*     */         public void resumeAccepts() {
/* 228 */           server.resumeAccepts();
/*     */         }
/*     */         
/*     */         public boolean isAcceptResumed() {
/* 232 */           return server.isAcceptResumed();
/*     */         }
/*     */         
/*     */         public void wakeupAccepts() {
/* 236 */           server.wakeupAccepts();
/*     */         }
/*     */         
/*     */         public void awaitAcceptable() throws IOException {
/* 240 */           server.awaitAcceptable();
/*     */         }
/*     */         
/*     */         public void awaitAcceptable(long time, TimeUnit timeUnit) throws IOException {
/* 244 */           server.awaitAcceptable(time, timeUnit);
/*     */         }
/*     */         
/*     */         public XnioWorker getWorker() {
/* 248 */           return server.getWorker();
/*     */         }
/*     */         
/*     */         @Deprecated
/*     */         public XnioExecutor getAcceptThread() {
/* 253 */           return server.getAcceptThread();
/*     */         }
/*     */         
/*     */         public XnioIoThread getIoThread() {
/* 257 */           return server.getIoThread();
/*     */         }
/*     */         
/*     */         public void close() throws IOException {
/* 261 */           server.close();
/*     */         }
/*     */         
/*     */         public boolean isOpen() {
/* 265 */           return server.isOpen();
/*     */         }
/*     */         
/*     */         public boolean supportsOption(Option<?> option) {
/* 269 */           return server.supportsOption(option);
/*     */         }
/*     */         
/*     */         public <T> T getOption(Option<T> option) throws IOException {
/* 273 */           return (T)server.getOption(option);
/*     */         }
/*     */         
/*     */         public <T> T setOption(Option<T> option, T value) throws IllegalArgumentException, IOException {
/* 277 */           return (T)server.setOption(option, value);
/*     */         }
/*     */       };
/* 280 */     acceptingChannel.getAcceptSetter().set(acceptListener);
/* 281 */     return acceptingChannel;
/*     */   }
/*     */   
/*     */   public AcceptingChannel<SslConnection> createSslConnectionServer(XnioWorker worker, InetSocketAddress bindAddress, ChannelListener<? super AcceptingChannel<SslConnection>> acceptListener, OptionMap optionMap) throws IOException {
/* 285 */     JsseAcceptingSslStreamConnection server = new JsseAcceptingSslStreamConnection(this.sslContext, worker.createStreamConnectionServer(bindAddress, null, optionMap), optionMap, bufferPool, bufferPool, optionMap.get(Options.SSL_STARTTLS, false));
/* 286 */     if (acceptListener != null) server.getAcceptSetter().set(acceptListener); 
/* 287 */     return server;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\ssl\JsseXnioSsl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */