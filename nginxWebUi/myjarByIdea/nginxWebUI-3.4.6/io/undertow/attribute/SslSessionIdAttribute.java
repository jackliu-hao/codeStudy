package io.undertow.attribute;

import io.undertow.server.HttpServerExchange;
import io.undertow.server.SSLSessionInfo;
import io.undertow.util.HexConverter;

public class SslSessionIdAttribute implements ExchangeAttribute {
   public static final SslSessionIdAttribute INSTANCE = new SslSessionIdAttribute();

   public String readAttribute(HttpServerExchange exchange) {
      SSLSessionInfo ssl = exchange.getConnection().getSslSessionInfo();
      return ssl != null && ssl.getSessionId() != null ? HexConverter.convertToHexString(ssl.getSessionId()) : null;
   }

   public void writeAttribute(HttpServerExchange exchange, String newValue) throws ReadOnlyAttributeException {
      throw new ReadOnlyAttributeException("SSL Session ID", newValue);
   }

   public String toString() {
      return "%{SSL_SESSION_ID}";
   }

   public static final class Builder implements ExchangeAttributeBuilder {
      public String name() {
         return "SSL Session ID";
      }

      public ExchangeAttribute build(String token) {
         return token.equals("%{SSL_SESSION_ID}") ? SslSessionIdAttribute.INSTANCE : null;
      }

      public int priority() {
         return 0;
      }
   }
}
