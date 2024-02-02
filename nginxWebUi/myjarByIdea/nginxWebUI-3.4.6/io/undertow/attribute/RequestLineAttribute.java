package io.undertow.attribute;

import io.undertow.server.HttpServerExchange;

public class RequestLineAttribute implements ExchangeAttribute {
   public static final String REQUEST_LINE_SHORT = "%r";
   public static final String REQUEST_LINE = "%{REQUEST_LINE}";
   public static final ExchangeAttribute INSTANCE = new RequestLineAttribute();

   private RequestLineAttribute() {
   }

   public String readAttribute(HttpServerExchange exchange) {
      StringBuilder sb = (new StringBuilder()).append(exchange.getRequestMethod().toString()).append(' ').append(exchange.getRequestURI());
      if (!exchange.getQueryString().isEmpty()) {
         sb.append('?');
         sb.append(exchange.getQueryString());
      }

      sb.append(' ').append(exchange.getProtocol().toString()).toString();
      return sb.toString();
   }

   public void writeAttribute(HttpServerExchange exchange, String newValue) throws ReadOnlyAttributeException {
      throw new ReadOnlyAttributeException("Request line", newValue);
   }

   public String toString() {
      return "%{REQUEST_LINE}";
   }

   public static final class Builder implements ExchangeAttributeBuilder {
      public String name() {
         return "Request line";
      }

      public ExchangeAttribute build(String token) {
         return !token.equals("%{REQUEST_LINE}") && !token.equals("%r") ? null : RequestLineAttribute.INSTANCE;
      }

      public int priority() {
         return 0;
      }
   }
}
