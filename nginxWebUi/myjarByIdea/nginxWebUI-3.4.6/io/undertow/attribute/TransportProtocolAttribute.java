package io.undertow.attribute;

import io.undertow.server.HttpServerExchange;

public class TransportProtocolAttribute implements ExchangeAttribute {
   public static final String TRANSPORT_PROTOCOL = "%{TRANSPORT_PROTOCOL}";
   public static final ExchangeAttribute INSTANCE = new TransportProtocolAttribute();

   private TransportProtocolAttribute() {
   }

   public String readAttribute(HttpServerExchange exchange) {
      return exchange.getConnection().getTransportProtocol();
   }

   public void writeAttribute(HttpServerExchange exchange, String newValue) throws ReadOnlyAttributeException {
      throw new ReadOnlyAttributeException("transport protocol", newValue);
   }

   public String toString() {
      return "%{TRANSPORT_PROTOCOL}";
   }

   public static final class Builder implements ExchangeAttributeBuilder {
      public String name() {
         return "Transport Protocol";
      }

      public ExchangeAttribute build(String token) {
         return token.equals("%{TRANSPORT_PROTOCOL}") ? TransportProtocolAttribute.INSTANCE : null;
      }

      public int priority() {
         return 0;
      }
   }
}
