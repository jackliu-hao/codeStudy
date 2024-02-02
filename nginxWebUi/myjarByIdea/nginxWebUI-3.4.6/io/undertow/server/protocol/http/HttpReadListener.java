package io.undertow.server.protocol.http;

import io.undertow.UndertowLogger;
import io.undertow.UndertowMessages;
import io.undertow.UndertowOptions;
import io.undertow.conduits.ReadDataStreamSourceConduit;
import io.undertow.connector.PooledByteBuffer;
import io.undertow.protocols.http2.Http2Channel;
import io.undertow.server.ConnectorStatisticsImpl;
import io.undertow.server.Connectors;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.protocol.ParseTimeoutUpdater;
import io.undertow.server.protocol.http2.Http2ReceiveListener;
import io.undertow.util.ClosingChannelExceptionHandler;
import io.undertow.util.ConnectionUtils;
import io.undertow.util.HeaderValues;
import io.undertow.util.Headers;
import io.undertow.util.HttpString;
import io.undertow.util.Methods;
import io.undertow.util.Protocols;
import io.undertow.util.StringWriteChannelListener;
import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import org.xnio.ChannelListener;
import org.xnio.ChannelListeners;
import org.xnio.IoUtils;
import org.xnio.StreamConnection;
import org.xnio.channels.StreamSinkChannel;
import org.xnio.channels.StreamSourceChannel;
import org.xnio.conduits.ConduitStreamSinkChannel;
import org.xnio.conduits.ConduitStreamSourceChannel;

final class HttpReadListener implements ChannelListener<ConduitStreamSourceChannel>, Runnable {
   private static final HttpString PRI = new HttpString("PRI");
   private static final byte[] PRI_EXPECTED = new byte[]{83, 77, 13, 10, 13, 10};
   private static final String BAD_REQUEST = "HTTP/1.1 400 Bad Request\r\nContent-Length: 0\r\nConnection: close\r\n\r\n";
   private final HttpServerConnection connection;
   private final ParseState state;
   private final HttpRequestParser parser;
   private HttpServerExchange httpServerExchange;
   private int read = 0;
   private final int maxRequestSize;
   private final long maxEntitySize;
   private final boolean recordRequestStartTime;
   private final boolean allowUnknownProtocols;
   private final boolean requireHostHeader;
   private volatile int requestState;
   private static final AtomicIntegerFieldUpdater<HttpReadListener> requestStateUpdater = AtomicIntegerFieldUpdater.newUpdater(HttpReadListener.class, "requestState");
   private final ConnectorStatisticsImpl connectorStatistics;
   private ParseTimeoutUpdater parseTimeoutUpdater;

   HttpReadListener(HttpServerConnection connection, HttpRequestParser parser, ConnectorStatisticsImpl connectorStatistics) {
      this.connection = connection;
      this.parser = parser;
      this.connectorStatistics = connectorStatistics;
      this.maxRequestSize = connection.getUndertowOptions().get(UndertowOptions.MAX_HEADER_SIZE, 1048576);
      this.maxEntitySize = connection.getUndertowOptions().get(UndertowOptions.MAX_ENTITY_SIZE, -1L);
      this.recordRequestStartTime = connection.getUndertowOptions().get(UndertowOptions.RECORD_REQUEST_START_TIME, false);
      this.requireHostHeader = connection.getUndertowOptions().get(UndertowOptions.REQUIRE_HOST_HTTP11, true);
      this.allowUnknownProtocols = connection.getUndertowOptions().get(UndertowOptions.ALLOW_UNKNOWN_PROTOCOLS, false);
      int requestParseTimeout = connection.getUndertowOptions().get(UndertowOptions.REQUEST_PARSE_TIMEOUT, -1);
      int requestIdleTimeout = connection.getUndertowOptions().get(UndertowOptions.NO_REQUEST_TIMEOUT, -1);
      if (requestIdleTimeout < 0 && requestParseTimeout < 0) {
         this.parseTimeoutUpdater = null;
      } else {
         this.parseTimeoutUpdater = new ParseTimeoutUpdater(connection, (long)requestParseTimeout, (long)requestIdleTimeout);
         connection.addCloseListener(this.parseTimeoutUpdater);
      }

      this.state = new ParseState(connection.getUndertowOptions().get(UndertowOptions.HTTP_HEADERS_CACHE_SIZE, 15));
   }

   public void newRequest() {
      this.state.reset();
      this.read = 0;
      if (this.parseTimeoutUpdater != null) {
         this.parseTimeoutUpdater.connectionIdle();
      }

      this.connection.setCurrentExchange((HttpServerExchange)null);
   }

