package io.undertow.client.http;

import io.undertow.UndertowLogger;
import io.undertow.UndertowOptions;
import io.undertow.client.ClientCallback;
import io.undertow.client.ClientConnection;
import io.undertow.client.ClientExchange;
import io.undertow.client.ClientRequest;
import io.undertow.client.ClientResponse;
import io.undertow.client.ClientStatistics;
import io.undertow.client.UndertowClientMessages;
import io.undertow.client.http2.Http2ClearClientProvider;
import io.undertow.client.http2.Http2ClientConnection;
import io.undertow.conduits.ByteActivityCallback;
import io.undertow.conduits.BytesReceivedStreamSourceConduit;
import io.undertow.conduits.BytesSentStreamSinkConduit;
import io.undertow.conduits.ChunkedStreamSinkConduit;
import io.undertow.conduits.ChunkedStreamSourceConduit;
import io.undertow.conduits.ConduitListener;
import io.undertow.conduits.FinishableStreamSourceConduit;
import io.undertow.conduits.FixedLengthStreamSourceConduit;
import io.undertow.conduits.ReadTimeoutStreamSourceConduit;
import io.undertow.conduits.WriteTimeoutStreamSinkConduit;
import io.undertow.connector.ByteBufferPool;
import io.undertow.connector.PooledByteBuffer;
import io.undertow.protocols.http2.Http2Channel;
import io.undertow.server.Connectors;
import io.undertow.server.protocol.http.HttpContinue;
import io.undertow.server.protocol.http.HttpOpenListener;
import io.undertow.util.AbstractAttachable;
import io.undertow.util.ConnectionUtils;
import io.undertow.util.Headers;
import io.undertow.util.HttpString;
import io.undertow.util.Methods;
import io.undertow.util.PooledAdaptor;
import io.undertow.util.Protocols;
import java.io.Closeable;
import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CopyOnWriteArrayList;
import org.jboss.logging.Logger;
import org.xnio.Bits;
import org.xnio.ChannelExceptionHandler;
import org.xnio.ChannelListener;
import org.xnio.ChannelListeners;
import org.xnio.IoUtils;
import org.xnio.Option;
import org.xnio.OptionMap;
import org.xnio.Options;
import org.xnio.StreamConnection;
import org.xnio.XnioIoThread;
import org.xnio.XnioWorker;
import org.xnio.channels.StreamSourceChannel;
import org.xnio.conduits.ConduitStreamSinkChannel;
import org.xnio.conduits.ConduitStreamSourceChannel;
import org.xnio.conduits.PushBackStreamSourceConduit;
import org.xnio.conduits.StreamSinkConduit;
import org.xnio.conduits.StreamSourceConduit;
import org.xnio.ssl.SslConnection;

class HttpClientConnection extends AbstractAttachable implements Closeable, ClientConnection {
   public final ConduitListener<StreamSinkConduit> requestFinishListener = new ConduitListener<StreamSinkConduit>() {
      public void handleEvent(StreamSinkConduit channel) {
         if (HttpClientConnection.this.currentRequest != null) {
            HttpClientConnection.this.currentRequest.terminateRequest();
         }

      }
   };
   public final ConduitListener<StreamSourceConduit> responseFinishedListener = new ConduitListener<StreamSourceConduit>() {
      public void handleEvent(StreamSourceConduit channel) {
         if (HttpClientConnection.this.currentRequest != null) {
            HttpClientConnection.this.currentRequest.terminateResponse();
         }

      }
   };
   private static final Logger log = Logger.getLogger(HttpClientConnection.class);
   private final Deque<HttpClientExchange> pendingQueue = new ArrayDeque();
   private HttpClientExchange currentRequest;
   private HttpResponseBuilder pendingResponse;
   private final OptionMap options;
   private final StreamConnection connection;
   private final PushBackStreamSourceConduit pushBackStreamSourceConduit;
   private final ClientReadListener clientReadListener = new ClientReadListener();
   private final ByteBufferPool bufferPool;
   private PooledByteBuffer pooledBuffer;
   private final StreamSinkConduit originalSinkConduit;
   private static final int UPGRADED = 268435456;
   private static final int UPGRADE_REQUESTED = 536870912;
   private static final int CLOSE_REQ = 1073741824;
   private static final int CLOSED = Integer.MIN_VALUE;
   private int state;
   private final ChannelListener.SimpleSetter<HttpClientConnection> closeSetter = new ChannelListener.SimpleSetter();
   private final ClientStatistics clientStatistics;
   private int requestCount;
   private int read;
   private int written;
   private boolean http2Tried = false;
   private boolean http2UpgradeReceived = false;
   private ClientConnection http2Delegate;
   private final List<ChannelListener<ClientConnection>> closeListeners = new CopyOnWriteArrayList();

