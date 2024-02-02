package io.undertow.attribute;

import io.undertow.server.HttpServerExchange;
import io.undertow.util.HeaderValues;
import io.undertow.util.HttpString;

public class ResponseHeaderAttribute implements ExchangeAttribute {
   private final HttpString responseHeader;

   public ResponseHeaderAttribute(HttpString responseHeader) {
      this.responseHeader = responseHeader;
   }

   public String readAttribute(HttpServerExchange exchange) {
      HeaderValues header = exchange.getResponseHeaders().get(this.responseHeader);
      if (header == null) {
         return null;
      } else if (header.size() == 1) {
         return header.getFirst();
      } else {
         StringBuilder sb = new StringBuilder();
         sb.append("[");

         for(int i = 0; i < header.size(); ++i) {
            if (i != 0) {
               sb.append(", ");
            }

            sb.append(header.get(i));
         }

         sb.append("]");
         return sb.toString();
      }
   }

   public void writeAttribute(HttpServerExchange exchange, String newValue) throws ReadOnlyAttributeException {
      exchange.getResponseHeaders().put(this.responseHeader, newValue);
   }

   public String toString() {
      return "%{o," + this.responseHeader + "}";
   }

   public static final class Builder implements ExchangeAttributeBuilder {
      public String name() {
         return "Response header";
      }

      public ExchangeAttribute build(String token) {
         if (token.startsWith("%{o,") && token.endsWith("}")) {
            HttpString headerName = HttpString.tryFromString(token.substring(4, token.length() - 1));
            return new ResponseHeaderAttribute(headerName);
         } else {
            return null;
         }
      }

      public int priority() {
         return 0;
      }
   }
}
