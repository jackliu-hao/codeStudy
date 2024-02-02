package io.undertow.attribute;

import io.undertow.server.HttpServerExchange;

public class QuotingExchangeAttribute implements ExchangeAttribute {
   private final ExchangeAttribute exchangeAttribute;
   public static final ExchangeAttributeWrapper WRAPPER = new Wrapper();

   public QuotingExchangeAttribute(ExchangeAttribute exchangeAttribute) {
      this.exchangeAttribute = exchangeAttribute;
   }

   public String readAttribute(HttpServerExchange exchange) {
      String svalue = this.exchangeAttribute.readAttribute(exchange);
      if (svalue != null && !"-".equals(svalue) && !svalue.isEmpty()) {
         StringBuilder buffer = new StringBuilder(svalue.length() + 2);
         buffer.append('\'');
         int i = 0;

         while(i < svalue.length()) {
            int j = svalue.indexOf(39, i);
            if (j == -1) {
               buffer.append(svalue.substring(i));
               i = svalue.length();
            } else {
               buffer.append(svalue.substring(i, j + 1));
               buffer.append('"');
               i = j + 2;
            }
         }

         buffer.append('\'');
         return buffer.toString();
      } else {
         return "-";
      }
   }

   public void writeAttribute(HttpServerExchange exchange, String newValue) throws ReadOnlyAttributeException {
      throw new ReadOnlyAttributeException();
   }

   public String toString() {
      return "\"" + this.exchangeAttribute.toString() + "\"";
   }

   public static class Wrapper implements ExchangeAttributeWrapper {
      public ExchangeAttribute wrap(ExchangeAttribute attribute) {
         return new QuotingExchangeAttribute(attribute);
      }
   }
}
