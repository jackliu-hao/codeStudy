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
import io.undertow.util.FlexBase64;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.xnio.ChannelListener;
import org.xnio.IoFuture;
import org.xnio.OptionMap;
import org.xnio.StreamConnection;
import org.xnio.XnioIoThread;
import org.xnio.XnioWorker;
import org.xnio.channels.BoundChannel;
import org.xnio.http.HandshakeChecker;
import org.xnio.http.HttpUpgrade;
import org.xnio.ssl.XnioSsl;

public class Http2ClearClientProvider implements ClientProvider {
   public void connect(ClientCallback<ClientConnection> listener, URI uri, XnioWorker worker, XnioSsl ssl, ByteBufferPool bufferPool, OptionMap options) {
      this.connect(listener, (InetSocketAddress)null, uri, (XnioWorker)worker, ssl, bufferPool, options);
   }

   public void connect(ClientCallback<ClientConnection> listener, URI uri, XnioIoThread ioThread, XnioSsl ssl, ByteBufferPool bufferPool, OptionMap options) {
      this.connect(listener, (InetSocketAddress)null, uri, (XnioIoThread)ioThread, ssl, bufferPool, options);
   }

   public Set<String> handlesSchemes() {
      return new HashSet(Arrays.asList("h2c"));
   }

   public void connect(ClientCallback<ClientConnection> listener, InetSocketAddress bindAddress, URI uri, XnioWorker worker, XnioSsl ssl, ByteBufferPool bufferPool, OptionMap options) {
      URI upgradeUri;
      try {
         upgradeUri = new URI("http", uri.getUserInfo(), uri.getHost(), uri.getPort(), uri.getPath(), uri.getQuery(), uri.getFragment());
      } catch (URISyntaxException var10) {
         listener.failed(new IOException(var10));
         return;
      }

      Map<String, String> headers = this.createHeaders(options, bufferPool, uri);
      HttpUpgrade.performUpgrade(worker, bindAddress, upgradeUri, headers, new Http2ClearOpenListener(bufferPool, options, listener, uri.getHost()), (ChannelListener)null, options, (HandshakeChecker)null).addNotifier(new FailedNotifier(listener), (Object)null);
   }

   public void connect(final ClientCallback<ClientConnection> listener, InetSocketAddress bindAddress, final URI uri, XnioIoThread ioThread, XnioSsl ssl, final ByteBufferPool bufferPool, final OptionMap options) {
      final URI upgradeUri;
      try {
         upgradeUri = new URI("http", uri.getUserInfo(), uri.getHost(), uri.getPort(), uri.getPath(), uri.getQuery(), uri.getFragment());
      } catch (URISyntaxException var10) {
         listener.failed(new IOException(var10));
         return;
      }

      if (bindAddress != null) {
         ioThread.openStreamConnection(bindAddress, new InetSocketAddress(uri.getHost(), uri.getPort()), new ChannelListener<StreamConnection>() {
            public void handleEvent(StreamConnection channel) {
               Map<String, String> headers = Http2ClearClientProvider.this.createHeaders(options, bufferPool, uri);
               HttpUpgrade.performUpgrade(channel, upgradeUri, headers, new Http2ClearOpenListener(bufferPool, options, listener, uri.getHost()), (HandshakeChecker)null).addNotifier(new FailedNotifier(listener), (Object)null);
            }
         }, new ChannelListener<BoundChannel>() {
            public void handleEvent(BoundChannel channel) {
            }
         }, options).addNotifier(new FailedNotifier(listener), (Object)null);
      } else {
         ioThread.openStreamConnection(new InetSocketAddress(uri.getHost(), uri.getPort()), new ChannelListener<StreamConnection>() {
            public void handleEvent(StreamConnection channel) {
               Map<String, String> headers = Http2ClearClientProvider.this.createHeaders(options, bufferPool, uri);
               HttpUpgrade.performUpgrade(channel, upgradeUri, headers, new Http2ClearOpenListener(bufferPool, options, listener, uri.getHost()), (HandshakeChecker)null).addNotifier(new FailedNotifier(listener), (Object)null);
            }
         }, new ChannelListener<BoundChannel>() {
            public void handleEvent(BoundChannel channel) {
            }
         }, options).addNotifier(new FailedNotifier(listener), (Object)null);
      }

   }

   private Map<String, String> createHeaders(OptionMap options, ByteBufferPool bufferPool, URI uri) {
      Map<String, String> headers = new HashMap();
      headers.put("HTTP2-Settings", createSettingsFrame(options, bufferPool));
      headers.put("Upgrade", "h2c");
      headers.put("Connection", "Upgrade, HTTP2-Settings");
      headers.put("Host", uri.getHost());
      headers.put("X-HTTP2-connect-only", "connect");
      return headers;
   }

