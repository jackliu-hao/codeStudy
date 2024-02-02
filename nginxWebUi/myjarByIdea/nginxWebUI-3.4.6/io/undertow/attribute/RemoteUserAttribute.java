package io.undertow.attribute;

import io.undertow.security.api.SecurityContext;
import io.undertow.server.HttpServerExchange;

public class RemoteUserAttribute implements ExchangeAttribute {
   public static final String REMOTE_USER_SHORT = "%u";
   public static final String REMOTE_USER = "%{REMOTE_USER}";
   public static final ExchangeAttribute INSTANCE = new RemoteUserAttribute();

   private RemoteUserAttribute() {
   }

   public String readAttribute(HttpServerExchange exchange) {
      SecurityContext sc = exchange.getSecurityContext();
      return sc != null && sc.isAuthenticated() ? sc.getAuthenticatedAccount().getPrincipal().getName() : null;
   }

   public void writeAttribute(HttpServerExchange exchange, String newValue) throws ReadOnlyAttributeException {
      throw new ReadOnlyAttributeException("Remote user", newValue);
   }

   public String toString() {
      return "%{REMOTE_USER}";
   }

   public static final class Builder implements ExchangeAttributeBuilder {
      public String name() {
         return "Remote user";
      }

      public ExchangeAttribute build(String token) {
         return !token.equals("%{REMOTE_USER}") && !token.equals("%u") ? null : RemoteUserAttribute.INSTANCE;
      }

      public int priority() {
         return 0;
      }
   }
}
