package org.xnio.ssl;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.Channel;
import java.security.AccessController;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.concurrent.TimeUnit;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import org.xnio.BufferAllocator;
import org.xnio.ByteBufferSlicePool;
import org.xnio.ChannelListener;
import org.xnio.ChannelListeners;
import org.xnio.FutureResult;
import org.xnio.IoFuture;
import org.xnio.IoUtils;
import org.xnio.Option;
import org.xnio.OptionMap;
import org.xnio.Options;
import org.xnio.Pool;
import org.xnio.StreamConnection;
import org.xnio.Xnio;
import org.xnio.XnioExecutor;
import org.xnio.XnioIoThread;
import org.xnio.XnioWorker;
import org.xnio._private.Messages;
import org.xnio.channels.AcceptingChannel;
import org.xnio.channels.AssembledConnectedSslStreamChannel;
import org.xnio.channels.BoundChannel;
import org.xnio.channels.ConnectedSslStreamChannel;
import org.xnio.channels.ConnectedStreamChannel;

public final class JsseXnioSsl extends XnioSsl {
   public static final boolean NEW_IMPL = (Boolean)AccessController.doPrivileged(() -> {
      return Boolean.parseBoolean(System.getProperty("org.xnio.ssl.new", "false"));
   });
   static final Pool<ByteBuffer> bufferPool;
   private final SSLContext sslContext;

   public JsseXnioSsl(Xnio xnio, OptionMap optionMap) throws NoSuchProviderException, NoSuchAlgorithmException, KeyManagementException {
      this(xnio, optionMap, JsseSslUtils.createSSLContext(optionMap));
   }

   public JsseXnioSsl(Xnio xnio, OptionMap optionMap, SSLContext sslContext) {
      super(xnio, sslContext, optionMap);
      this.sslContext = sslContext;
   }

   public SSLContext getSslContext() {
      return this.sslContext;
   }

   public static SSLEngine getSslEngine(SslConnection connection) {
      if (connection instanceof JsseSslStreamConnection) {
         return ((JsseSslStreamConnection)connection).getEngine();
      } else if (connection instanceof JsseSslConnection) {
         return ((JsseSslConnection)connection).getEngine();
      } else {
         throw Messages.msg.notFromThisProvider();
      }
   }

   public IoFuture<ConnectedSslStreamChannel> connectSsl(XnioWorker worker, InetSocketAddress bindAddress, InetSocketAddress destination, final ChannelListener<? super ConnectedSslStreamChannel> openListener, ChannelListener<? super BoundChannel> bindListener, OptionMap optionMap) {
      final FutureResult<ConnectedSslStreamChannel> futureResult = new FutureResult(IoUtils.directExecutor());
      IoFuture<SslConnection> futureSslConnection = this.openSslConnection(worker, bindAddress, destination, new ChannelListener<SslConnection>() {
         public void handleEvent(SslConnection sslConnection) {
            ConnectedSslStreamChannel assembledChannel = new AssembledConnectedSslStreamChannel(sslConnection, sslConnection.getSourceChannel(), sslConnection.getSinkChannel());
            if (!futureResult.setResult(assembledChannel)) {
               IoUtils.safeClose((Closeable)assembledChannel);
            } else {
               ChannelListeners.invokeChannelListener(assembledChannel, openListener);
            }

         }
      }, bindListener, optionMap).addNotifier(new IoFuture.HandlingNotifier<SslConnection, FutureResult<ConnectedSslStreamChannel>>() {
         public void handleCancelled(FutureResult<ConnectedSslStreamChannel> result) {
            result.setCancelled();
         }

         public void handleFailed(IOException exception, FutureResult<ConnectedSslStreamChannel> result) {
            result.setException(exception);
         }
      }, futureResult);
      futureResult.getIoFuture().addNotifier(new IoFuture.HandlingNotifier<ConnectedStreamChannel, IoFuture<SslConnection>>() {
         public void handleCancelled(IoFuture<SslConnection> result) {
            result.cancel();
         }
      }, futureSslConnection);
      futureResult.addCancelHandler(futureSslConnection);
      return futureResult.getIoFuture();
   }

   public IoFuture<SslConnection> openSslConnection(XnioWorker worker, InetSocketAddress bindAddress, InetSocketAddress destination, ChannelListener<? super SslConnection> openListener, ChannelListener<? super BoundChannel> bindListener, OptionMap optionMap) {
      return this.openSslConnection(worker.getIoThread(), bindAddress, destination, openListener, bindListener, optionMap);
   }

