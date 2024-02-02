package io.undertow.client.ajp;

import io.undertow.UndertowLogger;
import io.undertow.UndertowMessages;
import io.undertow.client.ClientCallback;
import io.undertow.client.ClientConnection;
import io.undertow.client.ClientExchange;
import io.undertow.client.ClientRequest;
import io.undertow.client.ClientResponse;
import io.undertow.client.ClientStatistics;
import io.undertow.client.UndertowClientMessages;
import io.undertow.connector.ByteBufferPool;
import io.undertow.protocols.ajp.AbstractAjpClientStreamSourceChannel;
import io.undertow.protocols.ajp.AjpClientChannel;
import io.undertow.protocols.ajp.AjpClientRequestClientStreamSinkChannel;
import io.undertow.protocols.ajp.AjpClientResponseStreamSourceChannel;
import io.undertow.util.AbstractAttachable;
import io.undertow.util.Headers;
import io.undertow.util.Protocols;
import java.io.Closeable;
import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.ClosedChannelException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import org.jboss.logging.Logger;
import org.xnio.Bits;
import org.xnio.ChannelExceptionHandler;
import org.xnio.ChannelListener;
import org.xnio.ChannelListeners;
import org.xnio.IoUtils;
import org.xnio.Option;
import org.xnio.OptionMap;
import org.xnio.StreamConnection;
import org.xnio.XnioIoThread;
import org.xnio.XnioWorker;
import org.xnio.channels.Channels;
import org.xnio.channels.StreamSinkChannel;
import org.xnio.channels.StreamSourceChannel;

class AjpClientConnection extends AbstractAttachable implements Closeable, ClientConnection {
   public final ChannelListener<AjpClientRequestClientStreamSinkChannel> requestFinishListener = new ChannelListener<AjpClientRequestClientStreamSinkChannel>() {
      public void handleEvent(AjpClientRequestClientStreamSinkChannel channel) {
         if (AjpClientConnection.this.currentRequest != null) {
            AjpClientConnection.this.currentRequest.terminateRequest();
         }

      }
   };
   public final ChannelListener<AjpClientResponseStreamSourceChannel> responseFinishedListener = new ChannelListener<AjpClientResponseStreamSourceChannel>() {
      public void handleEvent(AjpClientResponseStreamSourceChannel channel) {
         if (AjpClientConnection.this.currentRequest != null) {
            AjpClientConnection.this.currentRequest.terminateResponse();
         }

      }
   };
   private static final Logger log = Logger.getLogger(AjpClientConnection.class);
   private final Deque<AjpClientExchange> pendingQueue = new ArrayDeque();
   private AjpClientExchange currentRequest;
   private final OptionMap options;
   private final AjpClientChannel connection;
   private final ByteBufferPool bufferPool;
   private static final int UPGRADED = 268435456;
   private static final int UPGRADE_REQUESTED = 536870912;
   private static final int CLOSE_REQ = 1073741824;
   private static final int CLOSED = Integer.MIN_VALUE;
   private int state;
   private final ChannelListener.SimpleSetter<AjpClientConnection> closeSetter = new ChannelListener.SimpleSetter();
   private final ClientStatistics clientStatistics;
   private final List<ChannelListener<ClientConnection>> closeListeners = new CopyOnWriteArrayList();

