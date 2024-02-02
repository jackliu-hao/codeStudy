package io.undertow.client.http2;

import io.undertow.UndertowLogger;
import io.undertow.UndertowMessages;
import io.undertow.client.ClientCallback;
import io.undertow.client.ClientConnection;
import io.undertow.client.ClientExchange;
import io.undertow.client.ClientRequest;
import io.undertow.client.ClientStatistics;
import io.undertow.connector.ByteBufferPool;
import io.undertow.protocols.http2.AbstractHttp2StreamSourceChannel;
import io.undertow.protocols.http2.Http2Channel;
import io.undertow.protocols.http2.Http2DataStreamSinkChannel;
import io.undertow.protocols.http2.Http2GoAwayStreamSourceChannel;
import io.undertow.protocols.http2.Http2HeadersStreamSinkChannel;
import io.undertow.protocols.http2.Http2PingStreamSourceChannel;
import io.undertow.protocols.http2.Http2PushPromiseStreamSourceChannel;
import io.undertow.protocols.http2.Http2RstStreamStreamSourceChannel;
import io.undertow.protocols.http2.Http2StreamSinkChannel;
import io.undertow.protocols.http2.Http2StreamSourceChannel;
import io.undertow.server.protocol.http.HttpAttachments;
import io.undertow.util.HeaderMap;
import io.undertow.util.HeaderValues;
import io.undertow.util.Headers;
import io.undertow.util.HttpString;
import io.undertow.util.Methods;
import io.undertow.util.Protocols;
import java.io.Closeable;
import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.ClosedChannelException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;
import org.xnio.ChannelExceptionHandler;
import org.xnio.ChannelListener;
import org.xnio.ChannelListeners;
import org.xnio.IoUtils;
import org.xnio.Option;
import org.xnio.StreamConnection;
import org.xnio.XnioIoThread;
import org.xnio.XnioWorker;
import org.xnio.channels.Channels;
import org.xnio.channels.StreamSinkChannel;
import org.xnio.channels.StreamSourceChannel;

public class Http2ClientConnection implements ClientConnection {
   private final Http2Channel http2Channel;
   private final ChannelListener.SimpleSetter<ClientConnection> closeSetter = new ChannelListener.SimpleSetter();
   private final Map<Integer, Http2ClientExchange> currentExchanges = new ConcurrentHashMap();
   private static final AtomicLong PING_COUNTER = new AtomicLong();
   private boolean initialUpgradeRequest;
   private final String defaultHost;
   private final ClientStatistics clientStatistics;
   private final List<ChannelListener<ClientConnection>> closeListeners = new CopyOnWriteArrayList();
   private final boolean secure;
   private final Map<PingKey, ClientConnection.PingListener> outstandingPings = new HashMap();
   private final ChannelListener<Http2Channel> closeTask = new ChannelListener<Http2Channel>() {
      public void handleEvent(Http2Channel channel) {
         ChannelListeners.invokeChannelListener(Http2ClientConnection.this, Http2ClientConnection.this.closeSetter.get());
         Iterator var2 = Http2ClientConnection.this.closeListeners.iterator();

         while(var2.hasNext()) {
            ChannelListener<ClientConnection> listener = (ChannelListener)var2.next();
            listener.handleEvent(Http2ClientConnection.this);
         }

         var2 = Http2ClientConnection.this.currentExchanges.entrySet().iterator();

         while(var2.hasNext()) {
            Map.Entry<Integer, Http2ClientExchange> entry = (Map.Entry)var2.next();
            ((Http2ClientExchange)entry.getValue()).failed(new ClosedChannelException());
         }

         Http2ClientConnection.this.currentExchanges.clear();
      }
   };

   public Http2ClientConnection(Http2Channel http2Channel, boolean initialUpgradeRequest, String defaultHost, ClientStatistics clientStatistics, boolean secure) {
      this.http2Channel = http2Channel;
      this.defaultHost = defaultHost;
      this.clientStatistics = clientStatistics;
      this.secure = secure;
      http2Channel.getReceiveSetter().set(new Http2ReceiveListener());
      http2Channel.resumeReceives();
      http2Channel.addCloseTask(this.closeTask);
      this.initialUpgradeRequest = initialUpgradeRequest;
   }

