package io.undertow.client.http2;

import io.undertow.UndertowLogger;
import io.undertow.UndertowMessages;
import io.undertow.UndertowOptions;
import io.undertow.client.ALPNClientSelector;
import io.undertow.client.ClientCallback;
import io.undertow.client.ClientConnection;
import io.undertow.client.ClientProvider;
import io.undertow.client.ClientStatistics;
import io.undertow.conduits.ByteActivityCallback;
import io.undertow.conduits.BytesReceivedStreamSourceConduit;
import io.undertow.conduits.BytesSentStreamSinkConduit;
import io.undertow.connector.ByteBufferPool;
import io.undertow.connector.PooledByteBuffer;
import io.undertow.protocols.http2.Http2Channel;
import java.io.Closeable;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.xnio.ChannelListener;
import org.xnio.IoFuture;
import org.xnio.IoUtils;
import org.xnio.OptionMap;
import org.xnio.Options;
import org.xnio.StreamConnection;
import org.xnio.XnioIoThread;
import org.xnio.XnioWorker;
import org.xnio.ssl.SslConnection;
import org.xnio.ssl.XnioSsl;

public class Http2ClientProvider implements ClientProvider {
   private static final String HTTP2 = "h2";
   private static final String HTTP_1_1 = "http/1.1";
   private static final ChannelListener<SslConnection> FAILED = new ChannelListener<SslConnection>() {
      public void handleEvent(SslConnection connection) {
         UndertowLogger.ROOT_LOGGER.alpnConnectionFailed(connection);
         IoUtils.safeClose((Closeable)connection);
      }
   };

   public void connect(ClientCallback<ClientConnection> listener, URI uri, XnioWorker worker, XnioSsl ssl, ByteBufferPool bufferPool, OptionMap options) {
      this.connect(listener, (InetSocketAddress)null, uri, (XnioWorker)worker, ssl, bufferPool, options);
   }

   public void connect(ClientCallback<ClientConnection> listener, URI uri, XnioIoThread ioThread, XnioSsl ssl, ByteBufferPool bufferPool, OptionMap options) {
      this.connect(listener, (InetSocketAddress)null, uri, (XnioIoThread)ioThread, ssl, bufferPool, options);
   }

   public Set<String> handlesSchemes() {
      return new HashSet(Arrays.asList("h2"));
   }

   public void connect(ClientCallback<ClientConnection> listener, InetSocketAddress bindAddress, URI uri, XnioWorker worker, XnioSsl ssl, ByteBufferPool bufferPool, OptionMap options) {
      if (ssl == null) {
         listener.failed(UndertowMessages.MESSAGES.sslWasNull());
      } else {
         OptionMap tlsOptions = OptionMap.builder().addAll(options).set(Options.SSL_STARTTLS, true).getMap();
         if (bindAddress == null) {
            ssl.openSslConnection(worker, new InetSocketAddress(uri.getHost(), uri.getPort() == -1 ? 443 : uri.getPort()), this.createOpenListener(listener, uri, ssl, bufferPool, tlsOptions), tlsOptions).addNotifier(this.createNotifier(listener), (Object)null);
         } else {
            ssl.openSslConnection(worker, bindAddress, new InetSocketAddress(uri.getHost(), uri.getPort() == -1 ? 443 : uri.getPort()), this.createOpenListener(listener, uri, ssl, bufferPool, tlsOptions), tlsOptions).addNotifier(this.createNotifier(listener), (Object)null);
         }

      }
   }

