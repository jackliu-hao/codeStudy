package io.undertow.attribute;

import io.undertow.server.HttpServerExchange;

public class CompositeExchangeAttribute implements ExchangeAttribute {
   private final ExchangeAttribute[] attributes;

   public CompositeExchangeAttribute(ExchangeAttribute[] attributes) {
      ExchangeAttribute[] copy = new ExchangeAttribute[attributes.length];
      System.arraycopy(attributes, 0, copy, 0, attributes.length);
      this.attributes = copy;
   }

   public String readAttribute(HttpServerExchange exchange) {
      StringBuilder sb = new StringBuilder();

      for(int i = 0; i < this.attributes.length; ++i) {
         String val = this.attributes[i].readAttribute(exchange);
         if (val != null) {
            sb.append(val);
         }
      }

      return sb.toString();
   }

   public void writeAttribute(HttpServerExchange exchange, String newValue) throws ReadOnlyAttributeException {
      throw new ReadOnlyAttributeException("combined", newValue);
   }

   public String toString() {
      StringBuilder sb = new StringBuilder();

      for(int i = 0; i < this.attributes.length; ++i) {
         sb.append(this.attributes[i].toString());
      }

      return sb.toString();
   }
}