   HttpClientConnection(StreamConnection connection, OptionMap options, ByteBufferPool bufferPool) {
      if (options.get(UndertowOptions.ENABLE_STATISTICS, false)) {
         this.clientStatistics = new ClientStatisticsImpl();
         connection.getSinkChannel().setConduit(new BytesSentStreamSinkConduit(connection.getSinkChannel().getConduit(), new ByteActivityCallback() {
            public void activity(long bytes) {
               HttpClientConnection.this.written = (int)((long)HttpClientConnection.this.written + bytes);
            }
         }));
         connection.getSourceChannel().setConduit(new BytesReceivedStreamSourceConduit(connection.getSourceChannel().getConduit(), new ByteActivityCallback() {
            public void activity(long bytes) {
               HttpClientConnection.this.read = (int)((long)HttpClientConnection.this.read + bytes);
            }
         }));
      } else {
         this.clientStatistics = null;
      }

      this.options = options;
      this.connection = connection;
      this.pushBackStreamSourceConduit = new PushBackStreamSourceConduit(connection.getSourceChannel().getConduit());
      this.connection.getSourceChannel().setConduit(this.pushBackStreamSourceConduit);
      this.bufferPool = bufferPool;
      this.originalSinkConduit = connection.getSinkChannel().getConduit();
      connection.getCloseSetter().set(new ChannelListener<StreamConnection>() {
         public void handleEvent(StreamConnection channel) {
            HttpClientConnection.log.debugf("connection to %s closed", (Object)HttpClientConnection.this.getPeerAddress());
            HttpClientConnection var2 = HttpClientConnection.this;
            var2.state = var2.state | Integer.MIN_VALUE;
            ChannelListeners.invokeChannelListener(HttpClientConnection.this, HttpClientConnection.this.closeSetter.get());

            try {
               if (HttpClientConnection.this.pooledBuffer != null) {
                  HttpClientConnection.this.pooledBuffer.close();
               }
            } catch (Throwable var4) {
            }

            Iterator var5 = HttpClientConnection.this.closeListeners.iterator();

            while(var5.hasNext()) {
               ChannelListener<ClientConnection> listener = (ChannelListener)var5.next();
               listener.handleEvent(HttpClientConnection.this);
            }

            for(HttpClientExchange pending = (HttpClientExchange)HttpClientConnection.this.pendingQueue.poll(); pending != null; pending = (HttpClientExchange)HttpClientConnection.this.pendingQueue.poll()) {
               pending.setFailed(new ClosedChannelException());
            }

            if (HttpClientConnection.this.currentRequest != null) {
               HttpClientConnection.this.currentRequest.setFailed(new ClosedChannelException());
               HttpClientConnection.this.currentRequest = null;
               HttpClientConnection.this.pendingResponse = null;
            }

         }
      });
      connection.getSourceChannel().setReadListener(this.clientReadListener);
      connection.getSourceChannel().resumeReads();
   }

   public ByteBufferPool getBufferPool() {
      return this.bufferPool;
   }

   public SocketAddress getPeerAddress() {
      return this.connection.getPeerAddress();
   }

   StreamConnection getConnection() {
      return this.connection;
   }

   public <A extends SocketAddress> A getPeerAddress(Class<A> type) {
      return this.connection.getPeerAddress(type);
   }

   public ChannelListener.Setter<? extends HttpClientConnection> getCloseSetter() {
      return this.closeSetter;
   }

   public SocketAddress getLocalAddress() {
      return this.connection.getLocalAddress();
   }

   public <A extends SocketAddress> A getLocalAddress(Class<A> type) {
      return this.connection.getLocalAddress(type);
   }

   public XnioWorker getWorker() {
      return this.connection.getWorker();
   }

