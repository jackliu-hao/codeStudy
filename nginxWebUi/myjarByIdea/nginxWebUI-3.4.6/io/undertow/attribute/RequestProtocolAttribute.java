package io.undertow.attribute;

import io.undertow.server.HttpServerExchange;

public class RequestProtocolAttribute implements ExchangeAttribute {
   public static final String REQUEST_PROTOCOL_SHORT = "%H";
   public static final String REQUEST_PROTOCOL = "%{PROTOCOL}";
   public static final ExchangeAttribute INSTANCE = new RequestProtocolAttribute();

   private RequestProtocolAttribute() {
   }

   public String readAttribute(HttpServerExchange exchange) {
      return exchange.getProtocol().toString();
   }

   public void writeAttribute(HttpServerExchange exchange, String newValue) throws ReadOnlyAttributeException {
      throw new ReadOnlyAttributeException("Request protocol", newValue);
   }

   public String toString() {
      return "%{PROTOCOL}";
   }

   public static final class Builder implements ExchangeAttributeBuilder {
      public String name() {
         return "Request protocol";
      }

      public ExchangeAttribute build(String token) {
         return !token.equals("%{PROTOCOL}") && !token.equals("%H") ? null : RequestProtocolAttribute.INSTANCE;
      }

      public int priority() {
         return 0;
      }
   }
}
