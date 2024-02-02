package io.undertow.attribute;

import io.undertow.server.HttpServerExchange;
import io.undertow.server.RenegotiationRequiredException;
import io.undertow.server.SSLSessionInfo;
import io.undertow.util.Certificates;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import javax.net.ssl.SSLPeerUnverifiedException;

public class SslClientCertAttribute implements ExchangeAttribute {
   public static final SslClientCertAttribute INSTANCE = new SslClientCertAttribute();

   public String readAttribute(HttpServerExchange exchange) {
      SSLSessionInfo ssl = exchange.getConnection().getSslSessionInfo();
      if (ssl == null) {
         return null;
      } else {
         try {
            Certificate[] certificates = ssl.getPeerCertificates();
            return certificates.length > 0 ? Certificates.toPem(certificates[0]) : null;
         } catch (CertificateEncodingException | RenegotiationRequiredException | SSLPeerUnverifiedException var5) {
            return null;
         }
      }
   }

   public void writeAttribute(HttpServerExchange exchange, String newValue) throws ReadOnlyAttributeException {
      throw new ReadOnlyAttributeException("SSL Client Cert", newValue);
   }

   public String toString() {
      return "%{SSL_CLIENT_CERT}";
   }

   public static final class Builder implements ExchangeAttributeBuilder {
      public String name() {
         return "SSL Client Cert";
      }

      public ExchangeAttribute build(String token) {
         return token.equals("%{SSL_CLIENT_CERT}") ? SslClientCertAttribute.INSTANCE : null;
      }

      public int priority() {
         return 0;
      }
   }
}