   public XnioIoThread getIoThread() {
      return this.connection.getIoThread();
   }

   public boolean isOpen() {
      if (this.http2Delegate != null) {
         return this.http2Delegate.isOpen();
      } else {
         return this.connection.isOpen() && Bits.allAreClear(this.state, -1073741824);
      }
   }

   public boolean supportsOption(Option<?> option) {
      return this.http2Delegate != null ? this.http2Delegate.supportsOption(option) : this.connection.supportsOption(option);
   }

   public <T> T getOption(Option<T> option) throws IOException {
      return this.http2Delegate != null ? this.http2Delegate.getOption(option) : this.connection.getOption(option);
   }

   public <T> T setOption(Option<T> option, T value) throws IllegalArgumentException, IOException {
      return this.http2Delegate != null ? this.http2Delegate.setOption(option, value) : this.connection.setOption(option, value);
   }

   public boolean isUpgraded() {
      return this.http2Delegate != null ? this.http2Delegate.isUpgraded() : Bits.anyAreSet(this.state, 805306368);
   }

   public boolean isPushSupported() {
      return this.http2Delegate != null ? this.http2Delegate.isPushSupported() : false;
   }

   public boolean isMultiplexingSupported() {
      return this.http2Delegate != null ? this.http2Delegate.isMultiplexingSupported() : false;
   }

   public ClientStatistics getStatistics() {
      return this.http2Delegate != null ? this.http2Delegate.getStatistics() : this.clientStatistics;
   }

   public boolean isUpgradeSupported() {
      return this.http2Delegate == null;
   }

   public void addCloseListener(ChannelListener<ClientConnection> listener) {
      this.closeListeners.add(listener);
   }

   public void sendRequest(ClientRequest request, ClientCallback<ClientExchange> clientCallback) {
      try {
         Integer readTimeout = (Integer)this.connection.getOption(Options.READ_TIMEOUT);
         if (readTimeout != null && readTimeout > 0) {
            this.connection.getSourceChannel().setConduit(new ReadTimeoutStreamSourceConduit(this.connection.getSourceChannel().getConduit(), this.connection, new HttpOpenListener(this.bufferPool)));
         }

         Integer writeTimeout = (Integer)this.connection.getOption(Options.WRITE_TIMEOUT);
         if (writeTimeout != null && writeTimeout > 0) {
            this.connection.getSinkChannel().setConduit(new WriteTimeoutStreamSinkConduit(this.connection.getSinkChannel().getConduit(), this.connection, new HttpOpenListener(this.bufferPool)));
         }
      } catch (IOException var5) {
         IoUtils.safeClose((Closeable)this.connection);
         UndertowLogger.REQUEST_IO_LOGGER.ioException(var5);
      }

      if (this.http2Delegate != null) {
         this.http2Delegate.sendRequest(request, clientCallback);
      } else if (Bits.anyAreSet(this.state, -268435456)) {
         clientCallback.failed(UndertowClientMessages.MESSAGES.invalidConnectionState());
      } else {
         HttpClientExchange httpClientExchange = new HttpClientExchange(clientCallback, request, this);
         boolean ssl = this.connection instanceof SslConnection;
         if (!ssl && !this.http2Tried && this.options.get(UndertowOptions.ENABLE_HTTP2, false) && !request.getRequestHeaders().contains(Headers.UPGRADE)) {
            request.getRequestHeaders().put(new HttpString("HTTP2-Settings"), Http2ClearClientProvider.createSettingsFrame(this.options, this.bufferPool));
            request.getRequestHeaders().put(Headers.UPGRADE, "h2c");
            request.getRequestHeaders().put(Headers.CONNECTION, "Upgrade, HTTP2-Settings");
            this.http2Tried = true;
         }

         if (this.currentRequest == null) {
            this.initiateRequest(httpClientExchange);
         } else {
            this.pendingQueue.add(httpClientExchange);
         }

      }
   }

