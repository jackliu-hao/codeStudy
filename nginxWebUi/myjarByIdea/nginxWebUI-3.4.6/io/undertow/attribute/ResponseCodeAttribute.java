package io.undertow.attribute;

import io.undertow.server.HttpServerExchange;

public class ResponseCodeAttribute implements ExchangeAttribute {
   public static final String RESPONSE_CODE_SHORT = "%s";
   public static final String RESPONSE_CODE = "%{RESPONSE_CODE}";
   public static final ExchangeAttribute INSTANCE = new ResponseCodeAttribute();

   private ResponseCodeAttribute() {
   }

   public String readAttribute(HttpServerExchange exchange) {
      return Integer.toString(exchange.getStatusCode());
   }

   public void writeAttribute(HttpServerExchange exchange, String newValue) throws ReadOnlyAttributeException {
      exchange.setStatusCode(Integer.parseInt(newValue));
   }

   public String toString() {
      return "%{RESPONSE_CODE}";
   }

   public static final class Builder implements ExchangeAttributeBuilder {
      public String name() {
         return "Response code";
      }

      public ExchangeAttribute build(String token) {
         return !token.equals("%{RESPONSE_CODE}") && !token.equals("%s") ? null : ResponseCodeAttribute.INSTANCE;
      }

      public int priority() {
         return 0;
      }
   }
}
