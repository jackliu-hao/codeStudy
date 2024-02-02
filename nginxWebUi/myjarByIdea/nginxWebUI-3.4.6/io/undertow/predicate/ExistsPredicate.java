package io.undertow.predicate;

import io.undertow.attribute.ExchangeAttribute;
import io.undertow.server.HttpServerExchange;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ExistsPredicate implements Predicate {
   private final ExchangeAttribute attribute;

   ExistsPredicate(ExchangeAttribute attribute) {
      this.attribute = attribute;
   }

   public boolean resolve(HttpServerExchange value) {
      String att = this.attribute.readAttribute(value);
      if (att == null) {
         return false;
      } else {
         return !att.isEmpty();
      }
   }

   public String toString() {
      return "exists( '" + this.attribute.toString() + "' )";
   }

   public static class Builder implements PredicateBuilder {
      public String name() {
         return "exists";
      }

      public Map<String, Class<?>> parameters() {
         Map<String, Class<?>> params = new HashMap();
         params.put("value", ExchangeAttribute.class);
         return params;
      }

      public Set<String> requiredParameters() {
         return Collections.singleton("value");
      }

      public String defaultParameter() {
         return "value";
      }

      public Predicate build(Map<String, Object> config) {
         ExchangeAttribute value = (ExchangeAttribute)config.get("value");
         return new ExistsPredicate(value);
      }
   }
}