   AjpClientConnection(AjpClientChannel connection, OptionMap options, ByteBufferPool bufferPool, ClientStatistics clientStatistics) {
      this.clientStatistics = clientStatistics;
      this.options = options;
      this.connection = connection;
      this.bufferPool = bufferPool;
      connection.addCloseTask(new ChannelListener<AjpClientChannel>() {
         public void handleEvent(AjpClientChannel channel) {
            AjpClientConnection.log.debugf("connection to %s closed", (Object)AjpClientConnection.this.getPeerAddress());
            AjpClientConnection var2 = AjpClientConnection.this;
            var2.state = var2.state | Integer.MIN_VALUE;
            ChannelListeners.invokeChannelListener(AjpClientConnection.this, AjpClientConnection.this.closeSetter.get());
            Iterator var4 = AjpClientConnection.this.closeListeners.iterator();

            while(var4.hasNext()) {
               ChannelListener<ClientConnection> listener = (ChannelListener)var4.next();
               listener.handleEvent(AjpClientConnection.this);
            }

            for(AjpClientExchange pending = (AjpClientExchange)AjpClientConnection.this.pendingQueue.poll(); pending != null; pending = (AjpClientExchange)AjpClientConnection.this.pendingQueue.poll()) {
               pending.setFailed(new ClosedChannelException());
            }

            if (AjpClientConnection.this.currentRequest != null) {
               AjpClientConnection.this.currentRequest.setFailed(new ClosedChannelException());
               AjpClientConnection.this.currentRequest = null;
            }

         }
      });
      connection.getReceiveSetter().set(new ClientReceiveListener());
      connection.resumeReceives();
   }

   public ByteBufferPool getBufferPool() {
      return this.bufferPool;
   }

   public SocketAddress getPeerAddress() {
      return this.connection.getPeerAddress();
   }

   public <A extends SocketAddress> A getPeerAddress(Class<A> type) {
      return this.connection.getPeerAddress(type);
   }

   public ChannelListener.Setter<? extends AjpClientConnection> getCloseSetter() {
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
      return this.connection.isOpen();
   }

   public boolean supportsOption(Option<?> option) {
      return this.connection.supportsOption(option);
   }

   public <T> T getOption(Option<T> option) throws IOException {
      return this.connection.getOption(option);
   }

   public <T> T setOption(Option<T> option, T value) throws IllegalArgumentException, IOException {
      return this.connection.setOption(option, value);
   }

   public boolean isUpgraded() {
      return Bits.anyAreSet(this.state, 805306368);
   }

   public boolean isPushSupported() {
      return false;
   }

   public boolean isMultiplexingSupported() {
      return false;
   }

   public ClientStatistics getStatistics() {
      return this.clientStatistics;
   }

   public boolean isUpgradeSupported() {
      return false;
   }

   public void addCloseListener(ChannelListener<ClientConnection> listener) {
      this.closeListeners.add(listener);
   }

   public void sendRequest(ClientRequest request, ClientCallback<ClientExchange> clientCallback) {
      if (Bits.anyAreSet(this.state, -268435456)) {
         clientCallback.failed(UndertowClientMessages.MESSAGES.invalidConnectionState());
      } else {
         AjpClientExchange AjpClientExchange = new AjpClientExchange(clientCallback, request, this);
         if (this.currentRequest == null) {
            this.initiateRequest(AjpClientExchange);
         } else {
            this.pendingQueue.add(AjpClientExchange);
         }

      }
   }

   public boolean isPingSupported() {
      return true;
   }

   public void sendPing(ClientConnection.PingListener listener, long timeout, TimeUnit timeUnit) {
      this.connection.sendPing(listener, timeout, timeUnit);
   }

   private void initiateRequest(AjpClientExchange AjpClientExchange) {
      this.currentRequest = AjpClientExchange;
      ClientRequest request = AjpClientExchange.getRequest();
      String connectionString = request.getRequestHeaders().getFirst(Headers.CONNECTION);
      if (connectionString != null) {
         if (Headers.CLOSE.equalToString(connectionString)) {
            this.state |= 1073741824;
         }
      } else if (request.getProtocol() != Protocols.HTTP_1_1) {
         this.state |= 1073741824;
      }

      if (request.getRequestHeaders().contains(Headers.UPGRADE)) {
         this.state |= 536870912;
      }

      long length = 0L;
      String fixedLengthString = request.getRequestHeaders().getFirst(Headers.CONTENT_LENGTH);
      String transferEncodingString = request.getRequestHeaders().getLast(Headers.TRANSFER_ENCODING);
      if (fixedLengthString != null) {
         length = Long.parseLong(fixedLengthString);
      } else if (transferEncodingString != null) {
         length = -1L;
      }

      AjpClientRequestClientStreamSinkChannel sinkChannel = this.connection.sendRequest(request.getMethod(), request.getPath(), request.getProtocol(), request.getRequestHeaders(), request, this.requestFinishListener);
      this.currentRequest.setRequestChannel(sinkChannel);
      AjpClientExchange.invokeReadReadyCallback(AjpClientExchange);
      if (length == 0L) {
         try {
            sinkChannel.shutdownWrites();
            if (!sinkChannel.flush()) {
               this.handleFailedFlush(sinkChannel);
            }
         } catch (Throwable var10) {
            this.handleError(var10 instanceof IOException ? (IOException)var10 : new IOException(var10));
         }
      }

   }

