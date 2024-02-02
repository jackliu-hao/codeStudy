package io.undertow.protocols.ssl;

import io.undertow.UndertowOptions;
import io.undertow.connector.ByteBufferPool;
import io.undertow.server.DefaultByteBufferPool;
import java.io.Closeable;
import java.io.IOException;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.URI;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import javax.net.ssl.SNIHostName;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLParameters;
import org.xnio.ChannelListener;
import org.xnio.ChannelListeners;
import org.xnio.FutureResult;
import org.xnio.IoFuture;
import org.xnio.IoUtils;
import org.xnio.Option;
import org.xnio.OptionMap;
import org.xnio.Options;
import org.xnio.Sequence;
import org.xnio.SslClientAuthMode;
import org.xnio.StreamConnection;
import org.xnio.Xnio;
import org.xnio.XnioExecutor;
import org.xnio.XnioIoThread;
import org.xnio.XnioWorker;
import org.xnio.channels.AcceptingChannel;
import org.xnio.channels.AssembledConnectedSslStreamChannel;
import org.xnio.channels.BoundChannel;
import org.xnio.channels.ConnectedSslStreamChannel;
import org.xnio.channels.ConnectedStreamChannel;
import org.xnio.ssl.JsseSslUtils;
import org.xnio.ssl.JsseXnioSsl;
import org.xnio.ssl.SslConnection;
import org.xnio.ssl.XnioSsl;

public class UndertowXnioSsl extends XnioSsl {
   private static final ByteBufferPool DEFAULT_BUFFER_POOL = new DefaultByteBufferPool(true, 17408, -1, 12);
   private final ByteBufferPool bufferPool;
   private final Executor delegatedTaskExecutor;
   private volatile SSLContext sslContext;

   public UndertowXnioSsl(Xnio xnio, OptionMap optionMap) throws NoSuchProviderException, NoSuchAlgorithmException, KeyManagementException {
      this(xnio, optionMap, DEFAULT_BUFFER_POOL, JsseSslUtils.createSSLContext(optionMap));
   }

   public UndertowXnioSsl(Xnio xnio, OptionMap optionMap, SSLContext sslContext) {
      this(xnio, optionMap, DEFAULT_BUFFER_POOL, sslContext);
   }

   public UndertowXnioSsl(Xnio xnio, OptionMap optionMap, SSLContext sslContext, Executor delegatedTaskExecutor) {
      this(xnio, optionMap, DEFAULT_BUFFER_POOL, sslContext, delegatedTaskExecutor);
   }

   public UndertowXnioSsl(Xnio xnio, OptionMap optionMap, ByteBufferPool bufferPool) throws NoSuchProviderException, NoSuchAlgorithmException, KeyManagementException {
      this(xnio, optionMap, bufferPool, JsseSslUtils.createSSLContext(optionMap));
   }

   public UndertowXnioSsl(Xnio xnio, OptionMap optionMap, ByteBufferPool bufferPool, SSLContext sslContext) {
      this(xnio, optionMap, bufferPool, sslContext, (Executor)null);
   }

   public UndertowXnioSsl(Xnio xnio, OptionMap optionMap, ByteBufferPool bufferPool, SSLContext sslContext, Executor delegatedTaskExecutor) {
      super(xnio, sslContext, optionMap);
      this.bufferPool = bufferPool;
      this.sslContext = sslContext;
      this.delegatedTaskExecutor = delegatedTaskExecutor;
   }

   public SSLContext getSslContext() {
      return this.sslContext;
   }

   Executor getDelegatedTaskExecutor() {
      return this.delegatedTaskExecutor;
   }

   public static SSLEngine getSslEngine(SslConnection connection) {
      return connection instanceof UndertowSslConnection ? ((UndertowSslConnection)connection).getSSLEngine() : JsseXnioSsl.getSslEngine(connection);
   }

   public static SslConduit getSslConduit(SslConnection connection) {
      return ((UndertowSslConnection)connection).getSslConduit();
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
      FutureResult<SslConnection> futureResult = new FutureResult(worker);
      IoFuture<StreamConnection> connection = worker.openStreamConnection(bindAddress, destination, new StreamConnectionChannelListener(optionMap, destination, futureResult, openListener), bindListener, optionMap);
      return this.setupSslConnection(futureResult, connection);
   }