   public static String createSettingsFrame(OptionMap options, ByteBufferPool bufferPool) {
      PooledByteBuffer b = bufferPool.allocate();

      String var4;
      try {
         ByteBuffer currentBuffer = b.getBuffer();
         if (options.contains(UndertowOptions.HTTP2_SETTINGS_HEADER_TABLE_SIZE)) {
            pushOption(currentBuffer, 1, (Integer)options.get(UndertowOptions.HTTP2_SETTINGS_HEADER_TABLE_SIZE));
         }

         if (options.contains(UndertowOptions.HTTP2_SETTINGS_ENABLE_PUSH)) {
            pushOption(currentBuffer, 2, (Boolean)options.get(UndertowOptions.HTTP2_SETTINGS_ENABLE_PUSH) ? 1 : 0);
         }

         if (options.contains(UndertowOptions.HTTP2_SETTINGS_MAX_CONCURRENT_STREAMS)) {
            pushOption(currentBuffer, 3, (Integer)options.get(UndertowOptions.HTTP2_SETTINGS_MAX_CONCURRENT_STREAMS));
         }

         if (options.contains(UndertowOptions.HTTP2_SETTINGS_INITIAL_WINDOW_SIZE)) {
            pushOption(currentBuffer, 4, (Integer)options.get(UndertowOptions.HTTP2_SETTINGS_INITIAL_WINDOW_SIZE));
         }

         if (options.contains(UndertowOptions.HTTP2_SETTINGS_MAX_FRAME_SIZE)) {
            pushOption(currentBuffer, 5, (Integer)options.get(UndertowOptions.HTTP2_SETTINGS_MAX_FRAME_SIZE));
         }

         if (options.contains(UndertowOptions.HTTP2_SETTINGS_MAX_HEADER_LIST_SIZE)) {
            pushOption(currentBuffer, 6, (Integer)options.get(UndertowOptions.HTTP2_SETTINGS_MAX_HEADER_LIST_SIZE));
         } else if (options.contains(UndertowOptions.MAX_HEADER_SIZE)) {
            pushOption(currentBuffer, 6, (Integer)options.get(UndertowOptions.HTTP2_SETTINGS_MAX_HEADER_LIST_SIZE));
         }

         currentBuffer.flip();
         var4 = FlexBase64.encodeStringURL(currentBuffer, false);
      } finally {
         b.close();
      }

      return var4;
   }

   private static void pushOption(ByteBuffer currentBuffer, int id, int value) {
      currentBuffer.put((byte)(id >> 8 & 255));
      currentBuffer.put((byte)(id & 255));
      currentBuffer.put((byte)(value >> 24 & 255));
      currentBuffer.put((byte)(value >> 16 & 255));
      currentBuffer.put((byte)(value >> 8 & 255));
      currentBuffer.put((byte)(value & 255));
   }

   private static class ClientStatisticsImpl implements ClientStatistics {
      private long requestCount;
      private long read;
      private long written;

      private ClientStatisticsImpl() {
      }

      public long getRequestCount() {
         return this.requestCount;
      }

      public void setRequestCount(long requestCount) {
         this.requestCount = requestCount;
      }

      public void setRead(long read) {
         this.read = read;
      }

      public void setWritten(long written) {
         this.written = written;
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

   private static class FailedNotifier implements IoFuture.Notifier<StreamConnection, Object> {
      private final ClientCallback<ClientConnection> listener;

      FailedNotifier(ClientCallback<ClientConnection> listener) {
         this.listener = listener;
      }

      public void notify(IoFuture<? extends StreamConnection> ioFuture, Object attachment) {
         if (ioFuture.getStatus() == IoFuture.Status.FAILED) {
            this.listener.failed(ioFuture.getException());
         }

      }
   }

   private static class Http2ClearOpenListener implements ChannelListener<StreamConnection> {
      private final ByteBufferPool bufferPool;
      private final OptionMap options;
      private final ClientCallback<ClientConnection> listener;
      private final String defaultHost;

      Http2ClearOpenListener(ByteBufferPool bufferPool, OptionMap options, ClientCallback<ClientConnection> listener, String defaultHost) {
         this.bufferPool = bufferPool;
         this.options = options;
         this.listener = listener;
         this.defaultHost = defaultHost;
      }

      public void handleEvent(StreamConnection channel) {
         final ClientStatisticsImpl clientStatistics;
         if (this.options.get(UndertowOptions.ENABLE_STATISTICS, false)) {
            clientStatistics = new ClientStatisticsImpl();
            channel.getSinkChannel().setConduit(new BytesSentStreamSinkConduit(channel.getSinkChannel().getConduit(), new ByteActivityCallback() {
               public void activity(long bytes) {
                  clientStatistics.written = clientStatistics.written + bytes;
               }
            }));
            channel.getSourceChannel().setConduit(new BytesReceivedStreamSourceConduit(channel.getSourceChannel().getConduit(), new ByteActivityCallback() {
               public void activity(long bytes) {
                  clientStatistics.read = clientStatistics.read + bytes;
               }
            }));
         } else {
            clientStatistics = null;
         }

         Http2Channel http2Channel = new Http2Channel(channel, (String)null, this.bufferPool, (PooledByteBuffer)null, true, true, this.options);
         Http2ClientConnection http2ClientConnection = new Http2ClientConnection(http2Channel, true, this.defaultHost, clientStatistics, false);
         this.listener.completed(http2ClientConnection);
      }
   }
}