   public IoFuture<SslConnection> openSslConnection(XnioIoThread ioThread, InetSocketAddress bindAddress, final InetSocketAddress destination, final ChannelListener<? super SslConnection> openListener, ChannelListener<? super BoundChannel> bindListener, final OptionMap optionMap) {
      final FutureResult<SslConnection> futureResult = new FutureResult(ioThread);
      IoFuture<StreamConnection> connection = ioThread.openStreamConnection(bindAddress, destination, new ChannelListener<StreamConnection>() {
         public void handleEvent(StreamConnection connection) {
            SSLEngine sslEngine = JsseSslUtils.createSSLEngine(JsseXnioSsl.this.sslContext, optionMap, destination);
            boolean startTls = optionMap.get(Options.SSL_STARTTLS, false);

            Object wrappedConnection;
            try {
               wrappedConnection = JsseXnioSsl.NEW_IMPL ? new JsseSslConnection(connection, sslEngine, JsseXnioSsl.bufferPool, JsseXnioSsl.bufferPool) : new JsseSslStreamConnection(connection, sslEngine, JsseXnioSsl.bufferPool, JsseXnioSsl.bufferPool, startTls);
            } catch (RuntimeException var6) {
               futureResult.setCancelled();
               throw var6;
            }

            if (JsseXnioSsl.NEW_IMPL && !startTls) {
               try {
                  ((SslConnection)wrappedConnection).startHandshake();
               } catch (IOException var7) {
                  if (futureResult.setException(var7)) {
                     IoUtils.safeClose((Closeable)connection);
                  }
               }
            }

            if (!futureResult.setResult(wrappedConnection)) {
               IoUtils.safeClose((Closeable)connection);
            } else {
               ChannelListeners.invokeChannelListener((Channel)wrappedConnection, openListener);
            }

         }
      }, bindListener, optionMap);
      connection.addNotifier(new IoFuture.HandlingNotifier<StreamConnection, FutureResult<SslConnection>>() {
         public void handleCancelled(FutureResult<SslConnection> attachment) {
            attachment.setCancelled();
         }

         public void handleFailed(IOException exception, FutureResult<SslConnection> attachment) {
            attachment.setException(exception);
         }
      }, futureResult);
      futureResult.addCancelHandler(connection);
      return futureResult.getIoFuture();
   }

   public AcceptingChannel<ConnectedSslStreamChannel> createSslTcpServer(XnioWorker worker, InetSocketAddress bindAddress, ChannelListener<? super AcceptingChannel<ConnectedSslStreamChannel>> acceptListener, OptionMap optionMap) throws IOException {
      final AcceptingChannel<SslConnection> server = this.createSslConnectionServer(worker, bindAddress, (ChannelListener)null, optionMap);
      AcceptingChannel<ConnectedSslStreamChannel> acceptingChannel = new AcceptingChannel<ConnectedSslStreamChannel>() {
         public ConnectedSslStreamChannel accept() throws IOException {
            SslConnection connection = (SslConnection)server.accept();
            return connection == null ? null : new AssembledConnectedSslStreamChannel(connection, connection.getSourceChannel(), connection.getSinkChannel());
         }

         public ChannelListener.Setter<? extends AcceptingChannel<ConnectedSslStreamChannel>> getAcceptSetter() {
            return ChannelListeners.getDelegatingSetter(server.getAcceptSetter(), this);
         }

         public ChannelListener.Setter<? extends AcceptingChannel<ConnectedSslStreamChannel>> getCloseSetter() {
            return ChannelListeners.getDelegatingSetter(server.getCloseSetter(), this);
         }

         public SocketAddress getLocalAddress() {
            return server.getLocalAddress();
         }

         public <A extends SocketAddress> A getLocalAddress(Class<A> type) {
            return server.getLocalAddress(type);
         }

         public void suspendAccepts() {
            server.suspendAccepts();
         }

         public void resumeAccepts() {
            server.resumeAccepts();
         }

         public boolean isAcceptResumed() {
            return server.isAcceptResumed();
         }

         public void wakeupAccepts() {
            server.wakeupAccepts();
         }

         public void awaitAcceptable() throws IOException {
            server.awaitAcceptable();
         }

         public void awaitAcceptable(long time, TimeUnit timeUnit) throws IOException {
            server.awaitAcceptable(time, timeUnit);
         }

         public XnioWorker getWorker() {
            return server.getWorker();
         }

         /** @deprecated */
         @Deprecated
         public XnioExecutor getAcceptThread() {
            return server.getAcceptThread();
         }

         public XnioIoThread getIoThread() {
            return server.getIoThread();
         }

         public void close() throws IOException {
            server.close();
         }

         public boolean isOpen() {
            return server.isOpen();
         }

         public boolean supportsOption(Option<?> option) {
            return server.supportsOption(option);
         }

         public <T> T getOption(Option<T> option) throws IOException {
            return server.getOption(option);
         }

         public <T> T setOption(Option<T> option, T value) throws IllegalArgumentException, IOException {
            return server.setOption(option, value);
         }
      };
      acceptingChannel.getAcceptSetter().set(acceptListener);
      return acceptingChannel;
   }

   public AcceptingChannel<SslConnection> createSslConnectionServer(XnioWorker worker, InetSocketAddress bindAddress, ChannelListener<? super AcceptingChannel<SslConnection>> acceptListener, OptionMap optionMap) throws IOException {
      JsseAcceptingSslStreamConnection server = new JsseAcceptingSslStreamConnection(this.sslContext, worker.createStreamConnectionServer(bindAddress, (ChannelListener)null, optionMap), optionMap, bufferPool, bufferPool, optionMap.get(Options.SSL_STARTTLS, false));
      if (acceptListener != null) {
         server.getAcceptSetter().set(acceptListener);
      }

      return server;
   }

   static {
      bufferPool = new ByteBufferSlicePool(BufferAllocator.DIRECT_BYTE_BUFFER_ALLOCATOR, 21504, 2752512);
   }
}
