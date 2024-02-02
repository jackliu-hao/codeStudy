package io.undertow.server.protocol.http;

import io.undertow.UndertowLogger;
import io.undertow.UndertowMessages;
import io.undertow.UndertowOptions;
import io.undertow.conduits.BytesReceivedStreamSourceConduit;
import io.undertow.conduits.BytesSentStreamSinkConduit;
import io.undertow.conduits.IdleTimeoutConduit;
import io.undertow.conduits.ReadTimeoutStreamSourceConduit;
import io.undertow.conduits.WriteTimeoutStreamSinkConduit;
import io.undertow.connector.ByteBufferPool;
import io.undertow.connector.PooledByteBuffer;
import io.undertow.server.ConnectorStatistics;
import io.undertow.server.ConnectorStatisticsImpl;
import io.undertow.server.DelegateOpenListener;
import io.undertow.server.HttpHandler;
import io.undertow.server.ServerConnection;
import io.undertow.server.XnioByteBufferPool;
import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.xnio.ChannelListener;
import org.xnio.IoUtils;
import org.xnio.OptionMap;
import org.xnio.Options;
import org.xnio.Pool;
import org.xnio.StreamConnection;

public final class HttpOpenListener implements ChannelListener<StreamConnection>, DelegateOpenListener {
   private final Set<HttpServerConnection> connections;
   private final ByteBufferPool bufferPool;
   private final int bufferSize;
   private volatile HttpHandler rootHandler;
   private volatile OptionMap undertowOptions;
   private volatile HttpRequestParser parser;
   private volatile boolean statisticsEnabled;
   private final ConnectorStatisticsImpl connectorStatistics;

   /** @deprecated */
   @Deprecated
   public HttpOpenListener(Pool<ByteBuffer> pool) {
      this(pool, OptionMap.EMPTY);
   }

   /** @deprecated */
   @Deprecated
   public HttpOpenListener(Pool<ByteBuffer> pool, OptionMap undertowOptions) {
      this((ByteBufferPool)(new XnioByteBufferPool(pool)), undertowOptions);
   }

   public HttpOpenListener(ByteBufferPool pool) {
      this(pool, OptionMap.EMPTY);
   }

   public HttpOpenListener(ByteBufferPool pool, OptionMap undertowOptions) {
      this.connections = Collections.newSetFromMap(new ConcurrentHashMap());
      this.undertowOptions = undertowOptions;
      this.bufferPool = pool;
      PooledByteBuffer buf = pool.allocate();
      this.bufferSize = buf.getBuffer().remaining();
      buf.close();
      this.parser = HttpRequestParser.instance(undertowOptions);
      this.connectorStatistics = new ConnectorStatisticsImpl();
      this.statisticsEnabled = undertowOptions.get(UndertowOptions.ENABLE_CONNECTOR_STATISTICS, false);
   }

   public void handleEvent(StreamConnection channel) {
      this.handleEvent(channel, (PooledByteBuffer)null);
   }

   public void handleEvent(StreamConnection channel, PooledByteBuffer buffer) {
      if (UndertowLogger.REQUEST_LOGGER.isTraceEnabled()) {
         UndertowLogger.REQUEST_LOGGER.tracef("Opened connection with %s", channel.getPeerAddress());
      }

      try {
         Integer readTimeout = (Integer)channel.getOption(Options.READ_TIMEOUT);
         Integer idle = (Integer)this.undertowOptions.get(UndertowOptions.IDLE_TIMEOUT);
         if (idle != null) {
            IdleTimeoutConduit conduit = new IdleTimeoutConduit(channel);
            channel.getSourceChannel().setConduit(conduit);
            channel.getSinkChannel().setConduit(conduit);
         }

         if (readTimeout != null && readTimeout > 0) {
            channel.getSourceChannel().setConduit(new ReadTimeoutStreamSourceConduit(channel.getSourceChannel().getConduit(), channel, this));
         }

         Integer writeTimeout = (Integer)channel.getOption(Options.WRITE_TIMEOUT);
         if (writeTimeout != null && writeTimeout > 0) {
            channel.getSinkChannel().setConduit(new WriteTimeoutStreamSinkConduit(channel.getSinkChannel().getConduit(), channel, this));
         }
      } catch (IOException var6) {
         IoUtils.safeClose((Closeable)channel);
         UndertowLogger.REQUEST_IO_LOGGER.ioException(var6);
      } catch (Throwable var7) {
         IoUtils.safeClose((Closeable)channel);
         UndertowLogger.REQUEST_IO_LOGGER.handleUnexpectedFailure(var7);
      }

      if (this.statisticsEnabled) {
         channel.getSinkChannel().setConduit(new BytesSentStreamSinkConduit(channel.getSinkChannel().getConduit(), this.connectorStatistics.sentAccumulator()));
         channel.getSourceChannel().setConduit(new BytesReceivedStreamSourceConduit(channel.getSourceChannel().getConduit(), this.connectorStatistics.receivedAccumulator()));
      }

      final HttpServerConnection connection = new HttpServerConnection(channel, this.bufferPool, this.rootHandler, this.undertowOptions, this.bufferSize, this.statisticsEnabled ? this.connectorStatistics : null);
      HttpReadListener readListener = new HttpReadListener(connection, this.parser, this.statisticsEnabled ? this.connectorStatistics : null);
      if (buffer != null) {
         if (buffer.getBuffer().hasRemaining()) {
            connection.setExtraBytes(buffer);
         } else {
            buffer.close();
         }
      }

      if (this.connectorStatistics != null && this.statisticsEnabled) {
         this.connectorStatistics.incrementConnectionCount();
      }

      this.connections.add(connection);
      connection.addCloseListener(new ServerConnection.CloseListener() {
         public void closed(ServerConnection c) {
            HttpOpenListener.this.connections.remove(connection);
         }
      });
      connection.setReadListener(readListener);
      readListener.newRequest();
      channel.getSourceChannel().setReadListener(readListener);
      readListener.handleEvent(channel.getSourceChannel());
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
         this.parser = HttpRequestParser.instance(undertowOptions);
         this.statisticsEnabled = undertowOptions.get(UndertowOptions.ENABLE_CONNECTOR_STATISTICS, false);
      }
   }

   public ByteBufferPool getBufferPool() {
      return this.bufferPool;
   }

   public ConnectorStatistics getConnectorStatistics() {
      return this.statisticsEnabled ? this.connectorStatistics : null;
   }

   public void closeConnections() {
      Iterator var1 = this.connections.iterator();

      while(var1.hasNext()) {
         final HttpServerConnection i = (HttpServerConnection)var1.next();
         i.getIoThread().execute(new Runnable() {
            public void run() {
               IoUtils.safeClose((Closeable)i);
            }
         });
      }

   }
}
