package io.undertow.attribute;

import io.undertow.server.HttpServerExchange;

public class QueryStringAttribute implements ExchangeAttribute {
   public static final String QUERY_STRING_SHORT = "%q";
   public static final String QUERY_STRING = "%{QUERY_STRING}";
   public static final String BARE_QUERY_STRING = "%{BARE_QUERY_STRING}";
   public static final ExchangeAttribute INSTANCE = new QueryStringAttribute(true);
   public static final ExchangeAttribute BARE_INSTANCE = new QueryStringAttribute(false);
   private final boolean includeQuestionMark;

   private QueryStringAttribute(boolean includeQuestionMark) {
      this.includeQuestionMark = includeQuestionMark;
   }

   public String readAttribute(HttpServerExchange exchange) {
      String qs = exchange.getQueryString();
      return !qs.isEmpty() && this.includeQuestionMark ? '?' + qs : qs;
   }

   public void writeAttribute(HttpServerExchange exchange, String newValue) throws ReadOnlyAttributeException {
      exchange.setQueryString(newValue);
   }

   public String toString() {
      return this.includeQuestionMark ? "%{QUERY_STRING}" : "%{BARE_QUERY_STRING}";
   }

   public static final class Builder implements ExchangeAttributeBuilder {
      public String name() {
         return "Query String";
      }

      public ExchangeAttribute build(String token) {
         if (!token.equals("%{QUERY_STRING}") && !token.equals("%q")) {
            return token.equals("%{BARE_QUERY_STRING}") ? QueryStringAttribute.BARE_INSTANCE : null;
         } else {
            return QueryStringAttribute.INSTANCE;
         }
      }

      public int priority() {
         return 0;
      }
   }
}
