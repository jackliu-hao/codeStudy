package io.undertow.attribute;

import io.undertow.server.HttpServerExchange;

public class ConstantExchangeAttribute implements ExchangeAttribute {
   private final String value;

   public ConstantExchangeAttribute(String value) {
      this.value = value;
   }

   public String readAttribute(HttpServerExchange exchange) {
      return this.value;
   }

   public void writeAttribute(HttpServerExchange exchange, String newValue) throws ReadOnlyAttributeException {
      throw new ReadOnlyAttributeException("constant", newValue);
   }

   public String toString() {
      return this.value;
   }
}
