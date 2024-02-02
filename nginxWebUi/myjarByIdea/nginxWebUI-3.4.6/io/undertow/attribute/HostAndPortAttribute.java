package io.undertow.attribute;

import io.undertow.server.HttpServerExchange;

public class HostAndPortAttribute implements ExchangeAttribute {
   public static final String HOST_AND_PORT = "%{HOST_AND_PORT}";
   public static final ExchangeAttribute INSTANCE = new HostAndPortAttribute();

   private HostAndPortAttribute() {
   }

   public String readAttribute(HttpServerExchange exchange) {
      return exchange.getHostAndPort();
   }

   public void writeAttribute(HttpServerExchange exchange, String newValue) throws ReadOnlyAttributeException {
      throw new ReadOnlyAttributeException("Host and Port", newValue);
   }

   public String toString() {
      return "%{HOST_AND_PORT}";
   }

   public static final class Builder implements ExchangeAttributeBuilder {
      public String name() {
         return "Host and Port";
      }

      public ExchangeAttribute build(String token) {
         return token.equals("%{HOST_AND_PORT}") ? HostAndPortAttribute.INSTANCE : null;
      }

      public int priority() {
         return 0;
      }
   }
}
