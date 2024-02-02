package io.undertow.client.http2;

import io.undertow.UndertowOptions;
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
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.xnio.ChannelListener;
import org.xnio.IoFuture;
import org.xnio.OptionMap;
import org.xnio.StreamConnection;
import org.xnio.XnioIoThread;
import org.xnio.XnioWorker;
import org.xnio.conduits.ConduitStreamSinkChannel;
import org.xnio.ssl.XnioSsl;

public class Http2PriorKnowledgeClientProvider implements ClientProvider {
   private static final byte[] PRI_REQUEST = new byte[]{80, 82, 73, 32, 42, 32, 72, 84, 84, 80, 47, 50, 46, 48, 13, 10, 13, 10, 83, 77, 13, 10, 13, 10};

   public void connect(ClientCallback<ClientConnection> listener, URI uri, XnioWorker worker, XnioSsl ssl, ByteBufferPool bufferPool, OptionMap options) {
      this.connect(listener, (InetSocketAddress)null, uri, (XnioWorker)worker, ssl, bufferPool, options);
   }

   public void connect(ClientCallback<ClientConnection> listener, URI uri, XnioIoThread ioThread, XnioSsl ssl, ByteBufferPool bufferPool, OptionMap options) {
      this.connect(listener, (InetSocketAddress)null, uri, (XnioIoThread)ioThread, ssl, bufferPool, options);
   }

   public Set<String> handlesSchemes() {
      return new HashSet(Arrays.asList("h2c-prior"));
   }

   public void connect(ClientCallback<ClientConnection> listener, InetSocketAddress bindAddress, URI uri, XnioWorker worker, XnioSsl ssl, ByteBufferPool bufferPool, OptionMap options) {
      if (bindAddress == null) {
         worker.openStreamConnection(new InetSocketAddress(uri.getHost(), uri.getPort() == -1 ? 80 : uri.getPort()), this.createOpenListener(listener, bufferPool, options, uri.getHost()), options).addNotifier(this.createNotifier(listener), (Object)null);
      } else {
         worker.openStreamConnection(bindAddress, new InetSocketAddress(uri.getHost(), uri.getPort() == -1 ? 80 : uri.getPort()), this.createOpenListener(listener, bufferPool, options, uri.getHost()), (ChannelListener)null, options).addNotifier(this.createNotifier(listener), (Object)null);
      }

   }

   public void connect(ClientCallback<ClientConnection> listener, InetSocketAddress bindAddress, URI uri, XnioIoThread ioThread, XnioSsl ssl, ByteBufferPool bufferPool, OptionMap options) {
      if (bindAddress == null) {
         ioThread.openStreamConnection(new InetSocketAddress(uri.getHost(), uri.getPort() == -1 ? 80 : uri.getPort()), this.createOpenListener(listener, bufferPool, options, uri.getHost()), options).addNotifier(this.createNotifier(listener), (Object)null);
      } else {
         ioThread.openStreamConnection(bindAddress, new InetSocketAddress(uri.getHost(), uri.getPort() == -1 ? 80 : uri.getPort()), this.createOpenListener(listener, bufferPool, options, uri.getHost()), (ChannelListener)null, options).addNotifier(this.createNotifier(listener), (Object)null);
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

   private ChannelListener<StreamConnection> createOpenListener(final ClientCallback<ClientConnection> listener, final ByteBufferPool bufferPool, final OptionMap options, final String defaultHost) {
      return new ChannelListener<StreamConnection>() {
         public void handleEvent(StreamConnection connection) {
            Http2PriorKnowledgeClientProvider.this.handleConnected(connection, listener, bufferPool, options, defaultHost);
         }
      };
   }

   private void handleConnected(final StreamConnection connection, final ClientCallback<ClientConnection> listener, final ByteBufferPool bufferPool, final OptionMap options, final String defaultHost) {
      try {
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

         final ByteBuffer pri = ByteBuffer.wrap(PRI_REQUEST);
         pri.flip();
         ConduitStreamSinkChannel sink = connection.getSinkChannel();
         sink.write(pri);
         if (pri.hasRemaining()) {
            sink.setWriteListener(new ChannelListener<ConduitStreamSinkChannel>() {
               public void handleEvent(ConduitStreamSinkChannel channel) {
                  try {
                     channel.write(pri);
                     if (pri.hasRemaining()) {
                        return;
                     }

                     listener.completed(new Http2ClientConnection(new Http2Channel(connection, (String)null, bufferPool, (PooledByteBuffer)null, true, false, options), false, defaultHost, clientStatistics, false));
                  } catch (Throwable var4) {
                     IOException e = var4 instanceof IOException ? (IOException)var4 : new IOException(var4);
                     listener.failed(e);
                  }

               }
            });
            return;
         }

         listener.completed(new Http2ClientConnection(new Http2Channel(connection, (String)null, bufferPool, (PooledByteBuffer)null, true, false, options), false, defaultHost, clientStatistics, false));
      } catch (Throwable var9) {
         IOException e = var9 instanceof IOException ? (IOException)var9 : new IOException(var9);
         listener.failed(e);
      }

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
