package io.undertow.attribute;

import io.undertow.server.HttpServerExchange;

public class SubstituteEmptyWrapper implements ExchangeAttributeWrapper {
   private final String substitute;

   public SubstituteEmptyWrapper(String substitute) {
      this.substitute = substitute;
   }

   public ExchangeAttribute wrap(ExchangeAttribute attribute) {
      return new SubstituteEmptyAttribute(attribute, this.substitute);
   }

   public static class SubstituteEmptyAttribute implements ExchangeAttribute {
      private final ExchangeAttribute attribute;
      private final String substitute;

      public SubstituteEmptyAttribute(ExchangeAttribute attribute, String substitute) {
         this.attribute = attribute;
         this.substitute = substitute;
      }

      public String readAttribute(HttpServerExchange exchange) {
         String val = this.attribute.readAttribute(exchange);
         return val != null && !val.isEmpty() ? val : this.substitute;
      }

      public void writeAttribute(HttpServerExchange exchange, String newValue) throws ReadOnlyAttributeException {
         this.attribute.writeAttribute(exchange, newValue);
      }
   }
}
