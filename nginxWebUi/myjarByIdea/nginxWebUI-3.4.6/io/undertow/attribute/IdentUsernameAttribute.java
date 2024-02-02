package io.undertow.attribute;

import io.undertow.server.HttpServerExchange;

public class IdentUsernameAttribute implements ExchangeAttribute {
   public static final String IDENT_USERNAME = "%l";
   public static final ExchangeAttribute INSTANCE = new IdentUsernameAttribute();

   private IdentUsernameAttribute() {
   }

   public String readAttribute(HttpServerExchange exchange) {
      return null;
   }

   public void writeAttribute(HttpServerExchange exchange, String newValue) throws ReadOnlyAttributeException {
      throw new ReadOnlyAttributeException("Ident username", newValue);
   }

   public String toString() {
      return "%l";
   }

   public static final class Builder implements ExchangeAttributeBuilder {
      public String name() {
         return "Ident Username";
      }

      public ExchangeAttribute build(String token) {
         return token.equals("%l") ? IdentUsernameAttribute.INSTANCE : null;
      }

      public int priority() {
         return 0;
      }
   }
}
