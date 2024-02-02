package io.undertow.server;

import java.io.IOException;
import java.security.cert.Certificate;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSession;
import javax.security.cert.X509Certificate;
import org.xnio.SslClientAuthMode;

public interface SSLSessionInfo {
   static int calculateKeySize(String cipherSuite) {
      if (cipherSuite == null) {
         return 0;
      } else if (cipherSuite.contains("WITH_AES_256_")) {
         return 256;
      } else if (cipherSuite.contains("WITH_RC4_128_")) {
         return 128;
      } else if (cipherSuite.contains("WITH_AES_128_")) {
         return 128;
      } else if (cipherSuite.contains("WITH_RC4_40_")) {
         return 40;
      } else if (cipherSuite.contains("WITH_3DES_EDE_CBC_")) {
         return 168;
      } else if (cipherSuite.contains("WITH_IDEA_CBC_")) {
         return 128;
      } else if (cipherSuite.contains("WITH_RC2_CBC_40_")) {
         return 40;
      } else if (cipherSuite.contains("WITH_DES40_CBC_")) {
         return 40;
      } else {
         return cipherSuite.contains("WITH_DES_CBC_") ? 56 : 0;
      }
   }

   byte[] getSessionId();

   String getCipherSuite();

   default int getKeySize() {
      return calculateKeySize(this.getCipherSuite());
   }

   Certificate[] getPeerCertificates() throws SSLPeerUnverifiedException, RenegotiationRequiredException;

   /** @deprecated */
   @Deprecated
   X509Certificate[] getPeerCertificateChain() throws SSLPeerUnverifiedException, RenegotiationRequiredException;

   void renegotiate(HttpServerExchange var1, SslClientAuthMode var2) throws IOException;

   SSLSession getSSLSession();
}
