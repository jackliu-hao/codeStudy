package io.undertow.server.protocol.ajp;

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
import io.undertow.server.HttpHandler;
import io.undertow.server.OpenListener;
import io.undertow.server.ServerConnection;
import io.undertow.server.XnioByteBufferPool;
import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.xnio.IoUtils;
import org.xnio.OptionMap;
import org.xnio.Options;
import org.xnio.Pool;
import org.xnio.StreamConnection;
import org.xnio.channels.StreamSourceChannel;

public class AjpOpenListener implements OpenListener {
   private static final String DEFAULT_AJP_ALLOWED_REQUEST_ATTRIBUTES_PATTERN = SecurityActions.getSystemProperty("io.undertow.ajp.allowedRequestAttributesPattern");
   private final Set<AjpServerConnection> connections;
   private final ByteBufferPool bufferPool;
   private final int bufferSize;
   private volatile String scheme;
   private volatile HttpHandler rootHandler;
   private volatile OptionMap undertowOptions;
   private volatile AjpRequestParser parser;
   private volatile boolean statisticsEnabled;
   private final ConnectorStatisticsImpl connectorStatistics;
   private final ServerConnection.CloseListener closeListener;

   public AjpOpenListener(Pool<ByteBuffer> pool) {
      this(pool, OptionMap.EMPTY);
   }

   public AjpOpenListener(Pool<ByteBuffer> pool, OptionMap undertowOptions) {
      this((ByteBufferPool)(new XnioByteBufferPool(pool)), undertowOptions);
   }

   public AjpOpenListener(ByteBufferPool pool) {
      this(pool, OptionMap.EMPTY);
   }

   public AjpOpenListener(ByteBufferPool pool, OptionMap undertowOptions) {
      this.connections = Collections.newSetFromMap(new ConcurrentHashMap());
      this.closeListener = new ServerConnection.CloseListener() {
         public void closed(ServerConnection connection) {
            AjpOpenListener.this.connectorStatistics.decrementConnectionCount();
         }
      };
      this.undertowOptions = undertowOptions;
      this.bufferPool = pool;
      PooledByteBuffer buf = pool.allocate();
      this.bufferSize = buf.getBuffer().remaining();
      buf.close();
      this.parser = new AjpRequestParser((String)undertowOptions.get(UndertowOptions.URL_CHARSET, StandardCharsets.UTF_8.name()), undertowOptions.get(UndertowOptions.DECODE_URL, true), undertowOptions.get(UndertowOptions.MAX_PARAMETERS, 1000), undertowOptions.get(UndertowOptions.MAX_HEADERS, 200), undertowOptions.get(UndertowOptions.ALLOW_ENCODED_SLASH, false), undertowOptions.get(UndertowOptions.ALLOW_UNESCAPED_CHARACTERS_IN_URL, false), (String)undertowOptions.get(UndertowOptions.AJP_ALLOWED_REQUEST_ATTRIBUTES_PATTERN, DEFAULT_AJP_ALLOWED_REQUEST_ATTRIBUTES_PATTERN));
      this.connectorStatistics = new ConnectorStatisticsImpl();
      this.statisticsEnabled = undertowOptions.get(UndertowOptions.ENABLE_CONNECTOR_STATISTICS, false);
   }

   public void handleEvent(StreamConnection channel) {
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
      } catch (IOException var5) {
         IoUtils.safeClose((Closeable)channel);
         UndertowLogger.REQUEST_IO_LOGGER.ioException(var5);
      }

      if (this.statisticsEnabled) {
         channel.getSinkChannel().setConduit(new BytesSentStreamSinkConduit(channel.getSinkChannel().getConduit(), this.connectorStatistics.sentAccumulator()));
         channel.getSourceChannel().setConduit(new BytesReceivedStreamSourceConduit(channel.getSourceChannel().getConduit(), this.connectorStatistics.receivedAccumulator()));
         this.connectorStatistics.incrementConnectionCount();
      }

      final AjpServerConnection connection = new AjpServerConnection(channel, this.bufferPool, this.rootHandler, this.undertowOptions, this.bufferSize);
      AjpReadListener readListener = new AjpReadListener(connection, this.scheme, this.parser, this.statisticsEnabled ? this.connectorStatistics : null);
      if (this.statisticsEnabled) {
         connection.addCloseListener(this.closeListener);
      }

      connection.setAjpReadListener(readListener);
      this.connections.add(connection);
      connection.addCloseListener(new ServerConnection.CloseListener() {
         public void closed(ServerConnection c) {
            AjpOpenListener.this.connections.remove(connection);
         }
      });
      readListener.startRequest();
      channel.getSourceChannel().setReadListener(readListener);
      readListener.handleEvent((StreamSourceChannel)channel.getSourceChannel());
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
         this.parser = new AjpRequestParser((String)undertowOptions.get(UndertowOptions.URL_CHARSET, StandardCharsets.UTF_8.name()), undertowOptions.get(UndertowOptions.DECODE_URL, true), undertowOptions.get(UndertowOptions.MAX_PARAMETERS, 1000), undertowOptions.get(UndertowOptions.MAX_HEADERS, 200), undertowOptions.get(UndertowOptions.ALLOW_ENCODED_SLASH, false), undertowOptions.get(UndertowOptions.ALLOW_UNESCAPED_CHARACTERS_IN_URL, false));
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
         AjpServerConnection i = (AjpServerConnection)var1.next();
         IoUtils.safeClose((Closeable)i);
      }

   }

   public String getScheme() {
      return this.scheme;
   }

   public void setScheme(String scheme) {
      this.scheme = scheme;
   }
}
