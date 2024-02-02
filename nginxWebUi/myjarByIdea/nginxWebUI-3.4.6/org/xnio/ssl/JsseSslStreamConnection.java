package org.xnio.ssl;

import java.io.Closeable;
import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.util.Set;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLSession;
import org.xnio.Bits;
import org.xnio.ChannelListener;
import org.xnio.ChannelListeners;
import org.xnio.IoUtils;
import org.xnio.Option;
import org.xnio.Options;
import org.xnio.Pool;
import org.xnio.SslClientAuthMode;
import org.xnio.StreamConnection;
import org.xnio.conduits.StreamSinkConduit;
import org.xnio.conduits.StreamSourceConduit;

public final class JsseSslStreamConnection extends SslConnection {
   private final StreamConnection connection;
   private final JsseSslConduitEngine sslConduitEngine;
   private volatile boolean tls;
   private final ChannelListener.SimpleSetter<SslConnection> handshakeSetter;
   private volatile int state;
   private static final int FLAG_READ_CLOSE_REQUESTED = 1;
   private static final int FLAG_WRITE_CLOSE_REQUESTED = 2;
   private static final int FLAG_READ_CLOSED = 4;
   private static final int FLAG_WRITE_CLOSED = 8;
   private static final AtomicIntegerFieldUpdater<JsseSslStreamConnection> stateUpdater = AtomicIntegerFieldUpdater.newUpdater(JsseSslStreamConnection.class, "state");
   private static final Set<Option<?>> SUPPORTED_OPTIONS;

   public JsseSslStreamConnection(StreamConnection connection, SSLEngine sslEngine, boolean startTls) {
      this(connection, sslEngine, JsseXnioSsl.bufferPool, JsseXnioSsl.bufferPool, startTls);
   }

   JsseSslStreamConnection(StreamConnection connection, SSLEngine sslEngine, Pool<ByteBuffer> socketBufferPool, Pool<ByteBuffer> applicationBufferPool, boolean startTls) {
      super(connection.getIoThread());
      this.handshakeSetter = new ChannelListener.SimpleSetter();
      this.connection = connection;
      StreamSinkConduit sinkConduit = connection.getSinkChannel().getConduit();
      StreamSourceConduit sourceConduit = connection.getSourceChannel().getConduit();
      this.sslConduitEngine = new JsseSslConduitEngine(this, sinkConduit, sourceConduit, sslEngine, socketBufferPool, applicationBufferPool);
      this.tls = !startTls;
      this.setSinkConduit(new JsseSslStreamSinkConduit(sinkConduit, this.sslConduitEngine, this.tls));
      this.setSourceConduit(new JsseSslStreamSourceConduit(sourceConduit, this.sslConduitEngine, this.tls));
      this.getSourceChannel().setCloseListener((channel) -> {
         this.readClosed();
      });
      this.getSinkChannel().setCloseListener((channel) -> {
         this.writeClosed();
      });
   }

   public synchronized void startHandshake() throws IOException {
      if (!this.tls) {
         this.tls = true;
         ((JsseSslStreamSourceConduit)this.getSourceChannel().getConduit()).enableTls();
         ((JsseSslStreamSinkConduit)this.getSinkChannel().getConduit()).enableTls();
      }

      this.sslConduitEngine.beginHandshake();
   }

   public SocketAddress getPeerAddress() {
      return this.connection.getPeerAddress();
   }

   public SocketAddress getLocalAddress() {
      return this.connection.getLocalAddress();
   }

   protected void closeAction() throws IOException {
      if (!this.sslConduitEngine.isClosed()) {
         this.sslConduitEngine.close();
      } else {
         if (this.tls) {
            try {
               this.getSinkChannel().getConduit().terminateWrites();
            } catch (IOException var5) {
               try {
                  this.getSourceChannel().getConduit().terminateReads();
               } catch (IOException var3) {
               }

               IoUtils.safeClose((Closeable)this.connection);
               throw var5;
            }

            try {
               this.getSourceChannel().getConduit().terminateReads();
            } catch (IOException var4) {
               IoUtils.safeClose((Closeable)this.connection);
               throw var4;
            }

            super.closeAction();
         }

         this.connection.close();
      }

   }

   protected void notifyWriteClosed() {
   }

   protected void notifyReadClosed() {
   }