   private void initiateRequest(HttpClientExchange httpClientExchange) {
      ++this.requestCount;
      this.currentRequest = httpClientExchange;
      this.pendingResponse = new HttpResponseBuilder();
      ClientRequest request = httpClientExchange.getRequest();
      String connectionString = request.getRequestHeaders().getFirst(Headers.CONNECTION);
      if (connectionString != null) {
         if (Headers.CLOSE.equalToString(connectionString)) {
            this.state |= 1073741824;
         } else if (Headers.UPGRADE.equalToString(connectionString)) {
            this.state |= 536870912;
         }
      } else if (request.getProtocol() != Protocols.HTTP_1_1) {
         this.state |= 1073741824;
      }

      if (request.getRequestHeaders().contains(Headers.UPGRADE)) {
         this.state |= 536870912;
      }

      if (request.getMethod().equals(Methods.CONNECT)) {
         this.state |= 536870912;
      }

      ConduitStreamSourceChannel sourceChannel = this.connection.getSourceChannel();
      sourceChannel.setReadListener(this.clientReadListener);
      sourceChannel.resumeReads();
      ConduitStreamSinkChannel sinkChannel = this.connection.getSinkChannel();
      StreamSinkConduit conduit = this.originalSinkConduit;
      HttpRequestConduit httpRequestConduit = new HttpRequestConduit(conduit, this.bufferPool, request);
      httpClientExchange.setRequestConduit(httpRequestConduit);
      StreamSinkConduit conduit = httpRequestConduit;
      String fixedLengthString = request.getRequestHeaders().getFirst(Headers.CONTENT_LENGTH);
      String transferEncodingString = request.getRequestHeaders().getLast(Headers.TRANSFER_ENCODING);
      boolean hasContent = true;
      Object conduit;
      if (fixedLengthString != null) {
         try {
            long length = Long.parseLong(fixedLengthString);
            conduit = new ClientFixedLengthStreamSinkConduit(conduit, length, false, false, this.currentRequest);
            hasContent = length != 0L;
         } catch (NumberFormatException var14) {
            this.handleError((Throwable)var14);
            return;
         }
      } else if (transferEncodingString != null) {
         if (!transferEncodingString.toLowerCase(Locale.ENGLISH).contains(Headers.CHUNKED.toString())) {
            this.handleError(UndertowClientMessages.MESSAGES.unknownTransferEncoding(transferEncodingString));
            return;
         }

         conduit = new ChunkedStreamSinkConduit(httpRequestConduit, httpClientExchange.getConnection().getBufferPool(), false, false, httpClientExchange.getRequest().getRequestHeaders(), this.requestFinishListener, httpClientExchange);
      } else {
         conduit = new ClientFixedLengthStreamSinkConduit(httpRequestConduit, 0L, false, false, this.currentRequest);
         hasContent = false;
      }

      sinkChannel.setConduit((StreamSinkConduit)conduit);
      httpClientExchange.invokeReadReadyCallback();
      if (!hasContent) {
         try {
            sinkChannel.shutdownWrites();
            if (!sinkChannel.flush()) {
               sinkChannel.setWriteListener(ChannelListeners.flushingChannelListener((ChannelListener)null, new ChannelExceptionHandler<ConduitStreamSinkChannel>() {
                  public void handleException(ConduitStreamSinkChannel channel, IOException exception) {
                     HttpClientConnection.this.handleError(exception);
                  }
               }));
               sinkChannel.resumeWrites();
            }
         } catch (Throwable var13) {
            this.handleError(var13);
         }
      }

   }

   private void handleError(Throwable exception) {
      if (exception instanceof IOException) {
         this.handleError((IOException)exception);
      } else {
         this.handleError(new IOException(exception));
      }

   }

   private void handleError(IOException exception) {
      UndertowLogger.REQUEST_IO_LOGGER.ioException(exception);
      this.currentRequest.setFailed(exception);
      this.currentRequest = null;
      this.pendingResponse = null;
      IoUtils.safeClose((Closeable)this.connection);
   }

   public StreamConnection performUpgrade() throws IOException {
      log.debugf("connection to %s is being upgraded", (Object)this.getPeerAddress());
      if (Bits.allAreSet(this.state, -805306368)) {
         throw new IOException(UndertowClientMessages.MESSAGES.connectionClosed());
      } else {
         this.state |= 268435456;
         this.connection.getSinkChannel().setConduit(this.originalSinkConduit);
         this.connection.getSourceChannel().setConduit(this.pushBackStreamSourceConduit);
         return this.connection;
      }
   }

