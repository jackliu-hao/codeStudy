package io.undertow.attribute;

import io.undertow.server.HttpServerExchange;

public class NullAttribute implements ExchangeAttribute {
   public static final String NAME = "%{NULL}";
   public static final NullAttribute INSTANCE = new NullAttribute();

   private NullAttribute() {
   }

   public String readAttribute(HttpServerExchange exchange) {
      return null;
   }

   public void writeAttribute(HttpServerExchange exchange, String newValue) throws ReadOnlyAttributeException {
      throw new ReadOnlyAttributeException("%{NULL}", newValue);
   }

   public String toString() {
      return "%{NULL}";
   }

   public static final class Builder implements ExchangeAttributeBuilder {
      public String name() {
         return "null";
      }

      public ExchangeAttribute build(String token) {
         return token.equals("%{NULL}") ? NullAttribute.INSTANCE : null;
      }

      public int priority() {
         return 0;
      }
   }
}