   public IoFuture<SslConnection> openSslConnection(XnioIoThread ioThread, InetSocketAddress bindAddress, InetSocketAddress destination, ChannelListener<? super SslConnection> openListener, ChannelListener<? super BoundChannel> bindListener, OptionMap optionMap) {
      FutureResult<SslConnection> futureResult = new FutureResult(ioThread);
      IoFuture<StreamConnection> connection = ioThread.openStreamConnection(bindAddress, destination, new StreamConnectionChannelListener(optionMap, destination, futureResult, openListener), bindListener, optionMap);
      return this.setupSslConnection(futureResult, connection);
   }

   public SslConnection wrapExistingConnection(StreamConnection connection, OptionMap optionMap) {
      return new UndertowSslConnection(connection, createSSLEngine(this.sslContext, optionMap, (InetSocketAddress)connection.getPeerAddress(), true), this.bufferPool, this.delegatedTaskExecutor);
   }

   public SslConnection wrapExistingConnection(StreamConnection connection, OptionMap optionMap, boolean clientMode) {
      return new UndertowSslConnection(connection, createSSLEngine(this.sslContext, optionMap, (InetSocketAddress)connection.getPeerAddress(), clientMode), this.bufferPool, this.delegatedTaskExecutor);
   }

   public SslConnection wrapExistingConnection(StreamConnection connection, OptionMap optionMap, URI destinationURI) {
      SSLEngine sslEngine = createSSLEngine(this.sslContext, optionMap, this.getPeerAddress(destinationURI), true);
      SSLParameters sslParameters = sslEngine.getSSLParameters();
      if (sslParameters.getServerNames() == null || sslParameters.getServerNames().isEmpty()) {
         sslParameters.setServerNames(Collections.singletonList(new SNIHostName(destinationURI.getHost())));
         sslEngine.setSSLParameters(sslParameters);
      }

      return new UndertowSslConnection(connection, sslEngine, this.bufferPool, this.delegatedTaskExecutor);
   }

   private InetSocketAddress getPeerAddress(URI destinationURI) {
      String hostname = destinationURI.getHost();
      int port = destinationURI.getPort();
      if (port == -1) {
         port = destinationURI.getScheme().equals("wss") ? 443 : 80;
      }

      return new InetSocketAddress(hostname, port);
   }

   private static SSLEngine createSSLEngine(SSLContext sslContext, OptionMap optionMap, InetSocketAddress peerAddress, boolean client) {
      SSLEngine engine = sslContext.createSSLEngine((String)optionMap.get(Options.SSL_PEER_HOST_NAME, peerAddress.getHostString()), optionMap.get(Options.SSL_PEER_PORT, peerAddress.getPort()));
      engine.setUseClientMode(client);
      engine.setEnableSessionCreation(optionMap.get(Options.SSL_ENABLE_SESSION_CREATION, true));
      Sequence<String> cipherSuites = (Sequence)optionMap.get(Options.SSL_ENABLED_CIPHER_SUITES);
      if (cipherSuites != null) {
         Set<String> supported = new HashSet(Arrays.asList(engine.getSupportedCipherSuites()));
         List<String> finalList = new ArrayList();
         Iterator var8 = cipherSuites.iterator();

         while(var8.hasNext()) {
            String name = (String)var8.next();
            if (supported.contains(name)) {
               finalList.add(name);
            }
         }

         engine.setEnabledCipherSuites((String[])finalList.toArray(new String[finalList.size()]));
      }

      Sequence<String> protocols = (Sequence)optionMap.get(Options.SSL_ENABLED_PROTOCOLS);
      if (protocols != null) {
         Set<String> supported = new HashSet(Arrays.asList(engine.getSupportedProtocols()));
         List<String> finalList = new ArrayList();
         Iterator var18 = protocols.iterator();

         while(var18.hasNext()) {
            String name = (String)var18.next();
            if (supported.contains(name)) {
               finalList.add(name);
            }
         }

         engine.setEnabledProtocols((String[])finalList.toArray(new String[finalList.size()]));
      }

      if (!client) {
         SslClientAuthMode clientAuthMode = (SslClientAuthMode)optionMap.get(Options.SSL_CLIENT_AUTH_MODE);
         if (clientAuthMode != null) {
            switch (clientAuthMode) {
               case NOT_REQUESTED:
                  engine.setNeedClientAuth(false);
                  engine.setWantClientAuth(false);
                  break;
               case REQUESTED:
                  engine.setWantClientAuth(true);
                  break;
               case REQUIRED:
                  engine.setNeedClientAuth(true);
                  break;
               default:
                  throw new IllegalStateException();
            }
         }
      }

      boolean useCipherSuitesOrder = optionMap.get(UndertowOptions.SSL_USER_CIPHER_SUITES_ORDER, false);
      if (useCipherSuitesOrder) {
         SSLParameters sslParameters = engine.getSSLParameters();
         sslParameters.setUseCipherSuitesOrder(true);
         engine.setSSLParameters(sslParameters);
      }

      String endpointIdentificationAlgorithm = (String)optionMap.get(UndertowOptions.ENDPOINT_IDENTIFICATION_ALGORITHM, (Object)null);
      if (endpointIdentificationAlgorithm != null) {
         SSLParameters sslParameters = engine.getSSLParameters();
         sslParameters.setEndpointIdentificationAlgorithm(endpointIdentificationAlgorithm);
         engine.setSSLParameters(sslParameters);
      }

      return engine;
   }