   public Http2ClientConnection(Http2Channel http2Channel, ClientCallback<ClientExchange> upgradeReadyCallback, ClientRequest clientRequest, String defaultHost, ClientStatistics clientStatistics, boolean secure) {
      this.http2Channel = http2Channel;
      this.defaultHost = defaultHost;
      this.clientStatistics = clientStatistics;
      this.secure = secure;
      http2Channel.getReceiveSetter().set(new Http2ReceiveListener());
      http2Channel.resumeReceives();
      http2Channel.addCloseTask(this.closeTask);
      this.initialUpgradeRequest = false;
      Http2ClientExchange exchange = new Http2ClientExchange(this, (Http2StreamSinkChannel)null, clientRequest);
      exchange.setResponseListener(upgradeReadyCallback);
      this.currentExchanges.put(1, exchange);
   }

   public void sendRequest(ClientRequest request, ClientCallback<ClientExchange> clientCallback) {
      if (!this.http2Channel.isOpen()) {
         clientCallback.failed(new ClosedChannelException());
      } else {
         request.getRequestHeaders().put(Http2Channel.METHOD, request.getMethod().toString());
         boolean connectRequest = request.getMethod().equals(Methods.CONNECT);
         if (!connectRequest) {
            request.getRequestHeaders().put(Http2Channel.PATH, request.getPath());
            request.getRequestHeaders().put(Http2Channel.SCHEME, this.secure ? "https" : "http");
         }

         String host = request.getRequestHeaders().getFirst(Headers.HOST);
         if (host != null) {
            request.getRequestHeaders().put(Http2Channel.AUTHORITY, host);
         } else {
            request.getRequestHeaders().put(Http2Channel.AUTHORITY, this.defaultHost);
         }

         request.getRequestHeaders().remove(Headers.HOST);
         boolean hasContent = true;
         String fixedLengthString = request.getRequestHeaders().getFirst(Headers.CONTENT_LENGTH);
         String transferEncodingString = request.getRequestHeaders().getLast(Headers.TRANSFER_ENCODING);
         if (fixedLengthString != null) {
            try {
               long length = Long.parseLong(fixedLengthString);
               hasContent = length != 0L;
            } catch (NumberFormatException var12) {
               this.handleError(new IOException(var12));
               return;
            }
         } else if (transferEncodingString == null && !connectRequest) {
            hasContent = false;
         }

         request.getRequestHeaders().remove(Headers.CONNECTION);
         request.getRequestHeaders().remove(Headers.KEEP_ALIVE);
         request.getRequestHeaders().remove(Headers.TRANSFER_ENCODING);

         Http2HeadersStreamSinkChannel sinkChannel;
         try {
            sinkChannel = this.http2Channel.createStream(request.getRequestHeaders());
         } catch (Throwable var13) {
            IOException e = var13 instanceof IOException ? (IOException)var13 : new IOException(var13);
            clientCallback.failed(e);
            return;
         }

         final Http2ClientExchange exchange = new Http2ClientExchange(this, sinkChannel, request);
         this.currentExchanges.put(sinkChannel.getStreamId(), exchange);
         sinkChannel.setTrailersProducer(new Http2DataStreamSinkChannel.TrailersProducer() {
            public HeaderMap getTrailers() {
               HeaderMap attachment = (HeaderMap)exchange.getAttachment(HttpAttachments.RESPONSE_TRAILERS);
               Supplier<HeaderMap> supplier = (Supplier)exchange.getAttachment(HttpAttachments.RESPONSE_TRAILER_SUPPLIER);
               if (attachment != null && supplier == null) {
                  return attachment;
               } else if (attachment == null && supplier != null) {
                  return (HeaderMap)supplier.get();
               } else if (attachment == null) {
                  return null;
               } else {
                  HeaderMap supplied = (HeaderMap)supplier.get();
                  Iterator var4 = supplied.iterator();

                  while(var4.hasNext()) {
                     HeaderValues k = (HeaderValues)var4.next();
                     attachment.putAll(k.getHeaderName(), k);
                  }

                  return attachment;
               }
            }
         });
         if (clientCallback != null) {
            clientCallback.completed(exchange);
         }

         if (!hasContent) {
            try {
               sinkChannel.shutdownWrites();
               if (!sinkChannel.flush()) {
                  sinkChannel.getWriteSetter().set(ChannelListeners.flushingChannelListener((ChannelListener)null, new ChannelExceptionHandler<StreamSinkChannel>() {
                     public void handleException(StreamSinkChannel channel, IOException exception) {
                        Http2ClientConnection.this.handleError(exception);
                     }
                  }));
                  sinkChannel.resumeWrites();
               }
            } catch (Throwable var11) {
               this.handleError(var11);
            }
         }

      }
   }

