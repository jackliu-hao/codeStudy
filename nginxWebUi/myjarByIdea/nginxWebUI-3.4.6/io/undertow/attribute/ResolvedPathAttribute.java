package io.undertow.attribute;

import io.undertow.server.HttpServerExchange;

public class ResolvedPathAttribute implements ExchangeAttribute {
   public static final String RESOLVED_PATH = "%{RESOLVED_PATH}";
   public static final ExchangeAttribute INSTANCE = new ResolvedPathAttribute();

   private ResolvedPathAttribute() {
   }

   public String readAttribute(HttpServerExchange exchange) {
      return exchange.getResolvedPath();
   }

   public void writeAttribute(HttpServerExchange exchange, String newValue) throws ReadOnlyAttributeException {
      exchange.setResolvedPath(newValue);
   }

   public String toString() {
      return "%{RESOLVED_PATH}";
   }

   public static final class Builder implements ExchangeAttributeBuilder {
      public String name() {
         return "Resolved Path";
      }

      public ExchangeAttribute build(String token) {
         return token.equals("%{RESOLVED_PATH}") ? ResolvedPathAttribute.INSTANCE : null;
      }

      public int priority() {
         return 0;
      }
   }
}
