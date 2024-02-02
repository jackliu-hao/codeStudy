package io.undertow.attribute;

import io.undertow.server.HttpServerExchange;
import io.undertow.server.SSLSessionInfo;

public class SslCipherAttribute implements ExchangeAttribute {
   public static final SslCipherAttribute INSTANCE = new SslCipherAttribute();

   public String readAttribute(HttpServerExchange exchange) {
      SSLSessionInfo ssl = exchange.getConnection().getSslSessionInfo();
      return ssl == null ? null : ssl.getCipherSuite();
   }

   public void writeAttribute(HttpServerExchange exchange, String newValue) throws ReadOnlyAttributeException {
      throw new ReadOnlyAttributeException("SSL Cipher", newValue);
   }

   public String toString() {
      return "%{SSL_CIPHER}";
   }

   public static final class Builder implements ExchangeAttributeBuilder {
      public String name() {
         return "SSL Cipher";
      }

      public ExchangeAttribute build(String token) {
         return token.equals("%{SSL_CIPHER}") ? SslCipherAttribute.INSTANCE : null;
      }

      public int priority() {
         return 0;
      }
   }
}