   private void handleError(Throwable t) {
      IOException e = t instanceof IOException ? (IOException)t : new IOException(t);
      UndertowLogger.REQUEST_IO_LOGGER.ioException(e);
      IoUtils.safeClose((Closeable)this);
      Iterator var3 = this.currentExchanges.entrySet().iterator();

      while(var3.hasNext()) {
         Map.Entry<Integer, Http2ClientExchange> entry = (Map.Entry)var3.next();

         try {
            ((Http2ClientExchange)entry.getValue()).failed(e);
         } catch (Exception var6) {
            UndertowLogger.REQUEST_IO_LOGGER.ioException(new IOException(var6));
         }
      }

   }

   public StreamConnection performUpgrade() throws IOException {
      throw UndertowMessages.MESSAGES.upgradeNotSupported();
   }

   public ByteBufferPool getBufferPool() {
      return this.http2Channel.getBufferPool();
   }

   public SocketAddress getPeerAddress() {
      return this.http2Channel.getPeerAddress();
   }

   public <A extends SocketAddress> A getPeerAddress(Class<A> type) {
      return this.http2Channel.getPeerAddress(type);
   }

   public ChannelListener.Setter<? extends ClientConnection> getCloseSetter() {
      return this.closeSetter;
   }

   public SocketAddress getLocalAddress() {
      return this.http2Channel.getLocalAddress();
   }

   public <A extends SocketAddress> A getLocalAddress(Class<A> type) {
      return this.http2Channel.getLocalAddress(type);
   }

   public XnioWorker getWorker() {
      return this.http2Channel.getWorker();
   }

   public XnioIoThread getIoThread() {
      return this.http2Channel.getIoThread();
   }

   public boolean isOpen() {
      return this.http2Channel.isOpen() && !this.http2Channel.isPeerGoneAway() && !this.http2Channel.isThisGoneAway();
   }

   public void close() throws IOException {
      boolean var7 = false;

      try {
         var7 = true;
         this.http2Channel.sendGoAway(0);
         var7 = false;
      } finally {
         if (var7) {
            Iterator var4 = this.currentExchanges.entrySet().iterator();

            while(var4.hasNext()) {
               Map.Entry<Integer, Http2ClientExchange> entry = (Map.Entry)var4.next();
               ((Http2ClientExchange)entry.getValue()).failed(new ClosedChannelException());
            }

            this.currentExchanges.clear();
         }
      }

      Iterator var1 = this.currentExchanges.entrySet().iterator();

      while(var1.hasNext()) {
         Map.Entry<Integer, Http2ClientExchange> entry = (Map.Entry)var1.next();
         ((Http2ClientExchange)entry.getValue()).failed(new ClosedChannelException());
      }

      this.currentExchanges.clear();
   }

   public boolean supportsOption(Option<?> option) {
      return false;
   }

   public <T> T getOption(Option<T> option) throws IOException {
      return null;
   }

   public <T> T setOption(Option<T> option, T value) throws IllegalArgumentException, IOException {
      return null;
   }

   public boolean isUpgraded() {
      return false;
   }

   public boolean isPushSupported() {
      return true;
   }

