package io.undertow.attribute;

import io.undertow.server.HttpServerExchange;

public class RequestSchemeAttribute implements ExchangeAttribute {
   public static final String REQUEST_SCHEME = "%{SCHEME}";
   public static final ExchangeAttribute INSTANCE = new RequestSchemeAttribute();

   private RequestSchemeAttribute() {
   }

   public String readAttribute(HttpServerExchange exchange) {
      return exchange.getRequestScheme();
   }

   public void writeAttribute(HttpServerExchange exchange, String newValue) throws ReadOnlyAttributeException {
      exchange.setRequestScheme(newValue);
   }

   public String toString() {
      return "%{SCHEME}";
   }

   public static final class Builder implements ExchangeAttributeBuilder {
      public String name() {
         return "Request scheme";
      }

      public ExchangeAttribute build(String token) {
         return token.equals("%{SCHEME}") ? RequestSchemeAttribute.INSTANCE : null;
      }

      public int priority() {
         return 0;
      }
   }
}