   public void close() throws IOException {
      log.debugf("close called on connection to %s", (Object)this.getPeerAddress());
      if (this.http2Delegate != null) {
         this.http2Delegate.close();
      }

      if (!Bits.anyAreSet(this.state, Integer.MIN_VALUE)) {
         this.state |= -1073741824;
         ConnectionUtils.cleanClose(this.connection);
      }
   }

   public void exchangeDone() {
      log.debugf("exchange complete in connection to %s", (Object)this.getPeerAddress());
      this.connection.getSinkChannel().setConduit(this.originalSinkConduit);
      this.connection.getSourceChannel().setConduit(this.pushBackStreamSourceConduit);
      this.connection.getSinkChannel().suspendWrites();
      this.connection.getSinkChannel().setWriteListener((ChannelListener)null);
      if (Bits.anyAreSet(this.state, 1073741824)) {
         this.currentRequest = null;
         this.pendingResponse = null;
         this.state |= Integer.MIN_VALUE;
         IoUtils.safeClose((Closeable)this.connection);
      } else if (Bits.anyAreSet(this.state, 536870912)) {
         this.connection.getSourceChannel().suspendReads();
         this.currentRequest = null;
         this.pendingResponse = null;
         return;
      }

      this.currentRequest = null;
      this.pendingResponse = null;
      HttpClientExchange next = (HttpClientExchange)this.pendingQueue.poll();
      if (next == null) {
         this.connection.getSourceChannel().setReadListener(this.clientReadListener);
         this.connection.getSourceChannel().resumeReads();
      } else {
         this.initiateRequest(next);
      }

   }

   public void requestDataSent() {
      if (this.http2UpgradeReceived) {
         this.doHttp2Upgrade();
      }

   }

   protected void doHttp2Upgrade() {
      try {
         StreamConnection connectedStreamChannel = this.performUpgrade();
         Http2Channel http2Channel = new Http2Channel(connectedStreamChannel, (String)null, this.bufferPool, (PooledByteBuffer)null, true, true, this.options);
         Http2ClientConnection http2ClientConnection = new Http2ClientConnection(http2Channel, this.currentRequest.getResponseCallback(), this.currentRequest.getRequest(), this.currentRequest.getRequest().getRequestHeaders().getFirst(Headers.HOST), this.clientStatistics, false);
         http2ClientConnection.getCloseSetter().set(new ChannelListener<ClientConnection>() {
            public void handleEvent(ClientConnection channel) {
               ChannelListeners.invokeChannelListener(HttpClientConnection.this, HttpClientConnection.this.closeSetter.get());
            }
         });
         this.http2Delegate = http2ClientConnection;
         connectedStreamChannel.getSourceChannel().wakeupReads();
         this.currentRequest = null;
         this.pendingResponse = null;
      } catch (IOException var4) {
         UndertowLogger.REQUEST_IO_LOGGER.ioException(var4);
         IoUtils.safeClose((Closeable)this);
      }

   }

   private void prepareResponseChannel(ClientResponse response, ClientExchange exchange) {
      String encoding = response.getResponseHeaders().getLast(Headers.TRANSFER_ENCODING);
      boolean chunked = encoding != null && Headers.CHUNKED.equals(new HttpString(encoding));
      String length = response.getResponseHeaders().getFirst(Headers.CONTENT_LENGTH);
      if (exchange.getRequest().getMethod().equals(Methods.HEAD)) {
         this.connection.getSourceChannel().setConduit(new FixedLengthStreamSourceConduit(this.connection.getSourceChannel().getConduit(), 0L, this.responseFinishedListener));
      } else if (chunked) {
         this.connection.getSourceChannel().setConduit(new ChunkedStreamSourceConduit(this.connection.getSourceChannel().getConduit(), this.pushBackStreamSourceConduit, this.bufferPool, this.responseFinishedListener, exchange, this.connection));
      } else if (length != null) {
         try {
            long contentLength = Long.parseLong(length);
            this.connection.getSourceChannel().setConduit(new FixedLengthStreamSourceConduit(this.connection.getSourceChannel().getConduit(), contentLength, this.responseFinishedListener));
         } catch (NumberFormatException var8) {
            this.handleError((Throwable)var8);
            throw var8;
         }
      } else if (response.getProtocol().equals(Protocols.HTTP_1_1) && !Connectors.isEntityBodyAllowed(response.getResponseCode())) {
         this.connection.getSourceChannel().setConduit(new FixedLengthStreamSourceConduit(this.connection.getSourceChannel().getConduit(), 0L, this.responseFinishedListener));
      } else {
         this.connection.getSourceChannel().setConduit(new FinishableStreamSourceConduit(this.connection.getSourceChannel().getConduit(), this.responseFinishedListener));
         this.state |= 1073741824;
      }

   }