   public void handleEvent(ConduitStreamSourceChannel channel) {
      while(true) {
         if (requestStateUpdater.get(this) != 0) {
            if (!requestStateUpdater.compareAndSet(this, 1, 2)) {
               continue;
            }

            try {
               channel.suspendReads();
            } finally {
               requestStateUpdater.set(this, 1);
            }

            return;
         }

         this.handleEventWithNoRunningRequest(channel);
         return;
      }
   }

   public void handleEventWithNoRunningRequest(ConduitStreamSourceChannel channel) {
      PooledByteBuffer existing = this.connection.getExtraBytes();
      if ((existing != null || !this.connection.getOriginalSourceConduit().isReadShutdown()) && !this.connection.getOriginalSinkConduit().isWriteShutdown()) {
         PooledByteBuffer pooled = existing == null ? this.connection.getByteBufferPool().allocate() : existing;
         ByteBuffer buffer = pooled.getBuffer();
         boolean free = true;

         try {
            boolean bytesRead = false;

            do {
               int res;
               if (existing == null) {
                  buffer.clear();

                  try {
                     res = channel.read(buffer);
                  } catch (IOException var14) {
                     UndertowLogger.REQUEST_IO_LOGGER.debug("Error reading request", var14);
                     IoUtils.safeClose((Closeable)this.connection);
                     return;
                  }
               } else {
                  res = buffer.remaining();
               }

               if (res <= 0) {
                  if (bytesRead && this.parseTimeoutUpdater != null) {
                     this.parseTimeoutUpdater.failedParse();
                  }

                  this.handleFailedRead(channel, res);
                  return;
               }

               bytesRead = true;
               if (existing != null) {
                  existing = null;
                  this.connection.setExtraBytes((PooledByteBuffer)null);
               } else {
                  buffer.flip();
               }

               int begin = buffer.remaining();
               if (this.httpServerExchange == null) {
                  this.httpServerExchange = new HttpServerExchange(this.connection, this.maxEntitySize);
               }

               this.parser.handle(buffer, this.state, this.httpServerExchange);
               if (buffer.hasRemaining()) {
                  free = false;
                  this.connection.setExtraBytes(pooled);
               }

               int total = this.read + (begin - buffer.remaining());
               this.read = total;
               if (this.read > this.maxRequestSize) {
                  UndertowLogger.REQUEST_LOGGER.requestHeaderWasTooLarge(this.connection.getPeerAddress(), this.maxRequestSize);
                  this.sendBadRequestAndClose(this.connection.getChannel(), (Throwable)null);
                  return;
               }
            } while(!this.state.isComplete());

            if (this.parseTimeoutUpdater != null) {
               this.parseTimeoutUpdater.requestStarted();
            }

            this.connection.getOriginalSourceConduit().suspendReads();
            HttpServerExchange httpServerExchange = this.httpServerExchange;
            httpServerExchange.setRequestScheme(this.connection.getSslSession() != null ? "https" : "http");
            this.httpServerExchange = null;
            requestStateUpdater.set(this, 1);
            if (this.recordRequestStartTime) {
               Connectors.setRequestStartTime(httpServerExchange);
            }

            if (httpServerExchange.getProtocol() == Protocols.HTTP_2_0) {
               free = this.handleHttp2PriorKnowledge(pooled, httpServerExchange);
            } else {
               if (!this.allowUnknownProtocols) {
                  HttpString protocol = httpServerExchange.getProtocol();
                  if (protocol != Protocols.HTTP_1_1 && protocol != Protocols.HTTP_1_0 && protocol != Protocols.HTTP_0_9) {
                     UndertowLogger.REQUEST_IO_LOGGER.debugf("Closing connection from %s due to unknown protocol %s", this.connection.getChannel().getPeerAddress(), protocol);
                     this.sendBadRequestAndClose(this.connection.getChannel(), new IOException());
                     return;
                  }
               }

               HttpTransferEncoding.setupRequest(httpServerExchange);
               this.connection.setCurrentExchange(httpServerExchange);
               if (this.connectorStatistics != null) {
                  this.connectorStatistics.setup(httpServerExchange);
               }

               if (this.connection.getSslSession() != null) {
                  channel.suspendReads();
               }

               HeaderValues host = httpServerExchange.getRequestHeaders().get(Headers.HOST);
               if (host != null && host.size() > 1) {
                  this.sendBadRequestAndClose(this.connection.getChannel(), UndertowMessages.MESSAGES.moreThanOneHostHeader());
               } else if (!this.requireHostHeader || !httpServerExchange.getProtocol().equals(Protocols.HTTP_1_1) || host != null && host.size() != 0 && !host.getFirst().isEmpty()) {
                  if (!Connectors.areRequestHeadersValid(httpServerExchange.getRequestHeaders())) {
                     this.sendBadRequestAndClose(this.connection.getChannel(), UndertowMessages.MESSAGES.invalidHeaders());
                  } else {
                     Connectors.executeRootHandler(this.connection.getRootHandler(), httpServerExchange);
                  }
               } else {
                  this.sendBadRequestAndClose(this.connection.getChannel(), UndertowMessages.MESSAGES.noHostInHttp11Request());
               }
            }
         } catch (Throwable var15) {
            this.sendBadRequestAndClose(this.connection.getChannel(), var15);
         } finally {
            if (free) {
               pooled.close();
            }

         }
      } else {
         IoUtils.safeClose((Closeable)this.connection);
         channel.suspendReads();
      }
   }

