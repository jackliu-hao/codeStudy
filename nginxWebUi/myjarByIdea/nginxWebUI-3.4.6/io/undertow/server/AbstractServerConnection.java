package io.undertow.server;

import io.undertow.UndertowLogger;
import io.undertow.UndertowMessages;
import io.undertow.connector.ByteBufferPool;
import io.undertow.connector.PooledByteBuffer;
import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.xnio.ChannelListener;
import org.xnio.ChannelListeners;
import org.xnio.Option;
import org.xnio.OptionMap;
import org.xnio.Pool;
import org.xnio.StreamConnection;
import org.xnio.XnioIoThread;
import org.xnio.XnioWorker;
import org.xnio.conduits.ConduitStreamSinkChannel;
import org.xnio.conduits.ConduitStreamSourceChannel;
import org.xnio.conduits.StreamSinkConduit;
import org.xnio.conduits.StreamSourceConduit;

public abstract class AbstractServerConnection extends ServerConnection {
   protected final StreamConnection channel;
   protected final CloseSetter closeSetter;
   protected final ByteBufferPool bufferPool;
   protected final HttpHandler rootHandler;
   protected final OptionMap undertowOptions;
   protected final StreamSourceConduit originalSourceConduit;
   protected final StreamSinkConduit originalSinkConduit;
   protected final List<ServerConnection.CloseListener> closeListeners = new LinkedList();
   protected HttpServerExchange current;
   private final int bufferSize;
   private XnioBufferPoolAdaptor poolAdaptor;
   protected PooledByteBuffer extraBytes;

   public AbstractServerConnection(StreamConnection channel, ByteBufferPool bufferPool, HttpHandler rootHandler, OptionMap undertowOptions, int bufferSize) {
      this.channel = channel;
      this.bufferPool = bufferPool;
      this.rootHandler = rootHandler;
      this.undertowOptions = undertowOptions;
      this.bufferSize = bufferSize;
      this.closeSetter = new CloseSetter();
      if (channel != null) {
         this.originalSinkConduit = channel.getSinkChannel().getConduit();
         this.originalSourceConduit = channel.getSourceChannel().getConduit();
         channel.setCloseListener(this.closeSetter);
      } else {
         this.originalSinkConduit = null;
         this.originalSourceConduit = null;
      }

   }

   public Pool<ByteBuffer> getBufferPool() {
      if (this.poolAdaptor == null) {
         this.poolAdaptor = new XnioBufferPoolAdaptor(this.getByteBufferPool());
      }

      return this.poolAdaptor;
   }

   public HttpHandler getRootHandler() {
      return this.rootHandler;
   }

   public ByteBufferPool getByteBufferPool() {
      return this.bufferPool;
   }

   public StreamConnection getChannel() {
      return this.channel;
   }

   public ChannelListener.Setter<ServerConnection> getCloseSetter() {
      return this.closeSetter;
   }

   public XnioWorker getWorker() {
      return this.channel.getWorker();
   }

   public XnioIoThread getIoThread() {
      return this.channel == null ? null : this.channel.getIoThread();
   }

   public boolean isOpen() {
      return this.channel.isOpen();
   }

   public boolean supportsOption(Option<?> option) {
      return this.channel.supportsOption(option);
   }

   public <T> T getOption(Option<T> option) throws IOException {
      return this.channel.getOption(option);
   }

   public <T> T setOption(Option<T> option, T value) throws IllegalArgumentException, IOException {
      return this.channel.setOption(option, value);
   }

   public void close() throws IOException {
      this.channel.close();
   }

   public SocketAddress getPeerAddress() {
      return this.channel.getPeerAddress();
   }

   public <A extends SocketAddress> A getPeerAddress(Class<A> type) {
      return this.channel.getPeerAddress(type);
   }

   public SocketAddress getLocalAddress() {
      return this.channel.getLocalAddress();
   }

   public <A extends SocketAddress> A getLocalAddress(Class<A> type) {
      return this.channel.getLocalAddress(type);
   }

