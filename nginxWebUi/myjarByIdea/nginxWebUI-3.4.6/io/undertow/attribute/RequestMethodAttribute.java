package io.undertow.attribute;

import io.undertow.server.HttpServerExchange;

public class RequestMethodAttribute implements ExchangeAttribute {
   public static final String REQUEST_METHOD_SHORT = "%m";
   public static final String REQUEST_METHOD = "%{METHOD}";
   public static final ExchangeAttribute INSTANCE = new RequestMethodAttribute();

   private RequestMethodAttribute() {
   }

   public String readAttribute(HttpServerExchange exchange) {
      return exchange.getRequestMethod().toString();
   }

   public void writeAttribute(HttpServerExchange exchange, String newValue) throws ReadOnlyAttributeException {
      throw new ReadOnlyAttributeException("Request method", newValue);
   }

   public String toString() {
      return "%{METHOD}";
   }

   public static final class Builder implements ExchangeAttributeBuilder {
      public String name() {
         return "Request method";
      }

      public ExchangeAttribute build(String token) {
         return !token.equals("%{METHOD}") && !token.equals("%m") ? null : RequestMethodAttribute.INSTANCE;
      }

      public int priority() {
         return 0;
      }
   }
}
