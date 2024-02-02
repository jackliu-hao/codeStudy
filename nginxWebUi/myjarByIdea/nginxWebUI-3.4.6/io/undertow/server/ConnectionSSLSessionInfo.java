package io.undertow.server;

import io.undertow.UndertowMessages;
import io.undertow.UndertowOptions;
import io.undertow.connector.PooledByteBuffer;
import io.undertow.server.protocol.http.HttpServerConnection;
import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.cert.Certificate;
import java.util.concurrent.TimeUnit;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSession;
import javax.security.cert.X509Certificate;
import org.xnio.ChannelListener;
import org.xnio.IoUtils;
import org.xnio.Options;
import org.xnio.SslClientAuthMode;
import org.xnio.channels.Channels;
import org.xnio.channels.SslChannel;
import org.xnio.channels.StreamSourceChannel;

public class ConnectionSSLSessionInfo implements SSLSessionInfo {
   private static final SSLPeerUnverifiedException PEER_UNVERIFIED_EXCEPTION = new SSLPeerUnverifiedException("");
   private static final RenegotiationRequiredException RENEGOTIATION_REQUIRED_EXCEPTION = new RenegotiationRequiredException();
   private static final long MAX_RENEGOTIATION_WAIT = 30000L;
   private final SslChannel channel;
   private final HttpServerConnection serverConnection;
   private SSLPeerUnverifiedException unverified;
   private RenegotiationRequiredException renegotiationRequiredException;

   public ConnectionSSLSessionInfo(SslChannel channel, HttpServerConnection serverConnection) {
      this.channel = channel;
      this.serverConnection = serverConnection;
   }

   public byte[] getSessionId() {
      return this.channel.getSslSession().getId();
   }

   public String getCipherSuite() {
      return this.channel.getSslSession().getCipherSuite();
   }

   public Certificate[] getPeerCertificates() throws SSLPeerUnverifiedException, RenegotiationRequiredException {
      if (this.unverified != null) {
         throw this.unverified;
      } else if (this.renegotiationRequiredException != null) {
         throw this.renegotiationRequiredException;
      } else {
         try {
            return this.channel.getSslSession().getPeerCertificates();
         } catch (SSLPeerUnverifiedException var4) {
            try {
               SslClientAuthMode sslClientAuthMode = (SslClientAuthMode)this.channel.getOption(Options.SSL_CLIENT_AUTH_MODE);
               if (sslClientAuthMode == SslClientAuthMode.NOT_REQUESTED) {
                  this.renegotiationRequiredException = RENEGOTIATION_REQUIRED_EXCEPTION;
                  throw this.renegotiationRequiredException;
               }
            } catch (IOException var3) {
            }

            this.unverified = PEER_UNVERIFIED_EXCEPTION;
            throw this.unverified;
         }
      }
   }

   public X509Certificate[] getPeerCertificateChain() throws SSLPeerUnverifiedException, RenegotiationRequiredException {
      if (this.unverified != null) {
         throw this.unverified;
      } else if (this.renegotiationRequiredException != null) {
         throw this.renegotiationRequiredException;
      } else {
         try {
            return this.channel.getSslSession().getPeerCertificateChain();
         } catch (SSLPeerUnverifiedException var4) {
            try {
               SslClientAuthMode sslClientAuthMode = (SslClientAuthMode)this.channel.getOption(Options.SSL_CLIENT_AUTH_MODE);
               if (sslClientAuthMode == SslClientAuthMode.NOT_REQUESTED) {
                  this.renegotiationRequiredException = RENEGOTIATION_REQUIRED_EXCEPTION;
                  throw this.renegotiationRequiredException;
               }
            } catch (IOException var3) {
            }

            this.unverified = PEER_UNVERIFIED_EXCEPTION;
            throw this.unverified;
         }
      }
   }

   public void renegotiate(HttpServerExchange exchange, SslClientAuthMode sslClientAuthMode) throws IOException {
      if ("TLSv1.3".equals(this.channel.getSslSession().getProtocol())) {
         throw UndertowMessages.MESSAGES.renegotiationNotSupported();
      } else {
         this.unverified = null;
         this.renegotiationRequiredException = null;
         if (exchange.isRequestComplete()) {
            this.renegotiateNoRequest(exchange, sslClientAuthMode);
         } else {
            this.renegotiateBufferRequest(exchange, sslClientAuthMode);
         }

      }
   }

   public SSLSession getSSLSession() {
      return this.channel.getSslSession();
   }

