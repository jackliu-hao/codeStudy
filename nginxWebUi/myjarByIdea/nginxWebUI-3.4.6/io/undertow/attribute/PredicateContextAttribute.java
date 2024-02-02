package io.undertow.attribute;

import io.undertow.predicate.Predicate;
import io.undertow.server.HttpServerExchange;
import java.util.Map;

public class PredicateContextAttribute implements ExchangeAttribute {
   private final String name;

   public PredicateContextAttribute(String name) {
      this.name = name;
   }

   public String readAttribute(HttpServerExchange exchange) {
      Map<String, Object> context = (Map)exchange.getAttachment(Predicate.PREDICATE_CONTEXT);
      if (context != null) {
         Object object = context.get(this.name);
         return object == null ? null : object.toString();
      } else {
         return null;
      }
   }

   public void writeAttribute(HttpServerExchange exchange, String newValue) throws ReadOnlyAttributeException {
      Map<String, Object> context = (Map)exchange.getAttachment(Predicate.PREDICATE_CONTEXT);
      if (context != null) {
         context.put(this.name, newValue);
      }

   }

   public String toString() {
      return "${" + this.name + "}";
   }

   public static final class Builder implements ExchangeAttributeBuilder {
      public String name() {
         return "Predicate context variable";
      }

      public ExchangeAttribute build(String token) {
         if (token.startsWith("${") && token.endsWith("}") && token.length() > 3) {
            return new PredicateContextAttribute(token.substring(2, token.length() - 1));
         } else {
            return token.startsWith("$") ? new PredicateContextAttribute(token.substring(1, token.length())) : null;
         }
      }

      public int priority() {
         return 0;
      }
   }
}