   public OptionMap getUndertowOptions() {
      return this.undertowOptions;
   }

   public int getBufferSize() {
      return this.bufferSize;
   }

   public PooledByteBuffer getExtraBytes() {
      if (this.extraBytes != null && !this.extraBytes.getBuffer().hasRemaining()) {
         this.extraBytes.close();
         this.extraBytes = null;
         return null;
      } else {
         return this.extraBytes;
      }
   }

   public void setExtraBytes(PooledByteBuffer extraBytes) {
      this.extraBytes = extraBytes;
   }

   public StreamSourceConduit getOriginalSourceConduit() {
      return this.originalSourceConduit;
   }

   public StreamSinkConduit getOriginalSinkConduit() {
      return this.originalSinkConduit;
   }

   public ConduitState resetChannel() {
      ConduitState ret = new ConduitState(this.channel.getSinkChannel().getConduit(), this.channel.getSourceChannel().getConduit());
      this.channel.getSinkChannel().setConduit(this.originalSinkConduit);
      this.channel.getSourceChannel().setConduit(this.originalSourceConduit);
      return ret;
   }

   public void clearChannel() {
      this.channel.getSinkChannel().setConduit(this.originalSinkConduit);
      this.channel.getSourceChannel().setConduit(this.originalSourceConduit);
   }

   public void restoreChannel(ConduitState state) {
      this.channel.getSinkChannel().setConduit(state.sink);
      this.channel.getSourceChannel().setConduit(state.source);
   }

   protected static StreamSinkConduit sink(ConduitState state) {
      return state.sink;
   }

   protected static StreamSourceConduit source(ConduitState state) {
      return state.source;
   }

   public void addCloseListener(ServerConnection.CloseListener listener) {
      this.closeListeners.add(listener);
   }

   protected ConduitStreamSinkChannel getSinkChannel() {
      return this.channel.getSinkChannel();
   }

   protected ConduitStreamSourceChannel getSourceChannel() {
      return this.channel.getSourceChannel();
   }

   protected void setUpgradeListener(HttpUpgradeListener upgradeListener) {
      throw UndertowMessages.MESSAGES.upgradeNotSupported();
   }

   protected void maxEntitySizeUpdated(HttpServerExchange exchange) {
   }

   private class CloseSetter implements ChannelListener.Setter<ServerConnection>, ChannelListener<StreamConnection> {
      private ChannelListener<? super ServerConnection> listener;

      private CloseSetter() {
      }

      public void set(ChannelListener<? super ServerConnection> listener) {
         this.listener = listener;
      }

      public void handleEvent(StreamConnection channel) {
         try {
            Iterator var2 = AbstractServerConnection.this.closeListeners.iterator();

            while(var2.hasNext()) {
               ServerConnection.CloseListener l = (ServerConnection.CloseListener)var2.next();

               try {
                  l.closed(AbstractServerConnection.this);
               } catch (Throwable var8) {
                  UndertowLogger.REQUEST_LOGGER.exceptionInvokingCloseListener(l, var8);
               }
            }

            if (AbstractServerConnection.this.current != null) {
               AbstractServerConnection.this.current.endExchange();
            }

            ChannelListeners.invokeChannelListener(AbstractServerConnection.this, this.listener);
         } finally {
            if (AbstractServerConnection.this.extraBytes != null) {
               AbstractServerConnection.this.extraBytes.close();
               AbstractServerConnection.this.extraBytes = null;
            }

         }

      }

      // $FF: synthetic method
      CloseSetter(Object x1) {
         this();
      }
   }

   public static class ConduitState {
      final StreamSinkConduit sink;
      final StreamSourceConduit source;

      private ConduitState(StreamSinkConduit sink, StreamSourceConduit source) {
         this.sink = sink;
         this.source = source;
      }

      // $FF: synthetic method
      ConduitState(StreamSinkConduit x0, StreamSourceConduit x1, Object x2) {
         this(x0, x1);
      }
   }
}