   public void connect(ClientCallback<ClientConnection> listener, InetSocketAddress bindAddress, URI uri, XnioIoThread ioThread, XnioSsl ssl, ByteBufferPool bufferPool, OptionMap options) {
      if (ssl == null) {
         listener.failed(UndertowMessages.MESSAGES.sslWasNull());
      } else {
         if (bindAddress == null) {
            OptionMap tlsOptions = OptionMap.builder().addAll(options).set(Options.SSL_STARTTLS, true).getMap();
            ssl.openSslConnection(ioThread, new InetSocketAddress(uri.getHost(), uri.getPort() == -1 ? 443 : uri.getPort()), this.createOpenListener(listener, uri, ssl, bufferPool, tlsOptions), options).addNotifier(this.createNotifier(listener), (Object)null);
         } else {
            ssl.openSslConnection(ioThread, bindAddress, new InetSocketAddress(uri.getHost(), uri.getPort() == -1 ? 443 : uri.getPort()), this.createOpenListener(listener, uri, ssl, bufferPool, options), options).addNotifier(this.createNotifier(listener), (Object)null);
         }

      }
   }

   private IoFuture.Notifier<StreamConnection, Object> createNotifier(final ClientCallback<ClientConnection> listener) {
      return new IoFuture.Notifier<StreamConnection, Object>() {
         public void notify(IoFuture<? extends StreamConnection> ioFuture, Object o) {
            if (ioFuture.getStatus() == IoFuture.Status.FAILED) {
               listener.failed(ioFuture.getException());
            }

         }
      };
   }

   private ChannelListener<StreamConnection> createOpenListener(final ClientCallback<ClientConnection> listener, final URI uri, XnioSsl ssl, final ByteBufferPool bufferPool, final OptionMap options) {
      return new ChannelListener<StreamConnection>() {
         public void handleEvent(StreamConnection connection) {
            Http2ClientProvider.this.handleConnected(connection, listener, uri, bufferPool, options);
         }
      };
   }

   public static ALPNClientSelector.ALPNProtocol alpnProtocol(final ClientCallback<ClientConnection> listener, final URI uri, final ByteBufferPool bufferPool, final OptionMap options) {
      return new ALPNClientSelector.ALPNProtocol(new ChannelListener<SslConnection>() {
         public void handleEvent(SslConnection connection) {
            listener.completed(Http2ClientProvider.createHttp2Channel(connection, bufferPool, options, uri.getHost()));
         }
      }, "h2");
   }

   private void handleConnected(StreamConnection connection, ClientCallback<ClientConnection> listener, URI uri, ByteBufferPool bufferPool, OptionMap options) {
      ALPNClientSelector.runAlpn((SslConnection)connection, FAILED, listener, alpnProtocol(listener, uri, bufferPool, options));
   }

   private static Http2ClientConnection createHttp2Channel(StreamConnection connection, ByteBufferPool bufferPool, OptionMap options, String defaultHost) {
      final ClientStatisticsImpl clientStatistics;
      if (options.get(UndertowOptions.ENABLE_STATISTICS, false)) {
         clientStatistics = new ClientStatisticsImpl();
         connection.getSinkChannel().setConduit(new BytesSentStreamSinkConduit(connection.getSinkChannel().getConduit(), new ByteActivityCallback() {
            public void activity(long bytes) {
               clientStatistics.written = clientStatistics.written + bytes;
            }
         }));
         connection.getSourceChannel().setConduit(new BytesReceivedStreamSourceConduit(connection.getSourceChannel().getConduit(), new ByteActivityCallback() {
            public void activity(long bytes) {
               clientStatistics.read = clientStatistics.read + bytes;
            }
         }));
      } else {
         clientStatistics = null;
      }

      Http2Channel http2Channel = new Http2Channel(connection, (String)null, bufferPool, (PooledByteBuffer)null, true, false, options);
      return new Http2ClientConnection(http2Channel, false, defaultHost, clientStatistics, true);
   }

   private static class ClientStatisticsImpl implements ClientStatistics {
      private long requestCount;
      private long read;
      private long written;

      private ClientStatisticsImpl() {
      }

      public long getRequests() {
         return this.requestCount;
      }

      public long getRead() {
         return this.read;
      }

      public long getWritten() {
         return this.written;
      }

      public void reset() {
         this.read = 0L;
         this.written = 0L;
         this.requestCount = 0L;
      }

      // $FF: synthetic method
      ClientStatisticsImpl(Object x0) {
         this();
      }
   }
}
