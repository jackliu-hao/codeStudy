package io.undertow.attribute;

import io.undertow.server.HttpServerExchange;
import io.undertow.util.HeaderValues;
import io.undertow.util.HttpString;

public class RequestHeaderAttribute implements ExchangeAttribute {
   private final HttpString requestHeader;

   public RequestHeaderAttribute(HttpString requestHeader) {
      this.requestHeader = requestHeader;
   }

   public String readAttribute(HttpServerExchange exchange) {
      HeaderValues header = exchange.getRequestHeaders().get(this.requestHeader);
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
      exchange.getRequestHeaders().put(this.requestHeader, newValue);
   }

   public String toString() {
      return "%{i," + this.requestHeader + "}";
   }

   public static final class Builder implements ExchangeAttributeBuilder {
      public String name() {
         return "Request header";
      }

      public ExchangeAttribute build(String token) {
         if (token.startsWith("%{i,") && token.endsWith("}")) {
            HttpString headerName = HttpString.tryFromString(token.substring(4, token.length() - 1));
            return new RequestHeaderAttribute(headerName);
         } else {
            return null;
         }
      }

      public int priority() {
         return 0;
      }
   }
}