   private void handleFailedFlush(AjpClientRequestClientStreamSinkChannel sinkChannel) {
      sinkChannel.getWriteSetter().set(ChannelListeners.flushingChannelListener((ChannelListener)null, new ChannelExceptionHandler<StreamSinkChannel>() {
         public void handleException(StreamSinkChannel channel, IOException exception) {
            AjpClientConnection.this.handleError(exception);
         }
      }));
      sinkChannel.resumeWrites();
   }

   private void handleError(IOException exception) {
      this.currentRequest.setFailed(exception);
      IoUtils.safeClose((Closeable)this.connection);
   }

   public StreamConnection performUpgrade() throws IOException {
      throw UndertowMessages.MESSAGES.upgradeNotSupported();
   }

   public void close() throws IOException {
      log.debugf("close called on connection to %s", (Object)this.getPeerAddress());
      if (!Bits.anyAreSet(this.state, Integer.MIN_VALUE)) {
         this.state |= -1073741824;
         this.connection.close();
      }
   }

   public void requestDone() {
      this.currentRequest = null;
      if (Bits.anyAreSet(this.state, 1073741824)) {
         IoUtils.safeClose((Closeable)this.connection);
      } else if (Bits.anyAreSet(this.state, 536870912)) {
         IoUtils.safeClose((Closeable)this.connection);
         return;
      }

      AjpClientExchange next = (AjpClientExchange)this.pendingQueue.poll();
      if (next != null) {
         this.initiateRequest(next);
      }

   }

   public void requestClose() {
      this.state |= 1073741824;
   }

   class ClientReceiveListener implements ChannelListener<AjpClientChannel> {
      public void handleEvent(AjpClientChannel channel) {
         try {
            AbstractAjpClientStreamSourceChannel result = (AbstractAjpClientStreamSourceChannel)channel.receive();
            if (result == null) {
               if (!channel.isOpen()) {
                  AjpClientConnection.this.getIoThread().execute(new Runnable() {
                     public void run() {
                        if (AjpClientConnection.this.currentRequest != null) {
                           AjpClientConnection.this.currentRequest.setFailed(new ClosedChannelException());
                        }

                     }
                  });
               }

               return;
            }

            if (result instanceof AjpClientResponseStreamSourceChannel) {
               AjpClientResponseStreamSourceChannel response = (AjpClientResponseStreamSourceChannel)result;
               response.setFinishListener(AjpClientConnection.this.responseFinishedListener);
               ClientResponse cr = new ClientResponse(response.getStatusCode(), response.getReasonPhrase(), AjpClientConnection.this.currentRequest.getRequest().getProtocol(), response.getHeaders());
               if (response.getStatusCode() == 100) {
                  AjpClientConnection.this.currentRequest.setContinueResponse(cr);
               } else {
                  AjpClientConnection.this.currentRequest.setResponseChannel(response);
                  AjpClientConnection.this.currentRequest.setResponse(cr);
               }
            } else {
               Channels.drain((StreamSourceChannel)result, Long.MAX_VALUE);
            }
         } catch (Throwable var5) {
            UndertowLogger.CLIENT_LOGGER.exceptionProcessingRequest(var5);
            IoUtils.safeClose((Closeable)AjpClientConnection.this.connection);
            if (AjpClientConnection.this.currentRequest != null) {
               AjpClientConnection.this.currentRequest.setFailed(var5 instanceof IOException ? (IOException)var5 : new IOException(var5));
            }
         }

      }
   }
}