   public <T> T setOption(Option<T> option, T value) throws IllegalArgumentException, IOException {
      if (option == Options.SSL_CLIENT_AUTH_MODE) {
         SSLEngine engine = this.sslConduitEngine.getEngine();

         Object var4;
         try {
            var4 = option.cast(engine.getNeedClientAuth() ? SslClientAuthMode.REQUIRED : (engine.getWantClientAuth() ? SslClientAuthMode.REQUESTED : SslClientAuthMode.NOT_REQUESTED));
         } finally {
            engine.setNeedClientAuth(value == SslClientAuthMode.REQUIRED);
            engine.setWantClientAuth(value == SslClientAuthMode.REQUESTED);
         }

         return var4;
      } else if (option == Options.SECURE) {
         throw new IllegalArgumentException();
      } else {
         return this.connection.setOption(option, value);
      }
   }

   public <T> T getOption(Option<T> option) throws IOException {
      if (option == Options.SSL_CLIENT_AUTH_MODE) {
         SSLEngine engine = this.sslConduitEngine.getEngine();
         return option.cast(engine.getNeedClientAuth() ? SslClientAuthMode.REQUIRED : (engine.getWantClientAuth() ? SslClientAuthMode.REQUESTED : SslClientAuthMode.NOT_REQUESTED));
      } else {
         return option == Options.SECURE ? option.cast(this.tls) : this.connection.getOption(option);
      }
   }

   public boolean supportsOption(Option<?> option) {
      return SUPPORTED_OPTIONS.contains(option) || this.connection.supportsOption(option);
   }

   public SSLSession getSslSession() {
      return this.tls ? this.sslConduitEngine.getSession() : null;
   }

   public ChannelListener.Setter<? extends SslConnection> getHandshakeSetter() {
      return this.handshakeSetter;
   }

   SSLEngine getEngine() {
      return this.sslConduitEngine.getEngine();
   }

   protected boolean readClosed() {
      boolean closeRequestedNow;
      synchronized(this) {
         int oldVal;
         int newVal;
         do {
            oldVal = this.state;
            if (Bits.allAreSet(oldVal, 1)) {
               break;
            }

            newVal = oldVal | 1;
         } while(!stateUpdater.compareAndSet(this, oldVal, newVal));

         closeRequestedNow = Bits.allAreSet(oldVal, 1);
         if (!this.sslConduitEngine.isClosed() && this.tls) {
            return closeRequestedNow;
         }

         do {
            oldVal = this.state;
            if (Bits.allAreSet(oldVal, 4)) {
               return false;
            }

            newVal = oldVal | 4;
         } while(!stateUpdater.compareAndSet(this, oldVal, newVal));

         if (Bits.allAreSet(oldVal, 4)) {
            return false;
         }
      }

      super.readClosed();
      return closeRequestedNow;
   }

   protected boolean writeClosed() {
      boolean closeRequestedNow;
      synchronized(this) {
         int oldVal;
         int newVal;
         do {
            oldVal = this.state;
            if (Bits.allAreSet(oldVal, 2)) {
               break;
            }

            newVal = oldVal | 2;
         } while(!stateUpdater.compareAndSet(this, oldVal, newVal));

         closeRequestedNow = Bits.allAreSet(oldVal, 2);
         if (!this.sslConduitEngine.isClosed() && this.tls) {
            return closeRequestedNow;
         }

         do {
            oldVal = this.state;
            if (Bits.allAreSet(oldVal, 8)) {
               return false;
            }

            newVal = oldVal | 8;
         } while(!stateUpdater.compareAndSet(this, oldVal, newVal));

         if (Bits.allAreSet(oldVal, 8)) {
            return false;
         }
      }

      super.writeClosed();
      return closeRequestedNow;
   }

   protected void handleHandshakeFinished() {
      ChannelListener<? super SslConnection> listener = this.handshakeSetter.get();
      if (listener != null) {
         ChannelListeners.invokeChannelListener(this, listener);
      }
   }

   public boolean isReadShutdown() {
      return Bits.allAreSet(this.state, 1);
   }

   public boolean isWriteShutdown() {
      return Bits.allAreSet(this.state, 2);
   }

   static {
      SUPPORTED_OPTIONS = Option.setBuilder().add(Options.SECURE, Options.SSL_CLIENT_AUTH_MODE).create();
   }
}
