package io.undertow.server.protocol.http2;

import io.undertow.UndertowLogger;
import io.undertow.UndertowMessages;
import io.undertow.UndertowOptions;
import io.undertow.conduits.BytesReceivedStreamSourceConduit;
import io.undertow.conduits.BytesSentStreamSinkConduit;
import io.undertow.connector.ByteBufferPool;
import io.undertow.connector.PooledByteBuffer;
import io.undertow.protocols.http2.Http2Channel;
import io.undertow.server.ConnectorStatistics;
import io.undertow.server.ConnectorStatisticsImpl;
import io.undertow.server.DelegateOpenListener;
import io.undertow.server.HttpHandler;
import io.undertow.server.XnioByteBufferPool;
import java.io.Closeable;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.xnio.ChannelListener;
import org.xnio.IoUtils;
import org.xnio.OptionMap;
import org.xnio.Pool;
import org.xnio.StreamConnection;

public final class Http2OpenListener implements ChannelListener<StreamConnection>, DelegateOpenListener {
   private final Set<Http2Channel> connections;
   public static final String HTTP2 = "h2";
   /** @deprecated */
   @Deprecated
   public static final String HTTP2_14 = "h2-14";
   private final ByteBufferPool bufferPool;
   private final int bufferSize;
   private final ChannelListener<Http2Channel> closeTask;
   private volatile HttpHandler rootHandler;
   private volatile OptionMap undertowOptions;
   private volatile boolean statisticsEnabled;
   private final ConnectorStatisticsImpl connectorStatistics;
   private final String protocol;

   /** @deprecated */
   @Deprecated
   public Http2OpenListener(Pool<ByteBuffer> pool) {
      this(pool, OptionMap.EMPTY);
   }

   /** @deprecated */
   @Deprecated
   public Http2OpenListener(Pool<ByteBuffer> pool, OptionMap undertowOptions) {
      this(pool, undertowOptions, "h2");
   }

   /** @deprecated */
   @Deprecated
   public Http2OpenListener(Pool<ByteBuffer> pool, OptionMap undertowOptions, String protocol) {
      this((ByteBufferPool)(new XnioByteBufferPool(pool)), undertowOptions, protocol);
   }

   public Http2OpenListener(ByteBufferPool pool) {
      this(pool, OptionMap.EMPTY);
   }

   public Http2OpenListener(ByteBufferPool pool, OptionMap undertowOptions) {
      this(pool, undertowOptions, "h2");
   }

   public Http2OpenListener(ByteBufferPool pool, OptionMap undertowOptions, String protocol) {
      this.connections = Collections.newSetFromMap(new ConcurrentHashMap());
      this.closeTask = new ChannelListener<Http2Channel>() {
         public void handleEvent(Http2Channel channel) {
            Http2OpenListener.this.connectorStatistics.decrementConnectionCount();
         }
      };
      this.undertowOptions = undertowOptions;
      this.bufferPool = pool;
      PooledByteBuffer buf = pool.allocate();
      this.bufferSize = buf.getBuffer().remaining();
      buf.close();
      this.connectorStatistics = new ConnectorStatisticsImpl();
      this.statisticsEnabled = undertowOptions.get(UndertowOptions.ENABLE_STATISTICS, false);
      this.protocol = protocol;
   }

   public void handleEvent(StreamConnection channel, PooledByteBuffer buffer) {
      if (UndertowLogger.REQUEST_LOGGER.isTraceEnabled()) {
         UndertowLogger.REQUEST_LOGGER.tracef("Opened HTTP/2 connection with %s", channel.getPeerAddress());
      }

      Http2Channel http2Channel = new Http2Channel(channel, this.protocol, this.bufferPool, buffer, false, false, this.undertowOptions);
      Integer idleTimeout = (Integer)this.undertowOptions.get(UndertowOptions.IDLE_TIMEOUT);
      if (idleTimeout != null && idleTimeout > 0) {
         http2Channel.setIdleTimeout((long)idleTimeout);
      }

      if (this.statisticsEnabled) {
         channel.getSinkChannel().setConduit(new BytesSentStreamSinkConduit(channel.getSinkChannel().getConduit(), this.connectorStatistics.sentAccumulator()));
         channel.getSourceChannel().setConduit(new BytesReceivedStreamSourceConduit(channel.getSourceChannel().getConduit(), this.connectorStatistics.receivedAccumulator()));
         this.connectorStatistics.incrementConnectionCount();
         http2Channel.addCloseTask(this.closeTask);
      }

      this.connections.add(http2Channel);
      http2Channel.addCloseTask(new ChannelListener<Http2Channel>() {
         public void handleEvent(Http2Channel channel) {
            Http2OpenListener.this.connections.remove(channel);
         }
      });
      http2Channel.getReceiveSetter().set(new Http2ReceiveListener(this.rootHandler, this.getUndertowOptions(), this.bufferSize, this.connectorStatistics));
      http2Channel.resumeReceives();
   }

   public ConnectorStatistics getConnectorStatistics() {
      return this.statisticsEnabled ? this.connectorStatistics : null;
   }

   public void closeConnections() {
      Iterator var1 = this.connections.iterator();

      while(var1.hasNext()) {
         Http2Channel i = (Http2Channel)var1.next();
         IoUtils.safeClose((Closeable)i);
      }

   }

   public HttpHandler getRootHandler() {
      return this.rootHandler;
   }

   public void setRootHandler(HttpHandler rootHandler) {
      this.rootHandler = rootHandler;
   }

   public OptionMap getUndertowOptions() {
      return this.undertowOptions;
   }

   public void setUndertowOptions(OptionMap undertowOptions) {
      if (undertowOptions == null) {
         throw UndertowMessages.MESSAGES.argumentCannotBeNull("undertowOptions");
      } else {
         this.undertowOptions = undertowOptions;
         this.statisticsEnabled = undertowOptions.get(UndertowOptions.ENABLE_CONNECTOR_STATISTICS, false);
      }
   }

   public ByteBufferPool getBufferPool() {
      return this.bufferPool;
   }

   public void handleEvent(StreamConnection channel) {
      this.handleEvent(channel, (PooledByteBuffer)null);
   }
}
