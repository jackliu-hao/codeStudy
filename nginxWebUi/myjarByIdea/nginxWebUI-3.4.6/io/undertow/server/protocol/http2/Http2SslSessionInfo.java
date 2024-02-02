package io.undertow.server.protocol.http2;

import io.undertow.UndertowMessages;
import io.undertow.protocols.http2.Http2Channel;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.RenegotiationRequiredException;
import io.undertow.server.SSLSessionInfo;
import java.io.IOException;
import java.security.cert.Certificate;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSession;
import javax.security.cert.X509Certificate;
import org.xnio.Options;
import org.xnio.SslClientAuthMode;

class Http2SslSessionInfo implements SSLSessionInfo {
   private final Http2Channel channel;

   Http2SslSessionInfo(Http2Channel channel) {
      this.channel = channel;
   }

   public byte[] getSessionId() {
      return this.channel.getSslSession().getId();
   }

   public String getCipherSuite() {
      return this.channel.getSslSession().getCipherSuite();
   }

   public Certificate[] getPeerCertificates() throws SSLPeerUnverifiedException, RenegotiationRequiredException {
      try {
         return this.channel.getSslSession().getPeerCertificates();
      } catch (SSLPeerUnverifiedException var4) {
         try {
            SslClientAuthMode sslClientAuthMode = (SslClientAuthMode)this.channel.getOption(Options.SSL_CLIENT_AUTH_MODE);
            if (sslClientAuthMode == SslClientAuthMode.NOT_REQUESTED) {
               throw new RenegotiationRequiredException();
            }
         } catch (IOException var3) {
         }

         throw var4;
      }
   }

   public X509Certificate[] getPeerCertificateChain() throws SSLPeerUnverifiedException, RenegotiationRequiredException {
      try {
         return this.channel.getSslSession().getPeerCertificateChain();
      } catch (SSLPeerUnverifiedException var4) {
         try {
            SslClientAuthMode sslClientAuthMode = (SslClientAuthMode)this.channel.getOption(Options.SSL_CLIENT_AUTH_MODE);
            if (sslClientAuthMode == SslClientAuthMode.NOT_REQUESTED) {
               throw new RenegotiationRequiredException();
            }
         } catch (IOException var3) {
         }

         throw var4;
      }
   }

   public void renegotiate(HttpServerExchange exchange, SslClientAuthMode sslClientAuthMode) throws IOException {
      throw UndertowMessages.MESSAGES.renegotiationNotSupported();
   }

   public SSLSession getSSLSession() {
      return this.channel.getSslSession();
   }
}
