package io.undertow.attribute;

import io.undertow.security.api.SecurityContext;
import io.undertow.server.HttpServerExchange;

public class AuthenticationTypeExchangeAttribute implements ExchangeAttribute {
   public static final String TOKEN = "%{AUTHENTICATION_TYPE}";
   public static final ExchangeAttribute INSTANCE = new AuthenticationTypeExchangeAttribute();

   public String readAttribute(HttpServerExchange exchange) {
      SecurityContext sc = exchange.getSecurityContext();
      return sc == null ? null : sc.getMechanismName();
   }

   public void writeAttribute(HttpServerExchange exchange, String newValue) throws ReadOnlyAttributeException {
      throw new ReadOnlyAttributeException("Authentication Type", newValue);
   }

   public String toString() {
      return "%{AUTHENTICATION_TYPE}";
   }

   public static final class Builder implements ExchangeAttributeBuilder {
      public String name() {
         return "Authentication Type";
      }

      public ExchangeAttribute build(String token) {
         return token.equals("%{AUTHENTICATION_TYPE}") ? AuthenticationTypeExchangeAttribute.INSTANCE : null;
      }

      public int priority() {
         return 0;
      }
   }
}