   private class ClientStatisticsImpl implements ClientStatistics {
      private ClientStatisticsImpl() {
      }

      public long getRequests() {
         return (long)HttpClientConnection.this.requestCount;
      }

      public long getRead() {
         return (long)HttpClientConnection.this.read;
      }

      public long getWritten() {
         return (long)HttpClientConnection.this.written;
      }

      public void reset() {
         HttpClientConnection.this.read = 0;
         HttpClientConnection.this.written = 0;
         HttpClientConnection.this.requestCount = 0;
      }

      // $FF: synthetic method
      ClientStatisticsImpl(Object x1) {
         this();
      }
   }

   class ClientReadListener implements ChannelListener<StreamSourceChannel> {
      public void handleEvent(StreamSourceChannel channel) {
         HttpResponseBuilder builder = HttpClientConnection.this.pendingResponse;
         PooledByteBuffer pooled = HttpClientConnection.this.bufferPool.allocate();
         ByteBuffer buffer = pooled.getBuffer();
         boolean free = true;

         try {
            if (builder != null) {
               ResponseParseState state = builder.getParseState();

               do {
                  buffer.clear();

                  int resx;
                  try {
                     resx = channel.read(buffer);
                  } catch (IOException var34) {
                     IOException e = var34;
                     if (UndertowLogger.CLIENT_LOGGER.isDebugEnabled()) {
                        UndertowLogger.CLIENT_LOGGER.debugf(var34, "Connection closed with IOException", new Object[0]);
                     }

                     try {
                        if (HttpClientConnection.this.currentRequest != null) {
                           HttpClientConnection.this.currentRequest.setFailed(e);
                           HttpClientConnection.this.currentRequest = null;
                        }

                        HttpClientConnection.this.pendingResponse = null;
                        return;
                     } finally {
                        IoUtils.safeClose(channel, HttpClientConnection.this);
                     }
                  }

                  if (resx == 0) {
                     if (!channel.isReadResumed()) {
                        channel.getReadSetter().set(this);
                        channel.resumeReads();
                     }

                     return;
                  }

                  if (resx == -1) {
                     channel.suspendReads();

                     try {
                        if (HttpClientConnection.this.currentRequest != null) {
                           HttpClientConnection.this.currentRequest.setFailed(new IOException(UndertowClientMessages.MESSAGES.connectionClosed()));
                           HttpClientConnection.this.currentRequest = null;
                        }

                        HttpClientConnection.this.pendingResponse = null;
                        return;
                     } finally {
                        IoUtils.safeClose((Closeable)HttpClientConnection.this);
                     }
                  }

                  buffer.flip();
                  HttpResponseParser.INSTANCE.handle(buffer, state, builder);
                  if (buffer.hasRemaining()) {
                     free = false;
                     HttpClientConnection.this.pushBackStreamSourceConduit.pushBack(new PooledAdaptor(pooled));
                     HttpClientConnection.this.pushBackStreamSourceConduit.wakeupReads();
                  }
               } while(!state.isComplete());

               ClientResponse response = builder.build();
               String connectionString = response.getResponseHeaders().getFirst(Headers.CONNECTION);
               if (Bits.anyAreSet(HttpClientConnection.this.state, 536870912) && (connectionString == null || !Headers.UPGRADE.equalToString(connectionString)) && !response.getResponseHeaders().contains(Headers.UPGRADE) && (!HttpClientConnection.this.currentRequest.getRequest().getMethod().equals(Methods.CONNECT) || response.getResponseCode() != 200)) {
                  HttpClientConnection var10 = HttpClientConnection.this;
                  var10.state = var10.state & -536870913;
               }

               boolean close = false;
               if (connectionString != null) {
                  if (Headers.CLOSE.equalToString(connectionString)) {
                     close = true;
                  } else if (!response.getProtocol().equals(Protocols.HTTP_1_1) && !Headers.KEEP_ALIVE.equalToString(connectionString)) {
                     close = true;
                  }
               } else if (!response.getProtocol().equals(Protocols.HTTP_1_1)) {
                  close = true;
               }

               HttpClientConnection var11;
               if (close) {
                  var11 = HttpClientConnection.this;
                  var11.state = var11.state | 1073741824;

                  for(HttpClientExchange ex = (HttpClientExchange)HttpClientConnection.this.pendingQueue.poll(); ex != null; ex = (HttpClientExchange)HttpClientConnection.this.pendingQueue.poll()) {
                     ex.setFailed(new IOException(UndertowClientMessages.MESSAGES.connectionClosed()));
                  }
               }

               if (response.getResponseCode() == 101 && "h2c".equals(response.getResponseHeaders().getFirst(Headers.UPGRADE))) {
                  HttpClientConnection.this.http2UpgradeReceived = true;
                  if (HttpClientConnection.this.currentRequest.isRequestDataSent()) {
                     HttpClientConnection.this.doHttp2Upgrade();
                     return;
                  }

                  return;
               } else {
                  if (builder.getStatusCode() == 100) {
                     HttpClientConnection.this.pendingResponse = new HttpResponseBuilder();
                     HttpClientConnection.this.currentRequest.setContinueResponse(response);
                  } else {
                     HttpClientConnection.this.prepareResponseChannel(response, HttpClientConnection.this.currentRequest);
                     channel.getReadSetter().set((ChannelListener)null);
                     channel.suspendReads();
                     HttpClientConnection.this.pendingResponse = null;
                     HttpClientConnection.this.currentRequest.setResponse(response);
                     if (response.getResponseCode() == 417 && HttpContinue.requiresContinueResponse(HttpClientConnection.this.currentRequest.getRequest().getRequestHeaders())) {
                        var11 = HttpClientConnection.this;
                        var11.state = var11.state | 1073741824;
                        ConduitStreamSinkChannel sinkChannel = HttpClientConnection.this.connection.getSinkChannel();
                        sinkChannel.shutdownWrites();
                        if (!sinkChannel.flush()) {
                           sinkChannel.setWriteListener(ChannelListeners.flushingChannelListener((ChannelListener)null, (ChannelExceptionHandler)null));
                           sinkChannel.resumeWrites();
                        }

                        if (HttpClientConnection.this.currentRequest != null) {
                           HttpClientConnection.this.currentRequest.terminateRequest();
                        }

                        return;
                     }
                  }

                  return;
               }
            }

            buffer.clear();

            try {
               int res = channel.read(buffer);
               if (res == -1) {
                  UndertowLogger.CLIENT_LOGGER.debugf("Connection to %s was closed by the target server", HttpClientConnection.this.connection.getPeerAddress());
                  IoUtils.safeClose((Closeable)HttpClientConnection.this);
               } else if (res != 0) {
                  UndertowLogger.CLIENT_LOGGER.debugf("Target server %s sent unexpected data when no request pending, closing connection", HttpClientConnection.this.connection.getPeerAddress());
                  IoUtils.safeClose((Closeable)HttpClientConnection.this);
               }
            } catch (IOException var33) {
               if (UndertowLogger.CLIENT_LOGGER.isDebugEnabled()) {
                  UndertowLogger.CLIENT_LOGGER.debugf(var33, "Connection closed with IOException", new Object[0]);
               }

               IoUtils.safeClose((Closeable)HttpClientConnection.this.connection);
            }
         } catch (Throwable var35) {
            UndertowLogger.CLIENT_LOGGER.exceptionProcessingRequest(var35);
            IoUtils.safeClose((Closeable)HttpClientConnection.this.connection);
            if (HttpClientConnection.this.currentRequest != null) {
               HttpClientConnection.this.currentRequest.setFailed(new IOException(var35));
            }

            return;
         } finally {
            if (free) {
               pooled.close();
               HttpClientConnection.this.pooledBuffer = null;
            } else {
               HttpClientConnection.this.pooledBuffer = pooled;
            }

         }

      }
   }
}