   private boolean handleHttp2PriorKnowledge(PooledByteBuffer pooled, HttpServerExchange httpServerExchange) throws IOException {
      if (httpServerExchange.getRequestMethod().equals(PRI) && this.connection.getUndertowOptions().get(UndertowOptions.ENABLE_HTTP2, false)) {
         this.handleHttp2PriorKnowledge(this.connection.getChannel(), this.connection, pooled);
         return false;
      } else {
         this.sendBadRequestAndClose(this.connection.getChannel(), new IOException());
         return true;
      }
   }

   private void handleFailedRead(ConduitStreamSourceChannel channel, int res) {
      if (res == 0) {
         channel.setReadListener(this);
         channel.resumeReads();
      } else if (res == -1) {
         IoUtils.safeClose((Closeable)this.connection);
      }

   }

   private void sendBadRequestAndClose(final StreamConnection connection, Throwable exception) {
      UndertowLogger.REQUEST_IO_LOGGER.failedToParseRequest(exception);
      connection.getSourceChannel().suspendReads();
      (new StringWriteChannelListener("HTTP/1.1 400 Bad Request\r\nContent-Length: 0\r\nConnection: close\r\n\r\n") {
         protected void writeDone(StreamSinkChannel c) {
            super.writeDone(c);
            c.suspendWrites();
            IoUtils.safeClose((Closeable)connection);
         }

         protected void handleError(StreamSinkChannel channel, IOException e) {
            IoUtils.safeClose((Closeable)connection);
         }
      }).setup(connection.getSinkChannel());
   }

   public void exchangeComplete(final HttpServerExchange exchange) {
      this.connection.clearChannel();
      this.connection.setCurrentExchange((HttpServerExchange)null);
      final HttpServerConnection connection = this.connection;
      if (exchange.isPersistent() && !this.isUpgradeOrConnect(exchange)) {
         StreamConnection channel = connection.getChannel();
         if (connection.getExtraBytes() == null) {
            if (exchange.isInIoThread()) {
               this.newRequest();
               channel.getSourceChannel().setReadListener(this);
               channel.getSourceChannel().resumeReads();
               requestStateUpdater.set(this, 0);
            } else {
               do {
                  if (connection.getOriginalSourceConduit().isReadShutdown() || connection.getOriginalSinkConduit().isWriteShutdown()) {
                     channel.getSourceChannel().suspendReads();
                     channel.getSinkChannel().suspendWrites();
                     IoUtils.safeClose((Closeable)connection);
                     return;
                  }
               } while(!requestStateUpdater.compareAndSet(this, 1, 2));

               try {
                  this.newRequest();
                  channel.getSourceChannel().setReadListener(this);
                  channel.getSourceChannel().resumeReads();
               } finally {
                  requestStateUpdater.set(this, 0);
               }
            }
         } else if (exchange.isInIoThread()) {
            requestStateUpdater.set(this, 0);
            this.newRequest();
            channel.getIoThread().execute(this);
         } else {
            do {
               if (connection.getOriginalSinkConduit().isWriteShutdown()) {
                  channel.getSourceChannel().suspendReads();
                  channel.getSinkChannel().suspendWrites();
                  IoUtils.safeClose((Closeable)connection);
                  return;
               }
            } while(!requestStateUpdater.compareAndSet(this, 1, 2));

            try {
               this.newRequest();
               channel.getSourceChannel().suspendReads();
            } finally {
               requestStateUpdater.set(this, 0);
            }

            Object executor = exchange.getDispatchExecutor();
            if (executor == null) {
               executor = exchange.getConnection().getWorker();
            }

            ((Executor)executor).execute(this);
         }
      } else if (!exchange.isPersistent()) {
         if (connection.getExtraBytes() != null) {
            connection.getExtraBytes().close();
            connection.setExtraBytes((PooledByteBuffer)null);
         }

         ConnectionUtils.cleanClose(connection.getChannel(), connection);
      } else {
         if (connection.getExtraBytes() != null) {
            connection.getChannel().getSourceChannel().setConduit(new ReadDataStreamSourceConduit(connection.getChannel().getSourceChannel().getConduit(), connection));
         }

         try {
            if (!connection.getChannel().getSinkChannel().flush()) {
               connection.getChannel().getSinkChannel().setWriteListener(ChannelListeners.flushingChannelListener(new ChannelListener<ConduitStreamSinkChannel>() {
                  public void handleEvent(ConduitStreamSinkChannel conduitStreamSinkChannel) {
                     connection.getUpgradeListener().handleUpgrade(connection.getChannel(), exchange);
                  }
               }, new ClosingChannelExceptionHandler(new Closeable[]{connection})));
               connection.getChannel().getSinkChannel().resumeWrites();
               return;
            }

            connection.getUpgradeListener().handleUpgrade(connection.getChannel(), exchange);
         } catch (IOException var14) {
            UndertowLogger.REQUEST_IO_LOGGER.ioException(var14);
            IoUtils.safeClose((Closeable)connection);
         } catch (Throwable var15) {
            UndertowLogger.REQUEST_IO_LOGGER.handleUnexpectedFailure(var15);
            IoUtils.safeClose((Closeable)connection);
         }
      }

   }