   private IoFuture<SslConnection> setupSslConnection(FutureResult<SslConnection> futureResult, IoFuture<StreamConnection> connection) {
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

   public void updateSSLContext(SSLContext context) {
      this.sslContext = context;
   }

   public AcceptingChannel<SslConnection> createSslConnectionServer(XnioWorker worker, InetSocketAddress bindAddress, ChannelListener<? super AcceptingChannel<SslConnection>> acceptListener, OptionMap optionMap) throws IOException {
      UndertowAcceptingSslChannel server = new UndertowAcceptingSslChannel(this, worker.createStreamConnectionServer(bindAddress, (ChannelListener)null, optionMap), optionMap, this.bufferPool, false);
      if (acceptListener != null) {
         server.getAcceptSetter().set(acceptListener);
      }

      return server;
   }

   private class StreamConnectionChannelListener implements ChannelListener<StreamConnection> {
      private final OptionMap optionMap;
      private final InetSocketAddress destination;
      private final FutureResult<SslConnection> futureResult;
      private final ChannelListener<? super SslConnection> openListener;

      StreamConnectionChannelListener(OptionMap optionMap, InetSocketAddress destination, FutureResult<SslConnection> futureResult, ChannelListener<? super SslConnection> openListener) {
         this.optionMap = optionMap;
         this.destination = destination;
         this.futureResult = futureResult;
         this.openListener = openListener;
      }

      public void handleEvent(StreamConnection connection) {
         try {
            SSLEngine sslEngine = JsseSslUtils.createSSLEngine(UndertowXnioSsl.this.sslContext, this.optionMap, this.destination);
            SSLParameters params = sslEngine.getSSLParameters();
            InetAddress address = this.destination.getAddress();
            String hostnameValue = this.destination.getHostString();
            if (address instanceof Inet6Address && hostnameValue.contains(":")) {
               hostnameValue = address.getHostName();
            }

            params.setServerNames(Collections.singletonList(new SNIHostName(hostnameValue)));
            String endpointIdentificationAlgorithm = (String)this.optionMap.get(UndertowOptions.ENDPOINT_IDENTIFICATION_ALGORITHM, (Object)null);
            if (endpointIdentificationAlgorithm != null) {
               params.setEndpointIdentificationAlgorithm(endpointIdentificationAlgorithm);
            }

            sslEngine.setSSLParameters(params);
            SslConnection wrappedConnection = new UndertowSslConnection(connection, sslEngine, UndertowXnioSsl.this.bufferPool, UndertowXnioSsl.this.delegatedTaskExecutor);
            if (!this.futureResult.setResult(wrappedConnection)) {
               IoUtils.safeClose((Closeable)connection);
            } else {
               ChannelListeners.invokeChannelListener(wrappedConnection, this.openListener);
            }
         } catch (Throwable var8) {
            this.futureResult.setException(new IOException(var8));
         }

      }
   }
}
