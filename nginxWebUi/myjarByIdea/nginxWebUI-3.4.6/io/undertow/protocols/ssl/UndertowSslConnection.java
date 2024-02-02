package io.undertow.protocols.ssl;

import io.undertow.connector.ByteBufferPool;
import java.io.IOException;
import java.net.SocketAddress;
import java.util.Set;
import java.util.concurrent.Executor;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLSession;
import org.xnio.ChannelListener;
import org.xnio.ChannelListeners;
import org.xnio.Option;
import org.xnio.Options;
import org.xnio.SslClientAuthMode;
import org.xnio.StreamConnection;
import org.xnio.ssl.SslConnection;

class UndertowSslConnection extends SslConnection {
   private static final Set<Option<?>> SUPPORTED_OPTIONS;
   private final StreamConnection delegate;
   private final SslConduit sslConduit;
   private final ChannelListener.SimpleSetter<SslConnection> handshakeSetter = new ChannelListener.SimpleSetter();
   private final SSLEngine engine;

   UndertowSslConnection(StreamConnection delegate, SSLEngine engine, ByteBufferPool bufferPool, Executor delegatedTaskExecutor) {
      super(delegate.getIoThread());
      this.delegate = delegate;
      this.engine = engine;
      this.sslConduit = new SslConduit(this, delegate, engine, delegatedTaskExecutor, bufferPool, new HandshakeCallback());
      this.setSourceConduit(this.sslConduit);
      this.setSinkConduit(this.sslConduit);
   }

   public void startHandshake() throws IOException {
      this.sslConduit.startHandshake();
   }

   public SSLSession getSslSession() {
      return this.sslConduit.getSslSession();
   }

   public ChannelListener.Setter<? extends SslConnection> getHandshakeSetter() {
      return this.handshakeSetter;
   }

   protected void notifyWriteClosed() {
      try {
         this.sslConduit.notifyWriteClosed();
      } finally {
         super.notifyWriteClosed();
      }

   }

   protected void notifyReadClosed() {
      try {
         this.sslConduit.notifyReadClosed();
      } finally {
         super.notifyReadClosed();
      }

   }

   public SocketAddress getPeerAddress() {
      return this.delegate.getPeerAddress();
   }

   public SocketAddress getLocalAddress() {
      return this.delegate.getLocalAddress();
   }

   public SSLEngine getSSLEngine() {
      return this.sslConduit.getSSLEngine();
   }

   SslConduit getSslConduit() {
      return this.sslConduit;
   }

   public <T> T setOption(Option<T> option, T value) throws IllegalArgumentException, IOException {
      if (option == Options.SSL_CLIENT_AUTH_MODE) {
         Object var3;
         try {
            var3 = option.cast(this.engine.getNeedClientAuth() ? SslClientAuthMode.REQUIRED : (this.engine.getWantClientAuth() ? SslClientAuthMode.REQUESTED : SslClientAuthMode.NOT_REQUESTED));
         } finally {
            this.engine.setWantClientAuth(false);
            this.engine.setNeedClientAuth(false);
            if (value == SslClientAuthMode.REQUESTED) {
               this.engine.setWantClientAuth(true);
            } else if (value == SslClientAuthMode.REQUIRED) {
               this.engine.setNeedClientAuth(true);
            }

         }

         return var3;
      } else if (option == Options.SECURE) {
         throw new IllegalArgumentException();
      } else {
         return this.delegate.setOption(option, value);
      }
   }

   public <T> T getOption(Option<T> option) throws IOException {
      if (option == Options.SSL_CLIENT_AUTH_MODE) {
         return option.cast(this.engine.getNeedClientAuth() ? SslClientAuthMode.REQUIRED : (this.engine.getWantClientAuth() ? SslClientAuthMode.REQUESTED : SslClientAuthMode.NOT_REQUESTED));
      } else {
         return option == Options.SECURE ? Boolean.TRUE : this.delegate.getOption(option);
      }
   }

   public boolean supportsOption(Option<?> option) {
      return SUPPORTED_OPTIONS.contains(option) || this.delegate.supportsOption(option);
   }

   protected boolean readClosed() {
      return super.readClosed();
   }

   protected boolean writeClosed() {
      return super.writeClosed();
   }

   protected void closeAction() {
      this.sslConduit.close();
   }

   static {
      SUPPORTED_OPTIONS = Option.setBuilder().add(Options.SECURE, Options.SSL_CLIENT_AUTH_MODE).create();
   }

   private final class HandshakeCallback implements Runnable {
      private HandshakeCallback() {
      }

      public void run() {
         ChannelListener<? super SslConnection> listener = UndertowSslConnection.this.handshakeSetter.get();
         if (listener != null) {
            ChannelListeners.invokeChannelListener(UndertowSslConnection.this, listener);
         }
      }

      // $FF: synthetic method
      HandshakeCallback(Object x1) {
         this();
      }
   }
}
