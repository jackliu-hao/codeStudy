package io.undertow.client.ajp;

import io.undertow.UndertowOptions;
import io.undertow.client.ClientCallback;
import io.undertow.client.ClientConnection;
import io.undertow.client.ClientProvider;
import io.undertow.client.ClientStatistics;
import io.undertow.conduits.ByteActivityCallback;
import io.undertow.conduits.BytesReceivedStreamSourceConduit;
import io.undertow.conduits.BytesSentStreamSinkConduit;
import io.undertow.connector.ByteBufferPool;
import io.undertow.protocols.ajp.AjpClientChannel;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.xnio.ChannelListener;
import org.xnio.IoFuture;
import org.xnio.OptionMap;
import org.xnio.StreamConnection;
import org.xnio.XnioIoThread;
import org.xnio.XnioWorker;
import org.xnio.ssl.XnioSsl;

public class AjpClientProvider implements ClientProvider {
   public Set<String> handlesSchemes() {
      return new HashSet(Arrays.asList("ajp"));
   }

   public void connect(ClientCallback<ClientConnection> listener, URI uri, XnioWorker worker, XnioSsl ssl, ByteBufferPool bufferPool, OptionMap options) {
      this.connect(listener, (InetSocketAddress)null, uri, (XnioWorker)worker, ssl, bufferPool, options);
   }

   public void connect(ClientCallback<ClientConnection> listener, URI uri, XnioIoThread ioThread, XnioSsl ssl, ByteBufferPool bufferPool, OptionMap options) {
      this.connect(listener, (InetSocketAddress)null, uri, (XnioIoThread)ioThread, ssl, bufferPool, options);
   }

   public void connect(final ClientCallback<ClientConnection> listener, InetSocketAddress bindAddress, final URI uri, XnioWorker worker, final XnioSsl ssl, final ByteBufferPool bufferPool, final OptionMap options) {
      ChannelListener<StreamConnection> openListener = new ChannelListener<StreamConnection>() {
         public void handleEvent(StreamConnection connection) {
            AjpClientProvider.this.handleConnected(connection, listener, uri, ssl, bufferPool, options);
         }
      };
      IoFuture.Notifier<StreamConnection, Object> notifier = new IoFuture.Notifier<StreamConnection, Object>() {
         public void notify(IoFuture<? extends StreamConnection> ioFuture, Object o) {
            if (ioFuture.getStatus() == IoFuture.Status.FAILED) {
               listener.failed(ioFuture.getException());
            }

         }
      };
      if (bindAddress == null) {
         worker.openStreamConnection(new InetSocketAddress(uri.getHost(), uri.getPort() == -1 ? 8009 : uri.getPort()), openListener, options).addNotifier(notifier, (Object)null);
      } else {
         worker.openStreamConnection(bindAddress, new InetSocketAddress(uri.getHost(), uri.getPort() == -1 ? 8009 : uri.getPort()), openListener, (ChannelListener)null, options).addNotifier(notifier, (Object)null);
      }

   }

   public void connect(final ClientCallback<ClientConnection> listener, InetSocketAddress bindAddress, final URI uri, XnioIoThread ioThread, final XnioSsl ssl, final ByteBufferPool bufferPool, final OptionMap options) {
      ChannelListener<StreamConnection> openListener = new ChannelListener<StreamConnection>() {
         public void handleEvent(StreamConnection connection) {
            AjpClientProvider.this.handleConnected(connection, listener, uri, ssl, bufferPool, options);
         }
      };
      IoFuture.Notifier<StreamConnection, Object> notifier = new IoFuture.Notifier<StreamConnection, Object>() {
         public void notify(IoFuture<? extends StreamConnection> ioFuture, Object o) {
            if (ioFuture.getStatus() == IoFuture.Status.FAILED) {
               listener.failed(ioFuture.getException());
            }

         }
      };
      if (bindAddress == null) {
         ioThread.openStreamConnection(new InetSocketAddress(uri.getHost(), uri.getPort() == -1 ? 8009 : uri.getPort()), openListener, options).addNotifier(notifier, (Object)null);
      } else {
         ioThread.openStreamConnection(bindAddress, new InetSocketAddress(uri.getHost(), uri.getPort() == -1 ? 8009 : uri.getPort()), openListener, (ChannelListener)null, options).addNotifier(notifier, (Object)null);
      }

   }

   private void handleConnected(StreamConnection connection, ClientCallback<ClientConnection> listener, URI uri, XnioSsl ssl, ByteBufferPool bufferPool, OptionMap options) {
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

      listener.completed(new AjpClientConnection(new AjpClientChannel(connection, bufferPool, options), options, bufferPool, clientStatistics));
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