   public boolean isMultiplexingSupported() {
      return true;
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

   public boolean isPingSupported() {
      return true;
   }

   public void sendPing(ClientConnection.PingListener listener, long timeout, TimeUnit timeUnit) {
      long count = PING_COUNTER.incrementAndGet();
      byte[] data = new byte[]{(byte)((int)count), (byte)((int)(count << 8)), (byte)((int)(count << 16)), (byte)((int)(count << 24)), (byte)((int)(count << 32)), (byte)((int)(count << 40)), (byte)((int)(count << 48)), (byte)((int)(count << 54))};
      PingKey key = new PingKey(data);
      this.outstandingPings.put(key, listener);
      if (timeout > 0L) {
         this.http2Channel.getIoThread().executeAfter(() -> {
            ClientConnection.PingListener listener1 = (ClientConnection.PingListener)this.outstandingPings.remove(key);
            if (listener1 != null) {
               listener1.failed(UndertowMessages.MESSAGES.pingTimeout());
            }

         }, timeout, timeUnit);
      }

      this.http2Channel.sendPing(data, (channel, exception) -> {
         listener.failed(exception);
      });
   }

   private static final class PingKey {
      private final byte[] data;

      private PingKey(byte[] data) {
         this.data = data;
      }

      public boolean equals(Object o) {
         if (this == o) {
            return true;
         } else if (o != null && this.getClass() == o.getClass()) {
            PingKey pingKey = (PingKey)o;
            return Arrays.equals(this.data, pingKey.data);
         } else {
            return false;
         }
      }

      public int hashCode() {
         return Arrays.hashCode(this.data);
      }

      // $FF: synthetic method
      PingKey(byte[] x0, Object x1) {
         this(x0);
      }
   }

   private class Http2ReceiveListener implements ChannelListener<Http2Channel> {
      private Http2ReceiveListener() {
      }

      public void handleEvent(Http2Channel channel) {
         try {
            AbstractHttp2StreamSourceChannel result = (AbstractHttp2StreamSourceChannel)channel.receive();
            int streamx;
            final Http2ClientExchange requestx;
            if (result instanceof Http2StreamSourceChannel) {
               final Http2StreamSourceChannel streamSourceChannel = (Http2StreamSourceChannel)result;
               streamx = Integer.parseInt(streamSourceChannel.getHeaders().getFirst(Http2Channel.STATUS));
               requestx = (Http2ClientExchange)Http2ClientConnection.this.currentExchanges.get(streamSourceChannel.getStreamId());
               if (streamx < 200) {
                  if (streamx == 100) {
                     requestx.setContinueResponse(requestx.createResponse(streamSourceChannel));
                  }

                  Channels.drain((StreamSourceChannel)result, Long.MAX_VALUE);
                  return;
               }

               ((Http2StreamSourceChannel)result).setTrailersHandler(new Http2StreamSourceChannel.TrailersHandler() {
                  public void handleTrailers(HeaderMap headerMap) {
                     requestx.putAttachment(HttpAttachments.REQUEST_TRAILERS, headerMap);
                  }
               });
               result.addCloseTask(new ChannelListener<AbstractHttp2StreamSourceChannel>() {
                  public void handleEvent(AbstractHttp2StreamSourceChannel channel) {
                     Http2ClientConnection.this.currentExchanges.remove(streamSourceChannel.getStreamId());
                  }
               });
               streamSourceChannel.setCompletionListener(new ChannelListener<Http2StreamSourceChannel>() {
                  public void handleEvent(Http2StreamSourceChannel channel) {
                     Http2ClientConnection.this.currentExchanges.remove(streamSourceChannel.getStreamId());
                  }
               });
               if (requestx == null && Http2ClientConnection.this.initialUpgradeRequest) {
                  Channels.drain((StreamSourceChannel)result, Long.MAX_VALUE);
                  Http2ClientConnection.this.initialUpgradeRequest = false;
                  return;
               }

               if (requestx == null) {
                  channel.sendGoAway(1);
                  IoUtils.safeClose((Closeable)Http2ClientConnection.this);
                  return;
               }

               requestx.responseReady(streamSourceChannel);
            } else if (result instanceof Http2PingStreamSourceChannel) {
               this.handlePing((Http2PingStreamSourceChannel)result);
            } else if (result instanceof Http2RstStreamStreamSourceChannel) {
               Http2RstStreamStreamSourceChannel rstStream = (Http2RstStreamStreamSourceChannel)result;
               streamx = rstStream.getStreamId();
               UndertowLogger.REQUEST_LOGGER.debugf("Client received RST_STREAM for stream %s", streamx);
               requestx = (Http2ClientExchange)Http2ClientConnection.this.currentExchanges.remove(streamx);
               if (requestx != null) {
                  requestx.failed(UndertowMessages.MESSAGES.http2StreamWasReset());
               }

               Channels.drain((StreamSourceChannel)result, Long.MAX_VALUE);
            } else if (result instanceof Http2PushPromiseStreamSourceChannel) {
               Http2PushPromiseStreamSourceChannel stream = (Http2PushPromiseStreamSourceChannel)result;
               Http2ClientExchange request = (Http2ClientExchange)Http2ClientConnection.this.currentExchanges.get(stream.getAssociatedStreamId());
               if (request == null) {
                  channel.sendGoAway(1);
               } else if (request.getPushCallback() == null) {
                  channel.sendRstStream(stream.getPushedStreamId(), 7);
               } else {
                  ClientRequest cr = new ClientRequest();
                  cr.setMethod(new HttpString(stream.getHeaders().getFirst(Http2Channel.METHOD)));
                  cr.setPath(stream.getHeaders().getFirst(Http2Channel.PATH));
                  cr.setProtocol(Protocols.HTTP_1_1);
                  Iterator var6 = stream.getHeaders().iterator();

                  while(var6.hasNext()) {
                     HeaderValues header = (HeaderValues)var6.next();
                     cr.getRequestHeaders().putAll(header.getHeaderName(), header);
                  }

                  Http2ClientExchange newExchange = new Http2ClientExchange(Http2ClientConnection.this, (Http2StreamSinkChannel)null, cr);
                  if (!request.getPushCallback().handlePush(request, newExchange)) {
                     channel.sendRstStream(stream.getPushedStreamId(), 7);
                     IoUtils.safeClose((Closeable)stream);
                  } else if (!Http2ClientConnection.this.http2Channel.addPushPromiseStream(stream.getPushedStreamId())) {
                     channel.sendGoAway(1);
                  } else {
                     Http2ClientConnection.this.currentExchanges.put(stream.getPushedStreamId(), newExchange);
                  }
               }

               Channels.drain((StreamSourceChannel)result, Long.MAX_VALUE);
            } else if (result instanceof Http2GoAwayStreamSourceChannel) {
               Http2ClientConnection.this.close();
            } else if (result != null) {
               Channels.drain((StreamSourceChannel)result, Long.MAX_VALUE);
            }
         } catch (Throwable var9) {
            IOException e = var9 instanceof IOException ? (IOException)var9 : new IOException(var9);
            UndertowLogger.REQUEST_IO_LOGGER.ioException(e);
            IoUtils.safeClose((Closeable)Http2ClientConnection.this);
            Iterator var4 = Http2ClientConnection.this.currentExchanges.entrySet().iterator();

            while(var4.hasNext()) {
               Map.Entry<Integer, Http2ClientExchange> entry = (Map.Entry)var4.next();

               try {
                  ((Http2ClientExchange)entry.getValue()).failed(e);
               } catch (Throwable var8) {
                  UndertowLogger.REQUEST_IO_LOGGER.ioException(new IOException(var8));
               }
            }
         }

      }

      private void handlePing(Http2PingStreamSourceChannel frame) {
         byte[] id = frame.getData();
         if (!frame.isAck()) {
            frame.getHttp2Channel().sendPing(id);
         } else {
            ClientConnection.PingListener listener = (ClientConnection.PingListener)Http2ClientConnection.this.outstandingPings.remove(new PingKey(id));
            if (listener != null) {
               listener.acknowledged();
            }
         }

      }

      // $FF: synthetic method
      Http2ReceiveListener(Object x1) {
         this();
      }
   }
}