   public void renegotiateBufferRequest(HttpServerExchange exchange, SslClientAuthMode newAuthMode) throws IOException {
      int maxSize = exchange.getConnection().getUndertowOptions().get(UndertowOptions.MAX_BUFFERED_REQUEST_SIZE, 16384);
      if (maxSize <= 0) {
         throw new SSLPeerUnverifiedException("");
      } else {
         boolean requestResetRequired = false;
         StreamSourceChannel requestChannel = Connectors.getExistingRequestChannel(exchange);
         if (requestChannel == null) {
            requestChannel = exchange.getRequestChannel();
            requestResetRequired = true;
         }

         PooledByteBuffer pooled = exchange.getConnection().getByteBufferPool().allocate();
         boolean free = true;
         int usedBuffers = 0;
         PooledByteBuffer[] poolArray = null;
         int bufferSize = pooled.getBuffer().remaining();
         int allowedBuffers = (maxSize + bufferSize - 1) / bufferSize;
         poolArray = new PooledByteBuffer[allowedBuffers];
         poolArray[usedBuffers++] = pooled;
         boolean overflow = false;

         while(true) {
            boolean var23 = false;

            try {
               var23 = true;
               ByteBuffer buf = pooled.getBuffer();
               int res = Channels.readBlocking(requestChannel, buf);
               if (!buf.hasRemaining()) {
                  buf.flip();
                  if (allowedBuffers != usedBuffers) {
                     pooled = exchange.getConnection().getByteBufferPool().allocate();
                     poolArray[usedBuffers++] = pooled;
                     continue;
                  }

                  overflow = true;
               } else {
                  if (res != -1) {
                     continue;
                  }

                  buf.flip();
               }

               free = false;
               Connectors.ungetRequestBytes(exchange, poolArray);
               if (overflow) {
                  throw new SSLPeerUnverifiedException("Cannot renegotiate");
               }

               this.renegotiateNoRequest(exchange, newAuthMode);
               var23 = false;
            } finally {
               if (var23) {
                  if (free) {
                     PooledByteBuffer[] var18 = poolArray;
                     int var19 = poolArray.length;

                     for(int var20 = 0; var20 < var19; ++var20) {
                        PooledByteBuffer buf = var18[var20];
                        if (buf != null) {
                           buf.close();
                        }
                     }
                  }

                  if (requestResetRequired) {
                     exchange.requestChannel = null;
                  }

               }
            }

            if (free) {
               PooledByteBuffer[] var25 = poolArray;
               int var26 = poolArray.length;

               for(int var15 = 0; var15 < var26; ++var15) {
                  PooledByteBuffer buf = var25[var15];
                  if (buf != null) {
                     buf.close();
                  }
               }
            }

            if (requestResetRequired) {
               exchange.requestChannel = null;
            }

            return;
         }
      }
   }

   public void renegotiateNoRequest(HttpServerExchange exchange, SslClientAuthMode newAuthMode) throws IOException {
      AbstractServerConnection.ConduitState oldState = this.serverConnection.resetChannel();

      try {
         SslClientAuthMode sslClientAuthMode = (SslClientAuthMode)this.channel.getOption(Options.SSL_CLIENT_AUTH_MODE);
         if (sslClientAuthMode == SslClientAuthMode.NOT_REQUESTED) {
            SslHandshakeWaiter waiter = new SslHandshakeWaiter();
            this.channel.getHandshakeSetter().set(waiter);
            this.channel.setOption(Options.SSL_CLIENT_AUTH_MODE, newAuthMode);
            this.channel.getSslSession().invalidate();
            this.channel.startHandshake();
            this.serverConnection.getOriginalSinkConduit().flush();
            ByteBuffer buff = ByteBuffer.wrap(new byte[1]);
            long end = System.currentTimeMillis() + 30000L;

            while(!waiter.isDone() && this.serverConnection.isOpen() && System.currentTimeMillis() < end) {
               int read = this.serverConnection.getSourceChannel().read(buff);
               if (read != 0) {
                  throw new SSLPeerUnverifiedException("");
               }

               if (!waiter.isDone()) {
                  this.serverConnection.getSourceChannel().awaitReadable(end - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
               }
            }

            if (!waiter.isDone()) {
               if (this.serverConnection.isOpen()) {
                  IoUtils.safeClose((Closeable)this.serverConnection);
                  throw UndertowMessages.MESSAGES.rengotiationTimedOut();
               }

               IoUtils.safeClose((Closeable)this.serverConnection);
               throw UndertowMessages.MESSAGES.rengotiationFailed();
            }
         }
      } finally {
         if (oldState != null) {
            this.serverConnection.restoreChannel(oldState);
         }

      }

   }

   private static class SslHandshakeWaiter implements ChannelListener<SslChannel> {
      private volatile boolean done;

      private SslHandshakeWaiter() {
         this.done = false;
      }

      boolean isDone() {
         return this.done;
      }

      public void handleEvent(SslChannel channel) {
         this.done = true;
      }

      // $FF: synthetic method
      SslHandshakeWaiter(Object x0) {
         this();
      }
   }
}
