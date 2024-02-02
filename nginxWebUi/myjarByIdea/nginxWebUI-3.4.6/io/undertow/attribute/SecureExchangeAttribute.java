package io.undertow.attribute;

import io.undertow.server.HttpServerExchange;

public class SecureExchangeAttribute implements ExchangeAttribute {
   public static final String TOKEN = "%{SECURE}";
   public static final String LEGACY_INCORRECT_TOKEN = "${SECURE}";
   public static final ExchangeAttribute INSTANCE = new SecureExchangeAttribute();

   public String readAttribute(HttpServerExchange exchange) {
      return Boolean.toString(exchange.isSecure());
   }

   public void writeAttribute(HttpServerExchange exchange, String newValue) throws ReadOnlyAttributeException {
      exchange.putAttachment(HttpServerExchange.SECURE_REQUEST, Boolean.parseBoolean(newValue));
   }

   public String toString() {
      return "%{SECURE}";
   }

   public static class Builder implements ExchangeAttributeBuilder {
      public String name() {
         return "Secure";
      }

      public ExchangeAttribute build(String token) {
         return !token.equals("%{SECURE}") && !token.equals("${SECURE}") ? null : SecureExchangeAttribute.INSTANCE;
      }

      public int priority() {
         return 0;
      }
   }
}
