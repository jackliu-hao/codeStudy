package io.undertow.server;

import io.undertow.UndertowMessages;
import io.undertow.util.HexConverter;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.Collection;
import java.util.Iterator;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSession;
import javax.security.cert.X509Certificate;
import org.xnio.SslClientAuthMode;

public class BasicSSLSessionInfo implements SSLSessionInfo {
   private final byte[] sessionId;
   private final String cypherSuite;
   private final Certificate[] peerCertificate;
   private final X509Certificate[] certificate;
   private final Integer keySize;

   public BasicSSLSessionInfo(byte[] sessionId, String cypherSuite, String certificate, Integer keySize) throws CertificateException, javax.security.cert.CertificateException {
      this.sessionId = sessionId;
      this.cypherSuite = cypherSuite;
      this.keySize = keySize;
      if (certificate != null) {
         CertificateFactory cf = CertificateFactory.getInstance("X.509");
         byte[] certificateBytes = certificate.getBytes(StandardCharsets.US_ASCII);
         ByteArrayInputStream stream = new ByteArrayInputStream(certificateBytes);
         Collection<? extends Certificate> certCol = cf.generateCertificates(stream);
         this.peerCertificate = new Certificate[certCol.size()];
         X509Certificate[] legacyCertificate = new X509Certificate[certCol.size()];
         int i = 0;

         for(Iterator var11 = certCol.iterator(); var11.hasNext(); ++i) {
            Certificate cert = (Certificate)var11.next();
            this.peerCertificate[i] = cert;
            if (legacyCertificate != null) {
               try {
                  legacyCertificate[i] = X509Certificate.getInstance(cert.getEncoded());
               } catch (javax.security.cert.CertificateException var14) {
                  legacyCertificate = null;
               }
            }
         }

         this.certificate = legacyCertificate;
      } else {
         this.peerCertificate = null;
         this.certificate = null;
      }

   }

   public BasicSSLSessionInfo(byte[] sessionId, String cypherSuite, String certificate) throws CertificateException, javax.security.cert.CertificateException {
      this((byte[])sessionId, cypherSuite, certificate, (Integer)null);
   }

   public BasicSSLSessionInfo(String sessionId, String cypherSuite, String certificate) throws CertificateException, javax.security.cert.CertificateException {
      this((byte[])(sessionId == null ? null : fromHex(sessionId)), cypherSuite, certificate, (Integer)null);
   }

   public BasicSSLSessionInfo(String sessionId, String cypherSuite, String certificate, Integer keySize) throws CertificateException, javax.security.cert.CertificateException {
      this(sessionId == null ? null : fromHex(sessionId), cypherSuite, certificate, keySize);
   }

   public byte[] getSessionId() {
      if (this.sessionId == null) {
         return null;
      } else {
         byte[] copy = new byte[this.sessionId.length];
         System.arraycopy(this.sessionId, 0, copy, 0, copy.length);
         return copy;
      }
   }

   public String getCipherSuite() {
      return this.cypherSuite;
   }

   public int getKeySize() {
      return this.keySize != null ? this.keySize : SSLSessionInfo.super.getKeySize();
   }

   public Certificate[] getPeerCertificates() throws SSLPeerUnverifiedException {
      if (this.peerCertificate == null) {
         throw UndertowMessages.MESSAGES.peerUnverified();
      } else {
         return this.peerCertificate;
      }
   }

   public X509Certificate[] getPeerCertificateChain() throws SSLPeerUnverifiedException {
      if (this.certificate == null) {
         throw UndertowMessages.MESSAGES.peerUnverified();
      } else {
         return this.certificate;
      }
   }

   public void renegotiate(HttpServerExchange exchange, SslClientAuthMode sslClientAuthMode) throws IOException {
      throw UndertowMessages.MESSAGES.renegotiationNotSupported();
   }

   public SSLSession getSSLSession() {
      return null;
   }

   private static byte[] fromHex(String sessionId) {
      try {
         return HexConverter.convertFromHex(sessionId);
      } catch (Exception var2) {
         return null;
      }
   }
}