   private boolean isUpgradeOrConnect(HttpServerExchange exchange) {
      return exchange.isUpgrade() || exchange.getRequestMethod().equals(Methods.CONNECT) && ((HttpServerConnection)exchange.getConnection()).isConnectHandled();
   }

   public void run() {
      this.handleEvent(this.connection.getChannel().getSourceChannel());
   }

   private void handleHttp2PriorKnowledge(final StreamConnection connection, final HttpServerConnection serverConnection, PooledByteBuffer readData) throws IOException {
      ConduitStreamSourceChannel request = connection.getSourceChannel();
      byte[] data = new byte[PRI_EXPECTED.length];
      final ByteBuffer buffer = ByteBuffer.wrap(data);
      if (readData.getBuffer().hasRemaining()) {
         while(readData.getBuffer().hasRemaining() && buffer.hasRemaining()) {
            buffer.put(readData.getBuffer().get());
         }
      }

      final PooledByteBuffer extraData;
      if (readData.getBuffer().hasRemaining()) {
         extraData = readData;
      } else {
         readData.close();
         extraData = null;
      }

      if (!this.doHttp2PriRead(connection, buffer, serverConnection, extraData)) {
         request.getReadSetter().set(new ChannelListener<StreamSourceChannel>() {
            public void handleEvent(StreamSourceChannel channel) {
               try {
                  HttpReadListener.this.doHttp2PriRead(connection, buffer, serverConnection, extraData);
               } catch (IOException var3) {
                  UndertowLogger.REQUEST_IO_LOGGER.ioException(var3);
                  IoUtils.safeClose((Closeable)connection);
               } catch (Throwable var4) {
                  UndertowLogger.REQUEST_IO_LOGGER.handleUnexpectedFailure(var4);
                  IoUtils.safeClose((Closeable)connection);
               }

            }
         });
         request.resumeReads();
      }

   }

   private boolean doHttp2PriRead(StreamConnection connection, ByteBuffer buffer, HttpServerConnection serverConnection, PooledByteBuffer extraData) throws IOException {
      int i;
      if (buffer.hasRemaining()) {
         i = connection.getSourceChannel().read(buffer);
         if (i == -1) {
            return true;
         }

         if (buffer.hasRemaining()) {
            return false;
         }
      }

      buffer.flip();

      for(i = 0; i < PRI_EXPECTED.length; ++i) {
         if (buffer.get() != PRI_EXPECTED[i]) {
            throw UndertowMessages.MESSAGES.http2PriRequestFailed();
         }
      }

      Http2Channel channel = new Http2Channel(connection, (String)null, serverConnection.getByteBufferPool(), extraData, false, false, false, serverConnection.getUndertowOptions());
      Http2ReceiveListener receiveListener = new Http2ReceiveListener(serverConnection.getRootHandler(), serverConnection.getUndertowOptions(), serverConnection.getBufferSize(), (ConnectorStatisticsImpl)null);
      channel.getReceiveSetter().set(receiveListener);
      channel.resumeReceives();
      return true;
   }
}
