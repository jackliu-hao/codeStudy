package org.xnio.ssl;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.util.Set;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLSession;
import org.xnio.ChannelListener;
import org.xnio.ChannelListeners;
import org.xnio.Option;
import org.xnio.Options;
import org.xnio.Pool;
import org.xnio.SslClientAuthMode;
import org.xnio.StreamConnection;

public final class JsseSslConnection extends SslConnection {
   private final StreamConnection streamConnection;
   private final JsseStreamConduit conduit;
   private final ChannelListener.SimpleSetter<SslConnection> handshakeSetter;
   private static final Set<Option<?>> SUPPORTED_OPTIONS;

   public JsseSslConnection(StreamConnection streamConnection, SSLEngine engine) {
      this(streamConnection, engine, JsseXnioSsl.bufferPool, JsseXnioSsl.bufferPool);
   }

   JsseSslConnection(StreamConnection streamConnection, SSLEngine engine, Pool<ByteBuffer> socketBufferPool, Pool<ByteBuffer> applicationBufferPool) {
      super(streamConnection.getIoThread());
      this.handshakeSetter = new ChannelListener.SimpleSetter();
      this.streamConnection = streamConnection;
      this.conduit = new JsseStreamConduit(this, engine, streamConnection.getSourceChannel().getConduit(), streamConnection.getSinkChannel().getConduit(), socketBufferPool, applicationBufferPool);
      this.setSourceConduit(this.conduit);
      this.setSinkConduit(this.conduit);
   }

   public void startHandshake() throws IOException {
      this.conduit.beginHandshake();
   }

   public SSLSession getSslSession() {
      return this.conduit.getSslSession();
   }

   protected void closeAction() throws IOException {
      try {
         if (!this.conduit.isWriteShutdown()) {
            this.conduit.terminateWrites();
         }

         if (!this.conduit.isReadShutdown()) {
            this.conduit.terminateReads();
         }

         this.conduit.flush();
         this.conduit.markTerminated();
         this.streamConnection.close();
      } catch (Throwable var5) {
         try {
            if (!this.conduit.isReadShutdown()) {
               this.conduit.terminateReads();
            }
         } catch (Throwable var4) {
         }

         try {
            this.conduit.markTerminated();
            this.streamConnection.close();
         } catch (Throwable var3) {
         }

         throw var5;
      }
   }

   public SocketAddress getPeerAddress() {
      return this.streamConnection.getPeerAddress();
   }

   public SocketAddress getLocalAddress() {
      return this.streamConnection.getLocalAddress();
   }

   public ChannelListener.Setter<? extends SslConnection> getHandshakeSetter() {
      return this.handshakeSetter;
   }

   void invokeHandshakeListener() {
      ChannelListeners.invokeChannelListener(this, this.handshakeSetter.get());
   }

   public <T> T setOption(Option<T> option, T value) throws IllegalArgumentException, IOException {
      if (option == Options.SSL_CLIENT_AUTH_MODE) {
         SSLEngine engine = this.conduit.getEngine();

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
         return this.streamConnection.setOption(option, value);
      }
   }

   public <T> T getOption(Option<T> option) throws IOException {
      if (option == Options.SSL_CLIENT_AUTH_MODE) {
         SSLEngine engine = this.conduit.getEngine();
         return option.cast(engine.getNeedClientAuth() ? SslClientAuthMode.REQUIRED : (engine.getWantClientAuth() ? SslClientAuthMode.REQUESTED : SslClientAuthMode.NOT_REQUESTED));
      } else {
         return option == Options.SECURE ? option.cast(this.conduit.isTls()) : this.streamConnection.getOption(option);
      }
   }

   public boolean supportsOption(Option<?> option) {
      return SUPPORTED_OPTIONS.contains(option) || this.streamConnection.supportsOption(option);
   }

   public boolean isOpen() {
      return this.streamConnection.isOpen();
   }

   public boolean isWriteShutdown() {
      return this.streamConnection.isWriteShutdown();
   }

   public boolean isReadShutdown() {
      return this.streamConnection.isReadShutdown();
   }

   public SSLEngine getEngine() {
      return this.conduit.getEngine();
   }

   static {
      SUPPORTED_OPTIONS = Option.setBuilder().add(Options.SECURE, Options.SSL_CLIENT_AUTH_MODE).create();
   }
}
